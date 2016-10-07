/*
 * @(#)  LabelTextPanel.java Jun 30, 2005
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
 * LabelTextPanel
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public class LabelTextPanel extends ExportableItem
{
	private final IExportableItem[][] items;

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 */
	public LabelTextPanel(final int left, final int top, final int width, final int height,
			final IExportableItem[][] items)
	{
		super(left, top, width, height);
		this.items = items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.dstruct.IExportableItem#getType()
	 */
	public int getType()
	{
		return ExportConstants.ITEM_TYPE_LABELTEXTPANEL;
	}

	/**
	 * Method getItems
	 * 
	 * @return
	 */
	public final IExportableItem[][] getItems()
	{
		return this.items;
	}
}
