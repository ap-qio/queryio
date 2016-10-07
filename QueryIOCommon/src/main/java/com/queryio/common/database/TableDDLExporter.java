package com.queryio.common.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.DriverShim;
import com.queryio.common.util.AppLogger;

public class TableDDLExporter {

	private String sourceDBName;
	private String sourceURL;
	private String sourceUserName;
	private String sourcePassword;
	private String sourceDriver;
	private Connection sourceConnection;
	private String schemaName = "";
	private String sourceJarFile = null;
	private ArrayList<String> sourceTables = new ArrayList<String>();
	
	private BufferedWriter writer;
	private String filePath;
	
//	private long rowsToMigrate = 0;
//	private long rowsMigrated = 0;
	
	public String getSourceDBName() {
		return sourceDBName;
	}

	public void setSourceDBName(String sourceDBName) {
		this.sourceDBName = sourceDBName;
	}

	public String getSourceJarFile() {
		return sourceJarFile;
	}

	public void setSourceJarFile(String sourceJarFile) {
		this.sourceJarFile = sourceJarFile;
	}

//	public double getProgress() {
//		return (rowsToMigrate == 0) ? 100.0 : (rowsMigrated * 100 / rowsToMigrate);
//	}
	
	public String startDDLExport() throws Exception {
		verifyDatabaseSettings();
		getDDLStatementsForDatabase();
	
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}
	
	private void initWriter() throws Exception
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("EnvironmentalConstants.getAppHome()" + EnvironmentalConstants.getAppHome());
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("sourceDBName" + sourceDBName);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("schemaName" + schemaName);
		EnvironmentalConstants.getDDLFileContainer();
		filePath = EnvironmentalConstants.getDDLFileContainer() + "DDLStatementsForDatabase_" + sourceDBName + ".sql";
		FileWriter fw;
		BufferedWriter writer = null; 
		try {
			fw = new FileWriter(filePath);
			writer = new BufferedWriter(fw);
		} catch (IOException e) {
			AppLogger.getLogger().fatal("Error getting file writer", e);
			throw e;
		}
		this.writer = writer;
	}
	
	private void getDDLStatementsForDatabase() throws Exception 
	{
		initWriter();
		if (sourceTables.size() > 0) {
			for (String tableName : sourceTables) {
					getDDLForTable(tableName);
			}
		}
		closeWriter();
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("File path of the generated DDL statements" + filePath);
	}

	private void closeWriter() throws Exception
	{
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) 
		{
			AppLogger.getLogger().fatal("Error closing DDL export file", e);
			throw e;
		}
	}
	
	private void writeToFile(String line) throws Exception
	{
		try {
			writer.write(line);
			writer.write(";");
			writer.newLine();
		} catch (IOException e) {
			AppLogger.getLogger().fatal("Error writing DDL statements in File", e);
			throw e;
		}
	}
	
	private void getDDLForTable(String tableName) throws Exception 
	{	
		// create a table
		DatabaseMetaData dmd = sourceConnection.getMetaData();
		ResultSet rs = dmd.getColumns(null, schemaName, tableName, null);
		final StringBuffer query = new StringBuffer("CREATE TABLE ");
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
				(dataType== Types.DECIMAL)) && (rs.getInt("COLUMN_SIZE") != Integer.MAX_VALUE)) {
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
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("DDL statements Tables: "+query.toString());
		
		writeToFile(query.toString());
		
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
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("DDL statements Indexes: "+query.toString());
			writeToFile(query.toString());
		}
		
		}
	}

	private void verifyDatabaseSettings() throws Exception {

		sourceConnection = verifyDatabaseSettings(sourceDriver, sourceURL, sourceUserName, sourcePassword, sourceJarFile);
		DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(sourceDBName, null);
		findRowsForDDL(sourceConnection, props);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Total tables: " + this.sourceTables.size());
	}
	
	private void findRowsForDDL(Connection connection, DBTypeProperties props) throws Exception {
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

}
