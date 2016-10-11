package com.os3.server.common;

import org.apache.hadoop.fs.FileStatus;


public class FileStatusEx{
	FileStatus fileStatus;
	StreamWriteStatus streamWriteStatus;
		
	public FileStatus getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(FileStatus fileStatus) {
		this.fileStatus = fileStatus;
	}
	
	public StreamWriteStatus getStreamWriteStatus() {
		return streamWriteStatus;
	}

	public void setStreamWriteStatus(StreamWriteStatus streamWriteStatus) {
		this.streamWriteStatus = streamWriteStatus;
	}
}
