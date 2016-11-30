/*
 * @(#)  Label.java May 27, 2005
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

import com.queryio.common.exporter.ExportConstants;

/**
 * Label
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public class Label extends ExportableItem {
	private String sText;

	private int fontStyle = Font.STYLE_PLAIN;

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param sText
	 */
	public Label(final int left, final int top, final int width, final int height, final String sText) {
		super(left, top, width, height);
		this.sText = sText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.dstruct.IExportableItem#getType()
	 */
	public int getType() {
		return ExportConstants.ITEM_TYPE_LABEL;
	}

	/**
	 * Method getText
	 * 
	 * @return
	 */
	public final String getText() {
		return this.sText;
	}

	/**
	 * Method setText
	 * 
	 * @param string
	 */
	public void setText(final String string) {
		this.sText = string;
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}
}
