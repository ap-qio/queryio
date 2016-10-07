package com.queryio.database.migration;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.dao.NNMIgrationInfoDAO;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.util.AppLogger;
import com.queryio.config.db.DBConfigDAO;
import com.queryio.config.db.DriverShim;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;

public class DBMigrator implements Runnable {

	private String sourceDBName;
	private String sourceURL;
	private String sourceUserName;
	private String sourcePassword;
	private String sourceDriver;
	private Connection sourceConnection;
	private String schemaName = "";
	private String sourceJarFile = null;
	private ArrayList<String> sourceTables = new ArrayList<String>();
	private boolean isCustomTagDB = false;
	
	private String destinationDBName;
	private String destinationURL;
	private String destinationUserName;
	private String destinationPassword;
	private Connection destinationConnection;

	private String destinationDriver;
	private String destinationJarFile = null;
	private ArrayList<String> destinationTables = new ArrayList<String>();

	private String tablePrefix="";

	private Node namenode;
	private Host namenodeHost;
	
	private boolean createSchemaFlag;
	
	public String getTablePrefix() {
		return tablePrefix;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}
	
	public boolean isCreateSchemaFlag() {
		return createSchemaFlag;
	}

	public void setCreateSchemaFlag(boolean createSchemaFlag) {
		this.createSchemaFlag = createSchemaFlag;
	}

	public void setNamenode(Node namenode) {
		this.namenode = namenode;
	}

	public void setNamenodeHost(Host namenodeHost) {
		this.namenodeHost = namenodeHost;
	}
	
	private long tablesMigrated = 0;

	private String lastError;

	public String getSourceDBName() {
		return sourceDBName;
	}

	public void setSourceDBName(String sourceDBName) {
		this.sourceDBName = sourceDBName;
	}

	public String getDestinationDBName() {
		return destinationDBName;
	}

	public void setDestinationDBName(String destinationDBName) {
		this.destinationDBName = destinationDBName;
	}

	public boolean isCustomTagDB() {
		return isCustomTagDB;
	}

	public void setCustomTagDB(boolean isCustomTagDB) {
		this.isCustomTagDB = isCustomTagDB;
	}
	
	public String getSourceJarFile() {
		return sourceJarFile;
	}

	public void setSourceJarFile(String sourceJarFile) {
		this.sourceJarFile = sourceJarFile;
	}

	public String getDestinationJarFile() {
		return destinationJarFile;
	}

	public void setDestinationJarFile(String destinationJarFile) {
		this.destinationJarFile = destinationJarFile;
	}

	public void startMigration() {
		Thread migrationThread = new Thread(this, "DatabaseMigrator");
		migrationThread.setDaemon(true);
		migrationThread.start();
	}

	public void stopMigration() {
		try {
			if (sourceConnection != null)
				sourceConnection.close();

			if (destinationConnection != null)
				destinationConnection.close();

		} catch (SQLException ex) {
			AppLogger.getLogger().fatal("Exception while stopping migration",ex);
		}
	}

	public void run() {
		this.lastError = null;
		try {
			if(namenodeHost != null && namenode != null){
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("1. setting safemode as ON on " + namenode.getId());
				QueryIOAgentManager.setSafemode(namenodeHost, namenode, true);
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("2. Starting migration.");
			}			
			verifyDatabaseSettings();
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Migrating database");
			migrateDatabase();
		} catch (Exception ex) {
			this.lastError = ex.getLocalizedMessage();
			AppLogger.getLogger().fatal("Error: ",ex);
		}		
		finally
		{			
			try {
				if (lastError != null)
					updateMigrationProgress(-1);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error: ",e);
			}
			if(namenodeHost != null && namenode != null){
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("3. migration complete");		
				try {
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("4. setting safemode as OFF on " + namenode.getId());
					QueryIOAgentManager.setSafemode(namenodeHost, namenode, false);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Error: ",e);
				}
			}
			this.stopMigration();
		}
	}
	
	public String getErrorMessage(){
		return this.lastError;
	}
	

	private void migrateDatabase() throws Exception {
		this.tablesMigrated = 0;
		if(destinationDriver.equals(sourceDriver)||isCustomTagDB()){
			updateMigrationProgress(sourceTables.size());
			if (sourceTables.size() > 0) {
				for (String tableName : sourceTables) {
						migrateTable(tableName);
						this.tablesMigrated++;
						updateMigrationProgress(sourceTables.size());
				}
			}	
		}
		else{
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Different Db found.");
			this.tablesMigrated = 0;
			updateMigrationProgress(destinationTables.size());
			if (sourceTables.size() > 0) {
				for (String tableName : sourceTables) {
					if(!destinationTables.contains(tableName)){
						migrateTable(tableName);
						this.tablesMigrated++;
						updateMigrationProgress(destinationTables.size());
					}
				}
			}
		}
	}

	private void migrateTable(String tableName) throws SQLException {
		if(tableName.toLowerCase().startsWith("backup_")) {
			return;
		}
		// create a table
		DatabaseMetaData dmd = sourceConnection.getMetaData();
		ResultSet rs = dmd.getColumns(null, schemaName, tableName, null);
		final StringBuffer query = new StringBuffer("CREATE TABLE ");
		
		if (tablePrefix != null && (!tablePrefix.isEmpty()))
			tableName = tablePrefix + tableName;
		
		query.append(tableName);
		
		query.append('(');
		
		final StringBuffer psQuery = new StringBuffer("INSERT INTO ");
		psQuery.append(tableName);
		psQuery.append(" VALUES (");
		
		ArrayList<Integer> datatypes = new ArrayList<Integer>();

		boolean first = true;
		while (rs.next()) {
			if (!first) {
				query.append(',');
				psQuery.append(',');
			}
			first = false;
			
			psQuery.append('?');
			
			query.append(rs.getString("COLUMN_NAME"));
			query.append(' ');
			query.append(rs.getString("TYPE_NAME"));
			
			final int dataType = rs.getInt("DATA_TYPE");
			datatypes.add(dataType);
			
			if (((dataType == Types.VARCHAR) || (dataType == Types.NUMERIC) || (dataType == Types.BLOB) ||
				(dataType== Types.DECIMAL) || (dataType == Types.VARBINARY)) && (rs.getInt("COLUMN_SIZE") != Integer.MAX_VALUE)) {
				query.append('(');
				query.append(rs.getInt("COLUMN_SIZE"));
				query.append(')');
			}
			
			if (rs.getInt("NULLABLE") == DatabaseMetaData.columnNoNulls) {
				query.append(" NOT NULL");
	        }
		}
		query.append(')');
		psQuery.append(')');
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Query: "+query.toString());

		Statement stmt = destinationConnection.createStatement();
		
		if(createSchemaFlag)
		{
			stmt.executeUpdate(query.toString());
		}
		
		if(createSchemaFlag)
		{
			// Create indexes
			rs = dmd.getIndexInfo(null, schemaName, tableName, false, true);
			HashMap temp = null;
			ArrayList indexName = null;
			while (rs.next()) {
				if(temp==null){
					temp = new HashMap();
					indexName = new ArrayList();
					ArrayList arr =  new ArrayList();
					arr.add(rs.getString("COLUMN_NAME"));
					temp.put(rs.getString("INDEX_NAME"),arr);
					indexName.add(rs.getString("INDEX_NAME"));
				}
				else{
					ArrayList arr;
					if(temp.containsKey(rs.getString("INDEX_NAME"))){
						arr = (ArrayList)temp.get(rs.getString("INDEX_NAME"));
						arr.add(rs.getString("COLUMN_NAME"));
					}
					else{
						indexName.add(rs.getString("INDEX_NAME"));
						arr = new ArrayList();
						arr.add(rs.getString("COLUMN_NAME"));
					}
					temp.put(rs.getString("INDEX_NAME"),arr);
				}
			}
			if(temp!=null){
				
				for(int k=0;k<temp.size();k++){
					String currentIndex = (String)indexName.get(k);
					ArrayList arr = (ArrayList)temp.get(currentIndex);
					query.setLength(0);
					query.append("CREATE INDEX ");
					query.append(currentIndex);
					query.append(" ON ");
					query.append(tableName);
					query.append(" (");
					for(int l=0;l<arr.size();l++){
						query.append(arr.get(l));
						if(l<(arr.size()-1))
						{
							query.append(", ");
						}
					}
					query.append(")");
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Query: "+query.toString());
					stmt.executeUpdate(query.toString());
				}
		}
		
		// copy the data from source to destination
		stmt.close();
		}
		query.setLength(0);
		query.append("SELECT * FROM ");
		query.append(tableName.substring(tablePrefix.length()));
		stmt = sourceConnection.createStatement();
		stmt.setFetchSize(1000);
		rs = stmt.executeQuery(query.toString());
		PreparedStatement ps = destinationConnection.prepareStatement(psQuery.toString());
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(psQuery.toString());
		int count = 0;
		while (rs.next()) {
			ps.clearParameters();
			for (int j = 1; j <= datatypes.size(); j ++) {
				
				int dataType = datatypes.get(j - 1);
				switch (dataType)
				{
					case Types.INTEGER:
					case Types.FLOAT:
					case Types.DECIMAL:
					case Types.NUMERIC:
					case Types.DOUBLE:
					{
						ps.setDouble(j, rs.getDouble(j));
						break;
					}
					case Types.TIME:
					case Types.DATE:
					case Types.TIMESTAMP:
					{
						ps.setTimestamp(j, rs.getTimestamp(j));
						break;
					}
					case Types.VARCHAR:
					{
						ps.setString(j, rs.getString(j));
						break;
					}
					case Types.BOOLEAN:
					{
						ps.setBoolean(j, rs.getBoolean(j));
						break;
					}
					default:
					{
						ps.setObject(j, rs.getObject(j));
						break;
					}
				}
			}
			ps.addBatch();
			
			count++;
			
			if(count%1000==0) {
				ps.executeBatch();
				ps.clearBatch();
			}
		}
		
		if(count!=0 && count%1000!=0) {
			ps.executeBatch();
		}
		
		stmt.close();
		ps.close();
		destinationConnection.commit();
	}

	private void verifyDatabaseSettings() throws Exception {

		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Source settings being verified");
		sourceConnection = verifyDatabaseSettings(sourceDriver, sourceURL, sourceUserName, sourcePassword, sourceJarFile);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Source settings verified");
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Destination settings being verified");
		destinationConnection = verifyDatabaseSettings(destinationDriver, destinationURL, destinationUserName, destinationPassword, destinationJarFile);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Destination settings verified");
		destinationConnection.setAutoCommit(false);
		DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(sourceDBName, null);
		findRowsToMigrate(sourceConnection, props);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Total tables: " + this.sourceTables.size());
		
	}

	private void findRowsToMigrate(Connection connection, DBTypeProperties props) throws SQLException {
		readTables(connection, props);
	}

	private void readTables(Connection connection, DBTypeProperties props) throws SQLException {
		
		sourceTables.clear();
		DatabaseMetaData dmd = null;
		Statement stmt = null;
		ResultSet rs1 = null;
		ResultSet rs = null;
		
		try
		{
			// Get Current Schema
			
//			ResultSet rs1 = dmd.getSchemas();		// For HSQL
//			while (rs1.next()) {
	//
//				schemaName = rs1.getString("TABLE_SCHEM");
//				if (rs1.getBoolean("IS_DEFAULT")) {
//					break;
//				}
//			}
			String defaultSchemaQuery = props.getDefaultSchema();
			if (AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("defaultSchemaQuery:  " + defaultSchemaQuery);
			if (AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("props:  " + props);
			stmt = connection.createStatement();
			rs1 = stmt.executeQuery(defaultSchemaQuery);
			if (rs1.next())
			{
				schemaName = rs1.getString(1);
			}
			
			String[] tableTypes = { "TABLE" };
			dmd = connection.getMetaData();
			rs = dmd.getTables(null, schemaName, null, tableTypes);
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				if (tableName.startsWith("BACKUP_"))
					continue;
				sourceTables.add(tableName);
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("found " + rs.getString("TABLE_NAME"));
			}
		}
		finally
		{
			try {
				DatabaseFunctions.closeStatement(stmt);
			} catch(Exception e) {
				AppLogger.getLogger().fatal("Error: " + e.getLocalizedMessage(), e);
			}
			try {
				DatabaseFunctions.closeResultSet(rs1);
			} catch(Exception e) {
				AppLogger.getLogger().fatal("Error: " + e.getLocalizedMessage(), e);
			}
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch(Exception e) {
				AppLogger.getLogger().fatal("Error: " + e.getLocalizedMessage(), e);
			}
		}
	}

	private Connection verifyDatabaseSettings(String driverName, String url, String username, String password, String jarFile) throws Exception 
	{
		File file = new File(EnvironmentalConstants.getAppHome()+QueryIOConstants.JDBC_JAR_DIR+File.separator+jarFile);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("URL: "+file.toURI().toURL());
		URL u = file.toURI().toURL();
		String classname = driverName;
		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		Driver driver = (Driver)Class.forName(classname, true, ucl).newInstance();
		DriverManager.registerDriver(new DriverShim(driver));
		return DriverManager.getConnection(url, username, password);
	}

	private void updateMigrationProgress(long totalCount) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			String progress = tablesMigrated + " of " + totalCount + " tables migrated.";
			if ((totalCount == -1) || (lastError != null))
			{
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("updateDBMigrationStatusErrorFinal: sourceDBName: " + sourceDBName + " destinationDBName: " + destinationDBName + " oldStatus: " + QueryIOConstants.DBMIGRATION_STATUS_RUNNING + " newStatus: " + QueryIOConstants.DBMIGRATION_STATUS_FAILED + " error(lastError): " + lastError);
				if (tablePrefix != null && (!tablePrefix.isEmpty()))
				{
					NNMIgrationInfoDAO.updateMigrationInfo(connection, tablePrefix, new Timestamp(System.currentTimeMillis()), QueryIOConstants.PROCESS_STATUS_FAILED, lastError.toString());
				}
				else
				{
					DBConfigDAO.updateDBMigrationStatusErrorFinal(connection, sourceDBName, destinationDBName, QueryIOConstants.DBMIGRATION_STATUS_RUNNING, QueryIOConstants.DBMIGRATION_STATUS_FAILED, lastError, System.currentTimeMillis());
				}
			}
			else if (tablesMigrated >= totalCount)
			{
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("updateDBMigrationStatusFinal: sourceDBName: " + sourceDBName + " destinationDBName: " + destinationDBName + " oldStatus: " + QueryIOConstants.DBMIGRATION_STATUS_RUNNING + " newStatus: " + QueryIOConstants.DBMIGRATION_STATUS_SUCCESS + " progress: " + progress);
				if (tablePrefix != null && (!tablePrefix.isEmpty()))
				{
					NNMIgrationInfoDAO.updateMigrationInfo(connection, tablePrefix, new Timestamp(System.currentTimeMillis()), QueryIOConstants.PROCESS_STATUS_COMPLETED, null);
				}
				else
				{
					DBConfigDAO.updateDBMigrationStatusFinal(connection, sourceDBName, destinationDBName, QueryIOConstants.DBMIGRATION_STATUS_RUNNING, QueryIOConstants.DBMIGRATION_STATUS_SUCCESS, progress, System.currentTimeMillis());				
				}
			}			
			else
			{
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("updateDBMigrationProgress: sourceDBName: " + sourceDBName + " destinationDBName: " + destinationDBName + " oldStatus: " + QueryIOConstants.DBMIGRATION_STATUS_RUNNING + " progress: " + progress);
				if (!(tablePrefix != null && (!tablePrefix.isEmpty())))
				{
					DBConfigDAO.updateDBMigrationProgress(connection, sourceDBName, destinationDBName, QueryIOConstants.DBMIGRATION_STATUS_RUNNING, progress);				
				}
			}
		}
		finally
		{
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(
						"Error closing " + QueryIOConstants.DEFAULT_MONITOR_DB + " database connection.", e);
			}
		}
	}

	public String getSourceURL() {
		return sourceURL;
	}

	public void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}

	public String getSourceUserName() {
		return sourceUserName;
	}

	public void setSourceUserName(String sourceUserName) {
		this.sourceUserName = sourceUserName;
	}

	public String getSourcePassword() {
		return sourcePassword;
	}

	public void setSourcePassword(String sourcePassword) {
		this.sourcePassword = sourcePassword;
	}

	public String getSourceDriver() {
		return sourceDriver;
	}

	public void setSourceDriver(String sourceDriver) {
		this.sourceDriver = sourceDriver;
	}

	public String getDestinationURL() {
		return destinationURL;
	}

	public void setDestinationURL(String destinationURL) {
		this.destinationURL = destinationURL;
	}

	public String getDestinationUserName() {
		return destinationUserName;
	}

	public void setDestinationUserName(String destinationUserName) {
		this.destinationUserName = destinationUserName;
	}

	public String getDestinationPassword() {
		return destinationPassword;
	}

	public void setDestinationPassword(String destinationPassword) {
		this.destinationPassword = destinationPassword;
	}

	public String getDestinationDriver() {
		return destinationDriver;
	}

	public void setDestinationDriver(String destinationDriver) {
		this.destinationDriver = destinationDriver;
	}
	
//	public static void main(String[] args) throws InterruptedException {
//		DBMigrator migrator = new DBMigrator();
//		
//		migrator.setSourceURL("jdbc:hsqldb:hsql://192.168.0.12:5681/MetaStore");
//		migrator.setSourceDriver("org.hsqldb.jdbcDriver");
//		migrator.setSourceUserName("ADMIN");
//		migrator.setSourcePassword("ADMIN");
//		migrator.setSourceJarFile("hsqldb-2_2_8.jar");
//		
//		migrator.setDestinationURL("jdbc:hsqldb:hsql://192.168.0.16:5681/MetaStore");
//		migrator.setDestinationDriver("org.hsqldb.jdbcDriver");
//		migrator.setDestinationUserName("ADMIN");
//		migrator.setDestinationPassword("ADMIN");
//		migrator.setDestinationJarFile("hsqldb-2_2_8.jar");
//		
//		BasicConfigurator.configure();
//		AppLogger.setLogger(Logger.getRootLogger());
//		AppLogger.getLogger().setLevel(Level.DEBUG);
//
//		EnvironmentalConstants.setAppHome("/AppPerfect/QueryIO/tomcat/webapps/queryio/");
//		
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Starting DB to DB migration at: " + new Timestamp(System.currentTimeMillis()));
//		migrator.startMigration();
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Completed DB to DB migration at: " + new Timestamp(System.currentTimeMillis()));
//	}
}
