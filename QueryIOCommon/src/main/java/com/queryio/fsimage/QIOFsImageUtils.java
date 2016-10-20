package com.queryio.fsimage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.server.namenode.FSImageUtils;
import org.apache.hadoop.hdfs.server.namenode.FileStatusTreeModel;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.ColumnConstants;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.HDFSMetadata;
import com.queryio.common.database.HDFSMetadataReader;
import com.queryio.common.database.NSMetadata;
import com.queryio.common.database.NSMetadataReader;
import com.queryio.common.database.TableConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class QIOFsImageUtils {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
	public static final Log LOG = LogFactory.getLog(QIOFsImageUtils.class.getName());
	public static void constructFSImageFromDB(Connection connection, String storageDir, String backupId) throws Exception{
		FileStatusTreeModel treeModel = new FileStatusTreeModel();
		
		String namespaceId = fetchNamespaceId(connection, backupId);
		if(namespaceId == null){
			throw new Exception("Namespace ID not found");
		}
		
		String blockPoolId = fetchBlockPoolId(connection, backupId);
		if(blockPoolId == null){
			throw new Exception("Blockpool ID not found");
		}
	
		populateTreeModel(connection, (backupId != null ? backupId : "") + TableConstants.TABLE_HDFS_METADATA, treeModel);
		
		DatabaseMetaData dbm = connection.getMetaData();
		ResultSet tables = null;
		try {
			tables = dbm.getTables(null, null, (backupId != null ? backupId : "") + "DATATAGS_%", null);
			while (tables.next()) {
				populateTreeModel(connection, tables.getString(3), treeModel);
			}
		} finally {
			DatabaseFunctions.closeResultSet(tables);
		}
		LOG.info("Printing FileSystem tree structure...");
		treeModel.printTree();
		FSImageUtils.createFSImage(treeModel, storageDir);
		
		changeVersion(storageDir, namespaceId, blockPoolId);
	}
	public static void constructFsImageFromFile(NSMetadataReader nsMetadataReader, HDFSMetadataReader hdfsMetadataReader, String storageDir, String filePath) throws Exception{
		FileStatusTreeModel treeModel = new FileStatusTreeModel();
		
		nsMetadataReader = new NSMetadataReader(filePath + File.separator + "ns-metadata.xml");
		nsMetadataReader.start();
		
		String namespaceId = fetchNamespaceId(nsMetadataReader);
		if(namespaceId == null){
			throw new Exception("Namespace ID not found");
		}
		
		String blockPoolId = fetchBlockPoolId(nsMetadataReader);
		if(blockPoolId == null){
			throw new Exception("Blockpool ID not found");
		}
		
		hdfsMetadataReader = new HDFSMetadataReader(filePath + File.separator + "hdfs-metadata.xml");
		hdfsMetadataReader.start();
	
		populateTreeModel(hdfsMetadataReader, treeModel);
		
		treeModel.printTree();
		FSImageUtils.createFSImage(treeModel, storageDir);
		
		QIOFsImageUtils.changeVersion(storageDir, namespaceId, blockPoolId);
	}
	private static String fetchNamespaceId(NSMetadataReader nsMetadataReader) throws Exception {
		NSMetadata nsMetadata = null;
		while((nsMetadata = nsMetadataReader.next())!=null) {
			if(nsMetadata.getKey().equals(QueryIOConstants.NS_NAMESPACE_ID)) {
				return nsMetadata.getValue();
			}
		}
		return null;
	}
	
	private static String fetchBlockPoolId(NSMetadataReader nsMetadataReader) throws Exception {
		NSMetadata nsMetadata = null;
		while((nsMetadata = nsMetadataReader.next())!=null) {
			if(nsMetadata.getKey().equals(QueryIOConstants.NS_BLOCKPOOL_ID)) {
				return nsMetadata.getValue();
			}
		}
		return null;
	}
	
	private static String fetchNamespaceId(Connection connection, String backupId) throws Exception {
		Statement st = null;
		ResultSet rs = null;
		String query = "SELECT * FROM " + (backupId != null ? backupId : "") + TableConstants.TABLE_NS_METADATA  + " WHERE " + ColumnConstants.COL_NS_METADATA_KEY + " = '" + QueryIOConstants.NS_NAMESPACE_ID + "'";
		LOG.info("query: " + query);
		String namespaceId = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery(query);
			if(rs.next()){
				namespaceId = rs.getString(ColumnConstants.COL_NS_METADATA_VALUE);
			}	
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return namespaceId;
	}
	
	private static String fetchBlockPoolId(Connection connection, String backupId) throws Exception {
		Statement st = null;
		ResultSet rs = null;
		String query = "SELECT * FROM " + (backupId != null ? backupId : "") + TableConstants.TABLE_NS_METADATA  + " WHERE " + ColumnConstants.COL_NS_METADATA_KEY + " = '" + QueryIOConstants.NS_BLOCKPOOL_ID+ "'";
		LOG.info("query: " + query);
		String blockPoolId = null;
		try{
			st = connection.createStatement();
			rs = st.executeQuery(query);
			if(rs.next()){
				blockPoolId = rs.getString(ColumnConstants.COL_NS_METADATA_VALUE);
			}	
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		return blockPoolId;
	}
	 
	private static void populateTreeModel(HDFSMetadataReader hdfsMetadataReader, FileStatusTreeModel treeModel) throws NumberFormatException, IOException, ClassNotFoundException{
		HDFSMetadata hdfsMetadata = null;
		while((hdfsMetadata = hdfsMetadataReader.next())!=null) {
			Path path = new Path(hdfsMetadata.getFilePath());
			
			long mTime = hdfsMetadata.getModificationTime();
			long aTime = hdfsMetadata.getAccessTime();
			String owner = hdfsMetadata.getOwner();
			String group = hdfsMetadata.getUsergroup();
			String permStr = hdfsMetadata.getPermission();
			FsPermission permission = new FsPermission(getFsAction(permStr.substring(0, 3)), getFsAction(permStr.substring(3, 6)), getFsAction(permStr.substring(6, 9)));
			long blockSize = hdfsMetadata.getBlockSize();
			short replication = hdfsMetadata.getReplication();
			long len = hdfsMetadata.getLength();
			Block[] blocks = UserDefinedTagUtils.convert(hdfsMetadata.getBlocks());
		
			FileStatus fs = new FileStatus(len, false, replication, blockSize, mTime, aTime, permission, owner, group, path, blocks);
			treeModel.insert(path, fs);
		}
	}
	private static void populateTreeModel(Connection connection, String tableName, FileStatusTreeModel treeModel) throws Exception{
		Statement st = null;
		ResultSet rs = null;
		String query = "SELECT * FROM " + tableName;
		LOG.info("query: " + query);
		try{
			st = connection.createStatement();
			rs = st.executeQuery(query);
			
			while(rs.next()){
				Path path = new Path(rs.getString(TableMetadata.DEFAULT_TAG_FILEPATH));
				long mTime = format.parse(rs.getString(TableMetadata.DEFAULT_TAG_MODIFICATIONTIME)).getTime();
				long aTime = format.parse(rs.getString(TableMetadata.DEFAULT_TAG_ACCESSTIME)).getTime();
				String owner = rs.getString(TableMetadata.DEFAULT_TAG_OWNER);
				String group = rs.getString(TableMetadata.DEFAULT_TAG_GROUP);
				String permStr = rs.getString(TableMetadata.DEFAULT_TAG_PERMISSION);
				FsPermission permission = new FsPermission(getFsAction(permStr.substring(0, 3)), getFsAction(permStr.substring(3, 6)), getFsAction(permStr.substring(6, 9)));
				long blockSize = Long.parseLong(rs.getString(TableMetadata.DEFAULT_TAG_BLOCKSIZE));
				short replication = rs.getShort(TableMetadata.DEFAULT_TAG_REPLICATION);
				long len = Long.parseLong(rs.getString(TableMetadata.DEFAULT_TAG_LENGTH));
//				Block[] blocks = UserDefinedTagUtils.convert(rs.getBytes(TableMetadata.DEFAULT_TAG_BLOCKS));	//TODO: Uncomment this later
				Block[] blocks = convert(rs.getBytes(TableMetadata.DEFAULT_TAG_BLOCKS));
				FileStatus fs = new FileStatus(len, false, replication, blockSize, mTime, aTime, permission, owner, group, path, blocks);
				treeModel.insert(path, fs);
			}	
		}finally{
			DatabaseFunctions.closeSQLObjects(st, rs);
		}
		
	}
	
	private static Block[] convert(byte[] bytes) throws IOException{
		ByteArrayInputStream bais = null;
		Block[] blocks = null;
		try{
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			int count = ois.readInt();
			LOG.info("count : " + count);
			blocks = new Block[count];
			for(int i = 0; i < count; i ++){
				blocks[i] = new Block();
				blocks[i].readFields(ois);
			}
			return blocks;
		}finally{
			if(bais != null)
				bais.close();	
		}
	}
	
	public static void changeVersion(String storageDir, String namespaceId, String blockPoolId) throws IOException{
		LOG.info("Writing namespaceId: " + namespaceId);
		LOG.info("Writing blockPoolId: " + blockPoolId);
		
		File in = new File(storageDir + File.separator + "current" + File.separator + "VERSION");
		File out = new File(storageDir + File.separator + "current" + File.separator + "VERSION");
		
		BufferedReader reader = null;
	    Writer writer = null;
	    
	    List<String> lines = new ArrayList<String>();
	    List<String> myLines = new ArrayList<String>();
	    boolean flag1 = false, flag2 = false;
	    try
	    {
	    	reader = new BufferedReader(new FileReader(in));	    	
	    	String line = null;
	    	while ((line = reader.readLine()) != null){
	    		if(line.contains("namespaceID=")){
	    			lines.add("namespaceID=" + namespaceId);
	    			flag1 = true;
	    		}else if(line.contains("blockpoolID=")){
	    			lines.add("blockpoolID=" + blockPoolId);
	    			flag2 = true;
	    		}else{
	    			lines.add(line);
	    		}
	    	}
	    	if(!flag1){
	    		myLines.add("namespaceID=" + namespaceId);
	    	}
	    	if(!flag2){
	    		myLines.add("blockpoolID=" + blockPoolId);
	    	}
	    	
	    	writer = new BufferedWriter(new FileWriter(out));		    	
	    	for(int i=0; i<myLines.size(); i++)
	    	{
	    		writer.write((String)myLines.get(i));
	    		writer.write("\n");
	    	}		    	
	    	for(int i=0; i<lines.size(); i++)
	    	{
	    		writer.write((String)lines.get(i));
	    		writer.write("\n");
	    	}
	    }
	    finally
	    {
	    	try
	    	{
	    		if(reader != null)
	    			reader.close();
	    	}
	    	catch(Exception e)
	    	{
	    		AppLogger.getLogger().fatal(e.getMessage(), e);
	    	}
	    	try
	    	{
	    		if(writer != null)
	    			writer.close();
	    	}
	    	catch(Exception e)
	    	{
	    		AppLogger.getLogger().fatal(e.getMessage(), e);
	    	}
	    }
	}
	
	public static void main(String[] args) {
		String permStr = "rw-r--r--";
		String perm = "0";
		System.out.println("perm : " + perm);
//		public enum FsAction {
//			  NONE("---"),
//			  EXECUTE("--x"),
//			  WRITE("-w-"),
//			  WRITE_EXECUTE("-wx"),
//			  READ("r--"),
//			  READ_EXECUTE("r-x"),
//			  READ_WRITE("rw-"),
//			  ALL("rwx");
		FsPermission permission;
//		new FsPermission("0644");
//		FsPermission permission = new FsPermission(Short.parseShort(perm));
//        System.out.println(permission.toString());
        
//		FsPermission permission = new FsPermission("0644");
//        System.out.println(permission.toString());
        
//        permission = new FsPermission("rw-r--r--");
//        System.out.println(permission.toString());
        
        permission = new FsPermission(getFsAction(permStr.substring(0, 3)), getFsAction(permStr.substring(3, 6)), getFsAction(permStr.substring(6, 9)));
        System.out.println(permission.toString());
        
      }
	
	private static FsAction getFsAction(String permssion) {
		System.out.println("permssion : " + permssion);
		if(permssion.equals("---")) {
			return FsAction.NONE;
		} else if(permssion.equals("r--")) {
			return FsAction.READ;
		} else if(permssion.equals("-w-")) {
			return FsAction.WRITE;
		} else if(permssion.equals("--x")) {
			return FsAction.EXECUTE;
		} else if(permssion.equals("rw-")) {
			return FsAction.READ_WRITE;
		} else if(permssion.equals("r-x")) {
			return FsAction.READ_EXECUTE;
		} else if(permssion.equals("-wx")) {
			return FsAction.WRITE_EXECUTE;
		} else if(permssion.equals("rwx")) {
			return FsAction.ALL;
		}
		return null;
	}
}