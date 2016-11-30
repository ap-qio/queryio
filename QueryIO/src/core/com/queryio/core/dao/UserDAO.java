package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SecurityHandler;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.User;
import com.queryio.core.monitor.beans.SummaryTable;
import com.queryio.core.permissions.UserGroupDAO;

public class UserDAO {
	public static ArrayList getUsersDetails(final Connection connection) throws Exception {
		ArrayList ar = new ArrayList();
		Statement statement = null;
		ResultSet rs = null;

		try {
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_ALL_USERSINFO);
			while (rs.next()) {
				User user = new User();
				user.setUserName(rs.getString(ColumnConstants.COL_USER_USERNAME));
				user.setId(rs.getInt(ColumnConstants.COL_USER_ID));
				user.setFirstName(rs.getString(ColumnConstants.COL_USER_FIRSTNAME));
				user.setLastName(rs.getString(ColumnConstants.COL_USER_LASTNAME));
				user.setEmail(rs.getString(ColumnConstants.COL_USER_EMAIL));
				user.setPassword(rs.getString(ColumnConstants.COL_USER_PASSWORD));

				ar.add(user);
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(statement, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		return ar;

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

	public static SummaryTable getUsers(final Connection connection) throws Exception {
		SummaryTable summaryTable = new SummaryTable();

		Statement statement = null;
		ResultSet rs = null;

		try {

			ArrayList colNames = new ArrayList();
			ArrayList colValues = null;

			colNames.add("User ID");
			colNames.add("User Name");
			colNames.add("First Name");
			colNames.add("Last Name");
			colNames.add("E Mail");
			colNames.add("Role");

			summaryTable.setColNames(colNames);

			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_ALL_USERSINFO);

			while (rs.next()) {
				String userName = rs.getString(ColumnConstants.COL_USER_USERNAME);

				colValues = new ArrayList();

				colValues.add(rs.getInt(ColumnConstants.COL_USER_ID));
				colValues.add(userName);
				colValues.add(rs.getString(ColumnConstants.COL_USER_FIRSTNAME));
				colValues.add(rs.getString(ColumnConstants.COL_USER_LASTNAME));
				colValues.add(rs.getString(ColumnConstants.COL_USER_EMAIL));

				String roleName = getRole(connection, userName);
				if (roleName != null) {
					colValues.add(roleName);
				} else {
					colValues.add("No Role");
				}

				summaryTable.addRow(colValues);
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(statement, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}

		return summaryTable;
	}

	public static void insertUser(Connection connection, User user, String userRole, DWRResponse dwrResponse)
			throws Exception {
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		try {
			ps1 = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_INSERT_USER);

			user.setPassword(SecurityHandler.encryptData(user.getPassword()));

			ps1.setString(1, user.getUserName());
			ps1.setString(2, user.getFirstName());
			ps1.setString(3, user.getLastName());
			ps1.setString(4, user.getPassword());
			ps1.setString(5, user.getEmail());

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Adding user: " + user.getUserName());

			CoreDBManager.executeUpdateStatement(connection, ps1);

			ps2 = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_INSERT_USER_ROLE);

			ps2.setString(1, user.getUserName());
			ps2.setString(2, userRole);
			CoreDBManager.executeUpdateStatement(connection, ps2);
			dwrResponse.setDwrResponse(true, "User Inserted successfully", 200);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception while adding new User: " + e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps1);
			DatabaseFunctions.closePreparedStatement(ps2);
		}
	}

	public static String deleteUser(Connection connection, int userId) throws Exception {
		PreparedStatement ps = null;
		try {
			String userName = (getUserDetail(connection, userId)).getUserName();
			deleteRole(connection, userName);

			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_USER);
			ps.setInt(1, userId);
			CoreDBManager.executeUpdateStatement(connection, ps);
			return QueryIOConstants.RETURN_SUCCESS + "_" + QueryIOConstants.USER_DELETED + "-" + userId;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception while deleting User: " + e.getMessage(), e);
			return QueryIOConstants.RETURN_FAILURE + "_" + e.getMessage() + "-" + userId;
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);

		}
	}

	public static void updateUser(Connection connection, User user, DWRResponse dwrResponse) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_USER);

			ps.setString(1, user.getUserName());
			ps.setString(2, user.getFirstName());
			ps.setString(3, user.getLastName());
			ps.setString(4, user.getEmail());
			ps.setInt(5, user.getId());

			CoreDBManager.executeUpdateStatement(connection, ps);
			dwrResponse.setDwrResponse(true, "User Update Successfully", 200);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception while updating existing User: " + e.getMessage(), e);
			dwrResponse.setDwrResponse(false, e.getMessage(), 500);

		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void updatePassword(Connection connection, int id, String oldPassword, String newPassword,
			DWRResponse dwrResponse) throws Exception {
		PreparedStatement ps = null;
		try {
			if (!isOldPasswordCorrect(connection, id, oldPassword)) {
				dwrResponse.setDwrResponse(false, "Incorrect Password", 200);
				return;
			}

			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_PASSWORD);
			ps.setString(1, SecurityHandler.encryptData(newPassword));
			ps.setInt(2, id);

			CoreDBManager.executeUpdateStatement(connection, ps);
			dwrResponse.setDwrResponse(true, "Password Updated Successfully.", 200);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static boolean isOldPasswordCorrect(Connection connection, int id, String password) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_PASSWORD);
			ps.setInt(1, id);

			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			if (rs.next()) {
				if (SecurityHandler.encryptData(password).equals(rs.getString(ColumnConstants.COL_USER_PASSWORD))) {
					return true;
				}
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
		return false;
	}

	public static User getUserDetail(Connection connection, int userId) throws Exception {
		User user = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_USER);
			ps.setInt(1, userId);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				user = new User();
				user.setId(rs.getInt(ColumnConstants.COL_USER_ID));
				user.setUserName(rs.getString(ColumnConstants.COL_USER_USERNAME));
				user.setFirstName(rs.getString(ColumnConstants.COL_USER_FIRSTNAME));
				user.setLastName(rs.getString(ColumnConstants.COL_USER_LASTNAME));
				user.setPassword(rs.getString(ColumnConstants.COL_USER_PASSWORD));
				user.setEmail(rs.getString(ColumnConstants.COL_USER_EMAIL));
				user.setRole(getRole(connection, rs.getString(ColumnConstants.COL_USER_USERNAME)));
				user.setGroups(
						UserGroupDAO.getGroupsForUser(connection, rs.getString(ColumnConstants.COL_USER_USERNAME)));
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}

		return user;
	}

	public static User getUserDetail(Connection connection, String userName) throws Exception {
		User user = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_USER_FROM_USERNAME);
			ps.setString(1, userName);

			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				user = new User();
				user.setId(rs.getInt(ColumnConstants.COL_USER_ID));
				user.setUserName(rs.getString(ColumnConstants.COL_USER_USERNAME));
				user.setFirstName(rs.getString(ColumnConstants.COL_USER_FIRSTNAME));
				user.setLastName(rs.getString(ColumnConstants.COL_USER_LASTNAME));
				user.setPassword(rs.getString(ColumnConstants.COL_USER_PASSWORD));
				user.setEmail(rs.getString(ColumnConstants.COL_USER_EMAIL));
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}

		return user;
	}

	public static String getRole(Connection connection, String username) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs1 = null;
		String roleName = "";
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_ROLE);
			ps.setString(1, username);
			rs1 = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			if (rs1.next()) {
				roleName = rs1.getString(ColumnConstants.COL_USER_ROLES_ROLENAME);
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception while fetching User Role: " + e.getMessage(), e);
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs1);
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}

		return roleName;
	}

	public static void deleteRole(Connection connection, String username) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_ROLE);
			ps.setString(1, username);

			CoreDBManager.executeUpdateStatement(connection, ps);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception while deleting User Role: " + e.getMessage(), e);
		} finally {
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}
	}

	public static int getUserCount(final Connection connection) throws Exception {
		int count = 0;
		Statement statement = null;
		ResultSet rs = null;

		try {
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, "SELECT COUNT(USERNAME) FROM USERS");
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(statement, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Database Objects could not be closed, Exception: " + e.getMessage(), e);
			}
		}
		return count;

	}

}