package com.queryio.common.report;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.WordUtils;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.TickStyle;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.InsetsImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.AxisImpl;
import org.eclipse.birt.chart.model.component.impl.LabelImpl;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.Query;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.SeriesGrouping;
import org.eclipse.birt.chart.model.data.impl.QueryImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesGroupingImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.layout.ClientArea;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.DifferenceSeries;
import org.eclipse.birt.chart.model.type.impl.DifferenceSeriesImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DifferenceChartHandler {

	public static Chart getChart(String title, int dimension, int chartWidth, int chartHeight, String xSeries,
			JSONObject ySeriesPosititveJSON, JSONObject ySeriesNegativeJSON, String xSeriesTitle, String ySeriesTitle,
			JSONObject chartPreferences, HashMap resultSetColumns, boolean isHive, String yGrouping) throws Exception {

		JSONObject titleJSON = (JSONObject) chartPreferences.get("titleJson");
		JSONObject commonJSON = (JSONObject) chartPreferences.get("commonJson");

		ChartWithAxes chart = ChartWithAxesImpl.create();

		chart.setType("Difference Chart"); //$NON-NLS-1$
		chart.setSubType("Standard Difference Chart");

		Plot p = chart.getPlot();

		chart.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);
		chart.getBlock().getBounds().setWidth(chartWidth);
		chart.getBlock().getBounds().setHeight(chartHeight);

		chart.getBlock().getOutline().setVisible(true);

		// Plot

		chart.getBlock().setBackground(ColorDefinitionImpl.WHITE());

		chart.getPlot().getClientArea().setBackground(ColorDefinitionImpl.WHITE());

		// Title

		chart.getPlot().setBackground(ColorDefinitionImpl.WHITE());
		chart.getPlot().getClientArea().setBackground(ColorDefinitionImpl.TRANSPARENT());

		/* Legend */
		Legend legend = chart.getLegend();
		legend.setItemType(LegendItemType.SERIES_LITERAL);
		JSONObject legendProperties = (JSONObject) chartPreferences.get("legendJson");
		ChartPreferenceHandler.setLegendProperties(legendProperties, legend, ySeriesTitle);

		chart.getTitle().getLabel().getCaption().setValue(title);
		chart.getTitle().getLabel().getCaption().getFont().setSize(14);

		Axis xAxis = chart.getPrimaryBaseAxes()[0];

		xAxis.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);

		/* Setting X-axis properties. */
		JSONObject xAxisProperties = (JSONObject) chartPreferences.get("xAxisJson");
		ChartPreferenceHandler.setXAxisProperties(xAxis, xAxisProperties, xSeriesTitle);

		/* Plot */
		JSONArray topColors = null;
		if (commonJSON != null) {
			chart.getBlock().setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("background")));
			p.setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("plotBackground")));
			chart.getBlock().getBounds().setWidth(chartWidth);
			chart.getBlock().getBounds().setHeight(chartHeight);
			/* Client */
			ClientArea clientArea = p.getClientArea();
			clientArea.setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("clientBackground")));
			// Empty message
			String msg = (String) commonJSON.get("emptyChartMessage");
			if (msg != null && msg.length() > 0) {
				chart.getEmptyMessage().setVisible(true);
				chart.getEmptyMessage().getCaption().setValue(msg);
			}
			// Series palette
			topColors = (JSONArray) commonJSON.get("topColors");
		}

		Series seriesX = SeriesImpl.create();
		if (isHive) {
			xSeries = xSeries.toLowerCase();
		}
		Query xQuery = QueryImpl.create("row[\"" + xSeries + "\"]");
		String columnType = (String) resultSetColumns.get(xSeries);
		if (columnType.contains("VARCHAR")) {
			xAxis.setType(AxisType.TEXT_LITERAL);
		} else if (columnType.contains("INT") || columnType.contains("DECIMAL")) {
			xAxis.setType(AxisType.LINEAR_LITERAL);
		} else if (columnType.contains("TIMESTAMP")) {
			xAxis.setType(AxisType.DATE_TIME_LITERAL);
		}

		seriesX.getDataDefinition().add(xQuery);
		seriesX.setSeriesIdentifier(xSeriesTitle);

		SeriesDefinition xSeriesDef = SeriesDefinitionImpl.create();
		xAxis.getSeriesDefinitions().add(xSeriesDef);
		if (topColors != null) {
			for (int i = 0; i < topColors.size(); i++) {
				xSeriesDef.getSeriesPalette().getEntries()
						.add(ChartPreferenceHandler.hexToRGB((String) topColors.get(i)));
			}
		}
		xSeriesDef.getSeriesPalette().shift(0);
		xSeriesDef.getSeries().add(seriesX);

		ChartPreferenceHandler.setTitleProperties(titleJSON, chart, title);
		// Chart Out Line
		JSONObject chartOutLine = (JSONObject) chartPreferences.get("outLineJson");
		if (chartOutLine != null && chartOutLine.size() > 0) {
			Boolean isChartOutLineVisible = (Boolean) chartOutLine.get("visible");
			if (isChartOutLineVisible) {
				int chartOutlineWidthInt = Integer.parseInt((String) chartOutLine.get("width"));
				String titleOutlineColor = (String) chartOutLine.get("color");
				chart.getBlock()
						.setOutline(LineAttributesImpl.create(ChartPreferenceHandler.hexToRGB(titleOutlineColor),
								ChartPreferenceHandler.getLineStyleType((String) chartOutLine.get("style")),
								chartOutlineWidthInt));
			}

		}
		// Chart insets.
		JSONObject chartInsets = (JSONObject) chartPreferences.get("insetsJson");
		if (chartInsets != null && chartInsets.size() > 0) {
			int titleInsetTop = Integer.parseInt((String) chartInsets.get("top"));
			int titleInsetBottom = Integer.parseInt((String) chartInsets.get("bottom"));
			int titleInsetLeft = Integer.parseInt((String) chartInsets.get("left"));
			int titleInsetRight = Integer.parseInt((String) chartInsets.get("right"));
			chart.getTitle()
					.setInsets(InsetsImpl.create(titleInsetTop, titleInsetLeft, titleInsetBottom, titleInsetRight));
		}

		boolean primary = true;
		SeriesGrouping seriesGroup = SeriesGroupingImpl.create();
		seriesGroup.setEnabled(true);
		xSeriesDef.setGrouping(seriesGroup);

		Iterator it = ySeriesPosititveJSON.keySet().iterator();
		int i = 0;
		Iterator itn = ySeriesNegativeJSON.keySet().iterator();

		while (it.hasNext()) {

			String ySeries = (String) it.next();
			String function = (String) ySeriesPosititveJSON.get(ySeries);

			Axis yAxis;

			if (primary) {
				// Y-Axis
				yAxis = chart.getPrimaryOrthogonalAxis(xAxis);
				yAxis.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);
				yAxis.getTitle().setVisible(true);
				yAxis.setSideBySide(true);

				primary = false;
			} else {
				// Y-Axis (2)
				yAxis = AxisImpl.create(Axis.ORTHOGONAL);
				yAxis.getMajorGrid().setTickStyle(TickStyle.RIGHT_LITERAL);
				xAxis.getAssociatedAxes().add(yAxis);
				yAxis.setSideBySide(true);
			}
			yAxis.setType(AxisType.LINEAR_LITERAL);

			/* Setting Y-axis properties. */
			JSONObject yAxisProperties = (JSONObject) chartPreferences.get("yAxisJson");
			ChartPreferenceHandler.setYAxisProperties(yAxis, yAxisProperties, ySeriesTitle);

			if (isHive) {
				ySeries = ySeries.toLowerCase();
			}
			Query yQuery = QueryImpl.create("row[\"" + ySeries + "\"]");

			DifferenceSeries seriesY = (DifferenceSeries) DifferenceSeriesImpl.create();

			/* Setting Y-Series label properties. */
			JSONObject labelProperties = (JSONObject) chartPreferences.get("labelJson");
			ChartPreferenceHandler.setLabelProperties(labelProperties, seriesY, true, dimension);

			SeriesGrouping ySeriesGroup = SeriesGroupingImpl.create();
			ySeriesGroup.setEnabled(true);
			if ((function != null) && !(function.equals("#"))) {
				if (function.equalsIgnoreCase("DISTINCTCOUNT"))
					ySeriesGroup.setAggregateExpression(function);
				else
					ySeriesGroup.setAggregateExpression(WordUtils.capitalize(function.toLowerCase()));
				yQuery.setGrouping(ySeriesGroup);
			} else {

				// ySeriesGroup.setAggregateExpression("Sum");
				// yQuery.setGrouping(ySeriesGroup);
			}

			seriesY.getDataDefinition().add(yQuery);

			// Negative Series.
			String ySeries2 = (String) itn.next();
			String function2 = (String) ySeriesNegativeJSON.get(ySeries2);
			if (isHive) {
				ySeries2 = ySeries2.toLowerCase();
			}

			Query yQuery2 = QueryImpl.create("row[\"" + ySeries2 + "\"]");

			SeriesGrouping ySeriesNegativeGroup = SeriesGroupingImpl.create();
			ySeriesNegativeGroup.setEnabled(true);

			if ((function2 != null) && !(function2.equals("#"))) {
				if (function2.equalsIgnoreCase("DISTINCTCOUNT"))
					ySeriesNegativeGroup.setAggregateExpression(function2);
				else
					ySeriesNegativeGroup.setAggregateExpression(WordUtils.capitalize(function.toLowerCase()));
				yQuery2.setGrouping(ySeriesNegativeGroup);
			} else {
				// ySeriesNegativeGroup.setAggregateExpression("Sum");
				// seriesY.getDataDefinition().add(yQuery2);
			}

			seriesY.getDataDefinition().add(yQuery2);

			Label baseTitle = LabelImpl.create();
			baseTitle.getCaption().setValue(ySeries2);

			seriesY.setSeriesIdentifier(baseTitle.getCaption().getValue());

			SeriesDefinition ySeriesDef = SeriesDefinitionImpl.create();
			if (topColors != null && i < topColors.size()) {
				ySeriesDef.getSeriesPalette().getEntries()
						.add(ChartPreferenceHandler.hexToRGB((String) topColors.get(i)));
			}
			yAxis.getSeriesDefinitions().add(ySeriesDef);
			ySeriesDef.getSeries().add(seriesY);
			i++;
			if (yGrouping != null && !("none".equalsIgnoreCase(yGrouping)) && !("".equals(yGrouping))) {
				SeriesDefinition sdGroup = SeriesDefinitionImpl.create();
				Query query = QueryImpl.create("row[\"" + yGrouping + "\"]");
				sdGroup.setQuery(query);
				yAxis.getSeriesDefinitions().clear(); // Clear the original
				yAxis.getSeriesDefinitions().add(0, sdGroup);
				sdGroup.getSeries().add(ySeriesDef.getSeries().get(0));
			}
		}
		return chart;
	}

}
