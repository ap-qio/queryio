/*
 * @(#)  PropertySet.java
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

import java.util.Hashtable;
import java.util.LinkedList;

/**
 * This class is the dstruct which holds the PropertySet information such as the
 * name of the PropertySet and the Hashtable containing each property of the
 * notifier alongwith its value as the LinkedList.
 * 
 * @author Exceed Consultancy Services
 */
public class PropertySet {
	private String sName = null;

	// key = propery name
	// value = LinkedList of values(Stored as String)
	private Hashtable htProperties = null;

	/**
	 * Constaructor.
	 * 
	 * @param name
	 */
	public PropertySet(final String name) {
		this.sName = name;
		this.htProperties = new Hashtable();
	}

	/**
	 * GetName of the PropertySet.
	 * 
	 * @return String
	 */
	public final String getName() {
		return this.sName;
	}

	/**
	 * AddProperty adds the property to the PropertySet.
	 * 
	 * @param name
	 * @param value
	 */
	public void addProperty(final String name, final String value) {
		final Object oValue = this.htProperties.get(name);
		LinkedList llValues = null;

		if (oValue == null) {
			llValues = new LinkedList();
			llValues.add(value);
			this.htProperties.put(name, llValues);
		} else {
			llValues = (LinkedList) oValue;
			llValues.add(value);
		}
	}

	/**
	 * GetProperty returns the values of the specified property name.
	 * 
	 * @param name
	 * @return LinkedList
	 */
	public LinkedList getProperty(final String name) {
		return (LinkedList) this.htProperties.get(name);
	}

	/**
	 * GetAllProperties returns all the properties of the PropertySet. The
	 * values are returned as a Hashatble which has Key as the name of the
	 * Property whereas the value is the LinkedList of the values of the
	 * proeprty.
	 * 
	 * @return Hashtable
	 */
	public final Hashtable getAllProperties() {
		return this.htProperties;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final StringBuffer sbToString = new StringBuffer("\tPropertySet: sName = "); //$NON-NLS-1$
		sbToString.append(this.sName);
		sbToString.append("\n\t\tProperties: "); //$NON-NLS-1$
		sbToString.append(this.htProperties);

		return sbToString.toString();
	}
}
