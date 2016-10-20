package com.queryio.core.reports.nodes;

import java.sql.Connection;
import java.text.SimpleDateFormat;

import javax.swing.table.TableModel;

import org.json.simple.JSONObject;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.IExportableItem;
import com.queryio.common.exporter.dstruct.Label;
import com.queryio.common.exporter.dstruct.Table;
import com.queryio.common.util.AppLogger;
import com.queryio.core.customtags.BigQueryIdentifiers;
import com.queryio.core.customtags.CustomTagsManager;
import com.queryio.core.dao.NodeDAO;

public class BigQueryResult extends AbstractExportableNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7972674803530490788L;
	private String query;
	private String title;
	private int records;
	private String connectionPool = "";
	JSONObject properties;
	
	public BigQueryResult(String namenodeId, String exportedFileName, String query, int records) throws Exception{
		super("Big Query Result", 0, "Big Query Result", exportedFileName, "Big Query Result");
		Connection connection = null;
		try{
			connection = CoreDBManager.getQueryIODBConnection();
			this.connectionPool = NodeDAO.getDBNameForNameNodeMapping(connection, namenodeId);
		}finally{
			CoreDBManager.closeConnection(connection);
		}		 
		this.query = query;
		this.records = records;
		this.setFilePath(EnvironmentalConstants.getReportsDirectory());
	}
	public BigQueryResult(String exportedFileName, JSONObject properties, int records) {
		super("Big Query Result", 0, "Big Query Result", exportedFileName, "Big Query Result");
		
		this.query = (String) properties.get(BigQueryIdentifiers.SQLQUERY);
		this.properties = properties;
		
		this.records = records;
		this.setFilePath(EnvironmentalConstants.getReportsDirectory());
	}
	@Override
	public IExportableItem[] getItems() {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd hh:mm a");
		
		TableModel tableModel = null;
		try {
			tableModel = CustomTagsManager.getBigQueryResultTableModel(connectionPool, query, records, properties);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		int colCount = tableModel.getColumnCount();
		int width = 100/colCount;
		int[] colWidths = new int[colCount];
		for(int i=0; i<colWidths.length; i++){
			colWidths[i] = width;
		}
		
		IExportableItem[] items = new IExportableItem[]
			{
				new Label(0, 0, 100, 20, " Generated On: " + sdf.format(new java.util.Date())),
				new Label(0, 0, 100, 20, " " + query),
				new Label(0,0,100,20, " " + this.properties.get(BigQueryIdentifiers.QUERYHEADER)),
				new Table(0, 0, 99, 50, tableModel, colWidths, "Big Query Result"),
				new Label(0,0,100,20, " " + this.properties.get(BigQueryIdentifiers.QUERYFOOTER)),
			};
		return items;
	}

	@Override
	public String getHeaderText() {
		return this.title;
	}
	public void setTitle(String title){
		this.title=title;
	}
}