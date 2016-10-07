ReportSchedules = {

	selectedMRSchedules : [],
	selectedBigQuerySchedules : [],
	selectedSchedules : [],
	scheduleCache : [],
	scheduleBigQueryCache : [],
	scheduleMRCache : [],
	editCache : [],
	editBigQueryCache : [],
	editMRCache : [],
	userCache : [],
	currentPage : 1,
	currentOperation : null,
	hostAvailable : false,
	emailEnabled : false,
	isEditSchedule : false,
	logEnabled : false,

	$tabs : $("#scheduleTabs").tabs(),

	ready : function() {
		ReportSchedules.$tabs = $("#scheduleTabs").tabs();
		if (Navbar.currentScheduleTabSelected == '')
			Navbar.currentScheduleTabSelected = 'systemReportHead';
		$('#scheduleTabs').tabs({
			select : function(event, ui) {
				if (Navbar.isSetButtonWidth) {
					Navbar.isSetButtonWidth = false;
					Navbar.setButtonWidth();
				}
				Navbar.currentScheduleTabSelected = ui.panel.id;
			}
		});
		ReportSchedules.$tabs.tabs("select", Navbar.currentScheduleTabSelected);
		ReportSchedules.closeButton();
		ReportSchedules.closeBigQueryButton();
		ReportSchedules.closeMRButton();
		RemoteManager
				.getAllSysReportsSchedules(ReportSchedules.populateScheduleList);
		RemoteManager
				.getAllBigQuerySchedules(ReportSchedules.populateBigQueryScheduleList);
		RemoteManager
				.getAllMapRedSchedules(ReportSchedules.populateMapRedScheduleList);
		TriggerList.ready();
	},

	fillHost : function(list) {
		if (list.length > 0) {
			ReportSchedules.hostAvailable = true;
		}
	},

	closeButton : function() {
		if (document.getElementById('editSchedule') == undefined
				|| document.getElementById('editSchedule') == null)
			return;
		document.getElementById('editSchedule').disabled = true;
		document.getElementById('deleteSchedule').disabled = true;
	},

	closeBigQueryButton : function() {
		if (document.getElementById('editBigQuerySchedule') == undefined
				|| document.getElementById('editBigQuerySchedule') == null)
			return;
		document.getElementById('editBigQuerySchedule').disabled = true;
		document.getElementById('deleteBigQuerySchedule').disabled = true;
	},

	closeMRButton : function() {
		if (document.getElementById('editMRSchedule') == undefined
				|| document.getElementById('editMRSchedule') == null)
			return;
		document.getElementById('editMRSchedule').disabled = true;
		document.getElementById('deleteMRSchedule').disabled = true;
	},

	checkEnable : function() {
		ReportSchedules.selectAllHandler();
		ReportSchedules.findSelectedSchedules();

		if (ReportSchedules.selectedSchedules.length == 1) {
			document.getElementById('editSchedule').disabled = false;
			document.getElementById('deleteSchedule').disabled = false;
		} else if (ReportSchedules.selectedSchedules.length > 1) {
			document.getElementById('editSchedule').disabled = true;
			document.getElementById('deleteSchedule').disabled = false;
		} else
			ReportSchedules.closeButton();
	},

	checkBigQueryEnable : function() {
		ReportSchedules.selectAllBigQueryHandler();
		ReportSchedules.findBigQuerySelectedSchedules();

		if (ReportSchedules.selectedBigQuerySchedules.length == 1) {
			document.getElementById('editBigQuerySchedule').disabled = false;
			document.getElementById('deleteBigQuerySchedule').disabled = false;
		} else if (ReportSchedules.selectedBigQuerySchedules.length > 1) {
			document.getElementById('editBigQuerySchedule').disabled = true;
			document.getElementById('deleteBigQuerySchedule').disabled = false;
		} else
			ReportSchedules.closeBigQueryButton();
	},

	checkMREnable : function() {
		ReportSchedules.selectAllMRHandler();
		ReportSchedules.findMRSelectedSchedules();

		if (ReportSchedules.selectedMRSchedules.length == 1) {
			document.getElementById('editMRSchedule').disabled = false;
			document.getElementById('deleteMRSchedule').disabled = false;
		} else if (ReportSchedules.selectedMRSchedules.length > 1) {
			document.getElementById('editMRSchedule').disabled = true;
			document.getElementById('deleteMRSchedule').disabled = false;
		} else
			ReportSchedules.closeMRButton();
	},

	populateScheduleList : function(list) {
		if (document.getElementById('schedules_table_1') == undefined
				|| document.getElementById('schedules_table_1') == null) {
			return;
		}
		var tabledata = [];
		if (list != null) {
			for (var i = 0; i < list.length; i++) {
				var schedule = list[i];
				ReportSchedules.scheduleCache.push(schedule);
				tabledata
						.push([
								'<input type="checkbox" onClick="javascript:ReportSchedules.checkEnable()" id="test'
										+ i + '">',
								'<input type="hidden" value="' + schedule.name
										+ ',' + schedule.group + '">'
										+ schedule.name,
								schedule.reportId != null ? schedule.reportId
										.substring(1,
												schedule.reportId.length - 1)
										: "",
								schedule.selectedFormat != null ? schedule.selectedFormat
										.substring(
												1,
												schedule.selectedFormat.length - 1)
										: "",
								schedule.notificationEnable,
								ReportSchedules
										.findFrequency(schedule.interval),
								'<a href="javascript:ReportSchedules.showTriggerDetails(\''
										+ schedule.name + '\',\''
										+ schedule.group + '\')">Details</a>' ]);
			}
		}
		$('#schedules_table_1')
				.dataTable(
						{
							"bPaginate" : false,
							"bLengthChange" : false,
							"bFilter" : false,
							"bSort" : true,
							"bInfo" : false,
							"bDestroy" : true,
							"bAutoWidth" : true,
							"aaSorting" : [ [ 1, "desc" ] ],
							"aaData" : tabledata,
							"aoColumnDefs" : [ {
								"bSortable" : false,
								"aTargets" : [ 0 ]
							} ],
							"aoColumns" : [
									{
										"sTitle" : "<input type = 'checkbox' id = 'selectAllSchedules' onclick='javascript:ReportSchedules.selectAllSchedules()'>"
									}, {
										"sTitle" : "Schedule ID"
									}, {
										"sTitle" : "Report ID"
									}, {
										"sTitle" : "Format Type"
									}, {
										"sTitle" : "Notification Enable"
									}, {
										"sTitle" : "Frequency"
									}, {
										"sTitle" : "Triggers"
									} ]
						});

		// disabled the checkAll button when no data in the list
		if (list == null || list == undefined || list.length == 0)
			$('#selectAllSchedules').attr('disabled', true);
		else
			$('#selectAllSchedules').removeAttr('disabled');
	},

	populateBigQueryScheduleList : function(list) {
		if (document.getElementById('schedules_table_2') == undefined
				|| document.getElementById('schedules_table_2') == null) {
			return;
		}
		var tabledata = [];
		if (list != null) {
			for (var i = 0; i < list.length; i++) {
				bigqueryschedule++;
				var schedule = list[i];
				ReportSchedules.scheduleBigQueryCache.push(schedule);
				tabledata
						.push([
								'<input type="checkbox" onClick="javascript:ReportSchedules.checkBigQueryEnable()" id="BQtest'
										+ i + '">',
								'<input type="hidden" value="' + schedule.name
										+ ',' + schedule.group + '">'
										+ schedule.name,
								schedule.query != null ? schedule.query
										.substring(1, schedule.query.length - 1)
										: "",
								schedule.selectedFormat != null ? schedule.selectedFormat
										.substring(
												1,
												schedule.selectedFormat.length - 1)
										: "",
								schedule.notificationEnable,
								ReportSchedules
										.findFrequency(schedule.interval),
								'<a href="javascript:ReportSchedules.showTriggerDetails(\''
										+ schedule.name + '\',\''
										+ schedule.group + '\')">Details</a>' ]);

			}
		}
		$('#schedules_table_2')
				.dataTable(
						{
							"bPaginate" : false,
							"bLengthChange" : false,
							"bFilter" : false,
							"bSort" : true,
							"bInfo" : false,
							"bAutoWidth" : true,
							"bDestroy" : true,
							"aaSorting" : [ [ 1, "desc" ] ],
							"aaData" : tabledata,
							"aoColumnDefs" : [ {
								"bSortable" : false,
								"aTargets" : [ 0 ]
							} ],
							"aoColumns" : [
									{
										"sTitle" : "<input type = 'checkbox' id='selectAllBigQuerySchedules' onclick='javascript:ReportSchedules.selectAllBigQuerySchedules()'>"
									}, {
										"sTitle" : "Schedule ID"
									}, {
										"sTitle" : "Query ID"
									}, {
										"sTitle" : "Format Type"
									}, {
										"sTitle" : "Notification Enable"
									}, {
										"sTitle" : "Frequency"
									}, {
										"sTitle" : "Triggers"
									} ]
						});

		// disabled the checkAll button when no data in the list
		if (list == null || list == undefined || list.length == 0)
			$('#selectAllBigQuerySchedules').attr('disabled', true);
		else
			$('#selectAllBigQuerySchedules').removeAttr('disabled');
	},

	populateMapRedScheduleList : function(list) {
		if (document.getElementById('schedules_table_3') == undefined
				|| document.getElementById('schedules_table_3') == null) {
			return;
		}
		var tabledata = [];
		if (list != null) {
			for (var i = 0; i < list.length; i++) {
				mapreduceschedule++;
				var schedule = list[i];
				ReportSchedules.scheduleMRCache.push(schedule);
				tabledata
						.push([
								'<input type="checkbox" onClick="javascript:ReportSchedules.checkMREnable()" id="MRtest'
										+ i + '">',
								'<input type="hidden" value="' + schedule.name
										+ ',' + schedule.group + '">'
										+ schedule.name,
								schedule.jobName != null ? schedule.jobName
										.substring(1,
												schedule.jobName.length - 1)
										: "",
								schedule.notificationEnable,
								ReportSchedules
										.findFrequency(schedule.interval),
								'<a href="javascript:ReportSchedules.showTriggerDetails(\''
										+ schedule.name + '\',\''
										+ schedule.group + '\')">Details</a>' ]);

			}
		}
		$('#schedules_table_3')
				.dataTable(
						{
							"bPaginate" : false,
							"bLengthChange" : false,
							"bFilter" : false,
							"bSort" : true,
							"bInfo" : false,
							"bDestroy" : true,
							"bAutoWidth" : true,
							"aaSorting" : [ [ 1, "desc" ] ],
							"aaData" : tabledata,
							"aoColumnDefs" : [ {
								"bSortable" : false,
								"aTargets" : [ 0 ]
							} ],
							"aoColumns" : [
									{
										"sTitle" : "<input type = 'checkbox' id = 'selectAllMRSchedules' onclick='javascript:ReportSchedules.selectAllMRSchedules()'>"
									}, {
										"sTitle" : "Schedule ID"
									}, {
										"sTitle" : "Job Name"
									}, {
										"sTitle" : "Notification Enable"
									}, {
										"sTitle" : "Frequency"
									}, {
										"sTitle" : "Triggers"
									} ]
						});

		// disabled the checkAll button when no data in the list
		if (list == null || list == undefined || list.length == 0)
			$('#selectAllMRSchedules').attr('disabled', true);
		else
			$('#selectAllMRSchedules').removeAttr('disabled');
	},

	findSelectedSchedules : function() {
		ReportSchedules.selectedSchedules = [];
		for (var i = 0; i < ReportSchedules.scheduleCache.length; i++) {
			if (document.getElementById('test' + i).checked) {
				var sched = ReportSchedules.scheduleCache[i];
				ReportSchedules.selectedSchedules.push(sched.name + ','
						+ sched.group);
			}
		}
	},

	findBigQuerySelectedSchedules : function() {
		ReportSchedules.selectedBigQuerySchedules = [];
		for (var i = 0; i < ReportSchedules.scheduleBigQueryCache.length; i++) {
			if (document.getElementById('BQtest' + i).checked) {
				var sched = ReportSchedules.scheduleBigQueryCache[i];
				ReportSchedules.selectedBigQuerySchedules.push(sched.name + ','
						+ sched.group);
			}
		}
	},

	findMRSelectedSchedules : function() {
		ReportSchedules.selectedMRSchedules = [];
		for (var i = 0; i < ReportSchedules.scheduleMRCache.length; i++) {
			if (document.getElementById('MRtest' + i).checked) {
				var sched = ReportSchedules.scheduleMRCache[i];
				ReportSchedules.selectedMRSchedules.push(sched.name + ','
						+ sched.group);
			}
		}
	},

	selectAllSchedules : function() {
		if (document.getElementById('selectAllSchedules').checked) {
			var oTable = $('#schedules_table_1').dataTable();
			var numberRows = oTable.fnSettings().fnRecordsTotal();
			if (numberRows == 1)
				document.getElementById('editSchedule').disabled = false;
			else
				document.getElementById('editSchedule').disabled = true;
			$("#schedules_table_1 input[type=checkbox]").attr('checked',
					'checked');
			document.getElementById('deleteSchedule').disabled = false;
		} else {
			$("#schedules_table_1 input[type=checkbox]").removeAttr('checked');
			document.getElementById('editSchedule').disabled = true;
			document.getElementById('deleteSchedule').disabled = true;
		}
	},

	selectAllBigQuerySchedules : function() {

		if (document.getElementById('selectAllBigQuerySchedules').checked) {
			var oTable = $('#schedules_table_2').dataTable();
			var numberRows = oTable.fnSettings().fnRecordsTotal();
			if (numberRows == 1)
				document.getElementById('editBigQuerySchedule').disabled = false;
			else
				document.getElementById('editBigQuerySchedule').disabled = true;
			$("#schedules_table_2 input[type=checkbox]").attr('checked',
					'checked');
			document.getElementById('deleteBigQuerySchedule').disabled = false;
		} else {
			$("#schedules_table_2 input[type=checkbox]").removeAttr('checked');
			document.getElementById('editBigQuerySchedule').disabled = true;
			document.getElementById('deleteBigQuerySchedule').disabled = true;
		}
	},

	selectAllMRSchedules : function() {

		if (document.getElementById('selectAllMRSchedules').checked) {
			var oTable = $('#schedules_table_3').dataTable();
			var numberRows = oTable.fnSettings().fnRecordsTotal();
			if (numberRows == 1)
				document.getElementById('editMRSchedule').disabled = false;
			else
				document.getElementById('editMRSchedule').disabled = true;
			$("#schedules_table_3 input[type=checkbox]").attr('checked',
					'checked');
			document.getElementById('deleteMRSchedule').disabled = false;
		} else {
			$("#schedules_table_3 input[type=checkbox]").removeAttr('checked');
			document.getElementById('editMRSchedule').disabled = true;
			document.getElementById('deleteMRSchedule').disabled = true;
		}
	},

	selectAllHandler : function() {
		for (var i = 0; i < ReportSchedules.scheduleCache.length; i++) {
			if (!document.getElementById('test' + i).checked) {
				$("#selectAllSchedules").removeAttr('checked');
				break;
			}
		}
		if (i == ReportSchedules.scheduleCache.length) {
			$("#selectAllSchedules").attr('checked', 'checked');
		}
	},

	selectAllBigQueryHandler : function() {
		for (var i = 0; i < ReportSchedules.scheduleBigQueryCache.length; i++) {
			if (!document.getElementById('BQtest' + i).checked) {
				$("#selectAllBigQuerySchedules").removeAttr('checked');
				break;
			}
		}
		if (i == ReportSchedules.scheduleBigQueryCache.length) {
			$("#selectAllBigQuerySchedules").attr('checked', 'checked');
		}
	},

	selectAllMRHandler : function() {
		for (var i = 0; i < ReportSchedules.scheduleMRCache.length; i++) {
			if (!document.getElementById('MRtest' + i).checked) {
				$("#selectAllMRSchedules").removeAttr('checked');
				break;
			}
		}
		if (i == ReportSchedules.scheduleMRCache.length) {
			$("#selectAllMRSchedules").attr('checked', 'checked');
		}
	},

	deleteSchedule : function() {
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton = ' No';
		jConfirm('Are you sure you want to delete selected item(s)?',
				'Delete Schedule(s)', function(val) {
					if (val == true) {
						ReportSchedules.findSelectedSchedules();
						/*
						 * for(var i = 0;i<ReportSchedules.selectedSchedules.length;i++){
						 * var report = ReportSchedules.selectedSchedules[i] var
						 * reportType =
						 * report.substring(report.indexOf('-[')+2,report.length-1);
						 * if(reportType=='11'){ jAlert("You cannot delete
						 * system generated report.Please deselect system
						 * generated report.","Invalid Operation"); return; } }
						 */
						RemoteManager.deleteJob(
								ReportSchedules.selectedSchedules,
								ReportSchedules.deleteCallback);
					} else
						return;
				});
	},

	deleteBigQuerySchedule : function() {
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton = ' No';
		jConfirm('Are you sure you want to delete selected item(s)?',
				'Delete Schedule(s)', function(val) {
					if (val == true) {
						ReportSchedules.findBigQuerySelectedSchedules();
						/*
						 * for(var i = 0;i<ReportSchedules.selectedBigQuerySchedules.length;i++){
						 * var report =
						 * ReportSchedules.selectedBigQuerySchedules[i] var
						 * reportType =
						 * report.substring(report.indexOf('-[')+2,report.length-1);
						 * if(reportType=='11'){ jAlert("You cannot delete
						 * system generated report.Please deselect system
						 * generated report.","Invalid Operation"); return; } }
						 */
						RemoteManager.deleteJob(
								ReportSchedules.selectedBigQuerySchedules,
								ReportSchedules.deleteCallback);
					} else
						return;
				});
	},

	deleteMRSchedule : function() {
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton = ' No';
		jConfirm('Are you sure you want to delete selected item(s)?',
				'Delete Schedule(s)', function(val) {
					if (val == true) {
						ReportSchedules.findMRSelectedSchedules();
						/*
						 * for(var i = 0;i<ReportSchedules.selectedMRSchedules.length;i++){
						 * var report = ReportSchedules.selectedMRSchedules[i]
						 * var reportType =
						 * report.substring(report.indexOf('-[')+2,report.length-1);
						 * if(reportType=='11'){ jAlert("You cannot delete
						 * system generated report.Please deselect system
						 * generated report.","Invalid Operation"); return; } }
						 */
						RemoteManager.deleteJob(
								ReportSchedules.selectedMRSchedules,
								ReportSchedules.deleteCallback);
					} else
						return;
				});
	},

	deleteCallback : function(flag) {
		jQuery.alerts.okButton = ' Ok ';
		if (!flag) {
			jAlert('Some Error Occurred while deleting the job', 'Failed');
		} else {
			jAlert('Successfully deleted Jobs', 'Success');
		}
		Navbar.refreshView();
	},

	reBuiltTable : function() {
		var oTable = $('#schedules_table_1').dataTable();
		oTable.remove();
		$('#schedules_table_div').append(
				'<table id="schedules_table_1"></table>');
		RemoteManager
				.getAllSysReportsSchedules(ReportSchedules.populateScheduleList);
	},

	findScheduleInCache : function(name, group) {
		for (var i = 0; i < ReportSchedules.scheduleCache.length; i++) {
			var sched = ReportSchedules.scheduleCache[i];
			if (sched.name == name && sched.group == group) {
				return sched;
			}
		}
		return null;
	},

	findBigQueryScheduleInCache : function(name, group) {
		for (var i = 0; i < ReportSchedules.scheduleBigQueryCache.length; i++) {
			var sched = ReportSchedules.scheduleBigQueryCache[i];
			if (sched.name == name && sched.group == group) {
				return sched;
			}
		}
		return null;
	},

	findMRScheduleInCache : function(name, group) {
		for (var i = 0; i < ReportSchedules.scheduleMRCache.length; i++) {
			var sched = ReportSchedules.scheduleMRCache[i];
			if (sched.name == name && sched.group == group) {
				return sched;
			}
		}
		return null;
	},

	editSchedule : function() {
		ReportSchedules.isEditSchedule = true;
		ReportSchedules.currentOperation = "edit";
		Util.addLightbox("generateReport",
				"resources/editScheduledReport.html", null, null);
	},

	editBigQuerySchedule : function() {
		ReportSchedules.currentOperation = "edit";
		Util.addLightbox("generateReport", "resources/editScheduleQuery.html",
				null, null);
	},

	editMRSchedule : function() {
		ReportSchedules.currentOperation = "edit";
		Util.addLightbox("generateReport", "resources/editScheduleMRJob.html",
				null, null);
	},

	closeBox : function(isRefresh) {
		ReportSchedules.currentOperation = null;
		Util.removeLightbox("generateReport");
		if (isRefresh)
			Navbar.refreshView();
	},

	moveAllOptions : function(from, to) {
		var source = document.getElementById(from).getElementsByTagName(
				"option");
		for (var i = 0; i < source.length; i++) {
			$('#' + to).append(
					'<option value="' + source[i].value + '">'
							+ ReportSchedules.findUser(source[i].value)
							+ '</option>');
		}
		$('#' + from).children().remove();
	},

	editReady : function() {

		ReportSchedules.findSelectedSchedules();
		var scheduleName = ReportSchedules.selectedSchedules[0].substring(0,
				ReportSchedules.selectedSchedules[0].indexOf(","));
		var scheduleGroup = ReportSchedules.selectedSchedules[0]
				.substring(ReportSchedules.selectedSchedules[0].indexOf(",") + 1);
		var schedule = ReportSchedules.findScheduleInCache(scheduleName,
				scheduleGroup);
		document.getElementById('headerspan').innerHTML = scheduleName;
		// document.forms[0].interval.options[schedule.interval].selected =
		// true;
		$("#interval").prop("disabled", false);
		var dt = new Date(schedule.time);
		document.forms[0].reportDate.value = (dt.getMonth() + 1) + "/"
				+ dt.getDate() + "/" + (1900 + dt.getYear()) + " "
				+ dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
		document.forms[0].html.checked = schedule.selectedFormat
				.indexOf('HTML') > 0;
		document.forms[0].pdf.checked = schedule.selectedFormat.indexOf('PDF') > 0;
		document.forms[0].xls.checked = schedule.selectedFormat.indexOf('XLS') > 0;
		document.forms[0].notificationEnable.checked = schedule.notificationEnable;
		if (schedule.notificationEnable) {
			document.forms[0].alertRaisedNotificationMessage.value = schedule.notificationMessage;
			document.forms[0].notificationType.options[schedule.notificationType == 'Email' ? 0
					: 1].selected = true;
		}
		ReportSchedules.changeButton();
		ReportSchedules.logSave();
		ReportSchedules.currentPage = 1;
		ReportSchedules.nextScheduleStep(1);
		ReportSchedules.editCache = [];
		ReportSchedules.editCache.push(schedule);
		if (schedule.reportId.indexOf("NameNode Detail") > 0
				|| schedule.reportId.indexOf("DataNode Detail") > 0
				|| schedule.reportId.indexOf("ResourceManager Detail") > 0
				|| schedule.reportId.indexOf("NodeManager Detail") > 0)
			RemoteManager.getAllNodeIdWithIp(ReportSchedules.fillScroll);
		else
			document.getElementById('hostScroll').style.display = 'none';
		RemoteManager.getUserDetails(ReportSchedules.addUser);
	},

	editBigQueryReady : function() {

		ReportSchedules.findBigQuerySelectedSchedules();
		var scheduleName = ReportSchedules.selectedBigQuerySchedules[0]
				.substring(0, ReportSchedules.selectedBigQuerySchedules[0]
						.indexOf(","));
		var scheduleGroup = ReportSchedules.selectedBigQuerySchedules[0]
				.substring(ReportSchedules.selectedBigQuerySchedules[0]
						.indexOf(",") + 1);
		var schedule = ReportSchedules.findBigQueryScheduleInCache(
				scheduleName, scheduleGroup);
		document.getElementById('headerspan').innerHTML = schedule.name;
		// document.forms[0].interval.options[schedule.interval].selected =
		// true;
		$("#interval").prop("disabled", false);
		var dt = new Date(schedule.time);
		document.forms[0].reportDate.value = (dt.getMonth() + 1) + "/"
				+ dt.getDate() + "/" + (1900 + dt.getYear()) + " "
				+ dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
		document.forms[0].html.checked = schedule.selectedFormat
				.indexOf('HTML') > 0;
		document.forms[0].pdf.checked = schedule.selectedFormat.indexOf('PDF') > 0;
		document.forms[0].xls.checked = schedule.selectedFormat.indexOf('XLS') > 0;
		document.forms[0].notificationEnable.checked = schedule.notificationEnable;
		if (schedule.notificationEnable) {
			document.forms[0].alertRaisedNotificationMessage.value = schedule.notificationMessage;
			document.forms[0].notificationType.options[schedule.notificationType == 'Email' ? 0
					: 1].selected = true;
		}
		ReportSchedules.changeBQButton();
		ReportSchedules.logBQSave();
		ReportSchedules.currentPage = 1;
		ReportSchedules.nextBigQueryScheduleStep(1);
		ReportSchedules.editBigQueryCache = [];
		ReportSchedules.editBigQueryCache.push(schedule);
		RemoteManager.getUserDetails(ReportSchedules.addUser);
		RemoteManager.getNameNodes(ReportSchedules.fillBigQueryNameNodeScroll);
	},

	editMRReady : function() {

		ReportSchedules.findMRSelectedSchedules();
		var scheduleName = ReportSchedules.selectedMRSchedules[0].substring(0,
				ReportSchedules.selectedMRSchedules[0].indexOf(","));
		var scheduleGroup = ReportSchedules.selectedMRSchedules[0]
				.substring(ReportSchedules.selectedMRSchedules[0].indexOf(",") + 1);
		var schedule = ReportSchedules.findMRScheduleInCache(scheduleName,
				scheduleGroup);
		document.getElementById('headerspan').innerHTML = schedule.name;
		// document.forms[0].interval.options[schedule.interval].selected =
		// true;
		$("#interval").prop("disabled", false);
		var dt = new Date(schedule.time);
		document.forms[0].reportDate.value = (dt.getMonth() + 1) + "/"
				+ dt.getDate() + "/" + (1900 + dt.getYear()) + " "
				+ dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
		document.forms[0].notificationEnable.checked = schedule.notificationEnable;
		if (schedule.notificationEnable) {
			document.forms[0].alertRaisedNotificationMessage.value = schedule.notificationMessage;
			document.forms[0].notificationType.options[schedule.notificationType == 'Email' ? 0
					: 1].selected = true;
		}
		ReportSchedules.changeMRButton();
		ReportSchedules.logMRSave();
		ReportSchedules.currentPage = 1;
		ReportSchedules.nextMRScheduleStep(1);
		ReportSchedules.editMRCache = [];
		ReportSchedules.editMRCache.push(schedule);
		RemoteManager.getUserDetails(ReportSchedules.addUser);
		RemoteManager.getJobNameLists(ReportSchedules.fillMRJobScroll);
	},

	checkNotificationSettings : function(nbean) {
		ReportSchedules.emailEnabled = nbean.emailEnabled;
		ReportSchedules.logEnabled = nbean.logEnabled;
	},

	nextScheduleStep : function(selectedDiv) {
		var flag = true;
		if (ReportSchedules.currentPage < selectedDiv) {
			if (ReportSchedules.hostAvailable) {
				if (!ReportSchedules.emailEnabled
						&& !ReportSchedules.logEnabled) {
					document.getElementById('msg_td_1').innerHTML = "* You have not configured any notifications. Please configure notifications and return to this wizard. To configure notifications, go to <b>Dashboard > Notifications</b> tab.";
					return;
				} else if (selectedDiv == 3 && !ReportSchedules.emailEnabled) {
					document.getElementById('msg_td_2').innerHTML = "* You have not configured Email notification. Please configure Email notification and return to this wizard.";
					return;
				} else {
					flag = ReportSchedules.validate();
				}
			} else {
				document.getElementById('msg_td_1').innerHTML = "* No Host Available <br>";
				return;
			}
		}
		if (flag) {
			switch (selectedDiv) {
			case 1: {
				RemoteManager.getAllHostDetails(ReportSchedules.fillHost);
				RemoteManager
						.getNotificationSettings(ReportSchedules.checkNotificationSettings);
				$('#reportdiv1').show();
				$('#reportdiv2').hide();
				$('#reportdiv3').hide();
				break;
			}
			case 2: {
				var isScheduleExists = ReportSchedules.isEditSchedule;
				if (!isScheduleExists) {
					var isScheduleExists = false;
					if ($('#schedules_table_1 tbody tr').length > 0
							&& !$('#schedules_table_1 tbody tr td').hasClass(
									'dataTables_empty')) {
						var scheduleId = document.getElementById('schedID').value;
						$('#schedules_table_1 tbody tr')
								.each(
										function() {
											var row = this.cells;
											if (scheduleId == row[1].textContent) {
												jAlert(
														"Schedule Id already exists. Please specify unique Schedule Id",
														"Incorrect Detail");
												$("#popup_container").css(
														"z-index", "99999999");
												isScheduleExists = true;
											}

										});
					}

					if (!isScheduleExists) {
						$('#reportdiv1').hide();
						$('#reportdiv2').show();
						$('#reportdiv3').hide();
					}

				} else {
					$('#reportdiv1').hide();
					$('#reportdiv2').show();
					$('#reportdiv3').hide();
				}

				break;
			}
			case 3: {
				if (ReportSchedules.currentOperation == "add") {
					RemoteManager.getUserDetails(ReportSchedules.addUser);
				}
				$('#reportdiv1').hide();
				$('#reportdiv2').hide();
				$('#reportdiv3').show();
				$('input[value="Close"]').hide();
				break;
			}
			}
			ReportSchedules.currentPage = selectedDiv;
		}

	},

	nextBigQueryScheduleStep : function(selectedDiv) {
		var flag = true;
		if (ReportSchedules.currentPage < selectedDiv) {
			if (ReportSchedules.hostAvailable) {
				if (!ReportSchedules.emailEnabled
						&& !ReportSchedules.logEnabled) {
					document.getElementById('msg_td_1').innerHTML = "* You have not configured any notifications. Please configure notifications and return to this wizard. To configure notifications, go to <b>Dashboard > Notifications</b> tab.";
					return;
				} else if (selectedDiv == 3 && !ReportSchedules.emailEnabled) {
					document.getElementById('msg_td_1').innerHTML = "* You have not configured Email notification. Please configure Email notification and return to this wizard.";
					return;
				} else {
					flag = ReportSchedules.validateBigQuery();
				}
			} else {
				document.getElementById('msg_td_1').innerHTML = "* No Host Available <br>";
				return;
			}
		}
		if (flag) {
			switch (selectedDiv) {
			case 1: {
				RemoteManager.getAllHostDetails(ReportSchedules.fillHost);
				RemoteManager
						.getNotificationSettings(ReportSchedules.checkNotificationSettings);
				$('#reportdiv1').show();
				$('#reportdiv2').hide();
				$('#reportdiv3').hide();
				break;
			}
			case 2: {
				if (ReportSchedules.currentOperation == "add") {
					ReportSchedules.checkForBigQueryScheduleID();
				} else {
					$('#reportdiv1').hide();
					$('#reportdiv2').show();
					$('#reportdiv3').hide();
				}
				break;
			}
			case 3: {
				if (ReportSchedules.currentOperation == "add") {
					RemoteManager.getUserDetails(ReportSchedules.addUser);
				}
				$('#reportdiv1').hide();
				$('#reportdiv2').hide();
				$('#reportdiv3').show();
				break;
			}
			}
			ReportSchedules.currentPage = selectedDiv;
		}
	},

	nextMRScheduleStep : function(selectedDiv) {
		var flag = true;
		if (ReportSchedules.currentPage < selectedDiv) {
			if (ReportSchedules.hostAvailable) {
				if (!ReportSchedules.emailEnabled
						&& !ReportSchedules.logEnabled) {
					document.getElementById('msg_td_1').innerHTML = "* You have not configured any notifications. Please configure notifications and return to this wizard.  To configure notifications, go to <b>Dashboard > Notifications</b> tab.";
					return;
				} else if (selectedDiv == 3 && !ReportSchedules.emailEnabled) {
					document.getElementById('msg_td_1').innerHTML = "* You have not configured Email notification. Please configure Email notification and return to this wizard.";
					return;
				} else {
					flag = ReportSchedules.validateMR();
				}
			} else {
				document.getElementById('msg_td_1').innerHTML = "* No Host Available <br>";
				return;
			}

		}
		if (flag) {
			switch (selectedDiv) {
			case 1: {
				RemoteManager.getAllHostDetails(ReportSchedules.fillHost);
				RemoteManager
						.getNotificationSettings(ReportSchedules.checkNotificationSettings);
				$('#reportdiv1').show();
				$('#reportdiv2').hide();
				$('#reportdiv3').hide();
				break;
			}
			case 2: {
				if (ReportSchedules.currentOperation == "add") {
					ReportSchedules.checkForMRScheduleID();
				} else {
					$('#reportdiv1').hide();
					$('#reportdiv2').show();
					$('#reportdiv3').hide();
				}
				break;
			}
			case 3: {
				if (ReportSchedules.currentOperation == "add") {
					RemoteManager.getUserDetails(ReportSchedules.addUser);
				}
				$('#reportdiv1').hide();
				$('#reportdiv2').hide();
				$('#reportdiv3').show();
				break;
			}
			}
			ReportSchedules.currentPage = selectedDiv;
		}
	},

	fillScroll : function(list) {
		$('#hostName1 option').remove();
		if (ReportSchedules.currentOperation == "add") {
			for (var i = 0; i < list.length; i++) {
				var str = list[i].substring(list[i].lastIndexOf('#') + 1);
				var flag = true;
				if (str == 'datanode')
					flag = true;
				else if (str == 'namenode')
					flag = true;
				else if (str == 'resourcemanager')
					flag = true;
				else if (str == 'nodemanager')
					flag = true;
				else
					flag = false;
				if (flag) {
					$('#hostName1').append(
							'<option value="'
									+ list[i]
											.substring(0, list[i].indexOf('#'))
									+ '" >'
									+ list[i].substring(
											list[i].indexOf('#') + 1, list[i]
													.lastIndexOf('#'))
									+ ':'
									+ list[i].substring(list[i]
											.lastIndexOf('#') + 1)
									+ '</option>');
				}
				document.getElementById('hostName1').selectedIndex = 0;
			}
		} else {
			var schedule = ReportSchedules.editCache[0];
			for (var i = 0; i < list.length; i++) {
				var str = list[i].substring(list[i].lastIndexOf('#') + 1);
				var flag = true;
				if (str == 'datanode'
						&& schedule.reportId.indexOf("DataNode Detail") > 0)
					flag = true;
				else if (schedule.reportId.indexOf("NameNode Detail") > 0
						&& str == 'namenode')
					flag = true;
				else if (schedule.reportId.indexOf("ResourceManager Detail") > 0
						&& str == 'resourcemanager')
					flag = true;
				else if (schedule.reportId.indexOf("NodeManager Detail") > 0
						&& str == 'nodemanager')
					flag = true;
				else
					flag = false;
				var id = -1;
				var itr = 0;
				if (flag) {
					if (schedule.nodeId == parseInt(list[i].substring(0,
							list[i].indexOf('#'))))
						id = itr;
					$('#hostName1').append(
							'<option value="'
									+ list[i]
											.substring(0, list[i].indexOf('#'))
									+ '" >'
									+ list[i].substring(
											list[i].indexOf('#') + 1, list[i]
													.lastIndexOf('#'))
									+ ':'
									+ list[i].substring(list[i]
											.lastIndexOf('#') + 1)
									+ '</option>');
					itr++;
				}
				document.getElementById('hostName1').selectedIndex = id;
			}
			if (list.length == 0)
				jAlert('No Host Available', function() {
					ReportSchedules.closeBox(false)
				});
		}

	},

	moveSelectedOptions : function(from, to) {

		var source = document.getElementById(from).getElementsByTagName(
				"option");
		for (var i = 0; i < source.length; i++) {
			if (source[i].selected) {
				$('#' + to).append(
						'<option value="' + source[i].value + '">'
								+ ReportSchedules.findUser(source[i].value)
								+ '</option>');
			}
		}
		$("#" + from + " option:selected").remove();
	},

	findUser : function(val) {
		for (var i = 0; i < ReportSchedules.userCache.length; i++) {
			var user = ReportSchedules.userCache[i];
			if (user.id == val)
				return user.firstName + ' ' + user.lastName;
		}
	},

	addUser : function(list) {
		if (list != null) {
			ReportSchedules.userCache = [];
			$('#user').find('option').remove();
			$('#selected').find('option').remove();
			for (var i = 0; i < list.length; i++) {
				var user = list[i];
				ReportSchedules.userCache.push(user);
				if (i == 0)
					$('#user').append(
							'<option value="' + user.id + '" selected>'
									+ user.firstName + ' ' + user.lastName
									+ '</option>');
				else
					$('#user').append(
							'<option value="' + user.id + '">' + user.firstName
									+ ' ' + user.lastName + '</option>');
			}
			ReportSchedules.moveSelectedOptions('user', 'selected');
		}
	},

	updateSchedule : function() {
		var schedule = ReportSchedules.editCache[0];
		document.getElementById('msg_td_3').innerHTML = "";
		var selectedUser = document.getElementById('selected')
				.getElementsByTagName("option");
		var users = [];
		for (var i = 0; i < selectedUser.length; i++) {
			users.push(selectedUser[i].value);
		}
		if (users.length > 0) {
			var reportsType = ReportSchedules.findreportType(schedule);
			RemoteManager
					.updateJob(
							document.forms[0].interval.value,
							document.forms[0].reportDate.value,
							ReportSchedules.findExportType(),
							document.getElementById('notificationType').value,
							document
									.getElementById('alertRaisedNotificationMessage').value,
							users, reportsType, schedule.name, schedule.group,
							schedule.nodeId, $('#notificationEnable').is(
									':checked'), ReportSchedules.scheduleReturn);
		} else {
			document.getElementById('msg_td_3').innerHTML += "* Users Not Selected<br>";
		}
	},

	updateBigQuerySchedule : function() {
		var schedule = ReportSchedules.editBigQueryCache[0];
		var selectedUser = document.getElementById('selected')
				.getElementsByTagName("option");
		var users = [];
		for (var i = 0; i < selectedUser.length; i++) {
			users.push(selectedUser[i].value);
		}
		if (users.length > 0) {
			var reportScheduleForm = document.getElementById('scheduleReport');
			RemoteManager
					.updateQueryJob(
							reportScheduleForm.interval.value,
							reportScheduleForm.reportDate.value,
							ReportSchedules.findExportType(reportScheduleForm),
							document.getElementById('notificationType').value,
							document
									.getElementById('alertRaisedNotificationMessage').value,
							users, schedule.name, schedule.group, $('#queryID')
									.val(), $('#nameNodeID').val(), $(
									'#notificationEnable').is(':checked'),
							ReportSchedules.scheduleReturn);
		} else {
			jAlert("Users Not Selected.", "Incomplete Detail");
			$("#popup_container").css("z-index", "99999999999");
			return false;
		}
	},

	updateMRSchedule : function() {
		var schedule = ReportSchedules.editMRCache[0];
		var selectedUser = document.getElementById('selected')
				.getElementsByTagName("option");
		var users = [];
		for (var i = 0; i < selectedUser.length; i++) {
			users.push(selectedUser[i].value);
		}
		if (users.length > 0) {
			var reportScheduleForm = document.getElementById('scheduleReport');
			RemoteManager
					.updateMapRedJob(
							reportScheduleForm.interval.value,
							reportScheduleForm.reportDate.value,
							$('#jobName').val(),
							schedule.name,
							document.getElementById('notificationType').value,
							document
									.getElementById('alertRaisedNotificationMessage').value,
							users, schedule.group, $('#notificationEnable').is(
									':checked'), ReportSchedules.scheduleReturn);
		} else {
			jAlert("Users Not Selected.", "Incomplete Detail");
			$("#popup_container").css("z-index", "99999999999");
			return false;
		}
	},

	scheduleReturn : function(flag) {
		if (flag) {
			if (ReportSchedules.currentOperation == "edit") {
				jAlert('Successfully completed update operation', "Success");
			} else {
				jAlert('Successfully completed '
						+ ReportSchedules.currentOperation + ' operation',
						"Success");
			}
		} else {
			jAlert("Error occurred while updating job.", "Error");
		}
		$("#popup_container").css("z-index", "99999999");
		ReportSchedules.closeBox(true);
	},

	findExportType : function() {
		var exportFormatList = [];
		if (document.forms[0].html.checked)
			exportFormatList.push(0);
		if (document.forms[0].pdf.checked)
			exportFormatList.push(1);
		if (document.forms[0].xls.checked)
			exportFormatList.push(3);
		return exportFormatList;
	},

	validate : function() {
		var flag = true;

		if (ReportSchedules.currentOperation == "add") {
			switch (ReportSchedules.currentPage) {
			case 1: {
				document.getElementById('msg_td_1').innerHTML = "";
				if (document.getElementById('schedID').value == ""
						|| document.getElementById('schedID').value == null) {
					jAlert("Schedule Id not set.", "Incomplete Detail");
					$("#popup_container").css("z-index", "9999999");
					return false;
				}

				var reportArr = $('#reportType').val();
				if (reportArr == null) {
					jAlert("Report Type Not Selected", "Incomplete Detail");
					$("#popup_container").css("z-index", "9999999");
					return false;
				}
				var reportDate = document.forms[0].reportDate.value;
				if (reportDate == "") {
					jAlert("Date & Time Not Provided", "Incomplete Detail");
					$("#popup_container").css("z-index", "9999999");
					return false;
				}
				var exportFormatList = ReportSchedules.findExportType();
				if (exportFormatList.length == 0) {
					jAlert("Format Not Selected", "Incomplete Detail");
					$("#popup_container").css("z-index", "9999999");
					return false;
				}
				break;
			}
			case 2: {
				document.getElementById('msg_td_2').innerHTML = "";
				if (document.getElementById('alertRaisedNotificationMessage').value == "") {
					jAlert("No Message with alert set.", "Incomplete Detail");
					$("#popup_container").css("z-index", "9999999");
					return false;
				}
				break;
			}
			}
		} else {
			switch (ReportSchedules.currentPage) {
			case 1: {
				document.getElementById('msg_td_1').innerHTML = "";
				var reportDate = document.forms[0].reportDate.value;
				if (reportDate == "") {
					document.getElementById('msg_td_1').innerHTML += "* Date & Time Not Provided<br>";
					flag = false;
				}
				var exportFormatList = ReportSchedules.findExportType();
				if (exportFormatList.length == 0) {
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
		}
		return flag;
	},

	checkForBigQueryScheduleID : function() {
		if (document.getElementById('schedID').value != "") {
			RemoteManager.checkBigQueryScheduleId(document
					.getElementById('schedID').value,
					ReportSchedules.checkBigQueryResp);
		} else {
			document.getElementById('msg_td_1').innerHTML = "";
			document.getElementById('msg_td_1').innerHTML += "* Schedule Id Required<br>";
		}
	},

	checkBigQueryResp : function(flag) {
		if (flag) {
			jAlert(
					"ScheduleId already exists. Please specify unique Schedule Id",
					"Error");
		} else {
			$('#reportdiv1').hide();
			$('#reportdiv2').show();
			$('#reportdiv3').hide();
		}
		$("#popup_container").css("z-index", "99999999");
	},

	validateBigQuery : function() {
		var flag = true;
		switch (ReportSchedules.currentPage) {
		case 1: {
			document.getElementById('msg_td_1').innerHTML = "";
			if ($('#queryID').val() == null) {
				document.getElementById('msg_td_1').innerHTML += "* QueryID no selected<br>";
				flag = false;
			}
			var reportDate = document.forms[0].reportDate.value;
			if (reportDate == "") {
				document.getElementById('msg_td_1').innerHTML += "* Date & Time Not Provided<br>";
				flag = false;
			}
			var exportFormatList = ReportSchedules.findExportType();
			if (exportFormatList.length == 0) {
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

	checkForMRScheduleID : function() {
		if (document.getElementById('schedID').value != "") {
			RemoteManager.checkMapRedScheduleId(document
					.getElementById('schedID').value,
					ReportSchedules.checkMRResp);
		} else {
			document.getElementById('msg_td_1').innerHTML = "";
			document.getElementById('msg_td_1').innerHTML += "* Schedule Id Required<br>";
		}
	},

	checkMRResp : function(flag) {
		if (flag) {
			jAlert(
					"ScheduleId already exists. Please specify unique Schedule Id",
					"Error");
		} else {
			$('#reportdiv1').hide();
			$('#reportdiv2').show();
			$('#reportdiv3').hide();
		}
		$("#popup_container").css("z-index", "99999999");
	},

	validateMR : function() {
		var flag = true;
		switch (ReportSchedules.currentPage) {
		case 1: {
			document.getElementById('msg_td_1').innerHTML = "";
			if ($('#jobName').val() == null) {
				document.getElementById('msg_td_1').innerHTML += "* Job Name not selected<br>";
				flag = false;
			}
			var reportDate = document.forms[0].reportDate.value;
			if (reportDate == "") {
				document.getElementById('msg_td_1').innerHTML += "* Date & Time Not Provided<br>";
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

	findreportType : function(schedule) {
		var reportType = [];
		if (ReportSchedules.currentOperation == "add") {
			var reportArr = $('#reportType').val();
			if (reportArr.indexOf("hdfs") > -1)
				reportType.push(0);
			if (reportArr.indexOf("mapReduce") > -1)
				reportType.push(14);
			if (reportArr.indexOf("namenode") > -1)
				reportType.push(1);
			if (reportArr.indexOf("datanode") > -1)
				reportType.push(2);
			if (reportArr.indexOf("sforecast") > -1)
				reportType.push(5);
			if (reportArr.indexOf("alert") > -1)
				reportType.push(6);
			if (reportArr.indexOf("nndetail") > -1)
				reportType.push(8);
			if (reportArr.indexOf('dndetail') > -1)
				reportType.push(9);
			if (reportArr.indexOf('resourcemanager') > -1)
				reportType.push(10);
			if (reportArr.indexOf('nodemanager') > -1)
				reportType.push(11);
			if (reportArr.indexOf("rmDetail") > -1)
				reportType.push(12);
			if (reportArr.indexOf('nmDetail') > -1)
				reportType.push(13);

		} else {
			if (schedule.reportId.indexOf("HDFS Summary") > -1)
				reportType.push(0);
			if (schedule.reportId.indexOf("MapReduce Summary") > -1)
				reportType.push(14);
			if (schedule.reportId.indexOf("NameNode Summary") > -1)
				reportType.push(1);
			if (schedule.reportId.indexOf("DataNode Summary") > -1)
				reportType.push(2);
			if (schedule.reportId.indexOf("NameNode Detail") > -1)
				reportType.push(8);
			if (schedule.reportId.indexOf("DataNode Detail") > -1)
				reportType.push(9);
			if (schedule.reportId.indexOf("ResourceManager Summary") > -1)
				reportType.push(10);
			if (schedule.reportId.indexOf("NodeManager Summary") > -1)
				reportType.push(11);
			if (schedule.reportId.indexOf("ResourceManager Detail") > -1)
				reportType.push(12);
			if (schedule.reportId.indexOf("NodeManager Detail") > -1)
				reportType.push(13);
		}
		return reportType;

	},

	findFrequency : function(freq) {
		var strFreq = "";
		switch (parseInt(freq)) {
		case 0:
			strFreq = "Once";
			break;
		case 1:
			strFreq = "Twelve Hours";
			break;
		case 2:
			strFreq = "Daily";
			break;
		case 3:
			strFreq = "Weekly";
			break;
		}
		return strFreq;
	},

	reportTypeView : function(viewNo) {
		var selBox = document.getElementById('reportType');
		selBox.innerHTML = "";
		switch (viewNo) {
		case 1:
			document.getElementById('hostScroll').style.display = 'none';
			ReportSchedules.addOption(selBox, 'hdfs', 'HDFS Summary');
			ReportSchedules.addOption(selBox, 'mapReduce', 'MapReduce Summary');
			ReportSchedules.addOption(selBox, 'namenode', 'NameNode Summary');
			ReportSchedules.addOption(selBox, 'datanode', 'DataNode Summary');
			ReportSchedules.addOption(selBox, 'resourcemanager',
					'ResourceManager Summary');
			ReportSchedules.addOption(selBox, 'nodemanager',
					'NodeManager Summary');
			ReportSchedules.addOption(selBox, 'sforecast', 'Storage Forecast');
			ReportSchedules.addOption(selBox, 'alert', 'Alert Summary');
			break;
		case 2:
			document.getElementById('hostScroll').style.display = '';
			ReportSchedules.addOption(selBox, 'nndetail', 'NameNode Detail');
			ReportSchedules.addOption(selBox, 'dndetail', 'DataNode Detail');
			ReportSchedules.addOption(selBox, 'rmDetail',
					'ResourceManager Detail');
			ReportSchedules.addOption(selBox, 'nmDetail', 'NodeManager Detail');
			break;
		}
	},

	schedule : function() {
		ReportSchedules.isEditSchedule = false;
		ReportSchedules.currentOperation = "add";
		Util.addLightbox("generateReport", "resources/scheduleReports.html",
				null, null);
	},

	addOption : function(selectbox, value, text) {
		var optn = document.createElement("OPTION");
		optn.text = text;
		optn.value = value;
		selectbox.options.add(optn);
	},

	addSchedule : function() {
		var selectedUser = document.getElementById('selected')
				.getElementsByTagName("option");
		var users = [];
		for (var i = 0; i < selectedUser.length; i++) {
			users.push(selectedUser[i].value);
		}
		if (users.length > 0) {

			var reportScheduleForm = document.getElementById('scheduleReport');
			var reportsType = ReportSchedules.findreportType();
			var nodeID = -1;
			if (reportsType.indexOf(8) > -1 || reportsType.indexOf(9) > -1
					|| reportsType.indexOf(12) > -1
					|| reportsType.indexOf(13) > -1)
				// nodeID =
				// parseInt(document.getElementById('hostName1').value);
				nodeID = document.getElementById('hostName1').value;
			// console.log('nodeID' , nodeID);
			RemoteManager
					.scheduleJob(
							reportScheduleForm.interval.value,
							reportScheduleForm.reportDate.value,
							ReportSchedules.findExportType(reportScheduleForm),
							document.getElementById('notificationType').value,
							document
									.getElementById('alertRaisedNotificationMessage').value,
							users, reportsType, nodeID, document
									.getElementById('schedID').value,
							ReportSchedules.scheduleReturn);

		} else {
			jAlert("Users Not Selected.", "Incomplete Detail");
			$("#popup_container").css("z-index", "99999999999");
			return false;
		}
	},

	addBigQuerySchedule : function() {
		var selectedUser = document.getElementById('selected')
				.getElementsByTagName("option");
		var users = [];
		for (var i = 0; i < selectedUser.length; i++) {
			users.push(selectedUser[i].value);
		}
		if (users.length > 0) {
			var reportScheduleForm = document.getElementById('scheduleReport');
			RemoteManager
					.scheduleQueryJob(
							reportScheduleForm.interval.value,
							reportScheduleForm.reportDate.value,
							ReportSchedules.findExportType(reportScheduleForm),
							document.getElementById('notificationType').value,
							document
									.getElementById('alertRaisedNotificationMessage').value,
							users, document.getElementById('schedID').value, $(
									'#queryID').val(), $('#nameNodeID').val(),
							ReportSchedules.scheduleReturn);
		} else {
			jAlert("Users Not Selected.", "Incomplete Detail");
			$("#popup_container").css("z-index", "99999999999");
			return false;
		}
	},

	addMRSchedule : function() {
		var selectedUser = document.getElementById('selected')
				.getElementsByTagName("option");
		var users = [];
		for (var i = 0; i < selectedUser.length; i++) {
			users.push(selectedUser[i].value);
		}
		if (users.length > 0) {
			var reportScheduleForm = document.getElementById('scheduleReport');

			RemoteManager
					.scheduleMapRedJob(
							reportScheduleForm.interval.value,
							reportScheduleForm.reportDate.value,
							$('#jobName').val(),
							document.getElementById('schedID').value,
							document.getElementById('notificationType').value,
							document
									.getElementById('alertRaisedNotificationMessage').value,
							users, ReportSchedules.scheduleReturn);

		} else {
			jAlert("Users Not Selected.", "Incomplete Detail");
			$("#popup_container").css("z-index", "99999999999");
			return false;
		}
	},

	fillhostSelection : function() {
		var hostSelection = ReportSchedules.findreportType();
		if ((hostSelection.indexOf(8) > -1) && (hostSelection.indexOf(9) > -1)
				&& (hostSelection.indexOf(12) > -1)
				&& (hostSelection.indexOf(13) > -1)) {
			RemoteManager.getAllNodeIdWithIp(ReportSchedules.fillScroll);
		} else if (hostSelection.indexOf(8) > -1) {
			RemoteManager.getAllNameNodeIdWithIp(ReportSchedules.fillScroll);
		} else if (hostSelection.indexOf(9) > -1) {
			RemoteManager.getAllDataNodeIdWithIp(ReportSchedules.fillScroll);
		} else if (hostSelection.indexOf(12) > -1) {
			RemoteManager
					.getAllResourceManagerIdWithIp(ReportSchedules.fillScroll);
		} else if (hostSelection.indexOf(13) > -1) {
			RemoteManager.getAllNodeManagerIdWithIp(ReportSchedules.fillScroll);
		}
	},

	fillBigQueryNameNodeScroll : function(list) {
		if (ReportSchedules.currentOperation == "edit") {
			var schedule = ReportSchedules.editBigQueryCache[0];
			$('#nameNodeID option').remove();
			for (var i = 0; i < list.length; i++) {
				if (schedule.nameNode == list[i].id) {
					$('#nameNodeID').append(
							'<option selected value="' + list[i].id + '" >'
									+ list[i].id + '</option>');
				} else {
					$('#nameNodeID').append(
							'<option value="' + list[i].id + '" >' + list[i].id
									+ '</option>');
				}
			}
			RemoteManager.getAllBigQueriesId(schedule.nameNode,
					ReportSchedules.fillBigQueryScroll);
		} else {
			$('#nameNodeID option').remove();
			$('#queryID option').remove();
			for (var i = 0; i < list.length; i++) {
				if (i == 0) {
					$('#nameNodeID').append(
							'<option selected value="' + list[i].id + '" >'
									+ list[i].id + '</option>');
				} else {
					$('#nameNodeID').append(
							'<option value="' + list[i].id + '" >' + list[i].id
									+ '</option>');
				}
			}
			RemoteManager.getAllBigQueriesId($('#nameNodeID').val(),
					ReportSchedules.fillBigQueryScroll);
		}
	},

	fillBigQueryScroll : function(list) {
		if (ReportSchedules.currentOperation == "edit") {
			var schedule = ReportSchedules.editBigQueryCache[0];
			$('#queryID option').remove();
			for (var i = 0; i < list.length; i++) {
				if (schedule.query.indexOf(list[i]) > 0) {
					$('#queryID').append(
							'<option selected value="' + list[i] + '" >'
									+ list[i] + '</option>');
				} else {
					$('#queryID').append(
							'<option value="' + list[i] + '" >' + list[i]
									+ '</option>');
				}
			}
		} else {
			$('#queryID option').remove();
			for (var i = 0; i < list.length; i++) {
				$('#queryID').append(
						'<option value="' + list[i] + '" >' + list[i]
								+ '</option>');
			}
		}
	},

	fillMRJobScroll : function(list) {
		if (list != null) {
			if (ReportSchedules.currentOperation == "edit") {
				var schedule = ReportSchedules.editMRCache[0];
				$('#jobName option').remove();
				for (var i = 0; i < list.length; i++) {
					if (schedule.jobName.indexOf(list[i]) > 0) {
						$('#jobName').append(
								'<option selected value="' + list[i] + '" >'
										+ list[i] + '</option>');
					} else {
						$('#jobName').append(
								'<option value="' + list[i] + '" >' + list[i]
										+ '</option>');
					}
				}
			} else {
				$('#jobName option').remove();
				for (var i = 0; i < list.length; i++) {
					$('#jobName').append(
							'<option value="' + list[i] + '" >' + list[i]
									+ '</option>');
				}
			}
		}
	},

	performBigQueryOperation : function() {
		if (ReportSchedules.currentOperation == "add") {
			ReportSchedules.addBigQuerySchedule();
		} else {
			ReportSchedules.updateBigQuerySchedule();
		}
	},

	performMROperation : function() {
		if (ReportSchedules.currentOperation == "add") {
			ReportSchedules.addMRSchedule();
		} else {
			ReportSchedules.updateMRSchedule();
		}
	},

	scheduleBigQuery : function() {
		ReportSchedules.currentOperation = "add";
		Util.addLightbox("generateReport", "resources/editScheduleQuery.html",
				null, null);
	},

	scheduleMR : function() {
		ReportSchedules.currentOperation = "add";
		Util.addLightbox("generateReport", "resources/editScheduleMRJob.html",
				null, null);
	},

	changeQueryId : function(nn) {
		RemoteManager
				.getAllBigQueriesId(nn, ReportSchedules.fillBigQueryScroll);
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

	changeMRButton : function() {
		if ($('#notificationEnable').attr('checked')) {
			document.getElementById('MRNext').disabled = false;
			document.getElementById('MRFinish').disabled = true;
		} else {
			document.getElementById('MRNext').disabled = true;
			document.getElementById('MRFinish').disabled = false;
		}
	},

	changeButton : function() {
		if ($('#notificationEnable').attr('checked')) {
			document.getElementById('SRNext').disabled = false;
			document.getElementById('SRFinish').disabled = true;
		} else {
			document.getElementById('SRNext').disabled = true;
			document.getElementById('SRFinish').disabled = false;
		}
	},

	scheduleBQwithoutNotify : function() {
		ReportSchedules.currentPage = 1;
		var flag = ReportSchedules.validateBigQuery();
		if (flag) {
			if (ReportSchedules.currentOperation == 'add') {
				ReportSchedules.validateForBQSScheduleID();
			} else {
				var schedule = ReportSchedules.editBigQueryCache[0];
				var reportScheduleForm = document
						.getElementById('scheduleReport');
				RemoteManager.updateQueryJob(reportScheduleForm.interval.value,
						reportScheduleForm.reportDate.value, ReportSchedules
								.findExportType(reportScheduleForm), null,
						null, null, schedule.name, schedule.group,
						$('#queryID').val(), $('#nameNodeID').val(), false,
						ReportSchedules.scheduleReturn);
			}
		}

	},

	validateForBQSScheduleID : function() {
		if (document.getElementById('schedID').value != "") {
			RemoteManager.checkBigQueryScheduleId(document
					.getElementById('schedID').value,
					ReportSchedules.checkValidateBQResp);
		} else {
			document.getElementById('msg_td_1').innerHTML = "";
			document.getElementById('msg_td_1').innerHTML += "* Schedule Id Required<br>";
		}
	},

	checkValidateBQResp : function(flag) {
		if (flag) {
			jAlert(
					"ScheduleId already exists. Please specify unique Schedule Id",
					"Error");
			$("#popup_container").css("z-index", "99999999");
		} else {
			var reportScheduleForm = document.getElementById('scheduleReport');
			RemoteManager.scheduleQueryJobWithoutNotification(
					reportScheduleForm.interval.value,
					reportScheduleForm.reportDate.value, ReportSchedules
							.findExportType(reportScheduleForm), document
							.getElementById('schedID').value, $('#queryID')
							.val(), $('#nameNodeID').val(),
					ReportSchedules.scheduleReturn);
		}
	},

	scheduleMRwithoutNotify : function() {
		ReportSchedules.currentPage = 1;
		var flag = ReportSchedules.validateMR();
		if (flag) {
			if (ReportSchedules.currentOperation == 'add') {
				ReportSchedules.validateForMRScheduleID();
			} else {
				var schedule = ReportSchedules.editMRCache[0];
				var reportScheduleForm = document
						.getElementById('scheduleReport');
				RemoteManager.updateMapRedJob(
						reportScheduleForm.interval.value,
						reportScheduleForm.reportDate.value, $('#jobName')
								.val(), schedule.name, null, null, null,
						schedule.group, false, ReportSchedules.scheduleReturn);
			}
		}
	},

	validateForMRScheduleID : function() {
		if (document.getElementById('schedID').value != "") {
			RemoteManager.checkMapRedScheduleId(document
					.getElementById('schedID').value,
					ReportSchedules.checkValidateMRResp);
		} else {
			document.getElementById('msg_td_1').innerHTML = "";
			document.getElementById('msg_td_1').innerHTML += "* Schedule Id Required<br>";
		}
	},

	checkValidateMRResp : function(flag) {
		if (flag) {
			jAlert(
					"ScheduleId already exists. Please specify unique Schedule Id",
					"Error");
			$("#popup_container").css("z-index", "99999999");
		} else {
			var reportScheduleForm = document.getElementById('scheduleReport');
			RemoteManager.scheduleMapRedJobWithoutNotification(
					reportScheduleForm.interval.value,
					reportScheduleForm.reportDate.value, $('#jobName').val(),
					document.getElementById('schedID').value,
					ReportSchedules.scheduleReturn);
		}
	},

	scheduleWithoutNotify : function() {
		ReportSchedules.currentPage = 1;
		var flag = ReportSchedules.validate();

		if (flag) {
			if (ReportSchedules.currentOperation == 'add') {
				ReportSchedules.validateForScheduleID();
			} else {
				var schedule = ReportSchedules.editCache[0];
				var reportsType = ReportSchedules.findreportType(schedule);
				RemoteManager.updateJob(document.forms[0].interval.value,
						document.forms[0].reportDate.value, ReportSchedules
								.findExportType(), null, null, null,
						reportsType, schedule.name, schedule.group,
						schedule.nodeId, false, ReportSchedules.scheduleReturn);
			}
		}
	},

	validateForScheduleID : function() {
		if (document.getElementById('schedID').value != "") {
			RemoteManager.checkSysReportScheduleId(document
					.getElementById('schedID').value,
					ReportSchedules.checkValidateResp);
		} else {
			document.getElementById('msg_td_1').innerHTML = "";
			document.getElementById('msg_td_1').innerHTML += "* Schedule Id Required<br>";
		}
	},

	checkValidateResp : function(flag) {
		if (flag) {
			jAlert(
					"ScheduleId already exists. Please specify unique Schedule Id",
					"Error");
			$("#popup_container").css("z-index", "99999999");
		} else {
			var reportScheduleForm = document.getElementById('scheduleReport');
			var reportsType = ReportSchedules.findreportType();
			var nodeID = -1;
			if (reportsType.indexOf(8) > -1 || reportsType.indexOf(9) > -1
					|| reportsType.indexOf(12) > -1
					|| reportsType.indexOf(13) > -1) {
				// nodeID =
				// parseInt(document.getElementById('hostName1').value);
				nodeID = document.getElementById('hostName1').value;
			}
			RemoteManager.scheduleJobWithoutNotification(
					reportScheduleForm.interval.value,
					reportScheduleForm.reportDate.value, ReportSchedules
							.findExportType(reportScheduleForm), reportsType,
					nodeID, document.getElementById('schedID').value,
					ReportSchedules.scheduleReturn);
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

	logMRSave : function() {

		if ($('#notificationType').val() == 'Log') {
			document.getElementById('EmailNext').disabled = true;
			document.getElementById('logSave').disabled = false;
		} else {
			document.getElementById('EmailNext').disabled = false;
			document.getElementById('logSave').disabled = true;
		}
	},

	logSave : function() {
		if ($('#notificationType').val() == 'Log') {
			document.getElementById('EmailNext').disabled = true;
			document.getElementById('logSave').disabled = false;
		} else {
			document.getElementById('logSave').disabled = true;
			document.getElementById('EmailNext').disabled = false;

		}
	},

	saveMRLog : function() {
		var flag = ReportSchedules.validateMR();
		if (!ReportSchedules.logEnabled) {
			document.getElementById('msg_td_2').innerHTML = "* You have not configured Log notification. Please configure Log notification and return to this wizard.";
			return;
		}
		if (flag) {
			if (ReportSchedules.currentOperation == 'add') {
				var reportScheduleForm = document
						.getElementById('scheduleReport');
				RemoteManager
						.scheduleMapRedJob(
								reportScheduleForm.interval.value,
								reportScheduleForm.reportDate.value,
								$('#jobName').val(),
								document.getElementById('schedID').value,
								document.getElementById('notificationType').value,
								document
										.getElementById('alertRaisedNotificationMessage').value,
								null, ReportSchedules.scheduleReturn);
			} else {
				var schedule = ReportSchedules.editMRCache[0];
				var reportScheduleForm = document
						.getElementById('scheduleReport');
				RemoteManager
						.updateMapRedJob(
								reportScheduleForm.interval.value,
								reportScheduleForm.reportDate.value,
								$('#jobName').val(),
								schedule.name,
								document.getElementById('notificationType').value,
								document
										.getElementById('alertRaisedNotificationMessage').value,
								null, schedule.group, true,
								ReportSchedules.scheduleReturn);
			}
		}
	},

	saveBQLog : function() {
		var flag = ReportSchedules.validateBigQuery();
		if (!ReportSchedules.logEnabled) {
			document.getElementById('msg_td_2').innerHTML = "* You have not configured Log notification. Please configure Log notification and return to this wizard.";
			return;
		}
		if (flag) {
			if (ReportSchedules.currentOperation == 'add') {
				var reportScheduleForm = document
						.getElementById('scheduleReport');
				RemoteManager
						.scheduleQueryJob(
								reportScheduleForm.interval.value,
								reportScheduleForm.reportDate.value,
								ReportSchedules
										.findExportType(reportScheduleForm),
								document.getElementById('notificationType').value,
								document
										.getElementById('alertRaisedNotificationMessage').value,
								null, document.getElementById('schedID').value,
								$('#queryID').val(), $('#nameNodeID').val(),
								ReportSchedules.scheduleReturn);
			} else {
				var schedule = ReportSchedules.editBigQueryCache[0];
				var reportScheduleForm = document
						.getElementById('scheduleReport');
				RemoteManager
						.updateQueryJob(
								reportScheduleForm.interval.value,
								reportScheduleForm.reportDate.value,
								ReportSchedules
										.findExportType(reportScheduleForm),
								document.getElementById('notificationType').value,
								document
										.getElementById('alertRaisedNotificationMessage').value,
								null, schedule.name, schedule.group, $(
										'#queryID').val(), $('#nameNodeID')
										.val(), true,
								ReportSchedules.scheduleReturn);
			}
		}

	},

	saveLog : function() {
		var flag = ReportSchedules.validate();
		if (!ReportSchedules.logEnabled) {
			document.getElementById('msg_td_2').innerHTML = "* You have not configured Log notification. Please configure Log notification and return to this wizard.";
			return;
		}
		if (flag) {
			if (ReportSchedules.currentOperation == 'add') {
				var reportScheduleForm = document
						.getElementById('scheduleReport');
				var reportsType = ReportSchedules.findreportType();
				var nodeID = -1;
				if (reportsType.indexOf(8) > -1 || reportsType.indexOf(9) > -1
						|| reportsType.indexOf(12) > -1
						|| reportsType.indexOf(13) > -1) {
					// nodeID =
					// parseInt(document.getElementById('hostName1').value);
					nodeID = document.getElementById('hostName1').value;
				}
				RemoteManager
						.scheduleJob(
								reportScheduleForm.interval.value,
								reportScheduleForm.reportDate.value,
								ReportSchedules
										.findExportType(reportScheduleForm),
								document.getElementById('notificationType').value,
								document
										.getElementById('alertRaisedNotificationMessage').value,
								null, reportsType, nodeID, document
										.getElementById('schedID').value,
								ReportSchedules.scheduleReturn);
			} else {
				var schedule = ReportSchedules.editCache[0];
				var reportsType = ReportSchedules.findreportType(schedule);
				RemoteManager
						.updateJob(
								document.forms[0].interval.value,
								document.forms[0].reportDate.value,
								ReportSchedules.findExportType(),
								document.getElementById('notificationType').value,
								document
										.getElementById('alertRaisedNotificationMessage').value,
								null, reportsType, schedule.name,
								schedule.group, schedule.nodeId, true,
								ReportSchedules.scheduleReturn);
			}
		}
	},

	showTriggerDetails : function(jobName, jobGroup) {
		triggerJobName = jobName;
		triggerJobGroup = jobGroup;
		Util.addLightbox("generateReport",
				"resources/schedule_trigger_details.html", null, null);
	},

	populateTriggerDetail : function() {
		RemoteManager.getTriggerDetails(triggerJobGroup, triggerJobName,
				ReportSchedules.showTriggerTable);
	},

	showTriggerTable : function(list) {
		var tabledata = [];
		if (list != null) {
			for (var i = 0; i < list.length; i++) {
				var triggerBean = list[i];
				tabledata.push([ triggerBean.jobName, triggerBean.startTime,
						triggerBean.endTime, triggerBean.status ]);
			}
		}
		$('#schedules_trigger_table').dataTable({
			"sScrollX" : "100%",
			"bPaginate" : true,
			"bLengthChange" : true,
			"sPaginationType" : "full_numbers",
			"bFilter" : false,
			"bSort" : true,
			"bInfo" : false,
			"bAutoWidth" : true,
			"aLengthMenu" : [ [ 50, 100, 200, 500 ], [ 50, 100, 200, 500 ] ],
			"aaSorting" : [ [ 1, "desc" ] ],
			"aaData" : tabledata,
			"aoColumnDefs" : [ {
				"bSortable" : false,
				"aTargets" : [ 0 ]
			} ],
			"aoColumns" : [ {
				"sTitle" : "Job Name"
			}, {
				"sTitle" : "Start Time"
			}, {
				"sTitle" : "End Time"
			}, {
				"sTitle" : "Status"
			}, ]
		});
	}

};
