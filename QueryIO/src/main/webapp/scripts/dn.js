var isUserWaiting=false;
//var diskMap;
var selectedHdfsNode = [];
var nodeCache;
var hostCache;
var end = false;
var lastId = [];
var call = 0;
var endNode;
var callBackFunc='';
var disks = [];
var volumePath = [];
var nameNodeId = [];
var nodeId;
var portNumberValues = [];
var serverPort;
var httpPort;
var httpsPort;
var ipcPort;
var jmxPort;
var counter = 1;
var dataNodeForm='';
var popupid='';
var hostIP='';
function fillHostNames()
{
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
function populateAddNameNodeHostForm(list)
{
	selectList = dwr.util.byId('rack');
	dwr.util.removeAllOptions(selectList);
	addOption(selectList, 0, 'Select Rack Name');
	hostNames = 0;
	for (var i = 0; i < list.length; i++)
	{
		addOption(selectList, list[i].id, list[i].name);
	}
	dwr.util.setValue('cluster', hostNames);
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
	handleButton(true);
//	populateHdfsService();
}
function populateHdfsService()
{
	RemoteManager.getAllHostDetails(loadHostCache);
	RemoteManager.getAllDataNodeDetails(populateHdfsServiceList);
	handleButton(true);
}
function loadHostCache(list)
{
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
	if(document.getElementById('pattern')==undefined||document.getElementById('pattern')==null)return;
	dwr.util.removeAllRows("nodes", { filter:function(tr)
		{
			return (tr.id != "pattern");
		}
	});
	nodeCache = new Array();
	var node;
	var id='';
	for (var i = 0; i < list.length; i=i+2)
	{
		node = list[i];
		id=node.id;
		lastId.push(id);
		host=hostCache[node.hostId];
		dwr.util.cloneNode("pattern",{ idSuffix:id });
		dwr.util.byId("mark" + id).checked=false;
		setIp(host);
		dwr.util.byId('pattern' + id).style.display = '';
		if(i%2!=0){
			dwr.util.byId('pattern' + id).className = "coloredRow" 
		}
		nodeCache[id] = node;
	}
}
function setIp(host){
	if(host==null)return;
	 dwr.util.setValue("node.host" + lastId[call],host.hostIP);
	 call++;
}

function loadPage(pageToLoad)
{
	$("#service_ref").load(pageToLoad);
}
function saveDataNode()
{
	nodeType = "datanode";
	if (dwr.util.byId('hostForNode').value == 0)
	{
		jAlert("No Host Selected","Incomplete Detail");
		$("#popup_container").css("z-index","9999999");
		return;
	}
	var rowIds = getRowId('all');
	serverPort = dwr.util.byId('serverPort').value;
	httpPort = dwr.util.byId('httpPort').value;
	httpsPort = dwr.util.byId('httpsPort').value;
	ipcPort = dwr.util.byId('ipcPort').value;
	jmxPort = dwr.util.byId('jmxPort').value;
	nodeId = dwr.util.byId('id').value;
	disks = [];
	volumePath = [];
	for(var i = 0; i < rowIds.length; i++){
		var temp1 = $('#select-' + rowIds[i]).val();
		var temp2 = $('#volume-' + rowIds[i]).val();
		if(temp1 == "0"){
			jAlert("Incomplete Volume Info. Disk -" + i + " is not selected.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		if(temp2 == ""){
			jAlert("Incomplete Volume Info. Volume Path -" + i + " is empty.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
			return;
		}
//		if(Util.isContainWhiteSpace(temp2)){
//			jAlert("Volume Path - " + i + " contains space. Please remove space from Volume Path - " + i ,"Incomplete Detail");
//			$("#popup_container").css("z-index","99999999");
//			return;
//		}
		disks.push(temp1);
		volumePath.push(temp2);
	}
	if(disks.length == 0){
		jAlert("Incomplete Disk detail","Incomplete Detail");
		$("#popup_container").css("z-index","99999999");
		return;
	}
	var hostName =$("#hostForNode option:selected").text(); 
	hostIP = hostName;
	validateDiskVolumePath(disks,volumePath);
}
function validateDiskVolumePath(disks,volumePath){
	
	dataNodeForm = dwr.util.byId('dataNodeForm');
	callBackFunc ='saveDataNode';
	$("#adddn").load("pages/popup.jsp");
	
//	if(diskMap==null){
//		jAlert("Disk-volume mapping are not received from Host.It will take some time .After complition we will notify you for save node.");
//		$("#popup_container").css("z-index","99999999999");
//		isUserWaiting =true;
//	}
//	var statusArray = new Array();
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
//					$("#popup_container").css("z-index","99999999");
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
//	validateVolumePath(statusArray);
}


//function validateVolumePath(statusArray)
//{
//	
////	for(var i = 0;i<statusArray.length;i++){
////		if(!statusArray[i]){
////			jAlert('Invalid mapping of Voulme: "'+$('#volume-'+(i+1)).val()+'" with Disk "'+$('#select-'+(i+1)).val()+'"','Invalid Detail');
////			$("#popup_container").css("z-index","99999999");
////			return;
////		}
////	}
//	
////	jQuery.alerts.okButton = ' Yes ';
////	jQuery.alerts.cancelButton  = ' No';
////	jConfirm("All the contents in the given installation directory will be deleted. Are you sure you want to install node at given location?",'Add Node',function(val){
////		if (val== true)
////		{
//			dataNodeForm = dwr.util.byId('dataNodeForm');
//			callBackFunc ='saveDataNode';
//			$("#adddn").load("pages/popup.jsp");
////		}else{
////			return ; 
////		}	
////		jQuery.alerts.okButton = ' Yes ';
////		jQuery.alerts.cancelButton  = ' No';
////	});
////	$("#popup_container").css("z-index","99999999");
//}

function fillPopUp(flag)
{

	if(callBackFunc =='saveDataNode')
	{
		populateAddHdfsServiceList(true);
	}
	else{
		populateHdfsServicePopUpList(flag,null);
	}
}
function populateAddHdfsServiceList(flag , dwrResponse)
{
	var id = nodeId;
	if(flag)
	{	
		var isLocal = false;
		if(hostIP=="127.0.0.1" || hostIP == '$SSH_HOSTNAME$')
		{
			isLocal = true;
		}
		dwr.util.cloneNode('pop.pattern',{ idSuffix:id });
		dwr.util.setValue('popup.component','DataNode');
		dwr.util.setValue('popup.host' + id,id);
		dwr.util.setValue('popup.message' + id,'Installing Node.');	
		dwr.util.setValue('popup.status' + id,'Processing');
		dwr.util.byId('pop.pattern' + id).style.display = '';
		RemoteManager.addDataNode(dataNodeForm.hostForNode.value, nodeId,  serverPort, httpPort, httpsPort, ipcPort, jmxPort, disks, volumePath, isLocal, hdfsNodeAdded);
	}
	else
	{
		id = dwrResponse.id;
		var nodeid = -1;
		if(dwrResponse.taskSuccess)
		{
			img_src='images/Success_img.png'
			status = 'Success'; 
			dwr.util.byId('popup.image.success' + id).style.display = '';
			if(dataNodeForm.startnodechkbox.checked)
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
		if(nodeid!=-1){
			fillAllServices();
			selectedHdfsNode.push(nodeid);
			Util.removeLightbox("adddn");
			startHdfsNode(true);
		}
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
		dwr.util.setValue('popup.component','DataNode');
		for (var i = 0; i <selectedHdfsNode.length ; i++)
		{
			id = selectedHdfsNode[i];
			if(id!=undefined){
				var nodeType = 'datanode';
						dwr.util.setValue("node.host" + id,id);
						dwr.util.cloneNode('pop.pattern',{ idSuffix:id });
						dwr.util.setValue('popup.host' + id,id);
						dwr.util.setValue('popup.message' + id,'Performing service operation.');	
						dwr.util.setValue('popup.status' + id,'Processing');
						dwr.util.byId('pop.pattern' + id).style.display = '';
						popupid=id;
			}
		}
		callBackFunc(false);
	}
	else{
		id=dwrResponse.id
		if(dwr.util.byId('popup.image.fail' + id)==null||dwr.util.byId('popup.image.fail' + id)=='undefined')
			id=popupid;
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
		}
		Navbar.refreshNavBar();
	}
}
function closePopUpBox()
{
	Util.removeLightbox("adddn");
	Navbar.refreshNavBar();
	Navbar.refreshView();
}

function handleButton(val)
{
	if(val){
		$('#start.service').attr("disabled", "disabled").addClass("ui-state-disabled");
		$('#stop.service').attr("disabled", "disabled").addClass("ui-state-disabled");
		$('#delete.service').attr("disabled", "disabled").addClass("ui-state-disabled");
		$('#decommission.service').attr("disabled", "disabled").addClass("ui-state-disabled");
	}
}
function deleteHdfsNode(flag)
{
	if(flag){
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		 jConfirm('All the contents in the given installation directory will be deleted. Are you sure you want to delete this node ?','Delete Host',function(val){
			 if (val== true){
				 callBackFunc = deleteHdfsNode;
				 addLightbox("adddn", "pages/popup.jsp", null, null);
			 }else{
				 return ; 
			 }
			 jQuery.alerts.okButton = ' Ok ';
		     jQuery.alerts.cancelButton  = ' Cancel';
		});
		$("#popup_container").css("z-index","99999999");
	}
	else
	{
		for (var i=0; i<selectedHdfsNode.length; i++)
		{
			if (i == (selectedHdfsNode.length-1))
				endNode = selectedHdfsNode[i];
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
	if(flag){
		callBackFunc = startHdfsNode;
		addLightbox("adddn", "pages/popup.jsp", null, null);
	}
	else
	{
		for (var i=0; i<selectedHdfsNode.length; i++)
		{
			if (i == (selectedHdfsNode.length-1))
				endNode = selectedHdfsNode[i];
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
		jConfirm("This will stop selected DataNode. Are you sure?","Stop DataNode",function(confirm){
			if(confirm){
				//popupmsg = 'Stop operation performed on NameNode  ';
				callBackFunc = stopHdfsNode;
				addLightbox("adddn", "pages/popup.jsp", null, null);
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
			if (i == (selectedHdfsNode.length-1))
				endNode = selectedHdfsNode[i];
			RemoteManager.stopNode(selectedHdfsNode[i], hdfsNodeStopped);
		}
	}
}

function hdfsNodeStopped(dwrResponse)
{
	populateHdfsServicePopUpList(false, dwrResponse);
	
}

function decommissionHdfsNode(flag)
{
	
	if(flag){
		callBackFunc = decommissionHdfsNode;
		addLightbox("adddn", "pages/popup.jsp", null, null);
	}
	else
	{
		for (var i=0; i<selectedHdfsNode.length; i++)
		{
			if (i == (selectedHdfsNode.length-1))
				endNode = selectedHdfsNode[i];
			RemoteManager.decommission(selectedHdfsNode[i],handleDecommissioningResponse);
		}
	}
		
}

function handleDataNodeMonitoringResponse(dwrResponse){
	populateHdfsServicePopUpList(false, dwrResponse);
}

function startDataNodeMonitoring(flag)
{
	
	if(flag){
		callBackFunc = startDataNodeMonitoring;
		addLightbox("adddn", "pages/popup.jsp", null, null);
	}
	else
	{
		for (var i=0; i<selectedHdfsNode.length; i++)
		{
			if (i == (selectedHdfsNode.length-1))
				endNode = selectedHdfsNode[i];
//			RemoteManager.decommission(selectedHdfsNode[i],handleDataNodeMonitoringResponse);
			RemoteManager.setNodeMonitor(selectedHdfsNode[i],true,handleDataNodeMonitoringResponse);
		}
	}
		
}
function stopDataNodeMonitoring(flag)
{
	
	if(flag){
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm("Are you sure you want to stop monitoring of this node?",'Monitoring is running',function(val)
		{
		
			if (val){
				callBackFunc = stopDataNodeMonitoring;
				Util.addLightbox("adddn", "pages/popup.jsp", null, null);
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
			RemoteManager.setNodeMonitor(selectedHdfsNode[i],false,handleDataNodeMonitoringResponse);
		}
	}
		
}

function handleDecommissioningResponse(dwrResponse){
	populateHdfsServicePopUpList(false, dwrResponse);
}

function addVolumeClicked(){
	if(counter==10){
        jAlert("Only 10 Volumes are allowed","Volumes exceed");
        $("#popup_container").css("z-index","9999999");
        return false;
	}   
	var temp = counter + 1
	var volumePath = $('#volume-0').val();
	var tbl_data  = '<tr id = "row_'+temp+'">'+
	'<td><select id ="select-'+temp+'">'+$('#select-0').html()+' </select></td>'+
	'<td><input type="text" id ="volume-'+temp+'" value="'+volumePath+temp+'" ></td>'+
	'<td><a href="javascript:addVolumeClicked();"> <img alt="Add Volume" src="images/plus_sign_brown.png" id="plusImage" style="height: 15px;"></a><a style="color: white;" href="javascript:removeVolumeClicked('+temp+');"> <img alt="Remove Volume" src="images/minus_sign_brown.png" id="minusImage" style="height: 10px; width: 20px;"></a>'+ 
	'<input type="checkbox" value="1" onclick="javascript:DN_Summary.selectAllHostRow(this)" id="'+temp+'" style="display: none;"></td></tr>';
	
	
	$('#volInfotbl').append(tbl_data);

	counter = temp;
}

function removeVolumeClicked(id){
	if(counter==1){
          jAlert("No more Volumes to remove","Invalid action");
          $("#popup_container").css("z-index","9999999");
          return ;
    }   
	$("#row_"+id).remove();
	counter--;  
}

function addDnNextStep(step){
	
	switch (step){

		case 1 :{
			$('#div1').css('display','block');
			$('#div2').css('display','none');
			$('#div4').css('display','none');
			$('#headerMsg').text("Select a host for the DataNode, which stores data in the HDFS. A functional filesystem has more than one DataNode, with data replicated across them ");
			break;
		}
		case 3 :{
			var hostName =$("#hostForNode option:selected").text();  
			if(hostName=='Select Host'){
				jAlert('Host name is not selected. Please select host name for DataNode.','Insufficient detail');
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if (dwr.util.byId('id').value == '')
			{
				jAlert("Node Unique Identifier was not specified. Please provide a valid Unique Identifier to configure DataNode.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if(dataNodeIdArray.indexOf(dwr.util.byId('id').value) != -1)
			{
				jAlert("Current DataNode Id is already taken by another DataNode. Please enter a new DataNode Id.","Invalid Id");
				$('#id').focus();
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if(Util.isContainSpecialChar(dwr.util.byId('id').value)){
				jAlert("DataNode Id contains special character.Please remove special character from Id.","Invalid Id");
					$("#popup_container").css("z-index","9999999");
				return;
			}
			$('#div1').css('display','none');
			$('#div2').css('display','none');
			$('#div4').css('display','block');
			$('#headerMsg').text("Please configure the required ports for DataNode, which stores data in the HDFS.");
			break;
		}
		case 2 :{
			var hostName =$("#hostForNode option:selected").text();  
			if (dwr.util.byId('serverPort').value == '')
			{
				jAlert("Server Port was not specified. Please provide a valid Server Port to configure DataNode.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if (dwr.util.byId('httpPort').value == '')
			{
				jAlert("HTTP Port was not specified. Please provide a valid HTTP Port to configure DataNode.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if (dwr.util.byId('httpsPort').value == '')
			{
				jAlert("HTTPS Port was not specified. Please provide a valid HTTPS Port to configure DataNode.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if (dwr.util.byId('ipcPort').value == '')
			{
				jAlert("IPC Port name was not specified. Please provide a valid IPC Port to configure DataNode.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if (dwr.util.byId('jmxPort').value == '')
			{
				jAlert("JMX Port name was not specified. Please provide a valid JMX Port to configure DataNode.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			$('#div1').css('display','none');
			$('#div2').css('display','block');
			$('#div4').css('display','none');
			$('#headerMsg').text(" Now select the disk in which data should be stored. All disks available at the host are displayed in a drop down list.Enter the volume path where data should be stored. This is local repository of DataNode on that host.");
			
			var hostName =$('#hostForNode option:selected').text();
			
			
			break;
		}
	}
	
	
}

function fillUserHome(val){
	$('#volume-1').val(val+'/QueryIONodes/DataNode/Volume-1');
	$('#volume-0').val(val+'/QueryIONodes/DataNode/Volume-');
}
function getPhysicalDiskName()
{
	var hostName =$("#hostForNode option:selected").text();  
	if(hostName=='Select Host'){
		return;
	}
	RemoteManager.getPhysicalDiskNames(hostName, fillvolumeInfoTable);

//	RemoteManager.getVolumeDiskMap(hostName,fillDiskMapCache);
	
	RemoteManager.getUserHomeDirectoryPathForHost(hostName,fillUserHome);
}
//function fillDiskMapCache(map){
//	diskMap = map;
//	if(isUserWaiting){
//		jAlert("Disk volume mapping received from server. Please add datanode now.")
//	}
//	isUserWaiting = false;
//}
function fillvolumeInfoTable(list)
{
	if(list==null){
		jAlert("No disk info found at host.","No Disk found");
		$("#popup_container").css("z-index","9999999");
		return;	
	}
	
	selectList = dwr.util.byId('select-0');
	dwr.util.removeAllOptions(selectList);
	addOption(selectList, 0, 'Select Disk');
	hostNames = 0;
	//sorting the disk list
	list.sort();
	//for(var t=0;t<list.length;t++)
	for (var i = 0; i < list.length; i++)
	{
		addOption(selectList, list[i], list[i]);
	}
	dwr.util.setValue('select-0', hostNames);
	$('#select-1').html($('#select-0').html());
	
}

function isPortNumberNumericFields()
{
	portNumberValues.splice(0 , portNumberValues.length);
	portNumberValues.push(dwr.util.byId('serverPort').value);
	portNumberValues.push(dwr.util.byId('httpPort').value);
	portNumberValues.push(dwr.util.byId('httpsPort').value);
	portNumberValues.push(dwr.util.byId('ipcPort').value);
	portNumberValues.push(dwr.util.byId('jmxPort').value);
	var isNumeric = Util.isNumericPortNumbers(portNumberValues);
	if(isNumeric)
	{
		addDnNextStep(2);
	}
	else
	{
		jAlert("Only integers are allowed in port number fields.","Incomplete Detail");
		$("#popup_container").css("z-index","9999999");
	}
}
function getRowId(state)
{
	var ids = new Array();
    var table = document.getElementById( 'volInfotbl' );
    var input = table.getElementsByTagName('input'); 
    for ( var z = 0; z < input.length; z++ ) { 
    	var e = input[z];
    	if (e.type=='checkbox')
 		{
 			if(e.id =='selectAll')
 			{
 				continue;
 			}
 			else if(state=='checked'&& e.checked)
 			{
 				ids.push(e.id);
 			}
 			else if(state=='all'){
 				ids.push(e.id);
 			}
 		}
    } 
    return ids;
}

