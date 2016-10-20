package com.queryio.core.reports.nodes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.IExportableItem;
import com.queryio.common.exporter.dstruct.Label;
import com.queryio.common.exporter.dstruct.Table;
import com.queryio.core.billing.BillingManager;
import com.queryio.core.monitor.beans.SummaryTable;

public class BillingInvoice extends AbstractExportableNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9133956418982545274L;
	
	private Timestamp currentTimestamp;
	private Timestamp startTimestamp;
	private Timestamp endTimestamp;
	private String title;

	public BillingInvoice(String exportedFileName, Timestamp startTimestamp, Timestamp endTimestamp, String title)
	{
		super("Billing Invoice Report", 0, "Billing Invoice Report", exportedFileName, "Billing Invoice Report");
		this.currentTimestamp = new Timestamp(new Date().getTime());
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
		this.title = title;
		this.setFilePath(EnvironmentalConstants.getReportsDirectory());
	}

	@Override
	public IExportableItem[] getItems() 
	{
		SummaryTable st = BillingManager.getBillingReportSummaryTable(startTimestamp, endTimestamp);
		// TODO
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd hh:mm a");		
		
		IExportableItem[] items = new IExportableItem[]
				{
					new Label(0, 0, 100, 20, " Generated On: " + sdf.format(currentTimestamp.getTime())),
					new Label(0, 0, 100, 20, " Report Filter: "),
					new Label(0, 0, 100, 20, " Start Time: " + sdf.format(startTimestamp.getTime()) + "  End Time: " + sdf.format(endTimestamp.getTime())),
//					new Label(0, 0, 100, 20, " "),
					new Table(0, 0, 98, 50, getTableSummaryModel(st), getColumnSize(st), "Invoice Summary")
				};
		return items;
	}
	
	private int[] getColumnSize(SummaryTable st)
	{
		int result[] = new int[st.getColNames().size()];
		
		for(int i = 0; i < result.length; i ++)
		{
			if(i==0)
			{
				result[i] = (100/result.length)+1;
			}
			else if(i==result.length-1)
			{
				result[i] = (100/result.length)-1;
			}
			else
			{	
				result[i] = 100/result.length;
			}
		}
		return result;
	}

	private TableModel getTableSummaryModel(SummaryTable st)
	{		
		ArrayList colNameList = st.getColNames();
		DefaultTableModel tableModel = new DefaultTableModel();
		
		for(int i=0; i<colNameList.size(); i++)
		{
			tableModel.addColumn((String)colNameList.get(i));
		}
		Object o[];
		
		for(int i=0; i<st.getRows().size(); i++)
		{			
			ArrayList list = (ArrayList) st.getRows().get(i);
			o = new Object[list.size()];
			
			for(int j=0; j<o.length; j++)
			{
				o[j] = list.get(j);
			}
			tableModel.addRow(o);
		}				
		return tableModel;		
	}

	@Override
	public String getHeaderText() 
	{
		return title;
	}
}