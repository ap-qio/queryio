package com.queryio.core.ugiupdater;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.hadoop.conf.Configuration;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SecurityHandler;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;

public class UGIUpdater {

	public static void sendUserInformationUpdates() {
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList namenodes = NodeDAO.getAllNameNodesID(connection);
			String nameNodeId = null;
			String url = null;
			for (int i = 0; i < namenodes.size(); i++) {
				nameNodeId = (String) namenodes.get(i);

				String hostIP = HostDAO.getHostDetail(connection, NodeDAO.getNode(connection, nameNodeId).getHostId())
						.getHostIP();

				Configuration conf = RemoteManager.getNameNodeConfiguration(nameNodeId);

				String protocol = "http://";
				String ftpServerPort = conf.get(QueryIOConstants.QUERYIO_HDFSOVERFTP_PORT);
				boolean ftpServerSSLEnabled = conf.getBoolean(QueryIOConstants.QUERYIO_FTPSERVER_SSLENABLED, false);
				if (ftpServerSSLEnabled) {
					ftpServerPort = conf.get(QueryIOConstants.QUERYIO_FTPSERVER_SSLPORT);
					protocol = "https://";
				}

				url = protocol + hostIP + ":" + ftpServerPort + QueryIOConstants.QUERYIO_FTPSERVER_CONTEXT
						+ QueryIOConstants.USER_INFO_UPDATE_URL;

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger()
							.debug("Updating userinfo for ftp server [namenode: " + nameNodeId + "], url: " + url);

				fireUpdateRequest(url, QueryIOConstants.USER_INFO_REQUEST_HEADER_KEY,
						SecurityHandler.encryptData(UGIProvider.getUserPasswords()));

				protocol = "http://";
				String os3ServerPort = conf.get(QueryIOConstants.QUERYIO_OS3SERVER_PORT);
				boolean os3ServerSSLEnabled = conf.getBoolean(QueryIOConstants.QUERYIO_FTPSERVER_SSLENABLED, false);
				if (os3ServerSSLEnabled) {
					ftpServerPort = conf.get(QueryIOConstants.QUERYIO_OS3SERVER_SECUREPORT);
					protocol = "https://";
				}

				url = protocol + hostIP + ":" + os3ServerPort + QueryIOConstants.QUERYIO_OS3SERVER_CONTEXT
						+ QueryIOConstants.USER_INFO_UPDATE_URL;

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger()
							.debug("Updating userinfo for os3 server [namenode: " + nameNodeId + "], url: " + url);

				fireUpdateRequest(url, QueryIOConstants.USER_INFO_REQUEST_HEADER_KEY,
						SecurityHandler.encryptData(UGIProvider.getUserPasswords()));
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("updateUserInformation() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static void sendUserGroupInformationUpdates() {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Sending user-group information to all namenodes...");
		Connection connection = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			ArrayList namenodes = NodeDAO.getAllNameNodesID(connection);
			String nameNodeId = null;
			for (int i = 0; i < namenodes.size(); i++) {
				nameNodeId = (String) namenodes.get(i);

				// Configuration conf =
				// RemoteManager.getNameNodeConfiguration(nameNodeId);
				//
				// String httpAddress =
				// conf.get(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." +
				// nameNodeId);
				// if(httpAddress.endsWith("/")) httpAddress =
				// httpAddress.substring(0, httpAddress.length()-1);
				//
				// httpAddress += QueryIOConstants.GROUP_INFO_UPDATE_URL;
				//
				// if( ! httpAddress.contains("http")){
				// httpAddress = "http://" + httpAddress;
				// }
				//
				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("UGI Update URL for " +
				// httpAddress + ": " + httpAddress);
				//
				// try{
				// fireUpdateRequest(httpAddress,
				// QueryIOConstants.GROUP_INFO_REQUEST_HEADER_KEY,
				// SecurityHandler.encryptData(UGIProvider.getUserGroupInformation()));
				// }catch(Exception e){
				// AppLogger.getLogger().fatal("updateUserInformation() failed
				// with exception: " + e.getMessage(), e);
				// }

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Request UserGroup Mapping Request for Name Node Id " + nameNodeId);
				Node node = NodeDAO.getNode(connection, nameNodeId);
				Host host = HostDAO.getHostDetail(connection, node.getHostId());
				refreshNameNodeUserToGroupsMappings(host, node);
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("UGI updates sent to " + nameNodeId);

			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("updateUserInformation() failed with exception: " + e.getMessage(), e);
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public static void fireUpdateRequest(String remoteURL, String headerKey, String headerValue) throws Exception {
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

			httpCon.setRequestProperty(headerKey, headerValue);

			httpCon.connect();

			if (httpCon.getResponseCode() != HttpStatus.SC_OK) {
				throw new Exception("Improper response code received, code: " + httpCon.getResponseCode()
						+ ", message: " + httpCon.getResponseMessage());
			}
		} finally {
			if (httpCon != null)
				httpCon.disconnect();
		}
	}

	public static void refreshNameNodeUserToGroupsMappings(Host host, Node node) {
		QueryIOAgentManager.refreshUserToGroupMapping(host, node);
	}
}
