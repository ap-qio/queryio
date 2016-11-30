package com.queryio.core.authentication;

// package com.queryio.core.authentication;
//
// import java.io.IOException;
// import java.net.URI;
// import java.net.URISyntaxException;
//
// import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
// import org.apache.hadoop.fs.FileSystem;
// import org.apache.hadoop.hdfs.DFSConfigKeys;
// import org.apache.hadoop.hdfs.HdfsConfiguration;
// import org.apache.hadoop.security.UserGroupInformation;
//
// import com.queryio.common.EnvironmentalConstants;
// import com.queryio.common.util.AppLogger;
// import com.queryio.core.dfsmanager.DFSMap;
// import com.queryio.core.requestprocessor.LoginRequest;
//
// public class Authenticator {
// public static boolean authenticate(String username, String password){
//
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug("Authenticating with kerberos");
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug("Username: " + username);
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug("Password: " + password);
//
// boolean success = false;
//
// LoginRequest request = new LoginRequest(username, password);
// request.process();
//
// try {
// request.join();
// } catch (InterruptedException e) {
// AppLogger.getLogger().fatal(e.getMessage(), e);
// }
//
// if(request.isSuccessFul()){
// DFSMap.addDFS(username, request.getDFS());
// success = true;
//
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug("Authentication successful");
// } else {
// if(AppLogger.getLogger().isDebugEnabled())
// AppLogger.getLogger().debug("Authentication failed");
// }
//
// return success;
// }
//
// public static void main(String[] args) throws IOException,
// URISyntaxException{
//
// System.setProperty( "sun.security.krb5.debug", "true");
// System.setProperty( "java.security.krb5.realm", "queryiorealm");
// System.setProperty( "java.security.krb5.kdc", "192.168.0.19");
//
// String fsDefaultName = "hdfs://192.168.0.12:9000";
//
// HdfsConfiguration conf = new HdfsConfiguration();
// conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION,
// "kerberos");
// conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHORIZATION,
// "true");
//
// conf.set(DFSConfigKeys.DFS_NAMENODE_USER_NAME_KEY,
// EnvironmentalConstants.getNnUserName());
//
// UserGroupInformation.setConfiguration(conf);
// UserGroupInformation.getLoginUser("eshan", "12345");
//
// FileSystem dfs = FileSystem.get(new URI(fsDefaultName), conf);
// dfs.getStatus();
//
// }
// }
