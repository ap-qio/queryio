/*
 * @(#)  Alert.java
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

/**
 * This is the dataStructure responsible for holding one Alert data. This
 * dstruct has got all the info related to the alert, the ruleId, ruleName,
 * ControllerId, StartTime, EndTime, Severity, NotificationMessage
 * 
 * @author Exceed Consultancy Services
 */
public class Alert {
	/* ruleId of the violated rule */
	private String ruleId;

	private String hostname;
	/* controllerId of the violated rule */
	private String nodeId;

	private String description;

	/* startTime when the rule got violated */
	private String startTime;

	/*
	 * endTime when the rule got resetted, it will be -1 if the rule has not got
	 * resetted
	 */
	private String endTime;

	/* severity of the violated rule */
	private String severity;

	/**
	 * getRuleId
	 * 
	 * @return
	 */
	public String getRuleId() {
		return this.ruleId;
	}

	/**
	 * getControllerId
	 * 
	 * @return
	 */
	public String getNodeId() {
		return this.nodeId;
	}

	/**
	 * getStartTime
	 * 
	 * @return
	 */
	public String getStartTime() {
		return this.startTime;
	}

	/**
	 * getEndTime
	 * 
	 * @return
	 */
	public String getEndTime() {
		return this.endTime;
	}

	/**
	 * getSeverity
	 * 
	 * @return
	 */
	public String getSeverity() {
		return this.severity;
	}

	/**
	 * setRuleId
	 * 
	 * @param string
	 */
	public void setRuleId(final String string) {
		this.ruleId = string;
	}

	/**
	 * setControllerId
	 * 
	 * @param string
	 */
	public void setNodeId(final String nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * setStartTime
	 * 
	 * @param l
	 */
	public void setStartTime(final String l) {
		this.startTime = l;
	}

	/**
	 * setEndTime
	 * 
	 * @param l
	 */
	public void setEndTime(final String l) {
		this.endTime = l;
	}

	/**
	 * setSeverity
	 * 
	 * @param string
	 */
	public void setSeverity(final String string) {
		this.severity = string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final StringBuffer sbToString = new StringBuffer("Alert: ");

		sbToString.append(" RuleId=");
		sbToString.append(this.ruleId);
		sbToString.append(" ControllerId=");
		sbToString.append(this.nodeId);
		sbToString.append(" StartTime=");
		sbToString.append(this.startTime);
		sbToString.append(" EndTime=");
		sbToString.append(this.endTime);
		sbToString.append(" Severity=");
		sbToString.append(this.severity);

		return sbToString.toString();
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
