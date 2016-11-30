/*
 * @(#)  AttributeNode.java
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
package com.queryio.sysmoncommon.pdh.dstruct;

import java.io.Serializable;

/**
 * Provide the class level Java documentation here. Define the purpose of class
 * and if possible where and how this class is being used.
 * 
 * @author Exceed Consultancy Services
 * @version 1.0
 */
public class AttributeNode implements Serializable {
	private static final long serialVersionUID = 752600005701000041L;
	public String objectName;
	public String counterName;
	public String instanceName;
	public int instanceCount;
	public long value;

	public AttributeNode() {
		// DO NOTHING - THIS HAS BEEN ADDED TO AVOID COMPILATION ERRORS IN THE
		// CODE.
	}

	/**
	 * @param objectName
	 * @param counterName
	 * @param instanceName
	 * @param instanceCount
	 * @param value
	 */
	public AttributeNode(final String objectName, final String counterName, final String instanceName,
			final int instanceCount, final long value) {
		this.objectName = objectName;
		this.counterName = counterName;
		this.instanceName = instanceName;
		this.instanceCount = instanceCount;
		this.value = value;
	}

	/**
	 * @return String
	 */
	public String getCounterName() {
		return this.counterName;
	}

	/**
	 * @return int
	 */
	public int getInstanceCount() {
		return this.instanceCount;
	}

	/**
	 * @return String
	 */
	public String getInstanceName() {
		return this.instanceName;
	}

	/**
	 * @return String
	 */
	public String getObjectName() {
		return this.objectName;
	}

	/**
	 * @return long
	 */
	public long getValue() {
		return this.value;
	}

	/**
	 * @param string
	 *            void
	 */
	public void setCounterName(final String string) {
		this.counterName = string;
	}

	/**
	 * @param i
	 *            void
	 */
	public void setInstanceCount(final int i) {
		this.instanceCount = i;
	}

	/**
	 * @param string
	 *            void
	 */
	public void setInstanceName(final String string) {
		this.instanceName = string;
	}

	/**
	 * @param string
	 *            void
	 */
	public void setObjectName(final String string) {
		this.objectName = string;
	}

	/**
	 * @param l
	 *            void
	 */
	public void setValue(final long l) {
		this.value = l;
	}

}
