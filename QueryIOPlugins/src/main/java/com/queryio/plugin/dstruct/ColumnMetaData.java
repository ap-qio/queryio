package com.queryio.plugin.dstruct;

public class ColumnMetaData {
	private String columnName;
	private String columnSqlDataType;
	
	public ColumnMetaData(String columnName, String columnSqlDataType){
		this.columnName = columnName;
		this.columnSqlDataType = columnSqlDataType;
	}
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnSqlDataType() {
		return columnSqlDataType;
	}
	public void setColumnSqlDataType(String columnSqlDataType) {
		this.columnSqlDataType = columnSqlDataType;
	}
	public String toString(){
		return columnName + " " + columnSqlDataType;
	}
}
