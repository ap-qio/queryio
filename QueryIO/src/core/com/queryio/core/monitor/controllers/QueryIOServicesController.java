package com.queryio.core.monitor.controllers;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.HttpStatus;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StartupParameters;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.dao.QueryIOServiceDAO;

public class QueryIOServicesController extends Thread {
	private String nodeId;
	private boolean flag;
	private Connection connection;

	public QueryIOServicesController(String nodeId) {
		this.nodeId = nodeId;
	}

	public void run() {

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Starting QueryIOServicesController for node: " + nodeId);
		flag = true;

		int port1, port2;
		String url1 = null, url2 = null;
		String status1 = null, status2 = null;
		Node node;
		Host host;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			node = NodeDAO.getNode(connection, nodeId);
			host = HostDAO.getHostDetail(connection, node.getHostId());
			port1 = QueryIOAgentManager.getOS3ServerPort(host, node);
			url1 = "http://" + host.getHostIP() + ":" + (port1) + "/queryio/GetStatus";
			port2 = QueryIOAgentManager.getHDFSOverFTPServerPort(host, node);
			url2 = "http://" + host.getHostIP() + ":" + (port2) + "/hdfs-over-ftp/GetStatus";
		} catch (Exception e) {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Failed to start QueryIOServicesController for nodeId: " + nodeId, e);
			flag = false;
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Error closing connection.", e);
			}
		}
		while (flag) {
			try {
				connection = CoreDBManager.getQueryIODBConnection();
				node = NodeDAO.getNode(connection, nodeId);
				host = HostDAO.getHostDetail(connection, node.getHostId());
				try {
					fireURL(url1);
					status1 = QueryIOConstants.STATUS_STARTED;
				} catch (Exception e) {
					status1 = QueryIOConstants.STATUS_NOT_RESPONDING;
				}
				try {
					fireURL(url2);
					status2 = QueryIOConstants.STATUS_STARTED;
				} catch (Exception e) {
					status2 = QueryIOConstants.STATUS_NOT_RESPONDING;
				}

				QueryIOServiceDAO.updateStatus(connection, nodeId, QueryIOConstants.SERVICE_OS3, status1);
				QueryIOServiceDAO.updateStatus(connection, nodeId, QueryIOConstants.SERVICE_HDFS_OVER_FTP, status2);

			} catch (Exception e) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Error occured during QueryIOServices monitoring for nodeId: " + nodeId,
							e);
				flag = false;
			} finally {
				try {
					CoreDBManager.closeConnection(connection);
				} catch (Exception e) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Error closing connection.", e);
				}
			}
			if (this.flag) {
				try {
					Thread.sleep(StartupParameters.getDataFetchIntervalInSeconds() * 1000);
				} catch (InterruptedException e1) {
					// AppLogger.getLogger().fatal(e1.getMessage(), e1);
				}
			}
		}
	}

	public void stopCollectingData() {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Stopping QueryIOServicesController for node: " + nodeId);
		this.flag = false;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			QueryIOServiceDAO.updateStatus(connection, nodeId, QueryIOConstants.SERVICE_OS3,
					QueryIOConstants.STATUS_STOPPED);
			QueryIOServiceDAO.updateStatus(connection, nodeId, QueryIOConstants.SERVICE_HDFS_OVER_FTP,
					QueryIOConstants.STATUS_STOPPED);
		} catch (Exception e) {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Failed to stop QueryIOServicesController for nodeId: " + nodeId, e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Error closing connection.", e);
			}
		}
		super.interrupt();
	}

	public static void fireURL(String remoteURL) throws Exception {
		URL url = null;
		HttpURLConnection httpCon = null;

		try {
			url = new URL(remoteURL);

			if (remoteURL.contains("https:"))
				httpCon = (HttpsURLConnection) url.openConnection();
			else
				httpCon = (HttpURLConnection) url.openConnection();

			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("POST");
			httpCon.connect();

			if (httpCon.getResponseCode() != HttpStatus.SC_OK) {
				throw new Exception("Improper response code received");
			}
		} finally {
			if (httpCon != null)
				httpCon.disconnect();
		}
	}
}
