package com.queryio.core.requestprocessor;

import java.io.OutputStream;
import java.net.URI;
import java.sql.Connection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
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

public class GetFileStatusRequest extends RequestProcessorCore {
	private String nameNodeId;
	private String fsDefaultName;
	private FileSystem dfs;

	private FileStatus status = null;

	public GetFileStatusRequest(String user, Path path, String nameNodeId, String fsDefaultName) {
		super(user, null, path);
		this.fsDefaultName = fsDefaultName;
		this.nameNodeId = nameNodeId;
	}

	@Override
	public void process() throws Exception {
		this.successful = false;

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("GetFileRequest, user: " + this.user);

		Connection connection = null;
		OutputStream os = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			if (EnvironmentalConstants.isUseKerberos()) {
				User us = UserDAO.getUserDetail(connection, user);

				Configuration conf = ConfigurationManager.getKerberosConfiguration(connection, nameNodeId);

				try {
					UserGroupInformation.setConfiguration(conf);
					// UserGroupInformation.getLoginUser(us.getUserName(),
					// SecurityHandler.decryptData(us.getPassword()));

					dfs = FileSystem.get(new URI(fsDefaultName), conf);
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Could not authenticate user with kerberos, error: " + e.getMessage(),
							e);
					return;
				}
			} else {
				Configuration conf = ConfigurationManager.getConfiguration(connection, nameNodeId);
				dfs = QIODFSUtils.getFileSystemAs(user, group, conf, new URI(fsDefaultName));
			}

			status = dfs.getFileStatus(this.path);
		} finally {
			try {
				if (dfs != null)
					dfs.close();
				if (os != null)
					os.flush();
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error flushing input stream.", e);
			}
			try {
				if (os != null)
					os.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing input stream.", e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}

	public FileStatus getStatus() {
		return status;
	}
}
