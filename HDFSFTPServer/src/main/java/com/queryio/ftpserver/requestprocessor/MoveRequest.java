package com.queryio.ftpserver.requestprocessor;

import java.io.IOException;

import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.queryio.common.DFSMap;
import com.queryio.ftpserver.core.HdfsUser;

public class MoveRequest extends RequestProcessorCore {
	private FtpFile fileObject;

	public MoveRequest(HdfsUser user, Path path, FtpFile fileObject) {
		super(user, path);
		this.fileObject = fileObject;
	}

	public Object process() throws IllegalArgumentException, IOException {
		final FileSystem dfs = DFSMap.getDFSForUser(user.getName());
		return dfs.rename(path, new Path(fileObject.getAbsolutePath()));
	}
}
