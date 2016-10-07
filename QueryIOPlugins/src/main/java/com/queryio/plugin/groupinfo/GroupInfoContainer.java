package com.queryio.plugin.groupinfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GroupInfoContainer {
	private static final Log LOG = LogFactory.getLog(GroupInfoContainer.class);

	private static HashMap<String, List<String>> groupInfoMap = new HashMap<String, List<String>>();

	public static void setGroupInfo(String groupInfo) {
		if (groupInfo == null) {
			LOG.debug("Group information is not available");
		}

		LOG.info("Retreived group info: " + groupInfo);
		
		if (groupInfo != null) {

			HashMap<String, List<String>> userGroups = new HashMap<String, List<String>>();

			String[] lines = groupInfo.split("@NEWLINE@");

			for (int i = 0; i < lines.length; i++) {

				String userName = lines[i].split(":")[0];
				String groupNames = lines[i].split(":")[1];

				String[] groupArray = groupNames.split(",");

				List<String> list = new LinkedList<String>();

				for (int j = 0; j < groupArray.length; j++) {
					if (!groupArray[j].equals(""))
						list.add(groupArray[j]);
				}

				userGroups.put(userName, list);

			}
			
			groupInfoMap = userGroups;
		}
		
		printMap(groupInfoMap); // LOG
	}

	public static HashMap<String, List<String>> getGroupInfo() {
		return groupInfoMap;
	}
	
	public static void printMap(Map mp) {
		LOG.info("UGI----------");
	    Iterator it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        LOG.info(pairs.getKey() + " = " + pairs.getValue());
	    }
	    LOG.info("----------UGI");
	}
	
	public static void refreshGroupInformation(String groupInfo){
		groupInfoMap.clear();
		setGroupInfo(groupInfo);
	}
}
