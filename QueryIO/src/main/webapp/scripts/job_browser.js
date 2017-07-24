JB = {

	isAdhoc : false,

	appsCache : null,
	//jobsCache : null,
	jobsCacheAdhoc : null,

	selectedApp : [],
	selectedJob : [],
	selectedJobAdhoc : [],

	jqGridColumnDetails : [],
	allJobsArray : [],
	total : 0,

	appSelect : 0,
	jobSelect : 0,
	jobSelectAdhoc : 0,

	status : null,

	jobName : null,
	appName : null,

	jarFile : null,
	jarFileText : null,

	selectedNN : null,
	selectedRM : null,

	isJobJarUpdated : false,
	isJarUpdated : false,
	isChangeShown : true,

	libJarsArray : [],
	nativeFilesArray : [],
	updatedLibJarArray : {},
	updatedNativeFileArray : {},

	mainClass : null,
	arguments : null,
	maxMem : null,
	minMem : null,
	currentOperation : null,
	counter : 1,
	nativeCounter : 1,

	rowCounter : 1,
	rowNativeCounter : 1,

	fileName : [],
	userCache : [],
	currentPage : 1,

	containerObject : '',

	isFormLoaded : false,
	startResponse : [],
	emailEnabled : false,
	logEnabled : false,
	selectedJobsList : [],
	$tabs : $("#jobTabs").tabs(),
	timer : [],
	isSetTimer : false,

	pathPattern : null,
	sourcePath : null,

	ready : function() {
		$("#refreshViewButton").attr('disabled', 'disabled');
		JB.$tabs = $("#jobTabs").tabs();
		if (Navbar.currentJobTabSelected == '')
			Navbar.currentJobTabSelected = 'standardJobHead';
		$('#jobTabs').tabs({
			select : function(event, ui) {
				if (Navbar.isSetButtonWidth) {
					Navbar.isSetButtonWidth = false;
					Navbar.setButtonWidth();
				}
				Navbar.currentJobTabSelected = ui.panel.id;
			}
		});
		JB.$tabs.tabs("select", Navbar.currentJobTabSelected);
		JB.updatedLibJarArray = new Object();
		JB.updatedNativeFileArray = new Object();
		JB.populateJobsTable();
		// RemoteManager.getAllJobsList();
		RemoteManager.getAllAdhocJobsList(JB.populateAdhocJobsTable);
		RemoteManager.getAllApplicationsSummary(JB.populateApplicationsTable);
	},
	showJobDetail : function(url, appId) {

		url = "http://" + url + '/proxy/' + appId
		window.open(url);

	},
	populateApplicationsTable : function(sTable) {
		$('#JobBrowserApps_summary_table').html('');
		JB.isSetTimer = false;
		JB.appsCache = new Array();
		var columns = sTable.colNames;
		var rows = sTable.rows;
		var dataColumn = [];
		dataColumn
				.push({
					"sTitle" : '<input type="checkBox" id="selectAllApps" onClick="javascript:JB.selectAllJB(this.id);">'
				});
		for (var i = 0; i < columns.length; i++) {
			dataColumn.push({
				"sTitle" : columns[i]
			});
		}
		dataColumn.push({
			"sTitle" : "Container Logs"
		});

		var dataRow = [];
		for (var i = 0; i < rows.length; i++) {
			var rowDetail = rows[i];
			JB.appsCache[i] = rows[i];
			var rowData = [];
			rowData
					.push([ '<input onClick="javascript:JB.checkButtonApp(this.id);" type="checkbox" id="appNode'
							+ i + '" value="' + rowDetail[6] + '">' ]); // rowDetail[6]
			// For
			// "State"
			for (var j = 0; j < rowDetail.length - 1; j++) {
				if (j == 0 && rowDetail[6] == "Running") {
					rowData.push('<a href="javascript:JB.showJobDetail(\''
							+ rowDetail[rowDetail.length - 1] + '\',\''
							+ rowDetail[0] + '\');">' + rowDetail[0] + '</a>');
					continue;
				}
				rowData.push(rowDetail[j]);
			}
			if (rowDetail[7] == "Undefined") {
				JB.isSetTimer = true;
			}
			rowData.push('<a href="javascript:JB.showContainerLogs(\''
					+ rowDetail[0] + '\');">View Container Logs</a>');
			dataRow.push(rowData);
		}

		$('#JobBrowserApps_summary_table').dataTable({
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : true,
			"bDestroy" : true,
			"bInfo" : false,
			"bAutoWidth" : false,
			"aaSorting" : [ [ 1, "desc" ] ],
			"aaData" : dataRow,
			"aoColumns" : dataColumn,
			"fnDrawCallback" : function(oSettings) {
				$("#refreshViewButton").removeAttr('disabled');
			}
		});

		if (sTable.rows.length == 0 || sTable == null || sTable == undefined)
			$('#selectAllApps').attr('disabled', true);
		else
			$('#selectAllApps').attr('disabled', false);

		JB.updateExecutionHistory();

	},

	populateJobsTable : function(sTable) {

		$('#JobBrowser_summary_table')
				.dataTable(
						{
							// "bPaginate": false,
							// "bLengthChange": false,
							// "bFilter": false,
							// "bSort": true,
							// "bInfo": false,
							// "bAutoWidth": false,
							// "aaSorting": [[ 1, "desc" ]],
							// "aaData": dataRow,
							// "aoColumns": dataColumn
							"sScrollX" : "100%",
							"bPaginate" : true,
							"bLengthChange" : true,
							"aLengthMenu" : [ 10, 25, 50, 100 ],
							"sPaginationType" : "full_numbers",
							"bFilter" : false,
							"bDestroy" : true,
							"bSort" : true,
							"bInfo" : true,
							"bAutoWidth" : true,
							"serverSide" : true,
							"searching" : true,
							"aoColumnDefs" : [ {
								"bSortable" : false,
								"aTargets" : [ 0 ]
							} ],
							"fnServerData" : function(sSource, aoData,
									fnCallback, oSettings) {
								RemoteManager.getAllJobsList(JSON
										.stringify(aoData), {
									async : false,
									callback : function(result) {
										fnCallback(JB.fillJobsTable(result));
										$(window).trigger('resize');
									}
								});

							},
							"aoColumns" : [
									{
										"sTitle" : '<input type="checkBox" id="selectAllJobs" onClick="javascript:JB.selectAllJB(this.id);">'
									// , "bSortable" : false});
									// "sTitle" : '<input type="checkbox"
									// value="" id="selectAll"
									// onclick="javascript:AHQ.selectAllAdHocRow(this.id)">'
									}, {
										"sTitle" : "Job Name"
									}, {
										"sTitle" : "Main Class"
									}, {
										"sTitle" : "Arguments"
									}, {
										"sTitle" : "Name Node"
									}, {
										"sTitle" : "Resource Manager"
									}, {
										"sTitle" : "Jar File"
									}, {
										"sTitle" : "Lib Jar(s)"
									}, {
										"sTitle" : "Native File(s)"
									} ]
						});
		// });

		// disabled the checkAll button when no data in the list
		// JobBrowser_summary_table
		// if(sTable.rows.length == 0 || sTable == null || sTable == undefined)
		if ($('#JobBrowser_summary_table tbody tr td').hasClass(
				'dataTables_empty'))
			$('#selectAllJobs').attr('disabled', true);
		else
			$('#selectAllJobs').attr('disabled', false);

		$('#JobBrowser_summary_table_length').css('margin-top', 7 + 'px');
		$('#JobBrowser_summary_table_length').css('margin-bottom', 7 + 'px');
		$('#JobBrowser_summary_table_filter').css('margin-top', 7 + 'px');
		$('#JobBrowser_summary_table_filter').css('margin-bottom', 7 + 'px');
		
		
		$('#standardJobLink').click(function() {
			$(window).trigger('resize');
		});
	},

	fillJobsTable : function(jobsDataResult) {
		var tableList = new Array();
		//JB.jobsCache= new Array();
		JB.total = 0;
		JB.allJobsArray.splice(0, JB.allJobsArray.length);
		var jobsData = jobsDataResult["data"];
		if ((jobsData != null) && (jobsData.length > 0)) {
			JB.jqGridColumnDetails.splice(0, JB.jqGridColumnDetails.length);
			JB.total = jobsData.length;
			for (var i = 0; i < jobsData.length; i++) {
				var jobQuery = jobsData[i];
				var jobName = jobQuery[0] // JOBNAME;
				JB.allJobsArray.push(jobName);
				var check = '<input type="checkBox" id="' + jobName
						+ '"  onClick="javascript:JB.checkButtonJob(this.id)">';
				var namenodeId = jobQuery[1] // namenodeId
				var namenodeIdSpan = '<span id="namenode_' + jobName + '">'
						+ namenodeId + '</span>';
				var rmId = jobQuery[2] // ResourceManagerId;
				var jarFile = jobQuery[3] // Jarfile;
				var libJar = jobQuery[4] // LIBJar;
				var nativeFiles = jobQuery[5] // files;
				var className = jobQuery[6] // classname;
				var arguments = jobQuery[7] // arguments;

				tableList.push([ check, jobName, className, arguments,
						namenodeIdSpan, rmId, jarFile, libJar, nativeFiles ]);
			}
			jobsDataResult["data"] = tableList;
			//If required, JB.jobsCache value can be obtained from here
			//JB.jobsCache=tableList;
		}
		return jobsDataResult;

		// JB.jobsCache = new Array();
		// var columns = sTable.colNames;
		// var rows = sTable.rows;
		// var dataColumn = [];
		// dataColumn.push({"sTitle":'<input type="checkBox" id="selectAllJobs"
		// onClick="javascript:JB.selectAllJB(this.id);">', "bSortable" :
		// false});
		// for(var i=0;i<columns.length;i++)
		// {
		// dataColumn.push({"sTitle":columns[i]});
		// }
		// var dataRow = [];
		// for(var i=0;i<rows.length;i++){
		// var rowDetail = rows[i];
		// JB.jobsCache[i]=rows[i];
		// var rowData = [];
		// rowData.push(['<input
		// onClick="javascript:JB.checkButtonJob(this.id);" type="checkbox"
		// id="jobNode'+i+'">']);
		// for(var j=0;j<rowDetail.length;j++){
		// rowData.push(rowDetail[j]);
		// }
		// dataRow.push(rowData);
		// }
	},

	populateAdhocJobsTable : function(sTable) {
		JB.jobsCacheAdhoc = new Array();
		var columns = sTable.colNames;
		var rows = sTable.rows;
		var dataColumn = [];
		dataColumn
				.push({
					"sTitle" : '<input type="checkBox" id="selectAllJobsAdhoc" onClick="javascript:JB.selectAllJB(this.id);">',
					"bSortable" : false
				});
		for (var i = 0; i < columns.length; i++) {
			dataColumn.push({
				"sTitle" : columns[i]
			});
		}
		var dataRow = [];
		for (var i = 0; i < rows.length; i++) {
			var rowDetail = rows[i];
			JB.jobsCacheAdhoc[i] = rows[i];
			var rowData = [];
			rowData
					.push([ '<input onClick="javascript:JB.checkButtonJobAdhoc(this.id);" type="checkbox" id="jobNodeAdhoc'
							+ i + '">' ]);
			for (var j = 0; j < rowDetail.length; j++) {
				rowData.push(rowDetail[j]);
			}
			dataRow.push(rowData);
		}
		$('#JobBrowserAdhoc_summary_table').dataTable({
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : true,
			"bInfo" : false,
			"bAutoWidth" : false,
			"aaSorting" : [ [ 1, "desc" ] ],
			"aaData" : dataRow,
			"aoColumns" : dataColumn,
			"fnDrawCallback" : function(oSettings) {
				$("#refreshViewButton").removeAttr('disabled');
			}
		});

		// disabled the checkAll button when no data in the list
		if (sTable.rows.length == 0 || sTable == null || sTable == undefined)
			$('#selectAllJobsAdhoc').attr('disabled', true);
		else
			$('#selectAllJobsAdhoc').attr('disabled', false);
	},

	selectAllJB : function(id) {
		var flag = document.getElementById(id).checked;

		if (id == "selectAllApps") {
			JB.selectedApp.splice(0, JB.selectedApp.length);
			for (var i = 0; i < JB.appsCache.length; i++) {
				document.getElementById('appNode' + i).checked = flag;
				if (flag)
					JB.selectedApp.push('appNode' + i);
			}
			if (flag) {
				JB.appSelect = JB.appsCache.length;
			} else {
				JB.appSelect = 0;
			}
		} else if (id == "selectAllJobs") {
			JB.selectedJob.splice(0, JB.selectedJob.length);
			// change
			for (var i = 0; i < JB.allJobsArray.length; i++) {
				var jobname = JB.allJobsArray[i];
				document.getElementById(jobname).checked = flag;
				if (flag)
					JB.selectedJob.push(jobname);
			}
			if (flag) {
				JB.jobSelect = JB.allJobsArray.length;
			} else {
				JB.jobSelect = 0;
			}
		} else {
			JB.selectedJobAdhoc.splice(0, JB.selectedJobAdhoc.length);
			for (var i = 0; i < JB.jobsCacheAdhoc.length; i++) {
				document.getElementById('jobNodeAdhoc' + i).checked = flag;
				if (flag)
					JB.selectedJobAdhoc.push('jobNodeAdhoc' + i);
			}
			if (flag) {
				JB.jobSelectAdhoc = JB.jobsCacheAdhoc.length;
			} else {
				JB.jobSelectAdhoc = 0;
			}
		}
		JB.toggleButton(id, flag);
	},

	toggleButton : function(id, value, parent) {
		if (id == "selectAllApps") {
			$('#stopButton').attr("disabled", true);
		} else if (id == "selectAllJobs") {
			$("#startButton").attr("disabled", true);
			$("#editButton").attr("disabled", true);
			if (JB.jobSelect == 0) {
				$("#deleteButton").attr("disabled", true);
				$("#scheduleButton").attr("disabled", true);
			} else {
				if (JB.jobSelect == 1) {
					$("#startButton").attr("disabled", !value);
					$("#editButton").attr("disabled", !value);
				}
				$("#deleteButton").attr("disabled", !value);
				$("#scheduleButton").attr("disabled", !value);
			}
		} else if (id == "selectAllJobsAdhoc") {
			$("#startButtonAdhoc").attr("disabled", true);
			$("#editButtonAdhoc").attr("disabled", true);
			if (JB.jobSelectAdhoc == 0) {
				$("#deleteButtonAdhoc").attr("disabled", true);
				$("#scheduleButtonAdhoc").attr("disabled", true);
			} else {
				if (JB.jobSelectAdhoc == 1) {
					$("#startButtonAdhoc").attr("disabled", !value);
					$("#editButtonAdhoc").attr("disabled", !value);
				}
				$("#deleteButtonAdhoc").attr("disabled", !value);
				$("#scheduleButtonAdhoc").attr("disabled", !value);
			}
		} else {
			if (parent == "selectAllApps") {
				if (value == false)
					$('#selectAllApps').attr("checked", false);

				if (JB.selectedApp.length == 1)
					$('#stopButton').attr("disabled", false);
				else
					$('#stopButton').attr("disabled", true);
			} else if (parent == "selectAllJobs") {
				if (value == false)
					$('#selectAllJobs').attr("checked", false);

				if (JB.selectedJob.length < 1) {
					$("#startButton").attr("disabled", true);
					$("#editButton").attr("disabled", true);
					$("#deleteButton").attr("disabled", true);
					$("#scheduleButton").attr("disabled", true);
				} else {
					if (JB.selectedJob.length == 1) {
						$("#startButton").attr("disabled", false);
						$("#editButton").attr("disabled", false);
						$("#scheduleButton").attr("disabled", false);
					} else {
						$("#startButton").attr("disabled", true);
						$("#editButton").attr("disabled", true);
					}
					$("#deleteButton").attr("disabled", false);
				}
			} else {
				if (value == false)
					$('#selectAllJobsAdhoc').attr("checked", false);

				if (JB.selectedJobAdhoc.length < 1) {
					$("#startButtonAdhoc").attr("disabled", true);
					$("#editButtonAdhoc").attr("disabled", true);
					$("#deleteButtonAdhoc").attr("disabled", true);
					$("#scheduleButtonAdhoc").attr("disabled", true);
				} else {
					if (JB.selectedJobAdhoc.length == 1) {
						$("#startButtonAdhoc").attr("disabled", false);
						$("#editButtonAdhoc").attr("disabled", false);
						$("#scheduleButtonAdhoc").attr("disabled", false);
					} else {
						$("#startButtonAdhoc").attr("disabled", true);
						$("#editButtonAdhoc").attr("disabled", true);
					}
					$("#deleteButtonAdhoc").attr("disabled", false);
				}
			}
		}
	},
	checkButtonApp : function(id) {
		var flag = document.getElementById(id).checked;

		if (flag) {
			if (jQuery.inArray(id, JB.selectedApp) == -1) {
				JB.selectedApp.push(id);
				JB.appSelect++;
			}
		} else {
			var index = jQuery.inArray(id, JB.selectedApp);
			if (index != -1) {
				JB.selectedApp.splice(index, 1);
				JB.appSelect--;
			}
		}
		if (($('#JobBrowserApps_summary_table tr').length - 1) == JB.selectedApp.length) {
			document.getElementById("selectAllApps").checked = flag;
			JB.toggleButton("selectAllApps", flag);
		} else
			JB.toggleButton(id, flag, "selectAllApps");
		JB.enableDisableButton();
	},

	overWriteExisting : function() {
		var rows = $("#JobBrowser_summary_table").dataTable().fnGetNodes();
		for (var i = 0; i < rows.length; i++) {
			var name = $(rows[i]).find("td:eq(1)").html();
			if (name == $("#jobName").val()) {
				return true;
			}
		}
		rows = $("#JobBrowserAdhoc_summary_table").dataTable().fnGetNodes();
		for (var i = 0; i < rows.length; i++) {
			var name = $(rows[i]).find("td:eq(1)").html();
			if (name == $("#jobName").val()) {
				return true;
			}
		}
		return false;
	},

	enableDisableButton : function() {

		for (var i = 0; i < JB.selectedApp.length; i++) {
			var val = $('#' + JB.selectedApp[i]).val();
			if (val == 'Finished') {
				$('#stopButton').attr('disabled', 'disabled');
			} else {
				$('#stopButton').removeAttr('disabled');
				break;
			}
		}
		if (JB.selectedApp.length != 1)
			$('#stopButton').attr('disabled', 'disabled');
	},

	checkButtonJob : function(id) {
		var flag = document.getElementById(id).checked;
		if (flag) {
			if (jQuery.inArray(id, JB.selectedJob) == -1) {
				JB.selectedJob.push(id);
				JB.jobSelect++;
			}
		} else {
			var index = jQuery.inArray(id, JB.selectedJob);
			if (index != -1) {
				JB.selectedJob.splice(index, 1);
				JB.jobSelect--;
			}
		}
		if (($('#JobBrowser_summary_table tr').length - 1) == JB.selectedJob.length) {
			document.getElementById("selectAllJobs").checked = flag;
			JB.toggleButton("selectAllJobs", flag);
		} else
			JB.toggleButton(id, flag, "selectAllJobs");
	},

	checkButtonJobAdhoc : function(id) {
		var flag = document.getElementById(id).checked;
		if (flag) {
			if (jQuery.inArray(id, JB.selectedJobAdhoc) == -1) {
				JB.selectedJobAdhoc.push(id);
				JB.jobSelectAdhoc++;
			}
		} else {
			var index = jQuery.inArray(id, JB.selectedJobAdhoc);
			if (index != -1) {
				JB.selectedJobAdhoc.splice(index, 1);
				JB.jobSelectAdhoc--;
			}
		}
		if (($('#JobBrowserAdhoc_summary_table tr').length - 1) == JB.selectedJobAdhoc.length) {
			document.getElementById("selectAllJobsAdhoc").checked = flag;
			JB.toggleButton("selectAllJobsAdhoc", flag);
		} else
			JB.toggleButton(id, flag, "selectAllJobsAdhoc");
	},

	addApplication : function(isAdhoc) {
		JB.isAdhoc = isAdhoc;
		JB.status = 'add';
		JB.currentOperation = 'addJob';
		Util.addLightbox('app_Box', 'resources/add_Application.html');
	},

	editApplication : function(isAdhoc) {
		JB.isAdhoc = isAdhoc;
		JB.isFormLoaded = false;
		JB.status = 'edit';
		JB.currentOperation = 'editJob';
		Util.addLightbox('app_Box', 'resources/add_Application.html');
	},

	onLoadAddEdit : function() {
		if (JB.isAdhoc) {
			$('#inst')
					.text(
							"Submit a MapReduce job for Adhoc query execution. To run the job execute an adhoc query from Hadoop Query Manager view. The 'Where' clause defined in Query Designer will be passed as argument to this job during execution. The processed output of the AdHoc job will be stored in the Result table defined in Query Designer.");
			$('#headerspan').text("AdHoc - Content Processor");
			$('#classMessage').text("Parser Class");
			$('#adhocInterface').show();
			$("#pathPatternDiv").show();
			$("#sourcePathDiv").show();
		}
		$('#isAdhoc').val(JB.isAdhoc);
		JB.fillClusterNameNodeID();
	},

	fillForm : function() {
		if (JB.currentOperation == 'editJob') {
			var nn = '';
			var rm = '';

			var tableId = "JobBrowser_summary_table";
			if (JB.isAdhoc)
				tableId = "JobBrowserAdhoc_summary_table";

			$('#' + tableId + ' tbody tr').each(function() {
				var row = this.cells;
				if (row[0].firstChild.checked) {
					JB.jobName = row[1].textContent;
					$('#jobName').val(row[1].textContent);
					$('#mainClass').val(row[2].textContent);

					if (!JB.isAdhoc) {
						$('#arguments').val(row[3].textContent);
						nn = row[4].textContent;
						rm = row[5].textContent;
						$('#jarFileText').val(row[6].textContent);
						var libJars = row[7].textContent;
						var nativeFiles = row[8].textContent;
					} else {
						$('#sourcePath').val(row[3].textContent);
						$('#pathPattern').val(row[4].textContent);
						$('#arguments').val(row[5].textContent);
						nn = row[6].textContent;
						rm = row[7].textContent;
						$('#jarFileText').val(row[8].textContent);
						var libJars = row[9].textContent;
						var nativeFiles = row[10].textContent;
					}
					dwr.util.byId('executionJarFile').style.display = 'none';
					dwr.util.byId('jarFileText').style.display = '';

					if ((libJars != null) && (libJars != '')) {
						JB.libJarsArray = libJars.split(",");
					}
					if ((nativeFiles != null) && (nativeFiles != '')) {
						JB.nativeFilesArray = nativeFiles.split(",");
					}
				}
			});
			if (nn != '')
				$("#nameNodeId").val(nn);

			if (rm != '')
				$("#resourceManagerId").val(rm);
		} else if (JB.currentOperation == 'addJob') {
			dwr.util.byId('chooseFile').style.display = 'none';
			dwr.util.byId('executionJarFile').style.width = '250px';
		}
	},

	startApplication : function(isAdhoc) {
		JB.isAdhoc = isAdhoc;
		JB.currentOperation = 'startJob';
		var rowData = [];

		JB.jobName = '';

		var tableId = "JobBrowser_summary_table";
		if (JB.isAdhoc)
			tableId = "JobBrowserAdhoc_summary_table";
		$('#' + tableId + ' tbody tr').each(function() {
			var row = this.cells;
			if (row[0].firstChild.checked) {
				JB.jobName = row[1].textContent;
			}
		});
		if (JB.jobName == '')
			alert("No job selected to run.");
		Util.addLightbox('app_Box', 'pages/popup.jsp');

	},

	stopApplication : function() {
		JB.currentOperation = 'stopJob';
		var rowData = [];

		JB.appName = '';

		$('#JobBrowserApps_summary_table tbody tr').each(function() {
			var row = this.cells;
			if (row[0].firstChild.checked == true) {
				JB.appName = row[1].textContent;
			}
		});
		if (JB.appName == '')
			jAlert("No job selected to stop.");
		Util.addLightbox('app_Box', 'pages/popup.jsp');
	},

	deleteApplication : function(isAdhoc) {
		JB.isAdhoc = isAdhoc;
		JB.status = 'delete';
		JB.currentOperation = 'deleteJob';

		var rowData = [];

		JB.jobName = '';
		JB.selectedJobsList.splice(0, JB.selectedJobsList.length);
		var tableId = "JobBrowser_summary_table";
		if (JB.isAdhoc)
			tableId = "JobBrowserAdhoc_summary_table";
		$('#' + tableId + ' tbody tr').each(function() {
			var row = this.cells;
			if (row[0].firstChild.checked) {
				JB.jobName = row[1].textContent;
				JB.selectedJobsList.push(row[1].textContent);
			}
		});
		if (JB.selectedJobsList == '')
			jAlert("No job selected to delete.");
		else {
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton = ' No';
			jConfirm('Are you sure you want to delete selected item(s)?',
					'Delete Job(s)', function(val) {
						if (val == true) {
							// FIXME
							Util.addLightbox('app_Box', 'pages/popup.jsp');
						} else
							return;
					});
		}
	},

	callbackDeleteResponse : function(response) {
		if (JB.isJobJarUpdated) {
			JB.isJobJarUpdated = false;
			return;
		}

		var operSuc;
		var operFail;
		var message;
		var status;
		var imgId;
		var id = JB.jobName;

		if (JB.currentOperation == 'deleteJob') {
			operSuc = "deleted";
			operFail = "delete";
		}
		if ((response != null) && (response != undefined)
				&& response.taskSuccess) {
			message = response.responseMessage;
			status = "Success";
			imgId = "popup.image.success";
		} else {
			message = "Failed to " + operFail + " job.";
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

	callbackResponse : function(response) {
		if (JB.isJobJarUpdated) {
			JB.isJobJarUpdated = false;
			return;
		}

		var operSuc;
		var operFail;
		var message;
		var status;
		var imgId;
		var id = JB.jobName;

		if (JB.currentOperation == 'startJob') {
			operSuc = "started";
			operFail = "start";
		} else if (JB.currentOperation == 'editJob') {
			operSuc = "updated";
			operFail = "update";
		} else if (JB.currentOperation == 'deleteJob') {
			operSuc = "deleted";
			operFail = "delete";
		} else if (JB.currentOperation == 'stopJob') {
			operSuc = "stopped";
			operFail = "stop";
			id = JB.appName;
		}

		if ((response != null) && (response != false)) {
			if (response == true)
				message = "Job " + operSuc + " successfully."
			else
				message = "Job " + operSuc
						+ " successfully with application id " + response
						+ " .";
			status = "Success";
			imgId = "popup.image.success";
		} else {
			message = "Failed to " + operFail + " job.";
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

	showError : function(operFail) {
		var winCal = window.open("", "Error Log");
		var docCal = winCal.document;
		var message = "Failed to " + operFail + " job.";
		if ((JB.startResponse != null)
				&& (JB.startResponse.taskSuccess == false))
			message = message + " " + JB.startResponse.responseMessage;
		var page = "<html><head><title>Error Log</title></head><body>"
				+ message + "</body></html>";
		docCal.writeln(page);
	},

	startCallbackResponse : function(response) {
		var operSuc;
		var operFail;
		var message;
		var status;
		var imgId;
		var id = JB.jobName;

		if (JB.currentOperation == 'startJob') {
			operSuc = "started";
			operFail = "start";
		}

		if ((response != null) && (response.taskSuccess)) {
			message = "Job " + operSuc + " successfully with application id "
					+ response.responseMessage + " .";
			status = "Success";
			imgId = "popup.image.success";
		} else {
			JB.startResponse = response;
			message = response.responseMessage;
			var messageLink = "<a onclick = \"JB.showError('" + operFail
					+ "');\" style = 'cursor: pointer;'>View Error</a>";

			document.getElementById('log_div' + id).style.display = "";
			document.getElementById('log_div' + id).innerHTML = messageLink;

			status = "Failure";
			imgId = "popup.image.fail";
		}

		dwr.util.setValue('popup.message' + id, message);
		dwr.util.byId('popup.image.processing' + id).style.display = 'none';
		dwr.util.byId(imgId + id).style.display = '';

		dwr.util.setValue('popup.status' + id, status);
		dwr.util.byId('ok.popup').disabled = false;

		if (status == "Success") {
			Navbar.currentJobTabSelected = 'execHistoryHead';
			JB.$tabs.tabs("select", "#execHistoryTab");
		}
	},

	deleteApplicationRequest : function() {

	},

	deleteApplicationResponse : function(resp) {
		var id = 'delete';
		dwr.util.byId('popup.image.processing' + id).style.display = 'none';
		dwr.util.setValue('popup.message' + id, resp.responseMessage);
		dwr.util.setValue('popup.status' + id, resp.taskSuccess ? 'Success'
				: 'Failed');
		if (!resp.taskSuccess) {
			dwr.util.byId('popup.image.fail' + id).style.display = '';
			var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
			document.getElementById('log_div' + id).innerHTML = log;
			document.getElementById('log_div' + id).style.display = "block";
		} else {
			dwr.util.byId('popup.image.success' + id).style.display = '';
		}
		dwr.util.byId('ok.popup').disabled = false;
		Navbar.refreshView();
		Navbar.refreshNavBar();
	},

	closeBox : function(isRefresh) {
		Util.removeLightbox('app_Box');
		JB.isAdhoc = false;
		JB.jobName = null;
		JB.appName = null;
		JB.newJobName = null;
		JB.jarFile = null;
		JB.mainClass = null;
		JB.arguments = null;
		JB.currentOperation = null;
		JB.selectedNN = null;
		JB.selectedRM = null;
		JB.counter = 1;
		JB.nativeCounter = 1;
		JB.rowCounter = 1;
		JB.rowNativeCounter = 1;

		if (isRefresh)
			Navbar.refreshView();
	},

	updatedJar : function() {
		JB.isJobJarUpdated = true;
		JB.isJarUpdated = true;
	},

	showChooseFile : function() {
		if (JB.isChangeShown) {
			dwr.util.byId('executionJarFile').style.display = '';
			dwr.util.byId('jarFileText').style.display = 'none';
			$('#chooseFile').val('Keep Unchanged');
			JB.isChangeShown = false;
		} else {
			dwr.util.byId('executionJarFile').style.display = 'none';
			dwr.util.byId('jarFileText').style.display = '';
			$('#chooseFile').val('Change');
			JB.isChangeShown = true;
		}
	},

	updatedLibJar : function(index, id, value) {
		JB.updatedLibJarArray[index] = true;
		$('#libJarFT' + index).val('');
		JB.isJarUpdated = true;
		var extension = value.substring(value.length - 4, value.length);
		if (extension != ".jar" && extension != ".JAR") {
			jAlert("Only JAR files are required to be uploaded.",
					"Incorrect Detail");
			$("#" + id).val("");
			$("#popup_container").css("z-index", "99999999");
		}
	},

	updatedNativeFile : function(index, id, value) {
		JB.updatedNativeFileArray[index] = true;
		$('#nativeFT' + index).val('');
		JB.isJarUpdated = true;
	},

	showLibJarChooseFile : function(index) {
		if ($('#chooseLibJarFile' + index).val() == 'Change') {
			dwr.util.byId('libJarFile' + index).style.display = '';
			dwr.util.byId('libJarFT' + index).style.display = 'none';
			$('#chooseLibJarFile' + index).val('Keep Unchanged');
		} else {
			dwr.util.byId('libJarFile' + index).style.display = 'none';
			dwr.util.byId('libJarFT' + index).style.display = '';
			$('#chooseLibJarFile' + index).val('Change');
		}
	},

	showNativeFileChooseFile : function(index) {
		if ($('#chooseNativeFile' + index).val() == 'Change') {
			dwr.util.byId('nativeFile' + index).style.display = '';
			dwr.util.byId('nativeFT' + index).style.display = 'none';
			$('#chooseNativeFile' + index).val('Keep Unchanged');
		} else {
			dwr.util.byId('nativeFile' + index).style.display = 'none';
			dwr.util.byId('nativeFT' + index).style.display = '';
			$('#chooseNativeFile' + index).val('Change');
		}
	},

	uploadFile : function(form) {
		var value = $("#chooseFile").val();

		if ((value == "Change") && (JB.currentOperation == 'editJob')) {
			var temp = $("#jarFileText").val();
			$("#jarText").val(temp);
			$("#executionJarFile").remove();
		} else {
			if ($("#executionJarFile").val() == "") {
				jAlert("JAR required.", "Incomplete Detail");
				$("#popup_container").css("z-index", "99999999");
			} else {
				var file = $("#executionJarFile").val();
				file = file.substring(file.lastIndexOf("\\") + 1, file.length);
				$("#jarText").val(file);
			}
		}

		var newJob = document.getElementById('add_Job');

		if (newJob.jobName.value == null || newJob.jobName.value == '') {
			jAlert("Job Name required.", "Incomplete Detail");
			$("#popup_container").css("z-index", "99999999");
		} else {
			if (JB.currentOperation == 'editJob') {
				if (JB.isJobJarUpdated) {
					// RemoteManager.deleteJob(JB.jobName, JB.callbackResponse);
				} else if (!JB.isJarUpdated) {
					JB.newJobName = newJob.jobName.value;
					JB.mainClass = newJob.mainClass.value;
					JB.arguments = newJob.arguments.value;
					JB.pathPattern = newJob.pathPattern.value;
					JB.sourcePath = newJob.sourcePath.value;
					JB.jarFile = newJob.jarFileText.value;
					JB.selectedNN = newJob.nameNodeId.value;
					JB.selectedRM = newJob.resourceManagerId.value;
					Util.removeLightbox('app_Box');
					Util.addLightbox('app_Box', 'pages/popup.jsp');
					return;
				}
			}

			$('#inst').html('Submitting Job to Cluster');

			JB.showPopup();

			$('#okBox').css('display', '');

			var iframe = document.createElement("iframe");
			iframe.setAttribute("id", "upload_iframe");
			iframe.setAttribute("name", "upload_iframe");
			iframe.setAttribute("width", "0");
			iframe.setAttribute("height", "0");
			iframe.setAttribute("border", "0");
			iframe.setAttribute("style", "width: 0; height: 0; border: none;");

			var div_id = 'formDIV';
			// Add to document...
			form.parentNode.appendChild(iframe);
			window.frames['upload_iframe'].name = "upload_iframe";

			iframeId = document.getElementById("upload_iframe");

			// Add event...
			var eventHandler = function() {
				var content = '';
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

				JB.fillResponsePopup(content);
			}

			if (iframeId.addEventListener)
				iframeId.addEventListener("load", eventHandler, true);
			if (iframeId.attachEvent)
				iframeId.attachEvent("onload", eventHandler);

			// Set properties of form...
			form.setAttribute("target", "upload_iframe");
			form.setAttribute("action", "SubmitJob");
			form.setAttribute("method", "POST");
			form.setAttribute("enctype", "multipart/form-data");
			form.setAttribute("encoding", "multipart/form-data");
			form.setAttribute("accept-charset", "UTF-8");
			form.submit();
		}
	},

	showPopup : function() {
		$('#add_Job').css('display', 'none');
		$('#responsePopup').css('display', '');
		dwr.util.byId('popup.image.processing').style.display = '';
		dwr.util.setValue('popup.message', 'Processing Upload Request...');
		dwr.util.setValue('popup.status', 'Uploading');
	},

	fillResponsePopup : function(content) {
		var status;
		var imgId;

		if (content.indexOf("Failed") != -1
				|| content.indexOf("sufficient privileges") != -1) {
			status = "Failure";
			imgId = "popup.image.fail";
		} else {
			status = "Success";
			imgId = "popup.image.success";
		}
		dwr.util.byId('popup.image.processing').style.display = 'none';
		dwr.util.byId(imgId).style.display = '';

		dwr.util.setValue('popup.message', content);
		dwr.util.setValue('popup.status', status);
	},

	addLibClicked : function() {

		if (JB.counter == 10) {
			jAlert("Only 10 Files are allowed at once.", "Limit Reached");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		var temp = JB.rowCounter + 1;

		var tbl_data = '<tr id="libJarRow'
				+ temp
				+ '"><td style = "text-align: left; width: 10%;">Lib Jar'
				+ temp
				+ '</td><td style = "text-align: left; width: 80%;">'
				+ '<input type="text" id="libJarText'
				+ temp
				+ '" name="libJarText'
				+ temp
				+ '" value="" style="width: 60%; display: none;">'
				+ '<input type="file" id="libJarFile'
				+ temp
				+ '" name="libJarFile'
				+ temp
				+ '" onchange="javascript:JB.updatedLibJar('
				+ temp
				+ ', this.id, this.value);" style="width: 80%;">'
				+ '<input type="text" id="libJarFT'
				+ temp
				+ '" name="libJarFT'
				+ temp
				+ '" readonly="readonly" value="" style="width: 60%; display: none;">'
				+ '<a href="javascript:JB.removeLibClicked('
				+ temp
				+ ');" style="color: white; float: right; padding-top: 7px;"><img alt="Remove File" src="images/minus_sign_brown.png" id="minusImage" style="height: 10px; width: 20px;"></a>'
				+ '<a href="javascript:JB.addLibClicked();" style="float: right; padding-top: 7px;"><img alt="Add More File" src="images/plus_sign_brown.png" id="plusImage" style="height: 12px;"></a>'
				+ '</td>'
				+ '<td style = "text-align: left; width: 20%;">'
				+ '<input type="button" class="buttonAdmin" id="chooseLibJarFile'
				+ temp
				+ '" value="Change" onclick="javascript:JB.showLibJarChooseFile('
				+ temp
				+ ');" style="width: 110px; float: right; margin: 0px; display: none;"/>'
				+ '</td></tr>';
		var $tr;
		var isAdded = true;
		var index = JB.counter;
		while (isAdded) {
			$tr = $('#libJarRow' + index)
			if ($tr.length) {
				isAdded = false;
				$tr.after(tbl_data);
			}
			index++;
		}

		JB.fileName.push([ "file-" + temp ]);
		JB.counter++;
		JB.rowCounter++;
	},
	removeLibClicked : function(id) {
		if (JB.counter == 1) {
			jAlert("Atleast 1 File should be there.", "Invalid Action");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		$("#libJarRow" + id).remove();
		JB.fileName.splice(JB.fileName.indexOf('file-' + id), 1);
		var lib = $('#libJarFT' + id).val();
		if (lib != '')
			JB.isJarUpdated = true;
		JB.counter--;
	},
	addNativeFileClicked : function() {

		if (JB.nativeCounter == 10) {
			jAlert("Only 10 Files are allowed at once.", "Limit Reached");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		var temp = JB.rowNativeCounter + 1

		var tbl_data = '<tr id="nativeFileRow'
				+ temp
				+ '"><td style="width: 10%;">Native File '
				+ temp
				+ '</td><td style="width: 80%;">'
				+ '<input type="text" id="nativeText'
				+ temp
				+ '" name="nativeText'
				+ temp
				+ '" value="" style="width: 60%; display: none;">'
				+ '<input type="file" id="nativeFile'
				+ temp
				+ '" name="nativeFile'
				+ temp
				+ '" onchange="javascript:JB.updatedNativeFile('
				+ temp
				+ ', this.id, this.value);" style="width: 80%;">'
				+ '<input type="text" id="nativeFT'
				+ temp
				+ '" name="nativeFT'
				+ temp
				+ '" readonly="readonly" value="" style="width: 60%; display: none;">'
				+ '<a href="javascript:JB.removeNativeFileClicked('
				+ temp
				+ ');" style="color: white; float: right; padding-top: 7px;"><img alt="Remove File" src="images/minus_sign_brown.png" id="minusImage" style="height: 10px; width: 20px;"></a>'
				+ '<a href="javascript:JB.addNativeFileClicked();" style="float: right; padding-top: 7px;"><img alt="Add More File" src="images/plus_sign_brown.png" id="plusImage" style="height: 12px;"></a>'
				+ '</td>'
				+ '<td style = "width: 20%;">'
				+ '<input type="button" class="buttonAdmin" id="chooseNativeFile'
				+ temp
				+ '" value="Change" onclick="javascript:JB.showNativeFileChooseFile('
				+ temp
				+ ');" style="width: 110px; float: right; margin: 0px; display: none;"/>'
				+ '</td></tr>';

		var $tr;
		var isAdded = true;
		var index = JB.nativeCounter;
		while (isAdded) {
			$tr = $('#nativeFileRow' + index)
			if ($tr.length) {
				isAdded = false;
				$tr.after(tbl_data);
			}
			index++;
		}

		JB.fileName.push([ "file-" + temp ]);
		JB.nativeCounter++;
		JB.rowNativeCounter++;
	},
	removeNativeFileClicked : function(id) {
		if (JB.nativeCounter == 1) {
			jAlert("Atleast 1 File should be there.", "Invalid Action");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		$("#nativeFileRow" + id).remove();
		JB.fileName.splice(JB.fileName.indexOf('file-' + id), 1);
		var native = $('#nativeFT' + id).val();
		if (native != '')
			JB.isJarUpdated = true;
		JB.nativeCounter--;
	},
	nextStep : function(step) {

		switch (step) {

		case 1:
			$("#table").css("display", "");
			$("#table2").css("display", "none");
			break;
		case 2:
			if ($("#nameNodeId").val() == "Select NameNode") {
				jAlert("You must specify the NameNode.", "Insufficient Details");
				$("#popup_container").css("z-index", "99999999");
				return;
			} else if ($("#resourceManagerId").val() == "Select ResourceManager") {
				jAlert("You must specify the ResourceManager.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "99999999");
				return;
			}
			// else if(JB.overWriteExisting() && JB.status == 'add')
			// {
			// jAlert('Job Name already exists.','Conflicting Job names');
			// $("#popup_container").css("z-index","99999999");
			// return;
			// }
			else if ($('#jobName').val() == "") {
				jAlert("You must specify the Job Name.", "Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			} else if ($('#mainClass').val() == "") {
				jAlert("You must specify the Main Class for the job.",
						"Insufficient Details");
				$("#popup_container").css("z-index", "9999999");
				return;
			} else {
				var value = $("#chooseFile").val();
				if ((JB.status == 'edit' && value == "Keep Unchanged")
						|| JB.status == 'add') {
					if ($("#executionJarFile").val() == "") {
						jAlert("JAR required.", "Incomplete Detail");
						$("#popup_container").css("z-index", "99999999");
					} else {
						var jarName = "";
						jarName = $("#executionJarFile").val();
						var extension = jarName.substring(jarName.length - 4,
								jarName.length);
						if (extension != ".jar" && extension != ".JAR") {
							jAlert(
									"Only JAR files are required to be uploaded.",
									"Incorrect Detail");
							$("#popup_container").css("z-index", "99999999");
						} else {
							$("#table2").css("display", "");
							$("#table").css("display", "none");
						}
					}
				} else {
					$("#table2").css("display", "");
					$("#table").css("display", "none");
				}
			}

			// parse libJars and nativeFiles.
			if (!JB.isFormLoaded) {
				JB.isFormLoaded = true;
				if ((JB.libJarsArray != null) && (JB.libJarsArray.length > 0)) {
					var isRemoveLib = false;
					for (var i = 1; i <= JB.libJarsArray.length; i++) {
						dwr.util.byId('chooseLibJarFile' + i).style.display = '';
						dwr.util.byId('libJarFile' + i).style.display = 'none';
						dwr.util.byId('libJarFT' + i).style.display = '';
						$('#libJarFT' + i).val(JB.libJarsArray[i - 1]);
						isRemoveLib = true;
						JB.addLibClicked();
						JB.updatedLibJarArray[i] = false;
					}
					if (isRemoveLib)
						JB.removeLibClicked(i);
				}
				if ((JB.nativeFilesArray != null)
						&& (JB.nativeFilesArray.length > 0)) {
					var isRemoveNative = false;
					for (var i = 1; i <= JB.nativeFilesArray.length; i++) {
						dwr.util.byId('chooseNativeFile' + i).style.display = '';
						dwr.util.byId('nativeFile' + i).style.display = 'none';
						dwr.util.byId('nativeFT' + i).style.display = '';
						$('#nativeFT' + i).val(JB.nativeFilesArray[i - 1]);
						isRemoveNative = true;
						JB.addNativeFileClicked();
						JB.updatedNativeFileArray[i] = false;
					}
					if (isRemoveNative)
						JB.removeNativeFileClicked(i);
				}
			}

			break;

		}

	},

	scheduleApplication : function(isAdhoc) {
		JB.isAdhoc = isAdhoc;
		JB.status = 'schedule';
		JB.currentOperation = 'scheduleJob';
		JB.currentPage = 1;
		Util.addLightbox('app_Box', 'resources/schedule_Application.html');
	},

	validatePage : function() {
		var flag = true;
		switch (JB.currentPage) {
		case 1: {
			document.getElementById('msg_td_1').innerHTML = "";
			var reportDate = document.forms[0].schDate.value;
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

	checkForScheduleID : function() {
		if (document.getElementById('schedID').value != "") {
			RemoteManager.checkMapRedScheduleId(document
					.getElementById('schedID').value, JB.checkResp);
		} else {
			document.getElementById('msg_td_1').innerHTML = "";
			document.getElementById('msg_td_1').innerHTML += "* Schedule Id Required<br>";
		}
	},

	checkResp : function(flag) {
		if (flag) {
			jAlert("ScheduleId already Taken", "Error");
		} else {
			$('#schedulediv1').hide();
			$('#schedulediv2').show();
			$('#schedulediv3').hide();
		}
		$("#popup_container").css("z-index", "99999999");
	},

	checkNotificationSettings : function(nbean) {
		JB.emailEnabled = nbean.emailEnabled;
		JB.logEnabled = nbean.logEnabled;
	},

	nextScheduleStep : function(selectedDiv) {
		var flag = true;
		if (JB.currentPage < selectedDiv) {
			if (!JB.emailEnabled && !JB.logEnabled) {
				document.getElementById('msg_td_1').innerHTML = "* You have not configured any notifications. Please configure notifications and return to this wizard. To configure notifications, go to <b>Dashboard > Notifications</b> tab.";
				return;
			} else if (selectedDiv == 3 && !JB.emailEnabled) {
				document.getElementById('msg_td_2').innerHTML = "* You have not configured Email notification. Please configure Email notification and return to this wizard.";
				return;
			} else {
				flag = JB.validatePage();
			}
		}
		if (flag) {
			switch (selectedDiv) {
			case 1: {
				RemoteManager
						.getNotificationSettings(JB.checkNotificationSettings);
				$('#schedulediv1').show();
				$('#schedulediv2').hide();
				$('#schedulediv3').hide();
				break;
			}
			case 2: {
				JB.checkForScheduleID();
				break;
			}
			case 3: {
				RemoteManager.getUserDetails(JB.addUser);
				$('#schedulediv1').hide();
				$('#schedulediv2').hide();
				$('#schedulediv3').show();
				$('input[value="Close"]').hide();
				break;
			}
			}
			JB.currentPage = selectedDiv;
		}

	},

	addUser : function(list) {
		if (list != null) {
			$('#user option').remove();
			$('#selected option').remove();
			JB.userCache = [];
			for (var i = 0; i < list.length; i++) {
				var user = list[i];
				JB.userCache.push(user);
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
			JB.moveSelectedOptions('user', 'selected');
		}
	},

	moveSelectedOptions : function(from, to) {

		var source = document.getElementById(from).getElementsByTagName(
				"option");
		for (var i = 0; i < source.length; i++) {
			if (source[i].selected) {
				$('#' + to).append(
						'<option value="' + source[i].value + '">'
								+ JB.findUser(source[i].value) + '</option>');
			}
		}
		$("#" + from + " option:selected").remove();
	},

	findUser : function(val) {
		for (var i = 0; i < JB.userCache.length; i++) {
			var user = JB.userCache[i];
			if (user.id == val)
				return user.firstName + ' ' + user.lastName;
		}
	},

	saveScheduleClicked : function() {
		$("#error_msg").text("");
		document.getElementById("msg_td_3").innerHTML = "";
		var id = $("#schedID").val();
		var sinterval = document.forms[0].interval.value;
		var time = document.forms[0].schDate.value;
		var jobsId = JB.getJobsId();
		var selectedUser = document.getElementById('selected')
				.getElementsByTagName("option");
		var users = [];
		for (var i = 0; i < selectedUser.length; i++) {
			users.push(selectedUser[i].value);
		}
		if (users.length > 0) {
			$('#JobBrowser_summary_table tbody tr').each(function() {
				var row = this.cells;
				if (row[0].firstChild.checked) {
					JB.jobName = row[1].textContent
				}
			});
			if (id == '') {
				$("#error_msg").text("Schedule ID was not entered.");
				return;
			}
			if (time == '') {
				$("#error_msg").text("Schedule Time was not entered.");
				return;
			}
			RemoteManager
					.scheduleMapRedJob(
							sinterval,
							time,
							jobsId,
							id,
							document.getElementById('notificationType').value,
							document
									.getElementById('alertRaisedNotificationMessage').value,
							users, JB.scheduleSaved);
		} else {
			document.getElementById("msg_td_3").innerHTML = "Users not selected for notifications";
		}

	},

	scheduleSaved : function(response) {
		if (response == true) {
			JB.closeBox(true);
			jAlert('Job scheduled successfully');
			$("#popup_container").css("z-index", "9999999");
		} else {
			jAlert('Some Error Occurred during scheduling job.');
			$("#popup_container").css("z-index", "9999999");
		}

	},

	selectedImmediate : function() {
		if (document.forms[0].interval.value == 4) {
			// document.getElementById("schTime").style.display = 'none';
			$("#schTime").css("display", "none");
			$("#schId").css("display", "none");

		} else {
			// document.getElementById("schTime").style.display = '';
			$("#schTime").css("display", "");
			$("#schId").css("display", "");
		}
	},

	fillClusterNameNodeID : function() {

		RemoteManager
				.getAllNameNodeForDBNameMapping(JB.populateClusterNameNodeIds);
	},

	populateClusterNameNodeIds : function(list) {
		var data = "";

		data = Util.getCurrentIdDropDown(list, "Select NameNode",
				"Select NameNode");

		$('#nameNodeId').html(data);
		JB.fillResourceManagerIds();
	},

	fillResourceManagerIds : function() {
		RemoteManager.getAllResourceManagers(JB.populateResourceManager);
	},

	populateResourceManager : function(list) {
		var data = "";

		data = Util.getCurrentIdDropDown(list, "Select ResourceManager",
				"Select ResourceManager");

		$('#resourceManagerId').html(data);

		JB.fillForm();
	},
	showContainerLogs : function(appId) {
		RemoteManager.getNodeManagerLogsPath(appId, JB.populateLogsPath);
	},

	populateLogsPath : function(object) {
		JB.containerObject = object;
		Util.addLightbox("containerBox", "resources/showContainerTable.html",
				null, null);
	},

	getTreeTableForContainer : function() {
		var obj = JB.containerObject.nodeManagerHosts;
		var tableData = "<thead><tr>";
		tableData += "<th style = 'text-align: center; width: 22%;'>NodeManager</th>";
		tableData += "<th style = 'text-align: center; width: 50%;'>Container Name</th>";
		tableData += "<th style = 'text-align: center; width: 28%;'>Container Loglink</th>";
		tableData += "</tr></thead><tbody>";
		if (obj == null || obj == undefined || obj.length == 0) {
			tableData += "<tr><td style = 'text-align: center; font-size: 9pt;' colspan = '3'>No Data available</td></tr>";
		} else {
			for (var i = 0; i < obj.length; i++) {
				tableData += "<tr id = '" + obj[i].id + "'>";
				var extraCSS = "";
				if (i == 0)
					extraCSS = "padding: 0 0 0 10px;";

				tableData += "<td style = 'text-align: left; " + extraCSS
						+ "'>" + obj[i].id
						+ "</td><td colspan = '2'></td></tr>";
				for (var j = 0; j < obj[i].containerArray.length; j++) {
					tableData += "<tr class = 'child-of-" + obj[i].id + "'>";
					tableData += "<td style = 'text-align: center;'></td>";
					tableData += "<td>"
							+ obj[i].containerArray[j].containerName + "</td>";
					tableData += "<td style = 'text-align: center;'><a href=\"javascript:JB.viewLogFile('"
							+ obj[i].hostIP
							+ "','"
							+ obj[i].hostPort
							+ "','"
							+ obj[i].containerArray[j].logFilePath
							+ "');\">View Log</a></td>";
					tableData += "</tr>";
				}
			}
		}
		tableData += "</tbody>";

		$('#containerTable').html(tableData);

		$("#containerTable").treeTable();

		$('.expander').click(function() {
			var heightTable = $("#containerTable").height();
			var heightContainer = $("#pagerContainerTable").height();
			if (heightTable > 200) {
				$("#pagerContainerTable").height("220px");
				$("#pagerContainerTable").css('overflow-y', 'scroll');
			} else {
				$("#pagerContainerTable").height(heightTable + 10);
				$("#pagerContainerTable").css('overflow-y', 'hidden');
			}

		});
	},

	viewLogFile : function(host, port, path) {
		var url = "";
		url = "http://" + host + ":" + port
				+ "/agentqueryio/log?node-type=containerLog&host-dir=" + path
				+ "&file-type=log";
		window.open(url);
		return;
	},

	closeContainerBox : function() {
		Util.removeLightbox('containerBox');
	},

	getJobsId : function() {
		var list = [];
		if (JB.isAdhoc) {
			for (var i = 0; i < JB.selectedJobAdhoc.length; i++) {
				list.push((JB.selectedJobAdhoc[i]).substring(12));
			}
		} else {
			for (var i = 0; i < JB.selectedJob.length; i++) {
				list.push((JB.selectedJob[i]).substring(12));
			}
		}
		return list;
	},

	moveAllOptions : function(from, to) {
		var source = document.getElementById(from).getElementsByTagName(
				"option");
		for (var i = 0; i < source.length; i++) {
			$('#' + to).append(
					'<option value="' + source[i].value + '">'
							+ JB.findUser(source[i].value) + '</option>');
		}
		$('#' + from).children().remove();
	},

	saveMRLog : function() {
		var flag = JB.validatePage();
		if (flag) {
			var id = $("#schedID").val();
			var sinterval = document.forms[0].interval.value;
			var time = document.forms[0].schDate.value;
			var jobsId = JB.getJobsId();
			RemoteManager
					.scheduleMapRedJob(
							sinterval,
							time,
							jobsId,
							id,
							document.getElementById('notificationType').value,
							document
									.getElementById('alertRaisedNotificationMessage').value,
							null, JB.scheduleSaved);
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

	scheduleMRwithoutNotify : function() {
		JB.currentPage = 1;
		var flag = JB.validatePage();
		if (flag) {
			JB.validateForScheduleID();
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

	validateForScheduleID : function() {
		if (document.getElementById('schedID').value != "") {
			RemoteManager.checkMapRedScheduleId(document
					.getElementById('schedID').value, JB.checkValidateResp);
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
			var id = $("#schedID").val();
			var sinterval = document.forms[0].interval.value;
			var time = document.forms[0].schDate.value;
			var jobsId = JB.getJobsId();
			var reportScheduleForm = document.getElementById('scheduleReport');
			RemoteManager.scheduleMapRedJobWithoutNotification(sinterval, time,
					jobsId, id, JB.scheduleSaved);
		}
	},

	updateExecutionHistory : function() {
		if (JB.isSetTimer) {
			var timerProcess = setTimeout(
					function() {
						RemoteManager
								.getAllApplicationsSummary(JB.populateApplicationsTable);
					}, 5000);
			JB.timer.push(timerProcess);
		} else {
			for (var i = 0; i < JB.timer.length; i++) {
				clearTimeout(JB.timer[i]);
			}
		}
	}

};
