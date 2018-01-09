package com.queryio.common.remote;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdfs.DFSConfigKeys;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CustomTagDBConfig;
import com.queryio.common.database.CustomTagDBConfigManager;
import com.queryio.common.service.remote.QueryIOResponse;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StreamPumper;

public class MapRedOperation {
	private static Map queryIOStreamMap = new HashMap();

	public static QueryIOResponse runJob(String installDir, String nodeId, String jobName, String jarPath,
			String libJars, String files, String className, String arguments, String hdfsUri, String dbSourceId,
			String analyticsDbName, String encryptionKey, boolean isRecursive, boolean isFilterApply,
			String filterQuery, boolean deleteJobFile) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Running MR job");

		String[] cmd = getCommand(nodeId, installDir, jarPath, libJars, files, className, arguments, hdfsUri,
				dbSourceId, analyticsDbName, isRecursive, isFilterApply, filterQuery, encryptionKey);
		QueryIOResponse resp = executeYarnCommand(cmd, installDir, false, 0, false, libJars);
		if (deleteJobFile)
			deleteFolder(installDir, jobName);
		return resp;
	}

	public static QueryIOResponse runJobDataTagging(String installDir, String nodeId, String jobName, String jarPath,
			String libJars, String files, String className, String arguments, String hdfsUri, String dbSourceId,
			String encryptionKey, String fileTypeParser) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Running MR job");

		String[] cmd = getCommandDatatagging(nodeId, installDir, jarPath, libJars, files, className, arguments, hdfsUri,
				dbSourceId, encryptionKey, fileTypeParser);
		QueryIOResponse resp = executeYarnCommand(cmd, installDir, false, 0, false, libJars);
		return resp;
	}

	@SuppressWarnings("unchecked")
	private static String[] getCommand(String nodeId, String installDir, String jarPath, String libJars, String files,
			String className, String arguments, String hdfsUri, String dbSourceId, String analyticsDbName,
			boolean isRecursive, boolean isFilterApply, String filterQuery, String encryptionKey) {
		String libJarString = "";
		String filesString = "";
		if (libJars != null && !libJars.isEmpty()) {
			String[] arr = libJars.split(",");
			for (int i = 0; i < arr.length; i++) {
				if (i != 0)
					libJarString += ",";
				libJarString += installDir + "/" + QueryIOConstants.MAPREDRESOURCE + "/" + arr[i].trim();
			}
		}
		if (files != null && !files.isEmpty()) {
			String[] arr = files.split(",");
			for (int i = 0; i < arr.length; i++) {
				if (i != 0)
					filesString += ",";
				filesString += installDir + "/" + QueryIOConstants.MAPREDRESOURCE + "/" + arr[i].trim();
			}
		}

		List cmds = new ArrayList();
		cmds.add("bin/yarn");
		cmds.add("--config");
		cmds.add("etc/resourcemanager-conf_" + nodeId);
		cmds.add("jar");
		cmds.add(installDir + "/" + QueryIOConstants.MAPREDRESOURCE + "/" + jarPath);
		if (className != null && !className.isEmpty())
			cmds.add(className);
		cmds.add("-D" + DFSConfigKeys.FS_DEFAULT_NAME_KEY + "=" + hdfsUri);
		cmds.add("-D" + QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID + "=" + dbSourceId);
		cmds.add("-D" + QueryIOConstants.ANALYTICS_DB_DBSOURCEID + "=" + analyticsDbName);
		cmds.add("-D" + QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY + "=" + encryptionKey);

		// Input path filter
		cmds.add("-D" + QueryIOConstants.HIVE_QUERYIO_PARSE_RECURSIVE + "=" + isRecursive);
		cmds.add("-D" + QueryIOConstants.HIVE_QUERYIO_FILTER_APPLY + "=" + isFilterApply);
		cmds.add("-D" + QueryIOConstants.HIVE_QUERYIO_FILTER_QUERY + "=" + filterQuery);

		// DB for input path filter
		CustomTagDBConfig customTagDBConf = CustomTagDBConfigManager.getConfig(dbSourceId);
		cmds.add("-D" + QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_DRIVER_FILTER + "="
				+ customTagDBConf.getCustomTagDriverClass());
		cmds.add("-D" + QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_URL_FILTER + "="
				+ customTagDBConf.getCustomTagUrl());
		cmds.add("-D" + QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_USERNAME_FILTER + "="
				+ customTagDBConf.getCustomTagUserName());
		cmds.add("-D" + QueryIOConstants.QUERYIO_HIVE_METASTORE_CONNECTION_PASSWORD_FILTER + "="
				+ customTagDBConf.getCustomTagPassword());

		if (!libJarString.isEmpty()) {
			cmds.add("-libjars");
			cmds.add(libJarString);
		}
		if (!filesString.isEmpty()) {
			cmds.add("-files");
			cmds.add(filesString);
		}

		String str = "";
		boolean flag = false;
		if (arguments != null && !arguments.isEmpty()) {
			for (char c : arguments.toCharArray()) {
				if (c == ' ' && !flag) {
					cmds.add(str);
					str = "";
				} else {
					if (c == '\'') {
						flag = !flag;
					} else {
						str += c;
					}
				}
			}
			if (!str.isEmpty())
				cmds.add(str);
		}

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug(cmds);

		String[] cmdArray = new String[cmds.size()];
		cmds.toArray(cmdArray);

		return cmdArray;

	}

	private static String[] getCommandDatatagging(String nodeId, String installDir, String jarPath, String libJars,
			String files, String className, String arguments, String hdfsUri, String dbSourceId, String encryptionKey,
			String fileTypeParser) {
		String libJarString = "";
		String filesString = "";
		if (libJars != null && !libJars.isEmpty()) {
			String[] arr = libJars.split(",");
			for (int i = 0; i < arr.length; i++) {
				if (i != 0)
					libJarString += ",";
				libJarString += installDir + "/" + QueryIOConstants.MAPREDRESOURCE + "/" + arr[i].trim();
			}
		}
		if (files != null && !files.isEmpty()) {
			String[] arr = files.split(",");
			for (int i = 0; i < arr.length; i++) {
				if (i != 0)
					filesString += ",";
				filesString += installDir + "/" + QueryIOConstants.MAPREDRESOURCE + "/" + arr[i].trim();
			}
		}

		List cmds = new ArrayList();
		cmds.add("bin/yarn");
		cmds.add("--config");
		cmds.add("etc/resourcemanager-conf_" + nodeId);
		cmds.add("jar");
		cmds.add(installDir + "/" + QueryIOConstants.MAPREDRESOURCE + "/" + jarPath);
		if (className != null && !className.isEmpty())
			cmds.add(className);
		cmds.add("-D" + DFSConfigKeys.FS_DEFAULT_NAME_KEY + "=" + hdfsUri);
		cmds.add("-D" + QueryIOConstants.CUSTOM_TAG_DB_DBSOURCEID + "=" + dbSourceId);
		cmds.add("-D" + QueryIOConstants.QUERYIO_DFS_DATA_ENCRYPTION_KEY + "=" + encryptionKey);

		cmds.add("-D" + QueryIOConstants.DATATAGGING_GENERIC_FILE_TYPES_PARSERS_KEY + "=" + fileTypeParser);

		if (!libJarString.isEmpty()) {
			cmds.add("-libjars");
			cmds.add(libJarString);
		}
		if (!filesString.isEmpty()) {
			cmds.add("-files");
			cmds.add(filesString);
		}

		String str = "";
		boolean flag = false;
		if (arguments != null && !arguments.isEmpty()) {
			for (char c : arguments.toCharArray()) {
				if (c == ' ' && !flag) {
					cmds.add(str);
					str = "";
				} else {
					if (c == '\'') {
						flag = !flag;
					} else {
						str += c;
					}
				}
			}
			if (!str.isEmpty())
				cmds.add(str);
		}

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug(cmds);

		String[] cmdArray = new String[cmds.size()];
		cmds.toArray(cmdArray);

		return cmdArray;

	}

	public static QueryIOResponse createStream(String installDir, String relativePath, String objectName) {

		File file = new File(
				installDir + "/" + QueryIOConstants.MAPREDRESOURCE + "/" + relativePath + "/" + objectName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Creating stream for " + file.getAbsolutePath());
		try {
			FileOutputStream fos = new FileOutputStream(file);
			QueryIOTransferWrapper stream = new QueryIOTransferWrapper(fos);
			queryIOStreamMap.put(objectName, stream);
			return new QueryIOResponse(true, "");
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse writeToStream(String filePath, byte[] b, int offset, int length) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Writing to stream for " + filePath + ". Size: " + b.length);
		try {
			QueryIOTransferWrapper wrapper = (QueryIOTransferWrapper) queryIOStreamMap.get(filePath);
			if (wrapper != null) {
				wrapper.write(b, 0, length);
				return new QueryIOResponse(true, "");
			} else {
				return new QueryIOResponse(false, "");
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse closeStream(String installDir, String relativePath, String filePath, boolean unzip) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Closing stream for " + filePath);
		try {
			QueryIOTransferWrapper wrapper = (QueryIOTransferWrapper) queryIOStreamMap.get(filePath);
			if (wrapper != null) {
				wrapper.close();
				Enumeration entries;

				String absoluteFilePath = installDir + "/" + QueryIOConstants.MAPREDRESOURCE + File.separator
						+ relativePath + File.separator + filePath;

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("absoluteFilePath: " + absoluteFilePath);
				if (unzip && filePath.endsWith(".zip")) {
					ZipFile zipFile = new ZipFile(absoluteFilePath);

					entries = zipFile.entries();

					while (entries.hasMoreElements()) {
						ZipEntry entry = (ZipEntry) entries.nextElement();

						if (entry.isDirectory()) {
							// Assume directories are stored parents first then
							// children.
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug("Extracting directory: " + entry.getName());
							// This is not robust, just for demonstration
							// purposes.
							(new File(installDir + "/" + QueryIOConstants.MAPREDRESOURCE + File.separator + relativePath
									+ File.separator + entry.getName())).mkdirs();
							continue;
						}

						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Extracting file: " + entry.getName());
						File file = new File(installDir + "/" + QueryIOConstants.MAPREDRESOURCE + File.separator
								+ relativePath + File.separator + entry.getName());
						if (!file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
						copyInputStream(zipFile.getInputStream(entry),
								new BufferedOutputStream(new FileOutputStream(file)));
					}

					zipFile.close();
					new File(absoluteFilePath).delete();

				}
				queryIOStreamMap.remove(filePath);
				return new QueryIOResponse(true, "");
			} else {
				return new QueryIOResponse(false, "");
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

	private static QueryIOResponse executeYarnCommand(String[] cmd, String installDir, final boolean wait, int exitCode,
			boolean getOutput, String libJars) {
		Process process = null;
		StringWriter errorWriter = null;
		StringWriter inputWriter = null;
		try {

			String envp[] = getHadoopClasspath(installDir, libJars);

			process = Runtime.getRuntime().exec(cmd, envp, new File(installDir + QueryIOConstants.HADOOP_DIR_NAME));
			inputWriter = new StringWriter();
			final StreamPumper spInput = new StreamPumper(
					new BufferedReader(new InputStreamReader(process.getInputStream())), inputWriter);
			spInput.start();
			errorWriter = new StringWriter();
			if (process.getErrorStream() != null) {
				final StreamPumper spError = new StreamPumper(
						new BufferedReader(new InputStreamReader(process.getErrorStream())), errorWriter);
				spError.start();
			}
			String applicationId = "";
			String errorString = null;
			boolean flag = false;
			if (!wait) {
				int i = 0;
				while (true) {
					errorString = errorWriter.toString();
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug(i);
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Output of yarn proceess: \n" + inputWriter);
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("Error in yarn proceess: \n" + errorWriter);
					if (errorString.contains("Submitted application")) {
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Error String : " + errorString);
						// applicationId =
						// errorString.substring(errorString.lastIndexOf("Submitted
						// application") + 22, errorString.lastIndexOf("to
						// ResourceManager") -1);
						// Now it prints message like:
						// 14/05/20 13:08:31 INFO impl.YarnClientImpl: Submitted
						// application application_1400569938350_0004
						// 14/05/20 13:08:31 INFO mapreduce.Job: The url to
						// track the job:
						applicationId = errorString.substring(errorString.lastIndexOf("Submitted application") + 22);
						applicationId = applicationId.substring(0, applicationId.indexOf("\n"));
						flag = true;
						break;
					}
					if (spInput.isProcessCompleted()) {
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("process.exitValue(): " + process.exitValue());
						if (process.exitValue() != 0) {
							flag = false;
							break;
						} else {
							flag = true;
							break;
						}
					}
					Thread.sleep(5000);
					i++;
				}
			} else {
				process.waitFor();
			}

			int count = 0;
			while (!spInput.isProcessCompleted() && (count < 5)) {
				Thread.sleep(100);
				count++;
			}
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("errorWriter.toString(): " + errorWriter.toString());
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("inputWriter.toString(): " + inputWriter.toString());

			if (flag)
				return new QueryIOResponse(true, applicationId);
			else
				return new QueryIOResponse(false, errorWriter.toString());
		} catch (final Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			return new QueryIOResponse(false, ex.getMessage());
		} finally {
			if (errorWriter != null) {
				try {
					errorWriter.close();
				} catch (Exception e) {
					// ignore
				}
			}
			if (inputWriter != null) {
				try {
					inputWriter.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	private static String[] getHadoopClasspath(String installDir, String libJars) {
		String libJarString = "";
		if (libJars != null && !libJars.isEmpty()) {
			String[] arr = libJars.split(",");
			for (int i = 0; i < arr.length; i++) {
				if (i != 0)
					libJarString += File.pathSeparator;
				libJarString += installDir + "/" + QueryIOConstants.MAPREDRESOURCE + "/" + arr[i].trim();
			}
		}
		return new String[] { "HADOOP_CLASSPATH=$HADOOP_CLASSPATH" + File.pathSeparator + libJarString };
	}

	public static QueryIOResponse createJarCopyStream(String installDir, String jarFileName) {

		File dir = new File(installDir + QueryIOConstants.QUERYIOAGENT_DIR_NAME + File.separator + "webapps"
				+ File.separator + QueryIOConstants.AGENT_QUERYIO + File.separator + QueryIOConstants.JDBC_JAR_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, jarFileName);
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Creating stream for " + file.getAbsolutePath());
		try {
			FileOutputStream fos = new FileOutputStream(file);
			QueryIOTransferWrapper stream = new QueryIOTransferWrapper(fos);
			queryIOStreamMap.put(jarFileName, stream);
			return new QueryIOResponse(true, "");
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse writeJarToStream(String jarName, byte[] b, int offset, int length) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Writing to stream for " + jarName + ". Size: " + b.length);
		try {
			QueryIOTransferWrapper wrapper = (QueryIOTransferWrapper) queryIOStreamMap.get(jarName);
			if (wrapper != null) {
				wrapper.write(b, 0, length);

				return new QueryIOResponse(true, "");
			} else {
				return new QueryIOResponse(false, "");
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse closeJarCopyStream(String installDir, String jarFileName) {
		try {
			QueryIOTransferWrapper wrapper = (QueryIOTransferWrapper) queryIOStreamMap.get(jarFileName);
			wrapper.close();
			return new QueryIOResponse(true, "");
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse createDBConfigCopyStream(String installDir, String fileName) {

		File dir = new File(installDir + QueryIOConstants.QUERYIOAGENT_DIR_NAME + File.separator + "webapps"
				+ File.separator + QueryIOConstants.AGENT_QUERYIO + File.separator + "conf");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, fileName);

		try {
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			QueryIOTransferWrapper stream = new QueryIOTransferWrapper(fos);
			queryIOStreamMap.put(fileName, stream);
			return new QueryIOResponse(true, "");
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse writeDBCOnfigFile(String fileName, byte[] b, int offset, int length) {

		try {
			QueryIOTransferWrapper wrapper = (QueryIOTransferWrapper) queryIOStreamMap.get(fileName);
			if (wrapper != null) {
				wrapper.write(b, 0, length);

				return new QueryIOResponse(true, "");
			} else {
				return new QueryIOResponse(false, "");
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse closeDBConfigCopyStream(String installDir, String fileName) {
		try {
			QueryIOTransferWrapper wrapper = (QueryIOTransferWrapper) queryIOStreamMap.get(fileName);
			wrapper.close();
			return new QueryIOResponse(true, "");
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
			return new QueryIOResponse(false, e.getMessage());
		}
	}

	public static QueryIOResponse deleteFile(String installDir, String folderName, String fileName) {
		QueryIOResponse resp = null;
		try {
			boolean success = new File(installDir + "/" + QueryIOConstants.MAPREDRESOURCE + File.separator + folderName
					+ File.separator + fileName).delete();
			resp = new QueryIOResponse(success, null);
		} catch (Exception e) {
			resp = new QueryIOResponse(false, e.getLocalizedMessage());
		}
		return resp;
	}

	public static QueryIOResponse deleteFolder(String installDir, String folderName) {
		QueryIOResponse resp = null;
		try {
			boolean success = FileUtil.fullyDelete(
					new File(installDir + "/" + QueryIOConstants.MAPREDRESOURCE + File.separator + folderName));
			resp = new QueryIOResponse(success, null);
		} catch (Exception e) {
			resp = new QueryIOResponse(false, e.getLocalizedMessage());
		}
		return resp;
	}

	public static HashMap getNodeManagersLogsPath(String installDir, String nodeId, String applicationId) {
		HashMap containerLogMap = new LinkedHashMap();
		Configuration conf = new Configuration(false);
		String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + QueryIOConstants.NODEMANAGER
				+ "-conf_" + nodeId + "/yarn-site.xml";
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(new File(fileName));
			conf.addResource(fis);
			String logPath = conf.get("yarn.nodemanager.log-dirs");
			if ((logPath != null) && !(logPath.isEmpty())) {
				File folderPath = new File(logPath + File.separator + applicationId);
				if (folderPath.exists()) {
					File[] containerFolders = folderPath.listFiles();
					if ((containerFolders != null) && (containerFolders.length > 0)) {
						for (int i = 0; i < containerFolders.length; i++) {
							File file = new File(containerFolders[i].getAbsolutePath() + File.separator + "syslog");
							if (file.exists()) {
								containerLogMap.put(containerFolders[i].getName(), file.getAbsolutePath());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Exception: " + e.getLocalizedMessage(), e);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
		}

		return containerLogMap;
	}
}