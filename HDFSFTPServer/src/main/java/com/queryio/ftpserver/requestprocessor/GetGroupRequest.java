package com.queryio.ftpserver.requestprocessor;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.queryio.common.DFSMap;
import com.queryio.ftpserver.core.HdfsUser;

public class GetGroupRequest extends RequestProcessorCore{
	public GetGroupRequest(HdfsUser user, Path path){
		super(user, path);
	}
	
	public Object process() throws IOException{
		final FileSystem dfs = DFSMap.getDFSForUser(user.getName());
		return dfs.getFileStatus(path).getOwner();
	}
}
