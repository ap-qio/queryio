
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