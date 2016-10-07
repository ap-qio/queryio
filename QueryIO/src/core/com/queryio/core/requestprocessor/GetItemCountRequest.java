package com.queryio.core.requestprocessor;

import java.net.URI;
import java.sql.Connection;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.security.UserGroupInformation;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.database.CoreDBManager;
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
import com.queryio.core.utils.QIODFSUtils;

public class GetItemCountRequest extends RequestProcessorCore {
	private String nodeId;
	long itemCount = 0;
	
	public GetItemCountRequest(String user, String group, Path path, String nodeId) {
		super(user, group, path);
		this.nodeId = nodeId;
	}

	@Override
	public void process() throws Exception {
		this.successful = false;
		
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
						
			if(EnvironmentalConstants.isUseKerberos()){
				User us = UserDAO.getUserDetail(connection, user);
				
				Configuration conf = ConfigurationManager.getKerberosConfiguration(connection, nodeId);
				
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
				Configuration conf = ConfigurationManager.getConfiguration(connection, nodeId);
				dfs = QIODFSUtils.getFileSystemAs(user, group, conf, new URI(fsDefaultName));
			}
			
			this.itemCount = dfs.listStatus(this.path).length;
			
			this.successful = true;
		} finally {
			try
			{
				if(dfs!=null){
					dfs.close();
				}
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
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
	
	public long getItemCount() {
		return this.itemCount;
	}
}
