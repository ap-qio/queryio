package com.os3.server.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

@SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal")
public class Settings {

	private static final Logger LOGGER = Logger.getLogger(Settings.class);

	private final static Properties PROPERTIES = new Properties();

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
				LOGGER.error("Error loading properties" + ioe.getMessage(), ioe);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						// ignore this silently
						LOGGER.error("Ignore - Error in loading properties file.");
					}
				}
			}
		}
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
				LOGGER.error("Error loading properties" + ioe.getMessage(), ioe);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						// ignore this silently
						LOGGER.error("Ignore - Error in loading properties file.");
					}
				}
			}
		}

		return props;
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
			if (LOGGER.isInfoEnabled())
				LOGGER.info("Ignore - Error while getting IntProperty " + name + " , will return default value "
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
}
