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
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.applications.ApplicationManager;
import com.queryio.core.bean.DiagnosisStatusBean;
import com.queryio.core.bean.NotifyBean;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.NotifyDAO;
import com.queryio.core.notification.NotificationHandler;
import com.queryio.core.notifier.notifiers.NotificationManager;
import com.queryio.scheduler.service.SchedulerConstants;

public class DiagnosisProcessExecutor implements Job {

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
		String loggedInUser = null;
		String notificationEnable = null;
		String notificationType = null;

		String jobName = jDataMap.getString(SchedulerConstants.MAPRED_JOB_NAME);
		String namenodeId = jDataMap.getString("NAMENODEID");
		long startIndex = Long.parseLong(jDataMap.getString("STARTINDEX"));
		long endIndex = Long.parseLong(jDataMap.getString("ENDINDEX"));
		loggedInUser = jDataMap.getString("LOGGEDINUSER");
		try {
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

		String diagnosisId = namenodeId + new Timestamp(System.currentTimeMillis());

		status = startDiagnosisProcess(jobName, message, diagnosisId, namenodeId, startIndex, endIndex, loggedInUser);
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

	private String startDiagnosisProcess(String jobName, String message, String diagnosisId, String namenodeId,
			long startIndex, long endIndex, String loggedInUser) {
		String status = null;
		try {
			if (RemoteManager.isNonAdminAndDemo(loggedInUser)) {
				throw new Exception(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
			}

			DiagnosisAndRepairManager.diagnose(diagnosisId, namenodeId, startIndex, endIndex, loggedInUser);

			Thread.sleep(5000);

			DiagnosisStatusBean diagnosisStatus = DiagnosisAndRepairManager.getDiagnosisStatusForId(diagnosisId);
			if (diagnosisStatus != null) {
				status = diagnosisStatus.getStatus();
				while (diagnosisStatus.getStatus().equals(QueryIOConstants.PROCESS_STATUS_DIAGNOSING)) {
					diagnosisStatus = DiagnosisAndRepairManager.getDiagnosisStatusForId(diagnosisId);

					Thread.sleep(5000);

					if (diagnosisStatus.getStatus().equals(QueryIOConstants.PROCESS_STATUS_DIAGNOSIS_COMPLETE)
							|| diagnosisStatus.equals(QueryIOConstants.PROCESS_STATUS_DIAGNOSIS_FAILED)) {
						status = diagnosisStatus.getStatus();
						break;
					}
				}
			} else {
				status = "Diagnosis process could not be started";
			}
		} catch (Exception e) {
			status = "Could not start diagnostic process as scheduled, error: " + e.getMessage();
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
}
