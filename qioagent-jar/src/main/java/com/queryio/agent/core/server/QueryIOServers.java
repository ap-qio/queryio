package com.queryio.agent.core.server;

import java.io.File;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;


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
		
		try{
			
			QueuedThreadPool threadPool = new QueuedThreadPool();
	        threadPool.setMaxThreads(100);
	        
			final Server ftpServer = new Server();
			ftpServer.setThreadPool(threadPool);
	
	        SelectChannelConnector ftpConnector = new SelectChannelConnector();
	        ftpConnector.setPort(ftpServerPort);
	        ftpServer.setConnectors(new Connector[] {ftpConnector});
	
	        WebAppContext ftpServerApp = new WebAppContext();
	        ftpServerApp.setAttribute("hadoop_conf", hadoopConf);
	        ftpServerApp.setResourceBase(homeDir + File.separator + "webapps"+ File.separator +"hdfs-over-ftp");
	        ftpServerApp.setContextPath("/hdfs-over-ftp");
	        ftpServer.addHandler(ftpServerApp);
	        
	        final Server os3Server = new Server();
	        os3Server.setThreadPool(threadPool);
	        
	        SelectChannelConnector os3Connector = new SelectChannelConnector();
	        os3Connector.setPort(os3ServerPort);
	        os3Server.setConnectors(new Connector[] {os3Connector});
	        
	        WebAppContext os3ServerApp = new WebAppContext();
	        os3ServerApp.setAttribute("hadoop_conf", hadoopConf);
	        os3ServerApp.setResourceBase(homeDir + File.separator + "webapps"+ File.separator + "os3server");
	        os3ServerApp.setContextPath("/queryio");
	        os3Server.addHandler(os3ServerApp);
	
	        ftpServer.start();
	        ftpServer.setStopAtShutdown(true);
	        ftpServer.setSendServerVersion(true);
	        
	        os3Server.start();
	        os3Server.setStopAtShutdown(true);
	        os3Server.setSendServerVersion(true);
	        
	        System.out.println("STARTUP:SUCCESS");
	        new Thread(){
	        	public void run(){
	        		try {
						ftpServer.join();
					} catch (InterruptedException e) {
						System.out.println("STARTUP:FAILURE");
						e.printStackTrace();
					}	
	        	}
	        }.start();
	        
	        new Thread(){
	        	public void run(){
	        		try {
						os3Server.join();
					} catch (InterruptedException e) {
						System.out.println("STARTUP:FAILURE");
						e.printStackTrace();
					}	
	        	}
	        }.start();
	        
	        
		}catch(Exception e){
			System.out.println("STARTUP:FAILURE");
			e.printStackTrace();
		}	
	}
}