<%@page import="com.queryio.core.conf.RemoteManager"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title>Demo User Registration</title>
	<link rel="stylesheet" href="styles/global.css" type="text/css" ></link>
	<link rel="stylesheet" href="styles/nav_bar.css" type="text/css" ></link>
	<link rel="stylesheet" href="styles/surround_box.css" type="text/css" ></link>
	<script src="scripts/jquery-1.7.2.min.js"></script>
	<script src="scripts/jquery.alerts.js"></script>
	<link rel="stylesheet" href="styles/jquery.alerts.css" type="text/css" ></link>
	
	<script type="text/javascript" src="scripts/admin_users.js"></script>
	<script type="text/javascript" src="scripts/navbar.js"></script>
	<script type="text/javascript" src="scripts/util.js"></script>
	</head>
	<body>
		<div id="main">
			<div id="nav_bar" class="nav_bar">
				<div class="nav"> 
					<p id="user_welcome">
					<span id="login_info">Welcome Guest </span>
					<span id="logo">
						<a href="javascript:Navbar.changeTab('dashboard');"><a href="javascript:Navbar.changeTab('dashboard');"><img src="images/queryio_logo.png" style="width: 220px;height: 70px;"></a></a>
					</span>
					</p>
					<!-- <span style="font-size: 36px; color: #135; padding-left: 40px;" >Big Data Server</span> -->
				</div>
			</div>
			<div id="surround_box_div">
	 			<div id="surround_box">
	 				<span>
	 					<hr>
	 				</span>
	 				<span id="header_row" style="background: -webkit-gradient(linear, 0% 0%, 0% 80%, from(#579), to(#246));background-color:#135;color:white; margin-top: 10px; width: 50%; margin-left: 25%;">
	 					<span id="header" class="full-height" style="margin-left : 38%; font-weight: bold; font-size: 13px;">
	 						First time demo user registration
	 					</span>
	 				</span>
	  			
	  				<div id="main_box" class="main_box" style="border-bottom : 1px solid #DDD; margin-left: 25%; width: 50%;">
						<br>
						<div class="instructional" style="margin: 0 4%; font-size: 12px;">Welcome to QueryIO, Hadoop-based SQL & Big Data Analytics Solution.
							 Please configure login details for demo user of live QueryIO instance. You will need these details to login to the QueryIO server.
						<br>
						<br>
						Please go through <a href="javascript:Navbar.readmeClicked();">Read Me</a> document in its entirety before using the product.
						</div>
						<br>
						<hr  style="width: 95%; margin: 0 auto;">
						<div id = "formdiv" style="margin-left : 25%">
						<br>
							<form id="userForm" name="userForm" method="post" action="addUser.do" >
								<table class="viewTable" style="font-size: 10px; width: 75%;">
									<tbody>
										<tr>
											<td colspan="2" style="text-align: center; font-size: 10px;">
												<span id="errorMsg" style="color : red"> </span>
											</td>
										</tr>
										<tr>
											<td nowrap="nowrap" style="font-size: 11px;"><span style="color : red">*</span>E-mail</td>
											<td><span><input type="text" id="email" name="email">
											</span></td>
										</tr>
										<tr>
											<td nowrap="nowrap" style="font-size: 11px;"><span style="color : red">*</span>First Name</td>
											<td><span><input type="text" id="firstName" name="firstName">
											</span></td>
										</tr>
										<tr>
											<td nowrap="nowrap" style="font-size: 11px;"><span style="color : red">*</span>Last Name</td>
											<td><span><input type="text" id="lastName" name="lastName">
											</span></td>
										</tr>
										<tr style="display: none;">
											<td nowrap="nowrap" style="font-size: 11px;"></td>
											<td><span><input type="text" id="userRole" name="userRole" value="demo">demo</span></td>
										</tr>
										<tr>
											<td colspan="2">
											</td>
										</tr>
										<tr>
											<td colspan="2" style="padding-left: 20px;text-align: center;">
												<input type="button" class="button" name="login" value="Add" onclick="javascript:saveUser();" style="width: auto; background: -webkit-gradient(linear, 0% 0%, 0% 80%, from(#579), to(#246)); color: white; border-radius: 8px;background-color: #135;">
												<input type="reset" class="button" id="cancel.user" value="Reset" style=" padding: 3px 10px 5px; width: auto; background: -webkit-gradient(linear, 0% 0%, 0% 80%, from(#579), to(#246)); color: white; border-radius: 8px;background-color: #135;">
											 </td>
										</tr>
										
									</tbody>
								</table>
							</form>
						</div>
  					</div>
  				</div>
  			</div>
		</div>
	</body>
	<script type="text/javascript">
	function saveUser(){
		document.getElementById("errorMsg").innerHTML="";
		
		   if( document.userForm.firstName.value == "" )
		   {
			   document.getElementById("errorMsg").innerHTML="Please provide First Name";
		     document.userForm.firstName.focus() ;
		     return false;
		   }
		   if( document.userForm.lastName.value == "" )
		   {
			   document.getElementById("errorMsg").innerHTML="Please provide Last Name";
		     document.userForm.lastName.focus() ;
		     return false;
		   }
		  
		   if( document.userForm.email.value == "" )
		   {
			   document.getElementById("errorMsg").innerHTML="Email was not entered. Please enter a valid email id.";
		     document.userForm.email.focus() ;
		     return false;
		   }
		   if(!Util.validateEmail(document.userForm.email.value))
		   {
			   document.getElementById("errorMsg").innerHTML="Email address id not valid . Please enter a valid email id.";
		     document.userForm.email.focus() ;
		     return false;
		   }
		  
		   document.forms["userForm"].submit();
		   
		   
		   /* return true; */
	}
	</script>
</html>	