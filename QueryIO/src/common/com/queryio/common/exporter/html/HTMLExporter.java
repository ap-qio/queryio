/*
 * @(#)  HTMLExporter.java May 25, 2005
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
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT 
 * BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.exporter.html;

import java.io.File;
import java.util.ArrayList;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import com.queryio.common.ImageConstants;
import com.queryio.common.exporter.AbstractExporter;
import com.queryio.common.exporter.ExportConstants;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.Chart;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.Font;
import com.queryio.common.exporter.dstruct.Group;
import com.queryio.common.exporter.dstruct.HTMLPage;
import com.queryio.common.exporter.dstruct.IAbstractExportableTreeModel;
import com.queryio.common.exporter.dstruct.IAbstractExportableTreeNode;
import com.queryio.common.exporter.dstruct.IExportableItem;
import com.queryio.common.exporter.dstruct.ITotalModel;
import com.queryio.common.exporter.dstruct.Label;
import com.queryio.common.exporter.dstruct.LabelTextPanel;
import com.queryio.common.exporter.dstruct.Paragraph;
import com.queryio.common.exporter.dstruct.TabFolder;
import com.queryio.common.exporter.dstruct.Table;
import com.queryio.common.exporter.dstruct.TextBox;
import com.queryio.common.exporter.dstruct.Tree;
import com.queryio.common.exporter.exceptions.ExporterException;
import com.queryio.common.util.ServiceUtilities;
import com.queryio.common.util.StaticUtilities;

/**
 * HTMLExporter
 * 
 * @author AppPerfect Corp.
 * @version 10.0
 */
public final class HTMLExporter extends AbstractExporter implements HTMLConstants
{
	private static HTMLExporter thisInstance = null;
	private HTMLWriter writer = null;
	private String[] attrs, values;

	private String[] rootNodeViewPaths;

	/**
	 * 
	 */
	private HTMLExporter()
	{
		// Constructor has been made private to prevent external object instantiation of this class.
		// Use the getInstance() method in order to get an instance of this class.
	}
	/**
	 * @return
	 */
	public static HTMLExporter getNewInstance()
	{
		return new HTMLExporter();
	}
	/**
	 * @return
	 */
	public static HTMLExporter getInstance()
	{
		if (thisInstance == null)
		{
			thisInstance = getNewInstance();
		}
		return thisInstance;
	}
	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#getFileExtension()
	 */
	protected String getFileExtension()
	{
		return ExportConstants.getFileExtensionWithDot(ExportConstants.EXPORT_TYPE_HTML);
	}
	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportNode(com.queryio.sysmoncommon.exporter.dstruct.AbstractExportableNode)
	 */
	public void exportNode(final AbstractExportableNode nodeToExport) throws ExporterException
	{
		this.node = nodeToExport;
		if (this.writer == null)
		{
			this.writer = new HTMLWriter();
		}
		else
		{
			this.writer.reInitialize();
		}

		this.writer.startTag(HTML);
			this.writeHeadTag();
			this.startBodyTag();
		this.exportNodeItems();
		this.endBodyTag();
		this.writer.endTag(HTML);
		this.writer.writeFile(this.node.getFilePath(), this.node.getExportedFileName() + this.getFileExtension());
	}
	/**
	 * 
	 */
	private void writeHeadTag()
	{
		this.writer.startTag(HEAD);

		this.writer.startTag(TITLE);
		if (this.sCurrentProject != null)
		{
			this.writer.writeText(this.sCurrentProject);
		}
		else if (this.node.getHeaderText() != null)
		{
			this.writer.writeText(this.node.getHeaderText());
		}
		this.writer.endTag(TITLE);

		this.writer.startTag(SCRIPT, new String[] { LANGUAGE }, new String[] { LANGUAGE_JAVASCRIPT });
		if (this.bExportingResult)
		{
			this.writer.writePlainTextLine("var expandedNodesList = new Array();"); //$NON-NLS-1$
			this.writer.writePlainTextLine("var selectedNode = null;"); //$NON-NLS-1$
		}
		this.writer.writePlainTextLine("function changeTab(target, id)"); //$NON-NLS-1$
		this.writer.writePlainTextLine("{"); //$NON-NLS-1$
		this.writer.writePlainTextLine("\tdocument.getElementById(target).innerHTML = document.getElementById(id).innerHTML;"); //$NON-NLS-1$
		this.writer.writePlainTextLine("}"); //$NON-NLS-1$
		this.writer.endTag(SCRIPT);

		if (this.bExportingResult)
		{
			this.attrs = new String[] { SRC, LANGUAGE };
			this.values = new String[] { "js/tree.js", LANGUAGE_JAVASCRIPT }; //$NON-NLS-1$
			this.writer.startTag(SCRIPT, this.attrs, this.values);
			this.writer.endTag(SCRIPT);
			this.attrs = new String[] { SRC, LANGUAGE };
			this.values = new String[] { "js/encode.js", LANGUAGE_JAVASCRIPT }; //$NON-NLS-1$
			this.writer.startTag(SCRIPT, this.attrs, this.values);
			this.writer.endTag(SCRIPT);
			this.attrs = new String[] { TYPE, REL, HREF };
			this.values = new String[] { TYPE_TEXT_CSS, STYLESHEET, "css/tree.css" }; //$NON-NLS-1$
			this.writer.writeTag(LINK, this.attrs, this.values);
		}
		this.writer.startTag(STYLE, new String[] { TYPE }, new String[] { TYPE_TEXT_CSS });
		if (this.bExportingResult)
		{
			this.writer.writePlainTextLine("body { font-family: Tahoma, Arial, Helvetica, sans-serif; font-size: 75%; margin: 0; padding: 0; line-height: 1em; color: #000000; text-align: center; }"); //$NON-NLS-1$
			this.writer.writePlainTextLine(".bodyClass { position: relative; float: left; text-align: left; display: inline;  width: 100%; margin: 0; border-collapse: collapse; border: 1px solid #000000; }"); //$NON-NLS-1$
		}
		else
		{
			this.writer.writePlainTextLine("body { font-family: Tahoma, Arial, Helvetica, sans-serif; font-size: 75%; margin: 5px 5px 5px 5px; padding: 0 0 0 0; line-height: 1em; color: #000000; text-align: center; }"); //$NON-NLS-1$
			this.writer.writePlainTextLine(".bodyClass { position: relative; float: left; text-align: left; display: inline;  width: 100%; margin: 0; border-collapse: collapse; border: 0px solid #000000;}"); //$NON-NLS-1$
		}
		this.writer.writePlainTextLine("h1 { font-size: 3em; font-weight: normal; line-height: 1.3em; margin: 0 0 0.5em 0; padding: 0; }"); //$NON-NLS-1$
		this.writer.writePlainTextLine("h2 { font-size: 1.3em; font-weight: normal; line-height: 1.3em; margin: 0 0 0.5em 0; padding: 0; }"); //$NON-NLS-1$
		this.writer.writePlainTextLine("h6 { font-size: 1em; line-height: 1.3em; margin: 0 0 0.25em 0; padding: 0; }"); //$NON-NLS-1$

		this.writer.writePlainTextLine("table { font-size: 1em; margin: 0; padding: 0; border-collapse: collapse; border: 1px solid #a0a0a0; }"); //$NON-NLS-1$
		this.writer.writePlainTextLine("th { font-weight: bold; text-align: left; }"); //$NON-NLS-1$
		this.writer.writePlainTextLine("td { font-size: 0.9em; }"); //$NON-NLS-1$
		this.writer.writePlainTextLine(".table { color: #000; background: #fff; width: 100%; }"); //$NON-NLS-1$
		this.writer.writePlainTextLine(".table td, .table th { border: 1px solid #a0a0a0; padding: 0.5em; }"); //$NON-NLS-1$
		
		this.writer.writePlainTextLine(".rph { color: #000; background: #fff; width: 100%; border: 0px;}"); //$NON-NLS-1$
		this.writer.writePlainTextLine(".rph td, .rph th { border: 0px solid #a0a0a0; padding: 0.5em; }"); //$NON-NLS-1$
		
		this.writer.writePlainTextLine("a.tab { background-image:url('images/menu.png') ; color: #FFF; font-weight: bold; line-height: 18px; margin-rigth: 10px; padding: 2px 10px 2px 10px; text-decoration: none; border: 1px solid #a0a0a0; font-family:Tahoma, Arial, Helvetica, sans-serif;font-size: 8pt; }"); //$NON-NLS-1$
		this.writer.writePlainTextLine("a.tab:hover	{ color: #FFF; background-image:url('images/menu-hover.png'); font-family:Tahoma, Arial, Helvetica, sans-serif;font-size: 8pt;  }"); //$NON-NLS-1$
		this.writer.writePlainTextLine("a.tab:active { color: #FFF; background-image:url('images/menu-active.png'); font-family:Tahoma, Arial, Helvetica, sans-serif;font-size: 8pt;  }"); //$NON-NLS-1$
		this.writer.writePlainTextLine(".titleRow { background: url('images/chart-back.png');text-align: center; }"); //$NON-NLS-1$
		this.writer.writePlainTextLine(".headerRow { background: #ECEBEB  }"); //$NON-NLS-1$

		this.writer.endTag(STYLE);

		this.writer.endTag(HEAD);
	}
	/**
	 * 
	 */
	private void startBodyTag()
	{
		this.writer.startTag(BODY);

		if (!this.bExportingResult)
		{
			this.writeHeaderDiv();
		}
		this.writer.startTag(DIV, new String[] { CLASS }, new String[] { BODYCLASS });
		
//		if (!this.bExportingResult)
//		{
//			this.writer.startTag(FONT_BOLD);
//			this.writer.writeText("Date Generated: ");
//			this.writer.endTag(FONT_BOLD);
//			this.writer.writeText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date()));
//			this.writer.startTag("BR");
//			this.writer.startTag("BR");
//		}
	}
	/**
	 * 
	 */
	private void endBodyTag()
	{
		this.writer.endTag(DIV);
		this.writer.endTag(BODY);
	}
	/**
	 * 
	 */
	private void writeHeaderDiv()
	{
		this.writer.startTag(TABLE, new String[] { CLASS, WIDTH } , new String[] { "rph", "100%" });
		this.writer.startTag(TR);
		this.writer.startTag(TD, new String[] {ALIGN, WIDTH } , new String[] { "left", "75%" });
		this.writer.startTag(H1);
		if (!this.bExportingResult && (this.sCurrentProject != null) && (this.sCurrentProject.length() > 0))
		{
			this.writer.writeText(this.sCurrentProject + " : " + this.node.getHeaderText()); //$NON-NLS-1$
		}
		else
		{
			this.writer.writeText(this.node.getHeaderText());
		}
		this.writer.endTag(H1);
		this.writer.endTag(TD);
		this.writer.startTag(TD, new String[] {ALIGN, WIDTH } , new String[] { "right", "25%" });
		
		String imgSrc = "http://www.appperfect.com/images/ap.jpg";
		
		
		File imgFile = null;
		if (sHeaderImagePath != null)
		{
			try
			{
				imgFile = new File(sHeaderImagePath);
				if (imgFile.exists())
				{
					File dir = new File(this.node.getCompleteFileName(ExportConstants.EXPORT_TYPE_HTML)  + "_files");
					if (!dir.exists())
					{
						dir.mkdirs();
					}
					ServiceUtilities.copyNonUTFFile(sHeaderImagePath, dir.getAbsolutePath());
					imgSrc = dir.getName() + '/' + imgFile.getName();
				}
				else if (sHeaderImagePath.trim().length() > 0) // User has set it as some image link directly
				{
					imgSrc = sHeaderImagePath;
				}
			}
			catch (Exception ex)
			{
				// do nothing
			}
		}
		
		this.writer.writeTag(IMG, new String[] { SRC, HEIGHT }, new String[] {imgSrc,"80px"});
		this.writer.endTag(TD);
		this.writer.endTag(TR);
		this.writer.endTag(TABLE);
		
//		this.writer.startTag(H6);
//		this.writer.writeText(this.getDateString());
//		this.writer.endTag(H6);
		this.writer.startTag(HR);

		
		final String[] labels = this.node.getIdentifyingLabels();
		if (labels != null)
		{
			for (int i = 0; i < labels.length; i++)
			{
				this.attrs = new String[] { POSITION, FLOAT, WIDTH, HEIGHT, DISPLAY, BACKGROUND_COLOR };
				this.values = new String[] { POSITION_RELATIVE, FLOAT_LEFT, 100 + PERCT, 20 + PX, DISPLAY_HIDDEN,
						BGCOLOR_WHITE };

				String style = this.getStyleString(this.attrs, this.values);
				this.writer.startTag(DIV, new String[] { STYLE }, new String[] { style });
				this.writer.writeText(labels[i]);
				this.writer.endTag(DIV);
			}
		}
	}	
	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#reset()
	 */
	public void reset()
	{
		this.imgCount = 0;
	}
	/**
	 * @param location
	 * @param models
	 * @throws ExporterException
	 */
	public void exportResult(final String location, final ArrayList models) throws ExporterException
	{
		final int numTrees = models.size();
		this.rootNodeViewPaths = new String[numTrees];
		final String reportsLocation = location + File.separatorChar + "reports"; //$NON-NLS-1$
		if (numTrees > 1)
		{
			final ArrayList tabNames = new ArrayList();
			for (int i = 0; i < numTrees; i++)
			{
				final TreeModel model = (TreeModel) models.get(i);
				this.createTree(reportsLocation, model, "frame" + i, i); //$NON-NLS-1$
				tabNames.add(model.getRoot().toString());
			}
			this.createLeftPanel(reportsLocation);
			this.createTabsFrame(reportsLocation, (String[]) tabNames.toArray(new String[tabNames.size()]));
		}
		else if (numTrees == 1)
		{
			this.createTree(reportsLocation, (TreeModel) models.get(0), "treeFrame", 0); //$NON-NLS-1$
		}
		this.createMainFile(location);
	}
	/**
	 * @param location
	 * @param models
	 * @throws ExporterException
	 */
	public void exportProject(final String location, final ArrayList models) throws ExporterException
	{
		final int numTrees = models.size();
		this.rootNodeViewPaths = new String[numTrees];
		final String projectsLocation = location + File.separatorChar + "reports"; //$NON-NLS-1$
		if (numTrees > 1)
		{
			final ArrayList tabNames = new ArrayList();
			for (int i = 0; i < numTrees; i++)
			{
				final TreeModel model = (TreeModel) models.get(i);
				this.createTree(projectsLocation, model, "frame" + i, i); //$NON-NLS-1$
				tabNames.add(model.getRoot().toString());
			}
			this.createLeftPanel(projectsLocation);
			this.createTabsFrame(projectsLocation, (String[]) tabNames.toArray(new String[tabNames.size()]));
		}
		else if (numTrees == 1)
		{
			this.createTree(projectsLocation, (TreeModel) models.get(0), "treeFrame", 0); //$NON-NLS-1$
		}
		this.createMainProjectFile(location);
	}
	/**
	 * @param location
	 * @throws ExporterException
	 */
	private void createMainFile(final String location) throws ExporterException
	{
		if (this.writer == null)
		{
			this.writer = new HTMLWriter();
		}
		else
		{
			this.writer.reInitialize();
		}

		this.writer.startTag(HTML);
		this.writer.startTag(HEAD);

		this.writer.startTag(TITLE);
		this.writer.writeText(this.sCurrentProject);
		this.writer.endTag(TITLE);

		this.writer.endTag(HEAD);

		this.attrs = new String[] { COLS };
		this.values = new String[] { "225,*" }; //$NON-NLS-1$
		this.writer.startTag(FRAMESET, this.attrs, this.values);
		this.attrs = new String[] { NAME, SRC, MARGINWIDTH, MARGINHEIGHT, SCROLLING };
		this.values = new String[] { "treeFrame", "reports/treeFrame.html", "1", "1", DISPLAY_INLINE }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this.writer.writeTag(FRAME, this.attrs, this.values);
		this.attrs = new String[] { NAME, SRC, MARGINWIDTH, MARGINHEIGHT, SCROLLING };
		this.values = new String[] { "contents", "reports/" + this.rootNodeViewPaths[0], "1", "1", DISPLAY_INLINE }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this.writer.writeTag(FRAME, this.attrs, this.values);
		this.writer.endTag(FRAMESET);

		this.writer.endTag(HTML);

		this.writer.writeFile(location, "Result_" + this.sCurrentProject + this.getFileExtension()); //$NON-NLS-1$
	}
	
	private void createMainProjectFile(final String location) throws ExporterException
	{
		if (this.writer == null)
		{
			this.writer = new HTMLWriter();
		}
		else
		{
			this.writer.reInitialize();
		}

		this.writer.startTag(HTML);
		this.writer.startTag(HEAD);

		this.writer.startTag(TITLE);
		this.writer.writeText(this.sCurrentProject);
		this.writer.endTag(TITLE);

		this.writer.endTag(HEAD);

		this.attrs = new String[] { COLS };
		this.values = new String[] { "225,*" }; //$NON-NLS-1$
		this.writer.startTag(FRAMESET, this.attrs, this.values);
		this.attrs = new String[] { NAME, SRC, MARGINWIDTH, MARGINHEIGHT, SCROLLING };
		this.values = new String[] { "treeFrame", "reports/treeFrame.html", "1", "1", DISPLAY_INLINE }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this.writer.writeTag(FRAME, this.attrs, this.values);
		this.attrs = new String[] { NAME, SRC, MARGINWIDTH, MARGINHEIGHT, SCROLLING };
		this.values = new String[] { "contents", "reports/" + this.rootNodeViewPaths[0], "1", "1", DISPLAY_INLINE }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this.writer.writeTag(FRAME, this.attrs, this.values);
		this.writer.endTag(FRAMESET);

		this.writer.endTag(HTML);

		this.writer.writeFile(location, "Project_" + this.sCurrentProject + this.getFileExtension()); //$NON-NLS-1$
	}
	/**
	 * @param reportsLocation
	 * @param model
	 * @param frameName
	 * @param index
	 * @throws ExporterException
	 */
	private void createTree(final String reportsLocation, final TreeModel model, final String frameName, final int index)
			throws ExporterException
	{
		if (this.writer == null)
		{
			this.writer = new HTMLWriter();
		}
		else
		{
			this.writer.reInitialize();
		}

		this.writer.startTag(HTML);
		this.writer.startTag(HEAD);
		this.attrs = new String[] { LANGUAGE };
		this.values = new String[] { LANGUAGE_JAVASCRIPT };
		this.writer.startTag(SCRIPT, this.attrs, this.values);
		this.writer.writePlainTextLine("var expandedNodesList = new Array();"); //$NON-NLS-1$
		this.writer.writePlainTextLine("var selectedNode = null;"); //$NON-NLS-1$
		this.writer.endTag(SCRIPT);
		this.attrs = new String[] { SRC, LANGUAGE };
		this.values = new String[] { "js/tree.js", LANGUAGE_JAVASCRIPT }; //$NON-NLS-1$
		this.writer.startTag(SCRIPT, this.attrs, this.values);
		this.writer.endTag(SCRIPT);
		this.attrs = new String[] { SRC, LANGUAGE };
		this.values = new String[] { "js/encode.js", LANGUAGE_JAVASCRIPT }; //$NON-NLS-1$
		this.writer.startTag(SCRIPT, this.attrs, this.values);
		this.writer.endTag(SCRIPT);
		this.attrs = new String[] { TYPE, REL, HREF };
		this.values = new String[] { TYPE_TEXT_CSS, STYLESHEET, "css/tree.css" }; //$NON-NLS-1$
		this.writer.writeTag(LINK, this.attrs, this.values);

		this.writer.endTag(HEAD);
		this.writer.startTag(BODY);
		this.attrs = new String[] { ID };
		this.values = new String[] { "MainTreeSpan" }; //$NON-NLS-1$
		this.writer.startTag(SPAN, this.attrs, this.values);
		this.writer.endTag(SPAN);

		this.attrs = new String[] { LANGUAGE };
		this.values = new String[] { LANGUAGE_JAVASCRIPT };
		this.writer.startTag(SCRIPT, this.attrs, this.values);
		final AbstractExportableNode root = (AbstractExportableNode) model.getRoot();
		final String imageName = ImageConstants.getImageName(root.getImageID());
		final String rootNode = "node_" + root.getExportedFileName(); //$NON-NLS-1$
		final StringBuffer buff = new StringBuffer("var rootNode = new Node(null, '"); //$NON-NLS-1$
		buff.append(rootNode);
		buff.append("', '"); //$NON-NLS-1$
		buff.append(root.getDisplayName());
		buff.append("', '"); //$NON-NLS-1$
		if (root.getItems() != null)
		{
			this.rootNodeViewPaths[index] = root.getExportedFileName();
		}
		else
		{
			this.rootNodeViewPaths[index] = this.getNextNodeId(root, model);
		}
		this.rootNodeViewPaths[index] += this.getFileExtension();
		buff.append(this.rootNodeViewPaths[index]);

		buff.append("', '', 'images/"); //$NON-NLS-1$
		buff.append(imageName);
		if (!this.alImages.contains(imageName))
		{
			this.alImages.add(imageName);
		}
		buff.append("');"); //$NON-NLS-1$
		this.writer.writePlainTextLine(buff.toString());
		for (int i = 0; i < model.getChildCount(root); i++)
		{
			this.writeNode("node_" + root.getExportedFileName(), (AbstractExportableNode) root.getChildAt(i), model); //$NON-NLS-1$
		}
		this.writer.writePlainTextLine("var treeSpan = document.getElementById(\"MainTreeSpan\");"); //$NON-NLS-1$
		this.writer.writePlainTextLine("treeSpan.innerHTML = rootNode.toHtmlString();"); //$NON-NLS-1$
		buff.setLength(0);
		buff.append("requestFocusNodeWithoutReloading('"); //$NON-NLS-1$
		buff.append(rootNode);
		buff.append("');"); //$NON-NLS-1$
		this.writer.writePlainTextLine(buff.toString());
		this.writer.endTag(SCRIPT);

		this.writer.endTag(BODY);
		this.writer.endTag(HTML);

		this.writer.writeFile(reportsLocation, frameName + this.getFileExtension());

		this.exportModel(reportsLocation, model);
	}
	/**
	 * @param reportsLocation
	 * @param model
	 * @throws ExporterException
	 */
	private void exportModel(final String reportsLocation, final TreeModel model) throws ExporterException
	{
		this.exportTreeNode(reportsLocation, (AbstractExportableNode) model.getRoot(), model);
	}
	/**
	 * @param reportsLocation
	 * @param node
	 * @param model
	 * @throws ExporterException
	 */
	private void exportTreeNode(final String reportsLocation, final AbstractExportableNode node, final TreeModel model)
			throws ExporterException
	{
		synchronized (node)
		{
			final String originalPath = node.getFilePath();
			node.setFilePath(reportsLocation);
			this.exportNode(node);
			node.setFilePath(originalPath);
		}
		for (int i = 0; i < model.getChildCount(node); i++)
		{
			this.exportTreeNode(reportsLocation, (AbstractExportableNode) node.getChildAt(i), model);
		}
	}
	/**
	 * @param reportsLocation
	 * @throws ExporterException
	 */
	private void createLeftPanel(final String reportsLocation) throws ExporterException
	{
		if (this.writer == null)
		{
			this.writer = new HTMLWriter();
		}
		else
		{
			this.writer.reInitialize();
		}

		this.writer.startTag(HTML);

		this.attrs = new String[] { ROWS };
		this.values = new String[] { "*,35" }; //$NON-NLS-1$
		this.writer.startTag(FRAMESET, this.attrs, this.values);
		this.attrs = new String[] { NAME, SRC, MARGINWIDTH, MARGINHEIGHT, SCROLLING };
		this.values = new String[] { "tree", "frame0.html", "1", "1", DISPLAY_INLINE }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this.writer.writeTag(FRAME, this.attrs, this.values);
		this.attrs = new String[] { NAME, SRC, MARGINWIDTH, MARGINHEIGHT, SCROLLING };
		this.values = new String[] { "tabs", "treeTabs.html", "1", "1", DISPLAY_INLINE }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		this.writer.writeTag(FRAME, this.attrs, this.values);
		this.writer.endTag(FRAMESET);

		this.writer.endTag(HTML);

		this.writer.writeFile(reportsLocation, "treeFrame.html"); //$NON-NLS-1$
	}
	/**
	 * @param reportsLocation
	 * @param tabNames
	 * @throws ExporterException
	 */
	private void createTabsFrame(final String reportsLocation, final String[] tabNames) throws ExporterException
	{
		if (this.writer == null)
		{
			this.writer = new HTMLWriter();
		}
		else
		{
			this.writer.reInitialize();
		}

		this.writer.startTag(HTML);
		this.writer.startTag(HEAD);
			this.writer.startTag(SCRIPT, new String[] { SRC, LANGUAGE }, new String[] { "js/tabs.js", LANGUAGE_JAVASCRIPT });//$NON-NLS-1$
			this.writer.endTag(SCRIPT);
			this.writer.writeTag(LINK, new String[] { TYPE, REL, HREF }, new String[] { TYPE_TEXT_CSS, STYLESHEET, "css/tree.css" }); //$NON-NLS-1$
		this.writer.endTag(HEAD);
		this.writer.startTag(BODY);

		final StringBuffer buff = new StringBuffer();
		for (int i = 0; i < tabNames.length; i++)
		{
			buff.setLength(0);
			buff.append("javascript:loadURLInTarget('a"); //$NON-NLS-1$
			buff.append(i);
			buff.append("', 'frame"); //$NON-NLS-1$
			buff.append(i);
			buff.append(this.getFileExtension());
			buff.append("', '");//$NON-NLS-1$
			buff.append(this.rootNodeViewPaths[i]);
			buff.append("');"); //$NON-NLS-1$
			this.attrs = new String[] { ID, CLASS, HREF };
			if (i == 0)
			{
				this.values = new String[] { "a" + i, "activeTab", buff.toString() }; //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				this.values = new String[] { "a" + i, "tab", buff.toString() }; //$NON-NLS-1$ //$NON-NLS-2$
			}
			this.writer.startTag(A, this.attrs, this.values);
			this.writer.writeText(tabNames[i]);
			this.writer.endTag(A);
		}
		this.writer.endTag(BODY);
		this.writer.endTag(HTML);

		this.writer.writeFile(reportsLocation, "treeTabs.html"); //$NON-NLS-1$
	}
	/**
	 * @param parent
	 * @param node
	 * @param model
	 */
	private void writeNode(final String parent, final AbstractExportableNode node, final TreeModel model)
	{
		final String imageName = ImageConstants.getImageName(node.getImageID());
		final String fName = StaticUtilities.jsEncode(node.getExportedFileName());
		final StringBuffer buff = new StringBuffer("new Node('"); //$NON-NLS-1$
		buff.append(parent);
		buff.append("', 'node_"); //$NON-NLS-1$
		buff.append(fName);
		buff.append("', '"); //$NON-NLS-1$
		buff.append(StaticUtilities.jsEncode(node.getDisplayName()));
		buff.append("', '"); //$NON-NLS-1$

		if ((node.getHeaderText() != null) || (node.getIdentifyingLabels() != null))
		{
			buff.append(fName);
		}
		else
		{
			buff.append(StaticUtilities.jsEncode(this.getNextNodeId(node, model)));
		}
		buff.append(this.getFileExtension());
		buff.append("', '', 'images/"); //$NON-NLS-1$
		buff.append(imageName);
		if (!this.alImages.contains(imageName))
		{
			this.alImages.add(imageName);
		}
		buff.append("');"); //$NON-NLS-1$
		this.writer.writePlainTextLine(buff.toString());
		for (int i = 0; i < model.getChildCount(node); i++)
		{
			this.writeNode("node_" + fName, (AbstractExportableNode) node.getChildAt(i), model); //$NON-NLS-1$
		}
	}
	/**
	 * @param node
	 * @param model
	 * @return
	 */
	private String getNextNodeId(final AbstractExportableNode node, final TreeModel model)
	{
		if (node != null)
		{
			AbstractExportableNode next = (AbstractExportableNode) node.getNextSibling();
			if (next != null)
			{
				return next.getExportedFileName();
			}
			if (model.getChildCount(node) != 0)
			{
				next = (AbstractExportableNode) node.getChildAt(0);
				if (next != null)
				{
					return next.getExportedFileName();
				}
			}
		}
		return null;
	}
	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Group)
	 */
	protected void exportItem(final Group group) throws ExporterException
	{
		String style = this.getStyleString(new String[] { POSITION, FLOAT, WIDTH, BORDER }, 
				new String[] { POSITION_RELATIVE, FLOAT_LEFT, group.getWidth() + PERCT, BORDER_BLACK });
		this.writer.startTag(DIV, new String[] { STYLE }, new String[] { style });
			this.writer.startTag(H6);
			this.writer.writeText(group.getHeading());
			this.writer.endTag(H6);
			this.exportItems(group.getItems());
		this.writer.endTag(DIV);
	}
	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Table)
	 */
	protected void exportItem(final Table table)
	{
//		if (table.getTableHeader() != null)
//			{
//				this.writer.startTag(PARAGRAPH);
//				this.writer.startTag(H2);
//				this.writer.writeText(table.getTableHeader());
//				this.writer.endTag(H2);
//				this.writer.endTag(PARAGRAPH);
//			}
			String style = this.getStyleString(new String[] { POSITION, FLOAT, WIDTH, "WORD-BREAK", MARGIN }, 
					new String[] { POSITION_RELATIVE, FLOAT_LEFT, table.getWidth() + PERCT, "BREAK-ALL", "20 0 10 10" });
				
			this.writer.startTag(DIV, new String[] { STYLE }, new String[] { style });
			final int[] colWidths = table.getColumnWidths();
			final TableModel model = table.getModel();
			if (model != null)
			{
				final int numCols = model.getColumnCount();
				final int numRows = model.getRowCount();

				this.writer.startTag(TABLE, new String[] { CLASS } , new String[] { TABLE });
//				this.writer.startTag("THEAD"); //$NON-NLS-1$
				if (table.getTableHeader() != null)
				{
					this.writer.startTag(TR,new String[]{CLASS, STYLE},new String[]{TITLE_ROW, "height: 20px;"});
					this.writer.startTag(TD, new String[] {  STYLE,COL_SPAN  }, new String[] {"text-align: center;", numCols+"" });
					this.writer.startTag("b");
					this.writer.writeText(table.getTableHeader());
					this.writer.endTag("b");
					this.writer.endTag(TD);
					this.writer.endTag(TR);
					this.writer.startTag(TR ,new String[]{CLASS},new String[]{HEADERROW});
				}
				int colspan = 0;
				for (int i = 0; i < numCols; i++)
				{
					if ((colWidths.length > i) && (colWidths[i] > 0))
					{
						colspan ++;
						this.writer.startTag(TD, new String[] { WIDTH, ALIGN }, new String[] { colWidths[i] + PERCT, LEFT }); //$NON-NLS-1$
						this.writer.writeText(model.getColumnName(i));
						this.writer.endTag(TD);
					}
				}
				this.writer.endTag(TR);
//				this.writer.endTag("THEAD"); //$NON-NLS-1$
	
			this.writer.startTag("TBODY"); //$NON-NLS-1$
			for (int i = 0; i < numRows; i++)
			{
//				if (!table.isShowAlternateColor() || (i % 2 == 0))
//				{
//					//bgcolor="#F0F0F0"
					this.writer.startTag(TR);
//				}
//				else
//				{
//					this.writer.startTag(TR, new String[] { "bgcolor"}, new String[] { "#F0F0F0"});
//				}
				for (int j = 0, k = 0; j < numCols; j++, ++k)
				{
					if (k < colWidths.length)
					{
						if (colWidths[k] <= 0)
						{
							continue;
						}
					}
					final Object val = model.getValueAt(i, j);
					if (val != null && val instanceof Color)
					{
						this.writer.startTag(TD, new String[] { "bgcolor"}, new String[] { "#" + Color.toAWTColorString((Color)val)});
					}
					else
					{
						this.writer.startTag(TD);
					}
					final StringBuffer value = new StringBuffer();
					final StringBuffer spaceValue = new StringBuffer();
					if (val != null)
					{
						final String sVal = val.toString();
						if (sVal == null || sVal.trim().length() == 0 || val instanceof Color)
						{
							spaceValue.append(HTML_SPACE);
						}
						else
						{
							value.append(sVal);
							for (int index = 0; index < value.length(); index++)
							{
								if (value.charAt(index) != ' ')
								{
									break;
								}
								spaceValue.append(HTML_SPACE);
								value.deleteCharAt(index);
								index--;
							}
						}
					}
					else
					{
						spaceValue.append(HTML_SPACE);
					}
					this.writer.writePlainTextLine(spaceValue.toString(), false);
					this.writer.writeText(value.toString());
					this.writer.endTag(TD);
				}
				this.writer.endTag(TR);
			}
			if (table.isTotal())
			{
				final ITotalModel totalModel = (ITotalModel) model;
				this.writer.startTag(TR);
				for (int j = 0, k = 0; j < numCols; j++, ++k)
				{
					if (k < colWidths.length)
					{
						if (colWidths[j] <= 0)
						{
							continue;
						}
					}
					this.writer.startTag(TD);
					final Object val = totalModel.getValueForCol(j);
					this.writer.writeText((val == null) || (val.toString().trim().length() == 0) ? " " : val.toString()); //$NON-NLS-1$
					this.writer.endTag(TD);
				}
				this.writer.endTag(TR);
			}
			
			if (numRows == 0 && !table.isTotal())
			{
				this.writer.startTag(TR);
				this.writer.startTag(TD, new String [] {"colspan"}, new String [] {String.valueOf(colspan)});
				this.writer.writeText("No data found"); //$NON-NLS-1$
				this.writer.endTag(TD);
				this.writer.endTag(TR);
			}
			
			this.writer.endTag("TBODY"); //$NON-NLS-1$
			this.writer.endTag(TABLE);
		}
		this.writer.endTag(DIV);
	}
	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Label)
	 */
	protected void exportItem(final Label label)
	{
		this.writer.startTag(DIV, new String[] { STYLE }, new String[] { this.getStyleString(new String[] { POSITION, FLOAT, WIDTH }, 
			new String[] { POSITION_RELATIVE, FLOAT_LEFT, label.getWidth() + PERCT }) });
		boolean bold = (label.getFontStyle() & Font.STYLE_BOLD) == Font.STYLE_BOLD;
		boolean italic = (label.getFontStyle() & Font.STYLE_ITALIC) == Font.STYLE_ITALIC;
		if (bold)
		{
			this.writer.startTag(FONT_BOLD);
		}
		if (italic)
		{
			this.writer.startTag(FONT_ITALIC);
		}
		this.writer.writeText(label.getText());
		if (italic)
		{
			this.writer.endTag(FONT_ITALIC);
		}
		if (bold)
		{
			this.writer.endTag(FONT_BOLD);
		}
		this.writer.endTag(DIV);
	}
	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.TextBox)
	 */
	protected void exportItem(final TextBox textBox)
	{
		this.writer.startTag(DIV, new String[] { STYLE }, new String[] { this.getStyleString(new String[] { POSITION, FLOAT, WIDTH }, 
				new String[] { POSITION_RELATIVE, FLOAT_LEFT, textBox.getWidth() + PERCT }) });
			this.writer.writeTag(INPUT, new String[] { TYPE, VALUE, READONLY }, new String[] { TEXT, textBox.getText() });
		this.writer.endTag(DIV);
	}
	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Chart)
	 */
	protected void exportItem(final Chart chart) throws ExporterException
	{
		String style = this.getStyleString(new String[] { POSITION, FLOAT, WIDTH, HEIGHT, BORDER, MARGIN}, 
				new String[] { POSITION_RELATIVE, FLOAT_LEFT, chart.getWidth() + PERCT, chart.getHeight() * this.iHeight / 100 + PX, BORDER_GRAY, "20 0 10 10" });
		this.writer.startTag(DIV, new String[] { STYLE }, new String[] { style });
			this.writer.writeTag(IMG, new String[] { SRC }, new String[] { chart.getFileName(this.node.getCompleteFileName(ExportConstants.EXPORT_TYPE_HTML) + "_files", //$NON-NLS-1$
					chart.getWidth() * this.iWidth / 100, chart.getHeight() * this.iHeight / 100, this.imgCount++) });
		this.writer.endTag(DIV);
	}
	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Tree)
	 */
	protected void exportItem(final Tree tree)
	{
		if (tree.getModel() instanceof IAbstractExportableTreeModel)
		{
			this.exportTree(tree);
		}
		else
		{
			this.exportTreeTable(tree);
		}
	}

	private void exportTree(final Tree tree)
	{
		String style = this.getStyleString(new String[] { POSITION, FLOAT, WIDTH, HEIGHT }, 
				new String[] { POSITION_RELATIVE, FLOAT_LEFT, tree.getWidth() + PERCT, tree.getHeight() * this.iHeight / 100 + PX });
		this.writer.startTag(DIV, new String[] { STYLE }, new String[] { style });

		final TreeModel model = tree.getModel();
		final TreeNode root = (TreeNode) model.getRoot();
		this.writer.startTag(TABLE, new String[] { CLASS }, new String[] { TABLE });
		if (root != null)
		{
			this.writeTreeNode(root, 0);
		}
		this.writer.endTag(TABLE);
		this.writer.endTag(DIV);
	}
	/**
	 * @param node
	 * @param iLevel
	 */
	private void writeTreeNode(final TreeNode node, final int iLevel)
	{
		if (node != null)
		{
			this.writer.startTag(TR);
			this.writer.startTag(TD);
			// Add the node as a string with leading spaces for levels
			final StringBuffer sbBuff = new StringBuffer();
			for (int i = 0; i < iLevel; i++)
			{
				sbBuff.append("    "); //$NON-NLS-1$
			}
			if (node instanceof IAbstractExportableTreeNode)
			{
				sbBuff.append(((IAbstractExportableTreeNode) node).toExportString());
			}
			else
			{
				sbBuff.append(node.toString());
			}

			this.writer.writeText(sbBuff.toString());
			this.writer.endTag(TD);
			this.writer.endTag(TR);

			// Add all the child nodes of this node
			final int iChildCount = node.getChildCount();
			if (iChildCount > 0)
			{
				for (int i = 0; i < iChildCount; i++)
				{
					this.writeTreeNode(node.getChildAt(i), iLevel + 1);
				}
			}
		}
	}
	/**
	 * @param tree
	 */
	private void exportTreeTable(final Tree tree)
	{
		String style = this.getStyleString(new String[] { POSITION, FLOAT, WIDTH, HEIGHT }, 
				new String[] { POSITION_RELATIVE, FLOAT_LEFT, tree.getWidth() + PERCT, tree.getHeight() * this.iHeight / 100 + PX });
		this.writer.startTag(DIV, new String[] { STYLE }, new String[] { style });

		final TableModel model = this.convertTreeModelToTableModel(tree.getModel());
		final int numRows = model.getRowCount();
		
		this.writer.startTag(TABLE, new String[] { CLASS }, new String[] { TABLE });
		for (int i = 0; i < numRows; i++)
		{
			this.writer.startTag(TR);
				this.writer.startTag(TD, new String[] { WIDTH }, new String[] { FILL_100 });
					this.writer.writeText(model.getValueAt(i, 0).toString());
				this.writer.endTag(TD);
			this.writer.endTag(TR);
		}
		this.writer.endTag(TABLE);
		this.writer.endTag(DIV);
	}
	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.HTMLPage)
	 */
	protected void exportItem(final HTMLPage page)
	{
		this.attrs = new String[] { POSITION, FLOAT, WIDTH, HEIGHT, DISPLAY, BORDER };
		this.values = new String[] { POSITION_RELATIVE, FLOAT_LEFT, page.getWidth() + PERCT,
				page.getHeight() * this.iHeight / 100 + PX, DISPLAY_INLINE, BORDER_BLACK };

		final String style = this.getStyleString(this.attrs, this.values);
		this.attrs = new String[] { STYLE };
		this.values = new String[] { style };
		this.writer.startTag(DIV, this.attrs, this.values);

		this.attrs = new String[] { SRC, WIDTH, HEIGHT, FRAMEBORDER };
		this.values = new String[] { page.getFileName(), FILL_100, FILL_100, DEF_FRAMEBORDER };
		this.writer.startTag(IFRAME, this.attrs, this.values);
		this.writer.writeText("IFrames Not Supported."); //$NON-NLS-1$
		this.writer.endTag(IFRAME);

		this.writer.endTag(DIV);
	}
	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.TabFolder)
	 */
	protected void exportItem(final TabFolder tabFolder) throws ExporterException
	{
		final IExportableItem[][] tabs = tabFolder.getItems();
		final String[] tabNames = tabFolder.getTabNames();
		int numTabs = 0;
		if (tabNames != null && tabNames.length > 0)
		{
			numTabs = tabNames.length;
			final String targetId = tabFolder.getId() + "_tabs"; //$NON-NLS-1$
		
			this.writer.startTag(DIV);
			for (int i = 0; i < numTabs; i++)
			{
				this.attrs = new String[] { HREF, CLASS, ONCLICK };
				this.values = new String[] {"#", "tab", "javascript:changeTab('" + targetId + "', '" + tabFolder.getId() + '_' + tabNames[i] + "');" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				
				this.writer.startTag(A, this.attrs, this.values);
					this.writer.writeText(tabNames[i]);
				this.writer.endTag(A);
			}
			this.writer.endTag(DIV);

			String style = this.getStyleString(new String[] { WIDTH, DISPLAY }, new String[] { tabFolder.getWidth() + PERCT, DISPLAY_INLINE });
			this.writer.startTag(DIV, new String[] { ID, STYLE }, new String[] { targetId, style });
			this.writer.endTag(DIV);
			
			for (int i = 0; i < numTabs; i++)
			{
				this.attrs = new String[] { WIDTH, DISPLAY, BORDER_TOP };
				this.values = new String[] { 0 + PERCT, DISPLAY_HIDDEN, BORDER_BLACK };

				style = this.getStyleString(this.attrs, this.values);
				this.attrs = new String[] { ID, STYLE };
				this.values = new String[] { tabFolder.getId() + '_' + tabNames[i], style };
				this.writer.startTag(DIV, this.attrs, this.values);
				this.exportItems(tabs[i]);
				this.writer.endTag(DIV);
			}
			this.attrs = new String[] { LANGUAGE };
			this.values = new String[] { LANGUAGE_JAVASCRIPT };
			this.writer.startTag(SCRIPT, this.attrs, this.values);
			this.writer.writePlainTextLine("changeTab('" + targetId + "', '" + tabFolder.getId() + '_' + tabNames[0] + "');"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			this.writer.endTag(SCRIPT);
		}
	}
	
	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.LabelTextPanel)
	 */
	protected void exportItem(final LabelTextPanel panel) throws ExporterException
	{
		final IExportableItem[][] items = panel.getItems();
		for (int i = 0; i < items.length; i++)
		{
			for (int j = 0; j < items[i].length; j++)
			{
				this.exportItem(items[i][j]);
			}
		}
	}

	/**
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Paragraph)
	 */
	protected void exportItem(final Paragraph para) throws ExporterException
	{
		if (!para.isUseNewLineInsteadOfPargraph())
		{
			this.writer.startTag(PARAGRAPH);
		}
		final Label [] items = para.getItems();
		for (int i = 0; i < items.length; i++)
		{
			boolean bold = (items[i].getFontStyle() & Font.STYLE_BOLD) == Font.STYLE_BOLD;
			boolean italic = (items[i].getFontStyle() & Font.STYLE_ITALIC) == Font.STYLE_ITALIC;
			if (bold)
			{
				this.writer.startTag(FONT_BOLD);
			}
			if (italic)
			{
				this.writer.startTag(FONT_ITALIC);
			}
			this.writer.writeText(items[i].getText());
			if (italic)
			{
				this.writer.endTag(FONT_ITALIC);
			}
			if (bold)
			{
				this.writer.endTag(FONT_BOLD);
			}
		}
		if (para.isUseNewLineInsteadOfPargraph())
		{
			this.writer.startTag("BR");
		}
		else
		{
			this.writer.endTag(PARAGRAPH);
		}
	}
	
	/**
	 * @param styleAttrs
	 * @param styleValues
	 * @return
	 */
	private String getStyleString(final String[] styleAttrs, final String[] styleValues)
	{
		final StringBuffer sbTemp = new StringBuffer();
		if ((styleAttrs != null) && (styleAttrs.length > 0) && (styleValues != null) && (styleValues.length > 0))
		{
			for (int i = 0; i < styleAttrs.length; i++)
			{
				sbTemp.append(SPACE);
				sbTemp.append(styleAttrs[i]);
				sbTemp.append(COLON);
				sbTemp.append(SPACE);
				sbTemp.append(styleValues[i]);
				sbTemp.append(SEMICOLON);
			}
		}
		return sbTemp.toString();
	}
}
/*
	private void writeAllDivs(final TreeModel model) throws ExporterException
	{
		final AbstractExportableNode root = (AbstractExportableNode) model.getRoot();
		if (root.getItems() != null)
		{
			this.writeNodeDiv(root, model);
		}
		else
		{
			for (int i = 0; i < model.getChildCount(root); i++)
			{
				this.writeNodeDiv((AbstractExportableNode) root.getChildAt(i), model);
			}
		}
	}

	private void writeNodeDiv(final AbstractExportableNode node, final TreeModel model) throws ExporterException
	{
		this.node = node;
		final String style = this.getStyleString(new String[] { POSITION, FLOAT, WIDTH, HEIGHT }, 
				new String[] { POSITION_RELATIVE, FLOAT_LEFT, 0 + PERCT, 0 + PX });
		this.attrs = new String[] { ID, STYLE };
		this.values = new String[] { "view_" + node.getExportedFileName(), style }; //$NON-NLS-1$
		this.writer.startTag(DIV, this.attrs, this.values);
		this.exportNodeItems();
		this.writer.endTag(DIV);

		for (int i = 0; i < model.getChildCount(node); i++)
		{
			this.writeNodeDiv((AbstractExportableNode) node.getChildAt(i), model);
		}
	}
	private void writeTree(final TreeModel model, final int index)
	{
		final AbstractExportableNode root = (AbstractExportableNode) model.getRoot();
		final String imageName = ImageConstants.getImageName(root.getImageID());
		final String fName = StaticUtilities.jsEncode(root.getExportedFileName());
		final StringBuffer buff = new StringBuffer("var rootNode"); //$NON-NLS-1$
		buff.append(index);
		buff.append(" = new Node(null, 'node_"); //$NON-NLS-1$
		buff.append(fName);
		buff.append("', '"); //$NON-NLS-1$
		buff.append(StaticUtilities.jsEncode(root.getDisplayName()));
		buff.append("', 'view_"); //$NON-NLS-1$
		if (root.getItems() != null)
		{
			buff.append(fName);
		}
		else
		{
			buff.append(StaticUtilities.jsEncode(this.getNextNodeId(root, model)));
		}
		buff.append("', '', 'images/"); //$NON-NLS-1$
		buff.append(imageName);
		if (!this.alImages.contains(imageName))
		{
			this.alImages.add(imageName);
		}
		buff.append("');"); //$NON-NLS-1$
		this.writer.writePlainTextLine(buff.toString());
		for (int i = 0; i < model.getChildCount(root); i++)
		{
			this.writeNode("node_" + fName, (AbstractExportableNode) root.getChildAt(i), model); //$NON-NLS-1$
		}
	}


public void exportResult_Old(final String location, final ArrayList models) throws ExporterException
{
	final int numTrees = models.size();
	if (numTrees > 1)
	{
		*
		 * attrs = new String[] { POSITION, LEFT, TOP, WIDTH, HEIGHT,
		 * OVERFLOW, BORDER, BACKGROUND_COLOR }; values = new String[] {
		 * DEF_POSITION, 0 + PX, 0 + PX, WIDTH_LEFT * iWidth / 100 + PX,
		 * iHeight - 25 + PX, AUTO, DEF_BORDER, BGCOLOR_WHITE };
		 *
		this.attrs = new String[] { POSITION, FLOAT, WIDTH, HEIGHT, OVERFLOW, BORDER, BACKGROUND_COLOR };
		this.values = new String[] { POSITION_RELATIVE, FLOAT_LEFT, WIDTH_LEFT + PERCT, this.iHeight - 25 + PX, DISPLAY_INLINE,
				DEF_BORDER, BGCOLOR_WHITE };
		String style = this.getStyleString(this.attrs, this.values);
		this.attrs = new String[] { ID, STYLE };
		this.values = new String[] { "MainTreeSpan", style }; //$NON-NLS-1$
		this.writer.startTag(DIV, this.attrs, this.values);
		this.writer.endTag(DIV);
		 *
		 * attrs = new String[] { POSITION, LEFT, TOP, WIDTH, HEIGHT,
		 * OVERFLOW, BORDER }; values = new String[] { DEF_POSITION, 0 + PX,
		 * iHeight - 25 + PX, WIDTH_LEFT * iWidth / 100 + PX, 25 + PX, AUTO,
		 * DEF_BORDER };
		 * 
		this.attrs = new String[] { POSITION, FLOAT, WIDTH, HEIGHT, OVERFLOW, BORDER };
		this.values = new String[] { POSITION_RELATIVE, FLOAT_LEFT, WIDTH_LEFT + PERCT, 25 + PX, DISPLAY_INLINE, DEF_BORDER };
		style = this.getStyleString(this.attrs, this.values);
		this.attrs = new String[] { STYLE };
		this.values = new String[] { style };
		this.writer.startTag(DIV, this.attrs, this.values);
		for (int i = 0; i < numTrees; i++)
		{
			this.attrs = new String[] { HREF, ONCLICK };
			this.values = new String[] { "#", "javascript:changeTab('MainTreeSpan', 'tree" + i + "');" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			this.writer.startTag(A, this.attrs, this.values);
			this.writer.writeText(((TreeModel) models.get(i)).getRoot().toString() + ' ');
			this.writer.endTag(A);
		}
		this.writer.endTag(DIV);
		for (int i = 0; i < numTrees; i++)
		{
			 *
			 * attrs = new String[] { POSITION, LEFT, TOP, WIDTH, HEIGHT,
			 * OVERFLOW }; values = new String[] { DEF_POSITION, 0 + PX, 0 +
			 * PX, 0 + PX, 0 + PX, DEF_OVERFLOW };
			 *
			this.attrs = new String[] { POSITION, FLOAT, WIDTH, HEIGHT, OVERFLOW };
			this.values = new String[] { POSITION_RELATIVE, FLOAT_LEFT, 0 + PERCT, 0 + PX, DISPLAY_HIDDEN };
			style = this.getStyleString(this.attrs, this.values);
			this.attrs = new String[] { ID, STYLE };
			this.values = new String[] { "tree" + i, style }; //$NON-NLS-1$
			this.writer.startTag(DIV, this.attrs, this.values);
			this.writer.endTag(DIV);
		}

		this.attrs = new String[] { LANGUAGE };
		this.values = new String[] { LANGUAGE_JAVASCRIPT };
		this.writer.startTag(SCRIPT, this.attrs, this.values);
		for (int i = 0; i < numTrees; i++)
		{
			this.writeTree((TreeModel) models.get(i), i);
			this.writer
					.writePlainTextLine("document.getElementById('tree" + i + "').innerHTML=rootNode" + i + ".toHtmlString();"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		this.writer.writePlainTextLine("changeTab('MainTreeSpan', 'tree0');"); //$NON-NLS-1$
		this.writer.endTag(SCRIPT);
		 *
		 * attrs = new String[] { POSITION, LEFT, TOP, WIDTH, HEIGHT,
		 * OVERFLOW, BORDER }; values = new String[] { DEF_POSITION,
		 * WIDTH_LEFT * iWidth / 100 + PX, 0 + PX, WIDTH_RIGHT * iWidth /
		 * 100 + PX, iHeight + PX, AUTO, DEF_BORDER };
		 *
		this.attrs = new String[] { POSITION, FLOAT, WIDTH, HEIGHT, OVERFLOW, BORDER };
		this.values = new String[] { POSITION_RELATIVE, FLOAT_LEFT, WIDTH_RIGHT + PERCT, this.iHeight + PX, DISPLAY_INLINE,
				DEF_BORDER };
		style = this.getStyleString(this.attrs, this.values);
		this.attrs = new String[] { ID, STYLE };
		this.values = new String[] { "RightView", style }; //$NON-NLS-1$
		this.writer.startTag(DIV, this.attrs, this.values);
		this.writer.endTag(DIV);

		this.iWidth = WIDTH_RIGHT * this.iWidth / 100;
		for (int i = 0; i < numTrees; i++)
		{
			this.writeAllDivs((TreeModel) models.get(i));
		}
	}
	else
	{
		 *
		 * attrs = new String[] { POSITION, LEFT, TOP, WIDTH, HEIGHT,
		 * OVERFLOW, BORDER }; values = new String[] { DEF_POSITION, 0 + PX,
		 * 0 + PX, WIDTH_LEFT * iWidth / 100 + PX, iHeight + PX, AUTO,
		 * DEF_BORDER };
		 *
		this.attrs = new String[] { POSITION, FLOAT, WIDTH, HEIGHT, OVERFLOW, BORDER };
		this.values = new String[] { POSITION_RELATIVE, FLOAT_LEFT, WIDTH_RIGHT + PERCT, this.iHeight + PX, DISPLAY_INLINE,
				DEF_BORDER };
		String style = this.getStyleString(this.attrs, this.values);
		this.attrs = new String[] { ID, STYLE };
		this.values = new String[] { "MainTreeSpan", style }; //$NON-NLS-1$
		this.writer.startTag(DIV, this.attrs, this.values);
		this.writer.endTag(DIV);
		this.attrs = new String[] { LANGUAGE };
		this.values = new String[] { LANGUAGE_JAVASCRIPT };
		this.writer.startTag(SCRIPT, this.attrs, this.values);
		this.writeTree((TreeModel) models.get(0), 0);
		this.writer.writePlainTextLine("var treeSpan = document.getElementById('MainTreeSpan');"); //$NON-NLS-1$
		this.writer.writePlainTextLine("treeSpan.innerHTML = rootNode0.toHtmlString();"); //$NON-NLS-1$
		this.writer.endTag(SCRIPT);

		 *
		 * attrs = new String[] { POSITION, FLOAT, WIDTH, HEIGHT, OVERFLOW,
		 * BORDER }; values = new String[] { DEF_POSITION, WIDTH_LEFT *
		 * iWidth / 100 + PX, 0 + PX, WIDTH_RIGHT * iWidth / 100 + PX,
		 * iHeight + PX, AUTO, DEF_BORDER };
		 *
		this.attrs = new String[] { POSITION, FLOAT, WIDTH, HEIGHT, OVERFLOW, BORDER };
		this.values = new String[] { POSITION_RELATIVE, FLOAT_LEFT, WIDTH_RIGHT + PERCT, this.iHeight + PX, DISPLAY_INLINE,
				DEF_BORDER };
		style = this.getStyleString(this.attrs, this.values);
		this.attrs = new String[] { ID, STYLE };
		this.values = new String[] { "RightView", style }; //$NON-NLS-1$
		this.writer.startTag(DIV, this.attrs, this.values);
		this.writer.endTag(DIV);

		this.iWidth = WIDTH_RIGHT * this.iWidth / 100;
		this.writeAllDivs((TreeModel) models.get(0));
	}

	this.endBodyTag();
	this.writer.endTag(HTML);
	this.writer.writeFile(location, "Result_" + this.sCurrentProject + this.getFileExtension()); //$NON-NLS-1$
}
*/