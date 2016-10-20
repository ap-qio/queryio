var emailEnabled = false;
var logEnabled = false;
var DETAILS = '{details}';
var DETAILS_LEN = 9;
var controllerArr = [];
var rowId = 1;
var tableValue = [];
var cacheId = [];
var nodeCache;
var hostCache;
// var condition='<select name="condSelect" id = "condSelect"
// class="report_list_box"><option value="Over">Greater than</option><option
// value="Under">Less than</option><option value="Equals">Equal
// to</option><option value="Not Equals">Not Equal to</option></select>';
// var aggfunction='<select name="functionSelect"
// class="report_list_box"><option value="" selected>None (All
// Values)</option><option value="avg">Average</option><option
// value="min">Minimum</option><option value="max">Maximum</option></select>';
var setDefaultAttr;
var condition_prefix = '<select name="condSelect" id = "condSelect'
//var condition_suffix = '" class="report_list_box"><option value="Greater than">Greater than</option><option value="Less than">Less than</option><option value="Equal to">Equal to</option><option value="Not Equals">Not Equal to</option></select>';
var condition_suffix = '" class="report_list_box"><option value="Over">Greater than</option><option value="Under">Less than</option><option value="Equals">Equal to</option><option value="Not Equals">Not Equal to</option></select>';
var aggfunction_prefix = '<select name="functionSelect" id = "functionSelect'
var aggfunction_suffix = '" class="report_list_box"><option value="" selected>None (All Values)</option><option value="Average">Average</option><option value="Minimum">Minimum</option><option value="Maximum">Maximum</option></select>';
//var aggfunction='<select name="functionSelect" class="report_list_box"><option value="" selected>None (All Values)</option><option value="Average">Average</option><option value="Minimum">Minimum</option><option value="Maximum">Maximum</option></select>';

var createTable = false;

function doDetailsNotif(doc, src, raised) {
	var element = raised ? doc.forms[1].alertRaisedNotificationMessage
			: doc.forms[1].alertResetNotificationMessage;
	if (src.checked) {
		if (element.value.indexOf(DETAILS) == -1) {
			element.value = element.value + DETAILS;
		}
	} else {
		var itrValue = element.value;
		var index = itrValue.indexOf(DETAILS);
		while (index != -1) {
			itrValue = itrValue.substring(0, index)
					+ itrValue.substring(index + DETAILS_LEN);
			index = itrValue.indexOf(DETAILS);
		}
		element.value = itrValue;
	}
}

function changeAttributes() {

	var selectedText = document.getElementById('nodeIds').options[document
			.getElementById('nodeIds').selectedIndex].text;
	var nodeId = document.getElementById('nodeIds').value;
	if (selectedText.substring(selectedText.indexOf('-') + 1) == ' host') {
		RemoteManager.getAttributeListForHost(parseInt(nodeId),
				populateAttrList);
	} else {
		createTable = false;
		RemoteManager.getAttributeList(nodeId, populateAttrList);
	}
}

function populateAttrList(list) {

	if (list != null) {
		$("#attr_list").html("");
		for (var i = 0; i < list.length; i++) {
			var controller = list[i];
			$("#attr_list").html(
					$("#attr_list").html() + '<option value="'
							+ controller.name + '">' + controller.displayName
							+ '</option>');
			controllerArr.push(controller);
		}
		var t = $("#subExprsTable tbody tr").length;

		if ($('#subExprsTable') != undefined
				&& $("#subExprsTable tbody tr").length > 0) {
			$('#subExprsTable').dataTable().fnClearTable();
			cacheId.length = 0;

		}

	}
	if (createTable) {
		createTable = false;
	}
	fillExprsTable();
	if (isEditRule) {
		$("#headerspan").text("Edit Rule");
		// isTableCreated = true;
		RemoteManager.getRuleBean(ruleId, fillEditRule);
	}
}

function addRule() {
	if (cacheId.length == 0) {
		jAlert(
				'You did not specify any expressions for the rule. Please add expressions that should be evaluated for this rule.',
				'Insufficient Details');
		$("#popup_container").css("z-index", "99999999");
		return;
	}

	var operation = document.getElementById('rule.add').value.toLowerCase();
	var nodeId = document.getElementById('nodeIds').value;
	var ruleId = document.getElementById('ruleId').value;
	var severity = document.getElementById('severity').value;
	var notificationType = document.getElementById('notificationType').value;
	var alertRaisedNotificationMessage = '';
	var alertResetNotificationMessage = '';
	// alertRaisedNotificationMessage =
	// document.getElementById('alertRaisedNotificationMessage').value;
	// alertResetNotificationMessage =
	// document.getElementById('alertResetNotificationMessage').value;
	if (notificationType == 'Email') {
		alertRaisedNotificationMessage = document
				.getElementById('alertRaisedNotificationMessage').value;
		alertResetNotificationMessage = document
				.getElementById('alertResetNotificationMessage').value;
	} else {
		alertRaisedNotificationMessage = document
				.getElementById('logRaisedMessageRulesForAlert').value;
		alertResetNotificationMessage = document
				.getElementById('logResetMessageRulesForAlert').value;
	}
	var alertRaisedNotificationSubject = document
			.getElementById('alertRaisedNotificationSubject').value;
	var alertResetNotificationSubject = document
			.getElementById('alertResetNotificationSubject').value;
	var attrNames = [];
	var attrConditions = [];
	var attrValues = [];
	var attrFunction = [];
	var attrDuration = [];
	rowId = 1;
	for (var i = 0; i < cacheId.length; i++) {
		var row = document.getElementById('row_' + cacheId[i]);

		attrNames.push(document.getElementById('attr-' + cacheId[i]).value);
		// console.log('condition' , $('#condSelect' + rowId + '
		// :selected').eq(0).val());
		// console.log('rowId' , rowId);
		attrConditions
				.push($('#condSelect' + rowId + ' :selected').eq(0).val());
		attrValues.push(document.getElementById('conval' + cacheId[i]).value);
		if ($('#conval' + cacheId[i]).val() == "") {
			jAlert("Value required.", "Incomplete Detail");
			$("#popup_container").css("z-index", "99999999");
			return;
		}

		if (Util.isNumeric($('#conval' + cacheId[i]).val()) == false) {
			jAlert("Value must be numeric.", "Invalid Detail");
			$("#popup_container").css("z-index", "99999999");
			return;
		}
		attrFunction.push($('#functionSelect' + rowId + ' :selected').eq(0)
				.val());
		// console.log('attrFunction' , attrFunction);
		// console.log('attrCondition' , attrConditions);
		attrDuration
				.push(document.getElementById('durations' + cacheId[i]).value);
		if ($('#durations' + cacheId[i]).val() == "") {
			jAlert("Duration required.", "Incomplete Detail");
			$("#popup_container").css("z-index", "99999999");
			return;
		}

		if (Util.isNumeric($('#durations' + cacheId[i]).val()) == false) {
			jAlert("Duration must be numeric.", "Invalid Detail");
			$("#popup_container").css("z-index", "99999999");
			return;
		}
		rowId++;
	}
	if ($("#enableNotificationCheckbox").is(':checked') == false)
		notificationType = '-';

	var selectedText = document.getElementById('nodeIds').options[document
			.getElementById('nodeIds').selectedIndex].text;
	if (selectedText.substring(selectedText.indexOf('-') + 1) == ' host')
		nodeId = 'HOST_IP_'
				+ selectedText.substring(0, selectedText.indexOf('-'));

	RemoteManager.addOrUpdateRule(nodeId, ruleId, operation, severity,
			notificationType, alertRaisedNotificationSubject,
			alertRaisedNotificationMessage, alertResetNotificationSubject,
			alertResetNotificationMessage, attrNames, attrConditions,
			attrValues, attrFunction, attrDuration, showStatus);
}

function showStatus(dwrResponse) {

	if (dwrResponse.taskSuccess)
		jAlert(dwrResponse.responseMessage, 'Success');
	else
		jAlert(
				dwrResponse.responseMessage
						+ ' <br><a href="javascript:Navbar.showServerLog();">View Log</a>',
				'Error');

	RulesList.closeBox(true);
	// Navbar.isRefreshPage =true;
	// Navbar.changeTab('Configure Alerts','alerts', 'set_alerts');
}

function findNodes() {
	$("#nodeIds").html("");
	setDefaultAttr = true;
	RemoteManager.getAllHostDetails(populateHosts);
	// RemoteManager.getAllNameNodeDetails(populateNodes);
	// RemoteManager.getAllDataNodeDetails(populateNodes);
	// RemoteManager.getAllResourceManagerDetails(populateNodes);
	// RemoteManager.getAllNodeManagerDetails(populateNodes);

}

function fillEditRule(RuleBean) {
	$('#ruleId').val(RuleBean.ruleId);
	$('#nodeIds').val(RuleBean.nodeId);
	$('#severity').val(RuleBean.severity);
	$('#notificationType').val(RuleBean.notificationType);
	// console.log('RuleBean:: ' , RuleBean);
	if (isTableCreated) {

		isTableCreated = false;
		createTable = true;
		changeAttributes();
	}
	if (RuleBean.notificationType == 'Email') {
		$("#enableNotificationCheckbox").attr('checked', 'checked');
		$('#alertRaisedNotificationMessage').val(
				RuleBean.alertRaisedNotificationMessage);
		$('#alertResetNotificationMessage').val(
				RuleBean.alertResetNotificationMessage);
		$('#alertRaisedNotificationSubject').val(
				RuleBean.alertRaisedNotificationSubject);
		$('#alertResetNotificationSubject').val(
				RuleBean.alertResetNotificationSubject);
	} else if (RuleBean.notificationType == 'Log') {
		$("#enableNotificationCheckbox").attr('checked', 'checked');
		$('#logRaisedMessageRulesForAlert').val(
				RuleBean.alertRaisedNotificationMessage);
		$('#logResetMessageRulesForAlert').val(
				RuleBean.alertResetNotificationMessage);
	} else if (RuleBean.notificationType == '-') {
		$("#enableNotificationCheckbox").removeAttr('checked');
	}

	var text = RuleBean.alertRaisedNotificationMessage;
	if (text.indexOf('{details}') != -1) {
		document.getElementById('detailsInRaised').checked = true;
	}
	text = RuleBean.alertResetNotificationMessage;
	if (text.indexOf('{details}') != -1) {
		document.getElementById('detailsInReset').checked = true;
	}

	var row = 1;
	for (var i = 0; i < RuleBean.attrNames.length; i++) {
		$('#subExprsTable')
				.dataTable()
				.fnAddData(
						[ {
							"0" : '<input type = "hidden" id="attr-'
									+ row
									+ '" name = "attr" value = "'
									+ RuleBean.attrNames[i]
									+ '">'
									+ RuleBean.attrNames[i]
											.substring(RuleBean.attrNames[i]
													.indexOf('#') + 1),
							"1" : condition_prefix + row + condition_suffix,
							"2" : '<input type = "text" id = "conval' + row
									+ '" />',
							"3" : aggfunction_prefix + row + aggfunction_suffix,
							"4" : '<input id="durations' + row
									+ '" type="text" />',
							"5" : '<input type="button" name="delete" value="Delete" onclick="javascript:removeRow(this);" id="deleteB_'
									+ row + '" class="buttonClass" />',
							"DT_RowId" : 'row_' + row
						} ]);

		row++;
	}
	rowId = 1;
	for (var i = 0; i < RuleBean.attrNames.length; i++) {

		// console.log('RuleBean.conditions[i]', RuleBean.conditions[i]);

		$('#condSelect' + rowId).val(RuleBean.conditions[i]);
		$('#conval' + rowId).val(RuleBean.values[i]);
		$('#functionSelect' + rowId).val(RuleBean.aggregateFunctions[i]);
		$('#durations' + rowId).val(RuleBean.durations[i]);
		cacheId.push(rowId);
		rowId++;

		// document.forms[1].condSelect[i].value = RuleBean.conditions[i];
		// document.getElementById('conval'+rowId).value = RuleBean.values[i];
		// $(document.forms[1].functionSelect[i]).val(RuleBean.aggregateFunctions[i]);
		// document.getElementById('durations'+rowId).value =
		// RuleBean.durations[i];
		// cacheId.push(rowId);
		// rowId++;
	}

	if (RuleBean.attrNames != null && RuleBean.attrNames.length > 0) {
		$('#attr_list').val(RuleBean.attrNames[0]);
	}

	document.getElementById('rule.add').value = "Update";
	$("#ruleId").attr("disabled", "disabled");
	document.getElementById('nodeIds').disabled = true;
	// $("#nodeIds").attr("disabled", "disabled");
	enableNotification();
}

function fillExprsTable() {

	var colList = [ {
		"sTitle" : 'Attribute'
	}, {
		"sTitle" : 'Condition'
	}, {
		"sTitle" : 'Value'
	}, {
		"sTitle" : 'Aggregate Function'
	}, {
		"sTitle" : 'Duration (secs)'
	}, {
		"sTitle" : 'Remove'
	} ];

	$('#subExprsTable').dataTable({
		"bPaginate" : false,
		"bLengthChange" : false,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"bAutoWidth" : false,
		"bDestroy" : true,
		"aoColumns" : colList
	});
}
function loadHostCache(list) {
	var host = '';
	hostCache = new Array();
	var flag = false;
	for (var i = 0; i < list.length; i++) {
		host = list[i];
		hostCache[host.id] = host;
		flag = true;
	}

}
function populateHosts(list) {

	if (list != null) {
		var host = '';
		var data = '';
		for (var i = 0; i < list.length; i++) {
			host = list[i];
			data += '<option value="' + host.id + '">' + host.hostIP
					+ ' - host</option>'
			$("#nodeIds").html(
					$("#nodeIds").html() + '<option value="' + host.id + '">'
							+ host.hostIP + ' - host</option>');
		}
	}
	loadHostCache(list);
	RemoteManager.getAllNameNodeDetails(function(list) {
		populateNodes(list, false);
	});
	RemoteManager.getAllResourceManagerDetails(function(list) {
		populateNodes(list, false);
	});
	RemoteManager.getAllNodeManagerDetails(function(list) {
		populateNodes(list, true);
	});
}
function populateNodes(list, isChangeAttr) {

	if (list != null) {
		var node = '';
		var host = '';
		var id = '';
		for (var i = 0; i < list.length; i = i + 2) {

			node = list[i];
			id = node.id;
			// document.getElementById('nodeIds').innerHTML+='<option
			// value="'+id+'">'+node.id+' - '+node.nodeType+'</option>';
			$("#nodeIds").html(
					$("#nodeIds").html() + '<option value="' + id + '">'
							+ node.id + ' - ' + node.nodeType + '</option>');
			if (node.nodeType == "datanode") {
				flag = false;
			}
		}

		if (setDefaultAttr && list.length > 0 && isChangeAttr) {
			setDefaultAttr = false;
			createTable = true;
			isTableCreated = true;
			changeAttributes();
		}
	}
}
function setNotificationSettings(nbean) {
	emailEnabled = nbean.emailEnabled;
	logEnabled = nbean.logEnabled;
}

function nextRuleStep(step) {
	if (step == 2 && $("#enableNotificationCheckbox").is(':checked') == false) {
		step = 3;
	} else if (step == 2
			&& $("#enableNotificationCheckbox").is(':checked') == true) {
		if (!emailEnabled && !logEnabled) {
			jAlert(
					"You have not configured any notifications. Please configure notifications and return to this wizard. To configure notifications, go to <b>Dashboard > Notifications</b> tab.",
					"Error");
			$("#popup_container").css("z-index", "99999999");
			return;
		}
	} else if (step == 0) {
		if ($("#enableNotificationCheckbox").is(':checked')) {

			step = 2;
		} else
			step = 1;
	}

	switch (step) {
	case 1:
		$('#headerspan').text("Add Rule");
		$('#instruction').text("Configure Rule For Node.");
		$('#rulediv1').show();
		$('#rulediv2').hide();
		$('#rulediv3').hide();
		$("#add_rule").css("float", "");
		$("#add_rule").css("margin", "");
		break;
	case 2:
		if (document.getElementById('nodeIds').value == 0) {
			jAlert("Node Id is not selected.", "Incomplete Detail");
			$("#popup_container").css("z-index", "99999999");
			return;
		} else if (document.getElementById('ruleId').value == '') {
			jAlert("Rule Id is not defined.", "Incomplete Detail");
			$("#popup_container").css("z-index", "99999999");
			return;
		} else {
			$('#headerspan').text("Notification");
			$('#instruction')
					.text(
							"Note: Notification will be sent when rule violates or "
									+ "comes out of violation. All notifications configured for a "
									+ "node will be processed.");
			var isRuleExists = false;
			if (!isEditRule) {
				if ($('#rules_list_table tbody tr').length > 0
						&& !$('#rules_list_table tbody tr td').hasClass(
								'dataTables_empty')) {
					var ruleId = document.getElementById('ruleId').value;
					$('#rules_list_table tbody tr')
							.each(
									function() {
										var row = this.cells;
										if (ruleId == row[1].textContent) {
											jAlert(
													"Rule Id already exists. Please specify unique Rule Id",
													"Incorrect Detail");
											$("#popup_container").css(
													"z-index", "99999999");
											isRuleExists = true;
										}

									});
				}
			}
			if (!isRuleExists) {
				$('#rulediv2').show();
				$('#rulediv1').hide();
				$('#rulediv3').hide();
			}

		}
		break;
	case 3:
		if ($("#enableNotificationCheckbox").is(':checked')) {
			if ($("#notificationType").val() == 'Email') {
				var retval = validateEmail();
				if (!retval) {
					return;
				}
			} else if ($("#notificationType").val() == 'Log') {
				var retval = validateLog();
				if (!retval) {
					return;
				}
			}
		}
		var isRuleExists = false;
		if (!isEditRule) {
			if ($('#rules_list_table tbody tr').length > 0
					&& !$('#rules_list_table tbody tr td').hasClass(
							'dataTables_empty')) {
				var ruleId = document.getElementById('ruleId').value;
				$('#rules_list_table tbody tr')
						.each(
								function() {
									var row = this.cells;
									if (ruleId == row[1].textContent) {
										jAlert(
												"Rule Id already exists. Please specify unique Rule Id",
												"Incorrect Detail");
										$("#popup_container").css("z-index",
												"99999999");
										isRuleExists = true;
									}

								});
			}
		}
		if (!isRuleExists) {
			$('#headerspan').text("Expression");
			$('#instruction').text("Add Expression for attribute.");
			$('#rulediv3').show();
			$('#rulediv1').hide();
			$('#rulediv2').hide();
			$("#add_rule").css("float", "");
			$("#add_rule").css("margin", "");
		}
		break;
	}

	$('#add_rule').css({
		position : 'absolute',
		left : ($(window).width() - $('#add_rule_table').outerWidth()) / 2,

	});

}

function validateEmail() {
	if (!emailEnabled) {
		jAlert(
				"You have not configured Email notification. Please configure Email notification and return to this wizard.",
				"Error");
		$("#popup_container").css("z-index", "99999999");
		return;
	}

	if ($('#alertRaisedNotificationSubject').val() == null
			|| $('#alertRaisedNotificationSubject').val() == undefined
			|| $('#alertRaisedNotificationSubject').val() == '') {
		jAlert("Rule violate mail subject is empty.", "Incomplete Details");
		$("#popup_container").css("z-index", "99999999");
		return false;
	}
	if ($('#alertRaisedNotificationMessage').val() == null
			|| $('#alertRaisedNotificationMessage').val() == undefined
			|| $('#alertRaisedNotificationMessage').val() == '') {
		jAlert("Rule violate mail message is empty.", "Incomplete Details");
		$("#popup_container").css("z-index", "99999999");
		return false;
	}
	if ($('#alertResetNotificationSubject').val() == null
			|| $('#alertResetNotificationSubject').val() == undefined
			|| $('#alertResetNotificationSubject').val() == '') {
		jAlert("Mail subject when rule comes out of violation is empty.",
				"Incomplete Details");
		$("#popup_container").css("z-index", "99999999");
		return false;
	}
	if ($('#alertResetNotificationMessage').val() == null
			|| $('#alertResetNotificationMessage').val() == undefined
			|| $('#alertResetNotificationMessage').val() == '') {
		jAlert("Mail message when rule comes out of violation is empty.",
				"Incomplete Details");
		$("#popup_container").css("z-index", "99999999");
		return false;
	}
	return true;
}

function validateLog() {
	if (!logEnabled) {
		jAlert(
				"You have not configured Log notification. Please configure Log notification and return to this wizard.",
				"Error");
		$("#popup_container").css("z-index", "999999999");
		return;
	}
	if ($('#logRaisedMessageRulesForAlert').val() == null
			|| $('#logRaisedMessageRulesForAlert').val() == undefined
			|| $('#logRaisedMessageRulesForAlert').val() == '') {
		jAlert("Rule violate log message is empty.", "Incomplete Details");
		$("#popup_container").css("z-index", "99999999");
		return false;
	}
	if ($('#logResetMessageRulesForAlert').val() == null
			|| $('#logResetMessageRulesForAlert').val() == undefined
			|| $('#logResetMessageRulesForAlert').val() == '') {
		jAlert("Log message when rule comes out of violation is empty.",
				"Incomplete Details");
		$("#popup_container").css("z-index", "99999999");
		return false;
	}
	return true;
}

function cancelRule() {
	$('#service_ref').load('resources/alerts_configure.html');

}

function removeRow(doc) {

	var rowNo = doc.id.substring(doc.id.indexOf("_") + 1);
	var val = document.getElementById('attr-' + rowNo).value;
	val = tableValue.indexOf(val);
	tableValue.splice(val, 1);
	$('#subExprsTable').dataTable().fnDeleteRow(
			$('#subExprsTable').dataTable().fnGetPosition(
					document.getElementById('row_' + rowNo)));

	for (i = 0; i < cacheId.length; i++) {
		if (cacheId[i] == rowNo) {
			cacheId.splice(i, 1);
		}
	}

}

function findDispName(elemName) {
	for (var i = 0; i < controllerArr.length; i++) {
		var controller = controllerArr[i];
		if (controller.name == elemName) {
			return controller.displayName;
		}
	}
}

function addElement() {

	if (tableValue.indexOf(document.getElementById('attr_list').value) == -1) {

		$('#subExprsTable')
				.dataTable()
				.fnAddData(
						[ {
							"0" : '<input type = "hidden" id="attr-'
									+ rowId
									+ '" name = "attr" value = "'
									+ document.getElementById("attr_list").value
									+ '">'
									+ findDispName(document
											.getElementById('attr_list').value),
							"1" : condition_prefix + rowId + condition_suffix,
							"2" : '<input type = "text" id = "conval'
									+ rowId
									+ '" onkeydown="javascript:Util.maskNumericInput(event)"/>',
							"3" : aggfunction_prefix + rowId
									+ aggfunction_suffix,
							"4" : '<input id="durations'
									+ rowId
									+ '" type="text" onkeydown="javascript:Util.maskNumericInput(event)"/>',
							"5" : '<input type="button" name="delete" value="Delete" onclick="javascript:removeRow(this);" id="deleteB_'
									+ rowId + '" class="buttonClass" />',
							"DT_RowId" : 'row_' + rowId
						} ]);
		tableValue.push(document.getElementById('attr_list').value);

		cacheId.push(rowId);
		rowId++;
	}
	$('#add_rule').css({
		position : 'absolute',
		left : ($(window).width() - $('#add_rule_table').outerWidth()) / 2,

	});

}

function enableNotification() {
	var val = $("#notificationType").val();

	if (val == "Email") {
		$("#notificationTable").show();
		$("#logTypeContainerDiv").hide();
		$("#rulenext").show();
	} else if (val == "Log") {
		$("#logTypeContainerDiv").show();
		$("#notificationTable").hide();
	}
	$('#add_rule').css({
		position : 'absolute',
		left : ($(window).width() - $('#add_rule_table').outerWidth()) / 2,

	});
}

function editRule(Id) {
	isTableCreated = true;
	ruleId = Id;
	isEditRule = true;

}
