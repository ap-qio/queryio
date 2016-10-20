package com.queryio.proc.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.hadoop.conf.Configuration;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.util.AppLogger;
import com.queryio.common.util.StartupParameters;
import com.queryio.common.util.StreamPumper;

public class ProcManager 
{
	public static final String OS_NAME = System.getProperty("os.name"); //$NON-NLS-1$
	
	public static String INSTALL_DIR = "";
	public static String STATUS_FILE = "";
	public static String MONITOR_CONFIG_FILE = "";
	public static String APP_HOME = "";
	
	public static String COMMAND_START = "0-59 * * * * sh ";
	public static String SCRIPT_FILE_NAME = "AgentMonitor.sh";
	public static String SET_CRONTAB_SCRIPT_FILE_NAME = "SetCrontab.sh";
	public static String LOG_FILE_NAME = "AgentStatus.log";
	
	public static String startUpCommand = COMMAND_START + APP_HOME + SCRIPT_FILE_NAME + " > " + APP_HOME + LOG_FILE_NAME;
	
	
	public static String getInstallDir()
	{
		return INSTALL_DIR;
	}
	
	public static String getStatusFile()
	{
		return STATUS_FILE;
	}
	
	public static String getMonitorConfigFile()
	{
		return MONITOR_CONFIG_FILE;
	}
	
		
	public static String getUserName()
	{
		String cmd = "whoami";
		
		BufferedReader stdInput = null;
//		String procPID = QueryIOConstants.EMPTY_STRING;
		try
		{
			Process p = Runtime.getRuntime().exec(cmd);
		
			stdInput = new BufferedReader(new 
			InputStreamReader(p.getInputStream()));
	
			String s = null;
			if ((s = stdInput.readLine()) != null) 
			{
				return s;
			}
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		finally
		{
			try
			{
				if(stdInput!=null)	stdInput.close();
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		
		return null;
	}
	
	public static String readCrontabFile() throws IOException
	{
		String cmd = "crontab -l";
		
		BufferedReader stdInput = null;
		String content = "";
		
		try
		{
			Process p = Runtime.getRuntime().exec(cmd);
			
			stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	
			String s = null;
			while ((s = stdInput.readLine()) != null) 
			{
				content += s;
			}
		}
		finally
		{
			try
			{
				if(stdInput!=null)	stdInput.close();
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		
		return content;
	}
	
	public static String execSetCrontabScript() throws IOException
	{
		String cmd = "sh " + getInstallDir() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/bin/" + SET_CRONTAB_SCRIPT_FILE_NAME;
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("execSetCrontabScript cmd: " + cmd);
		
		BufferedReader stdInput = null;
	
		String content = "";
		try
		{
			Process p = Runtime.getRuntime().exec(cmd);
		
			stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	
			String s = null;
			while ((s = stdInput.readLine()) != null) 
			{
				content += s;
			}
		}
		finally
		{
			try
			{
				if(stdInput!=null)	stdInput.close();
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		
		return content;
	}
	
	public static String getCrontabFileContentWithoutAgentCommand() throws IOException
	{
		// Exclude previous agent monitor command from the content.
		
		String cmd = "crontab -l";
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("getCrontabFileContent cmd: " + cmd);
		
		BufferedReader stdInput = null;
	
		String content = "";
		try
		{
			Process p = Runtime.getRuntime().exec(cmd);
		
			stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	
			String s = null;
			while ((s = stdInput.readLine()) != null) 
			{
				if(s.equals(ProcManager.getStartUpCommandWithoutInterval()))		continue;
				
				content += s + "\n";
			}
		}
		finally
		{
			try
			{
				if(stdInput!=null)	stdInput.close();
			}
			catch(Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
		
		return content;
	}
	
	public static void clearCrontabFile() throws IOException
	{
		String cmd = "crontab -r";
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("clearCrontabFile cmd: " + cmd);
		
		/*Process p = */Runtime.getRuntime().exec(cmd);
	}
	
	public static void updateCrontabFile(String startUpCommand) throws IOException
	{
		// TODO: Add handling for windows
//		if(true) return;	// FIXME: Remove me later
		
		File file = new File(getInstallDir() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/bin/" + SCRIPT_FILE_NAME);
		if(!file.exists())	createAgentStatusScriptFile();
		
		String content = readCrontabFile();
		
		if(content.contains(startUpCommand))	return;
		
		File configFile = new File(getInstallDir() + QueryIOConstants.HADOOP_DIR_NAME + "/etc/namenode_conf/topologyConfig.sh");
		configFile.setExecutable(true);
		configFile.setReadable(true);
		configFile.setWritable(true);
		
		content = getCrontabFileContentWithoutAgentCommand();
		ProcManager.clearCrontabFile();
		
		content += "\n";
		content += startUpCommand;
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Crontab file content: " + content);
		
		file = new File(getInstallDir() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/bin/" + SET_CRONTAB_SCRIPT_FILE_NAME);
		file.delete();
		
		ProcManager.createSetCrontabScriptFile(content);
		
		String response = execSetCrontabScript();
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Response from set crontab command: " + response);
	}
	
	public static void createAgentStatusScriptFile() throws IOException
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("createAgentStatusScriptFile() called");
		
		BufferedWriter out = null;
		
		try
		{
			FileWriter fstream = new FileWriter(getInstallDir() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/bin/" + SCRIPT_FILE_NAME);
			out = new BufferedWriter(fstream);
			
			StringBuffer sBuf = new StringBuffer();
			sBuf.append("#!/bin/sh");
			sBuf.append("\n");
			
			//to get the environment variables in crontab 
			sBuf.append(". /etc/profile");
			sBuf.append("\n");
			
			sBuf.append("SERVICE='com.queryio.agent.core.server.QueryIOAgent'");
			sBuf.append("\n");
			sBuf.append("now=$(date);");
			sBuf.append("\n");
			sBuf.append("if ps ax | grep -v grep | grep $SERVICE | grep \"java\" > /dev/null");
			sBuf.append("\n");
			sBuf.append("then");
			sBuf.append("\n");
			sBuf.append("echo \"$SERVICE service running, everything is fine , Monitor Time: $now\"");
			sBuf.append("\n");
			sBuf.append("else");
			sBuf.append("\n");
			sBuf.append("echo \"$SERVICE is not running...Starting $SERVICE , Monitor Time: $now\";");
			sBuf.append("\n");
			sBuf.append("cd $1");
			sBuf.append("\n");
			sBuf.append("sh start_queryIOAgent.sh " + getInstallDir() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + " " + System.getProperty(QueryIOConstants.QUERYIOAGENT_PORT));
			sBuf.append("\n");
			sBuf.append("fi");
			
			out.write(sBuf.toString());
		}
		finally
		{
			try
			{
				if(out!=null)	out.close();
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}

	public static void deleteSetCrontabScriptFile()
	{
		File file = new File(getInstallDir() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/bin/" + SET_CRONTAB_SCRIPT_FILE_NAME);
		file.delete();
	}
	
	public static void createSetCrontabScriptFile(String startUpCommand) throws IOException
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("createSetCrontabScriptFile() called");
		
		BufferedWriter out = null;
		
		try
		{
			FileWriter fstream = new FileWriter(getInstallDir() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/bin/" + SET_CRONTAB_SCRIPT_FILE_NAME);
			out = new BufferedWriter(fstream);
		
			StringBuffer sBuf = new StringBuffer();
			sBuf.append("#!/bin/sh");
			sBuf.append("\n");
			sBuf.append("EDITOR=ed");
			sBuf.append("\n");
			sBuf.append("export EDITOR");
			sBuf.append("\n");
			sBuf.append("crontab -e << EOF > /dev/null");
			sBuf.append("\n");
			sBuf.append("a");
			sBuf.append("\n");
			sBuf.append(startUpCommand);
			sBuf.append("\n");
			sBuf.append(".");
			sBuf.append("\n");
			sBuf.append("w");
			sBuf.append("\n");
			sBuf.append("q");
			sBuf.append("\n");
			sBuf.append("EOF");
			sBuf.append("\n");
			sBuf.append("echo \"Crontab Entry Inserted Successfully\"");
			
			out.write(sBuf.toString());
		}
		finally
		{
			try
			{
				if(out!=null)	out.close();
			}
			catch (Exception e)
			{
				AppLogger.getLogger().fatal(e.getMessage(), e);
			}
		}
	}
	
	public static void addAgentStartUpMonitor() throws IOException
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Will fetch startUpCommand now.");
		String startUpCommand = ProcManager.getStartupCommand();
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("startUpCommand: " + startUpCommand);
		
		updateCrontabFile(startUpCommand);
	}
	
		
	public static boolean updateMonitorIntervalInFile(String key, int value)
	{
		boolean success = false;
		
		Configuration conf = new Configuration(false);
		
		String fileName = ProcManager.getMonitorConfigFile();
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try 
		{
			fis = new FileInputStream(new File(fileName));
			conf.addResource(fis);
			
			conf.setInt(key, value);
			
			fos = new FileOutputStream(new File(fileName)); 
			conf.writeXml(fos);
			
			success = true;
		} 
		catch(Exception ex)
		{
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
		}
		finally
		{
			try
			{
				if(fos != null)
					fos.close();				
			}
			catch (IOException e) 
			{
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
			try
			{
				if(fis != null)
					fos.close();				
			}
			catch (IOException e) 
			{
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
		}
		
		return success;
	}
	
	public static int getMonitorInterval(String key)
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("getMonitorIntervalMethod, with key :" + key);
				
		Configuration conf = new Configuration(false);
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Mark : 1");
		String fileName = getMonitorConfigFile();
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Mark : 2");
		FileInputStream fis = null;
//		String value = null;
		try 
		{
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Mark : 3");
			fis = new FileInputStream(new File(fileName));
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Mark : 4");
			conf.addResource(fis);		
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Mark : 5");
			if(conf.get(key)==null)	return -1;
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Mark : 6");
			
			return Integer.parseInt(conf.get(key));
		} 
		catch(Exception ex)
		{
			AppLogger.getLogger().fatal(ex.getMessage(), ex);
		}
		finally
		{
			try
			{	
				if(fis != null)
					fis.close();				
			}
			catch (IOException e) 
			{
				AppLogger.getLogger().fatal("Error closing steeam", e);
			}
		}
		
		return -1;
	}
	
	public static int getAgentMonitorIntervalInMinutes()
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("fetching agent monitoring intervalImpl");
		int monitorInterval = getMonitorInterval(QueryIOConstants.CONTROLLER_AGENT_MONITOR_INTERVAL_KEY);
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("fetched agent monitoring intervalImpl");
		return monitorInterval != -1 ? monitorInterval : QueryIOConstants.DEFAULT_AGENT_MONITOR_INTERVAL;
	}
	
	public static boolean setAgentMonitorInterval(int intervalInMinutes) throws IOException
	{
		boolean success = updateMonitorIntervalInFile(QueryIOConstants.CONTROLLER_AGENT_MONITOR_INTERVAL_KEY, intervalInMinutes);
		
		updateCrontabFile(ProcManager.getStartupCommand());
	
		return success;
	}
	
	public static String getStartupCommand()
	{
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("fetching agent monitoring interval");
		int agentMonitorInterval = ProcManager.getAgentMonitorIntervalInMinutes();
	
		agentMonitorInterval = agentMonitorInterval % 60;
		String agentProt = System.getProperty(QueryIOConstants.QUERYIOAGENT_PORT);

		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Agent Monitor Interval: " + agentMonitorInterval);
		
		String cmdStart = null;
		
		if(agentMonitorInterval!=0)
		{
			StringBuffer sBuf = new StringBuffer();
			for(int i=0; i<60; i+=agentMonitorInterval)
			{
				sBuf.append(i);
				sBuf.append(",");
			}
			
			cmdStart = sBuf.toString();
			
			if(cmdStart.lastIndexOf(",") == (cmdStart.length()-1))	cmdStart = cmdStart.substring(0, cmdStart.length()-1);
			
			if(cmdStart.equals(""))	cmdStart = "0-59";
		}
		else
		{
			cmdStart = "0-59";
		}
		
		cmdStart = cmdStart + " * * * * sh ";
		
		String startUpCommand = cmdStart + getInstallDir() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/bin/" + SCRIPT_FILE_NAME + " " + getInstallDir() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/bin/ " + agentProt + " > " + APP_HOME + LOG_FILE_NAME;
		
		if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("StartUp Command: " + startUpCommand);
		
		return startUpCommand;
	}
	
	public static String getStartUpCommandWithoutInterval()
	{
		return getInstallDir() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/bin/" + SCRIPT_FILE_NAME + " " + getInstallDir() + QueryIOConstants.QUERYIOAGENT_DIR_NAME + "/bin/ > " + APP_HOME + LOG_FILE_NAME;
	}
	
	public static String getPID(String procName){
        if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Fetching PID for process : " + procName);
        String procPID = QueryIOConstants.EMPTY_STRING;
        StringWriter inputWriter = null;
        Process process = null;
        try
        {                
            String cmd = "ps -ef";
            
            process = Runtime.getRuntime().exec(cmd);                            

            inputWriter = new StringWriter();
			final StreamPumper spInput = new StreamPumper(new BufferedReader(new InputStreamReader(process
					.getInputStream())), inputWriter);
			spInput.start();
			    			
			process.waitFor();
			int count = 0;
			while (!spInput.isProcessCompleted() && (count < 5))
			{
				Thread.sleep(100);
				count++;
			}
            String[] splits = inputWriter.toString().split(QueryIOConstants.NEW_LINE);
            
            for(int ctr=0; ctr<splits.length; ctr++){
            		if(! splits[ctr].contains(procName))    continue;
                
                splits[ctr] = splits[ctr].trim();
                
                int i=0;
                int col=0;
                boolean space = false;
                while(i<splits[ctr].length())
                {
                    if(splits[ctr].charAt(i)==' ')    space = true;
                    else if(space==true)
                    {
                        space = false;
                        col++;
                    }
                    if(col==1 && !space)
                    {
                        procPID += splits[ctr].charAt(i);
                    }
                    i++;
                }
                procPID += " ";
            }
           
        }catch(Exception e){
        	AppLogger.getLogger().fatal(e.getMessage(), e);
        }
		finally
        {
            if (inputWriter != null)
				try {
					inputWriter.close();
				} catch (IOException e) {
					// Ignore
				}
            if(process != null)
            	process.destroy();
        }
        
        return procPID;
    }
}