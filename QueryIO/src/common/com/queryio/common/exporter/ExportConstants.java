/*
 * @(#)  ExportConstants.java May 25, 2005
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

import java.util.StringTokenizer;

/**
 * ExportConstants
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public abstract class ExportConstants
{
	// String representation for export types
	public static final String[] EXPORT_TYPES = { "HTML", "PDF", "CSV", "XLS", "XML" }; //$NON-NLS-1$ //$NON-NLS-2$

	// Constants for type of export
	public static final int EXPORT_TYPE_HTML = 0;
	public static final int EXPORT_TYPE_PDF = 1;
	public static final int EXPORT_TYPE_CSV = 2;
	public static final int EXPORT_TYPE_XLS = 3;
	public static final int EXPORT_TYPE_XML = 4;

	// Contants for type of items that can be exported
	public static final int ITEM_TYPE_GROUP = 0;
	public static final int ITEM_TYPE_TABLE = 1;
	public static final int ITEM_TYPE_CHART = 2;
	public static final int ITEM_TYPE_TREE = 3;
	public static final int ITEM_TYPE_LABEL = 4;
	public static final int ITEM_TYPE_TEXTBOX = 5;
	public static final int ITEM_TYPE_HTMLPAGE = 6;
	public static final int ITEM_TYPE_TABFOLDER = 7;
	public static final int ITEM_TYPE_LABELTEXTPANEL = 8;
	public static final int ITEM_TYPE_PARAGRAH = 9;

	/**
	 * Method getFileExtension
	 * 
	 * @param exportType
	 * @return
	 */
	public static final String getFileExtension(final int exportType)
	{
		switch (exportType)
		{
			case EXPORT_TYPE_HTML:
			{
				return "html"; //$NON-NLS-1$
			}
			case EXPORT_TYPE_PDF:
			{
				return "pdf"; //$NON-NLS-1$
			}
			case EXPORT_TYPE_CSV:
			{
				return "csv"; //$NON-NLS-1$
			}
			case EXPORT_TYPE_XLS:
			{
				return "xls"; //$NON-NLS-1$
			}
			case EXPORT_TYPE_XML:
			{
				return "xml"; //$NON-NLS-1$
			}
		}
		return null;
	}

	/**
	 * Method getFileExtensionWithDot
	 * 
	 * @param exportType
	 * @return
	 */
	public static final String getFileExtensionWithDot(final int exportType)
	{
		return '.' + getFileExtension(exportType);
	}

	/**
	 * Method getOutputFormats
	 * 
	 * @param sOutputFormats
	 * @return
	 */
	public static final int[] getOutputFormats(final String sOutputFormats)
	{
		int[] aFormats = null;
		int iNumFormats = 0;
		if (sOutputFormats != null)
		{
			final StringTokenizer stFormats = new StringTokenizer(sOutputFormats, ","); //$NON-NLS-1$
			aFormats = new int[stFormats.countTokens()];
			String sOutputFormat = null;
			while (stFormats.hasMoreTokens())
			{
				sOutputFormat = stFormats.nextToken();
				if (sOutputFormat.equalsIgnoreCase(EXPORT_TYPES[EXPORT_TYPE_HTML]))
				{
					aFormats[iNumFormats++] = EXPORT_TYPE_HTML;
				}
				else if (sOutputFormat.equalsIgnoreCase(EXPORT_TYPES[EXPORT_TYPE_PDF]))
				{
					aFormats[iNumFormats++] = EXPORT_TYPE_PDF;
				}
				else if (sOutputFormat.equalsIgnoreCase(EXPORT_TYPES[EXPORT_TYPE_XLS]))
				{
					aFormats[iNumFormats++] = EXPORT_TYPE_XLS;
				}
				else if (sOutputFormat.equalsIgnoreCase(EXPORT_TYPES[EXPORT_TYPE_XML]))
				{
					aFormats[iNumFormats++] = EXPORT_TYPE_XML;
				}
				else if (sOutputFormat.equalsIgnoreCase(EXPORT_TYPES[EXPORT_TYPE_CSV]))
				{
					aFormats[iNumFormats++] = EXPORT_TYPE_CSV;
				}
			}
		}
		return aFormats;
	}
}
