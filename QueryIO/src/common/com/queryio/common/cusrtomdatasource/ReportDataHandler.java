package com.queryio.common.cusrtomdatasource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ReportDataHandler {


	
	private JSONArray column;
	private JSONArray rows;
	
	public JSONArray getColumn() {
		return column;
	}
	public void setColumn(JSONArray column) {
		this.column = column;
	}
	public JSONArray getRows() {
		return rows;
	}

	public JSONObject getRow(int index) {
		return (JSONObject) rows.get(index);
	}
	public String  getColumnValue(int rowIndex, String columnName) {
		JSONObject row = (JSONObject) rows.get(rowIndex);
		String val = (String)row.get(columnName);
		if("null".equalsIgnoreCase(val))
			val = "";
		return val;
		
	}

	public void setRows(JSONArray rows) {
		this.rows = rows;
	}
	public int getRowsLength(int recordFetch) {
		
		if(recordFetch < this.rows.size() && recordFetch != -1)
			return recordFetch;
		else
			return this.rows.size();
	}
	
	
//	public JSONArray getColumnValue(String columnName){
//		
//		
//		
//	}
	
	
	

	
	
}
