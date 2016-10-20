NN_Detail = {

	fillPieChart: function (list)
	{
		if (list == null || list == undefined)
		{
			$('#summary_chart').append('<div style="margin: 38px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
			return;
		}
		
		
		var seriesArray = [];
		var labels = [];
		var colors = [];
		
		if ((list[0] == 0) && (list[1] == 0))
		{
			seriesArray.push(['No Data', 100]);
			labels.push('No Data');
			colors.push('#C5B47F');
			
			$('#summary_chart').append('<div style="margin: 38px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
//				dwr.util.byId('summary_chart').style.background = 'url(images/noData.png) no-repeat';
		}
		else
		{
			seriesArray.push(['File Reads', list[0]]);
			labels.push('File Reads '+ list[0]);
			colors.push('#EAA228');
			seriesArray.push(['File Writes', list[1]]);
			labels.push('File Writes '+list[1]);
			colors.push('#579575');
			$.jqplot("summary_chart", [seriesArray], {
				title: {show: false},
				grid:{shadow: false, borderWidth:0.0, background: '#fff'},
				seriesColors: colors,
				seriesDefaults:{renderer:$.jqplot.PieRenderer, rendererOptions:{sliceMargin:2, startAngle: 45, diameter:185, showDataLabels: true}},
				legend:{show:true, location: 'e', labels: labels}
			});
			$('#legend-table'+table_count).css('bottom','0px');
			$('#legend-table'+table_count).css('width','50%');
			$('#legend-table'+table_count).css('margin-right','50px');
		}
		
	},
	
	showSummary: function (list)
	{
	
		if (list == null || list == undefined)
		{
    		$("#status_table_div").html('<span>NameNode Details not available. </span>');
			return;
		}
		
		$("#status_table_div").html('<div id="status_title" class="header" style="width: 99%">Status Summary</div>');
		
		for(var i=0;i<list.length;i++)
		{
			var param=list[i];
			if(i%2==0)
			{
				$('#status_table_div').append('<div class="row even">'+param.name+':<span> '+param.value+'</span></div>');
			}
			else
			{
				$('#status_table_div').append('<div class="row odd">'+param.name+':<span> '+param.value+'</span></div>');
			}
		}
	},
	
	loadResource: function()
	{
		var selectedInterval=Util.getCookie("TimeInterval");
		if(selectedInterval!=undefined){
			interval = selectedInterval;
			$('#chartInterval').val(selectedInterval);
		}
		if(resource=="chart.jsp"){
			$('#nn_summary_table_div').load('pages/status/chart.jsp?hostName='+Navbar.selectedChildTab+'&nodeType=NameNode&interval='+interval+'&nodeID='+selectedNameNode);
		}
		else{
			$("#service_ref").load("pages/status/chartMagnified.jsp?chartData="+cName+"&ip="+hostName+"&nodeType="+type+"&interval="+interval+"&nodeId="+selectedNameNode);
		}
	},
	
	
	changeInterval: function (newInterval)
	{
		interval = newInterval;
		$('#summary_chart').html('');
		RemoteManager.getNameNodeReadWritesIntervalBased(selectedNameNode, interval, NN_Detail.fillPieChart);
		RemoteManager.getNameNodeSummary(selectedNameNode, interval, NN_Detail.showSummary);
		NN_Detail.loadResource();
	},
	
	ready: function ()
	{
		RemoteManager.getNameNodeReadWritesIntervalBased(selectedNameNode, interval, NN_Detail.fillPieChart);
		RemoteManager.getNameNodeSummary(selectedNameNode, interval, NN_Detail.showSummary);
		NN_Detail.loadResource();
	}
};