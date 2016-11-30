package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.core.bean.Host;

public class HostDAO {
	public static ArrayList getAllHostDetails(final Connection connection) throws Exception {
		ArrayList hosts = new ArrayList();
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_ALL_HOSTSINFO);

			Host host = null;

			while (rs.next()) {
				host = new Host();
				host.setId(rs.getInt(ColumnConstants.COL_HOST_ID));
				host.setHostIP(rs.getString(ColumnConstants.COL_HOST_IP));
				host.setInstallDirPath(rs.getString(ColumnConstants.COL_HOST_INSTALLDIR));
				host.setStatus(rs.getString(ColumnConstants.COL_HOST_STATUS));
				host.setRackName(rs.getString(ColumnConstants.COL_HOST_RACKNAME));
				host.setAgentPort(rs.getString(ColumnConstants.COL_HOST_AGENTPORT));
				host.setMonitor(rs.getBoolean(ColumnConstants.COL_HOST_MONITOR));
				hosts.add(host);
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(statement, rs);
		}

		return hosts;
	}

	public static boolean isHostAlreadyAdded(final Connection connection, final String hostName) throws Exception {
		boolean added = false;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_SEARCH_HOST);
			ps.setString(1, hostName);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			if (rs.next()) {
				added = true;
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}

		return added;
	}

	public static Host getHostDetail(final Connection connection, int hostID) throws Exception {
		Host host = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_HOST_ID);
			ps.setInt(1, hostID);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				host = new Host();
				host.setId(rs.getInt(ColumnConstants.COL_HOST_ID));
				host.setHostIP(rs.getString(ColumnConstants.COL_HOST_IP));
				host.setInstallDirPath(rs.getString(ColumnConstants.COL_HOST_INSTALLDIR));
				host.setStatus(rs.getString(ColumnConstants.COL_HOST_STATUS));
				host.setRackName(rs.getString(ColumnConstants.COL_HOST_RACKNAME));
				host.setAgentPort(rs.getString(ColumnConstants.COL_HOST_AGENTPORT));
				host.setMonitor(rs.getBoolean(ColumnConstants.COL_HOST_MONITOR));
				host.setWindows(rs.getBoolean(ColumnConstants.COL_HOST_IS_WINDOWS));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return host;
	}

	public static Host getHostDetail(final Connection connection, String hostIP) throws Exception {
		Host host = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_HOST_IP);
			ps.setString(1, hostIP);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				host = new Host();
				host.setId(rs.getInt(ColumnConstants.COL_HOST_ID));
				host.setHostIP(rs.getString(ColumnConstants.COL_HOST_IP));
				host.setInstallDirPath(rs.getString(ColumnConstants.COL_HOST_INSTALLDIR));
				host.setStatus(rs.getString(ColumnConstants.COL_HOST_STATUS));
				host.setRackName(rs.getString(ColumnConstants.COL_HOST_RACKNAME));
				host.setAgentPort(rs.getString(ColumnConstants.COL_HOST_AGENTPORT));
				host.setMonitor(rs.getBoolean(ColumnConstants.COL_HOST_MONITOR));
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
			DatabaseFunctions.closeResultSet(rs);
		}

		return host;
	}

	public static void deleteHost(Connection connection, String hostIP) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_HOST);
			ps.setString(1, hostIP);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static ArrayList getNameNodeHosts(Connection connection) throws Exception {
		ArrayList hosts = new ArrayList();
		Host host = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_NAMENODE_HOST);

			while (rs.next()) {
				host = new Host();
				host.setId(rs.getInt(ColumnConstants.COL_HOST_ID));
				host.setHostIP(rs.getString(ColumnConstants.COL_HOST_IP));
				host.setInstallDirPath(rs.getString(ColumnConstants.COL_HOST_INSTALLDIR));
				host.setStatus(rs.getString(ColumnConstants.COL_HOST_STATUS));
				host.setRackName(rs.getString(ColumnConstants.COL_HOST_RACKNAME));
				host.setAgentPort(rs.getString(ColumnConstants.COL_HOST_AGENTPORT));
				host.setMonitor(rs.getBoolean(ColumnConstants.COL_HOST_MONITOR));
				hosts.add(host);
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(statement, rs);
		}

		return hosts;
	}

	public static ArrayList getNameNodeHostNames(Connection connection) throws Exception {
		ArrayList hostnames = new ArrayList();
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_NAMENODE_HOST);

			while (rs.next()) {
				hostnames.add(rs.getString(ColumnConstants.COL_HOST_IP));
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(statement, rs);
		}

		return hostnames;
	}

	public static ArrayList getAllNameNodeHostNames(Connection connection) throws Exception {
		ArrayList hostnames = new ArrayList();
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_ALL_NAMENODES);

			while (rs.next()) {
				Host host = HostDAO.getHostDetail(connection, rs.getInt(ColumnConstants.COL_NODE_HOSTID));
				hostnames.add(host.getHostIP());
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(statement, rs);
		}

		return hostnames;
	}

	public static ArrayList getDataNodeHosts(Connection connection) throws Exception {
		ArrayList hosts = new ArrayList();
		Host host = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_DATANODE);

			while (rs.next()) {
				host = new Host();
				host.setId(rs.getInt(ColumnConstants.COL_HOST_ID));
				host.setHostIP(rs.getString(ColumnConstants.COL_HOST_IP));
				host.setInstallDirPath(rs.getString(ColumnConstants.COL_HOST_INSTALLDIR));
				host.setStatus(rs.getString(ColumnConstants.COL_HOST_STATUS));
				host.setRackName(rs.getString(ColumnConstants.COL_HOST_RACKNAME));
				host.setAgentPort(rs.getString(ColumnConstants.COL_HOST_AGENTPORT));
				host.setMonitor(rs.getBoolean(ColumnConstants.COL_HOST_MONITOR));
				hosts.add(host);
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(statement, rs);
		}

		return hosts;
	}

	public static ArrayList getDataNodeHostNames(Connection connection) throws Exception {
		ArrayList hostnames = new ArrayList();
		Host host = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_DATANODE);

			while (rs.next()) {
				hostnames.add(rs.getString(ColumnConstants.COL_HOST_IP));
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(statement, rs);
		}

		return hostnames;
	}

	public static ArrayList getAllHostNames(final Connection connection) throws Exception {
		ArrayList hostNames = new ArrayList();
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_ALL_HOSTNAMES);

			while (rs.next()) {
				hostNames.add(rs.getString(1));
			}
		} finally {
			DatabaseFunctions.closeSQLObjects(statement, rs);
		}

		return hostNames;
	}

	public static void insertHost(Connection connection, Host host) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_INSERT_HOST);

			ps.setString(1, host.getHostIP());
			ps.setString(2, host.getInstallDirPath());
			ps.setString(3, QueryIOConstants.STATUS_STOPPED);
			ps.setString(4, host.getRackName());
			ps.setString(5, host.getAgentPort());
			ps.setBoolean(6, host.isMonitor());
			ps.setBoolean(7, host.isWindows());
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void updateStatus(Connection connection, Host host) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_STATUS_HOST);
			ps.setString(1, host.getStatus());
			ps.setInt(2, host.getId());
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static void setHostMonitor(Connection connection, int hostId, boolean monitor) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_MONITOR_HOST);
			ps.setBoolean(1, monitor);
			ps.setInt(2, hostId);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static ArrayList getHosts(Connection connection, ArrayList hostIds) throws Exception {
		ArrayList hosts = new ArrayList();
		Host host = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_HOST_ID);
			for (int i = 0; i < hostIds.size(); i++) {
				ps.setInt(1, (Integer) hostIds.get(i));
				rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

				if (rs.next()) {
					host = new Host();
					host.setId(rs.getInt(ColumnConstants.COL_HOST_ID));
					host.setHostIP(rs.getString(ColumnConstants.COL_HOST_IP));
					host.setInstallDirPath(rs.getString(ColumnConstants.COL_HOST_INSTALLDIR));
					host.setStatus(rs.getString(ColumnConstants.COL_HOST_STATUS));
					host.setAgentPort(rs.getString(ColumnConstants.COL_HOST_AGENTPORT));
					host.setMonitor(rs.getBoolean(ColumnConstants.COL_HOST_MONITOR));
					hosts.add(host);
				}
			}
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}

		return hosts;
	}

	public static ArrayList getAllHostforRack(final Connection connection, String rackid) throws Exception {
		Host host = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList hosts = new ArrayList();

		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_GET_HOST_RACKNAME);
			ps.setString(1, rackid);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			while (rs.next()) {
				host = new Host();
				host.setId(rs.getInt(ColumnConstants.COL_HOST_ID));
				host.setHostIP(rs.getString(ColumnConstants.COL_HOST_IP));
				host.setInstallDirPath(rs.getString(ColumnConstants.COL_HOST_INSTALLDIR));
				host.setStatus(rs.getString(ColumnConstants.COL_HOST_STATUS));
				host.setRackName(rs.getString(ColumnConstants.COL_HOST_RACKNAME));
				host.setMonitor(rs.getBoolean(ColumnConstants.COL_HOST_MONITOR));
				hosts.add(host);
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}

		return hosts;
	}

	public static void updateRackDetail(Connection connection, int hostId, String rackName) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_UPDATE_RACKDETAIL);

			ps.setString(1, rackName);
			ps.setInt(2, hostId);
			CoreDBManager.executeUpdateStatement(connection, ps);
		} finally {
			DatabaseFunctions.closePreparedStatement(ps);
		}
	}

	public static HashMap getHostIds(ArrayList<String> hosts, Connection connection) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		HashMap map = new HashMap();
		try {
			for (String host : hosts) {
				ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.QRY_GET_HOST_ID);
				ps.setString(1, host);
				rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

				if (rs.next()) {
					map.put(host, rs.getInt(ColumnConstants.COL_HOST_ID));
				}
			}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closePreparedStatement(ps);
		}

		return map;
	}
}