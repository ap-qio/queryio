package com.queryio.core.permissions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.core.bean.User;
import com.queryio.core.dao.UserDAO;

public class UserGroupDAO {
	
	public static boolean isUserAddedToGroup(final Connection connection, final String userName, final String groupName) throws Exception{
		boolean added = false;
		
		int userId = UserDAO.getUserId(connection, userName);
		String group = null;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_GROUPS_FOR_USER);
			ps.setInt(1, userId);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			while (rs.next())
			{
				group = GroupDAO.getGroupName(connection, rs.getInt(ColumnConstants.COL_USER_GROUPS_GROUPID));
				
				if(group.equalsIgnoreCase(groupName)){
					added = true;
					break;
				}
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}
		
		return added;
	}
	public static ArrayList getUsersForGroup(final Connection connection, String groupName) throws Exception{
		ArrayList userList = new ArrayList();
		
		int groupId = GroupDAO.getGroupId(connection, groupName);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_USERS_FOR_GROUP);
			ps.setInt(1, groupId);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			User user = null;
			while (rs.next())
			{
				
				user = UserDAO.getUserDetail(connection, rs.getInt(ColumnConstants.COL_USER_GROUPS_USERID));
				if(user != null)	userList.add(user);
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}
		return userList;
	}
	
	public static ArrayList getGroupsForUser(final Connection connection, String userName) throws Exception{
		ArrayList list = new ArrayList();
		
		int userId = UserDAO.getUserId(connection, userName);
		int groupId = -1;
		String groupName = null;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_GROUPS_FOR_USER);
			ps.setInt(1, userId);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			while (rs.next())
			{
				groupName = GroupDAO.getGroupName(connection, rs.getInt(ColumnConstants.COL_USER_GROUPS_GROUPID));
				
				if(groupName != null)	list.add(groupName);
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}
		
		return list;
	}
	
	public static String getDefaultGroupForUser(final Connection connection, String userName) throws Exception{
		int userId = UserDAO.getUserId(connection, userName);
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
				groupName = GroupDAO.getGroupName(connection, rs.getInt(ColumnConstants.COL_USER_GROUPS_GROUPID));
				
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
	
	public static void addUserToGroup(final Connection connection, String userName, String groupName, boolean isDefault) throws Exception{
		
		if(isUserAddedToGroup(connection, userName, groupName))	return;
		
		int userId = UserDAO.getUserId(connection, userName);
		
		if(userId == -1)	throw new Exception("User with specified username does not exist");
		
		int groupId = GroupDAO.getGroupId(connection, groupName);
		
		if(groupId==-1)	{
			GroupDAO.addGroup(connection, groupName);
			
			groupId = GroupDAO.getGroupId(connection, groupName);
		}
		
		PreparedStatement ps = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_ADD_USER_TO_GROUP);
			ps.setInt(1, userId);
			ps.setInt(2, groupId);
			ps.setBoolean(3, isDefault);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
	
	public static void deleteUserFromGroup(final Connection connection, int userId, String groupName) throws Exception{
		
		int groupId = GroupDAO.getGroupId(connection, groupName);
		
		if(groupId==-1)		throw new Exception("Group with specified name does not exist");
		
		PreparedStatement ps = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_USER_FROM_GROUP);
			ps.setInt(1, userId);
			ps.setInt(2, groupId);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
	public static void deleteUserFromAllGroups(final Connection connection, int userId) throws Exception{
		
		
		PreparedStatement ps = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_USER_FROM_ALL_GROUP);
			ps.setInt(1, userId);
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
}
