package com.queryio.sysmoncommon.sysmon.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

import com.queryio.sysmoncommon.sysmon.AbstractDataParser;
import com.queryio.sysmoncommon.sysmon.ResultParsingException;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.DriveInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.UnixNetworkInfo;

public class ZFSSolarisDataParser extends AbstractDataParser 
{
	
	public void parseNetStatCommand(String output) throws ResultParsingException 
	{
		/*
			root@motest19:~# dladm show-link -s
			LINK            IPACKETS   RBYTES   IERRORS    OPACKETS     OBYTES       OERRORS
			igb0            2121813895 1732931973074 0     1542632991   356238426078 0
			igb1            0          0        0          0            0            0
		
			// read bytes: RBYTES write bytes: OBYTES - absolute - so we need to find per sec.
		*/
		if (this.networkInfoList == null)
		{
			this.networkInfoList = new LinkedList();
		}
		else
		{
			this.networkInfoList.clear();
		}
		if (output != null)
		{
			try
			{
				String sLine = null;
				String [] array;
				StringTokenizer stk = new StringTokenizer(output, "\r\n\f");
				while (stk.hasMoreTokens())
				{
					sLine = stk.nextToken();
					if (sLine.indexOf("IPACKETS") == -1 && sLine.indexOf("LINK") == -1)
					{
						array = getTokens(sLine);
						if (array.length > 6)
						{
							//System.out.println("***** " + array[0] + " reads: " + array[2] + " writes: " + array[5]);
							this.networkInfoList.add(new UnixNetworkInfo(array[0], 0, Long.parseLong(array[2]) / 1024, Long.parseLong(array[5]) / 1024));
						}
					}
				}
			}
			catch (final Exception ex)
			{
				throw new ResultParsingException("Network Usage " + ex.getMessage(), output);
			}
		}
	}
	
	public void parseLoadAverages(String output) throws ResultParsingException
	{
		/*
			root@motest19:~# uptime
	 		11:09am  up 41 days  1:15,  6 users,  load average: 7.62, 7.82, 7.71
			// last three items.
		*/
		if (output != null)
		{
			try
			{
				String sLine = null;
				final String LA = "load average:";
				StringTokenizer stk = new StringTokenizer(output, "\r\n\f");
				while (stk.hasMoreTokens())
				{
					sLine = stk.nextToken();
					final int laIndex = sLine.indexOf(LA);
					if (laIndex != -1)
					{
						String loadAvgs = output.substring(laIndex + LA.length()).trim();
						int startIndex = 0;
						int nextIndex = loadAvgs.indexOf(',');
						this.f1MinLoadAvg = Float.parseFloat(loadAvgs.substring(startIndex, nextIndex));
						startIndex = nextIndex + 1;
						nextIndex = loadAvgs.indexOf(',', startIndex);
						this.f5MinLoadAvg = Float.parseFloat(loadAvgs.substring(startIndex, nextIndex));
						startIndex = nextIndex + 1;
						this.f15MinLoadAvg = Float.parseFloat(loadAvgs.substring(startIndex));
					}
				}
			}
			catch (final Exception ex)
			{
				throw new ResultParsingException("Load Average " + ex.getMessage(), output);
			}
		}
	}

	public void parseIOStatCommand(String output) throws ResultParsingException 
	{
		/*
			root@motest19:~# iostat 1
			   tty       cmdk0         cmdk1      iomemory-vsl0     nfs3           cpu
			 tin tout kps tps serv  kps tps serv  kps tps serv  kps tps serv   us sy wt id
			   0  199   3   0   30  1161  47   60  1246  50    0    0   0   75    1  9  0 90
			   0 3596   0   0    0   57   4    1  127   1    0    0   0    0    0 54  0 46
			   0  242   0   0    0  572  69    2    0   0    0    0   0    0    1 52  0 47
		 */
		if (output != null)
		{
			try
			{
				String sLine = null;
				String [] array;
				StringTokenizer stk = new StringTokenizer(output, "\r\n\f");
				while (stk.hasMoreTokens())
				{
					sLine = stk.nextToken();
					if (sLine.indexOf("tty") == -1 && sLine.indexOf("tin") == -1)
					{
						array = getTokens(sLine);
						if (array.length > 4)
						{
							int n = array.length;
							this.fCpuUsageTime = Math.max(0, 100 - Float.parseFloat(array[n - 1]));
							this.fWaitCPUUsageTime = Float.parseFloat(array[n - 2]);
							this.fSysCPUUsageTime = Float.parseFloat(array[n - 3]);
							this.fUserCPUUsageTime = Float.parseFloat(array[n - 4]);
						}
					}
				}
			}
			catch (final Exception ex)
			{
				throw new ResultParsingException("CPU Usage " + ex.getMessage(), output);
			}
		}
	}

	public void parseVmstatCommand(String output) throws ResultParsingException 
	{
		/*
			root@motest19:~# vmstat 1
			 kthr      memory            page            disk          faults      cpu
			 r b w   swap  free  re  mf pi po fr de sr cd cd im --   in   sy   cs us sy id
			 0 0 0 29144208 10268664 1 96 0 0  0  0  0  0 47 -359670 0 3032 10376 2383 1 9 90
			 0 0 0 26252412 7337492 13 77 0 0  0  0  0  0  0  2  0  646  465  388  0 51 49
			 4 0 0 26251980 7337476 0 216 0 0  0  0  0  0  0  0  0  678 1078  393  0 53 47
			 5 0 0 26253212 7338244 0 146 0 0  0  0  0  0  0  0  0  652  703  319  0 54 46
		*/
		if (output != null)
		{
			try
			{
				String sLine = null;
				String [] array;
				StringTokenizer stk = new StringTokenizer(output, "\r\n\f");
				while (stk.hasMoreTokens())
				{
					sLine = stk.nextToken();
					if (sLine.indexOf("kthr") == -1 && sLine.indexOf("memory") == -1 && sLine.indexOf("swap") == -1 && sLine.indexOf("free") == -1)
					{
						array = getTokens(sLine);
						if (array.length > 4)
						{
							int freeMemory = (int) (Long.parseLong(array[3])/1024);
							this.virtualMemInfo = new MemoryInfo(freeMemory, 0, freeMemory);
							freeMemory = (int) (Long.parseLong(array[4])/1024);
							this.physicalMemInfo = new MemoryInfo(freeMemory, 0, freeMemory);
						}
					}
				}
			}
			catch (final Exception ex)
			{
				throw new ResultParsingException("Memory Usage " + ex.getMessage(), output);
			}
		}
	}

	public void parseDFCommand(String output) throws ResultParsingException 
	{
		/*
			root@motest19:~# zpool iostat 1
			               capacity     operations    bandwidth
			pool         used  avail   read  write   read  write
			----------  -----  -----  -----  -----  -----  -----
			rpool       42.7G   885G      0      0    472  2.28K
			terrorbyte   799G   129G      4     92   275K  1.76M
			----------  -----  -----  -----  -----  -----  -----
			rpool       42.7G   885G      0      0      0      0
		*/
//		System.out.println("----- zpool iostat -----");
//		System.out.println(output);
//		System.out.println("----- end -----");
		if (this.diskInfoList == null)
		{
			this.diskInfoList = new LinkedList();
		}
		else
		{
			this.diskInfoList.clear();
		}
		if (this.diskSpaceInfoList == null)
		{
			this.diskSpaceInfoList = new LinkedList();
		}
		else
		{
			this.diskSpaceInfoList.clear();
		}
		if (output != null)
		{
			try
			{
				String sLine = null;
				String [] array;
				StringTokenizer stk = new StringTokenizer(output, "\r\n\f");
				HashMap diskSet = new HashMap();
				HashMap driveSet = new HashMap();
				while (stk.hasMoreTokens())
				{
					sLine = stk.nextToken();
					if (sLine.indexOf("capacity") == -1 && sLine.indexOf("bandwidth") == -1 && sLine.indexOf("read") == -1 && sLine.indexOf("write") == -1 && sLine.indexOf("----------") == -1 )
					{
						array = getTokens(sLine);
						if (array.length > 6)
						{
							float usedSpace;
							switch (array[1].charAt(array[1].length() - 1))
							{
								case 'K': usedSpace = Float.parseFloat(array[1].substring(0, array[1].length() - 1)) / 1024; break;
								case 'M': usedSpace = Float.parseFloat(array[1].substring(0, array[1].length() - 1)); break;
								case 'G': usedSpace = Float.parseFloat(array[1].substring(0, array[1].length() - 1)) * 1024; break;
								case 'T': usedSpace = Float.parseFloat(array[1].substring(0, array[1].length() - 1)) * 1024 * 1024; break;
								default: usedSpace = Float.parseFloat(array[1]) / (1024 * 1024); break;
							}
							float availSpace;
							switch (array[2].charAt(array[2].length() - 1))
							{
								case 'K': availSpace = Float.parseFloat(array[2].substring(0, array[2].length() - 1)) / 1024; break;
								case 'M': availSpace = Float.parseFloat(array[2].substring(0, array[2].length() - 1)); break;
								case 'G': availSpace = Float.parseFloat(array[2].substring(0, array[2].length() - 1)) * 1024; break;
								case 'T': availSpace = Float.parseFloat(array[2].substring(0, array[2].length() - 1)) * 1024 * 1024; break;
								default: availSpace = Float.parseFloat(array[2]) / (1024 * 1024); break;
							}
							driveSet.put(array[0], new DriveInfo(array[0], availSpace + usedSpace, usedSpace));
							
							// 5 & 6
							float readsPerSec;
							switch (array[5].charAt(array[5].length() - 1))
							{
								case 'K': readsPerSec = Float.parseFloat(array[5].substring(0, array[5].length() - 1)); break;
								case 'M': readsPerSec = Float.parseFloat(array[5].substring(0, array[5].length() - 1)) * 1024; break;
								case 'G': readsPerSec = Float.parseFloat(array[5].substring(0, array[5].length() - 1)) * (1024 * 1024); break;
								case 'T': readsPerSec = Float.parseFloat(array[5].substring(0, array[5].length() - 1)) * (1024 * 1024 * 1024); break;
								default: readsPerSec = Float.parseFloat(array[5]) / 1024; break;
							}
							float writesPerSec;
							switch (array[6].charAt(array[6].length() - 1))
							{
								case 'K': writesPerSec = Float.parseFloat(array[6].substring(0, array[6].length() - 1)); break;
								case 'M': writesPerSec = Float.parseFloat(array[6].substring(0, array[6].length() - 1)) * 1024; break;
								case 'G': writesPerSec = Float.parseFloat(array[6].substring(0, array[6].length() - 1)) * (1024 * 1024); break;
								case 'T': writesPerSec = Float.parseFloat(array[6].substring(0, array[6].length() - 1)) * (1024 * 1024* 1024); break;
								default: writesPerSec = Float.parseFloat(array[6]) / 1024; break;
							}
							diskSet.put(array[0], new DiskInfo(array[0], readsPerSec, writesPerSec));
						}
					}
				}
				if (!diskSet.isEmpty())
				{
					this.diskInfoList.addAll(diskSet.values());
				}
				if (!driveSet.isEmpty())
				{
					this.diskSpaceInfoList.addAll(driveSet.values());
				}
			}
			catch (final Exception ex)
			{
				throw new ResultParsingException("Disk Usage " + ex.getMessage(), output);
			}
		}
	}

	public void parseTopCommand(String output) throws ResultParsingException 
	{
		// do nothing
	}

	public void parseTopMemCommand(String output) throws ResultParsingException 
	{
		// do nothing
	}

	public void parsePrstatCommand(String output) throws ResultParsingException 
	{
		// do nothing
	}

}
