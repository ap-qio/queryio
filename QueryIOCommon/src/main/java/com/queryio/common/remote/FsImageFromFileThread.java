package com.queryio.common.remote;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Timestamp;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.dao.NNRestoreInfoDAO;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StreamPumper;
import com.queryio.fsimage.FsImageFromFile;

public class FsImageFromFileThread extends Thread{
	String id, backupId, namenodeId, installDir, backupFileSourcePath;
	FsImageFromFileThread(String id, String namenodeId, String installDir, String backupFileSourcePath, String backupId){
		this.id = id;
		this.namenodeId = namenodeId;
		this.installDir = installDir;
		this.backupFileSourcePath = backupFileSourcePath;
		this.backupId = backupId;
	}
	Process p = null;
	public void run(){
		BufferedWriter out = null;
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			NNRestoreInfoDAO.addRestoreInfo(connection, id, backupId, namenodeId);
			
			out = new BufferedWriter(new FileWriter(new File(EnvironmentalConstants.getAppHome() + File.separator + "logs" + File.separator + id + ".log")));
			
			String classpathString = "";
			File f = new File(installDir + "/" + QueryIOConstants.HADOOP_DIR_NAME  + "/share/hadoop/common/lib");
			File[] files = f.listFiles();
			for(File file : files){
				if(file.getName().endsWith(".jar"))
					classpathString += file.getAbsolutePath() + ":";
			}
			classpathString += installDir + "/" +QueryIOConstants.HADOOP_DIR_NAME  + "/share/hadoop/common/QueryIOPlugins.jar:";
			classpathString += installDir + "/" +QueryIOConstants.HADOOP_DIR_NAME  + "/share/hadoop/common/QueryIOCommon.jar:";
			classpathString += installDir + "/" +QueryIOConstants.QUERYIOSERVERS_DIR_NAME  + "/lib/hadoop-custom-compiled.jar:";
			classpathString += installDir + "/" +QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/webapps/agentqueryio/WEB-INF/lib/QueryIOAgent.jar:";
			classpathString += installDir + "/" +QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/webapps/agentqueryio/WEB-INF/lib/xstream-1.4.4.jar:";
			classpathString += installDir + "/" +QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/webapps/agentqueryio/WEB-INF/lib/stax-1.2.0.jar:";
			classpathString += installDir + "/" +QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/webapps/agentqueryio/WEB-INF/lib/stax-api-1.0.1.jar: ";
			String arguments = installDir + " " + namenodeId + " " + backupFileSourcePath;
			String command = System.getProperty("java.home") + "/bin/java -cp " + classpathString + FsImageFromFile.class.getCanonicalName() + " " + arguments;
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug(command);
			
			p = Runtime.getRuntime().exec(command);
			
			final StreamPumper spInput = new StreamPumper(new BufferedReader(new InputStreamReader(p
					.getInputStream())), out);
			spInput.start();
			
			if (p.getErrorStream() != null)
			{
				final StreamPumper spError = new StreamPumper(new BufferedReader(new InputStreamReader(p
						.getErrorStream())), out);
				spError.start();
			}
			
			int returnValue = p.waitFor();
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Process Exited with return value: " + returnValue);

			long savePt = System.currentTimeMillis();
			NNRestoreInfoDAO.updateRestoreInfo(connection, id, new Timestamp(savePt), returnValue == 0 ? QueryIOConstants.PROCESS_STATUS_COMPLETED : QueryIOConstants.PROCESS_STATUS_FAILED);					
			
	    }catch(Exception e){
	    	AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
	    }finally{
	    	if(out != null)
	    		try{
	    			out.close();	
	    		}catch(Exception e){
	    			AppLogger.getLogger().fatal("Error closing Log output stream", e);		
	    		}
	    	try{
	    		CoreDBManager.closeConnection(connection);	
    		}catch(Exception e){
    			AppLogger.getLogger().fatal("Error closing Database connection.", e);		
    		}
	    	
	    }
	}
	public void interrupt(){
		if(p != null)
			p.destroy();
		Connection connection = null;
		try{
			long savePt = System.currentTimeMillis();
			NNRestoreInfoDAO.updateRestoreInfo(connection, id, new Timestamp(savePt), QueryIOConstants.PROCESS_STATUS_STOPPED);
		 }catch(Exception e){
		    	AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
		    }finally{
		    	
		    	try{
		    		CoreDBManager.closeConnection(connection);	
	    		}catch(Exception e){
	    			AppLogger.getLogger().fatal("Error closing Database connection.", e);		
	    		}
		    	
		    }
		super.interrupt();
	}
}
