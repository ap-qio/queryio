MigrationDataTagger = {
	conditionalColumnsList : [],
	// tagList : [],
	currentPage : 1,
	selectedColumnListJSON : [],
	isCustomParserRegistered : false,
	isAdhoc : false,
	canBeViewed : false,
	// conditioncount : 1,
	conditioncountJson : {},
	rowNum : 0,
	tagcount : 1,
	operatorCount : 1,
	tagVacant : [ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ],
	nameNodeList : [],
	databaseList : [],
	schemaList : [],
	currentSchemaType : '',
	metaOperationsValueAdhoc : '',
	metaOperationsValueCustom : '',
	properCondition : true,
	REPLACE_ROW_ID : '--REPLACE_ROW_ID--',
	EditJson : {},
	tableMap : {},
	tableList : [ "DATATAGS_JTL", "DATATAGS_CSV", "DATATAGS_JSON",
			"DATATAGS_LOG", "DATATAGS_TXT", "DATATAGS_ACCESSLOG",
			"DATATAGS_IISLOG" ],
	fileTypeList : [ "ACCESSLOG", "CSV", "PAIRS", "JSON", "IISLOG"/* , "LOG" */],
	filetype : '',
	DATATYPE_STRING : "STRING",
	DATATYPE_TIMESTAMP : "TIMESTAMP",
	DATATYPE_LONG : "LONG",
	DATATYPE_SHORT : "SHORT",
	DATATYPE_INTEGER : "INTEGER",
	DATATYPE_DOUBLE : "DOUBLE",
	DATATYPE_DECIMAL : "DECIMAL",
	DATATYPE_BOOLEAN : "BOOLEAN",
	DATATYPE_BLOB : "BLOB",

	FIXED_NO_OF_RECORDS : 10,

	jsonaa : {},

	ready : function() {
		MigrationDataTagger.conditioncountJson = new Object();
		// MigrationDataTagger.tagNameList = new Array();
		MigrationDataTagger.conditioncountJson['1'] = 1;
		var id = 1;
		RemoteManager.getNonStandByNodes(MigrationDataTagger.populateNamenode);
	},

	populateNamenode : function(list) {
		if (list != null) {
			MigrationDataTagger.nameNodeList = [];
			data = '';
			for (var i = 0; i < list.length; i++) {
				MigrationDataTagger.nameNodeList.push(list[i].id);
				data += '<option value="' + list[i].id + '">' + list[i].id
						+ '</option>';
			}
			$('#namenode').html(data);
			RemoteManager.getAllDBNameForNameNodeMapping(list[0].id,
					MigrationDataTagger.populateDatabase);
		}
	},

	populateDatabase : function(list) {
		if (list != null) {
			MigrationDataTagger.databaseList = list;
			data = '';
			for (var i = 0; i < list.length; i++) {
				data += '<option value="' + list[i] + '">' + list[i]
						+ '</option>';
			}
			$('#database').html(data);
			RemoteManager.getAllTagTableNamesForDB(list[0],
					MigrationDataTagger.populateSchema);

		}
	},

	populateSchema : function(map) {

		MigrationDataTagger.tableMap = map;
		if (map != null) {
			var flag = 0;
			MigrationDataTagger.schemaList = [];
			data = '<option value="0">None</option>';

			for ( var tableName in map) {
				flag = 1;
				if (tableName == 'ns_metadata' || tableName == 'hdfs_metadata')
					continue;
				MigrationDataTagger.schemaList.push(tableName);
				data += '<option value="' + tableName + '">' + tableName
						+ '</option>';
			}

			$('#schema').html(data);
			MigrationDataTagger.schemaChanged('0');
			isAdhocTable = false;
		}
	},

	namenodeChanged : function(id) {
		RemoteManager.getAllDBNameForNameNodeMapping(id,
				MigrationDataTagger.populateDatabase);
	},

	databaseChanged : function(id) {
		MigrationDataTagger.currentSchemaType = id;

		RemoteManager.getAllDataTagTableNamesForDB(id,
				MigrationDataTagger.populateSchema);
	},
	// check this in add functionality
	schemaChanged : function(id) {

		if (id == '0') {
			isAdhocTable = false;

			$('#metaconstant_1').css('display', 'none');
			$('#metaoperation_1').css('display', 'none');
			$('#metaglobal_1').css('display', '');
			$('#mglobal_1').attr('checked', 'checked');
			$('#moperation_1').removeAttr('checked');
			$('#moperation_1').attr('disabled', true);
			$('#conditionBox_1').val('');
			$('#cond_1_1').val('');
			// $('#conditionBox').attr('disabled',true);

			MigrationDataTagger.selectedColumnListJSON = [];
			var obj = new Object();
			obj["columnMap"] = MigrationDataTagger.getDefaultHDFSCoreTags()
			MigrationDataTagger.populateColumnNames(obj, false);

		} else {

			for (var i = 1; i <= MigrationDataTagger.tagcount; i++) {
				$('#metaoperationdivtable_' + i).html('');
			}

			$('#moperation_1').removeAttr("disabled");
			$('#conditionBox_1').removeAttr("disabled");

			$('#mglobal_1').attr('checked', 'checked');

			var namenode = $('#namenode').val();
			var database = $('#database').val();
			var tablename = $('#schema').val();
			var ids = new Array();
			ids.push(id);
			isAdhocTable = MigrationDataTagger.tableMap[id];

			// console.log('ids : ' + ids);

			RemoteManager.getAllApplicableTagsList(namenode, database, ids,
					function(object) {
						MigrationDataTagger.populateColumnNames(object,
								isAdhocTable);
						MigrationDataTagger.fetchFileTypeforHiveTable(object,
								tablename);
					});

		}
	},

	populateColumnNames : function(object, isTableTypeAdhoc) {

		var tableName = $('#schema').val();
		if (tableName != 0) {
			isAdhocTable = MigrationDataTagger.tableMap[tableName];
		}

		columns = object["columnMap"];
		isCustomParserRegistered = object["isCustomParserRegistered"];
		var list = new Array();
		var arr = new Array();
		var count = 0;
		for ( var columnName in columns) {
			if (arr.indexOf(columnName) != -1)
				continue;

			arr.push(columnName);
			var temp = new Object;
			temp["colName"] = columnName.toUpperCase();
			temp["colType"] = columns[columnName].toUpperCase();
			temp["colIndex"] = count;
			count++;
			list.push(temp);
		}

		MigrationDataTagger.selectedColumnListJSON = list;

		MigrationDataTagger.isCustomParserRegistered = isCustomParserRegistered;

		$("#meta").css('display', '');
		MigrationDataTagger.populateMetaOperations(list,
				MigrationDataTagger.tableMap[tableName],
				isCustomParserRegistered);

		// for(var i=1 ; i<=10 ; i++)
		MigrationDataTagger.populateAttribute(1, 1);
		MigrationDataTagger.metachange(1, 1);
	},

	populateMetaOperations : function(list, isAdhoc, isCustomParserRegistered) {

		// $('#metaoperationdivtable').html('');
		// console.log('isCustomParserRegistered : ' +
		// isCustomParserRegistered);
		// console.log('in populateMetaOperations : ');
		// console.log('isAdhoc : ' + isAdhoc);
		MigrationDataTagger.isAdhoc = isAdhoc;
		if (isAdhoc) {
			MigrationDataTagger.metaOperationsValueAdhoc = '';
			var data = '';
			for (var i = 0; i < list.length; i++) {
				var c = i;
				data += '<tr>'
						+ '<td>'
						+ '<input id="metaradio'
						+ c
						+ '" type="radio"  name="metaopcheck'
						+ MigrationDataTagger.REPLACE_ROW_ID
						+ '" id="constant" onclick="javascript: MigrationDataTagger.metaradioclicked(this, '
						+ c
						+ ');">'
						+ '</td>'
						+ '<td id="metaattr'
						+ c
						+ '">'
						+ list[i]["colName"]
						+ '</td>'
						+ '<td >'
						+ '<select id="metaop'
						+ c
						+ '" disabled="disabled" style="width: 100%;">'
						+ '<option value="count">COUNT</option>'
						+ '<option value="distinctcount">DISTINCT COUNT</option>';

				var type = list[i]["colType"];
				if (type == MigrationDataTagger.DATATYPE_INTEGER
						|| type == MigrationDataTagger.DATATYPE_DECIMAL) {
					data += '<option value="sum">SUM</option>'
							+ '<option value="min">MIN</option>'
							+ '<option value="max">MAX</option>'
							+ '<option value="avg">AVERAGE</option>'
							+ '</select>' + '</td>' + '</tr>';
				}
			}
			MigrationDataTagger.metaOperationsValueAdhoc = data;
		} else if (isCustomParserRegistered == false) {
			MigrationDataTagger.metaOperationsValueCustom = '';
			var data = '';
			for (var i = 0; i < list.length; i++) {
				var c = i;
				data += '<tr>'
						+ '<td>'
						+ '<input id="metaradio'
						+ c
						+ '" type="radio"  name="metaopcheck'
						+ MigrationDataTagger.REPLACE_ROW_ID
						+ '" id="constant" onclick="javascript: MigrationDataTagger.metaradioclicked(this, '
						+ c
						+ ');">'
						+ '</td>'
						+ '<td id="metaattr'
						+ c
						+ '">'
						+ list[i]["colName"]
						+ '</td>'
						+ '<td >'
						+ '<select id="metaop'
						+ c
						+ '" disabled="disabled" style="width: 100%;">'
						+ '<option value="Copy">Copy</option>'
						+ '<option value="OccurrenceCount">Occurence Count</option>'
						+ '<option value="isExist">isExist</option>';
			}
			MigrationDataTagger.metaOperationsValueCustom = data;
		}

	},

	// deleteAdHocEntry : function ()
	// {
	// jQuery.alerts.okButton = ' Yes ';
	// jQuery.alerts.cancelButton = ' No';
	// jConfirm('Are you sure you want to delete selected adHoc
	// entries?','',function(val){
	// if (val == true){
	// MigrationDataTagger.currentOperation = 'delete';
	// Util.addLightbox("mdt_Box", "pages/popup.jsp");
	// }else{
	// return ;
	// }
	// jQuery.alerts.okButton = ' Ok ';
	// jQuery.alerts.cancelButton = 'Cancel';
	// });
	// $("#popup_container").css("z-index","99999999");
	// },

	showActivatedTabs : function() {

		$("#tabs li.active").removeClass('active');
		if (MigrationDataTagger.currentPage == 1) {
			$("#liConfig").addClass("active");
		} else if (MigrationDataTagger.currentPage == 2) {
			$("#liSchema").addClass("active");
		} else if (MigrationDataTagger.currentPage == 3) {
			$("#liApply").addClass("active");
		}
	},
	closeBox : function(isRefresh) {
		Util.removeLightbox('mdt_Box');
		MigrationDataTagger.currentOperation = null;
		MigrationDataTagger.selectedType = 'CSV';
		MigrationDataTagger.columnList = null;
		MigrationDataTagger.selectedColumnListJSON = [];
		MigrationDataTagger.selectedColumnNames = [];
		MigrationDataTagger.selectedAdHocId = null;
		MigrationDataTagger.selectedNameNodeId = null;
		MigrationDataTagger.selectedResourceManagerId = null;
		MigrationDataTagger.selectedSourcePath = null;
		MigrationDataTagger.selectedFilePattern = null;
		MigrationDataTagger.selectedPatternExpr = null;
		MigrationDataTagger.selectedValueSeparator = null;
		MigrationDataTagger.selectedEncoding = null;
		MigrationDataTagger.selectedAdHocTableName = null;
		MigrationDataTagger.index = -1;
		MigrationDataTagger.currentPage = 1;
		MigrationDataTagger.exceptionCase = 1;
		$("#liGeneral").addClass("active");
		MigrationDataTagger.canBeViewed = false;
		MigrationDataTagger.config = 1;
		MigrationDataTagger.isSuccess = false;
		// MigrationDataTagger.colDetails = null,
		MigrationDataTagger.isHeader = true;
		MigrationDataTagger.selectedDelimeter = null;
		// MigrationDataTagger.counter = -1;
		MigrationDataTagger.selectedAdHocTableName = null;
		MigrationDataTagger.tagcount = 1;
		MigrationDataTagger.operatorcount = 1;
		// MigrationDataTagger.conditioncount = 1;
		MigrationDataTagger.conditioncountJson = new Object();
		MigrationDataTagger.conditioncountJson['1'] = 1;
		MigrationDataTagger.tagVacant = [ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 ];
		$('#columnListGrid').remove();
		$('#columnListPager').remove();
		$('#columnListContainer').remove();
		if (isRefresh)
			Navbar.refreshView();
	},

	checkExceptionCase : function(id) {
		if (id == 'all') {
			MigrationDataTagger.exceptionCase = 1;
		} else if (id == 'one') {
			MigrationDataTagger.exceptionCase = 2;
		}

	},

	showDiv : function(pageNo) {
		MigrationDataTagger.nextPageSwitch(MigrationDataTagger.currentPage,
				pageNo);
		MigrationDataTagger.nextbuttonFunc(MigrationDataTagger.currentPage,
				pageNo);
	},

	validateSubTagID : function() {

		var regex = new RegExp("^[0-9]+$");

		$('#main_table_list tr').click(function() {
			var cid = $(this).attr('id');
			// console.log('cid ', cid + 'tagname', "tagname_" + regex);
			if (cid == ("tagname_" + regex)) {
				var cval = $(this).text();
				jAlert(cval);
			}
			var cval = $(this).text();
			alert(cval);
		});

	},

	blockSpecialCharButNotUnderScore : function(value) {

		$('#sub_error_span').html('');
		$('#sub_error_span').css('display', 'none');

		// $('#sub_error_span_1').html('');
		// $('#sub_error_span_1').css('display', 'none');

		var key = (value).charAt(0);
		var tagvalue = (value).charAt(value.length - 1);
		// console.log('tagvalue block: ', tagvalue);

		if (value.substring(0, (value.length - 1)) == '' && value.length > 1) {
			if (tagvalue == '$') {
				$('#sub_error_span_1')
						.text(
								'Tag ID is not provided. Please provide a unique Tag ID');
				$('#sub_error_span_1').css('display', '');
				// console.log('blockSpecialCharButNotUnderScore', 1);
				return false;
			} else {
				$('#sub_error_span')
						.text(
								'Tag ID is not provided. Please provide a unique Tag ID');
				$('#sub_error_span').css('display', '');
				// console.log('blockSpecialCharButNotUnderScore', 2);
				return false;
			}
		}

		if (value.length > 30) {
			if (tagvalue == '$') {
				$('#sub_error_span_1').text(
						'Value should be less than 30 character. '
								+ value.substring(0, (value.length - 1)));
				$('#sub_error_span_1').css('display', '');
				// console.log('blockSpecialCharButNotUnderScore', 3);
				return false;
			} else {
				$('#sub_error_span').text(
						'Value should be less than 30 character.' + value);
				$('#sub_error_span').css('display', '');
				// console.log('blockSpecialCharButNotUnderScore', 4);
				return false;
			}
		}
		var regex = new RegExp("^[a-zA-Z]+$");

		if (!regex.test(key)) {
			if (tagvalue == '$') {
				$('#sub_error_span_1').text(
						'First character of value should be alphabet.'
								+ value.substring(0, (value.length - 1)));
				$('#sub_error_span_1').css('display', '');
				// console.log('blockSpecialCharButNotUnderScore', 5);
				return false;
			} else {
				$('#sub_error_span').text(
						'First character of value should be alphabet.' + value);
				$('#sub_error_span').css('display', '');
				// console.log('blockSpecialCharButNotUnderScore', 6);
				return false;
			}

		}
		regex = new RegExp("^[a-zA-Z0-9_]+$");

		if (tagvalue == '$') {
			if (!regex.test(value.substring(0, (value.length - 1)))) {
				$('#sub_error_span_1').text(
						'Tag ID contains invalid character Please provide valid tag id.'
								+ value.substring(0, (value.length - 1)));
				$('#sub_error_span_1').css('display', '');
				// console.log('blockSpecialCharButNotUnderScore', 7);
				return false;
			}
		} else if (!regex.test(value)) {
			$('#sub_error_span').text(
					'Tag ID contains invalid character Please provide valid tag id.'
							+ value);
			$('#sub_error_span').css('display', '');
			// console.log('blockSpecialCharButNotUnderScore', 8);
			return false;
		}

		return true;
	},

	nextPageSwitch : function(to, from) {
		MigrationDataTagger.currentSchemaType = $('#database').val();

		if (to == 1 && from == 2) {
			var flag = MigrationDataTagger.blockSpecialCharButNotUnderScore($(
					'#tagname').val());
			// if ($('#tagname').val() == '') {
			// jAlert(
			// "Tag ID is not provided. Please provide a unique Tag ID",
			// "Invalid action");
			// $("#popup_container").css("z-index", "9999999");
			// return;
			// }
			if (!flag) {
				return false;
			}
			RemoteManager.isTagExist($('#tagname').val(),
					MigrationDataTagger.tagIdexists);
			$('#secondPage').css('display', '');
			$('#firstPage').css('display', 'none');
			MigrationDataTagger.currentPage = 2;
			MigrationDataTagger.showActivatedTabs();
			MigrationDataTagger.canBeViewed = true;
		} else if (to == 2 && from == 3) {
			$('#sub_error_span_1').html('');
			$('#sub_error_span_1').css('display', 'none');
			var tagNameList = new Array();
			for (var i = 1; i <= MigrationDataTagger.tagcount; i++) {
				tagname = $('#tagname_' + i).val();
				tagNameList.push(tagname);
			}
			// console.log('tagNameList', tagNameList);
			for (var i = 1; i <= MigrationDataTagger.tagcount; i++) {
				var flag = MigrationDataTagger
						.blockSpecialCharButNotUnderScore($('#tagname_' + i)
								.val()
								+ '$');
				if (!flag) {
					return false;
				}

				if ($('#tagname_' + i).val() == '') {
					$('#sub_error_span_1')
							.text(
									'Tag ID is not provided. Please provide a unique Tag ID');
					$('#sub_error_span_1').css('display', '');
					return;
				} else {
					var tagname = '';
					for (var j = 0; j < tagNameList.length; j++) {
						// if (index > -1 && ) {
						if (j != (i - 1)
								&& $('#tagname_' + i).val().toUpperCase() == tagNameList[j]
										.toUpperCase()) {
							$('#sub_error_span_1').text(
									'TagId already Exists. Enter Different TagID: '
											+ $('#tagname_' + i).val());
							$('#sub_error_span_1').css('display', '');
							return;
						}
					}
				}
			}
			MigrationDataTagger.canBeViewed = true;
			$('#secondPage').css('display', 'none');
			$('#thirdPage').css('display', '');
			MigrationDataTagger.currentPage = 3;
			MigrationDataTagger.showActivatedTabs();
		} else if (to == 2 && from == 1) {
			$('#secondPage').css('display', 'none');
			$('#firstPage').css('display', '');
			MigrationDataTagger.currentPage = 1;
			MigrationDataTagger.showActivatedTabs();
		} else if (to == 3 && from == 1) {
			$('#thirdPage').css('display', 'none');
			$('#firstPage').css('display', '');
			MigrationDataTagger.currentPage = 1;
			MigrationDataTagger.showActivatedTabs();
		} else if (to == 3 && from == 2) {
			$('#secondPage').css('display', '');
			$('#thirdPage').css('display', 'none');
			MigrationDataTagger.currentPage = 2;
			MigrationDataTagger.showActivatedTabs();
		}
	},
	showfirstPage : function() {
		$('#firstPage').css('display', 'none');
		$('#generaldiv').css('display', 'none');
		$('#firstPage').css('display', '');
		$('#secondPage').css('display', 'none');
		$('#viewJSON').css('display', 'none');
	},
	showFirstPage : function() {
		$('#firstPage').css('display', '');
		$('#generaldiv').css('display', '');
		$('#viewJSON').css('display', 'none');
		if (MigrationDataTagger.selectedType == 'JSON') {
			$('#viewJSON').css('display', '');
			$('#generaldiv').css('display', 'none');
		}
		$('#firstPage').css('display', 'none');
		$('#secondPage').css('display', 'none');
		$('#processingDiv').css('display', 'none');
	},

	getTagVacant : function() {
		for (var i = 1; i <= 10; i++) {
			if (MigrationDataTagger.tagVacant[i] == 0)
				return i;
		}
	},

	addConditionClicked : function(rid, cid) {
		// console.log('rid conditionclicked ', rid);
		// console.log('cid conditionclicked ', cid);
		// console.log('MigrationDataTagger.conditioncountJson[rid]
		// conditionclicked ', MigrationDataTagger.conditioncountJson[rid]);

		if (MigrationDataTagger.conditioncountJson[rid] == undefined) {
			MigrationDataTagger.conditioncountJson[rid] = 1;
		}
		var temp = MigrationDataTagger.conditioncountJson[rid] + 1;
		// console.log('temp', temp);

		// var tagcount = MigrationDataTagger.tagcount;
		// console.log('tagcount' , tagcount);
		var tbl_data = '<tr id="row_'
				+ rid
				+ '_'
				+ temp
				+ '">'
				+ '<td>'
				+ '<select style="width: 100%" id="attr_'
				+ rid
				+ '_'
				+ temp
				+ '" onchange="javascript:MigrationDataTagger.updateRelOp('
				+ rid
				+ ','
				+ temp
				+ ')">'
				+ '<option value="0">--Select--</option>'
				+ '</select>'
				+ '</td>'
				+ '<td>'
				+ '<select id="op_'
				+ rid
				+ '_'
				+ temp
				+ '" style="width: 100%;">'
				+ '<option value="=">=</option>'
				+ '<option value="!=">!=</option>'
				+ '<option value=">">></option>'
				+ '<option value="<">&#60;</option>'
				+ '<option value=">=">>=</option>'
				+ '<option value="<=">&#60;=</option>'
				+ '<option value="CONTAINS">CONTAINS</option>'
				+ '<option value="STARTSWITH">STARTS WITH</option>'
				+ '<option value="ENDSWITH">ENDS WITH</option>'
				+ '<option value="CONTAINEDIN">IN</option>'
				+ '<option value="NOTCONTAINEDIN">NOT IN</option>'
				+ '<option value="LIKE">LIKE</option>'
				+ '<option value="NOTLIKE">NOT LIKE</option>'
				+ '<option value="BETWEEN">BETWEEN</option>'
				+ '<option value="NOTBETWEEN">NOT BETWEEN</option>'
				+ '</select>'
				+ '</td>	'
				+ '<td>'
				+ '<input type="text" id="cond_'
				+ rid
				+ '_'
				+ temp
				+ '" placeholder="value">'
				+ '</td>'
				+ '<td>'
				+ '<select id="lop_'
				+ rid
				+ '_'
				+ temp
				+ '" disabled="disabled" style="width: 75%;">'
				+ '<option value="or">OR</option>	'
				+ '<option value="and">AND</option>'
				+ '</select>'
				+ '</td>'
				+ '<td>'
				+ '<a href="javascript:MigrationDataTagger.addConditionClicked('
				+ rid
				+ ','
				+ temp
				+ ' )">'
				+ '<img alt="Add Volume" src="images/plus_sign_brown.png" id="plusImage" style="height: 15px;">'
				+ '</a>'
				+ '<a href="javascript:MigrationDataTagger.removeConditionClicked('
				+ rid
				+ ','
				+ temp
				+ ')" style="color: white;">'
				+ '<img alt="Remove Volume" src="images/minus_sign_brown.png" id="minusImage" style="height: 10px; width: 20px;">'
				+ '</a>' + '</td>' + '</tr>';

		$('#conditionDiv_' + rid + ' #tag_list_table_' + rid).append(tbl_data);
		$('#conditionDiv_' + rid + ' #tag_edit_table_' + rid).append(tbl_data);
		// MigrationDataTagger.conditioncount = temp;
		MigrationDataTagger.conditioncountJson[rid] = temp;
		MigrationDataTagger.populateAttribute(temp, rid);
		temp = temp - 1;
		$('#conditionDiv_' + rid + ' #lop_' + rid + '_' + temp).prop(
				'disabled', false);
	},

	removeConditionClicked : function(rid, cid) {
		if (MigrationDataTagger.conditioncountJson[rid] == 1) {
			$('#conditionDiv_' + rid + ' #cond_1_1').val('');
			MigrationDataTagger.closeConditionDiv();
			return;
		}
		$(
				'#conditionDiv_' + rid + ' #row_' + rid + '_'
						+ MigrationDataTagger.conditioncountJson[rid]).remove();
		MigrationDataTagger.conditioncountJson[rid] = MigrationDataTagger.conditioncountJson[rid] - 1;
		temp = MigrationDataTagger.conditioncountJson[rid];
		$('#conditionDiv_' + rid + ' #lop_' + rid + '_' + temp).prop(
				'disabled', true);
	},

	addTagClicked : function() {
		var temp = MigrationDataTagger.tagcount + 1;
		var tag_data = '<tr id="row_'
				+ temp
				+ '">'
				+ '<td colspan="1" style="text-align: left;"><input '
				+ 'style="width: 97%;" type="text" id="tagname_'
				+ temp
				+ '" '
				+ 'placeholder="Tag ID" '
				+ ' onkeypress="javascript:Util.blockSpecialCharButNotUnderScore(window.event, this);"></td>'
				+ '<td id="where_operator_'
				+ temp
				+ '" colspan="1" style=""><span '
				+ 'class="sbox" style=""><input type="search" '
				+ 'id="operatorBox_'
				+ temp
				+ '" placeholder="Operator" results="0" '
				+ 'onclick="MigrationDataTagger.showOperatorDiv('
				+ temp
				+ ');" '
				+ 'onfocus="MigrationDataTagger.showOperatorDiv('
				+ temp
				+ ');" '
				+ 'style="width: 100%;" readonly></span> '
				+ '<div id="operatorDiv_'
				+ temp
				+ '" class="filtersBlock" '
				+ 'style="display: none; max-height: 250px;"> '
				+ '<span id="selectColClose1" class="divcloser"> '
				+ '<a href="javascript:MigrationDataTagger.closeOperatorDiv('
				+ temp
				+ ');"> '
				+ '<img src="images/light-box-close.png" '
				+ 'class="closerImage"> '
				+ '</a> '
				+ '</span> '
				+ '<table id="op_list_table_'
				+ temp
				+ '">'
				+ '	<tr>'
				+ '<td colspan="2"'
				+ '	style="width: 20%; text-align: left;"><input '
				+ '	type="radio" checked="checked" '
				+ '	name="metatagvalue_'
				+ temp
				+ '" id="mglobal_'
				+ temp
				+ '"'
				+ '	onclick="javascript:MigrationDataTagger.metachange(1,'
				+ temp
				+ ');"> '
				+ '	&nbsp Global Function &nbsp &nbsp &nbsp <input '
				+ '	type="radio" name="metatagvalue_'
				+ temp
				+ '" id="moperation_'
				+ temp
				+ '"'
				+ '	onclick="javascript:MigrationDataTagger.metachange(2,'
				+ temp
				+ ');"> '
				+ '	&nbsp Table Column Operator &nbsp &nbsp &nbsp <input '
				+ '	type="radio" name="metatagvalue_'
				+ temp
				+ '" id="mconstant_'
				+ temp
				+ '"'
				+ '	onclick="javascript:MigrationDataTagger.metachange(3,'
				+ temp
				+ ');"> '
				+ '	&nbsp Constant Value</td>'
				+ '</tr>'
				+ '<tr id="metaconstant_'
				+ temp
				+ '"'
				+ 'style="display: none;"> '
				// + '<td '
				// + 'style="width: 20%; padding: 0px; text-align: left;"></td>'
				+ '<td style="width: 70%; padding: 5px;"><input '
				+ '	id="metatagvaluec_'
				+ temp
				+ '"'
				+ ' style="width: 72%;" '
				+ '	type="text" placeholder="Constant"> <select '
				+ '	id="metaConstantDataType_'
				+ temp
				+ '"'
				+ ' style="width: 22%;">'
				+ '		<option value="STRING">STRING</option>'
				+ '		<option value="TIMESTAMP">TIMESTAMP</option>'
				+ '		<option value="LONG">LONG</option>'
				+ '		<option value="SHORT">SHORT</option>'
				+ '		<option value="INTEGER">INTEGER</option>'
				+ '		<option value="DOUBLE">DOUBLE</option>'
				+ '		<option value="DECIMAL">DECIMAL</option>'
				+ '		<option value="BLOB">BLOB</option>'
				+ '		<option value="BOOLEAN">BOOLEAN</option>'
				+ '</select></td>'
				+ '</tr>'
				+ '<tr id="metaoperation_'
				+ temp
				+ '"'
				+ ' style="display: none;">'
				// + '<td'
				// + ' style="width: 20%; padding: 0px; text-align: left;
				// height: 31px;"></td>'
				+ '<td style="width: 70%; padding: 5px;"><span'
				+ '	class="sbox" style=""> <input'
				+ '		type="search" id="metavaluefun_'
				+ temp
				+ '" '
				+ '		placeholder="Select Operator" results="0" '
				+ '		onclick="MigrationDataTagger.showmetaOperationDiv('
				+ temp
				+ ');"'
				+ '		onfocus="MigrationDataTagger.showmetaOperationDiv('
				+ temp
				+ ');"'
				+ '		style="width: 101%;" readonly>'
				+ '</span> '
				+ '	<div id="metaoperationdiv_'
				+ temp
				+ '" class="filtersBlock" '
				+ '		style="display: none; max-height: 250px; overflow-y: auto;"> '
				+ '		<span id="selectColClose" class="divcloser"> '
				+ '		<a'
				+ '			href="javascript:MigrationDataTagger.closemetaOperationDiv('
				+ temp
				+ ');"> '
				+ '			<img src="images/light-box-close.png" '
				+ '			class="closerImage">'
				+ '					</a>'
				+ '					</span>'
				+ '					<table id="metaoperationdivtable_'
				+ temp
				+ '">'
				+ '					</table>'
				+ '				</div></td>'
				+ '		</tr>'
				+ '		<tr id="metaglobal_'
				+ temp
				+ '" style="display:;">'
				// + ' <td'
				// + ' style="width: 20%; padding: 0px; text-align:
				// left;"></td>'
				+ '			<td style="width: 30%; padding: 5px;"><select '
				+ '				id="metavalueop_'
				+ temp
				+ '" style="width: 100%;" '
				+ '				onchange="javascript:MigrationDataTagger.metaValueChanged('
				+ temp
				+ ');"> '
				+ '					<option value="WordExists">WordExists</option> '
				+ '					<option value="WordOccurrenceCount">WordOccurrenceCount</option> '
				+ '					<option value="PatternExists">PatternExists</option>'
				+ '					<option value="PatternMatchCount">PatternMatchCount</option>'
				+ '					<option value="TotalWordCount">TotalWordCount</option>'
				+ '					<option value="TotalLineCount">TotalLineCount</option>'
				+ '			</select></td>'
				+ '			<td style="width: 40%; padding: 5px;"><input '
				+ '				type="text" id="metavaluecol_'
				+ temp
				+ '" '
				+ '				placeholder="Specify word"></td> '
				+ '		</tr>'
				+ '	</table>'
				+ '</div></td>'
				+ '<td id="where_select_'
				+ temp
				+ '" style="" colspan="1"><span '
				+ 'class="sbox" style=""> <input type="search" '
				+ '	id="conditionBox_'
				+ temp
				+ '" placeholder="Where..." '
				+ '	results="0" '
				+ '	onclick="MigrationDataTagger.showConditionDiv('
				+ temp
				+ ');" '
				+ '	onfocus="MigrationDataTagger.showConditionDiv('
				+ temp
				+ ');" '
				+ '	style="width: 100%;" readonly> '
				+ '</span> '
				+ '<div id="conditionDiv_'
				+ temp
				+ '" class="filtersBlock" '
				+ '	style="display: none; max-height: 250px; overflow-y: auto;">'
				+ '	<span id="selectColClose" class="divcloser">'
				+ '	<a'
				+ '		href="javascript:MigrationDataTagger.closeConditionDiv('
				+ temp
				+ ');">'
				+ '		<img src="images/light-box-close.png" '
				+ '			class="closerImage">  '
				+ '	</a>  '
				+ '	</span> '
				+ '	<table id="tag_list_table_'
				+ temp
				+ '"> '
				+ '		<tbody> '
				+ '			<tr> '
				+ '				<td nowrap="nowrap">Select Column</td> '
				+ '				<td nowrap="nowrap">Relational Operator</td> '
				+ '			    <td nowrap="nowrap">Value</td> '
				+ '				<td nowrap="nowrap">Logical Operator</td> '
				+ '				<td></td> '
				+ '			</tr> '
				+ '			<tr id="row_'
				+ temp
				+ '_1"> '
				+ '				<td><select style="width: 100%" id="attr_'
				+ temp
				+ '_1" '
				+ '					onchange="javascript:MigrationDataTagger.updateRelOp('
				+ temp
				+ ',1);"> '
				+ '				<option value="0">--Select--</option> '
				+ '				</select></td> '
				+ '				<td><select id="op_'
				+ temp
				+ '_1" style="width: 100%;"> '
				+ '						<option value="=">=</option> '
				+ '						<option value="!=">!=</option> '
				+ '						<option value=">">></option> '
				+ '						<option value="<">&#60;</option> '
				+ '						<option value=">=">>=</option> '
				+ '						<option value="<=">&#60;=</option> '
				+ '						<option value="CONTAINS">CONTAINS</option> '
				+ '						<option value="STARTSWITH">STARTS WITH</option> '
				+ '						<option value="ENDSWITH">ENDS WITH</option> '
				+ '						<option value="CONTAINEDIN">IN</option> '
				+ '						<option value="NOTCONTAINEDIN">NOT IN</option> '
				+ '						<option value="LIKE">LIKE</option> '
				+ '						<option value="NOTLIKE">NOT LIKE</option> '
				+ '						<option value="BETWEEN">BETWEEN</option> '
				+ '						<option value="NOTBETWEEN">NOTBETWEEN</option> '
				+ '				</select></td> '
				+ '				<td><input type="text" id="cond_'
				+ temp
				+ '_1" placeholder="value"></td> '
				+ '				<td><select id="lop_'
				+ temp
				+ '_1" disabled="disabled" style="width: 75%;">  '
				+ '						<option value="or">OR</option> '
				+ '						<option value="and">AND</option> '
				+ '				</select></td> '
				+ '				<td><a '
				+ '					href="javascript:MigrationDataTagger.addConditionClicked('
				+ temp
				+ ', 1 )"> '
				+ '						<img alt="Add Volume" '
				+ '						src="images/plus_sign_brown.png" '
				+ '						id="plusImage" style="height: 15px;"> '
				+ '				</a> <a '
				+ '					href="javascript:MigrationDataTagger.removeConditionClicked('
				+ temp + ', 1)"  ' + '					style="color: white;"> <img '
				+ '						alt="Remove Volume" '
				+ '						src="images/minus_sign_brown.png" '
				+ '						id="minusImage" '
				+ '						style="height: 10px; width: 20px;"> '
				+ '				</a></td> ' + '			</tr> ' + '	</table> '
				+ '</div></td> ' + '<td><a '
				+ 'href="javascript:MigrationDataTagger.addTagClicked()">  '
				+ '	<img alt="Add Sign" '
				+ '	src="images/plus_sign_brown.png" id="plusImage" '
				+ '	style="height: 15px;"> ' + '</a> <a '
				+ '	href="javascript:MigrationDataTagger.removeTagClicked('
				+ temp + ')" '
				+ 'style="color: white;"> <img alt="Remove Sign" '
				+ '	src="images/minus_sign_brown.png" id="minusImage" '
				+ '	style="height: 10px; width: 20px;"> ' + '</a></td> '
				+ '</tr>';

		// console.log('tagdata :', tag_data);
		$('#main_table_list').append(tag_data);
		$('#tagTable').append(tag_data);
		MigrationDataTagger.populateAttribute(1, temp);
		MigrationDataTagger.tagcount = temp;
		MigrationDataTagger.conditioncountJson[temp] = 1;
	},

	removeTagClicked : function(id) {
		if (MigrationDataTagger.tagcount == 1) {
			jAlert("There should be atleast one Tag.", "Invalid action");
			$("#popup_container").css("z-index", "9999999");
			return;
		} else {
			$('#row_' + id).remove();
			MigrationDataTagger.tagcount = MigrationDataTagger.tagcount - 1;
		}
	},

	populateAttribute : function(id, rid) {

		// console.log('ID populateAttribute :', id);
		// console.log('RID populateAttribute : ', rid);
		var list = '';
		var object = MigrationDataTagger.conditionalColumnsList;

		for (var i = 0; i < MigrationDataTagger.conditionalColumnsList.length; i++) {
			var temp = "<option value='" + object[i]['colName'] + "'>"
					+ object[i]['colName'] + "</option>";
			list += temp;
		}

		var value = $('#conditionDiv_' + rid + ' #attr_' + rid + '_' + id)
				.val();
		var value1 = $('#conditionDiv_' + rid + ' #tagvaluecol').val();

		// console.log('list : ' + list);
		$('#conditionDiv_' + rid + ' #attr_' + rid + '_' + id).html(list);
		MigrationDataTagger.removeDuplicatesFromDropDown('#conditionDiv_' + rid
				+ ' #attr_' + rid + '_' + id);
		$('#conditionDiv_' + rid + ' #tagvaluecol').html(list);

		if (value != '0')
			$('#conditionDiv_' + rid + ' #attr_' + rid + '_' + id).val(value);
		if (value1 != '0')
			$('#conditionDiv_' + rid + ' #tagvaluecol').val(value1);
		MigrationDataTagger.updateRelOp(rid, id);
	},

	removeDuplicatesFromDropDown : function(id) {
		var usedNames = {};
		$(id + " > option").each(function() {
			if (usedNames[this.text]) {
				$(this).remove();
			} else {
				usedNames[this.text] = this.value;
			}
		});
	},

	tagIdexists : function(response) {
		if (response == true) {
			jAlert("Tag ID already exists. Please provide a unique Tag ID",
					"Invalid action");
			$("#popup_container").css("z-index", "9999999");
			$('#firstPage').css('display', '');
			$('#secondPage').css('display', 'none');
			MigrationDataTagger.currentPage = 1;
			MigrationDataTagger.showActivatedTabs();
		} else {
			if (MigrationDataTagger.currentPage == 2) {
				$('#firstPage').css('display', 'none');
				$('#secondPage').css('display', '');
				MigrationDataTagger.showActivatedTabs();
			} else if (MigrationDataTagger.currentPage == 3) {
				$('#firstPage').css('display', 'none');
				$('#thirdPage').css('display', '');
				MigrationDataTagger.showActivatedTabs();
			}
		}
	},

	saveEditEntry : function() {
		MigrationDataTagger.currentSchemaType = $('#database').val();

		$('#sub_error_span_1').html('');
		$('#sub_error_span_1').css('display', 'none');
		var tagNameList = new Array();
		for (var i = 1; i <= MigrationDataTagger.tagcount; i++) {
			tagname = $('#tagname_' + i).val();
			tagNameList.push(tagname);
		}
		// console.log('tagNameList', tagNameList);
		for (var i = 1; i <= MigrationDataTagger.tagcount; i++) {
			var flag = MigrationDataTagger.blockSpecialCharButNotUnderScore($(
					'#tagname_' + i).val()
					+ '$');
			if (!flag) {
				return false;
			}

			if ($('#tagname_' + i).val() == '') {
				$('#sub_error_span_1')
						.text(
								'Tag ID is not provided. Please provide a unique Tag ID');
				$('#sub_error_span_1').css('display', '');
				return;
			} else {
				var tagname = '';
				for (var j = 0; j < tagNameList.length; j++) {
					// if (index > -1 && ) {
					if (j != (i - 1)
							&& $('#tagname_' + i).val().toUpperCase() == tagNameList[j]
									.toUpperCase()) {
						$('#sub_error_span_1').text(
								'TagId already Exists. Enter Different TagID: '
										+ $('#tagname_' + i).val());
						$('#sub_error_span_1').css('display', '');
						return;
					}
				}
			}
		}

		if (!MigrationDataTagger.validateTags()) {
			jAlert("Some fields are not filled. Please fill all the details.",
					"Invalid action");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		if ($('#onschedule').is(':checked') == true) {
			if ($('#starttime').val() == '') {
				jAlert("Please provide a schedule time to schedule tagging.",
						"Invalid action");
				$("#popup_container").css("z-index", "9999999");
				return;
			}
		}
		tJSON = MigrationDataTagger.createJSONofTags();
		MigrationDataTagger.EditJson["Tags"] = tJSON["Tags"];
		MigrationDataTagger.EditJson["Attributes"] = tJSON["Attributes"];

		var id = $('#tagname').val();

		var desc = $('#tagdesc').val();
		var tagtype = MigrationDataTagger.currentSchemaType;
		var isactive = $('#isactive').is(':checked');

		var applyDataTagObj = new Object();
		applyDataTagObj["postIngestTimeDetail"] = new Object();
		applyDataTagObj["RecordLevel"] = new Object();
		var iPostIngestType = false;
		var fileLevelProcessing = false;
		if ($('#oningest').is(':checked')) {
			iPostIngestType = false;
		} else {
			iPostIngestType = true;
		}
		applyDataTagObj["isPostIngest"] = iPostIngestType;
		if (iPostIngestType) {
			if ($('#frequency_interval').val() == '') {
				jAlert(
						'Frequency for post-ingest tag parsing is not defined.Please define frequency.',
						'Frequency not defined');
				$("#popup_container").css("z-index", "99999999");
				return;
			}
			if ($('#post_ingest_starttime').val() == '') {
				jAlert(
						'Starting time for post-ingest tag parsing is not defined.Please define starting time.',
						'Starting time not defined');
				$("#popup_container").css("z-index", "99999999");
				return;
			}
			applyDataTagObj["postIngestTimeDetail"]["Frequency"] = $(
					'#frequency_interval').val();
			applyDataTagObj["postIngestTimeDetail"]["timeUnit"] = $(
					'#time_unit').val();
			applyDataTagObj["postIngestTimeDetail"]["StartingTime"] = $(
					'#post_ingest_starttime').val();
			// Added Here
			if ($('#filelevelprocessing').is(':checked')
			// || $('#filelevelprocessing1').is(':checked')
			|| $('#filelevelprocessing2').is(':checked')) {
				fileLevelProcessing = true;
				applyDataTagObj["RecordLevel"] = false;
			} else {
				fileLevelProcessing = false;
				applyDataTagObj["RecordLevel"] = true;
			}
		}

		var applytag = false;
		// Add here
		if ($('#applytag').is(':checked')) {
			applytag = true;
		} else {
			applytag = false;
		}
		applyDataTagObj["applyTag"] = applytag;
		applyDataTagObj["applyTagTimeDetail"] = new Object();
		if ($('#applyNow').is(':checked')) {
			// $('#post-ingest_row-3').hide();
			// $('#post-ingest_row-4').hide();

			applyDataTagObj["applyTagTimeDetail"]["isApplyNow"] = true;
			applyDataTagObj["applyTagTimeDetail"]["scheduleTime"] = "0";
			// Added Here
			if ($('#filelevelprocessing').is(':checked')
			// || $('#filelevelprocessing1').is(':checked')
			|| $('#filelevelprocessing2').is(':checked')) {
				fileLevelProcessing = true;
				applyDataTagObj["RecordLevel"] = false;
			} else {
				fileLevelProcessing = false;
				applyDataTagObj["RecordLevel"] = true;
			}

		} else {
			applyDataTagObj["applyTagTimeDetail"]["isApplyNow"] = false;
			applyDataTagObj["applyTagTimeDetail"]["scheduleTime"] = $(
					'#starttime').val();
			// Added Here
			// $('#post-ingest_row-3').hide();
			// $('#post-ingest_row-4').hide();
			if ($('#filelevelprocessing').is(':checked')
			// || $('#filelevelprocessing1').is(':checked')
			|| $('#filelevelprocessing2').is(':checked')) {
				fileLevelProcessing = true;
				applyDataTagObj["RecordLevel"] = false;
			} else {
				fileLevelProcessing = false;
				applyDataTagObj["RecordLevel"] = true;
			}
		}

		// var operationType = '';
		// if ($('#mglobal').is(':checked')) {
		// operationType = 'global';
		// } else if ($('#moperation').is(':checked')) {
		// operationType = 'table';
		// } else {
		// operationType = 'constant';
		// }
		// 
		// applyDataTagObj["operationType"] = operationType;

		applyDataTagObj["TagInfo"] = MigrationDataTagger.EditJson;
		DDT.currentOperation = 'edit';
		$('#popup_procesing').show();
		$('#update_form_box').hide();
		$('#host_create').css('width', '450px');
		$('#headerspan').html('Status');
		$('#msg_td').hide();
		$('#msg_td2').hide();
		RemoteManager.updateCustomTagMetadatData(id, JSON
				.stringify(MigrationDataTagger.EditJson), desc, isactive,
				tagtype, JSON.stringify(applyDataTagObj), DDT.responseCallBack);
	},

	saveEntry : function() {

		if (!MigrationDataTagger.validateTags()) {
			$('#secondPage').css('display', '');
			$('#firstPage').css('display', 'none');
			$('#thirdPage').css('display', 'none');
			MigrationDataTagger.currentPage = 2;
			MigrationDataTagger.showActivatedTabs();
			$('#sub_error_span_1').text(
					'Some fields are not filled. Please fill all the details.');
			$('#sub_error_span_1').css('display', '');
			// jAlert("Some fields are not filled. Please fill all the
			// details.","Invalid action");
			// $("#popup_container").css("z-index", "9999999");
			return;
		}
		if ($('#applytag').is(':checked') == true) {
			if ($('#applyNow').is(':checked') == false
					&& $('#onschedule').is(':checked') == false) {
				jAlert(
						"Please select one of the option apply now or schedule now",
						"Invalid action");
				$("#popup_container").css("z-index", "9999999");
				return;
			}
		}
		if ($('#onschedule').is(':checked') == true) {
			if ($('#starttime').val() == '') {
				jAlert("Please provide a schedule time to schedule tagging.",
						"Invalid action");
				$("#popup_container").css("z-index", "9999999");
				return;
			}
		}

		var applyDataTagObj = new Object();
		applyDataTagObj["postIngestTimeDetail"] = new Object();
		applyDataTagObj["RecordLevel"] = new Object();
		var iPostIngestType = false;
		if ($('#oningest').is(':checked')) {
			iPostIngestType = false;
		} else {
			iPostIngestType = true;
		}
		applyDataTagObj["isPostIngest"] = iPostIngestType;

		if (iPostIngestType) {
			if ($('#frequency_interval').val() == '') {
				jAlert(
						'Frequency for post-ingest tag parsing is not defined.Please define frequency.',
						'Frequency not defined');
				$("#popup_container").css("z-index", "99999999");
				return;
			}
			if ($('#post_ingest_starttime').val() == '') {
				jAlert(
						'Starting time for post-ingest tag parsing is not defined.Please define starting time.',
						'Starting time not defined');
				$("#popup_container").css("z-index", "99999999");
				return;
			}
			applyDataTagObj["postIngestTimeDetail"]["Frequency"] = $(
					'#frequency_interval').val();
			applyDataTagObj["postIngestTimeDetail"]["timeUnit"] = $(
					'#time_unit').val();
			applyDataTagObj["postIngestTimeDetail"]["StartingTime"] = $(
					'#post_ingest_starttime').val();
			// // Added Here
			if ($('#filelevelprocessing').is(':checked')
			// || $('#filelevelprocessing1').is(':checked')
			|| $('#filelevelprocessing2').is(':checked')) {
				fileLevelProcessing = true;
				applyDataTagObj["RecordLevel"] = false;
			} else {
				fileLevelProcessing = false;
				applyDataTagObj["RecordLevel"] = true;
			}
		}

		$('#addTagBtn').attr('disabled', 'disabled');

		var applytag = false;
		var fileLevelProcessing = false;
		if ($('#applytag').is(':checked')) {
			applytag = true;
		} else {
			applytag = false;
		}
		applyDataTagObj["applyTag"] = applytag;
		applyDataTagObj["applyTagTimeDetail"] = new Object();
		if ($('#applyNow').is(':checked') && applytag == true) {

			applyDataTagObj["applyTagTimeDetail"]["isApplyNow"] = true;
			applyDataTagObj["applyTagTimeDetail"]["scheduleTime"] = "0";
			// Added Here
			if ($('#filelevelprocessing').is(':checked')
			// || $('#filelevelprocessing1').is(':checked')
			|| $('#filelevelprocessing2').is(':checked')) {

				fileLevelProcessing = true;
				applyDataTagObj["RecordLevel"] = false;
			} else {
				fileLevelProcessing = false;
				applyDataTagObj["RecordLevel"] = true;
			}

		} else {
			applyDataTagObj["applyTagTimeDetail"]["isApplyNow"] = false;
			applyDataTagObj["applyTagTimeDetail"]["scheduleTime"] = $(
					'#starttime').val();
			// Added Here
			if ($('#filelevelprocessing').is(':checked')
			// || $('#filelevelprocessing1').is(':checked')
			|| $('#filelevelprocessing2').is(':checked')) {
				fileLevelProcessing = true;
				applyDataTagObj["RecordLevel"] = false;
			} else {
				fileLevelProcessing = false;
				applyDataTagObj["RecordLevel"] = true;
			}
		}

		tJSON = MigrationDataTagger.createJSONofTags();
		// console.log('tJSON: ', tJSON);
		// var id = tJSON["Tags"][0]["TagName"];
		var id = $('#tagname').val();
		var namenode = $('#namenode').val();
		var metadata = $('#database').val();
		var tableName = $('#schema').val();
		// to change
		var desc = $('#tagdesc').val();

		var dbtype = $('#database').val();
		var isactive = $('#isactive').is(':checked');

		var isTableAdhoc = MigrationDataTagger.tableMap[tableName];

		applyDataTagObj["isTableAdhoc"] = isTableAdhoc;
		// Moved in createJSONofTags function
		// var operationType = '';
		// if ($('#mglobal').is(':checked')) {
		// operationType = 'global';
		// } else if ($('#moperation').is(':checked')) {
		// operationType = 'table';
		// } else {
		// operationType = 'constant';
		// }
		// 
		// applyDataTagObj["operationType"] = operationType;
		applyDataTagObj["TagInfo"] = tJSON;
		DDT.browse = applyDataTagObj["applyTagTimeDetail"]["isApplyNow"];

		MigrationDataTagger.closeBox(false);
		DDT.currentOperation = 'add';
		Util.addLightbox('add_confirm', 'pages/popup.jsp');
		RemoteManager.insertCustomTagMetadatData(id, namenode, tableName, JSON
				.stringify(tJSON), desc, isactive, dbtype, JSON
				.stringify(applyDataTagObj), DDT.responseCallBack);
	},

	getDataTypeByTagValue : function(tagValue) {
		var operation = tagValue.substring(0, tagValue.indexOf('('));
		var columnName = tagValue.substring(tagValue.indexOf('(') + 1, tagValue
				.indexOf(')'));
		var dataType = MigrationDataTagger.DATATYPE_INTEGER;
		if (isAdhocTable) {

			if (operation.toUpperCase().indexOf('COUNT') != -1) {

				dataType = MigrationDataTagger.DATATYPE_INTEGER;

			} else if (operation.toUpperCase().indexOf('COPY')
					|| operation.toUpperCase().indexOf('MIN') != -1
					|| operation.toUpperCase().indexOf('MAX') != -1
					|| operation.toUpperCase().indexOf('AVG') != -1
					|| operation.toUpperCase().indexOf('SUM') != -1) {

				for (var i = 0; i < MigrationDataTagger.selectedColumnListJSON.length; i++) {

					var obj = MigrationDataTagger.selectedColumnListJSON[i];
					if (obj["colName"] == columnName) {
						dataType = obj["colType"];
						break;
					}
				}
			}

		} else {

			if (tagValue.toUpperCase().indexOf('EXIST') != -1) {
				dataType = MigrationDataTagger.DATATYPE_BOOLEAN;
			} else if (tagValue.toUpperCase().indexOf('COPY') != -1) {
				dataType = MigrationDataTagger.DATATYPE_STRING;

				for (var i = 0; i < MigrationDataTagger.selectedColumnListJSON.length; i++) {

					var obj = MigrationDataTagger.selectedColumnListJSON[i];
					if (obj["colName"] == columnName) {
						dataType = obj["colType"];
						break;
					}
				}

			} else if (tagValue.toUpperCase().indexOf('COUNT') != -1) {
				dataType = MigrationDataTagger.DATATYPE_INTEGER;
			}
		}

		return dataType;
	},
	createJSONofTags : function() {
		var tags = [];

		for (var i = 1; i <= MigrationDataTagger.tagcount; i++) {
			var tag = new Object();
			// console.log('in JSION func : ', MigrationDataTagger.tagcount);
			tag["TagName"] = $('#tagname_' + i).val();

			// console.log('tag["TagName"]', tag["TagName"]);
			var tableName = $('#schema').val();
			var isTableAdhoc = MigrationDataTagger.tableMap[tableName];

			if ($('#where_operator_' + i + ' #mconstant_' + i).is(':checked')) {
				tag["TagValue"] = $(
						'#where_operator_' + i + ' #metatagvaluec_' + i).val();
				var dataType = $(
						'#where_operator_' + i + ' #metaConstantDataType_' + i)
						.val();

				tag["dataType"] = dataType;
				tag["isMethod"] = "false";
				tag["operationType"] = "constant";
			} else if ($('#where_operator_' + i + ' #moperation_' + i).is(
					':checked')) {
				tag["TagValue"] = $(
						'#where_operator_' + i + ' #metavaluefun_' + i).val();
				var dataType = MigrationDataTagger
						.getDataTypeByTagValue(tag["TagValue"]);

				tag["dataType"] = dataType;
				tag["operationType"] = "table";
				tag["isMethod"] = "true";
			} else {
				tag["TagValue"] = $(
						'#where_operator_' + i + ' #metavalueop_' + i).val()
						+ '(\''
						+ $('#where_operator_' + i + ' #metavaluecol_' + i)
								.val() + '\')';
				var dataType = MigrationDataTagger
						.getDataTypeByTagValue(tag["TagValue"]);

				tag["dataType"] = dataType;
				tag["isMethod"] = "true";
				tag["operationType"] = "global";
			}

			var condJSON = new Array();
			var relOp = new Array();

			if (MigrationDataTagger.properCondition == true) {
				for (var j = 1; j <= MigrationDataTagger.conditioncountJson[i]; j++) {
					var cond = new Object();
					cond["Column"] = $('#attr_' + i + '_' + j).val();
					cond["Operator"] = $('#op_' + i + '_' + j).val();
					cond["Value"] = $('#cond_' + i + '_' + j).val();
					if (cond["Column"] == undefined
							|| cond["Column"].length == 0
							|| cond["Operator"] == undefined
							|| cond["Operator"].length == 0
							|| cond["Value"] == undefined
							|| cond["Value"].length == 0) {
						continue;
					}
					if (j < MigrationDataTagger.conditioncountJson[i]) {
						relOp.push($('#lop_' + i + '_' + j).val());
					}
					condJSON.push(cond);
				}
			}

			if (condJSON.length > 0)
				tag["Expressions"] = condJSON;
			else
				tag["Expressions"] = [];
			tag["Relations"] = relOp;
			// Added Here
			var selectedTableName = $("#schema").val();

			var MasterJSON = new Object();
			if ($('#filelevelprocessing').is(':checked')
					|| $('#filelevelprocessing2').is(':checked')) {

				// If FileLevel is selected , RecordLevel processing is off
				MasterJSON["RecordLevel"] = false;
			} else {
				MasterJSON["RecordLevel"] = true;
			}

			tags.push(tag);
		}
		MasterJSON["Attributes"] = MigrationDataTagger.selectedColumnListJSON;
		MasterJSON["Tags"] = new Array();
		MasterJSON["Tags"] = tags;
		return MasterJSON;
	},

	validateTags : function() {
		var checkValue = true;
		MigrationDataTagger.currentSchemaType = $('#database').val();
		for (var rid = 1; rid <= MigrationDataTagger.tagcount; rid++) {
			// console.log('rid validate value', rid);
			if ($('#operatorDiv_' + rid + ' #mconstant_' + rid).is(':checked')) {
				if ($('#operatorDiv_' + rid + ' #metatagvaluec_' + rid).val() == '')
					// console.log('1 if');
					checkValue = false;
			} else if ($('#operatorDiv_' + rid + ' #mglobal_' + rid).is(
					':checked')) {
				// console.log('value metavalop' , $('#operatorDiv_' + rid + '
				// #metavalueop_' + rid).val());
				if ($('#operatorDiv_' + rid + ' #metavalueop_' + rid).val() == 'TotalWordCount'
						|| $('#operatorDiv_' + rid + ' #metavalueop_' + rid)
								.val() == 'TotalLineCount') {
					// console.log('2 if');
					// okay
				} else {
					if ($('#operatorDiv_' + rid + ' #metavaluecol_' + rid)
							.val() == '') {
						// console.log('3 if');
						checkValue = false;
					}
				}
			} else if ($('#operatorDiv_' + rid + ' #moperation_' + rid).is(
					':checked')) {
				if ($('#operatorDiv_' + rid + ' #metavaluefun_' + rid).val() == '') {
					// console.log('4 if');
					checkValue = false;
				}
			}

			MigrationDataTagger.properCondition = true;
			// Added

			if ($('#row_' + rid + ' #tagname_' + rid).val() == ""
					|| $('#row_' + rid + ' #operatorBox_' + rid).val() == ""
			/* || $('#row_' + rid + ' #where_select_' + rid).val() == "" */) {
				checkValue = false;
				MigrationDataTagger.properCondition = false;

			}
		}
		return checkValue;
	},

	conditionFormValidation : function(rid) {

		var condString = '';

		$('#conditionBox_' + rid).val('');
		for (var j = 1; j <= MigrationDataTagger.conditioncountJson[rid]; j++) {
			if ($('#conditionDiv_' + rid + ' #attr_' + rid + '_' + j).val() == ''
					|| $('#conditionDiv_' + rid + ' #op_' + rid + '_' + j)
							.val() == ''
					|| $('#conditionDiv_' + rid + ' #cond_' + rid + '_' + j)
							.val() == '') {
				$('#conditionDiv_' + rid).fadeIn('slow');
				return false;
			}

			condString += $('#conditionDiv_' + rid + ' #attr_' + rid + '_' + j)
					.val()
					+ ' '
					+ $('#conditionDiv_' + rid + ' #op_' + rid + '_' + j).val()
					+ ' '
					+ $('#conditionDiv_' + rid + ' #cond_' + rid + '_' + j)
							.val() + ' ';
			if (j < MigrationDataTagger.conditioncountJson[rid])
				condString += $(
						'#conditionDiv_' + rid + ' #lop_' + rid + '_' + j)
						.val().toUpperCase()
						+ ' ';
			// + ' ';

		}
		$('#conditionBox_' + rid).val(condString);
		// MigrationDataTagger.conditioncountJson[rid] =
		// MigrationDataTagger.conditioncountJson[rid] + 1;
		return true;
	},

	operatorFormValidation : function(rid) {

		var opString = '';
		$('#operatorBox_' + rid).val('');
		if ($("#operatorDiv_" + rid + " #mglobal_" + rid).is(':checked')) {
			opString = 'GLOBAL'
					+ ' '
					+ $(
							'#operatorDiv_' + rid + ' #metavalueop_' + rid
									+ ' option:selected').text().toUpperCase();
			if (!($('#operatorDiv_' + rid + ' #metavaluecol_' + rid).val() == '')) {
				opString = opString
						+ '('
						+ $('#operatorDiv_' + rid + ' #metavaluecol_' + rid)
								.val() + ')';

			}
		} else if ($("#operatorDiv_" + rid + " #moperation_" + rid).is(
				':checked')) {
			// opString = 'TABLE_OPERATOR' + ' ' + $('#metavaluefun').val();
			var data = '';
			for (var i = 0; i <= MigrationDataTagger.selectedColumnListJSON.length; i++) {

				if ($('#operatorDiv_' + rid + ' #metaradio' + i).is(':checked')) {
					// console.log('in meta radio div');
					data = $('#operatorDiv_' + rid + ' #metaop' + i).val()
							+ '('
							+ $('#operatorDiv_' + rid + ' #metaattr' + i)
									.html() + ')';
					break;
				}
			}
			$('#metavaluefun_' + rid).val(data);
			opString = 'TABLE_OPERATOR' + ' ' + $('#metavaluefun_' + rid).val();

		} else if ($("#operatorDiv_" + rid + " #mconstant_" + rid).is(
				':checked')) {
			opString = 'CONSTANT'
					+ ' '
					+ $(
							'#operatorDiv_' + rid + ' #metaConstantDataType_'
									+ rid + ' option:selected').text()
							.toUpperCase() + '('
					+ $('#operatorDiv_' + rid + ' #metatagvaluec_' + rid).val()
					+ ')';
		}
		$('#operatorBox_' + rid).val(opString);
		return true;
	},

	tagFormValidation : function() {
		var operString = '';
		// $('#operatorBox').val('');
		for (var j = 1; j <= MigrationDataTagger.tagcount; j++) {
			if ($('#tagname_' + j).val() == ''
					|| $('#where_operator_' + j).val() == ''
					|| $('#where_select_' + j).val() == '') {
				// $('#tagDiv').fadeIn('slow');
				return false;
			}

			// condString += $('#attr_' + j).val() + ' ' + $('#op_' +
			// j).val()
			// + ' ' + $('#cond_' + j).val() + ' ';
			// if (j < MigrationDataTagger.conditioncount)
			// condString += $('#lop_' + j).val().toUpperCase() + ' ';

		}
		// $('#conditionBox').val(condString);
		return true;

	},

	tagValueChanged : function() {
		if ($('#tagvalue').val() == "constant") {
			$('#tagvaluec').css('display', '');
			$('#tagvaluecol').css('display', 'none');
			$('#tagtype').val(MigrationDataTagger.DATATYPE_STRING);
		} else if ($('#tagvalue').val() == "count"
				|| $('#tagvalue').val() == "distinctcount") {
			$('#tagtype').val(MigrationDataTagger.DATATYPE_INTEGER);
			MigrationDataTagger.populateAttribute(1);
			$('#tagvaluec').css('display', 'none');
			$('#tagvaluecol').css('display', '');
		} else if ($('#tagvalue').val() == "avg") {
			$('#tagtype').val(MigrationDataTagger.DATATYPE_DECIMAL);
			MigrationDataTagger.populateAttributeForInt();
			$('#tagvaluec').css('display', 'none');
			$('#tagvaluecol').css('display', '');
		} else {
			$('#tagtype').val(MigrationDataTagger.DATATYPE_INTEGER);
			MigrationDataTagger.populateAttributeForInt();
			$('#tagvaluec').css('display', 'none');
			$('#tagvaluecol').css('display', '');
		}
	},

	populateAttributeForInt : function() {
		var list = '';
		var object = MigrationDataTagger.selectedColumnListJSON;
		for (var i = 0; i < MigrationDataTagger.selectedColumnListJSON.length; i++) {
			if (object[i]['colType'] == MigrationDataTagger.DATATYPE_INTEGER
					|| object[i]['colType'] == MigrationDataTagger.DATATYPE_DECIMAL) {
				var temp = "<option value='" + object[i]['colName'] + "'>"
						+ object[i]['colName'] + "</option>";
				list += temp;
			}
		}

		var value = $('#tagvaluecol').val();
		$('#tagvaluecol').html(list);
		if (value != '0')
			$('#tagvaluecol').val(value);
	},

	updateRelOp : function(tagrowid, id) {
		// console.log('tagrowid', tagrowid);
		// console.log('id : :: ', id);
		var newval = $('#attr_' + tagrowid + '_' + id).val();
		// console.log('newval : ', newval);
		var type = MigrationDataTagger.findType(newval);

		var data = '<option value="=">=</option>'
				+ '<option value="!=">!=</option>';

		if (type.toUpperCase() == MigrationDataTagger.DATATYPE_STRING) {

			data += '<option value="CONTAINS">CONTAINS</option>'
					+ '<option value="STARTSWITH">STARTS WITH</option>'
					+ '<option value="ENDSWITH">ENDS WITH</option>'
					+ '<option value="CONTAINEDIN">IN</option>'
					+ '<option value="NOTCONTAINEDIN">NOT IN</option>'
					+ '<option value="IS NULL">IS NULL</option>'
					+ '<option value="IS NOT NULL">IS NOT NULL</option>'
					+ '<option value="LIKE">LIKE</option>'
					+ '<option value="NOTLIKE">NOT LIKE</option>';

		} else {
			data += '<option value=">">></option>'
					+ '<option value="<">&#60;</option>'
					+ '<option value=">=">>=</option>'
					+ '<option value="<=">&#60;=</option>'
					+ '<option value="BETWEEN">BETWEEN</option>'
					+ '<option value="NOTBETWEEN">NOT BETWEEN</option>';
		}

		$('#op_' + tagrowid + '_' + id).html(data);
	},

	findType : function(val) {
		var object = MigrationDataTagger.selectedColumnListJSON;
		var type = '';
		for (var i = 0; i < MigrationDataTagger.selectedColumnListJSON.length; i++) {
			if (object[i]['colName'] == val) {
				type = object[i]['colType'];
				break;
			}
		}
		return type;
	},

	showConditionDiv : function(id) {
		$('#conditionDiv_' + id).fadeIn('slow');
	},

	showOperatorDiv : function(id) {
		$('#operatorDiv_' + id).fadeIn('slow');
	},
	// Need to change--
	closeOperatorDiv : function(id) {
		if (!MigrationDataTagger.operatorFormValidation(id))
			;
		$('#operatorDiv_' + id).hide();
	},
	closeConditionDiv : function(id) {
		if (!MigrationDataTagger.conditionFormValidation(id))
			;
		$('#conditionDiv_' + id).hide();
	},

	// closeTagdiv : function() {
	// // if(!MigrationDataTagger.tagFormValidation());
	// //
	// // // $('#tagDiv').hide();
	// },
	showhiveOperationDiv : function() {
		$('#hiveoperationdiv').fadeIn('slow');
	},

	closehiveOperationDiv : function() {
		data = '';
		for (var i = 1; i <= MigrationDataTagger.selectedColumnListJSON.length; i++) {

			if ($('#hiveradio' + i).is(':checked')) {
				data = $('#hiveop' + i).val() + '(' + $('#hiveattr' + i).html()
						+ ')';
				break;
			}
		}
		$('#metavaluefun').val(data);

		$('#hiveoperationdiv').hide();
	},

	hivechange : function(val) {
		if (val == 1) {
			$('#hiveconstant').css('display', '');
			$('#hiveoperation').css('display', 'none');
			$('#hiveglobal').css('display', 'none');
		} else if (val == 2) {
			$('#hiveconstant').css('display', 'none');
			$('#hiveoperation').css('display', '');
			$('#hiveglobal').css('display', 'none');
		} else {
			$('#hiveconstant').css('display', 'none');
			$('#hiveoperation').css('display', 'none');
			$('#hiveglobal').css('display', '');
		}
	},

	metachange : function(val, rid) {
		var isAdhoc = false;
		var tableName = $('#schema').val();
		isAdhoc = MigrationDataTagger.tableMap[tableName];
		if (val == 1) {
			$('#operatorDiv_' + rid + ' #metaconstant_' + rid).css('display',
					'none');
			$('#operatorDiv_' + rid + ' #metaoperation_' + rid).css('display',
					'none');
			$('#operatorDiv_' + rid + ' #metaglobal_' + rid).css('display', '');

			var list = new Array();
			var count = 0;
			var isTableNone = $('#schema').val();

			if (isTableNone == 0) {
				isTableNone = true;
			} else {
				isTableNone = false;
			}

			if (isTableNone) {
				var columns = MigrationDataTagger.getDefaultHDFSCoreTags();
				for ( var columnName in columns) {
					var temp = new Object;
					temp["colName"] = columnName.toUpperCase();
					temp["colType"] = columns[columnName].toUpperCase();
					temp["colIndex"] = count;
					count++;
					list.push(temp);
				}
				MigrationDataTagger.conditionalColumnsList = list;

			} else {

				if (isAdhoc) {

					var selectedCols = jQuery.extend(true, [],
							MigrationDataTagger.selectedColumnListJSON);
					var columns = MigrationDataTagger.getDefaultHDFSCoreTags();
					;
					for ( var columnName in columns) {
						var temp = new Object;
						temp["colName"] = columnName.toUpperCase();
						temp["colType"] = columns[columnName].toUpperCase();
						temp["colIndex"] = count;
						count++;
						selectedCols.push(temp);
					}
					MigrationDataTagger.conditionalColumnsList = selectedCols;

				} else {
					MigrationDataTagger.conditionalColumnsList = jQuery.extend(
							true, [],
							MigrationDataTagger.selectedColumnListJSON);

				}

			}

			MigrationDataTagger.populateAttribute(1, rid);
		} else if (val == 2) {
			$('#operatorDiv_' + rid + ' #metaconstant_' + rid).css('display',
					'none');
			$('#operatorDiv_' + rid + ' #metaoperation_' + rid).css('display',
					'');
			$('#operatorDiv_' + rid + ' #metaglobal_' + rid).css('display',
					'none');
			$('#operatorDiv_' + rid + ' #metavaluefun_' + rid).val('');

			var list = new Array();
			var count = 0;
			var isTableNone = $('#schema').val();
			if (isTableNone == 0) {
				isTableNone = true;
			} else {
				isTableNone = false;
			}

			if (isAdhoc) {
				var selectedCols = jQuery.extend(true, [],
						MigrationDataTagger.selectedColumnListJSON);

				var columns = MigrationDataTagger.getDefaultHDFSCoreTags();
				;
				for ( var columnName in columns) {
					var temp = new Object;
					temp["colName"] = columnName.toUpperCase();
					temp["colType"] = columns[columnName].toUpperCase();
					temp["colIndex"] = count;
					count++;
					selectedCols.push(temp);
				}
				MigrationDataTagger.conditionalColumnsList = selectedCols;

			} else {
				MigrationDataTagger.conditionalColumnsList = jQuery.extend(
						true, [], MigrationDataTagger.selectedColumnListJSON);

			}

			MigrationDataTagger.populateAttribute(1, rid);
		} else {
			$('#operatorDiv_' + rid + ' #metaconstant_' + rid).css('display',
					'');
			$('#operatorDiv_' + rid + ' #metaoperation_' + rid).css('display',
					'none');
			$('#operatorDiv_' + rid + ' #metaglobal_' + rid).css('display',
					'none');

			var list = new Array();
			var count = 0;

			if (isAdhoc) {

				var selectedCols = jQuery.extend(true, [],
						MigrationDataTagger.selectedColumnListJSON);
				var columns = MigrationDataTagger.getDefaultHDFSCoreTags();
				;
				for ( var columnName in columns) {
					var temp = new Object;
					temp["colName"] = columnName.toUpperCase();
					temp["colType"] = columns[columnName].toUpperCase();
					temp["colIndex"] = count;
					count++;
					selectedCols.push(temp);
				}
				MigrationDataTagger.conditionalColumnsList = selectedCols;

			} else {
				MigrationDataTagger.conditionalColumnsList = jQuery.extend(
						true, [], MigrationDataTagger.selectedColumnListJSON);
			}

			MigrationDataTagger.populateAttribute(1, rid);
		}
	},

	metaradioclicked : function(obj, val) {
		var tableId = $(obj).closest("table").attr("id");
		for (var i = 0; i < MigrationDataTagger.selectedColumnListJSON.length; i++) {
			$('#' + tableId + ' #metaop' + i).attr('disabled', 'disabled');
		}
		$('#' + tableId + ' #metaop' + val).attr('disabled', false);
	},

	showmetaOperationDiv : function(rid) {

		if (MigrationDataTagger.isAdhoc
				&& MigrationDataTagger.isEmpty($('#metaoperationdivtable_'
						+ rid))) {
			var data = MigrationDataTagger.metaOperationsValueAdhoc.replace(
					new RegExp(MigrationDataTagger.REPLACE_ROW_ID, 'g'), rid);
			$('#metaoperationdivtable_' + rid).html(data);
		} else if (!MigrationDataTagger.isCustomParserRegistered
				&& MigrationDataTagger.isEmpty($('#metaoperationdivtable_'
						+ rid))) {
			var data = MigrationDataTagger.metaOperationsValueCustom.replace(
					new RegExp(MigrationDataTagger.REPLACE_ROW_ID, 'g'), rid);
			$('#metaoperationdivtable_' + rid).html(data);
		}
		$('#metaoperationdiv_' + rid).fadeIn('slow');
	},

	isEmpty : function(el) {
		return !$.trim(el.html())
	},

	closemetaOperationDiv : function(rid) {

		var data = '';
		for (var i = 0; i <= MigrationDataTagger.selectedColumnListJSON.length; i++) {
			if ($('#metaoperationdiv_' + rid + ' #metaradio' + i)
					.is(':checked')) {
				data = $('#metaoperationdiv_' + rid + ' #metaop' + i).val()
						+ '('
						+ $('#metaoperationdiv_' + rid + ' #metaattr' + i)
								.html() + ')';
				break;
			}
		}
		$('#operatorDiv_' + rid + ' #metavaluefun_' + rid).val(data);
		$('#metaoperationdiv_' + rid).hide();

	},

	matachange : function(val) {
		if (val == 1) {
			$('#mataconstant').css('display', '');
			$('#mataoperation').css('display', 'none');
		} else {
			$('#mataconstant').css('display', 'none');
			$('#mataoperation').css('display', '');
		}
	},

	metaValueChanged : function(rid) {
		if ($('#operatorDiv_' + rid + ' #metavalueop_' + rid).val() == 'WordExists'
				|| $('#operatorDiv_' + rid + ' #metavalueop_' + rid).val() == 'WordOccurrenceCount') {
			$('#operatorDiv_' + rid + ' #metavaluecol_' + rid).css('display',
					'');
			$('#operatorDiv_' + rid + ' #metavaluecol_' + rid).attr(
					'placeholder', 'Specify word');

		} else if ($('#operatorDiv_' + rid + ' #metavalueop_' + rid).val() == 'PatternExists'
				|| $('#operatorDiv_' + rid + ' #metavalueop_' + rid).val() == 'PatternMatchCount') {
			$('#operatorDiv_' + rid + ' #metavaluecol_' + rid).css('display',
					'');
			$('#operatorDiv_' + rid + ' #metavaluecol_' + rid).attr(
					'placeholder', 'Specify pattern');
		} else {
			$('#operatorDiv_' + rid + ' #metavaluecol_' + rid).css('display',
					'none');
			$('#operatorDiv_' + rid + ' #metavaluecol_' + rid).val('');
		}
	},

	hiveValueChanged : function() {
		if ($('#hivevalueop').val() == 'WordExists'
				|| $('#hivevalueop').val() == 'WordOccurrenceCount') {
			$('#hiveglobalvalue').css('display', '');
			$('#hiveglobalvalue').attr('placeholder', 'Specify word');

		} else if ($('#hivevalueop').val() == 'PatternExists'
				|| $('#hivevalueop').val() == 'PatternMatchCount') {
			$('#hiveglobalvalue').css('display', '');
			$('#hiveglobalvalue').attr('placeholder', 'Specify pattern');
		} else {
			$('#hiveglobalvalue').css('display', 'none');
			$('#hiveglobalvalue').val('');
		}
	},

	startEdit : function(id) {
		MigrationDataTagger.conditioncountJson = new Object();
		// MigrationDataTagger.tagNameList = new Array();
		MigrationDataTagger.conditioncountJson['1'] = 1;

		MigrationDataTagger.editID = id;
		RemoteManager.getCustomTagMetaataDetailById(id,
				MigrationDataTagger.populateEditForm);
	},

	populateEditForm : function(response) {

		var json = eval("(" + response["dataTaggingTimeInfo"] + ")");
		if (json != null) {

			if (json["isPostIngest"]) {
				$('#onpostingest').attr('checked', 'checked');
				$('#post-ingest_row-1').show();
				$('#frequency_interval').val(
						json["postIngestTimeDetail"]["Frequency"]);
				$('#time_unit').val(json["postIngestTimeDetail"]["timeUnit"]);
				$('#post_ingest_starttime').val(
						json["postIngestTimeDetail"]["StartingTime"]);

				$('#post-ingest_row-2').show();
				if (json["RecordLevel"]) {
					$("#filelevelprocessing2").attr('checked', false);
					$("#recordlevelprocessing2").attr('checked', true);
				} else {
					$("#filelevelprocessing2").attr('checked', true);
					$("#recordlevelprocessing2").attr('checked', false);
				}
				// Add here

			} else {
				$('#oningest').attr('checked', 'checked');
				$('#post-ingest_row-1').hide();
				$('#post-ingest_row-2').hide();
			}
		}

		var isTableTypeAdhoc = json["isTableAdhoc"];
		var database = response["dbType"];
		var tableName = response["tableName"];
		if (tableName == "0") {
			tableName = "None";
		}

		if (($.inArray(tableName.toUpperCase(), MigrationDataTagger.tableList) > -1)) {
			$("#recordlevelprocessing2").attr('disabled', false);
		} else {
			$("#recordlevelprocessing2").attr('disabled', 'disabled');
		}
		var tagInfo = json["TagInfo"];
		// change here
		$('#schema').val(tableName);

		$('#database').val(database);

		// var operationType = json["operationType"];

		MigrationDataTagger.selectedColumnListJSON = tagInfo["Attributes"];

		$('#tagname').val(MigrationDataTagger.editID);

		if (response["isActive"] == true)
			$('#isactive').attr('checked', true);
		else
			$('#isactive').attr('checked', false);

		$('#tagdesc').val(response["desc"]);

		var tagJson = response["json"];

		var tagsInfoList = jQuery.parseJSON(tagJson);
		var tags = new Array();
		if (tagsInfoList != null) {

			tags = tagsInfoList.Tags;

			if (tags.length > 0 && null != tags) {

				for (var i = 1, j = 0; i <= tags.length; i++, j++) {
					MigrationDataTagger.editMultipleTags(i);
					var subtaginfo = new Object();
					subtaginfo = tags[j];
					$('#tagname_' + i).val(subtaginfo.TagName);

				}
				for (var i = 1; i <= tags.length; i++) {
					if (MigrationDataTagger.selectedColumnListJSON.length == 0) {
						// console.log('IN if part');
						// console.log('i', i);
						$('#metaconstant_' + i).css('display', 'none');
						$('#metaoperation_' + i).css('display', 'none');
						$('#metaglobal_' + i).css('display', '');
						$('#mglobal').attr('checked', 'checked');
						$('#moperation_' + i).removeAttr('checked');
						$('#moperation_' + i).attr('disabled', true);

						$('#conditionBox_' + i).val('');
						$('#cond_' + i + '_1').val('');
						$('#conditionBox_' + i).attr('disabled', true);

						MigrationDataTagger.selectedColumnListJSON = [];
					} else {
						// console.log('IN else part');
						// console.log('i', i);
						$('#moperation_' + i).removeAttr("disabled");
						$('#conditionBox_' + i).removeAttr("disabled");

						MigrationDataTagger.populateMetaOperations(
								MigrationDataTagger.selectedColumnListJSON,
								isTableTypeAdhoc,
								MigrationDataTagger.isCustomParserRegistered);
						for (var k = 1; k <= 10; k++)
							// change this
							MigrationDataTagger.populateAttribute(k, i);
						// change- here
						MigrationDataTagger.updateRelOp(1, i);
					}

				}
			}
		}

		MigrationDataTagger.EditJson = eval("(" + tagJson + ")");
		;
		json = MigrationDataTagger.EditJson;

		MigrationDataTagger.conditionalColumnsList = MigrationDataTagger.selectedColumnListJSON;

		tags = tagsInfoList.Tags;
		if (tags.length > 0 && null != tags) {
			for (var i = 1; i <= tags.length; i++) {
				var subtaginfo = tags[i - 1];
				var tagvalue = subtaginfo.TagValue;
				var operationType = subtaginfo.operationType;
				if (operationType == 'constant') {
					$('#mconstant_' + i).attr('checked', true);
					$('#metaconstant_' + i).css('display', '');
					$('#metaoperation_' + i).css('display', 'none');
					$('#metaglobal_' + i).css('display', 'none');
					$('#metatagvaluec_' + i).val(tagvalue);
					$('#metaConstantDataType_' + i).val(subtaginfo.dataType);

				} else if (operationType == "global") {
					$('#mglobal_' + i).attr('checked', true);
					$('#metaconstant_' + i).css('display', 'none');
					$('#metaoperation_' + i).css('display', 'none');
					$('#metaglobal_' + i).css('display', '');

					var funcval = MigrationDataTagger.getFuncVal(tagvalue);

					var func = funcval[0];
					var val = funcval[1];

					$('#metavalueop_' + i).val(func);
					$('#metavaluecol_' + i).val(val);
					if (func == 'TotalWordCount' || func == 'TotalLineCount')
						$('#metavaluecol_' + i).css('display', 'none');
				} else {

					$('#moperation_' + i).attr('checked', true);
					$('#metaconstant_' + i).css('display', 'none');
					$('#metaglobal_' + i).css('display', 'none');
					$('#metaoperation_' + i).css('display', '');
					$('#metavaluefun_' + i).val(tagvalue);
					var columnName = tagvalue.substring(
							tagvalue.indexOf('(') + 1, tagvalue.indexOf(')'));
					var operation = tagvalue
							.substring(0, tagvalue.indexOf('('));
					var index = 0;
					for (var h = 0; h < MigrationDataTagger.selectedColumnListJSON.length; h++) {
						var obj = MigrationDataTagger.selectedColumnListJSON[h];
						if (obj["colName"] == columnName) {
							index = obj["colIndex"];
							break;
						}
					}
					MigrationDataTagger.showmetaOperationDiv(i);
					MigrationDataTagger.metaradioclicked(
							$('#metaoperationdivtable_' + i), index);

					$('#metaoperationdivtable_' + i + ' #metaradio' + index)
							.attr('checked', 'checked');
					$('#metaoperationdivtable_' + i + ' #metaop' + index).val(
							operation);

				}
				MigrationDataTagger.operatorFormValidation(i);

				var relations = new Array();
				var expressions = new Array();
				relations = subtaginfo.Relations;

				expressions = subtaginfo.Expressions;
				MigrationDataTagger.populateAttribute(1, 1);
				MigrationDataTagger.closeConditionDiv(1);

				if (expressions.length > 0) {
					for (var j = 1; j <= relations.length; j++) {
						MigrationDataTagger.addConditionClicked(i, j + 1);
					}
					for (var j = 0; j < expressions.length; j++) {
						var cond = expressions[j];
						var count = j + 1;
						MigrationDataTagger.populateAttribute(count, i);
						$('#tag_edit_table_' + i + ' #attr_' + i + '_' + count)
								.val(cond["Column"]);
						$('#tag_edit_table_' + i + ' #op_' + i + '_' + count)
								.val(cond["Operator"]);
						$('#tag_edit_table_' + i + ' #cond_' + i + '_' + count)
								.val(cond["Value"]);

						if (j < (expressions.length - 1)) {
							$(
									'#tag_edit_table_' + i + ' #lop_' + i + '_'
											+ count).val(relations[j]);
							$(
									'#tag_edit_table_' + i + ' #lop_' + i + '_'
											+ count).removeAttr('disabled');
						}
						$('.filtersBlock').hide();
					}
				}
				$('.filtersBlock').hide();
				MigrationDataTagger.conditionFormValidation(i);
				MigrationDataTagger.closeConditionDiv(i);
			}
		}
	},

	nextbuttonFunc : function(to, from) {
		MigrationDataTagger.currentSchemaType = $('#database').val();

		if (to == 1 && from == 2) {

			var flag = true;
			if ($('#tagname').val() == '') {
				flag = false;
			}
			if ($('#database').val() == '') {
				flag = false;
			}
			if (!flag) {
				return false;
			}

			$('#secondPage').css('display', '');
			$('#firstPage').css('display', 'none');
			MigrationDataTagger.currentPage = 2;
			MigrationDataTagger.showActivatedTabs();
			MigrationDataTagger.canBeViewed = true;
		} else if (to == 2 && from == 1) {
			$('#secondPage').css('display', 'none');
			$('#firstPage').css('display', '');
			MigrationDataTagger.currentPage = 1;
			MigrationDataTagger.showActivatedTabs();
		}
	},

	editMultipleTags : function(id) {

		var temp = id;
		// MigrationDataTagger.rowNum = 0;
		var createTagrowData = '<tr id="row_'
				+ temp
				+ '">'
				+ '<td colspan="1" style="text-align: left;"><input '
				+ 'style="width: 97%;" type="text" id="tagname_'
				+ temp
				+ '" '
				+ 'placeholder="Tag ID" '
				+ ' onkeypress="javascript:Util.blockSpecialCharButNotUnderScore(window.event, this);"></td>'
				+ '<td id="where_operator_'
				+ temp
				+ '" colspan="1" style=""><span '
				+ 'class="sbox" style=""><input type="search" '
				+ 'id="operatorBox_'
				+ temp
				+ '" placeholder="Operator" results="0" '
				+ 'onclick="MigrationDataTagger.showOperatorDiv('
				+ temp
				+ ');" '
				+ 'onfocus="MigrationDataTagger.showOperatorDiv('
				+ temp
				+ ');" '
				+ 'style="width: 100%;" readonly></span> '
				+ '<div id="operatorDiv_'
				+ temp
				+ '" class="filtersBlock" '
				+ 'style="display: none; max-height: 250px;"> '
				+ '<span id="selectColClose1" class="divcloser"> '
				+ '<a href="javascript:MigrationDataTagger.closeOperatorDiv('
				+ temp
				+ ');"> '
				+ '<img src="images/light-box-close.png" '
				+ 'class="closerImage"> '
				+ '</a> '
				+ '</span> '
				+ '<table id="op_list_table_'
				+ temp
				+ '">'
				+ '	<tr>'
				+ '<td colspan="2"'
				+ '	style="width: 20%; text-align: left;"><input '
				+ '	type="radio" checked="checked" '
				+ '	name="metatagvalue_'
				+ temp
				+ '" id="mglobal_'
				+ temp
				+ '"'
				+ '	onclick="javascript:MigrationDataTagger.metachange(1,'
				+ temp
				+ ');"> '
				+ '	&nbsp Global Function &nbsp &nbsp &nbsp <input '
				+ '	type="radio" name="metatagvalue_'
				+ temp
				+ '" id="moperation_'
				+ temp
				+ '"'
				+ '	onclick="javascript:MigrationDataTagger.metachange(2,'
				+ temp
				+ ');"> '
				+ '	&nbsp Table Column Operator &nbsp &nbsp &nbsp <input '
				+ '	type="radio" name="metatagvalue_'
				+ temp
				+ '" id="mconstant_'
				+ temp
				+ '"'
				+ '	onclick="javascript:MigrationDataTagger.metachange(3,'
				+ temp
				+ ');"> '
				+ '	&nbsp Constant Value</td>'
				+ '</tr>'
				+ '<tr id="metaconstant_'
				+ temp
				+ '"'
				+ 'style="display: none;"> '
				// + '<td '
				// + 'style="width: 20%; padding: 0px; text-align: left;"></td>'
				+ '<td style="width: 70%; padding: 5px;"><input '
				+ '	id="metatagvaluec_'
				+ temp
				+ '"'
				+ ' style="width: 72%;" '
				+ '	type="text" placeholder="Constant"> <select '
				+ '	id="metaConstantDataType_'
				+ temp
				+ '"'
				+ ' style="width: 22%;">'
				+ '		<option value="STRING">STRING</option>'
				+ '		<option value="TIMESTAMP">TIMESTAMP</option>'
				+ '		<option value="LONG">LONG</option>'
				+ '		<option value="SHORT">SHORT</option>'
				+ '		<option value="INTEGER">INTEGER</option>'
				+ '		<option value="DOUBLE">DOUBLE</option>'
				+ '		<option value="DECIMAL">DECIMAL</option>'
				+ '		<option value="BLOB">BLOB</option>'
				+ '		<option value="BOOLEAN">BOOLEAN</option>'
				+ '</select></td>'
				+ '</tr>'
				+ '<tr id="metaoperation_'
				+ temp
				+ '"'
				+ ' style="display: none;">'
				// + '<td'
				// + ' style="width: 20%; padding: 0px; text-align: left;
				// height: 31px;"></td>'
				+ '<td style="width: 70%; padding: 5px;"><span'
				+ '	class="sbox" style=""> <input'
				+ '		type="search" id="metavaluefun_'
				+ temp
				+ '" '
				+ '		placeholder="Select Operator" results="0" '
				+ '		onclick="MigrationDataTagger.showmetaOperationDiv('
				+ temp
				+ ');"'
				+ '		onfocus="MigrationDataTagger.showmetaOperationDiv('
				+ temp
				+ ');"'
				+ '		style="width: 101%;" readonly>'
				+ '</span> '
				+ '	<div id="metaoperationdiv_'
				+ temp
				+ '" class="filtersBlock" '
				+ '		style="display: none; max-height: 250px; overflow-y: auto;"> '
				+ '		<span id="selectColClose" class="divcloser"> '
				+ '		<a'
				+ '			href="javascript:MigrationDataTagger.closemetaOperationDiv('
				+ temp
				+ ');"> '
				+ '			<img src="images/light-box-close.png" '
				+ '			class="closerImage">'
				+ '					</a>'
				+ '					</span>'
				+ '					<table id="metaoperationdivtable_'
				+ temp
				+ '">'
				+ '					</table>'
				+ '				</div></td>'
				+ '		</tr>'
				+ '		<tr id="metaglobal_'
				+ temp
				+ '" style="display:;">'
				// + ' <td'
				// + ' style="width: 20%; padding: 0px; text-align:
				// left;"></td>'
				+ '			<td style="width: 30%; padding: 5px;"><select '
				+ '				id="metavalueop_'
				+ temp
				+ '" style="width: 100%;" '
				+ '				onchange="javascript:MigrationDataTagger.metaValueChanged('
				+ temp
				+ ');"> '
				+ '					<option value="WordExists">WordExists</option> '
				+ '					<option value="WordOccurrenceCount">WordOccurrenceCount</option> '
				+ '					<option value="PatternExists">PatternExists</option>'
				+ '					<option value="PatternMatchCount">PatternMatchCount</option>'
				+ '					<option value="TotalWordCount">TotalWordCount</option>'
				+ '					<option value="TotalLineCount">TotalLineCount</option>'
				+ '			</select></td>'
				+ '			<td style="width: 40%; padding: 5px;"><input '
				+ '				type="text" id="metavaluecol_'
				+ temp
				+ '" '
				+ '				placeholder="Specify word"></td> '
				+ '		</tr>'
				+ '	</table>'
				+ '</div></td>'
				+ '<td id="where_select_'
				+ temp
				+ '" style="" colspan="1"><span '
				+ 'class="sbox" style=""> <input type="search" '
				+ '	id="conditionBox_'
				+ temp
				+ '" placeholder="Where..." '
				+ '	results="0" '
				+ '	onclick="MigrationDataTagger.showConditionDiv('
				+ temp
				+ ');" '
				+ '	onfocus="MigrationDataTagger.showConditionDiv('
				+ temp
				+ ');" '
				+ '	style="width: 100%;" readonly> '
				+ '</span> '
				+ '<div id="conditionDiv_'
				+ temp
				+ '" class="filtersBlock" '
				+ '	style="display: none; max-height: 250px; overflow-y: auto;">'
				+ '	<span id="selectColClose" class="divcloser">'
				+ '	<a'
				+ '		href="javascript:MigrationDataTagger.closeConditionDiv('
				+ temp
				+ ');">'
				+ '		<img src="images/light-box-close.png" '
				+ '			class="closerImage">  '
				+ '	</a>  '
				+ '	</span> '
				+ '	<table id="tag_edit_table_'
				+ temp
				+ '"> '
				+ '		<tbody> '
				+ '			<tr> '
				+ '				<td nowrap="nowrap">Select Column</td> '
				+ '				<td nowrap="nowrap">Relational Operator</td> '
				+ '			    <td nowrap="nowrap">Value</td> '
				+ '				<td nowrap="nowrap">Logical Operator</td> '
				+ '				<td></td> '
				+ '			</tr> '
				+ ' <tr id="row_'
				+ temp
				+ '_1"> '
				+ ' <td><select style="width: 100%" id="attr_'
				+ temp
				+ '_1" '
				+ ' onchange="javascript:MigrationDataTagger.updateRelOp('
				+ temp
				+ ',1);"> '
				+ ' <option value="0">--Select--</option> '
				+ ' </select></td> '
				+ ' <td><select id="op_'
				+ temp
				+ '_1" style="width: 100%;"> '
				+ ' <option value="=">=</option> '
				+ ' <option value="!=">!=</option> '
				+ ' <option value=">">></option> '
				+ ' <option value="<">&#60;</option> '
				+ ' <option value=">=">>=</option> '
				+ ' <option value="<=">&#60;=</option> '
				+ ' <option value="CONTAINS">CONTAINS</option> '
				+ ' <option value="STARTSWITH">STARTS WITH</option> '
				+ ' <option value="ENDSWITH">ENDS WITH</option> '
				+ ' <option value="CONTAINEDIN">IN</option> '
				+ ' <option value="NOTCONTAINEDIN">NOT IN</option> '
				+ ' <option value="LIKE">LIKE</option> '
				+ ' <option value="NOTLIKE">NOT LIKE</option> '
				+ ' <option value="BETWEEN">BETWEEN</option> '
				+ ' <option value="NOTBETWEEN">NOTBETWEEN</option> '
				+ ' </select></td> '
				+ ' <td><input type="text" id="cond_'
				+ temp
				+ '_1" placeholder="value"></td> '
				+ ' <td><select id="lop_'
				+ temp
				+ '_1" disabled="disabled" style="width: 75%;"> '
				+ ' <option value="or">OR</option> '
				+ ' <option value="and">AND</option> '
				+ ' </select></td> '
				+ ' <td><a '
				+ ' href="javascript:MigrationDataTagger.addConditionClicked('
				+ temp
				+ ', 1 )"> '
				+ ' <img alt="Add Volume" '
				+ ' src="images/plus_sign_brown.png" '
				+ ' id="plusImage" style="height: 15px;"> '
				+ ' </a> <a '
				+ ' href="javascript:MigrationDataTagger.removeConditionClicked('
				+ temp + ', 1)" ' + ' style="color: white;"> <img '
				+ ' alt="Remove Volume" '
				+ ' src="images/minus_sign_brown.png" ' + ' id="minusImage" '
				+ ' style="height: 10px; width: 20px;"> ' + ' </a></td> '
				+ ' </tr> ' + ' </table> ' + '</div></td> ' + '<td><a '
				+ 'href="javascript:MigrationDataTagger.addTagClicked()"> '
				+ ' <img alt="Add Sign" '
				+ ' src="images/plus_sign_brown.png" id="plusImage" '
				+ ' style="height: 15px;"> </a> <a '
				+ ' href="javascript:MigrationDataTagger.removeTagClicked('
				+ temp + ')" '
				+ 'style="color: white;"> <img alt="Remove Sign" '
				+ ' src="images/minus_sign_brown.png" id="minusImage" '
				+ ' style="height: 10px; width: 20px;"> ' + '</a></td> '
				+ '</tr>';

		$('#tagTable').append(createTagrowData);
		MigrationDataTagger.rowNum = MigrationDataTagger.rowNum + 1;
		MigrationDataTagger.tagcount = MigrationDataTagger.rowNum;
		MigrationDataTagger.conditioncountJson[temp] = 1;

	},
	getFuncVal : function(str) {
		var index = str.indexOf('(');
		var func = str.substring(0, index);
		index = str.indexOf('\'');
		var val = '';
		if (index != -1) {
			var val = str.substring((index + 1), (str.length - 2));
			var funval = new Array();
		}
		funval.push(func);
		funval.push(val);
		return funval;
	},

	tagApplyTime : function() {

		MigrationDataTagger.getValidProcessing();

		if ($('#applytag').is(':checked')) {
			$('#apply-tag-time-row').show();
		} else {
			$('#apply-tag-time-row').hide();
		}

	},

	tagApplyScheduleTime : function(num) {

		MigrationDataTagger.getValidProcessing();
		if (num == 1) {
			$('#schedule-time-row-1').hide();
		} else {
			$('#schedule-time-row-1').show();
		}
	},
	tagApplyChanged : function(num) {
		if (num == 1) {
			$('#post-ingest_row-1').hide();
			$('#post-ingest_row-2').hide();
			// Processing Row Below Apply Now Tag
			$('#apply-tag-time-row_2').hide();
			$('#apply-tag-time-row_3').hide();

		} else {

			MigrationDataTagger.getValidProcessing();
			$('#post-ingest_row-1').show();
			$('#post-ingest_row-2').show();
		}
	},
	getDefaultHDFSCoreTags : function() {
		var map = new Object();
		map['FILEPATH'] = MigrationDataTagger.DATATYPE_STRING;
		map['ACCESSTIME'] = MigrationDataTagger.DATATYPE_TIMESTAMP;
		map['MODIFICATIONTIME'] = MigrationDataTagger.DATATYPE_TIMESTAMP;
		map['OWNER'] = MigrationDataTagger.DATATYPE_STRING;
		map['USERGROUP'] = MigrationDataTagger.DATATYPE_STRING;
		map['PERMISSION'] = MigrationDataTagger.DATATYPE_STRING;
		map['REPLICATION'] = MigrationDataTagger.DATATYPE_INTEGER;
		map['LEN'] = MigrationDataTagger.DATATYPE_INTEGER;
		map['COMPRESSION_TYPE'] = MigrationDataTagger.DATATYPE_STRING;
		map['ENCRYPTION_TYPE'] = MigrationDataTagger.DATATYPE_STRING;
		map['BLOCKSIZE'] = MigrationDataTagger.DATATYPE_INTEGER;

		return map;
	},
	getValidProcessing : function() {
		var selectedTableName = $("#schema").val();

		var iPostIngestType = false;
		if ($('#oningest').is(':checked') && $('#applytag').is(':checked')) {
			iPostIngestType = true;
		} else if ($('#onpostingest').is(':checked')) {
			iPostIngestType = true;
		} else
			iPostIngestType = false;
		if (iPostIngestType) {
			if (($.inArray(selectedTableName.toUpperCase(),
					MigrationDataTagger.tableList) > -1)
					|| ($.inArray(MigrationDataTagger.filetype.toUpperCase(),
							MigrationDataTagger.fileTypeList) > -1)) {
				$('#apply-tag-time-row_2').show();
				$('#apply-tag-time-row_3').show();
			} else {
				/*
				 * When file type is not :
				 * CSV,JTL,IISLOG,JSON,ACCESSLOG,LOG,KEYVALUE; for them ,default
				 * processing type will be File level always
				 */
				$("#filelevelprocessing").attr('checked', true);
				$('#apply-tag-time-row_2').hide();
				$('#apply-tag-time-row_3').hide();
			}
		} else {
			$('#apply-tag-time-row_2').hide();
			$('#apply-tag-time-row_3').hide();
		}
	},

	fetchFileTypeforHiveTable : function(response, table) {
		var filetypeArr = response["fileType"];

		var fileType = '';

		for (var i = 0; i < filetypeArr.length; i++) {
			var tableObj = filetypeArr[i];
			if (tableObj[table] != null) {
				fileType = tableObj[table];
			}
		}

		MigrationDataTagger.filetype = fileType;

	}
}
