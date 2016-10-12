Reports = {
	isFromScheduler : false,
	status : [],
	userCache : [],
	currentSchedulePage : 1,
	reportType : '',
	users : '',
	startTime : '',
	endTime : '',
	hostAvailable : false,
	emailEnabled : false,
	logEnabled : false,

	emailReady : function() {
		document.forms[1].email.disabled = false;
		$('#emailReportDiv_1').show();
		$('#emailReportDiv_2').hide();
		Reports.setButtonDisable();
		if (document.getElementById('nndetail').checked
				|| document.getElementById('dndetail').checked
				|| document.getElementById('rmDetail').checked
				|| document.getElementById('nmDetail').checked)
			RemoteManager.getAllNodeIdWithIp(Reports.fillScroll);
		else
			document.getElementById('hostScroll').style.display = 'none';
		RemoteManager.getAllHostDetails(Reports.fillHost);
		RemoteManager
				.getNotificationSettings(Reports.checkNotificationSettings);
	},

	fillHost : function(list) {
		if (list.length > 0) {
			Reports.hostAvailable = true;
		}
	},

	fillScroll : function(list) {
		$('#hostName1').html('');
		for (var i = 0; i < list.length; i++) {
			var str = list[i].substring(list[i].lastIndexOf('#') + 1);
			var flag = true;
			if (str == 'datanode'
					&& document.getElementById('dndetail').checked)
				flag = true;
			else if (str == 'namenode'
					&& document.getElementById('nndetail').checked)
				flag = true;
			else if (str == 'resourcemanager'
					&& document.getElementById('rmDetail').checked)
				flag = true;
			else if (str == 'nodemanager'
					&& document.getElementById('nmDetail').checked)
				flag = true;
			else
				flag = false;
			if (flag) {
				$('#hostName1').append(
						'<option value="'
								+ list[i].substring(0, list[i].indexOf('#'))
								+ '" >'
								+ list[i].substring(0, list[i].indexOf('#'))
								+ "("
								+ list[i].substring(list[i].indexOf('#') + 1,
										list[i].lastIndexOf('#'))
								+ ')</option>');
			}
		}
	},

	findreportType : function() {
		var reportType = [];

		for (var i = 0; i < this.status.length; i++) {
			if (this.status[i] == "hdfs")
				reportType.push(0);
			if (this.status[i] == "mapReduce")
				reportType.push(14);
			if (this.status[i] == "namenode")
				reportType.push(1);
			if (this.status[i] == "datanode")
				reportType.push(2);
			if (this.status[i] == "alert")
				reportType.push(5);
			if (this.status[i] == "sforecast")
				reportType.push(6);
			if (this.status[i] == "resourcemanager")
				reportType.push(10);
			if (this.status[i] == "nodemanager")
				reportType.push(11);
		}
		// console.log('reportType' , reportType);
		return reportType;
	},

	addUser : function(list) {
		if (list != null) {
			if (Reports.userCache.length == 0) {
				for (var i = 0; i < list.length; i++) {
					user = list[i];
					Reports.userCache.push(user);
					$('#user').append(
							'<option value="' + user.id + '">' + user.firstName
									+ ' ' + user.lastName + '</option>');
				}
			}
		}
	},

	emailReport : function() {
		Util.addLightbox("generateReport", "resources/email_report.html", null,
				null);
	},

	findStatus : function() {
		// console.log('in findStatus');
		this.status = [];
		if (document.getElementById('hdfs').checked)
			this.status.push('hdfs');
		if (document.getElementById('mapReduce').checked)
			this.status.push('mapReduce');
		if (document.getElementById('namenode').checked)
			this.status.push('namenode');
		if (document.getElementById('datanode').checked)
			this.status.push('datanode');
		if (document.getElementById('resourcemanager').checked)
			this.status.push('resourcemanager');
		if (document.getElementById('nodemanager').checked)
			this.status.push('nodemanager');
		if (document.getElementById('alert').checked)
			this.status.push('alert');
		if (document.getElementById('sforecast').checked)
			this.status.push('sforecast');
		// if(document.getElementById('rmDetail').checked)
		// this.status.push('rmDetail');
		// if(document.getElementById('nmDetail').checked)
		// this.status.push('nmDetail');
		// if(document.getElementById('nndetail').checked)
		// this.status.push('nndetail');
		// if(document.getElementById('dndetail').checked)
		// this.status.push('dndetail');

	},

	view : function() {
		if (!Reports.hostAvailable) {
			document.getElementById('msg_td_1').innerHTML = "* No Host Available <br>";
			return;
		}
		Reports.findStatus();
		document.getElementById('msg_td_1').innerHTML = "";
		var title = document.forms[1].title.value;
		var endTime = '';
		var startTime = ''
		if ($('#rTime').is(':checked')) {
			endTime = document.getElementById('reportInterval').value;
			// console.log('if endTime', endTime);
			var d = (new Date()).getTime();
			// console.log('if d', d);
			switch (endTime) {
			case 1:
				startTime = d - (24 * (3600 * 1000));
				break;
			case 7:
				startTime = d - (7 * (24 * (3600 * 1000)));
				break;
			case 30:
				startTime = d - (30 * (24 * (3600 * 1000)));
				break;
			case 90:
				startTime = d - (90 * (24 * (3600 * 1000)));
				break;
			case 180:
				startTime = d - (180 * (24 * (3600 * 1000)));
				break;
			case 360:
				startTime = d - (360 * (24 * (3600 * 1000)));
				break;
			}
			var curr = new Date(d);
			endTime = (curr.getMonth() + 1) + "/" + curr.getDate() + "/"
					+ curr.getFullYear() + " " + (curr.getHours() + 1) + ":"
					+ curr.getMinutes() + ":" + curr.getSeconds();
			curr = new Date(d);
			startTime = (curr.getMonth() + 1) + "/" + curr.getDate() + "/"
					+ curr.getFullYear() + " " + curr.getHours() + ":"
					+ curr.getMinutes() + ":" + curr.getSeconds();

		} else {
			endTime = document.forms[1].enddate.value;
			startTime = document.forms[1].stdate.value;
			if (endTime < startTime) {
				jAlert("End Date/Time should be greater than start Date/Time",
						"Invalid action");
				$("#popup_container").css("z-index", "9999999");
				return;
			}
			var todayDate = (new Date()).getTime();
			var endDate = new Date(endTime);
			if (endDate.getTime() > todayDate) {
				jAlert("End Date/Time cannot be greater than current time",
						"Invalid action");
				$("#popup_container").css("z-index", "9999999");
				return;
			}
			;
		}
		if (title != "") {
			if (document.getElementById('html').checked
					|| document.getElementById('pdf').checked
					|| document.getElementById('xls').checked) {
				var format;
				if (document.getElementById('html').checked)
					format = 0;
				else if (document.getElementById('pdf').checked)
					format = 1;
				else if (document.getElementById('xls').checked)
					format = 3;
				if (Reports.findreportType().length > 0) {
					// console.log('in if findreportType');
					RemoteManager.viewGeneralReport(Reports.findreportType(),
							format, title, startTime, endTime,
							Reports.viewReturn);
				} else {
					RemoteManager.viewNodeReport(document
							.getElementById('hostName1').value, format, title,
							startTime, endTime, Reports.viewReturn);
				}
			} else {
				document.getElementById('msg_td_1').innerHTML = "* No Format Selected <br>";
			}
		} else {
			document.getElementById('msg_td_1').innerHTML += "* Please insert Title for Report<br>";
		}
	},

	viewReturn : function(path) {
		console.log('path', path);

		if (path != null) {
			// window.open(path, 'report', 'width:500px;height:500px;');
			var open = window.open(path, 'report', 'width:500px;height:500px;');
			if (open == null || typeof (open) == 'undefined')
				jAlert(
						"Turn off your pop-up blocker!\n We try to open the following url:\n "
								+ path, "Note");
			$("#popup_container").css("z-index", "9999999999");
			return;

		} else {
			jAlert('Some Error Occured');
			$("#popup_container").css("z-index", "9999999999");
		}

	},

	viewReport : function() {
		Util.addLightbox("generateReport", "resources/generate_report.html",
				null, null);
	},

	setButtonEnabled : function() {
		Reports.findStatus();
		var reports = Reports.findreportType();
		if ((document.getElementById('nndetail').checked
				|| document.getElementById('dndetail').checked
				|| document.getElementById('rmDetail').checked || document
				.getElementById('nmDetail').checked)
				&& reports.length > 0) {
			document.forms[0].view.disabled = true;
			document.forms[0].schedule.disabled = true;
			document.forms[0].email.disabled = true;
		} else if ((document.getElementById('nndetail').checked
				|| document.getElementById('dndetail').checked
				|| document.getElementById('rmDetail').checked || document
				.getElementById('nmDetail').checked)
				&& reports.length == 0) {
			if ((document.getElementById('nndetail').checked
					|| document.getElementById('dndetail').checked
					|| document.getElementById('rmDetail').checked || document
					.getElementById('nmDetail').checked))
				document.forms[0].view.disabled = false;
			else
				document.forms[0].view.disabled = true;
			document.forms[0].schedule.disabled = false;
			document.forms[0].email.disabled = false;
		} else if (reports.length > 0) {
			if (reports.length > 1) {
				document.forms[0].view.disabled = true;
			} else {
				document.forms[0].view.disabled = false;
			}
			document.forms[0].schedule.disabled = false;
			document.forms[0].email.disabled = false;
		} else {
			document.forms[0].view.disabled = true;
			document.forms[0].schedule.disabled = true;
			document.forms[0].email.disabled = true;
		}

	},

	setButtonDisable : function() {
		document.forms[1].stdate.value = "";
		document.forms[1].enddate.value = "";
		document.forms[1].title.value = "";
		// document.forms[1].stdate.disabled=true;
		// document.forms[1].enddate.disabled=true;
	},

	ready : function() {
		if (document.getElementById('reports_list_table_div') == undefined
				|| document.getElementById('reports_list_table_div') == null) {
			return;
		}

		$("#reports_list_table").remove();
		$('#reports_list_table_div').html(
				'<table id="reports_list_table"></table>');

		document.forms[0].view.disabled = true;
		document.forms[0].schedule.disabled = true;
		document.forms[0].email.disabled = true;
		var tableList = [];
		tableList
				.push([
						'<input type="checkbox" id="hdfs" name="nodeId" onClick="javascript:Reports.setButtonEnabled();" /><span id="hdfs_span">HDFS Summary</span>',
						'System summary of the HDFS cluster' ]);
		tableList
				.push([
						'<input type="checkbox" id="mapReduce" name="nodeId" onClick="javascript:Reports.setButtonEnabled();" /><span id="mapReduce_span">MapReduce Summary</span>',
						'System summary of the MapReduce view' ]);

		tableList
				.push([
						'<input type="checkbox" id="namenode" name="nodeId" onClick="javascript:Reports.setButtonEnabled();" /><span id="namenode_span">NameNode Summary</span>',
						'I/O summary on NameNode' ]);
		tableList
				.push([
						'<input type="checkbox" id="datanode" name="nodeId" onClick="javascript:Reports.setButtonEnabled();" /><span id="datanode_span">DataNode Summary</span>',
						'Storage usage summary on DataNode' ]);

		tableList
				.push([
						'<input type="checkbox" id="resourcemanager" name="nodeId" onClick="javascript:Reports.setButtonEnabled();" /><span id="resourcemanager_span">ResourceManager Summary</span>',
						'Applications summary on ResourceManager' ]);
		tableList
				.push([
						'<input type="checkbox" id="nodemanager" name="nodeId" onClick="javascript:Reports.setButtonEnabled();" /><span id="nodemanager_span">NodeManager Summary</span>',
						'Containers summary on NodeManager' ]);

		tableList
				.push([
						'<input type="checkbox" id="alert" name="nodeId" onClick="javascript:Reports.setButtonEnabled();" /><span id="alert_span">Alert Summary</span>',
						'Alerts generated when rules defined by user violates' ]);
		tableList
				.push([
						'<input type="checkbox" id="sforecast" name="nodeId" onClick="javascript:Reports.setButtonEnabled();" /><span id="sforecast_span">Storage Forecast</span>',
						'Storage Forecast Report' ]);
		tableList
				.push([
						'<input type="checkbox" id="nndetail" name="nodeId" onClick="javascript:Reports.setButtonEnabled();" /><span id="nndetail_span">NameNode Detail</span>',
						'NameNode Detailed Report' ]);
		tableList
				.push([
						'<input type="checkbox" id="dndetail" name="nodeId" onClick="javascript:Reports.setButtonEnabled();" /><span id="dndetail_span">DataNode Detail</span>',
						'DataNode Detailed Report' ]);

		tableList
				.push([
						'<input type="checkbox" id="rmDetail" name="nodeId" onClick="javascript:Reports.setButtonEnabled();" /><span id="rmDetail_span">ResourceManager Detail</span>',
						'ResourceManager Detailed Report' ]);
		tableList
				.push([
						'<input type="checkbox" id="nmDetail" name="nodeId" onClick="javascript:Reports.setButtonEnabled();" /><span id="nmDetail_span">NodeManager Detail</span>',
						'NodeManager Detailed Report' ]);

		$('#reports_list_table').dataTable({
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : true,
			"bDestroy" : true,
			"bInfo" : false,
			"bAutoWidth" : true,
			"aaSorting" : [ [ 1, "desc" ] ],
			"aaData" : tableList,
			"aoColumns" : [ {
				"sTitle" : "Report Type"
			}, {
				"sTitle" : "Description"
			} ]
		});
	},

	closeBox : function(isRefresh) {
		Util.removeLightbox("generateReport");
		if (isRefresh)
			Navbar.refreshView();
	},

	nextPage : function() {
		var flag = Reports.validateEmailpage();
		if (flag) {
			document.getElementById('msg_td_2').innerHTML = "";
			$('#emailReportDiv_1').hide();
			$('#emailReportDiv_2').show();
			RemoteManager.getUserDetails(Reports.addUser);
		}
	},

	validateEmailpage : function() {
		if (!Reports.hostAvailable) {
			document.getElementById('msg_td_1').innerHTML = "* No Host Available <br>";
			return;
		} else if (!Reports.emailEnabled) {
			document.getElementById('msg_td_1').innerHTML = "* You have not configured Email notification. Please configure Email notification and return to this wizard.";
			return;
		}

		var flag = true;
		document.getElementById('msg_td_1').innerHTML = "";
		var title = document.forms[1].title.value;
		var start = document.forms[1].stdate.value;
		var end = document.forms[1].enddate.value;
		if (title == "") {
			flag = false;
			document.getElementById('msg_td_1').innerHTML += "* Title Not Provided<br>";
		}
		if (start == "") {
			flag = false;
			document.getElementById('msg_td_1').innerHTML += "* Please Enter Start Date<br>";
		}
		if (end == "") {
			flag = false;
			document.getElementById('msg_td_1').innerHTML += "* Please Enter End Date<br>";
		}
		if (Reports.findExportType().length == 0) {
			flag = false;
			document.getElementById('msg_td_1').innerHTML += "* Format Not Selected<br>";
		}
		return flag;
	},

	moveSelectedOptions : function(from, to) {

		var source = document.getElementById(from).getElementsByTagName(
				"option");
		for (var i = 0; i < source.length; i++) {
			if (source[i].selected) {
				$('#' + to).append(
						'<option value="' + source[i].value + '">'
								+ Reports.findUser(source[i].value)
								+ '</option>');

			}
		}
		$("#" + from + " option:selected").remove();
	},

	findUser : function(val) {
		for (var i = 0; i < this.userCache.length; i++) {
			var user = this.userCache[i];
			if (user.id == val)
				return user.firstName + ' ' + user.lastName;
		}
	},

	findExportType : function() {
		var exportFormatList = [];
		if (document.forms[1].html.checked)
			exportFormatList.push(0);
		if (document.forms[1].pdf.checked)
			exportFormatList.push(1);
		if (document.forms[1].xls.checked)
			exportFormatList.push(3);
		return exportFormatList;
	},

	checkNotificationSettings : function(nbean) {
		Reports.emailEnabled = nbean.emailEnabled;
		Reports.logEnabled = nbean.logEnabled;
	},

	email : function() {
		document.getElementById('msg_td_2').innerHTML = "";
		var selectedUser = document.getElementById('selected')
				.getElementsByTagName("option");
		var users = [];
		for (var i = 0; i < selectedUser.length; i++) {
			users.push(selectedUser[i].value);
		}
		if (users.length > 0) {
			document.forms[1].email.disabled = true;
			var endTime = '';
			var startTime = ''
			if (document.getElementById('rTime').selected) {
				endTime = document.getElementById('reportInterval').value;
				var d = (new Date()).getTime;
				switch (endTime) {
				case 1:
					startTime = d - (24 * (3600 * 1000));
					break;
				case 7:
					startTime = d - (7 * (24 * (3600 * 1000)));
					break;
				case 30:
					startTime = d - (30 * (24 * (3600 * 1000)));
					break;
				case 90:
					startTime = d - (90 * (24 * (3600 * 1000)));
					break;
				case 180:
					startTime = d - (180 * (24 * (3600 * 1000)));
					break;
				case 360:
					startTime = d - (360 * (24 * (3600 * 1000)));
					break;
				}
				var curr = new Date(d)
				endTime = curr.getMonth() + "/" + curr.getDate() + "/"
						+ curr.getFullYear() + " " + curr.getHours() + ":"
						+ curr.getMinutes() + ":" + curr.getSeconds();
				curr = new Date(startTime);
				startTime = curr.getMonth() + "/" + curr.getDate() + "/"
						+ curr.getFullYear() + " " + curr.getHours() + ":"
						+ curr.getMinutes() + ":" + curr.getSeconds();
			} else {
				endTime = document.forms[1].enddate.value;
				startTime = document.forms[1].stdate.value;
			}
			if (document.getElementById('nndetail').checked
					|| document.getElementById('dndetail').checked
					|| document.getElementById('rmDetail').checked
					|| document.getElementById('nmDetail').checked) {
				Reports.emailStatus('node', users, startTime, endTime);
			} else {
				Reports.emailStatus('general', users, startTime, endTime);
			}
		} else {
			document.getElementById('msg_td_2').innerHTML += "* Users Not Selected<br>";
		}
	},

	emailStatus : function(reportType, users, startTime, endTime) {

		this.reportType = reportType;
		this.users = users;
		this.startTime = startTime;
		this.endTime = endTime;
		Util.addLightbox("reportStatus", "resources/emailStatus.html", null,
				null);
		$("#popup_container").css("z-index", "999999999");

	},

	sendEmail : function(reportType, users, startTime, endTime) {
		Reports.findStatus();
		if (reportType == 'node') {
			RemoteManager.mailNodeReport(
					document.getElementById('hostName1').value, Reports
							.findExportType(), users,
					document.forms[1].title.value, startTime, endTime,
					Reports.emailReturn);
		} else {
			RemoteManager.mailGeneralReport(Reports.findreportType(), Reports
					.findExportType(), users, document.forms[1].title.value,
					startTime, endTime, Reports.emailReturn);
		}
	},

	emailReturn : function(dwrResponse) {
		if (dwrResponse.taskSuccess) {
			document.getElementById('processingImg').style.display = 'none';
			document.getElementById('successImg').style.display = '';
			document.getElementById('popMsg').innerHTML = dwrResponse.responseMessage;
		} else {
			document.getElementById('processingImg').style.display = 'none';
			document.getElementById('failImg').style.display = '';
			document.getElementById('popMsg').innerHTML = dwrResponse.responseMessage;
			var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
			document.getElementById('log_div').innerHTML = log;
			document.getElementById('log_div').style.display = "block";
		}
		document.getElementById('okPopUp').disabled = false;
	},

	closePopUpBox : function() {
		Util.removeLightbox("reportStatus");
		Reports.closeBox(true);

	},

	moveAllOptions : function(from, to) {

		var source = document.getElementById(from).getElementsByTagName(
				"option");
		for (var i = 0; i < source.length; i++) {
			$('#' + to).append(
					'<option value="' + source[i].value + '">'
							+ Reports.findUser(source[i].value) + '</option>');
		}
		$('#' + from).children().remove();
	},

	backPage : function() {
		$('#emailReportDiv_2').hide();
		$('#emailReportDiv_1').show();
	},

	schedule : function() {
		Util.addLightbox("generateReport", "resources/scheduleReports.html",
				null, null);
	},

	nextScheduleStep : function(selectedDiv) {
		var flag = true;
		if (Reports.currentSchedulePage < selectedDiv) {
			if (Reports.hostAvailable) {
				if (!Reports.emailEnabled && !Reports.logEnabled) {
					document.getElementById('msg_td_1').innerHTML = "* Notification Settings not set.";
					return;
				} else if (selectedDiv == 3 && !Reports.emailEnabled) {
					document.getElementById('msg_td_2').innerHTML = "* You have not configured Email notification. Please configure Email notification and return to this wizard.";
					return;
				} else {

					flag = Reports.validateSchedule();
				}
			} else {
				document.getElementById('msg_td_1').innerHTML = "* No Host Available <br>";
				return;
			}
		}
		if (flag) {
			switch (selectedDiv) {
			case 1: {
				RemoteManager
						.getNotificationSettings(Reports.checkNotificationSettings);
				RemoteManager.getAllHostDetails(Reports.fillHost);
				$('#reportdiv1').show();
				$('#reportdiv2').hide();
				$('#reportdiv3').hide();
				break;
			}
			case 2: {
				Reports.checkForScheduleID();
				break;
			}
			case 3: {
				RemoteManager.getUserDetails(Reports.addUser);
				$('#reportdiv1').hide();
				$('#reportdiv2').hide();
				$('#reportdiv3').show();
				$('input[value="Close"]').hide();
				break;
			}
			}
			Reports.currentSchedulePage = selectedDiv;
		}
		if (selectedDiv == 1) {

			if (Reports.isFromScheduler) {
				RemoteManager.getAllNodeIdWithIp(Reports.fillScroll);
				if (($('#reportType').val() == 'nndetail')
						|| ($('#reportType').val() == 'dndetail')
						|| ($('#reportType').val() == 'rmDetail')
						|| ($('#reportType').val() == 'nmDetail')) {
					document.getElementById('hostScroll').style.display = '';
				} else {
					document.getElementById('hostScroll').style.display = 'none';
				}
			} else {

				if (document.getElementById('nndetail').checked
						|| document.getElementById('dndetail').checked
						|| document.getElementById('rmDetail').checked
						|| document.getElementById('nmDetail').checked)
					RemoteManager.getAllNodeIdWithIp(Reports.fillScroll);
				else
					document.getElementById('hostScroll').style.display = 'none';
			}

		}
	},

	checkForScheduleID : function() {
		if (document.getElementById('schedID').value != "")
			RemoteManager.checkSysReportScheduleId(document
					.getElementById('schedID').value, Reports.checkResp);
		else {
			document.getElementById('msg_td_1').innerHTML += "* Schedule Id Required<br>";
		}
	},

	validateSchedule : function() {
		var flag = true;
		switch (Reports.currentSchedulePage) {
		case 1: {
			document.getElementById('msg_td_1').innerHTML = "";
			var reportDate = document.forms[1].reportDate.value;
			if (reportDate == "") {
				document.getElementById('msg_td_1').innerHTML += "* Date & Time Not Provided<br>";
				flag = false;
			}
			var exportFormatList = Reports.findExportType();
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

	checkResp : function(flag) {
		if (flag) {
			jAlert('ScheduleId already Taken');
		} else {
			$('#reportdiv1').hide();
			$('#reportdiv2').show();
			$('#reportdiv3').hide();
		}
		$("#popup_container").css("z-index", "99999999");
	},

	addSchedule : function() {
		Reports.findStatus();
		var selectedUser = document.getElementById('selected')
				.getElementsByTagName("option");
		var users = [];
		for (var i = 0; i < selectedUser.length; i++) {
			users.push(selectedUser[i].value);
		}
		if (users.length > 0) {
			var reportsType = Reports.findreportType();
			reportType = $('#reportType').val();

			if (document.getElementById('nndetail').checked)
				reportsType.push(8);
			if (document.getElementById('dndetail').checked)
				reportsType.push(9);
			if (document.getElementById('rmDetail').checked)
				reportsType.push(12);
			if (document.getElementById('nmDetail').checked)
				reportsType.push(13);

			var nodeID = -1;
			if (document.getElementById('nndetail').checked
					|| document.getElementById('dndetail').checked
					|| document.getElementById('rmDetail').checked
					|| document.getElementById('nmDetail').checked) {
				nodeID = document.getElementById('hostName1').value;
			}
			RemoteManager
					.scheduleJob(
							document.forms[1].interval.value,
							document.forms[1].reportDate.value,
							Reports.findExportType(),
							document.getElementById('notificationType').value,
							document
									.getElementById('alertRaisedNotificationMessage').value,
							users, reportsType, nodeID, document
									.getElementById('schedID').value,
							Reports.scheduleReturn);
		} else {
			document.getElementById('msg_td_3').innerHTML += "* Users Not Selected<br>";
		}
	},

	scheduleReturn : function(flag) {
		if (flag) {
			jAlert('Report scheduled successfully', Reports.closeBox(true));
		} else {
			jAlert('Some Error Occured');
		}
		$("#popup_container").css("z-index", "99999999");
	},

	clickeRadioButton : function(id) {
		if (id == 'rTime') {
			document.getElementById('rTime').checked = true;
			document.getElementById('aTime').checked = false
			$("#startDate").css('display', 'none');
			$("#endDate").css('display', 'none');
			$("#relativeTimeRow").css('display', '');
			Reports.intervalChanged();
		}
		if (id == 'aTime') {
			document.getElementById('reportInterval').selectedIndex = 0;
			Reports.intervalChanged();
			document.getElementById('aTime').checked = true;
			document.getElementById('rTime').checked = false
			$("#relativeTimeRow").css('display', 'none');
			$("#startDate").css('display', '');
			$("#endDate").css('display', '');
		}
	},

	intervalChanged : function() {
		var days = document.getElementById('reportInterval').value;
		var d = new Date();
		var currentDateinMillis = d.getTime();
		var daysinMillis = 86400000 * parseFloat(days);
		var startDateinMillis = Math.abs(currentDateinMillis - daysinMillis);
		var startDate = new Date(startDateinMillis);
		document.getElementById('report.stdate').value = (startDate.getMonth() + 1)
				+ "/"
				+ startDate.getDate()
				+ "/"
				+ startDate.getFullYear()
				+ " "
				+ startDate.getHours()
				+ ":"
				+ startDate.getMinutes()
				+ ":" + startDate.getSeconds();
		document.getElementById('report.enddate').value = (d.getMonth() + 1)
				+ "/" + d.getDate() + "/" + d.getFullYear() + " "
				+ d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds();
	},

	generateReportReady : function() {
		document.getElementById('pdf').checked = true;
		Reports.clickeRadioButton('rTime');
		Reports.intervalChanged();
		var form = document.getElementById('deleteReportForm');
		for (var i = 0; i < form.elements.length; i++) {
			var e = form.elements[i];
			if (e.type == 'checkbox') {
				if (e.checked) {
					var id = e.id + '_span';
					var txt = document.getElementById(id).innerHTML;
					document.getElementById('headerspan').innerHTML = txt;
					document.getElementById('title').value = txt;
				}
			}
		}
		if (document.getElementById('nndetail').checked
				|| document.getElementById('dndetail').checked
				|| document.getElementById('rmDetail').checked
				|| document.getElementById('nmDetail').checked) {
			if (document.getElementById('nndetail').checked
					|| document.getElementById('dndetail').checked) {
				RemoteManager.getAllNodeIdWithIp(Reports.fillScroll);
			} else if (document.getElementById('rmDetail').checked) {
				RemoteManager.getAllNodeIdWithIp(Reports.fillScroll);
			} else if (document.getElementById('nmDetail').checked) {
				RemoteManager.getAllNodeManagerIdWithIp(Reports.fillScroll);
			}
		} else
			document.getElementById('hostScroll').style.display = 'none';
		RemoteManager.getAllHostDetails(Reports.fillHost);

	},

	viewNotification : function() {
		Navbar.changeTab('Notifications', 'admin', 'reportnotifications');
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

	logSave : function() {
		if ($('#notificationType').val() == 'Log') {
			document.getElementById('EmailNext').disabled = true;
			document.getElementById('logSave').disabled = false;
		} else {
			document.getElementById('logSave').disabled = true;
			document.getElementById('EmailNext').disabled = false;

		}
	},

	saveLog : function() {
		var flag = Reports.validateSchedule();
		if (!Reports.logEnabled) {
			document.getElementById('msg_td_2').innerHTML = "* Log Notification Settings not set.";
			return;
		}
		if (flag) {
			Reports.findStatus();
			var reportScheduleForm = document.getElementById('scheduleReport');
			var reportsType = Reports.findreportType();
			if (document.getElementById('nndetail').checked)
				reportsType.push(8);
			if (document.getElementById('dndetail').checked)
				reportsType.push(9);
			if (document.getElementById('rmDetail').checked)
				reportsType.push(12);
			if (document.getElementById('nmDetail').checked)
				reportsType.push(13);
			var nodeID = -1;
			if (reportsType.indexOf(8) > -1 || reportsType.indexOf(9) > -1
					|| reportsType.indexOf(12) > -1
					|| reportsType.indexOf(13) > -1) {
				nodeID = parseInt(document.getElementById('hostName1').value);
			}
			RemoteManager
					.scheduleJob(
							reportScheduleForm.interval.value,
							reportScheduleForm.reportDate.value,
							Reports.findExportType(reportScheduleForm),
							document.getElementById('notificationType').value,
							document
									.getElementById('alertRaisedNotificationMessage').value,
							null, reportsType, nodeID, document
									.getElementById('schedID').value,
							Reports.scheduleReturn);

		}
	},

	validateForScheduleID : function() {
		if (document.getElementById('schedID').value != "") {
			RemoteManager
					.checkSysReportScheduleId(document
							.getElementById('schedID').value,
							Reports.checkValidateResp);
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
			Reports.findStatus();
			var reportScheduleForm = document.getElementById('scheduleReport');
			var reportsType = Reports.findreportType();
			if (document.getElementById('nndetail').checked)
				reportsType.push(8);
			if (document.getElementById('dndetail').checked)
				reportsType.push(9);
			if (document.getElementById('rmDetail').checked)
				reportsType.push(12);
			if (document.getElementById('nmDetail').checked)
				reportsType.push(13);
			var nodeID = -1;
			if (reportsType.indexOf(8) > -1 || reportsType.indexOf(9) > -1
					|| reportsType.indexOf(12) > -1
					|| reportsType.indexOf(13) > -1) {
				nodeID = parseInt(document.getElementById('hostName1').value);
			}
			RemoteManager.scheduleJobWithoutNotification(
					reportScheduleForm.interval.value,
					reportScheduleForm.reportDate.value, Reports
							.findExportType(reportScheduleForm), reportsType,
					nodeID, document.getElementById('schedID').value,
					Reports.scheduleReturn);
		}
	},

	scheduleWithoutNotify : function() {
		Reports.currentSchedulePage = 1;
		var flag = Reports.validateSchedule();
		if (flag) {
			Reports.validateForScheduleID();
		}
	}
};