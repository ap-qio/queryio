RulesList = {

	ready: function () {
		RulesList.checkEnableDisable();
		RemoteManager.getRuleList(RulesList.populateRuleList);
	},
   	
   	checkEnableDisable: function(){
		if(selectedRule.length>0){
			dwr.util.byId('rule.delete').disabled=false;
			if(selectedRule.length>=2){
				dwr.util.byId('rule.edit').disabled=true;
				dwr.util.byId('rule.suspend').disabled=true;
				dwr.util.byId('rule.restart').disabled=true;
			}
			else{
				dwr.util.byId('rule.edit').disabled=false;
			}
			
		}
		else{
			dwr.util.byId('rule.delete').disabled=true;
			dwr.util.byId('rule.edit').disabled=true;
			dwr.util.byId('rule.suspend').disabled=true;
			dwr.util.byId('rule.restart').disabled=true;
		}
	},
   	
	selectAllRuleRow :function (element)
	{
		var val = element.checked;
		selectedRule.splice(0, selectedRule.length);
		for (var i=0;i<document.forms[0].elements.length;i++)
	 	{
	 		var e=document.forms[0].elements[i];
	 		if ((e.id != 'selectAllTest') && (e.type=='checkbox'))
	 		{
	 				e.checked=val;
	 				this.clickCheckBox(e.id);
	 		}
	 	}
	},

   	clickCheckBox : function(chkbxid){
   		var ruleId=chkbxid.substring(4,chkbxid.length);
		//dwr.util.byId('rule.suspend').disabled=false;
   		var rule;
   		for(var i=0;i<ruleArray.length;i++)
   		{
   			if(ruleId==ruleArray[i].ruleId){			
   				 rule = ruleArray[i];
   				 break;
   			}
   		}
   		if(document.getElementById(chkbxid).checked)
   			selectedRule.push(rule.ruleId)
   		else
   			selectedRule.splice(selectedRule.indexOf(rule.ruleId),1);
   		if(selectedRule.length == 1)
   		{
   			if(rule.ruleIgnored){
   				dwr.util.byId('rule.suspend').disabled=true;
   				dwr.util.byId('rule.restart').disabled=false;
   			}else{
   				dwr.util.byId('rule.suspend').disabled=false;
   				dwr.util.byId('rule.restart').disabled=true;
   			}
   		}
   		if(($('#rules_list_table tr').length -1) == selectedRule.length)
   			$("#selectAllTest").attr('checked', 'checked');
   		else
   			$("#selectAllTest").removeAttr('checked');
   		RulesList.checkEnableDisable();
   	},
   	
  /*	handleAllButton : function(flag){
   		if(flag){
   			$("#rule.delete").removeAttr('disabled');
   	   		$('#rule.delete').removeClass("ui-state-disabled");
   	   		$("#rule.restart").removeAttr('disabled');
   	   		$('#rule.restart').removeClass("ui-state-disabled");
   	   		$("#rule.suspend").removeAttr('disabled');
	   		$('#rule.suspend').removeClass("ui-state-disabled");
	   		if(selectedRule.length==1){
	   	 		$("#rule.edit").removeAttr('disabled');
		   		$('#rule.edit').removeClass("ui-state-disabled");
	   		}
   		}
   		else{
   			$('#rule.delete').attr("disabled", "disabled").addClass("ui-state-disabled");
   			$('#rule.suspend').attr("disabled", "disabled").addClass("ui-state-disabled");
   			$('#rule.restart').attr("disabled", "disabled").addClass("ui-state-disabled");
   			$('#rule.edit').attr("disabled", "disabled").addClass("ui-state-disabled");
   			
   		}
   	},
   	*/
   	
  	populateRuleList: function(list){
  	
   	if(list!=null){
   		var rules = [];
   		for(var i=0;i<list.length;i++){
   			var rule=list[i];
			var check='<input type="checkbox" id="test'+rule.ruleId+'" onclick="javascript:RulesList.clickCheckBox(this.id)">';
   			var ruleId=rule.ruleId;
			var nodeIp=rule.hostName;
			var severity=rule.severity;
//			var description=rule.description;
			var expressions=rule.expressions;
			var attr=rule.attributes;
			var notify=rule.notificationType;
			var alerts='<a href=\'javascript:RulesList.showAlerts("'+rule.ruleId+'");\'>Details</a>';
			tableList.push([check ,ruleId,nodeIp,severity,attr,notify,alerts]);
			rules.push(rule);
			totalRules++;
   		}
   		ruleArray = rules;
   	}		
   	$('#rules_list_table').dataTable( {
	        "bPaginate": false,
			"bLengthChange": false,
			"sPaginationType": "full_numbers",
			"bFilter": false,
			"bDestroy": true,
			"bSort": true,
			"bDestroy": true,
			"bInfo": false,
			"bAutoWidth": false,
			"aaSorting": [[ 1, "desc" ]],
			"aaData": tableList,
	        "aoColumnDefs": [{ "bSortable": false, "aTargets": [ 0] } ],
	        "aoColumns": [
	            { "sTitle": '<input type="checkbox" id="selectAllTest" onclick="javascript:RulesList.selectAllRuleRow(this)">' },
	            { "sTitle": "Rule ID" },
	            { "sTitle": "Host" },
	            { "sTitle": "Severity" },
	            { "sTitle": "Attribute" },
	            { "sTitle": "Notify" },
	            { "sTitle": "Alerts" },
	        ]
	    } ); 
   	
   		//disabled the checkAll button when no data in the list
   		if(list == null || list == undefined || list.length == 0)
   			document.getElementById('selectAllTest').disabled = true;
   		else
   			document.getElementById('selectAllTest').disabled = false;

   	
   	},
   	
   	addRule: function() {
   		isEditRule=false;
   		Util.addLightbox("addrule", "resources/addRule.html", null, null);
   	},
   	
   	closeBox : function(isRefresh){
   		Util.removeLightbox("addrule");
   		if(isRefresh)
   		{
   			Navbar.refreshView();
   			Navbar.refreshNavBar();
   		}
   	},
   	closeAlertDetailBox: function(){
   		Util.removeLightbox("alert");
   		Navbar.refreshView();
		Navbar.refreshNavBar();
   	},
   	
   	showAlerts: function(ruleId){
   		alertId = ruleId;
   		Util.addLightbox("alert", "resources/config_alert_detail.html", null, null);
   	},
   	
   	populateConfigAlertBox: function(){
   		RemoteManager.getAlertListForRule(0,alertId,RulesList.showTable);
   	},
   	showTable: function(list)
   	{
	   	if(list!=null){
	   		tableList=[];
			for(var i=0;i<list.length;i++){
				var alertTab=list[i]; 
				var severity=alertTab.severity;
				var ip=alertTab.hostname;
				var ruleId=alertTab.ruleId;
				var time=alertTab.startTime;
				var desc=alertTab.description;
				tableList.push([severity,ip,ruleId,time]);
		}
   	}
	$('#conf_alert_list_table').dataTable( {
			"sScrollX": "100%",
			"bPaginate": true,
			"bLengthChange": true,
			"sPaginationType": "full_numbers",
			"bFilter": false,
			"bSort": true,
			"bInfo": false,
			"bDestroy": true,
			"bAutoWidth": true,
			"aLengthMenu": [[ 50, 100, 200, 500 ], [ 50, 100, 200, 500 ]],
			"aaSorting": [[ 1, "desc" ]],
			"aaData": tableList,
			"aoColumns": [
	            { "sTitle": "Severity" },
	            { "sTitle": "Machine" },
	            { "sTitle": "RuleID" },
	            { "sTitle": "Alert Time" },
	        ]
	    } ); 
	$('#dataTables_scrollHead').css('font-size','1.2em');
	
   	
},

	editSelectedRule : function(){
		if(selectedRule.length==1){
			RulesList.addRule();
			editRule(selectedRule[0]);
		}
	},
	
	deleteSelectedRules : function(){
		if(selectedRule.length!=0)
		{
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm('Are you sure you want to delete selected item(s)?','Delete Rules for Alert(s)',function(val)
			{
				if (val == true)
				{
					for(var i=0;i<selectedRule.length;i++)
					{
						RemoteManager.deleteRule(selectedRule[i],RulesList.showResponse);
					}
				}
				else
					return;
			});
		}
		else
		{
			jAlert("No Rule Selected");
		}
	},
	
	
	suspendSelectedRules : function(){
		if(selectedRule.length!=0){
			for(var i=0;i<selectedRule.length;i++){
				RemoteManager.suspendRule(selectedRule[i],RulesList.showResponse);
				}
			}
		else{
			jAlert("No Rule Selected");
		}
	},
	
	
	startSelectedRules : function(){
		if(selectedRule.length!=0){
			for(var i=0;i<selectedRule.length;i++){
				RemoteManager.startRule(selectedRule[i],RulesList.showResponse);
				}
		}
		else{
			jAlert("No Rule Selected");
		}
	},
	
	
	showResponse : function(response){
		jQuery.alerts.okButton = ' Ok ';
		jAlert(response);
		Navbar.refreshView();
	},
	
	
	configureNotifications: function(){
		Navbar.changeTab('Notifications','dashboard', 'notifications_rules_for_alerts');
	}
	
};
	