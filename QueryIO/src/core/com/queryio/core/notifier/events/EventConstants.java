/*
 * @(#)  EventConstants.java
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

/**
 * This class provides the Constants for the Events.
 * 
 * @author Exceed Consultancy Services
 */
final class EventConstants {
	private EventConstants() {
		// Private Contructor provided
	}

	static final String XMLPARSER = "org.apache.xerces.parsers.SAXParser"; //$NON-NLS-1$
	static final String XMLFILENAME = "notificationmanagerconfig.xml"; //$NON-NLS-1$

	static final String DEF_XMLFILE_PATH = "."; //$NON-NLS-1$

	static final int DEF_SOCKET_PORT = 6666;
}
