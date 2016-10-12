/*
 * @(#)  SmsSender.java
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

package com.queryio.core.notifier.notifiers.impl;

import java.util.ArrayList;
import java.util.LinkedList;

import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

import com.queryio.common.util.CommonResourceManager;
import com.queryio.common.util.ResourceManager;

/**
 * This is the Sms Sender class which is actually responsible for sending Sms
 * Notifications.
 * 
 * @author Exceed Consultancy Services
 */
class SmsSender
{
	private static final transient ResourceManager RM = CommonResourceManager.loadResources("Apcommon_AppException"); //$NON-NLS-1$
	/**
	 * sendMessage
	 * 
	 * @throws Exception
	 */
	void sendMessage(SmsNotifier smsNotifier) throws Exception
	{
		Service srv = null;
		try
		{
			srv = new Service();
			SerialModemGateway gateway = new SerialModemGateway("modem.com1", smsNotifier.getComPort(), 
				smsNotifier.getBaudRate(), smsNotifier.getMobileManufacturer(), smsNotifier.getMobileModel());
			gateway.setFrom(this.getMobileNo(smsNotifier.getSendersMobileNo()));
			gateway.setInbound(false);
			gateway.setOutbound(true);
			gateway.setSimPin("0000");
			srv.addGateway(gateway);
			srv.startService();
			
			final LinkedList llReceipentsMobileNo = smsNotifier.getRecepientsMobileNos();
			final int iSize = llReceipentsMobileNo.size();
			
			final ArrayList list = new ArrayList(iSize);
			for (int i = 0; i < iSize; i++)
			{
				String recieverMobileNo = this.getMobileNo((String) llReceipentsMobileNo.get(i));
				list.add(new OutboundMessage(recieverMobileNo, smsNotifier.getMessage()));
				// Send a message synchronously.
				srv.sendMessages(list);
			}
		}
		catch (final Exception ex)
		{
			throw new RuntimeException(RM.getString("VALUE_ERROR_SENDING_SMS_MSG") + ex.toString()); //$NON-NLS-1$
		}
		finally
		{
			if (srv != null)
			{
				srv.stopService();
			}
		}
	}

	private String getMobileNo(final String mobileNo)
	{
		if (!(mobileNo.charAt(0) == '+'))
		{
			return "+" + mobileNo; //$NON-NLS-1$
		}
		return mobileNo;
	}
}
