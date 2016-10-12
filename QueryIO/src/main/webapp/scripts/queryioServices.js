QueryIOServices = {
		
	tableData : [],
	serviceSelected : '',
	
	selectedServicesList : [],
	hostsList : [],
	nameNodeStatus : {},
	
	selectedHost : null,
	clickOnStart : false,
	
	nameNodeOverAllStatusMap : {},
	
	ready : function()
	{
		QueryIOServices.checkEnableDisable();
		RemoteManager.getNameNodes(QueryIOServices.getAllNameNodeDetails);
	},
	
	fillHostList : function(list)
	{
		QueryIOServices.hostsList = list;
		QueryIOServicesManager.getAllServices(false, QueryIOServices.fillTable);
	},
	
	getAllNameNodeDetails : function(nameNodeDetails)
	{
		for(var i = 0 ; i < nameNodeDetails.length; i++)
		{
			QueryIOServices.nameNodeOverAllStatusMap[nameNodeDetails[i].id] = nameNodeDetails[i].status; 
		}
		
		RemoteManager.getAllHostDetails(QueryIOServices.fillHostList);
	},
	
	fillTable : function(summaryTable)
	{
		var totalNameNodeCount = 0;
		var nameNodeIDs = [];
		if(document.getElementById('queryio_services_table')=='undefined'||document.getElementById('queryio_services_table')==null)
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
					if(QueryIOServices.nameNodeOverAllStatusMap[currentNameNode] != 'Started')
					{
						tableData += '<input type="checkbox" disabled = "disabled" style = "" value="service-1" onClick="javascript:QueryIOServices.clickCheckBox(this.id)" id="service_' + currentNameNode + '" >';
						tableData += '<span class = "expandNamenode-' + totalNameNodeCount + '" style = "cursor: pointer;" >' + summaryTable.rows[i][0] + "</span></td><td style = 'color: #3083D0;' colspan = '" + (summaryTable.rows[i].length) + "'>This NameNode is " + QueryIOServices.nameNodeOverAllStatusMap[currentNameNode] + ". Please start NameNode to start/stop QueryIO Services.</td>";
					}
					else
					{
						tableData += '<input type="checkbox" style = "" value="service-1" onClick="javascript:QueryIOServices.clickCheckBox(this.id)" id="service_' + currentNameNode + '" >';
						tableData += '<span class = "expandNamenode-' + totalNameNodeCount + '" style = "cursor: pointer;" >' + summaryTable.rows[i][0] + "</td><td colspan = '" + (summaryTable.rows[i].length) + "'></td>";
					}
					tableData += "</tr>";
					totalNameNodeCount ++;
					QueryIOServices.nameNodeStatus[currentNameNode] = summaryTable.rows[i][summaryTable.rows[i].length - 1];
				}
				
				tableData += "<tr class = 'child-of-nameNode-" + currentNameNode + "'>";
				tableData += "<td></td>";
//				tableData += "<td>" + "<a style = \"cursor: pointer;\" id = \"serviceLink\" onclick = \"QueryIOServices.addBox(this.text, '" + summaryTable.rows[i][2] + "');\">" + summaryTable.rows[i][1] + "</a>"; + "</td>";
				for(var j = 1; j < summaryTable.rows[i].length; j++)
				{
					tableData += "<td>" + summaryTable.rows[i][j] + "</td>";
				}
				tableData += "<td>";
				for(var k=0; k<QueryIOServices.hostsList.length; k++)
				{
					if(QueryIOServices.hostsList[k]["hostIP"] == summaryTable.rows[i][2])
					{
						var hostIp = QueryIOServices.hostsList[k]["hostIP"];
						var port = QueryIOServices.hostsList[k]["agentPort"];
						var installationDir = QueryIOServices.hostsList[k]["installDirPath"];
						tableData += '<a href="javascript:QueryIOServices.viewLogFile(\''+hostIp+'\',\''+port+'\',\''+installationDir+'\',\''+summaryTable.rows[i][1]+'\');">View Log</a>';
						break;
					}
				}
				tableData += "</td></tr>";
			}
		}
		tableData += "</tbody>";
		
		$('#queryio_services_table').html(tableData);

		$("#queryio_services_table").treeTable({
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
			
			rowData.push('<input type="checkbox" value="service-1" onClick="javascript:QueryIOServices.clickCheckBox(this.id)" id="service_'+row[row.length-1]+'" >');
			
			serviceData.push(row[0]);
			var link = "<a style = \"cursor: pointer;\" id = \"serviceLink\" onclick = \"QueryIOServices.addBox(this.text);\">" + row[0] + "</a>";
			rowData.push(link);
			
			
			for(var j=1; j<row.length - 1;j++)
			{
				serviceData.push(row[j]);
				rowData.push(row[j]);
			}
			serviceData.push(row[j]);
			rowData.push('<div id="service.status'+row[row.length-1]+'">'+row[j]+'</div>');
			
			QueryIOServices.tableData.push(serviceData);
			tableRow.push(rowData);
		}
		
		/*
		if(document.getElementById('queryio_services_table')=='undefined'||document.getElementById('queryio_services_table')==null)
			return;

		var flag = true;
		var tableRow = [];
		var rowList = '';

		var colList = new Array();
		
		if(flag)
		{
			flag = false;
			colList.push({ "sTitle":'<input type="checkbox" value="service-1" id="selectAll" onclick="javascript:QueryIOServices.selectAllServices(this)" >' });
			for(var i=0; i<summaryTable.colNames.length; i++)
			{
				colList.push({ "sTitle": summaryTable.colNames[i]});
			}
		}
		
		var row;
		var tableRow = new Array();
		for(var i=0; i<summaryTable.rows.length; i++)
		{
			row = summaryTable.rows[i];
			var rowData = new Array();
			var serviceData = new Array();
			
			rowData.push('<input type="checkbox" value="service-1" onClick="javascript:QueryIOServices.clickCheckBox(this.id)" id="service'+row[row.length-1]+'" >');
			
			serviceData.push(row[0]);
			var link = "<a style = \"cursor: pointer;\" id = \"serviceLink\" onclick = \"QueryIOServices.addBox(this.text);\">" + row[0] + "</a>";
			rowData.push(link);
			
			
			for(var j=1; j<row.length - 1;j++)
			{
				serviceData.push(row[j]);
				rowData.push(row[j]);
			}
			serviceData.push(row[j]);
			rowData.push('<div id="service.status'+row[row.length-1]+'">'+row[j]+'</div>');
			
			QueryIOServices.tableData.push(serviceData);
			tableRow.push(rowData);	
		}
		
		
		$('#queryio_services_table').dataTable({
	        "bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": true,
			"bInfo": false,
			"bAutoWidth": false,
			"aaData": tableRow,
	        "aoColumns": colList
	    });
		
	   	//disabled the checkAll button when no data in the list
	   	if(summaryTable == null || summaryTable == undefined || summaryTable.length == 0)
			document.getElementById('selectAll').disabled = true;
		else
			$('#selectAll').removeAttr('disabled');
			
		*/
	},
	
	viewLogFile : function(hostIp, hostPort, installDir, serverName)
	{
		var url = "";
		if (serverName.indexOf('S3') != -1)
			url ="http://"+hostIp+":"+hostPort+"/agentqueryio/log?node-type=S3Server&host-dir="+installDir+"&file-type=log";
		else if(serverName.indexOf('FTP') != -1)
			url ="http://"+hostIp+":"+hostPort+"/agentqueryio/log?node-type=FTPServer&host-dir="+installDir+"&file-type=log";
		else if(serverName.indexOf('Hive') != -1)
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
	 				QueryIOServices.clickCheckBox(e.id);
	 		}
	 	}
	},
	
	checkEnableDisable: function()
	{
		if (QueryIOServices.selectedServicesList.length <= 0)
		{
			dwr.util.byId('queryioServiceStart').disabled = true;
			dwr.util.byId('queryioServiceStop').disabled = true;
		}
	},
	
	clickCheckBox : function(chkbxid)
	{
		
		if (document.getElementById('queryioServiceStart')=='undefined' || document.getElementById('queryioServiceStart')==null)
		return;
		
		var serviceId = chkbxid.substring(8, chkbxid.length);
		
		if($("#" + chkbxid).attr('checked'))
		{
			if (QueryIOServices.selectedServicesList.indexOf(serviceId) == -1)
				QueryIOServices.selectedServicesList.push(serviceId);
			
			QueryIOServices.enableDisableStartStopButton();
		}
		else
		{
			var index = QueryIOServices.selectedServicesList.indexOf(serviceId);
			QueryIOServices.selectedServicesList.splice(index, 1);
			
			QueryIOServices.enableDisableStartStopButton();
		}
		
		if(QueryIOServices.selectedServicesList.length <= 0)
		{
			$("#queryioServiceStart").attr('disabled','disabled');
			$("#queryioServiceStop").attr('disabled','disabled');
		}
		
		
//		if (document.getElementById('queryioServiceStart')=='undefined' || document.getElementById('queryioServiceStart')==null)
//			return;
//		
//		var serviceId = chkbxid.substring(7, chkbxid.length);
//		dwr.util.byId('queryioServiceStart').disabled = false
//		dwr.util.byId('queryioServiceStop').disabled = false;
//		
//		if (dwr.util.byId(chkbxid).checked)
//		{
//			if (QueryIOServices.selectedServicesList.indexOf(serviceId) == -1)
//				QueryIOServices.selectedServicesList.push(serviceId);
//			
//			if (dwr.util.getValue('service.status'+serviceId)=='Started')
//			{
//				dwr.util.byId('queryioServiceStart').disabled=true;
//			}
//			else if (dwr.util.getValue('service.status'+serviceId)=='Stopped')
//			{
//				dwr.util.byId('queryioServiceStop').disabled=true;
//			}
//			else
//			{
//				dwr.util.byId('queryioServiceStart').disabled=true;
//			}
//		}
//		else
//		{
//			var index = QueryIOServices.selectedServicesList.indexOf(serviceId);
//			QueryIOServices.selectedServicesList.splice(index, 1);
//			for(var i=0;i<QueryIOServices.selectedServicesList.length;i++)
//			{
//				if(dwr.util.getValue('service.status'+QueryIOServices.selectedServicesList[i])=='Started')
//				{
//					dwr.util.byId('queryioServiceStart').disabled=true;
//				}
//				else if (dwr.util.getValue('service.status'+QueryIOServices.selectedServicesList[i])=='Stopped')
//				{
//					dwr.util.byId('queryioServiceStop').disabled=true;
//				}
//				else
//				{
//					dwr.util.byId('queryioServiceStart').disabled=true;
//				}
//			}
//		}
//		
//		QueryIOServices.checkEnableDisable();
	},
	
	enableDisableStartStopButton : function()
	{
		var selectedNameNodeStatus = null;
		if(QueryIOServices.selectedServicesList.length != 0)
		{
			selectedNameNodeStatus = QueryIOServices.nameNodeStatus[QueryIOServices.selectedServicesList[0]];

		}
		var statusConflict = false;
		for(var i = 0; i < QueryIOServices.selectedServicesList.length; i++)
		{
			if(selectedNameNodeStatus != QueryIOServices.nameNodeStatus[QueryIOServices.selectedServicesList[i]])
			{
				if(QueryIOServices.nameNodeStatus[QueryIOServices.selectedServicesList[i]] != 'Not Responding')
				{
					statusConflict = true;
					break;
				}
			}
		}
		
		if(statusConflict)
		{
			$("#queryioServiceStart").attr('disabled','disabled');
			$("#queryioServiceStop").attr('disabled','disabled');
		}
		else
		{
			if(selectedNameNodeStatus != 'Stopped')
			{
				$("#queryioServiceStart").attr('disabled','disabled');
				$("#queryioServiceStop").removeAttr('disabled');
			}
			else
			{
				$("#queryioServiceStop").attr('disabled','disabled');
				$("#queryioServiceStart").removeAttr('disabled');
			}
		}
	},
	
	refreshView : function(){
		Navbar.isRefreshPage=true;
		Navbar.changeTab('QueryIO Services','admin', 'queryio_services');
	},
	
	addBox : function(value,host)
	{
		QueryIOServices.selectedHost = host;
		QueryIOServices.serviceSelected = value;
//		Util.addLightbox("showServices", "resources/showQueryIOServices.html", null, null);
	},
	
	closeBox : function()
   	{
   		Util.removeLightbox("showServices");
   	},
   	
   	fillForm : function()
   	{
   		QueryIOServices.hidePortDetail();
   		$("#headerspan").text(QueryIOServices.serviceSelected);
   		for(var i=0; i<QueryIOServices.tableData.length; i++)
   		{
   			if(QueryIOServices.tableData[i][1] == QueryIOServices.selectedHost && QueryIOServices.tableData[i][0] == QueryIOServices.serviceSelected)
   			{
   				$("#serviceName").val(QueryIOServices.tableData[i][0]);
   				$("#serviceHost").val(QueryIOServices.tableData[i][1]);
   				$("#serviceFTP").val(QueryIOServices.tableData[i][2]);
   				break;
   			}
   		}
   	},
   	
   	hidePortDetail : function()
   	{
   		
   		if(QueryIOServices.serviceSelected == 'S3 Compatible Server')
   		{
   			$("#trServiceFTP").hide();
   			$("#trServiceSFTP").hide();
   		}
   	},
   	
   	startService : function(flag)
   	{
   		if(flag)
   		{
   			QueryIOServices.clickOnStart = true;
   			Util.addLightbox("queryioServicesLightBox", "resources/queryio_services_lighbox.html", null, null);
   		}
   		else
   		{
   			for(var i = 0; i < QueryIOServices.selectedServicesList.length; i++)
   			{
   				RemoteManager.startQueryIOServices(QueryIOServices.selectedServicesList[i], false, QueryIOServices.startNodeResponse);
   			}
   		}
   		
   	},
   	
   	stopService : function(flag)
   	{
   		if(flag)
   		{
   			QueryIOServices.clickOnStart = false;
   			Util.addLightbox("queryioServicesLightBox", "resources/queryio_services_lighbox.html", null, null);
   		}
   		else
   		{
   			for(var i = 0; i < QueryIOServices.selectedServicesList.length; i++)
   			{
   				RemoteManager.stopQueryIOServices(QueryIOServices.selectedServicesList[i], false, QueryIOServices.stopNodeResponse);   		
   			}
   		}
   	},
   	
   	startNodeResponse : function(response)
   	{
   		if (response.taskSuccess)
   		{
   			$("#popupMessage" + response.id).html('QueryIO Services were successfully started.');
   			$("#popupStatus" + response.id).html('Success');
   			$("#popupImageProcessing" + response.id).css('display', 'none');
   			$("#popupImageSuccess" + response.id).css('display', '');
   		}
   		else
   		{
   			$("#popupMessage" + response.id).html('Failed to start QueryIO Services. ' + response.responseMessage);
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
   			$("#popupMessage" + response.id).html('QueryIO Services were successfully stopped.');
   			$("#popupStatus" + response.id).html('Success');
   			$("#popupImageProcessing" + response.id).css('display', 'none');
   			$("#popupImageSuccess" + response.id).css('display', '');
   		}
   		else
   		{
   			$("#popupMessage" + response.id).html('Failed to stop QueryIO Services. ' + response.responseMessage);
   			$("#popupStatus" + response.id).html('Failure');
   			$("#popupImageProcessing" + response.id).css('display', 'none');
   			$("#popupImageFail" + response.id).css('display', '');
   		}
   		
   		$("#okPopup").removeAttr('disabled');
   	},
   	
   	closePopupBox : function()
   	{
   		Util.removeLightbox("queryioServicesLightBox");
   		Navbar.refreshView();
   	},
   	
   	fillPopup : function()
   	{
   		for(var i = 0; i < QueryIOServices.selectedServicesList.length; i++)
   		{
   			var data = '<tr id="popPattern" style="">' + 
   			'<td><span id="popupHost' + QueryIOServices.selectedServicesList[i] +'">' + QueryIOServices.selectedServicesList[i] +'</span></td>'	+
   			'<td><span id="popupMessage' + QueryIOServices.selectedServicesList[i] +'">' + 'Query IO Services are getting processed' +'</span><br><div id="log_div" style="display: none;"></div></td>' +
   			'<td><span id="popupStatus' + QueryIOServices.selectedServicesList[i] +'">' + 'Processing' + '</span></td>' +
   			'<td>' +
   			'<span id="popupImageFail' + QueryIOServices.selectedServicesList[i] +'" style="display:none;"><img src="images/Fail_img.png" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>' +
   			'<span id="popupImageSuccess' + QueryIOServices.selectedServicesList[i] +'" style="display:none;"><img  src="images/Success_img.png" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>' +
   			'<span id="popupImageProcessing' + QueryIOServices.selectedServicesList[i] +'"><img  src="images/process.gif" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>' +
   			'</td>' +
   			'</tr>';
   			
   			$("#queryio_operation_data_table tr:last").before(data);
   		}
   		
   		if (QueryIOServices.clickOnStart)
   		{
   			QueryIOServices.startService(false);
   		}
   		else
   		{
   			QueryIOServices.stopService(false);
   		}
   	}
};