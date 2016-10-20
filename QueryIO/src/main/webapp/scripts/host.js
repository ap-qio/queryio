var selectedHost = [];
var hostCache;
var endHost ;
var end=false;
var callBackFunc='';
var hostForm='';
var typeHostForm;
var flagForSave=false;

function fillAllHosts()
{
	dwr.util.byId('delete.host').disabled=true;
	RemoteManager.getAllHostDetails(populateHostList);
}

function populateHostList(list)
{
	hostCache = new Array();
	var host;
	var id;
	for (var i = 0; i < list.length; i++)
	{
		host = list[i];
		id = host.id;
		
		dwr.util.cloneNode('pattern',{ idSuffix:id });
		dwr.util.byId('mark' + id).checked = false;
		dwr.util.setValue('host.ip' + id,host.hostIP);
		dwr.util.setValue('host.installDir' + id, host.installDirPath);
		dwr.util.byId('pattern' + id).style.display = '';
		if(i%2!=0){
			dwr.util.byId('pattern' + id).className = "coloredRow";
		}
		hostCache[id] = host;
	}
}

function clickCheckBox(chkbxid)
{
	var hostId=chkbxid.substring(4,chkbxid.length);
	if(dwr.util.byId(chkbxid).checked)
	{
		selectedHost.push(hostId);
		handleButton(false);
	}
	else
	{
		var index = selectedHost.indexOf(hostId);
		selectedHost.splice(index, 1);
		if (selectedHost.length > 0)
		{
			handleButton(false);
		}
		else
		{
			handleButton(true);
		}
	}
	if(selectedHost.length>0){
		dwr.util.byId('delete.host').disabled=false;
	}
	
}

function handleButton(val)
{
	document.getElementById('delete.host').disabled = val;
}

function deleteHostClicked(flag)
{
	
	if(flag){
		var val =false; 
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
			jAlert('All the contents in the given installation directory will be deleted. Are you sure you want to delete this host at given location?','',function(rr){
				val=rr;
				jQuery.alerts.okButton = ' OK ';
				 jQuery.alerts.cancelButton  = ' Cancel ';
			});
			
		$("#popup_container").css("z-index","99999999");
	    if (val== true){
			callBackFunc ='deleteHostClicked';
			addLightbox("Host", "pages/popup.jsp", null, null);
	    }else{
	        return ; 
	    }
	}
	else
	{
		for (var i=0; i<selectedHost.length; i++)
		{
			if (i == (selectedHost.length-1))
				endHost=selectedHost[i];
			//DWR call for stop node by node id.
			RemoteManager.deleteHost(selectedHost[i], hostDeleted);
		}
	}
}

function hostDeleted(dwrResponse)
{
	populatePopUpList(false, dwrResponse);
}

function saveHostClicked(flag, type)
{
	if($('#hostIP').val()==''){
		jAlert("HostName/IP was not provided.","Insufficient Details");
		$("#popup_container").css("z-index","99999999");
		return;
	}else if($('#hostUserName').val()==''){
		jAlert("UserName was not provided.","Insufficient Details");
		$("#popup_container").css("z-index","99999999");
		return;
	}else if($('#hostUserName').val()==''){
		jAlert("UserName was not provided.","Insufficient Details");
		$("#popup_container").css("z-index","99999999");
		return;
	}else if($('#authenticationMethod').val()=='password'){
	
		 if($('#password').val()==''){
				jAlert("PassWord was not provided.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
		
	}else if($('#authenticationMethod').val()=='privateKey'){
		
		 if($('#privateKey').val()==''){
				jAlert("Private Key was not provided.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
		
	}else if($('#installDirPath').val()==''){
		jAlert("Installation directory was not provided.","Insufficient Details");
		$("#popup_container").css("z-index","99999999");
		return;
	}else if($('#rackName').val()==''){
		jAlert("Rack Name  was not provided.","Insufficient Details");
		$("#popup_container").css("z-index","99999999");
		return;
	}else if($('#agentPort').val()==''){
		jAlert("QueryIO Agent Port was not provided.","Insufficient Details");
		$("#popup_container").css("z-index","99999999");
		return;
	}else if(Util.isContainWhiteSpace($('#installDirPath').val())){
		jAlert("Installation directory contains space. Please remove space from Installation directory.","Insufficient Details");
		$("#popup_container").css("z-index","99999999");
		return;
	}
	
	var agentPortValue =  $('#agentPort').val();
	var oneChar ;
	for (var i = 0; i < agentPortValue.length; i++) {
	    oneChar = agentPortValue.charAt(i).charCodeAt(0);
	    if (oneChar < 48 || oneChar > 57) {
	        jAlert("QueryIO Agent Port number must be a Integer.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
	}
	
	
	var password = $('#password').val();
	var privatekey = $('#privateKey').val();
	var javaHome = $('#javaHome').val();
	if(javaHome==""){
		jAlert("Java_Home was not provided for run hdfs services on host.Please provide valid JAVA_HOME.","Insufficient Details");
		$("#popup_container").css("z-index","99999999");
		return;
	}
	if(password==""){
			password=null;
	}
	if(privatekey==""){
		privatekey=null;
	}
	
	var val = false; 
	hostForm = dwr.util.byId('hostForm');
	callBackFunc = 'saveHostClicked';
	if (type == "nn")
		$("#addnn").load("pages/popup.jsp");
	else if(type == "chn")
		$("#addchpn").load("pages/popup.jsp");
	else
		$("#adddn").load("pages/popup.jsp");

	handleSaveClicked(true);

	$("#popup_container").css("z-index","99999999");
}
function validateJavaHomeBeforeSave(flag , type)
{
	flagForSave = flag;
	typeHostForm = type;
	var password = $('#password').val();
	var privatekey = $('#privateKey').val();
	var javaHome = $('#javaHome').val();
	var port = $('#port').val();
	if(javaHome==""){
		jAlert("Java_Home was not provided for run hdfs services on host.Please provide valid JAVA_HOME.","Insufficient Details");
		$("#popup_container").css("z-index","99999999");
		return;
	}
	if(password==""){
			password=null;
	}
	if(privatekey==""){
		privatekey=null;
	}
	RemoteManager.validateJavaHome($('#hostIP').val(), $('#hostUserName').val(),  password, privatekey,javaHome, port, validateJavaHomeAndSave);
}
function validateJavaHomeAndSave(isValidationSuccess){
	if(isValidationSuccess){
		saveHostClicked(flagForSave, typeHostForm);
	}else{
		jAlert("JAVA_HOME provided for host is not valid.Please provide a valid JAVA_HOME .","Error");
		$("#popup_container").css("z-index","99999999");
		return;
	}
}

function hostSaved(dwrResponse)
{	
	populateAddHostList(false, dwrResponse)
}

function handleSaveClicked(val)
{
	dwr.util.byId('save.host').disabled = val;
	dwr.util.byId('cancel.host').disabled = val;
}

function populatePopup(response)
{
//	var status;
//	var message = "";
//	if (response.startsWith("0"))
//	{
//		status = "Failed";
//		image = "../images/ap.png"
//	}
//	else
//	{
//		status = "Success";
//		image = "../images/tick.png"
//	}
//	dwr.util.setValue('popup.message' + index, message);
//	dwr.util.setValue('popup.status' + index, status);
//	dwr.util.byId('popup.image' + index).style.background = image;
//	index ++;
}

function navigationClickHandlerHost()
{
	loadPage("resources/dashboard.html");
}

function loadPage(pageToLoad)
{
	var page = pageToLoad;
	$("#service_ref").load(page);
}
function selectAllHostRow(element)
{
	var val = element.checked;
	for (var i=0;i<document.forms[0].elements.length;i++)
 	{
 		var e=document.forms[0].elements[i];
 		if ((e.id != 'selectAll') && (e.id != 'mark') && (e.type=='checkbox'))
 		{
 				e.checked=val;
 				clickCheckBox(e.id);
 		}
 	}
}
function fillPopUp(flag)
{
	if(callBackFunc =='deleteHostClicked')
	{
		populatePopUpList(flag,null,null);
	}
	else
	{
		populateAddHostList(true);
	}
}
function populateAddHostList(flag , dwrResponse)
{
	var id ;
	if(hostForm!=''){
		
		id = hostForm.hostIP.value;
	
	if(flag){
		dwr.util.cloneNode('pop.pattern',{ idSuffix:id });
		dwr.util.setValue('popup.host' + id,id);
		dwr.util.setValue('popup.message' + id,'Adding host..');	
		dwr.util.setValue('popup.status' + id,'Processing');
		dwr.util.byId('pop.pattern' + id).style.display = '';
		var password = null;
		var privateKey = null;
		if(hostForm.authenticationMethod.value=='password'){
			password = hostForm.password.value;
		}else{
			privateKey=hostForm.privateKey.value;
		}
		
		RemoteManager.insertHost(hostForm.hostIP.value,hostForm.hostUserName.value,password,privateKey, hostForm.installDirPath.value, 
				hostForm.rackName.value, hostForm.agentPort.value,hostForm.javaHome.value,hostForm.port.value, hostSaved);
	}
	else{
		var isSuccess = dwrResponse.taskSuccess;
		id = dwrResponse.id;
		var status='';
		var log='';
		if(isSuccess)
		{
			img_src='images/Success_img.png'
			status = 'Success'; 
			dwr.util.byId('popup.image.success' + id).style.display = '';
		}
		else
		{
			img_src='images/Fail_img.png'
			status = 'Fail';
			dwr.util.byId('popup.image.fail' + id).style.display = '';
			
			log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
			
			document.getElementById('log_div'+ id).innerHTML=log;
			document.getElementById('log_div'+ id).style.display="block";
			
		}
		dwr.util.byId('popup.image.processing' + id).style.display = 'none';
		dwr.util.setValue('popup.message' + id,dwrResponse.responseMessage);
		dwr.util.setValue('popup.status' + id,status);
		document.getElementById('ok.popup').disabled = false;
		if(isSuccess)
		{
			if (typeHostForm == "nn")
			{
				Util.removeLightbox("addnn");
				setTimeout('Util.addLightbox("addnn", "resources/add_nn.html", null, null)',1000);
//				Util.addLightbox("addnn", "resources/add_nn.html", null, null);
				
			}
			else if(typeHostForm=="dn")
			{
				Util.removeLightbox("adddn");
				setTimeout('Util.addLightbox("adddn", "resources/add_dn.html", null, null)',1000);
//				Util.addLightbox("adddn", "resources/add_dn.html", null, null);
			}
			else if(typeHostForm=="chn")
			{
				Util.removeLightbox("addchpn");
				setTimeout('Util.addLightbox("addchpn", "resources/add_check_point.html", null, null)',1000);
//				Util.addLightbox("adddn", "resources/add_dn.html", null, null);
			}
			else{
//				Util.removeLightbox("adddn");
			}
		}
		Navbar.refreshView();
	}
	}
}

function populatePopUpList(flag,dwrResponse)
{
	var id='';

	end = false;
	var img_src='';
	var status='';

	if(flag)
	{
		for (var i = 0; i <selectedHost.length ; i++)
		{
			id = selectedHost[i];
			if(hostCache[id]!=undefined)
			{
				dwr.util.cloneNode('pop.pattern',{ idSuffix:id });
				dwr.util.setValue('popup.host' + id,hostCache[id].hostIP);
				dwr.util.setValue('popup.message' + id,'');	
				dwr.util.setValue('popup.status' + id,'Processing');
				dwr.util.byId('pop.pattern' + id).style.display = '';
			}
		}
		deleteHostClicked(false);
	}
	else
	{
		id=dwrResponse.id;
		var isSuccess = dwrResponse.taskSuccess;
		if(isSuccess)
		{
			img_src='images/Success_img.png'
			status = 'Success'; 
			dwr.util.byId('popup.image.success' + id).style.display = '';
		}
		else
		{
			img_src='images/Fail_img.png'
			status = 'Fail';
			dwr.util.byId('popup.image.fail' + id).style.display = '';
		}
		dwr.util.byId('popup.image.processing' + id).style.display = 'none';
		dwr.util.setValue('popup.message' + id,dwr.responseMessage);
		dwr.util.setValue('popup.status' + id,status);
		if(endHost==id)
		{
			document.getElementById('ok.popup').disabled = false;
		}
		Navbar.refreshView();
	}
}
function closePopUpBox()
{
	if (typeHostForm == "nn")
	{
		removeLightbox("addnn");
	}
	else if(typeHostForm == "chn")
	{
		removeLightbox("addchpn");
	}
	else
	{
		removeLightbox("adddn");
	}
//	navigationClickHandlerHost();
}
function setAgentPort(port){
	$('#agentPort').val(port);
}
function fillQueryIOAgentPort(){
	RemoteManager.getQueryIOAgentPort(setAgentPort);
}
function viewLogFile(hostIp,hostPort,installDir){
	
	var url ="http://"+hostIp+":"+hostPort+"/agentqueryio/log?node-type=Agent&host-dir="+installDir+"&file-type=log";
	window.open(url);
	return;
}


function hostNextStep(step){
	if(step==1){
		$('#hostStep1').css('display','');
		$('#hostStep2').css('display','none');
		
		
	}else{
		document.getElementById('next').disabled = false;
		if($('#hostIP').val()==''){
			jAlert('Please enter Hostname/IP');
			$("#popup_container").css("z-index","99999999");
			return;
		}
		if($('#hostUserName').val()==''){
			jAlert('SSH user was not entered. Please provide SSH User for host.');
			$("#popup_container").css("z-index","99999999");
			return;
		}
		if($('#password').val()==''&&$('#privateKey').val()==''){
			jAlert('Password/Private Key was not entered. Please provide Password/Private Key for host.');
			$("#popup_container").css("z-index","99999999");
			return;
		}
		var password = $('#password').val();
		var privatekey = $('#privateKey').val();
		if(password==""){
				password=null;
		}
		if(privatekey==""){
			privatekey=null;
		}
		var port = $('#port').val();
		if(port == '' || port == null)
		{
			jAlert("SSH port not provided.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		else if(!port.match("^[0-9]{1,6}$"))
		{
			jAlert("SSH port can not be character.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		document.getElementById('next').disabled = true;
		RemoteManager.getUserHomeDirectory($('#hostIP').val(), $('#hostUserName').val(),  password, privatekey,port,fillInstallationValue);
		RemoteManager.getJavaHome($('#hostIP').val(), $('#hostUserName').val(),  password, privatekey,port,fillJavaHomeValue);
		
		
	}
	function fillInstallationValue (val){
		document.getElementById('next').disabled = false;
		if(val==null||val==""){
			jAlert('It seems that credentials provided for hostname is invalid or host is down.','SSh Login Failed');
			$("#popup_container").css("z-index","99999999");
			return;
		}
		val=val.trim();
		$('#installDirPath').val(val);
		$('#hostStep1').css('display','none');
		$('#hostStep2').css('display','');
	}
	function fillJavaHomeValue (val){
		if(val==null||val==""){
			jAlert('Java Home not found.It seems that credentials provided for hostname is invalid or host is down.','Java Home Error');
			$("#popup_container").css("z-index","99999999");
			return;
		}
		val=val.trim();
		$('#javaHome').val(val);
		$('#hostStep1').css('display','none');
		$('#hostStep2').css('display','');
	}
}