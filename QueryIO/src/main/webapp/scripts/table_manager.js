CM={
		chartCache : new Object,
		totalCChart:1,
		isEditChart :false,
		selectedNameNode : '',
		selectedChartArray :new Array(),
		chartArray :new Array(),
		isNewChart : false,
		
		ready : function(){
			CM.selectedNameNode = $("#queryIONameNodeId").val();
			if(CM.selectedNameNode==""||CM.selectedNameNode==null){
				$('#chart_list_table_div').html('There is no Namespace configured currently. Please setup a cluster and import data to cluster to use Query and Analysis features.');
				$('#chart_list_table_div').css('text-align','center');
				return;
			}
			DA.selectedNameNode=CM.selectedNameNode;
			CM.populateChartSummaryTable();
			RemoteManager.getNotificationSettings(CM.setNotification);
		},
		
		populateChartSummaryTable : function(){
			$('#chart_list_table').dataTable( {
	   			"sScrollX": "100%",
	   			"bPaginate": true,
				"bLengthChange": true,
				"sPaginationType": "full_numbers",
//				"aLengthMenu" : [50, 100, 200, 500 ],
				"bFilter": false,
				"bSort": true,
				"bInfo": true,
				"bDestroy": true,
				"serverSide": true,
				"searching": true,
				"aoColumnDefs": [{ "bSortable": false, "aTargets": [ 0 ] }],
				"fnServerData": function ( sSource, aoData, fnCallback, oSettings ) {
					RemoteManager.getAllChartsInfo({
			            async:false,
			            callback:function(result){
			                 fnCallback(CM.fillChartSummaryTable(result));	
			                 $(window).trigger('resize');
			                }});
					
					},
				"bAutoWidth": true,
		        "aoColumns": [
		            { "sTitle": '<input type="checkbox" value="selectAll" id="selectAll" onclick="javascript:CM.selectAllCharts(this.id)">'},
		            { "sTitle": "ChartID" },
		            { "sTitle": "Description" },
		            { "sTitle": "Query" }
		        ]
		    } );
			if($('#chart_list_table tbody tr td').hasClass('dataTables_empty'))
				document.getElementById('selectAll').disabled = true;
			else
				document.getElementById('selectAll').disabled = false;
			
			$('#chart_list_table_length').css('margin-top', 7 + 'px');
			$('#chart_list_table_length').css('margin-bottom', 7 + 'px');
			$('#chart_list_table_filter').css('margin-top', 7 + 'px');
			$('#chart_list_table_filter').css('margin-bottom', 7 + 'px');
			
			
		},
		
		fillChartSummaryTable : function(chartData){
			
			CM.selectedChartArray.splice(0, CM.selectedChartArray.length);
			document.getElementById('selectAll').checked = false;
			CM.toggleButton("selectAll", false);
			var object = chartData["data"];
			var tableList = new Array();
			var cCache = new Object;
			if(object!=null)
	   		{
				    CM.chartArray.splice(0,CM.chartArray.length);
					for (var i=0; i< object.length;i++)
					{
						var chartData = object[i];
						var chartId = chartData[0]; // queryID
						var description = chartData[1]; // query description
						var query = chartData[2]; // query description
						var chartc = {};
						chartc["id"] = chartId;
						chartc["description"] = description;
						chartc["query"] = query;
						
						CM.totalChart++;
			   			CM.chartArray.push(chartId);
						var check='<input type="checkbox" id="'+chartId+'" onclick="javascript:CM.clickBox(this.id);">';
						
						cCache[chartId] = chartc;
//						if(status=="SUCCESS"){
//							var path='Reports/Birt'+query["reportpath"];
						chartId='<a href = "javascript:CM.showChart(\''+chartId+'\');">'+chartId+"</a>";
//						}
						var queryContent = '<a href = "javascript:CM.showQuery(\''+query+'\');">'+query+"</a>";
						tableList.push([check,chartId,description,queryContent]);
		   			}
		  	}
			chartData["data"] = tableList;
			CM.chartCache = cCache;
			return chartData;
		},
		
		clickBox : function(id)
		{
			var flag = document.getElementById(id).checked;
			if (flag == true)
			{
				CM.selectedChartArray.push(id.toString());
			}
			else
			{
				var index = jQuery.inArray(id.toString(), CM.selectedChartArray);
				if (index != -1)
				{
					CM.selectedChartArray.splice(index, 1);
				}
			}
			if(($('#chart_list_table tr').length - 1) == CM.selectedChartArray.length)
			{
				document.getElementById("selectAll").checked = flag;
				CM.selectAllChart("selectAll", flag);
			}
			else
				CM.toggleButton(id, flag, "selectAll");
		},
  		selectAllChart : function(id)
   		{

  			var flag = document.getElementById(id).checked;
  			
  			CM.selectedChartArray.splice(0, CM.selectedChartArray.length);
  			for (var i=0; i<CM.chartArray.length; i++)
  			{
  				document.getElementById(CM.chartArray[i]).checked = flag;
  				if (flag)
  				{	
  					CM.selectedChartArray.push(CM.chartArray[i]);
  				}
  			}
  			CM.toggleButton(id, flag);
   		},
   		toggleButton : function(id , value)
   		{

   			if (id == "selectAll")
   			{
   				if (CM.selectedChartArray.length == 1)
   				{
   					dwr.util.byId('chartEdit').disabled=false;
   					dwr.util.byId('chartClone').disabled=false;
   				}
   				else
   				{
   					dwr.util.byId('chartClone').disabled=true;
   					dwr.util.byId('chartEdit').disabled=true;
   				}
   				dwr.util.byId('chartDelete').disabled=!value;
   				
   			}
   			else
   			{
				if(value == false)
					$('#selectAll').attr("checked",false);
				
				if (CM.selectedChartArray.length < 1)
				{
					dwr.util.byId('chartClone').disabled=true;
   					dwr.util.byId('chartEdit').disabled=true;
   					dwr.util.byId('chartDelete').disabled=true;
				}
				else
				{
					if (CM.selectedChartArray.length == 1)
					{
						dwr.util.byId('chartEdit').disabled=false;
	   					dwr.util.byId('chartClone').disabled=false;
					}
					else
					{
						dwr.util.byId('chartClone').disabled=true;
	   					dwr.util.byId('chartEdit').disabled=true;
					}
					dwr.util.byId('chartDelete').disabled=false;
				}
   				
   		
   			}
   		},
		addNewChart : function(){
			CM.isEditChart = false;
			CM.isNewChart = true;
			Navbar.isAddNewChart=true;
			Navbar.isFromsummaryView = true;
			Navbar.changeTab('Charts','charts','edit_charts');
		},
		editSelectedChart : function(){
			Navbar.isEditChart = true;
			var chartId = CM.selectedChartArray[0]+'';
			Navbar.selectedChartId = chartId;
			Navbar.isFromsummaryView = true;
			var nameNodeId = CM.selectedNameNode;
			Navbar.changeTab('Charts','charts','edit_charts');
		},
		backToSummary : function(){
			$('#refreshViewButton').attr('onclick','javascript:Navbar.refreshView()');
			Navbar.refreshView();
		},
		showQuery: function(queryId){
			Navbar.isEditQuery = true;
			Navbar.selectedQueryId = queryId;
			Navbar.isFromsummaryView = true;
			var nameNodeId = CM.selectedNameNode;
			Navbar.changeTab('QueryDesigner','analytics','QueryDesigner');
		},
		
		cloneSelectedChart : function(){
			
			DA.selectedChartId = CM.selectedChartArray[0]
			
			Util.addLightbox("addclone", "resources/cloneChart.html", null, null);
		},
		fillCloneChartObject : function(object){
			
			DA.chartInfo =jQuery.extend(true, {},object);
		},
		deleteSelectedChart : function(){
			DA.selectedChartId = CM.selectedChartArray[0];
			DA.deleteChart();
		},
		populateDeleteChartBox : function(){
			
			var chartIdArray  = CM.selectedChartArray;
			for (var i = 0; i <chartIdArray.length ; i++)
			{
				var id = chartIdArray[i];
				dwr.util.cloneNode('pattern',{ idSuffix:id });
				dwr.util.setValue('chart' + id,id);
				dwr.util.setValue('message' + id,"Perform delete operation on chart "+id);
				dwr.util.setValue('status' + id,'Deleting');
				dwr.util.byId('pattern' + id).style.display = '';
				
			}
			var nameNode = $('#queryIONameNodeId').val();
			
			for (var i = 0; i <chartIdArray.length ; i++)
			{
				var id = chartIdArray[i];
				RemoteManager.deleteChart(id, CM.processDeleteChartResponse);
				
			}
		},
		processDeleteChartResponse : function(dwrResponse){
			
			var id=dwrResponse.id;
			if(dwrResponse.taskSuccess){
				img_src='images/Success_img.png'
				status = 'Success'; 
				dwr.util.byId('imageSuccess' + id).style.display = '';
				var userId = $('#loggedInUserId').text();
				var obj =  Util.getCookie("last-visit-chart"+userId);
				var idInfoObj = null;
				if(obj != null && obj != undefined){
					var filePathObj = JSON.parse(obj);
					idInfoObj = JSON.parse(Util.getCookie("last-visit-idInfoMap"+userId));
					for (var i in idInfoObj)
					{
			    		if (idInfoObj[i] == id)
			    		{ 
			    			
			    			delete filePathObj[i];
						    delete idInfoObj[i];
						    
						    var userId = $('#loggedInUserId').text();
							Util.setCookie("last-visit-cchart"+userId,JSON.stringify(filePathObj), 15);
							Util.setCookie("last-visit-idInfoMap"+userId,JSON.stringify(idInfoObj), 15);
			    		}
					}
				}
				
				
			}
			else{
				img_src='images/Fail_img.png'
				status = 'Fail';
				dwr.util.byId('imageFail' + id).style.display = '';
				var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
				document.getElementById('log_div'+ id).innerHTML=log;
				document.getElementById('log_div'+ id).style.display="block";
			}
			dwr.util.byId('imageProcessing' + id).style.display = 'none';
			dwr.util.setValue('message' + id,dwrResponse.responseMessage);
			dwr.util.setValue('status' + id,status);
			document.getElementById('okbtn').disabled = false;
			Navbar.selectedChartId = '';
//			Navbar.refreshNavBar();
			
			
		},
		showChart : function(){
			Navbar.selectedChartId= CM.selectedChartArray[0];
			Navbar.isEditChart=true;
			
			Navbar.changeTab('Charts','charts','edit_charts');
			
		},
		closeDeleteChartBox : function(){
			DA.closeBox(true);
		},
		fillExecuteTab : function(obj){
			
		},

};