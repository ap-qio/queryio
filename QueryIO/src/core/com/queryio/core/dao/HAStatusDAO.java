package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;

public class HAStatusDAO {
	public static void insertEntry(Connection connection, String activeNodeId, String standbyNodeId) throws Exception {
		PreparedStatement pst = null;
		String query = "INSERT INTO " + TableConstants.TABLE_HASTATUS + " VALUES (?,?)";
		try {
			pst = connection.prepareStatement(query);
			pst.setString(1, activeNodeId);
			pst.setString(2, standbyNodeId);
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static void swapEntry(Connection connection, String newActiveNodeId, String newStandbyNodeId)
			throws Exception {
		PreparedStatement pst = null;
		String query = "UPDATE " + TableConstants.TABLE_HASTATUS + " SET " + ColumnConstants.COL_HASTATUS_ACTIVENODEID
				+ " = ?, " + ColumnConstants.COL_HASTATUS_STANDBYNODEID + " = ? WHERE "
				+ ColumnConstants.COL_HASTATUS_ACTIVENODEID + " = ?";
		try {
			pst = connection.prepareStatement(query);
			pst.setString(1, newActiveNodeId);
			pst.setString(2, newStandbyNodeId);
			pst.setString(3, newStandbyNodeId);
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static boolean isHANode(Connection connection, String nodeId) throws Exception {
		PreparedStatement pst = null;
		String query = "SELECT * FROM " + TableConstants.TABLE_HASTATUS + " WHERE "
				+ ColumnConstants.COL_HASTATUS_ACTIVENODEID + " = ? OR " + ColumnConstants.COL_HASTATUS_STANDBYNODEID
				+ " = ?";
		ResultSet rs = null;
		try {
			pst = connection.prepareStatement(query);
			pst.setString(1, nodeId);
			pst.setString(2, nodeId);
			rs = pst.executeQuery();
			if (rs.next()) {
				return true;
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(pst, rs);
		}
		return false;
	}

	public static String getStandbyNodeId(Connection connection, String nodeId) throws Exception {
		PreparedStatement pst = null;
		String query = "SELECT * FROM " + TableConstants.TABLE_HASTATUS + " WHERE "
				+ ColumnConstants.COL_HASTATUS_ACTIVENODEID + " = ?";
		ResultSet rs = null;
		try {
			pst = connection.prepareStatement(query);
			pst.setString(1, nodeId);
			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getString(ColumnConstants.COL_HASTATUS_STANDBYNODEID);
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(pst, rs);
		}
		return null;
	}

	public static String getActiveNodeId(Connection connection, String nodeId) throws Exception {
		PreparedStatement pst = null;
		String query = "SELECT * FROM " + TableConstants.TABLE_HASTATUS + " WHERE "
				+ ColumnConstants.COL_HASTATUS_STANDBYNODEID + " = ?";
		ResultSet rs = null;
		try {
			pst = connection.prepareStatement(query);
			pst.setString(1, nodeId);
			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getString(ColumnConstants.COL_HASTATUS_ACTIVENODEID);
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(pst, rs);
		}
		return null;
	}

	public static void removeEntry(Connection connection, String activeNodeId) throws Exception {
		PreparedStatement pst = null;
		String query = "DELETE FROM " + TableConstants.TABLE_HASTATUS + " WHERE "
				+ ColumnConstants.COL_HASTATUS_ACTIVENODEID + " = ?";
		try {
			pst = connection.prepareStatement(query);
			pst.setString(1, activeNodeId);
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}
}
