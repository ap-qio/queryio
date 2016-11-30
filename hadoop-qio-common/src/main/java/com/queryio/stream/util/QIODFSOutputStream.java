package com.queryio.stream.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.json.simple.JSONObject;

import com.queryio.common.QueryIOConstants;
import com.queryio.plugin.datatags.UserDefinedTag;

public class QIODFSOutputStream extends OutputStream {
	TagHandler tagHandler;
	OutputStream out;
	private ArrayList<UserDefinedTag> externalTags = new ArrayList<UserDefinedTag>();

	public QIODFSOutputStream(FileSystem dfs, DFSOutputStream dfsOut, String compressionType, String encryptionType,
			JSONObject tagsJSON, String filePath) throws Exception {
		tagHandler = new TagHandler(dfs, dfs.getConf(), filePath, getExternalTags(), compressionType, encryptionType,
				tagsJSON);

		int compressionTypeValue = EncryptionHandler.getCompressionTypeValue(compressionType);
		boolean encryptionTypeValue = EncryptionHandler.getEncryptionTypeValue(encryptionType);

		out = new EncryptionHandler(Cipher.ENCRYPT_MODE, encryptionTypeValue,
				dfs.getConf().get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY))
						.getCompressedCipherOutputStream(dfsOut, compressionTypeValue);
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

	/*
	 * @QUERYIO@
	 */
	public void addTag(UserDefinedTag tag) {
		externalTags.add(tag);
	}

	/*
	 * @QUERYIO@
	 */
	public void addTags(List<UserDefinedTag> tags) {
		externalTags.addAll(tags);
	}

	/*
	 * @QUERYIO@
	 */
	public ArrayList<UserDefinedTag> getExternalTags() {
		return this.externalTags;
	}
}
