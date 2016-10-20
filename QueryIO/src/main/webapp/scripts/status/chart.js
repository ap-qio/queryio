var row=1;
var noOfChart=0;

var seriesColor = ['#443266','#CC9752','#996699','#FFCC00','#666633','#217C7E'];


function chartReady(flag){
	if(flag){
		createAllChart();
	}
	else{
		if(nodeType=='RM'){
			ChartManager.getResourceManagerChartData(nodeId,selectedInterval,populateServiceList);
		}
		else{
			ChartManager.getNodeManagerChartData(nodeId,selectedInterval,populateServiceList);
		}
	}
}


function createAllChart()
{
	if(nodeType=='NameNode')
	{
		ChartManager.getNameNodeChartData(nodeId, interval, populateServiceList);
	}
	else if(nodeType=='DataNode')
	{
		ChartManager.getDataNodeChartData(hostName, interval, populateServiceList);
	}
	
}

function populateServiceList(list)
{
	if(document.getElementById('chartTable')==undefined||document.getElementById('chartTable')==null)return;
	serviceCache = new Array();
	var host;
	var id;
	if(list==null)
	{
		$('#chart_0_').innerText="There is no chart to show";
	}
	else
	{
		for (var i = 0; i < list.length; i++)
		{
			chartData = list[i];

			id = chartData.chartName;
		
			if(id=='Voltage Status'||(id=='Fan' && nodeType!='DataNode')||id=='Voltage'||id=='Fan Status'){
				$('#chart_'+row+'_').remove();
				row++;
				var height='100px';
				if(id=='Voltage Status')
					height='400px'
				$('#chartTable').append('<tr><td colspan="2" id="chart_'+row+'_'+id+'" style="width: 98%; height: '+height+'">\
						<div id="title_'+id+'" class="chart_header" style="margin-top: 15px;" ></div>\
						<div id="legend_'+id+'" class="legend" align="center" style="border-left: 1px solid #CCC; border-right: 1px solid #CCC;width: 98.7%; "></div>\
						<a id="chart.click_'+id+'" href=""><div id="graph_'+id+'" style="height: '+height+'; width: 98.7%; border-left: 1px solid #CCC; border-right: 1px solid #CCC; border-bottom: 1px solid #CCC;">\
						</div></a>	\
						</td></tr>');
				
				if(id=='Fan Status'){
					generateFanStatusBarChart(id,chartData,row);
					continue;
				}else if(id=='Voltage Status'){
					generateVoltageStatusBarChart(id,chartData,row);
					continue;
				}
				
			}else{

				var unit ='';	
				if(noOfChart%2==0)
				{
				$('#chart_'+row+'_').remove();
				row++;
				
				$('#chartTable').append('<tr><td id="chart_'+row+'_" style="width: 49%; height: 100pt;">\
						<div id="title_" class="chart_header" style="margin-top: 15px;" ></div>\
						<div id="legend_" class="legend" align="center" style="border-left: 1px solid #CCC; border-right: 1px solid #CCC;width: 99%;"></div>\
						<a id="chart.click_" href=""><div id="graph_" style="height: 100px; width: 99%; border-left: 1px solid #CCC; border-right: 1px solid #CCC; border-bottom: 1px solid #CCC;">\
						</div></a></td></tr>');
				}
			
				if(id==undefined)return;
				while(id.indexOf(" ")>0)
				{
					id=id.replace(" ","_");
				}

					dwr.util.cloneNode('chart_'+row+'_',{ idSuffix:id });
			
					if(noOfChart%2==1)
					{
						$('#chart_'+row+'_'+id).css({"height":"120px","padding":"0px 0px 0px 0px"});
					}
					else
					{
						$('#chart_'+row+'_'+id).css({"height":"120px","padding":"0px 0px 0px 0px"});
					}
			
			}
		

			
			
			
			dwr.util.byId('chart.click_'+id).href="javascript:show('"+id+"')";
			dwr.util.setValue('title_' + id, chartData.chartName);
			
			var seriesVal= chartData.seriesList;
			var series = [];
			var ticks = [];
			
			var gap = parseInt(chartData.dataPoints.length/5);
			
			for(var j=0;j<seriesVal.length;j++)
			{	
				var ySeries=seriesVal[j];
				var seriesData = [];
				
				for(var k=0;k<chartData.dataPoints.length;k++)
				{
					if(ySeries.values[k]==null)
						seriesData.push([chartData.dataPoints[k],0]);
					else
						seriesData.push([parseInt(chartData.dataPoints[k]),parseInt(ySeries.values[k])]);				
					if(k%gap==0&&j==0)
					{
						ticks.push([parseInt(chartData.dataPoints[k])]);
					}
				}

				unit=ySeries.name.substring(ySeries.name.indexOf('(')+1,ySeries.name.indexOf(')'));	

				if(ySeries.name.indexOf('(')>1){
					ySeries.name=ySeries.name.substring(0,ySeries.name.indexOf('('));
					if(id.indexOf('Temp')!=-1){
						ySeries.name=ySeries.name.substring(0,ySeries.name.indexOf('Temp'));
					}
				}
				if(ySeries.type==0){
					series.push(makeSeriesAreaChart(ySeries.name,seriesData,seriesColor[j]));
				}
				else{
					series.push(makeSeriesLineChart(ySeries.name,seriesData,seriesColor[j]));
				}
			}
			$.plot(dwr.util.byId('graph_'+id),series,makeOptionalField(dwr.util.byId('legend_'+id),chartData.dataPoints.length>6?ticks:chartData.dataPoints.length,chartData.maxValue,0,seriesVal.length));
			bindTheID($('#graph_'+id),0);
			noOfChart++;
			
			if(unit.indexOf('degrees C')!=-1)
			unit=' ℃';
			$('#yaxis_unit_lable_'+'graph_'+id).text(unit);
			$('#canvas_'+'graph_'+id).css('margin-left','10px');
		}
	
		if($('#chart_'+row+'_'))25
		{	
			$('#chart_'+row+'_').remove();
		}
		$('#row_chart_0_').remove();
	}
}

function show(chartName)
{
	var interval=Util.getCookie("TimeInterval");
	if(interval!=undefined){
		selectedInterval = interval;
		$('#chartInterval').val(interval);
	}
	if(nodeType=='NameNode'){
		$('#NameNode'+hostName).html('<a id="NameNode'+hostName+'" href="javascript:Navbar.changeTab(\''+hostName+'\',\'nn_detail\');"  > '+hostName+'</a>');	
	}else{
		$('#DataNode'+hostName).html('<a id="DataNode'+hostName+'" href="javascript:Navbar.changeTab(\''+hostName+'\',\'dn_detail\');"  > '+hostName+'</a>');
	}
	var pathHeader = $("#pathbar_div").html();//document.getElementById("pathbar_div").innerHTML;
	
	var newPath = pathHeader;
	var newIndex = 0;
	while(newPath.indexOf("<img") != -1)
	{
		var index = newPath.indexOf("<img");
		newIndex += index + 4;
		
		var temp = newPath.substring(index + 4);
		newPath = temp;
	}
	newIndex -= 4;
	pathHeader = pathHeader.substring(0, newIndex);

	if(nodeType=="DataNode"){
		pathHeader += '<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\''+Navbar.selectedTabId+'\',\'dn_detail\');" class="tab_banner">'+Navbar.selectedTabId+'</a></span>';
	}
	else if(nodeType=="NameNode"){
		pathHeader += '<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\''+nodeId+'\',\'nn_detail\',\''+hostName+'\');" class="tab_banner">'+nodeId+'</a></span>';
	}
	else if(nodeType=="RM"){
		pathHeader += '<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'rm_detail\',\''+Navbar.selectedChildTab+'\',\''+Navbar.selectedGrandChildTab+'\');" class="tab_banner">'+nodeId+'</a></span>';
	}
	else {
		pathHeader += '<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'nm_detail\',\''+Navbar.selectedChildTab+'\',\''+Navbar.selectedGrandChildTab+'\');" class="tab_banner">'+nodeId+'</a></span>';
	}
	pathHeader += '<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span> '+chartName+'</span>';
	$("#pathbar_div").html(pathHeader);
	if(nodeType == "NameNode" || nodeType == "DataNode"){
		$("#service_ref").load("pages/status/chartMagnified.jsp?chartData="+chartName+"&nodeId="+nodeId+"&ip="+hostName+"&nodeType="+nodeType+"&interval="+selectedInterval);
	}
	else{
		$("#service_ref").load("pages/status/chartMagnified.jsp?chartData="+chartName+"&nodeId="+nodeId+"&nodeType="+nodeType+"&interval="+selectedInterval);
	}
}

function showChartTooltip(x, y, contentsx, contentsy)
{
	$('<div id="tooltip">' + contentsy + ' on ' + contentsx +'</div>').css( {
	position: 'absolute',
	'font-size': '8px',
	display: 'none',
	top: y + 5,
	left: x + 12,
	'background-color': '#fffaff',
    opacity: 0.80
	}).appendTo("body").fadeIn(200);
}

function makeSeriesAreaChart(seriesTitle,seriesData,seriesColor){
	var series={
			label:seriesTitle,
			data:seriesData,
			color: seriesColor,
			lines: { show: true, lineWidth: '1', fill: true}											
			};	
	return series;	
}

function makeSeriesLineChart(seriesTitle,seriesData,seriesColor){
	var series={
			label:seriesTitle,
			data:seriesData,
			color: seriesColor,
			lines: { show: true, lineWidth: '1', fill: false}											
			};	
	return series;	
}



var lastYear=0;	
function makeOptionalField(objContainer,noOfTicks,maxVal,serverSideTimeOffset,xserLength)
{
	var lastYear=0;
	var options = {
			legend: {noColumns:14,
				container: objContainer, 
				labelBoxBorderColor: '#fffaff',
                backgroundOpacity: 0,
				},
			xaxis: {
					ticks: noOfTicks,
					tickLength : 0, 
					tickFormatter: function(val){
							var d=new Date(val);
							var day="";
							switch(d.getDay())
							{
									case 0:day = 'Sun';break;
									case 1:day = 'Mon';break;
									case 2:day = 'Tue';break;
									case 3:day = 'Wed';break;
									case 4:day = 'Thu';break;
									case 5:day = 'Fri';break;
									case 6:day = 'Sat';break;
							}
							var month = '';
							switch(d.getMonth())
							{
								case 0: month = 'Jan';break;
								case 1: month = 'Feb';break;
								case 2: month = 'Mar';break;
								case 3: month = 'Apr';break;
								case 4: month = 'May';break;
								case 5: month = 'June';break;
								case 6: month = 'July';break;
								case 7: month = 'Aug';break;
								case 8: month = 'Sept';break;
								case 9: month = 'Oct';break;
								case 10: month = 'Nov';break;
								case 11: month = 'Dec';break;
							}
							var date = '';
							if(d.getDate()<10)
								date = "0" + d.getDate();
							else
								date = d.getDate();

							if ((selectedInterval == 'onehour') || (selectedInterval == 'oneday'))
							{
								return (d.getHours()+":"+(d.getMinutes()<10?'0':'')+d.getMinutes());
							} 
							else
							{
								year = d.getFullYear();
								if (selectedInterval == 'oneweek')
								{
									if(lastYear != year)
									{
										lastYear = year;
										return (day + ", " + month + "-" + date + " " + year);
									}
									return (day + ", " + month + "-" + date);
								}
								else
								{
									if(lastYear != year)
									{
										lastYear = year;
										return (month + "-" + date + ", " + year);
									}
									return (month + "-" + date);
								}	
							}
						},
						font: {
				    		 size: 10,
				    		 style: "normal",
				    		 family: "Tahoma",  
				   			},	
					},
			
			grid: {show:true, borderWidth: 1, borderColor: "#D0D0D0", color: "black", hoverable: true},
			yaxis:{
						font: {
					     size: 10,
					     style: "normal",
					     family: "Tahoma",  
						} ,
						min:0,
						max:maxVal,
						tickLength : 0, 
						color: "black" 
			},
						
	    };
		return options;
}

function bindTheID(container,serverSideTimeOffset)
{
		container.bind('plothover', function (event, pos, item)    {
		
		    $("#x").text(pos.x.toFixed(2));
		    $("#y").text(pos.y.toFixed(2));
		
		    if (item)
		    {
		        $("#tooltip").remove();
		        var val = item.datapoint[0];
//		        var x = new Date(val);
//		        val+=(x.getTimezoneOffset() * 60000)+serverSideTimeOffset;
				x=new Date(val);
		        var y = item.datapoint[1];
		        var day='';
		        switch(x.getDay()){
					case 0:day = 'Sun';break;
					case 1:day = 'Mon';break;
					case 2:day = 'Tue';break;
					case 3:day = 'Wed';break;
					case 4:day = 'Thu';break;
					case 5:day = 'Fri';break;
					case 6:day = 'Sat';break;
				}
		        var month = '';
				switch(x.getMonth())
				{
					case 0: month = 'Jan';break;
					case 1: month = 'Feb';break;
					case 2: month = 'Mar';break;
					case 3: month = 'Apr';break;
					case 4: month = 'May';break;
					case 5: month = 'June';break;
					case 6: month = 'July';break;
					case 7: month = 'Aug';break;
					case 8: month = 'Sept';break;
					case 9: month = 'Oct';break;
					case 10: month = 'Nov';break;
					case 11: month = 'Dec';break;
				}
				if (y<0)
					return "";
				else
				showChartTooltip(item.pageX, item.pageY,(x.getDate()+","+month+"'"+x.getFullYear()+" "+x.getHours()+":"+(x.getMinutes()<10?'0':'')+x.getMinutes()), y);
		    }
		    else
		    {
		        $("#tooltip").remove();
		    }
		});
}

function generateFanStatusBarChart(id, chartData,row){
	
	var ticks = new Array();
	var seriesList=chartData.seriesList;
	var dataPoints=chartData.dataPoints
	
	for(var i=0;i<seriesList.length;i++){
		var tickName =seriesList[i].name;
		ticks.push([i+1,tickName.substr(0,tickName.indexOf('Status'))]);
	}
	
	
	var css_id = '#graph_'+id;
	var avg=parseInt(dataPoints.length/5);
	
	

	  var dataList= new Array();
	  
	  var dataWarnLo = new Array();
	  var dataCritLo = new Array();
	  var dataBelowCrit = new Array();max
	  var dataWarnhi = new Array();
	  var dataCrithi = new Array();
	  var dataAbovecrit = new Array();
	  var dataInit = new Array();
	  var dataOk = new Array();

	
	var max = dataPoints[0];
	
	
	var k=1;
	
	var xticks =[];
	
	
	  for(var i=0;i<dataPoints.length;i+=avg){
		  for(var j=0;j<seriesList.length;j++){
			  k=dataPoints[i];
			  if(seriesList[j].values[i]==6){
				  dataWarnLo.push([k,j+1]);
			  }else if(seriesList[j].values[i]==4){
				  dataCritLo.push([k,j+1]);
			  }else if(seriesList[j].values[i]==3){
				  dataBelowCrit.push([k,j+1]);
			  }else if(seriesList[j].values[i]==5){
				  dataWarnhi.push([k,j+1]);
			  }else if(seriesList[j].values[i]==2){
				  dataCrithi.push([k,j+1]);
			  }else if(seriesList[j].values[i]==1){
				  dataAbovecrit.push([k,j+1]);
			  }else if(seriesList[j].values[i]==-1){
				  dataInit.push([k,j+1]);
			  }else if(seriesList[j].values[i]==7){
				  dataOk.push([k,j+1]);
			  }
		  }
		  xticks.push([dataPoints[i],convertTimeStampDate(dataPoints[i])]);
		  k++;
	  }
	  
	  var data = [
	  	        {label: 'Warn-lo', data: dataWarnLo},
	  	        {label: 'Crit-lo',  data:dataCritLo},
	  	        {label: 'BelowCrit',  data: dataBelowCrit},
	  	        {label: 'Warn-hi',  data:dataWarnhi},
	  	        {label: 'Crit-hi',  data: dataCrithi},
	  	        {label: 'AboveCrit',  data: dataAbovecrit},
	  	        {label: 'Init', data:dataInit},
	  	        {label: 'OK',  data: dataOk},
	  	    ];
    
    dwr.util.byId('chart.click_'+id).href="javascript:show('Fan_Status')";
	dwr.util.setValue('title_' + id, 'Fan Status');
	 $.plot(dwr.util.byId('graph_'+id),data,makeOptionalFieldForFanStatus(dwr.util.byId('legend_'+id),seriesList.length,chartData.maxValue,0,seriesList.length,ticks,xticks,max));

	 
	 
	 bindTheID($('#graph_'+id),0);
		noOfChart++;
	

	if($('#chart_'+row+'_'))
	{	
		$('#chart_'+row+'_').remove();
	}
	$('#row_chart_0_').remove();

	$('#yaxis_unit_lable_'+'graph_'+id).text('RPM');
}


function generateVoltageStatusBarChart(id,chartData,rowj){
	

	var ticks = new Array();
	var seriesList=chartData.seriesList;
	var dataPoints=chartData.dataPoints
	
	for(var i=0;i<seriesList.length;i++){
		var tickName =seriesList[i].name;
		ticks.push([i+1,tickName.substr(0,tickName.indexOf('Status'))]);
	}
	
	
	var css_id = '#graph_'+id;
	var avg=parseInt(dataPoints.length/5);
	
	  var dataList= new Array();
	  
	  var dataWarnLo = new Array();id
	  var dataCritLo = new Array();
	  var dataBelowCrit = new Array();max
	  var dataWarnhi = new Array();
	  var dataCrithi = new Array();
	  var dataAbovecrit = new Array();
	  var dataInit = new Array();
	  var dataOk = new Array();
	
	
	
	var max =convertTimeStampDate(dataPoints[0]);
	
	
	
	var k=1;
	
	var xticks =[];


	  for(var i=0;i<dataPoints.length;i+=avg){
		  k=dataPoints[i];
		  for(var j=0;j<seriesList.length;j++){
			  if(seriesList[j].values[i]=='Warn-lo'){
				  dataWarnLo.push([k,j+1]);
			  }else if(seriesList[j].values[i]=='Crit-lo'){
				  dataCritLo.push([k,j+1]);
			  }else if(seriesList[j].values[i]=='BelowCrit'){
				  dataBelowCrit.push([k,j+1]);
			  }else if(seriesList[j].values[i]=='Warn-hi'){
				  dataWarnhi.push([k,j+1]);
			  }else if(seriesList[j].values[i]=='Crit-hi'){
				  dataCrithi.push([k,j+1]);
			  }else if(seriesList[j].values[i]=='AboveCrit'){
				  dataAbovecrit.push([k,j+1]);
			  }else if(seriesList[j].values[i]=='Init'){
				  dataInit.push([k,j+1]);
			  }else if(seriesList[j].values[i]=='OK'){
				  dataOk.push([k,j+1]);
			  }
		  }
		 

		  
		  xticks.push([dataPoints[i],convertTimeStampDate(dataPoints[i])]);
		  k++;
	  }
	  var data = [
	  	        {label: 'Warn-lo', data: dataWarnLo},
	  	        {label: 'Crit-lo',  data:dataCritLo},
	  	        {label: 'BelowCrit',  data: dataBelowCrit},
	  	        {label: 'Warn-hi',  data:dataWarnhi},
	  	        {label: 'Crit-hi',  data: dataCrithi},
	  	        {label: 'AboveCrit',  data: dataAbovecrit},
	  	        {label: 'Init', data:dataInit},
	  	        {label: 'OK',  data: dataOk},
	  	    ];
    dwr.util.byId('chart.click_'+id).href="javascript:show('Voltage_Status')";
	dwr.util.setValue('title_' + id,'Voltage Status');
	 $.plot(dwr.util.byId('graph_'+id),data,makeOptionalFieldForFanStatus(dwr.util.byId('legend_'+id),seriesList.length,chartData.maxValue,0,seriesList.length,ticks,xticks,max));

	 $('#yaxis_unit_lable_'+'graph_'+id).text('Volt');
}


function makeOptionalFieldForFanStatus(objContainer,noOfTicks,maxVal,serverSideTimeOffset,xserLength,yticks,xticks,max)
{
	var lastYear=0;
	var options = {
			legend: {noColumns:14,
				container: objContainer, 
				labelBoxBorderColor: '#fffaff',
                backgroundOpacity: 0,
				},
				xaxis: {
					ticks: noOfTicks,
					tickLength : 0, 
					tickFormatter: function(val){
							var d=new Date(val);
							var day="";
							switch(d.getDay())
							{
									case 0:day = 'Sun';break;
									case 1:day = 'Mon';break;
									case 2:day = 'Tue';break;
									case 3:day = 'Wed';break;
									case 4:day = 'Thu';break;
									case 5:day = 'Fri';break;
									case 6:day = 'Sat';break;
							}
							var month = '';
							switch(d.getMonth())
							{
								case 0: month = 'Jan';break;
								case 1: month = 'Feb';break;
								case 2: month = 'Mar';break;
								case 3: month = 'Apr';break;
								case 4: month = 'May';break;
								case 5: month = 'June';break;
								case 6: month = 'July';break;
								case 7: month = 'Aug';break;
								case 8: month = 'Sept';break;
								case 9: month = 'Oct';break;
								case 10: month = 'Nov';break;
								case 11: month = 'Dec';break;
							}
							var date = '';
							if(d.getDate()<10)
								date = "0" + d.getDate();
							else
								date = d.getDate();

							if ((selectedInterval == 'onehour') || (selectedInterval == 'oneday'))
							{
								return (d.getHours()+":"+(d.getMinutes()<10?'0':'')+d.getMinutes());
							} 
							else
							{
								year = d.getFullYear();
								if (selectedInterval == 'oneweek')
								{
									if(lastYear != year)
									{
										lastYear = year;
										return (day + ", " + month + "-" + date + " " + year);
									}
									return (day + ", " + month + "-" + date);
								}
								else
								{
									if(lastYear != year)
									{
										lastYear = year;
										return (month + "-" + date + ", " + year);
									}
									return (month + "-" + date);
								}	
							}
						},
						font: {
				    		 size: 9,
				    		 style: "normal",
				    		 family: "Tahoma",  
				   			},	
					},
				
				 series: {stack: 0,
	                 lines: {show: false, steps: false },
	                 bars: {show: true, align: 'center', barWidth: 0.3, candle: false, horizontal: true},
	                 stack:true},
	             
					 yaxis: {ticks: yticks},
						
	    };
		return options;
}
function convertTimeStampDate(val){
	var d=new Date(val);
	var day="";
	switch(d.getDay())
	{
			case 0:day = 'Sun';break;
			case 1:day = 'Mon';break;
			case 2:day = 'Tue';break;
			case 3:day = 'Wed';break;
			case 4:day = 'Thu';break;
			case 5:day = 'Fri';break;
			case 6:day = 'Sat';break;
	}
	var month = '';
	switch(d.getMonth())
	{
		case 0: month = 'Jan';break;
		case 1: month = 'Feb';break;
		case 2: month = 'Mar';break;
		case 3: month = 'Apr';break;
		case 4: month = 'May';break;
		case 5: month = 'June';break;
		case 6: month = 'July';break;
		case 7: month = 'Aug';break;
		case 8: month = 'Sept';break;
		case 9: month = 'Oct';break;
		case 10: month = 'Nov';break;
		case 11: month = 'Dec';break;
	}
	var date = '';
					
	if(d.getDate()<10)
		date = "0" + d.getDate();
	else
		date = d.getDate();

	if ((selectedInterval == 'onehour') || (selectedInterval == 'oneday'))
	{
		return (d.getHours()+":"+(d.getMinutes()<10?'0':'')+d.getMinutes());
	} 
	else
	{
		year = d.getFullYear();
		if (selectedInterval == 'oneweek')
		{
			if(lastYear != year)
			{
				lastYear = year;
				return (day + ", " + month + "-" + date + " " + year);
			}
			return (day + ", " + month + "-" + date);
		}
		else
		{
			if(lastYear != year)
			{
				lastYear = year;
				return (month + "-" + date + ", " + year);
			}
			return (month + "-" + date);
		}	
	}
}