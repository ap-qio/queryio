package com.queryio.core.requestprocessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.security.UserGroupInformation;

import com.google.gson.Gson;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.bean.User;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.dao.HostDAO;
import com.queryio.core.dao.NodeDAO;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.monitor.beans.SummaryTable;
import com.queryio.core.namespace.NamespaceConflict;
import com.queryio.core.utils.QIODFSUtils;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class NamespaceVSDBDiagnosisRequest extends RequestProcessorCore {
	SummaryTable filesList = new SummaryTable();
	String nodeId;

	static String queryStart = "SELECT " + ColumnConstants.COL_TAG_VALUES_FILEPATH + ","
			+ ColumnConstants.COL_TAG_VALUES_LENGTH + "," + ColumnConstants.COL_TAG_VALUES_OWNER + ","
			+ ColumnConstants.COL_TAG_VALUES_USERGROUP + "," + ColumnConstants.COL_TAG_VALUES_PERMISSION + ","
			+ ColumnConstants.COL_TAG_VALUES_ACCESSTIME + "," + ColumnConstants.COL_TAG_VALUES_BLOCKSIZE + ","
			+ ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME + "," + ColumnConstants.COL_TAG_VALUES_REPLICATION
			+ " FROM ";
	static String queryEnd = " WHERE FILEPATH=?";

	private long startIndex;

	private long curIndex;

	private long endIndex;
	private String filePath;

	Gson gson = new Gson();

	public NamespaceVSDBDiagnosisRequest(String nodeId, String user, long startIndex, long endIndex, String filePath) {
		super(user, null, null);
		this.nodeId = nodeId;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.filePath = filePath;
	}

	public void process() throws Exception {
		Connection connection = null;

		FileSystem dfs = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();

			Node node = NodeDAO.getNode(connection, nodeId);
			if (node == null)
				return;

			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList list = new ArrayList();
			list.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList result = QueryIOAgentManager.getConfig(host, list, node, "core-site.xml");
			if (result == null || result.size() == 0) {
				return;
			}

			String fsDefaultName = (String) result.get(0);
			Configuration conf = null;
			if (EnvironmentalConstants.isUseKerberos()) {
				User us = UserDAO.getUserDetail(connection, user);

				conf = ConfigurationManager.getKerberosConfiguration(connection, nodeId);

				try {
					UserGroupInformation.setConfiguration(conf);
					// UserGroupInformation.getLoginUser(us.getUserName(),
					// SecurityHandler.decryptData(us.getPassword()));

					dfs = FileSystem.get(new URI(fsDefaultName), conf);
					dfs.getStatus();
				} catch (Exception e) {
					AppLogger.getLogger().fatal("Could not authenticate user with kerberos, error: " + e.getMessage(),
							e);
					return;
				}
			} else {
				conf = ConfigurationManager.getConfiguration(connection, nodeId);
				dfs = QIODFSUtils.getFileSystemAs(user, group, conf, new URI(fsDefaultName));
			}

			// DistributedFileSystem fs = (DistributedFileSystem) dfs;
			// fs.getClient().setSafeMode(SafeModeAction.SAFEMODE_ENTER);

			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();

			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);

			// Perform diagnosis

			diagnose(dfs, conf, "/", bw);

		} finally {
			try {
				if (bw != null)
					bw.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}

			try {
				if (dfs != null)
					dfs.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}

			try {
				if (connection != null)
					CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	private void diagnose(FileSystem dfs, Configuration conf, String src, BufferedWriter bw) throws Exception {
		Path path = new Path(src);
		FileStatus stat = dfs.getFileStatus(path);
		if (!stat.isDirectory()) {
			// Perform diagnosis

			if (curIndex > endIndex) {
				return;
			}

			if (curIndex >= startIndex) {

				Map<String, Object> metadata = getMetadata(conf, src);

				NamespaceConflict conflict = new NamespaceConflict();

				if (metadata == null) {
					conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_MISSING_ENTRY_IN_DATABASE);
				} else {
					try {

						Timestamp accessTime = (Timestamp) metadata.get(ColumnConstants.COL_TAG_VALUES_ACCESSTIME);
						if (accessTime != null) {
							Calendar cal = Calendar.getInstance();
							cal.setTimeInMillis(stat.getAccessTime());
							cal.set(Calendar.MILLISECOND, 0);
							if (cal.getTimeInMillis() != accessTime.getTime()) {
								conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
								conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_ACCESSTIME,
										String.valueOf(stat.getAccessTime()), String.valueOf(accessTime.getTime()));
							}
						} else {
							conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
							conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_ACCESSTIME,
									String.valueOf(stat.getAccessTime()), "N/A");
						}

						Timestamp modificationTime = (Timestamp) metadata
								.get(ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME);
						if (modificationTime != null) {
							Calendar cal = Calendar.getInstance();
							cal.setTimeInMillis(stat.getModificationTime());
							cal.set(Calendar.MILLISECOND, 0);
							if (cal.getTimeInMillis() != modificationTime.getTime()) {
								conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
								conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME,
										String.valueOf(stat.getModificationTime()),
										String.valueOf(modificationTime.getTime()));
							}
						} else {
							conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
							conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME,
									String.valueOf(stat.getModificationTime()), "N/A");
						}

						Long blockSize = (Long) metadata.get(ColumnConstants.COL_TAG_VALUES_BLOCKSIZE);
						if (blockSize != null) {
							if (stat.getBlockSize() != blockSize) {
								conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
								conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_BLOCKSIZE,
										String.valueOf(stat.getBlockSize()), String.valueOf(blockSize));
							}
						} else {
							conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
							conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_BLOCKSIZE,
									String.valueOf(stat.getBlockSize()), "N/A");
						}

						Integer replication = (Integer) metadata.get(ColumnConstants.COL_TAG_VALUES_REPLICATION);
						if (replication != null) {
							if (stat.getReplication() != replication) {
								conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
								conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_REPLICATION,
										String.valueOf(stat.getReplication()), String.valueOf(replication));
							}
						} else {
							conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
							conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_REPLICATION,
									String.valueOf(stat.getReplication()), "N/A");
						}

						Long length = (Long) metadata.get(ColumnConstants.COL_TAG_VALUES_LENGTH);
						if (length != null) {
							if (stat.getLen() != length) {
								conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
								conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_LENGTH,
										String.valueOf(stat.getLen()), String.valueOf(length));
							}
						} else {
							conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
							conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_LENGTH,
									String.valueOf(stat.getLen()), "N/A");
						}

						String owner = (String) metadata.get(ColumnConstants.COL_TAG_VALUES_OWNER);
						if (owner != null) {
							if (!stat.getOwner().equals(owner)) {
								conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
								conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_OWNER,
										String.valueOf(stat.getOwner()), String.valueOf(owner));
							}
						} else {
							conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
							conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_OWNER,
									String.valueOf(stat.getOwner()), "N/A");
						}

						String userGroup = (String) metadata.get(ColumnConstants.COL_TAG_VALUES_USERGROUP);
						if (userGroup != null) {
							if (!stat.getGroup().equals(userGroup)) {
								conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
								conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_USERGROUP,
										String.valueOf(stat.getGroup()), String.valueOf(userGroup));
							}
						} else {
							conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
							conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_USERGROUP,
									String.valueOf(stat.getGroup()), "N/A");
						}

						String permission = (String) metadata.get(ColumnConstants.COL_TAG_VALUES_PERMISSION);
						if (permission != null) {
							if (!stat.getPermission().toString().equals(permission)) {
								conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
								conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_PERMISSION,
										String.valueOf(stat.getPermission()), String.valueOf(permission));
							}
						} else {
							conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE);
							conflict.addConflictInfo(ColumnConstants.COL_TAG_VALUES_PERMISSION,
									String.valueOf(stat.getPermission()), "N/A");
						}

					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}

				if (conflict.getConflictType() != NamespaceConflict.CONFLICT_TYPE_NONE) {
					// Conflict detected

					conflict.setFilePath(src);
					conflict.setConflictTypeDescription();

					AppLogger.getLogger().fatal(
							"Conflict: " + conflict.getFilePath() + ", " + conflict.getConflictTypeDescription());

					// Write conflict info to file
					bw.write(gson.toJson(conflict));
					bw.newLine();
					bw.flush();
				}
			}

			curIndex++;

		} else {
			FileStatus[] stats = dfs.listStatus(path);
			for (int i = 0; i < stats.length; i++) {
				if (!src.endsWith("/"))
					src += "/";

				if (src.startsWith("/tmp/")) {
					continue;
				}

				diagnose(dfs, conf, src + stats[i].getPath().getName(), bw);
			}
		}
	}

	public Map<String, Object> getMetadata(Configuration conf, String filePath) throws Exception {
		Map<String, Object> metadata = null;

		if (conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID) == null) {
			AppLogger.getLogger().fatal(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID + " property is absent in config.");
		} else {
			metadata = getObjectMetadata(conf, filePath, TableConstants.TABLE_HDFS_METADATA);

			if (metadata == null) {
				metadata = getObjectMetadata(conf, filePath,
						("DATATAGS_" + UserDefinedTagUtils.getFileExtension(filePath)).toUpperCase());
			}

			if (metadata == null) {
				metadata = getObjectMetadata(conf, filePath, null);
			}
		}

		return metadata;
	}

	public Map<String, Object> getObjectMetadata(Configuration conf, String filePath, String tableName)
			throws Exception {
		Map<String, Object> map = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet res = null;
		IDataTagParser tagParser = null;
		try {
			String connectionName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			connection = CoreDBManager.getCustomTagDBConnection(connectionName);

			if (tableName == null) {
				tagParser = UserDefinedTagResourceFactory.getParserFromConstructor(conf, filePath, null, null);

				tableName = UserDefinedTagResourceFactory.getTableName(tagParser, filePath);
			}

			DatabaseMetaData meta = connection.getMetaData();
			res = meta.getTables(null, null, null, new String[] { "TABLE" });
			while (res.next()) {
				if (res.getString("TABLE_NAME").toUpperCase().startsWith(tableName)) {
					stmt = connection.prepareStatement(queryStart + res.getString("TABLE_NAME") + queryEnd);

					stmt.setString(1, filePath);

					rs = DatabaseFunctions.getQueryResultsForPreparedStatement(stmt);
					ResultSetMetaData rsmd = rs.getMetaData();

					if (rs.next()) {
						map = new HashMap<String, Object>();
						for (int i = 1; i <= rsmd.getColumnCount(); i++) {
							map.put(rsmd.getColumnName(i).toUpperCase(), rs.getObject(rsmd.getColumnName(i)));
						}
						return map;
					}
				}
			}
		} finally {
			try {
				DatabaseFunctions.closeResultSet(rs);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				DatabaseFunctions.closeResultSet(res);
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

	public static void main(String[] args) {
		NamespaceConflict conflict = new NamespaceConflict();
		conflict.setFilePath("/path/to/file");
		conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_MISSING_ENTRY_IN_DATABASE);
		conflict.addConflictInfo("ACCESSTIME", "1", "2");
		conflict.addConflictInfo("MODIFICATIONTIME", "2", "3");
		conflict.addConflictInfo("LENGTH", "3", "4");
		conflict.addConflictInfo("BLOCKS", "4", "5");

		conflict.setConflictTypeDescription();

		System.out.println(new Gson().toJson(conflict));
	}
}
