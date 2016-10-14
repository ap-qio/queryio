package com.queryio.common.remote;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.AppLogger;

public class UpgradeHadoop2_4_0To2_7_0 {
	
	public static void startUpgrade(String path) throws Exception
	{
		updateSiteFile(path + QueryIOConstants.HIVE_DIR_NAME + "/conf/hive-env.sh" , true);
		updateSiteFile(path + QueryIOConstants.HIVE_DIR_NAME + "/conf/hive-site.xml" , true);
		String hPath = path+ "/" + QueryIOConstants.HADOOP_DIR_NAME + "/etc";
		updateHadoopPath(hPath);
		upgradeNameNode(path);
	}
	
	public static void updateHadoopPath(String path) throws Exception
	{
		File f = new File(path);
		for (File file : f.listFiles())
		{
			if(file.getPath().contains("core-site.xml") || file.getPath().contains("hdfs-site.xml") || file.getPath().contains("hadoop-env.sh") || file.getPath().contains("ssl-client.xml") || file.getPath().contains("ssl-server.xml") || file.getPath().contains("yarn-env.sh") || file.getPath().contains("yarn-site.xml") || file.getPath().contains("mapred-env.sh") ||  file.getPath().contains("capacity-scheduler.xml"))
			{
				updateSiteFile(file.getPath(), false);
			}
			if (file.isDirectory())
			{
				updateHadoopPath(file.toString());
			}
		}
	}
  	
  	public static void updateSiteFile(String filePath, boolean isHive) throws Exception
  	{
  		File in = new File(filePath);
		File out = new File(filePath);
  		
  		BufferedReader reader = null;
	    Writer writer = null;
	    ArrayList lines = new ArrayList();
	    try
	    {
	    	if(in.exists())
	    	{
		    	reader = new BufferedReader(new FileReader(in));	    	
		    	String line = null;
		    	while ((line = reader.readLine()) != null){
		    		if(line.contains(QueryIOConstants.HADOOP_2_0_3_DIR_NAME))
		    			line = line.replace(QueryIOConstants.HADOOP_2_0_3_DIR_NAME , QueryIOConstants.HADOOP_DIR_NAME);
		    		else if(line.contains(QueryIOConstants.HADOOP_2_0_4_DIR_NAME))
		    			line = line.replace(QueryIOConstants.HADOOP_2_0_4_DIR_NAME , QueryIOConstants.HADOOP_DIR_NAME);
		    		else if(line.contains(QueryIOConstants.HADOOP_2_2_0_DIR_NAME))
		    			line = line.replace(QueryIOConstants.HADOOP_2_2_0_DIR_NAME , QueryIOConstants.HADOOP_DIR_NAME);
		    		else if(line.contains(QueryIOConstants.HADOOP_2_4_0_DIR_NAME))
		    			line = line.replace(QueryIOConstants.HADOOP_2_4_0_DIR_NAME , QueryIOConstants.HADOOP_DIR_NAME);
		    		if(line.contains(QueryIOConstants.NM_AUX_SERVICE))
		    			line = line.replace(QueryIOConstants.NM_AUX_SERVICE , QueryIOConstants.NM_AUX_SERVICE_UPDATE);
		    		if(isHive && line.contains(QueryIOConstants.HIVE_0_10_0_DIR_NAME))
		    			line = line.replace(QueryIOConstants.HIVE_0_10_0_DIR_NAME , QueryIOConstants.HIVE_DIR_NAME);
		    		if(isHive && line.contains(QueryIOConstants.HIVE_0_11_0_DIR_NAME))
		    			line = line.replace(QueryIOConstants.HIVE_0_11_0_DIR_NAME , QueryIOConstants.HIVE_DIR_NAME);
		    		if(isHive && line.contains(QueryIOConstants.HIVE_0_12_0_DIR_NAME))
		    			line = line.replace(QueryIOConstants.HIVE_0_12_0_DIR_NAME , QueryIOConstants.HIVE_DIR_NAME);
		    		if(isHive && line.contains(QueryIOConstants.HIVE_0_13_0_DIR_NAME))
		    			line = line.replace(QueryIOConstants.HIVE_0_13_0_DIR_NAME , QueryIOConstants.HIVE_DIR_NAME);
		    		if(line.contains(QueryIOConstants.RM_RESOURCE_CALC))
		    			line = line.replace(QueryIOConstants.RM_RESOURCE_CALC , QueryIOConstants.RM_RESOURCE_CALC_UPDATE);
		    		
		    		if(line.trim().startsWith("export YARN_HOME=")) {
		    			line = line.replace("export YARN_HOME=", "export HADOOP_YARN_HOME=");
		    		}
		    		
		    		// With hadoop 2.7.0 we have started using hive 1.2.0. Hive JDBC Driver class and hive JDBC url has been updated in this version
		    		// 1. JDBC Driver org.apache.hadoop.hive.jdbc.HiveDriver  => org.apache.hive.jdbc.HiveDriver
		    		// 2. JDBC URL 	jdbc:hive://localhost:10000/default => jdbc:hive2://localhost:10000/default
		    		
		    		if(line.trim().contains("org.apache.hadoop.hive.jdbc.HiveDriver")) {
		    			line = line.replace("org.apache.hadoop.hive.jdbc.HiveDriver", "org.apache.hive.jdbc.HiveDriver");
		    		}
		    		
		    		if(line.trim().contains("jdbc:hive:")) {
		    			line = line.replace("jdbc:hive:", "jdbc:hive2:");
		    		}
		    		
	    			lines.add(line);
		    	}
		    	
		    	
		    	writer = new BufferedWriter(new FileWriter(out));		    	
		    	for(int i=0; i<lines.size(); i++)
		    	{
		    		writer.write((String)lines.get(i));
		    		writer.write("\n");
		    	}
	    	}
	    }
	    finally
	    {
	    	try
	    	{
	    		if(reader != null)
	    			reader.close();
	    	}
	    	catch(Exception e)
	    	{
	    		AppLogger.getLogger().fatal(e.getMessage(), e);
	    	}
	    	try
	    	{
	    		if(writer != null)
	    			writer.close();
	    	}
	    	catch(Exception e)
	    	{
	    		AppLogger.getLogger().fatal(e.getMessage(), e);
	    	}
	    }
  	}
  	
  	public static void upgradeNameNode(String path)
	{
		String command = "";
		try
		{
			command = "sh " + path + "/bin/upgrade_namenode.sh";
			AppLogger.getLogger().fatal("command : " + command);
			NodeOperation.executeCommand(command);
        }
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}
  	
	

}
