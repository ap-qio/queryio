/*
 * @(#)  Tree.java May 31, 2005
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

import javax.swing.tree.TreeModel;

import com.queryio.common.exporter.ExportConstants;

/**
 * Tree
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public class Tree extends ExportableItem
{
	private final TreeModel model;

	/**
	 * Constructor
	 * 
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 */
	public Tree(final int left, final int top, final int width, final int height, final TreeModel model)
	{
		super(left, top, width, height);
		this.model = model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.dstruct.IExportableItem#getType()
	 */
	public int getType()
	{
		return ExportConstants.ITEM_TYPE_TREE;
	}

	/**
	 * Method getModel
	 * 
	 * @return
	 */
	public final TreeModel getModel()
	{
		return this.model;
	}
}
