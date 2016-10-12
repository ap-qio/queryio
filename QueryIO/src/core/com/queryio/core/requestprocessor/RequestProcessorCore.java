package com.queryio.core.requestprocessor;

import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;

import com.queryio.common.util.AppLogger;

public abstract class RequestProcessorCore {
	String user = null;
	String group = null;
	Path path = null;
	boolean successful = true;

	public RequestProcessorCore(String user, String group, Path path) {
		this.user = user;
		this.group = group;
		this.path = path;
	}

	public abstract void process() throws Exception;

	public boolean isSuccessFul() {
		return this.successful;
	}

	protected FsPermission getPermissions(FileSystem dfs, Path path)
			throws IOException {
		return dfs.getFileStatus(path).getPermission();
	}

	protected boolean isFile(FileSystem dfs, Path path) {
		try {
			return dfs.isFile(path);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return false;
	}

	protected String getOwnerName(FileSystem dfs, Path path) {
		try {
			FileStatus fs = dfs.getFileStatus(path);
			return fs.getOwner();
		} catch (IOException e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return null;
	}

	protected String getGroupName(FileSystem dfs, Path path) {
		try {
			FileStatus fs = dfs.getFileStatus(path);
			return fs.getGroup();
		} catch (IOException e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return null;
	}

	protected boolean isReadable(FileSystem dfs, Path path) {
		try {
			FsPermission permissions = getPermissions(dfs, path);
			if (this.user.equals(getOwnerName(dfs, path))) {
				if (permissions.toString().substring(0, 1).equals("r")) {
					return true;
				}

			} else if (permissions.toString().substring(6, 7).equals("r")) {
				return true;
			}

			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	protected boolean isWritable(FileSystem dfs, Path path) {
		try {
			FsPermission permissions = getPermissions(dfs, path);
			if (this.user.equals(getOwnerName(dfs, path))) {
				if (permissions.toString().substring(1, 2).equals("w")) {
					return true;
				}

			} else if (permissions.toString().substring(7, 8).equals("w")) {
				return true;
			}

			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isWritable(dfs, getParent(path));
	}

	protected Path getParent(Path path) {
		String pathS = path.toString();
		String parentS = "/";
		int pos = pathS.lastIndexOf("/");
		if (pos > 0) {
			parentS = pathS.substring(0, pos);
		}
		return new Path(parentS);
	}
}
