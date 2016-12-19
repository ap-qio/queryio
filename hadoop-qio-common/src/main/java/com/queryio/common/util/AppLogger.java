/*
 * @(#) AppLogger.java    Created on Jan 30, 2004
 *
 * Copyright (C) 2002 - 2004 Exceed Consultancy Services. All rights reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT 
 * THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;

/**
 * This class is used to log messages and exceptions in the log file. This class
 * reads logger properties file and configures logger object accordingly.
 * 
 * @author Exceed Consultancy Services
 * @version 1.0
 */
public final class AppLogger {
	public static final int FATAL = 0;
	public static final int ERROR = 1;
	public static final int WARN = 2;
	public static final int INFO = 3;
	public static final int DEBUG = 4;
	private static final String LOGGER_FILE = "logger.properties"; //$NON-NLS-1$
	private static final String LOG_FILE_PROPERTY = "log4j.appender.FileApr.File"; //$NON-NLS-1$

	private static Logger _apConsoleLogger; // $NON-NLS-1$
	private static Logger _apQueryIOOS3Logger;

	public static final void initLoggerProperties(final String loggerPropertiesLocation, final String logFileLocation) {
		initLoggerProperties(loggerPropertiesLocation, logFileLocation, LOGGER_FILE, LOG_FILE_PROPERTY);
	}

	public static final void initLoggerProperties(final String loggerPropertiesLocation, final String logFileLocation,
			final String logFileName) {
		initLoggerProperties(loggerPropertiesLocation, logFileLocation, LOGGER_FILE, LOG_FILE_PROPERTY, logFileName);
	}

	public static final void initLoggerProperties(final String loggerPropertiesLocation, final String logFileLocation,
			final String loggerFileName, final String logFileProperty) {
		initLoggerProperties(loggerPropertiesLocation, logFileLocation, loggerFileName, logFileProperty, null);
	}

	public static final void initQIOLogger(){
		_apQueryIOOS3Logger = Logger.getLogger("QueryIO");

		if (_apConsoleLogger == null) {
			_apConsoleLogger = Logger.getLogger("QueryIO_CONSOLE"); //$NON-NLS-1$
		}
	}
	
	public static final void initLoggerProperties(final String loggerPropertiesLocation, final String logFileLocation,
			final String loggerFileName, final String logFileProperty, final String logFileName) {
		BufferedInputStream bis = null;
		try {
			// disable logging by parent handlers
			Properties prop = new Properties();
			final File file = new File(loggerPropertiesLocation, loggerFileName);
			if (file.exists()) {
				// create the file handler and set the logging level to ALL
				bis = new BufferedInputStream(new FileInputStream(file));
				prop.load(bis);
				final File logFile = new File(logFileLocation,
						logFileName != null ? logFileName : prop.getProperty(logFileProperty));
				if (!logFile.getParentFile().exists()) {
					logFile.getParentFile().mkdirs();
				}
				prop.setProperty(logFileProperty, logFile.getAbsolutePath());
				PropertyConfigurator.configure(prop);
			}
			Logger.getRootLogger().setLevel(Level.FATAL);
			_apQueryIOOS3Logger = Logger.getLogger("QueryIO");

			if (_apConsoleLogger == null) {
				_apConsoleLogger = Logger.getLogger("QueryIO_CONSOLE"); //$NON-NLS-1$
			}
		} catch (final Exception e) {
			BasicConfigurator.configure();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
	}

	/**
	 * @param priority
	 * @return
	 */
	public static final Priority getPriority(final int priority) {
		switch (priority) {
		case FATAL:
			return Level.FATAL;
		case ERROR:
			return Level.ERROR;
		case WARN:
			return Level.WARN;
		case INFO:
			return Level.INFO;
		case DEBUG:
			return Level.DEBUG;
		default:
			return Level.FATAL;
		}
	}

	/**
	 * @return
	 */
	public static final Logger getConsoleLogger() {
		return _apConsoleLogger;
	}

	/**
	 * @param productType
	 * @return
	 */
	public static final Logger getLogger() {
		return _apQueryIOOS3Logger;
	}

	/**
	 * @param productId
	 * @param level
	 */
	public static final void setLoggerLevel(final int productId, final int level) {
		final Logger logger = getLogger();
		if (logger != null) {
			int currentLevel = FATAL;
			switch (logger.getLevel().toInt()) {
			case Priority.FATAL_INT:
				currentLevel = FATAL;
				break;
			case Priority.ERROR_INT:
				currentLevel = ERROR;
				break;
			case Priority.WARN_INT:
				currentLevel = WARN;
				break;
			case Priority.INFO_INT:
				currentLevel = INFO;
				break;
			case Priority.DEBUG_INT:
				currentLevel = DEBUG;
				break;
			default:
				currentLevel = FATAL;
				break;
			}
			if (currentLevel != level) {
				switch (level) {
				case FATAL:
					logger.setLevel(Level.FATAL);
					break;
				case ERROR:
					logger.setLevel(Level.ERROR);
					break;
				case WARN:
					logger.setLevel(Level.WARN);
					break;
				case INFO:
					logger.setLevel(Level.INFO);
					break;
				case DEBUG:
					logger.setLevel(Level.DEBUG);
					break;
				default: // do nothing
					break;
				}
			}
		}
	}

	public static void setLogger(Logger logger) {
		_apQueryIOOS3Logger = logger;
	}
}