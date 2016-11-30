/*
 * @(#)  HTMLPage.java May 31, 2005
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
 * HTMLPage
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public class HTMLPage extends ExportableItem {
	private final String fileName;

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param fileName
	 */
	public HTMLPage(final int left, final int top, final int width, final int height, final String fileName) {
		super(left, top, width, height);
		this.fileName = fileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.dstruct.IExportableItem#getType()
	 */
	public int getType() {
		return ExportConstants.ITEM_TYPE_HTMLPAGE;
	}

	/**
	 * Method getFileName
	 * 
	 * @return
	 */
	public final String getFileName() {
		return this.fileName;
	}
}
