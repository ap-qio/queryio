package com.queryio.ftpserver.userinfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;

public class UserInfoManager {
	protected static final Logger LOGGER = Logger.getLogger(UserInfoManager.class);
	
	public static void fetchUserInformation() throws Exception {
		LOGGER.debug("Fetching user information");
		HashMap<String, String> userMap = new HashMap<String, String>();
		HashMap<String, String> groupMap = new HashMap<String, String>();
		
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			statement = DatabaseFunctions.getStatement(connection);
				
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_ALL_USERNAMES_PASSWORDS);
			while(rs.next())
			{
				LOGGER.debug("User: " + rs.getString(ColumnConstants.COL_USER_USERNAME) + " : " + rs.getString(ColumnConstants.COL_USER_PASSWORD));
				userMap.put(rs.getString(ColumnConstants.COL_USER_USERNAME), rs.getString(ColumnConstants.COL_USER_PASSWORD));
				groupMap.put(rs.getString(ColumnConstants.COL_USER_USERNAME), getDefaultGroupForUser(connection, rs.getString(ColumnConstants.COL_USER_USERNAME)));
			}
		}
		finally
		{
			try
			{
				DatabaseFunctions.closeSQLObjects(statement, rs);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		
		UserInfoContainer.setUserInfo(userMap);
		UserInfoContainer.setUserGroupInfo(groupMap);
	}
	
	public static int getUserId(Connection connection, String userName) throws Exception
	{
		int id = -1;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_USERID_FROM_USERNAME);
			ps.setString(1, userName);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			if (rs.next())
			{
				id = rs.getInt(ColumnConstants.COL_USER_ID);
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}
		
		return id;
	}
	
	public static String getGroupName(final Connection connection, int id) throws Exception{
		String groupName = null;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_GROUP_NAME_FOR_ID);
			ps.setInt(1, id);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			while (rs.next())
			{
				groupName = rs.getString(ColumnConstants.COL_GROUPS_GROUPNAME);
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}
		
		return groupName;		
	}
	
	public static String getDefaultGroupForUser(final Connection connection, String userName) throws Exception{
		int userId = getUserId(connection, userName);
		String groupName = null;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_DEFAULT_GROUP_FOR_USER);
			ps.setInt(1, userId);
			ps.setBoolean(2, true);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			if (rs.next())
			{
				groupName = getGroupName(connection, rs.getInt(ColumnConstants.COL_USER_GROUPS_GROUPID));
				
				return groupName;
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}
		
		return null;
	}
}
