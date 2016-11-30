package com.queryio.core.namespace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.NotifyBean;
import com.queryio.core.dao.NotifyDAO;
import com.queryio.core.notification.NotificationHandler;
import com.queryio.core.notifier.notifiers.NotificationManager;
import com.queryio.scheduler.service.SchedulerConstants;

public class NamespaceBackupJobExecutor implements Job {

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		String status = null;
		Connection con = null;
		PreparedStatement ps = null;
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
		JobDataMap jDataMap = jobExecutionContext.getMergedJobDataMap();
		String message = null;
		ArrayList usersIdList = null;
		String notificationEnable = null;
		String notificationType = null;
		String migrationId = null;
		try {
			migrationId = (String) jDataMap.get(SchedulerConstants.BACKUP_JOB_NAME);

			notificationEnable = (String) jDataMap.get(SchedulerConstants.NOTIFICATION_ENABLE);
			if (Boolean.parseBoolean(notificationEnable)) {
				message = jDataMap.getString(SchedulerConstants.NOTIFICATION_MESSAGE);
				notificationType = (String) jDataMap.get(SchedulerConstants.NOTIFICATION_TYPE);
				if (notificationType.equals(SchedulerConstants.NOTIFICATION_EMAIL)) {
					String userIds = (String) jDataMap.get(SchedulerConstants.EMAIL_ADDRESS);
					String userList[] = userIds.substring(1, userIds.length() - 1).split(",");
					usersIdList = new ArrayList();
					for (String userID : userList) {
						usersIdList.add(userID.trim());
					}
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			throw new Exception("Scheduler Execution Exception");
		}
		String status = null;

		status = performMigrationTask(migrationId, message);
		try {
			if (Boolean.parseBoolean(notificationEnable)) {
				if (notificationType.equals(SchedulerConstants.NOTIFICATION_EMAIL)) {
					mailStatus(message, status, usersIdList);
				} else {
					logStatus(message, status);
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			throw e;
		}

		if (!status.equals(FinalApplicationStatus.SUCCEEDED.toString())) {
			throw new Exception("Error Executing NamespaceBackup Job.");
		}

	}

	private String performMigrationTask(String migrationId, String message) {
		String status = null;
		try {
			DWRResponse resp = new DWRResponse();
			if (resp.isTaskSuccess()) {

			} else {
				status = resp.getResponseMessage();
			}
		} catch (Exception e) {
			status = e.getMessage();
			AppLogger.getLogger().fatal("Error Occurred while executing scheduled task " + e.getMessage(), e);
		}
		return status;
	}

	private String pollApplicationStatus(String applicationId) {
		if (applicationId != null) {

		} else {

		}

		return null;
	}

	private void mailStatus(String message, String status, ArrayList usersList) throws Exception {
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
		if (notify.isEmailEnabled()) {
			NotificationHandler.generateEmailNotification(notifMgr, "Scheduled NamespaceBackup Job",
					message + " : " + status, notify, usersList, true);
		} else {
			throw new Exception("Error Generating Notification");
		}
	}

	private void logStatus(String message, String status) throws Exception {
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
					"[Scheduled NamespaceBackup Job] - " + message + " : " + status, notify, true);
		} else {
			throw new Exception("Error Generating Notification");
		}

	}
}
