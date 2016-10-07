package com.queryio.plugin.datatags;

public class ColumnMetadata {

	private String columnName = null;
	private Class columnJavaClass = null;
	private int size = -1;
	public ColumnMetadata(String columnName, Class columnJavaClass) {
		this.columnName = columnName;
		this.columnJavaClass = columnJavaClass;
	}
	public ColumnMetadata(String columnName, Class columnJavaClass, int size) {
		this.columnName = columnName;
		this.columnJavaClass = columnJavaClass;
		this.size = size;
	}
	public String getColumnName() {
		return columnName;
	}
	public Class getColumnSqlDataType() {
		return columnJavaClass;
	}
	public boolean isSizable(){
		return size == -1 ? false : true;
	}
	public int getSize(){
		return this.size;
	}
}
