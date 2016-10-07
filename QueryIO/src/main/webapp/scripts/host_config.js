Hosts = {
	isHostAddOperation : true,
	hostId : null,
	currentOperation : '',

	ready : function() {
		RemoteManager.getAllHostDetails(Hosts.fillHostList);
		dwr.util.byId('host.delete').disabled = true;
		dwr.util.byId('host.edit').disabled = true;
		dwr.util.byId('host.start').disabled = true;
		dwr.util.byId('host.stop').disabled = true;
		dwr.util.byId('startMonitoring').disabled = true;
		dwr.util.byId('stopMonitoring').disabled = true;
		dwr.util.byId('startNodes').disabled = true;
		dwr.util.byId('stopNodes').disabled = true;
	},

	fillHostList : function(list) {
		var tableList = new Array();
		if (list != null) {
			for (var i = 0; i < list.length; i++) {
				var host = list[i];
				var hostId = host.id;
				var check = '<input type="checkbox" id="'
						+ hostId
						+ '" onclick="javascript:Hosts.clickCheckBox(this.id)">';
				var hostIp = host.hostIP;
				var rackName = host.rackName;
				var installationDir = host.installDirPath;
				var status = host.status;
				var port = host.agentPort
				var monitoring = ''
				if (host.monitor) {
					monitoring = 'Started';
				} else {
					monitoring = 'Stopped';
				}
				var log = '<a href="javascript:viewLogFile(\'' + hostIp
						+ '\',\'' + port + '\',\'' + installationDir
						+ '\');">View Log</a>';
				tableList.push([ check, hostIp, rackName, installationDir,
						status, monitoring, log ]);
				hostMap[hostId] = host;
			}

		}
		$('#hosts_list_table').dataTable({
			// "sScrollX": "100%",
			"bPaginate" : false,
			"bLengthChange" : true,
			"sPaginationType" : "full_numbers",
			"bFilter" : false,
			"bDestroy" : true,
			"bSort" : true,
			"bInfo" : false,
			"bDestroy" : true,
			"bAutoWidth" : true,
			"aaData" : tableList,

			"aoColumns" : [ {
				"sTitle" : ''
			}, {
				"sTitle" : "Host IP"
			}, {
				"sTitle" : "Rack Name"
			}, {
				"sTitle" : "Installation Path"
			}, {
				"sTitle" : "Status"
			}, {
				"sTitle" : "Monitoring"
			}, {
				"sTitle" : "Agent Log"
			}
			// ,
			// { "sTitle": "Nodes Status" }
			]
		});
	},

	deleteHost : function() {
		Hosts.currentOperation = "delete";
		Util.addLightbox("hostop", "resources/host_operation_popup.html", null,
				null);
	},
	closeBox : function(isRefresh) {
		Util.removeLightbox("hostop");
		if (isRefresh)
			Navbar.refreshView();
	},

	clickCheckBox : function(chkbxid) {
		var serviceId = chkbxid;
		// Hosts.startNodes(serviceId);
		// Hosts.stopNodes(serviceId);

		dwr.util.byId('host.delete').disabled = false

		if (dwr.util.byId(chkbxid).checked) {
			if (selectedHost.indexOf(serviceId) == -1)
				selectedHost.push(serviceId);
		} else {
			var index = selectedHost.indexOf(serviceId);
			selectedHost.splice(index, 1);
		}
		if (selectedHost.length == 1) {
			dwr.util.byId('host.delete').disabled = false;
			dwr.util.byId('host.edit').disabled = false;
			dwr.util.byId('startNodes').disabled = false;
			dwr.util.byId('stopNodes').disabled = false;
			Hosts.checkStartStop();

		} else {
			dwr.util.byId('startMonitoring').disabled = true;
			dwr.util.byId('stopMonitoring').disabled = true;
			dwr.util.byId('host.delete').disabled = true;
			dwr.util.byId('host.edit').disabled = true;
			dwr.util.byId('host.start').disabled = true;
			dwr.util.byId('host.stop').disabled = true;
			dwr.util.byId('startNodes').disabled = true;
			dwr.util.byId('stopNodes').disabled = true;

		}
	},

	checkStartStop : function() {
		$('#hosts_list_table tbody tr').each(function() {
			var row = this.cells;

			if (row[0].firstChild.id == selectedHost[0]) {
				var status = row[4].textContent;
				var mstatus = row[5].textContent;

				if (status != "Stopped") {
					dwr.util.byId('host.start').disabled = true;
					dwr.util.byId('host.stop').disabled = false;
					if (mstatus != "Stopped") {
						dwr.util.byId('startMonitoring').disabled = true;
						dwr.util.byId('stopMonitoring').disabled = false;
					} else {
						dwr.util.byId('startMonitoring').disabled = false;
						dwr.util.byId('stopMonitoring').disabled = true;
					}
				} else {
					dwr.util.byId('host.start').disabled = false;
					dwr.util.byId('host.stop').disabled = true;
					dwr.util.byId('startMonitoring').disabled = true;
					dwr.util.byId('stopMonitoring').disabled = true;
					dwr.util.byId('startNodes').disabled = true;
					dwr.util.byId('stopNodes').disabled = true;

				}

			}
		});
	},

	deleteHostPopUpready : function() {
		var id = selectedHost[0];
		var host = hostMap[id];

		$('#host_name_span1').text(host.hostIP);
		$('#host_name_span2').text(host.hostIP);

		if (Hosts.currentOperation == "start") {
			$('#headerspan').text("Start Host");
			$("#details").text("Provide ssh credentials to start the host.");
			$("#Operation").val("Start");
		} else if (Hosts.currentOperation == "stop") {
			$('#headerspan').text("Stop Host");
			$("#details").text("Provide ssh credentials to stop the host.");
			$("#Operation").val("Stop");
		}

		if (host.hostIP == "127.0.0.1") {
			Hosts.deleteSelectedHost(true);
		}

	},

	deleteSelectedHost : function(isLocal) {

		var hostId = selectedHost[0];
		var username = $('#username').val();
		var password = $('#password').val();
		var privateKey = $('#privateKey').val();
		if (!isLocal) {
			if (username == '') {
				jAlert("Please provide proper credentails for host",
						'Incomplete details');
				$("#popup_container").css("z-index", "99999999");
				return;
			}
			if ($('#authenticationMethod').val() == 'password') {
				privateKey = null;
			} else {
				password = null;
			}
			var port = $('#port').val();
			if (port == '' || port == null) {
				jAlert("SSH port not provided.", "Insufficient Details");
				$("#popup_container").css("z-index", "99999999");
				return;
			} else if (!port.match("^[0-9]{1,6}$")) {
				jAlert("SSH port can not be character.", "Insufficient Details");
				$("#popup_container").css("z-index", "99999999");
				return;
			}
		}

		$('#status_message').text("Processing");
		if (Hosts.currentOperation == "start") {
			$('#status').text("Starting host..");
			if (isLocal) {
				RemoteManager.startHostInstaller(parseInt(hostId), username,
						password, privateKey, port, isLocal,
						Hosts.handleDeleteHostResp);
			} else {
				RemoteManager.startHost(parseInt(hostId), username, password,
						privateKey, port, Hosts.handleDeleteHostResp);
			}
		} else if (Hosts.currentOperation == "stop") {
			$('#status').text("Stopping host..");
			if (isLocal) {
				RemoteManager.stopHost(parseInt(hostId), "", "", "", "",
						isLocal, Hosts.handleDeleteHostResp);
			} else {
				RemoteManager.stopHost(parseInt(hostId), username, password,
						privateKey, port, isLocal, Hosts.handleDeleteHostResp);
			}

		} else if (Hosts.currentOperation == "delete") {
			$('#status').text("Deletion in progress..");

			RemoteManager.deleteHost(parseInt(hostId), username, password,
					privateKey, port, isLocal, Hosts.handleDeleteHostResp);
		} else if (Hosts.currentOperation == "stopNodes") {
			$('#headerspan').text("Stopping Nodes");
			$('#status').text("Stopping Nodes in progress..");

			RemoteManager.getStatusofNodesforHost(parseInt(hostId),
					Hosts.currentOperation, Hosts.getNodesStatus);
		} else if (Hosts.currentOperation == "startNodes") {
			$('#headerspan').text("Starting Nodes");
			$('#status').text("Starting Nodes in progress..");

			RemoteManager.getStatusofNodesforHost(parseInt(hostId),
					Hosts.currentOperation, Hosts.getNodesStatus);
		}
		document.getElementById('msg_td').innerHTML = "Performing operation....";
		$('#image_processing').css('display', '');
		$('#host_form_div').css('display', 'none');
		$('#host_status_div').css('display', '');

	},
	handleDeleteHostResp : function(dwrResponse) {
		var st = '';
		if (dwrResponse.taskSuccess) {
			st = 'Success';
			$('#image_success').css('display', '');
			$('#image_processing').css('display', 'none');
			document.getElementById('msg_td').innerHTML = "Operation performed successfully.";
			$('#msg_td').css('color', 'green');

		} else {
			st = 'Failed';
			$('#image_fail').css('display', '');
			$('#image_processing').css('display', 'none');
			var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
			document.getElementById('log_div').innerHTML = log;
			document.getElementById('log_div').style.display = "block";
			document.getElementById('msg_td').innerHTML = "Operation failed.";
			$('#msg_td').css('color', 'red');
		}

		$('#status_message').text(dwrResponse.responseMessage);
		$('#status').text(st);
		document.getElementById("deletehost.ok").disabled = false;
		Navbar.refreshView();

	},
	addNewHost : function() {

		Util.addLightbox("adddn", "resources/add_host.html", null, null);
	},
	closeAddHostBox : function() {
		Util.removeLightbox("adddn");
	},
	editHost : function() {
		this.hostId = selectedHost[0];
		Util.addLightbox("hostop", "resources/edit_host.html", null, null);
	},
	startHost : function() {
		Hosts.currentOperation = "start";
		Util.addLightbox("hostop", "resources/host_operation_popup.html", null,
				null);
	},
	stopHost : function() {
		Hosts.currentOperation = "stop";
		Util.addLightbox("hostop", "resources/host_operation_popup.html", null,
				null);
	},
	addHostReady : function() {

		var hostId = this.hostId;
		if (hostId != undefined)
			RemoteManager.getHost(parseInt(hostId), Hosts.editHostReady);
	},
	editHostReady : function(host) {

		$('#hostIP').val(host.hostIP);
		$('#rackName').val(host.rackName);
		$('#hostId').text(host.id);
	},
	saveEditHost : function() {

		document.getElementById('save.host').disabled = true;
		var hostId = $('#hostId').text();
		var rackName = $('#rackName').val();
		if (rackName == '') {
			jAlert("Rack name was not provided.Please provide rack name",
					"Insufficient details");
			$("#popup_container").css("z-index", "99999999");
			return;
		}
		RemoteManager.updateRackConfig(parseInt(hostId), rackName,
				Hosts.handleEditSaveresponse)
	},
	handleEditSaveresponse : function(dwrResponse) {

		var st = '';
		if (dwrResponse.taskSuccess) {
			st = 'Success';

		} else {
			st = 'Failed';
		}
		Hosts.closeBox(true);
		jAlert(dwrResponse.responseMessage, st);
		Navbar.refreshView();
	},
	stopHostMonitoring : function() {
		Hosts.currentOperation = "stopMonitoring";
		Util.addLightbox("hostop", "pages/popup.jsp", null, null);
	},
	startHostMonitoring : function() {
		Hosts.currentOperation = "startMonitoring";
		Util.addLightbox("hostop", "pages/popup.jsp", null, null);
	},

	nodeHostPopUpready : function() {
		var id = selectedHost[0];
		var host = hostMap[id];
		$('#host_name_span1').text(host.hostIP);
		$('#host_name_span2').text(host.hostIP);
		if (Hosts.currentOperation == "startNodes"
				|| Hosts.currentOperation == "stopNodes") {
			Hosts.nodeOperationOnSelectedHost(id);
		}
	},

	nodeOperationOnSelectedHost : function(hostId) {

		if (Hosts.currentOperation == "stopNodes") {

			$('#headerspan').text("Stopping Nodes");
			$('#status_message').text("Stopping Nodes in progress..");
			$('#status').text("Stopping");
			RemoteManager.getStatusofNodesforHost(parseInt(hostId),
					Hosts.currentOperation, Hosts.getNodesStatus);

		} else if (Hosts.currentOperation == "startNodes") {

			$('#headerspan').text("Starting Nodes");
			$('#status_message').text("Starting Nodes in progress..");
			$('#status').text("Starting");

			RemoteManager.getStatusofNodesforHost(parseInt(hostId),
					Hosts.currentOperation, Hosts.getNodesStatus);

		}

		document.getElementById('msg_td').innerHTML = "Performing operation....";
		$('#image_processing').css('display', '');
		$('#host_form_div').css('display', 'none');
		$('#host_status_div').css('display', '');

	},
	startNodes : function(flag) {
		if (flag) {
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton = ' No ';
			jConfirm("Do you want to start all the Nodes?", "Start All Nodes",
					function(confirm) {
						if (confirm) {
							var id = selectedHost[0];
							Hosts.currentOperation = "startNodes";
							// RemoteManager.getStatusofNodesforHost(id,
							// Hosts.currentOperation,
							// Hosts.getNodesStatus);
							Util.addLightbox("hostop",
									"resources/node_operation_popup.html",
									null, null);
						} else {
							return;
						}
					});
		}
	},
	stopNodes : function(flag) {
		if (flag) {
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton = ' No ';
			jConfirm("Do you want to stop all Nodes?", "Stop All Nodes",
					function(confirm) {
						if (confirm) {
							var id = selectedHost[0];
							Hosts.currentOperation = "stopNodes";
							// RemoteManager.getStatusofNodesforHost(id,
							// Hosts.currentOperation,
							// Hosts.getNodesStatus);
							Util.addLightbox("hostop",
									"resources/node_operation_popup.html",
									null, null);
						} else {
							return;
						}
					});
		}
	},
	getNodesStatus : function(response) {

		var status;
		var id = selectedHost[0];
		var finalStatus = false;
		// When no nodes are present
		if (response.length == 0) {
			$('#nodeheader').hide();
			$('#image_processing').css('display', 'none');
			$('#status').text("Nodes not configured for selected host");
			$('#status').css('color', 'red');
			var data2 = '<tr>'
					+ '<td colspan="4" style="text-align: center;">'
					+ '<input type="button" disabled="true"  class="buttonAdmin" id="deletehost.ok" value="Ok" onclick="javascript:Hosts.closeBox(true);">'
					+ '</td>' + '</tr>';
			$('#hostnodedetails').append(data2);
			document.getElementById("deletehost.ok").disabled = false;
			Navbar.refreshView();
			return;
		}
		$('#nodeheader').show();
		for (var i = 1, j = 2; i <= response.length; i++, j++) {

			var nodeArr = response[i - 1];

			var value = nodeArr.node;

			var arr = value.split(":");

			$('#node_name_span' + i).text(arr[0]);
			if ("Stopped" == arr[1]) {
				$('#headerspan').text('Stopping Nodes');
				status = "stopped";
				$('#status').text('Success');
				$('#node_status' + i).text(arr[1]);
				$('#image_success' + i).css('display', '');
				$('#image_processing' + i).css('display', 'none');
				document.getElementById('msg_td').innerHTML = "Operation performed successfully.";
				$('#msg_td').css('color', 'green');
				// finalStatus = true;

			} else if ("Started" == arr[1] || "Launching" == arr[1]) {
				$('#headerspan').text('Starting Nodes');
				status = "started";
				$('#status').text('Success');
				$('#node_status' + i).text("Started");
				$('#image_success' + i).css('display', '');
				$('#image_processing' + i).css('display', 'none');
				document.getElementById('msg_td').innerHTML = "Operation performed successfully.";
				$('#msg_td').css('color', 'green');
				// finalStatus = true;

			} else {
				// finalStatus = false;
				$('#image_fail' + i).css('display', '');
				$('#image_processing' + i).css('display', 'none');
				$('#status').text('failed');
				$('#node_status' + i).text(arr[1]);
				var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
				document.getElementById('log_div').innerHTML = log;
				document.getElementById('log_div').style.display = "block";
				document.getElementById('msg_td').innerHTML = "Operation failed.";
				$('#msg_td').css('color', 'red');
			}

			var data = '<tr>'
					+ '<td style="text-align: center;"><span id="node_name_span'
					+ j
					+ '"'
					+ '></span></td>'
					// + '<td style="text-align: center;"><span
					// id="node_message1"></span><br><div id="log_div"
					// style="display: none;"></div></td> -->
					+ '<td style="text-align: center;"><span id="node_status'
					+ j
					+ '"'
					+ '></span></td>'
					+ '<td style="text-align: center;">'
					+ '<span id="image_fail'
					+ j
					+ '"'
					+ 'style="display:none;"><img src="images/Fail_img.png" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>'
					+ '<span id="image_success'
					+ j
					+ '"'
					+ 'style="display:none;"><img  src="images/Success_img.png" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>'
					+ '<span id="image_processing'
					+ j
					+ '"'
					+ ' style="display:none;"><img  src="images/process.gif" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>'
					+ '</td>' + '</tr>';

			$('#hostnodedetails').append(data);

		}
		var data2 = '<tr>'
				+ '<td colspan = "4"><hr></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="4" style="text-align: center;">'
				+ '<input type="button" disabled="true"  class="buttonAdmin" id="deletehost.ok" value="Ok" onclick="javascript:Hosts.closeBox(true);">'
				+ '</td>' + '</tr>';
		$('#hostnodedetails').append(data2);

		var check = false;
		var countStop = 0;
		var countStart = 0;

		for (var i = 1; i <= response.length; i++) {
			if ($('#node_status' + i).text() == "Stopped"
					|| $('#node_status' + i).text() == "stopped") {
				check = true;
				countStop = countStop + 1;
			} else {
				check = false;
				countStart = countStart + 1;
			}
		}

		if (check == true && countStop < response.length
				&& Hosts.currentOperation == "stopNodes") {
			$('#status_message').text("Not All Nodes Stopped Successfully");
			$('#status').text("Failed");
			$('#image_processing').css('display', 'none');
			$('#image_success').css('display', 'none');
			$('#image_fail').css('display', '');
		} else if (Hosts.currentOperation == "stopNodes") {
			$('#status_message').text("Nodes Stopped Successfully");
			$('#status').text("Success");
			$('#image_processing').css('display', 'none');
			$('#image_fail').css('display', 'none');
			$('#image_success').css('display', '');
		}

		if (check == false && countStart < response.length
				&& Hosts.currentOperation == "startNodes") {
			$('#status_message').text("Not All Nodes Started Successfully");
			$('#status').text("Failed");
			$('#image_processing').css('display', 'none');
			$('#image_success').css('display', 'none');
			$('#image_fail').css('display', '');
		} else if (Hosts.currentOperation == "startNodes") {
			$('#status_message').text("Nodes Started Successfully");
			$('#status').text("Success");
			$('#image_processing').css('display', 'none');
			$('#image_success').css('display', '');
			$('#image_fail').css('display', 'none');
		}

		document.getElementById("deletehost.ok").disabled = false;
		Navbar.refreshView();

	}

};