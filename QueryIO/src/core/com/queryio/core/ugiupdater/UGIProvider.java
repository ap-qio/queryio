package com.queryio.core.ugiupdater;

import java.sql.Connection;
import java.util.ArrayList;

import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.permissions.UserGroupDAO;

public class UGIProvider {
	public static String getUserPasswords() throws Exception{
		String response = "";
		
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			
			ArrayList userNames = UserDAO.getAllUserNames(connection);
			String password = null;
			String userName = null;
			String group = null;
			
			for(int i=0; i<userNames.size(); i++){
				userName = (String) userNames.get(i);
				
				response += userName;
				response += ":";
				
				password = UserDAO.getUserDetail(connection, userName).getPassword();
				response += password;
				
				response += ":";
				
				group = UserGroupDAO.getDefaultGroupForUser(connection, userName);
				response += group;
				
				response += "@NEWLINE@";
			}
		} finally{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		
		return response;
	}
	
	public static String getUserGroupInformation() throws Exception{
		String response = "";
		
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			
			ArrayList userNames = UserDAO.getAllUserNames(connection);
			ArrayList groupNames = null;
			String userName = null;
			
			for(int i=0; i<userNames.size(); i++){
				userName = (String) userNames.get(i);
				
				response += userName;
				response += ":";
				
				groupNames = UserGroupDAO.getGroupsForUser(connection, userName);
				
				for(int j=0; j<groupNames.size(); j++){
					response += (String) groupNames.get(j);
					if(j != groupNames.size()-1)	response += ",";
				}
				
				response += "@NEWLINE@";
			}
		} finally{
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		
		return response;
	}
}
