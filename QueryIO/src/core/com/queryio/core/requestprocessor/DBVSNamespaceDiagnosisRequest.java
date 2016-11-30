package com.queryio.core.requestprocessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.ArrayList;

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
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SecurityHandler;
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

public class DBVSNamespaceDiagnosisRequest extends RequestProcessorCore {
	SummaryTable filesList = new SummaryTable();
	String nodeId;

	static String queryStart = "SELECT " + ColumnConstants.COL_TAG_VALUES_FILEPATH + ","
			+ ColumnConstants.COL_TAG_VALUES_ACCESSTIME + "," + ColumnConstants.COL_TAG_VALUES_BLOCKSIZE + ","
			+ ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME + "," + ColumnConstants.COL_TAG_VALUES_REPLICATION
			+ " FROM ";

	private ArrayList conflicts = new ArrayList();

	private long startIndex;

	private long curIndex;

	private long endIndex;
	private String filePath;

	Gson gson = new Gson();

	public DBVSNamespaceDiagnosisRequest(String nodeId, String user, long startIndex, long endIndex, String filePath) {
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

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("User: " + us.getUserName() + ",password: "
							+ SecurityHandler.decryptData(us.getPassword()));

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

			diagnose(dfs, conf, bw);

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

	private void diagnose(FileSystem dfs, Configuration conf, BufferedWriter bw) throws Exception {
		Connection connection = null;
		DBTypeProperties props = null;
		try {
			String connectionName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			connection = CoreDBManager.getCustomTagDBConnection(connectionName);

			props = CustomTagDBConfigManager.getDatabaseDataTypeMap(connectionName, null);

			ArrayList<String> tableNames = getTableNames(connection, props);

			Statement stmt = null;
			ResultSet rs = null;

			String filePath = null;
			FileStatus stat = null;
			Long blockSize = null;
			for (int i = 0; i < tableNames.size(); i++) {
				String query = queryStart + tableNames.get(i);

				try {
					stmt = DatabaseFunctions.getStatement(connection);

					rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);

					while (rs.next()) {

						NamespaceConflict conflict = new NamespaceConflict();

						try {
							filePath = rs.getString(ColumnConstants.COL_TAG_VALUES_FILEPATH);

							stat = dfs.getFileStatus(new Path(filePath));
						} catch (FileNotFoundException e) {
							conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_MISSING_ENTRY_IN_NAMESPACE);
						}

						if (conflict.getConflictType() != NamespaceConflict.CONFLICT_TYPE_NONE) {
							// Conflict detected

							if (filePath != null) {
								conflict.setFilePath(filePath);
								conflict.setConflictTypeDescription();

								AppLogger.getLogger().fatal("Conflict: " + conflict.getFilePath() + ", "
										+ conflict.getConflictTypeDescription());

								bw.write(gson.toJson(conflict));
								bw.newLine();
								bw.flush();
							}
						}
					}
				} catch (SQLSyntaxErrorException e) {
					// Parsing unrelated table
					AppLogger.getLogger().debug("Error in parsing table " + tableNames.get(i) + " " + e.getMessage());
					// AppLogger.getLogger().fatal(e.getMessage());
				} finally {

					try {
						DatabaseFunctions.closeResultSet(rs);
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
					try {
						DatabaseFunctions.closeStatement(stmt);
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
			}
		} finally {
			try {
				if (connection != null)
					CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	private ArrayList<String> getTableNames(final Connection connection, DBTypeProperties props) throws SQLException {
		ArrayList<String> tables = new ArrayList();
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = md.getTables(null, props.getDefaultSchema(), "%", new String[] { "TABLE" });
		while (rs.next()) {
			String tableName = rs.getString(3);
			if (tableName.equals(TableConstants.TABLE_LOOKUP) || tableName.equals(TableConstants.TABLE_DIRECTORIES))
				continue;
			tables.add(tableName);
		}
		return tables;
	}

	public ArrayList getConflicts() {
		return conflicts;
	}
}
