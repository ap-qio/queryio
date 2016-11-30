/*
 * @(#)  DBManager.java
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
package com.queryio.common.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.AppLogger;
//import com.queryio.common.util.CryptManager;

/**
 * This class acts as an interface for all the Database related funtions. This
 * class initializes the databaseConnections for both the QueryIO and archive
 * datbases. This has got the set Functions for the database pooling parameters.
 * This class is responsible for getting the QueryIO and archive db connections
 * and closing the same.
 * 
 * @author Exceed Consultancy Services
 */
@SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal")
public class CoreDBManager {
	protected static final Logger LOGGER = Logger.getLogger(CoreDBManager.class);
	/* database configuration of QueryIO data base */
	private static DBConfig queryIODBConfig = null;
	private static boolean isPerfect = false;

	/**
	 * @throws Exception
	 */
	private CoreDBManager() throws Exception {
		initialize();
	}

	public static void initialize() throws Exception {
		initializeQueryIODBConnection();
		initializeAllCustomTagDBConnections();
	}

	public static void initializeCustomTagDBConnection(String dbPoolName) throws Exception {
		CustomTagDBConfig dbConfig = CustomTagDBConfigManager.getConfig(dbPoolName);
		Driver driver = DBManager.getDriver(dbConfig.getCustomTagDriverJarPath(), dbConfig.getCustomTagDriverClass());

		// initializing the DBConnections
		DBConfig c = new DBConfig(dbConfig.getCustomTagPoolName(), dbConfig.getCustomTagMaxConn(),
				dbConfig.getCustomTagMaxIdleConn(), dbConfig.getCustomTagMaxWaitTime(),
				GenericObjectPool.WHEN_EXHAUSTED_GROW);

		// CryptManager cm = new CryptManager(dbConfig.getCustomTagPassword());
		// final String password =
		// cm.decryptData(dbConfig.getCustomTagPassword());

		DatabaseManager.setInitialProperties(c.getConnectionName(), c.getMaxDBConnections(), c.getWaitStrategyForDB(),
				c.getMaxDBWaitTime(), c.getMaxDBIdleConnections(), driver, dbConfig.getCustomTagUrl(),
				dbConfig.getCustomTagUserName(), dbConfig.getCustomTagPassword(), false);

		LOGGER.debug("CustomTag Database, class: " + driver.getClass().getName() + " Major: " + driver.getMajorVersion()
				+ " minor: " + driver.getMinorVersion());

	}

	public static void initializeAllCustomTagDBConnections() throws Exception {
		List list = CustomTagDBConfigManager.getAllCustomTagDBConfigList();

		for (int i = 0; i < list.size(); i++) {
			CustomTagDBConfig dbConfig = (CustomTagDBConfig) list.get(i);
			Driver driver = DBManager.getDriver(dbConfig.getCustomTagDriverJarPath(),
					dbConfig.getCustomTagDriverClass());

			// initializing the DBConnections
			DBConfig c = new DBConfig(dbConfig.getCustomTagPoolName(), dbConfig.getCustomTagMaxConn(),
					dbConfig.getCustomTagMaxIdleConn(), dbConfig.getCustomTagMaxWaitTime(),
					GenericObjectPool.WHEN_EXHAUSTED_GROW);

			// CryptManager cm = new
			// CryptManager(dbConfig.getCustomTagPassword());
			// final String password =
			// cm.decryptData(dbConfig.getCustomTagPassword());

			DatabaseManager.setInitialProperties(c.getConnectionName(), c.getMaxDBConnections(),
					c.getWaitStrategyForDB(), c.getMaxDBWaitTime(), c.getMaxDBIdleConnections(), driver,
					dbConfig.getCustomTagUrl(), dbConfig.getCustomTagUserName(), dbConfig.getCustomTagPassword(),
					false);

			LOGGER.debug("CustomTag Database, class: " + driver.getClass().getName() + " Major: "
					+ driver.getMajorVersion() + " minor: " + driver.getMinorVersion());

		}
	}

	// Stops all HSQL DBs running for custom tags and hive metastore
	// Running SHUTDOWN SQL on all DBs will stop DB Server process also
	public static void stopAllCustomTagDBs() throws Exception {
		List list = CustomTagDBConfigManager.getAllCustomTagDBConfigList();

		for (int i = 0; i < list.size(); i++) {
			CustomTagDBConfig dbConfig = (CustomTagDBConfig) list.get(i);

			if (dbConfig.getCustomTagDBType().equals(DatabaseConstants.DB_HSQL)) {
				HsqlServer.stopServer(CoreDBManager.getCustomTagDBConnection(dbConfig.getCustomTagPoolName()));
			}
		}
	}
	//
	// public static void startDBServer(String connectionURL) {
	// try {
	//
	// String dbPort = connectionURL.substring(connectionURL.lastIndexOf(":") +
	// 1, connectionURL.lastIndexOf("/"));
	// String dbName = connectionURL.substring(connectionURL.lastIndexOf("/") +
	// 1);
	// if (!StaticUtilities.isProcessRunning("org.hsqldb.Server -port " +
	// dbPort)) {
	// if (AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug("Attempting to start database server");
	// try {
	// // HSQL database
	// new HsqlServer(dbName, dbPort).startServer();
	// } catch (Exception ex) {
	// AppLogger.getLogger().fatal("Error Starting Database Process", ex);
	// }
	//
	// try {
	// Thread.sleep(5000);
	// } catch (InterruptedException e1) {
	// // do nothing
	// }
	// }
	// } catch (Exception e) {
	// AppLogger.getLogger().debug("Caught SQLException while re-initializing db
	// conection", e);
	// }
	// }

	/**
	 * initializes the ActiveConnection
	 * 
	 * @throws Exception
	 */
	public static void initializeQueryIODBConnection() throws Exception {
		final Driver driver = DBManager.getDriver(EnvironmentalConstants.getQueryIODatabaseDriverPath(),
				EnvironmentalConstants.getQueryIODatabaseDriverClass());

		// initializing the QueryIO DBConnections
		createQueryIODBConfig(EnvironmentalConstants.getQueryIODatabasePoolName(),
				EnvironmentalConstants.getQueryIODatabaseMaxConn(),
				EnvironmentalConstants.getQueryIODatabaseMaxIdleConn(),
				EnvironmentalConstants.getQueryIODatabaseMaxWaitTime(), GenericObjectPool.WHEN_EXHAUSTED_GROW);

		if (!queryIODBConfig.getConnectionName().equals(QueryIOConstants.DEFAULT_MONITOR_DB)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("set isPerfect to true");
			isPerfect = true;
		}
		// CryptManager cm = new
		// CryptManager(EnvironmentalConstants.getQueryIODatabasePassword());
		// final String password =
		// cm.decryptData(EnvironmentalConstants.getQueryIODatabasePassword());

		DatabaseManager.setInitialProperties(queryIODBConfig.getConnectionName(), queryIODBConfig.getMaxDBConnections(),
				queryIODBConfig.getWaitStrategyForDB(), queryIODBConfig.getMaxDBWaitTime(),
				queryIODBConfig.getMaxDBIdleConnections(), driver, EnvironmentalConstants.getQueryIODatabaseURL(),
				EnvironmentalConstants.getQueryIODatabaseUserName(),
				EnvironmentalConstants.getQueryIODatabasePassword(), false);

		LOGGER.debug("QueryIO Database, class: " + driver.getClass().getName() + " Major: " + driver.getMajorVersion()
				+ " minor: " + driver.getMinorVersion());
	}

	public static void createQueryIODBConfig(final String connectionName, final int MaxConn, final int MaxIdleConn,
			final long MaxWaitTime, final byte strategy) {
		queryIODBConfig = new DBConfig(connectionName, MaxConn, MaxIdleConn, MaxWaitTime, strategy);
	}

	/**
	 * reinitializes the QueryIOConnection
	 * 
	 * @throws Exception
	 */
	public static void reInitializeQueryIOConnection() throws Exception {
		initializeQueryIODBConnection();
	}

	public static void reInitializeCustomTagConnection() throws Exception {
		initializeAllCustomTagDBConnections();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public static Connection getQueryIODBConnection() throws Exception {
		Connection connection = null;
		connection = DatabaseManager.getConnection(queryIODBConfig.getConnectionName());
		if ((connection != null) && !connection.getAutoCommit()) {
			setAutoCommitOnConnection(connection, true);
		}
		return connection;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public static Connection getQueryIODBConnectionOnStartup() throws Exception {
		Connection connection = null;
		connection = DatabaseManager.getConnectionOnStartup(queryIODBConfig.getConnectionName());
		if ((connection != null) && !connection.getAutoCommit()) {
			setAutoCommitOnConnection(connection, true);
		}
		return connection;
	}

	public static Connection getCustomTagDBConnectionOnStartup(String poolName) throws Exception {
		LOGGER.info("public static Connection getCustomTagDBConnection() throws Exception: " + poolName);

		Connection connection = null;
		connection = DatabaseManager.getConnectionOnStartup(poolName);
		if ((connection != null) && !connection.getAutoCommit()) {
			setAutoCommitOnConnection(connection, true);
		}
		return connection;
	}

	public static Connection getCustomTagDBConnection(String poolName) throws Exception {
		LOGGER.info("public static Connection getCustomTagDBConnection() throws Exception: " + poolName);

		Connection connection = null;
		connection = DatabaseManager.getConnection(poolName);
		if ((connection != null) && !connection.getAutoCommit()) {
			setAutoCommitOnConnection(connection, true);
		}
		return connection;
	}

	/**
	 * Closes the connection. Basically the close connection does not actually
	 * close the connection but instead returns the connection back to the pool.
	 * 
	 * @param connection
	 */
	public static void closeConnection(final Connection connection) throws Exception {
		try {
			if (connection != null) {
				// if(EnvironmentalConstants.getQueryIODatabaseDriverClass().contains("hsql")){
				// rollbackOnConnection(connection);
				// }
				setAutoCommitOnConnection(connection, true);
			}
		} finally {
			DatabaseManager.closeDbConnection(connection);
		}
	}

	/**
	 * Closes the connection. Basically the close connection does not actually
	 * close the connection but instead returns the connection back to the pool.
	 * 
	 * @param connection
	 */
	public static void closeConnectionSilently(final Connection connection) {
		try {
			if (connection != null) {
				setAutoCommitOnConnection(connection, true);
			}
			DatabaseManager.closeDbConnection(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error closing database connection.", e);
		}
	}

	/**
	 * This method returns the database representation for the given data type.
	 * 
	 * @param dataTypeRequired
	 * @return
	 */
	public static String getDataTypeRepresentation(final String dataTypeRequired, final int maxLength,
			final String databaseName) {
		return DatabaseDataTypes.getDataTypeRepresentation(dataTypeRequired, maxLength, databaseName);
	}

	/**
	 * This method sets the bQueryIODatabaseMigrating flag Called when migrating
	 * QueryIO database
	 * 
	 * @param isMigrating
	 */
	public static void setQueryIODatabaseMigrating(final boolean isMigrating) {
		queryIODBConfig.setBDatabaseMigrating(isMigrating);
	}

	/**
	 * This method is a wrapper for
	 * DatabaseFunctions.getQueryResultsForStatement.
	 * 
	 * @param statement
	 * @param query
	 * @throws Exception
	 */
	public static ResultSet getQueryResultsForStatement(final Statement statement, final String query)
			throws Exception {
		return DatabaseFunctions.getQueryResultsForStatement(statement, query);
	}

	/**
	 * This method is a wrapper for
	 * DatabaseFunctions.getQueryResultsForStatement.
	 * 
	 * @param ps
	 * @throws Exception
	 */
	public static ResultSet getQueryResultsForPreparedStatement(final PreparedStatement ps) throws Exception {
		return DatabaseFunctions.getQueryResultsForPreparedStatement(ps);
	}

	/**
	 * This method is a wrapper for DatabaseFunctions.executeUpdateStatement. If
	 * the database is migrating then all the update calls should be prevented.
	 * The method checks whether the database updated is in migrating condition
	 * or not. If it is migrating then it throws an SQL exception.
	 * 
	 * @param connection
	 * @param statement
	 * @param insertQuery
	 * @param isQueryIOConnection
	 * @throws Exception
	 */
	public static int executeUpdateStatement(final Connection connection, final Statement statement,
			final String insertQuery) throws Exception {
		return DatabaseFunctions.executeUpdateStatement(statement, insertQuery);
	}

	/**
	 * This method is a wrapper for DatabaseFunctions.executeUpdateStatement. If
	 * the database is migrating then all the update calls should be prevented.
	 * The method checks whether the database updated is in migrating condition
	 * or not. If it is migrating then it throws an SQL exception. *
	 * 
	 * @param ps
	 */
	public static int executeUpdateStatement(final Connection connection, final PreparedStatement ps) throws Exception {
		return DatabaseFunctions.executeUpdateStatement(ps);
	}

	/**
	 * @param connection
	 * @param autoCommit
	 * @throws Exception
	 */
	public static void setAutoCommitOnConnection(final Connection connection, final boolean autoCommit)
			throws Exception {
		if ((connection != null) && !connection.isClosed()) {
			connection.setAutoCommit(autoCommit);
		}
	}

	/**
	 * @param connection
	 * @throws Exception
	 */
	public static void commitOnConnection(final Connection connection) throws Exception {
		if (connection != null && !connection.isClosed()) {
			connection.commit();
		}
	}

	/**
	 * @param connection
	 * @throws Exception
	 */
	public static void rollbackOnConnection(final Connection connection) throws Exception {
		if (connection != null && !connection.isClosed()) {
			connection.rollback();
		}
	}

	public static void validateDatabase(Map<String, Object> valuesContainer) throws Exception {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rsForMetaData = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			// closeConnection(getQueryIODBConnection());
			connection = getQueryIODBConnectionOnStartup();

			String tableName = (String) valuesContainer.get("tableName");
			stmt = connection.createStatement();
			rsForMetaData = stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 1");
			ResultSetMetaData rsmd = rsForMetaData.getMetaData();

			String[] valuesArray = (String[]) valuesContainer.get("valuesArray");
			StringBuilder query = new StringBuilder("SELECT * FROM " + tableName);
			query.append(" WHERE ");
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				if (i == rsmd.getColumnCount() - 1)
					query.append(rsmd.getColumnName(i + 1) + "=" + valuesArray[i]);
				else
					query.append(rsmd.getColumnName(i + 1) + "=" + valuesArray[i] + " AND ");
			}
			AppLogger.getLogger().debug("Query generated : " + query.toString());
			pst = connection.prepareStatement(query.toString());
			rs = null;
			rs = pst.executeQuery();

			if (rs.next()) {
				isPerfect = true;
			}
		} catch (Exception e) {
			isPerfect = false;
			throw e;
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rsForMetaData);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing resultset.", e);
			}
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing statement.", e);
			}
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing resultset.", e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(pst);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing prepared statement.", e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection.", e);
			}
		}
	}

	public static Map<String, Object> getLastFiredQueryDetails() throws Exception {
		BufferedReader br = null;
		String[] valuesArray = null;
		String tableName = null;
		try {
			StringBuilder queryioScriptFilePath = new StringBuilder(EnvironmentalConstants.getAppHome() + "../../../");

			queryioScriptFilePath.append(File.separatorChar);
			queryioScriptFilePath.append("services");
			queryioScriptFilePath.append(File.separatorChar);
			queryioScriptFilePath.append("queryio.sql");
			File file = new File(queryioScriptFilePath.toString());
			br = new BufferedReader(new FileReader(file));
			String line = null;
			String nonEmptyLine = null;
			while ((line = br.readLine()) != null) {
				if (!line.trim().isEmpty())
					nonEmptyLine = line.trim();
			}
			line = nonEmptyLine;
			if (line.toUpperCase().startsWith("INSERT INTO")) {
				// Not considering case of selective columns insert. Eg: INSERT
				// INTO TABLENAME(COL1, COL2, ... CLONN) values (VAL1, VAL2, ...
				// VALN)
				tableName = line.substring(11, line.indexOf("(") - 7).trim(); // INSERT
																				// INTO
																				// TABLENAME
																				// VALUES(VAL1,
																				// VAL2..VALN);

				String[] values = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).split(",");
				valuesArray = new String[values.length];
				for (int i = 0; i < values.length; i++) {
					valuesArray[i] = values[i].trim();
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error parsing queryio.script to get last fired query.", e);
			throw e;
		} finally {
			if (br != null)
				br.close();
		}
		Map<String, Object> valuesContainer = new HashMap<String, Object>();

		valuesContainer.put("tableName", tableName);
		valuesContainer.put("valuesArray", valuesArray);
		return valuesContainer;
	}

	public static boolean isPerfect() {
		return isPerfect;
	}

	public static Connection getConnection(String url, String username, String password) throws Exception {
		return DriverManager.getConnection(url, username, password);
	}

}
