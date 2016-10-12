package com.queryio.core.dfsmanager;
//package com.queryio.core.dfsmanager;
//
//import java.sql.Connection;
//import java.util.HashMap;
//
//import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.hdfs.DFSConfigKeys;
//import org.apache.hadoop.hdfs.HdfsConfiguration;
//
//import com.queryio.common.EnvironmentalConstants;
//import com.queryio.common.database.CoreDBManager;
//import com.queryio.common.util.AppLogger;
//import com.queryio.core.agent.QueryIOAgentManager;
//import com.queryio.core.bean.Host;
//import com.queryio.core.bean.Node;
//import com.queryio.core.dao.HostDAO;
//import com.queryio.core.dao.NodeDAO;
//
//public class DFSMap {
//	private static HashMap<String, FileSystem> dfsMap = new HashMap<String, FileSystem>();
//	
//	public static synchronized void addDFS(String username, FileSystem dfs){
//		if(dfsMap.containsKey(username))	dfsMap.remove(username);
//		
//		dfsMap.put(username,  dfs);
//	}
//	
//	public static synchronized void removeDFS(String username){
//		if(dfsMap.containsKey(username))	dfsMap.remove(username);
//	}
//	
//	public static synchronized FileSystem getDFSForUser(String username){
//		return dfsMap.get(username);
//	}
//	
//	public static HdfsConfiguration getKerberosConfiguration() throws Exception{
//		Connection connection = null;
//		try
//		{	
//			connection = CoreDBManager.getQueryIODBConnection();
//			
//			Node node = NodeDAO.getNamenode(connection);
//			if(node==null)	return null;
//			
//			Host host = HostDAO.getHostDetail(connection, node.getHostId());
//			String[] result = QueryIOAgentManager.getConfig(host, new String[]{DFSConfigKeys.FS_DEFAULT_NAME_KEY}, node, "core-site.xml");
//			if(result == null || result.length == 0){
//				return null;
//			}
//			
//			String fsDefaultName = result[0];
//			
//			HdfsConfiguration conf = new HdfsConfiguration();
//			conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION, "kerberos");
//			conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHORIZATION, "true");
//			
//			conf.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY, fsDefaultName);
//			
//			conf.set(DFSConfigKeys.DFS_NAMENODE_USER_NAME_KEY, EnvironmentalConstants.getNnUserName());
//			
//			return conf;
//			
//		}finally{
//			try
//			{
//				if(connection != null)
//					CoreDBManager.closeConnection(connection);
//			}
//			catch(Exception e)
//			{
//				AppLogger.getLogger().fatal("Error closing database connection.", e);
//			}
//		}
//	}
//}
