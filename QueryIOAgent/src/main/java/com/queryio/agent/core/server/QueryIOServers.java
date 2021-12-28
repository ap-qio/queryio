package com.queryio.agent.core.server;

import java.io.File;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class QueryIOServers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final String homeDir = args[0];
		final int ftpServerPort = Integer.parseInt(args[1]);
		final int os3ServerPort = Integer.parseInt(args[2]);
		final String hadoopConf = args[3];
		System.out.println("STARTUP:STARTING QueryIOServers ");
		System.out.println("Home Dir: " + homeDir);
		System.out.println("FTP-Port: " + ftpServerPort);
		System.out.println("OS3-Port: " + os3ServerPort);
		System.out.println("Hadoop Configuration path: " + hadoopConf);

		try {
			QueuedThreadPool threadPool = new QueuedThreadPool();
			threadPool.setMaxThreads(100);

			final Server ftpServer = new Server(threadPool);
			ServerConnector ftpConnector = new ServerConnector(ftpServer);
			ftpConnector.setPort(ftpServerPort);
			ftpServer.setConnectors(new Connector[] { ftpConnector });

			WebAppContext ftpServerApp = new WebAppContext();
			ftpServerApp.setAttribute("hadoop_conf", hadoopConf);
			ftpServerApp.setResourceBase(homeDir + File.separator + "webapps" + File.separator + "hdfs-over-ftp");
			ftpServerApp.setContextPath("/hdfs-over-ftp");
			ftpServer.setHandler(ftpServerApp);

			final Server os3Server = new Server(threadPool);
			ServerConnector os3Connector = new ServerConnector(os3Server);
			os3Connector.setPort(os3ServerPort);
			os3Server.setConnectors(new Connector[] { os3Connector });

			WebAppContext os3ServerApp = new WebAppContext();
			os3ServerApp.setAttribute("hadoop_conf", hadoopConf);
			os3ServerApp.setResourceBase(homeDir + File.separator + "webapps" + File.separator + "os3server");
			os3ServerApp.setContextPath("/queryio");
			os3Server.setHandler(os3ServerApp);

			ftpServer.start();
			ftpServer.setStopAtShutdown(true);
//			ftpServer.setSendServerVersion(true);

			os3Server.start();
			os3Server.setStopAtShutdown(true);
//			os3Server.setSendServerVersion(true);

			System.out.println("STARTUP:SUCCESS");
			new Thread() {
				public void run() {
					try {
						ftpServer.join();
					} catch (InterruptedException e) {
						System.out.println("STARTUP:FAILURE");
						e.printStackTrace();
					}
				}
			}.start();

			new Thread() {
				public void run() {
					try {
						os3Server.join();
					} catch (InterruptedException e) {
						System.out.println("STARTUP:FAILURE");
						e.printStackTrace();
					}
				}
			}.start();

		} catch (Exception e) {
			System.out.println("STARTUP:FAILURE");
			e.printStackTrace();
		}
	}
}