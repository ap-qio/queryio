package com.queryio.ftpserver.userinfo;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.queryio.common.util.SecurityHandler;

public class UserInfoContainer {
	protected static final Logger logger = Logger.getLogger(UserInfoContainer.class);

	private static HashMap<String, String> userInfoMap = new HashMap<String, String>();
	private static HashMap<String, String> userGroupMap = new HashMap<String, String>();
	
	public static void setUserInfo(HashMap<String, String> userInfo){
		userInfoMap = userInfo;
	}
	
	public static void setUserGroupInfo(HashMap<String, String> userInfo){
		userGroupMap = userInfo;
	}
	
	public static void setUserInfo(String userInfo) {
		logger.debug("Updating user info");
		
		if (userInfo == null) {
			logger.debug("User information is not available");
		}

		if (userInfo != null) {

			HashMap<String, String> users = new HashMap<String, String>();
			HashMap<String, String> groups = new HashMap<String, String>();

			String[] lines = userInfo.split("@NEWLINE@");

			for (int i = 0; i < lines.length; i++) {

				String userName = lines[i].split(":")[0];
				String password = lines[i].split(":")[1];
				String group = lines[i].split(":")[2];

				users.put(userName, password);
				groups.put(userName, group);

				logger.debug("USER: " + userName + " : " + password + " : " + group);
			}
			
			synchronized(userInfoMap){
				userInfoMap = users;
			}
			synchronized(userGroupMap){
				userGroupMap = groups;
			}
		}
	}

	public static String getDefaultGroupForUser(String userName){
		synchronized(userGroupMap){
			return userGroupMap.get(userName);
		}
	}
	
	public static boolean validateUser(String userName, String password) throws Exception{
		synchronized(userInfoMap){
			String pass = userInfoMap.get(userName);
		
			if(pass!=null){
				pass = SecurityHandler.decryptData(pass);
			}
			
			if(pass!=null && pass.equals(password)){
				return true;
			}
			
			return false;
		}
	}
}
