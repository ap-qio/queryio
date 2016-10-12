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
						
//						System.out.println("smartctl is here without complete path");
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
	
//	public static void getFailureDiskDetail(String diskName, ArrayList<String []> failureHardDiskDetails) throws IOException
//	{
////		get attributes of particular hard disk specified in diskName parameter
//		Process diskAttributeStatus = Runtime.getRuntime().exec("/usr/local/sbin/smartctl -a " + diskName);
//		BufferedReader inputStream = new BufferedReader(new InputStreamReader(diskAttributeStatus.getInputStream()));
//		String line = null;
//		String [] diskDetails = new String[TOTAL_NUMBER_OF_INFORMATION];
//		
//		while((line = inputStream.readLine()) != null)
//		{
//			if(diskDetails[DEVICE_ID] == null && line.contains("Device Id:"))
//			{
//				diskDetails[DEVICE_ID] = line.split(":")[1].trim();
//			}
//			else if(diskDetails[DISK_SERIAL_NUMBER] == null && line.startsWith("Serial Number:"))
//			{
//				diskDetails[DISK_SERIAL_NUMBER] = line.split(":")[1].trim();
//			}	
//			else if(diskDetails[MODEL_NUMBER] == null && line.startsWith("Device Model:"))
//			{
//				diskDetails[MODEL_NUMBER] = line.split(":")[1].trim();
//			}
//			else if(diskDetails[DISK_CAPACITY] == null && line.startsWith("User Capacity:"))
//			{
//				diskDetails[DISK_CAPACITY] = line.split(":")[1].trim();
//			}
//			else if(diskDetails[BAD_SECTOR_COUNT] == null && line.contains("Reallocated_Sector_Ct"))
//			{
//				String [] lineToken = line.split(" ");
//				diskDetails[BAD_SECTOR_COUNT] = lineToken[lineToken.length - 1];
//				break;
//			}
//		}
//		
////		get mounting information
//		Process mountInfo = Runtime.getRuntime().exec("df");
//		inputStream = new BufferedReader(new InputStreamReader(mountInfo.getInputStream()));
//		while((line = inputStream.readLine()) != null)
//		{
	
//			if(line.startsWith(diskName))
//			{
//				String [] lineToken = line.split(" ");
//				diskDetails[MOUNT_INFO] = lineToken[lineToken.length - 1];
//				break;
//			}
//		}
//		
//		diskDetails[IP_ADDRESS] = getIPAddress();
//		
//		failureHardDiskDetails.add(diskDetails);
//	}
//	
//	public static void sendNotification(String IPAddress, ArrayList<String []> failureHardDiskDetails)
//	{
////		failureHardDiskDetails contains all the required information for hard disks which are imminent to failure
//	}
//	
//	public static String getIPAddress()
//	{
//		NetworkInterface ni = null;
//		try 
//		{
//			ni = NetworkInterface.getByName("eth0");
//		} 
//		catch (SocketException e) 
//		{
//			AppLogger.getLogger().fatal("Could not fetch system IP, Exception: " + e.getMessage(), e);
//		}
//        Enumeration<InetAddress> inetAddresses =  ni.getInetAddresses();
//        
//        while(inetAddresses.hasMoreElements()) 
//        {
//            InetAddress ia = inetAddresses.nextElement();
//            if(!ia.isLinkLocalAddress()) 
//            {
//            	return ia.getHostAddress();
//            }
//        }
//        return "";
//	}
}