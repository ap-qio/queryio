package com.queryio.demo.mr.report.mydbr;

import java.util.ArrayList;

public class JTLEntry {
	ArrayList<String> columns = new ArrayList<String>();
	ArrayList<String> values = new ArrayList<String>();

	public ArrayList<String> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<String> columns) {
		this.columns = columns;
	}

	public ArrayList<String> getValues() {
		return values;
	}

	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	public void addValue(String value) {
		this.values.add(value);
	}

	public void clearValues() {
		this.values.clear();
	}

	public String toString() {
		return columns + "\n" + values;
	}
}
