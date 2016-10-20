
package com.queryio.core.databrowser;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.core.bean.Node;
import com.queryio.core.conf.RemoteManager;
import com.queryio.core.monitor.beans.SummaryTable;

/**
 * Servlet implementation class DataBrowserManager
 */

public class DataBrowserManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Map<String, Integer> columnMap = new HashMap<String, Integer>();
	
    public DataBrowserManager() {

    }
    public void init(ServletConfig config) throws ServletException {
    	System.out.println("Ninja");
    	System.out.println("Ninja Bro");
		super.init(config);
		int count=0;
		columnMap.put("Name",new Integer(count++));
		columnMap.put("Size",new Integer(count++));
		columnMap.put("Replicas",new Integer(count++));
		columnMap.put("Last_Read",new Integer(count++)); 
		columnMap.put("Last_Write",new Integer(count++));
		columnMap.put("Permission",new Integer(count++));
		columnMap.put("Owner",new Integer(count++));
		columnMap.put("Group",new Integer(count++));
		columnMap.put("Compression",new Integer(count++));
		columnMap.put("Encryption",new Integer(count++));
		columnMap.put("Type",new Integer(count++));
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Data Browser Servlet Intialized");
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}
	@SuppressWarnings("unchecked")
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("Nishant jain");
		String query = request.getParameter("query");
		String rows=request.getParameter("rows");
		String pageno=request.getParameter("page");
	    String sidx=request.getParameter("sidx");
	    String sord=request.getParameter("sord");
		String nodeId =request.getParameter("nodeId");
		String dirPath=request.getParameter("dirPath");
		
	    if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("query: "+query);
	    if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Request received for dirPath(Encoded): "+dirPath+" For Node "+nodeId);
	    
        JSONObject rowData = new JSONObject();
        JSONArray rowArray = new JSONArray();
        int cpage=pageno!=null?Integer.parseInt(pageno):0;
        int crow=rows!=null?Integer.parseInt(rows):0;
    
        if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("fetching summary table for nodeId: " + nodeId);
        SummaryTable summaryTable = null;
        if(query==null){
        	summaryTable = RemoteManager.listFiles(nodeId, request.getRemoteUser(), dirPath, crow, cpage);
        } else {
        	summaryTable = RemoteManager.listSelectedFiles(nodeId, request.getRemoteUser(), dirPath, crow, cpage, query);
        }
        if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("got summary table");
        
        if( summaryTable != null && summaryTable.getTotalRowCount()!=0 ){
	        	ArrayList summaryRow = summaryTable.getRows();
	        	DataBrowserComparator comparator = new DataBrowserComparator();
	        	comparator.setSortColIndex(sidx==null||sidx.length()==0?0:columnMap.get(sidx).intValue()); //default sort according to name
	        	comparator.setSorder(sord==null?"asc":sord);
	        	Collections.sort(summaryRow,comparator);	
		        int totalRecord =  crow != 0 && crow < summaryTable.getTotalRowCount()?(int)Math.ceil((float)summaryTable.getTotalRowCount()/crow):1;
		        rowData.put("total",totalRecord);
		        rowData.put("page",cpage);
		        
//		        rowData.put("records",summaryRow.size());
		        rowData.put("records",summaryTable.getTotalRowCount());
		        
		            for(int i = 0;i<summaryRow.size();i++){
		            	ArrayList summaryRowArray = (ArrayList) summaryRow.get(i);
			        	JSONObject rowObject = new JSONObject();    
			        	rowObject.put("id", i+1);
			        	JSONArray tableRow = new JSONArray();
			        	
//				        	System.out.println(summaryRowArray);
			        	
			        	boolean found = false;
			        	ArrayList nodes = RemoteManager.getNameNodes();
			        	for(int index=0; index<nodes.size(); index++){
			        		if(((Node) nodes.get(index)).getId().equals(nodeId)){
			        			found = true;
			        		}
			        	}
			        	
			        	if( ! found){
			        		if(nodes.size()>0){
			        			nodeId = ((Node) nodes.get(0)).getId();
			        		}
			        	}
			        	
			        	for(int j = 0;j<summaryRowArray.size()-1;j++){
			        			if(j==0){
			        				String fileName = (String) summaryRowArray.get(0);
			        				String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
			        				
			        				if(Collections.binarySearch(EnvironmentalConstants.getCommomFileTypeNames(), fileExtension) < 0)
			        				{
			        					fileExtension = "_blank";
			        				}
			        				String path = String.valueOf(summaryRowArray.get(0));
			        				String tempPath = path.replace("'", "\\'");
			        				tableRow.add("<input type=\"checkbox\" value=\""+path+"\" onclick=\"javascript:DataBrowser.clickCheckBox(this.id, this.checked); DataBrowser.setSelectedRowId(this);\" id=\"chkbox_"+(i+1)+"\">");
			        				if(query == null)
			        				{
					        			if(summaryRowArray.get(summaryRowArray.size()-1) != null && summaryRowArray.get(summaryRowArray.size()-1).equals("dir"))
					        			{
					        				tableRow.add("<img style=\"vertical-align:middle; width: 18px; height: 16px;\" src=\"images/folder_icon.png\"> &nbsp;<a style=\"text-decoration:none\" href=\"javascript:DataBrowser.listFiles(\'"+ URLEncoder.encode(dirPath + tempPath, "UTF-8") +"\');\" ><span id=\"name\">"+path +"</span></a>");
			        						tableRow.add("<span id=\"kind\" style=\"display:none\">Directory</span>");
					        			}
					        			else
					        			{
					        				tableRow.add("<a target=\"_blank\" href=\"FileDownload?namenode="+nodeId+"&filePath="+URLEncoder.encode(dirPath + path, "UTF-8")+"\"><img style=\"vertical-align:middle; width: 18px; height: 16px;\"  src=\"images/extensions/" + fileExtension + ".png\"></a> &nbsp;<a href=\"FileDownload?namenode="+nodeId+"&filePath="+URLEncoder.encode(dirPath + path, "UTF-8")+"\" target=\"_blank\"><span id=\"name\">"+path+"</span></a>");
					        				tableRow.add("<span id=\"kind\" style=\"display:none\">File</span>");
					        			}
					        			continue;
			        				}
			        				else
			        				{
			        					if(summaryRowArray.get(summaryRowArray.size()-1) != null && summaryRowArray.get(summaryRowArray.size()-1).equals("dir"))
			        					{
			        						tableRow.add("<img style=\"vertical-align:middle; width: 18px; height: 16px;\" src=\"images/folder_icon.png\">&nbsp;<a style=\"text-decoration:none\" href=\"javascript:DataBrowser.listFiles(\'"+ URLEncoder.encode(dirPath + tempPath, "UTF-8")+"\');\" ><span id=\"name\">"+path +"</span></a>");
			        						tableRow.add("<span id=\"kind\" style=\"display:none\">Directory</span>");
			        					}
					        			else
					        			{
					        				tableRow.add("<a target=\"_blank\" href=\"FileDownload?namenode="+nodeId+"&filePath="+URLEncoder.encode(path, "UTF-8")+"\"><img style=\"vertical-align:middle; width: 18px; height: 16px;\"  src=\"images/extensions/" + fileExtension + ".png\"></a>&nbsp;<a href=\"FileDownload?namenode="+nodeId+"&filePath="+URLEncoder.encode(path, "UTF-8")+"\"><span id=\"name\">"+path+"</span></a>");
					        				tableRow.add("<span id=\"kind\" style=\"display:none\">File</span>");
					        			}
					        			continue;
			        				}
				        		}
			        		tableRow.add(summaryRowArray.get(j));
			        	}
			        	if(query != null){
			        		tableRow.add(summaryRowArray.get(summaryRowArray.size()-1));
			        	}
				        rowObject.put("cell", tableRow);
				        rowArray.add(rowObject);
			        }
		        
		        rowData.put("rows",rowArray);
	        }
	        else{
	        	rowData.put("total",0);
		        rowData.put("page",0);
		        rowData.put("records",0);
		        JSONObject cell = new JSONObject();
		        JSONArray cellArray = new JSONArray();
		        for(int i=0; i<11; i++)
		        {
		        	if(i == 6)
		        		cellArray.add("<center><label style = 'font-size : 13px;'>No data available on cluster</label></center>");
		        	else
		        		cellArray.add("");
		        }
		        cell.put("cell",cellArray);
		        rowArray.add(cell);
	        	rowData.put("rows",rowArray);
	        }
//	        if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("RowData: " + rowData.toJSONString());
        response.getWriter().println(rowData);
		
	}
	
	
}
