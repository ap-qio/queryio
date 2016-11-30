package com.queryio.common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.beans.NNRestoreInfo;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.database.TableConstants;

public class NNRestoreInfoDAO {
	public static void addRestoreInfo(final Connection connection, String restoreId, String migrationId,
			String nameNodeId) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.INSERT_NN_RESTOTRE_STATUS);

			ps.setString(1, restoreId);
			ps.setString(2, migrationId);
			DatabaseFunctions.setDateTime(ps, 3, System.currentTimeMillis());
			ps.setString(4, nameNodeId);
			ps.setString(5, QueryIOConstants.PROCESS_STATUS_INPROGRESS);

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void updateRestoreInfo(final Connection connection, String restoreId, Timestamp endTime,
			String status) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.UPDATE_NN_RESTORE_STATUS);

			DatabaseFunctions.setDateTime(ps, 1, endTime.getTime());
			ps.setString(2, status);
			ps.setString(3, restoreId);

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void deleteRestoreInfo(final Connection connection, String restoreId) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.DELETE_NN_RESTORE);

			ps.setString(1, restoreId);

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void deleteRestoreInfoFromList(final Connection connection, String restoreIds) throws Exception {
		Statement stmt = null;
		String deleteQuery = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			deleteQuery = "DELETE FROM " + TableConstants.TABLE_NN_RESTORE_STATUS + " WHERE "
					+ ColumnConstants.COL_NN_RESTORE_STATUS_RESTOREID + " IN (" + restoreIds + ")";

			CoreDBManager.executeUpdateStatement(connection, stmt, deleteQuery);
		} finally {
			DatabaseFunctions.closeStatement(stmt);
		}
	}

	public static ArrayList<NNRestoreInfo> getRestoreInfoList(final Connection connection) throws Exception {
		ArrayList<NNRestoreInfo> list = new ArrayList<NNRestoreInfo>();

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);

			rs = DatabaseFunctions.getQueryResultsForStatement(stmt,
					"SELECT * FROM " + TableConstants.TABLE_NN_RESTORE_STATUS);

			while (rs.next()) {
				NNRestoreInfo info = new NNRestoreInfo();

				info.setRestoreId(rs.getString(ColumnConstants.COL_NN_RESTORE_STATUS_RESTOREID));
				info.setMigrationId(rs.getString(ColumnConstants.COL_NN_RESTORE_STATUS_MIGRATIONID));
				String namenodeId = rs.getString(ColumnConstants.COL_NN_RESTORE_STATUS_NAMENODEID);
				info.setNamenodeId(namenodeId);
				info.setStartTime(rs.getTimestamp(ColumnConstants.COL_NN_RESTORE_STATUS_START_TIME));
				info.setEndTime(rs.getTimestamp(ColumnConstants.COL_NN_RESTORE_STATUS_END_TIME));
				info.setStatus(rs.getString(ColumnConstants.COL_NN_RESTORE_STATUS_STATUS));
				list.add(info);
			}
		} finally {
			DatabaseFunctions.closeStatement(stmt);
			DatabaseFunctions.closeResultSet(rs);
		}

		return list;
	}

	public static ArrayList<NNRestoreInfo> getRestoreInfoSelectedList(final Connection connection, String restoreIds)
			throws Exception {
		ArrayList<NNRestoreInfo> list = new ArrayList<NNRestoreInfo>();

		Statement stmt = null;
		ResultSet rs = null;
		String selectQuery = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			selectQuery = "SELECT * FROM " + TableConstants.TABLE_NN_RESTORE_STATUS + " WHERE "
					+ ColumnConstants.COL_NN_RESTORE_STATUS_RESTOREID + " IN (" + restoreIds + ")";

			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, selectQuery);

			while (rs.next()) {
				NNRestoreInfo info = new NNRestoreInfo();

				info.setRestoreId(rs.getString(ColumnConstants.COL_NN_RESTORE_STATUS_RESTOREID));
				info.setMigrationId(rs.getString(ColumnConstants.COL_NN_RESTORE_STATUS_MIGRATIONID));
				String namenodeId = rs.getString(ColumnConstants.COL_NN_RESTORE_STATUS_NAMENODEID);
				info.setNamenodeId(namenodeId);
				info.setStartTime(rs.getTimestamp(ColumnConstants.COL_NN_RESTORE_STATUS_START_TIME));
				info.setEndTime(rs.getTimestamp(ColumnConstants.COL_NN_RESTORE_STATUS_END_TIME));
				info.setStatus(rs.getString(ColumnConstants.COL_NN_RESTORE_STATUS_STATUS));
				list.add(info);
			}
		} finally {
			DatabaseFunctions.closeStatement(stmt);
			DatabaseFunctions.closeResultSet(rs);
		}

		return list;
	}
}
