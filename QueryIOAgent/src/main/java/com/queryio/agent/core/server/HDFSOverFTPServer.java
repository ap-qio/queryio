package com.queryio.agent.core.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class HDFSOverFTPServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final String homeDir = args[0];
		System.setProperty("jetty.port", args[1]);
		final int port = Integer.parseInt(args[1]);
		final String hadoopConf = args[2];
		System.out.println("STARTUP:STARTING FTP SERVER ");
		System.out.println("Home Dir: " + homeDir);
		System.out.println("Port: " + port);
		System.out.println("Hadoop Configuration path: " + hadoopConf);
		Server server = null;

		try {
			server = new Server();
			ServerConnector connector = new ServerConnector(server);
			connector.setPort(port);
			server.addConnector(connector);
			WebAppContext webapp = new WebAppContext();
			webapp.setContextPath("/hdfs-over-ftp");
			webapp.setWar(homeDir + "/webapps/" + "hdfs-over-ftp");
			webapp.setDescriptor(homeDir + "/webapps/" + "hdfs-over-ftp/WEB_INF/web.xml");
			webapp.setAttribute("hadoop_conf", hadoopConf);
			server.setHandler(webapp);
			server.start();
			System.out.println("STARTUP:SUCCESS");
			server.join();
		} catch (Exception e) {
			System.out.println("STARTUP:FAILURE");
			e.printStackTrace();
		}
	}

}