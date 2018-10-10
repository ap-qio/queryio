package com.queryio.common.remote;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.service.remote.QueryIOResponse;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.PlatformHandler;
import com.queryio.common.util.ServiceUtilities;
import com.queryio.common.util.StaticUtilities;
import com.queryio.common.util.StreamPumper;
import com.queryio.proc.manager.ProcManager;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NodeOperation {

	private static String separator = File.separator;
	private static String exportConstant = PlatformHandler.isWindows() ? "set" : "export";

	public static QueryIOResponse formatNamenode(String installDir, String nodeId) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Formatting Namenode");

		StringBuilder cmd = new StringBuilder();
		// cmd.append("bin").append(separator);
		if (PlatformHandler.isWindows()) {
			cmd.append(installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "hdfs.cmd");
		} else {
			cmd.append("bin/hdfs");
		}
		cmd.append(" --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "etc").append(separator)
				.append("namenode-conf_").append(nodeId).append(separator)
				.append(" namenode -format -force -nonInteractive -clusterid queryio");
		return executeHadoopCommand(cmd.toString(), installDir, true, 0, false, true);
	}

	public static QueryIOResponse startNode(String installDir, String nodeType, String nodeId) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Starting " + nodeType);
		String cmd = null;
		if (nodeType.equals(QueryIOConstants.NAMENODE) || nodeType.equals(QueryIOConstants.DATANODE)
				|| nodeType.equals(QueryIOConstants.SECONDARYNAMENODE)
				|| nodeType.equals(QueryIOConstants.JOURNALNODE)) {
			if (PlatformHandler.isWindows()) {
				cmd = installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "hdfs.cmd"
						+ " --queryionodeid " + nodeId + " --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME
						+ separator + "etc" + separator + nodeType + "-conf_" + nodeId + " " + nodeType;
			} else {
				cmd = "sbin/hadoop-daemon.sh --config etc/" + nodeType + "-conf_" + nodeId + " "
						+ QueryIOConstants.START + " " + nodeType;
			}

			QueryIOResponse response = executeHadoopCommand(cmd, installDir, false, 0, false, true);
			if (response.isSuccessful()) {
				if (nodeType.equals(QueryIOConstants.NAMENODE))
					response.setResponseMsg(QueryIOConstants.NAMENODE_STARTED_SUCCESS);
				if (nodeType.equals(QueryIOConstants.DATANODE))
					response.setResponseMsg(QueryIOConstants.DATANODE_STARTED_SUCCESS);
				if (nodeType.equals(QueryIOConstants.SECONDARYNAMENODE))
					response.setResponseMsg(QueryIOConstants.SECONDARYNAMENODE_STARTED_SUCCESS);
				if (nodeType.equals(QueryIOConstants.JOURNALNODE))
					response.setResponseMsg(QueryIOConstants.JOURNALNODE_STARTED_SUCCESS);
			} else {
				String responseMsg = response.getResponseMsg();
				if (nodeType.equals(QueryIOConstants.NAMENODE))
					response.setResponseMsg(QueryIOConstants.NAMENODE_STARTED_FAILED + "\n" + responseMsg);
				if (nodeType.equals(QueryIOConstants.DATANODE))
					response.setResponseMsg(QueryIOConstants.DATANODE_STARTED_FAILED + "\n" + responseMsg);
				if (nodeType.equals(QueryIOConstants.SECONDARYNAMENODE))
					response.setResponseMsg(QueryIOConstants.SECONDARYNAMENODE_STARTED_FAILED + "\n" + responseMsg);
				if (nodeType.equals(QueryIOConstants.JOURNALNODE))
					response.setResponseMsg(QueryIOConstants.JOURNALNODE_STARTED_FAILED + "\n" + responseMsg);
			}
			return response;
		} else if (nodeType.equals(QueryIOConstants.RESOURCEMANAGER)) {
			if (PlatformHandler.isWindows()) {
				cmd = installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "yarn.cmd"
				// + " --queryionodeid " + nodeId
						+ " --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "etc" + separator
						+ nodeType + "-conf_" + nodeId + " " + nodeType;
			} else {
				cmd = "sbin/yarn-daemon.sh --config etc/" + nodeType + "-conf_" + nodeId + " " + QueryIOConstants.START
						+ " " + nodeType;
			}
			QueryIOResponse response = executeHadoopCommand(cmd, installDir, false, 0, false, true);
			if (response.isSuccessful()) {
				if (!PlatformHandler.isWindows()) {
					cmd = "sbin/mr-jobhistory-daemon.sh --config etc/resourcemanager-conf_" + nodeId
							+ " start historyserver"; // Windows
														// version
														// don't
														// have
														// historyServer
					response = executeHadoopCommand(cmd, installDir, false, 0, false, true);
				}
				if (response.isSuccessful()) {
					if (nodeType.equals(QueryIOConstants.RESOURCEMANAGER))
						response.setResponseMsg(QueryIOConstants.RESOURCEMANAGER_STARTED_SUCCESS);
				} else {
					String responseMsg = response.getResponseMsg();
					if (nodeType.equals(QueryIOConstants.RESOURCEMANAGER))
						response.setResponseMsg(QueryIOConstants.RESOURCEMANAGER_STARTED_FAILED + "\n" + responseMsg);
				}
			} else {
				String responseMsg = response.getResponseMsg();
				if (nodeType.equals(QueryIOConstants.RESOURCEMANAGER))
					response.setResponseMsg(QueryIOConstants.RESOURCEMANAGER_STARTED_FAILED + "\n" + responseMsg);
			}
			return response;
		} else {
			if (PlatformHandler.isWindows()) {
				cmd = installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "yarn.cmd"
				// + " --queryionodeid " + nodeId
						+ " --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "etc" + separator
						+ nodeType + "-conf_" + nodeId + " " + nodeType;
			} else {
				cmd = "sbin/yarn-daemon.sh --config etc/" + nodeType + "-conf_" + nodeId + " " + QueryIOConstants.START
						+ " " + nodeType;
			}
			QueryIOResponse response = executeHadoopCommand(cmd, installDir, false, 0, false, true);
			if (response.isSuccessful()) {
				if (nodeType.equals(QueryIOConstants.NODEMANAGER))
					response.setResponseMsg(QueryIOConstants.NODEMANAGER_STARTED_SUCCESS);
			} else {
				String responseMsg = response.getResponseMsg();
				if (nodeType.equals(QueryIOConstants.NODEMANAGER))
					response.setResponseMsg(QueryIOConstants.NODEMANAGER_STARTED_FAILED + "\n" + responseMsg);
			}
			return response;
		}
	}

	private static String getNodeClass(String nodeType) {
		if (nodeType.equals(QueryIOConstants.NAMENODE)) {
			return "org.apache.hadoop.hdfs.server.namenode.NameNode";
		}
		if (nodeType.equals(QueryIOConstants.DATANODE)) {
			return "org.apache.hadoop.hdfs.server.datanode.DataNode";
		}
		if (nodeType.equals(QueryIOConstants.SECONDARYNAMENODE)) {
			return "org.apache.hadoop.hdfs.server.namenode.SecondaryNameNode";
		}
		if (nodeType.equals(QueryIOConstants.JOURNALNODE)) {
			return "org.apache.hadoop.hdfs.qjournal.server.JournalNode";
		}
		if (nodeType.equals(QueryIOConstants.RESOURCEMANAGER)) {
			return "org.apache.hadoop.yarn.server.resourcemanager.ResourceManager";
		}
		if (nodeType.equals(QueryIOConstants.NODEMANAGER)) {
			return "org.apache.hadoop.yarn.server.nodemanager.NodeManager";
		}
		if (nodeType.equals("historyserver")) {
			return "org.apache.hadoop.mapreduce.v2.hs.JobHistoryServer";
		}

		return null;
	}

	public static QueryIOResponse stopNode(String installDir, String nodeType, String nodeId) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Stopping " + nodeType);
		if (nodeType.equals(QueryIOConstants.NAMENODE) || nodeType.equals(QueryIOConstants.DATANODE)
				|| nodeType.equals(QueryIOConstants.SECONDARYNAMENODE)
				|| nodeType.equals(QueryIOConstants.JOURNALNODE)) {
			QueryIOResponse response = new QueryIOResponse(false, "");
			if (PlatformHandler.isWindows()) {
				boolean isSuccess = killProcessWindows(getNodeClass(nodeType));
				response.setSuccessful(isSuccess);
			} else {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Unix based platform.");
				String cmd = "sbin/hadoop-daemon.sh --config etc/" + nodeType + "-conf_" + nodeId + " "
						+ QueryIOConstants.STOP + " " + nodeType;
				response = executeHadoopCommand(cmd, installDir, false, 0, false, true);
			}
			if (response.isSuccessful()) {
				if (nodeType.equals(QueryIOConstants.NAMENODE))
					response.setResponseMsg(QueryIOConstants.NAMENODE_STOPPED_SUCCESS);
				if (nodeType.equals(QueryIOConstants.DATANODE))
					response.setResponseMsg(QueryIOConstants.DATANODE_STOPPED_SUCCESS);
				if (nodeType.equals(QueryIOConstants.SECONDARYNAMENODE))
					response.setResponseMsg(QueryIOConstants.SECONDARYNAMENODE_STOPPED_SUCCESS);
				if (nodeType.equals(QueryIOConstants.JOURNALNODE))
					response.setResponseMsg(QueryIOConstants.JOURNALNODE_STOPPED_SUCCESS);
			} else {
				String responseMsg = response.getResponseMsg();
				if (nodeType.equals(QueryIOConstants.NAMENODE))
					response.setResponseMsg(QueryIOConstants.NAMENODE_STOPPED_FAILED + "\n" + responseMsg);
				if (nodeType.equals(QueryIOConstants.DATANODE))
					response.setResponseMsg(QueryIOConstants.DATANODE_STOPPED_FAILED + "\n" + responseMsg);
				if (nodeType.equals(QueryIOConstants.SECONDARYNAMENODE))
					response.setResponseMsg(QueryIOConstants.SECONDARYNAMENODE_STOPPED_FAILED + "\n" + responseMsg);
				if (nodeType.equals(QueryIOConstants.JOURNALNODE))
					response.setResponseMsg(QueryIOConstants.JOURNALNODE_STOPPED_FAILED + "\n" + responseMsg);
			}
			return response;
		} else if (nodeType.equals(QueryIOConstants.RESOURCEMANAGER)) {
			QueryIOResponse response = new QueryIOResponse(false, "");
			if (PlatformHandler.isWindows()) {
				boolean isSuccess = killProcessWindows(getNodeClass(nodeType));
				response.setSuccessful(isSuccess);
			} else {
				String cmd = "sbin/yarn-daemon.sh --config etc/" + nodeType + "-conf_" + nodeId + " "
						+ QueryIOConstants.STOP + " " + nodeType;
				response = executeHadoopCommand(cmd, installDir, false, 0, false, true);
			}

			if (!PlatformHandler.isWindows()) {
				String cmd = "sbin/mr-jobhistory-daemon.sh --config etc/resourcemanager-conf_" + nodeId
						+ " stop historyserver"; // Windows
													// version
													// don't
													// have
													// historyServer
				response = executeHadoopCommand(cmd, installDir, false, 0, false, true);
			}

			if (response.isSuccessful()) {
				if (nodeType.equals(QueryIOConstants.RESOURCEMANAGER))
					response.setResponseMsg(QueryIOConstants.RESOURCEMANAGER_STOPPED_SUCCESS);
			} else {
				String responseMsg = response.getResponseMsg();
				if (nodeType.equals(QueryIOConstants.RESOURCEMANAGER))
					response.setResponseMsg(QueryIOConstants.RESOURCEMANAGER_STOPPED_FAILED + "\n" + responseMsg);
			}
			return response;
		} else {
			QueryIOResponse response = new QueryIOResponse(false, "");
			if (PlatformHandler.isWindows()) {
				boolean isSuccess = killProcessWindows(getNodeClass(nodeType));
				response.setSuccessful(isSuccess);
			} else {
				String cmd = "sbin/yarn-daemon.sh --config etc/" + nodeType + "-conf_" + nodeId + " "
						+ QueryIOConstants.STOP + " " + nodeType;
				response = executeHadoopCommand(cmd, installDir, false, 0, false, true);
			}

			if (response.isSuccessful()) {
				if (nodeType.equals(QueryIOConstants.NODEMANAGER))
					response.setResponseMsg(QueryIOConstants.NODEMANAGER_STOPPED_SUCCESS);
			} else {
				String responseMsg = response.getResponseMsg();
				if (nodeType.equals(QueryIOConstants.NODEMANAGER))
					response.setResponseMsg(QueryIOConstants.NODEMANAGER_STOPPED_FAILED + "\n" + responseMsg);
			}
			return response;
		}
	}

	public static QueryIOResponse performBootstrapStandby(String installDir, String nodeId) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("performing bootstrapStandby.");
		String cmd = null;
		if (PlatformHandler.isWindows()) {
			cmd = installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "hdfs.cmd"
					+ " --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "etc" + separator
					+ "namenode-conf_" + nodeId + " namenode -bootstrapStandby";
		} else {
			cmd = "bin/hdfs --config etc/namenode-conf_" + nodeId + " namenode -bootstrapStandby";
		}

		return executeHadoopCommand(cmd, installDir, true, 0, false, true);
	}

	public static QueryIOResponse initializeSharedEdits(String installDir, String nodeId) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("initializeSharedEdits.");
		String cmd = null;
		if (PlatformHandler.isWindows()) {
			cmd = installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "hdfs.cmd"
					+ " --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "etc" + separator
					+ "namenode-conf_" + nodeId + " namenode -initializeSharedEdits";
		} else {
			cmd = "bin/hdfs --config etc/namenode-conf_" + nodeId + " namenode -initializeSharedEdits";
		}

		return executeHadoopCommand(cmd, installDir, true, 0, false, true);
	}

	public static QueryIOResponse transitionNodeToActive(String installDir, String nodeId, String activeNode) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("performing transitionToActive.");
		String cmd = null;
		if (PlatformHandler.isWindows()) {
			cmd = installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "hdfs.cmd"
					+ " --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "etc" + separator
					+ "namenode-conf_" + nodeId + " haadmin -transitionToActive " + activeNode;
		} else {
			cmd = "bin/hdfs --config etc/namenode-conf_" + nodeId + " haadmin -transitionToActive " + activeNode;
		}

		return executeHadoopCommand(cmd, installDir, true, 0, false, true);
	}

	public static QueryIOResponse runFSCKCommand(String installDir, String nodeId) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Running fsck command.");
		String cmd = null;
		if (PlatformHandler.isWindows()) {
			cmd = installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "hdfs.cmd"
					+ " --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "etc" + separator
					+ "namenode-conf_" + nodeId + " fsck / -files -blocks -racks";
		} else {
			cmd = "bin/hdfs --config etc/" + QueryIOConstants.NAMENODE + "-conf_" + nodeId
					+ "/ fsck / -files -blocks -racks";
		}

		return executeHadoopCommand(cmd, installDir, false, 0, true, true);
	}

	public static QueryIOResponse startBalancer(String installDir, String nodeId) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Running Balancer.");
		String cmd = null;
		if (PlatformHandler.isWindows()) {
			cmd = installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "hdfs.cmd"
					+ " --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "etc" + separator
					+ "namenode-conf_" + nodeId + " balancer";
		} else {
			cmd = "bin/hdfs --config etc/" + QueryIOConstants.NAMENODE + "-conf_" + nodeId + " balancer";// -D"
																											// +
																											// DFSConfigKeys.DFS_NAMESERVICES
																											// +
																											// "=";
		}

		return executeHadoopCommand(cmd, installDir, false, 0, true, true);
	}

	public static QueryIOResponse refreshNodes(String installDir, String nodeId) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Refreshing Nodes.");
		String cmd = null;
		if (PlatformHandler.isWindows()) {
			cmd = installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "hdfs.cmd"
					+ " --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "etc" + separator
					+ QueryIOConstants.NAMENODE + "-conf_" + nodeId + separator + " dfsadmin -refreshNodes";
		} else {
			cmd = "bin/hdfs --config etc/" + QueryIOConstants.NAMENODE + "-conf_" + nodeId + "/ dfsadmin -refreshNodes";
		}

		return executeHadoopCommand(cmd, installDir, false, 0, false, true);
	}

	public static QueryIOResponse setSafemode(String installDir, String nodeId, boolean safemode) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Setting safemode to " + (safemode ? "ON" : "OFF") + ".");
		String cmd = null;
		if (PlatformHandler.isWindows()) {
			cmd = installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "hdfs.cmd"
					+ " --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "etc" + separator
					+ "namenode-conf_" + nodeId + "/ dfsadmin -safemode " + (safemode ? "enter" : "leave");
		} else {
			cmd = "bin/hdfs --config etc/" + QueryIOConstants.NAMENODE + "-conf_" + nodeId + "/ dfsadmin -safemode "
					+ (safemode ? "enter" : "leave");
		}

		return executeHadoopCommand(cmd, installDir, false, 0, false, true);
	}

	public static QueryIOResponse performFailover(String installDir, String nodeId, String failoverArg) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Performing failover. Failover args: " + failoverArg);
		String cmd = null;
		if (PlatformHandler.isWindows()) {
			cmd = installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "hdfs.cmd"
					+ " --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "etc" + separator
					+ "namenode-conf_" + nodeId + "/ haadmin -failover " + failoverArg;
		} else {
			cmd = "bin/hdfs --config etc/" + QueryIOConstants.NAMENODE + "-conf_" + nodeId + "/ haadmin -failover "
					+ failoverArg;
		}

		return executeHadoopCommand(cmd, installDir, false, 0, true, true);
	}

	public static QueryIOResponse executeHadoopCommand(String cmd, String installDir, final boolean unblock,
			int exitCode, boolean getOutput, boolean appendHadoopDir) {
		AppLogger.getLogger().info("Entered executeHadoopCommand");
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug(cmd);
		Process process = null;
		StringWriter errorWriter = null;
		StringWriter inputWriter = null;
		try {
			AppLogger.getLogger().info("Entered executeHadoopCommand: Starting process");
			if (appendHadoopDir)
				process = Runtime.getRuntime().exec(cmd, null, new File(installDir + QueryIOConstants.HADOOP_DIR_NAME));
			else {
				process = Runtime.getRuntime().exec(cmd, null, new File(installDir));
			}
			AppLogger.getLogger().info("Entered executeHadoopCommand: Started process");
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
			AppLogger.getLogger().info("Entered executeHadoopCommand: Started streams");

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
			AppLogger.getLogger().info("Entered executeHadoopCommand: Going to wait now");

			process.waitFor();
			AppLogger.getLogger().info("Entered executeHadoopCommand: Wait over");
			// Thread.sleep(10000); // TODO: Windows temp handling. Uncomment
			// process.waitFor() later.
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
				AppLogger.getLogger().debug("targetProcess.exitValue()" + process.exitValue()); // TODO:
																								// Windows
																								// temp
																								// handling.
																								// Uncomment
																								// this
																								// later.

			if (process.exitValue() != exitCode || errorWriter.toString().contains("Exception")
					|| inputWriter.toString().contains("Exception"))
				if (process.exitValue() != exitCode) // TODO: Windows temp
														// handling.
				{
					String msg = errorWriter.toString();
					if (msg.isEmpty())
						msg = inputWriter.toString();
					return new QueryIOResponse(false, "Operation failed with following error.\n"
							+ (msg.indexOf("FATAL") != -1 ? msg.substring(msg.indexOf("FATAL")) : msg));
				}
			if (getOutput)
				return new QueryIOResponse(true,
						QueryIOConstants.NODE_OPERATION_SUCCESS + "\n" + inputWriter.toString());
			else
				return new QueryIOResponse(true, QueryIOConstants.NODE_OPERATION_SUCCESS);
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

	public static QueryIOResponse formatDirectory(String installDir, String dirPath) {
		StringWriter inputWriter = null;
		StringWriter errorWriter = null;
		try {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Formatting Datanode");
			File file = new File(dirPath);
			ServiceUtilities.deleteFile(file);
			file.mkdirs();
			return new QueryIOResponse(true, QueryIOConstants.NODE_FORMAT_SUCCESS);

		} catch (final Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			return new QueryIOResponse(false, ex.getMessage());
		} finally {
			try {
				if (errorWriter != null)
					errorWriter.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
			try {
				if (inputWriter != null)
					inputWriter.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
		}
	}

	public static QueryIOResponse updateHadoopEnv(String installDir, String nodeType, String nodeId) {
		String sourceFileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf_" + nodeId
				+ "/core-site.xml";
		String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf_" + nodeId;
		if (PlatformHandler.isWindows()) {
			fileName += "/hadoop-env.cmd";
		} else {
			fileName += "/hadoop-env.sh";
		}
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug(fileName);
		FileInputStream sourceIs = null;
		FileOutputStream fos = null;
		BufferedReader br = null;
		DataInputStream in = null;
		try {

			sourceIs = new FileInputStream(new File(sourceFileName));
			Configuration conf = new Configuration(false);
			conf.addResource(sourceIs);

			FileInputStream fstream = new FileInputStream(fileName);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			StringBuffer sb = new StringBuffer();
			boolean a = false, b = false, c = false, d = false, e = false, f = false, g = false, h = false;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if (!a && strLine.startsWith(exportConstant + " HADOOP_OPTS")) {
					updateConf(conf.get(QueryIOConstants.HADOOP_OPTS_KEY), "HADOOP_OPTS", strLine, sb, false);
					a = true;
				} else if (!b && strLine.startsWith(exportConstant + " HADOOP_LOG_DIR")) {
					updateConf(conf.get(QueryIOConstants.HADOOP_LOG_DIR_KEY), "HADOOP_LOG_DIR", strLine, sb, true);
					b = true;
				} else if (!c && strLine.startsWith(exportConstant + " HADOOP_PID_DIR")) {
					updateConf(conf.get(QueryIOConstants.HADOOP_PID_DIR_KEY), "HADOOP_PID_DIR", strLine, sb, false);
					c = true;
				} else if (!d && strLine.startsWith(exportConstant + " HADOOP_HEAPSIZE")) {
					updateConf(conf.get(QueryIOConstants.HADOOP_HEAP_SIZE), "HADOOP_HEAPSIZE", strLine, sb, false);
					d = true;
				} else if (!e && strLine.startsWith(exportConstant + " HADOOP_NAMENODE_OPTS")) {
					updateConf(conf.get(QueryIOConstants.NAMENODE_OPTS_KEY), "HADOOP_NAMENODE_OPTS", strLine, sb,
							false);
					e = true;
				} else if (!f && strLine.startsWith(exportConstant + " HADOOP_DATANODE_OPTS")) {
					updateConf(conf.get(QueryIOConstants.DATANODE_OPTS_KEY), "HADOOP_DATANODE_OPTS", strLine, sb,
							false);
					f = true;
				} else if (!g && strLine.startsWith(exportConstant + " HADOOP_SECONDARYNAMENODE_OPTS")) {
					updateConf(conf.get(QueryIOConstants.SECONDARYNAMENODE_OPTS_KEY), "HADOOP_SECONDARYNAMENODE_OPTS",
							strLine, sb, false);
					g = true;
				} else if (!h && strLine.startsWith(exportConstant + " HADOOP_JOURNALNAMENODE_OPTS")) {
					updateConf(conf.get(QueryIOConstants.JOURNALNODE_OPTS_KEY), "HADOOP_JOURNALNAMENODE_OPTS", strLine,
							sb, false);
					h = true;
				} else {
					sb.append(strLine);
					sb.append("\n");
				}
			}

			if (!a) {
				updateConf(conf.get(QueryIOConstants.HADOOP_OPTS_KEY), "HADOOP_OPTS", sb, false);
			}
			if (!b) {
				updateConf(conf.get(QueryIOConstants.HADOOP_LOG_DIR_KEY), "HADOOP_LOG_DIR", sb, true);
			}
			if (!c) {
				updateConf(conf.get(QueryIOConstants.HADOOP_PID_DIR_KEY), "HADOOP_PID_DIR", sb, false);
			}
			if (!d) {
				updateConf(conf.get(QueryIOConstants.HADOOP_HEAP_SIZE), "HADOOP_HEAPSIZE", sb, false);
			}
			if (!e) {
				updateConf(conf.get(QueryIOConstants.NAMENODE_OPTS_KEY), "HADOOP_NAMENODE_OPTS", sb, false);
			}
			if (!f) {
				updateConf(conf.get(QueryIOConstants.DATANODE_OPTS_KEY), "HADOOP_DATANODE_OPTS", sb, false);
			}
			if (!g) {
				updateConf(conf.get(QueryIOConstants.SECONDARYNAMENODE_OPTS_KEY), "HADOOP_SECONDARYNAMENODE_OPTS", sb,
						false);
			}
			if (!h) {
				updateConf(conf.get(QueryIOConstants.JOURNALNODE_OPTS_KEY), "HADOOP_JOURNALNAMENODE_OPTS", sb, false);
			}

			fos = new FileOutputStream(new File(fileName));
			fos.write(sb.toString().getBytes());

			return new QueryIOResponse(true, QueryIOConstants.CONFIGURATION_UPDATE_SUCCESS);
		} catch (Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			return new QueryIOResponse(false, QueryIOConstants.CONFIGURATION_UPDATE_FAILED + "\n" + ex.getMessage());
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
			try {
				if (sourceIs != null) {
					sourceIs.close();
				}
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
		}
	}

	private static void updateConf(String val, String key, StringBuffer sb, boolean mkdir) {
		updateConf(val, key, null, sb, mkdir);
	}

	private static void updateConf(String val, String key, String strLine, StringBuffer sb, boolean mkdir) {
		if (val != null && val.length() != 0) {
			if (mkdir) {
				File file = new File(val);
				if (!file.exists()) {
					file.mkdirs();
				}
			}
			sb.append(exportConstant);
			sb.append(" ");
			sb.append(key);
			sb.append("=");
			if (PlatformHandler.isWindows()) {
				sb.append(val);
			} else {
				sb.append("\"");
				sb.append(val);
				sb.append("\"");
			}
			sb.append("\n");
		} else if (strLine != null) {
			sb.append(strLine);
			sb.append("\n");
		}
	}

	public static QueryIOResponse updateYarnEnv(String installDir, String nodeType, String nodeId) {
		String sourceFileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf_" + nodeId
				+ "/yarn-site.xml";
		String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf_" + nodeId;
		if (PlatformHandler.isWindows()) {
			fileName += "/yarn-env.cmd";
		} else {
			fileName += "/yarn-env.sh";
		}

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug(fileName);
		FileInputStream sourceIs = null;
		FileOutputStream fos = null;
		BufferedReader br = null;
		DataInputStream in = null;
		try {

			sourceIs = new FileInputStream(new File(sourceFileName));
			Configuration conf = new Configuration(false);
			conf.addResource(sourceIs);

			FileInputStream fstream = new FileInputStream(fileName);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			StringBuffer sb = new StringBuffer();
			StringBuffer originalSB = new StringBuffer();
			boolean b1 = false, b2 = false, b3 = false, b4 = false, b5 = false, b6 = false, b7 = false, b8 = false,
					b9 = false, b10 = false;
			// Read File Line By Line
			String val;
			while ((strLine = br.readLine()) != null) {
				if (!b1 && strLine.startsWith(exportConstant + " YARN_LOG_DIR")) {
					val = conf.get(QueryIOConstants.YARN_LOG_DIR_KEY);
					if (val != null && val.length() != 0) {
						File file = new File(val);
						if (!file.exists()) {
							file.mkdirs();
						}
						sb.append(exportConstant + " YARN_LOG_DIR=" + val);
						sb.append("\n");
					} else {
						originalSB.append(strLine);
						originalSB.append("\n");
					}
					b1 = true;
				} else if (!b2 && strLine.startsWith(exportConstant + " YARN_HEAPSIZE")) {
					val = conf.get(QueryIOConstants.YARN_HEAP_SIZE);
					if (val != null && val.length() != 0) {
						sb.append(exportConstant + " YARN_HEAPSIZE=" + val);
						sb.append("\n");
					} else {
						originalSB.append(strLine);
						originalSB.append("\n");
					}
					b2 = true;
				} else if (!b3 && strLine.startsWith(exportConstant + " YARN_NODEMANAGER_OPTS")) {
					val = conf.get(QueryIOConstants.NODEMANAGER_OPTS_KEY);
					if (val != null && val.length() != 0) {
						sb.append(exportConstant + " YARN_NODEMANAGER_OPTS=\"" + val + "\"");
						sb.append("\n");
					} else {
						originalSB.append(strLine);
						originalSB.append("\n");
					}
					b3 = true;
				} else if (!b4 && strLine.startsWith(exportConstant + " YARN_RESOURCEMANAGER_OPTS")) {
					val = conf.get(QueryIOConstants.RESOURCEMANAGER_OPTS_KEY);
					if (val != null && val.length() != 0) {
						sb.append(exportConstant + " YARN_RESOURCEMANAGER_OPTS=\"" + val + "\"");
						sb.append("\n");
					} else {
						originalSB.append(strLine);
						originalSB.append("\n");
					}
					b4 = true;
				} else if (!b5 && strLine.startsWith(exportConstant + " YARN_PID_DIR")) {
					val = conf.get(QueryIOConstants.YARN_PID_DIR_KEY);
					if (val != null && val.length() != 0) {
						File file = new File(val);
						if (!file.exists()) {
							file.mkdirs();
						}
						sb.append(exportConstant + " YARN_PID_DIR=" + val);
						sb.append("\n");
					} else {
						originalSB.append(strLine);
						originalSB.append("\n");
					}
					b5 = true;
				} else if (!b6 && strLine.startsWith(exportConstant + " HADOOP_MAPRED_HOME")) {
					sb.append(exportConstant + " HADOOP_MAPRED_HOME=" + installDir + QueryIOConstants.HADOOP_DIR_NAME);
					sb.append("\n");
					b6 = true;
				} else if (!b7 && strLine.startsWith(exportConstant + " HADOOP_COMMON_HOME")) {
					sb.append(exportConstant + " HADOOP_COMMON_HOME=" + installDir + QueryIOConstants.HADOOP_DIR_NAME);
					sb.append("\n");
					b7 = true;
				} else if (!b8 && strLine.startsWith(exportConstant + " HADOOP_HDFS_HOME")) {
					sb.append(exportConstant + " HADOOP_HDFS_HOME=" + installDir + QueryIOConstants.HADOOP_DIR_NAME);
					sb.append("\n");
					b8 = true;
				} else if (!b9 && strLine.startsWith(exportConstant + " HADOOP_YARN_HOME")) {
					sb.append(exportConstant + " HADOOP_YARN_HOME=" + installDir + QueryIOConstants.HADOOP_DIR_NAME);
					sb.append("\n");
					b9 = true;
				} else if (!b10 && strLine.startsWith(exportConstant + " YARN_OPTS")) {
					val = conf.get(QueryIOConstants.YARN_OPTS_KEY);
					if (val != null && val.length() != 0) {
						sb.append(exportConstant + " YARN_OPTS=\"" + val + "\"");
						sb.append("\n");
					} else {
						originalSB.append(strLine);
						originalSB.append("\n");
					}
					b10 = true;
				} else {
					originalSB.append(strLine);
					originalSB.append("\n");
				}

			}

			if (!b1) {
				val = conf.get(QueryIOConstants.YARN_LOG_DIR_KEY);
				if (val != null && val.length() != 0) {
					File file = new File(val);
					if (!file.exists()) {
						file.mkdirs();
					}
					sb.append(exportConstant + " YARN_LOG_DIR=" + val);
					sb.append("\n");
				}
			}
			if (!b2) {
				val = conf.get(QueryIOConstants.YARN_HEAP_SIZE);
				if (val != null && val.length() != 0) {
					sb.append(exportConstant + " YARN_HEAPSIZE=" + val);
					sb.append("\n");
				}
			}
			if (!b3) {
				val = conf.get(QueryIOConstants.NODEMANAGER_OPTS_KEY);
				if (val != null && val.length() != 0) {
					sb.append(exportConstant + " YARN_NODEMANAGER_OPTS=\"" + val + "\"");
					sb.append("\n");
				}
			}
			if (!b4) {
				val = conf.get(QueryIOConstants.RESOURCEMANAGER_OPTS_KEY);
				if (val != null && val.length() != 0) {
					sb.append(exportConstant + " YARN_RESOURCEMANAGER_OPTS=\"" + val + "\"");
					sb.append("\n");
				}
			}
			if (!b5) {
				val = conf.get(QueryIOConstants.YARN_PID_DIR_KEY);
				if (val != null && val.length() != 0) {
					File file = new File(val);
					if (!file.exists()) {
						file.mkdirs();
					}
					sb.append(exportConstant + " YARN_PID_DIR=" + val);
					sb.append("\n");
				}
			}
			if (!b6) {
				sb.append(exportConstant + " HADOOP_MAPRED_HOME=" + installDir + QueryIOConstants.HADOOP_DIR_NAME);
				sb.append("\n");
			}
			if (!b7) {
				sb.append(exportConstant + " HADOOP_COMMON_HOME=" + installDir + QueryIOConstants.HADOOP_DIR_NAME);
				sb.append("\n");
			}
			if (!b8) {
				sb.append(exportConstant + " HADOOP_HDFS_HOME=" + installDir + QueryIOConstants.HADOOP_DIR_NAME);
				sb.append("\n");
			}
			if (!b9) {
				sb.append(exportConstant + " HADOOP_YARN_HOME=" + installDir + QueryIOConstants.HADOOP_DIR_NAME);
				sb.append("\n");
			}
			if (!b10) {
				val = conf.get(QueryIOConstants.YARN_OPTS_KEY);
				if (val != null && val.length() != 0) {
					sb.append(exportConstant + " YARN_OPTS=\"" + val + "\"");
					sb.append("\n");
				}
			}
			sb.append("\n");

			String str = sb.toString() + originalSB.toString();

			fos = new FileOutputStream(new File(fileName));
			fos.write(str.getBytes());

			String fileName2 = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf_" + nodeId
					+ "/mapred-env.sh";
			fstream = new FileInputStream(fileName2);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			sb = new StringBuffer();
			originalSB = new StringBuffer();
			b1 = false;
			b2 = false;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if (!b1 && strLine.startsWith(exportConstant + " HADOOP_MAPRED_LOG_DIR")) {
					val = conf.get(QueryIOConstants.YARN_LOG_DIR_KEY);
					if (val != null && val.length() != 0) {
						File file = new File(val);
						if (!file.exists()) {
							file.mkdirs();
						}
						sb.append(exportConstant + " HADOOP_MAPRED_LOG_DIR=" + val);
						sb.append("\n");
					} else {
						originalSB.append(strLine);
						originalSB.append("\n");
					}
					b1 = true;
				} else if (!b2 && strLine.startsWith(exportConstant + " HADOOP_MAPRED_PID_DIR")) {
					val = conf.get(QueryIOConstants.YARN_PID_DIR_KEY);
					if (val != null && val.length() != 0) {
						sb.append(exportConstant + " HADOOP_MAPRED_PID_DIR=" + val);
						sb.append("\n");
					} else {
						originalSB.append(strLine);
						originalSB.append("\n");
					}
					b2 = true;
				} else {
					originalSB.append(strLine);
					originalSB.append("\n");
				}

			}

			if (!b1) {
				val = conf.get(QueryIOConstants.YARN_LOG_DIR_KEY);
				if (val != null && val.length() != 0) {
					File file = new File(val);
					if (!file.exists()) {
						file.mkdirs();
					}
					sb.append(exportConstant + " HADOOP_MAPRED_LOG_DIR=" + val);
					sb.append("\n");
				}
			}
			if (!b2) {
				val = conf.get(QueryIOConstants.YARN_PID_DIR_KEY);
				if (val != null && val.length() != 0) {
					sb.append(exportConstant + " HADOOP_MAPRED_PID_DIR=" + val);
					sb.append("\n");
				}
			}

			sb.append("\n");

			str = sb.toString() + originalSB.toString();

			fos = new FileOutputStream(new File(fileName2));
			fos.write(str.getBytes());

			return new QueryIOResponse(true, QueryIOConstants.CONFIGURATION_UPDATE_SUCCESS);
		} catch (Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			return new QueryIOResponse(false, QueryIOConstants.CONFIGURATION_UPDATE_FAILED + "\n" + ex.getMessage());
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
			try {
				if (sourceIs != null) {
					sourceIs.close();
				}
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
		}
	}

	public static QueryIOResponse copyEditsDirToSharedDir(String installDir, String nodeId) {
		Configuration conf = new Configuration(false);
		String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/namenode-conf_" + nodeId
				+ "/hdfs-site.xml";

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(fileName));
			conf.addResource(fis);
			String editsDirPath = conf.get(DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY);
			if (editsDirPath.indexOf(",") != -1) {
				editsDirPath = editsDirPath.split(",")[0];
			}
			if (editsDirPath.startsWith("file://"))
				editsDirPath = editsDirPath.substring(7);
			String sharedEditsDirPath = conf.get(DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY);
			if (sharedEditsDirPath.indexOf(",") != -1) {
				sharedEditsDirPath = sharedEditsDirPath.split(",")[0];
			}
			if (sharedEditsDirPath.startsWith("file://"))
				sharedEditsDirPath = sharedEditsDirPath.substring(7);
			File file = new File(sharedEditsDirPath);
			StaticUtilities.deleteFile(file);

			copyFolder(new File(editsDirPath), new File(sharedEditsDirPath), true);

			return new QueryIOResponse(true, null);
		} catch (Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			return new QueryIOResponse(false, "Copying edits dir to shared dir failed due to: " + ex.getMessage());
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
		}
	}

	public static QueryIOResponse copySharedDirLogstoEditsLogs(String installDir, String nodeId) {
		Configuration conf = new Configuration(false);
		String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/namenode-conf_" + nodeId
				+ "/hdfs-site.xml";

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(fileName));
			conf.addResource(fis);
			String editsDirPath = conf.get(DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY);
			if (editsDirPath.indexOf(",") != -1) {
				editsDirPath = editsDirPath.split(",")[0];
			}
			if (editsDirPath.startsWith("file://"))
				editsDirPath = editsDirPath.substring(7);
			String sharedEditsDirPath = conf.get(DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY);
			if (sharedEditsDirPath.indexOf(",") != -1) {
				sharedEditsDirPath = sharedEditsDirPath.split(",")[0];
			}
			if (sharedEditsDirPath.startsWith("file://"))
				sharedEditsDirPath = sharedEditsDirPath.substring(7);

			copyFolder(new File(sharedEditsDirPath), new File(editsDirPath), false);

			return new QueryIOResponse(true, null);
		} catch (Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			return new QueryIOResponse(false, "Copying edits dir to shared dir failed due to: " + ex.getMessage());
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
		}
	}

	public static void copyFolder(File src, File dest, boolean overWrite) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdirs();
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile, overWrite);
			}

		} else {
			if (!overWrite) {
				if (dest.exists())
					return;
			}
			InputStream in = null;
			OutputStream out = null;
			try {
				// if file, then copy it
				// Use bytes stream to support all file types
				in = new FileInputStream(src);
				out = new FileOutputStream(dest);

				byte[] buffer = new byte[1024];

				int length;
				// copy the file content in bytes
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Exception e) {

					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (Exception e) {

					}
				}
			}
		}
	}

	public static QueryIOResponse updateNetworkConfig(String installDir, String nodeId, String[] hostIps,
			String[] rackNames) {
		StringBuffer sb = null;
		if (PlatformHandler.isWindows()) {
			sb = updateNetworkConfigWindows(hostIps, rackNames);
		} else {
			sb = updateNetworkConfigUnixBased(hostIps, rackNames);
		}

		String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + File.separator + "etc" + File.separator
				+ QueryIOConstants.NAMENODE + "-conf_" + nodeId + File.separator;
		if (PlatformHandler.isWindows()) {
			fileName += "topologyConfig.bat";
		} else {
			fileName += "topologyConfig.sh";
		}
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new FileWriter(fileName, false));
			output.write(sb.toString());
			output.newLine();
			return new QueryIOResponse(true, null);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching status of name node", e);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing Writer", e);
			}
		}
		return new QueryIOResponse(false, null);
	}

	private static StringBuffer updateNetworkConfigUnixBased(String[] hostIps, String[] rackNames) {
		StringBuffer sb = new StringBuffer("#topologyConfig.sh\n#Returns rack of datanode\n");

		if (hostIps == null || hostIps.length == 0) {
			sb.append("echo \"/default-rack\"\n");
		} else {
			boolean flag = true;
			for (int i = 0; i < hostIps.length; i++) {
				if (!flag) {
					sb.append("elif ");
				} else {
					sb.append("if ");
					flag = false;
				}
				sb.append("[ $1 == " + hostIps[i] + " ]");
				sb.append("; then\n");
				sb.append("\techo \"" + rackNames[i] + "\"\n");
			}
			sb.append("else\n");
			sb.append("\techo \"/default-rack\"\n");
			sb.append("fi\n");
		}
		return sb;
	}

	private static StringBuffer updateNetworkConfigWindows(String[] hostIps, String[] rackNames) {
		StringBuffer sb = new StringBuffer("@echo off\nREM topologyConfig.bat\nREM Returns rack of datanode\n");

		if (hostIps == null || hostIps.length == 0) {
			sb.append("echo /default-rack\n");
		} else {
			for (int i = 0; i < hostIps.length; i++) {
				if (i == 0) {
					sb.append("if ");
				} else {
					sb.append(" else if ");
				}
				sb.append(" %1 == ").append(hostIps[i]).append(" ( \n");
				sb.append("\techo ").append(rackNames[i]).append("\n").append(")");
			}
			sb.append(" else (\n");
			sb.append("\techo \"/default-rack\"\n");
			sb.append(")");
		}
		return sb;
	}

	public static QueryIOResponse clearConfiguration(String installDir, String nodeType, String nodeId) {
		String dirName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf_" + nodeId;
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("clearing config for node: " + nodeId);
		try {

			File configDir = new File(dirName);
			if (configDir.exists()) {
				FileUtil.fullyDelete(configDir);
			}
			copyFolder(new File(installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf"),
					configDir, true);
			if (nodeType.equals(QueryIOConstants.NAMENODE)) {
				File topologyConfigFile = new File(installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType
						+ "-conf_" + nodeId + "/topologyConfig.sh");
				topologyConfigFile.setExecutable(true);
				topologyConfigFile.setReadable(true);
				topologyConfigFile.setWritable(true);
			}
			return new QueryIOResponse(true, "Configuration cleared successfully.");
		} catch (Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			return new QueryIOResponse(false, ex.getMessage());
		}
	}

	public static QueryIOResponse updateConfiguration(String installDir, String nodeType, String nodeId,
			String configFileName, ArrayList property, ArrayList value, boolean refresh) {
		Configuration conf = new Configuration(false);

		String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf_" + nodeId + "/"
				+ configFileName;
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("updating fileName: " + fileName + " for " + nodeId);
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {

			File file = new File(
					installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf_" + nodeId);
			if (!file.exists()) {
				copyFolder(new File(installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf"), file,
						true);
				if (nodeType.equals(QueryIOConstants.NAMENODE)) {
					File topologyConfigFile = new File(installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/"
							+ nodeType + "-conf_" + nodeId + "/topologyConfig.sh");
					topologyConfigFile.setExecutable(true);
					topologyConfigFile.setReadable(true);
					topologyConfigFile.setWritable(true);
				}
			}

			if (!refresh) {
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);
			}
			for (int i = 0; i < property.size(); i++) {
				String key = (String) property.get(i);
				String val = ((String) value.get(i)).trim();
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug(key + " " + val);
				if (conf.get(key) != null && conf.get(key).length() != 0)
					conf.unset(key);
				conf.set(key, val);
			}

			fos = new FileOutputStream(new File(fileName));
			conf.writeXml(fos);

			return new QueryIOResponse(true, "Configuration done successfully.");
		} catch (Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			return new QueryIOResponse(false, ex.getMessage());
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
		}
	}

	public static QueryIOResponse unsetConfiguration(String installDir, String nodeType, String nodeId,
			String configFileName, ArrayList property) {
		Configuration conf = new Configuration(false);

		String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf_" + nodeId + "/"
				+ configFileName;
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("fileName: " + fileName);
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {

			fis = new FileInputStream(new File(fileName));
			conf.addResource(fis);

			for (int i = 0; i < property.size(); i++) {
				String key = (String) property.get(i);
				if (conf.get(key) != null && conf.get(key).length() != 0)
					conf.unset(key);
			}

			fos = new FileOutputStream(new File(fileName));
			conf.writeXml(fos);

			return new QueryIOResponse(true, "Configuration done successfully.");
		} catch (Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			return new QueryIOResponse(false, ex.getMessage());
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing stream", e);
			}
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing stream", e);
			}
		}
	}

	public static ArrayList getConfiguration(String installDir, String nodeType, String nodeId, String configFileName,
			ArrayList property) {
		Configuration conf = new Configuration(false);
		String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf_" + nodeId + "/"
				+ configFileName;
		ArrayList result = new ArrayList();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(fileName));
			conf.addResource(fis);
			for (int i = 0; i < property.size(); i++) {
				String str = conf.get((String) property.get(i));
				result.add(str == null ? "" : str);
			}
		} catch (Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
		}
		return result;
	}

	public static String[] getAllConfiguration(String installDir, String nodeType, String nodeId,
			String configFileName) {
		Configuration conf = new Configuration(false);
		String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + nodeType + "-conf_" + nodeId + "/"
				+ configFileName;
		FileInputStream fis = null;
		List list = new ArrayList();
		try {
			fis = new FileInputStream(new File(fileName));
			conf.addResource(fis);
			Iterator<Entry<String, String>> i = conf.iterator();
			while (i.hasNext()) {
				Entry<String, String> entry = i.next();
				list.add(entry.getKey() + QueryIOConstants.SEPERATOR + entry.getValue());
			}
		} catch (Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
		}
		String[] result = new String[list.size()];
		list.toArray(result);
		return result;
	}

	public static QueryIOResponse updateHostsList(String installDir, String nodeId, String[] datanodeAdds) {
		return updateList(installDir, nodeId, datanodeAdds, "hosts");
	}

	public static QueryIOResponse updateHostsExcludeList(String installDir, String nodeId, String[] datanodeAdds) {
		return updateList(installDir, nodeId, datanodeAdds, "hosts-exclude");
	}

	public static QueryIOResponse updateList(String installDir, String nodeId, String[] datanodeAdds, String listName) {
		String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + QueryIOConstants.NAMENODE + "-conf_"
				+ nodeId + "/" + listName;
		AppLogger.getLogger().fatal("Updating " + listName + " list. Size: " + datanodeAdds.length);
		BufferedWriter output = null;
		try {
			new File(fileName).delete();
			output = new BufferedWriter(new FileWriter(fileName));
			for (String datanodeAdd : datanodeAdds) {
				output.write(datanodeAdd);
				output.newLine();
			}
			output.write("");
			return new QueryIOResponse(true, null);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error fetching status of name node", e);
			return new QueryIOResponse(false, null);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing Writer", e);
			}
		}
	}

	public static QueryIOResponse startQueryIOServices(String installDir, String nodeId) {
		int port1 = getOS3ServerPort(installDir, nodeId);
		if (port1 == -1) {
			return new QueryIOResponse(false, "Property " + QueryIOConstants.QUERYIO_OS3SERVER_PORT
					+ " is absent in Node " + nodeId + " configuration.");
		}
		int port2 = getHDFSOverFTPServerPort(installDir, nodeId);
		if (port2 == -1) {
			return new QueryIOResponse(false, "Property " + QueryIOConstants.QUERYIO_HDFSOVERFTP_PORT
					+ " is absent in Node " + nodeId + " configuration.");
		}
		String dir = installDir + QueryIOConstants.QUERYIOSERVERS_DIR_NAME + File.separator + "bin";
		String cmd = null;
		if (PlatformHandler.isWindows()) {
			cmd = dir + separator + "start_servers.bat " + installDir + QueryIOConstants.QUERYIOSERVERS_DIR_NAME + " "
					+ port2 + " " + port1 + " " + installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/namenode-conf_"
					+ nodeId;
		} else {
			cmd = "sh start_servers.sh " + installDir + QueryIOConstants.QUERYIOSERVERS_DIR_NAME + " " + port2 + " "
					+ port1 + " " + installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/namenode-conf_" + nodeId;
		}
		QueryIOResponse response = executeCommand(dir, cmd);

		return response;
	}

	public static int getOS3ServerPort(String installDir, String nodeId) {
		ArrayList keys = new ArrayList();
		keys.add(QueryIOConstants.QUERYIO_OS3SERVER_PORT);
		ArrayList list = getConfiguration(installDir, QueryIOConstants.NAMENODE, nodeId, "core-site.xml", keys);
		if (list != null && list.size() > 0) {
			return Integer.parseInt((String) list.get(0));
		}
		return -1;
	}

	public static int getHDFSOverFTPServerPort(String installDir, String nodeId) {
		ArrayList keys = new ArrayList();
		keys.add(QueryIOConstants.QUERYIO_HDFSOVERFTP_PORT);
		ArrayList list = getConfiguration(installDir, QueryIOConstants.NAMENODE, nodeId, "core-site.xml", keys);
		if (list != null && list.size() > 0) {
			return Integer.parseInt((String) list.get(0));
		}
		return -1;
	}

	public static QueryIOResponse stopQueryIOServices(String installDir, String nodeId) {
		// TODO stop through script
		int port = getHDFSOverFTPServerPort(installDir, nodeId);
		if (port == -1) {
			return new QueryIOResponse(false, "Property " + QueryIOConstants.QUERYIO_HDFSOVERFTP_PORT
					+ " is absent in Node " + nodeId + " configuration.");
		}
		String pid = null;
		if (PlatformHandler.isWindows()) {
			pid = getProcessIDsWindows(QueryIOConstants.QUERYIOSERVERS_DIR_NAME + " " + port);
		} else {
			pid = ProcManager.getPID(QueryIOConstants.QUERYIOSERVERS_DIR_NAME + " " + port);
		}
		if (pid.equalsIgnoreCase(QueryIOConstants.EMPTY_STRING))
			return new QueryIOResponse(true, QueryIOConstants.NAMENODE_SERVICES_NOT_RUNNING);

		if (PlatformHandler.isWindows()) {
			killProcessWindows(QueryIOConstants.QUERYIOSERVERS_DIR_NAME + " " + port);
		} else {
			killProcessUnixBased(pid);
		}
		return new QueryIOResponse(true, QueryIOConstants.NAMENODE_SERVICES_STOPPED_SUCCESS);
	}

	public static QueryIOResponse executeCommand(String dir, String cmd) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug(cmd);
		Process process = null;
		StringWriter errorWriter = null;
		StringWriter inputWriter = null;
		try {
			process = Runtime.getRuntime().exec(cmd, null, new File(dir));
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

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Waiting ..");
			int i = 0;
			while (i < 60 && !inputWriter.toString().contains("STARTUP:SUCCESS")
					&& !errorWriter.toString().contains("STARTUP:SUCCESS")) {
				Thread.sleep(5000);
				i++;
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug(i);
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("errorWriter.toString()" + errorWriter.toString());
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("inputWriter.toString()" + inputWriter.toString());

			if (i < 15)
				return new QueryIOResponse(true, QueryIOConstants.NAMENODE_SERVICES_STARTED_SUCCESS);
			else
				return new QueryIOResponse(true, QueryIOConstants.NAMENODE_SERVICES_STARTED_FAILED);

		} catch (final Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			return new QueryIOResponse(true,
					QueryIOConstants.NAMENODE_SERVICES_STARTED_FAILED + "\n" + ex.getMessage());
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

	public static void killLocalProcess(String pid) {
		if (PlatformHandler.isWindows()) {
			killProcessWindows(pid);
		} else {
			killProcessUnixBased(pid);
		}
	}

	public static void killProcessUnixBased(String pid) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("kill process(s): " + pid);
		try {
			Runtime.getRuntime().exec("kill -9 " + pid);
		} catch (IOException e) {
		}
	}

	public static QueryIOResponse checkPortAvailability(String installDir, String nodeType, String nodeId) {
		String jmxPortSearchString = "-Dcom.sun.management.jmxremote.port=";
		List<Integer> portList = new ArrayList<Integer>();
		FileInputStream fis = null;
		try {
			if (nodeType.equals(QueryIOConstants.NAMENODE)) {
				Configuration conf = new Configuration(false);
				String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + QueryIOConstants.NAMENODE
						+ "-conf_" + nodeId + "/hdfs-site.xml";
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);
				fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + QueryIOConstants.NAMENODE
						+ "-conf_" + nodeId + "/core-site.xml";
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);

				String http = conf.get(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY + "." + nodeId);
				try {
					portList.add(Integer.parseInt(http.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String https = conf.get(DFSConfigKeys.DFS_NAMENODE_HTTPS_ADDRESS_KEY + "." + nodeId);
				try {
					portList.add(Integer.parseInt(https.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String server = conf.get(DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY + "." + nodeId);
				try {
					portList.add(Integer.parseInt(server.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String os3 = conf.get(QueryIOConstants.QUERYIO_OS3SERVER_PORT);
				try {
					portList.add(Integer.parseInt(os3));
				} catch (Exception e) {
					// Ignore
				}

				String secureOS3 = conf.get(QueryIOConstants.QUERYIO_OS3SERVER_SECUREPORT);
				try {
					portList.add(Integer.parseInt(secureOS3));
				} catch (Exception e) {
					// Ignore
				}

				String ftp = conf.get(QueryIOConstants.QUERYIO_FTPSERVER_PORT);
				try {
					portList.add(Integer.parseInt(ftp));
				} catch (Exception e) {
					// Ignore
				}

				String sftp = conf.get(QueryIOConstants.QUERYIO_FTPSERVER_SSLPORT);
				try {
					portList.add(Integer.parseInt(sftp));
				} catch (Exception e) {
					// Ignore
				}

				String hdfsOverFtp = conf.get(QueryIOConstants.QUERYIO_HDFSOVERFTP_PORT);
				try {
					portList.add(Integer.parseInt(hdfsOverFtp));
				} catch (Exception e) {
					// Ignore
				}

				String jmx = conf.get(QueryIOConstants.NAMENODE_OPTS_KEY);
				// -Dcom.sun.management.jmxremote.port=XXXX
				for (String str : jmx.split(" ")) {
					if (str.contains(jmxPortSearchString)) {
						try {
							portList.add(Integer.parseInt(str.split("=")[1]));
						} catch (Exception e) {
							// Ignore
						}
						break;
					}
				}
			} else if (nodeType.equals(QueryIOConstants.DATANODE)) {
				Configuration conf = new Configuration(false);
				String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + QueryIOConstants.DATANODE
						+ "-conf_" + nodeId + "/hdfs-site.xml";
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);
				fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + QueryIOConstants.DATANODE
						+ "-conf_" + nodeId + "/core-site.xml";
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);

				String http = conf.get(DFSConfigKeys.DFS_DATANODE_HTTP_ADDRESS_KEY);
				try {
					portList.add(Integer.parseInt(http.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String server = conf.get(DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY);
				try {
					portList.add(Integer.parseInt(server.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String ipc = conf.get(DFSConfigKeys.DFS_DATANODE_IPC_ADDRESS_KEY);
				try {
					portList.add(Integer.parseInt(ipc.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String jmx = conf.get(QueryIOConstants.DATANODE_OPTS_KEY);
				// -Dcom.sun.management.jmxremote.port=XXXX
				for (String str : jmx.split(" ")) {
					if (str.contains(jmxPortSearchString)) {
						try {
							portList.add(Integer.parseInt(str.split("=")[1]));
						} catch (Exception e) {
							// Ignore
						}
						break;
					}
				}
			} else if (nodeType.equals(QueryIOConstants.SECONDARYNAMENODE)) {
				Configuration conf = new Configuration(false);
				String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/"
						+ QueryIOConstants.SECONDARYNAMENODE + "-conf_" + nodeId + "/hdfs-site.xml";
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);
				fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + QueryIOConstants.SECONDARYNAMENODE
						+ "-conf_" + nodeId + "/core-site.xml";
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);

				String server = conf.get(DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY + "."
						+ conf.get(DFSConfigKeys.DFS_NAMESERVICES));
				try {
					portList.add(Integer.parseInt(server.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String jmx = conf.get(QueryIOConstants.SECONDARYNAMENODE_OPTS_KEY);
				// -Dcom.sun.management.jmxremote.port=XXXX
				for (String str : jmx.split(" ")) {
					if (str.contains(jmxPortSearchString)) {
						try {
							portList.add(Integer.parseInt(str.split("=")[1]));
						} catch (Exception e) {
							// Ignore
						}
						break;
					}
				}
			} else if (nodeType.equals(QueryIOConstants.JOURNALNODE)) {
				Configuration conf = new Configuration(false);
				String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + QueryIOConstants.JOURNALNODE
						+ "-conf_" + nodeId + "/hdfs-site.xml";
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);
				fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + QueryIOConstants.JOURNALNODE
						+ "-conf_" + nodeId + "/core-site.xml";
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);

				String server = conf.get(DFSConfigKeys.DFS_JOURNALNODE_RPC_ADDRESS_KEY);
				try {
					portList.add(Integer.parseInt(server.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}
				String http = conf.get(DFSConfigKeys.DFS_JOURNALNODE_HTTP_ADDRESS_KEY);
				try {
					portList.add(Integer.parseInt(http.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String jmx = conf.get(QueryIOConstants.JOURNALNODE_OPTS_KEY);
				// -Dcom.sun.management.jmxremote.port=XXXX
				for (String str : jmx.split(" ")) {
					if (str.contains(jmxPortSearchString)) {
						try {
							portList.add(Integer.parseInt(str.split("=")[1]));
						} catch (Exception e) {
							// Ignore
						}
						break;
					}
				}
			} else if (nodeType.equals(QueryIOConstants.RESOURCEMANAGER)) {
				Configuration conf = new Configuration(false);
				String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/"
						+ QueryIOConstants.RESOURCEMANAGER + "-conf_" + nodeId + "/yarn-site.xml";
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);
				fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + QueryIOConstants.RESOURCEMANAGER
						+ "-conf_" + nodeId + "/mapred-site.xml";
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);

				String server = conf.get(YarnConfiguration.RM_ADDRESS);
				try {
					portList.add(Integer.parseInt(server.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String admin = conf.get(YarnConfiguration.RM_ADMIN_ADDRESS);
				try {
					portList.add(Integer.parseInt(admin.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String resourceTracker = conf.get(YarnConfiguration.RM_RESOURCE_TRACKER_ADDRESS);
				try {
					portList.add(Integer.parseInt(resourceTracker.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String scheduler = conf.get(YarnConfiguration.RM_SCHEDULER_ADDRESS);
				try {
					portList.add(Integer.parseInt(scheduler.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String webapp = conf.get(YarnConfiguration.RM_WEBAPP_ADDRESS);
				try {
					portList.add(Integer.parseInt(webapp.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String jobHistoyServer = conf.get("mapreduce.jobhistory.address");
				try {
					portList.add(Integer.parseInt(jobHistoyServer.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String jobHistoyWebapp = conf.get("mapreduce.jobhistory.webapp.address");
				try {
					portList.add(Integer.parseInt(jobHistoyWebapp.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String jmx = conf.get(QueryIOConstants.RESOURCEMANAGER_OPTS_KEY);
				// -Dcom.sun.management.jmxremote.port=XXXX
				for (String str : jmx.split(" ")) {
					if (str.contains(jmxPortSearchString)) {
						try {
							portList.add(Integer.parseInt(str.split("=")[1]));
						} catch (Exception e) {
							// Ignore
						}
						break;
					}
				}
			} else if (nodeType.equals(QueryIOConstants.NODEMANAGER)) {
				Configuration conf = new Configuration(false);
				String fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + QueryIOConstants.NODEMANAGER
						+ "-conf_" + nodeId + "/yarn-site.xml";
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);
				fileName = installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/" + QueryIOConstants.NODEMANAGER
						+ "-conf_" + nodeId + "/mapred-site.xml";
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);

				String localizer = conf.get(YarnConfiguration.NM_LOCALIZER_ADDRESS);
				try {
					portList.add(Integer.parseInt(localizer.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String webapp = conf.get(YarnConfiguration.NM_WEBAPP_ADDRESS);
				try {
					portList.add(Integer.parseInt(webapp.split(":")[1]));
				} catch (Exception e) {
					// Ignore
				}

				String jmx = conf.get(QueryIOConstants.NODEMANAGER_OPTS_KEY);
				// -Dcom.sun.management.jmxremote.port=XXXX
				for (String str : jmx.split(" ")) {
					if (str.contains(jmxPortSearchString)) {
						try {
							portList.add(Integer.parseInt(str.split("=")[1]));
						} catch (Exception e) {
							// Ignore
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (Exception e) {
					// Ignore
				}

		}

		return checkPortAvailability(portList);
	}

	public static QueryIOResponse checkPortAvailability(List<Integer> portList) {
		List<Integer> unavailablePortList = new ArrayList<Integer>();
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Checking Port Availability for ports: " + portList);
		for (int port : portList) {
			Socket s = null;
			try {
				InetAddress addr = InetAddress.getLocalHost();
				s = new Socket(addr, port);
				unavailablePortList.add(port);
			} catch (Exception e) {

			} finally {
				if (s != null)
					try {
						s.close();
					} catch (Exception e) {
						// Ignore
					}
			}
		}
		if (unavailablePortList.size() > 0) {
			return new QueryIOResponse(false, "Ports: " + unavailablePortList.toString() + " are already in use.");
		} else {
			return new QueryIOResponse(true, null);
		}
	}

	public static QueryIOResponse fetchNamespaceId(String namespaceDir) {
		File in = new File(namespaceDir + File.separator + "current" + File.separator + "VERSION");
		String namespaceId = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains("namespaceID=")) {
					namespaceId = line.split("=")[1];
					break;
				}
			}
			return new QueryIOResponse(true, namespaceId);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			return new QueryIOResponse(false, e.getLocalizedMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static QueryIOResponse fetchBlockPoolId(String namespaceDir) {
		File in = new File(namespaceDir + File.separator + "current" + File.separator + "VERSION");
		String blockpoolID = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains("blockpoolID=")) {
					blockpoolID = line.split("=")[1];
					break;
				}
			}
			return new QueryIOResponse(true, blockpoolID);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getLocalizedMessage(), e);
			return new QueryIOResponse(false, e.getLocalizedMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void updateHiveSite(String filePath, String dbPort) throws Exception {
		File in = null;
		File out = null;

		BufferedReader reader = null;
		Writer writer = null;

		ArrayList lines = new ArrayList();
		try {
			filePath = filePath + QueryIOConstants.HIVE_DIR_NAME + "/conf/hive-site.xml";
			in = new File(filePath);
			out = new File(filePath);
			reader = new BufferedReader(new FileReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains("queryio.hive.metastore.ConnectionURL")) {
					String port = line.substring(line.lastIndexOf(":") + 1, line.indexOf("/metastore"));
					line = line.replace(port, dbPort);
				}
				if (line.contains("javax.jdo.option.ConnectionURL")) {
					String port = line.substring(line.lastIndexOf(":") + 1, line.indexOf("/hive"));
					line = line.replace(port, dbPort);
				}

				lines.add(line);
			}

			writer = new BufferedWriter(new FileWriter(out));
			for (int i = 0; i < lines.size(); i++) {
				writer.write((String) lines.get(i));
				writer.write("\n");
			}
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (writer != null)
					writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void updateHadoopPath(String path) throws Exception {
		String installedHadoop = getInstalledHadoopDir(path);
		AppLogger.getLogger().fatal("installedDir : " + installedHadoop);
		if (!installedHadoop.isEmpty()) {
			if (installedHadoop.equals(QueryIOConstants.HADOOP_DIR_NAME)) {
				return;
			} else if (installedHadoop.equals(QueryIOConstants.HADOOP_2_4_0_DIR_NAME)) {
				UpgradeHadoop2_4_0To2_7_0.startUpgrade(path);
			} else if (installedHadoop.equals(QueryIOConstants.HADOOP_2_2_0_DIR_NAME)) {
				UpgradeHadoop2_2_0To2_4_0.startUpgrade(path);
			} else if (installedHadoop.equals(QueryIOConstants.HADOOP_2_0_4_DIR_NAME)) {
				UpgradeHadoop2_0_4To2_2_0.startUpgrade(path);
			} else if (installedHadoop.equals(QueryIOConstants.HADOOP_2_0_3_DIR_NAME)) {
				UpgradeHadoop2_0_4To2_2_0.startUpgrade(path);
			} else if (installedHadoop.equals(QueryIOConstants.HADOOP_2_7_0_DIR_NAME)) {
				UpgradeHadoop2_7_0To2_7_1.startUpgrade(path);
			}

		}
	}

	public static String getInstalledHadoopDir(String path) {
		String dir = "";
		String command = "ls " + path + "/../QueryIOPackage_bak/";
		Process p = null;
		BufferedReader inReader = null;
		try {
			p = Runtime.getRuntime().exec(command);
			inReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = inReader.readLine()) != null) {
				if (line.contains("adoop"))
					dir = line;
			}
			inReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			line = null;
			while ((line = inReader.readLine()) != null) {
				AppLogger.getLogger().fatal(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (p != null)
				p.destroy();
			if (inReader != null) {
				try {
					inReader.close();
				} catch (IOException e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}

		}
		return dir;
	}

	public static void updateStartStopScripts(String hostHome) throws Exception {

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("updateStartStopScripts()");
		String home = hostHome;
		if (!hostHome.endsWith("/"))
			hostHome = hostHome + "/";
		String[] filePaths = { hostHome + QueryIOConstants.SCRIPTS_DIR_NAME + "/start_agent.sh",
				hostHome + QueryIOConstants.SCRIPTS_DIR_NAME + "/stop_agent.sh",
				hostHome + QueryIOConstants.SCRIPTS_DIR_NAME + "/start_hadoop.sh",
				hostHome + QueryIOConstants.SCRIPTS_DIR_NAME + "/start_hive.sh",
				hostHome + QueryIOConstants.SCRIPTS_DIR_NAME + "/start_queryio_services.sh",
				hostHome + QueryIOConstants.SCRIPTS_DIR_NAME + "/stop_hadoop.sh",
				hostHome + QueryIOConstants.SCRIPTS_DIR_NAME + "/stop_hive.sh",
				hostHome + QueryIOConstants.SCRIPTS_DIR_NAME + "/stop_queryio_services.sh",
				hostHome + QueryIOConstants.SCRIPTS_DIR_NAME + "/upgrade_namenode.sh", };

		for (String filePath : filePaths) {
			File in = new File(filePath);
			File out = new File(filePath);

			BufferedReader reader = null;
			Writer writer = null;

			ArrayList lines = new ArrayList();
			try {
				reader = new BufferedReader(new FileReader(in));
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (line.contains("$USER_INSTALL_DIR$"))
						line = line.replace("$USER_INSTALL_DIR$", home);
					lines.add(line);
				}

				writer = new BufferedWriter(new FileWriter(out));
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
		updateStartServerManually(hostHome);
	}

	public static void updateStartServerManually(String home) {
		if (home.endsWith("/"))
			home = home.substring(0, home.length() - 1);
		String filePath = home + File.separator + QueryIOConstants.QUERYIOSERVERS_DIR_NAME + File.separator
				+ "bin/start_servers_manually.sh";
		File in = new File(filePath);
		File out = new File(filePath);

		BufferedReader reader = null;
		Writer writer = null;

		ArrayList lines = new ArrayList();
		try {
			reader = new BufferedReader(new FileReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains("$USER_INSTALL_DIR$"))
					line = line.replace("$USER_INSTALL_DIR$", home);
				lines.add(line);
			}

			writer = new BufferedWriter(new FileWriter(out));
			for (int i = 0; i < lines.size(); i++) {
				writer.write((String) lines.get(i));
				writer.write("\n");
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
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

	public static void backupHadoopEtc(String hostHome) throws Exception {

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("backupHadoopEtc()");
		String etcDir = hostHome + "/" + QueryIOConstants.HADOOP_DIR_NAME + "/etc";
		String etcBackUpDir = hostHome + "_bak/ " + QueryIOConstants.HADOOP_DIR_NAME + "/etc";

		AppLogger.getLogger().fatal("etcDir: " + etcDir);
		AppLogger.getLogger().fatal("etcBackUpDir: " + etcBackUpDir);
		String cmd = "cp -r " + etcBackUpDir + " " + etcDir + " ";
		QueryIOResponse response = executeHadoopCommand(cmd, hostHome, false, 0, false, false);
	}

	public static void updateJavaHome(String installDir, String javaHomePath) {
		// StartupParameters.getHadoopDirLocation() +
		// QueryIOConstants.HADOOP_DIR_NAME
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("updateJavaHome() : " + javaHomePath);

		String extention = PlatformHandler.isWindows() ? ".cmd" : ".sh";
		// TODO: Temp windows handling. Remove this line later.
		// javaHomePath = "C:\\Program Files\\Java\\jdk1.7.0_11";
		// if (javaHomePath.contains(" ") && !javaHomePath.startsWith("\"")) {
		// javaHomePath = "\"" + javaHomePath + "\"";
		// }
		String[] filePaths = {
				// installDir + QueryIOConstants.HADOOP_DIR_NAME + "/bin/yarn",
				installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/datanode-conf/hadoop-env" + extention,
				installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/namenode-conf/hadoop-env" + extention,
				installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/nodemanager-conf/hadoop-env" + extention,
				installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/resourcemanager-conf/hadoop-env" + extention,
				installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/secondarynamenode-conf/hadoop-env" + extention,
				installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/journalnode-conf/hadoop-env" + extention,
				installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/nodemanager-conf/yarn-env" + extention,
				installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/resourcemanager-conf/yarn-env" + extention,
				installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/nodemanager-conf/mapred-env" + extention,
				installDir + QueryIOConstants.HADOOP_DIR_NAME + "/etc/resourcemanager-conf/mapred-env" + extention,

				// installDir + QueryIOConstants.HADOOP_DIR_NAME + "/bin/hdfs",
				// installDir + QueryIOConstants.HADOOP_DIR_NAME +
				// "/bin/mapred",
				// installDir + QueryIOConstants.HADOOP_DIR_NAME + "/bin/rcc",
				// installDir + QueryIOConstants.HADOOP_DIR_NAME +
				// "/bin/hadoop"};
		};
		List<String> filePathsList = Arrays.asList(filePaths);
		filePaths = new String[filePathsList.size()];
		if (!PlatformHandler.isWindows()) {
			filePathsList = new ArrayList<String>(filePathsList);
			filePathsList.add(installDir + QueryIOConstants.HIVE_DIR_NAME + "/conf/hive-env" + extention);
		}
		filePaths = filePathsList.toArray(filePaths);
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
					if (line.contains(exportConstant + " JAVA_HOME=")) {
						lines.add(exportConstant + " JAVA_HOME=" + javaHomePath);
						flag = true;
					} else {
						lines.add(line);
					}
				}
				if (!flag) {
					myLines.add(exportConstant + " JAVA_HOME=" + javaHomePath);
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
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
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

	public static void updateLoggerPropertiesFile(String installDir) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("updateLoggerPropertiesFile()");
		String propFilePath = installDir + QueryIOConstants.QUERYIOSERVERS_DIR_NAME
				+ "/webapps/hdfs-over-ftp/WEB-INF/log4j.properties";
		updateLoggerPropertiesFile(propFilePath, "ftp", installDir, "ftpserver");

		propFilePath = installDir + QueryIOConstants.QUERYIOSERVERS_DIR_NAME
				+ "/webapps/os3server/WEB-INF/os3log4j.properties";
		updateLoggerPropertiesFile(propFilePath, "os3", installDir, "os3server");
	}

	private static void updateLoggerPropertiesFile(String propFilePath, String prefix, String homeDir,
			String logFileName) {
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
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
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
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (Exception e) {
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void updateHiveHadoopHome(String installDir) throws Exception {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("updateHadoopHome()");
		String hadoopHome = installDir + QueryIOConstants.HADOOP_DIR_NAME;
		String[] filePaths = { installDir + QueryIOConstants.HIVE_DIR_NAME + "/conf/hive-env.sh" };
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
					if (line.startsWith("HADOOP_HOME=")) {
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

	public static QueryIOResponse updateHiveSiteConfiguration(String installDir, String nodeId, ArrayList property,
			ArrayList value, boolean refresh) {
		Configuration conf = new Configuration(false);

		String fileName = installDir + QueryIOConstants.HIVE_DIR_NAME + "/conf/hive-site.xml";
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("updating fileName: " + fileName + " for " + nodeId);
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {

			if (!refresh) {
				fis = new FileInputStream(new File(fileName));
				conf.addResource(fis);
			}
			for (int i = 0; i < property.size(); i++) {
				String key = (String) property.get(i);
				String val = ((String) value.get(i)).trim();
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug(key + " " + val);
				if (val.startsWith("file://") && PlatformHandler.isWindows()) {
					val = val.substring("file://".length());
				}
				if (conf.get(key) != null && conf.get(key).length() != 0) {
					conf.unset(key);
				}
				conf.set(key, val);
			}
			fos = new FileOutputStream(new File(fileName));
			conf.writeXml(fos);

			return new QueryIOResponse(true, "Hive Configuration done successfully.");
		} catch (Exception ex) {
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
			return new QueryIOResponse(false, ex.getMessage());
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing stream", e);
			}
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				AppLogger.getLogger().fatal("Error closing stream", e);
			}
		}
	}
	
	// Changes done for Hive 2.1
	// Need to run schematool for creation of hive metastore DB
	
public static QueryIOResponse configureHiveSchema(String installDir) throws IOException {
		if (!installDir.endsWith("/")) {
			installDir = installDir + "/";
		}
		installDir = installDir + QueryIOConstants.HIVE_DIR_NAME;

		AppLogger.getLogger().debug("installDir: " + installDir);
		
		String schemaToolCmd = "bin/schematool -dbType mysql -initSchema --verbose";
		String schemaToolCmdInfo = "bin/schematool -dbType mysql -info";
		
		QueryIOResponse checkHive = executeHadoopCommand(schemaToolCmdInfo, installDir, false, 0, false, false);
		
		QueryIOResponse schemaCreationResponse = executeHadoopCommand(schemaToolCmd, installDir, false, 0, false, false);

		if(schemaCreationResponse.isSuccessful()){
			schemaCreationResponse.setResponseMsg(QueryIOConstants.HIVE_STARTED_SUCCESS);
			schemaCreationResponse.setSuccessful(true);
			
		
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger()
				.debug("configureHiveScheme");
		}
		else {
			
			
			
			
			schemaCreationResponse.setResponseMsg(QueryIOConstants.HIVE_STARTED_FAILED);
			schemaCreationResponse.setSuccessful(false);
			
			
			if(checkHive.isSuccessful())
			{
				schemaCreationResponse.setResponseMsg(QueryIOConstants.HIVE_STARTED_SUCCESS);
				schemaCreationResponse.setSuccessful(true);
			}
			
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger()
				.debug("configureHiveScheme");
			
		}
		return schemaCreationResponse;
	}

	public static QueryIOResponse startHiveServer(String installDir) throws IOException {
		
		if (!installDir.endsWith("/")) {
			installDir = installDir + "/";
		}
		installDir = installDir + QueryIOConstants.HIVE_DIR_NAME;

		AppLogger.getLogger().debug("installDir: " + installDir);

		String cmd = "sh bin/startHive.sh";
		QueryIOResponse response = executeHadoopCommand(cmd, installDir, false, 0, false, false);
		if (response.isSuccessful()) {
			response.setResponseMsg(QueryIOConstants.HIVE_STARTED_SUCCESS);
			response.setSuccessful(true);
		} else {
			response.setResponseMsg(QueryIOConstants.HIVE_STARTED_FAILED);
			response.setSuccessful(false);
		}
		return response;

	}

	public static QueryIOResponse stopHiveServer() {
		String pid = null;
		if (PlatformHandler.isWindows()) {
			pid = getProcessIDsWindows(QueryIOConstants.HIVE_HIVESERVER_CLASS);
			killProcessWindows(QueryIOConstants.HIVE_HIVESERVER_CLASS);
		} else {
			// TODO stop through script
			pid = ProcManager.getPID(QueryIOConstants.HIVE_HIVESERVER_CLASS);

			killProcessUnixBased(pid);
		}
		if (pid.equalsIgnoreCase(QueryIOConstants.EMPTY_STRING))
			return new QueryIOResponse(true, QueryIOConstants.HIVE_SERVICE_NOT_RUNNING);

		return new QueryIOResponse(true, QueryIOConstants.HIVE_SERVICE_STOPPED_SUCCESS);
	}

	public static void executeCommand(String command) {
		Process p = null;
		BufferedReader inReader = null;
		try {
			p = Runtime.getRuntime().exec(command);
			inReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			// while((line=inReader.readLine())!=null)
			// {
			// AppLogger.getLogger().fatal(line);
			// }
			inReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			line = null;
			while ((line = inReader.readLine()) != null) {
				AppLogger.getLogger().fatal(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (p != null)
				p.destroy();
			if (inReader != null) {
				try {
					inReader.close();
				} catch (IOException e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}

		}
	}

	private static boolean killProcessWindows(String processName) {
		int exitCode = -1;
		String processID = null;
		try {
			processID = getProcessIDsWindows(processName);
			if (AppLogger.getLogger().isDebugEnabled()) {
				AppLogger.getLogger().debug("ProcessID for " + processName + " is " + processID);
			}
			if (processID != null && !processID.isEmpty()) {
				exitCode = Runtime.getRuntime().exec("taskkill /F " + processID).waitFor();
			} else {
				exitCode = 0;
			}
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error while killing Process " + processName, e);
		}
		return exitCode == 0;
	}

	private static String getProcessIDsWindows(String processName) {
		String processIDList = "";
		String processID = "";
		String cmd = "wmic PROCESS get Processid,Commandline";
		Process process = null;
		BufferedReader reader = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains(processName)) {
					line = line.trim();
					if (line.length() > 6) {
						processID = line.substring(line.length() - 6, line.length()).trim();
						processIDList += "/PID " + processID + " ";
					}
				}
			}
		} catch (IOException e) {
			AppLogger.getLogger().fatal("Error fetching process ID for windows.", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					AppLogger.getLogger().fatal("Error closing reader.", e);
				}
			}
			if (process != null) {
				process.destroy();
			}
		}
		return processIDList;
	}

	public static QueryIOResponse refreshUserToGroupsMappings(String installDir, String nodeId) {
		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("Refreshing Namenode UserGroup Mapping");
		StringBuilder cmd = new StringBuilder();
		// cmd.append("bin").append(separator);
		if (PlatformHandler.isWindows()) {
			cmd.append(installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "bin" + separator + "hdfs.cmd");
		} else {
			cmd.append("bin/hdfs");
		}
		cmd.append(" --config " + installDir + QueryIOConstants.HADOOP_DIR_NAME + separator + "etc").append(separator)
				.append("namenode-conf_").append(nodeId).append(separator)
				.append(" dfsadmin -refreshUserToGroupsMappings");
		return executeHadoopCommand(cmd.toString(), installDir, true, 0, false, true);
	}

	public static void main(String[] args) {
		String[] ips = { "192.168.0.25", "192.168.0.22", "192.168.0.2" };
		String[] rackNames = { "/default-rack", "/my-rack", "/your-rack" };
		System.out.println(updateNetworkConfigWindows(ips, rackNames).toString());
	}
}