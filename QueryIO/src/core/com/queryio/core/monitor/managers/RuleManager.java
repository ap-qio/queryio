package com.queryio.core.monitor.managers;

import java.sql.Connection;
import java.util.ArrayList;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.RuleBean;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.RuleDAO;

public class RuleManager {
	public static boolean addRule(RuleBean ruleBean) {
		Connection connection = null;

		try {
			if (RemoteManager.isNonAdminAndDemo(null)) {
				return false;
			}
			connection = CoreDBManager.getQueryIODBConnection();

			RuleDAO.addRule(connection, ruleBean);

			return true;
		} catch (Exception e) {
			AppLogger.getLogger().fatal("addRule() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return false;
	}

	public static DWRResponse addOrUpdateRule(String nodeId, String ruleId, String actionType, String severity,
			String notificationType, String alertRaisedNotificationSubject, String alertRaisedNotificationMessage,
			String alertResetNotificationSubject, String alertResetNotificationMessage, String[] attrNames,
			String[] conditions, String[] values, String[] aggregateFunctions, String[] durations) {
		DWRResponse dwrResponse = new DWRResponse();
		dwrResponse.setId(ruleId);
		Connection connection = null;

		try {
			if (RemoteManager.isNonAdminAndDemo(null)) {
				dwrResponse.setDwrResponse(false, QueryIOConstants.NOT_AN_AUTHORIZED_USER, 403);
				return dwrResponse;
			}
			connection = CoreDBManager.getQueryIODBConnection();

			RuleBean ruleBean = new RuleBean();
			ruleBean.setNodeId(nodeId);
			ruleBean.setRuleId(ruleId);
			ruleBean.setActionType(actionType);
			ruleBean.setSeverity(severity);
			ruleBean.setRuleIgnored(false);
			ruleBean.setNotificationType(notificationType);
			ruleBean.setAlertRaisedNotificationSubject(alertRaisedNotificationSubject);
			ruleBean.setAlertRaisedNotificationMessage(alertRaisedNotificationMessage);
			ruleBean.setAlertResetNotificationSubject(alertResetNotificationSubject);
			ruleBean.setAlertResetNotificationMessage(alertResetNotificationMessage);
			ruleBean.setAttrNames(attrNames);
			ruleBean.setConditions(conditions);
			ruleBean.setValues(values);
			ruleBean.setAggregateFunctions(aggregateFunctions);
			ruleBean.setDurations(durations);

			if (ruleBean.getActionType().equals(QueryIOConstants.ACTION_ADDRULE))
				RuleDAO.addRule(connection, ruleBean);
			else if (ruleBean.getActionType().equals(QueryIOConstants.ACTION_UPDATERULE))
				RuleDAO.updateRule(connection, ruleBean);

			dwrResponse.setDwrResponse(true, "Rule added successfully.", 200);
		} catch (Exception e) {
			dwrResponse.setDwrResponse(false, "Exception occurred while adding rule." + e.getLocalizedMessage(), 500);
			AppLogger.getLogger().fatal("addRule() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return dwrResponse;
	}

	public static ArrayList getRuleList() {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return RuleDAO.getRuleList(connection);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getRuleList() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static ArrayList getRules(String nodeId) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return RuleDAO.getRules(connection, nodeId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getRules() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static RuleBean getRuleBean(String ruleId) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return RuleDAO.getRuleBean(connection, ruleId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("getRules() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return null;
	}

	public static String suspendRule(String ruleId) {
		Connection connection = null;
		String status = "";

		try {
			if (RemoteManager.isNonAdminAndDemo(null)) {
				return QueryIOConstants.NOT_AN_AUTHORIZED_USER;
			}
			connection = CoreDBManager.getQueryIODBConnection();

			RuleDAO.suspendRule(connection, ruleId);

		} catch (Exception e) {
			AppLogger.getLogger().fatal("suspendRule() failed with exception: " + e.getMessage(), e);
			status = "Suspend Rule failed with exception: " + e.getMessage();
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
				if (status.isEmpty())
					status = "Database connection could not be closed.";
			}
		}
		if (status.isEmpty())
			status = "Suspend Rule operation completed successfully.";

		return status;
	}

	public static String startRule(String ruleId) {
		Connection connection = null;
		String status = "";
		try {
			if (RemoteManager.isNonAdminAndDemo(null)) {
				return QueryIOConstants.NOT_AN_AUTHORIZED_USER;
			}
			connection = CoreDBManager.getQueryIODBConnection();

			RuleDAO.startRule(connection, ruleId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("restartRule() failed with exception: " + e.getMessage(), e);
			status = "Start Rule failed with exception: " + e.getMessage();
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
				if (status.isEmpty())
					status = "Database connection could not be closed.";
			}
		}
		if (status.isEmpty())
			status = "Start Rule operation completed successfully.";

		return status;
	}

	public static String deleteRule(String ruleId) {
		Connection connection = null;
		String status = "";
		try {
			if (RemoteManager.isNonAdminAndDemo(null)) {
				return QueryIOConstants.NOT_AN_AUTHORIZED_USER;
			}
			connection = CoreDBManager.getQueryIODBConnection();

			RuleDAO.deleteRule(connection, ruleId);
			status = "Rule deleted successfully";
		} catch (Exception e) {
			status = "Deleted rule failed with exception: " + e.getMessage();
			AppLogger.getLogger().fatal("deleteRule() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		return status;
	}

	public static boolean doesRuleIdExist(String ruleId) {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			return RuleDAO.doesRuleIdExist(connection, ruleId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("deleteRule() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}

		return false;
	}
}
