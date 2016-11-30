package com.queryio.common.report;

import java.util.Iterator;

import org.apache.commons.lang.WordUtils;
import org.eclipse.birt.chart.examples.radar.model.type.RadarSeries;
import org.eclipse.birt.chart.examples.radar.model.type.impl.RadarSeriesImpl;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.SortOption;
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
import org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl;
import org.eclipse.birt.chart.model.layout.ClientArea;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.queryio.core.customtags.BigQueryIdentifiers;

public class RadarChartHandler {

	public static Chart getRadarChart(String title, int dimension, int chartWidth, int chartHeight,
			JSONObject xSeriesObj, JSONObject ySeriesJSON, String xSeriesTitle, String ySeriesTitle,
			JSONObject chartPreferences, boolean isHive, String yGrouping) throws Exception {

		JSONObject titleJSON = (JSONObject) chartPreferences.get("titleJson");
		JSONObject commonJSON = (JSONObject) chartPreferences.get("commonJson");
		ChartWithoutAxes radarChart = ChartWithoutAxesImpl.create();

		radarChart.setType("Radar Chart");

		// radarChart.setSubType("Spider Radar Chart");
		// radarChart.setSubType("Bullseye Radar Chart");
		radarChart.setSubType("Standard Radar Chart");

		Plot p = radarChart.getPlot();
		ClientArea clientArea = p.getClientArea();

		/* common json properties */
		JSONArray topColors = null;
		if (commonJSON != null) {
			radarChart.getBlock().setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("background")));
			p.setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("plotBackground")));
			radarChart.getBlock().getBounds().setWidth(chartWidth);
			radarChart.getBlock().getBounds().setHeight(chartHeight);
			/* Client */
			clientArea.setBackground(ChartPreferenceHandler.hexToRGB((String) commonJSON.get("clientBackground")));
			// Empty message
			String msg = (String) commonJSON.get("emptyChartMessage");
			if (msg != null && msg.length() > 0) {
				radarChart.getEmptyMessage().setVisible(true);
				radarChart.getEmptyMessage().getCaption().setValue(msg);
			}
			topColors = (JSONArray) commonJSON.get("topColors");
		}

		ChartPreferenceHandler.setTitleProperties(titleJSON, radarChart, title);

		// Chart Out Line
		JSONObject chartOutLine = (JSONObject) chartPreferences.get("outLineJson");
		if (chartOutLine != null && chartOutLine.size() > 0) {
			Boolean isChartOutLineVisible = (Boolean) chartOutLine.get("visible");
			if (isChartOutLineVisible) {
				int chartOutlineWidthInt = Integer.parseInt((String) chartOutLine.get("width"));
				String chartOutlineColor = (String) chartOutLine.get("color");
				radarChart.getBlock()
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
			radarChart.getTitle()
					.setInsets(InsetsImpl.create(titleInsetTop, titleInsetLeft, titleInsetBottom, titleInsetRight));
		}

		/* Legend */
		Legend legend = radarChart.getLegend();
		JSONObject legendProperties = (JSONObject) chartPreferences.get("legendJson");
		ChartPreferenceHandler.setLegendProperties(legendProperties, legend, ySeriesTitle);

		/* X-Axis */
		Series xSeries = SeriesImpl.create();

		String xSeriesPie = (String) xSeriesObj.get(BigQueryIdentifiers.X_SERIES);
		if (isHive)
			xSeriesPie = xSeriesPie.toLowerCase();
		Query xQuery = QueryImpl.create("row[\"" + xSeriesPie + "\"]");
		xSeries.getDataDefinition().add(xQuery);
		xSeries.getLabel().getCaption().setValue(xSeriesTitle);
		xSeries.getLabel().setVisible(true);
		SeriesDefinition xSeriesDef = SeriesDefinitionImpl.create();
		radarChart.getSeriesDefinitions().add(xSeriesDef);
		if (topColors != null) {
			for (int i = 0; i < topColors.size(); i++) {
				xSeriesDef.getSeriesPalette().getEntries()
						.add(ChartPreferenceHandler.hexToRGB((String) topColors.get(i)));
			}
		}
		xSeriesDef.getSeriesPalette().shift(0);
		xSeriesDef.getSeries().add(xSeries);
		SeriesGrouping seriesGroup = SeriesGroupingImpl.create();
		seriesGroup.setEnabled(true);
		xSeriesDef.setGrouping(seriesGroup);

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

		Iterator it = ySeriesJSON.keySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			RadarSeries radarLeader = RadarSeriesImpl.create();

			radarLeader.getLineAttributes().setVisible(true);
			radarChart.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);

			// Label JSON
			JSONObject labelProperties = (JSONObject) chartPreferences.get("labelJson");
			ChartPreferenceHandler.setLabelProperties(labelProperties, radarLeader, false, dimension);

			String ySeriesPie = (String) it.next();
			String function = (String) ySeriesJSON.get(ySeriesPie);
			if ((ySeriesPie != null) && (isHive)) {
				ySeriesPie = ySeriesPie.toLowerCase();
			}
			Query yQuery = QueryImpl.create("row[\"" + ySeriesPie + "\"]");

			if ((function != null) && !(function.equals("#"))) {
				SeriesGrouping pieSeriesGroup = SeriesGroupingImpl.create();
				pieSeriesGroup.setEnabled(true);
				if (function.equalsIgnoreCase("DISTINCTCOUNT"))
					pieSeriesGroup.setAggregateExpression(function);
				else
					pieSeriesGroup.setAggregateExpression(WordUtils.capitalize(function.toLowerCase()));
				yQuery.setGrouping(pieSeriesGroup);
			}
			radarLeader.getDataDefinition().add(yQuery);

			radarLeader.getLabel().getCaption().setValue(ySeriesTitle);

			Label baseTitle = LabelImpl.create();
			radarLeader.setSeriesIdentifier(baseTitle.getCaption().getValue());
			SeriesDefinition ySeriesDef = SeriesDefinitionImpl.create();
			xSeriesDef.getSeriesDefinitions().add(ySeriesDef);
			if (topColors != null && i < topColors.size()) {
				ySeriesDef.getSeriesPalette().getEntries()
						.add(ChartPreferenceHandler.hexToRGB((String) topColors.get(i)));
			}
			ySeriesDef.getSeries().add(radarLeader);
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

		return radarChart;
	}

}
