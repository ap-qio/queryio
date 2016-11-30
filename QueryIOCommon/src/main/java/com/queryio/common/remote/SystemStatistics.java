package com.queryio.common.remote;

import java.io.Serializable;
import java.util.Map;

import com.queryio.sysmoncommon.sysmon.dstruct.DiskInfo;

public class SystemStatistics implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1205694117644994070L;
	private float recdPacketsPerSec;
	private float sentPacketsPerSec;
	private float cpuUsage;
	private float ramFree;
	private float ramTotal;
	private DiskInfo[] diskInfo;
	private String diskHealthStatistics;
	private Map<String, String[]> ipmiTemperatureMap;
	private Map<String, String[]> ipmiFanMap;
	private Map<String, String[]> ipmiVoltageMap;

	public Map<String, String[]> getIpmiTemperatureMap() {
		return ipmiTemperatureMap;
	}

	public void setIpmiTemperatureMap(Map<String, String[]> ipmiTemperatureMap) {
		this.ipmiTemperatureMap = ipmiTemperatureMap;
	}

	public Map<String, String[]> getIpmiFanMap() {
		return ipmiFanMap;
	}

	public void setIpmiFanMap(Map<String, String[]> ipmiFanMap) {
		this.ipmiFanMap = ipmiFanMap;
	}

	public Map<String, String[]> getIpmiVoltageMap() {
		return ipmiVoltageMap;
	}

	public void setIpmiVoltageMap(Map<String, String[]> ipmiVoltageMap) {
		this.ipmiVoltageMap = ipmiVoltageMap;
	}

	public float getRecdPacketsPerSec() {
		return recdPacketsPerSec;
	}

	public void setRecdPacketsPerSec(float recdPacketsPerSec) {
		this.recdPacketsPerSec = recdPacketsPerSec;
	}

	public float getSentPacketsPerSec() {
		return sentPacketsPerSec;
	}

	public void setSentPacketsPerSec(float sentPacketsPerSec) {
		this.sentPacketsPerSec = sentPacketsPerSec;
	}

	public float getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(float cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public float getRamFree() {
		return ramFree;
	}

	public void setRamFree(float ramFree) {
		this.ramFree = ramFree;
	}

	public float getRamTotal() {
		return ramTotal;
	}

	public void setRamTotal(float ramTotal) {
		this.ramTotal = ramTotal;
	}

	public DiskInfo[] getDiskInfo() {
		return diskInfo;
	}

	public void setDiskInfo(DiskInfo[] diskInfo) {
		this.diskInfo = diskInfo;
	}

	public String getDiskHealthStatistics() {
		return diskHealthStatistics;
	}

	public void setDiskHealthStatistics(String diskHealthStatistics) {
		this.diskHealthStatistics = diskHealthStatistics;
	}

}
