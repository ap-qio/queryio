package com.queryio.core.monitor.charts;

import java.io.Serializable;
import java.util.ArrayList;

public class ChartData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 698492612023089877L;
	private String chartName;
	private ArrayList dataPoints;
	private ArrayList seriesList;

	public ChartData(String chartName) {
		dataPoints = new ArrayList();
		seriesList = new ArrayList();

		this.chartName = chartName;
	}

	public void setChartName(String chartName) {
		this.chartName = chartName;
	}

	public String getChartName() {
		return this.chartName;
	}

	public void setDataPoints(ArrayList dataPoints) {
		this.dataPoints = dataPoints;
	}

	public ArrayList getDataPoints() {
		return this.dataPoints;
	}

	public void addSeries(Series series) {
		seriesList.add(series);
	}

	public ArrayList getSeriesList() {
		return this.seriesList;
	}
}
