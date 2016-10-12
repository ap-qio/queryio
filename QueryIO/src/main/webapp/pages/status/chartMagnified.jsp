<%@page import="java.util.*" %>

<script language="javascript" type="text/javascript" src="scripts/status/chartMagnified.js" ></script>
<script type="text/javascript" src="scripts/jquery.flot.js"  ></script>
<script type="text/javascript" src="dwr/interface/ChartManager.js" ></script>
<link rel="stylesheet" href="styles/chart.css" type="text/css" />
<div>
<table id="chartTable" style="width:100%;" class="outtertab_table">
	<tr id="row_chart_0">
		<td id="chart_0" style="width: 49%; height: 400pt; padding: 30px 0px 0px 0px;">
			<div class="chart_header" style="width: 98.3%;"><span id="title" style="width: 90%; float: left;"></span>
			<span style="width: 10%; float: right;"><a style="color: whitesmoke; width: 70%; margin-left : 80px;" href="javascript:backToChart();">Back</a></span></div>
			<div id="legend" align="center" style="border-left: 1px solid #CCC; border-right: 1px solid #CCC;height:20pt; width:98.7% "></div>
			<div id="graph" style=" border-left: 1px solid #CCC; border-right: 1px solid #CCC;border-bottom: 1px solid #CCC; height: 100%; width:98.7% "></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	resource = 'chartMagnified.jsp';
	<%
	boolean flag = false;
	for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
		String paramName = (String)e.nextElement();
		if(paramName.equals("ip")){
				flag = true;
				break;
		}
	}
	%>

	var hostName;
	var selectedInterval;
	var nodeId;// = null;
	var cName = '<%= request.getParameter("chartData") %>';
	var hostname;
	var type;
	
	<%if(flag){%>
	
		hostName = '<%=request.getParameter("ip")%>';
		nodeId = '<%=request.getParameter("nodeId")%>';
		type = '<%=request.getParameter("nodeType")%>';
		selectedInterval = '<%=request.getParameter("interval")%>';
		chartReady(true);
	<%}
	else{
	 %>
		 nodeId = '<%=request.getParameter("nodeId")%>';
		 selectedInterval = '<%=request.getParameter("interval")%>';
		 type = '<%=request.getParameter("nodeType")%>';
		 chartReady(false);
	<%}%>
	
	
	
	
</script>

</div>