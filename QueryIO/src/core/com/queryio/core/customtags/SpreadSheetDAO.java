package com.queryio.core.customtags;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;

public class SpreadSheetDAO {

	public static void insert(Connection connection, String sheetId, String path, String nameNodeId)
			throws SQLException {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_SPREADSHEETS_INSERT_SHEET);
			ps.setString(1, sheetId);
			ps.setString(2, path);
			ps.setString(3, nameNodeId);

			DatabaseFunctions.executeUpdateStatement(ps);
		} finally {
			try {
				DatabaseFunctions.closeStatement(ps);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static ArrayList<String> getSheetId(Connection connection, String nameNodeId) throws SQLException {
		ArrayList<String> ids = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_SPREADSHEETS_GET_SHEET_IDS);
			ps.setString(1, nameNodeId);

			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				ids.add(rs.getString(ColumnConstants.COL_SPREADSHEETS_SHEET_ID));
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return ids;
	}

	public static String getPath(Connection connection, String sheetId) throws SQLException {
		String path = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_SPREADSHEETS_GET_PATH);
			ps.setString(1, sheetId);

			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				path = rs.getString(ColumnConstants.COL_SPREADSHEETS_PATH);
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return path;
	}

	public static void delete(Connection connection, String sheetId) throws SQLException {
		PreparedStatement ps = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_SPREADSHEETS_DELETE_SHEET);
			ps.setString(1, sheetId);

			DatabaseFunctions.executeUpdateStatement(ps);
		} finally {
			try {
				DatabaseFunctions.closeStatement(ps);
			} catch (SQLException e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
}
