package com.queryio.common.charts.swinggraphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.queryio.common.charts.independent.AppChart;

/*
 * @(#)  AppChartPanel.java Feb 7, 2005
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

/**
 * 
 * @author Exceed Consultancy Services
 */
public class AppChartPanel extends JPanel {
	private static final long serialVersionUID = 752600005701000031L;
	AppChart chartObject;
	private BufferedImage chartImage;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */
	public void paint(final Graphics g) {
		final int width = this.getWidth();
		final int height = this.getHeight();

		this.chartObject.createImage(width, height);
		this.chartImage = ((SWINGUserInterface) this.chartObject.getUserInterface()).getImage();
		g.drawImage(this.chartImage, 0, 0, width, height, null);
	}

	/**
	 * method getChartObject
	 * 
	 * @return
	 */
	public final AppChart getChartObject() {
		return this.chartObject;
	}

	/**
	 * method setChartObject
	 * 
	 * @param object
	 */
	public void setChartObject(final AppChart object) {
		this.chartObject = object;
	}
}
