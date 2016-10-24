package com.queryio.agent.core.server;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang.SystemUtils;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import com.queryio.common.QueryIOConstants;

public class QueryIOAgent {

	private static Server server = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("QueryIOAgent Called with args: " + Arrays.toString(args));

		if (SystemUtils.IS_OS_WINDOWS && args.length > 0) {
			if (args[0].equals("start"))
				start(args);
			else
				stop(args);
		} else {
			start(args);
		}
	}

	public static void start(String[] args) {
		try {
			// In case of windows first argument will be either start or stop.
			int paramStartIndex = SystemUtils.IS_OS_WINDOWS ? 1 : 0;
			String agentHome = args[paramStartIndex];
			String agentPort = args[++paramStartIndex];
			int port = Integer.parseInt(agentPort);
			System.out.println("Starting QueryIOAgent " + agentHome + " on port " + port);
			System.setProperty(QueryIOConstants.QUERYIOAGENT_PORT, agentPort);
			server = new Server();
			SelectChannelConnector connector = new SelectChannelConnector();
			connector.setPort(port);
			server.addConnector(connector);
			WebAppContext webapp = new WebAppContext();
			webapp.setContextPath("/" + QueryIOConstants.AGENT_QUERYIO);
			webapp.setWar(agentHome + File.separator + "webapps" + File.separator + QueryIOConstants.AGENT_QUERYIO);
			webapp.setDescriptor(agentHome + File.separator + "webapps" + File.separator + QueryIOConstants.AGENT_QUERYIO + File.separator + "WEB_INF" + File.separator + "web.xml");
			server.setHandler(webapp);
			server.start();
			server.join();
		} catch (Exception e) {
			System.err.println("Error while starting QueryIOAgent " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	public static void stop(String[] args) {
		try {
			server.stop();
		} catch (Exception e) {
			System.err.println("Error while stopping QueryIOAgent " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}
