package com.queryio.sysmoncommon.sysmon;

import java.util.ArrayList;

import com.queryio.common.IOSProtocolConstants;
import com.queryio.common.util.AppLogger;

import com.queryio.common.service.wrapper.MonitorWindowsServiceWrapper;
import com.queryio.sysmoncommon.pdh.dstruct.AttributeNode;
import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.MemoryInfo;
import com.queryio.sysmoncommon.sysmon.dstruct.NetworkInfo;

public class WindowsMonitorController
{
	private static String controllerId = "QIOController";
	private MonitorWindowsServiceWrapper monitorServiceWrapper = null;
	boolean isFirstTime = true;
	private boolean connectedToHost = false;
	private AttributeNode[] nodes = null;
	private NetworkInfo[] networkInfoArr = null;
	private DiskInfo[] diskInfoArr = null;
	private int diskCtr = 0;
	private ArrayList<String> diskInstances = null;
	
	private static String getValue(final String[] args, final int index, final String defaultValue)
	{
		if ((args != null) && (args.length > index))
		{
			return args[index];
		}
		return defaultValue;
	}
	
	public boolean connect() throws UnsatisfiedLinkError, Exception
	{
		if (!this.connectedToHost)
		{
			if (this.monitorServiceWrapper == null)
			{
				this.monitorServiceWrapper = new MonitorWindowsServiceWrapper(IOSProtocolConstants.LOOPBACKADDRESS, controllerId);
			}
			this.monitorServiceWrapper.enableNativeLog(true);
			
			this.connectedToHost = this.monitorServiceWrapper.connectToMachine();
			if (this.connectedToHost)
			{
				this.validateCollectData();
			}
			return this.connectedToHost;
		}
		return false;
	}
	
	protected void validateCollectData() {
		this.collectData();
	}
	
	public void disconnect() 
	{
		this.monitorServiceWrapper.disconnectFromMachine();
	}
	
	public void collectData()
	{
//		if (this.isFirstTime)
//		{
			getDisksInstances(false);
			final AttributeNode[] nodes = new AttributeNode[7 + (this.diskInstances.size()*2)];
			int ctr = 0;

			// Total CPU Usage	// 0 - CPU Usage
			nodes[ctr] = new AttributeNode();
			nodes[ctr].setObjectName("Processor");
			nodes[ctr].setCounterName("% Processor Time");
			nodes[ctr].setInstanceName("_Total");
			nodes[ctr].setInstanceCount(0);
			ctr++;

			// Total virtual memory	// 1
			nodes[ctr] = new AttributeNode();
			nodes[ctr].setObjectName("Memory");
			nodes[ctr].setCounterName("Commit Limit");
			nodes[ctr].setInstanceName(null);
			nodes[ctr].setInstanceCount(0);
			ctr++;

			// Used virtual memory	// 2
			nodes[ctr] = new AttributeNode();
			nodes[ctr].setObjectName("Memory");
			nodes[ctr].setCounterName("Committed Bytes");
			nodes[ctr].setInstanceName(null);
			nodes[ctr].setInstanceCount(0);
			ctr++;

			// Page Faults per sec	// 3
			nodes[ctr] = new AttributeNode();
			nodes[ctr].setObjectName("Memory");
			nodes[ctr].setCounterName("Page Faults/sec");
			nodes[ctr].setInstanceName(null);
			nodes[ctr].setInstanceCount(0);
			ctr++;

			// Physical memory	// 4
			nodes[ctr] = new AttributeNode();
			nodes[ctr].setObjectName("Memory");
			nodes[ctr].setCounterName("Available MBytes");
			nodes[ctr].setInstanceName(null);
			nodes[ctr].setInstanceCount(0);
			ctr++;

			// Disk i/o	//5
			nodes[ctr] = new AttributeNode();
			nodes[ctr].setObjectName("PhysicalDisk");
			nodes[ctr].setCounterName("Disk Reads/sec");
			nodes[ctr].setInstanceName("_Total");
			nodes[ctr].setInstanceCount(0);
			ctr++;

			//6
			nodes[ctr] = new AttributeNode();
			nodes[ctr].setObjectName("PhysicalDisk");
			nodes[ctr].setCounterName("Disk Writes/sec");
			nodes[ctr].setInstanceName("_Total");
			nodes[ctr].setInstanceCount(0);
			ctr++;
			
			diskCtr = ctr;
			
			for(int i=0;i<this.diskInstances.size();i++)
			{
				nodes[ctr] = new AttributeNode();
				nodes[ctr].setObjectName("PhysicalDisk");
				nodes[ctr].setCounterName("Disk Reads/sec");
				nodes[ctr].setInstanceName(this.diskInstances.get(i).toString());
				nodes[ctr].setInstanceCount(0);
				ctr++;
				
				nodes[ctr] = new AttributeNode();
				nodes[ctr].setObjectName("PhysicalDisk");
				nodes[ctr].setCounterName("Disk Writes/sec");
				nodes[ctr].setInstanceName(this.diskInstances.get(i).toString());
				nodes[ctr].setInstanceCount(0);
				ctr++;
			}
			System.out.println("node.lenth collect data  : " + nodes.length);
			System.out.println("collect data : "  + this.monitorServiceWrapper.collectData(nodes));
			this.isFirstTime = false;
//		}

		synchronized (this)
		{
			AppLogger.getLogger().debug("Inside synchronized block.");
			this.nodes = this.monitorServiceWrapper.fetchCollectedData();
			AppLogger.getLogger().debug("this.nodes == null : " + (this.nodes == null));
			this.networkInfoArr = this.monitorServiceWrapper.getNetworkInfo();
			getDiskInfo();			
		}
	}
	
	public NetworkInfo[] getNetworkInfo()
	{
		return this.networkInfoArr;
	}
	
	public DiskInfo[] getPhysicalDiskInfo() throws Exception
	{
		return this.diskInfoArr;
	}
	
	public float getSystemCPUUsage() throws Exception
	{
		return (float) nodes[0].getValue();
	}
	
	public MemoryInfo getPhysicalMemoryInfo() throws Exception
	{
		return new MemoryInfo(0, 0, (int) nodes[4].getValue(), (float) nodes[3].getValue());
	}
	
	private void getDiskInfo()
	{
		this.diskInfoArr = new DiskInfo[this.diskInstances.size()];
		for(int i=0;i<this.diskInstances.size();i++)
		{
			//diskCtr contains counter value from where individual disk read/sec and write/sec are stored in nodes array.
			// For example if diskCtr = 6....then first disk read/sec will be at position 6 and write/sec will be at position 7. For next disk, reads/pec will be at position 8 and write/sec will be at position 9 and so on.
			AppLogger.getLogger().debug("diskInstances.size : " + diskInstances.size());
			AppLogger.getLogger().debug("nodes.size : " + nodes.length);
			AppLogger.getLogger().debug("i : " + i);
			AppLogger.getLogger().debug("diskCtr+i : " + (diskCtr+i));
			AppLogger.getLogger().debug("diskCtr+i+1 : " + (diskCtr+i+1));
			DiskInfo info = new DiskInfo(this.diskInstances.get(i), (float) this.nodes[diskCtr+i].getValue(), (float) this.nodes[diskCtr+i+1].getValue());
			this.diskInfoArr[i] = info;
		}
	}
	
	public ArrayList<String> getDisksInstances(boolean stripDiskName)
	{
		this.diskInstances = new ArrayList<String>();
		AttributeNode[] node = this.monitorServiceWrapper.fetchAttributes(new String[] {"PhysicalDisk"});
		for(int i=0;i<node.length;i++)
		{
			if(node[i].getCounterName().contains("Current Disk Queue Length") && !node[i].getInstanceName().contains("Total"))
			{
				if(stripDiskName) {
					this.diskInstances.add("Disk " + node[i].getInstanceName().substring(0, node[i].getInstanceName().indexOf(" ")));
				} else {					
					this.diskInstances.add(node[i].getInstanceName());
				}
			}			
		}
		System.out.println("instances.size : " + this.diskInstances.size());
		for(int i=0;i<this.diskInstances.size();i++)
		{
			System.out.println(this.diskInstances.get(i));
		}
		return diskInstances;
	}	
}
