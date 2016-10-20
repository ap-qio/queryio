/*
 * @(#) PlatformHandler.java
 *
 * Copyright (C) 2002 - 2004 Exceed Consultancy Services. All rights reserved.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.queryio.common.IOSProtocolConstants;

/**
 * This class is used to get platform specific Information. All methods of this
 * class are static. This class has private constructor, hence cannot be
 * instantiated.
 * 
 * @author Exceed Consultancy Services.
 * @version 1.0
 */
public final class PlatformHandler
{
	private static String emptyString = ""; //$NON-NLS-1$
	private static File nativeLibDirectory;
	private static Map envVars;
	private static String scriptSuffixes[];
	private static String browserStartCommand;

	public static final String OS_NAME = System.getProperty("os.name"); //$NON-NLS-1$
	public static final String JAVA_OS_ARCH = System.getProperty("os.arch"); //$NON-NLS-1$
	public static final String JAVA_ARCH = System.getProperty(JDKProperties.ARCH_DATA_MODEL); //$NON-NLS-1$

	public static final String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
	public static final String PATH_SEPARATOR = File.pathSeparator;
	public static final String FILE_SEPARATOR = File.separator;

	public static final String USER_DIR = System.getProperty("user.dir"); //$NON-NLS-1$

	/**
	 * Private constructor of the class to prevent it from getting instantiated.
	 */
	private PlatformHandler()
	{
		// EMPTY constructor to avoid instantiation of this class.
	}

	/**
	 * Method getJavadocExecutable.
	 * 
	 * @return String
	 */
	// public static String getJavadocExecutable()
	// {
	// if(isWindows())
	// {
	// return "javadoc.exe"; //$NON-NLS-1$
	// }
	// return "javadoc"; //$NON-NLS-1$
	// }
	/**
	 * Method setNativeLibDirectory.
	 * 
	 * @param file
	 */
	public static void setNativeLibDirectory(final File file)
	{
		nativeLibDirectory = file;
	}

	/**
	 * Method getNativeLibDirectory.
	 * 
	 * @return File
	 */
	public static File getNativeLibDirectory()
	{
		return nativeLibDirectory;
	}

	/**
	 * Method setBrowserStartCommand.
	 * 
	 * @param s
	 */
	public static void setBrowserStartCommand(final String s)
	{
		browserStartCommand = s;
	}

	/**
	 * Method getBrowserStartCommand.
	 * 
	 * @return String
	 */
	public static String getBrowserStartCommand()
	{
		return browserStartCommand;
	}

	/**
	 * Method showURL.
	 * 
	 * @param url
	 */
	public static void showURL(final URL url)
	{
		showURL(url.toExternalForm());
	}

	/**
	 * Method showURL.
	 * 
	 * @param s
	 */
	public static void showURL(final String s)
	{
		if ((browserStartCommand == null) || (browserStartCommand.length() == 0))
		{
			defaultShowURL(s);
		}
		else
		{
			customShowURL(s);
		}
	}

	/**
	 * Method customShowURL.
	 * 
	 * @param s
	 */
	private static void customShowURL(final String s)
	{
		execCommand(browserStartCommand + s);
	}

	/**
	 * Method defaultShowURL.
	 * 
	 * @param s
	 */
	private static void defaultShowURL(final String url)
	{
		if (isWindows())
		{
			execCommand("rundll32.exe url.dll,FileProtocolHandler " + url); //$NON-NLS-1$
		}
		else if(PlatformHandler.isMacOS())
		{
			execCommand("open "+ url);
		}
		else
		{
			execCommand("firefox " + url + " || mozilla " + url + " || konqueror " + url + " || opera " + url); //$NON-NLS-1$
		}
	}

	/**
	 * Method isSolaris.
	 * 
	 * @return boolean
	 */
	public static boolean isSolaris()
	{
		return isSolaris(OS_NAME);
	}

	/**
	 * Method isSolaris.
	 * 
	 * @return boolean
	 */
	public static boolean isSolaris(final String osName)
	{
		return (osName != null) && osName.equals("SunOS"); //$NON-NLS-1$
	}

	/**
	 * Method isSolarisSparc.
	 * 
	 * @return boolean
	 */
	public static boolean isSolarisSparc()
	{
		return isSolarisSparc(OS_NAME, JAVA_OS_ARCH);
	}

	/**
	 * Method isSolarisSparc.
	 * 
	 * @return boolean
	 */
	public static boolean isSolarisSparc(final String osName, final String osArch)
	{
		return (osArch != null) && isSolaris(osName) && osArch.equals("sparc"); //$NON-NLS-1$
	}

	/**
	 * Method isLinux.
	 * 
	 * @return boolean
	 */
	public static boolean isLinux()
	{
		return isLinux(OS_NAME);
	}

	/**
	 * Method isLinux.
	 * 
	 * @return boolean
	 */
	public static boolean isLinux(final String osName)
	{
		return (osName != null) && osName.toLowerCase().startsWith("linux"); //$NON-NLS-1$
	}

	/**
	 * Method isLinuxX86.
	 * 
	 * @return boolean
	 */
	public static boolean isLinuxX86(final String osName, final String osArch)
	{
		return (osArch != null) && isLinux(osName) && (osArch.indexOf("86") > -1); //$NON-NLS-1$
	}

	/**
	 * Method isLinuxX86.
	 * 
	 * @return boolean
	 */
	public static boolean isLinuxX86()
	{
		return isLinuxX86(OS_NAME, JAVA_OS_ARCH);
	}

	/**
	 * Method isMac.
	 * 
	 * @return boolean
	 */
	public static boolean isMacOS()
	{
		return isMacOS(OS_NAME);
	}

	/**
	 * Method isMac.
	 * 
	 * @return boolean
	 */
	public static boolean isMacOS(final String osName)
	{
		return (osName != null) && osName.toLowerCase().startsWith("mac"); //$NON-NLS-1$
	}

	/**
	 * Method isMacOSX.
	 * 
	 * @return boolean
	 */
	public static boolean isMacOSX()
	{
		return isMacOSX(OS_NAME, JAVA_OS_ARCH);
	}

	/**
	 * Method isMacOSX.
	 * 
	 * @return boolean
	 */
	public static boolean isMacOSX(final String osName, final String osArch)
	{
		return (osArch != null) && isMacOS(osName) && (osArch.toUpperCase().indexOf("X") > -1); //$NON-NLS-1$
	}

	/**
	 * Method isWindows.
	 * 
	 * @return boolean
	 */
	public static boolean isWindows()
	{
		return isWindows(OS_NAME);
	}

	/**
	 * Method isWindows.
	 * 
	 * @return boolean
	 */
	private static boolean isWindows(final String osName)
	{
		return (osName != null) && osName.toLowerCase().startsWith("win"); //$NON-NLS-1$
	}

	// /**
	// * Method isWindows95.
	// * @return boolean
	// */
	// public static boolean isWindows95()
	// {
	// return isWindows95(OS_NAME);
	// }
	//
	// /**
	// * Method isWindows95.
	// * @return boolean
	// */
	// public static boolean isWindows95(String osName)
	// {
	// return osName != null &&
	// (
	// osName.startsWith("Windows 98") //$NON-NLS-1$
	// || osName.startsWith("Windows 95") //$NON-NLS-1$
	// || osName.startsWith("Windows ME") //$NON-NLS-1$
	// );
	// }

	/**
	 * This method will returnt true if java data model architecture is 64bit.
	 * 
	 * @return
	 */
	public static boolean is64bitProductVersion()
	{
		// non of the standard 32bit OS architecture name has 64 as part of
		// their string hence it is expected that if "64" is part of OS
		// architecture name then it is 64bit OS
		return (JAVA_ARCH != null) && JAVA_ARCH.equalsIgnoreCase("64");
	}

	/**
	 * Method getPlatformDescriptor.
	 * 
	 * @return String
	 */
	public static String getPlatformDescriptor()
	{
		if (isWindows())
		{
			return "windows"; //$NON-NLS-1$
		}
		if (isLinuxX86())
		{
			return "linux-x86"; //$NON-NLS-1$
		}
		if (isMacOS())
		{
			return "mac"; //$NON-NLS-1$
		}
		if (isSolarisSparc())
		{
			return "solaris-sparc"; //$NON-NLS-1$
		}
		return "unknown"; //$NON-NLS-1$
	}

	/**
	 * Method getLibraryName.
	 * 
	 * @param s
	 * @return String
	 */
	public static String getLibraryName(final String s)
	{
		if (isWindows())
		{
			return s + ".dll"; //$NON-NLS-1$
		}
		if (isMacOS())
		{
			return "lib" + s + ".jnilib"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return "lib" + s + ".so"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Method getLibraryEnvVarName.
	 * 
	 * @return String
	 */
	public static String getLibraryEnvVarName()
	{
		if (isWindows())
		{
			return "PATH"; //$NON-NLS-1$
		}
		else if (isMacOS())
		{
			return "DYLD_LIBRARY_PATH"; //$NON-NLS-1$
		}

		return "LD_LIBRARY_PATH"; //$NON-NLS-1$
	}

	/**
	 * Method getEnvVariables.
	 * 
	 * @return Map
	 */
	public static Map getEnvVariables()
	{
		if (envVars == null)
		{
			initEnvVariables();
		}
		return envVars;
	}

	/**
	 * Method initEnvVariables.
	 */
	private static void initEnvVariables()
	{
		envVars = new HashMap();
		if (isWindows())
		{
			initEnvVariablesWindows();
		}
		else
		{
			initEnvVariablesUnix();
		}
	}

	/**
	 * Method initEnvVariablesWindows.
	 */
	private static void initEnvVariablesWindows()
	{
		initEnvVariablesFromProcess("cmd.exe /c set"); //$NON-NLS-1$
	}

	/**
	 * Method initEnvVariablesUnix.
	 */
	private static void initEnvVariablesUnix()
	{
		initEnvVariablesFromProcess("/usr/bin/env"); //$NON-NLS-1$
	}

	/**
	 * Method initEnvVariablesFromProcess.
	 * 
	 * @param s
	 */
	private static void initEnvVariablesFromProcess(final String s)
	{
		try
		{
			final Process process = Runtime.getRuntime().exec(s);
			final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String s1;
			while ((s1 = bufferedreader.readLine()) != null)
			{
				breakUpEnvVariable(s1);
			}
		}
		catch (final IOException ioexception)
		{
			// do not log this
		}
	}

	/**
	 * Method breakUpEnvVariable.
	 * 
	 * @param s
	 */
	private static void breakUpEnvVariable(final String s)
	{
		final int i = s.indexOf(61);
		if (i > -1)
		{
			final String s1 = s.substring(0, i);
			final String s2 = s.substring(i + 1);
			envVars.put(s1, s2);
		}
	}

	/**
	 * Method execCommand.
	 * 
	 * @param s
	 */
	private static void execCommand(final String s)
	{
		if (s != null)
		{
			try
			{
				Runtime.getRuntime().exec(s);
			}
			catch (final IOException ioexception)
			{
				// do not log this
			}
		}
	}

	public static String getKeyToolExecutable(String path)
	{
		path = path.trim();
		if (!path.endsWith(FILE_SEPARATOR))
		{
			path += FILE_SEPARATOR;
		}
		String sCommand = emptyString;
		if (isMacOS())
		{
			if (path.indexOf("Home") == -1) //$NON-NLS-1$
			{
				sCommand = path + "Home" + FILE_SEPARATOR + "bin" + FILE_SEPARATOR + getJavaExecutable(false); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				sCommand = path + "bin" + FILE_SEPARATOR + getKeyToolExecutable(); //$NON-NLS-1$
			}
		}
		else
		{
			sCommand = path + "bin" + FILE_SEPARATOR + getKeyToolExecutable(); //$NON-NLS-1$
		}
		try
		{
			sCommand = new File(sCommand).getCanonicalPath();
		}
		catch (final Exception e)
		{
			sCommand = new File(sCommand).getAbsolutePath();
		}
		return sCommand;
	}

	public static String getJavaExecutable(final String path)
	{
		return getJavaExecutable(path, false);
	}

	/**
	 * Method getJavaExecutable.
	 * 
	 * @return String
	 */
	public static String getJavaExecutable(String path, final boolean nonui)
	{
		path = path.trim();
		if (!path.endsWith(FILE_SEPARATOR))
		{
			path += FILE_SEPARATOR;
		}
		String sCommand = emptyString;
		if (isMacOS())
		{
			if (path.indexOf("Home") == -1) //$NON-NLS-1$
			{
				sCommand = path + "Home" + FILE_SEPARATOR + "bin" + FILE_SEPARATOR + getJavaExecutable(nonui); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				sCommand = path + "bin" + FILE_SEPARATOR + getJavaExecutable(nonui); //$NON-NLS-1$
			}
		}
		else
		{
			sCommand = path + "bin" + FILE_SEPARATOR + getJavaExecutable(nonui); //$NON-NLS-1$
		}
		try
		{
			sCommand = new File(sCommand).getCanonicalPath();
		}
		catch (final Exception e)
		{
			sCommand = new File(sCommand).getAbsolutePath();
		}
		return sCommand;
	}

	/**
	 * Method getJavaExecutable.
	 * 
	 * @param bSwtApplication
	 * @return String
	 */
	private static String getJavaExecutable(final boolean nonui)
	{
		if (isWindows())
		{
			return nonui ? "java.exe" : "javaw.exe"; //$NON-NLS-1$
		}
		return "java"; //$NON-NLS-1$
	}

	private static String getKeyToolExecutable()
	{
		if (isWindows())
		{
			return "keytool.exe"; //$NON-NLS-1$
		}
		return "keytool"; //$NON-NLS-1$
	}

	/**
	 * PlatformHandler # isAllJarsInClasspath
	 * 
	 * @return
	 */
	public static boolean isAllJarsInClasspath()
	{
		return true;
	}

	/**
	 * Method getWorkaroundPath.
	 * 
	 * @param s
	 * @return String
	 */
	public static String getWorkaroundPath(String s)
	{
		int i;
		while ((i = s.indexOf("%20")) > -1) //$NON-NLS-1$
		{
			s = s.substring(0, i) + " " + s.substring(i + 3); //$NON-NLS-1$
		}
		return s;
	}

	/**
	 * Method getScriptSuffixes.
	 * 
	 * @return String[]
	 */
	public static String[] getScriptSuffixes()
	{
		if (scriptSuffixes == null)
		{
			if (isWindows())
			{
				scriptSuffixes = (new String[] { "bat", "cmd" //$NON-NLS-1$ //$NON-NLS-2$
				});
			}
			else
			{
				scriptSuffixes = (new String[] { "", "sh" //$NON-NLS-1$ //$NON-NLS-2$
				});
			}
		}
		return scriptSuffixes;
	}

	/**
	 * Method getCommentIdentifier.
	 * 
	 * @return String
	 */
	public static String getCommentIdentifier()
	{
		if (isWindows())
		{
			return "rem"; //$NON-NLS-1$
		}
		return "#"; //$NON-NLS-1$
	}

	public static int getOSType()
	{
		return getOSType(OS_NAME);
	}

	public static int getOSType(final String osName)
	{
		if (osName == null)
		{
			return IOSProtocolConstants.WINDOWS;
		}
		if (isWindows(osName))
		{
			return IOSProtocolConstants.WINDOWS;
		}
		else if (isLinux(osName))
		{
			return IOSProtocolConstants.LINUX;
		}
		else if (isSolaris(osName))
		{
			return IOSProtocolConstants.SOLARIS;
		}
		else if (isMacOS(osName))
		{
			return IOSProtocolConstants.MACOSX;
		}
		return IOSProtocolConstants.UNDEFINED;
	}

	// /**
	// * This method is intended to be used by notification classes only
	// * Returns true only if JDK version is 1.4 or higher
	// * @return
	// */
	// public static boolean isJDKCompatible()
	// {
	// String javaVersion = System.getProperty("java.specification.version");
	// if("1.4".compareTo(javaVersion) > 0)
	// {
	// return false;
	// }
	// return true;
	// }

	/**
	 * Method encodeSpaces.
	 * 
	 * @param s
	 * @return String
	 */
	/*
	 * private static String encodeSpaces(String s) { StringBuffer stringbuffer =
	 * new StringBuffer(); int i = s.length(); for(int j = 0; j < i; j++) {
	 * if(s.charAt(j) == ' ') { stringbuffer.append("%20"); //$NON-NLS-1$ } else {
	 * stringbuffer.append(s.charAt(j)); } }
	 * 
	 * return stringbuffer.toString(); }
	 */

}
