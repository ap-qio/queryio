/*
 * @(#)  ExportableItem.java May 27, 2005
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

/**
 * ExportableItem
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public abstract class ExportableItem implements IExportableItem {
	private final int left, top;
	private int width, height;
	private boolean xmlExport = true;

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 */
	protected ExportableItem(final int left, final int top, final int width, final int height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}

	/**
	 * Method getHeight
	 * 
	 * @return
	 */
	public final int getHeight() {
		return this.height;
	}

	/**
	 * Method getLeft
	 * 
	 * @return
	 */
	public final int getLeft() {
		return this.left;
	}

	/**
	 * Method getTop
	 * 
	 * @return
	 */
	public final int getTop() {
		return this.top;
	}

	/**
	 * Method getWidth
	 * 
	 * @return
	 */
	public final int getWidth() {
		return this.width;
	}

	public void setSize(final int width, final int height) {
		this.height = height;
		this.width = width;
	}

	public boolean isXmlExport() {
		return xmlExport;
	}

	public void setXmlExport(boolean xmlExport) {
		this.xmlExport = xmlExport;
	}

}
