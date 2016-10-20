/*
 * @(#)  Rule.java
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
package com.queryio.core.monitor.dstruct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.queryio.core.monitor.alerts.evaluator.AlertEvaluationManager;
/**
 * This is the dataStructure responsible for holding one Rule. This dstruct has
 * got all the info related to the Rule, the ruleId, ruleName, ControllerId,
 * Severity, NotificationMessage and String [] expressions
 * 
 * @author Exceed Consultancy Services
 */

public class Rule implements Serializable
{
	private static final long serialVersionUID = 7526000057010002409L;
	private String ruleId;
	private String nodeId;
	private String severity;
	private String alertRaisedNotificationSubject;
	private String alertRaisedNotificationMessage;
	private String alertResetNotificationSubject;
	private String alertResetNotificationMessage;
	private String notificationType;
	private ArrayList expressions;
	private boolean ruleIgnored;
	private String hostName;
	
	private String attributes;
	private String description;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/* stored for generating attributeId for rule evaluation */
	private transient ArrayList alAttrs = null;

	public Rule()
	{
		this.expressions = new ArrayList();
	}

	/**
	 * @param ruleBean
	 */
	public Rule(final Rule rule)
	{
		this.ruleId = rule.ruleId;
		this.ruleIgnored = rule.ruleIgnored;
		this.nodeId = rule.nodeId;
		this.severity = rule.severity;
		this.alertRaisedNotificationSubject = rule.alertRaisedNotificationSubject;
		this.alertRaisedNotificationMessage = rule.alertRaisedNotificationMessage;
		if (rule.expressions != null)
		{
			this.expressions = new ArrayList(rule.expressions.size());
			for (final Iterator expressionsIterator = rule.expressions.iterator(); expressionsIterator.hasNext();)
			{
				final RuleExpression ruleExpression = (RuleExpression) expressionsIterator.next();
				this.expressions.add(new RuleExpression(ruleExpression));
			}
		}
	}

	/**
	 * getControllerId
	 * 
	 * @return
	 */
	public String getNodeId()
	{
		return this.nodeId;
	}

	/**
	 * getNotificationMessage
	 * 
	 * @return
	 */
	public String getAlertRaisedNotificationMessage()
	{
		return this.alertRaisedNotificationMessage;
	}
	
	public String getAlertRaisedNotificationSubject() 
	{
		return alertRaisedNotificationSubject;
	}

	/**
	 * getRuleId
	 * 
	 * @return
	 */
	public String getRuleId()
	{
		return this.ruleId;
	}

	/**
	 * getSeverity
	 * 
	 * @return
	 */
	public String getSeverity()
	{
		return this.severity;
	}

	/**
	 * getExpressions
	 * 
	 * @return
	 */
	public ArrayList getExpressions()
	{
		return this.expressions;
	}

	/**
	 * setControllerId
	 * 
	 * @param string
	 */
	public void setNodeId(final String id)
	{
		this.nodeId = id;
	}

	/**
	 * setNotificationMessage
	 * 
	 * @param string
	 */
	public void setAlertRaisedNotificationMessage(final String string)
	{
		this.alertRaisedNotificationMessage = string;
	}

	public void setAlertRaisedNotificationSubject(String notificationSubject) 
	{
		this.alertRaisedNotificationSubject = notificationSubject;
	}
	
	/**
	 * setRuleId
	 * 
	 * @param string
	 */
	public void setRuleId(final String string)
	{
		this.ruleId = string;
	}

	/**
	 * setSeverity
	 * 
	 * @param string
	 */
	public void setSeverity(final String string)
	{
		this.severity = string;
	}

	/**
	 * setExpressions
	 * 
	 * @param list
	 */
	public void setExpressions(final ArrayList list)
	{
		this.expressions = list;
	}

	/**
	 * This method is for adding a new sub-expression to a rule.
	 * 
	 * @param expr -
	 *            The sub-expression to be added.
	 */
	public void addSubExpression(final RuleExpression expr)
	{
		if (this.expressions == null)
		{
			this.expressions = new ArrayList();
		}
		this.expressions.add(expr);
	}

	/**
	 * This method is for retreiving the number of sub-expressions in the rule.
	 * 
	 * @return int - The number of sub-expressions in the rule.
	 */
	public int getNumExpressions()
	{
		return this.expressions.size();
	}

	public int getIndex(final String attributeName) throws Exception
	{
		return this.alAttrs.indexOf(attributeName);
	}

	public boolean isRuleIgnored() 
	{
		return ruleIgnored;
	}

	public void setRuleIgnored(boolean ignored) 
	{
		this.ruleIgnored = ignored;
	}
	
	public String getAlertResetNotificationMessage() 
	{
		return alertResetNotificationMessage;
	}

	public void setAlertResetNotificationMessage(String alertResetNotificationMessage) 
	{
		this.alertResetNotificationMessage = alertResetNotificationMessage;
	}

	public String getAlertResetNotificationSubject() 
	{
		return alertResetNotificationSubject;
	}

	public void setAlertResetNotificationSubject(String alertResetNotificationSubject) 
	{
		this.alertResetNotificationSubject = alertResetNotificationSubject;
	}

	/**
	 * Method getExpression() This method creates the expression & returns it.
	 * 
	 * @return String - The complete expression.
	 */
	public String getExpression()
	{
		final StringBuffer sBuff = new StringBuffer();
		if (this.alAttrs == null)
		{
			this.alAttrs = new ArrayList(this.expressions.size());
		}
		else
		{
			this.alAttrs.clear();
		}

		int index = -1;
		String agFunction = null;
		for (int i = 0; i < this.expressions.size(); i++)
		{
			final RuleExpression subExpr = (RuleExpression) this.expressions.get(i);

			if (subExpr.isFunction())
			{
				agFunction = subExpr.getAggregateFunction();
				sBuff.append("duration(");
				sBuff.append(getRuleId() + "_" + i);
				sBuff.append(',');
				sBuff.append(AlertEvaluationManager.EXPR_TS);
				sBuff.append(',');
				sBuff.append((agFunction == null ? "none" : agFunction));
				sBuff.append(',');
				sBuff.append((subExpr.getDuration() > 0 ? (subExpr.getDuration() * 1000) : 0));
				sBuff.append(',');
			}

			index = this.alAttrs.indexOf(subExpr.getAttributeName());
			if (index == -1)
			{
				index = this.alAttrs.size();
				this.alAttrs.add(subExpr.getAttributeName());
			}
			sBuff.append(AlertEvaluationManager.EXPR_ATTRIBUTE_ID_ENCAPSULATOR);
			sBuff.append(index);
			sBuff.append(AlertEvaluationManager.EXPR_ATTRIBUTE_ID_ENCAPSULATOR);
			if (subExpr.getCondition().equals(AlertEvaluationManager.CONDITION_OVER))
			{
				sBuff.append('>');
			}
			else if (subExpr.getCondition().equals(AlertEvaluationManager.CONDITION_UNDER))
			{
				sBuff.append('<');
			}
			else if (subExpr.getCondition().equals(AlertEvaluationManager.CONDITION_EQUALS))
			{
				sBuff.append("==");
			}
			else if (subExpr.getCondition().equals(AlertEvaluationManager.CONDITION_NOTEQUALS))
			{
				sBuff.append("!=");
			}
			sBuff.append(subExpr.getValue());

			if (subExpr.isFunction())
			{
				sBuff.append(')');
			}

			// sBuff.append(')');
			if (i != this.expressions.size() - 1)
			{
				sBuff.append("||");
			}
		}
		// UtilityFunctions.getLogger().info("Rule,expression: " + sBuff.toString());
		return sBuff.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer sbToString = new StringBuffer("Rule:");
		sbToString.append(" RuleId=");
		sbToString.append(this.getRuleId());
		sbToString.append(" ControllerId=");
		sbToString.append(this.getNodeId());
		sbToString.append(" Severity=");
		sbToString.append(this.getSeverity());
		sbToString.append(" AlertRaisedNotificationSubject=");
		sbToString.append(this.getAlertRaisedNotificationSubject());
		sbToString.append(" AlertRaisedNotificationMessage=");
		sbToString.append(this.getAlertRaisedNotificationMessage());
		sbToString.append(" AlertResetNotificationSubject=");
		sbToString.append(this.getAlertResetNotificationSubject());
		sbToString.append(" AlertResetNotificationMessage=");
		sbToString.append(this.getAlertResetNotificationMessage());
		return sbToString.toString();
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
