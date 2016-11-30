package com.queryio.core.permissions;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.queryio.common.database.CoreDBManager;
import com.queryio.common.util.AppLogger;
import com.queryio.core.ugiupdater.UGIProvider;

public class GroupInformationProvider extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GroupInformationProvider() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			provideGroupNames(response);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error providing user-groups.", e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			provideGroupNames(response);
		} catch (Exception e) {
			AppLogger.getLogger().fatal("Error providing user-groups.", e);
		}
	}

	private void provideGroupNames(HttpServletResponse response) throws Exception {
		PrintWriter writer = null;
		Connection connection = null;
		try {
			connection = CoreDBManager.getQueryIODBConnection();
			writer = response.getWriter();

			writer.write(UGIProvider.getUserGroupInformation());
		} finally {
			try {
				CoreDBManager.closeConnection(connection);
			} catch (Exception e) {
				AppLogger.getLogger().fatal("Error closing database connection.", e);
			}
			if (writer != null) {
				writer.flush();
			}
		}
	}
}
