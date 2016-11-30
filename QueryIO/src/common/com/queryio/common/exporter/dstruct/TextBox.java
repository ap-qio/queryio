/*
 * @(#)  TextBox.java May 30, 2005
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
 * TextBox
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public class TextBox extends ExportableItem {
	private String sText;
	private boolean bReadOnly;

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param sText
	 */
	public TextBox(final int left, final int top, final int width, final int height, final String sText) {
		this(left, top, width, height, sText, true);
	}

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param sText
	 * @param bReadOnly
	 */
	protected TextBox(final int left, final int top, final int width, final int height, final String sText,
			final boolean bReadOnly) {
		super(left, top, width, height);
		this.sText = sText;
		this.bReadOnly = bReadOnly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.dstruct.IExportableItem#getType()
	 */
	public int getType() {
		return ExportConstants.ITEM_TYPE_TEXTBOX;
	}

	/**
	 * Method isReadOnly
	 * 
	 * @return
	 */
	public final boolean isReadOnly() {
		return this.bReadOnly;
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
	 * Method setReadOnly
	 * 
	 * @param b
	 */
	public void setReadOnly(final boolean b) {
		this.bReadOnly = b;
	}

	/**
	 * Method setText
	 * 
	 * @param string
	 */
	public void setText(final String string) {
		this.sText = string;
	}
}
