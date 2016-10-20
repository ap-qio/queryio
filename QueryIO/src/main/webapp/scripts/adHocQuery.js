AHQ = {
	currentOperation : null,
	selectedType : 'CSV',
	total : 0,
	recordsTotal : 0,
	columnList : null,
	selectedColumnListJSON : [],
	selectedColumnNames : [],
	selectedAdHocId : null,
	selectedNameNodeId : null,
	selectedResourceManagerId : null,
	selectedSourcePath : null,
	selectedParseRecursive : false,
	selectedFilePattern : null,
	selectedPatternExpr : null,
	selectedValueSeparator : null,
	isFirstRowHeader : false,
	canBeViewed : false,
	selectedEncoding : null,
	selectedAdHocIdArray : [],
	allAdHocArray : [],
	onlyOneAdHoc : null,
	headerFile : null,
	jqGridColumnDetails : [],
	index : -1,
	config : 1,
	isSuccess : false,
	colDetails : null,
	selectedEncoding : null,
	isHeader : true,
	selectedDelimeter : null,
	counter : -1,
	currentPage : 1,
	exceptionCase : 1,
	selectedAdHocTableName : null,
	todoRedirect : false,
	selectedFileName : null,
	isFileFilterRegexValid : false,

	FIXED_NO_OF_RECORDS : 10,

	ready : function() {
		var list = {};
		// AHQ.selectedNameNodeId = dwr.util.byId('nameNodeId').value;
		AHQ.populateAdHocQueryTable();
		dwr.util.byId('editButton').disabled = true;
		dwr.util.byId('deleteButton').disabled = true;
	},

	populateAdHocQueryTable : function() {
		$('#adHocTable')
				.dataTable(
						{
							"sScrollX" : "100%",
							"bPaginate" : true,
							"bLengthChange" : true,
							"aLengthMenu" : [ 10, 25, 50, 100 ],
							"sPaginationType" : "full_numbers",
							"bFilter" : false,
							"bDestroy" : true,
							"bSort" : true,
							"bInfo" : true,
							"bAutoWidth" : false,
							"serverSide" : true,
							"searching" : true,
							"aoColumnDefs" : [ {
								"bSortable" : false,
								"aTargets" : [ 0 ]
							} ],
							"fnServerData" : function(sSource, aoData,
									fnCallback, oSettings) {
								// console.log('aoData' , aoData);
								RemoteManager
										.getAdHocQueryInfoAll(
												JSON.stringify(aoData),
												{
													async : false,
													callback : function(result) {
														// console.log('callback'
														// , result);
														fnCallback(AHQ
																.fillAdhocSummaryTable(result));
														$(window).trigger(
																'resize');
													}
												});

							},
							"aoColumns" : [
									{
										"sTitle" : '<input type="checkbox" value="" id="selectAll" onclick="javascript:AHQ.selectAllAdHocRow(this.id)">'
									}, {
										"sTitle" : 'Hive ID'
									}, {
										"sTitle" : "NameNode"
									}, {
										"sTitle" : "ResourceManager"
									}, {
										"sTitle" : "Source Path"
									}, {
										"sTitle" : "Parse Recursive"
									}, {
										"sTitle" : "DataSource Type"
									}, {
										"sTitle" : "Hive Table Name"
									}, {
										"sTitle" : "File Filter Pattern"
									}, {
										"sTitle" : "Encoding"
									}, {
										"sTitle" : "Arguments"
									} ]
						});

		// $('#adHocTable_length').css('margin-bottom', '7px');
		// $('#adHocTable_length').css('margin-top', '7px');

		// adHocTable
		if ($('#adHocTable tbody tr td').hasClass('dataTables_empty'))
			document.getElementById('selectAll').disabled = true;
		else
			document.getElementById('selectAll').disabled = false;

		$('#adHocTable_length').css('margin-top', 7 + 'px');
		$('#adHocTable_length').css('margin-bottom', 7 + 'px');
		$('#adHocTable_filter').css('margin-top', 7 + 'px');
		$('#adHocTable_filter').css('margin-bottom', 7 + 'px');
	},

	fillAdhocSummaryTable : function(adhocQueryDataResult) {
		var tableList = new Array();
		AHQ.total = 0;
		AHQ.allAdHocArray.splice(0, AHQ.allAdHocArray.length);
		var adhocQueryData = adhocQueryDataResult["data"];
		AHQ.recordsTotal = adhocQueryDataResult["recordsTotal"];
		if ((adhocQueryData != null) && (adhocQueryData.length > 0)) {
			AHQ.jqGridColumnDetails.splice(0, AHQ.jqGridColumnDetails.length);
			AHQ.total = adhocQueryData.length;
			for (var i = 0; i < adhocQueryData.length; i++) {
				var adHocQuery = adhocQueryData[i];
				var adHocId = adHocQuery[0] // adHocId;
				AHQ.allAdHocArray.push(adHocId);
				var check = '<input type="checkbox" id="'
						+ adHocId
						+ '" onclick="javascript:AHQ.clickAdHocCheckBox(this.id)">';
				var namenodeId = adHocQuery[1] // namenodeId
				var namenodeIdSpan = '<span id="namenode_' + adHocId + '">'
						+ namenodeId + '</span>';
				var rmId = adHocQuery[2] // ResourceManagerId;
				var sourcePath = adHocQuery[3] // sourcePath;
				var parseRecursive = adHocQuery[4] // parseRecursive;
				var type = adHocQuery[5] // type;
				var adHocTableName = adHocQuery[6] // adHocTableName;
				var filePathPattern = adHocQuery[7] // filePathPattern;
				var colDetails = adHocQuery[8] //columnDetails
				var encoding = adHocQuery[9] // encoding;
				var pattern = adHocQuery[10] // arguments;

				tableList.push([ check, adHocId, namenodeIdSpan, rmId,
						sourcePath, parseRecursive, type, adHocTableName,
						filePathPattern, encoding, pattern ]);
				AHQ.jqGridColumnDetails.push(colDetails);
				console.log('jqGridColumnDetails : ',AHQ.jqGridColumnDetails);
			}
			adhocQueryDataResult["data"] = tableList;
		}
		return adhocQueryDataResult;
	},
	clickAdHocCheckBox : function(id) {

		var flag = document.getElementById(id).checked;

		if (flag == true) {
			AHQ.selectedAdHocIdArray.push(id.toString());
		} else {
			var index = jQuery.inArray(id.toString(), AHQ.selectedAdHocIdArray);
			if (index != -1) {
				AHQ.selectedAdHocIdArray.splice(index, 1);
			}
		}
		if (AHQ.selectedAdHocIdArray.length == 1 && flag) {
			AHQ.onlyOneAdHoc = id;
		}
		if (($('#adHocTable tr').length - 1) == AHQ.selectedAdHocIdArray.length) {
			document.getElementById("selectAll").checked = flag;
			AHQ.selectAllAdHocRow("selectAll", flag);
		} else
			AHQ.toggleButton(id, flag, "selectAll");

	},

	autoSelectedClicked : function() {
		$('#rec1').css('display', '');
		AHQ.config = 1;
		if (AHQ.selectedType == 'JSON') {
			$('#jsonSampleFile').css('display', '');
			$('#hasRecordsInJSON').css('display', '');
		}
	},

	manualConfigClicked : function() {
		$('#rec1').css('display', 'none');
		AHQ.config = 2;
		if (AHQ.selectedType == 'JSON') {
			$('#jsonSampleFile').css('display', 'none');
			$('#hasRecordsInJSON').css('display', 'none');
		}

	},

	selectAllAdHocRow : function(id) {
		var flag = document.getElementById(id).checked;

		AHQ.selectedAdHocIdArray.splice(0, AHQ.selectedAdHocIdArray.length);
		for (var i = 0; i < AHQ.allAdHocArray.length; i++) {
			document.getElementById(AHQ.allAdHocArray[i]).checked = flag;
			if (flag) {
				AHQ.selectedAdHocIdArray.push(AHQ.allAdHocArray[i]);
			}
		}
		AHQ.toggleButton(id, flag);
	},

	toggleButton : function(id, value) {

		if (id == "selectAll") {
			if (AHQ.selectedAdHocIdArray.length == 1) {
				dwr.util.byId('addButton').disabled = false;
				dwr.util.byId('editButton').disabled = false;
				dwr.util.byId('deleteButton').disabled = false;
			} else {
				dwr.util.byId('editButton').disabled = true;
			}
			dwr.util.byId('addButton').disabled = !value;
			dwr.util.byId('deleteButton').disabled = !value;
		} else {
			if (value == false)
				$('#selectAll').attr("checked", false);

			if (AHQ.selectedAdHocIdArray.length < 1) {
				dwr.util.byId('editButton').disabled = true;
				dwr.util.byId('deleteButton').disabled = true;
			} else {
				if (AHQ.selectedAdHocIdArray.length == 1) {
					dwr.util.byId('editButton').disabled = false;
					dwr.util.byId('addButton').disabled = false;
					dwr.util.byId('deleteButton').disabled = false;
				} else {
					dwr.util.byId('editButton').disabled = true;
				}
				dwr.util.byId('deleteButton').disabled = false;
				dwr.util.byId('addButton').disabled = false;
			}
		}
		dwr.util.byId('addButton').disabled = false;
	},

	addAdHocEntry : function() {
		AHQ.currentOperation = 'add';
		Util.addLightbox('ahq_Box', 'resources/add_AdHocQuery.html');
	},

	editAdHocEntry : function() {
		if (AHQ.selectedAdHocIdArray.length == 1) {
			AHQ.currentOperation = 'edit';
			Util.addLightbox('ahq_Box', 'resources/editAdHoc.html');
		}
	},

	deleteAdHocEntry : function() {
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton = ' No';
		jConfirm('Are you sure you want to delete selected adHoc entries?', '',
				function(val) {
					if (val == true) {
						AHQ.currentOperation = 'delete';
						Util.addLightbox("ahq_Box", "pages/popup.jsp");
					} else {
						return;
					}
					jQuery.alerts.okButton = ' Ok ';
					jQuery.alerts.cancelButton = 'Cancel';
				});
		$("#popup_container").css("z-index", "99999999");
	},

	fillClusterNameNodeID : function() {
		RemoteManager
				.getAllNameNodeForDBNameMapping(AHQ.populateClusterNameNodeIds);
	},

	populateClusterNameNodeIds : function(list) {
		var data = '';

		data = Util.getCurrentIdDropDown(list, "Select NameNode",
				"Select NameNode");

		$('#nameNodeId').html(data);
		AHQ.fillResourceManagerIds();
	},

	fillResourceManagerIds : function() {
		RemoteManager.getAllResourceManagers(AHQ.populateResourceManager);
	},

	populateResourceManager : function(list) {
		var data = '';

		data = Util.getCurrentIdDropDown(list, "Select ResourceManager",
				"Select ResourceManager");

		$('#resourceManagerId').html(data);

		if (AHQ.currentOperation == 'edit') {
			AHQ.fillEditform();
		}
	},

	fillEditform : function() {
		var rows = $('table#adHocTable > tbody > tr');
		for (var i = 0; i < rows.length; i++) {
			var adHocId = $(rows[i]).find("td:eq(1)").html();
			if ((document.getElementById(adHocId).checked)) {
			//	console.log('AdhocId : ',adHocId);
				var nnId = $(rows[i]).find("td:eq(2) span").text();
				var rmId = $(rows[i]).find("td:eq(3)").html();
				var sourcePath = $(rows[i]).find("td:eq(4)").html();
				var type = $(rows[i]).find("td:eq(5)").html();
				var fileType = $(rows[i]).find("td:eq(6)").html();
				var tablename = $(rows[i]).find("td:eq(7)").html();
				var pattern = $(rows[i]).find("td:eq(8)").html();
				var encoding = $(rows[i]).find("td:eq(9)").html();

				$('#adHocId').val(adHocId);
				$("#adHocId").prop("disabled", true);
				$('#adHocType').val(fileType);
				document.getElementById('adHocType').disabled = true;
				$('#nameNodeId').val(nnId);
				$('#resourceManagerId').val(rmId);
				$('#sourcePath').val(sourcePath);
				$('#nameNodeId').prop('disabled', true);
				$('#sourcePath').prop('disabled', true);
				$('#adHocTableName').val(tablename);
				document.getElementById('adHocTableName').disabled = true;

				$('#filePattern').val(pattern);
				$('#encoding').val(encoding);

				AHQ.index = i;
				break;
			}
		}
	},

	changeOptions : function(val) {
		AHQ.selectedType = val;
		AHQ.config = 1;
		$('#headerFile').val('');
		$("#regexField").hide();
		$('#records').val('100');
		$("#hasRecords").show();

		if (AHQ.selectedType == 'CSV') {
			$('#log').css('display', 'none');
			$('#deli').css('display', '');
			$('#sepa').css('display', '');
			$('#hasHeaderId').css('display', '');
			$('#radio').css('display', '');
			$('#rec1').css('display', '');
			$('#sourcePath').val('');

			document.getElementById('filePattern').value = '.*\\\.csv';

			$('#adHocTableName').val("HiveCSVTable" + (AHQ.total + 1));
		} else if (AHQ.selectedType == 'LOG' || AHQ.selectedType == 'ACCESSLOG') {
			$('#log').css('display', '');
			$('#deli').css('display', 'none');
			$('#sepa').css('display', 'none');
			$('#hasHeaderId').css('display', 'none');
			$('#radio').css('display', '');

			if (AHQ.selectedType == 'LOG') {
				$('#adHocTableName').val("HiveLOG4JTable" + (AHQ.total + 1));
				document.getElementById('filePattern').value = '.*\\.log';

				$('#sourcePath').val('');
			} else {
				$('#adHocTableName')
						.val("HiveApacheLogTable" + (AHQ.total + 1));
				document.getElementById('filePattern').value = '.*\\.accesslog';
			}
		} else if (AHQ.selectedType == 'JSON') {
			document.getElementById('filePattern').value = '.*\\.json';
			$('#adHocTableName').val("HiveJSONTable" + (AHQ.total + 1));
			$('#sourcePath').val('');
		} else if (AHQ.selectedType == 'IISLOG') {
			$('#log').css('display', 'none');
			$('#deli').css('display', '');
			$('#sepa').css('display', 'none');
			$('#hasHeaderId').css('display', '');
			$('#radio').css('display', '');
			$('#rec1').css('display', '');
			document.getElementById('filePattern').value = '.*\\.iislog';
			$('#adHocTableName').val("HiveIISLOGTable" + (AHQ.total + 1));

			$('#sourcePath').val('');
		} else if (AHQ.selectedType == 'REGEX') {
			$("#regexField").show();
			document.getElementById('filePattern').value = '.*\.';
			$('#adHocTableName').val("HiveREGEXTable" + (AHQ.total + 1));
		} else if (AHQ.selectedType == 'PAIRS') {
			$('#hasHeaderId').css('display', 'none');
			$('#deli').css('display', '');
			$('#sepa').css('display', '');
			$('#delimiterText1').text('Text Seperator');
			// $('#value_sepera').text('Key Value Seperator');
			document.getElementById('filePattern').value = '.*\\.txt';
			$('#adHocTableName')
					.val("HiveKeyValuePairsTable" + (AHQ.total + 1));
		} else if (AHQ.selectedType == 'MBOX') {
			$('#log').css('display', 'none');
			$('#deli').css('display', 'none');
			$('#sepa').css('display', 'none');
			$('#hasHeaderId').css('display', 'none');
			$('#radio').css('display', '');
			document.getElementById('filePattern').value = '.*\\.eml';
			$('#adHocTableName').val("HiveMBOXTable" + (AHQ.total + 1));
			$("#hasRecords").hide();

			$('#sourcePath').val('');
		} else if (AHQ.selectedType == 'SEQUENCE') {
			$('#adHocTableName').val(
					"HiveSequenceMetaDataTable" + (AHQ.total + 1));
			document.getElementById('filePattern').value = '.*\\.seq';
		} else if (AHQ.selectedType == 'XML') {
			$('#deli').css('display', '');
			$('#delimiterText1').text('XML Node Name*');
			$('#sepa').css('display', 'none');
			$('#hasHeaderId').css('display', 'none');
			document.getElementById('filePattern').value = '.*\\.xml';
			$('#adHocTableName').val("HiveXMLTable" + (AHQ.total + 1));

			$('#records').val('10');
		}

	},
	setEncoding : function(val) {
		AHQ.selectedEncoding = val;
	},

	showJsonTextArea : function() {
		$('#textBoxForJSON').css('display', '');
	},
	removeJsonTextArea : function() {
		$('#textBoxForJSON').css('display', 'none');
	},
	isValidJSON : function(str) {
		try {
			var json = jQuery.parseJSON(str);
			return true;
		} catch (e) {
			return false;
		}
	},
	process : function(data) {
		SetTab();
		window.IsCollapsible = $id("CollapsibleView").checked;
		var json = $id("RawJson").value;
		var html = "";
		try {
			if (json == "")
				json = "\"\"";
			var obj = eval("[" + json + "]");
			html = ProcessObject(obj[0], 0, false, false, false);
			$id("Canvas").innerHTML = "<PRE class='CodeContainer'>" + html
					+ "</PRE>";
		} catch (e) {
			alert("JSON is not well formated:\n" + e.message);
			$id("Canvas").innerHTML = "";
		}
	},
	processJSON : function(json) {
		var length = json.length;
	},
	previewJSON : function() {
		var str = document.getElementById('pastedJson').value;
		var isValid = AHQ.isValidJSON(str);
		if (isValid) {
			var json = jQuery.parseJSON(str);
			var data = json;
			AHQ.processJSON(json);
			var data = [ {
				label : 'node1',
				children : [ {
					label : 'child1'
				}, {
					label : 'child2'
				} ]
			}, {
				label : 'node2',
				children : [ {
					label : 'child3'
				} ]
			} ];
			$('#jsonTreeViewer').css('display', '');

			$(function() {
				$('#jsonTreeViewer').tree({
					data : data
				});
			});
		} else {
			jAlert("Entered JSON is invalid.Please insert correct JSON.",
					"Incorrect Details");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
	},
	updateEntry : function() {
		if (AHQ.isFieldsInvalid()) {
			jAlert("Fields specified are not complete or invalid.",
					"Insufficient Details");
			$("#popup_container").css("z-index", "9999999");
			return;
		} else {
			AHQ.selectedAdHocId = dwr.util.byId('adHocId').value;
			AHQ.selectedResourceManagerId = dwr.util.byId('resourceManagerId').value;
			AHQ.selectedFilePattern = dwr.util.byId('filePattern').value;
			AHQ.selectedEncoding = dwr.util.byId('encoding').value;
			AHQ.selectedNameNodeId = dwr.util.byId('nameNodeId').value;
			Util.removeLightbox('ahq_Box');
			Util.addLightbox('ahq_Box', 'pages/popup.jsp');
		}
	},

	saveEntry : function() {
		$('#error_span').hide();
		$('#error_span').text('');

		// for (i = 1; i <
		// document.getElementById('sample_table').rows[0].cells.length-1; i++)
		// {
		// var header = document.getElementById('header_'+i).value;
		// if(!(/^[a-zA-Z][0-9a-zA-Z]*$/.test(header)))/* &&
		// !(/^[0-9]+$/.test(header)) &&
		// !(document.getElementById('dataType_'+i).value.match("STRING")))*/
		// {
		// jAlert("Please provide proper column name.","Column Name");
		// $("#popup_container").css("z-index","9999999");
		// return;
		// }
		// }
		if ((AHQ.config == 2) && (AHQ.isFieldsInvalid())) {
			jAlert(
					"Fields specified are not complete or invalid. Add atleast one column with FILEPATH",
					"Insufficient Details");
			$("#popup_container").css("z-index", "9999999");
			return;
		}

		else {
			AHQ.selectedAdHocId = dwr.util.byId('adHocId').value;
			AHQ.selectedNameNodeId = dwr.util.byId('nameNodeId').value;
			AHQ.selectedResourceManagerId = dwr.util.byId('resourceManagerId').value;
			AHQ.selectedType = dwr.util.byId('adHocType').value;
			AHQ.selectedSourcePath = dwr.util.byId('sourcePath').value;
			AHQ.selectedParseRecursive = dwr.util.byId('parseRecursive').checked;
			AHQ.selectedFilePattern = dwr.util.byId('filePattern').value;
			AHQ.selectedEncoding = dwr.util.byId('encoding').value;
			if (AHQ.selectedType == 'LOG' || AHQ.selectedType == 'ACCESSLOG') {
				AHQ.selectedPatternExpr = dwr.util.byId('log_pattern').value;
			} else if (AHQ.selectedType == 'REGEX') {
				AHQ.selectedPatternExpr = dwr.util.byId('regex').value;
			} else {
				AHQ.selectedPatternExpr = dwr.util.byId('delimiterValue').value;
			}
			AHQ.selectedValueSeparator = dwr.util.byId('separatorValue').value;
			AHQ.isFirstRowHeader = document.getElementById('isFirstLineHeader').checked;
			if (AHQ.config == 1) {
				var json = AHQ.colDetails;
				var count = 0;
				// for(var key in json[0]['meta']['details'])
				var j = 0;
				// if($('#adHocType').val() == 'CSV' || $('#adHocType').val() ==
				// 'JSON' || $('#adHocType').val() == 'IISLOG' ||
				// $('#adHocType').val() == 'ACCESSLOG')
				// j=1;

				for (var i = j; i < json[0]['meta']['details'].length; i++) {
					if (Util.isContainSpecialCharButNotUnderscore(document
							.getElementById('header_' + i).value)) {
						$('#error_span')
								.text(
										"Value"
												+ document
														.getElementById('header_'
																+ i).value
												+ "contains special character. Please remove special character from column name.");
						$('#error_span').show();
						return;
					}
					if (!(/^[a-zA-Z][0-9a-zA-Z_]*$/.test(document
							.getElementById('header_' + i).value))) {
						$('#error_span')
								.text(
										"Coloumn name : "
												+ document
														.getElementById('header_'
																+ i).value
												+ " should start with alphabet.");
						$('#error_span').show();
						return;
					}
					var obj = json[0]['meta']['details'][i];
					if (document.getElementById('textBox_' + i).value == null
							|| document.getElementById('textBox_' + i).value == undefined
							|| document.getElementById('textBox_' + i).value == "") {
						if (document.getElementById('dataType_' + i).value == "STRING") {
							jAlert("Size must be specified.",
									"Insufficient Details");
							$("#popup_container").css("z-index", "9999999");
							return;
						} else
							obj["type"] = document.getElementById('dataType_'
									+ i).value;
					}

					else
						obj["type"] = document.getElementById('dataType_' + i).value
								+ "("
								+ document.getElementById('textBox_' + i).value
								+ ")";

					// obj["isInclude"]=document.getElementById('include_'+i).checked;
				}
				var i = 0;

				for ( var key in json[0]['meta']['header']) {
					if (json[0]['meta']['header'][key] != null
							&& document.getElementById('header_' + i) != null) {
						var header = document.getElementById('header_' + i).value;
						var value = json[0]['meta']['header'][key];
						json[0]['meta']['header'][i] = header;
					}
					i++;
				}
				AHQ.colDetails = null;
				AHQ.colDetails = json;
				if (AHQ.isFieldsInvalidDataTable()) {
					jAlert("Fields specified are not complete or invalid.",
							"Insufficient Details");
					$("#popup_container").css("z-index", "9999999");
					return;
				}
			}
			// }

			AHQ.selectedAdHocTableName = dwr.util.byId('adHocTableName').value;
			Util.removeLightbox('ahq_Box');
			Util.addLightbox('ahq_Box', 'pages/popup.jsp');
		}
	},

	isFieldsInvalid : function() {
		AHQ.columnList = jQuery("#columnListGrid").jqGrid('getRowData');
		AHQ.selectedColumnListJSON = new Array();
		var isInvalid = true;
		//console.log('columnList : ', AHQ.columnList.length);
		for (var i = 0; i < AHQ.columnList.length; i++) {
			var columnRowData = {
				"colName" : AHQ.columnList[i].colName,
				"colType" : AHQ.columnList[i].colType,
				"colIndex" : AHQ.columnList[i].colIndex
			};
			isInvalid = false;
			AHQ.selectedColumnListJSON.push(columnRowData);

		}
		if (isInvalid)
			return true;
		else
			return false;

	},

	isFieldsInvalidDataTable : function() {
		AHQ.selectedColumnListJSON = new Array();
		var json = AHQ.colDetails;

		for (var i = 0; i < json[0]['meta']['details'].length; i++) {
			// var isInclude = json[0]['meta']['details'][i]['isInclude'];
			// if (isInclude)
			// {
			var index = json[0]['meta']['details'][i]['index'];
			var type = json[0]['meta']['details'][i]['type'];
			// var header = AHQ.selectedColumnNames[i];
			var header = json[0]['meta']['header'][i];

			var columnRowData = {
				"colName" : header,
				"colType" : type,
				"colIndex" : index
			};
			AHQ.selectedColumnListJSON.push(columnRowData);
			// }
		}

		return false;
	},

	fillColumnsByJson : function(list) {
		if (list != null) {
			var fields = jQuery.parseJSON(list);
			for (var i = 0; i < fields.length; i++) {
				var columnObject = fields[i];
				jQuery("#columnListGrid").jqGrid('addRowData', i + 1, ({

					colName : columnObject.colName,
					colType : columnObject.colType,
					colIndex : columnObject.colIndex,
				}));
			}

			$("#columnListGrid").jqGrid().setGridParam({
				sortorder : "asc",
				sortname : "colIndex"
			}).trigger("reloadGrid");
		}
	},

	fillColumnTableInfo : function(list) {
		if (list != null) {
			fields = list.fields;
			for (var i = 0; i < fields.length; i++) {
				var columnObject = fields[i];
				jQuery("#columnListGrid").jqGrid('addRowData', i + 1, ({

					colName : columnObject.colName,
					colType : columnObject.colType,
					colIndex : columnObject.colIndex,
				}));
			}

			$("#columnListGrid").jqGrid().setGridParam({
				sortorder : "asc",
				sortname : "colIndex"
			}).trigger("reloadGrid");
		}
	},

	createColumnTable : function() {
		var lastSel = "";
		$('#columnListGrid').remove();
		$('#columnListPager').remove();
		$('#columnListContainer')
				.html(
						'<table id="columnListGrid"></table><div id="columnListPager"></div>');

		var wd = ($("#columnListContainer").width() - 14) / 3;

		jQuery("#columnListGrid")
				.jqGrid(
						{

							datatype : "local",
							colNames : [ 'Column Name', 'Column Type', 'Index' ],
							colModel : [
									{
										name : 'colName',
										key : true,
										index : 'colName',
										width : wd,
										align : "left",
										editable : true,
										sorttype : "String",
										editrules : {
											required : true
										}
									},
									{
										name : 'colType',
										index : 'colType',
										width : wd,
										align : "left",
										editable : true,
										edittype : "select",
										editoptions : {
											value : "INTEGER:INTEGER; STRING(250):STRING(250); TIMESTAMP:TIMESTAMP"
										},
										editrules : {
											required : true
										},
										formoptions : {
											elmsuffix : "<span style= 'color: red;'>*</span>"
										}
									}, {
										name : 'colIndex',
										index : 'colIndex',
										width : wd,
										align : "right",
										editable : true,
										sorttype : "int"
									} ],
							pager : '#columnListPager',
							altRows : true,
							viewrecords : true,
							sortable : true,
							sortname : 'colIndex',
							sortorder : "asc",

							rowList : [],
							pgbuttons : false,
							pgtext : null,
							pagination : false,

							height : ($("#columnListContainer").height() - 25),
							width : ($("#columnListContainer").width()),
							shrinkToFit : false,
							caption : "",
							loadui : "disable",

							loadComplete : function(data) {
							},

							onSortCol : function(index, idxcol, sortorder) {
								if (this.p.lastsort >= 0
										&& this.p.lastsort !== idxcol
										&& this.p.colModel[this.p.lastsort].sortable !== false) {
									// show the icons of last sorted column
									$(this.grid.headers[this.p.lastsort].el)
											.find(
													">div.ui-jqgrid-sortable>span.s-ico")
											.show();
									$(this.grid.headers[this.p.lastsort].el)
											.removeClass('ui-state-highlight');
								}
								$(this.grid.headers[idxcol].el).addClass(
										'ui-state-highlight');
							},

							onSelectRow : function(id) {
								// if(id && id != lastSel)
								// {
								// jQuery('#columnListGrid').saveRow(lastSel);
								// lastSel = id;
								// }
								// jQuery('#columnListGrid').editRow(id, true);
							},

							editurl : "adHocQuery.do?type=doNothing"
						});

		jQuery("#columnListGrid").jqGrid('navGrid', "#columnListPager", {
			edit : false,
			add : false,
			del : true
		}, {}, {}, {
			afterShowForm : function(formid) {
				$("#delmodcolumnListGrid").css('z-index', '9500000');
			}
		}, {});

		jQuery("#columnListGrid").jqGrid('inlineNav', "#columnListPager", {
			edit : true,
			editicon : "ui-icon-pencil",
			add : true,
			addicon : "ui-icon-plus",
			save : true,
			saveicon : "ui-icon-disk",
			cancel : true,
			cancelicon : "ui-icon-cancel",
			addParams : {
				useFormatter : false,
				keys : true,
				aftersavefunc : function() {
					var grid = $("#columnListGrid");
					grid.trigger("reloadGrid");
				}
			},
			editParams : {
				useFormatter : false,
				keys : true,
				aftersavefunc : function() {
					var grid = $("#columnListGrid");
					grid.trigger("reloadGrid");
				}
			}
		});

		$("#search_columnListGrid").hide();
		$("#refresh_columnListGrid").hide();

		$("#columnListPager_center").css('display', 'none');
		$("#pg_columnListPager").css('height', '20px');

		$(jQuery("#columnListGrid")[0].grid.headers[0].el).addClass(
				'ui-state-highlight'); // Highlight first column header on grid
		// load.

		// AHQ.fillColumnsByJson(AHQ.jqGridColumnDetails[0]);

		if (AHQ.currentOperation == 'add')
			RemoteManager.getDefaultColumns(AHQ.selectedType,
					AHQ.fillColumnTableInfo);
	},

	createColumnTableForEdit : function() {
		var lastSel = "";
		$('#columnListGrid').remove();
		$('#columnListPager').remove();
		$('#columnListContainer')
				.html(
						'<table id="columnListGrid"></table><div id="columnListPager"></div>');

		var wd = ($("#columnListContainer").width() - 20) / 3;

		jQuery("#columnListGrid")
				.jqGrid(
						{

							datatype : "local",
							colNames : [ 'Column Name', 'Column Type', 'Index' ],
							colModel : [
									{
										name : 'colName',
										key : true,
										index : 'colName',
										width : wd,
										align : "left",
										editable : true,
										sorttype : "String",
										editrules : {
											required : true
										}
									},
									{
										name : 'colType',
										index : 'colType',
										width : wd,
										align : "left",
										editable : true,
										edittype : "select",
										editoptions : {
											value : "INTEGER:INTEGER; STRING(250):STRING(250); TIMESTAMP:TIMESTAMP"
										},
										editrules : {
											required : true
										},
										formoptions : {
											elmsuffix : "<span style= 'color: red;'>*</span>"
										}
									}, {
										name : 'colIndex',
										index : 'colIndex',
										width : wd,
										align : "right",
										editable : true,
										sorttype : "int"
									} ],
							// pager: '#columnListPager',
							altRows : true,
							viewrecords : true,
							sortable : true,
							sortname : 'colIndex',
							sortorder : "asc",

							rowList : [],
							pgbuttons : false,
							pgtext : null,
							pagination : false,

							height : ($("#columnListContainer").height() - 25),
							width : ($("#columnListContainer").width() - 5),
							shrinkToFit : false,
							caption : "",
							loadui : "disable",

							loadComplete : function(data) {
							},

							onSortCol : function(index, idxcol, sortorder) {
								if (this.p.lastsort >= 0
										&& this.p.lastsort !== idxcol
										&& this.p.colModel[this.p.lastsort].sortable !== false) {
									// show the icons of last sorted column
									$(this.grid.headers[this.p.lastsort].el)
											.find(
													">div.ui-jqgrid-sortable>span.s-ico")
											.show();
									$(this.grid.headers[this.p.lastsort].el)
											.removeClass('ui-state-highlight');
								}
								$(this.grid.headers[idxcol].el).addClass(
										'ui-state-highlight');
							},

							editurl : "adHocQuery.do?type=doNothing"
						});

		// jQuery("#columnListGrid").jqGrid('navGrid',"#columnListPager",
		// {edit:false, add:true, del:true},
		// { // edit option
		// onclickSubmit : function (params, postdata)
		// {
		// return postdata;
		// },
		// afterShowForm : function (formid)
		// {
		// $("#editmodcolumnListGrid").css('z-index','9500000');
		// },
		// viewPagerButtons : false,
		// closeAfterEdit : true,
		// reloadAfterSubmit : false,
		// width : 230,
		// resize : false,
		// recreateForm : true,
		// closeOnEscape : true
		// },
		// { // add option
		// onclickSubmit : function (params, postdata)
		// {
		// return postdata;
		// },
		// afterShowForm : function (formid)
		// {
		// $("#editmodcolumnListGrid").css('z-index','9500000');
		// },
		// closeAfterAdd : true,
		// reloadAfterSubmit : false,
		// width : 230,
		// resize : false,
		// recreateForm : true,
		// closeOnEscape : true
		// },
		// { // delete option
		// afterShowForm : function (formid)
		// {
		// $("#delmodcolumnListGrid").css('z-index','9500000');
		// },
		// resize : false,
		// closeOnEscape : true
		// },
		// { // search option
		// resize : false,
		// closeOnEscape : true
		// }
		// );

		$("#columnListPager_center").css('display', 'none');
		$("#pg_columnListPager").css('height', '20px');

		$(jQuery("#columnListGrid")[0].grid.headers[0].el).addClass(
				'ui-state-highlight'); // Highlight first column header on grid
		// load.

		var rows = $('table#adHocTable > tbody > tr');
		for (var i = 0; i < rows.length; i++) {
			var adHocId = $(rows[i]).find("td:eq(1)").html();			
			if ((document.getElementById(adHocId).checked))
				{
					AHQ.fillColumnsByJson(AHQ.jqGridColumnDetails[i]);
				}
		  }

	},
	showActivatedTabs : function() {

		$("#tabs li.active").removeClass('active');
		if (AHQ.currentPage == 1) {
			$("#liGeneral").addClass("active");
		} else if (AHQ.currentPage == 2) {
			$("#liConfig").addClass("active");
		} else if (AHQ.currentPage == 3) {
			$("#liSchema").addClass("active");
		}
	},
	closeBox : function(isRefresh) {
		Util.removeLightbox('ahq_Box');

		if (AHQ.todoRedirect) {
			Navbar.hiveViewNameNode = AHQ.selectedNameNodeId;
			Navbar.hiveViewSelectedTable = AHQ.selectedAdHocTableName
					.toLowerCase();
			Navbar.currentHiveTabSelected = 'ui-tabs-2'; /*
															 * for
															 * 'databasesHiveHead'; //
															 * move to hive
															 * database view.
															 */
			AHQ.todoRedirect = false;
		}

		AHQ.currentOperation = null;
		AHQ.selectedType = 'CSV';
		AHQ.columnList = null;
		AHQ.selectedColumnListJSON = [];
		AHQ.selectedColumnNames = [];
		AHQ.selectedAdHocId = null;
		AHQ.selectedNameNodeId = null;
		AHQ.selectedResourceManagerId = null;
		AHQ.selectedSourcePath = null;
		AHQ.selectedParseRecursive = false;
		AHQ.selectedFilePattern = null;
		AHQ.selectedPatternExpr = null;
		AHQ.selectedValueSeparator = null;
		AHQ.selectedEncoding = null;
		AHQ.selectedAdHocTableName = null;
		AHQ.selectedFileName = null;
		AHQ.index = -1;
		AHQ.currentPage = 1;
		AHQ.exceptionCase = 1;
		$("#liGeneral").addClass("active");
		AHQ.canBeViewed = false;
		AHQ.config = 1;
		AHQ.isSuccess = false;
		AHQ.colDetails = null, AHQ.isHeader = true;
		AHQ.selectedDelimeter = null;
		AHQ.counter = -1;
		AHQ.selectedAdHocTableName = null;
		$('#columnListGrid').remove();
		$('#columnListPager').remove();
		$('#columnListContainer').remove();

		if (isRefresh)
			Navbar.refreshView();
	},

	closeEditBox : function() {
		Util.removeLightbox('ahq_Box');
		AHQ.currentOperation = null;
		AHQ.selectedType = null;
		AHQ.columnList = null;
		AHQ.selectedColumnListJSON = [];
		AHQ.selectedColumnNames = [];
		AHQ.selectedAdHocId = null;
		AHQ.selectedNameNodeId = null;
		AHQ.selectedResourceManagerId = null;
		AHQ.selectedSourcePath = null;
		AHQ.selectedParseRecursive = false;
		AHQ.selectedFilePattern = null;
		AHQ.selectedPatternExpr = null;
		AHQ.selectedValueSeparator = null;
		AHQ.isFirstRowHeader = false;
		AHQ.selectedEncoding = null;
		AHQ.selectedAdHocTableName = null;
		AHQ.selectedFileName = null;

	},
	dwrCallBackResponse : function(dwrResponse) {
		var message;
		var status;
		var imgId;
		var id = AHQ.selectedAdHocId;
		AHQ.todoRedirect = false;

		if ((dwrResponse != null) && (dwrResponse != undefined)) {
			if (dwrResponse.taskSuccess) {
				status = "Success";
				imgId = "popup.image.success";

				if (AHQ.currentOperation == 'add')
					AHQ.todoRedirect = true;
			} else {
				status = "Failure";
				imgId = "popup.image.fail";
			}

			message = dwrResponse.responseMessage;
		} else {
			message = "Failed to perform hive entry operation.";
			status = "Failure";
			imgId = "popup.image.fail";
			var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
			document.getElementById('log_div' + id).innerHTML = log;
			document.getElementById('log_div' + id).style.display = "block";
		}

		dwr.util.byId('popup.image.processing' + id).style.display = 'none';
		dwr.util.byId(imgId + id).style.display = '';

		dwr.util.setValue('popup.message' + id, message);
		dwr.util.setValue('popup.status' + id, status);
		dwr.util.byId('ok.popup').disabled = false;
	},

	checkExceptionCase : function(id) {
		if (id == 'all') {
			AHQ.exceptionCase = 1;
		} else if (id == 'one') {
			AHQ.exceptionCase = 2;
		}

	},

	isAdHocIdExist : function() {
		var isExist = false;
		if ($('#adHocTable tbody tr').length > 0
				&& !$('#adHocTable tbody tr td').hasClass('dataTables_empty')) {
			var ruleId = document.getElementById('adHocId').value;
			$('#adHocTable tbody tr')
					.each(
							function() {
								var row = this.cells;
								if (ruleId == row[1].textContent) {
									jAlert(
											"Hive Id already exists. Please specify unique Hive Id",
											"Incorrect Detail");
									$("#popup_container").css("z-index",
											"99999999");
									isExist = true;
								}

							});
		}
		return isExist;
	},

	closePopUp : function() {
		document.getElementById('nextbtn1').disabled = false;
		document.getElementById('backbtn1').disabled = false;

		if (AHQ.isSuccess) {
			$('#firstPage').css('display', 'none');
			$('#secondPage').css('display', 'none');
			$('#processingDiv').css('display', 'none');
			$('#viewJSON').css('display', 'none');
			$('#host_create').css('width', '850px');
			$('#thirdPage').css('display', '');
			$('#columnListContainer').css('display', 'none');
			$('#sampledataTables').css('display', '');
			$('#sampledataTables').css('max-width', '800px');
			AHQ.createSampleDataTables();
			// AHQ.fillColumnsByJson(AHQ.colDetails);
		} else {

			$('#firstPage').css('display', 'none');
			$('#host_create').css('width', '600px');
			if (AHQ.selectedType == 'JSON') {
				$('#secondPage').css('display', 'none');
				$('#viewJSON').css('display', '');
				$('#failed1').css('display', '');
			} else {
				$('#failed').css('display', '');
				$('#viewJSON').css('display', 'none');
				$('#secondPage').css('display', '');
			}
			AHQ.currentPage = 2;
			AHQ.showActivatedTabs();
			$('#failed').css('display', '');
			$('#columnListContainer').css('display', 'none');
			$('#sampledataTables').css('display', 'none');
			$('#processingDiv').css('display', 'none');
			$('#thirdPage').css('display', 'none');
		}
	},

	includeClicked : function(id) {
		var flag = document.getElementById(id).checked;
		id = id.toString();
		var index = id.indexOf('_');
		id = id.substring(index + 1, id.length);
		if (!flag) {
			$('#dataType_' + id).attr('disabled', 'disabled');
			$('#header_' + id).attr('disabled', 'disabled');
		} else {
			$('#dataType_' + id).removeAttr('disabled');
			$('#header_' + id).removeAttr('disabled');
		}
	},

	setStringSize : function(id, j) {
		var text = document.getElementById(id).value;
		if (text == "STRING") {
			$("#textBox_" + j).css('display', '');
		} else {
			$("#textBox_" + j).css('display', 'none');
		}
	},

	createSampleDataTables : function() {
		$('#columnListGrid').remove();
		$('#columnListPager').remove();
		$('#sample_table').remove();

		$('#sampledataTables').html(
				'<table id="sample_table" class="dataTable"></table>');
		// $('#columnListContainer').html('<table
		// id="columnListGrid"></table><div id="columnListPager"></div>');

		var json = AHQ.colDetails;
		var headerFields = json[0]['meta']['header'];
		var count = 1;
		var columnNames = new Array();
		var columnTypes = new Array();
		var columnData = new Array();

		if (json[0]['meta'] != null || json[0]['meta'] != undefined) {
			for ( var key in json[0]['meta']['details']) {
				var value1 = json[0]['meta']['details'][key];
				columnTypes.push(value1['type']);
				count++;
			}

			var colList = [];
			var tableRow = [];
			count--;
			if (AHQ.isHeader) {
				for ( var key in json[0]['meta']['details']) {
					var value = json[0]['meta']['header'][key];
					columnNames.push(value);
				}
			} else {
				for (var i = 0; i < count; i++) {
					if (i == 0)
						columnNames.push("filepath");
					else
						columnNames.push('Column' + i);
				}
			}

			AHQ.selectedColumnNames = columnNames;

			var fileType = $("#adHocType").val();
			for (var i = 0; i < columnNames.length; i++) {

				// var chk = '<input type="checkbox" name="include"
				// id="include_'+i+'" checked="checked"
				// onclick="javascript:AHQ.includeClicked(this.id);">';
				if (i == 0)
					var col = '<input type="text" id = "header_'
							+ i
							+ '" value="'
							+ columnNames[i]
							+ '" style="width: 145px;" readonly="readonly" onkeypress="javascript:Util.blockSpecialCharButNotUnderScore(window.event,this);"  ><span id="sub_error_span" style="display: none;color:red; "></span>';
				else
					var col = '<input type="text" id = "header_'
							+ i
							+ '" value="'
							+ columnNames[i]
							+ '" style="width: 145px;"  onkeypress="javascript:Util.blockSpecialCharButNotUnderScore(window.event,this);"  ><span id="sub_error_span" style="display: none;color:red; "></span>';
				var title = '<div style="width: 150px;">' /* + chk */+ col
						+ '</div>';

				// if ((i == 0) && (fileType == "CSV" || fileType == "JSON" ||
				// fileType == "IISLOG" || fileType == "ACCESSLOG"))
				// colList.push({ "sTitle": title, "bSortable": false
				// ,"bVisible": false});
				// else
				colList.push({
					"sTitle" : title,
					"bSortable" : false
				});
			}

			AHQ.counter = count;
			var dataLength = json[0]['meta']['data'].length;

			if (dataLength > AHQ.FIXED_NO_OF_RECORDS)
				dataLength = AHQ.FIXED_NO_OF_RECORDS;

			for (var i = -1; i < dataLength; i++) {
				var rowData = [];
				/*
				 * if(i==-2) { for(var j=0;j<count;j++) { rowData.push('<input
				 * type="checkbox" name="include" id="include_'+j+'"
				 * checked="checked"
				 * onclick="javascript:AHQ.includeClicked(this.id);"> include
				 * '); } tableRow.push(rowData); continue; } else
				 */if (i == -1) {
					for (var j = 0; j < count; j++) {
						var div = '<div style="width: 170px">';
						var selected = '<select id="dataType_'
								+ j
								+ '" name="datatype" onchange="javascript:AHQ.setStringSize(this.id , '
								+ j
								+ ')" style="text-align:center;width: auto;" > <option id="STRING" value="STRING">STRING</option>'
								+ '<option id="TIMESTAMP" value="TIMESTAMP">TIMESTAMP</option>'
								+ '<option id="LONG"  value="LONG">LONG</option>'
								+ '<option id="SHORT" value="SHORT">SHORT</option>'
								+ '<option id="INTEGER" value="INTEGER">INTEGER</option>'
								+ '<option id="DOUBLE" value="DOUBLE">DOUBLE</option>'
								+ '<option id="DECIMAL" value="DECIMAL">DECIMAL</option>'
								+ '<option id="BOOLEAN" value="BOOLEAN">BOOLEAN</option>'
								+ '<option id="BLOB" value="BLOB">BLOB</option>'
								+ '</select>';
						div = div + selected;

						var textBox = '<input type="text" id="textBox_'
								+ j
								+ '" placeholder="Size" style="display: none; width: 18%;">';
						div = div + textBox + "</div>";
						rowData.push(div);
					}
					tableRow.push(rowData);
					continue;
				} else {
					for (var k = 0; k < count; k++) {
						rowData.push(json[0]['meta']['data'][i][k]);
					}
				}

				tableRow.push(rowData);
			}

			$('#sample_table').dataTable({
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bDestroy" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"aoColumns" : colList,
				"aaData" : tableRow
			});

			/*
			 * $('#sample_table').ready(function(){ for(var i=0;i<count;i++)
			 * $("#dataType_"+i).combify(); });
			 */
			var startCount = 0;
			// if($('#adHocType').val() == 'CSV' || $('#adHocType').val() ==
			// 'JSON' || $('#adHocType').val() == 'IISLOG' ||
			// $('#adHocType').val() == 'ACCESSLOG' )
			// {
			// $('#include_0').removeAttr('checked');
			// startCount = 1;
			// }
			for (var i = startCount; i < count; i++) {
				var type = columnTypes[i];
				var index = type.indexOf('STRING');
				if (index != -1) {
					var data1 = "<option value=" + type + ">" + type
							+ "</option>";
					$('#dataType_' + i).append(data1);
				}
				$('#dataType_' + i).val(type);
				AHQ.setStringSize("dataType_" + i, i);
			}
		} else {
			jAlert("Your file can't be parsed.An Error occured.",
					"Invalid File Details");
			$("#popup_container").css("z-index", "9999999");
			return;
		}

	},

	createSampleColumnTable : function() {
		$('#columnListGrid').remove();
		$('#columnListPager').remove();
		$('#columnListContainer')
				.html(
						'<table id="columnListGrid"></table><div id="columnListPager"></div>');

		// var wd = ($("#columnListContainer").width() - 20)/3;
		var json = AHQ.colDetails;
		var headerFields = json[0]['meta']['header'];
		var count = 1;
		var columnNames = new Array();
		var columnTypes = new Array();
		// extracting values from a json
		for ( var key in json[0]['meta']['header']) {
			var value = json[0]['meta']['header'][key];
			var value1 = json[0]['meta']['details'][key];
			columnTypes.push(value1['type']);
			if (AHQ.isHeader)
				columnNames.push(value);
			else
				columnNames.push('Column' + count);
			count++;

		}
		wd = ($("#columnListContainer").width() - 20) / count;

		jQuery("#columnListGrid")
				.jqGrid(
						{

							datatype : "local",
							colNames : columnNames,
							colModel : [
									{
										name : 'colName',
										key : true,
										index : 'colName',
										width : wd,
										align : "left",
										editable : true,
										sorttype : "String",
										editrules : {
											required : true
										}
									},
									{
										name : 'colType',
										index : 'colType',
										width : wd,
										align : "left",
										editable : true,
										edittype : "select",
										editoptions : {
											value : "INTEGER:INTEGER; STRING(250):STRING(250); TIMESTAMP:TIMESTAMP"
										},
										editrules : {
											required : true
										},
										formoptions : {
											elmsuffix : "<span style= 'color: red;'>*</span>"
										}
									}, {
										name : 'colIndex',
										index : 'colIndex',
										width : wd,
										align : "right",
										editable : true,
										sorttype : "int"
									} ],
							pager : '#columnListPager',
							altRows : true,
							viewrecords : true,
							sortable : true,
							sortname : 'colIndex',
							sortorder : "asc",

							rowList : [],
							pgbuttons : false,
							pgtext : null,
							pagination : false,

							height : ($("#columnListContainer").height() - 50),
							width : ($("#columnListContainer").width()),
							shrinkToFit : false,
							caption : "",
							loadui : "disable",

							loadComplete : function(data) {
							},

							onSortCol : function(index, idxcol, sortorder) {
								if (this.p.lastsort >= 0
										&& this.p.lastsort !== idxcol
										&& this.p.colModel[this.p.lastsort].sortable !== false) {
									// show the icons of last sorted column
									$(this.grid.headers[this.p.lastsort].el)
											.find(
													">div.ui-jqgrid-sortable>span.s-ico")
											.show();
									$(this.grid.headers[this.p.lastsort].el)
											.removeClass('ui-state-highlight');
								}
								$(this.grid.headers[idxcol].el).addClass(
										'ui-state-highlight');
							},

							editurl : "adHocQuery.do?type=doNothing"
						});

		jQuery("#columnListGrid").jqGrid('navGrid', "#columnListPager", {
			edit : true,
			add : true,
			del : true
		}, { // edit option
			onclickSubmit : function(params, postdata) {
				return postdata;
			},
			afterShowForm : function(formid) {
				$("#editmodcolumnListGrid").css('z-index', '9500000');
			},
			viewPagerButtons : false,
			closeAfterEdit : true,
			reloadAfterSubmit : false,
			width : 230,
			resize : false,
			recreateForm : true,
			closeOnEscape : true
		}, { // add option
			onclickSubmit : function(params, postdata) {
				return postdata;
			},
			afterShowForm : function(formid) {
				$("#editmodcolumnListGrid").css('z-index', '9500000');
			},
			closeAfterAdd : true,
			reloadAfterSubmit : false,
			width : 230,
			resize : false,
			recreateForm : true,
			closeOnEscape : true
		}, { // delete option
			afterShowForm : function(formid) {
				$("#delmodcolumnListGrid").css('z-index', '9500000');
			},
			resize : false,
			closeOnEscape : true
		}, { // search option
			resize : false,
			closeOnEscape : true
		});

		$("#columnListPager_center").css('display', 'none');
		$("#pg_columnListPager").css('height', '20px');

		$(jQuery("#columnListGrid")[0].grid.headers[0].el).addClass(
				'ui-state-highlight'); // Highlight first column header on grid
		// load.
		if (AHQ.currentOperation == 'add')
			RemoteManager.getDefaultColumns(AHQ.selectedType,
					AHQ.fillColumnTableInfo);

	},

	fileUpload : function() {
		if (AHQ.selectedType == 'JSON')
			var form = document.getElementById("fileParser1");
		else
			var form = document.getElementById("fileParser");

		document.getElementById('nextbtn1').disabled = true;
		document.getElementById('backbtn1').disabled = true;

		var iframe = document.createElement("iframe");
		iframe.setAttribute("id", "upload_iframe");
		iframe.setAttribute("name", "upload_iframe");
		iframe.setAttribute("width", "0");
		iframe.setAttribute("height", "0");
		iframe.setAttribute("border", "0");
		iframe.setAttribute("style", "width: 0; height: 0; border: none;");
		$('#processingDiv').css('display', '');
		$('#nextbtn').button("disable");
		$('#backbtn').button("disable");

		var div_id = 'formDIV';
		// Add to document...
		form.appendChild(iframe);
		window.frames['upload_iframe'].name = "upload_iframe";

		iframeId = document.getElementById("upload_iframe");
		// // Add event...
		var eventHandler = function() {

			$('#processingDiv').css('display', '');
			$('#host_create').css('width', '600px');
			$('#secondPage').css('display', '');
			$('#thirdPage').css('display', 'none');
			var fileName = '';
			if (AHQ.selectedType == 'JSON')
				fileName = $('#headerFileJSON').val();
			else
				fileName = $('#headerFile').val();

			AHQ.selectedFileName = fileName;
			$('#parser').text(fileName);
			var content = new Object();
			if (iframeId.detachEvent)
				iframeId.detachEvent("onload", eventHandler);
			else
				iframeId.removeEventListener("load", eventHandler, false);

			// Message from server...
			if (iframeId.contentDocument) {
				content = iframeId.contentDocument.body.textContent;
			} else if (iframeId.contentWindow) {
				content = iframeId.contentWindow.document.body.textContent;
			} else if (iframeId.document) {
				content = iframeId.document.body.textContent;
			}
			var str = content + '';
			var json = jQuery.parseJSON(str);
			var myJSONText = JSON.stringify(json);
			var response = json[0]["success"];
			AHQ.isSuccess = false;
			if (response == 'File uploaded successfully.'
					&& (json[0]["name"] != null && json[0]["name"] != undefined)
					&& (json[0]["meta"] != null && json[0]["meta"] != undefined)
					&& (json[0]["meta"]['details'] != null && json[0]["meta"]['details'] != undefined)
					&& (json[0]["meta"]['details'].length != 0 && json[0]["meta"]['data'].length != 0)) {
				AHQ.isSuccess = true;
				AHQ.colDetails = json;
			} else {
				AHQ.isSuccess = false;
			}
			AHQ.closePopUp();
		}

		if (iframeId.addEventListener)
			iframeId.addEventListener("load", eventHandler, true);
		if (iframeId.attachEvent)
			iframeId.attachEvent("onload", eventHandler);

		// Set properties of form...
		form.setAttribute("target", "upload_iframe");
		form.setAttribute("action", "AdHocQuerySampleFileUpload");
		form.setAttribute("method", "post");
		form.setAttribute("enctype", "multipart/form-data");
		form.setAttribute("encoding", "multipart/form-data");
		$('#parser').text($('#tagName').val());
		$('#tagDiv').hide();
		$('#respMessage').text("File is uploading.");
		form.submit();
		// $('#thirdPage').css('display','');
	},

	nextPageSwitchEdit : function(from, to) {
		if (from == 1 && to == 2) {
			if ($("#nameNodeId").val() == "Select NameNode") {
				jAlert("You must specify the NameNode.", "Insufficient Details");
				$("#popup_container").css("z-index", "99999999");
				return;
			} else if ($("#resourceManagerId").val() == "Select ResourceManager") {
				jAlert("You must specify the ResourceManager.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "99999999");
				return;
			} else if ($('#adHocId').val() == "") {
				jAlert("You must specify the unique Hive Id.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			} else if ($('#sourcePath').val() == "") {
				jAlert("You must specify the Source Path.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			} else if ($('#filePattern').val() == "") {
				jAlert("You must specify the File Path Pattern.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			} else if ($('#filePattern').val() != "") {

				AHQ.isFileFilterRegexValid = false;
				RemoteManager.validateJavaRegex($('#filePattern').val(), {
					async : false,
					callback : function(dwrResponse) {
						if (dwrResponse.taskSuccess) {
							AHQ.isFileFilterRegexValid = true;
						} else {
							jAlert(
									"You must specify the correct File Path Pattern: \n"
											+ dwrResponse.responseMessage,
									"Incorrect Details");
							$("#popup_container").css("z-index", "9999999");
							AHQ.isFileFilterRegexValid = false;
						}
					}
				});

				if (!AHQ.isFileFilterRegexValid) {
					return;
				}

			} else if ($('#adHocTableName').val() == "") {
				jAlert("You must specify the Hive Table Name.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			}
			$('#firstPage').css('display', 'none');
			$('#host_create').css('width', '600px');
			$('#thirdPage').css('display', '');
			AHQ.createColumnTableForEdit();
		} else if (from == 2 && to == 1) {
			$('#firstPage').css('display', '');
			$('#thirdPage').css('display', 'none');
		}
	},
	showDiv : function(pageNo) {

		AHQ.showActivatedTabs();
		AHQ.nextPageSwitch(AHQ.currentPage, pageNo);
	},
	nextPageSwitch : function(to, from) {
		$('#failed').css('display', 'none');
		if (to == 2 && from == 3) {
			if (AHQ.config == 1
					&& (AHQ.selectedType == 'CSV'
							|| AHQ.selectedType == 'IISLOG' || AHQ.selectedType == 'MBOX')) {
				if (AHQ.selectedType == 'CSV') {
					if ($('#delimiterValue').val() == "") {
						jAlert("You must specify the delimiter value.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}
					if ($('#records').val() == "") {
						jAlert(
								"You must specify number of records to be parsed.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}
					if ($('#headerFile').val() == "") {
						jAlert("You must specify sample file.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}
				} else if (AHQ.selectedType == 'IISLOG') {
					if ($('#delimiterValue').val() == "") {
						jAlert("You must specify delimiter value.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}
					if ($('#records').val() == "") {
						jAlert(
								"You must specify number of records to be parsed.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}
					if ($('#headerFile').val() == "") {
						jAlert("You must specify sample file.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}

				} else if (AHQ.selectedType == 'MBOX'
						&& $('#headerFile').val() == "") {
					jAlert("You must specify sample file.",
							"Insufficient Details");
					$("#popup_container").css("z-index", "9999999");
					return;
				}

				// $('#firstPage').css('display','none');
				AHQ.isHeader = document.getElementById('isFirstLineHeader').checked;

				$('#encodingValueForm').val($("#encoding").val());
				$('#adHocTypeForm').val($("#adHocType").val());
				$('#selNameNodeIdForm').val($("#nameNodeId").val());
				if (AHQ.selectedType == 'CSV') {
					$('#separatorValueForm').val($("#separatorValue").val());
					$('#delimiterValueForm').val($("#delimiterValue").val());
				} else if (AHQ.selectedType == 'IISLOG') {
					$('#delimiterValueForm').val($("#delimiterValue").val());
				}
				AHQ.fileUpload();
			} else if ((AHQ.selectedType == 'LOG' && AHQ.config == 1)
					|| (AHQ.selectedType == 'ACCESSLOG' && AHQ.config == 1)) {
				$('#firstPage').css('display', 'none');
				$('#delimiterValueForm').val($("#log_pattern").val());
				$('#encodingValueForm').val($("#encoding").val());
				$('#adHocTypeForm').val($("#adHocType").val());
				$('#selNameNodeIdForm').val($("#nameNodeId").val());
				AHQ.fileUpload();
			} else if ((AHQ.selectedType == 'LOG' && AHQ.config == 2)
					|| (AHQ.selectedType == 'ACCESSLOG' && AHQ.config == 1)) {
				$('#firstPage').css('display', 'none');
				$('#secondPage').css('display', 'none');
				$('#thirdPage').css('display', '');
				$('#sampledataTables').css('display', 'none');
				$('#columnListContainer').css('display', '');
				AHQ.createColumnTable();
			} else if (AHQ.config == 1
					&& (AHQ.selectedType == 'JSON' || AHQ.selectedType == 'XML'
							|| AHQ.selectedType == 'PAIRS' || AHQ.selectedType == 'REGEX')) {
				if (AHQ.selectedType == 'JSON') {
					$('#adHocTypeFormJSON').val($("#adHocType").val());
					$('#encodingValueFormJSON').val($("#encoding").val());
					$('#records').val($('#recordsJSON').val());
					if ($('#headerFileJSON').val() == "") {
						jAlert("You must specify sample file.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}
				} else if (AHQ.selectedType == 'XML') {
					if ($('#delimiterValue').val() == "") {
						jAlert("You must specify xml node name.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}
					if ($('#headerFile').val() == "") {
						jAlert("You must specify sample file.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}

					$('#adHocTypeForm').val($("#adHocType").val());
					$('#delimiterValueForm').val($("#delimiterValue").val());
				} else if (AHQ.selectedType == 'PAIRS') {
					if ($('#delimiterValue').val() == "") {
						jAlert("You must specify delimiter.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}
					/*
					 * if($('#separatorValue').val()=="") { jAlert("You must
					 * specify seperator value.","Insufficient Details");
					 * $("#popup_container").css("z-index","9999999"); return; }
					 */
					if ($('#headerFile').val() == "") {
						jAlert("You must specify sample file.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}
					$('#encodingValueForm').val($("#encoding").val());
					$('#selNameNodeIdForm').val($("#nameNodeId").val());
					$('#adHocTypeForm').val($("#adHocType").val());
					$('#separatorValueForm').val($("#separatorValue").val());
					$('#delimiterValueForm').val($("#delimiterValue").val());
					$('#headerFile').css('float', 'right');
					$('#headerFile').css('margin-right', '-15px');
				} else if (AHQ.selectedType == 'REGEX') {
					if ($('#records').val() == "") {
						jAlert("You must specify regular expression.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}
					if ($('#headerFile').val() == "") {
						jAlert("You must specify sample file.",
								"Insufficient Details");
						$("#popup_container").css("z-index", "9999999");
						return;
					}
					$('#encodingValueForm').val($("#encoding").val());
					$('#selNameNodeIdForm').val($("#nameNodeId").val());
					$('#delimiterValueForm').val($("#regex").val());
					$('#adHocTypeForm').val($("#adHocType").val());
				}
				AHQ.fileUpload();
			} else if (AHQ.config == 2) {
				$('#firstPage').css('display', 'none');
				$('#viewJSON').css('display', 'none');
				$('#secondPage').css('display', 'none');
				$('#thirdPage').css('display', '');
				$('#sampledataTables').css('display', 'none');
				$('#columnListContainer').css('display', '');
				AHQ.createColumnTable();
			}
			AHQ.currentPage = 3;
			AHQ.canBeViewed = true;
			AHQ.showActivatedTabs();
		} else if (to == 3 && from == 2) {
			$('#firstPage').css('display', 'none');
			$('#thirdPage').css('display', 'none');
			$('#host_create').css('width', '600px');
			$('#failed').css('display', 'none');
			if (AHQ.selectedType == 'JSON')
				$('#viewJSON').css('display', '');
			else
				$('#secondPage').css('display', '');
			$('#processingDiv').css('display', 'none');
			AHQ.currentPage = 2;
			AHQ.showActivatedTabs();
		} else if (to == 1 && from == 2) {
			$('#delimiterText').text('Log Pattern*');
			$('#delimiterText1').text('Delimiter*');
			$('#failed').css('display', 'none');
			$('#headerFile').removeAttr('margin-right');
			$('#headerFileJSON').removeAttr('margin-right');
			var isAdHocIdExists = AHQ.isAdHocIdExist();
			if (isAdHocIdExists) {
				return;
			}
			if ($("#nameNodeId").val() == "Select NameNode") {
				jAlert("You must specify the NameNode.", "Insufficient Details");
				$("#popup_container").css("z-index", "99999999");
				return;
			} else if ($("#resourceManagerId").val() == "Select ResourceManager") {
				jAlert("You must specify the ResourceManager.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "99999999");
				return;
			} else if ($('#adHocId').val() == "") {
				jAlert("You must specify the unique Hive Id.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			} else if ($('#sourcePath').val() == "") {
				jAlert("You must specify the Source Path.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			} else if ($('#sourcePath').val() == "/") {
				jAlert("Root folder (/) as HDFS Source Path is not supported.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			} else if ($('#filePattern').val() == "") {
				jAlert("You must specify the File Path Pattern.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			} else if ($('#filePattern').val() != "") {

				AHQ.isFileFilterRegexValid = false;
				RemoteManager.validateJavaRegex($('#filePattern').val(), {
					async : false,
					callback : function(dwrResponse) {
						if (dwrResponse.taskSuccess) {
							AHQ.isFileFilterRegexValid = true;
						} else {
							jAlert(
									"You must specify the correct File Path Pattern: \n"
											+ dwrResponse.responseMessage,
									"Incorrect Details");
							$("#popup_container").css("z-index", "9999999");
							AHQ.isFileFilterRegexValid = false;
						}
					}
				});

				if (!AHQ.isFileFilterRegexValid) {
					return;
				}

			} else if ($('#adHocTableName').val() == "") {
				jAlert("You must specify the Hive Table Name.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			}
			if (AHQ.selectedType == 'CSV' || AHQ.selectedType == 'LOG'
					|| AHQ.selectedType == 'IISLOG'
					|| AHQ.selectedType == 'FIXED'
					|| AHQ.selectedType == 'ACCESSLOG') {
				AHQ.showSecondPage();
				$('#host_create').css('width', '600px');
				$('#failed').css('display', 'none');
				$('#processingDiv').css('display', 'none');
				$('#delimiterValue').val(',');
				if (AHQ.selectedType == 'ACCESSLOG')
					document.getElementById('log_pattern').value = '%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i\"';
				else if (AHQ.selectedType == 'LOG')
					document.getElementById('log_pattern').value = '%d{dd MMM,HH:mm:ss:SSS} [%t] [%c] [%x] [%X] [%p] [%l] [%r] %C{3} %F-%L [%M] - %m%n';
			} else if (AHQ.selectedType == 'JSON') {
				$('#firstPage').css('display', 'none');
				$('#secondPage').css('display', 'none');
				$('#thirdPage').css('display', 'none');
				$('#viewJSON').css('display', '');
			} else if (AHQ.selectedType == 'MBOX'
					|| AHQ.selectedType == 'PAIRS' || AHQ.selectedType == 'XML') {
				AHQ.showSecondPage();
				// console.log('in '+AHQ.selectedType );
				$('#headerFile').css('margin-right', '-15px');
				if (AHQ.selectedType == 'XML') {
					$('#delimiterText1').text('XML Node Name*');
					$('#delimiterValue').val('');
					// console.log('in xml file');

				} else if (AHQ.selectedType == 'PAIRS') {
					$('#records').css('margin-right', '-15px');
					$('#headerFile').css('margin-right', '-15px');
				}
			} else if (AHQ.selectedType == 'REGEX') {
				AHQ.showSecondPage();
				$('#log').css('display', 'none');
				$('#deli').css('display', 'none');
				$('#sepa').css('display', 'none');
				$('#hasHeaderId').css('display', 'none');
				$('#radio').css('display', '');
				$('#rec1').css('display', '');
				$('#delimiterText1').text('Regular Expression*');
				$('#headerFile').css('margin-right', '-15px');
			}

			console.log("Here");
			AHQ.currentPage = 2;
			AHQ.showActivatedTabs();
		} else if (to == 2 && from == 1) {

			if (AHQ.selectedType == 'JSON') {
				$('#viewJSON').css('display', 'none');
			}
			AHQ.showFirstPage();
			AHQ.currentPage = 1;
			AHQ.showActivatedTabs();
		} else if (to == 3 && from == 1) {
			if (AHQ.selectedType == 'JSON') {
				$('#viewJSON').css('display', 'none');
			}
			$('#host_create').css('width', '600px');
			AHQ.showFirstPage();
			AHQ.currentPage = 1;
			AHQ.showActivatedTabs();
		} else if (to == 1 && from == 3) {
			if (AHQ.canBeViewed) {
				$('#firstPage').css('display', 'none');
				$('#secondPage').css('display', 'none');
				$('#thirdPage').css('display', '');
				$('#viewJSON').css('display', 'none');
				AHQ.currentPage = 3;
				AHQ.showActivatedTabs();
			} else {
				jAlert(
						"You must specify configurations to view schema definitions.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "99999999");
				return;
			}
		}
	},
	showSecondPage : function() {
		$('#firstPage').css('display', 'none');
		$('#secondPage').css('display', '');
		$('#thirdPage').css('display', 'none');
		$('#viewJSON').css('display', 'none');
	},
	showFirstPage : function() {
		$('#firstPage').css('display', '');
		$('#secondPage').css('display', 'none');
		$('#thirdPage').css('display', 'none');
		$('#processingDiv').css('display', 'none');
	},
	showChooseFile : function() {
		var value = $("#chooseFile").val();
		if (value == "Change") {
			$("#chooseFile").val("Keep Unchanged");
			$("#fileAdHocOld").hide();
			$("#fileAdHoc").show();
			$("#fileAdHoc").css('display', '');
		} else {
			$("#chooseFile").val("Change");
			$("#fileAdHocOld").show();
			$("#fileAdHoc").hide();
		}
	},

	nextPage : function(pageNo, isNew) {
		if (!isNew) {
			if (pageNo == 2) {
				$('#firstPage').css('display', 'none');
				$('#secondPage').css('display', '');
				AHQ.createColumnTable();
			} else {
				$('#secondPage').css('display', 'none');
				$('#firstPage').css('display', '');
			}
			AHQ.createColumnTable();
			if (AHQ.index != -1) {
				var columnDetails = AHQ.jqGridColumnDetails[AHQ.index];
				var jsonObj = $.parseJSON(columnDetails);
				var list = {
					"fields" : jsonObj
				};
				AHQ.fillColumnTableInfo(list);
			}

			return;
		}
		if (pageNo == 2 && isNew) {
			var isAdHocIdExists = AHQ.isAdHocIdExist();
			if (isAdHocIdExists) {
				return;
			}
			if ($("#nameNodeId").val() == "Select NameNode") {
				jAlert("You must specify the NameNode.", "Insufficient Details");
				$("#popup_container").css("z-index", "99999999");
				return;
			} else if ($("#resourceManagerId").val() == "Select ResourceManager") {
				jAlert("You must specify the ResourceManager.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "99999999");
				return;
			} else if ($('#adHocId').val() == "") {
				jAlert("You must specify the unique Hive Id.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			} else if ($('#sourcePath').val() == "") {
				jAlert("You must specify the Source Path.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			} else if ($('#sourcePath').val() == "/") {
				jAlert("Root folder (/) as HDFS Source Path is not supported.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			}
			// else if ($('#patternExpr').val()=="")
			// {
			// jAlert("You must specify the Pattern Expression.","Insufficient
			// Details");
			// $("#popup_container").css("z-index","9999999");
			// return;
			// }
			else if ($('#adHocTableName').val() == "") {
				jAlert("You must specify the Hive Table Name.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			}

			$('#secondPage').css('display', '');
			$('#firstPage').css('display', 'none');
			$('#thirdPage').css('display', 'none');
			AHQ.createColumnTable();
		}

		else if (pageNo == 3 && isNew) {
			AHQ.createColumnTable();
			$('#thirdPage').css('display', '');
			$('#secondPage').css('display', 'none');
			$('#firstPage').css('display', 'none');
			AHQ.createColumnTable();
		} else {
			$('#secondPage').css('display', 'none');
			$('#firstPage').css('display', '');
		}
	}
}