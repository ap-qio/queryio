var isUserWaiting=false;
var diskMap;
var selectedHdfsNode = [];
var nodeCache;
var hostCache;
var end = false;
var lastId = [];
var call = 0;
var endNode;
var callBackFunc='';
var nameNodeForm='';
var nodeId;
var popupmsg='Installing Node.';
var migratedDBName = [];
var selectedId = '';
var isRestartDataNode = false;
var isDeleteNameNode = false;

function fillDBNamesForNameNode(nodeId)
{
	selectedId = nodeId;
	RemoteManager.getAllConnectionsNameForOperation(fillmigratedDBNames);
}

function fillmigratedDBNames(list)
{
	migratedDBName = list;
	RemoteManager.getAllConnectionsNameForNameNode(true, populateNameNodeDataBaseDropDown);
}

function populateNameNodeDataBaseDropDown(list)
{
	var opt ='<option value="">Select Database</option>';
	if(list!=null && list!=undefined){
		list.sort();
		for(var item in list)
		{
			
			for(var j=0;j<migratedDBName.length;j++){
				var index = migratedDBName[j].indexOf(list[item]);
				if(index!=-1&&migratedDBName[j][2]=='RUNNING'){
					if(list.indexOf(list[item])!=-1)
						list.splice(list.indexOf(list[item]),1);
				}
			}
		}
		for(var item in list)
		{
			opt +='<option value="'+list[item]+'">'+list[item]+'</option>';
		}
		
	}
	$('#NameNodeDB').html(opt);
	
	if ((selectedId != null) || (selectedId != ''))
	{
		RemoteManager.getDBNameForNameNodeMapping(selectedId, fillDbNameConfigList);
	}
	
	fillAnalyticsDBNamesForNameNode(selectedId);
}

function fillDbNameConfigList(dbName)
{
	if ((dbName != null) && (dbName != undefined))
	{
		$('#NameNodeDB').css("display","none");
		$('#chooseDbName').css("display","");
		$('#dbNameText').css("display","");
		$('#dbNameText').val(dbName);
	}
	else
	{
		$('#chooseDbName').css("display","none");
	}
}

function fillAnalyticsDBNamesForNameNode(nodeId)
{
	selectedId = nodeId;
	RemoteManager.getAllConnectionsNameForNameNode(false, populateNameNodeAnalyticsDbDropDown);
}

function populateNameNodeAnalyticsDbDropDown(list)
{
	var opt ='<option value="">Select Database</option>';
	if(list!=null && list!=undefined){
		list.sort();
		for(var item in list)
		{
			
			for(var j=0;j<migratedDBName.length;j++){
				var index = migratedDBName[j].indexOf(list[item]);
				if(index!=-1&&migratedDBName[j][2]=='RUNNING'){
					if(list.indexOf(list[item])!=-1)
						list.splice(list.indexOf(list[item]),1);
				}
			}
		}
		for(var item in list)
		{
			opt +='<option value="'+list[item]+'">'+list[item]+'</option>';
		}

	}
	$('#analyticsDB').html(opt);
	
	if ((selectedId != null) || (selectedId != ''))
	{
		RemoteManager.getAnalyticsDBNameForNameNodeMapping(selectedId, fillAnalyticsDbNameConfigList);
	}
}

function fillAnalyticsDbNameConfigList(dbName)
{
	if ((dbName != null) && (dbName != undefined))
	{
		$('#analyticsDB').css("display","none");
		$('#chooseAnalyticsDbName').css("display","");
		$('#analyticsDbNameText').css("display","");
		$('#analyticsDbNameText').val(dbName);
	}
	else
	{
		$('#chooseAnalyticsDbName').css("display","none");
	}
}

function fillHostNamesForNameNode()
{
	//get all host ip and id for populate hostForNode selection box.
	RemoteManager.getAllHostDetails(populateAddHostForm);	
}

function populateAddHostForm(list)
{
	selectList = dwr.util.byId('hostForNode');
	dwr.util.removeAllOptions(selectList);
	addOption(selectList, 0, 'Select Host');
	hostNames = 0;
	for (var i = 0; i < list.length; i++)
	{
		addOption(selectList, list[i].id, list[i].hostIP);
	}
	dwr.util.setValue('hostForNode', hostNames);
}
function addOption(selectbox, value, text)
{
	var optn = document.createElement("OPTION");
	optn.text = text;
	optn.value = value;
	selectbox.options.add(optn);
}
function fillAllServices()
{
	populateHdfsService();
}
function populateHdfsService()
{
//	RemoteManager.getAllHostDetails(loadHostCache);
//	RemoteManager.getNameNodes(populateHdfsServiceList);
}

function loadHostCache(list)
{
	// unused method
	var host='';
	hostCache =new Array();
	for (var i = 0; i < list.length; i++)
	{
		host = list[i];
		hostCache[host.id]=host;
	}	
}
function populateHdfsServiceList(list)
{
	// unused method
	if(document.getElementById('pattern')=='undefined'||document.getElementById('pattern')==null)return;
	dwr.util.removeAllRows("nodes", { filter:function(tr)
		{
			return (tr.id != "pattern");
    }});
	nodeCache = new Array();
	var node;
	var id='';
	for (var i = 0; i < list.length; i=i+2)
	{
		nodeCache[id] = node;
	}
}
function setIp(host){

	 dwr.util.setValue("node.host" + lastId[call],host.hostIP);
//	 dwr.util.byId("node.loadChart" +lastId[call]).href='javascript:show(\"'+host.hostIP+'\");';
	 call++;
}

function loadPage(pageToLoad)
{
	$("#service_ref").load(pageToLoad);
}
function saveHdfsNode()
{
	nodeType = "namenode";
	
	if(dwr.util.byId('disk').value =='select disk'){
		jAlert("Disk Name  is Empty. Please select a valid disk name.","Incomplete Detail");
		$("#popup_container").css("z-index","9999999");
		return;	
	}	
	if(dwr.util.byId('dirPath').value ==''){
		jAlert("Installation directory path is empty. Please enter a valid directory path.","Incomplete Detail");
		$("#popup_container").css("z-index","9999999");
		return;	
	}
//	if(Util.isContainWhiteSpace(dwr.util.byId('dirPath').value)){
//		jAlert("Installation directory path contains space. Please remove space from directory path.","Incomplete Detail");
//		$("#popup_container").css("z-index","9999999");
//		return;	
//	}
	var disks = new Array();
	disks.push(dwr.util.byId('disk').value);
	var volumePath = new Array();
	volumePath.push(dwr.util.byId('dirPath').value);
	
	checkStartedDataNodes();
	
//	validateDiskVolumePathForNameNode(disks,volumePath);
}

function validateDiskVolumePathForNameNode(disks,volumePath){
	
//	if(diskMap==null){
//		jAlert("Disk-volume mapping are not received from Host.It will take some time .After complition we will notify you for save node.");
//		$("#popup_container").css("z-index","99999999999");
//		isUserWaiting =true;
//	}
	
	var statusArray = new Array();
	
//	var volume='';
//	var disk='';
//	for(var i = 0;i<volumePath.length;i++){
//		volume = volumePath[i];
//		disk=disks[i];
//		if(volume!=null && volume.length!=0)
//		{
//			if(volume.charAt(volume.length-1) != '/')
//			{
//				volume += '/';
//			}
//		}
//		var tempPath = "";
//		var  valid = true;
//		var value = '';
//		var attr = disks[i];
//			if(!(diskMap.hasOwnProperty(disk))){
//				disk+='s2';
//				if(!(diskMap.hasOwnProperty(disk))){
//					jAlert('Disk '+disks[i]+' is not properly mounted or some error occured while fetching the details of disk.','Invalid Details');
//					$("#popup_container").css("z-index","999999999");
//					return;
//				}
//			}
//				val = diskMap[disk];
//				tempPath = val.replace(" ", "\\ "); 
//				
//				if((volume.indexOf(val)==0))
//				{
//						valid = true;
//				}
//		statusArray.push(valid);
//	}
	
	validateVolumePath(statusArray);
}

function validateVolumePath(statusArray)
{
//	for(var i = 0;i<statusArray.length;i++){
//		if(!statusArray[i]){
//			jAlert('Invalid mapping of Voulme: "'+$('#dirPath').val()+'" with Disk "'+$('#disk').val()+'"','Invalid Detail');
//			$("#popup_container").css("z-index","99999999");
//			return;
//		}
//	}
	
//	jQuery.alerts.okButton = ' Yes ';
//	jQuery.alerts.cancelButton  = ' No';
//		jConfirm("All the contents in the given installation directory will be deleted. Are you sure you want to install node at given location?",'Node Installation',function(val){
//			if (val== true)
//			{
	
	
				nameNodeForm = dwr.util.byId('nameNodeForm');
				callBackFunc ='saveHdfsNode';
				$("#addnn").load("pages/popup.jsp");
//			}else{
//				return ; 
//			}	
//			jQuery.alerts.okButton = 'Ok';
//			jQuery.alerts.cancelButton  = 'Cancel';
//			
//		});
//	$("#popup_container").css("z-index","99999999");
}

function checkStartedDataNodes()
{
	RemoteManager.getAllDataNodesStarted(confirmRestartDataNode);
}

function confirmRestartDataNode(count)
{
	if (count > 0)
	{
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		var msg = "";
		if(count == 1)
			msg = 'There is 1 DataNode running. This operation requires restart of DataNode. Do you want to restart running DataNode? ';
		else
			msg = 'There are ' + count + ' DataNodes running. This operation requires restart of DataNodes. Do you want to restart running DataNodes? ';
		jConfirm(msg,'DataNode(s) Running',function(val)
		{
			isRestartDataNode = val;
			if (isDeleteNameNode)
				deleteHdfsNode(true);
			else
			{
				nameNodeForm = dwr.util.byId('nameNodeForm');
				callBackFunc ='saveHdfsNode';
				$("#addnn").load("pages/popup.jsp");
			}
			return;
		});
		$("#popup_container").css("z-index","99999999");
	}
	else
	{
		isRestartDataNode = false;
		if (isDeleteNameNode)
			deleteHdfsNode(true);
		else
		{
			nameNodeForm = dwr.util.byId('nameNodeForm');
			callBackFunc ='saveHdfsNode';
			$("#addnn").load("pages/popup.jsp");
		}
	}
}

function fillPopUp(flag)
{
	if(callBackFunc =='saveHdfsNode')
	{
		populateAddHdfsServiceList(true);
	}
	else
	{
		populateHdfsServicePopUpList(flag,null);
	}
}
function populateAddHdfsServiceList(flag , dwrResponse)
{
	
	var id = nameNodeForm.id.value;
	
	if(flag){
		dwr.util.setValue('popup.component','NameNode');
		dwr.util.cloneNode('pop.pattern',{ idSuffix:id });
		dwr.util.setValue('popup.host' + id,id);
		dwr.util.setValue('popup.message' + id,'Installing NameNode at host '+nameNodeForm.hostForNode.options[nameNodeForm.hostForNode.selectedIndex].text);	
		dwr.util.setValue('popup.status' + id,'Processing');
		dwr.util.byId('pop.pattern' + id).style.display = '';
		var hostId= parseInt(nameNodeForm.hostForNode.value)
		var analyticsDb = null;
		if (nameNodeForm.useDifferentDb.checked)
		{
			analyticsDb = nameNodeForm.analyticsDB.value;
		}
		RemoteManager.addNameNode(hostId,nameNodeForm.id.value,nameNodeForm.disk.value,nameNodeForm.dirPath.value,nameNodeForm.serverPort.value,nameNodeForm.httpPort.value,nameNodeForm.httpsPort.value,nameNodeForm.jmxPort.value,
				nameNodeForm.os3ServerPort.value,nameNodeForm.secureOs3ServerPort.value, nameNodeForm.hdfsOverFtpServerPort.value,nameNodeForm.ftpServerPort.value, nameNodeForm.secureFtpServerPort.value,nameNodeForm.NameNodeDB.value,analyticsDb, isRestartDataNode, hdfsNodeAdded); 
//		int hostId, String nodeId,String disk, String dirPath, String serverPort, String httpPort, String httpsPort, String ipcPort, String jmxPort) {
	}
	else{
		var status=''
		var id = dwrResponse.id;
		var nodeid='';
		if(dwrResponse.taskSuccess)	
		{
			img_src='images/Success_img.png'
			status = 'Success'; 
			dwr.util.byId('popup.image.success' + id).style.display = '';
			if(nameNodeForm.startnodechkbox.checked)
			{
				nodeid = dwrResponse.id;
			}
		}
		else
		{
			img_src='images/Fail_img.png'
			status = 'Fail';
			dwr.util.byId('popup.image.fail' + id).style.display = '';
			var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
			document.getElementById('log_div'+ id).innerHTML=log;
			document.getElementById('log_div'+ id).style.display="block";
		}
		dwr.util.byId('popup.image.processing' + id).style.display = 'none';
		dwr.util.setValue('popup.message' + id,dwrResponse.responseMessage);
		dwr.util.setValue('popup.status' + id,status);
		document.getElementById('ok.popup').disabled = false;
		if(nodeid!='')
		{
			fillAllServices();
			selectedHdfsNode.push(nodeid);
			Util.removeLightbox("addnn");
			startHdfsNode(true);
		}
		Navbar.fillAllQueryIONameNode();
		Navbar.refreshNavBar();
	}
}
function hdfsNodeAdded(dwrResponse)
{
	populateAddHdfsServiceList(false, dwrResponse)	
}
function populateHdfsServicePopUpList(flag,dwrResponse)
{
	var id='';
	var img_src='';
	var status='';
	if(flag){
		dwr.util.setValue('popup.component','NameNode');
		for (var i = 0; i <selectedHdfsNode.length ; i++)
		{
			id = selectedHdfsNode[i];
				var nodeType = 'namenode';
						dwr.util.setValue("node.host" + id,id);
						dwr.util.cloneNode('pop.pattern',{ idSuffix:id });
						dwr.util.setValue('popup.host' + id,id);
						dwr.util.setValue('popup.message' + id,popupmsg+id);
						dwr.util.setValue('popup.status' + id,'Processing');
						dwr.util.byId('pop.pattern' + id).style.display = '';
			
		}
		callBackFunc(false);
	}
	else{
		
		id=dwrResponse.id;
		if(dwrResponse.taskSuccess){
			img_src='images/Success_img.png'
			status = 'Success'; 
			dwr.util.byId('popup.image.success' + id).style.display = '';
		}
		else{
			img_src='images/Fail_img.png'
			status = 'Fail';
			dwr.util.byId('popup.image.fail' + id).style.display = '';
			var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
			document.getElementById('log_div'+ id).innerHTML=log;
			document.getElementById('log_div'+ id).style.display="block";
		}
		dwr.util.byId('popup.image.processing' + id).style.display = 'none';
		dwr.util.setValue('popup.message' + id,dwrResponse.responseMessage);
		dwr.util.setValue('popup.status' + id,status);
		if(endNode==id){
			document.getElementById('ok.popup').disabled = false;
			if (isDeleteNameNode)
				isDeleteNameNode = false;
		}
		Navbar.refreshNavBar();
		
	}
}
function closePopUpBox()
{
	Navbar.isRefreshPage = true;
	Util.removeLightbox("addnn");
	navigationClickHandlerHdfsService();
}

function navigationClickHandlerHdfsService()
{
	Navbar.refreshNavBar();
	Navbar.changeTab('TreeNameNode','nn_summary');

}

function handleButton(val)
{
	dwr.util.byId('delete.service').disabled=val;
	dwr.util.byId('start.service').disabled=val;
	dwr.util.byId('stop.service').disabled=val;
	dwr.util.byId('filecheck.service').disabled=val;
	dwr.util.byId('balancer.service').disabled=val;
}

function deleteHdfsNameNode()
{
	isDeleteNameNode = true;
	checkStartedDataNodes();
}

function deleteHdfsNode(flag)
{
	popupmsg = 'Deleting';
	if(flag){
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		var isAnyNodeIsHA = false;
		for (var i=0; i<selectedHdfsNode.length; i++) {
			var nodeType = $('#nodeType' + selectedHdfsNode[i]).text();
			if(nodeType != 'Non-HA') {
				isAnyNodeIsHA = true;
				break;
			}
		}
		if(isAnyNodeIsHA) {			
			jConfirm('This operation will remove your HA cluster. Do you want to delete anyway? (To remove an HA cluster, please delete both HA Namenodes.)','',function(value){
				if(value==true){
					
					jConfirm('All the contents in the given installation directory will be deleted. Are you sure you want to delete this node at given location?','',function(val){
						if (val== true){
							callBackFunc = deleteHdfsNode;
							Util.addLightbox("addnn", "pages/popup.jsp", null, null);
						}else{
							return ;
						}
					});
				}else{
					return;
				}
				
				jQuery.alerts.okButton = ' Ok ';
				jQuery.alerts.cancelButton  = 'Cancel';
				
			});
		}
		else
		{
			jConfirm('All the contents in the given installation directory will be deleted. Are you sure you want to delete this node at given location?','',function(val){
				if (val== true){
					callBackFunc = deleteHdfsNode;
					Util.addLightbox("addnn", "pages/popup.jsp", null, null);
				}else{
					return ;
				}
			});
		}
		
		
		$("#popup_container").css("z-index","99999999");
	}
	else
	{
		for (var i=0; i<selectedHdfsNode.length; i++)
		{
			if (i == (selectedHdfsNode.length-1))
				endNode = selectedHdfsNode[i];
			//DWR call for delete node by node id.
			if (isDeleteNameNode)
				RemoteManager.deleteNameNode(selectedHdfsNode[i], isRestartDataNode, hdfsNodeDeleted);
			else
				RemoteManager.deleteNode(selectedHdfsNode[i], hdfsNodeDeleted);
		}
	}
	
	
}

function hdfsNodeDeleted(dwrResponse)
{
	populateHdfsServicePopUpList(false, dwrResponse);
}

function startHdfsNode(flag)
{
	popupmsg = 'Start operation performed on NameNode  ';
	if(flag){
		callBackFunc = startHdfsNode;
		Util.addLightbox("addnn", "pages/popup.jsp", null, null);
	}
	else
	{
		for (var i=0; i<selectedHdfsNode.length; i++)
		{
			if (i == (selectedHdfsNode.length-1))
				endNode = selectedHdfsNode[i];
			//DWR call for start node by node id.
			RemoteManager.startNode(selectedHdfsNode[i] , false , hdfsNodeStarted);
		}
	}
	
}

function hdfsNodeStarted(dwrResponse)
{
	populateHdfsServicePopUpList(false, dwrResponse);
	
}


function stopHdfsNode(flag)
{
	if(flag){
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm("This will stop selected NameNode. Are you sure?","Stop NameNode",function(confirm){
			if(confirm){
				popupmsg = 'Stop operation performed on NameNode  ';
				
					callBackFunc = stopHdfsNode;
					Util.addLightbox("addnn", "pages/popup.jsp", null, null);
				}
			else{
				return;
			}
			jQuery.alerts.okButton = 'Ok';
			jQuery.alerts.cancelButton  = 'Cancel';
		});
		$("#popup_container").css("z-index","99999999");
	}
	else
	{
		for (var i=0; i<selectedHdfsNode.length; i++)
		{
			if (i == (selectedHdfsNode.length-1)){
				endNode = selectedHdfsNode[i];
			}
			RemoteManager.stopNode(selectedHdfsNode[i], hdfsNodeStopped);
		}
	}
}
function startMonitoring(flag){
	
	popupmsg = 'Monitoring service is started on NameNode ';
	
	if(flag){
		callBackFunc = startMonitoring;
		Util.addLightbox("addnn", "pages/popup.jsp", null, null);
	}
	else
	{
		for (var i=0; i<selectedHdfsNode.length; i++)
		{
			if (i == (selectedHdfsNode.length-1))
				endNode = selectedHdfsNode[i];
			
			RemoteManager.setNodeMonitor(selectedHdfsNode[i],true,handleMonitoringServiceResponse);
		}
	}
	
}
function stopMonitoring(flag){
	
	popupmsg = 'Monitoring service is stoped on NameNode ';
	
	if(flag){

		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm("Are you sure you want to stop monitoring of node?",'Monitoring is running',function(val)
		{
		
			if (val){
				callBackFunc = stopMonitoring;
				Util.addLightbox("addnn", "pages/popup.jsp", null, null);
			}
			else
			{
				return;
			}
			
			
		});
		jQuery.alerts.okButton = ' Ok';
		jQuery.alerts.cancelButton  = ' Cancel';
		$("#popup_container").css("z-index","99999999");
		
	}
	else
	{
		for (var i=0; i<selectedHdfsNode.length; i++)
		{
			if (i == (selectedHdfsNode.length-1))
				endNode = selectedHdfsNode[i];
			RemoteManager.setNodeMonitor(selectedHdfsNode[i],false,handleMonitoringServiceResponse);
		}
	}
	
}

function handleMonitoringServiceResponse(dwrResponse){
	populateHdfsServicePopUpList(false, dwrResponse);
}


function fsck(flag){
		
	popupmsg = 'Health check running on NameNode ';
	document.getElementById('filecheck.service').disabled = true;
	if(flag){
		callBackFunc = fsck;
		Util.addLightbox("addnn", "pages/popup.jsp", null, null);
	}
	else
	{
		for (var i=0; i<selectedHdfsNode.length; i++)
		{
			if (i == (selectedHdfsNode.length-1))
				endNode = selectedHdfsNode[i];
			//DWR call for start node by node id.
			RemoteManager.runFSCKCommand(selectedHdfsNode[i], handleFsckResponse);
		}
	}
	
}

function handleFsckResponse(dwrResponse){
	populateHdfsServicePopUpList(false, dwrResponse);
}

function hdfsNodeStopped(dwrResponse)
{
	populateHdfsServicePopUpList(false, dwrResponse);
	
}

function balancer(flag){
	
	popupmsg = 'Balancer service running on NameNode ';
	
	document.getElementById('balancer.service').disabled = true;
	if(flag){
		callBackFunc = balancer;
		Util.addLightbox("addnn", "pages/popup.jsp", null, null);
	}
	else
	{
		for (var i=0; i<selectedHdfsNode.length; i++)
		{
			if (i == (selectedHdfsNode.length-1))
				endNode = selectedHdfsNode[i];
			//DWR call for start node by node id.
			RemoteManager.runBalancer(selectedHdfsNode[i], handleBalancerResponse);
		}
	}
	
	
}

function handleBalancerResponse(dwrResponse){	
	populateHdfsServicePopUpList(false, dwrResponse);

}
