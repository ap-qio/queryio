/*
 * @(#) StaticUtilities.java 	1.0 	Oct 01, 2002
 *
 * Copyright (C) 2002- 2004 Exceed Consultancy Services. All rights reserved.
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipOutputStream;

import jregex.Matcher;
import jregex.Pattern;
import jregex.Replacer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.queryio.common.IProductConstants;
import com.queryio.common.ImageConstants;
import com.queryio.common.QueryIOConstants;


/**
 * A utility class which provides various static utility functions.
 *
 * @author Exceed consultancy Services
 * @version 1.0 Sep 18, 2003
 */
@SuppressWarnings("PMD.AvoidUsingShortType")
public final class StaticUtilities
{
	private static Logger logger = Logger.getLogger(StaticUtilities.class);
	private static final int FIRST_USED_DEVSUITE_PORT = 8670;
	private static final String DEFUALT_FORMAT = "HH:mm:ss"; //$NON-NLS-1$
	private static final SimpleDateFormat SDF = new SimpleDateFormat();
	private static Date date = null;
	private static final DateFormat DTDATETIMEFORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
			DateFormat.MEDIUM);
	private static NumberFormat floatNumberFormat = null;
	private static NumberFormat intNumberFormat = null;

	public static final String[] BROWSER_TYPES = {"IE", "Mozilla/Firefox", "Safari", "Chrome", "Opera", 
		"iPhone","Custom"};
	
	/**
	 * Constant used to define browser test device
	 */
	public static final int TEST_DEVICE_BROWSER = 0;
	
	/**
	 * Constant used to define iPhone test device
	 */
	public static final int TEST_DEVICE_IPHONE = 1;
	
	/**
	 * Constant used to define Blackberry test device
	 */
	public static final int TEST_DEVICE_BLACKBERRY = 2;
	
	/**
	 * Constant used to define Android test device
	 */
	public static final int TEST_DEVICE_ANDROID = 3;
	
	/**
	 * Constant used to define Phone device horizontal orientation
	 */
	public static final int DEVICE_ORIENTATION_HORIZONTAL = 0;
	
	/**
	 * Constant used to define Phone device vertical orientation
	 */
	public static final int DEVICE_ORIENTATION_VERTICAL = 1;
	
	/**
	 * Constant used to define Blackberry Model 9000
	 */
	public static final int BLACKBERRY_MODEL_9000 = 0;
	
	/**
	 * Constant used to define Blackberry Model 9700
	 */
	public static final int BLACKBERRY_MODEL_9700 = 1;
	
	/**
	 * Constructor for StaticMethods.
	 */
	private StaticUtilities()
	{
		// Private constructor to prevent instantiation of this class.
	}

	public static boolean isIEBrowser(String browserPath, boolean testPhone, boolean enabledForFlex)
	{
		if(testPhone || enabledForFlex)
		{
			return false;
		}
		else
		{
			return (browserPath != null && PlatformHandler.isWindows() && (browserPath.toLowerCase().indexOf("iexplore") >= 0));
		}
			
	}
	
	/**
	 * @param sUrl
	 * @return
	 */
	public static String getPathFromURL(String sUrl)
	{
		int index = ((sUrl.indexOf("://") != -1) ? (sUrl.indexOf("://") + "://".length() + 1) : 0);
		index = sUrl.indexOf('/', index);
		return ((index != -1) ? sUrl.substring(index, sUrl.length()) : sUrl); // Remove HostName
	}

	/**
	 * @param sUrl
	 * @return
	 */
	public static String getEncodedURI(final String sUrl)
	{
		if (sUrl != null)
		{
			final StringBuffer sb = new StringBuffer(sUrl.length());
			char c;
			for (int i = 0; i < sUrl.length(); i++)
			{
				c = sUrl.charAt(i);
				switch (c)
				{
					case '<':
					{
						sb.append("%3C"); //$NON-NLS-1$
						break;
					}
					case '>':
					{
						sb.append("%3E"); //$NON-NLS-1$
						break;
					}
					case '[':
					{
						sb.append("%5B"); //$NON-NLS-1$
						break;
					}
					case ']':
					{
						sb.append("%5D"); //$NON-NLS-1$
						break;
					}
					case '^':
					{
						sb.append("%5E"); //$NON-NLS-1$
						break;
					}
					case '|':
					{
						sb.append("%7C"); //$NON-NLS-1$
						break;
					}
					case '~':
					{
						sb.append("%7E"); //$NON-NLS-1$
						break;
					}
					case ' ':
					{
						sb.append("+"); //$NON-NLS-1$
						break;
					}
					default:
					{
						sb.append(c);
						break;
					}
				}
			}
			return sb.toString();
		}
		return sUrl;
	}

	public static String getPathFromURI(final String sUrl)
	{
		String action = sUrl.trim();
		if (action.indexOf('?') != -1)
		{
			action = action.substring(0, action.indexOf('?'));
		}
		final int iHttpIndex = action.indexOf("://"); //$NON-NLS-1$
		int iEndIndex = 0;
		if (iHttpIndex >= 0)
		{
			iEndIndex = iHttpIndex + "://".length() + 1; //$NON-NLS-1$
		}
		final int iFirstSlash = action.indexOf('/', iEndIndex);
		if (iFirstSlash >= 0)
		{
			action = action.substring(iFirstSlash);
		}
		else
		{
			action = "/"; //$NON-NLS-1$
		}
		if (action.indexOf(';') != -1)
		{
			action = action.substring(0, action.indexOf(';'));
		}
		return action;
	}
	/**
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty( String str )
	{
		return str == null || str.length() == 0 || str.trim().length() == 0;
	}
	/**
	 * @param str
	 * @return
	 */
	public static boolean hasContent( String str )
	{
		return str != null && str.trim().length() > 0;
	}	
	/**
	 * @param array
	 * @param separator
	 * @return
	 */
	public static String join( String[] array, String separator )
	{
		StringBuffer buf = new StringBuffer();
		for( int i = 0; i < array.length; i++ )
		{
			if( i > 0 )
				buf.append( separator );
			buf.append( array[i] );
		}
		return buf.toString();
	}	
	/**
	 * @param str
	 * @return
	 */
	public static String quote( String str )
	{
		if( str == null )
		{
			return str;
		}
		if( str.length() < 2 || !str.startsWith( "\"" ) || !str.endsWith( "\"" ) )
		{
			str = "\"" + str + "\"";
		}
		return str;
	}	
	/**
	 * Method
	 * <code>equals<code> compares two strings and returns <code>boolean<code>
	 * value indicating whether the two strings are equal or not. This method should
	 * be used to avoid null pointer exception while comparing two strings.
	 *
	 * @param str1  <tt>String</tt> representing the first string.
	 * @param str2  <tt>String</tt> representing the second string.
	 * @param bIgnoreCase whether case is to be ignored while comparing.
	 *
	 * @return a <tt>boolean</tt> showing if the strings are same or not.
	 */
	public static boolean equals(final String str1, final String str2, final boolean bIgnoreCase)
	{
		boolean bEqual = false;
		if ((str1 == null) && (str2 == null))
		{
			bEqual = true;
		}
		else if ((str1 != null) && (str2 != null))
		{
			if (bIgnoreCase)
			{
				bEqual = str1.equalsIgnoreCase(str2);
			}
			else
			{
				bEqual = str1.equals(str2);
			}
		}
		return bEqual;
	}
	/**
	 * @param baseUrl
	 * @param url
	 * @return
	 */
	public static String joinRelativeUrl( String baseUrl, String url )
	{
		if( baseUrl.indexOf( '?' ) > 0 )
			baseUrl = baseUrl.substring( 0, baseUrl.indexOf( '?' ) );

		boolean isWindowsUrl = baseUrl.indexOf( '\\' ) >= 0;
		boolean isUsedInUnix = File.separatorChar == '/';

		if( isUsedInUnix && isWindowsUrl )
		{
			baseUrl = baseUrl.replace( '\\', '/' );
			url = url.replace( '\\', '/' );
		}

		boolean isFile = baseUrl.startsWith( "file:" );

		int ix = baseUrl.lastIndexOf( '\\' );
		if( ix == -1 )
			ix = baseUrl.lastIndexOf( '/' );

		// absolute?
		if( url.startsWith( "/" ) && !isFile )
		{
			ix = baseUrl.indexOf( "/", baseUrl.indexOf( "//" ) + 2 );
			return baseUrl.substring( 0, ix ) + url;
		}

		// remove leading "./"
		while( url.startsWith( ".\\" ) || url.startsWith( "./" ) )
			url = url.substring( 2 );

		// remove leading "../"
		while( url.startsWith( "../" ) || url.startsWith( "..\\" ) )
		{
			int ix2 = baseUrl.lastIndexOf( '\\', ix - 1 );
			if( ix2 == -1 )
				ix2 = baseUrl.lastIndexOf( '/', ix - 1 );
			if( ix2 == -1 )
				break;

			baseUrl = baseUrl.substring( 0, ix2 + 1 );
			ix = ix2;

			url = url.substring( 3 );
		}

		// remove "/./"
		while( url.indexOf( "/./" ) != -1 || url.indexOf( "\\.\\" ) != -1 )
		{
			int ix2 = url.indexOf( "/./" );
			if( ix2 == -1 )
				ix2 = url.indexOf( "\\.\\" );

			url = url.substring( 0, ix2 ) + url.substring( ix2 + 2 );
		}

		// remove "/../"
		while( url.indexOf( "/../" ) != -1 || url.indexOf( "\\..\\" ) != -1 )
		{
			int ix2 = -1;

			int ix3 = url.indexOf( "/../" );
			if( ix3 == -1 )
			{
				ix3 = url.indexOf( "\\..\\" );
				ix2 = url.lastIndexOf( '\\', ix3 - 1 );
			}
			else
			{
				ix2 = url.lastIndexOf( '/', ix3 - 1 );
			}

			if( ix2 == -1 )
				break;

			url = url.substring( 0, ix2 ) + url.substring( ix3 + 3 );
		}

		String result = baseUrl.substring( 0, ix + 1 ) + url;
		if( isFile )
			result = result.replace( '/', File.separatorChar );

		return result;
	}
	/**
	 * This method searches the specified substring in the given string and if
	 * found replaces all the occurences with the given replace string
	 *
	 * @param originalStr
	 *            String on which search and replace is performed
	 * @param sWordToReplace
	 *            String to search for
	 * @param sReplaceWith
	 *            String to replace the found substring
	 * @return Modified string with all the occurences of substring replaced
	 */
	public static String replaceAll(final String originalStr, final String sWordToReplace, final String sReplaceWith)
	{
		final Pattern p = new Pattern(sWordToReplace);
		final Replacer r = p.replacer(sReplaceWith);
		return r.replace(originalStr);
	}
	
	public static String [] split(final String input, final String pattern, int limit)
	{
		final Pattern p = new Pattern(pattern);
        int index = 0;
        boolean matchLimited = limit > 0;
        ArrayList matchList = new ArrayList();
        Matcher m = p.matcher(input);

        // Add segments before each match found
        while(m.find()) {
            if (!matchLimited || matchList.size() < limit - 1) {
                String match = input.substring(index, m.start()).toString();
                matchList.add(match);
                index = m.end();
            } else if (matchList.size() == limit - 1) { // last one
                String match = input.substring(index,
                                                 input.length()).toString();
                matchList.add(match);
                index = m.end();
            }
        }

        // If no match was found, return this
        if (index == 0)
            return new String[] {input.toString()};

        // Add remaining segment
        if (!matchLimited || matchList.size() < limit)
            matchList.add(input.substring(index, input.length()).toString());

        // Construct result
        int resultSize = matchList.size();
        if (limit == 0)
            while (resultSize > 0 && matchList.get(resultSize-1).equals(""))
                resultSize--;
        String[] result = new String[resultSize];
        matchList.subList(0, resultSize).toArray(result);
        return result;
		
	}
	
	/**
	 * @param originalStr
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static String replaceAll(final String originalStr, final char c1, final char c2)
	{
		if (originalStr == null)
		{
			return originalStr;
		}
		final char[] c = originalStr.toCharArray();
		final int length = c.length;
		for (int i = 0; i < length; i++)
		{
			if (c[i] == c1)
			{
				c[i] = c2;
			}
		}
		return new String(c);
	}

	/**
	 * @param originalStr
	 * @param subString
	 * @param withString
	 * @return
	 */
	public static String replaceFirst(final String originalStr, final String subString, final String withString)
	{
		if ((originalStr.length() == 0) || (subString.length() == 0))
		{
			return originalStr;
		}
		final int iAt = originalStr.indexOf(subString);
		if (iAt < 0)
		{
			return originalStr;
		}
		if (iAt == 0)
		{
			return withString + originalStr.substring(subString.length());
		}
		return originalStr.substring(0, iAt) + withString + originalStr.substring(iAt + subString.length());
	}

	/**
	 * @param sStringRep
	 * @return
	 */
	public static String removeFirstAndLastQuote(final String sStringRep)
	{
		String sTemp = sStringRep;
		final int iLength = sTemp == null ? 0 : sTemp.length();
		if ((iLength > 1) && (sTemp.charAt(0) == '"') && (sTemp.charAt(iLength - 1) == '"'))
		{
			sTemp = sTemp.substring(1, iLength - 1);
		}
		return sTemp;
	}

	/**
	 * method matches
	 *
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean matches(final String str1, final String str2)
	{
		final Pattern p = new Pattern(str2);
		final Matcher matcher = p.matcher(str1);
		return matcher.matches();
	}

	/**
	 * @param targetHost
	 * @param nonProxyHosts
	 * @param byPassLocalAddress
	 * @return
	 */
	public static boolean isNonProxiableHost(final String targetHost, String nonProxyHosts,
			final boolean bypassProxyForLocalAddress)
	{
		boolean bNonProxiableHost = false;
		if (bypassProxyForLocalAddress)
		{
			nonProxyHosts += ((nonProxyHosts != null) ? "|" : "");
			nonProxyHosts += "127.0.0.1|localhost";
		}
		if (nonProxyHosts != null)
		{
			final StringTokenizer tok = new StringTokenizer(nonProxyHosts, "|,;");
			while (tok.hasMoreTokens())
			{
				String nonProxiableHost = tok.nextToken().trim();
				nonProxiableHost = StringUtils.replace(nonProxiableHost, ".", "\\.");
				nonProxiableHost = StringUtils.replace(nonProxiableHost, "*", ".*");
				if (matches(targetHost, nonProxiableHost))
				{
					bNonProxiableHost = true;
					break;
				}
			}
		}
		return bNonProxiableHost;
	}

	/**
	 * returns the current date formatted in medium style, for the sake of
	 * recording it in reports
	 *
	 * @return the current date formatted in medium style.
	 */
	public static String getCurrentTimestamp()
	{
		final Date dt = new Date();
		final DateFormat medium = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
		return medium.format(dt);
	}

	/**
	 * cloneArrayList
	 *
	 * @param al
	 * @return
	 * @return ArrayList
	 */
	public static ArrayList cloneArrayList(final ArrayList al)
	{
		if (al != null)
		{

			final ArrayList alClone = new ArrayList();
			try
			{
				for (int i = 0; i < al.size(); i++)
				{
					final AppCloneable obj = (AppCloneable) al.get(i);
					alClone.add(obj.clone());
				}
			}
			catch (final Exception e)
			{
				AppLogger.getLogger().log(
						AppLogger.getPriority(AppLogger.WARN), e); //$NON-NLS-1$
			}

			return alClone;
		}
		return null;
	}

	/**
	 * This method is used to provide String representation of ArrayList
	 * contnets, eliminating enclosing square brackets.
	 */
	public static String arrayListToString(final ArrayList alStringList)
	{
		if ((alStringList != null) && !alStringList.isEmpty())
		{
			String str = alStringList.toString();
			// remove square brackets.
			str = str.substring(1, str.length() - 1);
			return str;
		}

		return IProductConstants.EMPTY_STRING;
	}

	/**
	 * This method is used to provide String representation of Vector contnets,
	 * eliminating enclosing square brackets.
	 */
	public static String vectorToString(final Vector vStringList)
	{
		if ((vStringList != null) && !vStringList.isEmpty())
		{
			String str = vStringList.toString();
			// remove square brackets.
			str = str.substring(1, str.length() - 1);
			return str;
		}

		return IProductConstants.EMPTY_STRING;
	}

	/**
	 * converts given time unit in milliseconds to minutes. Returns the nearest
	 * integer value i.e. uses Math.round() to return the most correct value as
	 * result of division.
	 */
	public static int millisToMinutes(final long milliSecs)
	{
		return Math.round(Math.round(milliSecs / 1000.0f) / 60.0f);
	}

	/**
	 * millisToSeconds
	 *
	 * @param milliSecs
	 * @return
	 */
	public static int millisToSeconds(final long milliSecs)
	{
		return Math.round(milliSecs / 1000.0f);
	}

	/**
	 * millisToSeconds
	 *
	 * @param milliSecs
	 * @return
	 */
	public static long millisToSeconds(final double milliSecs)
	{
		return Math.round(milliSecs / 1000.0f);
	}

	/**
	 * @param milliSecs
	 * @return
	 */
	public static double millisToSecondsD(final double milliSecs)
	{
		return milliSecs / 1000.0f;
	}

	/**
	 * @param milliSecs
	 * @return
	 */
	public static float millisToSecondsD(final float milliSecs)
	{
		return milliSecs / 1000.0f;
	}

	/**
	 * @param iMinutes
	 * @return
	 */
	public static long minutesToMillis(final int iMinutes)
	{
		return (iMinutes * 60 * 1000);
	}
	/**
	 * Renames a file with old name with the new name. If the last parameter is
	 * true, then if the file with new name already exists, it will be deleted
	 * before old name is renamed to new name.
	 *
	 * @param oldName
	 *            old name of file
	 * @param newName
	 *            new name for the file
	 * @param deleteIfExists
	 *            if true, file with new name will be deleted if present.
	 * @return boolean true, if rename was attempted and was successful.
	 * @throws IOException
	 */
	public static boolean renameFile(final String oldName, final String newName, final boolean deleteIfExists)
			throws IOException
	{
		final File newFile = new File(newName);
		if (deleteIfExists)
		{
			if (newFile.exists())
			{
				/* boolean bDeleted = */newFile.delete();
			}
		}
		final File oldFile = new File(oldName);
		if (oldFile.exists())
		{
			return moveFile(oldFile, newFile);
		}
		return false;
	}

	/**
	 * Use this method to move given srcFile as destFile. If srcFile is valid
	 * file then destFile should not belong to folder and vice versa.
	 *
	 * @param srcFile
	 * @param destFile
	 * @return
	 * @throws IllegalArgumentException -
	 *             If srcFile or destFile is/are null.
	 * @throws IOException -
	 *             If I/O error occured while moving srcFile.
	 */
	public static boolean moveFile(final File srcFile, final File destFile) throws IOException
	{
		if ((srcFile == null) || (destFile == null))
		{
			throw new IllegalArgumentException("Source or destination file cannot be null.");
		}
		if (!srcFile.exists())
		{
			return false;
		}
		boolean success = true;
		if (!srcFile.renameTo(destFile))
		{
			if (deleteFile(destFile))
			{
				success = srcFile.renameTo(destFile);
			}
			else
			{
				success = false;
			}
		}
		// cannot move using renameTo() hence move file manually
		// dont perform this operation for invalid file i.e. directory, etc.
		if (!success && srcFile.isFile())
		{
			// first copy file
			copyFileAs(srcFile, destFile);
			// second delete source file
			if (!srcFile.delete())
			{
				// if cannot delete then mark it to delete on JVM exit
				srcFile.deleteOnExit();
			}
		}
		return success;
	}

	public static void copyFileAs(final File sourceFile, final File destFile) throws IOException
	{
		final BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceFile));
		final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
		final byte bArray[] = new byte[512];
		int count;
		while (in.available() > 0)
		{
			count = in.read(bArray);
			if (count != -1)
			{
				out.write(bArray, 0, count);
			}
			else
			{
				break;
			}
		}
		in.close();
		out.close();
	}

	/**
	 * @param sourceFileName
	 * @param destFolder
	 * @throws Exception
	 */
	public static void copyNonUTFFile(final String sourceFileName, final String destFolder) throws Exception
	{
		ServiceUtilities.copyNonUTFFile(sourceFileName, destFolder);
	}

	public static void copyUTFFile(final File sourceFile, final File destFolder) throws IOException
	{
		copyUTFFile(sourceFile, destFolder, false);
	}
	
	public static void copyIPhoneFiles(final String sourceDir, final String destFolder) throws Exception
	{
		File file = new File(destFolder + File.separatorChar + "iphone-hort.png");
		if(!file.exists())
		{
			copyNonUTFFile(sourceDir + File.separatorChar + "bg-panel.png", destFolder);
			copyNonUTFFile(sourceDir + File.separatorChar + "bg-panel-h.png", destFolder);
			copyNonUTFFile(sourceDir + File.separatorChar + "bg-test.png", destFolder);
			copyNonUTFFile(sourceDir + File.separatorChar + "iphone-hort.png", destFolder);
			copyNonUTFFile(sourceDir + File.separatorChar + "iphone-vert.png", destFolder);
		}
	}

	/**
	 * @param sourceFile
	 * @param destFolder
	 * @throws IOException
	 */
	public static void copyUTFFile(final File sourceFile, final File destFolder, final boolean bEncode)
			throws IOException
	{
		final String fileContents = getFileContents(sourceFile.getAbsolutePath());//, bEncode);

		/*
		 * PLEASE DO NOT CHANGE THE FOLLOWING CODE RELATED TO USE OF STREAMS
		 * EVEN IF IT VIOLATES THE CA RULE.... USING BYTEARRY STREAM CORRUPTS
		 * THE STRING FOR UTF-8.... ANY CHANGE IN THE STREAMS CODE BELOW WILL
		 * RESULT IN INTERNATIONALISED PAGES NOT SHOWN PROPERLY.
		 */
		final File destinationFile = new File(destFolder, sourceFile.getName());
		final FileOutputStream fos = new FileOutputStream(destinationFile); // $IGN_Close_streams$
		final PrintWriter fWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"))); //$NON-NLS-1$
		fWriter.write(fileContents);
		fWriter.flush();
		fWriter.close();
	}

	public static void copyUTFFile(final File sourceFile, final File destFolder, final String fileName)
			throws IOException
	{
		copyUTFFile(sourceFile, destFolder, fileName, false);
	}

	/**
	 * @param sourceFile
	 * @param destFolder
	 * @param fileName
	 * @throws IOException
	 */
	public static void copyUTFFile(final File sourceFile, final File destFolder, final String fileName,
			final boolean bEncode) throws IOException
	{
		final String fileContents = getFileContents(sourceFile.getAbsolutePath());//, bEncode);

		if (fileContents != null)
		{
			/*
			 * PLEASE DO NOT CHANGE THE FOLLOWING CODE RELATED TO USE OF STREAMS
			 * EVEN IF IT VIOLATES THE CA RULE.... USING BYTEARRY STREAM
			 * CORRUPTS THE STRING FOR UTF-8.... ANY CHANGE IN THE STREAMS CODE
			 * BELOW WILL RESULT IN INTERNATIONALISED PAGES NOT SHOWN PROPERLY.
			 */
			final File destinationFile = new File(destFolder, fileName);
			final FileOutputStream fos = new FileOutputStream(destinationFile); // $IGN_Close_streams$
			final PrintWriter fWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"))); //$NON-NLS-1$
			fWriter.write(fileContents);
			fWriter.flush();
			fWriter.close();
		}
	}

	/**
	 * @param sourceDir
	 * @param destDir
	 * @throws Exception
	 */
	public static void copyDirectory(final File sourceDir, final File destDir) throws Exception
	{
		if (sourceDir.isDirectory() && destDir.isDirectory())
		{
			final File[] files = sourceDir.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				final File file = files[i];
				if (file.isDirectory())
				{
					final File newDestDir = new File(destDir, file.getName());
					newDestDir.mkdir();
					copyDirectory(file, newDestDir);
				}
				else
				{
					copyNonUTFFile(file.getAbsolutePath(), destDir.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * @param oos
	 * @throws IOException
	 */
	public static void flushStream(final ObjectOutputStream oos) throws IOException
	{
		oos.flush();
		oos.reset();
	}

	/**
	 * @param sFullName
	 * @return String
	 */
	public static final String getFilenameFromPath(final String sFullName)
	{
		if (sFullName == null)
		{
			return null;
		}
		if (sFullName.trim().length() == 0)
		{
			return IProductConstants.EMPTY_STRING;
		}
		final int iLastSlash = sFullName.lastIndexOf(File.separatorChar);
		if (iLastSlash < 0)
		{
			return sFullName;
		}
		return sFullName.substring(iLastSlash + 1);
	}

	/**
	 * returns primitive value corresponding to the String passed to it as a
	 * parameter. If NumberFormatException occurs while parsing the string, a
	 * default value is returned.
	 */
	public static int parseInt(final String sVal, final int iDefault)
	{
		int i = 0;
		try
		{
			i = Integer.parseInt(sVal);
		}
		catch (final NumberFormatException nfe)
		{
			i = iDefault;
		}
		return i;
	}

	public static void writeToStream(InputStream stream, OutputStream baos, long len) throws IOException {
		final byte[] readBuffer = new byte[1024];
		int bytesIn = 0;
		long readSoFar = 0;
		while (readSoFar < len && (bytesIn = stream.read(readBuffer, 0, (int) Math.min(readBuffer.length, len - readSoFar))) != -1) {
			baos.write(readBuffer, 0, bytesIn);
			readSoFar += bytesIn;
			baos.flush();
		}
	}
	
	
	/**
	 * returns primitive value corresponding to the String passed to it as a
	 * parameter. If NumberFormatException occurs while parsing the string, a
	 * default value is returned.
	 */
	public static long parseLong(final String sVal, final long iDefault)
	{
		long i = 0L;
		try
		{
			i = Long.parseLong(sVal);
		}
		catch (final NumberFormatException nfe)
		{
			i = iDefault;
		}
		return i;
	}

	/**
	 * returns primitive value corresponding to the String passed to it as a
	 * parameter. If NumberFormatException occurs while parsing the string, a
	 * default value is returned.
	 */
	public static short parseShort(final String sVal, final short iDefault)
	{
		short i = 0;
		try
		{
			i = Short.parseShort(sVal);
		}
		catch (final NumberFormatException nfe)
		{
			i = iDefault;
		}
		return i;
	}

	/**
	 * returns the name for the log directory to be used which is of the format
	 * <testName>/<MM_dd_yyyy_hh_mm>/ Note that the name ends with the file
	 * separator char.
	 */
	public static String createDirWithTimestamp(final String childDir, final boolean append)
	{
		return ServiceUtilities.createDirWithTimestamp(childDir, append);
	}

	/**
	 * returns an i18n string of the form "x minutes, y seconds".for the
	 * argument passed in milliseconds.
	 *
	 * @param millis
	 *            the time unit in milliseconds.
	 */
	public static String getMinutesAndSeconds(final long millis)
	{
		final float fSecs = millis / 1000.0f;
		final int iMins = (int) fSecs / 60;
		final int iSecs = (int) fSecs % 60;
		String tempTime = null;
		if (iMins > 0)
		{
			tempTime = IProductConstants.EMPTY_STRING;
			tempTime += String.valueOf(iMins) + " minutes "; //$NON-NLS-1$
		}
		if (iSecs > 0)
		{
			if (tempTime == null)
			{
				tempTime = IProductConstants.EMPTY_STRING;
			}
			tempTime += String.valueOf(iSecs) + " seconds"; //$NON-NLS-1$
		}
		if (tempTime == null)
		{
			tempTime = "0 seconds"; //$NON-NLS-1$
		}
		return tempTime;
	}

	/**
	 * @return
	 */
	public static DateFormat getDateTimeFormat()
	{
		return DTDATETIMEFORMAT;
	}

	/**
	 * @param time
	 * @return
	 */
	public static String getTimeStampFormated(final Timestamp time)
	{
		return DTDATETIMEFORMAT.format(new Date(time.getTime()));
	}

	/**
	 * @param time
	 * @return
	 */
	public static String getTimeStampFormated(final long time)
	{
		return DTDATETIMEFORMAT.format(new Date(time));
	}

	/**
	 * @param time
	 * @return
	 */
	public static String formatTime(final long time)
	{
		return formatTime(time, DEFUALT_FORMAT);
	}

	/**
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static String formatTime(final long time, final String pattern)
	{
		SDF.applyPattern(pattern);

		if (date == null)
		{
			date = new Date(time);
		}
		else
		{
			date.setTime(time);
		}

		return SDF.format(date);
	}

	/**
	 * returns an i18n string of the form "x minutes, y seconds".for the
	 * argument passed in milliseconds.
	 *
	 * @param millis
	 *            the time unit in milliseconds.
	 */
	public static String getHrsMinutesAndSeconds(final long millis)
	{
		final float fSecs = millis / 1000.0f;
		final int iHrs = (int) fSecs / 3600;
		final int iRemSecs = (int) (fSecs % 3600);
		final int iMins = iRemSecs / 60;
		final int iSecs = iRemSecs % 60;
		String tempTime = IProductConstants.EMPTY_STRING;
		// FIXME - I think we can use String.format("%2d:%2d%2d", iHrs, iMins, iSecs) instead of the following code. - Amol
		if (iHrs < 10)
		{
			tempTime = "0"; //$NON-NLS-1$
		}
		tempTime += String.valueOf(iHrs) + ":"; //$NON-NLS-1$
		if (iMins < 10)
		{
			tempTime += "0"; //$NON-NLS-1$
		}
		tempTime += String.valueOf(iMins) + ":"; //$NON-NLS-1$
		if (iSecs < 10)
		{
			tempTime += "0"; //$NON-NLS-1$
		}
		tempTime += String.valueOf(iSecs);
//		if (tempTime == null)
//		{
//			tempTime = "00:00:00"; //$NON-NLS-1$
//		}
		return tempTime;
	}

	/**
	 * This method is used for creating a compressed byte array from int array.
	 * This method supports maximum value 12799
	 */
	public static byte[] getByteArray12799(final int arr[])
	{
		final byte[] abcbyte = new byte[2 * arr.length];
		for (int i = 0, j = 0; i < arr.length; i++)
		{
			abcbyte[j] = (byte) (arr[i] / 100);
			abcbyte[j + 1] = (byte) (arr[i] % 100);
			j += 2;
		}
		return abcbyte;
	}

	/**
	 * This method is used for converting a compressed byte array to int array.
	 * This method supports maximum value 12799
	 */
	public static int[] getIntArray12799(final byte arr[])
	{
		final int[] values = new int[arr.length / 2];
		for (int i = 0, j = 0; i < values.length; i++)
		{
			values[i] = arr[j] * 100 + arr[j + 1];
			j += 2;
		}
		return values;
	}

	/**
	 * getIntArrayAsString
	 *
	 * @param values
	 * @return String
	 */
	public static String getIntArrayAsString(final int values[])
	{
		if ((values != null) && (values.length > 0))
		{
			final StringBuffer sb = new StringBuffer(50);
			for (int i = 0; i < values.length; i++)
			{
				sb.append(values[i]);
				sb.append(", "); //$NON-NLS-1$
			}
			sb.delete(sb.length() - 2, sb.length());
			final String s = sb.toString();
			sb.setLength(0);
			return s;
		}
		return IProductConstants.EMPTY_STRING;
	}

	/**
	 * getIntArrayFromString
	 *
	 * @param value
	 * @return int[]
	 */
	public static int[] getIntArrayFromString(final String value)
	{
		if (value != null)
		{
			final StringTokenizer st = new StringTokenizer(value, ", "); //$NON-NLS-1$
			final int values[] = new int[st.countTokens()];
			for (int i = 0; i < values.length; i++)
			{
				values[i] = Integer.parseInt(st.nextToken());
			}
			return values;
		}
		return new int[0];
	}

//	/**
//	 * Encoding function that encrypts the given string. The string can be
//	 * decoded later.
//	 *
//	 * @param strToEncrypt
//	 * @return encrypted string
//	 */
//	public static String encodeString(final String strToEncrypt)
//	{
//		return new String(Base64.encodeBase64(strToEncrypt.getBytes()));
//	}
//
//	/**
//	 * Decoding function that decrypts the given string that was encrypted with
//	 * encodeString function
//	 *
//	 * @param strToEncrypt
//	 * @return encrypted string
//	 */
//	public static String decodeString(final String strToDecrypt)
//	{
//		return new String(Base64.decodeBase64(strToDecrypt.getBytes()));
//	}

	/**
	 * This function is used to encode an ordinary string that may contain
	 * special characters or more than one consecutive spaces for appropriate
	 * HTML display.
	 *
	 * @param s
	 * @return String
	 */
	public static final String htmlEncode(final String s)
	{
		String sHtmlEncoded = IProductConstants.EMPTY_STRING;
		if (s == null)
		{
			return null;
		}

		final StringBuffer sbHtmlEncoded = new StringBuffer();
		final char chrarry[] = s.toCharArray();
		for (int i = 0; i < chrarry.length; i++)
		{
			sbHtmlEncoded.append(getEncodedString(chrarry[i]));
		}
		sHtmlEncoded = sbHtmlEncoded.toString();
		return sHtmlEncoded;
	}

	/**
	 * @param c
	 * @return
	 */
	private static String getEncodedString(final char c)
	{
		switch (c)
		{
			case '\t': // Horizontal tab
				return "&#09;"; //$NON-NLS-1$
			case '\n': // Line feed
				return "<br>"; //$NON-NLS-1$
			case '\r': // Carriage Return
				return "&#13;"; //$NON-NLS-1$
			case ' ': // Space
				return "&nbsp;"; //$NON-NLS-1$
			case '"': // Quotation mark
				return "&#34;"; //$NON-NLS-1$
			case '#': // Hash
				return "&#35;"; //$NON-NLS-1$
			case '\'': // Apostrophe
				return "&#39;"; //$NON-NLS-1$
			case '<': // Less than
				return "&lt;"; //$NON-NLS-1$
			case '>': // Greater than
				return "&gt;"; //$NON-NLS-1$
			case '`': // Acute accent
				return "&#96;"; //$NON-NLS-1$
			case '&': // Ampersand
				return "&amp;"; //$NON-NLS-1$
			default:
				return String.valueOf(c);
		}
	}

	/**
	 * This method encodes a String to be written as javascript
	 *
	 * @param s
	 * @return String
	 */
	public static final String jsEncode(final String s)
	{
		if ((s == null) || (s.length() == 0))
		{
			return s;
		}
		final String sReplace = "'\"\n\r\t\\"; // CHARACTERS TO REPLACE
		// //$NON-NLS-1$

		final StringBuffer sJsEncoded = new StringBuffer();
		final char chrarry[] = s.toCharArray();
		for (int i = 0; i < chrarry.length; i++)
		{
			final char c = chrarry[i];
			sJsEncoded.append((sReplace.indexOf(c) >= 0) ? ("\\" + c) : (IProductConstants.EMPTY_STRING + c)); //$NON-NLS-1$
		}
		return sJsEncoded.toString();
	}

	/**
	 * This method check whether the email specified is valid or not
	 *
	 * @param email
	 * @return boolean
	 */
	public static boolean isEmailValid(String email)
	{
		email = email.trim();

		if (email.length() == 0)
		{
			return false;
		}

		final int length = email.length();
		final int indexOfAtTheRate = email.indexOf('@');
		final int lastIndexOfAtTheRate = email.lastIndexOf('@');
		final int lastIndexOfDot = email.lastIndexOf('.');

		if (!((indexOfAtTheRate > 0 /* @ is not the first char */)
				&& (indexOfAtTheRate < length - 1 /* @ is not the last char */)
				&& (indexOfAtTheRate == lastIndexOfAtTheRate /*
																			 * only one @
																			 * exists
																			 */) && (lastIndexOfDot > 0 /* . is not the first char */)
				&& (lastIndexOfDot < length - 1 /* . is not the last char */)
				&& (indexOfAtTheRate < lastIndexOfDot /* @ comes before . */)
				&& (lastIndexOfDot - indexOfAtTheRate != 1 /*
																		 * . and @ don't
																		 * come together
																		 */) && Character.isLetter(email.charAt(0)) /*
																 * first character
																 * is a letter
																 */
		))
		{
			return false;
		}

		return true;
	}

	public static int[] filterDuplicatesFromSortedArray(final int[] arrToFilterDuplicates)
	{
		if ((arrToFilterDuplicates != null) && (arrToFilterDuplicates.length > 1))
		{
			// ArrayList alNewList = new
			// ArrayList(arrToFilterDuplicates.length);
			final int[] alNewList = new int[arrToFilterDuplicates.length];
			// insert the first value in the ArrayList
			int currInsertedValue = arrToFilterDuplicates[0];
			int x = 0;
			alNewList[x++] = currInsertedValue;

			for (int i = 1; i < arrToFilterDuplicates.length; i++)
			{
				if (currInsertedValue == arrToFilterDuplicates[i])
				{
					continue;
				}
				currInsertedValue = arrToFilterDuplicates[i];
				alNewList[x++] = currInsertedValue;
			}

			final int size = alNewList.length;
			final int[] filteredArray = new int[size];
			System.arraycopy(alNewList, 0, filteredArray, 0, size);
			// for(int i = 0; i < size; i++)
			// {
			// filteredArray[i] = alNewList[i];
			// }

			return filteredArray;
		}

		return arrToFilterDuplicates;
	}

	/**
	 * @param arrToSort
	 */
	public static void sort(final int[] arrToSort)
	{
		if ((arrToSort != null) && (arrToSort.length > 1))
		{
			StaticUtilities.mergeSort(arrToSort, 0, arrToSort.length - 1);
		}
	}

	/**
	 * @param arrToSort
	 * @param lo
	 * @param hi
	 */
	private static void mergeSort(final int[] arrToSort, final int lo, final int hi)
	{
		if (lo < hi)
		{
			final int middle = (lo + hi) / 2;
			mergeSort(arrToSort, lo, middle);
			mergeSort(arrToSort, middle + 1, hi);
			StaticUtilities.merge(arrToSort, lo, hi);
		}
	}

	/**
	 * @param arrToSort
	 * @param lo
	 * @param hi
	 */
	private static void merge(final int[] arrToSort, final int lo, final int hi)
	{
		int i, j, ctr, middle;
		final int n = hi - lo + 1;

		final int[] tempArr = new int[n]; // temporary array

		ctr = 0;
		middle = (lo + hi) / 2;

		// copy lower half to array b
		for (i = lo; i <= middle; i++)
		{
			tempArr[ctr++] = arrToSort[i];
		}

		// copy upper half to array b in opposite order
		for (j = hi; j >= middle + 1; j--)
		{
			tempArr[ctr++] = arrToSort[j];
		}

		i = 0;
		j = n - 1;
		ctr = lo;

		// copy back next-greatest element at each time
		// until i and j cross
		while (i <= j)
		{
			if (tempArr[i] <= tempArr[j])
			{
				arrToSort[ctr++] = tempArr[i++];
			}
			else
			{
				arrToSort[ctr++] = tempArr[j--];
			}
		}
	}

	public static String getValueIgnoreCase(final Attributes atts, final String sKey, final String defaultValue)
	{
		final String retValue = atts.getValue(sKey);
		if (retValue != null)
		{
			return retValue;
		}
		final int length = atts.getLength();
		for (int i = 0; i < length; i++)
		{
			if (atts.getQName(i).equalsIgnoreCase(sKey))
			{
				return atts.getValue(i);
			}
		}
		return defaultValue;
	}

	/**
	 *
	 * @param handler
	 * @param file
	 * @throws Exception
	 */
	public static void parseXML(final ContentHandler handler, final File file) throws Exception
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8")); //$NON-NLS-1$
			parseXML(handler, handler instanceof ErrorHandler ? (ErrorHandler)handler:null, reader);
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (final IOException e)
				{
				}
			}
		}
	}

	/**
	 *
	 * @param handler
	 * @param file
	 * @throws Exception
	 */
	public static void parseXML(final DefaultHandler handler, final String xmlString) throws Exception
	{
		final StringReader reader = new StringReader(xmlString);
		try
		{
			parseXML(handler, handler, reader);
		}
		finally
		{
			reader.close();
		}
	}
	
	private static void parseXML(final ContentHandler handler, ErrorHandler errorHandler, final Reader reader) throws Exception
	{
		/*
		 * This handling is added for Plugins. By default context Class loader
		 * of the thread is AppClassLoader Where as all the plugin classes are
		 * loaded by PluginClassLoader so we need to set it as the context class
		 * loader of current thread.
		 *
		 * Store the context classloader of current thread in a variable to
		 * restore it back in the end.
		 */

		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(handler.getClass().getClassLoader());
		/*
		 * Some IDEs use Crimson driver which conflicts with Xerces Store the
		 * system property SAX driver and reset it in the end
		 */
		final String xmlDriver = System.getProperty("org.xml.sax.driver"); //$NON-NLS-1$
		try
		{
			System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser"); //$NON-NLS-1$ //$NON-NLS-2$
			final XMLReader parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(handler);
			parser.setErrorHandler(errorHandler);
			parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
			parser.setFeature("http://xml.org/sax/features/external-general-entities", false); //$NON-NLS-1$
			parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false); //$NON-NLS-1$
			parser.parse(new InputSource(reader));
			reader.close();
		}
		finally
		{
			if (xmlDriver != null)
			{
				System.setProperty("org.xml.sax.driver", xmlDriver); //$NON-NLS-1$
			}
			Thread.currentThread().setContextClassLoader(cl);
		}
	}

	/**
	 * Converts the comma seperated string to array of strings.
	 *
	 * @param source
	 * @return
	 */
	public static String[] getStringArrayFromString(final String source)
	{
		if (source != null)
		{
			final StringTokenizer st = new StringTokenizer(source, ", "); //$NON-NLS-1$
			final String result[] = new String[st.countTokens()];
			for (int i = 0; i < result.length; i++)
			{
				result[i] = st.nextToken().trim();
			}
			return result;
		}
		return new String[0];
	}

	/**
	 * convertIntoStringFormat
	 *
	 * @param s
	 * @return
	 */
	public static String convertIntoJavaStringFormat(final String s)
	{
		if (s != null)
		{
			final StringBuffer sb = new StringBuffer(s.length());
			for (int i = 0; i < s.length(); i++)
			{
				if (s.charAt(i) == '\"')
				{
					sb.append("\\\""); //$NON-NLS-1$
				}
				else if (s.charAt(i) == '\n')
				{
					sb.append("\\n"); //$NON-NLS-1$
				}
				else if (s.charAt(i) == '\r')
				{
					sb.append("\\r"); //$NON-NLS-1$
				}
				else if (s.charAt(i) == '\t')
				{
					sb.append("\\t"); //$NON-NLS-1$
				}
				else if (s.charAt(i) == '\f')
				{
					sb.append("\\f"); //$NON-NLS-1$
				}
				else if (s.charAt(i) == '\\')
				{
					sb.append("\\\\"); //$NON-NLS-1$
				}
				else if (s.charAt(i) == '\b')
				{
					sb.append("\\b"); //$NON-NLS-1$
				}
				else
				{
					sb.append(s.charAt(i));
				}
			}
			return sb.toString();
		}
		return s;
	}

	/**
	 *
	 * @param s
	 * @return
	 */
	public static String convertIntoSingleSpacedJavaStringFormat(final String s)
	{
		if (s != null)
		{
			final StringBuffer sb = new StringBuffer(s.length());
			for (int i = 0; i < s.length(); i++)
			{
				if ((s.charAt(i) == '\n') || (s.charAt(i) == '\r') || (s.charAt(i) == '\t') || (s.charAt(i) == '\f')
						|| (s.charAt(i) == ' '))
				{
					if (sb.charAt(sb.length() - 1) != ' ')
					{
						sb.append(" "); //$NON-NLS-1$
					}
				}
				else
				{
					sb.append(s.charAt(i));
				}
			}
			return sb.toString();
		}
		return s;
	}

	public static int[] getUnCoveredLines(final int[] lines, final int[] linesCount)
	{
		if (linesCount == null)
		{
			return lines;
		}
		// ArrayList al = new ArrayList(lines != null ? lines.length:0);
		final int[] al = new int[lines != null ? lines.length : 0];
		if (lines != null)
		{
			int x = 0;
			for (int i = 0; i < lines.length; i++)
			{
				if (linesCount[i] == 0)
				{
					al[x++] = lines[i];
				}
			}
		}
		final int[] unCoveredLines = new int[al.length];
		for (int i = 0; i < al.length; i++)
		{
			unCoveredLines[i] = al[i];
		}
		return unCoveredLines;
	}

	/**
	 * Deletes file or folder. In case of folder, it deletes files present in
	 * the folder reccursively, and in the end deletes the folder itself. This
	 * method should be used with care.
	 *
	 * @param file
	 */
	public static boolean deleteFile(final File file)
	{
		return ServiceUtilities.deleteFile(file);
	}

	public static ServerSocket createServerSocket(int port)
	{
		ServerSocket socket = null;
		while (socket == null)
		{
			try
			{
				socket = new ServerSocket(port);
			}
			catch (final Exception ex)
			{
				// The port is not available look for some other port
				port = getAvailablePort(port);
			}
		}
		return socket;
	}

	private static int getAvailablePort(final int port)
	{
		if (port <= FIRST_USED_DEVSUITE_PORT)
		{
			return port - 1;
		}
		return FIRST_USED_DEVSUITE_PORT;
	}

	/**
	 * This method will create a server socket on the port and check if the port
	 * is available. If the port is not available it will check for availability
	 * on other ports. Finally the server socket is closed.
	 *
	 * @param port
	 * @return
	 */
	public static int getFreePort(int port)
	{
		ServerSocket socket = null;
		try
		{
			while (socket == null)
			{
				try
				{
					socket = new ServerSocket(port);
				}
				catch (final Exception ex)
				{
					// The port is not available look for some other port
					port = getAvailablePort(port);
				}
			}
		}
		finally
		{
			if (socket != null)
			{
				try
				{
					socket.close();
				}
				catch (final IOException e)
				{
					AppLogger.getLogger().log(
							AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); //$NON-NLS-1$
				}
			}
		}
		return port;
	}

	/**
	 * This method searches the specified substring in the given string and if
	 * found replaces all the occurences with the given replace string
	 *
	 * @param s
	 *            String on which search and replace is performed
	 * @param sWordToReplace
	 *            String to search for
	 * @param sReplaceWith
	 *            String to replace the found substring
	 * @return Modified string with all the occurences of substring replaced
	 */
	public static String searchAndReplace(String originalStr, final String sWordToReplace, final String sReplaceWith)
	{
		if (originalStr == null)
		{
			return null;
		}
		String sLower = originalStr.toLowerCase();
		final String sWR = sWordToReplace.toLowerCase();
		int i = 0;
		do
		{
			i = sLower.indexOf(sWR, i);
			if (i != -1)
			{
				originalStr = originalStr.substring(0, i) + sReplaceWith
						+ originalStr.substring(i + sWordToReplace.length());
				i += sReplaceWith.length();
				sLower = originalStr.toLowerCase();
			}
		}
		while (i != -1);
		return originalStr;
	}

	/**
	 *
	 * @param path -
	 *            list of path separated by File.pathSeparator (generally
	 *            classpath)
	 * @return array of urls mapping to each path which can be used in
	 *         UrlClassLoader
	 */
	public static URL[] getUrls(final String pathList, final int productConstant)
	{
		final ArrayList alUrls = new ArrayList();
		if (pathList != null)
		{
			final StringTokenizer stok = new StringTokenizer(pathList, File.pathSeparator);
			while (stok.hasMoreTokens())
			{
				final String path = stok.nextToken();
				try
				{
					alUrls.add(new File(path).toURI().toURL());
				}
				catch (final Exception e)
				{
					AppLogger.getLogger().log(
							AppLogger.getPriority(AppLogger.DEBUG), "can not add class path = " + path, e); //$NON-NLS-1$
				}
			}
		}
		return (URL[]) alUrls.toArray(new URL[alUrls.size()]);
	}

	/**
	 * Method zipDirectoryToFile This method zips the specified directory into
	 * the specified file.
	 *
	 * @param outputFileName
	 * @param dir2zip
	 * @throws Exception
	 */
	public static void zipDirectoryToFile(final String outputFileName, final String dir2zip) throws Exception
	{
		ServiceUtilities.zipDirectoryToFile(outputFileName, dir2zip);
	}

	/**
	 * Method zipDir
	 *
	 * @param dir2zip
	 * @param zos
	 */
	public static void zipFileOrDirectory(final String orgDir, final String file2Zip, final ZipOutputStream zos)
			throws Exception
	{
		ServiceUtilities.zipFileOrDirectory(orgDir, file2Zip, zos);
	}

	/**
	 * Use this method to read boolean value from given Attribbutes and key
	 *
	 * @param key
	 * @param atts
	 * @param bDefault
	 * @return On success it will return boolean value. On failure it will
	 *         return given default value.
	 */
	public static boolean readBoolean(final String key, final Attributes atts, final boolean bDefault)
	{
		return readBoolean(atts.getValue(key), bDefault);
	}

	/**
	 * Use this method to read boolean value from given string.
	 *
	 * @param sValue
	 * @param bDefault
	 * @return On success it will return boolean value. On failure it will
	 *         return given default value.
	 */
	public static boolean readBoolean(final String sValue, boolean bDefault)
	{
		try
		{
			if (sValue != null)
			{
				bDefault = new Boolean(sValue).booleanValue();
			}
		}
		catch (final Exception ex)
		{
			// DO NOTHING
		}
		return bDefault;
	}

	/**
	 * Use this method to read integer from given Attributes and key.
	 *
	 * @param key
	 * @param atts
	 * @param iDefaultValue
	 * @return On success it will return integer value. On failure it will
	 *         return given default value.
	 */
	public static int readInteger(final String key, final Attributes atts, final int iDefaultValue)
	{
		return readInteger(atts.getValue(key), iDefaultValue);
	}

	/**
	 * Use this method to read integer from given string.
	 *
	 * @param sValue
	 * @param iDefaultValue
	 * @return On success it will return integer value. On failure it will
	 *         return given default value.
	 */
	public static int readInteger(final String sValue, int iDefaultValue)
	{
		try
		{
			if (sValue != null)
			{
				iDefaultValue = Integer.parseInt(sValue);
			}
		}
		catch (final NumberFormatException ex)
		{
			// DO NOTHING
		}
		return iDefaultValue;
	}

	/**
	 * Use this method to read integer from given Attributes and key.
	 *
	 * @param key
	 * @param atts
	 * @param lDefaultValue
	 * @return On success it will return long value. On failure it will return
	 *         given default value.
	 */
	public static long readLong(final String key, final Attributes atts, final long lDefaultValue)
	{
		return readLong(atts.getValue(key), lDefaultValue);
	}

	/**
	 * Use this method to read integer from given string.
	 *
	 * @param key
	 * @param atts
	 * @param lDefaultValue
	 * @return On success it will return long value. On failure it will return
	 *         given default value.
	 */
	public static long readLong(final String sValue, long lDefaultValue)
	{
		try
		{
			if (sValue != null)
			{
				lDefaultValue = Long.parseLong(sValue);
			}
		}
		catch (final NumberFormatException ex)
		{
			// DO NOTHING
		}
		return lDefaultValue;
	}

	/**
	 * Use this method to read integer from given Attributes and key.
	 *
	 * @param key
	 * @param atts
	 * @param iDefaultValue
	 * @return On success it will return byte value. On failure it will return
	 *         given default value.
	 */
	public static byte readByte(final String key, final Attributes atts, final int iDefaultValue)
	{
		return readByte(atts.getValue(key), iDefaultValue);
	}

	/**
	 * Use this method to read integer from given string.
	 *
	 * @param sValue
	 * @param iDefaultValue
	 * @return On success it will return byte value. On failure it will return
	 *         given default value.
	 */
	public static byte readByte(final String sValue, final int iDefaultValue)
	{
		byte bReturn = (byte) iDefaultValue;
		try
		{
			if (sValue != null)
			{
				bReturn = Byte.parseByte(sValue);
			}
		}
		catch (final NumberFormatException ex)
		{
			// DO NOTHING
		}
		return bReturn;
	}

	/**
	 * Use this method to read string value from given Attributes and key.
	 *
	 * @param key
	 * @param atts
	 * @param sDefaultValue
	 * @return If value is null then it will return default value.
	 */
	public static String readString(final String key, final Attributes atts, String sDefaultValue)
	{
		final String sTemp = atts.getValue(key);
		if (sTemp != null)
		{
			sDefaultValue = sTemp;
		}
		return sDefaultValue;
	}

	/**
	 * Use this method to read float value from given Attributes and key.
	 *
	 * @param key
	 * @param atts
	 * @param fDefaultValue
	 * @return on success it will return float value and on failure it will
	 *         return default value.
	 */
	public static float readFloat(final String key, final Attributes atts, final float fDefaultValue)
	{
		return readFloat(atts.getValue(key), fDefaultValue);
	}

	/**
	 * Use this method to read float value from given string.
	 *
	 * @param sValue
	 * @param fDefaultValue
	 * @return on success it will return float value and on failure it will
	 *         return default value.
	 */
	public static float readFloat(final String sValue, float fDefaultValue)
	{
		try
		{
			if (sValue != null)
			{
				fDefaultValue = Float.parseFloat(sValue);
			}
		}
		catch (final NumberFormatException ex)
		{
			// DO NOTHING
		}
		return fDefaultValue;
	}

	// @TODO: move this method to RepositoryUtils
	private static final String[] PRIMITIVEDATATYPES = new String[] {
			"void", "int", "long", "float", "double", "byte", "char", "short", "boolean" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$

	public static boolean isPrimitiveDataType(final String string)
	{
		for (int i = 0; i < PRIMITIVEDATATYPES.length; i++)
		{
			if (PRIMITIVEDATATYPES[i].equals(string))
			{
				return true;
			}
		}
		return false;

	}

	// @TODO: move this method to RepositoryUtils
	/**
	 * method isExtendedPrimitiveDataType
	 *
	 * @param string
	 * @return
	 */
	public static boolean isExtendedPrimitiveDataType(final String string)
	{
		if ("String".equals(string) || "java.lang.String".equals(string)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			return true;
		}
		return isPrimitiveDataType(string);
	}
	/**
	 * Method isValidBrowserPath.
	 *
	 * @param path
	 * @return boolean
	 */
	private static boolean isValidBrowserPath(String sBrowserPath)
	{
		boolean bFound = false;
		sBrowserPath = sBrowserPath.toLowerCase();
		if (PlatformHandler.isWindows() || (sBrowserPath.indexOf("netscp") >= 0) || (sBrowserPath.indexOf("netscape") >= 0) //$NON-NLS-1$ //$NON-NLS-2$
				|| (sBrowserPath.indexOf("firefox") >= 0) || (sBrowserPath.indexOf("mozilla") >= 0)|| (sBrowserPath.indexOf("safari") >= 0)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			bFound = true;
		}
		return bFound;
	}
	
	/**
	 * @param sBrowserPath
	 * @param sStartingUrl
	 * @return
	 * @throws Exception
	 */
	public static Process launchBrowser(String sBrowserPath, String sStartingUrl) throws Exception
	{
		Process prBrowser = null;
		String url = sStartingUrl;
		if(url.indexOf("127.0.0.1") > -1 && url.indexOf("127.0.0.1") <= 9) /* 8 to detect that the position of the string is atmost after https:// */ //$NON-NLS-1$ //$NON-NLS-2$
		{
			url = StaticUtilities.replaceFirst(url, "127.0.0.1", "localhost"); //$NON-NLS-1$ //$NON-NLS-2$
		}		
		if(sBrowserPath == null || sBrowserPath.trim().length() == 0
//				|| !NameValidator.isValidPath(sBrowserPath) 
				|| !isValidBrowserPath(sBrowserPath))
		{
			if(PlatformHandler.isMacOS())
			{
				try
				{
					//open URL
		            prBrowser = Runtime.getRuntime().exec("open " + url); //$NON-NLS-1$
				}
				catch (Exception e)
				{
					// DO NOTHING 
				}				
			}			
			else if(PlatformHandler.isWindows())
			{
				try
				{
					prBrowser = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
					//prBrowser = Runtime.getRuntime().exec("start "+ url); //$NON-NLS-1$
				}
				catch (IOException e)
				{
					// DO NOTHING 
				}
			}
			else
			{
				final String[] browserCommands = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "safari" };
				String browser = null;
				for (int count = 0; count < browserCommands.length && browser == null; count++)
				{
					if (Runtime.getRuntime().exec(new String[] {"which", browserCommands[count]}).waitFor() == 0)
					{
						browser = browserCommands[count];
						if (browser == null)
						{
			               throw new Exception("Could not find web browser");
						}
						try
						{
							prBrowser = Runtime.getRuntime().exec(new String[] {browser, url});
						}
						catch (IOException e)
						{
							// DO NOTHING 
						}							
					}
			     }
				//prBrowser = Runtime.getRuntime().exec("netscape " + url); //$NON-NLS-1$
//				If you're not using Debian and aren't particular about it being their default browser (just an installed browser) you can always use || to do it. Like this:
//				Code:
//				firefox url || mozilla url || konqueror url || opera url || etc...
//				Granted that requires you to list all of the possible browsers that might be installed, but it will work on any system.
			}			
		}
		else
		{
			if (PlatformHandler.isMacOS())
			{
				prBrowser = Runtime.getRuntime().exec(new String [] {"open", "-a", sBrowserPath, url}); //$NON-NLS-1$
			}
			else
			{
				prBrowser = Runtime.getRuntime().exec(new String [] {sBrowserPath, url}); //$NON-NLS-1$
			}
		}
		return prBrowser;
	}

	/**
	 *
	 * method launchBrowser
	 *
	 * @param systemOptions
	 * @param shell
	 * @param bAutoSetProxy
	 * @param proxyPort
	 * @param sStartingUrl
	 */
	public static Process launchEmailClient(final String sStartingUrl) throws Exception
	{
		Process prBrowser = null;
		try
		{
			String url = sStartingUrl;
			if ((url.indexOf("127.0.0.1") > -1) && (url.indexOf("127.0.0.1") <= 9)) /*
																 * 8 to detect that the
																 * position of the
																 * string is atmost
																 * after https://
																 *///$NON-NLS-1$ //$NON-NLS-2$
			{
				url = StaticUtilities.replaceFirst(url, "127.0.0.1", "localhost"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (PlatformHandler.isMacOS())
			{
				prBrowser = Runtime.getRuntime().exec("open " + url); //$NON-NLS-1$
			}
			else if (PlatformHandler.isWindows())
			{
				prBrowser = Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler " + url); //$NON-NLS-1$
			}
			else
			{
				prBrowser = Runtime.getRuntime().exec("netscape " + url); //$NON-NLS-1$
			}
		}
		catch (final IOException e)
		{
			throw e;
		}
		return prBrowser;
	}

	/**
	 * Resolved the String parameter to a classname. Incase of array classes of
	 * primitive datatypes the fullclassname of the primitive type along with
	 * the array representation is returned. method resolveParameter This method
	 * has been given package scope to improve performance.
	 *
	 * @param parameter
	 * @return
	 */
	public static final String resolveParameter(String parameter)
	{
		if ((parameter != null) && (parameter.length() > 0/* && parameter.startsWith("[") */)) //$NON-NLS-1$
		{
			int iNestingDepth = 0;
			String className = parameter;
			while (className.charAt(0) == '[') //$NON-NLS-1$
			{
				iNestingDepth++;
				className = className.substring(1, className.length());
			}
			if (className.equals("B")) //$NON-NLS-1$
			{
				className = "byte"; //$NON-NLS-1$
			}
			else if (className.equals("C")) //$NON-NLS-1$
			{
				className = "char"; //$NON-NLS-1$
			}
			else if (className.equals("I")) //$NON-NLS-1$
			{
				className = "int"; //$NON-NLS-1$
			}
			else if (className.equals("Z")) //$NON-NLS-1$
			{
				className = "boolean"; //$NON-NLS-1$
			}
			else if (className.equals("D")) //$NON-NLS-1$
			{
				className = "double"; //$NON-NLS-1$
			}
			else if (className.equals("F")) //$NON-NLS-1$
			{
				className = "float"; //$NON-NLS-1$
			}
			else if (className.equals("J")) //$NON-NLS-1$
			{
				className = "long"; //$NON-NLS-1$
			}
			else if (className.equals("S")) //$NON-NLS-1$
			{
				className = "short"; //$NON-NLS-1$
			}
			else if (className.charAt(0) == 'L') //$NON-NLS-1$
			{
				className = className.substring(1, className.length());
				if (className.indexOf(';') != -1)
				{
					className = className.substring(0, className.indexOf(';'));
				}
			}
			for (int i = 0; i < iNestingDepth; i++)
			{
				className += "[]"; //$NON-NLS-1$
			}
			parameter = className;
		}
		return parameter;
	}

	/**
	 * method trimSpaces. Removes tabs and space characters in a String.
	 *
	 * @param str
	 * @return
	 */
	public static String trimSpaces(String str)
	{
		if (str != null)
		{
			final StringTokenizer st = new StringTokenizer(str, " \t"); //$NON-NLS-1$
			str = IProductConstants.EMPTY_STRING;
			while (st.hasMoreTokens())
			{
				str += st.nextToken();
			}
		}
		return str;
	}

	/**
	 * Method getStringAsVector
	 *
	 * @param string
	 * @return
	 */
	public static Vector getStringAsVector(final String string)
	{
		final Vector v = new Vector();
		if (string != null)
		{
			final StringTokenizer strTok = new StringTokenizer(string, ";"); //$NON-NLS-1$
			while (strTok.hasMoreTokens())
			{
				v.add(strTok.nextToken());
			}
		}
		return v;
	}

	/**
	 * Method getVectorAsString
	 *
	 * @param v
	 * @return
	 */
	public static String getVectorAsString(final Vector v)
	{
		StringBuffer sb = null;
		if ((v != null) && (v.size() > 0))
		{
			sb = new StringBuffer();
			for (int i = 0; i < v.size(); i++)
			{
				sb.append(v.get(i));
				sb.append(";"); //$NON-NLS-1$
			}
		}
		return sb == null ? null : sb.toString();
	}

	public static void trimToSize(final ArrayList al, final int iSize)
	{
		int iCurrSize = al.size();
		while (iCurrSize > iSize)
		{
			al.remove(iCurrSize - 1);
			iCurrSize--;
		}
	}

	public static boolean isRedirectionResponseCode(final int iResponseCode)
	{
		return ((iResponseCode == 301) || (iResponseCode == 302)
				|| (iResponseCode == 307) || (iResponseCode == 303));
	}

	/**
	 * method getNextNewFileName is used to generate next new file name which is
	 * not present in parent folder specified. New file name be base on fileName
	 * parameter e.g. if fileName is "Sample.gif" then new file name can be
	 * "Sample_X.gif" where X={1,2,3,...N}.
	 *
	 * @param parentFolder
	 * @param fileName
	 * @return New file name base on fileName.
	 */
	public static String getNextNewFileName(final String parentFolder, final String fileName)
	{
		final String sName = fileName.lastIndexOf(".") != -1 ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName; //$NON-NLS-1$ //$NON-NLS-2$
		final String sExt = fileName.lastIndexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf("."), fileName.length()) : IProductConstants.EMPTY_STRING; //$NON-NLS-1$ //$NON-NLS-2$

		int iCount = 1;
		File file = new File(parentFolder, MessageFormat.format(
				"{0}_{1}{2}", new Object[] { sName, String.valueOf(iCount), sExt })); //$NON-NLS-1$
		while (!file.exists())
		{
			iCount++;
			file = new File(parentFolder, MessageFormat.format(
					"{0}_{1}{2}", new Object[] { sName, String.valueOf(iCount), sExt })); //$NON-NLS-1$
		}
		return MessageFormat.format("{0}_{1}{2}", new Object[] { sName, String.valueOf(iCount), sExt }); //$NON-NLS-1$
	}

//	public static String getFileContents(final String sFilePath)
//	{
//		return getFileContents(sFilePath, false);
//	}

	public static String getFileContents(final String sFilePath)
	{
		try
		{
//			boolean bEncode = true;
			final File file = new File(sFilePath);
			if (file.exists() && file.canRead())
			{
				/*
				 * PLEASE DO NOT CHANGE THE FOLLOWING CODE RELATED TO USE OF
				 * STREAMS EVEN IF IT VIOLATES THE CA RULE.... USING BYTEARRY
				 * STREAM CORRUPTS THE STRING FOR UTF-8.... ANY CHANGE IN THE
				 * STREAMS CODE BELOW WILL RESULT IN INTERNATIONALISED PAGES NOT
				 * SHOWN PROPERLY.
				 */// $IGN_Close_streams$
				final FileInputStream fis = new FileInputStream(file); // $IGN_Close_streams$
				InputStreamReader streamReader = new InputStreamReader(fis, "UTF-8"); //$NON-NLS-1$
//				if (bEncode)
//				{
//					streamReader = new InputStreamReader(fis, "UTF-8"); //$NON-NLS-1$
//				}
//				else
//				{
//					streamReader = new InputStreamReader(fis); //$NON-NLS-1$
//				}
				final BufferedReader reader = new BufferedReader(streamReader);
				final char[] chars = new char[1024];
				int readCount;
				final StringBuffer sbTemp = new StringBuffer();
				while ((readCount = reader.read(chars)) >= 0)
				{
					sbTemp.append(chars, 0, readCount);
				}
				reader.close();
				return sbTemp.toString();
			}
		}
		catch (final Exception ex)
		{
			// DO NOTHING
		}
		return null;
	}

	public static String getContextRoot(final String url)
	{
		try
		{
			if (url == null)
			{
				return IProductConstants.EMPTY_STRING;
			}
			String str = url;
			// printf("URL: %ls\n", str);
			final int i1 = str.lastIndexOf('.');
			final int i2 = str.lastIndexOf('/');
			// This is the case when get_URL returns empty string
			if ((i1 == -1) && (i2 == -1))
			{
				return IProductConstants.EMPTY_STRING;
			}
			else if ((i1 == -1) && (i2 >= 0))
			{
				final int i3 = str.lastIndexOf('?');
				final int i4 = str.lastIndexOf(';');
				final int i5 = str.lastIndexOf('&');
				// http://localhost:8080/google/search?hl=en&q="Anna+Disk"+download&meta=
				// In above case parameters are supplied to search servlet hence
				// context root will be http://localhost:8080/google
				if ((i3 > i2) || (i4 > i2) || (i5 > i2))
				{
					str = str.substring(0, i2);
				}
			}
			// http://localhost:8080/petstore/index.html
			else if ((i1 > i2) && (i2 > 0))
			{
				if (str.charAt(i2 - 1) != '/')
				{
					str = str.substring(0, i2);
				}
			}
			else if (i2 > i1)
			{
				final int i3 = str.lastIndexOf('?');
				final int i4 = str.lastIndexOf(';');
				final int i5 = str.lastIndexOf('&');
				// http://www.google.co.in/search?hl=en&q="Anna+Disk"+download&meta=
				// In above case parameters are supplied to search servlet hence
				// context root will be http://www.google.co.in/
				if ((i3 > i2) || (i4 > i2) || (i5 > i2) || (i2 >= 0))
				{
					str = str.substring(0, i2);
				}
			}
			if (str.charAt(str.length() - 1) == '/')
			{
				str = str.substring(0, str.length() - 1);
			}
			return str;
		}
		catch(Exception e)
		{
			AppLogger.getLogger().log(
					AppLogger.getPriority(AppLogger.FATAL), "cannot find context root", e); //$NON-NLS-1$	
		}
		return "";
	}

	public static String urlDecode(final String string)
	{
		return urlDecode(string, null); //$NON-NLS-1$
	}

	public static String urlDecode(final String string, final String enc)
	{
		if ((enc != null) && (enc.trim().length() > 0))
		{
			try
			{
				final Class urlDecoderClazz = Class.forName("java.net.URLDecoder"); //$NON-NLS-1$
				if (urlDecoderClazz != null)
				{
					final Method method = urlDecoderClazz.getMethod("decode", new Class[] { String.class, String.class }); //$NON-NLS-1$
					if (method != null)
					{
						return (String) method.invoke(null, new Object[] { string, enc });
					}
				}
			}
			catch (final Exception ex)
			{
				// ignore it.
			}
		}
		try
		{
			return URLDecoder.decode(string);
		}
		catch (final Exception ex)
		{
			return string;
		}
	}

	public static String urlEncode(final String string)
	{
		return urlEncode(string, null);
	}
	/**
	 * @param string
	 * @return
	 */
	public static boolean isEncoded(final String string)
	{
//		1. There are no spaces,.
//		2. All percentage signs are followed by two digits.
//		3. There are no characters outside of a..b, A..B, 0..9, ".", "", "-", "%" and "+" in it.
		boolean bEncoded = true;
		if(string != null && string.length() > 0)
		{
			int len = string.length();
			int counter = 0;
			while(counter < len)
			{
				char ch = string.charAt(counter++);
				if(ch == ' ')
				{
					bEncoded = false;
					break;
				}
				if(ch == '%')
				{
					if (string.length() >= counter + 2)
					{
						String s = string.substring(counter, counter + 2);
						try
						{
							counter += 2;
							Integer.parseInt(s, 16);
						}
						catch (Exception ex)
						{
							bEncoded = false;
							break;
						}
					}
//					else
//					{
//						bEncoded = false;
//						break;
//					}					
				}
				
				if(! (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ('0' <= ch && ch <= '9')
						|| (ch == '.') || (ch == '-') || (ch == '!') || (ch == '*') || (ch == '\'')
						|| (ch == '(') || (ch == ')') || (ch == '_') || (ch == '%') || (ch == '+')))
				{
					bEncoded = false;
					break;
				}
			}
		}
		return bEncoded;
	}
	
	/**
	 * @param string
	 * @param enc
	 * @return
	 */
	public static String urlEncode(final String string, final String enc)
	{
		if(isEncoded(string))
		{
			return string;
		}
		if ((enc != null) && (enc.trim().length() > 0))
		{
			try
			{
				final Class urlEncoderClazz = Class.forName("java.net.URLEncoder"); //$NON-NLS-1$
				if (urlEncoderClazz != null)
				{
					final Method method = urlEncoderClazz.getMethod("encode", new Class[] { String.class, String.class }); //$NON-NLS-1$
					if (method != null)
					{
						return (String) method.invoke(null, new Object[] { string, enc });
					}
				}
			}
			catch (final Exception ex)
			{
				// ignore it.
			}
		}
		try
		{
			return URLEncoder.encode(string);
		}
		catch (final Exception ex)
		{
			return string;
		}
	}

	/**
	 * getRelativePath method is used to extract relative path from
	 * sOriginalPath with reference of sReferencePath. NOTE: sReferencePath
	 * should be starting part of sOriginalPath. You can have difference in
	 * pathSeparator chars ('\\' and '/').
	 *
	 * @param sOriginalPath
	 * @param sReferencePath
	 * @return
	 */
	public static String getRelativePath(final String sOriginalPath, String sReferencePath)
	{
		if ((sOriginalPath == null) || (sReferencePath == null))
		{
			return sOriginalPath;
		}
		String sRelativePath = searchAndReplace(sOriginalPath, "\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		sReferencePath = searchAndReplace(sReferencePath, "\\", "/"); //$NON-NLS-1$	//$NON-NLS-2$
		if (sRelativePath.startsWith(sReferencePath))
		{
			sRelativePath = sRelativePath.substring(sReferencePath.length());
			if (sRelativePath.charAt(0) == '/')
			{
				sRelativePath = sRelativePath.substring(1);
			}
			return sRelativePath;
		}
		return sOriginalPath;
	}

	/**
	 * resolveRelativePath method is used to extract absolute path from relative
	 * filePath with reference of referencePath. NOTE: sReferencePath should be
	 * starting part of sOriginalPath. You can have difference in pathSeparator
	 * chars ('\\' and '/').
	 *
	 * @param filePath
	 * @param referencePath
	 * @return
	 */
	public static String resolveRelativePath(final String filePath, final String referencePath)
	{
		if ((filePath == null) || (filePath.length() == 0) || (referencePath == null)
				|| new File(filePath).isAbsolute())
		{
			return filePath;
		}
		return new File(referencePath, filePath).getAbsolutePath();
	}

	// public static void main(String[] s)
	// {
	// StaticUtilities.getContextRoot("http://localhost:8080/petstore");
	// StaticUtilities.getContextRoot("http://localhost:8080/petstore/");
	// StaticUtilities.getContextRoot("http://www.google.co.in");
	// StaticUtilities.getContextRoot("http://localhost:8080/petstore/cart.do");
	// StaticUtilities.getContextRoot("http://www.google.co.in/search?hl");
	// StaticUtilities.getContextRoot("http://www.google.co.in/search?hl=en&q=\"Anna+Disk\"+download&meta=");
	// }

	/**
	 * Used to know if given string - [/*] is starting of a comment or is
	 * appearing inside a string literal.
	 */
	public static boolean appearsInsideStringLiteral(final String sLine, final int iPos)
	{
		final char[] ca = sLine.toCharArray();
		final int iLength = ca.length;
		boolean bInsideDoubleQuotes = false;

		for (int i = 0; i < iLength; i++)
		{
			if (i >= iPos)
			{
				break;
			}

			if (ca[i] == '\\')
			{
				i++; // ignore this backslash and the following character
				continue;
			}

			if (ca[i] == '"')
			{
				bInsideDoubleQuotes = !bInsideDoubleQuotes;
			}
		}
		return bInsideDoubleQuotes;
	}

	/**
	 * @return
	 */
	public static NumberFormat getIntegerFormat()
	{
		if (intNumberFormat == null)
		{
			intNumberFormat = NumberFormat.getInstance();
			intNumberFormat.setMaximumFractionDigits(0);
			intNumberFormat.setMinimumFractionDigits(0);
		}
		return intNumberFormat;
	}

	/**
	 * @return
	 */
	public static NumberFormat getDecimalFormat()
	{
		if (floatNumberFormat == null)
		{
			floatNumberFormat = NumberFormat.getInstance();
			floatNumberFormat.setMaximumFractionDigits(2);
			floatNumberFormat.setMinimumFractionDigits(2);
		}
		return floatNumberFormat;
	}

	/**
	 * @param time
	 * @return
	 */
	public static String getTime(final long time)
	{
		final DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		if (time <= 0)
		{
			return IProductConstants.EMPTY_STRING;
		}
		final Date dEndTime = new Date(time);
		return df.format(dEndTime);
	}

	/**
	 * @param url
	 * @return
	 */
	public static boolean isValidProtocol(final URL url)
	{
		final String protocol = url.getProtocol();
		if (protocol != null)
		{
			return protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("https"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return false;
	}

	public static String getBaseClassNameIfArray(final String arrayClassName)
	{
		final int iPos = arrayClassName.indexOf('[');
		if (iPos >= 0)
		{
			return arrayClassName.substring(0, iPos);
		}
		return arrayClassName;
	}

	public static int resolveDatatypeFromValue(final String value)
	{
		try
		{
			final int intValue = Integer.parseInt(value);

			// optimised by checking the code of parseByte & parseShort methods.
			if ((intValue >= Byte.MIN_VALUE) && (intValue <= Byte.MAX_VALUE))
			{
				return DataTypes.BYTE;
			}
			else if ((intValue >= Short.MIN_VALUE) && (intValue <= Short.MAX_VALUE))
			{
				return DataTypes.SHORT;
			}
			return DataTypes.INTEGER;

		}
		catch (final Exception ex)
		{
			// do nothing
		}

		try
		{
			Long.parseLong(value);
			return DataTypes.LONG;
		}
		catch (final Exception ex)
		{
			// do nothing
		}

		try
		{
			Float.parseFloat(value);
			return DataTypes.FLOAT;
		}
		catch (final Exception ex)
		{
			// do nothing
		}
		try
		{
			Double.parseDouble(value);
			return DataTypes.DOUBLE;
		}
		catch (final Exception ex)
		{
			// do nothing
		}
		return DataTypes.STRING;
	}

	/**
	 * returns whether type2 can be assigned to type1
	 */
	public static boolean isPrimitiveAssignableFrom(final String type1, final String type2)
	{
		// We are using a common class used in AgentlessMonitor, which also
		// resolves java.lang.* belonging to
		// java.lang.Number hierarchy, but for us it will end in primitive only.
		final int dataType1 = DataTypes.resolvePrimitiveDataType(type1, true);
		final int dataType2 = DataTypes.resolvePrimitiveDataType(type2, true);

		// Uncomment following code to find which primitive can be assigned to
		// whom
		/*
		 * byte b = 0x00; short s = (short)0; int i = 0; float f = 0; long l =
		 * 0; double d = 0; char c = 'c';
		 *
		 * d = b; d = s; d = i; d = l; d = f;
		 *
		 * f = b; f = s; f = i; f = l; f = d; // compilation error
		 *
		 * l = b; l = s; l = i; l = d; // compilation error l = f; //
		 * compilation error
		 *
		 * i = b; i = s; i = f; // compilation error i = l; // compilation error
		 * i = d; // compilation error
		 *
		 * s = b; // compilation error s = i; // compilation error s = f; //
		 * compilation error s = l; // compilation error s = d; // compilation
		 * error
		 *
		 * b = s; // compilation error b = i; // compilation error b = f; //
		 * compilation error b = l; // compilation error b = d; // compilation
		 * error
		 */

		if ((dataType1 != DataTypes.UNDEFINED) && (dataType2 != DataTypes.UNDEFINED))
		{
			if (dataType1 == dataType2)
			{
				return true;
			}

			switch (dataType1)
			{
				case DataTypes.DOUBLE:
				{
					switch (dataType2)
					{
						case DataTypes.INTEGER:
						case DataTypes.LONG:
						case DataTypes.FLOAT:
						case DataTypes.DOUBLE:
						case DataTypes.BYTE:
						case DataTypes.SHORT:
						{
							return true;
						}
					}
					break;
				}
				case DataTypes.FLOAT:
				{
					switch (dataType2)
					{
						case DataTypes.INTEGER:
						case DataTypes.LONG:
						case DataTypes.FLOAT:
						case DataTypes.BYTE:
						case DataTypes.SHORT:
						{
							return true;
						}
					}
					break;
				}
				case DataTypes.LONG:
				{
					switch (dataType2)
					{
						case DataTypes.INTEGER:
						case DataTypes.LONG:
						case DataTypes.BYTE:
						case DataTypes.SHORT:
						{
							return true;
						}
					}
					break;
				}
				case DataTypes.INTEGER:
				{
					switch (dataType2)
					{
						case DataTypes.INTEGER:
						case DataTypes.BYTE:
						case DataTypes.SHORT:
						{
							return true;
						}
					}
					break;
				}
				case DataTypes.SHORT:
				{
					switch (dataType2)
					{
						case DataTypes.BYTE:
						case DataTypes.SHORT:
						{
							return true;
						}
					}
					break;
				}
				case DataTypes.BYTE:
				{
					switch (dataType2)
					{
						case DataTypes.BYTE:
						{
							return true;
						}
					}
					break;
				}
			}
		}
		return false;
	}

	/**
	 * This method is used to get String having string representation of each
	 * element of given collection separated by given delimiter. This method by
	 * default trims string representation of collection elements.
	 *
	 * @param coll
	 * @param delim
	 * @return
	 */
	public static String getDelimitedString(final Collection coll, final String delim)
	{
		return getDelimitedString(coll, delim, true);
	}

	/**
	 * This method is used to get String having string representation of each
	 * element of given collection separated by given delimiter. Pass trim as
	 * false to disable trim of string representation of collection elements.
	 *
	 * @param coll
	 * @param delim
	 * @param trim
	 * @return
	 */
	public static String getDelimitedString(final Collection coll, final String delim, final boolean trim)
	{
		final StringBuffer sb = new StringBuffer();
		if ((coll != null) && (coll.size() > 0))
		{
			final Object[] arr = coll.toArray();
			for (int i = 0; i < arr.length; i++)
			{
				if (arr[i] != null)
				{
					sb.append(trim ? arr[i].toString().trim() : arr[i].toString());
					sb.append(delim);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * This method is used to list of tokens delimited by given delimiter in
	 * given string.
	 */
	public static ArrayList getTokens(final String string, final String delim)
	{
		final ArrayList al = new ArrayList();
		if (string != null)
		{
			if (delim == null)
			{
				al.add(string);
			}
			else
			{
				final StringTokenizer tokenizer = new StringTokenizer(string, delim);
				while (tokenizer.hasMoreTokens())
				{
					al.add(tokenizer.nextToken());
				}
			}
		}
		return al;
	}

	/**
	 * This method is used to test given process whether it is terminated or
	 * not.
	 *
	 * @param p
	 * @return Returns true if process is terminated and returns false if it is
	 *         running.
	 */
	public static boolean isProcessTerminated(final Process p)
	{
		try
		{
			if (p != null)
			{
				p.exitValue();
			}
		}
		catch (final IllegalThreadStateException e)
		{
			return false;
		}
		return true;
	}

	/**
	 * ServerLauncher # readDataFromProcess
	 *
	 * @param process
	 */
	public static void readDataFromProcess(final Process process, final OutputStream out, final OutputStream err)
	{
		ServiceUtilities.readDataFromProcess(process, out, err);
	}

	/**
	 * ServerLauncher # readDataFromProcess
	 *
	 * @param process
	 */
	public static void readDataFromProcess(final Process process, final File opFolder)
	{
		ServiceUtilities.readDataFromProcess(process, opFolder);
	}

	/**
	 * Use this method to get exit value of given process. If process do not
	 * exit in given timeout period then it will destroy process and return -1.
	 *
	 * @param process
	 * @param timeout
	 * @return Returns exit value returned by process.
	 */
	public static int getExitValue(final Process process, final int timeout)
	{
		return ServiceUtilities.getExitValue(process, timeout);
	}

	public static int[] calculatePercentage(final int firstNumerator, final int secondNumerator, final int denominator)
	{
		final int[] percentages = new int[2];
		percentages[0] = calculatePercentage(firstNumerator, denominator);
		percentages[1] = calculatePercentage(secondNumerator, denominator);
		if (percentages[0] + percentages[1] > 100)
		{
			percentages[1] = Math.max(0, 100 - percentages[0]);
		}
		return percentages;
	}

	public static int calculatePercentage(final int numerator, final int denominator)
	{
		return (denominator != 0 ? Math.round(100f * numerator / denominator) : 0);
	}

	/**
	 * Use this method to generate name with given prefix and count from 1 -
	 * 100.
	 *
	 * @param map
	 * @param prefix
	 * @return It will return null if fail to generated name.
	 */
	public static String generateName(final Map map, final String prefix)
	{
		if ((map == null) || (prefix == null))
		{
			return null;
		}
		if (map.get(prefix) == null)
		{
			return prefix;
		}
		boolean bContinue = true;
		int count = 1;
		final int MAX_COUNT = 100;
		while (bContinue)
		{
			final String name = prefix + '_' + count;
			if (map.get(name) == null)
			{
				return name;
			}
			++count;
			if (count >= MAX_COUNT)
			{
				bContinue = false;
			}
		}
		return null;
	}

	/**
	 * @param name
	 */
	public static void makeValidJavaName(final StringBuffer name)
	{
		final int size = name.length();
		for (int i = 0; i < size; i++)
		{
			if (!Character.isJavaIdentifierPart(name.charAt(i)))
			{
				name.setCharAt(i, '_');
			}
		}
	}

	/**
	 * @param file
	 * @return
	 */
	public static String getCanonicalPath(final File file)
	{
		try
		{
			return file.getCanonicalPath();
		}
		catch (final IOException ioe)
		{
			AppLogger.getLogger().log(
					AppLogger.getPriority(AppLogger.FATAL), "cannot find canonical path", ioe); //$NON-NLS-1$
			return file.getAbsolutePath();
		}
	}

	/**
	 * @param sPath
	 * @return
	 */
	public static String getCanonicalPath(final String sPath)
	{
		return getCanonicalPath(new File(sPath));
	}

	public static String timeInMSToString(final long timeInMS)
	{
		return timeInNSToString(timeInMS * 1000000); // to convert ms into ns
	}

	public static String timeInNSToString(final long timeInNS)
	{
		double dTime = timeInNS;
		final String NS_STRING = " ns";
		String type = NS_STRING; //$NON-NLS-1$
		if (dTime > 1000)
		{
			dTime = dTime / 1000;
			type = " �s"; //$NON-NLS-1$
		}
		if (dTime > 1000)
		{
			dTime = dTime / 1000;
			type = " ms"; //$NON-NLS-1$
		}
		if (dTime > 1000)
		{
			dTime = dTime / 1000;
			type = " sec"; //$NON-NLS-1$
		}
		if (type.equalsIgnoreCase(" sec"))
		{
			if (dTime > 3600) // more than 1 hr
			{
				final int hr = (int) dTime / 3600;
				final int leftsecs = (int) dTime % 3600;
				final int min = (leftsecs) / 60;
				final int sec = leftsecs % 60;
				return hr + " hr" + ":" + min + " min" + ":" + sec + type;
			}
			if (dTime > 60) // more than 1 min
			{
				final int min = (int) dTime / 60;
				final int sec = ((int) dTime) % 60;
				return min + " min" + ":" + sec + type;
			}
		}
		return (type.equals(NS_STRING) ? String.valueOf((long) dTime) : StaticUtilities.getDecimalFormat()
				.format(dTime))
				+ type;
	}

	public static final String bytesToString(final long bytes)
	{
		return bytesToString((double) bytes);
	}

	public static final String bytesToString(double bBytes)
	{
		final String BYTE_STRING = " Bytes"; //$NON-NLS-1$
		String type = BYTE_STRING;
		if (bBytes > 1024)
		{
			bBytes = bBytes / 1024;
			type = " KB"; //$NON-NLS-1$
		}
		if (bBytes > 1024)
		{
			bBytes = bBytes / 1024;
			type = " MB"; //$NON-NLS-1$
		}
		if (bBytes > 1024)
		{
			bBytes = bBytes / 1024;
			type = " GB"; //$NON-NLS-1$
		}

		return (type.equalsIgnoreCase(BYTE_STRING) ? String.valueOf((long) bBytes) : StaticUtilities.getDecimalFormat()
				.format(bBytes))
				+ type;
	}

	public static boolean extractZip(final String zipFileName, final String destinationDir)
	{
		try
		{
			return ServiceUtilities.extractZip(zipFileName, destinationDir);
		}
		catch (final IOException e)
		{
			AppLogger.getLogger().log(
					AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); //$NON-NLS-1$
		}
		return false;
	}

	public static boolean extractZip(final InputStream zipis, final String destinationDir)
	{
		try
		{
			return ServiceUtilities.extractZip(zipis, destinationDir);
		}
		catch (final Exception e)
		{
			AppLogger.getLogger().log(
					AppLogger.getPriority(AppLogger.FATAL), e.getMessage(), e); //$NON-NLS-1$
			return false;
		}
	}
	
	public static String getNameForDevice(int deviceId, int orientation, String sBrowserPath)
	{
		if(deviceId == TEST_DEVICE_BROWSER)
		{
			String browserPath = sBrowserPath.trim().toLowerCase();
			if ((browserPath.indexOf("netscp") >= 0) || (browserPath.indexOf("netscape") >= 0)) //$NON-NLS-1$ //$NON-NLS-2$
			{
				return "Netscape";
			}
			else if (browserPath.indexOf("firefox") >= 0)
			{
				return "Firefox";
			}
			else if (browserPath.indexOf("mozilla") >= 0)
			{
				return "Mozilla";
			}
			else if (browserPath.indexOf("iexplore") >= 0)
			{
				return "IE";
			}
			else if (browserPath.indexOf("safari") >= 0)
			{
				return "Safari";
			}
			else if (browserPath.indexOf("chrome") >= 0)
			{
				return "Chrome";
			}
			else if (browserPath.indexOf("opera") >= 0)
			{
				return "Opera";
			}
			else
			{
				String brPath = sBrowserPath;
				int iLastSlash = brPath.lastIndexOf(File.separatorChar);
				
				if (iLastSlash == -1)
				{
					iLastSlash = brPath.lastIndexOf("/");
				}
	
				if(iLastSlash >= 0)
				{
					brPath = brPath.substring(iLastSlash + 1);
					
					iLastSlash = brPath.lastIndexOf(".");
					
					if(iLastSlash > 0)
					{
						brPath = brPath.substring(0, iLastSlash);
					}
				}
				return brPath;
			}
		}
		else if(deviceId == TEST_DEVICE_BLACKBERRY)
		{
			return "Blackberry";
		}
		else
		{
			if(orientation == DEVICE_ORIENTATION_HORIZONTAL)
			{
				return "iPhone - Horizontal";
			}
			else
			{
				return "iPhone - Vertical";
			}
		}
	}
	
	public static int getImageForDevice(int deviceId, String sBrowserPath)
	{
		if(deviceId == TEST_DEVICE_BROWSER)
		{
			String browserPath = sBrowserPath.trim().toLowerCase();
			if ((browserPath.indexOf("netscp") >= 0) || (browserPath.indexOf("netscape") >= 0)) //$NON-NLS-1$ //$NON-NLS-2$
			{
				return ImageConstants.NETSCAPE_16;
			}
			else if (browserPath.indexOf("firefox") >= 0)
			{
				return ImageConstants.FIREFOX_16;
			}
			else if (browserPath.indexOf("mozilla") >= 0)
			{
				return ImageConstants.MOZILLA_16;
			}
			else if (browserPath.indexOf("iexplore") >= 0)
			{
				return ImageConstants.IE_16;
			}
			else if (browserPath.indexOf("safari") >= 0)
			{
				return ImageConstants.SAFARI_16;
			}
			else if (browserPath.indexOf("chrome") >= 0)
			{
				return ImageConstants.CHROME_16;
			}
			else if (browserPath.indexOf("opera") >= 0)
			{
				return ImageConstants.OPERA_16;
			}
			else
			{
				return ImageConstants.BROWSER_16;
			}
		}
		else
		{
			return ImageConstants.SAFARI_16;
		}
	}
	
	public static String getBrowserType(int bt)
	{
		if (bt >= 0 && bt <= BROWSER_TYPES.length - 1)
		{
			return BROWSER_TYPES[bt];
		}
		return BROWSER_TYPES[6];
	}
	
	public static String getPID(String procName)
	{
		BufferedReader stdInput = null;
		String procPID = "";
		try
		{
			Process p = Runtime.getRuntime().exec("ps -ef");
		
			stdInput = new BufferedReader(new 
			InputStreamReader(p.getInputStream()));
	
			String s = null;
			while ((s = stdInput.readLine()) != null) 
			{
				if(s.contains(procName))
				{
					int i=0;
					int col=0;
					boolean space = false;
					while(i<s.length())
					{
						if(s.charAt(i)==' ')	space = true;
						else if(space==true)
						{
							space = false;
							col++;
						}
						if(col==2 && !space)
						{
							procPID += s.charAt(i);
						}
						i++;
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.fatal(e.getMessage(), e);
		}
		finally
		{
			try
			{
				if(stdInput!=null)	stdInput.close();
			}
			catch(Exception e)
			{
				logger.fatal(e.getMessage(), e);
			}
		}
		
		return procPID;
	}
	
	public static boolean isProcessRunning(String processName) 
	{
		if( ! getPID(processName).equals(""))	return true;
		
    	return false;
	}
	
	public static String toCamelCase(String s)
	{
		String[] parts = s.split("_");
		String camelCaseString = "";
		for (String part : parts){
			camelCaseString = camelCaseString + toProperCase(part);
		}
		return camelCaseString;
	}

	public static String toProperCase(String s)
	{
	    return s.substring(0, 1).toUpperCase() +
	               s.substring(1).toLowerCase();
	}

	public static String getFormattedStorageSize(long bytes)
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		
		if(bytes < QueryIOConstants.ONE_KB)	return bytes + " bytes";
		
		if(bytes < QueryIOConstants.ONE_MB)	return nf.format(bytes/(double)QueryIOConstants.ONE_KB) + " KB";

		if(bytes < QueryIOConstants.ONE_GB)	return nf.format(bytes/(double)QueryIOConstants.ONE_MB) + " MB";
		
		if(bytes < QueryIOConstants.ONE_TB)	return nf.format(bytes/(double)QueryIOConstants.ONE_GB) + " GB";
		
		return nf.format(bytes/(float)QueryIOConstants.ONE_TB) + " TB"; 
	}
	
//	public static String getHostAddress() {
//		final GetIpAddress gp = new GetIpAddress();
//		try{
//			gp.fetchAllDetails();
//			return gp.getPrimaryIPAddress();
//		} catch(Exception e){
//			try {
//				return InetAddress.getLocalHost().getHostAddress();
//			} catch (UnknownHostException e1) {
//				return "127.0.0.1";
//			}
//		}
//		
//	}
	
	public static String getFileExtension(String fileName){
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
	
	public static String getMD5Hash(String message)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(message.getBytes(), 0, message.length());
		String hash = new BigInteger(1, digest.digest()).toString(16);
		return hash;
	}
	
	public static void main(String[] args){
		try {
			System.out.println(getMD5Hash("admin"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int indexOf(String[] list, String value)
	{
		for(int i=0; i<list.length; i++)
		{
			if(list[i].equals(value))
				return i;
		}
		
		return -1;
	}
}