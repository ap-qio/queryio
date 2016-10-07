package com.queryio.file.upload;

import org.apache.commons.fileupload.ProgressListener;

public class FileUploadListener implements ProgressListener {

	private volatile long 
    bytesRead = 0L,
    contentLength = 0L,
    item = 0L;
	private String ErrorMsg;
	private String status;
	
	
	

	public FileUploadListener() {
		super();
		this.status = "Started";
	}
	
	public void update(long aBytesRead, long aContentLength, int anItem) {
		bytesRead = aBytesRead;
		contentLength = aContentLength;
//		item = anItem;
		this.status="Uploading";
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Uploading File item :"+this.item+" bytes read :"+this.bytesRead+" of total conten length :"+this.contentLength);
	}   
	public void setItem(long anItem) {
		this.item = anItem;
	}
	
	public String getErrorMsg() {
		return ErrorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		ErrorMsg = errorMsg;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getBytesRead() {
			return bytesRead;
	}

	public long getContentLength() {
		return contentLength;
	}

	public long getItem() {
		return item;
	}
	
	
}
