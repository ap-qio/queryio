
<%
	String currentView = (String) request.getSession().getAttribute("viewType");
	if ("AdminView".equalsIgnoreCase(currentView)) {
		request.getSession().setAttribute("viewType", "UserView");
	}
	else {
		request.getSession().setAttribute("viewType", "AdminView");
	}
	
	response.sendRedirect("/queryio/index.jsp");
%>