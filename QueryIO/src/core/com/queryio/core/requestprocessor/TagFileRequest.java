package com.queryio.core.requestprocessor;

import java.io.OutputStream;
import java.net.URI;
import java.sql.Connection;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.security.UserGroupInformation;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SecurityHandler;
import com.queryio.core.bean.User;
import com.queryio.core.conf.ConfigurationManager;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.utils.QIODFSUtils;
import com.queryio.plugin.datatags.IDataTagParser;
import com.queryio.plugin.datatags.TableMetadata;
import com.queryio.plugin.datatags.UserDefinedTag;
import com.queryio.userdefinedtags.common.UserDefinedTagDAO;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;
import com.queryio.userdefinedtags.common.UserDefinedTagUtils;

public class TagFileRequest extends RequestProcessorCore {
	private String nameNodeId;
	private FileSystem dfs;
	List<UserDefinedTag> extraTags;
	
	public TagFileRequest(String user, Path path, String nameNodeId, List<UserDefinedTag> extraTags) {
		super(user, null, path);
		this.nameNodeId = nameNodeId;
		this.extraTags = extraTags; 
	}
	
	@Override
	public void process() throws Exception {
		this.successful = false;
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("GetFileRequest, user: " + this.user);
		
		Connection connection = null;
		OutputStream os = null;
		Configuration conf = null;
		try
		{
			connection = CoreDBManager.getQueryIODBConnection();
			
			if(EnvironmentalConstants.isUseKerberos()){
				User us = UserDAO.getUserDetail(connection, user);
				
				conf = ConfigurationManager.getKerberosConfiguration(connection, nameNodeId);
				
				try{
					UserGroupInformation.setConfiguration(conf);
//					UserGroupInformation.getLoginUser(us.getUserName(), SecurityHandler.decryptData(us.getPassword()));
					
					dfs = FileSystem.get(new URI(conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY)), conf);
				}
				catch(Exception e){
					AppLogger.getLogger().fatal("Could not authenticate user with kerberos, error: " + e.getMessage(), e);
					return;
				}
			} else {
				conf = ConfigurationManager.getConfiguration(connection, nameNodeId); 
				dfs = QIODFSUtils.getFileSystemAs(user, group, conf, new URI(conf.get(DFSConfigKeys.FS_DEFAULT_NAME_KEY)));
			}
			
			IDataTagParser tagParser = UserDefinedTagResourceFactory.getParserFromConstructor(conf, this.path.toString(), null, null);
			String tableName = UserDefinedTagResourceFactory.getTableName(tagParser, this.path.toString());
			
			List<UserDefinedTag> defaultTags = UserDefinedTagUtils.generateDefaultTags(dfs, this.path.toString());
			
			Connection metaCon = null;
			try {
				
				metaCon = UserDefinedTagResourceFactory.getConnectionWithPoolInit(conf, true);
				
				DBTypeProperties dbTypeProp = CustomTagDBConfigManager.getDatabaseDataTypeMap(null, CustomTagDBConfigManager.getConfig(RemoteManager.getDBNameForNameNodeMapping(nameNodeId)).getCustomTagDBType());
				
				UserDefinedTagDAO.insertTagValues(metaCon, dbTypeProp, tableName,
						this.path.toString(), defaultTags, extraTags, true, tagParser == null ? new TableMetadata("DEFAULT", null) 
								: tagParser.getTableMetaData(UserDefinedTagUtils.getFileExtension(this.path.toString())));
			} finally {
				try
				{
					CoreDBManager.closeConnection(metaCon);
				}
				catch(Exception e)
				{
					AppLogger.getLogger().fatal("Error closing database connection.", e);
				}
			}
		} finally {
			try
			{
				if(dfs!=null)
					dfs.close();
				if(os!=null)	os.flush();
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error flushing input stream.", e);
			}
			try
			{
				if(os!=null)	os.close();
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing input stream.", e);
			}
			try
			{
				CoreDBManager.closeConnection(connection);
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}		
	}
	
	

}
