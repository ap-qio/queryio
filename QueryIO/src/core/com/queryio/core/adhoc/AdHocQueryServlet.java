package com.queryio.core.adhoc;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdHocQueryServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	{
		doProcess(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		doProcess(request, response);
	}
	
	private void doProcess(HttpServletRequest request, HttpServletResponse response)
	{
		// do Nothing
	}
}