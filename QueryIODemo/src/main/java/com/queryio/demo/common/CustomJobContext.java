package com.queryio.demo.common;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSInputStream;
import org.apache.hadoop.hdfs.DistributedFileSystem;

public class CustomJobContext {
	private static FileSystem dfs = null;
	private static Configuration conf = null;
	private static final Log LOG = LogFactory.getLog(CustomJobContext.class);

	public static void initialize(Configuration conf) throws IOException {
		LOG.info("Initializing CustomJobContext..");
		CustomJobContext.conf = conf;
		initialize();
	}

	private static void initialize() throws IOException {
		dfs = FileSystem.get(conf);
	}

	public static InputStream getInputStream(FileStatus fileStatus) throws IOException {
		if (dfs == null) {
			initialize();
		}
		String src = fileStatus.getPath().toUri().getPath();
		LOG.info("Reading filePath: " + src);
		DistributedFileSystem fs = (DistributedFileSystem) dfs;
		InputStream dfsInputStream = null;
		InputStream qioInputStream = null;
		try {
			dfsInputStream = (DFSInputStream) fs.getClient().open(src);
			qioInputStream = new CustomQIODFSInputStream(dfsInputStream, conf, src);
		} catch (Exception e) {
			LOG.fatal(e.getMessage(), e);
			if (dfsInputStream != null) {
				dfsInputStream.close();
			}
			throw new IOException(e.getMessage());
		}
		return qioInputStream;
	}

	public static FileStatus getFileStatus(Path path) throws IOException {
		if (dfs == null) {
			initialize();
		}
		return dfs.getFileStatus(path);
	}

	public static void shutdown() throws IOException {
		if (dfs != null) {
			dfs.close();
		}
	}
}
