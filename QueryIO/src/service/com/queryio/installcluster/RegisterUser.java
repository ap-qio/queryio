package com.queryio.installcluster;

import java.sql.Connection;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.User;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.permissions.GroupDAO;
import com.queryio.core.permissions.UserGroupDAO;

public class RegisterUser {
	

	public void addUser(String qioUSername, String qioPassword, String qioFname, String qioLname, String qioEmail)
	{
		
		String[] groupNames = new String[1];
		String userGroup = QueryIOConstants.DEFAULT_GROUP_NAME;
		String userRole = QueryIOConstants.ROLES_ADMIN;
		String userName = qioUSername;
		String firstName = qioPassword;
		String lastName = qioFname;
		String password = qioLname;
		String email = qioEmail;
		groupNames[0]=userGroup;
		System.out.print("\n UserName :"+userName+", "+userRole+","+userGroup);
		Connection connection = null;
		DWRResponse dwrResponse = new DWRResponse();
		try {
			connection = CoreDBManager.getQueryIODBConnection();
		
			User user = new User();
			user.setUserName(userName);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setPassword(password);
			user.setEmail(email);
			dwrResponse =new DWRResponse();
			
			GroupDAO.addGroup(connection, userGroup);
			UserDAO.insertUser(connection, user, userRole, dwrResponse);
			UserGroupDAO.addUserToGroup(connection, userName, userGroup, true);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		System.out.println("DwrResponse :"+dwrResponse.getResponseMessage()+" status :"+dwrResponse.isTaskSuccess());
		
			if(dwrResponse.isTaskSuccess())
				System.out.println("User Successfully added");
			else
				System.out.println("Failed to register new user. ");
		
	}

}
