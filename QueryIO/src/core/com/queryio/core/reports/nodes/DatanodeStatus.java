package com.queryio.core.reports.nodes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.IProductConstants;
import com.queryio.common.charts.independent.AppChart;
import com.queryio.common.charts.independent.AppPieChart;
import com.queryio.common.charts.independent.ChartConstants;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.properties.ChartPropertiesManager;
import com.queryio.common.charts.util.UIGraphicsFactory;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.Chart;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Group;
import com.queryio.common.exporter.dstruct.IExportableItem;
import com.queryio.common.exporter.dstruct.Label;
import com.queryio.common.exporter.dstruct.Table;
import com.queryio.core.monitor.beans.Parameter;
import com.queryio.core.monitor.beans.SummaryTable;
import com.queryio.core.monitor.managers.SummaryManager;

public class DatanodeStatus extends AbstractExportableNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6757527434993431105L;
	private Timestamp currentTimestamp; 
	private Timestamp startTimestamp; 
	private Timestamp endTimestamp; 
	private static UserInterface userInterface = UIGraphicsFactory.getUserInterface(IProductConstants.USER_INTERFACE_SWING);
	String title = null;
	public DatanodeStatus(String exportedFileName, Timestamp startTimestamp, Timestamp endTimestamp, String title) {
		super("Datanode Status Report", 0, "Datanode Status Report", exportedFileName, "Datanode Status Report");
		this.currentTimestamp = new Timestamp((new Date()).getTime());
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
		this.title = title;
		this.setFilePath(EnvironmentalConstants.getReportsDirectory());
	}
	@Override
	public IExportableItem[] getItems() {
		IExportableItem[] items = null;
		SummaryTable st = SummaryManager.getAllDataNodesSummaryTable(true);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd hh:mm a");		
		items = new IExportableItem[] 
			{
				new Label(0, 0, 100, 20, " Generated On: "+sdf.format(currentTimestamp.getTime())),
				new Label(0, 0, 100, 20, " Report Filter:"),
				new Label(0, 0, 100, 20, " Start Time: "+ sdf.format(startTimestamp.getTime())+"  End Time: "+ sdf.format(endTimestamp.getTime())),
				new Label(0, 0, 100, 20, " "),
				new Group(0, 0, 100, 50, "", new IExportableItem[]
					{				
						new Chart(0, 0, 49, 40, getPieChart()),				
						new Table(50, 0, 49, 40, getStatusSummaryTableModel(), new int[] { 50 }, null)
					}),
				new Table(0, 0, 99, 50, getTableSummaryModel(st), getColumnSize(st), "DataNodes Summary")
			};
		return items;
	}

	private int[] getColumnSize(SummaryTable st) {
		int result[] = new int[st.getColNames().size()-1];
		for(int i = 0; i < result.length; i ++){
			if(i == 0){
				result[i] = 100/result.length + 2;
			} else if (i == result.length - 1){
				result[i] = 100/result.length - 1;
			} else{
				result[i] = 100/result.length;
			}
		}
		return result;
	}

	private TableModel getTableSummaryModel(SummaryTable st) {		
		ArrayList colNameList = st.getColNames();
		DefaultTableModel tableModel = new DefaultTableModel();
		for(int i=0;i<colNameList.size()-2;i++){
			tableModel.addColumn((String)colNameList.get(i));
		}
		Object o[];
		for(int i = 0; i < st.getRows().size(); i ++){			
			ArrayList list = (ArrayList) st.getRows().get(i);
			o = new Object[list.size()-2];
			for(int j = 0; j < o.length; j ++){
				o[j] = list.get(j);
			}
			tableModel.addRow(o);
		}				
		return tableModel;		
	}
	private TableModel getStatusSummaryTableModel() {
		ArrayList clusterSumm = SummaryManager.getAllDataNodeStatusSummary();	

		String[] colNames = {"Status Summary"};
		DefaultTableModel tableModel = new DefaultTableModel(colNames, 0);
		
		Object o[];
		for(int i = 0; i < clusterSumm.size(); i ++){			
			Parameter p = (Parameter) clusterSumm.get(i);
			o = new Object[1];
			o[0] = (String) p.getName() + " :  " + p.getValue();
			tableModel.addRow(o);
		}				
		return tableModel;
		
	}
	private AppChart getPieChart() {
		ArrayList clusterInfo = SummaryManager.getClusterMemoryInfo();

		AppPieChart pieChart = new AppPieChart(userInterface, ChartPropertiesManager.TYPE_RUNTIME_CHARTS, IProductConstants.QUERYIO);
		pieChart.setBackgroundColor(ChartConstants.COLOR_WHITE);
		pieChart.setChartTitle("Storage Summary");
		pieChart.showTitle(true);
		pieChart.showLegend(true);
		pieChart.showBorder(true);
		pieChart.setAlternateBackgroundColor(ChartConstants.COLOR_ALTERNATE);
		
		Double total = (Double) clusterInfo.get(0);
		Double used = (Double) clusterInfo.get(1);
		if(!(total==0&&used==0)){
			
			pieChart.initializeSeries(clusterInfo.size());
			while(total > Integer.MAX_VALUE){
				total /= 100;
				used /= 100;
			}
			
			pieChart.setSeriesName(0, "Total capacity(in GB)");
			pieChart.setSeriesValue(0, total.intValue());
			
			pieChart.setSeriesName(1, "Capacity Used(in GB)");
			pieChart.setSeriesValue(1, used.intValue());
			
			pieChart.setSeriesColour(0, Color.decode("#579575"));
			pieChart.setSeriesColour(1, Color.decode("#4BB2C5"));
		}else{
			pieChart.initializeSeries(1);
			pieChart.setSeriesName(0, "No Data");
			pieChart.setSeriesValue(0, 100);
			pieChart.setSeriesColour(0, Color.decode("#808080"));
		}
			
		return pieChart;		
	}

	@Override
	public String getHeaderText() {
		return title;
	}
}
