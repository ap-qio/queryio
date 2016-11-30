/*
 * @(#)  EventXMLFileParser.java
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

package com.queryio.core.notifier.events;

import java.io.File;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.DefaultHandler;

import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;
import com.queryio.common.util.StaticUtilities;

/**
 * This class is responsible for parsing the xml file. The XML file has got
 * information like the IPAddress and the Port on which the NotifcationServer is
 * listening for the posting of Events.
 * 
 * @author Exceed Consultancy Services
 */
class EventXMLFileParser extends DefaultHandler implements ErrorHandler {
	static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_AppException"); //$NON-NLS-1$

	private boolean bIPAddress = false;
	private boolean bPort = false;

	/**
	 * Retreives the Notification Manager properties like the IPAddress, Port
	 */
	void retreiveNotificationManagerProperties() {

		final EventManager evtMgr = EventManager.getInstance();

		// setting the path of the xmlfile for reading the file.
		final String sFileNameWithPath = evtMgr.getConfigXMLFilePath() != null ? evtMgr.getConfigXMLFilePath()
				: EventConstants.DEF_XMLFILE_PATH;
		final StringBuffer sbFileNamePath = new StringBuffer();

		if (sFileNameWithPath.endsWith(File.separator)) {
			sbFileNamePath.append(sFileNameWithPath);
			sbFileNamePath.append(EventConstants.XMLFILENAME);
		} else {
			sbFileNamePath.append(sFileNameWithPath);
			sbFileNamePath.append(File.separator);
			sbFileNamePath.append(EventConstants.XMLFILENAME);
		}

		try {
			// parsing the xml fil
			// XMLReader parser =
			// XMLReaderFactory.createXMLReader(EventConstants.XMLPARSER);
			// parser.setContentHandler(this);
			// parser.parse(new InputSource(new InputStreamReader(new
			// BufferedInputStream(new
			// FileInputStream(sbFileNamePath.toString())), "UTF-8")));
			StaticUtilities.parseXML(this, new File(sbFileNamePath.toString()));
		} catch (final Exception ex) {
			final StringBuffer sbException = new StringBuffer(RM.getString("VALUE_ERROR_PARSING_MSG")); //$NON-NLS-1$
			sbException.append(sbFileNamePath.toString());
			sbException.append(" file:\n"); //$NON-NLS-1$
			sbException.append(ex.toString());

			throw new RuntimeException(sbException.toString());
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String,
	 *      Attributes)
	 */
	public void startElement(final String namespaceURI, final String localName, final String qualifiedName,
			final Attributes atts) {
		if (qualifiedName.equals("IP")) //$NON-NLS-1$
		{
			this.bIPAddress = true;
		} else if (qualifiedName.equals("SocketPort")) //$NON-NLS-1$
		{
			this.bPort = true;
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(final char[] text, final int start, final int length) {
		if (this.bIPAddress) {
			EventManager.getInstance().setNotifMgrIPAddress(new String(text, start, length));
		} else if (this.bPort) {
			EventManager.getInstance().setNotifMgrPort(new String(text, start, length));
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	public void endElement(final String namespaceURI, final String localName, final String qualifiedName) {
		if (qualifiedName.equals("IP")) //$NON-NLS-1$
		{
			this.bIPAddress = false;
		} else if (qualifiedName.equals("SocketPort")) //$NON-NLS-1$
		{
			this.bPort = false;
		}
	}
}
