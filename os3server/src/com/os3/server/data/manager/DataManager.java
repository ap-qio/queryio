package com.os3.server.data.manager;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.os3.server.common.StreamWriteStatus;
import com.os3.server.hadoop.BucketFilter;
import com.os3.server.hadoop.DFSManager;
import com.queryio.common.HadoopConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.util.AppLogger;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;

public class DataManager {
	protected final static Logger LOGGER = Logger.getLogger(DataManager.class);

	public static boolean isFile(FileSystem dfs, Path path) throws IOException {
		return DFSManager.isFile(dfs, path);
	}

	public static boolean isValidBucketName(String bucketName) {
		return DFSManager.isValidBucketName(bucketName);
	}

	public static boolean isBucketEmpty(FileSystem dfs, String bucketName) throws IOException {
		return DFSManager.isBucketEmpty(dfs, bucketName);
	}

	public static boolean doesBucketExists(FileSystem dfs, String bucketName) throws IOException {
		return DFSManager.doesBucketExists(dfs, bucketName);
	}

	public static boolean doesObjectExist(FileSystem dfs, String bucketName, String objectName) throws IOException {
		return DFSManager.doesObjectExist(dfs, bucketName, objectName);
	}

	public static boolean doesPathExist(FileSystem dfs, Path path) throws IOException {
		return DFSManager.doesPathExist(dfs, path);
	}

	public static FileStatus getPathStatus(FileSystem dfs, Path path) throws IOException {
		return DFSManager.getPathStatus(dfs, path);
	}

	public static void setOwner(FileSystem dfs, Path path, String owner, String group) throws IOException {
		DFSManager.setOwner(dfs, path, owner, group);
	}

	@SuppressWarnings("PMD.AvoidUsingShortType")
	public static void setPermissions(FileSystem dfs, Path path, short permissions) throws IOException {
		DFSManager.setPermissions(dfs, path, permissions);
	}

	public static FileStatus getObjectStatus(FileSystem dfs, String bucketName, String objectName) throws IOException {
		return DFSManager.getObjectStatus(dfs, bucketName, objectName);
	}

	public static InputStream getObjectDataInputStream(FileSystem dfs, String bucketName, String objectName,
			String compressionType, String encryptionType) throws Exception {
		return DFSManager.getObjectDataInputStream(dfs, bucketName, objectName, compressionType, encryptionType);
	}

	public static InputStream getObjectDataInputStream(FileSystem dfs, String bucketName, String objectName,
			String compressionType, String encryptionType, String checkSum) throws Exception {
		return DFSManager.getObjectDataInputStream(dfs, bucketName, objectName, compressionType, encryptionType);
	}

	public static FileStatus[] getObjectList(FileSystem dfs, String bucketName, BucketFilter filter)
			throws IOException {
		return DFSManager.getObjectList(dfs, bucketName, filter);
	}

	public static ArrayList getAllDirStats(FileSystem dfs) throws IOException {
		return DFSManager.getAllDirStats(dfs);
	}

	public static boolean deleteBucket(FileSystem dfs, String bucketName) throws IOException {
		return DFSManager.deleteBucket(dfs, bucketName);
	}

	public static boolean deleteObject(FileSystem dfs, String bucketName, String objectName) throws IOException {
		return DFSManager.deleteObject(dfs, bucketName, objectName);
	}

	@SuppressWarnings("PMD.AvoidUsingShortType")
	public static boolean createBucket(FileSystem dfs, String bucketName, String username, String group,
			short permission) throws IOException {
		return DFSManager.createBucket(dfs, bucketName, username, group, permission);
	}

	public static boolean createBucket(FileSystem dfs, String bucketName, String username, String group)
			throws IOException {
		return DFSManager.createBucket(dfs, bucketName, username, group);
	}

	public static StreamWriteStatus createObject(String username, String group, FileSystem dfs, String bucketName,
			String objectName, long contentLength, InputStream inputStream, List<UserDefinedTag> tags,
			String compressionType, String encryptionType) throws IOException, NoSuchAlgorithmException {
		return DFSManager.createObject(username, group, dfs, bucketName, objectName, contentLength, inputStream, tags,
				compressionType, encryptionType);
	}

	@SuppressWarnings("PMD.AvoidUsingShortType")
	public static StreamWriteStatus createObject(String username, String group, short permission, FileSystem dfs,
			String bucketName, String objectName, long contentLength, InputStream inputStream,
			List<UserDefinedTag> tags, String compressionType, String encryptionType)
			throws IOException, NoSuchAlgorithmException {
		return DFSManager.createObject(username, group, permission, dfs, bucketName, objectName, contentLength,
				inputStream, tags, compressionType, encryptionType);
	}

	@SuppressWarnings("unchecked")
	public static Map getObjectMetadata(String filePath) throws Exception {
		Map map = new HashMap();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = UserDefinedTagResourceFactory.getConnectionWithPoolInit(HadoopConstants.getHadoopConf(), true);

			IDataTagParser tagParser = UserDefinedTagResourceFactory
					.getParserFromConstructor(HadoopConstants.getHadoopConf(), filePath, null, null);

			stmt = connection.prepareStatement("SELECT * FROM "
					+ UserDefinedTagResourceFactory.getTableName(tagParser, filePath) + " WHERE FILEPATH=?");
			stmt.setString(1, filePath);

			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(stmt);
			ResultSetMetaData rsmd = rs.getMetaData();

			if (rs.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					map.put(rsmd.getColumnName(i), rs.getObject(rsmd.getColumnName(i)));
				}
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closePreparedStatement(stmt);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception ex) {
				AppLogger.getLogger().fatal("Error closing database connection.", ex);
			}
		}
		return map;
	}
}
