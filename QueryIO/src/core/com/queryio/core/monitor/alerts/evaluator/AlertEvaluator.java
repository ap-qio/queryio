/*
 * @(#)  AlertEvaluator.java
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.core.monitor.alerts.evaluator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;

import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.QueryConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.dao.AlertDAO;
import com.queryio.core.dao.RuleDAO;
import com.queryio.core.monitor.controllers.ControllerData;
import com.queryio.core.monitor.controllers.ControllerManager;
import com.queryio.core.monitor.dstruct.Rule;
import com.queryio.core.monitor.dstruct.RuleExpression;


/**
 * This is the AlertEvaluator class actually responsible for Rules Evaluation.
 * 
 * @author Exceed Consultancy Services
 */
public class AlertEvaluator
{
	private String controllerState = null;
	private String nodeId = null;
	
	private long timeStamp = -1;
	private ControllerData controllerData = null;

	private final ExpressionParser expressionParser = new ExpressionParser();

	/**
	 * This is the method which is called from the BaseController after writing
	 * the data in the database. This method evaluates all the rules and then
	 * fires the ControllerDataProcess event in the end.
	 */
	public void serve(final ArrayList rules)
	{
		try
		{
			this.evaluate(rules);
		}
		catch (final Exception e)
		{
			AppLogger.getLogger().fatal("Evaluating rules failed with exception: " + e.getMessage(), e);
		}
	}

	/**
	 * This method does the actual evaluation of the Rule. This method iterates
	 * through all the rules and checks whether the rule is resetter or a rule
	 * is violated and then fires the event accordingly.
	 * 
	 * @throws Exception
	 */
	private void evaluate(final ArrayList rules) throws Exception
	{
		Connection connection = null;

		final ArrayList attributeNames = new ArrayList();
		ArrayList activeRuleIds = null;
		ArrayList activeRules = null;

		ArrayList violatedAttributes = new ArrayList();
		ArrayList resetAttributes = new ArrayList();

		final ArrayList violatedRules = new ArrayList();
		final ArrayList resetRules = new ArrayList();
		String state = ControllerManager.CONTROLLER_STATE_OK;
		try
		{
			// 1. get the DB Connection
			connection = CoreDBManager.getQueryIODBConnection();

			// 2. get the rule IDs which are currently active
			activeRuleIds = RuleDAO.getRuleIdsOfActiveAlerts(connection, this.nodeId);

			Rule rule = null;
			RuleExpression re = null;
			String expression = null;
			String attributeName = null;
			int noOfSubExprs = -1;
			String currId = null;
			Object currVal = null;
			ArrayList subExpressions = null;
			int attributeId = -1;
			boolean bEvaluate = true;
			double value = 0;
			final String attr = "attr";

			// 3. evaluate each rule
			for (final Iterator iter = rules.iterator(); iter.hasNext();)
			{
				bEvaluate = true;
				// clear the expression parser
				this.expressionParser.clearAll();
				attributeNames.clear();

				// get the next rule
				rule = (Rule) iter.next();
				
				// For Range Monitoring
				
				expression = rule.getExpression();
				subExpressions = rule.getExpressions();
				
				AppLogger.getLogger().info("expression: " + expression);
				
				noOfSubExprs = rule.getNumExpressions();
				if (rule.isRuleIgnored())
				{
					if (activeRuleIds != null && activeRuleIds.contains(rule.getRuleId()))
					{
						re = null;
						for (int j = 0; j < noOfSubExprs; j++)
						{
							re = (RuleExpression) subExpressions.get(j);
							attributeName = re.getAttributeName();
							if (!attributeNames.contains(attributeName))
							{
								attributeNames.add(attributeName);
							}
						}
						resetAttributes = mergeList(resetAttributes, attributeNames);
						resetRules.add(rule);
					}
					continue;
				}
				this.expressionParser.addVariableAsObject(AlertEvaluationManager.EXPR_TS, new Long(this.controllerData.getTimeStamp()));
				re = null;
				for (int j = 0; j < noOfSubExprs; j++)
				{
					re = (RuleExpression) subExpressions.get(j);
					attributeName = re.getAttributeName();
					if (!attributeNames.contains(attributeName))
					{
						attributeNames.add(attributeName);
					}
					
					attributeId = rule.getIndex(attributeName);
					
					// get the current Value from the ControllerData using the
					// columnName of the attribute
					currVal = this.controllerData.getValue(re.getColumnName());
					
					currId = attr + attributeId;
					
					if (currVal == null)
					{
						bEvaluate = false;
						break;
					}
					final StringBuffer sbTemp = new StringBuffer(String.valueOf(AlertEvaluationManager.EXPR_ATTRIBUTE_ID_ENCAPSULATOR));
					sbTemp.append(attributeId);
					sbTemp.append(AlertEvaluationManager.EXPR_ATTRIBUTE_ID_ENCAPSULATOR);
					
					expression = StaticUtilities.searchAndReplace(expression, sbTemp.toString(), currId);
					this.expressionParser.addVariableAsObject(currId, currVal);
					AppLogger.getLogger().info(
							"var: " + currId + " value: " + currVal);
				}
				if (bEvaluate)
				{
					expression = this.searchAndReplaceFunctions(expression, this.timeStamp);
					this.expressionParser.parseExpression(expression);
					
					AppLogger.getLogger().info(
							"expression after doing serch and replace: " + expression);
					if (this.expressionParser.hasError())
					{
						final String msg = "Error in evaluating expression: " + this.expressionParser.getErrorInfo()
								+ " for controller: " + this.nodeId;
						AppLogger.getLogger().log(
								AppLogger.getPriority(AppLogger.FATAL), msg);
					}
					else
					{
						value = this.expressionParser.getValue();
						if (value != 0)
						{
							// evaluated to true
							AppLogger.getLogger().info("rule evalauted to true");
							if ((activeRuleIds == null) || !activeRuleIds.contains(rule.getRuleId()))
							{
								// this is a newly violated rule, hence add the
								// attributes to the violated list
								violatedAttributes = mergeList(violatedAttributes, attributeNames);
								violatedRules.add(rule);
								
								// Write to the ALERTATTRIBUTES TABLE here.
								AlertDAO.writeAlertAttribute(connection, rule.getRuleId(), rule.getNodeId(),
										this.timeStamp, attributeNames);
							}
	//							else if (activeRuleIds != null)
							{
								/*
								 * this rule was active last time & continues to
								 * be active this time as well
								 */
								if (activeRules == null)
								{
									activeRules = new ArrayList(activeRuleIds != null ? activeRuleIds.size():0);
								}
								activeRules.add(rule);
							}
							
							/*
							 * if current state is OK, then the new state is
							 * either Warning/Error
							 */
							if (state.equals(ControllerManager.CONTROLLER_STATE_OK))
							{
								state = rule.getSeverity();
							}
							/*
							 * if current state is Warning, then new state can
							 * be higher than Warning i.e. Error
							 */
							else if (state.equals(ControllerManager.CONTROLLER_STATE_WARNING)
									&& rule.getSeverity().equals(ControllerManager.CONTROLLER_STATE_ERROR))
							{
								state = rule.getSeverity();
							}
						}
						else
						{
							AppLogger.getLogger().info("rule evalauted to false");
							// evaluated to false, need to update the reset time
							if ((activeRuleIds != null) && activeRuleIds.contains(rule.getRuleId()))
							{
								// this is a newly reset rule, hence add the
								// attributes to the reset list
								resetAttributes = mergeList(resetAttributes, attributeNames);
								resetRules.add(rule);
							}
						}
					}
				}
			}

			/*
			 * removing the attributes from reset list, for which atleast some
			 * other rule got violated
			 */
			for (final Iterator iter = resetAttributes.iterator(); iter.hasNext();)
			{
				attributeName = (String) iter.next();
				if (violatedAttributes.contains(attributeName))
				{
					iter.remove();
				}
			}

			/*
			 * some rules are still active, it can have some attributes which
			 * have been reset by other rules but are still active due to other
			 * rules. Remove such attributes from resetAttributes list
			 */
			if ((activeRules != null) && (activeRules.size() > 0))
			{
				for (final Iterator iter = activeRules.iterator(); iter.hasNext();)
				{
					rule = (Rule) iter.next();
					// For Range Monitoring
					
					noOfSubExprs = rule.getNumExpressions();
					subExpressions = rule.getExpressions();
					for (int j = 0; j < noOfSubExprs; j++)
					{
						re = (RuleExpression) subExpressions.get(j);
						resetAttributes.remove(re.getAttributeName());
					}
				}
			}

			// 4. If any alert have been reset, fire alertResetEvent
			if (resetRules.size() > 0)
			{
				AppLogger.getLogger().info("alert reset event fired rules: " + resetRules + " attributes: " + resetAttributes);
				// write the alert reset to the data base
				for (final Iterator iter = resetRules.iterator(); iter.hasNext();)
				{
					rule = (Rule) iter.next();
					AlertDAO.writeResetAlert(connection, rule.getRuleId(), this.nodeId, this.timeStamp, rule
							.getSeverity());
				}
				// fire alert reset event
				PreparedStatement ps = null;
				try
				{
				    ps = connection.prepareStatement(QueryConstants.PREPARED_QRY_UPDATE_ATTRIBUTESTATE);
					for (int i = 0; i < resetAttributes.size(); i++) 
					{
						DatabaseFunctions.setDateTime(ps, 1, this.timeStamp);
						ps.setString(2, this.nodeId);
						ps.setString(3, (String) resetAttributes.get(i));
						CoreDBManager.executeUpdateStatement(connection, ps);
					}
				}
				finally
				{
				    DatabaseFunctions.closePreparedStatement(ps);
				}
				
				AlertEvaluationManager.fireAlertResetEvent(this.nodeId, resetRules, resetAttributes,
						this.timeStamp);
			}

			// 5. If any alert has been raised. fire alertRaisedEvent
			if (violatedRules.size() > 0)
			{
			    PreparedStatement ps = null;
			    try
			    {
			        ps = connection.prepareStatement(QueryConstants.PREPARED_QRY_INSERT_ATTRIBUTESTATE);
					for (int i = 0; i < violatedAttributes.size(); i++) {
						ps.setString(1, rule.getSeverity());
						ps.setString(2, (String) violatedAttributes.get(i));
						ps.setString(3, this.nodeId);
						DatabaseFunctions.setDateTime(ps, 4, this.timeStamp);
						CoreDBManager.executeUpdateStatement(connection, ps);
					}
			    }
			    finally
                {
                    DatabaseFunctions.closePreparedStatement(ps);
                }
				AppLogger.getLogger().info("alert raised event fired rules: " + 
					violatedRules + " attributes: " + violatedAttributes);
				
				// write the alert generated to the data base
				for (final Iterator iter = violatedRules.iterator(); iter.hasNext();)
				{
					rule = (Rule) iter.next();

					AlertDAO.writeGeneratedAlert(connection, rule.getRuleId(), this.nodeId, this.timeStamp,
							rule.getSeverity());
				}
				
				AlertEvaluationManager.fireAlertRaisedEvent(this.nodeId, violatedRules, violatedAttributes,
						this.timeStamp);
			}
		}
		catch (final Exception ex)
		{
			final String msg = "Error in Alert Evaluation: for node: " + this.nodeId;
			AppLogger.getLogger().log(AppLogger.getPriority(AppLogger.FATAL), msg, ex);
		}
		finally
		{
			CoreDBManager.closeConnection(connection);
		}
	}

	/**
	 * method getControllerData
	 * 
	 * @return
	 */
	public ControllerData getControllerData()
	{
		return this.controllerData;
	}

	/**
	 * method getControllerId
	 * 
	 * @return
	 */
	public String getNodeId()
	{
		return this.nodeId;
	}

	/**
	 * method getTimeStamp
	 * 
	 * @return
	 */
	public long getTimeStamp()
	{
		return this.timeStamp;
	}

	/**
	 * method setControllerData
	 * 
	 * @param data
	 */
	public void setControllerData(final ControllerData data)
	{
		this.controllerData = data;
	}

	/**
	 * method setControllerId
	 * 
	 * @param string
	 */
	public void setControllerId(final String nodeId)
	{
		this.nodeId = nodeId;
	}

	/**
	 * method setTimeStamp
	 * 
	 * @param l
	 */
	public void setTimeStamp(final long l)
	{
		this.timeStamp = l;
	}

	/**
	 * method getControllerState
	 * 
	 * @return
	 */
	public String getControllerState()
	{
		return this.controllerState;
	}

	/**
	 * method setControllerState
	 * 
	 * @param string
	 */
	public void setControllerState(final String string)
	{
		this.controllerState = string;
	}

	/**
	 * searchAndReplaceFunctions
	 * 
	 * @param sExpression
	 * @param timeStamp
	 * @return
	 */
	private final String searchAndReplaceFunctions(String sExpression, final long timeStamp)
	{
		final StringBuffer sbTemp = new StringBuffer("timeduration(");
		sbTemp.append(timeStamp);
		sbTemp.append(',');

		sExpression = StaticUtilities.searchAndReplace(sExpression, "timeduration(", sbTemp.toString());
		return sExpression;
	}
	
	public static final ArrayList mergeList(final ArrayList firstList, final ArrayList secondList)
	{
		if (firstList == null)
		{
			return secondList;
		}
		if (secondList != null)
		{
			Object listObject = null;
			for (final Iterator iter = secondList.iterator(); iter.hasNext();)
			{
				listObject = iter.next();
				if (!firstList.contains(listObject))
				{
					firstList.add(listObject);
				}
			}
		}
		return firstList;
	}

	public void setNodeId(String nodeId) 
	{
		this.nodeId = nodeId;
	}
}
