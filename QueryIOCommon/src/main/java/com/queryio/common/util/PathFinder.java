/*
 * @(#) PathFinder.java    1.0     Apr.16, 2003
 *
 * Copyright (C) 2002- 2003 Exceed Consultancy Services. All rights reserved.
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

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;

/**
 * This class provides static method to find Product Home, Images home etc. from
 * the URL provided
 * 
 * @author Exceed Consultancy Services
 * @version 1.0
 */
public class PathFinder {
	private static String sDevSuiteHome; // To Store Product Path
	private static String sDevSuiteUIHome; // To Store Devsuite UI Path
	private static String sImagesHome; // To Store Images Path
	private static String sLibHome; // To Store Lib folder path
	private static String sLastOpenedFolder; // To Store the Last Open Folder
	// private static FilenameFilter jarFilter;

	// static
	// {
	// jarFilter = new FilenameFilter()
	// {
	// public boolean accept(final File dir, final String name)
	// {
	// return name.endsWith(".jar"); //$NON-NLS-1$
	// }
	// };
	// }

	private static void checkXMLDriver() {
		final String xmlDriver = System.getProperty("org.xml.sax.driver"); //$NON-NLS-1$
		if (xmlDriver == null) {
			System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public static void setPath(final String path, final boolean ui) {
		checkXMLDriver();
		if (ui) {
			sDevSuiteUIHome = path;
		} else {
			sDevSuiteHome = path;
			sImagesHome = sDevSuiteHome + "images" + File.separatorChar;//$NON-NLS-1$
			sLibHome = sDevSuiteHome + "lib";//$NON-NLS-1$

			final String cpDir = getLibHome(true) + "apredistcommon.jar"; //$NON-NLS-1$
			System.setProperty("jasper.reports.compile.class.path", cpDir); //$NON-NLS-1$
		}
	}

	/*
	 * public static void setReportsPath(String path, int productType, boolean
	 * ui) { switch(productType) { case IProductConstants.CODE_ANALYZER: {
	 * sCAReportsHome = path; break; } case IProductConstants.UNIT_TESTER: {
	 * sUTReportsHome = path; break; } case IProductConstants.JAVA_PROFILER: {
	 * sJPReportsHome = path; break; } case IProductConstants.LOAD_TESTER: {
	 * sLTReportsHome = path; break; } case IProductConstants.FUNCTIONAL_TESTER:
	 * { sFTReportsHome = path; break; } case IProductConstants.CODE_COVERAGE: {
	 * sCCReportsHome = path; break; } case IProductConstants.AGENTLESS_MONITOR:
	 * { sAMReportsHome = path; break; } case
	 * IProductConstants.WIN_FUNCTIONAL_TESTER: { sWTReportsHome = path; break;
	 * } default: { break; } } }
	 */

	/**
	 * getProductPath
	 * 
	 * @param url
	 * @return String
	 */
	public static final String getProductPath(final URL url) {
		String sPath = null;
		/*
		 * Block to check whether the XML driver property is set or not If not
		 * then set it with XML parser class path.
		 */
		checkXMLDriver();

		try {
			if (url != null) {
				String sProductpath = url.getFile();
				if (url.getProtocol().equalsIgnoreCase("jar")) //$NON-NLS-1$
				{
					int iStartIndex = 0;
					iStartIndex = PlatformHandler.isWindows() ? (sProductpath.indexOf(":/") + 2) //$NON-NLS-1$
							: (sProductpath.indexOf(":") + 1); //$NON-NLS-1$
					sProductpath = sProductpath.substring(iStartIndex, sProductpath.indexOf("!/")); //$NON-NLS-1$ //$NON-NLS-2$
					sProductpath = new File(sProductpath).getParent();
					sProductpath = new File(sProductpath).getCanonicalPath();
				} else {
					final File f = new File(sProductpath, "../../../.."); //$NON-NLS-1$
					sProductpath = f.getPath();
					try {
						sProductpath = f.getCanonicalPath();
					} catch (final Exception ex) {
						sProductpath = f.getAbsolutePath();
					}
				}
				sPath = StaticUtilities.urlDecode(sProductpath); // ,
				// "UTF-8");
				// //$NON-NLS-1$
			} else {
				sPath = PlatformHandler.USER_DIR + File.separatorChar;// Set
				// Product
				// Home
			}
		} catch (final Exception e) {
			sPath = PlatformHandler.USER_DIR + File.separatorChar;
		}
		return sPath;
	}

	/**
	 * getDevSuiteHome
	 * 
	 * @return String
	 */
	public static final String getDevSuiteHome() {
		return sDevSuiteHome;
	}

	public static final String getDevSuiteHomeWithForwardSlash() {
		return StaticUtilities.replaceAll(sDevSuiteHome, '\\', '/');
	}

	/**
	 * Returns the imagesHome.
	 * 
	 * @return String
	 */
	public static final String getImagesHome() {
		return sImagesHome;
	}

	/**
	 * method getLibHome
	 * 
	 * @return devsuite\lib folder path
	 */
	public static final String getLibHome(final boolean bAddEndFileSeparator) {
		if (bAddEndFileSeparator && !sLibHome.endsWith(File.separator)) {
			return sLibHome + File.separatorChar;
		}
		return sLibHome;
	}

	public static final String getLibHome(final int productType, final boolean bAddEndFileSeparator) {
		final File stdHome = new File(getProductHome(productType));
		File tsCoreHome;
		if (stdHome.getName().indexOf('_') != -1) // will not be called in
													// development system
		{
			tsCoreHome = new File(stdHome.getParentFile(),
					ServiceUtilities.DIR_TEST_STUDIO + stdHome.getName().substring(stdHome.getName().indexOf('_')));

		} else {
			tsCoreHome = new File(stdHome.getParentFile(), ServiceUtilities.DIR_TEST_STUDIO);
		}
		String libHome = new File(tsCoreHome, "lib").getAbsolutePath();
		if (bAddEndFileSeparator && !libHome.endsWith(File.separator)) {
			return libHome + File.separatorChar;
		}
		return libHome;
	}

	public static final String getProductUIHome(final int productType) {
		String sProductHome = sDevSuiteUIHome;
		// switch (productType)
		// {
		// case IProductConstants.CODE_ANALYZER:
		// {
		// sProductHome = sCAProductUIHome;
		// break;
		// }
		// case IProductConstants.UNIT_TESTER:
		// {
		// sProductHome = sUTProductUIHome;
		// break;
		// }
		// case IProductConstants.JAVA_PROFILER:
		// {
		// sProductHome = sJPProductUIHome;
		// break;
		// }
		// case IProductConstants.LOAD_TESTER:
		// {
		// sProductHome = sLTProductUIHome;
		// break;
		// }
		// case IProductConstants.FUNCTIONAL_TESTER:
		// {
		// sProductHome = sFTProductUIHome;
		// break;
		// }
		// case IProductConstants.CODE_COVERAGE:
		// {
		// sProductHome = sCCProductUIHome;
		// break;
		// }
		// case IProductConstants.AGENTLESS_MONITOR:
		// {
		// sProductHome = sAMProductUIHome;
		// break;
		// }
		// case IProductConstants.APP_TEST:
		// {
		// sProductHome = sWTProductUIHome;
		// break;
		// }
		// }
		return sProductHome;
	}

	/**
	 * method getProductHome
	 * 
	 * @param productType
	 * @return
	 */
	public static final String getProductHome(final int productType) {
		String sProductHome = null;
		// switch (productType)
		// {
		// case IProductConstants.CODE_ANALYZER:
		// {
		// sProductHome = sCAProductHome;
		// break;
		// }
		// case IProductConstants.UNIT_TESTER:
		// {
		// sProductHome = sUTProductHome;
		// break;
		// }
		// case IProductConstants.JAVA_PROFILER:
		// {
		// sProductHome = sJPProductHome;
		// break;
		// }
		// case IProductConstants.LOAD_TESTER:
		// {
		// sProductHome = sLTProductHome;
		// break;
		// }
		// case IProductConstants.FUNCTIONAL_TESTER:
		// {
		// sProductHome = sFTProductHome;
		// break;
		// }
		// case IProductConstants.CODE_COVERAGE:
		// {
		// sProductHome = sCCProductHome;
		// break;
		// }
		// case IProductConstants.AGENTLESS_MONITOR:
		// {
		// sProductHome = sAMProductHome;
		// break;
		// }
		// case IProductConstants.APP_TEST:
		// {
		// sProductHome = sWTProductHome;
		// break;
		// }
		// }
		return sProductHome;
	}

	/**
	 * method getReportsHome
	 * 
	 * @param productType
	 * @return
	 */
	public static final String getReportsHome(final int productType) {
		String sReportsHome = null;
		// switch (productType)
		// {
		// case IProductConstants.CODE_ANALYZER:
		// {
		// sReportsHome = sCAReportsHome;
		// break;
		// }
		// case IProductConstants.UNIT_TESTER:
		// {
		// sReportsHome = sUTReportsHome;
		// break;
		// }
		// case IProductConstants.JAVA_PROFILER:
		// {
		// sReportsHome = sJPReportsHome;
		// break;
		// }
		// case IProductConstants.LOAD_TESTER:
		// {
		// sReportsHome = sLTReportsHome;
		// break;
		// }
		// case IProductConstants.FUNCTIONAL_TESTER:
		// {
		// sReportsHome = sFTReportsHome;
		// break;
		// }
		// case IProductConstants.CODE_COVERAGE:
		// {
		// sReportsHome = sCCReportsHome;
		// break;
		// }
		// case IProductConstants.AGENTLESS_MONITOR:
		// {
		// sReportsHome = sAMReportsHome;
		// break;
		// }
		// case IProductConstants.APP_TEST:
		// {
		// sReportsHome = sWTReportsHome;
		// break;
		// }
		// }
		return sReportsHome;
	}

	/**
	 * @param productType
	 * @return
	 */
	public static final String getOutputHome(final int productType) {
		String sOutputHome = null;
		// switch (productType)
		// {
		// case IProductConstants.CODE_ANALYZER:
		// {
		// sOutputHome = sCAOutputHome;
		// break;
		// }
		// case IProductConstants.UNIT_TESTER:
		// {
		// sOutputHome = sUTOutputHome;
		// break;
		// }
		// case IProductConstants.JAVA_PROFILER:
		// {
		// sOutputHome = sJPOutputHome;
		// break;
		// }
		// case IProductConstants.LOAD_TESTER:
		// {
		// sOutputHome = sLTOutputHome;
		// break;
		// }
		// case IProductConstants.FUNCTIONAL_TESTER:
		// {
		// sOutputHome = sFTOutputHome;
		// break;
		// }
		// case IProductConstants.CODE_COVERAGE:
		// {
		// sOutputHome = sCCOutputHome;
		// break;
		// }
		// case IProductConstants.AGENTLESS_MONITOR:
		// {
		// sOutputHome = sAMOutputHome;
		// break;
		// }
		// case IProductConstants.APP_TEST:
		// {
		// sOutputHome = sWTOutputHome;
		// break;
		// }
		// }
		return sOutputHome;
	}

	/**
	 * getAbsolutePath
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getAbsolutePath(String fileName, final int productType) {
		try {
			if ((fileName != null) && (fileName.length() > 0) && (fileName.charAt(0) == '.')
					&& (getProductHome(productType) != null)) // $NON-NLS-1$
			{
				fileName = fileName.substring(2, fileName.length());
				fileName = new File(getProductHome(productType), fileName).getCanonicalPath();
			}
		} catch (final Exception e) {
			fileName = new File(getProductHome(productType), fileName).getAbsolutePath();
		}
		return fileName;
	}

	/**
	 * Method getRelativePath.
	 * 
	 * @param fileName
	 * @return String
	 */
	public static String getRelativePath(String fileName, final int productType) {
		if ((fileName != null) && (fileName.length() > 0)) {
			if (fileName.charAt(0) != '.') // $NON-NLS-1$
			{
				if (fileName.startsWith(getProductHome(productType))) {
					final int pathIndex = getProductHome(productType).endsWith(File.separator)
							? (getProductHome(productType).length() - 1) : getProductHome(productType).length();
					if (fileName.charAt(pathIndex) == File.separatorChar) {
						fileName = fileName.substring(pathIndex);
						if ((fileName.length() > 1) && (fileName.charAt(1) == '.')) {
							if ((fileName.length() > 2) && (fileName.charAt(2) != '.')) {
								return fileName.substring(1);
							}
						}
						return "." + fileName /* fileName.substring(pathIndex) */; //$NON-NLS-1$
					}
				}
			}
		}
		return fileName;
	}

	/**
	 * replaces all the " " with "\ " if the platform is other than windows. if
	 * the platform is windows then returns "\"" + origPath + "\""
	 * 
	 * @param origPath
	 * @return
	 */
	public static String getSystemSpecificEscapedPath(final String origPath) {
		return ServiceUtilities.getSystemSpecificEscapedPath(origPath, PlatformHandler.isWindows());
	}

	/**
	 * 
	 * method getSystemSpecificEscapedPath
	 * 
	 * @param origPath
	 * @param bForWindows
	 * @return
	 */
	public static String getSystemSpecificEscapedPath(final String origPath, final boolean bForWindows) {
		return ServiceUtilities.getSystemSpecificEscapedPath(origPath, bForWindows);
		/*
		 * String path = null; if(bForWindows) { if (origPath.endsWith("\\"))
		 * //$NON-NLS-1$ { origPath += "\\"; //$NON-NLS-1$ } path = "\"" +
		 * origPath + "\""; //$NON-NLS-1$ //$NON-NLS-2$ } else { StringBuffer
		 * buffer = new StringBuffer(); char[] chars = origPath.toCharArray();
		 * for(int i=0; i<chars.length; i++) { if(chars[i] == ' ') {
		 * buffer.append('\\'); } buffer.append(chars[i]); } path =
		 * buffer.toString(); //StaticUtilities.replaceAll(origPath, " ", "\\
		 * "); } return path;
		 */
	}

	/**
	 * getSLastOpenedFolder
	 * 
	 * @return
	 */
	public static String getLastOpenedFolder() {
		return sLastOpenedFolder;
	}

	/**
	 * setSLastOpenedFolder
	 * 
	 * @param bString
	 */
	public static void setLastOpenedFolder(final String bString) {
		sLastOpenedFolder = bString;
	}

	/**
	 * Returns JRE path This method assumes that jdkPath parameter is a valid
	 * JRE folder or a parent folder
	 * 
	 * @param jdkPath
	 * @return
	 */
	public static String getJrePath(final String jdkPath) {
		return ServiceUtilities.getJrePath(jdkPath);
		// if(PlatformHandler.isMacOS())
		// {
		// if(jdkPath.indexOf("Home") != -1) //$NON-NLS-1$
		// {
		// return jdkPath;
		// }
		// File rtJarFile = new File(jdkPath, "Classes"+ File.separatorChar
		// +"classes.jar"); //$NON-NLS-1$ //$NON-NLS-2$
		// return rtJarFile.exists() ? jdkPath + File.separatorChar + "Home" :
		// jdkPath; //$NON-NLS-1$
		// }
		// File rtJarFile = new File(jdkPath, "lib"+ File.separatorChar
		// +"rt.jar"); //$NON-NLS-1$ //$NON-NLS-2$
		// return rtJarFile.exists() ? jdkPath : jdkPath + File.separatorChar +
		// "jre"; //$NON-NLS-1$
	}

	/**
	 * method getBootClassPath
	 * 
	 * @param JDKHome
	 * @return
	 */
	public static String getBootClassPath(final String JDKHome) {
		// TODO: Don't know what forms the bootclasspath on Mac OS X
		// if it is /Library/Java/Home/lib then following code will work fine
		// else we need to change it to
		// new File(PathFinder.getJrePath(JDKHome), "../Classes");
		File jreLibFolder = null;
		if (PlatformHandler.isMacOS()) {
			jreLibFolder = new File(PathFinder.getJrePath(JDKHome), ".." + File.separatorChar + "Classes"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			jreLibFolder = new File(PathFinder.getJrePath(JDKHome), "lib"); //$NON-NLS-1$
		}
		final StringBuffer sb = new StringBuffer();

		final File[] libFiles = jreLibFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar"); //$NON-NLS-1$
			}
		});
		if (libFiles != null) {
			for (int i = 0; i < libFiles.length; i++) {
				final File file = libFiles[i];
				sb.append(file.getAbsolutePath());
				sb.append(File.pathSeparatorChar);
			}
		}
		return sb.toString();
	}

	// key: application id, value: Standalone Home
	private static IntHashMap mapStandaloneHome = new IntHashMap(3);

	private static String ECLIPSE_HOME = null;

	/**
	 * This method will return defined eclipse home using
	 * {@link #setEclipseHome(String)} method.
	 * 
	 * @see #setEclipseHome(String)
	 * @return
	 */
	public static String getEclipseHome() {
		return ECLIPSE_HOME;
	}

	/**
	 * Use this method to define eclipseHome.
	 * 
	 * @return
	 */
	public static void setEclipseHome(final String eclipseHome) {
		ECLIPSE_HOME = eclipseHome;
	}

	public static String resolveLocation(String location) {
		if (PlatformHandler.isWindows() && (location.charAt(0) == '/')) {
			location = location.substring(1);
		}
		try {
			location = new File(location).getCanonicalPath() + File.separatorChar;
		} catch (final IOException ex) {
			location = new File(location).getAbsolutePath() + File.separatorChar;
		}
		return location;
	}

	/*
	 * Copy of this method exists in JREFunctions. Please keep both copies in
	 * sync
	 */
	public static String getClassPathEntry(final String parent, final String filter, final String jarName) {
		final File folder = new File(parent);

		if (folder.exists()) {
			final File[] files = folder.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return (pathname.getName().startsWith(filter));
				}
			});
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					return files[i].getAbsolutePath();
				} else if (jarName != null) {
					final File file = searchFile(files[i], jarName);
					if (file != null) {
						return file.getAbsolutePath();
					}
				}
			}
		}
		return null;
	}

	/*
	 * Copy of this method exists in JREFunctions. Please keep both copies in
	 * sync
	 */
	private static File searchFile(final File parent, final String fileName) {
		final File[] files = parent.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile() && files[i].getName().equals(fileName)) {
				return files[i];
			} else if (files[i].isDirectory()) {
				final File file = searchFile(files[i], fileName);
				if (file != null) {
					return file;
				}
			}
		}
		return null;
	}

	/**
	 * Use this method to set DevTest / WebTest / FunctionalTest standalone
	 * home.
	 * 
	 * @param applicationID
	 * @param path
	 */
	public static void setStandaloneHome(final int applicationID, final String path) {
		mapStandaloneHome.put(applicationID, path);
	}

	/**
	 * Use this method to get DevTest / WebTest / FunctionalTest standalone
	 * home.
	 * 
	 * @param applicationID
	 * @return
	 */
	public static String getStandaloneProductHome(final int applicationID) {
		return (String) mapStandaloneHome.get(applicationID);
	}
}