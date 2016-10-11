package com.queryio.disk.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.queryio.common.util.AppLogger;

import com.queryio.common.DiskInfo;
import com.queryio.sysmoncommon.engine.SystemMonitor;

public class DiskManager 
{
	public static ArrayList getPhysicalDiskNames()
	{
		try
		{
			if(SystemMonitor.isLinux(SystemMonitor.OS_NAME))
				return getPhysicalDiskNamesLinux();
			else if(SystemMonitor.isMacOS(SystemMonitor.OS_NAME))				
				return getPhysicalDiskNamesMacOS();
			else
				return SystemMonitor.windowMonitor.getDisksInstances(true);
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return null;
	}
	
	public static ArrayList getPhysicalDisksInfo()
	{
		try
		{
			if(SystemMonitor.isLinux(SystemMonitor.OS_NAME))
				return getPhysicalDisksInfoLinux();
			else if(SystemMonitor.isMacOS(SystemMonitor.OS_NAME)) {				
				return getPhysicalDisksInfoMacOS();
			} else {
				return getPhysicalDisksInfoWindowsOS();
			}
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		return null;
	}
	
	private static ArrayList getPhysicalDisksInfoWindowsOS() throws IOException {
		ArrayList disks = new ArrayList();
		
		String[] responseDiskUtil = getDiskpartOutputWindows();
		
		DiskInfo diskInfo = null;
		
		if(responseDiskUtil!=null)
		{
			for(int i=0; i<responseDiskUtil.length; i++)
			{
				if(!responseDiskUtil[i].startsWith(" "))
				{
					diskInfo = new DiskInfo();
					diskInfo.setName(responseDiskUtil[i]);
					
					disks.add(diskInfo);
				}
			}
		}

		return disks;
	}

	
	
	private static String[] getDiskpartOutputWindows() {
		// TODO Auto-generated method stub
		return null;
	}

	public static HashMap getPhysicalDiskMap() throws IOException
	{
		
		if(SystemMonitor.isMacOS(SystemMonitor.OS_NAME))
			return getPhysicalDiskMapMacOS();
		else
			return getPhysicalDiskMapLinux();
			
	}
	
	public static String formatDisk(String disk) throws IOException
	{
		if(SystemMonitor.isLinux(SystemMonitor.OS_NAME))
			return formatDiskLinux(disk);
		else
			return formatDiskMacOS(disk);
	}

	public static ArrayList getPhysicalDiskNamesMacOS() throws IOException
	{
		ArrayList disks = new ArrayList();
		
		String[] response = DiskManager.executeCommand("diskutil list").split("\n");
		
		if(response!=null)
		{
			for(int i=0; i<response.length; i++)
			{
				if(!response[i].startsWith(" "))	disks.add(response[i]);
			}
		}
		
		return disks;
	}
	
	private static ArrayList getPhysicalDisksInfoMacOS() throws Exception
	{
		ArrayList disks = new ArrayList();
		
		String[] responseDiskUtil = DiskManager.executeCommand("diskutil list").split("\n");
		
		DiskInfo diskInfo = null;
		
		if(responseDiskUtil!=null)
		{
			for(int i=0; i<responseDiskUtil.length; i++)
			{
				if(!responseDiskUtil[i].startsWith(" "))
				{
					diskInfo = new DiskInfo();
					diskInfo.setName(responseDiskUtil[i]);
					
					disks.add(diskInfo);
				}
			}
		}

		return disks;
	}

	public static HashMap getPhysicalDiskMapMacOS() throws IOException
	{
		ArrayList diskNames = DiskManager.getPhysicalDiskNames();
		
		HashMap diskMap = new HashMap();
		String[] response = DiskManager.executeCommand("df -h").split("\n");
		
		String value = null;
		
		String[] splits = null;
		if(response!=null)
		{
			for(int z=0; z<response.length; z++)
			{
				for(int i=0; i<diskNames.size(); i++)
				{
					if(response[z].startsWith((String)diskNames.get(i)))
					{
						splits = response[z].split("%");
						
						value = splits[splits.length-1].trim();
						
						if(value!=null && value.length()!=0)
						{
							if(value.charAt(value.length()-1) != File.separatorChar)
							{
								value += File.separator;
							}
						}
						
						diskMap.put(response[z].split(" ")[0], value);
					}
				}
			}
		}
		
		Set set = diskMap.entrySet(); 
		Iterator iter = set.iterator(); 

		ArrayList list = new ArrayList();
		
		while(iter.hasNext()) 
		{ 
			Map.Entry me = (Map.Entry)iter.next(); 
			list.add(me);
		} 
		
		HashMap linkedMap = new LinkedHashMap();
		
		int size = list.size();
		
		for(int index=0; index<size; index++)
		{
			int maxIndex = 0;
			for(int j=0; j<list.size(); j++)
			{
				if((((String)((Map.Entry)list.get(j)).getValue()).split(File.separatorChar=='\\' ? "\\\\" : File.separator).length) > (((String)((Map.Entry)list.get(maxIndex)).getValue()).split(File.separatorChar=='\\' ? "\\\\" : File.separator).length))
				{
					maxIndex = j;	
				}
			}
			
			linkedMap.put(((String)((Map.Entry)list.get(maxIndex)).getKey()), ((String)((Map.Entry)list.get(maxIndex)).getValue()));
			
			list.remove(maxIndex);
		}
		
		return linkedMap;
	}
	
	private static String formatDiskMacOS(String disk) 
	{
		String cmd = "diskutil eraseVolume \"HFS+\" my-ntfs " + disk.trim();
		String response = null;
		
		try
		{
			response = DiskManager.executeCommand(cmd);
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		
		return response;
	}

	public static ArrayList getPhysicalDiskNamesLinux() throws IOException
	{	
		ArrayList disks = null;
		
		String[] response = DiskManager.executeCommand("/bin/lsblk").split("\n");
		
		if (response != null)
		{
			disks = new ArrayList();
			for(int i=0; i<response.length; i++)
			{
				if (response[i].contains(" disk "))
				{
					String[] temp = response[i].split("\\s+");
					disks.add("/dev/" + temp[0]);
				}
			}
		}
		
		return disks;
	}
	
	private static ArrayList getPhysicalDisksInfoLinux() throws IOException
	{
		ArrayList disks = new ArrayList();
		
		String[] response = DiskManager.executeCommand("/bin/lsblk").split("\n");
		
		DiskInfo diskInfo = null;
		if(response!=null)
		{
			for(int i=0; i<response.length; i++)
			{
				if (response[i].contains(" disk "))
				{
					String[] temp = response[i].split("\\s+");
					
					diskInfo = new DiskInfo();
					diskInfo.setName("/dev/" + temp[0]);
					
					disks.add(diskInfo);
				}
			}
		}
		
		return disks;
	}

	private static HashMap getPhysicalDiskMapLinux() throws IOException 
	{
		ArrayList diskNames = DiskManager.getPhysicalDiskNames();
		
		HashMap diskMap = new HashMap();
		String[] response = DiskManager.executeCommand("df -h").split("\n");
		
		String[] splits = null;
		if(response!=null)
		{
			for(int z=0; z<response.length; z++)
			{
				for(int i=0; i<diskNames.size(); i++)
				{
					if(response[z].startsWith((String)diskNames.get(i)))
					{
						splits = response[z].split("%");
						diskMap.put(diskNames.get(i), splits[splits.length-1].trim());
					}
				}
			}
		}
		
		Set set = diskMap.entrySet(); 
		Iterator iter = set.iterator(); 

		ArrayList list = new ArrayList();
		
		while(iter.hasNext()) 
		{ 
			Map.Entry me = (Map.Entry)iter.next(); 
			list.add(me);
		} 
		
		HashMap linkedMap = new LinkedHashMap();
		
		int size = list.size();
		
		for(int index=0; index<size; index++)
		{
			int maxIndex = 0;
			for(int j=0; j<list.size(); j++)
			{
				if((((String)((Map.Entry)list.get(j)).getValue()).split(File.separatorChar=='\\' ? "\\\\" : File.separator).length) > (((String)((Map.Entry)list.get(maxIndex)).getValue()).split(File.separatorChar=='\\' ? "\\\\" : File.separator).length))
				{
					maxIndex = j;	
				}
			}
			
			linkedMap.put(((String)((Map.Entry)list.get(maxIndex)).getKey()), ((String)((Map.Entry)list.get(maxIndex)).getValue()));
			
			list.remove(maxIndex);
		}
		
		return linkedMap;
	}
	
	private static String formatDiskLinux(String disk) 
	{
		String cmd = "mkfs.ext3 " + disk.trim();
		String response = null;
		
		try
		{
			response = DiskManager.executeCommand(cmd);
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		
		return response;
	}
	
	public static boolean validateVolumeDiskMapping(HashMap physicalDiskMap, String disk, String volume) throws IOException
	{
		Set set = physicalDiskMap.entrySet(); 
		Iterator i = set.iterator(); 

		disk = disk.trim();
		volume = volume.trim();
		
		if(volume!=null && volume.length()!=0)
		{
			if(volume.charAt(volume.length()-1) != File.separatorChar)
			{
				volume += File.separator;
			}
		}
		
		String tempPath = "";
		
		boolean valid = true;
		
		while(i.hasNext()) 
		{ 
			Map.Entry me = (Map.Entry)i.next();
			
			tempPath = ((String) me.getValue()).replace(" ", "\\ "); 
					
			if((volume.startsWith((String) me.getValue())) || (volume.startsWith(tempPath)))
			{
				if(((String)me.getKey()).startsWith(disk))
				{
					valid = true;
					break;
				}
				else
				{
					valid = false;
					break;
				}
			}
		} 	
		
		return valid;
	}
	
	public static ArrayList validateVolumeDiskMapping(ArrayList disks, ArrayList volumes)
	{
		ArrayList validationResult = new ArrayList();
		
		
		try
		{
			HashMap physicalDiskMap = DiskManager.getPhysicalDiskMap();
			
			for(int i=0; i<disks.size(); i++)
			{
				validationResult.add(DiskManager.validateVolumeDiskMapping(physicalDiskMap, (String)disks.get(i), (String)volumes.get(i)));
			}
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		
		return validationResult;
	}
	public static HashMap getDiskMap()
	{
		HashMap physicalDiskMap = null;
		try
		{
			physicalDiskMap = DiskManager.getPhysicalDiskMap();
			
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
			
		}
		return physicalDiskMap;
	}
	
	public static String formatDisks(ArrayList disks)
	{
		String response = "";
		
		try
		{
			for(int i=0; i<disks.size(); i++)
			{
				response += "Format Disk Response for: " + disks.get(i) + "\n";
				response += DiskManager.formatDisk((String)disks.get(i)) + "\n";
			}
		}
		catch(Exception e)
		{
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		
		return response;
	}
	
	public static String executeCommand(String cmd) throws IOException
	{
		BufferedReader stdInput = null;
		String content = "";
		
		try
		{
			Process p = Runtime.getRuntime().exec(cmd);
			
			stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	
			String s = null;
			while ((s = stdInput.readLine()) != null) 
			{
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
	
	public static void main(String[] args) throws IOException
	{
		String disk = "/dev/disk0";
		String volume = "/Volumes/Server HD";
		for(Object str : getPhysicalDiskNamesMacOS()) {
			System.out.println(str);
		}
//		String disk = "/dev/sdb";
//		String volume = "/";
		
		HashMap physicalDiskMap = DiskManager.getPhysicalDiskMap();
		
//		HashMap physicalDiskMap = new LinkedHashMap();
//		
//		physicalDiskMap.put("/dev/sdb2", "/des/vd/s/");
//		physicalDiskMap.put("/dev/sda2", "/dec/");
//		physicalDiskMap.put("/dev/sdb1", "/dess/");
//		physicalDiskMap.put("/dev/sdb3", "/desaa/");
//		physicalDiskMap.put("/dev/sda1", "/");
		
		Set set = physicalDiskMap.entrySet(); 
		Iterator i = set.iterator(); 

		while(i.hasNext()) 
		{ 
			Map.Entry me = (Map.Entry)i.next(); 
			System.out.print(me.getKey() + ": "); 
			System.out.println(me.getValue()); 
		} 
		
//		System.out.println(disk + " : " + volume + " : " + validateVolumeDiskMapping(physicalDiskMap, disk, volume));
		System.out.println("System : "+System.getProperty("os.name").toLowerCase());
		System.out.println(executeCommand("pwd"));
	}
}
