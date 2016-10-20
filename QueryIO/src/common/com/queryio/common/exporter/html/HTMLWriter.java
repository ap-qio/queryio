/*
 * @(#)  HTMLWriter.java May 25, 2005
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
package com.queryio.common.exporter.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.queryio.common.exporter.ExportManager;
import com.queryio.common.exporter.exceptions.ExporterException;
import com.queryio.common.util.ResourceManager;
import com.queryio.common.util.StaticUtilities;

/**
 * HTMLWriter Create an instance of this class for writing one HTML file. Incase
 * you need to reuse this instance call the reInitialize method. Use the
 * startTag, endTag, writeTag and writeText methods for adding content to the
 * file. Finally give a call to the writeFile method to get the required file.
 * 
 * @author Exceed Consultancy Services
 * @version 5.5
 */
public class HTMLWriter
{
	private static final transient ResourceManager RM = ExportManager.RM;

	// Characters used
	private static final char NEWLINE = '\n';
//	private static final char TAB = '\t';
	private static final char GT = '>';
	private static final char LT = '<';
	private static final char EQUAL = '=';
	private static final char SLASH = '/';
	private static final char QUOTE = '"';
	private static final char SPACE = ' ';

	private final StringBuffer buffer = new StringBuffer();
	private final StringBuffer tempBuffer = new StringBuffer();
	private int numTabs;
	private boolean bTagisTD = false;
	private final String sTagTD = "TD";

	/**
	 * Constructor
	 * 
	 */
	HTMLWriter()
	{
		this.init();
	}

	/**
	 * Method reInitialize
	 * 
	 */
	void reInitialize()
	{
		this.init();
	}

	/**
	 * Method startTag
	 * 
	 * @param sTagName
	 */
	void startTag(final String sTagName)
	{
		this.startTag(sTagName, null, null);
	}

	/**
	 * Method startTag
	 * 
	 * @param sTagName
	 * @param attrs
	 */
	void startTag(final String sTagName, final String[] attrs, final String[] values)
	{
		this.tempBuffer.setLength(0);
//		this.addTabs(this.tempBuffer);
		this.tempBuffer.append(LT);
		this.tempBuffer.append(sTagName);
		this.addAttributes(this.tempBuffer, attrs, values);
		this.tempBuffer.append(GT);
		if (sTagName.equals(this.sTagTD))
		{
			this.bTagisTD = true;
		}
		else
		{
			this.tempBuffer.append(NEWLINE);
		}
		this.buffer.append(this.tempBuffer);

		this.numTabs++;
	}

	/**
	 * Method endTag
	 * 
	 * @param sTagName
	 */
	void endTag(final String sTagName)
	{
		this.numTabs--;
		this.tempBuffer.setLength(0);
		if (sTagName.equals(this.sTagTD))
		{
			this.bTagisTD = false;
		}
		else
		{
//			this.addTabs(this.tempBuffer);
		}
		this.tempBuffer.append(LT);
		this.tempBuffer.append(SLASH);
		this.tempBuffer.append(sTagName);
		this.tempBuffer.append(GT);
		this.tempBuffer.append(NEWLINE);

		this.buffer.append(this.tempBuffer);
	}

	/**
	 * Method writeTag
	 * 
	 * @param sTagName
	 * @param attrs
	 * @param values
	 */
	void writeTag(final String sTagName, final String[] attrs, final String[] values)
	{
		this.tempBuffer.setLength(0);
//		this.addTabs(this.tempBuffer);
		this.tempBuffer.append(LT);
		this.tempBuffer.append(sTagName);
		this.addAttributes(this.tempBuffer, attrs, values);
		this.tempBuffer.append(SLASH);
		this.tempBuffer.append(GT);
		this.tempBuffer.append(NEWLINE);

		this.buffer.append(this.tempBuffer);
	}

	/**
	 * Method writeText
	 * 
	 * @param text
	 */
	void writeText(final String text)
	{
		this.tempBuffer.setLength(0);
		if (!this.bTagisTD)
		{
//			this.addTabs(this.tempBuffer);
			this.tempBuffer.append(StaticUtilities.htmlEncode(text));
			this.tempBuffer.append(NEWLINE);
		}
		else
		{
			this.tempBuffer.append(StaticUtilities.htmlEncode(text));
		}
		this.buffer.append(this.tempBuffer);
	}

	/**
	 * Method writePlainTextLine
	 * 
	 * @param text
	 */
	void writePlainTextLine(final String text)
	{
		this.writePlainTextLine(text, true);
	}

	/**
	 * Method writePlainTextLine
	 * 
	 * @param text
	 */
	void writePlainTextLine(final String text, final boolean addNewLine)
	{
		this.tempBuffer.setLength(0);
//		this.addTabs(this.tempBuffer);
		this.tempBuffer.append(text);
		this.tempBuffer.append(NEWLINE);

		this.buffer.append(this.tempBuffer);
	}

	/**
	 * Method writeFile
	 * 
	 * @param file
	 */
	void writeFile(final String path, final String fileName) throws ExporterException
	{
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(new File(path, fileName)));
			writer.write(this.buffer.toString());
		}
		catch (final Throwable e)
		{
			throw new ExporterException(RM.getString("ERR_WRITING_FILE") + fileName, e); //$NON-NLS-1$
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			}
			catch (final Throwable e)
			{
				throw new ExporterException(RM.getString("ERR_WRITING_FILE") + fileName, e); //$NON-NLS-1$
			}
		}
	}

	// PRIVATE METHODS

	/**
	 * Method init
	 * 
	 */
	private void init()
	{
		this.buffer.setLength(0);
		this.numTabs = 0;
	}

	/**
	 * Method addAttributes
	 * 
	 * @param buff
	 * @param attrs
	 * @param values
	 */
	private void addAttributes(final StringBuffer buff, final String[] attrs, final String[] values)
	{
		int len1, len2 = 0;
		if ((attrs != null) && ((len1 = attrs.length) > 0)) // $IGN_Avoid_assignment_in_if$
		{
			if ((values != null) && ((len2 = values.length) > 0)) // $IGN_Avoid_assignment_in_if$
			{
				for (int i = 0; i < len2; i++)
				{
					buff.append(SPACE);
					buff.append(attrs[i]);
					buff.append(EQUAL);
					buff.append(QUOTE);
					buff.append(values[i]);
					buff.append(QUOTE);
					len1--;
				}
			}
			for (int i = 0; i < len1; i++)
			{
				buff.append(SPACE);
				buff.append(attrs[len2 + i]);
			}
		}
	}

	/**
	 * Method addTabs
	 * 
	 * @param buff
	 */
//	private void addTabs(final StringBuffer buff)
//	{
//		 for (int i = 0; i < numTabs; i++)
//		 {
//			 buff.append(TAB);
//		 }
//	}
}
