/*
 * @(#)  EventReader.java
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

import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;
import com.queryio.core.notifier.common.NotificationEvent;

/**
 * This class is the one which creates the server socket and waits for the new
 * Events to be received. This class informs the NotificationManager about the
 * new Events that it receives.
 * 
 * @author Exceed Consultancy Services
 */
class EventReader implements Runnable
{
	static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_AppException"); //$NON-NLS-1$

	private ServerSocket sockServer = null;
	private Socket sock = null;
	private ObjectInputStream is = null;

	/**
	 * Constructor. The Server Socket is created here.
	 * 
	 * @param notifMgrIPAddress
	 * @param notifMgrPort
	 */
	EventReader(final String notifMgrIPAddress, final int notifMgrPort)
	{
		try
		{
			final InetAddress inetAddrRemote = InetAddress.getByName(notifMgrIPAddress);
			this.sockServer = new ServerSocket(notifMgrPort, 50, inetAddrRemote);
		}
		catch (final Exception ex)
		{
			throw new RuntimeException(RM.getString("VALUE_SOCKET_INPUT_STREAM_MSG") + ex.toString()); //$NON-NLS-1$
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		while (true)
		{
			try
			{
				// blocking the server socket listening for
				// posting of Events by the EventManager
				this.sock = this.sockServer.accept();
				this.is = new ObjectInputStream(new BufferedInputStream(this.sock.getInputStream()));
				NotificationManager.getInstance().fireEventReceived((NotificationEvent) this.is.readObject());
			}
			catch (final Exception ex)
			{
				throw new RuntimeException(RM.getString("VALUE_OBJECT_INPUT_STREAM_MSG") + ex.toString()); //$NON-NLS-1$ //$IGN_Avoid_object_instantiation_in_loops$ //$IGN_String_concatenation_in_loop$
			}
		}
	}
}
