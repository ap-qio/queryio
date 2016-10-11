package com.queryio.sysmoncommon.sysmon.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.queryio.common.IOSProtocolConstants;

import com.queryio.sysmoncommon.sysmon.dstruct.LogInfo;

import jregex.Pattern;
import jregex.Replacer;

public abstract class AbstractProtocolWrapper
{
	public static final int BUFFER_SIZE = 512;
	
	protected BufferedReader lineReader;
	protected BufferedReader errLineReader;
	protected InputStream in;
	protected OutputStream out;
	protected int iWaitTime = 10000;
	protected int iTargetOSType = IOSProtocolConstants.LINUX;
	
	public static final String APPPERFECT_PROMPT = "APPPERFECT>";
	public static final String APPPERFECT_PROMPT_WINDOWS = "APPPERFECT$G";
	protected String[] matches = new String[] { APPPERFECT_PROMPT };
	protected static final int NLOOP = 10;
	
	protected String hostName;
	protected String userName;
	protected String password;
	protected int portId;
	
	protected String firewallUserName = "";
	protected String firewallPassword = "";
	protected String superUserName = "";
	protected String superUserPassword = "";
	
	/**
	 * @param targetOsType
	 */
	public AbstractProtocolWrapper(String hostName, String userName, String password, int portId, int iTargetOSType)
	{
		this.hostName = hostName;
		this.userName = userName;
		this.password = password;
		this.portId = portId;
		this.iTargetOSType = iTargetOSType;
	}

	public void setFirewallUserName(String firewallUserName)
	{
		this.firewallUserName = firewallUserName;
	}

	public void setFirewallPassword(String firewallPassword)
	{
		this.firewallPassword = firewallPassword;
	}

	public void setSuperUserName(String superUserName)
	{
		this.superUserName = superUserName;
	}

	public void setSuperUserPassword(String superUserPassword)
	{
		this.superUserPassword = superUserPassword;
	}

	public abstract boolean connect() throws IOException;

	public abstract boolean isConnected();

	public abstract void disconnect() throws IOException;

	public abstract boolean login() throws IOException;

	public abstract String getTopOutputforMac() throws IOException;

	public abstract String getTopOutputForLinux() throws IOException;
	
	public abstract String getIostatOutputForSolaris() throws IOException;

	public abstract String getIostatOutputForLinux(boolean nfs) throws IOException;	
	
	/**
	 * @param cmd
	 * @throws IOException
	 */
	public void send(String cmd) throws IOException
	{
//		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Sending command: " + cmd);
		out.write((cmd + "\n").getBytes());
		out.flush();
	}
	
	/**
	 * Send a command to the remote host and return the result of the command
	 */
	public String execute(String cmd, boolean bApplyTimeOut) throws IOException
	{
		send(cmd);
		return waitfor(matches, bApplyTimeOut);
	}	
	/**
	 * @param match
	 * @param bApplyTimeOut
	 * @return
	 * @throws IOException
	 */
	public final String waitfor(String match, boolean bApplyTimeOut) throws IOException
	{
		String[] matches = new String[1];
		matches[0] = match;

		return waitfor(matches, bApplyTimeOut);
	}
	/**
	 * method waitfor Wait for a string to come from the remote host and return
	 * all that characters that are received until that happens (including the
	 * string being waited for)
	 * 
	 * @param searchElements
	 * @param bApplyTimeOut
	 * @return
	 * @throws IOException
	 */
	protected String waitfor(String[] searchElements, boolean bApplyTimeOut)
			throws IOException
	{
		//System.out.println("started waitFor command ..."+cmd);
		ScriptHandler[] handlers = new ScriptHandler[searchElements.length];
		for (int i = 0; i < searchElements.length; i++)
		{
			// initialize the handlers
			handlers[i] = new ScriptHandler();
			handlers[i].setup(searchElements[i]);
		}
		byte[] b = new byte[512];
		int n = 0;
		StringBuffer ret = new StringBuffer();
		String current;
		int count = 0;
		while(n >= 0)
		{
			int av = in.available();
			if(av != 0)
			{
				n = in.read(b);
				if(n > 0)
				{
					current = new String( b, 0, n );
					ret.append( current );
					for ( int i = 0; i < handlers.length ; i++ )
					{
						if ( handlers[i].match( b, n ) )
						{
							return ret.toString();
						} // if
					} // for
				} // if
			}
			else if(bApplyTimeOut)
			{
				if(count <= NLOOP)
				{
					count++;
				}
				else
				{
					return null;	//process timed out
				}
				try 
				{
					//System.out.println("Going to sleep()");
					Thread.sleep(iWaitTime/NLOOP);
					//System.out.println("came out of sleep()");
				} 
				catch (InterruptedException e) 
				{
					// SUPRESS EXCEPTION
				}
			}		
		} // while
		return null; // should never happen
	}

	public void setWaitingTime(int timeInms)
	{
		iWaitTime = timeInms;
	}

	public Process getDummyProcess()
	{
		return null;
	}

	public static String replaceAll(String originalStr, String str1, String str2)
	{
		Pattern p = new Pattern(str1);
		Replacer r = p.replacer(str2);
		return r.replace(originalStr);
	}
	
	public String readLine() throws IOException
	{
		return (lineReader != null) ? lineReader.readLine():null;
	}
	
	private long searchInLogFileImpl(String command, String [] searchArray, long lastLine, LogInfo logInfo) throws Exception
	{
		long totalLines = lastLine;
		send(command);
		try
		{
			String sshWait = System.getProperty("ssh.wait", "500");
			long lWait = 500;
			try
			{
				lWait = Long.parseLong(sshWait);
			}
			catch (Exception exx)
			{
				
			}
			Thread.sleep(lWait);
		}
		catch(Exception e)
		{
			//DO NOTHING
		}
		String line = readLine();
		if (this.superUserName != null && line != null && line.trim().length() == 0)
		{
			line = readLine();
		}
		if (line == null && errLineReader != null && errLineReader.ready())
		{
			String errLine = errLineReader.readLine();
			if (errLine != null && errLine.indexOf("Permission denied") != -1)
			{
				throw new RuntimeException("Error reading file: " + errLine);
			}
		}
		while (line != null)
		{
			for (int i = 0; i < searchArray.length; i++) 
			{
				if(line.indexOf(searchArray[i]) != -1)
				{
					if (!logInfo.isFound())
					{
						logInfo.setFound(true);
						logInfo.setLineNum(lastLine);
						logInfo.setLine(line);
						logInfo.setSearchString(searchArray[i]);
					}
					logInfo.incrementMatchCount();
					logInfo.getMatchedItems().add(new String [] {String.valueOf(lastLine), searchArray[i], line});
					break;
				}
			}
			lastLine++;
			totalLines++;
			line = readLine();
		}
		logInfo.setLastLineRead(lastLine);
		return totalLines;
	}
	
	public LogInfo searchInLogFile(String fileName, String tailCommandPrefix, String [] searchArray, long lastLine) throws Exception
	{
		long prevLastLine = lastLine;
		final LogInfo logInfo = new LogInfo();
		final long lastLineFound = searchInLogFileImpl(tailCommandPrefix + lastLine + " " + fileName, searchArray, lastLine, logInfo);
		//System.out.println("Previous: " + prevLastLine + " new: " + lastLineFound);
		if (!logInfo.isFound() && prevLastLine == lastLineFound)
		{
			// does that mean there is no addition to the log file or the log file was deleted, hence it now 
			// has lesser no of lines?
			long newLastLineFound = -1;
			// Fire "wc -l fileName" command to get the line numbers in the file
			String result = execute("wc -l " + fileName, true);
			if (result != null)
			{
				result = result.trim();
				int spaceIndex = result.indexOf(' ');
				if (spaceIndex != -1)
				{
					try
					{
						newLastLineFound = Long.parseLong(result.substring(0, spaceIndex));
					}
					catch (Exception ex)
					{
						// do nothing
					}
				}
			}
			//System.out.println("Result: " + result + " new last line: " + newLastLineFound);
			if (newLastLineFound != -1 && newLastLineFound < (lastLineFound - 1))
			{
				final LogInfo checkLogInfo = new LogInfo();
				searchInLogFileImpl(tailCommandPrefix + "1 " + fileName, searchArray, 1, checkLogInfo);
				return checkLogInfo;
			}
		}
		return logInfo;
	}
	
	public BufferedReader getLineReader() 
	{
		return lineReader;
	}

	// this.LOGFILECOMMAND + lastLine + " " +
	/*
	// This method does not fire "tail" command again to check if file was reset etc. It assumes that the
	// file will not be deleted and lines will be added to it in append mode.
	public LogInfo searchInLogFile(String command, String [] searchArray, long lastLine) throws Exception
	{
		//long totalLines = lastLine;
		String sshWait = System.getProperty("ssh.wait", "500");
		long lWait = 500;
		try
		{
			lWait = Long.parseLong(sshWait);
		}
		catch (Exception exx)
		{
			
		}
		//AppLogger.getLogger().fatal(command);
		final LogInfo logInfo = new LogInfo();
		send(command);
		String line = readLine();
		while (line != null)
		{
			if (!logInfo.isFound())
			{
				for (int i = 0; i < searchArray.length; i++) 
				{
					if(line.indexOf(searchArray[i]) != -1)
					{
						logInfo.setFound(true);
						logInfo.setLineNum(lastLine);
						logInfo.setLine(line);
						logInfo.setSearchString(searchArray[i]);
						break;
					}
				}
				lastLine++;
			}
			line = readLine();
			if (line == null)
			{
				try
				{
					Thread.sleep(lWait);
				}
				catch (InterruptedException ie)
				{
					// do nothing
				}
				line = readLine();
				//AppLogger.getLogger().fatal("Tried once more, line count so far: " + totalLines);
			}
			//totalLines ++;
		}
		logInfo.setLastLineRead(lastLine);
		//AppLogger.getLogger().fatal("Last line: " + totalLines);
		return logInfo;
	}
	*/

}
