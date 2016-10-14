package com.queryio.database.migrate;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.AppLogger;

public class DBAcivator {

	public static ArrayList<String> getAllTableNames(String driverClass, String connectionURL, String username, String password, String dbDriverJar) {
		ArrayList<String> tables = new ArrayList<String>();
		Connection connection = null;
		DatabaseMetaData md = null;
		Statement statement = null;
		ResultSet rs = null;
		try{
			Properties connectionProps = new Properties();
			connectionProps.put("user", username);
			connectionProps.put("password", password);
			AppLogger.getLogger().debug(EnvironmentalConstants.getAppHome() + File.separator + QueryIOConstants.DATABASE_DRIVER_JAR_FILE_DIR
					+ File.separator + dbDriverJar);
			AppLogger.getLogger().debug("Modifying = "+EnvironmentalConstants.getAppHome() + QueryIOConstants.DATABASE_DRIVER_JAR_FILE_DIR
			+ File.separator + dbDriverJar);
			File file = new File(EnvironmentalConstants.getAppHome() + QueryIOConstants.DATABASE_DRIVER_JAR_FILE_DIR
					+ File.separator + dbDriverJar);

			URL u = file.toURI().toURL();
			final URLClassLoader ucl = new URLClassLoader(new URL[] { u },
					Thread.currentThread().getContextClassLoader());
			Driver driver = (Driver)Class.forName(driverClass, true, ucl).newInstance();
			connection = driver.connect(connectionURL, connectionProps);
			statement = connection.createStatement();
			md = connection.getMetaData();
			rs = md.getTables(null, null, "%", new String[] {"TABLE"});
			while(rs.next()){
				String tableName;
				tableName = rs.getString(3);
				AppLogger.getLogger().debug(tableName);
				if(tableName!=null && !tableName.trim().equalsIgnoreCase(""));
					tables.add(tableName);
			}
		}catch(Exception e){
			AppLogger.getLogger().fatal("Error while fetching tables from database.", e);
		}finally{
			try{
				if(connection!=null)
					connection.close();
			}catch(Exception e){
				AppLogger.getLogger().debug(e);
			}try{
				if(statement != null)
					statement.close();
			}catch(Exception e){
				AppLogger.getLogger().debug(e);
			}try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				AppLogger.getLogger().debug(e);
			}
			
		}
		return tables;
	}
	
}
