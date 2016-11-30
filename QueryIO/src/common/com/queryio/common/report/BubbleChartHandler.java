package com.queryio.common.report;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.WordUtils;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.SortOption;
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
import org.eclipse.birt.chart.model.type.BubbleSeries;
import org.eclipse.birt.chart.model.type.impl.BubbleSeriesImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.common.MetadataConstants;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.database.DBTypeProperties;
import com.queryio.core.customtags.BigQueryIdentifiers;

public class BubbleChartHandler {

	public static Chart getBubbleChart(String title, int dimension, int chartWidth, int chartHeight,
			JSONObject xSeriesObj, JSONObject ySeriesJSON, JSONObject ySeriesValues, JSONObject ySeriesSizeJSON,
			String xSeriesTitle, String ySeriesTitle, JSONObject chartPreferences, HashMap resultSetColumns,
			String dbName, boolean isHive, String yGrouping) throws Exception {

		DBTypeProperties props = CustomTagDBConfigManager.getDatabaseDataTypeMap(dbName, null);
		JSONObject titleJSON = (JSONObject) chartPreferences.get("titleJson");
		JSONObject commonJSON = (JSONObject) chartPreferences.get("commonJson");

		ChartWithAxes chart = ChartWithAxesImpl.create();

		chart.setType("Bubble Chart"); //$NON-NLS-1$
		chart.setSubType("Standard Bubble Chart");

		Plot p = chart.getPlot();

		chart.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);
		dimension = ChartDimension.TWO_DIMENSIONAL;
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

		String xSeries = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES);

		if (isHive) {
			xSeries = xSeries.toLowerCase();
		}
		Query xQuery = QueryImpl.create("row[\"" + xSeries + "\"]");
		String columnType = (String) resultSetColumns.get(xSeries);
		if (columnType.contains(props.getTypeMap().get(MetadataConstants.STRING_WRAPPER_CLASS))) {
			xAxis.setType(AxisType.TEXT_LITERAL);
		} else if (columnType.contains(props.getTypeMap().get(MetadataConstants.INTEGER_WRAPPER_CLASS))
				|| columnType.contains(props.getTypeMap().get(MetadataConstants.DECIMAL_WRAPPER_CLASS))) {
			xAxis.setType(AxisType.LINEAR_LITERAL);
		} else if (columnType.contains(props.getTypeMap().get(MetadataConstants.TIMESTAMP_WRAPPER_CLASS))) {
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

		String sortType = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES_SORT_TYPE);

		if (!BigQueryIdentifiers.X_SERIES_SORT_TYPE_NONE.equalsIgnoreCase(sortType)) {
			String sortColumn = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES_SORT_COLUMN);
			Query xSortQuery = QueryImpl.create("row[\"" + sortColumn + "\"]");

			if (BigQueryIdentifiers.X_SERIES_SORT_TYPE_ASCENDING.equalsIgnoreCase(sortType))
				xSeriesDef.setSorting(SortOption.ASCENDING_LITERAL);
			else
				xSeriesDef.setSorting(SortOption.DESCENDING_LITERAL);

			xSeriesDef.setSortKey(xSortQuery);
			xSeriesDef.setSortStrength(2);
		}

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

		Iterator its = ySeriesSizeJSON.keySet().iterator();
		String ySeriesSize = (String) its.next();
		String functionSize = (String) ySeriesSizeJSON.get(ySeriesSize);

		Iterator it = ySeriesValues.keySet().iterator();
		int i = 0;

		while (it.hasNext()) {

			BubbleSeries seriesY = (BubbleSeries) BubbleSeriesImpl.create();

			JSONObject labelProperties = (JSONObject) chartPreferences.get("labelJson");
			ChartPreferenceHandler.setLabelProperties(labelProperties, seriesY, true, dimension);

			String ySeries = (String) it.next();
			String function = (String) ySeriesValues.get(ySeries);

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

			SeriesGrouping ySeriesGroup = SeriesGroupingImpl.create();
			ySeriesGroup.setEnabled(true);
			if ((function != null) && !(function.equals("#"))) {
				if (function.equalsIgnoreCase("DISTINCTCOUNT"))
					ySeriesGroup.setAggregateExpression(function);
				else
					ySeriesGroup.setAggregateExpression(WordUtils.capitalize(function.toLowerCase()));
			} else {
				// ySeriesGroup.setAggregateExpression("Sum");
			}
			yQuery.setGrouping(ySeriesGroup);
			seriesY.getDataDefinition().add(yQuery);

			if (isHive) {
				ySeriesSize = ySeriesSize.toLowerCase();
			}
			Query yQuery2 = QueryImpl.create("row[\"" + ySeriesSize + "\"]");
			SeriesGrouping ySeriesGroupSize = SeriesGroupingImpl.create();
			ySeriesGroupSize.setEnabled(true);
			if ((functionSize != null) && !(functionSize.equals("#"))) {
				if (functionSize.equalsIgnoreCase("DISTINCTCOUNT"))
					ySeriesGroupSize.setAggregateExpression(functionSize);
				else
					ySeriesGroupSize.setAggregateExpression(WordUtils.capitalize(functionSize.toLowerCase()));
			} else {
				// ySeriesGroupSize.setAggregateExpression("Sum");
			}
			yQuery2.setGrouping(ySeriesGroupSize);
			seriesY.getDataDefinition().add(yQuery2);

			yAxis.setType(AxisType.LINEAR_LITERAL);
			Label baseTitle = LabelImpl.create();
			baseTitle.getCaption().setValue(ySeries);

			seriesY.setSeriesIdentifier(baseTitle.getCaption().getValue());

			SeriesDefinition ySeriesDef = SeriesDefinitionImpl.create();
			if (topColors != null && i < topColors.size()) {
				ySeriesDef.getSeriesPalette().getEntries()
						.add(ChartPreferenceHandler.hexToRGB((String) topColors.get(i)));
			}
			ySeriesDef.getSeries().add(seriesY);
			yAxis.getSeriesDefinitions().add(ySeriesDef);
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
