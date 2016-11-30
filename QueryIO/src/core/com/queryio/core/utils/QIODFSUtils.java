package com.queryio.core.utils;

import java.io.IOException;
import java.net.URI;
import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.security.UserGroupInformation;

@SuppressWarnings("PMD.AvoidUsingShortType")
public class QIODFSUtils {

	public static FileSystem getFileSystemAs(final String username, final String group, final Configuration conf,
			final URI uri) throws IOException, InterruptedException {
		final UserGroupInformation ugi = UserGroupInformation.createRemoteUser(username);
		return ugi.doAs(new PrivilegedExceptionAction<FileSystem>() {
			@Override
			public FileSystem run() throws Exception {
				return FileSystem.get(uri, conf);
			}
		});
	}

	public static FileSystem getFileSystemAs(final String username, final String group, final Configuration conf)
			throws IOException, InterruptedException {

		final UserGroupInformation ugi = UserGroupInformation.createRemoteUser(username);

		return ugi.doAs(new PrivilegedExceptionAction<FileSystem>() {
			@Override
			public FileSystem run() throws Exception {
				return FileSystem.get(conf);
			}
		});
	}

	public static DFSClient getDFSClient(final String username, final String group, final Configuration conf)
			throws IOException, InterruptedException {

		final UserGroupInformation ugi = UserGroupInformation.createRemoteUser(username);

		return ugi.doAs(new PrivilegedExceptionAction<DFSClient>() {
			@Override
			public DFSClient run() throws Exception {
				return new DFSClient(conf);
			}
		});
	}

	public static FsPermission parsePermissions(short permissions) {
		FsAction u = getAction(permissions / 100);
		int t = permissions % 100;
		FsAction g = getAction(t / 10);
		FsAction o = getAction(t % 10);

		return new FsPermission(u, g, o, false);
	}

	public static FsAction getAction(int i) {
		switch (i) {
		case 0:
			return FsAction.NONE;
		case 1:
			return FsAction.EXECUTE;
		case 2:
			return FsAction.WRITE;
		case 3:
			return FsAction.WRITE_EXECUTE;
		case 4:
			return FsAction.READ;
		case 5:
			return FsAction.READ_EXECUTE;
		case 6:
			return FsAction.READ_WRITE;
		case 7:
			return FsAction.ALL;
		default:
			return FsAction.READ;
		}
	}

}
