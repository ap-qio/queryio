/*
 * @(#)  AbstractExporter.java Jun 1, 2005
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
package com.queryio.common.exporter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.Chart;
import com.queryio.common.exporter.dstruct.Group;
import com.queryio.common.exporter.dstruct.HTMLPage;
import com.queryio.common.exporter.dstruct.IExportableItem;
import com.queryio.common.exporter.dstruct.Label;
import com.queryio.common.exporter.dstruct.LabelTextPanel;
import com.queryio.common.exporter.dstruct.Paragraph;
import com.queryio.common.exporter.dstruct.TabFolder;
import com.queryio.common.exporter.dstruct.Table;
import com.queryio.common.exporter.dstruct.TextBox;
import com.queryio.common.exporter.dstruct.Tree;
import com.queryio.common.exporter.exceptions.ExporterException;
import com.queryio.common.util.ResourceManager;

/**
 * AbstractExporter
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public abstract class AbstractExporter
{
	protected static final transient ResourceManager RM = ExportManager.RM;

	protected ArrayList alImages = new ArrayList();

	protected String sCurrentProject;
	protected String sHeaderImagePath;
	protected int iWidth; 
	protected int iHeight;
	protected boolean bExportingResult = false;
	protected AbstractExportableNode node;
	protected int imgCount = 0;

	/**
	 * Method setProjectName
	 * 
	 * @param s
	 */
	public void setProjectName(final String s)
	{
		this.sCurrentProject = s;
	}
	
	public void setHeaderImagePath(String path)
	{
		this.sHeaderImagePath = path;
	}

	public void reset()
	{
		// classes extending should override this method
	}

	/**
	 * Method setSize
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(final int width, final int height)
	{
		this.iWidth = width;
		this.iHeight = height;
	}

	/**
	 * Method setImageList
	 * 
	 * @param alImages
	 */
	public void setImageList(final ArrayList alImages)
	{
		this.alImages = alImages;
	}

	/**
	 * Method setExportingResult
	 * 
	 * @param exportingResult
	 */
	public void setExportingResult(final boolean exportingResult)
	{
		this.bExportingResult = exportingResult;
	}

	/**
	 * Method exportNodeItems
	 * 
	 * @throws ExporterException
	 */
	protected void exportNodeItems() throws ExporterException
	{
		this.exportItems(this.node.getItems());
	}

	/**
	 * Method exportItems
	 * 
	 * @param items
	 * @throws ExporterException
	 */
	protected void exportItems(final IExportableItem[] items) throws ExporterException
	{
		if ((items != null) && (items.length > 0))
		{
			for (int i = 0; i < items.length; i++)
			{
				this.exportItem(items[i]);
			}
		}
	}

	/**
	 * Method getDateString
	 * 
	 * @return
	 */
	protected String getDateString()
	{
		return "Date Generated: " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date()); //$NON-NLS-1$
	}

	/**
	 * Method convertTreeModelToTableModel
	 * 
	 * @param treeModel
	 * @return
	 */
	protected TableModel convertTreeModelToTableModel(final TreeModel treeModel)
	{
		final DefaultTableModel tableModel = new DefaultTableModel();
		if (treeModel != null)
		{
			tableModel.addColumn(RM.getString("VALUE_DUMMY_COLUMN")); //$NON-NLS-1$
			final TreeNode root = (TreeNode) treeModel.getRoot();
			if (root != null)
			{
				this.addNodeToTable(tableModel, root, 0);
			}
		}
		return tableModel;
	}

	/**
	 * Method addNodeToTable
	 * 
	 * @param tableModel
	 * @param node
	 * @param iLevel
	 * @param iRowCount
	 */
	protected void addNodeToTable(final DefaultTableModel tableModel, final TreeNode node, final int iLevel)
	{
		if (node != null)
		{
			// Add the node as a string with leading spaces for levels
			final StringBuffer sbBuff = new StringBuffer();
			for (int i = 0; i < iLevel; i++)
			{
				sbBuff.append("    "); //$NON-NLS-1$
			}
			sbBuff.append(node.toString());
			tableModel.addRow(new String[] { sbBuff.toString() });
			// Add all the child nodes of this node
			final int iChildCount = node.getChildCount();
			if (iChildCount > 0)
			{
				for (int i = 0; i < iChildCount; i++)
				{
					this.addNodeToTable(tableModel, node.getChildAt(i), iLevel + 1);
				}
			}
		}
	}

	/**
	 * Method exportItem
	 * 
	 * @param item
	 * @throws ExporterException
	 */
	protected void exportItem(final IExportableItem item) throws ExporterException
	{
		if (item != null)
		{
			switch (item.getType())
			{
				case ExportConstants.ITEM_TYPE_GROUP:
				{
					if (item instanceof Group)
					{
						this.exportItem((Group) item);
					}
					else
					{
						throw new ExporterException(RM.getString("ERR_INVALID_CLASS_FOR_GROUP") //$NON-NLS-1$
								+ item.getClass());
					}
					break;
				}
				case ExportConstants.ITEM_TYPE_TABLE:
				{
					if (item instanceof Table)
					{
						this.exportItem((Table) item);
					}
					else
					{
						throw new ExporterException(RM.getString("ERR_INVALID_CLASS_FOR_TABLE") //$NON-NLS-1$
								+ item.getClass());
					}
					break;
				}
				case ExportConstants.ITEM_TYPE_CHART:
				{
					if (item instanceof Chart)
					{
						this.exportItem((Chart) item);
					}
					else
					{
						throw new ExporterException(RM.getString("ERR_INVALID_CLASS_FOR_CHART") //$NON-NLS-1$
								+ item.getClass());
					}
					break;
				}
				case ExportConstants.ITEM_TYPE_TREE:
				{
					if (item instanceof Tree)
					{
						this.exportItem((Tree) item);
					}
					else
					{
						throw new ExporterException(RM.getString("ERR_INVALID_CLASS_FOR_TREE") //$NON-NLS-1$
								+ item.getClass());
					}
					break;
				}
				case ExportConstants.ITEM_TYPE_LABEL:
				{
					if (item instanceof Label)
					{
						this.exportItem((Label) item);
					}
					else
					{
						throw new ExporterException(RM.getString("ERR_INVALID_CLASS_FOR_LABEL") //$NON-NLS-1$
								+ item.getClass());
					}
					break;
				}
				case ExportConstants.ITEM_TYPE_TEXTBOX:
				{
					if (item instanceof TextBox)
					{
						this.exportItem((TextBox) item);
					}
					else
					{
						throw new ExporterException(RM.getString("ERR_INVALID_CLASS_FOR_TEXTBOX") //$NON-NLS-1$
								+ item.getClass());
					}
					break;
				}
				case ExportConstants.ITEM_TYPE_HTMLPAGE:
				{
					if (item instanceof HTMLPage)
					{
						this.exportItem((HTMLPage) item);
					}
					else
					{
						throw new ExporterException(RM.getString("ERR_INVALID_CLASS_FOR_HTMLPAGE") //$NON-NLS-1$
								+ item.getClass());
					}
					break;
				}
				case ExportConstants.ITEM_TYPE_TABFOLDER:
				{
					if (item instanceof TabFolder)
					{
						this.exportItem((TabFolder) item);
					}
					else
					{
						throw new ExporterException(RM.getString("ERR_INVALID_CLASS_FOR_TABFOLDER") //$NON-NLS-1$
								+ item.getClass());
					}
					break;
				}
				case ExportConstants.ITEM_TYPE_LABELTEXTPANEL:
				{
					if (item instanceof LabelTextPanel)
					{
						this.exportItem((LabelTextPanel) item);
					}
					else
					{
						throw new ExporterException(RM.getString("ERR_INVALID_CLASS_FOR_LABELTEXTPANEL") //$NON-NLS-1$
								+ item.getClass());
					}
					break;
				}
				case ExportConstants.ITEM_TYPE_PARAGRAH:
				{
					if (item instanceof Paragraph)
					{
						this.exportItem((Paragraph) item);
					}
					else
					{
						throw new ExporterException(RM.getString("ERR_INVALID_CLASS_FOR_PARAGRAPH") //$NON-NLS-1$
								+ item.getClass());
					}
					break;
				}
				default:
				{
					throw new ExporterException(RM.getString("ERR_INVALID_ITEM_TYPE") + item.getType()); //$NON-NLS-1$
				}
			}
		}
	}

	// Methods to be implemented for external usage
	/**
	 * Method exportNode
	 * 
	 * @param nodeToExport
	 * @throws ExporterException
	 */
	public abstract void exportNode(AbstractExportableNode nodeToExport) throws ExporterException;

	// Methods to be implemented for internal usage
	protected abstract void exportItem(Group group) throws ExporterException;

	protected abstract void exportItem(Table table) throws ExporterException;

	protected abstract void exportItem(Chart chart) throws ExporterException;

	protected abstract void exportItem(Tree tree) throws ExporterException;

	protected abstract void exportItem(Label label) throws ExporterException;

	protected abstract void exportItem(TextBox textBox) throws ExporterException;

	protected abstract void exportItem(HTMLPage page) throws ExporterException;

	protected abstract void exportItem(TabFolder tabFolder) throws ExporterException;

	protected abstract void exportItem(LabelTextPanel panel) throws ExporterException;
	
	protected abstract void exportItem(Paragraph panel) throws ExporterException;

	protected abstract String getFileExtension();

	public void exportResult(String location, ArrayList models) throws ExporterException 
	{
		// do nothing
		
	}
}
