/**
 *
 */
package com.queryio.common.exporter.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import com.queryio.common.exporter.AbstractExporter;
import com.queryio.common.exporter.ExportConstants;
import com.queryio.common.exporter.dstruct.AbstractExportableNode;
import com.queryio.common.exporter.dstruct.Chart;
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
import com.queryio.common.util.XMLWriter;

/**
 * XMLExporter
 *
 * @author Exceed Consultancy Services
 * @version 9.0
 */
public final class XMLExporter extends AbstractExporter {
	private XMLWriter writer = null;
	private static XMLExporter thisInstance = null;

	/**
	 *
	 */
	private XMLExporter() {
		// Constructor has been made private to prevent external object
		// instantiation of this class.
		// Use the getInstance() method in order to get an instance of this
		// class.
	}

	public static XMLExporter getNewInstance() {
		return new XMLExporter();
	}

	/**
	 * @return
	 */
	public static XMLExporter getInstance() {
		if (thisInstance == null) {
			thisInstance = getNewInstance();
		}
		return thisInstance;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.queryio.sysmoncommon.exporter.AbstractExporter#getFileExtension()
	 */
	protected String getFileExtension() {
		return ExportConstants.getFileExtensionWithDot(ExportConstants.EXPORT_TYPE_XML);
	}

	public void exportResult(String location, ArrayList models) throws ExporterException {
		BufferedWriter bufferedWriter = null;
		try {
			final File file = new File(location, "Result_" + sCurrentProject + this.getFileExtension());
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")); //$NON-NLS-1$
			this.writer = new XMLWriter(bufferedWriter);
			this.writer.startDocument(); // Write the <XML tag
			this.writer.startElement("CompleteReport", "Name", sCurrentProject);
			// Now export all the models.
			TreeModel treeModel;
			AbstractExportableNode root;
			for (int i = 0; i < models.size(); i++) {
				treeModel = (TreeModel) models.get(i);
				root = (AbstractExportableNode) treeModel.getRoot();
				this.writeNode(root, treeModel, location); // $NON-NLS-1$
			}
			this.writer.endElement("CompleteReport");
			this.writer.endDocument();
		} catch (final Exception e) {
			throw new ExporterException(
					RM.getString("ERR_WRITING_FILE") + "Result_" + sCurrentProject + this.getFileExtension(), e);
		} finally {
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (final IOException e) {
					AppLogger.getLogger().log(AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); // $NON-NLS-1$
				}
			}
		}
		this.node = null;// to remove unwanted reference after the file has
	}

	private void writeNode(AbstractExportableNode node, TreeModel treeModel, String reportsLocation)
			throws ExporterException, IOException {
		this.node = node;
		String nodeName = this.node.getViewId();
		this.writer.startElement(nodeName, "Name", this.node.getDisplayName());

		// export node's items.
		final String originalPath = node.getFilePath();
		node.setFilePath(reportsLocation);
		this.exportItems(node.getItems());
		node.setFilePath(originalPath);
		// export its children now
		final int n = treeModel.getChildCount(node);
		AbstractExportableNode childNode;
		for (int j = 0; j < n; j++) {
			childNode = (AbstractExportableNode) treeModel.getChild(node, j);
			this.writeNode(childNode, treeModel, reportsLocation); // $NON-NLS-1$
		}
		this.writer.endElement(nodeName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.queryio.sysmoncommon.exporter.AbstractExporter#exportNode(com.queryio
	 * .sysmoncommon.exporter.dstruct.IExportableNode)
	 */
	public void exportNode(final AbstractExportableNode nodeToExport) throws ExporterException {
		this.node = nodeToExport;
		BufferedWriter bufferedWriter = null;
		try {
			final File file = new File(this.node.getFilePath(),
					this.node.getExportedFileName() + this.getFileExtension());
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")); //$NON-NLS-1$
			this.writer = new XMLWriter(bufferedWriter);
			this.writer.startDocument(); // Write the <XML tag

			this.writer.startElement("Report", "Name", this.node.getDisplayName());
			this.exportNodeItems();
			this.writer.endElement("Report");
			this.writer.endDocument();
		} catch (final Exception e) {
			throw new ExporterException(RM.getString("ERR_WRITING_FILE") + nodeToExport.getExportedFileName() //$NON-NLS-1$
					+ this.getFileExtension(), e);
		} finally {
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (final IOException e) {
					AppLogger.getLogger().log(AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); // $NON-NLS-1$
				}
			}
		}
		this.node = null;// to remove unwanted reference after the file has
		// been written
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio
	 * .sysmoncommon.exporter.dstruct.Group)
	 */
	protected void exportItem(final Group group) throws ExporterException {
		if (!group.isXmlExport())
			return;
		this.exportItems(group.getItems());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio
	 * .sysmoncommon.exporter.dstruct.TabFolder)
	 */
	protected void exportItem(final TabFolder tabFolder) throws ExporterException {
		if (!tabFolder.isXmlExport())
			return;

		final IExportableItem[][] tabs = tabFolder.getItems();
		if (tabs != null) {
			for (int i = 0; i < tabs.length; i++) {
				this.exportItems(tabs[i]);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio
	 * .sysmoncommon.exporter.dstruct.Table)
	 */
	protected void exportItem(final Table table) throws ExporterException {
		if (!table.isXmlExport())
			return;
		this.exportTableModel(table.getModel(), table.getTableHeader());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio
	 * .sysmoncommon.exporter.dstruct.Tree)
	 */
	protected void exportItem(final Tree tree) throws ExporterException {
		if (!tree.isXmlExport())
			return;

		this.exportTreeModel(tree.getModel());
		// exportTableModel(convertTreeModelToTableModel(tree.getModel()));
	}

	private void writeTreeNode(final TreeNode node) throws Exception {
		if (node != null) {
			this.writer.startElement("TreeNode");
			if (node instanceof IAbstractExportableTreeNode) {
				this.writer.writeCData(((IAbstractExportableTreeNode) node).toExportString()); // $NON-NLS-1$
			} else {
				this.writer.writeCData(node.toString()); // $NON-NLS-1$
			}

			// Add all the child nodes of this node
			final int iChildCount = node.getChildCount();
			if (iChildCount > 0) {
				for (int i = 0; i < iChildCount; i++) {
					this.writeTreeNode(node.getChildAt(i));
				}
			}
			this.writer.endElement("TreeNode");
		}
	}

	private void exportTreeModel(final TreeModel model) {
		if (model instanceof IAbstractExportableTreeModel) {
			final TreeNode root = (TreeNode) model.getRoot();
			if (root != null) {
				try {
					this.writeTreeNode(root);
				} catch (final Exception e) {
					AppLogger.getLogger().log(AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); // $NON-NLS-1$
				}
			}
		} else {
			this.exportTableModel(this.convertTreeModelToTableModel(model), null);
		}
	}

	/**
	 * @param model
	 */
	private void exportTableModel(final TableModel model, String tableHeader) {
		String value = null;
		Object object = null;
		try {
			if (tableHeader != null) {
				this.writer.startElement("TableData", new String[] { "TableHeader", "NumRows", "NumColumns" },
						new String[] { tableHeader, String.valueOf(model.getRowCount()),
								String.valueOf(model.getColumnCount()) });
			} else {
				this.writer.startElement("TableData", new String[] { "NumRows", "NumColumns" },
						new String[] { String.valueOf(model.getRowCount()), String.valueOf(model.getColumnCount()) });
			}
			// write all rows
			final int numCols = model.getColumnCount();
			final int numRows = model.getRowCount();
			for (int i = 0; i < numRows; i++) {
				this.writer.startElement("TableRow", "RowCount", String.valueOf(i));
				for (int j = 0; j < numCols; j++) {
					value = model.getColumnName(j);
					object = model.getValueAt(i, j);
					this.writer.startElement("TableColumn", "Name", value != null ? value : "Column" + (i + 1));
					this.writer.writeCData(object != null ? object.toString() : "<null>"); //$NON-NLS-1$
					this.writer.endElement("TableColumn");
				}
				this.writer.endElement("TableRow");
			}
			this.writer.endElement("TableData");
		} catch (final Exception e) {
			AppLogger.getLogger().log(AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); // $NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio
	 * .sysmoncommon.exporter.dstruct.Chart)
	 */
	protected void exportItem(final Chart chart) throws ExporterException {
		// DO NOTHING
		// if (!chart.isXmlExport()) return;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio
	 * .sysmoncommon.exporter.dstruct.Label)
	 */
	protected void exportItem(final Label label) throws ExporterException {
		try {
			if (!label.isXmlExport())
				return;
			this.writer.startElement("Label");
			this.writer.writeCData(label.getText() != null ? label.getText() : "<null>"); //$NON-NLS-1$
			this.writer.endElement("Label");
		} catch (final Exception e) {
			AppLogger.getLogger().log(AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); // $NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio
	 * .sysmoncommon.exporter.dstruct.TextBox)
	 */
	protected void exportItem(final TextBox textBox) throws ExporterException {
		try {
			if (!textBox.isXmlExport())
				return;
			this.writer.startElement("TextBox");
			this.writer.writeCData(textBox.getText() != null ? textBox.getText() : "<null>"); //$NON-NLS-1$
			this.writer.endElement("TextBox");
		} catch (final Exception e) {
			AppLogger.getLogger().log(AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); // $NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio
	 * .sysmoncommon.exporter.dstruct.HTMLPage)
	 */
	protected void exportItem(final HTMLPage page) throws ExporterException {
		try {
			if (!page.isXmlExport())
				return;
			this.writer.startElement("Link");
			this.writer.writeCData(page.getFileName() != null ? page.getFileName() : "<null>"); //$NON-NLS-1$
			this.writer.endElement("Link");
		} catch (final Exception e) {
			AppLogger.getLogger().log(AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); // $NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio
	 * .sysmoncommon.exporter.dstruct.LabelTextPanel)
	 */
	protected void exportItem(final LabelTextPanel panel) throws ExporterException {
		if (panel != null) {
			if (!panel.isXmlExport())
				return;
			final IExportableItem[][] items = panel.getItems();
			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					this.exportItems(items[i]);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.queryio.sysmoncommon.exporter.AbstractExporter#exportItem(com.queryio
	 * .sysmoncommon.exporter.dstruct.Paragraph)
	 */
	protected void exportItem(final Paragraph para) throws ExporterException {
		if (!para.isXmlExport())
			return;
		final Label[] items = para.getItems();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < items.length; i++) {
			buffer.append(items[i].getText());
		}
		this.exportItem(new Label(para.getLeft(), para.getTop(), para.getWidth(), para.getHeight(), buffer.toString()));
	}

}