package com.queryio.web.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.hadoop.hdfs.DFSConfigKeys;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.charts.util.UIGraphicsFactory;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfig;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DatabaseConfigParser;
import com.queryio.common.database.DatabaseConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.HsqlServer;
import com.queryio.common.database.PostgresServer;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SecurityHandler;
import com.queryio.common.util.Settings;
import com.queryio.common.util.StartupParameters;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.conf.MigrationManager;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.conf.TagParserConfigManager;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.monitor.controllers.ControllerManager;
import com.queryio.core.permissions.PermissionsManager;
import com.queryio.installcluster.ClusterSetup;
import com.queryio.installcluster.UpgradeCluster;
import com.queryio.scheduler.service.SchedulerDAO;

/**
 * <p>
 * StartupListener class used to initialize and database settings and populate
 * any application-wide drop-downs.
 * <p>
 * Keep in mind that this listener is executed outside of
 * OpenSessionInViewFilter, so if you're using Hibernate you'll have to
 * explicitly initialize all loaded data at the Dao or service level to avoid
 * LazyInitializationException. Hibernate.initialize() works well for doing
 * this.
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class StartupListener implements ServletContextListener {
	public void contextInitialized(final ServletContextEvent event) {
		try {
			final ServletContext context = event.getServletContext();
			UIGraphicsFactory.setUserInterfaceClassLoader(this.getClass().getClassLoader());

			EnvironmentalConstants.setAppHome(context.getRealPath("/"));
			EnvironmentalConstants
					.setJdbcDriverPath(EnvironmentalConstants.getAppHome() + QueryIOConstants.JDBC_JAR_DIR);
			EnvironmentalConstants.setReportsDirectory(context.getRealPath("/") + "Reports");
			EnvironmentalConstants.setSpreadSheetsDirectory(context.getRealPath("/") + "SpreadSheetsHTML");

			EnvironmentalConstants.setDDLFileContainer(EnvironmentalConstants.getAppHome() + "conf" + File.separator);

			File file = new File(EnvironmentalConstants.getReportsDirectory());
			if (!file.exists()) {
				file.mkdirs();
			}
			EnvironmentalConstants.setWebinfDirectory(context.getRealPath("/WEB-INF"));

			AppLogger.initLoggerProperties(EnvironmentalConstants.getAppHome(), "../logs/");
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("HELLO ");
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("AppHome: " + EnvironmentalConstants.getAppHome());

			// -DisDemoVersion=true
			String isDemoVersion = System.getProperty("isDemoVersion", "false");
			EnvironmentalConstants.setDemoVersion(Boolean.parseBoolean(isDemoVersion));
			AppLogger.getLogger().fatal("AppHome Demo Version enabled: " + EnvironmentalConstants.isDemoVersion());

			FileInputStream fis = null;
			try {
				final StringBuffer sbFileName = new StringBuffer(EnvironmentalConstants.getAppHome());
				sbFileName.append("conf/");
				sbFileName.append(QueryIOConstants.QUERYIOPROPERTIES_FILENAME);
				Properties prop = new Properties();
				fis = new FileInputStream(sbFileName.toString());
				prop.load(fis);
				StartupParameters.setDefaultDirRemote(prop.getProperty(QueryIOConstants.DEFAULT_DIR_REMOTE));
				StartupParameters.setHadoopDirLocation(EnvironmentalConstants.getAppHome() + "../../../"
						+ prop.getProperty(QueryIOConstants.HADOOP_DIR_LOCATION));
				StartupParameters
						.setSessionTimeout(Integer.parseInt(prop.getProperty(QueryIOConstants.SESSION_TIMEOUT)));
				StartupParameters.setDataFetchIntervalInSeconds(
						Integer.parseInt(prop.getProperty(QueryIOConstants.DATA_FETCH_INTERVAL)));
				StartupParameters.setQueryIOAgentPort(prop.getProperty(QueryIOConstants.QUERYIOAGENT_PORT));
				EnvironmentalConstants
						.setUseKerberos(Boolean.parseBoolean(prop.getProperty(QueryIOConstants.USE_KERBEROS_KEY)));

				String minThreadCount = prop.getProperty(QueryIOConstants.MIN_THREAD_COUNT_KEY);
				if (minThreadCount != null && !minThreadCount.equals(QueryIOConstants.EMPTY_STRING)) {
					EnvironmentalConstants.setMinThreadCount(Integer.parseInt(minThreadCount));
				}

				String maxThreadCount = prop.getProperty(QueryIOConstants.MAX_THREAD_COUNT_KEY);
				if (maxThreadCount != null && !maxThreadCount.equals(QueryIOConstants.EMPTY_STRING)) {
					EnvironmentalConstants.setMaxThreadCount(Integer.parseInt(maxThreadCount));
				}

				Settings.getThreadPoolExecutor(); // Initializing thread pool
													// executor.

				if (EnvironmentalConstants.isUseKerberos()) {
					String nnUserName = prop.getProperty(DFSConfigKeys.DFS_NAMENODE_USER_NAME_KEY);
					if (nnUserName == null || nnUserName.equals("")) {
						throw new RuntimeException(DFSConfigKeys.DFS_NAMENODE_USER_NAME_KEY + " not set ");
					}

					EnvironmentalConstants.setNnUserName(nnUserName);
				}

				EnvironmentalConstants.setCommomFileTypeNames(
						parseCommonFileTypeNames(prop.getProperty(QueryIOConstants.COMMON_FILE_TYPES)));

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Namenode username: " + EnvironmentalConstants.getNnUserName());
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Use kerberos: " + EnvironmentalConstants.isUseKerberos());

				System.setProperty("sun.security.krb5.debug", prop.getProperty("sun.security.krb5.debug"));
				System.setProperty("java.security.krb5.realm", prop.getProperty("java.security.krb5.realm"));
				System.setProperty("java.security.krb5.kdc", prop.getProperty("java.security.krb5.kdc"));

			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error reading queryio.properties file", e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {

					}
				}
			}

			Map<String, Object> valuesContainer = CoreDBManager.getLastFiredQueryDetails();
			try {
				final StringBuffer sbFileName = new StringBuffer(EnvironmentalConstants.getWebinfDirectory());
				sbFileName.append('/');
				sbFileName.append(QueryIOConstants.DBCONFIG_XML);
				EnvironmentalConstants.setDbConfigFilePath(sbFileName.toString());
				new DatabaseConfigParser().loadDatabaseConfiguration(EnvironmentalConstants.getDbConfigFilePath());
				CoreDBManager.initialize();
				CoreDBManager.validateDatabase(valuesContainer);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error in Initialization of Database Connection", e);
			}

			if (!CoreDBManager.isPerfect()) {
				String dbPort = EnvironmentalConstants.getQueryIODatabaseURL().substring(
						EnvironmentalConstants.getQueryIODatabaseURL().lastIndexOf(":") + 1,
						EnvironmentalConstants.getQueryIODatabaseURL().lastIndexOf("/"));
				String dbName = EnvironmentalConstants.getQueryIODatabaseURL()
						.substring(EnvironmentalConstants.getQueryIODatabaseURL().lastIndexOf("/") + 1);

				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Attempting to start database
				// server");
				AppLogger.getLogger().fatal("Attempting to start database server");

				if (EnvironmentalConstants.getQueryIODatabaseType().equals(DatabaseConstants.DB_HSQL)) {
					new HsqlServer(new String[] { dbName }, dbPort).startServer();
				} else {
					new PostgresServer(dbName).startServer();
				}

				int count = 0;

				while (!CoreDBManager.isPerfect() && (count < 50)) {
					try {
						if (count != 0)
							AppLogger.getLogger()
									.fatal("Database not started yet, waiting for some time to re-try the same. Current try count: "
											+ count);
						// HSQL database
						Thread.sleep(5000);
						count++;
						CoreDBManager.validateDatabase(valuesContainer);
					} catch (Exception e) {
						AppLogger.getLogger().fatal("Error starting database: ", e);
					}
				}
			}

			if (CoreDBManager.isPerfect()) {
				AppLogger.getLogger().fatal("QueryIO Database is running");

				// Check if Custom Tag DB is running
				// If Custom tag DBs are not running and DB type is HSQL then
				// start a HSQL server process for them.
				List<String> dbNames = new ArrayList<String>();
				String port = null;
				for (int i = 0; i < CustomTagDBConfigManager.getAllCustomTagDBConfigList().size(); i++) {
					CustomTagDBConfig config = (CustomTagDBConfig) CustomTagDBConfigManager
							.getAllCustomTagDBConfigList().get(i);
					try {
						CoreDBManager.getCustomTagDBConnectionOnStartup(config.getCustomTagPoolName());
					} catch (Exception e) {
						String dbURL = config.getCustomTagUrl();
						String dbName = dbURL.substring(dbURL.lastIndexOf("/") + 1);
						String dbPort = dbURL.substring(dbURL.lastIndexOf(":") + 1, dbURL.lastIndexOf("/"));
						if (config.getCustomTagDBType().equals(DatabaseConstants.DB_HSQL)) {
							if (port == null || port.equals(dbPort)) {
								dbNames.add(dbName);
								port = dbPort;
							} else {
								AppLogger.getLogger().fatal("Unable to start DB: " + dbName
										+ ". Make sure that all custom tag DBs are configured to run on same port.");
							}
						} else {
							AppLogger.getLogger().fatal("Exception occured while connecting to DB: " + dbName, e);
						}
					}
				}

				Connection connection = null;
				try {

					connection = CoreDBManager.getQueryIODBConnection();

					int dataFetchInterval = QueryIOConstants.DEFAULT_CONTROLLER_DATA_FETCH_INTERVAL;

					try {
						dataFetchInterval = Integer.parseInt(RemoteManager
								.getHadoopConfig(QueryIOConstants.CONTROLLER_DATA_FETCH_INTERVAL_KEY).getValue());
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}

					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger()
								.debug("Controller Data Fetch Interval: " + dataFetchInterval + " seconds.");

					ArrayList hostList = HostDAO.getAllHostDetails(connection);
					ArrayList nodeList = null;
					Host host = null;
					Node node = null;
					for (int i = 0; i < hostList.size(); i++) {
						host = (Host) hostList.get(i);
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Starting Host monitor for host: " + host.getHostIP());

						if (host.isMonitor()) {
							ControllerManager.startHostMonitorController(connection, host);
						}

						nodeList = NodeDAO.getAllNodesForHost(connection, host.getId());

						for (int j = 0; j < nodeList.size(); j++) {
							node = (Node) nodeList.get(j);

							if (!node.getStatus().equals(QueryIOConstants.STATUS_STOPPED)) {
								if (!node.isMonitor()) {
									continue;
								}
								try {
									if (node.getNodeType().startsWith(QueryIOConstants.NAMENODE)) {
										ControllerManager.startNameNodeController(connection, host, node,
												StartupParameters.getDataFetchIntervalInSeconds());
										if (node.getServiceStatus() != null
												&& !node.getServiceStatus().equals(QueryIOConstants.STATUS_STOPPED)) {
											ControllerManager.startQueryIOServiceController(node.getId());
										}
										if (node.getHiveServiceStatus() != null && !node.getHiveServiceStatus()
												.equals(QueryIOConstants.STATUS_STOPPED)) {
											ControllerManager.startHiveServiceController(node.getId());
										}
									} else if (node.getNodeType().equals(QueryIOConstants.DATANODE)) {
										ControllerManager.startDataNodeController(connection, host, node,
												StartupParameters.getDataFetchIntervalInSeconds());
									} else if (node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)) {
										ControllerManager.startResourceManagerController(connection, host, node,
												StartupParameters.getDataFetchIntervalInSeconds());
									} else if (node.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {
										ControllerManager.startNodeManagerController(connection, host, node,
												StartupParameters.getDataFetchIntervalInSeconds());
									}

									if (AppLogger.getLogger().isDebugEnabled())
										AppLogger.getLogger().debug("Monitor started for " + node.getNodeType() + " on "
												+ host.getHostIP());
								} catch (Exception e) {
									AppLogger.getLogger()
											.fatal("Could not start monitor for already running " + node.getNodeType()
													+ " on " + host.getHostIP() + ", Exception: " + e.getMessage(), e);
								}
							}
						}
					}
				} catch (Exception e) {
					AppLogger.getLogger().fatal(
							"Could not start monitors for already running nodes, Exception: " + e.getMessage(), e);
				} finally {
					try {
						CoreDBManager.closeConnection(connection);
					} catch (Exception e) {
						AppLogger.getLogger().fatal("Error closing connection, Exception: " + e.getMessage(), e);
					}

				}

				if (dbNames.size() > 0) {
					try {
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug(
									"Attempting to start CustomTag Databases: " + Arrays.toString(dbNames.toArray()));
						new HsqlServer(dbNames.toArray(new String[dbNames.size()]), port).startServer();
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("CustomTag Databases server started successfully");
					} catch (Exception ex) {
						AppLogger.getLogger().fatal("Error Starting Database Process", ex);
					}
				}

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("EnvironmentalConstants.getQueryIODatabasePoolName(): "
							+ EnvironmentalConstants.getQueryIODatabasePoolName());
				if (EnvironmentalConstants.getQueryIODatabasePoolName().equals(QueryIOConstants.DEFAULT_MONITOR_DB)) {
					try {
						SchedulerDAO.initializeScheduler();
						if (SchedulerDAO.isSchedulerInitialized()) {
							SchedulerDAO.startScheduler();
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug("Scheduler initialized and started ");
						}
					} catch (Exception e) {
						AppLogger.getLogger().fatal("Exception during Scheduler iniialize: " + e.getMessage());
					}
				}

				FileInputStream fisCluster = null;
				FileOutputStream fos = null;
				boolean clear = false;
				StringBuffer sbFileName = null;
				Properties prop = null;
				try {

					sbFileName = new StringBuffer(EnvironmentalConstants.getAppHome());
					sbFileName.append("WEB-INF/");
					sbFileName.append("install_cluster.properties");

					prop = new Properties();
					fisCluster = new FileInputStream(sbFileName.toString());

					prop.load(fisCluster);
					String isSetup = prop.getProperty(QueryIOConstants.CLUSTER_SETUP);
					String qioUsername = "";
					String qioPassword = "";
					boolean userAdded = true;
					try {
						userAdded = Boolean.valueOf(prop.getProperty(QueryIOConstants.CLUSTER_SETUP_USER).trim());
						qioUsername = prop.getProperty(QueryIOConstants.CLUSTER_QIO_USERNAME);
						qioPassword = prop.getProperty(QueryIOConstants.CLUSTER_QIO_PASSWORD);
						String qioFname = prop.getProperty(QueryIOConstants.CLUSTER_QIO_FNAME);
						String qioLname = prop.getProperty(QueryIOConstants.CLUSTER_QIO_LNAME);
						String qioEmail = prop.getProperty(QueryIOConstants.CLUSTER_QIO_EMAIL);
						if (!userAdded) {

							AppLogger.getLogger().fatal("Registering new User...");
							if (new ClusterSetup().addUser(qioUsername, qioPassword, qioFname, qioLname, qioEmail)) {
								userAdded = true;
								prop.setProperty(QueryIOConstants.CLUSTER_SETUP_USER, "true");
								AppLogger.getLogger().fatal("User added successfully");
							} else {
								AppLogger.getLogger().fatal("Failed to add user.");
							}

						}
					} catch (Exception e) {

					}

					if (isSetup.equalsIgnoreCase("true") && !clear) {
						boolean isSetupHost = Boolean
								.valueOf(prop.getProperty(QueryIOConstants.CLUSTER_SETUP_HOST).trim());
						boolean isSetupNN = Boolean.valueOf(prop.getProperty(QueryIOConstants.CLUSTER_SETUP_NN).trim());
						boolean isSetupDN = Boolean.valueOf(prop.getProperty(QueryIOConstants.CLUSTER_SETUP_DN).trim());
						boolean isSetupRM = Boolean.valueOf(prop.getProperty(QueryIOConstants.CLUSTER_SETUP_RM).trim());
						boolean isSetupNM = Boolean.valueOf(prop.getProperty(QueryIOConstants.CLUSTER_SETUP_NM).trim());
						boolean isLocal = Boolean.valueOf(prop.getProperty(QueryIOConstants.CLUSTER_SETUP_LOC).trim());
						String installUserHome = prop.getProperty(QueryIOConstants.CLUSTER_SETUP_USERHOME);
						String installJavaHome = prop.getProperty(QueryIOConstants.CLUSTER_SETUP_JAVAHOME);

						if (isSetupNN && isSetupDN && isSetupRM && isSetupNM && userAdded) {
							clear = true;
						} else {

							String ip = prop.getProperty(QueryIOConstants.CLUSTER_IP);
							String ipUsername = prop.getProperty(QueryIOConstants.CLUSTER_USERNAME);
							String ipPassword = prop.getProperty(QueryIOConstants.CLUSTER_PASSWORD);
							String ipPrivateKey = prop.getProperty(QueryIOConstants.CLUSTER_PRIVATE_KEY);
							String ipSshPort = prop.getProperty(QueryIOConstants.CLUSTER_SSH_PORT);

							if (!isLocal) {
								ipPassword = (ipPassword.equals("null") || ipPassword.equals("")) ? null
										: SecurityHandler.decryptData(ipPassword);
								ipPrivateKey = (ipPrivateKey.equals("null") || ipPrivateKey.equals("")) ? null
										: SecurityHandler.decryptData(ipPrivateKey);
							}

							if (!isSetupHost) {
								HashMap<String, Boolean> flag = new ClusterSetup().installNodes(ip, ipUsername,
										ipPassword, qioUsername, qioPassword, isLocal, installUserHome, installJavaHome,
										ipPrivateKey, ipSshPort);
								Iterator iterator = flag.keySet().iterator();
								boolean success = true;
								while (iterator.hasNext()) {
									String key = (String) iterator.next();
									// System.out.println("Flag map key : " +
									// key + " : value : " + flag.get(key));
									if (!flag.get(key)) {
										success = false;
									}
								}
								if (success && userAdded) {
									clear = true;
								} else {
									if (flag.get(QueryIOConstants.HOST))
										prop.setProperty(QueryIOConstants.CLUSTER_SETUP_HOST, "true");
									if (flag.get(QueryIOConstants.NAMENODE))
										prop.setProperty(QueryIOConstants.CLUSTER_SETUP_NN, "true");
									if (flag.get(QueryIOConstants.DATANODE))
										prop.setProperty(QueryIOConstants.CLUSTER_SETUP_DN, "true");
									if (flag.get(QueryIOConstants.RESOURCEMANAGER))
										prop.setProperty(QueryIOConstants.CLUSTER_SETUP_RM, "true");
									if (flag.get(QueryIOConstants.NODEMANAGER))
										prop.setProperty(QueryIOConstants.CLUSTER_SETUP_NM, "true");
								}
							} else {
								if (!isSetupNN) {
									if (new ClusterSetup().installNameNode(ip, isLocal, installUserHome))
										prop.setProperty(QueryIOConstants.CLUSTER_SETUP_NN, "true");
								}
								if (!isSetupDN) {
									if (new ClusterSetup().installDataNode(ip, isLocal, installUserHome))
										prop.setProperty(QueryIOConstants.CLUSTER_SETUP_DN, "true");
								}
								if (!isSetupRM) {
									if (new ClusterSetup().installRM(ip, isLocal, installUserHome))
										prop.setProperty(QueryIOConstants.CLUSTER_SETUP_RM, "true");
								}
								if (!isSetupNM) {
									if (new ClusterSetup().installNM(ip, isLocal, installUserHome))
										prop.setProperty(QueryIOConstants.CLUSTER_SETUP_NM, "true");
								}
							}
						}
					}

				} catch (Exception e) {
					AppLogger.getLogger().fatal("Failed to setup cluster on startup " + e.getMessage(), e);
				} finally {
					if (clear) {
						fos = new FileOutputStream(sbFileName.toString());
						String temp = QueryIOConstants.CLUSTER_SETUP + "=false";
						byte[] contentInBytes = temp.getBytes();
						fos.write(contentInBytes);
						fos.flush();
						fos.close();
					} else {
						fos = new FileOutputStream(sbFileName.toString());
						prop.store(fos, "");
					}
					if (fisCluster != null) {
						try {
							fisCluster.close();
						} catch (Exception e) {

						}
					}
					if (fos != null) {
						try {
							fos.close();
						} catch (Exception e) {

						}
					}
				}

				try {
					sbFileName = new StringBuffer(System.getProperty("user.home"));
					sbFileName.append(File.separator + QueryIOConstants.METADATA_FILE);
					prop = new java.util.Properties();
					prop = new Properties();
					fisCluster = new FileInputStream(sbFileName.toString());
					prop.load(fisCluster);
					boolean isUpgrade = Boolean.valueOf(prop.getProperty(QueryIOConstants.ISUPGRADE).trim());

					if (isUpgrade) {
						Connection conn = null;
						try {
							conn = CoreDBManager.getQueryIODBConnection();
							addColumns(conn);
							String installDir = prop.getProperty(QueryIOConstants.QUERYIO_INSTALL_LOC);
							if (new UpgradeCluster().startUpgrade(installDir)) {
								prop.setProperty(QueryIOConstants.ISUPGRADE, "false");
							}
						} finally {
							if (conn != null) {
								CoreDBManager.closeConnection(conn);
							}
						}
					}
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Failed to setup cluster during upgrade " + e.getMessage(), e);
				} finally {
					fos = new FileOutputStream(sbFileName.toString());
					prop.store(fos, "");
					if (fisCluster != null) {
						try {
							fisCluster.close();
						} catch (Exception e) {

						}
					}
					if (fos != null) {
						try {
							fos.close();
						} catch (Exception e) {

						}
					}
				}

				// try
				// {
				// // Generate System Scheduler for Billing
				//
				// ArrayList schedulesList = ScheduleManager.getAllSchedules();
				//
				// if ((schedulesList == null) || (schedulesList.size() < 1))
				// {
				// Date end = new Date(System.currentTimeMillis() + (60000 *
				// 10));
				// String endDate = String.valueOf(end.getMonth() +
				// 1)+"/"+String.valueOf(end.getDate())+"/"+String.valueOf(end.getYear()
				// + 1900)+"
				// "+String.valueOf(end.getHours())+":"+String.valueOf(end.getMinutes())+":"+String.valueOf(end.getSeconds());
				//
				//// String scheduledReportTime = "7/10/2012 01:40:00";
				// String scheduledReportTime = endDate;
				//
				// String interval = "" + SchedulerConstants.SCH_FREQUENCY_ONCE+
				// "";
				//
				// ArrayList exportList = new ArrayList();
				// exportList.add(ExportConstants.EXPORT_TYPE_PDF);
				//
				// ArrayList userList = new ArrayList();
				// userList.add("1");
				//
				// ArrayList reportIdList = new ArrayList();
				// reportIdList.add(QueryIOConstants.BILLING_REPORT_SYSTEM);
				//
				// boolean isDefaultScheduler =
				// ScheduleManager.scheduleJob(interval, scheduledReportTime,
				// exportList, SchedulerConstants.NOTIFICATION_EMAIL, "Billing
				// Invoice", userList, reportIdList, null, "Billing_Default_1");
				//
				// if (isDefaultScheduler)
				// System.out.println("INFO: Billing Invoice Scheduler
				// initialization successful.");
				// else
				// System.out.println("INFO: Billing Invoice Scheduler
				// initialization failed.");
				// }
				// }
				// catch (Exception e)
				// {
				// AppLogger.getLogger().fatal("Could not schedule the system
				// generated Billing Report, Exception: " + e.getMessage(), e);
				// System.out.println("INFO: Billing Invoice Scheduler
				// initialization failed. Check logs for Detail.");
				// }

				TagParserConfigManager.init();

				if (EnvironmentalConstants.isDemoVersion()) {
					PermissionsManager.addGroup(QueryIOConstants.DEFAULT_GROUP_NAME_DEMO);
				}

			} else {
				try {
					CoreDBManager.validateDatabase(valuesContainer);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error validating the required databases.", e);
					throw new RuntimeException("QueryIO database failed to start.", e);
				}
			}

		} catch (Throwable th) {
			th.printStackTrace();
			throw new RuntimeException("QueryIO server startup failed", th);
		}
	}

	private void addColumns(Connection connection) {
		String query = "ALTER TABLE " + TableConstants.TABLE_HOSTS + " ADD COLUMN "
				+ ColumnConstants.COL_HOST_IS_WINDOWS + " BOOLEAN DEFAULT FALSE";
		excuteDDLQuery(connection, query);
		query = "ALTER TABLE " + TableConstants.TABLE_HIVETABLE + " ADD COLUMN "
				+ ColumnConstants.COL_HIVETABLES_FILE_NAME + " VARCHAR(2550)";
		excuteDDLQuery(connection, query);
	}

	private void excuteDDLQuery(Connection connection, String query) {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.execute(query);
		} catch (SQLException e) {
			AppLogger.getLogger().fatal("Error adding column.", e);
		} finally {
			if (stmt != null) {
				try {
					DatabaseFunctions.closeStatement(stmt);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private ArrayList<String> parseCommonFileTypeNames(String commaSeparatedString) {
		ArrayList<String> list = new ArrayList<String>();
		String[] fileList = commaSeparatedString.split(",");
		for (String fileName : fileList) {
			list.add(fileName);
		}
		Collections.sort(list);
		return list;
	}

	public void contextDestroyed(final ServletContextEvent event) {
		MigrationManager.stopAllMigrationThreads();
		ControllerManager.stopAllControllers();
		if (SchedulerDAO.isSchedulerStarted()) {
			SchedulerDAO.shutdownScheduler();
		}

		Settings.getThreadPoolExecutor().shutdownNow();

		try {
			String dbName = EnvironmentalConstants.getQueryIODatabaseURL()
					.substring(EnvironmentalConstants.getQueryIODatabaseURL().lastIndexOf("/") + 1);

			if (EnvironmentalConstants.getQueryIODatabaseType().equals(DatabaseConstants.DB_HSQL)) {
				HsqlServer.stopServer(CoreDBManager.getQueryIODBConnection());
				CoreDBManager.stopAllCustomTagDBs();
			} else {
				new PostgresServer(dbName).stopDatabaseServers();
			}

		} catch (Exception e) {
			// e.printStackTrace();
		}
	}
}
