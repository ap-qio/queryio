package com.queryio.core.monitor.managers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.AlertDAO;

public class AlertManager {
	public static ArrayList getAlertList(long fromTime) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return AlertDAO.getAlertList(connection, fromTime);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAlertList() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAlertListForNode(long fromTime, String nodeId) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return AlertDAO.getAlertListForNode(connection, fromTime, nodeId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAlertListForNode() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAlertListForRule(long fromTime, String ruleId) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return AlertDAO.getAlertListForRule(connection, fromTime, ruleId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAlertListForRule() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getAlertListInBetween(long fromTime, long toTime) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return AlertDAO.getAlertListInBetween(connection, fromTime, toTime);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAlertList() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static String deleteAlerts(long fromTime, String[] alertIdentifiers) {
		Connection connection = null;

		try {
			if (RemoteManager.isNonAdminAndDemo(null)) {
				return QueryIOConstants.NOT_AN_AUTHORIZED_USER;
			}
			connection = CoreDBManager.getQueryIODBConnection();

			AlertDAO.deleteAlerts(connection, alertIdentifiers);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("deleteAlerts() failed with exception: " + e.getMessage(), e);
			return "Alert could not be deleted";
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return "Alert deleted";
	}

	public static int getAlertCountForNode(String nodeId) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return AlertDAO.getAlertCountForNode(connection, nodeId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAlertCountForNode() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return 0;
	}

	public static void fillAlertAttributes(HashMap map) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			AlertDAO.fillAlertAttributes(connection, map);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getAlertCountForNode() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
}
