package com.queryio.common.cusrtomdatasource;

import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.queryio.common.util.AppLogger;



public class ReportDataConnection {
	public static int size;
	
	public ReportDataHandler getReportDataHandler(String filePath) {
		  JSONParser parser = new JSONParser();
		  ReportDataHandler data = null;
			try {
				AppLogger.getLogger().debug("filePath : " + filePath);
				JSONObject obj = (JSONObject) parser.parse(new FileReader(filePath));
		 
				JSONArray columns = (JSONArray) obj.get("columns");
				
				JSONArray rows = (JSONArray) obj.get("rows");
				
						
				
				data = new ReportDataHandler();
				data.setColumn(columns);
				data.setRows(rows);
				size = rows.size();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e);
				e.printStackTrace();
			}
			return data;
	  }
	public static void main(String []arg){
		
		int count = 0;
		ReportDataConnection rdh = new ReportDataConnection();
		ReportDataHandler reportDataHandler = rdh
				.getReportDataHandler("/QueryIO/tomcat/webapps/queryio/Reports/a/namenode/New Query 2.json");
		int maxrecord = reportDataHandler.getRowsLength(300);
		if (count < maxrecord) {
			reportDataHandler.getColumnValue(count,
					"FILEPATH");
			reportDataHandler.getColumnValue(count, "LEN");
					count++;
			
		}
		
	}
}
