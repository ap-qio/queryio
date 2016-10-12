package com.queryio.core.requestprocessor;

import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.security.UserGroupInformation;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
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
import com.queryio.core.databrowser.FileOffSetFilter;
import com.queryio.core.monitor.beans.SummaryTable;
import com.queryio.core.utils.QIODFSUtils;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.stream.util.EncryptionHandler;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class ListFilesRequest extends RequestProcessorCore {
	SummaryTable filesList = new SummaryTable();
	Path path;
	int rows;
	int pageNo;
	String nodeId;
	
	static String QUERY_START = "SELECT " + EncryptionHandler.COL_COMPRESSION_TYPE + "," + EncryptionHandler.COL_ENCRYPTION_TYPE + " FROM ";
	static String QUERY_END = " WHERE FILEPATH=?";
	
	public ListFilesRequest(String nodeId, String user, Path path, int rows, int pageNo) {
		super(user, null, path);
		this.nodeId = nodeId;
		this.path = path;
		this.pageNo = pageNo;
		this.rows = rows;
	}
	
	public void process() throws Exception{
		ArrayList dirList = new ArrayList();
		ArrayList fileList = new ArrayList();
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
					
					if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("User: " + us.getUserName() + ",password: " + SecurityHandler.decryptData(us.getPassword()));
					
					try{
						UserGroupInformation.setConfiguration(conf);
						UserGroupInformation.getLoginUser(us.getUserName(), SecurityHandler.decryptData(us.getPassword()));
						
						dfs = FileSystem.get(new URI(fsDefaultName), conf);
					}
					catch(Exception e){
						AppLogger.getLogger().fatal("Could not authenticate user with kerberos, error: " + e.getMessage(), e);
						return;
					}
			} else {
				conf = ConfigurationManager.getConfiguration(connection, nodeId); 
				dfs = QIODFSUtils.getFileSystemAs(user, group, conf, new URI(fsDefaultName));
			}
			
			int length = pageNo*rows;
			int offset = length==rows ? 1 : length-rows + 1;
			
			FileOffSetFilter fosf = new FileOffSetFilter(offset,length);
			FileStatus[] filterstatus = dfs.listStatus(path,fosf);
			Path[] listedPaths = FileUtil.stat2Paths(filterstatus);
			
			ArrayList colNames = new ArrayList();
			colNames.add("Name");
			colNames.add("Size");
			colNames.add("Replicas");
			colNames.add("Last Read");
			colNames.add("Last Write");
			colNames.add("Permission");
			colNames.add("Owner");
			colNames.add("Group");
			colNames.add("Type");
			filesList.setColNames(colNames);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("FilterlistedPaths length :"+listedPaths.length);
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Total  path :"+fosf.getCount());
			
			filesList.setTotalRowCount(fosf.getCount());
			
			for (int k=0;k<listedPaths.length;k++) {
				Path p = listedPaths[k];
				FileStatus stat = dfs.getFileStatus(p);
				
				ArrayList row = new ArrayList();
				double size = stat.getLen();
				String units[] = {"B", "KB", "MB", "GB"};
				String unit = units[0];
				for(int i=1; i < units.length; i++){
					double temp = size / 1024; 
					if(temp < 1){
						break;
					}else{
						size = temp; 
						unit = units[i];
					}
				}
				DecimalFormat df = new DecimalFormat("#.##");
				
				row.add(p.getName());				
				row.add(!stat.isDirectory() ? df.format(size) + " " + unit:"");
				row.add(!stat.isDirectory() ? stat.getReplication():"");
				row.add(!stat.isDirectory() ? sdf.format(stat.getAccessTime()):"");
				row.add(sdf.format(stat.getModificationTime()));
				row.add(stat.getPermission().toString());
				row.add(stat.getOwner());
				row.add(stat.getGroup());
				
				String src = p.toString();
				if(src.contains("://")) {
					src = src.substring(src.indexOf("://") + 3);
					src = src.substring(src.indexOf("/"));
				}
				
				if (stat.isDirectory()) {
					row.add(QueryIOConstants.EMPTY_STRING);
					row.add(QueryIOConstants.EMPTY_STRING);
				} else {
					try {
						Map<String, String> metadata = getMetadata(conf, src);

						AppLogger.getLogger().debug("C: " + metadata
								.get(EncryptionHandler.COL_COMPRESSION_TYPE));
						
						AppLogger.getLogger().debug("E: " + metadata
								.get(EncryptionHandler.COL_ENCRYPTION_TYPE));
						
						
						row.add(metadata
								.get(EncryptionHandler.COL_COMPRESSION_TYPE));
						row.add(metadata
								.get(EncryptionHandler.COL_ENCRYPTION_TYPE));
					} catch (Exception e) {
						AppLogger.getLogger().fatal(e.getMessage(), e);

						row.add("N/A");
						row.add("N/A");
					}
				}
				
				row.add(!stat.isDirectory() ? "file":"dir");				
				if(stat.isDirectory()){
					dirList.add(row);
				}else{
					fileList.add(row);
				}				
			}
		} finally {
			try
			{
				if(dfs!=null)
					dfs.close();
				if(connection != null)
					CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
		for(int i = 0; i < dirList.size(); i++){
			filesList.addRow((ArrayList)dirList.get(i));
		}
		for(int i = 0; i < fileList.size(); i++){
			filesList.addRow((ArrayList)fileList.get(i));
		}
	}
	
	public SummaryTable getFilesList() {
		return filesList;
	}
	
	public static Map<String, String> getMetadata(Configuration conf, String filePath) throws Exception {
		Map<String, String> metadata = null;
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Fetching metadata for: " + filePath);
		
		if(conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID)==null){
			metadata = new HashMap<String, String>();
			metadata.put(EncryptionHandler.COL_COMPRESSION_TYPE, "N/A");
			metadata.put(EncryptionHandler.COL_ENCRYPTION_TYPE, "N/A");
		} else {
			metadata = getObjectMetadata(conf, filePath, TableConstants.TABLE_HDFS_METADATA);
			
			if(metadata == null){
				metadata = getObjectMetadata(conf, filePath, ("DATATAGS_" + UserDefinedTagUtils.getFileExtension(filePath)).toUpperCase());
			}
			
			if(metadata == null){
				metadata = getObjectMetadata(conf, filePath, null);
			}
			
			if(metadata==null) {
				metadata = new HashMap<String, String>();
				metadata.put(EncryptionHandler.COL_COMPRESSION_TYPE, "none");
				metadata.put(EncryptionHandler.COL_ENCRYPTION_TYPE, "none");
			}
		}
		
		return metadata;
	}
	
	public static Map<String, String> getObjectMetadata(Configuration conf, String filePath, String tableName) throws Exception{
		Map map = null;	
		Connection connection  = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet res = null;
		IDataTagParser tagParser = null;
		try{
			String connectionName = conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID);
			connection = CoreDBManager.getCustomTagDBConnection(connectionName);
			
			if(tableName==null){
				tagParser = UserDefinedTagResourceFactory.getParserFromConstructor(conf, filePath, null, null);
				tableName = UserDefinedTagResourceFactory.getTableName(tagParser, filePath);
			}
			
			AppLogger.getLogger().debug("tableName: " + tableName);
			
			DatabaseMetaData meta = connection.getMetaData();
			res = meta.getTables(null, null, null, 
			     new String[] {"TABLE"});
			boolean found = false;
			while (res.next()) {
				if(res.getString("TABLE_NAME").equalsIgnoreCase(tableName)){
			    	 found = true;
				}
			}
			
			AppLogger.getLogger().debug("file: " + filePath + ", tableName: " + tableName + ", found: " + found);
			
			if(!found){
				return null;
			}
			  
			stmt = connection.prepareStatement(QUERY_START + tableName + QUERY_END);
			
			stmt.setString(1, filePath);
			
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(stmt);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			if(rs.next()){
				AppLogger.getLogger().debug("file: " + filePath + ", tableName: " + tableName + ", found: " + found + ", entry found");
				map = new HashMap();
				for(int i=1; i<=rsmd.getColumnCount(); i++){
						map.put(rsmd.getColumnName(i).toUpperCase(), rs.getObject(rsmd.getColumnName(i)));
				}
			}
		} finally {
			try{
				DatabaseFunctions.closeResultSet(rs);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try{
				DatabaseFunctions.closeResultSet(res);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try{
				DatabaseFunctions.closePreparedStatement(stmt);
			} catch(Exception e){
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
			try{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception ex){
				AppLogger.getLogger().fatal("Error closing database connection.", ex);
			}
		}
		return map;
	}
}
