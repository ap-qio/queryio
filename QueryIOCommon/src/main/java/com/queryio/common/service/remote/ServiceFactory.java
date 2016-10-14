/*
 * @(#) ServiceFactory.java Feb 20, 2007
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
package com.queryio.common.service.remote;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.springframework.remoting.httpinvoker.CommonsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

import com.queryio.common.util.StartupParameters;

public class ServiceFactory
{
	private static final String[] PROTOCOLS = { "http", "https" };

	public static final ServiceFactory INSTANCE = new ServiceFactory();

	private HttpInvokerRequestExecutor httpInvokerRequestExecutor;
	private final Map pathInterfaceMap;

	private ServiceFactory()
	{
		this.pathInterfaceMap = new HashMap();

		this.pathInterfaceMap.put(RemoteService.QUERYIO_SERVICE_BEAN, QueryIOService.class);
		
	}

	private synchronized HttpInvokerRequestExecutor getRequestExecutor()
	{
		if (this.httpInvokerRequestExecutor == null)
		{
			this.httpInvokerRequestExecutor = new CommonsHttpInvokerRequestExecutor();

			((CommonsHttpInvokerRequestExecutor)this.httpInvokerRequestExecutor).setReadTimeout(StartupParameters.getSessionTimeout());

		}
		return this.httpInvokerRequestExecutor;
	}

	public static String constructServiceURI(final int protocol, final String hostName, final int port,
			final String contextPath)
	{
		String ipAddress = hostName;
		try
		{
			ipAddress = InetAddress.getByName(hostName).getHostAddress();
		}
		catch (final Exception e)
		{
			// DO NOTHING
		}

		final StringBuffer buffer = new StringBuffer();
		buffer.append(PROTOCOLS[protocol % PROTOCOLS.length]);
		buffer.append("://");
		buffer.append(ipAddress);
		buffer.append(':');
		buffer.append(port);
		buffer.append('/');
		buffer.append(contextPath);
		return buffer.toString();
	}

	/**
	 * 
	 * @param serviceURI
	 * @param path
	 * @return
	 */
	public RemoteService getRemoteService(final String serviceURI, final String serviceName)
	{
		final HttpInvokerProxyFactoryBean bean = new HttpInvokerProxyFactoryBean();
		bean.setServiceUrl(serviceURI + serviceName);
		bean.setServiceInterface((Class) this.pathInterfaceMap.get(serviceName));
		bean.setHttpInvokerRequestExecutor(this.getRequestExecutor());
		bean.afterPropertiesSet();
		return (RemoteService) bean.getObject();
	}

	public RemoteService getRemoteService(final int protocol, final String hostName, final int port,
			final String contextPath, final String serviceName)
	{
		return this.getRemoteService(constructServiceURI(protocol, hostName, port, contextPath), serviceName);
	}
	
	
	
}
