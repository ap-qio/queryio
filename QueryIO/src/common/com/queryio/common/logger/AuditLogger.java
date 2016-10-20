package com.queryio.common.logger;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.queryio.common.EnvironmentalConstants;
import com.queryio.common.ExtendedPatternLayout;


public final class AuditLogger {

	private final static String LOGFILELOCATION = EnvironmentalConstants.getAppHome() + "../../logs/user/";
	
	
	public static Logger getUserLogger(String userName) {
		final String logFileName = userName + "_User_Activity.log";
		File logFile = new File(LOGFILELOCATION + logFileName);
		if (!logFile.getParentFile().exists())
			logFile.getParentFile().mkdirs();
		return configureLogger(logFile.getAbsolutePath());
	}
	
	private static Logger configureLogger(String logFileLocation) {		
		Logger logger = Logger.getLogger(logFileLocation);
		logger.setLevel(Level.INFO);
		logger.removeAllAppenders();
		logger.addAppender(getDailyRollingFileAppender(logFileLocation));
		return logger;		
	}
	
	private static DailyRollingFileAppender getDailyRollingFileAppender(String logFilePath) {
		DailyRollingFileAppender appender = null;
		PatternLayout layout = new ExtendedPatternLayout("%d{dd MMM,HH:mm:ss:SSS} - %m%n");				
		try {
			appender = new DailyRollingFileAppender(layout, logFilePath, ".yyyy-MM-dd");
			appender.setAppend(true);
			appender.activateOptions();
		} catch (IOException e) { }
		return appender;
	}
}
