package com.queryio.scheduler.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.conf.RemoteManager;

public class ScheduleManager {
	public static boolean scheduleJob(String interval, String reportTime, ArrayList selectedFormat,
			String notificationType, String notificationMessage, ArrayList userList, ArrayList reportId, String nodeId,
			String schedName) {
		SchedulerBean schedule = null;
		try {
			schedule = new SchedulerBean();
			schedule.setNotificationEnable(true);
			if (userList != null) {
				schedule.setEmailUserIds(userList.toString());
			}
			schedule.setInterval(Integer.parseInt(interval));
			schedule.setNotificationMessage(notificationMessage);
			schedule.setReportId(reportId.toString());
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = format.parse(reportTime);
			schedule.setTime(date.getTime());
			schedule.setNotificationType(notificationType);
			schedule.setSelectedFormat(selectedFormat.toString());
			schedule.setNodeId(nodeId);
			schedule.setName(schedName);
			SchedulerDAO.addJob(schedule);
			return SchedulerDAO.isSchedulerStarted();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error while addidng job" + e.getMessage(), e);
		}
		return false;
	}

	public static boolean scheduleJobWithoutNotification(String interval, String reportTime, ArrayList selectedFormat,
			ArrayList reportId, String nodeId, String schedName) {
		SchedulerBean schedule = null;
		try {
			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("Report Time : "+reportTime+"
			// SlectedFormat :"+selectedFormat+ " notification Type :
			// "+notificationType+" notification Message "
			// +notificationMessage+" userList "+userList+" reportId
			// "+reportId+" schedule Name: "+schedName+"nodeId :"+nodeId);
			schedule = new SchedulerBean();
			schedule.setNotificationEnable(false);
			schedule.setInterval(Integer.parseInt(interval));
			schedule.setReportId(reportId.toString());
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = format.parse(reportTime);
			schedule.setTime(date.getTime());
			schedule.setSelectedFormat(selectedFormat.toString());
			schedule.setNodeId(nodeId);
			schedule.setName(schedName);
			SchedulerDAO.addJob(schedule);
			return SchedulerDAO.isSchedulerStarted();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error while addidng job" + e.getMessage(), e);
		}
		return false;
	}

	public static boolean scheduleQueryJob(String interval, String reportTime, ArrayList selectedFormat,
			String notificationType, String notificationMessage, ArrayList userList, String schedName, ArrayList query,
			String nameNode) {
		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("Report Time : "+reportTime+"
		// SlectedFormat :"+selectedFormat+ " notification Type :
		// "+notificationType
		// +" notification Message "+notificationMessage+" userList "+userList+"
		// schedule Name: "+schedName+"query :"+query);
		QuerySchedulerBean queryScheduler = null;
		try {
			queryScheduler = new QuerySchedulerBean();
			queryScheduler.setNotificationEnable(true);
			if (userList != null) {
				queryScheduler.setEmailUserIds(userList.toString());
			}
			queryScheduler.setUsername(RemoteManager.getLoggedInUser());
			queryScheduler.setNotificationMessage(notificationMessage);
			queryScheduler.setNotificationType(notificationType);
			queryScheduler.setNameNode(nameNode);
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = format.parse(reportTime);
			queryScheduler.setTime(date.getTime());
			queryScheduler.setSelectedFormat(selectedFormat.toString());
			queryScheduler.setName(schedName);
			queryScheduler.setQuery(query.toString());
			queryScheduler.setInterval(Integer.parseInt(interval));
			SchedulerDAO.addQueryJob(queryScheduler);
			return SchedulerDAO.isSchedulerStarted();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error while addidng job" + e.getMessage(), e);
		}
		return false;
	}

	public static boolean scheduleQueryJobWithoutNotification(String interval, String reportTime,
			ArrayList selectedFormat, String schedName, ArrayList query, String nameNode) {
		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("Report Time : "+reportTime+"
		// SlectedFormat :"+selectedFormat+ " notification Type :
		// "+notificationType
		// +" notification Message "+notificationMessage+" userList "+userList+"
		// schedule Name: "+schedName+"query :"+query);
		QuerySchedulerBean queryScheduler = null;
		try {
			queryScheduler = new QuerySchedulerBean();
			queryScheduler.setNotificationEnable(false);
			queryScheduler.setUsername(RemoteManager.getLoggedInUser());
			queryScheduler.setNameNode(nameNode);
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = format.parse(reportTime);
			queryScheduler.setTime(date.getTime());
			queryScheduler.setSelectedFormat(selectedFormat.toString());
			queryScheduler.setName(schedName);
			queryScheduler.setQuery(query.toString());
			queryScheduler.setInterval(Integer.parseInt(interval));
			SchedulerDAO.addQueryJob(queryScheduler);
			return SchedulerDAO.isSchedulerStarted();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error while addidng job" + e.getMessage(), e);
		}
		return false;
	}

	public static boolean scheduleMapRedJob(String interval, String scheduleTime, ArrayList mapRedJobName,
			String scheduleName, String notificationType, String notificationMessage, ArrayList userList) {
		SchedulerBean schedule = null;
		try {
			schedule = new SchedulerBean();
			schedule.setNotificationEnable(true);
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = format.parse(scheduleTime);
			schedule.setTime(date.getTime());
			schedule.setName(scheduleName);
			schedule.setInterval(Integer.parseInt(interval));
			schedule.setNotificationMessage(notificationMessage);
			schedule.setNotificationType(notificationType);
			if (userList != null) {
				schedule.setEmailUserIds(userList.toString());
			}
			schedule.setJobName(mapRedJobName.toString());
			SchedulerDAO.addMapRedJobSchedule(schedule);
			return SchedulerDAO.isSchedulerStarted();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("MapRed job could not be scheduled" + e.getMessage(), e);
		}
		return false;
	}

	public static boolean scheduleNamespaceBackup(String interval, String scheduleTime, String migrationId,
			String scheduleName, String notificationType, String notificationMessage, ArrayList userList) {
		SchedulerBean schedule = null;
		try {
			schedule = new SchedulerBean();
			schedule.setNotificationEnable(true);
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = format.parse(scheduleTime);
			schedule.setTime(date.getTime());
			schedule.setName(scheduleName);
			schedule.setInterval(Integer.parseInt(interval));
			schedule.setNotificationMessage(notificationMessage);
			schedule.setNotificationType(notificationType);
			if (userList != null) {
				schedule.setEmailUserIds(userList.toString());
			}
			schedule.setJobName(migrationId);
			SchedulerDAO.addNamespaceBackupSchedule(schedule);
			return SchedulerDAO.isSchedulerStarted();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("NamespaceBackup job could not be scheduled" + e.getMessage(), e);
		}
		return false;
	}

	public static DWRResponse scheduleNamespaceDiagnosis(String interval, String scheduleTime, String namenodeId,
			long startIndex, long endIndex, String scheduleName, boolean isNotitificationEnable,
			String notificationType, String notificationMessage, ArrayList userList) {
		DWRResponse dwrRespnse = new DWRResponse();
		dwrRespnse.setId(scheduleName);
		SchedulerBean schedule = null;
		try {
			schedule = new SchedulerBean();
			schedule.setNotificationEnable(isNotitificationEnable);
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = format.parse(scheduleTime);
			schedule.setTime(date.getTime());
			schedule.setName(scheduleName);
			schedule.setInterval(Integer.parseInt(interval));
			schedule.setNotificationMessage(notificationMessage);
			schedule.setNotificationType(notificationType);
			if (userList != null) {
				schedule.setEmailUserIds(userList.toString());
			}
			schedule.setJobName(namenodeId);
			SchedulerDAO.addNamespaceDiagnosisSchedule(schedule, namenodeId, startIndex, endIndex);
			if (SchedulerDAO.isSchedulerStarted()) {
				dwrRespnse.setDwrResponse(true, "Backup process scheduled successfully.", 200);
			} else {
				dwrRespnse.setDwrResponse(false, "scheduler failed to schedule backup process.", 500);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("scheduler failed to schedule backup process" + e.getMessage(), e);
			dwrRespnse.setDwrResponse(false, "scheduler failed to schedule backup process" + e.getMessage(), 500);
		}
		return dwrRespnse;
	}

	public static boolean scheduleMapRedJobWithoutNotification(String interval, String scheduleTime,
			ArrayList mapRedJobName, String scheduleName) {
		SchedulerBean schedule = null;
		try {
			schedule = new SchedulerBean();
			schedule.setNotificationEnable(false);
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = format.parse(scheduleTime);
			schedule.setTime(date.getTime());
			schedule.setName(scheduleName);
			schedule.setInterval(Integer.parseInt(interval));
			schedule.setJobName(mapRedJobName.toString());
			SchedulerDAO.addMapRedJobSchedule(schedule);
			return SchedulerDAO.isSchedulerStarted();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("MapRed job could not be scheduled" + e.getMessage(), e);
		}
		return false;
	}

	public static boolean scheduleMapRedJobWithoutNotificationDataTagging(String interval, long scheduleTime,
			ArrayList mapRedJobName, String scheduleName) {
		SchedulerBean schedule = null;
		try {
			schedule = new SchedulerBean();
			schedule.setNotificationEnable(false);
			schedule.setTime(scheduleTime);
			schedule.setName(scheduleName);
			schedule.setInterval(Integer.parseInt(interval));
			schedule.setJobName(mapRedJobName.toString());
			SchedulerDAO.addMapRedJobSchedule(schedule);
			return SchedulerDAO.isSchedulerStarted();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("MapRed job could not be scheduled" + e.getMessage(), e);
		}
		return false;
	}

	public static ArrayList getAllMapRedSchedules() {
		ArrayList scheduleList = null;
		try {
			scheduleList = new ArrayList();
			SchedulerDAO.getAllMapRedSchedules(scheduleList);
			return scheduleList;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Caught exception while getting schedule List", e);
		}
		return null;
	}

	public static ArrayList getAllSysReportsSchedules() {
		ArrayList scheduleList = null;
		try {
			scheduleList = new ArrayList();
			SchedulerDAO.getAllSysReportsSchedules(scheduleList);
			return scheduleList;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Caught exception while getting schedule List", e);
		}
		return null;
	}

	public static ArrayList getAllBigQuerySchedules() {
		ArrayList scheduleList = null;
		try {
			scheduleList = new ArrayList();
			SchedulerDAO.getAllBigQuerySchedules(scheduleList);
			return scheduleList;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Caught exception while getting schedule List", e);
		}
		return null;
	}

	public static boolean deleteJob(ArrayList key) {
		boolean flag = false;
		try {
			AppLogger.getLogger().fatal("key size: " + key.size());
			for (int i = 0; i < key.size(); i++) {
				String jobKey[] = ((String) key.get(i)).split(",");
				String grp = jobKey[1];
				if (jobKey.length > 2) {
					for (int j = 2; j < jobKey.length; j++) {
						grp += "," + jobKey[j];
					}
				}
				flag = SchedulerDAO.deleteJob(jobKey[0], grp);
				if (flag)
					continue;
				else
					break;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error while deleting job: " + e.getMessage(), e);
		}
		return flag;
	}

	public static boolean updateJob(String interval, String reportTime, ArrayList selectedFormat,
			String notificationType, String notificationMessage, ArrayList userList, ArrayList reportId, String name,
			String group, String nodeId, Boolean notificationEnable) {
		SchedulerBean schedule = null;
		try {
			schedule = new SchedulerBean();
			schedule.setNotificationEnable(notificationEnable);
			schedule.setName(name);
			schedule.setGroup(group);
			if (notificationEnable) {
				schedule.setNotificationMessage(notificationMessage);
				schedule.setNotificationType(notificationType);
				if (schedule.getNotificationType().equals(SchedulerConstants.NOTIFICATION_EMAIL)) {
					schedule.setEmailUserIds(userList.toString());
				}
			}
			schedule.setInterval(Integer.parseInt(interval));
			schedule.setReportId(reportId.toString());
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = format.parse(reportTime);
			schedule.setTime(date.getTime());
			schedule.setSelectedFormat(selectedFormat.toString());
			schedule.setNodeId(nodeId);

			SchedulerDAO.updateJob(schedule);
			return SchedulerDAO.isSchedulerStarted();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error while addidng job" + e.getMessage(), e);
		}
		return false;
	}

	public static boolean updateQueryJob(String interval, String reportTime, ArrayList selectedFormat,
			String notificationType, String notificationMessage, ArrayList userList, String schedName, String group,
			ArrayList query, String nameNode, Boolean notificationEnable) {
		QuerySchedulerBean queryScheduler = null;
		try {
			queryScheduler = new QuerySchedulerBean();
			queryScheduler.setNotificationEnable(notificationEnable);
			if (notificationEnable) {
				queryScheduler.setNotificationType(notificationType);
				queryScheduler.setNotificationMessage(notificationMessage);
				if (queryScheduler.getNotificationType().equals(SchedulerConstants.NOTIFICATION_EMAIL)) {
					queryScheduler.setEmailUserIds(userList.toString());
				}
			}
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = format.parse(reportTime);
			queryScheduler.setTime(date.getTime());
			queryScheduler.setSelectedFormat(selectedFormat.toString());
			queryScheduler.setName(schedName);
			queryScheduler.setGroup(group);
			queryScheduler.setQuery(query.toString());
			queryScheduler.setNameNode(nameNode);
			queryScheduler.setInterval(Integer.parseInt(interval));

			SchedulerDAO.updateQueryJob(queryScheduler);
			return SchedulerDAO.isSchedulerStarted();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error while addidng job" + e.getMessage(), e);
		}
		return false;
	}

	public static boolean updateMapRedJob(String interval, String scheduleTime, ArrayList mapRedJobName,
			String scheduleName, String notificationType, String notificationMessage, ArrayList userList, String group,
			Boolean notificationEnable) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("group: " + group);
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("scheduleName: " + scheduleName);
		SchedulerBean schedule = null;
		try {
			schedule = new SchedulerBean();
			schedule.setNotificationEnable(notificationEnable);
			if (notificationEnable) {
				schedule.setNotificationMessage(notificationMessage);
				schedule.setNotificationType(notificationType);
				if (schedule.getNotificationType().equals(SchedulerConstants.NOTIFICATION_EMAIL)) {
					schedule.setEmailUserIds(userList.toString());
				}
			}
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = format.parse(scheduleTime);
			schedule.setTime(date.getTime());
			schedule.setName(scheduleName);
			schedule.setGroup(group);
			schedule.setInterval(Integer.parseInt(interval));
			schedule.setJobName(mapRedJobName.toString());
			SchedulerDAO.updateMRJob(schedule);
			return SchedulerDAO.isSchedulerStarted();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("MapRed job could not be scheduled" + e.getMessage(), e);
		}
		return false;
	}

	public static boolean checkSysReportScheduleId(String id) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("checkScheduleId");
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Id: " + id);
		boolean flag = false;
		try {
			ArrayList arr = SchedulerDAO.getAllSysReportsScheduleID();
			for (int i = 0; i < arr.size(); i++) {
				if (id.equals((String) arr.get(i))) {
					flag = true;
					break;
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("Exception caught while checking ScheduleId already in use or not " + e.getMessage(), e);
		}
		return flag;
	}

	public static boolean checkBigQueryScheduleId(String id) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("checkScheduleId");
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Id: " + id);
		boolean flag = false;
		try {
			ArrayList arr = SchedulerDAO.getAllBigQueryScheduleID();
			for (int i = 0; i < arr.size(); i++) {
				if (id.equals((String) arr.get(i))) {
					flag = true;
					break;
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("Exception caught while checking ScheduleId already in use or not " + e.getMessage(), e);
		}
		return flag;
	}

	public static boolean checkMapRedScheduleId(String id) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("checkScheduleId");
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Id: " + id);
		boolean flag = false;
		try {
			ArrayList arr = SchedulerDAO.getAllMapRedScheduleID();
			for (int i = 0; i < arr.size(); i++) {
				if (id.equals((String) arr.get(i))) {
					flag = true;
					break;
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("Exception caught while checking ScheduleId already in use or not " + e.getMessage(), e);
		}
		return flag;
	}

	public static ArrayList getTriggerDetails(String jobGroup, String jobName) {
		return SchedulerDAO.getTriggerDetail(jobGroup, jobName);
	}

	public static ArrayList getAllTriggerDetails(String jobGroup, String jobName) {
		return SchedulerDAO.getAllTriggerDetail();
	}

	public static DWRResponse deleteTriggers(ArrayList triggerList) {
		return SchedulerDAO.deleteTriggers(triggerList);
	}

}