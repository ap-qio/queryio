package com.queryio.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.queryio.common.IProductConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.bean.RuleBean;
import com.queryio.core.monitor.alerts.evaluator.AlertEvaluationManager;
import com.queryio.core.monitor.controllers.ControllerAttribute;
import com.queryio.core.monitor.dstruct.Rule;
import com.queryio.core.monitor.dstruct.RuleExpression;

/**
 * This is the DAO calss responsible for all the Database Operation for Add /
 * Update / List and Delete Rules.
 * 
 * @author Exceed Consultancy Services
 */

@SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal")
public class RuleDAO {
	private RuleDAO() {
		// private constructor
	}

	public static ArrayList getAllRuleIds(final Connection connection) throws Exception {
		final Statement statement = DatabaseFunctions.getStatement(connection);
		final ResultSet rs = DatabaseFunctions.getQueryResultsForStatement(statement,
				QueryConstants.QRY_GET_ALL_RULEIDS);
		final ArrayList arrIds = new ArrayList();
		while (rs.next()) {
			arrIds.add(rs.getString(1));
		}

		return arrIds;
	}

	public static boolean doesRuleIdExist(final Connection connection, final String ruleId) throws Exception {
		boolean bRuleIdExist = false;

		// get all the group id's existing in the database
		final Statement statement = DatabaseFunctions.getStatement(connection);
		final ResultSet rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_GET_ALL_RULEIDS);

		// iterate through all the group id's and check whether the control id
		// does exist in the database
		while (rs.next()) {
			if (ruleId.equalsIgnoreCase(rs.getString(ColumnConstants.COL_RULES_RULEID))) {
				bRuleIdExist = true;
				break;
			}
		}

		DatabaseFunctions.closeSQLObjects(statement, rs);

		return bRuleIdExist;
	}

	public static void addPredefinedRules(final Connection connection, final String nodeId, final String[] rules)
			throws Exception {
		RuleBean ruleBean = null;

		final String[] conditions = { AlertEvaluationManager.CONDITION_OVER, AlertEvaluationManager.CONDITION_UNDER,
				AlertEvaluationManager.CONDITION_EQUALS, AlertEvaluationManager.CONDITION_NOTEQUALS };

		String separator = "|*|";
		int seplen = 3;
		String ruleId;
		String attributeName;
		for (int i = 0; i < rules.length; i++) {
			// parse the string & create a rule bean from it
			ruleBean = new RuleBean();
			ruleBean.setRuleIgnored(false);
			ruleBean.setSeverity(AlertEvaluationManager.RULESEVERITY_ERROR);

			String itrRule = rules[i];
			int index = itrRule.indexOf(separator);
			attributeName = itrRule.substring(0, index);
			ruleBean.setAttrNames(new String[] { attributeName });
			itrRule = itrRule.substring(index + seplen);

			index = itrRule.indexOf(separator);
			ruleId = itrRule.substring(0, index);
			itrRule = itrRule.substring(index + seplen);

			ruleBean.setRuleId(ruleId);
			ruleBean.setAlertRaisedNotificationSubject(ruleId + " rule violated");
			ruleBean.setAlertResetNotificationSubject(ruleId + " rule came out of violation");
			ruleBean.setAlertRaisedNotificationMessage(
					ruleId + " rule violated. " + attributeName + " value is unexpected.");
			ruleBean.setAlertResetNotificationMessage(
					ruleId + " rule came out of violation. " + attributeName + " value is within expected range.");

			index = itrRule.indexOf(separator);
			ruleBean.setConditions(new String[] { conditions[Integer.parseInt(itrRule.substring(0, index))] });
			itrRule = itrRule.substring(index + seplen);

			ruleBean.setValues(new String[] { itrRule });

			ruleBean.setAggregateFunctions(new String[] { IProductConstants.EMPTY_STRING });
			ruleBean.setDurations(new String[] { IProductConstants.EMPTY_STRING });

			// set the controller id & pass the rule bean
			ruleBean.setNodeId(nodeId);
			addRule(connection, ruleBean);
		}
	}

	/**
	 * This method adds the rule in the database. Steps involved in adding a
	 * rule are 1. Add the Rule in the Rules Table 2. Add the Expressions in the
	 * RuleExpressions Table for the Rule 3. Assign the NotifyPermissions for
	 * both the UserNotify and RuleNotify RelationTypes
	 * 
	 * @param connection
	 * @param ruleBean
	 * @throws Exception
	 */
	public static void addRule(final Connection connection, final RuleBean ruleBean) throws Exception {
		insertRule(connection, ruleBean);
		insertExpressions(connection, ruleBean);
	}

	/**
	 * This method inserts the Rule related information in the Rules Tables
	 * 
	 * @param connection
	 * @param ruleBean
	 * @throws Exception
	 */
	private static void insertRule(final Connection connection, final RuleBean ruleBean) throws Exception {
		// add the Rule related details in the Rules Table
		final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_INSERT_RULE);

		ps.setString(1, ruleBean.getRuleId());
		ps.setString(2, ruleBean.getNodeId());
		ps.setString(3, ruleBean.getSeverity());
		ps.setString(4, ruleBean.getAlertRaisedNotificationMessage());
		ps.setString(5, ruleBean.getAlertRaisedNotificationSubject());
		ps.setString(6, ruleBean.getAlertResetNotificationMessage());
		ps.setString(7, ruleBean.getAlertResetNotificationSubject());
		ps.setBoolean(8, ruleBean.isRuleIgnored());
		ps.setString(9, ruleBean.getNotificationType());

		CoreDBManager.executeUpdateStatement(connection, ps);
		DatabaseFunctions.closePreparedStatement(ps);
	}

	/**
	 * This method inserts all the expressions of the Rule
	 * 
	 * @param connection
	 * @param ruleBean
	 * @throws Exception
	 */
	private static void insertExpressions(final Connection connection, final RuleBean ruleBean) throws Exception {
		final String[] attrNames = ruleBean.getAttrNames();
		String[] conditions = ruleBean.getConditions();
		String[] values = ruleBean.getValues();
		final String[] aggregateFunctions = ruleBean.getAggregateFunctions();
		final String[] durations = ruleBean.getDurations();

		// add the Expression related details in the RuleExpressions Table
		final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_INSERT_RULEEXPRESSION);

		for (int i = 0; i < attrNames.length; i++) {
			ps.setString(1, ruleBean.getRuleId());
			if (conditions == null) {
				conditions = new String[1];
				conditions[i] = "None";
			}
			if (values == null) {
				values = new String[1];
				values[i] = "-1";
			}

			// System.out.println("conditions[i]: " + conditions[i] + "
			// values[i]: " + values[i]);

			ps.setString(2, conditions[i]);
			ps.setInt(3, Integer.parseInt(values[i]));

			// if the Start Time not present then add the StartTime as null
			if (StaticUtilities.isNullOrEmpty(aggregateFunctions[i])) {
				ps.setString(4, null);
			} else {
				ps.setString(4, aggregateFunctions[i]);
			}

			ps.setString(5, null);
			// if the duration is not present then store the duration as -1
			if (StaticUtilities.isNullOrEmpty(durations[i])) {
				ps.setInt(6, -1);
			} else {
				ps.setInt(6, Integer.parseInt(durations[i]));
			}
			ps.setString(7, attrNames[i]);
			ps.setString(8, ruleBean.getNodeId());

			CoreDBManager.executeUpdateStatement(connection, ps);
		}

		DatabaseFunctions.closePreparedStatement(ps);
	}

	public static RuleBean selectRule(final Connection connection, final String ruleId) throws Exception {
		RuleBean ruleBean = null;

		final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_SELECT_RULE);
		ps.setString(1, ruleId);
		final ResultSet rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

		if (rs.next()) {
			ruleBean = new RuleBean();
			ruleBean.setRuleId(ruleId);
			ruleBean.setRuleIgnored(rs.getBoolean(ColumnConstants.COL_RULES_IGNORERULE));
			ruleBean.setNodeId(rs.getString(ColumnConstants.COL_RULES_NODEID));
			ruleBean.setSeverity(rs.getString(ColumnConstants.COL_RULES_SEVERITY));
			ruleBean.setAlertRaisedNotificationMessage(rs.getString(ColumnConstants.COL_RULES_ALERTRAISEDNOTIFMSG));
			ruleBean.setAlertRaisedNotificationSubject(rs.getString(ColumnConstants.COL_RULES_ALERTRAISEDNOTIFSUB));
			ruleBean.setAlertResetNotificationMessage(rs.getString(ColumnConstants.COL_RULES_ALERTRESETNOTIFMSG));
			ruleBean.setAlertResetNotificationSubject(rs.getString(ColumnConstants.COL_RULES_ALERTRESETNOTIFSUB));
		}

		DatabaseFunctions.closeSQLObjects(ps, rs);

		if (ruleBean != null) {
			// gets all the Rule expressions and sets the same in the RuleBean
			getAllRuleExpressions(connection, ruleId, ruleBean);

			final ArrayList alRuleExpressions = ruleBean.getExpressions();

			final int iSize = alRuleExpressions.size();

			final String[] attrNames = new String[iSize];
			final String[] conditions = new String[iSize];
			final String[] values = new String[iSize];
			final String[] aggregateFunctions = new String[iSize];
			final String[] durations = new String[iSize];

			for (int i = 0; i < iSize; i++) {
				final RuleExpression subExpr = (RuleExpression) alRuleExpressions.get(i);

				attrNames[i] = subExpr.getAttributeName();
				conditions[i] = subExpr.getCondition();
				values[i] = String.valueOf(subExpr.getValue());
				if (StaticUtilities.isNullOrEmpty(subExpr.getAggregateFunction())) {
					aggregateFunctions[i] = "";
				} else {
					aggregateFunctions[i] = subExpr.getAggregateFunction();
				}
				if (subExpr.getDuration() == -1) {
					durations[i] = "";
				} else {
					durations[i] = String.valueOf(subExpr.getDuration());
				}
			}
			ruleBean.setAttrNames(attrNames);
			ruleBean.setConditions(conditions);
			ruleBean.setValues(values);
			ruleBean.setAggregateFunctions(aggregateFunctions);
			ruleBean.setDurations(durations);
		}
		return ruleBean;
	}

	/**
	 * This method gets all the SubExpressions and sets the same in the Rule
	 * Object
	 * 
	 * @param connection
	 * @param ruleId
	 * @param ruleBean
	 * @throws Exception
	 */
	public static void getAllRuleExpressions(final Connection connection, final String ruleId, final Rule rule)
			throws Exception {
		final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QUERY_SELECT_RULE_EXPRESSIONS);
		ps.setString(1, ruleId);
		final ResultSet rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

		while (rs.next()) {
			final RuleExpression subExpr = new RuleExpression();

			subExpr.setRuleId(ruleId);
			subExpr.setCondition(rs.getString(ColumnConstants.COL_RULEEXPRESSIONS_CONDITION));
			subExpr.setValue(rs.getInt(ColumnConstants.COL_RULEEXPRESSIONS_VALUE));
			subExpr.setAggregateFunction(rs.getString(ColumnConstants.COL_RULEEXPRESSIONS_STARTTIME));
			subExpr.setDuration(rs.getInt(ColumnConstants.COL_RULEEXPRESSIONS_DURATION));
			subExpr.setAttributeName(rs.getString(ColumnConstants.COL_RULEEXPRESSIONS_ATTRIBUTENAME));

			rule.addSubExpression(subExpr);
		}

		DatabaseFunctions.closeSQLObjects(ps, rs);
	}

	/**
	 * This method updates the rules in the database. Steps involved in updating
	 * a rule are 1. Update the rule related information like ruleName,
	 * severity, notification message etc 2. Delete all the Rule Expressions of
	 * the Rule 3. Insert all the RuleExpressions 4. Update the User
	 * Notifications for the Rule
	 */
	public static void updateRule(final Connection connection, final RuleBean ruleBean) throws Exception {
		// update the Rule related details in the Rules Table
		PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_UPDATE_RULE);
		ps.setString(1, ruleBean.getNodeId());
		ps.setString(2, ruleBean.getSeverity());
		ps.setString(3, ruleBean.getAlertRaisedNotificationMessage());
		ps.setString(4, ruleBean.getAlertRaisedNotificationSubject());
		ps.setString(5, ruleBean.getAlertResetNotificationMessage());
		ps.setString(6, ruleBean.getAlertResetNotificationSubject());
		ps.setBoolean(7, ruleBean.isRuleIgnored());
		ps.setString(8, ruleBean.getNotificationType());
		ps.setString(9, ruleBean.getRuleId());

		CoreDBManager.executeUpdateStatement(connection, ps);
		DatabaseFunctions.closePreparedStatement(ps);

		// delete all the ruleExpressions of the Rule
		ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_RULEEXPRESSIONS);
		ps.setString(1, ruleBean.getRuleId());

		CoreDBManager.executeUpdateStatement(connection, ps);
		DatabaseFunctions.closePreparedStatement(ps);

		// insert all the new RuleExpressions for the Rule
		insertExpressions(connection, ruleBean);

		// update the notifications
		// updateRuleNotifications(connection, ruleBean.getRuleId(),
		// ruleBean.getSelectedNotifs());
	}

	/** ******** Update Rule related functions end ********* */
	/** ******** Rule List related functions start ********* */

	/**
	 * This method gets the entire list of the Controllers from the database.
	 * 
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	public static ArrayList getRuleList(final Connection connection) throws Exception {
		final ArrayList rulesList = new ArrayList();
		Rule rule = null;

		final Statement statement = DatabaseFunctions.getStatement(connection);
		final ResultSet rs = CoreDBManager.getQueryResultsForStatement(statement, QueryConstants.QRY_RULES_LIST);

		Node node = null;
		Host host = null;
		ArrayList ruleExpressions = null;
		RuleExpression ruleExpression = null;
		String attributes = null;
		while (rs.next()) {
			rule = new Rule();

			node = null;
			host = null;
			rule.setRuleId(rs.getString(ColumnConstants.COL_RULES_RULEID));
			rule.setRuleIgnored(rs.getBoolean(ColumnConstants.COL_RULES_IGNORERULE));
			rule.setNodeId(rs.getString(ColumnConstants.COL_RULES_NODEID));

			node = NodeDAO.getNode(connection, rs.getString(ColumnConstants.COL_RULES_NODEID));

			if (node != null)
				host = HostDAO.getHostDetail(connection, node.getHostId());
			else {
				if (rule.getNodeId().indexOf("HOST_IP_") != -1) {
					String hostname = rule.getNodeId().substring(rule.getNodeId().lastIndexOf("_") + 1,
							rule.getNodeId().length());
					rule.setHostName(hostname);
				}
			}
			if (host != null)
				rule.setHostName(host.getHostIP());

			rule.setSeverity(rs.getString(ColumnConstants.COL_RULES_SEVERITY));
			rule.setAlertRaisedNotificationMessage(rs.getString(ColumnConstants.COL_RULES_ALERTRAISEDNOTIFMSG));
			rule.setAlertRaisedNotificationSubject(rs.getString(ColumnConstants.COL_RULES_ALERTRAISEDNOTIFSUB));
			rule.setAlertResetNotificationMessage(rs.getString(ColumnConstants.COL_RULES_ALERTRESETNOTIFMSG));
			rule.setAlertResetNotificationSubject(rs.getString(ColumnConstants.COL_RULES_ALERTRESETNOTIFSUB));
			rule.setNotificationType(rs.getString(ColumnConstants.COL_RULES_NOTIFICATIONTYPE));

			getAllRuleExpressions(connection, rule.getRuleId(), rule);

			ruleExpressions = rule.getExpressions();
			ruleExpression = null;
			attributes = "";
			for (int i = 0; i < ruleExpressions.size(); i++) {
				ruleExpression = (RuleExpression) ruleExpressions.get(i);

				attributes += ruleExpression.getAttributeName().substring(
						ruleExpression.getAttributeName().indexOf("#") + 1, ruleExpression.getAttributeName().length());
				if (i != ruleExpressions.size() - 1)
					attributes += ", ";
			}

			rule.setAttributes(attributes);
			rule.setDescription(rule.getExpression());

			rulesList.add(rule);
		}

		DatabaseFunctions.closeSQLObjects(statement, rs);

		return rulesList;
	}

	public static Rule getRule(final Connection connection, final String ruleId) throws Exception {
		Rule rule = null;

		final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_RULE);
		ps.setString(1, ruleId);
		final ResultSet rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

		while (rs.next()) {
			rule = new Rule();

			rule.setRuleId(rs.getString(ColumnConstants.COL_RULES_RULEID));
			rule.setRuleIgnored(rs.getBoolean(ColumnConstants.COL_RULES_IGNORERULE));
			rule.setNodeId(rs.getString(ColumnConstants.COL_RULES_NODEID));
			rule.setSeverity(rs.getString(ColumnConstants.COL_RULES_SEVERITY));
			rule.setAlertRaisedNotificationMessage(rs.getString(ColumnConstants.COL_RULES_ALERTRAISEDNOTIFMSG));
			rule.setAlertRaisedNotificationSubject(rs.getString(ColumnConstants.COL_RULES_ALERTRAISEDNOTIFSUB));
			rule.setAlertResetNotificationMessage(rs.getString(ColumnConstants.COL_RULES_ALERTRESETNOTIFMSG));
			rule.setAlertResetNotificationSubject(rs.getString(ColumnConstants.COL_RULES_ALERTRESETNOTIFSUB));
			rule.setRuleIgnored(rs.getBoolean(ColumnConstants.COL_RULES_IGNORERULE));
			rule.setNotificationType(rs.getString(ColumnConstants.COL_RULES_NOTIFICATIONTYPE));

			getAllRuleExpressions(connection, rule.getRuleId(), rule);
		}
		DatabaseFunctions.closeSQLObjects(ps, rs);

		return rule;
	}

	/** ******** Rule List related functions end ********* */
	/** ******** Delete Rule related functions start ********* */

	/**
	 * These are the steps involved in deletion of Rule 1. Check whether there
	 * is an Active Alert for the Rule. If yes, 1a. Get the ControllerId and
	 * Severity for the Rule getting deleted 1b. Reset the Alert and change the
	 * Controller State appropriately 2. Delete the Rule related entries from
	 * the NotifyPermissions table for both the GroupNotify and RuleNotify
	 * relation type 3. Delete the Rule related entries from the RuleExpressions
	 * table 4. Delete the Rule
	 * 
	 * @param connection
	 * @param userId
	 * @throws Exception
	 */
	public static void deleteRule(final Connection connection, final String ruleId) throws Exception {
		// check whether there is an active Alert present for the Rule
		PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_ISACTIVEALERTPRESENT_FORRULE);
		ps.setString(1, ruleId);
		final ResultSet rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

		if (rs.next()) {
			// get the controller Id for the rule
			final PreparedStatement psControllerId = DatabaseFunctions.getPreparedStatement(connection,
					QueryConstants.PREPARED_QRY_GETCONROLLERID_FORRULE);
			psControllerId.setString(1, ruleId);

			final ResultSet rsControllerId = CoreDBManager.getQueryResultsForPreparedStatement(psControllerId);

			if (rsControllerId.next()) {
				// reset the Alert, this method even sets the Controller State
				// approriately
				String nodeId = rsControllerId.getString(ColumnConstants.COL_RULES_NODEID);

				AlertDAO.writeResetAlert(connection, ruleId, nodeId, System.currentTimeMillis(),
						rsControllerId.getString(ColumnConstants.COL_RULES_SEVERITY));

				PreparedStatement psQueryAttributeState = connection
						.prepareStatement(QueryConstants.PREPARED_QRY_SELECT_ATTRIBUTESTATE);
				PreparedStatement psUpdateAttrState = connection
						.prepareStatement(QueryConstants.PREPARED_QRY_UPDATE_ATTRIBUTESTATE);
				psQueryAttributeState.setString(1, nodeId);
				ResultSet rsAttrState = psQueryAttributeState.executeQuery();
				while (rsAttrState.next()) {
					DatabaseFunctions.setDateTime(psUpdateAttrState, 1, System.currentTimeMillis());
					psUpdateAttrState.setString(2, nodeId);
					psUpdateAttrState.setString(3,
							rsAttrState.getString(ColumnConstants.COL_ATTRIBUTESTATE_ATTRIBUTENAME));
					CoreDBManager.executeUpdateStatement(connection, psUpdateAttrState);
				}
				DatabaseFunctions.closePreparedStatement(psUpdateAttrState);
				DatabaseFunctions.closeSQLObjects(psQueryAttributeState, rsAttrState);
			}

			DatabaseFunctions.closeSQLObjects(psControllerId, rsControllerId);
		}

		DatabaseFunctions.closeSQLObjects(ps, rs);

		ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_RULEEXPRESSIONS);
		ps.setString(1, ruleId);

		CoreDBManager.executeUpdateStatement(connection, ps);
		DatabaseFunctions.closePreparedStatement(ps);

		// delete the Rule
		ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.PREPARED_QRY_DELETE_RULE);
		ps.setString(1, ruleId);

		CoreDBManager.executeUpdateStatement(connection, ps);
		DatabaseFunctions.closePreparedStatement(ps);
	}

	public static ArrayList getRules(final Connection connection, final String nodeId) throws Exception {
		ArrayList rules = null;
		Rule rule = null;

		final PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.GET_CONTROLLER_RULES);
		final PreparedStatement psExpressions = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.GET_RULE_EXPRESSIONS);

		ps.setString(1, nodeId);
		final ResultSet rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);

		ArrayList expressions = null;
		Iterator iter = null;

		while (rs.next()) {
			rule = new Rule();
			rule.setRuleId(rs.getString(ColumnConstants.COL_RULES_RULEID));
			rule.setRuleIgnored(rs.getBoolean(ColumnConstants.COL_RULES_IGNORERULE));
			rule.setNodeId(rs.getString(ColumnConstants.COL_RULES_NODEID));
			rule.setSeverity(rs.getString(ColumnConstants.COL_RULES_SEVERITY));
			rule.setAlertRaisedNotificationMessage(rs.getString(ColumnConstants.COL_RULES_ALERTRAISEDNOTIFMSG));
			rule.setAlertRaisedNotificationSubject(rs.getString(ColumnConstants.COL_RULES_ALERTRAISEDNOTIFSUB));
			rule.setAlertResetNotificationMessage(rs.getString(ColumnConstants.COL_RULES_ALERTRESETNOTIFMSG));
			rule.setAlertResetNotificationSubject(rs.getString(ColumnConstants.COL_RULES_ALERTRESETNOTIFSUB));
			rule.setNotificationType(rs.getString(ColumnConstants.COL_RULES_NOTIFICATIONTYPE));

			expressions = getRuleExpressions(connection, psExpressions, rule.getRuleId(), nodeId);

			if (expressions != null) {
				for (iter = expressions.iterator(); iter.hasNext();) {
					rule.addSubExpression((RuleExpression) iter.next());
				}
			}
			if (rules == null) {
				rules = new ArrayList();
			}
			rules.add(rule);
		}

		DatabaseFunctions.closeStatement(psExpressions);
		DatabaseFunctions.closeSQLObjects(ps, rs);

		return rules;
	}

	private static ArrayList getRuleExpressions(final Connection connection, final PreparedStatement ps,
			final String ruleId, final String nodeId) throws Exception {
		ArrayList expressions = null;
		ps.setString(1, ruleId);
		final ResultSet rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
		RuleExpression re = null;
		ControllerAttribute ca = null;

		while (rs.next()) {
			re = new RuleExpression();
			re.setRuleId(ruleId);
			re.setCondition(rs.getString(ColumnConstants.COL_RULEEXPRESSIONS_CONDITION));
			re.setValue(rs.getInt(ColumnConstants.COL_RULEEXPRESSIONS_VALUE));
			re.setAggregateFunction(rs.getString(ColumnConstants.COL_RULEEXPRESSIONS_STARTTIME));
			re.setDuration(rs.getInt(ColumnConstants.COL_RULEEXPRESSIONS_DURATION));
			re.setAttributeName(rs.getString(ColumnConstants.COL_RULEEXPRESSIONS_ATTRIBUTENAME));

			// getting the column name and setting the same in the rules
			// expression

			ca = MonitorDAO.getHistoricalControllerAttribute(connection, nodeId, re.getAttributeName());

			if (ca != null) {
				re.setColumnName(ca.getColumnName());

				if (expressions == null) {
					expressions = new ArrayList();
				}

				expressions.add(re);
			} else {
				AppLogger.getLogger().fatal(
						"Controller attribute not found for rule expression: " + re.toString() + ", node: " + nodeId);
			}
		}
		DatabaseFunctions.closeResultSet(rs);
		return expressions;
	}

	public static ArrayList getRuleIdsOfActiveAlerts(final Connection connection, final String nodeId)
			throws Exception {
		ArrayList ruleIds = null;
		PreparedStatement ps = null;
		if (nodeId == null) {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_ALL_ACTIVE_ALERT_RULEIDS);
		} else {
			ps = DatabaseFunctions.getPreparedStatement(connection, QueryConstants.GET_ACTIVE_ALERT_RULEIDS);
			ps.setString(1, nodeId);
		}
		final ResultSet rs = CoreDBManager.getQueryResultsForPreparedStatement(ps);
		while (rs.next()) {
			if (ruleIds == null) {
				ruleIds = new ArrayList();
			}
			ruleIds.add(rs.getString(ColumnConstants.COL_ALERTS_RULEID));
		}
		DatabaseFunctions.closeSQLObjects(ps, rs);
		return ruleIds;
	}

	public static void suspendRule(final Connection connection, String ruleId) throws Exception {
		PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_SUSPEND_RULE);
		ps.setString(1, ruleId);

		CoreDBManager.executeUpdateStatement(connection, ps);
		DatabaseFunctions.closePreparedStatement(ps);
	}

	public static void startRule(final Connection connection, String ruleId) throws Exception {
		PreparedStatement ps = DatabaseFunctions.getPreparedStatement(connection,
				QueryConstants.PREPARED_QRY_RESTART_RULE);
		ps.setString(1, ruleId);

		CoreDBManager.executeUpdateStatement(connection, ps);
		DatabaseFunctions.closePreparedStatement(ps);
	}

	public static RuleBean getRuleBean(Connection connection, String ruleId) throws Exception {
		Rule rule = getRule(connection, ruleId);
		RuleBean ruleBean = null;

		if (rule != null) {

			ruleBean = new RuleBean();

			ruleBean.setRuleId(rule.getRuleId());
			ruleBean.setNodeId(rule.getNodeId());
			ruleBean.setAlertRaisedNotificationSubject(rule.getAlertRaisedNotificationSubject());
			ruleBean.setAlertRaisedNotificationMessage(rule.getAlertRaisedNotificationMessage());
			ruleBean.setAlertResetNotificationSubject(rule.getAlertResetNotificationSubject());
			ruleBean.setAlertResetNotificationMessage(rule.getAlertResetNotificationMessage());
			ruleBean.setSeverity(rule.getSeverity());
			ruleBean.setNotificationType(rule.getNotificationType());

			ArrayList ruleExpressions = rule.getExpressions();
			RuleExpression ruleExpression = null;

			String[] attrNames = null;
			String[] conditions = null;
			String[] values = null;
			String[] aggregateFunctions = null;
			String[] durations = null;

			if (ruleExpressions != null) {
				attrNames = new String[ruleExpressions.size()];
				conditions = new String[ruleExpressions.size()];
				values = new String[ruleExpressions.size()];
				aggregateFunctions = new String[ruleExpressions.size()];
				durations = new String[ruleExpressions.size()];

				for (int i = 0; i < ruleExpressions.size(); i++) {
					ruleExpression = (RuleExpression) ruleExpressions.get(i);

					attrNames[i] = ruleExpression.getAttributeName();
					conditions[i] = ruleExpression.getCondition();
					values[i] = String.valueOf(ruleExpression.getValue());
					aggregateFunctions[i] = ruleExpression.getAggregateFunction();
					durations[i] = String.valueOf(ruleExpression.getDuration());
				}
			}

			ruleBean.setAttrNames(attrNames);
			ruleBean.setConditions(conditions);
			ruleBean.setValues(values);
			ruleBean.setAggregateFunctions(aggregateFunctions);
			ruleBean.setDurations(durations);
		}

		return ruleBean;
	}
}
