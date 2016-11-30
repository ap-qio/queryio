package com.queryio.core.conf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.BasicConfigurator;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.SSHRemoteExecution;
import com.queryio.common.util.StartupParameters;
import com.queryio.common.util.StaticUtilities;
import com.queryio.common.util.StreamPumper;
import com.queryio.common.util.ZipUtil;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;

public class OneTimeConfig {
	/*
	 * Installation of hadoop on remote system. return true if installation is
	 * successful.
	 */
	static boolean installHadoop(Host host, String userName, String password, String sshPrivateKey, String javaHomePath,
			String port, boolean isLocal) throws Exception {
		boolean isInstalled = false;
		// AppLogger.getLogger().fatal("Port: "+port);

		// zip hadoop structure.
		String zipFileName = "Archive.zip";
		String zipFilePath = StartupParameters.getHadoopDirLocation() + zipFileName;
		String unzipUtilPath = StartupParameters.getHadoopDirLocation() + "UnzipUtil.jar";
		// 1
		// DBConfigDAO.replaceDBConfigFromHadoop();
		// //2
		// updateAgentQueryIOPropertiesFile(host.getAgentPort());
		// //3
		// if(javaHomePath != null && !javaHomePath.isEmpty())
		// updateJavaHome(javaHomePath);
		// 4
		// updateLoggerPropertiesFile(host.getInstallDirPath());

		File zipFile = new File(zipFilePath);
		File unzipUtilFile = new File(unzipUtilPath);
		synchronized (OneTimeConfig.class) {
			if (!zipFile.exists()) {
				File directoryToZip1 = new File(
						StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME);
				File directoryToZip2 = new File(
						StartupParameters.getHadoopDirLocation() + QueryIOConstants.QUERYIOAGENT_DIR_NAME);
				File directoryToZip3 = new File(
						StartupParameters.getHadoopDirLocation() + QueryIOConstants.QUERYIOSERVERS_DIR_NAME);
				File directoryToZip4 = new File(
						StartupParameters.getHadoopDirLocation() + QueryIOConstants.MAPREDRESOURCE);
				File directoryToZip5 = new File(
						StartupParameters.getHadoopDirLocation() + QueryIOConstants.HIVE_DIR_NAME);
				File directoryToZip6 = new File(
						StartupParameters.getHadoopDirLocation() + QueryIOConstants.SCRIPTS_DIR_NAME);
				File directoryToZip7 = new File(StartupParameters.getHadoopDirLocation() + "version.txt");

				ArrayList listFiles = new ArrayList();
				listFiles.add(directoryToZip1);
				listFiles.add(directoryToZip2);
				listFiles.add(directoryToZip3);
				listFiles.add(directoryToZip4);
				listFiles.add(directoryToZip5);
				listFiles.add(directoryToZip6);
				listFiles.add(directoryToZip7);

				ZipUtil.compressFiles(listFiles, zipFilePath);
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Compression performed.");
			}
		}

		if (isLocal) {
			Process process = null;
			StringWriter errorWriter = null;
			StringWriter inputWriter = null;

			try {
				String[] temp = zipFilePath.split("/");
				String remoteDir = host.getInstallDirPath() != null ? host.getInstallDirPath()
						: StartupParameters.getDefaultDirRemote();
				File destPath = new File(remoteDir);

				if (!destPath.exists())
					destPath.mkdirs();

				File dest = new File(destPath, temp[temp.length - 1]);
				boolean scpSuccessful = false;

				try {
					StaticUtilities.copyFileAs(zipFile, dest);
					scpSuccessful = true;
				} catch (Exception e1) {
					AppLogger.getLogger().fatal(e1.getLocalizedMessage(), e1);
				}

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("copyFile: " + scpSuccessful);

				if (scpSuccessful) {
					if (SystemUtils.IS_OS_WINDOWS) {
						try {
							scpSuccessful = false;
							temp = unzipUtilPath.split("/");
							StaticUtilities.copyFileAs(unzipUtilFile, new File(destPath, temp[temp.length - 1]));
							scpSuccessful = true;
						} catch (Exception e1) {
							AppLogger.getLogger().fatal(e1.getLocalizedMessage(), e1);
						}
						if (scpSuccessful) {
							String cmd = "java -cp " + remoteDir + "UnzipUtil.jar com.queryio.common.UnzipUtil "
									+ (remoteDir + zipFileName) + " " + remoteDir;

							AppLogger.getLogger().debug("Executing commands to unzip archive.zip : " + cmd);
							process = Runtime.getRuntime().exec(cmd);
							AppLogger.getLogger().debug("Waiting for commands to get completed..");
							process.waitFor();
							AppLogger.getLogger().debug("Commands executed.");
							cmd = "cmd.exe /C del /F " + (remoteDir + zipFileName);
							process = Runtime.getRuntime().exec(cmd);
							AppLogger.getLogger().debug("Deleting Archive file..");
							process.waitFor();
							AppLogger.getLogger().debug("Commands executed.");
							isInstalled = true;
						}
					} else {
						String[] command1 = { "unzip", "-o", temp[temp.length - 1] };

						process = Runtime.getRuntime().exec(command1, null, destPath);

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

						boolean unblock = true;

						if (unblock) {
							Writer writer = null;
							try {
								writer = new OutputStreamWriter(process.getOutputStream());
								writer.write("Y\nY\n");
								writer.flush();
							} catch (Exception e) {
								// Ignore
							} finally {
								if (writer != null)
									writer.close();
							}
						}

						process.waitFor();
						int count = 0;
						while (!spInput.isProcessCompleted() && (count < 5)) {
							Thread.sleep(100);
							count++;
						}

						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("errorWriter.toString()" + errorWriter.toString());
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("inputWriter.toString()" + inputWriter.toString());
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("targetProcess.exitValue()" + process.exitValue());

						String[] command2 = { "chmod", "-R", "700", destPath.getAbsolutePath() };

						process = Runtime.getRuntime().exec(command2, null, destPath);

						inputWriter = new StringWriter();
						final StreamPumper spInput1 = new StreamPumper(
								new BufferedReader(new InputStreamReader(process.getInputStream())), inputWriter);
						spInput1.start();
						errorWriter = new StringWriter();
						if (process.getErrorStream() != null) {
							final StreamPumper spError = new StreamPumper(
									new BufferedReader(new InputStreamReader(process.getErrorStream())), errorWriter);
							spError.start();
						}

						unblock = true;

						if (unblock) {
							Writer writer = null;
							try {
								writer = new OutputStreamWriter(process.getOutputStream());
								writer.write("Y\nY\n");
								writer.flush();
							} catch (Exception e) {
								// Ignore
							} finally {
								if (writer != null)
									writer.close();
							}
						}

						process.waitFor();
						count = 0;
						while (!spInput1.isProcessCompleted() && (count < 5)) {
							Thread.sleep(100);
							count++;
						}

						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("errorWriter.toString()" + errorWriter.toString());
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("inputWriter.toString()" + inputWriter.toString());
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("targetProcess.exitValue()" + process.exitValue());

						String[] command3 = { "rm", "-f", temp[temp.length - 1] };

						process = Runtime.getRuntime().exec(command3, null, destPath);
						isInstalled = true;
					}

				}
			} catch (final Exception ex) {
				AppLogger.getLogger().fatal(ex.getMessage(), ex);
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
		} else {
			SSHRemoteExecution remoteExec = null;
			Session session = null;

			try {
				remoteExec = new SSHRemoteExecution();

				if (password != null) {
					session = remoteExec.createSession(host.getHostIP(), userName, password, Integer.parseInt(port));
				} else if (sshPrivateKey != null) {
					session = remoteExec.createSessionWithPrivateKeyFile(host.getHostIP(), userName, sshPrivateKey,
							Integer.parseInt(port));
				} else {
					throw new Exception("Session could not be created");
				}

				if (session != null) {
					session.connect(StartupParameters.getSessionTimeout());
					String remoteDir = host.getInstallDirPath() != null ? host.getInstallDirPath()
							: StartupParameters.getDefaultDirRemote();

					boolean scpSuccessful = remoteExec.sftpFileToRemote(zipFile, remoteDir, session);
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("sftpFile: " + scpSuccessful);

					if (scpSuccessful) {
						boolean isUnzipUtilCopied = remoteExec.sftpFileToRemote(unzipUtilFile, remoteDir, session);
						if (isUnzipUtilCopied) {

							String cmd = null;
							if (host.isWindows()) {
								cmd = "java -cp " + remoteDir + "UnzipUtil.jar com.queryio.common.UnzipUtil "
										+ (remoteDir + zipFileName) + " " + remoteDir + " && cd " + remoteDir
										+ " && del /F " + (remoteDir + zipFileName);
							} else {
								cmd = "java -cp " + remoteDir + "UnzipUtil.jar com.queryio.common.UnzipUtil "
										+ (remoteDir + zipFileName) + " " + remoteDir + "; cd " + remoteDir
										+ "; chmod -R 700 *; rm -f " + (remoteDir + zipFileName);
							}
							AppLogger.getLogger().debug("cmd : " + cmd);
							remoteExec.executeCommand(session, cmd);
							if (AppLogger.getLogger().isDebugEnabled())
								AppLogger.getLogger().debug(remoteExec.getOutput());
							isInstalled = true;
						} else {
							AppLogger.getLogger().fatal("UnzipUtil.class could not copied to Host.");
						}
					}
				}
			} finally {
				try {
					if (remoteExec != null)
						remoteExec.closeSession(session);
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return isInstalled;
	}

	public static boolean isWindows(SSHRemoteExecution remoteExec, Session session) {
		try {
			remoteExec.executeCommand(session, "ver");
			String uname = remoteExec.getOutput();
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("ver command output for windows : " + uname);
			if (uname != null && uname.trim().toLowerCase().contains("microsoft windows")) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void updateJavaHome(String javaHomePath) throws IOException {
		// StartupParameters.getHadoopDirLocation() +
		// QueryIOConstants.HADOOP_DIR_NAME
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("updateJavaHome()");
		String[] filePaths = {
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME + "/bin/yarn",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME
						+ "/etc/datanode-conf/hadoop-env.sh",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME
						+ "/etc/namenode-conf/hadoop-env.sh",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME
						+ "/etc/nodemanager-conf/hadoop-env.sh",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME
						+ "/etc/resourcemanager-conf/hadoop-env.sh",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME
						+ "/etc/secondarynamenode-conf/hadoop-env.sh",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME
						+ "/etc/nodemanager-conf/yarn-env.sh",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME
						+ "/etc/resourcemanager-conf/yarn-env.sh",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME
						+ "/etc/nodemanager-conf/mapred-env.sh",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME
						+ "/etc/resourcemanager-conf/mapred-env.sh",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME + "/bin/hdfs",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME + "/bin/mapred",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME + "/bin/rcc",
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME + "/bin/hadoop" };
		for (String filePath : filePaths) {
			File in = new File(filePath);
			File out = new File(filePath);

			BufferedReader reader = null;
			Writer writer = null;

			ArrayList lines = new ArrayList();
			ArrayList myLines = new ArrayList();
			boolean flag = false;
			try {
				reader = new BufferedReader(new FileReader(in));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (line.contains("export JAVA_HOME=")) {
						lines.add("export JAVA_HOME=" + javaHomePath);
						flag = true;
					} else {
						lines.add(line);
					}
				}
				if (!flag) {
					myLines.add("export JAVA_HOME=" + javaHomePath);
				}

				writer = new BufferedWriter(new FileWriter(out));
				for (int i = 0; i < myLines.size(); i++) {
					writer.write((String) myLines.get(i));
					writer.write("\n");
				}
				for (int i = 0; i < lines.size(); i++) {
					writer.write((String) lines.get(i));
					writer.write("\n");
				}
			} finally {
				try {
					if (reader != null)
						reader.close();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
				try {
					if (writer != null)
						writer.close();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		}
	}

	private static void updateHadoopHome() throws IOException {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("updateHadoopHome()");
		String hadoopHome = StartupParameters.getHadoopDirLocation() + QueryIOConstants.HADOOP_DIR_NAME;
		String[] filePaths = {
				StartupParameters.getHadoopDirLocation() + QueryIOConstants.HIVE_DIR_NAME + "/conf/hive-env.sh" };
		for (String filePath : filePaths) {
			File in = new File(filePath);
			File out = new File(filePath);

			BufferedReader reader = null;
			Writer writer = null;

			ArrayList lines = new ArrayList();
			ArrayList myLines = new ArrayList();
			boolean flag = false;
			try {
				reader = new BufferedReader(new FileReader(in));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (line.contains("HADOOP_HOME=")) {
						lines.add("HADOOP_HOME=" + hadoopHome);
						flag = true;
					} else {
						lines.add(line);
					}
				}
				if (!flag) {
					myLines.add("HADOOP_HOME=" + hadoopHome);
				}

				writer = new BufferedWriter(new FileWriter(out));
				for (int i = 0; i < myLines.size(); i++) {
					writer.write((String) myLines.get(i));
					writer.write("\n");
				}
				for (int i = 0; i < lines.size(); i++) {
					writer.write((String) lines.get(i));
					writer.write("\n");
				}
			} finally {
				try {
					if (reader != null)
						reader.close();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
				try {
					if (writer != null)
						writer.close();
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
		}
	}

	private static void updateQueryIOServicsSerrverConfigFile(String servicesPort) throws IOException {
		String filePath = StartupParameters.getHadoopDirLocation() + QueryIOConstants.QUERYIOSERVERS_DIR_NAME
				+ "/conf/server_service.xml";

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("updateQueryIOServicsSerrverConfigFile()");

		Writer writer = null;

		ArrayList lines = new ArrayList();

		lines.add("<Server port=\"" + (Integer.parseInt(servicesPort) + 1) + "\" shutdown=\"SHUTDOWN\">");
		lines.add("\t<Service name=\"Catalina\">");
		lines.add("\t\t<Connector port=\"" + servicesPort + "\"/>");
		lines.add("\t\t\t<Engine name=\"Catalina\" defaultHost=\"localhost\">");
		lines.add("\t\t\t\t<Host name=\"localhost\" debug=\"0\" appBase=\"webapps\" >");
		lines.add("\t\t\t</Host>");
		lines.add("\t\t</Engine>");
		lines.add("\t</Service>");
		lines.add("</Server>");

		File out = new File(filePath);
		try {
			writer = new BufferedWriter(new FileWriter(out));

			for (int i = 0; i < lines.size(); i++) {
				writer.write((String) lines.get(i));
				writer.write("\n");
			}
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

	}

	// private static void updateAgentQueryIOPropertiesFile(String
	// queryIOAgentPort) throws IOException{
	// String propFilePath = StartupParameters.getHadoopDirLocation() +
	// QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/webapps/" +
	// QueryIOConstants.AGENT_QUERYIO + "/conf/queryioagent.properties";
	//
	// if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug("updateAgentQueryIOPropertiesFile()");
	//
	// File in = new File(propFilePath);
	// File out = new File(propFilePath);
	//
	// BufferedReader reader = null;
	// Writer writer = null;
	//
	// ArrayList lines = new ArrayList();
	//
	//
	// WebContext ctx = WebContextFactory.get();
	// HttpServletRequest request = ctx.getHttpServletRequest();
	//
	// String protocol = request.isSecure() ? "https" : "http";
	//
	// String queryIOGroupInfoURL = protocol + "://" +
	// StaticUtilities.getHostAddress() + ":" + request.getServerPort() +
	// "/queryio/GetUserGroupInformation";
	//
	// if(AppLogger.getLogger().isDebugEnabled())
	// AppLogger.getLogger().debug("queryIOGroupInfoURL: " +
	// queryIOGroupInfoURL);
	//
	// try
	// {
	// reader = new BufferedReader(new FileReader(in));
	//
	// String line = null;
	// while ((line = reader.readLine()) != null){
	// if(line.contains("queryIOGroupInfoURL")){
	// lines.add("queryIOGroupInfoURL=" + queryIOGroupInfoURL);
	// }else if(line.contains(QueryIOConstants.QUERYIOAGENT_PORT)){
	// lines.add(QueryIOConstants.QUERYIOAGENT_PORT + "=" + queryIOAgentPort);
	// }else{
	// lines.add(line);
	// }
	// }
	// }
	// finally
	// {
	// try
	// {
	// if(reader != null)
	// reader.close();
	// }
	// catch(Exception e)
	// {
	// AppLogger.getLogger().fatal(e.getMessage(), e);
	// }
	// }
	//
	// try
	// {
	// writer = new BufferedWriter(new FileWriter(out));
	//
	// for(int i=0; i<lines.size(); i++)
	// {
	// writer.write((String) lines.get(i));
	// writer.write("\n");
	// }
	// }
	// finally
	// {
	// try
	// {
	// if(writer != null)
	// writer.close();
	// }
	// catch(Exception e)
	// {
	// AppLogger.getLogger().fatal(e.getMessage(), e);
	// }
	// }
	// }

	private static void updateLoggerPropertiesFile(String homeDir) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("updateLoggerPropertiesFile()");
		String propFilePath = StartupParameters.getHadoopDirLocation() + QueryIOConstants.QUERYIOSERVERS_DIR_NAME
				+ "/webapps/hdfs-over-ftp/WEB-INF/log4j.properties";
		updateLoggerPropertiesFile(propFilePath, "ftp", homeDir, "ftpserver");

		propFilePath = StartupParameters.getHadoopDirLocation() + QueryIOConstants.QUERYIOSERVERS_DIR_NAME
				+ "/webapps/os3server/WEB-INF/os3log4j.properties";
		updateLoggerPropertiesFile(propFilePath, "os3", homeDir, "os3server");
	}

	private static void updateLoggerPropertiesFile(String propFilePath, String prefix, String homeDir,
			String logFileName) throws Exception {
		File in = new File(propFilePath);
		File out = new File(propFilePath);

		BufferedReader reader = null;
		Writer writer = null;

		ArrayList lines = new ArrayList();

		try {
			reader = new BufferedReader(new FileReader(in));

			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains("log4j.appender." + prefix + ".File")) {
					lines.add("log4j.appender." + prefix + ".File=" + homeDir + QueryIOConstants.QUERYIOSERVERS_DIR_NAME
							+ "/logs/" + logFileName + ".log");
				} else {
					lines.add(line);
				}
			}
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}

		try {
			writer = new BufferedWriter(new FileWriter(out));

			for (int i = 0; i < lines.size(); i++) {
				writer.write((String) lines.get(i));
				writer.write("\n");
			}
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	static void uninstallHost(Host host, String userName, String password, String sshPrivateKey, String port,
			boolean isLocal) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Uninstalling host");
		String cmd = QueryIOConstants.EMPTY_STRING;
		String remoteDir = host.getInstallDirPath() != null ? host.getInstallDirPath()
				: StartupParameters.getDefaultDirRemote();
		if (isLocal) {
			QueryIOAgentManager.stopQueryIOAgent(host, "", "", "", "", true);
			cmd += "crontab -r; ";
			cmd += "rm -rf " + remoteDir;
			Runtime.getRuntime().exec(cmd);
		} else {
			SSHRemoteExecution remoteExec = null;
			Session session = null;
			try {
				remoteExec = new SSHRemoteExecution();

				if (password != null) {
					session = remoteExec.createSession(host.getHostIP(), userName, password, Integer.parseInt(port));
				} else if (sshPrivateKey != null) {
					session = remoteExec.createSessionWithPrivateKeyFile(host.getHostIP(), userName, sshPrivateKey,
							Integer.parseInt(port));
				} else {
					throw new Exception("Session could not be created");
				}
				if (session != null) {
					session.connect(StartupParameters.getSessionTimeout());

					cmd += "crontab -r; ";
					String pid = null;
					if (password != null) {
						pid = getRemotePID(host, userName, password, QueryIOConstants.QUERYIOAGENT_DIR_NAME, port);
					} else {
						pid = getRemotePIDusingPrivateKey(host, userName, sshPrivateKey,
								QueryIOConstants.QUERYIOAGENT_DIR_NAME, port);
					}
					if (pid != null) {
						cmd += "kill -9 " + pid + "; ";
					}

					cmd += "rm -rf " + remoteDir;

					remoteExec.executeCommand(session, cmd);
				}
			} finally {
				if (remoteExec != null)
					remoteExec.closeSession(session);
			}
		}
	}

	public static String getRemotePID(Host host, String userName, String password, String procName, String port)
			throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Fetching remote PID for QueryIOAgent");
		SSHRemoteExecution remoteExec = null;
		Session session = null;
		String procPID = QueryIOConstants.EMPTY_STRING;
		try {
			remoteExec = new SSHRemoteExecution();
			session = remoteExec.createSession(host.getHostIP(), userName, password, Integer.parseInt(port));

			if (session != null) {
				session.connect(StartupParameters.getSessionTimeout());

				if (host.isWindows()) {
					procPID = getProcessIDWindows(procName, remoteExec, session);
				} else {
					procPID = getProcessIDUnixBased(procName, remoteExec, session);

				}
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("PID: " + procPID);
			}
		} finally {
			if (remoteExec != null)
				remoteExec.closeSession(session);
		}

		return procPID;
	}

	public static String getRemotePIDusingPrivateKey(Host host, String userName, String sshPrivateKey, String procName,
			String port) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Fetching remote PID for QueryIOAgent");
		SSHRemoteExecution remoteExec = null;
		Session session = null;
		String procPID = QueryIOConstants.EMPTY_STRING;
		try {
			remoteExec = new SSHRemoteExecution();
			session = remoteExec.createSessionWithPrivateKeyFile(host.getHostIP(), userName, sshPrivateKey,
					Integer.parseInt(port));

			if (session != null) {
				session.connect(StartupParameters.getSessionTimeout());
				if (host.isWindows()) {
					procPID = getProcessIDWindows(procName, remoteExec, session);
				} else {
					procPID = getProcessIDUnixBased(procName, remoteExec, session);
				}

				// if(AppLogger.getLogger().isDebugEnabled())
				// AppLogger.getLogger().debug("Remote command output: " +
				// remoteExec.getOutput());
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("PID: " + procPID);
			}
		} finally {
			if (remoteExec != null)
				remoteExec.closeSession(session);
		}

		return procPID;
	}

	private static String getProcessIDUnixBased(String procName, SSHRemoteExecution remoteExec, Session session)
			throws Exception {
		String cmd = "ps -ef";
		String procPID = "";
		remoteExec.executeCommand(session, cmd);

		String[] splits = remoteExec.getOutput().split(QueryIOConstants.NEW_LINE);

		for (int ctr = 0; ctr < splits.length; ctr++) {
			if (!splits[ctr].contains(procName))
				continue;

			splits[ctr] = splits[ctr].trim();

			int i = 0;
			int col = 0;
			boolean space = false;
			while (i < splits[ctr].length()) {
				if (splits[ctr].charAt(i) == ' ')
					space = true;
				else if (space == true) {
					space = false;
					col++;
				}
				if (col == 1 && !space) {
					procPID += splits[ctr].charAt(i);
				}
				i++;
			}
			procPID += " ";
		}
		return procPID;
	}

	private static String getProcessIDWindows(String procName, SSHRemoteExecution remoteExec, Session session)
			throws Exception {
		String cmd = "wmic PROCESS get Processid,Commandline";
		String procPID = "";
		remoteExec.executeCommand(session, cmd);

		String[] splits = remoteExec.getOutput().split(QueryIOConstants.NEW_LINE);
		String line = null;
		for (int i = 0; i < splits.length; i++) {
			line = splits[i];
			if (line.contains(procName)) {
				line = line.trim();
				if (line.length() > 6)
					procPID += line.substring(line.length() - 6, line.length()).trim();
				procPID += " ";
			}
		}
		return procPID;
	}

	public static String getProcessIDWindows(String procName) throws Exception {
		String cmd = "wmic PROCESS get Processid,Commandline";
		String procPID = "";

		String[] splits = IOUtils.toString(Runtime.getRuntime().exec(cmd).getInputStream())
				.split(QueryIOConstants.NEW_LINE);
		String line = null;
		for (int i = 0; i < splits.length; i++) {
			line = splits[i];
			if (line.contains(procName)) {
				line = line.trim();
				if (line.length() > 6)
					procPID += line.substring(line.length() - 6, line.length()).trim();
				procPID += " ";
			}
		}
		return procPID;
	}

	public static String getRemoteUserHomeDirectory(Host host, String userName, String password, String sshPrivateKey,
			int port) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Fetching remote user home directory");
		SSHRemoteExecution remoteExec = null;
		Session session = null;
		String userHome = QueryIOConstants.EMPTY_STRING;
		try {
			remoteExec = new SSHRemoteExecution();

			if (password != null) {
				session = remoteExec.createSession(host.getHostIP(), userName, password, port);
			} else if (sshPrivateKey != null) {
				session = remoteExec.createSessionWithPrivateKeyFile(host.getHostIP(), userName, sshPrivateKey, port);
			} else {
				throw new Exception("Session could not be created");
			}

			if (session != null) {
				session.connect(StartupParameters.getSessionTimeout());

				String cmd = "pwd";

				remoteExec.executeCommand(session, cmd);

				userHome = remoteExec.getOutput();

				if (userHome == null || userHome.isEmpty()) {
					// Windows handling.
					remoteExec.executeCommand(session, "cd");
					userHome = remoteExec.getOutput();
				}

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug(
							"UserHome directory for " + host.getHostIP() + ", user: " + userName + " is " + userHome);
			}
		} finally {
			if (remoteExec != null)
				remoteExec.closeSession(session);
		}

		return userHome;
	}

	public static boolean validateJavaHome(String hostName, String userName, String password, String sshPrivateKey,
			String javaHome, String port) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("validating java home");
		SSHRemoteExecution remoteExec = null;
		Session session = null;
		boolean isValidationSuccess = false;
		String output = QueryIOConstants.EMPTY_STRING;
		try {
			remoteExec = new SSHRemoteExecution();

			if (password != null) {
				session = remoteExec.createSession(hostName, userName, password, Integer.parseInt(port));
			} else if (sshPrivateKey != null) {
				session = remoteExec.createSessionWithPrivateKeyFile(hostName, userName, sshPrivateKey,
						Integer.parseInt(port));
			} else {
				throw new Exception("Session could not be created");
			}

			if (session != null) {
				session.connect(StartupParameters.getSessionTimeout());

				String javaInsidePath = File.separator + "bin" + File.separator + "java";
				String cmd = "ls " + javaHome.trim() + javaInsidePath;
				remoteExec.executeCommand(session, cmd);

				output = remoteExec.getOutput();
				if (output != null) {
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger()
								.debug("output :" + output + " length:" + output.length() + " cmd :" + cmd);

					if (output.indexOf(javaHome.trim() + javaInsidePath) == 0) {
						isValidationSuccess = true;
					} else {
						// Windows
						cmd = "dir " + javaHome.trim() + javaInsidePath + ".exe";
						remoteExec.executeCommand(session, cmd);
						output = remoteExec.getOutput();
						if (output != null && output.toLowerCase().contains("java.exe")) {
							isValidationSuccess = true;
						}
					}
				}

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger()
							.debug("JAVA_Version for " + hostName + ", user: " + userName + " for JAVA_HOME " + javaHome
									+ "  validation" + isValidationSuccess + " index :" + output != null
											? output.indexOf(javaHome + "/bin/java") : "");
			}
		} finally {
			if (remoteExec != null)
				remoteExec.closeSession(session);
		}
		return isValidationSuccess;
	}

	public static String getJavaHome(String hostName, String userName, String password, String sshPrivateKey, int port)
			throws Exception {
		// /System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Fetching remote user JAVA_HOME");
		SSHRemoteExecution remoteExec = null;
		Session session = null;
		String javaHome = QueryIOConstants.EMPTY_STRING;
		try {
			remoteExec = new SSHRemoteExecution();

			if (password != null) {
				session = remoteExec.createSession(hostName, userName, password, port);
			} else if (sshPrivateKey != null) {
				session = remoteExec.createSessionWithPrivateKeyFile(hostName, userName, sshPrivateKey, port);
			} else {
				throw new Exception("Session could not be created");
			}

			if (session != null) {
				session.connect(StartupParameters.getSessionTimeout());

				String cmd = "uname";

				remoteExec.executeCommand(session, cmd);

				String uname = remoteExec.getOutput();
				if (uname.startsWith("Darwin") || uname.startsWith("darwin")) {
					// macOSX
					cmd = "/usr/libexec/java_home";
					remoteExec.executeCommand(session, cmd);
					// javaHome = QueryIOConstants.DEFAULT_JAVA_HOME_MACOSX;
					// cmd = "cd " + javaHome;
					// remoteExec.executeCommand(session, cmd);
					if (remoteExec.getExitCode() != 0) {
						javaHome = "";
					} else {
						javaHome = remoteExec.getOutput().trim();
					}

				} else if (uname.startsWith("Linux") || uname.startsWith("linux")) {
					cmd = "whereis java";

					remoteExec.executeCommand(session, cmd);

					javaHome = remoteExec.getOutput();

					if (javaHome != null) {
						int index = javaHome.indexOf("/");
						if (index > 0) {
							javaHome = javaHome.substring(index);
						}
						String[] arr = javaHome.split(" ");
						if (arr != null && arr.length > 0)
							javaHome = arr[0];

					}

					cmd = "readlink -f " + javaHome;

					remoteExec.executeCommand(session, cmd);

					javaHome = remoteExec.getOutput();

					if (javaHome.contains("/bin/java"))
						javaHome = javaHome.substring(0, javaHome.indexOf("/bin/java"));
				} else {
					// Get windows OS type.
					remoteExec.executeCommand(session, "ver");
					uname = remoteExec.getOutput();
					if (AppLogger.getLogger().isDebugEnabled())
						AppLogger.getLogger().debug("ver command output for windows : " + uname);
					if (uname != null && uname.trim().toLowerCase().contains("microsoft windows")) {
						remoteExec.executeCommand(session, "echo %JAVA_HOME%");
						javaHome = remoteExec.getOutput();
					} else {
						throw new Exception("OS type not resolved.");
					}
				}
				if (!verifyJavaVersion(session, javaHome)) {
					javaHome = "";
				}

				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger()
							.debug("JAVA_HOME for " + hostName + ", user: " + userName + " is " + javaHome);
			}
		} finally {
			if (remoteExec != null)
				remoteExec.closeSession(session);
		}

		return javaHome;
	}

	public static String getJavaHome() throws Exception {
		String output = null;
		String line = null;
		Process process = null;
		BufferedReader inReader = null;
		if (SystemUtils.IS_OS_LINUX) {
			process = Runtime.getRuntime().exec("whereis java");
			inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((output = inReader.readLine()) != null) {
				line = output;
			}
			if (line != null) {
				if (line.contains(":")) {
					line = line.substring(line.indexOf(":") + 1);
				}
				line = line.trim();
				line = line.substring(0, line.indexOf("java") + 4);
				line = line.trim();
			}
			process = Runtime.getRuntime().exec("readlink -f " + line);
			final BufferedReader inReader1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((output = inReader1.readLine()) != null) {
				line = output;
			}
			line = line.substring(0, line.indexOf("/bin"));
		}
		// else if(uname.startsWith("Darwin") || uname.startsWith("darwin"))
		else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
			process = Runtime.getRuntime().exec("/usr/libexec/java_home");
			inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((output = inReader.readLine()) != null) {
				line = output;
			}
			if (line == null) {
				line = "";
			}

		} else if (SystemUtils.IS_OS_WINDOWS) {
			AppLogger.getLogger().debug("Java home using System.getProperty() : " + System.getProperty("java.home"));
			line = System.getProperty("java.home");
		}
		if (!verifyJavaVersion(line)) {
			line = "";
		}
		return line;
	}

	private static boolean verifyJavaVersion(String line) throws IOException {
		String output;
		Process process;
		BufferedReader inReader = null;
		String javaHome = line + File.separator + "bin" + File.separator + "java";
		String versionInfo = "";
		try {
			process = Runtime.getRuntime().exec(javaHome + " -version");
			inReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while ((output = inReader.readLine()) != null) {
				versionInfo += output;
			}
			return checkVersion(versionInfo);
		} finally {
			if (inReader != null) {
				inReader.close();
			}
		}
	}

	private static boolean verifyJavaVersion(Session session, String line) throws Exception {

		String javaHome = line + File.separator + "bin" + File.separator + "java";
		String versionInfo = "";
		String command = javaHome + " -version";

		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		channel.setCommand(command);
		// channel.setInputStream(System.in);
		channel.setErrStream(System.err);
		BufferedReader inReader = null;
		try {
			InputStream stderr = channel.getErrStream();
			channel.connect();

			String output;
			while (channel.getExitStatus() == -1) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
			if (channel.getExitStatus() == 0) {
				inReader = new BufferedReader(new InputStreamReader(stderr));
				while ((output = inReader.readLine()) != null) {
					versionInfo += output;
				}
				return checkVersion(versionInfo);
			}
			return false;
		} finally {
			channel.disconnect();
			if (inReader != null) {
				inReader.close();
			}
		}
	}

	public static boolean checkVersion(String versionInfo) {
		String[] version = versionInfo.split("\"");
		if (version.length > 2) {
			String ver = version[1];
			float parseFloat = Float.parseFloat(ver.substring(0, 3));
			if (parseFloat >= 1.7) {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Java version is 1.7 or higher");
				return true;
			}
		}
		AppLogger.getLogger().error("Java version is below 1.7. QueryIO requires java v1.7 or higher.");
		return false;
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		String javaHome;
		try {
			javaHome = getJavaHome("192.168.0.17", "api-dev-123", "App4ever#", "", 22);
			System.err.println(javaHome);
			System.err.println(validateJavaHome("192.168.0.17", "api-dev-123", "App4ever#", "", javaHome, "22"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public static void main(String[] args) {
	// SSHRemoteExecution remoteExec = null;
	// Session session = null;
	//
	// try
	// {
	// BasicConfigurator.configure();
	// remoteExec = new SSHRemoteExecution();
	//
	// session = remoteExec.createSession("192.168.0.25", "AppPerfect",
	// "appperfect", 22);
	//// session = remoteExec.createSession("192.168.0.22", "admin",
	// "App4ever#", 22);
	//
	//
	// if (session != null)
	// {
	// session.connect(StartupParameters.getSessionTimeout());
	// String remoteDir = "D:\\QueryIO\\test\\";
	//
	// String zipFileName = "Archive.zip";
	// String cmd = "cd & cd";
	//// cmd = "java -cp " + remoteDir + "UnzipUtil.jar
	// com.queryio.common.UnzipUtil " + (remoteDir + zipFileName) + " " +
	// remoteDir + " && cd " + remoteDir + " && del /F " + (remoteDir +
	// zipFileName);
	//// cmd = "cd C:\\Users\\AppPerfect\\QueryIOPackage\\QueryIOAgent\\bin &
	// start /B startQIOAgent.bat
	// C:\\Users\\AppPerfect\\QueryIOPackage\\QueryIOAgent 6680 > help.txt";
	//// cmd = "java -cp " + remoteDir + "UnzipUtil.jar
	// com.queryio.common.UnzipUtil " + (remoteDir + zipFileName) + " " +
	// remoteDir;
	// cmd = "cd D:\\testy & START /B inf.exe > 2.txt";
	// System.out.println("cmd : " + cmd);
	//// remoteExec.executeCommand(session, cmd);
	// Channel channel = session.openChannel("exec");
	// ((ChannelExec) channel).setCommand(cmd);
	// ((ChannelExec) channel).setPty(false);
	// channel.setInputStream(null);
	// channel.setOutputStream(null);
	// channel.connect();
	// Thread.sleep(10000);
	// channel.disconnect();
	// session.disconnect();
	//// Channel channel = session.openChannel("exec");
	//// ((ChannelExec)channel).setCommand(cmd);
	//// channel.connect();
	//// System.out.println(remoteExec.getOutput());
	// }
	// }
	// catch(Exception e) {
	// e.printStackTrace();
	// }
	// finally
	// {
	// try {
	// if (remoteExec != null)
	// remoteExec.closeSession(session);
	// }catch(Exception e){
	// // ignore
	// }
	// }
	// }

	// public static void main(String[] args) {
	// ChannelSftp channelSftp = null;
	// SSHRemoteExecution remoteExec = null;
	// Session session = null;
	// try
	// {
	// String remoteDirectory = "C:\\Users\\AppPerfect\\QueryIO";
	//// String remoteDirectory = "/C:/Users/AppPerfect/QueryIO";
	// remoteExec = new SSHRemoteExecution();
	// session = remoteExec.createSession("192.168.0.25", "AppPerfect",
	// "appperfect", 22);
	// session.connect(StartupParameters.getSessionTimeout());
	// channelSftp = (ChannelSftp)session.openChannel("sftp");
	// channelSftp.connect();
	//
	// //Windows
	// remoteDirectory = remoteDirectory.replaceAll("\\\\", "/");
	// if( ! remoteDirectory.startsWith("/")) {
	// remoteDirectory = "/" + remoteDirectory;
	// }
	//
	// System.out.println("remoteDirectory : " + remoteDirectory);
	// String[] dirPathTokens = remoteDirectory.split("/");
	// String dirPath = dirPathTokens[0];
	// for(int i = 1; i < dirPathTokens.length; i ++){
	// dirPath += "/" + dirPathTokens[i];
	// System.out.println(dirPath);
	// try{
	// channelSftp.cd(dirPath);
	// }catch(Exception e){
	// e.printStackTrace();
	// channelSftp.mkdir(dirPath);
	// }
	// }
	// System.out.println("realPath: " + channelSftp.realpath(remoteDirectory));
	// System.out.println("Current dir : " + channelSftp.pwd());
	// channelSftp.cd(remoteDirectory);
	// System.out.println("Current dir after cd: " + channelSftp.pwd());
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// finally
	// {
	// if (channelSftp != null) {channelSftp.disconnect();}
	// }
	// }

}