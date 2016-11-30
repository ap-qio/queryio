/*
 * @(#)  AppVerticalStackChart.java Feb 7, 2005
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.charts.independent;

import com.queryio.common.charts.components.MarkerObject;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.series.Series;
import com.queryio.common.charts.series.YAxisSeries;
import com.queryio.common.exporter.dstruct.Rectangle;

/**
 * 
 * @author Exceed Consultancy Services
 */
public final class AppVerticalStackChart extends AppAbstractBarChart {

	public AppVerticalStackChart(final UserInterface userInterface, final int nodeType, final int productID) {
		super(userInterface, nodeType, productID);
	}

	public void drawSeries(final UserInterface graphics) {
		final Series[] yAxisSeries = this.getYAxisSeries();
		if (yAxisSeries.length > 0) {
			int iCurrentCount = this.getXAxisSeries().getCurrentCount();

			int yValue;
			int xCor;
			int yCor;
			int prevYCor = this.yStart - 1;
			int ht = 0;
			YAxisSeries ySeries;
			for (int i = 0; i < iCurrentCount; i++) {
				xCor = this.xStart + i * this.eachDataWidth;
				prevYCor = this.yStart - 1;
				for (int j = 0; j < yAxisSeries.length; j++) {
					if (this.shouldDrawSeries(j)) {
						ySeries = (YAxisSeries) yAxisSeries[j];
						yValue = ySeries.getValue(i);
						yCor = this.resolveYCorordinate(yValue);
						if ((Integer.MIN_VALUE != yValue) && (yCor < this.yStart)) {
							ht = this.yStart - yCor;
							if (this.bShowToolTip) {
								final Rectangle marker = new Rectangle(xCor + 1 + this.insetForBarOnOneside,
										prevYCor - ht + 1, this.eachDataWidth - (this.insetForBarOnOneside * 2), ht);
								final String xValue = this.getXAxisSeries().getFormattedValue(i);
								final String formattedYValue = String.valueOf(yValue);
								this.addMarkerObject(new MarkerObject(marker, formattedYValue, xValue, i, j));
							}
							graphics.setBackground(getColorForIndex(j, this.chartProperties));
							graphics.fillRectangle(xCor + 1 + this.insetForBarOnOneside, prevYCor - ht + 1,
									this.eachDataWidth - (this.insetForBarOnOneside * 2), ht);
							prevYCor -= ht;
						}
					}
				}
			}
		}
	}

	public int[] getYAxisMinMaxValues() {
		if (this.bMinMaxYValueSet) {
			return super.getYAxisMinMaxValues();
		}

		final Series[] yAxisSeries = this.getYAxisSeries();
		YAxisSeries series = null;
		final int[] minMax = new int[] { Integer.MAX_VALUE, Integer.MIN_VALUE };
		for (int i = 0; i < yAxisSeries.length; i++) {
			if (this.shouldDrawSeries(i)) {
				series = (YAxisSeries) yAxisSeries[i];
				if (Integer.MAX_VALUE != series.getMinValue()) {
					minMax[0] = Math.min(minMax[0], series.getMinValue());
				}
			}
		}
		int capacity = 0;
		if (series != null) {
			capacity = series.getCapacity();
		}
		for (int j = 0; j < capacity; j++) {
			int value = Integer.MIN_VALUE;
			for (int i = 0; i < yAxisSeries.length; i++) {
				if (this.shouldDrawSeries(i)) {
					series = (YAxisSeries) yAxisSeries[i];
					if (Integer.MIN_VALUE == value) {
						value = series.getValue(j);
					} else {
						value += series.getValue(j);
					}
				}
			}
			minMax[1] = Math.max(minMax[1], value);
		}
		if ((minMax[0] == Integer.MAX_VALUE) && (minMax[1] == Integer.MIN_VALUE)) {
			minMax[0] = 0;
			minMax[1] = 2;
		} else if (minMax[0] == minMax[1]) {
			if (minMax[0] >= 1) {
				minMax[0] -= 1;
			}
			minMax[1] += 1;
		}

		return minMax;

	}

}
