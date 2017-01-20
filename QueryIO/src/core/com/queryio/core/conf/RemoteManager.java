package com.queryio.core.conf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.SystemUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ibm.icu.text.SimpleDateFormat;
import com.jcraft.jsch.Session;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.beans.NNDBMigrationInfo;
import com.queryio.common.dao.GenericDBConfigDAO;
import com.queryio.common.dao.NNMIgrationInfoDAO;
import com.queryio.common.dao.NNRestoreInfoDAO;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfig;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBBackupTools;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.database.TableConstants;
import com.queryio.common.logger.AuditLogger;
import com.queryio.common.service.remote.QueryIOResponse;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.GetIpAddress;
import com.queryio.common.util.SSHRemoteExecution;
import com.queryio.common.util.SecurityHandler;
import com.queryio.common.util.StartupParameters;
import com.queryio.common.util.StaticUtilities;
import com.queryio.config.db.DBActivationRequestor;
import com.queryio.config.db.DBConfigBean;
import com.queryio.config.db.DBConfigDAO;
import com.queryio.config.db.DBConfigManager;
import com.queryio.core.adhoc.AdHocHiveClient;
import com.queryio.core.adhoc.AdHocQueryManager;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.applications.ApplicationManager;
import com.queryio.core.bean.AdHocQueryBean;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.DiagnosisStatusBean;
import com.queryio.core.bean.HadoopConfig;
import com.queryio.core.bean.HadoopService;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.MapRedJobConfig;
import com.queryio.core.bean.MigrationInfo;
import com.queryio.core.bean.Node;
import com.queryio.core.bean.NotifyBean;
import com.queryio.core.bean.QueryIOService;
import com.queryio.core.bean.RuleBean;
import com.queryio.core.bean.SearchedHostDetail;
import com.queryio.core.bean.TagParserConfig;
import com.queryio.core.bean.TreeBean;
import com.queryio.core.bean.User;
import com.queryio.core.bean.Volume;
import com.queryio.core.billing.BillingManager;
import com.queryio.core.consolidate.ConsolidationUtility;
import com.queryio.core.customtags.BigQueryManager;
import com.queryio.core.customtags.CustomTagsDAO;
import com.queryio.core.customtags.CustomTagsManager;
import com.queryio.core.customtags.metadata.MetaDataTagManager;
import com.queryio.core.dao.AdHocQueryDAO;
import com.queryio.core.dao.BillingDAO;
import com.queryio.core.dao.HAStatusDAO;
import com.queryio.core.dao.HadoopConfigDAO;
import com.queryio.core.dao.HadoopServiceDAO;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.MapRedJobConfigDAO;
import com.queryio.core.dao.MonitorDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.dao.NotifyDAO;
import com.queryio.core.dao.QueryIOServiceDAO;
import com.queryio.core.dao.StatusDAO;
import com.queryio.core.dao.TagParserDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.dao.VolumeDAO;
import com.queryio.core.datasources.DataSourceManager;
import com.queryio.core.datasources.FTPDataSource;
import com.queryio.core.datasources.HDFSDataSource;
import com.queryio.core.datasources.HTTPDataSource;
import com.queryio.core.datasources.S3DataSource;
import com.queryio.core.datasources.SFTPDataSource;
import com.queryio.core.datasources.SSHDataSource;
import com.queryio.core.monitor.beans.LiveAttribute;
import com.queryio.core.monitor.beans.SummaryTable;
import com.queryio.core.monitor.beans.SystemAttribute;
import com.queryio.core.monitor.controllers.ControllerManager;
import com.queryio.core.monitor.managers.AlertManager;
import com.queryio.core.monitor.managers.RuleManager;
import com.queryio.core.monitor.managers.SummaryManager;
import com.queryio.core.namespace.DiagnosisAndRepairManager;
import com.queryio.core.notification.NotificationHandler;
import com.queryio.core.permissions.PermissionsManager;
import com.queryio.core.permissions.ResetRootPermissionsThread;
import com.queryio.core.permissions.UserGroupDAO;
import com.queryio.core.reports.ReportManager;
import com.queryio.core.requestprocessor.GetItemCountRequest;
import com.queryio.core.requestprocessor.ListFilesRequest;
import com.queryio.core.requestprocessor.ListSelectedFilesRequest;
import com.queryio.core.requestprocessor.TagFileRequest;
import com.queryio.core.snapshots.SnapshotManager;
import com.queryio.core.ugiupdater.UGIUpdater;
import com.queryio.file.upload.FileUploadListener;
import com.queryio.file.upload.TagParserDBSchemaUpdator;
import com.queryio.ha.HATransitionManager;
import com.queryio.hadoopconfig.HadoopConfigManager;
import com.queryio.installcluster.UpgradeCluster;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.scheduler.service.ScheduleManager;
import com.queryio.scheduler.service.SchedulerConstants;
import com.queryio.stream.util.EncryptionHandler;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

@SuppressWarnings("PMD.AvoidUsingShortType")
public class RemoteManager {

	private static String parseDetailsKey = "ParseDetails";
	private static String fileType = "FileType";
	private static String hiveFileType = "HiveFileType";
	private static String json = "jsonString";

	private static String delimiterUIJSON = "delimiter";
	private static String encodingUIJSON = "encoding";
	private static String ifErrorOccurUIJSON = "ifErrorOccur";
	private static String hasHeaderUIJSON = "hasHeader";
	private static String valueSeperatorUIJSON = "valueSeperator";

	private static String delimiterDBJSON = "delimiter";
	private static String encodingDBSON = "encoding";
	private static String ifErrorOccurDBJSON = "isSkipAllRecords";
	private static String hasHeaderDBJSON = "isFirstRowHeader";
	private static String valueSeperatorDBJSON = "valueSeparator";

	public static long getItemCount(String nodeId, String path) {

		try {
			path = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			AppLogger.getLogger().fatal(e1.getLocalizedMessage(), e1);
		}

		GetItemCountRequest request = new GetItemCountRequest(RemoteManager.getLoggedInUser(), null, new Path(path),
				nodeId);

		try {
			request.process();
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
		}

		return request.getItemCount();
	}

	public static String getDefaultGroupForUser(String username) throws Exception {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return UserGroupDAO.getDefaultGroupForUser(connection, username);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static HashMap getRackTree() {
		HashMap rackMap = new HashMap();

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList datanodes = NodeDAO.getAllDatanodes(connection);
			Node node = null;
			Host host = null;
			HashMap hostMap = null;
			ArrayList nodeList = null;

			for (int i = 0; i < datanodes.size(); i++) {
				node = (Node) datanodes.get(i);
				host = HostDAO.getHostDetail(connection, node.getHostId());

				if (rackMap.get(host.getRackName()) == null) {
					rackMap.put(host.getRackName(), new HashMap());
				}
				hostMap = (HashMap) rackMap.get(host.getRackName());

				if (!hostMap.containsKey(host.getHostIP())) {
					if (hostMap.get(host.getHostIP()) == null) {
						hostMap.put(host.getHostIP(), new ArrayList());
					}
				}
				nodeList = (ArrayList) hostMap.get(host.getHostIP());
				nodeList.add(node);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return rackMap;
	}

	public static ArrayList getPhysicalDiskNames(String hostname) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, hostname);
			return QueryIOAgentManager.getPhysicalDiskNames(host);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static HashMap getPhysicalDiskNamesAutomation(String hostname, String id) {
		HashMap map = new HashMap();
		map.put("id", id);
		map.put("disks", getPhysicalDiskNames(hostname));

		return map;
	}

	public static HashMap getMetaStoreNamesAutomation(String hostname, String id) {
		HashMap map = new HashMap();
		map.put("id", id);
		map.put("MetaStore", getAllConnectionsNameForNameNode(true));

		return map;
	}

	public static HashMap getAdhocDbNamesAutomation(String hostname, String id) {
		HashMap map = new HashMap();
		map.put("id", id);
		map.put("Hive", getAllConnectionsNameForNameNode(false));

		return map;
	}

	public static HashMap getAllDbNamesAutomation(String hostname, String id) {
		HashMap map = new HashMap();
		map.put("id", id);
		map.put("MetaStore", getAllConnectionsNameForNameNode(true));
		map.put("adhocDb", getAllConnectionsNameForNameNode(false));
		return map;
	}

	public static HashMap getConfigurationServerPort(ArrayList<String> keys) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			HashMap configVal = new HashMap();
			for (String key : keys) {
				configVal.put(key, HadoopConfigDAO.getHadoopConfig(connection, key));
			}
			return configVal;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static HashMap getVolumeDiskMap(String hostname) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, hostname);
			return QueryIOAgentManager.getVolumeDiskMap(host);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList validateVolumeDiskMapping(String hostname, ArrayList disks, ArrayList volumes) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, hostname);
			return QueryIOAgentManager.validateVolumeDiskMapping(host, disks, volumes);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static String formatDisks(String hostname, ArrayList disks) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, hostname);
			return QueryIOAgentManager.formatDisks(host, disks);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static DWRResponse addHadoopConfigValue(String type, String configkey, String configValue,
			String description) {
		DWRResponse dwrResponse = new DWRResponse();
		Connection connection = null;
		try {
			if (!isAdmin()) {
				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}

			connection = CoreDBManager.getQueryIODBConnection();
			HadoopConfigDAO.addHadoopConfigValue(connection, type, configkey, configValue, description);
			dwrResponse.setDwrResponse(true, "System configuration key added successfully.", 200);

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;
	}

	public static DWRResponse validateJavaRegex(String regex) {
		DWRResponse dwrResponse = new DWRResponse();
		try {
			Pattern.compile(regex);
			dwrResponse.setDwrResponse(true, "Regex is valid", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		}
		return dwrResponse;
	}

	public static SummaryTable getAllHadoopConfigs() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return HadoopConfigDAO.getAllHadoopConfigs(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static SummaryTable getAllHadoopConfigsForHDFSType() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return HadoopConfigDAO.getAllHDFSHadoopConfigs(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static SummaryTable getAllHadoopConfigsForMapReduceType() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return HadoopConfigDAO.getAllMapReduceHadoopConfigs(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static HadoopConfig getHadoopConfig(String key) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return HadoopConfigDAO.getHadoopConfig(connection, key);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static SummaryTable getHAHadoopConfigs() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return HadoopConfigDAO.getHAHadoopConfigs(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static void updateDataFetchInterval() {
		int dataFetchInterval = QueryIOConstants.DEFAULT_CONTROLLER_DATA_FETCH_INTERVAL;

		try {
			dataFetchInterval = Integer.parseInt(
					RemoteManager.getHadoopConfig(QueryIOConstants.CONTROLLER_DATA_FETCH_INTERVAL_KEY).getValue());
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}

		ControllerManager.setAllControllersDataFetchInterval(dataFetchInterval);
	}

	public static DWRResponse updateHadoopConfigs(ArrayList configKeys, ArrayList configValues) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		try {
			if (!isAdmin()) {
				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}
			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Update Hadoop Configuration Settings Requested.");
			connection = CoreDBManager.getQueryIODBConnection();
			HadoopConfigDAO.updateHadoopConfigDefaultValue(connection, configKeys, configValues);

			new Thread() {
				public void run() {
					RemoteManager.updateDataFetchInterval();
				}
			}.start();

			dwrResponse.setDwrResponse(true, "System configuration key added successfully.", 200);

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return dwrResponse;
	}

	public static ArrayList getNodeConfig(String nodeId) {

		// DO NOT PASS THIS CONFIG VALUES TO FILESYSTEM OBJECTS.

		Connection connection = null;
		ArrayList result = new ArrayList();
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList configs = QueryIOAgentManager.getAllNodeConfig(host, node);
			HadoopConfig config;
			ArrayList list;

			for (int i = 0; i < configs.size(); i++) {
				config = (HadoopConfig) configs.get(i);
				list = new ArrayList();

				if (config.getKey().equals(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY)) {
					config.setValue(SecurityHandler.decryptData(config.getValue().trim()));
				}

				list.add(config.getKey());
				// list.add("<input type=\"text\" id=\"" + config.getKey() +
				// "\" value=\"" + config.getValue() +
				// "\"
				// onchange=\"javascript:NN_Config.saveChangedValue(this);\">");
				list.add(config.getValue());
				list.add(config.getDescription());
				result.add(list);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return result;
	}

	public static DWRResponse updateDb(String nodeId, String dbName, String analyticsDbName) {

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList configKeys = new ArrayList();
			ArrayList configValues = new ArrayList();
			configKeys.add(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			configValues.add(dbName);
			configKeys.add(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
			configValues.add(analyticsDbName);

			DWRResponse dwrResponse = updateNodeConfig(nodeId, configKeys, configValues);

			NodeDAO.getAllNameNodesDBMapped(connection);

			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception in updating BigQuery DB Mapping: " + e.getLocalizedMessage(), e);
			DWRResponse dwrResponse = new DWRResponse();
			dwrResponse.setDwrResponse(false, QueryIOConstants.CONFIGURATION_UPDATE_FAILED
					+ " Exception in updating BigQuery DB Mapping: " + e.getMessage(), 200);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection: " + e.getLocalizedMessage(), e);
			}
		}
	}

	public static DWRResponse deleteConfigurationKey(String nodeId, String configKeys) {

		DWRResponse dwrResponse = new DWRResponse();
		try {
			if (!isAdmin()) {
				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}
			// TODO:

			dwrResponse.setDwrResponse(true, "System configuration key deleted successfully.", 200);

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		} finally {
			try {
				// CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;

	}

	public static DWRResponse deleteSystemConfigurationKey(String configKeys) {

		DWRResponse dwrResponse = new DWRResponse();
		Connection connection = null;
		try {
			if (!isAdmin()) {
				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}

			connection = CoreDBManager.getQueryIODBConnection();
			HadoopConfigDAO.deleteHadoopConfigValue(connection, configKeys);
			dwrResponse.setDwrResponse(true, "System configuration key deleted successfully.", 200);

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;

	}

	public static DWRResponse updateDbAssociated(String nodeId, String dbName, boolean isMetadata) {

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList configKeys = new ArrayList();
			ArrayList configValues = new ArrayList();
			if (isMetadata)
				configKeys.add(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			else
				configKeys.add(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
			configValues.add(dbName);
			DWRResponse dwrResponse = updateNodeConfig(nodeId, configKeys, configValues);

			NodeDAO.getAllNameNodesDBMapped(connection);

			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception in updating BigQuery DB Mapping: " + e.getLocalizedMessage(), e);
			DWRResponse dwrResponse = new DWRResponse();
			dwrResponse.setDwrResponse(false, QueryIOConstants.CONFIGURATION_UPDATE_FAILED
					+ " Exception in updating BigQuery DB Mapping: " + e.getMessage(), 200);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection: " + e.getLocalizedMessage(), e);
			}
		}
	}

	public static DWRResponse updateNodeConfig(String nodeId, ArrayList configKeys, ArrayList configValues) {

		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setDwrResponse(false, "No changes found in configuration settings.", 500);

		if (configKeys.size() != 0) {
			Connection connection = null;
			try {
				if (!isAdmin()) {
					dwrResponse.setResponseCode(401);
					dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
					return dwrResponse;
				}
				connection = CoreDBManager.getQueryIODBConnection();
				Node n = NodeDAO.getNode(connection, nodeId);
				Host h = HostDAO.getHostDetail(connection, n.getHostId());

				// JMX Port Update handling
				String key = "";
				if (n.getNodeType().equals(QueryIOConstants.NAMENODE)) {
					key = QueryIOConstants.NAMENODE_OPTS_KEY;
				} else if (n.getNodeType().equals(QueryIOConstants.DATANODE)) {
					key = QueryIOConstants.DATANODE_OPTS_KEY;
				} else if (n.getNodeType().equals(QueryIOConstants.SECONDARYNAMENODE)) {
					key = QueryIOConstants.SECONDARYNAMENODE_OPTS_KEY;
				} else if (n.getNodeType().equals(QueryIOConstants.JOURNALNODE)) {
					key = QueryIOConstants.JOURNALNODE_OPTS_KEY;
				} else if (n.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)) {
					key = QueryIOConstants.RESOURCEMANAGER_OPTS_KEY;
				} else if (n.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {
					key = QueryIOConstants.NODEMANAGER_OPTS_KEY;
				}
				if (configKeys.indexOf(key) != -1) {
					String[] args = ((String) configValues.get(configKeys.indexOf(key))).split(" ");
					for (int var = 0; var < args.length; var++) {
						if (args[var].startsWith("-Dcom.sun.management.jmxremote.port=")) {
							String jmxPort = args[var].substring(args[var].indexOf("=") + 1);
							NodeDAO.updateJMXPort(connection, nodeId, jmxPort);
						}
					}
				}

				int encryptionKeyPropIndex = configKeys.indexOf(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY);
				if (encryptionKeyPropIndex != -1) {
					configValues.set(encryptionKeyPropIndex,
							SecurityHandler.encryptData(((String) configValues.get(encryptionKeyPropIndex)).trim()));
				}

				if (configKeys.contains(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID)) {
					int index = configKeys.indexOf(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
					String dbSource = (String) configValues.get(index);
					ArrayList arr = NodeDAO.getAllNameNodesDBMapped(connection);
					if ((arr != null) && (arr.contains(nodeId)))
						NodeDAO.updateNameNodeDBMapping(connection, nodeId, dbSource);
					else
						NodeDAO.insertNameNodeDBMapping(connection, nodeId, dbSource, "");
				}
				if (configKeys.contains(QueryIOConstants.ANALYTICS_DB_DBSOURCEID)) {
					int index = configKeys.indexOf(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
					String dbSource = (String) configValues.get(index);
					ArrayList arr = NodeDAO.getAllNameNodesDBMapped(connection);
					if ((arr != null) && (arr.contains(nodeId)))
						NodeDAO.updateNameNodeAnalyticsDBMapping(connection, nodeId, dbSource);
					else
						NodeDAO.insertNameNodeDBMapping(connection, nodeId, "", dbSource);
				}

				if (configKeys.contains(QueryIOConstants.QUERYIO_DATANODE_DATA_DISK)) {
					String diskName = (String) configValues
							.get(configKeys.indexOf(QueryIOConstants.QUERYIO_DATANODE_DATA_DISK));
					VolumeDAO.updateDisks(connection, nodeId, diskName);
				}
				if (configKeys.contains(QueryIOConstants.QUERYIO_NAMENODE_DATA_DISK)) {
					String diskName = (String) configValues
							.get(configKeys.indexOf(QueryIOConstants.QUERYIO_NAMENODE_DATA_DISK));
					VolumeDAO.updateDisks(connection, nodeId, diskName);
				}
				if (configKeys.contains(DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY)) {
					String volumePath = (String) configValues
							.get(configKeys.indexOf(DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY));
					VolumeDAO.updatePath(connection, nodeId, volumePath);
				}
				if (configKeys.contains(DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY)) {
					String volumePath = (String) configValues
							.get(configKeys.indexOf(DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY));
					VolumeDAO.updatePath(connection, nodeId, volumePath);
				}

				AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
						.info("Update Hadoop Configuration Settings Requested for " + n.getNodeType() + " on host "
								+ h.getHostIP());
				QueryIOResponse response = QueryIOAgentManager.setAllNodeConfig(h, n, configKeys, configValues);

				if (!response.isSuccessful()) {
					dwrResponse.setDwrResponse(response.isSuccessful(), response.getResponseMsg(), 200);
					return dwrResponse;
				}

				if (configKeys.indexOf(QueryIOConstants.DATANODE_OPTS_KEY) != -1
						|| configKeys.indexOf(QueryIOConstants.NAMENODE_OPTS_KEY) != -1
						|| configKeys.indexOf(QueryIOConstants.HADOOP_LOG_DIR_KEY) != -1
						|| configKeys.indexOf(QueryIOConstants.HADOOP_PID_DIR_KEY) != -1
						|| configKeys.indexOf(QueryIOConstants.HADOOP_HEAP_SIZE) != -1
						|| configKeys.indexOf(QueryIOConstants.HADOOP_OPTS_KEY) != -1) {
					response = QueryIOAgentManager.updateHadoopEnv(h, n);
				}

				if (!response.isSuccessful()) {
					dwrResponse.setDwrResponse(response.isSuccessful(), response.getResponseMsg(), 200);
					return dwrResponse;
				}

				if (configKeys.indexOf(QueryIOConstants.YARN_HEAP_SIZE) != -1
						|| configKeys.indexOf(QueryIOConstants.YARN_LOG_DIR_KEY) != -1
						|| configKeys.indexOf(QueryIOConstants.YARN_PID_DIR_KEY) != -1
						|| configKeys.indexOf(QueryIOConstants.RESOURCEMANAGER_OPTS_KEY) != -1
						|| configKeys.indexOf(QueryIOConstants.NODEMANAGER_OPTS_KEY) != -1
						|| configKeys.indexOf(QueryIOConstants.YARN_OPTS_KEY) != -1) {
					response = QueryIOAgentManager.updateYarnEnv(h, n);
				}

				if (!response.isSuccessful()) {
					dwrResponse.setDwrResponse(response.isSuccessful(), response.getResponseMsg(), 200);
					return dwrResponse;
				}

				if (!n.getStatus().equals(QueryIOConstants.STATUS_STOPPED)) {
					n.setStatus(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION);
					NodeDAO.updateStatus(connection, n);
				}

				dwrResponse.setDwrResponse(true, QueryIOConstants.CONFIGURATION_UPDATE_SUCCESS, 200);
				return dwrResponse;

			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
				dwrResponse.setDwrResponse(false,
						QueryIOConstants.CONFIGURATION_UPDATE_FAILED + " Exception: " + e.getMessage(), 200);
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

	public static ArrayList getTreeDetails() {
		Connection connection = null;
		ArrayList tree = new ArrayList();

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList nodeIds = NodeDAO.getAllNodeIds(connection);
			ArrayList volumes = null;
			Volume volume = null;
			TreeBean bean;
			String nodeId;
			Node node;
			Iterator volumeIterator = null;

			Iterator hostIterator = nodeIds.iterator();
			while (hostIterator.hasNext()) {
				nodeId = (String) hostIterator.next();
				node = NodeDAO.getNode(connection, nodeId);
				volumes = VolumeDAO.getAllVolumes(connection, nodeId);

				if (node.getNodeType().equals(QueryIOConstants.DATANODE)) {
					bean = new TreeBean();
					bean.setNodeId(nodeId);

					volumeIterator = volumes.iterator();
					while (volumeIterator.hasNext()) {
						volume = (Volume) volumeIterator.next();
						bean.addChild(volume);
					}
					tree.add(bean);
				}

			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection", e);
			}
		}
		return tree;
	}

	public static DWRResponse runFSCKCommand(String nodeId) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			if (!isAdmin()) {
				dwrResponse.setResponseCode(401);
				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Start FSCK requested for host " + host.getHostIP());
			HadoopService hadoopService = new HadoopService();
			hadoopService.setNodeId(nodeId);
			hadoopService.setTimeOfCall(timestamp);
			hadoopService.setType(QueryIOConstants.HADOOP_SERVICE_FSCK);
			hadoopService.setStatus(QueryIOConstants.PROCESS_STATUS_INPROGRESS);
			HadoopServiceDAO.insert(connection, hadoopService);

			QueryIOAgentManager.runFSCKCommand(host, node, dwrResponse);

			String data = dwrResponse.getResponseMessage();

			String tPath = "/fsck_" + timestamp.getDate() + "_" + (timestamp.getMonth() + 1) + "_"
					+ (1900 + timestamp.getYear()) + "_" + timestamp.getHours() + "_" + timestamp.getMinutes() + ".txt";

			String path = EnvironmentalConstants.getReportsDirectory() + tPath;

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("path: " + path);

			File file = new File(path);
			file.createNewFile();

			FileWriter fstream = null;
			BufferedWriter out = null;
			try {
				fstream = new FileWriter(path);
				out = new BufferedWriter(fstream);
				out.write(data);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
				if (fstream != null) {
					try {
						fstream.close();
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
			}

			if (dwrResponse.isTaskSuccess()) {
				hadoopService.setStatus(QueryIOConstants.PROCESS_STATUS_COMPLETED);
				hadoopService.setOutputFilePath("Reports" + tPath);
				HadoopServiceDAO.update(connection, hadoopService);
				dwrResponse.setResponseMessage(QueryIOConstants.HEALTH_CHECK_SUCCESS);
				return dwrResponse;
			} else {
				hadoopService.setStatus(QueryIOConstants.PROCESS_STATUS_FAILED);
				HadoopServiceDAO.update(connection, hadoopService);
				dwrResponse.setResponseMessage(QueryIOConstants.HEALTH_CHECK_FAILED);
				return dwrResponse;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static DWRResponse runBalancer(String nodeId) {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			if (!isAdmin()) {
				dwrResponse.setResponseCode(401);
				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Start Balancer requested for host " + host.getHostIP());
			HadoopService hadoopService = new HadoopService();
			hadoopService.setNodeId(nodeId);
			hadoopService.setTimeOfCall(timestamp);
			hadoopService.setType(QueryIOConstants.HADOOP_SERVICE_BALANCER);
			hadoopService.setStatus(QueryIOConstants.PROCESS_STATUS_INPROGRESS);
			HadoopServiceDAO.insert(connection, hadoopService);

			QueryIOAgentManager.startBalancer(host, node, dwrResponse);

			String tPath = "/balancer_" + timestamp.getDate() + "_" + (timestamp.getMonth() + 1) + "_"
					+ (1900 + timestamp.getYear()) + "_" + timestamp.getHours() + "_" + timestamp.getMinutes() + ".txt";

			String data = dwrResponse.getResponseMessage();
			String path = EnvironmentalConstants.getReportsDirectory() + tPath;

			File file = new File(path);
			file.createNewFile();

			FileWriter fstream = null;
			BufferedWriter out = null;
			try {
				fstream = new FileWriter(path);
				out = new BufferedWriter(fstream);
				out.write(data);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
				if (fstream != null) {
					try {
						fstream.close();
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
			}

			if (dwrResponse.isTaskSuccess()) {
				hadoopService.setStatus(QueryIOConstants.PROCESS_STATUS_COMPLETED);
				hadoopService.setOutputFilePath("Reports" + tPath);
				HadoopServiceDAO.update(connection, hadoopService);
				dwrResponse.setResponseMessage(QueryIOConstants.BALANCER_RUN_SUCCESS);
				return dwrResponse;

			} else {
				hadoopService.setStatus(QueryIOConstants.PROCESS_STATUS_FAILED);
				HadoopServiceDAO.update(connection, hadoopService);
				dwrResponse.setResponseMessage(QueryIOConstants.BALANCER_RUN_FAILED);
				return dwrResponse;
			}

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setResponseCode(500);
			dwrResponse.setTaskSuccess(false);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static ArrayList getHadoopServiceLogs() {
		ArrayList list = new ArrayList();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			list = HadoopServiceDAO.getAll(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return list;
	}

	public static Host getHost(int hostID) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return HostDAO.getHostDetail(connection, hostID);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static NotifyBean getNotificationSettings() {

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NotifyDAO.getNotificationSettings(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static boolean updateNotificationSettings(NotifyBean bean) {
		Connection connection = null;
		try {
			if (!isAdmin()) {
				return false;
			}
			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Update Notification Settings Requested.");
			connection = CoreDBManager.getQueryIODBConnection();

			NotifyDAO.updateNotificationSettings(connection, bean);
			return true;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return false;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static ArrayList getNameNodes() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NodeDAO.getAllNameNodes(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getUserDetails() {

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return UserDAO.getUsersDetails(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static SummaryTable getAllUsers() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return UserDAO.getUsers(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static int getUserCount() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return UserDAO.getUserCount(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return 0;
	}

	public static DWRResponse insertUser(String userRole, String userName, String firstName, String lastName,
			String password, String email, String[] groupNames, String defaultGroup) {
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setDwrResponse(false, "", 403);
		dwrResponse.setId("0");
		Connection connection = null;
		try {
			if (!isAdmin()) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}

			if (groupNames == null || defaultGroup == null) {
				dwrResponse.setDwrResponse(false, "You must provide required details", 403);
				return dwrResponse;
			}

			boolean contains = false;
			for (int i = 0; i < groupNames.length; i++) {
				if (groupNames[i].equals(defaultGroup)) {
					contains = true;
				}
			}

			if (!contains) {
				dwrResponse.setDwrResponse(false, "User must belong to the default group", 403);
				return dwrResponse;
			}

			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Insert user requested with username:" + userName);
			connection = CoreDBManager.getQueryIODBConnection();
			User user = new User();
			user.setUserName(userName);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setPassword(password);
			user.setEmail(email);

			UserDAO.insertUser(connection, user, userRole, dwrResponse);

			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			for (String group : groupNames) {
				if (group.equals(QueryIOConstants.DEFAULT_GROUP_NAME) && !userRole.equals("Admin")) {
					continue;
				}

				if (group.equals(defaultGroup)) {
					UserGroupDAO.addUserToGroup(connection, userName, group, true);
				} else {
					UserGroupDAO.addUserToGroup(connection, userName, group, false);
				}
			}

			/*
			 * Inform all the namenodes and queryio services about the updates.
			 * It's CRUCIAL.
			 */
			UGIUpdater.sendUserInformationUpdates();
			UGIUpdater.sendUserGroupInformationUpdates();
			/*  */

			dwrResponse.setDwrResponse(true, "User added successfully", 200);
			return dwrResponse;

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static DWRResponse updatePassword(int id, String oldPassword, String newPassword) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		try {
			if (!(isAdmin() || isCurrentUser(RemoteManager.getLoggedInUser()))) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}

			connection = CoreDBManager.getQueryIODBConnection();

			UserDAO.updatePassword(connection, id, oldPassword, newPassword, dwrResponse);

			/*
			 * Inform all the namenodes and queryio services about the updates.
			 * It's CRUCIAL.
			 */
			UGIUpdater.sendUserInformationUpdates();
			UGIUpdater.sendUserGroupInformationUpdates();
			/*  */

		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error updating password", e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;
	}

	public static DWRResponse updateUser(int id, String userName, String firstName, String lastName, String email,
			String[] groupNames, String defaultGroup) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setId(id + "");
		dwrResponse.setTaskSuccess(false);
		try {
			if (!(isAdmin() || isCurrentUser(userName))) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}

			if (groupNames == null || defaultGroup == null) {
				dwrResponse.setDwrResponse(false, "You must provide required details", 403);
				return dwrResponse;
			}

			boolean contains = false;
			for (int i = 0; i < groupNames.length; i++) {
				if (groupNames[i].equals(defaultGroup)) {
					contains = true;
				}
			}

			if (!contains) {
				dwrResponse.setDwrResponse(false, "User must belong to the default group", 403);
				return dwrResponse;
			}

			connection = CoreDBManager.getQueryIODBConnection();
			User user = new User();

			user.setId(id);
			user.setUserName(userName);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);

			UserDAO.updateUser(connection, user, dwrResponse);

			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			String userRole = UserDAO.getRole(connection, userName);

			UserGroupDAO.deleteUserFromAllGroups(connection, id);
			for (String group : groupNames) {
				if (group.equals(QueryIOConstants.DEFAULT_GROUP_NAME) && !userRole.equals("Admin")) {
					continue;
				}

				if (group.equals(defaultGroup)) {
					UserGroupDAO.addUserToGroup(connection, userName, group, true);
				} else {
					UserGroupDAO.addUserToGroup(connection, userName, group, false);
				}
			}

			/*
			 * Inform all the namenodes and queryio services about the updates.
			 * It's CRUCIAL.
			 */
			UGIUpdater.sendUserInformationUpdates();
			UGIUpdater.sendUserGroupInformationUpdates();
			/*  */

			dwrResponse.setDwrResponse(true, "User updated successfully", 200);
			return dwrResponse;
		} catch (Exception e) {
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static JSONObject getCurrentUserDetail(String userName) {
		JSONObject object = new JSONObject();

		object.put("time", getServerTime());

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			object.put("user", UserDAO.getUserDetail(connection, userName));
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return object;
	}

	public static User getUserDetail(int userId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return UserDAO.getUserDetail(connection, userId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static DWRResponse deleteUsersFromList(ArrayList userList) {
		DWRResponse resp = null;
		for (int i = 0; i < userList.size(); i++) {
			resp = deleteUser(Integer.parseInt((String) userList.get(i)));
			if (!resp.isTaskSuccess()) {
				return resp;
			}
		}
		return resp;
	}

	public static DWRResponse deleteUser(int userId) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setDwrResponse(false, "", 403);
		dwrResponse.setId(String.valueOf(userId));
		try {
			if (!isAdmin()) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}
			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Delete user requested for userId:" + userId);
			connection = CoreDBManager.getQueryIODBConnection();

			UserDAO.deleteUser(connection, userId);
			UserGroupDAO.deleteUserFromAllGroups(connection, userId);

			/*
			 * Inform all the namenodes and queryio services about the updates.
			 * It's CRUCIAL.
			 */
			UGIUpdater.sendUserInformationUpdates();
			UGIUpdater.sendUserGroupInformationUpdates();
			/*  */

			dwrResponse.setDwrResponse(true, QueryIOConstants.USER_DELETED, 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getLocalizedMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;
	}

	public static ArrayList getAllHostDetails() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return HostDAO.getAllHostDetails(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getAllNodeDetails(String hostname) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NodeDAO.getAllNodesForHost(connection, HostDAO.getHostDetail(connection, hostname).getId());
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getAllNameNodeDetails() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NodeDAO.getAllNodesForType(connection, QueryIOConstants.NAMENODE);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getAllDataNodeDetails() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NodeDAO.getAllNodesForType(connection, QueryIOConstants.DATANODE);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static Map<String, Integer> getAllNodesCount() {

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NodeDAO.getAllNodesCount(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static DWRResponse insertHost(String hostIP, String userName, String password, String sshPrivateKeyFile,
			String dirPath, String rackName, String agentPort, String javaHomePath, String port) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(hostIP);
		try {
			if (!isAdmin()) {
				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}

			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Insert host requested for hostname:" + hostIP);

			connection = CoreDBManager.getQueryIODBConnection();

			if (!dirPath.endsWith(File.separator))
				dirPath = dirPath + File.separator;
			dirPath += "QueryIOPackage" + File.separator;

			Host host = new Host();
			host.setHostIP(hostIP);
			host.setInstallDirPath(dirPath);
			host.setRackName(rackName);
			host.setAgentPort(agentPort);
			host.setWindows(isWindows(host, userName, password, port, sshPrivateKeyFile));

			if (HostDAO.isHostAlreadyAdded(connection, host.getHostIP())) {
				dwrResponse.setResponseMessage(QueryIOConstants.HOST_ALREADY_PRESENT);
				dwrResponse.setResponseCode(302); // This status code is being
													// used by autmatic cluster
													// setup.
				return dwrResponse;
			}

			// installation of hadoop failed
			if (!OneTimeConfig.installHadoop(host, userName, password, sshPrivateKeyFile, javaHomePath, port, false)) {
				dwrResponse.setResponseMessage(QueryIOConstants.HADOOP_INSTALLATION_FAILS);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}
			try {
				HostDAO.insertHost(connection, host);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
				dwrResponse.setResponseMessage(QueryIOConstants.INSERTION_FAILURE);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			host = HostDAO.getHostDetail(connection, hostIP);

			ArrayList colNames = new ArrayList();
			ArrayList colTypes = new ArrayList();

			colNames.add(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
			colTypes.add(QueryIOConstants.DATATYPE_TIMESTAMP);

			ArrayList systemAttributes = MonitorDAO.getHostSystemAttributes(connection);
			for (int j = 0; j < systemAttributes.size(); j++) {
				colNames.add(((SystemAttribute) systemAttributes.get(j)).getColumnName());
				colTypes.add(((SystemAttribute) systemAttributes.get(j)).getDataType());
			}
			MonitorDAO.createHostMonitorTable(connection, host.getId(), colNames, colTypes);

			colNames.clear();
			colTypes.clear();

			ArrayList liveAttributes = MonitorDAO.getHostLiveAttributes(connection);
			for (int j = 0; j < liveAttributes.size(); j++) {
				colNames.add(((LiveAttribute) liveAttributes.get(j)).getColumnName());
				colTypes.add(((LiveAttribute) liveAttributes.get(j)).getDataType());
			}
			MonitorDAO.createHostSummaryTable(connection, host.getId(), colNames, colTypes);

			dwrResponse = startHost(host.getId(), userName, password, sshPrivateKeyFile, port);
			if (dwrResponse.isTaskSuccess()) {
				dwrResponse.setResponseMessage(QueryIOConstants.HOST_ADDED_SUCCESS);
				updateMetadataFile(QueryIOConstants.METADATA_FILE_HOSTS, hostIP, dirPath, true);
			}
			dwrResponse.setId(hostIP);
			try {
				String hadoopHome = host.getInstallDirPath();
				if (hadoopHome.endsWith("/"))
					hadoopHome = hadoopHome.substring(0, hadoopHome.length() - 1);
				QueryIOAgentManager.updateStartStopScripts(host, hadoopHome);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error while updating start stop scripts.", e);
				dwrResponse.setResponseMessage("Error while updating start stop scripts.");
				dwrResponse.setResponseCode(500);
				dwrResponse.setTaskSuccess(false);
				return dwrResponse;
			}
			try {
				String hadoopHome = host.getInstallDirPath();
				if (hadoopHome.endsWith("/"))
					hadoopHome = hadoopHome.substring(0, hadoopHome.length() - 1);
				QueryIOAgentManager.updateHadoopPath(host, host.getInstallDirPath());
			} catch (Exception e) {
				AppLogger.getLogger()
						.fatal("Error while updating hadoop directory in core-site.xml file and hdfs-site.xml.", e);
				dwrResponse.setResponseMessage(
						"Error while updating hadoop directory in core-site.xml file and hdfs-site.xml.");
				dwrResponse.setResponseCode(500);
				dwrResponse.setTaskSuccess(false);
				return dwrResponse;
			}
			try {
				QueryIOAgentManager.updateJavaHome(host, javaHomePath);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error while updating JavaHome.", e);
				dwrResponse.setResponseMessage("Error while updating JavaHome.");
				dwrResponse.setResponseCode(500);
				dwrResponse.setTaskSuccess(false);
				return dwrResponse;
			}
			try {
				QueryIOAgentManager.updateLoggerPropertiesFile(host);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error while updating Logger properties.", e);
				dwrResponse.setResponseMessage("Error while updating Logger properties.");
				dwrResponse.setResponseCode(500);
				dwrResponse.setTaskSuccess(false);
				return dwrResponse;
			}
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

	}

	private static boolean isWindows(Host host, String userName, String password, String port, String sshPrivateKey)
			throws Exception {
		SSHRemoteExecution remoteExec = null;
		Session session = null;
		try {
			remoteExec = new SSHRemoteExecution();

			if (password != null) {
				session = remoteExec.createSession(host.getHostIP(), userName, password, Integer.parseInt(port));
			} else if (sshPrivateKey != null) {
				session = remoteExec.createSessionWithPrivateKeyFile(host.getHostIP(), userName, sshPrivateKey,
						Integer.parseInt(port));
			} else {
				throw new Exception("Session could not be created");
			}
			session.connect(StartupParameters.getSessionTimeout());
			return OneTimeConfig.isWindows(remoteExec, session);
		} finally {
			try {
				if (remoteExec != null)
					remoteExec.closeSession(session);
			} catch (Exception e) {
				// ignore
			}
		}
	}

	public static DWRResponse insertHostAutomatation(String hostIP, String userName, String password,
			String sshPrivateKeyFile, String rackName, String port, String installationPath, String queryIOAgentPort,
			String id) {

		DWRResponse response;
		if (installationPath == null || installationPath.trim().isEmpty()) {
			AppLogger.getLogger().debug("installation path not recieved from UI. Will be installed in User Home.");
			installationPath = getUserHomeDirectory(hostIP, userName, password, sshPrivateKeyFile,
					Integer.parseInt(port.trim()));
		}

		if (installationPath != null) {
			installationPath = installationPath.replaceAll("[\n\r]", "");
		}

		AppLogger.getLogger().debug("userName : " + userName);
		AppLogger.getLogger().debug("Password : " + password);
		AppLogger.getLogger().debug("installationPath : " + installationPath);
		AppLogger.getLogger().debug("queryIOAgentPort : " + queryIOAgentPort);
		AppLogger.getLogger().debug("rackName : " + rackName);
		String javaHome = getJavaHome(hostIP, userName, password, sshPrivateKeyFile, Integer.parseInt(port.trim()));

		if (installationPath == null || javaHome == null) {
			response = new DWRResponse();
			response.setTaskSuccess(false);
			if (installationPath == null)
				response.setResponseMessage("Authentication Failure.");
			else if (javaHome == null)
				response.setResponseMessage("JavaHome not found.");
		} else {
			response = insertHost(hostIP, userName, password, sshPrivateKeyFile, installationPath, rackName,
					queryIOAgentPort, javaHome, port);
		}
		response.setId(id);
		return response;
	}

	public static DWRResponse startHost(int id, String userName, String password, String sshPrivateKeyFile,
			String port) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		try {
			if (isNonAdminAndDemo(null)) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, id);
			QueryIOAgentManager.startQueryIOAgent(host, userName, password, sshPrivateKeyFile, port, false);

			startMonitor(connection, host);
			host = HostDAO.getHostDetail(connection, id);
			String status = host.getStatus();
			int attempts = 0;
			while (!status.equals(QueryIOConstants.STATUS_STARTED) && attempts < 120) {

				HostDAO.setHostMonitor(connection, host.getId(), true);

				Thread.sleep(1000);
				attempts++;
				host = HostDAO.getHostDetail(connection, id);
				status = host.getStatus();
			}

			if (status.equals(QueryIOConstants.STATUS_STARTED)) {

				dwrResponse.setDwrResponse(true, "Agent Monitor has started.", 200);
			} else {
				dwrResponse.setDwrResponse(false, "Agent could not be started successfully.", 500);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);

		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return dwrResponse;
	}

	public static DWRResponse setHostMonitor(int id, boolean monitor) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		try {
			if (isNonAdminAndDemo(null)) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, id);

			host = HostDAO.getHostDetail(connection, id);

			if (monitor && host.getStatus().startsWith(QueryIOConstants.STATUS_STOPPED)) {
				dwrResponse.setDwrResponse(false,
						"Agent on specified host is not running. "
								+ "To start host monitoring, first start agent on that machine and then start host monitor.",
						401);
				return dwrResponse;
			}

			if (monitor) {
				ControllerManager.stopHostMonitorController(host.getId());
				ControllerManager.startHostMonitorController(connection, host);
			} else {
				ControllerManager.stopHostMonitorController(host.getId());
			}

			HostDAO.setHostMonitor(connection, host.getId(), monitor);

			dwrResponse.setDwrResponse(true, "Monitor " + (monitor ? "started" : "stopped") + " for host", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);

		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return dwrResponse;
	}

	public static DWRResponse stopHost(int id, String userName, String password, String sshPrivateKeyFile, String port,
			boolean isLocal) {
		DWRResponse dwrResponse = new DWRResponse();
		Connection connection = null;
		try {
			if (isNonAdminAndDemo(null)) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, id);

			try {
				QueryIOAgentManager.stopMonitoring(host);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}

			ControllerManager.stopHostMonitorController(host.getId());

			if (isLocal) {
				QueryIOAgentManager.stopQueryIOAgent(host, "", "", "", "", isLocal);
			} else {
				QueryIOAgentManager.stopQueryIOAgent(host, userName, password, sshPrivateKeyFile, port, isLocal);
			}

			host.setStatus(QueryIOConstants.STATUS_STOPPED);
			HostDAO.updateStatus(connection, host);

			HostDAO.setHostMonitor(connection, host.getId(), false);

			dwrResponse.setDwrResponse(true, "Agent Monitor has stopped.", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);

		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;
	}

	public static void startMonitor(Connection connection, Host host) {
		try {
			ControllerManager.startHostMonitorController(connection, host);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Monitor started for " + host.getHostIP());
		} catch (Exception e) {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Error starting monitor for " + host.getHostIP());
		}
	}

	public static void stopMonitor(Host host) {
		try {
			ControllerManager.stopHostMonitorController(host.getId());
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Monitor stopped for " + host.getHostIP());
		} catch (Exception e) {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Error stopping monitor for " + host.getHostIP());
		}
	}

	public static DWRResponse deleteHost(int hostID, String userName, String password, String privateKey, String port,
			boolean isLocal) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(Integer.toString(hostID));
		try {

			if (isNonAdminAndDemo(null)) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}

			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, hostID);

			if (RemoteManager.isHostContainsNode(host.getHostIP())) {
				dwrResponse.setResponseCode(401);
				dwrResponse.setResponseMessage(
						"There are node(s) configured on this host. Please delete all those node(s) before removing host.");
				return dwrResponse;
			}

			stopMonitor(host);
			OneTimeConfig.uninstallHost(host, userName, password, privateKey, port, isLocal);
			HostDAO.deleteHost(connection, host.getHostIP());
			MonitorDAO.deleteHostMonitorTable(connection, host.getId());
			MonitorDAO.deleteHostSummaryTable(connection, host.getId());
			ConsolidationUtility.clearLastConsolidationData(connection, String.valueOf(host.getId()));

			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseMessage(QueryIOConstants.HOST_DELETED_SUCCESS);
			dwrResponse.setResponseCode(200);
			updateMetadataFile(QueryIOConstants.METADATA_FILE_HOSTS, host.getHostIP(), "", false);
			return dwrResponse;

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setResponseCode(501);
			return dwrResponse;

		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static DWRResponse deleteNameNode(String nodeID, boolean isRestartDataNode) {
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeID);
		if (!isAdmin()) {
			dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
			dwrResponse.setResponseCode(401);
			return dwrResponse;
		}

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeID);

			String nnId = null;
			if ((nnId = HAStatusDAO.getActiveNodeId(connection, node.getId())) != null) {
				HATransitionManager.disableHA(connection, nnId, dwrResponse);
				HAStatusDAO.removeEntry(connection, nnId);
			} else if (NodeDAO.getAllNameNodes(connection).size() > 1) {

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Removing configuration for " + node.getId());
				ArrayList datanodes = NodeDAO.getAllDatanodes(connection);
				ArrayList namenodes = NodeDAO.getAllNameNodes(connection);
				namenodes.remove(node);

				ArrayList unsetKeys = new ArrayList();
				unsetKeys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + node.getId());
				unsetKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + node.getId());
				unsetKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + node.getId());

				String nameservices = "";
				for (int i = 0; i < namenodes.size(); i++) {
					if (i != 0) {
						nameservices += ", ";
					}
					Node namenode = (Node) namenodes.get(i);
					nameservices += namenode.getId();
				}

				ArrayList keys = new ArrayList();
				keys.add(DFSConfigKeys.DFS_NAMESERVICES);
				ArrayList values = new ArrayList();
				values.add(nameservices);
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Unset Keys: " + unsetKeys);
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Key: " + keys + ", Value: " + values);
				for (int i = 0; i < namenodes.size(); i++) {
					Node namenode = (Node) namenodes.get(i);
					Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
					QueryIOResponse resp = QueryIOAgentManager.unsetConfiguration(namenodeHost, namenode, unsetKeys,
							"hdfs-site.xml");
					dwrResponse.setDwrResponse(resp.isSuccessful(), resp.getResponseMsg(),
							resp.isSuccessful() ? 200 : 500);
					resp = QueryIOAgentManager.setAllNodeConfig(namenodeHost, namenode, keys, values);
					dwrResponse.setResponseMessage(resp.getResponseMsg());
					dwrResponse.setTaskSuccess(resp.isSuccessful());
				}

				for (int i = 0; i < datanodes.size(); i++) {
					Node datanode = (Node) datanodes.get(i);
					Host datanodeHost = HostDAO.getHostDetail(connection, datanode.getHostId());
					QueryIOResponse resp = QueryIOAgentManager.unsetConfiguration(datanodeHost, datanode, unsetKeys,
							"hdfs-site.xml");
					dwrResponse.setDwrResponse(resp.isSuccessful(), resp.getResponseMsg(),
							resp.isSuccessful() ? 200 : 500);
					resp = QueryIOAgentManager.setAllNodeConfig(datanodeHost, datanode, keys, values);
					dwrResponse.setResponseMessage(resp.getResponseMsg());
					dwrResponse.setTaskSuccess(resp.isSuccessful());

					boolean isUpdateStatus = true;
					if (dwrResponse.isTaskSuccess()) {
						if (datanode.getStatus().equals(QueryIOConstants.STATUS_STARTED)) {
							if (isRestartDataNode) {
								DWRResponse dwrResp = stopNode(datanode.getId());
								if (dwrResp.isTaskSuccess()) {
									dwrResp = startNode(datanode.getId(), false);
									if (dwrResp.isTaskSuccess()) {
										dwrResponse.setResponseMessage(
												resp.getResponseMsg() + " DataNode restarted successfully.");
										dwrResponse.setTaskSuccess(true);
										isUpdateStatus = false;
									} else {
										dwrResponse.setResponseMessage(
												dwrResponse.getResponseMessage() + " " + dwrResp.getResponseMessage());
										dwrResponse.setTaskSuccess(false);
										isUpdateStatus = true;
									}
								} else {
									dwrResponse.setResponseMessage(
											dwrResponse.getResponseMessage() + " " + dwrResp.getResponseMessage());
									dwrResponse.setTaskSuccess(false);
									isUpdateStatus = true;
								}
							}
						}
						if (isUpdateStatus) {
							datanode.setStatus(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION);
							NodeDAO.updateStatus(connection, datanode);
						}
					}
				}

			} else if (NodeDAO.getAllDatanodes(connection).size() != 0) {
				dwrResponse.setResponseMessage(
						"Cluster contains other nodes, please delete all those nodes before deleting namenode.");
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}
			if (!node.getStatus().startsWith(QueryIOConstants.STATUS_STOPPED))
				stopNode(nodeID);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Delete " + node.getNodeType() + " requested for host " + host.getHostIP());
			QueryIOAgentManager.stopNode(host, node, dwrResponse);

			if (dwrResponse.isTaskSuccess()) {
				// Calculate and update table for Billing Invoice
				Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
				if (!BillingDAO.updateBillingReportEntry(connection, currentTimestamp)) {
					// Need to calculate startTime
					Timestamp start = null;

					Calendar cal = Calendar.getInstance(); // locale-specific
					cal.setTime(currentTimestamp);

					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);

					Timestamp tempTimestamp = new Timestamp(cal.getTimeInMillis() - 86400000);
					String id = (tempTimestamp.getYear() + 1900) + "_" + (tempTimestamp.getMonth() + 1) + "_"
							+ tempTimestamp.getDate();

					ps = DatabaseFunctions.getPreparedStatement(connection,
							QueryConstants.PREPARED_QRY_GET_BILLING_DATA_TIMESTAMP_FOR_ID);
					ps.setString(1, id);
					rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
					if (rs.next()) {
						start = rs.getTimestamp(ColumnConstants.COL_BILLING_REPORT_DATA_TIMESTAMP);
					} else {
						start = new Timestamp(currentTimestamp.getTime() - 86400000); // Back
																						// 1
																						// day.
					}
					BillingDAO.insertBillingReportEntry(connection, BillingDAO.calculateBillingData(connection,
							NodeDAO.getAllDatanodes(connection), start, currentTimestamp));
				}

				StatusDAO.deleteNodeData(connection, node.getId());
				ConsolidationUtility.clearLastConsolidationData(connection, nodeID);

				MonitorDAO.deleteNameNodeMonitorTable(connection, node.getId());
				MonitorDAO.deleteNameNodeSummaryTable(connection, node.getId());
				VolumeDAO.removeVolumes(connection, nodeID);

				NodeDAO.deleteNameNodeDBMapping(connection, node.getId());
				QueryIOServiceDAO.delete(connection, node.getId());
				HadoopServiceDAO.delete(connection, node.getId());
				TagParserDAO.deleteByNamenodeId(connection, node.getId());
				NodeDAO.delete(connection, node);

			} else {
				return dwrResponse;
			}
			dwrResponse.setDwrResponse(true, QueryIOConstants.NODE_DELETED_SUCCESS, 200);
			updateMetadataFile(QueryIOConstants.METADATA_FILE_NN, host.getHostIP(), "", false);
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Result Set could not be closed, Exception: " + e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Preapared Statement could not be closed, Exception: " + e.getMessage(), e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static DWRResponse deleteNode(String nodeID) {
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeID);
		if (!isAdmin()) {
			dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
			dwrResponse.setResponseCode(401);
			return dwrResponse;
		}

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String ip = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeID);
			if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
				String nnId = null;
				if ((nnId = HAStatusDAO.getActiveNodeId(connection, node.getId())) != null) {
					HATransitionManager.disableHA(connection, nnId, dwrResponse);
					HAStatusDAO.removeEntry(connection, nnId);
				} else if (NodeDAO.getAllNameNodes(connection).size() > 1) {

					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Removing configuration for " + node.getId());
					ArrayList datanodes = NodeDAO.getAllDatanodes(connection);
					ArrayList namenodes = NodeDAO.getAllNameNodes(connection);
					namenodes.remove(node);

					ArrayList unsetKeys = new ArrayList();
					unsetKeys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + node.getId());
					unsetKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + node.getId());
					unsetKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + node.getId());

					String nameservices = "";
					for (int i = 0; i < namenodes.size(); i++) {
						if (i != 0) {
							nameservices += ", ";
						}
						Node namenode = (Node) namenodes.get(i);
						nameservices += namenode.getId();
					}

					ArrayList keys = new ArrayList();
					keys.add(DFSConfigKeys.DFS_NAMESERVICES);
					ArrayList values = new ArrayList();
					values.add(nameservices);
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Unset Keys: " + unsetKeys);
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Key: " + keys + ", Value: " + values);
					for (int i = 0; i < namenodes.size(); i++) {
						Node namenode = (Node) namenodes.get(i);
						Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
						ip = namenodeHost.getHostIP();
						QueryIOResponse resp = QueryIOAgentManager.unsetConfiguration(namenodeHost, namenode, unsetKeys,
								"hdfs-site.xml");
						dwrResponse.setDwrResponse(resp.isSuccessful(), resp.getResponseMsg(),
								resp.isSuccessful() ? 200 : 500);
						resp = QueryIOAgentManager.setAllNodeConfig(namenodeHost, namenode, keys, values);
						dwrResponse.setDwrResponse(resp.isSuccessful(), resp.getResponseMsg(),
								resp.isSuccessful() ? 200 : 500);
						dwrResponse.setResponseMessage(resp.getResponseMsg());
						dwrResponse.setTaskSuccess(resp.isSuccessful());
					}

					for (int i = 0; i < datanodes.size(); i++) {
						Node datanode = (Node) datanodes.get(i);
						Host datanodeHost = HostDAO.getHostDetail(connection, datanode.getHostId());
						ip = datanodeHost.getHostIP();
						QueryIOResponse resp = QueryIOAgentManager.unsetConfiguration(datanodeHost, datanode, unsetKeys,
								"hdfs-site.xml");
						dwrResponse.setDwrResponse(resp.isSuccessful(), resp.getResponseMsg(),
								resp.isSuccessful() ? 200 : 500);
						resp = QueryIOAgentManager.setAllNodeConfig(datanodeHost, datanode, keys, values);
						dwrResponse.setResponseMessage(resp.getResponseMsg());
						dwrResponse.setTaskSuccess(resp.isSuccessful());
					}

				} else if (NodeDAO.getAllDatanodes(connection).size() != 0) {
					dwrResponse.setResponseMessage(
							"Cluster contains other nodes, please delete all those nodes before deleting namenode.");
					dwrResponse.setResponseCode(500);
					return dwrResponse;
				}
			}
			if (!node.getStatus().startsWith(QueryIOConstants.STATUS_STOPPED))
				stopNode(nodeID);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Delete " + node.getNodeType() + " requested for host " + host.getHostIP());
			QueryIOAgentManager.stopNode(host, node, dwrResponse);

			if (dwrResponse.isTaskSuccess()) {
				if (!node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)
						&& !node.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {
					// Calculate and update table for Billing Invoice
					Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
					if (!BillingDAO.updateBillingReportEntry(connection, currentTimestamp)) {
						// Need to calculate startTime
						Timestamp start = null;

						Calendar cal = Calendar.getInstance(); // locale-specific
						cal.setTime(currentTimestamp);

						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.MILLISECOND, 0);

						Timestamp tempTimestamp = new Timestamp(cal.getTimeInMillis() - 86400000);
						String id = (tempTimestamp.getYear() + 1900) + "_" + (tempTimestamp.getMonth() + 1) + "_"
								+ tempTimestamp.getDate();

						ps = DatabaseFunctions.getPreparedStatement(connection,
								QueryConstants.PREPARED_QRY_GET_BILLING_DATA_TIMESTAMP_FOR_ID);
						ps.setString(1, id);
						rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
						if (rs.next()) {
							start = rs.getTimestamp(ColumnConstants.COL_BILLING_REPORT_DATA_TIMESTAMP);
						} else {
							start = new Timestamp(currentTimestamp.getTime() - 86400000); // Back
																							// 1
																							// day.
						}
						BillingDAO.insertBillingReportEntry(connection, BillingDAO.calculateBillingData(connection,
								NodeDAO.getAllDatanodes(connection), start, currentTimestamp));
					}
				}

				StatusDAO.deleteNodeData(connection, node.getId());
				ConsolidationUtility.clearLastConsolidationData(connection, nodeID);

				if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {

					Host hostTemp = HostDAO.getHostDetail(connection, node.getHostId());
					ip = hostTemp.getHostIP();

					MonitorDAO.deleteNameNodeMonitorTable(connection, node.getId());
					MonitorDAO.deleteNameNodeSummaryTable(connection, node.getId());
					VolumeDAO.removeVolumes(connection, nodeID);

					NodeDAO.deleteNameNodeDBMapping(connection, node.getId());
					QueryIOServiceDAO.delete(connection, node.getId());
					HadoopServiceDAO.delete(connection, node.getId());
					NodeDAO.delete(connection, node);
				} else if (node.getNodeType().equals(QueryIOConstants.DATANODE)) {

					Host hostTemp = HostDAO.getHostDetail(connection, node.getHostId());
					ip = hostTemp.getHostIP();

					MonitorDAO.deleteDataNodeMonitorTable(connection, node.getId());
					MonitorDAO.deleteDataNodeSummaryTable(connection, node.getId());

					VolumeDAO.removeVolumes(connection, nodeID);

					ArrayList list = new ArrayList();
					list.add(DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY);
					ArrayList value = QueryIOAgentManager.getConfig(host, list, node, "hdfs-site.xml");
					String datanodeAddress = (String) value.get(0);
					NodeDAO.delete(connection, node);
					NodeDAO.deleteDecommissionNode(connection, node.getId());
					HadoopConfigManager.updateHostsList(connection, false);
					HadoopConfigManager.updateHostsExcludeList(connection, true);

				} else if (node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)) {

					Host hostTemp = HostDAO.getHostDetail(connection, node.getHostId());
					ip = hostTemp.getHostIP();

					MonitorDAO.deleteResourceManagerMonitorTable(connection, node.getId());
					MonitorDAO.deleteResourceManagerSummaryTable(connection, node.getId());
					VolumeDAO.removeVolumes(connection, nodeID);
					NodeDAO.delete(connection, node);
				} else if (node.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {

					Host hostTemp = HostDAO.getHostDetail(connection, node.getHostId());
					ip = hostTemp.getHostIP();

					MonitorDAO.deleteNodeManagerMonitorTable(connection, node.getId());
					MonitorDAO.deleteNodeManagerSummaryTable(connection, node.getId());
					NodeDAO.delete(connection, node);
				} else {
					NodeDAO.delete(connection, node);
				}

			} else {
				return dwrResponse;
			}
			dwrResponse.setDwrResponse(true, QueryIOConstants.NODE_DELETED_SUCCESS, 200);

			if (node.getNodeType().equals(QueryIOConstants.NAMENODE))
				updateMetadataFile(QueryIOConstants.METADATA_FILE_NN, ip, "", false);
			else if (node.getNodeType().equals(QueryIOConstants.DATANODE))
				updateMetadataFile(QueryIOConstants.METADATA_FILE_DN, ip, "", false);
			else if (node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER))
				updateMetadataFile(QueryIOConstants.METADATA_FILE_RM, ip, "", false);
			else if (node.getNodeType().equals(QueryIOConstants.NODEMANAGER))
				updateMetadataFile(QueryIOConstants.METADATA_FILE_NM, ip, "", false);

			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Result Set could not be closed, Exception: " + e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Preapared Statement could not be closed, Exception: " + e.getMessage(), e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

	}

	public static DWRResponse startQueryIOServices(String nodeId, boolean isHiveViewSelected) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Starting services for node: " + nodeId);
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		Connection connection = null;

		try {
			if (!isAdmin()) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());

			if (!node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
				dwrResponse.setDwrResponse(false,
						"Services are associated with NameNode. Please specify NameNode id to start services.", 401);
				return dwrResponse;
			}

			if (!(node.getStatus().equals(QueryIOConstants.STATUS_STARTED))
					|| (node.getStatus().equals(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION))) {
				dwrResponse.setDwrResponse(false,
						"NameNode is not running. Please start NameNode before starting services.", 401);
				return dwrResponse;
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Calling agent start services method");

			if (isHiveViewSelected) {
				QueryIOAgentManager.startHiveServer(host, node, dwrResponse);
			} else {
				QueryIOAgentManager.startQueryIOServices(host, node, dwrResponse);
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Agent start services method invoked");

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Task Success: " + dwrResponse.isTaskSuccess());

			if (dwrResponse.isTaskSuccess()) {
				if (isHiveViewSelected) {
					node.setHiveServiceStatus(QueryIOConstants.STATUS_STARTED);
					NodeDAO.updateHiveServiceStatus(connection, node);
					ControllerManager.startHiveServiceController(nodeId);
				} else {
					node.setServiceStatus(QueryIOConstants.STATUS_STARTED);
					NodeDAO.updateServiceStatus(connection, node);
					ControllerManager.startQueryIOServiceController(nodeId);
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("Services could not be started for nodeId: " + nodeId + " Exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}

		return dwrResponse;
	}

	public static DWRResponse stopQueryIOServices(String nodeId, boolean isHiveViewSelected) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Stopping services for node: " + nodeId);
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		Connection connection = null;

		try {
			if (!isAdmin()) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());

			if (!node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
				dwrResponse.setDwrResponse(false,
						"Services are associated with NameNode. Please specify NameNode id to stop services.", 401);
				return dwrResponse;
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Calling agent stop services method");

			if (isHiveViewSelected) {
				QueryIOAgentManager.stopHiveServer(host, node, dwrResponse);
			} else {
				QueryIOAgentManager.stopQueryIOServices(host, node, dwrResponse);
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Agent stop services method invoked");

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Task Success: " + dwrResponse.isTaskSuccess());

			if (dwrResponse.isTaskSuccess()) {
				if (isHiveViewSelected) {
					node.setHiveServiceStatus(QueryIOConstants.STATUS_STOPPED);
					NodeDAO.updateHiveServiceStatus(connection, node);
					ControllerManager.stopHiveServiceController(nodeId);
				} else {
					node.setServiceStatus(QueryIOConstants.STATUS_STOPPED);
					NodeDAO.updateServiceStatus(connection, node);
					ControllerManager.stopQueryIOServiceController(nodeId);
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(
					"QueryIO services could not be stopped for nodeId: " + nodeId + " Exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}

		return dwrResponse;
	}

	public static DWRResponse startNode(String nodeId, Boolean isEvaluation) {

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Starting node: " + nodeId);
		int mark = 0;
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			if (!isAdmin()) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			QueryIOResponse queryIOResponse = null;
			String nnId = null;
			queryIOResponse = QueryIOAgentManager.checkPortAvailability(host, node);
			if (!queryIOResponse.isSuccessful()) {
				dwrResponse.setDwrResponse(false, queryIOResponse.getResponseMsg(), 401);
				return dwrResponse;
			}
			if (node.getNodeType().equals(QueryIOConstants.NAMENODE) && HAStatusDAO.isHANode(connection, node.getId())
					&& (nnId = HAStatusDAO.getActiveNodeId(connection, node.getId())) != null) {
				if (NodeDAO.getNode(connection, nnId).getStatus().startsWith(QueryIOConstants.STATUS_STOPPED)) {
					dwrResponse.setDwrResponse(false,
							"Cannot start StandBy Namenode because Active NameNode of this cluster is not running. "
									+ "To start the HA-Enabled cluster, first start Active NameNode and then start StandBy NameNode",
							401);
					return dwrResponse;
				}

				queryIOResponse = QueryIOAgentManager.transitionNodeToActive(host, node, nnId);
				if (!queryIOResponse.isSuccessful()) {
					dwrResponse.setDwrResponse(false, queryIOResponse.getResponseMsg(), 401);
					return dwrResponse;
				}
			} else if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
				ResetRootPermissionsThread permissionResetter = new ResetRootPermissionsThread(host, node, "/",
						QueryIOConstants.DEFAULT_GROUP_NAME, (short) (777), false);
				permissionResetter.start();
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Calling agent start node method");

			QueryIOAgentManager.startNode(host, node, dwrResponse);

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Agent start node method invoked");

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Task Success: " + dwrResponse.isTaskSuccess());

			if (dwrResponse.isTaskSuccess()) {
				int dataFetchInterval = QueryIOConstants.DEFAULT_CONTROLLER_DATA_FETCH_INTERVAL;
				// try {
				// dataFetchInterval = Integer
				// .parseInt(ConfigurationManager.getConfiguration(connection,
				// nodeId).get(
				// QueryIOConstants.CONTROLLER_DATA_FETCH_INTERVAL_KEY));
				// } catch (Exception e) {
				// AppLogger.getLogger().fatal(e.getMessage(), e);
				// }

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Controller Data Fetch Interval: " + dataFetchInterval + " seconds.");

				try {
					if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
						ControllerManager.startNameNodeController(connection, host, node, dataFetchInterval);
					} else if (node.getNodeType().equals(QueryIOConstants.DATANODE)) {
						ControllerManager.startDataNodeController(connection, host, node, dataFetchInterval);
					} else if (node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)) {
						ControllerManager.startResourceManagerController(connection, host, node, dataFetchInterval);
					} else if (node.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {
						ControllerManager.startNodeManagerController(connection, host, node, dataFetchInterval);
					}
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger()
								.debug("Monitor started for " + node.getNodeType() + " on " + host.getHostIP());

					NodeDAO.setNodeMonitor(connection, nodeId, true);
				} catch (Exception e) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Monitor could not be started for " + node.getNodeType() + " on "
								+ host.getHostIP() + ", Exception: " + e.getMessage(), e);
				}

				if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
					String responseMsg = dwrResponse.getResponseMessage();
					if (dwrResponse.isTaskSuccess()) {
						QueryIOAgentManager.startQueryIOServices(host, node, dwrResponse);
						dwrResponse.setResponseMessage(responseMsg + "\n" + dwrResponse.getResponseMessage());

						if (dwrResponse.isTaskSuccess()) {
							node.setServiceStatus(QueryIOConstants.STATUS_LAUNCHING);
							NodeDAO.updateServiceStatus(connection, node);
							ControllerManager.startQueryIOServiceController(nodeId);
						}
					}

					if (!host.isWindows()) {
						Configuration conf = ConfigurationManager.getConfiguration(connection, nodeId);
						ArrayList configKeys = new ArrayList();
						ArrayList configValues = new ArrayList();

						configKeys.add(QueryIOConstants.HIVE_QUERYIO_HDFS_URI);
						configValues.add(conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY));

						configKeys.add(QueryIOConstants.HIVE_QUERYIO_DEFAULT_FS);
						configValues.add(conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY));

						configKeys.add(QueryIOConstants.HIVE_METASTORE_WAREHOUSE_DIR);
						configValues.add(conf.get(QueryIOConstants.HIVE_QUERYIO_WAREHOUSE_DIR));

						configKeys.add(QueryIOConstants.HIVE_MAPREDUCE_FRAMEWORK_NAME);
						if (isEvaluation) {
							configValues.add("local");
						} else {
							String framworkName = conf.get(QueryIOConstants.HIVE_QUERYIO_MAPREDUCE_FRAMEWORK_NAME_KEY);
							if (framworkName == null)
								framworkName = "local";
							configValues.add(framworkName);
						}
						configKeys.add(QueryIOConstants.HIVE_QUERYLOG_LOCATION);
						configValues.add(host.getInstallDirPath() + QueryIOConstants.HIVE_DIR_NAME + "/logs");
						// configValues.add(""); // Not to generate any history
						// file.

						String analyticsDbName = NodeDAO.getAnalyticsDBNameForNameNodeMapping(connection, nodeId);

						if (analyticsDbName != null) {
							DBConfigBean dbConfigBean = DBConfigDAO.getConnectionDetail(analyticsDbName);
							if (dbConfigBean != null) {
								configKeys.add(QueryIOConstants.HIVE_METASTORE_CONNECTION_DRIVER);
								configValues.add(dbConfigBean.getPrimaryDriverName());
								configKeys.add(QueryIOConstants.HIVE_METASTORE_CONNECTION_URL);
								configValues.add(dbConfigBean.getPrimaryConnectionURL());
								configKeys.add(QueryIOConstants.HIVE_METASTORE_CONNECTION_USERNAME);
								configValues.add(dbConfigBean.getPrimaryUsername());
								configKeys.add(QueryIOConstants.HIVE_METASTORE_CONNECTION_PASSWORD);
								configValues.add(dbConfigBean.getPrimaryPassword());
							}
							CustomTagDBConfig customTagDBConfigBean = CustomTagDBConfigManager
									.getConfig(analyticsDbName);
							if (customTagDBConfigBean != null) {
								String databaseType = customTagDBConfigBean.getCustomTagDBType();
								if (databaseType != null && databaseType.equalsIgnoreCase(QueryIOConstants.MYSQL_DB)) {
									configKeys.add(QueryIOConstants.HIVE_PROPERTY_ONE_NAME_FOR_MYSQL);
									configValues.add(QueryIOConstants.HIVE_PROPERTY_ONE_VALUE_FOR_MYSQL);
									configKeys.add(QueryIOConstants.HIVE_PROPERTY_TWO_NAME_FOR_MYSQL);
									configValues.add(QueryIOConstants.HIVE_PROPERTY_TWO_VALUE_FOR_MYSQL);
								}
							}
						}

						String metadataDbName = NodeDAO.getDBNameForNameNodeMapping(connection, nodeId);

						if (metadataDbName != null) {
							DBConfigBean dbConfigBean = DBConfigDAO.getConnectionDetail(metadataDbName);
							if (dbConfigBean != null) {
								configKeys.add(QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_DRIVER_FILTER);
								configValues.add(dbConfigBean.getPrimaryDriverName());
								configKeys.add(QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_URL_FILTER);
								configValues.add(dbConfigBean.getPrimaryConnectionURL());
								configKeys.add(QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_USERNAME_FILTER);
								configValues.add(dbConfigBean.getPrimaryUsername());
								configKeys.add(QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_PASSWORD_FILTER);
								configValues.add(dbConfigBean.getPrimaryPassword());
							}
						}

						DWRResponse response = QueryIOAgentManager.updateHiveSiteConfiguration(nodeId, configKeys,
								configValues);

						if (analyticsDbName != null && metadataDbName != null) {
							if (response.isTaskSuccess()) {
								QueryIOAgentManager.startHiveServer(host, node, response);
								node.setHiveServiceStatus(QueryIOConstants.STATUS_LAUNCHING);
								if (response.isTaskSuccess()) {
									boolean flag = checkHiveStarted(nodeId);
									if (flag) {
										node.setHiveServiceStatus(QueryIOConstants.STATUS_STARTED);
										ControllerManager.startQueryIOServiceController(nodeId);
										dwrResponse.setDwrResponse(true,
												dwrResponse.getResponseMessage() + " " + response.getResponseMessage(),
												response.getResponseCode());
									} else {
										node.setHiveServiceStatus(QueryIOConstants.STATUS_NOT_RESPONDING);
										ControllerManager.startQueryIOServiceController(nodeId);
										dwrResponse.setDwrResponse(false, QueryIOConstants.HIVE_SERVER_START_FAILED,
												400);
									}
								} else {
									node.setHiveServiceStatus(QueryIOConstants.STATUS_NOT_RESPONDING);
									dwrResponse.setDwrResponse(false,
											dwrResponse.getResponseMessage() + " " + response.getResponseMessage(),
											response.getResponseCode());
								}
							} else {
								node.setHiveServiceStatus(QueryIOConstants.STATUS_NOT_RESPONDING);
								dwrResponse.setDwrResponse(false,
										dwrResponse.getResponseMessage() + " " + response.getResponseMessage(),
										response.getResponseCode());
							}
							NodeDAO.updateHiveServiceStatus(connection, node);
							ControllerManager.startHiveServiceController(nodeId);
						}
						createHiveFolder(conf);

					}
				}

				if (node.getNodeType().equals(QueryIOConstants.SECONDARYNAMENODE)
						|| node.getNodeType().equals(QueryIOConstants.JOURNALNODE))
					node.setStatus(QueryIOConstants.STATUS_STARTED);
				else
					node.setStatus(QueryIOConstants.STATUS_LAUNCHING);
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger()
							.debug("Start node Mark : " + (Thread.currentThread().getStackTrace()[1].getLineNumber()));
				NodeDAO.updateStatus(connection, node);
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger()
							.debug("Start node Mark : " + (Thread.currentThread().getStackTrace()[1].getLineNumber()));
			} else {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger()
							.debug("Start node Mark : " + (Thread.currentThread().getStackTrace()[1].getLineNumber()));
				return dwrResponse;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(
					"Monitor thread could not be started for nodeId: " + nodeId + " Exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger()
					.debug("Start node Mark : " + (Thread.currentThread().getStackTrace()[1].getLineNumber()));
		return dwrResponse;
	}

	private static boolean checkHiveStarted(String nameNodeId) {
		int count = 0;

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Attempting to start HIVE server");

		boolean flag = false;

		while (!flag && count < QueryIOConstants.HIVE_CONNECTION_RETRY_LIMIT) {
			try {
				if (count != 0)
					AppLogger.getLogger()
							.fatal("Hive server not started yet, waiting for some time to re-try the same. Current try count: "
									+ count);

				Thread.sleep(5000);
				count++;

				AdHocHiveClient.isHiveStarted(nameNodeId);
				flag = true;
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Exception occured while connecting to hive: ", e);
				flag = false;
			}
		}
		if (flag) {
			AppLogger.getLogger().debug("INFO: Hive server is running");
			return true;
		}

		try {
			AdHocHiveClient.isHiveStarted(nameNodeId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		AppLogger.getLogger().debug("FATAL: Hive failed to start");
		return false;
	}

	private static void createHiveFolder(Configuration conf) {
		String str = getLoggedInUser();
		if (str == null) {
			str = conf.get(QueryIOConstants.LOGGED_IN_USER_INSTALLER);
		}

		Thread.currentThread().setName(str);
		FileSystem dfs = null;
		String warehouseDir = "";
		Path filePath = null;
		DFSOutputStream dfsOutputStream = null;

		try {
			dfs = FileSystem.get(conf);
			dfs.setConf(conf);

			warehouseDir = conf.get(QueryIOConstants.HIVE_QUERYIO_WAREHOUSE_DIR);

			String tempPath = warehouseDir.substring(warehouseDir.lastIndexOf(":"), warehouseDir.length());
			warehouseDir = tempPath.substring(tempPath.indexOf("/"), tempPath.length());

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("warehouseDir: " + warehouseDir);

			filePath = new Path(warehouseDir);
			try {
				if (!dfs.exists(filePath)) {
					dfs.mkdirs(filePath);
					int s = 777;
					dfs.setPermission(filePath, parsePermissions(s));
				}
			} catch (Exception e) {
				AppLogger.getLogger().fatal(warehouseDir + " path could not be created in HDFS : ", e);
			} finally {
				try {
					if (dfsOutputStream != null)
						dfsOutputStream.close();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
					throw e;
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Could not create Hive Directory in HDFS: " + e);
		} finally {
			try {
				if (dfs != null)
					dfs.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	private static FsPermission parsePermissions(int permissions) {
		FsAction u = getAction(permissions / 100);
		int t = permissions % 100;
		FsAction g = getAction(t / 10);
		FsAction o = getAction(t % 10);

		return new FsPermission(u, g, o, false);
	}

	private static FsAction getAction(int i) {
		switch (i) {
		case 0:
			return FsAction.NONE;
		case 1:
			return FsAction.EXECUTE;
		case 2:
			return FsAction.WRITE;
		case 3:
			return FsAction.WRITE_EXECUTE;
		case 4:
			return FsAction.READ;
		case 5:
			return FsAction.READ_EXECUTE;
		case 6:
			return FsAction.READ_WRITE;
		case 7:
			return FsAction.ALL;
		default:
			return FsAction.READ;
		}
	}

	public static DWRResponse stopNode(String nodeId) {

		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);

		try {
			if (!isAdmin()) {
				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);

			Host host = HostDAO.getHostDetail(connection, node.getHostId());

			/* Stop Monitor thread for node here */

			if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
				ControllerManager.stopNameNodeController(host.getHostIP(), node);
			} else if (node.getNodeType().equals(QueryIOConstants.DATANODE)) {
				ControllerManager.stopDataNodeController(host.getHostIP(), node);
			} else if (node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)) {
				ControllerManager.stopResourceManagerController(host.getHostIP(), node);
			} else if (node.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {
				ControllerManager.stopNodeManagerController(host.getHostIP(), node);
			} else if (node.getNodeType().equals(QueryIOConstants.SECONDARYNAMENODE)) {
				// TODO
			}

			AppLogger.getLogger().info("Monitor stopped for " + node.getNodeType() + " on " + host.getHostIP());

			/* Stop node here */

			QueryIOAgentManager.stopNode(host, node, dwrResponse);
			if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
				String responseMsg = dwrResponse.getResponseMessage();
				QueryIOAgentManager.stopQueryIOServices(host, node, dwrResponse);
				dwrResponse.setResponseMessage(responseMsg + "\n" + dwrResponse.getResponseMessage());

				if (dwrResponse.isTaskSuccess()) {
					node.setServiceStatus(QueryIOConstants.STATUS_STOPPED);
					NodeDAO.updateServiceStatus(connection, node);
					ControllerManager.stopQueryIOServiceController(nodeId);
					ControllerManager.stopHiveServiceController(nodeId);
				}

				responseMsg = dwrResponse.getResponseMessage();
				QueryIOAgentManager.stopHiveServer(host, node, dwrResponse);
				dwrResponse.setResponseMessage(responseMsg + "\n" + dwrResponse.getResponseMessage());

				if (dwrResponse.isTaskSuccess()) {
					node.setHiveServiceStatus(QueryIOConstants.STATUS_STOPPED);
					NodeDAO.updateHiveServiceStatus(connection, node);
				}
			}

			if (dwrResponse.isTaskSuccess()) {
				node.setStatus(QueryIOConstants.STATUS_STOPPED);
				NodeDAO.updateStatus(connection, node);

				NodeDAO.setNodeMonitor(connection, nodeId, false);
			} else {
				AppLogger.getLogger().fatal(dwrResponse.getResponseMessage());

				/* Node could not be stopped. Restart the monitor threads. */

				int dataFetchInterval = QueryIOConstants.DEFAULT_CONTROLLER_DATA_FETCH_INTERVAL;

				try {
					dataFetchInterval = Integer.parseInt(ConfigurationManager.getConfiguration(connection, nodeId)
							.get(QueryIOConstants.CONTROLLER_DATA_FETCH_INTERVAL_KEY));
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Controller Data Fetch Interval: " + dataFetchInterval + " seconds.");

				try {
					if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
						ControllerManager.startNameNodeController(connection, host, node, dataFetchInterval);
					} else if (node.getNodeType().equals(QueryIOConstants.DATANODE)) {
						ControllerManager.startDataNodeController(connection, host, node, dataFetchInterval);
					} else if (node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)) {
						ControllerManager.startResourceManagerController(connection, host, node, dataFetchInterval);
					} else if (node.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {
						ControllerManager.startNodeManagerController(connection, host, node, dataFetchInterval);
					}
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger()
								.debug("Monitor restarted for " + node.getNodeType() + " on " + host.getHostIP());

					NodeDAO.setNodeMonitor(connection, nodeId, true);
				} catch (Exception e) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Monitor could not be restarted for " + node.getNodeType() + " on "
								+ host.getHostIP() + ", Exception: " + e.getMessage(), e);
				}

				return dwrResponse;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(
					"Monitor thread could not be stopped for nodeId: " + nodeId + " Exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
		return dwrResponse;
	}

	public static DWRResponse setNodeMonitor(String nodeId, boolean monitor) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("setNodeMonitor: " + nodeId + " : " + monitor);

		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			if (!isAdmin()) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			QueryIOResponse queryIOResponse = null;
			String nnId = null;

			if (monitor && node.getStatus().startsWith(QueryIOConstants.STATUS_STOPPED)) {
				dwrResponse.setDwrResponse(false, "Specified node is not running. "
						+ "To start node monitoring, first start the node and then start node monitor.", 401);
				return dwrResponse;
			}

			int dataFetchInterval = QueryIOConstants.DEFAULT_CONTROLLER_DATA_FETCH_INTERVAL;

			try {
				dataFetchInterval = Integer.parseInt(ConfigurationManager.getConfiguration(connection, nodeId)
						.get(QueryIOConstants.CONTROLLER_DATA_FETCH_INTERVAL_KEY));
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Controller Data Fetch Interval: " + dataFetchInterval + " seconds.");

			if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
				try {
					if (monitor) {
						ControllerManager.startNameNodeController(connection, host, node, dataFetchInterval);
					} else {
						ControllerManager.stopNameNodeController(host.getHostIP(), node);
					}
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Monitor " + (monitor ? "started" : "stopped")
								+ " for name node on " + host.getHostIP());
					dwrResponse.setResponseMessage(
							"Monitor " + (monitor ? "started" : "stopped") + " for name node on " + host.getHostIP());
					dwrResponse.setTaskSuccess(true);
				} catch (Exception e) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Monitor could not be " + (monitor ? "started" : "stopped")
								+ " for name node on " + host.getHostIP(), e);
					dwrResponse.setResponseMessage("Monitor could not be " + (monitor ? "started" : "stopped")
							+ " for name node on " + host.getHostIP());
				}
			} else if (node.getNodeType().equals(QueryIOConstants.DATANODE)) {
				try {
					if (monitor) {
						ControllerManager.startDataNodeController(connection, host, node, dataFetchInterval);
					} else {
						ControllerManager.stopDataNodeController(host.getHostIP(), node);
					}
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Monitor " + (monitor ? "started" : "stopped")
								+ " for data node on " + host.getHostIP());
					dwrResponse.setTaskSuccess(true);
					dwrResponse.setResponseMessage(
							"Monitor " + (monitor ? "started" : "stopped") + " for data node on " + host.getHostIP());
				} catch (Exception e) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Monitor could not be " + (monitor ? "started" : "stopped")
								+ " for data node on " + host.getHostIP(), e);
					dwrResponse.setResponseMessage("Monitor could not be " + (monitor ? "started" : "stopped")
							+ " for data node on " + host.getHostIP());
				}
			} else if (node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)) {
				try {
					if (monitor) {
						ControllerManager.startResourceManagerController(connection, host, node, dataFetchInterval);
					} else {
						ControllerManager.stopResourceManagerController(host.getHostIP(), node);
					}
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Monitor " + (monitor ? "started" : "stopped")
								+ " for Resource Manager node on " + host.getHostIP());
					dwrResponse.setTaskSuccess(true);
					dwrResponse.setResponseMessage("Monitor " + (monitor ? "started" : "stopped")
							+ " for Resource Manager node on " + host.getHostIP());
				} catch (Exception e) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Monitor could not be " + (monitor ? "started" : "stopped")
								+ " for Resource Manager on " + host.getHostIP(), e);
					dwrResponse.setResponseMessage("Monitor could not be " + (monitor ? "started" : "stopped")
							+ " for Resource Manager on " + host.getHostIP());
				}
			} else if (node.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {
				try {
					if (monitor) {
						ControllerManager.startNodeManagerController(connection, host, node, dataFetchInterval);
					} else {
						ControllerManager.stopNodeManagerController(host.getHostIP(), node);
					}
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Monitor " + (monitor ? "started" : "stopped")
								+ " for Node Manager on " + host.getHostIP());
					dwrResponse.setTaskSuccess(true);
					dwrResponse.setResponseMessage("Monitor " + (monitor ? "started" : "stopped")
							+ " for Node Manager on " + host.getHostIP());
				} catch (Exception e) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Monitor could not be " + (monitor ? "started" : "stopped")
								+ " for Node Manager on " + host.getHostIP(), e);
					dwrResponse.setResponseMessage("Monitor could not be " + (monitor ? "started" : "stopped")
							+ " for Node Manager on " + host.getHostIP());
				}
			} else if (node.getNodeType().equals(QueryIOConstants.SECONDARYNAMENODE)) {
				// TODO
			}

			if (dwrResponse.isTaskSuccess()) {
				NodeDAO.setNodeMonitor(connection, nodeId, monitor);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(
					"Monitor thread could not be started for nodeId: " + nodeId + " Exception: " + e.getMessage(), e);
			dwrResponse.setDwrResponse(false, "Node Monitoring service is failed.", 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}

		return dwrResponse;
	}

	public static DWRResponse decommission(String nodeId) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			if (!isAdmin()) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();

			Node datanode = NodeDAO.getNode(connection, nodeId);
			Host dataNodeHost = HostDAO.getHostDetail(connection, datanode.getHostId());

			ArrayList list = new ArrayList();
			list.add(DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY);
			ArrayList value = QueryIOAgentManager.getConfig(dataNodeHost, list, datanode, "hdfs-site.xml");
			String datanodeAddress = (String) value.get(0);

			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Decomission requested for node " + dataNodeHost.getHostIP());

			List namenodes = NodeDAO.getAllNameNodes(connection);
			NodeDAO.insertDecommissionNode(connection, nodeId);
			HadoopConfigManager.updateHostsExcludeList(connection, true);
			dwrResponse.setDwrResponse(true, "Decommissioning of this node is in progress.", 500);
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
	}

	public DWRResponse addDataNode(int hostId, String nodeId, String serverPort, String httpPort, String httpsPort,
			String ipcPort, String jmxPort, ArrayList disks, ArrayList volumePath, boolean isLocal) {

		List<Integer> portList = new ArrayList<Integer>();
		portList.add(Integer.parseInt(serverPort));
		portList.add(Integer.parseInt(httpPort));
		portList.add(Integer.parseInt(httpsPort));
		portList.add(Integer.parseInt(jmxPort));
		portList.add(Integer.parseInt(ipcPort));

		Connection connection = null;
		Host dataNodeHost = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			// checking if the user has admin rights
			if (!isAdmin()) {

				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			dataNodeHost = HostDAO.getHostDetail(connection, hostId);

			QueryIOResponse resp = QueryIOAgentManager.checkPortAvailability(dataNodeHost, portList);
			if (!resp.isSuccessful()) {
				dwrResponse.setResponseMessage("Datanode could not be added.\n" + resp.getResponseMsg());
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Add Datanode requsted on host" + dataNodeHost.getHostIP());

			// checking if atleast one namenode is added
			ArrayList namenodes = getAllNonStandByNodes(connection);
			if (namenodes.size() == 0) {
				dwrResponse.setResponseMessage(QueryIOConstants.NO_NAMENODE_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// checking if an node is already present by this id
			if (NodeDAO.getNode(connection, nodeId) != null) {
				dwrResponse.setResponseMessage(
						"DataNode could not be added.\n" + QueryIOConstants.NODE_ID_ALREADY_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// Check for nodemanager host mapping in resourcemanager's etc host
			// file.

			Node namenode = (Node) namenodes.get(0);
			Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());

			String hostName = QueryIOAgentManager.getHostName(namenodeHost);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("NameNode hostname: " + hostName);
			String hostAddress = QueryIOAgentManager.getHostAddress(namenodeHost);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("NameNode IP: " + hostAddress);
			// String hostName = QueryIOAgentManager.getHostName(namenodeHost);
			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("NameNode hostname: " + hostName);
			// String hostAddress =
			// QueryIOAgentManager.getHostAddress(namenodeHost);
			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("NameNode IP: " + hostAddress);
			if (!QueryIOAgentManager.hasMapping(dataNodeHost, hostName, hostAddress)) {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Found no mapping. pinging..");
				// if (!QueryIOAgentManager.isReachable(dataNodeHost,
				// namenodeHost.getHostIP())) {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Unable to ping");
				// dwrResponse
				// .setResponseMessage("Unable to ping NameNode by hostname from
				// DataNode host ["
				// + dataNodeHost.getHostIP()
				// + "]. In case you have Domain Name Server on your network
				// then please check with your network administrator on
				// NameNode's host resolution. Alternatively you can add
				// NameNode's HOST-IP mapping in /etc/hosts file on DataNode
				// host. Please note this will require administrative
				// priviliges.");
				dwrResponse.setResponseMessage("No IP-Hostname found of NameNode host on DataNode host ["
						+ dataNodeHost.getHostIP()
						+ "]. In case you have Domain Name Server on your network then please check with your network administrator on NameNode's host resolution. Alternatively you can add NameNode's HOST-IP mapping in /etc/hosts file on DataNode host. Please note this will require administrative priviliges.");
				dwrResponse.setResponseCode(500);
				return dwrResponse;
				// }
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Ping successfully");
			}

			// creating node Object
			Node node = new Node();
			node.setId(nodeId);
			node.setNodeType(QueryIOConstants.DATANODE);
			node.setHostId(dataNodeHost.getId());
			node.setStatus(QueryIOConstants.STATUS_STOPPED);
			node.setJmxPort(jmxPort);
			for (int i = 0; i < volumePath.size(); i++) {
				String dirPath = (String) volumePath.get(i);
				if (!dirPath.endsWith("/"))
					dirPath = dirPath + "/";
				dirPath += node.getId() + "/";
				volumePath.set(i, dirPath);
			}

			String dirPath = "";
			String disk = "";
			for (int i = 0; i < volumePath.size(); i++) {
				if (i != 0) {
					dirPath += ",";
					disk += ",";
				}
				dirPath += (String) volumePath.get(i);
				disk += (String) disks.get(i);
			}
			// getting all configuration related to federated nameservices of
			// cluster
			String nameServices = "";
			ArrayList federatedKeys = new ArrayList();
			ArrayList federatedValues = new ArrayList();
			for (int i = 0; i < namenodes.size(); i++) {
				namenode = (Node) namenodes.get(i);
				namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
				if (i != 0)
					nameServices += ",";
				nameServices += namenode.getId();
				federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + namenode.getId());
				federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + namenode.getId());
				federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + namenode.getId());
			}
			try {
				namenode = (Node) namenodes.get(0);
				namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
				federatedValues = QueryIOAgentManager.getConfig(namenodeHost, federatedKeys, namenode, "hdfs-site.xml");
				if (federatedValues == null) {
					throw new Exception("Host " + namenodeHost.getHostIP() + " is not responding. "
							+ "The configuration changes pertaining to this operation are to be applied on all hosts having datanodes.");
				}
			} catch (Exception e) {
				dwrResponse.setDwrResponse(false, e.getMessage(), 500);
				return dwrResponse;
			}
			federatedKeys.add(DFSConfigKeys.DFS_NAMESERVICES);
			federatedValues.add(nameServices);

			// updating all config at remote host
			QueryIOAgentManager.setDatanodeDefaultConfiguration(federatedKeys, federatedValues, dataNodeHost, node,
					dirPath, serverPort, httpPort, httpsPort, ipcPort, jmxPort, disk, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			QueryIOAgentManager.updateHadoopEnv(dataNodeHost, node);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// formatting node
			for (int i = 0; i < volumePath.size(); i++) {
				QueryIOAgentManager.formatDirectory(dataNodeHost, (String) volumePath.get(i), dwrResponse);
				if (!dwrResponse.isTaskSuccess()) {
					return dwrResponse;
				}
			}

			// write node to db
			NodeDAO.insertNode(connection, node);

			// create monitoring tables
			ArrayList colNames = new ArrayList();
			ArrayList colTypes = new ArrayList();

			colNames.add(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
			colTypes.add(QueryIOConstants.DATATYPE_TIMESTAMP);

			ArrayList systemAttributes = MonitorDAO.getDataNodeSystemAttributes(connection);
			for (int j = 0; j < systemAttributes.size(); j++) {
				colNames.add(((SystemAttribute) systemAttributes.get(j)).getColumnName());
				colTypes.add(((SystemAttribute) systemAttributes.get(j)).getDataType());
			}
			MonitorDAO.createDataNodeMonitorTable(connection, node.getId(), colNames, colTypes);

			colNames.clear();
			colTypes.clear();

			ArrayList liveAttributes = MonitorDAO.getDataNodeLiveAttributes(connection);
			for (int j = 0; j < liveAttributes.size(); j++) {
				colNames.add(((LiveAttribute) liveAttributes.get(j)).getColumnName());
				colTypes.add(((LiveAttribute) liveAttributes.get(j)).getDataType());
			}
			MonitorDAO.createDataNodeSummaryTable(connection, node.getId(), colNames, colTypes);

			// write volumes to db
			HadoopConfigManager.updateRackConfig(connection);

			VolumeDAO.addVolume(connection, node.getId(), disk, dirPath);

			AppLogger.getLogger()
					.info("Monitor table added for " + node.getNodeType() + " on " + dataNodeHost.getHostIP());

			HadoopConfigManager.updateHostsList(connection, true);

			dwrResponse.setResponseMessage(QueryIOConstants.DATANODE_ADDED_SUCCESS);
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseCode(200);
			updateMetadataFile(QueryIOConstants.METADATA_FILE_DN, dataNodeHost.getHostIP(), dirPath, true);
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
	}

	public static DWRResponse addStandbyNameNode(int hostId, String nodeId, String namenodeId, String disk,
			String volumePath, String sharedDirPath, String servicePort, String httpPort, String httpsPort,
			String jmxPort, String os3ServerPort, String secureOs3ServerPort, String hdfsoverftpServerPort,
			String ftpServerPort, String secureFtpPort, boolean isJournal) {

		List<Integer> portList = new ArrayList<Integer>();
		portList.add(Integer.parseInt(servicePort));
		portList.add(Integer.parseInt(httpPort));
		portList.add(Integer.parseInt(httpsPort));
		portList.add(Integer.parseInt(jmxPort));
		portList.add(Integer.parseInt(os3ServerPort));
		portList.add(Integer.parseInt(secureOs3ServerPort));
		portList.add(Integer.parseInt(hdfsoverftpServerPort));
		portList.add(Integer.parseInt(ftpServerPort));
		portList.add(Integer.parseInt(secureFtpPort));

		Connection connection = null;
		Host host = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			// checking if the user has admin rights
			if (!isAdmin()) {

				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();

			List namenodeList = NodeDAO.getAllNameNodes(connection);
			if (namenodeList.size() > 1) {
				dwrResponse.setResponseMessage("StandBy NameNode could not be added.\n"
						+ "Namenodes in your cluster are currently federated. HA feature is not supported with Nameservice federation.");
				dwrResponse.setResponseCode(500);
				return dwrResponse;

			}

			host = HostDAO.getHostDetail(connection, hostId);

			QueryIOResponse resp = QueryIOAgentManager.checkPortAvailability(host, portList);
			if (!resp.isSuccessful()) {
				dwrResponse.setResponseMessage("StandBy NameNode could not be added.\n" + resp.getResponseMsg());
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Add Stand by Namenode requsted on host" + host.getHostIP());

			// checking if node is already present by this id
			if (NodeDAO.getNode(connection, nodeId) != null) {
				dwrResponse.setResponseMessage(
						"StandBy NameNode could not be added.\n" + QueryIOConstants.NODE_ID_ALREADY_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// checking if node is already configured as a HA node
			if (HAStatusDAO.isHANode(connection, namenodeId)) {
				dwrResponse.setResponseMessage("Node " + namenodeId + " is already configured as an HA node.");
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// checking if namenode for this standby node exist
			Node namenode = NodeDAO.getNode(connection, namenodeId);
			if (namenode == null) {
				dwrResponse.setResponseMessage("No namenode is present on this cluster by this Id.");
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}
			Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());

			if (isJournal) {
				String[] journalNodes = sharedDirPath.split(",");
				if (journalNodes.length % 2 == 0) {
					dwrResponse.setResponseMessage("Even no of Journal nodes selected.");
					dwrResponse.setResponseCode(500);
					return dwrResponse;
				}
				sharedDirPath = "qjournal://";
				ArrayList nodeKeys = new ArrayList();
				nodeKeys.add(DFSConfigKeys.DFS_JOURNALNODE_RPC_ADDRESS_KEY);
				for (int i = 0; i < journalNodes.length; i++) {
					if (i != 0) {
						sharedDirPath += ";";
					}
					Node n = NodeDAO.getNode(connection, journalNodes[i].trim());
					Host h = HostDAO.getHostDetail(connection, n.getHostId());
					ArrayList nodeValues = QueryIOAgentManager.getConfig(h, nodeKeys, n, "hdfs-site.xml");
					if (nodeValues == null || nodeValues.size() != nodeKeys.size()) {
						throw new Exception("Host " + h.getHostIP() + " is not responding.");
					}
					sharedDirPath += nodeValues.get(0);
				}
			}

			// creating node object
			Node node = new Node();
			node.setId(nodeId);
			node.setNodeType(QueryIOConstants.NAMENODE);
			node.setHostId(host.getId());
			node.setStatus(QueryIOConstants.STATUS_STOPPED);
			node.setJmxPort(String.valueOf(jmxPort));
			if (!volumePath.endsWith("/"))
				volumePath = volumePath + "/";
			volumePath += node.getId() + "/";

			if (!sharedDirPath.endsWith("/"))
				sharedDirPath = sharedDirPath + "/";
			sharedDirPath += namenode.getId();

			// getting namenode's ports

			ArrayList nodeKeys = new ArrayList();
			nodeKeys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + namenodeId);
			nodeKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + namenodeId);
			nodeKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + namenodeId);
			List nodeValues = QueryIOAgentManager.getConfig(namenodeHost, nodeKeys, namenode, "hdfs-site.xml");

			ArrayList coreSitekeys = new ArrayList();
			coreSitekeys.add(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			coreSitekeys.add(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
			coreSitekeys.add(QueryIOConstants.CUSTOM_TAG_DB_DBCONFIGPATH);
			nodeValues.addAll(QueryIOAgentManager.getConfig(namenodeHost, coreSitekeys, namenode, "core-site.xml"));
			if (nodeValues == null || nodeValues.size() != 6) {
				throw new Exception("Host " + namenodeHost.getHostIP() + " is not responding. "
						+ "The configuration changes pertaining to this operation are to be applied on host having namenode "
						+ namenodeId);
			}

			// constructing HA configuration
			ArrayList keys = new ArrayList();
			ArrayList values = new ArrayList();
			keys.add(DFSConfigKeys.DFS_HA_NAMENODES_KEY_PREFIX + "." + namenodeId);
			values.add(namenodeId + "," + nodeId);
			keys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + namenodeId + "." + namenodeId);
			values.add((String) nodeValues.get(0));
			keys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + namenodeId + "." + nodeId);
			values.add(host.getHostIP() + ":" + servicePort);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + namenodeId + "." + namenodeId);
			values.add((String) nodeValues.get(1));
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + namenodeId + "." + nodeId);
			values.add(host.getHostIP() + ":" + httpPort);
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + namenodeId + "." + namenodeId);
			values.add((String) nodeValues.get(2));
			keys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + namenodeId + "." + nodeId);
			values.add(host.getHostIP() + ":" + httpsPort);
			keys.add(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			values.add((String) nodeValues.get(3));
			keys.add(QueryIOConstants.CUSTOM_TAG_DB_DBCONFIGPATH);
			values.add((String) nodeValues.get(4));
			keys.add(DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY);
			values.add(sharedDirPath);
			keys.add(DFSConfigKeys.DFS_HA_NAMENODE_ID_KEY);
			values.add(nodeId);

			// updating all config at remote host
			QueryIOAgentManager.setStandByNamenodeDefaultConfiguration(namenodeId, node, host, keys, values, volumePath,
					servicePort, httpsPort, jmxPort, os3ServerPort, secureFtpPort, hdfsoverftpServerPort, ftpServerPort,
					secureFtpPort, disk, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			QueryIOAgentManager.updateHadoopEnv(host, node);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// format namenode
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Formatting stand-by namenode.");
			QueryIOAgentManager.formatNamenode(host, node, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Formatting stand-by namenode done.");

			// updating HA configuration on all datanodes and namenode
			values.set((values.size() - 1), namenodeId);

			resp = QueryIOAgentManager.setAllNodeConfig(namenodeHost, namenode, keys, values);
			namenode.setStatus(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION);
			NodeDAO.updateStatus(connection, namenode);
			dwrResponse.setResponseMessage(resp.getResponseMsg());
			dwrResponse.setTaskSuccess(resp.isSuccessful());
			if (!dwrResponse.isTaskSuccess()) {
				dwrResponse.setDwrResponse(false,
						"Host " + namenodeHost.getHostIP() + " is not responding. "
								+ "The configuration changes pertaining to this operation are to be applied on all hosts having namenodes and/or datanodes.",
						500);
			}

			if (!isJournal)
				QueryIOAgentManager.copyEditsDirToSharedDir(namenodeHost, namenode, dwrResponse);

			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			dwrResponse = stopNode(namenodeId);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			QueryIOResponse response = QueryIOAgentManager.initializeSharedEdits(namenodeHost, namenode);
			if (!response.isSuccessful()) {
				dwrResponse.setResponseCode(500);
				dwrResponse.setResponseMessage(response.getResponseMsg());
				dwrResponse.setTaskSuccess(false);
				return dwrResponse;
			}

			dwrResponse = startNode(namenodeId, false);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			response = QueryIOAgentManager.performBootstrapStandby(host, node);
			if (!response.isSuccessful()) {
				dwrResponse.setResponseCode(500);
				dwrResponse.setResponseMessage(response.getResponseMsg());
				dwrResponse.setTaskSuccess(false);
				return dwrResponse;
			}

			ArrayList datanodes = NodeDAO.getAllDatanodes(connection);
			for (int i = 0; i < datanodes.size(); i++) {
				Node datanode = (Node) datanodes.get(i);
				Host datanodeHost = HostDAO.getHostDetail(connection, datanode.getHostId());
				resp = QueryIOAgentManager.setAllNodeConfig(datanodeHost, datanode, keys, values);
				datanode.setStatus(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION);
				NodeDAO.updateStatus(connection, datanode);
				dwrResponse.setResponseMessage(resp.getResponseMsg());
				dwrResponse.setTaskSuccess(resp.isSuccessful());
				if (!dwrResponse.isTaskSuccess()) {
					dwrResponse.setDwrResponse(false,
							"Host " + datanodeHost.getHostIP() + " is not responding. "
									+ "The configuration changes pertaining to this operation are to be applied on all hosts having namenodes and/or datanodes.",
							500);
				}
			}

			// write node to db
			NodeDAO.insertNode(connection, node);
			HAStatusDAO.insertEntry(connection, namenodeId, nodeId);
			VolumeDAO.addVolume(connection, node.getId(), disk, volumePath);

			ArrayList colNames = new ArrayList();
			ArrayList colTypes = new ArrayList();

			colNames.add(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
			colTypes.add(QueryIOConstants.DATATYPE_TIMESTAMP);

			ArrayList systemAttributes = MonitorDAO.getNameNodeSystemAttributes(connection);
			for (int j = 0; j < systemAttributes.size(); j++) {
				colNames.add(((SystemAttribute) systemAttributes.get(j)).getColumnName());
				colTypes.add(((SystemAttribute) systemAttributes.get(j)).getDataType());
			}
			MonitorDAO.createNameNodeMonitorTable(connection, node.getId(), colNames, colTypes);

			colNames.clear();
			colTypes.clear();

			ArrayList liveAttributes = MonitorDAO.getNameNodeLiveAttributes(connection);
			for (int j = 0; j < liveAttributes.size(); j++) {
				colNames.add(((LiveAttribute) liveAttributes.get(j)).getColumnName());
				colTypes.add(((LiveAttribute) liveAttributes.get(j)).getDataType());
			}
			MonitorDAO.createNameNodeSummaryTable(connection, node.getId(), colNames, colTypes);

			String metadataDb = NodeDAO.getDBNameForNameNodeMapping(connection, namenodeId);
			if (metadataDb != null || !metadataDb.isEmpty()) {
				NodeDAO.insertNameNodeDBMapping(connection, node.getId(), metadataDb,
						NodeDAO.getAnalyticsDBNameForNameNodeMapping(connection, namenodeId));
			}

			AppLogger.getLogger().info("Monitor table added for " + node.getNodeType() + " on " + host.getHostIP());
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseMessage(QueryIOConstants.NODE_ADDED_SUCCESS);
			dwrResponse.setResponseCode(200);
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setResponseCode(500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
	}

	public int getAllDataNodesStarted() {
		int count = 0;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NodeDAO.getAllDataNodesStarted(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception in getAllDataNodesStarted(): " + e.getLocalizedMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
		return count;
	}

	public DWRResponse addNameNode(int hostId, String nodeId, String disk, String dirPath, String serverPort,
			String httpPort, String httpsPort, String jmxPort, String os3ServerPort, String secureOs3ServerPort,
			String hdfsoverftpServerPort, String ftpServerPort, String secureFtpPort, String connectionName,
			String analyticsDbName, boolean isRestartDataNode) {

		List<Integer> portList = new ArrayList<Integer>();
		portList.add(Integer.parseInt(serverPort));
		portList.add(Integer.parseInt(httpPort));
		portList.add(Integer.parseInt(httpsPort));
		portList.add(Integer.parseInt(jmxPort));
		portList.add(Integer.parseInt(os3ServerPort));
		portList.add(Integer.parseInt(secureOs3ServerPort));
		portList.add(Integer.parseInt(hdfsoverftpServerPort));
		portList.add(Integer.parseInt(ftpServerPort));
		portList.add(Integer.parseInt(secureFtpPort));

		Connection connection = null;
		Host host = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			// checking if the user has admin rights
			if (!isAdmin()) {

				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();

			List namenodeList = NodeDAO.getAllNameNodes(connection);
			if (namenodeList.size() > 0) {
				Node namenode = (Node) namenodeList.get(0);
				if (HAStatusDAO.isHANode(connection, namenode.getId())) {
					dwrResponse.setResponseMessage("NameNode could not be added.\n"
							+ "Cluster is configured as High Avalability cluster. Nameservice federation is currently not supported with HA mode.");
					dwrResponse.setResponseCode(500);
					return dwrResponse;
				}
			}

			host = HostDAO.getHostDetail(connection, hostId);
			QueryIOResponse resp = QueryIOAgentManager.checkPortAvailability(host, portList);
			if (!resp.isSuccessful()) {
				dwrResponse.setResponseMessage("NameNode could not be added.\n" + resp.getResponseMsg());
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Add Namenode requsted on host" + host.getHostIP());

			// checking if node is already present by this id
			if (NodeDAO.getNode(connection, nodeId) != null) {
				dwrResponse.setResponseMessage(
						"NameNode could not be added.\n" + QueryIOConstants.NODE_ID_ALREADY_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// creating node object
			Node node = new Node();
			node.setId(nodeId);
			node.setNodeType(QueryIOConstants.NAMENODE);
			node.setHostId(host.getId());
			node.setStatus(QueryIOConstants.STATUS_STOPPED);
			node.setJmxPort(String.valueOf(jmxPort));

			if (!dirPath.endsWith("/"))
				dirPath = dirPath + "/";
			dirPath += node.getId() + "/";

			// getting all configuration related to federated nameservices of
			// cluster
			String nameServices = "";
			ArrayList federatedKeys = new ArrayList();
			ArrayList federatedValues = new ArrayList();
			ArrayList datanodes = NodeDAO.getAllDatanodes(connection);
			ArrayList namenodes = getAllNonStandByNodes(connection);
			if (namenodes.size() > 0) {
				for (int i = 0; i < namenodes.size(); i++) {
					Node namenode = (Node) namenodes.get(i);
					nameServices += namenode.getId() + ",";
					federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + namenode.getId());
					federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + namenode.getId());
					federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + namenode.getId());
				}
				try {
					Node namenode = (Node) namenodes.get(0);
					Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
					federatedValues = QueryIOAgentManager.getConfig(namenodeHost, federatedKeys, namenode,
							"hdfs-site.xml");
					if (federatedValues == null) {
						throw new Exception("Host " + namenodeHost.getHostIP() + " is not responding. "
								+ "The configuration changes pertaining to this operation are to be applied on all hosts having datanodes/namenodes.");
					}
				} catch (Exception e) {
					dwrResponse.setDwrResponse(false, e.getMessage(), 500);
					return dwrResponse;
				}
			}
			nameServices += node.getId();
			federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + node.getId());
			federatedValues.add(host.getHostIP() + ":" + serverPort);
			federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + node.getId());
			federatedValues.add(host.getHostIP() + ":" + httpPort);
			federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + node.getId());
			federatedValues.add(host.getHostIP() + ":" + httpsPort);
			federatedKeys.add(DFSConfigKeys.DFS_NAMESERVICES);
			federatedValues.add(nameServices);

			// populate list for Default On Ingest Tag Parser
			ArrayList fileTypeList = new ArrayList();
			String[] arr = QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES.split(",");
			for (String str : arr) {
				str = str.trim();
				if (!fileTypeList.contains(str)) {
					fileTypeList.add(str);
					federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "." + str);
					federatedValues.add(QueryIOConstants.DEFAULT_ONINGEST_PARSER_CLASS_NAME);
				}
			}
			// populate list for Default On Ingest Tag Parser for WIKI Files
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES_WIKI);
			federatedValues.add(QueryIOConstants.DEFAULT_ONINGEST_PARSER_CLASS_NAME_WIKI);

			// for hive types
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.ADHOC_TYPE_CSV.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_CSV);
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.TYPE_JTL.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_CSV);
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.ADHOC_TYPE_JSON.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_JSON);
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.ADHOC_TYPE_LOG.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_LOG4J);
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.ADHOC_TYPE_ACCESSLOG.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_APACHE_LOG);
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.ADHOC_TYPE_IISLOG.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_IIS_LOG);
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.ADHOC_TYPE_PAIRS_EXTENSION.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_KVPAIRS);

			String fileTypes = QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES.concat(",")
					.concat(QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES_WIKI);
			fileTypes = fileTypes.concat(",").concat(QueryIOConstants.ADHOC_TYPE_CSV.toLowerCase()).concat(",")
					.concat(QueryIOConstants.ADHOC_TYPE_JSON.toLowerCase()).concat(",")
					.concat(QueryIOConstants.ADHOC_TYPE_LOG.toLowerCase()).concat(",")
					.concat(QueryIOConstants.ADHOC_TYPE_PAIRS_EXTENSION.toLowerCase()).concat(",")
					.concat(QueryIOConstants.ADHOC_TYPE_IISLOG.toLowerCase()).concat(",")
					.concat(QueryIOConstants.ADHOC_TYPE_ACCESSLOG.toLowerCase());

			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_FILETYPES);
			federatedValues.add(fileTypes);

			// updating all config at remote host
			QueryIOAgentManager.setNamenodeDefaultConfiguration(node, host, federatedKeys, federatedValues, dirPath,
					serverPort, httpPort, httpsPort, jmxPort, os3ServerPort, secureOs3ServerPort, hdfsoverftpServerPort,
					ftpServerPort, secureFtpPort, connectionName, analyticsDbName, disk, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			QueryIOAgentManager.updateHadoopEnv(host, node);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// format namenode
			QueryIOAgentManager.formatNamenode(host, node, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// updating all datanode configuration w.r.t. nameservices
			// federation
			for (int i = 0; i < datanodes.size(); i++) {
				Node datanode = (Node) datanodes.get(i);
				Host datanodeHost = HostDAO.getHostDetail(connection, datanode.getHostId());
				resp = QueryIOAgentManager.setAllNodeConfig(datanodeHost, datanode, federatedKeys, federatedValues);
				dwrResponse.setResponseMessage(resp.getResponseMsg());
				dwrResponse.setTaskSuccess(resp.isSuccessful());
				boolean isUpdateStatus = true;
				if (dwrResponse.isTaskSuccess()) {
					if (datanode.getStatus().equals(QueryIOConstants.STATUS_STARTED)) {
						if (isRestartDataNode) {
							DWRResponse dwrResp = stopNode(datanode.getId());
							if (dwrResp.isTaskSuccess()) {
								dwrResp = startNode(datanode.getId(), false);
								if (dwrResp.isTaskSuccess()) {
									dwrResponse.setResponseMessage(
											resp.getResponseMsg() + " DataNode restarted successfully.");
									dwrResponse.setTaskSuccess(true);
									isUpdateStatus = false;
								} else {
									dwrResponse.setResponseMessage(
											dwrResponse.getResponseMessage() + " " + dwrResp.getResponseMessage());
									dwrResponse.setTaskSuccess(false);
									isUpdateStatus = true;
								}
							} else {
								dwrResponse.setResponseMessage(
										dwrResponse.getResponseMessage() + " " + dwrResp.getResponseMessage());
								dwrResponse.setTaskSuccess(false);
								isUpdateStatus = true;
							}
						}
					}
					if (isUpdateStatus) {
						datanode.setStatus(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION);
						NodeDAO.updateStatus(connection, datanode);
					}
				} else {
					dwrResponse.setDwrResponse(false,
							"Host " + datanodeHost.getHostIP() + " is not responding. "
									+ "The configuration changes pertaining to this operation are to be applied on all hosts having namenodes and/or datanodes.",
							500);
				}
			}

			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			for (int i = 0; i < namenodes.size(); i++) {
				Node namenode = (Node) namenodes.get(i);
				Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
				resp = QueryIOAgentManager.setAllNodeConfig(namenodeHost, namenode, federatedKeys, federatedValues);
				dwrResponse.setResponseMessage(resp.getResponseMsg());
				dwrResponse.setTaskSuccess(resp.isSuccessful());
				if (!dwrResponse.isTaskSuccess()) {
					dwrResponse.setDwrResponse(false,
							"Host " + namenodeHost.getHostIP() + " is not responding. "
									+ "The configuration changes pertaining to this operation are to be applied on all hosts having namenodes and/or datanodes.",
							500);
				}
			}

			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// write node to db
			NodeDAO.insertNode(connection, node);
			NodeDAO.insertNameNodeDBMapping(connection, node.getId(), connectionName, analyticsDbName);
			VolumeDAO.addVolume(connection, node.getId(), disk, dirPath);

			// creating monitoring tables
			ArrayList colNames = new ArrayList();
			ArrayList colTypes = new ArrayList();
			colNames.add(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
			colTypes.add(QueryIOConstants.DATATYPE_TIMESTAMP);
			ArrayList systemAttributes = MonitorDAO.getNameNodeSystemAttributes(connection);
			for (int j = 0; j < systemAttributes.size(); j++) {
				colNames.add(((SystemAttribute) systemAttributes.get(j)).getColumnName());
				colTypes.add(((SystemAttribute) systemAttributes.get(j)).getDataType());
			}
			MonitorDAO.createNameNodeMonitorTable(connection, node.getId(), colNames, colTypes);

			colNames.clear();
			colTypes.clear();

			ArrayList liveAttributes = MonitorDAO.getNameNodeLiveAttributes(connection);
			for (int j = 0; j < liveAttributes.size(); j++) {
				colNames.add(((LiveAttribute) liveAttributes.get(j)).getColumnName());
				colTypes.add(((LiveAttribute) liveAttributes.get(j)).getDataType());
			}
			MonitorDAO.createNameNodeSummaryTable(connection, node.getId(), colNames, colTypes);

			AppLogger.getLogger().info("Monitor table added for " + node.getNodeType() + " on " + host.getHostIP());
			QueryIOServiceDAO.insert(connection, new QueryIOService(nodeId, QueryIOConstants.SERVICE_HDFS_OVER_FTP,
					QueryIOConstants.STATUS_STOPPED));
			QueryIOServiceDAO.insert(connection,
					new QueryIOService(nodeId, QueryIOConstants.SERVICE_OS3, QueryIOConstants.STATUS_STOPPED));
			QueryIOServiceDAO.insert(connection,
					new QueryIOService(nodeId, QueryIOConstants.SERVICE_HIVE, QueryIOConstants.STATUS_STOPPED));

			dwrResponse.setResponseCode(200);
			dwrResponse.setResponseMessage(QueryIOConstants.NAMENODE_ADDED_SUCCESS);
			dwrResponse.setTaskSuccess(true);

			updateMetadataFile(QueryIOConstants.METADATA_FILE_NN, host.getHostIP(), dirPath, true);

			String namespaceId = null;
			resp = QueryIOAgentManager.fetchNamespaceId(host, dirPath);
			if (resp.isSuccessful())
				namespaceId = resp.getResponseMsg();
			String blockPoolId = null;
			resp = QueryIOAgentManager.fetchBlockPoolId(host, dirPath);
			if (resp.isSuccessful())
				blockPoolId = resp.getResponseMsg();

			if (namespaceId != null && blockPoolId != null) {
				if (connectionName != null && !connectionName.isEmpty())
					updateNSMetadata(connectionName, namespaceId, blockPoolId);
			}

			// Register on ingest parser
			resp = addContentParser(connection, node);

			addDefaultMRJob(connection, node);

			// For WIKI Files

			// Add Handling for hive.

			QueryIOAgentManager.updateHiveHadoopHome(host);
			// ArrayList configKeys = new ArrayList();
			// ArrayList configValues = new ArrayList();
			// configKeys.add(QueryIOConstants.HIVE_METASTORE_WAREHOUSE_DIR);
			// String hdfsUri = "hdfs://" + host.getHostIP() + ":" + serverPort;
			// String warehouseLocation = hdfsUri +
			// AdHocHiveClient.warehouseDir;
			// configValues.add(warehouseLocation);
			// configKeys.add(QueryIOConstants.HIVE_MAPREDUCE_FRAMEWORK_NAME);
			// configValues.add("yarn");
			// configKeys.add(QueryIOConstants.HIVE_QUERYLOG_LOCATION);
			// configValues.add(host.getInstallDirPath() +
			// QueryIOConstants.HIVE_DIR_NAME + "/logs");
			//// configValues.add(""); // Not to generate any history file.
			//
			// DWRResponse response =
			// QueryIOAgentManager.updateHiveSiteConfiguration(nodeId,
			// configKeys, configValues);
			//
			// if (response.isTaskSuccess())
			// {
			// QueryIOAgentManager.startHiveServer(host, node, response);
			//
			// if (response.isTaskSuccess())
			// dwrResponse.setDwrResponse(true, dwrResponse.getResponseMessage()
			// + " " + response.getResponseMessage(),
			// response.getResponseCode());
			// else
			// dwrResponse.setDwrResponse(false,
			// dwrResponse.getResponseMessage() + " " +
			// response.getResponseMessage(), response.getResponseCode());
			// }
			// else
			// dwrResponse.setDwrResponse(false,
			// dwrResponse.getResponseMessage() + " " +
			// response.getResponseMessage(), response.getResponseCode());

			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(200);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
	}

	private static void addDefaultMRJob(Connection connection, Node node) {
		List<Node> nodes;
		try {
			nodes = NodeDAO.getAllNodesForType(connection, QueryIOConstants.RESOURCEMANAGER);
			String jobName = QueryIOConstants.DATATAGGING_DEFAULT_JOB;
			String arguments = "default" + " " + "\"" + jobName + "\" " + "/ " + "'' " + 0 + " " + "-1" + " " + "true"; // -1
																														// =>
																														// All
																														// file
																														// till
																														// time
																														// of
																														// Job
																														// Run.
			// By default Recursive was true, and input path filter was false.
			MapRedJobConfig jobConfig = new MapRedJobConfig(node.getId(), nodes.get(0).getId(), jobName,
					QueryIOConstants.DATATAGGING_GENERIC_PARSER_JOB_NAME,
					QueryIOConstants.DATATAGGING_GENERIC_PARSER_JOB_LIB_JAR_NAME, null,
					QueryIOConstants.DATATAGGING_GENERIC_PARSER_JOB_CLASS, arguments, true, false, null);

			MapRedJobConfigDAO.insert(connection, jobConfig);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error adding default MR Job.", e);
		}
	}

	private static QueryIOResponse addContentParser(Connection connection, Node node) {
		QueryIOResponse resp = null;
		try {
			String defaultJarFile = StartupParameters.getHadoopDirLocation() + File.separator
					+ QueryIOConstants.MAPREDRESOURCE + File.separator + QueryIOConstants.DEFAULT_USERDEFINEDTAGSJAR
					+ File.separator + QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR;

			ArrayList fileTypeTemp = new ArrayList();
			String[] list = QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES.split(",");
			for (String str : list) {
				str = str.trim();
				if (!fileTypeTemp.contains(str)) {
					fileTypeTemp.add(str);

					TagParserDAO.insert(connection, QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + str.toUpperCase(),
							QueryIOConstants.DEFAULT_ONINGEST_PARSER_DESCRIPTION,
							QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + "_" + node.getId().toLowerCase()
									+ File.separator + QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR,
							str, QueryIOConstants.DEFAULT_ONINGEST_PARSER_CLASS_NAME, node.getId(), true, true);

					TagParserDBSchemaUpdator dbUpdator = new TagParserDBSchemaUpdator(defaultJarFile,
							QueryIOConstants.DEFAULT_ONINGEST_PARSER_CLASS_NAME, node.getId(), str);
					resp = dbUpdator.parse();
				}
			}

			String destJarFile = EnvironmentalConstants.getAppHome() + File.separator + QueryIOConstants.MAPREDRESOURCE
					+ File.separator + QueryIOConstants.TAGPARSER_JAR_DIR + File.separator
					+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + "_" + node.getId().toLowerCase();

			File destFile = new File(destJarFile);

			if (!destFile.exists())
				destFile.mkdirs();

			StaticUtilities.copyFileAs(new File(defaultJarFile), new File(
					destFile.getAbsolutePath() + File.separator + QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR));

			TagParserDAO.insert(connection,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME
							+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES_WIKI.toUpperCase(),
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_DESCRIPTION,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + "_" + node.getId().toLowerCase() + File.separator
							+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES_WIKI,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_CLASS_NAME_WIKI, node.getId(), true, true);

			TagParserDBSchemaUpdator dbUpdator = new TagParserDBSchemaUpdator(defaultJarFile,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_CLASS_NAME_WIKI, node.getId(),
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES_WIKI);
			resp = dbUpdator.parse();

			TagParserDAO.insert(connection,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + QueryIOConstants.ADHOC_TYPE_CSV.toUpperCase(),
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_DESCRIPTION,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + "_" + node.getId().toLowerCase() + File.separator
							+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR,
					QueryIOConstants.ADHOC_TYPE_CSV.toLowerCase(),
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_CSV, node.getId(), true, true);

			dbUpdator = new TagParserDBSchemaUpdator(defaultJarFile,
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_CSV, node.getId(),
					QueryIOConstants.ADHOC_TYPE_CSV);
			resp = dbUpdator.parse();

			TagParserDAO.insert(connection,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + QueryIOConstants.ADHOC_TYPE_CSV.toUpperCase(),
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_DESCRIPTION,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + "_" + node.getId().toLowerCase() + File.separator
							+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR,
					QueryIOConstants.TYPE_JTL.toLowerCase(), QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_CSV,
					node.getId(), true, true);

			dbUpdator = new TagParserDBSchemaUpdator(defaultJarFile,
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_CSV, node.getId(), QueryIOConstants.TYPE_JTL);
			resp = dbUpdator.parse();

			TagParserDAO.insert(connection,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + QueryIOConstants.ADHOC_TYPE_JSON.toUpperCase(),
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_DESCRIPTION,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + "_" + node.getId().toLowerCase() + File.separator
							+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR,
					QueryIOConstants.ADHOC_TYPE_JSON.toLowerCase(),
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_JSON, node.getId(), true, true);

			dbUpdator = new TagParserDBSchemaUpdator(defaultJarFile,
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_JSON, node.getId(),
					QueryIOConstants.ADHOC_TYPE_JSON);
			resp = dbUpdator.parse();

			TagParserDAO.insert(connection,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + QueryIOConstants.ADHOC_TYPE_ACCESSLOG.toUpperCase(),
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_DESCRIPTION,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + "_" + node.getId().toLowerCase() + File.separator
							+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR,
					QueryIOConstants.ADHOC_TYPE_ACCESSLOG.toLowerCase(),
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_APACHE_LOG, node.getId(), true, true);

			dbUpdator = new TagParserDBSchemaUpdator(defaultJarFile,
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_APACHE_LOG, node.getId(),
					QueryIOConstants.ADHOC_TYPE_ACCESSLOG);
			resp = dbUpdator.parse();

			TagParserDAO.insert(connection,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + QueryIOConstants.ADHOC_TYPE_LOG.toUpperCase(),
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_DESCRIPTION,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + "_" + node.getId().toLowerCase() + File.separator
							+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR,
					QueryIOConstants.ADHOC_TYPE_LOG.toLowerCase(),
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_LOG4J, node.getId(), true, true);

			dbUpdator = new TagParserDBSchemaUpdator(defaultJarFile,
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_LOG4J, node.getId(),
					QueryIOConstants.ADHOC_TYPE_LOG);
			resp = dbUpdator.parse();

			TagParserDAO.insert(connection,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + QueryIOConstants.ADHOC_TYPE_IISLOG.toUpperCase(),
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_DESCRIPTION,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + "_" + node.getId().toLowerCase() + File.separator
							+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR,
					QueryIOConstants.ADHOC_TYPE_IISLOG.toLowerCase(),
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_IIS_LOG, node.getId(), true, true);

			dbUpdator = new TagParserDBSchemaUpdator(defaultJarFile,
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_IIS_LOG, node.getId(),
					QueryIOConstants.ADHOC_TYPE_IISLOG);
			resp = dbUpdator.parse();

			TagParserDAO.insert(connection,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + QueryIOConstants.ADHOC_TYPE_PAIRS.toUpperCase(),
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_DESCRIPTION,
					QueryIOConstants.DEFAULT_ONINGEST_PARSER_NAME + "_" + node.getId().toLowerCase() + File.separator
							+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR,
					QueryIOConstants.ADHOC_TYPE_PAIRS.toLowerCase(),
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_KVPAIRS, node.getId(), true, true);

			dbUpdator = new TagParserDBSchemaUpdator(defaultJarFile,
					QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_KVPAIRS, node.getId(),
					QueryIOConstants.ADHOC_TYPE_PAIRS_EXTENSION);
			resp = dbUpdator.parse();
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			return resp;
		}
	}

	private static void updateNSMetadata(String dbSourceId, String namespaceId, String blockPoolId) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("namespaceId: " + namespaceId);
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("blockPoolId: " + blockPoolId);
		Connection connection = null;
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			connection = CoreDBManager.getCustomTagDBConnection(dbSourceId);
			String query = "SELECT * FROM " + TableConstants.TABLE_NS_METADATA;
			st = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(st, query);

			if (rs.next()) {
				query = "DELETE FROM " + TableConstants.TABLE_NS_METADATA;
				try {
					pst = DatabaseFunctions.getPreparedStatement(connection, query);
					pst.execute();
				} finally {
					DatabaseFunctions.closePreparedStatement(pst);
				}
			}

			query = "INSERT INTO " + TableConstants.TABLE_NS_METADATA + " (" + ColumnConstants.COL_NS_METADATA_KEY
					+ ", " + ColumnConstants.COL_NS_METADATA_VALUE + ") VALUES (?,?)";
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, QueryIOConstants.NS_NAMESPACE_ID);
			pst.setString(2, namespaceId);
			pst.execute();
			DatabaseFunctions.executeUpdateStatement(pst);
			pst.setString(1, QueryIOConstants.NS_BLOCKPOOL_ID);
			pst.setString(2, blockPoolId);
			DatabaseFunctions.executeUpdateStatement(pst);
		} finally {
			CoreDBManager.closeConnection(connection);
			DatabaseFunctions.closeSQLObjects(st, rs);
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static void copyFolder(File src, File dest, boolean overWrite) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdirs();
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile, overWrite);
			}

		} else {
			if (!overWrite) {
				if (dest.exists())
					return;
			}
			InputStream in = null;
			OutputStream out = null;
			try {
				// if file, then copy it
				// Use bytes stream to support all file types
				in = new FileInputStream(src);
				out = new FileOutputStream(dest);

				byte[] buffer = new byte[1024];

				int length;
				// copy the file content in bytes
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
			} finally {
				if (in != null)
					try {
						in.close();
					} catch (Exception e) {
					}
				if (out != null)
					try {
						out.close();
					} catch (Exception e) {
					}
			}
		}
	}

	public static DWRResponse addCheckpointNode(int hostId, String nodeId, String namenodeId, String dirPath,
			String httpPort, String jmxPort) {

		List<Integer> portList = new ArrayList<Integer>();
		portList.add(Integer.parseInt(httpPort));
		portList.add(Integer.parseInt(jmxPort));

		Connection connection = null;
		Host host = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			// checking if the user has admin rights
			if (!isAdmin()) {

				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			host = HostDAO.getHostDetail(connection, hostId);

			QueryIOResponse resp = QueryIOAgentManager.checkPortAvailability(host, portList);
			if (!resp.isSuccessful()) {
				dwrResponse.setResponseMessage("CheckpointNode could not be added.\n" + resp.getResponseMsg());
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Add CheckpointNode requsted on host" + host.getHostIP());

			Node namenode = NodeDAO.getNode(connection, namenodeId);
			Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());

			// checking if node is already present by this id
			if (NodeDAO.getNode(connection, nodeId) != null) {
				dwrResponse.setResponseMessage(
						"CheckpointNode could not be added.\n" + QueryIOConstants.NODE_ID_ALREADY_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// creating node object
			Node node = new Node();
			node.setId(nodeId);
			node.setHostId(hostId);
			node.setNodeType(QueryIOConstants.SECONDARYNAMENODE);
			node.setStatus(QueryIOConstants.STATUS_STOPPED);
			node.setJmxPort(jmxPort);

			if (!dirPath.endsWith("/"))
				dirPath = dirPath + "/";
			dirPath += node.getId() + "/";

			ArrayList keys = new ArrayList();
			keys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + namenodeId);
			ArrayList values = QueryIOAgentManager.getConfig(namenodeHost, keys, namenode, "hdfs-site.xml");
			if (values == null || values.size() != 1) {
				dwrResponse.setResponseMessage("Namenode host is not responding.");
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// updating all config at remote host
			QueryIOAgentManager.setSecondaryNameNodeConfig(host, node, namenodeId, (String) values.get(0), httpPort,
					jmxPort, dirPath, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			QueryIOAgentManager.updateHadoopEnv(host, node);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// format namenode
			QueryIOAgentManager.formatDirectory(host, dirPath, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// creating monitoring tables
			// ArrayList colNames = new ArrayList();
			// ArrayList colTypes = new ArrayList();
			// colNames.add(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
			// colTypes.add(QueryIOConstants.DATATYPE_TIMESTAMP);
			// ArrayList systemAttributes = MonitorDAO
			// .getNameNodeSystemAttributes(connection);
			// for (int j = 0; j < systemAttributes.size(); j++) {
			// colNames.add(((SystemAttribute) systemAttributes.get(j))
			// .getColumnName());
			// colTypes.add(((SystemAttribute) systemAttributes.get(j))
			// .getDataType());
			// }
			// MonitorDAO.createNameNodeMonitorTable(connection, node.getId(),
			// colNames, colTypes);
			//
			// colNames.clear();
			// colTypes.clear();
			//
			// ArrayList liveAttributes = MonitorDAO
			// .getNameNodeLiveAttributes(connection);
			// for (int j = 0; j < liveAttributes.size(); j++) {
			// colNames.add(((LiveAttribute) liveAttributes.get(j))
			// .getColumnName());
			// colTypes.add(((LiveAttribute) liveAttributes.get(j))
			// .getDataType());
			// }
			// MonitorDAO.createNameNodeSummaryTable(connection, node.getId(),
			// colNames, colTypes);
			//
			// AppLogger.getLogger().info(
			// "Monitor table added for " + node.getNodeType() + " on "
			// + host.getHostIP());
			// write node to db
			NodeDAO.insertNode(connection, node);

			dwrResponse.setResponseCode(200);
			dwrResponse.setResponseMessage(QueryIOConstants.NODE_ADDED_SUCCESS);
			dwrResponse.setTaskSuccess(true);
			updateMetadataFile(QueryIOConstants.METADATA_FILE_CPN, host.getHostIP(), dirPath, true);
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(200);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
	}

	public static DWRResponse addJournalNode(int hostId, String nodeId, String dirPath, String serverPort,
			String httpPort, String jmxPort) {

		List<Integer> portList = new ArrayList<Integer>();
		portList.add(Integer.parseInt(serverPort));
		portList.add(Integer.parseInt(httpPort));
		portList.add(Integer.parseInt(jmxPort));

		Connection connection = null;
		Host host = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			// checking if the user has admin rights
			if (!isAdmin()) {

				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			host = HostDAO.getHostDetail(connection, hostId);

			QueryIOResponse resp = QueryIOAgentManager.checkPortAvailability(host, portList);
			if (!resp.isSuccessful()) {
				dwrResponse.setResponseMessage("JournalNode could not be added.\n" + resp.getResponseMsg());
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Add JournalNode requsted on host" + host.getHostIP());

			// checking if node is already present by this id
			if (NodeDAO.getNode(connection, nodeId) != null) {
				dwrResponse.setResponseMessage(
						"JournalNode could not be added.\n" + QueryIOConstants.NODE_ID_ALREADY_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// creating node object
			Node node = new Node();
			node.setId(nodeId);
			node.setHostId(hostId);
			node.setNodeType(QueryIOConstants.JOURNALNODE);
			node.setStatus(QueryIOConstants.STATUS_STOPPED);
			node.setJmxPort(jmxPort);

			if (!dirPath.endsWith("/"))
				dirPath = dirPath + "/";
			dirPath += node.getId() + "/";

			// updating all config at remote host
			QueryIOAgentManager.setJournalNodeConfig(host, node, serverPort, httpPort, jmxPort, dirPath, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			QueryIOAgentManager.updateHadoopEnv(host, node);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// format namenode
			QueryIOAgentManager.formatDirectory(host, dirPath, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			NodeDAO.insertNode(connection, node);

			dwrResponse.setResponseCode(200);
			dwrResponse.setResponseMessage(QueryIOConstants.NODE_ADDED_SUCCESS);
			dwrResponse.setTaskSuccess(true);
			updateMetadataFile(QueryIOConstants.METADATA_FILE_JN, host.getHostIP(), dirPath, true);
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(200);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
	}

	public static ArrayList getNonStandByNodes() {
		Connection connection = null;
		ArrayList result = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			result = getAllNonStandByNodes(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return result;
	}

	public static ArrayList getAllNonStandByNodes(Connection connection) throws Exception {
		ArrayList result = new ArrayList();
		ArrayList list = NodeDAO.getAllNameNodes(connection);
		for (Object o : list) {
			Node node = (Node) o;
			if (!(HAStatusDAO.isHANode(connection, node.getId())
					&& HAStatusDAO.getActiveNodeId(connection, node.getId()) != null)) {
				result.add(node);
			}
		}
		return result;
	}

	public static ArrayList getAttributeList(String nodeId) {
		Connection connection = null;
		ArrayList caList = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());

			if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
				caList = MonitorDAO.getNameNodeControllerHistoricalAttributes(connection, node.getId());
			} else if (node.getNodeType().equals(QueryIOConstants.DATANODE)) {
				caList = MonitorDAO.getDataNodeControllerHistoricalAttributes(connection, node.getId());
			} else if (node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)) {
				caList = MonitorDAO.getResourceManagerControllerHistoricalAttributes(connection, node.getId());
			} else if (node.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {
				caList = MonitorDAO.getNodeManagerControllerHistoricalAttributes(connection, node.getId());
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return caList;
	}

	public static ArrayList getAttributeListForHost(int hostId) {
		Connection connection = null;
		ArrayList caList = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, hostId);

			caList = MonitorDAO.getHostControllerHistoricalAttributes(connection, host.getId());
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return caList;
	}

	public static ArrayList getUserDetailInfo(String userIdList) {
		Connection connection = null;
		ArrayList userNameList = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			userNameList = new ArrayList();
			String userList[] = userIdList.substring(1, userIdList.length() - 1).split(",");
			for (String userID : userList) {
				User user = UserDAO.getUserDetail(connection, Integer.parseInt(userID.trim()));
				userNameList.add(user.getFirstName() + " " + user.getLastName());
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return userNameList;
	}

	public static ArrayList getAllNodeIdWithIp() {
		ArrayList arr = null;
		Connection connection = null;
		try {
			arr = new ArrayList();
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList nList = NodeDAO.getAllNodeIds(connection);
			for (int i = 0; i < nList.size(); i++) {
				Node node = NodeDAO.getNode(connection, (String) nList.get(i));

				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				arr.add(node.getId() + "#" + host.getHostIP() + "#" + node.getNodeType());
			}
			return arr;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getAllNameNodeIdWithIp() {
		ArrayList arr = null;
		Connection connection = null;
		try {
			arr = new ArrayList();
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList nList = NodeDAO.getAllNameNodes(connection);// NodeIds(connection);
			for (int i = 0; i < nList.size(); i++) {
				Node node = (Node) nList.get(i);
				;// Node node = NodeDAO.getNode(connection, (String)
					// nList.get(i));

				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				arr.add(node.getId() + "#" + host.getHostIP() + "#" + node.getNodeType());
			}
			return arr;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getAllJournalNodeIds() {
		ArrayList arr = null;
		Connection connection = null;
		try {
			arr = new ArrayList();
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList nList = NodeDAO.getAllJournalNodes(connection);

			for (int i = 0; i < nList.size(); i++) {
				Node node = (Node) nList.get(i);
				arr.add(node.getId());
			}
			return arr;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getAllDataNodeIdWithIp() {
		ArrayList arr = null;
		Connection connection = null;
		try {
			arr = new ArrayList();
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList nList = NodeDAO.getAllDatanodes(connection);// NodeIds(connection);
			for (int i = 0; i < nList.size(); i++) {
				Node node = (Node) nList.get(i);// NodeDAO.getNode(connection,
												// ((Node)
												// nList.get(i)).getId());

				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				arr.add(node.getId() + "#" + host.getHostIP() + "#" + node.getNodeType());
			}
			return arr;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getAllResourceManagerIdWithIp() {
		ArrayList arr = null;
		Connection connection = null;
		try {
			arr = new ArrayList();
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList nList = NodeDAO.getAllRMs(connection);// NodeIds(connection);
			for (int i = 0; i < nList.size(); i++) {
				Node node = (Node) nList.get(i);// NodeDAO.getNode(connection,
												// ((Node)
												// nList.get(i)).getId());

				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				arr.add(node.getId() + "#" + host.getHostIP() + "#" + node.getNodeType());
			}
			return arr;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getAllNodeManagerIdWithIp() {
		ArrayList arr = null;
		Connection connection = null;
		try {
			arr = new ArrayList();
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList nList = NodeDAO.getAllNMs(connection);// NodeIds(connection);
			for (int i = 0; i < nList.size(); i++) {
				Node node = (Node) nList.get(i);// NodeDAO.getNode(connection,
												// ((Node)
												// nList.get(i)).getId());

				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				arr.add(node.getId() + "#" + host.getHostIP() + "#" + node.getNodeType());
			}
			return arr;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static SummaryTable listFiles(String nodeId, String user, String path, int rows, int pageNo) {

		ListFilesRequest request = new ListFilesRequest(nodeId, user, new Path(path), rows, pageNo);
		try {
			request.process();
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}

		return request.getFilesList();
	}

	public static SummaryTable listSelectedFiles(String nodeId, String user, String path, int rows, int pageNo,
			String query) {

		ListSelectedFilesRequest request;
		try {
			request = new ListSelectedFilesRequest(user, path, nodeId, rows, pageNo, query);
			request.process();

			return request.getFilesList();
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}

		return null;
	}

	public static String getLoggedInUser() {
		WebContext ctx = WebContextFactory.get();

		if (ctx == null) {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("ctx null");
			return null;
		}
		if (ctx.getHttpServletRequest() == null)
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("ctx.getHttpServletRequest() null");

		String user = ctx.getHttpServletRequest().getRemoteUser();

		return user;
	}

	public static boolean isAdmin() {
		WebContext ctx = WebContextFactory.get();

		String value = ctx.getHttpServletRequest().getUserPrincipal().toString();

		value = value.substring(value.indexOf("(") + 1, value.indexOf(")"));

		String[] splits = value.split(",");
		AppLogger.getLogger().debug("isAdmin context value : " + value);
		for (int i = 0; i < splits.length; i++) {
			if (splits[i].equalsIgnoreCase("Admin")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNonAdminAndDemo(String userName) {

		Connection connection = null;
		try {

			if (!EnvironmentalConstants.isDemoVersion()) {
				return false;
			}

			if (userName == null) {
				return !(RemoteManager.isAdmin());
			}

			connection = CoreDBManager.getQueryIODBConnection();
			String userRole = UserDAO.getRole(connection, userName);

			// System.out.println("userName: " + userName + " userRole: " +
			// userRole);

			if ("Admin".equalsIgnoreCase(userRole)) {
				return false;
			}
			return true;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return false;
	}

	public static boolean isCurrentUser(String userName) {
		WebContext ctx = WebContextFactory.get();
		String name = ctx.getHttpServletRequest().getUserPrincipal().getName();
		if (userName.equals(name)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isHostContainsNode(String hostname) {
		ArrayList nodes = RemoteManager.getAllNodeDetails(hostname);
		if (nodes == null || nodes.size() == 0) {
			return false;
		}
		return true;

	}

	public static DWRResponse initiateFailOver(String nodeId) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			HATransitionManager.performFailover(connection, nodeId, dwrResponse);
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

	}

	public static DWRResponse updateRackConfig(int hostId, String rackName) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		try {
			if (!isAdmin()) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			HostDAO.updateRackDetail(connection, hostId, rackName);
			HadoopConfigManager.updateRackConfig(connection);
			dwrResponse.setDwrResponse(true, "Rack name updated successfully.", 200);
			return dwrResponse;

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static String[] getNode(String nodeId) {
		Connection connection = null;
		Node node = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			String[] detail = { host.getRackName(), host.getHostIP(), nodeId };
			return detail;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return null;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

	}

	public static String[] getHostRack(String hostIP) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, hostIP);
			String[] hostdetails = { host.getRackName(), hostIP };
			return hostdetails;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return null;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

	}

	public static String getQueryIOAgentPort() {

		try {

			return StartupParameters.getQueryIOAgentPort();

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return null;
		}

	}

	public static ArrayList getAllResourceManagers() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NodeDAO.getAllNodeIdsForType(connection, QueryIOConstants.RESOURCEMANAGER);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return null;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static ArrayList getAllResourceManagerDetails() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NodeDAO.getAllNodesForType(connection, QueryIOConstants.RESOURCEMANAGER);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getAllNodeManagerDetails() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NodeDAO.getAllNodesForType(connection, QueryIOConstants.NODEMANAGER);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static DWRResponse addResourceManager(String nodeId, int hostId, String serverPort, String schedulerPort,
			String webAppPort, String adminPort, String jmxPort, String resourceTrackerPort,
			String jobHistoryServerPort, String jobHistoryWebappPort, String dirPath) {
		List<Integer> portList = new ArrayList<Integer>();
		portList.add(Integer.parseInt(serverPort));
		portList.add(Integer.parseInt(schedulerPort));
		portList.add(Integer.parseInt(webAppPort));
		portList.add(Integer.parseInt(adminPort));
		portList.add(Integer.parseInt(jmxPort));
		portList.add(Integer.parseInt(resourceTrackerPort));
		portList.add(Integer.parseInt(jobHistoryServerPort));
		portList.add(Integer.parseInt(jobHistoryWebappPort));

		Connection connection = null;
		Host host = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			// checking if the user has admin rights
			if (!isAdmin()) {

				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			host = HostDAO.getHostDetail(connection, hostId);

			QueryIOResponse resp = QueryIOAgentManager.checkPortAvailability(host, portList);
			if (!resp.isSuccessful()) {
				dwrResponse.setResponseMessage("ResourceManager could not be added.\n" + resp.getResponseMsg());
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Add ResourceManager requsted on host" + host.getHostIP());

			// checking if an node is already present by this id
			if (NodeDAO.getNode(connection, nodeId) != null) {
				dwrResponse.setResponseMessage(
						"Resource Manager could not be added.\n" + QueryIOConstants.NODE_ID_ALREADY_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// creating node Object
			Node node = new Node();
			node.setId(nodeId);
			node.setNodeType(QueryIOConstants.RESOURCEMANAGER);
			node.setHostId(host.getId());
			node.setStatus(QueryIOConstants.STATUS_STOPPED);
			node.setJmxPort(jmxPort);

			if (!dirPath.endsWith("/"))
				dirPath = dirPath + "/";
			String jhTempPath = "/tmp/" + nodeId + "/JobHistoryTemp/";
			String jhPath = "/tmp/" + nodeId + "/JobHistory/";

			// updating all config at remote host
			QueryIOAgentManager.setResourceManagerDefaultConfiguration(host, node, serverPort, schedulerPort,
					webAppPort, adminPort, jmxPort, resourceTrackerPort, jobHistoryServerPort, jobHistoryWebappPort,
					jhTempPath, jhPath, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			QueryIOAgentManager.updateYarnEnv(host, node);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// formatting node
			QueryIOAgentManager.formatDirectory(host, jhTempPath, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			QueryIOAgentManager.formatDirectory(host, jhPath, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// write node to db
			NodeDAO.insertNode(connection, node);

			// create monitoring tables
			ArrayList colNames = new ArrayList();
			ArrayList colTypes = new ArrayList();

			colNames.add(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
			colTypes.add(QueryIOConstants.DATATYPE_TIMESTAMP);

			ArrayList systemAttributes = MonitorDAO.getResourceManagerSystemAttributes(connection);
			for (int j = 0; j < systemAttributes.size(); j++) {
				colNames.add(((SystemAttribute) systemAttributes.get(j)).getColumnName());
				colTypes.add(((SystemAttribute) systemAttributes.get(j)).getDataType());
			}

			MonitorDAO.createResourceManagerMonitorTable(connection, node.getId(), colNames, colTypes);

			colNames.clear();
			colTypes.clear();

			ArrayList liveAttributes = MonitorDAO.getResourceManagerLiveAttributes(connection);
			for (int j = 0; j < liveAttributes.size(); j++) {
				colNames.add(((LiveAttribute) liveAttributes.get(j)).getColumnName());
				colTypes.add(((LiveAttribute) liveAttributes.get(j)).getDataType());
			}

			MonitorDAO.createResourceManagerSummaryTable(connection, node.getId(), colNames, colTypes);

			AppLogger.getLogger().info("Monitor table added for " + node.getNodeType() + " on " + host.getHostIP());
			dwrResponse.setResponseMessage(QueryIOConstants.RESOURCEMANAGER_ADDED_SUCCESS);
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseCode(200);
			updateMetadataFile(QueryIOConstants.METADATA_FILE_RM, host.getHostIP(), dirPath, true);
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
	}

	public static DWRResponse addNodeManager(String nodeId, int hostId, String resourceManagerId, String localizerPort,
			String webAppPort, String jmxPort, String dirPath, boolean isLocal) {

		List<Integer> portList = new ArrayList<Integer>();
		portList.add(Integer.parseInt(localizerPort));
		portList.add(Integer.parseInt(webAppPort));
		portList.add(Integer.parseInt(webAppPort));
		portList.add(Integer.parseInt(jmxPort));

		Connection connection = null;
		Host nodeManagerHost = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			// checking if the user has admin rights
			if (!isAdmin()) {

				dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
				dwrResponse.setResponseCode(401);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			nodeManagerHost = HostDAO.getHostDetail(connection, hostId);

			QueryIOResponse resp = QueryIOAgentManager.checkPortAvailability(nodeManagerHost, portList);
			if (!resp.isSuccessful()) {
				dwrResponse.setResponseMessage("Node Manager could not be added.\n" + resp.getResponseMsg());
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			AuditLogger.getUserLogger(WebContextFactory.get().getHttpServletRequest().getRemoteUser())
					.info("Add NodeManager requsted on host" + nodeManagerHost.getHostIP());

			// checking if atleast one namenode is added
			Node rmNode = NodeDAO.getNode(connection, resourceManagerId);
			if (rmNode == null) {
				dwrResponse.setResponseMessage("ResourceManager not present by this Id");
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// checking if an node is already present by this id
			if (NodeDAO.getNode(connection, nodeId) != null) {
				dwrResponse.setResponseMessage(
						"Node Manager could not be added.\n" + QueryIOConstants.NODE_ID_ALREADY_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// Check for nodemanager host mapping in resourcemanager's etc host
			// file.

			Host resourceManagerHost = HostDAO.getHostDetail(connection, rmNode.getHostId());

			String hostName = QueryIOAgentManager.getHostName(resourceManagerHost);
			AppLogger.getLogger().debug("ResourceManager hostname: " + hostName);
			String hostAddress = QueryIOAgentManager.getHostAddress(resourceManagerHost);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("ResourceManager IP: " + hostAddress);
			if (!QueryIOAgentManager.hasMapping(nodeManagerHost, hostName, hostAddress)) {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Found no mapping. pinging..");
				// if (!QueryIOAgentManager.isReachable(nodeManagerHost,
				// resourceManagerHost.getHostIP())) {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Unable to ping");
				// dwrResponse
				// .setResponseMessage("Unable to ping ResourceManager by
				// hostname from NodeManager's host ["
				// + nodeManagerHost.getHostIP()
				// + "]. In case you have Domain Name Server on your network
				// then please check with your network administrator on
				// NameNode's host resolution. Alternatively you can add
				// ResourceManager's HOST-IP mapping in /etc/hosts file on
				// NodeManager host. Please note this will require
				// administrative priviliges.");
				dwrResponse.setResponseMessage(
						"No IP-Hostname mapping found on ResourceManager host of NodeManager's host ["
								+ nodeManagerHost.getHostIP()
								+ "]. In case you have Domain Name Server on your network then please check with your network administrator on NameNode's host resolution. Alternatively you can add ResourceManager's HOST-IP mapping in /etc/hosts file on NodeManager host. Please note this will require administrative priviliges.");
				dwrResponse.setResponseCode(500);
				return dwrResponse;
				// }
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Ping successfully");
			}

			// creating node Object
			Node node = new Node();
			node.setId(nodeId);
			node.setNodeType(QueryIOConstants.NODEMANAGER);
			node.setHostId(nodeManagerHost.getId());
			node.setStatus(QueryIOConstants.STATUS_STOPPED);
			node.setJmxPort(jmxPort);

			if (!dirPath.endsWith("/"))
				dirPath = dirPath + "/";
			String logDir = dirPath + node.getId() + "NodeManagerLog/";
			String localDir = dirPath + node.getId() + "NodeManagerLocal/";

			ArrayList keys = new ArrayList();
			keys.add(YarnConfiguration.RM_ADDRESS);
			keys.add(YarnConfiguration.RM_SCHEDULER_ADDRESS);
			keys.add(YarnConfiguration.RM_WEBAPP_ADDRESS);
			keys.add(YarnConfiguration.RM_ADMIN_ADDRESS);
			keys.add(YarnConfiguration.RM_RESOURCE_TRACKER_ADDRESS);

			ArrayList values = QueryIOAgentManager.getConfig(resourceManagerHost, keys, rmNode, "yarn-site.xml");
			if (values == null || values.size() != 5) {
				dwrResponse.setResponseMessage("Namenode Host not responding.");
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// updating all config at remote host
			QueryIOAgentManager.setNodeManagerDefaultConfiguration(nodeManagerHost, node, (String) values.get(0),
					(String) values.get(1), (String) values.get(2), (String) values.get(3), (String) values.get(4),
					localizerPort, webAppPort, jmxPort, logDir, localDir, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			QueryIOAgentManager.updateYarnEnv(nodeManagerHost, node);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			// formatting node
			QueryIOAgentManager.formatDirectory(nodeManagerHost, logDir, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			QueryIOAgentManager.formatDirectory(nodeManagerHost, localDir, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// write node to db
			NodeDAO.insertNode(connection, node);

			// // create monitoring tables
			ArrayList colNames = new ArrayList();
			ArrayList colTypes = new ArrayList();

			colNames.add(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
			colTypes.add(QueryIOConstants.DATATYPE_TIMESTAMP);

			ArrayList systemAttributes = MonitorDAO.getNodeManagerSystemAttributes(connection);
			for (int j = 0; j < systemAttributes.size(); j++) {
				colNames.add(((SystemAttribute) systemAttributes.get(j)).getColumnName());
				colTypes.add(((SystemAttribute) systemAttributes.get(j)).getDataType());
			}

			MonitorDAO.createNodeManagerMonitorTable(connection, node.getId(), colNames, colTypes);

			colNames.clear();
			colTypes.clear();

			ArrayList liveAttributes = MonitorDAO.getNodeManagerLiveAttributes(connection);
			for (int j = 0; j < liveAttributes.size(); j++) {
				colNames.add(((LiveAttribute) liveAttributes.get(j)).getColumnName());
				colTypes.add(((LiveAttribute) liveAttributes.get(j)).getDataType());
			}

			MonitorDAO.createNodeManagerSummaryTable(connection, node.getId(), colNames, colTypes);

			AppLogger.getLogger()
					.info("Monitor table added for " + node.getNodeType() + " on " + nodeManagerHost.getHostIP());
			dwrResponse.setResponseMessage(QueryIOConstants.NODEMANAGER_ADDED_SUCCESS);
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseCode(200);
			updateMetadataFile(QueryIOConstants.METADATA_FILE_NM, nodeManagerHost.getHostIP(), dirPath, true);
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
	}

	public static HashMap getResourceManagerTree() {
		HashMap hostMap = new HashMap();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList resourceManagers = NodeDAO.getAllNodesForType(connection, QueryIOConstants.RESOURCEMANAGER);
			Node node = null;
			Host host = null;

			ArrayList nodeList = null;

			for (int i = 0; i < resourceManagers.size(); i++) {
				if (resourceManagers.get(i).getClass() != Node.class)
					continue;

				node = (Node) resourceManagers.get(i);
				host = HostDAO.getHostDetail(connection, node.getHostId());

				if (hostMap.get(host.getHostIP()) == null) {
					hostMap.put(host.getHostIP(), new ArrayList());
				}

				nodeList = (ArrayList) hostMap.get(host.getHostIP());
				nodeList.add(node);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return hostMap;
	}

	public static HashMap getNodeManagerTree() {
		HashMap hostMap = new HashMap();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList nodeManagers = NodeDAO.getAllNodesForType(connection, QueryIOConstants.NODEMANAGER);
			Node node = null;
			Host host = null;

			ArrayList nodeList = null;

			for (int i = 0; i < nodeManagers.size(); i++) {
				if (nodeManagers.get(i).getClass() != Node.class)
					continue;

				node = (Node) nodeManagers.get(i);
				host = HostDAO.getHostDetail(connection, node.getHostId());

				if (hostMap.get(host.getHostIP()) == null) {
					hostMap.put(host.getHostIP(), new ArrayList());
				}

				nodeList = (ArrayList) hostMap.get(host.getHostIP());

				nodeList.add(node);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return hostMap;
	}

	public static DWRResponse updateDBConfiguration(String url, String driverName, String userName, String password,
			boolean primary) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Updating " + (primary ? "primary" : "secondary") + " DBConfig. URL: " + url
					+ " DriverClassName: " + driverName + " Username: " + userName + " Password: " + password);
		DWRResponse response = new DWRResponse();
		response.setResponseMessage("BigQuery Database settings updated successfully.");
		response.setTaskSuccess(true);

		ArrayList keys = new ArrayList();
		ArrayList values = new ArrayList();

		if (primary) {
			keys.add(QueryIOConstants.CUSTOM_TAG_DB_PRIMARY_URL);
			values.add(url);
			keys.add(QueryIOConstants.CUSTOM_TAG_DB_PRIMARY_DRIVER);
			values.add(driverName);
			keys.add(QueryIOConstants.CUSTOM_TAG_DB_PRIMARY_USERNAME);
			values.add(userName);
			keys.add(QueryIOConstants.CUSTOM_TAG_DB_PRIMARY_URL);
			values.add(password);
		} else {
			keys.add(QueryIOConstants.CUSTOM_TAG_DB_SECONDARY_URL);
			values.add(url);
			keys.add(QueryIOConstants.CUSTOM_TAG_DB_SECONDARY_DRIVER);
			values.add(driverName);
			keys.add(QueryIOConstants.CUSTOM_TAG_DB_SECONDARY_USERNAME);
			values.add(userName);
			keys.add(QueryIOConstants.CUSTOM_TAG_DB_SECONDARY_PASSWORD);
			values.add(password);
		}

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			List nodes = NodeDAO.getAllNameNodes(connection);
			for (int i = 0; i < nodes.size(); i++) {
				Node node = (Node) nodes.get(i);
				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Updating DBConfig for node:" + node.getId());
				QueryIOResponse resp = QueryIOAgentManager.setAllNodeConfig(host, node, keys, values);
				response.setResponseMessage(resp.getResponseMsg());
				response.setTaskSuccess(resp.isSuccessful());
				if (!response.isTaskSuccess()) {
					return response;
				}
			}

			nodes = NodeDAO.getAllRMs(connection);
			for (int i = 0; i < nodes.size(); i++) {
				Node node = (Node) nodes.get(i);
				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Updating DBConfig for node:" + node.getId());
				QueryIOResponse resp = QueryIOAgentManager.setAllNodeConfig(host, node, keys, values);
				response.setResponseMessage(resp.getResponseMsg());
				response.setTaskSuccess(resp.isSuccessful());
				if (!response.isTaskSuccess()) {
					return response;
				}
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Updating DBConfig in db");
			HadoopConfigDAO.updateHadoopConfigDefaultValue(connection, keys, values);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Done");
		} catch (Exception e) {
			response.setDwrResponse(false, e.getMessage(), 500);
			AppLogger.getLogger().fatal("Error updating BigQuery DB Configuration, Message: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error Closing DB Connection, Message: " + e.getMessage(), e);
			}
		}
		return response;
	}

	public static ArrayList getAllNameNodeIP() {
		Connection connection = null;
		ArrayList nameNodeIP = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			nameNodeIP = HostDAO.getNameNodeHostNames(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllNameNodeIP(), Esxception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return nameNodeIP;
	}

	public static ArrayList getNameNodeForIP(String ip) {

		Connection connection = null;
		Host host = null;
		ArrayList nameNode = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			host = HostDAO.getHostDetail(connection, ip);
			nameNode = NodeDAO.getAllNameNodesForHost(connection, host.getId());
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllNameNodeIP(), Esxception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return nameNode;
	}

	public static JSONObject getAllAvailableTagsList(String namenodeId, String dbName, ArrayList tableNames) {
		Configuration conf = null;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			conf = RemoteManager.getNameNodeConfiguration(namenodeId);
			return (JSONObject) CustomTagsManager.getAllAvailableTags(dbName, tableNames);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching namenode configuration.", e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;

	}

	public static JSONObject getAllApplicableTagsList(String namenodeId, String dbName, ArrayList tableNames) {
		try {
			JSONObject jsonObject = (JSONObject) getAllAvailableTagsList(namenodeId, dbName, tableNames);
			if (jsonObject != null) {
				Map allColumns = (Map) jsonObject.get("columnMap");
				ArrayList<String> customDataTags = getAllCustomTagMetadataIds();
				for (String tagToBeRemoved : customDataTags) {
					AppLogger.getLogger().debug("tagToBeRemoved : " + tagToBeRemoved);
					allColumns.remove(tagToBeRemoved);
				}
				removeHadoopCoreTags(allColumns);
				jsonObject.put("columnMap", allColumns);
				jsonObject.put("isCustomParserRegistered", isCustomParserRegistered(namenodeId, tableNames));
				jsonObject.put("fileType", getFileTypeForAdhocTable(namenodeId, dbName, tableNames));
			}
			return jsonObject;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching namenode configuration.", e);
		}
		return null;
	}

	private static boolean isCustomParserRegistered(String namenodeId, ArrayList tableNames) throws Exception {
		Configuration conf = null;
		conf = RemoteManager.getNameNodeConfiguration(namenodeId);

		String fileType = null;
		if (tableNames != null && tableNames.size() > 0) {
			String[] splittedData = ((String) tableNames.get(0)).split("_");
			if (splittedData.length > 1) {
				fileType = splittedData[1];
			}
		}
		AppLogger.getLogger().debug("fileType : " + fileType);
		if (fileType == null) {
			return false;
		}
		String parserFileTypes = conf.get(QueryIOConstants.CUSTOM_TAG_PARSER_FILETYPES, "");
		AppLogger.getLogger().debug("parserFileTypes : " + parserFileTypes);

		boolean foundParser = false;
		if (!parserFileTypes.isEmpty()) {
			for (String str : parserFileTypes.split(",")) {
				if (str.equals(fileType)) {
					foundParser = true;
					break;
				}
			}
		}
		if (conf.get(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "." + fileType) != null && conf
				.get(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "." + fileType).endsWith("DataTagParser")) {
			return false;
		} else {
			return foundParser;
		}
	}

	private static void removeHadoopCoreTags(Map allColumns) {
		allColumns.remove(ColumnConstants.COL_TAG_VALUES_FILEPATH.toLowerCase());
		allColumns.remove(ColumnConstants.COL_TAG_VALUES_ACCESSTIME.toLowerCase());
		allColumns.remove(ColumnConstants.COL_TAG_VALUES_BLOCKSIZE.toLowerCase());
		allColumns.remove(ColumnConstants.COL_TAG_VALUES_LENGTH.toLowerCase());
		allColumns.remove(ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME.toLowerCase());
		allColumns.remove(ColumnConstants.COL_TAG_VALUES_OWNER.toLowerCase());
		allColumns.remove(ColumnConstants.COL_TAG_VALUES_PERMISSION.toLowerCase());
		allColumns.remove(ColumnConstants.COL_TAG_VALUES_REPLICATION.toLowerCase());
		allColumns.remove(ColumnConstants.COL_TAG_VALUES_USERGROUP.toLowerCase());
		allColumns.remove(EncryptionHandler.COL_COMPRESSION_TYPE.toLowerCase());
		allColumns.remove(EncryptionHandler.COL_ENCRYPTION_TYPE.toLowerCase());
	}

	public static ArrayList getAllDBNameForNameNodeMapping(String namenodeId) throws Exception {
		ArrayList dbNameList = new ArrayList();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			String dbName = NodeDAO.getDBNameForNameNodeMapping(connection, namenodeId);
			if ((dbName != null) && (!dbName.isEmpty()))
				dbNameList.add(dbName);
			dbName = NodeDAO.getAnalyticsDBNameForNameNodeMapping(connection, namenodeId);
			if ((dbName != null) && (!dbName.isEmpty()))
				dbNameList.add(dbName);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching namenode configuration.", e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dbNameList;
	}

	public static Map<String, String> getAllDBNameWithTypeForNameNodeMapping(String namenodeId) throws Exception {
		Map<String, String> dbNameList = new HashMap<String, String>();
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			String dbName = NodeDAO.getDBNameForNameNodeMapping(connection, namenodeId);
			if ((dbName != null) && (!dbName.isEmpty()))
				dbNameList.put("Metastore", dbName);
			dbName = NodeDAO.getAnalyticsDBNameForNameNodeMapping(connection, namenodeId);
			if ((dbName != null) && (!dbName.isEmpty()))
				dbNameList.put("Hive", dbName);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching namenode configuration.", e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dbNameList;
	}

	public static String getDBNameForNameNodeMapping(String namenodeId) throws Exception {
		String dbName = "";
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			dbName = NodeDAO.getDBNameForNameNodeMapping(connection, namenodeId);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching namenode configuration.", e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dbName;
	}

	public static String getAnalyticsDBNameForNameNodeMapping(String namenodeId) throws Exception {
		String dbName = "";
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			dbName = NodeDAO.getAnalyticsDBNameForNameNodeMapping(connection, namenodeId);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching namenode configuration.", e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dbName;
	}

	public static DWRResponse getNameNodeForDBNameMapping(String dbName) {
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setDwrResponse(false, "", 500);
		String nameNodeId = null;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			nameNodeId = NodeDAO.getNameNodeForDBNameMapping(connection, dbName);
			dwrResponse.setDwrResponse(true, "", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching namenode configuration.", e);
			dwrResponse.setDwrResponse(false, "Exception: " + e.getMessage(), 200);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		dwrResponse.setId(nameNodeId);
		return dwrResponse;
	}

	public static DWRResponse getNameNodeForAnalyticsDBNameMapping(String dBName) {
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setDwrResponse(false, "", 500);
		String nameNodeId = null;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			nameNodeId = NodeDAO.getNameNodeForAnalyticsDBNameMapping(connection, dBName);
			dwrResponse.setDwrResponse(true, "", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching namenode configuration.", e);
			dwrResponse.setDwrResponse(false, "Exception: " + e.getMessage(), 200);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		dwrResponse.setId(nameNodeId);
		return dwrResponse;
	}

	public Map<String, Boolean> getAllTagTableNamesForDB(String dbName) {
		Map<String, Boolean> map = null;
		try {
			map = CustomTagsManager.getAllTagTableNames(dbName);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error getAllTagTableNamesForDB.", e);
		}
		return map;
	}

	public Map<String, Boolean> getAllDataTagTableNamesForDB(String dbName) {
		Map<String, Boolean> map = null;
		Connection connection = null;
		try {
			if (dbName.equalsIgnoreCase("hive")) {
				connection = CoreDBManager.getQueryIODBConnection();
				map = AdHocQueryDAO.getAllDataTagTableNames(connection);
			} else {
				map = CustomTagsManager.getAllTagTableNames(dbName);
			}

		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error getAllTagTableNamesForDB.", e);
		} finally {
			try {
				if (connection != null)
					CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return map;
	}

	// Added for fetching filetype for Hive tables
	public static JSONArray getFileTypeForAdhocTable(String namenode, String dbName, ArrayList tableNames) {
		JSONArray fileType = new JSONArray();
		Connection connection = null;
		AppLogger.getLogger().debug("dbName:" + dbName);
		try {
			if ("hive".equalsIgnoreCase(dbName)) {
				connection = CoreDBManager.getQueryIODBConnection();

				for (int j = 0; j < tableNames.size(); j++) {

					String tableName = ((String) tableNames.get(j)).toLowerCase();
					AdHocQueryBean adHocInfoFromTable = AdHocQueryDAO.getAdHocInfoFromTable(connection, namenode,
							tableName);
					JSONObject obj = new JSONObject();
					obj.put(tableName, adHocInfoFromTable.getType());
					fileType.add(obj);
				}
				AppLogger.getLogger().fatal("fileType" + fileType);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error getFileTypeForAdhocTable.", e);
		} finally {
			try {
				if (connection != null)
					CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return fileType;
	}

	public ArrayList<String> getAllTagTableListForDB(String dbName) {
		ArrayList<String> list = null;
		try {
			list = CustomTagsManager.getAllTagTableNamesList(dbName);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error getAllTagTableListForDB.", e);
		}
		return list;
	}

	public static ArrayList getAllNameNodeForDBNameMapping() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NodeDAO.getAllNameNodesDBMapped(connection);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching getAllNameNodesMapping.", e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public Map<String, Boolean> getAllTagTableNames(String namenodeId, String dbName) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			if (dbName != null)
				return CustomTagsManager.getAllTagTableNames(dbName);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching namenode configuration.", e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static String getFsDefaultName(String nodeId) throws Exception {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			Node node = NodeDAO.getNode(connection, nodeId);
			if (node == null)
				return null;

			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList list = new ArrayList();
			list.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList result = QueryIOAgentManager.getConfig(host, list, node, "core-site.xml");
			if (result == null || result.size() == 0) {
				return null;
			}
			return (String) result.get(0);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static Configuration getNameNodeConfiguration(String nameNodeId) throws Exception {
		Connection connection = null;
		Configuration conf = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			if (EnvironmentalConstants.isUseKerberos()) {
				conf = ConfigurationManager.getKerberosConfiguration(connection, nameNodeId);
			} else {
				conf = ConfigurationManager.getConfiguration(connection, nameNodeId);
			}
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return conf;
	}

	public static boolean runJob() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return false;
	}

	public static Connection getCustomTagDBConnection(String poolName) throws Exception {
		return CoreDBManager.getCustomTagDBConnection(poolName);
	}

	public static String getUserHomeDirectory(String hostname, String userName, String password, String sshPrivateKey,
			int port) {
		Host host = new Host();
		host.setHostIP(hostname);
		try {
			return OneTimeConfig.getRemoteUserHomeDirectory(host, userName, password, sshPrivateKey, port);
		} catch (Exception e) {
			e.printStackTrace();
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return null;
	}

	public static String getJavaHome(String hostname, String userName, String password, String sshPrivateKey,
			int port) {
		try {
			return OneTimeConfig.getJavaHome(hostname, userName, password, sshPrivateKey, port);
		} catch (Exception e) {
			e.printStackTrace();
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return null;
	}

	public static String getLocalJavaHome() {
		try {
			return OneTimeConfig.getJavaHome();
		} catch (Exception e) {
			e.printStackTrace();
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return null;
	}

	public static String getLocalUserHomeDirectory() {
		String hostName = null;

		try {
			String userHome = System.getProperty("user.home");
			return userHome;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error occurred while retrieving user home : " + e);
		}
		return null;
	}

	public static DWRResponse insertLocalHost(String id) {
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setId(id);
		AppLogger.getLogger().debug("isAdmin() : " + isAdmin());
		if (!isAdmin()) {
			dwrResponse.setResponseMessage(QueryIOConstants.NOT_AN_AUTHORIZED_USER);
			dwrResponse.setResponseCode(401);
			return dwrResponse;
		}
		String primaryIPAddress = new GetIpAddress().getPrimaryIPAddress();
		dwrResponse = insertHostInstaller(primaryIPAddress, null, null, "null", getLocalUserHomeDirectory(),
				"/default-rack", "6680", getLocalJavaHome(), "22", true, false, "");
		return dwrResponse;
	}

	// public static String getLocalIP()
	// {
	// GetIpAddress gia = null;
	// try
	// {
	// gia = new GetIpAddress();
	// return gia.getPrimaryIPAddress();
	// }
	// catch(Exception e)
	// {
	// AppLogger.getLogger().fatal("Error occurred while retrieving IP : " + e);
	// }
	// return null;
	// }

	public static boolean validateJavaHome(String hostname, String userName, String password, String sshPrivateKey,
			String javaHome, String port) {

		try {
			return OneTimeConfig.validateJavaHome(hostname, userName, password, sshPrivateKey, javaHome, port);
		} catch (Exception e) {
			e.printStackTrace();
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}

		return false;
	}

	public static String getUserHomeDirectoryPathForHost(String hostname) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			String userHome = QueryIOAgentManager.getUserHome(HostDAO.getHostDetail(connection, hostname));
			AppLogger.getLogger().debug("UserHome rcvd: " + userHome);
			return userHome;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static DWRResponse getUserHomeDirectoryPathForHostAutomation(String hostname, String id) {
		DWRResponse response = new DWRResponse();
		response.setId(id);
		response.setResponseMessage(getUserHomeDirectoryPathForHost(hostname) + "/QueryIONodes");
		return response;
	}

	public static boolean isHANode(String nodeId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return HAStatusDAO.isHANode(connection, nodeId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return false;
	}

	public static ArrayList getAllCheckpointNodes() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NodeDAO.getAllCheckpointNodes(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getAllJournalNodes() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NodeDAO.getAllJournalNodes(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static void insertOnIngestTagParserConfig(String name, String description, String jarName,
			String tagParserfileTypes, String tagParserClassName, String namenodeId, boolean isActive)
			throws Exception {
		Connection connection = null;
		connection = CoreDBManager.getQueryIODBConnection();
		TagParserConfigManager.handleOnIngestParser(connection, name, description, jarName, tagParserfileTypes,
				tagParserClassName, namenodeId, isActive);
		CoreDBManager.closeConnection(connection);
	}

	public static DWRResponse updateOnIngestTagParserConfigExceptJarInfo(String name, String description,
			String tagParserfileTypes, String tagParserClassName, String namenodeId, boolean isActive) {
		DWRResponse response = new DWRResponse();
		response.setId(name);
		Connection connection = null;
		try {
			if (isNonAdminAndDemo(null)) {
				response.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
			}
			connection = CoreDBManager.getQueryIODBConnection();
			TagParserConfigManager.updateOnIngestParserExceptJarInfo(connection, name, description, tagParserfileTypes,
					tagParserClassName, namenodeId, isActive);
			CoreDBManager.closeConnection(connection);
			response.setTaskSuccess(true);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error while updating OnIngest Parser details.", e);
			response.setTaskSuccess(false);
		}
		return response;
	}

	public static void insertPostIngestTagParserConfig(String name, String description, String jarName,
			String tagParserfileTypes, String tagParserClassName, String namenodeId, String rmId) throws Exception {

		Connection connection = null;
		connection = CoreDBManager.getQueryIODBConnection();
		Configuration conf = RemoteManager.getNameNodeConfiguration(namenodeId);
		String hdfsURI = conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
		TagParserConfigManager.handlePostIngestParser(connection, namenodeId, rmId, name, description, hdfsURI, jarName,
				tagParserfileTypes, tagParserClassName);
		CoreDBManager.closeConnection(connection);
	}

	public static ArrayList getAllOnIngestTagParserConfigs() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return TagParserDAO.getAllOnIngest(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static SummaryTable getAllPostIngestTagParserConfigs() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return TagParserDAO.getAllPostIngest(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static TagParserConfig getTagParserConfig(int tagParserId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return TagParserDAO.get(connection, tagParserId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static DWRResponse deleteTagParserConfig(int tagParserId) {
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setId(String.valueOf(tagParserId));
		Connection connection = null;

		try {
			if (isNonAdminAndDemo(null)) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();
			TagParserConfig config = TagParserDAO.get(connection, tagParserId);
			if (config.isOnIngest())
				TagParserConfigManager.handleDeleteOnIngestParser(connection, config);
			else
				TagParserConfigManager.handleDeletePostIngestParser(connection, config);
			dwrResponse.setDwrResponse(true, "Paser deleted successfully.", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return dwrResponse;
	}

	public static void replaceDBJar(String connectionName, String url, String userName, String password,
			String driverName, String agentJar, boolean isCustomTagDB) throws Exception {
		ArrayList nodeList = RemoteManager.getNameNodes();
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = (Node) nodeList.get(i);
			Host host = RemoteManager.getHost(node.getHostId());
			QueryIOAgentManager.transferDriverJar(host, agentJar);
		}
	}

	public static void replaceAgentDBConfigFile() throws Exception {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList hosts = HostDAO.getAllHostDetails(connection);
			for (int i = 0; i < hosts.size(); i++) {
				Host host = (Host) hosts.get(i);
				QueryIOAgentManager.replaceDBConfigFromHosts(host);
			}
		} finally {
			CoreDBManager.closeConnection(connection);
		}
	}

	public static void activateDBOnServer(boolean isCustomTagDB) {

		ArrayList nodeList = RemoteManager.getNameNodes();
		String ftpPort = null;
		String os3Port = null;
		for (int i = 0; i < nodeList.size(); i++) {
			ftpPort = null;
			os3Port = null;
			int flagIndex = 0;
			Node node = (Node) nodeList.get(i);
			ArrayList arr = getNodeConfig(node.getId());
			for (int j = 0; j < arr.size(); j++) {
				if (flagIndex >= 2) {
					break;
				}
				ArrayList row = (ArrayList) arr.get(j);
				if (((String) row.get(0)).equals(QueryIOConstants.QUERYIO_HDFSOVERFTP_PORT)) {
					ftpPort = (String) row.get(1);
					continue;
				}
				if (((String) row.get(0)).equals(QueryIOConstants.QUERYIO_OS3SERVER_PORT)) {
					os3Port = (String) row.get(1);
					continue;
				}
			}
			Host host = RemoteManager.getHost(node.getHostId());
			try {
				DBActivationRequestor.reInitializeFTPServer(host, ftpPort, isCustomTagDB);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Reinitialize failed on HDFS OVER FTP Server for host " + host.getHostIP()
						+ ". " + e.getLocalizedMessage());
			}
			try {
				DBActivationRequestor.reInitializeOS3Server(host, os3Port, isCustomTagDB);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Reinitialize failed on OS3 Server for host " + host.getHostIP() + ". "
						+ e.getLocalizedMessage());
			}
		}

	}

	public static void reInitializeHadoopConfigOnServers() {
		ArrayList nodeList = RemoteManager.getNameNodes();
		String ftpPort = null;
		String os3Port = null;
		for (int i = 0; i < nodeList.size(); i++) {
			ftpPort = null;
			os3Port = null;
			int flagIndex = 0;
			Node node = (Node) nodeList.get(i);
			ArrayList arr = getNodeConfig(node.getId());
			for (int j = 0; j < arr.size(); j++) {
				if (flagIndex >= 2) {
					break;
				}
				ArrayList row = (ArrayList) arr.get(j);
				if (((String) row.get(0)).equals(QueryIOConstants.QUERYIO_HDFSOVERFTP_PORT)) {
					ftpPort = (String) row.get(1);
					continue;
				}
				if (((String) row.get(0)).equals(QueryIOConstants.QUERYIO_OS3SERVER_PORT)) {
					os3Port = (String) row.get(1);
					continue;
				}
			}
			Host host = RemoteManager.getHost(node.getHostId());
			try {
				HadoopConfigReInitRequestor.reInitializeFTPServer(host, ftpPort);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("reInitializeHadoopConfigOnServers failed on HDFS OVER FTP Server for host "
						+ host.getHostIP() + ". " + e.getLocalizedMessage());
			}
			try {
				HadoopConfigReInitRequestor.reInitializeOS3Server(host, os3Port);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("reInitializeHadoopConfigOnServers failed on OS3 Server for host "
						+ host.getHostIP() + ". " + e.getLocalizedMessage());
			}
		}
	}

	public static Timestamp getServerTime() {

		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
		return currentTimestamp;
	}

	public static ArrayList getAllConnectionsNameForNameNode(boolean isPrimary) {
		ArrayList arr = null;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			arr = DBConfigDAO.getAllCustomDBConnectionName(isPrimary);
			if (arr != null) {
				ArrayList dbNames = null;
				if (isPrimary)
					dbNames = NodeDAO.getAllDBNamesMapping(connection);
				else
					dbNames = NodeDAO.getAllAnalyticsDBNamesMapping(connection);
				if (dbNames != null) {
					Iterator it = dbNames.iterator();
					String dbName = null;
					while (it.hasNext()) {
						dbName = (String) it.next();
						if (arr.contains(dbName)) {
							arr.remove(dbName);
						}
					}
				}
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("getAllConnectionsNameForNameNode() arr: " + arr);
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("getAllConnectionsNameForNameNode() failed with Exception: " + e.getLocalizedMessage(), e);
		} finally {
			if (connection != null) {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error closing connection: " + e.getLocalizedMessage(), e);
				}
			}
		}
		return arr;
	}

	public static ArrayList getAllDbName(boolean isPrimary) {
		ArrayList arr = null;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			arr = DBConfigDAO.getAllCustomDBConnectionName(isPrimary);

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("getAllMetaStoreName() arr: " + arr);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllMetaStoreName() failed with Exception: " + e.getLocalizedMessage(), e);
		} finally {
			if (connection != null) {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error closing connection: " + e.getLocalizedMessage(), e);
				}
			}
		}
		return arr;
	}

	public static ArrayList getAllNameNodesForDBMapped(boolean isMetadata) {
		ArrayList arr = null;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			arr = NodeDAO.getAllNameNodesID(connection);
			if (arr != null) {
				ArrayList nameNodesMapped = NodeDAO.getAllNameNodesMapping(connection, isMetadata);
				if (nameNodesMapped != null) {
					Iterator it = nameNodesMapped.iterator();
					String nameNodeId = null;
					while (it.hasNext()) {
						nameNodeId = (String) it.next();
						if (arr.contains(nameNodeId)) {
							arr.remove(nameNodeId);
						}
					}
				}
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("getAllNameNodesForDBMapped() arr: " + arr);
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("getAllNameNodesForDBMapped() failed with Exception: " + e.getLocalizedMessage(), e);
		} finally {
			if (connection != null) {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error closing connection: " + e.getLocalizedMessage(), e);
				}
			}
		}
		return arr;
	}

	public static JSONObject getDataUploadStatus(String fileId) {

		JSONObject object = new JSONObject();

		try {
			HttpSession session = WebContextFactory.get().getHttpServletRequest().getSession();
			FileUploadListener fileUploadListener = (FileUploadListener) session
					.getAttribute("fileUploadListener" + fileId);
			object.put("fileId", fileId);
			if (fileUploadListener == null) {
				object.put("BytesRead", "0");
				object.put("ContentLength", "0");
				object.put("Item", "no file");
				object.put("message", "no session found");
				object.put("pervalue", 0);
				object.put("status", "init");
				object.put("ErrorMsg", "No File Found");
			} else {

				object.put("BytesRead", fileUploadListener.getBytesRead());
				object.put("ContentLength", fileUploadListener.getContentLength());
				object.put("Item", fileUploadListener.getItem());
				long perval = (long) (100 * fileUploadListener.getBytesRead() / fileUploadListener.getContentLength());
				object.put("pervalue", perval);
				object.put("status", fileUploadListener.getStatus());
				object.put("ErrorMsg", fileUploadListener.getErrorMsg());
				if (fileUploadListener.getStatus().equals("Success")
						|| fileUploadListener.getStatus().equals("Failed")) {
					session.removeAttribute("fileUploadListener" + fileId);
				}
			}

		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception occored while fetching file uploading status " + e.getMessage());
		}

		return object;
	}

	public static boolean validateNotification(NotifyBean bean, String notifyType) {

		try {
			NotificationHandler.validateNotification(bean, notifyType);
			return true;

		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return false;
		}
	}

	public static DWRResponse startDBToFileMigration(final String backupId, final String nameNodeId, final int hostId,
			final String path, final boolean isAutoDiagnose) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("DB to file migration request received");
		DWRResponse dwrResponse = new DWRResponse();
		try {
			String backupFolder = null;
			if (path.endsWith(File.separator))
				backupFolder = path + backupId.replace(" ", "_").toLowerCase();
			else
				backupFolder = path + File.separator + backupId.replace(" ", "_").toLowerCase();

			final String backupDir = backupFolder;

			final String user = RemoteManager.getLoggedInUser();

			if (RemoteManager.isNonAdminAndDemo(user)) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}

			Thread th = new Thread() {
				public void run() {
					Connection connection = null;
					try {
						connection = CoreDBManager.getQueryIODBConnection();
						NNMIgrationInfoDAO.addMigrationInfo(connection, backupId, nameNodeId, hostId, backupDir, null);
						Host host = HostDAO.getHostDetail(connection, hostId);
						String dbName = NodeDAO.getDBNameForNameNodeMapping(connection, nameNodeId);
						if (dbName == null) {
							throw new Exception(
									"No database found with the selected NameNode. See logs for more details.");
						}
						// CustomTagDBConfig dbConfig =
						// CustomTagDBConfigManager.getConfig(dbName);

						if (isAutoDiagnose) {
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug("Initiating diagnosis for backup, id: " + backupId);
							NNMIgrationInfoDAO.updateMigrationInfo(connection, backupId, null, "Initiating diagnosis",
									null);

							String diagnosisId = "Diagnose_" + backupId;
							DiagnosisAndRepairManager.diagnoseForUser(diagnosisId, nameNodeId, 0, Long.MAX_VALUE, user);
							DiagnosisStatusBean dStat = DiagnosisAndRepairManager.getDiagnosisStatusForId(diagnosisId);
							while (dStat.getEndTime() == null && dStat.getError() == null) {
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e) {
									AppLogger.getLogger().fatal(e.getMessage(), e);
								}
								dStat = DiagnosisAndRepairManager.getDiagnosisStatusForId(diagnosisId);
							}
							if (dStat.getError() != null) {
								// Diagnosis failed

								NNMIgrationInfoDAO.updateMigrationInfo(connection, backupId, null,
										"Diagnosis failed, could not proceed with backup", null);

								AppLogger.getLogger().fatal(
										"Diagnosis failed, could not proceed with backup, error: " + dStat.getError());
							} else {
								// Diagnosis completed successfully. Proceed
								// with repair.

								if (AppLogger.getLogger().isDebugEnabled())
									AppLogger.getLogger().debug("Initiating repair for backup, id: " + backupId);
								NNMIgrationInfoDAO.updateMigrationInfo(connection, backupId, null, "Initiating repair",
										null);

								DiagnosisAndRepairManager.repairForUser(diagnosisId, nameNodeId, user);

								dStat = DiagnosisAndRepairManager.getDiagnosisStatusForId(diagnosisId);
								while (dStat.getEndTime() == null && dStat.getError() == null) {
									try {
										Thread.sleep(5000);
									} catch (InterruptedException e) {
										AppLogger.getLogger().fatal(e.getMessage(), e);
									}
									dStat = DiagnosisAndRepairManager.getDiagnosisStatusForId(diagnosisId);
								}

								if (dStat.getError() != null) {
									// Repair failed

									NNMIgrationInfoDAO.updateMigrationInfo(connection, backupId, null,
											"Repair failed, could not proceed with backup", null);

									AppLogger.getLogger().fatal(
											"Repair failed, could not proceed with backup, error: " + dStat.getError());
								} else {
									// Repair completed successfully. Proceed
									// with backup.
									if (AppLogger.getLogger().isDebugEnabled())
										AppLogger.getLogger().debug("Initiating backup, id: " + backupId);

									NNMIgrationInfoDAO.updateMigrationInfo(connection, backupId, null,
											"Initiating backup", null);

									QueryIOAgentManager.startDBToFileMigration(backupId, host, dbName, backupDir);
								}
							}
						} else {
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger()
										.debug("Initiating backup without prior diagnosis, id: " + backupId);

							QueryIOAgentManager.startDBToFileMigration(backupId, host, dbName, backupDir);
						}
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					} finally {
						if (connection != null) {
							try {
								CoreDBManager.closeConnection(connection);
							} catch (Exception e) {
								AppLogger.getLogger().fatal("Error closing connection: " + e.getLocalizedMessage(), e);
							}
						}
					}
				}
			};
			th.start();

			QueryIOResponse resp = new QueryIOResponse(true, "Request submitted sucessfully");

			dwrResponse.setDwrResponse(resp.isSuccessful(), resp.getResponseMsg(), 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		}

		return dwrResponse;
	}

	public static DWRResponse startDBToDBMigration(final String backupId, final String nameNodeId,
			final String destDbName, final boolean isAutoDiagnose) {
		DWRResponse dwrResponse = new DWRResponse();

		final String user = RemoteManager.getLoggedInUser();

		dwrResponse.setDwrResponse(true, "Request submitted successfully", 200);

		try {

			if (RemoteManager.isNonAdminAndDemo(user)) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}

			Thread th = new Thread() {
				public void run() {
					Connection connection = null;
					try {
						connection = CoreDBManager.getQueryIODBConnection();

						NNMIgrationInfoDAO.addMigrationInfo(connection, backupId, nameNodeId, -1, null, destDbName);
						String dbName = NodeDAO.getDBNameForNameNodeMapping(connection, nameNodeId);
						if (dbName == null) {
							throw new Exception(
									"No database found with the selected NameNode. See logs for more details.");
						}

						final DBConfigBean sourceDBBean = DBConfigDAO.getConnectionDetail(dbName);
						final DBConfigBean destinationDBBean = DBConfigDAO.getConnectionDetail(destDbName);
						boolean isCustomTagDB = sourceDBBean.isCustomTagDB();
						Node nameNode = NodeDAO.getNode(connection, nameNodeId);
						Host nameNodeHost = HostDAO.getHostDetail(connection, nameNode.getHostId());

						if (isAutoDiagnose) {
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug("Initiating diagnosis for backup, id: " + backupId);
							NNMIgrationInfoDAO.updateMigrationInfo(connection, backupId, null, "Initiating diagnosis",
									null);

							String diagnosisId = "Diagnose_" + backupId;
							DiagnosisAndRepairManager.diagnoseForUser(diagnosisId, nameNodeId, 0, Long.MAX_VALUE, user);
							DiagnosisStatusBean dStat = DiagnosisAndRepairManager.getDiagnosisStatusForId(diagnosisId);
							while (dStat.getEndTime() == null && dStat.getError() == null) {
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e) {
									AppLogger.getLogger().fatal(e.getMessage(), e);
								}
								dStat = DiagnosisAndRepairManager.getDiagnosisStatusForId(diagnosisId);
							}
							if (dStat.getError() != null) {
								// Diagnosis failed

								NNMIgrationInfoDAO.updateMigrationInfo(connection, backupId, null,
										"Diagnosis failed, could not proceed with backup", null);

								AppLogger.getLogger().fatal(
										"Diagnosis failed, could not proceed with backup, error: " + dStat.getError());
							} else {
								// Diagnosis completed successfully. Proceed
								// with repair.

								if (AppLogger.getLogger().isDebugEnabled())
									AppLogger.getLogger().debug("Initiating repair for backup, id: " + backupId);
								NNMIgrationInfoDAO.updateMigrationInfo(connection, backupId, null, "Initiating repair",
										null);

								DiagnosisAndRepairManager.repairForUser(diagnosisId, nameNodeId, user);

								dStat = DiagnosisAndRepairManager.getDiagnosisStatusForId(diagnosisId);
								while (dStat.getEndTime() == null && dStat.getError() == null) {
									try {
										Thread.sleep(5000);
									} catch (InterruptedException e) {
										AppLogger.getLogger().fatal(e.getMessage(), e);
									}
									dStat = DiagnosisAndRepairManager.getDiagnosisStatusForId(diagnosisId);
								}

								if (dStat.getError() != null) {
									// Repair failed

									NNMIgrationInfoDAO.updateMigrationInfo(connection, backupId, null,
											"Repair failed, could not proceed with backup", null);

									AppLogger.getLogger().fatal(
											"Repair failed, could not proceed with backup, error: " + dStat.getError());
								} else {
									// Repair completed successfully. Proceed
									// with backup.
									if (AppLogger.getLogger().isDebugEnabled())
										AppLogger.getLogger().debug("Initiating backup, id: " + backupId);

									NNMIgrationInfoDAO.updateMigrationInfo(connection, backupId, null,
											"Initiating backup", null);

									DBConfigDAO.startMigration(sourceDBBean, destinationDBBean, isCustomTagDB, nameNode,
											nameNodeHost, true, backupId);
								}
							}
						} else {
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger()
										.debug("Initiating backup without prior diagnosis, id: " + backupId);

							DBConfigDAO.startMigration(sourceDBBean, destinationDBBean, isCustomTagDB, nameNode,
									nameNodeHost, true, backupId);
						}
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					} finally {
						try {
							CoreDBManager.closeConnection(connection);
						} catch (Exception e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}
					}
				}
			};
			th.start();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		}

		return dwrResponse;
	}

	public static ArrayList getAllBackupLists() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NNMIgrationInfoDAO.getMigrationInfoList(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
		} finally {
			if (connection != null) {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error closing connection: " + e.getLocalizedMessage(), e);
				}
			}
		}

		return null;
	}

	public static ArrayList getAllRestoreHistoryLists() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return NNRestoreInfoDAO.getRestoreInfoList(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
		} finally {
			if (connection != null) {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error closing connection: " + e.getLocalizedMessage(), e);
				}
			}
		}

		return null;
	}

	public static DWRResponse deleteBackupEntry(ArrayList backupIdList) {
		DWRResponse dwrResponse = new DWRResponse();
		Connection connection = null;
		try {
			if (RemoteManager.isNonAdminAndDemo(null)) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}

			connection = CoreDBManager.getQueryIODBConnection();
			StringBuilder backupIds = null;
			if ((backupIdList != null) && (backupIdList.size() > 0)) {
				backupIds = new StringBuilder();
				String id = null;
				for (int i = 0; i < backupIdList.size() - 1; i++) {
					id = String.valueOf(backupIdList.get(i));
					backupIds.append("'" + id + "'");
					backupIds.append(",");
				}
				id = String.valueOf("'" + backupIdList.get(backupIdList.size() - 1) + "'");
				backupIds.append(id);
			} else {
				throw new Exception("No backup Id specified for delete.");
			}

			if (backupIds != null) {
				ArrayList<NNDBMigrationInfo> list = NNMIgrationInfoDAO.getMigrationInfoSelectedList(connection,
						backupIds.toString());
				if ((list != null) && (list.size() > 0)) {
					NNDBMigrationInfo obj = null;
					for (int i = 0; i < list.size(); i++) {
						obj = list.get(i);
						if (obj.getBackupFolder() != null) {
							Host host = HostDAO.getHostDetail(connection, obj.getHostId());
							if (host == null) {
								throw new Exception("Host does not exists.");
							}
							QueryIOResponse resp = QueryIOAgentManager.deleteBackupData(host, obj.getBackupFolder());
						} else if (obj.getDbName() != null) {
							Connection customtagConnection = CoreDBManager.getCustomTagDBConnection(obj.getDbName());
							DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(obj.getDbName(),
									null);

							if (customtagConnection == null) {
								throw new Exception("Connection does not exists for db " + obj.getDbName());
							}
							DBBackupTools.dropBackupTables(customtagConnection, obj.getMigrationId(), props);
						}
					}
				}
				NNMIgrationInfoDAO.deleteMigrationInfoFromList(connection, backupIds.toString());
				dwrResponse.setDwrResponse(true, QueryIOConstants.BACKUP_DELETED_SUCCESS, 200);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
			dwrResponse.setDwrResponse(false,
					QueryIOConstants.BACKUP_DELETED_FAILED + " Reason: " + e.getLocalizedMessage(), 500);
		} finally {
			if (connection != null) {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error closing connection: " + e.getLocalizedMessage(), e);
				}
			}
		}

		return dwrResponse;
	}

	public static DWRResponse deleteRestoreEntry(ArrayList restoreIdList) {
		DWRResponse dwrResponse = new DWRResponse();
		Connection connection = null;
		try {
			if (RemoteManager.isNonAdminAndDemo(null)) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}

			connection = CoreDBManager.getQueryIODBConnection();
			StringBuilder restoreIds = null;
			if ((restoreIdList != null) && (restoreIdList.size() > 0)) {
				restoreIds = new StringBuilder();
				String id = null;
				for (int i = 0; i < restoreIdList.size() - 1; i++) {
					id = String.valueOf(restoreIdList.get(i));
					restoreIds.append("'" + id + "'");
					restoreIds.append(",");
				}
				id = String.valueOf("'" + restoreIdList.get(restoreIdList.size() - 1) + "'");
				restoreIds.append(id);

				NNRestoreInfoDAO.deleteRestoreInfoFromList(connection, restoreIds.toString());
				dwrResponse.setDwrResponse(true, QueryIOConstants.BACKUP_DELETED_SUCCESS, 200);
			} else {
				dwrResponse.setDwrResponse(false,
						QueryIOConstants.RESTORE_DELETED_FAILED + " Reason: No restore Id specified for delete.", 500);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
			dwrResponse.setDwrResponse(false,
					QueryIOConstants.RESTORE_DELETED_FAILED + " Reason: " + e.getLocalizedMessage(), 500);
		} finally {
			if (connection != null) {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error closing connection: " + e.getLocalizedMessage(), e);
				}
			}
		}

		return dwrResponse;
	}

	public Map<String, Object> getAllTagsForFile(String filePath, String namenodeId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			Configuration conf = ConfigurationManager.getConfiguration(connection, namenodeId);

			Map<String, Object> tags = getMetadata(conf, filePath);

			if (tags != null) {
				tags.remove(ColumnConstants.COL_TAG_VALUES_ACCESSTIME);
				tags.remove(ColumnConstants.COL_TAG_VALUES_BLOCKS);
				tags.remove(ColumnConstants.COL_TAG_VALUES_BLOCKSIZE);
				tags.remove(ColumnConstants.COL_TAG_VALUES_FILEPATH);
				tags.remove(ColumnConstants.COL_TAG_VALUES_LENGTH);
				tags.remove(ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME);
				tags.remove(ColumnConstants.COL_TAG_VALUES_OWNER);
				tags.remove(ColumnConstants.COL_TAG_VALUES_PERMISSION);
				tags.remove(ColumnConstants.COL_TAG_VALUES_REPLICATION);
				tags.remove(ColumnConstants.COL_TAG_VALUES_USERGROUP);
				tags.remove(ColumnConstants.COL_MIGRATIONINFO_COMPRESSION_TYPE);
				tags.remove(ColumnConstants.COL_MIGRATIONINFO_ENCRYPTION_TYPE);
			}
			return tags;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return null;
	}

	public DWRResponse saveTagsForFile(String filePath, String namenodeId, String tagsJSONString) {
		DWRResponse response = new DWRResponse();

		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			Configuration conf = ConfigurationManager.getConfiguration(connection, namenodeId);

			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(tagsJSONString);

			List<UserDefinedTag> extraTags = new ArrayList<UserDefinedTag>();

			Iterator it = obj.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				AppLogger.getLogger().fatal("Rcvd prop: " + key + " : " + obj.get(key));
				extraTags.add(new UserDefinedTag(key, obj.get(key)));
			}

			TagFileRequest request = new TagFileRequest(RemoteManager.getLoggedInUser(), new Path(filePath), namenodeId,
					extraTags);
			request.process();

			response.setDwrResponse(true, "Tags saved successfully.", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);

			response.setDwrResponse(false, "Error occurred while saving tags. " + e.getMessage(), 500);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return response;
	}

	private Map<String, Object> getMetadata(Configuration conf, String filePath) throws Exception {
		Map<String, Object> metadata = null;

		if (conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID) == null) {

		} else {
			metadata = getObjectMetadata(conf, filePath, TableConstants.TABLE_HDFS_METADATA);

			if (metadata == null) {
				metadata = getObjectMetadata(conf, filePath,
						("DATATAGS_" + UserDefinedTagUtils.getFileExtension(filePath)).toUpperCase());
			}

			if (metadata == null) {
				metadata = getObjectMetadata(conf, filePath, null);
			}
		}

		return metadata;
	}

	private Map<String, Object> getObjectMetadata(Configuration conf, String filePath, String tableName)
			throws Exception {
		Map<String, Object> map = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet res = null;
		IDataTagParser tagParser = null;
		try {
			String connectionName = conf.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
			connection = CoreDBManager.getCustomTagDBConnection(connectionName);

			if (tableName == null) {
				tagParser = UserDefinedTagResourceFactory.getParserFromConstructor(conf, filePath, null, null);

				tableName = UserDefinedTagResourceFactory.getTableName(tagParser, filePath);
			}

			DatabaseMetaData meta = connection.getMetaData();
			res = meta.getTables(null, null, null, new String[] { "TABLE" });
			boolean found = false;
			while (res.next()) {
				if (res.getString("TABLE_NAME").equals(tableName)) {
					found = true;
				}
			}

			if (!found) {
				return null;
			}

			stmt = connection.prepareStatement(
					"SELECT * FROM " + tableName + " WHERE " + ColumnConstants.COL_TAG_VALUES_FILEPATH + "=?");

			stmt.setString(1, filePath);

			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(stmt);
			ResultSetMetaData rsmd = rs.getMetaData();

			if (rs.next()) {
				map = new HashMap<String, Object>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					map.put(rsmd.getColumnName(i), rs.getObject(rsmd.getColumnName(i)));
				}
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeResultSet(res);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception ex) {
				AppLogger.getLogger().fatal("Error closing database connection.", ex);
			}
		}
		return map;
	}

	// interruptReconstructFsImageDaemon
	// public static DWRResponse reconstructFsImage(String nameNodeId)
	// {
	// DWRResponse dwrResponse = new DWRResponse();
	// Connection connection = null;
	// try
	// {
	// connection = CoreDBManager.getQueryIODBConnection();
	// Node node = NodeDAO.getNode(connection, nameNodeId);
	// Host host = HostDAO.getHostDetail(connection, node.getHostId());
	// String restoreId = QueryIOAgentManager.reconstructFsImage(host,
	// nameNodeId);
	// dwrResponse.setDwrResponse(true, QueryIOConstants.RESTORE_STARTED + "
	// with id " + restoreId, 200);
	// }
	// catch (Exception e)
	// {
	// AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
	// dwrResponse.setDwrResponse(false, QueryIOConstants.BACKUP_FAILED + "
	// Reason: " + e.getLocalizedMessage(), 500);
	// }
	//
	// return dwrResponse;
	// }

	private static String[] getCombinations(String pattern) {
		String[] combos = null;
		String token = null;
		if (pattern.contains("*")) {
			pattern = pattern.replaceAll("[*]", "[1-255]");
		}
		if (pattern.contains("[")) {
			String replacement = pattern.substring(pattern.indexOf("["), pattern.indexOf("]") + 1);

			token = pattern.substring(pattern.indexOf("[") + 1, pattern.indexOf("]"));

			String[] splits = token.split("-");
			int start = Integer.parseInt(splits[0]);
			int end = Integer.parseInt(splits[1]);

			combos = new String[end - start + 1];
			int index = 0;
			for (int i = start; i <= end; i++) {
				combos[index++] = pattern.replace(replacement, String.valueOf(i));
			}
		} else {
			combos = new String[1];
			combos[0] = pattern;
		}
		return combos;
	}

	public static void generateCombinations(String pattern, int index, String current, ArrayList combinations) {
		if (index == 4) {
			combinations.add(current);
			return;
		}

		String[] splits = pattern.split("\\.");

		String[] combos = getCombinations(splits[index]);

		String prev = current;
		for (int i = 0; i < combos.length; i++) {
			if (index == 0) {
				current = String.valueOf(combos[i]);
			} else {
				current = prev + "." + combos[i];
			}
			if (!pattern.isEmpty())
				generateCombinations(pattern.trim(), index + 1, current, combinations);
		}

	}

	public static ArrayList checkHosts(String pattern) {
		ArrayList availableHosts = new ArrayList();
		ArrayList unreachableHosts = new ArrayList();

		String splitRegex = "[" + System.getProperty("line.separator") + "\t,]";

		String[] allPatterns = pattern.split(splitRegex);

		for (int n = 0; n < allPatterns.length; n++) {
			ArrayList combinations = new ArrayList();
			try {
				if (!allPatterns[n].isEmpty())
					generateCombinations(allPatterns[n].trim(), 0, null, combinations);

				int timeout = 5000;

				combinations.removeAll(availableHosts);
				combinations.removeAll(unreachableHosts);

				for (int i = 0; i < combinations.size(); i++) {
					String host = (String) combinations.get(i);
					try {
						AppLogger.getLogger().debug("Checking host: " + host);
						if (InetAddress.getByName(host).isReachable(timeout)) {
							availableHosts.add(host);
						} else {
							unreachableHosts.add(host);
						}
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		ArrayList hostsArray = checkNewHosts(availableHosts, true);
		hostsArray.addAll(checkNewHosts(unreachableHosts, false));
		return hostsArray;
	}

	public static ArrayList checkNewHosts(ArrayList availableHosts, boolean isReachable) {
		ArrayList<SearchedHostDetail> list = new ArrayList<SearchedHostDetail>();
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			for (int i = 0; i < availableHosts.size(); i++) {
				SearchedHostDetail detail = new SearchedHostDetail();
				detail.setHostIP((String) availableHosts.get(i));
				detail.setAlreadyAdded(HostDAO.isHostAlreadyAdded(connection, (String) availableHosts.get(i)));

				if (detail.isAlreadyAdded()) {
					setAlreadyAddedNodesInfo(detail);
				}

				detail.setAvailable(isReachable);
				list.add(detail);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		for (int i = 0; i < list.size(); i++) {
			AppLogger.getLogger().fatal(list.get(i).getHostIP());
			AppLogger.getLogger().fatal(list.get(i).isAlreadyAdded());
		}
		return list;
	}

	public static void setAlreadyAddedNodesInfo(SearchedHostDetail hostDetail) {
		ArrayList<String> hostList = new ArrayList<String>();
		hostList.add(hostDetail.getHostIP());

		int hostId = (Integer) getHostIds(hostList).get(hostDetail.getHostIP());

		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Map<String, Boolean> map = NodeDAO.getAllNodesTypeForHost(connection, hostId);
			if (map.get(QueryIOConstants.NAMENODE) != null) {
				hostDetail.setNameNode(true);
			}

			if (map.get(QueryIOConstants.DATANODE) != null) {
				hostDetail.setDataNode(true);
			}

			if (map.get(QueryIOConstants.NODEMANAGER) != null) {
				hostDetail.setNodeManager(true);
			}

			if (map.get(QueryIOConstants.RESOURCEMANAGER) != null) {
				hostDetail.setResourceManager(true);
			}
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

	public static boolean validateConnection(String hostname, String user, String password, String privateKeyFilePath) {
		Host host = new Host();
		host.setHostIP(hostname);
		try {
			String userHome = OneTimeConfig.getRemoteUserHomeDirectory(host, user, password, privateKeyFilePath, 22); // TODO:
																														// Remove
																														// hard
																														// coded
																														// port.
			if (userHome != null && !userHome.equals(QueryIOConstants.EMPTY_STRING)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return false;
	}

	public static HashMap getHostIds(ArrayList<String> hosts) {
		Connection connection = null;
		HashMap map = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			map = HostDAO.getHostIds(hosts, connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return map;
	}

	public static DWRResponse insertHostInstaller(String hostIP, String userName, String password,
			String sshPrivateKeyFile, String dirPath, String rackName, String agentPort, String javaHomePath,
			String port, boolean isLocal, boolean isUpgrade, String dbPort) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(hostIP);
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			if (!dirPath.endsWith("/"))
				dirPath = dirPath + "/";
			dirPath += "QueryIOPackage/";

			Host host = new Host();
			host.setHostIP(hostIP);
			host.setInstallDirPath(dirPath);
			host.setRackName(rackName);
			host.setAgentPort(agentPort);
			if (isLocal) {
				host.setWindows(SystemUtils.IS_OS_WINDOWS);
			} else {
				host.setWindows(isWindows(host, userName, password, port, sshPrivateKeyFile));
			}

			if (javaHomePath.isEmpty()) {
				dwrResponse.setResponseMessage(QueryIOConstants.JAVAHOME_FAILURE);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			if (!isUpgrade) {
				if (HostDAO.isHostAlreadyAdded(connection, host.getHostIP())) {
					dwrResponse.setResponseMessage(QueryIOConstants.HOST_ALREADY_PRESENT);
					dwrResponse.setResponseCode(302); // This status code is
														// being used by
														// autmatic cluster
														// setup.
					return dwrResponse;
				}
			}

			// installation of hadoop failed
			if (!OneTimeConfig.installHadoop(host, userName, password, sshPrivateKeyFile, javaHomePath, port,
					isLocal)) {
				dwrResponse.setResponseMessage(QueryIOConstants.HADOOP_INSTALLATION_FAILS);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			if (!isUpgrade) {
				try {
					HostDAO.insertHost(connection, host);
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
					dwrResponse.setResponseMessage(QueryIOConstants.INSERTION_FAILURE);
					dwrResponse.setResponseCode(500);
					return dwrResponse;
				}

			}

			host = HostDAO.getHostDetail(connection, hostIP);
			if (!isUpgrade) {
				ArrayList colNames = new ArrayList();
				ArrayList colTypes = new ArrayList();

				colNames.add(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
				colTypes.add(QueryIOConstants.DATATYPE_TIMESTAMP);

				ArrayList systemAttributes = MonitorDAO.getHostSystemAttributes(connection);
				for (int j = 0; j < systemAttributes.size(); j++) {
					colNames.add(((SystemAttribute) systemAttributes.get(j)).getColumnName());
					colTypes.add(((SystemAttribute) systemAttributes.get(j)).getDataType());
				}
				MonitorDAO.createHostMonitorTable(connection, host.getId(), colNames, colTypes);

				colNames.clear();
				colTypes.clear();

				ArrayList liveAttributes = MonitorDAO.getHostLiveAttributes(connection);
				for (int j = 0; j < liveAttributes.size(); j++) {
					colNames.add(((LiveAttribute) liveAttributes.get(j)).getColumnName());
					colTypes.add(((LiveAttribute) liveAttributes.get(j)).getDataType());
				}
				MonitorDAO.createHostSummaryTable(connection, host.getId(), colNames, colTypes);

			}
			if (isUpgrade) {
				try {
					String home = host.getInstallDirPath();
					home = home.substring(0, home.indexOf("QueryIOPackage"));
					UpgradeCluster.backupHadoopEtc(host.getHostIP(), userName, password, sshPrivateKeyFile, port, home,
							false, isLocal);
				} catch (Exception e) {
					e.printStackTrace();
					AppLogger.getLogger().fatal("Error while updating etc.", e);
					dwrResponse.setResponseMessage("Error while updating etc.");
					dwrResponse.setResponseCode(500);
					dwrResponse.setTaskSuccess(false);
					return dwrResponse;
				}
			}

			dwrResponse = startHostInstaller(host.getId(), userName, password, sshPrivateKeyFile, port, isLocal);

			if (dwrResponse.isTaskSuccess()) {
				dwrResponse.setResponseMessage(QueryIOConstants.HOST_ADDED_SUCCESS);
				if (!isUpgrade) {
					updateMetadataFile(QueryIOConstants.METADATA_FILE_HOSTS, hostIP, dirPath, true);
				}
			}
			dwrResponse.setId(hostIP);

			try {
				QueryIOAgentManager.updateJavaHome(host, javaHomePath);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error while updating JavaHome.", e);
				dwrResponse.setResponseMessage("Error while updating JavaHome.");
				dwrResponse.setResponseCode(500);
				dwrResponse.setTaskSuccess(false);
				return dwrResponse;
			}
			try {
				QueryIOAgentManager.updateLoggerPropertiesFile(host);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error while updating Logger properties.", e);
				dwrResponse.setResponseMessage("Error while updating Logger properties.");
				dwrResponse.setResponseCode(500);
				dwrResponse.setTaskSuccess(false);
				return dwrResponse;
			}
			if (isUpgrade) {
				try {
					String home = host.getInstallDirPath();
					home = home.substring(0, home.indexOf("QueryIOPackage"));
					QueryIOAgentManager.updateHiveSite(host, host.getInstallDirPath(), dbPort);
				} catch (Exception e) {
					e.printStackTrace();
					AppLogger.getLogger().fatal("Error while updating hive-site.xml.", e);
					dwrResponse.setResponseMessage("Error while updating hive-site.xml.");
					dwrResponse.setResponseCode(500);
					dwrResponse.setTaskSuccess(false);
					return dwrResponse;
				}
			}
			try {
				String hadoopHome = host.getInstallDirPath();
				if (hadoopHome.endsWith("/"))
					hadoopHome = hadoopHome.substring(0, hadoopHome.length() - 1);
				QueryIOAgentManager.updateStartStopScripts(host, host.getInstallDirPath());
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error while updating start stop scripts.", e);
				dwrResponse.setResponseMessage("Error while updating start stop scripts.");
				dwrResponse.setResponseCode(500);
				dwrResponse.setTaskSuccess(false);
				return dwrResponse;
			}

			try {
				String hadoopHome = host.getInstallDirPath();
				if (hadoopHome.endsWith("/"))
					hadoopHome = hadoopHome.substring(0, hadoopHome.length() - 1);
				QueryIOAgentManager.updateHadoopPath(host, host.getInstallDirPath());
			} catch (Exception e) {
				AppLogger.getLogger()
						.fatal("Error while updating hadoop directory in core-site.xml file and hdfs-site.xml.", e);
				dwrResponse.setResponseMessage(
						"Error while updating hadoop directory in core-site.xml file and hdfs-site.xml.");
				dwrResponse.setResponseCode(500);
				dwrResponse.setTaskSuccess(false);
				return dwrResponse;
			}

			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

	}

	public static DWRResponse insertHostAutomatationInstaller(String hostIP, String userName, String password,
			String sshPrivateKeyFile, String rackName, String port, String id, boolean isLocal, String installUserHome,
			String installJavaHome, boolean isUpgrade) {

		DWRResponse response;

		String userHomeDir = null;

		if (isLocal)
			userHomeDir = installUserHome;
		else
			userHomeDir = getUserHomeDirectory(hostIP, userName, password, sshPrivateKeyFile, Integer.parseInt(port));

		if (userHomeDir != null)
			userHomeDir = userHomeDir.replaceAll("[\n\r]", "");

		String javaHome = null;

		if (isLocal)
			javaHome = installJavaHome;
		else
			javaHome = getJavaHome(hostIP, userName, password, sshPrivateKeyFile, Integer.parseInt(port.trim()));

		if (userHomeDir == null || javaHome == null) {
			response = new DWRResponse();
			response.setTaskSuccess(false);
			if (userHomeDir == null)
				response.setResponseMessage("Authentication Failure.");
			else if (javaHome == null)
				response.setResponseMessage("JavaHome not found.");
		} else {
			response = insertHostInstaller(hostIP, userName, password, sshPrivateKeyFile, userHomeDir, rackName,
					getQueryIOAgentPort(), javaHome, port, isLocal, isUpgrade, "");
		}
		response.setId(id);
		return response;
	}

	public static DWRResponse startHostInstaller(int id, String userName, String password, String sshPrivateKeyFile,
			String port, boolean isLocal) {
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, id);
			QueryIOAgentManager.startQueryIOAgent(host, userName, password, sshPrivateKeyFile, port, isLocal);

			startMonitor(connection, host);
			host = HostDAO.getHostDetail(connection, id);
			String status = host.getStatus();
			int attempts = 0;
			while (!status.equals(QueryIOConstants.STATUS_STARTED) && attempts < 120) {

				HostDAO.setHostMonitor(connection, host.getId(), true);

				Thread.sleep(1000);
				attempts++;
				host = HostDAO.getHostDetail(connection, id);
				status = host.getStatus();
			}

			if (status.equals(QueryIOConstants.STATUS_STARTED)) {

				dwrResponse.setDwrResponse(true, "Agent Monitor has started.", 200);
			} else {
				dwrResponse.setDwrResponse(false, "Agent could not be started successfully.", 500);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);

		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return dwrResponse;
	}

	public static DWRResponse addNameNodeInstaller(int hostId, String nodeId, String disk, String dirPath,
			String serverPort, String httpPort, String httpsPort, String jmxPort, String os3ServerPort,
			String secureOs3ServerPort, String hdfsoverftpServerPort, String ftpServerPort, String secureFtpPort,
			String connectionName, String analyticsDbName, boolean isRestartDataNode) {

		List<Integer> portList = new ArrayList<Integer>();
		portList.add(Integer.parseInt(serverPort));
		portList.add(Integer.parseInt(httpPort));
		portList.add(Integer.parseInt(httpsPort));
		portList.add(Integer.parseInt(jmxPort));
		portList.add(Integer.parseInt(os3ServerPort));
		portList.add(Integer.parseInt(secureOs3ServerPort));
		portList.add(Integer.parseInt(hdfsoverftpServerPort));
		portList.add(Integer.parseInt(ftpServerPort));
		portList.add(Integer.parseInt(secureFtpPort));
		// int hostId = Integer.parseInt(Id);
		Connection connection = null;
		Host host = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			// checking if the user has admin rights
			connection = CoreDBManager.getQueryIODBConnection();

			List namenodeList = NodeDAO.getAllNameNodes(connection);
			if (namenodeList.size() > 0) {
				Node namenode = (Node) namenodeList.get(0);
				if (HAStatusDAO.isHANode(connection, namenode.getId())) {
					dwrResponse.setResponseMessage("NameNode could not be added.\n"
							+ "Cluster is configured as High Avalability cluster. Nameservice federation is currently not supported with HA mode.");
					dwrResponse.setResponseCode(500);
					return dwrResponse;
				}
			}

			host = HostDAO.getHostDetail(connection, hostId);
			QueryIOResponse resp = QueryIOAgentManager.checkPortAvailability(host, portList);
			if (!resp.isSuccessful()) {
				dwrResponse.setResponseMessage("NameNode could not be added.\n" + resp.getResponseMsg());
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// checking if node is already present by this id
			if (NodeDAO.getNode(connection, nodeId) != null) {
				dwrResponse.setResponseMessage(
						"NameNode could not be added.\n" + QueryIOConstants.NODE_ID_ALREADY_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// creating node object
			Node node = new Node();
			node.setId(nodeId);
			node.setNodeType(QueryIOConstants.NAMENODE);
			node.setHostId(host.getId());
			node.setStatus(QueryIOConstants.STATUS_STOPPED);
			node.setJmxPort(String.valueOf(jmxPort));

			if (!dirPath.endsWith("/"))
				dirPath = dirPath + "/";
			dirPath += node.getId() + "/";

			// getting all configuration related to federated nameservices of
			// cluster
			String nameServices = "";
			ArrayList federatedKeys = new ArrayList();
			ArrayList federatedValues = new ArrayList();
			ArrayList datanodes = NodeDAO.getAllDatanodes(connection);
			ArrayList namenodes = getAllNonStandByNodes(connection);
			if (namenodes.size() > 0) {
				for (int i = 0; i < namenodes.size(); i++) {
					Node namenode = (Node) namenodes.get(i);
					nameServices += namenode.getId() + ",";
					federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + namenode.getId());
					federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + namenode.getId());
					federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + namenode.getId());
				}
				try {
					Node namenode = (Node) namenodes.get(0);
					Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
					federatedValues = QueryIOAgentManager.getConfig(namenodeHost, federatedKeys, namenode,
							"hdfs-site.xml");
					if (federatedValues == null) {
						throw new Exception("Host " + namenodeHost.getHostIP() + " is not responding. "
								+ "The configuration changes pertaining to this operation are to be applied on all hosts having datanodes/namenodes.");
					}
				} catch (Exception e) {
					dwrResponse.setDwrResponse(false, e.getMessage(), 500);
					return dwrResponse;
				}
			}
			nameServices += node.getId();
			federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + node.getId());
			federatedValues.add(host.getHostIP() + ":" + serverPort);
			federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + node.getId());
			federatedValues.add(host.getHostIP() + ":" + httpPort);
			federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + node.getId());
			federatedValues.add(host.getHostIP() + ":" + httpsPort);
			federatedKeys.add(DFSConfigKeys.DFS_NAMESERVICES);
			federatedValues.add(nameServices);

			// populate list for Default On Ingest Tag Parser
			ArrayList fileTypeList = new ArrayList();
			String[] arr = QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES.split(",");
			for (String str : arr) {
				str = str.trim();
				if (!fileTypeList.contains(str)) {
					fileTypeList.add(str);
					federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "." + str);
					federatedValues.add(QueryIOConstants.DEFAULT_ONINGEST_PARSER_CLASS_NAME);
				}
			}
			// populate list for Default On Ingest Tag Parser for WIKI Files
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES_WIKI);
			federatedValues.add(QueryIOConstants.DEFAULT_ONINGEST_PARSER_CLASS_NAME_WIKI);
			// for hive types
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.ADHOC_TYPE_CSV.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_CSV);
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.TYPE_JTL.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_CSV);
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.ADHOC_TYPE_JSON.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_JSON);
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.ADHOC_TYPE_LOG.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_LOG4J);
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.ADHOC_TYPE_ACCESSLOG.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_APACHE_LOG);
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.ADHOC_TYPE_IISLOG.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_IIS_LOG);
			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_CLASSNAME_PREFIX + "."
					+ QueryIOConstants.ADHOC_TYPE_PAIRS_EXTENSION.toLowerCase());
			federatedValues.add(QueryIOConstants.DEFAULT_DATA_TAG_PARSER_CLASS_NAME_KVPAIRS);

			String fileTypes = QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES.concat(",")
					.concat(QueryIOConstants.DEFAULT_ONINGEST_PARSER_FILETYPES_WIKI);
			fileTypes = fileTypes.concat(",").concat(QueryIOConstants.ADHOC_TYPE_CSV.toLowerCase()).concat(",")
					.concat(QueryIOConstants.ADHOC_TYPE_JSON.toLowerCase()).concat(",")
					.concat(QueryIOConstants.ADHOC_TYPE_LOG.toLowerCase()).concat(",")
					.concat(QueryIOConstants.ADHOC_TYPE_PAIRS_EXTENSION.toLowerCase()).concat(",")
					.concat(QueryIOConstants.ADHOC_TYPE_IISLOG.toLowerCase()).concat(",")
					.concat(QueryIOConstants.ADHOC_TYPE_ACCESSLOG.toLowerCase());

			federatedKeys.add(QueryIOConstants.CUSTOM_TAG_PARSER_FILETYPES);
			federatedValues.add(fileTypes);

			// updating all config at remote host
			QueryIOAgentManager.setNamenodeDefaultConfiguration(node, host, federatedKeys, federatedValues, dirPath,
					serverPort, httpPort, httpsPort, jmxPort, os3ServerPort, secureOs3ServerPort, hdfsoverftpServerPort,
					ftpServerPort, secureFtpPort, connectionName, analyticsDbName, disk, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			QueryIOAgentManager.updateHadoopEnv(host, node);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// format namenode
			QueryIOAgentManager.formatNamenode(host, node, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// updating all datanode configuration w.r.t. nameservices
			// federation
			for (int i = 0; i < datanodes.size(); i++) {
				Node datanode = (Node) datanodes.get(i);
				Host datanodeHost = HostDAO.getHostDetail(connection, datanode.getHostId());
				resp = QueryIOAgentManager.setAllNodeConfig(datanodeHost, datanode, federatedKeys, federatedValues);
				dwrResponse.setResponseMessage(resp.getResponseMsg());
				dwrResponse.setTaskSuccess(resp.isSuccessful());
				boolean isUpdateStatus = true;
				if (dwrResponse.isTaskSuccess()) {
					if (datanode.getStatus().equals(QueryIOConstants.STATUS_STARTED)) {
						if (isRestartDataNode) {
							DWRResponse dwrResp = stopNode(datanode.getId());
							if (dwrResp.isTaskSuccess()) {
								dwrResp = startNode(datanode.getId(), false);
								if (dwrResp.isTaskSuccess()) {
									dwrResponse.setResponseMessage(
											resp.getResponseMsg() + " DataNode restarted successfully.");
									dwrResponse.setTaskSuccess(true);
									isUpdateStatus = false;
								} else {
									dwrResponse.setResponseMessage(
											dwrResponse.getResponseMessage() + " " + dwrResp.getResponseMessage());
									dwrResponse.setTaskSuccess(false);
									isUpdateStatus = true;
								}
							} else {
								dwrResponse.setResponseMessage(
										dwrResponse.getResponseMessage() + " " + dwrResp.getResponseMessage());
								dwrResponse.setTaskSuccess(false);
								isUpdateStatus = true;
							}
						}
					}
					if (isUpdateStatus) {
						datanode.setStatus(QueryIOConstants.STATUS_STARTED_WITH_OUTDATED_CONFIGURATION);
						NodeDAO.updateStatus(connection, datanode);
					}
				} else {
					dwrResponse.setDwrResponse(false,
							"Host " + datanodeHost.getHostIP() + " is not responding. "
									+ "The configuration changes pertaining to this operation are to be applied on all hosts having namenodes and/or datanodes.",
							500);
				}
			}

			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			for (int i = 0; i < namenodes.size(); i++) {
				Node namenode = (Node) namenodes.get(i);
				Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
				resp = QueryIOAgentManager.setAllNodeConfig(namenodeHost, namenode, federatedKeys, federatedValues);
				dwrResponse.setResponseMessage(resp.getResponseMsg());
				dwrResponse.setTaskSuccess(resp.isSuccessful());
				if (!dwrResponse.isTaskSuccess()) {
					dwrResponse.setDwrResponse(false,
							"Host " + namenodeHost.getHostIP() + " is not responding. "
									+ "The configuration changes pertaining to this operation are to be applied on all hosts having namenodes and/or datanodes.",
							500);
				}
			}

			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// write node to db
			NodeDAO.insertNode(connection, node);
			NodeDAO.insertNameNodeDBMapping(connection, node.getId(), connectionName, analyticsDbName);
			VolumeDAO.addVolume(connection, node.getId(), disk, dirPath);

			// creating monitoring tables
			ArrayList colNames = new ArrayList();
			ArrayList colTypes = new ArrayList();
			colNames.add(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
			colTypes.add(QueryIOConstants.DATATYPE_TIMESTAMP);
			ArrayList systemAttributes = MonitorDAO.getNameNodeSystemAttributes(connection);
			for (int j = 0; j < systemAttributes.size(); j++) {
				colNames.add(((SystemAttribute) systemAttributes.get(j)).getColumnName());
				colTypes.add(((SystemAttribute) systemAttributes.get(j)).getDataType());
			}
			MonitorDAO.createNameNodeMonitorTable(connection, node.getId(), colNames, colTypes);

			colNames.clear();
			colTypes.clear();

			ArrayList liveAttributes = MonitorDAO.getNameNodeLiveAttributes(connection);
			for (int j = 0; j < liveAttributes.size(); j++) {
				colNames.add(((LiveAttribute) liveAttributes.get(j)).getColumnName());
				colTypes.add(((LiveAttribute) liveAttributes.get(j)).getDataType());
			}
			MonitorDAO.createNameNodeSummaryTable(connection, node.getId(), colNames, colTypes);

			AppLogger.getLogger().info("Monitor table added for " + node.getNodeType() + " on " + host.getHostIP());
			QueryIOServiceDAO.insert(connection, new QueryIOService(nodeId, QueryIOConstants.SERVICE_HDFS_OVER_FTP,
					QueryIOConstants.STATUS_STOPPED));
			QueryIOServiceDAO.insert(connection,
					new QueryIOService(nodeId, QueryIOConstants.SERVICE_OS3, QueryIOConstants.STATUS_STOPPED));
			QueryIOServiceDAO.insert(connection,
					new QueryIOService(nodeId, QueryIOConstants.SERVICE_HIVE, QueryIOConstants.STATUS_STOPPED));

			dwrResponse.setResponseCode(200);
			dwrResponse.setResponseMessage(QueryIOConstants.NAMENODE_ADDED_SUCCESS);
			dwrResponse.setTaskSuccess(true);

			updateMetadataFile(QueryIOConstants.METADATA_FILE_NN, host.getHostIP(), dirPath, true);

			String namespaceId = null;
			resp = QueryIOAgentManager.fetchNamespaceId(host, dirPath);
			if (resp.isSuccessful())
				namespaceId = resp.getResponseMsg();
			String blockPoolId = null;
			resp = QueryIOAgentManager.fetchBlockPoolId(host, dirPath);
			if (resp.isSuccessful())
				blockPoolId = resp.getResponseMsg();

			if (namespaceId != null && blockPoolId != null) {
				if (connectionName != null && !connectionName.isEmpty())
					updateNSMetadata(connectionName, namespaceId, blockPoolId);
			}

			// Register on ingest parser
			addContentParser(connection, node);

			addDefaultMRJob(connection, node);

			// Add Handling for hive.

			QueryIOAgentManager.updateHiveHadoopHome(host);

			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(200);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
	}

	public static DWRResponse startNodeInstaller(String nodeId, String userName, Boolean isEvaluation) {

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Starting node: " + nodeId);

		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {

			connection = CoreDBManager.getQueryIODBConnection();
			Node node = NodeDAO.getNode(connection, nodeId);
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			QueryIOResponse queryIOResponse = null;
			String nnId = null;

			queryIOResponse = QueryIOAgentManager.checkPortAvailability(host, node);
			if (!queryIOResponse.isSuccessful()) {
				dwrResponse.setDwrResponse(false, queryIOResponse.getResponseMsg(), 401);
				return dwrResponse;
			}

			if (node.getNodeType().equals(QueryIOConstants.NAMENODE) && HAStatusDAO.isHANode(connection, node.getId())
					&& (nnId = HAStatusDAO.getActiveNodeId(connection, node.getId())) != null) {
				if (NodeDAO.getNode(connection, nnId).getStatus().startsWith(QueryIOConstants.STATUS_STOPPED)) {
					dwrResponse.setDwrResponse(false,
							"Cannot start StandBy Namenode because Active NameNode of this cluster is not running. "
									+ "To start the HA-Enabled cluster, first start Active NameNode and then start StandBy NameNode",
							401);
					return dwrResponse;
				}

				queryIOResponse = QueryIOAgentManager.transitionNodeToActive(host, node, nnId);
				if (!queryIOResponse.isSuccessful()) {
					dwrResponse.setDwrResponse(false, queryIOResponse.getResponseMsg(), 401);
					return dwrResponse;
				}
			} else if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
				ResetRootPermissionsThread permissionResetter = new ResetRootPermissionsThread(host, node, "/",
						QueryIOConstants.DEFAULT_GROUP_NAME, (short) (777), false);
				permissionResetter.start();
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Calling agent start node method");

			QueryIOAgentManager.startNode(host, node, dwrResponse);

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Agent start node method invoked");

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Task Success: " + dwrResponse.isTaskSuccess());

			if (dwrResponse.isTaskSuccess()) {
				int dataFetchInterval = QueryIOConstants.DEFAULT_CONTROLLER_DATA_FETCH_INTERVAL;

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Controller Data Fetch Interval: " + dataFetchInterval + " seconds.");

				try {
					if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
						ControllerManager.startNameNodeController(connection, host, node, dataFetchInterval);
					} else if (node.getNodeType().equals(QueryIOConstants.DATANODE)) {
						ControllerManager.startDataNodeController(connection, host, node, dataFetchInterval);
					} else if (node.getNodeType().equals(QueryIOConstants.RESOURCEMANAGER)) {
						ControllerManager.startResourceManagerController(connection, host, node, dataFetchInterval);
					} else if (node.getNodeType().equals(QueryIOConstants.NODEMANAGER)) {
						ControllerManager.startNodeManagerController(connection, host, node, dataFetchInterval);
					}
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger()
								.debug("Monitor started for " + node.getNodeType() + " on " + host.getHostIP());

					NodeDAO.setNodeMonitor(connection, nodeId, true);
				} catch (Exception e) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Monitor could not be started for " + node.getNodeType() + " on "
								+ host.getHostIP() + ", Exception: " + e.getMessage(), e);
				}

				if (node.getNodeType().equals(QueryIOConstants.NAMENODE)) {
					String responseMsg = dwrResponse.getResponseMessage();
					if (dwrResponse.isTaskSuccess()) {
						QueryIOAgentManager.startQueryIOServices(host, node, dwrResponse);
						dwrResponse.setResponseMessage(responseMsg + "\n" + dwrResponse.getResponseMessage());

						if (dwrResponse.isTaskSuccess()) {
							node.setServiceStatus(QueryIOConstants.STATUS_LAUNCHING);
							NodeDAO.updateServiceStatus(connection, node);
							ControllerManager.startQueryIOServiceController(nodeId);
						}
					}

					if (!host.isWindows()) {
						Configuration conf = ConfigurationManager.getConfiguration(connection, nodeId);

						ArrayList configKeys = new ArrayList();
						ArrayList configValues = new ArrayList();

						configKeys.add(QueryIOConstants.HIVE_QUERYIO_HDFS_URI);
						configValues.add(conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY));

						configKeys.add(QueryIOConstants.HIVE_QUERYIO_DEFAULT_FS);
						configValues.add(conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY));

						configKeys.add(QueryIOConstants.HIVE_METASTORE_WAREHOUSE_DIR);
						configValues.add(conf.get(QueryIOConstants.HIVE_QUERYIO_WAREHOUSE_DIR));

						configKeys.add(QueryIOConstants.HIVE_MAPREDUCE_FRAMEWORK_NAME);
						if (isEvaluation) {
							configValues.add("local");
						} else {
							String framworkName = conf.get(QueryIOConstants.HIVE_QUERYIO_MAPREDUCE_FRAMEWORK_NAME_KEY);
							if (framworkName == null)
								framworkName = "local";
							configValues.add(framworkName);
						}
						configKeys.add(QueryIOConstants.HIVE_QUERYLOG_LOCATION);
						configValues.add(host.getInstallDirPath() + QueryIOConstants.HIVE_DIR_NAME + "/logs");
						// configValues.add(""); // Not to generate any history
						// file.

						String analyticsDbName = NodeDAO.getAnalyticsDBNameForNameNodeMapping(connection, nodeId);

						if (analyticsDbName != null) {
							DBConfigBean dbConfigBean = DBConfigDAO.getConnectionDetail(analyticsDbName);
							if (dbConfigBean != null) {
								configKeys.add(QueryIOConstants.HIVE_METASTORE_CONNECTION_DRIVER);
								configValues.add(dbConfigBean.getPrimaryDriverName());
								configKeys.add(QueryIOConstants.HIVE_METASTORE_CONNECTION_URL);
								configValues.add(dbConfigBean.getPrimaryConnectionURL());
								configKeys.add(QueryIOConstants.HIVE_METASTORE_CONNECTION_USERNAME);
								configValues.add(dbConfigBean.getPrimaryUsername());
								configKeys.add(QueryIOConstants.HIVE_METASTORE_CONNECTION_PASSWORD);
								configValues.add(dbConfigBean.getPrimaryPassword());
							}
							String databaseType = CustomTagDBConfigManager.getConfig(analyticsDbName)
									.getCustomTagDBType();
							if (databaseType.equalsIgnoreCase(QueryIOConstants.MYSQL_DB)) {
								configKeys.add(QueryIOConstants.HIVE_PROPERTY_ONE_NAME_FOR_MYSQL);
								configValues.add(QueryIOConstants.HIVE_PROPERTY_ONE_VALUE_FOR_MYSQL);
								configKeys.add(QueryIOConstants.HIVE_PROPERTY_TWO_NAME_FOR_MYSQL);
								configValues.add(QueryIOConstants.HIVE_PROPERTY_TWO_VALUE_FOR_MYSQL);
							}
						}

						String metadataDbName = NodeDAO.getDBNameForNameNodeMapping(connection, nodeId);

						if (metadataDbName != null) {
							DBConfigBean dbConfigBean = DBConfigDAO.getConnectionDetail(metadataDbName);
							if (dbConfigBean != null) {
								configKeys.add(QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_DRIVER_FILTER);
								configValues.add(dbConfigBean.getPrimaryDriverName());
								configKeys.add(QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_URL_FILTER);
								configValues.add(dbConfigBean.getPrimaryConnectionURL());
								configKeys.add(QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_USERNAME_FILTER);
								configValues.add(dbConfigBean.getPrimaryUsername());
								configKeys.add(QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_PASSWORD_FILTER);
								configValues.add(dbConfigBean.getPrimaryPassword());
							}
						}

						conf.set(QueryIOConstants.LOGGED_IN_USER_INSTALLER, userName);

						DWRResponse response = QueryIOAgentManager.updateHiveSiteConfiguration(nodeId, configKeys,
								configValues);

						if (response.isTaskSuccess()) {
							QueryIOAgentManager.startHiveServer(host, node, response);
							node.setHiveServiceStatus(QueryIOConstants.STATUS_LAUNCHING);

							if (response.isTaskSuccess()) {
								boolean flag = checkHiveStarted(nodeId);
								if (flag) {
									node.setHiveServiceStatus(QueryIOConstants.STATUS_STARTED);
									dwrResponse.setDwrResponse(true,
											dwrResponse.getResponseMessage() + " " + response.getResponseMessage(),
											response.getResponseCode());
								} else {
									node.setHiveServiceStatus(QueryIOConstants.STATUS_NOT_RESPONDING);
									dwrResponse.setDwrResponse(false, QueryIOConstants.HIVE_SERVER_START_FAILED, 400);
								}
							} else {
								node.setHiveServiceStatus(QueryIOConstants.STATUS_NOT_RESPONDING);
								dwrResponse.setDwrResponse(false,
										dwrResponse.getResponseMessage() + " " + response.getResponseMessage(),
										response.getResponseCode());
							}
						} else {
							node.setHiveServiceStatus(QueryIOConstants.STATUS_NOT_RESPONDING);
							dwrResponse.setDwrResponse(false,
									dwrResponse.getResponseMessage() + " " + response.getResponseMessage(),
									response.getResponseCode());
						}

						createHiveFolder(conf);

						NodeDAO.updateHiveServiceStatus(connection, node);
						ControllerManager.startHiveServiceController(nodeId);
					}
				}

				if (node.getNodeType().equals(QueryIOConstants.SECONDARYNAMENODE)
						|| node.getNodeType().equals(QueryIOConstants.JOURNALNODE))
					node.setStatus(QueryIOConstants.STATUS_STARTED);
				else
					node.setStatus(QueryIOConstants.STATUS_LAUNCHING);
				NodeDAO.updateStatus(connection, node);
			} else {
				return dwrResponse;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(
					"Monitor thread could not be started for nodeId: " + nodeId + " Exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}

		return dwrResponse;
	}

	public static DWRResponse addDataNodeInstaller(int hostId, String nodeId, String serverPort, String httpPort,
			String httpsPort, String ipcPort, String jmxPort, ArrayList disks, ArrayList volumePath, boolean isLocal) {

		List<Integer> portList = new ArrayList<Integer>();
		portList.add(Integer.parseInt(serverPort));
		portList.add(Integer.parseInt(httpPort));
		portList.add(Integer.parseInt(httpsPort));
		portList.add(Integer.parseInt(jmxPort));
		portList.add(Integer.parseInt(ipcPort));

		Connection connection = null;
		Host dataNodeHost = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			// checking if the user has admin rights
			connection = CoreDBManager.getQueryIODBConnection();
			dataNodeHost = HostDAO.getHostDetail(connection, hostId);

			QueryIOResponse resp = QueryIOAgentManager.checkPortAvailability(dataNodeHost, portList);
			if (!resp.isSuccessful()) {
				dwrResponse.setResponseMessage("Datanode could not be added.\n" + resp.getResponseMsg());
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// AuditLogger.getUserLogger(
			// WebContextFactory.get().getHttpServletRequest()
			// .getRemoteUser()).info(
			// "Add Datanode requsted on host" + dataNodeHost.getHostIP());

			// checking if atleast one namenode is added
			ArrayList namenodes = getAllNonStandByNodes(connection);
			if (namenodes.size() == 0) {
				dwrResponse.setResponseMessage(QueryIOConstants.NO_NAMENODE_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// checking if an node is already present by this id
			if (NodeDAO.getNode(connection, nodeId) != null) {
				dwrResponse.setResponseMessage(
						"DataNode could not be added.\n" + QueryIOConstants.NODE_ID_ALREADY_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// Check for nodemanager host mapping in resourcemanager's etc host
			// file.

			Node namenode = (Node) namenodes.get(0);
			Host namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());

			String hostName = QueryIOAgentManager.getHostName(namenodeHost);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("NameNode hostname: " + hostName);
			String hostAddress = QueryIOAgentManager.getHostAddress(namenodeHost);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("NameNode IP: " + hostAddress);
			if (!QueryIOAgentManager.hasMapping(dataNodeHost, hostName, hostAddress)) {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Found no mapping. pinging..");
				// if (!QueryIOAgentManager.isReachable(dataNodeHost,
				// namenodeHost.getHostIP())) {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Unable to ping");
				// dwrResponse
				// .setResponseMessage("Unable to ping NameNode by hostname from
				// DataNode host ["
				// + dataNodeHost.getHostIP()
				// + "]. In case you have Domain Name Server on your network
				// then please check with your network administrator on
				// NameNode's host resolution. Alternatively you can add
				// NameNode's HOST-IP mapping in /etc/hosts file on DataNode
				// host. Please note this will require administrative
				// priviliges.");
				dwrResponse.setResponseMessage("No IP-Hostname found of NameNode host on DataNode host ["
						+ dataNodeHost.getHostIP()
						+ "]. In case you have Domain Name Server on your network then please check with your network administrator on NameNode's host resolution. Alternatively you can add NameNode's HOST-IP mapping in /etc/hosts file on DataNode host. Please note this will require administrative priviliges.");
				dwrResponse.setResponseCode(500);
				return dwrResponse;
				// }
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Ping successfully");
			}

			// creating node Object
			Node node = new Node();
			node.setId(nodeId);
			node.setNodeType(QueryIOConstants.DATANODE);
			node.setHostId(dataNodeHost.getId());
			node.setStatus(QueryIOConstants.STATUS_STOPPED);
			node.setJmxPort(jmxPort);
			for (int i = 0; i < volumePath.size(); i++) {
				String dirPath = (String) volumePath.get(i);
				if (!dirPath.endsWith("/"))
					dirPath = dirPath + "/";
				dirPath += node.getId() + "/";
				volumePath.set(i, dirPath);
			}

			String dirPath = "";
			String disk = "";
			for (int i = 0; i < volumePath.size(); i++) {
				if (i != 0) {
					dirPath += ",";
					disk += ",";
				}
				dirPath += (String) volumePath.get(i);
				disk += (String) disks.get(i);
			}

			// getting all configuration related to federated nameservices of
			// cluster
			String nameServices = "";
			ArrayList federatedKeys = new ArrayList();
			ArrayList federatedValues = new ArrayList();
			for (int i = 0; i < namenodes.size(); i++) {
				namenode = (Node) namenodes.get(i);
				namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
				if (i != 0)
					nameServices += ",";
				nameServices += namenode.getId();
				federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + namenode.getId());
				federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + namenode.getId());
				federatedKeys.add(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + namenode.getId());
			}
			try {
				namenode = (Node) namenodes.get(0);
				namenodeHost = HostDAO.getHostDetail(connection, namenode.getHostId());
				federatedValues = QueryIOAgentManager.getConfig(namenodeHost, federatedKeys, namenode, "hdfs-site.xml");
				if (federatedValues == null) {
					throw new Exception("Host " + namenodeHost.getHostIP() + " is not responding. "
							+ "The configuration changes pertaining to this operation are to be applied on all hosts having datanodes.");
				}
			} catch (Exception e) {
				dwrResponse.setDwrResponse(false, e.getMessage(), 500);
				return dwrResponse;
			}
			federatedKeys.add(DFSConfigKeys.DFS_NAMESERVICES);
			federatedValues.add(nameServices);

			// updating all config at remote host
			QueryIOAgentManager.setDatanodeDefaultConfiguration(federatedKeys, federatedValues, dataNodeHost, node,
					dirPath, serverPort, httpPort, httpsPort, ipcPort, jmxPort, disk, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			QueryIOAgentManager.updateHadoopEnv(dataNodeHost, node);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// formatting node
			for (int i = 0; i < volumePath.size(); i++) {
				QueryIOAgentManager.formatDirectory(dataNodeHost, (String) volumePath.get(i), dwrResponse);
				if (!dwrResponse.isTaskSuccess()) {
					return dwrResponse;
				}
			}

			// write node to db
			NodeDAO.insertNode(connection, node);

			// create monitoring tables
			ArrayList colNames = new ArrayList();
			ArrayList colTypes = new ArrayList();

			colNames.add(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
			colTypes.add(QueryIOConstants.DATATYPE_TIMESTAMP);

			ArrayList systemAttributes = MonitorDAO.getDataNodeSystemAttributes(connection);
			for (int j = 0; j < systemAttributes.size(); j++) {
				colNames.add(((SystemAttribute) systemAttributes.get(j)).getColumnName());
				colTypes.add(((SystemAttribute) systemAttributes.get(j)).getDataType());
			}
			MonitorDAO.createDataNodeMonitorTable(connection, node.getId(), colNames, colTypes);

			colNames.clear();
			colTypes.clear();

			ArrayList liveAttributes = MonitorDAO.getDataNodeLiveAttributes(connection);
			for (int j = 0; j < liveAttributes.size(); j++) {
				colNames.add(((LiveAttribute) liveAttributes.get(j)).getColumnName());
				colTypes.add(((LiveAttribute) liveAttributes.get(j)).getDataType());
			}
			MonitorDAO.createDataNodeSummaryTable(connection, node.getId(), colNames, colTypes);

			// write volumes to db
			HadoopConfigManager.updateRackConfig(connection);

			VolumeDAO.addVolume(connection, node.getId(), disk, dirPath);

			AppLogger.getLogger()
					.info("Monitor table added for " + node.getNodeType() + " on " + dataNodeHost.getHostIP());

			HadoopConfigManager.updateHostsList(connection, true);

			dwrResponse.setResponseMessage(QueryIOConstants.DATANODE_ADDED_SUCCESS);
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseCode(200);
			updateMetadataFile(QueryIOConstants.METADATA_FILE_DN, dataNodeHost.getHostIP(), dirPath, true);
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
	}

	public static DWRResponse addResourceManagerInstaller(String nodeId, int hostId, String serverPort,
			String schedulerPort, String webAppPort, String adminPort, String jmxPort, String resourceTrackerPort,
			String jobHistoryServerPort, String jobHistoryWebappPort, String dirPath) {
		List<Integer> portList = new ArrayList<Integer>();
		portList.add(Integer.parseInt(serverPort));
		portList.add(Integer.parseInt(schedulerPort));
		portList.add(Integer.parseInt(webAppPort));
		portList.add(Integer.parseInt(adminPort));
		portList.add(Integer.parseInt(jmxPort));
		portList.add(Integer.parseInt(resourceTrackerPort));
		portList.add(Integer.parseInt(jobHistoryServerPort));
		portList.add(Integer.parseInt(jobHistoryWebappPort));

		Connection connection = null;
		Host host = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			// checking if the user has admin rights
			connection = CoreDBManager.getQueryIODBConnection();
			host = HostDAO.getHostDetail(connection, hostId);

			QueryIOResponse resp = QueryIOAgentManager.checkPortAvailability(host, portList);
			if (!resp.isSuccessful()) {
				dwrResponse.setResponseMessage("ResourceManager could not be added.\n" + resp.getResponseMsg());
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// checking if an node is already present by this id
			if (NodeDAO.getNode(connection, nodeId) != null) {
				dwrResponse.setResponseMessage(
						"Resource Manager could not be added.\n" + QueryIOConstants.NODE_ID_ALREADY_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// creating node Object
			Node node = new Node();
			node.setId(nodeId);
			node.setNodeType(QueryIOConstants.RESOURCEMANAGER);
			node.setHostId(host.getId());
			node.setStatus(QueryIOConstants.STATUS_STOPPED);
			node.setJmxPort(jmxPort);

			if (!dirPath.endsWith("/"))
				dirPath = dirPath + "/";
			String jhTempPath = "/tmp/" + nodeId + "/JobHistoryTemp/";
			String jhPath = "/tmp/" + nodeId + "/JobHistory/";

			// updating all config at remote host
			QueryIOAgentManager.setResourceManagerDefaultConfiguration(host, node, serverPort, schedulerPort,
					webAppPort, adminPort, jmxPort, resourceTrackerPort, jobHistoryServerPort, jobHistoryWebappPort,
					jhTempPath, jhPath, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			QueryIOAgentManager.updateYarnEnv(host, node);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// formatting node
			QueryIOAgentManager.formatDirectory(host, jhTempPath, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			QueryIOAgentManager.formatDirectory(host, jhPath, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// write node to db
			NodeDAO.insertNode(connection, node);

			// create monitoring tables
			ArrayList colNames = new ArrayList();
			ArrayList colTypes = new ArrayList();

			colNames.add(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
			colTypes.add(QueryIOConstants.DATATYPE_TIMESTAMP);

			ArrayList systemAttributes = MonitorDAO.getResourceManagerSystemAttributes(connection);
			for (int j = 0; j < systemAttributes.size(); j++) {
				colNames.add(((SystemAttribute) systemAttributes.get(j)).getColumnName());
				colTypes.add(((SystemAttribute) systemAttributes.get(j)).getDataType());
			}

			MonitorDAO.createResourceManagerMonitorTable(connection, node.getId(), colNames, colTypes);

			colNames.clear();
			colTypes.clear();

			ArrayList liveAttributes = MonitorDAO.getResourceManagerLiveAttributes(connection);
			for (int j = 0; j < liveAttributes.size(); j++) {
				colNames.add(((LiveAttribute) liveAttributes.get(j)).getColumnName());
				colTypes.add(((LiveAttribute) liveAttributes.get(j)).getDataType());
			}

			MonitorDAO.createResourceManagerSummaryTable(connection, node.getId(), colNames, colTypes);

			AppLogger.getLogger().info("Monitor table added for " + node.getNodeType() + " on " + host.getHostIP());
			dwrResponse.setResponseMessage(QueryIOConstants.RESOURCEMANAGER_ADDED_SUCCESS);
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseCode(200);
			updateMetadataFile(QueryIOConstants.METADATA_FILE_RM, host.getHostIP(), dirPath, true);
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
	}

	public static DWRResponse addNodeManagerInstaller(String nodeId, int hostId, String resourceManagerId,
			String localizerPort, String webAppPort, String jmxPort, String dirPath, boolean isLocal) {

		List<Integer> portList = new ArrayList<Integer>();
		portList.add(Integer.parseInt(localizerPort));
		portList.add(Integer.parseInt(webAppPort));
		portList.add(Integer.parseInt(webAppPort));
		portList.add(Integer.parseInt(jmxPort));

		Connection connection = null;
		Host nodeManagerHost = null;
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setTaskSuccess(false);
		dwrResponse.setId(nodeId);
		try {
			// checking if the user has admin rights
			connection = CoreDBManager.getQueryIODBConnection();
			nodeManagerHost = HostDAO.getHostDetail(connection, hostId);

			QueryIOResponse resp = QueryIOAgentManager.checkPortAvailability(nodeManagerHost, portList);
			if (!resp.isSuccessful()) {
				dwrResponse.setResponseMessage("Node Manager could not be added.\n" + resp.getResponseMsg());
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// checking if atleast one namenode is added
			Node rmNode = NodeDAO.getNode(connection, resourceManagerId);
			if (rmNode == null) {
				dwrResponse.setResponseMessage("ResourceManager not present by this Id");
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// checking if an node is already present by this id
			if (NodeDAO.getNode(connection, nodeId) != null) {
				dwrResponse.setResponseMessage(
						"Node Manager could not be added.\n" + QueryIOConstants.NODE_ID_ALREADY_PRESENT);
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// Check for nodemanager host mapping in resourcemanager's etc host
			// file.

			Host resourceManagerHost = HostDAO.getHostDetail(connection, rmNode.getHostId());

			String hostName = QueryIOAgentManager.getHostName(resourceManagerHost);
			AppLogger.getLogger().debug("ResourceManager hostname: " + hostName);
			String hostAddress = QueryIOAgentManager.getHostAddress(resourceManagerHost);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("ResourceManager IP: " + hostAddress);
			if (!QueryIOAgentManager.hasMapping(nodeManagerHost, hostName, hostAddress)) {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Found no mapping. pinging..");
				// if (!QueryIOAgentManager.isReachable(nodeManagerHost,
				// resourceManagerHost.getHostIP())) {
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Unable to ping");
				// dwrResponse
				// .setResponseMessage("Unable to ping ResourceManager by
				// hostname from NodeManager's host ["
				// + nodeManagerHost.getHostIP()
				// + "]. In case you have Domain Name Server on your network
				// then please check with your network administrator on
				// NameNode's host resolution. Alternatively you can add
				// ResourceManager's HOST-IP mapping in /etc/hosts file on
				// NodeManager host. Please note this will require
				// administrative priviliges.");
				dwrResponse.setResponseMessage(
						"No IP-Hostname mapping found on ResourceManager host of NodeManager's host ["
								+ nodeManagerHost.getHostIP()
								+ "]. In case you have Domain Name Server on your network then please check with your network administrator on NameNode's host resolution. Alternatively you can add ResourceManager's HOST-IP mapping in /etc/hosts file on NodeManager host. Please note this will require administrative priviliges.");
				dwrResponse.setResponseCode(500);
				return dwrResponse;
				// }
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Ping successfully");
			}

			// creating node Object
			Node node = new Node();
			node.setId(nodeId);
			node.setNodeType(QueryIOConstants.NODEMANAGER);
			node.setHostId(nodeManagerHost.getId());
			node.setStatus(QueryIOConstants.STATUS_STOPPED);
			node.setJmxPort(jmxPort);

			if (!dirPath.endsWith("/"))
				dirPath = dirPath + "/";
			String logDir = dirPath + node.getId() + "NodeManagerLog/";
			String localDir = dirPath + node.getId() + "NodeManagerLocal/";

			ArrayList keys = new ArrayList();
			keys.add(YarnConfiguration.RM_ADDRESS);
			keys.add(YarnConfiguration.RM_SCHEDULER_ADDRESS);
			keys.add(YarnConfiguration.RM_WEBAPP_ADDRESS);
			keys.add(YarnConfiguration.RM_ADMIN_ADDRESS);
			keys.add(YarnConfiguration.RM_RESOURCE_TRACKER_ADDRESS);

			ArrayList values = QueryIOAgentManager.getConfig(resourceManagerHost, keys, rmNode, "yarn-site.xml");
			if (values == null || values.size() != 5) {
				dwrResponse.setResponseMessage("Namenode Host not responding.");
				dwrResponse.setResponseCode(500);
				return dwrResponse;
			}

			// updating all config at remote host
			QueryIOAgentManager.setNodeManagerDefaultConfiguration(nodeManagerHost, node, (String) values.get(0),
					(String) values.get(1), (String) values.get(2), (String) values.get(3), (String) values.get(4),
					localizerPort, webAppPort, jmxPort, logDir, localDir, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			QueryIOAgentManager.updateYarnEnv(nodeManagerHost, node);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			// formatting node
			QueryIOAgentManager.formatDirectory(nodeManagerHost, logDir, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}
			QueryIOAgentManager.formatDirectory(nodeManagerHost, localDir, dwrResponse);
			if (!dwrResponse.isTaskSuccess()) {
				return dwrResponse;
			}

			// write node to db
			NodeDAO.insertNode(connection, node);

			// // create monitoring tables
			ArrayList colNames = new ArrayList();
			ArrayList colTypes = new ArrayList();

			colNames.add(ColumnConstants.COL_MONITORDATA_MONITOR_TIME);
			colTypes.add(QueryIOConstants.DATATYPE_TIMESTAMP);

			ArrayList systemAttributes = MonitorDAO.getNodeManagerSystemAttributes(connection);
			for (int j = 0; j < systemAttributes.size(); j++) {
				colNames.add(((SystemAttribute) systemAttributes.get(j)).getColumnName());
				colTypes.add(((SystemAttribute) systemAttributes.get(j)).getDataType());
			}

			MonitorDAO.createNodeManagerMonitorTable(connection, node.getId(), colNames, colTypes);

			colNames.clear();
			colTypes.clear();

			ArrayList liveAttributes = MonitorDAO.getNodeManagerLiveAttributes(connection);
			for (int j = 0; j < liveAttributes.size(); j++) {
				colNames.add(((LiveAttribute) liveAttributes.get(j)).getColumnName());
				colTypes.add(((LiveAttribute) liveAttributes.get(j)).getDataType());
			}

			MonitorDAO.createNodeManagerSummaryTable(connection, node.getId(), colNames, colTypes);

			AppLogger.getLogger()
					.info("Monitor table added for " + node.getNodeType() + " on " + nodeManagerHost.getHostIP());
			dwrResponse.setResponseMessage(QueryIOConstants.NODEMANAGER_ADDED_SUCCESS);
			dwrResponse.setTaskSuccess(true);
			dwrResponse.setResponseCode(200);
			updateMetadataFile(QueryIOConstants.METADATA_FILE_NM, nodeManagerHost.getHostIP(), dirPath, true);
			return dwrResponse;
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			dwrResponse.setResponseMessage(e.getMessage());
			dwrResponse.setTaskSuccess(false);
			dwrResponse.setResponseCode(500);
			return dwrResponse;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("QueryIODBConnection could not be closed: Exception: " + e.getMessage(), e);
			}
		}
	}

	public static void updateMetadataFile(String nodeType, String hostIP, String destDir, boolean isAdd) {
		FileInputStream fisCluster = null;
		FileOutputStream fos = null;
		StringBuffer sbFileName = null;
		Properties prop = null;
		String seperator = ".!";
		try {
			sbFileName = new StringBuffer(System.getProperty("user.home"));
			sbFileName.append(File.separator + QueryIOConstants.METADATA_FILE);

			prop = new Properties();
			fisCluster = new FileInputStream(sbFileName.toString());

			prop.load(fisCluster);
			String hostList = prop.getProperty(nodeType);
			if (hostList != null) {
				if (isAdd) {
					hostList = hostList.concat(hostIP + ":" + destDir + seperator);
				} else {
					if (hostList.contains(hostIP)) {
						int ind = hostList.indexOf(hostIP);
						String temp = hostList.substring(ind, hostList.indexOf(seperator, ind));
						hostList = hostList.replace(temp + seperator, "");
					}
				}
				prop.setProperty(nodeType, hostList);
				fos = new FileOutputStream(sbFileName.toString());
				prop.store(fos, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fisCluster != null) {
				try {
					fisCluster.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public static ArrayList<String> getAllCustomTagMetadataIds() {

		ArrayList<String> idsList = null;
		try {
			idsList = MetaDataTagManager.getAllCustomTagMetadataIds();
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error getting custom tag metadata IDs.", e);
		}
		if (idsList == null)
			idsList = new ArrayList<String>();
		return idsList;
	}

	// public static ArrayList<Map<String, String>>
	// getAllCustomTagsMetadataDetail() {
	//
	// try {
	// return MetaDataTagManager.getAllCustomTagsMetadataDetail();
	// } catch (Exception e) {
	// AppLogger.getLogger().fatal("Error getting custom tag metadata IDs.", e);
	// }
	// return null;
	// }

	public static Map<String, Object> getCustomTagMetaataDetailById(String tagID) {

		try {
			return MetaDataTagManager.getCustomTagMetaataDetailById(tagID);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error getting custom tag metadata IDs.", e);
		}
		return null;
	}

	public static JSONObject getAllCustomTagsMetadataDetail(String aoData) {

		try {
			return MetaDataTagManager.getAllCustomTagsMetadataDetail(aoData);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error getting custom tag metadata IDs.", e);
		}
		return null;
	}

	public static DWRResponse insertCustomTagMetadatData(String id, String namenodeId, String tableName,
			String metadata, String desc, boolean isActive, String dbType, String dataTaggingTimeInfo) {

		DWRResponse response = new DWRResponse();
		String fileType = null;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			String hiveTableName = null;

			AppLogger.getLogger().debug("dbType : " + dbType);
			AppLogger.getLogger().debug("tableName : " + tableName);

			String dbPoolName = getDBNameForNameNodeMapping(namenodeId);
			String hiveDbName = getAnalyticsDBNameForNameNodeMapping(namenodeId);

			DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(dbPoolName, null);

			boolean callTikaParser = true;
			boolean isHive = false;

			if (dbType.equalsIgnoreCase(hiveDbName)) {
				hiveTableName = tableName;
				isHive = true;
				if (tableName.equalsIgnoreCase("0")) {
					fileType = "ALL_FILES";
				} else {
					Map<String, String> map = fillJSONKeysForCustomTagMetaData(namenodeId, tableName, metadata);
					metadata = map.get(json);
					fileType = map.get(RemoteManager.fileType);
					String hiveFileType = map.get(RemoteManager.hiveFileType);
					tableName = "DATATAGS_" + hiveFileType;
					callTikaParser = decideIfTikaIsCalled(dbPoolName, props, metadata);
				}
			} else if (dbType.equalsIgnoreCase(dbPoolName)) {
				callTikaParser = true;
				if (tableName.equalsIgnoreCase("0")) {
					fileType = "ALL_FILES";
				} else {
					String tablePrefix = "DATATAGS_";
					fileType = tableName.toUpperCase().substring(tablePrefix.length());
					metadata = fillFileTypeInJSON(metadata, fileType);
				}
			}

			metadata = insertParserDetailInTag(metadata, dbType, fileType, namenodeId);

			JSONParser jsonParser = new JSONParser();
			JSONObject scheduleJSON = (JSONObject) jsonParser.parse(dataTaggingTimeInfo);

			JSONObject tagsJSON = (JSONObject) jsonParser.parse(metadata);

			IDataTagParser tagParser = UserDefinedTagResourceFactory.getParserFromConstructor(
					RemoteManager.getNameNodeConfiguration(namenodeId), fileType, tagsJSON, null);

			if (fileType.equalsIgnoreCase("ALL_FILES")) {
				MetaDataTagManager.addColumnInAllMetadataTables(namenodeId, props, tagsJSON);
			} else if (tagParser != null) {
				MetaDataTagManager.addColumnInMetadataTables(namenodeId, props, tableName, tagsJSON);
			}

			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

			String defaultPath = "/";
			if (isHive) {
				defaultPath = AdHocQueryDAO.getAdHocInfoFromTable(connection, namenodeId, hiveTableName)
						.getSourcePath();
				JSONObject metaJSON = (JSONObject) jsonParser.parse(metadata);
				metaJSON.put(QueryIOConstants.HIVETYPEPATH, defaultPath);
				metadata = metaJSON.toString();
			}
			AppLogger.getLogger().debug("updateHiveMetadata(metadata): " + metadata);
			ArrayList<String> jobNames = new ArrayList<String>();
			String applyNowJobName = null;
			if (isActive) {
				boolean isPostIngest = Boolean.parseBoolean(String.valueOf(scheduleJSON.get("isPostIngest")));
				if (isPostIngest) {
					JSONObject postIngestTimeDetail = (JSONObject) scheduleJSON.get("postIngestTimeDetail");
					if (postIngestTimeDetail != null) {
						String frequency = String.valueOf(postIngestTimeDetail.get("Frequency"));
						String timeUnit = String.valueOf(postIngestTimeDetail.get("timeUnit"));
						Date startTime = sdf.parse(String.valueOf(postIngestTimeDetail.get("StartingTime")));
						long endTime = 0L;
						long min = 0;
						int freq = 0;
						if (timeUnit != null) // Convert frequency to minutes
												// for each case
						{
							if (timeUnit.equalsIgnoreCase("Hours")) {
								min = Integer.parseInt(frequency) * 60;
								frequency = String.valueOf(min);
							}
						}
						if (!(frequency == null)) {
							freq = Integer.parseInt(frequency);
							endTime = startTime.getTime() + (freq * 60 * 1000); // milliseconds
						}

						List<Node> nodes = NodeDAO.getAllNodesForType(connection, QueryIOConstants.RESOURCEMANAGER);
						String jobName = QueryIOConstants.DATATAGGING_PREFIX + id + "_" + System.nanoTime();
						jobNames.add(jobName);
						String scheduleName = QueryIOConstants.DATATAGGING_PREFIX + id;

						String arguments = id + " " + "\"" + jobName + "\" " + defaultPath + " '"
								+ tagsJSON.toJSONString() + "' " + startTime.getTime() + " " + endTime + " "
								+ callTikaParser;
						// By default Recursive was true, and input path filter
						// was false.
						MapRedJobConfig jobConfig = new MapRedJobConfig(namenodeId, nodes.get(0).getId(), jobName,
								QueryIOConstants.DATATAGGING_GENERIC_PARSER_JOB_NAME,
								QueryIOConstants.DATATAGGING_GENERIC_PARSER_JOB_LIB_JAR_NAME, null,
								QueryIOConstants.DATATAGGING_GENERIC_PARSER_JOB_CLASS, arguments, true, false, null);

						MapRedJobConfigDAO.insert(connection, jobConfig);

						ArrayList mapredJobName = new ArrayList();
						mapredJobName.add(jobName);
						ScheduleManager.scheduleMapRedJobWithoutNotificationDataTagging(frequency, startTime.getTime(),
								mapredJobName, scheduleName);
						// Schedule job
					}
				}

				boolean isApplyTag = Boolean.parseBoolean(String.valueOf(scheduleJSON.get("applyTag")));
				if (isApplyTag) {
					JSONObject applyTagTimeDetail = (JSONObject) scheduleJSON.get("applyTagTimeDetail");
					if (applyTagTimeDetail != null) {
						boolean isApplyNow = Boolean.parseBoolean(String.valueOf(applyTagTimeDetail.get("isApplyNow")));
						if (isApplyNow) {
							List<Node> nodes = NodeDAO.getAllNodesForType(connection, QueryIOConstants.RESOURCEMANAGER);
							String jobName = QueryIOConstants.DATATAGGING_PREFIX + id + "_" + System.nanoTime();
							jobNames.add(jobName);
							String arguments = id + " " + "\"" + jobName + "\" " + defaultPath + " '"
									+ tagsJSON.toJSONString() + "' " + 0 + " " + System.currentTimeMillis() + " "
									+ callTikaParser;
							// By default Recursive was true, and input path
							// filter was false.
							MapRedJobConfig jobConfig = new MapRedJobConfig(namenodeId, nodes.get(0).getId(), jobName,
									QueryIOConstants.DATATAGGING_GENERIC_PARSER_JOB_NAME,
									QueryIOConstants.DATATAGGING_GENERIC_PARSER_JOB_LIB_JAR_NAME, null,
									QueryIOConstants.DATATAGGING_GENERIC_PARSER_JOB_CLASS, arguments, true, false,
									null);

							MapRedJobConfigDAO.insert(connection, jobConfig);
							applyNowJobName = jobName;
							// Execute job after adding Job detail in DB. See at
							// the end of this method.

						} else {
							Date scheduleTime = sdf.parse(String.valueOf(applyTagTimeDetail.get("scheduleTime")));

							List<Node> nodes = NodeDAO.getAllNodesForType(connection, QueryIOConstants.RESOURCEMANAGER);
							String jobName = QueryIOConstants.DATATAGGING_PREFIX + "NOW_" + id + "_"
									+ System.nanoTime();
							jobNames.add(jobName);
							String scheduleName = QueryIOConstants.DATATAGGING_PREFIX + "NOW_" + id;
							String arguments = id + " " + "\"" + jobName + "\" " + defaultPath + " '"
									+ tagsJSON.toJSONString() + "' " + 0 + " " + System.currentTimeMillis() + " "
									+ callTikaParser;
							// By default Recursive was true, and input path
							// filter was false.
							MapRedJobConfig jobConfig = new MapRedJobConfig(namenodeId, nodes.get(0).getId(), jobName,
									QueryIOConstants.DATATAGGING_GENERIC_PARSER_JOB_NAME,
									QueryIOConstants.DATATAGGING_GENERIC_PARSER_JOB_LIB_JAR_NAME, null,
									QueryIOConstants.DATATAGGING_GENERIC_PARSER_JOB_CLASS, arguments, true, false,
									null);

							MapRedJobConfigDAO.insert(connection, jobConfig);

							ArrayList mapredJobName = new ArrayList();
							mapredJobName.add(jobName);
							ScheduleManager.scheduleMapRedJobWithoutNotificationDataTagging(
									String.valueOf(SchedulerConstants.SCH_FREQUENCY_ONCE), scheduleTime.getTime(),
									mapredJobName, scheduleName);

							// Schedule job to run at scheduleTime
						}
					}
				}
			}
			String commaSeparatedjobNames = "";
			for (int i = 0; i < jobNames.size(); i++) {
				if (i == jobNames.size() - 1) {
					commaSeparatedjobNames += jobNames.get(i);
				} else {
					commaSeparatedjobNames += jobNames.get(i) + ",";
				}
			}
			MetaDataTagManager.insertCustomTagMetadatData(id, metadata, desc, isActive, dbType, fileType, namenodeId,
					tableName, dataTaggingTimeInfo, commaSeparatedjobNames);

			if (applyNowJobName != null) {
				ApplicationManager.runJobDataTagging(applyNowJobName);
			}

			response.setTaskSuccess(true);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error inserting custom tag metadata.", e);

			response.setTaskSuccess(false);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}

		}
		return response;
	}

	public static boolean isPostIngestJobRunning(String jobName) {
		ArrayList<String> applicationIdsList = getApplicationIdByJobName(jobName);
		AppLogger.getLogger().debug("applicationIdsList : " + applicationIdsList);
		boolean isJobRunning = false;
		for (String applicationId : applicationIdsList) {
			String applicationStatus = ApplicationManager.getApllicationStatus(applicationId);

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger()
						.debug("applicationStatus: " + applicationStatus + ", for application ID : " + applicationId);

			if (applicationStatus.equals(FinalApplicationStatus.UNDEFINED.toString())) {
				isJobRunning = true;
				break;
			}
		}
		return isJobRunning;
	}

	public static ArrayList<String> getApplicationIdByJobName(String jobName) {
		SummaryTable summary = ApplicationManager.getAllApplicationsSummary();
		ArrayList allJobs = summary.getRows();
		int applicationIdIndex = 0;
		int jobNameIndex = 2;
		ArrayList<String> applicationIdsList = new ArrayList<String>();

		String currentJobName = null;
		ArrayList currentRow = null;
		for (int i = 0; i < allJobs.size(); i++) {
			currentRow = (ArrayList) allJobs.get(i);
			currentJobName = (String) currentRow.get(jobNameIndex);
			AppLogger.getLogger().debug("currentjobName : " + currentJobName);
			currentJobName = currentJobName.replaceAll("\"", "");
			if (jobName.equals(currentJobName)) {
				applicationIdsList.add((String) currentRow.get(applicationIdIndex));
			}
		}
		return applicationIdsList;
	}

	private static boolean decideIfTikaIsCalled(String dbName, DBTypeProperties props, String json) {
		boolean callTikaParser = false;
		Connection customTagConnection = null;
		try {
			customTagConnection = CoreDBManager.getCustomTagDBConnection(dbName);
			Map colNames = CustomTagsDAO.getAllAvailableTags(customTagConnection, TableConstants.TABLE_HDFS_METADATA,
					props);
			if (colNames != null) {
				JSONObject jsonObj = (JSONObject) new JSONParser().parse(json);
				JSONArray tags = (JSONArray) jsonObj.get("Tags");
				JSONObject tagFirstElement = (JSONObject) tags.get(0);
				if (tagFirstElement != null) {
					JSONArray expressions = (JSONArray) tagFirstElement.get("Expressions");
					if (expressions != null) {
						JSONObject firstExpr = null;
						for (int i = 0; i < expressions.size(); i++) {
							firstExpr = (JSONObject) expressions.get(i);
							if (firstExpr != null) {
								String columnName = String.valueOf(firstExpr.get("Column"));
								if ((colNames.containsKey(columnName.toLowerCase()))
										|| (colNames.containsKey(columnName.toUpperCase()))) {
									callTikaParser = true;
									break;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Could not decide if tika is called for given tag.", e);
		} finally {
			try {
				CoreDBManager.closeConnection(customTagConnection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
		}

		return callTikaParser;
	}

	public static DWRResponse updateCustomTagMetadatDataIsColumnValue(String id, boolean isActive) {

		DWRResponse response = new DWRResponse();
		try {

			MetaDataTagManager.updateCustomTagMetadatDataIsColumnValue(id, isActive);
			response.setTaskSuccess(true);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error updating custom tag metadata.", e);
			response.setTaskSuccess(false);
		}
		return response;
	}

	public static DWRResponse updateCustomTagMetadatData(String id, String metadata, String desc, boolean isActive,
			String dbType, String dataTaggingTimeInfo) {

		DWRResponse response = new DWRResponse();

		HashMap<String, String> mapRedJobConfig = null;

		try {
			// if(dbType.equalsIgnoreCase("HIVE")) {
			// Map<String, String> map =
			// fillJSONKeysForCustomTagMetaData(namenode, tableName, metadata);
			// metadata = map.get(json);
			// }
			metadata = insertParserDetailInTag(metadata, dbType, fileType,
					getCustomTagMetaataDetailById(id).get("nameNodeId").toString());

			MetaDataTagManager.updateCustomTagMetadatData(id, metadata, desc, isActive, dataTaggingTimeInfo);

			mapRedJobConfig = updateMapRedConfiguration(id, getCustomTagMetaataDetailById(id).get("jobName").toString(),
					metadata, dataTaggingTimeInfo);
			for (Map.Entry<String, String> entry : mapRedJobConfig.entrySet()) {
				MetaDataTagManager.updateMapRedJobConfig(entry.getKey(), entry.getValue());
			}
			response.setTaskSuccess(true);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error updating custom tag metadata.", e);
			response.setTaskSuccess(false);
		}
		return response;
	}

	public static DWRResponse deleteCustomTagMetadatData(ArrayList<String> ids) {

		DWRResponse response = new DWRResponse();
		String jobName = null;
		ArrayList<String> allJobNames = new ArrayList<String>();
		ArrayList<String> runningTagsIds = new ArrayList<String>();
		try {
			for (String tagId : ids) {
				Map<String, String> map = getTagNameForCustomTagMetadata(tagId);
				jobName = map.get("jobName");
				if (isPostIngestJobRunning(jobName)) {
					runningTagsIds.add(tagId);
				} else {
					String fileType = map.get("fileType");
					if (fileType.equalsIgnoreCase("ALL_FILES")) {
						MetaDataTagManager.deleteColumnFromAllTables(map.get("nameNodeId"), map.get("columnName"));
					} else {
						// if
						// (fileType.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_IISLOG)
						// ||
						// fileType.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_ACCESSLOG))
						// fileType = QueryIOConstants.ADHOC_TYPE_LOG;
						MetaDataTagManager.deleteColumn(map.get("nameNodeId"), "DATATAGS_" + fileType,
								map.get("columnName"));
					}
					if (jobName != null && !jobName.trim().isEmpty()) {
						allJobNames.add(jobName); // To Delete Jobs
					}
				}
			}
			if (runningTagsIds.size() > 0) {
				ids.removeAll(runningTagsIds);
				response.setTaskSuccess(false);
			} else {
				response.setTaskSuccess(true);
			}
			if (ids.size() > 0) {
				MetaDataTagManager.deleteCustomTagMetadatData(ids);
			}
			if (allJobNames.size() > 0) {
				ApplicationManager.deleteAllJobs(allJobNames);
			}
			// response.setDwrResponse(true, "Tag deleted successfully..", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error deleting custom tag metadata.", e);
			response.setTaskSuccess(false);
		}
		return response;
	}

	public static boolean isTagExist(String id) {

		try {
			return MetaDataTagManager.isTagExist(id);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error getting custom tag metadata ID.", e);
		}
		return true;
	}

	public static boolean isQueryExist(String id) {
		try {
			return BigQueryManager.isQueryExist(id);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error getting custom tag metadata ID.", e);
		}
		return true;
	}

	public static DWRResponse activateCustomTagMetaData(boolean isActivate, ArrayList ids) {
		DWRResponse response = new DWRResponse();
		try {
			int totalUpdatesRecords = 0;
			totalUpdatesRecords = MetaDataTagManager.activateCustomTagMetadatData(ids, isActivate);
			if (totalUpdatesRecords > 0)
				response.setTaskSuccess(true);
			else
				response.setTaskSuccess(false);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error activating custom tag metadata.", e);
			response.setTaskSuccess(false);
		}
		return response;
	}

	private static Map<String, String> fillJSONKeysForCustomTagMetaData(String namenode, String tableName,
			String metadata) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		AdHocQueryBean bean = AdHocQueryManager.getAdHocQueryArguments(namenode, tableName);
		if (bean == null) {
			return map;
		}
		String jsonString = bean.getArguments();
		JSONObject arguments = (JSONObject) new JSONParser().parse(jsonString);
		JSONObject metadataObject = (JSONObject) new JSONParser().parse(metadata);
		AppLogger.getLogger().debug("arguments before process : " + metadataObject.toJSONString());

		JSONObject parseDetail = new JSONObject();
		parseDetail.put(delimiterUIJSON, arguments.get(delimiterDBJSON));
		parseDetail.put(encodingUIJSON, arguments.get(encodingDBSON));
		parseDetail.put(valueSeperatorUIJSON, arguments.get(valueSeperatorDBJSON));
		parseDetail.put(ifErrorOccurUIJSON, arguments.get(ifErrorOccurDBJSON));
		parseDetail.put(hasHeaderUIJSON, arguments.get(hasHeaderDBJSON));

		metadataObject.put(parseDetailsKey, parseDetail);
		String fType = bean.getType();
		if (fType.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_PAIRS))
			fType = QueryIOConstants.ADHOC_TYPE_PAIRS_EXTENSION;
		else if (QueryIOConstants.ADHOC_TYPE_MBOX.equalsIgnoreCase(fType))
			fType = "EML";

		String hiveType = fType;
		// if (fType.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_IISLOG) ||
		// fType.equalsIgnoreCase(QueryIOConstants.ADHOC_TYPE_ACCESSLOG))
		// hiveType = "LOG";
		metadataObject.put(fileType, fType);
		AppLogger.getLogger().debug("arguments after process : " + metadataObject.toJSONString());
		map.put(json, metadataObject.toJSONString());
		AppLogger.getLogger().debug("bean.getType().toUpperCase() : " + fType);
		map.put(fileType, fType.toUpperCase());
		map.put(hiveFileType, hiveType.toUpperCase());
		return map;
	}

	private static String fillFileTypeInJSON(String jsonString, String fileType) throws Exception {
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonString);
		jsonObject.put("FileType", fileType);
		return jsonObject.toJSONString();
	}

	private static String insertParserDetailInTag(String jsonString, String dbType, String fileType, String namenodeId)
			throws Exception {
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonString);
		JSONArray jsonArray = (JSONArray) jsonObject.get("Tags");
		for (Object obj : jsonArray) {

			JSONObject tag = (JSONObject) obj;
			String parserType = null;

			String dbPoolName = getDBNameForNameNodeMapping(namenodeId);
			String hiveDbName = getAnalyticsDBNameForNameNodeMapping(namenodeId);

			if (fileType.equalsIgnoreCase("ALL_FILES") || dbType.equalsIgnoreCase(dbPoolName)) {
				parserType = "TIKA";
				if (jsonObject.get("Attributes") == null) {
					AppLogger.getLogger().debug("Attributes key is null");
				} else {
					AppLogger.getLogger().debug("Attributes key is not null");
				}
				jsonObject.remove("Attributes");
			} else if (dbType.equalsIgnoreCase(hiveDbName)) {
				parserType = "LOGICAL";
			}

			tag.put("ParserType", parserType);
		}
		return jsonObject.toJSONString();
	}

	// Added For updating JOb Arguments in MapRedJobConfig Table
	private static HashMap<String, String> updateMapRedConfiguration(String id, String jobName, String jsonString,
			String dataTaggingTimeInfo) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		HashMap<String, String> configMap = new HashMap<String, String>();
		String[] jobNameArray = jobName.split(",");
		JSONObject scheduleJSON = (JSONObject) new JSONParser().parse(dataTaggingTimeInfo);
		JSONObject postIngestTimeDetail = (JSONObject) scheduleJSON.get("postIngestTimeDetail");
		boolean isPostIngest = Boolean.parseBoolean(String.valueOf(scheduleJSON.get("isPostIngest")));
		// checking for hiveTypePath
		JSONObject tagInfo = (JSONObject) scheduleJSON.get("TagInfo");
		String filePath;
		if (tagInfo.containsKey("hiveTypePath"))
			filePath = String.valueOf(tagInfo.get("hiveTypePath"));
		else
			filePath = "/";

		if (isPostIngest) {
			if (postIngestTimeDetail != null) {
				String frequency = String.valueOf(postIngestTimeDetail.get("Frequency"));
				String timeUnit = String.valueOf(postIngestTimeDetail.get("timeUnit"));
				long startTime = System.currentTimeMillis();
				// sdf.parse(String.valueOf(postIngestTimeDetail.get("StartingTime")));
				long endTime = 0L;
				long min = 0;
				int freq = 0;
				if (timeUnit != null) // Convert frequency to minutes
										// for each case
				{
					if (timeUnit.equalsIgnoreCase("Hours")) {
						min = Integer.parseInt(frequency) * 60;
						frequency = String.valueOf(min);
					}
				}

				if (frequency != null) {
					freq = Integer.parseInt(frequency);
					endTime = startTime + (freq * 60 * 1000); // milliseconds
				}

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("datataggingTimeInfo :" + dataTaggingTimeInfo);

				for (String job : jobNameArray) {
					StringBuilder arguments = new StringBuilder();
					// String arguments = id + " " + "\"" + jobName + "\" " +
					// defaultPath + " '" + tagsJSON.toJSONString()
					// + "' " + startTime.getTime() + " " + endTime + " " +
					// callTikaParser;
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("JobName : " + job);
					arguments.append(id).append(" ").append("\"").append(job).append("\" ").append(filePath)
							.append(" '").append(jsonString).append("' ").append(startTime).append(" ").append(endTime)
							.append(" ").append("true");
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Post Ingest Arguments :" + arguments);
					configMap.put(job, arguments.toString());
				}

			}
		} else {
			for (String job : jobNameArray) {
				StringBuilder arguments = new StringBuilder();
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("JobName : " + job);

				arguments.append(id).append(" ").append("\"").append(job).append("\" ").append(filePath).append(" '")
						.append(jsonString).append("' ").append("0").append(" ").append(System.currentTimeMillis())
						.append(" ").append("true");
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Ingest Arguments :" + arguments);
				configMap.put(job, arguments.toString());
			}
		}
		return configMap;
	}

	private static Map<String, String> getTagNameForCustomTagMetadata(String id) throws Exception {
		Map<String, String> values = new HashMap<String, String>();
		Map<String, Object> map = getCustomTagMetaataDetailById(id);
		String fileType = (String) map.get("fileType");
		String jsonString = (String) map.get("json");
		JSONObject jsonObj = (JSONObject) new JSONParser().parse(jsonString);
		JSONArray tags = (JSONArray) jsonObj.get("Tags");
		JSONObject tagFirstElement = (JSONObject) tags.get(0);
		String columnName = (String) tagFirstElement.get("TagName");
		values.put("fileType", fileType);
		values.put("columnName", jsonObj.toJSONString());
		values.put("nameNodeId", (String) map.get("nameNodeId"));
		values.put("jobName", (String) map.get("jobName"));
		return values;
	}

	public static SummaryTable getAllNameNodesSummaryTable(boolean isSummaryForReport) {
		return SummaryManager.getAllNameNodesSummaryTable(isSummaryForReport);
	}

	public static SummaryTable getAllDataNodesSummaryTable(boolean isSummaryForReport) {
		return SummaryManager.getAllDataNodesSummaryTable(isSummaryForReport);
	}

	public static ArrayList getAllNameNodeStatusSummary() {
		return SummaryManager.getAllNameNodeStatusSummary();
	}

	public static ArrayList getAllNameNodeReadWrites() {
		return SummaryManager.getAllNameNodeReadWrites();
	}

	public static ArrayList getAllDataNodeStatusSummary() {
		return SummaryManager.getAllDataNodeStatusSummary();
	}

	public static ArrayList getAllDataNodeMemoryInfo() {

		return SummaryManager.getAllDataNodeMemoryInfo();
	}

	public static ArrayList getDataNodeSummary(String nodeId, String interval) {
		return SummaryManager.getDataNodeSummary(nodeId, interval);
	}

	public static ArrayList getDataNodeMemoryInfo(String nodeId) {
		return SummaryManager.getDataNodeMemoryInfo(nodeId);
	}

	public static SummaryTable getAllDataNodesSummaryTableforHost(String hostIP, boolean isSummaryForReport) {
		return SummaryManager.getAllDataNodesSummaryTableforHost(hostIP, isSummaryForReport);

	}

	public static ArrayList getAllDataNodeStatusSummaryforHost(String hostIP) {
		return SummaryManager.getAllDataNodeStatusSummaryforHost(hostIP);
	}

	public static ArrayList getAllDataNodeMemoryInfoForHost(String hostIP) {
		return SummaryManager.getAllDataNodeMemoryInfoForHost(hostIP);

	}

	public static SummaryTable getAllResourceManagersSummaryTable(boolean isSummaryForReport) {
		return SummaryManager.getAllResourceManagersSummaryTable(isSummaryForReport);
	}

	public static SummaryTable getAllResourceManagersSummaryTableForHost(String hostIP) {
		return SummaryManager.getAllResourceManagersSummaryTableForHost(hostIP);
	}

	public static SummaryTable getAllNodeManagersSummaryTable(boolean isSummaryForReport) {
		return SummaryManager.getAllNodeManagersSummaryTable(isSummaryForReport);
	}

	public static ArrayList getResourceManagerAppsDetail() {
		return SummaryManager.getResourceManagerAppsDetail();
	}

	public static ArrayList getNodeManagerAppsDetail() {
		return SummaryManager.getNodeManagerAppsDetail();
	}

	public static ArrayList getNameNodeReadWritesIntervalBased(String nodeId, String interval) {
		return SummaryManager.getNameNodeReadWritesIntervalBased(nodeId, interval);
	}

	public static ArrayList getNameNodeSummary(String nodeId, String interval) {
		return SummaryManager.getNameNodeSummary(nodeId, interval);
	}

	public static ArrayList getAllNameNodeReadWritesBasedOnIP(String ipStr) {
		return SummaryManager.getAllNameNodeReadWritesBasedOnIP(ipStr);
	}

	public static SummaryTable getSummaryTableForNameNodeBasedOnIP(String ip) {
		return SummaryManager.getSummaryTableForNameNodeBasedOnIP(ip);
	}

	public static ArrayList getNameNodeStatusSummaryBasedOnIP(String ip) {
		return SummaryManager.getNameNodeStatusSummaryBasedOnIP(ip);
	}

	public static SummaryTable getAllNodeManagersSummaryTableForHost(String hostIP) {
		return SummaryManager.getAllNodeManagersSummaryTableForHost(hostIP);
	}

	public static ArrayList getAllDataNodeStatusSummaryforRack(String rackid) {
		return SummaryManager.getAllDataNodeStatusSummaryforRack(rackid);
	}

	public static ArrayList getResourceManagerAppsDetailForIp(String hostname) {

		return SummaryManager.getResourceManagerAppsDetailForIp(hostname);
	}

	public static ArrayList getResourceManagerAppsDetailForId(String id) {
		return SummaryManager.getResourceManagerAppsDetailForId(id);
	}

	public static ArrayList getAllDataNodeMemoryInfoForRack(String rackId) {
		return SummaryManager.getAllDataNodeMemoryInfoForRack(rackId);
	}

	public static SummaryTable getAllDataNodesSummaryTableforRack(String rackid, boolean isSummaryForReport) {
		return SummaryManager.getAllDataNodesSummaryTableforRack(rackid, isSummaryForReport);
	}

	public static ArrayList getNodeManagerAppsDetailForIp(String hostname) {
		return SummaryManager.getNodeManagerAppsDetailForIp(hostname);
	}

	public static ArrayList getNodeManagerAppsDetailForId(String id) {
		return SummaryManager.getNodeManagerAppsDetailForId(id);
	}

	public static ArrayList getAllMigrationDetails() {
		return MigrationManager.getAllMigrationDetails();
	}

	public static boolean stopMigration(int id) {
		return MigrationManager.stopMigration(id);
	}

	public static boolean deleteMigration(ArrayList id) {
		return MigrationManager.deleteMigration(id);
	}

	public static boolean resumeFTPMigration(int id, String userName, String password, boolean overwrite) {
		return MigrationManager.resumeFTPMigration(id, userName, password, overwrite);
	}

	public static boolean resumeS3Migration(int id, String accessKey, String secureAccessKey, boolean overwrite) {
		return MigrationManager.resumeS3Migration(id, accessKey, secureAccessKey, overwrite);
	}

	public static String startS3MigrationDS(String nodeId, String title, String dataStore, String s3DataSourceId,
			String bucketName, String innerKey, String hdfsPath, boolean importType, boolean secure, boolean unzip,
			String compressionType, String encryptionType) {
		return MigrationManager.startS3MigrationDS(nodeId, title, dataStore, s3DataSourceId, bucketName, innerKey,
				hdfsPath, importType, secure, unzip, compressionType, encryptionType);
	}

	public static JSONObject getAllAvailableEmailFolders(String dataConnection) {
		return MigrationManager.getAllAvailableEmailFolders(dataConnection);
	}

	public static String startSFTPMigration(String nodeId, String title, String dataStore, String hostName,
			String userName, String password, String hdfsPath, String sourcePath, boolean importType, boolean unzip,
			String compressionType, String encryptionType) {
		return MigrationManager.startSFTPMigration(nodeId, title, dataStore, hostName, userName, password, hdfsPath,
				sourcePath, importType, unzip, compressionType, encryptionType);
	}

	public static String startFTPMigration(String nodeId, String title, String dataStore, String hostName,
			String userName, String password, String hdfsPath, String sourcePath, boolean importType, boolean unzip,
			String compressionType, String encryptionType) {
		return MigrationManager.startFTPMigration(nodeId, title, dataStore, hostName, userName, password, hdfsPath,
				sourcePath, importType, unzip, compressionType, encryptionType);
	}

	public static String startFTPMigrationDS(String ndoeId, String title, String dataStore, String ftpDataSourceId,
			String hdfsPath, String sourcePath, boolean importType, boolean unzip, String compressionType,
			String encryptionType) {
		return MigrationManager.startFTPMigrationDS(ndoeId, title, dataStore, ftpDataSourceId, hdfsPath, sourcePath,
				importType, unzip, compressionType, encryptionType);
	}

	public static String exportToDataBase(String nodeId, String title, String dataStore, String driverClass,
			String connectionUrl, String username, String password, String hdfsPath, String responseJson,
			String dbdriverjar, String maxConn, String maxIdleConn, String waitTime) {
		return MigrationManager.exportToDataBase(nodeId, title, dataStore, driverClass, connectionUrl, username,
				password, hdfsPath, responseJson, dbdriverjar, maxConn, maxIdleConn, waitTime);
	}

	public static String startSSHMigration(String nodeId, String title, String dataStore, String hostName, String port,
			String userName, String password, String path, boolean isSSH, String hdfsPath, boolean isImportType,
			boolean unzip, String compressionType, String encryptionType) {
		return MigrationManager.startSSHMigration(nodeId, title, dataStore, hostName, port, userName, password, path,
				isSSH, hdfsPath, isImportType, unzip, compressionType, encryptionType);
	}

	public static String startHTTPMigration(String nodeId, String title, String dataStore, String baseURL,
			String userName, String password, String file, String encoding, String hdfsPath, boolean isImportType,
			String compressionType, String encryptionType) {
		return MigrationManager.startHTTPMigration(nodeId, title, dataStore, baseURL, userName, password, file,
				encoding, hdfsPath, isImportType, compressionType, encryptionType);
	}

	public static String startEmailMigration(String nodeId, String title, String dataStore, String dataConnection,
			String folder, String startDate, String endDate, String prefix, String suffix, String hdfsPath,
			String compressionType, String encryptionType) {
		return MigrationManager.startEmailMigration(nodeId, title, dataStore, dataConnection, folder, startDate,
				endDate, prefix, suffix, hdfsPath, compressionType, encryptionType);
	}

	public static MigrationInfo getByTitle(String title, int fileId) throws Exception {
		return MigrationManager.getByTitle(title, fileId);
	}

	public static String startHDFSMigration(String nodeId, String title, String dataStore, String hostName, String file,
			String user, String group, String hdfsPath, boolean isImportType, boolean unzip, String compressionType,
			String encryptionType) {
		return MigrationManager.startHDFSMigration(nodeId, title, dataStore, hostName, file, user, group, hdfsPath,
				isImportType, unzip, compressionType, encryptionType);
	}

	public static String startDataBaseMigration(String nodeId, String title, String dataStore, String driverClass,
			String connectionUrl, String username, String password, String hdfsPath, ArrayList<String> tables,
			String dbdriverjar, String compressionType, String encryption, boolean importType, String maxConn,
			String maxIdleConn, String waitTime) {
		return MigrationManager.startDataBaseMigration(nodeId, title, dataStore, driverClass, connectionUrl, username,
				password, hdfsPath, tables, dbdriverjar, compressionType, encryption, importType, maxConn, maxIdleConn,
				waitTime);
	}

	// ReportManger
	public static DWRResponse emailBigDataReport(ArrayList reportTypeList, ArrayList exportFormatList,
			ArrayList usersIdList, String namenodeId, String queryId) {
		return ReportManager.emailBigDataReport(reportTypeList, exportFormatList, usersIdList, namenodeId, queryId);
	}

	public static String viewGeneralReport(ArrayList reportTypeList, String exportFormat, String title,
			String startTime, String endTime) {
		return ReportManager.viewGeneralReport(reportTypeList, exportFormat, title, startTime, endTime);
	}

	public static String viewNodeReport(String nodeId, String exportFormat, String title, String startTime,
			String endTime) {
		return ReportManager.viewNodeReport(nodeId, exportFormat, title, startTime, endTime);
	}

	public static DWRResponse mailNodeReport(String nodeId, ArrayList exportFormatList, ArrayList usersIdList,
			String title, String startTime, String endTime) {
		return ReportManager.mailNodeReport(nodeId, exportFormatList, usersIdList, title, startTime, endTime);
	}

	public static DWRResponse mailGeneralReport(ArrayList reportTypeList, ArrayList exportFormatList,
			ArrayList usersIdList, String title, String startTime, String endTime) {
		return ReportManager.mailGeneralReport(reportTypeList, exportFormatList, usersIdList, title, startTime,
				endTime);
	}

	// RuleManager
	public static RuleBean getRuleBean(String ruleId) {
		return RuleManager.getRuleBean(ruleId);
	}

	public static DWRResponse addOrUpdateRule(String nodeId, String ruleId, String actionType, String severity,
			String notificationType, String alertRaisedNotificationSubject, String alertRaisedNotificationMessage,
			String alertResetNotificationSubject, String alertResetNotificationMessage, String[] attrNames,
			String[] conditions, String[] values, String[] aggregateFunctions, String[] durations) {
		return RuleManager.addOrUpdateRule(nodeId, ruleId, actionType, severity, notificationType,
				alertRaisedNotificationSubject, alertRaisedNotificationMessage, alertResetNotificationSubject,
				alertResetNotificationMessage, attrNames, conditions, values, aggregateFunctions, durations);
	}

	public static ArrayList getRuleList() {
		return RuleManager.getRuleList();

	}

	public static String startRule(String ruleId) {
		return RuleManager.startRule(ruleId);
	}

	public static String suspendRule(String ruleId) {
		return RuleManager.suspendRule(ruleId);
	}

	public static String deleteRule(String ruleId) {
		return RuleManager.deleteRule(ruleId);
	}

	// Billing Manager's Method
	public static String viewBillingReport(String exportFormat, String title, String startTime, String endTime) {
		return BillingManager.viewBillingReport(exportFormat, title, startTime, endTime);
	}

	public static void mailBillingReport(String exportFormat, ArrayList usersIdList, String title, String startTime,
			String endTime, boolean defaultReport) throws Exception {
		BillingManager.mailBillingReport(exportFormat, usersIdList, title, startTime, endTime, defaultReport);
	}

	public static SummaryTable getBillingReportSummaryTable(Timestamp startTime, Timestamp endTime) {
		return BillingManager.getBillingReportSummaryTable(startTime, endTime);
	}

	// GenericDBConfigManager
	public static String[] getAllDatabaseNames() throws FileNotFoundException {
		return GenericDBConfigDAO.getAllDatabaseNames();
	}

	// DBCOnfigManager
	public static SummaryTable getMigrationStatus() {
		return DBConfigManager.getMigrationStatus();
	}

	public static ArrayList getAllConnectionsName(boolean isHiveViewSelected) {
		return DBConfigDAO.getAllDBNameMode(isHiveViewSelected);
	}

	public static DBConfigBean getDBDetail(String connectionName) {
		return DBConfigDAO.getConnectionDetail(connectionName);

	}

	public static DWRResponse migrateDB(String sourceConnectionName, String destinationConnectionName,
			boolean createSchemaFlag) {
		return DBConfigManager.migrateDB(sourceConnectionName, destinationConnectionName, createSchemaFlag);

	}

	public static DWRResponse exportDDL(String sourceConnectionName) {
		return DBConfigManager.exportDDL(sourceConnectionName);
	}

	public static DWRResponse removeConnection(String connectionName) {
		return DBConfigManager.removeConnection(connectionName);
	}

	public static ArrayList getAllConnectionsNameForOperation() {
		return DBConfigManager.getAllConnectionsNameForOperation();
	}

	// DataSourceManager
	public static Object getDataSource(String id) {
		return DataSourceManager.getDataSource(id);
	}

	public static ArrayList getAllDataConnections() {
		return DataSourceManager.getAllDataConnections();
	}

	public static FTPDataSource getFTPDataSource(String id) {
		return DataSourceManager.getFTPDataSource(id);
	}

	public static SFTPDataSource getSFTPDataSource(String id) {
		return DataSourceManager.getSFTPDataSource(id);
	}

	public static S3DataSource getS3DataSource(String id) {
		return DataSourceManager.getS3DataSource(id);
	}

	public static HTTPDataSource getHTTPDataSource(String id) {
		return DataSourceManager.getHTTPDataSource(id);
	}

	public static HDFSDataSource getHDFSDataSource(String id) {
		return DataSourceManager.getHDFSDataSource(id);
	}

	public static SSHDataSource getSSHDataSource(String id) {
		return DataSourceManager.getSSHDataSource(id);
	}

	public static DWRResponse deleteDataConnection(ArrayList<String> id) {
		return DataSourceManager.deleteDataConnection(id);
	}

	public static DWRResponse addFTPDataSource(String id, String host, int port, String username, String password) {
		return DataSourceManager.addFTPDataSource(id, host, port, username, password);
	}

	public static DWRResponse updateFTPDataSource(String id, String host, int port, String username, String password) {
		return DataSourceManager.updateFTPDataSource(id, host, port, username, password);
	}

	public static DWRResponse addSFTPDataSource(String id, String host, int port, String username, String password) {
		return DataSourceManager.addSFTPDataSource(id, host, port, username, password);
	}

	public static DWRResponse updateSFTPDataSource(String id, String host, int port, String username, String password) {
		return DataSourceManager.updateSFTPDataSource(id, host, port, username, password);
	}

	public static DWRResponse updateS3DataSource(String id, String accessKey, String secretAccessKey) {
		return DataSourceManager.updateS3DataSource(id, accessKey, secretAccessKey);
	}

	public static DWRResponse updateHDFSDataSource(String id, String host, int port, String group, String username) {
		return DataSourceManager.updateHDFSDataSource(id, host, port, group, username);
	}

	public static DWRResponse updateEmailDataSource(String id, String emailAddress, String password,
			String mailServerAddress, String accountName, String protocol, String socket, String port,
			String connectionTimeOut, String readTimeOut) {
		return DataSourceManager.updateEmailDataSource(id, emailAddress, password, mailServerAddress, accountName,
				protocol, socket, port, connectionTimeOut, readTimeOut);
	}

	public static DWRResponse addEmailDataSource(String id, String emailAddress, String password,
			String mailServerAddress, String accountName, String protocol, String socket, String port,
			String connectionTimeOut, String readTimeOut) {
		return DataSourceManager.addEmailDataSource(id, emailAddress, password, mailServerAddress, accountName,
				protocol, socket, port, connectionTimeOut, readTimeOut);
	}

	public static DWRResponse addHDFSDataSource(String id, String host, int port, String group, String username) {
		return DataSourceManager.addHDFSDataSource(id, host, port, group, username);
	}

	public static DWRResponse addS3DataSource(String id, String accessKey, String secretAccessKey) {
		return DataSourceManager.addS3DataSource(id, accessKey, secretAccessKey);
	}

	public static DWRResponse updateHTTPDataSource(String id, String baseURL, String username, String password) {
		return DataSourceManager.updateHTTPDataSource(id, baseURL, username, password);

	}

	public static DWRResponse updateSSHDataSource(String id, String host, int port, String user, String pass,
			String key) {
		return DataSourceManager.updateSSHDataSource(id, host, port, user, pass, key);
	}

	public static DWRResponse addSSHDataSource(String id, String host, int port, String user, String pass, String key) {
		return DataSourceManager.addSSHDataSource(id, host, port, user, pass, key);
	}

	public static DWRResponse addHTTPDataSource(String id, String baseURL, String username, String password) {
		return DataSourceManager.addHTTPDataSource(id, baseURL, username, password);
	}

	public static DWRResponse updateDBDataSource(String id, String driver, String connUrl, String username,
			String password, String driverJar) {
		return DataSourceManager.updateDBDataSource(id, driver, connUrl, username, password, driverJar);
	}

	// PermissionManager

	public static DWRResponse addGroup(String groupName) {
		return PermissionsManager.addGroup(groupName);
	}

	public static ArrayList getAllUserNames() {
		return PermissionsManager.getAllUserNames();
	}

	public static ArrayList getUserForGroup(String groupName) {
		return PermissionsManager.getUserForGroup(groupName);
	}

	public static DWRResponse deleteFiles(String nodeId, String dirPath, ArrayList list) {

		return PermissionsManager.deleteFiles(nodeId, dirPath, list);
	}

	public static ArrayList getAllGroupNames() {
		return PermissionsManager.getAllGroupNames();
	}

	public static DWRResponse setOwnerAndPermissions(String nodeId, String path, String owner, String group,
			int permissions, boolean recursive) {
		return PermissionsManager.setOwnerAndPermissions(nodeId, path, owner, group, permissions, recursive);
	}

	public static DWRResponse deleteGroup(String groupName) {
		return PermissionsManager.deleteGroup(groupName);
	}

	// SnapshotManager
	public static ArrayList getSnapshots() {
		return SnapshotManager.getSnapshots();
	}

	public static boolean deleteSnapshots(ArrayList ids) {
		return SnapshotManager.deleteSnapshots(ids);
	}

	public static boolean addSnapshot(String id, String namenodeHost, String location) {
		return SnapshotManager.addSnapshot(id, namenodeHost, location);
	}

	public static boolean doesSnapshotExist(String id) {
		return SnapshotManager.doesSnapshotExist(id);
	}

	// ApplicatinManager
	public static DWRResponse runJob(String jobName) {
		// By default Recursive was true, and input path filter was false.
		return ApplicationManager.runJob(jobName, true, false, null);
	}

	public static boolean updateAdhocJobDetails(String jobName, String newJobName, String mainClass, String arguments,
			String pathPattern, String srcPath, String nnId, String rmId) {
		return ApplicationManager.updateAdhocJobDetails(jobName, newJobName, mainClass, arguments, pathPattern, srcPath,
				nnId, rmId);
	}

	public static boolean updateJobDetails(String jobName, String newJobName, String mainClass, String arguments,
			String nnId, String rmId) {
		return ApplicationManager.updateJobDetails(jobName, newJobName, mainClass, arguments, nnId, rmId);
	}

	public static DWRResponse deleteAllAdHocJobs(ArrayList<String> jobs) {
		return ApplicationManager.deleteAllAdHocJobs(jobs);
	}

	public static DWRResponse deleteAllJobs(ArrayList<String> jobs) {
		return ApplicationManager.deleteAllJobs(jobs);
	}

	public static boolean killApplication(String applicationId) {
		return ApplicationManager.killApplication(applicationId);
	}

	public static ArrayList getJobNameLists() {
		return ApplicationManager.getJobNameLists();
	}

	public static JSONObject getAllJobsList() {
		return ApplicationManager.getAllJobsList();
	}

	public static JSONObject getAllJobsList(String aoData) {
		return ApplicationManager.getAllJobsList(aoData);
	}

	public static JSONObject getNodeManagerLogsPath(String applicationId) {
		return ApplicationManager.getNodeManagerLogsPath(applicationId);
	}

	public static SummaryTable getAllAdhocJobsList() {
		return ApplicationManager.getAllAdhocJobsList();
	}

	public static SummaryTable getAllApplicationsSummary() {
		return ApplicationManager.getAllApplicationsSummary();
	}

	// AdHocQueryManager
	public static DWRResponse addAdHocQueryCSV(String adHocId, String nameNodeId, String rmId, String sourcePath,
			boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields,
			String delimiter, String valueSeparator, boolean isFirstRowHeader, String encoding,
			String isSkipAllRecordsString) {
		return AdHocQueryManager.addAdHocQueryCSV(adHocId, nameNodeId, rmId, sourcePath, parseRecursive, adHocTableName,
				fileName, filePathPattern, fields, delimiter, valueSeparator, isFirstRowHeader, encoding,
				isSkipAllRecordsString);
	}

	public static DWRResponse addAdHocQueryLOG(String adHocId, String nameNodeId, String rmId, String sourcePath,
			boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields,
			String pattern, String encoding) {
		return AdHocQueryManager.addAdHocQueryLOG(adHocId, nameNodeId, rmId, sourcePath, parseRecursive, adHocTableName,
				fileName, filePathPattern, fields, pattern, encoding);
	}

	public static DWRResponse addAdHocQueryJSON(String adHocId, String nameNodeId, String rmId, String sourcePath,
			boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields,
			String encoding) {
		return AdHocQueryManager.addAdHocQueryJSON(adHocId, nameNodeId, rmId, sourcePath, parseRecursive,
				adHocTableName, fileName, filePathPattern, fields, encoding);
	}

	public static DWRResponse addAdHocQueryIISLOG(String adHocId, String nameNodeId, String rmId, String sourcePath,
			boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields,
			String delimiter, boolean isFirstRowHeader, String encoding) {
		return AdHocQueryManager.addAdHocQueryIISLOG(adHocId, nameNodeId, rmId, sourcePath, parseRecursive,
				adHocTableName, fileName, filePathPattern, fields, delimiter, isFirstRowHeader, encoding);
	}

	public static DWRResponse addAdHocQueryAccessLog(String adHocId, String nameNodeId, String rmId, String sourcePath,
			boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields,
			String pattern, String encoding) {
		return AdHocQueryManager.addAdHocQueryAccessLog(adHocId, nameNodeId, rmId, sourcePath, parseRecursive,
				adHocTableName, fileName, filePathPattern, fields, pattern, encoding);
	}

	public static DWRResponse updateAdHocQuery(String adHocId, String nameNodeId, String rmId, String filePathPattern,
			String encoding) {
		return AdHocQueryManager.updateAdHocQuery(adHocId, nameNodeId, rmId, filePathPattern, encoding);
	}

	public static DWRResponse deleteAdHocQuery(ArrayList adHocIdList) {
		return AdHocQueryManager.deleteAdHocQuery(adHocIdList);
	}

	public static DWRResponse addAdHocQueryXML(String adHocId, String nameNodeId, String rmId, String sourcePath,
			boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields,
			String nodeName, String encoding) {
		return AdHocQueryManager.addAdHocQueryXML(adHocId, nameNodeId, rmId, sourcePath, parseRecursive, adHocTableName,
				fileName, filePathPattern, fields, nodeName, encoding);
	}

	public static DWRResponse addAdHocQueryRegex(String adHocId, String nameNodeId, String rmId, String sourcePath,
			boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields,
			String pattern, String encoding) {
		return AdHocQueryManager.addAdHocQueryRegex(adHocId, nameNodeId, rmId, sourcePath, parseRecursive,
				adHocTableName, fileName, filePathPattern, fields, pattern, encoding);
	}

	public static DWRResponse addAdHocQueryKVPairs(String adHocId, String nameNodeId, String rmId, String sourcePath,
			boolean parseRecursive, String adHocTableName, String fileName, String filePathPattern, String fields,
			String delimiter, String valueSeparator, String encoding) {
		return AdHocQueryManager.addAdHocQueryKVPairs(adHocId, nameNodeId, rmId, sourcePath, parseRecursive,
				adHocTableName, fileName, filePathPattern, fields, delimiter, valueSeparator, encoding);
	}

	public static JSONObject getDefaultColumns(String type) {
		return AdHocQueryManager.getDefaultColumns(type);
	}

	public static JSONObject getAdHocQueryInfoAll() {
		return AdHocQueryManager.getAdHocQueryInfoAll();
	}

	// Added for server side pagenation
	public static JSONObject getAdHocQueryInfoAll(String aoData) {
		return AdHocQueryManager.getAdHocQueryInfoAll(aoData);
	}

	// AlertManager
	public static ArrayList getAlertList(long fromTime) {
		return AlertManager.getAlertList(fromTime);
	}

	public static String deleteAlerts(long fromTime, String[] alertIdentifiers) {
		return AlertManager.deleteAlerts(fromTime, alertIdentifiers);
	}

	public static ArrayList getAlertListForRule(long fromTime, String ruleId) {
		return AlertManager.getAlertListForRule(fromTime, ruleId);
	}

	// BigQueryManager
	public static DWRResponse deleteBigQuery(String namenodeId, String queryId) {
		return BigQueryManager.deleteBigQuery(namenodeId, queryId);
	}

	public static DWRResponse saveBigQuery(String namenodeId, String dbName, String jsonProperties) {
		return BigQueryManager.saveBigQuery(namenodeId, dbName, jsonProperties);
	}

	public static DWRResponse saveChart(String queryId, String jsonProperties) {
		return BigQueryManager.saveChart(queryId, jsonProperties);
	}

	public static DWRResponse saveTable(String queryId, String jsonProperties) {
		return BigQueryManager.saveTable(queryId, jsonProperties);
	}

	public static DWRResponse saveQuery(String queryID, String desc, String namenodeId, String dbName, String qs) {
		return BigQueryManager.saveQuery(queryID, desc, namenodeId, dbName, qs);
	}

	public static DWRResponse dropBigQueryTable(String databaseName, String[] tableList) {
		return BigQueryManager.dropBigQueryTable(databaseName, tableList);
	}

	public static DWRResponse clearBigQueryTable(String databaseName, String[] tableList) {
		return BigQueryManager.clearBigQueryTable(databaseName, tableList);
	}

	public static SummaryTable viewSchemaBigQueryTable(String databaseName, String tableName) {
		return BigQueryManager.viewSchemaBigQueryTable(databaseName, tableName);

	}

	public static JSONObject getBigQueryInfo(String namenodeId, String queryId, String userName) {
		return BigQueryManager.getBigQueryInfo(namenodeId, queryId, userName);
	}

	public static JSONObject getChartPreferences() {
		return BigQueryManager.getChartPreferences();
	}

	public static JSONObject isQueryAdhoc(String nameNodeId, String queryId, int executionId) {
		return BigQueryManager.isQueryAdhoc(nameNodeId, queryId, executionId);
	}

	public static JSONObject getAllBigQueriesInfo(String namenodeId) {
		return BigQueryManager.getAllBigQueriesInfo(namenodeId);
	}

	public static DWRResponse getAllChartsInfo() {
		return BigQueryManager.getAllChartsInfo();
	}

	public static JSONObject getAllBigQueriesInfo(String namenodeId, String aoData) {
		return BigQueryManager.getAllBigQueriesInfo(namenodeId, aoData);
	}

	public static DWRResponse saveChartPreferences(String chartPreferencesJson) {
		return BigQueryManager.saveChartPreferences(chartPreferencesJson);
	}

	public static JSONObject executeQuery(String namenodeId, String queryId, String userName, boolean forceCreateNew) {
		return BigQueryManager.executeQuery(namenodeId, queryId, userName, forceCreateNew);
	}

	public static int getQueryExecutionId() {
		return BigQueryManager.getQueryExecutionId();
	}

	public static JSONObject isQueryComplete(int executionId) {
		return BigQueryManager.isQueryComplete(executionId);
	}

	public static JSONObject getResultTableName(String selectedTable, String nameNodeId) {
		return BigQueryManager.getResultTableName(selectedTable, nameNodeId);
	}

	public static String exportBigQueryReport(String namenodeId, String queryId, String formatType) {
		return BigQueryManager.exportBigQueryReport(namenodeId, queryId, formatType);
	}

	public static String getQueryFilterTableName(String tableName, String nameNodeId) {
		return BigQueryManager.getQueryFilterTableName(tableName, nameNodeId);
	}

	public static ArrayList getAllBigQueriesId(String namenodeId) {
		return BigQueryManager.getAllBigQueriesId(namenodeId);

	}

	public static JSONObject getSpreadSheetSlickResultsMetadata(String queryId, String namenodeId) {
		return BigQueryManager.getSpreadSheetSlickResultsMetadata(queryId, namenodeId);
	}

	public static JSONObject getSpreadSheetQueryStatus(String queryId, String namenodeId, String userName) {
		return BigQueryManager.getSpreadSheetQueryStatus(queryId, namenodeId, userName);
	}

	public static boolean deleteSpreadSheetQueryStatus(String queryId, String namenodeId, String userName) {
		return BigQueryManager.deleteSpreadSheetQueryStatus(queryId, namenodeId, userName);
	}

	public static ArrayList getSpreadSheets(String nameNodeId) {
		return BigQueryManager.getSpreadSheets(nameNodeId);
	}

	public static void getSpreadSheetSlickResults(String queryId, String namenodeId, int count, int offset,
			PrintWriter writer) {
		BigQueryManager.getSpreadSheetSlickResults(queryId, namenodeId, count, offset, writer);
	}

	public static ArrayList getAllSysReportsSchedules() {
		return ScheduleManager.getAllSysReportsSchedules();
	}

	public static boolean scheduleJob(String interval, String reportTime, ArrayList selectedFormat,
			String notificationType, String notificationMessage, ArrayList userList, ArrayList reportId, String nodeId,
			String schedName) {
		return ScheduleManager.scheduleJob(interval, reportTime, selectedFormat, notificationType, notificationMessage,
				userList, reportId, nodeId, schedName);

	}

	public static boolean updateJob(String interval, String reportTime, ArrayList selectedFormat,
			String notificationType, String notificationMessage, ArrayList userList, ArrayList reportId, String name,
			String group, String nodeId, Boolean notificationEnable) {
		return ScheduleManager.updateJob(interval, reportTime, selectedFormat, notificationType, notificationMessage,
				userList, reportId, name, group, nodeId, notificationEnable);
	}

	public static boolean updateQueryJob(String interval, String reportTime, ArrayList selectedFormat,
			String notificationType, String notificationMessage, ArrayList userList, String schedName, String group,
			ArrayList query, String nameNode, Boolean notificationEnable) {
		return ScheduleManager.updateQueryJob(interval, reportTime, selectedFormat, notificationType,
				notificationMessage, userList, schedName, group, query, nameNode, notificationEnable);
	}

	public static boolean updateMapRedJob(String interval, String scheduleTime, ArrayList mapRedJobName,
			String scheduleName, String notificationType, String notificationMessage, ArrayList userList, String group,
			Boolean notificationEnable) {
		return ScheduleManager.updateMapRedJob(interval, scheduleTime, mapRedJobName, scheduleName, notificationType,
				notificationMessage, userList, group, notificationEnable);
	}

	public static boolean scheduleJobWithoutNotification(String interval, String reportTime, ArrayList selectedFormat,
			ArrayList reportId, String nodeId, String schedName) {
		return ScheduleManager.scheduleJobWithoutNotification(interval, reportTime, selectedFormat, reportId, nodeId,
				schedName);
	}

	public static boolean checkSysReportScheduleId(String id) {
		return ScheduleManager.checkSysReportScheduleId(id);
	}

	public static DWRResponse deleteTriggers(ArrayList triggerList) {
		return ScheduleManager.deleteTriggers(triggerList);
	}

	public static ArrayList getAllTriggerDetails(String jobGroup, String jobName) {
		return ScheduleManager.getAllTriggerDetails(jobGroup, jobName);
	}

	public static boolean deleteJob(ArrayList key) {
		return ScheduleManager.deleteJob(key);
	}

	public static ArrayList getAllMapRedSchedules() {
		return ScheduleManager.getAllMapRedSchedules();
	}

	public static ArrayList getAllBigQuerySchedules() {
		return ScheduleManager.getAllBigQuerySchedules();
	}

	public static boolean scheduleQueryJob(String interval, String reportTime, ArrayList selectedFormat,
			String notificationType, String notificationMessage, ArrayList userList, String schedName, ArrayList query,
			String nameNode) {
		return ScheduleManager.scheduleQueryJob(interval, reportTime, selectedFormat, notificationType,
				notificationMessage, userList, schedName, query, nameNode);
	}

	public static ArrayList getTriggerDetails(String jobGroup, String jobName) {
		return ScheduleManager.getTriggerDetails(jobGroup, jobName);
	}

	public static boolean scheduleQueryJobWithoutNotification(String interval, String reportTime,
			ArrayList selectedFormat, String schedName, ArrayList query, String nameNode) {
		return ScheduleManager.scheduleQueryJobWithoutNotification(interval, reportTime, selectedFormat, schedName,
				query, nameNode);
	}

	public static boolean checkMapRedScheduleId(String ip) {
		return ScheduleManager.checkMapRedScheduleId(ip);
	}

	public static boolean scheduleMapRedJob(String interval, String scheduleTime, ArrayList mapRedJobName,
			String scheduleName, String notificationType, String notificationMessage, ArrayList userList) {
		return ScheduleManager.scheduleMapRedJob(interval, scheduleTime, mapRedJobName, scheduleName, notificationType,
				notificationMessage, userList);
	}

	public static boolean scheduleMapRedJobWithoutNotification(String interval, String scheduleTime,
			ArrayList mapRedJobName, String scheduleName) {
		return ScheduleManager.scheduleMapRedJobWithoutNotification(interval, scheduleTime, mapRedJobName,
				scheduleName);
	}

	public static DWRResponse scheduleNamespaceDiagnosis(String interval, String scheduleTime, String namenodeId,
			long startIndex, long endIndex, String scheduleName, boolean isNotitificationEnable,
			String notificationType, String notificationMessage, ArrayList userList) {
		return ScheduleManager.scheduleNamespaceDiagnosis(interval, scheduleTime, namenodeId, startIndex, endIndex,
				scheduleName, isNotitificationEnable, notificationType, notificationMessage, userList);
	}

	public static boolean checkBigQueryScheduleId(String id) {
		return ScheduleManager.checkBigQueryScheduleId(id);
	}

	// Added by Ranjana
	public static JSONArray getStatusofNodesforHost(int hostID, String status) {
		Connection connection = null;
		JSONArray jsonArray = new JSONArray();
		// JSONObject nodeDetails = new JSONObject();

		// HashMap<String, String> map = new HashMap<String, String>();
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList<Node> nodeList = NodeDAO.getAllNodesForHost(connection, hostID);
			for (Node node : nodeList) {
				AppLogger.getLogger().debug("node ID " + node.getId());
				if (status.equalsIgnoreCase(QueryIOConstants.START_NODES)) {
					startNode(node.getId(), false);
				} else {
					stopNode(node.getId());
				}
			}
			nodeList = NodeDAO.getAllNodesForHost(connection, hostID);
			AppLogger.getLogger().fatal("NodeList Size: " + nodeList.size());
			AppLogger.getLogger().fatal("NodeList details: " + nodeList);
			for (int i = 0; i < nodeList.size(); i++) {
				JSONObject nodeDetails = new JSONObject();
				nodeDetails.put("node",
						nodeList.get(i).getId().concat(QueryIOConstants.COLON).concat(nodeList.get(i).getStatus()));
				jsonArray.add(nodeDetails);
			}

			AppLogger.getLogger().fatal("NodeDetails: " + jsonArray);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return jsonArray;
	}
}