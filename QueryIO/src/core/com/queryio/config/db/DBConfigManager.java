package com.queryio.config.db;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.DatabaseConfigParser;
import com.queryio.common.database.HsqlServer;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.monitor.beans.SummaryTable;
import com.queryio.plugin.datatags.ColumnMetadata;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.userdefinedtags.common.UserDefinedTagDAO;

public class DBConfigManager {

	public static void createConnection(String connectionName, String connectionType,
			String primaryConnectionURL, String primaryUsername,
			String primaryPassword, String primaryDriverName,
			String jarFileName, boolean isPrimary, long maxConnections, long maxIdleConnections, long waitTimeMilliSeconds) throws Exception {
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Verifying connection...");
		Connection conn = null;
		try {
			Properties connectionProps = new Properties();
			connectionProps.put("user", primaryUsername);
			connectionProps.put("password", primaryPassword);
			File file = new File(EnvironmentalConstants.getJdbcDriverPath()
					+ File.separator + jarFileName);
			URL u = file.toURI().toURL();
			final URLClassLoader ucl = new URLClassLoader(new URL[] { u },
					Thread.currentThread().getContextClassLoader());
			Driver driver = (Driver)Class.forName(primaryDriverName, true, ucl).newInstance();
			conn  = driver.connect(primaryConnectionURL, connectionProps);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Connection is verified");
		
		try
		{
			DBConfigDAO.addDBConnection(connectionName, connectionType, primaryConnectionURL,
					primaryUsername, primaryPassword, primaryDriverName,
					jarFileName, isPrimary, maxConnections, maxIdleConnections, waitTimeMilliSeconds);
		}
		catch(Exception e)
		{
			throw e;
		}
		RemoteManager.replaceAgentDBConfigFile();
		DBConfigDAO.replaceDBConfigFromHadoop();

		new DatabaseConfigParser()
				.loadDatabaseConfiguration(EnvironmentalConstants
						.getDbConfigFilePath());

		CoreDBManager.initializeAllCustomTagDBConnections();
		RemoteManager.activateDBOnServer(true);
				
		try {			
			conn  = CoreDBManager.getCustomTagDBConnection(connectionName);
			DBTypeProperties typeProperties = CustomTagDBConfigManager.getDatabaseDataTypeMap(null, connectionType);
			if(!UserDefinedTagDAO.checkIfTableExists(conn, TableConstants.TABLE_HDFS_METADATA) && isPrimary)
			{
				UserDefinedTagDAO.createDatabaseTable(conn, typeProperties, TableConstants.TABLE_HDFS_METADATA, new TableMetadata("DEFAULT", null).getColumnData());
			}
			if(!UserDefinedTagDAO.checkIfTableExists(conn, TableConstants.TABLE_NS_METADATA) && isPrimary)
			{
				List<ColumnMetadata> list = new ArrayList<ColumnMetadata>();
				list.add(new ColumnMetadata(ColumnConstants.COL_NS_METADATA_KEY, String.class, 1024));
				list.add(new ColumnMetadata(ColumnConstants.COL_NS_METADATA_VALUE, String.class, 1024));
				UserDefinedTagDAO.createDatabaseTable(conn, typeProperties, TableConstants.TABLE_NS_METADATA, list);
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public static ArrayList getAllConnectionsName(boolean isHiveViewSelected) {
		return DBConfigDAO.getAllDBNameMode(isHiveViewSelected);
	}

	public static ArrayList getAllConnectionsNameForOperation() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			return DBConfigDAO.getAllDBNameForOperation(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(
						"Error closing database connection.", e);
			}
		}
		return null;
	}

	public static DBConfigBean getDBDetail(String connectionName) {
		return DBConfigDAO.getConnectionDetail(connectionName);

	}

	public static void updateConnection(String connectionName,
			String primaryConnectionURL, String primaryUsername,
			String primaryPassword, String primaryDriverName,
			String jarFileName, boolean isPrimary, long maxConnections, long maxIdleConnections, long waitTimeMilliSeconds) throws Exception {
		String jarFile = "";
		if(jarFileName == null || jarFileName.isEmpty()){
			jarFile = CustomTagDBConfigManager.getConfig(connectionName).getCustomTagDriverJarPath();
		}else{
			jarFile = EnvironmentalConstants.getJdbcDriverPath()
					+ File.separator + jarFileName;
		}
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Verifying connection...");
		Connection conn = null;
		try {
			Properties connectionProps = new Properties();
			connectionProps.put("user", primaryUsername);
			connectionProps.put("password", primaryPassword);
			File file = new File(jarFile);
			URL u = file.toURI().toURL();
			final URLClassLoader ucl = new URLClassLoader(new URL[] { u },
					Thread.currentThread().getContextClassLoader());
			Class.forName(primaryDriverName, true, ucl);
			conn = DriverManager.getConnection(primaryConnectionURL,
					connectionProps);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Connection is verified");
		
		DBConfigDAO.updateDBConnection(connectionName, primaryConnectionURL,
				primaryUsername, primaryPassword, primaryDriverName,
				jarFileName, maxConnections, maxIdleConnections, waitTimeMilliSeconds);
		RemoteManager.replaceAgentDBConfigFile();
		DBConfigDAO.replaceDBConfigFromHadoop();

		new DatabaseConfigParser()
				.loadDatabaseConfiguration(EnvironmentalConstants
						.getDbConfigFilePath());

		CoreDBManager.initializeAllCustomTagDBConnections();
		RemoteManager.activateDBOnServer(true);
		
		try {			
			conn  = CoreDBManager.getCustomTagDBConnection(connectionName);
			DBTypeProperties typeProperties = CustomTagDBConfigManager.getDatabaseDataTypeMap(connectionName, null);
			if(!UserDefinedTagDAO.checkIfTableExists(conn, TableConstants.TABLE_HDFS_METADATA) && isPrimary)
			{
				UserDefinedTagDAO.createDatabaseTable(conn, typeProperties, TableConstants.TABLE_HDFS_METADATA, new TableMetadata("DEFAULT", null).getColumnData());
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	public static DWRResponse migrateDB(String sourceConnectionName,
			String destinationConnectionName, boolean createSchemaFlag) {
		DWRResponse response = null;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			String namenodeId = NodeDAO.getNameNodeForDBNameMapping(connection,
					sourceConnectionName);
			Node namenode = null;
			Host namenodeHost = null;
			if (namenodeId != null) {
				namenode = NodeDAO.getNode(connection, namenodeId);
				namenodeHost = HostDAO.getHostDetail(connection,
						namenode.getHostId());
			}

			DBConfigBean sourceDBBean = DBConfigDAO
					.getConnectionDetail(sourceConnectionName);
			DBConfigBean destinationDBBean = DBConfigDAO
					.getConnectionDetail(destinationConnectionName);
			boolean isCustomTagDB = sourceDBBean.isCustomTagDB();
			response = DBConfigDAO.startMigration(sourceDBBean,
					destinationDBBean, isCustomTagDB, namenode, namenodeHost, createSchemaFlag, null);
			if (response.isTaskSuccess()) {
				DBConfigDAO.insertDBMigrationStatusEntry(connection,
						sourceConnectionName, destinationConnectionName,
						QueryIOConstants.DBMIGRATION_STATUS_RUNNING, null,
						System.currentTimeMillis(), -1);
			}

		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception caught while migrating DB",
					e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(
						"Error closing database connection.", e);
			}
		}
		return response;
	}
	
	public static DWRResponse exportDDL(String sourceConnectionName) {
		DWRResponse response = null;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			String namenodeId = NodeDAO.getNameNodeForDBNameMapping(connection,
					sourceConnectionName);
			Node namenode = null;
			Host namenodeHost = null;
			if (namenodeId != null) {
				namenode = NodeDAO.getNode(connection, namenodeId);
				namenodeHost = HostDAO.getHostDetail(connection,
						namenode.getHostId());
			}

			DBConfigBean sourceDBBean = DBConfigDAO
					.getConnectionDetail(sourceConnectionName);
			
			response = DBConfigDAO.startTableDDLExport(sourceDBBean, namenode, namenodeHost);
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Successfully exported DDL statements");
		}
		catch (Exception e) {
			AppLogger.getLogger().fatal("Exception caught while Exporting DDL statements", e);
			response = new DWRResponse();
			response.setDwrResponse(false, "Failed: " + e.getMessage(), 200);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(
						"Error closing database connection.", e);
			}
		}
		return response;
	}

	public static SummaryTable getMigrationStatus() {
		Connection connection = null;
		SummaryTable summaryTable = new SummaryTable();
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList colNames = new ArrayList();
			ArrayList colValues = null;
			colNames.add("Source Database");
			colNames.add("Destination Database");
			colNames.add("Start Time");
			colNames.add("End Time");
			colNames.add("Status");
			colNames.add("Progress");
			colNames.add("Error");
			summaryTable.setColNames(colNames);

			colValues = DBConfigDAO.getAllDBMigrationStatus(connection);
			summaryTable.setRows(colValues);
		} catch (Exception e) {
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(
					"Exception caught while getMigrationStatus: ", e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(
						"Error closing database connection.", e);
			}
		}
		return summaryTable;
	}

	public static DWRResponse removeConnection(String connectionName) {
		DWRResponse response = null;
		try {

			response = RemoteManager
					.getNameNodeForDBNameMapping(connectionName);
			if (!response.isTaskSuccess())
				return response;
			String nameNodeId = response.getId();
			if (nameNodeId == null) {
				response = DBConfigDAO.removeConnection(connectionName);
				RemoteManager.replaceAgentDBConfigFile();
				DBConfigDAO.replaceDBConfigFromHadoop();
				response = new DWRResponse();
				response.setTaskSuccess(true);
				response.setResponseMessage("Connection is removed.");
			} else {
				response = new DWRResponse();
				response.setResponseCode(500);
				response.setTaskSuccess(false);
				response.setResponseMessage("The connection is currently associated with NameNode Id: "
						+ nameNodeId);
			}
		} catch (Exception e) {
			response = new DWRResponse();
			response.setResponseCode(500);
			response.setTaskSuccess(false);
			response.setResponseMessage(e.getMessage());
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(
					"Exception caught removing DB connection", e);
		}
		return response;
	}

//	public static void startDBServer(String driverName, String jarFile,
//			String userName, String password, String connectionURL,
//			String connectionName, String currentConnectionName) {
//		try {
//
//			String dbPort = connectionURL.substring(
//					connectionURL.lastIndexOf(":") + 1,
//					connectionURL.lastIndexOf("/"));
//			String dbName = connectionURL.substring(connectionURL
//					.lastIndexOf("/") + 1);
//			if (!StaticUtilities.isProcessRunning("org.hsqldb.Server -port "
//					+ dbPort)) {
//				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(
//						"Attempting to start database server");
//				try {
//					// HSQL database
//					new HsqlServer(dbName, dbPort).startServer();
//				} catch (Exception ex) {
//					AppLogger.getLogger().fatal(
//							"Error Starting Database Process", ex);
//				}
//
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e1) {
//					// do nothing
//				}
//			}
//		} catch (Exception e) {
//			AppLogger
//					.getLogger()
//					.debug("Caught SQLException while re-initializing db conection",
//							e);
//		}
//	}
}