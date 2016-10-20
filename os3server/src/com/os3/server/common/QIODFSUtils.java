package com.os3.server.common;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;

public class QIODFSUtils {
	public static FileSystem getFileSystemAs(final String username, final String group,
			final Configuration conf) throws IOException, InterruptedException {
		final UserGroupInformation ugi = UserGroupInformation.createUserForTesting(
				username, new String[] { group });
		
		return ugi.doAs(new PrivilegedExceptionAction<FileSystem>() {
			@Override
			public FileSystem run() throws Exception {
				return FileSystem.get(conf);
			}
		});
	}
}
