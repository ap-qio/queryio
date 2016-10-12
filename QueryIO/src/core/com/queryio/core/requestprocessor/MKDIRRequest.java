package com.queryio.core.requestprocessor;

import java.net.URI;
import java.sql.Connection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SecurityHandler;
import com.queryio.core.bean.User;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.utils.QIODFSUtils;

public class MKDIRRequest extends RequestProcessorCore{
	private Configuration conf;
	private String fsDefaultName;
	public MKDIRRequest(String nodeId, String fsDefaultName, String user, String group, Path path, Configuration conf) {
		super(user, group, path);
		this.conf = conf;
		this.fsDefaultName = fsDefaultName;
	}

	public void process() throws Exception{
		this.successful = false;
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("MKDIRRequest, user: " + this.user);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("MKDIRRequest, group: " + this.group);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("FsDefaultName: " + this.fsDefaultName);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Path: " + this.path);
		
		Connection connection = null;
		
		FileSystem dfs = null;
		
		try
		{
			if(EnvironmentalConstants.isUseKerberos()){
				connection = CoreDBManager.getQueryIODBConnection();
				User us = UserDAO.getUserDetail(connection, user);
				
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
				dfs = QIODFSUtils.getFileSystemAs(user, group, conf, new URI(fsDefaultName));
			}
			
			dfs.mkdirs(this.path);
			dfs.setOwner(path, this.user, this.group);
			this.successful = true;
		} finally {
			try
			{	
				if(dfs!=null)
					dfs.close();
				if(connection != null){
					CoreDBManager.closeConnection(connection);
				}
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
		}
	}
}
