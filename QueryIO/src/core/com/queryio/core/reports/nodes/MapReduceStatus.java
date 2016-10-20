package com.queryio.core.reports.nodes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.IProductConstants;
import com.queryio.common.QueryIOConstants;
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

public class MapReduceStatus extends AbstractExportableNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2894709916457261119L;
	private static UserInterface userInterface = UIGraphicsFactory.getUserInterface(IProductConstants.USER_INTERFACE_SWING);
	private Timestamp currentTimestamp;
	private Timestamp startTimestamp; 
	private Timestamp endTimestamp;
	String title;
	public MapReduceStatus(String exportedFileName, Timestamp startTimestamp, Timestamp endTimestamp,String title) {
		super("MapReduce Status Report", 0, "MapReduce Status Report", exportedFileName, "MapReduce Status Report");
		this.currentTimestamp = new Timestamp(new Date().getTime());
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
		this.title = title;
		this.setFilePath(EnvironmentalConstants.getReportsDirectory());
	}

	@Override
	public IExportableItem[] getItems() {
		SummaryTable st = SummaryManager.getAllResourceManagersSummaryTable(true);
		SummaryTable st1 = SummaryManager.getAllNodeManagersSummaryTable(true);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd hh:mm a");
		IExportableItem[] items = new IExportableItem[]
			{
				new Label(0, 0, 100, 20, " Generated On: "+sdf.format(currentTimestamp.getTime())),
				new Label(0, 0, 100, 20, " Report Filter:"),
				new Label(0, 0, 100, 20, " Start Time: "+ sdf.format(startTimestamp.getTime())+"  End Time: "+ sdf.format(endTimestamp.getTime())),
				new Label(0, 0, 100, 20, " "),
				new Group(0, 0, 100, 50, "", new IExportableItem[]
					{
						new Chart(50, 0, 24, 30, getPieChart(QueryIOConstants.NODEMANAGER)),
						new Table(75, 0, 24, 30, getStatusSummaryTableModel(QueryIOConstants.NODEMANAGER), new int[] { 50 }, null),
						new Chart(0, 0, 24, 30, getPieChart(QueryIOConstants.RESOURCEMANAGER)),
						new Table(25, 0, 24, 30, getStatusSummaryTableModel(QueryIOConstants.RESOURCEMANAGER), new int[] { 50 }, null)
					}),
				new Table(0, 0, 99, 50, getTableSummaryModel(st), getColumnSize(st), "ResourceManagers Summary"),
				new Table(0, 0, 99, 50, getTableSummaryModel(st1), getColumnSize(st1), "NodeManagers Summary")
			};
		return items;
	}

	private int[] getColumnSize(SummaryTable st) {
		int result[] = new int[st.getColNames().size()-1];
		for(int i = 0; i < result.length; i ++){
			if(i==0){
				result[i] = (100/result.length)+2;
			}
			else if(i==result.length-1){
				result[i] = (100/result.length)-1;
			}
			else{	
				result[i] = 100/result.length;
			}
		}
		return result;
	}

	private TableModel getTableSummaryModel(SummaryTable st) {		
		ArrayList colNameList = st.getColNames();
		DefaultTableModel tableModel = new DefaultTableModel();
		for(int i=0;i<colNameList.size()-1;i++){
			tableModel.addColumn((String)colNameList.get(i));
		}
		Object o[];
		for(int i = 0; i < st.getRows().size(); i ++){			
			ArrayList list = (ArrayList) st.getRows().get(i);
			o = new Object[list.size()-1];
			for(int j = 0; j < o.length; j ++){
				o[j] = list.get(j);
			}
			tableModel.addRow(o);
		}				
		return tableModel;		
	}

	private TableModel getStatusSummaryTableModel(String nodeType) {
		ArrayList clusterSumm = new ArrayList();
		
		if (nodeType.equals(QueryIOConstants.RESOURCEMANAGER))
			clusterSumm = SummaryManager.getResourceManagerAppsDetailStatusSummary();
		else
			clusterSumm = SummaryManager.getNodeManagerAppsDetailStatusSummary();

		String[] colNames = {"Status Summary"};
		DefaultTableModel tableModel = new DefaultTableModel(colNames, 0);
		
		Object o[];
		for(int i = 0; i < clusterSumm.size(); i ++){			
			Parameter p = (Parameter) clusterSumm.get(i);
			o = new Object[1];
			o[0] = (String) p.getName() + " :  " + String.valueOf(p.getValue());
			tableModel.addRow(o);
		}				
		return tableModel;
		
	}

	private AppChart getPieChart(String nodeType) {
		ArrayList clusterInfo = new ArrayList();
		

		AppPieChart pieChart = new AppPieChart(userInterface, ChartPropertiesManager.TYPE_RUNTIME_CHARTS, IProductConstants.QUERYIO);
		pieChart.setBackgroundColor(ChartConstants.COLOR_WHITE);
		
		pieChart.showTitle(true);
		pieChart.showLegend(true);
		pieChart.showBorder(true);
		pieChart.setAlternateBackgroundColor(ChartConstants.COLOR_ALTERNATE);
		
		boolean isNoData = false;
		
		if (nodeType.equals(QueryIOConstants.RESOURCEMANAGER))
		{
			clusterInfo = SummaryManager.getResourceManagerAppsDetail();
			pieChart.setChartTitle("Application Summary");
			
			int appsRunning = (Integer) clusterInfo.get(0);
			int appsPending = (Integer) clusterInfo.get(1);
			int appsCompleted = (Integer) clusterInfo.get(2);
			int appsKilled = (Integer) clusterInfo.get(3);
			
			Double appsRunningPer = (Double) (appsRunning * 1.0);
			Double appsPendingPer = (Double) (appsPending * 1.0);
			Double appsCompletedPer = (Double) (appsCompleted * 1.0);
			Double appsKilledPer = (Double) (appsKilled * 1.0);
			
			if (!(appsRunningPer==0 && appsPendingPer==0 && appsCompletedPer==0 && appsKilledPer==0))
			{
				pieChart.initializeSeries(clusterInfo.size());
				while (appsRunningPer > Integer.MAX_VALUE)
				{
					appsRunningPer /= 100;
					appsPendingPer /= 100;
					appsCompletedPer /= 100;
					appsKilledPer /= 100;
				}
				
				pieChart.setSeriesName(0, "Running");
				pieChart.setSeriesValue(0, appsRunningPer.intValue());
				
				pieChart.setSeriesName(1, "Pending");
				pieChart.setSeriesValue(1, appsPendingPer.intValue());
				
				pieChart.setSeriesName(2, "Completed");
				pieChart.setSeriesValue(2, appsCompletedPer.intValue());
				
				pieChart.setSeriesName(3, "Killed");
				pieChart.setSeriesValue(3, appsKilledPer.intValue());
				
				pieChart.setSeriesColour(0, Color.decode("#CC9752"));
				pieChart.setSeriesColour(1, Color.decode("#996699"));
				pieChart.setSeriesColour(2, Color.decode("#FFCC00"));
				pieChart.setSeriesColour(3, Color.decode("#EAA228"));
			}
			else
			{
				isNoData = true;
			}
		}
		else
		{
			clusterInfo = SummaryManager.getNodeManagerAppsDetail();
			pieChart.setChartTitle("Container Summary");
			
			int containersCompleted = (Integer) clusterInfo.get(1);
			int containersFailed = (Integer) clusterInfo.get(2);
			int containersKilled = (Integer) clusterInfo.get(3);
			int containersRunning = (Integer) clusterInfo.get(4);
			int containersIniting = (Integer) clusterInfo.get(5);
			
			Double containersCompletedPer = (Double) (containersCompleted * 1.0);
			Double containersFailedPer = (Double) (containersFailed * 1.0);
			Double containersKilledPer = (Double) (containersKilled * 1.0);
			Double containersRunningPer = (Double) (containersRunning * 1.0);
			Double containersInitingPer = (Double) (containersIniting * 1.0);
			
			if (!(containersCompletedPer==0 && containersFailedPer==0 && containersKilledPer==0 && containersRunningPer==0 && containersInitingPer==0))
			{
				pieChart.initializeSeries(clusterInfo.size() - 1);
				while (containersCompletedPer > Integer.MAX_VALUE)
				{
					containersCompletedPer /= 100;
					containersFailedPer /= 100;
					containersKilledPer /= 100;
					containersRunningPer /= 100;
					containersInitingPer /= 100;
				}
				
				pieChart.setSeriesName(0, "Completed");
				pieChart.setSeriesValue(0, containersCompletedPer.intValue());
				
				pieChart.setSeriesName(1, "Failed");
				pieChart.setSeriesValue(1, containersFailedPer.intValue());
				
				pieChart.setSeriesName(2, "Killed");
				pieChart.setSeriesValue(2, containersKilledPer.intValue());
				
				pieChart.setSeriesName(3, "Running");
				pieChart.setSeriesValue(3, containersRunningPer.intValue());
				
				pieChart.setSeriesName(4, "Initing");
				pieChart.setSeriesValue(4, containersInitingPer.intValue());
				
				pieChart.setSeriesColour(0, Color.decode("#CC9752"));
				pieChart.setSeriesColour(1, Color.decode("#996699"));
				pieChart.setSeriesColour(2, Color.decode("#FFCC00"));
				pieChart.setSeriesColour(3, Color.decode("#EAA228"));
				pieChart.setSeriesColour(4, Color.decode("#11A211"));
			}
			else
			{
				isNoData = true;
			}
		}
		
		if (isNoData)
		{
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