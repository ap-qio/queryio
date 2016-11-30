/*
 * @(#) BaseSystemInfoWriter.java
 *
 * Copyright (C) 2002 - 2004 Exceed Consultancy Services. All rights reserved
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Set;

import com.queryio.common.exporter.dstruct.BaseSystemInfo;
import com.queryio.common.util.PathFinder;
import com.queryio.common.util.XMLWriter;

/**
 * This class is used to write System and Default.xml files on disk. This class
 * is extended by each applications SystemWriter Class. Common things will be
 * written from here and application specific things will be written from
 * respective System Writer classes.
 * 
 * @author Exceed Consultancy Services
 * @version 1.0
 */
public abstract class BaseSystemInfoWriter {
	// VARIABLE DECLARATION - DO NOT DELETE
	protected BaseSystemInfo systemInfo;
	private static final transient String SYSTEMFILE = "System.xml"; //$NON-NLS-1$
	private static final transient String DEFAULTFILE = "default.xml"; //$NON-NLS-1$

	protected XMLWriter xmlWriterForSystem;
	protected XMLWriter xmlWriterForDefault;

	/**
	 * Constructor for BaseSystemInfoWriter.
	 */
	public BaseSystemInfoWriter(final BaseSystemInfo systemInfo) throws Exception {
		this.systemInfo = systemInfo;
		final BufferedWriter writerForSystemDetails = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(new File(PathFinder.getDevSuiteHome(), SYSTEMFILE)), "UTF-8")); //$NON-NLS-1$
		this.xmlWriterForSystem = new XMLWriter(writerForSystemDetails);
		this.xmlWriterForSystem.startDocument(); // Write the <XML tag

		if (this.getProductHome() != null) {
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(this.getProductHome(), DEFAULTFILE)), "UTF-8")); //$NON-NLS-1$
			this.xmlWriterForDefault = new XMLWriter(writer);
			this.xmlWriterForDefault.startDocument(); // Write the <XML tag
			this.xmlWriterForDefault.startElement(IBaseSystemTagConstants.PRODUCT_INFO);
		}
	}

	/**
	 * BaseSystemInfoWriter # getProductHome
	 * 
	 * @return
	 */
	protected abstract String getProductHome();

	/**
	 * BaseSystemInfoWriter # saveSystem
	 * 
	 * @throws Exception
	 */
	public final void saveSystem() throws Exception {
		this.saveDetailSettings();
		this.closeWriting();
	}

	/**
	 * Method saveSystem.
	 * 
	 * @throws Exception
	 */
	protected void saveDetailSettings() throws Exception {
		final String[] sysAttributeKeys = { IBaseSystemTagConstants.HIGHLIGHTONMOUSEMOVE };
		final String[] sysAttributeValues = { String.valueOf(this.systemInfo.isHighlightRowOnMouseOver()) };
		// String[] sysAttributeKeys =
		// { IBaseSystemTagConstants.WIDTH, IBaseSystemTagConstants.HEIGHT,
		// IBaseSystemTagConstants.HIGHLIGHTONMOUSEMOVE,
		// IBaseSystemTagConstants.STATUSBAR,
		// IBaseSystemTagConstants.OUTPUT_WINDOW,
		// IBaseSystemTagConstants.LEFT_PANE_WIDTH,
		// IBaseSystemTagConstants.X_POSITION,
		// IBaseSystemTagConstants.Y_POSITION,
		// };
		// String[] sysAttributeValues =
		// { String.valueOf(systemInfo.getWidth()),
		// String.valueOf(systemInfo.getHeight()),
		// String.valueOf(systemInfo.isHighlightRowOnMouseOver()),
		// String.valueOf(systemInfo.isStatusBarVisible()),
		// String.valueOf(systemInfo.isConsoleMessageWindowVisible()),
		// String.valueOf(systemInfo.getNavigationPaneWidth()),
		// String.valueOf(systemInfo.getX()), String.valueOf(systemInfo.getY()),
		// };
		this.xmlWriterForSystem.startElement(IBaseSystemTagConstants.SYSTEM, sysAttributeKeys, sysAttributeValues);

		// write font settings
		final boolean bSystemFont = this.systemInfo.isUsingSystemFont();
		if (bSystemFont) {
			this.xmlWriterForSystem.writeElement(IBaseSystemTagConstants.APPLICATION_FONT,
					IBaseSystemTagConstants.USING_DEFAULT, String.valueOf(true));
		} else {
			final String fontname = this.systemInfo.getCustomFontName();
			if (fontname != null) {
				final String[] fontSettingTags = { IBaseSystemTagConstants.USING_DEFAULT,
						IBaseSystemTagConstants.FONT_NAME, IBaseSystemTagConstants.FONT_SIZE,
						IBaseSystemTagConstants.FONT_STYLE, };
				final String[] fontSettingValues = { String.valueOf(false), fontname,
						String.valueOf(this.systemInfo.getCustomFontHeight()),
						String.valueOf(this.systemInfo.getCustomFontStyle()) };

				this.xmlWriterForSystem.writeElement(IBaseSystemTagConstants.APPLICATION_FONT, fontSettingTags,
						fontSettingValues);
			}
		}
		// Create JDK Path Element
		final String[] sysJDKAttributeKeys = { IBaseSystemTagConstants.NAME, IBaseSystemTagConstants.PATH,
				IBaseSystemTagConstants.VENDOR, IBaseSystemTagConstants.VERSION, IBaseSystemTagConstants.DEFAULT, };
		final Set jdkList = this.systemInfo.getJVMList();
		final Iterator iterator = jdkList.iterator();
		while (iterator.hasNext()) {
			final String sJdkName = (String) iterator.next();
			final String[] sysJDKAttributeValues = { sJdkName, this.systemInfo.getJVMPath(sJdkName),
					this.systemInfo.getJVMVendor(sJdkName), this.systemInfo.getJVMVersion(sJdkName),
					String.valueOf(this.systemInfo.isJVMDefault(sJdkName)) };
			this.xmlWriterForSystem.writeElement(IBaseSystemTagConstants.JDK, sysJDKAttributeKeys,
					sysJDKAttributeValues);
		}
	}

	/**
	 * closeWriting
	 * 
	 * @throws Exception
	 */
	private final void closeWriting() throws Exception {
		this.xmlWriterForSystem.endElement(IBaseSystemTagConstants.SYSTEM);
		this.xmlWriterForSystem.endDocument();
		if (this.xmlWriterForDefault != null) {
			this.xmlWriterForDefault.endElement(IBaseSystemTagConstants.PRODUCT_INFO);
			this.xmlWriterForDefault.endDocument();
		}
	}
}