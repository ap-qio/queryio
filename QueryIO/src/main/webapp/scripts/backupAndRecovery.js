BAR = {
		totalBAR : 0,
		migratedDBName : [],
		currentOperation : null,
		isFileSystem : true,
		userCache :[],
		selectedBackupId : '',
		selectedNameNodeId : '',
		selectedHostId : '',
		selectedDestPath : '',
		selectedDestDb : '',
		selectedScheduleId :'',
		selectedScheduleTime :'',
		selectedScheduleInterval :'',
		currentTabSelected :'',
		selectedDianoseid : '',
		isAutoDiagnose : true,
		
		$tabs : $("#backupandDiagnoseTab").tabs(),
		ready : function()
		{
			BAR.$tabs = $("#backupandDiagnoseTab").tabs();
			if (Navbar.currentBackupTabSelected == '')
				Navbar.currentBackupTabSelected = 'diagnoserepairDiv';
			$('#backupandDiagnoseTab').tabs({
				select: function(event, ui){
						
//						Navbar.setButtonWidth();
						Navbar.currentBackupTabSelected = ui.panel.id;
				}
			});
			BAR.$tabs.tabs("select",Navbar.currentBackupTabSelected);
			
			dwr.util.byId('restorebar').disabled=true;
			dwr.util.byId('deletebar').disabled=true;
			RemoteManager.getAllBackupLists(BAR.populateBackupAndRecoverytable);
			DiagnosisAndRepairManager.getDiagnosisStatus(BAR.populateDiagnoseTable);
			dwr.util.byId('deleteDiagnosisEntryBtn').disabled=true;
			dwr.util.byId('repairBtn').disabled=true;
			RemoteManager.getAllRestoreHistoryLists(BAR.populateRestoreHistory);
		},
		
		populateRestoreHistory : function(list)
		{
			var tableList = new Array();
			
			if ((list != null) && (list.length > 0))
	   		{
				for (var i=0;i<list.length;i++)
	   			{
					var restoreProcess = list[i];
		   			var restoreId = restoreProcess.restoreId;
					var check='<input type="checkbox" id="'+restoreId+'" onclick="javascript:BAR.clickRestoreCheckBox(this.id)">';
					
					if(restoreProcess.migrationId==null||restoreProcess.migrationId=='')
						var migrationId = '-';
					else
						var migrationId = restoreProcess.migrationId;
					
					var namenode = '<span id="namenode_'+restoreId+'">'+ restoreProcess.namenodeId+'</span>';;
					var starttime = restoreProcess.startTime;
					var endtime = restoreProcess.endTime;
					var status = '<span id="status_'+restoreId+'">'+ restoreProcess.status+'</span>';
					
					tableList.push([check, restoreId, migrationId, namenode, starttime, endtime, status]);
	   			}
		  	}
			
		   	$('#restore_table').dataTable({
		   			"bPaginate": false,
					"bLengthChange": false,
					"sPaginationType": "full_numbers",
					"bFilter": false,
					"bDestroy": true,
					"bSort": true,
					"bInfo": false,
					"bDestroy": true,
					"bAutoWidth": false,
					"aaData": tableList,
			        
			        "aoColumns": [
			            { "sTitle":'<input type="checkbox" value="" id="selectAllRT" onclick="javascript:BAR.selectAllRestoreRow(this)">'},
			            { "sTitle": 'Restore ID' },
			            { "sTitle": 'Backup ID' },
			            { "sTitle": "NameNode" },
			            { "sTitle": "Start Time" },
			            { "sTitle": "End Time" },
			            { "sTitle": "Status" }
			        ]
			    });
		   	dwr.util.byId('stopRestoreBtn').disabled=true;				
		   	dwr.util.byId('deleteRestoreBtn').disabled=true;
		   	if (list == null || list == undefined || list.length == 0)
		   	{
		   		document.getElementById('selectAllRT').disabled = true;
			}
		   	else
		   	{
		   		document.getElementById('selectAllRT').disabled = false;
		   	}	
		   	
		   	var flag = false;
		   	for(i=0; i<list.length; i++)
		   	{
		   		var restore = list[i];
		   		if(restore.status.indexOf("Completed") == -1 && restore.status.indexOf("Failed") == -1)
		   		{
		   			flag = true;
		   			break;
		   		}
		   	}
			
		   	if(flag)
		   	{
		   		var timerProcess = setTimeout(function()
		   		{
		   			RemoteManager.getAllRestoreHistoryLists(BAR.updateRestoreHistory);
				},1000);
		   	}	   		
		},
		
		updateRestoreHistory : function(list)
		{
			var flag = false;
			
			flag = BAR.updateTable(list, "restore_table", "restoreId", "status", 1, 6, "Completed", "Failed");
			
			if(flag)
			{
				var timerProcess = setTimeout(function()
		   		{
					RemoteManager.getAllRestoreHistoryLists(BAR.updateRestoreHistory);
				},1000);
			}
			else
				RemoteManager.getAllRestoreHistoryLists(BAR.populateRestoreHistory);
		},
		
		populateDiagnoseTable : function(list){
			var tableList = new Array();
			
			if ((list != null) && (list.length > 0))
	   		{
				for (var i=0;i<list.length;i++)
	   			{
					var dianoseProcess = list[i];
		   			var processId = dianoseProcess.diagnosisId;
					var check='<input type="checkbox" id="'+processId+'" onclick="javascript:BAR.clickDianoseCheckBox(this.id)">';
					var namenode = '<span id="namenode_'+processId+'">'+ dianoseProcess.namenodeId+'</span>';;
					var starttime = dianoseProcess.startTime;
					var endtime = dianoseProcess.endTime;
					var status = '<span id="status_'+processId+'">'+ dianoseProcess.status+'</span>';
					var error =	dianoseProcess.error;
					if(dianoseProcess.status.indexOf("Failed")!=-1){
						status =  "<a href=\"javascript:BAR.showError('"+processId+"','diagnoseError');\">"+dianoseProcess.status+"</a>" +
										"<div style=\"display:none;\" id=\"diagnose_error_"+processId+"\">"+error+"</div>";
					}else{
						status =  "<span id=\"status_"+processId+"\">"+ dianoseProcess.status+"</span>";	
					}
					var isRepair = dianoseProcess.isRepair;
					var repairCol = "<button class=\"button\" type=\"button\" id=\"repairBtn\" onclick=\"javascript:BAR.liveBackup('"+processId+"');\"  style=\"width:56px;\">Repair</button>";
					console.log('isRepair: ' + isRepair);
					if(isRepair==true||isRepair==1 || isRepair=='t' || isRepair == 'TRUE'){
						isRepair = true;
					}else{
						isRepair = false;
					}
					repairCol = '<span style=\"display:none;\" id="repair_status_'+processId+'">'+isRepair+'</span>'
					var logLink = "<a href=\"javascript:BAR.showLog('"+processId+"');\">View</a>"+repairCol;
					tableList.push([check, processId, namenode, starttime, endtime,logLink, status]);
	   			}
		  	}
			
		   	$('#diagnose_repair_table').dataTable( {
		   			"bPaginate": false,
					"bLengthChange": false,
					"sPaginationType": "full_numbers",
					"bFilter": false,
					"bDestroy": true,
					"bSort": true,
					"bInfo": false,
					"bDestroy": true,
					"bAutoWidth": false,
					"aaData": tableList,
			        
			        "aoColumns": [
			            { "sTitle":'<input type="checkbox" value="" id="selectAllDRT" onclick="javascript:BAR.selectAllDiagnoseRow(this)" >'},
			            { "sTitle": 'Diagnosis ID' },
			            { "sTitle": "NameNode" },
			            { "sTitle": "Start Time" },
			            { "sTitle": "End Time" },
			            { "sTitle": "Diagnosis Report" },
			            { "sTitle": "Status" }
			        ]
			    } );
		   	if (list == null || list == undefined || list.length == 0)
		   		document.getElementById('selectAllDRT').disabled = true;
		   	else
		   		document.getElementById('selectAllDRT').disabled = false;
			
		   	var flag = false;
		   	for(i=0; i<list.length; i++)
		   	{
		   		var diagnose = list[i];
		   		if(diagnose.status.indexOf("Complete") == -1 && diagnose.status.indexOf("Failed") == -1)
		   		{
		   			flag = true;
		   			break;
		   		}
		   	}
		   	
		   	if(flag)
		   	{
		   		var timerProcess = setTimeout(function()
		   		{
		   			DiagnosisAndRepairManager.getDiagnosisStatus(BAR.updateDiagnoseTable);
				},1000);
		   	}
		},
		
		updateDiagnoseTable : function(list)
		{
			var flag = false;
			
			flag = BAR.updateTable(list, "diagnose_repair_table", "diagnosisId", "status", 1, 6, "Complete", "Failed");
			
			if(flag)
			{
				var timerProcess = setTimeout(function()
		   		{
					DiagnosisAndRepairManager.getDiagnosisStatus(BAR.updateDiagnoseTable);
				},1000);
			}
			else
				DiagnosisAndRepairManager.getDiagnosisStatus(BAR.populateDiagnoseTable);
		},
		
		populateBackupAndRecoverytable : function(list)
		{
			var tableList = new Array();
			
			if ((list != null) && (list.length > 0))
	   		{
				for (var i=0;i<list.length;i++)
	   			{
		   			var backupProcess = list[i];
		   			var processId = backupProcess.migrationId;
					var check='<input type="checkbox" id="'+processId+'" onclick="javascript:BAR.clickCheckBox(this.id)">';
					var namenode = backupProcess.namenodeId;
					var hostId = backupProcess.hostId;
					var host = backupProcess.hostIP;
					var path = backupProcess.backupFolder;
					host = host+path;
					var database = backupProcess.dbName;
					var starttime = backupProcess.startTime;
					var endtime = backupProcess.endTime;
					var status = "";
					var error =	backupProcess.error;
					if(error !=null && error.indexOf('No such file or directory') > 0){
						error = error + ": Permission denied.";
					}
					if(backupProcess.status.indexOf("Failed")!=-1){
						status =  "<a href=\"javascript:BAR.showError('"+processId+"','backupandRecoveryError');\">"+ backupProcess.status+"</a><div style=\"display:none;\" id=\"backup_error_"+processId+"\">"+error+"</div>";
					}else{
						status =  "<span id=\"status_"+processId+"\">"+ backupProcess.status+"</span>";	
					}
					
					var backupType = 'File System';
					var backupLoc = '';
					if ((database!='' && host=='') || (hostId == -1)) {
						backupType = 'Database'
						backupLoc = database;
					} else {
						backupType = 'File System'
						backupLoc = host;
					}

					tableList.push([check, processId, namenode, backupType, backupLoc, starttime, endtime, status]);
	   			}
		  	}
			
		   	$('#backup_recovery_table').dataTable( {
		   			"bPaginate": false,
					"bLengthChange": false,
					"sPaginationType": "full_numbers",
					"bFilter": false,
					"bDestroy": true,
					"bSort": true,
					"bInfo": false,
					"bDestroy": true,
					"bAutoWidth": false,
					"aaData": tableList,
			        
			        "aoColumns": [
			            { "sTitle":'<input type="checkbox" value="" id="selectAllR" onclick="javascript:BAR.selectAllRow(this)" >'},
			            { "sTitle": 'Backup ID' },
			            { "sTitle": "NameNode" },
			            { "sTitle": "Backup Type" },
			            { "sTitle": "Backup Location" },
			            { "sTitle": "Start Time" },
			            { "sTitle": "End Time" },
			            { "sTitle": "Status" }
			            
			        ]
			    } );
		   	if (list == null || list == undefined || list.length == 0)
		   		document.getElementById('selectAllR').disabled = true;
		   	else
		   		document.getElementById('selectAllR').disabled = false;
		   		
		   	var flag = false;
		   	for(i=0; i<list.length; i++)
		   	{
		   		var restore = list[i];
		   		if(restore.status.indexOf("Completed") == -1 && restore.status.indexOf("Failed") == -1)
		   		{
		   			flag = true;
		   			break;
		   		}
		   	}
			
		   	if(flag)
		   	{
		   		var timerProcess = setTimeout(function()
		   		{
		   			RemoteManager.getAllBackupLists(BAR.updateBackupAndRecoverytable);
				},1000);
		   	}
		},
		
		updateBackupAndRecoverytable : function(list)
		{
			var flag = false;
			
			flag = BAR.updateTable(list, "backup_recovery_table", "migrationId", "status", 1, 7, "Completed", "Failed");
			
			if(flag)
			{
				var timerProcess = setTimeout(function()
		   		{
		   			RemoteManager.getAllBackupLists(BAR.updateBackupAndRecoverytable);
				},1000);
			}
			else
				RemoteManager.getAllBackupLists(BAR.populateBackupAndRecoverytable);
		},
		
		updateTable : function(list, tableName, paramId, paramStatus, idPos, statusPos, stopCondition1, stopCondition2)
		{
			var flag = false;
			var count = 0;
			$("#" + tableName + " tbody tr").each(function()
			{
				var row = this.cells;
				
				var status = row[parseInt(statusPos)].innerText;
				var id = row[parseInt(idPos)].innerHTML;
				
				var oTable = $("#" + tableName).dataTable();
				
				for(i=0; i<list.length; i++)
				{
					var idFetched = "";
					var statusFetched = "";
					
					if(paramId == "migrationId")
						idFetched = list[i].migrationId;
					else if(paramId == "diagnosisId")
						idFetched = list[i].diagnosisId;
					else if(paramId == "restoreId")
						idFetched = list[i].restoreId;
					
					if(paramStatus == "status")
						statusFetched = list[i].status;
					
					if(id == idFetched)
					{
						var condition = false;
						if(stopCondition2 != null)
							condition = (statusFetched.indexOf(stopCondition1) == -1 && statusFetched.indexOf(stopCondition2) == -1);
						else
							condition = (statusFetched.indexOf(stopCondition1) == -1);
						
						if(condition)
						{
							oTable.fnUpdate(statusFetched, count, parseInt(statusPos));
							flag = true;
						}
					}
				}
				count ++;
			});
			
			return flag;
		},
		
		clickRestoreCheckBox : function(id){
			var serviceId=id;

			
			if(dwr.util.byId(serviceId).checked)
			{
				if(selectedRestore.indexOf(serviceId)==-1)
					selectedRestore.push(serviceId);
			}
			else
			{
				var index = selectedRestore.indexOf(serviceId);
				selectedRestore.splice(index, 1);
			}
			if(selectedRestore.length>0)
			{
				dwr.util.byId('deleteRestoreBtn').disabled=false;
				
				if(selectedRestore.length == 1)
					BAR.disableEnableStopRestore();
				else
					dwr.util.byId('stopRestoreBtn').disabled = true;
				
			}else{
				
				dwr.util.byId('stopRestoreBtn').disabled=true;				
				dwr.util.byId('deleteRestoreBtn').disabled=true;
			}
			//To enable select all
			if($("#restore_table tr").length - 1  == selectedRestore.length)
			{
				document.getElementById('selectAllRT').checked = dwr.util.byId(serviceId).checked;
			}
			if($("#restore_table tr").length-2 == selectedRestore.length && dwr.util.byId(serviceId).checked == false)
			{
				document.getElementById('selectAllRT').checked = dwr.util.byId(serviceId).checked;
			} 	

		},
		
		disableEnableStopRestore : function()
		{
			var flag = false;
			$("#restore_table tbody tr").each(function()
			{
				var row = this.cells;
				var status = row[6].innerText;
				var id = row[1].innerHTML;
				
				if(id == selectedRestore[0])
				{
					if(status.indexOf("Completed") == -1 && status.indexOf("Failed") == -1)
						flag = true;
				}
			});
			
			if(flag)
				dwr.util.byId('stopRestoreBtn').disabled = false;
			else
				dwr.util.byId('stopRestoreBtn').disabled = true;
		},
		
		clickDianoseCheckBox : function(id){
			var serviceId=id;

			
			if(dwr.util.byId(serviceId).checked)
			{
				if(selectedDAR.indexOf(serviceId)==-1)
					selectedDAR.push(serviceId);
			}
			else
			{
				var index = selectedDAR.indexOf(serviceId);
				selectedDAR.splice(index, 1);
			}
			if(selectedDAR.length>0){
				dwr.util.byId('deleteDiagnosisEntryBtn').disabled=false;
				
				if(selectedDAR.length==1){
					if($('#repair_status_'+selectedDAR[0]).text()=='true'||$('#repair_status_'+selectedDAR[0]).text()==true){
						dwr.util.byId('repairBtn').disabled=false;	
					}else{
						dwr.util.byId('repairBtn').disabled=true;
					}
				}else{
					dwr.util.byId('repairBtn').disabled=true;	
				}
			}else{
				dwr.util.byId('deleteDiagnosisEntryBtn').disabled=true;
				dwr.util.byId('repairBtn').disabled=true;
			}
			//To enable select all
			if($("#diagnose_repair_table tr").length - 1  == selectedDAR.length)
			{
				document.getElementById('selectAllDRT').checked = dwr.util.byId(serviceId).checked;
			} 
			if($("#diagnose_repair_table tr").length-2 == selectedDAR.length && dwr.util.byId(serviceId).checked == false)
			{
				document.getElementById('selectAllDRT').checked = dwr.util.byId(serviceId).checked;
			} 	

		},
		clickCheckBox : function(id){
						
			var serviceId=id;

			
			if(dwr.util.byId(serviceId).checked)
			{
				if(selectedBAR.indexOf(serviceId)==-1)
					selectedBAR.push(serviceId);
			}
			else
			{
				var index = selectedBAR.indexOf(serviceId);
				selectedBAR.splice(index, 1);
			}
			if(selectedBAR.length>0){
				dwr.util.byId('deletebar').disabled=false;
				if(selectedBAR.length==1)
					BAR.enableDisableRestoreButton();
				else
					dwr.util.byId('restorebar').disabled=true;
			}else{
				dwr.util.byId('deletebar').disabled=true;
				dwr.util.byId('restorebar').disabled=true;
			}
			//To enable select all
			if($("#backup_recovery_table tr").length - 1  == selectedBAR.length)
			{
				document.getElementById('selectAllR').checked = dwr.util.byId(serviceId).checked;
			}
			if($("#backup_recovery_table tr").length-2 == selectedBAR.length && dwr.util.byId(serviceId).checked == false)
			{
				document.getElementById('selectAllR').checked = dwr.util.byId(serviceId).checked;
			} 
		},
		
		enableDisableRestoreButton : function()
		{
			var flag = false;
			$("#backup_recovery_table tbody tr").each(function()
			{
				var row = this.cells;
				var status = row[7].innerText;
				var id = row[1].innerHTML;
				
				if(id == selectedBAR[0])
				{
					if(status.indexOf("Completed") != -1)
						flag = true;
				}
			});
			
			if(flag)
				dwr.util.byId('restorebar').disabled = false;
			else
				dwr.util.byId('restorebar').disabled = true;
		},
		
		restore : function()
		{
			BAR.selectedBackupId = selectedBAR[0];
			BAR.currentOperation = 'restore' 
			Util.addLightbox("bar_Box", "resources/restore_entry.html", null, null);
		},
		
		scheduleBackup : function()
		{
			BAR.currentOperation = 'schedule';
			Util.addLightbox('bar_Box','resources/schedule_backup_entry.html');
		},
		
		deleteBackup : function(){
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm('Are you sure you want to delete backup?','',function(val){
				if (val== true){
					BAR.currentOperation = 'delete' 
					Util.addLightbox("bar_Box", "pages/popup.jsp", null, null);
				}else{
					return ;
				}
				jQuery.alerts.okButton = ' Ok ';
				jQuery.alerts.cancelButton  = 'Cancel';
				});
			$("#popup_container").css("z-index","99999999");
			
		},
		liveBackup : function(){
			BAR.selectedDianoseid = selectedDAR[0]; 
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm('Are you sure you want to proceed with repair?','',function(val){
				if (val== true){
					BAR.currentOperation = 'repair';
					Util.addLightbox("bar_Box", "pages/popup.jsp", null, null);
				}else{
					return ;
				}
				jQuery.alerts.okButton = ' Ok ';
				jQuery.alerts.cancelButton  = 'Cancel';
				});
			$("#popup_container").css("z-index","99999999");
			
			
		},
				
		addNewBackupEntry : function()
		{
			BAR.currentOperation = 'add';
			Util.addLightbox('bar_Box','resources/add_backup_entry.html');
		},
		handleSchedulerResponse : function(dwrResponse){
			
			if (dwrResponse.taskSuccess)
			{
				status = "Success";
				imgId = "imageSuccess";
			}
			else
			{
				status = "Failure";
				imgId = "imageFail";
			}

			message = dwrResponse.responseMessage;
		
			dwr.util.byId('imageProcessing').style.display = 'none';
			dwr.util.byId(imgId).style.display = '';
		dwr.util.setValue('message', message);
		dwr.util.setValue('status', status);
		dwr.util.byId('okpopup').disabled = false;
			
		},
		createScheduleBackupEntry : function(isNotified){
			
			var interval = dwr.util.byId('interval').value;
			var scheduleTime = dwr.util.byId('scheduleTime').value;
			var namenodeId = dwr.util.byId('nameNodeId').value;
			var startIndex = 0;
			var endIndex = 9999;
			var scheduleName = dwr.util.byId('schedID').value;
			var notificationType = '';
			var notificationMessage = '';
			var userList = null;
			var isNotificationEnable = false;
			
//			SchedulerManager.scheduleNamespaceDiagnosis(String interval, String scheduleTime, String namenodeId, long startIndex, long endIndex, 
//					String scheduleName,String notificationType, String notificationMessage,ArrayList userList)
			
			
			
			if(isNotified){
				RemoteManager.scheduleNamespaceDiagnosis(interval, scheduleTime, namenodeId, startIndex, endIndex, scheduleName, isNotificationEnable, notificationType, notificationMessage, userList, BAR.handleSchedulerResponse );
			}else{	
			var valid = true;
			if ( dwr.util.byId('schedID').value == '')
			{
				valid = false;
				jAlert("Schedule Id was not specified. Please provide a valid Unique Schedule Identifier to schedule new backup entry.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
			}
			if (dwr.util.byId('backupId').value == '')
			{
				valid = false;
				jAlert("Backup Id Unique Identifier was not specified. Please provide a valid Unique Identifier to add new backup entry.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
			}
			else if (dwr.util.byId('nameNodeId').value == "Select NameNode")
			{
				valid = false;
				jAlert("No NameNode Id Selected","Incomplete Detail");
				$("#popup_container").css("z-index","9999999");
			}
			else if ( dwr.util.byId('scheduleTime').value =='')
			{
				valid = false;
				jAlert("Schedule Time was not specified. Please specify schedule time.","Incomplete Detail");
				$("#popup_container").css("z-index","9999999");
			}
			else
			{
				if (BAR.isFileSystem)
				{
					if (dwr.util.byId('hostForNode').value == 0)
					{
						valid = false;
						jAlert("No Host Selected","Incomplete Detail");
						$("#popup_container").css("z-index","9999999");
					}
					else if (dwr.util.byId('dirPath').value == '')
					{
						valid = false;
						jAlert("Directory Path was not specified. Please provide a valid Path to store backup files.","Incomplete detail");
						$("#popup_container").css("z-index","9999999");
					}
				}
				else
				{
					if (dwr.util.byId('destinationDb').value == 0)
					{
						valid = false;
						jAlert("Destination Database was not specified.","Incomplete detail");
						$("#popup_container").css("z-index","9999999");
					}
				}
			}
			
			if (valid)
			{
				BAR.selectedBackupId = dwr.util.byId('backupId').value;
				BAR.selectedNameNodeId = dwr.util.byId('nameNodeId').value;
				$('#backupPopId').text(dwr.util.byId('backupId').value);
				$('#popupDiv').show();
				$('#reportdiv1').hide();
				$('#reportdiv3').hide();
				$('#reportdiv2').hide();
//				var obj = {"id":"","responseCode":200,"responseMessage":"Backup scheduled successfully","taskSuccess":true};
//				BAR.handleSchedulerResponse(obj); 
				
				RemoteManager.scheduleNamespaceDiagnosis(interval, scheduleTime, namenodeId, startIndex, endIndex, scheduleName, isNotificationEnable, notificationType, notificationMessage, userList, BAR.handleSchedulerResponse );
				console.log(interval, scheduleTime, namenodeId, startIndex, endIndex, scheduleName, isNotificationEnable, notificationType, notificationMessage, userList);
			}
			 
		}
			
			
		},
		saveBackupEntry : function()
		{
			var isSchedule = false;
			if (BAR.currentOperation == 'schedule'){
				isSchedule = true;
			}
				
			var valid = true;
			if (isSchedule && dwr.util.byId('schedID').value == '')
			{
				valid = false;
				jAlert("Schedule Id was not specified. Please provide a valid Unique Schedule Identifier to schedule new backup entry.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
			}
			if (dwr.util.byId('backupId').value == '')
			{
				valid = false;
				jAlert("Backup Id Unique Identifier was not specified. Please provide a valid Unique Identifier to add new backup entry.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
			}
			else if (dwr.util.byId('nameNodeId').value == "Select NameNode")
			{
				valid = false;
				jAlert("No NameNode Id Selected","Incomplete Detail");
				$("#popup_container").css("z-index","9999999");
			}
			else if (isSchedule && dwr.util.byId('scheduleTime').value =='')
			{
				valid = false;
				jAlert("Schedule Time was not specified. Please specify schedule time.","Incomplete Detail");
				$("#popup_container").css("z-index","9999999");
			}
			else
			{
				if (BAR.isFileSystem)
				{
					if (dwr.util.byId('hostForNode').value == 0)
					{
						valid = false;
						jAlert("No Host Selected","Incomplete Detail");
						$("#popup_container").css("z-index","9999999");
					}
					else if (dwr.util.byId('dirPath').value == '')
					{
						valid = false;
						jAlert("Directory Path was not specified. Please provide a valid Path to store backup files.","Incomplete detail");
						$("#popup_container").css("z-index","9999999");
					}
				}
				else
				{
					if (dwr.util.byId('destinationDb').value == 0)
					{
						valid = false;
						jAlert("Destination Database was not specified.","Incomplete detail");
						$("#popup_container").css("z-index","9999999");
					}
				}
			}
			
			if (valid)
			{
				BAR.selectedBackupId = dwr.util.byId('backupId').value;
				BAR.selectedNameNodeId = dwr.util.byId('nameNodeId').value;
				
				if (isSchedule)
				{
//					BAR.selectedDestPath = dwr.util.byId('dirPath').value;
//					BAR.selectedDestDb  = dwr.util.byId('destinationDb').value;
//					BAR.selectedScheduleId =dwr.util.byId('schedID').value;
//					BAR.selectedScheduleTime  = dwr.util.byId('scheduleTime').value;
//					BAR.selectedScheduleInterval = dwr.util.byId('interval').value;
				}
				else
				{
					if (BAR.isFileSystem)
					{
						BAR.selectedDestPath = dwr.util.byId('dirPath').value;
						BAR.selectedHostId = dwr.util.byId('hostForNode').value;
					}
					else
					{
						BAR.selectedDestDb = dwr.util.byId('destinationDb').value;
					}
					BAR.isAutoDiagnose =$('#autoDiagnose').is(':checked');
						
				}
				
//				var sid = dwr.util.byId('schedID').value;
//				var bid  = dwr.util.byId('backupId').value ;
//				var nnid = dwr.util.byId('nameNodeId').value ;
//				var hostid = dwr.util.byId('hostForNode').value;
//				var scheduleTime = dwr.util.byId('scheduleTime').value;
//				var interval = dwr.util.byId('interval').value;
//				var dirPath = dwr.util.byId('dirPath').value == ''
//					var ddb = dwr.util.byId('destinationDb').value;
//				if(isSchedule){
//					//call for schedule
//					
//				}else{
//					//call for add.
//				}
				
				Util.removeLightbox('bar_Box');
				Util.addLightbox('bar_Box','pages/popup.jsp');
			}
		},
		handleRepairResponse : function(response){
			var obj = {};
			if(response == true){
				 obj = {"id":"","responseCode":200,"responseMessage":"Repair operation performed successfully","taskSuccess":true};
				
			}else{
				
				obj = {"id":"","responseCode":500,"responseMessage":"Repair process failed.","taskSuccess":false};
			}
			BAR.saveResponseCallback(obj);
		},
		saveResponseCallback : function(dwrResponse)
		{
			var message;
			var status;
			var imgId;
			var id = "Backup";
			
			if ((dwrResponse != null) && (dwrResponse != undefined))
			{
				if (dwrResponse.taskSuccess)
				{
					status = "Success";
					imgId = "popup.image.success";
				}
				else
				{
					status = "Failure";
					imgId = "popup.image.fail";
				}

				message = dwrResponse.responseMessage;
			}
			else
			{
				message = "Failed to start backup process.";
				status = "Failure";
				imgId = "popup.image.fail";
				var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
				document.getElementById('log_div'+ id).innerHTML=log;
				document.getElementById('log_div'+ id).style.display="block";
			}
			
			dwr.util.byId('popup.image.processing'+id).style.display = 'none';
			dwr.util.byId(imgId+id).style.display = '';
			
			dwr.util.setValue('popup.message'+id, message);
			dwr.util.setValue('popup.status'+id, status);
			dwr.util.byId('ok.popup').disabled = false;
		},
		
		fillClusterNameNodeID : function()
		{
			var date= new Date();

			$('#backupId').val('backup_'+date.getDate()+"_"+(date.getMonth()+1)+"_"+date.getFullYear()+"_"+date.getHours()+"_"+date.getMinutes()+"_"+date.getSeconds());
			RemoteManager.getAllNameNodeForDBNameMapping(BAR.populateClusterNameNodeIds);
		},
		
		populateClusterNameNodeIds : function(list)
		{
			var data='';
			
			data = Util.getCurrentIdDropDown(list, "Select NameNode", "Select NameNode");
			
			$('#nameNodeId').html(data);
			
			if(BAR.currentOperation == 'restore' || BAR.currentOperation == 'createFSImage')
			{
				return;
			}
			
			BAR.fillHostNames();
		},
		
		fillHostNames : function()
		{
			RemoteManager.getAllHostDetails(BAR.populateHostList);
		},
		
		populateHostList : function(list)
		{
			var data='';
			
			if(list == null || list == undefined || list.length == 0)
				data='<option value="Select Host">Select Host</option>';
			else
			{
				for(var i=0;i<list.length;i++)
				{
					var node = list[i];
					data+='<option value="'+node.id+'">'+node.hostIP+'</option>';
				}				
			}
			
			$('#hostForNode').html(data);
			BAR.fillDBNamesForNameNode();
		},
		
		fillDBNamesForNameNode : function()
		{
			RemoteManager.getAllDbName(true, BAR.populateDbDropDown);
		},

		getMigratedDBNamesList : function(list)
		{
			BAR.migratedDBName = list;
			RemoteManager.getAllConnectionsNameForNameNode(true, BAR.populateDbDropDown);
		},

		populateDbDropDown : function(list)
		{
			var data ='<option value="">Select Database</option>';
			if (list!=null && list!=undefined)
			{
				list.sort();
//				for (var item in list)
//				{
//					for (var j=0;j<BAR.migratedDBName.length;j++){
//						var index = BAR.migratedDBName[j].indexOf(list[item]);
//						if (index!=-1 && BAR.migratedDBName[j][2]=='RUNNING')
//						{
//							if (list.indexOf(list[item])!=-1)
//								list.splice(list.indexOf(list[item]),1);
//						}
//					}
//				}
				for (var item in list)
				{
					data +='<option value="'+list[item]+'">'+list[item]+'</option>';
				}
			}
			
			$('#destinationDb').html(data);
		},

		changeOptions : function(value)
		{
			if (value == "FileSystem")
			{
				BAR.isFileSystem = true;
				dwr.util.byId('hostRow').style.display = '';
				dwr.util.byId('showDirPath').style.display = '';
				dwr.util.byId('showDestDb').style.display = 'none';
			}
			else if (value == "Database")
			{
				BAR.isFileSystem = false;
				dwr.util.byId('hostRow').style.display = 'none';
				dwr.util.byId('showDirPath').style.display = 'none';
				dwr.util.byId('showDestDb').style.display = '';
			}
		},
		
		closeBox: function(isRefresh)
		{
			Util.removeLightbox('bar_Box');
			BAR.currentOperation = null;
			BAR.selectedBackupId = '';
			BAR.selectedNameNodeId = '';
			BAR.selectedHostId = '';
			BAR.selectedDestPath = '';
			BAR.selectedDestDb = '';
		
			if (isRefresh)
				Navbar.refreshView();
		},
		
		selectAllRestoreRow : function(element){
			var val = element.checked;
			for (var i=0;i<document.forms[2].elements.length;i++)
		 	{
		 		var e=document.forms[2].elements[i];
		 		if ((e.id != 'selectAllRT') && (e.type=='checkbox'))
		 		{
		 				e.checked=val;
		 				BAR.clickRestoreCheckBox(e.id);
		 		}
		 	}
		},
		
		selectAllDiagnoseRow : function(element){
			var val = element.checked;
			for (var i=0;i<document.forms[0].elements.length;i++)
		 	{
		 		var e=document.forms[0].elements[i];
		 		if ((e.id != 'selectAllDRT') && (e.type=='checkbox'))
		 		{
		 				e.checked=val;
		 				BAR.clickDianoseCheckBox(e.id);
		 		}
		 	}
		},
		
		selectAllRow :function (element)
		{
			var val = element.checked;
			for (var i=0;i<document.forms[1].elements.length;i++)
		 	{
		 		var e=document.forms[1].elements[i];
		 		if ((e.id != 'selectAllR') && (e.type=='checkbox'))
		 		{
		 				e.checked=val;
		 				BAR.clickCheckBox(e.id);
		 		}
		 	}
		},
		
		restoreBackupEntry : function(){
			
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm('Are you sure you want to create the Namenode\'s FSImage using the selected backed-up?','',function(val){
				if (val == true){
					
					BAR.selectedNameNodeId = $('#nameNodeId').val();
					
					Util.removeLightbox('bar_Box');
					Util.addLightbox('bar_Box','pages/popup.jsp');
				}else{
					return ;
				}
				jQuery.alerts.okButton = ' Ok ';
				jQuery.alerts.cancelButton  = 'Cancel';
				});
			$("#popup_container").css("z-index","99999999");
		},
		
		repairBackupEntry : function(){
		},
		diagnoseNameNode : function(){
			if (dwr.util.byId('backupId').value == "")
			{
				jAlert("Diagnosis Id Unique Identifier was not specified. Please provide a valid Unique Identifier to add new diagnosis entry.","Incomplete Detail");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			else if (dwr.util.byId('nameNodeId').value == "Select NameNode")
			{
				jAlert("No NameNode Id Selected","Incomplete Detail");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			else if (dwr.util.byId('stopDiagnose').checked && dwr.util.byId('nconflict').value == "")
			{
				jAlert("Number of conflicts was not specified.","Incomplete Detail");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			var userName = Util.getLoggedInUserName();
			DiagnosisAndRepairManager.diagnose($('#backupId').val(),$('#nameNodeId').val(),0,999999,userName,BAR.handleDiagnoseResponse);
		},
		handleDiagnoseResponse : function(response){
			
			if(response)
				jAlert("Diagnosis process started successfully","Success");
			else
				jAlert("Diagnosis process failed ","Failed");
			
			BAR.closeBox(true);
		},
		diagnoseBackup : function(){
			BAR.currentOperation = 'diagnose';
			Util.addLightbox('bar_Box','resources/diagnose_nn.html');
		},
		
		createFSImage : function(){
			BAR.currentOperation = 'createFSImage';
			Util.addLightbox('bar_Box','resources/create_fs_image.html');
		},
		
		createFSImageForNN : function(){
			if (dwr.util.byId('nameNodeId').value == "Select NameNode")
			{
				jAlert("No NameNode Id Selected","Incomplete Detail");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm('Are you sure you want to create the Namenode\'s FSImage using currently configured metadata DB?','',function(val){
				if (val == true){
					
					BAR.selectedNameNodeId = $('#nameNodeId').val();
					
					Util.removeLightbox('bar_Box');
					Util.addLightbox('bar_Box','pages/popup.jsp');					
				}else{
					return ;
				}
				jQuery.alerts.okButton = ' Ok ';
				jQuery.alerts.cancelButton  = 'Cancel';
				});
			$("#popup_container").css("z-index","99999999");
		},
		
		deleteDiagnoseEntry : function(){
			
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm('Are you sure you want to delete selected diagnosis entries?','',function(val){
				if (val== true){
					BAR.currentOperation = 'deleteDiagnosis' 
					Util.addLightbox("bar_Box", "pages/popup.jsp", null, null);
				}else{
					return ;
				}
				jQuery.alerts.okButton = ' Ok ';
				jQuery.alerts.cancelButton  = 'Cancel';
				});
			$("#popup_container").css("z-index","99999999");
			
		},
		showLog : function(id){
			BAR.selectedDianoseid = id; 
			Util.addLightbox('bar_Box','resources/conflict_log.html');
		},
		showError : function(id,operation){
			BAR.selectedDianoseid = id;
			BAR.currentOperation=operation
			Util.addLightbox('bar_Box','resources/view_error.html');
			
		},
		populateConflictTable : function(){

			jQuery("#conflict_browser_table")
					.jqGrid(
							{
								url : 'getDiagnosisReport.do?diagnosisId='+BAR.selectedDianoseid,
								datatype : "json",
								colNames : [
										'File Path', 'Conflict'],
								colModel : [  {
									name : 'File Path',
									index : 'File Path',
									width :$("#jqgrid_container").width()/2,
									sortable : true,
									resizable : true
								}, {
									name : 'Conflict',
									index : 'Conflict',
									width :$("#jqgrid_container").width()/2,
									sortable : true,
									resizable : true
								}
								],
								altRows : false,
								onSelectRow : function(id) {
									
								},
								rowNum : 10,
								rowList : [ 10, 50, 100, 500, 1000 ],
								shrinkToFit : true,
								viewrecords : true,
								pager : "#pager",
//								height : $("#jqgrid_container").height(),
//								width : ($("#jqgrid_container").width() - 3),
								sortable : true,
								multiselect : false,
								caption : "",
								onSortCol : function(index, idxcol, sortorder) {
									// data_analyzer_table");
									if (this.p.lastsort >= 0
											&& this.p.lastsort !== idxcol
											&& this.p.colModel[this.p.lastsort].sortable !== false) {
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
							}).navGrid('#pager', {
						add : false,
						edit : false,
						del : false,
						search : false,
						refresh : false
					});
			$('#jqgh_CheckBox').removeClass("ui-jqgrid-sortable");
			$('#alertmod').remove();
			$('#pager_left').remove();
			$('#pager_center').css('width', ($('#jqgrid_container').width() - 150));
			$('#pager_center :input[type=text]').css('width', '20%');
		},
		hadleDeleteDiagnose : function(dwrResponse){

			var message;
			var status;
			var imgId;
			var id = dwrResponse.id;
			
			if ((dwrResponse != null) && (dwrResponse != undefined))
			{
				if (dwrResponse.taskSuccess)
				{
					status = "Success";
					imgId = "popup.image.success";
				}
				else
				{
					status = "Failure";
					imgId = "popup.image.fail";
				}

				message = dwrResponse.responseMessage;
			}
			else
			{
				message = "Failed to delete diagnose entry.";
				status = "Failure";
				imgId = "popup.image.fail";
				var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
				document.getElementById('log_div'+ id).innerHTML=log;
				document.getElementById('log_div'+ id).style.display="block";
			}
			
			dwr.util.byId('popup.image.processing'+id).style.display = 'none';
			dwr.util.byId(imgId+id).style.display = '';
			
			dwr.util.setValue('popup.message'+id, message);
			dwr.util.setValue('popup.status'+id, status);
			dwr.util.byId('ok.popup').disabled = false;
		},
		
		stopRestore : function()
		{
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm('Are you sure you want to stop selected operation?','Stop Operation',function(val)
			{
				if (val== true)
				{
					BAR.currentOperation = 'stop'; 
					Util.addLightbox("bar_Box", "pages/popup.jsp", null, null);
				}
				else
				{
					return;
				}
				jQuery.alerts.okButton = ' Ok ';
				jQuery.alerts.cancelButton  = 'Cancel';
			});
			$("#popup_container").css("z-index","99999999");
		},
		deleteRestore : function()
		{
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm('Are you sure you want to delete selected entries?','Delete Restore History',function(val)
			{
				if (val== true)
				{
					BAR.currentOperation = 'deleteHistory'; 
					Util.addLightbox("bar_Box", "pages/popup.jsp", null, null);
				}
				else
				{
					return;
				}
				jQuery.alerts.okButton = ' Ok ';
				jQuery.alerts.cancelButton  = 'Cancel';
			});
			$("#popup_container").css("z-index","99999999");
		},
		
		showConflict : function(divId)
		{
			var rowJSON = $("#" + divId).html();
			var conflictInfo = $.parseJSON(rowJSON);
			if(conflictInfo == null)
			{
				$("#conflict_detail_table").empty();
				return;
			}
			var tableContent = "<thead><tr><th></th>";
			for(var i = 0; i < conflictInfo.length; i++)
			{
				tableContent += "<th>" + conflictInfo[i].columnName + "</th>";
			}
			tableContent += "</tr></thead><tbody><tr><td>Expected Value</td>";
			for(var i = 0; i < conflictInfo.length; i++)
			{
				tableContent += "<td>" + conflictInfo[i].expectedValue + "</td>";
			}
			tableContent += "</tr><tr><td>Found Value</td>";
			for(var i = 0; i < conflictInfo.length; i++)
			{
				tableContent += "<td>" + conflictInfo[i].foundValue + "</td>";
			}
			tableContent += "</tr></tbody>";
			
			$("#conflict_detail_table").html(tableContent);
		},
		addUser: function(list){
			if(list!=null){
				if(BAR.userCache.length==0){
					for(var i=0;i<list.length;i++){
							user=list[i];
							BAR.userCache.push(user);
							$('#user').append('<option value="'+user.id+'">'+user.firstName+' '+user.lastName+'</option>');
						}
					}
				}
			},
			findUser: function(val){
				for(var i=0;i<BAR.userCache.length;i++){
					var user=BAR.userCache[i];
					if(user.id==val)
						return user.firstName+' '+user.lastName;
				}
			}
};