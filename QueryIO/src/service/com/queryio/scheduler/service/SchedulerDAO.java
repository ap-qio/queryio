/*
 * @(#) JobScheduler.java    1.0     16/12/2002 (DD/MM/YYYY)
 *
 * Copyright (C) 2002 - 2004 Exceed Consultancy Services. All rights reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.scheduler.service;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.quartz.DateBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.jdbcjobstore.StdJDBCConstants;
import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;
import org.quartz.impl.triggers.CalendarIntervalTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.applications.MapRedJobExecutor;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.namespace.DiagnosisProcessExecutor;
import com.queryio.core.namespace.NamespaceBackupJobExecutor;
import com.queryio.core.reports.ReportManager;


public class SchedulerDAO 
{
	private static Scheduler scheduler;
	private static String schedQueryioTriggerName = "QueryIO_Trigger"; //$NON-NLS-1$
    private static final String SCHED_SYS_REPORT_JOB_NAME = "SYS_REPORT"; //$NON-NLS-1$
    private static final String SCHED_BIG_QUERY_JOB_NAME = "BIG_QUERY"; //$NON-NLS-1$
    private static final String SCHED_MAPRED_GROUP = "MAPRED";
    private static final String SCHED_DIAGNOSIS_GROUP = "DIAGNOSIS";
    private static final String SCHED_NAMESPACE_GROUP = "NAMESPACE";
    private static int lastJobId = 0;
    private static boolean initialized = false;
    
	private static String countJob = "SELECT count(*) FROM " + SchedulerConstants.TABLE_PREFIX 
			+ StdJDBCConstants.TABLE_JOB_DETAILS + " WHERE " + StdJDBCConstants.COL_SCHEDULER_NAME + " = '" 
			+ SchedulerConstants.SCHEDULER_NAME + "' AND " + StdJDBCConstants.COL_JOB_GROUP + " LIKE ?";

	private static String getAllJobDetailsSysReports = "SELECT * FROM " + SchedulerConstants.TABLE_PREFIX 
			+ StdJDBCConstants.TABLE_JOB_DETAILS + " WHERE " + StdJDBCConstants.COL_SCHEDULER_NAME + " = '" 
			+ SchedulerConstants.SCHEDULER_NAME + "' AND " + StdJDBCConstants.COL_JOB_GROUP + " LIKE '" 
					+ SCHED_SYS_REPORT_JOB_NAME + "%'";
	
	
	
	private static String getAllJobDetailsBigQuery = "SELECT * FROM " + SchedulerConstants.TABLE_PREFIX 
			+ StdJDBCConstants.TABLE_JOB_DETAILS + " WHERE " + StdJDBCConstants.COL_SCHEDULER_NAME + " = '" 
			+ SchedulerConstants.SCHEDULER_NAME + "' AND " + StdJDBCConstants.COL_JOB_GROUP + " LIKE '" 
					+ SCHED_BIG_QUERY_JOB_NAME + "%'";
	
	
	private static String getAllJobDetailsMapred = "SELECT * FROM " + SchedulerConstants.TABLE_PREFIX 
			+ StdJDBCConstants.TABLE_JOB_DETAILS + " WHERE " + StdJDBCConstants.COL_SCHEDULER_NAME + " = '" 
			+ SchedulerConstants.SCHEDULER_NAME + "' AND " + StdJDBCConstants.COL_JOB_GROUP + " = '" 
					+ SCHED_MAPRED_GROUP + "'";
	
	
	private static String getAllJobDetailsByJobNameJobGroup = "SELECT * FROM " + SchedulerConstants.TABLE_PREFIX 
			+ StdJDBCConstants.TABLE_JOB_DETAILS + " WHERE " + StdJDBCConstants.COL_SCHEDULER_NAME + " = '" 
			+ SchedulerConstants.SCHEDULER_NAME + "' AND " + StdJDBCConstants.COL_JOB_NAME + " =  ? " +
			"AND " + StdJDBCConstants.COL_JOB_GROUP + " = ?";
	
	private static String getTriggerDetails = "SELECT "+  SchedulerConstants.ID  +", " + SchedulerConstants.STARTTIME +", " 
			+ SchedulerConstants.ENDTIME + ", " + SchedulerConstants.STATUS + ", " + SchedulerConstants.REASON_FOR_FAILURE + " FROM " 
			+ TableConstants.TABLE_TRIGGERED_SCHEDULEJOB_STATE + " WHERE " + StdJDBCConstants.COL_JOB_GROUP 
			+ " = ? AND " + StdJDBCConstants.COL_JOB_NAME + " = ? ";
	
	private static String getAllTriggerDetails = "SELECT "+  SchedulerConstants.ID +", " + SchedulerConstants.JOB_NAME +", "
			+  SchedulerConstants.JOB_GROUP +", " + SchedulerConstants.STARTTIME +", "  
			+ SchedulerConstants.ENDTIME + ", " + SchedulerConstants.STATUS + ", " + SchedulerConstants.REASON_FOR_FAILURE + " FROM " 
			+ TableConstants.TABLE_TRIGGERED_SCHEDULEJOB_STATE;
	
	
	private static String deleteSelectedTriggers = "DELETE FROM "+TableConstants.TABLE_TRIGGERED_SCHEDULEJOB_STATE + " WHERE "+ SchedulerConstants.ID + " = ?";
	
	
	public static void initializeScheduler(){
    	if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("initializing scheduler: ");
	    try{	
	    	scheduler = new StdSchedulerFactory(EnvironmentalConstants.getAppHome()+"quartz.properties").getScheduler();//.getDefaultScheduler();
	    	initialized=true;
	    }
		catch(Exception e){
			AppLogger.getLogger().fatal("Error Occured while initializing scheduler: "+ e.getMessage(), e);
		}
    }
    
    
    public static boolean isSchedulerInitialized(){
    	return initialized;
    }
    
    public static void startScheduler(){
	    try{
	    	scheduler.start();
	    }
		catch(Exception e){
			AppLogger.getLogger().fatal("Error Occured while initializing scheduler: "+e.getMessage());
		}
    }
	
    public static void shutdownScheduler(){
    	try{
    	   scheduler.shutdown();
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while shutting down Scheduler: "+e.getMessage());
    	}
    }
    
    public static boolean isSchedulerStarted(){
    	try{
    		return scheduler.isStarted();
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while checking scheduler is started: "+e.getMessage());
    	}
    	return false;
    }
	
	private static void setMapRedCommonProperties(SchedulerBean schedule, JobDataMap dataMap)
    {
		dataMap.put(SchedulerConstants.MAPRED_JOB_NAME, schedule.getJobName());
		dataMap.put(SchedulerConstants.FREQUENCY, String.valueOf(schedule.getInterval()));
		dataMap.put(SchedulerConstants.REPORT_TIME,String.valueOf(schedule.getTime()));
		dataMap.put(SchedulerConstants.NOTIFICATION_ENABLE, String.valueOf(schedule.isNotificationEnable()));
		if(schedule.isNotificationEnable()){
			dataMap.put(SchedulerConstants.NOTIFICATION_MESSAGE, schedule.getNotificationMessage());
			dataMap.put(SchedulerConstants.NOTIFICATION_TYPE,schedule.getNotificationType());
			dataMap.put(SchedulerConstants.EMAIL_ADDRESS, schedule.getEmailUserIds());
		}
    }
	
	private static void setBackupCommonProperties(SchedulerBean schedule, JobDataMap dataMap)
    {
		dataMap.put(SchedulerConstants.BACKUP_JOB_NAME, schedule.getJobName());
		dataMap.put(SchedulerConstants.FREQUENCY, String.valueOf(schedule.getInterval()));
		dataMap.put(SchedulerConstants.REPORT_TIME,String.valueOf(schedule.getTime()));
		dataMap.put(SchedulerConstants.NOTIFICATION_ENABLE, String.valueOf(schedule.isNotificationEnable()));
		if(schedule.isNotificationEnable()){
			dataMap.put(SchedulerConstants.NOTIFICATION_MESSAGE, schedule.getNotificationMessage());
			dataMap.put(SchedulerConstants.NOTIFICATION_TYPE,schedule.getNotificationType());
			dataMap.put(SchedulerConstants.EMAIL_ADDRESS, schedule.getEmailUserIds());
		}
    }
		
	private static void setCommonProperties(SchedulerBean schedule, JobDataMap dataMap)
    {
		dataMap.put(SchedulerConstants.REPORT_ID, schedule.getReportId());
        dataMap.put(SchedulerConstants.FORMAT_TYPE, schedule.getSelectedFormat());
        dataMap.put(SchedulerConstants.FREQUENCY, String.valueOf(schedule.getInterval()));
        dataMap.put(SchedulerConstants.REPORT_TIME,String.valueOf(schedule.getTime()));
        dataMap.put(SchedulerConstants.NODE_ID, String.valueOf(schedule.getNodeId()));
        dataMap.put(SchedulerConstants.NOTIFICATION_ENABLE, String.valueOf(schedule.isNotificationEnable()));
        if(schedule.isNotificationEnable()){
	        dataMap.put(SchedulerConstants.NOTIFICATION_TYPE,schedule.getNotificationType());
	        dataMap.put(SchedulerConstants.NOTIFICATION_MESSAGE, schedule.getNotificationMessage());
	        dataMap.put(SchedulerConstants.EMAIL_ADDRESS, schedule.getEmailUserIds());
        }
    }
    private static void setCommonProperties(QuerySchedulerBean querySchedule, JobDataMap dataMap)
    {
    	dataMap.put(SchedulerConstants.NAMENODE,querySchedule.getNameNode());
    	dataMap.put(SchedulerConstants.USERNAME,querySchedule.getUsername());
        dataMap.put(SchedulerConstants.FORMAT_TYPE, querySchedule.getSelectedFormat());
        dataMap.put(SchedulerConstants.FREQUENCY, String.valueOf(querySchedule.getInterval()));
        dataMap.put(SchedulerConstants.REPORT_TIME,String.valueOf(querySchedule.getTime()));
        dataMap.put(SchedulerConstants.QUERY, String.valueOf(querySchedule.getQuery()));
        dataMap.put(SchedulerConstants.NOTIFICATION_ENABLE, String.valueOf(querySchedule.isNotificationEnable()));
        if(querySchedule.isNotificationEnable()){
	        dataMap.put(SchedulerConstants.NOTIFICATION_TYPE,querySchedule.getNotificationType());
	        dataMap.put(SchedulerConstants.NOTIFICATION_MESSAGE, querySchedule.getNotificationMessage());
	        dataMap.put(SchedulerConstants.EMAIL_ADDRESS, querySchedule.getEmailUserIds());
        }
    }
    
    
    
    public static void scheduleMapRedJob(JobDetailImpl jobDetailImpl,SchedulerBean schedule){
    	try{
    	Trigger trigger = null;
    	String grpName = schedule.getGroup();
    	String triggerName = schedQueryioTriggerName+SchedulerConstants.HIPHEN+String.valueOf(getLastJobId());
    	
    	if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Scheduling mapred job, group: " + grpName + ", trigger: " + triggerName);
    	
    	switch (schedule.getInterval())
         {
	         case SchedulerConstants.SCH_FREQUENCY_ONCE:		//Once
	         {
	        	 trigger= new SimpleTriggerImpl(triggerName,grpName, new Date(schedule.getTime()), new Date(schedule.getTime()+60000), 0, 0);
	        	 break;
	         }
             case SchedulerConstants.SCH_FREQUENCY_DAILY:		//Daily
             {
             	trigger= (new CalendarIntervalTriggerImpl(triggerName,grpName,DateBuilder.IntervalUnit.DAY,1));
             	((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
                break;
             }
             case SchedulerConstants.SCH_FREQUENCY_WEEKLY:	//Weekly
             {
            	 trigger= new CalendarIntervalTriggerImpl(triggerName,grpName,DateBuilder.IntervalUnit.DAY,7);
            	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
            	 break;
             }
             case SchedulerConstants.SCH_FREQUENCY_TWELVE_HOURS:
             {
            	 trigger= new CalendarIntervalTriggerImpl(triggerName,grpName
            			 ,DateBuilder.IntervalUnit.HOUR,12);
            	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
                 break; 
             }
             default:	// from interval
             {
            	 trigger= new CalendarIntervalTriggerImpl(triggerName,grpName
            			 ,DateBuilder.IntervalUnit.MINUTE,schedule.getInterval());
            	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
            	 break;
             }	                
         }
         scheduler.scheduleJob(jobDetailImpl, trigger);
         if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("A job " + jobDetailImpl.getName() + " is scheduled successfully and will be triggerd at " + trigger.getStartTime());
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while scheduling job: "+e.getMessage(), e);
    	}
   }
    
    public static void scheduleJob(JobDetailImpl jobDetailImpl,SchedulerBean schedule, String grpName) throws Exception{
    	try{
    	Trigger trigger = null;
    	String triggerName = schedQueryioTriggerName+SchedulerConstants.HIPHEN+String.valueOf(getLastJobId());
    	switch (schedule.getInterval())
         {
	         case SchedulerConstants.SCH_FREQUENCY_TWELVE_HOURS:
	         {
	        	 trigger= new CalendarIntervalTriggerImpl(triggerName,grpName
	        			 ,DateBuilder.IntervalUnit.HOUR,12);
	        	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
	             break; 
	         }
             case SchedulerConstants.SCH_FREQUENCY_DAILY:		//Daily
             {
             	trigger= (new CalendarIntervalTriggerImpl(triggerName,grpName,DateBuilder.IntervalUnit.DAY,1));
             	((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
             	//trigger.setName();
                break;
             }
             case SchedulerConstants.SCH_FREQUENCY_WEEKLY:	//Weekly
             {
            	 trigger= new CalendarIntervalTriggerImpl(triggerName,grpName,DateBuilder.IntervalUnit.DAY,7);
            	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
            	 break;
             }
             default:	//Once
             {
            	trigger= new SimpleTriggerImpl(triggerName,grpName, new Date(schedule.getTime()), new Date(schedule.getTime()+60000), 0, 0);
                 break;
             }	                
         }
         scheduler.scheduleJob(jobDetailImpl, trigger);
         if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("A job " + jobDetailImpl.getName() + " is scheduled successfully and will be triggerd at " + trigger.getStartTime());
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while scheduling job: "+e.getMessage(), e);
    		throw e;
    	}
   }
    
    
    public static void scheduleJob(JobDetailImpl jobDetailImpl,QuerySchedulerBean querySchedule){
    	try{
    	Trigger trigger = null;
    	String grpName=SCHED_BIG_QUERY_JOB_NAME+SchedulerConstants.HIPHEN+String.valueOf(querySchedule.getQuery());
    	String triggerName = schedQueryioTriggerName+SchedulerConstants.HIPHEN+String.valueOf(getLastJobId());
    	switch (querySchedule.getInterval())
         {
             case SchedulerConstants.SCH_FREQUENCY_DAILY:		//Daily
             {
             	trigger= (new CalendarIntervalTriggerImpl(triggerName,grpName,DateBuilder.IntervalUnit.DAY,1));
             	((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(querySchedule.getTime()));
             	//trigger.setName();
                break;
             }
             case SchedulerConstants.SCH_FREQUENCY_WEEKLY:	//Weekly
             {
            	 trigger= new CalendarIntervalTriggerImpl(triggerName,grpName,DateBuilder.IntervalUnit.DAY,7);
            	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(querySchedule.getTime()));
            	 break;
             }
             case SchedulerConstants.SCH_FREQUENCY_TWELVE_HOURS:
             {
            	 trigger= new CalendarIntervalTriggerImpl(triggerName,grpName
            			 ,DateBuilder.IntervalUnit.HOUR,12);
            	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(querySchedule.getTime()));
                 break; 
             }
             default:	//Once
             {
            	 if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Trriger set to once Time :"+querySchedule.getTime());
            	  
            	trigger= new SimpleTriggerImpl(triggerName,grpName, new Date(querySchedule.getTime()), new Date(querySchedule.getTime()+60000), 0, 0);
                 break;
             }	                
         }
    	scheduler.scheduleJob(jobDetailImpl, trigger);
         if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("A job " + jobDetailImpl.getName() + " is scheduled successfully and will be triggerd at " + trigger.getStartTime());
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while scheduling job: "+e.getMessage(), e);
    	}
   }
    
       
    private static void setLastJobId(String grpName){
    	Connection conn=null;
    	int count = -1;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
	        try {
	        	conn = CoreDBManager.getQueryIODBConnection();
	        	ps = conn.prepareStatement(countJob);
	    		ps.setString(1,grpName);
    			rs=ps.executeQuery();
	            while(rs.next()){
	            	count = rs.getInt(1);
	            }
	        } 
	        catch(Exception e){
	        	AppLogger.getLogger().fatal("Error Occurred:"+e.getMessage(),e);
	        }
	        finally
	    	{
	    		try
	    		{
	    			DatabaseFunctions.closeSQLObjects(ps, rs);
	    		}
	    		catch(Exception e)
	    		{
	    			AppLogger.getLogger().fatal(e.getMessage(), e);
	    		}
	    	}
	        if(count==-1)
	        	lastJobId=0;
	        else{
	        	lastJobId = count;
	        }
    }
    
    private static int getLastJobId(){
    	return lastJobId;
    }
    
    public static void addJob(SchedulerBean schedule){
    	try{
	    	if(isSchedulerInitialized()&&!scheduler.isStarted()){
	    		startScheduler();
	    	}
	    	String grpName=SCHED_SYS_REPORT_JOB_NAME+SchedulerConstants.HIPHEN+String.valueOf(schedule.getReportId());
	    	setLastJobId(grpName);
	    	JobDetailImpl jobDetailImpl = new JobDetailImpl();
	    	jobDetailImpl.setJobClass(JobExecutor.class);
	    	jobDetailImpl.setDescription(null);
	    	jobDetailImpl.setDurability(true);
	    	jobDetailImpl.setRequestsRecovery(true);
	    	jobDetailImpl.setKey(new JobKey(schedule.getName(),grpName));
	    	JobDataMap jobDataMap = new JobDataMap();
	    	setCommonProperties(schedule,jobDataMap);
	    	jobDetailImpl.setJobDataMap(jobDataMap);
	    	scheduleJob(jobDetailImpl,schedule, grpName);	    	
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while adding job: "+e.getMessage(),e);
    	}
    }
    
    public static void addNamespaceBackupSchedule(SchedulerBean schedule){
    	try{
	    	if(isSchedulerInitialized()&&!scheduler.isStarted()){
	    		startScheduler();
	    	}
	    	setLastJobId(SCHED_NAMESPACE_GROUP);
	    	JobDetailImpl jobDetailImpl = new JobDetailImpl();
	    	jobDetailImpl.setJobClass(NamespaceBackupJobExecutor.class);
	    	jobDetailImpl.setDurability(true);
	    	jobDetailImpl.setRequestsRecovery(true);
	    	jobDetailImpl.setKey(new JobKey(schedule.getName(),SCHED_NAMESPACE_GROUP));
	    	JobDataMap jobDataMap = new JobDataMap();
	    	setBackupCommonProperties(schedule,jobDataMap);
	    	jobDetailImpl.setJobDataMap(jobDataMap);
	    	scheduleJob(jobDetailImpl,schedule, SCHED_NAMESPACE_GROUP);	    	
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while adding job: "+e.getMessage(),e);
    	}
    }
    
    public static void addMapRedJobSchedule(SchedulerBean schedule){
    	try{
	    	if(isSchedulerInitialized()&&!scheduler.isStarted()){
	    		startScheduler();
	    	}
	    	setLastJobId(SCHED_MAPRED_GROUP);
	    	JobDetailImpl jobDetailImpl = new JobDetailImpl();
	    	jobDetailImpl.setJobClass(MapRedJobExecutor.class);
	    	jobDetailImpl.setDurability(true);
	    	jobDetailImpl.setRequestsRecovery(true);
	    	jobDetailImpl.setKey(new JobKey(schedule.getName(),SCHED_MAPRED_GROUP));
	    	JobDataMap jobDataMap = new JobDataMap();
	    	setMapRedCommonProperties(schedule,jobDataMap);
	    	jobDetailImpl.setJobDataMap(jobDataMap);
	    	scheduleJob(jobDetailImpl,schedule, SCHED_MAPRED_GROUP);	    	
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while adding job: "+e.getMessage(),e);
    	}
    }
    
    public static void addNamespaceDiagnosisSchedule(SchedulerBean schedule, String namenodeId, long startIndex, long endIndex) throws Exception{
	    	if(isSchedulerInitialized()&&!scheduler.isStarted()){
	    		startScheduler();
	    	}
	    	setLastJobId(SCHED_DIAGNOSIS_GROUP);
	    	JobDetailImpl jobDetailImpl = new JobDetailImpl();
	    	jobDetailImpl.setJobClass(DiagnosisProcessExecutor.class);
	    	jobDetailImpl.setDurability(true);
	    	jobDetailImpl.setRequestsRecovery(true);
	    	jobDetailImpl.setKey(new JobKey(schedule.getName(),SCHED_DIAGNOSIS_GROUP));
	    	JobDataMap jobDataMap = new JobDataMap();
	    	
	    	jobDataMap.put("NAMENODEID", namenodeId);
	    	jobDataMap.put("LOGGEDINUSER", RemoteManager.getLoggedInUser());
	    	jobDataMap.put("STARTINDEX", String.valueOf(startIndex));
	    	jobDataMap.put("ENDINDEX", String.valueOf(endIndex));
	    	
	    	setMapRedCommonProperties(schedule,jobDataMap);
	    	jobDetailImpl.setJobDataMap(jobDataMap);
	    	scheduleJob(jobDetailImpl,schedule, SCHED_DIAGNOSIS_GROUP);	    	
    	
    }
    
    public static void addQueryJob(QuerySchedulerBean querySchedule){
    	try{
    	if(scheduler != null){
    		if(isSchedulerInitialized()&&!scheduler.isStarted()){
	    		startScheduler();
	    	}
	    	String grpName=SCHED_BIG_QUERY_JOB_NAME+SchedulerConstants.HIPHEN+String.valueOf(querySchedule.getQuery());
	    	setLastJobId(grpName);
	    	JobDetailImpl jobDetailImpl = new JobDetailImpl();
	    	jobDetailImpl.setJobClass(QueryJobExecutor.class);
	    	jobDetailImpl.setDescription(null);
	    	jobDetailImpl.setDurability(true);
	    	jobDetailImpl.setRequestsRecovery(true);
	    	jobDetailImpl.setKey(new JobKey(querySchedule.getName(),grpName));
	    	JobDataMap jobDataMap = new JobDataMap();
	    	setCommonProperties(querySchedule,jobDataMap);
	    	jobDetailImpl.setJobDataMap(jobDataMap);
	    	scheduleJob(jobDetailImpl,querySchedule);
    	}
    	else
    	{
    		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Scheduler is off.");
    	}
    		
    	}catch (Exception e) {
    		AppLogger.getLogger().fatal("Error Occured while adding job: "+e.getMessage(),e);
		}
    }
    
    public static SchedulerBean getMapredScheduleBean(Connection connection, String jobName){
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	try{
    		st=connection.prepareStatement(getAllJobDetailsByJobNameJobGroup);
    		st.setString(1, jobName);
    		st.setString(2, SCHED_MAPRED_GROUP);
    		rs=st.executeQuery();
    		if(rs.next()){
     			SchedulerBean sBean = new SchedulerBean();
    			JobDetail jdetail = scheduler.getJobDetail(new JobKey(rs.getString(StdJDBCDelegate.COL_JOB_NAME),rs.getString(StdJDBCDelegate.COL_JOB_GROUP)));
    			JobDataMap jMap = jdetail.getJobDataMap();    			
    			sBean.setInterval(Integer.parseInt((String)jMap.get(SchedulerConstants.FREQUENCY)));
    			sBean.setTime(Long.parseLong((String)jMap.get(SchedulerConstants.REPORT_TIME)));
    			return sBean;
    		}	
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error while getting scheduler info from DB "+e.getMessage(),e);
    	}
    	finally{
    		try{
    			DatabaseFunctions.closeSQLObjects(st, rs);
    		}
    		catch(Exception e){
    			AppLogger.getLogger().fatal("Exception Caught while closing sql objects "+e.getMessage(),e);
    		}
    	}
    	return null;
    }
    
    
    
   
    
    public static void getAllSysReportsSchedules(ArrayList scheduleList){
    	Connection conn = null;
    	Statement st = null;
    	ResultSet rs = null;
    	try{
    		conn = CoreDBManager.getQueryIODBConnection();
    		st=conn.createStatement();
    		rs=st.executeQuery(getAllJobDetailsSysReports);
    		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("GET_ALL_JOB_DETAILS_SYS_REPORTS"+getAllJobDetailsSysReports);
    		while(rs.next()){
     			SchedulerBean sBean = new SchedulerBean();
    			JobDetail jdetail = scheduler.getJobDetail(new JobKey(rs.getString(StdJDBCDelegate.COL_JOB_NAME),rs.getString(StdJDBCDelegate.COL_JOB_GROUP)));
    			JobDataMap jMap = jdetail.getJobDataMap();
    			if((String)jMap.get(SchedulerConstants.REPORT_ID)!=null && !((String)jMap.get(SchedulerConstants.REPORT_ID)).trim().equals(""))
    			{
    				sBean.setReportId(ReportManager.getReportName((String)jMap.get(SchedulerConstants.REPORT_ID)).toString());
    			}
    			if((String)jMap.get(SchedulerConstants.FORMAT_TYPE)!=null && !((String)jMap.get(SchedulerConstants.FORMAT_TYPE)).trim().equals("")){
    				sBean.setSelectedFormat(ReportManager.getExportFormat((String)jMap.get(SchedulerConstants.FORMAT_TYPE)).toString());
    			}
    			
    			sBean.setInterval(Integer.parseInt((String)jMap.get(SchedulerConstants.FREQUENCY)));
    			sBean.setNotificationEnable(Boolean.parseBoolean(jMap.getString(SchedulerConstants.NOTIFICATION_ENABLE)));
    			if(sBean.isNotificationEnable()){
    				if((String)jMap.get(SchedulerConstants.EMAIL_ADDRESS)!=null && !((String)jMap.get(SchedulerConstants.EMAIL_ADDRESS)).trim().equals("")){
        				sBean.setEmailUserIds(RemoteManager.getUserDetailInfo((String)jMap.get(SchedulerConstants.EMAIL_ADDRESS)).toString());
        			}
    				else{
    					sBean.setEmailUserIds(null);
    				}
	    			sBean.setNotificationMessage((String)jMap.get(SchedulerConstants.NOTIFICATION_MESSAGE));
	    			sBean.setNotificationType((String)jMap.get(SchedulerConstants.NOTIFICATION_TYPE));
    			}
    			else{
    				sBean.setNotificationMessage(null);
	    			sBean.setNotificationType(null);
	    			sBean.setEmailUserIds(null);
    			}
    			
    			sBean.setName(jdetail.getKey().getName());
    			sBean.setGroup(jdetail.getKey().getGroup());
    			sBean.setNodeId((String)jMap.get(SchedulerConstants.NODE_ID));
    			sBean.setTime(Long.parseLong((String)jMap.get(SchedulerConstants.REPORT_TIME)));
    			scheduleList.add(sBean);
    		}	
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error while getting scheduler info from DB "+e.getMessage(),e);
    	}
    	finally{
    		try{
    			CoreDBManager.closeConnection(conn);
    		}catch(Exception e){
    			AppLogger.getLogger().fatal("Exception Caught while closing connection. "+e.getMessage(),e);
    		}
    		try{
    			DatabaseFunctions.closeSQLObjects(st, rs);
    		}
    		catch(Exception e){
    			AppLogger.getLogger().fatal("Exception Caught while closing sql objects "+e.getMessage(),e);
    		}
    	}
    }
    
    
    public static void getAllBigQuerySchedules(ArrayList scheduleList){
    	Connection conn = null;
    	Statement st = null;
    	ResultSet rs = null;
    	try{
    		conn = CoreDBManager.getQueryIODBConnection();
    		st=conn.createStatement();
    		rs=st.executeQuery(getAllJobDetailsBigQuery);
    		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("GET_ALL_JOB_DETAILS_BIG_QUERY: "+getAllJobDetailsBigQuery);
    		while(rs.next()){
    			QuerySchedulerBean qBean = new QuerySchedulerBean();
    			JobDetail jdetail = scheduler.getJobDetail(new JobKey(rs.getString(StdJDBCDelegate.COL_JOB_NAME),rs.getString(StdJDBCDelegate.COL_JOB_GROUP)));
    			JobDataMap jMap = jdetail.getJobDataMap();
    			qBean.setName(jdetail.getKey().getName());
    			qBean.setGroup(jdetail.getKey().getGroup());
    			qBean.setSelectedFormat(ReportManager.getExportFormat((String)jMap.get(SchedulerConstants.FORMAT_TYPE)).toString());
    			qBean.setInterval((Integer)jMap.getInt(SchedulerConstants.FREQUENCY));
    			qBean.setNotificationEnable(Boolean.parseBoolean((String)jMap.get(SchedulerConstants.NOTIFICATION_ENABLE)));
    			if(qBean.isNotificationEnable())
    			{
	    			qBean.setNotificationMessage((String)jMap.get(SchedulerConstants.NOTIFICATION_MESSAGE));
	    			qBean.setNotificationType((String)jMap.get(SchedulerConstants.NOTIFICATION_TYPE));
	    			if(qBean.getNotificationType().equals(SchedulerConstants.NOTIFICATION_EMAIL)){
	    				qBean.setEmailUserIds(RemoteManager.getUserDetailInfo((String)jMap.get(SchedulerConstants.EMAIL_ADDRESS)).toString());
	    			}
	    		}
				else{
					qBean.setNotificationMessage(null);
	    			qBean.setNotificationType(null);
	    			qBean.setEmailUserIds(null);
				}
    			qBean.setTime(Long.parseLong((String)jMap.get(SchedulerConstants.REPORT_TIME)));
    			qBean.setQuery((String)jMap.get(SchedulerConstants.QUERY));
    			qBean.setNameNode((String)jMap.get(SchedulerConstants.NAMENODE));
    			scheduleList.add(qBean);
    		}	
    		
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error while getting scheduler info from DB "+e.getMessage(),e);
    	}
    	finally{
    		try{
    			CoreDBManager.closeConnection(conn);
    		}catch(Exception e){
    			AppLogger.getLogger().fatal("Exception Caught while closing connection. "+e.getMessage(),e);
    		}
    		try{
    			DatabaseFunctions.closeSQLObjects(st, rs);
    		}
    		catch(Exception e){
    			AppLogger.getLogger().fatal("Exception Caught while closing sql objects "+e.getMessage(),e);
    		}
    	}
    }
    
    
    public static void getAllMapRedSchedules(ArrayList scheduleList){
    	Connection conn = null;
    	Statement st = null;
    	ResultSet rs = null;
    	try{
    		conn = CoreDBManager.getQueryIODBConnection();
    		st=conn.createStatement();
    		rs=st.executeQuery(getAllJobDetailsMapred);
    		while(rs.next()){
     			SchedulerBean sBean = new SchedulerBean();
    			JobDetail jdetail = scheduler.getJobDetail(new JobKey(rs.getString(StdJDBCDelegate.COL_JOB_NAME),rs.getString(StdJDBCDelegate.COL_JOB_GROUP)));
    			JobDataMap jMap = jdetail.getJobDataMap();
    			sBean.setName(jdetail.getKey().getName());
    			sBean.setGroup(jdetail.getKey().getGroup());
    			sBean.setInterval(jMap.getInt(SchedulerConstants.FREQUENCY));
    			sBean.setNotificationEnable(Boolean.parseBoolean((String)jMap.get(SchedulerConstants.NOTIFICATION_ENABLE)));
    			if(sBean.isNotificationEnable()){
	    			sBean.setNotificationMessage((String)jMap.get(SchedulerConstants.NOTIFICATION_MESSAGE));
	    			sBean.setNotificationType((String)jMap.get(SchedulerConstants.NOTIFICATION_TYPE));
	    			if(sBean.getNotificationType().equals(SchedulerConstants.NOTIFICATION_EMAIL))
	    			{
	    				sBean.setEmailUserIds(RemoteManager.getUserDetailInfo((String)jMap.get(SchedulerConstants.EMAIL_ADDRESS)).toString());
	    			}
    			}
    			else{
    				sBean.setNotificationMessage(null);
	    			sBean.setNotificationType(null);
	    			sBean.setEmailUserIds(null);
    			}
    			sBean.setTime(Long.parseLong((String)jMap.get(SchedulerConstants.REPORT_TIME)));
    			sBean.setJobName((String)jMap.get(SchedulerConstants.MAPRED_JOB_NAME));
    			scheduleList.add(sBean);
    		}	
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error while getting scheduler info from DB "+e.getMessage(),e);
    	}
    	finally{
    		try{
    			CoreDBManager.closeConnection(conn);
    		}catch(Exception e){
    			AppLogger.getLogger().fatal("Exception Caught while closing connection. "+e.getMessage(),e);
    		}
    		try{
    			DatabaseFunctions.closeSQLObjects(st, rs);
    		}
    		catch(Exception e){
    			AppLogger.getLogger().fatal("Exception Caught while closing sql objects "+e.getMessage(),e);
    		}
    	}
    }
    
    
    public static boolean deleteJob(String name, String group){
    	try{
	    	if(isSchedulerInitialized()){
	    		scheduler.deleteJob(new JobKey(name, group));
	    	}
	    	return true;
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error occured while deletion "+e.getMessage(),e);
    	}
    	return false;
    	
    }
    
    
    
    public static void updateJob(SchedulerBean schedule){
    	try{
	    	if(isSchedulerInitialized()&&!scheduler.isStarted()){
	    		startScheduler();
	    	}
	    	JobDetailImpl jobDetailImpl = (JobDetailImpl)scheduler.getJobDetail(new JobKey(schedule.getName(),schedule.getGroup()));
	    	setLastJobId(schedule.getGroup());
	    	jobDetailImpl.setDescription(null);
	    	jobDetailImpl.setDurability(true);
	    	jobDetailImpl.setRequestsRecovery(true);
	    	JobDataMap jobDataMap = new JobDataMap();
	    	setCommonProperties(schedule,jobDataMap);
	    	jobDetailImpl.setJobDataMap(jobDataMap);
	    	updateTrigger(jobDetailImpl,schedule);
	    	
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while adding job: "+e.getMessage(),e);
    	}
    }
    
    
    public static void updateQueryJob(QuerySchedulerBean schedule){
    	try{
	    	if(isSchedulerInitialized()&&!scheduler.isStarted()){
	    		startScheduler();
	    	}
	    	JobDetailImpl jobDetailImpl = (JobDetailImpl)scheduler.getJobDetail(new JobKey(schedule.getName(),schedule.getGroup()));
	    	setLastJobId(schedule.getGroup());
	    	jobDetailImpl.setDescription(null);
	    	jobDetailImpl.setDurability(true);
	    	jobDetailImpl.setRequestsRecovery(true);
	    	JobDataMap jobDataMap = new JobDataMap();
	    	setCommonProperties(schedule,jobDataMap);
	    	jobDetailImpl.setJobDataMap(jobDataMap);
	    	updateQueryTrigger(jobDetailImpl,schedule);
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while adding job: "+e.getMessage(),e);
    	}
    }
    
    
    public static void updateMRJob(SchedulerBean schedule){
    	try{
	    	if(isSchedulerInitialized()&&!scheduler.isStarted()){
	    		startScheduler();
	    	}
	    	JobDetailImpl jobDetailImpl = (JobDetailImpl)scheduler.getJobDetail(new JobKey(schedule.getName(),schedule.getGroup()));
	    	setLastJobId(schedule.getGroup());
	    	jobDetailImpl.setDescription(null);
	    	jobDetailImpl.setDurability(true);
	    	jobDetailImpl.setRequestsRecovery(true);
	    	JobDataMap jobDataMap = new JobDataMap();
	    	setMapRedCommonProperties(schedule,jobDataMap);
	    	jobDetailImpl.setJobDataMap(jobDataMap);
	    	updateTrigger(jobDetailImpl,schedule);
	    	
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while adding job: "+e.getMessage(),e);
    	}
    }
    
    

    public static void updateTrigger(JobDetailImpl jobDetailImpl,SchedulerBean schedule){
    	try{
    	Trigger trigger = null;
    	
    	String triggerName = schedQueryioTriggerName+SchedulerConstants.HIPHEN+(schedule.getName().substring(schedule.getName().indexOf(SchedulerConstants.HIPHEN)+1));
    	trigger = scheduler.getTrigger(new TriggerKey(triggerName,schedule.getGroup()));
    	switch (schedule.getInterval())
         {
             case SchedulerConstants.SCH_FREQUENCY_DAILY:		//Daily
             {
             	trigger= (new CalendarIntervalTriggerImpl(triggerName,schedule.getGroup(),DateBuilder.IntervalUnit.DAY,1));
             	((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
             	//trigger.setName();
                break;
             }
             case SchedulerConstants.SCH_FREQUENCY_WEEKLY:	//Weekly
             {
            	 trigger= new CalendarIntervalTriggerImpl(triggerName,schedule.getGroup(),DateBuilder.IntervalUnit.DAY,7);
            	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
            	 break;
             }
             case SchedulerConstants.SCH_FREQUENCY_TWELVE_HOURS:
             {
            	 trigger= new CalendarIntervalTriggerImpl(triggerName,schedule.getGroup()
            			 ,DateBuilder.IntervalUnit.HOUR,12);
            	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
                 break; 
             }
             default:	//Once
             {
            	trigger= new SimpleTriggerImpl(triggerName,schedule.getGroup(), new Date(schedule.getTime()), new Date(schedule.getTime()+60000), 0, 0);
                 break;
             }	                
         }
    	deleteJob(schedule.getName(),schedule.getGroup());
    	  scheduler.scheduleJob(jobDetailImpl, trigger);
         AppLogger.getLogger().fatal("A job " + jobDetailImpl.getName() + " is re-scheduled successfully and will be triggerd at " + trigger.getStartTime());
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while scheduling job: "+e.getMessage(), e);
    	}
   }
    
    
    public static void updateQueryTrigger(JobDetailImpl jobDetailImpl,QuerySchedulerBean querySchedule){
    	try{
        	Trigger trigger = null;
        	String grpName=SCHED_BIG_QUERY_JOB_NAME+SchedulerConstants.HIPHEN+String.valueOf(querySchedule.getQuery());
        	String triggerName = schedQueryioTriggerName+SchedulerConstants.HIPHEN+String.valueOf(getLastJobId());
        	switch (querySchedule.getInterval())
             {
                 case SchedulerConstants.SCH_FREQUENCY_DAILY:		//Daily
                 {
                 	trigger= (new CalendarIntervalTriggerImpl(triggerName,grpName,DateBuilder.IntervalUnit.DAY,1));
                 	((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(querySchedule.getTime()));
                 	//trigger.setName();
                    break;
                 }
                 case SchedulerConstants.SCH_FREQUENCY_WEEKLY:	//Weekly
                 {
                	 trigger= new CalendarIntervalTriggerImpl(triggerName,grpName,DateBuilder.IntervalUnit.DAY,7);
                	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(querySchedule.getTime()));
                	 break;
                 }
                 case SchedulerConstants.SCH_FREQUENCY_TWELVE_HOURS:
                 {
                	 trigger= new CalendarIntervalTriggerImpl(triggerName,grpName
                			 ,DateBuilder.IntervalUnit.HOUR,12);
                	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(querySchedule.getTime()));
                     break; 
                 }
                 default:	//Once
                 {
                	 if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Trriger set to once Time :"+querySchedule.getTime());
                	  
                	trigger= new SimpleTriggerImpl(triggerName,grpName, new Date(querySchedule.getTime()), new Date(querySchedule.getTime()+60000), 0, 0);
                     break;
                 }	                
             }
        	deleteJob(querySchedule.getName(),querySchedule.getGroup());
        	scheduler.scheduleJob(jobDetailImpl, trigger);
             if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("A job " + jobDetailImpl.getName() + " is scheduled successfully and will be triggerd at " + trigger.getStartTime());
        	}
        	catch(Exception e){
        		AppLogger.getLogger().fatal("Error Occured while scheduling job: "+e.getMessage(), e);
        	}
   }
    
    public static void updateMRTrigger(JobDetailImpl jobDetailImpl,SchedulerBean schedule){
    	try{
    	Trigger trigger = null;
    	String triggerName = schedQueryioTriggerName+SchedulerConstants.HIPHEN+String.valueOf(getLastJobId());
    	trigger = scheduler.getTrigger(new TriggerKey(triggerName,schedule.getGroup()));
    	switch (schedule.getInterval())
         {
             case SchedulerConstants.SCH_FREQUENCY_DAILY:		//Daily
             {
             	trigger= (new CalendarIntervalTriggerImpl(triggerName,schedule.getGroup(),DateBuilder.IntervalUnit.DAY,1));
             	((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
             	//trigger.setName();
                break;
             }
             case SchedulerConstants.SCH_FREQUENCY_WEEKLY:	//Weekly
             {
            	 trigger= new CalendarIntervalTriggerImpl(triggerName,schedule.getGroup(),DateBuilder.IntervalUnit.DAY,7);
            	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
            	 break;
             }
             case SchedulerConstants.SCH_FREQUENCY_TWELVE_HOURS:
             {
            	 trigger= new CalendarIntervalTriggerImpl(triggerName,schedule.getGroup()
            			 ,DateBuilder.IntervalUnit.HOUR,12);
            	 ((CalendarIntervalTriggerImpl)trigger).setStartTime(new Date(schedule.getTime()));
                 break; 
             }
             default:	//Once
             {
            	trigger= new SimpleTriggerImpl(triggerName,schedule.getGroup(), new Date(schedule.getTime()), new Date(schedule.getTime()+60000), 0, 0);
                 break;
             }	                
         }
    		deleteJob(schedule.getName(),schedule.getGroup());
    	  scheduler.scheduleJob(jobDetailImpl, trigger);
         AppLogger.getLogger().fatal("A job " + jobDetailImpl.getName() + " is re-scheduled successfully and will be triggerd at " + trigger.getStartTime());
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error Occured while scheduling job: "+e.getMessage(), e);
    	}
   }
    
    
   public static ArrayList getAllSysReportsScheduleID()
   {
    	ArrayList scheduleList = null;
    	ArrayList scheduleIdList = null;
    	try{
    		scheduleList = new ArrayList();
    		scheduleIdList = new ArrayList();
    		SchedulerDAO.getAllSysReportsSchedules(scheduleList);
    		for(int i=0;i<scheduleList.size();i++){
    			SchedulerBean sched = (SchedulerBean)scheduleList.get(i);
    			scheduleIdList.add(sched.getName());
    		}
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error while fetching ScheduleId");
    	}
    	return scheduleIdList;
    }
    
    public static ArrayList getAllBigQueryScheduleID()
    {
    	ArrayList scheduleList = null;
    	ArrayList scheduleIdList = null;
    	try{
    		scheduleList = new ArrayList();
    		scheduleIdList = new ArrayList();
    		SchedulerDAO.getAllBigQuerySchedules(scheduleList);
    		for(int i=0;i<scheduleList.size();i++){
    			QuerySchedulerBean sched = (QuerySchedulerBean)scheduleList.get(i);
    			scheduleIdList.add(sched.getName());
    		}
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error while fetching ScheduleId");
    	}
    	return scheduleIdList;
    }
    
    public static ArrayList getAllMapRedScheduleID()
    {
    	ArrayList scheduleList = null;
    	ArrayList scheduleIdList = null;
    	try{
    		scheduleList = new ArrayList();
    		scheduleIdList = new ArrayList();
    		SchedulerDAO.getAllMapRedSchedules(scheduleList);
    		for(int i=0;i<scheduleList.size();i++){
    			SchedulerBean sched = (SchedulerBean)scheduleList.get(i);
    			scheduleIdList.add(sched.getName());
    		}
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal("Error while fetching ScheduleId");
    	}
    	return scheduleIdList;
    }
    
    
    
    public static ArrayList getTriggerDetail(String jobGroup, String jobName)
    {
    	ArrayList arr = null;
    	Connection con = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:a");
    	try
    	{
    		arr = new ArrayList();
    		con = CoreDBManager.getQueryIODBConnection();
    		ps = con.prepareStatement(getTriggerDetails);
    		ps.setString(1, jobGroup);
    		ps.setString(2, jobName);
    		rs = ps.executeQuery();
    		while(rs.next())
    		{
    			TriggerDetailBean tgBean = new TriggerDetailBean();
    			tgBean.setID(rs.getInt(1));
    			tgBean.setJobName(jobName);
    			String grp = jobGroup;
    			if(grp.equals(SchedulerConstants.MAPRED_GROUP_NAME))
    			{
    				grp = "MapReduce Job";	
    			}
    			else
				{
    				grp = grp.substring(0,grp.indexOf(SchedulerConstants.HIPHEN));
    				if(grp.equals(SCHED_SYS_REPORT_JOB_NAME)){
    					grp = "System Reports";
    				}
    				else{
    					grp = "Big Query";
    				}
				}
    			tgBean.setJobGroup(grp);
    			tgBean.setStartTime(sdf.format(rs.getTimestamp(2).getTime()));
    			tgBean.setEndTime(sdf.format(rs.getTimestamp(3).getTime()));
    			tgBean.setStatus(rs.getString(4));
    			tgBean.setReasonForFailure(rs.getString(5));
    			arr.add(tgBean);
    		}
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal(e.getMessage(),e);
    	}
    	finally{
    		try{
	    		if(rs!=null){
	    			DatabaseFunctions.closeResultSet(rs);
	    		}
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
    	return arr;
    }
    
    
    public static ArrayList getAllTriggerDetail()
    {
    	ArrayList arr = null;
    	Connection con = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:a");
    	try
    	{
    		arr = new ArrayList();
    		con = CoreDBManager.getQueryIODBConnection();
    		stmt = con.createStatement();
    		rs = stmt.executeQuery(getAllTriggerDetails);
    		while(rs.next())
    		{
    			TriggerDetailBean tgBean = new TriggerDetailBean();
    			tgBean.setID(rs.getInt(1));
    			tgBean.setJobName(rs.getString(2));
    			String grp = rs.getString(3);
    			if(grp.equals(SchedulerConstants.MAPRED_GROUP_NAME))
    			{
    				grp = "MapReduce Job";	
    			}
    			else
				{
    				if(grp.indexOf(SchedulerConstants.HIPHEN) != -1)
    					grp = grp.substring(0,grp.indexOf(SchedulerConstants.HIPHEN));
    				if(grp.equals(SCHED_SYS_REPORT_JOB_NAME)){
    					grp = "System Reports";
    				}
    				else{
    					grp = "Hadoop SQL Query";
    				}
				}
    			tgBean.setJobGroup(grp);
    			tgBean.setStartTime(sdf.format(rs.getTimestamp(4).getTime()));
    			tgBean.setEndTime(sdf.format(rs.getTimestamp(5).getTime()));
    			tgBean.setStatus(rs.getString(6));
    			tgBean.setReasonForFailure(rs.getString(7));
    			arr.add(tgBean);
    		}
    	}
    	catch(Exception e){
    		AppLogger.getLogger().fatal(e.getMessage(),e);
    	}
    	finally{
    		try{
	    		if(rs!=null){
	    			DatabaseFunctions.closeResultSet(rs);
	    		}
	    		if(stmt!=null){
	    			DatabaseFunctions.closeStatement(stmt);
	    		}
	    		if(con!=null){
	    			DatabaseManager.closeDbConnection(con);
	    		}
    		}
    		catch(Exception e){
    			AppLogger.getLogger().fatal(e.getMessage(),e);
    		}
    	}
    	return arr;
    }
    
    public static DWRResponse deleteTriggers(ArrayList triggerList)
    {
    	DWRResponse resp = new DWRResponse();
    	Connection con = null;
    	PreparedStatement ps = null;
    	try{
    		con = CoreDBManager.getQueryIODBConnection();
    		for(int i=0;i<triggerList.size();i++)
    		{
	    		ps = con.prepareStatement(deleteSelectedTriggers);
	    		ps.setInt(1, Integer.parseInt(((String)triggerList.get(i))));
	    		ps.executeUpdate();
	    		try{
	    			ps.close();
	    		}
	    		catch(Exception e)
	    		{
	    			AppLogger.getLogger().fatal(e.getMessage(),e);
	    		}
    		}
    		resp.setDwrResponse(true, SchedulerConstants.SUCCESS, 200);
    	}
    	catch(Exception e)
    	{
    		AppLogger.getLogger().fatal(e.getMessage(),e);
    		resp.setDwrResponse(false, e.getMessage(), 500);
    	}
    	finally
    	{
    		if(con!=null)
    		try{
    			CoreDBManager.closeConnection(con);
    		}
    		catch(Exception e)
    		{
    			AppLogger.getLogger().fatal(e.getMessage(),e);
    		}
    	}
    	return resp;
    }
    
}