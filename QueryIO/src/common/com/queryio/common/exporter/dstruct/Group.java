/*
 * @(#)  Group.java May 27, 2005
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
 * Group
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public class Group extends ExportableItem
{
	private String sHeading;
	private IExportableItem[] items;

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 */
	public Group(final int left, final int top, final int width, final int height)
	{
		this(left, top, width, height, null, null);
	}

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param heading
	 * @param items
	 */
	public Group(final int left, final int top, final int width, final int height, final String heading,
			final IExportableItem[] items)
	{
		super(left, top, width, height);
		this.sHeading = heading;
		this.items = items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.dstruct.IExportableItem#getType()
	 */
	public int getType()
	{
		return ExportConstants.ITEM_TYPE_GROUP;
	}

	/**
	 * Method getHeading
	 * 
	 * @return
	 */
	public final String getHeading()
	{
		return this.sHeading;
	}

	/**
	 * Method getItems
	 * 
	 * @return
	 */
	public final IExportableItem[] getItems()
	{
		return this.items;
	}

	/**
	 * Method setHeading
	 * 
	 * @param s
	 */
	public void setHeading(final String s)
	{
		this.sHeading = s;
	}

	/**
	 * Method setItems
	 * 
	 * @param items
	 */
	public void setItems(final IExportableItem[] items)
	{
		this.items = items;
	}
}
