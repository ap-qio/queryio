package com.queryio.core.monitor.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class SummaryTable implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList colNames;
	private ArrayList rows;
	private long totalRowCount;
	
	
	public long getTotalRowCount() {
		return totalRowCount;
	}

	public void setTotalRowCount(long totalRowCount) {
		this.totalRowCount = totalRowCount;
	}

	public SummaryTable()
	{
		this.colNames = new ArrayList();
		this.rows = new ArrayList();
	}
	
	public ArrayList getColNames() 
	{
		return colNames;
	}
	
	public void setColNames(ArrayList colNames) 
	{
		this.colNames = colNames;
	}
	
	public void addRow(ArrayList row)
	{
		rows.add(row);
	}
	
	public ArrayList getRows() 
	{
		return rows;
	}
	
	public void setRows(ArrayList rows) 
	{
		this.rows = rows;
	}
	public void setRow(int index, ArrayList row){
		this.rows.set(index, row);
	}
}
