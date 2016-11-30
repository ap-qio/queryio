package com.queryio.sysmoncommon.sysmon;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import com.queryio.common.IOSProtocolConstants;

public class DataParserFactory {
	private static Properties properties;
	public static final transient String SYSMON_PROPERTIES_FILE = "sysmon.properties"; //$NON-NLS-1$

	private DataParserFactory() {
		// do nothing
	}

	public static void initialize(final String fileLocation) {
		properties = new Properties();
		BufferedInputStream bis = null;

		try {
			bis = new BufferedInputStream(new FileInputStream(new File(fileLocation, SYSMON_PROPERTIES_FILE)));
			properties.load(bis);
		} catch (final IOException e) {
			// log this
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (final IOException e) {
					// ignore this
				}
			}
		}
	}

	public static AbstractDataParser getDataParser(final int osType, final String osVersion) {
		return getDataParser(osType, osVersion, null);
	}

	// osType = IOSProtocolConstants.LINUX, .SOLARIS, .MACOSX, .AIX
	// osVersion = their versions
	public static AbstractDataParser getDataParser(final int osType, final String osVersion, String sTopVersion) {
		final StringBuffer propertyName = new StringBuffer("");
		switch (osType) {
		case IOSProtocolConstants.LINUX: {
			propertyName.append("linux");
			break;
		}
		case IOSProtocolConstants.SOLARIS_OLD: {
			propertyName.append("solaris");
			break;
		}
		case IOSProtocolConstants.SOLARIS: {
			propertyName.append("solaris.zfs");
			break;
		}
		case IOSProtocolConstants.MACOSX: {
			propertyName.append("macosx");
			break;
		}
		case IOSProtocolConstants.AIX: {
			propertyName.append("aix");
			break;
		}
		}
		if (osVersion != null && osVersion.trim().length() > 0) {
			if (osType == IOSProtocolConstants.MACOSX && (osVersion.indexOf("10.6") != -1
					|| osVersion.indexOf("10.7") != -1 || osVersion.indexOf("10.8") != -1)) {
				propertyName.append(".10.6");
			} else if (osType == IOSProtocolConstants.MACOSX && osVersion.indexOf("10.9") != -1) {
				propertyName.append(".10.9");
			} else if (osType == IOSProtocolConstants.MACOSX && osVersion.indexOf("10.10") != -1) {
				propertyName.append(".10.10");
			} else if (osType == IOSProtocolConstants.MACOSX && osVersion.indexOf("10.11") != -1) {
				propertyName.append(".10.11");
			} else {
				final String sVersionInfo = getVersionInfo(osVersion);
				if (sVersionInfo.trim().length() > 0) {
					propertyName.append('.');
					propertyName.append(sVersionInfo);
				}
			}
		}
		if (sTopVersion != null && sTopVersion.trim().length() > 0) {
			sTopVersion = getTopVersion(sTopVersion);
			if (sTopVersion.length() != 0) {
				propertyName.append(".top_");
				propertyName.append(sTopVersion);
			}
		}
		AbstractDataParser parser = null;

		try {
			// e.g.
			// linux.rhe_3=com.queryio.sysmoncommon.sysmon.parser.LinuxRHEParser
			// e.g.
			// linux.rhe_4=com.queryio.sysmoncommon.sysmon.parser.LinuxRHEParser

			String className = properties.getProperty(propertyName.toString());
			String sProperty = propertyName.toString();
			while (className == null) {
				sProperty = refineProperty(sProperty);
				if (sProperty == null) {
					break;
				}
				className = properties.getProperty(sProperty);
			}

			// properties.list(System.out);

			// System.out.println("Parser Class: " + className);

			final Class parserClass = Class.forName(className);

			if (parserClass != null) {
				parser = (AbstractDataParser) parserClass.newInstance();
			}
		} catch (final ClassNotFoundException e) {
			// log this
		} catch (final InstantiationException e) {
			// log this
		} catch (final IllegalAccessException e) {
			// log this
		} catch (final Throwable th) {
			th.printStackTrace(System.err);
		}
		return parser;
	}

	private static final String[] LINUX_TYPES = new String[] { "Red Hat Linux", "Red Hat Linux Enterprise",
			"SuSE Linux", "SuSE Linux Enterprise", "Debian" };
	private static final String[] LINUX_TYPES_ABBR = new String[] { "rhd", "rhe", "suse", "suse_en", "debian" };

	private static String getVersionInfo(String sVersionDetails) {
		final StringBuffer version_name = new StringBuffer();
		if ((sVersionDetails != null) && (sVersionDetails.trim().length() > 0)) {
			int index = -1;
			for (int i = 0; i < LINUX_TYPES.length; i++) {
				index = sVersionDetails.indexOf(LINUX_TYPES[i]);
				if (index != -1) {
					version_name.append(LINUX_TYPES_ABBR[i]);
					final int gccIndex = sVersionDetails.indexOf("gcc");
					if (gccIndex != -1) {
						index = sVersionDetails.indexOf(LINUX_TYPES[i], gccIndex);
					}
					sVersionDetails = sVersionDetails.substring(index + LINUX_TYPES[i].length()).trim();
					index = sVersionDetails.indexOf(')');
					if (index != -1) {
						sVersionDetails = sVersionDetails.substring(0, index).trim();
						index = sVersionDetails.indexOf('-');
						if (index != -1) {
							sVersionDetails = sVersionDetails.substring(0, index).trim();
						}
						version_name.append('_');
						version_name.append(sVersionDetails);
					}
				}
			}
		}
		return version_name.toString();
	}

	private static String getTopVersion(final String sTopVersionInfo) {
		final StringTokenizer st = new StringTokenizer(sTopVersionInfo);
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (s.indexOf("version") != -1) {
				s = st.nextToken();
				return s;
			}
		}
		return "";
	}

	private static String refineProperty(String sProperty) {
		if (sProperty.equalsIgnoreCase("linux") || sProperty.equalsIgnoreCase("solaris")
				|| sProperty.equalsIgnoreCase("macosx") || sProperty.equalsIgnoreCase("aix")) {
			return null;
		}
		if (sProperty.indexOf(".top_") != -1) {
			sProperty = sProperty.substring(0, sProperty.indexOf(".top_"));
		} else if (sProperty.indexOf('_') > sProperty.lastIndexOf('.')) {
			sProperty = sProperty.substring(0, sProperty.lastIndexOf('_'));
		} else if (sProperty.indexOf('.') != -1) {
			sProperty = sProperty.substring(0, sProperty.lastIndexOf('.'));
		}
		return sProperty;
	}

	/*
	 * public static void main(String[] args) { AbstractDataParser parser;
	 * DataParserFactory.initialize("E:\\v10.0.0\\com.queryio.sysmoncommon");
	 * 
	 * parser = DataParserFactory.getDataParser(IOSProtocolConstants.AIX, null,
	 * null); System.out.println("Parser (AIX): " +
	 * parser.getClass().getName()); parser =
	 * DataParserFactory.getDataParser(IOSProtocolConstants.LINUX,
	 * "(Red Hat Linux 3.2.3)", "Top version 2.0.13");
	 * System.out.println("Parser (Linux): " + parser.getClass().getName()); }
	 */
}
