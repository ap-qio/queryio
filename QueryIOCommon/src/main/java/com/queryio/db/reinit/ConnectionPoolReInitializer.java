package com.queryio.db.reinit;

import java.io.File;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfig;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DatabaseManager;
import com.queryio.common.util.AppLogger;

public class ConnectionPoolReInitializer {

	public static void reInitializeDB(String driverName, String jarFile, String userName, String password, String connectionURL, String connectionName, String currentConnectionName, boolean isCustomTagDB) throws Exception  
	{
	try{
		if(isCustomTagDB){
			for(int i = 0; i < CustomTagDBConfigManager.getAllCustomTagDBConfigList().size(); i ++){
				CustomTagDBConfig config = CustomTagDBConfigManager.getConfig(currentConnectionName);
				if(config != null){
					config.setCustomTagUrl(connectionURL);
					config.setCustomTagUserName(userName); 
					config.setCustomTagPassword(password);
					config.setCustomTagDriverJarPath(EnvironmentalConstants.getJdbcDriverPath()+File.separator+jarFile);
					config.setCustomTagDriverClass(driverName);
					config.setCustomTagPoolName(connectionName); 
					DatabaseManager.removeConnectionPool(currentConnectionName);
				}else{
					config = new CustomTagDBConfig();
					config.setCustomTagUrl(connectionURL);
					config.setCustomTagUserName(userName); 
					config.setCustomTagPassword(password);
					config.setCustomTagDriverJarPath(EnvironmentalConstants.getJdbcDriverPath()+File.separator+jarFile);
					config.setCustomTagDriverClass(driverName);
					config.setCustomTagPoolName(connectionName);
					CustomTagDBConfigManager.addCustomTagDBConfig(config);
				}
				CoreDBManager.reInitializeCustomTagConnection();
			}
			
		}
		else
		{
			EnvironmentalConstants.setQueryIODatabaseURL(connectionURL);
			EnvironmentalConstants.setQueryIODatabaseUserName(userName); 
			EnvironmentalConstants.setQueryIODatabasePassword(password);
			EnvironmentalConstants.setQueryIODatabaseDriverPath(EnvironmentalConstants.getJdbcDriverPath()+File.separator+jarFile);
			EnvironmentalConstants.setQueryIODatabaseDriverClass(driverName);
			EnvironmentalConstants.setQueryIODatabasePoolName(connectionName); 
			DatabaseManager.removeConnectionPool(currentConnectionName);
			CoreDBManager.reInitializeQueryIOConnection();
		}
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Re Initializing DB");
	}
	catch(Exception e)
	{
		throw e;
	}
		
	}
	
}
