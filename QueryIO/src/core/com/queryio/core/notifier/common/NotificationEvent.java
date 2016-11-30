/*
 * @(#)  NotificationEvent.java
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

package com.queryio.core.notifier.common;

import java.io.Serializable;
import java.util.Hashtable;

import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;

/**
 * This is the Event Class to be used to create an Event to be sent from the
 * Client to the Notification Server. This is a serializable class and is sent
 * from the client to server using the ObjectOutputStream.
 * 
 * @author Exceed Consultancy Services
 */
public class NotificationEvent implements Serializable {
	static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_AppException"); //$NON-NLS-1$
	private static final long serialVersionUID = 752600005701000039L;
	private final String sEventType;

	// key = PropertyName
	// value = Serializable Object
	private final Hashtable htProperties;

	/**
	 * Constructor, needs the eventType.
	 * 
	 * @param eventType
	 * @throws Exception
	 */
	public NotificationEvent(final String eventType) throws Exception {
		if (eventType == null) {
			throw new RuntimeException(RM.getString("VALUE_EVENT_TYPE_NULL_MSG")); //$NON-NLS-1$
		}

		this.sEventType = eventType;
		this.htProperties = new Hashtable();
	}

	/**
	 * Add a Property. The value has to be a serializable object.
	 * 
	 * @param sName
	 * @param oValue
	 * @throws Exception
	 */
	public void addProperty(final String sName, final Object oValue) throws Exception {
		if (oValue == null) {
			throw new RuntimeException(RM.getString("VALUE_PROPERTY_NULL_MSG")); //$NON-NLS-1$
		}

		if (this.htProperties.containsKey(sName)) {
			throw new RuntimeException(RM.getString("VALUE_PROPERTRY_SAME_NAME_MSG")); //$NON-NLS-1$
		}
		this.htProperties.put(sName, oValue);
	}

	/**
	 * Remove a Property.
	 * 
	 * @param sName
	 * @throws Exception
	 */
	public void removeProperty(final String sName) throws Exception {
		if (this.htProperties.containsKey(sName)) {
			this.htProperties.remove(sName);
		} else {
			throw new RuntimeException(RM.getString("VALUE_PROPERTY_DOES_NOT_EXIST_MSG")); //$NON-NLS-1$
		}
	}

	/**
	 * Set value of the Property. Value has to be a serializable object.
	 * 
	 * @param sName
	 * @param oValue
	 * @throws Exception
	 */
	public void setProperty(final String sName, final Object oValue) throws Exception {
		if (oValue == null) {
			throw new RuntimeException(RM.getString("VALUE_PROPERTY_NULL_MSG")); //$NON-NLS-1$
		}

		if (this.htProperties.containsKey(sName)) {
			this.htProperties.remove(sName);
			this.htProperties.put(sName, oValue);
		} else {
			throw new RuntimeException(RM.getString("VALUE_SET_PROPERTY_DOES_NOT_EXISTS_MSG")); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the EventType.
	 * 
	 * @return String
	 */
	public final String getEventType() {
		return this.sEventType;
	}

	public boolean containsProperty(final String sName) {
		return this.htProperties.get(sName) != null;
	}

	/**
	 * Get Property.
	 * 
	 * @param sName
	 * @return Object
	 * @throws Exception
	 */
	public Object getProperty(final String sName) throws Exception {
		final Object oValue = this.htProperties.get(sName);
		if (oValue == null) {
			throw new RuntimeException(RM.getString("VALUE_RETRIEVE_PROPERTY_DOES_NOT_EXISTS_MSG")); //$NON-NLS-1$
		}

		return oValue;
	}

	/**
	 * Gets All Properties. The Hashtable has key as PropertyName, and the value
	 * is the Serializable Object for the Property.
	 * 
	 * @param sName
	 * @return Hashtable
	 * @throws Exception
	 */
	public Hashtable getAllProperties(final String sName) throws Exception {
		if (this.htProperties == null) {
			throw new RuntimeException(RM.getString("VALUE_NO_PROPERTY")); //$NON-NLS-1$
		}

		return this.htProperties;
	}
}
