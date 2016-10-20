SnapshotSchedules = {

	selectedSchedules: [],
	scheduleCache: [],
	addCache: [],
	userCache: [],
	currentPage: 1,
	
	ready: function () {
		
		SnapshotSchedules.closeButton();
		RemoteManager.getSnapshots(SnapshotSchedules.populateSnapshotList);
	},
   		    
   		    
   	closeButton: function(){
   		document.getElementById('addSchedule').disabled=false;
   		document.getElementById('deleteSchedule').disabled=true;
   	},
   	
   	checkEnable: function(){
   		SnapshotSchedules.findSelectedSchedules();
   		if(SnapshotSchedules.selectedSchedules.length==1){
   			document.getElementById('addSchedule').disabled=false;
   			document.getElementById('deleteSchedule').disabled=false;
   		}
   		else if(SnapshotSchedules.selectedSchedules.length>1){
   			document.getElementById('addSchedule').disabled=false;
   			document.getElementById('deleteSchedule').disabled=false;
   		}
   		else
   			SnapshotSchedules.closeButton();
   	},
   		    
	populateSnapshotList: function(list){
		SnapshotSchedules.scheduleCache = [];
	var tabledata =[];
	if(list!=null){
		for(var i=0;i<list.length;i++){
			var snapshot = list[i];
			SnapshotSchedules.scheduleCache.push(snapshot.id);
			tabledata.push(['<input type="checkbox" onClick="javascript:SnapshotSchedules.checkEnable()" id="test'+i+'">', snapshot.id, snapshot.hostname, snapshot.location, snapshot.status, snapshot.time]);
//			tabledata.push(['<input type="checkbox" onClick="javascript:SnapshotSchedules.checkEnable()" id="test'+i+'">','<input type="hidden" value="'+schedule.name+','+schedule.group+'">'+
//			schedule.name+','+schedule.group,schedule.scheduleId.substring(1,schedule.scheduleId.length-1),schedule.selectedFormat.substring(1,schedule.selectedFormat.length-1),
//			schedule.notificationType,schedule.emailUserIds.substring(1,schedule.emailUserIds.length-1),SnapshotSchedules.findFrequency(schedule.interval)]);
		}
	}
	$('#schedules_table').dataTable( {
        "bPaginate": false,
		"bLengthChange": false,
		"bFilter": false,
		"bSort": true,
		"bInfo": false,
		"bDestroy": true,
		"bAutoWidth": true,
		"aaSorting": [[ 1, "desc" ]],
		"aaData": tabledata,
        "aoColumnDefs": [{ "bSortable": false, "aTargets": [ 0 ] } ],
        "aoColumns": [
             { "sTitle": ""},
            { "sTitle": "Title" },
            { "sTitle": "Namenode" },
            { "sTitle": "Location" },
            { "sTitle": "Status" },
            { "sTitle": "Time" },
        ]
    	} ); 
	},
	findSelectedSchedules: function(){
		SnapshotSchedules.selectedSchedules = [];
		for(var i=0;i<SnapshotSchedules.scheduleCache.length;i++)
		{
			if(document.getElementById('test'+i).checked)
			{
				var sched = SnapshotSchedules.scheduleCache[i];
				SnapshotSchedules.selectedSchedules.push(sched);
			}
		}
	},

	populateNodeList: function(list)
	{
		if(list!=null)
		{
			document.getElementById('node_list').innerHTML="";
			for(var i=0;i<list.length;i++)
			{
				var host = list[i];
				document.getElementById('node_list').innerHTML+='<option value="'+host.hostIP+'">'+host.hostIP+'</option>';
			}
		}		
	},
	deleteSchedule: function(){
		SnapshotSchedules.findSelectedSchedules();
		RemoteManager.deleteSnapshots(SnapshotSchedules.selectedSchedules,SnapshotSchedules.deleteCallback);
	},
	
	deleteCallback: function(flag)
	{
		if(!flag){
			jAlert('Some Error Occurred while deleting the snapshots');
		}
		else{
			jAlert('Successfully deleted Snapshots',SnapshotSchedules.reBuiltTable());
		}

	},
	
	
	reBuiltTable: function(){
		var oTable=$('#schedules_table').dataTable();
		oTable.remove();
		$('#schedules_table_div').append('<table id="schedules_table"></table>');
		RemoteManager.getSnapshots(SnapshotSchedules.populateSnapshotList);
	},
	
	findScheduleInCache: function(name,group){
		for(var i=0;i<SnapshotSchedules.scheduleCache.length;i++){
				var sched = SnapshotSchedules.scheduleCache[i];
				if(sched.name==name&&sched.group==group){
					return sched;
				}
			}
		return null;	
	},
	
	addSchedule: function(){
		Util.addLightbox("generateschedule", "resources/addScheduledSnapshot.html", null, null);
	},
	
	closeBox: function(){
		Util.removeLightbox("generateschedule");
	},
	
	
	moveAllOptions: function (from,to){
		var source = document.getElementById(from).getElementsByTagName("option");	
		for (var i=0; i< source.length; i++)
		{
			$('#'+to).append('<option value="'+source[i].value+'">'+SnapshotSchedules.findUser(source[i].value)+'</option>');
		}
		$('#'+from).children().remove();
	},
	
	
	addReady: function(){
	
		RemoteManager.getNameNodeHosts(SnapshotSchedules.populateNodeList);
	},
	
	
	nextScheduleStep: function(selectedDiv){
		var flag = true;
		if(SnapshotSchedules.currentPage<selectedDiv){
			flag =	SnapshotSchedules.validate();
		}

		if(flag){	
			switch(selectedDiv){
				case 1:{
					$('#schedulediv1').show();
					$('#schedulediv2').hide();
					$('#schedulediv3').hide();
					break;
				}
				case 2:{
					$('#schedulediv1').hide();
					$('#schedulediv2').show();
					$('#schedulediv3').hide();
					break;
				}
				case 3:{
					$('#schedulediv1').hide();
					$('#schedulediv2').hide();
					$('#schedulediv3').show();
					$('input[value="Close"]').hide();
					break;
				}
			}
			SnapshotSchedules.currentPage = selectedDiv;
		}
	},
	
	moveSelectedOptions: function(from,to){

		var source = document.getElementById(from).getElementsByTagName("option");
		for (var i=0; i< source.length; i++)
		{
			if (source[i].selected)
			{	
				$('#'+to).append('<option value="'+source[i].value+'">'+SnapshotSchedules.findUser(source[i].value)+'</option>');
			}
		}
	 	$("#"+from+" option:selected").remove();
	},

	findUser: function(val){
		for(var i=0;i<this.userCache.length;i++){
			var user=this.userCache[i];
			if(user.id==val)
				return user.firstName+' '+user.lastName;
		}
	},
	
	addUser: function(list){
		var schedule = SnapshotSchedules.addCache[0];
		if(list!=null){
			SnapshotSchedules.userCache=[];
				for(var i=0;i<list.length;i++){
					user=list[i];
					SnapshotSchedules.userCache.push(user);
					if(schedule.emailUserIds.indexOf(user.firstName+' '+user.lastName)>0)
						$('#user').append('<option value="'+user.id+'" selected>'+user.firstName+' '+user.lastName+'</option>');
					else
						$('#user').append('<option value="'+user.id+'">'+user.firstName+' '+user.lastName+'</option>');
				}
			SnapshotSchedules.moveSelectedOptions('user','selected');
		}
	},

	updateSchedule: function(){
		var schedule = SnapshotSchedules.addCache[0];
		document.getElementById('msg_td_3').innerHTML="";
		var selectedUser=document.getElementById('selected').getElementsByTagName("option");
		var users=[];
		for(var i=0;i<selectedUser.length;i++){
			users.push(selectedUser[i].value);
		}
		if(users.length>0){
			RemoteManager.updateJob(document.forms[0].interval.value,document.forms[0].scheduleDate.value,SnapshotSchedules.findFormatList(),
			document.getElementById('notificationType').value, document.getElementById('alertRaisedNotificationMessage').value,users,
			SnapshotSchedules.findscheduleType(schedule),schedule.name,schedule.group,SnapshotSchedules.scheduleReturn);
		}
		else{
			document.getElementById('msg_td_3').innerHTML+="* Users Not Selected<br>";
		}
	},
	
	scheduleReturn: function(flag){
		if(flag){
			jAlert('Successfully Updated Job',SnapshotSchedules.closeBox(),SnapshotSchedules.reBuiltTable());
		}
		else{
			jAlert("Some Error Occurred");
		}		
	},



	findFormatList: function(){
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
		switch(SnapshotSchedules.currentPage){
			case 1:{
				document.getElementById('msg_td_1').innerHTML="";
				var scheduleDate=document.forms[0].scheduleDate.value;
				if(scheduleDate==""){
					document.getElementById('msg_td_1').innerHTML+="* Date & Time Not Provided<br>";				
					flag = false;
				}	
				var exportFormatList = SnapshotSchedules.findFormatList();
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
		return flag;
	},	


	findscheduleType: function(schedule){
		var scheduleType=[];
			if(schedule.scheduleId.indexOf("HDFS Summary")>0)
				scheduleType.push(0);
			if(schedule.scheduleId.indexOf("NameNode Summary")>0)
				scheduleType.push(1);
			if(schedule.scheduleId.indexOf("DataNode Summary")>0)
				scheduleType.push(2);
		return scheduleType;
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
	save: function(){
		var id = document.getElementById("snapshotID").value;
		var namenode = document.getElementById("node_list").value;
		var location = document.getElementById("location").value;

		RemoteManager.addSnapshot(id, namenode, location, SnapshotSchedules.showStatus)
	},
	
	showStatus: function(status)
	{
		if(!status)
		{
			var id = document.getElementById("snapshotID").value;
			RemoteManager.doesSnapshotExist(id, SnapshotSchedules.checkConflict)
		}
		else
		{
			jAlert('Snapshot Created Successfuly',SnapshotSchedules.reBuiltTable());
			SnapshotSchedules.closeBox();
		}
	},
	
	checkConflict: function(status)
	{
		if(!status)
		{
			jAlert('Snapshot could not be created');
			
			SnapshotSchedules.closeBox();
		}
		else
		{
			jAlert('Snapshot Title already Exists. Enter a different Title');
			$("#popup_container").css("z-index","9999999");
		}
	}
};
	