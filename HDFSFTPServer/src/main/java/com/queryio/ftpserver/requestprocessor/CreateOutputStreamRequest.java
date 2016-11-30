package com.queryio.ftpserver.requestprocessor;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import com.queryio.common.DFSMap;
import com.queryio.ftpserver.core.HdfsUser;
import com.queryio.stream.util.EncryptionHandler;
import com.queryio.stream.util.QIODFSOutputStream;

public class CreateOutputStreamRequest extends RequestProcessorCore {

	public CreateOutputStreamRequest(HdfsUser user, Path path) {
		super(user, path);
	}

	public Object process() throws IOException {
		final FileSystem dfs = DFSMap.getDFSForUser(user.getName());

		DFSOutputStream dfsOutputStream = null;
		OutputStream qioOutputStream = null;
		DistributedFileSystem fs = (DistributedFileSystem) dfs;
		try {
			dfsOutputStream = (DFSOutputStream) fs.getClient().create(this.path.toUri().getPath(), true);
			qioOutputStream = new QIODFSOutputStream(dfs, dfsOutputStream, EncryptionHandler.NONE,
					EncryptionHandler.NONE, null, this.path.toUri().getPath());
		} catch (Exception e) {
			if (dfsOutputStream != null) {
				dfsOutputStream.close();
			}
			throw new IOException(e);
		}

		return qioOutputStream;
	}
}
