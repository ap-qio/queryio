package com.queryio.ftpserver.core;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.log4j.Logger;

public class HdfsFileSystemView implements FileSystemView {
	protected static final Logger LOGGER = Logger.getLogger(HdfsFileSystemView.class);
	private String currDir = "/";
	private HdfsUser user;

	protected HdfsFileSystemView(HdfsUser user) throws FtpException {
		if (user == null) {
			throw new IllegalArgumentException("user can not be null");
		}
		if (user.getHomeDirectory() == null) {
			throw new IllegalArgumentException("HdfsUser home directory can not be null");
		}

		LOGGER.debug("HdfsFileSystemView created for user: " + user.getName());

		this.user = user;
	}

	public FtpFile getHomeDirectory() {
		return new HdfsFtpFile(this.user.getHomeDirectory(), this.user);
	}

	public FtpFile getWorkingDirectory() {
		return new HdfsFtpFile(this.currDir, this.user);
	}

	public FtpFile getFile(String file) {
		String path;
		if (file.startsWith("/")) {
			path = file;
		} else {
			if (this.currDir.length() > 1)
				path = this.currDir + "/" + file;
			else
				path = "/" + file;
		}
		return new HdfsFtpFile(path, this.user);
	}

	public boolean changeWorkingDirectory(String dir) {
		String path;
		if (dir.startsWith("/")) {
			path = dir;
		} else {
			if (this.currDir.length() > 1)
				path = this.currDir + "/" + dir;
			else
				path = "/" + dir;
		}
		HdfsFtpFile file = new HdfsFtpFile(path, this.user);
		if ((file.isDirectory()) && (file.isReadable())) {
			this.currDir = path;
			return true;
		}
		return false;
	}

	public boolean isRandomAccessible() {
		return true;
	}

	public void dispose() {
	}
}