package com.queryio.ftpserver.core;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.log4j.Logger;

public class HdfsFileSystemView implements FileSystemView {
	protected static final Logger logger = Logger.getLogger(HdfsFileSystemView.class);
	/* 16 */private String currDir = "/";
	private HdfsUser user;

	protected HdfsFileSystemView(HdfsUser user) throws FtpException {
		/* 25 */if (user == null) {
			/* 26 */throw new IllegalArgumentException("user can not be null");
		}
		/* 28 */if (user.getHomeDirectory() == null) {
			/* 29 */throw new IllegalArgumentException(
					"HdfsUser home directory can not be null");
		}

		logger.debug("HdfsFileSystemView created for user: " + user.getName());
		
		/* 36 */this.user = user;
	}

	public FtpFile getHomeDirectory() {
		return new HdfsFtpFile(this.user.getHomeDirectory(), this.user);
	}

	public FtpFile getWorkingDirectory() {
		return new HdfsFtpFile(this.currDir, this.user);
	}

	public FtpFile getFile(String file) {
		String path;
		/* 60 */if (file.startsWith("/")) {
			/* 61 */path = file;
		} else {
			if (this.currDir.length() > 1)
				/* 63 */path = this.currDir + "/" + file;
			else
				/* 65 */path = "/" + file;
		}
		/* 67 */return new HdfsFtpFile(path, this.user);
	}

	public boolean changeWorkingDirectory(String dir) {
		String path;
		if (dir.startsWith("/")) {
			/* 76 */path = dir;
		} else {
			if (this.currDir.length() > 1)
				/* 78 */path = this.currDir + "/" + dir;
			else
				/* 80 */path = "/" + dir;
		}
		/* 82 */HdfsFtpFile file = new HdfsFtpFile(path, this.user);
		/* 83 */if ((file.isDirectory()) && (file.isReadable())) {
			/* 84 */this.currDir = path;
			/* 85 */return true;
		}
		/* 87 */return false;
	}

	public boolean isRandomAccessible() {
		/* 95 */return true;
	}

	public void dispose() {
	}
}