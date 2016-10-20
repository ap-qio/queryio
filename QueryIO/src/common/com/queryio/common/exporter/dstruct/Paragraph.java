/*
 * @(#)  Paragraph.java Jun 30, 2005
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
 * Paragraph
 * 
 * @author Exceed Consultancy Services
 * @version 10.5
 */
public class Paragraph extends ExportableItem
{
	private final Label [] items;
	private final boolean useNewLineInsteadOfPargraph;

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 */
	public Paragraph(final int left, final int top, final int width, final int height,
		final Label [] items, boolean useNewLineInsteadOfPargraph)
	{
		super(left, top, width, height);
		this.items = items;
		this.useNewLineInsteadOfPargraph = useNewLineInsteadOfPargraph;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.dstruct.IExportableItem#getType()
	 */
	public int getType()
	{
		return ExportConstants.ITEM_TYPE_PARAGRAH;
	}

	/**
	 * Method getItems
	 * 
	 * @return
	 */
	public final Label [] getItems()
	{
		return this.items;
	}

	public boolean isUseNewLineInsteadOfPargraph() 
	{
		return useNewLineInsteadOfPargraph;
	}
}
