package com.queryio.core.monitor.managers;

import java.sql.Connection;
import java.util.ArrayList;

import javax.swing.table.TableModel;

import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.MonitorDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.monitor.beans.Parameter;
import com.queryio.core.monitor.beans.SummaryTable;

public class SummaryManager {
	public static ArrayList getClusterMemoryInfo() {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getClusterMemoryInfo(connection, NodeDAO.getAllNameNodes(connection));
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getClusterMemoryInfo() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getNameNodeMemoryInfo(String nodeId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getNameNodeMemoryInfo(connection, nodeId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getNameNodeMemoryInfo() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getNameNodeReadWritesIntervalBased(String nodeId, String interval) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getNameNodeReadWrites(connection, nodeId, interval);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getNameNodeReadWritesIntervalBased() failed with exception: " + e.getMessage(),
					e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getNameNodeReadWrites(String nodeId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getNameNodeReadWrites(connection, nodeId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getNameNodeReadWrites() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getDataNodeReadWrites(String nodeId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getDataNodeReadWrites(connection, nodeId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getDataNodeReadWrites() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllNameNodeReadWrites() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getNameNodeReadWrites(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllNameNodeReadWrites() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllDataNodeReadWrites() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getDataNodeReadWrites(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllDataNodeReadWrites() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getDataNodeMemoryInfo(String nodeId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getDataNodeMemoryInfo(connection, nodeId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getDataNodeMemoryInfo() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllDataNodeMemoryInfo() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getAllDataNodeMemoryInfo(connection, NodeDAO.getAllDatanodes(connection));
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getDataNodeMemoryInfo() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllDataNodeMemoryInfoForRack(String rackId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList hostsForRack = HostDAO.getAllHostforRack(connection, rackId);

			ArrayList dataNodeForHost = new ArrayList();

			for (Object object : hostsForRack) {
				int hostId = ((Host) object).getId();
				ArrayList nodes = NodeDAO.getAllDataNodesForHost(connection, hostId);
				for (Object node : nodes) {
					dataNodeForHost.add(node);
				}
			}

			return MonitorDAO.getAllDataNodeMemoryInfo(connection, dataNodeForHost);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllDataNodeMemoryInfoForRack() failed with exception: " + e.getMessage(),
					e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllDataNodeMemoryInfoForHost(String hostIP) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, hostIP);
			return MonitorDAO.getAllDataNodeMemoryInfo(connection,
					NodeDAO.getAllDataNodesForHost(connection, host.getId()));
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllDataNodeMemoryInfoForHost() failed with exception: " + e.getMessage(),
					e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	// public static ArrayList getClusterSummary()
	// {
	// Connection connection = null;
	// try
	// {
	// connection = CoreDBManager.getQueryIODBConnection();
	//
	// return MonitorDAO.getClusterSummaryData(connection,
	// TableConstants.TABLE_NAMENODE_LIVE_ATTRIBUTES,
	// NodeDAO.getAllNameNodes(connection));
	// }
	// catch (Exception e)
	// {
	// AppLogger.getLogger().fatal("getClusterSummary() failed with exception: "
	// + e.getMessage(), e);
	// }
	// finally
	// {
	// try
	// {
	// CoreDBManager.closeConnection(connection);
	// }
	// catch(Exception e)
	// {
	// AppLogger.getLogger().fatal("Error closing database connection.", e);
	// }
	// }
	//
	// return null;
	// }
	//
	public static SummaryTable getAllNameNodesSummaryTable(boolean isSummaryForReport) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getAllNameNodesSummaryTable(connection, TableConstants.TABLE_NAMENODE_LIVE_ATTRIBUTES,
					NodeDAO.getAllNameNodes(connection), isSummaryForReport);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllNameNodesSummaryTable() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static SummaryTable getAllDataNodesSummaryTable(boolean isSummaryForReport) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getAllDataNodesSummaryTable(connection, TableConstants.TABLE_DATANODE_LIVE_ATTRIBUTES,
					NodeDAO.getAllDatanodes(connection), isSummaryForReport);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllDataNodesSummaryTable() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static SummaryTable getAllDataNodesSummaryTableforRack(String rackid, boolean isSummaryForReport) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList hostsForRack = HostDAO.getAllHostforRack(connection, rackid);
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("hostsForRack ID :" + hostsForRack.size());
			ArrayList dataNodeForHost = new ArrayList();

			for (Object object : hostsForRack) {
				int hostId = ((Host) object).getId();
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Host ID :" + hostId);
				ArrayList nodes = NodeDAO.getAllDataNodesForHost(connection, hostId);

				for (Object node : nodes) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Node ID :" + ((Node) node).getId());
					dataNodeForHost.add(node);
				}
			}

			return MonitorDAO.getAllDataNodesSummaryTable(connection, TableConstants.TABLE_DATANODE_LIVE_ATTRIBUTES,
					dataNodeForHost, isSummaryForReport);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllDataNodesSummaryTableforRack() failed with exception: " + e.getMessage(),
					e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static SummaryTable getAllDataNodesSummaryTableforHost(String hostIP, boolean isSummaryForReport) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList dataNodeForHost = new ArrayList();

			Host host = HostDAO.getHostDetail(connection, hostIP);
			ArrayList nodes = NodeDAO.getAllDataNodesForHost(connection, host.getId());
			for (Object node : nodes) {
				dataNodeForHost.add(node);
			}

			return MonitorDAO.getAllDataNodesSummaryTable(connection, TableConstants.TABLE_DATANODE_LIVE_ATTRIBUTES,
					dataNodeForHost, isSummaryForReport);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllDataNodesSummaryTableforHost() failed with exception: " + e.getMessage(),
					e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getNameNodeSummary(String nodeId, String interval) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getNameNodeSummaryData(connection, nodeId, interval);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getNameNodeSummary failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllNameNodeStatusSummary() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getNameNodeSummaryData(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllNameNodeStatusSummary failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getDataNodeSummary(String nodeId, String interval) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getDataNodeSummaryData(connection, nodeId, interval);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getDataNodeSummary failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllDataNodeStatusSummary() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getDataNodeSummaryData(connection, NodeDAO.getAllDatanodes(connection));
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllDataNodeStatusSummary failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllDataNodeStatusSummaryforRack(String rackid) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList hostsForRack = HostDAO.getAllHostforRack(connection, rackid);

			ArrayList dataNodeForHost = new ArrayList();

			for (Object object : hostsForRack) {
				int hostId = ((Host) object).getId();
				ArrayList nodes = NodeDAO.getAllDataNodesForHost(connection, hostId);
				for (Object node : nodes) {
					dataNodeForHost.add(node);
				}
			}

			return MonitorDAO.getDataNodeSummaryData(connection, dataNodeForHost);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllDataNodeStatusSummaryforRack failed with exception: " + e.getMessage(),
					e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllDataNodeStatusSummaryforHost(String hostIP) {
		Connection connection = null;
		try {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("HostId :" + hostIP);
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, hostIP);
			return MonitorDAO.getDataNodeSummaryData(connection,
					NodeDAO.getAllDataNodesForHost(connection, host.getId()));
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllDataNodeStatusSummaryforHost failed with exception: " + e.getMessage(),
					e);
			return null;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

	}

	public static TableModel getStorageForecastReportTableModel() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getStorageForecastTableModel(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getStorageForecastReportTableModel failed with exception: " + e.getMessage(),
					e);
			return null;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

	}

	public static long getNameNodeTotalValueForColumn(String nodeId, String colName) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getNameNodeTotalValueForColumn(connection, nodeId, colName);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getNameNodeTotalValueForColumn failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return 0;
	}

	public static long getDataNodeTotalValueForColumn(String nodeId, String colName) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getDataNodeTotalValueForColumn(connection, nodeId, colName);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getDataNodeTotalValueForColumn failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return 0;
	}

	public static long getNameNodeTotalFilesWritten(String nodeId) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return MonitorDAO.getNameNodeTotalFilesWritten(connection, nodeId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getNameNodeTotalFilesWritten failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return 0;
	}

	public static SummaryTable getAllResourceManagersSummaryTableForHost(String hostIP) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList resourceManagersForHost = new ArrayList();

			Host host = HostDAO.getHostDetail(connection, hostIP);
			ArrayList nodes = NodeDAO.getAllResourceManagersForHost(connection, host.getId());
			for (Object node : nodes) {
				resourceManagersForHost.add(node);
			}

			return MonitorDAO.getAllResourceManagersSummaryTable(connection,
					TableConstants.TABLE_RESOURCE_MANAGER_LIVE_ATTRIBUTES, resourceManagersForHost, false);
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("getAllResourceManagersSummaryTableForHost() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static SummaryTable getAllResourceManagersSummaryTable(boolean isSummaryForReport) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList resourceManagersForHost = new ArrayList();

			ArrayList hostList = HostDAO.getAllHostDetails(connection);
			for (int i = 0; i < hostList.size(); i++) {
				ArrayList nodes = NodeDAO.getAllResourceManagersForHost(connection, ((Host) hostList.get(i)).getId());
				for (Object node : nodes) {
					resourceManagersForHost.add(node);
				}
			}

			return MonitorDAO.getAllResourceManagersSummaryTable(connection,
					TableConstants.TABLE_RESOURCE_MANAGER_LIVE_ATTRIBUTES, resourceManagersForHost, isSummaryForReport);
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("getAllResourceManagersSummaryTableforHost() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static SummaryTable getAllNodeManagersSummaryTableForHost(String hostIP) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList nodeManagersForHost = new ArrayList();

			Host host = HostDAO.getHostDetail(connection, hostIP);
			ArrayList nodes = NodeDAO.getAllNodeManagersForHost(connection, host.getId());
			for (Object node : nodes) {
				nodeManagersForHost.add(node);
			}

			return MonitorDAO.getAllNodeManagersSummaryTable(connection,
					TableConstants.TABLE_NODE_MANAGER_LIVE_ATTRIBUES, nodeManagersForHost, false);
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("getAllNodeManagersSummaryTableForHost() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static SummaryTable getAllNodeManagersSummaryTable(boolean isSummaryForReport) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList nodeManagersForHost = new ArrayList();

			ArrayList hostList = HostDAO.getAllHostDetails(connection);
			for (int i = 0; i < hostList.size(); i++) {
				ArrayList nodes = NodeDAO.getAllNodeManagersForHost(connection, ((Host) hostList.get(i)).getId());
				for (Object node : nodes) {
					nodeManagersForHost.add(node);
				}
			}

			return MonitorDAO.getAllNodeManagersSummaryTable(connection,
					TableConstants.TABLE_NODE_MANAGER_LIVE_ATTRIBUES, nodeManagersForHost, isSummaryForReport);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllNodeManagersSummaryTable() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getResourceManagerAppsDetailStatusSummary() {
		ArrayList list = new ArrayList();
		try {
			ArrayList appData = getResourceManagerAppsDetail();
			if ((appData != null) && (appData.size() > 0)) {
				int total = 0;
				for (int i = 0; i < appData.size(); i++) {
					total += (Integer) appData.get(i);
				}
				list.add(new Parameter("Total Apps Submitted", total));
				list.add(new Parameter("Total Apps Running", appData.get(0)));
				list.add(new Parameter("Total Apps Pending", appData.get(1)));
				list.add(new Parameter("Total Apps Completed", appData.get(2)));
				list.add(new Parameter("Total Apps Killed", appData.get(3)));
			}
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("getResourceManagerAppsDetailStatusSummary() failed with exception: " + e.getMessage(), e);
		}

		return list;
	}

	public static ArrayList getResourceManagerAppsDetail() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList resourceManagersIds = new ArrayList();

			ArrayList hostList = HostDAO.getAllHostDetails(connection);
			for (int i = 0; i < hostList.size(); i++) {
				ArrayList nodes = NodeDAO.getAllResourceManagersForHost(connection, ((Host) hostList.get(i)).getId());
				for (int j = 0; j < nodes.size(); j++) {
					Node node = (Node) nodes.get(j);
					resourceManagersIds.add(node.getId());
				}
			}
			return MonitorDAO.getAllResourceManagerAppDetails(connection, resourceManagersIds);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("getResourceManagerAppsDetail() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getResourceManagerAppsDetailStatusSummaryForId(String id) {
		ArrayList list = new ArrayList();
		try {
			ArrayList appData = getResourceManagerAppsDetailForId(id);
			if ((appData != null) && (appData.size() > 0)) {
				int total = 0;
				for (int i = 0; i < appData.size(); i++) {
					total += (Integer) appData.get(i);
				}
				list.add(new Parameter("Total Apps Submitted", total));
				list.add(new Parameter("Total Apps Running", appData.get(0)));
				list.add(new Parameter("Total Apps Pending", appData.get(1)));
				list.add(new Parameter("Total Apps Completed", appData.get(2)));
				list.add(new Parameter("Total Apps Killed", appData.get(3)));
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(
					"getResourceManagerAppsDetailStatusSummaryForId() failed with exception: " + e.getMessage(), e);
		}

		return list;
	}

	public static ArrayList getResourceManagerAppsDetailForId(String id) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList resourceManagersIds = new ArrayList();
			resourceManagersIds.add(id);
			return MonitorDAO.getAllResourceManagerAppDetails(connection, resourceManagersIds);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("getResourceManagerAppsDetail() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getResourceManagerAppsDetailForIp(String hostname) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList resourceManagersIds = new ArrayList();
			Host host = HostDAO.getHostDetail(connection, hostname);
			ArrayList nodes = NodeDAO.getAllResourceManagersForHost(connection, host.getId());
			for (int j = 0; j < nodes.size(); j++) {
				Node node = (Node) nodes.get(j);
				resourceManagersIds.add(node.getId());
			}
			return MonitorDAO.getAllResourceManagerAppDetails(connection, resourceManagersIds);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("getResourceManagerAppsDetail() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getNodeManagerAppsDetailStatusSummary() {
		ArrayList list = new ArrayList();
		try {
			ArrayList appData = getNodeManagerAppsDetail();
			if ((appData != null) && (appData.size() > 0)) {
				list.add(new Parameter("Total Containers Launched", appData.get(0)));
				list.add(new Parameter("Total Containers Completed", appData.get(1)));
				list.add(new Parameter("Total Containers Failed", appData.get(2)));
				list.add(new Parameter("Total Containers Killed", appData.get(3)));
				list.add(new Parameter("Total Containers Running", appData.get(4)));
				list.add(new Parameter("Total Containers Initing", appData.get(5)));
			}
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("getNodeManagerAppsDetailStatusSummary() failed with exception: " + e.getMessage(), e);
		}

		return list;
	}

	public static ArrayList getNodeManagerAppsDetail() {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList resourceManagersIds = new ArrayList();

			ArrayList hostList = HostDAO.getAllHostDetails(connection);
			for (int i = 0; i < hostList.size(); i++) {
				ArrayList nodes = NodeDAO.getAllNodeManagersForHost(connection, ((Host) hostList.get(i)).getId());
				for (int j = 0; j < nodes.size(); j++) {
					Node node = (Node) nodes.get(j);
					resourceManagersIds.add(node.getId());
				}
			}
			return MonitorDAO.getAllNodeManagerAppDetails(connection, resourceManagersIds);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("getNodeManagerAppsDetail() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getNodeManagerAppsDetailStatusSummaryForId(String id) {
		ArrayList list = new ArrayList();
		try {
			ArrayList appData = getNodeManagerAppsDetailForId(id);
			if ((appData != null) && (appData.size() > 0)) {
				list.add(new Parameter("Total Containers Launched", appData.get(0)));
				list.add(new Parameter("Total Containers Completed", appData.get(1)));
				list.add(new Parameter("Total Containers Failed", appData.get(2)));
				list.add(new Parameter("Total Containers Killed", appData.get(3)));
				list.add(new Parameter("Total Containers Running", appData.get(4)));
				list.add(new Parameter("Total Containers Initing", appData.get(5)));
			}
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("getNodeManagerAppsDetailStatusSummaryForId() failed with exception: " + e.getMessage(), e);
		}

		return list;
	}

	public static ArrayList getNodeManagerAppsDetailForId(String id) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList resourceManagersIds = new ArrayList();
			resourceManagersIds.add(id);
			return MonitorDAO.getAllNodeManagerAppDetails(connection, resourceManagersIds);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("getResourceManagerAppsDetail() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static ArrayList getNodeManagerAppsDetailForIp(String hostname) {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			ArrayList resourceManagersIds = new ArrayList();
			Host host = HostDAO.getHostDetail(connection, hostname);
			ArrayList nodes = NodeDAO.getAllNodeManagersForHost(connection, host.getId());
			for (int j = 0; j < nodes.size(); j++) {
				Node node = (Node) nodes.get(j);
				resourceManagersIds.add(node.getId());
			}
			return MonitorDAO.getAllNodeManagerAppDetails(connection, resourceManagersIds);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("getResourceManagerAppsDetail() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

	public static SummaryTable getSummaryTableForNameNodeBasedOnIP(String ipStr) {
		Connection connection = null;
		Host host = null;
		ArrayList namenodeList = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			host = HostDAO.getHostDetail(connection, ipStr);
			namenodeList = NodeDAO.getAllNameNodesForHost(connection, host.getId());
			return MonitorDAO.getNameNodeSummaryDataBasedOnList(connection, namenodeList);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getNameNodeSummary failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getNameNodeStatusSummaryBasedOnIP(String ipStr) {
		Connection connection = null;
		Host host = null;
		ArrayList namenodeList = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			host = HostDAO.getHostDetail(connection, ipStr);
			namenodeList = NodeDAO.getAllNameNodesForHost(connection, host.getId());
			return MonitorDAO.getNameNodeSummaryDataBasedOnNNList(connection, namenodeList);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getNameNodeSummary failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAllNameNodeReadWritesBasedOnIP(String ipStr) {
		Connection connection = null;
		Host host = null;
		ArrayList namenodeList = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			host = HostDAO.getHostDetail(connection, ipStr);
			namenodeList = NodeDAO.getAllNameNodesForHost(connection, host.getId());
			return MonitorDAO.getNameNodeReadWritesBasedOnNNList(connection, namenodeList);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAllNameNodeReadWrites() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return null;
	}

}
