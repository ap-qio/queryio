AlertList = {
	ruleResponse : [],
	currentRuleId : '',
	
	ready: function () {
		RemoteManager.getAlertList(0,AlertList.populateAlertList);
		dwr.util.byId('alert.delete').disabled=true;
   	},
  	
   	clickCheckBox : function(chkbxid){
   		var ruleId=chkbxid.substring(4,chkbxid.length);
   		var alert= '';
   		var startTime='';
   			if(document.getElementById(chkbxid).checked){
		   			selectedAlert.push(ruleId);

   			}
   			else{
   				for(var i = 0;i<selectedAlert.length;i++){
   					if(selectedAlert[i]==ruleId){
   						selectedAlert.splice(i,1);
   					}
   				}
   			}
   			
   			if(selectedAlert.length>0)
   				dwr.util.byId('alert.delete').disabled=false;
   			else
   				dwr.util.byId('alert.delete').disabled=true;
   			
   			if (($('#alerts_list_table tr').length - 1) == selectedAlert.length)
   				document.getElementById('selectAll').checked = true;
   			else
   				document.getElementById('selectAll').checked = false;
   	},
   	
   	populateAlertList: function(list){
   		if(list!=null)
   		{
   				var alerts = new Array();
	   			for(var i=0;i<list.length;i++)
	   			{
		   			var alertTab=list[i]; 
		   			var ruleId=alertTab.ruleId;
		   			var ruleLink = "<a onclick = \"AlertList.showRuleAttributes('"+ruleId+"');\" style = \"cursor: pointer;\">" + alertTab.ruleId + "</a>";
		   			var check='<input type="checkbox" id="test'+ruleId+'$'+alertTab.startTime+'$'+alertTab.endTime+'" onclick="javascript:AlertList.clickCheckBox(this.id)">';
					var severity=alertTab.severity;
					var ip=alertTab.hostname;
					var time='<span id = "span'+ruleId+'">'+ alertTab.startTime+'</span>';
					var desc=alertTab.description;
					tableList.push([check,severity,ip,ruleLink,desc,alertTab.startTime]);
					alerts.push(alertTab);
	   			}
	   			alertArray = alerts;
	   			
	  	}
	   	$('#alerts_list_table').dataTable( {
	   			"bPaginate": true,
	   			"bLengthChange": true,
				"sPaginationType": "full_numbers",
				"bFilter": true,
				"bSort": true,
				"bInfo": false,
				"bDestroy": true,
				"bAutoWidth": true,
				"aaSorting": [[ 1, "desc" ]],
				"aLengthMenu": [[ 50, 100, 200, 500 ], [ 50, 100, 200, 500 ]],
				"iDisplayLength" : 50,
				"aaData": tableList,
		        "aoColumns": [
		            { "sTitle": '<input type="checkbox" id="selectAll" onclick="javascript:AlertList.selectAllAlerts(this)">' },
		            { "sTitle": "Severity" },
		            { "sTitle": "Machine" },
		            { "sTitle": "RuleID" },
	   	            { "sTitle": "Description" },
		            { "sTitle": "Alert Time" },
		        ]
		    } );
	   	
	   	Navbar.getPreferredRowCountForDataTable("alerts_list_table");
	   	
	   	//disabled the checkAll button when no data in the list
	   	if(list == null || list == undefined || list.length == 0)
			document.getElementById('selectAll').disabled = true;
		else
			document.getElementById('selectAll').disabled = false;
		$('#alerts_list_table_length').css('margin-top' , 5+'px');
		$('#alerts_list_table_length').css('margin-bottom' , 5+'px');
		$('.dataTables_filter').css('margin-top', '3px');
   	},
   	
   	showRuleAttributes : function(ruleId)
   	{
   		AlertList.currentRuleId = ruleId;
   		RemoteManager.getRuleBean(ruleId, AlertList.callBackRule);
   	},
   	
   	callBackRule :  function(response)
   	{
   		AlertList.ruleResponse = response;
   		Util.addLightbox("viewRule", "resources/ruleViewer.html", null, null);
   	},
   	
   	fillRuleViewer : function()
   	{
   		$('#view_rule').css({
   			position:'absolute',
   			left: ($(window).width() - $('#view_rule_table').outerWidth())/2,
   			
   		});
   		
   		var colList = [
   		               {"sTitle":'Attribute'},
   		               { "sTitle":'Condition'},
   		               { "sTitle":'Value'},
   		               {"sTitle":'Aggregate Function'},
   		               { "sTitle":'Duration (secs)'}]; 
   		
   		$('#viewAttrTable').dataTable({
   	        "bPaginate": false,
   			"bLengthChange": false,
   			"bFilter": false,
   			"bSort": false,
   			"bInfo": false,
   			"bAutoWidth": false,
   			"bDestroy": true,
   			"aoColumns": colList
   	    });

   		$('#ruleID').text(AlertList.currentRuleId);
   		if(AlertList.ruleResponse == null && AlertList.ruleResponse == undefined)
   		{
   			return;
   		}
   		else
   		{
   			var rowId = 1;
   			for(var i = 0; i<AlertList.ruleResponse.attrNames.length;i++){
   				$('#viewAttrTable').dataTable().fnAddData([{
   					"0":'<input type = "hidden" id="attr-'+rowId+'" name = "attr" value = "'+AlertList.ruleResponse.attrNames[i]+'">'+AlertList.ruleResponse.attrNames[i].substring(AlertList.ruleResponse.attrNames[i].indexOf('#')+1),
   					"1":'<select disabled name="condSelect" id = "condSelect' + rowId + '" class="report_list_box"><option value="Over">Greater than</option><option value="Under">Less than</option><option value="Equals">Equal to</option><option value="Not Equals">Not Equal to</option></select>',
   					"2":'<input readonly type = "text" id = "conval'+rowId+'" />',
   					"3":'<select disabled name="functionSelect" id = "aggSelect' + rowId + '" class="report_list_box"><option value="none">None (All Values)</option><option value="avg">Average</option><option value="min">Minimum</option><option value="max">Maximum</option></select>',
   					"4":'<input readonly id="durations'+rowId+'" type="text" />',
   					"DT_RowId": 'row_'+rowId}]);
   				rowId++;
   			}
   			rowId = 1;
   			for(var i = 0; i<AlertList.ruleResponse.attrNames.length;i++){
   				document.getElementById('condSelect'+rowId).value = AlertList.ruleResponse.conditions[i];
   				document.getElementById('conval'+rowId).value = AlertList.ruleResponse.values[i];
   				if(AlertList.ruleResponse.aggregateFunctions[i] == null || AlertList.ruleResponse.aggregateFunctions[i] == "")
   					AlertList.ruleResponse.aggregateFunctions[i] = "none";
   				document.getElementById('aggSelect'+rowId).value = AlertList.ruleResponse.aggregateFunctions[i];
   				document.getElementById('durations'+rowId).value = AlertList.ruleResponse.durations[i];
   				rowId++;
   			}   			
   		}
   	},
   	
   	closeViewBox : function()
   	{
   		Util.removeLightbox("viewRule");
   	},
   	
   	deleteAlerts :function()
   	{
   		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm('Are you sure you want to delete selected item(s)?','Delete Alert(s)',function(val)
		{
			if (val == true)
			{
				RemoteManager.deleteAlerts(0,selectedAlert,AlertList.showDeletedAlertResponse);	
			}
			else
				return;
			jQuery.alerts.okButton = ' Ok ';
			jQuery.alerts.cancelButton  = ' Cancel';
		});
   		
   	},
   	showDeletedAlertResponse : function(response)
   	{
   		jAlert(response,"Alert Delete");
   		Navbar.refreshView();
   	},
   	selectAllAlerts : function(element)
   	{
   		selectedAlert.splice(0, selectedAlert.length);
		var val = element.checked;
		for (var i=0;i<document.forms[0].elements.length;i++)
	 	{	
	 		var e=document.forms[0].elements[i];
	 		if ((e.id != 'selectAll') && (e.type=='checkbox'))
	 		{
	 				e.checked=val;
	 			AlertList.clickCheckBox(e.id);
	 		}
	 	}
   		
   	}
   	
};
	