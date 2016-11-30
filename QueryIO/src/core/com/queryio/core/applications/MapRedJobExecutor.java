package com.queryio.core.applications;

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
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.MapRedJobConfig;
import com.queryio.core.bean.NotifyBean;
import com.queryio.core.dao.MapRedJobConfigDAO;
import com.queryio.core.dao.NotifyDAO;
import com.queryio.core.notification.NotificationHandler;
import com.queryio.core.notifier.notifiers.NotificationManager;
import com.queryio.scheduler.service.SchedulerConstants;

public class MapRedJobExecutor implements Job {

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		String status = null;
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ps = connection.prepareStatement(QueryConstants.INSERT_SCHEDULE_JOB_STATE);
			String jobName = jobExecutionContext.getJobDetail().getKey().getName();
			ps.setString(1, jobName);
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

			if ((jobName != null) && (jobName.startsWith(QueryIOConstants.DATATAGGING_PREFIX))) {
				int interval = Integer.parseInt(
						jobExecutionContext.getJobDetail().getJobDataMap().getString(SchedulerConstants.FREQUENCY));
				updateJobDataTagging(connection, jobName, interval);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				if (ps != null) {
					DatabaseFunctions.closePreparedStatement(ps);
				}
				if (connection != null) {
					DatabaseManager.closeDbConnection(connection);
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
		ArrayList jobIdList = null;
		String notificationEnable = null;
		String notificationType = null;
		try {
			String jobIds = (String) jDataMap.get(SchedulerConstants.MAPRED_JOB_NAME);
			String jobList[] = jobIds.substring(1, jobIds.length() - 1).split(",");
			jobIdList = new ArrayList();
			for (String userID : jobList) {
				jobIdList.add(userID.trim());
			}
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
		for (int i = 0; i < jobIdList.size(); i++) {
			String jobName = (String) jobIdList.get(i);
			status = executeMapReduceJob(jobName, message);
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
		}
		if (!status.equals(FinalApplicationStatus.SUCCEEDED.toString())) {
			throw new Exception("Error Executing MapReduce Job.");
		}

	}

	private String executeMapReduceJob(String jobName, String message) {
		String status = null;
		try {
			// By default Recursive was true, and input path filter was false.
			DWRResponse resp = ApplicationManager.runJob(jobName, true, false, null);
			if (resp.isTaskSuccess()) {
				String applicationId = resp.getResponseMessage();
				while (true) {
					status = pollApplicationStatus(applicationId);
					if (status.equals(FinalApplicationStatus.UNDEFINED.toString())) {
						Thread.sleep(5000);
					} else {
						break;
					}
				}
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
			String applicationStatus = ApplicationManager.getApllicationStatus(applicationId);

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("applicationStatus: " + applicationStatus);

			if (applicationStatus.equals(FinalApplicationStatus.SUCCEEDED.toString())) {
				return FinalApplicationStatus.SUCCEEDED.toString();

			} else if (applicationStatus.equals(FinalApplicationStatus.FAILED.toString())
					|| applicationStatus.equals(FinalApplicationStatus.KILLED.toString())) {
				return FinalApplicationStatus.FAILED.toString();
			} else {
				return FinalApplicationStatus.UNDEFINED.toString();
			}
		} else {
			return FinalApplicationStatus.UNDEFINED.toString();
		}
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
			NotificationHandler.generateEmailNotification(notifMgr, "Scheduled MapReduce Job", message + " : " + status,
					notify, usersList, true);
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
					"[Scheduled MapReduce Job] - " + message + " : " + status, notify, true);
		} else {
			throw new Exception("Error Generating Notification");
		}

	}

	private void updateJobDataTagging(Connection connection, String jobName, int interval) throws Exception {
		MapRedJobConfig jobConfig = MapRedJobConfigDAO.get(connection, jobName);
		if (jobConfig != null) {
			String arguments = jobConfig.getArguments();
			String args = arguments.substring(0, arguments.lastIndexOf(" "));
			long endTime = Long.valueOf(arguments.substring(arguments.lastIndexOf(" ")).trim());
			long startTime = endTime;
			endTime = endTime + interval;

			String newArgs = args.substring(0, args.lastIndexOf(" ")) + " " + startTime + " " + endTime;

			jobConfig.setArguments(newArgs);

			MapRedJobConfigDAO.updateJob(connection, jobConfig);
		}
	}
}
