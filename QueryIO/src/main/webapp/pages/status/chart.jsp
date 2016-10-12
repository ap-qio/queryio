<%@page import="java.util.*" %>
<link rel="stylesheet" href="styles/chart.css" type="text/css" />
<script type="text/javascript" src="scripts/status/chart.js" ></script>
<script type="text/javascript" src="scripts/jquery.flot.js"  ></script>
<script type="text/javascript" src="dwr/interface/ChartManager.js" ></script>

<div>
<br>
<table id="chartTable" style="width:98.6%; margin: 0 10px;">
	<tr id="row_chart_0_">
		<td id="chart_0_" style="width: 50%; height: 100pt; display: none">
			<div id="title_" class="chart_header" style="margin-top: 15px;" ></div>
			<div id="legend_" class="legend" align="center" style="border-left: 1px solid #CCC; border-right: 1px solid #CCC;"></div>
			<div id="graph_" style="height: 100px; width: 99.5%; border-left: 1px solid #CCC; border-right: 1px solid #CCC;	border-bottom: 1px solid #CCC;"></div>	
		</td>
	</tr>
</table>


</div>

<script type="text/javascript">
<%
boolean flag = false;
for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
	String paramName = (String)e.nextElement();
	if(paramName.equals("hostName")){
			flag = true;
			break;
	}
}
%>

var hostName;
var nodeType;
var selectedInterval;
var nodeId;// = null;

<%if(flag){%>
hostName = '<%=request.getParameter("hostName")%>';
nodeType = '<%=request.getParameter("nodeType")%>';
nodeId = '<%=request.getParameter("nodeID")%>';
selectedInterval = '<%=request.getParameter("interval")%>';
chartReady(true);
<%}
else{
 %>
 nodeId = '<%=request.getParameter("nodeID")%>';
 selectedInterval = '<%=request.getParameter("interval")%>';
 nodeType = '<%=request.getParameter("nodeType")%>';
 chartReady(false);
<%}%>
</script>