package com.queryio.common;

import java.io.File;
import java.net.URL;

import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StartupParameters;

public class ClassPathUtility {

	public static CustomClassLoder customClassLoder = null;

	public static synchronized void recycleClassLoder() {
		try {
			closeClassLoder();
			String tagParserJarDir = EnvironmentalConstants.getAppHome() + File.separator + ".." + File.separator + ".."
					+ File.separator + ".." + File.separator + QueryIOConstants.MAPREDRESOURCE + File.separator
					+ QueryIOConstants.TAGPARSER_JAR_DIR + File.separator;

			String defaultJarFile = EnvironmentalConstants.getAppHome() + File.separator + ".." + File.separator + ".."
					+ File.separator + ".." + File.separator + QueryIOConstants.MAPREDRESOURCE + File.separator
					+ QueryIOConstants.DEFAULT_USERDEFINEDTAGSJAR + File.separator
					+ QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR;

			File f = new File(defaultJarFile);

			URL url = f.toURI().toURL();
			AppLogger.getLogger().info("Loading jars from " + url.toString());
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader == null) {
				classLoader = ClassPathUtility.class.getClassLoader();
			}
			customClassLoder = new CustomClassLoder(new URL[] { url }, classLoader);

			addFile(defaultJarFile);

			f = new File(tagParserJarDir);
			if (f.exists()) {
				addFolder(tagParserJarDir);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized void recycleClassLoderForUIServer() {
		try {
			closeClassLoder();
			String tagParserJarDir = EnvironmentalConstants.getAppHome() + File.separator
					+ QueryIOConstants.MAPREDRESOURCE + File.separator + QueryIOConstants.TAGPARSER_JAR_DIR
					+ File.separator;

			String defaultJarFile = StartupParameters.getHadoopDirLocation() + File.separator
					+ QueryIOConstants.MAPREDRESOURCE + File.separator + QueryIOConstants.DEFAULT_USERDEFINEDTAGSJAR
					+ File.separator + QueryIOConstants.DEFAULT_ONINGEST_PARSER_LIBJAR;

			File f = new File(defaultJarFile);

			URL url = f.toURI().toURL();
			AppLogger.getLogger().info("Loading jars from " + url.toString());
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader == null) {
				classLoader = ClassPathUtility.class.getClassLoader();
			}
			customClassLoder = new CustomClassLoder(new URL[] { url }, classLoader);

			addFile(defaultJarFile);

			f = new File(tagParserJarDir);
			if (f.exists()) {
				addFolder(tagParserJarDir);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void closeClassLoder() {
		if (customClassLoder != null) {
			customClassLoder.close();
		}
		customClassLoder = null;
	}

	public static Class<?> getClass(String className)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class<?> parserClass = null;
		try {
			parserClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			parserClass = Class.forName(className, true, customClassLoder);
		}
		return parserClass;
	}

	public static void addFolder(String s) throws Exception {
		File f = new File(s);
		addFolder(f);
	}

	public static void addFolder(File f) throws Exception {
		if (f.exists()) {
			File[] files = f.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					addFolder(file);
				} else {
					if (file.getAbsolutePath().endsWith(".jar")) {
						AppLogger.getLogger().info("File: " + file.getAbsolutePath() + " Name: " + file.getName());
						addFile(file);
					}
				}
			}
		}
	}

	public static void addFile(String s) throws Exception {
		File f = new File(s);
		addFile(f);
	}

	public static void addFile(File f) throws Exception {
		customClassLoder.addToPath(f.toURI().toURL());
	}

	//
	// public static void loadUsingCustomClassLoder(File f, String parserClass)
	// {
	// try {
	// URL jarUrl = f.toURI().toURL();
	// customClassLoder.addToPath(jarUrl);
	// System.out.println("Using Custom Class Loder!!!" + jarUrl.getPath());
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if (customClassLoder != null)
	// customClassLoder.close();
	// }
	// }
	//
	// public static void addURL(URL u) throws Exception {
	// System.out.println("ADDING " + u.getPath());
	// Class<?>[] parameters = new Class[]{URL.class};
	// ClassLoader contextClassLoader = ClassPathUtility.class.getClassLoader();
	// URLClassLoader webappClassLoader = (URLClassLoader)contextClassLoader;
	//// URLClassLoader webappClassLoader = (URLClassLoader)
	// ClassLoader.getSystemClassLoader();
	// Class<?> sysclass = URLClassLoader.class;
	// try
	// {
	// Method method = sysclass.getDeclaredMethod("addURL", parameters);
	// method.setAccessible(true);
	// method.invoke(webappClassLoader, new Object[]{ u });
	// }
	// catch (Throwable t)
	// {
	// throw new IOException("Error, could not add URL to system classloader");
	// }
	// }
}