DA = {
	queryFilterObj:{},
	queryFilterColMap:{},
	checkForAdded : false,
	selectedTableSchema : {},	
	globalChartPreferences : {},
	currentChartDetail : {},
	blockPreviewToShow : false,
	isExecuteAfterSave : false,
	isTableSelectedByUser : true,
	loadFirstTime : true,
	selectedHistoryObj : {},
	isSetQueryRequest : false,
	colMap : {},
	selectedGroupHeaderArray : [],
	groupByRowCount : 0,
	queryInfo : {},
	selectedGroupFooterArray : [],
	selectedWhereArray : [],
	selectedHavingArray : [],
	selectedColumnHeaderArray : [],
	exportReportType : [],
	currentSchedulePage : 1,
	userCache : [],
	status : [],
	searchColumn : [],
	searchFrom : null,
	isColumnSelected : false,
	isFromSelected : false,
	isGroupBySelected : false,
	isHavingSelected : false,
	query : '',
	reportType : '',
	users : '',
	startTime : '',
	endTime : '',
	columnsForCurrentFromSelection : [],
	colList : [],
	selectedQueryId : '',
	selectedChartId : '',
	cloneData : [],
	isSave : false,
	isDelete : false,
	isClone : false,
	currentType : null,
	selectedExportFormat : '',
	chartDesignerDirtyBit : false,
	isFirstTime : false,
	exportQueryId : '',
	tempData : [],
	cntSelection : null,
	selectedNameNode : '',
	selectedDbName : '',
	lastSelectedDbName : '',
	isHive : false,
	currentExecutionId : '',
	lastColumn : '',
	isNewQuery : false,
	emailEnabled : false,
	logEnabled : false,
	tableMap : new Object(),
	currentSelectedChart : null,
	isHistoryFilled : false,
	showCommand : function() {
		if (DA.searchFrom == null) {
			$('#query_textarea').val('');
			return;
		}
		var query = 'SELECT ';
		for ( var i = 0; i < DA.searchColumn.length; i++) {
			if (i != 0) {
				query += ', ';
			}
			query += DA.searchColumn[i];
			if(DA.searchColumn[i].indexOf('(') != -1){
				query += ' AS ';
				var aliasName =  DA.searchColumn[i];
				aliasName = aliasName.replace(' ','_');
				aliasName = aliasName.replace('(','_');
				aliasName = aliasName.replace(')','_');
				query+=aliasName.substring(0,aliasName.lastIndexOf('_')).toLowerCase()+" ";
			}
			
			
		}
		query += ' ';
		query += ' FROM ' + DA.searchFrom;

		if ($('#where_col').val() != "") {

			query += ' WHERE ' + $('#where_col').val();
		}

		if ($('#grp_by_col').val() != "") {
			query += ' GROUP BY ';
			query += $('#grp_by_col').val() + ' ';
		}
		
		if ($('#having_col').val() != "") 
		{
			query += ' HAVING ' + $('#having_col').val();
		}
		
		if ($('#order_by_col').val() != "") {
			query += ' ORDER BY ';
			query += $('#order_by_col').val() + ' ';

		}
		$('#query_textarea').val(query);
		DA.query = query;
		DA.showReportPreview();
	},

	resetQueryInfoJSON : function() {
		
		DA.queryInfo = new Object();
		DA.queryInfo["queryId"] = "";
		DA.queryInfo["queryDesc"] = "";
		DA.queryInfo["sqlQuery"] = "";
		DA.queryInfo["chartDetail"] = DA.getInitialChartPRObject();
		DA.queryInfo["colDetail"] = new Object();
		DA.queryInfo["colHeaderDetail"] = DA.getDefaultHeaderColumnJSON();

		DA.queryInfo["queryHeader"] = new Object();
		DA.queryInfo["queryHeader"]["header"] = new Object();
		DA.queryInfo["queryHeader"]["header"]["title"] = "";

		DA.queryInfo["queryFooter"] = new Object();
		DA.queryInfo["queryFooter"]["footer"] = new Object();
		DA.queryInfo["queryFooter"]["footer"]["title"] = "";

		DA.queryInfo["groupHeader"] = new Object();
		DA.queryInfo["groupFooter"] = new Object();

		DA.queryInfo["selectedColumn"] = new Object();
		DA.queryInfo["selectedTable"] = new Array();
		DA.queryInfo["selectedWhere"] = new Object();
		DA.queryInfo["selectedOrderBy"] = new Array();
		DA.queryInfo["selectedGroupBy"] = new Array();
		DA.queryInfo["selectedHaving"] = new Object();

		DA.queryInfo["setHighFidelityOutput"] = true;
		DA.queryInfo["setLimitResultRows"] = true;
		DA.queryInfo["limitResultRowsValue"] = 300;
		DA.queryInfo["namenode"] = $('#queryIONameNodeId').val();
		DA.queryInfo["dbName"] = "";
		DA.queryInfo["persistResults"] = false;
		
		DA.queryInfo["isFilterQuery"] =  false
		DA.queryInfo["queryFilterDetail"] =  new Object();
		DA.queryInfo["queryFilterDetail"]["filterQuery"] = ''; 
		DA.queryInfo["queryFilterDetail"]["selectedWhere"] = new Object();
		DA.queryInfo["queryFilterDetail"]["selectedTable"] = new Array();
		
	},
	ready : function() {
		Navbar.isDataAvailabe = false;
		DA.checkForAdded = false;
		DA.resetQueryInfoJSON();

		DA.selectedNameNode = $("#queryIONameNodeId").val();
		BQS.selectedNameNode = $("#queryIONameNodeId").val();

		if (Navbar.isEditQuery) {
			if (Navbar.selectedQueryId != null && Navbar.selectedQueryId != '')
				$("#bigQueryIds").val(Navbar.selectedQueryId);
		}
		DA.selectedQueryId = $("#bigQueryIds").val();

		if (Navbar.selectedQueryId == '' || Navbar.selectedQueryId == null
				|| Navbar.selectedQueryId == undefined)
			Navbar.selectedQueryId = DA.selectedQueryId;

		DA.setDBNameForNameNode(DA.selectedNameNode);
		$('#chartPreviewDiv').css('display', 'none');

	},

	resizeGrid : function() {

		$("#chartTable")
				.setGridWidth(($("#chartContainer").width() - 2), false);

	},

	nameNodeChanged : function() {
		if (Navbar.queryManagerDirtyBit == true) {
			jConfirm("Some of the fields of Query \"" + DA.selectedQueryId
					+ "\" are modified. Do you want to navigate?", 'Query',
					function(val) {
						if (val == true) {
							DA.ready();
							DA.setQueryDirtyBitHandlerEvent();
						}
					});
		} else {
			DA.ready();
			DA.setQueryDirtyBitHandlerEvent();
		}
	},

	slide : function() {
		$("#expandQueryID").hide();
		$("#expandQueryDesign").hide();
		$("#expandQueryProperties").hide();
		$("#expandQueryChart").hide();
		$("#expandQueryPreview").hide();
		$("#expandChartDesigner").hide();
		$("#Show").hide();

		$("#Hide").click(function() {
			$("#Hide").hide();
			$("#Show").show();
			// $("#queryHistory").width("3%");
			// $("#data_analyzer_query_builder").width("96%");
			$("#queryHistory").width("30px");
			$("#queryHistory").css('min-width', '30px');
			// $("#data_analyzer_query_builder").css('padding-left','20px');
			$("#data_analyzer_query_builder").width("94%");
			$("#leftTable").css("display", "none");
			DA.resizeGrid();
		});
		$("#Show").click(function() {
			$("#Show").hide();
			$("#Hide").show();
			$("#queryHistory").width("210px");
			$("#queryHistory").css('min-width', '210px');
			// $("#data_analyzer_query_builder").css('margin-left','235px');
			$("#data_analyzer_query_builder").width("auto");
			$("#leftTable").css("display", "block");
			DA.resizeGrid();
		});

		$("#shrinkQueryID").click(function() {
			$("#shrinkQueryID").hide();
			$("#expandQueryID").show();
			$("#divQueryID").css("display", "none");

		});
		$("#expandQueryID").click(function() {
			$("#expandQueryID").hide();
			$("#shrinkQueryID").show();
			$("#divQueryID").css("display", "block");

		});

		$("#shrinkQueryDesign").click(function() {
			$("#shrinkQueryDesign").hide();
			$("#expandQueryDesign").show();
			$("#rightPart").css("display", "none");

		});
		$("#expandQueryDesign").click(function() {
			$("#expandQueryDesign").hide();
			$("#shrinkQueryDesign").show();
			$("#rightPart").css("display", "block");

		});

		$("#shrinkQueryProperties").click(function() {
			$("#shrinkQueryProperties").hide();
			$("#expandQueryProperties").show();
			$("#divQueryProperties").css("display", "none");

		});
		$("#expandQueryProperties").click(function() {
			$("#expandQueryProperties").hide();
			$("#shrinkQueryProperties").show();
			$("#divQueryProperties").css("display", "block");

		});

		$("#shrinkQueryChart").click(function() {
			$("#shrinkQueryChart").hide();
			$("#expandQueryChart").show();
			$("#divQueryChart").css("display", "none");

		});
		$("#expandQueryChart").click(function() {
			$("#expandQueryChart").hide();
			$("#shrinkQueryChart").show();
			$("#divQueryChart").css("display", "block");

		});

		$("#shrinkQueryPreview").click(function() {
			$("#shrinkQueryPreview").hide();
			$("#expandQueryPreview").show();
			$("#divQueryPreview").css("display", "none");

		});
		$("#expandQueryPreview").click(function() {
			$("#expandQueryPreview").hide();
			$("#shrinkQueryPreview").show();
			$("#divQueryPreview").css("display", "block");

		});
		$("#shrinkChartDesigner").click(function() {
			$("#shrinkChartDesigner").hide();
			$("#expandChartDesigner").show();
			$("#divChartDesigner").css("display", "none");

		});
		$("#expandChartDesigner").click(function() {
			$("#expandChartDesigner").hide();
			$("#shrinkChartDesigner").show();
			$("#divChartDesigner").css("display", "block");

		});
	},

	populateNameNodeFromList : function(map) {
		var list = [];
		if (map.ConnectionError == null) {
			DA.tableMap = map;
			for ( var tableName in map) {
				list.push(tableName);
			}
		} else {
			var dbname = DA.selectedDbName;

			jAlert("Could not connect to " + dbname
					+ " database. Please check if database is running.",
					"Error");
			if (DA.selectedDbName != DA.lastSelectedDbName)
				DA.selectedDbName = DA.lastSelectedDbName;
			$('#queryIODatabase').val(DA.selectedDbName);
			return;
		}

		if (list == null || list.length == 0) {

			var dName = DA.selectedDbName;
			if ((dName == "") || (dName == "Not Configured")) {
				jAlert(
						"Current Namespace is not associated with any database. Please configure a database for selected Namespace.",
						"Error");

			} else {
				jAlert("There is no table found in selected " + dName
						+ " database.", "Error");

				if (DA.selectedDbName != DA.lastSelectedDbName)
					DA.selectedDbName = DA.lastSelectedDbName;
				$('#queryIODatabase').val(DA.selectedDbName);
			}

			return;
		}
		Navbar.isDataAvailabe = true;
		var data = '<form name="tables" id="tables">';
		data += '<table><tr><td colspan="2"><span>Search On:</span><span id="selectColClose" class="divcloser"><a href="javascript:DA.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span></td></tr>';
		for ( var i = 0; i < list.length; i++) {
			var tbl_name = list[i];
			if (i == 0) {
				data += '<tr><td><input checked checked="checked" type="checkbox" name="nnID[]" id="filterBy'
						+ tbl_name
						+ '" value="'
						+ tbl_name
						+ '" onclick="DA.setLocationSearch(\''
						+ tbl_name
						+ '\',this.checked);" > </td><td>' + tbl_name + '</td></tr>';
			} else {
				data += '<tr><td><input type="checkbox" name="nnID[]" id="filterBy'
						+ tbl_name
						+ '" value="'
						+ tbl_name
						+ '" onclick="DA.setLocationSearch(\''
						+ tbl_name
						+ '\',this.checked);" > </td><td>' + tbl_name + '</td></tr>';
			}
		}
		data += '</table></form>';
		$('#searchFromFilters').html(data);

		if ((Navbar.isEditQuery || DA.isFirstTime) && (!Navbar.isAddNewQuery)) {
			DA.selectedQueryId = $('#bigQueryIds').val();
			if (Navbar.selectedQueryId == "" || Navbar.selectedQueryId == null) {
				Navbar.selectedQueryId = $('#bigQueryIds').val();
			} else {
				$('#bigQueryIds').val(Navbar.selectedQueryId);
			}
			var userName = Util.getLoggedInUserName();
			if (BQS.selectedNameNode != '' && Navbar.selectedQueryId != '') {
				RemoteManager.getBigQueryInfo(BQS.selectedNameNode,
						Navbar.selectedQueryId,userName, DA.fillSavedQuery);
			}
			return;
		}
		Navbar.isAddNewQuery = false;
		DA.isTableSelectedByUser = false;
		DA.selectTableOperation(list[0]);
		$('#filterBy' + list[0]).attr('checked', 'checked');
	},

	selectChartTask : function() {
		var option = $("#chartAction").val();
		if (option == "Add") {
			$('#defaultImg').css('display', '');
			DA.addNewChart();
		} else if (option == "Delete") {
			var cnt = jQuery("#chartTable").jqGrid('getGridParam', 'records');
			var cntSelection = jQuery("#chartTable").jqGrid('getGridParam',
					'selrow');
			if (cnt == 0) {
				jAlert("No Chart available.", "Error");
			} else if (cntSelection == null) {
				jAlert("No Chart selected.", "Error");
			} else {
				DA.deleteChart();
			}
		} else if (option == "Chart Preferences") {
			RC.chartType = '';
			RC.chartOperation = 'add';
			RC.chartPreferenceType = 'global';
			Util.addLightbox("chart_prefernces",
					"resources/chart_preferences.html", null, null);
		} else if (option == "Clone") {
			if (DA.selectedChartId == null || DA.selectedChartId == undefined
					|| DA.selectedChartId == "") {
				jAlert("Please select a chart before using clone operation.",
						"Error");
				return;
			}
			RC.chartType = '';
			RC.chartOperation = 'clone';
			RC.chartPreferenceType = 'global';
			Util.addLightbox("addclone", "resources/clone_chart.html", null,
					null);
		}
		$("#chartAction").prop('selectedIndex', 0);
		DA.hidePreview();
	},

	hidePreview : function() {
		var cnt = jQuery("#chartTable").jqGrid('getGridParam', 'records');
		var cntSelection = jQuery("#chartTable").jqGrid('getGridParam',
				'selrow');
		if (cnt == 0) {
			$('#chartPreviewDiv').css('display', 'none');
		} else if (cntSelection == null) {
			$('#chartPreviewDiv').css('display', 'none');
		}
	},

	populateSearchColNames : function(tagListObject) {
		
		if(tagListObject == null || tagListObject == undefined){
			jAlert("No column found for selected table.","No column Found");
			return;
		}
		var map = tagListObject["columnMap"];
		var tableSchema = tagListObject["tableSchema"];
		DA.selectedTableSchema = tableSchema;
		if (map == null || map == undefined)
			return;
		var list = new Array();
		
		for ( var attr in map) {
			list.push(attr);
		}

		DA.colList = list;
		DA.colMap = map;
		var obj = new Object();
		var headerObj = new Object();

		if (DA.columnsForCurrentFromSelection.length > 0) {
			DA.columnsForCurrentFromSelection.splice(1,
					DA.columnsForCurrentFromSelection.length);
		}
		var data = '<span id="selectColClose" class="divcloser"><a href="javascript:DA.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span> <table id="columnTable"><tbody><tr><td nowrap="nowrap">Select Column</td><td nowrap="nowrap">Aggregate Function</td></tr>';
		var groupbyOptData = '<span> </span><span id="selectColClose" class="divcloser"><a href="javascript:DA.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span> <br><form name="groupByForm" id="groupByForm" style = "white-space: nowrap;">';
		var havingData = '<span id="selectColClose" class="divcloser"><a href="javascript:DA.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span> <table id="having_tbl"><tbody><tr><td nowrap="nowrap">Select Column</td><td nowrap="nowrap">Relational Operator</td><td nowrap="nowrap">Value</td><td nowrap="nowrap">Logical Operator</td></tr>';
		var colOpt = '';
		var where_data_table = '<span id="selectColClose" class="divcloser"><a href="javascript:DA.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span> <table id="where_tbl"><tbody><tr><td nowrap="nowrap">Select Column</td><td nowrap="nowrap">Relational Operator</td><td nowrap="nowrap">Value</td><td nowrap="nowrap">Logical Operator</td></tr>';

		data += '<tr><td><input checked type="checkbox" name="ALL" id="filterByALL" value="*" onclick="DA.selectColumnOperationWrapper(\'*\', this.checked);" checked="checked" >All</td><td nowrap="nowrap">'
				+ '</td></tr>';

		for ( var i = 0; i < list.length; i++) {
			DA.columnsForCurrentFromSelection.push(list[i]);
			obj[list[i] + ''] = new Object();
			headerObj[list[i] + ''] = new Object();
			data += '<tr><td nowrap="nowrap"><input type="checkbox" name="'
					+ list[i] + '" id="filterBy' + list[i] + '" value="'
					+ list[i] + '" onclick="DA.selectColumnOperationWrapper(\'' + list[i]
					+ '\', this.checked);" > ' + list[i] + '</td>';
			data += '<td nowrap="nowrap">'
					+ DA.getAggregateFunctionDropDown('aggregate_', list[i],
							'DA.selectAggregateFunction(this,\'' + list[i]
									+ '\')') + '</td></tr>';
			groupbyOptData += '<input type="checkbox" name="groupBy' + list[i]
					+ '" id="groupBy' + list[i] + '" value="' + list[i]
					+ '" onclick="DA.setGroupBy(\'' + list[i]
					+ '\', this.checked);" > ' + list[i] + '<br>';
			colOpt += '<option value="' + list[i] + '">' + list[i]
					+ '</option>';
			
			where_data_table += '<tr>';
			where_data_table += '<td nowrap="nowrap"><input type="checkbox" name="'
					+ list[i]
					+ '" id="whereBy'
					+ list[i]
					+ '" value="'
					+ list[i]
					+ '" onclick="DA.setWhereIn(\''
					+ list[i]
					+ '\', this.checked);" > ' + list[i] + '</td>';
			where_data_table += '<td>'
					+ DA.getRelationalOperatorDropDown(list[i],'roperator_','DA.selectRelationalFunction',DA.colMap[list[i]]) + '</td>';
			where_data_table += '<td><input type="text" id="whereval_'
					+ list[i]
					+ '" value="" onblur="javascript:DA.makeWhereCondition();"></td>';
			where_data_table += '<td>' + DA.getLogicalOperatorDropDown(list[i],'loperator_','DA.selectLogicalFunction')
					+ '</td>';
			
			havingData += '<tr>';
			havingData += '<td nowrap="nowrap"><input type="checkbox" name="'
				+ list[i]
			+ '" id="having'
			+ list[i]
			+ '" value="'
			+ list[i]
			+ '" onclick="DA.setHavingIn(\''
			+ list[i]
			+ '\', this.checked);" > ' + list[i] + '</td>';
			havingData += '<td>'
				+ DA.getRelationalOperatorDropDown(list[i],'roperatorHaving_','DA.selectRelationalFunctionForHaving',DA.colMap[list[i]]) + '</td>';
			havingData += '<td><input type="text" id="havingval_'
				+ list[i]
			+ '" value="" onblur="javascript:DA.makeHavingCondition();"></td>';
			havingData += '<td>' + DA.getLogicalOperatorDropDown(list[i],'loperatorHaving_','DA.makeHavingCondition')
			+ '</td>';
			
			if (i == list.length - 1)
				DA.lastColumn = list[i];

		}
		groupbyOptData += '</form>';
		DA.searchColumn.splice(0, DA.searchColumn.length);
		DA.searchColumn = [];
		DA.setColSearch('*', true);
		data += '</tbody></table>';
		where_data_table += '</tbody></table>';

		$('#searchColFilters').html(data);
		$('#whereFilters').html(where_data_table);

		$('#groupByColFilters').html(groupbyOptData);
		$('#grp_by_col').val('');
		$('#having_col').val('');
		
		$('#havingColFilters').html(havingData);
		$('#selectColForProp').html(colOpt);
		$('#selectColHeaderForProp').html(colOpt);
		
		if(DA.isHistoryFilled){
			DA.isHistoryFilled = false;

		}else{
			DA.selectColumnOperationWrapper('*', true);
			
		}

		DA.populateComparisonKeyDropdown(list);
		DA.showCommand();
		DA.queryInfo["colDetail"] = new Object();
		DA.queryInfo["colHeaderDetail"] = DA.getDefaultHeaderColumnJSON();
		DA.queryInfo["groupFooter"] = new Object();
		DA.queryInfo["groupHeader"] = new Object();
		DA.queryInfo["chartDetail"] = DA.getInitialChartPRObject();
		DA.queryInfo["queryHeader"] = new Object();
		DA.queryInfo["queryHeader"]["header"] = new Object();
		DA.queryInfo["queryHeader"]["header"]["title"] = "";

		DA.queryInfo["queryFooter"] = new Object();
		DA.queryInfo["queryFooter"]["footer"] = new Object();
		DA.queryInfo["queryFooter"]["footer"]["title"] = "";

		DA.queryInfo["selectedColumn"] = new Object();
		DA.queryInfo["selectedWhere"] = new Object();
		DA.queryInfo["selectedOrderBy"] = new Array();
		DA.queryInfo["selectedGroupBy"] = new Array();
		DA.queryInfo["selectedHaving"] = new Object();

		DA.showReportPreview();
		RC.ready();

		DA.selectedWhereArray = [];
		$('#where_col').val("");

		if (DA.isSetQueryRequest) {
			DA.isSetQueryRequest = false;
			DA.setQueryBuilderValues();

		}
		if (DA.blockPreviewToShow) {
			DA.blockPreviewToShow = false;
			DA.showReportPreview();
		}
		DA.setQueryDirtyBitHandlerEvent();
	},

	populateComparisonKeyDropdown : function(list) {

		var data = '';
		if (list == null)
			return;

		for ( var i = 0; i < list.length; i++) {
			data += '<option value="' + list[i] + '">' + list[i] + '</option>';
		}
		$('#comparison_col').html(data);
		$('#orderby_col').html(data);
		$('#groupby_col').append(data);
	},

	serachColChanged : function() {
		DA.showCommand();
	},
	searchFromChanged : function() {
		DA.showCommand();
	},
	showColFilters : function(element) {
		$('#searchColFilters').fadeIn('slow');
		$(
				'#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchFromFilters,#groupByColFilters,#havingColFilters,#orderByColFilters,#whereFilters')
				.hide();
		DA.isColumnSelected = true;

	},
	showGroupByCol : function(element) {
		$('#groupByColFilters').fadeIn('slow');
		$(
				'#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters,#havingColFilters,#orderByColFilters,#whereFilters')
				.hide();
		DA.isGroupBySelected = true;

	},
	showHavingCol : function(element) {
		
		if($("#grp_by_col").val() == "")
		{
			jAlert("HAVING clause cannot be set before GROUP BY clause.", "Improper Query");
			return;
		}
			
		$('#havingColFilters').fadeIn('slow');
		$(
				'#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters,#groupByColFilters,#orderByColFilters,#whereFilters')
				.hide();
		DA.isHavingSelected = true;

	},
	showWhereCol : function(element) {
		$('#whereFilters').fadeIn('slow');
		$(
				'#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters,#havingColFilters,#groupByColFilters,#orderByColFilters')
				.hide();
	},
	showOrderByCol : function(element) {
		$('#orderByColFilters').fadeIn('slow');
		$(
				'#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters, #havingColFilters,#groupByColFilters,#whereFilters')
				.hide();

		var theForm = document.getElementById("orderByForm");
		if (theForm == null || theForm == undefined || theForm.elements == null
				|| theForm.elements == undefined)
			return;
		for ( var i = 0; i < theForm.elements.length; i++) {
			var e = theForm.elements[i];
			if (e.type == 'checkbox') {
				for ( var key in DA.queryInfo["selectedOrderBy"]) {
					if (e.value == key) {
						e.checked = true;
						var order = DA.queryInfo["selectedOrderBy"][key];
						$("#order" + e.value).val(order);
					}
				}
			}
		}
	},
	showGroupHeaderByCol : function(element) {
		$('#groupHeaderColFilters').fadeIn('slow');
		$(
				'#columnDetailColFilters,#columnHeaderColFilters,#searchColFilters,#searchFromFilters,#groupByColFilters,#havingColFilters,#orderByColFilters,#whereFilters')
				.hide();
	},
	showColumnHeaderByCol : function(element) {
		$('#columnHeaderColFilters').fadeIn('slow');
		$(
				'#columnDetailColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters,#groupByColFilters,#havingColFilters,#orderByColFilters,#whereFilters')
				.hide();
	},
	showColumnDetailByCol : function(element) {
		$('#columnDetailColFilters').fadeIn('slow');
		$(
				'#columnHeaderColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters,#groupByColFilters,#havingColFilters,#orderByColFilters,#whereFilters')
				.hide();

	},

	showGroupFooterByCol : function(element) {
		$('#groupFooterColFilters').fadeIn('slow');
		$(
				'#columnHeaderColFilters,#columnDetailColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters,#groupByColFilters,#havingColFilters,#orderByColFilters,#whereFilters')
				.hide();
	},
	showLineYSeriesCol : function(id) {
		// $('#line_y_seriesColFilters').show();
		// $('#line_y_seriesColFilters').fadeIn('slow');
	},
	showFromFilters : function(element) {
		$('#searchFromFilters').fadeIn('slow');
		$(
				'#columnHeaderColFilters,#groupHeaderColFilters,#searchColFilters,#columnDetailColFilters,#groupByColFilters,#havingColFilters,#orderByColFilters,#whereFilters')
				.hide();

		DA.isFromSelected = true;

	},

	setGroupBy : function(value, isChecked) {
		var values = [];
		var numberChecked = 0;
		var theForm = document.getElementById("groupByForm");
		if (theForm == null || theForm == undefined || theForm.elements == null
				|| theForm.elements == undefined)
			return;
		for ( var i = 0; i < theForm.elements.length; i++) {
			var e = theForm.elements[i];
			if (e.type == 'checkbox') {
				if (e.checked) {

//					if(DA.searchColumn.indexOf(e.value) == -1){
//						jAlert("The columns in the GROUP BY clause must be appear in the SELECT clause.","Column not selected");
//						e.checked = false
//						return;
//					}
					values.push(e.value);
					numberChecked ++;
				}
			}
		}
		DA.queryInfo["selectedGroupBy"] = values
		$('#grp_by_col').val(values);
		DA.showCommand();
		
		DA.setHaving(numberChecked);
	},
	
	setHaving : function(number) 
	{
		if(number == 0)
		{
			$('#having_tbl').find('input[type=checkbox]:checked').removeAttr('checked');
			$("#having_col").val("");
			DA.showCommand();
		}
	},
	
	isColumnUsedInReport : function(colName) {
		var groupHeader = DA.queryInfo["groupHeader"];
		for ( var col in DA.queryInfo["groupHeader"]) {
			if (colName == col) {
				return true;
			}
		}
		for ( var col in DA.queryInfo["groupFooter"]) {
			if (colName == col) {
				return true;
			}
		}

		for ( var chart in DA.queryInfo["chartDetail"]) {
			if (DA.queryInfo["chartDetail"][chart]["xseries"] == colName) {
				return true;
			}
			if ((DA.queryInfo["chartDetail"][chart]["xseriesSortType"] != "None") && (DA.queryInfo["chartDetail"][chart]["xseriesSortColumn"] == colName)) {
				return true;
			}
			for ( var ycol in DA.queryInfo["chartDetail"][chart]["yseries"]) {
				if (ycol == colName) {
					return true;
				}
			}
		}

		return false;
	},

	removeColumnFromReport : function(colName) {
		var groupHeader = DA.queryInfo["groupHeader"];
		delete DA.queryInfo["groupHeader"][colName];
		delete DA.queryInfo["groupFooter"][colName];
		for ( var chart in DA.queryInfo["chartDetail"]) {
			if (DA.queryInfo["chartDetail"][chart]["xseries"] == colName) {
				delete DA.queryInfo["chartDetail"][chart];
				continue;
			}
			if ((DA.queryInfo["chartDetail"][chart]["xseriesSortType"] != "None") && (DA.queryInfo["chartDetail"][chart]["xseriesSortColumn"] == colName)) {
				delete DA.queryInfo["chartDetail"][chart];
				continue;
			}
			for ( var ycol in DA.queryInfo["chartDetail"][chart]["yseries"]) {
				if (ycol == colName) {
					delete DA.queryInfo["chartDetail"][chart];
				}
			}
		}
		return true;
	},
	selectColumnOperation : function(value, isChecked) {
		DA.enableSelectAggregateFunction(value, isChecked);

		if (!(DA.isSetQueryRequest && isChecked == true)) {
			DA.setSelectedColumn(value, isChecked);
		}

		if (value == "*") { // && DA.searchColumn.length > 0) {
			if (isChecked) {
				DA.searchColumn = [];
				DA.searchColumn.push(value);
				$('#searchColFilters').find('input[type=checkbox]:checked')
						.removeAttr('checked');
				$('#searchColFilters').find('select').attr('disabled',
						'disabled');
				$('#searchColFilters').find('select').prop('selectedIndex', 0);
				$('#srch_col_fld').val("*");
				$('#filterByALL').attr('checked', 'checked');

			}
		} else {
			if (DA.searchColumn.indexOf("*") != -1) {
				if (value != "*") {
					$('#filterByALL').removeAttr('checked');
					DA.searchColumn.splice(DA.searchColumn.indexOf("*"),
							DA.searchColumn.length);
					DA.searchColumn.push(value);
				} else {
					jQuery.inArray(value, DA.searchColumn);
					DA.searchColumn.splice(index, 1);
				}
			} else {
				// if(!document.getElementById("aggregate_"+value).disabled)
				// {
				var aggVal = $("#aggregate_" + value).val();
				if (aggVal != "") {
					if (aggVal.indexOf("DISTINCT") != -1)
						value = aggVal + " " + value + ")";
					else
						value = aggVal + "(" + value + ")";
				}
				// }
				var index = jQuery.inArray(value, DA.searchColumn);
				if (isChecked) {
					if (index == -1) // Not in array
						DA.searchColumn.push(value);
				} else {
					if (index != -1) // Present in array
						DA.searchColumn.splice(index, 1);
				}
			}
		}

		// set Header/footer/order by col according to selected col.
		$('#srch_col_fld').val(DA.searchColumn);
		DA.setOrderByDropDown();
		
		// set col selected in group by.

//		if (isChecked) {
//			$('#groupBy' + value).attr('checked', 'checked');
//		} else {
//			$('#groupBy' + value).removeAttr('checked');
//		}
		//DA.setGroupBy(value, isChecked);

	},
	
	selectColumnOperationWrapper : function(value, isChecked){
		DA.setColSearch(value,isChecked);
	
	
	
//		if(value.indexOf('*') == 0){
//			if(isChecked){
//				for(var i = 0; i< DA.colList.length; i++){
//					$('#filterBy'+DA.colList[i]).attr('checked','checked');
//					DA.setColSearch(DA.colList[i],isChecked);
//				}
//			}else{
//				for(var i = 0; i< DA.colList.length; i++){
//					$('#filterByALL').removeAttr('checked');
//					for(var i = 0; i< DA.colList.length; i++){
//						$('#filterBy'+DA.colList[i]).removeAttr('checked');
//						DA.setColSearch(DA.colList[i],false);
//					}
//				}
//			}
//		}else{
//			
//			
//				DA.setColSearch(value,isChecked);	
//				
////			
//			if($('#filterByALL').is(':checked')){
//				$('#filterByALL').removeAttr('checked');
////				for(var i = 0; i< DA.colList.length; i++){
////					$('#filterBy'+DA.colList[i]).removeAttr('checked');
////					DA.setColSearch(DA.colList[i],false);
////				}
//			}
////			$('#filterBy'+value).attr('checked','checked');
////			DA.setColSearch(value,isChecked);
//		}
		
		

	},
	
	setColSearch : function(value, isChecked) {
		
		var isConfirm = false;
		if (isChecked) {
			isConfirm = true;
			DA.selectColumnOperation(value, isChecked);
		} else {
			var isUsed = DA.isColumnUsedInReport(value);
			if (isUsed) {
				jConfirm(
						"This column is used in report.Removal of this column will remove all the changes related to this column",
						'Confirm', function(val) {
							if (val == true) {
								isConfirm = DA.removeColumnFromReport(value);
								DA.selectColumnOperation(value, isChecked);
							} else {
								$('#filterBy' + value).attr('checked',
										'checked');
								return;
							}
						});
			} else {
				isConfirm = true;
				DA.selectColumnOperation(value, isChecked);
			}
		}
		
		
	
	},

	fetchResultTableName : function(response) {
		if (response == null)
			return;

		if (response.adhoc == false) {								//Comment these 4 lines to hide Result Table
			$('#resultTableNameTD').hide();						//TODO Result Table span removed
			$('#resultTableName').attr('disabled', 'disabled');
		} else {
			$('#resultTableNameTD').show();						//TODO Result Table span removed
			$('#resultTableName').removeAttr('disabled');
		}
		$('#resultTableName').val(response.resultTableName);
		
		DA.queryInfo["resultTableName"] = response.resultTableName; 
		DA.currentExecutionId = response.executionId;
		DA.setResultTableName();
		var value = response.tableName;
		var flag = false;
		if (response.adhoc == true) {

			if ($('#filterBy' + value).is(':checked')) {
				flag = true;
			} else {
				flag = false;
			}
			if (flag) {

				var theForm = document.getElementById("tables");
				if (theForm == null || theForm == undefined
						|| theForm.elements == null
						|| theForm.elements == undefined)
					return;
				for ( var i = 0; i < theForm.elements.length; i++) {
					var e = theForm.elements[i];
					if (e.type == 'checkbox') {
						e.checked = false;
					}
				}
				$('#filterBy' + value).attr('checked', 'checked');
			}

		}
		if (!flag) {

			var theForm = document.getElementById("tables");
			if (theForm == null || theForm == undefined
					|| theForm.elements == null
					|| theForm.elements == undefined)
				return;

			for ( var i = 0; i < theForm.elements.length; i++) {
				var e = theForm.elements[i];
				if (e.type == 'checkbox') {
					var val = e.value + '';
					if (DA.tableMap[val]) {
						e.checked = false;
						continue;
					}
				}
			}
		}
	},
	
	persistClicked : function(checked)
	{
		DA.queryInfo["persistResults"] = checked;
		document.getElementById('resultTableName').disabled= !checked;
	},
	
	selectTableOperation : function(value) {
		var flag = false;
		var values = [];
		if (value == undefined)
			return;

		$('#resultTableName').val(value);
		var nameNodeId = $('#queryIONameNodeId').val();
		
		RemoteManager.getResultTableName(value, nameNodeId, DA.fetchResultTableName);

		if (DA.tableMap[value]) {

			if ($('#filterBy' + value).is(':checked')) {
				flag = true;
			} else {
				flag = false;
			}
			if (flag) {
				// $('#srch_from_fld').width('70%');
				// $('#resultTableNameSpan').show();
				// $('#resultTableName').removeAttr('disabled');

				values.push(value);
				var theForm = document.getElementById("tables");
				if (theForm == null || theForm == undefined
						|| theForm.elements == null
						|| theForm.elements == undefined)
					return;
				for ( var i = 0; i < theForm.elements.length; i++) {
					var e = theForm.elements[i];
					if (e.type == 'checkbox') {
						e.checked = false;
					}
				}

				$('#filterBy' + value).attr('checked', 'checked');
			}

		}
		if (!flag) {

			var theForm = document.getElementById("tables");
			if (theForm == null || theForm == undefined
					|| theForm.elements == null
					|| theForm.elements == undefined)
				return;

			for ( var i = 0; i < theForm.elements.length; i++) {
				var e = theForm.elements[i];
				if (e.type == 'checkbox') {
					var val = (e.value).toUpperCase() + '';
					if (val.indexOf('ADHOC') == 0) {
						e.checked = false;
						continue;
					}
					if (e.checked) {

						values.push(e.value);
					}
				}
			}
			// $('#srch_from_fld').width('100%');
			// $('#resultTableNameSpan').hide();
			// $('#resultTableName').attr('disabled','disabled');
		}
		DA.searchFrom = values;

		
		$('#srch_from_fld').val(values);
		DA.queryInfo["selectedTable"] = DA.searchFrom;
		RemoteManager.getAllAvailableTagsList(DA.selectedNameNode,
				DA.selectedDbName, values, DA.populateSearchColNames);
	},

	setLocationSearch : function(value, isChecked) {
		if (!DA.isTableSelectedByUser) {
			DA.selectTableOperation(value);
			DA.isTableSelectedByUser = true;
			return;
		}
		DA.checkForAdded = true;
		
		if(!isChecked && DA.selectedTableSchema.hasOwnProperty(value)){
			
			var columnList = DA.selectedTableSchema[value];
			var isUsed  = false;
			var columnName = '';
			for(var i = 0; i < columnList.length; i++)
			{
				var isConfirm = false;
				isUsed = DA.isColumnUsedInReport(columnList[i]);
				if(isUsed){
					columnName = columnList[i];
					break;
				}
					
			}
			if (isUsed) {
				jConfirm(
						"Table "+value+" contains column "+columnName+", which is used in report design.Removal of this table will remove all the changes related to this column",
						'Confirm', function(val) {
							if (val == true) {
								Navbar.queryManagerDirtyBit = true;
								DA.selectTableOperation(value);
							} else {
								DA.checkForAdded = false;
								$('#filterBy' + value).attr('checked',
										'checked');
								return;
							}
						});
			}else{
				
				Navbar.queryManagerDirtyBit = true;
				DA.selectTableOperation(value);
			} 
			
		}else{
			Navbar.queryManagerDirtyBit = true;
			DA.selectTableOperation(value);
		}

	},
	SearchReady : function() {
		filterState = false;
		$(
				'#column_detail_col,#columnDetailColFilters,#column_header_col,#columnHeaderColFilters,#group_footer_col,#groupFooterColFilters,#group_header_col,#groupHeaderColFilters, #srch_col_fld, #searchColFilters,#srch_from_fld, #searchFromFilters,#grp_by_col,#groupByColFilters,#havingColFilters,#order_by_col,#orderByColFilters,#where_col,#having_col')
				.hover(function() {
					filterState = true;
				}, function() {
					filterState = false;
				});
		$('html')
				.bind(
						'click',
						function() {
							if (!filterState) {
								$(
										'#columnDetailColFilters,#columnHeaderColFilters,#groupFooterColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters,#groupByColFilters,#havingColFilters,#orderByColFilters')
										.hide();

								if (DA.isColumnSelected) {
									DA.serachColChanged();
									DA.isColumnSelected = false;

								}
								if (DA.isFromSelected) {
									DA.searchFromChanged();
									DA.isFromSelected = false;
								}
							}
						});

	},

	getQueryInfoObject : function() {
		var queryId = $('#queryId').val();
		var queryDesc = $('#queryDesc').val();
		var sqlQuery = $('#query_textarea').val();

		DA.queryInfo["queryId"] = queryId;
		DA.queryInfo["queryDesc"] = queryDesc;
		DA.queryInfo["sqlQuery"] = sqlQuery;
		DA.queryInfo["executionId"] = DA.currentExecutionId;
		DA.queryInfo["setHighFidelityOutput"] = DA.getHighFidelityState();
		DA.queryInfo["setLimitResultRows"] = DA.getLimitResultRowsState();
		DA.queryInfo["limitResultRowsValue"] = $('#limitResultRowsValue').val();

		// DA.queryInfo["queryFooter"]=new Object();
		// DA.queryInfo["queryFooter"]["footer"]=new Object();
		// DA.queryInfo["queryHeader"]=new Object();
		// DA.queryInfo["queryHeader"]["header"]=new Object();

	},

	setHighFidelity : function(isChecked) {
		DA.queryInfo["setHighFidelityOutput"] = isChecked;
	},

	getHighFidelityState : function() {
		return $('#highFidelityOutput').is(':checked');
	},

	runCommand : function() {
		

		if (DA.isNewQuery || DA.isHive) {
			Navbar.queryManagerDirtyBit = true;
		}

		if (Navbar.queryManagerDirtyBit == true) {
			DA.isExecuteAfterSave = true;
			Util.addLightbox('viewerLightBox', 'resources/showProcessing.html', null, null);
			DA.saveQuery();
			
		} else {
			DA.executeCommand();
			
		}

	},
	removeQueryFromCached : function(queryId){
		var userId = $('#loggedInUserId').text();
		var obj =  Util.getCookie("last-visit-query"+userId);
		var idInfoObj = null;
		if(obj != null && obj != undefined){
			var filePathObj = JSON.parse(obj);
			idInfoObj = JSON.parse(Util.getCookie("last-visit-idInfoMap"+userId));
			for (var i in idInfoObj)
			{
	    		if (idInfoObj[i] == queryId)
	    		{ 
	    			
	    			delete filePathObj[i];
				    delete idInfoObj[i];
				    
				    var userId = $('#loggedInUserId').text();
					Util.setCookie("last-visit-query"+userId,JSON.stringify(filePathObj), 15);
					Util.setCookie("last-visit-idInfoMap"+userId,JSON.stringify(idInfoObj), 15);
					break;
	    		}
			}
		}
		
		
	},
	executeCommand : function() {
		DA.removeQueryFromCached($('#queryId').val());
		if (($("#error_msg").text() != '')) {
			jAlert("Error in Chart Designer.", "Error : Charts");
			return;
		}
		DA.getQueryInfoObject();
		DA.query = DA.queryInfo["sqlQuery"];
		DA.queryInfo["namenode"] = $('#queryIONameNodeId').val();
		DA.queryInfo["dbName"] = $('#queryIODatabase').val();

		if (DAT.tempQuery == $('#queryId').val()) {
			Navbar.selectedQueryId = '';
			DA.selectedQueryId = '';
			Navbar.isExecuteQuery = false;
		} else {
			Navbar.selectedQueryId = $('#queryId').val();
			Navbar.isExecuteQuery = true;
		}

		Navbar.changeTab('QueryViewer', 'analytics', 'QueryViewer');
	},

	showEmailLightBox : function() {
		Util.addLightbox("export", "resources/email_big_data.html", null, null);
	},

	showExportLightBox : function() {
		Util.addLightbox("export", "resources/export_big_data.html", null,
						null);
	},

	fillQueryName : function() {
		$('#title').val(DA.exportQueryId);
	},

	emailDataReport : function() {
		document.getElementById('msg_td_2').innerHTML = "";
		var selectedUser = document.getElementById('selected')
				.getElementsByTagName("option");
		var users = [];
		var title = $('#title').val();
		var records = -1;
		if ($('#pages').val() != '*') {
			records = parseInt($('#pages').val());
		}
		for ( var i = 0; i < selectedUser.length; i++) {
			users.push(selectedUser[i].value);
		}
		if (users.length > 0) {
			DA.userCache = [];
			DA.emailStatus('BigQuery Result', users, null, null);

		} else {
			document.getElementById('msg_td_2').innerHTML += "* Users Not Selected<br>";
		}

	},

	backPage : function() {
		$('#emailReportDiv_2').hide();
		$('#emailReportDiv_1').show();

	},
	emailStatus : function(reportType, users, startTime, endTime) {

		DA.reportType = reportType;
		DA.users = users;
		DA.startTime = startTime;
		DA.endTime = endTime;
		// Util.addLightbox("export", "resources/bigdataReportSent.html", null,
		// null);
		$('#export').load("resources/bigdataReportSent.html");
		$("#popup_container").css("z-index", "99999999");

	},
	sendEmail : function(reportType, users, startTime, endTime) {
		DA.findStatus();
		var namenodeId = $('#queryIONameNodeId').val();
		RemoteManager.emailBigDataReport(null, DA.exportReportType, users,
				namenodeId, DA.exportQueryId, DA.emailReturn);

	},
	findStatus : function() {
		this.status = [];
		this.status.push('BigQuery Result');
	},

	emailReturn : function(dwrResponse) {
		if (dwrResponse.taskSuccess) {
			//jAlert(dwrResponse.responseMessage, "Success");
			//DA.closeBox(true)
			$('#okPopUp').prop('disabled', false);			
			$('#processingImg').css('display', 'none')
			$('#successImg').css('display', '');
			$('#popMsg').html(dwrResponse.responseMessage);
			$('#popStatusMsg').html('Success');
			

		} else {
			//jAlert(dwrResponse.responseMessage, "Failed");
			//DA.closeBox(true)
			$('#okPopUp').prop('disabled', false);
			$('#processingImg').css('display', 'none')
			$('#failImg').css('display', '')
			$('#popMsg').html(dwrResponse.responseMessage);
			$('#popStatusMsg').html('Failed');
		}

	},

	closePopUpBox : function()
	{
		DA.closeBox(true);
	},
	
	findreportType : function() {
		var reportType = [];
		reportType.push(7);
		return reportType;
	},
	findExportType : function() {
		var exportFormatList = [];
		if ($('#html').is(':checked'))
			exportFormatList.push(0);
		if ($('#pdf').is(':checked'))
			exportFormatList.push(1);
		if ($('#xls').is(':checked'))
			exportFormatList.push(3);
		return exportFormatList;
	},

	checkQueryEmpty : function() {
		var val = $("#query_textarea").val();
		if (val == "") {
			document.getElementById('executeQuery').disabled = true;
			document.getElementById('saveQuery').disabled = true;
		} else {
			document.getElementById('executeQuery').disabled = false;
			document.getElementById('saveQuery').disabled = false;
		}
	},

	exportData : function() {
		DA.selectedExportFormat = '';
		var namenodeId = $('#queryIONameNodeId').val();

		if ($('#html_radio').is(':checked')) {
			DA.selectedExportFormat = 'HTML';
		} else if ($('#pdf_radio').is(':checked')) {
			DA.selectedExportFormat = 'PDF';
		} else if ($('#xls_radio').is(':checked')) {
			DA.selectedExportFormat = 'XLS';
		} else {
			jAlert("Please select a export type.", "Export Type Not Defined.");
			return;
		}

		$('#generate_reoprt_table input[name="Export"]').prop('disabled', true);
		RemoteManager.exportBigQueryReport(namenodeId, DA.exportQueryId,
				DA.selectedExportFormat, DA.reportGenerated);

	},

	reportGenerated : function(path) {
		var namenodeId = $('#queryIONameNodeId').val();
		var userName = Util.getLoggedInUserName();
		filePath = 'Reports/' + userName + '/' + namenodeId.toLowerCase() + "/" + path;
		if (path != null) {
			if (DA.selectedExportFormat == 'XLS') {
				DA.closeBox(true);
				window.open(filePath, 'Report', 'width:500px;height:500px;');
				return;
			} else if (DA.selectedExportFormat == 'PDF') {
				DA.closeBox(true);
				window.open(filePath, 'Report', 'width:500px;height:500px;');
				return;
			} else if (DA.selectedExportFormat == 'HTML') {
				DA.closeBox(true);
				window.open(filePath, 'Report');
				return;
			}

		} else {
			jAlert('Some Error Occured while generating reporting.', 'Error');
		}
		DA.closeBox(true);

	},

	addClone : function() {
		if (($("#queryIdClone").val() == '')) {
			jAlert("Query ID  is not specified.", "Error : Query Id");
			$("#popup_container").css("z-index", "99999999");
			return;
		}
		DA.cloneData = jQuery.extend(true, {}, DA.queryInfo);
		DA.cloneData["queryId"] = $("#queryIdClone").val();
		DA.cloneData["queryDesc"] = $("#queryDescClone").val();
		
		RemoteManager.isQueryExist($("#queryIdClone").val(),DA.QueryIdexists);	
	},
	
	QueryIdexists : function(response){
		if(response == true){
			jAlert("Query ID already exists. Please provide a unique Query ID","Invalid action");
	        $("#popup_container").css("z-index","9999999");
		}else{
			DA.isClone = true;
			Util.addLightbox('export', 'pages/popup.jsp');
			DA.closeCloneBox();
		}
	},

	closeCloneBox : function() {
		Util.removeLightbox("addclone");

	},

	closeBox : function(isRefresh) {
		Util.removeLightbox("export");
		if ((DA.isSave) || (DA.isDelete)) {
			DA.isSave = false;
			DA.isDelete = false;
			if (isRefresh)
				Navbar.refreshView();
		}
	},

	// createGridTable : function(tabIndex) {
	// return;
	// var colModelArray = [];
	// var array;
	// var colHeaderArray = new Array();
	// if (DA.searchColumn[0] == "*") {
	// array = new Array();
	// // array.push(["FilePath"]);
	// // "+JSON.stringify(DA.columnsForCurrentFromSelection));
	// for ( var i = 0; i < DA.colList.length; i++) {
	// array.push(DA.colList[i]);
	// }
	// } else {
	// array = DA.searchColumn;
	//
	// if (DA.searchColumn.length > 0) {
	// // array.splice(0,0,["FilePath"]);
	// }
	// }
	// // var widthLess;
	// // if (arraylength < )
	// colModelArray.push([ "FilePath" ]);
	// colHeaderArray.push([ "FilePath" ]);
	// for ( var i = 0; i < array.length; i++) {
	// colHeaderArray.push(array[i]);
	// colModelArray.push({
	// name : array[i],
	// index : array[i],
	// sortable : true,
	// resizable : true
	// });
	// }
	//
	// if (colModelArray.length == 0)
	// return;
	//
	// var tableDiv = "#data_analyzer_table_" + tabIndex;
	// var pagerDiv = "#pager_" + tabIndex;
	// var gridContainer = "#jqgrid_container_" + tabIndex;
	//
	// $(tableDiv).remove();
	// $(pagerDiv).remove();
	// $(gridContainer).html(
	// '<table id="data_analyzer_table_' + tabIndex
	// + '"></table><div id="pager_' + tabIndex + '"></div>');
	// jQuery(tableDiv)
	// .jqGrid(
	// {
	// url : 'databrowser.do?query=' + DA.query
	// + '&nodeId=' + DA.searchFrom + '&dirPath=/',
	// datatype : "json",
	// colNames : colHeaderArray,
	// colModel : colModelArray,
	// onSelectRow : function(id) {
	// },
	// rowNum : 100,
	// rowList : [ 100, 200 ],
	// pager : pagerDiv,
	//
	// height : ($(gridContainer).height() - 55),
	// width : ($(gridContainer).width() - 5),
	// shrinkToFit : false,
	// altRows : true,
	// viewrecords : true,
	// sortable : true,
	// multiselect : false,
	// caption : "",
	// onSortCol : function(index, idxcol, sortorder) {
	// // data_analyzer_table");
	// if (this.p.lastsort >= 0
	// && this.p.lastsort !== idxcol
	// && this.p.colModel[this.p.lastsort].sortable !== false) {
	// $(this.grid.headers[this.p.lastsort].el)
	// .find(
	// ">div.ui-jqgrid-sortable>span.s-ico")
	// .show();
	// $(this.grid.headers[this.p.lastsort].el)
	// .removeClass('ui-state-highlight');
	// }
	// $(this.grid.headers[idxcol].el).addClass(
	// 'ui-state-highlight');
	// },
	//
	// }).navGrid(pagerDiv, {
	// add : false,
	// edit : false,
	// del : false,
	// search : false,
	// refresh : false
	// });
	// // $('.ui-pg-input').css('height','20px');
	// // $('.ui-widget-content ui-corner-all').css('font-size','9pt');
	// // $('.ui-jqgrid-bdiv').css('height','100%');
	// // $('.ui-jqgrid-bdiv').css('min-height','510px');
	// // $('#load_data_browser_table').css('display','none');
	// // $('#alertmod').remove();
	// // $('#search_data_browser_table').remove();
	// // $('#pager').css('height','50px');
	//
	// $(jQuery(tableDiv)[0].grid.headers[0].el)
	// .addClass('ui-state-highlight'); // Highlight first column
	// // header on grid load.
	// },

	// setSelectedFileId : function(id) {
	// },
	nextPage : function() {
		var flag = DA.validateEmailpage();
		if (DA.exportReportType.length == 0) {
			document.getElementById('msg_td_1').innerHTML = "Please select a format of reports.";
			return;
		}
		if (flag) {
			document.getElementById('msg_td_2').innerHTML = "";
			$('#emailReportDiv_1').hide();
			$('#emailReportDiv_2').show();
			RemoteManager.getUserDetails(DA.addUser);
		}

	},
	addUser : function(list) {
		if (list != null) {
			if (DA.userCache.length == 0) {
				for ( var i = 0; i < list.length; i++) {
					user = list[i];
					DA.userCache.push(user);
					$('#user').append(
							'<option value="' + user.id + '">' + user.firstName
									+ ' ' + user.lastName + '</option>');
				}
			}
		}

	},

	generateReportReady : function() {
		document.getElementById('pdf_chk').checked = true;
		DA.exportReportType.push('PDF');
	},
	moveAllOptions : function(from, to) {
		var source = document.getElementById(from).getElementsByTagName(
				"option");
		for ( var i = 0; i < source.length; i++) {
			$('#' + to).append(
					'<option value="' + source[i].value + '">'
							+ DA.findUser(source[i].value) + '</option>');
		}
		$('#' + from).children().remove();

	},

	moveSelectedOptions : function(from, to) {
		var source = document.getElementById(from).getElementsByTagName(
				"option");
		for ( var i = 0; i < source.length; i++) {
			if (source[i].selected) {
				$('#' + to).append(
						'<option value="' + source[i].value + '">'
								+ DA.findUser(source[i].value) + '</option>');

			}
		}
		$("#" + from + " option:selected").remove();

	},
	findUser : function(val) {
		for ( var i = 0; i < DA.userCache.length; i++) {
			var user = DA.userCache[i];
			if (user.id == val)
				return user.firstName + ' ' + user.lastName;
		}

	},

	validateSchedule : function() {
		var flag = true;
		switch (DA.currentSchedulePage) {
		case 1: {
			document.getElementById('msg_td_1').innerHTML = "";
			if ($('#pages').val() == '') {
				document.getElementById('msg_td_1').innerHTML = "";
				document.getElementById('msg_td_1').innerHTML += "Please provide no of records for generated report.<br>";
				flag = false;
			}
			var exportFormatList = DA.findExportType();
			if (exportFormatList.length == 0) {
				document.getElementById('msg_td_1').innerHTML = "";
				document.getElementById('msg_td_1').innerHTML += "* Format Not Selected<br>";
				flag = false;
			}
			break;
		}
		case 2: {
			document.getElementById('msg_td_2').innerHTML = "";
			if (document.getElementById('alertRaisedNotificationMessage').value == "") {
				document.getElementById('msg_td_2').innerHTML += "* Message Cannot be Empty";
				flag = false;
			}
			break;
		}
		}
		return flag;
	},
	checkForScheduleID : function() {
		if (document.getElementById('schedID').value != "") {
			RemoteManager.checkBigQueryScheduleId(document
					.getElementById('schedID').value, DA.checkResp);
		} else {
			document.getElementById('msg_td_1').innerHTML = "";
			document.getElementById('msg_td_1').innerHTML += "* Schedule Id Required<br>";
		}
	},
	checkResp : function(flag) {
		if (flag) {
			jAlert("ScheduleId already Taken", "Error");
		} else {
			$('#reportdiv1').hide();
			$('#reportdiv2').show();
			$('#reportdiv3').hide();
		}
		$("#popup_container").css("z-index", "99999999");
	},

	checkNotificationSettings : function(nbean) {
		DA.emailEnabled = nbean.emailEnabled;
		DA.logEnabled = nbean.logEnabled;
	},

	nextScheduleStep : function(selectedDiv) {
		var flag = true;
		if (DA.currentSchedulePage < selectedDiv) {
			if (!DA.emailEnabled && !DA.logEnabled) {
				document.getElementById('msg_td_1').innerHTML = "* You have not configured any notifications. Please configure notifications and return to this wizard. To configure notifications, go to <b>Admin > Notifications</b> tab.";
				return;
			} else if (selectedDiv == 3 && !DA.emailEnabled) {
				document.getElementById('msg_td_2').innerHTML = "* You have not configured Email notification. Please configure Email notification and return to this wizard.";
				return;
			} else {
				flag = DA.validateSchedule();
			}
		}
		if (flag) {
			switch (selectedDiv) {
			case 1: {
				RemoteManager
						.getNotificationSettings(DA.checkNotificationSettings);
				$('#reportdiv1').show();
				$('#reportdiv2').hide();
				$('#reportdiv3').hide();
				break;
			}
			case 2: {
				DA.checkForScheduleID();
				break;
			}
			case 3: {
				RemoteManager.getUserDetails(DA.addUser);
				$('#reportdiv1').hide();
				$('#reportdiv2').hide();
				$('#reportdiv3').show();
				$('input[value="Close"]').hide();
				break;
			}
			}
			DA.currentSchedulePage = selectedDiv;
		}
	},
	emailReady : function() {
		$('#emailReportDiv_1').show();
		$('#emailReportDiv_2').hide();
	},

	validateEmailpage : function() {
		var flag = true;

		return flag;

		document.getElementById('msg_td_1').innerHTML = "";
		var title = document.forms[1].title.value;

		if (title == "") {
			flag = false;
			document.getElementById('msg_td_1').innerHTML += "* Title Not Provided<br>";
		}

		if (DA.findExportType().length == 0) {
			flag = false;
			document.getElementById('msg_td_1').innerHTML += "* Format Not Selected<br>";
		}
		return flag;
	},

	addSchedule : function() {
		DA.findStatus();
		var selectedUser = document.getElementById('selected')
				.getElementsByTagName("option");
		var users = [];

		for ( var i = 0; i < selectedUser.length; i++) {
			users.push(selectedUser[i].value);

		}

		if (users.length > 0) {
			var reportDate = $('#reportDate').val();
			var interval = $('#interval').val();
			RemoteManager.scheduleQueryJob(interval, reportDate, DA
					.findExportType(), document
					.getElementById('notificationType').value, document
					.getElementById('alertRaisedNotificationMessage').value,
					users, document.getElementById('schedID').value,
					BQS.selectedQueryArray, BQS.selectedNameNode,
					DA.scheduleReturn);
		} else {
			document.getElementById('msg_td_3').innerHTML += "* Users Not Selected<br>";
		}

	},
	scheduleReturn : function(flag) {
		if (flag) {
			jAlert('SQL-Query Report scheduled successfully', 'Success', DA
					.closeBox());
		} else {
			jAlert('Some Error Occured', 'Error');
		}
		$("#popup_container").css("z-index", "99999999");
	},
	cloneQuery : function() {

		Navbar.queryManagerDirtyBit = false;
		DA.chartDesignerDirtyBit = false;

		DA.selectedQueryId = DA.queryInfo["queryId"];
		DA.query = DA.queryInfo["sqlQuery"];
		DA.isSave = true;
		DA.isClone = true;

	},
	saveQuery : function() {
			console.log(DA.queryInfo);
		DA.queryInfo["searchColumn"] = DA.searchColumn;
		DA.setResultTableName();
		if (!Navbar.isDataAvailabe) {
			var dName = DA.selectedDbName;
			if (dName == "Not Configured")
				jAlert(
						"Current Namespace is not associated with any database. Please configure a database for selected Namespace.",
						"Error");
			else
				jAlert(
						"There is no table available in selected "
								+ dName
								+ " database.Please upload data from Data Import/Export tab to use Analytics feature.",
						"Error");
			return;
		}
		if (Util.blockSpecialCharButNotSpace($('#queryId').val())) {
			jAlert(
					"Query Id contains special character. Please remove special character from Id.",
					"Invalid Id");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		var names = [];
		$('#bigQueryIds option').each(function() {
			names.push($(this).attr('value'));
		});
		for ( var i = 0; i < names.length; i++) {
			if (names[i] == $("#queryId").val()) {
				if (BQS.isNewQuery) {
					BQS.isNewQuery = false;
					jAlert("Query with Query ID \"" + names[i]
							+ "\" already exists. Please rename the Query ID.",
							"Error : Query Id");
					return;
				} else if (names[i] != $("#bigQueryIds").val()) {
					jAlert("Query with Query ID \"" + names[i]
							+ "\" already exists. Please rename the Query ID.",
							"Error : Query Id");
					return;
				}
			}
		}
		DA.chartDesignerDirtyBit = false;
		if (!DA.isClone)
			DA.getQueryInfoObject();

		if (($("#queryId").val() == '')) {
			jAlert("Query ID  is not specified.", "Error : Query Id");
			return;
		}
		if (($("#query_textarea").val() == '')) {
			jAlert("Query Title is not specified.", "Error : Query Title");
			return;
		}
		if (($("#error_msg").text() != '')) {
			jAlert("Error in Chart Designer.", "Error : Charts");
			return;
		}

		DA.selectedQueryId = DA.queryInfo["queryId"];
		Navbar.selectedQueryId = DA.selectedQueryId;
		DA.query = DA.queryInfo["sqlQuery"];
		DA.isSave = true;
		Navbar.queryManagerDirtyBit = false;
		DAT.tempQuery = '';
		DA.queryInfo["namenode"] = $('#queryIONameNodeId').val();
		DA.queryInfo["colspanDetail"] = DA.getChartColSpanDetail();
		DA.queryInfo["dbName"] = $('#queryIODatabase').val();
		
		var list = new Array();
		
		var checkboxes = $('table#columnTable input[type=checkbox]');
		
		for(var t = 0; t < checkboxes.length; t++){
			var e =checkboxes[t];
			if(e.checked && e.name != 'ALL'){
				list.push(e.name);
				DA.selectAggregateFunction(null,e.name);
			}
		}
		DA.queryInfo["selectedColumnList"] = list;
		
		
		if (DA.isExecuteAfterSave) {
			DA.isExecuteAfterSave = false;
			var str = JSON.stringify(DA.queryInfo);
			RemoteManager.saveBigQuery(DA.selectedNameNode,
					DA.selectedDbName, str, DA.executeCommand);
		} else {
			Util.addLightbox('export', 'pages/popup.jsp');
		}

	},

	querySaveResponse : function(response) {
		var id = DA.selectedQueryId;

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
				$("#gview_chartTable .ui-jqgrid-hdiv").css(
						"display", "none")
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
		$("#chartDiv").css('display', '');
		$("#deleteChart").removeAttr("disabled");
		$("#addChart").removeAttr("disabled");
		DA.resetChart();

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
								if (DA.chartDesignerDirtyBit == true) {
									jQuery.alerts.okButton = ' Yes ';
									jQuery.alerts.cancelButton = ' No';
									jConfirm(
											"Some of the fields of Chart \""
													+ DA.selectedChartId
													+ "\" are modified. Do you want to navigate?",
											'Chart Designer',
											function(val) {
												if (val == true) {
													DA.onChartSelect(id, value);
													DA.chartDesignerDirtyBit = false;
												} else
													return false;
											});
									jQuery.alerts.okButton = ' Ok ';
									jQuery.alerts.cancelButton = ' Cancel';
								} else {
									DA.onChartSelect(id, value);
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
			if(DA.currentSelectedChart != null)
			{
				if(chartObject["title"] == DA.currentSelectedChart)
				{
					DA.currentSelectedChart = chartObject["title"];
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
			DA.onChartSelect(selRowId, value);

		$(jQuery("#chartTable")[0].grid.headers[1].el).addClass(
				'ui-state-highlight');

	},

	onChartSelect : function(id, value) {
		var allRowsOnCurrentPage = $('#chartTable').jqGrid('getDataIDs');
		for ( var i = 0; i < allRowsOnCurrentPage.length; i++) {
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
		DA.selectedChartId = rowData.ID;
		$("#chartOptionsDiv").css('display', '');
		$("#deleteChart").removeAttr("disabled");
		var currTitle = DA.selectedChartId;
		$("#chart_type").val(value[currTitle]["type"]);
		RC.setXSeriesTable('line_x_series');
		RC.setXSeriesTable('y_series_grouping');
		 $('#y_series_grouping').append($('<option>', { 
		        value: "none",
		        text : "None" 
		    }));
		 $('#y_series_grouping option:contains("None")').prop('selected', true);
		RC.setXSeriesTable('pie_x_series');
		RC.setXSeriesTable('pie_y_series');
		var type = value[currTitle]["type"];
		RC.fillChartDimension(type);
		if ((type == "line") || (type == "bar") || (type == "area") || (type == "scatter") || (type =="tube") || (type =="cone") || (type =="pyramid") ) 
		{
			$('#line_y_seriesColFilters').html(RC.getYSeriesHtmlData('line','series',true, true, false, false));
		}
		else if(type == "meter" || type == "radar" || type == "pie") 
		{
			$('#line_y_seriesColFilters').html(RC.getYSeriesHtmlData('line','series',true, true, false, false));
		}
		else if ((type == "bubble"))
		{
			$('#bubble_y_valueColFilters').html(RC.getYSeriesHtmlData('bubble','value',true, true, false, true));
   			$('#bubble_y_sizeColFilters').html(RC.getYSeriesHtmlData('bubble','size',false, true, false, true));
		}
		else if (type=="gantt"){
			$('#gantt_y_labelColFilters').html(RC.getYSeriesHtmlData('gantt','label',false, true, true, true));
   			$('#gantt_y_startColFilters').html(RC.getYSeriesHtmlData('gantt','start',false, false, false, true));
   			$('#gantt_y_endColFilters').html(RC.getYSeriesHtmlData('gantt','end',false, false, false, true));
		}
		else if ((type =="difference")) 
		{
			$('#difference_y_positiveColFilters').html(RC.getYSeriesHtmlData('difference','positive',false, true, false, false));
   			$('#difference_y_negetiveColFilters').html(RC.getYSeriesHtmlData('difference','negetive',false, true, false, false));
		}
		else if ((type =="stock"))
		{
			$('#stock_y_highColFilters').html(RC.getYSeriesHtmlData('stock','high',true,true, false, false));
   			$('#stock_y_lowColFilters').html(RC.getYSeriesHtmlData('stock','low',true, true, false, false));
   			$('#stock_y_openColFilters').html(RC.getYSeriesHtmlData('stock','open',true, true, false, false));
   			$('#stock_y_closeColFilters').html(RC.getYSeriesHtmlData('stock','close',true, true, false, false));
		}
		DA.fillChart(value);
		DA.showChartSample('', value[DA.selectedChartId]);
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
		$('#bubble_chart_table').css("display","none");
		$('#stock_chart_table').css("display","none");
		$('#difference_chart_table').css("display","none");
		$('#gantt_chart_table').css("display","none");
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
		var currTitle = DA.selectedChartId;
		$("#chart_type").val(value[currTitle]["type"]);
		$("#chart_position").val(value[currTitle]["position"]);
		$("#chart_height").val(value[currTitle]["height"]);
		$("#chart_width").val(value[currTitle]["width"]);
		$("#row_position").val(value[currTitle]["rowPosition"]);
		$("#col_span").val(value[currTitle]["colSpan"]);
		$("#line_chart_title").val(value[currTitle]["title"]);
		$("#chart_dimension").val(value[currTitle]["dimension"]);
		$('#chartPRButton').removeAttr("disabled");

		DA.currentSelectedChart = value[currTitle]["title"];
		
		RC.sortType = value[currTitle]["xseriesSortType"];
		
		var type = value[currTitle]["type"];
		if ((type == "line") || (type == "bar") || (type == "pie") || (type == "area") || (type == "scatter")|| (type =="meter") || (type =="tube") || (type =="cone") || (type =="pyramid") || (type =="radar")) 
		{
			// $('#pie_chart_table').css("display", "none");
			$('#line_chart_table').css("display", "");
			
			$('#pie_chart_table').css("display","none");
   			$('#bubble_chart_table').css("display","none");
   			$('#stock_chart_table').css("display","none");
   			$('#difference_chart_table').css("display","none");
   			$('#gantt_chart_table').css("display","none");
			
			$("#line_y_series").val(value[currTitle]["yseriesArray"]);
			$("#line_x_axis_legend").val(value[currTitle]["xlegend"]);
			$("#line_y_axis_legend").val(value[currTitle]["ylegend"]);

			
			$("#line_x_series").val(value[currTitle]["xseries"]);
			$("#y_scale_min_val").val(value[currTitle]["yScaleMinVal"]);
			if(value[currTitle]["xseries"] == 'Linear'){
				$('#y_scale_min_val_row').show();
			}
			else{
				$('#y_scale_min_val_row').hide();	
			}
			$("#line_x_series_scale").val(value[currTitle]["chartscale"]);
			if(value[currTitle]["chartscale"] == 'Linear')
			{
				$('#y_scale_min_val_row').show();
			}else{
				$('#y_scale_min_val_row').hide();
			}
				
			$("#y_series_grouping").val(value[currTitle]["ygrouping"]);
			$("#line_x_series_sort_type").val(value[currTitle]["xseriesSortType"]);
			if (value[currTitle]["xseriesSortType"] != "None") {
				$("#line_x_series_sort_column").removeAttr("disabled");
				$("#line_x_series_sort_column").val(value[currTitle]["xseriesSortColumn"]);
			}
			RC.ySeriesHistory['lineseries'] = jQuery.extend(true, {},
					value[currTitle]["yseries"]);
			RC.selectedChartYObject['lineseries'] = jQuery.extend(true, {},
					value[currTitle]["yseries"]);
			RC.selectedChartYArray['lineseries'] = value[currTitle]["yseriesArray"];
			RC.issetTimeOut = true;
			RC.setYSeriesHistoryObj('line','series');

			// } else if (type == "pie") {
			// $('#pie_chart_table').css("display", "");
			// $('#line_chart_table').css("display", "none");
			// $("#pie_x_series").val(value[currTitle]["xseries"]);
			// $("#pie_y_series").val(value[currTitle]["yseries"][0]);
		} else if ((type == "bubble"))
		{
			$('#bubble_chart_table').css("display","");
			$('#line_chart_table').css("display","none");
			
			$('#pie_chart_table').css("display","none");
   			$('#stock_chart_table').css("display","none");
   			$('#difference_chart_table').css("display","none");
   			$('#gantt_chart_table').css("display","none");
			
			$("#bubble_x_series").val(value[currTitle]["xseries"]);
			$("#bubble_y_series_grouping").val(value[currTitle]["ygrouping"]);
			$("#bubble_x_series_sort_type").val(value[currTitle]["xseriesSortType"]);
			if (value[currTitle]["xseriesSortType"] != "None") {
				$("#bubble_x_series_sort_column").removeAttr("disabled");
				$("#bubble_x_series_sort_column").val(value[currTitle]["xseriesSortColumn"]);
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
			RC.setYSeriesHistoryObj('bubble','value');
			
			RC.ySeriesHistory['bubblesize'] = jQuery.extend(true, {},
					value[currTitle]["yseriesSize"]);
			RC.selectedChartYObject['bubblesize'] = jQuery.extend(true, {},
					value[currTitle]["yseriesSize"]);
			RC.selectedChartYArray['bubblesize'] = value[currTitle]["yseriesSizeArray"];
			RC.issetTimeOut = true;
			RC.setYSeriesHistoryObj('bubble','size');

		} else if ((type =="stock"))
		{
			$('#stock_chart_table').css("display","");
			$('#line_chart_table').css("display","none");
			
			$('#pie_chart_table').css("display","none");
   			$('#bubble_chart_table').css("display","none");
   			$('#difference_chart_table').css("display","none");
   			$('#gantt_chart_table').css("display","none");
			
			$("#stock_x_series").val(value[currTitle]["xseries"]);
			$("#stock_y_series_grouping").val(value[currTitle]["ygrouping"]);
			
			$("#stock_x_series_sort_type").val(value[currTitle]["xseriesSortType"]);
			
			if (value[currTitle]["xseriesSortType"] != "None") {
				$("#stock_x_series_sort_column").removeAttr("disabled");
				$("#stock_x_series_sort_column").val(value[currTitle]["xseriesSortColumn"]);
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
			RC.setYSeriesHistoryObj('stock','high');
			
			RC.ySeriesHistory['stocklow'] = jQuery.extend(true, {},
					value[currTitle]["yseriesLow"]);
			RC.selectedChartYObject['stocklow'] = jQuery.extend(true, {},
					value[currTitle]["yseriesLow"]);
			RC.selectedChartYArray['stocklow'] = value[currTitle]["yseriesLowArray"];
			RC.issetTimeOut = true;
			RC.setYSeriesHistoryObj('stock','low');
			
			RC.ySeriesHistory['stockopen'] = jQuery.extend(true, {},
					value[currTitle]["yseriesOpen"]);
			RC.selectedChartYObject['stockopen'] = jQuery.extend(true, {},
					value[currTitle]["yseriesOpen"]);
			RC.selectedChartYArray['stockopen'] = value[currTitle]["yseriesOpenArray"];
			RC.issetTimeOut = true;
			RC.setYSeriesHistoryObj('stock','open');
			
			RC.ySeriesHistory['stockclose'] = jQuery.extend(true, {},
					value[currTitle]["yseriesClose"]);
			RC.selectedChartYObject['stockclose'] = jQuery.extend(true, {},
					value[currTitle]["yseriesClose"]);
			RC.selectedChartYArray['stockclose'] = value[currTitle]["yseriesCloseArray"];
			RC.issetTimeOut = true;
			RC.setYSeriesHistoryObj('stock','close');
			


		} else if ((type =="difference")) 
		{
			$('#difference_chart_table').css("display","");
			$('#line_chart_table').css("display","none");
			
			$('#pie_chart_table').css("display","none");
   			$('#bubble_chart_table').css("display","none");
   			$('#stock_chart_table').css("display","none");
   			$('#gantt_chart_table').css("display","none");
			
			$("#difference_x_series").val(value[currTitle]["xseries"]);
			$("#difference_y_series_grouping").val(value[currTitle]["ygrouping"]);
			
			$("#difference_x_series_sort_type").val(value[currTitle]["xseriesSortType"]);
			if (value[currTitle]["xseriesSortType"] != "None") {
				$("#difference_x_series_sort_column").removeAttr("disabled");
				$("#difference_x_series_sort_column").val(value[currTitle]["xseriesSortColumn"]);
			}
			$("#difference_x_axis_legend").val(value[currTitle]["xlegend"]);
			$("#difference_y_axis_legend").val(value[currTitle]["ylegend"]);
			
			$("#difference_y_positive").val(value[currTitle]["yseriesPositiveArray"]);
			$("#difference_y_negetive").val(value[currTitle]["yseriesNegativeArray"]);
			
			RC.ySeriesHistory['differencepositive'] = jQuery.extend(true, {},
					value[currTitle]["yseriesPositive"]);
			RC.selectedChartYObject['differencepositive'] = jQuery.extend(true, {},
					value[currTitle]["yseriesPositive"]);
			RC.selectedChartYArray['differencepositive'] = value[currTitle]["yseriesPositiveArray"];
			RC.issetTimeOut = true;
			RC.setYSeriesHistoryObj('difference','positive');
			
			RC.ySeriesHistory['differencenegetive'] = jQuery.extend(true, {},
					value[currTitle]["yseriesNegative"]);
			RC.selectedChartYObject['differencenegetive'] = jQuery.extend(true, {},
					value[currTitle]["yseriesNegative"]);
			RC.selectedChartYArray['differencenegetive'] = value[currTitle]["yseriesNegativeArray"];
			RC.issetTimeOut = true;
			RC.setYSeriesHistoryObj('difference','negetive');

		} else if ((type =="gantt"))
		{
			$('#gantt_chart_table').css("display","");
			$('#line_chart_table').css("display","none");
			
			$('#pie_chart_table').css("display","none");
   			$('#bubble_chart_table').css("display","none");
   			$('#stock_chart_table').css("display","none");
   			$('#difference_chart_table').css("display","none");
			
			$("#gantt_x_series").val(value[currTitle]["xseries"]);
			$("#gantt_x_series_sort_type").val(value[currTitle]["xseriesSortType"]);
			if (value[currTitle]["xseriesSortType"] != "None") {
				$("#gantt_x_series_sort_column").removeAttr("disabled");
				$("#gantt_x_series_sort_column").val(value[currTitle]["xseriesSortColumn"]);
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
			RC.setYSeriesHistoryObj('gantt','label');

			RC.ySeriesHistory['ganttstart'] = jQuery.extend(true, {},
					value[currTitle]["yseriesStart"]);
			RC.selectedChartYObject['ganttstart'] = jQuery.extend(true, {},
					value[currTitle]["yseriesStart"]);
			RC.selectedChartYArray['ganttstart'] = value[currTitle]["yseriesStartArray"];
			RC.issetTimeOut = true;
			RC.setYSeriesHistoryObj('gantt','start');
			
			RC.ySeriesHistory['ganttend'] = jQuery.extend(true, {},
					value[currTitle]["yseriesEnd"]);
			RC.selectedChartYObject['ganttend'] = jQuery.extend(true, {},
					value[currTitle]["yseriesEnd"]);
			RC.selectedChartYArray['ganttend'] = value[currTitle]["yseriesEndArray"];
			RC.issetTimeOut = true;
			RC.setYSeriesHistoryObj('gantt','end');
			
		} else	
		{
			$('#pie_chart_table').css("display", "none");
			$('#line_chart_table').css("display", "none");
   			$('#bubble_chart_table').css("display","none");
   			$('#stock_chart_table').css("display","none");
   			$('#difference_chart_table').css("display","none");
   			$('#gantt_chart_table').css("display","none");
		}

	},

	addNewChart : function() {
		if (DA.chartDesignerDirtyBit == true) {
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton = ' No';
			jConfirm(
					"Some of the fields of Chart \"" + DA.selectedChartId
							+ "\" are modified. Do you want to navigate?",
					'Chart Designer',
					function(val) {
						if (val == true) {
							var allRowsOnCurrentPage = $('#chartTable').jqGrid(
									'getDataIDs');
							for ( var i = 0; i < allRowsOnCurrentPage.length; i++)
								$('#' + allRowsOnCurrentPage[i]).css(
										'background-color', 'white');
							RC.ready();
							DA.chartDesignerDirtyBit = false;
							DA.resetChart();
							$("#lineSave").html("Add");
							$("#lineAdd").html("Add");
							$("#bubbleSave").html("Add");
							$("#bubbleAdd").html("Add");
							$("#ganttSave").html("Add");
							$("#ganttAdd").html("Add");
							$("#stockSave").html("Add");
							$("#stockAdd").html("Add");
							$("#differenceSave").html("Add");
							$("#differenceAdd").html("Add");
							
							
							$("#chartOptionsDiv").css('display', '');
							$("#lineAdd").css('display', '');
							$("#lineSave").css('display', 'none');
							$("#bubbleAdd").css('display', '');
							$("#bubbleSave").css('display', 'none');
							$("#stockAdd").css('display', '');
							$("#stockSave").css('display', 'none');
							$("#ganttAdd").css('display', '');
							$("#ganttSave").css('display', 'none');
							$("#differenceAdd").css('display', '');
							$("#differenceSave").css('display', 'none');
							
							$("#pieAdd").css('display', '');
							$("#pieSave").css('display', 'none');
						}
					});
			jQuery.alerts.okButton = ' Ok ';
			jQuery.alerts.cancelButton = ' Cancel';
		} else {
			var allRowsOnCurrentPage = $('#chartTable').jqGrid('getDataIDs');
			for ( var i = 0; i < allRowsOnCurrentPage.length; i++)
				$('#' + allRowsOnCurrentPage[i]).css('background-color',
						'white');
			RC.ready();
			DA.chartDesignerDirtyBit = false;
			DA.resetChart();
			$("#lineSave").html("Add");
			$("#lineAdd").html("Add");
			$("#bubbleSave").html("Add");
			$("#bubbleAdd").html("Add");			
			$("#ganttSave").html("Add");
			$("#ganttAdd").html("Add");
			$("#stockSave").html("Add");
			$("#stockAdd").html("Add");
			$("#differenceSave").html("Add");
			$("#differenceAdd").html("Add");
			
			
			$("#chartOptionsDiv").css('display', '');
			$("#lineAdd").css('display', '');
			$("#lineSave").css('display', 'none');
			$("#bubbleAdd").css('display', '');
			$("#bubbleSave").css('display', 'none');
			$("#ganttAdd").css('display', '');
			$("#ganttSave").css('display', 'none');
			$("#stockAdd").css('display', '');
			$("#stockSave").css('display', 'none');
			$("#differenceAdd").css('display', '');
			$("#differenceSave").css('display', 'none');
			
			// $("#pieAdd").css('display', '');
			// $("#pieSave").css('display', 'none');

		}

	},

	deleteChart : function() {
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton = ' No';
		jConfirm('Are you sure you want to delete this chart ?',
				'Delete Chart', function(val) {
					if (val == true) {
						DA.resetChart();
						DA.currentSelectedChart = null;
						RC.deleteChart(DA.selectedChartId);
						DA.createChartGrid(DA.queryInfo["chartDetail"]);
						DA.showReportPreview();
						DA.hidePreview();
					} else {
						return;
					}
					jQuery.alerts.okButton = ' Ok ';
					jQuery.alerts.cancelButton = ' Cancel';
				});
		$("#popup_container").css("z-index", "99999999");

	},

	fillSavedQuery : function(historyObj) {
		if (historyObj == null || historyObj == undefined)
			return;
		
		DA.isHistoryFilled = true;
		DA.blockPreviewToShow = true;
		$("#error_msg").text("");
		DA.queryInfo = jQuery.extend(true, {}, historyObj);

		$("#queryIODatabase").val(historyObj["dbName"]);
		
		DA.setHiveFlag(DA.selectedNameNode, historyObj["dbName"]);

		DA.selectedDbName = $("#queryIODatabase").val();
		
		if (DA.lastSelectedDbName != DA.selectedDbName) {
			DA.lastSelectedDbName = DA.selectedDbName;
			RemoteManager.getAllTagTableNames(DA.selectedNameNode,
					DA.selectedDbName, DA.onlyPopulateTableNameDiv);

		}

		$("#queryId").val(historyObj["queryId"]);
		$("#queryIdTitle").text(historyObj["queryId"]);
		$("#design_link").text("Design: " + $("#queryId").val());
		$("#preview_span").text("Preview: " + $("#queryId").val());
		$("#queryDesc").val(historyObj["queryDesc"]);
		$("#query_textarea").val(historyObj["sqlQuery"]);
		
		$('#srch_col_fld').val(historyObj["searchColumn"]);
		
		var isFilterQuery = historyObj["isFilterQuery"];
		
		if(isFilterQuery){
		
			DA.setQueryFilterDetail(historyObj["queryFilterDetail"], historyObj["selectedTable"][0]);
			
		}
		

		if (DA.queryInfo["setLimitResultRows"]) {
			$('#limitResultRows').attr('checked', 'checked');
			$('#limitResultRowsValue')
					.val(DA.queryInfo["limitResultRowsValue"]);
			$('#limitResultRowsValue').css('display', '');
		} else {
			$('#limitResultRows').removeAttr('checked');
			$('#limitResultRowsValue').css('display', 'none');
		}
		if (DA.queryInfo["setHighFidelityOutput"]) {
			$('#highFidelityOutput').attr('checked', 'checked');
		} else {
			$('#highFidelityOutput').removeAttr('checked');
		}

		$('#searchFromFilters').find('input[type=checkbox]:checked')
				.removeAttr('checked');

		DA.isSetQueryRequest = true;

		var selectedTableObj = historyObj["selectedTable"];

		for ( var i = 0; i < selectedTableObj.length; i++) {
			var value = selectedTableObj[i] + '';
			// if (value.indexOf('ADHOC') == 0){
			// $('#srch_from_fld').width('70%');
			// $('#resultTableNameSpan').show();
			// $('#resultTableName').removeAttr('disabled');
			// }

			$('#filterBy' + selectedTableObj[i]).attr("checked", true);
		}

		DA.searchFrom = selectedTableObj;

		$('#srch_from_fld').val(selectedTableObj);
		

		RemoteManager.getAllAvailableTagsList(DA.selectedNameNode,
				DA.selectedDbName, selectedTableObj, DA.populateSearchColNames);
		
		RemoteManager.getResultTableName($('#srch_from_fld').val(), DA.selectedNameNode,
				DA.fetchResultTableName);

		DA.selectedHistoryObj = jQuery.extend(true, {}, historyObj);
		BQS.isEditQuery = false;
		
		
		
	},
	
	setHiveFlag : function(nameNodeId, dbName)
	{
		RemoteManager.getAllDBNameWithTypeForNameNodeMapping(nameNodeId, 
			function(list)
			{
				if(list["Metastore"] == dbName)
					DA.isHive = false;
				else if(list["Hive"] == dbName)
					DA.isHive = true;
				if(DA.isHive)
				{
					$('#query_filter_table').css('visibility','visible');
				}
				else
				{
					$('#query_filter_table').css('visibility','hidden');
					$('#is_apply_query_filter').removeAttr('checked')
					$('#query_filter_sql').css('visibility','hidden');
					DA.applyQueryFilter();
				}
			}
		);
	},
	
	adjustGridHeight : function() {
		var topHeight = $("#data_analyzer_query_builder").height();
		$("#queryHistoryTable").setGridHeight(
				$("#table_container").height() - 10, false);
	},
	showReportPreview : function() {

		if (DA.blockPreviewToShow)
			return;

		// show preview in header section.
		DA.setColSectionPreview("colHeaderDetail");
		DA.setColSectionPreview("colDetail");
		DA.setReportHeaderPreview("queryFooter");
		DA.setReportHeaderPreview("queryHeader");
		DA.setGroupSectionPreview("groupHeader");
		DA.setGroupSectionPreview("groupFooter");
		DA.createChartGrid(DA.queryInfo["chartDetail"]);

	},
	drawAllChart : function() {

	},

	getAggregateFunctionDropDown : function(idprefix, colName, onChageFunc) {

		if (DA.colList.indexOf(colName) == -1 && colName != '*') {
			return "";
		}

		var dataType = DA.colMap[colName];
		var data = '<select id="'
				+ idprefix
				+ colName
				+ '" disabled="disabled" style="width:100%;" onchange="javascript:'
				+ onChageFunc + ';">';
		data += '<option value=""></option>';
		data += '<option value="COUNT">COUNT</option>';
		data += '<option value="COUNT(DISTINCT">DISTINCT COUNT</option>';
		if (dataType.toUpperCase() == 'INTEGER' || dataType.toUpperCase() == 'LONG'
				|| dataType.toUpperCase() == 'DECIMAL' || dataType.toUpperCase() == 'SHORT'
				|| dataType.toUpperCase() == 'DOUBLE') {
			data += '<option value="SUM">SUM</option>';
			data += '<option value="MIN">MIN</option>';
			data += '<option value="MAX">MAX</option>';
			data += '<option value="AVG">AVG</option>';
		}
		data += '</select>';
		return data;

	},
	enableSelectAggregateFunction : function(colName, isSelected) {

		if (isSelected) {
			$('#aggregate_' + colName).removeAttr('disabled');
		} else {
			$('#aggregate_' + colName).attr('disabled', 'disabled');
		}
	},
	selectAggregateFunction : function(element, colName) {
		var value = $('#aggregate_' + colName).val();
		var func = "";
		if (value.indexOf("DISTINCT") != -1)
			func = value + " " + colName + ")";
		else
			func = value + "(" + colName + ")";

		for ( var i = 0; i < DA.searchColumn.length; i++) {

			if (DA.searchColumn[i].indexOf(colName) != -1) {
				DA.searchColumn.splice(i, 1);
			}
		}
		if (value == "") {
			DA.searchColumn.push(colName);
		} else {
			DA.searchColumn.push(func);
			// set col selected in group by.
//			$('#groupBy' + colName).removeAttr('checked');
//			DA.setGroupBy(colName, false);
		}

		$('#srch_col_fld').val(DA.searchColumn);
		if(DA.queryInfo["selectedColumn"][colName]==undefined || DA.queryInfo["selectedColumn"][colName]==null ){
			DA.queryInfo["selectedColumn"][colName] = new Object();
		}
		DA.queryInfo["selectedColumn"][colName]["function"] = value;
		DA.setOrderByDropDown();

	},
	setOrderByDropDown : function() {
		var list = '';
		if (DA.searchColumn[0] == '*' && DA.searchColumn.length == 1) {
			list = this.colList;
		} else {
			list = DA.searchColumn;
		}
		var orderbyOptData = '<span> </span><span id="selectColClose" class="divcloser"><a href="javascript:DA.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span> <br><form name="orderByForm" id="orderByForm">';
		orderbyOptData += '<table>';
		for ( var i = 0; i < list.length; i++) {
			var index = jQuery.inArray(list[i], DA.colList);
			if (index == -1)
				continue;

			var checked = '';
			var descseleted = '';
			var ascseleted = '';

			if (DA.queryInfo["selectedOrderBy"].hasOwnProperty(list[i])) {
				checked = 'checked="checked"';
				if (DA.queryInfo["selectedOrderBy"][list[i]] == 'DESC') {
					descseleted = 'selected="selected"';
				} else {
					ascseleted = 'selected="selected"';
				}
			}
			orderbyOptData += '<tr>'
					+ '<td width = "70%" style = "white-space: nowrap;">'
					+ '<input type="checkbox" name="orderBy'
					+ list[i]
					+ '" id="orderBy'
					+ list[i]
					+ '" value="'
					+ list[i]
					+ '" onclick="DA.setOrderBy(\''
					+ list[i]
					+ '\', this.checked);" '
					+ checked
					+ ' style=" margin-bottom: 10px;" > '
					+ list[i]
					+ '</td><td>'
					+ '<select id="order'
					+ list[i]
					+ '" onchange = "DA.setOrderBy(\''
					+ list[i]
					+ '\');" >'
					+ '<option value = "ASC" '
					+ ascseleted
					+ '>ASC</option>'
					+ '<option value = "DESC" '
					+ descseleted
					+ '>DESC</option>' + '</select>' + '</td></tr>';
		}
		orderbyOptData += '</table>';
		$('#orderByColFilters').html(orderbyOptData);
		RC.ready();
		if (!DA.blockPreviewToShow)
			DA.setOrderBy();

	},
	setOrderBy : function(value, isChecked) {
		var values = new Array();
		var valuesQuery = [];
		var theForm = document.getElementById("orderByForm");
		if (theForm == null || theForm == undefined || theForm.elements == null
				|| theForm.elements == undefined)
			return;
		for ( var i = 0; i < theForm.elements.length; i++) {
			var e = theForm.elements[i];
			if (e.type == 'checkbox') {
				if (e.checked) {
					var valueOrder = $("#order" + e.value).val();

					if (!values.hasOwnProperty(e.value))
						values[e.value] = new Object();
					values[e.value] = valueOrder;

					valuesQuery.push(e.value + ' ' + valueOrder);
				}
			}
		}
		$('#order_by_col').val(valuesQuery);
		DA.queryInfo["selectedOrderBy"] = jQuery.extend(true, {}, values);
		DA.showCommand();

	},
	getRelationalOperatorDropDown : function(colName, idPrefix, onChaneFunction, dataType) {
		
		var dbType  = $('#queryIODatabase').val();
		var data = '<select id="'+idPrefix
				+ colName
				+ '" disabled="disabled" style="width:100%;" onchange="javascript:'+onChaneFunction+'(this.value,\''
				+ colName + '\');">';
		data += '<option value=""></option>';
		data += '<option value=" = ">=</option>';
		data += '<option value=" != ">!=</option>';
		if (dataType.toUpperCase() == 'INTEGER' || dataType.toUpperCase() == 'LONG'
				|| dataType.toUpperCase() == 'DECIMAL' || dataType.toUpperCase() == 'SHORT'
				|| dataType.toUpperCase() == 'DOUBLE') {
			data += '<option value=" > ">></option>';
			data += '<option value=" < "><</option>';
			data += '<option value=" <= "><=</option>';
			data += '<option value=" >= ">>=</option>';

		}
		if(!(dbType.toUpperCase().indexOf('HIVE') == 0)){
			
			data += '<option value=" IN ">IN</option>';
			data += '<option value=" NOT IN ">NOT IN</option>';
		}
		
		data += '<option value=" IS NULL ">IS NULL</option>';
		data += '<option value=" IS NOT NULL ">IS NOT NULL</option>';			
		data += '<option value=" LIKE ">LIKE</option>';
		data += '<option value=" NOT LIKE ">NOT LIKE</option>';
		data += '<option value=" BETWEEN ">BETWEEN</option>';
		data += '<option value=" NOT BETWEEN ">NOT BETWEEN</option>';


		data += '</select>';
		return data;

	},
	
	getLogicalOperatorDropDown : function(colName, idPrefix, onChangeFunction) {
		var data = '<select id="'+idPrefix
				+ colName
				+ '" disabled="disabled" style="width:100%;" onchange="javascript:'+onChangeFunction+'(this,\''
				+ colName + '\');">';
		data += '<option value=""></option>';
		data += '<option value="AND">AND</option>';
		data += '<option value="OR">OR</option>';

		data += '</select>';
		return data;

	},
	

	setWhereIn : function(colName, isChecked) {
		DA.enableWhereComponent(colName, isChecked);
		if (!isChecked) {
			$('#roperator_' + colName).val('');
			$('#whereval_' + colName).val('');
			$('#loperator_' + colName).val('');
			DA.setSelectedWhereInJSON(colName, false);
		}
		DA.makeWhereCondition();

	},
	
	setNotOperator : function()
	{
		DA.makeWhereCondition();
	},
	
	setHavingIn : function(colName, isChecked) 
	{
		DA.enableHavingComponent(colName, isChecked);
		if (!isChecked)
		{
			$('#roperatorHaving_' + colName).val('');
			$('#havingval_' + colName).val('');
			$('#loperatorHaving_' + colName).val('');
			DA.setSelectedHavingInJSON(colName, false);
		}
		DA.makeHavingCondition();
	},

	setSelectedWhereInJSON : function(colName, isSelected) {
		if (isSelected) {
			if (!DA.queryInfo["selectedWhere"].hasOwnProperty(colName)) {
				DA.queryInfo["selectedWhere"][colName] = new Object();
			}
			DA.queryInfo["selectedWhere"][colName]["roperator"] = $(
					'#roperator_' + colName).val();
			DA.queryInfo["selectedWhere"][colName]["value"] = $(
					'#whereval_' + colName).val();
			DA.queryInfo["selectedWhere"][colName]["loperator"] = $(
					'#loperator_' + colName).val();
		} else {
			delete DA.queryInfo["selectedWhere"][colName];

		}

	},
	setSelectedHavingInJSON : function(colName, isSelected) {
		if (isSelected) 
		{
			if (!DA.queryInfo["selectedHaving"].hasOwnProperty(colName))
			{
				DA.queryInfo["selectedHaving"][colName] = new Object();
			}
			DA.queryInfo["selectedHaving"][colName]["roperator"] = $('#roperatorHaving_' + colName).val();
			DA.queryInfo["selectedHaving"][colName]["value"] = $('#havingvalHaving_' + colName).val();
			DA.queryInfo["selectedHaving"][colName]["loperator"] = $('#loperatorHaving_' + colName).val();
			
		} else {
			delete DA.queryInfo["selectedHaving"][colName];
			
		}
		
	},

	makeWhereCondition : function() {
		var frm = document.getElementById('where_tbl').getElementsByTagName(
				"input");
		var len = frm.length;
		// var colName ='';
		var cond = '';
		DA.selectedWhereArray = [];
		for (i = 0; i < len; i++)
		{
			if (frm[i].type == "checkbox") {

				if (frm[i].checked) {

					var colName = frm[i].value;
					
					DA.selectedWhereArray.push(colName);
					
					cond += colName;
					cond += $('#roperator_' + colName).val() + ' ';

					var colDataType = DA.colMap[colName];
					var operator = $('#roperator_' + colName).val();
					if ((colDataType.toUpperCase() == "STRING" || colDataType.toUpperCase() == "BLOB" || colDataType.toUpperCase() == 'TIMESTAMP')
							&& operator.toUpperCase().indexOf("IN") == -1
							&& operator.toUpperCase().indexOf("IS NULL") == -1
							&& operator.toUpperCase().indexOf("IS NOT NULL") == -1) {
						cond += " '" + $('#whereval_' + colName).val() + "' ";
					} else {
						cond += ' ' + $('#whereval_' + colName).val() + ' ';
					}

					cond += $('#loperator_' + colName).val() + ' ';
					
					if (!DA.queryInfo["selectedWhere"].hasOwnProperty(colName))
					{
						DA.queryInfo["selectedWhere"][colName] = new Object();
					}
					DA.queryInfo["selectedWhere"][colName]["roperator"] = $(
							'#roperator_' + colName).val();
					DA.queryInfo["selectedWhere"][colName]["loperator"] = $(
							'#loperator_' + colName).val();
					DA.queryInfo["selectedWhere"][colName]["value"] = $(
							'#whereval_' + colName).val();

				}
			}
		}
		$('#where_col').val(cond);
		DA.showCommand();

	},
	makeHavingCondition : function()
	{
		var frm = document.getElementById('having_tbl').getElementsByTagName("input");
		var len = frm.length;
		// var colName ='';
		var cond = '';
		DA.selectedHavingArray = [];
		for (i = 0; i < len; i++)
		{
			if (frm[i].type === "checkbox") {
				
				if (frm[i].checked) {
					
					var colName = frm[i].value;
					DA.selectedHavingArray.push(colName);
					
					cond += colName;
					cond += $('#roperatorHaving_' + colName).val() + ' ';
					
					var colDataType = DA.colMap[colName];
					var operator = $('#roperatorHaving_' + colName).val();
					if ((colDataType.toUpperCase() == "STRING"|| colDataType.toUpperCase() == "BLOB" || colDataType.toUpperCase() == 'TIMESTAMP')
							&& operator.toUpperCase().indexOf("IN") == -1
							&& operator.toUpperCase().indexOf("IS NULL") == -1
							&& operator.toUpperCase().indexOf("IS NOT NULL") == -1) {
						cond += " '" + $('#havingval_' + colName).val() + "' ";
					} 
					else 
					{
						cond += ' ' + $('#havingval_' + colName).val() + ' ';
					}
					
					cond += $('#loperatorHaving_' + colName).val() + ' ';
					
					if (!DA.queryInfo["selectedHaving"].hasOwnProperty(colName)) 
					{
						DA.queryInfo["selectedHaving"][colName] = new Object();
					}
					DA.queryInfo["selectedHaving"][colName]["roperator"] = $('#roperatorHaving_' + colName).val();
					DA.queryInfo["selectedHaving"][colName]["loperator"] = $('#loperatorHaving_' + colName).val();
					DA.queryInfo["selectedHaving"][colName]["value"] = $('#havingval_' + colName).val();
					
				}
			}
		}
		$('#having_col').val(cond);
		DA.showCommand();
		
	},
	selectRelationalFunction : function(value, colName) {
		if (value.indexOf("IS NULL") != -1 || value.indexOf("IS NOT NULL") != -1) {
			$('#whereval_' + colName).val("");
			$('#whereval_' + colName).attr('disabled', 'disabled');
		} else
			$('#whereval_' + colName).removeAttr('disabled');
		DA.makeWhereCondition();
	},
	selectRelationalFunctionForHaving : function(value, colName) {
		if (value.indexOf("IS NULL") != -1 || value.indexOf("IS NOT NULL") != -1) 
		{
			$('#havingval_' + colName).val("");
			$('#havingval_' + colName).attr('disabled', 'disabled');
		}
		else
			$('#havingval_' + colName).removeAttr('disabled');
		DA.makeHavingCondition();
	},
	selectLogicalFunction : function() {
		DA.makeWhereCondition();

	},
	enableWhereComponent : function(colName, isSelected)
	{
		if (isSelected) 
		{
			$('#roperator_' + colName).removeAttr('disabled');
			$('#whereval_' + colName).removeAttr('disabled');
			if (colName != DA.lastColumn)
				$('#loperator_' + colName).removeAttr('disabled');
		} else {
			$('#roperator_' + colName).attr('disabled', 'disabled');
			$('#whereval_' + colName).attr('disabled', 'disabled');
			$('#loperator_' + colName).attr('disabled', 'disabled');
		}

	},
	enableHavingComponent : function(colName, isSelected)
	{
		if (isSelected) 
		{
			$('#roperatorHaving_' + colName).removeAttr('disabled');
			$('#havingval_' + colName).removeAttr('disabled');
			if (colName != DA.lastColumn)
				$('#loperatorHaving_' + colName).removeAttr('disabled');
		}
		else 
		{
			$('#roperatorHaving_' + colName).attr('disabled', 'disabled');
			$('#havingval_' + colName).attr('disabled', 'disabled');
			$('#loperatorHaving_' + colName).attr('disabled', 'disabled');
		}
		
	},
	// setGroupHeaderTable : function() {
	//
	// var list = '';
	// if (DA.searchColumn[0] == '*' && DA.searchColumn.length == 1) {
	// list = this.colList;
	// } else {
	// list = DA.searchColumn;
	// }
	// var groupHeader = '<div id="groupHeaderTable"><table><tbody>';
	// groupHeader += '<tr><td nowrap="nowrap">Select Key</td><td
	// nowrap="nowrap">Prefix</td><td nowrap="nowrap">Aggregate Function</td><td
	// nowrap="nowrap">Suffix</td></tr>';
	// for ( var i = 0; i < list.length; i++) {
	//
	// groupHeader += '<tr><td nowrap="nowrap"><input type="checkbox"
	// name="groupHeader'
	// + list[i]
	// + '" id="groupHeader'
	// + list[i]
	// + '" value="'
	// + list[i]
	// + '" onclick="DA.setGroupHeader(\''
	// + list[i]
	// + '\', this.checked);" > ' + list[i] + '</td>';
	// groupHeader += '<td nowrap="nowrap"><input type="text"
	// id="group_header_prefix'
	// + list[i]
	// + '" name="group_header_prefix" placeholder="prefix"
	// onblur="javascript:DA.setGroupHeaderInJSON();" disabled="disabled">
	// </td>';
	// groupHeader += '<td nowrap="nowrap">'
	// + DA.getAggregateFunctionDropDownForGroupHeader(list[i])
	// + '</td>';
	// groupHeader += '<td nowrap="nowrap"><input type="text"
	// id="group_header_suffix'
	// + list[i]
	// + '" name="group_header_suffix" placeholder="suffix"
	// onblur="javascript:DA.setGroupHeaderInJSON();" disabled="disabled">
	// </td>';
	// // groupHeader+='<td
	// //
	// nowrap="nowrap">'+DA.getCSSStyleDropDown(list[i],'group_header_style','DA.setGroupHeaderInJSON()')+'</td>';
	// // groupHeader+='<td nowrap="nowrap"><input type="text"
	// // id="group_header_style_val'+list[i]+'"
	// // name="group_header_style_val"
	// // onblur="javascript:DA.setGroupHeaderInJSON();"
	// // disabled="disabled"> </td>';
	// groupHeader += '<td nowrap = "nowrap"><a href =
	// "javascript:DA.createCSSGeneratorWizard(\''
	// + list[i] + '\', \'groupHeader\');">set style</a></td>';
	// groupHeader += '</tr>'
	// }
	// groupHeader += '</tbody></table></div>'
	// // $('#groupHeaderColFilters').html(groupHeader);
	//
	// },

	// setGroupFooterTable : function() {
	//
	// var list = '';
	// if (DA.searchColumn[0] == '*' && DA.searchColumn.length == 1) {
	// list = this.colList;
	// } else {
	// list = DA.searchColumn;
	// }
	// var groupFooter = '<div id="groupFooterTable"><table><tbody>';
	// groupFooter += '<tr><td nowrap="nowrap">Select Column</td><td
	// nowrap="nowrap">Prefix</td><td nowrap="nowrap">Aggregate Function</td><td
	// nowrap="nowrap">Suffix</td><td>Style</td></tr>';
	// for ( var i = 0; i < list.length; i++) {
	//
	// groupFooter += '<tr><td nowrap="nowrap"><input type="checkbox"
	// name="groupFooter'
	// + list[i]
	// + '" id="groupFooter'
	// + list[i]
	// + '" value="'
	// + list[i]
	// + '" onclick="DA.setGroupFooter(\''
	// + list[i]
	// + '\', this.checked);" > ' + list[i] + '</td>';
	// groupFooter += '<td nowrap="nowrap"><input type="text"
	// disabled="disabled" id="group_footer_prefix'
	// + list[i]
	// + '" name="group_footer_prefix" placeholder="prefix"
	// onblur="javascript:DA.setGroupFooterInJSON();"> </td>';
	// groupFooter += '<td nowrap="nowrap">'
	// + DA.getAggregateFunctionDropDownForGroupFooter(list[i])
	// + '</td>';
	//
	// groupFooter += '<td nowrap="nowrap"><input type="text"
	// disabled="disabled" id="group_footer_suffix'
	// + list[i]
	// + '" name="group_footer_suffix" placeholder="suffix"
	// onblur="javascript:DA.setGroupFooterInJSON();"> </td>';
	// groupFooter += '<td nowrap = "nowrap"><a href =
	// "javascript:DA.createCSSGeneratorWizard(\''
	// + list[i] + '\', \'groupFooter\');">set style</a></td>';
	// groupFooter += '</tr>';
	// }
	// groupFooter += '</tbody></table></div>'
	// // $('#groupFooterColFilters').html(groupFooter);
	//
	// },
	getAggregateFunctionDropDownForGroupHeader : function(colName) {
		if (DA.colList.indexOf(colName) == -1) {
			return "";
		}

		var dataType = DA.colMap[colName];
		var data = '<select id="header_'
				+ colName
				+ '"  style="width:100%;" onchange="javascript:DA.setGroupHeaderInJSON();" disabled="disabled">';
		data += '<option value=""></option>';
		data += '<option value="COUNT">COUNT</option>';
		if (dataType.toUpperCase() == 'INTEGER' || dataType.toUpperCase() == 'DECIMAL') {
			data += '<option value="SUM">SUM</option>';
			data += '<option value="MIN">MIN</option>';
			data += '<option value="MAX">MAX</option>';
			data += '<option value="AVG">AVG</option>';
		}
		data += '</select>';
		return data;

	},
	getAggregateFunctionDropDownForGroupFooter : function(colName) {
		if (DA.colList.indexOf(colName) == -1) {
			return "";
		}

		var dataType = DA.colMap[colName];
		var data = '<select id="footer_'
				+ colName
				+ '"  style="width:100%;" onchange="javascript:DA.setGroupFooterInJSON();" disabled="disabled">';
		data += '<option value=""></option>';
		data += '<option value="COUNT">COUNT</option>';
		data += '<option value="DistinctCount">DISTINCT COUNT</option>';
		if (dataType.toUpperCase() == 'INTEGER' || dataType.toUpperCase() == 'LONG'
				|| dataType.toUpperCase() == 'SHORT' || dataType.toUpperCase() == 'DOUBLE') {
			data += '<option value="SUM">SUM</option>';
			data += '<option value="MIN">MIN</option>';
			data += '<option value="MAX">MAX</option>';
			data += '<option value="AVG">AVG</option>';
		}
		data += '</select>';
		return data;

	},

	createFormattingWizard : function(type) {
		DA.currentType = type;
		Util.addLightbox("formattingWizard", "resources/formatter_wizard.html",
				null, null);
	},

	setGroupHeader : function(colName, isChecked) {
		for ( var i = 0; i < DA.selectedGroupHeaderArray.length; i++) {
			if (DA.selectedGroupHeaderArray[i] == colName) {
				DA.selectedGroupHeaderArray.splice(i, 1);
			}
		}
		if (isChecked) {
			DA.selectedGroupHeaderArray.push(colName);
			$(document.getElementById('group_header_prefix' + colName))
					.removeAttr('disabled');
			$(document.getElementById('header_' + colName)).removeAttr(
					'disabled');
			$(document.getElementById('group_header_suffix' + colName))
					.removeAttr('disabled');
			$(document.getElementById('group_header_style' + colName))
					.removeAttr('disabled');
		} else {

			$(document.getElementById('group_header_prefix' + colName)).attr(
					'disabled', 'disabled');
			$(document.getElementById('header_' + colName)).attr('disabled',
					'disabled');
			;
			$(document.getElementById('group_header_suffix' + colName)).attr(
					'disabled', 'disabled');
			$(document.getElementById('group_header_style' + colName)).attr(
					'disabled', 'disabled');
			$(document.getElementById('group_header_style_val' + colName))
					.attr('disabled', 'disabled');

			if (DA.queryInfo["groupHeader"] != undefined
					&& DA.queryInfo["groupHeader"] != null
					&& DA.queryInfo["groupHeader"] != "") {
				delete DA.queryInfo["groupHeader"][colName];
			}
		}

		if (DA.selectedGroupHeaderArray.length == 0) {
			$("#nextViewCSSGenerator").attr('disabled', 'disabled');
		} else {
			$("#nextViewCSSGenerator").removeAttr('disabled');
		}

		DA.setGroupHeaderInJSON();

	},

	setGroupFooter : function(colName, isChecked) {
		for ( var i = 0; i < DA.selectedGroupFooterArray.length; i++) {
			if (DA.selectedGroupFooterArray[i] == colName) {
				DA.selectedGroupFooterArray.splice(i, 1);
			}
		}
		if (isChecked) {
			DA.selectedGroupFooterArray.push(colName);

			$(document.getElementById('group_footer_prefix' + colName))
					.removeAttr('disabled');
			$(document.getElementById('footer_' + colName)).removeAttr(
					'disabled');
			$(document.getElementById('group_footer_suffix' + colName))
					.removeAttr('disabled');
			$(document.getElementById('group_footer_style' + colName))
					.removeAttr('disabled');
			$(document.getElementById('group_footer_style_val' + colName))
					.removeAttr('disabled');
		} else {
			$(document.getElementById('group_footer_prefix' + colName)).attr(
					'disabled', 'disabled');
			$(document.getElementById('footer_' + colName)).attr('disabled',
					'disabled');
			$(document.getElementById('group_footer_suffix' + colName)).attr(
					'disabled', 'disabled');
			$(document.getElementById('group_footer_style' + colName)).attr(
					'disabled', 'disabled');
			$(document.getElementById('group_footer_style_val' + colName))
					.attr('disabled', 'disabled');
			if (DA.queryInfo["groupFooter"] != undefined
					&& DA.queryInfo["groupFooter"] != null
					&& DA.queryInfo["groupFooter"] != "") {
				delete DA.queryInfo["groupFooter"][colName];
			}
		}

		if (DA.selectedGroupFooterArray.length == 0) {
			$("#nextViewCSSGenerator").attr('disabled', 'disabled');
		} else {
			$("#nextViewCSSGenerator").removeAttr('disabled');
		}

		DA.setGroupFooterInJSON();

	},

	setGroupHeaderInJSON : function() {
		var prefix = '';
		var suffix = '';
		var func = '';
		if (DA.queryInfo["groupHeader"] == undefined
				|| DA.queryInfo["groupHeader"] == null
				|| DA.queryInfo["groupHeader"] == "") {
			DA.queryInfo["groupHeader"] = new Object();
		}
		for ( var i = 0; i < DA.selectedGroupHeaderArray.length; i++) {
			var colName = DA.selectedGroupHeaderArray[i];
			if (DA.queryInfo["groupHeader"][colName] == undefined
					|| DA.queryInfo["groupHeader"][colName] == null) {
				DA.queryInfo["groupHeader"][colName] = new Object();
			}

			var val = $(
					document.getElementById('group_header_prefix' + colName))
					.val();
			val = $('input[id="group_header_prefix' + colName + '"]').val();
			DA.queryInfo["groupHeader"][colName]["prefix"] = $(
					document.getElementById('group_header_prefix' + colName))
					.val();
			var header_func = $(document.getElementById('header_' + colName))
					.val();
			if (header_func == undefined || header_func == null) {
				header_func = ""
			}
			DA.queryInfo["groupHeader"][colName]["function"] = header_func;
			DA.queryInfo["groupHeader"][colName]["suffix"] = $(
					document.getElementById('group_header_suffix' + colName))
					.val();
			if (DA.queryInfo["groupHeader"][colName]["style"] == undefined
					|| DA.queryInfo["groupHeader"][colName] == null) {
				DA.queryInfo["groupHeader"][colName]["style"] = new Object();
				DA.queryInfo["groupHeader"][colName]["style"]["border"] = "1px solid #DDD";
			}
			var styleKey = $(
					document.getElementById('group_header_style' + colName))
					.val();
			DA.queryInfo["groupHeader"][colName]["style"][styleKey] = $(
					document.getElementById('group_header_style_val' + colName))
					.val();
		}
		$('#group_header_col').val(JSON.stringify(DA.queryInfo["groupHeader"]));
		DA.showReportPreview();

	},
	setGroupFooterInJSON : function() {
		var prefix = '';
		var suffix = '';
		var func = '';
		if (DA.queryInfo["groupFooter"] == undefined
				|| DA.queryInfo["groupFooter"] == null
				|| DA.queryInfo["groupFooter"] == "") {
			DA.queryInfo["groupFooter"] = new Object();
		}
		for ( var i = 0; i < DA.selectedGroupFooterArray.length; i++) {
			var colName = DA.selectedGroupFooterArray[i];
			if (DA.queryInfo["groupFooter"][colName] == undefined
					|| DA.queryInfo["groupFooter"][colName] == null) {
				DA.queryInfo["groupFooter"][colName] = new Object();
			}
			var footer_func = $(document.getElementById('footer_' + colName))
					.val();
			if (footer_func == undefined || footer_func == null) {
				footer_func = "";
			}
			DA.queryInfo["groupFooter"][colName]["function"] = footer_func;
			DA.queryInfo["groupFooter"][colName]["prefix"] = $(
					document.getElementById('group_footer_prefix' + colName))
					.val();
			DA.queryInfo["groupFooter"][colName]["suffix"] = $(
					document.getElementById('group_footer_suffix' + colName))
					.val();
			if (DA.queryInfo["groupFooter"][colName]["style"] == undefined
					|| DA.queryInfo["groupFooter"][colName]["style"] == null) {
				DA.queryInfo["groupFooter"][colName]["style"] = new Object();
				DA.queryInfo["groupFooter"][colName]["style"]["border"] = "1px solid #DDD";
			}
			var styleKey = $(
					document.getElementById('group_footer_style' + colName))
					.val();
			DA.queryInfo["groupFooter"][colName]["style"][styleKey] = $(
					document.getElementById('group_footer_style_val' + colName))
					.val();
		}
		$('#group_footer_col').val(JSON.stringify(DA.queryInfo["groupFooter"]));
		DA.showReportPreview();

	},
	// setColumnHeaderTable : function() {
	//
	// var list = '';
	// if (DA.searchColumn[0] == '*' && DA.searchColumn.length == 1) {
	// list = this.colList;
	// } else {
	// list = DA.searchColumn;
	// }
	// var groupHeader = '<table><tbody>';
	// groupHeader += '<tr><td nowrap="nowrap">Select Column</td><td
	// nowrap="nowrap">Title</td><td nowrap="nowrap">Style</td></tr>';
	// for ( var i = 0; i < list.length; i++) {
	//
	// groupHeader += '<tr><td nowrap="nowrap"><input type="checkbox"
	// name="columnHeader'
	// + list[i]
	// + '" id="columnHeader'
	// + list[i]
	// + '" value="'
	// + list[i]
	// + '" onclick="DA.setColumnHeaderProperty(\''
	// + list[i] + '\', this.checked);" > ' + list[i] + '</td>';
	// // groupHeader+='<td
	// // nowrap="nowrap">'+DA.getHeaderCSSStyleDropDown(list[i])+'</td>';
	//
	// groupHeader += '<td nowrap="nowrap"><input type="text"
	// id="column_header_title'
	// + list[i]
	// + '" name="column_header_title" value="'
	// + list[i]
	// + '" placeholder="Title"
	// onblur="javascript:DA.setColumnHeaderPropInJSON();"> </td>';
	// // groupHeader += '<td><a onclick =
	// // "javascript:DA.createCSSGeneratorWizard("' + list[i] + ');">set
	// // style</a></td>';
	// groupHeader += '<td nowrap = "nowrap"><a href =
	// "javascript:DA.createCSSGeneratorWizard(\''
	// + list[i] + '\', \'columnHeader\');">set style</a></td>';
	// groupHeader += '</tr>'
	// }
	// groupHeader += '</tbody></table>'
	// $('#columnHeaderColFilters').html(groupHeader);
	//
	// },

	createCSSGeneratorWizard : function(type) {
		DA.currentType = type;
		if (type == "columnHeader")
			DA.tempData = jQuery.extend(true, {},
					DA.queryInfo["colHeaderDetail"]);
		else if (type == "columnDetail")
			DA.tempData = jQuery.extend(true, {}, DA.queryInfo["colDetail"]);
		else
			DA.tempData = jQuery.extend(true, {}, DA.queryInfo[type]);

		Util.addLightbox("cssGeneratorWizard",
				"resources/css_generator_wizard.html", null, null);

	},

	setColumnHeaderProperty : function(colName, isChecked) {
		for ( var i = 0; i < DA.selectedColumnHeaderArray.length; i++) {
			if (DA.selectedColumnHeaderArray[i] == colName) {
				DA.selectedColumnHeaderArray.splice(i, 1);
			}
		}
		if (isChecked) {
			DA.selectedColumnHeaderArray.push(colName);
			$('#columnHeaderProperty_' + colName).removeAttr('disabled');
			$('#column_header_title' + colName).removeAttr('disabled');
		} else {
			$('#columnHeaderProperty_' + colName).attr('disabled', 'disabled');
			$('#column_header_title' + colName).attr('disabled', 'disabled');
			delete DA.queryInfo["colHeaderDetail"][colName];
		}
		DA.setColumnHeaderPropInJSON();

	},
	selectedColumnDetailArray : [],
	setColumnDetailProperty : function(colName, isChecked) {
		for ( var i = 0; i < DA.selectedColumnDetailArray.length; i++) {
			if (DA.selectedColumnDetailArray[i] == colName) {
				DA.selectedColumnDetailArray.splice(i, 1);
			}
		}
		if (isChecked) {
			DA.selectedColumnDetailArray.push(colName);
			$('#columnDetailProperty_' + colName).removeAttr('disabled');
			$('#column_detail_prop' + colName).removeAttr('disabled');
		} else {
			$('#columnDetailProperty_' + colName).attr('disabled', 'disabled');
			$('#column_detail_prop' + colName).attr('disabled', 'disabled');
			delete DA.queryInfo["colDetail"][colName];
		}
		DA.setColumnDetailPropInJSON();

	},
	getColumnDetailCSSStyleDropDown : function(colName) {
		if (DA.colList.indexOf(colName) == -1) {
			return "";
		}

		var dataType = DA.colMap[colName];
		var data = '<select id="columnDetailProperty_'
				+ colName
				+ '"  style="width:100%;" onchange="javascript:DA.setColumnDetailPropInJSON();" disabled="disabled">';
		data += '<option value=""></option>';
		data += '<option value="background-color">background-color</option>';
		data += '<option value="font">font</option>';
		data += '<option value="width">width</option>';

		data += '</select>';
		return data;
	},
	getHeaderCSSStyleDropDown : function(colName) {
		if (DA.colList.indexOf(colName) == -1) {
			return "";
		}

		var dataType = DA.colMap[colName];
		var data = '<select id="columnHeaderProperty_'
				+ colName
				+ '"  style="width:100%;" onchange="javascript:DA.setColumnHeaderPropInJSON();" disabled="disabled">';
		data += '<option value=""></option>';
		data += '<option value="background-color">background-color</option>';
		data += '<option value="font">font</option>';
		data += '<option value="width">width</option>';

		data += '</select>';
		return data;
	},
	setColumnHeaderPropInJSON : function() {

		var styleKey = '';
		var styleValue = '';

		if (DA.queryInfo["colHeaderDetail"] == undefined
				|| DA.queryInfo["colHeaderDetail"] == null) {
			DA.queryInfo["colHeaderDetail"] = DA.getDefaultHeaderColumnJSON();
		}
		for ( var i = 0; i < DA.selectedColumnHeaderArray.length; i++) {
			var colName = DA.selectedColumnHeaderArray[i];

			if (DA.queryInfo["colHeaderDetail"][colName] == undefined
					|| DA.queryInfo["colHeaderDetail"][colName] == null) {
				DA.queryInfo["colHeaderDetail"][colName] = DA
						.getDefaultHeaderColumnJSONForCol(colName);
				DA.queryInfo["colHeaderDetail"][colName]["border"] = "1px solid #DDD";
			}

			// var styleKey =$('#columnHeaderProperty_'+colName).val();
			// var styleVal = $('#column_header_prop'+colName).val();
			var colTitle = $('#column_header_title' + colName).val();

			if (colTitle == "") {
				delete DA.queryInfo["colHeaderDetail"][colName]["title"];
			} else {
				DA.queryInfo["colHeaderDetail"][colName]["title"] = colTitle;

			}

		}
		$('#column_header_col').val(
				JSON.stringify(DA.queryInfo["colHeaderDetail"]));

		DA.showReportPreview();

	},
	setColumnDetailPropInJSON : function() {
		var styleKey = '';
		var styleValue = '';

		if (DA.queryInfo["colDetail"] == undefined
				|| DA.queryInfo["colDetail"] == null) {
			DA.queryInfo["colDetail"] = new Object();
		}
		for ( var i = 0; i < DA.selectedColumnDetailArray.length; i++) {
			var colName = DA.selectedColumnDetailArray[i];

			if (DA.queryInfo["colDetail"][colName] == undefined
					|| DA.queryInfo["colDetail"][colName] == null) {
				DA.queryInfo["colDetail"][colName] = new Object();
				DA.queryInfo["colDetail"][colName]["border"] = "1px solid #DDD";

			}

			var styleKey = $('#columnDetailProperty_' + colName).val();
			var styleVal = $('#column_detail_prop' + colName).val();
			DA.queryInfo["colDetail"][colName][styleKey] = styleVal;

		}

		$('#column_detail_col').val(JSON.stringify(DA.queryInfo["colDetail"]));
		DA.showReportPreview();

	},
	getCSSStyleDropDown : function(colName, idPrefix, onChangeFunc) {
		if (DA.colList.indexOf(colName) == -1) {
			return "";
		}

		var dataType = DA.colMap[colName];
		var data = '<select id="' + idPrefix + colName
				+ '"  style="width:100%;" onchange="javascript:' + onChangeFunc
				+ ';" disabled="disabled">';
		data += '<option value=""></option>';
		data += '<option value="background-color">background-color</option>';
		data += '<option value="font">font</option>';
		data += '<option value="width">width</option>';

		data += '</select>';
		return data;
	},
	showAddChart : function(position) {
		chartPosition = position;
		Util.addLightbox("addchart", "resources/add_report_chart.html", null,
				null);
	},
	getChartTemplate : function(idprefix) {
		var chartDiv = '';
		// chartDiv += '<div style="display: block; "
		// id="'+idprefix+'chartPreviewDiv" >'
		// +'<div id="'+idprefix+'main_chart_preveiw_title" ><b>Chart
		// Title</b></div>'
		// +'<div id="'+idprefix+'main_chart_preview_plot_area" style="width:
		// 75%; float: left;">'
		// +'<div id="'+idprefix+'main_chart_preview_client_area"
		// style="text-align: center;">'
		// +'<div id="'+idprefix+'main_preview_chart" style="height: 200px">'
		// +'</div>'
		// +'<span id="'+idprefix+'main_chart_preveiw_series" class="header"
		// style="width: 100%;"><b>Series 1</b></span>'
		// +'</div>'
		// +'</div>'
		// +'<div style=" width: 15%;height: 215px; float: right; margin-left:
		// 2%; margin-right: 5%;"
		// id="'+idprefix+'main_parent_chart_legend_div">'
		// +'<div style=" " id="'+idprefix+'main_chart_legend_div">'
		// +'<div id="'+idprefix+'main_legend_title">Legend Title</div>'
		// +'<table id="'+idprefix+'mainlegendTable">'
		// +'</table>'
		// +'</div>'
		// +'</div>'
		// +'</div></div>';
		//		
		chartDiv += '<div style="display: block; height:100%;" id="'
				+ idprefix
				+ 'chartPreviewDiv" >'
				+ '<div id="'
				+ idprefix
				+ 'main_chart_preveiw_title"  ><b>Chart Title</b></div>'
				+ '<div id="'
				+ idprefix
				+ 'main_chart_preview_plot_area" style="width: 75%; float: left;">'
				+ '<div id="'
				+ idprefix
				+ 'main_chart_preview_client_area" style="text-align: center;">'
				+ '<div id="'
				+ idprefix
				+ 'main_preview_chart" style="height: 200px">'
				+ '</div>'
				+ '<span id="'
				+ idprefix
				+ 'main_chart_preveiw_series" class="header" style="width: 100%;"><b>Series 1</b></span>'
				+ '</div>'

				+ '</div>'
				+ '<div style="  width: 15%;height: 200px; float: right; margin-left: 2%; margin-right: 5%;" id="'
				+ idprefix + 'main_parent_chart_legend_div">'
				+ '<div style=" " id="' + idprefix + 'main_chart_legend_div">'
				+ '<div id="' + idprefix
				+ 'main_legend_title">Legend Title</div>' + '<table id="'
				+ idprefix + 'mainlegendTable">' + '</table>' + '</div>'
				+ '</div>' + '</div>'

		return chartDiv;

	},

	setReportHeaderPreview : function(detailSection) {
		var htmlContent = '';
		var title = '';
		var previewLabelId = '';

		if (detailSection == "queryHeader") {
			previewLabelId = "queryHeaderPreview";
		} else {
			previewLabelId = "queryFooterPreview";
		}

		for ( var key in DA.queryInfo[detailSection]) {

			var headerObj = DA.queryInfo[detailSection][key];
			var colStyle = '';
			if (headerObj.hasOwnProperty("title")) {
				for ( var attr in headerObj) {
					if (attr == "font-size" || attr == "title"
							|| attr == "width")
						continue;

					if (attr.indexOf("color") != -1) {
						$('#' + previewLabelId)
								.css(attr, "#" + headerObj[attr]);
						colStyle += attr + ": #" + headerObj[attr] + ";";
					} else {
						$('#' + previewLabelId).css(attr, headerObj[attr]);
						colStyle += attr + ":" + headerObj[attr] + ";";
					}

				}
				title = headerObj["title"];
			}

			// htmlContent+='<span id="report_'+detailSection+'">';
			htmlContent += '<div  style="width:100%;height:100%;' + colStyle
					+ '">' + title + '</div>';
			// htmlContent+='<span >'+title+'</span></span></span>&nbsp;';
		}
		$('#' + previewLabelId).html(title);
		if (detailSection == "queryHeader") {
			$('#report_header_preview').html(htmlContent);
		} else if (detailSection == "queryFooter") {
			$('#report_footer_preview').html(htmlContent);
		}

	},

	setColSectionPreview : function(detailSection) {
		var htmlContent = '';
		var className = "preview-col-name";
		if (detailSection == "colDetail") {
			className = "preview-item-name";
		}
		var list = '';
		if (DA.searchColumn[0] == '*' && DA.searchColumn.length == 1) {
			list = this.colList;
		} else {
			list = DA.searchColumn;
		}
		var headerObj = DA.queryInfo[detailSection]

		for ( var i = 0; i < list.length; i++) {
			var colName = list[i] + '';
			var colStyle = '';
			var colTitle = colName;
			if (headerObj.hasOwnProperty(colName)) {
				for ( var attr in headerObj[colName]) {
					if (attr == "title") {
						if (headerObj[colName][attr] != "")
							colTitle = headerObj[colName][attr];
						continue;
					}
					if (attr == "font-size" || attr == "width")
						continue;
					if (attr.indexOf("color") != -1)
						colStyle += attr + ": #" + headerObj[colName][attr]
								+ ";";
					else
						colStyle += attr + ":" + headerObj[colName][attr] + ";";
				}
			}

			htmlContent += '<span class="preview-button" id="' + detailSection
					+ '_' + colName + '">';
			htmlContent += '<span class="' + className + '" style="' + colStyle
					+ '">';
			htmlContent += '<span class="preview-item-name-text">' + colTitle
					+ '</span></span></span>&nbsp;';
		}
		if (detailSection == "colHeaderDetail") {

			$('#column_header_preview').html(htmlContent);
		} else if (detailSection == "colDetail") {
			$('#column_detail_preview').html(htmlContent);
		}

	},

	setGroupSectionPreview : function(detailSection) {
		var htmlContent = '';
		var list = '';
		var headerObj = DA.queryInfo[detailSection]
		for ( var attr in headerObj) {
			if (attr == null || attr == "" || attr == "null")
				continue;

			var colName = attr;
			var styleObj = headerObj[attr]["style"];
			var cssStyle = "";
			for ( var prop in styleObj) {
				if (attr == "font-size" || attr == "title" || attr == "width")
					continue;
				if (prop.indexOf("color") != -1)
					cssStyle += prop + ": #" + styleObj[prop] + ";";
				else
					cssStyle += prop + ":" + styleObj[prop] + ";";
			}
			htmlContent += '<span class="preview-button" id="' + detailSection
					+ '_' + colName + '">';
			htmlContent += '<span class="preview-item-name" style="' + cssStyle
					+ '">';
			htmlContent += '<span class="preview-item-name-text">' + colName
					+ '</span></span></span>&nbsp;';
		}
		if (detailSection == "groupHeader") {
			$('#group_header_preview').html(htmlContent);
		} else if (detailSection == "groupFooter") {
			$('#group_footer_preview').html(htmlContent);
		}

	},

	setSelectedColumn : function(colName, isSelected) {
		if (isSelected) {
			if (colName == '*') {
				DA.queryInfo["selectedColumn"] = new Object();
				DA.queryInfo["selectedColumn"]["*"] = "*";
			} else if (DA.queryInfo["selectedColumn"].hasOwnProperty("*")) {
				delete DA.queryInfo["selectedColumn"]["*"];
			}
			DA.queryInfo["selectedColumn"][colName] = new Object();
			DA.queryInfo["selectedColumn"][colName]["function"] = $(
					'#aggregate_' + colName).val();
		} else {
			delete DA.queryInfo["selectedColumn"][colName];
		}

	},

	setQueryBuilderValues : function() {
		DA.queryInfo = jQuery.extend(true, {}, DA.selectedHistoryObj);

		var selectedColumnObj = DA.queryInfo["selectedColumn"];
		
		
		for ( var colName in selectedColumnObj) {
			if(DA.colList.indexOf(colName) == -1){
				delete DA.queryInfo["selectedColumn"][colName];
				continue;
			}
//			if (colName == "*") {
//				$('#filterByALL').attr("checked", "checked");
//				DA.selectColumnOperationWrapper('*', true);
//			} else {
				$('#filterByALL').removeAttr("checked");
				$('#filterBy' + colName).attr("checked", true);
				
				$('#aggregate_' + colName).val(selectedColumnObj[colName]["function"]);
				DA.selectColumnOperationWrapper(colName, true);
				$('#aggregate_' + colName).removeAttr("disabled");
				
				DA.selectAggregateFunction(null, colName)
//		}
		}

		var selectedWhereObj = DA.queryInfo["selectedWhere"];
		DA.selectedWhereArray = [];
		for ( var colName in selectedWhereObj) {
			$('#whereBy' + colName).attr("checked", true);
			$('#roperator_' + colName).val(
					DA.queryInfo["selectedWhere"][colName]["roperator"]);
			$('#loperator_' + colName).val(
					DA.queryInfo["selectedWhere"][colName]["loperator"]);
			$('#whereval_' + colName).val(
					DA.queryInfo["selectedWhere"][colName]["value"]);
			
			$('#roperator_' + colName).removeAttr("disabled");
			$('#whereval_' + colName).removeAttr("disabled");
			$('#loperator_' + colName).removeAttr("disabled");
		}
		DA.makeWhereCondition();

		var selectedHavingObj = DA.queryInfo["selectedHaving"];
		DA.selectedHavingArray = [];
		for ( var colName in selectedHavingObj) {
			$('#having' + colName).attr("checked", true);
			$('#roperatorHaving_' + colName).val(
					DA.queryInfo["selectedHaving"][colName]["roperator"]);
			$('#loperatorHaving_' + colName).val(
					DA.queryInfo["selectedHaving"][colName]["loperator"]);
			$('#havingval_' + colName).val(
					DA.queryInfo["selectedHaving"][colName]["value"]);
			
			$('#roperator_' + colName).removeAttr("disabled");
			$('#havingval_' + colName).removeAttr("disabled");
			$('#loperatorHaving_' + colName).removeAttr("disabled");
		}
		DA.makeHavingCondition();

		var selectedGroupByObj = DA.queryInfo["selectedGroupBy"];
		DA.groupByForm = DA.queryInfo["selectedGroupBy"];
		$('#grp_by_col').val(DA.queryInfo["selectedGroupBy"]);
		for ( var i = 0; i < selectedGroupByObj.length; i++) {
			$('#groupBy' + selectedGroupByObj[i]).attr("checked", true);
		}
		DA.groupByForm = DA.queryInfo["selectedGroupBy"];
		DA.setOrderByDropDown();
		var selectedOrderByObj = DA.queryInfo["selectedOrderBy"];
		var arrOrder = [];
		for (key in selectedOrderByObj)
			arrOrder.push(key + ' ' + selectedOrderByObj[key]);
		$('#order_by_col').val(arrOrder);

		for ( var i = 0; i < selectedOrderByObj.length; i++) {
			$('#orderBy' + selectedOrderByObj[i]).attr("checked", true);
		}
		$("#query_textarea").val(DA.queryInfo["sqlQuery"]);
		
		document.getElementById("persist").checked = DA.queryInfo["persistResults"];
		DA.persistClicked(DA.queryInfo["persistResults"]);

		DA.createChartGrid(DA.queryInfo["chartDetail"]);
		DA.showReportPreview();

		if (Navbar.isFromsummaryView && Navbar.isViewerView
				&& Navbar.isExecuteQuery) {
			DAT.executeSeletedQuery();
			Navbar.isExecuteQuery = false;
		}

	},

	deleteQuery : function() {
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton = ' No';
		jConfirm('Are you sure you want to delete this Query ?',
				'Delete Query', function(val) {
					if (val == true) {
						// callBackFunc = deleteQuery;
						DA.isDelete = true;
						Navbar.queryManagerDirtyBit = false;
						Util.addLightbox('export',
								'resources/delete_big_query_box.html');
					} else {
						return;
					}
					jQuery.alerts.okButton = ' Ok ';
					jQuery.alerts.cancelButton = ' Cancel';
				});
		$("#popup_container").css("z-index", "99999999");

	},

	queryIdChanged : function() {
		$('#queryIdTitle').text($('#queryId').val());
		$("#design_link").text("Design: " + $("#queryId").val());
		$("#preview_span").text("Preview: " + $("#queryId").val());
		DA.showReportPreviewNew();
	},
	setResultTableName : function(element) {

		var value = $('#resultTableName').val();
		if (value == "") {
			jAlert("Please Enter valid Resulted table Name", "Error");
			return;
		}
		DA.queryInfo["resultTableName"] = value;

	},
	closeSelectionDiv : function() {

		$(
				'#gantt_y_labelColFilters,#gantt_y_startColFilters,#gantt_y_endColFilters,#difference_y_positiveColFilters,#difference_y_negetiveColFilters,#stock_y_highColFilters,#stock_y_lowColFilters,#stock_y_openColFilters,#stock_y_closeColFilters,#line_y_seriesColFilters,#bubble_y_valueColFilters,#bubble_y_sizeColFilters,#searchColFilters,#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchFromFilters,#groupByColFilters,#havingColFilters,#orderByColFilters,#whereFilters')
				.hide();
	},
	onlyPopulateTableNameDiv : function(map) {

		var list = [];
		DA.tableMap = map;

		for ( var tableName in map) {
			list.push(tableName);
		}

		if (list == null || list.length == 0) {
			jAlert(
					"There is no data available for selected namenode.Please upload data from Data Import/Export tab.",
					"Error");
			return;
		}
		Navbar.isDataAvailabe = true;
		var data = '<form name="tables" id="tables">';
		data += '<table><tr><td colspan="2"><span>Search On:</span><span id="selectColClose" class="divcloser"><a href="javascript:DA.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span></td></tr>';
		for ( var i = 0; i < list.length; i++) {
			var tbl_name = list[i];
			if (i == 0) {
				data += '<tr><td><input checked checked="checked" type="checkbox" name="nnID[]" id="filterBy'
						+ tbl_name
						+ '" value="'
						+ tbl_name
						+ '" onclick="DA.setLocationSearch(\''
						+ tbl_name
						+ '\',this.checked);" > </td><td>' + tbl_name + '</td></tr>';
			} else {
				data += '<tr><td><input type="checkbox" name="nnID[]" id="filterBy'
						+ tbl_name
						+ '" value="'
						+ tbl_name
						+ '" onclick="DA.setLocationSearch(\''
						+ tbl_name
						+ '\',this.checked);" > </td><td>' + tbl_name + '</td></tr>';
			}
		}
		data += '</table></form>';
		$('#searchFromFilters').html(data);
		
		
			
		$('#searchFromFilters').find('input[type=checkbox]:checked')
				.removeAttr('checked');
		var obj = DA.queryInfo["selectedTable"];
		for ( var i = 0; i < obj.length; i++) {
			$('#filterBy' + obj[i]).attr('checked', 'checked');
		}

	},
	setQueryDirtyBitHandlerEvent : function() {
		$('input[type=checkbox]').click(function() {
			Navbar.queryManagerDirtyBit = true;
		});

		$('select').change(function() {
			if ((this.id != "queryIONameNodeId") && (this.id != "bigQueryIds"))
				Navbar.queryManagerDirtyBit = true;
		});

		$('input[type=text]').keypress(function() {
			Navbar.queryManagerDirtyBit = true;
		});

		$('textarea').keypress(function() {
			Navbar.queryManagerDirtyBit = true;
		});

		$('input[type=search]').keypress(function() {
			Navbar.queryManagerDirtyBit = true;
		});
		$('input[type=search]').change(function() {
			Navbar.queryManagerDirtyBit = true;
		});
		$('div#chartOptionsDiv input[type=text]').keypress(function() {
			DA.chartDesignerDirtyBit = true;
		});

		$('div#chartOptionsDiv input[type=search]').keypress(function() {
			DA.chartDesignerDirtyBit = true;
		});

		$('div#chartOptionsDiv select').change(function() {
			DA.chartDesignerDirtyBit = true;
		});

		$('div#chartOptionsDiv input[type=checkbox]').click(function() {
			DA.chartDesignerDirtyBit = true;
		});
	},

	queryChanged : function() {
		if (Navbar.queryManagerDirtyBit == true) {
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton = ' No';
			jConfirm("Some of the fields of Query \"" + DA.selectedQueryId
					+ "\" are modified. Do you want to navigate?", 'BigQuery',
					function(val) {
						if (val == true) {
							$(
									"#bigQueryIds option[value='"
											+ DAT.tempQuery + "']").remove();
							DAT.tempQuery = '';
							DA.changeSelectedQuery();
							Navbar.queryManagerDirtyBit = false;

						} else {
							$('#bigQueryIds').val(DA.selectedQueryId);
						}
					});
			jQuery.alerts.okButton = ' Ok ';
			jQuery.alerts.cancelButton = ' Cancel';
		} else {
			DA.changeSelectedQuery();
		}
	},
	changeSelectedQuery : function() {
		BQS.isEditQuery = true;
		var queryId = $('#bigQueryIds').val();
		var nameNode = $('#queryIONameNodeId').val();
		var userName = Util.getLoggedInUserName();
		Navbar.selectedQueryId = queryId;
		DA.selectedQueryId = queryId;
		RemoteManager.getBigQueryInfo(nameNode, queryId,userName, DA.fillSavedQuery);
	},

	setLimitResultRowsState : function(isChecked) {
		DA.queryInfo["setLimitResultRows"] = isChecked;
		if (isChecked)
			$('#limitResultRowsValue').css('display', '');
		else
			$('#limitResultRowsValue').css('display', 'none');

	},

	getLimitResultRowsState : function() {
		return $('#limitResultRows').is(':checked');
	},

	saveBQLog : function() {
		var flag = DA.validateSchedule();
		if (flag) {
			DA.findStatus();
			var reportDate = $('#reportDate').val();
			var interval = $('#interval').val();
			RemoteManager.scheduleQueryJob(interval, reportDate, DA
					.findExportType(), document
					.getElementById('notificationType').value, document
					.getElementById('alertRaisedNotificationMessage').value,
					null, document.getElementById('schedID').value,
					BQS.selectedQueryArray, BQS.selectedNameNode,
					DA.scheduleReturn);
		}

	},

	logBQSave : function() {

		if ($('#notificationType').val() == 'Log') {
			document.getElementById('EmailNext').disabled = true;
			document.getElementById('logSave').disabled = false;
		} else {
			document.getElementById('EmailNext').disabled = false;
			document.getElementById('logSave').disabled = true;
		}

	},

	scheduleBQwithoutNotify : function() {
		var flag = DA.validateSchedule();
		if (flag) {
			DA.validateForScheduleID();
		}
	},

	changeBQButton : function() {
		if ($('#notificationEnable').attr('checked')) {
			document.getElementById('BQNext').disabled = false;
			document.getElementById('BQFinish').disabled = true;
		} else {
			document.getElementById('BQNext').disabled = true;
			document.getElementById('BQFinish').disabled = false;
		}
	},

	validateForScheduleID : function() {
		if (document.getElementById('schedID').value != "") {
			RemoteManager.checkBigQueryScheduleId(document
					.getElementById('schedID').value, DA.checkValidateResp);
		} else {
			document.getElementById('msg_td_1').innerHTML = "";
			document.getElementById('msg_td_1').innerHTML += "* Schedule Id Required<br>";
		}
	},

	checkValidateResp : function(flag) {
		if (flag) {
			jAlert("ScheduleId already Taken", "Error");
			$("#popup_container").css("z-index", "99999999");
		} else {
			DA.findStatus();
			var reportDate = $('#reportDate').val();
			var interval = $('#interval').val();
			RemoteManager.scheduleQueryJobWithoutNotification(interval,
					reportDate, DA.findExportType(), document
							.getElementById('schedID').value,
					BQS.selectedQueryArray, BQS.selectedNameNode,
					DA.scheduleReturn);
		}
	},
	getDefaultHeaderColumnJSON : function() {
		
		var header = new Object();
		this.colMap;
		for ( var attr in DA.colMap) {
			var colObject = new Object();
			colObject["title"] = attr;
			colObject["color"] = "333333";
			colObject["text-align"] = "center";
			colObject["font-size"] = "10px";
			colObject["font-style"] = "normal";
			colObject["background-color"] = "69abdb";
			colObject["font-family"] = "Arial";
			colObject["font-weight"] = "bold";
			colObject["format"] = {};
			if (DA.colMap[attr].toUpperCase() == 'INTEGER' || DA.colMap[attr].toUpperCase() == 'LONG') {
				colObject["width"] = "70px";
			} else if (DA.colMap[attr].toUpperCase() == 'DOUBLE') {
				colObject["width"] = "120px";
			} else if (DA.colMap[attr].toUpperCase() == 'STRING') {
				colObject["width"] = "100px";
			} else if (DA.colMap[attr].toUpperCase() == 'TIMESTAMP') {
				colObject["width"] = "125px";
			} else if (DA.colMap[attr].toUpperCase() == 'SHORT') {
				colObject["width"] = "40px";
			} else {
				colObject["width"] = "100px";
			}
			if (attr.toUpperCase() == 'FILEPATH') {
				colObject["width"] = "250px";
			}
			header[attr] = colObject;
		}
		return header;
	},
	getDefaultHeaderColumnJSONForCol : function(colName) {
		var colObject = new Object();
		colObject["title"] = colName;
		colObject["color"] = "#2E6E9E";
		colObject["text-align"] = "center";
		colObject["font-size"] = "12px";
		colObject["font-style"] = "normal";
		colObject["background-color"] = "#90c7f1";
		colObject["font-family"] = "Arial";
		colObject["font-weight"] = "bold";
		colObject["format"] = {};

		if (DA.colMap[colName].toUpperCase() == 'INTEGER') {
			colObject["width"] = "70px";
		} else if (DA.colMap[colName].toUpperCase() == 'DOUBLE') {
			colObject["width"] = "120px";
		} else if (DA.colMap[colName].toUpperCase() == 'STRING') {
			colObject["width"] = "100px";
		} else if (DA.colMap[colName].toUpperCase() == 'TIMESTAMP') {
			colObject["width"] = "125px";
		} else if (DA.colMap[colName].toUpperCase() == 'SHORT') {
			colObject["width"] = "40px";
		} else {
			colObject["width"] = "100px";
		}
		if (colName.toUpperCase() == 'FILEPATH') {
			colObject["width"] = "250px";
		}
		return colObject;
	},

	setDBNameForNameNode : function(nameNodeId) {
		if (nameNodeId != null && nameNodeId != '' && nameNodeId != undefined) {
			RemoteManager.getAllDBNameWithTypeForNameNodeMapping(nameNodeId, DA.fillDBName);
		}
	},

	fillDBName : function(dbNameList)
	{
		var data = "";
		if (dbNameList != null) 
		{
			DA.selectedDbName = dbNameList["Metastore"];
			DA.isHive = false;
			DA.lastSelectedDbName = DA.selectedDbName;
			
			if(dbNameList["Metastore"] != null && dbNameList["Metastore"] != "")
				data += '<option value="' + dbNameList["Metastore"] + '">' + dbNameList["Metastore"] + '</option>';
			if(dbNameList["Hive"] != null && dbNameList["Hive"] != "")
				data += '<option value="' + dbNameList["Hive"] + '">' + dbNameList["Hive"] + '</option>';
		}
		
		$('#queryIODatabase').html(data);
		DA.afterReady();
	},

	changeQueryIODbName : function(dbName) {

		Navbar.isAddNewQuery = true;
		if (DA.selectedDbName == '')
			DA.lastSelectedDbName = dbName;
		else
			DA.lastSelectedDbName = DA.selectedDbName;
		DA.selectedDbName = dbName;
		RemoteManager.getAllTagTableNames(DA.selectedNameNode,
				DA.selectedDbName, DA.populateNameNodeFromList);
		
		if(!DA.isHive)
			DA.isHive = true;
		else
			DA.isHive = false;
		
		if(DA.isHive)
		{
			$('#query_filter_table').css('visibility','visible');
		}
		else
		{
			$('#query_filter_table').css('visibility','hidden');
			$('#is_apply_query_filter').removeAttr('checked')
			$('#query_filter_sql').css('visibility','hidden');
			DA.applyQueryFilter();
		}
	},

	afterReady : function() {

		RemoteManager.getAllTagTableNames(DA.selectedNameNode,
				DA.selectedDbName, DA.populateNameNodeFromList);

		DA.isFirstTime = true;
		DA.chartDesignerDirtyBit = false;
		DA.slide();
		DA.SearchReady();
		DA.adjustGridHeight();
		DA.createInitialChartGrid();

		$("#jqgrid_container").height(
				$("#service_ref").height() - ($("#rightPart").height() + 50));
		jQuery(window).bind('resize', function() {
			DA.resizeGrid();
		}).trigger('resize');

		$('#queryIONameNodeId').removeAttr('disabled');

		$("#chartContainer").height($("#chartDiv").height() - 42);
	},

	saveChangeColor : function() {
		var colorArray = [];
		for ( var i = 1; i < 11; i++) {
			colorArray.push($('#color' + i).val());
		}
		DA.closeBox(false);
	},

	loadChartDesigner : function(type) {
		currentDesignerType = type;
		Util.addLightbox("chartDesigner", "resources/chart_designer.html",
				null, null);
	},
	getInitialChartPRObject : function() {
		
		var obj = null
					
		 if(DA.checkForAdded && DA.queryInfo["chartDetail"] != null && DA.queryInfo["chartDetail"] != undefined && DA.queryInfo["chartDetail"]["chart1"] != undefined ){
			var availabelColumn = this.colList;
			for(var i = 0 ; i< availabelColumn.length ; i++)
			{
				var colName = availabelColumn[i];
				for ( var chart in DA.queryInfo["chartDetail"])
				{
					
					if(availabelColumn.indexOf(DA.queryInfo["chartDetail"][chart]["xseries"]) == -1)
					{
						delete DA.queryInfo["chartDetail"][chart];
						continue;
					}
					
					for ( var ycol in DA.queryInfo["chartDetail"][chart]["yseries"]) 
					{
						if(availabelColumn.indexOf(ycol) == -1){
							delete DA.queryInfo["chartDetail"][chart];
						}
						
					}
				}
			}
			DA.checkForAdded = false;
			obj =  DA.queryInfo["chartDetail"];
		}else{
			obj = new Object();
			obj["chartPreferences"] = new Object();
			obj["chartPreferences"] = DA.globalChartPreferences
		}
		 return obj;
			
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
		PR.commonJson["topColors"] = [ "579575", "4BB2C5", "EAA228", "C5B47F", "953579", "4B5DE4", "D8B83F", "990000", "003300", "004a6d" ];

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
				"topColors" : [ "579575", "4BB2C5", "EAA228", "C5B47F", "953579", "4B5DE4", "D8B83F", "990000", "003300", "004a6d" ]
			};
		}

		var seriesArray = [];
		var labels = [];
		var colors = [];

		var colorCode = commonJson["topColors"];
		for ( var i = 0; i < colorCode.length; i++) {
			seriesArray.push([ String.fromCharCode(65 + i), +(6.6) * (i + 1) ]);
			labels.push(String.fromCharCode(65 + i));
			colors.push('#' + colorCode[i]);
		}

		var pos;
		var loc;
		if (prObject["labelJson"]["position"] == "inside") {
			pos = 0.6;
			if(chart["type"] == "bar" || chart["type"] == "tube" || chart["type"] == "cone" || chart["type"] == "pyramid")
				loc = 's';
			else
				loc = 'e';
		} else {
			pos = 1.1;
			if(chart["type"] == "bar" || chart["type"] == "tube" || chart["type"] == "cone" || chart["type"] == "pyramid")
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
			
		}
		else if (chart["type"] == "stock") {
			$('#' + idprefix + 'main_preview_chart').html('<img src="images/stockchart.png"/ style="height: 170px; background: #'+commonJson["clientBackground"]+' ">');
			$('#' + idprefix + 'mainlegendTable').html('<tbody><tr class="jqplot-table-legend" style="background-color: transparent;"><td class="jqplot-table-legend jqplot-table-legend-swatch" style="text-align: center; padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;"><div class="jqplot-table-legend-swatch-outline" style="color: rgb(0, 150, 188);"><div class="jqplot-table-legend-swatch"></div></div></td><td class="jqplot-table-legend jqplot-table-legend-label" style="padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;">Series 1</td></tr></tbody>');
		}
		else if (chart["type"] == "gantt") {
			$('#' + idprefix + 'main_preview_chart').html('<img src="images/ganttchart.png"/ style="height: 170px; background: #'+commonJson["clientBackground"]+' ">');
			$('#' + idprefix + 'mainlegendTable').html('<tbody><tr class="jqplot-table-legend" style="background-color: transparent;"><td class="jqplot-table-legend jqplot-table-legend-swatch" style="text-align: center; padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;"><div class="jqplot-table-legend-swatch-outline" style="color: rgb(0, 150, 188);"><div class="jqplot-table-legend-swatch"></div></div></td><td class="jqplot-table-legend jqplot-table-legend-label" style="padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;">Series 1</td></tr></tbody>');
		}
		else if (chart["type"] == "difference") {
			$('#' + idprefix + 'main_preview_chart').html('<img src="images/differencechart.png"/ style="height: 170px; background: #'+commonJson["clientBackground"]+' ">');
			$('#' + idprefix + 'mainlegendTable').html('<tbody><tr class="jqplot-table-legend" style="background-color: transparent;"><td class="jqplot-table-legend jqplot-table-legend-swatch" style="text-align: center; padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;"><div class="jqplot-table-legend-swatch-outline" style="color: rgb(0, 150, 188);"><div class="jqplot-table-legend-swatch"></div></div></td><td class="jqplot-table-legend jqplot-table-legend-label" style="padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;">Series 1</td></tr></tbody>');
			
//			var l0 = [6,  11, 10, 13, 11,  7];
//		    var l1 = [3,   6,  7,  7,  5,  3];
//		    var l2 = [4,   8,  9, 10, 8,   6];
//		    var l3 = [9,  13, 14, 16, 17, 19];
//		    var l4 = [15, 17, 16, 18, 13, 11];
//		    $.jqplot(plotId, [l0,l3], {
//				title : {
//					show : false
//				},
//				series : [ {
//					showMarker : false,
//					pointLabels : {
//						show : true,
//						location : loc,
//						ypadding : 2,
//						labelsFromSeries : true
//					}
//					
//				} ],
//				seriesDefaults: {
//		            rendererOptions: {
//		                smooth: true
//		            }
//		        },
//				fillBetween: {
//
//		            series1: 1,
//		            series2: 2,
//		            color: "rgba(0,150, 188, 1)",
//		            baseSeries: 0,
//  
//		            fill: true
//		        },
//				grid : {
//					shadow : false,
//					borderWidth : 0.0,
//					background : 'transparent',
//					gridLineColor : prObject["yAxisJson"]["gridline"]["color"],
//					gridLineWidth : prObject["yAxisJson"]["gridline"]["width"]
//				},
//				seriesColors : [ colorCode[0] ],
//				axes : {
//					xaxis : {
//						label : chart["xlegend"],
//						tickOptions : {
//							showGridline : isXGridLineVisible,
//							showLabel : prObject.xAxisJson["visible"],
//						}
//					},
//					yaxis : {
//						label : chart["ylegend"],
//						tickOptions : {
//							showGridline : isYGridLineVisible,
//							showLabel : prObject.yAxisJson["visible"],
//						},
//
//					}
//				},
//				legend : {
//					show : true,
//					location : 'e'
//				},
//			});
//			$(
//					'#'
//							+ idprefix
//							+ 'main_preview_chart div.jqplot-table-legend-swatch-outline')
//					.css("color", "#" + colorCode[0]);
//			
//			
//			$('#' + idprefix + 'main_preview_chart .jqplot-series-shadowCanvas').css("z-index", "10" );
			
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
					showLine:false, 
			        markerOptions: { size: 7, style:"x" }
					
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
			
		}  else if (chart["type"] == "area") {
			
			$.jqplot(plotId , [[11, 9, 5, 12,14]],{
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
			if(loc == 's')
				ypad = 30;
			$.jqplot(plotId , [[0,11,0,0, 9,0,0, 5,0,0, 12,0]],{
				series : [ {
					showMarker : false,
					fill : true,
					pointLabels : {
						show : true,
						location : loc,
						ypadding : ypad,
						 labels:['', '11', '','', '9','','', '5','','','12','']
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
							ticks: [0,5,10,15], 
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
			
			var arr = [
			           [11, 123, 20, "Log"], [45, 92, 20, "csv"], 
			           [24, 104, 20, "ppt"], [40, 63, 20, "doc"], 
			           ];
			            
	           $.jqplot(plotId,[arr],{
	               seriesDefaults:{
	                   renderer: $.jqplot.BubbleRenderer,
	                   rendererOptions: {
	                       bubbleAlpha: 0.6,
	                       highlightAlpha: 0.8,
	                       autoscaleBubbles : false,
		                    autoscalePointsFactor : -1.0
	                   },
	                   shadow: true,
	                   shadowAlpha: 0.05
	               },
	               grid : {
						shadow : false,
						borderWidth : 0.0,
						background : 'transparent',
						gridLineColor : prObject["yAxisJson"]["gridline"]["color"],
						gridLineWidth : prObject["yAxisJson"]["gridline"]["width"]
					},
					//seriesColors : colors,
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

		}  else if(chart["type"] =='meter') {
	        
			$.jqplot(plotId,[[1]],{
			       seriesDefaults: {
			           renderer: $.jqplot.MeterGaugeRenderer,
			           rendererOptions: {
			               background : 'transparent',
			               showTickLabels : prObject["labelJson"]["visible"],
			               ringColor : '#00000',
			               tickColor : '#0096bc',
			               ringWidth : 1.0,
			               labelPosition : 'bottom',
			               
			           },
			           pointLabels: { show: false }
			       },
			       legend : {
						show : true,
						location : 'e'
					},
			   });
			
			$('#' + idprefix + 'main_chart_preview_client_area .jqplot-series-canvas').css('background-color','#'+commonJson["clientBackground"]);
			$('#' + idprefix + 'main_preview_chart div.jqplot-meterGauge-tick').css("z-index", "99" );
	        $('#' + idprefix + 'main_preview_chart div.jqplot-table-legend-swatch-outline').css("color", "#" + colorCode[0] );
		
		}else if (chart["type"] == "bar" || chart["type"] == "tube" ) {
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
			$('#'+ idprefix+ 'main_preview_chart div.jqplot-table-legend-swatch-outline').css("color", "#" + colorCode[0]);

		}   
		else if(chart["type"] == "radar") {
	        $('#' + idprefix + 'main_preview_chart').html('<img src="images/radar.png"/ style="height: 170px; background: #'+commonJson["clientBackground"]+' ">');
	        $('#' + idprefix + 'mainlegendTable').html('<tbody><tr class="jqplot-table-legend" style="background-color: transparent;"><td class="jqplot-table-legend jqplot-table-legend-swatch" style="text-align: center; padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;"><div class="jqplot-table-legend-swatch-outline" style="color: rgb(0, 150, 188);"><div class="jqplot-table-legend-swatch"></div></div></td><td class="jqplot-table-legend jqplot-table-legend-label" style="padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;">Series 1</td></tr></tbody>');
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

		if(chart["type"] != "radar" && chart["type"] != "difference" && chart["type"] != "stock" && chart["type"] != "gantt") {
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
		$('div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick').css("z-index",
				"1000000");
		$('div.jqplot-data-label, div.jqplot-point-label, div.jqplot-bubble-label , div.jqplot-meterGauge-tick').css("text-align",
				prObject["labelJson"]["text-align"]);
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

		
		if(prObject["labelJson"]["position"]=="outside" && chart["type"] == "bubble")
		{
			$('#' + idprefix + 'main_preview_chart div.jqplot-bubble-label ').each(function() {
				var temp = $(this).css("top");
				temp = temp.substring(0,temp.length - 2) - 30;
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

	editChartPreferences : function() {
		for ( var attr in DA.queryInfo["chartDetail"]) {
			if (attr == "chartPreferences")
				continue;
			if (DA.queryInfo["chartDetail"][attr]["title"] == $(
					'#line_chart_title').val()) {
				RC.chartKey = attr;
				RC.chartType = DA.queryInfo["chartDetail"][attr]["type"];
				break;
			}
		}
		RC.chartOperation = 'edit';
		RC.chartPreferenceType = 'local';

		Util.addLightbox("chart_prefernces",
				"resources/chart_preferences.html", null, null);
	},
	showReportPreviewNew : function() {

		var isChecbook = true;
		var group_header_data = '<tr>';
		var group_footer_data = '<tr>';
		var tbl_data = '<tr>';
		var row_data1 = '<tr class="preview-row-data">';
		var row_data2 = '<tr class="preview-row-data">';
		var group_by_data = '';
		
		var colList = '';
		if (DA.searchColumn == '*') {
			colList = DA.colList;
		} else {
			colList = DA.searchColumn;
		}
		var groupByArray = [];
		var groupHeader = DA.queryInfo["groupHeader"];
		var i = 0;
		for ( var attr in groupHeader) {
			var padding = 15 * i;
			var grpHeaderStyle = 'padding-left:' + padding + ';';
			for ( var prop in groupHeader[attr]["style"]) {
				if (prop == "")
					continue;
				if (prop.indexOf("color") != -1)
					grpHeaderStyle += prop + ": #"
							+ groupHeader[attr]["style"][prop] + ";";
				else
					grpHeaderStyle += prop + ":"
							+ groupHeader[attr]["style"][prop] + ";";

			}
			group_by_data += '<tr>';
			group_by_data += '<td colspan="' + (colList.length + 1)
					+ '" style="' + grpHeaderStyle + '">';
			var sampleData = attr;

			if (groupHeader[attr]["style"]["format"] != undefined
					&& groupHeader[attr]["style"]["format"] != ""
					&& groupHeader[attr]["style"]["format"] != null) {
				if (groupHeader[attr]["style"]["format"]["sample"] != undefined
						&& groupHeader[attr]["style"]["format"]["sample"] != ""
						&& groupHeader[attr]["style"]["format"]["sample"] != null) {
					sampleData = groupHeader[attr]["style"]["format"]["sample"];
				}
			}
			group_by_data += groupHeader[attr]["prefix"] + ' '
					+ groupHeader[attr]["function"] + '(' + sampleData + ')  '
					+ groupHeader[attr]["suffix"] + '</td>';
			group_by_data += '</tr>';
			i++;
		}

		for ( var i = 0; i < colList.length; i++) {
			var attr = colList[i] + '';
			var headerVal = '';
			var grpHeaderStyle = '';
			var style = DA.queryInfo["groupHeader"][attr];
			if (style != undefined && style != null) {
				headerVal += style["prefix"] + ' ';
				headerVal += style["function"] + ' ';
				if (style["style"]["format"] != undefined
						&& style["style"]["format"] != ""
						&& style["style"]["format"] != null) {
					if (style["style"]["format"]["sample"] != undefined
							&& style["style"]["format"]["sample"] != ""
							&& style["style"]["format"]["sample"] != null) {
						headerVal += style["style"]["format"]["sample"];
					}
				}
				headerVal += style["suffix"];
				for ( var attr in style["style"]) {
					if (attr == "")
						continue;
					if (attr.indexOf("color") != -1)
						grpHeaderStyle += attr + ": #" + style["style"][attr]
								+ ";";
					else
						grpHeaderStyle += attr + ":" + style["style"][attr]
								+ ";";

				}
			}
			group_header_data += '<td style="' + grpHeaderStyle + '">'
					+ headerVal + '</td>';
		}

		for ( var i = 0; i < colList.length; i++) {
			var attr = colList[i] + '';
			var colTitle = colList[i];
			var style = DA.queryInfo["colDetail"][attr];
			var headerStyle = DA.queryInfo["colHeaderDetail"][attr];
			var headerTitle = attr;
			var styleProp = 'border:1px solid #DDD;';
			var headerStyleProp = 'border:1px solid #DDD;';
			if (style != undefined && style != null) {
				for ( var prop in style) {
					if (prop == "format") {
						if (style[prop] != undefined && style[prop] != ""
								&& style[prop] != null) {
							if (style[prop]["sample"] != undefined
									&& style[prop]["sample"] != ""
									&& style[prop]["sample"] != null) {
								colTitle = style[prop]["sample"];
							}
						}
						continue;
					}
					if (prop.indexOf("color") != -1)
						styleProp += prop + ": #" + style[prop] + ";";
					else
						styleProp += prop + ":" + style[prop] + ";";
				}
			}
			if (headerStyle != undefined && headerStyle != null) {

				for ( var prop in headerStyle) {
					if (prop == "title") {
						if (headerStyle[prop] != "")
							headerTitle = headerStyle[prop];
						continue;
					}
					if (prop.indexOf("color") != -1)
						headerStyleProp += prop + ": #" + headerStyle[prop]
								+ ";";
					else
						headerStyleProp += prop + ":" + headerStyle[prop] + ";";
				}
			}
			tbl_data += '<td style="' + headerStyleProp
					+ '" class="default_header_style">' + headerTitle + '</td>';
			row_data1 += '<td style="' + styleProp + '">' + colTitle + '1</td>';
			row_data2 += '<td style="' + styleProp + '">' + colTitle + '2</td>';
		}
		var groupFooter = DA.queryInfo["groupFooter"];
		for ( var i = 0; i < colList.length; i++) {
			var attr = colList[i] + '';
			var footerVal = '';
			var grpFooterStyle = '';
			var style = groupFooter[attr];
			if (style != undefined && style != null) {
				footerVal += style["prefix"] + ' ';
				footerVal += style["function"] + ' ';
				if (style["style"]["format"] != undefined
						&& style["style"]["format"] != ""
						&& style["style"]["format"] != null) {
					if (style["style"]["format"]["sample"] != undefined
							&& style["style"]["format"]["sample"] != ""
							&& style["style"]["format"]["sample"] != null) {
						footerVal += style["style"]["format"]["sample"];
					}
				}
				footerVal += style["suffix"];
				for ( var attr in style["style"]) {
					if (attr == "")
						continue;
					if (attr.indexOf("color") != -1)
						grpFooterStyle += attr + ": #" + style["style"][attr]
								+ ";";
					else
						grpFooterStyle += attr + ":" + style["style"][attr]
								+ ";";
				}
			}
			group_footer_data += '<td style="' + grpFooterStyle + '">'
					+ footerVal + '</td>';
		}
		group_header_data += '</tr>';
		tbl_data += '</tr>';
		row_data1 += '</tr>';
		row_data2 += '</tr>';
		group_footer_data += '</tr>'

		var data = DA.getChartHtml("groupHeader");
		+group_header_data + tbl_data + row_data1 + group_footer_data
				+ DA.getChartHtml("groupFooter");

		if (groupHeader != "" || groupHeader != undefined) {

			// data = DA.getQueryHeaderCharts(colList.length);
			data = DA.getChartHtml("queryHeader");
			data += tbl_data;

			for ( var i = 1; i < 3; i++) {
				var str1 = group_by_data;

				var rowVal = row_data1;
				data += DA.getChartHtml("groupHeader");
				if (i == 2)
					rowVal = row_data2;
				var j = 0;
				for ( var attr in groupHeader) {
					var headerTitle = attr;
					var padding = 15 * j;

					var grpHeaderStyle = 'padding-left:' + padding + ';';
					for ( var prop in groupHeader[attr]["style"]) {
						if (prop == "") {
							continue;
						}
						if (prop == "format") {
							if (groupHeader[attr]["style"]["format"] != undefined
									&& groupHeader[attr]["style"]["format"] != ""
									&& groupHeader[attr]["style"]["format"] != null) {
								if (groupHeader[attr]["style"]["format"]["sample"] != undefined
										&& groupHeader[attr]["style"]["format"]["sample"] != ""
										&& groupHeader[attr]["style"]["format"]["sample"] != null) {
									headerTitle = groupHeader[attr]["style"]["format"]["sample"];
								}
							}
							continue;
						}
						if (prop.indexOf("color") != -1)
							grpHeaderStyle += prop + ": #"
									+ groupHeader[attr]["style"][prop] + ";";
						else
							grpHeaderStyle += prop + ":"
									+ groupHeader[attr]["style"][prop] + ";";

					}

					data += '<tr>';
					data += '<td colspan="' + (colList.length + 1)
							+ '" style="' + grpHeaderStyle + '">';
					data += groupHeader[attr]["prefix"] + ' '
							+ groupHeader[attr]["function"] + ' ' + headerTitle
							+ '  ' + i + ' ' + groupHeader[attr]["suffix"]
							+ '</td>';
					data += '</tr>';

					var replacer = groupByArray[j] + ' DUMMY';
					rowVal.replace(groupByArray[j] + '', replacer);
					j++;
				}

				data += rowVal;
				data += rowVal;
				data += rowVal;
				if (DA.queryInfo["groupFooter"] != "")
					data += group_footer_data;
				data += DA.getChartHtml("groupFooter");

			}

		}
		data += DA.getChartHtml("queryFooter");

		$('#previewContentTable').html("");
		$('#previewContentTable').html(data);

		$('#queryHeaderPreview').text(
				DA.queryInfo["queryHeader"]["header"]["title"]);

		$('#groupHeaderPreview').text($('#groupHeader').val());

		$('#groupFooterPreview').text($('#groupFooter').val());
		$('#queryFooterPreview').text(
				DA.queryInfo["queryFooter"]["footer"]["title"]);

		DA.setQueryDirtyBitHandlerEvent();
		setTimeout(function() {
			DA.drawReportPreviewCharts()
		}, 100);
		
		var rows = $('tr.preview-row-data');
		for(var i = 0; i< rows.length; i++){
			if (i % 2 == 0){
				$(rows[i]).css('background-color','#EDF3FE');
				$(rows[i]).css('border','1px solid rgb(197, 219, 236)');
			}
		}

	},
	getChartStructure : function() {
		var chartObj = DA.queryInfo["chartDetail"];
		var chartRenderObj = new Object();
		chartRenderObj["queryHeader"] = new Object();
		chartRenderObj["queryFooter"] = new Object();
		chartRenderObj["groupHeader"] = new Object();
		chartRenderObj["groupFooter"] = new Object();
		var row = 1;
		for ( var attr in chartObj) {

			if (chartObj[attr]["position"] == "queryHeader") {
				var obj = new Object();
				row = parseInt(chartObj[attr]["rowPosition"]);
				obj["colspan"] = chartObj[attr]["colSpan"];
				obj["key"] = attr;
				if (chartRenderObj["queryHeader"][row] == null
						|| chartRenderObj["queryHeader"][row] == undefined) {
					chartRenderObj["queryHeader"][row] = new Array();
				}
				chartRenderObj["queryHeader"][row].push(obj)

			} else if (chartObj[attr]["position"] == "queryFooter") {
				var obj = new Object();
				row = parseInt(chartObj[attr]["rowPosition"]);
				obj["colspan"] = chartObj[attr]["colSpan"];
				obj["key"] = attr;
				if (chartRenderObj["queryFooter"][row] == null
						|| chartRenderObj["queryFooter"][row] == undefined) {
					chartRenderObj["queryFooter"][row] = new Array();
				}
				chartRenderObj["queryFooter"][row].push(obj);

			} else if (chartObj[attr]["position"] == "groupHeader") {
				var obj = new Object();
				row = parseInt(chartObj[attr]["rowPosition"]);
				obj["colspan"] = chartObj[attr]["colSpan"];
				obj["key"] = attr;
				if (chartRenderObj["groupHeader"][row] == null
						|| chartRenderObj["groupHeader"][row] == undefined) {
					chartRenderObj["groupHeader"][row] = new Array();
				}
				chartRenderObj["groupHeader"][row].push(obj);

			} else if (chartObj[attr]["position"] == "groupFooter") {
				var obj = new Object();
				row = parseInt(chartObj[attr]["rowPosition"]);
				obj["colspan"] = chartObj[attr]["colSpan"];
				obj["key"] = attr;
				if (chartRenderObj["groupFooter"][row] == null
						|| chartRenderObj["groupFooter"][row] == undefined) {
					chartRenderObj["groupFooter"][row] = new Array();
				}
				chartRenderObj["groupFooter"][row].push(obj);
			}

		}
		return chartRenderObj;
	},
	getChartHtml : function(section) {
		var chartObj = DA.getChartStructure();

		var queryHeaderCharts = chartObj[section];

		var chart_data = '<tr><td colspan="100"><div><table style="width:100%;"><tbody>';
		var i = 0;
		for ( var key in queryHeaderCharts) {
			i++;
			var row = parseInt(key);
			var chartArray = queryHeaderCharts[key];
			while (i < row) {
				chart_data += '<tr><td></td></tr>';
				row--;
			}
			chart_data += '<tr>';
			
			for ( var j = 0; j < chartArray.length; j++) {
				if(j == 0)
					chart_data += '<td colspan="' + chartArray[j]["colspan"] + '">';
				
				var attr = chartArray[j]["key"];
				var chartImg = '';

				chart_data += '<div style="height: '
						+ DA.queryInfo["chartDetail"][attr]["height"]
						+ 'px; width: '
						+ DA.queryInfo["chartDetail"][attr]["width"] + 'px; display:table-cell; padding-right:10px;">';
				var prefix = Util.getUniqueId(4);
				chart_data += DA.getChartTemplate(prefix);
				var chartObj = new Object();
				chartObj["id"] = prefix;
				chartObj["chartKey"] = attr;
				previewCharts.push(chartObj);
				chart_data += '</div>';
			}
			chart_data += '</td></tr>';
		}

		chart_data += '</tbody></table></div></td></tr>';
		return chart_data;
	},
	drawReportPreviewCharts : function() {
		var chartObj = DA.queryInfo["chartDetail"];
		for ( var i = 0; i < previewCharts.length; i++) {
			var key = previewCharts[i]["chartKey"];
			var id = previewCharts[i]["id"];

			DA.showChartSample(id, DA.queryInfo["chartDetail"][key]);
		}
	},

	checkID : function(count) {
		$('#bigQueryIds').find('option').each(function() {
			if ("New Query " + count == $(this).val()) {
				count = DA.checkID(count + 1);
			}
		});
		return (count);
	},

	setGlobalChartPreferences : function(object) {
		if (object == null) {
			RemoteManager.saveChartPreferences(JSON
					.stringify(DA.globalChartPreferences),
					DA.handleSavePreferencesResponse);

		} else {
			DA.globalChartPreferences = jQuery.extend({}, object);
		}
	},
	handleSavePreferencesResponse : function(dwrResponse) {
		
	},
	getChartColSpanDetail : function() {

		var chartObj = DA.getChartStructure();
		var obj = new Object();
		for ( var key in chartObj) {
			var max = 1;
			var section = chartObj[key];
			for ( var attr in section) {
				var array = section[attr];
				if (max < array.length) {
					max = array.length;
				}
			}
			obj[key] = max;
		}
		return obj;
	},
	
	showQueryFilterDiv : function(isShow){
		
		if(isShow){
			$('#query_filter_div').show();
		}else{
			$('#query_filter_div').hide();
			$('#query_filter_where_div').hide();
			$('#query_filter_tables_div').hide();
		}
		
	},
	showQueryFilterTables : function(isShow){
		
		if(isShow){
			$('#query_filter_tables_div').show();
		}else{
			$('#query_filter_tables_div').hide();
		}
	},
	populateQueryFilterTableDiv : function(map, isSettingHistoryObj){
		var list = [];
		

		for ( var tableName in map) {
			list.push(tableName);
		}

		if (list == null || list.length == 0) {
			jAlert(
					"There is no data available for selected namenode.Please upload data from Data Import/Export tab.",
					"Error");
			return;
		}
		
		var data = '<form name="filter_tables" id="filter_tables">';
		data += '<table><tr><td colspan="2"><span>Search On:</span><span id="selectTableClose" class="divcloser"><a href="javascript:DA.showQueryFilterTables(false);"><img src="images/light-box-close.png" class="closerImage"></a></span></td></tr>';
		for ( var i = 0; i < list.length; i++) {
			var tbl_name = list[i];
			if (i == 0) {
				data += '<tr><td><input checked checked="checked" type="radio" name="nnID[]" id="query_filter_by'
						+ tbl_name
						+ '" value="'
						+ tbl_name
						+ '" onclick="DA.setTableForQueryFilter(\''
						+ tbl_name
						+ '\',this.checked);" > </td><td>' + tbl_name + '</td></tr>';
			} else {
				data += '<tr><td><input type="radio" name="nnID[]" id="query_filter_by'
						+ tbl_name
						+ '" value="'
						+ tbl_name
						+ '" onclick="DA.setTableForQueryFilter(\''
						+ tbl_name
						+ '\',this.checked);" > </td><td>' + tbl_name + '</td></tr>';
			}
		}
		data += '</table></form>';
		$('#query_filter_tables_div').html(data);
		
		if(isSettingHistoryObj){
			
			//if setting hobj from history
			var tables = DA.queryFilterObj["selectedTable"];
			for(var i = 0; i< tables.length;i++){
				$('#query_filter_by'+tables[i]).attr('checked','checked');
			}
			
			
		}else{
			
			DA.setTableForQueryFilter();
		}
			
		
	
	},
	setTableForQueryFilter : function(){

		var flag = false;
		var values = [];
		
		if (!flag) {

			var theForm = document.getElementById("filter_tables");
			if (theForm == null || theForm == undefined
					|| theForm.elements == null
					|| theForm.elements == undefined)
				return;

			for ( var i = 0; i < theForm.elements.length; i++) {
				var e = theForm.elements[i];
				if (e.type == 'radio') {
					if (e.checked) {
						values.push(e.value);
					}
				}
			}
		}
		
		$('#query_filter_tables').val(values);
		
		
		DA.queryInfo["queryFilterDetail"]["selectedTable"] = values; 
		
		var metastoreTableName = document.getElementById("queryIODatabase").options[0].value;
		
		RemoteManager.getAllAvailableTagsList(DA.selectedNameNode,
				metastoreTableName, values,
				function(tagListObject){
						DA.populateWhereClauseForQueryFilter(tagListObject,false);
				} );
		DA.showQueryFilterCommand();
	
	},
	populateWhereClauseForQueryFilter : function(tagListObject, isSettingHistoryObj) {
		
		var map = tagListObject["columnMap"];
		var tableSchema = tagListObject["tableSchema"];
		DA.selectedTableSchema = tableSchema;
		if (map == null || map == undefined)
			return;
		var list = new Array();
		
		for ( var attr in map) {
			list.push(attr);
		}
		DA.queryFilterColMap = map;
		
		
		var where_data_table = '<span id="select_wher_query_filter_close" class="divcloser"><a href="javascript:DA.showQueryFilterWhere(false);">'
			+'<img src="images/light-box-close.png" class="closerImage"></a></span> <table id="query_filter_where_tbl">'
			+'<tbody><tr><td nowrap="nowrap">Select Column</td><td nowrap="nowrap">Relational Operator</td><td nowrap="nowrap">Value</td><td nowrap="nowrap">Logical Operator</td></tr>';
		
		for ( var i = 0; i < list.length; i++) {
			where_data_table += '<tr>';
			where_data_table += '<td nowrap="nowrap"><input type="checkbox" name="'
					+ list[i]
					+ '" id="query_filter_whereBy'
					+ list[i]
					+ '" value="'
					+ list[i]
					+ '" onclick="DA.setWhereClauseForQueryFilter(\''
					+ list[i]
					+ '\', this.checked);" > ' + list[i] + '</td>';
			where_data_table += '<td>'
					+ DA.getRelationalOperatorDropDown(list[i],'query_filter_r_operator_','DA.selectRelationalFuncForQueryFilterWhere',DA.queryFilterColMap[list[i]]) + '</td>';
			where_data_table += '<td><input type="text" id="query_filter_whereval_'
					+ list[i]
					+ '" value="" onblur="javascript:DA.makeQueryFilterWhereClause();"></td>';
			where_data_table += '<td>' + DA.getLogicalOperatorDropDown(list[i],'query_filter_loperator_','DA.selectLogicalFuncForQueryFilterWhere')
					+ '</td>';
		}
		
		where_data_table += '</tbody></table>';
		$('#query_filter_where_div').html(where_data_table);
		
		if(isSettingHistoryObj){
			DA.setWhereQueryFilterValues();	
		}
		
	},
	
	selectRelationalFuncForQueryFilterWhere : function(value, columnName){
		DA.makeQueryFilterWhereClause();
		DA.showQueryFilterCommand();
	},
	
	selectLogicalFuncForQueryFilterWhere : function(){
		DA.makeQueryFilterWhereClause();
		DA.showQueryFilterCommand();
	},
	
	
	setWhereClauseForQueryFilter : function(colName, isChecked) {
		DA.enableQueryFilterWhereComponent(colName, isChecked);
		if (!isChecked) {
			$('#query_filter_r_operator_' + colName).val('');
			$('#query_filter_whereval_' + colName).val('');
			$('#query_filter_loperator_' + colName).val('');
		}
		DA.makeQueryFilterWhereClause();
	},
	enableQueryFilterWhereComponent : function(colName, isSelected){
		if (isSelected) 
		{
			$('#query_filter_r_operator_' + colName).removeAttr('disabled');
			$('#query_filter_whereval_' + colName).removeAttr('disabled');
			$('#query_filter_loperator_' + colName).removeAttr('disabled');
			
		} else {
			$('#query_filter_r_operator_' + colName).attr('disabled', 'disabled');
			$('#query_filter_whereval_' + colName).attr('disabled', 'disabled');
			$('#query_filter_loperator_' + colName).attr('disabled', 'disabled');
		}
	},
	
	makeQueryFilterWhereClause : function(){
		
			var frm = document.getElementById('query_filter_where_tbl').getElementsByTagName(
					"input");
			var len = frm.length;
			// 
			var cond = '';
			for (i = 0; i < len; i++)
			{
				if (frm[i].type == "checkbox") {

					if (frm[i].checked) {

						var colName = frm[i].value;
						
						cond += colName;
						cond += $('#query_filter_r_operator_' + colName).val() + ' ';

						var colDataType = DA.queryFilterColMap[colName];
						var operator = $('#query_filter_r_operator_' + colName).val();
						if ((colDataType.toUpperCase() == "STRING" || colDataType.toUpperCase().indexOf("BYTE") != -1 || colDataType.toUpperCase() == 'TIMESTAMP')
								&& operator.toUpperCase().indexOf("IN") == -1
								&& operator.toUpperCase().indexOf("IS NULL") == -1
								&& operator.toUpperCase().indexOf("IS NOT NULL") == -1) {
							cond += " '" + $('#query_filter_whereval_' + colName).val() + "' ";
						} else {
							cond += ' ' + $('#query_filter_whereval_' + colName).val() + ' ';
						}

						cond += $('#query_filter_loperator_' + colName).val() + ' ';
						
						if(!DA.queryInfo["queryFilterDetail"].hasOwnProperty("selectedWhere")){
							DA.queryInfo["queryFilterDetail"]["selectedWhere"] = new Object();
						}
						if (!DA.queryInfo["queryFilterDetail"]["selectedWhere"].hasOwnProperty(colName))
						{
							DA.queryInfo["queryFilterDetail"]["selectedWhere"][colName] = new Object();
						}
						DA.queryInfo["queryFilterDetail"]["selectedWhere"][colName]["roperator"] = $('#query_filter_r_operator_' + colName).val();
						DA.queryInfo["queryFilterDetail"]["selectedWhere"][colName]["loperator"] = $('#query_filter_loperator_' + colName).val();
						DA.queryInfo["queryFilterDetail"]["selectedWhere"][colName]["value"] = $('#query_filter_whereval_' + colName).val();
						

					}
				}
			}
			$('#query_filter_where').val(cond);
			DA.showQueryFilterCommand();
	},
	
	showQueryFilterCommand : function(){
		
		var query = 'SELECT ';
		query += ' filepath ';
		query += ' FROM ' + $('#query_filter_tables').val();
		if ($('#query_filter_where').val() != "") {

			query += ' WHERE ' + $('#query_filter_where').val();
		}
		$('#query_filter_sql').val(query);
		DA.queryInfo["queryFilterDetail"]["filterQuery"] = query;
		
	},
	
	showQueryFilterWhere : function(isShow){
	
		if(isShow){
			$('#query_filter_where_div').show();
		}else{
			$('#query_filter_where_div').hide();
		}
	},
	
	applyQueryFilter : function(){
		
		var isApplyFilter = $('#is_apply_query_filter').is(':checked')
		
		if(isApplyFilter){

				DA.queryInfo["isFilterQuery"] = true;
				$('#query_filter_sql').css('visibility','visible');
				RemoteManager.getQueryFilterTableName(DA.queryInfo["selectedTable"][0],$('#queryIONameNodeId').val(),function(tableName){
					if(tableName == null ||tableName == undefined){
						jAlert("Table not found for input path filter");
						$('#is_apply_query_filter').removeAttr('checked','checked');
						DA.applyQueryFilter();
						return;
					}
					
					var map = new Object();
					map[tableName] = false;
					// Fix for JTL files
					if (DA.stringEndsWith(tableName, "_CSV")) {
						var newTable = tableName.substr(0, tableName.lastIndexOf("_CSV")) + "_JTL";
						map[newTable] = false;
					}
					DA.populateQueryFilterTableDiv(map, false);
					
				});
				
		}else{
			
			DA.queryInfo["isFilterQuery"] = false;
			
			$('#query_filter_sql').css('visibility','hidden');
			$('#query_filter_div').hide();
			$('#query_filter_where_div').hide();
			$('#query_filter_tables_div').hide();
			$('#query_filter_sql').val('');
		}
	},
	
	stringEndsWith : function(str, suffix) {
	    return (str.indexOf(suffix, str.length - suffix.length) !== -1);
	},
	
	setQueryFilterDetail : function(queryFilterObj, tablename)
	{
		DA.queryFilterObj = queryFilterObj;
		$('#query_filter_table').css('visibility','visible');
		$('#is_apply_query_filter').attr('checked','checked');
		$('#query_filter_sql').css('visibility','visible');
		

		
		RemoteManager.getQueryFilterTableName(tablename,$('#queryIONameNodeId').val(),function(tableName){
			if(tableName == null ||tableName == undefined){
				jAlert("Table not found for input path filter");
				$('#is_apply_query_filter').removeAttr('checked','checked');
				DA.applyQueryFilter();
				return;
			}
			var map = new Object();
			map[tableName] = false;
			// Fix for JTL files
			if (DA.stringEndsWith(tableName, "_CSV")) {
				var newTable = tableName.substr(0, tableName.lastIndexOf("_CSV")) + "_JTL";
				map[newTable] = false;
			}
			DA.populateQueryFilterTableDiv(map, false);
			
		});

		var values = DA.queryFilterObj["selectedTable"]
		$('#query_filter_tables').val(values);
		
		var metastoreTableName = document.getElementById("queryIODatabase").options[0].value;
		
		RemoteManager.getAllAvailableTagsList(DA.selectedNameNode,
				metastoreTableName, values,
				function(tagListObject){
					DA.populateWhereClauseForQueryFilter(tagListObject,true);
				});
		
	},
	setWhereQueryFilterValues : function(){
		
		var whereObj = DA.queryFilterObj["selectedWhere"];
		
		for(var colName in whereObj){
			
			DA.enableQueryFilterWhereComponent(colName, true);
			$('#query_filter_whereBy'+colName).attr('checked','checked');
			$('#query_filter_r_operator_' + colName).val(whereObj[colName]["roperator"]);
			$('#query_filter_loperator_' + colName).val(whereObj[colName]["loperator"]);
			$('#query_filter_whereval_' + colName).val(whereObj[colName]["value"]);
		}
		
		DA.makeQueryFilterWhereClause();
	}
	
	

};