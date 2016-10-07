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
import com.queryio.common.charts.independent.AppLineChart;
import com.queryio.common.charts.independent.AppPieChart;
import com.queryio.common.charts.independent.ChartConstants;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.properties.ChartPropertiesManager;
import com.queryio.common.charts.series.XAxisLongSeries;
import com.queryio.common.charts.series.YAxisSeries;
import com.queryio.common.charts.util.ChartFormatting;
import com.queryio.common.charts.util.UIGraphicsFactory;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.Chart;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Group;
import com.queryio.common.exporter.dstruct.IExportableItem;
import com.queryio.common.exporter.dstruct.Label;
import com.queryio.common.exporter.dstruct.Table;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;
import com.queryio.core.monitor.beans.Parameter;
import com.queryio.core.monitor.charts.ChartData;
import com.queryio.core.monitor.charts.Series;
import com.queryio.core.monitor.managers.ChartManager;
import com.queryio.core.monitor.managers.SummaryManager;

public class NodeManagerInfo extends AbstractExportableNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6757527434993431105L;
	private Timestamp endTimestamp;
	private Timestamp startTimestamp;
	Host host;
	String title = "";
	Node node;
	private static UserInterface userInterface = UIGraphicsFactory.getUserInterface(IProductConstants.USER_INTERFACE_SWING);
	public NodeManagerInfo(String exportedFileName, Timestamp startTimestamp, Timestamp endTimestamp, Host host, Node node, String title) {
		super("NodeManager Info Report Id: " + node.getId() + " Host: " + host.getHostIP(), 0, "NodeManager Info Report Id: " + node.getId() + " Host: " + host.getHostIP(), exportedFileName, "NodeManager Info Report Id: " + node.getId() + " Host: " + host.getHostIP());
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
		this.host = host;
		this.node=node;
		this.title = title;
		this.setFilePath(EnvironmentalConstants.getReportsDirectory());
	}
	@Override
	public IExportableItem[] getItems() {
		ArrayList chartDataList = ChartManager.getNodeManagerChartDataBetweenTime(node.getId(), startTimestamp, endTimestamp);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, yyyy-MM-dd hh:mm a");	
		IExportableItem[] items = new IExportableItem[chartDataList.size() + 9];
		
		items[0] = new Label(0, 0, 100, 20, " Generated On: "+ sdf.format((new Date()).getTime()));
		items[2] = new Label(0, 0, 100, 20, " Host Name: " + host.getHostIP());
		items[4] = new Label(0, 0, 100, 20, " Report Filter:");
		items[6] = new Label(0, 0, 100, 20," Start Time: "+ sdf.format(startTimestamp.getTime())+"  End Time: "+ sdf.format(endTimestamp.getTime()));
		items[7] = new Label(0, 0, 100, 20," ");
		items[8] = new Group(0, 0, 100, 50, "", new IExportableItem[]
					{				
						new Chart(0, 0, 49, 30, getPieChart()),				
						new Table(50, 0, 49, 30, getStatusSummaryTableModel(), new int[] { 50 }, null)
					});
		int k=0;
		int i = 0;
		for(i = 0; i < chartDataList.size()/2; i ++){
			items[i + 9] = new Group(0, 0, 100, 25, "", new IExportableItem[]
				{
					new Chart(0, 0, 49, 20, getChart((ChartData)chartDataList.get(k++))),
					new Chart(0, 0, 49, 20, getChart((ChartData)chartDataList.get(k++)))
				});
		}
		if(chartDataList.size() % 2 == 1)
		{
			items[i + 9] = new Group(0, 0, 50, 25, "", new IExportableItem[]
					{
						new Chart(0, 0, 49, 20, getChart((ChartData)chartDataList.get(k++))),
					});
		}
		return items;
	}

	private TableModel getStatusSummaryTableModel() {
		ArrayList clusterSumm = SummaryManager.getNodeManagerAppsDetailStatusSummaryForId(node.getId());

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
		ArrayList clusterInfo = SummaryManager.getNodeManagerAppsDetailForId(node.getId());

		AppPieChart pieChart = new AppPieChart(userInterface, ChartPropertiesManager.TYPE_RUNTIME_CHARTS, IProductConstants.QUERYIO);
		pieChart.setBackgroundColor(ChartConstants.COLOR_WHITE);
		pieChart.setChartTitle("Container Summary");
		pieChart.showTitle(true);
		pieChart.showBorder(true);
		pieChart.setAlternateBackgroundColor(ChartConstants.COLOR_ALTERNATE);
		pieChart.showLegend(true);
		
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
		
		if(!(containersCompletedPer==0 && containersFailedPer==0 && containersKilledPer==0 && containersRunningPer==0 && containersInitingPer==0))
		{
			pieChart.initializeSeries(clusterInfo.size() - 1);
			while(containersCompletedPer > Integer.MAX_VALUE)
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
		else{
			pieChart.initializeSeries(1);
			pieChart.setSeriesName(0, "No Data");
			pieChart.setSeriesValue(0, 100);
			pieChart.setSeriesColour(0, Color.decode("#808080"));
		}
		return pieChart;
	}
	
	private AppChart getChart(ChartData cd){ 		
		
		final AppLineChart lineChart = new AppLineChart(userInterface, ChartPropertiesManager.TYPE_RUNTIME_CHARTS, IProductConstants.QUERYIO);
		lineChart.initializeSeries(cd.getSeriesList().size(), cd.getDataPoints().size(), false);
		lineChart.setChartTitle(cd.getChartName());
		lineChart.showTitle(true);
		lineChart.setYAxisTitle("Count");
		lineChart.showXAxisTitle(false);
		lineChart.showYAxisTitle(true);
		lineChart.setBackgroundColor(ChartConstants.COLOR_WHITE);
		ChartFormatting.setAreaColor(lineChart);
		lineChart.showLegend(true);
		lineChart.showYAxisGrids(true);
		lineChart.showXAxisGrids(true);
		lineChart.showMarker(true);
		lineChart.setLogarithmic(true);
//		lineChart.setYAxisSeriesColour(0, new Color(16, 146, 50));
//		lineChart.showLabel(true);
		
		final XAxisLongSeries xSeries = (XAxisLongSeries)lineChart.getXAxisSeries();
		
		
		long timestamp;
		int value, max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
		if(cd.getDataPoints().size()>23){
			int diff = cd.getDataPoints().size()/23;
			if(diff==1){
				diff = (int)(((float)cd.getDataPoints().size()/23)+0.5);
			}
			for (int index = 0,i=0; index < cd.getDataPoints().size(); index +=diff,i++) 
			{
				timestamp = (Long)cd.getDataPoints().get(index);
				xSeries.setValue(i, timestamp);		
			}
			for (int index = 0; index < cd.getSeriesList().size(); index ++) 
			{
				Series series = (Series) cd.getSeriesList().get(index);
				lineChart.setYAxisSeriesText(index, series.getName());
				YAxisSeries ySeries = (YAxisSeries)lineChart.getYAxisSeries(index);
				int j=0;
				for(int i = 0 ; i < series.getValues().size(); i +=diff,j++){
					String str = String.valueOf(series.getValues().get(i));
					if(str != null && !str.equals("null"))
						value = ((Double)(Double.parseDouble(str))).intValue();
					else{
						value = 0;
					}
					ySeries.setValue(j, value);
					if(value > max){
						max = value;
					}
					if(value < min){
						min = value;
					}
				}
				lineChart.setMinMaxYValue(j, max + 10);
			}	
		}
		else{
			for (int index = 0; index < cd.getDataPoints().size(); index ++) 
			{
				timestamp = (Long)cd.getDataPoints().get(index);
				xSeries.setValue(index, timestamp);		
			}
			for (int index = 0; index < cd.getSeriesList().size(); index ++) 
			{
				Series series = (Series) cd.getSeriesList().get(index);
				lineChart.setYAxisSeriesText(index, series.getName());
				YAxisSeries ySeries = (YAxisSeries)lineChart.getYAxisSeries(index);
				for(int i = 0 ; i < series.getValues().size(); i ++){
					String str = String.valueOf(series.getValues().get(i));
					if(str != null && !str.equals("null"))
						value = ((Double)(Double.parseDouble(str))).intValue();
					else{
						value = 0;
					}
					ySeries.setValue(i, value);
					if(value > max){
						max = value;
					}
					if(value < min){
						min = value;
					}
				}
				lineChart.setMinMaxYValue(index, max + 10);
			}		
		}
		return lineChart;		
	}


	@Override
	public String getHeaderText() {
		return title;
	}
	
}