package com.queryio.sysmoncommon.samples;

import java.util.ArrayList;

import com.queryio.common.IOSProtocolConstants;
import com.queryio.common.service.wrapper.MonitorWindowsServiceWrapper;
import com.queryio.common.service.wrapper.UniqueIdGenerator;
import com.queryio.sysmoncommon.pdh.dstruct.AttributeNode;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.ProcessInfo;

public class WindowsMonitor
{
	private static String getValue(final String[] args, final int index, final String defaultValue)
	{
		if ((args != null) && (args.length > index))
		{
			return args[index];
		}
		return defaultValue;
	}

	public static void main(final String[] args)
	{
		System.setProperty("devsuite.home", "F:\\QIOV35");
		try
		{
			final MonitorWindowsServiceWrapper mwRequest = new MonitorWindowsServiceWrapper(IOSProtocolConstants.LOOPBACKADDRESS, UniqueIdGenerator
					.generateUniqueId("controllerId"));
			final String monitorHostName = getValue(args, 0, "192.168.0.33");
			final String userName = getValue(args, 1, "administrator");
			final String password = getValue(args, 2, "admin");
			final String domainName = getValue(args, 3, null);

			mwRequest.setMonitorHostName(monitorHostName);
//			mwRequest.setDomainName(domainName);
//			mwRequest.setUserName(userName);
//			mwRequest.setPassword(password);
			mwRequest.enableNativeLog(true);
			boolean flag = mwRequest.connectToMachine();
			System.out.println("Ping successful: " + flag);
			// if (flag)
			{
				AttributeNode[] nodes = new AttributeNode[7];
				int ctr = 0;

				// Total CPU Usage
				nodes[ctr] = new AttributeNode();
				nodes[ctr].setObjectName("Processor");
				nodes[ctr].setCounterName("% Processor Time");
				nodes[ctr].setInstanceName("_Total");
				nodes[ctr].setInstanceCount(0);
				ctr++;

				// Total virtual memory
				nodes[ctr] = new AttributeNode();
				nodes[ctr].setObjectName("Memory");
				nodes[ctr].setCounterName("Commit Limit");
				nodes[ctr].setInstanceName(null);
				nodes[ctr].setInstanceCount(0);
				ctr++;

				// Used virtual memory
				nodes[ctr] = new AttributeNode();
				nodes[ctr].setObjectName("Memory");
				nodes[ctr].setCounterName("Committed Bytes");
				nodes[ctr].setInstanceName(null);
				nodes[ctr].setInstanceCount(0);
				ctr++;

				// Page Faults per sec
				nodes[ctr] = new AttributeNode();
				nodes[ctr].setObjectName("Memory");
				nodes[ctr].setCounterName("Page Faults/sec");
				nodes[ctr].setInstanceName(null);
				nodes[ctr].setInstanceCount(0);
				ctr++;

				// Physical memory
				nodes[ctr] = new AttributeNode();
				nodes[ctr].setObjectName("Memory");
				nodes[ctr].setCounterName("Committed Bytes");
				nodes[ctr].setInstanceName(null);
				nodes[ctr].setInstanceCount(0);
				ctr++;

				// Disk i/o
				nodes[ctr] = new AttributeNode();
				nodes[ctr].setObjectName("PhysicalDisk");
				nodes[ctr].setCounterName("Disk Reads/sec");
				nodes[ctr].setInstanceName("_Total");
				nodes[ctr].setInstanceCount(0);
				ctr++;

				nodes[ctr] = new AttributeNode();
				nodes[ctr].setObjectName("PhysicalDisk");
				nodes[ctr].setCounterName("Disk Writes/sec");
				nodes[ctr].setInstanceName("_Total");
				nodes[ctr].setInstanceCount(0);
				ctr++;
				
				System.out.println("ndoe.size : " + nodes.length);

				flag = mwRequest.collectData(nodes);
				System.out.println("Collect data successful: " + flag);
				for (int dc = 0; dc < 3; dc++)
				{
					System.out.println("\nFetching data");
					nodes = mwRequest.fetchCollectedData();
					if (nodes != null)
					{
						for (int i = 0; i < nodes.length; i++)
						{
							final int value = (int) nodes[i].getValue();
							switch (i)
							{
								case 0:
								{
									// Total CPU usage
									System.out.println("Total CPU usage: " + value);
									break;
								}
								case 1:
								{
									// Total virtual memory
									System.out.println("Total virtual memory: "
											+ (int) (nodes[i].getValue() / (1000 * 1000)));
									break;
								}
								case 2:
								{
									// Used virtual memory
									System.out.println("Used virtual memory: "
											+ (int) (nodes[i].getValue() / (1000 * 1000)));
									break;
								}
								case 3:
								{
									// Page faults per sec
									System.out.println("Page faults per sec: " + value);
									break;
								}
								case 4:
								{
									// Physical memory
									System.out.println("Available Physical memory: " + value);
									break;
								}
								case 5:
								{
									// Disk reads per sec
									System.out.println("Disk reads/sec: " + value);
									break;
								}
								case 6:
								{
									// Disk writes per sec
									System.out.println("Disk writes/sec: " + value);
									break;
								}
							}
						}
					}
					else
					{
						System.out.println("Nodes returned are NULL");
					}
					final NetworkInfo[] info = mwRequest.getNetworkInfo();
					if (info != null)
					{
						for (int i = 0; i < info.length; i++)
						{
							System.out.println("name: " + info[i].getName() + " Recd/sec: "
									+ info[i].getRecdPacketsPerSec() + " Sent/sec: " + info[i].getSentPacketsPerSec());
						}
					}
					
					ProcessInfo [] processes = mwRequest.getProcessInfo();
					for(int i = 0; i < processes.length; i++) 
					{
						System.out.print("["+processes[i].getProcessID() + ", " + processes[i].getName() + "], "); 
					} 
					System.out.println();
					Thread.sleep(2000);
				}
//				AttributeNode[] node = mwRequest.fetchAttributes(new String[] {"PhysicalDisk"});
//				System.out.println("pc nodes len : " + node.length);
//				for(int i=0;i<node.length;i++)
//				{
//					System.out.println("--------------");
//					System.out.println(node[i].getObjectName());
//					System.out.println(node[i].getCounterName());
//					System.out.println(node[i].getInstanceName());
//					System.out.println("--------------");
//				}
//				flag = mwRequest.disconnectFromMachine();
//				System.out.println("Disconnected: " + flag);
				getDiskInfo(mwRequest);
			}
		}
		catch (final Throwable th)
		{
			th.printStackTrace();
		}
	}
	
	private static ArrayList getDiskInfo(MonitorWindowsServiceWrapper mwRequest)
	{
		DiskInfo[] disk = null;
		ArrayList instances = new ArrayList();
		AttributeNode[] node = mwRequest.fetchAttributes(new String[] {"PhysicalDisk"});
		for(int i=0;i<node.length;i++)
		{
			if(node[i].getCounterName().contains("Current Disk Queue Length") && !node[i].getInstanceName().contains("Total"))
			{
				instances.add(node[i].getInstanceName());
			}			
		}
		disk = new DiskInfo[instances.size()];
		System.out.println("instances.size : " + instances.size());
		for(int i=0;i<instances.size();i++)
		{
			System.out.println(instances.get(i));
		}
		return instances;
	}

}
