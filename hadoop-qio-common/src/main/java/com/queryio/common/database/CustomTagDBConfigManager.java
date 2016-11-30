package com.queryio.common.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.filefilter.HiddenFileFilter;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.MetadataConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.AppLogger;

public class CustomTagDBConfigManager {
	
private static Map<String, CustomTagDBConfig> customTagDBConfigMap = new HashMap<String, CustomTagDBConfig>();
private static Map<String, DBTypeProperties> customTagDBTypeConfigMap = new HashMap<String, DBTypeProperties>();
	
	public static void addCustomTagDBConfig(CustomTagDBConfig dbConfig){
		if(customTagDBConfigMap.containsKey(dbConfig.getCustomTagPoolName())){
			
			CustomTagDBConfig config = (CustomTagDBConfig) customTagDBConfigMap.get(dbConfig.getCustomTagPoolName());
			if(dbConfig.getCustomTagDriverClass() != null && !dbConfig.getCustomTagDriverClass().isEmpty())				
				config.setCustomTagDriverClass(dbConfig.getCustomTagDriverClass());
			if(dbConfig.getCustomTagDriverJarPath() != null && !dbConfig.getCustomTagDriverJarPath().isEmpty())				
				config.setCustomTagDriverJarPath(dbConfig.getCustomTagDriverJarPath());
			if(dbConfig.getCustomTagMaxIdleConn() != -1)				
				config.setCustomTagMaxIdleConn(dbConfig.getCustomTagMaxIdleConn());
			if(dbConfig.getCustomTagMaxWaitTime() != -1)				
				config.setCustomTagMaxWaitTime(dbConfig.getCustomTagMaxWaitTime());
			if(dbConfig.getCustomTagMaxConn() != -1)				
				config.setCustomTagMaxConn(dbConfig.getCustomTagMaxConn());
			if(dbConfig.getCustomTagUserName() != null && !dbConfig.getCustomTagUserName().isEmpty())				
				config.setCustomTagUserName(dbConfig.getCustomTagUserName());
			if(dbConfig.getCustomTagDBType() != null && !dbConfig.getCustomTagDBType().isEmpty())				
				config.setCustomTagDBType(dbConfig.getCustomTagDBType());
							
			config.setCustomTagPassword(dbConfig.getCustomTagPassword());
			
			if(dbConfig.getCustomTagUrl() != null && !dbConfig.getCustomTagUrl().isEmpty())				
				config.setCustomTagUrl(dbConfig.getCustomTagUrl());
			if(dbConfig.getCustomTagPoolName() != null && !dbConfig.getCustomTagPoolName().isEmpty())				
				config.setCustomTagPoolName(dbConfig.getCustomTagPoolName());
		}else{
			customTagDBConfigMap.put(dbConfig.getCustomTagPoolName(), dbConfig);	
		}
	}
	
	public static void addDBTypeProperties(String databaseType, DBTypeProperties props)
	{
		customTagDBTypeConfigMap.put(databaseType, props);
	}
	
	public static void addCustomTagDBTypeConfig(String databaseName, String databaseType) throws FileNotFoundException
	{
		// Either databaseName or databaseType could be kept null. Providing both of them is not necessary.
		if(databaseType == null)
			databaseType = CustomTagDBConfigManager.getConfig(databaseName).getCustomTagDBType();
		
		DBTypeProperties prop = null;
		
		if(!customTagDBTypeConfigMap.containsKey(databaseType))
		{
			prop = add(databaseType);
			if(prop == null)
				throw new FileNotFoundException();
			customTagDBTypeConfigMap.put(databaseType, prop);
		}
	}
	
	public static DBTypeProperties getDatabaseDataTypeMap(String databaseName, String databaseType) throws FileNotFoundException
	{	
		// Either databaseName or databaseType could be kept null. Providing both of them is not necessary.
			
		if(databaseType == null)
			databaseType = CustomTagDBConfigManager.getConfig(databaseName).getCustomTagDBType();

		DBTypeProperties prop = customTagDBTypeConfigMap.get(databaseType);
		
		return prop;
	}

	private static DBTypeProperties add(String databaseType) throws FileNotFoundException
	{
		// Do actual parsing and return DBTypeProperties object from DBTypeConfig/<databaseName>/dbtypemetadata.properties
		
		String filesList[] = null;
		filesList = getAllDatabaseNames();
		
		if(doesDatabaseExist(databaseType, filesList))
		{
			return parse(databaseType);
		}
		
		return null;
	}	
	
	public static boolean doesDatabaseExist(String dbName, String[] filesList)
	{
		
		for(int i=0; i<filesList.length; i++)
		{
			if(filesList[i].equals(dbName))
				return true;
		}
		
		return false;
	}
	
	public static String[] getAllDatabaseNames() throws FileNotFoundException
	{
		String filePath = "";
		File file = null;
		
		filePath = EnvironmentalConstants.getAppHome() + File.separator + QueryIOConstants.DB_CONFIG_FOLDER;
		file = new File(filePath);
		
		return file.list((FilenameFilter) HiddenFileFilter.VISIBLE);
	}
	
	private static DBTypeProperties parse(String dbType) throws FileNotFoundException
	{
		String filePath = "";
		Properties prop = new Properties();
		FileInputStream file = null;
		DBTypeProperties dbProperties = new DBTypeProperties();
		Map<Class, String> typeMap = null;
		
		try
		{
			filePath = EnvironmentalConstants.getAppHome() + File.separator + QueryIOConstants.DB_CONFIG_FOLDER + File.separator + dbType + File.separator + QueryIOConstants.DB_CONFIG_METADATA_PROPERTIES;
//			filePath = "/QueryIO/tomcat/webapps/queryio/DBTypeConfig/PostgreSQL/dbtypemetadata.properties";
			
			AppLogger.getLogger().debug("Parsing dbType: " + dbType);
			
			file = new FileInputStream(filePath);
			
			prop.load(file);
			
			typeMap = new HashMap<Class, String>();
			typeMap.put(MetadataConstants.STRING_WRAPPER_CLASS , prop.getProperty(MetadataConstants.STRING_TYPE_METADATA));
			typeMap.put(MetadataConstants.TIMESTAMP_WRAPPER_CLASS , prop.getProperty(MetadataConstants.TIMESTAMP_TYPE_METADATA));
			typeMap.put(MetadataConstants.LONG_WRAPPER_CLASS , prop.getProperty(MetadataConstants.LONG_TYPE_METADATA));
			typeMap.put(MetadataConstants.SHORT_WRAPPER_CLASS , prop.getProperty(MetadataConstants.SHORT_TYPE_METADATA));
			typeMap.put(MetadataConstants.INTEGER_WRAPPER_CLASS , prop.getProperty(MetadataConstants.INTEGER_TYPE_METADATA));
			typeMap.put(MetadataConstants.REAL_WRAPPER_CLASS , prop.getProperty(MetadataConstants.REAL_TYPE_METADATA));
			typeMap.put(MetadataConstants.DOUBLE_WRAPPER_CLASS , prop.getProperty(MetadataConstants.DOUBLE_TYPE_METADATA));
			typeMap.put(MetadataConstants.DECIMAL_WRAPPER_CLASS , prop.getProperty(MetadataConstants.DECIMAL_TYPE_METADATA));
			typeMap.put(MetadataConstants.BOOLEAN_WRAPPER_CLASS , prop.getProperty(MetadataConstants.BOOLEAN_TYPE_METADATA));
			typeMap.put(MetadataConstants.BLOB_WRAPPER_CLASS , prop.getProperty(MetadataConstants.BLOB_TYPE_METADATA));
			
			dbProperties.setDefaultSchema(prop.getProperty(MetadataConstants.DEFAULT_SCHEMA));
			dbProperties.setTypeMap(typeMap);
			
			return dbProperties;
		}
		catch (Exception e)
		{
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
		}
		finally
		{
			try 
			{
				file.close();
			} 
			catch (IOException e) 
			{
				AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			}
		}
		return null;
	}
	
	public static void displayDBTypeProperties(DBTypeProperties prop)
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("prop.getDefaultSchema(): " + prop.getDefaultSchema());
		
		Iterator it = prop.getTypeMap().entrySet().iterator();
		while(it.hasNext())
		{
		    Map.Entry<Class, String> entry = (Entry<Class, String>) it.next();
		    
		    if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(entry.getKey() + ": " +entry.getValue());
		}
	}
	
	public static void main(String args[])
	{
		try {
//			getAllDatabaseNames();
			displayDBTypeProperties(parse("PostgreSQL"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List getAllCustomTagDBConfigList(){
		return new ArrayList(customTagDBConfigMap.values());
	}
	
	public static CustomTagDBConfig getConfig(String poolName){
		return (CustomTagDBConfig) customTagDBConfigMap.get(poolName);
	}
	
	public static List getAllCustomTagDBTypeConfigList(){
		return new ArrayList(customTagDBTypeConfigMap.values());
	}
}
