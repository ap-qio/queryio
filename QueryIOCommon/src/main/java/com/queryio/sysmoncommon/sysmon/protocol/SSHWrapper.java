package com.queryio.sysmoncommon.sysmon.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.queryio.common.IOSProtocolConstants;

public class SSHWrapper extends AbstractProtocolWrapper {
	private JSch jsch = null;
	private Session firewallSession = null;
	private Session session = null;
	private Channel channel = null;
	private String keyFile;
	private int authMethod;

	// /**
	// * @param targetOsType
	// */
	// public SSHWrapper(final String hostName, final String userName, final
	// String password, final String key, final int authMethod, final int
	// targetOsType)
	// {
	// this(hostName, userName, password, key, authMethod,
	// IOSProtocolConstants.DEFAULT_SSH_PORT, targetOsType);
	// }

	public SSHWrapper(final String hostName, final String userName, final String password, final String key,
			final int authMethod, final int portId, final int targetOsType) {
		super(hostName, userName, password, (portId != -1 ? portId : IOSProtocolConstants.DEFAULT_SSH_PORT),
				targetOsType);
		this.jsch = new JSch();
		this.keyFile = key;
		this.authMethod = authMethod;
	}

	/**
	 * method connect
	 */
	public boolean connect() throws IOException {
		try {
			int assignedPort = this.portId;
			String assignedHost = this.hostName;
			// System.out.println("this.firewallUserName >> " +
			// this.firewallUserName + " this.userName >>> " + this.userName);
			if (this.firewallUserName != null && this.firewallUserName.trim().length() > 0
					&& (!this.firewallUserName.toLowerCase().trim().equals("null"))) {
				int charIndex = firewallUserName.indexOf('@');
				final String fUserName = firewallUserName.substring(0, charIndex);
				String fHostName = firewallUserName.substring(charIndex + 1);
				int fPort = this.portId;

				charIndex = fHostName.indexOf(':');
				if (charIndex != -1) {
					try {
						fPort = Integer.parseInt(fHostName.substring(charIndex + 1));
					} catch (Exception ex) {
						// do nothing
					}
					fHostName = fHostName.substring(0, charIndex);
				}

				this.firewallSession = this.jsch.getSession(fUserName, fHostName, fPort);
				this.firewallSession.setDaemonThread(true);
				this.firewallSession.setUserInfo(new SSHUserInfo(this.firewallPassword, false));
				this.firewallSession.connect();

				assignedPort = this.firewallSession.setPortForwardingL(0, this.hostName, this.portId);
				assignedHost = IOSProtocolConstants.LOCALHOST;
			}
			if (authMethod != IOSProtocolConstants.AUTH_METHOD_PASSWORD) {
				this.jsch.addIdentity(keyFile);
			}
			this.session = this.jsch.getSession(this.userName, assignedHost, assignedPort);
			this.session.setDaemonThread(true);
		} catch (final JSchException e) {
			throw new IOException(e.getMessage());
		}
		return true;
	}

	public boolean isConnected() {
		return this.session != null ? this.session.isConnected() : false;
	}

	/**
	 * Disconnect the telnet connection
	 */
	public void disconnect() throws IOException {
		if (this.isConnected()) {
			this.session.disconnect();
		}
		if (this.firewallSession != null) {
			this.firewallSession.disconnect();
		}
	}

	/**
	 * Login into remote host.
	 */
	public boolean login() throws IOException {
		session.setUserInfo(
				new SSHUserInfo(this.password, (authMethod == IOSProtocolConstants.AUTH_METHOD_PRIVATE_KEY)));
		try {
			session.connect();
			if (iTargetOSType == IOSProtocolConstants.AIX) {
				channel = session.openChannel("shell");
				((ChannelShell) channel).setPty(false);

				in = channel.getInputStream();
				out = channel.getOutputStream();
				lineReader = new BufferedReader(new InputStreamReader(in));
				errLineReader = new BufferedReader(new InputStreamReader(channel.getExtInputStream()));
				channel.connect();

				// Following code was present in earlier handling. Please
				// uncomment it and verify
				// if this works fine.
				/*
				 * send("PATH=$PATH:/usr/sbin:/usr/bin:/usr/local/bin:");
				 * send("export PATH"); send("PS1=\"APPPERFECT>\"");
				 * send("TERM=ansi"); send("export TERM"); send(""); try {
				 * Thread.sleep(1000); String line = null; while
				 * (lineReader.ready()) { line = readLine(); if (line == null ||
				 * APPPERFECT_PROMPT.equals(line)) { break; } } } catch
				 * (Exception ex) { // do nothing }
				 */
			}
		} catch (JSchException e) {
			throw new IOException(e.getMessage());
		}
		return true;
	}

	/**
	 * @see com.queryio.sysmoncommon.sysmon.protocol.AbstractProtocolWrapper#send(java.lang.String)
	 */
	public void send(String cmd) throws IOException {
		if (iTargetOSType == IOSProtocolConstants.AIX) {
			super.send(cmd);
		} else {
			try {
				// this is done to remove the channel from its pool (static
				// vector)
				if (channel != null) {
					channel.disconnect();
				}
			} catch (Exception je) {
				// do nothing
			}
			try {
				channel = session.openChannel("exec");

				boolean superUserProvided = this.superUserName != null && this.superUserName.trim().length() > 0;
				if (superUserProvided) {
					((ChannelExec) channel).setPty(true);
					((ChannelExec) channel).setCommand("su " + this.superUserName + " -c \"" + cmd + "\" -s /bin/sh");
				} else {
					((ChannelExec) channel).setCommand(cmd);
				}
				((ChannelExec) channel).setEnv("PATH", "$PATH:/usr/sbin:/usr/bin:/usr/local/bin:");
				((ChannelExec) channel).setEnv("TERM", "ansi");

				out = channel.getOutputStream();
				in = channel.getInputStream();
				lineReader = new BufferedReader(new InputStreamReader(in));
				errLineReader = new BufferedReader(new InputStreamReader(channel.getExtInputStream()));

				channel.connect();
				if (superUserProvided) {
					byte[] tmp = new byte[512];
					in.read(tmp, 0, tmp.length);
					super.send(this.superUserPassword);
				}

				try {
					String sshWait = System.getProperty("ssh.wait", "500");
					long lWait = 500;
					try {
						lWait = Long.parseLong(sshWait);
					} catch (Exception exx) {

					}
					Thread.sleep(lWait);
				} catch (Exception e) {
					// DO NOTHING
				}
			} catch (JSchException je) {
				throw new IOException(je.getMessage());
			}
		}
	}

	/*
	 * protected String waitfor(final String[] searchElements, final boolean
	 * bApplyTimeOut) throws IOException { System.out.println(">>>waitfor<<<" +
	 * cmd); if (iTargetOSType == IOSProtocolConstants.AIX) { return
	 * super.waitfor(searchElements, bApplyTimeOut); } final StringBuffer ret =
	 * new StringBuffer(); String line = readLine(); while (line != null) {
	 * //System.out.println(line); ret.append(line); ret.append("\n"); line =
	 * readLine(); } return ret.toString(); }
	 */
	protected String waitfor(String[] searchElements, boolean bApplyTimeOut) throws IOException {
		final StringBuffer ret = new StringBuffer();
		if (iTargetOSType == IOSProtocolConstants.AIX) {
			ScriptHandler[] handlers = new ScriptHandler[searchElements.length];
			for (int i = 0; i < searchElements.length; i++) {
				// initialize the handlers
				handlers[i] = new ScriptHandler();
				handlers[i].setup(searchElements[i]);
			}
			String line = lineReader.readLine();
			byte[] b;
			while (line != null) {
				ret.append(line);
				ret.append("\n");
				for (int i = 0; i < handlers.length; i++) {
					b = line.getBytes();
					if (handlers[i].match(b, b.length)) {
						// System.out.println(">>>>>>"+ret.toString()+"\n<<<");
						return ret.toString();
					} // if
				} // for
				if (!lineReader.ready()) {
					break;
				}
				line = lineReader.readLine();
			}
		} else {
			String line = readLine();
			while (line != null) {
				ret.append(line);
				ret.append("\n");
				line = readLine();
			}
		}
		return ret.toString();
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public String getTopOutputforMac() throws IOException {
		final NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());

		String sLine = null;
		final StringBuffer ret = new StringBuffer();
		String temp;
		int numProcess = 0;
		boolean bProcesses = false;

		while (true) {
			if (bProcesses && (numProcess <= 0)) {
				break;
			}
			if (bProcesses) {
				--numProcess;
			}
			sLine = readLine();
			if (sLine == null) {
				break;
			}
			ret.append(sLine);
			ret.append("\n");
			if (sLine.startsWith("Processes:")) {
				final StringTokenizer line = new StringTokenizer(sLine, " ");
				temp = line.nextToken(); // Ignore Being Processes:
				temp = line.nextToken();
				temp = temp.trim();
				try {
					numProcess = nf.parse(temp).intValue();
				} catch (final Exception e) {
					numProcess = -1;
				}
			}
			if (sLine.indexOf("PID") != -1 && sLine.indexOf("COMMAND") != -1) {
				bProcesses = true;

			}
		} // while
		return ret.toString();
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public String getTopOutputForLinux() throws IOException {
		final NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());

		String sLine = null;
		final StringBuffer ret = new StringBuffer();
		String temp;
		int numProcess = 0;
		boolean bProcesses = false;
		boolean iterate = true;
		boolean once = false;
		while (iterate) {
			if (bProcesses && (numProcess <= 0)) {
				break;
			}
			if (bProcesses) {
				--numProcess;
			}
			sLine = readLine();
			if (sLine == null) {
				break;
			}
			ret.append(sLine);
			ret.append("\n");
			if ((sLine.indexOf("processes:") > 0) || (sLine.indexOf("Tasks:") == 0)) {
				if (once) {
					iterate = false;
				} else {
					once = true;
				}
				final StringTokenizer line = new StringTokenizer(sLine, " ");
				temp = line.nextToken(); // read total number of process
				if (sLine.indexOf("Tasks:") == 0) {
					temp = line.nextToken(); // read total number of process
				}
				temp = temp.trim();
				try {
					numProcess = nf.parse(temp).intValue();
				} catch (final Exception e) {
					numProcess = -1;
				}
			}
			if (sLine.indexOf("PID") != -1 && sLine.indexOf("USER") != -1) {
				bProcesses = true;
			}
		} // while
		return ret.toString();
	}

	/**
	 * @return
	 */
	public String getIostatOutputForSolaris() throws IOException {
		String sLine = null;
		final StringBuffer ret = new StringBuffer();
		// boolean bFound = false;
		/*
		 * device r/s w/s kr/s kw/s wait actv svc_t %w %b dad0 1.4 2.7 20.9 45.2
		 * 0.4 0.1 117.1 2 5 fd0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0 0 sd0 0.0 0.0 0.0
		 * 0.0 0.0 0.0 0.0 0 0 nfs1 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0 0
		 */
		while (true) {
			sLine = readLine();
			if (sLine == null || sLine.indexOf("extended device statistics") != -1) {
				break;
			}
			ret.append(sLine);
			ret.append("\n");
		} // while
		return ret.toString();
	}

	/**
	 * @return
	 */
	public String getIostatOutputForLinux(boolean nfs) throws IOException {
		String sLine = null;
		final StringBuffer ret = new StringBuffer();
		int deviceCount = 0;
		boolean bEmpty = false;
		int expectedDeviceCount = nfs ? 2 : 1;

		while (true) {
			if (deviceCount >= expectedDeviceCount && bEmpty) {
				break;
			}
			sLine = readLine();
			if (sLine == null) {
				break;
			}
			if (sLine.indexOf("command not found") != -1) {
				ret.append(sLine);
				break;
			}
			if ((sLine.length() == 0) && deviceCount >= expectedDeviceCount) {
				bEmpty = true;
			}
			if (sLine.indexOf("Device:") != -1) {
				if (deviceCount >= expectedDeviceCount) {
					bEmpty = true;
					continue;
				}
				deviceCount++;
			}
			if (deviceCount <= expectedDeviceCount) {
				ret.append(sLine);
				ret.append("\n");
			}
		} // while
		return ret.toString();
	}
}
