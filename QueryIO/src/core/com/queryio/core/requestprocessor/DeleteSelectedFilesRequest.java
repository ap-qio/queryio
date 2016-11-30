package com.queryio.core.requestprocessor;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.User;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.utils.QIODFSUtils;

public class DeleteSelectedFilesRequest extends RequestProcessorCore {
	private String nodeId;

	public DeleteSelectedFilesRequest(String nodeId, String user, Path path) {
		super(user, null, path);
		this.nodeId = nodeId;
	}

	public void process() throws Exception {
		this.successful = false;

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("In DeleteSelectedFilesRequest, user: " + this.user);

		Connection connection = null;
		FileSystem dfs = null;
		try {

			connection = CoreDBManager.getQueryIODBConnection();

			Configuration config = ConfigurationManager.getConfiguration(connection, nodeId);

			if (EnvironmentalConstants.isUseKerberos()) {
				User us = UserDAO.getUserDetail(connection, user);

				Configuration conf = ConfigurationManager.getKerberosConfiguration(connection, nodeId);
				Iterator<Entry<String, String>> i = conf.iterator();
				while (i.hasNext()) {
					Entry<String, String> e = i.next();
					config.set(e.getKey(), e.getValue());
				}
				try {
					UserGroupInformation.setConfiguration(conf);
					// UserGroupInformation.getLoginUser(us.getUserName(),
					// SecurityHandler.decryptData(us.getPassword()));

					dfs = FileSystem.get(config);
					dfs.getStatus();
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Could not authenticate user with kerberos, error: " + e.getMessage(),
							e);
					return;
				}
			} else {
				dfs = QIODFSUtils.getFileSystemAs(user, group, config);
			}

			this.successful = dfs.delete(this.path, true);

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Path: " + dfs.getFileStatus(new Path("/")).getPath());
		} finally {
			try {
				if (dfs != null)
					dfs.close();
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
}
