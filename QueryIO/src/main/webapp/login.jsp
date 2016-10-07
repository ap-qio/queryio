<%@page import="com.queryio.core.conf.RemoteManager"%>
<%@page import="com.queryio.common.EnvironmentalConstants"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<%
 	String mess = String.valueOf(request.getSession().getAttribute("message"));
	System.out.println("mess: " + mess);
	
	if (request.getRemoteUser() != null) {
		if(!request.getRemoteUser().equalsIgnoreCase("null"))
			response.sendRedirect("index.jsp");
	} else {
		int userCount = RemoteManager.getUserCount();

		if (userCount == 0) {
			response.sendRedirect("firstUserRegistration.jsp");
		}
	}
 
%>
<head>
<title>Login</title>
<link rel="stylesheet" href="styles/nav_bar.css" type="text/css" />
<link rel="stylesheet" href="styles/surround_box.css" type="text/css" />
<script type="text/javascript" src="scripts/navbar.js"></script>
<script type="text/javascript" src="scripts/util.js"></script>
<script type="text/javascript" src="scripts/jquery-1.7.2.min.js"></script>
<script src="scripts/jquery-ui-1.8.20.custom.min.js"></script>

<!--[if IE]>
		<meta http-equiv="X-UA-Compatible" content="IE=9" />
		<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
		<link rel="stylesheet" href="styles/globalforie.css" type="text/css" />
		<![endif]-->
<![if !IE]>
<link rel="stylesheet" href="styles/global.css" type="text/css" />
<![endif]>

<link rel="SHORTCUT ICON" href="images/favicon.ico">

<script>
var isLoggedIn = <%=request.getUserPrincipal()%>;

var uri = window.location.href;
var pattern = new RegExp("(/login.jsp)$");
if( ! uri.match(pattern)) {
	if(isLoggedIn == null) {
		var loginLocation = window.location.protocol + "//" + window.location.host + "/queryio/login.jsp";
		window.location = loginLocation;
	}
}
</script>

</head>
<body>
	<div id="main">
		<div id="nav_bar" class="nav_bar">
			<div class="nav">
				<p id="user_welcome">
					<span id="login_info"><a
						href="javascript:Navbar.helpClicked();">Help</a> | Welcome Guest </span>
					<span id="logo"> <a
						href="javascript:Navbar.changeTab('dashboard');"><a
							href="javascript:Navbar.changeTab('dashboard');"><img
								src="images/queryio_logo.png"
								style="width: 220px; height: 70px;"></a></a>
					</span>
				</p>
				<!-- 				<span style="font-size: 36px; color: #135; padding-left: 40px;" >Big Data Server</span> -->
			</div>
		</div>
		<div id="surround_box_div">
			<div id="surround_box">
				<span id="header_row"
					style="background: -webkit-gradient(linear, 0% 0%, 0% 80%, from(#579), to(#246) ); background-color: #135; color: white;">
					<span id="header" class="full-height" style="margin-left: 50%">
						Login </span>
				</span>
				<div></div>
				<div id="main_box" class="main_box"
					style="border-bottom: 1px solid #DDD">
					<br>
					<div id="formdiv" style="margin-left: 43%">
					<%
						String message = String.valueOf(request.getSession().getAttribute("message"));
					
						System.out.println("message: " + message);
					
						if (EnvironmentalConstants.isDemoVersion() && (message != null) && !("null".equalsIgnoreCase(message)))
						/* if (EnvironmentalConstants.isDemoVersion()) */
						{
					%>
							<p style="font-size: 11px;" id="message"><%=message %></p>
					<%
						}
					%>
						<br>
						<form method="post" id="loginForm" name="loginForm" action="j_security_check" onsubmit="return validateAndStoreLoginDetail();">
							<table>
								<tr id="uname" style="display: block;">
									<td style=" width: 50px">
										Username 
									</td>
									<td>
										<input type="text" class="text medium" name="j_username" id="j_username" tabindex="1" style="margin-bottom: 5px; width: 130px;">
									</td>
								</tr>

								<tr id="passwd" style=" display: block;">
									<td style=" width: 50px"> 
										Password 
									</td> 
									<td>
										<input type="password" class="text medium" name="j_password" id="j_password" tabindex="2" style=" width: 130px;">
									</td>
								</tr>
								<tr style="display: ; text-align: center;" >
									<td colspan="2" style="text-align: center;">
										<input type="submit" class="button" name="login" value="Login" tabindex="4" style="margin-left: 10%; background: -webkit-gradient(linear, 0% 0%, 0% 80%, from(#579), to(#246) ); color: white; border-radius: 8px; background-color: #135;">
										
								<%
									if (EnvironmentalConstants.isDemoVersion())
									{
								%>
										<input type="button" class="button" name="NewUser" value="New User" onclick="javascript:redirectToDemo();" tabindex="4" style="margin-left: 5%; background: -webkit-gradient(linear, 0% 0%, 0% 80%, from(#579), to(#246) ); color: white; border-radius: 8px; background-color: #135; padding: 5px 10px 6px;">
								<%
									}
								%>
									</td>
								</tr>
							</table>
						</form>
					</div>

				</div>
			</div>
</body>
<script type="text/javascript">

function validateAndStoreLoginDetail()
{
		
	   if( document.loginForm.j_username.value == "" )
	   {
	   	 document.getElementById("message").text = "Username was not provided. Please provide a valid username.";
	     document.loginForm.j_username.focus() ;
	     return false;
	   }
	   else{
		   Util.setCookie("queryioUser",document.loginForm.j_username.value,-1);
		   Util.setCookie("queryioUser",document.loginForm.j_username.value,100);
	   }
	   if(document.loginForm.j_password.value == "")
	   {
		   document.getElementById("message").text = "Password was not provided. Please provide a valid password for user."
		   document.loginForm.j_password.focus() ;
	     return false;
	   }	
	   
	   return true;
	   
}

	function redirectToDemo()
	{
		<% System.out.println("redirectToDemo"); %>
		var demoLoginLocation = window.location.protocol + "//" + window.location.host + "/queryio/demoUserRegistration.jsp";
		window.location = demoLoginLocation;
	}

</script>
</html>