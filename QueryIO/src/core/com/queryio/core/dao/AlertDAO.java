package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;

import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.bean.RuleBean;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.monitor.dstruct.Alert;
import com.queryio.core.monitor.dstruct.Rule;
import com.queryio.core.monitor.managers.RuleManager;

/**
 * This is the DAO calss responsible for all the Database Operation for List and
 * Delete Alerts.
 * 
 * @author Exceed Consultancy Services
 */
public class AlertDAO {
	/** ******** Alert List related functions start ********* */

	/**
	 * This method gets the list of the Alerts from the database.
	 * 
	 * @param connection
	 * @param fromTime
	 * @return
	 * @throws Exception
	 */
	public static ArrayList getAlertList(final Connection connection, final long fromTime) throws Exception {
		ArrayList alertsList = new ArrayList();
		final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_ALERT_LIST);
		DatabaseFunctions.setDateTime(ps, 1, fromTime);
		ResultSet rs = null;

		Rule rule = null;
		Node node = null;
		Host host = null;
		String ruleId = null;
		ArrayList list = null;

		HashMap alertAttributesMap = new HashMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
		fillAlertAttributes(connection, alertAttributesMap);

		try {
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			Alert alert = null;

			while (rs.next()) {
				alert = new Alert();

				node = null;
				host = null;
				rule = null;

				String nodeId = rs.getString(ColumnConstants.COL_ALERTS_NODEID);

				node = NodeDAO.getNode(connection, nodeId);
				if (node != null) {
					StringBuffer sBuf = new StringBuffer();
					list = (ArrayList) alertAttributesMap.get(node.getId());
					if (list != null) {
						sBuf.append(list.size() != 1 ? "Alert Attributes: " : "Alert Attribute: ");
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i) != null)
								sBuf.append(((String) list.get(i)).substring(((String) list.get(i)).indexOf("#") + 1,
										((String) list.get(i)).length()));
							if (i != list.size() - 1)
								sBuf.append(", ");
						}
					}
					alert.setDescription(sBuf.toString());
					host = HostDAO.getHostDetail(connection, node.getHostId());
				}

				if (host != null)
					alert.setHostname(host.getHostIP());

				ruleId = rs.getString(ColumnConstants.COL_ALERTS_RULEID);

				alert.setRuleId(ruleId);

				rule = RuleDAO.getRule(connection, ruleId);

				alert.setNodeId(nodeId);
				alert.setStartTime(rs.getTimestamp(ColumnConstants.COL_ALERTS_STARTTIME) != null
						? sdf.format(rs.getTimestamp(ColumnConstants.COL_ALERTS_STARTTIME).getTime()) : null);
				alert.setEndTime(rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME) != null
						? sdf.format(rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME).getTime()) : null);
				alert.setSeverity(rs.getString(ColumnConstants.COL_ALERTS_SEVERITY));

				alertsList.add(alert);
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(ps, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return alertsList;
	}

	public static ArrayList getActiveAlertListForNodeBeforeTime(final Connection connection, long beforeTime,
			String targetNodeId) throws Exception {
		ArrayList alertsList = new ArrayList();
		final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_ACTIVE_ALERT_LIST_BEFORE_TIME);
		DatabaseFunctions.setDateTime(ps, 1, beforeTime);
		ps.setString(2, targetNodeId);
		ResultSet rs = null;

		Node node = null;
		Host host = null;
		String ruleId = null;
		ArrayList list = null;

		HashMap alertAttributesMap = new HashMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
		fillAlertAttributes(connection, alertAttributesMap);

		try {
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			Alert alert = null;

			while (rs.next()) {
				alert = new Alert();

				node = null;
				host = null;

				String nodeId = rs.getString(ColumnConstants.COL_ALERTS_NODEID);

				node = NodeDAO.getNode(connection, nodeId);
				if (node != null) {
					StringBuffer sBuf = new StringBuffer();
					list = (ArrayList) alertAttributesMap.get(node.getId());
					if (list != null) {
						sBuf.append(list.size() != 1 ? "Alert Attributes: " : "Alert Attribute: ");
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i) != null)
								sBuf.append(((String) list.get(i)).substring(((String) list.get(i)).indexOf("#") + 1,
										((String) list.get(i)).length()));
							if (i != list.size() - 1)
								sBuf.append(", ");
						}
					}
					alert.setDescription(sBuf.toString());
					host = HostDAO.getHostDetail(connection, node.getHostId());
				}

				if (host != null)
					alert.setHostname(host.getHostIP());

				ruleId = rs.getString(ColumnConstants.COL_ALERTS_RULEID);

				alert.setRuleId(ruleId);

				if (rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME) == null) {
					alert.setNodeId(nodeId);
					alert.setStartTime(rs.getTimestamp(ColumnConstants.COL_ALERTS_STARTTIME) != null
							? sdf.format(rs.getTimestamp(ColumnConstants.COL_ALERTS_STARTTIME).getTime()) : null);
					alert.setEndTime(rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME) != null
							? sdf.format(rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME).getTime()) : null);
					alert.setSeverity(rs.getString(ColumnConstants.COL_ALERTS_SEVERITY));

					alertsList.add(alert);
				}
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(ps, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return alertsList;
	}

	public static int getAlertCountForNode(final Connection connection, String nodeId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_ALERT_COUNT_FOR_NODE);
			ps.setString(1, nodeId);

			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			if (rs.next()) {
				count = rs.getInt(1);
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(ps, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return count;
	}

	public static ArrayList getAlertListForNode(final Connection connection, final long fromTime, String selectedNodeId)
			throws Exception {
		ArrayList alertsList = new ArrayList();
		final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_ALERT_LIST_FOR_NODE);
		DatabaseFunctions.setDateTime(ps, 1, fromTime);
		ps.setString(2, selectedNodeId);

		ResultSet rs = null;
		Rule rule = null;
		Node node = null;
		Host host = null;
		String ruleId = null;
		ArrayList list = null;

		HashMap alertAttributesMap = new HashMap();

		fillAlertAttributes(connection, alertAttributesMap);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
		try {
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			Alert alert = null;

			while (rs.next()) {
				alert = new Alert();

				node = null;
				host = null;
				rule = null;

				String nodeId = rs.getString(ColumnConstants.COL_ALERTS_NODEID);

				node = NodeDAO.getNode(connection, nodeId);
				if (node != null) {
					StringBuffer sBuf = new StringBuffer();
					list = (ArrayList) alertAttributesMap.get(node.getId());
					if (list != null) {
						sBuf.append(list.size() != 1 ? "Alert Attributes: " : "Alert Attribute: ");
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i) != null)
								sBuf.append(((String) list.get(i)).substring(((String) list.get(i)).indexOf("#") + 1,
										((String) list.get(i)).length()));
							if (i != list.size() - 1)
								sBuf.append(", ");
						}
					}
					alert.setDescription(sBuf.toString());
					host = HostDAO.getHostDetail(connection, node.getHostId());
				}

				if (host != null)
					alert.setHostname(host.getHostIP());

				ruleId = rs.getString(ColumnConstants.COL_ALERTS_RULEID);

				alert.setRuleId(ruleId);

				rule = RuleDAO.getRule(connection, ruleId);

				if (rule != null) {
					RuleDAO.getAllRuleExpressions(connection, ruleId, rule);
				}

				alert.setNodeId(nodeId);
				alert.setStartTime(rs.getTimestamp(ColumnConstants.COL_ALERTS_STARTTIME) != null
						? sdf.format(rs.getTimestamp(ColumnConstants.COL_ALERTS_STARTTIME).getTime()) : null);
				alert.setEndTime(rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME) != null
						? sdf.format(rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME).getTime()) : null);
				alert.setSeverity(rs.getString(ColumnConstants.COL_ALERTS_SEVERITY));

				alertsList.add(alert);
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(ps, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return alertsList;
	}

	public static ArrayList getAlertListForNodeBetween(final Connection connection, final long fromTime, long toTime,
			String selectedNodeId) throws Exception {
		ArrayList alertsList = new ArrayList();
		final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_ALERT_LIST_FOR_NODE_BETWEEN);
		DatabaseFunctions.setDateTime(ps, 1, fromTime);
		DatabaseFunctions.setDateTime(ps, 2, toTime);
		ps.setString(3, selectedNodeId);

		ResultSet rs = null;
		Node node = null;
		Host host = null;
		String ruleId = null;
		ArrayList list = null;

		HashMap alertAttributesMap = new HashMap();

		fillAlertAttributes(connection, alertAttributesMap);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
		try {
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			Alert alert = null;

			while (rs.next()) {
				alert = new Alert();

				node = null;
				host = null;

				String nodeId = rs.getString(ColumnConstants.COL_ALERTS_NODEID);

				node = NodeDAO.getNode(connection, nodeId);
				if (node != null) {
					StringBuffer sBuf = new StringBuffer();
					list = (ArrayList) alertAttributesMap.get(node.getId());
					if (list != null) {
						sBuf.append(list.size() != 1 ? "Alert Attributes: " : "Alert Attribute: ");
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i) != null)
								sBuf.append(((String) list.get(i)).substring(((String) list.get(i)).indexOf("#") + 1,
										((String) list.get(i)).length()));
							if (i != list.size() - 1)
								sBuf.append(", ");
						}
					}
					alert.setDescription(sBuf.toString());
					host = HostDAO.getHostDetail(connection, node.getHostId());
				}

				if (host != null)
					alert.setHostname(host.getHostIP());

				ruleId = rs.getString(ColumnConstants.COL_ALERTS_RULEID);

				alert.setRuleId(ruleId);

				alert.setNodeId(nodeId);
				alert.setStartTime(rs.getTimestamp(ColumnConstants.COL_ALERTS_STARTTIME) != null
						? sdf.format(rs.getTimestamp(ColumnConstants.COL_ALERTS_STARTTIME).getTime()) : null);
				alert.setEndTime(rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME) != null
						? sdf.format(rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME).getTime()) : null);
				alert.setSeverity(rs.getString(ColumnConstants.COL_ALERTS_SEVERITY));

				alertsList.add(alert);
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(ps, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return alertsList;
	}

	public static ArrayList getAlertListForRule(final Connection connection, final long fromTime, String ruleId)
			throws Exception {
		ArrayList alertsList = new ArrayList();
		final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_ALERT_LIST_FOR_RULE);
		DatabaseFunctions.setDateTime(ps, 1, fromTime);
		ps.setString(2, ruleId);

		ResultSet rs = null;
		Rule rule = null;
		Node node = null;
		Host host = null;
		ArrayList list = null;

		HashMap alertAttributesMap = new HashMap();

		fillAlertAttributes(connection, alertAttributesMap);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
		try {
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			Alert alert = null;

			while (rs.next()) {
				alert = new Alert();

				node = null;
				host = null;
				rule = null;

				String nodeId = rs.getString(ColumnConstants.COL_ALERTS_NODEID);

				node = NodeDAO.getNode(connection, nodeId);
				if (node != null) {
					StringBuffer sBuf = new StringBuffer();
					list = (ArrayList) alertAttributesMap.get(node.getId());
					if (list != null) {
						sBuf.append(list.size() != 1 ? "Alert Attributes: " : "Alert Attribute: ");
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i) != null)
								sBuf.append(((String) list.get(i)).substring(((String) list.get(i)).indexOf("#") + 1,
										((String) list.get(i)).length()));
							if (i != list.size() - 1)
								sBuf.append(", ");
						}
					}
					alert.setDescription(sBuf.toString());
					host = HostDAO.getHostDetail(connection, node.getHostId());
				}

				if (host != null)
					alert.setHostname(host.getHostIP());

				ruleId = rs.getString(ColumnConstants.COL_ALERTS_RULEID);

				alert.setRuleId(ruleId);

				rule = RuleDAO.getRule(connection, ruleId);

				if (rule != null) {
					RuleDAO.getAllRuleExpressions(connection, ruleId, rule);
					alert.setDescription(rule.getExpression());
				}

				alert.setNodeId(nodeId);
				alert.setStartTime(rs.getTimestamp(ColumnConstants.COL_ALERTS_STARTTIME) != null
						? sdf.format(rs.getTimestamp(ColumnConstants.COL_ALERTS_STARTTIME).getTime()) : null);
				alert.setEndTime(rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME) != null
						? sdf.format(rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME).getTime()) : null);
				alert.setSeverity(rs.getString(ColumnConstants.COL_ALERTS_SEVERITY));

				alertsList.add(alert);
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(ps, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return alertsList;
	}

	/**
	 * 
	 * @param connection
	 * @param mapAttributesInAlert
	 * @throws Exception
	 */
	public static void fillAlertAttributes(final Connection connection, final Map mapAttributesInAlert)
			throws Exception {
		if (mapAttributesInAlert == null) {
			return;
		}
		Statement st = null;
		ResultSet rs = null;
		try {
			st = DatabaseFunctions.getStatement(connection);
			rs = CoreDBManager.getQueryResultsForStatement(st, QueryConstants.QRY_ALERT_ATTRIBUTES);
			String nodeId = null;
			String attributeName = null;
			ArrayList list = null;
			while (rs.next()) {
				nodeId = rs.getString(ColumnConstants.COL_RULES_NODEID);
				attributeName = rs.getString(ColumnConstants.COL_RULEEXPRESSIONS_ATTRIBUTENAME);
				list = (ArrayList) mapAttributesInAlert.get(nodeId);
				if (list == null) {
					list = new ArrayList();
					mapAttributesInAlert.put(nodeId, list);
				}
				if (!list.contains(attributeName)) {
					list.add(attributeName);
				}
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(st, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void fillAlertTimes(final Connection connection, final String controllerId,
			final String attributeName, final long startTime, final long endTime, final ArrayList alertStartTimes,
			final ArrayList alertEndTimes) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_ALERT_ATTRIBUTE_TIMES);
			ps.setString(1, controllerId);
			ps.setString(2, attributeName);
			DatabaseFunctions.setDateTime(ps, 3, startTime);
			DatabaseFunctions.setDateTime(ps, 4, startTime);
			DatabaseFunctions.setDateTime(ps, 5, endTime);
			DatabaseFunctions.setDateTime(ps, 6, endTime);
			DatabaseFunctions.setDateTime(ps, 7, endTime);
			DatabaseFunctions.setDateTime(ps, 8, startTime);
			DatabaseFunctions.setDateTime(ps, 9, endTime);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			while (rs.next()) {
				alertStartTimes.add(new Long(DatabaseFunctions.getDateTime(rs, ColumnConstants.COL_ALERTS_STARTTIME)));
				alertEndTimes.add(new Long(DatabaseFunctions.getDateTime(rs, ColumnConstants.COL_ALERTS_ENDTIME)));
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(ps, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void fillControllerAlertTimes(final Connection connection, final String controllerId,
			final long startTime, final long endTime, final ArrayList alertStartTimes, final ArrayList alertEndTimes)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_ALERT_CONTROLLER_TIMES);
			ps.setString(1, controllerId);
			DatabaseFunctions.setDateTime(ps, 2, startTime);
			DatabaseFunctions.setDateTime(ps, 3, endTime);

			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			while (rs.next()) {
				alertStartTimes.add(new Long(DatabaseFunctions.getDateTime(rs, ColumnConstants.COL_ALERTS_STARTTIME)));
				alertEndTimes.add(new Long(DatabaseFunctions.getDateTime(rs, ColumnConstants.COL_ALERTS_ENDTIME)));
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(ps, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static boolean hasValue(Connection connection, String controllerId, String attrName, long startTime,
			long endTime) throws Exception {
		boolean hasAlertRange = false;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_ALERT_ATTRIBUTE_TIMES);
			ps.setString(1, controllerId);
			ps.setString(2, attrName);
			DatabaseFunctions.setDateTime(ps, 3, startTime);
			DatabaseFunctions.setDateTime(ps, 4, startTime);
			DatabaseFunctions.setDateTime(ps, 5, endTime);
			DatabaseFunctions.setDateTime(ps, 6, endTime);
			DatabaseFunctions.setDateTime(ps, 7, endTime);
			DatabaseFunctions.setDateTime(ps, 8, startTime);
			DatabaseFunctions.setDateTime(ps, 9, endTime);
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			while (rs.next()) {
				hasAlertRange = true;
				break;
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(ps, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return hasAlertRange;
	}

	public static void resetAlert(final Connection connection, final String ruleId, final String nodeId,
			final long endTime) throws Exception {
		PreparedStatement psUpdate = null;
		try {
			psUpdate = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_RESET_ALERT);

			DatabaseFunctions.setDateTime(psUpdate, 1, endTime);
			psUpdate.setString(2, ruleId);
			psUpdate.setString(3, nodeId);

			CoreDBManager.executeUpdateStatement(connection, psUpdate);
		} finally {
			try {
				DatabaseFunctions.closePreparedStatement(psUpdate);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

	}

	public static void writeResetAlert(final Connection connection, final String ruleId, final String nodeId,
			final long endTime, final String severity) throws Exception {
		resetAlert(connection, ruleId, nodeId, endTime);
	}

	public static void writeGeneratedAlert(final Connection connection, final String ruleId, final String nodeId,
			final long startTime, final String severity) throws Exception {
		// resets the previously generated Alert for the Rule if it has not been
		// reset yet for any reasons.

		resetAlert(connection, ruleId, nodeId, startTime);

		// write the alert in the alerts table

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("writeGeneratedAlert, nodeId: " + nodeId);

		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_INSERT_ALERT);

			ps.setString(1, ruleId);
			ps.setString(2, nodeId);
			ps.setString(3, severity);
			DatabaseFunctions.setDateTime(ps, 4, startTime);

			CoreDBManager.executeUpdateStatement(connection, ps);

		} finally {
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	private static boolean violatedRuleWithTheSeverityExistForController(final Connection connection,
			final String ruleId, final String nodeId, final String severity) throws Exception {
		boolean bExists = false;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_VIOLATEDRULEWITHTHESEVERITYEXISTFORCONTROLLER);

			ps.setString(1, severity);
			ps.setString(2, nodeId);
			ps.setString(3, ruleId);

			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
			if (rs.next()) {
				bExists = true;
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(ps, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		return bExists;
	}

	public static void writeGeneratedAlertAttributes(final Connection connection, final String ruleId,
			final String nodeId, final long startTime, final ArrayList attributeNames) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_INSERT_ALERTATTRIBUTES);

			for (int i = 0; i < attributeNames.size(); i++) {
				ps.setString(1, ruleId);
				ps.setString(2, nodeId);
				DatabaseFunctions.setDateTime(ps, 3, startTime);
				ps.setString(4, (String) attributeNames.get(i));
				CoreDBManager.executeUpdateStatement(connection, ps);
				ps.clearParameters();
			}
		} finally {
			try {
				DatabaseFunctions.closePreparedStatement(ps);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void writeResetAllAlert(final long timeStamp) throws Exception {
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_RESET_ALLALERTS);
			DatabaseFunctions.setDateTime(ps, 1, timeStamp);
			CoreDBManager.executeUpdateStatement(connection, ps);
			DatabaseFunctions.closePreparedStatement(ps);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void writeAlertAttribute(final Connection connection, final String ruleId, final String nodeId,
			final long timeStamp, final ArrayList attributeNames) throws Exception {
		writeGeneratedAlertAttributes(connection, ruleId, nodeId, timeStamp, attributeNames);
	}

	public static void deleteAlerts(final Connection connection, final String[] alertIdentifiers) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("deleteAlerts() called");

		PreparedStatement psDeleteAlertAttribute = null;
		PreparedStatement psDeleteAlert = null;

		try {
			psDeleteAlertAttribute = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_DELETE_ALERT_ATTRIBUTE);
			psDeleteAlert = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_DELETE_ALERT);

			String alertIdentifier = null;
			String ruleId = null;
			long startTime = -1;
			long endTime = -1;
			for (int i = 0; i < alertIdentifiers.length; i++) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug(alertIdentifiers[i]);

				alertIdentifier = alertIdentifiers[i];

				ruleId = alertIdentifier.substring(0, alertIdentifier.indexOf('$'));
				RuleBean bean = RuleManager.getRuleBean(ruleId);
				Configuration conf = ConfigurationManager.getConfiguration(connection, bean.getNodeId());
				Integer interval = Integer.parseInt(conf.get("queryio.controller.data.fetch.interval"));
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
				java.util.Date parsedDate = dateFormat.parse(
						alertIdentifier.substring(alertIdentifier.indexOf('$') + 1, alertIdentifier.lastIndexOf('$')));
				java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
				startTime = timestamp.getTime();
				if (!alertIdentifier.substring(alertIdentifier.lastIndexOf('$') + 1).equals("null")) {
					parsedDate = dateFormat.parse(alertIdentifier.substring(alertIdentifier.lastIndexOf('$') + 1));
					timestamp = new java.sql.Timestamp(parsedDate.getTime());
					endTime = timestamp.getTime();
				} else {
					endTime = startTime + (interval * 1000);
				}
				psDeleteAlert.setString(1, ruleId);
				DatabaseFunctions.setDateTime(psDeleteAlert, 2, startTime);
				DatabaseFunctions.setDateTime(psDeleteAlert, 3, endTime);

				psDeleteAlertAttribute.setString(1, ruleId);
				DatabaseFunctions.setDateTime(psDeleteAlertAttribute, 2, startTime);
				DatabaseFunctions.setDateTime(psDeleteAlertAttribute, 3, endTime);

				CoreDBManager.executeUpdateStatement(connection, psDeleteAlert);
				CoreDBManager.executeUpdateStatement(connection, psDeleteAlertAttribute);
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Alerts deleted");
		} finally {
			try {
				DatabaseFunctions.closePreparedStatement(psDeleteAlertAttribute);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(psDeleteAlert);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(psDeleteAlertAttribute);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static ArrayList getAlertListInBetween(final Connection connection, final long fromTime, final long toTime)
			throws Exception {
		ArrayList alertsList = new ArrayList();
		final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_ALERT_LIST_IN_BETWEEN);
		DatabaseFunctions.setDateTime(ps, 1, fromTime);
		DatabaseFunctions.setDateTime(ps, 2, toTime);
		ResultSet rs = null;

		Rule rule = null;
		Node node = null;
		Host host = null;
		String ruleId = null;
		ArrayList list = null;

		HashMap alertAttributesMap = new HashMap();

		fillAlertAttributes(connection, alertAttributesMap);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
		try {
			rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

			Alert alert = null;

			while (rs.next()) {
				alert = new Alert();

				node = null;
				host = null;
				rule = null;

				String nodeId = rs.getString(ColumnConstants.COL_ALERTS_NODEID);

				node = NodeDAO.getNode(connection, nodeId);
				if (node != null) {
					StringBuffer sBuf = new StringBuffer();
					list = (ArrayList) alertAttributesMap.get(node.getId());
					if (list != null) {
						sBuf.append(list.size() != 1 ? "Alert Attributes: " : "Alert Attribute: ");
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i) != null)
								sBuf.append(((String) list.get(i)).substring(((String) list.get(i)).indexOf("#") + 1,
										((String) list.get(i)).length()));
							if (i != list.size() - 1)
								sBuf.append(", ");
						}
					}
					alert.setDescription(sBuf.toString());
					host = HostDAO.getHostDetail(connection, node.getHostId());
				}

				if (host != null)
					alert.setHostname(host.getHostIP());

				ruleId = rs.getString(ColumnConstants.COL_ALERTS_RULEID);

				alert.setRuleId(ruleId);

				rule = RuleDAO.getRule(connection, ruleId);

				alert.setNodeId(nodeId);
				alert.setStartTime(rs.getTimestamp(ColumnConstants.COL_ALERTS_STARTTIME) != null
						? sdf.format(rs.getTimestamp(ColumnConstants.COL_ALERTS_STARTTIME).getTime()) : null);
				alert.setEndTime(rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME) != null
						? sdf.format(rs.getTimestamp(ColumnConstants.COL_ALERTS_ENDTIME).getTime()) : null);
				alert.setSeverity(rs.getString(ColumnConstants.COL_ALERTS_SEVERITY));

				alertsList.add(alert);
			}
		} finally {
			try {
				DatabaseFunctions.closeSQLObjects(ps, rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		return alertsList;
	}

	public static boolean hasAlert(Connection connection, String nodeId, long startTime, long endTime, String alertType)
			throws Exception {
		ArrayList alertList = getActiveAlertListForNodeBeforeTime(connection, endTime + 1, nodeId);

		if (alertList != null) {
			for (int i = 0; i < alertList.size(); i++) {
				if (((Alert) alertList.get(i)).getSeverity().equalsIgnoreCase(alertType))
					return true;
			}
		}

		alertList = getAlertListForNodeBetween(connection, startTime, endTime, nodeId);

		if (alertList != null) {
			for (int i = 0; i < alertList.size(); i++) {
				if (((Alert) alertList.get(i)).getSeverity().equalsIgnoreCase(alertType))
					return true;
			}
		}

		return false;
	}
}
