/*
 * @(#)  JMXMonitorController.java
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
package com.queryio.core.monitor.controllers;

import javax.management.remote.JMXServiceURL;

/**
 * @author Exceed Consultancy Services
 * 
 */
public class JMXMonitorController extends AbstractJMXController
{
	@Override
	public void setInitProperty(String property, String value) throws Exception 
	{
		super.setInitProperty(property, value);
	}
	

	@Override
	protected JMXServiceURL getJMXServiceURL() throws Exception
	{
		final String url = JMXMonitorConstants.DEFAULT_PROTOCOL_START + this.sHostNameOrIPAddress + ":"
				+ this.iConnectorPort + JMXMonitorConstants.DEFAULT_PROTOCOL_END;
		return new JMXServiceURL(url);
	}
}
