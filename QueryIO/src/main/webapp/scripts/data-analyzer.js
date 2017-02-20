DA = {
	queryFilterObj : {},
	queryFilterColMap : {},
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
	chartInfo : null,
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

	showWhereCol : function(element) {
		$('#whereFilters').fadeIn('slow');
		$(
				'#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters,#havingColFilters,#groupByColFilters,#orderByColFilters')
				.hide();
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
			if ((DA.queryInfo["chartDetail"][chart]["xseriesSortType"] != "None")
					&& (DA.queryInfo["chartDetail"][chart]["xseriesSortColumn"] == colName)) {
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
			if ((DA.queryInfo["chartDetail"][chart]["xseriesSortType"] != "None")
					&& (DA.queryInfo["chartDetail"][chart]["xseriesSortColumn"] == colName)) {
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

	persistClicked : function(checked) {
		DA.queryInfo["persistResults"] = checked;
		document.getElementById('resultTableName').disabled = !checked;
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
			Util.addLightbox('viewerLightBox', 'resources/showProcessing.html',
					null, null);
			DA.saveQuery();

		} else {
			DA.executeCommand();

		}

	},
	removeQueryFromCached : function(queryId) {
		var userId = $('#loggedInUserId').text();
		var obj = Util.getCookie("last-visit-query" + userId);
		var idInfoObj = null;
		if (obj != null && obj != undefined) {
			var filePathObj = JSON.parse(obj);
			idInfoObj = JSON.parse(Util.getCookie("last-visit-idInfoMap"
					+ userId));
			for ( var i in idInfoObj) {
				if (idInfoObj[i] == queryId) {

					delete filePathObj[i];
					delete idInfoObj[i];

					var userId = $('#loggedInUserId').text();
					Util.setCookie("last-visit-query" + userId, JSON
							.stringify(filePathObj), 15);
					Util.setCookie("last-visit-idInfoMap" + userId, JSON
							.stringify(idInfoObj), 15);
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
		Util
				.addLightbox("export", "resources/export_big_data.html", null,
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
		for (var i = 0; i < selectedUser.length; i++) {
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
			// jAlert(dwrResponse.responseMessage, "Success");
			// DA.closeBox(true)
			$('#okPopUp').prop('disabled', false);
			$('#processingImg').css('display', 'none')
			$('#successImg').css('display', '');
			$('#popMsg').html(dwrResponse.responseMessage);
			$('#popStatusMsg').html('Success');

		} else {
			// jAlert(dwrResponse.responseMessage, "Failed");
			// DA.closeBox(true)
			$('#okPopUp').prop('disabled', false);
			$('#processingImg').css('display', 'none')
			$('#failImg').css('display', '')
			$('#popMsg').html(dwrResponse.responseMessage);
			$('#popStatusMsg').html('Failed');
		}

	},

	closePopUpBox : function() {
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
		filePath = 'Reports/' + userName + '/' + namenodeId.toLowerCase() + "/"
				+ path;
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

		RemoteManager.isQueryExist($("#queryIdClone").val(), DA.QueryIdexists);
	},

	QueryIdexists : function(response) {
		if (response == true) {
			jAlert("Query ID already exists. Please provide a unique Query ID",
					"Invalid action");
			$("#popup_container").css("z-index", "9999999");
		} else {
			DA.isClone = true;
			Util.addLightbox('export', 'pages/popup.jsp');
			DA.closeCloneBox();
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
				for (var i = 0; i < list.length; i++) {
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
		for (var i = 0; i < source.length; i++) {
			$('#' + to).append(
					'<option value="' + source[i].value + '">'
							+ DA.findUser(source[i].value) + '</option>');
		}
		$('#' + from).children().remove();

	},

	moveSelectedOptions : function(from, to) {
		var source = document.getElementById(from).getElementsByTagName(
				"option");
		for (var i = 0; i < source.length; i++) {
			if (source[i].selected) {
				$('#' + to).append(
						'<option value="' + source[i].value + '">'
								+ DA.findUser(source[i].value) + '</option>');

			}
		}
		$("#" + from + " option:selected").remove();

	},
	findUser : function(val) {
		for (var i = 0; i < DA.userCache.length; i++) {
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

		for (var i = 0; i < selectedUser.length; i++) {
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
			if (DA.currentSelectedChart != null) {
				if (chartObject["title"] == DA.currentSelectedChart) {
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
		DA.selectedChartId = rowData.ID;
		$("#chartOptionsDiv").css('display', '');
		$("#deleteChart").removeAttr("disabled");
		var currTitle = DA.selectedChartId;
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
							for (var i = 0; i < allRowsOnCurrentPage.length; i++)
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
			for (var i = 0; i < allRowsOnCurrentPage.length; i++)
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

	deleteAChart : function() {

		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton = ' No';
		jConfirm('Are you sure you want to delete this Chart ?',
				'Delete Chart', function(val) {
					if (val == true) {
						// callBackFunc = deleteQuery;
						DA.isDelete = true;
						Navbar.chartManagerDirtyBit = false;
						Util.addLightbox('export',
								'resources/delete_chart_box.html');
					} else {
						return;
					}
					jQuery.alerts.okButton = ' Ok ';
					jQuery.alerts.cancelButton = ' Cancel';
				});
		$("#popup_container").css("z-index", "99999999");

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

	setOrderBy : function(value, isChecked) {
		var values = new Array();
		var valuesQuery = [];
		var theForm = document.getElementById("orderByForm");
		if (theForm == null || theForm == undefined || theForm.elements == null
				|| theForm.elements == undefined)
			return;
		for (var i = 0; i < theForm.elements.length; i++) {
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


	createFormattingWizard : function(type) {
		DA.currentType = type;
		Util.addLightbox("formattingWizard", "resources/formatter_wizard.html",
				null, null);
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

	
	selectedColumnDetailArray : [],
	
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

		for (var i = 0; i < list.length; i++) {
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
		RemoteManager.getBigQueryInfo(nameNode, queryId, userName,
				DA.fillSavedQuery);
	},

	setLimitResultRowsState : function(isChecked) {
		DA.queryInfo["setLimitResultRows"] = isChecked;
		if (isChecked)
			$('#limitResultRowsValue').css('display', '');
		else
			$('#limitResultRowsValue').css('display', 'none');

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
		for (var i = 1; i < 11; i++) {
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

		if (DA.checkForAdded && DA.queryInfo["chartDetail"] != null
				&& DA.queryInfo["chartDetail"] != undefined
				&& DA.queryInfo["chartDetail"]["chart1"] != undefined) {
			var availabelColumn = this.colList;
			for (var i = 0; i < availabelColumn.length; i++) {
				var colName = availabelColumn[i];
				for ( var chart in DA.queryInfo["chartDetail"]) {

					if (availabelColumn
							.indexOf(DA.queryInfo["chartDetail"][chart]["xseries"]) == -1) {
						delete DA.queryInfo["chartDetail"][chart];
						continue;
					}

					for ( var ycol in DA.queryInfo["chartDetail"][chart]["yseries"]) {
						if (availabelColumn.indexOf(ycol) == -1) {
							delete DA.queryInfo["chartDetail"][chart];
						}

					}
				}
			}
			DA.checkForAdded = false;
			obj = DA.queryInfo["chartDetail"];
		} else {
			obj = new Object();
			obj["chartPreferences"] = new Object();
			obj["chartPreferences"] = DA.globalChartPreferences
		}
		return obj;

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

		for (var i = 0; i < colList.length; i++) {
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

		for (var i = 0; i < colList.length; i++) {
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
		for (var i = 0; i < colList.length; i++) {
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

			for (var i = 1; i < 3; i++) {
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
		for (var i = 0; i < rows.length; i++) {
			if (i % 2 == 0) {
				$(rows[i]).css('background-color', '#EDF3FE');
				$(rows[i]).css('border', '1px solid rgb(197, 219, 236)');
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

			for (var j = 0; j < chartArray.length; j++) {
				if (j == 0)
					chart_data += '<td colspan="' + chartArray[j]["colspan"]
							+ '">';

				var attr = chartArray[j]["key"];
				var chartImg = '';

				chart_data += '<div style="height: '
						+ DA.queryInfo["chartDetail"][attr]["height"]
						+ 'px; width: '
						+ DA.queryInfo["chartDetail"][attr]["width"]
						+ 'px; display:table-cell; padding-right:10px;">';
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
		for (var i = 0; i < previewCharts.length; i++) {
			var key = previewCharts[i]["chartKey"];
			var id = previewCharts[i]["id"];

			DA.showChartSample(id, DA.queryInfo["chartDetail"][key]);
		}
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

};