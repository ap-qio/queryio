/*
 * @(#)  AbstractExportableNode.java Jun 2, 2005
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

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.exporter.ExportConstants;

/**
 * AbstractExportableNode
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public abstract class AbstractExportableNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 426177440101917221L;
	private String sExportedFileName;
	private String filePath = QueryIOConstants.EMPTY_STRING;
	private int imageId = -1;
	private String displayName;
	private String sViewId;

	/**
	 * Constructor
	 * 
	 * @param object
	 *            : The object to be stored in the DefaultMutableTreeNode
	 * @param imageId
	 *            : The integer id for the image to be shown next to the node in
	 *            the tree. This can be changed later on using the
	 *            setImageId(int) method.
	 * @param displayName
	 *            : The text that appears next to the image of the node in the
	 *            tree. This can be changed later using the
	 *            setDisplayName(String) method.
	 * @param exportedFileName
	 *            : The default file name for saving this node. This can be
	 *            changed later using the setFileName(String) method. This name
	 *            is without the file extension.
	 */
	protected AbstractExportableNode(final Object object, final int imageId, final String displayName,
			final String exportedFileName, final String viewId) {
		super(object);
		this.imageId = imageId;
		this.displayName = displayName;
		this.sExportedFileName = exportedFileName;
		this.sViewId = viewId;
		if (exportedFileName != null) {
			this.sExportedFileName = this.fixFileName(this.sExportedFileName.replace(' ', '_'));
		}
	}

	/**
	 * @param fileName
	 * @return
	 */
	private String fixFileName(String fileName) {
		for (int i = 0; i < fileName.length(); i++) {
			final char temp = fileName.charAt(i);
			if (!Character.isUnicodeIdentifierPart(temp)) {
				fileName = fileName.replace(temp, '_');
			}
		}
		return fileName;
	}

	/**
	 * @return
	 */
	public String getExportedFileName() {
		return this.sExportedFileName;
	}

	/**
	 * @param fileName
	 */
	public final void setExportedFileName(final String fileName) {
		this.sExportedFileName = fileName;
	}

	/**
	 * @return
	 */
	public final String getFilePath() {
		return this.filePath;
	}

	/**
	 * @param filePath
	 */
	public final void setFilePath(final String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return
	 */
	public int getImageID() {
		return this.imageId;
	}

	/**
	 * @param iImageID
	 */
	public void setImageID(final int iImageID) {
		this.imageId = iImageID;
	}

	/**
	 * @return
	 */
	public String getViewId() {
		return this.sViewId;
	}

	/**
	 * @param viewId
	 */
	public void setViewId(final String viewId) {
		this.sViewId = viewId;
	}

	/**
	 * @return
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @param displayName
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @param exportType
	 * @return
	 */
	public final String getCompleteFileName(final int exportType) {
		return new File(this.filePath, this.sExportedFileName + ExportConstants.getFileExtensionWithDot(exportType))
				.getAbsolutePath();
	}

	/**
	 * @return
	 */
	public String[] getIdentifyingLabels() {
		return null;
	}

	public ReportHeader getReportHeader() {
		return null;
	}

	// Abstract methods
	public abstract IExportableItem[] getItems();

	public abstract String getHeaderText();
}