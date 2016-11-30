/*
 * @(#)  Notifier.java
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

package com.queryio.core.notifier.dstruct;

/**
 * This class is the dstruct which holds the Notifer information such as the
 * name, class name, propertyset name and the PropertySet object.
 * 
 * @author Exceed Consultancy Services
 */
public class Notifier {
	private String sName = null;
	private String sClassName = null;

	private String sPropertySetName = null;
	private PropertySet propertySet = null;

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param className
	 */
	public Notifier(final String name, final String className) {
		this.sName = name;
		this.sClassName = className;
	}

	/**
	 * Returns the Name of the Notifier.
	 * 
	 * @return String
	 */
	public final String getName() {
		return this.sName;
	}

	/**
	 * Returns the ClassName.
	 * 
	 * @return String
	 */
	public final String getClassName() {
		return this.sClassName;
	}

	/**
	 * Set PropertySetName.
	 * 
	 * @param propertySetName
	 */
	public void setPropertySetName(final String propertySetName) {
		this.sPropertySetName = propertySetName;
	}

	/**
	 * Get PropertySetName.
	 * 
	 * @return String
	 */
	public final String getPropertySetName() {
		return this.sPropertySetName;
	}

	/**
	 * Method set PropertySet.
	 * 
	 * @param propSet
	 */
	public void setPropertySet(final PropertySet propSet) {
		this.propertySet = propSet;
	}

	/**
	 * Method get PropertySet.
	 * 
	 * @return PropertySet
	 */
	public final PropertySet getPropertySet() {
		return this.propertySet;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final StringBuffer sbToString = new StringBuffer("Notifier: sName = "); //$NON-NLS-1$
		sbToString.append(this.sName);
		sbToString.append(" sClassName = "); //$NON-NLS-1$
		sbToString.append(this.sClassName);
		sbToString.append(" sPropertySetName = "); //$NON-NLS-1$
		sbToString.append(this.sPropertySetName);
		sbToString.append('\n');
		sbToString.append(this.propertySet);

		return sbToString.toString();
	}
}
