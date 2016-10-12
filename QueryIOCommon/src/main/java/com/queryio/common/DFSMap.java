package com.queryio.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.log4j.Logger;
import com.queryio.common.EnvironmentalConstants;

public class DFSMap {
	
	protected final static Logger logger = Logger.getLogger(DFSMap.class);

	private static HashMap<String, FileSystem> dfsMap = new HashMap<String, FileSystem>();
	private static HashMap<String, String> tokenMap = new HashMap<String, String>();
	
	public static synchronized void updateDFSConfig(){
		Iterator<String> i = dfsMap.keySet().iterator();
		while(i.hasNext()){
			String user = i.next();
			FileSystem fs;
			try {
				Thread.currentThread().setName(user);
				fs = FileSystem.get(HadoopConstants.getHadoopConf());
				dfsMap.remove(user);
				dfsMap.put(user, fs);
				System.out.println("updated dfs instance for user " + user);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static synchronized void addDFS(String username, FileSystem dfs){
		if(dfsMap.containsKey(username))	
			dfsMap.remove(username);
		
		dfsMap.put(username,  dfs);
	}
	
	public static synchronized void removeDFS(String username){
		if(dfsMap.containsKey(username))	dfsMap.remove(username);
	}
	
	public static synchronized FileSystem getDFSForUser(String username){
		return dfsMap.get(username);
	}
	
	public static synchronized void addToken(String token, String username){
		if(tokenMap.containsKey(token))		tokenMap.remove(token);
		
		tokenMap.put(token, username);
	}
	
	public static synchronized void removeToken(String token){
		if(tokenMap.containsKey(token))		tokenMap.remove(token);
	}
	
	public static synchronized String getUserForToken(String token){
		return tokenMap.get(token);
	}

	public static HdfsConfiguration getKerberosConfiguration(){
		HdfsConfiguration conf = DFSMap.getConfiguration();
		
		conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION, "kerberos");
		conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHORIZATION, "true");
	
		conf.set(DFSConfigKeys.DFS_NAMENODE_USER_NAME_KEY, EnvironmentalConstants.getNnUserName());
		
		// DFS_NAMENODE_USER_NAME_KEY needs to be set in hadoop.properties - [ dfs.namenode.kerberos.principal ]
		
		return conf;
	}
	
	public static HdfsConfiguration getConfiguration(){
		HdfsConfiguration conf = new HdfsConfiguration();
        
		conf.set(DFSConfigKeys.FS_DEFAULT_NAME_KEY, HadoopConstants.getHadoopConf().get(DFSConfigKeys.FS_DEFAULT_NAME_KEY));
        conf.set(DFSConfigKeys.DFS_REPLICATION_KEY, HadoopConstants.getHadoopConf().get(DFSConfigKeys.DFS_REPLICATION_KEY));
        
        // Set customtagdb properties

        Iterator<Entry<String,String>> i = HadoopConstants.getHadoopConf().iterator();
        Entry<String, String> entry;
        while(i.hasNext()){
        	entry = i.next();       
        	conf.set(entry.getKey(), entry.getValue());
        }

        return conf;
    }
}