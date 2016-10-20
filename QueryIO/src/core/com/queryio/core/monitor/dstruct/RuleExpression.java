/*
 * @(#)  RuleExpression.java
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

import com.queryio.core.monitor.alerts.evaluator.AlertEvaluationManager;

/**
 * This class acts as a data structure for storing a sub-expression of a rule.
 * 
 * @author Exceed Consultancy Services
 */
public class RuleExpression
{
	private String ruleId;
	private String attributeName;
	private String columnName;
	private String condition;
	private int value;
	private String aggregateFunction;
	private int duration;

	public RuleExpression()
	{
		this.condition = AlertEvaluationManager.CONDITION_OVER;
		aggregateFunction = "";
		value = 0;
		duration = 0;
	}

	public RuleExpression(final RuleExpression ruleExpression)
	{
		this.condition = AlertEvaluationManager.CONDITION_OVER;
		this.ruleId = ruleExpression.ruleId;
		this.attributeName = ruleExpression.attributeName;
		this.columnName = ruleExpression.columnName;
		this.condition = ruleExpression.condition;
		this.value = ruleExpression.value;
		this.aggregateFunction = ruleExpression.aggregateFunction;
		this.duration = ruleExpression.duration;
	}

	/**
	 * An expression is a function if either it has duration OR has start time
	 * OR end time
	 * 
	 * @return
	 */
	public boolean isFunction()
	{
		return (this.duration > 0);
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
	 * getAttributeName
	 * 
	 * @return
	 */
	public String getAttributeName()
	{
		return this.attributeName;
	}

	/**
	 * getColumnName
	 * 
	 * @return
	 */
	public String getColumnName()
	{
		return this.columnName;
	}

	/**
	 * getCondition
	 * 
	 * @return
	 */
	public String getCondition()
	{
		return this.condition;
	}

	/**
	 * getDuration
	 * 
	 * @return
	 */
	public int getDuration()
	{
		return this.duration;
	}

	/**
	 * getAggregateFunction
	 * 
	 * @return
	 */
	public String getAggregateFunction()
	{
		return this.aggregateFunction;
	}

	/**
	 * getValue
	 * 
	 * @return
	 */
	public int getValue()
	{
		return this.value;
	}

	/**
	 * setAttributeName
	 * 
	 * @param string
	 */
	public void setAttributeName(final String string)
	{
		this.attributeName = string;
	}

	/**
	 * setCondition
	 * 
	 * @param string
	 */
	public void setCondition(final String string)
	{
		this.condition = string;
	}

	/**
	 * setDuration
	 * 
	 * @param i
	 */
	public void setDuration(final int i)
	{
		this.duration = i;
	}

	/**
	 * setAggregateFunction
	 * 
	 * @param string
	 */
	public void setAggregateFunction(final String string)
	{
		this.aggregateFunction = string;
	}

	/**
	 * setValue
	 * 
	 * @param i
	 */
	public void setValue(final int i)
	{
		this.value = i;
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
	 * setColumnName
	 * 
	 * @param string
	 */
	public void setColumnName(final String string)
	{
		this.columnName = string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer sbToString = new StringBuffer("RuleExpression:");

		sbToString.append(" RuleId=");
		sbToString.append(this.getRuleId());
		sbToString.append(" AttributeName=");
		sbToString.append(this.getAttributeName());
		sbToString.append(" ColumnName=");
		sbToString.append(this.getColumnName());
		sbToString.append(" Condition=");
		sbToString.append(this.getCondition());
		sbToString.append(" Value=");
		sbToString.append(this.getValue());
		sbToString.append(" AggregateFunction=");
		sbToString.append(this.getAggregateFunction());
		sbToString.append(" Duration=");
		sbToString.append(this.getDuration());

		return sbToString.toString();
	}

}
