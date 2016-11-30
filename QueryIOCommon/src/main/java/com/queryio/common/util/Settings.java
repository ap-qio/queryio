package com.queryio.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.queryio.common.EnvironmentalConstants;

@SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal")
public class Settings {

	private static Logger logger = Logger.getLogger(Settings.class);

	private final static Properties PROPERTIES = new Properties();

	private static ThreadPoolExecutor threadPoolExecutor = null;

	private Settings() {
		// This class has only static methods
	}

	public static void loadProperties(String fileName) {
		File file = new File(fileName);
		loadProperties(file);
	}

	private static void loadProperties(File file) {
		if (file.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				PROPERTIES.load(fis); // No need for BufferInputStream as
										// Properties internally creates a
										// Buffered stream
			} catch (IOException ioe) {
				logger.error("Error loading properties" + ioe.getMessage(), ioe);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						// ignore this silently
						logger.error("Ignore - Error in loading properties file.");
					}
				}
			}
		}
	}

	public static Properties getProperties() {
		return PROPERTIES;
	}

	public static int getIntProperty(String name, int defaultValue) {
		try {
			if (name != null && PROPERTIES.getProperty(name) != null) {
				return Integer.parseInt(PROPERTIES.getProperty(name));
			}
		} catch (Exception ex) {
			// ignore the exception
			if (logger.isInfoEnabled())
				logger.info("Ignore - Error while getting IntProperty " + name + " , will return default value "
						+ defaultValue, ex);
		}
		return defaultValue;
	}

	public static boolean getBoolean(String name) {
		return Boolean.parseBoolean(PROPERTIES.getProperty(name, "false"));
	}

	public static String get(String name) {
		return PROPERTIES.getProperty(name);
	}

	public static int getInt(String name) {
		return Integer.parseInt(PROPERTIES.getProperty(name, "10240"));
	}

	public static Properties getPropertiesObject(File file) {
		Properties props = new Properties();

		if (file.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				props.load(fis); // No need for BufferInputStream as Properties
									// internally creates a Buffered stream
			} catch (IOException ioe) {
				logger.error("Error loading properties" + ioe.getMessage(), ioe);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						// ignore this silently
						logger.error("Ignore - Error in loading properties file.");
					}
				}
			}
		}

		return props;
	}

	public static ThreadPoolExecutor getThreadPoolExecutor() {
		if (threadPoolExecutor == null) {
			int minThreads = EnvironmentalConstants.getMinThreadCount();
			int maxThreads = EnvironmentalConstants.getMaxThreadCount();
			threadPoolExecutor = new ThreadPoolExecutor(minThreads, // core
																	// thread
																	// pool size
					maxThreads, // maximum thread pool size
					1, // time to wait before resizing pool
					TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(maxThreads, true),
					new ThreadPoolExecutor.CallerRunsPolicy());
		}
		return threadPoolExecutor;
	}
}
