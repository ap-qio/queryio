package com.queryio.core.permissions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;

public class GroupDAO {
	
	public static void addGroup(final Connection connection, String groupName) throws Exception{
		groupName = groupName.toLowerCase();
		
		if(getGroupId(connection, groupName) != -1)	return;
		
		PreparedStatement ps = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_ADD_GROUP);
			ps.setString(1, groupName);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
	
	public static void deleteGroup(final Connection connection, String groupName) throws Exception{
		PreparedStatement ps = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_GROUP_WITH_NAME);
			ps.setString(1, groupName);
			
			CoreDBManager.executeUpdateStatement(connection, ps);
		}
		finally
		{
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}
	
	public static ArrayList getAllGroupsList(final Connection connection) throws Exception{
		ArrayList list = new ArrayList();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_GROUP_NAMES);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			while (rs.next())
			{
				list.add(rs.getString(ColumnConstants.COL_GROUPS_GROUPNAME));
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}
		
		return list;
	}
	
	public static int getGroupId(final Connection connection, String group) throws Exception{
		int groupId = -1;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_GROUP_ID_FOR_NAME);
			ps.setString(1, group);
			
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			
			while (rs.next())
			{
				groupId = rs.getInt(ColumnConstants.COL_GROUPS_GROUPID);
			}
		}
		finally
		{
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}
		
		return groupId;
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
}
