DN_Host = {

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
//			dwr.util.byId('summary_chart').style.background = 'url(images/noData.png) no-repeat';
		}
		else
		{
			while(list[1].indexOf(",")>-1){
				list[1] = list[1].replace(",","");
			}
			while(list[0].indexOf(",")>-1){
				list[0] = list[0].replace(",","");
			}
			var memSize = ' GB ';
			if(list[0]>1024||list[1]>1024)
			{
				memSize = ' TB ';
				list[0] = list[0]/1024;
				list[1] = list[1]/1024;
			}
			var list1 = parseFloat(list[1]);
			var list0 = parseFloat(list[0]);
			var usedMem = list1;
			usedMem = usedMem * 100;
			usedMem = Math.round(usedMem);
			usedMem = usedMem / 100;
			
			seriesArray.push(['Used', usedMem]);
			
			var usedPer = ((list1/list0)*100);
			usedPer = usedPer * 100;
			usedPer = Math.round(usedPer);
			usedPer = usedPer / 100;
			
			labels.push('Used ' + usedMem + memSize );
			colors.push('#EAA228');
			
			var freeMem = (list0-list1);
			freeMem = freeMem * 100;
			freeMem = Math.round(freeMem);
			freeMem = freeMem / 100;
			
			seriesArray.push(['Free', freeMem]);

			var freePer = (((list0-list1)/list0)*100);
			freePer = freePer * 100;
			freePer = Math.round(freePer);
			freePer = freePer / 100;
			
			labels.push('Free ' + freeMem + memSize );
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
    		$("#status_table_div").html('<span>DataNode Details not available. </span>');
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
			$('#dn_summary_table_div').load('pages/status/chart.jsp?hostName='+selectedDataNode+'&nodeType=DataNode&interval='+interval);
		}
		else{
			$("#service_ref").load("pages/status/chartMagnified.jsp?chartData="+cName+"&ip="+hostName+"&nodeType="+type+"&interval="+interval);
		}
	},
	
	changeInterval: function (newInterval)
	{
		interval = newInterval;
		
		RemoteManager.getDataNodeSummary(selectedDataNode, interval, DN_Host.showSummary);
		DN_Host.loadResource();
	},
	
	ready: function ()
	{
		
		RemoteManager.getNode(selectedDataNode, Navbar.setDataNodePathbar);
		RemoteManager.getDataNodeMemoryInfo(selectedDataNode, DN_Host.fillPieChart);
		RemoteManager.getDataNodeSummary(selectedDataNode, interval, DN_Host.showSummary);
		DN_Host.loadResource();
	}
};