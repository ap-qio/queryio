/*
 * @(#)  NotifierXMLFileParser.java
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

package com.queryio.core.notifier.notifiers;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;
import com.queryio.common.util.StaticUtilities;
import com.queryio.core.notifier.dstruct.Notifier;
import com.queryio.core.notifier.dstruct.PropertySet;

/**
 * 
 * @author Exceed Consultancy Services
 */
class NotifierXMLFileParser extends DefaultHandler implements ErrorHandler {
	static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_AppException"); //$NON-NLS-1$

	private NotificationManager notifMgr = null;

	private boolean bEnableSocketComm = false;
	private boolean bIP = false;
	private boolean bSocketPort = false;
	private boolean bNotificationEvent = false;
	private boolean bEventNotifier = false;
	// private boolean bNotifiers = false;
	private boolean bNotifierPropertySet = false;
	private boolean bNotifierProperty = false;
	private boolean bValue = false;

	private String sCurrentEventType = null;
	private String sCurrentPropertyName = null;

	private PropertySet currPropSet = null;

	// stores the list of the eventTypes for a Notifier
	// this is for temporary usage for storing the EventTypes
	// and its Notifiers relationship while the xml file is being parsed
	// key = eventType, value = LinkedList of Names of Notifiers
	private Hashtable htNotifierEventType = null;

	// stores all the Notifiers object temporarily in the Hashtables while the
	// xml file is being parsed
	// key = notifierName, value = Object of Notifier
	private Hashtable htNotifiers = null;

	// stores all the PropertySet object temporarily in the Hashtables while the
	// xml file is being parsed
	// key = propertySetName, value = Object of PropertySet
	private Hashtable htPropertySet = null;

	/**
	 * Retreives the Notifier Configuration,
	 */
	void retreiveNotifierConfiguration() {
		this.notifMgr = NotificationManager.getInstance();
		final String sFileNameWithPath = (this.notifMgr.getConfigXMLFilePath() != null)
				? this.notifMgr.getConfigXMLFilePath() : NotifierConstants.DEF_XMLFILE_PATH;
		final StringBuffer sbFileNamePath = new StringBuffer();

		if (sFileNameWithPath.endsWith(File.separator)) {
			sbFileNamePath.append(sFileNameWithPath);
			sbFileNamePath.append(NotifierConstants.EVTNOTIF_XMLFILENAME);
		} else {
			sbFileNamePath.append(sFileNameWithPath);
			sbFileNamePath.append(File.separator);
			sbFileNamePath.append(NotifierConstants.EVTNOTIF_XMLFILENAME);
		}

		try {
			// XMLReader parser = null;
			// parser =
			// XMLReaderFactory.createXMLReader(NotifierConstants.XMLPARSER);
			// parser.setContentHandler(this);
			// parser.parse(new InputSource(new InputStreamReader(new
			// BufferedInputStream(new
			// FileInputStream(sbFileNamePath.toString())), "UTF-8")));
			StaticUtilities.parseXML(this, new File(sbFileNamePath.toString()));
		} catch (final Exception ex) {
			final StringBuffer sbException = new StringBuffer(RM.getString("VALUE_ERROR_PARSING")); //$NON-NLS-1$
			sbException.append(sbFileNamePath.toString());
			sbException.append(RM.getString("VALUE_FILE")); //$NON-NLS-1$
			sbException.append(ex.toString());

			throw new RuntimeException(sbException.toString());
		}

		if ((this.htNotifierEventType == null) || (this.htNotifiers == null)) {
			final StringBuffer sbException = new StringBuffer(RM.getString("VALUE_INVALID_XML_FILE_MSG")); //$NON-NLS-1$
			sbException.append(sbFileNamePath.toString());
			sbException.append(RM.getString("VALUE_FILE_VERIFY_MSG")); //$NON-NLS-1$

			throw new RuntimeException(sbException.toString());
		}

		// setting propertySet for each Notifiers
		final Enumeration enumNotifiers = this.htNotifiers.elements();
		Notifier notif = null;
		String sPropertySetName = null;
		Object oPropertySet = null;

		while (enumNotifiers.hasMoreElements()) {
			notif = (Notifier) enumNotifiers.nextElement();
			sPropertySetName = notif.getPropertySetName();
			if (!this.isStringNullOrEmpty(sPropertySetName)) {
				oPropertySet = this.htPropertySet.get(sPropertySetName);

				if (oPropertySet == null) {
					throw new RuntimeException(RM.getString("VALUE_INVALID_PROPERTYSET")); //$NON-NLS-1$ //$IGN_Avoid_object_instantiation_in_loops$
				}

				notif.setPropertySet((PropertySet) oPropertySet);
			}
		}

		// setting all the notifiers for each EventType
		final Enumeration enumEventTypes = this.htNotifierEventType.keys();
		Object oValue = null;
		LinkedList llNotifierNames = null;
		String sEventType = null;
		String sNotifierName = null;
		int iSize = 0;

		while (enumEventTypes.hasMoreElements()) {
			sEventType = (String) enumEventTypes.nextElement();
			llNotifierNames = (LinkedList) this.htNotifierEventType.get(sEventType);

			iSize = llNotifierNames.size();
			for (int i = 0; i < iSize; i++) {
				sNotifierName = (String) llNotifierNames.get(i);
				oValue = this.htNotifiers.get(sNotifierName);

				if (oValue == null) {
					final StringBuffer sbException = new StringBuffer(RM.getString("VALUE_NOTIFIER_NAME")); //$NON-NLS-1$
					sbException.append(sNotifierName);
					sbException.append(RM.getString("VALUE_EVENT_TYPE")); //$NON-NLS-1$
					sbException.append(sEventType);

					throw new RuntimeException(sbException.toString());
				}

				this.notifMgr.registerEventNotifier(sEventType, (Notifier) oValue);
			}
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String,
	 *      Attributes)
	 */
	public void startElement(final String namespaceURI, final String localName, final String qualifiedName,
			final Attributes atts) throws SAXException {
		if (qualifiedName.equals(INotifierTagConstants.ENABLE_SOCKET_COMM)) {
			this.bEnableSocketComm = true;
		} else if (qualifiedName.equals(INotifierTagConstants.IP)) {
			this.bIP = true;
		} else if (qualifiedName.equals(INotifierTagConstants.SOCKET_PORT)) {
			this.bSocketPort = true;
		} else if (qualifiedName.equals(INotifierTagConstants.NOTIFICATION_EVENT)) {
			this.bNotificationEvent = true;
			this.sCurrentEventType = atts.getValue(INotifierTagConstants.TYPE);

			if (this.isStringNullOrEmpty(this.sCurrentEventType)) {
				throw new SAXException(RM.getString("VALUE_INVALID_EVENT_TYPE")); //$NON-NLS-1$
			}
		} else if (qualifiedName.equals(INotifierTagConstants.EVENT_NOTIFIER)) {
			this.bEventNotifier = true;
		} else if (qualifiedName.equals(INotifierTagConstants.NOTIFIER)) {
			// bNotifiers = true;
			final String sName = atts.getValue(INotifierTagConstants.NAME);
			final String sClassName = atts.getValue(INotifierTagConstants.CLASS_NAME);
			final String sPropSetName = atts.getValue(INotifierTagConstants.PROPERTY_SET);

			if (this.isStringNullOrEmpty(sName)) {
				throw new SAXException(RM.getString("VALUE_INVALID_NOTIFIER")); //$NON-NLS-1$
			}
			if (this.isStringNullOrEmpty(sClassName)) {
				throw new SAXException(RM.getString("VALUE_CLASS_NAME")); //$NON-NLS-1$
			}

			final Notifier notif = new Notifier(sName, sClassName);
			if (!this.isStringNullOrEmpty(sPropSetName)) {
				notif.setPropertySetName(sPropSetName);
			}

			if (this.htNotifiers == null) {
				this.htNotifiers = new Hashtable();
			}

			this.htNotifiers.put(notif.getName(), notif);
		} else if (qualifiedName.equals(INotifierTagConstants.NOTIFIER_PROPERTY_SET)) {
			this.bNotifierPropertySet = true;

			final String sName = atts.getValue(INotifierTagConstants.NAME);
			if (this.isStringNullOrEmpty(sName)) {
				throw new SAXException(RM.getString("VALUE_PROPERTYSET_NAME")); //$NON-NLS-1$
			}

			this.currPropSet = new PropertySet(sName);
			if (this.htPropertySet == null) {
				this.htPropertySet = new Hashtable();
			}
			this.htPropertySet.put(sName, this.currPropSet);
		} else if (qualifiedName.equals(INotifierTagConstants.NOTIFIER_PROPERTY)) {
			this.bNotifierProperty = true;

			this.sCurrentPropertyName = atts.getValue(INotifierTagConstants.NAME);
			if (this.isStringNullOrEmpty(this.sCurrentPropertyName)) {
				throw new SAXException(RM.getString("VALUE_PROPERTY_NAME")); //$NON-NLS-1$
			}
		} else if (qualifiedName.equals(INotifierTagConstants.VALUE)) {
			this.bValue = true;
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(final char[] text, final int start, final int length) {
		if (this.bEnableSocketComm && !this.bIP && !this.bSocketPort) {
			this.notifMgr.setIsSocketNotificationEnabled(new String(text, start, length));
		} else if (this.bEnableSocketComm && this.bIP) {
			this.notifMgr.setServerIP(new String(text, start, length));
		} else if (this.bEnableSocketComm && this.bSocketPort) {
			this.notifMgr.setSocketPort(new String(text, start, length));
		} else if (this.bNotificationEvent && this.bEventNotifier) {
			final String sNotifierName = new String(text, start, length);
			if (sNotifierName.length() == 0) {
				throw new RuntimeException(RM.getString("VALUE_INVALID_NOTIFIER_NAME")); //$NON-NLS-1$
			}

			if (this.htNotifierEventType == null) {
				this.htNotifierEventType = new Hashtable();
			}

			final Object oNotifierNames = this.htNotifierEventType.get(this.sCurrentEventType);
			LinkedList llNotifierNames = null;

			if (oNotifierNames == null) {
				llNotifierNames = new LinkedList();
				llNotifierNames.addLast(sNotifierName);

				this.htNotifierEventType.put(this.sCurrentEventType, llNotifierNames);
			} else {
				llNotifierNames = (LinkedList) oNotifierNames;
				llNotifierNames.addLast(sNotifierName);
			}
		} else if (this.bNotifierPropertySet && this.bNotifierProperty && this.bValue) {
			final String sValue = new String(text, start, length);

			if (this.currPropSet != null) {
				this.currPropSet.addProperty(this.sCurrentPropertyName, sValue);
			}
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	public void endElement(final String namespaceURI, final String localName, final String qualifiedName) {
		if (qualifiedName.equals(INotifierTagConstants.ENABLE_SOCKET_COMM)) {
			this.bEnableSocketComm = false;
		} else if (qualifiedName.equals(INotifierTagConstants.IP)) {
			this.bIP = false;
		} else if (qualifiedName.equals(INotifierTagConstants.SOCKET_PORT)) {
			this.bSocketPort = false;
		} else if (qualifiedName.equals(INotifierTagConstants.NOTIFICATION_EVENT)) {
			this.sCurrentEventType = null;
			this.bNotificationEvent = false;
		} else if (qualifiedName.equals(INotifierTagConstants.EVENT_NOTIFIER)) {
			this.bEventNotifier = false;
		} else if (qualifiedName.equals(INotifierTagConstants.NOTIFIER)) {
			// bNotifiers = false;
		} else if (qualifiedName.equals(INotifierTagConstants.NOTIFIER_PROPERTY_SET)) {
			this.currPropSet = null;
			this.bNotifierPropertySet = false;
		} else if (qualifiedName.equals(INotifierTagConstants.NOTIFIER_PROPERTY)) {
			this.bNotifierProperty = false;
			this.sCurrentPropertyName = null;
		} else if (qualifiedName.equals(INotifierTagConstants.VALUE)) {
			this.bValue = false;
		}
	}

	/**
	 * isStringNullOrEmpty
	 * 
	 * @param sTemp
	 * @return
	 */
	private boolean isStringNullOrEmpty(final String sTemp) {
		if ((sTemp == null) || (sTemp.length() == 0)) {
			return true;
		}

		return false;
	}
}
