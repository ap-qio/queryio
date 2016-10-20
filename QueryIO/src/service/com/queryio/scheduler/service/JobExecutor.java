/* 
 * @(#) AbstractJob.java Oct 25, 2007 
 * 
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved. 
 * 
 * This software is proprietary information of Exceed Consultancy Services and 
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall 
 * not disclose this information and shall use it only in accordance with the 
 * terms of License. 
 * 
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE 
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT 
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE 
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, 
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. 
 */ 
package com.queryio.scheduler.service;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.reports.ReportManager;


public class JobExecutor implements Job 
{
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
	{
		String status = null;
		Connection con = null;
		PreparedStatement ps = null;
		try{
			con = CoreDBManager.getQueryIODBConnection();
			ps = con.prepareStatement(QueryConstants.INSERT_SCHEDULE_JOB_STATE);
			ps.setString(1, jobExecutionContext.getJobDetail().getKey().getName());
			ps.setString(2, jobExecutionContext.getJobDetail().getKey().getGroup());
			ps.setTimestamp(3, new Timestamp(jobExecutionContext.getFireTime().getTime()));
			Boolean flag = true;
			try{
				executeJob(jobExecutionContext);
			}
			catch(Exception e){
				flag = false;
				status = e.getMessage();
				AppLogger.getLogger().fatal(e.getMessage(),e);
			}
			ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			if(flag){
				ps.setString(5, SchedulerConstants.SUCCESS);
			}
			else{
				ps.setString(5, SchedulerConstants.FAILURE);
			}
			ps.setString(6,status);
			ps.executeUpdate();
		}
		catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(),e);
		}
		finally{
			try{
				if(ps!=null){
					DatabaseFunctions.closePreparedStatement(ps);
				}
				if(con!=null){
					DatabaseManager.closeDbConnection(con);
				}
			}
			catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(),e);
			}
		}
	}
	
	
	public void executeJob(JobExecutionContext jobExecutionContext)  throws Exception
	{
		
		ArrayList reportTypeList = null;
		ArrayList exportFormatList = null;
		ArrayList usersIdList = null;
		DWRResponse dwrResponse = null;
		reportTypeList = new ArrayList();	
		exportFormatList = new ArrayList();
		
		JobDataMap jDataMap = jobExecutionContext.getMergedJobDataMap(); 
		String notificationEnable = null;
		String reportIds = null;
		String notificationType = null;
		String endDate = null;
		String startDate = null;
		try{
			reportIds=(String)jDataMap.get(SchedulerConstants.REPORT_ID);
			reportIds = reportIds.substring(1,reportIds.length()-1);
			String reportType[]=reportIds.split(",");
			
			for(String reportId:reportType){
				reportTypeList.add(reportId.trim());
			}
	
			String formatIds=(String)jDataMap.get(SchedulerConstants.FORMAT_TYPE); 
			String formatList[]=formatIds.substring(1,formatIds.length()-1).split(",");
			for(String formatType:formatList)
			{
				exportFormatList.add(formatType.trim());
			}
			notificationEnable = (String)jDataMap.get(SchedulerConstants.NOTIFICATION_ENABLE);
			notificationType = null;
			if(Boolean.parseBoolean(notificationEnable))
			{
				notificationType =  (String)jDataMap.get(SchedulerConstants.NOTIFICATION_TYPE);
				if(notificationType.equals(SchedulerConstants.NOTIFICATION_EMAIL))
				{
					usersIdList = new ArrayList();
					String userIds = (String)jDataMap.get(SchedulerConstants.EMAIL_ADDRESS);
					String userList[] = userIds.substring(1,userIds.length()-1).split(",");
					for(String userID:userList){
						usersIdList.add(userID.trim());	
					}
				}
			}
			
			Date end = new Date();
			endDate = String.valueOf(end.getMonth() + 1)+"/"+String.valueOf(end.getDate())+"/"+String.valueOf(end.getYear() + 1900)+" "+String.valueOf(end.getHours())+":"+String.valueOf(end.getMinutes())+":"+String.valueOf(end.getSeconds());
			int frequency = Integer.parseInt((String)jDataMap.get(SchedulerConstants.FREQUENCY));
			long diff = 0;
			switch(frequency){
				case SchedulerConstants.SCH_FREQUENCY_ONCE:
				case SchedulerConstants.SCH_FREQUENCY_DAILY:
					diff = 86400000;
					break;
				case SchedulerConstants.SCH_FREQUENCY_TWELVE_HOURS:
					diff = 43200000;
					break;
				case SchedulerConstants.SCH_FREQUENCY_WEEKLY:
					diff = (long) (86400000*7);
					break;
			}
			Date start = new Date(end.getTime()-diff);
			startDate = String.valueOf(start.getMonth() + 1)+"/"+String.valueOf(start.getDate())+"/"+String.valueOf(start.getYear() + 1900)+" "+String.valueOf(start.getHours())+":"+String.valueOf(start.getMinutes())+":"+String.valueOf(start.getSeconds());
		}
		catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(),e);
			throw new Exception("Scheduler Execution Exception");
		}
		if(Boolean.parseBoolean(notificationEnable))
		{
			if (reportTypeList.size() > 0)
			{
				if (reportTypeList.contains(Integer.toString(QueryIOConstants.NN_DETAIL)) || reportTypeList.contains(Integer.toString(QueryIOConstants.DN_DETAIL)) || reportTypeList.contains(Integer.toString(QueryIOConstants.REPORT_RM_DETAIL)) || reportTypeList.contains(Integer.toString(QueryIOConstants.REPORT_NM_DETAIL)))
				{
					if(notificationType.equals(SchedulerConstants.NOTIFICATION_EMAIL))
					{
						dwrResponse = ReportManager.mailNodeReport((String)jDataMap.get(SchedulerConstants.NODE_ID), exportFormatList, usersIdList,jobExecutionContext.getJobDetail().getKey().getName(), startDate, endDate);
					}
					else{
						dwrResponse  = ReportManager.logNodeReport((String)jDataMap.get(SchedulerConstants.NODE_ID), exportFormatList, jobExecutionContext.getJobDetail().getKey().getName(), startDate, endDate);
					}
				}
				else
				{
					if(notificationType.equals(SchedulerConstants.NOTIFICATION_EMAIL))
					{
						dwrResponse  = ReportManager.mailGeneralReport(reportTypeList,exportFormatList, usersIdList, jobExecutionContext.getJobDetail().getKey().getName(), startDate, endDate);
					}
					else{
						dwrResponse  = ReportManager.logGeneralReport(reportTypeList,exportFormatList, jobExecutionContext.getJobDetail().getKey().getName(), startDate, endDate);
					}
				}
			}
		}
		else{
			if (reportTypeList.size() > 0)
			{
				if (reportTypeList.contains(Integer.toString(QueryIOConstants.NN_DETAIL)) || reportTypeList.contains(Integer.toString(QueryIOConstants.DN_DETAIL)) 
						|| reportTypeList.contains(Integer.toString(QueryIOConstants.REPORT_RM_DETAIL)) || reportTypeList.contains(Integer.toString(QueryIOConstants.REPORT_NM_DETAIL)))
				{
					dwrResponse  = ReportManager.mailNodeReport((String)jDataMap.get(SchedulerConstants.NODE_ID), exportFormatList, null,jobExecutionContext.getJobDetail().getKey().getName(), startDate, endDate);
				}
				else{
					dwrResponse  = ReportManager.mailGeneralReport(reportTypeList,exportFormatList, null, jobExecutionContext.getJobDetail().getKey().getName(), startDate, endDate);
				}
			}
		}
		if(dwrResponse != null && !dwrResponse.isTaskSuccess())
		{
			throw new Exception(dwrResponse.getResponseMessage());
		}
	}
}
