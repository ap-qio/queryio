package com.queryio.common.report;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.WordUtils;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.DialChart;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.impl.InsetsImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.LabelImpl;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.Query;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.SeriesGrouping;
import org.eclipse.birt.chart.model.data.impl.QueryImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesGroupingImpl;
import org.eclipse.birt.chart.model.impl.DialChartImpl;
import org.eclipse.birt.chart.model.layout.ClientArea;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.DialSeries;
import org.eclipse.birt.chart.model.type.impl.DialSeriesImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MeterChartHandler {

	public static Chart getChart(String title, int dimension, int chartWidth, int chartHeight, String dxSeries,
			JSONObject ySeriesJSON, String xSeriesTitle, String ySeriesTitle, JSONObject chartPreferences,
			HashMap resultSetColumns, boolean isHive, String yGrouping) throws Exception {

		JSONObject titleJSON = (JSONObject) chartPreferences.get("titleJson");
		JSONObject commonJSON = (JSONObject) chartPreferences.get("commonJson");

		DialChart dChart = (DialChart) DialChartImpl.create();
		dChart.setType("Meter Chart");
		dChart.setSubType("Superimposed Meter Chart");

		Plot p = dChart.getPlot();
		ClientArea clientArea = p.getClientArea();

		/* common json properties */
		JSONArray topColors = null;
		if (commonJSON != null) {
			dChart.getBlock().setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("background")));
			p.setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("plotBackground")));
			dChart.getBlock().getBounds().setWidth(chartWidth);
			dChart.getBlock().getBounds().setHeight(chartHeight);
			/* Client */
			clientArea.setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("clientBackground")));
			// Empty message
			String msg = (String) commonJSON.get("emptyChartMessage");
			if (msg != null && msg.length() > 0) {
				dChart.getEmptyMessage().setVisible(true);
				dChart.getEmptyMessage().getCaption().setValue(msg);
			}
			topColors = (JSONArray) commonJSON.get("topColors");
		}

		ChartPreferenceHandler.setTitleProperties(titleJSON, dChart, title);
		// Chart Out Line
		JSONObject chartOutLine = (JSONObject) chartPreferences.get("outLineJson");
		if (chartOutLine != null && chartOutLine.size() > 0) {
			Boolean isChartOutLineVisible = (Boolean) chartOutLine.get("visible");
			if (isChartOutLineVisible) {
				int chartOutlineWidthInt = Integer.parseInt((String) chartOutLine.get("width"));
				String chartOutlineColor = (String) chartOutLine.get("color");
				dChart.getBlock()
						.setOutline(LineAttributesImpl.create(ChartPreferenceHandler.hexToRGB(chartOutlineColor),
								ChartPreferenceHandler.getLineStyleType((String) chartOutLine.get("style")),
								chartOutlineWidthInt));
			}
		}
		// Chart insets.
		JSONObject titleinsets = (JSONObject) chartPreferences.get("insetsJson");
		if (titleinsets != null && titleinsets.size() > 0) {
			int titleInsetTop = Integer.parseInt((String) titleinsets.get("top"));
			int titleInsetBottom = Integer.parseInt((String) titleinsets.get("bottom"));
			int titleInsetLeft = Integer.parseInt((String) titleinsets.get("left"));
			int titleInsetRight = Integer.parseInt((String) titleinsets.get("right"));
			dChart.getTitle()
					.setInsets(InsetsImpl.create(titleInsetTop, titleInsetLeft, titleInsetBottom, titleInsetRight));
		}

		/* Legend */
		Legend legend = dChart.getLegend();
		JSONObject legendProperties = (JSONObject) chartPreferences.get("legendJson");
		ChartPreferenceHandler.setLegendProperties(legendProperties, legend, ySeriesTitle);

		/* X-Axis */
		Series xSeries = SeriesImpl.create();
		if (isHive) {
			dxSeries = dxSeries.toLowerCase();
		}
		Query xQuery = QueryImpl.create("row[\"" + dxSeries + "\"]");
		xSeries.getDataDefinition().add(xQuery);
		xSeries.getLabel().getCaption().setValue(xSeriesTitle);
		xSeries.getLabel().setVisible(true);
		SeriesDefinition xSeriesDef = SeriesDefinitionImpl.create();
		dChart.getSeriesDefinitions().add(xSeriesDef);
		xSeriesDef.getSeriesPalette().shift(0);
		xSeriesDef.getSeries().add(xSeries);
		SeriesGrouping seriesGroup = SeriesGroupingImpl.create();
		seriesGroup.setEnabled(true);
		xSeriesDef.setGrouping(seriesGroup);
		Iterator it = ySeriesJSON.keySet().iterator();
		int i = 0;

		while (it.hasNext()) {
			DialSeries dialLeader = (DialSeries) DialSeriesImpl.create();

			// Label JSON
			JSONObject labelProperties = (JSONObject) chartPreferences.get("labelJson");
			ChartPreferenceHandler.setLabelProperties(labelProperties, dialLeader, false, dimension);

			dChart.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);
			String ySeriesMeter = (String) it.next();
			String function = (String) ySeriesJSON.get(ySeriesMeter);
			if (isHive) {
				ySeriesMeter = ySeriesMeter.toLowerCase();
			}
			Query yQuery = QueryImpl.create("row[\"" + ySeriesMeter + "\"]");

			if ((function != null) && !(function.equals("#"))) {
				SeriesGrouping pieSeriesGroup = SeriesGroupingImpl.create();
				pieSeriesGroup.setEnabled(true);
				if (function.equalsIgnoreCase("DISTINCTCOUNT"))
					pieSeriesGroup.setAggregateExpression(function);
				else
					pieSeriesGroup.setAggregateExpression(WordUtils.capitalize(function.toLowerCase()));
				yQuery.setGrouping(pieSeriesGroup);
			}
			dialLeader.getDataDefinition().add(yQuery);

			dialLeader.getLabel().getCaption().setValue(ySeriesTitle);
			Label baseTitle = LabelImpl.create();
			dialLeader.setSeriesIdentifier(baseTitle.getCaption().getValue());
			SeriesDefinition ySeriesDef = SeriesDefinitionImpl.create();
			xSeriesDef.getSeriesDefinitions().add(ySeriesDef);
			if (topColors != null && i < topColors.size()) {
				ySeriesDef.getSeriesPalette().getEntries()
						.add(ChartPreferenceHandler.hexToRGB((String) topColors.get(i)));
			}
			ySeriesDef.getSeries().add(dialLeader);
			i++;
			if (yGrouping != null && !("none".equalsIgnoreCase(yGrouping)) && !("".equals(yGrouping))) {
				SeriesDefinition sdGroup = SeriesDefinitionImpl.create();
				Query query = QueryImpl.create("row[\"" + yGrouping + "\"]");
				sdGroup.setQuery(query);
				xSeriesDef.getSeriesDefinitions().clear(); // Clear the original
				xSeriesDef.getSeriesDefinitions().add(0, sdGroup);
				sdGroup.getSeries().add(ySeriesDef.getSeries().get(0));
			}
		}

		return dChart;
	}

}
