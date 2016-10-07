package com.queryio.ftpserver.requestprocessor;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Logger;

import com.queryio.common.DFSMap;
import com.queryio.common.EnvironmentalConstants;
import com.queryio.ftpserver.core.HdfsUser;
import com.queryio.ftpserver.core.QIODFSUtils;
import com.queryio.ftpserver.userinfo.UserInfoContainer;

public class LoginRequest extends RequestProcessorCore{
	private static Logger log = Logger.getLogger(LoginRequest.class);
	
	private FileSystem fileSystemDFS;
	
	public LoginRequest(HdfsUser user, Path path) {
		super(user, path);
	}
	
	public Object process() throws Exception{
		FileSystem fs = null;
		if(EnvironmentalConstants.isUseKerberos()){
			final Configuration conf = DFSMap.getKerberosConfiguration();
		
			UserGroupInformation.setConfiguration(conf);
			UserGroupInformation.getLoginUser(user.getName(), user.getPassword());
				
			fs = FileSystem.get(conf);
			fs.getStatus();
		} else {
			if( ! UserInfoContainer.validateUser(user.getName(), user.getPassword())){
				return null;
			}
			final Configuration conf = DFSMap.getConfiguration();
			
			fs = QIODFSUtils.getFileSystemAs(
					user.getName(), user.getDefaultGroup(), conf);
			fs.getStatus();
		}
		return fs;
	}

	public FileSystem getDFS(){
		return this.fileSystemDFS;
	}
}
