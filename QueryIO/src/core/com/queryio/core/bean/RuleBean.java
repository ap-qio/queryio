/*
 * @(#)  RuleBean.java
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
package com.queryio.core.bean;

import com.queryio.core.monitor.dstruct.Rule;

/**
 * This is the bean class that will be used in the Add/Update Group.
 * 
 * @author Exceed Consultancy Services
 */
public class RuleBean extends Rule
{
	private static final long serialVersionUID = 7526000057010002380L;
	private String actionType = "add";

	private String[] attrNames = new String[0];
	private String[] conditions = new String[0];
	private String[] values = new String[0];
	private String[] aggregateFunctions = new String[0];
	private String[] durations = new String[0];

	public RuleBean()
	{
	}

	/**
	 * @param ruleBean
	 */
	public RuleBean(final RuleBean ruleBean)
	{
		super(ruleBean);
		this.actionType = ruleBean.actionType;

		final int length = ruleBean.attrNames.length;
		if (length > 0)
		{
			this.attrNames = new String[length];
			System.arraycopy(ruleBean.attrNames, 0, this.attrNames, 0, length);

			this.conditions = new String[length];
			System.arraycopy(ruleBean.conditions, 0, this.conditions, 0, length);

			this.values = new String[length];
			System.arraycopy(ruleBean.values, 0, this.values, 0, length);

			this.aggregateFunctions = new String[length];
			System.arraycopy(ruleBean.aggregateFunctions, 0, this.aggregateFunctions, 0, length);

			this.durations = new String[length];
			System.arraycopy(ruleBean.durations, 0, this.durations, 0, length);
		}
	}

	/**
	 * getActionType
	 * 
	 * @return
	 */
	public String getActionType()
	{
		return this.actionType;
	}

	/**
	 * setActionType
	 * 
	 * @param string
	 */
	public void setActionType(final String string)
	{
		this.actionType = string;
	}

	/**
	 * getAttrNames
	 * 
	 * @return
	 */
	public String[] getAttrNames()
	{
		return this.attrNames;
	}

	/**
	 * getConditions
	 * 
	 * @return
	 */
	public String[] getConditions()
	{
		return this.conditions;
	}

	/**
	 * getDurations
	 * 
	 * @return
	 */
	public String[] getDurations()
	{
		return this.durations;
	}

	/**
	 * getAggregateFunctions
	 * 
	 * @return
	 */
	public String[] getAggregateFunctions()
	{
		return this.aggregateFunctions;
	}

	/**
	 * getValues
	 * 
	 * @return
	 */
	public String[] getValues()
	{
		return this.values;
	}

	/**
	 * setAttrNames
	 * 
	 * @param strings
	 */
	public void setAttrNames(final String[] strings)
	{
		this.attrNames = strings;
	}

	/**
	 * setConditions
	 * 
	 * @param strings
	 */
	public void setConditions(final String[] strings)
	{
		this.conditions = strings;
	}

	/**
	 * setDurations
	 * 
	 * @param strings
	 */
	public void setDurations(final String[] strings)
	{
		this.durations = strings;
	}

	/**
	 * setAggregateFunctions
	 * 
	 * @param strings
	 */
	public void setAggregateFunctions(final String[] strings)
	{
		this.aggregateFunctions = strings;
	}

	/**
	 * setValues
	 * 
	 * @param strings
	 */
	public void setValues(final String[] strings)
	{
		this.values = strings;
	}

	/**
	 * clears all the Expression related Variables. This is needed when the
	 * Controller Selection is changed in the Add Rules page
	 * 
	 */
	public void clearExpressionVariables()
	{
		this.attrNames = new String[0];
		this.conditions = new String[0];
		this.values = new String[0];
		this.aggregateFunctions = new String[0];
		this.durations = new String[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		final StringBuffer sbToString = new StringBuffer("RuleBean:");

		sbToString.append(" RuleId=");
		sbToString.append(this.getRuleId());
		sbToString.append(" NodeId=");
		sbToString.append(this.getNodeId());
		sbToString.append(" Severity=");
		sbToString.append(this.getSeverity());
		sbToString.append(" NotificationSubject=");
		sbToString.append(this.getAlertRaisedNotificationMessage());
		sbToString.append(" NotificationMessage=");
		sbToString.append(this.getAlertRaisedNotificationSubject());
		sbToString.append(" Expressions=");

		for (int i = 0; i < this.attrNames.length; i++)
		{
			sbToString.append('{');
			sbToString.append("name=");
			sbToString.append(this.attrNames[i]);
			sbToString.append("conditions=");
			sbToString.append(this.conditions[i]);
			sbToString.append("values=");
			sbToString.append(this.values[i]);
			sbToString.append("aggregateFunctions=");
			sbToString.append(this.aggregateFunctions[i]);
			sbToString.append("durations=");
			sbToString.append(this.durations[i]);
			sbToString.append('}');
			sbToString.append('\t');
		}

		return sbToString.toString();
	}
}
