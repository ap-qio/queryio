var smsModelArray = new Array();
var nameElements = new Array();
var nameElementNames = new Array();
var latestDoc;
var notificationBean = new Array();

smsModelArray['Nokia'] = ['6070', '6100', '6210', '6310', '6310i', '6230', '6230i', '6681', '8250', '8310', '6610', '6800', '7210', '6810', '7250i', '6103', '6020', '3220', '6822', '5140', '5140i'];
smsModelArray['Siemens'] = ['MC35i', 'M35', 'M50', 'M65', 'C45', 'TC35i', 'C65', 'M55', 'TC65t'];
smsModelArray['Sony Ericsson'] = ['K800i', 'SE K800i', 'K700i', 'K750i', 'SE W850i', 'W880i', 'GC89', 'Z550a', 'W800', 'W580i', 'W810', 'i320', 'GT48'];
smsModelArray['Wavecom'] = ['M1206B', 'M1306B', 'WMOD2 Wismo', 'Fastrack Supreme 10', 'WISMOQCDMA CDMA'];
smsModelArray['Motorola'] = ['V3'];
smsModelArray['Billionton'] = ['PCMCIA PCGPRSQ-B'];
smsModelArray['ITengo'] = ['3000', 'WM1080A'];
smsModelArray['Janus'] = ['GSM864Q'];
smsModelArray['Sharp'] = ['GX30', 'GX32'];
smsModelArray['SIMCOM Ltd'] = ['SIMCOM_SIM100S'];
smsModelArray['Ubinetics'] = ['GDC201'];

/*function hideShowElement(name, count, show)
		{
			for (var i = 1; i <= count; i ++)
			{
				var elem = document.getElementById(name + '.' + i);
				if (elem)
				{
					elem.style.display = show ? '':'none';
				}
			}
		}*/
		
//trims the str for " " characters from left
function LTrim(str)
{
    if (str==null)
    {
        return null;
    }

    for(var i=0; str.charAt(i)==" "; i++);
    return str.substring(i,str.length);
}

// trims the str for " " characters from right
function RTrim(str)
{
    if (str==null)
    {
        return null;
    }

    for(var i=str.length-1;str.charAt(i)==" ";i--);
    return str.substring(0,i+1);
}

// trims the str for " " characters from left and right
function Trim(str)
{
    return LTrim(RTrim(str));
}

function onEmailEnabled(doc, src)
{
	var form = document.getElementById('emailNotificationForm');
	if (src.checked)
	{
		form.emailSenderName.disabled = false;
		form.emailSenderAddress.disabled = false;
		form.emailSMTPServer.disabled = false;
		form.emailSMTPPort.disabled = false;
		form.emailUsername.disabled = false;
		form.emailPassword.disabled = false;
		//form.validateEmailConfig.disabled = false;
		document.getElementById('validateEmail').disabled = false;
		document.getElementById('securedProtocol').disabled = false;
		document.getElementById('authRequired').disabled = false;
	}
	else
	{
		form.emailSenderName.disabled = true;
		form.emailSenderAddress.disabled = true;
		form.emailSMTPServer.disabled = true;
		form.emailSMTPPort.disabled = true;
		form.emailUsername.disabled = true;
		form.emailPassword.disabled = true;
		document.getElementById('securedProtocol').disabled = true;
		document.getElementById('authRequired').disabled = true;
		document.getElementById('validateEmail').disabled = true;
		//form.validateEmailConfig.disabled = true;
		
	}
}


function onAuthenticationReq(doc, src)
{
	var form = document.getElementById('emailNotificationForm');
	if(src.checked)
	{
		form.emailUsername.disabled = false;
		form.emailPassword.disabled = false;
	}
	else
	{
		form.emailUsername.disabled = true;
		form.emailPassword.disabled = true;
	}
}



function submitNotify(doc, update)
{
	/*if(checkLengthOfNames(nameElements, nameElementNames, errMsgForNameElementsLength))
	{*/
		var errFound = false;
		var errMsg;
		var mailform = document.getElementById('emailNotificationForm');
		var smsform = document.getElementById('smsNotificationForm');
		// email is enabled so some of the fields are required
		if(mailform.emailEnabled[0].checked)
		{
			if(Trim(mailform.emailSenderName.value) == '')
			{
				if(! errFound)
				{
					errMsg = errMsgForEmailEnabled;
					errFound = true;
				}
				errMsg = errMsg + '\n' + emailSenderNameVal;
			}
			if(Trim(mailform.emailSenderAddress.value) == '')
			{
				if(! errFound)
				{
					errMsg = errMsgForEmailEnabled;
					errFound = true;
				}
				errMsg = errMsg + '\n' + emailSenderAddressVal;
			}
			if(Trim(mailform.emailSMTPServer.value) == '')
			{
				if(! errFound)
				{
					errMsg = errMsgForEmailEnabled;
					errFound = true;
				}
				errMsg = errMsg + '\n' + emailSMTPServerVal;
			}
			if(Trim(mailform.emailSMTPPort.value) == '')
			{
				if(! errFound)
				{
					errMsg = errMsgForEmailEnabled;
					errFound = true;
				}
				errMsg = errMsg + '\n' + emailSMTPPortVal;
			}
			if(mailform.authRequired[0].checked)
			{
				if(Trim(mailform.emailUsername.value) == '')
				{
					if(! errFound)
					{
						errMsg = errMsgForEmailEnabled;
						errFound = true;
					}
					errMsg = errMsg + '\n' + emailUsername;
				}
				if(Trim(mailform.emailPassword.value) == '')
				{
					if(! errFound)
					{
						errMsg = errMsgForEmailEnabled;
						errFound = true;
					}
					errMsg = errMsg + '\n' + emailPassword;
				}
			}
		}
		if(smsform.smsEnabled[0].checked)
		{
			if(Trim(smsform.smsNumber.value) == '')
			{
				if(errFound)
				{
					errMsg = errMsg + '\n\n' + errMsgForSmsEnabled;
					errMsg = errMsg + '\n' + smsNumberVal;
					errFound = true;
				}
				else
				{
					errMsg = errMsgForSmsEnabled;
					errMsg = errMsg + '\n' + smsNumberVal;
					errFound = true;
				}
			}
			if(Trim(smsform.smsSerialPort.value) == '')
			{
				if(errFound)
				{
					errMsg = errMsg + '\n\n' + errMsgForSmsEnabled;
					errMsg = errMsg + '\n' + smsSerialPortVal;
					errFound = true;
				}
				else
				{
					errMsg = errMsgForSmsEnabled;
					errMsg = errMsg + '\n' + smsSerialPortVal;
					errFound = true;
				}
			}
			if(Trim(smsform.smsBaudRate.value) == '')
			{
				if(errFound)
				{
					errMsg = errMsg + '\n\n' + errMsgForSmsEnabled;
					errMsg = errMsg + '\n' + smsBaudRateVal;
					errFound = true;
				}
				else
				{
					errMsg = errMsgForSmsEnabled;
					errMsg = errMsg + '\n' + smsBaudRateVal;
					errFound = true;
				}
			}
		}
		if(errFound)
		{
			jAlert(errMsg, "Error");
			return;
		}

		// email is enabled so check whether the smtp port is valid or not
/*		if(doc.forms[0].emailEnabled[0].checked)
		{
			errFound = ! checkValidIntegers([document.forms[0].emailSMTPPort], [emailSMTPPortVal], errMsgForIntElements);
		}
		if(doc.forms[0].smsEnabled[0].checked)
		{
			errFound = ! checkValidIntegers([document.forms[0].smsBaudRate], [smsBaudRateVal], errMsgForIntElements);
		}
		if(! errFound)
		{
			doc.forms[0].validateClicked.value = update;
			doc.forms[0].submit();
		}
	//}
*/
}

function showNotificationLog()
{
	var path = $('#logFilePath').val();
	window.open("GetServerLog?file-type=notificationLog&LogPath="+path);
}

function saveNotify(doc)
{
	
	
	var mailform = document.getElementById('emailNotificationForm');
	var smsform = document.getElementById('smsNotificationForm');
	var logform = document.getElementById('logNotificationForm');
	
	if(mailform.emailEnabled[0].checked){
		
		for (var i=0;i<mailform.elements.length;i++)
	 	{
	 		var e=mailform.elements[i];
	 		if (e.type=='text')
	 		{
	 			if(e.value == '' && document.getElementById('authRequired').checked){
	 				
	 				jAlert('Please provide complete detail for email notification.','Incomplete Detail');
	 				return;
	 			}	
	 				
	 		}
	 		if((e.name == "emailSenderAddress" || e.name == "emailUsername"))
	 		{
	 			var emailValid = Util.validateEmail(e.value);
	 			if(!emailValid)
	 			{
	 				jAlert('E-mail Id is not valid.','Incorrect Detail');
	 				return;
	 			}
	 		}
	 	}
		
	}
	if(smsform.smsEnabled[0].checked){
		
		for (var i=0;i<smsform.elements.length;i++)
	 	{
	 		var e=smsform.elements[i];
	 		if (e.type=='text'||e.type=='password')
	 		{
	 			if(e.value == ''){
	 				
	 				jAlert('Please provide  complete detail for sms notification.','Incomplete Detail');
	 				return;
	 			}	
	 				
	 		}
	 	}
		
	}
	if(logform.logEnabled[0].checked){
		
		for (var i=0;i<logform.elements.length;i++)
	 	{
	 		var e=logform.elements[i];
	 		if (e.type=='text'||e.type=='password')
	 		{
	 			if(e.value == ''){
	 				
	 				jAlert('Please provide  complete detail for log notification.','Incomplete Detail');
	 				return;
	 			}	
	 				
	 		}
	 	}
		
	}
	
	
	var notifBean = {
			emailEnabled : mailform.emailEnabled[0].checked,
			emailSenderName : mailform.emailSenderName.value,
			emailSenderAddress : mailform.emailSenderAddress.value,
			emailSMTPServer : mailform.emailSMTPServer.value,
			emailSMTPPort : mailform.emailSMTPPort.value,
			securedProtocol : mailform.securedProtocol[0].checked,
			authRequired : mailform.authRequired[0].checked,
			emailUsername : mailform.emailUsername.value,
			emailPassword : mailform.emailPassword.value,
			
			smsEnabled : smsform.smsEnabled[0].checked,
			smsNumber : smsform.smsNumber.value,
			smsSerialPort : smsform.smsSerialPort.value,
			smsManufacturer : smsform.smsManufacturer.value,
			smsSelectedModel : smsform.smsSelectedModel.value,
			smsBaudRate : smsform.smsBaudRate.value,
			
			logEnabled : logform.logEnabled[0].checked,
			logFilePath : logform.logFilePath.value
	};
//	var p = { name:"Fred", age:21 };
	
/*	notifBean.emailEnabled = doc.forms[0].emailEnabled[0].checked;
	notifBean.emailSenderName = doc.forms[0].emailSenderName.value;
	notifBean.emailSenderAddress = doc.forms[0].emailSenderAddress.value;
	notifBean.emailSMTPServer = doc.forms[0].emailSMTPServer.value;
	notifBean.emailSMTPPort = doc.forms[0].emailSMTPPort.value;
	notifBean.securedProtocol = doc.forms[0].securedProtocol[0].checked;
	notifBean.authRequired = doc.forms[0].authRequired[0].checked;
	notifBean.emailUsername = doc.forms[0].emailUsername.value;
	notifBean.emailPassword = doc.forms[0].emailPassword.value;
	
	notifBean.smsEnabled = doc.forms[0].smsEnabled[0].checked;
	notifBean.smsNumber = doc.forms[0].smsNumber.value;
	notifBean.smsSerialPort = doc.forms[0].smsSerialPort.value;
	notifBean.smsManufacturer = doc.forms[0].smsManufacturer.value;
	notifBean.smsSelectedModel = doc.forms[0].smsSelectedModel.value;
	notifBean.smsBaudRate = doc.forms[0].smsBaudRate.value;
	
	notifBean.logEnabled = doc.forms[0].logEnabled[0].checked;
	notifBean.logFilePath = doc.forms[0].logFilePath.value;
*/	
	RemoteManager.updateNotificationSettings(notifBean, beanUpdated);
}

function beanUpdated(resp)
{

	if(resp){
		jAlert("Notification Settings updated.","Update");	
	}
	else{
		jAlert("You do not have sufficient privilege to update notification settings.","Updation Failed");
	}
//	document.getElementsByName('saveEmailConfig').disabled = true;
//	document.getElementsByName('emailEnabled').checked = false;
}

function onSmsEnabled(doc, src)
{
	var vDisabled = !src.checked;
	
	var smsform = document.getElementById('smsNotificationForm');
	smsform.smsNumber.disabled = vDisabled;
	smsform.smsSerialPort.disabled = vDisabled;
	smsform.smsManufacturer.disabled = vDisabled;
	smsform.smsModel.disabled = vDisabled;
	smsform.smsBaudRate.disabled = vDisabled;
	//smsform.validateSmsConfig.disabled = vDisabled;
	
}

/*
function checkLengthOfNames(nameElems, nameElemNames, errMsg)
{
	var errNotFound = true;
	for (var i = 0; i < nameElems.length; i++)
	{
		if (nameElems[i].value.length > 128)
		{
			errMsg = errMsg + '\n' + nameElemNames[i];
			errNotFound = false;
		}
	}

	if(errNotFound == false)
	{
		jAlert(errMsg);
	}
	return errNotFound;
}
*/

function onManufacturerChange(doc, selMfg, selModel, setSelected)
{
	var selModelValue = '';
	var form = document.getElementById('emailNotificationForm');
	if (setSelected)
	{
		selModelValue = form.smsSelectedModel.value;
	}
	var i;
	for(i = selModel.options.length - 1; i >= 0; i--)
	{
		selModel.remove(i);
	}
	
	var models = smsModelArray[selMfg.value];
	for(i = 0; i < models.length; i++)
	{
		var optn = doc.createElement("OPTION");
		optn.text = models[i];
		optn.value = models[i];
		if (setSelected && selModelValue == models[i])
		{
			optn.selected = true;
		}
		selModel.options.add(optn);
	}
}


function onLogEnabled(doc, src)
{
	var logform = document.getElementById('logNotificationForm');
	logform.logFilePath.disabled = !src.checked;
	//logform.validateLogConfig.disabled = !src.checked;
	if(src.checked)
	{
		document.getElementById('viewLog').disabled = false;
		document.getElementById('validateLog').disabled = false;
	}
	else
	{
		document.getElementById('viewLog').disabled = true;
		document.getElementById('validateLog').disabled = true;
	}
	
}

function getAllDetails(doc)
{
	latestDoc = doc;
	RemoteManager.getNotificationSettings(fillNotifPage);
}

function fillNotifPage(notifBean)
{
	var mailform = document.getElementById('emailNotificationForm');
	var smsform = document.getElementById('smsNotificationForm');
	var logform = document.getElementById('logNotificationForm');
	
	mailform.emailEnabled[0].checked = notifBean.emailEnabled;
	onEmailEnabled(latestDoc, mailform.emailEnabled[0]);
	
	if(notifBean.emailSenderName != null)
		mailform.emailSenderName.value = notifBean.emailSenderName;
	if(notifBean.emailSenderAddress != null)
		mailform.emailSenderAddress.value = notifBean.emailSenderAddress;
	if(notifBean.emailSMTPServer != null)
		mailform.emailSMTPServer.value = notifBean.emailSMTPServer;
	if(notifBean.emailSMTPPort != null)
		mailform.emailSMTPPort.value = notifBean.emailSMTPPort;
	mailform.securedProtocol[0].checked = notifBean.securedProtocol;
	mailform.authRequired[0].checked = notifBean.authRequired;
	onAuthenticationReq(null, mailform.authRequired[0]);
	if(notifBean.emailUsername != null)
		mailform.emailUsername.value = notifBean.emailUsername;
	if(notifBean.emailPassword != null)
		mailform.emailPassword.value = notifBean.emailPassword;
	
	
	
	smsform.smsEnabled[0].checked = notifBean.smsEnabled;
	onSmsEnabled(latestDoc, smsform.smsEnabled[0]);
	
	smsform.smsNumber.value = notifBean.smsNumber;
	smsform.smsSerialPort.value = notifBean.smsSerialPort;
	smsform.smsManufacturer.value = notifBean.smsManufacturer;
	smsform.smsSelectedModel.value = notifBean.smsSelectedModel;
	smsform.smsBaudRate.value = notifBean.smsBaudRate;
	
	logform.logEnabled[0].checked = notifBean.logEnabled;
	if(notifBean.logFilePath != null)
		logform.logFilePath.value = notifBean.logFilePath;
	onLogEnabled(latestDoc, logform.logEnabled[0]);
	
}

function handleBack()
{
	if(Navbar.selectedChildTab == 'notifications_rules_for_alerts')
		Navbar.changeTab('Configure Alerts','dashboard', 'set_alerts');
	else if(Navbar.selectedChildTab == 'reportnotifications')
		Navbar.changeTab('All Reports','admin', 'all_reports');
}

function validateEmailSettings(){
	
	var mailform = document.getElementById('emailNotificationForm');
	var smsform = document.getElementById('smsNotificationForm');
	var logform = document.getElementById('logNotificationForm');
	
	if(mailform.emailEnabled[0].checked){

		for (var i=0;i<mailform.elements.length;i++)
	 	{
	 		var e=mailform.elements[i];
	 		if (e.type=='text')
	 		{
	 			if(e.value == '' && document.getElementById('authRequired').checked){
	 				jAlert('Please provide complete detail for email notification.','Incomplete Detail');
	 				return;
	 			}	
	 		}
	 		if((e.name == "emailSenderAddress" || e.name == "emailUsername"))
	 		{
	 			var emailValid = Util.validateEmail(e.value);
	 			if(!emailValid)
	 			{
	 				jAlert('E-mail Id is not valid.','Incorrect Detail');
	 				return;
	 			}
	 		}
	 	}
		
	}
	
	var notifBean = {
			emailEnabled : mailform.emailEnabled[0].checked,
			emailSenderName : mailform.emailSenderName.value,
			emailSenderAddress : mailform.emailSenderAddress.value,
			emailSMTPServer : mailform.emailSMTPServer.value,
			emailSMTPPort : mailform.emailSMTPPort.value,
			securedProtocol : mailform.securedProtocol[0].checked,
			authRequired : mailform.authRequired[0].checked,
			emailUsername : mailform.emailUsername.value,
			emailPassword : mailform.emailPassword.value,
			
			smsEnabled : smsform.smsEnabled[0].checked,
			smsNumber : smsform.smsNumber.value,
			smsSerialPort : smsform.smsSerialPort.value,
			smsManufacturer : smsform.smsManufacturer.value,
			smsSelectedModel : smsform.smsSelectedModel.value,
			smsBaudRate : smsform.smsBaudRate.value,
			
			logEnabled : logform.logEnabled[0].checked,
			logFilePath : logform.logFilePath.value
	};

	notificationBean = notifBean;
	Util.addLightbox('validate', 'pages/popup.jsp');
}
function handleNotificationResponse(isValidationSuccess)
{
	var id = "validate";
	var imgId = "";
	var status = "";
	var message = "";
	
	if (isValidationSuccess) 
	{
		status = "Success";
		imgId = "popup.image.success";
		message = "Notification settings validated successfully. QueryIO has sent a validation email to your email id.";
		
	} 
	else 
	{
		status = "Failure";
		imgId = "popup.image.fail";
		message = "QueryIO could not send validation email to your email id. Please check your notification settings.";
	}

	
	dwr.util.byId('popup.image.processing' + id).style.display = 'none';
	dwr.util.byId(imgId + id).style.display = '';

	dwr.util.setValue('popup.message' + id, message);
	dwr.util.setValue('popup.status' + id, status);
	dwr.util.byId('ok.popup').disabled = false;
}
function closeBox()
{
	Util.removeLightbox('validate');
}
function handleNotificationLogResponse(isValidationSuccess){
	if(isValidationSuccess){
		jAlert("Log Notification settings validated successfully.","Validation Successfull");
	}else{
		jAlert("QueryIO could not validate Log setting on given file path .Please check your log notification settings.","Validation Failed");
	}
}


function validateLogSettings(){
	var mailform = document.getElementById('emailNotificationForm');
	var smsform = document.getElementById('smsNotificationForm');
	var logform = document.getElementById('logNotificationForm');
	
	if(mailform.emailEnabled[0].checked){

		for (var i=0;i<mailform.elements.length;i++)
	 	{
	 		var e=mailform.elements[i];
	 		if (e.type=='text')
	 		{
	 			if(e.value == '' && document.getElementById('authRequired').checked){
	 				jAlert('Please provide complete detail for email notification.','Incomplete Detail');
	 				return;
	 			}	
	 		}
	 		if((e.name == "emailSenderAddress" || e.name == "emailUsername"))
	 		{
	 			var emailValid = Util.validateEmail(e.value);
	 			if(!emailValid)
	 			{
	 				jAlert('E-mail Id is not valid.','Incorrect Detail');
	 				return;
	 			}
	 		}
	 	}
		
	}
	
	var notifBean = {
			emailEnabled : mailform.emailEnabled[0].checked,
			emailSenderName : mailform.emailSenderName.value,
			emailSenderAddress : mailform.emailSenderAddress.value,
			emailSMTPServer : mailform.emailSMTPServer.value,
			emailSMTPPort : mailform.emailSMTPPort.value,
			securedProtocol : mailform.securedProtocol[0].checked,
			authRequired : mailform.authRequired[0].checked,
			emailUsername : mailform.emailUsername.value,
			emailPassword : mailform.emailPassword.value,
			
			smsEnabled : smsform.smsEnabled[0].checked,
			smsNumber : smsform.smsNumber.value,
			smsSerialPort : smsform.smsSerialPort.value,
			smsManufacturer : smsform.smsManufacturer.value,
			smsSelectedModel : smsform.smsSelectedModel.value,
			smsBaudRate : smsform.smsBaudRate.value,
			
			logEnabled : logform.logEnabled[0].checked,
			logFilePath : logform.logFilePath.value
	};

	RemoteManager.validateNotification(notifBean,"Log",handleNotificationLogResponse);
}