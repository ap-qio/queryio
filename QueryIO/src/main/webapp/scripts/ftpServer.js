FTPServer = {

		
		
		ready : function(){
			
			document.getElementById('ftpStart').disabled = true;
			document.getElementById('ftpStop').disabled = true;
			FTPServer.fillFTPNameNodeHost();
			HOFManager.getAllConfig(FTPServer.fillFTPServerTable);
			
			
			
			
		},
		
		fillFTPServerTable : function(list){
			$("#ftpForm :input").attr("disabled", true);
			$("#editFtpConfig").removeAttr("disabled");
			$("#cancelConfig").removeAttr("disabled");
			$("#saveFtpConfig").removeAttr("disabled");
			
			if(list!=null && list!=undefined &&list.length>0 ){
				$('#editFtpConfig').css("display","");
				$('#cancelConfig').css("display","none");
				$('#saveFtpConfig').css("display","none");
				$('#hostUriRow').css("display","");
				$('#statusRow').css("display","");
				$('#hostRow').css("display","none");
				$('#ftpServerRow').css("display","");
				$('#hostUri').val(list[0]);
				$('#ftpServer').val(list[1]);
				
				
				
				var myselect=document.getElementById("hostForFtp")
				for (var i=0; i<myselect.options.length; i++){
				 if (myselect.options[i].text==list[1]){
					 myselect.options[i].selected=true;
				  break
				 }
				}
				
//				$('#hostForFtp').val(list[1]);
				
				
				$('#port').val(list[2]);
				HOFManager.getStatus(FTPServer.setStatus);
				
				
				
			}
			else
			{	
				//$('#ftp_server_table_div').html('<span> FTP Server Not Configured</span>');
				$('#editFtpConfig').css("display","none");
				$('#cancelConfig').css("display","none");
				$('#saveFtpConfig').css("display","");
				$('#hostUriRow').css("display","none");
				$('#statusRow').css("display","none");
				$("#ftpForm :input").attr("disabled", false);
				$('#hostRow').css("display","");
				$('#ftpServerRow').css("display","none");
				$('#message').text("Please configure FTP Server settings.");
			}

						
		},
		
		setStatus : function(resp){
			
			
			if(resp=='Stopped'){
				document.getElementById('ftpStart').disabled = false;
				document.getElementById('ftpStop').disabled = true;
				
				$('#statusStart').css("display","none");
				$('#statusStop').css("display","");
				$('#noStatus').css("display","none");
			}
			else if(resp=='Started'){
			
				document.getElementById('ftpStart').disabled = true;
				document.getElementById('ftpStop').disabled = false;
				$('#statusStart').css("display","");
				$('#statusStop').css("display","none");
				$('#noStatus').css("display","none");
				
			}
			else{
				
				document.getElementById('ftpStart').disabled = true;
				document.getElementById('ftpStop').disabled = false;
				$('#statusStart').css("display","none");
				$('#statusStop').css("display","none");
				$('#noStatus').css("display","");
			}
			
			$('#status').text(resp);
		},
		
		configureFTPServer : function(){
			
			Util.addLightbox("configureFTP", "resources/configureFTPServer.html", null, null);
		},
		
		startServer : function(){
			document.getElementById('ftpStart').disabled = true;
			document.getElementById('ftpStop').disabled = false;
			HOFManager.startServer(FTPServer.handleStartServerResp);
		},
		
		handleStartServerResp : function(dwrResponse){
			var status = '';
			if(dwrResponse.taskSuccess){
					status = 'Success';
			}else{
				status = 'Failed';
			}
			jAlert(dwrResponse.responseMessage,status);
			FTPServer.refreshView();
		},
		
		stopServer : function(){
			document.getElementById('ftpStart').disabled = false;
			HOFManager.stopServer(true, FTPServer.handleStopServer);
		},
		handleStopServer : function(resp){
			if(resp){
				jAlert("FTP server stopped  successfully.","Server stopped.")	
			}else{
				jAlert("You do not have sufficient privilege to stop FTP server.","Operation failed");
			}
			
			FTPServer.refreshView();
		},
		closeBox : function(){
			Util.removeLightbox("configureFTP");
		},
		
		fillFTPNameNodeHost : function(){
			RemoteManager.getNameNodes(FTPServer.fillFTPNameNodeHostOption);	
		},
		
		fillFTPNameNodeHostOption : function(list){
			
			selectList = dwr.util.byId('hostForFtp');
			dwr.util.removeAllOptions(selectList);
			FTPServer.addOption(selectList, 0, 'Select Host');
			hostNames = 0;
			for (var i = 0; i < list.length; i++)
			{
//				if(list[i].status=='Started'){
					FTPServer.addOption(selectList, list[i].id, list[i].id);
//				}
			}
			dwr.util.setValue('hostForFtp', hostNames);
			
		},
		addOption : function(selectbox, value, text)
		{
			var optn = document.createElement("OPTION");
			optn.text = text;
			optn.value = value;
			selectbox.options.add(optn);
		},
		saveConfig : function(){
			
			var hostId = document.getElementById('hostForFtp').value;
			var port = document.getElementById('port').value;
			
			if(hostId==0){
				jAlert("No Host is selected . Please select a host for ftp server.","Insufficient details");
				return;
			}
			else if(port==''){
				jAlert("You  didn't provide port .Please provide valid port for ftp server.","Insufficient details");
				return;
			}
			HOFManager.setConfig(hostId,port,FTPServer.handleSaveResponse);
			
			
		},
		
		handleSaveResponse : function(dwrResponse){
			
			
			if(dwrResponse.taskSuccess){
				jAlert(dwrResponse.responseMessage,'Success');	
			}else{
				jAlert(dwrResponse.responseMessage,'Failed');
			}
			
			FTPServer.refreshView();
		},
		
		editConfig : function(){
			
			$('#editFtpConfig').css("display","none");
			$('#cancelConfig').css("display","");
			$('#saveFtpConfig').css("display","");
			$('#hostUriRow').css("display","none");
			$('#statusRow').css("display","none");
			$('#hostRow').css("display","");
			$('#ftpServerRow').css("display","none");
			
			$("#ftpForm :input").attr("disabled", false);
			$("#editFtpConfig").removeAttr("disabled");
			$("#cancelConfig").removeAttr("disabled");
			$("#saveFtpConfig").removeAttr("disabled");
		},
		cancelEditConfig : function(){
			FTPServer.refreshView();
		},
		refreshView : function(){
			Navbar.isRefreshPage=true;
			Navbar.changeTab('FTP Server','admin', 'ftp_server');
		}
		
};
