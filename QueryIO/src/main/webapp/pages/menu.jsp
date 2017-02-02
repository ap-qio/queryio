<%@page import="com.queryio.core.bean.Node"%>
<%@page import="com.queryio.core.bean.Host"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.queryio.core.conf.RemoteManager"%>

<!--
<link rel="stylesheet" href="styles/menu.css" type="text/css" />
<script src="scripts/navbar.js"></script>
<script type="text/javascript" src="scripts/jquery-1.7.2.min.js"></script>
<link rel="stylesheet" href="styles/jquery.jqplot.min.css" type="text/css" />
<link rel="stylesheet" href="styles/jquery.dataTables.css" type="text/css" />
<link rel="stylesheet" href="styles/dashboard.css" type="text/css" />
<script src="scripts/jquery.treeTable.js"></script>
<script src="scripts/jquery.jqplot.js"></script>


<script src="scripts/dashboard.js"></script>


<link rel="stylesheet" href="styles/tree.css" type="text/css" />
		<link rel="stylesheet" href="styles/jquery.treeTable.css" type="text/css" />
		<script src="scripts/jquery-ui-1.8.20.custom.min.js"></script>

<script src="scripts/jquery.dataTables.js"></script>
-->
<script type="text/javascript">
	
</script>


<div id="menu_old" style="margin-bottom: -8px;">

	<%
		ArrayList hostIPList = RemoteManager.getAllHostDetails();
	%>
	<ul id="menu_ul">
		<%
			if (!"AdminView".equalsIgnoreCase((String) request.getSession().getAttribute("viewType"))) {
		%>

		<li onmouseout="showDropDown(1);" id="Data_li"
			style="margin-left: 1px;" class="first user"><a id="Data"
			href="javascript:Navbar.changeTab('Data Migration','data', 'data_overview');hideDropDown(1);">Data</a>
			<ul>
				<li onmouseout="showDropDown(1);"><a id="Data Migration"
					href="javascript:Navbar.changeTab('Data Migration','data', 'data_migration');hideDropDown(1);">Import/Export</a></li>
				<li onmouseout="showDropDown(1);"><a
					href="javascript:Navbar.changeTab('Data','data');hideDropDown(1);">Browse</a></li>
				<li onmouseout="showDropDown(1);"><a
					href="javascript:Navbar.changeTab('DB_Config','data','define_schema');hideDropDown(1);">Define Schema</a></li>
				<li onmouseout="showDropDown(1);"><a
					href="javascript:Navbar.changeTab('DB_Config','data','define_data_tags');hideDropDown(1);">Define Tags</a></li>				
			</ul>
		</li>

		<li onmouseout="showDropDown(3);" id="Analytics_li" class="user"><a
			href="javascript:Navbar.changeTab('Analytics','analytics', 'analytics_overview');hideDropDown(3);">Analyze</a>
			<ul>
				<li onmouseout="showDropDown(3);"><a
					href="javascript:Navbar.changeTab('Analytics','analytics');hideDropDown(3);">Manage Query</a></li>
				<li onmouseout="showDropDown(3);"><a
					href="javascript:Navbar.changeTab('QueryDesigner','analytics','QueryDesigner');hideDropDown(3);">Design Report</a></li>
				<li onmouseout="showDropDown(3);"><a
					href="javascript:Navbar.changeTab('QueryViewer','analytics','QueryViewer');hideDropDown(3);">View Report</a></li>
 				<li onmouseout="showDropDown(3);"><a
					href="javascript:Navbar.changeTab('QuerySpreadSheet','analytics','QuerySpreadSheet');hideDropDown(3);">SpreadSheet</a></li>
 				<!-- <li onmouseout="showDropDown(3);"><a  href="javascript:Navbar.changeTab('QuerySpreadSheetSlick','analytics','QuerySpreadSheetSlick');hideDropDown(3);">Slick SpreadSheet Viewer</a></li>-->
			</ul></li>

		<li onmouseout="showDropDown(5);" id="Jobs_li" style="border-right: 0px; width: 33%;" class="user end">
			<a
			href="javascript:Navbar.changeTab('jobs','jobs', 'jobs_overview');hideDropDown(5);">Jobs</a>
			<ul>
				<li onmouseout="showDropDown(5);"><a href="javascript:Navbar.changeTab('jobs','jobs','JobBrowser');hideDropDown(5);">Manage Jobs</a></li>
				<li onmouseout="showDropDown(5);"><a href="javascript:Navbar.changeTab('jobs','jobs','JobHistory');hideDropDown(5);">History</a></li>
				<li onmouseout="showDropDown(5);"><a href="javascript:Navbar.changeTab('jobs','jobs','JobStatus');hideDropDown(5);">Status</a></li>
			</ul>
		</li>

<!-- 		<li onmouseout="showDropDown(3);" id="Scheduler_li" class="user"
			style="border-right: 0px; width: 24%;"><a
			href="javascript:Navbar.changeTab('Analytics','analytics');hideDropDown(3);">Scheduler</a>
			<ul>
				<li onmouseout="showDropDown(3);"><a
					href="javascript:Navbar.changeTab('Analytics','analytics');hideDropDown(3);">Query</a></li>
				<li onmouseout="showDropDown(3);"><a
					href="javascript:Navbar.changeTab('QueryDesigner','analytics','QueryDesigner');hideDropDown(3);">Jobs</a></li>
				<li onmouseout="showDropDown(3);"><a
					href="javascript:Navbar.changeTab('QueryViewer','analytics','QueryViewer');hideDropDown(3);">Status</a></li>
			</ul></li>
 -->


		<%
			} else {
		%>

		<li onmouseout="showDropDown(5);" id="Setup_li"
			style="margin-left: 1px;" class="first admin"><a id="Dashboard"
			href="javascript:Navbar.changeTab('Setup','setup','quicksetup');hideDropDown(5)">Quick
				Setup</a>
			<ul>
				<li onmouseout="showDropDown(9);"><a id="automateCluster"
					href="javascript:Navbar.changeTab('Setup','setup','quicksetup');hideDropDown(9);">Cluster
						Setup</a></li>
				<li onmouseout="showDropDown(9);"><a id="Hosts"
					href="javascript:Navbar.changeTab('Setup','setup','hosts');hideDropDown(9);">Manage
						Hosts</a></li>
			</ul></li>

		<li onmouseout="showDropDown(7);" id="Hadoop_li" class="admin"><a id="System Config"
			href="javascript:Navbar.changeTab('Hadoop','Hadoop','HDFS');hideDropDown(7);">Manage
				HDFS</a>
			<ul>
				<li onmouseout="showDropDown(7);"><a
					href="javascript:Navbar.changeTab('Hadoop','Hadoop');hideDropDown(7);">
						HDFS Overview</a></li>
				<li onmouseout="showDropDown(7);"><a
					href="javascript:Navbar.changeTab('Hadoop','Hadoop','nn_summary');hideDropDown(7);">NameNode</a></li>
				<li onmouseout="showDropDown(7);"><a
					href="javascript:Navbar.changeTab('Hadoop','Hadoop','dn_summary');hideDropDown(7);">DataNode</a></li>
				<li onmouseout="showDropDown(7);"><a id="Check Point"
					href="javascript:Navbar.changeTab('Hadoop','Hadoop', 'JournalNode');hideDropDown(7);">Journal
						Node</a></li>
				<li onmouseout="showDropDown(7);"><a id="Check Point"
					href="javascript:Navbar.changeTab('Hadoop','Hadoop', 'CheckPointNode','CheckPointNode');hideDropDown(7);">CheckPoint
						Node</a></li>
				<li onmouseout="showDropDown(7);"><a id="System Config"
					href="javascript:Navbar.changeTab('Hadoop','Hadoop', 'system_config_HDFS');hideDropDown(7);">Configure
						HDFS</a></li>
			</ul></li>

		<li onmouseout="showDropDown(7);" id="MapReduce_li" class="admin"><a
			href="javascript:Navbar.changeTab('MapReduce','MapReduce');hideDropDown(7);">
				Manage MapReduce/Yarn</a>
			<ul>
				<li onmouseout="showDropDown(7);"><a
					href="javascript:Navbar.changeTab('MapReduce','MapReduce');hideDropDown(7);">
						MapReduce Overview</a></li>
				<li onmouseout="showDropDown(7);"><a
					href="javascript:Navbar.changeTab('MapReduce','MapReduce', 'ResourceManager');hideDropDown(7);">ResourceManager</a></li>
				<li onmouseout="showDropDown(7);"><a
					href="javascript:Navbar.changeTab('MapReduce','MapReduce', 'NodeManager');hideDropDown(7);">NodeManager</a></li>
				<li onmouseout="showDropDown(7);"><a id="System Config"
					href="javascript:Navbar.changeTab('MapReduce','MapReduce', 'system_config_MR','MapReduce');hideDropDown(7);">Configure
						MapReduce</a></li>
			</ul></li>

		<li onmouseout="showDropDown(7);" id="Monitor_li" class="admin"><a
			id="Hadoop"
			href="javascript:Navbar.changeTab('Monitor','Monitor','system_monitor');hideDropDown(7);">Monitor</a>
			<ul id="Admin">
				<li onmouseout="showDropDown(5);"><a
					href="javascript:Navbar.changeTab('Monitor','Monitor', 'system_monitor');hideDropDown(5);">System Monitor</a></li>
				<li onmouseout="showDropDown(7);"><a
					href="javascript:Navbar.changeTab('Monitor','Monitor','HDFS');hideDropDown(7);">
						HDFS Monitor</a></li>
				<li onmouseout="showDropDown(5);"><a
					href="javascript:Navbar.changeTab('Monitor','Monitor','MapReduce');hideDropDown(5);">
						MapReduce Monitor</a></li>
				<li onmouseout="showDropDown(5);"><a
					href="javascript:Navbar.changeTab('Monitor','Monitor','all_alerts');hideDropDown(5);">
						Alerts</a></li>
				<li onmouseout="showDropDown(5);"><a id="Configure Alerts"
					href="javascript:Navbar.changeTab('Monitor','Monitor','set_alerts');hideDropDown(5);">Rules
				</a></li>
				<li onmouseout="showDropDown(9);"><a
					href="javascript:Navbar.changeTab('Monitor','Monitor', 'all_reports');hideDropDown(9);">System
						Reports</a></li>
				
			</ul></li>

		<li onmouseout="showDropDown(7);" id="Admin_li" class="end admin" style="border-right: 0px; width: 19.5%;"><a
			id="Hadoop"
			href="javascript:Navbar.changeTab('Admin','Admin','queryio_services');hideDropDown(7);">Others</a>
			<ul id="Admin">
				<li onmouseout="showDropDown(9);"><a id="FTP Server"
					href="javascript:Navbar.changeTab('Admin','Admin','queryio_services');hideDropDown(9);">QueryIO
						Services</a></li>
				<li onmouseout="showDropDown(9);"><a
					href="javascript:Navbar.changeTab('Admin','Admin','manage_datasources');hideDropDown(9);">Manage
						Datasource</a></li>
				<li onmouseout="showDropDown(9);"><a id="Users"
					href="javascript:Navbar.changeTab('Admin','Admin','users');hideDropDown(9);">
						Users & Groups</a></li>
				<li onmouseout="showDropDown(5);"><a id="Users"
					href="javascript:Navbar.changeTab('Admin','Admin','notifications');hideDropDown(5);">
						Notifications</a></li>
				<li onmouseout="showDropDown(9);"><a id="Report Schedules"
					href="javascript:Navbar.changeTab('Admin','Admin', 'report_schedules');hideDropDown(9);">
						Schedules</a></li>
			</ul></li>

		<%
			}
		%>

	</ul>
	<span id="totaldn" style="display: none"><span id="hostList"
		style="display: none"><%=hostIPList.size()%></span> </span> <br
		class="clearfix" />
	<script type="text/javascript">
		Navbar.setActiveTab();
		$(function() {
			if ($.browser.msie && $.browser.version.substr(0, 1) < 7) {
				$('li').has('ul').mouseover(function() {
					$(this).children('ul').show();
				}).mouseout(function() {
					$(this).children('ul').hide();
				})
			}
		});
		var tdn = $('#totaldn').text();
		var tnn = $('#totalnn').text();
		var thost = $('#hostList').text();

		if (thost == 0) {
			isFirstTimeCluster = false;
			Navbar.changeTab('Admin', 'admin');
			Util.deleteAllCookie();
		}

		if (tdn == 1 && tnn == 1 && Navbar.selectedTabId == 'Admin'
				&& Navbar.selectedTabName == 'admin' && Navbar.isquickStartOpen) {
			Navbar.isquickStartOpen = false;
			Navbar.gettingStarted();
		}
	</script>
</div>