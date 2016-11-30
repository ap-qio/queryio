/*
 * @(#)  Table.java May 31, 2005
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

import javax.swing.table.TableModel;

import com.queryio.common.exporter.ExportConstants;

/**
 * Table
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public class Table extends ExportableItem {
	private TableModel model;
	private final int[] columnWidths;
	private final boolean bTotal;

	private boolean showAlternateColor = true;
	private String tableHeader = null;

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param model
	 * @param colWidths
	 */
	public Table(final int left, final int top, final int width, final int height, final TableModel model,
			final int[] colWidths) {
		this(left, top, width, height, model, colWidths, false);
	}

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param model
	 * @param colWidths
	 * @param bTotal
	 */
	public Table(final int left, final int top, final int width, final int height, final TableModel model,
			final int[] colWidths, final boolean bTotal) {
		this(left, top, width, height, model, colWidths, bTotal, null);
	}

	public Table(final int left, final int top, final int width, final int height, final TableModel model,
			final int[] colWidths, String tableHeader) {
		this(left, top, width, height, model, colWidths, false, tableHeader);
	}

	public Table(final int left, final int top, final int width, final int height, final TableModel model,
			final int[] colWidths, final boolean bTotal, String tableHeader) {
		super(left, top, width, height);
		this.model = model;
		this.columnWidths = colWidths;
		this.bTotal = bTotal;
		this.tableHeader = tableHeader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.dstruct.IExportableItem#getType()
	 */
	public int getType() {
		return ExportConstants.ITEM_TYPE_TABLE;
	}

	/**
	 * Method getColumnWidths
	 * 
	 * @return
	 */
	public final int[] getColumnWidths() {
		return this.columnWidths;
	}

	/**
	 * Method getModel
	 * 
	 * @return
	 */
	public final TableModel getModel() {
		return this.model;
	}

	/**
	 * Method setModel
	 * 
	 * @param model
	 */
	public void setModel(final TableModel model) {
		this.model = model;
	}

	/**
	 * Method isTotal
	 * 
	 * @return boolean
	 */
	public final boolean isTotal() {
		return this.bTotal;
	}

	public boolean isShowAlternateColor() {
		return showAlternateColor;
	}

	public void setShowAlternateColor(boolean showAlternateColor) {
		this.showAlternateColor = showAlternateColor;
	}

	public String getTableHeader() {
		return tableHeader;
	}

	public void setTableHeader(String tableHeader) {
		this.tableHeader = tableHeader;
	}

}
