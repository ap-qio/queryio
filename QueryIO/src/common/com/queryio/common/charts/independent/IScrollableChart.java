package com.queryio.common.charts.independent;

import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.exporter.dstruct.Rectangle;

public interface IScrollableChart
{
	boolean isXAxisTitle();

	boolean isYAxisTitle();

	boolean isScrollable();

	void setScrollable(boolean scrollable);

	Rectangle getSeriesPlotBounds();

	Rectangle getYAxisTitleBounds();

	Rectangle getXAxisTitleBounds();

	Rectangle getYAxisBounds();

	void drawScrollableArea(UserInterface graphics, int originY, int width, int maximum);

	int getTotalPlotHeight();
	
	int getTotalChartHeight(UserInterface graphics, int width, int height);

	int getIncrement();

}
