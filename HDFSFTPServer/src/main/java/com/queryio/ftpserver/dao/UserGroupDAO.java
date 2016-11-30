package com.queryio.ftpserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;

public class UserGroupDAO {
	public static ArrayList getGroupsForUser(final Connection connection, String userName) throws Exception {
		ArrayList list = new ArrayList();

		int userId = getUserId(connection, userName);
		// int groupId = -1;
		String groupName = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_GROUPS_FOR_USER);
			ps.setInt(1, userId);

			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				groupName = getGroupName(connection, rs.getInt(ColumnConstants.COL_USER_GROUPS_GROUPID));

				if (groupName != null)
					list.add(groupName);
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}

		return list;
	}

	public static int getUserId(Connection connection, String userName) throws Exception {
		int id = -1;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_GET_USERID_FROM_USERNAME);
			ps.setString(1, userName);

			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				id = rs.getInt(ColumnConstants.COL_USER_ID);
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}

		return id;
	}

	public static String getGroupName(final Connection connection, int id) throws Exception {
		String groupName = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_GROUP_NAME_FOR_ID);
			ps.setInt(1, id);

			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				groupName = rs.getString(ColumnConstants.COL_GROUPS_GROUPNAME);
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}

		return groupName;
	}

	public static ArrayList getAllUserNames(final Connection connection) throws Exception {
		ArrayList list = new ArrayList();
		Statement statement = null;
		ResultSet rs = null;

		try {
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_ALL_USERNAMES);
			while (rs.next()) {
				list.add(rs.getString(ColumnConstants.COL_USER_USERNAME));
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(statement, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		return list;

	}
}
