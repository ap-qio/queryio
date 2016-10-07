ScheduledJob = {

	selectedSchedules: [],
	scheduleCache: [],
	editCache: [],
	userCache: [],
	currentPage: 1,
	currentOperation: null,
	
	ready: function () {
		
		ScheduledJob.closeButton();
		RemoteManager.getAllMapredSchedules(ScheduledJob.populateScheduleList);
   	},
   		    
   		    
   	closeButton: function(){
   		if(document.getElementById('editSchedule')==undefined||document.getElementById('editSchedule')==null)return;
   		document.getElementById('editSchedule').disabled=true;
   		document.getElementById('deleteSchedule').disabled=true;
   	},
   	
   	checkEnable: function(){
   		ScheduledJob.selectAllHandler();
   		ScheduledJob.findSelectedSchedules();
   		
   		if(ScheduledJob.selectedSchedules.length==1){
   			document.getElementById('editSchedule').disabled=false;
   			document.getElementById('deleteSchedule').disabled=false;
   		}
   		else if(ScheduledJob.selectedSchedules.length>1 ){
   			document.getElementById('editSchedule').disabled=true;
   			document.getElementById('deleteSchedule').disabled=false;
   		}
   		else
	   		ScheduledJob.closeButton();
   	},
   		    
	populateScheduleList: function(list){
		if(document.getElementById('schedules_table')==undefined||document.getElementById('schedules_table')==null)return;
	var tabledata =[];
	if(list!=null){
		for(var i=0;i<list.length;i++){
			var schedule = list[i];
			ScheduledJob.scheduleCache.push(schedule);
			tabledata.push(['<input type="checkbox" onClick="javascript:ScheduledJob.checkEnable()" id="test'+i+'">','<input type="hidden" value="'+schedule.name+','+schedule.group+'">'+
			schedule.name, schedule.reportId!=null?schedule.reportId.substring(1,schedule.reportId.length-1):"",schedule.selectedFormat!=null?schedule.selectedFormat.substring(1,schedule.selectedFormat.length-1):"",
			schedule.notificationType,schedule.emailUserIds!=null?schedule.emailUserIds.substring(1,schedule.emailUserIds.length-1):"",ScheduledJob.findFrequency(schedule.interval)]);
		}
	}
	$('#schedules_table').dataTable( {
        "bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": true,
		"bDestroy": true,
		"bInfo": false,
		"bAutoWidth": true,
		"aaSorting": [[ 1, "desc" ]],
		"aaData": tabledata,
        "aoColumnDefs": [{ "bSortable": false, "aTargets": [ 0 ] } ],
        "aoColumns": [
             { "sTitle": "<input type = 'checkbox' id = 'selectAllSchedules' onclick='javascript:ScheduledJob.selectAllSchedules()'>"},
            { "sTitle": "Schedule ID" },
            { "sTitle": "Job Name" },
            { "sTitle": "Format Type" },
            { "sTitle": "Notification Type" },
            { "sTitle": "Email ID" },
            { "sTitle": "Frequency" },
        ]
    	} ); 
	
   	//disabled the checkAll button when no data in the list
   	if(list == null || list == undefined || list.length == 0)
		$('#selectAllSchedules').attr('disabled',true);
	else
		$('#selectAllSchedules').removeAttr('disabled');
	},
	findSelectedSchedules: function(){
		ScheduledJob.selectedSchedules = [];
		for(var i=0;i<ScheduledJob.scheduleCache.length;i++){
			if(document.getElementById('test'+i).checked ){
				var sched = ScheduledJob.scheduleCache[i];
				ScheduledJob.selectedSchedules.push(sched.name+','+sched.group);
			}
		}
	},
	selectAllSchedules : function(){
		if(document.getElementById('selectAllSchedules').checked)
		{
			var oTable = $('#schedules_table').dataTable();
			var numberRows = oTable.fnSettings().fnRecordsTotal();
			if(numberRows == 1)
				document.getElementById('editSchedule').disabled = false;
			else 
				document.getElementById('editSchedule').disabled=true;
			$("#schedules_table input[type=checkbox]").attr('checked', 'checked');
			document.getElementById('deleteSchedule').disabled=false;
		}
		else
		{
			$("#schedules_table input[type=checkbox]").removeAttr('checked');
			document.getElementById('editSchedule').disabled=true;
			document.getElementById('deleteSchedule').disabled=true;
		}
	},
	
	selectAllHandler : function(){
		for(var i=0;i<ScheduledJob.scheduleCache.length;i++){
			if(!document.getElementById('test'+i).checked){
				$("#selectAllSchedules").removeAttr('checked');
				break;
			}
		}
		if(i == ScheduledJob.scheduleCache.length)
		{
			$("#selectAllSchedules").attr('checked', 'checked');
		}
	},
	
	deleteSchedule: function(){
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm('Are you sure you want to delete selected item(s)?','Delete Schedule(s)',function(val)
		{
			if (val == true)
			{
				ScheduledJob.findSelectedSchedules();
				for(var i = 0;i<ScheduledJob.selectedSchedules.length;i++){
					var report = ScheduledJob.selectedSchedules[i]
					var reportType = report.substring(report.indexOf('-[')+2,report.length-1);
					if(reportType=='11'){
						jAlert("You cannot delete system generated report.Please deselect system generated report.","Invalid Operation");
						return;
					}
				}
				RemoteManager.deleteJob(ScheduledJob.selectedSchedules,ScheduledJob.deleteCallback);
			}
			else
				return;
		});
	},
	
	deleteCallback: function(flag){
		if(!flag){
			jAlert('Some Error Occurred while deleting the job','Failed');
		}
		else{
			jAlert('Successfully deleted Jobs','Success');
		}
		Navbar.refreshView();
	},
	
	
	reBuiltTable: function(){
		var oTable=$('#schedules_table').dataTable();
		oTable.remove();
		$('#schedules_table_div').append('<table id="schedules_table"></table>');
		RemoteManager.getAllNonMapredSchedules(ScheduledJob.populateScheduleList);
	},
	
	findScheduleInCache: function(name,group){
		for(var i=0;i<ScheduledJob.scheduleCache.length;i++){
				var sched = ScheduledJob.scheduleCache[i];
				if(sched.name==name&&sched.group==group){
					return sched;
				}
			}
		return null;	
	},
	
	editSchedule: function(){
		ScheduledJob.currentOperation = "edit";
		Util.addLightbox("generateReport", "resources/editScheduledReport.html", null, null);
	},
	
	closeBox: function(){
		ScheduledJob.currentOperation = null;
		Util.removeLightbox("generateReport");
	},
	
	
	moveAllOptions: function (from,to){
		var source = document.getElementById(from).getElementsByTagName("option");	
		for (var i=0; i< source.length; i++)
		{
			$('#'+to).append('<option value="'+source[i].value+'">'+ScheduledJob.findUser(source[i].value)+'</option>');
		}
		$('#'+from).children().remove();
	},
	
	
	editReady: function(){
	
		ScheduledJob.findSelectedSchedules();
		var scheduleName = ScheduledJob.selectedSchedules[i].substring(0,ScheduledJob.selectedSchedules[i].indexOf(","));
		var scheduleGroup = ScheduledJob.selectedSchedules[i].substring(ScheduledJob.selectedSchedules[i].indexOf(",")+1);
		var schedule = ScheduledJob.findScheduleInCache(scheduleName,scheduleGroup);
		document.getElementById('headerspan').innerHTML=schedule.reportId.substring(1,schedule.reportId.length-1);
		document.forms[0].interval.options[schedule.interval].selected = true;
		var dt = new Date(schedule.time);
		document.forms[0].reportDate.value = (dt.getMonth()+1)+"/"+dt.getDate()+"/"+(1900+dt.getYear())+" "+dt.getHours()+":"+dt.getMinutes()+":"+dt.getSeconds();
		document.forms[0].html.checked=schedule.selectedFormat.indexOf('HTML')>0;
		document.forms[0].pdf.checked=schedule.selectedFormat.indexOf('PDF')>0;
		document.forms[0].xls.checked=schedule.selectedFormat.indexOf('XLS')>0;
		document.forms[0].alertRaisedNotificationMessage.value = schedule.notificationMessage;
		document.forms[0].notificationType.options[schedule.notificationType=='Email'?0:1].selected = true;
		ScheduledJob.nextScheduleStep(1);
		ScheduledJob.editCache = [];
		ScheduledJob.editCache.push(schedule);
		if(schedule.reportId.indexOf("NameNode Detail")>0 || schedule.reportId.indexOf("DataNode Detail")>0 || schedule.reportId.indexOf("ResourceManager Detail")>0 || schedule.reportId.indexOf("NodeManager Detail")>0)
			RemoteManager.getAllNodeIdWithIp(ScheduledJob.fillScroll);
		else
			document.getElementById('hostScroll').style.display = 'none';
		RemoteManager.getUserDetails(ScheduledJob.addUser);
	},
	
	
	nextScheduleStep: function(selectedDiv){
		var flag = true;
		if(ScheduledJob.currentPage<selectedDiv){
			flag =	ScheduledJob.validate();
		}

		if(flag){	
			switch(selectedDiv){
				case 1:{
					$('#reportdiv1').show();
					$('#reportdiv2').hide();
					$('#reportdiv3').hide();
					break;
				}
				case 2:{
					$('#reportdiv1').hide();
					$('#reportdiv2').show();
					$('#reportdiv3').hide();
					break;
				}
				case 3:{
					if(ScheduledJob.currentOperation=="add"){
						RemoteManager.getUserDetails(ScheduledJob.addUser);
					}
					$('#reportdiv1').hide();
					$('#reportdiv2').hide();
					$('#reportdiv3').show();
					$('input[value="Close"]').hide();
					break;
				}
			}
			ScheduledJob.currentPage = selectedDiv;
		}
		
	},
	
	
	fillScroll: function(list){
		$('#hostName1 option').remove();
		if(ScheduledJob.currentOperation=="add"){
			for(var i=0;i<list.length;i++){
				var str = list[i].substring(list[i].lastIndexOf('#')+1);
				var flag = true;		
				if(str=='datanode')
					flag = true;
				else if(str=='namenode')
					flag = true;
				else if(str=='resourcemanager')
					flag = true;
				else if(str=='nodemanager')
					flag = true;
				else
					flag = false;
				if(flag){
					$('#hostName1').append('<option value="'+list[i].substring(0,list[i].indexOf('#'))+'" >'
					+list[i].substring(list[i].indexOf('#')+1,list[i].lastIndexOf('#'))+':'+list[i].substring(list[i].lastIndexOf('#')+1)+'</option>');
				}
				document.getElementById('hostName1').selectedIndex = 0;
			}
		}
		else{
			var schedule = ScheduledJob.editCache[0];
			for(var i=0;i<list.length;i++){
				var str = list[i].substring(list[i].lastIndexOf('#')+1);
				var flag = true;		
				if(str=='datanode'&&schedule.reportId.indexOf("DataNode Detail")>0)
					flag = true;
				else if(schedule.reportId.indexOf("NameNode Detail")>0&&str=='namenode')
					flag = true;
				else if(schedule.reportId.indexOf("ResourceManager Detail")>0&&str=='resourcemanager')
					flag = true;
				else if(schedule.reportId.indexOf("NodeManager Detail")>0&&str=='nodemanager')
					flag = true;
				else
					flag = false;
				var id  = -1;
				var itr = 0;
				if(flag){
					if(schedule.nodeId==parseInt(list[i].substring(0,list[i].indexOf('#'))))
						id = itr;
					$('#hostName1').append('<option value="'+list[i].substring(0,list[i].indexOf('#'))+'" >'
					+list[i].substring(list[i].indexOf('#')+1,list[i].lastIndexOf('#'))+':'+list[i].substring(list[i].lastIndexOf('#')+1)+'</option>');
					itr++;
				}
				document.getElementById('hostName1').selectedIndex = id;
			}
			if(list.length==0)
				jAlert('No Host Available',ScheduledJob.closeBox);
		}
			
	},
	
	moveSelectedOptions: function(from,to){

		var source = document.getElementById(from).getElementsByTagName("option");
		for (var i=0; i< source.length; i++)
		{
			if (source[i].selected)
			{	
				$('#'+to).append('<option value="'+source[i].value+'">'+ScheduledJob.findUser(source[i].value)+'</option>');
			}
		}
	 	$("#"+from+" option:selected").remove();
	},

	findUser: function(val){
		for(var i=0;i<ScheduledJob.userCache.length;i++){
			var user=ScheduledJob.userCache[i];
			if(user.id==val)
				return user.firstName+' '+user.lastName;
		}
	},
	
	addUser: function(list){
		if(list!=null){
			ScheduledJob.userCache=[];
				for(var i=0;i<list.length;i++){
					var user=list[i];
					ScheduledJob.userCache.push(user);
					if(i==0)
						$('#user').append('<option value="'+user.id+'" selected>'+user.firstName+' '+user.lastName+'</option>');
					else
						$('#user').append('<option value="'+user.id+'">'+user.firstName+' '+user.lastName+'</option>');
				}
			ScheduledJob.moveSelectedOptions('user','selected');
		}
	},

	updateSchedule: function(){
		var schedule = ScheduledJob.editCache[0];
		document.getElementById('msg_td_3').innerHTML="";
		var selectedUser=document.getElementById('selected').getElementsByTagName("option");
		var users=[];
		for(var i=0;i<selectedUser.length;i++){
			users.push(selectedUser[i].value);
		}
		if(users.length>0){
			var reportsType=ScheduledJob.findreportType(schedule);
			RemoteManager.updateJob(document.forms[0].interval.value,document.forms[0].reportDate.value,ScheduledJob.findExportType(),
				document.getElementById('notificationType').value, document.getElementById('alertRaisedNotificationMessage').value,users,
				reportsType,schedule.name,schedule.group,schedule.nodeId,ScheduledJob.scheduleReturn);
		}
		else{
			document.getElementById('msg_td_3').innerHTML+="* Users Not Selected<br>";
		}
	},
	
	scheduleReturn: function(flag){
		if(flag){
			jAlert('Successfully Completed Operation',ScheduledJob.closeBox(),ScheduledJob.reBuiltTable());
		}
		else{
			jAlert("Some Error Occurred during updating job.");
		}
		$("#popup_container").css("z-index","99999999");
	},



	findExportType: function(){
		var exportFormatList=[];
		if(document.forms[0].html.checked)
				exportFormatList.push(0);
		if(document.forms[0].pdf.checked)
				exportFormatList.push(1);
		if(document.forms[0].xls.checked)
				exportFormatList.push(3);
		return exportFormatList;
	},

	
	
	validate: function(){
		var flag = true;
		
		if(ScheduledJob.currentOperation == "add"){
			switch(ScheduledJob.currentPage){
				case 1:{
					document.getElementById('msg_td_1').innerHTML="";
					if(document.getElementById('schedID').value==""||document.getElementById('schedID').value==null){
						jAlert("Schedule Id not set.","Incomplete Detail");
						$("#popup_container").css("z-index","9999999");
						return false;	
					}
						
					var reportArr = $('#reportType').val();
					if(reportArr==null){
						jAlert("Report Type Not Selected","Incomplete Detail");
						$("#popup_container").css("z-index","9999999");
						return false;	
					}
					var reportDate=document.forms[0].reportDate.value;
					if(reportDate==""){
						jAlert("Date & Time Not Provided","Incomplete Detail");
						$("#popup_container").css("z-index","9999999");
						return false;
					}	
					var exportFormatList = ScheduledJob.findExportType();
					if(exportFormatList.length==0){
						jAlert("Format Not Selected","Incomplete Detail");
						$("#popup_container").css("z-index","9999999");
						return false;
					}
					break;
				}
				case 2:{
					document.getElementById('msg_td_2').innerHTML="";
					if(document.getElementById('alertRaisedNotificationMessage').value==""){
						jAlert("No Message with alert set.","Incomplete Detail");
						$("#popup_container").css("z-index","9999999")
						return false;
					}
					break;
				}
			}
		}
		else{
			switch(ScheduledJob.currentPage)
			{
				case 1:{
					document.getElementById('msg_td_1').innerHTML="";
					var reportDate=document.forms[0].reportDate.value;
					if(reportDate==""){
						document.getElementById('msg_td_1').innerHTML+="* Date & Time Not Provided<br>";				
						flag = false;
					}	
					var exportFormatList = ScheduledJob.findExportType();
					if(exportFormatList.length==0){
						document.getElementById('msg_td_1').innerHTML+="* Format Not Selected<br>";
						flag = false;
					}
					break;
				}
				case 2:{
					document.getElementById('msg_td_2').innerHTML="";
					if(document.getElementById('alertRaisedNotificationMessage').value==""){
						document.getElementById('msg_td_2').innerHTML+="* Message Cannot be Empty";
						flag = false;
					}
					break;
				}
			}
		}
		return flag;
	},	

	findreportType: function(schedule){
		var reportType= [];
		if(ScheduledJob.currentOperation == "add")
		{
			var reportArr = $('#reportType').val();
			if(reportArr.indexOf("hdfs")>0)
				reportType.push(0);
			if(reportArr.indexOf("mapReduce")>0)
				reportType.push(14);
			if(reportArr.indexOf("namenode")>-1)
				reportType.push(1);
			if(reportArr.indexOf("datanode")>-1)
				reportType.push(2);
			if(reportArr.indexOf("sforecast")>-1)
				reportType.push(5);
			if(reportArr.indexOf("alert")>-1)
				reportType.push(6);
			if(reportArr.indexOf("nndetail")>-1)
				reportType.push(8);
			if(reportArr.indexOf('dndetail')>-1)
				reportType.push(9);
			if(reportArr.indexOf('resourcemanager')>-1)
				reportType.push(10);
			if(reportArr.indexOf('nodemanager')>-1)
				reportType.push(11);
			if(reportArr.indexOf("rmDetail")>-1)
				reportType.push(12);
			if(reportArr.indexOf('nmDetail')>-1)
				reportType.push(13);
		
		}
		else{
			if(schedule.reportId.indexOf("HDFS Summary")>0)
				reportType.push(0);
			if(schedule.reportId.indexOf("MapReduce Summary")>0)
				reportType.push(14);
			if(schedule.reportId.indexOf("NameNode Summary")>0)
				reportType.push(1);
			if(schedule.reportId.indexOf("DataNode Summary")>0)
				reportType.push(2);
			if(schedule.reportId.indexOf("NameNode Detail")>0)
				reportType.push(8);
			if(schedule.reportId.indexOf("DataNode Detail")>0)
				reportType.push(9);
			if(schedule.reportId.indexOf("ResourceManager Summary")>0)
				reportType.push(10);
			if(schedule.reportId.indexOf("NodeManager Summary")>0)
				reportType.push(11);
			if(schedule.reportId.indexOf("ResourceManager Detail")>0)
				reportType.push(12);
			if(schedule.reportId.indexOf("NodeManager Detail")>0)
				reportType.push(13);
			
			if(schedule.reportId.indexOf("System Generated Billing Invoice")>0)
				reportType.push(101);
			else if(schedule.reportId.indexOf("Billing")>0)
				reportType.push(100);
		}
		return reportType;
		
	},
	

	findFrequency: function(freq){
		var strFreq = "";
		switch(parseInt(freq)){
			case 0:	strFreq = "Once"; break;
			case 1: strFreq = "Twelve Hours"; break;
			case 2: strFreq = "Daily"; break;
			case 3: strFreq = "Weekly"; break;
		}
		return strFreq;
	},
	
	
	reportTypeView: function(viewNo)
	{
		var selBox = document.getElementById('reportType');
		selBox.innerHTML ="";
		switch(viewNo){
		case 1:
			document.getElementById('hostScroll').style.display = 'none';
			ScheduledJob.addOption(selBox,'hdfs','HDFS Summary');
			ScheduledJob.addOption(selBox,'mapReduce','MapReduce Summary');
			ScheduledJob.addOption(selBox,'namenode','NameNode Summary');
			ScheduledJob.addOption(selBox,'datanode','DataNode Summary');
			ScheduledJob.addOption(selBox,'resourcemanager','ResourceManager Summary');
			ScheduledJob.addOption(selBox,'nodemanager','NodeManager Summary');
			ScheduledJob.addOption(selBox,'sforecast','Storage Forecast');
			ScheduledJob.addOption(selBox,'alert','Alert Summary');
			break;
		case 2:
			document.getElementById('hostScroll').style.display = '';
			ScheduledJob.addOption(selBox,'nndetail','NameNode Detail');
			ScheduledJob.addOption(selBox,'dndetail','DataNode Detail');
			ScheduledJob.addOption(selBox,'rmDetail','ResourceManager Detail');
			ScheduledJob.addOption(selBox,'nmDetail','NodeManager Detail');
			break;
		}
	},
	
	
	schedule: function(){
		ScheduledJob.currentOperation = "add";
		Util.addLightbox("generateReport", "resources/scheduleReports.html", null, null);
	},
	
	
	addOption: function(selectbox, value, text)
	{
		var optn = document.createElement("OPTION");
		optn.text = text;
		optn.value = value;
		selectbox.options.add(optn);
	},
	
	addSchedule: function(){
		var selectedUser=document.getElementById('selected').getElementsByTagName("option");
		var users=[];
		for(var i=0;i<selectedUser.length;i++){
			users.push(selectedUser[i].value);
		}
		if(users.length>0){

			var reportScheduleForm = document.getElementById('scheduleReport');
			var reportsType=ScheduledJob.findreportType();
			var nodeID = -1;
			if(reportsType.indexOf(8)>-1||reportsType.indexOf(9)>-1 || reportsType.indexOf(12)>-1 || reportsType.indexOf(13)>-1)
				nodeID = parseInt(document.getElementById('hostName1').value);
			
			RemoteManager.scheduleJob(reportScheduleForm.interval.value,reportScheduleForm.reportDate.value,ScheduledJob.findExportType(reportScheduleForm),
			document.getElementById('notificationType').value, document.getElementById('alertRaisedNotificationMessage').value,
			users, reportsType, nodeID, document.getElementById('schedID').value,ScheduledJob.scheduleReturn);
		
		}
		else{
			jAlert("Users Not Selected.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999999");
			return false;
		}	
	},
	
	fillhostSelection: function()
	{
		var hostSelection = ScheduledJob.findreportType();
		if((hostSelection.indexOf(8)>-1) && (hostSelection.indexOf(9)>-1) && (hostSelection.indexOf(12)>-1) && (hostSelection.indexOf(13)>-1)){
			RemoteManager.getAllNodeIdWithIp(ScheduledJob.fillScroll);
		}
		else if(hostSelection.indexOf(8)>-1){
			RemoteManager.getAllNameNodeIdWithIp(ScheduledJob.fillScroll);
		}
		else if(hostSelection.indexOf(9)>-1){
			RemoteManager.getAllDataNodeIdWithIp(ScheduledJob.fillScroll);
		}
		else if(hostSelection.indexOf(12)>-1){
			RemoteManager.getAllResourceManagerIdWithIp(ScheduledJob.fillScroll);
		}
		else if(hostSelection.indexOf(13)>-1){
			RemoteManager.getAllNodeManagerIdWithIp(ScheduledJob.fillScroll);
		}
	},
	
};
	