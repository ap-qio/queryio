package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.database.TableConstants;
import com.queryio.core.bean.HadoopService;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;

public class HadoopServiceDAO {

	public static void insert(Connection connection, HadoopService hadoopService) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_INSERT_HADOOPSERVICE);
			ps.setString(1, hadoopService.getNodeId());
			ps.setTimestamp(2, hadoopService.getTimeOfCall());
			ps.setString(3, hadoopService.getType());
			ps.setString(4, hadoopService.getStatus());
			ps.setString(5, hadoopService.getOutputFilePath());

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void update(Connection connection, HadoopService hadoopService) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_HADOOPSERVICE);

			ps.setString(1, hadoopService.getStatus());
			ps.setString(2, hadoopService.getOutputFilePath());
			ps.setString(3, hadoopService.getNodeId());
			ps.setTimestamp(4, hadoopService.getTimeOfCall());
			ps.setString(5, hadoopService.getType());

			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static ArrayList getAll(Connection connection) throws Exception {
		ArrayList data = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try {
			st = connection.createStatement();
			rs = st.executeQuery("SELECT * FROM " + TableConstants.TABLE_HADOOPSERVICES);

			while (rs.next()) {
				ArrayList rowData = new ArrayList();
				String id = rs.getString(ColumnConstants.COL_HADOOPSERVICES_NODEID);
				Node node = NodeDAO.getNode(connection, id);
				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				rowData.add(id);
				rowData.add(host.getHostIP());
				rowData.add(rs.getString(ColumnConstants.COL_HADOOPSERVICES_TIMEOFCALL));
				rowData.add(rs.getString(ColumnConstants.COL_HADOOPSERVICES_TYPE));
				rowData.add(rs.getString(ColumnConstants.COL_HADOOPSERVICES_STATUS));
				rowData.add(rs.getString(ColumnConstants.COL_HADOOPSERVICES_OUTPUTFILEPATH));
				data.add(rowData);
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return data;
	}

	public static ArrayList delete(Connection connection, String nodeId) throws Exception {
		ArrayList data = new ArrayList();
		Statement st = null;
		try {
			st = connection.createStatement();
			st.execute("DELETE FROM " + TableConstants.TABLE_HADOOPSERVICES + " WHERE "
					+ ColumnConstants.COL_HADOOPSERVICES_NODEID + " = '" + nodeId + "'");
		} finally {
			DatabaseFunctions.closeStatement(st);
		}
		return data;
	}

}
