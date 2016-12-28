<%@page import="java.security.Principal"%>
<%@page language="java" import="java.util.Set"%>
<%@page language="java" import="java.util.ArrayList"%>
<%@page language="java" import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page language="java" import="java.util.HashMap"%>
<%@page language="java" import="com.queryio.core.conf.RemoteManager"%>
<%@page language="java" import="org.apache.catalina.realm.GenericPrincipal"%>
<%
	
//String username = request.getRemoteUser();
String username = request.getUserPrincipal().getName();
System.out.println("Roles: ");
final Principal userPrincipal = request.getUserPrincipal();
GenericPrincipal genericPrincipal = (GenericPrincipal) userPrincipal;
final String[] roles = genericPrincipal.getRoles();
for(String role: roles){
	System.out.println(role);	
}
if (username == null)
{
	System.out.println("username: " + username);
}

%>
<!--[if IE]>
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<link rel="stylesheet" href="styles/globalforie.css" type="text/css" />
<![endif]-->
<![if !IE]>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 <link rel="stylesheet" href="styles/global.css" type="text/css" />
<![endif]>
<link rel="SHORTCUT ICON" href="images/favicon.ico">

<html>
	<head>
		<title>QueryIO - Hadoop based SQL & Big Data Analytics Solution</title>
		
		<!-- uncomment it for demo.queryio -->
		<link rel="stylesheet" href="demo.css" type="text/css" />
		<link rel="stylesheet" href="styles/jquery-ui-1.8.20.custom.css" type="text/css" />
		
		
		<script type="text/javascript" src="scripts/jquery-1.7.2.min.js"></script>
		<script type="text/javascript" src="scripts/jquery-ui-1.8.20.custom.min.js"></script>
		<script type="text/javascript" src="demo.js"></script>
		<script type="text/javascript" src="dwr/engine.js"></script>
		<script type="text/javascript" src="dwr/util.js"></script>
		<script type="text/javascript" src="dwr/interface/RemoteManager.js"></script>
		<script type="text/javascript" src="scripts/navbar.js"></script>
		
		
		<!-- comment it for demo.queryio -->
		<!-- 
		<script type="text/javascript" src="scripts/jquery-1.7.2.min.js"></script>
		<script type="text/javascript" src="scripts/popup.js"></script>
		<script type="text/javascript" src="scripts/jquery.alerts.js"></script>
		<script type="text/javascript" src="scripts/jquery-ui-1.8.20.custom.min.js"></script>
		<script type="text/javascript" src="scripts/navbar.js"></script>
		<script type="text/javascript" src="scripts/util.js"></script>
		<script type="text/javascript" src="scripts/jquery.treeTable.js"></script>
		<script type="text/javascript" src="scripts/jquery.dataTables.js"></script>
		
		<script type="text/javascript" src="dwr/engine.js"></script>
		<script type="text/javascript" src="dwr/util.js"></script>
		<script type="text/javascript" src="dwr/interface/RemoteManager.js"></script>
		<script type="text/javascript" src="scripts/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="scripts/jquery-ui-timepicker-addon.js"></script>
		<script src="scripts/data_migration.js"></script>
		<link rel="stylesheet" href="styles/jquery-ui-1.8.20.custom.css" type="text/css" />
		<link rel="stylesheet" href="styles/nav_bar.css" type="text/css" />
		<link rel="stylesheet" href="styles/surround_box.css" type="text/css" />
		<link rel="stylesheet" href="styles/tree.css" type="text/css" />
		<link rel="stylesheet" href="styles/jquery.treeTable.css" type="text/css" />
		<link rel="stylesheet" href="styles/lightbox.css" type="text/css" />
		<link rel="stylesheet" href="styles/ui.jqgrid.css" type="text/css" />
		<link rel="stylesheet" href="styles/nd.css" type="text/css" />
		<link rel="stylesheet" href="styles/jquery.alerts.css" type="text/css" />
		<link rel="stylesheet" href="styles/jquery.jqplot.min.css" type="text/css" />
		<link rel="stylesheet" href="styles/jquery.dataTables.css" type="text/css" />
		<link rel="stylesheet" href="styles/jquery-ui-timepicker-addon.css" type="text/css" />
		
		 -->
		<script type="text/javascript">

		checkUrl();
		
		function  checkUrl()
		{
			var domain;
			var logoutUrl = 'logout.jsp'
			
			var urLength , domainLength;
			var index;
			var url1 = document.URL;
			urLength = url1.length;
			domainLength = url1.indexOf('login');
			if(domainLength != -1)
			{
				domain = url1.substring(0 , domainLength);
				url1 = url1.substring(domainLength , urLength);
				if(url1=='login.jsp')
				{
					domain = domain + loginUrl;
			
				}
			}
			
			
		}
		
		$(window).bind('hashchange', function() {
		 checkUrl();
		 
		});
	
		

				function showDropDown(index){
					var li = document.getElementById('menu_ul').childNodes;
					var ul = li[index].childNodes;
					if(ul[ul.length-2].hasOwnProperty('style')) {
						ul[ul.length-2].style.display = '';					
					}
				}
				
				function hideDropDown(index){
					var li = document.getElementById('menu_ul').childNodes;
					var ul = li[index].childNodes;
					if(ul[ul.length-2].hasOwnProperty('style')) {
						ul[ul.length-2].style.display = 'none';						
					}
				}
		
		
			var selectedNameNode;
			var selectedDataNode;
			var selectedNodeTypeForDetailView;
		
			var treeHidden = false;
			function hideMenu()
			{
				if(treeHidden)
				{
					document.getElementById("services_div").style.display = "block";
					document.getElementById("service_ref").style.width = "85%";
					document.getElementById("hideTreeButton").innerHTML = "<img src='images/hide_button.png' alt='Hide Menu' title='Hide Menu'/>";
				}
				else
				{
					document.getElementById("services_div").style.display = "none";
					document.getElementById("service_ref").style.width = "100%";
					document.getElementById("hideTreeButton").innerHTML = "<img src='images/reveal_button.png' alt='Show Menu' title='Show Menu'/>";
				}
				treeHidden = !treeHidden;
			}

			

			var serverTime;
			var isFirstTime = true;
			var LOC_INDEX = {
				ready: function() {
					
				}
			}

			function showUserName(detailObject)
			{
				if (detailObject != null)
				{
					var user = detailObject.user;
					if (user != null)
					{
						document.getElementById("loggedInUser").firstChild.nodeValue = user.firstName + ' ' + user.lastName;
						document.getElementById("loggedInUserId").innerHTML=user.id;
						document.getElementById("loggedInUserName").innerHTML=user.userName;
					}
					serverTime = detailObject.time;
					
					updateClock();
	 				
	 				setInterval('updateClock()', 1000);
				}
				
				/* RemoteManager.getServerTime(fetchServerTime); */
			}
			
			function getUserName(currentUser)
			{
				RemoteManager.getCurrentUserDetail(currentUser, showUserName);
			}
			
			/* function fetchServerTime(time)
			{
				console.log("fetchServerTime: " , time);
 				serverTime = time;
 				
 				updateClock();
 				
 				setInterval('updateClock()', 1000);
			} */
 
			function updateClock()
			{
				var currentTime;
				if (isFirstTime)
				{
					currentTime = new Date(serverTime);
					isFirstTime = false;
				}
				else{
					currentTime = new Date();
				}
			  	document.getElementById("clock").firstChild.nodeValue = currentTime;
			}
		</script>
		<style type="text/css">
			.submenu{display: none}
		</style>
		<style type="text/css">
			.subcategoryitems{display: none}
		</style>
		<style type="text/css">
			.sub2categoryitems{display: none}
		</style>
	</head>
	<body class = "appleGrey" onload="">
		<div id="main">
			<div id="nav_bar" class="nav_bar">
				<div class="nav">
					<p id="user_welcome">
					<span id="login_info"><span id="clock">&nbsp;</span>| Hello,<span id="loggedInUser">&nbsp;</span><span id = "loggedInUserName" style="display: none;"></span><span id = "loggedInUserId" style="display: none;"></span>| <a href="javascript:Navbar.helpClicked();">Help</a> | <a href="javascript:Navbar.gettingStarted();">Quick Start</a> | <a href="logout.jsp">Logout</a></span>
					<span id="logo">
						<a href="javascript:Navbar.changeTab('Dashboard','dashboard');">
							<img src="images/queryio_logo.png" style="width: 220px;height: 70px;">
						</a>
					</span>
					</p>
				</div>
			</div>
		</div>
		<div id="surround_box_div">
	 		<div id="surround_box">
	 			<!-- menu goes here -->
	 			
				<div id="menu_container" > 			
				</div>			
	 			
	 			<!-- menu end here -->
	 			
				<div id="header_row" style="display: none;">
				</div>
		
				<div>
	  			</div>
	  			<!-- <div id="main_box" class="main_box">
	  				</div>-->
				<div id="right_header" class="right_header" style="width: 100%; float: left; ">
					<span class="right_header_span" id="rhs_header" style="display: none">Dashboard</span>
					<div id="pathbar_div"></div>
					<button class="button" id="refreshViewButton" onclick="javascript:Navbar.refreshView()" style="float: right;  display: block;height: 94%;margin-bottom: 1px;margin-right: 5px;margin-top: 1px;font-size: 100%;">Refresh</button>
<!-- 				<button id = "resumeRefresh" onclick = "javascript:Navbar.toggleRefresh(true);" style = "float: right; margin: 3px 20px; display: none;">Resume</button>
					<button id = "pauseRefresh" onclick = "javascript:Navbar.toggleRefresh(false);" style = "float: right; margin: 3px 20px;">Pause</button>
					<span id = "autoRefreshSpan" style = "float: right; margin-top: 3px;">
						<label>Auto Refresh Interval </label>
						<input id = "refreshTimeout" onchange = "javascript:Navbar.setAutoRefreshTimeout();" type = "text" placeholder = "(in seconds)" />
					</span>  -->
					<span id="intervalHeader" style="float: right;color: black; font-weight: normal; font-size: 16px; padding: 5px; display: none;">
						<span style="float: right; margin-right: 10pt;">
							<select style = "font-size: 60%; padding: 0px 0px 0px 0px;" id='chartInterval' onchange="javascript:Navbar.changeInterval(this.options[this.selectedIndex].value);">
								<option selected value='onehour'>1 hour</option>
								<option value='oneday'>1 day</option>
								<option value='oneweek'>7 days</option>
								<option value='onemonth'>30 days</option>
								<option value='quarter'>90 days</option>
								<option value='halfyear'>180 days</option>
								<option value='oneyear'>360 days</option>
							</select>
						</span>
					</span>
					<button class="button" type="button" id = "migrationStatusBtn" name="migrationStatusBtn" onclick="javascript:DB_Config.moveToMigrationStatus();" style="float: right; font-size: 100%; height: 94%; margin-top: 1px; margin-bottom: 1px; margin-right: 5px; display: none;">Migration Statistics</button>
					<span id="queryIONameNodeIdSpan" style="float: right;color: black; font-weight: normal;  padding: 5px; display: none;">
						<span style=" margin-right: 10pt;">
							<span>	Namespace :	</span>
							<select id="queryIONameNodeId" style = "font-size: 100%;padding: 0px 0px 0px 0px;" onchange="javascript:Navbar.changeQueryIONameNodeId(this.options[this.selectedIndex].value);">
							</select>
						</span>
					</span>
				</div>
			
				<div id="service_ref" class="right" style="overflow: auto; float: none;">
				</div>
				
	  		</div>
		</div>
	</body>
	<script type="text/javascript">
	var queryioUser = "<%= request.getUserPrincipal().getName() %>" ;
	getUserName(queryioUser);
	<% Object message = request.getSession().getAttribute("message");
		System.out.println("message : " + message);
		boolean promtPassword = message != null ? true : false;
		request.getSession().removeAttribute("message");
	%>
	Navbar.promptPasswordChange = <%= promtPassword %>
	/* Remove this later */
	/* Navbar.promptPasswordChange = true; */
	/*  */
	Navbar.ready();
		var lastSelectedId = 'Dashboard';
		var isFirstTimeCluster = true;
		var isFirstTime = true;
		var table_count=0;
		$(LOC_INDEX.ready);
		String.prototype.trim=function(){return this.replace(/^\s+|\s+$/g, '');};
	</script>
	<script type="text/javascript" src="scripts/jquery.dataTables.js"></script>
	<script type="text/javascript" src="scripts/js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="scripts/jquery-ui-timepicker-addon.js"></script>
	<link rel="stylesheet" href="styles/ui.jqgrid.css" type="text/css" />
	<link rel="stylesheet" href="styles/nd.css" type="text/css" />
	<link rel="stylesheet" href="styles/jquery.jqplot.min.css" type="text/css" />
	<link rel="stylesheet" href="styles/jquery-ui-timepicker-addon.css" type="text/css" />
	<link rel="stylesheet" href="styles/jquery.dataTables.css" type="text/css" />
		
</html>