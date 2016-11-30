package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.CheckpointNode;

public class CheckpointNodeDAO {
	public static void insert(Connection connection, CheckpointNode node) throws SQLException {
		String query = "INSERT INTO " + TableConstants.TABLE_CHECKPOINTNODES + " ("
				+ ColumnConstants.COL_CHECKPOINTNODES_ID + "," + ColumnConstants.COL_CHECKPOINTNODES_HOSTID + ")"
				+ " VALUES (?,?)";
		PreparedStatement pst = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, node.getId());
			pst.setInt(2, node.getHostId());
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static void delete(Connection connection, String id) throws SQLException {
		String query = "DELETE FROM " + TableConstants.TABLE_CHECKPOINTNODES + " WHERE "
				+ ColumnConstants.COL_CHECKPOINTNODES_ID + " = ?";
		PreparedStatement pst = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, query);
			pst.setString(1, id);
			pst.execute();
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
		}
	}

	public static CheckpointNode get(Connection connection, String id) throws SQLException {
		String query = "SELECT * FROM " + TableConstants.TABLE_CHECKPOINTNODES + " WHERE "
				+ ColumnConstants.COL_CHECKPOINTNODES_ID + " = ?";
		PreparedStatement st = null;
		ResultSet rs = null;
		CheckpointNode node = null;
		try {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug(query);
			st = DatabaseFunctions.getPreparedStatement(connection, query);
			st.setString(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				node = new CheckpointNode(rs.getString(ColumnConstants.COL_CHECKPOINTNODES_ID),
						rs.getInt(ColumnConstants.COL_CHECKPOINTNODES_HOSTID));
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return node;
	}

	public static ArrayList getAll(Connection connection) throws SQLException {
		String query = "SELECT * FROM " + TableConstants.TABLE_CHECKPOINTNODES;
		PreparedStatement st = null;
		ResultSet rs = null;
		CheckpointNode node = null;
		ArrayList list = new ArrayList();
		try {

			for (int i = 1; i < 10; i++) {
				CheckpointNode node1 = new CheckpointNode("node" + i, 1);
				node1.setDirPath("dirpath" + i);
				if (i % 2 == 0)
					node1.setStatus("Started");
				else
					node1.setStatus("Stopped");
				list.add(node1);
			}
			return list;
			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug(query);
			// st = DatabaseFunctions.getPreparedStatement(connection, query);
			// rs = st.executeQuery();
			// while(rs.next()){
			// list.add(new
			// CheckpointNode(rs.getString(ColumnConstants.COL_CHECKPOINTNODES_ID),
			// rs.getInt(ColumnConstants.COL_CHECKPOINTNODES_HOSTID)));
			// }
		} finally {
			// DatabaseFunctions.closeSQLObjects(st, rs);
		}
		// return list;
	}
}
