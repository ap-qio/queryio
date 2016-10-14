package com.queryio.common.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.filefilter.HiddenFileFilter;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.MetadataConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CustomTagDBConfig;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.DriverShim;
import com.queryio.common.util.AppLogger;

public class GenericDBConfigDAO 
{
	public static String[] getAllDatabaseNames() throws FileNotFoundException
	{
		String filePath = "";
		File file = null;
		
		filePath = EnvironmentalConstants.getAppHome() + File.separator + QueryIOConstants.DB_CONFIG_FOLDER;
//		filePath = "/QueryIO/tomcat/webapps/queryio/DBTypeConfig";
		file = new File(filePath);
		
		return file.list((FilenameFilter) HiddenFileFilter.VISIBLE);
	}
	
	public static ArrayList<String> getAllGenericDataTypes()
	{
		ArrayList<String> arr = new ArrayList<String>();
		Iterator it = MetadataConstants.STATIC_DATATYPES_TO_WRAPPER_MAP.keySet().iterator();
		
		while(it.hasNext())
		{
			try
			{
				String key = it.next().toString();
				arr.add(key);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Cannot get generic data types : " , e);
			}
		}
		
		return arr;
	}
	
	public static String getGenericDataType(String type, DBTypeProperties props)
	{
		Class wrapperClass;
		
		if(props.getKeyFromValue(type) != null)
		{
			wrapperClass = props.getKeyFromValue(type);
			return MetadataConstants.STATIC_WRAPPER_MAP_TO_DATATYPES.get(wrapperClass);
		}
		else
			return MetadataConstants.GENERIC_DATA_TYPE_STRING;

	}
	
	private static Connection verifyDatabaseSettings(String driverName, String url, String username, String password, String jarFile) throws Exception 
	{
		System.out.println("driverName: " + driverName);
		System.out.println("url: " + url);
		System.out.println("username: " + username);
		System.out.println("password: " + password);
		System.out.println("jarFile: " + jarFile);
		File file = new File(EnvironmentalConstants.getAppHome()+QueryIOConstants.JDBC_JAR_DIR+File.separator+jarFile);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("URL: "+file.toURI().toURL());
		URL u = file.toURI().toURL();
		String classname = driverName;
		URLClassLoader ucl = new URLClassLoader(new URL[] { u });
		Driver driver = (Driver)Class.forName(classname, true, ucl).newInstance();
		DriverManager.registerDriver(new DriverShim(driver));
		return DriverManager.getConnection(url, username, password);
	}
	
	public static String getDefaultSchemaFromDBName(String dbName)
	{
		Connection conn = null;
		String defaultSchemaQuery = null;
		String schemaName = null;
		DBTypeProperties props = null;
		CustomTagDBConfig dbConfig = null;
		
		Statement stmt = null;
		ResultSet rs = null;
		
		try
		{
			dbConfig = CustomTagDBConfigManager.getConfig(dbName);
			conn = verifyDatabaseSettings(dbConfig.getCustomTagDriverClass(), dbConfig.getCustomTagUrl(), dbConfig.getCustomTagUserName(), dbConfig.getCustomTagPassword(), dbConfig.getCustomTagDriverJarPath());
			props = CustomTagDBConfigManager.getDatabaseDataTypeMap(dbName, null);
			
			defaultSchemaQuery = props.getDefaultSchema();
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(defaultSchemaQuery);
			if (rs.next())
			{
				schemaName = rs.getString(1);
			}
			
			System.out.println("dbName: " + dbName);
			System.out.println("defaultSchemaQuery: " + defaultSchemaQuery);
			System.out.println("schemaName: " + schemaName);
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
		}
		
		return schemaName;
	}
	
	public static void main(String args[])
	{
		try 
		{
//			String arr[] = getAllDatabaseNames();
//			for(int i=0; i<arr.length; i++)
//				System.out.println(arr[i]);
//			System.out.println(getAllGenericDataTypes().toString());
			getDefaultSchemaFromDBName("newDB");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
