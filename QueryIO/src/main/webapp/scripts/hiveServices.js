HiveServices = {
		
	tableData : [],
	serviceSelected : '',
	
	selectedServicesList : [],
	hostsList : [],
	nameNodeStatus : {},
	
	selectedHost : null,
	clickOnStart : false,
	
	nameNodeOverAllStatusMap : {},
	timer : [],
	isSetTimer : false,
	
	ready : function()
	{
		HiveServices.checkEnableDisable();
		RemoteManager.getNameNodes(HiveServices.getAllNameNodeDetails);
	},
	
	fillHostList : function(list)
	{
		HiveServices.hostsList = list;
		QueryIOServicesManager.getAllServices(true, HiveServices.fillTable);
	},
	
	getAllNameNodeDetails : function(nameNodeDetails)
	{
		for(var i = 0 ; i < nameNodeDetails.length; i++)
		{
			HiveServices.nameNodeOverAllStatusMap[nameNodeDetails[i].id] = nameNodeDetails[i].status; 
		}
		RemoteManager.getAllHostDetails(HiveServices.fillHostList);
	},
	
	fillTable : function(summaryTable)
	{
		var totalNameNodeCount = 0;
		HiveServices.isSetTimer = false;
		var nameNodeIDs = [];
		if(document.getElementById('hive_services_table')=='undefined'||document.getElementById('hive_services_table')==null)
			return;
		
		var tableData = "<thead><tr>";
		for(var i = 0; i < summaryTable.colNames.length; i++)
		{
			tableData += "<th style = 'width: 20%;'>" + summaryTable.colNames[i] + "</th>";
		}
		tableData += "<th>Server Log</th>";
		tableData += "</tr></thead><tbody>";
		if(summaryTable.rows == null || summaryTable.rows == undefined || summaryTable.rows == "")
		{
			tableData += "<tr><td style = 'text-align: center; font-size: 9pt;' colspan = '" +  (summaryTable.colNames.length + 1) + "'>No Data available</td></tr>";
		}
		else
		{
			var currentNameNode = "";
			for(var i = 0; i < summaryTable.rows.length; i++)
			{
				if(currentNameNode != summaryTable.rows[i][0])
				{
					currentNameNode = summaryTable.rows[i][0];
					
					nameNodeIDs.push(currentNameNode);
					// Name node having expand feature
					tableData += "<tr id = 'nameNode-" + currentNameNode + "' class = 'nameNodeRow-" + totalNameNodeCount + "'>";
					tableData += "<td>";
					if(HiveServices.nameNodeOverAllStatusMap[currentNameNode] != 'Started')
					{
						tableData += '<input type="checkbox" disabled = "disabled" style = "" value="service-1" onClick="javascript:HiveServices.clickCheckBox(this.id)" id="service_' + currentNameNode + '" >';
						tableData += '<span class = "expandNamenode-' + totalNameNodeCount + '" style = "cursor: pointer;" >' + summaryTable.rows[i][0] + "</span></td><td style = 'color: #3083D0;' colspan = '" + (summaryTable.rows[i].length) + "'>This NameNode is " + HiveServices.nameNodeOverAllStatusMap[currentNameNode] + ". Please start NameNode to start/stop Hive Service.</td>";
					}
					else
					{
						tableData += '<input type="checkbox" style = "" value="service-1" onClick="javascript:HiveServices.clickCheckBox(this.id)" id="service_' + currentNameNode + '" >';
						tableData += '<span class = "expandNamenode-' + totalNameNodeCount + '" style = "cursor: pointer;" >' + summaryTable.rows[i][0] + "</td><td colspan = '" + (summaryTable.rows[i].length) + "'></td>";
					}
					tableData += "</tr>";
					totalNameNodeCount ++;
					HiveServices.nameNodeStatus[currentNameNode] = summaryTable.rows[i][summaryTable.rows[i].length - 1];
				}
				
				tableData += "<tr class = 'child-of-nameNode-" + currentNameNode + "'>";
				tableData += "<td></td>";
				for(var j = 1; j < summaryTable.rows[i].length; j++)
				{
					tableData += "<td>" + summaryTable.rows[i][j] + "</td>";
				}
				if(summaryTable.rows[i][3] == 'Not Responding')
					HiveServices.isSetTimer = true;
				tableData += "<td>";
				for(var k=0; k<HiveServices.hostsList.length; k++)
				{
					if(HiveServices.hostsList[k]["hostIP"] == summaryTable.rows[i][2])
					{
						var hostIp = HiveServices.hostsList[k]["hostIP"];
						var port = HiveServices.hostsList[k]["agentPort"];
						var installationDir = HiveServices.hostsList[k]["installDirPath"];
						tableData += '<a href="javascript:HiveServices.viewLogFile(\''+hostIp+'\',\''+port+'\',\''+installationDir+'\',\''+summaryTable.rows[i][1]+'\');">View Log</a>';
						break;
					}
				}
				tableData += "</td></tr>";
			}
		}
		tableData += "</tbody>";
		
		$('#hive_services_table').html(tableData);

		$("#hive_services_table").treeTable({
			expandable: false
		});
		
		//code to make treetable expandable
//		for(var m = 0; m < totalNameNodeCount; m++)
//		{
//			$(".expandNamenode-" + m).click({val : nameNodeIDs[m]}, function(event){
//				$("#nameNode-" + event.data.val).toggleBranch();
//			});
//		}
		var row;
		var tableRow = new Array();
		for(var i=0; i < summaryTable.rows.length; i++)
		{
			// remove name node entry from row data, for not to break the existing functionality
			summaryTable.rows[i].splice(0,1);
			
			row = summaryTable.rows[i];
			var rowData = new Array();
			var serviceData = new Array();
			
			rowData.push('<input type="checkbox" value="service-1" onClick="javascript:HiveServices.clickCheckBox(this.id)" id="service_'+row[row.length-1]+'" >');
			
			serviceData.push(row[0]);
			var link = "<a style = \"cursor: pointer;\" id = \"serviceLink\" onclick = \"HiveServices.addBox(this.text);\">" + row[0] + "</a>";
			rowData.push(link);
			
			
			for(var j=1; j<row.length - 1;j++)
			{
				serviceData.push(row[j]);
				rowData.push(row[j]);
			}
			serviceData.push(row[j]);
			rowData.push('<div id="service.status'+row[row.length-1]+'">'+row[j]+'</div>');
			
			HiveServices.tableData.push(serviceData);
			tableRow.push(rowData);
		}
//		--
		HiveServices.hiveServiceState();
	},
	
	viewLogFile : function(hostIp, hostPort, installDir, serverName)
	{
		var url = "";
		if (serverName.indexOf('Hive') != -1)
			url = "http://" + hostIp + ":" + hostPort + "/agentqueryio/log?node-type=HiveServer&host-dir=" + installDir + "&file-type=log";
		window.open(url);
		return;
	},
	
	selectAllServices : function (element)
	{
		var val = element.checked;
		for (var i=0; i<document.forms[0].elements.length; i++)
	 	{
	 		var e = document.forms[0].elements[i];
	 		if ((e.id != 'selectAll') && (e.type=='checkbox'))
	 		{
	 				e.checked = val;
	 				HiveServices.clickCheckBox(e.id);
	 		}
	 	}
	},
	
	checkEnableDisable: function()
	{
		if (HiveServices.selectedServicesList.length <= 0)
		{
			dwr.util.byId('hiveServiceStart').disabled = true;
			dwr.util.byId('hiveServiceStop').disabled = true;
		}
	},
	
	clickCheckBox : function(chkbxid)
	{
		
		if (document.getElementById('hiveServiceStart')=='undefined' || document.getElementById('hiveServiceStart')==null)
		return;
		
		var serviceId = chkbxid.substring(8, chkbxid.length);
		
		if($("#" + chkbxid).attr('checked'))
		{
			if (HiveServices.selectedServicesList.indexOf(serviceId) == -1)
				HiveServices.selectedServicesList.push(serviceId);
			
			HiveServices.enableDisableStartStopButton();
		}
		else
		{
			var index = HiveServices.selectedServicesList.indexOf(serviceId);
			HiveServices.selectedServicesList.splice(index, 1);
			
			HiveServices.enableDisableStartStopButton();
		}
		
		if(HiveServices.selectedServicesList.length <= 0)
		{
			$("#hiveServiceStart").attr('disabled','disabled');
			$("#hiveServiceStop").attr('disabled','disabled');
		}
	},
	
	enableDisableStartStopButton : function()
	{
		var selectedNameNodeStatus = null;
		if(HiveServices.selectedServicesList.length != 0)
		{
			selectedNameNodeStatus = HiveServices.nameNodeStatus[HiveServices.selectedServicesList[0]];

		}
		var statusConflict = false;
		for(var i = 0; i < HiveServices.selectedServicesList.length; i++)
		{
			if(selectedNameNodeStatus != HiveServices.nameNodeStatus[HiveServices.selectedServicesList[i]])
			{
				if(HiveServices.nameNodeStatus[HiveServices.selectedServicesList[i]] != 'Not Responding')
				{
					statusConflict = true;
					break;
				}
			}
		}
		
		if(statusConflict)
		{
			$("#hiveServiceStart").attr('disabled','disabled');
			$("#hiveServiceStop").attr('disabled','disabled');
		}
		else
		{
			if(selectedNameNodeStatus != 'Stopped')
			{
				$("#hiveServiceStart").attr('disabled','disabled');
				$("#hiveServiceStop").removeAttr('disabled');
			}
			else
			{
				$("#hiveServiceStop").attr('disabled','disabled');
				$("#hiveServiceStart").removeAttr('disabled');
			}
		}
	},
	
	refreshView : function(){
		Navbar.isRefreshPage=true;
		Navbar.changeTab('Hive Services','admin', 'hive_services');
	},
	
	addBox : function(value,host)
	{
		HiveServices.selectedHost = host;
		HiveServices.serviceSelected = value;
//		Util.addLightbox("showServices", "resources/showHiveServices.html", null, null);
	},
	
	closeBox : function()
   	{
   		Util.removeLightbox("showServices");
   	},
   	
   	fillForm : function()
   	{
   		HiveServices.hidePortDetail();
   		$("#headerspan").text(HiveServices.serviceSelected);
   		for(var i=0; i<HiveServices.tableData.length; i++)
   		{
   			if(HiveServices.tableData[i][1] == HiveServices.selectedHost && HiveServices.tableData[i][0] == HiveServices.serviceSelected)
   			{
   				$("#serviceName").val(HiveServices.tableData[i][0]);
   				$("#serviceHost").val(HiveServices.tableData[i][1]);
   				$("#serviceFTP").val(HiveServices.tableData[i][2]);
   				break;
   			}
   		}
   	},
   	
   	hidePortDetail : function()
   	{
   		
   		if(HiveServices.serviceSelected == 'S3 Compatible Server')
   		{
   			$("#trServiceFTP").hide();
   			$("#trServiceSFTP").hide();
   		}
   	},
   	
   	startService : function(flag)
   	{
   		if(flag)
   		{
   			HiveServices.clickOnStart = true;
   			Util.addLightbox("hiveServicesLightBox", "resources/queryio_services_lighbox.html", null, null);
   		}
   		else
   		{
   			for(var i = 0; i < HiveServices.selectedServicesList.length; i++)
   			{
   				RemoteManager.startQueryIOServices(HiveServices.selectedServicesList[i], true, HiveServices.startNodeResponse);
   			}
   		}
   		
   	},
   	
   	stopService : function(flag)
   	{
   		if(flag)
   		{
   			HiveServices.clickOnStart = false;
   			Util.addLightbox("hiveServicesLightBox", "resources/queryio_services_lighbox.html", null, null);
   		}
   		else
   		{
   			for(var i = 0; i < HiveServices.selectedServicesList.length; i++)
   			{
   				RemoteManager.stopQueryIOServices(HiveServices.selectedServicesList[i], true, HiveServices.stopNodeResponse);   		
   			}
   		}
   	},
   	
   	startNodeResponse : function(response)
   	{
   		if (response.taskSuccess)
   		{
   			$("#popupMessage" + response.id).html('Hive Service was successfully started.');
   			$("#popupStatus" + response.id).html('Success');
   			$("#popupImageProcessing" + response.id).css('display', 'none');
   			$("#popupImageSuccess" + response.id).css('display', '');
   		}
   		else
   		{
   			$("#popupMessage" + response.id).html('Failed to start Hive Service. ' + response.responseMessage);
   			$("#popupStatus" + response.id).html('Failure');
   			$("#popupImageProcessing" + response.id).css('display', 'none');
   			$("#popupImageFail" + response.id).css('display', '');
   		}
   		
   		$("#okPopup").removeAttr('disabled');
   	},
   	
   	stopNodeResponse : function(response)
   	{
   		if (response.taskSuccess)
   		{
   			$("#popupMessage" + response.id).html('Hive Services was successfully stopped.');
   			$("#popupStatus" + response.id).html('Success');
   			$("#popupImageProcessing" + response.id).css('display', 'none');
   			$("#popupImageSuccess" + response.id).css('display', '');
   		}
   		else
   		{
   			$("#popupMessage" + response.id).html('Failed to stop Hive Service. ' + response.responseMessage);
   			$("#popupStatus" + response.id).html('Failure');
   			$("#popupImageProcessing" + response.id).css('display', 'none');
   			$("#popupImageFail" + response.id).css('display', '');
   		}
   		
   		$("#okPopup").removeAttr('disabled');
   	},
   	
   	closePopupBox : function()
   	{
   		Util.removeLightbox("hiveServicesLightBox");
   		Navbar.refreshView();
   	},
   	
   	fillPopup : function()
   	{
   		for(var i = 0; i < HiveServices.selectedServicesList.length; i++)
   		{
   			var data = '<tr id="popPattern" style="">' + 
   			'<td><span id="popupHost' + HiveServices.selectedServicesList[i] +'">' + HiveServices.selectedServicesList[i] +'</span></td>'	+
   			'<td><span id="popupMessage' + HiveServices.selectedServicesList[i] +'">' + 'Request for Hive Service is getting processed' +'</span><br><div id="log_div" style="display: none;"></div></td>' +
   			'<td><span id="popupStatus' + HiveServices.selectedServicesList[i] +'">' + 'Processing' + '</span></td>' +
   			'<td>' +
   			'<span id="popupImageFail' + HiveServices.selectedServicesList[i] +'" style="display:none;"><img src="images/Fail_img.png" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>' +
   			'<span id="popupImageSuccess' + HiveServices.selectedServicesList[i] +'" style="display:none;"><img  src="images/Success_img.png" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>' +
   			'<span id="popupImageProcessing' + HiveServices.selectedServicesList[i] +'"><img  src="images/process.gif" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>' +
   			'</td>' +
   			'</tr>';
   			
   			$("#queryio_operation_data_table tr:last").before(data);
   		}
   		
   		if (HiveServices.clickOnStart)
   		{
   			HiveServices.startService(false);
   		}
   		else
   		{
   			HiveServices.stopService(false);
   		}
   	},
   	
   	hiveServiceState : function()
	{
		if(HiveServices.isSetTimer) {
			var timerProcess = setTimeout(function() {QueryIOServicesManager.getAllServices(true, HiveServices.fillTable); },30000);
			HiveServices.timer.push(timerProcess);
		} else 	{
			for(var i=0;i<HiveServices.timer.length;i++){
				clearTimeout(HiveServices.timer[i]);
			}
		}
	}
};