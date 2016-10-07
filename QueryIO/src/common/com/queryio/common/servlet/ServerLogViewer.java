package com.queryio.common.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.util.AppLogger;

public class ServerLogViewer extends HttpServlet{
	private static final long serialVersionUID = 4065765220980456110L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String filePath = null;
		String folderPath = EnvironmentalConstants.getAppHome() + "../../logs/";
		String fileType = request.getParameter("file-type");
		String data = "";
		BufferedReader reader = null;
		FileInputStream fis = null;
		InputStreamReader is= null;
		if (fileType == null) {
			fileType = "log";
		} else if (fileType.equals("notificationLog")) {
			folderPath = request.getParameter("LogPath");
		} else {
			fileType = "log";
		}
		PrintWriter pw = response.getWriter();
		if (folderPath.isEmpty()) {
			pw.println("Log directory not set.");
		} else {
			try{
			File folder = new File(folderPath);
			String fileName = "AppQueryIO.log";
			response.setContentType("text/html");
			if (fileType.equals("notificationLog")) {
				FileReader fr = new FileReader(folder);
				reader = new BufferedReader(fr);
				while ((data = reader.readLine()) != null) {
					pw.println(data);
					pw.println("<br/>");
				}
			} else {
				filePath = folderPath + File.separator + fileName;
				fis = new FileInputStream(filePath);
				is = new InputStreamReader(fis);
				reader = new BufferedReader(is);
				while ((data = reader.readLine()) != null) {
					pw.println(data);
					pw.println("<br/>");
				}

			}
			}catch(Exception e){
				AppLogger.getLogger().fatal("Exception occured while reading log"+e.getMessage());
				pw.println("Exception occured while reading log"+e.getMessage());
			}finally{
				if (fis!=null) {
					fis.close();
				}
				if (is!=null) {
					is.close();
				}
				if(reader!=null){
					reader.close();
				}
			}
		}
	}
	 protected void doPost(HttpServletRequest request,
			 HttpServletResponse response) throws ServletException, IOException {
		 doGet(request, response);
	 }
//	 private static String getFileExtention(String filePath) {
//		char extensionSeparator = '.';
//		int dot = filePath.lastIndexOf(extensionSeparator);
//	    return filePath.substring(dot + 1);
//	}
}
