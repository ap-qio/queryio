package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.Node;

public class NodeDAO {

	public static ArrayList getAllNodeIds(Connection connection) throws Exception {
		ArrayList nodeIds = null;
		Statement stmt = null;
		ResultSet rs = null;
		String id = null;
		try {
			nodeIds = new ArrayList();
			stmt = DatabaseFunctions.getStatement(connection);

			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, QueryConstants.QRY_GET_ALL_NODES_NODEID);

			while (rs.next()) {
				id = rs.getString(ColumnConstants.COL_NODE_ID);
				nodeIds.add(id);
			}
		} finally {
			DatabaseFunctions.closeStatement(stmt);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodeIds;
	}

	public static ArrayList<Node> getAllNodesForHost(Connection connection, int hostId) throws Exception {
		ArrayList<Node> nodes = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			nodes = new ArrayList<Node>();
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NODESFORHOST);
			pst.setInt(1, hostId);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setServiceStatus(rs.getString(ColumnConstants.COL_NODE_SERVICESTATUS));
				node.setHiveServiceStatus(rs.getString(ColumnConstants.COL_NODE_HIVESERVICESTATUS));
				node.setMonitor(rs.getBoolean(ColumnConstants.COL_NODE_MONITOR));
				nodes.add(node);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static Map<String, Boolean> getAllNodesTypeForHost(Connection connection, int hostId) throws Exception {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NODES_TYPE_FORHOST);
			pst.setInt(1, hostId);
			rs = pst.executeQuery();

			while (rs.next()) {
				String nodeType = rs.getString(ColumnConstants.COL_NODE_NODETYPE);
				map.put(nodeType, true);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return map;
	}

	public static ArrayList getAllDataNodesForHost(Connection connection, int hostId) throws Exception {
		ArrayList nodes = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_DATA_NODESFORHOST);
			pst.setInt(1, hostId);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setMonitor(rs.getBoolean(ColumnConstants.COL_NODE_MONITOR));
				nodes.add(node);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static ArrayList getAllResourceManagersForHost(Connection connection, int hostId) throws Exception {
		ArrayList nodes = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.QRY_GET_ALL_RESOURCE_MANAGERS_FORHOST);
			pst.setInt(1, hostId);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setMonitor(rs.getBoolean(ColumnConstants.COL_NODE_MONITOR));
				nodes.add(node);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static ArrayList getAllNodeManagersForHost(Connection connection, int hostId) throws Exception {
		ArrayList nodes = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NODE_MANAGERS_FORHOST);
			pst.setInt(1, hostId);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setMonitor(rs.getBoolean(ColumnConstants.COL_NODE_MONITOR));
				nodes.add(node);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static ArrayList getAllNameNodesForHost(Connection connection, int hostId) throws Exception {
		ArrayList nodes = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NAME_NODESFORHOST);
			pst.setInt(1, hostId);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setServiceStatus(rs.getString(ColumnConstants.COL_NODE_SERVICESTATUS));
				node.setHiveServiceStatus(rs.getString(ColumnConstants.COL_NODE_HIVESERVICESTATUS));
				node.setMonitor(rs.getBoolean(ColumnConstants.COL_NODE_MONITOR));
				nodes.add(node);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static ArrayList getAllDatanodes(Connection connection) throws Exception {
		ArrayList nodes = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NODESFORTYPE);
			pst.setString(1, QueryIOConstants.DATANODE);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setMonitor(rs.getBoolean(ColumnConstants.COL_NODE_MONITOR));
				nodes.add(node);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static int getAllDataNodesStarted(Connection connection) throws Exception {
		int count = 0;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, QueryConstants.QRY_GET_ALL_DATANODES_STARTED);
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(stmt);
		}
		return count;
	}

	public static ArrayList getAllRMs(Connection connection) throws Exception {
		ArrayList nodes = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NODESFORTYPE);
			pst.setString(1, QueryIOConstants.RESOURCEMANAGER);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setMonitor(rs.getBoolean(ColumnConstants.COL_NODE_MONITOR));
				nodes.add(node);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static ArrayList getAllNMs(Connection connection) throws Exception {
		ArrayList nodes = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NODESFORTYPE);
			pst.setString(1, QueryIOConstants.NODEMANAGER);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setMonitor(rs.getBoolean(ColumnConstants.COL_NODE_MONITOR));
				nodes.add(node);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static ArrayList getAllNodesForType(Connection connection, String type) throws Exception {
		ArrayList nodes = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NODESFORTYPE);
			pst.setString(1, type);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setServiceStatus(rs.getString(ColumnConstants.COL_NODE_SERVICESTATUS));
				node.setHiveServiceStatus(rs.getString(ColumnConstants.COL_NODE_HIVESERVICESTATUS));
				node.setMonitor(rs.getBoolean(ColumnConstants.COL_NODE_MONITOR));
				nodes.add(node);
				nodes.add(VolumeDAO.getAllVolumes(connection, node.getId()));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static Map<String, Integer> getAllNodesCount(Connection connection) throws Exception {
		Map<String, Integer> nodeCountMap = new HashMap<String, Integer>();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String[] nodesType = { QueryIOConstants.NAMENODE, QueryIOConstants.DATANODE, QueryIOConstants.RESOURCEMANAGER,
				QueryIOConstants.NODEMANAGER };

		for (int i = 0; i < 4; i++) {
			try {
				pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NODESFORTYPE_COUNT);
				pst.setString(1, nodesType[i]);
				rs = pst.executeQuery();
				rs.next();
				nodeCountMap.put(nodesType[i], rs.getInt(1));
			} finally {
				DatabaseFunctions.closeResultSet(rs);
				DatabaseFunctions.closePreparedStatement(pst);
			}
		}

		return nodeCountMap;
	}

	public static ArrayList getAllNodeIdsForType(Connection connection, String type) throws Exception {
		ArrayList nodes = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NODESFORTYPE);
			pst.setString(1, type);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				nodes.add(rs.getString(ColumnConstants.COL_NODE_ID));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static ArrayList getAllNameNodes(Connection connection) throws Exception {
		ArrayList nodes = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NAMENODES);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				node = new Node();

				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setServiceStatus(rs.getString(ColumnConstants.COL_NODE_SERVICESTATUS));
				node.setHiveServiceStatus(rs.getString(ColumnConstants.COL_NODE_HIVESERVICESTATUS));
				node.setMonitor(rs.getBoolean(ColumnConstants.COL_NODE_MONITOR));
				nodes.add(node);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static ArrayList getAllNameNodesID(Connection connection) throws Exception {
		ArrayList nodes = new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NAMENODES_ID);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				nodes.add(rs.getString(ColumnConstants.COL_NODE_ID));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static int getAllNodeCountForType(Connection connection, String type) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int count = 0;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_COUNT_ALL_NODESFORTYPE);
			pst.setString(1, type);
			rs = pst.executeQuery();

			while (rs.next()) {
				count = rs.getInt(1);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}

		return count;
	}

	public static void insertNode(Connection connection, Node node) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_INSERT_NODE);
			ps.setString(1, node.getId());
			ps.setInt(2, node.getHostId());
			ps.setString(3, node.getNodeType());
			ps.setString(4, node.getStatus());
			ps.setString(5, node.getJmxPort());
			ps.setString(6, node.getServiceStatus());
			ps.setString(7, node.getHiveServiceStatus());
			ps.setBoolean(8, node.isMonitor());
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void insertNameNodeDBMapping(Connection connection, String namenodeId, String dbName,
			String analyticsDbName) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_INSERT_NAMENODE_DB_MAPPING);
			ps.setString(1, namenodeId);
			ps.setString(2, dbName);
			ps.setString(3, analyticsDbName);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void deleteNameNodeDBMapping(Connection connection, String namenodeId) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_DELETE_NAMENODE_DB_MAPPING);
			ps.setString(1, namenodeId);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void updateNameNodeDBMapping(Connection connection, String namenodeId, String dbName)
			throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_UPDATE_NAMENODE_DB_MAPPING);
			ps.setString(1, dbName);
			ps.setString(2, namenodeId);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void updateNameNodeAnalyticsDBMapping(Connection connection, String namenodeId, String dbName)
			throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_UPDATE_NAMENODE_ANALYTICS_DB_MAPPING);
			ps.setString(1, dbName);
			ps.setString(2, namenodeId);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static String getDBNameForNameNodeMapping(Connection connection, String namenodeId) throws Exception {
		String dbName = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.QRY_GET_DBNAME_FROM_NAMENODE_MAPPING);
			pst.setString(1, namenodeId);
			rs = pst.executeQuery();

			if (rs.next()) {
				dbName = rs.getString(ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_DBNAME);
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(pst);
		}
		return dbName;
	}

	public static String getAnalyticsDBNameForNameNodeMapping(Connection connection, String namenodeId)
			throws Exception {
		String dbName = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.QRY_GET_ANALYTICS_DBNAME_FROM_NAMENODE_MAPPING);
			pst.setString(1, namenodeId);
			rs = pst.executeQuery();

			if (rs.next()) {
				dbName = rs.getString(ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_ANALYTICS_DBNAME);
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(pst);
		}
		return dbName;
	}

	public static String getNameNodeForDBNameMapping(Connection connection, String dbName) throws Exception {
		String nameNodeId = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.QRY_GET_NAMENODE_FROM_DBNAME_MAPPING);
			pst.setString(1, dbName);
			rs = pst.executeQuery();

			if (rs.next()) {
				nameNodeId = rs.getString(ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID);
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(pst);
		}
		return nameNodeId;
	}

	public static String getNameNodeForAnalyticsDBNameMapping(Connection connection, String dbName) throws Exception {
		String nameNodeId = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.QRY_GET_NAMENODE_FROM_ANALYTICS_DBNAME_MAPPING);
			pst.setString(1, dbName);
			rs = pst.executeQuery();

			if (rs.next()) {
				nameNodeId = rs.getString(ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID);
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(pst);
		}
		return nameNodeId;
	}

	public static ArrayList getAllNameNodesDBMapped(Connection connection) throws Exception {
		ArrayList arr = new ArrayList();
		ArrayList removeList = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, QueryConstants.QRY_GET_ALL_NAMENODES_MAPPING);
			while (rs.next()) {
				String dbName = rs.getString(ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_DBNAME);
				String analyticsDb = rs.getString(ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_ANALYTICS_DBNAME);
				String nameNodeId = rs.getString(ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID);
				if (((dbName != null) && (!dbName.isEmpty())) || ((analyticsDb != null) && (!analyticsDb.isEmpty()))) {
					arr.add(nameNodeId);
				} else {
					removeList.add(nameNodeId);
				}
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(stmt);
		}

		try {
			for (int i = 0; i < removeList.size(); i++) {
				deleteNameNodeDBMapping(connection, String.valueOf(removeList.get(i)));
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception in deleting NameNode entries.", e);
		}
		return arr;
	}

	public static ArrayList getAllNameNodesMapping(Connection connection, boolean isMetadata) throws Exception {
		ArrayList arr = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			String col;
			if (isMetadata) {
				col = ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_DBNAME;
				rs = DatabaseFunctions.getQueryResultsForStatement(stmt,
						QueryConstants.QRY_GET_ALL_NAMENODES_DB_MAPPING);
			} else {
				col = ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_ANALYTICS_DBNAME;
				rs = DatabaseFunctions.getQueryResultsForStatement(stmt,
						QueryConstants.QRY_GET_ALL_NAMENODES_ANALYTICS_DB_MAPPING);
			}

			while (rs.next()) {
				String dbName = rs.getString(col);
				if ((dbName != null) && (!dbName.isEmpty()))
					arr.add(rs.getString(ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_NAMENODE_ID));
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(stmt);
		}
		return arr;
	}

	public static ArrayList getAllDBNamesMapping(Connection connection) throws Exception {
		ArrayList arr = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt, QueryConstants.QRY_GET_ALL_DBNAMES_MAPPING);
			while (rs.next()) {
				arr.add(rs.getString(ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_DBNAME));
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(stmt);
		}
		return arr;
	}

	public static ArrayList getAllAnalyticsDBNamesMapping(Connection connection) throws Exception {
		ArrayList arr = new ArrayList();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = DatabaseFunctions.getStatement(connection);
			rs = DatabaseFunctions.getQueryResultsForStatement(stmt,
					QueryConstants.QRY_GET_ALL_ANALYTICS_DBNAMES_MAPPING);

			while (rs.next()) {
				arr.add(rs.getString(ColumnConstants.COL_NAMENODE_CUSTOMDB_MAPPING_ANALYTICS_DBNAME));
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeStatement(stmt);
		}
		return arr;
	}

	public static void delete(Connection connection, Node node) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_NODE);
			ps.setString(1, node.getId());
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void updateHiveServiceStatus(Connection connection, Node node) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_UPDATE_HIVESERVICESTATUS_NODE);
			ps.setString(1, node.getHiveServiceStatus());
			ps.setString(2, node.getId());
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void updateServiceStatus(Connection connection, Node node) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_UPDATE_SERVICESTATUS_NODE);
			ps.setString(1, node.getServiceStatus());
			ps.setString(2, node.getId());
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void setNodeMonitor(Connection connection, String nodeId, boolean monitor) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_MONITOR_NODE);
			ps.setBoolean(1, monitor);
			ps.setString(2, nodeId);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void updateStatus(Connection connection, Node node) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_STATUS_NODE);
			ps.setString(1, node.getStatus());
			ps.setString(2, node.getId());
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void updateNodeType(Connection connection, Node node) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_NODETYPE_NODE);
			ps.setString(1, node.getNodeType());
			ps.setString(2, node.getId());
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static Node getNode(Connection connection, String nodeId) throws Exception {
		Node node = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_NODE);
			pst.setString(1, nodeId);
			rs = pst.executeQuery();
			if (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setServiceStatus(rs.getString(ColumnConstants.COL_NODE_SERVICESTATUS));
				node.setHiveServiceStatus(rs.getString(ColumnConstants.COL_NODE_HIVESERVICESTATUS));
				node.setMonitor(rs.getBoolean(ColumnConstants.COL_NODE_MONITOR));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return node;
	}

	// public static Node getNamenode(Connection connection) throws Exception{
	// ArrayList list = getAllNameNodes(connection);
	// for(int i = 0; i < list.size(); i++){
	// Node node = (Node)list.get(i);
	// if(HAStatusDAO.isActive(connection, node.getId()))
	// return node;
	// }
	// return null;
	// }
	//
	// public static Node getStandbyNamenode(Connection connection) throws
	// Exception{
	// ArrayList list = getAllNameNodes(connection);
	// for(int i = 0; i < list.size(); i++){
	// Node node = (Node)list.get(i);
	// if(!HAStatusDAO.isActive(connection, node.getId()))
	// return node;
	// }
	// return null;
	// }
	public static void updateJMXPort(Connection connection, String nodeId, String port) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_JMXPORT_NODE);
			ps.setString(1, port);
			ps.setString(2, nodeId);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static Node getNamenode(Connection connection, int hostId) throws Exception {
		ArrayList list = getAllNameNodes(connection);
		for (int i = 0; i < list.size(); i++) {
			Node node = (Node) list.get(i);
			if (node.getHostId() == hostId)
				return node;
		}
		return null;
	}

	public static ArrayList getAllCheckpointNodes(Connection connection) throws Exception {
		ArrayList nodes = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			nodes = new ArrayList();
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NODESFORTYPE);
			pst.setString(1, QueryIOConstants.SECONDARYNAMENODE);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setServiceStatus(rs.getString(ColumnConstants.COL_NODE_SERVICESTATUS));
				node.setHiveServiceStatus(rs.getString(ColumnConstants.COL_NODE_HIVESERVICESTATUS));
				nodes.add(node);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static ArrayList getAllJournalNodes(Connection connection) throws Exception {
		ArrayList nodes = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			nodes = new ArrayList();
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_ALL_NODESFORTYPE);
			pst.setString(1, QueryIOConstants.JOURNALNODE);
			rs = pst.executeQuery();
			Node node = null;
			while (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setServiceStatus(rs.getString(ColumnConstants.COL_NODE_SERVICESTATUS));
				node.setHiveServiceStatus(rs.getString(ColumnConstants.COL_NODE_HIVESERVICESTATUS));
				nodes.add(node);
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static void insertDecommissionNode(Connection connection, String datanodeId) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_INSERT_DECOMMISSIONNODES);
			ps.setString(1, datanodeId);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void deleteDecommissionNode(Connection connection, String datanodeId) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_DELETE_DECOMMISSIONNODES);
			ps.setString(1, datanodeId);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static ArrayList getAllDecomissioningDatanodes(Connection connection) throws Exception {
		ArrayList nodes = new ArrayList();
		Statement st = null;
		ResultSet rs = null;
		try {
			st = DatabaseFunctions.getStatement(connection);
			rs = st.executeQuery(QueryConstants.QRY_GET_ALL_DECOMISSIONED);
			Node node = null;
			while (rs.next()) {
				node = new Node();
				node.setId(rs.getString(ColumnConstants.COL_NODE_ID));
				node.setHostId(rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				node.setNodeType(rs.getString(ColumnConstants.COL_NODE_NODETYPE));
				node.setStatus(rs.getString(ColumnConstants.COL_NODE_STATUS));
				node.setJmxPort(rs.getString(ColumnConstants.COL_NODE_JMXPORT));
				node.setMonitor(rs.getBoolean(ColumnConstants.COL_NODE_MONITOR));
				nodes.add(node);
			}
		} finally {
			DatabaseFunctions.closeStatement(st);
			DatabaseFunctions.closeResultSet(rs);
		}
		return nodes;
	}

	public static boolean isNodeDecommission(Connection connection, String datanodeId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_IS_NODE_DECOMMISSION);
			pst.setString(1, datanodeId);
			rs = pst.executeQuery();
			if (rs.next()) {
				return true;
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(pst);
			DatabaseFunctions.closeResultSet(rs);
		}
		return false;
	}
}