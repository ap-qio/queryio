package com.queryio.sysmoncommon.sysmon.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import com.queryio.common.util.AppLogger;

/* This is a wrapper class for TelnetClient. Provides convenience method like
 *login and execute on top of basic functionality provided by TelnetClient
 */
public class ShellWrapper extends AbstractProtocolWrapper {
	private Process process;
	private boolean connected = false;

	/**
	 * @throws Exception
	 */
	public ShellWrapper(final int targetOsType) {
		super(null, null, null, -1, targetOsType);
	}

	/**
	 * method connect Open a telnet connection to specified host and port
	 * 
	 * @throws IOException
	 */
	public boolean connect() throws IOException {
		try {
			this.process = Runtime.getRuntime().exec("sh");
		} catch (final IOException ex) {
			this.process = Runtime.getRuntime().exec("/bin/sh");
		}
		try {
			this.in = this.process.getInputStream();
			this.out = this.process.getOutputStream();
			this.errLineReader = new BufferedReader(new InputStreamReader(this.process.getErrorStream()));

			this.lineReader = new BufferedReader(new InputStreamReader(this.in));

			this.execute("PS1=\"" + AbstractProtocolWrapper.APPPERFECT_PROMPT + "\"", false);
			this.execute("PATH=$PATH:/usr/sbin:/usr/bin:/usr/local/bin:", false);
			this.execute("export PATH", false);
			this.execute("export TERM=ansi", false);

			this.connected = true;

			return true;
		} catch (final IOException e) {
			this.disconnect();
			throw e;
		}
	}

	public boolean isConnected() {
		return this.connected;
	}

	public boolean login() throws IOException {
		return this.connected;
	}

	/**
	 * method disconnect
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		if (this.process != null) {
			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Disconnecting...");
			this.connected = false;

			try {
				Thread th = new Thread() {
					public void run() {
						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Closing line reader");
						try {
							lineReader.close();
						} catch (IOException e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}

						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Closing input stream");
						try {
							in.close();
						} catch (IOException e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}

						if (AppLogger.getLogger().isDebugEnabled())
							AppLogger.getLogger().debug("Closing output stream");
						try {
							out.close();
						} catch (IOException e) {
							AppLogger.getLogger().fatal(e.getMessage(), e);
						}
					}
				};

				th.start();

				try {
					th.join(3000);
				} catch (Exception e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}

				if (th.isAlive()) {
					AppLogger.getLogger().fatal("Disconnect thread timed out...Interrupting disconnect thread");
					th.interrupt();
				}
			} finally {
				if (AppLogger.getLogger().isDebugEnabled())
					AppLogger.getLogger().debug("Destroying process");
				this.process.destroy();
			}

			if (AppLogger.getLogger().isDebugEnabled())
				AppLogger.getLogger().debug("Disconnect complete");
		}
	}

	/**
	 * method execute
	 * 
	 * @param cmd
	 * @return
	 * @throws IOException
	 */
	public String execute(final String cmd, boolean bApplyTimeOut) throws IOException {
		this.send(cmd);

		String result = null;
		if (bApplyTimeOut) {
			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("echo $PS1: " +
			// AbstractProtocolWrapper.APPPERFECT_PROMPT);
			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("cmd: " + cmd);
			this.send("echo $PS1");
			result = this.waitfor(AbstractProtocolWrapper.APPPERFECT_PROMPT, bApplyTimeOut);
		}
		// if(AppLogger.getLogger().isDebugEnabled())
		// AppLogger.getLogger().debug("cmd: " + cmd + " bApplyTimeOut: " +
		// bApplyTimeOut);
		return result;
	}

	/**
	 * Wait for a string to come from the remote host and return all that
	 * characters that are received until that happens (including the string
	 * being waited for).
	 * 
	 * @param match
	 *            the string to look for
	 * @return skipped characters
	 */
	/*
	 * private String waitfor(String[] searchElements) throws IOException {
	 * ScriptHandler[] handlers = new ScriptHandler[searchElements.length]; for
	 * ( int i = 0; i < searchElements.length; i++ ) { // initialize the
	 * handlers handlers[i] = new ScriptHandler(); handlers[i].setup(
	 * searchElements[i] ); }
	 * 
	 * byte[] b = new byte[512]; int n = 0; StringBuffer ret = new
	 * StringBuffer(); String current;
	 * 
	 * while(n >= 0) { { n = in.read(b); if(n > 0) { for ( int i = 0; i <
	 * handlers.length ; i++ ) { if ( handlers[i].match( b, n ) ) { int av =
	 * in.available(); if(av != 0) { n = in.read(b); } return ret.toString(); }
	 * // if } // for current = new String( b, 0, n ); ret.append( current ); }
	 * // if } } return null; // should never happen }
	 */

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

			// if(AppLogger.getLogger().isDebugEnabled())
			// AppLogger.getLogger().debug("sLine: " + sLine);
			//
			if (sLine == null) {
				break;
			}

			ret.append(sLine);
			ret.append("\n");
			if (sLine.startsWith("Processes:")) {

				final StringTokenizer line = new StringTokenizer(sLine);
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
			sLine = readLine();
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
	 * @return
	 * @throws IOException
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
			if (sLine.indexOf("extended device statistics") != -1) {
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
			sLine = this.readLineFromInputStream();
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

	private String readLineFromInputStream() throws IOException {
		final int ONE_SECOND = 1000;
		final int timeout = (int) (0.5 * ONE_SECOND); // in milliseconds
		final long startTime = System.currentTimeMillis();
		long currentTime = 0;

		int ch = -1;
		final StringBuffer sbLine = new StringBuffer();
		final int MAX = 512;
		final byte b[] = new byte[MAX];
		int count = 0;
		while (true) {
			final int av = this.in.available();
			if (av != 0) {
				ch = this.in.read();
				if ((ch == -1) || (ch == '\n')) {
					break;
				}
				b[count++] = (byte) ch;
				if (count == MAX) {
					sbLine.append(new String(b, 0, count));
					count = 0;
				}
			} else {
				currentTime = System.currentTimeMillis();
				if ((currentTime - startTime) <= timeout) {
					try {
						Thread.sleep(ONE_SECOND);
					} catch (final InterruptedException e) {
						// SUPRESS EXCEPTION
					}
				} else {
					// timed out
					if ((sbLine.length() == 0) && (count == 0)) {
						return null;
					}
					break;
				}
			}
		}
		if (count > 0) {
			sbLine.append(new String(b, 0, count));
		}
		return sbLine.toString();
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