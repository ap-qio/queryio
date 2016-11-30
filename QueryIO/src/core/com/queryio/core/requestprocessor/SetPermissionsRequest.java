package com.queryio.core.requestprocessor;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.security.UserGroupInformation;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.bean.User;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.utils.QIODFSUtils;

public class SetPermissionsRequest extends RequestProcessorCore {
	private int permissions;
	private String owner;
	private String group;
	private boolean recursive;
	private String nodeId;

	public SetPermissionsRequest(String nodeId, String user, Path path, String owner, String group, int permissions,
			boolean recursive) {
		super(user, null, path);
		this.nodeId = nodeId;
		this.owner = owner;
		this.group = group;
		this.permissions = permissions;
		this.recursive = recursive;
	}

	public void process() throws Exception {
		this.successful = false;

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("In SetPermissionsRequest, user: " + this.user);

		Connection connection = null;

		FileSystem dfs = null;

		try {
			connection = CoreDBManager.getQueryIODBConnection();

			Node node = NodeDAO.getNode(connection, nodeId);
			if (node == null)
				return;

			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList list = new ArrayList();
			list.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList result = QueryIOAgentManager.getConfig(host, list, node, "core-site.xml");
			if (result == null || result.size() == 0) {
				return;
			}

			String fsDefaultName = (String) result.get(0);

			if (EnvironmentalConstants.isUseKerberos()) {
				User us = UserDAO.getUserDetail(connection, user);

				Configuration conf = ConfigurationManager.getKerberosConfiguration(connection, nodeId);

				try {
					UserGroupInformation.setConfiguration(conf);
					// UserGroupInformation.getLoginUser(us.getUserName(),
					// SecurityHandler.decryptData(us.getPassword()));

					dfs = FileSystem.get(new URI(fsDefaultName), conf);
					dfs.getStatus();
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Could not authenticate user with kerberos, error: " + e.getMessage(),
							e);
					return;
				}
			} else {
				Configuration conf = ConfigurationManager.getConfiguration(connection, nodeId);
				dfs = QIODFSUtils.getFileSystemAs(user, group, conf, new URI(fsDefaultName));
			}

			if (this.recursive) {
				setPermissions(dfs, this.path, this.owner, this.group, this.permissions);
			} else {
				dfs.setOwner(this.path, this.owner, this.group);
				FsPermission fsP = parsePermissions(this.permissions);

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger()
							.debug("Set permissions request for this.path: " + this.path + " this.owner: " + this.owner
									+ " this.group: " + this.group + " this.permissions: " + this.permissions
									+ " new FsPermission(this.permissions): " + fsP);
				dfs.setPermission(this.path, fsP);
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Root: " + dfs.getFileStatus(new Path("/")).getPermission() + ", owner: "
						+ dfs.getFileStatus(new Path("/")).getOwner());

			this.successful = true;
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

	private void setPermissions(FileSystem dfs, Path path, String owner, String group, int permissions)
			throws IOException {
		dfs.setOwner(path, owner, group);

		FsPermission fsP = parsePermissions(permissions);

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger()
					.debug("Set permissions request for this.path: " + this.path + " this.owner: " + this.owner
							+ " this.group: " + this.group + " this.permissions: " + this.permissions
							+ " new FsPermission(this.permissions): " + fsP);
		dfs.setPermission(path, fsP);

		if (dfs.getFileStatus(path).isDirectory()) {
			FileStatus[] statusList = dfs.listStatus(path);
			for (int i = 0; i < statusList.length; i++) {
				setPermissions(dfs, statusList[i].getPath(), owner, group, permissions);
			}
		}
	}

	private FsPermission parsePermissions(int permissions) {
		FsAction u = getAction(permissions / 100);
		int t = permissions % 100;
		FsAction g = getAction(t / 10);
		FsAction o = getAction(t % 10);

		return new FsPermission(u, g, o, false);
	}

	private FsAction getAction(int i) {
		switch (i) {
		case 0:
			return FsAction.NONE;
		case 1:
			return FsAction.EXECUTE;
		case 2:
			return FsAction.WRITE;
		case 3:
			return FsAction.WRITE_EXECUTE;
		case 4:
			return FsAction.READ;
		case 5:
			return FsAction.READ_EXECUTE;
		case 6:
			return FsAction.READ_WRITE;
		case 7:
			return FsAction.ALL;
		default:
			return FsAction.READ;
		}
	}
}