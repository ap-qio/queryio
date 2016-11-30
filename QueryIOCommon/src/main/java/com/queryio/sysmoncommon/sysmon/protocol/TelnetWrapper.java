/*
 * @(#)  TelnetWrapper.java
 *
 * Copyright (C) 2002 Exceed Consultancy Services. All Rights Reserved.
 *
 * This software is proprietary information of Exceed Consultancy Services and
 * constitutes valuable trade secrets of Exceed Consultancy Services. You shall
 * not disclose this information and shall use it only in accordance with the
 * terms of License.
 *
 * EXCEED CONSULTANCY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EXCEED CONSULTANCY SERVICES SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.queryio.sysmoncommon.sysmon.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.net.telnet.TelnetClient;

import com.queryio.common.IOSProtocolConstants;

/**
 * This is a wrapper class for TelnetClient. Provides convenience method like
 * login and execute on top of basic functionality provided by TelnetClient
 * 
 * IMP - By default, the Telnet service supplied with Windows 2000 requires NTLM
 * authentication. However, if Windows 2000 is configured to use Kerberos as its
 * default authentication method, then Telnet users are not able to obtain
 * access to domain/AD resources including network validation. To allow clear
 * text passwords ala unix: Run tlntadmn.exe Select Display / change registry
 * settings Select NTLM Change the default setting from 2 to 0 to disable the
 * NTLM requirement To start the telnet server, at the commandline:
 * 
 * net start tlntsvr
 * 
 * @author Exceed Consultancy Services
 */
public class TelnetWrapper extends AbstractProtocolWrapper {
	private final TelnetClient telnet;

	PipedInputStream processInputStream;
	PipedOutputStream processOutputStream;

	/**
	 * @param targetOsType
	 */
	public TelnetWrapper(final String hostName, final String userName, final String password, final int targetOsType) {
		this(hostName, userName, password, IOSProtocolConstants.DEFAULT_TELNET_PORT, targetOsType);
	}

	/**
	 * @param targetOsType
	 */
	public TelnetWrapper(final String hostName, final String userName, final String password, final int portId,
			final int targetOsType) {
		super(hostName, userName, password, (portId != -1 ? portId : IOSProtocolConstants.DEFAULT_TELNET_PORT),
				targetOsType);
		this.telnet = new TelnetClient();
	}

	/**
	 * Open a telnet connection to specified host and port
	 * 
	 * @param host
	 *            IP address of target machine
	 * @param port
	 *            port number of telnet server
	 */
	public boolean connect() throws IOException {
		this.telnet.connect(this.hostName, this.portId);
		this.in = this.telnet.getInputStream();
		this.out = this.telnet.getOutputStream();
		this.lineReader = new BufferedReader(new InputStreamReader(this.in));

		if (this.iTargetOSType == IOSProtocolConstants.WINDOWS) {
			this.processInputStream = new PipedInputStream();
			this.processOutputStream = new PipedOutputStream(this.processInputStream);
		}
		return true;
	}

	public boolean isConnected() {
		return (this.telnet != null) && this.telnet.isConnected();
	}

	/**
	 * Disconnect the telnet connection
	 */
	public void disconnect() throws IOException {
		if ((this.telnet != null) && this.telnet.isConnected()) {
			this.in.close();
			this.out.close();
			this.lineReader.close(); // IMP- close lineReader after
			// in.close() or system hangs
			this.telnet.disconnect();
		}
	}

	/**
	 * Login into remote host. This is a convenience method and only works if
	 * the prompts are "login:" and "Password:".
	 * 
	 * @param user
	 *            the user name
	 * @param pwd
	 *            the password
	 */
	public boolean login() throws IOException {
		// System.out.println("telnet wrapper : login()");
		if (this.waitfor("login:", true) == null) {
			// this can happen in case of windows target machine
			// if "Server allows NTLM authentication only
			// Server has closed connection"
			return false;
		}
		// System.out.println("*******8telnet wrapper : sending user name");
		this.send(this.userName);

		// System.out.println("*******8telnet wrapper : waiting for password");
		if (this.waitfor(this.iTargetOSType == IOSProtocolConstants.WINDOWS ? "password" : "Password:", true) == null) {
			// This can happen if "iTargetOSType" is not maching with the actual
			// target OS
			return false;
		}
		// System.out.println("*******8telnet wrapper : sending password");
		this.send(this.password);

		int av = this.in.available();
		int count = 0;
		while (av == 0) {
			if (count <= NLOOP) {
				count++;
			} else {
				return false;
			}
			try {
				Thread.sleep(this.iWaitTime / NLOOP);
			} catch (final InterruptedException e) {
				// SUPRESS EXCEPTION
			}
			av = this.in.available();
		}

		byte[] b = null;
		String value = null;
		if (av > 0) {
			b = new byte[av];
			av = this.in.available();
			this.in.read(b, 0, av);
			value = new String(b);
			if (this.iTargetOSType == IOSProtocolConstants.WINDOWS) {
				if (value.indexOf("C:\\") != -1) {
					this.execute("prompt " + APPPERFECT_PROMPT_WINDOWS, true);
					this.execute("set PATH=%PATH%", true);
					return true;
				}
			} else {
				if (value.toLowerCase().indexOf("last login") != -1) {
					this.execute("PS1=\"" + APPPERFECT_PROMPT + "\"", true);
					this.execute("PS1=\"" + APPPERFECT_PROMPT + "\"", true);
					this.execute("PATH=$PATH:/usr/sbin:/usr/bin:/usr/local/bin:", true);
					this.execute("export PATH", true);
					return true;
				}
			}
		}
		return false;
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
			if (bProcesses && (numProcess < 0)) {
				break;
			}
			if (bProcesses) {
				--numProcess;
			}
			sLine = this.lineReader.readLine();
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

		while (true) {
			if (bProcesses && (numProcess <= 0)) {
				break;
			}
			if (bProcesses) {
				--numProcess;
			}
			sLine = this.lineReader.readLine();
			if (sLine == null) {
				break;
			}
			ret.append(sLine);
			ret.append("\n");
			if (sLine.indexOf("processes:") > 0) {
				final StringTokenizer line = new StringTokenizer(sLine, " ");
				temp = line.nextToken(); // read total number of process
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
	 * method getDummyProcess
	 * 
	 * @return
	 */
	public Process getDummyProcess() {
		if (this.iTargetOSType == IOSProtocolConstants.WINDOWS) {
			// The output of the process(created through a telnet wrapper) has
			// junk characters as the
			// telnet to windows sends console escape characters in ASCII which
			// have a pattern like
			// "?[*;*H". for ex. "[14;12H"catalina_AppPerfect_jp.bat"
			// run[15;1HUsing CATALINA_BASE:"
			// So there is a need for removing these junk characters to show a
			// clean output with new line
			// The second problem with telnet is that data comes repeatedly by
			// getting prepended with latest output.

			new Thread() {
				public void run() {
					try {
						final StringBuffer buffer = new StringBuffer();
						byte[] data = new byte[256];
						boolean bContinue = true;
						int bytesRead;
						String dataString = "";
						while (bContinue) {
							bytesRead = TelnetWrapper.this.in.read(data);
							if (bytesRead != -1) {
								dataString += new String(data, 0, bytesRead);
								if (dataString.length() > 64) {
									if (buffer.toString().indexOf(dataString) == -1) {
										buffer.append(dataString);
										// System.out.println("Original=" +
										// dataString);
										dataString = replaceAll(dataString, ".\\[.*;.*H", "\n");
										// System.out.println("Replaced=" +
										// dataString);
										data = dataString.getBytes();
										bytesRead = data.length;
										TelnetWrapper.this.processOutputStream.write(data, 0, bytesRead);
										sleep(150);
									}
									dataString = "";
								}
							} else {
								bContinue = false;
							}
						}
					} catch (final Exception e) {
						// TODO log
					}
				}
			}.start();
		}
		return new Process() {
			public void destroy() {
				try {
					TelnetWrapper.this.disconnect();
				} catch (final Exception e) {
					// e.printStackTrace();
				}
			}

			public int exitValue() {
				return 0;
			}

			public InputStream getErrorStream() {
				return null;
			}

			public InputStream getInputStream() {
				return TelnetWrapper.this.iTargetOSType == IOSProtocolConstants.WINDOWS
						? TelnetWrapper.this.processInputStream : TelnetWrapper.this.in;
			}

			public OutputStream getOutputStream() {
				return TelnetWrapper.this.out;
			}

			public int waitFor() {
				return 0;
			}
		};
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
			sLine = this.lineReader.readLine();
			if (sLine != null && sLine.indexOf("extended device statistics") != -1) {
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
			sLine = this.lineReader.readLine();
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

	public String getTopasOutputForAIX() throws IOException {
		String sLine = null;
		StringBuffer ret = new StringBuffer();
		while (true) {
			sLine = lineReader.readLine();
			ret.append(sLine);
			ret.append("\n");

			if (sLine != null && sLine.indexOf("quit") != -1) {
				break;
			}
		} // while
		return ret.toString();
	}
}