NN_HOST = {
	
		ready: function(){
			RemoteManager.getSummaryTableForNameNodeBasedOnIP(Navbar.selectedHost,NN_HOST.populateNodeDetailSummaryTable);
			RemoteManager.getNameNodeStatusSummaryBasedOnIP(Navbar.selectedHost,NN_HOST.fillStatusTable);
			RemoteManager.getAllNameNodeReadWritesBasedOnIP(Navbar.selectedHost,NN_HOST.fillSummaryChart);
		},
		
//		fillSummaryTable: function(list){
//			for(var i=0;i<list.length;i++){
//				
//			}
//		},
		
		
		fillStatusTable : function(list)
		{
			
			if(document.getElementById('nn_status_table_div')=='undefined'||document.getElementById('nn_status_table_div')==null)return;
	    	var div_data='<div class=" table_header_div" id="status_summary_title" style="width: 98.8%;">Status summary</div>';
	    	
	    	if (list == null || list == 'undefined')
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
		
		populateNodeDetailSummaryTable : function(summaryTable)
		{
			if(document.getElementById('nn_summary_table_div')=='undefined'||document.getElementById('nn_summary_table_div')==null)return;
			var flag=true;
			var colList=[];
			var tableRow=[];
			var rowList='';
			if (summaryTable == null || summaryTable == 'undefined')
			{
				$("#nn_summary_table_div").html('<span>NameNode Details not available. </span>');
				return;
			}

			if(flag)
			{
				flag = false;
				for(var i=0; i< summaryTable.colNames.length-1; i++)
				{
					if(summaryTable.colNames[i]=='Host')continue;
					colList.push({ "sTitle": summaryTable.colNames[i]});	
				}
			}
			rowList = summaryTable.rows;
			var row;
			for(var i=0; i<rowList.length; i++)
			{
				row = rowList[i];
				var rowData = new Array();
				
				if(row[row.length-3]=='Started'){
						rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\',\''+row[1]+'\');" ><img id ="statusStart" alt="" src="images/status_start.png" class="statusImage" >&nbsp;'+row[0]+'</a><br>'+row[1]+'');	
					}
					else if(row[row.length-3]=='Stopped'){
						rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\',\''+row[1]+'\');" ><img id ="statusStop" alt="" src="images/status_stop.png" class="statusImage" >&nbsp;'+row[0]+'<br></a>'+row[1]+'');
					}
					else{
						rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\',\''+row[1]+'\');" ><img id ="noStatus" alt="" src="images/no_status.png" class="statusImage" >&nbsp;'+row[0]+'<br></a>'+row[1]+'');
					}
			
				
				for(var j=2;j<row.length-1;j++){
					
					if(j==row.length-2)
					rowData.push('<div id="node.status'+row[row.length-1]+'">'+row[j]+'</div>');
					else{
						rowData.push(row[j]);
					}
						
				}
				tableRow.push(rowData);	
			}
			
			$('#nn_summary_table').dataTable( {
		        "bPaginate": false,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": false,
				"bInfo": false,
				"bDestroy": true,
				"bAutoWidth": false,
				"aaData": tableRow,
		        "aoColumns": colList
		    }); 
		},
	
		fillSummaryChart : function(list)
		{
			
			if(document.getElementById('nn_summary_chart')==undefined || document.getElementById('nn_summary_chart')==null)return;
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
				$('#nn_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
			}
			else
			{
				seriesArray.push(['File Reads', parseInt(list[0])]);
				labels.push('File Reads '+ list[0]);
				colors.push('#EAA228');
				seriesArray.push(['File Writes', parseInt(list[1])]);
				labels.push('File Writes '+list[1]);
				colors.push('#579575');
				
				$.jqplot("nn_summary_chart", [seriesArray], {
					title: {show: false},
					grid:{shadow: false, borderWidth:0.0, background: '#fff'},
					seriesColors: colors,
					seriesDefaults:{renderer:$.jqplot.PieRenderer,  rendererOptions:{sliceMargin:2, startAngle: 45, diameter:170, showDataLabels: true}},
					legend:{show:true, location: 'e', labels: labels}
				});
				$('#legend-table'+table_count).css('bottom','0px');
				$('#legend-table'+table_count).css('width','50%');
				$('#legend-table'+table_count).css('margin-right','50px');
			}
		}
		
};