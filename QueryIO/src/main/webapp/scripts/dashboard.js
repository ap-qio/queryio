Dashboard = {

		populateNodeDetailSummaryTable : function(summaryTable)
		{
			if(document.getElementById('nn_summary_table')==undefined||document.getElementById('nn_summary_table')==null)return;
			if(summaryTable == null || summaryTable == undefined)
			{
				$("#nn_summary_table").html('<tr><td style="text-align:center;"><span>Node details not available. </span></td></tr>');
				return;
				
			}
			
			var flag=true;
			var colList=[];
			var rowList='';
			var tableRow = new Array();
			if (flag)
			{
				flag = false;
				for(var i=0; i< summaryTable.colNames.length-1; i++)
				{
					if( summaryTable.colNames[i]=='Host')continue;
					if(i==0){
						colList.push({ "sTitle": summaryTable.colNames[i],"sClass": "dataTableNodeId"});
						continue;
					}
					colList.push({ "sTitle": summaryTable.colNames[i]});	
				}
			}
			rowList = summaryTable.rows;
			
			var row;
			for(var i=0; i<rowList.length; i++)
			{
				row = rowList[i];
				var rowData = new Array();
				for(var j=0;j<row.length-1;j++){
					if( summaryTable.colNames[j]=='Host')continue;
					if(j==0){
						if(row[row.length-3]=='Started'){
							rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\',\''+row[1]+'\');" ><img id ="statusStart" alt="" src="images/status_start.png" class="statusImage" >&nbsp;'+row[j]+'</a><br>&nbsp;<a href="javascript:Navbar.changeTab(\'' + row[1] + '\',\'nn_host\');">' + row[1] + '</a>');	
						}
						else if(row[row.length-3]=='Stopped'){
							rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\',\''+row[1]+'\');" ><img id ="statusStop" alt="" src="images/status_stop.png" class="statusImage" >&nbsp;'+row[j]+'</a><br> &nbsp;<a href="javascript:Navbar.changeTab(\'' + row[1] + '\',\'nn_host\');">' + row[1] + '</a>');
						}
						else{
							rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\',\''+row[1]+'\');" ><img id ="noStatus" alt="" src="images/no_status.png" class="statusImage" >&nbsp;'+row[j]+'</a><br>&nbsp;<a href="javascript:Navbar.changeTab(\'' + row[1] + '\',\'nn_host\');">' + row[1] + '</a>');
						}
						continue;
					}
					rowData.push(row[j]);
					
				}
				
				tableRow.push(rowData);	
			}

			$('#nn_summary_table').dataTable({
		        "bPaginate": false,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": true,
				"bInfo": false,
				"bAutoWidth": false,
				"bDestroy": true,
				"aaData": tableRow,
		        "aoColumns": colList
		    });
		},

		populateDataDetailSummaryTable :function(summaryTable)
		{
			if(document.getElementById('dn_summary_table')==undefined||document.getElementById('dn_summary_table')==null)return;
			if (summaryTable == null || summaryTable == undefined)
			{
				$("#dn_summary_table").html('<span>Node details not available. </span>');
				return;
			}
			
			var flag=true;
			var colList = summaryTable.colNames;
			var list = summaryTable.rows;
			
			var table_data = '<thead><tr>';
			
			for(var i=0;i<colList.length-2;i++)
			{
				if(colList[i]=='Host')continue;
				table_data+='<th>'+colList[i]+'</th>';
			}
			
			table_data+='</tr></thead>';
			var row='';
			var last=0;
			var flag = true;
			var flagNoStatus = false;
//			var diskstatusArray = new Array();
			var diskStatusMap = new Object();
			var statusValue=0;
			
			if (list.length == 0)
			{
				var newColList = [];
				for(var i=0;i<colList.length-1;i++)
				{
					if(i==0){
						newColList.push({ "sTitle": colList[i],"sClass": "dataTableNodeId"});	
						continue;
					}

					newColList.push({ "sTitle": colList[i]});	
				}
				
				$('#dn_summary_table').dataTable({
			        "bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"bDestroy": true,
					"aaData": list,
			        "aoColumns": newColList
			    });
			}
			else
			{
				
				for (var i=0;i<list.length;i++)
				{
					row=list[i];
					flag =true;
					statusValue=0;
					table_data +='<tr id = parent-'+(i+1);
					
					if(i!=0&&list[i-1][0]==row[0])
					{
						if(last==0)
						{
							last = i; 
						}
						table_data+=' class = "child-of-parent-'+last+'">';
						table_data+='<td></td>'
							flag=false;
					}
					else
					{
						last =0;
						table_data+='>';
						
						table_data+='<td>';
						table_data+='<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'dn_detail\');" >';
						if(row[row.length-4]=='Started'){
							table_data+='<img id ="statusStart" alt="" src="images/status_start.png" class="statusImage" >'+row[0]+'<br></a><a id="host'+row[1]+'" href="javascript:Navbar.changeTab(\''+row[1]+'\',\'host_summary\');"> '+row[1]+'</a></td>';	
						}
						else if(row[row.length-4]=='Stopped'){
							table_data+='<img id ="statusStop" alt="" src="images/status_stop.png" class="statusImage" >'+row[0]+'<br></a><a id="host'+row[1]+'" href="javascript:Navbar.changeTab(\''+row[1]+'\',\'host_summary\');"> '+row[1]+'</a></td>';
						}
						else{
							table_data+='<img id ="noStatus" alt="" src="images/no_status.png" class="statusImage" >'+row[0]+'<br></a><a id="host'+row[1]+'" href="javascript:Navbar.changeTab(\''+row[1]+'\',\'host_summary\');"> '+row[1]+'</a></td>';
						}
					}
					for (var j=2;j<row.length-1;j++)
					{
						if(colList[j] == 'Disk')
						{
	                        if(flag)
	                        {
	                        	table_data+='<td><img id ="nodeStatusHealthy'+row[row.length-1]+'" alt="" src="images/status_start.png" class="statusImage" style="display:none;" ><img id ="nodeStatusFailure'
                        		+ row[row.length-1]+'" alt="" src="images/status_stop.png" class="statusImage" style="display:none;"  ><img id ="nodeStatusNotDefined'
                        		+ row[row.length-1]+'" alt="" src="images/no_status.png" class="statusImage" style="display:none;" >'+row[j]+'</td>';
	                        	 if(row[row.length-2]=='Healthy')
	 	                         {
	 	    						statusValue=1;
	 	    				    }
	 	    				 	else if(row[row.length-2] == "N/A")
	 	    					{
	 	    						statusValue=2;
	 	    						
	 	    					}
	 	    				 	else if(row[row.length-2] == "")
	 	    					{
	 	    						statusValue=0;
	 	    						
	 	    					}
	 	    					else{
	 	    						statusValue=3;
	 	    					}

	                        }
	                        else
	                        {
								table_data+='<td  style="width : 12%; word-break: break-all;" >';
								if(row[row.length-2]=='Healthy'){
		    						table_data+='<img id ="nodeStatusHealthy" alt="" src="images/status_start.png" class="statusImage" >'+row[j]+'</a></td>';	
		    						statusValue=1;
		    					}else if(row[row.length-2] == "N/A"){
		    						table_data+='<img id ="nodeStatusNotDefined" alt="" src="images/no_status.png" class="statusImage" >'+row[j]+'</a></td>';
		    						statusValue=2;
		    					}else{
		    						table_data+='<img id ="nodeStatusFailure" alt="" src="images/status_stop.png" class="statusImage" >'+row[j]+'</a></td>';
		    						statusValue=3;
		    					}
	                        }
	                    }else if(colList[j]=='Rack Name'){
	                    	table_data+='<td><a id="rack'+row[j]+'" href="javascript:Navbar.changeTab(\''+row[j]+'\',\'rack_summary\');">'+row[j]+'</a></td>';
	                    }
						else if(colList[j] == 'Volume'){
	                        table_data+='<td  style="width : 12%; word-break: break-all;" >'+row[j]+'</td>';
	                    }else if(colList[j] == 'Status'){
                    		 table_data+='<td id="node.status'+row[row.length-1]+'" >'+row[j]+'</td>';
                    	}else if(colList[j] != 'Disk Health Status'){
	                        table_data+='<td>'+row[j]+'</td>';
	                    }
					}
					if(statusValue<diskStatusMap[row[row.length-1]] ){
						statusValue=diskStatusMap[row[row.length-1]];
					}
					diskStatusMap[row[row.length-1]]=statusValue;
					table_data +='</tr>';
				}
				
				
				$('#dn_summary_table').html(table_data);
				$("#dn_summary_table").treeTable(
						{
							expandable: true,
							clickableNodeNames: true
						});
				$('.expander').css('margin-left','-13px');
				for ( var nodeId in diskStatusMap) {
					if(diskStatusMap[nodeId]==1){
						$('#nodeStatusHealthy'+nodeId).css('display','');
					}else if(diskStatusMap[nodeId]==2){
						$('#nodeStatusNotDefined'+nodeId).css('display','');
					}else{
						$('#nodeStatusFailure'+nodeId).css('display','');
					}
				}
			}
			
		},
		
		fillStatusTable : function(list)
		{
			if(document.getElementById('status_table_div')==undefined||document.getElementById('status_table_div')==null)return;
			if (list == null || list == undefined)
			{
	    		$("#status_table_div").html('<span>Cluster Details not available. </span>');
				return;
			}
			
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
	    fillNameNodeStatusTable : function(list)
		{
	    	var div_data='';
	    	if(document.getElementById('nn_status_table_div')==undefined||document.getElementById('nn_status_table_div')==null)return;
	    	if (list == null || list == undefined)
			{
	    		$("#nn_status_table_div").html('<span>NameNode Details not available. </span>');
				return;
			}
	    	
	    	var tmp;
    		for(var i=0;i<list.length;i++)
    		{
    			tmp=list[i];
    			if(i%2==0)
    			{
    				div_data+='<div class="row even">';
    			}
    			else
    			{
    				div_data+='<div class="row odd">';
    			}
    			div_data+=tmp.name+": ";
    			div_data+=tmp.value;
    			div_data+='</div>';
    		}
    		$("#nn_status_table_div").html(div_data);
		},
		
		fillNameNodeSummaryChart : function(list)
		{
			if(document.getElementById('nn_summary_chart')==undefined||document.getElementById('nn_summary_chart')==null)
				return;
			
//			if (list == null || list == undefined)
//			{
//				$('#nn_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
//				return;
//			}
			
			
			var seriesArray = [];
			var labels = [];
			var colors = [];
			


				if(document.getElementById('nn_summary_chart')=='undefined'||document.getElementById('nn_summary_chart')==null)return;
				if (list == null || list == 'undefined')
				{
					$('#nn_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
					return;
				}
				
				var seriesArray = [];
				var labels = [];
				var colors = [];
				
				if ((list[0] == 0) && (list[1] == 0))
				{
					seriesArray.push(['No Data', 100]);
					labels.push('No Data');
					colors.push('#6C6C6C');
					
					$('#nn_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
				}
				else
				{
					seriesArray.push(['File Reads', list[0]]);
					labels.push('File Reads '+ list[0]);
					colors.push('#EAA228');
					seriesArray.push(['File Writes', list[1]]);
					labels.push('File Writes '+list[1]);
					colors.push('#579575');
					
					
					$.jqplot("nn_summary_chart", [seriesArray], {
						title: {show: false},
						grid:{shadow: false, borderWidth:0.0, background: '#fff'},
						seriesColors: colors,
						seriesDefaults:{renderer:$.jqplot.PieRenderer,  rendererOptions:{sliceMargin:2, startAngle: 45, diameter:170, showDataLabels: true}},
						legend:{show:true, location: 's', labels: labels}
					});
					$('#legend-table'+table_count).css('width','100%');
					$('#legend-table'+table_count).css('bottom','0px');
				}
				
			
			
		},
		
		fillDataNodeStatusTable : function(list)
		{
	    	var div_data='';
	    	if(document.getElementById('dn_status_table_div')==undefined||document.getElementById('dn_status_table_div')==null)return;
	    	if (list == null || list == undefined)
			{
	    		$("#dn_status_table_div").html('<span>DataNode Details not available. </span>');
				return;
			}
	    	
			var tmp;
			for(var i=0;i<list.length;i++)
			{
				tmp=list[i];
				if(i%2==0)
				{
					div_data+='<div class="row even">';
				}
				else
				{
					div_data+='<div class="row odd">';
				}
				div_data+=tmp.name+": ";
				div_data+=tmp.value;
				div_data+='</div>';
			}
	        $("#dn_status_table_div").html(div_data);
		},
		
		fillDataNodeSummaryChart : function(list)
		{
			if(document.getElementById('dn_summary_chart')==undefined||document.getElementById('dn_summary_chart')==null)
				return;
			
			if (list == null || list == undefined)
			{
				$('#dn_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
				return;
			}
			
			var seriesArray = [];
			var labels = [];
			var colors = [];
			
			if ((list[0] == 0) && (list[1] == 0))
			{
				seriesArray.push(['No Data', 100]);
				labels.push('No Data');
				colors.push('#6C6C6C');
				
				$('#dn_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
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
//				colors.push('#579575');
				
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
//				colors.push('#FFB508');
				
				colors.push('#579575');

				$.jqplot("dn_summary_chart", [seriesArray], {
					title: {show: false},
					grid:{shadow: false, borderWidth:0.0, background: '#fff'},
					seriesColors: colors,
					seriesDefaults:{renderer:$.jqplot.PieRenderer, rendererOptions:{sliceMargin:2, startAngle: 45, diameter:170, showDataLabels: true}},
					legend:{show:true, location: 's', labels: labels}
				});
				
//				$('#legend-table').css('margin-left','0px');
				$('#legend-table'+table_count).css('margin-bottom','0px');
				$('#legend-table'+table_count).css('left','0px');
//				$('#legend-table').css('bottom','0px');
				$('#legend-table'+table_count).css('bottom','0px');
				$('#legend-table'+table_count).css('width','100%');
				
				
//				tb.css('bottom','0px');
//				tb.css('left','0px');
			}
			
		},
	    ready: function ()
	    {
	    	
			RemoteManager.getAllNameNodesSummaryTable(false, Dashboard.populateNodeDetailSummaryTable);
			RemoteManager.getAllDataNodesSummaryTable(false, Dashboard.populateDataDetailSummaryTable);
			
			RemoteManager.getAllNameNodeStatusSummary(Dashboard.fillNameNodeStatusTable);
			RemoteManager.getAllNameNodeReadWrites(Dashboard.fillNameNodeSummaryChart);
			
			RemoteManager.getAllDataNodeStatusSummary(Dashboard.fillDataNodeStatusTable);
			RemoteManager.getAllDataNodeMemoryInfo(Dashboard.fillDataNodeSummaryChart);
			
		}
		
};