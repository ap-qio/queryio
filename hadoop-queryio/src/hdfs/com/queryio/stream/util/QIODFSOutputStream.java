package com.queryio.stream.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.crypto.Cipher;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.json.simple.JSONObject;

import com.queryio.common.QueryIOConstants;

public class QIODFSOutputStream extends OutputStream {
	TagHandler tagHandler;
	OutputStream out;
	
	public QIODFSOutputStream(FileSystem dfs, DFSOutputStream dfsOut, String compressionType, String encryptionType, JSONObject tagsJSON) throws Exception {
		tagHandler = new TagHandler(dfs, dfsOut.getConfiguration(), dfsOut.getFilePath(), dfsOut.getExternalTags(), compressionType, encryptionType, tagsJSON);
		
		int compressionTypeValue = EncryptionHandler.getCompressionTypeValue(compressionType);
		boolean encryptionTypeValue = EncryptionHandler.getEncryptionTypeValue(encryptionType);
		
		out = new EncryptionHandler(Cipher.ENCRYPT_MODE,
				encryptionTypeValue, dfsOut.getConfiguration().get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY))
				.getCompressedCipherOutputStream(dfsOut,
						compressionTypeValue);
	}

	public void write(int arg0) throws IOException {
		tagHandler.write(arg0);
		out.write(arg0);
	}
	
	public void write(byte[] buf, int off, int len) throws IOException {
		tagHandler.write(buf, off, len);
		out.write(buf, off, len);
	}

	public void close() throws IOException {
		out.close();
		tagHandler.close();
	}
}
