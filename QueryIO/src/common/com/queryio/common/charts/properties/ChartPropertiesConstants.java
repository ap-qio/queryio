/*
 * @(#) ChartPropertiesConstants.java May 26, 2006 Copyright (C) 2002 Exceed
 * Consultancy Services. All Rights Reserved. This software is proprietary
 * information of Exceed Consultancy Services and constitutes valuable trade
 * secrets of Exceed Consultancy Services. You shall not disclose this
 * information and shall use it only in accordance with the terms of License.
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.queryio.common.charts.properties;

/**
 * This class defines constants for the various properties a chart can have.
 * These constants are used as keys for the hashmaps and also for UI.
 * 
 * @author Exceed Consultancy Services
 */
class ChartPropertiesConstants
{
	private static int i = 0;

	private ChartPropertiesConstants()
	{

	}

	static final int CHART_TITLE = i++;
	static final int BACKGROUND_COLOUR = i++;
	static final int ALTERNATE_BACKGROUND_COLOUR = i++;
	static final int STRETCH_ALTERNATE_BACKGROUND = i++;
	static final int AREA_COLOUR = i++;
	static final int TITLE_FONT = i++;
	static final int AXIS_FONT = i++;
	static final int LEGEND_FONT = i++;
	static final int TICK_FONT_DEFAULT = i++;
	static final int TICK_FONT8 = i++;
	static final int X_AXIS_TITLE = i++;
	static final int Y_AXIS_TITLE = i++;
	static final int COLOURS = i++;
	static final int Y_AXIS_SERIES_TEXT = i++;
	static final int X_AXIS_GRIDS = i++;
	static final int Y_AXIS_GRIDS = i++;
	static final int Y_AXIS_SHOW = i++;
	static final int SHOW_LEGEND = i++;
	static final int LOGARITHMIC = i++;
	static final int SHOW_CHART_TITLE = i++;
	static final int SHOW_X_AXIS_TITLE = i++;
	static final int SHOW_Y_AXIS_TITLE = i++;
	static final int SHOW_CHART_MARKER = i++;
	static final int SHOW_CHART_LABEL = i++;
	static final int CHART_TITLE_FONT = i++;
	static final int X_AXIS_TITLE_FONT = i++;
	static final int Y_AXIS_TITLE_FONT = i++;
	static final int CHART_TITLE_TEXT_COLOUR = i++;
	static final int X_AXIS_TITLE_TEXT_COLOUR = i++;
	static final int Y_AXIS_TITLE_TEXT_COLOUR = i++;
	static final int CHART_TITLE_BACKGROUND_COLOUR = i++;
	static final int X_AXIS_TITLE_BACKGROUND_COLOUR = i++;
	static final int Y_AXIS_TITLE_BACKGROUND_COLOUR = i++;
	static final int LEGEND_BACKGROUND_COLOUR = i++;
	static final int LEGEND_TEXT_COLOUR = i++;
	static final int TICK_BACKGROUND_COLOUR = i++;
	static final int TICK_TEXT_COLOUR = i++;
	static final int FIT_TO_SCALE = i++;
}
