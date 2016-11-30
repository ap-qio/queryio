package com.queryio.common.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.queryio.common.QueryIOConstants;
import com.queryio.common.database.CoreDBManager;
import com.queryio.core.bean.DWRResponse;
import com.queryio.core.bean.User;
import com.queryio.core.dao.UserDAO;
import com.queryio.core.permissions.GroupDAO;
import com.queryio.core.permissions.UserGroupDAO;

public class AddUserServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			doProcess(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			doProcess(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {

		boolean isDemoUserAdded = false;

		String[] groupNames = new String[1];
		String userGroup = null;
		String userRole = null;
		String userName = null;
		String password = null;

		String email = request.getParameter("email");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");

		userRole = request.getParameter("userRole");

		if ((userRole != null) && (QueryIOConstants.DEFAULT_GROUP_NAME_DEMO.equalsIgnoreCase(userRole))) {
			userGroup = QueryIOConstants.DEFAULT_GROUP_NAME_DEMO;
			userName = email;
			userRole = "User";
			RandomString rs = new RandomString(8);
			password = rs.nextString();
			isDemoUserAdded = true;

		} else {
			userGroup = QueryIOConstants.DEFAULT_GROUP_NAME;
			userRole = QueryIOConstants.ROLES_ADMIN;
			userName = request.getParameter("userName");
			password = request.getParameter("password");
		}

		groupNames[0] = userGroup;

		Connection connection = null;

		connection = CoreDBManager.getQueryIODBConnection();
		User user = new User();
		user.setUserName(userName);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPassword(password);
		user.setEmail(email);
		DWRResponse dwrResponse = new DWRResponse();

		GroupDAO.addGroup(connection, userGroup);
		UserDAO.insertUser(connection, user, userRole, dwrResponse);
		UserGroupDAO.addUserToGroup(connection, userName, userGroup, true);

		if (dwrResponse.isTaskSuccess()) {
			if (isDemoUserAdded) {
				String message = "Demo user has been created successfully. Please login with userName as: <b>"
						+ userName + "</b> and password as: <b>" + password + "</b>";
				request.getSession().setAttribute("message", message);
				request.getRequestDispatcher("login.jsp").forward(request, response);
			} else {
				response.sendRedirect("login.jsp");
			}
		}
	}
}