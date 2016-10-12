package com.queryio.core.bean;

import java.io.Serializable;

public class Host implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private String hostIP;
	private String installDirPath;
	private String status;
	private String rackName;
	private String agentPort;
	private boolean monitor;
	private boolean isWindows;
	
	public String getRackName() {
		return rackName;
	}

	public void setRackName(String rackName) {
		this.rackName = rackName;
	}

	public String getHostIP()
	{
		return hostIP;
	}
	
	public void setHostIP(String hostIP)
	{
		this.hostIP = hostIP;
	}
	
	public String getInstallDirPath()
	{
		return installDirPath;
	}

	public void setInstallDirPath(String dirPath)
	{
		this.installDirPath = dirPath;
	}

	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String toString()
	{	
		return ("HostIP: " + this.hostIP+" ID :"+this.id);
	}
	public boolean equals(Object o){
		if(o instanceof Host){
			Host h = (Host) o;
			if(this.id == h.id)
				return true;
		}
		return false;
	}
	public int hashCode(){
		int result = 1;
		result += 31 * this.id;
		result += 31 * (this.hostIP != null ? this.hostIP.hashCode() : 0);
		result += 31 * (this.installDirPath != null ? this.installDirPath.hashCode() : 0);
		result += 31 * (this.status != null ? this.status.hashCode() : 0);
		result += 31 * (this.rackName != null ? this.rackName.hashCode() : 0);
		result += 31 * (this.agentPort != null ? this.agentPort.hashCode() : 0);
		return result;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAgentPort() {
		return agentPort;
	}

	public void setAgentPort(String agentPort) {
		this.agentPort = agentPort;
	}

	public boolean isMonitor() {
		return monitor;
	}

	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}

	public boolean isWindows() {
		return isWindows;
	}

	public void setWindows(boolean isWindows) {
		this.isWindows = isWindows;
	}
}