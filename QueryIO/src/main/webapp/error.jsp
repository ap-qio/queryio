<%@page import="com.queryio.common.EnvironmentalConstants"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<%
if(request.getRemoteUser()!=null)	response.sendRedirect("index.jsp");
%>
	<head>
	<title>Login</title>
		<link rel="stylesheet" href="styles/global.css" type="text/css" />
		<link rel="stylesheet" href="styles/nav_bar.css" type="text/css" />
		<link rel="stylesheet" href="styles/surround_box.css" type="text/css" />
		<script type="text/javascript" src="scripts/navbar.js"></script>
		<script type="text/javascript" src="scripts/jquery-1.7.2.min.js"></script>
		<script src="scripts/jquery-ui-1.8.20.custom.min.js"></script>
	</head>
	<body>
		<div id="main">
		<div id="nav_bar" class="nav_bar">
			<div class="nav"> 
				<p id="user_welcome">
				<span id="login_info"><a href="javascript:Navbar.helpClicked();">Help</a> | Welcome Guest </span>
				<span id="logo">
					<a href="javascript:Navbar.changeTab('dashboard');"><img src="images/queryio_logo.png" style="width: 220px;height: 70px;"></a>
				</span>
				</p>
<!-- 				<span style="font-size: 36px; color: #1d5867; padding-left: 40px;" >Big Data Server</span> -->
			</div>
		</div>
		<div id="surround_box_div">
	 			<div id="surround_box">
	 				<span id="header_row" style="background: -webkit-gradient(linear, 0% 0%, 0% 80%, from(#579), to(#246));background-color:#135;color:white; ">
	 					
						<span id="header" class="full-height"  style="margin-left : 50%">
	 						Login
	 					</span>
	 				</span>
	  			<div>
  			</div>
  			<div id="main_box" class="main_box" style="border-bottom : 1px solid #DDD">
  			
	
	<div id = "formdiv"  style="margin-left : 43%">
	
	<%
		String message = String.valueOf(request.getSession().getAttribute("message"));
	
		System.out.println("message: " + message);
	
		if (EnvironmentalConstants.isDemoVersion() && (message != null) && !("null".equalsIgnoreCase(message)))
		{
	%>
			<p style="font-size: 11px;"><%=message %></p>
	<%
		}
	%>
	<br />
	<form method="post" id="loginForm" action="j_security_check"  name="loginForm" onsubmit="return validateAndStoreLoginDetail();">	
		<span style = "color : red; padding-left: 5px;"> Invalid username / password. Please enter correct credentials to login.</span>
		<table>
		    <tr id="uname">
		       	<td class="desc" style="width: 50px;">
		            Username
		   		</td>
		        <td>
		        	<input type="text" class="text medium" name="j_username" id="j_username" tabindex="1" style="margin-bottom: 5px; width: 130px;">
		    	</td>
		    </tr>
		
		    <tr id="passwd" style="margin-top: 5px;">
		        <td class="desc" style="width: 50px;">
		            Password
		        </td>
		        <td>
		        	<input type="password" class="text medium" name="j_password" id="j_password" tabindex="2" style="width: 130px;">
		    	</td>
		    </tr>
		    <tr>
		    	<td colspan="2" style="text-align: center;">
					<input type="submit" class="button" name="login" value="Login" tabindex="4" style="margin-left: 10%; background: -webkit-gradient(linear, 0% 0%, 0% 80%, from(#579), to(#246) ); color: white; border-radius: 8px; background-color: #135;">
			<%
				if (EnvironmentalConstants.isDemoVersion())
				{
			%>
					<input type="button" class="button" name="NewUser" value="New User" tabindex="4" style="margin-left: 5%; background: -webkit-gradient(linear, 0% 0%, 0% 80%, from(#579), to(#246) ); color: white; border-radius: 8px; background-color: #135; padding: 5px 10px 6px;">
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
//	checkUrl();

function validateAndStoreLoginDetail()
{
		
	   if( document.loginForm.j_username.value == "" )
	   {
	   	 document.getElementById("message").text = "Username was not provided. Please provide a valid username.";
	     document.loginForm.j_username.focus() ;
	     return false;
	   }
	   else{
		   //Util.setCookie("queryioUser",document.loginForm.j_username.value,-1);
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

	function  checkUrl()
	{
		console.log('checkURL called.');
		var domain;
		var loginUrl = 'login.jsp'
		
		var urLength , domainLength;
		var index;
		var url1 = document.URL;

		urLength = url1.length;
		domainLength = url1.indexOf('j_secu');
		if(domainLength != -1)
		{
			domain = url1.substring(0 , domainLength);
			url1 = url1.substring(domainLength , urLength);
			if(url1=='j_security_check')
			{
				domain = domain + loginUrl;
				window.location = ''+domain;
			}
		}
	}
	$(window).bind('hashchange', function() {
	 checkUrl();
	 
	});
		
	</script>
</html>	