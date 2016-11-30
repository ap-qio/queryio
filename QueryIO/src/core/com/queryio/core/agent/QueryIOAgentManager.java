package com.queryio.core.agent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import com.jcraft.jsch.Session;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.remote.SystemStatistics;
import com.queryio.common.service.remote.QueryIOResponse;
import com.queryio.common.service.remote.QueryIOService;
import com.queryio.common.service.remote.RemoteService;
import com.queryio.common.service.remote.ServiceFactory;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SSHRemoteExecution;
import com.queryio.common.util.StartupParameters;
import com.queryio.common.util.ZipUtil;
import com.queryio.core.adhoc.AdHocHiveClient;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.HadoopConfig;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.MapRedJobConfig;
import com.queryio.core.bean.Node;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.conf.OneTimeConfig;
import com.queryio.core.conf.TagParserConfigManager;
import com.queryio.core.dao.HadoopConfigDAO;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;

public class QueryIOAgentManager {

	public static void startQueryIOAgent(final Host host, String userName, String password, String sshPrivateKeyFile,
			String port, boolean isLocal) throws Exception {

		if (isLocal) {
			try {
				final String cmd;
				if (SystemUtils.IS_OS_WINDOWS) {
					cmd = "cmd.exe /C cd " + host.getInstallDirPath() + QueryIOConstants.QUERYIOAGENT_DIR_NAME
							+ File.separator + "bin" + " && start " + host.getInstallDirPath()
							+ QueryIOConstants.QUERYIOAGENT_DIR_NAME + File.separator + "bin" + File.separator
							+ "startQIOAgent.bat " + host.getInstallDirPath() + QueryIOConstants.QUERYIOAGENT_DIR_NAME
							+ " " + host.getAgentPort();
				} else {
					cmd = "nohup /bin/sh " + host.getInstallDirPath() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/bin/"
							+ "start_queryIOAgent.sh " + host.getInstallDirPath()
							+ QueryIOConstants.QUERYIOAGENT_DIR_NAME + " " + host.getAgentPort() + " >> "
							+ host.getInstallDirPath() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/webapps/"
							+ QueryIOConstants.AGENT_QUERYIO + "/AgentStatus.log 2>&1 &";
				}
				AppLogger.getLogger().debug("cmd to start QIOAgent : " + cmd);
				// // FIXME : Temporary solution for windows.. uncomment lines
				// below later.
				// new Thread() {
				// public void run() {
				// try {
				// Runtime.getRuntime().exec(cmd, null, new
				// File(host.getInstallDirPath()
				// + QueryIOConstants.QUERYIOAGENT_DIR_NAME
				// + File.separator + "bin"));
				// } catch (Exception e) {
				// AppLogger.getLogger().fatal(e);
				// }
				// };
				// }.start();
				File destPath = new File(
						host.getInstallDirPath() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + File.separator + "bin");

				Runtime.getRuntime().exec(cmd, null, destPath);
				// String cmd = "./startQIOAgent.sh " + host.getInstallDirPath()
				// + QueryIOConstants.QUERYIOAGENT_DIR_NAME + " " +
				// host.getAgentPort();
				//
				// process = Runtime.getRuntime().exec(cmd, null, destPath);

			} catch (final Exception ex) {
				AppLogger.getLogger().fatal(ex.getMessage(), ex);
			}
		} else {
			final SSHRemoteExecution remoteExec;
			final Session session;
			try {
				remoteExec = new SSHRemoteExecution();

				if (password != null) {
					session = remoteExec.createSession(host.getHostIP(), userName, password, 22);
				} else if (sshPrivateKeyFile != null) {
					session = remoteExec.createSessionWithPrivateKeyFile(host.getHostIP(), userName, sshPrivateKeyFile,
							Integer.parseInt(port));
				} else {
					throw new Exception("Session could not be created");
				}
				final String cmd;
				if (session != null) {
					session.connect(StartupParameters.getSessionTimeout());

					if (host.isWindows()) {
						cmd = "cd " + host.getInstallDirPath() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "\\bin"
								+ " & " + "start \"\" startQIOAgent.bat " + host.getInstallDirPath()
								+ QueryIOConstants.QUERYIOAGENT_DIR_NAME + " " + host.getAgentPort();

					} else {
						cmd = "cd " + host.getInstallDirPath() + QueryIOConstants.QUERYIOAGENT_DIR_NAME
								+ "/bin; nohup /bin/sh start_queryIOAgent.sh " + host.getInstallDirPath()
								+ QueryIOConstants.QUERYIOAGENT_DIR_NAME + " " + host.getAgentPort() + " >> "
								+ host.getInstallDirPath() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/webapps/"
								+ QueryIOConstants.AGENT_QUERYIO + "/AgentStatus.log 2>&1 &";
					}
					// FIXME : Temporary solution for windows.. uncomment lines
					// below later.
					new Thread() {
						public void run() {
							try {
								if (AppLogger.getLogger().isDebugEnabled())
									AppLogger.getLogger().debug("cmd : " + cmd);
								remoteExec.executeCommand(session, cmd);
								if (AppLogger.getLogger().isDebugEnabled())
									AppLogger.getLogger().debug(remoteExec.getOutput());
							} catch (Exception e) {
								AppLogger.getLogger().fatal(e);
							}
						};
					}.start();
					// remoteExec.executeCommand(session, cmd); //FIXME :
					// Uncomment lines below later.
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug(remoteExec.getOutput());
				}
			} finally {
				// FIXME : Temporary solution for windows.. uncomment lines
				// below later.
				// if (remoteExec != null)
				// remoteExec.closeSession(session);
			}
		}
	}

	public static void stopQueryIOAgent(Host host, String userName, String password, String sshPrivateKeyFile,
			String port, boolean isLocal) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Stopping host");

		if (isLocal) {
			try {
				String cmd = "";
				File destPath = new File(
						host.getInstallDirPath() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + File.separator + "bin");
				if (host.isWindows()) {
					String pid = null;

					pid = OneTimeConfig.getProcessIDWindows(QueryIOConstants.QUERYIOAGENT_DIR_NAME);

					if (pid != null) {
						String[] pidSplit = null;
						pidSplit = pid.split(" ");
						for (String processID : pidSplit) {
							cmd += "taskkill /F /PID " + processID + " & ";
						}
					}
				} else {
					cmd = "sh " + host.getInstallDirPath() + "/bin/stop_agent.sh ";
				}

				Runtime.getRuntime().exec(cmd, null, destPath);

			} catch (final Exception ex) {
				AppLogger.getLogger().fatal(ex.getMessage(), ex);
			}
		} else {
			String cmd = QueryIOConstants.EMPTY_STRING;
			SSHRemoteExecution remoteExec = null;
			Session session = null;
			try {
				remoteExec = new SSHRemoteExecution();
				if (password != null) {
					session = remoteExec.createSession(host.getHostIP(), userName, password, Integer.parseInt(port));
				} else if (sshPrivateKeyFile != null) {
					session = remoteExec.createSessionWithPrivateKeyFile(host.getHostIP(), userName, sshPrivateKeyFile,
							Integer.parseInt(port));
				} else {
					throw new Exception("Session could not be created");
				}
				if (session != null) {
					session.connect(StartupParameters.getSessionTimeout());
					if (host.isWindows()) {
						String pid = null;
						if (password != null) {
							pid = OneTimeConfig.getRemotePID(host, userName, password,
									QueryIOConstants.QUERYIOAGENT_DIR_NAME, port);
						} else {
							pid = OneTimeConfig.getRemotePIDusingPrivateKey(host, userName, sshPrivateKeyFile,
									QueryIOConstants.QUERYIOAGENT_DIR_NAME, port);
						}
						if (pid != null) {
							String[] pidSplit = null;
							pidSplit = pid.split(" ");
							for (String processID : pidSplit) {
								cmd += "taskkill /F /PID " + processID + " & ";
							}
						}
					} else {
						cmd += "crontab -r; ";
						String pid = null;
						if (password != null) {
							pid = OneTimeConfig.getRemotePID(host, userName, password,
									QueryIOConstants.QUERYIOAGENT_DIR_NAME, port);
						} else {
							pid = OneTimeConfig.getRemotePIDusingPrivateKey(host, userName, sshPrivateKeyFile,
									QueryIOConstants.QUERYIOAGENT_DIR_NAME, port);
						}
						if (pid != null) {
							cmd += "kill -9 " + pid + "; ";
						}
					}
					remoteExec.executeCommand(session, cmd);
				}
			} finally {
				if (remoteExec != null)
					remoteExec.closeSession(session);
			}
		}
	}

	public static QueryIOResponse test(Host host) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.test();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static void ping(Host host) throws Exception {
		QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0, host.getHostIP(),
				Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
				RemoteService.QUERYIO_SERVICE_BEAN);
		remoteService.test();
	}

	public static String getUserHome(Host host) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.getUserHome();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
		}
		return null;
	}

	public static void startNode(Host host, Node node, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			QueryIOResponse response = remoteService.startNode(host.getInstallDirPath(), node.getNodeType(),
					node.getId());
			dwrResponse.setResponseMessage(response.getResponseMsg());
			dwrResponse.setTaskSuccess(response.isSuccessful());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseMessage(e.getMessage());
		}
	}

	public static void startQueryIOServices(Host host, Node node, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			QueryIOResponse response = remoteService.startQueryIOServices(host.getInstallDirPath(), node.getId());
			dwrResponse.setResponseMessage(response.getResponseMsg());
			dwrResponse.setTaskSuccess(response.isSuccessful());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseMessage(e.getMessage());
		}
	}

	public static void stopQueryIOServices(Host host, Node node, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			QueryIOResponse response = remoteService.stopQueryIOServices(host.getInstallDirPath(), node.getId());
			dwrResponse.setResponseMessage(response.getResponseMsg());
			dwrResponse.setTaskSuccess(response.isSuccessful());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseMessage(e.getMessage());
		}
	}

	public static int getOS3ServerPort(Host host, Node node) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.getOS3ServerPort(host.getInstallDirPath(), node.getId());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
		}
		return -1;
	}

	public static int getHDFSOverFTPServerPort(Host host, Node node) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.getHDFSOverFTPServerPort(host.getInstallDirPath(), node.getId());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
		}
		return -1;
	}

	public static void stopNode(Host host, Node node, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			QueryIOResponse response = remoteService.stopNode(host.getInstallDirPath(), node.getNodeType(),
					node.getId());
			dwrResponse.setResponseMessage(response.getResponseMsg());
			dwrResponse.setTaskSuccess(response.isSuccessful());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseMessage(e.getMessage());
		}
	}

	public static QueryIOResponse performBootstrapStandby(Host host, Node node) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.performBootstrapStandby(host.getInstallDirPath(), node.getId());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse initializeSharedEdits(Host host, Node node) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.initializeSharedEdits(host.getInstallDirPath(), node.getId());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse transitionNodeToActive(Host host, Node node, String activeNode) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.transitionNodeToActive(host.getInstallDirPath(), node.getId(), activeNode);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static void formatDirectory(Host host, String dirPath, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			QueryIOResponse response = remoteService.formatDirectory(host.getInstallDirPath(), dirPath);
			dwrResponse.setResponseMessage(response.getResponseMsg());
			dwrResponse.setTaskSuccess(response.isSuccessful());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
		}
	}

	public static void formatNamenode(Host host, Node node, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);

			QueryIOResponse response = remoteService.formatNamenode(host.getInstallDirPath(), node.getId());
			dwrResponse.setResponseMessage(response.getResponseMsg());
			dwrResponse.setTaskSuccess(response.isSuccessful());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
		}
	}

	public static void setNamenodeDefaultConfiguration(Node node, Host namenodeHost, List keys, List values,
			String dirPath, String servicePort, String httpPort, String httpsPort, String jmxPort,
			String os3ServicesPort, String secureOs3ServerPort, String hdfsoverftpServerPort, String ftpserverPort,
			String secureFtpserverPort, String connectionName, String analyticsDbName, String disk,
			DWRResponse dwrResponse) {

		replacePathSeparatorForWindows(namenodeHost);
		if (namenodeHost.isWindows()) {
			dirPath = "file:///" + dirPath.replaceAll("\\\\", "/");
		}
		Connection connection = null;
		ArrayList coreSiteKeyList = new ArrayList();
		ArrayList coreSiteValueList = new ArrayList();
		ArrayList hdfsSiteKeyList = new ArrayList();
		ArrayList hdfsSiteValueList = new ArrayList();
		String hdfsUri = "";
		String keyStorePath = namenodeHost.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/keystore";
		HadoopConfig config;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			HashMap configs = HadoopConfigDAO.getAllNameNodeHadoopConfigs(connection);

			Iterator i = configs.keySet().iterator();
			String key;
			while (i.hasNext()) {
				key = (String) i.next();
				config = (HadoopConfig) configs.get(key);
				if (!key.startsWith("dfs")) {
					if (key.equals(DFSConfigKeys.FS_DEFAULT_NAME_KEY)) {
						hdfsUri = "hdfs://" + namenodeHost.getHostIP() + ":" + servicePort;
						config.setValue(hdfsUri);
					} else if (key.equals(DFSConfigKeys.NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY)) {
						config.setValue(namenodeHost.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/etc/"
								+ QueryIOConstants.NAMENODE + "-conf_" + node.getId() + "/topologyConfig.sh");
					} else if (key.equals(QueryIOConstants.NAMENODE_OPTS_KEY)) {
						String[] args = config.getValue().split(" ");
						String val = "";

						for (int var = 0; var < args.length; var++) {
							if (var != 0)
								val += " ";
							if (args[var].startsWith("-Dcom.sun.management.jmxremote.port=")) {
								val += "-Dcom.sun.management.jmxremote.port=" + jmxPort;
							} else if (namenodeHost.isWindows() && args[var].startsWith("$HADOOP_NAMENODE_OPTS")) {
								val += args[var].replaceAll("\\$", "%") + "%";
							} else {
								val += args[var];
							}
						}
						config.setValue(val);
					} else if (key.equals(QueryIOConstants.HADOOP_LOG_DIR_KEY)) {
						config.setValue(namenodeHost.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/logs/"
								+ QueryIOConstants.NAMENODE + "_" + node.getId());
					} else if (key.equals(QueryIOConstants.QUERYIO_OS3SERVER_PORT)) {
						config.setValue(os3ServicesPort);
					} else if (key.equals(QueryIOConstants.QUERYIO_NAMENODE_DATA_DISK)) {
						config.setValue(disk);
					} else if (key.equals(QueryIOConstants.QUERYIO_OS3SERVER_SECUREPORT)) {
						config.setValue(secureOs3ServerPort);
					} else if (key.equals(QueryIOConstants.QUERYIO_HDFSOVERFTP_PORT)) {
						config.setValue(hdfsoverftpServerPort);
					} else if (key.equals(QueryIOConstants.QUERYIO_FTPSERVER_PORT)) {
						config.setValue(ftpserverPort);
					} else if (key.equals(QueryIOConstants.QUERYIO_FTPSERVER_SSLPORT)) {
						config.setValue(secureFtpserverPort);
					} else if (key.equals(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID)) {
						if (connectionName != null)
							config.setValue(connectionName);
					} else if (key.equals(QueryIOConstants.ANALYTICS_DB_DBSOURCEID)) {
						if (analyticsDbName != null)
							config.setValue(analyticsDbName);
					} else if (key.equals(QueryIOConstants.HIVE_QUERYIO_MAPREDUCE_FRAMEWORK_NAME_KEY)) {
						config.setValue("local");
					} else if (key.equals(QueryIOConstants.HIVE_QUERYIO_WAREHOUSE_DIR)) {
						String warehouseDir = "hdfs://" + namenodeHost.getHostIP() + ":" + servicePort
								+ AdHocHiveClient.warehouseDir;
						config.setValue(warehouseDir);
					} else if (key.equals(QueryIOConstants.HIVE_QUERYIO_CONNECTION_URL)) {
						String hiveUrl = "jdbc:hive2://" + namenodeHost.getHostIP() + ":10000/default";
						config.setValue(hiveUrl);
					} else if (key.equals(QueryIOConstants.QUERYIO_FTPSERVER_SSLKEYSTORE)) {
						config.setValue(keyStorePath);
					} else if (key.equals(QueryIOConstants.CUSTOM_TAG_DB_DBCONFIGPATH)) {
						config.setValue(namenodeHost.getInstallDirPath() + "/" + QueryIOConstants.QUERYIOAGENT_DIR_NAME
								+ "/webapps/" + QueryIOConstants.AGENT_QUERYIO + "/conf/dbconfig.xml");
					} else if (key.equals(QueryIOConstants.HADOOP_PID_DIR_KEY)) {
						config.setValue(namenodeHost.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/pid/"
								+ QueryIOConstants.NAMENODE + "_" + node.getId());
					}
					coreSiteKeyList.add(key);
					coreSiteValueList.add(config.getValue());
				} else {
					if (key.equals(DFSConfigKeys.DFS_NAMESERVICES)) {
						continue;
					} else if (key.equals(DFSConfigKeys.DFS_NAMESERVICE_ID)) {
						config.setValue(node.getId());
					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY)) {
						continue;
					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY)) {
						continue;
					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY)) {
						continue;
					} else if (key.equals(DFSConfigKeys.DFS_HA_NAMENODES_KEY_PREFIX)) {
						continue;
					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY)) {
						continue;
					} else if (key.endsWith("address")) {
						config.setValue(namenodeHost.getHostIP() + ":" + config.getValue().split(":")[1]);
					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY)) {
						config.setValue(dirPath);
					} else if (key.equals(DFSConfigKeys.DFS_HOSTS)) {
						config.setValue(namenodeHost.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/etc/"
								+ QueryIOConstants.NAMENODE + "-conf_" + node.getId() + "/hosts");
					} else if (key.equals(DFSConfigKeys.DFS_HOSTS_EXCLUDE)) {
						config.setValue(namenodeHost.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/etc/"
								+ QueryIOConstants.NAMENODE + "-conf_" + node.getId() + "/hosts-exclude");
					}
					hdfsSiteKeyList.add(config.getKey());
					hdfsSiteValueList.add(config.getValue());
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
		} finally {
			undoPathSeparatorForWindows(namenodeHost);
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			if (!key.startsWith("dfs")) {
				coreSiteKeyList.add(key);
				coreSiteValueList.add((String) values.get(i));
			} else {
				hdfsSiteKeyList.add(key);
				hdfsSiteValueList.add((String) values.get(i));
			}
		}
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					namenodeHost.getHostIP(), Integer.parseInt(namenodeHost.getAgentPort()),
					QueryIOConstants.AGENT_QUERYIO, RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.clearConfiguration(namenodeHost.getInstallDirPath(), QueryIOConstants.NAMENODE, node.getId());

			remoteService.updateConfiguration(namenodeHost.getInstallDirPath(), QueryIOConstants.NAMENODE, node.getId(),
					"core-site.xml", coreSiteKeyList, coreSiteValueList, true);

			remoteService.updateConfiguration(namenodeHost.getInstallDirPath(), QueryIOConstants.NAMENODE, node.getId(),
					"hdfs-site.xml", hdfsSiteKeyList, hdfsSiteValueList, true);

			ArrayList sslKeys = new ArrayList();
			sslKeys.add("ssl.server.keystore.location");
			sslKeys.add("ssl.server.keystore.password");
			sslKeys.add("ssl.server.keystore.keypassword");
			ArrayList sslValues = new ArrayList();
			sslValues.add(keyStorePath);
			sslValues.add(QueryIOConstants.SSL_DEFAULT_PASSWORD);
			sslValues.add(QueryIOConstants.SSL_DEFAULT_PASSWORD);
			remoteService.updateConfiguration(namenodeHost.getInstallDirPath(), QueryIOConstants.NAMENODE, node.getId(),
					"ssl-server.xml", sslKeys, sslValues, true);

			sslKeys = new ArrayList();
			sslKeys.add("ssl.client.keystore.location");
			sslKeys.add("ssl.client.keystore.password");
			sslKeys.add("ssl.client.keystore.keypassword");
			sslValues = new ArrayList();
			sslValues.add(keyStorePath);
			sslValues.add(QueryIOConstants.SSL_DEFAULT_PASSWORD);
			sslValues.add(QueryIOConstants.SSL_DEFAULT_PASSWORD);
			remoteService.updateConfiguration(namenodeHost.getInstallDirPath(), QueryIOConstants.NAMENODE, node.getId(),
					"ssl-client.xml", sslKeys, sslValues, true);

			dwrResponse.setResponseMessage("configured successfully");
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseCode(200);
			// return new QueryIOResponse(true, "configured successfully");
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + namenodeHost.getHostIP() + " not responding", e);

			dwrResponse.setResponseMessage("Host" + namenodeHost.getHostIP() + " not responding. " + e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
			// return new QueryIOResponse(false, e.getMessage());
		}
	}

	private static void replacePathSeparatorForWindows(Host host) {
		if (host.isWindows()) {
			host.setInstallDirPath(host.getInstallDirPath().replaceAll("\\\\", "/")); // Windows
																						// handling
																						// as
																						// '\'
																						// is
																						// considered
																						// as
																						// malformed
																						// in
																						// java.net.URI.
																						// It
																						// shows
																						// warning
																						// message
																						// and
																						// error
																						// trace
																						// when
																						// Hadoop
																						// command
																						// is
																						// executed.
																						// We
																						// consider
																						// command
																						// failure
																						// if
																						// 'Error'
																						// /
																						// 'Exception'
																						// word
																						// is
																						// present
																						// in
																						// output.
		}
	}

	private static void undoPathSeparatorForWindows(Host host) {
		if (host.isWindows()) {
			host.setInstallDirPath(host.getInstallDirPath().replaceAll("/", "\\\\"));
		}
	}

	public static void setStandByNamenodeDefaultConfiguration(String namenodeId, Node node, Host host, ArrayList keys,
			ArrayList values, String dirPath, String servicePort, String httpsPort, String jmxPort,
			String os3ServicesPort, String secureOs3ServerPort, String hdfsoverftpServerPort, String ftpserverPort,
			String secureFtpserverPort, String disk, DWRResponse dwrResponse) {

		Connection connection = null;
		ArrayList coreSiteKeyList = new ArrayList();
		ArrayList coreSiteValueList = new ArrayList();
		ArrayList hdfsSiteKeyList = new ArrayList();
		ArrayList hdfsSiteValueList = new ArrayList();
		String hdfsUri = "";

		replacePathSeparatorForWindows(host);

		HadoopConfig config;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			HashMap configs = HadoopConfigDAO.getAllNameNodeHadoopConfigs(connection);

			Iterator i = configs.keySet().iterator();
			String key;
			while (i.hasNext()) {
				key = (String) i.next();
				config = (HadoopConfig) configs.get(key);
				if (!key.startsWith("dfs")) {
					if (key.equals(DFSConfigKeys.FS_DEFAULT_NAME_KEY)) {
						hdfsUri = "hdfs://" + host.getHostIP() + ":" + servicePort;
						config.setValue(hdfsUri);
					} else if (key.equals(DFSConfigKeys.NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/etc/"
								+ QueryIOConstants.NAMENODE + "-conf_" + node.getId() + "/topologyConfig.sh");
					} else if (key.equals(QueryIOConstants.NAMENODE_OPTS_KEY)) {
						String[] args = config.getValue().split(" ");
						String val = "";

						for (int var = 0; var < args.length; var++) {
							if (var != 0)
								val += " ";
							if (args[var].startsWith("-Dcom.sun.management.jmxremote.port=")) {
								val += "-Dcom.sun.management.jmxremote.port=" + jmxPort;
							} else if (host.isWindows() && args[var].startsWith("$HADOOP_NAMENODE_OPTS")) {
								val += args[var].replaceAll("\\$", "%") + "%";
							} else {
								val += args[var];
							}
						}
						config.setValue(val);
					} else if (key.equals(QueryIOConstants.QUERYIO_OS3SERVER_PORT)) {
						config.setValue(os3ServicesPort);
					} else if (key.equals(QueryIOConstants.QUERYIO_NAMENODE_DATA_DISK)) {
						config.setValue(disk);
					} else if (key.equals(QueryIOConstants.QUERYIO_OS3SERVER_SECUREPORT)) {
						config.setValue(secureOs3ServerPort);
					} else if (key.equals(QueryIOConstants.QUERYIO_HDFSOVERFTP_PORT)) {
						config.setValue(hdfsoverftpServerPort);
					} else if (key.equals(QueryIOConstants.QUERYIO_FTPSERVER_PORT)) {
						config.setValue(ftpserverPort);
					} else if (key.equals(QueryIOConstants.QUERYIO_FTPSERVER_SSLPORT)) {
						config.setValue(secureFtpserverPort);
					} else if (key.equals(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID)) {
						continue;
					} else if (key.equals(QueryIOConstants.ANALYTICS_DB_DBSOURCEID)) {
						continue;
					} else if (key.equals(QueryIOConstants.HADOOP_LOG_DIR_KEY)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/logs/"
								+ QueryIOConstants.NAMENODE + "_" + node.getId());
					} else if (key.equals(QueryIOConstants.HADOOP_PID_DIR_KEY)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/pid/"
								+ QueryIOConstants.NAMENODE + "_" + node.getId());
					}
					coreSiteKeyList.add(key);
					coreSiteValueList.add(config.getValue());
				} else {
					if (key.equals(DFSConfigKeys.DFS_NAMESERVICES)) {
						config.setValue(namenodeId);
					} else if (key.equals(DFSConfigKeys.DFS_NAMESERVICE_ID)) {
						config.setValue(namenodeId);
					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY)) {
						continue;
					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY)) {
						continue;
					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY)) {
						continue;
					} else if (key.equals(DFSConfigKeys.DFS_HA_NAMENODES_KEY_PREFIX)) {
						continue;
					} else if (key.endsWith("address")) {
						config.setValue(host.getHostIP() + ":" + config.getValue().split(":")[1]);
					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY)) {
						// if(host.isWindows()) {
						// config.setValue(dirPath.replaceAll("\\\\", "/"));
						// } else {
						// config.setValue(dirPath);
						// }
						config.setValue(dirPath);
					} else if (key.equals(DFSConfigKeys.DFS_HOSTS)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/etc/"
								+ QueryIOConstants.NAMENODE + "-conf_" + node.getId() + "/hosts");
					} else if (key.equals(DFSConfigKeys.DFS_HOSTS_EXCLUDE)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/etc/"
								+ QueryIOConstants.NAMENODE + "-conf_" + node.getId() + "/hosts-exclude");
					}
					hdfsSiteKeyList.add(config.getKey());
					hdfsSiteValueList.add(config.getValue());
				}
			}
			TagParserConfigManager.populateCustomTagParserConfig(connection, namenodeId, coreSiteKeyList,
					coreSiteValueList);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
		} finally {
			undoPathSeparatorForWindows(host);
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			if (!key.startsWith("dfs")) {
				coreSiteKeyList.add(key);
				coreSiteValueList.add((String) values.get(i));
			} else {
				hdfsSiteKeyList.add(key);
				hdfsSiteValueList.add((String) values.get(i));
			}
		}

		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.clearConfiguration(host.getInstallDirPath(), QueryIOConstants.NAMENODE, node.getId());

			remoteService.updateConfiguration(host.getInstallDirPath(), QueryIOConstants.NAMENODE, node.getId(),
					"core-site.xml", coreSiteKeyList, coreSiteValueList, true);

			remoteService.updateConfiguration(host.getInstallDirPath(), QueryIOConstants.NAMENODE, node.getId(),
					"hdfs-site.xml", hdfsSiteKeyList, hdfsSiteValueList, true);

			ArrayList sslKeys = new ArrayList();
			sslKeys.add("ssl.server.keystore.location");
			sslKeys.add("ssl.server.keystore.password");
			sslKeys.add("ssl.server.keystore.keypassword");
			ArrayList sslValues = new ArrayList();

			sslValues.add(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/keystore");
			sslValues.add(QueryIOConstants.SSL_DEFAULT_PASSWORD);
			sslValues.add(QueryIOConstants.SSL_DEFAULT_PASSWORD);
			remoteService.updateConfiguration(host.getInstallDirPath(), QueryIOConstants.NAMENODE, node.getId(),
					"ssl-server.xml", sslKeys, values, true);

			sslKeys = new ArrayList();
			sslKeys.add("ssl.client.keystore.location");
			sslKeys.add("ssl.client.keystore.password");
			sslKeys.add("ssl.client.keystore.keypassword");

			sslValues = new ArrayList();
			sslValues.add(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/keystore");
			sslValues.add(QueryIOConstants.SSL_DEFAULT_PASSWORD);
			sslValues.add(QueryIOConstants.SSL_DEFAULT_PASSWORD);
			remoteService.updateConfiguration(host.getInstallDirPath(), QueryIOConstants.NAMENODE, node.getId(),
					"ssl-client.xml", sslKeys, sslValues, true);

			dwrResponse.setResponseMessage("configured successfully");
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseCode(200);
			// return new QueryIOResponse(true, "configured successfully");
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);

			dwrResponse.setResponseMessage("Host" + host.getHostIP() + " not responding. " + e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
			// return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static void setDatanodeDefaultConfiguration(ArrayList federatedKeys, ArrayList federatedValues,
			Host datanodeHost, Node node, String volumePath, String serverPort, String httpPort, String httpsPort,
			String ipcPort, String jmxPort, String disks, DWRResponse dwrResponse) {

		Connection connection = null;
		ArrayList coreSiteKeyList = new ArrayList();
		ArrayList coreSiteValueList = new ArrayList();
		ArrayList hdfsSiteKeyList = new ArrayList();
		ArrayList hdfsSiteValueList = new ArrayList();

		replacePathSeparatorForWindows(datanodeHost);
		volumePath = volumePath.replaceAll("\\\\", "/");
		HadoopConfig config;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			HashMap configs = HadoopConfigDAO.getAllDataNodeHadoopConfigs(connection);

			Iterator i = configs.keySet().iterator();
			String key;

			while (i.hasNext()) {
				key = (String) i.next();
				config = (HadoopConfig) configs.get(key);
				if (!key.startsWith("dfs")) {
					if (key.equals(QueryIOConstants.DATANODE_OPTS_KEY)) {
						String[] args = config.getValue().split(" ");
						String val = "";
						for (int var = 0; var < args.length; var++) {
							if (var != 0)
								val += " ";
							if (args[var].startsWith("-Dcom.sun.management.jmxremote.port=")) {
								val += "-Dcom.sun.management.jmxremote.port=" + jmxPort;
							} else if (datanodeHost.isWindows() && args[var].startsWith("$HADOOP_DATANODE_OPTS")) {
								val += args[var].replaceAll("\\$", "%") + "%";
							} else {
								val += args[var];
							}
						}
						config.setValue(val);
					} else if (key.equals(QueryIOConstants.HADOOP_LOG_DIR_KEY)) {
						config.setValue(datanodeHost.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/logs/"
								+ QueryIOConstants.DATANODE + "_" + node.getId());
					} else if (key.equals(QueryIOConstants.HADOOP_PID_DIR_KEY)) {
						config.setValue(datanodeHost.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/pid/"
								+ QueryIOConstants.DATANODE + "_" + node.getId());
					} else if (key.equals(QueryIOConstants.QUERYIO_DATANODE_DATA_DISK)) {
						config.setValue(disks);
					}
					coreSiteKeyList.add(key);
					coreSiteValueList.add(config.getValue());
				} else {
					if (key.equals(DFSConfigKeys.DFS_NAMESERVICES)) {
						continue;
					} else if (key.equals(DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY)) {
						config.setValue(datanodeHost.getHostIP() + ":" + serverPort);
					} else if (key.equals(DFSConfigKeys.DFS_DATANODE_HTTP_ADDRESS_KEY)) {
						config.setValue(datanodeHost.getHostIP() + ":" + httpPort);
					} else if (key.equals(DFSConfigKeys.DFS_DATANODE_HTTPS_ADDRESS_KEY)) {
						config.setValue(datanodeHost.getHostIP() + ":" + httpsPort);
					} else if (key.equals(DFSConfigKeys.DFS_DATANODE_IPC_ADDRESS_KEY)) {
						config.setValue(datanodeHost.getHostIP() + ":" + ipcPort);
					} else if (volumePath != null && key.equals(DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY)) {
						config.setValue((datanodeHost.isWindows() ? "file:///" : "file://") + volumePath);
					}

					hdfsSiteKeyList.add(config.getKey());
					hdfsSiteValueList.add(config.getValue());
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseCode(500);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
		} finally {
			undoPathSeparatorForWindows(datanodeHost);
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}

		for (int i = 0; i < federatedKeys.size(); i++) {
			String key = (String) federatedKeys.get(i);
			if (!key.startsWith("dfs")) {
				coreSiteKeyList.add(key);
				coreSiteValueList.add((String) federatedValues.get(i));
			} else {
				hdfsSiteKeyList.add(key);
				hdfsSiteValueList.add((String) federatedValues.get(i));
			}
		}

		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					datanodeHost.getHostIP(), Integer.parseInt(datanodeHost.getAgentPort()),
					QueryIOConstants.AGENT_QUERYIO, RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.clearConfiguration(datanodeHost.getInstallDirPath(), QueryIOConstants.DATANODE, node.getId());

			remoteService.updateConfiguration(datanodeHost.getInstallDirPath(), QueryIOConstants.DATANODE, node.getId(),
					"core-site.xml", coreSiteKeyList, coreSiteValueList, true);
			remoteService.updateConfiguration(datanodeHost.getInstallDirPath(), QueryIOConstants.DATANODE, node.getId(),
					"hdfs-site.xml", hdfsSiteKeyList, hdfsSiteValueList, true);
			ArrayList keys = new ArrayList();
			keys.add("ssl.server.keystore.location");
			keys.add("ssl.server.keystore.password");
			keys.add("ssl.server.keystore.keypassword");
			ArrayList values = new ArrayList();
			values.add(datanodeHost.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/keystore");
			values.add(QueryIOConstants.SSL_DEFAULT_PASSWORD);
			values.add(QueryIOConstants.SSL_DEFAULT_PASSWORD);
			remoteService.updateConfiguration(datanodeHost.getInstallDirPath(), QueryIOConstants.DATANODE, node.getId(),
					"ssl-server.xml", keys, values, true);
			keys = new ArrayList();
			keys.add("ssl.client.keystore.location");
			keys.add("ssl.client.keystore.password");
			keys.add("ssl.client.keystore.keypassword");
			values = new ArrayList();
			values.add(datanodeHost.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/keystore");
			values.add(QueryIOConstants.SSL_DEFAULT_PASSWORD);
			values.add(QueryIOConstants.SSL_DEFAULT_PASSWORD);
			remoteService.updateConfiguration(datanodeHost.getInstallDirPath(), QueryIOConstants.DATANODE, node.getId(),
					"ssl-client.xml", keys, values, true);

			dwrResponse.setDwrResponse(true, QueryIOConstants.DATANODE + " " + "configured successfully", 200);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + datanodeHost.getHostIP() + " not responding", e);
			dwrResponse.setDwrResponse(false,
					"Host " + datanodeHost.getHostIP() + " not responding. Exception " + e.getMessage(), 500);

		}
	}

	public static QueryIOResponse unsetConfiguration(Host host, Node node, ArrayList keys, String fileName) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.unsetConfiguration(host.getInstallDirPath(), node.getNodeType(), node.getId(),
					fileName, keys);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			return new QueryIOResponse(false, e.getLocalizedMessage());
		}
	}

	public static void startMonitoring(Host host) throws Exception {
		QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0, host.getHostIP(),
				Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
				RemoteService.QUERYIO_SERVICE_BEAN);
		remoteService.startMonitoring();
	}

	public static void stopMonitoring(Host host) {
		QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0, host.getHostIP(),
				Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
				RemoteService.QUERYIO_SERVICE_BEAN);
		remoteService.stopMonitoring();
	}

	public static SystemStatistics getSystemStatistics(Host host) throws Exception {
		QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0, host.getHostIP(),
				Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
				RemoteService.QUERYIO_SERVICE_BEAN);
		return remoteService.getSystemStatistics();
	}

	public static void runFSCKCommand(Host host, Node node, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			QueryIOResponse response = remoteService.runFSCKCommand(host.getInstallDirPath(), node.getId());
			dwrResponse.setDwrResponse(response.isSuccessful(), response.getResponseMsg(), 206);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);

		}
	}

	public static void startBalancer(Host host, Node node, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);

			QueryIOResponse response = remoteService.startBalancer(host.getInstallDirPath(), node.getId());
			dwrResponse.setDwrResponse(response.isSuccessful(), response.getResponseMsg(), 206);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		}
	}

	public static QueryIOResponse refreshNodes(Host nameNodeHost, Node node) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					nameNodeHost.getHostIP(), Integer.parseInt(nameNodeHost.getAgentPort()),
					QueryIOConstants.AGENT_QUERYIO, RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.refreshNodes(nameNodeHost.getInstallDirPath(), node.getId());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + nameNodeHost.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse setSafemode(Host nameNodeHost, Node node, boolean safemode) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					nameNodeHost.getHostIP(), Integer.parseInt(nameNodeHost.getAgentPort()),
					QueryIOConstants.AGENT_QUERYIO, RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.setSafemode(nameNodeHost.getInstallDirPath(), node.getId(), safemode);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + nameNodeHost.getHostIP() + " not responding", e);
		}
		return null;
	}

	public static QueryIOResponse updateHostsList(Host nameNodeHost, Node node, String[] datanodeAdds) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					nameNodeHost.getHostIP(), Integer.parseInt(nameNodeHost.getAgentPort()),
					QueryIOConstants.AGENT_QUERYIO, RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.updateHostsList(nameNodeHost.getInstallDirPath(), node.getId(), datanodeAdds);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + nameNodeHost.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse updateHostsExcludeList(Host nameNodeHost, Node node, String[] datanodeAdds) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					nameNodeHost.getHostIP(), Integer.parseInt(nameNodeHost.getAgentPort()),
					QueryIOConstants.AGENT_QUERYIO, RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.updateHostsExcludeList(nameNodeHost.getInstallDirPath(), node.getId(), datanodeAdds);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + nameNodeHost.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse setAllNodeConfig(Host host, Node node, ArrayList configkeys, ArrayList configValues) {

		String configKey;
		ArrayList keyList1 = new ArrayList();
		ArrayList valueList1 = new ArrayList();
		ArrayList keyList2 = new ArrayList();
		ArrayList valueList2 = new ArrayList();
		String fileName1 = null, fileName2 = null;

		if (node.getNodeType().equals(QueryIOConstants.NAMENODE) || node.getNodeType().equals(QueryIOConstants.DATANODE)
				|| node.getNodeType().equals(QueryIOConstants.SECONDARYNAMENODE)) {
			for (int i = 0; i < configkeys.size(); i++) {
				configKey = (String) configkeys.get(i);
				if (configKey.startsWith("dfs")) {
					keyList2.add(configKey);
					valueList2.add((String) configValues.get(i));
				} else {
					keyList1.add(configKey);
					valueList1.add((String) configValues.get(i));
				}
			}
			fileName1 = "core-site.xml";
			fileName2 = "hdfs-site.xml";
		} else {
			for (int i = 0; i < configkeys.size(); i++) {
				configKey = (String) configkeys.get(i);
				if (configKey.startsWith("mapred")) {
					keyList2.add(configKey);
					valueList2.add((String) configValues.get(i));
				} else {
					keyList1.add(configKey);
					valueList1.add((String) configValues.get(i));
				}
			}
			fileName1 = "yarn-site.xml";
			fileName2 = "mapred-site.xml";
		}

		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);

			if (keyList1.size() != 0) {
				remoteService.updateConfiguration(host.getInstallDirPath(), node.getNodeType(), node.getId(), fileName1,
						keyList1, valueList1, false);
			}
			if (keyList2.size() != 0) {
				remoteService.updateConfiguration(host.getInstallDirPath(), node.getNodeType(), node.getId(), fileName2,
						keyList2, valueList2, false);
			}
			return new QueryIOResponse(true, QueryIOConstants.DATANODE + " " + "configured successfully");
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static ArrayList getAllNodeConfig(Host host, Node node) {
		QueryIOService remoteService = null;
		ArrayList result = new ArrayList();
		try {
			remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0, host.getHostIP(),
					Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			if (remoteService != null) {
				Connection connection = null;

				HashMap configMap = null;
				try {
					connection = CoreDBManager.getQueryIODBConnection();
					configMap = HadoopConfigDAO.getHadoopConfigs(connection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				} finally {
					try {
						CoreDBManager.closeConnection(connection);
					} catch (Exception e) {
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Error closing connection", e);
					}
				}

				String[] nodeConfig = null;
				if (node.getNodeType().equals(QueryIOConstants.NAMENODE)
						|| node.getNodeType().equals(QueryIOConstants.DATANODE)
						|| node.getNodeType().equals(QueryIOConstants.JOURNALNODE)
						|| node.getNodeType().equals(QueryIOConstants.SECONDARYNAMENODE)) {
					nodeConfig = remoteService.getAllConfiguration(host.getInstallDirPath(), node.getNodeType(),
							node.getId(), "hdfs-site.xml");
					getDescriptions(node.getNodeType(), nodeConfig, configMap, result);

					nodeConfig = remoteService.getAllConfiguration(host.getInstallDirPath(), node.getNodeType(),
							node.getId(), "core-site.xml");
					getDescriptions(node.getNodeType(), nodeConfig, configMap, result);
				} else {
					nodeConfig = remoteService.getAllConfiguration(host.getInstallDirPath(), node.getNodeType(),
							node.getId(), "mapred-site.xml");
					getDescriptions(node.getNodeType(), nodeConfig, configMap, result);

					nodeConfig = remoteService.getAllConfiguration(host.getInstallDirPath(), node.getNodeType(),
							node.getId(), "yarn-site.xml");
					getDescriptions(node.getNodeType(), nodeConfig, configMap, result);
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
		}
		return result;
	}

	private static void getDescriptions(String nodeType, String[] nodeConfig, HashMap configMap, ArrayList result) {
		HadoopConfig hc = null;
		for (int i = 0; i < nodeConfig.length; i++) {
			String[] arr = nodeConfig[i].split(QueryIOConstants.SEPERATOR);

			HadoopConfig config = new HadoopConfig();
			config.setKey(arr[0]);
			config.setValue(arr[1]);
			config.setType(nodeType);

			if (configMap.containsKey(arr[0])) {
				hc = (HadoopConfig) configMap.get(arr[0]);
				config.setDescription(hc.getDescription());
			} else {
				String key = arr[0];
				while (key.indexOf(".") != -1) {
					key = key.substring(0, key.lastIndexOf("."));
					if (configMap.containsKey(key)) {
						hc = (HadoopConfig) configMap.get(key);
						config.setDescription(hc.getDescription());
						break;
					}
				}
			}

			result.add(config);
		}
	}

	public static ArrayList getConfig(Host host, ArrayList key, Node node, String fileName) {
		QueryIOService remoteService = null;
		try {
			remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0, host.getHostIP(),
					Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.getConfiguration(host.getInstallDirPath(), node.getNodeType(), node.getId(), fileName,
					key);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
		}
		return null;
	}

	public static HashMap getNodeManagerLogsPath(String nodeId, String applicationId, Host host) {
		QueryIOService remoteService = null;
		try {
			remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0, host.getHostIP(),
					Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.getNodeManagerLogsPath(host.getInstallDirPath(), nodeId, applicationId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
		}
		return null;
	}

	public static ArrayList getPhysicalDiskNames(Host host) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.getPhysicalDisksInfo();

		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
		}
		return null;
	}

	public static boolean isMacOS(Host host) {
		QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0, host.getHostIP(),
				Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
				RemoteService.QUERYIO_SERVICE_BEAN);
		return remoteService.isMacOS();
	}

	public static boolean isWindowsOS(Host host) {
		QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0, host.getHostIP(),
				Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
				RemoteService.QUERYIO_SERVICE_BEAN);
		return remoteService.isWindowsOS();
	}

	public static ArrayList validateVolumeDiskMapping(Host host, ArrayList disks, ArrayList volumes) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.validateVolumeDiskMapping(disks, volumes);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
		}
		return null;
	}

	public static HashMap getVolumeDiskMap(Host host) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.getDiskMap();

		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
		}
		return null;
	}

	public static String formatDisks(Host host, ArrayList disks) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.formatDisks(disks);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
		}
		return null;
	}

	public static QueryIOResponse updateNetworkTopology(Host host, Node node, String[] hostIps, String[] rackNames) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.updateNetworkConfig(host.getInstallDirPath(), node.getId(), hostIps, rackNames);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static void copyEditsDirToSharedDir(Host host, Node node, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			QueryIOResponse res = remoteService.copyEditsDirToSharedDir(host.getInstallDirPath(), node.getId());
			dwrResponse.setDwrResponse(res.isSuccessful(), res.getResponseMsg(), 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		}
	}

	public static void performFailOver(Host host, Node node, String args, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);

			QueryIOResponse res = remoteService.performFailover(host.getInstallDirPath(), node.getId(), args);
			dwrResponse.setDwrResponse(res.isSuccessful(), res.getResponseMsg(), 200);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		}
	}

	public static QueryIOResponse updateHadoopEnv(Host host, Node node) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.updateHadoopEnv(host.getInstallDirPath(), node.getNodeType(), node.getId());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}

	}

	public static QueryIOResponse updateYarnEnv(Host host, Node node) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.updateYarnEnv(host.getInstallDirPath(), node.getNodeType(), node.getId());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}

	}

	public static void copySharedDirLogstoEditsLogs(Host host, Node node, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);

			QueryIOResponse res = remoteService.copySharedDirLogstoEditsLogs(host.getInstallDirPath(), node.getId());
			dwrResponse.setDwrResponse(res.isSuccessful(), res.getResponseMsg(), 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		}

	}

	public static String getOSUserName(Host host, Node node) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);

			return remoteService.getOSUserName();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);

		}
		return null;
	}

	public static void setResourceManagerDefaultConfiguration(Host host, Node node, String serverPort,
			String schedulerPort, String webAppPort, String adminPort, String jmxPort, String resourceTrackerPort,
			String jobHistoryServerPort, String jobHistoryWebappPort, String jhTempPath, String jhPath,
			DWRResponse dwrResponse) {
		Connection connection = null;
		ArrayList mapRedKeyList = new ArrayList();
		ArrayList mapRedValueList = new ArrayList();
		ArrayList yarnKeyList = new ArrayList();
		ArrayList yarnValueList = new ArrayList();

		replacePathSeparatorForWindows(host);

		HadoopConfig config;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			HashMap configs = HadoopConfigDAO.getAllResourceManagerHadoopConfigs(connection);

			Iterator i = configs.keySet().iterator();
			String key;
			while (i.hasNext()) {
				key = (String) i.next();
				config = (HadoopConfig) configs.get(key);
				if (key.startsWith("mapred")) {
					if (key.equals("mapreduce.jobhistory.address")) {
						config.setValue(host.getHostIP() + ":" + jobHistoryServerPort);
					} else if (key.equals("mapreduce.jobhistory.webapp.address")) {
						config.setValue(host.getHostIP() + ":" + jobHistoryWebappPort);
					} else if (key.equals("mapreduce.jobhistory.intermediate-done-dir")) {
						config.setValue(jhTempPath);
					} else if (key.equals("mapreduce.jobhistory.done-dir")) {
						config.setValue(jhPath);
					}
					mapRedKeyList.add(key);
					mapRedValueList.add(config.getValue());
				} else {
					if (key.equals(YarnConfiguration.RM_ADDRESS)) {
						config.setValue(host.getHostIP() + ":" + serverPort);
					} else if (key.equals(YarnConfiguration.RM_SCHEDULER_ADDRESS)) {
						config.setValue(host.getHostIP() + ":" + schedulerPort);
					} else if (key.equals(YarnConfiguration.RM_WEBAPP_ADDRESS)) {
						config.setValue(host.getHostIP() + ":" + webAppPort);
					} else if (key.equals(YarnConfiguration.RM_RESOURCE_TRACKER_ADDRESS)) {
						config.setValue(host.getHostIP() + ":" + resourceTrackerPort);
					} else if (key.equals(YarnConfiguration.RM_ADMIN_ADDRESS)) {
						config.setValue(host.getHostIP() + ":" + adminPort);
					} else if (key.equals(QueryIOConstants.YARN_LOG_DIR_KEY)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/logs/"
								+ QueryIOConstants.RESOURCEMANAGER + "_" + node.getId());
					} else if (key.equals(QueryIOConstants.YARN_PID_DIR_KEY)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/pid/"
								+ QueryIOConstants.RESOURCEMANAGER + "_" + node.getId());
					} else if (key.equals(QueryIOConstants.RESOURCEMANAGER_OPTS_KEY)) {
						String[] args = config.getValue().split(" ");
						String val = "";

						for (int var = 0; var < args.length; var++) {
							if (var != 0)
								val += " ";
							if (args[var].startsWith("-Dcom.sun.management.jmxremote.port=")) {
								val += "-Dcom.sun.management.jmxremote.port=" + jmxPort;
							} else if (host.isWindows() && args[var].startsWith("$YARN_RESOURCEMANAGER_OPTS")) {
								val += args[var].replaceAll("\\$", "%") + "%";
							} else {
								val += args[var];
							}
						}
						config.setValue(val);
					}
					yarnKeyList.add(config.getKey());
					yarnValueList.add(config.getValue());
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
		} finally {
			undoPathSeparatorForWindows(host);
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}

		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);

			remoteService.updateConfiguration(host.getInstallDirPath(), QueryIOConstants.RESOURCEMANAGER, node.getId(),
					"mapred-site.xml", mapRedKeyList, mapRedValueList, true);

			remoteService.updateConfiguration(host.getInstallDirPath(), QueryIOConstants.RESOURCEMANAGER, node.getId(),
					"yarn-site.xml", yarnKeyList, yarnValueList, true);

			dwrResponse.setResponseMessage("configured successfully");
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseCode(200);
			// return new QueryIOResponse(true, "configured successfully");
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			dwrResponse.setResponseMessage("Host" + host.getHostIP() + " not responding. " + e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
			// return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static void setNodeManagerDefaultConfiguration(Host host, Node node, String rmServerUri,
			String rmSchedulerUri, String rmWebAppUri, String rmAdminUri, String rmResourceTrackerUri,
			String localizerPort, String webAppPort, String jmxPort, String logDir, String localDir,
			DWRResponse dwrResponse) {
		Connection connection = null;
		ArrayList mapRedKeyList = new ArrayList();
		ArrayList mapRedValueList = new ArrayList();
		ArrayList yarnKeyList = new ArrayList();
		ArrayList yarnValueList = new ArrayList();
		String hdfsUri = "";

		replacePathSeparatorForWindows(host);

		HadoopConfig config;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			HashMap configs = HadoopConfigDAO.getAllNodeManagerHadoopConfigs(connection);

			Iterator i = configs.keySet().iterator();
			String key;
			while (i.hasNext()) {
				key = (String) i.next();
				config = (HadoopConfig) configs.get(key);
				if (key.startsWith("mapred")) {
					mapRedKeyList.add(key);
					mapRedValueList.add(config.getValue());
				} else {
					if (key.equals(YarnConfiguration.RM_ADDRESS)) {
						config.setValue(rmServerUri);
					} else if (key.equals(YarnConfiguration.RM_RESOURCE_TRACKER_ADDRESS)) {
						config.setValue(rmResourceTrackerUri);
					} else if (key.equals(YarnConfiguration.RM_SCHEDULER_ADDRESS)) {
						config.setValue(rmSchedulerUri);
					} else if (key.equals(YarnConfiguration.RM_WEBAPP_ADDRESS)) {
						config.setValue(rmWebAppUri);
					} else if (key.equals(YarnConfiguration.RM_ADMIN_ADDRESS)) {
						config.setValue(rmAdminUri);
					} else if (key.equals(YarnConfiguration.NM_LOCALIZER_ADDRESS)) {
						config.setValue(host.getHostIP() + ":" + localizerPort);
					} else if (key.equals(YarnConfiguration.NM_WEBAPP_ADDRESS)) {
						config.setValue(host.getHostIP() + ":" + webAppPort);
					} else if (key.equals(YarnConfiguration.NM_LOCAL_DIRS)) {
						config.setValue(localDir);
					} else if (key.equals(YarnConfiguration.NM_LOG_DIRS)) {
						config.setValue(logDir);
					} else if (key.equals(QueryIOConstants.YARN_LOG_DIR_KEY)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/logs/"
								+ QueryIOConstants.NODEMANAGER + "_" + node.getId());
					} else if (key.equals(QueryIOConstants.YARN_PID_DIR_KEY)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/pid/"
								+ QueryIOConstants.NODEMANAGER + "_" + node.getId());
					} else if (key.equals(QueryIOConstants.NODEMANAGER_OPTS_KEY)) {
						String[] args = config.getValue().split(" ");
						String val = "";

						for (int var = 0; var < args.length; var++) {
							if (var != 0)
								val += " ";
							if (args[var].startsWith("-Dcom.sun.management.jmxremote.port=")) {
								val += "-Dcom.sun.management.jmxremote.port=" + jmxPort;
							} else if (host.isWindows() && args[var].startsWith("$YARN_NODEMANAGER_OPTS")) {
								val += args[var].replaceAll("\\$", "%") + "%";
							} else {
								val += args[var];
							}
						}
						config.setValue(val);
					}
					yarnKeyList.add(config.getKey());
					yarnValueList.add(config.getValue());
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
		} finally {
			undoPathSeparatorForWindows(host);
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}

		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);

			remoteService.clearConfiguration(host.getInstallDirPath(), QueryIOConstants.NODEMANAGER, node.getId());

			remoteService.updateConfiguration(host.getInstallDirPath(), QueryIOConstants.NODEMANAGER, node.getId(),
					"mapred-site.xml", mapRedKeyList, mapRedValueList, true);

			remoteService.updateConfiguration(host.getInstallDirPath(), QueryIOConstants.NODEMANAGER, node.getId(),
					"yarn-site.xml", yarnKeyList, yarnValueList, true);

			dwrResponse.setResponseMessage("configured successfully");
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseCode(200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			dwrResponse.setResponseMessage("Host" + host.getHostIP() + " not responding. " + e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
		}
	}

	public static QueryIOResponse runJob(Host host, Node node, MapRedJobConfig config, boolean deleteJobFile) {
		Connection connection = null;
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			connection = CoreDBManager.getQueryIODBConnection();
			Configuration conf = ConfigurationManager.getConfiguration(connection, config.getNamenodeId());
			String dbName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			String analyticsDbName = conf.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("runJob :" + host.getStatus());
			return remoteService.runJob(host.getInstallDirPath(), node.getId(), config.getJobName(),
					config.getJarName(), config.getLibJars(), config.getFiles(), config.getClassName(),
					config.getArguments(), conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY), dbName, analyticsDbName,
					conf.get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY), config.isRecursive(),
					config.isFilterApply(), config.getFilterQuery(), deleteJobFile);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			String error = "host " + host.getHostIP() + " not responding";
			return new QueryIOResponse(false, error);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database Conneciton", e);
			}
		}
	}

	public static QueryIOResponse runJobDataTagging(Host host, Node node, MapRedJobConfig config, String hdfsURI,
			String dbName, String encryptionKey, String fileTypeParsers) {
		Connection connection = null;
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Host Status :" + host.getStatus());

			return remoteService.runJobDataTagging(host.getInstallDirPath(), node.getId(), config.getJobName(),
					config.getJarName(), config.getLibJars(), config.getFiles(), config.getClassName(),
					config.getArguments(), hdfsURI, dbName, encryptionKey, fileTypeParsers);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			String error = "Host " + host.getHostIP() + " not responding";
			return new QueryIOResponse(false, error);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database Conneciton", e);
			}
		}
	}

	public static QueryIOResponse transferFolder(Host host, String relativePath, String folderName)
			throws FileNotFoundException, IOException {
		QueryIOResponse resp = null;

		String filePath = folderName + ".zip";

		String zipFilePath = EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE + "/"
				+ relativePath + "/" + filePath;

		File directoryToZip1 = new File(EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE
				+ "/" + relativePath + "/" + folderName);

		ArrayList listFiles = new ArrayList();
		listFiles.add(directoryToZip1);

		ZipUtil.compressFiles(listFiles, zipFilePath);
		File zipFile = new File(zipFilePath);
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Compressing done");

		BufferedInputStream fis = null;
		try {
			fis = new BufferedInputStream(new FileInputStream(zipFile));
			try {
				QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
						host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
						RemoteService.QUERYIO_SERVICE_BEAN);
				resp = remoteService.createStream(host.getInstallDirPath(), relativePath, filePath);
				byte[] b = new byte[QueryIOConstants.BUFFER_SIZE];
				int noOfBytes;

				while (resp.isSuccessful() && (noOfBytes = fis.read(b)) != -1) {
					resp = remoteService.writeToStream(filePath, b, 0, noOfBytes);
				}
				resp = remoteService.closeStream(host.getInstallDirPath(), relativePath, filePath, true);
				return resp;
			} catch (Exception e) {
				AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
				String error = "host " + host.getHostIP() + " not responding";
				return new QueryIOResponse(false, error);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error opening FileInputStream for file: " + zipFile.getAbsolutePath(), e);
			return new QueryIOResponse(false, e.getMessage());
		} finally {
			zipFile.delete();
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error closing FileInputStream", e);
				}
			}
		}

	}

	public static QueryIOResponse transferFile(Host host, String folderName, String fileName)
			throws FileNotFoundException, IOException {
		QueryIOResponse resp = null;

		String filePath = fileName;
		String absoluteFilePath = EnvironmentalConstants.getAppHome() + "/" + QueryIOConstants.MAPREDRESOURCE + "/"
				+ filePath;

		BufferedInputStream fis = null;
		try {
			fis = new BufferedInputStream(new FileInputStream(absoluteFilePath));
			try {
				QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
						host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
						RemoteService.QUERYIO_SERVICE_BEAN);
				resp = remoteService.createStream(host.getInstallDirPath(), folderName, filePath);
				byte[] b = new byte[QueryIOConstants.BUFFER_SIZE];
				int noOfBytes;

				while (resp.isSuccessful() && (noOfBytes = fis.read(b)) != -1) {
					resp = remoteService.writeToStream(filePath, b, 0, noOfBytes);
				}
				resp = remoteService.closeStream(host.getInstallDirPath(), folderName, filePath, false);
				return resp;
			} catch (Exception e) {
				AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
				return new QueryIOResponse(false, e.getMessage());
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error opening FileInputStream for file: " + absoluteFilePath, e);
			return new QueryIOResponse(false, e.getMessage());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error closing FileInputStream", e);
				}
			}
		}
	}

	public static QueryIOResponse deleteFile(Host host, String folderName, String fileName)
			throws FileNotFoundException, IOException {
		QueryIOResponse resp = null;

		String filePath = (folderName == null ? "" : folderName + "/") + fileName;
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.deleteFile(host.getInstallDirPath(), folderName, fileName);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse deleteFolder(Host host, String folderName) throws FileNotFoundException, IOException {
		QueryIOResponse resp = null;

		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.deleteFolder(host.getInstallDirPath(), folderName);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static void setJournalNodeConfig(Host host, Node node, String serverPort, String httpPort, String jmxPort,
			String dirPath, DWRResponse dwrResponse) {
		Connection connection = null;
		ArrayList coreSiteKeyList = new ArrayList();
		ArrayList coreSiteValueList = new ArrayList();
		ArrayList hdfsSiteKeyList = new ArrayList();
		ArrayList hdfsSiteValueList = new ArrayList();

		replacePathSeparatorForWindows(host);

		HadoopConfig config;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			HashMap configs = HadoopConfigDAO.getAllJournalNodeHadoopConfigs(connection);

			Iterator i = configs.keySet().iterator();
			String key;
			while (i.hasNext()) {
				key = (String) i.next();
				config = (HadoopConfig) configs.get(key);
				if (!key.startsWith("dfs")) {
					if (key.equals(QueryIOConstants.JOURNALNODE_OPTS_KEY)) {
						String[] args = config.getValue().split(" ");
						String val = "";

						for (int var = 0; var < args.length; var++) {
							if (var != 0)
								val += " ";
							if (args[var].startsWith("-Dcom.sun.management.jmxremote.port=")) {
								val += "-Dcom.sun.management.jmxremote.port=" + jmxPort;
							} else if (host.isWindows() && args[var].startsWith("$HADOOP_JOURNALNODE_OPTS")) {
								val += args[var].replaceAll("\\$", "%") + "%";
							} else {
								val += args[var];
							}
						}
						config.setValue(val);
					} else if (key.equals(QueryIOConstants.HADOOP_LOG_DIR_KEY)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/logs/"
								+ QueryIOConstants.JOURNALNODE + "_" + node.getId());
					} else if (key.equals(QueryIOConstants.HADOOP_PID_DIR_KEY)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/pid/"
								+ QueryIOConstants.JOURNALNODE + "_" + node.getId());
					} else {
						continue;
					}
					coreSiteKeyList.add(key);
					coreSiteValueList.add(config.getValue());
				} else {
					if (key.equals(DFSConfigKeys.DFS_JOURNALNODE_EDITS_DIR_KEY)) {
						config.setValue(dirPath);
					} else if (key.equals(DFSConfigKeys.DFS_JOURNALNODE_RPC_ADDRESS_KEY)) {
						config.setValue(host.getHostIP() + ":" + serverPort);
					} else if (key.equals(DFSConfigKeys.DFS_JOURNALNODE_HTTP_ADDRESS_KEY)) {
						config.setValue(host.getHostIP() + ":" + httpPort);
					} else {
						continue;
					}
					hdfsSiteKeyList.add(config.getKey());
					hdfsSiteValueList.add(config.getValue());
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
		} finally {
			undoPathSeparatorForWindows(host);
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}

		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.clearConfiguration(host.getInstallDirPath(), QueryIOConstants.JOURNALNODE, node.getId());
			remoteService.updateConfiguration(host.getInstallDirPath(), QueryIOConstants.JOURNALNODE, node.getId(),
					"core-site.xml", coreSiteKeyList, coreSiteValueList, true);

			remoteService.updateConfiguration(host.getInstallDirPath(), QueryIOConstants.JOURNALNODE, node.getId(),
					"hdfs-site.xml", hdfsSiteKeyList, hdfsSiteValueList, true);

			dwrResponse.setResponseMessage("configured successfully");
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseCode(200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			dwrResponse.setResponseMessage("Host" + host.getHostIP() + " not responding. " + e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
		}

	}

	public static void setSecondaryNameNodeConfig(Host host, Node node, String namenodeId, String namenodeRpcAdd,
			String httpPort, String jmxPort, String dirPath, DWRResponse dwrResponse) {

		Connection connection = null;
		ArrayList coreSiteKeyList = new ArrayList();
		ArrayList coreSiteValueList = new ArrayList();
		ArrayList hdfsSiteKeyList = new ArrayList();
		ArrayList hdfsSiteValueList = new ArrayList();
		replacePathSeparatorForWindows(host);
		HadoopConfig config;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			HashMap configs = HadoopConfigDAO.getAllCheckpointHadoopConfigs(connection);

			Iterator i = configs.keySet().iterator();
			String key;
			while (i.hasNext()) {
				key = (String) i.next();
				config = (HadoopConfig) configs.get(key);
				if (!key.startsWith("dfs")) {
					if (key.equals(DFSConfigKeys.FS_DEFAULT_NAME_KEY)) {
						config.setValue("hdfs://" + namenodeRpcAdd);
					} else if (key.equals(QueryIOConstants.HADOOP_LOG_DIR_KEY)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/logs/"
								+ QueryIOConstants.SECONDARYNAMENODE + "_" + node.getId());
					} else if (key.equals(QueryIOConstants.HADOOP_PID_DIR_KEY)) {
						config.setValue(host.getInstallDirPath() + QueryIOConstants.HADOOP_DIR_NAME + "/pid/"
								+ QueryIOConstants.SECONDARYNAMENODE + "_" + node.getId());
					} else if (key.equals(QueryIOConstants.SECONDARYNAMENODE_OPTS_KEY)) {
						String[] args = config.getValue().split(" ");
						String val = "";

						for (int var = 0; var < args.length; var++) {
							if (var != 0)
								val += " ";
							if (args[var].startsWith("-Dcom.sun.management.jmxremote.port=")) {
								val += "-Dcom.sun.management.jmxremote.port=" + jmxPort;
							} else if (host.isWindows() && args[var].startsWith("$HADOOP_SECONDARYNAMENODE_OPTS")) {
								val += args[var].replaceAll("\\$", "%") + "%";
							} else {
								val += args[var];
							}
						}
						config.setValue(val);
					} else {
						continue;
					}
					coreSiteKeyList.add(key);
					coreSiteValueList.add(config.getValue());
				} else {
					if (key.equals(DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY)) {
						config.setKey(DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY + "." + namenodeId);
						config.setValue(host.getHostIP() + ":" + httpPort);
					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_CHECKPOINT_DIR_KEY)) {
						config.setValue(dirPath);
					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_CHECKPOINT_PERIOD_KEY)) {

					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_CHECKPOINT_CHECK_PERIOD_KEY)) {

					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_NUM_CHECKPOINTS_RETAINED_KEY)) {

					} else if (key.equals(DFSConfigKeys.DFS_NAMENODE_CHECKPOINT_TXNS_KEY)) {

					} else {
						continue;
					}

					hdfsSiteKeyList.add(config.getKey());
					hdfsSiteValueList.add(config.getValue());
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
		} finally {
			undoPathSeparatorForWindows(host);
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
		hdfsSiteKeyList.add(DFSConfigKeys.DFS_NAMESERVICES);
		hdfsSiteValueList.add(namenodeId);
		hdfsSiteKeyList.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + namenodeId);
		hdfsSiteValueList.add(namenodeRpcAdd);
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.clearConfiguration(host.getInstallDirPath(), QueryIOConstants.SECONDARYNAMENODE,
					node.getId());
			remoteService.updateConfiguration(host.getInstallDirPath(), QueryIOConstants.SECONDARYNAMENODE,
					node.getId(), "core-site.xml", coreSiteKeyList, coreSiteValueList, true);

			remoteService.updateConfiguration(host.getInstallDirPath(), QueryIOConstants.SECONDARYNAMENODE,
					node.getId(), "hdfs-site.xml", hdfsSiteKeyList, hdfsSiteValueList, true);

			dwrResponse.setResponseMessage("configured successfully");
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseCode(200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			dwrResponse.setResponseMessage("Host" + host.getHostIP() + " not responding. " + e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
		}
	}

	public static QueryIOResponse transferDriverJar(Host host, String jarFileName) throws Exception {
		QueryIOResponse resp = null;
		String jarFilePath = EnvironmentalConstants.getAppHome() + QueryIOConstants.JDBC_JAR_DIR + File.separator
				+ jarFileName;
		File jarFile = new File(jarFilePath);

		BufferedInputStream fis = null;
		try {
			fis = new BufferedInputStream(new FileInputStream(jarFile));

			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			resp = remoteService.createJarCopyStream(host.getInstallDirPath(), jarFileName);
			byte[] b = new byte[QueryIOConstants.BUFFER_SIZE];
			int noOfBytes;

			while ((noOfBytes = fis.read(b)) != -1) {
				resp = remoteService.writeJarToStream(jarFileName, b, 0, noOfBytes);
			}
			resp = remoteService.closeJarCopyStream(host.getInstallDirPath(), jarFileName);
			return resp;

		} catch (Exception e) {
			throw e;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}

	public static QueryIOResponse replaceDBConfigFromHosts(Host host) throws Exception {
		QueryIOResponse resp = null;
		String fileName = QueryIOConstants.DBCONFIG_XML;
		String configFilePath = EnvironmentalConstants.getAppHome() + "WEB-INF" + File.separator + fileName;
		File dbConfigFile = new File(configFilePath);

		BufferedInputStream fis = null;
		try {
			fis = new BufferedInputStream(new FileInputStream(dbConfigFile));

			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			resp = remoteService.createDBConfigCopyStream(host.getInstallDirPath(), fileName);
			byte[] b = new byte[QueryIOConstants.BUFFER_SIZE];
			int noOfBytes;

			while ((noOfBytes = fis.read(b)) != -1) {
				resp = remoteService.writeDBCOnfigFile(fileName, b, 0, noOfBytes);
			}
			resp = remoteService.closeDBConfigCopyStream(host.getInstallDirPath(), fileName);
			return resp;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error opening FileInputStream for file: " + dbConfigFile.getAbsolutePath(), e);
			throw e;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}

	public static boolean hasMapping(Host host, String hostName, String hostAddress) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.hasMapping(hostName, hostAddress);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
		}
		return false;
	}

	public static boolean isReachable(Host host, String hostname) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.isReachable(hostname);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
		}
		return false;
	}

	public static String getHostName(Host host) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.getHostName();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
		}
		return null;
	}

	public static String getHostAddress(Host host) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.getHostAddress();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
		}
		return null;
	}

	public static QueryIOResponse checkPortAvailability(Host host, Node node) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.checkPortAvailability(host.getInstallDirPath(), node.getNodeType(), node.getId());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse checkPortAvailability(Host host, List<Integer> portList) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.checkPortAvailability(portList);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse startDBToFileMigration(String migrationId, Host host, String dbName,
			String destPath) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.startDBToFileMigration(migrationId, dbName, destPath);
			return new QueryIOResponse(true, QueryIOConstants.BACKUP_STARTED);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse deleteBackupData(Host host, String destPath) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.deleteBackupData(destPath);
			return new QueryIOResponse(true, "");
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse fetchNamespaceId(Host host, String namespaceDir) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.fetchNamespaceId(namespaceDir);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse fetchBlockPoolId(Host host, String namespaceDir) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			return remoteService.fetchBlockPoolId(namespaceDir);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host.getHostIP() + " not responding", e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static void updateJavaHome(Host host, String javaHomePath) throws Exception {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.updateJavaHome(host.getInstallDirPath(), javaHomePath);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			throw e;
		}
	}

	public static void updateHadoopPath(Host host, String path) throws Exception {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.updateHadoopPath(path);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			throw e;
		}
	}

	public static void updateStartStopScripts(Host host, String hostHome) throws Exception {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.updateStartStopScripts(hostHome);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			throw e;
		}
	}

	public static void updateHiveSite(Host host, String hostHome, String dbPort) throws Exception {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.updateHiveSite(hostHome, dbPort);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			throw e;
		}
	}

	public static void backupHadoopEtc(Host host, String hostHome) throws Exception {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.backupHadoopEtc(hostHome);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			throw e;
		}
	}

	public static void updateLoggerPropertiesFile(Host host) throws Exception {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.updateLoggerPropertiesFile(host.getInstallDirPath());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			throw e;
		}
	}

	public static void updateHiveHadoopHome(Host host) throws Exception {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			remoteService.updateHiveHadoopHome(host.getInstallDirPath());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			throw e;
		}
	}

	public static DWRResponse updateHiveSiteConfiguration(String nodeId, ArrayList configKeys, ArrayList configValues) {

		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setDwrResponse(false, "", 500);

		if (configKeys.size() != 0) {
			Connection connection = null;
			try {
				connection = CoreDBManager.getQueryIODBConnection();
				Node n = NodeDAO.getNode(connection, nodeId);
				Host h = HostDAO.getHostDetail(connection, n.getHostId());

				QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
						h.getHostIP(), Integer.parseInt(h.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
						RemoteService.QUERYIO_SERVICE_BEAN);

				QueryIOResponse response = remoteService.updateHiveSiteConfiguration(h.getInstallDirPath(), n.getId(),
						configKeys, configValues, false);

				if (!response.isSuccessful()) {
					dwrResponse.setDwrResponse(response.isSuccessful(), response.getResponseMsg(), 200);
					return dwrResponse;
				}

				// if (!n.getStatus().equals(QueryIOConstants.STATUS_STOPPED)) {
				// n.setStatus(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION);
				// NodeDAO.updateStatus(connection, n);
				// }

				dwrResponse.setDwrResponse(true, "Hive" + QueryIOConstants.CONFIGURATION_UPDATE_SUCCESS, 200);

				return dwrResponse;

			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
				dwrResponse.setDwrResponse(false,
						"Hive" + QueryIOConstants.CONFIGURATION_UPDATE_FAILED + " Exception: " + e.getMessage(), 200);
				return dwrResponse;
			} finally {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error closing database connection.", e);
				}
			}
		}
		return dwrResponse;
	}

	public static void startHiveServer(Host host, Node node, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			QueryIOResponse response = remoteService.startHiveServer(host.getInstallDirPath());
			dwrResponse.setResponseMessage(response.getResponseMsg());
			dwrResponse.setTaskSuccess(response.isSuccessful());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseMessage(e.getMessage());
		}
	}

	public static void stopHiveServer(Host host, Node node, DWRResponse dwrResponse) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			QueryIOResponse response = remoteService.stopHiveServer();
			dwrResponse.setResponseMessage(response.getResponseMsg());
			dwrResponse.setTaskSuccess(response.isSuccessful());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseMessage(e.getMessage());
		}
	}

	public static void startWindowsMonitoring(Host host, String username, String password) throws Exception {
		QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0, host.getHostIP(),
				Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
				RemoteService.QUERYIO_SERVICE_BEAN);
		remoteService.startWindowsMonitoring(host.getInstallDirPath(), username, password);
	}

	public static void refreshUserToGroupMapping(Host host, Node node) {
		try {
			QueryIOService remoteService = (QueryIOService) ServiceFactory.INSTANCE.getRemoteService(0,
					host.getHostIP(), Integer.parseInt(host.getAgentPort()), QueryIOConstants.AGENT_QUERYIO,
					RemoteService.QUERYIO_SERVICE_BEAN);
			QueryIOResponse response = remoteService.refreshUserToGroupsMappings(host.getInstallDirPath(),
					node.getId());
			AppLogger.getLogger().debug("Refresh UserGroupMapping at NameNode Status: " + response.getResponseMsg());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("host " + host + " not responding", e);
		}

	}
}