package com.queryio.sysmoncommon.sysmon;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import jregex.Matcher;
import jregex.Pattern;

import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NFSStat;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;

public abstract class AbstractDataParser
{
    protected static final Pattern DECIMAL_NUMBER_PATTERN = new Pattern("\\d+");
    protected static final Pattern WHITE_SPACE_PATTERN = new Pattern("\\s+");
    protected static final Pattern NEW_LINE_PATTERN = new Pattern("[\r\n\f]+"); //$NON-NLS-1$
    
	protected static final String NEW_LINE_TOKENS = "\r\n\f"; //$NON-NLS-1$
	
	protected MemoryInfo physicalMemInfo;
	protected MemoryInfo virtualMemInfo;
	
	protected LinkedList processInfoList;
	protected LinkedList networkInfoList;
	protected LinkedList diskInfoList;
	protected LinkedList diskSpaceInfoList;
	protected NFSStat nfsStats;
	
	protected float fCpuUsageTime;
	protected float fUserCPUUsageTime;
	protected float fSysCPUUsageTime;
	protected float fWaitCPUUsageTime;
	
	protected float f1MinLoadAvg;
	protected float f5MinLoadAvg;
	protected float f15MinLoadAvg;
	
	protected int noOfProcs = 0;
	protected int noOfCPUs = 1;
	protected NumberFormat nf = null;
	protected ProcessFilter processFilter = null;
	protected boolean monitorProcesses = true;
	
	// All implementor's should have a default constructor as its object is created by reflection using default constructor
	public AbstractDataParser()
	{
		this.nf = NumberFormat.getInstance(Locale.getDefault());
	}
	
	public void setProcessFilter(ProcessFilter processFilter) 
	{
		this.processFilter = processFilter;
	}	
	
	public void setMonitorProcesses(boolean monitorProcess) 
	{
		this.monitorProcesses = monitorProcess;
	}
	
	protected String [] getTokens(String line)
	{
		final StringTokenizer stk = new StringTokenizer(line);
		ArrayList list = new ArrayList(20);
		
		String value;
		while(stk.hasMoreTokens())
		{
			value = stk.nextToken().trim();
			if (value.length() > 0)
			{
				list.add(value);
			}
		}
		String [] tokens = new String[list.size()];
		list.toArray(tokens);
		return tokens;
	}
	
	protected long parsePSTime(String value) throws Exception
	{
		// format of value will be [[ dd-]hh:]mm:ss
		//String org = value;
		long time = 0;
		if(value.trim().length() > 0)
		{
			int days = 0;
			int hours = 0;
			int mins = 0;
			int seconds = 0;
			
			final int hyphenIndex = value.indexOf('-');
			if (hyphenIndex != -1)
			{
				days = nf.parse(value.substring(0, hyphenIndex)).intValue();
				value = value.substring(hyphenIndex + 1);
			}
			int firstColonIndex = value.indexOf(':');
			final int nextColonIndex = value.indexOf(':', firstColonIndex + 1);
			if (nextColonIndex != -1)
			{
				// hh is present.
				hours = nf.parse(value.substring(0, firstColonIndex)).intValue();
				value = value.substring(firstColonIndex + 1);
				firstColonIndex = value.indexOf(':');
			}
			mins = nf.parse(value.substring(0, firstColonIndex)).intValue();
			seconds = nf.parse(value.substring(firstColonIndex + 1)).intValue();
			
			//time = days * (24 * 60 * 60) + hours * (60 * 60) + mins * 60 + seconds;
			time = days * 86400 + hours * 3600 + mins * 60 + seconds;
			//System.out.println(org + "\tdays: " + days + " hours: " + hours + " mins: " + mins + " seconds: " + seconds + " >> so time: " + time);
		}
		return time;
	}
	

	
	//abstract methods
	public abstract void parseTopCommand(String output) throws ResultParsingException;
	public abstract void parseNetStatCommand(String output) throws ResultParsingException;
	public abstract void parseIOStatCommand(String output) throws ResultParsingException;
	public abstract void parseDFCommand(String output) throws ResultParsingException;
	//optional methods
	public abstract void parseTopMemCommand(String output) throws ResultParsingException;
	public abstract void parsePrstatCommand(String output) throws ResultParsingException;
	public abstract void parseVmstatCommand(String output) throws ResultParsingException;
	
	public MemoryInfo getPhysicalMemoryInfo()
	{
		if(physicalMemInfo == null)
		{
			physicalMemInfo = new MemoryInfo(0, 0, 0);
		}
		return physicalMemInfo;
	}
	
	public MemoryInfo getVirtualMemoryInfo()
	{
		if(virtualMemInfo == null)
		{
			virtualMemInfo = new MemoryInfo(0, 0, 0);
		}
		return virtualMemInfo;
	}
	
	public void resetProcessInfoList()
	{
		if (this.processInfoList == null)
		{
			this.processInfoList = new LinkedList();
		}
		else
		{
			this.processInfoList.clear();
		}
	}
	
	public ProcessInfo[] getProcessInfo()
	{
		if(processInfoList != null)
		{
			return (ProcessInfo[])processInfoList.toArray(new ProcessInfo[processInfoList.size()]);
		}
		return null;
	}	
	
	public final NetworkInfo[] getNetworkInfo()
	{
		if(networkInfoList != null)
		{
			NetworkInfo [] networkArray = new NetworkInfo[networkInfoList.size()];
			for (int i = 0; i < networkInfoList.size(); i++) {
				networkArray[i] = (NetworkInfo)networkInfoList.get(i);
			}
			return networkArray;
		}
		return null;
	}
	
	public DiskInfo[] getPhysicalDiskInfo()
	{
		if(diskInfoList != null)
		{
			return (DiskInfo[])diskInfoList.toArray(new DiskInfo[diskInfoList.size()]);
		}
		return null;		
	}
	
	public DriveInfo[] getDiskSpaceInfo()
	{
		if(diskSpaceInfoList != null)
		{
			return (DriveInfo[])diskSpaceInfoList.toArray(new DriveInfo[diskSpaceInfoList.size()]);
		}
		return null;
	}
	
	public int getCPUUsage()
	{
		return (int)fCpuUsageTime;
	}
	
	public int getUserCPUUsage()
	{
		return (int)fUserCPUUsageTime;
	}
	
	public float getSystemCPUUsage()
	{
		return fSysCPUUsageTime;
	}
	
	public int getWaitCPUUsage()
	{
		return (int)fWaitCPUUsageTime;
	}
	
	public int get1MinLoadAverage()
	{
		return (int)Math.round(f1MinLoadAvg);
	}	

	public int get5MinLoadAverage()
	{
		return (int)Math.round(f5MinLoadAvg);
	}	
	
	public int get15MinLoadAverage()
	{
		return (int)Math.round(f15MinLoadAvg);
	}		
	
	public int getNoOfProcessors() 
	{
		return noOfCPUs;
	}

	public List parseProcessIdList(String output) throws ResultParsingException
	{
		return new ArrayList();
	}

	public void parseLoadAverages(String output) throws ResultParsingException
	{
		//Empty implementation
	}

	public void parseProcStat(String output) throws ResultParsingException
	{
		//Empty implementation
	}

	public void parseProcMeminfo(String output) throws ResultParsingException
	{
		//Empty implementation
	}

	public void parseProcProcessInfo(String output, boolean psResult) throws ResultParsingException
	{
        //Empty implementation
    }
	
    public int getProcessCount()
    {
        return noOfProcs;
    }

    public void parseNSFStatCommand(String clientStats, String serverStats)
    {
        //Empty implementation
    }

    public NFSStat getNFSStat()
    {
        return nfsStats;
    }
    
    /******************************String pattern handling ***********************************************************/
    //Note: Following implementation is slightly different compared to implementation in StaticUtilities
    public static String[] split(final String input, final String pattern)
    {
        return split(input, new Pattern(pattern), 0);
    }
    
    public static String[] split(final String input, final Pattern pattern)
    {
        return split(input, pattern, 0);
    }
    
    public static String[] split(final String input, final String pattern, int limit)
    {
        return split(input, new Pattern(pattern), limit);
    }
    
    public static String[] split(final String input, final Pattern ppattern, int limit)
    {
        int index = 0;
        boolean matchLimited = limit > 0;
        ArrayList matchList = new ArrayList();
        Matcher m = ppattern.matcher(input);

        // Add segments before each match found
        while (m.find())
        {
            if (!matchLimited || matchList.size() < limit - 1)
            {
                String match = input.substring(index, m.start()).toString();
                if (match.length() > 0)
                {
                    matchList.add(match);
                }
                index = m.end();
            } else if (matchList.size() == limit - 1)
            { // last one
                String match = input.substring(index, input.length()).toString();
                if (match.length() > 0)
                {
                    matchList.add(match);
                }
                index = m.end();
            }
        }

        // If no match was found, return this
        if (index == 0)
            return new String[] { input.toString() };
        
        // Add remaining segment
        if (!matchLimited || matchList.size() < limit)
            matchList.add(input.substring(index, input.length()).toString());
        
        // Construct result
        int resultSize = matchList.size();
        if (limit == 0)
            while (resultSize > 0 && matchList.get(resultSize - 1).equals(""))
                resultSize--;
        String[] result = new String[resultSize];
        matchList.subList(0, resultSize).toArray(result);
        return result;
    }
}
