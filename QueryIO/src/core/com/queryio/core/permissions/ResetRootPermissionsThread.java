package com.queryio.core.permissions;

import com.queryio.common.util.AppLogger;
import com.queryio.core.agent.QueryIOAgentManager;
import com.queryio.core.bean.Host;
import com.queryio.core.bean.Node;

public class ResetRootPermissionsThread extends Thread {
	Host host;
	Node node;
	String path;
	String group;
	short permissions;
	boolean recursive;
	
	public ResetRootPermissionsThread(Host host, Node node, String path, String group, short permissions, boolean recursive){
		this.host = host;
		this.node = node;
		this.path = path;
		this.group = group;
		this.permissions = permissions;
		this.recursive = recursive;
	}
	
	public void run(){
		String rootUser = QueryIOAgentManager.getOSUserName(host,
				node);
		
		// Wait first time to get namenode started.
		try {
			Thread.sleep(40000);
		} catch (InterruptedException e) {
			AppLogger.getLogger().fatal(e.getMessage(), e);
		}
		
		if (rootUser != null){
			boolean success = false;
			while( ! success){
				
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Attempting to reset permissions on root directory on node: " + this.node.getId());
				if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("rootUser : " + rootUser);
				success = PermissionsManager.setFileOwnerAndPermissions(this.node.getId(),
						rootUser, this.path, rootUser, this.group, permissions, recursive);
						
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					AppLogger.getLogger().fatal(e.getMessage(), e);
				}
			}
			
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Permissions reset successful on node: " + this.node.getId());
		} else {
			if(AppLogger.getLogger().isDebugEnabled()) AppLogger.getLogger().debug("Could not reset permissions on node: " + this.node.getId() + ", username not available");
		}
	}
}
