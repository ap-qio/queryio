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

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.report.ReportConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.ZipUtil;
import com.queryio.core.bean.NotifyBean;
import com.queryio.core.bean.User;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.customtags.BigQueryManager;
import com.queryio.core.dao.NotifyDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.notification.NotificationHandler;
import com.queryio.core.notifier.notifiers.NotificationManager;

public class QueryJobExecutor implements Job {
	public static String typeHtml = "html";
	public static String typePdf = "pdf";
	public static String typeXls = "xls";

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		Connection con = null;
		PreparedStatement ps = null;
		String status = null;
		try {
			con = CoreDBManager.getQueryIODBConnection();
			ps = con.prepareStatement(QueryConstants.INSERT_SCHEDULE_JOB_STATE);
			ps.setString(1, jobExecutionContext.getJobDetail().getKey().getName());
			ps.setString(2, jobExecutionContext.getJobDetail().getKey().getGroup());
			ps.setTimestamp(3, new Timestamp(jobExecutionContext.getFireTime().getTime()));
			Boolean flag = true;
			try {
				executeJob(jobExecutionContext);
			} catch (Exception e) {
				flag = false;
				status = e.getMessage();
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			if (flag) {
				ps.setString(5, SchedulerConstants.SUCCESS);
			} else {
				ps.setString(5, SchedulerConstants.FAILURE);
			}
			ps.setString(6, status);
			ps.executeUpdate();
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				if (ps != null) {
					DatabaseFunctions.closePreparedStatement(ps);
				}
				if (con != null) {
					DatabaseManager.closeDbConnection(con);
				}
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public void executeJob(JobExecutionContext jobExecutionContext) throws Exception {
		ArrayList exportFormatList = null;
		ArrayList usersIdList = null;
		ArrayList finalUsersIdList = null;
		ArrayList queryIDLists = null;
		String message = null;
		String nameNode = null;
		String notificationEnable = null;
		String notificationType = null;
		String username = null;
		try {
			exportFormatList = new ArrayList();

			queryIDLists = new ArrayList();
			JobDataMap jDataMap = jobExecutionContext.getMergedJobDataMap();

			nameNode = (String) jDataMap.get(SchedulerConstants.NAMENODE);
			username = (String) jDataMap.get(SchedulerConstants.USERNAME);
			String queryIDs = (String) jDataMap.get(SchedulerConstants.QUERY);
			String queryList[] = (queryIDs.substring(1, queryIDs.length() - 1)).split(",");

			for (String queryID : queryList) {
				queryIDLists.add(queryID.trim());
			}

			String formatIds = (String) jDataMap.get(SchedulerConstants.FORMAT_TYPE);
			String formatList[] = formatIds.substring(1, formatIds.length() - 1).split(",");
			for (String formatType : formatList) {
				exportFormatList.add(formatType.trim());
			}
			notificationEnable = (String) jDataMap.get(SchedulerConstants.NOTIFICATION_ENABLE);
			notificationType = null;
			if (Boolean.parseBoolean(notificationEnable)) {
				message = (String) jDataMap.get(SchedulerConstants.NOTIFICATION_MESSAGE);
				notificationType = (String) jDataMap.get(SchedulerConstants.NOTIFICATION_TYPE);
				if (notificationType.equals(SchedulerConstants.NOTIFICATION_EMAIL)) {
					usersIdList = new ArrayList();
					String userIds = (String) jDataMap.get(SchedulerConstants.EMAIL_ADDRESS);
					String userList[] = userIds.substring(1, userIds.length() - 1).split(",");
					for (String userID : userList) {
						usersIdList.add(userID.trim());
					}

					finalUsersIdList = getEmailIds(usersIdList);
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			throw new Exception("Scheduler Execution Exception");
		}
		Boolean jobSuccess = true;
		for (int i = 0; i < queryIDLists.size(); i++) {
			String query = (String) queryIDLists.get(i);
			ArrayList fileList = null;
			try {
				for (int j = 0; j < exportFormatList.size(); j++) {
					if (j == 0) {
						fileList = new ArrayList();
					}
					String format = findFormat((String) exportFormatList.get(j));
					String filePath = executeQueryJob(query, nameNode, username, finalUsersIdList, message, format);
					String designFilePath = BigQueryManager.getDesignFilePath(nameNode, query, username);
					String reportDir = designFilePath.substring(0, designFilePath.lastIndexOf(File.separator));
					String finalPath = reportDir + File.separator + filePath;

					if (filePath != null) {
						if (format.equalsIgnoreCase(ReportConstants.TYPE_HTML)) {
							ArrayList listFiles = new ArrayList();
							File reportFile = new File(reportDir + File.separator + filePath);
							listFiles.add(reportFile);
							String temp = filePath.replace(File.separator, "");
							File folder = new File(
									reportDir + File.separator + "resources_" + temp.replace(".html", ""));
							listFiles.add(folder);
							String destZipFile = reportDir + File.separator + filePath.replace(".html", ".zip");
							ZipUtil.compressFiles(listFiles, destZipFile);
							fileList.add(destZipFile);

						} else {

							fileList.add(finalPath);
						}

					} else {
						jobSuccess = false;
					}
				}
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
				throw new Exception("Error Executing Query");
			}
			try {
				if (Boolean.parseBoolean(notificationEnable)) {
					if (notificationType.equals(SchedulerConstants.NOTIFICATION_EMAIL)) {
						mailGeneratedBigQueryReport(fileList, message, finalUsersIdList);
					} else {
						logGeneratedBigQueryReport(fileList, message);
					}
				}
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
				throw e;
			}
		}
	}

	private ArrayList getEmailIds(ArrayList<String> usersIdList) throws Exception {
		Connection connection = null;
		ArrayList usersList = new ArrayList();

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			if (usersIdList != null) {
				for (int i = 0; i < usersIdList.size(); i++) {
					User user = UserDAO.getUserDetail(connection, Integer.parseInt((String) usersIdList.get(i)));
					usersList.add(user);
				}
			}
			return usersList;
		} catch (Exception e) {
			throw e;
		}
	}

	private String findFormat(String val) {
		if (val.equals("0")) {
			return typeHtml;
		} else if (val.equals("1")) {
			return typePdf;
		} else {
			return typeXls;
		}
	}

	private String executeQueryJob(String queryID, String namenodeId, String userName, ArrayList usersIdList,
			String message, String format) {
		JSONObject object = BigQueryManager.executeQuery(namenodeId, queryID, userName, true, format);
		if (object != null) {
			int executionId = (Integer) object.get("executionId");
			JSONObject queryObj = BigQueryManager.isQueryCompleteInternal(executionId, false, format,
					RemoteManager.getLoggedInUser());
			String filePath = (String) queryObj.get("filePath");
			while (queryObj.get("error") == null) {
				try {
					Thread.sleep(1000); // For Adhoc jobs using OS3 Server.
				} catch (InterruptedException e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
				queryObj = BigQueryManager.isQueryCompleteInternal(executionId, false, format,
						RemoteManager.getLoggedInUser());
				filePath = (String) queryObj.get("filePath");
				if (filePath != null) {
					return filePath;
				}
			}
		}
		return null;
	}

	private void mailGeneratedBigQueryReport(ArrayList fileList, String message, ArrayList usersList) throws Exception {
		NotificationManager notifMgr = NotificationManager.getInstance();
		notifMgr.setConfigXMLFilePath(EnvironmentalConstants.getWebinfDirectory());
		notifMgr.initializeNotificationManager();
		NotifyBean notify = new NotifyBean();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			notify = NotifyDAO.getNotificationSettings(connection);
		} finally {
			CoreDBManager.closeConnection(connection);
		}

		for (int i = 0; i < fileList.size(); i++) {
			AppLogger.getLogger().debug("Files getting mailed[" + i + "] : " + fileList.get(i).toString());
		}

		notify.setAttachments(fileList);
		if (notify.isEmailEnabled()) {
			NotificationHandler.generateEmailNotification(notifMgr, "Scheduled Big-Query Report", message, notify,
					usersList, true);
		} else {
			throw new Exception("Error Generating Notification");
		}
	}

	private void logGeneratedBigQueryReport(ArrayList fileList, String message) throws Exception {
		NotificationManager notifMgr = NotificationManager.getInstance();
		notifMgr.setConfigXMLFilePath(EnvironmentalConstants.getWebinfDirectory());
		notifMgr.initializeNotificationManager();
		NotifyBean notify = new NotifyBean();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			notify = NotifyDAO.getNotificationSettings(connection);
		} finally {
			CoreDBManager.closeConnection(connection);
		}
		if (notify.isLogEnabled()) {
			NotificationHandler.generateLogNotification(notifMgr,
					"[Scheduled Big-Query Report] - " + message + fileList.toString(), notify, true);
		} else {
			throw new Exception("Error Generating Notification");
		}
	}

}
