/*
 * @(#) NameValidator.java   1.0     26/03/20032 (DD/MM/YYYY)
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

import java.io.File;

/**
 * This class has got static methods to validate various things like name, port,
 * path etc.
 * 
 * @author Exceed Consultancy Services
 * @version 1.0
 */
public final class NameValidator {

	public static final int TOMCAT_APPSERVER = 0;
	public static final int WEBLOGIC_APPSERVER = 1;
	public static final int WEBSPHERE_APPSERVER = 2;
	public static final int JRUN_APPSERVER = 3;
	public static final int JBOSS_APPSERVER = 4;

	public static final int FILE = 0;
	public static final int DIR = 1;
	public static final int FILEDIR = 2;
	// private static final String DOT = "."; //$NON-NLS-1$
	public static final int MAX_PROJECT_NAME_LENGTH = 200;

	private static String sError = null;

	/**
	 * @see java.lang.Object#Object()
	 */
	private NameValidator() {
		// EMPTY private constructor to avoid instantiation of this class.
	}

	/**
	 * Method getFilenameFromPath.
	 * 
	 * @param sFullName
	 * @return String
	 */
	public static final String getFilenameFromPath(final String sFullName) {
		if (sFullName == null) {
			return null;
		}
		if (sFullName.trim().length() == 0) {
			return ""; //$NON-NLS-1$
		}
		final int iLastSlash = sFullName.lastIndexOf(File.separatorChar);
		if (iLastSlash < 0) {
			return sFullName;
		}
		return sFullName.substring(iLastSlash + 1);
	}

	/**
	 * This method returns true if the argument can be used as a valid port
	 * number i.e. it is between 1 and 65535 (inclusive).
	 * 
	 * @param port
	 * @return boolean
	 */
	public static boolean isPortValid(final int port) {
		if ((port > 0) && (port < 65536)) {
			return true;
		}

		return false;
	}

	/**
	 * Method isInputValid.
	 * 
	 * @param obj
	 * @return boolean
	 */
	public static boolean isInputValid(final Object obj) {
		return true;
		// // Check for non-empty string and
		// // \ / : * ? " < > | characters should not appear in it.
		// final String sName = (String) obj;
		// // sName = sName.trim();
		// sError = null;
		// if (sError == null)
		// {
		// return true;
		// }
		// return false;
	}

	/**
	 * checkWebApplicationPath
	 * 
	 * @param documentRoot
	 * @return
	 */
	public static boolean checkWebApplicationPath(final String documentRoot) {
		return new File(documentRoot, "WEB-INF").exists(); //$NON-NLS-1$
	}

	/**
	 * Method isProjectNameValid. This method checks if the Entered Project File
	 * name is valid and it will also check if the Project Name is less than the
	 * specified number of Characters.
	 * 
	 * @param name
	 * @return boolean
	 */
	public static boolean isProjectNameValid(String name) {
		name = name.trim();
		sError = null;
		if (isInputValid(name)) {
			if (name.length() > NameValidator.MAX_PROJECT_NAME_LENGTH) {
				// sError =
				// RM.getString("ERR_PROJECT_NAME_LENGTH_EXCEEDS_MAX_LIMIT");
				// //$NON-NLS-1$
			}
		}
		// Removed the word Project as the same dialog is be used for repository
		// export also
		// else
		// {
		// sError = "Project " + sError;
		// }
		if (sError == null) {
			return true;
		}
		return false;
	}

	/**
	 * Method getErrorMessage.
	 * 
	 * @return String
	 */
	public static String getErrorMessage() {
		return sError;
	}

	/**
	 * Checks whether the name is valid. If name contains any of invalid
	 * characters, it returns false, else returns true.
	 * 
	 * @param sName
	 *            name that is to be validated
	 * @return boolean
	 */
	public static boolean isNameValid(final String sName) {
		// \ / : ; * ? " < > | characters not allowed
		boolean bValid = true;
		if (sName != null) {
			for (int i = 0; bValid && (i < sName.length()); i++) {
				switch (sName.charAt(i)) {
				case ' ':
					bValid = false;
					break;
				case '\\':
					bValid = false;
					break;
				case '/':
					bValid = false;
					break;
				case ':':
					bValid = false;
					break;
				case ';':
					bValid = false;
					break;
				case '*':
					bValid = false;
					break;
				case '?':
					bValid = false;
					break;
				case '\"':
					bValid = false;
					break;
				case '<':
					bValid = false;
					break;
				case '>':
					bValid = false;
					break;
				case '|':
					bValid = false;
					break;
				}
			}
		}
		return bValid;
	}

	/**
	 * Method isValidPath.
	 * 
	 * @param path
	 * @param fileOrDirectory
	 * @return boolean
	 */
	public static boolean isValidPath(String path, final String parent, final int fileOrDirectory) {
		if ((path == null) || (path.trim().length() == 0)) {
			return false;
		}
		path = path.trim();
		File f = new File(path);
		if ((path.charAt(0) == '.') && (parent != null) && (parent.trim().length() > 0)) {
			f = new File(parent, path);
		}
		if (f.exists()) {
			if (fileOrDirectory == DIR) {
				return f.isDirectory();
			}
			if (fileOrDirectory == FILE) {
				return !f.isDirectory();
			}
			return true;
		}
		return false;
	}

	/**
	 * isValidPath
	 * 
	 * @param path
	 * @param fileOrDirectory
	 * @return
	 */
	public static boolean isValidPath(final String path, final int fileOrDirectory) {
		return isValidPath(path, null, fileOrDirectory);
	}

	/**
	 * Method isValidPath.
	 * 
	 * @param path
	 * @return boolean
	 */
	public static boolean isValidPath(final String path) {
		return isValidPath(path, FILEDIR);
	}

	/**
	 * isValidPath
	 * 
	 * @param path
	 * @param parent
	 * @return
	 */
	public static boolean isValidPath(final String path, final String parent) {
		return isValidPath(path, parent, FILEDIR);
	}

	/**
	 * Method isValidJavaPath.
	 * 
	 * @param path
	 * @return boolean
	 */
	public static boolean isValidJavaPath(final String path) {
		// final File file = new File(PlatformHandler.getJavaExecutable(path));
		// return file.exists();
		return true;
	}

	/**
	 * Method isValidAppServerPath.
	 * 
	 * @param serverType
	 * @param path
	 * @return boolean
	 */
	public static boolean isValidAppServerPath(final int serverType, final String path) {
		boolean bFound = false;

		switch (serverType) {
		case TOMCAT_APPSERVER: {
			final File file = new File(path, "bin\\catalina.bat"); //$NON-NLS-1$
			bFound = file.exists();
			break;
		}
		case WEBLOGIC_APPSERVER: {
			bFound = true;
			break;
		}
		case WEBSPHERE_APPSERVER: {
			bFound = true;
			break;
		}
		case JRUN_APPSERVER: {
			bFound = true;
			break;
		}
		case JBOSS_APPSERVER: {
			bFound = true;
			break;
		}
		default: {
			bFound = false;
			break;
		}
		}
		return bFound;
	}

	/**
	 * Method isIntegerValuePositve.
	 * 
	 * @param i
	 * @return boolean
	 */
	public static boolean isIntegerValueNegative(final int iValue) {
		if (iValue < 0) {
			return true;
		}
		return false;
	}

	/**
	 * Method isURLConnectionValid.
	 * 
	 * @param string
	 * @return boolean
	 */
	// public static boolean isURLConnectionValid(String sUrl)
	// {
	// if(sUrl != null)
	// {
	// try
	// {
	// if (!((sUrl.startsWith("http://") || sUrl.startsWith("https://"))))
	// //$NON-NLS-1$ //$NON-NLS-2$
	// {
	// sUrl = "http://" + sUrl; //$NON-NLS-1$
	// }
	// // Check if the URL that is entered is just the protocol and no actual
	// address.
	// int index = sUrl.indexOf("//"); //$NON-NLS-1$
	// String temp = sUrl.substring(index+2);
	// if(temp == null || temp.length() == 0 || temp.charAt(0) == '/')
	// //$NON-NLS-1$
	// {
	// return false;
	// }
	// URL url = new URL(sUrl);
	// if(sUrl.startsWith("https://")) //$NON-NLS-1$
	// {
	// return true;
	// }
	// URLConnection urlConnection = url.openConnection();
	// urlConnection.connect();
	// return true;
	// }
	// catch (Exception e)
	// {
	// return false;
	// }
	// }
	// return false;
	// }
	//
	// /**
	// * Method isURLValid.
	// * @param string
	// * @return boolean
	// */
	// public static boolean isURLValid(String sUrl)
	// {
	// if(sUrl != null)
	// {
	// try
	// {
	// sUrl = new URL(sUrl).getHost();
	// if(sUrl == null || sUrl.trim().length() == 0)
	// {
	// return false;
	// }
	// InetAddress.getByName(sUrl);
	// return true;
	// }
	// catch (UnknownHostException e)
	// {
	// return false;
	// }
	// catch (MalformedURLException e)
	// {
	// return false;
	// }
	// }
	// return false;
	// }
	/**
	 * Validate method name checked for First character - alphabet and others
	 * alpha numeric method isMethodNameValid
	 * 
	 * @param methodName
	 * @return
	 */
	public static boolean isMethodNameValid(String methodName) {
		final String validCharacters = "_"; //$NON-NLS-1$
		if (methodName != null) {
			methodName = methodName.trim();
			if (methodName.length() == 0) {
				return false;
			}
			final char[] chars = methodName.toCharArray();
			if (!Character.isLetter(chars[0])) {
				return false;
			}
			for (int i = 0; i < chars.length; i++) {
				if (!(Character.isLetterOrDigit(chars[i]) || (validCharacters.indexOf(chars[i]) != -1))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * method isImportNameValid
	 * 
	 * @param sName
	 * @return
	 */
	public static boolean isImportNameValid(final String sName) {
		// \ / : * ; ? " < > | characters not allowed
		boolean bValid = true;
		if (sName != null) {
			final int iLength = sName.length();
			for (int i = 0; bValid && (i < iLength); i++) {
				switch (sName.charAt(i)) {
				case ' ':
					bValid = false;
					break;
				case '\\':
					bValid = false;
					break;
				case '/':
					bValid = false;
					break;
				case ':':
					bValid = false;
					break;
				case ';':
					bValid = false;
					break;
				case '*':
					bValid = (i == iLength - 1) ? true : false;
					break;
				case '?':
					bValid = false;
					break;
				case '\"':
					bValid = false;
					break;
				case '<':
					bValid = false;
					break;
				case '>':
					bValid = false;
					break;
				case '|':
					bValid = false;
					break;
				}
			}
		}
		return bValid;
	}

	/**
	 * method isPackageNameValid
	 * 
	 * @param sPacakgeName
	 * @return
	 */
	public static boolean isPackageNameValid(final String sPacakgeName) {
		if ((sPacakgeName == null) || (sPacakgeName.length() == 0)) {
			// sError = RM.getString("ERR_MSG_PACKAGE_NAME"); //$NON-NLS-1$
			return false;
		}
		if ((sPacakgeName.charAt(0) == '.') || (sPacakgeName.charAt(sPacakgeName.length() - 1) == '.')) {
			// sError = RM.getString("ERR_MSG_INVALID_PACKAGE_NAME");
			// //$NON-NLS-1$
			return false;
		}
		if (!isImportNameValid(sPacakgeName)) {
			// sError = RM.getString("ERR_MSG_CHARACTERS"); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	/**
	 * method isPackageNameValid
	 * 
	 * @param sPackageName
	 * @param sStartWith
	 * @return
	 */
	public static boolean isPackageNameValid(final String sPackageName, final String[] sStartWith) {
		sError = null;
		if ((sStartWith != null) && (sStartWith.length > 0)) {
			StringBuffer sb = new StringBuffer(); // $NON-NLS-1$
			for (int i = 0; i < sStartWith.length; i++) {
				if (sPackageName.startsWith(sStartWith[i])) {
					sb = null;
					break;
				}
				sb.append(' ');
				sb.append(sStartWith[i]);
				if (i + 1 != sStartWith.length) {
					sb.append(',');
				}
			}
			if (sb != null) {
				sError = sb.toString();
			}
		}
		if ((sError != null) && (sError.length() > 0)) {
			return false;
		}
		return isPackageNameValid(sPackageName);
	}

	/**
	 * method isClassNameValid
	 * 
	 * @param sClassName
	 * @return
	 */
	public static boolean isClassNameValid(final String sClassName) {
		if ((sClassName == null) || (sClassName.length() == 0)) {
			// sError = RM.getString("ERR_MSG_ENTER_CLASS_NAME"); //$NON-NLS-1$
			return false;
		}
		if ((sClassName.charAt(0) == '.') || (sClassName.charAt(sClassName.length() - 1) == '.')
				|| (sClassName.charAt(0) == '$') || (sClassName.charAt(sClassName.length() - 1) == '$')) {
			// sError = RM.getString("ERR_MSG_INVALID_CLASS_NAME");
			// //$NON-NLS-1$
			return false;
		}
		if (!isImportNameValid(sClassName)) {
			// sError = RM.getString("ERR_MSG_CHARACTERS"); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	/**
	 * method isClassNameValid
	 * 
	 * @param sClassName
	 * @param sPackageNames
	 * @return
	 */
	public static boolean isClassNameValid(final String sClassName, final String[] sPackageNames) {
		sError = null;
		if ((sPackageNames != null) && (sPackageNames.length > 0)) {
			if (sClassName.lastIndexOf('.') >= 0) {
				StringBuffer sb = new StringBuffer(); // $NON-NLS-1$
				final String sPackage = sClassName.substring(0, sClassName.lastIndexOf('.'));
				for (int i = 0; i < sPackageNames.length; i++) {
					if (sPackage.startsWith(sPackageNames[i])) {
						sb = null;
						break;
					}
					sb.append(' ');
					sb.append(sPackageNames[i]);
					if (i + 1 != sPackageNames.length) {
						// sb.append(RM.getString("MSG_Or")); //$NON-NLS-1$
					}
				}
				if (sb != null) {
					sError = sb.toString();
				}
			}
		}
		if ((sError != null) && (sError.length() > 0)) {
			return false;
		}
		return isClassNameValid(sClassName);
	}
}