package com.queryio.core.requestprocessor;

// package com.queryio.core.requestprocessor;
//
// import java.net.URI;
//
// import org.apache.hadoop.fs.FileSystem;
// import org.apache.hadoop.hdfs.HdfsConfiguration;
// import org.apache.hadoop.security.UserGroupInformation;
//
// import com.queryio.common.EnvironmentalConstants;
// import com.queryio.common.util.AppLogger;
// import com.queryio.core.dfsmanager.DFSMap;
//
// public class LoginRequest extends RequestProcessorCore{
// FileSystem dfs;
// String password;
//
// public LoginRequest(String user, String password) {
// super(user, null);
// this.password = password;
// }
//
// public void run(){
// HdfsConfiguration conf = null;
// try {
// conf = DFSMap.getKerberosConfiguration();
//
// UserGroupInformation.setConfiguration(conf);
// UserGroupInformation.getLoginUser(user, password);
//
// dfs = FileSystem.get(new URI(EnvironmentalConstants.getFsDefaultName()),
// conf);
// dfs.getStatus();
// } catch (Exception e) {
// AppLogger.getLogger().fatal(e.getMessage(), e);
// this.successful = false;
// }
// }
//
// public FileSystem getDFS(){
// return this.dfs;
// }
// }
