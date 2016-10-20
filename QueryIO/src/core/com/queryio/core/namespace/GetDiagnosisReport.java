package com.queryio.core.namespace;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.util.AppLogger;

public class GetDiagnosisReport extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String diagnosisId = String.valueOf(request.getParameter("diagnosisId"));
		String rows=request.getParameter("rows");
		String pageno=request.getParameter("page");
	    String sidx=request.getParameter("sidx");
	    String sord=request.getParameter("sord");
	    
	    int cpage=pageno!=null?Integer.parseInt(pageno):0;
        int crow=rows!=null?Integer.parseInt(rows):0;
	    
        int endIndex = cpage*crow;
		int startIndex = endIndex==crow?0:endIndex-crow-1;
		
		JSONObject object = DiagnosisAndRepairManager.getDiagnosisReport(diagnosisId, startIndex, endIndex);
		
		JSONArray conflicts = (JSONArray) object.get("conflicts");
		
		int records = Integer.valueOf(String.valueOf(object.get("endIndex")));
		int total = 1;
		
		if (crow > 0)
		{
			total = records/crow;
			if ((records % crow) > 0)
				total = total + 1;			
		}
		
		JSONObject rowData = new JSONObject();
		
		JSONArray rowArray = new JSONArray();
		  
		
		rowData.put("total",total);
        rowData.put("page",cpage);
        rowData.put("records",records);
        
        for(int i=0;i<conflicts.size();i++){
        	JSONObject conflictObject = (JSONObject) conflicts.get(i);
        	JSONObject rowObject = new JSONObject();
        	rowObject.put("id",i+1);
        	JSONArray cell = new JSONArray();
        	cell.add(conflictObject.get("filePath"));
        	if(conflictObject.get("conflictTypeDescription").equals(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERNECE_DESC))
        		cell.add(NamespaceConflict.CONFLICT_TYPE_METADATA_DIFFERNECE_DESC + " <a href='javascript:BAR.showConflict(\"conflictDiv_" + (i + 1) + "\");'>Details</a><div id='conflictDiv_" + (i + 1) + "' style = 'display : none;'>" + conflictObject.get("conflictInfo") + "</div>");
        	else
        		cell.add(conflictObject.get("conflictTypeDescription"));
        	rowObject.put("cell",cell);
        	rowArray.add(rowObject);
        }
        
        
        
        rowData.put("rows",rowArray);
       
		response.setContentType("application/json");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.write(rowData.toJSONString());
			
			writer.flush();
		} catch (IOException e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
	}
}