package com.queryio.ftpserver.requestprocessor;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;

import com.queryio.common.DFSMap;
import com.queryio.ftpserver.core.HdfsUser;
import com.queryio.stream.util.QIODFSInputStream;

public class CreateInputStreamRequest extends RequestProcessorCore {
	public CreateInputStreamRequest(HdfsUser user, Path path) {
		super(user, path);
	}

	public Object process() throws IOException {
		final FileSystem dfs = DFSMap.getDFSForUser(user.getName());

		DFSInputStream dfsInputStream = null;
		InputStream qioInputStream = null;
		DistributedFileSystem fs = (DistributedFileSystem) dfs;
		try {
			dfsInputStream = (DFSInputStream) fs.getClient().open(this.path.toUri().getPath());
			qioInputStream = new QIODFSInputStream(dfsInputStream, fs, this.path.toUri().getPath());

			return qioInputStream;
		} catch (Exception e) {
			if (dfsInputStream != null) {
				dfsInputStream.close();
			}
			throw new IOException(e);
		}
	}
}
