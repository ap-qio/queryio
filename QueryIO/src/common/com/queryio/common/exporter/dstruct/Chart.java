/*
 * @(#)  Chart.java May 31, 2005
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
package com.queryio.common.exporter.dstruct;

import java.awt.Graphics2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.queryio.common.charts.independent.AppChart;
import com.queryio.common.charts.independent.IScrollableChart;
import com.queryio.common.charts.interfaces.UserInterface;
import com.queryio.common.charts.util.UIGraphicsFactory;
import com.queryio.common.exporter.ExportConstants;
import com.queryio.common.exporter.ExportManager;
import com.queryio.common.exporter.exceptions.ExporterException;
import com.queryio.common.util.ResourceManager;

/**
 * Chart
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public class Chart extends ExportableItem {
	private static final transient ResourceManager RM = ExportManager.RM;
	// private static final transient String sClassName =
	// "com.queryio.sysmoncommon.exporter.dstruct.Chart";
	AppChart chart = null;

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param chart
	 */
	public Chart(final int left, final int top, final int width, final int height, final AppChart chart) {
		super(left, top, width, height);
		this.chart = chart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.dstruct.IExportableItem#getType()
	 */
	public int getType() {
		return ExportConstants.ITEM_TYPE_CHART;
	}

	public boolean isScrollable() {
		return (this.chart instanceof IScrollableChart);
	}

	public void drawChartForPDF(final Graphics2D g2, final int actualWidth, final int actualHeight) {
		final boolean scrollableChart = isScrollable();
		boolean scrollable = false;
		int height = actualHeight;
		if (scrollableChart) {
			scrollable = ((IScrollableChart) this.chart).isScrollable();
			((IScrollableChart) this.chart).setScrollable(false);
		}

		this.chart.drawChartForPDF(g2, actualWidth, height);

		if (scrollableChart) {
			((IScrollableChart) this.chart).setScrollable(scrollable);
		}
	}

	/**
	 * Method getFileName
	 * 
	 * @param sDir
	 * @param actualWidth
	 * @param actualHeight
	 * @param cnt
	 * @return
	 */
	public String getFileName(final String sDir, final int actualWidth, final int actualHeight, final int cnt)
			throws ExporterException {
		final boolean scrollableChart = (this.chart instanceof IScrollableChart);
		boolean scrollable = false;
		int height = actualHeight;
		if (scrollableChart) {
			scrollable = ((IScrollableChart) this.chart).isScrollable();
			if (scrollable) {
				final UserInterface scratchUserInterface = UIGraphicsFactory
						.getUserInterface(this.chart.getUserInterface().getUserInterfaceType());
				scratchUserInterface.createGraphics(actualWidth, height);
				height = ((IScrollableChart) this.chart).getTotalChartHeight(scratchUserInterface, actualWidth, height);
				scratchUserInterface.disposeGraphics();
				scratchUserInterface.disposeImage();
			}
			((IScrollableChart) this.chart).setScrollable(false);
		}

		final byte[] data = this.chart.getPNG(actualWidth, height, null);

		if (scrollableChart) {
			((IScrollableChart) this.chart).setScrollable(scrollable);
		}

		File file = null;
		File dir = null;
		try {
			dir = new File(sDir);
			if (dir.exists()) {
				if (!dir.isDirectory()) {
					throw new ExporterException(RM.getString("ERR_CREATING_DIRECTORY") + dir.getPath()); //$NON-NLS-1$
				}
			} else {
				dir.mkdirs();
			}
			file = new File(dir, "img_" + cnt); //$NON-NLS-1$ //$NON-NLS-2$
			final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), data.length); // $IGN_Close_streams$
			try {
				this.chart.getUserInterface().disposeImage();
				this.chart.resetOutputStream();
			} catch (Exception e) {
				// TODO
			}
			bos.write(data);
			bos.close();
		} catch (final Exception exception) {
			throw new ExporterException(
					RM.getString("ERR_WRITING_IMAGE_DATA") + file != null ? file.getAbsolutePath() : "", exception); //$NON-NLS-1$
		}
		return dir.getName() + '/' + file.getName();
	}

	/**
	 * Method getData
	 * 
	 * @param actualWidth
	 * @param actualHeight
	 * @return
	 */
	public byte[] getData(final int actualWidth, final int actualHeight) {
		return this.chart.getPNG(actualWidth, actualHeight, null);
	}

	/**
	 * Method setBackgroundColor
	 * 
	 * @param color
	 */
	public void setBackgroundColor(final Color color) {
		this.chart.setBackgroundColor(color);
	}

	public AppChart getAppChart() {
		return this.chart;
	}
}
