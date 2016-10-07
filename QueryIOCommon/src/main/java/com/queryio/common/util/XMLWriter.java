/*
 * @(#) XMLWriter.java 1.0 02/10/2002 (DD/MM/YYYY)
 *
 * Copyright (C) 2002- 2007 Exceed Consultancy Services. All rights reserved.
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
package com.queryio.common.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;


/**
 * This class is a utility class which provides methods to create an XML
 * document. It can also be used to write XML to any output stream wrapped which
 * is wrapped in a writer. In case you want to use this class to write XML to
 * network stream, then you might want to turn off the pretty printing i.e. the
 * use of tabs and new lines which are are used to indent the XML document with
 * the method <code>setPrettyPrint(false)</code>. You can also say
 * <code>setAutoClose(false)</code> so that the writer is not automatically
 * closed when the <code>endDocument()</code> method is called.
 * <P>
 * To use it, create an instance of XMLWriter :<BR>
 * <CODE>XMLWriter xml = new XMLWriter(new java.io.FileWriter("myxml,xml"));
 * </CODE>
 * </P>
 * <P>
 * The entire document should be written within <code>startDocument()</code>
 * and <code>endDocument()</code> methods. The following sample code:
 * </p>
 * <p>
 * <code>xml.startDocument()<br>
 * xml.writeElement("Greeting", "How are you?")<br>
 * xml.endDocument()
 * </code>
 * </p>
 * <p>
 * will generate the following XML document:
 * </p>
 * <p>
 * <code>
 * &lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;<BR>
 * &lt;greeting&gt;How are you?&lt;/greeting&gt;<BR>
 * </code>
 * </p>
 * 
 * @author Exceed Consultancy Services.
 * 
 * @date 1st October, 2002.
 * 
 * @version 1.0
 */
public class XMLWriter
{
	/** used to create the file, in this case buffering is done */
	private Writer writer = null;

	/**
	 * in this case sbuffer is not used, but bytes are written directly to
	 * stream
	 */
	private OutputStream out = null;

	/** for buffering data to be written */
	private StringBuffer sBuffer = null;

	/** for controlling the number of tabs for indentation */
	private int iElementLevel = 0;

	/** size of the buffer */
	private static final int MAX_BUFFER = 1024;

	/** message format related constants */
	private static final byte[] CRLF_BYTES = "\r\n".getBytes(); //$NON-NLS-1$
	private static final byte[] MIME_BOUNDARY_BYTES = "--MIME_boundary\r\n".getBytes(); //$NON-NLS-1$
	private static final byte[] FINAL_MIME_BOUNDARY_BYTES = "--MIME_boundary--\r\n".getBytes(); //$NON-NLS-1$

	/** some constants */
	private static final String CLOSE_LT = "</"; //$NON-NLS-1$
	private static final String CLOSE_RT = " />"; //$NON-NLS-1$
	private static final char TAB = '\t';
	private static final char LF = '\n';

	/** predefined XML entities constants */
	private static final char LESS_THAN = '<';
	private static final char GREATER_THAN = '>';
	private static final char AMPERSAND = '&';
	private static final char APOSTROPHE = '\'';
	private static final char QUOTATION = '"';
	private static final String COMMENT_START = "<!--"; //$NON-NLS-1$
	private static final String COMMENT_END = "-->"; //$NON-NLS-1$

	/** encoded XML entities constants */
	private static final String EN_LESS_THAN = "&lt;"; //$NON-NLS-1$
	private static final String EN_GREATER_THAN = "&gt;"; //$NON-NLS-1$
	private static final String EN_AMPERSAND = "&amp;"; //$NON-NLS-1$
	private static final String EN_APOSTROPHE = "&apos;"; //$NON-NLS-1$
	private static final String EN_QUOTATION = "&quot;"; //$NON-NLS-1$

	/** flag to know when to write new line and tabs */
	private boolean bBodyChars = false;

	/** flag to know whether to encode the given string or not. */
	private static final boolean bEncode = true;

	/** to pretty indent the XML document */
	private boolean bPrettyPrint = true;

	/** whether to close the writer when endDocument is called */
	private boolean bAutoClose = true;

	/**
	 * this is the default constructor which creates a <code>XMLWriter</code>.
	 * Use the <code>setWriter</code> method before using any of the writer
	 * methods.
	 */
	public XMLWriter()
	{
		// DO NOTHING
	}

	/**
	 * constructs a <code>XMLWriter</code> with the given parameters.
	 * 
	 * @param writer
	 *            an instance of java.io.Writer
	 */
	public XMLWriter(final Writer writerObj)
	{
		this.writer = writerObj;
	}

	/**
	 * constructs a <code>XMLWriter</code> with the given parameters.
	 * 
	 * @param writer
	 *            an instance of java.io.Writer
	 * @param bPrettyPrint
	 *            whether to pretty print the document.
	 * @param bAutoClose
	 *            whether to close the writer automatically.
	 */
	public XMLWriter(final Writer writerObj, final boolean prettyPrint, final boolean autoClose)
	{
		this.writer = writerObj;
		this.bPrettyPrint = prettyPrint;
		this.bAutoClose = autoClose;
	}

	/**
	 * constructs a <code>XMLWriter</code> with the given parameters.
	 * 
	 * @param writer
	 *            an instance of java.io.Writer
	 * @param bPrettyPrint
	 *            whether to pretty print the document.
	 * @param bAutoClose
	 *            whether to close the writer automatically.
	 */
	public XMLWriter(final OutputStream output, final boolean prettyPrint, final boolean autoClose)
	{
		this.out = output;
		this.bPrettyPrint = prettyPrint;
		this.bAutoClose = autoClose;
	}

	/**
	 * sets the reference of the writer to which XML will be written.
	 * 
	 * @param writer
	 *            the writer to which XML will be written.
	 */
	public void setWriter(final Writer writerObj)
	{
		this.writer = writerObj;
	}

	/**
	 * returns the reference of the writer to which XML will be written.
	 * 
	 * @return the writer to which XML will be written.
	 */
	public final Writer getWriter()
	{
		return this.writer;
	}

	/**
	 * whether to use indentation while writing the document. If you are writing
	 * the XML over a network stream, you might want to turn off pretty
	 * printing. The default status of pretty printing is true.
	 * 
	 * @param bPrettyPrint
	 *            whether to pretty print the document.
	 */
	public void setPrettyPrint(final boolean prettyPrint)
	{
		this.bPrettyPrint = prettyPrint;
	}

	/**
	 * sets whether to close the writer automatically when endDocument is
	 * called. If the writer is a socket outputstream, you might want to turn
	 * auto-close off, to keep the connection alive. The default status of
	 * auto-close is true.
	 * 
	 * @param bAutoClose
	 *            whether to close the writer automatically.
	 */
	public void setAutoClose(final boolean autoClose)
	{
		this.bAutoClose = autoClose;
	}

	/**
	 * writes the start of the document
	 */
	public void startDocument() throws IOException
	{
		this.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //$NON-NLS-1$
	}

	/**
	 * writes the start tag for an element, for eg : <Settings>
	 */
	public void startElement(final String elemName) throws IOException
	{
		this.iElementLevel++;

		if (this.bPrettyPrint)
		{
			// write new line
			this.write(LF);

			// write indent tabs
			for (int i = 1; i < this.iElementLevel; i++)
			{
				this.write(TAB);
			}
		}

		this.write(LESS_THAN);
		this.write(elemName);
		this.write(GREATER_THAN);
	}

	/**
	 * writes the start tag for an element along with attributes. The attributes
	 * are to be supplied in a <code>java.util.Hashtable</code> where both the
	 * key and values are <code>String</code> objects. The key will be
	 * considered as the attribute name and the value will be taken as attribute
	 * value. The attribute value will automatically xml escaped.
	 * 
	 * @param elemName
	 *            The name of the element to be started.
	 * @param attributes
	 *            A <code>Hashtable</code> of name and value pairs.
	 */
	public void startElement(final String elemName, final Hashtable attributes) throws IOException
	{
		this.iElementLevel++;

		if (this.bPrettyPrint)
		{
			// write new line
			this.write(LF);

			// write indent tabs
			for (int i = 1; i < this.iElementLevel; i++)
			{
				this.write(TAB);
			}
		}

		this.write(LESS_THAN);
		this.write(elemName);

		// write the attributes...
		if (attributes != null)
		{
			final Enumeration keys = attributes.keys();
			while (keys.hasMoreElements())
			{
				final String key = (String) keys.nextElement();
				final String value = (String) attributes.get(key);

				// write ' key="value"'
				this.write(' ');
				this.write(key);
				this.write('=');
				this.write(QUOTATION);
				if (bEncode)
				{
					this.write(xmlEncode(value));
				}
				else
				{
					this.write(value);
				}
				this.write(QUOTATION);
			}
		}

		this.write(GREATER_THAN);
	}

	/**
	 * writes the start tag for an element along with attributes. The attributes
	 * are to be supplied in a <code>java.util.Map</code> where both the key
	 * and values are <code>String</code> objects. The key will be considered
	 * as the attribute name and the value will be taken as attribute value. The
	 * attribute value will automatically xml escaped.
	 * 
	 * @param elemName
	 *            The name of the element to be started.
	 * @param attributes
	 *            A <code>Map</code> of name and value pairs.
	 */
	public void startElement(final String elemName, final Map attributes) throws IOException
	{
		this.startElement(elemName, new Hashtable(attributes));
	}

	/**
	 * writes the start tag for an element along with attributes. The attributes
	 * are to be supplied in a String array format. There will be an array for
	 * keys and an array for values. Its assumed that one to one mapping between
	 * key and value pairs is present. The key will be considered as the
	 * attribute name and the value will be taken as attribute value. The
	 * attribute value will automatically xml escaped.
	 * 
	 * @param elemName
	 *            The name of the element to be started.
	 * @param keys
	 *            Array of key attributes.
	 * @param values
	 *            Array of attribute values.
	 */
	public void startElement(final String elemName, final String[] keys, final String[] values) throws IOException
	{
		this.startElement(elemName, keys, values, keys.length);
	}

	/**
	 * writes the full tag for an element along with attributes. It also closes
	 * the tag at the end that is there will be no separate end tag The
	 * attributes are to be supplied in a String array format. There will be an
	 * array for keys and an array for values. Its assumed that one to one
	 * mapping between key and value pairs is present. The key will be
	 * considered as the attribute name and the value will be taken as attribute
	 * value. The attribute value will automatically xml escaped.
	 * 
	 * @param elemName
	 *            The name of the element to be started.
	 * @param keys
	 *            Array of key attributes.
	 * @param values
	 *            Array of attribute values.
	 */
	public void writeElement(final String elemName, final String[] keys, final String[] values) throws IOException
	{
		this.writeElement(elemName, keys, values, keys.length);
	}

	/**
	 * writes the start tag for an element along with attributes. The attributes
	 * are to be supplied in a String array format. There will be an array for
	 * keys and an array for values. Its assumed that one to one mapping between
	 * key and value pairs is present. The key will be considered as the
	 * attribute name and the value will be taken as attribute value. The
	 * attribute value will automatically xml escaped.
	 * 
	 * @param elemName
	 *            The name of the element to be started.
	 * @param keys
	 *            Array of key attributes.
	 * @param values
	 *            Array of attribute values.
	 * @param len
	 *            Number of Attributes to be taken from the array
	 */
	public void startElement(final String elemName, final String[] keys, final String[] values, final int len)
			throws IOException
	{
		this.iElementLevel++;

		if (this.bPrettyPrint)
		{
			// write new line
			this.write(LF);

			// write indent tabs
			for (int i = 1; i < this.iElementLevel; i++)
			{
				this.write(TAB);
			}
		}

		this.write(LESS_THAN);
		this.write(elemName);

		// write the attributes...
		if (keys != null)
		{
			for (int i = 0; i < len; i++)
			{
				final String key = keys[i];
				final String value = values[i];
				// write ' key="value"'
				if (value != null)
				{
					this.write(' ');
					this.write(key);
					this.write('=');
					this.write(QUOTATION);
					if (bEncode)
					{
						this.write(xmlEncode(value));
					}
					else
					{
						this.write(value);
					}
					this.write(QUOTATION);
				}
			}
		}

		this.write(GREATER_THAN);
	}

	/**
	 * writes the full tag for an element along with attributes. It also closes
	 * the tag at the end that is there will be no separate end tag The
	 * attributes are to be supplied in a String array format. There will be an
	 * array for keys and an array for values. Its assumed that one to one
	 * mapping between key and value pairs is present. The key will be
	 * considered as the attribute name and the value will be taken as attribute
	 * value. The attribute value will automatically xml escaped.
	 * 
	 * @param elemName
	 *            The name of the element to be started.
	 * @param keys
	 *            Array of key attributes.
	 * @param values
	 *            Array of attribute values.
	 * @param len
	 *            Number of Attributes to be taken from the array
	 */
	public void writeElement(final String elemName, final String[] keys, final String[] values, final int len)
			throws IOException
	{
		this.iElementLevel++;

		if (this.bPrettyPrint)
		{
			// write new line
			this.write(LF);

			// write indent tabs
			for (int i = 1; i < this.iElementLevel; i++)
			{
				this.write(TAB);
			}
		}

		this.write(LESS_THAN);
		this.write(elemName);

		// write the attributes...
		if (keys != null)
		{
			for (int i = 0; i < len; i++)
			{
				final String key = keys[i];
				final String value = values[i];
				if (value != null)
				{
					this.write(' ');
					this.write(key);
					this.write('=');
					this.write(QUOTATION);
					if (bEncode)
					{
						this.write(xmlEncode(value));
					}
					else
					{
						this.write(value);
					}
					this.write(QUOTATION);
				}
			}
		}

		this.write(CLOSE_RT);

		this.iElementLevel--;
		this.bBodyChars = false;
	}

	/**
	 * writes the start tag for an element along with attribute. The attribute
	 * is to be supplied in the String format. There will be a String for keys
	 * and a String for value. Its assumed that one to one mapping between key
	 * and value pair is present. The key will be considered as the attribute
	 * name and the value will be taken as attribute value. The attribute value
	 * will automatically xml escaped.
	 * 
	 * @param elemName
	 *            The name of the element to be started.
	 * @param key
	 *            String key attributes.
	 * @param value
	 *            String attribute values.
	 */
	public void startElement(final String elemName, final String key, final String value) throws IOException
	{
		this.iElementLevel++;

		if (this.bPrettyPrint)
		{
			// write new line
			this.write(LF);
			// write indent tabs
			for (int i = 1; i < this.iElementLevel; i++)
			{
				this.write(TAB);
			}
		}

		this.write(LESS_THAN);
		this.write(elemName);

		// write the attributes...
		if ((key != null) && (value != null))
		{
			// write ' key="value"'
			this.write(' ');
			this.write(key);
			this.write('=');
			this.write(QUOTATION);
			if (bEncode)
			{
				this.write(xmlEncode(value));
			}
			else
			{
				this.write(value);
			}
			this.write(QUOTATION);
		}
		this.write(GREATER_THAN);
	}

	/**
	 * writes the full tag for an element along with attributes. It also closes
	 * the tag at the end so there is no separate end tag The attribute is to be
	 * supplied in the String format. There will be a String for key and a
	 * String for value. Its assumed that one to one mapping between key and
	 * value pair is present. The key will be considered as the attribute name
	 * and the value will be taken as attribute value. The attribute value will
	 * automatically xml escaped.
	 * 
	 * @param elemName
	 *            The name of the element to be started.
	 * @param key
	 *            String key attribute.
	 * @param value
	 *            String attribute value.
	 */
	public void writeElement(final String elemName, final String key, final String value) throws IOException
	{
		this.iElementLevel++;

		if (this.bPrettyPrint)
		{
			// write new line
			this.write(LF);

			// write indent tabs
			for (int i = 1; i < this.iElementLevel; i++)
			{
				this.write(TAB);
			}
		}

		this.write(LESS_THAN);
		this.write(elemName);

		// write the attributes...
		if ((key != null) && (value != null))
		{
			// write ' key="value"'
			this.write(' ');
			this.write(key);
			this.write('=');
			this.write(QUOTATION);
			if (bEncode)
			{
				this.write(xmlEncode(value));
			}
			else
			{
				this.write(value);
			}
			this.write(QUOTATION);
		}
		this.write(CLOSE_RT);

		this.iElementLevel--;
		this.bBodyChars = false;
	}

	/**
	 * Method characters.
	 * 
	 * @param str
	 * @throws IOException
	 */
	public void characters(final String str) throws IOException
	{
		this.write(xmlEncode(str));
		this.bBodyChars = true;
	}

	/**
	 * writes the end tag for an element, for eg : </Settings>
	 */
	public void endElement(final String elemName) throws IOException
	{
		if (this.bPrettyPrint)
		{
			// only write a new line and indent tabs
			// when closing a parent element without body characters
			if (!this.bBodyChars)
			{
				this.write(LF);

				// write indent tabs
				for (int i = 1; i < this.iElementLevel; i++)
				{
					this.write(TAB);
				}
			}
		}

		this.write(CLOSE_LT);
		this.write(elemName);
		this.write(GREATER_THAN);

		this.iElementLevel--;
		this.bBodyChars = false;
	}

	/**
	 * writes an element with String content. for eg: <Name>Group1</Name>
	 */
	public void writeElement(final String elemName, final String characters) throws IOException
	{
		if (characters != null)
		{
			this.startElement(elemName);
			this.characters(characters);
			this.endElement(elemName);
		}
	}

	/**
	 * writes an element with String content. for eg: <Name>Group1</Name>
	 */
	public void writeElement(final String elemName, final Hashtable attributes, final String characters)
			throws IOException
	{
		this.startElement(elemName, attributes);
		this.characters(characters);
		this.endElement(elemName);
	}

	/**
	 * writes an empty element. for eg: <IsServiceRunning />
	 */
	public void writeEmptyElement(final String elemName) throws IOException
	{
		this.iElementLevel++;

		if (this.bPrettyPrint)
		{
			// write new line
			this.write(LF);

			// write indent tabs
			for (int i = 1; i < this.iElementLevel; i++)
			{
				this.write(TAB);
			}
		}

		this.write(LESS_THAN);
		this.write(elemName);
		this.write(CLOSE_RT);

		this.iElementLevel--;
		this.bBodyChars = false;
	}

	/**
	 * call this method to specify end of document. It flushes the buffer and
	 * closes the writer if auto-close is on.
	 */
	public void endDocument() throws IOException
	{
		this.flush();
		if (this.bAutoClose)
		{
			this.close();
		}
	}

	/**
	 * you will not need to call this if the autoclose flag is set to true while
	 * creating this writer.
	 */
	public void close() throws IOException
	{
		if (this.writer != null)
		{
			this.flush();
			this.writer.close();
		}
		else if (this.out != null)
		{
			this.out.close();
		}
	}

	// /////////// OUTPUTSTREAM SPECIFIC API : START ///////////////

	/**
	 * OutputStream specific : Note: this method should only be used in case the
	 * instance has been created using the OutputStream constructor. You will
	 * never need to and should not call this if you are using the
	 * java.io.Writer version constructor.
	 */
	public void write(final byte[] bytes) throws IOException
	{
		this.write(bytes, 0, bytes.length);
	}

	/**
	 * OutputStream specific : Note: this method should only be used in case the
	 * instance has been created using the OutputStream constructor. You will
	 * never need to and should not call this if you are using the
	 * java.io.Writer version constructor.
	 */
	public void write(final byte[] bytes, final int offset, final int len) throws IOException
	{
		if (this.out != null)
		{
			this.out.write(bytes, offset, len);
			this.out.flush();
		}
	}

	/**
	 * Note: OutputStream specific : writes the end of mime boundary to the
	 * outputstream
	 */
	public void endBoundary() throws IOException
	{
		this.write(MIME_BOUNDARY_BYTES);
	}

	/**
	 * Note: OutputStream specific : writes a mime header to the outputstream
	 */
	public void writeHeader(final String headerName, final String headerValue) throws IOException
	{
		this.write(headerName);
		this.write(": "); //$NON-NLS-1$
		this.write(headerValue);
		this.write(CRLF_BYTES);
	}

	/**
	 * Note: OutputStream specific : writes a CRLF to denote end of mime headers
	 */
	public void endHeaders() throws IOException
	{
		this.write(CRLF_BYTES);
	}

	/**
	 * Note: OutputStream specific : writes the final mime boundary to the
	 * outputstream You should not write anything further to the stream, once
	 * this method is called.
	 */
	public void finalBoundary() throws IOException
	{
		this.write(FINAL_MIME_BOUNDARY_BYTES);
	}

	// /////////// OUTPUTSTREAM SPECIFIC API : END ///////////////

	/**
	 * writes the string to the buffer. If the buffer has reached the maximum
	 * size, the contents of the buffer are written to the stream and buffer is
	 * re-initialized. This method will not do anything if the parameter is null
	 * or empty.
	 */
	public void write(final String str) throws IOException
	{
		// in case u are writing to stream, dont buffer...
		if (this.out != null)
		{
			this.out.write(str.getBytes());
			this.out.flush();
			return;
		}

		if ((str == null) || (str.length() == 0))
		{
			return;
		}

		if (this.sBuffer == null)
		{
			this.sBuffer = new StringBuffer(MAX_BUFFER);
		}

		if (this.sBuffer.length() + str.length() >= MAX_BUFFER)
		{
			this.flush();
		}

		this.sBuffer.append(str);
	}

	/**
	 * writes the char to the buffer. A convenient overloaded method.
	 */
	private void write(final char ch) throws IOException
	{
		this.write(String.valueOf(ch));
	}

	/**
	 * writes the contents of the buffer to the stream and re-initializes the
	 * buffer.
	 */
	private void flush() throws IOException
	{
		if (this.writer != null)
		{
			this.writer.write(this.sBuffer.toString());
			this.writer.flush();
			this.sBuffer = new StringBuffer(MAX_BUFFER);
		}
		if (this.out != null)
		{
			this.out.flush();
		}
	}

	/**
	 * Method xmlEncode.
	 * 
	 * @param str
	 * @return String
	 */
	public static String xmlEncode(final String str)
	{
		if (str == null)
		{
			return null;
		}

		if (str.length() == 0)
		{
			return "";
		}

		final StringBuffer sbEncode = new StringBuffer(str.length() * 2);
		final char chrarry[] = str.toCharArray();
		for (int i = 0; i < chrarry.length; i++)
		{
			final char ch = chrarry[i];
			switch (ch)
			{
				case LESS_THAN:
					sbEncode.append(EN_LESS_THAN);
					break;
				case GREATER_THAN:
					sbEncode.append(EN_GREATER_THAN);
					break;
				case AMPERSAND:
					sbEncode.append(EN_AMPERSAND);
					break;
				case APOSTROPHE:
					sbEncode.append(EN_APOSTROPHE);
					break;
				case QUOTATION:
					sbEncode.append(EN_QUOTATION);
					break;
				case 9: // $IGN_Use_break_for_each_case$
				case 10: // $IGN_Use_break_for_each_case$
				case 13:
					sbEncode.append("&#x"); //$NON-NLS-1$
					sbEncode.append(Integer.toHexString(ch));
					sbEncode.append(";"); //$NON-NLS-1$
					break;
				default:
					sbEncode.append(ch);
					break;
			}
		}

		return sbEncode.toString();
	}

	/**
	 * Method writeCData.
	 * 
	 * @param string
	 */
	public void writeCData(final String data) throws IOException
	{
		this.writeCData(data, true);
	}

	/**
	 * Method writeCData.
	 * 
	 * @param string
	 */
	public void writeCData(final String data, final boolean checkForTrim) throws IOException
	{
		if (data != null)
		{
			if (checkForTrim && (data.trim().length() == 0))
			{
				return;
			}
			this.write("<![CDATA["); //$NON-NLS-1$
			this.write(data);
			// write(LF);
			this.write("]]>"); //$NON-NLS-1$
			this.bBodyChars = true;
		}
	}

	/**
	 * 
	 */
	public void setElementLevel(final int level)
	{
		this.iElementLevel = level;
	}

	/**
	 * Method writeComments
	 * 
	 * @param comments
	 * @throws IOException
	 */
	public void writeComments(final ArrayList comments) throws IOException
	{
		if (this.bPrettyPrint)
		{
			// write new line
			this.write(LF);

			// write indent tabs
			for (int i = 1; i < this.iElementLevel; i++)
			{
				this.write(TAB);
			}
		}
		this.write(COMMENT_START);

		for (int cnt = 0; cnt < comments.size(); cnt++)
		{
			if (this.bPrettyPrint)
			{
				// write new line
				this.write(LF);

				// write indent tabs
				for (int i = 1; i < this.iElementLevel; i++)
				{
					this.write(TAB);
				}
			}
			this.write(comments.get(cnt).toString());
		}

		if (this.bPrettyPrint)
		{
			// write new line
			this.write(LF);

			// write indent tabs
			for (int i = 1; i < this.iElementLevel; i++)
			{
				this.write(TAB);
			}
		}
		this.write(COMMENT_END);

		if (this.bPrettyPrint)
		{
			// write new line
			this.write(LF);
		}
	}
}
