DataConnection ={
	
	connectionData : [],
	connectionIDs : [],
	selectedConnection : [],
	currentOperation : '',
	currentViewedConnectionId : '',
	currentViewedConnectionType : '',
	
	ready : function()
	{
		document.getElementById('editButton').disabled = true;
		document.getElementById('deleteButton').disabled = true;
		
		RemoteManager.getAllDataConnections(DataConnection.populateConnectionsTable);
	},
	
	populateConnectionsTable : function(response)
	{
		DataConnection.connectionData = response;
		var colList = [];
		colList.push({ "sTitle":'<input type="checkbox" value="tag" id="selectAll" onclick="javascript:DataConnection.selectAll(this.id)" >', "sWidth": "35px" });
		colList.push({ "sTitle":'Connection ID'});
		colList.push({ "sTitle":'Source'});
		
		var tableRow = [];
		for(var i=0; i<DataConnection.connectionData.length; i++)
		{
			var row = DataConnection.connectionData[i];

			var rowData = new Array();
			var type = "";
			if(row.type == 0)
				type = "FTP";
			else if(row.type == 1)
				type = "S3";
			else if(row.type == 2)
				type = "HTTP/HTTPS";
			else if(row.type == 3)
				type = "POP/IMAP";
			else if(row.type == 4)
				type = "HDFS";
			else if(row.type == 5)
				type = "SSH";
			else if(row.type == 6)
				type = "SFTP";
			else if(row.type == 9)
				type = "DATABASE";
			
			rowData.push('<input type="checkbox" value="'+row.id+'" onClick="javascript:DataConnection.clickBox(this.id)" id="'+row.id+'" >');
			rowData.push("<a onclick = \"DataConnection.viewConnection('" + row.id + "', '" + type + "');\" style = \"cursor: pointer; text-decoration: none;\">" + row.id + "</a>");
			rowData.push(type);
			
			DataConnection.connectionIDs.push(row.id);
			tableRow.push(rowData);
		}
		
		$('#dataConnectionTable').dataTable
		({		       
			"bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": true,
			"bInfo": false,
			"bDestroy": true,
			"bAutoWidth": false,
			"aoColumns": colList,
			"aaData": tableRow
	    });
		
		if(response == null || response == undefined || response.length == 0)
			document.getElementById('selectAll').disabled = true;
		else
			document.getElementById('selectAll').disabled = false;
	},
	
	clickBox : function(id)
	{
		var flag = document.getElementById(id).checked;
		if (flag == true)
		{
			DataConnection.selectedConnection.push(id.toString());
			document.getElementById('editButton').disabled = false;
		}
		else
		{
			var index = jQuery.inArray(id.toString(), DataConnection.selectedConnection);
			if (index != -1)
			{
				DataConnection.selectedConnection.splice(index, 1);
			}
		}
		if(($('#dataConnectionTable tr').length - 1) == DataConnection.selectedConnection.length)
		{
			document.getElementById("selectAll").checked = flag;
			DataConnection.selectAll("selectAll", flag);
		}
		else
			DataConnection.toggleButton(id, flag, "selectAll");
	},
	
	selectAll: function(id)
	{
		var flag = document.getElementById(id).checked;
		
		DataConnection.selectedConnection.splice(0, DataConnection.selectedConnection.length);
		for (var i=0; i<DataConnection.connectionIDs.length; i++)
		{
			document.getElementById(DataConnection.connectionIDs[i]).checked = flag;
			if (flag)
				DataConnection.selectedConnection.push(DataConnection.connectionIDs[i]);
		}
		DataConnection.toggleButton(id, flag);
	},
	
	toggleButton: function(id, value, parent)
	{
		if (id == "selectAll")
		{
							
			$("#deleteButton").attr("disabled", !value);
		}
		else
		{
			if(value == false)
				$('#selectAll').attr("checked",false);
			
			if (DataConnection.selectedConnection.length < 1)
			{
				$("#editButton").attr("disabled", true);
				$("#deleteButton").attr("disabled", true);
			}
			else
			{
				if (DataConnection.selectedConnection.length == 1)
				{
					$("#editButton").attr("disabled", false);
				}
				else
				{
					$("#editButton").attr("disabled", true);						
				}
				$("#deleteButton").attr("disabled", false);
			}
			
		}
	},
	
	addConnection : function()
	{
		DataConnection.currentOperation = "add";
		Util.addLightbox("conn", "resources/addDataConnection.html", null, null);
	},
	
	deleteConnection : function()
	{
		DataConnection.currentOperation = "delete";
		
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm('Are you sure you want to delete selected item(s)?','Delete Data Connection(s)',function(val)
		{
			if (val == true)
			{
				RemoteManager.deleteDataConnection(DataConnection.selectedConnection, DataConnection.saveCallBack);
			}
			jQuery.alerts.okButton = ' Ok ';
			jQuery.alerts.cancelButton  = ' Cancel ';
		});
	},
	
	editConnection : function()
	{
		DataConnection.currentOperation = "edit";
		Util.addLightbox("conn", "resources/addDataConnection.html", null, null);
		DataSourceManager.getSource(DataConnection.selectedConnection[0], DataConnection.fillEditForm);
	},
	
	viewConnection : function(connId, type)
	{
		DataConnection.currentOperation = "view";
		DataConnection.currentViewedConnectionId = connId;
		DataConnection.currentViewedConnectionType = type;
		Util.addLightbox("conn", "resources/addDataConnection.html", null, null);
	},
	
	fillEditForm : function(response)
	{
		var type = "";
		for(var i=0; i<DataConnection.connectionData.length; i++)
		{
			var row = DataConnection.connectionData[i];
			if(row.id == response.id)
			{
				if(row.type == 0)
					type = "FTP";
				else if(row.type == 1)
					type = "Amazon";
				else if(row.type == 2)
					type = "HTTP/HTTPS";
				else if(row.type == 3)
					type = "POP/IMAP";
				else if(row.type == 4)
					type = "HDFS";
				else if(row.type == 5)
					type = "SSH";
				else if(row.type == 6)
					type = "SFTP";
				else if(row.type == 9)
					type = "DB";
			}
		}

		$("#connectionId").val(response.id);
		$('#connectionId').attr('readonly','readonly');
		$("#source").val(type);
		document.getElementById('source').disabled = true;
		DataConnection.sourceChanged("source");
		
		if(type == "FTP")
		{
			$("#hostnameData").val(response.host);
			$("#portnumberData").val(response.port);
			$("#usernameData").val(response.username);
			$("#passwordData").val(response.password)
		}
		else if(type == "SFTP")
		{
			$("#SFTPHost").val(response.host);
			$("#SFTPPort").val(response.port);
			$("#SFTPUser").val(response.username);
			$("#SFTPPass").val(response.password)
		}
		else if(type == "Amazon")
		{
			$("#accessKeyData").val(response.accessKey);
			$("#secureAccessKeyData").val(response.secretAccessKey);
		}
		else if(type == "HTTP/HTTPS")
		{
			$("#HttpBaseURL").val(response.baseURL);
			$("#HttpUser").val(response.userName);
			$("#HttpPass").val(response.password);
		}
		else if(type == "POP/IMAP")
		{
			$("#EmailAdd").val(response.emailAddress);
			$("#EmailPass").val(response.password);
			$("#EmailSadd").val(response.mailServerAddress);
			$("#EmailAname").val(response.accountName);
			$("#Emailprotocol").val(response.protocol);
			$("#EmailSocket").val(response.socketType);
			$("#EmailPort").val(response.port);
			$("#EmailCTO").val(response.connectionTimeOut);
			$("#EmailRTO").val(response.readTimeOut);
			
		}	
		else if(type == "HDFS")
		{
			$("#HDFSHost").val(response.host);
			$("#HDFSPort").val(response.port);
			$("#HDFSUser").val(response.username);
			$("#HDFSGroup").val(response.group);			
		}
		else if(type == "SSH")
		{
			$("#SSHHost").val(response.host);
			$("#SSHPort").val(response.port);
			$("#SSHUser").val(response.username);
			$("#SSHPass").val(response.password);
			$("#SSHKey").val(response.key);			
			if($("#SSHKey").val()=="")
			{
				$("#authMethodLB").val("SSHpLB");
				$('#SSHprLB').css('display','');
				$('#SSHkrLB').css('display','none');
			}
			else
			{
				$("#authMethodLB").val("SSHkLB");
				$('#SSHprLB').css('display','none');
				$('#SSHkrLB').css('display','');		
			}
		}else if(type == "DB")
		{
			$("#DBDriverClass").val(response.driver);
			$("#DBConnectionURL").val(response.connectionURL);
			$("#DataBaseUser").val(response.userName);
			$("#DataBasePass").val(response.password);
			$("#DataBaseDriverJar").hide();
			$("#DataBaseDriverJarEdit").show();
			$("#DataBaseDriverJarEdit").val(response.jarFileName);
		}
	},
	
	fillViewForm : function(response)
	{
		var colList = [];
		colList.push({ "sTitle":'Connection Parameters'});
		colList.push({ "sTitle":'Values'});
		var tableRow = [];
		for(var key in response)
		{
			var rowData = new Array();
			
			var keyValue = response[key];
			if(key == "id")
				continue;
			if(key == "password")
			{
				var tempCount = keyValue.length;
				keyValue = "";
				for(var i=0; i<tempCount; i++)
					keyValue += "&#149;";
			}
			if(key == "tableNames" && keyValue!=null && keyValue!=undefined && keyValue.length!=0){
				var tableNames = "";
				tableNames = '<select multiple="multiple" disabled>';
				for(var i=0;i< keyValue.length;i++)
					tableNames += '<option>' +keyValue[i] +  '</option>';
				tableNames += '</select>';
				keyValue = tableNames;
			}
			rowData.push(key.toUpperCase());
			rowData.push(keyValue);
			
			
			tableRow.push(rowData);
		}
				
		$('#viewDataConnectionTable').dataTable
		({		       
			"bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": true,
			"bInfo": false,
			"bDestroy": true,
			"bAutoWidth": false,
			"aoColumns": colList,
			"aaData": tableRow
	    });
		
		$('#viewDataConnectionTable tr:first-child td').css('padding', '3px 0px 3px 10px');
		$('#viewDataConnectionTable tr td').css('text-align', 'left');
	},
	
	saveConnection : function()
	{
		if($("#connectionId").val() == "")
		{
			jAlert("You must specify the unique Connection Id.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		else if($("#source").val() == "0")
		{
			jAlert("You must specify the source.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		else if($("#source").val() == "Amazon")
		{
			if($("#accessKeyData").val() == "")
			{
				jAlert("You must specify the access key.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#secureAccessKeyData").val() == "")
			{
				jAlert("You must specify the secure access key.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
		}
		else if($("#source").val() == "FTP")
		{
			if($("#hostnameData").val() == "")
			{
				jAlert("You must specify the FTP host.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#portnumberData").val() == "")
			{
				jAlert("You must specify the FTP port.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if(!Util.isNumeric($("#portnumberData").val()))
			{
				jAlert("FTP Port must be a numeric value.","Improper Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#usernameData").val() == "")
			{
				jAlert("You must specify the Username.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#passwordData").val() == "")
			{
				jAlert("You must specify the Password.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
		}
		else if($("#source").val() == "SFTP")
		{
			if($("#SFPTHost").val() == "")
			{
				jAlert("You must specify the SFTP host.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#SFTPPort").val() == "")
			{
				jAlert("You must specify the SFTP port.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if(!Util.isNumeric($("#SFTPPort").val()))
			{
				jAlert("SFTP Port must be a numeric value.","Improper Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#SFTPUser").val() == "")
			{
				jAlert("You must specify the Username.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#SFPTPass").val() == "")
			{
				jAlert("You must specify the Password.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
		}		
		else if($("#source").val() == "HDFS")
		{
			if($("#HDFSHost").val() == "")
			{
				jAlert("You must specify the HDFS Host.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if(!Util.isInHdfsFormat($("#HDFSHost").val()))
			{
				jAlert("HDFS Host should be in format (Hdfs://xxx).","Improper Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#HDFSUser").val() == "")
			{
				jAlert("You must specify the HDFS Username.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#HDFSGroup").val() == "")
			{
				jAlert("You must specify the HDFS Group.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#HDFSPort").val() == "")
			{
				jAlert("You must specify the HDFS Port.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if(!Util.isNumeric($("#HDFSPort").val()))
			{
				jAlert("HDFS Port must be a numeric value.","Improper Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			 
		}
		else if($("#source").val() == "POP/IMAP")
		{
			if($("#EmailAdd").val() == "")
			{
				jAlert("You must specify the Email Address.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if(!Util.validateEmail($("#EmailAdd").val()))
			{
				jAlert("Email Address must be in format \"abc@abc.com\"");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			
			else if($("#EmailPass").val() == "")
			{
				jAlert("You must specify the Password.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#EmailSadd").val() == "")
			{
				jAlert("You must specify the Email Server Address.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#EmailAname").val() == "")
			{
				jAlert("You must specify the Email Account Name.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#EmailPort").val() == "")
			{
				jAlert("You must specify the Mail Server Port.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if(!Util.isNumeric($("#EmailPort").val()))
			{
				jAlert("Mail Server Port must be a numeric value.","Improper Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#EmailCTO").val() == "")
			{
				jAlert("You must specify the Connection Timeout Value.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if(!Util.isNumeric($("#EmailCTO").val()))
			{
				jAlert("Connection Timeout must be a numeric value.","Improper Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#EmailRTO").val() == "")
			{
				jAlert("You must specify the Read Timeout Value.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}		
			else if(!Util.isNumeric($("#EmailRTO").val()))
			{
				jAlert("Read Timeout must be a numeric value.","Improper Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
		}
		else if($("#source").val() == "HTTP/HTTPS")
		{
			if($("#HttpBaseURL").val() == "")
			{
				jAlert("You must specify the Base URL.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if(!Util.isInHttpFormat($("#HttpBaseURL").val()))
			{
				jAlert("Base URL should be in format (Http://xxx or Https://xxx).","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
//			else if($("#HttpUser").val() == "")
//			{
//				jAlert("You must specify the Username.","Insufficient Details");
//				$("#popup_container").css("z-index","99999999");
//				return;
//			}
//			else if($("#HttpPass").val() == "")
//			{
//				jAlert("You must specify the Password.","Insufficient Details");
//				$("#popup_container").css("z-index","99999999");
//				return;
//			}			
		}
		else if($("#source").val() == "SSH")
		{
			if($("#SSHHost").val() == "")
			{
				jAlert("You must specify the SSH Host.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#SSHPort").val() == "")
			{
				jAlert("You must specify the SSH Port.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if(!Util.isNumeric($("#SSHPort").val()))
			{
				jAlert("SSH Port must be a numeric value.","Improper Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#SSHUser").val() == "")
			{
				jAlert("You must specify the SSH Username.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			else if($("#authMethodLB").val() == "SSHpLB")
			{	
				if($("#SSHPass").val() == "")
				{
					jAlert("You must specify the SSH Password.","Insufficient Details");
					$("#popup_container").css("z-index","99999999");
					return;
				}
			}
			else if($("#authMethodLB").val() == "SSHkLB")
			{
				if($("#SSHKey").val() == "")
				{
					jAlert("You must specify the SSH Key.","Insufficient Details");
					$("#popup_container").css("z-index","99999999");
					return;
				}		
			}
		}
		
		//Validations over. Function Calling starts 
		
		var connectionId = $("#connectionId").val();
		if($("#source").val() == "FTP")
		{
			var host = $("#hostnameData").val();
			var port = $("#portnumberData").val();
			port = parseInt(port);
			var uname = $("#usernameData").val();
			var password = $("#passwordData").val();
			
			if(DataConnection.currentOperation == "add")
				RemoteManager.addFTPDataSource(connectionId, host, port, uname, password, DataConnection.saveCallBack);
			else if(DataConnection.currentOperation == "edit")
				RemoteManager.updateFTPDataSource(connectionId, host, port, uname, password, DataConnection.saveCallBack);
			else if(DataConnection.currentOperation == "externalAdd")
				RemoteManager.addFTPDataSource(connectionId, host, port, uname, password, DataConnection.saveExternalCallBack);
		} 
		else if($("#source").val() == "SFTP")
		{
			var host = $("#SFTPHost").val();
			var port = $("#SFTPPort").val();
			port = parseInt(port);
			var uname = $("#SFTPUser").val();
			var password = $("#SFTPPass").val();
			
			if(DataConnection.currentOperation == "add")
				RemoteManager.addSFTPDataSource(connectionId, host, port, uname, password, DataConnection.saveCallBack);
			else if(DataConnection.currentOperation == "edit")
				RemoteManager.updateSFTPDataSource(connectionId, host, port, uname, password, DataConnection.saveCallBack);
			else if(DataConnection.currentOperation == "externalAdd")
				RemoteManager.addSFTPDataSource(connectionId, host, port, uname, password, DataConnection.saveExternalCallBack);
		}
		else if($("#source").val() == "Amazon")
		{
			var accessKey = $("#accessKeyData").val();
			var secureAccessKey = $("#secureAccessKeyData").val();
			
			if(DataConnection.currentOperation == "add")
				RemoteManager.addS3DataSource(connectionId, accessKey, secureAccessKey, DataConnection.saveCallBack);
			else if(DataConnection.currentOperation == "edit")
				RemoteManager.updateS3DataSource(connectionId, accessKey, secureAccessKey, DataConnection.saveCallBack);
			else if(DataConnection.currentOperation == "externalAdd")
				RemoteManager.addS3DataSource(connectionId, accessKey, secureAccessKey, DataConnection.saveExternalCallBack);
		}
		else if($("#source").val() == "HDFS")
		{
			var host = $("#HDFSHost").val();
			var port = $("#HDFSPort").val();
			port = parseInt(port);
			var user = $("#HDFSUser").val();
			var group = $("#HDFSGroup").val();			
			
			if(DataConnection.currentOperation == "add")
				RemoteManager.addHDFSDataSource(connectionId,host,port,group,user,DataConnection.saveCallBack);
			else if(DataConnection.currentOperation == "edit")
				RemoteManager.updateHDFSDataSource(connectionId,host,port,group,user,DataConnection.saveCallBack);
			else if(DataConnection.currentOperation == "externalAdd")
				RemoteManager.addHDFSDataSource(connectionId,host,port,group,user,DataConnection.saveExternalCallBack);

		}	
		else if($("#source").val() == "POP/IMAP")
		{
			var emailadd = $("#EmailAdd").val();
			var pass = $("#EmailPass").val();
			var sadd = $("#EmailSadd").val();
			var aname = $("#EmailAname").val();
			var protocol = $("#Emailprotocol").val();
			var socket = $("#EmailSocket").val();
			var port = $("#EmailPort").val();
			var CTO = $("#EmailCTO").val();
			var RTO = $("#EmailRTO").val();

			if(DataConnection.currentOperation == "add")
				RemoteManager.addEmailDataSource(connectionId, emailadd, pass, sadd, aname, protocol, socket, port, CTO, RTO, DataConnection.saveCallBack);			
			else if(DataConnection.currentOperation == "edit")
				RemoteManager.updateEmailDataSource(connectionId, emailadd, pass, sadd, aname, protocol, socket, port, CTO, RTO, DataConnection.saveCallBack);	
			else if(DataConnection.currentOperation == "externalAdd")
				RemoteManager.addEmailDataSource(connectionId, emailadd, pass, sadd, aname, protocol, socket, port, CTO, RTO, DataConnection.saveExternalCallBack);
		}	
		else if($("#source").val() == "HTTP/HTTPS")
		{
			var url = $("#HttpBaseURL").val();
			var user = $("#HttpUser").val();
			var pass =$("#HttpPass").val();
			
			if(DataConnection.currentOperation == "add")
				RemoteManager.addHTTPDataSource(connectionId, url, user, pass, DataConnection.saveCallBack);		
			else if(DataConnection.currentOperation == "edit")
				RemoteManager.updateHTTPDataSource(connectionId, url, user, pass, DataConnection.saveCallBack);		
			else if(DataConnection.currentOperation == "externalAdd")
				RemoteManager.addHTTPDataSource(connectionId, url, user, pass, DataConnection.saveExternalCallBack);		
		}	
		else if($("#source").val() == "SSH")
		{
			var host = $("#SSHHost").val();
			var port = $("#SSHPort").val();
			port = parseInt(port);
			var user = $("#SSHUser").val();
			var pass = $("#SSHPass").val();
			var key = $("#SSHKey").val();
			
			if(DataConnection.currentOperation == "add")
				RemoteManager.addSSHDataSource(connectionId,host,port,user,pass,key, DataConnection.saveCallBack);
			else if(DataConnection.currentOperation == "edit")
				RemoteManager.updateSSHDataSource(connectionId,host,port,user,pass,key, DataConnection.saveCallBack);
			else if(DataConnection.currentOperation == "externalAdd")
				RemoteManager.addSSHDataSource(connectionId,host,port,user,pass,key, DataConnection.saveExternalCallBack);
		}
	},
	
	saveCallBack : function(response)
	{
		if(response.taskSuccess)
			jAlert(response.responseMessage, "Success");
		else
			jAlert(response.responseMessage, "Failure");
		DataConnection.closeBox(true);
	},
	
	saveExternalCallBack : function(response)
	{
		var status = "";
		if(response.taskSuccess)
			status = "Success";
		else
			status = "Failure";
		jAlert(response.responseMessage, status, function(val){
			if(val == true)
			{
				if(DataMigrate.currentDataStore == "FTP")
				{
					RemoteManager.getAllDataConnections(DataMigrate.fillFTPConnectionIds);
					DataMigrate.clearFTPfields();
				}
				else if(DataMigrate.currentDataStore == "SFTP")
				{
					RemoteManager.getAllDataConnections(DataMigrate.fillSFTPConnectionIds);
					DataMigrate.clearSFTPfields();
				}
				else if(DataMigrate.currentDataStore == "HDFS")
				{
					RemoteManager.getAllDataConnections(DataMigrate.fillHDFSConnectionIds);
					DataMigrate.clearHDFSfields();
				} 
				else if(DataMigrate.currentDataStore == "POP/IMAP")
				{
					RemoteManager.getAllDataConnections(DataMigrate.fillIMAPConnectionIds);
					DataMigrate.clearIMAPfields();
				} 
				else if(DataMigrate.currentDataStore == "HTTP/HTTPS")
				{
					RemoteManager.getAllDataConnections(DataMigrate.fillHTTPConnectionIds);
					DataMigrate.clearHTTPfields();
				} 
				else if(DataMigrate.currentDataStore == "SSH")
				{
					RemoteManager.getAllDataConnections(DataMigrate.fillSSHConnectionIds);
					DataMigrate.clearSSHfields();
				}
				else
				{
					RemoteManager.getAllDataConnections(DataMigrate.fillS3ConnectionIds);
					DataMigrate.clearS3fields();
				}				
			}
		});
		$("#popup_container").css("z-index","9999999");
		DataConnection.closeBox(false);
	},
	
	sourceChanged : function(id)
	{
		var value = $("#" + id).val();
		if(value == "Amazon")
		{			
			$("#HDFSDivLB").hide();
			$("#httpDivLB").hide();
			$("#amazonDiv").show();
			$("#ftpDiv").hide();
			$("#emailDivLB").hide();		
			$("#SSHDivLB").hide();	
			$("#SFTPDivLB").hide();
			$("#DataBaseDivLB").hide();
			$("#saveDBConnection").hide();
			$("#saveConnection").show();

		}
		else if(value == "FTP")
		{
			$("#HDFSDivLB").hide();
			$("#httpDivLB").hide();
			$("#amazonDiv").hide();
			$("#ftpDiv").show();
			$("#emailDivLB").hide();
			$("#SSHDivLB").hide();
			$("#SFTPDivLB").hide();
			$("#DataBaseDivLB").hide();
			$("#saveDBConnection").hide();
			$("#saveConnection").show();

		}
		else if(value == "SFTP")
		{
			$("#HDFSDivLB").hide();
			$("#httpDivLB").hide();
			$("#amazonDiv").hide();
			$("#ftpDiv").hide();
			$("#emailDivLB").hide();
			$("#SSHDivLB").hide();
			$("#SFTPDivLB").show();
			$("#DataBaseDivLB").hide();
			$("#saveDBConnection").hide();
			$("#saveConnection").show();

		}
		else if(value=="HDFS")
		{
			$("#HDFSDivLB").show();
			$("#httpDivLB").hide();
			$("#amazonDiv").hide();
			$("#ftpDiv").hide();
			$("#emailDivLB").hide();			
			$("#SSHDivLB").hide();	
			$("#SFTPDivLB").hide();
			$("#DataBaseDivLB").hide();
			$("#saveDBConnection").hide();
			$("#saveConnection").show();

		}
		else if(value=="HTTP/HTTPS")
		{
			$("#HDFSDivLB").hide();
			$("#httpDivLB").show();
			$("#amazonDiv").hide();
			$("#ftpDiv").hide();
			$("#emailDivLB").hide();			
			$("#SSHDivLB").hide();
			$("#SFTPDivLB").hide();
			$("#DataBaseDivLB").hide();
			$("#saveDBConnection").hide();
			$("#saveConnection").show();

		}
		else if(value=="POP/IMAP")
		{
			$("#HDFSDivLB").hide();
			$("#httpDivLB").hide();
			$("#amazonDiv").hide();
			$("#ftpDiv").hide();
			$("#emailDivLB").show();			
			$("#SSHDivLB").hide();
			$("#SFTPDivLB").hide();
			$("#DataBaseDivLB").hide();
			$("#saveDBConnection").hide();
			$("#saveConnection").show();

		}
		else if(value=="SSH")
		{
			$("#HDFSDivLB").hide();
			$("#httpDivLB").hide();
			$("#amazonDiv").hide();
			$("#ftpDiv").hide();
			$("#emailDivLB").hide();			
			$("#SSHDivLB").show();
			$("#SFTPDivLB").hide();
			$("#DataBaseDivLB").hide();
			$("#saveDBConnection").hide();
			$("#saveConnection").show();

		}
		else if(value=="DB")
		{
			$("#HDFSDivLB").hide();
			$("#httpDivLB").hide();
			$("#amazonDiv").hide();
			$("#ftpDiv").hide();
			$("#emailDivLB").hide();			
			$("#SSHDivLB").hide();
			$("#SFTPDivLB").hide();
			$("#DataBaseDivLB").show();
			$("#saveConnection").hide();
			$("#saveDBConnection").show();
		}
		else
		{
			$("#HDFSDivLB").hide();
			$("#httpDivLB").hide();
			$("#amazonDiv").hide();
			$("#ftpDiv").hide();
			$("#emailDivLB").hide();			
			$("#SSHDivLB").hide();
			$("#SFTPDivLB").hide();
			$("#DataBaseDivLB").hide();
		}
	},
	
	closeBox : function(isRefresh)
	{
		DataConnection.selectedConnection.splice(0, DataConnection.selectedConnection.length);
		DataConnection.currentOperation = "";
		$("#editButton").attr("disabled", true);
		$("#deleteButton").attr("disabled", true);
		$('input:checkbox').removeAttr('checked');
		
		Util.removeLightbox("conn");
		
		if(isRefresh)
			Navbar.refreshView();
	},
	saveDBConnection : function()
	{
		if($('#connectionId').val()==""){
			jAlert("You must specify the connection id.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}	   
		if($("#DBDriverClass").val() == ""){
			jAlert("You must specify the database driver class.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		if($("#DBConnectionURL").val() == ""){
			jAlert("You must specify the database connection url.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		if($("#DataBaseUser").val() == ""){
			jAlert("You must specify the username.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		if($("#DataBasePass").val() == ""){
			jAlert("You must specify the password.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		if(DataConnection.currentOperation == "edit")
		{
			var connectionId = $("#connectionId").val();
			var driver = $("#DBDriverClass").val();
			var connUrl = $("#DBConnectionURL").val();
			var uname = $("#DataBaseUser").val();
			var password = $("#DataBasePass").val();
			var driverJar = $("#DataBaseDriverJarEdit").val();
			
			RemoteManager.updateDBDataSource(connectionId, driver, connUrl, uname, password, driverJar, DataConnection.saveCallBack);
			
		}else if(DataConnection.currentOperation == "add"){
			
			if($("#DataBaseDriverJar").val() == ""){
				jAlert("You must specify the jar file.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			var filename = $("#DataBaseDriverJar").val();
			filename = filename.substring(filename.length-4 , filename.length);
			if(filename!='.jar' && filename!='.JAR'){
				jAlert("You must specify the jar file.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			var form = document.getElementById("CommonForm");
			DataConnection.fileUpload(form);
		}
		
		
	},
	fileUpload : function(form )
	{
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
        $("#addEditTable").hide();
        var id = document.getElementById('DataBaseDriverJar').value;
    	id = id.substring(id.lastIndexOf("\\")+1 , id.length);
        $("#parser").text(id);
        $("#respMessage").text('Uploading Jar...');
        $("#headerspan").text('Status');
        $("#processingDiv").css('display', '');
        $('#respProcessing').css('display','');
        $('#log_div').hide();
    	$('#respStatus').text('Processing...');
    	$('#respFail').css('display','none');
    	$('#respSuccess').css('display','none');
        iframeId = document.getElementById("upload_iframe");
        
        var eventHandler = function (){
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
                content=JSON.parse(content);
                $('#respProcessing').css('display','none');
                $('#respMessage').text(content.message);
                if(content.status == 'sucess'){
        
                	$("#respMessage").text('Jar uploaded successfully.');
                	$('#respStatus').text('Success');
                	$('#respSuccess').css('display','');
                	
                }else{
                	
                	$("#respMessage").text('Jar uploading failed.');
                	$('#respStatus').text('Failed');
                	$('#respFail').css('display','');
                }
                $('#okpopup').removeAttr('disabled');
    	}
        
        if (iframeId.addEventListener) iframeId.addEventListener("load", eventHandler, true);
        if (iframeId.attachEvent) iframeId.attachEvent("onload", eventHandler);
        // Set properties of form...
        form.setAttribute("target", "upload_iframe");
        form.setAttribute("action", "DataBaseJarFileUpload");
        form.setAttribute("method", "post");
        form.setAttribute("enctype", "multipart/form-data");
        form.setAttribute("encoding", "multipart/form-data");
        form.submit();
    
	},
	closePopUp : function(){
		Util.removeLightbox("addDC");
		Navbar.refreshView();
	},
	checkRequest : function()
	{
		var flag = document.getElementById('DataBaseImportAll').checked;
		if(flag)
		{
			$('#db_tables').css('display' , 'none');
		}
		else
		{
			$('#db_tables').css('display' , '');
		}
	}
};