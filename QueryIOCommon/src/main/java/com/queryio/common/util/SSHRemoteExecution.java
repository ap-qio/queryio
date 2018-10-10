package com.queryio.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Remote Execution of commands on a remote machine.
 */

public class SSHRemoteExecution {
	static final Log LOG = LogFactory.getLog(SSHRemoteExecution.class);
	static final int SSH_PORT = 22;
	static final String DEFAULT_IDENTITY = "id_dsa";
	static final String DEFAULT_KNOWNHOSTS = "known_hosts";
	static final String FS = System.getProperty("file.separator");
	static final String LS = System.getProperty("line.separator");
	private int exitCode;
	private StringBuffer output;
	private String commandString;

	final StringBuffer errorMessage = new StringBuffer();

	public SSHRemoteExecution() throws Exception {
	}

	public Session createSession(String hostName, String userName, String password, int portNumber)
			throws JSchException {
		JSch jsch = null;
		Session session = null;

		jsch = new JSch();

		if (AppLogger.getLogger().isDebugEnabled())
			AppLogger.getLogger().debug("in createSession");

		session = jsch.getSession(userName, hostName, portNumber);

		session.setUserInfo(new SSHUserInfo(password, false));

		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);

		return session;
	}

	public Session createSessionWithPrivateKeyFile(String hostName, String userName, String privateKeyFile,
			int portNumber) throws Exception {
		JSch jsch = null;
		Session session = null;

		try {
			jsch = new JSch();

			jsch.addIdentity(privateKeyFile);

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Private key file: " + privateKeyFile);

			session = jsch.getSession(userName, hostName, portNumber);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
		} catch (Exception e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return session;
	}

	/**
	 * Execute command at remote host under given user
	 * 
	 * @param remoteHostName
	 *            remote host name
	 * @param user
	 *            is the name of the user to be login under; current user will
	 *            be used if this is set to <code>null</code>
	 * @param command
	 *            to be executed remotely
	 * @param identityFile
	 *            is the name of alternative identity file; default is
	 *            ~user/.ssh/id_dsa
	 * @param portNumber
	 *            remote SSH daemon port number, default is 22
	 * @throws Exception
	 *             in case of errors
	 */
	public void executeCommand(Session session, String command) throws Exception {
		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug(command);
		commandString = command;

		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		channel.setInputStream(null);
		final BufferedReader errReader = new BufferedReader(
				new InputStreamReader(((ChannelExec) channel).getErrStream()));
		BufferedReader inReader = new BufferedReader(new InputStreamReader(channel.getInputStream()));

		channel.connect();

		Thread errorThread = new Thread() {
			@Override
			public void run() {
				try {
					String line = errReader.readLine();
					while ((line != null) && !isInterrupted()) {
						errorMessage.append(line);
						errorMessage.append(LS);
						line = errReader.readLine();
					}
				} catch (IOException ioe) {
					LOG.warn("Error reading the error stream", ioe);
				}
			}
		};

		try {
			errorThread.start();
		} catch (IllegalStateException e) {
			LOG.debug(e);
		}

		try {
			parseExecResult(inReader);
			String line = inReader.readLine();
			while (line != null) {
				line = inReader.readLine();
			}
			if (channel.isClosed()) {
				exitCode = channel.getExitStatus();
				LOG.debug("exit-status: " + exitCode);
			}

			try {
				// make sure that the error thread exits
				errorThread.join();
			} catch (InterruptedException ie) {
				LOG.warn("Interrupted while reading the error stream", ie);
			}
		} catch (Exception ie) {
			throw new IOException(ie.toString());
		} finally {
			try {
				inReader.close();
			} catch (IOException ioe) {
				LOG.warn("Error while closing the input stream", ioe);
			}
			try {
				errReader.close();
			} catch (IOException ioe) {
				LOG.warn("Error while closing the error stream", ioe);
			}
			channel.disconnect();
		}
	}

	public void closeSession(Session session) throws Exception {
		if (session != null)
			session.disconnect();
	}

	public int getExitCode() {
		return exitCode;
	}

	protected void parseExecResult(BufferedReader lines) throws IOException {
		output = new StringBuffer();
		char[] buf = new char[512];
		int nRead;
		while ((nRead = lines.read(buf, 0, buf.length)) > 0) {
			output.append(buf, 0, nRead);
		}
	}

	/** Get the output of the ssh command. */
	public String getOutput() {
		return (output == null) ? "" : output.toString();
	}

	/** Get the String representation of ssh command */
	public String getCommandString() {
		return commandString;
	}

	/*
	 * For Open S3 implementation
	 */
	public boolean sftpFileToRemote(File localFile, String remoteDirectory, Session session) throws IOException {
		ChannelSftp channelSftp = null;
		try {
			String fileName = localFile.getName();
			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();

			// Windows handling.. This will not affect Linux based hosts.
			// C:\\Users\\AppPerfect\\QueryIO is to be converted as
			// /C:/Users/AppPerfect/QueryIO
			remoteDirectory = remoteDirectory.replaceAll("\\\\", "/");
			if (!remoteDirectory.startsWith("/")) {
				remoteDirectory = "/" + remoteDirectory;
			}

			String[] dirPathTokens = remoteDirectory.split("/");
			String dirPath = dirPathTokens[0];
			for (int i = 1; i < dirPathTokens.length; i++) {
				dirPath += "/" + dirPathTokens[i];
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug(dirPath);
				try {
					channelSftp.cd(dirPath);
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
					channelSftp.mkdir(dirPath);
				}
			}
			channelSftp.cd(remoteDirectory);
			channelSftp.put(new FileInputStream(localFile), fileName);
		} catch (Exception e) {
			AppLogger.getLogger()
					.fatal("Could not transfer file " + localFile.getCanonicalPath() + " to " + remoteDirectory);
			throw new IOException(e);
		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
			}
		}
		return true;
	}
}