package com.queryio.core.monitor.controllers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.bean.User;
import com.queryio.core.consolidate.HostConsolidationThread;
import com.queryio.core.consolidate.NodeConsolidationThread;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.MonitorDAO;
import com.queryio.core.dao.NotifyDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.monitor.beans.Parameter;
import com.queryio.core.notification.NotificationHandler;
import com.queryio.core.notifier.notifiers.NotificationManager;

public class ControllerManager {
	/* constants denoting the Unknown state of the controller */
	public static final String CONTROLLER_STATE_UNKNOWN = "Unknown";

	/* constants denoting the Ok state of the controller */
	public static final String CONTROLLER_STATE_OK = "Ok";

	/* constants denoting the suspended state of the controller */
	public static final String CONTROLLER_STATE_SUSPENDED = "Suspended";

	/* constants denoting the polling state of the controller */
	public static final String CONTROLLER_STATE_POLLING = "Polling";

	/* constants denoting the Warning state of the controller */
	public static final String CONTROLLER_STATE_WARNING = "Warning";

	/* constants denoting the Error state of the controller */
	public static final String CONTROLLER_STATE_ERROR = "Error";

	private static HashMap controllerMap = new HashMap();
	private static HashMap consolidationThreadMap = new HashMap();

	private static HashMap hostMonitorMap = new HashMap();
	private static HashMap queryIoServicesMonitorMap = new HashMap();
	private static HashMap hiveServicesMonitorMap = new HashMap();

	private static Hashtable htControllers = null;
	private static Hashtable htBriefControllerInformation = null;

	private static Thread nodeStatusConsolidator = null;

	public static void setAllControllersDataFetchInterval(int dataFetchInterval) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("setAllControllersDataFetchInterval() : " + dataFetchInterval);

		if (controllerMap.size() != 0) {
			final Iterator itr = controllerMap.keySet().iterator();
			String controllerId = null;
			while (itr.hasNext()) {
				controllerId = (String) itr.next();
				BaseController controller = (BaseController) controllerMap.get(controllerId);
				if (controller != null)
					controller.setDataFetchInterval(dataFetchInterval);

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Updating data fetch interval for controller: " + controllerId);
			}
		}
	}

	public static void startNameNodeController(final Connection connection, final Host host, Node node,
			int dataFetchInterval) throws Exception {
		AbstractJMXController controller = new JMXMonitorController();

		ArrayList alControllerHistoricalAttributes = MonitorDAO.getNameNodeControllerHistoricalAttributes(connection,
				node.getId());
		ArrayList alControllerSummaryAttributes = MonitorDAO.getNameNodeControllerSummaryAttributes(connection,
				node.getId());

		node.setNodeType(QueryIOConstants.NAMENODE);
		controller.setNodeId(node.getId());
		controller.setNodeType(QueryIOConstants.NAMENODE);
		controller.setHost(host);
		controller.setConnectorPort(Integer.parseInt(node.getJmxPort()));
		controller.setDataFetchInterval(dataFetchInterval);

		controller.setMonitoredHistoricalAttributes(alControllerHistoricalAttributes);
		controller.setMonitoredSummaryAttributes(alControllerSummaryAttributes);

		startControllerInThread(controller, host, node);

		String uid = host.getHostIP() + "namenode" + node.getId();
		if (!controllerMap.containsKey(uid)) {
			controllerMap.put(uid, controller);
		}
	}

	public static void startDataNodeController(final Connection connection, final Host host, Node node,
			int dataFetchInterval) throws Exception {
		AbstractJMXController controller = new JMXMonitorController();

		ArrayList alControllerHistoricalAttributes = MonitorDAO.getDataNodeControllerHistoricalAttributes(connection,
				node.getId());
		ArrayList alControllerSummaryAttributes = MonitorDAO.getDataNodeControllerSummaryAttributes(connection,
				node.getId());

		controller.setNodeId(node.getId());
		controller.setNodeType(QueryIOConstants.DATANODE);
		controller.setHost(host);
		controller.setConnectorPort(Integer.parseInt(node.getJmxPort()));
		controller.setDataFetchInterval(dataFetchInterval);

		controller.setMonitoredHistoricalAttributes(alControllerHistoricalAttributes);
		controller.setMonitoredSummaryAttributes(alControllerSummaryAttributes);

		startControllerInThread(controller, host, node);

		String uid = host.getHostIP() + "datanode" + node.getId();
		if (!controllerMap.containsKey(uid)) {
			controllerMap.put(uid, controller);
		}
	}

	public static void startResourceManagerController(final Connection connection, final Host host, Node node,
			int dataFetchInterval) throws Exception {
		AbstractJMXController controller = new JMXMonitorController();

		ArrayList alControllerHistoricalAttributes = MonitorDAO
				.getResourceManagerControllerHistoricalAttributes(connection, node.getId());
		ArrayList alControllerSummaryAttributes = MonitorDAO.getResourceManagerControllerSummaryAttributes(connection,
				node.getId());

		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("alControllerHistoricalAttributes: " +
		// alControllerHistoricalAttributes);
		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("alControllerSummaryAttributes: " +
		// alControllerSummaryAttributes);

		node.setNodeType(QueryIOConstants.RESOURCEMANAGER);
		controller.setNodeId(node.getId());
		controller.setNodeType(QueryIOConstants.RESOURCEMANAGER);
		controller.setHost(host);
		controller.setConnectorPort(Integer.parseInt(node.getJmxPort()));
		controller.setDataFetchInterval(dataFetchInterval);

		controller.setMonitoredHistoricalAttributes(alControllerHistoricalAttributes);
		controller.setMonitoredSummaryAttributes(alControllerSummaryAttributes);

		startControllerInThread(controller, host, node);

		String uid = host.getHostIP() + "resourcemanager" + node.getId();
		if (!controllerMap.containsKey(uid)) {
			controllerMap.put(uid, controller);
		}
	}

	public static void startNodeManagerController(final Connection connection, final Host host, Node node,
			int dataFetchInterval) throws Exception {
		AbstractJMXController controller = new JMXMonitorController();

		ArrayList alControllerHistoricalAttributes = MonitorDAO.getNodeManagerControllerHistoricalAttributes(connection,
				node.getId());
		ArrayList alControllerSummaryAttributes = MonitorDAO.getNodeManagerControllerSummaryAttributes(connection,
				node.getId());

		node.setNodeType(QueryIOConstants.NODEMANAGER);
		controller.setNodeId(node.getId());
		controller.setNodeType(QueryIOConstants.NODEMANAGER);
		controller.setHost(host);
		controller.setConnectorPort(Integer.parseInt(node.getJmxPort()));
		controller.setDataFetchInterval(dataFetchInterval);

		controller.setMonitoredHistoricalAttributes(alControllerHistoricalAttributes);
		controller.setMonitoredSummaryAttributes(alControllerSummaryAttributes);

		startControllerInThread(controller, host, node);

		String uid = host.getHostIP() + "nodemanager" + node.getId();
		if (!controllerMap.containsKey(uid)) {
			controllerMap.put(uid, controller);
		}
	}

	public static void startHostMonitorController(final Connection connection, final Host host) throws Exception {
		HostMonitorController dm = new HostMonitorController();
		dm.setHost(host);
		dm.startCollectingData();

		hostMonitorMap.put(host.getId(), dm);

		HostConsolidationThread consolidationThread = new HostConsolidationThread(host);
		consolidationThreadMap.put(host.getId(), consolidationThread);
		consolidationThread.start();
	}

	public static void startHostMonitorController(final Connection connection, final Host host, final String username,
			final String password) throws Exception {
		HostMonitorController dm = new HostMonitorController();
		dm.setHost(host);
		dm.setUserName(username);
		dm.setPassword(password);
		dm.startCollectingData();

		hostMonitorMap.put(host.getId(), dm);

		HostConsolidationThread consolidationThread = new HostConsolidationThread(host);
		consolidationThreadMap.put(host.getId(), consolidationThread);
		consolidationThread.start();
	}

	public static void startQueryIOServiceController(final String nodeId) throws Exception {
		QueryIOServicesController controller = new QueryIOServicesController(nodeId);
		controller.start();
		queryIoServicesMonitorMap.put(nodeId + QueryIOServicesController.class, controller);
	}

	public static void startHiveServiceController(final String nodeId) throws Exception {
		HiveServicesController controller = new HiveServicesController(nodeId);
		controller.start();
		hiveServicesMonitorMap.put(nodeId + HiveServicesController.class, controller);
	}

	private static void startControllerInThread(final BaseController baseController, Host host, Node node) {
		new Thread(baseController, node.getId()).start();

		NodeConsolidationThread consolidationThread = new NodeConsolidationThread(node);
		consolidationThreadMap.put(node.getId(), consolidationThread);
		consolidationThread.start();
	}

	public static void stopNameNodeController(final String hostname, Node node) {
		String uid = hostname + "namenode" + node.getId();
		AbstractJMXController controller = (AbstractJMXController) controllerMap.get(uid);
		if (controller != null)
			controller.stopCollectingData();

		NodeConsolidationThread consolidationThread = (NodeConsolidationThread) consolidationThreadMap
				.get(node.getId());
		if (consolidationThread != null)
			consolidationThread.interrupt();

		controllerMap.remove(uid);
	}

	public static void stopDataNodeController(final String hostname, Node node) {
		String uid = hostname + "datanode" + node.getId();

		AbstractJMXController controller = (AbstractJMXController) controllerMap.get(uid);
		if (controller != null)
			controller.stopCollectingData();

		NodeConsolidationThread consolidationThread = (NodeConsolidationThread) consolidationThreadMap
				.get(node.getId());
		if (consolidationThread != null)
			consolidationThread.interrupt();

		controllerMap.remove(uid);
	}

	public static void stopResourceManagerController(final String hostname, Node node) {
		String uid = hostname + "resourcemanager" + node.getId();

		AbstractJMXController controller = (AbstractJMXController) controllerMap.get(uid);
		if (controller != null)
			controller.stopCollectingData();

		NodeConsolidationThread consolidationThread = (NodeConsolidationThread) consolidationThreadMap
				.get(node.getId());
		if (consolidationThread != null)
			consolidationThread.interrupt();

		controllerMap.remove(uid);
	}

	public static void stopNodeManagerController(final String hostname, Node node) {
		String uid = hostname + "nodemanager" + node.getId();

		AbstractJMXController controller = (AbstractJMXController) controllerMap.get(uid);
		if (controller != null)
			controller.stopCollectingData();

		NodeConsolidationThread consolidationThread = (NodeConsolidationThread) consolidationThreadMap
				.get(node.getId());
		if (consolidationThread != null)
			consolidationThread.interrupt();

		controllerMap.remove(uid);
	}

	public static void stopHostMonitorController(final int hostId) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();
			Host host = HostDAO.getHostDetail(connection, hostId);

			QueryIOAgentManager.stopMonitoring(host);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}

		HostMonitorController controller = (HostMonitorController) hostMonitorMap.get(hostId);
		if (controller != null)
			controller.stopCollectingData();
		hostMonitorMap.remove(hostId);

		HostConsolidationThread consolidationThread = (HostConsolidationThread) consolidationThreadMap.get(hostId);
		if (consolidationThread != null)
			consolidationThread.interrupt();

		controllerMap.remove(hostId);
	}

	public static void stopQueryIOServiceController(final String nodeId) {
		QueryIOServicesController controller = (QueryIOServicesController) queryIoServicesMonitorMap
				.get(nodeId + QueryIOServicesController.class);
		if (controller != null)
			controller.stopCollectingData();
		queryIoServicesMonitorMap.remove(nodeId + QueryIOServicesController.class);
	}

	public static void stopHiveServiceController(final String nodeId) {
		HiveServicesController controller = (HiveServicesController) hiveServicesMonitorMap
				.get(nodeId + HiveServicesController.class);
		if (controller != null)
			controller.stopCollectingData();
		hiveServicesMonitorMap.remove(nodeId + HiveServicesController.class);
	}

	public static ArrayList getLiveAttributes(Connection connection, ArrayList alControllerAttributes, Host host,
			int connectorPort) throws Exception {
		ArrayList liveAttrributesList = new ArrayList();

		AbstractJMXController controller = new JMXMonitorController();

		controller.setHost(host);
		controller.setConnectorPort(connectorPort);

		controller.setMonitoredHistoricalAttributes(alControllerAttributes);

		controller.initControllerData();

		controller.collectData();

		ControllerData cd = controller.getControllerHistoricalData();

		ArrayList colNames = cd.getColumnNames();
		for (int i = 0; i < colNames.size(); i++) {
			liveAttrributesList.add(new Parameter(colNames.get(i), cd.getValue((String) colNames.get(i))));
		}

		return liveAttrributesList;
	}

	public static ArrayList getNameNodeLiveAttributes(Connection connection, Host hostname, int connectorPort)
			throws Exception {
		ArrayList liveAttrributesList = null;

		try {
			ArrayList alControllerAttributes = MonitorDAO.getNameNodeLiveControllerAttributes(connection);

			liveAttrributesList = getLiveAttributes(connection, alControllerAttributes, hostname, connectorPort);
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("Monitoring live attributes for Name Node failed with exception: " + e.getMessage(), e);
		}

		return liveAttrributesList;
	}

	public static ArrayList getDataNodeLiveAttributes(Connection connection, Host host, int connectorPort) {
		ArrayList liveAttrributesList = null;

		try {
			ArrayList alControllerAttributes = MonitorDAO.getDataNodeLiveControllerAttributes(connection);

			liveAttrributesList = getLiveAttributes(connection, alControllerAttributes, host, connectorPort);
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("Monitoring live attributes for Data Node failed with exception: " + e.getMessage(), e);
		}

		return liveAttrributesList;
	}

	public static boolean testNode(Host host, int connectorPort) {
		AbstractJMXController controller = new JMXMonitorController();
		controller.setHost(host);
		controller.setConnectorPort(connectorPort);
		try {
			controller.testConnect();
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("hostName responding at " + connectorPort);
			return true;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Node not responding on " + host.getHostIP(), e);
			return false;
		}
	}

	public static void stopAllControllers() {
		if (controllerMap.size() != 0) {
			final Iterator itr = controllerMap.keySet().iterator();
			String controllerId = null;
			while (itr.hasNext()) {
				controllerId = (String) itr.next();
				BaseController controller = (BaseController) controllerMap.get(controllerId);
				if (controller != null)
					controller.stopCollectingData();
			}
		}
		if (consolidationThreadMap.size() != 0) {
			final Iterator itr = consolidationThreadMap.keySet().iterator();
			Object controllerId = null;
			while (itr.hasNext()) {
				controllerId = itr.next();
				Thread consolidationThread = (Thread) consolidationThreadMap.get(controllerId);
				if (consolidationThread != null)
					consolidationThread.interrupt();
			}
		}
		if (hostMonitorMap.size() != 0) {
			final Iterator itr = hostMonitorMap.keySet().iterator();
			Object controllerId = -1;
			while (itr.hasNext()) {
				controllerId = itr.next();
				HostMonitorController hostMonitorThread = (HostMonitorController) hostMonitorMap.get(controllerId);
				if (hostMonitorThread != null)
					hostMonitorThread.stopCollectingData();
			}
		}
		if (queryIoServicesMonitorMap.size() != 0) {
			final Iterator itr = queryIoServicesMonitorMap.keySet().iterator();
			Object controllerId = null;
			while (itr.hasNext()) {
				controllerId = itr.next();
				QueryIOServicesController thread = (QueryIOServicesController) queryIoServicesMonitorMap
						.get(controllerId);
				if (thread != null)
					thread.stopCollectingData();
			}
		}
	}

	public static boolean sendEmailNotification(String subject, String message) {
		boolean notified = false;
		Connection connection = null;
		try {
			NotificationManager notifMgr = NotificationManager.getInstance();
			notifMgr.setConfigXMLFilePath(EnvironmentalConstants.getWebinfDirectory());
			notifMgr.initializeNotificationManager();
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList users = UserDAO.getUsersDetails(connection);
			User user = null;
			ArrayList userList = new ArrayList();
			for (int i = 0; i < users.size(); i++) {
				user = (User) users.get(i);
				String roleName = "";
				try {
					roleName = UserDAO.getRole(connection, user.getUserName());
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Role name could not be obtained for user: " + user.getUserName(), e);
				}

				if (roleName.equalsIgnoreCase(QueryIOConstants.ROLES_ADMIN)) {
					userList.add(user);
				}
			}

			NotificationHandler.generateEmailNotification(notifMgr, subject, message,
					NotifyDAO.getNotificationSettings(connection), userList, true);

			notified = true;
		} catch (Exception e) {
			// AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing connection, Exception: " + e.getMessage(), e);
			}
		}

		return notified;
	}
}
