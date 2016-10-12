package com.queryio.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.queryio.common.util.PlatformHandler;
import com.queryio.common.util.StreamPumper;


public class ServiceUtilities
{
	public transient static final String DIR_DEVTOOLS = "apdevtools"; //$NON-NLS-1$
	public transient static final String DIR_WEBTOOLS = "apwebtools"; //$NON-NLS-1$
	public transient static final String DIR_DEVTEST = "com.appperfect.devtest"; //$NON-NLS-1$
	public transient static final String DIR_WEBTEST = "com.appperfect.webtest"; //$NON-NLS-1$
	public transient static final String DIR_PLUGINS = "plugins"; //$NON-NLS-1$
	public transient static final String DIR_ECLIPSE = "eclipse"; //$NON-NLS-1$
	public transient static final String DIR_TEST_STUDIO = "com.appperfect.teststudio"; //$NON-NLS-1$

	public transient static final String DIR_CODETOOLS = "apcodetools"; //$NON-NLS-1$
	public transient static final String DIR_UNITTOOLS = "apunittools"; //$NON-NLS-1$
	public transient static final String DIR_PROFILERTOOLS = "approfilertools"; //$NON-NLS-1$
	public transient static final String DIR_WEBFTTOOLS = "apwebtools"; //$NON-NLS-1$	
	public transient static final String DIR_LOADTOOLS = "aploadtools"; //$NON-NLS-1$
	public transient static final String DIR_APPTOOLS = "apapptools"; //$NON-NLS-1$
	
	public transient static final String DIR_CODETEST = "com.appperfect.codeanalyzer"; //$NON-NLS-1$
	public transient static final String DIR_PROFILER = "com.appperfect.javaprofiler"; //$NON-NLS-1$
	public transient static final String DIR_UNITTEST = "com.appperfect.unittester"; //$NON-NLS-1$
	public transient static final String DIR_WEBFTTEST = "com.appperfect.functionaltester"; //$NON-NLS-1$
	public transient static final String DIR_LOADTEST = "com.appperfect.loadtester"; //$NON-NLS-1$
	public transient static final String DIR_APPTEST = "com.appperfect.apptest"; //$NON-NLS-1$
	
	public transient static final String DIR_LOGS = "logs"; //$NON-NLS-1$
	public transient static final String DIR_LIB = "lib"; //$NON-NLS-1$
	public transient static final String DIR_BIN = "bin"; //$NON-NLS-1$

	static boolean development = false;
//	private static String devTSHome = null;

	private static FileInputStream finLock;

	private static final String FILE_SEPARATOR = File.separator;
//	private static final String WRAPPER_DIR = "wrapper";
	private static String USER_JDK_HOME = null;
	private static final String TRUNCATED = "..."; //$NON-NLS-1$

	// used for development system
	public static void setDevelopmentSystem(final boolean development)
	{
		ServiceUtilities.development = development;
//		devTSHome = System.getProperty("devsuite.home"); //$NON-NLS-1$
	}

	private ServiceUtilities()
	{
		// do nothing
	}

	public static String getProductExecutable(final String productHome)
	{
		final File eclipseDir = new File(productHome, ServiceUtilities.DIR_ECLIPSE);
		final StringBuffer bufCommand = new StringBuffer(eclipseDir.getAbsolutePath());
		bufCommand.append(File.separatorChar);

		String exeName = PlatformHandler.isMacOS()? "Eclipse":"eclipse";
		
		final File[] list = eclipseDir.listFiles();
		if ((list != null) && (list.length > 1))
		{
			boolean checkFolder = PlatformHandler.isMacOS();
			for (int index = 0; index < list.length; index++)
			{
				if ((checkFolder && list[index].isFile()) || (!checkFolder && list[index].isDirectory()))
				{
					continue;
				}
				
				if (list[index].getName().indexOf("Profiler") != -1)
				{
					exeName = "Profiler";
					break;
				}
				else if (list[index].getName().indexOf("CodeTest") != -1)
				{
					exeName = "CodeTest";
					break;
				}
				else if (list[index].getName().indexOf("UnitTest") != -1)
				{
					exeName = "UnitTest";
					break;
				}
				else if (list[index].getName().indexOf("WebTest") != -1)
				{
					exeName = "WebTest";
					break;
				}
				else if (list[index].getName().indexOf("LoadTest") != -1)
				{
					exeName = "LoadTest";
					break;
				}
				else if (list[index].getName().indexOf("AppTest") != -1)
				{
					exeName = "AppTest";
					break;
				}
				else if (list[index].getName().indexOf("TestStudio") != -1)
				{
					exeName = "TestStudio";
					break;
				}
				else if (list[index].getName().indexOf("DevTest") != -1)
				{
					exeName = "DevTest";
					break;
				}
			}
		}
		
		bufCommand.append(exeName); //$NON-NLS-1$
		if (PlatformHandler.isWindows())
		{
			bufCommand.append(".exe");
		}
		else if (PlatformHandler.isMacOS())
		{
			// Changes done to run scheduler on MacOSX
			bufCommand.append(".app/Contents/MacOS/eclipse"); //$NON-NLS-1$
		}
		return bufCommand.toString();
	}
	

	/**
	 * @param productHome
	 * @param applicationId
	 * @return
	 * @throws IOException
	 */

	public static byte[] getBytesOfFile(final File file) throws IOException
	{
		BufferedInputStream bis = null;
		try
		{
			if (file.exists())
			{
				bis = new BufferedInputStream(new FileInputStream(file));
				return getBytesOfFile(bis);
			}
			return null;
		}
		finally
		{
			if (bis != null)
			{
				bis.close();
			}
		}
	}

	public static byte[] getBytesOfFile(final InputStream stream) throws IOException
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		final byte[] readBuffer = new byte[8192];
		int bytesIn = 0;
		while ((bytesIn = stream.read(readBuffer)) != -1)
		{
			baos.write(readBuffer, 0, bytesIn);
		}
		return baos.toByteArray();
	}

	/**
	 * Returns JRE path This method assumes that jdkPath parameter is a valid
	 * JRE folder or a parent folder
	 * 
	 * @param jdkPath
	 * @return
	 */
	public static String getJrePath(final String jdkPath)
	{
		String jrePath = jdkPath;
		if (PlatformHandler.isMacOS())
		{
			if (jrePath.indexOf("Home") != -1) //$NON-NLS-1$
			{
				return jrePath;
			}
			final File rtJarFile = new File(jrePath, "Classes" + File.separatorChar + "classes.jar"); //$NON-NLS-1$ //$NON-NLS-2$
			return rtJarFile.exists() ? jrePath + File.separatorChar + "Home" : jrePath; //$NON-NLS-1$
		}
		File rtJarFile = new File(jrePath, DIR_LIB + File.separatorChar + "rt.jar"); //$NON-NLS-1$

		if (!rtJarFile.exists())
		{
			jrePath = jrePath + File.separatorChar + "jre";
			rtJarFile = new File(jrePath, DIR_LIB + File.separatorChar + "rt.jar"); //$NON-NLS-1$
		}
		if (rtJarFile.exists())
		{
			return jrePath;
		}
		// for IBM look for jdkpath+ jre\lib\core.jar
		jrePath = jdkPath;
		File coreJarFile = new File(jrePath, DIR_LIB + File.separatorChar + "core.jar"); //$NON-NLS-1$
		if (coreJarFile.exists())
		{
			return jrePath;
		}

		jrePath = jrePath + File.separatorChar + "jre";
		coreJarFile = new File(jrePath, DIR_LIB + File.separatorChar + "core.jar"); //$NON-NLS-1$

		if (coreJarFile.exists())
		{
			return jrePath;
		}
		return null;
	}

	public static String getJavaRuntimeJarPath(String jdkPath)
	{
		File rtJarFile;
		if (PlatformHandler.isMacOS())
		{
			final File jdkFile = new File(jdkPath);
			final File jdkHome = jdkFile.getName().equals("Home") ? //$NON-NLS-1$
			jdkFile.getParentFile()
					: jdkFile;
			rtJarFile = new File(jdkHome, "Classes" + File.separatorChar + "classes.jar"); //$NON-NLS-1$
		}
		else
		{
			rtJarFile = new File(jdkPath, DIR_LIB + File.separatorChar + "rt.jar"); //$NON-NLS-1$
			if (!rtJarFile.exists())
			{
				jdkPath = jdkPath + File.separatorChar + "jre";
				rtJarFile = new File(jdkPath, DIR_LIB + File.separatorChar + "rt.jar"); //$NON-NLS-1$
			}
		}
		return rtJarFile.getAbsolutePath();
	}

	/**
	 * replaces all the " " with "\ " if the platform is other than windows. if
	 * the platform is windows then returns "\"" + origPath + "\""
	 * 
	 * @param origPath
	 * @return
	 */
	public static String getSystemSpecificEscapedPath(final String origPath)
	{
		return getSystemSpecificEscapedPath(origPath, PlatformHandler.isWindows());
	}

	/**
	 * 
	 * method getSystemSpecificEscapedPath
	 * 
	 * @param origPath
	 * @param bForWindows
	 * @return
	 */
	public static String getSystemSpecificEscapedPath(String origPath, final boolean bForWindows)
	{
		String path = null;
		if (bForWindows)
		{
			if (origPath.endsWith("\\")) //$NON-NLS-1$
			{
				origPath += "\\"; //$NON-NLS-1$
			}
			path = "\"" + origPath + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			final StringBuffer buffer = new StringBuffer();
			final char[] chars = origPath.toCharArray();
			for (int i = 0; i < chars.length; i++)
			{
				if (chars[i] == ' ')
				{
					buffer.append('\\');
				}
				buffer.append(chars[i]);
			}
			path = buffer.toString(); // StaticUtilities.replaceAll(origPath,
			// " ", "\\ ");
		}
		return path;
	}

	public static String getJavaExecutable(String productHome)
	{
		if (!productHome.endsWith(FILE_SEPARATOR))
		{
			productHome += FILE_SEPARATOR;
		}

		final String userJdkHome = getUserJdkHome(productHome);
		String javaHome = userJdkHome;
		if (javaHome == null)
		{
			javaHome = productHome + "jre" + FILE_SEPARATOR; //$NON-NLS-1$
		}

		String sCommand = "";
		if (PlatformHandler.isMacOS())
		{
			if (javaHome.indexOf("Home") == -1) //$NON-NLS-1$
			{
				sCommand = javaHome + "Home" + FILE_SEPARATOR + DIR_BIN + FILE_SEPARATOR + getJavaExecutable(); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				sCommand = javaHome + DIR_BIN + FILE_SEPARATOR + getJavaExecutable(); //$NON-NLS-1$
			}
		}
		else
		{
			sCommand = javaHome + DIR_BIN + FILE_SEPARATOR + getJavaExecutable(); //$NON-NLS-1$
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
	 * Use this method to get user jdk home i.e. either bundled jre or jre
	 * provided by user at installation time.
	 * 
	 * @param productHome
	 * @return
	 */
	public static String getUserJdkHome(final String productHome)
	{
		if (USER_JDK_HOME == null)
		{
			File file = new File(productHome, ".install4j" + File.separatorChar + "variables.properties");
			if (!file.exists())
			{
				file = new File(productHome, "variables.properties");
			}
			if (file.exists())
			{
				try
				{
					final Properties props = new Properties();
					FileInputStream fis;
					fis = new FileInputStream(file);
					props.load(fis);
					fis.close();
					USER_JDK_HOME = props.getProperty("USER_JDK_HOME");
				}
				catch (final Exception e)
				{
					// ingore
				}
			}
		}
		return USER_JDK_HOME;
	}

	/**
	 * Method getJavaExecutable.
	 * 
	 * @param bSwtApplication
	 * @return String
	 */
	private static String getJavaExecutable()
	{
		if (PlatformHandler.isWindows())
		{
			return "javaw.exe"; //$NON-NLS-1$
		}
		return "java"; //$NON-NLS-1$
	}

	/**
	 * Checks for an instance already running. Uses 'instance.lck' file present
	 * in the product home. Creates new and grabs lock on this file if able to
	 * delete it.
	 * 
	 * @param path
	 * @param lockFile
	 * @return
	 */
	public static boolean setLock(final String path, final String lockFile)
	{
		if ((path == null) || (lockFile == null))
		{
			return false;
		}
		final File fileProductHome = new File(path);
		if (!fileProductHome.exists())
		{
			return false;
		}
		final File fileLock = new File(fileProductHome, lockFile);
		try
		{
			if (fileLock.exists() && !fileLock.delete())
			{
				return false;
			}
			if (!fileLock.exists())
			{
				fileLock.createNewFile();
			}
			finLock = new FileInputStream(fileLock);
		}
		catch (final Exception e)
		{
			return false;
		}
		return true;
	}

	/**
	 * 
	 * method releaseLock
	 * 
	 * @return
	 */
	public static boolean releaseLock()
	{
		if (finLock != null)
		{
			try
			{
				finLock.close();
			}
			catch (final Exception e)
			{
				return false;
			}
			finLock = null;
		}
		return true;
	}

	public static URLClassLoader createClassLoader(final String home, final List excludedResources, List extraResources)
	{
		final String[] jarNames = JarUtils.getAllJars(home, excludedResources);
		if (extraResources == null)
		{
			extraResources = Collections.EMPTY_LIST;
		}
		final int totalLength = jarNames.length + extraResources.size();
		final int excludedLength = jarNames.length;
		final URL[] urls = new URL[totalLength];
		for (int i = 0; i < totalLength; i++)
		{
			try
			{
				if (i < excludedLength)
				{
					urls[i] = new File(jarNames[i]).toURI().toURL();
				}
				else
				{
					urls[i] = new File((String) extraResources.get(i - excludedLength)).toURI().toURL();
				}
			}
			catch (final MalformedURLException e)
			{
				// do nothing
			}

		}
		return new URLClassLoader(urls);
	}

	public static void copyNonUTFFile(final String sourceFileName, final String destFolder) throws Exception
	{
		final File sourceFile = new File(sourceFileName);
		// copyFile(sourceFile, new File(destFolder));
		final BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceFile)); // $IGN_Close_streams$
		final File destinationFile = new File(destFolder, sourceFile.getName());
		final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destinationFile)); // $IGN_Close_streams$
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
		out.flush();
		out.close();
	}

	private static final int[] getPluginDetails(final String pluginName)
	{
		final int[] array = new int[4];

		final int indexOf_ = pluginName.lastIndexOf('_');
		if (indexOf_ != -1)
		{
			final String text = pluginName.substring(indexOf_ + 1);
			final StringTokenizer stk = new StringTokenizer(text, ".-");
			int tokenCtr = 0;
			while (stk.hasMoreTokens())
			{
				try
				{
					array[tokenCtr] = Integer.parseInt(stk.nextToken());
				}
				catch (final NumberFormatException nfe)
				{

				}
				tokenCtr++;
			}
		}
		return array;
	}

	/**
	 * This method will return plugin folder name which is
	 * 
	 * @param parentDir
	 * @param pluginName
	 * @return
	 */
	public static String getLatestPluginDirName(final String parentDir, final String pluginName)
	{
		final File[] list = new File(parentDir).listFiles(new PluginDirNameFilter(pluginName));
		if (list.length > 1)
		{
			final Comparator comparator = new Comparator()
			{
				public int compare(Object o1, Object o2)
				{
					String file1 = ((File) o1).getName();
					String file2 = ((File) o2).getName();

					if (file1.equals(file2))
					{
						return 0;
					}
					int[] array1 = getPluginDetails(file1);
					int[] array2 = getPluginDetails(file2);

					boolean firstBigger = false;
					for (int i = 0; i < array1.length; i++)
					{
						if (array1[i] > array2[i])
						{
							firstBigger = true;
						}
						if (firstBigger)
						{
							break;
						}
					}
					return firstBigger ? -1 : 1;
				}
			};
			Arrays.sort(list, comparator);
		}
		return ((list.length > 0) ? list[0].getName() : null);
	}

	private static class PluginDirNameFilter implements FilenameFilter
	{
		private final String pattern;

		PluginDirNameFilter(final String pattern)
		{
			this.pattern = pattern;
		}

		public boolean accept(final File dir, final String name)
		{
			// if file does not belong to valid directory then return false
			if (!new File(dir, name).isDirectory())
			{
				return false;
			}
			String nameWithoutExtension = name;
			final int index = name.lastIndexOf('_'); //$NON-NLS-1$
			if (index != -1)
			{
				nameWithoutExtension = name.substring(0, index);
			}
			return nameWithoutExtension.equals(this.pattern);
		}

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
		final ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputFileName)));
		try
		{
			zipFileOrDirectory(dir2zip, dir2zip, zos);
		}
		finally
		{
			zos.flush();
			zos.close();
		}
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
		final byte[] readBuffer = new byte[2156];
		int bytesIn = 0;
		final File zipEntryFile = new File(file2Zip);
		if (!zipEntryFile.exists())
		{
			return;
		}
		else if (zipEntryFile.isDirectory())
		{
			final String[] dirList = zipEntryFile.list();
			for (int i = 0; i < dirList.length; i++)
			{
				final File f = new File(zipEntryFile, dirList[i]);
				zipFileOrDirectory(orgDir, f.getPath(), zos);
			}
		}
		else
		{
			BufferedInputStream fis = null;
			try
			{
				fis = new BufferedInputStream(new FileInputStream(zipEntryFile));
				String path = zipEntryFile.getPath();
				final int index = path.indexOf(orgDir);
				if (index != -1)
				{
					path = path.substring(index + orgDir.length());
				}
				final ZipEntry anEntry = new ZipEntry(path);
				anEntry.setTime(zipEntryFile.lastModified());
				zos.putNextEntry(anEntry);
				while ((bytesIn = fis.read(readBuffer)) != -1)
				{
					zos.write(readBuffer, 0, bytesIn);
				}
			}
			finally
			{
				if (fis != null)
				{
					fis.close();
				}
			}
		}
	}

	public static boolean extractZip(final String zipFileName, final String destinationDir) throws IOException
	{
		return extractZip(new FileInputStream(zipFileName), destinationDir);
	}

	public static boolean extractZip(final InputStream zipis, final String destinationDir) throws IOException
	{
		ZipInputStream zipinputstream = null;
		try
		{
			final byte[] buf = new byte[1024];
			ZipEntry zipentry;
			zipinputstream = new ZipInputStream(zipis);
			zipentry = zipinputstream.getNextEntry();
			while (zipentry != null)
			{
				// for each entry to be extracted
				final String entryName = zipentry.getName();
				final File newFile = new File(destinationDir, entryName);
				if (zipentry.isDirectory())
				{
					if (!newFile.exists())
					{
						newFile.mkdirs();
					}
				}
				else
				{
					if (!newFile.getParentFile().exists())
					{
						newFile.getParentFile().mkdirs();
					}
					int n;
					final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile
							.getAbsolutePath()));
					while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
					{
						bos.write(buf, 0, n);
					}
					bos.flush();
					bos.close();
				}

				zipinputstream.closeEntry();
				zipentry = zipinputstream.getNextEntry();

			}// while
			return true;
		}
		finally
		{
			try
			{
				if (zipinputstream != null)
				{
					zipinputstream.close();
				}
			}
			catch (final IOException e)
			{
				// do nothing
			}
		}
	}

	public static boolean deleteFile(final File file)
	{
		if (file.exists())
		{
			if (file.isDirectory())
			{
				final File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					// do not return from here, else it will not delete other
					// files in the same directory.
					deleteFile(files[i]);
				}
			}
			return file.delete();
		}
		return false;
	}

	/**
	 * returns the name for the log directory to be used which is of the format
	 * <testName>/<MM_dd_yyyy_hh_mm>/ Note that the name ends with the file
	 * separator char.
	 */
	public static String createDirWithTimestamp(final String childDir, final boolean append)
	{
		final StringBuffer sbDirName = new StringBuffer(30);
		// append test name and file separator.
		sbDirName.append(childDir);
		if (sbDirName.charAt(sbDirName.length() - 1) != File.separatorChar)
		{
			sbDirName.append(File.separatorChar);
		}
		// append timestamp in MM_dd_yyyy_hh_mm
		final Calendar dtNow = Calendar.getInstance();
		sbDirName.append(dtNow.get(Calendar.MONTH) + 1);
		sbDirName.append('_');
		sbDirName.append(dtNow.get(Calendar.DAY_OF_MONTH));
		sbDirName.append('_');
		sbDirName.append(dtNow.get(Calendar.YEAR));
		sbDirName.append('_');
		sbDirName.append(dtNow.get(Calendar.HOUR_OF_DAY));
		sbDirName.append('_');
		sbDirName.append(dtNow.get(Calendar.MINUTE));
		sbDirName.append('_');
		sbDirName.append(dtNow.get(Calendar.SECOND));
		sbDirName.append('_');
		sbDirName.append(dtNow.get(Calendar.MILLISECOND) % 100);
		if (append)
		{
			sbDirName.append(File.separatorChar);
		}
		return sbDirName.toString();
	}

	/**
	 * ServerLauncher # readDataFromProcess
	 * 
	 * @param process
	 */
	public static StreamPumper [] readDataFromProcess(final Process process, final OutputStream out, final OutputStream err)
	{
		if (process != null)
		{
			final StreamPumper spOut = new StreamPumper(new BufferedInputStream(process.getInputStream()), out);
			spOut.setDaemon(true);
			spOut.start();
			final StreamPumper spErr = new StreamPumper(new BufferedInputStream(process.getErrorStream()), err);
			spErr.setDaemon(true);
			spErr.start();
			
			return new StreamPumper[] {spOut, spErr};
		}
		return null;
	}

	/**
	 * ServerLauncher # readDataFromProcess
	 * 
	 * @param process
	 */
	public static void readDataFromProcess(final Process process, final File opFolder)
	{
		if (process != null)
		{
			FileOutputStream fosOut = null;
			FileOutputStream fosErr = null;
			try
			{
				// for debugging
				if ((opFolder != null) && opFolder.exists() && opFolder.isDirectory())
				{
					fosOut = new FileOutputStream(new File(opFolder, "debug.out")); //$NON-NLS-1$
					fosErr = new FileOutputStream(new File(opFolder, "debug.err")); //$NON-NLS-1$
				}
			}
			catch (final Throwable th)
			{
				fosOut = null;
				fosErr = null;
			}
			readDataFromProcess(process, fosOut, fosErr);
		}
	}
	
	public static int getExitValue(final Process process, final int timeout)
	{
		if (process != null)
		{
			final long startTime = System.currentTimeMillis();
			final boolean bContinue = true;
			while (bContinue)
			{
				try
				{
					return process.exitValue();
				}
				catch (final IllegalThreadStateException ex)
				{
					try
					{
						// wait for 250 ms
						Thread.sleep(250);
					}
					catch (final InterruptedException e)
					{
						// DO NOTHING
					}
				}
				// if +ve timeout value then validate timeout
				if ((timeout >= 0) && (System.currentTimeMillis() - startTime > timeout))
				{
					process.destroy();
					return -1; // -1 indicate timeout
				}
			}
		}
		return 0;
	}
	
	public static String getLengthRestrictedString(String original, int maxLen)
	{
		if (original == null || original.length() <= maxLen)
		{
			return original;
		}
		StringBuffer buffer = new StringBuffer(original);
		buffer.setLength(Math.max(0, maxLen - 3));
		buffer.append(TRUNCATED);
		return buffer.toString(); 
	}

}
