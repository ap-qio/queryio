package com.queryio.ftpserver.requestprocessor;

import org.apache.hadoop.fs.Path;

import com.queryio.ftpserver.core.HdfsUser;

public abstract class RequestProcessorCore {
	protected HdfsUser user = null;
	protected Path path = null;
	
	public RequestProcessorCore(HdfsUser user, Path path) {
		this.user = user;
		this.path = path;
	}

	public abstract Object process() throws Exception;
}
