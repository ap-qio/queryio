package com.queryio.demo.common;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.hadoop.conf.Configuration;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.DatabaseFunctions;
import com.queryio.common.database.TableConstants;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class CustomQIODFSInputStream extends InputStream {
	InputStream in;
	
	private static final String QUERY_START = "SELECT " + CustomEncryptionHandler.COL_COMPRESSION_TYPE + "," + CustomEncryptionHandler.COL_ENCRYPTION_TYPE + " FROM ";
	private static final String QUERY_END = " WHERE FILEPATH=?";
	private String _compressionType;
	
	public String getCompressionType() {
		return _compressionType;
	}

	public String getEncryptionType() {
		return _encryptionType;
	}

	private String _encryptionType;
	
	public CustomQIODFSInputStream(InputStream dfsIn, Configuration conf, String filePath) throws Exception {
		Map<String, String> metadata = null;
	
		if(conf.get(QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID)==null){
			in = new CustomEncryptionHandler(Cipher.DECRYPT_MODE,
					false, conf.get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY))
					.getCompressedCipherInputStream(dfsIn,
							CustomEncryptionHandler.COMPRESSION_TYPE_NONE);
		} else {
			metadata = getObjectMetadata(conf, filePath, TableConstants.TABLE_HDFS_METADATA);
			
			if(metadata == null){
				metadata = getObjectMetadata(conf, filePath, ("DATATAGS_" + UserDefinedTagUtils.getFileExtension(filePath)).toUpperCase());
			}
			
			if(metadata == null){
				metadata = getObjectMetadata(conf, filePath, null);
			}
			
			if(metadata==null) {
				in = new CustomEncryptionHandler(Cipher.DECRYPT_MODE,
						false, conf.get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY))
						.getCompressedCipherInputStream(dfsIn,
								CustomEncryptionHandler.COMPRESSION_TYPE_NONE);
			} else {
				_compressionType = metadata.get(CustomEncryptionHandler.COL_COMPRESSION_TYPE);
				_encryptionType = metadata.get(CustomEncryptionHandler.COL_ENCRYPTION_TYPE);
				int compressionType = CustomEncryptionHandler.getCompressionTypeValue(_compressionType);
				boolean encryptionType = CustomEncryptionHandler.getEncryptionTypeValue(_encryptionType);
				
				in = new CustomEncryptionHandler(Cipher.DECRYPT_MODE,
						encryptionType, conf.get(QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY))
						.getCompressedCipherInputStream(dfsIn,
								compressionType);
			}
		}
	}
	
	public Map<String, String> getObjectMetadata(Configuration conf, String filePath, String tableName) throws Exception{
		Map map = null;	
		Connection connection  = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSet res = null;
		IDataTagParser tagParser = null;
		try{
			connection = UserDefinedTagResourceFactory.getConnectionWithPoolInit(conf, true);
			
			if(tableName==null){
				tagParser = UserDefinedTagResourceFactory.getParserFromConstructor(conf, filePath, null, null);
				
				tableName = UserDefinedTagResourceFactory.getTableName(tagParser, filePath);
			}
			
			DatabaseMetaData meta = connection.getMetaData();
			res = meta.getTables(null, null, null, 
			     new String[] {"TABLE"});
			boolean found = false;
			while (res.next()) {
				if(res.getString("TABLE_NAME").equals(tableName)){
			    	 found = true;
				}
			}
			
			if(!found){
				return null;
			}
			  
			stmt = connection.prepareStatement(QUERY_START + tableName + QUERY_END);
			
			stmt.setString(1, filePath);
			
			rs = DatabaseFunctions.getQueryResultsForPreparedStatement(stmt);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			if(rs.next()){
				map = new HashMap();
				for(int i=1; i<=rsmd.getColumnCount(); i++){
						map.put(rsmd.getColumnName(i), rs.getObject(rsmd.getColumnName(i)));
					}
				}
		} finally {
			DatabaseFunctions.closeResultSet(rs);
			DatabaseFunctions.closeResultSet(res);
			DatabaseFunctions.closePreparedStatement(stmt);
			CoreDBManager.closeConnection(connection);			
		}
		return map;
	}
	
	@Override
	public void mark(int readlimit) {
		in.mark(readlimit);
	}
	
	@Override
	public void reset() throws IOException {
		in.reset();
	}
	
	@Override
	public int read() throws IOException {
		return in.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}
	
	@Override
	public long skip(long n) throws IOException{
		return in.skip(n);
	}
	
	@Override
	public void close() throws IOException{
		in.close();
	}
	
	@Override
	public int available() throws IOException {
		return in.available();
	}
}
