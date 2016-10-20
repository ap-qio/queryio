TriggerList = {
	
	selectedCount : 0,	
	triggerCache : [],
	scheduleArray :new Array(),
	response: null,
	
	ready: function()
	{
		dwr.util.byId('trigger.delete').disabled=true;
		RemoteManager.getAllTriggerDetails(TriggerList.populateList);
	},
	
	populateList: function(list)
	{
		var tabledata =[];
		TriggerList.scheduleArray.splice(0 , TriggerList.scheduleArray.length);
		if(list!=null){
			for(var i=0;i<list.length;i++){
				var trigger = list[i];
				TriggerList.triggerCache.push(trigger);
				tabledata.push(['<input type="checkbox" onClick="javascript:TriggerList.clickBox(this.id)" id="Test'+trigger.ID+'">',
				                trigger.jobName,trigger.jobGroup, trigger.startTime, trigger.endTime, trigger.status, trigger.reasonForFailure ]);
			}
		}
		$('#trigger_list_table').dataTable( {
	        "bPaginate": true,
			"bLengthChange": true,
			"sPaginationType": "full_numbers",
			"bFilter": true,
			"bSort": true,
			"bInfo": false,
			"bAutoWidth": true,
			"bDestroy": true,
			"aaSorting": [[ 1, "desc" ]],
			"aaData": tabledata,
	        "aoColumnDefs": [{ "bSortable": false, "aTargets": [ 0 ] } ],
	        "aoColumns": [
//	            { "sTitle": "<input type = 'checkbox' id = 'selectAllTriggers' onclick='javascript:TriggerList.selectAllTriggers()'>"},
				{ "sTitle": "<input type = 'checkbox' id = 'selectAllTriggers' onclick='javascript:TriggerList.selectAllSchedules(this.id)'>"},
	            { "sTitle": "Schedule ID" },
	            { "sTitle": "Schedule Type" },
	            { "sTitle": "Start Time" },
	            { "sTitle": "End Time" },
	            { "sTitle": "Status" },
	            { "sTitle": "Reason For Failure" }
	        ]
	    	} ); 
		
	   	//disabled the checkAll button when no data in the list
	   	if(list == null || list == undefined || list.length == 0)
			$('#selectAllTriggers').attr('disabled',true);
		else
			$('#selectAllTriggers').removeAttr('disabled');
	   	
	   	$("#trigger_list_table_filter").css('margin-top', '5px');
	   	$("#trigger_list_table_filter").css('margin-bottom', '5px');
	   	$("#trigger_list_table_length").css('margin-top', '5px');
	},
		
	clickBox : function(id)
	{
		var flag = document.getElementById(id).checked;
		if (flag == true)
		{
			TriggerList.scheduleArray.push(id);
		}
		else
		{
			var index = jQuery.inArray(id, TriggerList.scheduleArray);
			if (index != -1)
			{
				TriggerList.scheduleArray.splice(index, 1);
			}
		}
		if(($('#trigger_list_table tr').length - 1) == TriggerList.scheduleArray.length)
		{
			document.getElementById("selectAllTriggers").checked = flag;
			TriggerList.selectAllSchedules("selectAllTriggers", flag);
		}
		else
			TriggerList.toggleButton(id, flag, "selectAllTriggers");
	},
	
	deleteTriggers: function()
	{
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm('Are you sure you want to delete selected item(s)?','Delete Schedule Status',function(val)
		{
			if (val == true)
			{
				var id = [];
				for(var i=0;i<TriggerList.triggerCache.length;i++)
				{
					if(document.getElementById('Test'+TriggerList.triggerCache[i].ID).checked)
					{
						var trigger = TriggerList.triggerCache[i].ID;
						id.push(trigger);
					}
				}
				RemoteManager.deleteTriggers(id,TriggerList.deleteReturn);
			}
			else
				return;
			jQuery.alerts.okButton = ' Ok';
			jQuery.alerts.cancelButton  = ' Cancel';
		});
	},
	
	deleteReturn: function(resp)
	{
		if(resp.taskSuccess)
		{
			jAlert("Selected schedule status deleted successfully","Schedule status removal");
		}
		else
		{
			jAlert("Selected schedule status deletion failed due to "+resp.responseMessage, "Schedule status removal");
		}
		Navbar.refreshView();
	},
	
	
	selectAllSchedules: function (id){
		
		var flag = document.getElementById("selectAllTriggers").checked;
		TriggerList.scheduleArray.splice(0, TriggerList.scheduleArray.length);
		for (var i=0; i<TriggerList.triggerCache.length; i++)
			{
				document.getElementById('Test'+TriggerList.triggerCache[i].ID).checked = flag;
				if (flag)
				{	
					TriggerList.scheduleArray.push('Test'+TriggerList.triggerCache[i].ID);
				}
			}
		TriggerList.toggleButton(id , flag);
	},
	
	
	toggleButton : function(id , value)
		{

			if (id == "selectAllTriggers")
			{
				dwr.util.byId('trigger.delete').disabled=!value;
			}
			else
			{
			if(value == false)
				$('#selectAllTriggers').attr("checked",false);
			
			if (TriggerList.scheduleArray.length < 1)
			{
				dwr.util.byId('trigger.delete').disabled=true;
			}
			else
			{
				dwr.util.byId('trigger.delete').disabled=false;
			}
				
		
			}
		}

};