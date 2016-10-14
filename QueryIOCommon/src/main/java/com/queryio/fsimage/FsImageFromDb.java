package com.queryio.fsimage;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import com.queryio.common.QueryIOConstants;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.log4j.BasicConfigurator;

import com.queryio.userdefinedtags.common.UserDefinedTagResourceFactory;

public class FsImageFromDb {
	public static final Log LOG = LogFactory.getLog(FsImageFromDb.class.getName());
	public static void main(String arg[]) throws Exception{
		Thread.currentThread().setName("queryio");
		BasicConfigurator.configure();
		if(arg.length < 2){
			System.err.println("USAGE: FsImageFromDb <install-dir> <namenode-id> [<backup-dbsource> <backup-id>]");
			System.exit(1);
		}
		LOG.info("Writing args: ");
		for(int i =0 ; i < arg.length; i ++){
			LOG.info((i+1) + ". " + arg[i]);
		}
		String installDir = arg[0];
		String namenodeId = arg[1];
		String backupDBSource = null;
		String backupId = null;

		if(arg.length > 3){
			backupDBSource = arg[2];
			backupId = arg[3];
		}
		
		String nnConfDir = installDir + File.separator + QueryIOConstants.HADOOP_DIR_NAME + File.separator + "etc" + File.separator + "namenode-conf_" + namenodeId;
		Configuration conf = new Configuration(false);
		FileInputStream in = null;
		FileInputStream in2 = null;
		try{
			in = new FileInputStream(new File(nnConfDir + File.separator + "core-site.xml"));
			conf.addResource(in);
			in2 = new FileInputStream(new File(nnConfDir + File.separator + "hdfs-site.xml"));
			conf.addResource(in2);
			
			String storageDir = conf.get(DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY);
			if(backupDBSource != null)
				conf.set(QueryIOConstants.ANALYTICS_DB_DBSOURCEID, backupDBSource);
			UserDefinedTagResourceFactory.initConnection(conf, true);
			Connection connection = null;
			try{
				connection = UserDefinedTagResourceFactory.getConnectionWithoutPoolInit(conf);
				QIOFsImageUtils.constructFSImageFromDB(connection, storageDir, backupId);
			}finally{
				if(connection != null){
					connection.close();
				}
			}

		}finally{
			if(in != null){
				in.close();
			}
			if(in2 != null){
				in2.close();
			}
		}		
	}
}
