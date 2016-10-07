


function chartReady(flag){
	Navbar.selectedTabName = 'chartMagnified';
	if(flag){
			Navbar.selectedTabId = hostName;
			createAllChart();
	}
	else{
		Navbar.selectedTabId = nodeId;
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
	if(type=='NameNode'){
	ChartManager.getNameNodeChartData(nodeId,selectedInterval,populateServiceList);
	}
	else if(type=='DataNode'){
	ChartManager.getDataNodeChartData(hostName,selectedInterval,populateServiceList);
	}
}

function populateServiceList(list){

	
	for (var i = 0; i < list.length; i++)
	{
		chartData=list[i];
		var id=chartData.chartName;
		if(id==undefined)return;
		while(id.indexOf(" ")>0){
		id=id.replace(" ","_");
		}
		
		if(id==cName && cName=='Voltage_Status'){
			

			showBarStatusBarChart('Voltage Status',chartData,0);
		}
		else if(id==cName && cName=='Fan_Status'){

			showBarStatusBarChart('Fan Status',chartData,0);
		}
		
		if(id==cName && cName!='Voltage_Status' && cName!='Fan_Status'){
			dwr.util.setValue('title',chartData.chartName);
			var seriesVal= chartData.seriesList;
			var series = [];
			var ticks = [];
			var unit='';
			var gap = parseInt(chartData.dataPoints.length/10);
			
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
						ticks.push(parseInt(chartData.dataPoints[k]));
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
			$.plot(dwr.util.byId('graph'),series,makeOptionalField(dwr.util.byId('legend'),chartData.dataPoints.length>12?ticks:chartData.dataPoints.length,chartData.maxValue,0,seriesVal.length));
			bindTheID($('#graph'),0);
			noOfChart++;
			if(unit.indexOf('degrees C')!=-1)
				unit=' ℃';
				$('#yaxis_unit_lable_'+'graph').text(unit);
				$('#canvas_'+'graph').css('margin-left','10px');
				$('#yaxis_unit_lable_'+'graph').css('padding','180px 0px 175px 5px');
				
				
		}
	}
}

function backToChart()
{
	
	if(nodeType=='NameNode')
		Navbar.changeTab(nodeId,'nn_detail',hostName);
	else if(nodeType=='NM')
		Navbar.changeTab('Hadoop','nm_detail',Node_Manager.selectedIP,nodeId);
	else if(nodeType=='RM')
		Navbar.changeTab('Hadoop','rm_detail',Resource_Manager.selectedIP,nodeId);
	else
		Navbar.changeTab(hostName,'dn_detail');
}


function showChartTooltip(x, y, contentsx, contentsy)
{
	$('<div id="tooltip">' + contentsy + ' on ' + contentsx +'</div>').css( {
	position: 'absolute',
	'font-size': '10px',
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
                backgroundOpacity: 0
				},
			xaxis: {
					ticks: noOfTicks,
					tickLength : 0, 
					tickFormatter: function(val){
							var d=new Date(val);
							//val+=(d.getTimezoneOffset() * 60000)+serverSideTimeOffset;
							//d=new Date(val);
							var day="";
							switch(d.getDay()){
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
		        var x = new Date(val);
//		        val+=(x.getTimezoneOffset() * 60000)+serverSideTimeOffset;
//				x=new Date(val);
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
function showBarStatusBarChart(id,chartData,rowj){
	
	
	
	
	
	

	var ticks = new Array();
	var seriesList=chartData.seriesList;
	var dataPoints=chartData.dataPoints
	
	for(var i=0;i<seriesList.length;i++){
		var tickName =seriesList[i].name;
		ticks.push([i+1,tickName.substr(0,tickName.indexOf('Status'))]);
	}
	var xticks =[];
	j=1
	for(var i=0 ;i<dataPoints.length;i+=avg){
		xticks.push([j,convertTimeStampDate(dataPoints[i])]);
		j++;
	}
	
	var css_id = '#graph';

	
//	    var data = [
//	        {label: 'Warn-lo', data: [[10,1], [0,2], [10,3]]},
//	        {label: 'Crit-lo',  data: [[20,1], [20,2], [20,3]]},
//	        {label: 'BelowCrit',  data: [[25,1], [25,2], [25,3]]},
//	        {label: 'Warn-hi',  data: [[30,1], [30,2], [30,3]]},
//	        {label: 'Crit-hi',  data: [[40,1], [40,2], [40,3]]},
//	        {label: 'AboveCrit',  data: [[50,1], [50,2], [50,3]]},
//	        {label: 'Init', data: [[70,1], [70,2], [70,3]]},
//	        {label: 'OK',  data: [[300,1], [300,2], [+id300,3]]},
//	    ];
//	
	  var dataList= new Array();
	  
	  var dataWarnLo = new Array();id
	  var dataCritLo = new Array();
	  var dataBelowCrit = new Array();max
	  var dataWarnhi = new Array();
	  var dataCrithi = new Array();
	  var dataAbovecrit = new Array();
	  var dataInit = new Array();
	  var dataOk = new Array();
	  var avg=parseInt(dataPoints.length/5);
		
	
	
	
	
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
   
    
    //dwr.util.byId('chart.click').href="javascript:show('Voltage_Status')";
	dwr.util.setValue('title',id);
	 $.plot(dwr.util.byId('graph'),data,makeOptionalFieldForFanStatus(dwr.util.byId('legend'),seriesList.length,chartData.maxValue,0,seriesList.length,ticks,xticks,max));
	 if(id.indexOf('Voltage')!=-1)
	 $('#yaxis_unit_lable_'+'graph').text('Volt');
	 else
		 $('#yaxis_unit_lable_'+'graph').text('RPM');
	

		$('#canvas_'+'graph').css('margin-left','10px');
		$('#yaxis_unit_lable_'+'graph').css('padding','180px 0px 175px 5px');

	
}

