SecDataNode = {

		isUserWaiting:false,
		 diskMap:null,
		seconaryNameNodeForm :null,
		currentPage:1,
		isJournal:true,
		
		
		ready : function(){
			//get all host ip and id for populate hostForSecNode selection box.
			SecDataNode.drawPage2Table();
			RemoteManager.getAllHostDetails(SecDataNode.fillhostForSecNode);
			RemoteManager.getNonStandByNodes(SecDataNode.fillAllNameNode);
		},
		fillAllNameNode : function(list){

			
			var data='<option value="">Select NameNode</option>';
			for(var i=0;i<list.length;i++){
				var node=list[i];
				data+='<option value="'+node.id+'">'+node.id+'</option>'
			}
			$('#NameNodeId').html(data);
			
		},
		
		
		fillhostForSecNode : function(list){
			selectList = dwr.util.byId('hostForSecNode');
			dwr.util.removeAllOptions(selectList);
			addOption(selectList, 0, 'Select Host');
			hostNames = 0;
			for (var i = 0; i < list.length; i++)
			{
				addOption(selectList, list[i].id, list[i].hostIP);
			}
			dwr.util.setValue('hostForSecNode', hostNames);
		},
		
		
//		
		closeBox : function(isRefresh){
			
			Util.removeLightbox("addnn");
			if(isRefresh)
			{
				Navbar.refreshView();
				Navbar.refreshNavBar();
			}

		},
		saveSecNameNode : function()
		{
			nodeType = "namenode";
			
			if($('#secNNSource').val()=='journal')
				isJournal=true;
			else
				isJournal=false;

			if (dwr.util.byId('diskForSecNN').value == 'select disk')
			{
				jAlert("Disk Name  is Empty. Please select a valid disk name.","Incomplete Detail");
				$("#popup_container").css("z-index","9999999");
				return;
			}	
			else if(dwr.util.byId('dirPathForSecNN').value ==''){
				jAlert("Installation directory path is empty. Please enter a valid directory path.","Incomplete Detail");
				$("#popup_container").css("z-index","9999999");
				return;			
			}
			
			else if(isJournal==false)
			{
				if (dwr.util.byId('sharedDirPathForSecNN').value =='')
				{
					jAlert("Shared directory path is empty. Please enter a valid directory path.","Incomplete Detail");
					$("#popup_container").css("z-index","9999999");
					return;
				}
			}
			if(isJournal==true)
			{
				if (dwr.util.byId('journalNodes').value =='')
				{
					jAlert("Journal Nodes is empty. Please choose atleast one Journal Node.","Incomplete Detail");
					$("#popup_container").css("z-index","9999999");
					return;
				}
			}
			
			var disks = new Array();
			disks.push(dwr.util.byId('diskForSecNN').value);
			var volumePath = new Array();
			volumePath.push(dwr.util.byId('dirPathForSecNN').value);
			
			SecDataNode.validateDiskVolumePathForNameNode(disks,volumePath);
			
		
		},
		validateDiskVolumePathForNameNode : function(disks,volumePath){
			
			if(SecDataNode.diskMap==null){
				jAlert("Disk-volume mapping are not received from Host.It will take some time .After complition we will notify you for save node.");
				$("#popup_container").css("z-index","99999999999");
				SecDataNode.isUserWaiting =true;
			}

			console.log(" volumePath1" + volumePath);
			console.log(" disks" + disks);
			console.log(" secDataNode.diskMap" , SecDataNode.diskMap);
			var statusArray = new Array();
			var volume='';
			var disk='';
			for(var i = 0;i<volumePath.length;i++){
				volume = volumePath[i];
				disk=disks[i];
				if(volume!=null && volume.length!=0)
				{
					if(volume.charAt(volume.length-1) != '/')
					{
						volume += '/';
					}
				}
				var tempPath = "";
				var  valid = false;
				var value = '';
				var attr = disk;
				
				console.log(" volume" + volume);
				console.log(" disk" + disk);
				console.log(" secDataNode.diskMap" , SecDataNode.diskMap);
				
				var check =disk.split(" ");
				
					//if(!(SecDataNode.diskMap.hasOwnProperty(disk))){
				if(!(SecDataNode.diskMap.hasOwnProperty(check[0].trim()))){
						disk+='s2';
						if(!(SecDataNode.diskMap.hasOwnProperty(disk))){
							jAlert('Disk '+disks[i]+' is not properly mounted or some error occured while fetching the details of disk.','Invalid Detail');
							return;
						}
					}
						//val = SecDataNode.diskMap[disk];
				       val = SecDataNode.diskMap[check[0].trim()];	
				     tempPath = val.replace(" ", "\\ "); 
						
//						if((volume.indexOf(val)==0))
//						{
							valid = true;
//						}
				statusArray.push(valid);
			}
			
			console.log(" secDataNode.diskMap" + SecDataNode.diskMap);
			SecDataNode.validateVolumePath(statusArray);
			
		}
,
		 validateVolumePath : function(statusArray)
		{
			for(var i = 0;i<statusArray.length;i++){
				if(!statusArray[i]){
					jAlert('Invalid mapping of Volume: "'+$('#dirPathForSecNN').val()+'" with Disk "'+$('#diskForSecNN').val()+'"','Invalid Detail');
					$("#popup_container").css("z-index","99999999");
					return;
				}
			}
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
				jConfirm("All the contents in the given installation directory will be deleted. Are you sure you want to install node at given location?",'Node Installation',function(val){
					if (val== true)
					{
						SecDataNode.seconaryNameNodeForm =document.getElementById('secNameNodeForm');
						$("#addnn").load("resources/add_sec_nn_lightbox.html");

					}else{
						return ; 
					}	
					jQuery.alerts.okButton = 'Ok';
					jQuery.alerts.cancelButton  = 'Cancel';
					
				});
			$("#popup_container").css("z-index","99999999");
		},
		getPhysicalDiskName : function(element){
			var id = element.id;
			var hostName =$('#'+id+' option:selected').text();  
			if(hostName=='Select Host'){
				return;
			}
			RemoteManager.getPhysicalDiskNames(hostName, SecDataNode.fillvolumeInfoTable);
			RemoteManager.getVolumeDiskMap(hostName,SecDataNode.fillDiskMapCache);
		},
		fillvolumeInfoTable : function(list)
		{
			if(list==null){
				jAlert("No disk info found at host.","No Disk found");
				$("#popup_container").css("z-index","9999999");
				return;	
			}
			
			var data = '<option value="select disk">Select Disk</option>';
			for(var i=0;i<list.length;i++){
				data+= '<option value="'+list[i]+'">'+list[i]+'</option>';
			}
			$('#diskForSecNN').html(data);
		},
		 fillDiskMapCache : function(map) {
				SecDataNode.diskMap = map;
				if(isUserWaiting){
					jAlert("Disk volume mapping received from server. Please add datanode now.", "Information")
					$("#popup_container").css("z-index","9999999");
				}
				SecDataNode.isUserWaiting = false;
			},

		lightBoxReady : function(){
			
			var host = SecDataNode.seconaryNameNodeForm.hostForSecNode.options[SecDataNode.seconaryNameNodeForm.hostForSecNode.selectedIndex].text;

			$("#host").html(host);
			$("#message").text("Adding Secondary NameNode");
			$("#status").html("");
			var hostId = SecDataNode.seconaryNameNodeForm.hostForSecNode.value;
			
			var diskName = SecDataNode.seconaryNameNodeForm.diskForSecNN.value;
			
			var dirPathForSecNN =SecDataNode.seconaryNameNodeForm.dirPathForSecNN.value;

			if(isJournal==false)
			{
				var sharedDirPathForSecNN =SecDataNode.seconaryNameNodeForm.sharedDirPathForSecNN.value;
			}
			else
			{
				var flag=0;
				var sharedDirPathForSecNN ='';
				var list=new Array();
		        var groupOptions = SecDataNode.seconaryNameNodeForm.journalNodes; 
		        for (var i=0; i< groupOptions.length; i++) 
		        {
		        	if (groupOptions[i].selected) 
		        	{
		        		list.push(groupOptions[i].value);
		        	}
	          	}
				for(var i=0;i<list.length;i++)
				{
					if(flag==0)
					{
						flag=1;
						var node=list[i];
						sharedDirPathForSecNN+=node;
					}
					else
					{
						var node=list[i];
						sharedDirPathForSecNN += ',' + node;
					}
				}
			}
			var namenodeId=SecDataNode.seconaryNameNodeForm.NameNodeId.value;
			
			var volumePath = new Array();
			volumePath.push(dirPathForSecNN);
			
			RemoteManager.addStandbyNameNode(hostId,SecDataNode.seconaryNameNodeForm.id.value,namenodeId,diskName,dirPathForSecNN,sharedDirPathForSecNN,SecDataNode.seconaryNameNodeForm.serverPort.value,SecDataNode.seconaryNameNodeForm.httpPort.value,SecDataNode.seconaryNameNodeForm.httpsPort.value,SecDataNode.seconaryNameNodeForm.jmxPort.value,SecDataNode.seconaryNameNodeForm.os3ServerPort.value, SecDataNode.seconaryNameNodeForm.secureOs3ServerPort.value,SecDataNode.seconaryNameNodeForm.hdfsOverFtpServerPort.value,SecDataNode.seconaryNameNodeForm.ftpServerPort.value,SecDataNode.seconaryNameNodeForm.secureFtpServerPort.value,isJournal,SecDataNode.handleResponse);
//			int hostId, String nodeId,namenodeId,disk,volumePath,sharedDirPath,servicePort,httpPort,httpsPort,jmxPort,os3ServerPort,secureOs3ServerPort,hdfsoverftpServerPort,ftpServerPort,secureFtpPort,isJournal
		},
		handleResponse : function(dwrResponse){
			var status='';
			if(dwrResponse.taskSuccess)	
			{
				img_src='images/Success_img.png'
				status = 'Success'; 
				dwr.util.byId('imagesuccess').style.display = '';
				
			}
			else
			{
				img_src='images/Fail_img.png'
				status = 'Fail';
				dwr.util.byId('imagefail').style.display = '';
				var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
				document.getElementById('log_div').innerHTML=log;
				document.getElementById('log_div').style.display="block";
				
			}
			dwr.util.byId('imageprocessing' ).style.display = 'none';
			dwr.util.setValue('message',dwrResponse.responseMessage);
			dwr.util.setValue('status',status);
			document.getElementById('okbutton').disabled = false;
			Navbar.refreshNavBar();			
			
		},
		
		nextPage: function(pageNo){
			var valid = true;
			switch(pageNo){
				case 1:
					SecDataNode.currentPage = pageNo;
					$('#nndetail').html('NameNode keeps the directory tree of all files in the file system in form of metadata, and tracks where across the cluster, the file data is kept.');
					$('#secPage1').css('display','');
					$('#secPage2').css('display','none');
					$('#nnType').css('display','');
					break;
				case 2:
					if(SecDataNode.currentPage<pageNo){
						valid = SecDataNode.checkValidity(1);
					}
					if(valid){
						SecDataNode.currentPage = pageNo;
						$('#nndetail').html('Please configure the required ports for starting NameNode service on the host selected.');
						$('#nnType').css('display','none');
						$('#secPage2').css('display','');
						$('#secPage1').css('display','none');
						$('#secPage3').css('display','none');
					}
					break;
				case 3:
					valid = SecDataNode.checkValidity(2);
					if(valid){
						SecDataNode.currentPage = pageNo;
						$('#nndetail').html('Please select the disk and specify the directory path where installation may take place. Also select whether QJM or NFS mount should be used for high availability.');
						$('#secPage2').css('display','none');
						$("#startNamenodeAfterInstall").hide();
						$("#startnodechkbox").removeAttr('checked');
						$('#secPage3').css('display','');
						$('#dirPathForSecNN').val('');
						$('#sharedDirPathForSecNN').val('');
						var hostName =$('#hostForSecNode option:selected').text();
						RemoteManager.getUserHomeDirectoryPathForHost(hostName,SecDataNode.fillUserHome);
						RemoteManager.getAllJournalNodeIds(SecDataNode.fillJournalNodeList)
					}
					break;
			}
		},
		
		fillUserHome : function(val){
			$('#dirPathForSecNN').val(val+'/QueryIONodes/NameNode');
			$('#sharedDirPathForSecNN').val(val+'/QueryIONodes/SharedDirectory1');
		},
		
		fillJournalNodeList : function(list)
		{
			var data='';
			for(var i=0;i<list.length;i++)
			{
				var node=list[i];
				data+='<option value="'+node+'">'+node+'</option>'
			}
			$('#journalNodes').html(data);
			
		},
		
		secNNsourceChanged : function(){
			if($('#secNNSource').val()=='journal')
			{
				$('#journalNodeList').css('display','');
				$('#sharedDirPath').css('display','none');
			}
			else if($('#secNNSource').val()=='path')
			{
				$('#journalNodeList').css('display','none');
				$('#sharedDirPath').css('display','');
			}				
		},
		
		
		
		checkValidity : function(pageNo)
		{
			var valid = true;
			var secNN = document.getElementById('secNameNodeForm');
			if(pageNo == 1)
			{
				if (dwr.util.byId('hostForSecNode').value == 0)
				{
					valid = false;
					jAlert("No Host Selected","Error");
					$("#popup_container").css("z-index","9999999");
				}	
				else if (dwr.util.byId('id').value == '')
				{
					valid = false;
					jAlert("Node Unique Identifier was not specified. Please provide a valid Unique Identifier to configure DataNode.","Error");
					$("#popup_container").css("z-index","9999999");
			
				}
				else if (dwr.util.byId('NameNodeId').value == '')
				{
					valid = false;
					jAlert("NameNode not selected.","Error");
					$("#popup_container").css("z-index","9999999");
			
				}
			}
			else{
				
				if (secNN.serverPort.value == '')
				{
					valid = false;
					jAlert("Server port not set.","Incomplete Detail");
					$("#popup_container").css("z-index","9999999");
				}	
				else if (secNN.httpPort.value == '')
				{
					valid = false;
					jAlert("Http port not set.","Incomplete detail");
					$("#popup_container").css("z-index","9999999");
				}
				else if (secNN.httpsPort.value == '')
				{
					valid = false;
					jAlert("Https port not set.","Incomplete detail");
					$("#popup_container").css("z-index","9999999");
				}
				else if (secNN.jmxPort.value == '')
				{
					valid = false;
					jAlert("JMX port not set.","Incomplete detail");
					$("#popup_container").css("z-index","9999999");
				}
				else if (secNN.os3ServerPort.value == '')
				{
					valid = false;
					jAlert("OS3 Server port not set.","Incomplete detail");
					$("#popup_container").css("z-index","9999999");
				}
				else if (secNN.hdfsOverFtpServerPort.value == '')
				{
					valid = false;
					jAlert("HDFS Over Ftp Server port not set.","Incomplete detail");
					$("#popup_container").css("z-index","9999999");
				}
				else if (secNN.ftpServerPort.value == '')
				{
					valid = false;
					jAlert("FTP server port not set.","Incomplete detail");
					$("#popup_container").css("z-index","9999999");
				}
			}
			return valid;
		},
		
		drawPage2Table : function()
		{
			var keyList = new Array();
			keyList.push("dfs.namenode.rpc-address");
			keyList.push("dfs.namenode.http-address");
			keyList.push("dfs.namenode.https-address");
			keyList.push("queryio.s3server.port");
			keyList.push("queryio.s3server.ssl.port");
			keyList.push("queryio.ftpserver.port");
			keyList.push("queryio.hdfsoverftp.port");
			keyList.push("queryio.ftpserver.ssl.port");
			keyList.push("queryio.namenode.options");
			
			
			
			RemoteManager.getConfigurationServerPort(keyList,SecDataNode.drawNameNodeConfigurationTable);
		},
		drawNameNodeConfigurationTable : function(map){
			
			
			var serverPort = map["dfs.namenode.rpc-address"]["value"];
			serverPort = serverPort.substring(serverPort.indexOf(':')+1).trim();
			
			
			var http = map["dfs.namenode.http-address"]["value"];
			http = http.substring(http.indexOf(':')+1).trim();
			
			var https = map["dfs.namenode.https-address"]["value"];
			https = https.substring(https.indexOf(':')+1).trim();

			var s3serverPort = map["queryio.s3server.port"]["value"];
			
			var s3SslserverPort = map["queryio.s3server.ssl.port"]["value"];
			
			var hdfsOverFtpPort = map["queryio.hdfsoverftp.port"]["value"];
			
			var ftpServerPort = map["queryio.ftpserver.port"]["value"];
			
			var ftpSslServerPort = map["queryio.ftpserver.ssl.port"]["value"];
			
			var jmx = map["queryio.namenode.options"]["value"];
			jmx = jmx.substring(jmx.indexOf('jmxremote.port=')+1);
			jmx = jmx.substring(jmx.indexOf('=')+1,jmx.indexOf('-Dcom.sun.management')).trim();
			
			var rowList = [
							[ "Server Port", '<input type="text" id="serverPort" value="'+serverPort+'">', 'The NameNode server port'],
							[ "HTTP Port", '<input type="text" id="httpPort" value="'+http+'">', 'The NameNode http port.'],
							[ "HTTPS Port", '<input type="text" id="httpsPort" value="'+https+'">', 'The NameNode secure http port.'],
							[ "JMX Port", '<input type="text" id="jmxPort" value="'+jmx+'">', 'JMX Monitoring port'],
							[ "S3 Compatible REST Server Port", '<input type="text" id="os3ServerPort" value="'+s3serverPort+'">', 'S3 Compatible REST Server Port'],
							[ "S3 Compatible REST Secure Server Port", '<input type="text" id="secureOs3ServerPort" value="'+s3SslserverPort+'">', 'S3 Compatible REST Secure Server Port'],
							[ "HDFS Over FTP Server Port", '<input type="text" id="hdfsOverFtpServerPort" value="'+hdfsOverFtpPort+'">', 'The HDFS over FTP server port to enable FTP services'],
							[ "FTP Server Port", '<input type="text" id="ftpServerPort" value="'+ftpServerPort+'">', 'FTP server port to access HDFS'],
							[ "Secure FTP Server Port", '<input type="text" id="secureFtpServerPort" value="'+ftpSslServerPort+'">', 'Secure FTP server port to access HDFS']
							];
			
			
			
			$('#page2Table').dataTable({
				"bPaginate": false,
				"bLengthChange": true,
				"bFilter": false,
				"bSort": false,
				"bInfo": false,
				"bAutoWidth": false,
				"bDestroy": true,
		        "aaData":rowList ,
				"aoColumns": [
				{ "sTitle": "Port Title" },
				{ "sTitle": "Port Number" },
				{ "sTitle": "Description" }
				]
		    });
			
			$('#standByNameNodeSecPage2').dataTable({
				"bPaginate": false,
				"bLengthChange": true,
				"bFilter": false,
				"bSort": false,
				"bInfo": false,
				"bAutoWidth": false,
				"bDestroy": true,
		        "aaData":rowList,
				"aoColumns": [
				{ "sTitle": "Port Title" },
				{ "sTitle": "Port Number" },
				{ "sTitle": "Description" }
				]
		    });
	
		}
		
		
};
