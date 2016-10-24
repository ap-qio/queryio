package com.queryio.agent.web;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.remote.NodeOperation;
import com.queryio.common.util.AppLogger;
 
public class LogViewer extends HttpServlet {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 4065765220980456110L;
	public void doGet(HttpServletRequest request, HttpServletResponse response)
             throws ServletException, IOException {
		String folderPath = ""; 
		
		String nodeType = request.getParameter("node-type");
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("nodeType: " + nodeType);
		String hostDirPath = request.getParameter("host-dir");
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("hostDirPath: " + hostDirPath);
		String fileType = request.getParameter("file-type");
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("fileType: " + fileType);
		if (nodeType.equals("Agent")){
			 
			 folderPath = hostDirPath + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/webapps/" + QueryIOConstants.AGENT_QUERYIO + "/logs/";
			 
		}
		else if(nodeType.equals("S3Server") || nodeType.equals("FTPServer"))
		{
			 folderPath = hostDirPath + QueryIOConstants.QUERYIOSERVERS_DIR_NAME + "/logs/";
		}
		else if(nodeType.equals("HiveServer"))
		{
			folderPath = hostDirPath + QueryIOConstants.HIVE_DIR_NAME + "/logs/";
		}
		else if(nodeType.equals("containerLog"))
		{
			 folderPath = hostDirPath;
		}
		else{
			 String nodeId = request.getParameter("node-id");
			 if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("nodeId: " + nodeId);
			 ArrayList keys = new ArrayList();
			 if(nodeType.equals(QueryIOConstants.NAMENODE) || nodeType.equals(QueryIOConstants.DATANODE)){
				 keys.add(QueryIOConstants.HADOOP_LOG_DIR_KEY);
				 ArrayList values = NodeOperation.getConfiguration(hostDirPath, nodeType, nodeId, "core-site.xml", keys);
				 if(values.size() > 0){
					 folderPath = (String)(values.get(0));	 
					 if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("folderPath: " + folderPath);
				 }			 
			 }else {
				 keys.add(QueryIOConstants.YARN_LOG_DIR_KEY);
				 ArrayList values = NodeOperation.getConfiguration(hostDirPath, nodeType, nodeId, "yarn-site.xml", keys);
				 if(values.size() > 0){
					 folderPath = (String)(values.get(0));	
					 if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("folderPath: " + folderPath);
				 }
			 }
		}
			 PrintWriter pw = response.getWriter();
			 
			 if(folderPath.isEmpty())
			 {
				 pw.println("Log directory not set.");
			 }
			 else
			 {
				 String fileName = "";
				 if(nodeType.equals("S3Server"))
					 fileName = "os3server.log";
				 else if(nodeType.equals("FTPServer"))
					 fileName = "ftpserver.log";
				 else if(nodeType.equals("HiveServer"))
					 fileName = "out.log";
				 else if(nodeType.equals("containerLog"))
				 {
					 fileName = ".txt";
				 }
				 else
				 {
					 File folder = new File(folderPath);
					 String[] list = folder.list();
					 for(String file : list)
					 {
						 if(getFileExtention(file).equals(fileType))
						 {
							 fileName = file;
							 if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("fileName: " + fileName);
							 break;
						 }
					 }
				 }
				 if(fileName.isEmpty())
				 {
					 pw.println("Requested file-type does not exist in log folder.");
				 }
				 else
				 {
					 BufferedReader reader = null;
					 try {
						 response.setContentType("text/html");
						 String filePath = "";
						 if(nodeType.equals("containerLog"))
							 filePath = folderPath;
						 else
							 filePath = folderPath +"/"+ fileName;
					     if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("filePath: " + filePath);
					     
					     reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));    
	
				         String text = "";
				         while ((text = reader.readLine()) != null) {
				        	 pw.println(text);
				        	 pw.println("<br/>");
				         }
					     
					 } catch(Exception e) {
						 AppLogger.getLogger().fatal(e.getMessage(), e);
					     pw.println(e.getMessage());   
					 }
				 }
			 }	
	}
	 protected void doPost(HttpServletRequest request,
			 HttpServletResponse response) throws ServletException, IOException {
		 doGet(request, response);
	 }
	 private static String getFileExtention(String filePath) {
		char extensionSeparator = '.';
		int dot = filePath.lastIndexOf(extensionSeparator);
	    return filePath.substring(dot + 1);
	}
}