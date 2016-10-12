/*
 * @(#)  PDFExporter.java May 25, 2005
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
package com.queryio.common.exporter.pdf;

import java.awt.Graphics2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.queryio.common.IProductConstants;
import com.queryio.common.charts.independent.IScrollableChart;
import com.queryio.common.charts.swinggraphics.SWINGUserInterface;
import com.queryio.common.exporter.AbstractExporter;
import com.queryio.common.exporter.ExportConstants;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.Chart;
import com.queryio.common.exporter.dstruct.Color;
import com.queryio.common.exporter.dstruct.ExportableItem;
import com.queryio.common.exporter.dstruct.Group;
import com.queryio.common.exporter.dstruct.HTMLPage;
import com.queryio.common.exporter.dstruct.IAbstractExportableTreeModel;
import com.queryio.common.exporter.dstruct.IAbstractExportableTreeNode;
import com.queryio.common.exporter.dstruct.IExportableItem;
import com.queryio.common.exporter.dstruct.ITotalModel;
import com.queryio.common.exporter.dstruct.Label;
import com.queryio.common.exporter.dstruct.LabelTextPanel;
import com.queryio.common.exporter.dstruct.ReportHeader;
import com.queryio.common.exporter.dstruct.TabFolder;
import com.queryio.common.exporter.dstruct.Table;
import com.queryio.common.exporter.dstruct.TextBox;
import com.queryio.common.exporter.dstruct.Tree;
import com.queryio.common.exporter.exceptions.ExporterException;


/**
 * Add comments for class here.
 * 
 * @author Exceed Consultancy Services
 * @version 1.0
 */
public final class PDFExporter extends AbstractExporter
{
	private static PDFExporter thisInstance = null;
	private Document document = null;
	private PdfWriter writer = null;
	private int iOriginalHeight = 0;
	private int iCurrentPos = 0;

	private static final Font DEF_FONT = FontFactory.getFont("Tahoma", 10); //$NON-NLS-1$
	private static final Font HDR_FONT = FontFactory.getFont("Tahoma", 12, Font.BOLD); //$NON-NLS-1$
	private static final Font TBL_HDR_FONT = FontFactory.getFont("Tahoma", 10, Font.BOLD); //$NON-NLS-1$
	private static final Font COL_HDR_FONT = FontFactory.getFont("Tahoma", 10, Font.BOLD); //$NON-NLS-1$
	private static final Color DEF_BGCOLOR = new Color(255, 255, 255);
	
	private transient Section topMostSection;
	private transient PdfPTable groupTable;
	
	private static FontMapper fontMapper = new DefaultFontMapper();

	/**
	 * Constructor
	 * 
	 */
	private PDFExporter()
	{
		// Constructor has been made private to prevent external object
		// instantiation of this class.
		// Use the getInstance() method in order to get an instance of this
		// class.
	}

	public static PDFExporter getNewInstance()
	{
		return new PDFExporter();
	}

	/**
	 * Method getInstance
	 * 
	 * @return
	 */
	public static PDFExporter getInstance()
	{
		if (thisInstance == null)
		{
			thisInstance = getNewInstance();
		}
		return thisInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#getFileExtension()
	 */
	protected String getFileExtension()
	{
		return ExportConstants.getFileExtensionWithDot(ExportConstants.EXPORT_TYPE_PDF);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportNode(com.queryio.sysmoncommon.exporter.dstruct.IExportableNode)
	 */
	public void exportNode(final AbstractExportableNode nodeToExport) throws ExporterException
	{
		this.node = nodeToExport;
		this.document = new Document(new Rectangle(this.iWidth, this.iHeight), 10f, 10f, 5f, 5f);
		this.iWidth -= 20;
		this.iOriginalHeight = this.iHeight -= 10;
		try
		{
			this.writer = PdfWriter.getInstance(this.document, new BufferedOutputStream(new FileOutputStream(new File(
					this.node.getFilePath(), this.node.getExportedFileName() + this.getFileExtension()))));
			this.addMetaData();
			this.document.open();
			this.addReportHeader(true);
			final int iStart = this.addHeader();
			this.iHeight -= iStart;
			this.iCurrentPos = this.iHeight;
			this.exportNodeItems();
		}
		catch (final Exception e)
		{
			throw new ExporterException(RM.getString("ERR_WRITING_FILE") + nodeToExport.getExportedFileName() //$NON-NLS-1$
					+ this.getFileExtension(), e);
		}
		this.document.close();
	}

	/**
	 * Method addMetaData
	 * 
	 */
	private void addMetaData()
	{
		// meta-data
		this.document.addTitle(this.sCurrentProject != null ? 
			this.sCurrentProject:(this.node.getHeaderText() != null ? this.node.getHeaderText():"AppPerfect Report"));
		this.document.addAuthor("AppPerfect Corporation"); //$NON-NLS-1$
		// document.addSubject("Generated by AppPerfect Corporation"); //$NON-NLS-1$
		this.document.addKeywords("iText, AppPerfect, Load Test, App Test, Web Test, Code Test, Java Profiler, Unit Test, Agentless Monitor, Test Manager"); //$NON-NLS-1$
		this.document.addCreator("Pdf document using iText"); //$NON-NLS-1$
	}
	
	private void addReportHeader(boolean showReportTitle) throws ExporterException{
		try{
			Paragraph header = new Paragraph();
	
			String imgSrc = "http://www.appperfect.com/images/ap.jpg";
			
			File imgFile = null;
			Image headerImg = null;
			if (sHeaderImagePath != null)
			{
				try
				{
					imgFile = new File(sHeaderImagePath);
					if (imgFile.exists())
					{
						imgSrc = sHeaderImagePath;
					}
					headerImg = Image.getInstance(imgSrc);
				}
				catch (Exception ex)
				{
					// do nothing
				}
			}
			
			if (headerImg != null)
			{
				headerImg.scalePercent(75);
				headerImg.setAlignment(Element.ALIGN_RIGHT);
				header.add(headerImg);
			}
			ReportHeader reportHeader = node.getReportHeader();
			if(reportHeader != null){
				Paragraph p = new Paragraph(reportHeader.toString(showReportTitle), HDR_FONT);
				p.setAlignment(Element.ALIGN_LEFT);
				header.add(p);
			}else{
				Paragraph p = new Paragraph(this.node.getHeaderText(), HDR_FONT);
				p.setAlignment(Element.ALIGN_LEFT);
				header.add(p);
			}
			addElement(header);
		}
		catch (final Exception exception)
		{
			throw new ExporterException(RM.getString("ERR_ADDING_HEADER"), exception); //$NON-NLS-1$
		}
	}

	/**
	 * Method addHeader
	 * 
	 * @return
	 * @throws ExporterException
	 */
	private int addHeader() throws ExporterException
	{
		int pos = 0;
		try
		{
			pos += 15;
			final String[] labels = this.node.getIdentifyingLabels();
			if (labels != null)
			{
				final Font labelsFont = FontFactory.getFont("Tahoma", 10, Font.NORMAL);
				for (int i = 0; i < labels.length; i++)
				{
					addElement(new Paragraph(labels[i], labelsFont));
					pos += 15;
				}
			}
		}
		catch (final Exception exception)
		{
			throw new ExporterException(RM.getString("ERR_ADDING_HEADER"), exception); //$NON-NLS-1$
		}
		return pos;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Group)
	 */
	protected void exportItem(final Group group) throws ExporterException
	{
		try
		{
			if (this.iCurrentPos < 15)
			{
		    	this.document.newPage();
				this.iCurrentPos = this.iOriginalHeight;
			}
			addElement(new Paragraph(group.getHeading(), DEF_FONT));
			this.iCurrentPos -= 15;
		}
		catch (final Exception exception)
		{
			throw new ExporterException(RM.getString("ERR_EXPORTING_GROUP"), exception); //$NON-NLS-1$
		}
		final IExportableItem[] items = group.getItems();
		
		int top = 0;
		boolean allEqual = true;
		for (int i = 0; i < items.length; i++) 
		{
			if (i == 0)
			{
				top = items[i].getTop();
			}
			else if (items[i].getTop() != top)
			{
				allEqual = false;
				break;
			}
		}
		
		if (allEqual)
		{
			try
			{
				groupTable = new PdfPTable(items.length);
				groupTable.setTotalWidth(group.getWidth() * this.iWidth / 100);
				groupTable.setLockedWidth(true);
				groupTable.setHorizontalAlignment(Element.ALIGN_LEFT);
				groupTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
				groupTable.getDefaultCell().setPadding(0f);
				this.exportItems(group.getItems());
				groupTable.setExtendLastRow(false);
				addElement(groupTable);
			}
			catch (Exception ex)
			{
				throw new ExporterException(RM.getString("ERR_EXPORTING_GROUP"), ex); //$NON-NLS-1$				
			}
			finally
			{
				groupTable = null;
			}
		}
		else
		{
			final int[][] size = new int[items.length][2];
			for (int i = items.length - 1; i >= 0; --i)
			{
				if (items[i] instanceof ExportableItem)
				{
					size[i][0] = items[i].getWidth();
					size[i][1] = items[i].getHeight();
					final int maxWidth = Math.max(group.getWidth(), size[i][0]);
					final int minWidth = Math.min(group.getWidth(), size[i][0]);
					final int maxHeight = Math.max(group.getHeight(), size[i][1]);
					final int minHeight = Math.min(group.getHeight(), size[i][1]);
					((ExportableItem) items[i]).setSize(minWidth * 100 / maxWidth, minHeight * 100 / maxHeight);
				}
			}
			this.exportItems(group.getItems());
			for (int i = items.length - 1; i >= 0; --i)
			{
				if (items[i] instanceof ExportableItem)
				{
					((ExportableItem) items[i]).setSize(size[i][0], size[i][1]);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Table)
	 */
	protected void exportItem(final Table table) throws ExporterException
	{
		try
		{
			if (table.getTableHeader() != null)
			{
				addElement(new Paragraph(table.getTableHeader(), TBL_HDR_FONT));
			}
			final int[] colWidths = table.getColumnWidths();
			int nPdfCols = 0;
			final TableModel model = table.getModel();
			final int numCols = model.getColumnCount();
			final int minColCounts = Math.min(numCols, colWidths.length);
			final int numRows = model.getRowCount();
			for (int x = 0; x < minColCounts; ++x)
			{
				if (colWidths[x] == 0)
				{
					++nPdfCols;
				}
			}
			final PdfPTable pdfTable = new PdfPTable(minColCounts - nPdfCols);
			float [] newColWidths = new float[numCols - nPdfCols];
			int ctr = 0;
			for (int x = 0; x < minColCounts; ++x)
			{
				if (colWidths[x] != 0)
				{
					newColWidths[ctr] = colWidths[x];
					ctr ++;
				}
			}
			pdfTable.setWidths(newColWidths);			
			pdfTable.setTotalWidth(table.getWidth() * this.iWidth / 100);
			pdfTable.setLockedWidth(true);
			pdfTable.setHorizontalAlignment(Element.ALIGN_LEFT);
			pdfTable.setHeaderRows(1);
			pdfTable.setSpacingBefore(5f);
			pdfTable.setSpacingAfter(5f);
			pdfTable.getDefaultCell().setPadding(5f);
			// pdfTable.getDefaultCell().setBackgroundColor(DEF_AWT_BGCOLOR);
			pdfTable.getDefaultCell().setNoWrap(false);
			
			for (int i = 0, k = 0; i < numCols; i++, ++k)
			{
				if (k < colWidths.length)
				{
					if (colWidths[k] <= 0)
					{
						continue;
					}
				}
				pdfTable.addCell(new Phrase(model.getColumnName(i), COL_HDR_FONT));
			}
			pdfTable.getDefaultCell().setPadding(2f);
			pdfTable.getDefaultCell().setBackgroundColor(java.awt.Color.white);
			if (numRows > 0)
			{
				for (int i = 0; i < numRows; i++)
				{
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
						if(j==0){
							PdfPCell cell = new PdfPCell(new Phrase(val == null ? IProductConstants.EMPTY_STRING : val.toString(), DEF_FONT));
							if (val == null)
								cell.setNoWrap(true);
							else
								cell.setNoWrap(false);
							
							pdfTable.addCell(cell);	
						}else{
							pdfTable.addCell(new Phrase(val == null ? IProductConstants.EMPTY_STRING : val.toString(), DEF_FONT));
						}
					}
				}
			}
			else
			{
				PdfPCell cell = new PdfPCell(pdfTable.getDefaultCell());
				cell.setColspan(numCols - nPdfCols);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setPhrase(new Phrase("No data found"));
				pdfTable.addCell(cell);
			}
			if (table.isTotal())
			{
				final ITotalModel totalModel = (ITotalModel) model;
				for (int j = 0, k = 0; j < numCols; j++, ++k)
				{
					if (k < colWidths.length)
					{
						if (colWidths[k] <= 0)
						{
							continue;
						}
					}
					final Object val = totalModel.getValueForCol(j);
					pdfTable.addCell(new Phrase(val == null ? IProductConstants.EMPTY_STRING : val.toString(), DEF_FONT));

				}
			}

			final int tableHeight = (int) pdfTable.getTotalHeight() + 10;
			if (this.iCurrentPos < tableHeight)
			{
				this.iCurrentPos = (tableHeight - this.iCurrentPos) % this.iOriginalHeight;
			}
			else
			{
				this.iCurrentPos -= tableHeight;
			}
			addElement(pdfTable);
		}
		catch (final Exception exception)
		{
			throw new ExporterException(RM.getString("ERR_EXPORTING_TABLE"), exception); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Chart)
	 */
	protected void exportItem(final Chart chart) throws ExporterException
	{
		try
		{
			chart.setSize((chart.getWidth() - 10), (chart.getHeight() - 10));
			chart.setBackgroundColor(DEF_BGCOLOR);
			int height = chart.getHeight() * this.iHeight / 100;
			//final int height = chart.getHeight() * this.iHeight / (chart.isScrollable() ? 150:200);
			final int width = chart.getWidth() * this.iWidth / 100;
			final PdfContentByte cb = this.writer.getDirectContent();
			final PdfTemplate tp = cb.createTemplate(width, height);
			Graphics2D g2 = tp.createGraphics(width, height, fontMapper);
			if (chart.isScrollable())
			{
				boolean scrollable = ((IScrollableChart) chart.getAppChart()).isScrollable();
				if (scrollable)
				{
					final SWINGUserInterface swingInterface = new SWINGUserInterface();
					swingInterface.setThinLine(true);
					swingInterface.setExternalGraphics(g2);
					height = ((IScrollableChart) chart.getAppChart()).getTotalChartHeight(swingInterface, width, height);
					g2.dispose();
					g2 = tp.createGraphics(width, height, fontMapper);
				}
			}
			chart.drawChartForPDF(g2, width, height);
			g2.dispose();
			final Image image = Image.getInstance(tp);
			if (this.iCurrentPos < height)
			{
		    	this.document.newPage();
				this.iCurrentPos = this.iOriginalHeight;
			}
			addElement(image);
			this.iCurrentPos -= height;
			// System.out.println("PDFExporter.exportItem()....chart");
		}
		catch (final Exception exception)
		{
			throw new ExporterException(RM.getString("ERR_EXPORTING_CHART"), exception); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Tree)
	 */
	protected void exportItem(final Tree tree) throws ExporterException
	{
		if (tree.getModel() instanceof IAbstractExportableTreeModel)
		{
			this.exportTreeModel(tree);
		}
		else
		{
			this.exportTableModel(tree);
		}
	}

	private void writeTreeNode(final PdfPTable pdfTable, final TreeNode node, final int iLevel)
	{
		if (node != null)
		{
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
			pdfTable.addCell(new Phrase(sbBuff.toString(), DEF_FONT));
			// Add all the child nodes of this node
			final int iChildCount = node.getChildCount();
			if (iChildCount > 0)
			{
				for (int i = 0; i < iChildCount; i++)
				{
					this.writeTreeNode(pdfTable, node.getChildAt(i), iLevel + 1);
				}
			}
		}
	}

	private void exportTreeModel(final Tree tree) throws ExporterException
	{
		try
		{
			final TreeNode root = (TreeNode) tree.getModel().getRoot();
			final PdfPTable pdfTable = new PdfPTable(1);
			pdfTable.setWidths(new int[] { 100 });
			pdfTable.setTotalWidth(tree.getWidth() * this.iWidth / 100);
			pdfTable.setLockedWidth(true);
			pdfTable.setHorizontalAlignment(Element.ALIGN_LEFT);
			pdfTable.setSpacingBefore(5f);
			pdfTable.setSpacingAfter(5f);
			pdfTable.getDefaultCell().setPadding(2f);
			pdfTable.getDefaultCell().setBackgroundColor(java.awt.Color.white);

			if (root != null)
			{
				this.writeTreeNode(pdfTable, root, 0);
			}

			final int tableHeight = (int) pdfTable.getTotalHeight() + 10;
			if (this.iCurrentPos < tableHeight)
			{
				this.iCurrentPos = (tableHeight - this.iCurrentPos) % this.iOriginalHeight;
			}
			else
			{
				this.iCurrentPos -= tableHeight;
			}
			addElement(pdfTable);
		}
		catch (final Exception exception)
		{
			throw new ExporterException(RM.getString("ERR_EXPORTING_TREE"), exception); //$NON-NLS-1$
		}
	}

	private void exportTableModel(final Tree tree) throws ExporterException
	{
		try
		{
			final TableModel model = this.convertTreeModelToTableModel(tree.getModel());
			final int numRows = model.getRowCount();
			final PdfPTable pdfTable = new PdfPTable(1);
			pdfTable.setWidths(new int[] { 100 });
			pdfTable.setTotalWidth(tree.getWidth() * this.iWidth / 100);
			pdfTable.setLockedWidth(true);
			pdfTable.setHorizontalAlignment(Element.ALIGN_LEFT);
			pdfTable.setSpacingBefore(5f);
			pdfTable.setSpacingAfter(5f);
			pdfTable.getDefaultCell().setPadding(2f);
			pdfTable.getDefaultCell().setBackgroundColor(java.awt.Color.white);
			for (int i = 0; i < numRows; i++)
			{
				final Object val = model.getValueAt(i, 0);
				pdfTable.addCell(new Phrase(val == null ? IProductConstants.EMPTY_STRING : val.toString(), DEF_FONT));
			}
			final int tableHeight = (int) pdfTable.getTotalHeight() + 10;
			if (this.iCurrentPos < tableHeight)
			{
				this.iCurrentPos = (tableHeight - this.iCurrentPos) % this.iOriginalHeight;
			}
			else
			{
				this.iCurrentPos -= tableHeight;
			}
			addElement(pdfTable);
		}
		catch (final Exception exception)
		{
			throw new ExporterException(RM.getString("ERR_EXPORTING_TREE"), exception); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Label)
	 */
	protected void exportItem(final Label label) throws ExporterException
	{
		try
		{
			if (this.iCurrentPos < 15)
			{
		    	this.document.newPage();
				this.iCurrentPos = this.iOriginalHeight;
			}
			addElement(new Paragraph(label.getText(), DEF_FONT));
			this.iCurrentPos -= 15;
		}
		catch (final Exception exception)
		{
			throw new ExporterException(RM.getString("ERR_EXPORTING_LABEL"), exception); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.TextBox)
	 */
	protected void exportItem(final TextBox textBox) throws ExporterException
	{
		try
		{
			if (this.iCurrentPos < 15)
			{
		    	this.document.newPage();
				this.iCurrentPos = this.iOriginalHeight;
			}
			addElement(new Paragraph(textBox.getText(), DEF_FONT));
			this.iCurrentPos -= 15;
		}
		catch (final Exception exception)
		{
			throw new ExporterException(RM.getString("ERR_EXPORTING_TEXTBOX"), exception); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.HTMLPage)
	 */
	protected void exportItem(final HTMLPage page) throws ExporterException
	{
		try
		{
			if (page.getFileName() != null && page.getFileName().trim().length() > 0)
			{
				final Anchor anchor = new Anchor("View File"); //$NON-NLS-1$
				anchor.getFont().setStyle(4);
				anchor.getFont().setColor(0, 0, 255);
				anchor.setName("View File"); //$NON-NLS-1$
				if ("about:blank".equals(page.getFileName())) //$NON-NLS-1$
				{
					anchor.setReference("about:blank"); //$NON-NLS-1$
				}
				else
				{
					anchor.setReference("file://" + page.getFileName()); //$NON-NLS-1$
				}
				addElement(anchor);
			}
		}
		catch (final Exception exception)
		{
			throw new ExporterException(RM.getString("ERR_EXPORTING_HTMLPAGE"), exception); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.TabFolder)
	 */
	protected void exportItem(final TabFolder tabs) throws ExporterException
	{
		final Section section = bExportingResult ? topMostSection:null;
		try
		{
			final String[] tabNames = tabs.getTabNames();
			
			final IExportableItem[][] items = tabs.getItems();
			final int numTabs = tabNames.length;
			for (int i = 0; i < numTabs; i++)
			{
				if (this.bExportingResult)
				{
					this.iCurrentPos -= 30;
					
					try
					{
			            Section childSection = section.addSection(tabNames[i], 0);
			            topMostSection = childSection;
						this.exportItems(items[i]);
						if (i != numTabs - 1)
						{
					    	this.document.newPage();
							this.iCurrentPos = this.iOriginalHeight;
						}
		            }
		            finally
		            {
		            	topMostSection = section;
		            }
				}
				else
				{
					this.writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
					final PdfOutline root = this.writer.getDirectContent().getRootOutline();
					//addElement(new Paragraph(tabNames[i], DEF_FONT));
					this.iCurrentPos -= 30;
					new PdfOutline(root, new PdfDestination(PdfDestination.FITBH, this.iOriginalHeight), tabNames[i]);
					this.exportItems(items[i]);
					if (i != numTabs - 1)
					{
				    	this.document.newPage();
						this.iCurrentPos = this.iOriginalHeight;
					}
				}
			}
		}
		catch (final Exception exception)
		{
			throw new ExporterException(RM.getString("ERR_EXPORTING_TABFOLDER"), exception); //$NON-NLS-1$
		}
        finally
        {
        	topMostSection = section;
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.LabelTextPanel)
	 */
	protected void exportItem(final LabelTextPanel panel) throws ExporterException
	{
		try
		{
			final PdfPTable table = new PdfPTable(4);
			table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
			int count = 0;
			final IExportableItem[][] items = panel.getItems();
			Paragraph para;
			String value;
			for (int i = 0; i < items.length; i++)
			{
				for (int j = 0; j < items[i].length; j++)
				{
					if (items[i][j] != null)
					{
						if (items[i][j] instanceof Label)
						{
							value = ((Label) items[i][j]).getText();
						}
						else
						{
							value = ((TextBox) items[i][j]).getText();
						}
						para = new Paragraph();
						para.add(new Chunk(value, DEF_FONT));
						table.addCell(para);
						++count;
					}
				}
				if (this.iCurrentPos < 15)
				{
			    	this.document.newPage();
					this.iCurrentPos = this.iOriginalHeight;
				}
				this.iCurrentPos -= 15;

			}
			while (count % 4 != 0)
			{
				para = new Paragraph();
				para.add(new Chunk("", DEF_FONT));
				table.addCell(para);
				count++;
			}
			addElement(new Chunk("", DEF_FONT));
			addElement(table);
		}
		catch (final Exception exception)
		{
			throw new ExporterException(RM.getString("ERR_EXPORTING_LABELTEXTPANEL"), exception); //$NON-NLS-1$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Paragraph)
	 */
	protected void exportItem(final com.queryio.common.exporter.dstruct.Paragraph para) throws ExporterException
	{
		final Label [] items = para.getItems();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < items.length; i++)
		{
			buffer.append(items[i].getText());
		}
		this.exportItem(new Label(para.getLeft(), para.getTop(), para.getWidth(), para.getHeight(), buffer.toString()));
	}
	
	
	private void addElement(Element element) throws DocumentException
	{
		if (groupTable != null && element != groupTable)
		{
			if (element instanceof Phrase)
			{
				groupTable.addCell((Phrase)element);
			}
			else if (element instanceof Image)
			{
				groupTable.addCell((Image)element);
			}
			else if (element instanceof PdfPCell)
			{
				groupTable.addCell((PdfPCell)element);
			}
			else if (element instanceof PdfPTable)
			{
				groupTable.addCell((PdfPTable)element);
			}
		}
		else if (topMostSection != null)
		{
			topMostSection.add(element);
		}
		else
		{
			this.document.add(element);
		}
	}

	public void reset()
	{
		topMostSection = null;
	}
	
	public void exportResult(String location, ArrayList models) throws ExporterException
	{
		this.document = new Document(new Rectangle(this.iWidth, this.iHeight), 20f, 20f, 20f, 0f);
		this.iWidth -= 40;
		this.iOriginalHeight = this.iHeight -= 20;
		try
		{
			this.writer = PdfWriter.getInstance(this.document, new BufferedOutputStream(
				new FileOutputStream(new File(location, "Result_" + sCurrentProject + this.getFileExtension()))));
			this.addMetaData();
            writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
			this.document.open();
			this.iCurrentPos = this.iHeight;
		
			// Now export all the models.
			TreeModel treeModel;
			AbstractExportableNode root;
			for (int i = 0; i < models.size(); i++) 
			{
				treeModel = (TreeModel)models.get(i);
				root = (AbstractExportableNode)treeModel.getRoot();

                Chapter chapter = new Chapter(root.getDisplayName(), i + 1);
                chapter.setNumberDepth(0);
                topMostSection = chapter;
                this.node = root;
        		this.addReportHeader(false);
        		final int iStart = this.addHeader();
    			this.iHeight -= iStart;
				this.writeNode(topMostSection, root, treeModel, location); //$NON-NLS-1$
				topMostSection = null;
				addElement(chapter);
				
			}
		}
		catch (final Exception e)
		{
			throw new ExporterException("Error exporting comple result as PDF", e);
		}
		this.document.close();
	}
	
	private void writeNode(Section section, AbstractExportableNode node, TreeModel treeModel, String reportsLocation) throws ExporterException 
	{
		this.node = node;

		// export node's items.
		final String originalPath = node.getFilePath();
		node.setFilePath(reportsLocation);
		this.exportItems(node.getItems());
		node.setFilePath(originalPath);
		
		section.add(Chunk.NEXTPAGE);
		
		// export its children now
		final int n = treeModel.getChildCount(node);
		AbstractExportableNode childNode;
		for (int j = 0; j < n; j++)
		{
			childNode = (AbstractExportableNode)treeModel.getChild(node, j);
            Section childSection = section.addSection(childNode.getDisplayName(), 0);
            topMostSection = childSection;
			this.writeNode(topMostSection, childNode, treeModel, reportsLocation); //$NON-NLS-1$
			topMostSection = section;
		}
	}
	
}
