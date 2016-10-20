package com.queryio.sysmoncommon.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.queryio.common.util.AppLogger;
import com.queryio.disk.manager.DiskManager;

public class DiskMonitor {
	public static final int DEVICE_ID = 0;
	public static final int DISK_SERIAL_NUMBER = 1;
	public static final int MODEL_NUMBER = 2;
	public static final int DISK_CAPACITY = 3;
	public static final int BAD_SECTOR_COUNT = 4;
	public static final int MOUNT_INFO = 5;
	public static final int IP_ADDRESS = 6;
	public static final int TOTAL_NUMBER_OF_INFORMATION = 7;
	
	public static final String DISK_HEALTH_PASS = "1";
	public static final String DISK_HEALTH_FAIL = "0";
	public static final String DISK_HEALTH_UNDEFINED = "2";
	
	
	public static String getDiskHealthStatistics() 
	{
		ArrayList hardDiskNames = null;
		
/*		Arraylist failureHardDiskDetails will contain each hard disk's information which is imminent to failure.
		String array will have the values related to the particular hard disk
		String [DEVICE_ID] = Device ID
		String [DISK_SERIAL_NUMBER] = Disk serial number
		String [MODEL_NUMBER] = Model number
		String [DISK_CAPACITY] = Disk capacity
		String [BAD_SECTOR_COUNT] = Bad Sector count
		String [MOUNT_INFO] = Mounting information
*/
			
//		ArrayList<String []> failureHardDiskDetails = new ArrayList<String[]>();
		
		String diskStatus = "";
		
		try 
		{
			hardDiskNames = DiskManager.getPhysicalDiskNames();
			
			if (hardDiskNames != null && hardDiskNames.size() > 0)
			{

				Process healthStatusProcess = null;
				String line = null;
				
				for(int i = 0; i < hardDiskNames.size(); i++)
				{
					BufferedReader inputStreamStatus = null;
//					get health status of particular hard disk
					try 
					{	
						// /usr/local/sbin/
						healthStatusProcess = Runtime.getRuntime().exec("smartctl -H " + hardDiskNames.get(i));
						
						inputStreamStatus = new BufferedReader(new InputStreamReader(healthStatusProcess.getInputStream()));
						
						boolean isPermissionDenied = true;
						while((line = inputStreamStatus.readLine()) != null)
						{
//						if health status is other than PASSED
							
							if(line.startsWith("SMART overall-health self-assessment"))
							{
								/* getFailureDiskDetail(hardDiskNames.get(i), failureHardDiskDetails);		*/
								if(!line.endsWith("PASSED"))
								{
									diskStatus += hardDiskNames.get(i) + "=" + DISK_HEALTH_FAIL + ",";
								}
								else
								{
									diskStatus += hardDiskNames.get(i) + "=" + DISK_HEALTH_PASS + ",";
								}
								isPermissionDenied = false;
								break;
							}
						}
						if(isPermissionDenied)
						{
							diskStatus += hardDiskNames.get(i) + "=" + DISK_HEALTH_UNDEFINED + ",";
						}
					} 
					catch (Exception e) 
					{
						diskStatus += hardDiskNames.get(i) + "=" + DISK_HEALTH_UNDEFINED + ",";
					}
					finally
					{
						if(inputStreamStatus != null)
						{
							inputStreamStatus.close();
						}
					}
				}	
			}
						
		} 
		catch (IOException e) 
		{
			AppLogger.getLogger().fatal("Exception: " + e.getMessage(), e);
		}
		
		return diskStatus.substring(0, diskStatus.lastIndexOf(",") + 1);
	}
	
	public static void main(String[] args) {
		new DiskMonitor().getDiskHealthStatistics();
	}
	
}