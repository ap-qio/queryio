CM={
		chartCache : new Object,
		totalChart:1,
		isEditChart :false,
		selectedNameNode : '',
		selectedChartArray :new Array(),
		currentSelectedChart : '',
		selectedChartId : '',
		chartArray :new Array(),
		isNewChart : false,
		isEditChart : false,
		colList: [],
		selectedQueryId: '',
		queries: [],
		queryInfo: {},
		globalChartPreferences: {},
		checkForAdded: true,
		chartDesignerDirtyBit: false,
		searchColumn: [],
		
		ready : function(){
			CM.selectedNameNode = $("#queryIONameNodeId").val();
			if(CM.selectedNameNode==""||CM.selectedNameNode==null){
				$('#chart_list_table_div').html('There is no Namespace configured currently. Please setup a cluster and import data to cluster to use Query and Analysis features.');
				$('#chart_list_table_div').css('text-align','center');
				return;
			}
			CM.populateChartSummaryTable();
		},
		
		readyChart : function() {
			CM.populateQueries();
		},
		
		populateQueries : function() {
			RemoteManager.getAllQueriesInfo(function (response) {
				var object = JSON.parse(response["responseMessage"]);
				if(object!=null)
		   		{
					CM.queries = object;
					for (queryData in CM.queries)
					{
						var cur = object[queryData];
						console.log("cur: ", cur)
						var queryId = cur["id"]; // queryID
						$('#selectQueryId').append('<option value="' + queryId + '">' + queryId + '</option>');
					}
		   		}
			});
		},
		
		queryIdChanged : function() {
			var queryId = $('#selectQueryId').val();
			console.log(queryId);
			for (queryData in CM.queries)
			{
				var cur = CM.queries[queryData];
				var thisQueryId = cur["id"];
				console.log("thisQueryId: " + thisQueryId);
				if(queryId == thisQueryId){
					var thisColMap = cur["selectedCols"];
					CM.selectedQueryId = queryId;
					CM.colMap = JSON.parse(thisColMap);
				}
			}

			CM.populateColumns();
			CM.createInitialChartGrid();
			RC.ready();
			
		},
		
		closeSelectionDiv : function() {

			$(
					'#gantt_y_labelColFilters,#gantt_y_startColFilters,#gantt_y_endColFilters,#difference_y_positiveColFilters,#difference_y_negetiveColFilters,#stock_y_highColFilters,#stock_y_lowColFilters,#stock_y_openColFilters,#stock_y_closeColFilters,#line_y_seriesColFilters,#bubble_y_valueColFilters,#bubble_y_sizeColFilters,#searchColFilters,#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchFromFilters,#groupByColFilters,#havingColFilters,#orderByColFilters,#whereFilters')
					.hide();
		},
		
		populateColumns : function() {
			console.log(CM.colMap);
			// Do something with this colmap
			for(col in CM.colMap){
				CM.searchColumn.push(col);
				CM.colList.push(col);
			}
			
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
			console.log(chartData);
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
		
		saveChart : function() {
			CM.showChartSavePopup();
		},
		
		showChartSavePopup : function() {
			Util.addLightbox('export', 'pages/popup.jsp');
		},
		
		chartSaveResponse : function(response) {
			var id = CM.selectedChartId;

			if (response.taskSuccess) {
				status = "Success";
				imgId = "popup.image.success";

			} else {
				status = "Failure";
				imgId = "popup.image.fail";
			}

			message = response.responseMessage;

			dwr.util.byId('popup.image.processing' + id).style.display = 'none';
			dwr.util.byId(imgId + id).style.display = '';

			dwr.util.setValue('popup.message' + id, message);
			dwr.util.setValue('popup.status' + id, status);
			dwr.util.byId('ok.popup').disabled = false;
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
			
			CM.selectedChartId = CM.selectedChartArray[0]
			
			Util.addLightbox("addclone", "resources/cloneChart.html", null, null);
		},
		fillCloneChartObject : function(object){
			
			CM.chartInfo =jQuery.extend(true, {},object);
		},
		deleteSelectedChart : function(){
			CM.selectedChartId = CM.selectedChartArray[0];
			CM.deleteChart();
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
			
		},
		showChart : function(){
			Navbar.selectedChartId= CM.selectedChartArray[0];
			Navbar.isEditChart=true;
			
			Navbar.changeTab('Charts','charts','edit_charts');
			
		},
		closeDeleteChartBox : function(){
			CM.closeBox(true);
		},
		
		closeBox : function(isRefresh) {
			Util.removeLightbox("export");
				if (isRefresh)
					Navbar.refreshView();
		},
		
		showLineYSeriesCol : function(id) {
			// $('#line_y_seriesColFilters').show();
			// $('#line_y_seriesColFilters').fadeIn('slow');
		},
		
		editChartPreferences : function() {
			for ( var attr in CM.queryInfo["chartDetail"]) {
				if (attr == "chartPreferences")
					continue;
				if (CM.queryInfo["chartDetail"][attr]["title"] == $(
						'#line_chart_title').val()) {
					RC.chartKey = attr;
					RC.chartType = CM.queryInfo["chartDetail"][attr]["type"];
					break;
				}
			}
			RC.chartOperation = 'edit';
			RC.chartPreferenceType = 'local';

			Util.addLightbox("chart_prefernces",
					"resources/chart_preferences.html", null, null);
		},
		
		resetChart : function() {
			$('#chartPreviewDiv').css('display', 'none');
			$('#defaultImg').css('display', '');
			$("#row_position").val("1");
			$("#col_span").val("1");

			$("#chart_width").val("300");
			$("#chart_height").val("300");
			$("#line_chart_title").val("");
			$('#chart_type').prop('selectedIndex', 0);
			$('#chart_dimension').prop('selectedIndex', 0);
			$('#chart_position').prop('selectedIndex', 0);

			$('#line_x_series').prop('selectedIndex', 0);
			$('#gantt_x_series').prop('selectedIndex', 0);
			$('#difference_x_series').prop('selectedIndex', 0);
			$('#stock_x_series').prop('selectedIndex', 0);
			$('#bubble_x_series').prop('selectedIndex', 0);

			$("#line_y_series").val("");
			$("#bubble_y_value").val("");
			$("#bubble_y_size").val("");
			$("#stock_y_high").val("");
			$("#stock_y_low").val("");
			$("#stock_y_open").val("");
			$("#stock_y_close").val("");
			$("#difference_y_positive").val("");
			$("#difference_y_negetive").val("");
			$("#gantt_y_label").val("");
			$("#gantt_y_start").val("");
			$("#gantt_y_end").val("");

			$("#line_x_axis_legend").val("");
			$("#line_y_axis_legend").val("");

			$("#bubble_x_axis_legend").val("");
			$("#bubble_y_axis_legend").val("");
			$("#gantt_x_axis_legend").val("");
			$("#gantt_y_axis_legend").val("");
			$("#difference_x_axis_legend").val("");
			$("#difference_y_axis_legend").val("");
			$("#stock_x_axis_legend").val("");
			$("#stock_y_axis_legend").val("");
			$("#y_scale_min_val").val("");
			$("#line_x_series_scale").val("Linear");

			// $('#pie_x_series').prop('selectedIndex', 0);
			// $('#pie_y_series').prop('selectedIndex', 0);

			$("#line_chart_table").css('display', 'none');
			$('#bubble_chart_table').css("display", "none");
			$('#stock_chart_table').css("display", "none");
			$('#difference_chart_table').css("display", "none");
			$('#gantt_chart_table').css("display", "none");
			// $("#pie_chart_table").css('display', 'none');

			jQuery("#chartTable").resetSelection();
			if (document.getElementById('deleteChart') != undefined
					&& document.getElementById('deleteChart') != null)
				document.getElementById('deleteChart').disabled = true;

		},

		fillChart : function(value) {
			RC.ready();
			$("#lineAdd").css('display', 'none');
			$("#lineSave").css('display', '');
			$("#bubbleAdd").css('display', 'none');
			$("#bubbleSave").css('display', '');
			$("#ganttAdd").css('display', 'none');
			$("#ganttSave").css('display', '');
			$("#differenceAdd").css('display', 'none');
			$("#differenceSave").css('display', '');
			$("#stockAdd").css('display', 'none');
			$("#stockSave").css('display', '');

			// $("#pieAdd").css('display', 'none');
			// $("#pieSave").css('display', '');
			$("#error_msg").text('');
			var currTitle = CM.selectedChartId;
			$("#chart_type").val(value[currTitle]["type"]);
			$("#chart_position").val(value[currTitle]["position"]);
			$("#chart_height").val(value[currTitle]["height"]);
			$("#chart_width").val(value[currTitle]["width"]);
			$("#row_position").val(value[currTitle]["rowPosition"]);
			$("#col_span").val(value[currTitle]["colSpan"]);
			$("#line_chart_title").val(value[currTitle]["title"]);
			$("#chart_dimension").val(value[currTitle]["dimension"]);
			$('#chartPRButton').removeAttr("disabled");

			CM.currentSelectedChart = value[currTitle]["title"];

			RC.sortType = value[currTitle]["xseriesSortType"];

			var type = value[currTitle]["type"];
			if ((type == "line") || (type == "bar") || (type == "pie")
					|| (type == "area") || (type == "scatter") || (type == "meter")
					|| (type == "tube") || (type == "cone") || (type == "pyramid")
					|| (type == "radar")) {
				// $('#pie_chart_table').css("display", "none");
				$('#line_chart_table').css("display", "");

				$('#pie_chart_table').css("display", "none");
				$('#bubble_chart_table').css("display", "none");
				$('#stock_chart_table').css("display", "none");
				$('#difference_chart_table').css("display", "none");
				$('#gantt_chart_table').css("display", "none");

				$("#line_y_series").val(value[currTitle]["yseriesArray"]);
				$("#line_x_axis_legend").val(value[currTitle]["xlegend"]);
				$("#line_y_axis_legend").val(value[currTitle]["ylegend"]);

				$("#line_x_series").val(value[currTitle]["xseries"]);
				$("#y_scale_min_val").val(value[currTitle]["yScaleMinVal"]);
				if (value[currTitle]["xseries"] == 'Linear') {
					$('#y_scale_min_val_row').show();
				} else {
					$('#y_scale_min_val_row').hide();
				}
				$("#line_x_series_scale").val(value[currTitle]["chartscale"]);
				if (value[currTitle]["chartscale"] == 'Linear') {
					$('#y_scale_min_val_row').show();
				} else {
					$('#y_scale_min_val_row').hide();
				}

				$("#y_series_grouping").val(value[currTitle]["ygrouping"]);
				$("#line_x_series_sort_type").val(
						value[currTitle]["xseriesSortType"]);
				if (value[currTitle]["xseriesSortType"] != "None") {
					$("#line_x_series_sort_column").removeAttr("disabled");
					$("#line_x_series_sort_column").val(
							value[currTitle]["xseriesSortColumn"]);
				}
				RC.ySeriesHistory['lineseries'] = jQuery.extend(true, {},
						value[currTitle]["yseries"]);
				RC.selectedChartYObject['lineseries'] = jQuery.extend(true, {},
						value[currTitle]["yseries"]);
				RC.selectedChartYArray['lineseries'] = value[currTitle]["yseriesArray"];
				RC.issetTimeOut = true;
				RC.setYSeriesHistoryObj('line', 'series');

				// } else if (type == "pie") {
				// $('#pie_chart_table').css("display", "");
				// $('#line_chart_table').css("display", "none");
				// $("#pie_x_series").val(value[currTitle]["xseries"]);
				// $("#pie_y_series").val(value[currTitle]["yseries"][0]);
			} else if ((type == "bubble")) {
				$('#bubble_chart_table').css("display", "");
				$('#line_chart_table').css("display", "none");

				$('#pie_chart_table').css("display", "none");
				$('#stock_chart_table').css("display", "none");
				$('#difference_chart_table').css("display", "none");
				$('#gantt_chart_table').css("display", "none");

				$("#bubble_x_series").val(value[currTitle]["xseries"]);
				$("#bubble_y_series_grouping").val(value[currTitle]["ygrouping"]);
				$("#bubble_x_series_sort_type").val(
						value[currTitle]["xseriesSortType"]);
				if (value[currTitle]["xseriesSortType"] != "None") {
					$("#bubble_x_series_sort_column").removeAttr("disabled");
					$("#bubble_x_series_sort_column").val(
							value[currTitle]["xseriesSortColumn"]);
				}
				$("#bubble_x_axis_legend").val(value[currTitle]["xlegend"]);
				$("#bubble_y_axis_legend").val(value[currTitle]["ylegend"]);

				$("#bubble_y_value").val(value[currTitle]["yseriesValueArray"]);
				$("#bubble_y_size").val(value[currTitle]["yseriesSizeArray"]);

				RC.ySeriesHistory['bubblevalue'] = jQuery.extend(true, {},
						value[currTitle]["yseriesValue"]);
				RC.selectedChartYObject['bubblevalue'] = jQuery.extend(true, {},
						value[currTitle]["yseriesValue"]);
				RC.selectedChartYArray['bubblevalue'] = value[currTitle]["yseriesValueArray"];
				RC.issetTimeOut = true;
				RC.setYSeriesHistoryObj('bubble', 'value');

				RC.ySeriesHistory['bubblesize'] = jQuery.extend(true, {},
						value[currTitle]["yseriesSize"]);
				RC.selectedChartYObject['bubblesize'] = jQuery.extend(true, {},
						value[currTitle]["yseriesSize"]);
				RC.selectedChartYArray['bubblesize'] = value[currTitle]["yseriesSizeArray"];
				RC.issetTimeOut = true;
				RC.setYSeriesHistoryObj('bubble', 'size');

			} else if ((type == "stock")) {
				$('#stock_chart_table').css("display", "");
				$('#line_chart_table').css("display", "none");

				$('#pie_chart_table').css("display", "none");
				$('#bubble_chart_table').css("display", "none");
				$('#difference_chart_table').css("display", "none");
				$('#gantt_chart_table').css("display", "none");

				$("#stock_x_series").val(value[currTitle]["xseries"]);
				$("#stock_y_series_grouping").val(value[currTitle]["ygrouping"]);

				$("#stock_x_series_sort_type").val(
						value[currTitle]["xseriesSortType"]);

				if (value[currTitle]["xseriesSortType"] != "None") {
					$("#stock_x_series_sort_column").removeAttr("disabled");
					$("#stock_x_series_sort_column").val(
							value[currTitle]["xseriesSortColumn"]);
				}
				$("#stock_x_axis_legend").val(value[currTitle]["xlegend"]);
				$("#stock_y_axis_legend").val(value[currTitle]["ylegend"]);

				$("#stock_y_high").val(value[currTitle]["yseriesHighArray"]);
				$("#stock_y_low").val(value[currTitle]["yseriesLowArray"]);
				$("#stock_y_open").val(value[currTitle]["yseriesOpenArray"]);
				$("#stock_y_close").val(value[currTitle]["yseriesCloseArray"]);

				RC.ySeriesHistory['stockhigh'] = jQuery.extend(true, {},
						value[currTitle]["yseriesHigh"]);
				RC.selectedChartYObject['stockhigh'] = jQuery.extend(true, {},
						value[currTitle]["yseriesHigh"]);
				RC.selectedChartYArray['stockhigh'] = value[currTitle]["yseriesHighArray"];
				RC.issetTimeOut = true;
				RC.setYSeriesHistoryObj('stock', 'high');

				RC.ySeriesHistory['stocklow'] = jQuery.extend(true, {},
						value[currTitle]["yseriesLow"]);
				RC.selectedChartYObject['stocklow'] = jQuery.extend(true, {},
						value[currTitle]["yseriesLow"]);
				RC.selectedChartYArray['stocklow'] = value[currTitle]["yseriesLowArray"];
				RC.issetTimeOut = true;
				RC.setYSeriesHistoryObj('stock', 'low');

				RC.ySeriesHistory['stockopen'] = jQuery.extend(true, {},
						value[currTitle]["yseriesOpen"]);
				RC.selectedChartYObject['stockopen'] = jQuery.extend(true, {},
						value[currTitle]["yseriesOpen"]);
				RC.selectedChartYArray['stockopen'] = value[currTitle]["yseriesOpenArray"];
				RC.issetTimeOut = true;
				RC.setYSeriesHistoryObj('stock', 'open');

				RC.ySeriesHistory['stockclose'] = jQuery.extend(true, {},
						value[currTitle]["yseriesClose"]);
				RC.selectedChartYObject['stockclose'] = jQuery.extend(true, {},
						value[currTitle]["yseriesClose"]);
				RC.selectedChartYArray['stockclose'] = value[currTitle]["yseriesCloseArray"];
				RC.issetTimeOut = true;
				RC.setYSeriesHistoryObj('stock', 'close');

			} else if ((type == "difference")) {
				$('#difference_chart_table').css("display", "");
				$('#line_chart_table').css("display", "none");

				$('#pie_chart_table').css("display", "none");
				$('#bubble_chart_table').css("display", "none");
				$('#stock_chart_table').css("display", "none");
				$('#gantt_chart_table').css("display", "none");

				$("#difference_x_series").val(value[currTitle]["xseries"]);
				$("#difference_y_series_grouping").val(
						value[currTitle]["ygrouping"]);

				$("#difference_x_series_sort_type").val(
						value[currTitle]["xseriesSortType"]);
				if (value[currTitle]["xseriesSortType"] != "None") {
					$("#difference_x_series_sort_column").removeAttr("disabled");
					$("#difference_x_series_sort_column").val(
							value[currTitle]["xseriesSortColumn"]);
				}
				$("#difference_x_axis_legend").val(value[currTitle]["xlegend"]);
				$("#difference_y_axis_legend").val(value[currTitle]["ylegend"]);

				$("#difference_y_positive").val(
						value[currTitle]["yseriesPositiveArray"]);
				$("#difference_y_negetive").val(
						value[currTitle]["yseriesNegativeArray"]);

				RC.ySeriesHistory['differencepositive'] = jQuery.extend(true, {},
						value[currTitle]["yseriesPositive"]);
				RC.selectedChartYObject['differencepositive'] = jQuery.extend(true,
						{}, value[currTitle]["yseriesPositive"]);
				RC.selectedChartYArray['differencepositive'] = value[currTitle]["yseriesPositiveArray"];
				RC.issetTimeOut = true;
				RC.setYSeriesHistoryObj('difference', 'positive');

				RC.ySeriesHistory['differencenegetive'] = jQuery.extend(true, {},
						value[currTitle]["yseriesNegative"]);
				RC.selectedChartYObject['differencenegetive'] = jQuery.extend(true,
						{}, value[currTitle]["yseriesNegative"]);
				RC.selectedChartYArray['differencenegetive'] = value[currTitle]["yseriesNegativeArray"];
				RC.issetTimeOut = true;
				RC.setYSeriesHistoryObj('difference', 'negetive');

			} else if ((type == "gantt")) {
				$('#gantt_chart_table').css("display", "");
				$('#line_chart_table').css("display", "none");

				$('#pie_chart_table').css("display", "none");
				$('#bubble_chart_table').css("display", "none");
				$('#stock_chart_table').css("display", "none");
				$('#difference_chart_table').css("display", "none");

				$("#gantt_x_series").val(value[currTitle]["xseries"]);
				$("#gantt_x_series_sort_type").val(
						value[currTitle]["xseriesSortType"]);
				if (value[currTitle]["xseriesSortType"] != "None") {
					$("#gantt_x_series_sort_column").removeAttr("disabled");
					$("#gantt_x_series_sort_column").val(
							value[currTitle]["xseriesSortColumn"]);
				}
				$("#gantt_x_axis_legend").val(value[currTitle]["xlegend"]);
				$("#gantt_y_axis_legend").val(value[currTitle]["ylegend"]);

				$("#gantt_y_label").val(value[currTitle]["yseriesLabelArray"]);
				$("#gantt_y_start").val(value[currTitle]["yseriesStartArray"]);
				$("#gantt_y_end").val(value[currTitle]["yseriesEndArray"]);

				RC.ySeriesHistory['ganttlabel'] = jQuery.extend(true, {},
						value[currTitle]["yseriesLabel"]);
				RC.selectedChartYObject['ganttlabel'] = jQuery.extend(true, {},
						value[currTitle]["yseriesLabel"]);
				RC.selectedChartYArray['ganttlabel'] = value[currTitle]["yseriesLabelArray"];
				RC.issetTimeOut = true;
				RC.setYSeriesHistoryObj('gantt', 'label');

				RC.ySeriesHistory['ganttstart'] = jQuery.extend(true, {},
						value[currTitle]["yseriesStart"]);
				RC.selectedChartYObject['ganttstart'] = jQuery.extend(true, {},
						value[currTitle]["yseriesStart"]);
				RC.selectedChartYArray['ganttstart'] = value[currTitle]["yseriesStartArray"];
				RC.issetTimeOut = true;
				RC.setYSeriesHistoryObj('gantt', 'start');

				RC.ySeriesHistory['ganttend'] = jQuery.extend(true, {},
						value[currTitle]["yseriesEnd"]);
				RC.selectedChartYObject['ganttend'] = jQuery.extend(true, {},
						value[currTitle]["yseriesEnd"]);
				RC.selectedChartYArray['ganttend'] = value[currTitle]["yseriesEndArray"];
				RC.issetTimeOut = true;
				RC.setYSeriesHistoryObj('gantt', 'end');

			} else {
				$('#pie_chart_table').css("display", "none");
				$('#line_chart_table').css("display", "none");
				$('#bubble_chart_table').css("display", "none");
				$('#stock_chart_table').css("display", "none");
				$('#difference_chart_table').css("display", "none");
				$('#gantt_chart_table').css("display", "none");
			}

		},
		
		getDefaultChartDetails : function() {
			var obj = new Object();

			obj["chartPreferences"] = new Object();
			PR = {
				commonJson : {},
				yAxisJson : {},
				outLineJson : {},
				insetsJson : {},
				leaderLineJson : {},
				xAxisJson : {},
				titleJson : {},
				lineChartJson : {},
				labelJson : {},
				legendJson : {}
			};
			var currentType = CHARTPR.currentType;

			PR.commonJson["background"] = "ffffff";
			PR.commonJson["wallBackground"] = "ffffff";
			PR.commonJson["floorBackground"] = "ffffff";
			PR.commonJson["clientBackground"] = "ffffff";
			PR.commonJson["plotBackground"] = "ffffff";
			PR.commonJson["emptyChartMessage"] = "";
			PR.commonJson["topColors"] = [ "579575", "4BB2C5", "EAA228", "C5B47F",
					"953579", "4B5DE4", "D8B83F", "990000", "003300", "004a6d" ];

			PR.leaderLineJson["visible"] = false;
			PR.leaderLineJson["style"] = "stretch";
			PR.leaderLineJson["width"] = "1";
			PR.leaderLineJson["color"] = "000000";
			PR.leaderLineJson["lineStyle"] = "Solid";
			PR.leaderLineJson["lineLength"] = "2";

			PR.xAxisJson["value"] = "Xaxis";
			PR.xAxisJson["position"] = "below";
			PR.xAxisJson["visible"] = true;
			PR.xAxisJson["background"] = "";
			PR.xAxisJson["shadow"] = "";
			PR.xAxisJson["text-align"] = "left";
			PR.xAxisJson["color"] = "4c4c4c";
			PR.xAxisJson["font-style"] = "normal";
			PR.xAxisJson["font-size"] = "12";
			PR.xAxisJson["font-family"] = "Verdana";
			PR.xAxisJson["titleFontSize"] = "12";
			PR.xAxisJson["titleColor"] = "4c4c4c";

			PR.xAxisJson["gridline"] = new Object();
			PR.xAxisJson["gridline"]["visible"] = false;
			PR.xAxisJson["gridline"]["style"] = "Solid";
			PR.xAxisJson["gridline"]["width"] = "1";
			PR.xAxisJson["gridline"]["color"] = "000000";

			PR.xAxisJson["outline"] = new Object();
			PR.xAxisJson["outline"]["visible"] = false;
			PR.xAxisJson["outline"]["style"] = "Solid";
			PR.xAxisJson["outline"]["width"] = "1";
			PR.xAxisJson["outline"]["color"] = "000000";

			PR.xAxisJson["insets"] = new Object();
			PR.xAxisJson["insets"]["top"] = "1";
			PR.xAxisJson["insets"]["bottom"] = "1";
			PR.xAxisJson["insets"]["left"] = "1";
			PR.xAxisJson["insets"]["right"] = "1";

			PR.yAxisJson["value"] = "Yaxis";
			PR.yAxisJson["position"] = "left";
			PR.yAxisJson["visible"] = true;
			PR.yAxisJson["background"] = "";
			PR.yAxisJson["shadow"] = "";
			PR.yAxisJson["text-align"] = "left";
			PR.yAxisJson["color"] = "4c4c4c";
			PR.yAxisJson["font-style"] = "normal";
			PR.yAxisJson["font-size"] = "12";
			PR.yAxisJson["font-family"] = "Verdana";
			PR.yAxisJson["titleFontSize"] = "12";
			PR.yAxisJson["titleColor"] = "4c4c4c";

			PR.yAxisJson["gridline"] = new Object();
			PR.yAxisJson["gridline"]["visible"] = true;
			PR.yAxisJson["gridline"]["style"] = "Solid";
			PR.yAxisJson["gridline"]["width"] = "1";
			PR.yAxisJson["gridline"]["color"] = "000000";

			PR.yAxisJson["outline"] = new Object();
			PR.yAxisJson["outline"]["visible"] = false;
			PR.yAxisJson["outline"]["style"] = "Solid";
			PR.yAxisJson["outline"]["width"] = "1";
			PR.yAxisJson["outline"]["color"] = "000000";

			PR.yAxisJson["insets"] = new Object();
			PR.yAxisJson["insets"]["top"] = "1";
			PR.yAxisJson["insets"]["bottom"] = "1";
			PR.yAxisJson["insets"]["left"] = "1";
			PR.yAxisJson["insets"]["right"] = "1";

			PR.titleJson["value"] = "Chart";
			PR.titleJson["anchor"] = "top";
			PR.titleJson["text-align"] = "center";
			PR.titleJson["color"] = "333333";
			PR.titleJson["font-style"] = "bold";
			PR.titleJson["font-size"] = "12";
			PR.titleJson["font-family"] = "Verdana";
			PR.titleJson["background"] = "69abdb";
			PR.titleJson["outline"] = new Object();
			PR.titleJson["outline"]["visible"] = false;
			PR.titleJson["outline"]["style"] = "Solid";
			PR.titleJson["outline"]["width"] = "1";
			PR.titleJson["outline"]["color"] = "000000";
			PR.titleJson["insets"] = new Object();
			PR.titleJson["insets"]["top"] = "1";
			PR.titleJson["insets"]["bottom"] = "1";
			PR.titleJson["insets"]["left"] = "1";
			PR.titleJson["insets"]["right"] = "1";

			PR.lineChartJson["showCurve"] = false;
			PR.lineChartJson["curveColor"] = "";

			PR.labelJson["visible"] = false;
			PR.labelJson["position"] = "outside";
			PR.labelJson["text-align"] = "left";
			PR.labelJson["background"] = "";
			PR.labelJson["shadow"] = "";
			PR.labelJson["font-color"] = "000000";
			PR.labelJson["font-style"] = "normal";
			PR.labelJson["font-size"] = "12";
			PR.labelJson["font-family"] = "Arial";
			PR.labelJson["values"] = 'actual';
			PR.labelJson["prefix"] = "";
			PR.labelJson["suffix"] = "";
			PR.labelJson["separator"] = ":";

			PR.labelJson["outline"] = new Object();
			PR.labelJson["outline"]["visible"] = false;
			PR.labelJson["outline"]["style"] = "Solid";
			PR.labelJson["outline"]["width"] = "1";
			PR.labelJson["outline"]["color"] = "000000";

			PR.labelJson["insets"] = new Object();
			PR.labelJson["insets"]["top"] = "1";
			PR.labelJson["insets"]["bottom"] = "1";
			PR.labelJson["insets"]["left"] = "1";
			PR.labelJson["insets"]["right"] = "1";

			PR.legendJson["visible"] = true;

			PR.legendJson["visibleTitle"] = false;
			PR.legendJson["title-font-color"] = "000000";
			PR.legendJson["title-font-size"] = "12";
			PR.legendJson["title-font-family"] = "Arial";
			PR.legendJson["title-font-style"] = "normal";

			PR.legendJson["position"] = "right";
			PR.legendJson["anchor"] = "middle";
			PR.legendJson["Stretch"] = "horizontal";
			PR.legendJson["background"] = "";

			PR.legendJson["outline"] = new Object();
			PR.legendJson["outline"]["visible"] = false;
			PR.legendJson["outline"]["style"] = "Solid";
			PR.legendJson["outline"]["width"] = "1";
			PR.legendJson["outline"]["color"] = "000000";

			PR.legendJson["insets"] = new Object();
			PR.legendJson["insets"]["top"] = "1";
			PR.legendJson["insets"]["bottom"] = "1";
			PR.legendJson["insets"]["left"] = "1";
			PR.legendJson["insets"]["right"] = "1";

			PR.outLineJson["visible"] = true;
			PR.outLineJson["style"] = "Solid";
			PR.outLineJson["width"] = "2";
			PR.outLineJson["color"] = "000000";

			PR.insetsJson["top"] = "1";
			PR.insetsJson["bottom"] = "1";
			PR.insetsJson["left"] = "1";
			PR.insetsJson["right"] = "1";

			obj["chartPreferences"] = PR;
			return obj;

		},
		
		showChartSample : function(idprefix, chart) {

			$('#' + idprefix + 'defaultImg').css('display', 'none');
			$('#' + idprefix + 'chartPreviewDiv').css('display', '');
			var prObject = chart["chartPreferences"];
			$('#' + idprefix + 'main_chart_preveiw_title').html(
					'<b>' + chart["title"] + '</b>')
			var commonJson = prObject["commonJson"]

			var isXGridLineVisible = prObject["xAxisJson"]["gridline"]["visible"];
			var isYGridLineVisible = prObject["yAxisJson"]["gridline"]["visible"];

			if (commonJson == undefined || commonJson == null) {
				commonJson = {
					"background" : "bf0000",
					"wallBackground" : "ffaa56",
					"floorBackground" : "aaffd4",
					"clientBackground" : "ffff56",
					"includeAxesBackground" : "007f7f",
					"withinAxesBackground" : "aad4ff",
					"plotBackground" : "7f00ff",
					"emptyChartMessage" : "Hello",
					"topColors" : [ "579575", "4BB2C5", "EAA228", "C5B47F",
							"953579", "4B5DE4", "D8B83F", "990000", "003300",
							"004a6d" ]
				};
			}

			var seriesArray = [];
			var labels = [];
			var colors = [];

			var colorCode = commonJson["topColors"];
			for (var i = 0; i < colorCode.length; i++) {
				seriesArray.push([ String.fromCharCode(65 + i), +(6.6) * (i + 1) ]);
				labels.push(String.fromCharCode(65 + i));
				colors.push('#' + colorCode[i]);
			}

			var pos;
			var loc;
			if (prObject["labelJson"]["position"] == "inside") {
				pos = 0.6;
				if (chart["type"] == "bar" || chart["type"] == "tube"
						|| chart["type"] == "cone" || chart["type"] == "pyramid")
					loc = 's';
				else
					loc = 'e';
			} else {
				pos = 1.1;
				if (chart["type"] == "bar" || chart["type"] == "tube"
						|| chart["type"] == "cone" || chart["type"] == "pyramid")
					loc = 'n';
				else
					loc = 'w';
			}

			$('#' + idprefix + 'main_preview_chart').html('');

			var plotId = idprefix + 'main_preview_chart';
			if (document.getElementById(plotId) == undefined) {
				return;
			}

			if (chart["type"] == "pie") {
				$.jqplot(plotId, [ seriesArray ], {
					title : {
						show : false
					},
					grid : {
						shadow : false,
						borderWidth : 0.0,
						background : 'transparent'
					},
					seriesColors : colors,
					seriesDefaults : {
						renderer : $.jqplot.PieRenderer,
						rendererOptions : {
							sliceMargin : 2,
							startAngle : 45,
							diameter : 120,
							showDataLabels : true,
							dataLabels : 'value',
							dataLabelPositionFactor : pos
						}
					},
					legend : {
						show : true,
						location : 'e',
						labels : labels
					}
				});
			} else if (chart["type"] == "line") {
				$.jqplot(plotId, [ [ 28, 13, 25, 15, 33 ] ], {
					title : {
						show : false
					},
					series : [ {
						showMarker : false,
						pointLabels : {
							show : true,
							location : loc,
							ypadding : 2,
							labelsFromSeries : true
						}

					} ],

					grid : {
						shadow : false,
						borderWidth : 0.0,
						background : 'transparent',
						gridLineColor : prObject["yAxisJson"]["gridline"]["color"],
						gridLineWidth : prObject["yAxisJson"]["gridline"]["width"]
					},
					seriesColors : [ colorCode[0] ],
					axes : {
						xaxis : {
							label : chart["xlegend"],
							tickOptions : {
								showGridline : isXGridLineVisible,
								showLabel : prObject.xAxisJson["visible"],
							}
						},
						yaxis : {
							label : chart["ylegend"],
							tickOptions : {
								showGridline : isYGridLineVisible,
								showLabel : prObject.yAxisJson["visible"],
							},

						}
					},
					legend : {
						show : true,
						location : 'e'
					},
				});
				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-table-legend-swatch-outline')
						.css("color", "#" + colorCode[0]);

			} else if (chart["type"] == "stock") {
				$('#' + idprefix + 'main_preview_chart').html(
						'<img src="images/stockchart.png"/ style="height: 170px; background: #'
								+ commonJson["clientBackground"] + ' ">');
				$('#' + idprefix + 'mainlegendTable')
						.html(
								'<tbody><tr class="jqplot-table-legend" style="background-color: transparent;"><td class="jqplot-table-legend jqplot-table-legend-swatch" style="text-align: center; padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;"><div class="jqplot-table-legend-swatch-outline" style="color: rgb(0, 150, 188);"><div class="jqplot-table-legend-swatch"></div></div></td><td class="jqplot-table-legend jqplot-table-legend-label" style="padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;">Series 1</td></tr></tbody>');
			} else if (chart["type"] == "gantt") {
				$('#' + idprefix + 'main_preview_chart').html(
						'<img src="images/ganttchart.png"/ style="height: 170px; background: #'
								+ commonJson["clientBackground"] + ' ">');
				$('#' + idprefix + 'mainlegendTable')
						.html(
								'<tbody><tr class="jqplot-table-legend" style="background-color: transparent;"><td class="jqplot-table-legend jqplot-table-legend-swatch" style="text-align: center; padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;"><div class="jqplot-table-legend-swatch-outline" style="color: rgb(0, 150, 188);"><div class="jqplot-table-legend-swatch"></div></div></td><td class="jqplot-table-legend jqplot-table-legend-label" style="padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;">Series 1</td></tr></tbody>');
			} else if (chart["type"] == "difference") {
				$('#' + idprefix + 'main_preview_chart').html(
						'<img src="images/differencechart.png"/ style="height: 170px; background: #'
								+ commonJson["clientBackground"] + ' ">');
				$('#' + idprefix + 'mainlegendTable')
						.html(
								'<tbody><tr class="jqplot-table-legend" style="background-color: transparent;"><td class="jqplot-table-legend jqplot-table-legend-swatch" style="text-align: center; padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;"><div class="jqplot-table-legend-swatch-outline" style="color: rgb(0, 150, 188);"><div class="jqplot-table-legend-swatch"></div></div></td><td class="jqplot-table-legend jqplot-table-legend-label" style="padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;">Series 1</td></tr></tbody>');

				// var l0 = [6, 11, 10, 13, 11, 7];
				// var l1 = [3, 6, 7, 7, 5, 3];
				// var l2 = [4, 8, 9, 10, 8, 6];
				// var l3 = [9, 13, 14, 16, 17, 19];
				// var l4 = [15, 17, 16, 18, 13, 11];
				// $.jqplot(plotId, [l0,l3], {
				// title : {
				// show : false
				// },
				// series : [ {
				// showMarker : false,
				// pointLabels : {
				// show : true,
				// location : loc,
				// ypadding : 2,
				// labelsFromSeries : true
				// }
				//					
				// } ],
				// seriesDefaults: {
				// rendererOptions: {
				// smooth: true
				// }
				// },
				// fillBetween: {
				//
				// series1: 1,
				// series2: 2,
				// color: "rgba(0,150, 188, 1)",
				// baseSeries: 0,
				//  
				// fill: true
				// },
				// grid : {
				// shadow : false,
				// borderWidth : 0.0,
				// background : 'transparent',
				// gridLineColor : prObject["yAxisJson"]["gridline"]["color"],
				// gridLineWidth : prObject["yAxisJson"]["gridline"]["width"]
				// },
				// seriesColors : [ colorCode[0] ],
				// axes : {
				// xaxis : {
				// label : chart["xlegend"],
				// tickOptions : {
				// showGridline : isXGridLineVisible,
				// showLabel : prObject.xAxisJson["visible"],
				// }
				// },
				// yaxis : {
				// label : chart["ylegend"],
				// tickOptions : {
				// showGridline : isYGridLineVisible,
				// showLabel : prObject.yAxisJson["visible"],
				// },
				//
				// }
				// },
				// legend : {
				// show : true,
				// location : 'e'
				// },
				// });
				// $(
				// '#'
				// + idprefix
				// + 'main_preview_chart div.jqplot-table-legend-swatch-outline')
				// .css("color", "#" + colorCode[0]);
				//			
				//			
				// $('#' + idprefix + 'main_preview_chart
				// .jqplot-series-shadowCanvas').css("z-index", "10" );

			}

			else if (chart["type"] == "scatter") {
				$.jqplot(plotId, [ [ 28, 13, 25, 15, 33 ] ], {
					title : {
						show : false
					},
					series : [ {
						pointLabels : {
							show : true,
							location : loc,
							ypadding : 2,
							labelsFromSeries : true
						},
						showLine : false,
						markerOptions : {
							size : 7,
							style : "x"
						}

					} ],

					grid : {
						shadow : false,
						borderWidth : 0.0,
						background : 'transparent',
						gridLineColor : prObject["yAxisJson"]["gridline"]["color"],
						gridLineWidth : prObject["yAxisJson"]["gridline"]["width"]
					},
					seriesColors : [ colorCode[0] ],
					axes : {
						xaxis : {
							label : chart["xlegend"],
							tickOptions : {
								showGridline : isXGridLineVisible,
								showLabel : prObject.xAxisJson["visible"],
							}
						},
						yaxis : {
							label : chart["ylegend"],
							tickOptions : {
								showGridline : isYGridLineVisible,
								showLabel : prObject.yAxisJson["visible"],
							},

						}
					},
					legend : {
						show : true,
						location : 'e'
					},
				});
				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-table-legend-swatch-outline')
						.css("color", "#" + colorCode[0]);

			} else if (chart["type"] == "area") {

				$.jqplot(plotId, [ [ 11, 9, 5, 12, 14 ] ], {
					series : [ {
						showMarker : false,
						fill : true,
						pointLabels : {
							show : true,
							location : loc,
							ypadding : 2,
							labelsFromSeries : true
						}

					} ],
					grid : {
						shadow : false,
						borderWidth : 0.0,
						background : 'transparent',
						gridLineColor : prObject["yAxisJson"]["gridline"]["color"],
						gridLineWidth : prObject["yAxisJson"]["gridline"]["width"]
					},
					seriesColors : [ colorCode[0] ],

					axes : {
						xaxis : {
							label : chart["xlegend"],
							renderer : $.jqplot.CategoryAxisRenderer,
							ticks : ticks,
							tickOptions : {
								showGridline : isXGridLineVisible,
								showLabel : prObject.xAxisJson["visible"],
							}
						},
						yaxis : {
							label : chart["ylegend"],
							tickOptions : {
								showGridline : isYGridLineVisible,
								showLabel : prObject.yAxisJson["visible"],
							}
						}
					},
					legend : {
						show : true,
						location : 'e'
					},
				});

				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-table-legend-swatch-outline')
						.css("color", "#" + colorCode[0]);

			} else if (chart["type"] == "cone" || chart["type"] == "pyramid") {
				var ypad = 2;
				if (loc == 's')
					ypad = 30;
				$.jqplot(plotId, [ [ 0, 11, 0, 0, 9, 0, 0, 5, 0, 0, 12, 0 ] ], {
					series : [ {
						showMarker : false,
						fill : true,
						pointLabels : {
							show : true,
							location : loc,
							ypadding : ypad,
							labels : [ '', '11', '', '', '9', '', '', '5', '', '',
									'12', '' ]
						}

					} ],
					grid : {
						shadow : false,
						borderWidth : 0.0,
						background : 'transparent',
						gridLineColor : prObject["yAxisJson"]["gridline"]["color"],
						gridLineWidth : prObject["yAxisJson"]["gridline"]["width"]
					},
					seriesColors : [ colorCode[0] ],

					axes : {
						xaxis : {
							label : chart["xlegend"],
							renderer : $.jqplot.CategoryAxisRenderer,
							ticks : ticks,
							tickOptions : {
								showGridline : isXGridLineVisible,
								showLabel : prObject.xAxisJson["visible"],
							}
						},
						yaxis : {
							label : chart["ylegend"],
							ticks : [ 0, 5, 10, 15 ],
							tickOptions : {
								showGridline : isYGridLineVisible,
								showLabel : prObject.yAxisJson["visible"],
							}
						}
					},
					legend : {
						show : true,
						location : 'e'
					},
				});

				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-table-legend-swatch-outline')
						.css("color", "#" + colorCode[0]);

			} else if (chart["type"] == "bubble") {

				var arr = [ [ 11, 123, 20, "Log" ], [ 45, 92, 20, "csv" ],
						[ 24, 104, 20, "ppt" ], [ 40, 63, 20, "doc" ], ];

				$.jqplot(plotId, [ arr ], {
					seriesDefaults : {
						renderer : $.jqplot.BubbleRenderer,
						rendererOptions : {
							bubbleAlpha : 0.6,
							highlightAlpha : 0.8,
							autoscaleBubbles : false,
							autoscalePointsFactor : -1.0
						},
						shadow : true,
						shadowAlpha : 0.05
					},
					grid : {
						shadow : false,
						borderWidth : 0.0,
						background : 'transparent',
						gridLineColor : prObject["yAxisJson"]["gridline"]["color"],
						gridLineWidth : prObject["yAxisJson"]["gridline"]["width"]
					},
					// seriesColors : colors,
					seriesColors : [ colorCode[0] ],

					axes : {
						xaxis : {
							label : chart["xlegend"],
							renderer : $.jqplot.CategoryAxisRenderer,
							ticks : ticks,
							tickOptions : {
								showGridline : isXGridLineVisible,
								showLabel : prObject.xAxisJson["visible"],
							}
						},
						yaxis : {
							label : chart["ylegend"],
							tickOptions : {
								showGridline : isYGridLineVisible,
								showLabel : prObject.yAxisJson["visible"],
							}
						}
					},
					legend : {
						show : true,
						location : 'e'
					},
				});

				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-table-legend-swatch-outline')
						.css("color", "#" + colorCode[0]);

			} else if (chart["type"] == 'meter') {

				$.jqplot(plotId, [ [ 1 ] ], {
					seriesDefaults : {
						renderer : $.jqplot.MeterGaugeRenderer,
						rendererOptions : {
							background : 'transparent',
							showTickLabels : prObject["labelJson"]["visible"],
							ringColor : '#00000',
							tickColor : '#0096bc',
							ringWidth : 1.0,
							labelPosition : 'bottom',

						},
						pointLabels : {
							show : false
						}
					},
					legend : {
						show : true,
						location : 'e'
					},
				});

				$(
						'#'
								+ idprefix
								+ 'main_chart_preview_client_area .jqplot-series-canvas')
						.css('background-color',
								'#' + commonJson["clientBackground"]);
				$('#' + idprefix + 'main_preview_chart div.jqplot-meterGauge-tick')
						.css("z-index", "99");
				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-table-legend-swatch-outline')
						.css("color", "#" + colorCode[0]);

			} else if (chart["type"] == "bar" || chart["type"] == "tube") {
				$.jqplot.config.enablePlugins = true;
				var s1 = [ 2, 6, 7, 10 ];
				var ticks = [ 'a', 'b', 'c', 'd' ];

				$.jqplot(plotId, [ s1 ], {

					grid : {
						shadow : false,
						borderWidth : 0.0,
						background : 'transparent',
						gridLineColor : prObject["yAxisJson"]["gridline"]["color"],
						gridLineWidth : prObject["yAxisJson"]["gridline"]["width"]
					},
					seriesColors : [ colorCode[0] ],
					seriesDefaults : {
						renderer : $.jqplot.BarRenderer,
						pointLabels : {
							show : true,
							location : loc
						}
					},
					axes : {
						xaxis : {
							label : chart["xlegend"],
							renderer : $.jqplot.CategoryAxisRenderer,
							ticks : ticks,
							tickOptions : {
								showGridline : isXGridLineVisible,
								showLabel : prObject.xAxisJson["visible"],
							}
						},
						yaxis : {
							label : chart["ylegend"],
							tickOptions : {
								showGridline : isYGridLineVisible,
								showLabel : prObject.yAxisJson["visible"],
							}
						}
					},
					legend : {
						show : true,
						location : 'e'
					},
				});
				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-table-legend-swatch-outline')
						.css("color", "#" + colorCode[0]);

			} else if (chart["type"] == "radar") {
				$('#' + idprefix + 'main_preview_chart').html(
						'<img src="images/radar.png"/ style="height: 170px; background: #'
								+ commonJson["clientBackground"] + ' ">');
				$('#' + idprefix + 'mainlegendTable')
						.html(
								'<tbody><tr class="jqplot-table-legend" style="background-color: transparent;"><td class="jqplot-table-legend jqplot-table-legend-swatch" style="text-align: center; padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;"><div class="jqplot-table-legend-swatch-outline" style="color: rgb(0, 150, 188);"><div class="jqplot-table-legend-swatch"></div></div></td><td class="jqplot-table-legend jqplot-table-legend-label" style="padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;">Series 1</td></tr></tbody>');
			}

			$('#' + idprefix + 'legend-table' + table_count).css('bottom', '0px');
			$('#' + idprefix + 'legend-table' + table_count).css('width', '50%');
			$('#' + idprefix + 'legend-table' + table_count).css('margin-right',
					'50px');

			$('#' + idprefix + 'main_chart_preview_plot_area').css(
					'background-color', '#' + commonJson["plotBackground"]);
			$(
					'#'
							+ idprefix
							+ 'main_chart_preview_client_area .jqplot-series-shadowCanvas')
					.css('background-color', '#' + commonJson["clientBackground"]);

			$('#' + idprefix + 'main_chart_preview_client_area .jqplot-grid-canvas')
					.css('z-index', '19');
			$(
					'#'
							+ idprefix
							+ 'main_chart_preview_client_area .jqplot-series-canvas')
					.css('z-index', '19');

			$('#' + idprefix + 'chartPreviewDiv').css('background-color',
					'#' + commonJson["background"]);
			$('#' + idprefix + 'chartPreviewDiv td.jqplot-table-legend').css(
					'background-color', '#' + commonJson["background"]);
			$('#' + idprefix + 'chartPreviewDiv td.jqplot-table-legend').css(
					'border-radius', '0px');

			if (chart["type"] != "radar" && chart["type"] != "difference"
					&& chart["type"] != "stock" && chart["type"] != "gantt") {
				$('#' + idprefix + 'mainlegendTable')
						.html(
								$(
										'#'
												+ idprefix
												+ 'main_preview_chart table.jqplot-table-legend')
										.html());
				$('#' + idprefix + 'main_preview_chart table.jqplot-table-legend')
						.remove();
			}

			$('#' + idprefix + 'main_chart_preveiw_title').css('font-size',
					prObject.titleJson["font-size"] + 'px');
			$('#' + idprefix + 'main_chart_preveiw_title').css("font-family",
					prObject["titleJson"]["font-family"]);
			$('#' + idprefix + 'main_chart_preveiw_title').css("font-weight",
					prObject["titleJson"]["font-style"]);
			$('#' + idprefix + 'main_chart_preveiw_title').css("text-align",
					prObject["titleJson"]["text-align"]);
			$('#' + idprefix + 'main_chart_preveiw_title').css('background',
					'#' + prObject.titleJson["background"]);
			if (prObject.titleJson["color"] == "")
				$('#' + idprefix + 'main_chart_preveiw_title').css('color',
						'transparent');
			else
				$('#' + idprefix + 'main_chart_preveiw_title').css('color',
						'#' + prObject.titleJson["color"]);
			var outLine = prObject["titleJson"]["outline"];
			if (outLine["visible"]) {
				if (outLine["color"] == "")
					$('#' + idprefix + 'main_chart_preveiw_title').css(
							'border-color', 'transparent');
				else
					$('#' + idprefix + 'main_chart_preveiw_title').css(
							'border-color', '#' + outLine["color"]);
				$('#' + idprefix + 'main_chart_preveiw_title').css("border-style",
						outLine["style"]);
				$('#' + idprefix + 'main_chart_preveiw_title').css("border-width",
						outLine["width"] + "px");
			} else {
				$('#' + idprefix + 'main_chart_preveiw_title').css("border-style",
						"none");
			}
			var insets = prObject["titleJson"]["insets"];
			$('#' + idprefix + 'main_chart_preveiw_title').css("margin-top",
					insets["top"] + "px");
			$('#' + idprefix + 'main_chart_preveiw_title').css("margin-right",
					insets["right"] + "px");
			$('#' + idprefix + 'main_chart_preveiw_title').css("margin-bottom",
					insets["bottom"] + "px");
			$('#' + idprefix + 'main_chart_preveiw_title').css("margin-left",
					insets["left"] + "px");

			if (prObject["outLineJson"]["visible"]) {
				$('#' + idprefix + 'chartPreviewDiv').css("border-style",
						prObject["outLineJson"]["style"]);
				$('#' + idprefix + 'chartPreviewDiv').css("border-color",
						'#' + prObject["outLineJson"]["color"]);
				$('#' + idprefix + 'chartPreviewDiv').css("border-width",
						prObject["outLineJson"]["width"] + "px");
			} else {
				$('#' + idprefix + 'chartPreviewDiv').css("border-style", "none");
			}

			if (!prObject["labelJson"]["visible"]) {
				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
						.remove();
			}
			$(
					'div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
					.css("z-index", "1000000");
			$(
					'div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
					.css("text-align", prObject["labelJson"]["text-align"]);
			if (prObject["labelJson"]["background"] == "")
				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
						.css("background", 'transparent');
			else
				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
						.css("background",
								'#' + prObject["labelJson"]["background"]);
			$(
					'#'
							+ idprefix
							+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
					.css("text-shadow",
							'2px 2px #' + prObject["labelJson"]["shadow"]);
			$(
					'#'
							+ idprefix
							+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
					.css("color", '#' + prObject["labelJson"]["font-color"]);
			$(
					'#'
							+ idprefix
							+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
					.css("font-size", prObject["labelJson"]["font-size"] + 'px');
			$(
					'#'
							+ idprefix
							+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
					.css("font-family", prObject["labelJson"]["font-family"]);
			$(
					'#'
							+ idprefix
							+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
					.css("font-weight", prObject["labelJson"]["font-style"]);
			var outLine = prObject["labelJson"]["outline"];
			if (outLine["visible"]) {
				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
						.css("border-style", outLine["style"]);
				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
						.css("border-color", '#' + outLine["color"]);
				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
						.css("border-width", outLine["width"] + "px");
			}
			var insets = prObject["labelJson"]["insets"];
			$(
					'#'
							+ idprefix
							+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
					.css("margin-top", insets["top"] + "px");
			$(
					'#'
							+ idprefix
							+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
					.css("margin-right", insets["right"] + "px");
			$(
					'#'
							+ idprefix
							+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
					.css("margin-bottom", insets["bottom"] + "px");
			$(
					'#'
							+ idprefix
							+ 'main_preview_chart div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
					.css("margin-left", insets["left"] + "px");

			if (prObject["labelJson"]["values"] == "percent") {
				$(
						'#'
								+ idprefix
								+ 'main_preview_chart div.jqplot-data-label,#main_preview_chart div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick')
						.each(
								function() {
									var temp = $(this).html();
									temp = prObject["labelJson"]["prefix"] + temp
											+ '.' + temp + '%'
											+ prObject["labelJson"]["suffix"];
									$(this).html(temp);
								})
			}

			// y-axis
			var yAxisJson = prObject["yAxisJson"];

			if (yAxisJson["visible"])
				$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
						"display", "");
			else
				$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
						"display", "none");
			$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
					"font-size", yAxisJson["font-size"] + 'px');
			$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
					"font-family", yAxisJson["font-family"]);
			$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
					"font-weight", yAxisJson["font-style"]);
			$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
					"color", '#' + yAxisJson["color"]);
			$('#main_preview_chart div.jqplot-yaxis-tick').css("text-align",
					yAxisJson["text-align"]);
			if (yAxisJson["background"] == "")
				$('#main_preview_chart div.jqplot-yaxis-tick').css("background",
						'transparent');
			else
				$('#main_preview_chart div.jqplot-yaxis-tick').css("background",
						'#' + yAxisJson["background"]);
			$('#main_preview_chart div.jqplot-yaxis-tick').css("text-shadow",
					'2px 2px #' + yAxisJson["shadow"]);
			if (yAxisJson["outline"]["visible"]) {
				$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
						"border-style", yAxisJson["outline"]["style"]);
				if (yAxisJson["outline"]["color"] == "")
					$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick')
							.css("border-color", 'transparent');
				else
					$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick')
							.css("border-color",
									'#' + yAxisJson["outline"]["color"]);
				$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
						"border-width", yAxisJson["outline"]["width"] + "px");
			}
			$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
					"margin-top", yAxisJson["insets"]["top"] + "px");
			$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
					"padding-right", yAxisJson["insets"]["right"] + "px");
			$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
					"margin-bottom", yAxisJson["insets"]["bottom"] + "px");
			$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
					"margin-left", yAxisJson["insets"]["left"] + "px");
			$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
					"z-index", "9999999");
			if (yAxisJson["position"] == "left")
				$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
						"margin-right", "0px");
			else
				$('#' + idprefix + 'main_preview_chart div.jqplot-yaxis-tick').css(
						"margin-right", "-30px");

			// x-axis
			var xAxisJson = prObject["xAxisJson"];
			if (xAxisJson["visible"])
				$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
						"display", "");
			else
				$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
						"display", "none");
			$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
					"font-size", xAxisJson["font-size"] + 'px');
			$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
					"font-family", xAxisJson["font-family"]);
			$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
					"font-weight", xAxisJson["font-style"]);
			$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
					"color", '#' + xAxisJson["color"]);
			$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
					"text-align", xAxisJson["text-align"]);
			if (xAxisJson["background"] == "")
				$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
						"background", 'transparent');
			else
				$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
						"background", '#' + xAxisJson["background"]);
			$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
					"text-shadow", '2px 2px #' + xAxisJson["shadow"]);
			if (xAxisJson["outline"]["visible"]) {
				$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
						"border-style", xAxisJson["outline"]["style"]);
				if (xAxisJson["outline"]["color"] == "")
					$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick')
							.css("border-color", 'transparent');
				else
					$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick')
							.css("border-color",
									'#' + xAxisJson["outline"]["color"]);
				$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
						"border-width", xAxisJson["outline"]["width"] + "px");
			}
			$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
					"padding-top", xAxisJson["insets"]["top"] + "px");
			$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
					"margin-right", xAxisJson["insets"]["right"] + "px");
			$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
					"margin-bottom", xAxisJson["insets"]["bottom"] + "px");
			$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
					"margin-left", xAxisJson["insets"]["left"] + "px");
			$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
					"z-index", "9999999");
			if (xAxisJson["position"] == "below")
				$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
						"margin-top", "0px");
			else
				$('#' + idprefix + 'main_preview_chart div.jqplot-xaxis-tick').css(
						"margin-top", "-30px");

			if (prObject["labelJson"]["position"] == "outside"
					&& chart["type"] == "bubble") {
				$('#' + idprefix + 'main_preview_chart div.jqplot-bubble-label ')
						.each(function() {
							var temp = $(this).css("top");
							temp = temp.substring(0, temp.length - 2) - 30;
							$(this).css("top", temp + "px");
						});
			}

			// legend
			var legendJson = prObject["legendJson"];

			if (legendJson["position"] == "left") {
				$('#' + idprefix + 'main_parent_chart_legend_div').css("float",
						"left");
				$('#' + idprefix + 'main_chart_preview_plot_area').css("float",
						"right");
			} else {
				$('#' + idprefix + 'main_parent_chart_legend_div').css("float",
						"right");
				$('#' + idprefix + 'main_chart_preview_plot_area').css("float",
						"left");
			}

			if (legendJson["anchor"] == "top") {
				$('#' + idprefix + 'main_chart_legend_div').css("margin-top",
						"10px");
			} else if (legendJson["anchor"] == "bottom") {
				$('#' + idprefix + 'main_chart_legend_div').css("margin-top",
						"130px");
			} else {
				$('#' + idprefix + 'main_chart_legend_div').css("margin-top",
						"70px");
			}

			if (legendJson["background"] == "")
				$('#' + idprefix + 'main_parent_chart_legend_div').css(
						"background-color", "transparent");
			else
				$('#' + idprefix + 'main_parent_chart_legend_div').css(
						"background-color", '#' + legendJson["background"]);

			if (legendJson["background"] == "")
				$('#' + idprefix + 'mainlegendTable .jqplot-table-legend').css(
						"background-color", "transparent");
			else
				$('#' + idprefix + 'mainlegendTable .jqplot-table-legend').css(
						"background-color", '#' + legendJson["background"]);
			$('#' + idprefix + 'main_legend_title').text(
					$(line_y_axis_legend).val());
			$('#' + idprefix + 'main_legend_title').css("font-family",
					legendJson["title-font-family"]);
			$('#' + idprefix + 'main_legend_title').css("font-size",
					legendJson["title-font-size"] + "px");
			$('#' + idprefix + 'main_legend_title').css("color",
					"#" + legendJson["title-font-color"]);
			$('#' + idprefix + 'main_legend_title').css("font-weight",
					legendJson["title-font-style"]);

			if (legendJson["visibleTitle"]) {
				$('#' + idprefix + 'main_legend_title').css("display", "");
			} else {
				$('#' + idprefix + 'main_legend_title').css("display", "none");
			}

			if (legendJson["visible"]) {
				$('#' + idprefix + 'main_parent_chart_legend_div').css("display",
						"");
			} else {
				$('#' + idprefix + 'main_parent_chart_legend_div').css("display",
						"none");
			}
			// CHARTPR.setDivOutLine('chart_legend_div',legendJson["outline"]);
			if (prObject.titleJson["anchor"] == "top") {
				$('#' + idprefix + 'main_chart_preveiw_title').css("position", "");
				$('#' + idprefix + 'main_chart_preveiw_title').css("top", "");
				$('#' + idprefix + 'main_parent_chart_legend_div').css(
						"margin-top", "0px");
				$('#' + idprefix + 'main_chart_preview_plot_area').css(
						"margin-top", "0px");
			} else {
				$('#' + idprefix + 'main_chart_preveiw_title').css("position",
						"relative");
				$('#' + idprefix + 'main_chart_preveiw_title').css("top", "88.5%");
				$('#' + idprefix + 'main_parent_chart_legend_div').css(
						"margin-top", "-17px");
				$('#' + idprefix + 'main_chart_preview_plot_area').css(
						"margin-top", "-17px");
			}
			CHARTPR.setDivOutLine(idprefix + 'main_parent_chart_legend_div',
					legendJson["outline"]);
		},
		
		getInitialChartPRObject : function() {

			var obj = null

			if (CM.checkForAdded && CM.queryInfo["chartDetail"] != null
					&& CM.queryInfo["chartDetail"] != undefined
					&& CM.queryInfo["chartDetail"]["chart1"] != undefined) {
				var availabelColumn = this.colList;
				for (var i = 0; i < availabelColumn.length; i++) {
					var colName = availabelColumn[i];
					for ( var chart in CM.queryInfo["chartDetail"]) {

						if (availabelColumn
								.indexOf(CM.queryInfo["chartDetail"][chart]["xseries"]) == -1) {
							delete CM.queryInfo["chartDetail"][chart];
							continue;
						}

						for ( var ycol in CM.queryInfo["chartDetail"][chart]["yseries"]) {
							if (availabelColumn.indexOf(ycol) == -1) {
								delete CM.queryInfo["chartDetail"][chart];
							}

						}
					}
				}
				CM.checkForAdded = false;
				obj = CM.queryInfo["chartDetail"];
				obj["chartPreferences"] = CM.globalChartPreferences
			} else {
				obj = new Object();
				obj["chartPreferences"] = new Object();
				obj["chartPreferences"] = CM.globalChartPreferences
			}
			return obj;

		},
		
		loadChartDesigner : function(type) {
			currentDesignerType = type;
			Util.addLightbox("chartDesigner", "resources/chart_designer.html",
					null, null);
		},
		
		createInitialChartGrid : function() {
			$("#chartDiv").css('display', '');
			$("#deleteChart").removeAttr("disabled");
			$("#addChart").removeAttr("disabled");

			$('#chartContainer').html('<table id="chartTable"></table>');
			jQuery("#chartTable").jqGrid({

				datatype : "local",
				colNames : [ 'ID', 'Chart Title' ],
				colModel : [ {
					name : 'ID',
					index : 'ID',
					// width : ($("#chartContainer").width() - 5),
					hidden : true
				}, {
					name : 'title',
					index : 'title',
					width : ($("#chartContainer").width() - 5),
					sorttype : "text"
				} ],
				height : ($("#chartContainer").height()),
				width : ($("#chartContainer").width() - 2),
				shrinkToFit : false,
				rowNum : length,
				pager : "",
				altRows : true,
				viewrecords : true,
				sortable : false,
				pagination : false,
				caption : "",
				// loadui: "disable",

				gridComplete : function() {
					$("#gview_chartTable .ui-jqgrid-hdiv").css("display", "none")
				},

				onSortCol : function(index, idxcol, sortorder) {
				},

				onSelectRow : function(id) {
					$("#lineSave").html('Save');
					$("#lineAdd").html("Save");
					$("#bubbleSave").html('Save');
					$("#bubbleAdd").html("Save");
					$("#ganttSave").html('Save');
					$("#ganttAdd").html("Save");
					$("#differenceSave").html('Save');
					$("#differenceAdd").html("Save");
					$("#stockSave").html('Save');
					$("#stockAdd").html("Save");
					$("#chartPRButton").css("width", "55px");
				},
			});

		},
		
		createChartGrid : function(value) {
			console.log(value);
			$("#chartDiv").css('display', '');
			$("#deleteChart").removeAttr("disabled");
			$("#addChart").removeAttr("disabled");
			CM.resetChart();

			$('#chartContainer').html('<table id="chartTable"></table>');
			jQuery("#chartTable")
					.jqGrid(
							{

								datatype : "local",
								colNames : [ 'ID', 'Chart Title' ],
								colModel : [ {
									name : 'ID',
									index : 'ID',
									// width : ($("#chartContainer").width() - 5),
									hidden : true
								}, {
									name : 'title',
									index : 'title',
									width : ($("#chartContainer").width() - 7),
									sorttype : "text"
								} ],
								height : ($("#chartContainer").height()),
								width : ($("#chartContainer").width() - 2),
								shrinkToFit : false,
								rowNum : length,
								pager : "",
								altRows : true,
								viewrecords : true,
								sortable : false,
								pagination : false,
								caption : "",
								// loadui: "disable",

								onSortCol : function(index, idxcol, sortorder) {
								},

								gridComplete : function() {
									$("#gview_chartTable .ui-jqgrid-hdiv").css(
											"display", "none")
								},

								beforeSelectRow : function(id, e) {
									if (CM.chartDesignerDirtyBit == true) {
										jQuery.alerts.okButton = ' Yes ';
										jQuery.alerts.cancelButton = ' No';
										jConfirm(
												"Some of the fields of Chart \""
														+ CM.selectedChartId
														+ "\" are modified. Do you want to navigate?",
												'Chart Designer',
												function(val) {
													if (val == true) {
														CM.onChartSelect(id, value);
														CM.chartDesignerDirtyBit = false;
													} else
														return false;
												});
										jQuery.alerts.okButton = ' Ok ';
										jQuery.alerts.cancelButton = ' Cancel';
									} else {
										CM.onChartSelect(id, value);
									}
								},

							// onSelectRow : function(id) {
							// },
							});
			jQuery("#chartTable").jqGrid('navGrid', '', {
				add : false,
				edit : false,
				del : false
			});

			var i = 1;
			var currentChartIndex = 0;
			for ( var chartName in value) {
				if (chartName == "chartPreferences")
					continue;

				var chartObject = value[chartName];
				var cellObj = ({
					"ID" : chartName,
					"title" : chartObject["title"]
				});
				if (CM.currentSelectedChart != null) {
					if (chartObject["title"] == CM.currentSelectedChart) {
						CM.currentSelectedChart = chartObject["title"];
						currentChartIndex = (i - 1);
					}
				}
				jQuery("#chartTable").jqGrid('addRowData', i, cellObj);
				i++;
			}
			$("#chartCount").text(i - 1);

			var selRowId = jQuery("#chartTable").jqGrid('getDataIDs')[currentChartIndex];

			jQuery("#chartTable").jqGrid('setSelection', selRowId, true);
			if (i > 1)
				CM.onChartSelect(selRowId, value);

			$(jQuery("#chartTable")[0].grid.headers[1].el).addClass(
					'ui-state-highlight');

		},

		onChartSelect : function(id, value) {
			var allRowsOnCurrentPage = $('#chartTable').jqGrid('getDataIDs');
			for (var i = 0; i < allRowsOnCurrentPage.length; i++) {
				if (allRowsOnCurrentPage[i] == id)
					$('#' + allRowsOnCurrentPage[i]).css('background-color',
							'#ECECEC');
				else
					$('#' + allRowsOnCurrentPage[i]).css('background-color',
							'white');
			}

			$("#lineSave").html('Update');
			$("#bubbleSave").html('Update');
			$("#stockSave").html('Update');
			$("#differenceSave").html('Update');
			$("#ganttSave").html('Update');

			$("#lineAdd").html("Update");
			$("#bubbleAdd").html("Update");
			$("#differenceAdd").html("Update");
			$("#stockAdd").html("Update");
			$("#ganttAdd").html("Update");

			var rowData = jQuery("#chartTable").jqGrid('getRowData', id); // data
			// of
			// the
			// row
			CM.selectedChartId = rowData.ID;
			$("#chartOptionsDiv").css('display', '');
			$("#deleteChart").removeAttr("disabled");
			var currTitle = CM.selectedChartId;
			$("#chart_type").val(value[currTitle]["type"]);
			RC.setXSeriesTable('line_x_series');
			RC.setXSeriesTable('y_series_grouping');
			$('#y_series_grouping').append($('<option>', {
				value : "none",
				text : "None"
			}));
			$('#y_series_grouping option:contains("None")').prop('selected', true);
			RC.setXSeriesTable('pie_x_series');
			RC.setXSeriesTable('pie_y_series');
			var type = value[currTitle]["type"];
			RC.fillChartDimension(type);
			if ((type == "line") || (type == "bar") || (type == "area")
					|| (type == "scatter") || (type == "tube") || (type == "cone")
					|| (type == "pyramid")) {
				$('#line_y_seriesColFilters').html(
						RC.getYSeriesHtmlData('line', 'series', true, true, false,
								false));
			} else if (type == "meter" || type == "radar" || type == "pie") {
				$('#line_y_seriesColFilters').html(
						RC.getYSeriesHtmlData('line', 'series', true, true, false,
								false));
			} else if ((type == "bubble")) {
				$('#bubble_y_valueColFilters').html(
						RC.getYSeriesHtmlData('bubble', 'value', true, true, false,
								true));
				$('#bubble_y_sizeColFilters').html(
						RC.getYSeriesHtmlData('bubble', 'size', false, true, false,
								true));
			} else if (type == "gantt") {
				$('#gantt_y_labelColFilters').html(
						RC.getYSeriesHtmlData('gantt', 'label', false, true, true,
								true));
				$('#gantt_y_startColFilters').html(
						RC.getYSeriesHtmlData('gantt', 'start', false, false,
								false, true));
				$('#gantt_y_endColFilters').html(
						RC.getYSeriesHtmlData('gantt', 'end', false, false, false,
								true));
			} else if ((type == "difference")) {
				$('#difference_y_positiveColFilters').html(
						RC.getYSeriesHtmlData('difference', 'positive', false,
								true, false, false));
				$('#difference_y_negetiveColFilters').html(
						RC.getYSeriesHtmlData('difference', 'negetive', false,
								true, false, false));
			} else if ((type == "stock")) {
				$('#stock_y_highColFilters').html(
						RC.getYSeriesHtmlData('stock', 'high', true, true, false,
								false));
				$('#stock_y_lowColFilters').html(
						RC.getYSeriesHtmlData('stock', 'low', true, true, false,
								false));
				$('#stock_y_openColFilters').html(
						RC.getYSeriesHtmlData('stock', 'open', true, true, false,
								false));
				$('#stock_y_closeColFilters').html(
						RC.getYSeriesHtmlData('stock', 'close', true, true, false,
								false));
			}
			console.log("value before: ", value);
			CM.fillChart(value);
			console.log("value after: ", value);
			CM.showChartSample('', value[CM.selectedChartId]);
		},
		
		setGlobalChartPreferences : function(object) {
			if (object == null) {
				RemoteManager.saveChartPreferences(JSON
						.stringify(CM.globalChartPreferences),
						CM.handleSavePreferencesResponse);

			} else {
				CM.globalChartPreferences = jQuery.extend({}, object);
			}
		},
		
		handleSavePreferencesResponse : function(dwrResponse) {

		},
		
		closeCloneBox : function() {
			Util.removeLightbox("addclone");

		},


};

function fillPopUp(flag)
{
	var id = CM.selectedChartId;
	
	dwr.util.cloneNode('pop.pattern',{ idSuffix: id});
	dwr.util.byId('pop.pattern'+id).style.display = '';
	dwr.util.byId('popup.image.processing'+id).style.display = '';
	dwr.util.setValue('popup.component','Chart ID');
	dwr.util.setValue('popup.host'+id,id);
	dwr.util.setValue('popup.message'+id,'Processing Request...');
	
	if (CM.isDelete){
		dwr.util.setValue('popup.status'+id,'Deleting..');
	}
	
	var data = CM.queryInfo["chartDetail"];
	
	
	if(CM.isClone)
	{
		dwr.util.setValue('popup.status'+id,'Cloning..');	
		RemoteManager.saveChart($('#chartIdText').val(), CM.selectedQueryId, $('#chartDescText').val(), JSON.stringify(data), CM.chartSaveResponse);
		CM.isClone = false;
	}
	else{
		dwr.util.setValue('popup.status'+id,'Saving..');
		console.log("Hello");
		RemoteManager.saveChart($('#chartIdText').val(), CM.selectedQueryId, $('#chartDescText').val(), JSON.stringify(data), CM.chartSaveResponse);
		console.log("Hello Again");
	}
	
};

function closePopUpBox()
{
    CM.closeBox(true);
}