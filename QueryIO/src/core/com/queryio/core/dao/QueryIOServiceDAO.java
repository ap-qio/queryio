package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.core.bean.QueryIOService;

public class QueryIOServiceDAO {
	public static void insert(Connection connection, QueryIOService service) throws Exception {
		PreparedStatement pst = null;
		try {
			String query = "INSERT INTO " + TableConstants.TABLE_QUERYIOSERVICES + "("
					+ ColumnConstants.COL_QUERYIOSERVICES_NODEID + "," + ColumnConstants.COL_QUERYIOSERVICES_NODETYPE
					+ "," + ColumnConstants.COL_QUERYIOSERVICES_STATUS + ") VALUES (?, ?, ?)";
			pst = connection.prepareStatement(query);
			pst.setString(1, service.getNodeId());
			pst.setString(2, service.getServiceType());
			pst.setString(3, service.getStatus());
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static void delete(Connection connection, String nodeId) throws Exception {
		PreparedStatement pst = null;
		try {
			String query = "DELETE FROM " + TableConstants.TABLE_QUERYIOSERVICES + " WHERE "
					+ ColumnConstants.COL_QUERYIOSERVICES_NODEID + " = ?";

			pst = connection.prepareStatement(query);
			pst.setString(1, nodeId);
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static void updateStatus(Connection connection, String nodeId, String serviceType, String status)
			throws Exception {
		PreparedStatement pst = null;
		try {
			String query = "UPDATE " + TableConstants.TABLE_QUERYIOSERVICES + " SET "
					+ ColumnConstants.COL_QUERYIOSERVICES_STATUS + " = ? WHERE "
					+ ColumnConstants.COL_QUERYIOSERVICES_NODEID + " = ? AND "
					+ ColumnConstants.COL_QUERYIOSERVICES_NODETYPE + " = ?";
			pst = connection.prepareStatement(query);
			pst.setString(1, status);
			pst.setString(2, nodeId);
			pst.setString(3, serviceType);
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static QueryIOService get(Connection connection, String nodeId, String serviceType) throws SQLException {
		PreparedStatement pst = null;
		ResultSet rs = null;
		QueryIOService service = null;
		try {
			String query = "SELECT " + ColumnConstants.COL_QUERYIOSERVICES_STATUS + " FROM "
					+ TableConstants.TABLE_QUERYIOSERVICES + " WHERE " + ColumnConstants.COL_QUERYIOSERVICES_NODEID
					+ " = ? AND " + ColumnConstants.COL_QUERYIOSERVICES_NODETYPE + " = ?";
			pst = connection.prepareStatement(query);
			pst.setString(1, nodeId);
			pst.setString(2, serviceType);
			rs = pst.executeQuery();
			if (rs.next()) {
				service = new QueryIOService(nodeId, serviceType,
						rs.getString(ColumnConstants.COL_QUERYIOSERVICES_STATUS));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
		return service;
	}
}
