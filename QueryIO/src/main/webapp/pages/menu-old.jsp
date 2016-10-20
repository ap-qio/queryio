 <%@page import="com.queryio.core.bean.Node"%>
<%@page import="com.queryio.core.bean.Host"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.queryio.core.conf.RemoteManager"%>
<script type="text/javascript" src="scripts/jquery.dropotron-1.0.js"></script>
<link rel="stylesheet" href="styles/test.css" type="text/css" />

 <script type="text/javascript">
 $(function() {
		$('#menu > ul').dropotron({
			mode: 'slide',
			globalOffsetY: 11,
			offsetY: -15
		});
		$("ul.subnav").parent().append("<span></span>"); //Only shows drop down trigger when js is enabled (Adds empty span tag after ul.subnav*)
		
		$("ul.topnav li span").click(function() { //When trigger is clicked...
			
			//Following events are applied to the subnav itself (moving subnav up and down)
			$(this).parent().find("ul.subnav").slideDown('fast').show(); //Drop down the subnav on click

			$(this).parent().hover(function() {
			}, function(){	
				$(this).parent().find("ul.subnav").slideUp('slow'); //When the mouse hovers out of the subnav, move it back up
			});

			//Following events are applied to the trigger (Hover events for the trigger)
			}).hover(function() { 
				$(this).addClass("subhover"); //On hover over, add class "subhover"
			}, function(){	//On Hover Out
				$(this).removeClass("subhover"); //On hover out, remove class "subhover"
		});

	});

</script>




 <div id="menu">
		<ul id="menu_ul">
			<!-- DashBoard -->
			<li class="menuactive first " id="Dashboard_li" style="margin-left: 1px;">
				<span class="opener"><a id="Dashboard" href="javascript:Navbar.changeTab('Dashboard','dashboard');" >Dashboard</a></span>
			</li>
			
			<!-- Status -->
			<li id="Status_li">
				<span class="opener"><a id="Status"  href="javascript:Navbar.changeTab('Status','status');" >Status</a></span>
			</li>
			
			
			<!-- DataBrowser -->
			<li id="DataBrowser_li" >
				<span class="opener"><a id="DataBrowser"  href="javascript:Navbar.changeTab('DataBrowser','databrowser');">Data Browser</a></span>
			</li>
			
			
			<!-- NameNode -->
			<li id="TreeNameNode_li">
				<span class="opener"><a id="TreeNameNode" href="javascript:Navbar.changeTab('TreeNameNode','nn_summary');">NameNode</a></span>
				<ul id="nntree">
				<%
				int totalNn=0;
				ArrayList nameNodes = RemoteManager.getNameNodes();
				for(int i=0;i<nameNodes.size();i++,totalNn++){
					
					
					String nameNodeId=null;
					nameNodeId=((Node)nameNodes.get(i)).getId();
					%>
					<li><a id="nn'<%=nameNodeId %>" href="javascript:Navbar.changeTab('<%=nameNodeId %>','nn_detail');"  ><%=nameNodeId %></a></li>
					
					<%
					
				}
				
				%>
			
				</ul>
			</li>
			
			
			<!-- DataNode -->
			
			<li id="TreeDataNode_li">
			
				<span class="opener"><a id="TreeDataNode"  href="javascript:Navbar.changeTab('TreeDataNode','dn_summary');" >DataNode</a></span>
				<ul id="dntree">
				<%
					HashMap map = RemoteManager.getRackTree();
						
					int totalDn = 0;
					Set<String> keys= map.keySet();	
					String nodeId=null;
					for(String attr:keys){
						%>
						<li style="width: 90%;">
						<span ><a id="rack<%=attr %>" href="javascript:Navbar.changeTab('<%=attr %>','rack_summary');"  ><%=attr %></a>
						
						<%
							HashMap hostMap = (HashMap) map.get(attr);
							
							
							Set<String> hostSet = hostMap.keySet();
							if(hostSet.size()>0){
							%><b></b></span><ul><%	
							for(String hostName:hostSet){
								
						%>
							<li style="width: 90%;">
							<span ><a id="host<%=hostName %>" href="javascript:Navbar.changeTab('<%=hostName %>','host_summary'); Navbar.setHostPathbar(['<%=attr %>','<%=hostName %>']);"  ><%=hostName %></a>
								
						
					
								<%
									ArrayList hostList = (ArrayList)hostMap.get(hostName);
									if(hostList.size()>0){
										%><b></b></span><ul><%
									for(int i=0;i<hostList.size();i++,totalDn++){
										
										Node node=(Node)hostList.get(i);
										nodeId = node.getId();
										%>
						
										<li style="width: 90%;">
										<a id ="dn<%=nodeId %>" href="javascript:Navbar.changeTab('<%=nodeId %>','dn_detail'); Navbar.setDataNodePathbar(['<%=attr %>','<%=hostName %>','<%=nodeId %>']);" ><%=nodeId %></a>
							
										</li>
						<%
							}
							%></ul><%
						}else{
							%></span><%
						}
						%>
							
							</li>
						<%
							}
							%></ul><%
						}else{
							%></span><%		
						}
						%>		
						
						</li>
				<%
					}
				%>	
				
				</ul>
			</li>
			
			
			<!-- Reports -->
			
			<li id="Reports_li">
			
				<span class="opener"><a id="All Reports" href="javascript:Navbar.changeTab('All Reports','reports', 'all_reports');" >Reports</a></span>
				<ul id="Reports">
				<!-- 	<li><a id="All Reports" href="javascript:Navbar.changeTab('All Reports','reports', 'all_reports');" >All Reports</a></li>-->
					<li><a id="Report Schedules" href="javascript:Navbar.changeTab('Report Schedules','reports', 'report_schedules');" >Report Schedules</a></li>
				</ul>
			</li>
			
			
			
			<!-- Alerts -->
			<li id="Alerts_li">
			
				<span class="opener"><a id="All Alerts" href="javascript:Navbar.changeTab('All Alerts','alerts', 'all_alerts');" >Alerts</a></span>
				<ul id="Alerts">
					<!-- <li><a id="All Alerts" href="javascript:Navbar.changeTab('All Alerts','alerts', 'all_alerts');" >All Alerts</a></li>-->
					<li><a id="Configure Alerts" href="javascript:Navbar.changeTab('Configure Alerts','alerts', 'set_alerts');"  >Configure Alerts</a></li>
				</ul>
			</li>
			
			
			<!-- Admin -->
			<li class="end" style="border-right: none" id="Users_li">
			
				<span class="opener"><a id="Users" href="javascript:Navbar.changeTab('Users','admin','users');"  >Admin</a></span>
				<ul id="Admin">
					<li><a id="Data Migration" href="javascript:Navbar.changeTab('Data Migration','admin', 'data_migration');" >Data Migration</a></li>
					<!-- <li><a id="FTP Server" href="javascript:Navbar.changeTab('FTP Server','admin', 'ftp_server');"  >FTP Server</a></li>-->
					<li><a id="FTP Server" href="javascript:Navbar.changeTab('QueryIO Services','admin', 'queryio_services');"  >QueryIO Services</a></li>
					<li><a id="Hosts" href="javascript:Navbar.changeTab('Hosts','admin', 'hosts');" >Hosts</a></li>
					<li><a id="System Config" href="javascript:Navbar.changeTab('System Config','admin', 'system_config');"  >System Config</a></li>
					<li><a id="Users" href="javascript:Navbar.changeTab('Users','admin','users');"  >Groups & Users</a></li>
					<li><a id="Notifications" href="javascript:Navbar.changeTab('Notifications','admin', 'notifications');"  >Notifications</a></li>
					<li><a id="Billing" href="javascript:Navbar.changeTab('Billing','admin', 'billing');"  >Billing</a></li>
				</ul>
			</li>
			
			
			<!-- end of menu -->
			
		</ul>
		<span id="totaldn" style="display: none"><%=totalDn+1 %></span><span id="totalnn" style="display: none"><%=totalNn+1 %></span>
		<br class="clearfix" />
		<script type="text/javascript">
		Navbar.setActiveTab();
		</script>
	</div>
	
	
	
	