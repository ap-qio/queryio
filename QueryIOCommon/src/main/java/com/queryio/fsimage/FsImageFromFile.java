package com.queryio.fsimage;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.log4j.BasicConfigurator;

import com.queryio.common.database.HDFSMetadataReader;
import com.queryio.common.database.NSMetadataReader;
import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;

public class FsImageFromFile {
	public static final Log LOG = LogFactory.getLog(FsImageFromFile.class.getName());
	public static void main(String arg[]) throws Exception{
		BasicConfigurator.configure();
		String storageDir = null;
		Thread.currentThread().setName("queryio");
		BasicConfigurator.configure();
		
		if(arg.length < 3){
			System.err.println("USAGE: FsImageFromFile <install-dir> <namenode-id> <file-path>");
			System.exit(1);
		}
		LOG.info("Writing args: ");
		for(int i =0 ; i < arg.length; i ++){
			LOG.info((i+1) + ". " + arg[i]);
		}
		String installDir = arg[0];
		String namenodeId = arg[1];
		String filePath = arg[2];
		
		String nnConfDir = installDir + File.separator + QueryIOConstants.HADOOP_DIR_NAME + File.separator + "etc" + File.separator + "namenode-conf_" + namenodeId;
		Configuration conf = new Configuration(false);
		FileInputStream in = null;
		FileInputStream in2 = null;
		Connection connection = null;
		NSMetadataReader nsMetadataReader = null;
		HDFSMetadataReader hdfsMetadataReader = null;
		try{
			in = new FileInputStream(new File(nnConfDir + File.separator + "core-site.xml"));
			conf.addResource(in);
			in2 = new FileInputStream(new File(nnConfDir + File.separator + "hdfs-site.xml"));
			conf.addResource(in2);
			
			UserDefinedTagResourceFactory.initConnection(conf, true);
			if(storageDir == null)
				storageDir = conf.get(DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY);
			
			QIOFsImageUtils.constructFsImageFromFile(nsMetadataReader, hdfsMetadataReader, storageDir, filePath);
		
		}finally{
			CoreDBManager.closeConnection(connection);
			if(in != null){
				in.close();
			}
			if(in2 != null){
				in2.close();
			}
			if(nsMetadataReader!=null) {
				nsMetadataReader.close();
			}
			if(hdfsMetadataReader!=null) {
				hdfsMetadataReader.close();
			}
		}		
	}
}
