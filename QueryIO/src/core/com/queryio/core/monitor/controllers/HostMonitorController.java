package com.queryio.core.monitor.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.remote.SystemStatistics;
import com.queryio.common.service.remote.QueryIOResponse;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StartupParameters;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;
import com.queryio.core.dao.DiskMonitoredDataDAO;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.MonitorDAO;
import com.queryio.core.dao.NotifyDAO;
import com.queryio.core.dao.RuleDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.dao.VolumeDAO;
import com.queryio.core.monitor.alerts.evaluator.AlertEvaluator;
import com.queryio.core.monitor.managers.StatusManager;
import com.queryio.core.notification.NotificationHandler;
import com.queryio.core.notifier.notifiers.NotificationManager;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;

public class HostMonitorController extends Thread {

	/*
	 * /* 1 01h / "Warn-lo", // "Warning-lo", /* 2 02h / "Crit-lo", //
	 * "Critical-lo", /* 3 04h / "BelowCrit", // "BelowCrit-lo", /* 4 08h /
	 * "Warn-hi", // "Warning-hi", /* 5 10h / "Crit-hi", // "Critical-hi", /* 6
	 * 20h / "AboveCrit", // "AboveCrit-hi", /* 7 40h / "Init ", /*in init
	 * state, no reading
	 */
	/*
	 * 8 80h / "OK* ",
	 */

	private String username = null;
	private String password = null;

	private boolean notifiedOk = false;
	private boolean notifiedError = false;

	private HashMap status = null;

	public static final String DISK_HEALTH_PASS = "1";
	public static final String DISK_HEALTH_FAIL = "0";

	private Host host;

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	private boolean collectData;
	ControllerData controllerHistoricalData;
	ControllerData controllerSummaryData;
	float totalBytesReadPerSec = 0;
	float totalBytesWritePerSec = 0;

	public void run() {
		status = new HashMap();
		status.put("AboveCrit", "1");
		status.put("Crit-hi", "2");
		status.put("BelowCrit", "3");
		status.put("Crit-lo", "4");
		status.put("Warn-hi", "5");
		status.put("Warn-lo", "6");
		status.put("OK", "7");

		controllerHistoricalData = new ControllerData();
		controllerSummaryData = new ControllerData();
		String disksHealthStatus = null;
		String oldStatus = "";
		SystemStatistics systemStatistics = null;
		QueryIOResponse response = null;
		Connection connection = null;

		long curTime = 0;
		int failedAttempts = 0;
		notifiedOk = true;
		notifiedError = false;

		final AlertEvaluator evaluator = new AlertEvaluator();
		ArrayList rules = null;

		boolean monitoringStarted = false;

		while (this.collectData) {
			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("Querying agent for system
			// statistics");

			totalBytesReadPerSec = 0;
			totalBytesWritePerSec = 0;

			try {
				QueryIOAgentManager.ping(host);
				setRunning();
			} catch (Exception e) {
				setNotResponding();
			}

			if (!monitoringStarted) {
				try {
					if (QueryIOAgentManager.isWindowsOS(host)) {
						QueryIOAgentManager.startWindowsMonitoring(host, username, password);
					} else {
						QueryIOAgentManager.startMonitoring(host);
					}
					monitoringStarted = true;
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Monitoring started on host");
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Could not start monitoring process on host");
					AppLogger.getLogger().fatal(e.getMessage(), e);
					try {
						Thread.sleep(StartupParameters.getDataFetchIntervalInSeconds() * 1000);
					} catch (InterruptedException e1) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
					continue;
				}
			}

			try {
				connection = CoreDBManager.getQueryIODBConnection();
				// Host Monitoring

				systemStatistics = QueryIOAgentManager.getSystemStatistics(host);

				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("CPU Usage: " +
				// systemStatistics.getCpuUsage());

				disksHealthStatus = systemStatistics.getDiskHealthStatistics();

				DiskInfo[] infos = systemStatistics.getDiskInfo();

				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Disk Info Size: " +
				// infos.length);

				ArrayList localDisks = VolumeDAO.getAllDisksForHosts(connection, this.host.getId());

				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Registered Disks: " +
				// localDisks);

				if (QueryIOAgentManager.isMacOS(host)) {
					for (DiskInfo info : infos) {
						// if(AppLogger.getLogger().isDebugEnabled())
						// AppLogger.getLogger().debug("Fetched disk info name:
						// " + info.getName());
						for (int i = 0; i < localDisks.size(); i++) {
							String diskName = (String) localDisks.get(i);
							DiskMonitoredDataDAO.addMonitoredData(connection, this.host.getId(), diskName,
									info.getReadsPerSec(), info.getWritesPerSec(),
									getDiskStatus(disksHealthStatus, diskName));
							totalBytesReadPerSec += info.getReadsPerSec();
							totalBytesWritePerSec += info.getWritesPerSec();
						}
					}
				} else {
					if (infos != null) {
						for (DiskInfo info : infos) {
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug("Fetched disk info name: " + info.getName());
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug("localDisks.size(): " + localDisks.size());
							for (int i = 0; i < localDisks.size(); i++) {
								String diskName = (String) localDisks.get(i);
								if (AppLogger.getLogger().isDebugEnabled())
									AppLogger.getLogger().debug("DiskName : " + diskName);
								if (diskName.contains(info.getName())) {
									DiskMonitoredDataDAO.addMonitoredData(connection, this.host.getId(), diskName,
											info.getReadsPerSec(), info.getWritesPerSec(),
											getDiskStatus(disksHealthStatus, diskName));
									totalBytesReadPerSec += info.getReadsPerSec();
									totalBytesWritePerSec += info.getWritesPerSec();
								}
							}
						}
					}
				}
				this.controllerHistoricalData.setValue(ColumnConstants.COL_MONITORDATA_MONITOR_TIME.toLowerCase(),
						new Timestamp(System.currentTimeMillis()));
				this.controllerHistoricalData.setValue(ColumnConstants.COL_NW_RECDBYTESPERSEC.toLowerCase(),
						systemStatistics.getRecdPacketsPerSec() * 1024);
				this.controllerHistoricalData.setValue(ColumnConstants.COL_NW_SENTBYTESPERSEC.toLowerCase(),
						systemStatistics.getSentPacketsPerSec() * 1024);
				this.controllerHistoricalData.setValue(ColumnConstants.COL_DSK_BYTESREADPERSEC.toLowerCase(),
						totalBytesReadPerSec * 1024);
				this.controllerHistoricalData.setValue(ColumnConstants.COL_DSK_BYTESWRITEPERSEC.toLowerCase(),
						totalBytesWritePerSec * 1024);
				this.controllerHistoricalData.setValue(ColumnConstants.COL_CPUUSAGE.toLowerCase(),
						systemStatistics.getCpuUsage());

				this.controllerSummaryData.setValue(ColumnConstants.COL_NW_RECDBYTESPERSEC,
						systemStatistics.getRecdPacketsPerSec() * 1024);
				this.controllerSummaryData.setValue(ColumnConstants.COL_NW_SENTBYTESPERSEC,
						systemStatistics.getSentPacketsPerSec() * 1024);
				this.controllerSummaryData.setValue(ColumnConstants.COL_DSK_BYTESREADPERSEC,
						totalBytesReadPerSec * 1024);
				this.controllerSummaryData.setValue(ColumnConstants.COL_DSK_BYTESWRITEPERSEC,
						totalBytesWritePerSec * 1024);
				this.controllerSummaryData.setValue(ColumnConstants.COL_CPUUSAGE, systemStatistics.getCpuUsage());
				this.controllerSummaryData.setValue(ColumnConstants.COL_RAMFREE, systemStatistics.getRamFree());
				this.controllerSummaryData.setValue(ColumnConstants.COL_RAMTOTAL, systemStatistics.getRamTotal());

				// IPMI Montoring starts from here

				Map<String, String[]> temperatureMap = systemStatistics.getIpmiTemperatureMap();
				Map<String, String[]> fanMap = systemStatistics.getIpmiFanMap();
				Map<String, String[]> voltageMap = systemStatistics.getIpmiVoltageMap();

				String temperaturePrefix = "TEMP";
				String fanPrefix = "FAN";
				String voltagePrefix = "VOLTAGE";

				String tableNameMonitor = QueryIOConstants.MONITORDATA_TABLE_PREFIX + this.host.getId()
						+ "_HOST_MONITORDATA";

				String tableNameSummary = QueryIOConstants.MONITORDATA_TABLE_PREFIX + this.host.getId()
						+ "_HOST_SUMMARYDATA";

				if (temperatureMap != null) {
					addSensorValues(this.controllerHistoricalData, connection, temperatureMap, "Temperature",
							this.controllerHistoricalData.getColumnNames(), tableNameMonitor, temperaturePrefix);
					addSensorValues(this.controllerSummaryData, connection, temperatureMap, "Temperature",
							this.controllerSummaryData.getColumnNames(), tableNameSummary, temperaturePrefix);
				}

				if (fanMap != null) {
					addSensorValues(this.controllerHistoricalData, connection, fanMap, "Fan",
							this.controllerHistoricalData.getColumnNames(), tableNameMonitor, fanPrefix);
					addSensorValues(this.controllerSummaryData, connection, fanMap, "Fan",
							this.controllerSummaryData.getColumnNames(), tableNameSummary, fanPrefix);
				}
				if (voltageMap != null) {
					addSensorValues(this.controllerHistoricalData, connection, voltageMap, "Voltage",
							this.controllerHistoricalData.getColumnNames(), tableNameMonitor, voltagePrefix);
					addSensorValues(this.controllerSummaryData, connection, voltageMap, "Voltage",
							this.controllerSummaryData.getColumnNames(), tableNameSummary, voltagePrefix);
				}

				// IPMI Monitoring end

				try {
					MonitorDAO.insertHostMonitorData(connection, host.getId(),
							this.controllerHistoricalData.getColumnNames(), this.controllerHistoricalData.getValues());
					MonitorDAO.updateHostSummaryData(connection, host.getId(),
							this.controllerSummaryData.getColumnNames(), this.controllerSummaryData.getValues());

					StatusManager.addStatus(QueryIOConstants.HOST_RULES_PREFIX + host.getHostIP(),
							System.currentTimeMillis(), QueryIOConstants.NODE_STATUS_OK);

					if (this.collectData)
						handleDiskFailureEvent(disksHealthStatus, oldStatus, host.getHostIP());

					// if(AppLogger.getLogger().isDebugEnabled())
					// AppLogger.getLogger().debug("Evaluating rules for host: "
					// + host.getHostIP());

					rules = RuleDAO.getRules(connection, QueryIOConstants.HOST_RULES_PREFIX + this.host.getHostIP());
					evaluator.setNodeId(QueryIOConstants.HOST_RULES_PREFIX + this.host.getHostIP());
					evaluator.setControllerData(this.controllerHistoricalData);
					evaluator.setTimeStamp(curTime);
					if (rules != null)
						evaluator.serve(rules);
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}

				if (!notifiedOk) {
					failedAttempts = 0;

					String subject = "Monitor Success: " + this.host.getHostIP();
					String message = "Monitoring system " + this.host.getHostIP() + " succeeded.";

					AppLogger.getLogger().info("Monitoring system " + this.host.getHostIP() + " succeeded.");

					notifiedError = false;
					notifiedOk = ControllerManager.sendEmailNotification(subject, message);
				}
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
				failedAttempts++;

				StatusManager.addStatus(QueryIOConstants.HOST_RULES_PREFIX + host.getHostIP(),
						System.currentTimeMillis(), QueryIOConstants.NODE_STATUS_FAILURE);

				if ((failedAttempts > EnvironmentalConstants.getMonitorRetryCount()) && (!notifiedError)) {
					String subject = "Monitor Failure: " + this.host.getHostIP();

					String message = "Monitoring system " + this.host.getHostIP() + " failed with exception: "
							+ e.getMessage();
					message += QueryIOConstants.NEW_LINE;

					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));

					message += errors.toString();

					notifiedOk = false;
					try {
						notifiedError = ControllerManager.sendEmailNotification(subject, message);
					} catch (Exception ex) {
						if (failedAttempts <= 1) {
							AppLogger.getLogger().fatal("Monitoring system " + this.host.getHostIP()
									+ " failed with exception: " + e.getMessage(), e);
							AppLogger.getLogger().fatal("Error sending E-Mail notification: " + ex.getMessage(), ex);
						}
					}
				}
			} finally {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error closing connection, Exception: " + e.getMessage(), e);
				}
			}
			if (this.collectData) {
				try {
					Thread.sleep(StartupParameters.getDataFetchIntervalInSeconds() * 1000);
				} catch (InterruptedException e1) {
					AppLogger.getLogger().fatal(e1.getMessage(), e1);
				}
			}
		}
	}

	private void addSensorValues(ControllerData controllerData, Connection connection, Map<String, String[]> map,
			String groupName, ArrayList<String> columnNames, String tableName, String columnPrefix) {
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();

		while (it.hasNext()) {
			String key = it.next();
			String objectName = key;
			key = columnPrefix + key.replaceAll("[^a-zA-Z0-9]+", "").toUpperCase();
			String[] values = map.get(objectName);
			String keyStatus = key + "STATUS";
			String statusGroupName = groupName + " Status";
			String objectNameStatus = objectName + "Status";
			if (!columnNames.contains(key)) {
				// alter table query goes here.

				String alterTableQuery = "alter table " + tableName + " add column " + key + " FLOAT default 0.0";
				String addStatusColumn = "alter table " + tableName + " add column " + keyStatus
						+ " INTEGER default 'N/A'";

				String insertQuery = "INSERT INTO PUBLIC.HOST_SYSTEM_ATTRIBUTES VALUES ('" + objectName + "', '"
						+ groupName + "' ,'" + key + "', '" + objectName + " ( " + values[2] + " )', 'DOUBLE', '"
						+ groupName + "')";
				String insertStatus = "INSERT INTO PUBLIC.HOST_SYSTEM_ATTRIBUTES VALUES ('" + objectNameStatus + "', '"
						+ statusGroupName + "' ,'" + keyStatus + "', '" + objectNameStatus + " ( " + values[2]
						+ " )', 'INTEGER', '" + statusGroupName + "')";

				Statement stmt = null;
				try {
					stmt = connection.createStatement();
					stmt.execute(alterTableQuery);
					stmt.execute(addStatusColumn);
					stmt.executeUpdate(insertQuery);
					stmt.executeUpdate(insertStatus);
				} catch (SQLException e) {
					AppLogger.getLogger().fatal("Error in alter/insert statement :" + e.getMessage(), e);
				} finally {
					try {
						DatabaseFunctions.closeStatement(stmt);
					} catch (SQLException e) {
						AppLogger.getLogger().fatal("Error closing statement :" + e.getMessage(), e);
					}
				}
			}

			controllerData.setValue(key, values[1]);
			controllerData.setValue(keyStatus, getStatus(values[0]));

		}
	}

	//
	private String getDiskStatus(String diskHealthStatus, String name) {
		if (diskHealthStatus == null) {
			return "N/A";
		}
		String[] arr = diskHealthStatus.split(",");
		for (String str : arr) {
			String[] arr2 = str.split("=");
			if (arr2.length == 2) {
				if (name.equalsIgnoreCase(arr2[0])) {
					return arr2[1];
				}
			}
		}
		return "0";
	}

	public void handleDiskFailureEvent(String diskHealthStatus, String oldStatus, String hostName) {
		ArrayList<String> failureDiskList = new ArrayList<String>();
		String[] diskHealthArray = null;
		if (diskHealthStatus != null && diskHealthStatus.length() > 0) {
			diskHealthArray = diskHealthStatus.split(",");
			for (int i = 0; i < diskHealthArray.length; i++) {
				if (diskHealthArray[i].split("=")[1].equalsIgnoreCase(DISK_HEALTH_FAIL)) {
					failureDiskList.add(diskHealthArray[i].split("=")[0]);
				}
			}
		}

		if (failureDiskList.size() > 0) {
			// Send Mail
			String emailBody = "";

			emailBody += "Hard Disk failure Notification\n\n";
			emailBody += "Number of hard disk which are imminent to failure = " + failureDiskList.size() + "\n\n";
			emailBody += "Hard Disk information\n\n";

			for (int i = 0; i < failureDiskList.size(); i++) {
				emailBody += (i + 1) + failureDiskList.get(i) + "\n";
			}

			if (!oldStatus.equalsIgnoreCase(emailBody)) {

				Connection connection = null;
				try {
					NotificationManager notifMgr = NotificationManager.getInstance();
					notifMgr.setConfigXMLFilePath(EnvironmentalConstants.getWebinfDirectory());
					notifMgr.initializeNotificationManager();
					connection = CoreDBManager.getQueryIODBConnection();
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug(emailBody);
					NotificationHandler.generateEmailNotification(notifMgr,
							"Disk Failure Imminent Notification for host : " + hostName, emailBody,
							NotifyDAO.getNotificationSettings(connection), UserDAO.getUsersDetails(connection), true);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error getting connection, Exception: " + e.getMessage(), e);
				} finally {
					try {
						CoreDBManager.closeConnection(connection);
					} catch (Exception e) {
						AppLogger.getLogger().fatal("Error closing connection, Exception: " + e.getMessage(), e);
					}
				}

				oldStatus = emailBody;
			}
		}
	}

	public void setNotResponding() {
		if (host.getStatus().equals(QueryIOConstants.STATUS_NOT_RESPONDING)) {
			return;
		}

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			host.setStatus(QueryIOConstants.STATUS_NOT_RESPONDING);
			HostDAO.updateStatus(connection, host);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public void setRunning() {
		if (host.getStatus().equals(QueryIOConstants.STATUS_STARTED)) {
			return;
		}

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			host.setStatus(QueryIOConstants.STATUS_STARTED);
			HostDAO.updateStatus(connection, host);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public void startCollectingData() {
		this.collectData = true;
		this.start();
	}

	public void stopCollectingData() {
		this.collectData = false;
		super.interrupt();
		if (host.getStatus().equals(QueryIOConstants.STATUS_STOPPED)) {
			return;
		}
	}

	private int getStatus(String status) {
		if (this.status.containsKey(status))
			return (Integer) this.status.get(status);
		else
			return -1;
	}

	public void setUserName(String userName) {
		this.username = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}