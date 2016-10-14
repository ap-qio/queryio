package com.queryio.common.remote;

import java.io.FileOutputStream;
import java.io.IOException;

public class QueryIOTransferWrapper {
	byte[] b;
	
	FileOutputStream _STREAM;
	public QueryIOTransferWrapper(FileOutputStream fis) {
		_STREAM = fis; 
	}
	public void write(byte[] b, int offset, int length) throws IOException{
		_STREAM.write(b, offset, length);
	}
	public void close() throws IOException{
		_STREAM.close();
	}
}
