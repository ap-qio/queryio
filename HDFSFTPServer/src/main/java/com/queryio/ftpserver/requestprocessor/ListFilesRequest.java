package com.queryio.ftpserver.requestprocessor;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.queryio.common.DFSMap;
import com.queryio.ftpserver.core.HdfsFtpFile;
import com.queryio.ftpserver.core.HdfsUser;

public class ListFilesRequest extends RequestProcessorCore{
	
	public ListFilesRequest(HdfsUser user, Path path){
		super(user, path);
	}
	
	public List<FtpFile> process() throws IOException {
		final FileSystem dfs = DFSMap.getDFSForUser(user.getName());
	
		final FileStatus fileStats[] = dfs.listStatus(path);
		final List<FtpFile> fileObjects = new LinkedList<FtpFile>();
		
		for (int i = 0; i < fileStats.length; i++) {
			fileObjects.add(new HdfsFtpFile(fileStats[i].getPath().toString(), user));
		}
		
		return fileObjects;
	}
}
