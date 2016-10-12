MRDashboard = {

		
		populateRMTable : function (sTable)
		{
	   		rmCache =new Array();
	   		var columns = sTable.colNames;
	   		var rows = sTable.rows;
			var dataColumn = [];
			var wd = $('#nn_summary_table').width() * .986/(columns.length);
			for(var i=0;i<columns.length;i++)
			{
					if(i==0){
						dataColumn.push({"sTitle":columns[i] , "sWidth": wd,"sClass": "dataTableNodeId"});	
						continue;
					}
					dataColumn.push({"sTitle":columns[i] , "sWidth": wd});
			}
			var dataRow = [];
			for(var i=0;i<rows.length;i++){
				var rowDetail = rows[i];
				rmCache[i]=rows[i];
				var rowData = [];
				for(var j=0;j<rowDetail.length;j++){
					if(j==0){
						var imgStatus;
						if(rowDetail[rowDetail.length-2]=='Stopped'){
							imgStatus = '<img id="noStatus" alt="" src="images/status_stop.png" class="statusImage">';
						}
						else if(rowDetail[rowDetail.length-2]=='Started'){
							imgStatus = '<img id="noStatus" alt="" src="images/status_start.png" class="statusImage">';
						}
						else{
							imgStatus = '<img id="noStatus" alt="" src="images/no_status.png" class="statusImage">';
						}
						rowData.push(['<a href="javascript:Navbar.changeTab(\'Hadoop\',\'rm_detail\',\''+rowDetail[1]+'\',\''+rowDetail[j]+'\');">'+imgStatus+rowDetail[j]+'</a>']);
					}
					else if(j==1){
						rowData.push(['<a href="javascript:Navbar.changeTab(\'Hadoop\',\'rm_detail\',\''+rowDetail[j]+'\');">'+rowDetail[j]+'</a>']);
					}
					else{
						rowData.push(rowDetail[j]);
					}
					
				}
				dataRow.push(rowData);
			}
	   			
	   	$('#nn_summary_table').dataTable( {
				        "bPaginate": false,
//				        "sScrollX": "1200px",
//						"bScrollCollapse": true,
						"bLengthChange": false,
						"bFilter": false,
						"bSort": true,
						"bDestroy": true,
						"bInfo": false,
						"bAutoWidth": false,
						"aaSorting": [[ 1, "desc" ]],
						"aaData": dataRow,
				        "aoColumns": dataColumn
				    } );
	   		var divHeight = $("#nn_summary_table_div").height();
	   		$("#nn_summary_table_div").height(divHeight);
		},
		
		
		
		fillRMTableAndChart: function(list)
		{
			MRDashboard.fillRMSummaryChart(list);
			MRDashboard.fillRMStatusTable(list);
		},
		
		fillRMStatusTable: function(list)
		{
			var total = 0;
			for(var i=0;i<list.length;i++){
				total += parseInt(list[i]);
			}
			document.getElementById("RM_apps_submitted").innerHTML = total;
			document.getElementById("RM_apps_running").innerHTML = list[0];
			document.getElementById("RM_apps_pending").innerHTML = list[1];
			document.getElementById("RM_apps_completed").innerHTML = list[2];
			document.getElementById("RM_apps_killed").innerHTML = list[3];
		},
		
		
		fillRMSummaryChart : function(list)
		{
			
			var total = 0;
			for(var i=0;i<list.length;i++){
				total += parseInt(list[i]);
			}
					
			if(document.getElementById('nn_summary_chart')==undefined||document.getElementById('nn_summary_chart')==null)return;
					
			if (list == null || list == undefined)
			{
				$('#nn_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
				return;
			}
			
			var seriesArray = [];
			var labels = [];
			var colors = [];
			
			if (total == 0)
			{
				$('#nn_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
			}
			else
			{
				seriesArray.push(['Running', (list[0]/total)*100]);
				seriesArray.push(['Pending', (list[1]/total)*100]);
				seriesArray.push(['Completed', (list[2]/total)*100]);
				seriesArray.push(['Killed', (list[3]/total)*100]);
				labels.push('Running');
				colors.push('#CC9752');
				labels.push('Pending');
				colors.push('#996699');
				labels.push('Completed');
				colors.push('#FFCC00');
				labels.push('Killed');
				colors.push('#EAA228');

				$.jqplot("nn_summary_chart", [seriesArray], {
					title: {show: false},
					grid:{shadow: false, borderWidth:0.0, background: '#fff'},
					seriesColors: colors,
					seriesDefaults:{renderer:$.jqplot.PieRenderer, rendererOptions:{sliceMargin:2, startAngle: 45, diameter:185, showDataLabels: true}},
					legend:{show:true, location: 's', labels: labels}
				});
				$('#legend-table'+table_count).css('bottom','0px');
				$('#legend-table'+table_count).css('left','-15px');
				$('#legend-table'+table_count).css('width','95%');
			}
		},
		
		
		
		
		
		
		
		populateNMTable : function (sTable)
		{
	   		nmCache =new Array();
	   		var columns = sTable.colNames;
	   		var rows = sTable.rows;
			var dataColumn = [];
			for(var i=0;i<columns.length;i++)
			{
					if(i==0){
						dataColumn.push({"sTitle":columns[i],"sClass": "dataTableNodeId"});
						continue;
						
					}
					dataColumn.push({"sTitle":columns[i]});
			}
			var dataRow = [];
			for(var i=0;i<rows.length;i++){
				var rowDetail = rows[i];
				nmCache[i]=rows[i];
				var rowData = [];
				for(var j=0;j<rowDetail.length;j++){
					if(j==0){
						var imgStatus;
						if(rowDetail[rowDetail.length-2]=='Stopped'){
							imgStatus = '<img id="noStatus" alt="" src="images/status_stop.png" class="statusImage">';
						}
						else if(rowDetail[rowDetail.length-2]=='Started'){
							imgStatus = '<img id="noStatus" alt="" src="images/status_start.png" class="statusImage">';
						}
						else{
							imgStatus = '<img id="noStatus" alt="" src="images/no_status.png" class="statusImage">';
						}
						rowData.push(['<a href="javascript:Navbar.changeTab(\'Hadoop\',\'nm_detail\',\''+rowDetail[1]+'\',\''+rowDetail[j]+'\');">'+imgStatus+rowDetail[j]+'</a>']);
					}
					else if(j==1){
						rowData.push(['<a href="javascript:Navbar.changeTab(\'Hadoop\',\'nm_detail\',\''+rowDetail[j]+'\');">'+rowDetail[j]+'</a>']);
					}
					else{
						rowData.push(rowDetail[j]);
					}
//					rowData.push(rowDetail[j]);
				}
				dataRow.push(rowData);
			}
		
		$('#dn_summary_table').dataTable( {
				        "bPaginate": false,
						"bLengthChange": false,
						"bFilter": false,
						"bSort": true,
						"bInfo": false,
						"bDestroy": true,
						"bAutoWidth": true,
						"aaSorting": [[ 1, "desc" ]],
						"aaData": dataRow,
				        "aoColumns": dataColumn
				    } );
			var divHeight = $("#dn_summary_table_div").height();
	   		$("#dn_summary_table_div").height(divHeight);
	   		var mainDiv = $("#service_ref").height();
	   		$("#service_ref").height("auto");
		},
		
		
		
		fillNMTableAndChart: function(list)
		{
			MRDashboard.fillNMSummaryChart(list);
			MRDashboard.fillNMStatusTable(list);
		},
		
		fillNMStatusTable: function(list)
		{
			document.getElementById("NM_container_launched").innerHTML = list[0];
			document.getElementById("NM_container_completed").innerHTML = list[1];
			document.getElementById("NM_container_failed").innerHTML = list[2];
			document.getElementById("NM_container_killed").innerHTML = list[3];
			document.getElementById("NM_container_running").innerHTML = list[4];
			document.getElementById("NM_container_initing").innerHTML = list[5];
		},
		
		
		fillNMSummaryChart : function(list)
		{
			
			
			if(document.getElementById('dn_summary_chart')==undefined||document.getElementById('dn_summary_chart')==null)return;
			if (list == null || list == undefined)
			{
				$('#dn_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
				return;
			}
			
			var seriesArray = [];
			var labels = [];
			var colors = [];
			
			if (list[0] == 0)
			{	
				$('#dn_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
			}
			else
			{
				seriesArray.push(['Completed', (list[1]/list[0])*100]);
				seriesArray.push(['Failed', (list[2]/list[0])*100]);
				seriesArray.push(['Killed', (list[3]/list[0])*100]);
				seriesArray.push(['Running', (list[4]/list[0])*100]);
				seriesArray.push(['Initing', (list[5]/list[0])*100]);
				labels.push('Completed');
				colors.push('#CC9752');
				labels.push('Failed');
				colors.push('#996699');
				labels.push('Killed');
				colors.push('#FFCC00');
				labels.push('Running');
				colors.push('#EAA228');
				labels.push('Initing');
				colors.push('#11A211');
				
				$.jqplot("dn_summary_chart", [seriesArray], {
					title: {show: false},
					grid:{shadow: false, borderWidth:0.0, background: '#fff'},
					seriesColors: colors,
					seriesDefaults:{renderer:$.jqplot.PieRenderer, rendererOptions:{sliceMargin:2, startAngle: 45, diameter:185, showDataLabels: true}},
					legend:{show:true, location: 's', labels: labels}
				});
				$('#legend-table'+table_count).css('bottom','0px');
				$('#legend-table'+table_count).css('left','-10px');
				$('#legend-table'+table_count).css('width','95%');
			}
		},
	
	    ready: function ()
	    {
			RemoteManager.getAllResourceManagersSummaryTable(false, MRDashboard.populateRMTable);
			RemoteManager.getAllNodeManagersSummaryTable(false, MRDashboard.populateNMTable);
			RemoteManager.getResourceManagerAppsDetail(MRDashboard.fillRMTableAndChart);
			RemoteManager.getNodeManagerAppsDetail(MRDashboard.fillNMTableAndChart);
//			Navbar.refreshNavBar();
			
		}
		
};