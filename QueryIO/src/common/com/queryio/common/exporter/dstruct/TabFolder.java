/*
 * @(#)  TabFolder.java Jun 3, 2005
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
 * TabFolder
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public class TabFolder extends ExportableItem {
	private final String id;
	private final String[] tabNames;
	private final IExportableItem[][] items;

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param id
	 * @param tabNames
	 * @param items
	 */
	public TabFolder(final int left, final int top, final int width, final int height, final String id,
			final String[] tabNames, final IExportableItem[][] items) {
		super(left, top, width, height);
		this.id = id;
		this.tabNames = tabNames;
		this.items = items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.dstruct.IExportableItem#getType()
	 */
	public int getType() {
		return ExportConstants.ITEM_TYPE_TABFOLDER;
	}

	/**
	 * Method getId
	 * 
	 * @return
	 */
	public final String getId() {
		return this.id;
	}

	/**
	 * Method getTabNames
	 * 
	 * @return
	 */
	public final String[] getTabNames() {
		return this.tabNames;
	}

	/**
	 * Method getItems
	 * 
	 * @return
	 */
	public final IExportableItem[][] getItems() {
		return this.items;
	}
}
