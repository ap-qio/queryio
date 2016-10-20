package com.queryio.demo.adhoc;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualTreeBidiMap;

public class AdHocEntry {
	BidiMap columns = new DualTreeBidiMap();
	Map<Integer, Class> colTypes = new TreeMap<Integer, Class>();
	Map<Integer, Object> values = new TreeMap<Integer, Object>();
	
	public BidiMap getColumns() {
		return columns;
	}
	public void setColumns(Map<Integer, String> columns) {
		this.columns = new DualTreeBidiMap(columns);
	}
	public Map<Integer, Class> getColumnTypes() {
		return colTypes;
	}
	public void setColumnTypes(Map<Integer, Class> colTypes) {
		this.colTypes =  colTypes;
	}
	public Map<Integer, Object> getValues() {
		return values;
	}
	public void addValue(int index, Object value){
		this.values.put(index, value);
	}
	public void clearValues(){
		this.values.clear();
	}
	public String toString(){
		return columns + "\n" + values + "\n" + colTypes;
	}
}
