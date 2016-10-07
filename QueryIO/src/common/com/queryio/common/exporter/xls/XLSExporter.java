/**
 *
 */
package com.queryio.common.exporter.xls;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;

import com.queryio.common.exporter.AbstractExporter;
import com.queryio.common.exporter.ExportConstants;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.Chart;
import com.queryio.common.exporter.dstruct.Counter;
import com.queryio.common.exporter.dstruct.Group;
import com.queryio.common.exporter.dstruct.HTMLPage;
import com.queryio.common.exporter.dstruct.IAbstractExportableTreeModel;
import com.queryio.common.exporter.dstruct.IAbstractExportableTreeNode;
import com.queryio.common.exporter.dstruct.IExportableItem;
import com.queryio.common.exporter.dstruct.Label;
import com.queryio.common.exporter.dstruct.LabelTextPanel;
import com.queryio.common.exporter.dstruct.Paragraph;
import com.queryio.common.exporter.dstruct.TabFolder;
import com.queryio.common.exporter.dstruct.Table;
import com.queryio.common.exporter.dstruct.TextBox;
import com.queryio.common.exporter.dstruct.Tree;
import com.queryio.common.exporter.exceptions.ExporterException;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StaticUtilities;

/**
 * XLSExporter
 *
 * @author Exceed Consultancy Services
 * @version 9.0
 */
public class XLSExporter extends AbstractExporter
{
	private static XLSExporter thisInstance = null;

	protected HSSFWorkbook workbook = null;
	protected HSSFSheet sheet = null;
	private String sheetName = null;

	protected HSSFCellStyle emptyCellStyle = null;
	protected HSSFCellStyle dateCellStyle = null;
	protected HSSFCellStyle boldCellStyle = null;

	protected int rowIndex = 0;

	/**
	 *
	 */
	private XLSExporter()
	{
		// Constructor has been made private to prevent external object
		// instantiation of this class.
		// Use the getInstance() method in order to get an instance of this
		// class.
	}

	public static XLSExporter getNewInstance()
	{
		return new XLSExporter();
	}

	/**
	 * @return
	 */
	public static XLSExporter getInstance()
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
		return ExportConstants.getFileExtensionWithDot(ExportConstants.EXPORT_TYPE_XLS);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportNode(com.queryio.sysmoncommon.exporter.dstruct.IExportableNode)
	 */
	public void exportNode(final AbstractExportableNode nodeToExport) throws ExporterException
	{
		this.node = nodeToExport;
		OutputStream outputStream = null;
		try
		{
			final File file = new File(this.node.getFilePath(), this.node.getExportedFileName()
					+ this.getFileExtension());
			outputStream = new BufferedOutputStream(new FileOutputStream(file)); //$NON-NLS-1$

			this.workbook = new HSSFWorkbook();
			this.emptyCellStyle = this.workbook.createCellStyle();

			this.dateCellStyle = this.workbook.createCellStyle();
			this.dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("M/d/yy h:mm"));

			final HSSFFont boldFont = this.workbook.createFont();
			boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
			this.boldCellStyle = this.workbook.createCellStyle();
			this.boldCellStyle.setFont(boldFont);

			this.exportNodeItems();
			this.workbook.write(outputStream);
		}
		catch (final IOException e)
		{
			throw new ExporterException(RM.getString("ERR_WRITING_FILE") + nodeToExport.getExportedFileName() //$NON-NLS-1$
					+ this.getFileExtension(), e);
		}
		finally
		{
			if (outputStream != null)
			{
				try
				{
					outputStream.flush();
					outputStream.close();
				}
				catch (final IOException e)
				{
					AppLogger.getLogger().log(
							AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); //$NON-NLS-1$
				}
			}
			this.sheet = null;
			this.sheetName = null;
		}
	}

	private void checkSheetCreation()
	{
		// Create Sheet
		if (this.sheet == null)
		{
			final String name = (this.sheetName != null) ? this.sheetName:("Sheet" + this.workbook.getNumberOfSheets() + 1);
			this.sheet = this.workbook.createSheet(name); //$NON-NLS-1$
			this.rowIndex = 0;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Group)
	 */
	protected void exportItem(final Group group) throws ExporterException
	{
		this.exportItems(group.getItems());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.TabFolder)
	 */
	protected void exportItem(final TabFolder tabFolder) throws ExporterException
	{
		final IExportableItem[][] tabs = tabFolder.getItems();
		final String[] tabNames = tabFolder.getTabNames();
		if (tabs != null)
		{
			String prevSheetName = this.sheetName;
			HSSFSheet prevSheet = this.sheet;
			for (int i = 0; i < tabs.length; i++)
			{
				// Create Sheet
				String sheetName = (((tabNames[i] != null) && (tabNames[i].trim().length() != 0)) ? tabNames[i]
						: "Sheet" + this.workbook.getNumberOfSheets() + 1);
				sheetName = sheetName.replace('/', '_');
				sheetName = sheetName.replace('\\', '_');
				sheetName = sheetName.replace('*', '_');
				sheetName = sheetName.replace('?', '_');
				sheetName = sheetName.replace('[', '_');
				sheetName = sheetName.replace(']', '_');
				this.sheetName = sheetName;
				this.sheet = null;
				//this.sheet = this.workbook.createSheet(sheetName);
				this.rowIndex = 0;
				this.exportItems(tabs[i]);
			}
			this.sheet = prevSheet;
			this.sheetName = prevSheetName;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Table)
	 */
	protected void exportItem(final Table table) throws ExporterException
	{
		this.exportTableModel(table.getLeft(), table.getTop(), table.getModel(), table.getColumnWidths());
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
			this.exportTreeModel(tree.getLeft(), tree.getTop(), tree.getModel());
		}
		else
		{
			this.exportTableModel(tree.getLeft(), tree.getTop(), this.convertTreeModelToTableModel(tree.getModel()), null);
		}
	}

	private int getBestMatchingRow(final int xCor, final int yCor)
	{
		// TODO - implement this based on the input received
		if (this.rowIndex > 0)
		{
			this.rowIndex += 2; // To have 2 blank rows between two entries
		}
		return this.rowIndex;
	}

	private void writeTreeNode(final TreeNode node, final int iLevel, final Counter rowCount)
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
			HSSFRow row = this.sheet.getRow((short) rowCount.getValue());
			if (row == null)
			{
				row = this.sheet.createRow((short) rowCount.getValue());
				this.rowIndex++;
			}
			rowCount.incrementCounter();
			HSSFCell cell = row.getCell(0);
			if (cell == null)
			{
				cell = row.createCell(0);
				cell.setCellStyle(this.emptyCellStyle);
			}
			this.setCellValue(cell, sbBuff.toString());
			// Add all the child nodes of this node
			final int iChildCount = node.getChildCount();
			if (iChildCount > 0)
			{
				for (int i = 0; i < iChildCount; i++)
				{
					this.writeTreeNode(node.getChildAt(i), iLevel + 1, rowCount);
				}
			}
		}
	}

	private void exportTreeModel(final int left, final int top, final TreeModel model)
	{
		try
		{
			this.checkSheetCreation();
			final int rowNum = this.getBestMatchingRow(left, top);
			final TreeNode root = (TreeNode) model.getRoot();
			final Counter ctr = new Counter(rowNum + 1);
			if (root != null)
			{
				this.writeTreeNode(root, 0, ctr);
			}
		}
		catch (final Exception e)
		{
			AppLogger.getLogger().log(
					AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); //$NON-NLS-1$
		}
	}

	/**
	 * @param model
	 */
	private void exportTableModel(final int left, final int top, final TableModel model, final int[] colWidths)
	{
		String value = null;
		try
		{
			this.checkSheetCreation();
			// write column headers
			final int numCols = model.getColumnCount();
			int rowNum = this.getBestMatchingRow(left, top);
			HSSFCell cell = null;
			HSSFRow row = this.sheet.getRow((short) rowNum);
			if (row == null)
			{
				row = this.sheet.createRow((short) rowNum);
				rowNum++;
				this.rowIndex++;
			}
			int actualColIndex = 0;
			for (int colIndex = 0; colIndex < numCols; colIndex++)
			{
				value = model.getColumnName(colIndex);
				if (colWidths == null || colWidths[colIndex] > 0)
				{
					cell = row.getCell(actualColIndex);
					if (cell == null)
					{
						cell = row.createCell(actualColIndex);
						cell.setCellStyle(this.boldCellStyle);
					}
					this.setCellValue(cell, value != null ? value : "Column" + (actualColIndex + 1));
					actualColIndex ++;
				}
			}

			int rowCount = rowNum;
			// write all rows
			final int numRows = model.getRowCount();
			boolean [] coldataNumeric = new boolean [numCols];
			Object cellData;
			for (int i = 0; i < numRows; i++)
			{
				rowCount = rowNum + i; // + 2;
				row = this.sheet.getRow((short) rowCount);
				if (row == null)
				{
					row = this.sheet.createRow((short) rowCount);
					this.rowIndex++;
				}
				actualColIndex = 0;
				for (int j = 0; j < numCols; j++)
				{
					if (colWidths == null || colWidths[j] > 0)
					{
						cell = row.getCell(actualColIndex);
						if (cell == null)
						{
							cell = row.createCell(actualColIndex);
							cell.setCellStyle(this.emptyCellStyle);
						}
						actualColIndex ++;
						cellData = model.getValueAt(i, j);
						if (cellData != null)
						{
							if (j == 0)
							{
								try
								{ 
									cellData = cellData.toString();
									coldataNumeric[j] = true;
								}
								catch (Exception ex)
								{
									// do nothing
								}
							}
							else if (coldataNumeric[j])
							{
								try
								{
									cellData = StaticUtilities.getDecimalFormat().parse(cellData.toString());
								}
								catch (Exception ex)
								{
									// do nothing
								}
							}
						}
						this.setCellValue(cell, cellData);
					}
				}
			}
		}
		catch (final Exception e)
		{
			AppLogger.getLogger().fatal("error exporting table model", e); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Chart)
	 */
	protected void exportItem(final Chart chart) throws ExporterException
	{
		// Ideally, insert X-Axis values & Y-Axis values in the sheet and add
		// XLS's chart. If user modifies a value, chart should reflect it.
		// DO NOTHING - Currently POI does not support Charts.
	}

	private void setCellValue(final HSSFCell cell, final Object value)
	{
		if (cell != null)
		{
			if (value == null)
			{
				cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
			}
			else if (value instanceof Calendar)
			{
				cell.setCellValue(((Calendar) value).getTime());
				cell.setCellStyle(this.dateCellStyle);
			}
			else if (value instanceof Date)
			{
				cell.setCellValue((Date) value);
				cell.setCellStyle(this.dateCellStyle);
			}
			else if (value instanceof Boolean)
			{
				cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
				// cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(((Boolean) value).booleanValue());
				// cell.setCellValue(String.valueOf(((Boolean)value).booleanValue()));
			}
			else if (value instanceof Number)
			{
//				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
//				// cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell.setCellValue(((Number) value).doubleValue());
//				// cell.setCellValue(String.valueOf(((Number)value).doubleValue()));
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(String.valueOf(((Number) value).doubleValue()));
				
			}
			else
			{
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(new HSSFRichTextString(value.toString()));
			}
		}
	}

	private void exportText(final int left, final int top, final int width, final int height, final String text)
	{
		this.checkSheetCreation();
		final int rowNum = this.getBestMatchingRow(left, top);
		HSSFCell cell = null;
		HSSFRow row = this.sheet.getRow((short) rowNum);
		if (row == null)
		{
			row = this.sheet.createRow((short) rowNum);
		}
		cell = row.getCell(0);
		if (cell == null)
		{
			cell = row.createCell(0);
			cell.setCellStyle(this.emptyCellStyle);
		}
		this.setCellValue(cell, text);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Label)
	 */
	protected void exportItem(final Label label) throws ExporterException
	{
		this.exportText(label.getLeft(), label.getTop(), label.getWidth(), label.getHeight(), label.getText());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.TextBox)
	 */
	protected void exportItem(final TextBox textBox) throws ExporterException
	{
		this
				.exportText(textBox.getLeft(), textBox.getTop(), textBox.getWidth(), textBox.getHeight(), textBox
						.getText());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.HTMLPage)
	 */
	protected void exportItem(final HTMLPage page) throws ExporterException
	{
		this.checkSheetCreation();
		final int rowNum = this.getBestMatchingRow(0, 0);
		HSSFRow row = this.sheet.getRow((short) rowNum);
		if (row == null)
		{
			row = this.sheet.createRow((short) rowNum);
		}
		HSSFCell cell = row.getCell(0);
		if (cell == null)
		{
			cell = row.createCell(0);
			cell.setCellStyle(this.emptyCellStyle);
			cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
		}
		final String fileName = page.getFileName() != null ? page.getFileName() : "<null>";
		cell.setCellFormula("HYPERLINK(\"file:///" + fileName + "\"" + ", \"HTMLFile\")");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.LabelTextPanel)
	 */
	protected void exportItem(final LabelTextPanel panel) throws ExporterException
	{
		if (panel != null)
		{
			final IExportableItem[][] items = panel.getItems();
			if (items != null)
			{
				for (int i = 0; i < items.length; i++)
				{
					this.exportItems(items[i]);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio.sysmoncommon.exporter.dstruct.Paragraph)
	 */
	protected void exportItem(final Paragraph para) throws ExporterException
	{
		final Label [] items = para.getItems();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < items.length; i++)
		{
			buffer.append(items[i].getText());
		}
		this.exportItem(new Label(para.getLeft(), para.getTop(), para.getWidth(), para.getHeight(), buffer.toString()));
	}
	
}