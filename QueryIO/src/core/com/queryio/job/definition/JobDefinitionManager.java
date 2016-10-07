package com.queryio.job.definition;

import java.sql.Connection;

import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;

public class JobDefinitionManager {
	public static void addJobDefinition(String jobName, String tableName){
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			JobDefinitionDAO.addJobDefinition(connection, jobName, tableName);
		} catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try{
				CoreDBManager.closeConnection(connection);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
	
	public static void deleteJobDefinition(String jobName, String tableName){
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			JobDefinitionDAO.deleteJobDefinition(connection, jobName);
		} catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try{
				CoreDBManager.closeConnection(connection);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
	
	public static String getTableName(String jobName){
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			return JobDefinitionDAO.getTableName(connection, jobName);
		} catch(Exception e){
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try{
				CoreDBManager.closeConnection(connection);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return null;
	}
}
