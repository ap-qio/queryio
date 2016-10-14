package com.queryio.core.requestprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.userdefinedtags.common.UserDefinedTagDAO;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class DBRepairRequest extends RequestProcessorCore {
	SummaryTable filesList = new SummaryTable();
	String nodeId;
	
	static String queryStart = "SELECT "
			+ ColumnConstants.COL_TAG_VALUES_FILEPATH + ","
			+ ColumnConstants.COL_TAG_VALUES_ACCESSTIME + ","
			+ ColumnConstants.COL_TAG_VALUES_BLOCKSIZE + ","
			+ ColumnConstants.COL_TAG_VALUES_MODIFICATIONTIME + ","
			+ ColumnConstants.COL_TAG_VALUES_REPLICATION
			+ " FROM ";
	static String queryEnd = " WHERE FILEPATH=?";
	
	private ArrayList conflicts = new ArrayList();
	
	private String filePath;

	Gson gson = new Gson();

	public DBRepairRequest(String nodeId, String user, String filePath) {
		super(user, null, null);
		this.nodeId = nodeId;
		this.filePath = filePath;
	}
	
	public void process() throws Exception {
		Thread.currentThread().setName(this.user); /* CRUCIAL */
		
		Connection connection = null;
		
		FileSystem dfs = null;
		try
		{	
			connection = CoreDBManager.getQueryIODBConnection();
			
			Node node = NodeDAO.getNode(connection, nodeId);
			if(node==null)	return;
			
			Host host = HostDAO.getHostDetail(connection, node.getHostId());
			ArrayList list = new ArrayList();
			list.add(DFSConfigKeys.FS_DEFAULT_NAME_KEY);
			ArrayList result = QueryIOAgentManager.getConfig(host, list, node, "core-site.xml");
			if(result == null || result.size() == 0){
				return;
			}
			
			String fsDefaultName = (String)result.get(0);
			Configuration conf = null;
			if(EnvironmentalConstants.isUseKerberos()){
					User us = UserDAO.getUserDetail(connection, user);
					
					conf = ConfigurationManager.getKerberosConfiguration(connection, nodeId);
					
					try{
						UserGroupInformation.setConfiguration(conf);
						UserGroupInformation.getLoginUser(us.getUserName(), SecurityHandler.decryptData(us.getPassword()));
						
						dfs = FileSystem.get(new URI(fsDefaultName), conf);
						dfs.getStatus();
					}
					catch(Exception e){
						AppLogger.getLogger().fatal("Could not authenticate user with kerberos, error: " + e.getMessage(), e);
						return;
					}
			} else {
				conf = ConfigurationManager.getConfiguration(connection, nodeId); 
				dfs = FileSystem.get(URI.create(fsDefaultName), conf);
			}
			
//			DistributedFileSystem fs = (DistributedFileSystem) dfs;
//			fs.getClient().setSafeMode(SafeModeAction.SAFEMODE_ENTER);

			// Perform repair
			
			repair(dfs, conf);
			
		} finally {
			try {
				if(dfs!=null)
					dfs.close();
			} catch(Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
			
			try {
				if(connection != null)
					CoreDBManager.closeConnection(connection);
			} catch(Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
	
	private void repair(FileSystem dfs, Configuration conf) throws Exception {
		FileReader fw = null;
		BufferedReader bw = null;
		Connection connection = null;
		try {
			fw = new FileReader(new File(filePath));
			bw = new BufferedReader(fw);
			
			String dbPoolName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			connection = CoreDBManager.getCustomTagDBConnection(dbPoolName);
			DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(dbPoolName, null);
			
			String line = null;
			
			NamespaceConflict conflict = null;
			while ((line = bw.readLine()) != null) {
				conflict = gson.fromJson(line, NamespaceConflict.class);
				
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Resolving conflict: " + conflict.getFilePath() + ", " + conflict.getConflictTypeDescription());
				
				repairConflict(dfs, conf, connection, props, conflict);
			}
		} finally {
			try {
				if(bw!=null) {
					bw.close();
				}
			} catch(Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try {
				if(connection != null)
					CoreDBManager.closeConnection(connection);
			} catch(Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
	
	private void repairConflict(FileSystem dfs, Configuration conf, Connection connection, DBTypeProperties props, NamespaceConflict conflict) throws Exception {
		if(conflict.getConflictType()==NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERENCE) {
			addOrUpdateMetadata(dfs, conf, connection, props, conflict.getFilePath());
		} else if(conflict.getConflictType()==NamespaceConflict.CONFLICT_TYPE_MISSING_ENTRY_IN_DATABASE) {
			addOrUpdateMetadata(dfs, conf, connection, props, conflict.getFilePath());
		} else if(conflict.getConflictType()==NamespaceConflict.CONFLICT_TYPE_MISSING_ENTRY_IN_NAMESPACE) {
			deleteMetadata(dfs, conf, connection, conflict.getFilePath());
		} 
	}
	
	private void addOrUpdateMetadata(FileSystem dfs, Configuration conf, Connection connection, DBTypeProperties props, String fileName) throws Exception {
		IDataTagParser tagParser = UserDefinedTagResourceFactory.getParserFromConstructor(conf, fileName, null, null);		
		String tableName = UserDefinedTagResourceFactory.getTableName(tagParser, fileName);
		
		List<UserDefinedTag> tags = UserDefinedTagUtils.generateDefaultTags(dfs, fileName);
		
		UserDefinedTagDAO.insertTagValues(connection, props, tableName,
				fileName, tags, null, tagParser == null ? false : tagParser.updateDbSchema(), tagParser == null ? new TableMetadata("DEFAULT", null) 
						: tagParser.getTableMetaData(UserDefinedTagUtils.getFileExtension(fileName)));
	}
	
	private void deleteMetadata(FileSystem dfs, Configuration conf, Connection connection, String fileName) throws Exception {
		UserDefinedTagDAO.deleteFromDefaultTable(connection, fileName);
		UserDefinedTagDAO.deleteFileFromAllMetadataTables(connection, fileName);
	}
	
	private void diagnose(FileSystem dfs, Configuration conf, ObjectOutputStream os) throws Exception {
		Connection connection = null;
		try {
			String connectionName = conf.get(QueryIOConstants.ANALYTICS_DB_DBSOURCEID);
			connection = CoreDBManager.getCustomTagDBConnection(connectionName);
			
			ArrayList<String> tableNames = getTableNames(connection);
			
			Statement stmt = null;
			ResultSet rs = null;
			
			String filePath = null;
			FileStatus stat = null;
			Long blockSize = null;
			for(int i=0; i<tableNames.size(); i++) {
				String query = queryStart + tableNames.get(i);
				
				try {
					rs = DatabaseFunctions.getQueryResultsForStatement(stmt, query);
					
					while(rs.next()) {
					
						NamespaceConflict conflict = new NamespaceConflict();
						
						try {
							filePath = rs.getString(ColumnConstants.COL_TAG_VALUES_FILEPATH);
							
							stat = dfs.getFileStatus(new Path(filePath));
						} catch(FileNotFoundException e) {
							conflict.setConflictType(NamespaceConflict.CONFLICT_TYPE_MISSING_ENTRY_IN_NAMESPACE);
						}
						
						if(conflict.getConflictType() != NamespaceConflict.CONFLICT_TYPE_NONE) {
							// Conflict detected
							
							if(filePath!=null) {
								conflict.setFilePath(filePath);
								
								os.writeObject(conflict);
							}
						}
					}
				} finally {
					try {
						DatabaseFunctions.closeResultSet(rs);
					} catch(Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
					try {
						DatabaseFunctions.closeStatement(stmt);
					} catch(Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);
					}
				}
			}
		} finally {
			try {
				if(connection != null)
					CoreDBManager.closeConnection(connection);
			} catch(Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
	
	private ArrayList<String> getTableNames(final Connection connection) throws SQLException {
		ArrayList<String> tables = new ArrayList();
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		while (rs.next()) {
			tables.add(rs.getString(3));
		}
		return tables;
	}
	
	public ArrayList getConflicts() {
		return conflicts;
	}
}
