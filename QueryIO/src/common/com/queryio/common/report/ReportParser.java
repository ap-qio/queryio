package com.queryio.common.report;

import java.io.InputStream;
import java.util.Iterator;

import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.report.model.api.CellHandle;
import org.eclipse.birt.report.model.api.DataItemHandle;
import org.eclipse.birt.report.model.api.GridHandle;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.RowHandle;
import org.eclipse.birt.report.model.api.SimpleMasterPageHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.api.TableHandle;
import org.eclipse.birt.report.model.api.TextItemHandle;
import org.json.simple.JSONObject;

import com.queryio.common.util.AppLogger;
import com.queryio.core.customtags.BigQueryIdentifiers;

public class ReportParser {
//	public static void main(String[] args) throws FileNotFoundException {
//		System.out
//				.println(getJSONProperties("QuerryId1", "", new FileInputStream(
//						"/AppPerfect/HiperCloudStore/tomcat/webapps/queryio/Reports/Birt/Reporter_1351590475063.rptdesign")));
//	}

	public static JSONObject getJSONProperties(String queryId, String queryDesc, InputStream reportDesignDocStream) {
		JSONObject properties = new JSONObject();
		properties.put(BigQueryIdentifiers.QUERYID, queryId);
		properties.put(BigQueryIdentifiers.QUERYDESC, queryDesc);
		IReportRunnable designHandle = null;
		IRunAndRenderTask task = null;
		RenderOption options = null;

		IReportEngine engine = ReportHandler.getReportEngine();

		try {
			designHandle = engine.openReportDesign(reportDesignDocStream);
			ReportDesignHandle report = (ReportDesignHandle) designHandle
					.getDesignHandle();

			populateJSON(report, properties);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}

		return properties;
	}

	private static void populateJSON(ReportDesignHandle designHandle,
			JSONObject jsonObj) {
		
		String sqlQuery = (String) designHandle.getDataSets().get(0).getProperty("queryText");
		jsonObj.put(BigQueryIdentifiers.SQLQUERY, sqlQuery);
				
		String queryHeader = ((LabelHandle)((SlotHandle)((CellHandle)((RowHandle)((GridHandle)designHandle.getBody().get(0)).getRows().get(0)).getCells().get(0)).getContent()).get(0)).getText();
		jsonObj.put(BigQueryIdentifiers.QUERYHEADER, queryHeader);
		
		String queryFooter = ((TextItemHandle)((CellHandle)((RowHandle)((GridHandle)((SimpleMasterPageHandle)designHandle.getMasterPages().getContents().get(0)).getPageFooter().get(0)).getRows().get(0)).getCells().get(0)).getContent().get(0)).getContent();
		jsonObj.put(BigQueryIdentifiers.QUERYFOOTER, queryFooter);
		
		SlotHandle handle = ((RowHandle)((TableHandle)designHandle.getBody().get(1)).getHeader().getContents().get(0)).getCells();
		JSONObject arr = new JSONObject();
		for(int i = 0; i < handle.getContents().size(); i ++){
			CellHandle cellHandle = ((CellHandle)handle.get(i)); 
			Iterator it = cellHandle.getContent().iterator();			
			while(it.hasNext()){
				LabelHandle labelHandle = (LabelHandle) it.next();			
				String key = (String) labelHandle.getProperty("text");
				JSONObject o = new JSONObject();				
				if(key != null){
					Iterator iterator = labelHandle.getPropertyIterator();
					while(iterator.hasNext()){
						PropertyHandle ph = (PropertyHandle) iterator.next();
						String propName = ReportConstants.getCSSPropertyFromBirt(ph.getDefn().getName());
						if(propName != null && ph.getValue() != null){
							o.put(propName, ph.getStringValue());
						}
					}
				}				
				arr.put(key, o);
			}			
		}
		jsonObj.put(BigQueryIdentifiers.COLHEADERDETAIL, arr);
		handle = ((RowHandle)((TableHandle)designHandle.getBody().get(1)).getDetail().getContents().get(0)).getCells();
		arr = new JSONObject();
		for(int i = 0; i < handle.getContents().size(); i ++){
			CellHandle cellHandle = ((CellHandle)handle.get(i)); 
			Iterator it = cellHandle.getContent().iterator();			
			while(it.hasNext()){
				DataItemHandle dataItemHandle = (DataItemHandle) it.next();
				String key = (String) dataItemHandle.getProperty("resultSetColumn");
				JSONObject o = new JSONObject();				
				if(key != null){
					Iterator iterator = dataItemHandle.getPropertyIterator();
					while(iterator.hasNext()){
						PropertyHandle ph = (PropertyHandle) iterator.next();
						String propName = ReportConstants.getCSSPropertyFromBirt(ph.getDefn().getName());
						if(propName != null && ph.getValue() != null){
							o.put(propName, ph.getStringValue());
						}
					}
				}				
				arr.put(key, o);			
			}			
		}	
		jsonObj.put(BigQueryIdentifiers.COLDETAIL, arr);		
	}
	
}
