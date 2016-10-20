SystemConfig = {
	
	configKeyList:'',
	
	saveSettings: function ()
	{
		document.getElementById('systemconfig.save').disabled = true;
		var keyList = SystemConfig.configKeyList;
		var configvalueList= new Array();
		for(var i=0;i<keyList.length;i++)
		{
			configvalueList.push(document.getElementById(keyList[i]).value);
		}
		RemoteManager.updateHadoopConfigs(keyList,configvalueList,SystemConfig.settingUpdated);
	},
	
	settingUpdated : function(dwrResponse)
	{
		if(dwrResponse.taskSuccess){
			
			jAlert("System Configuration updated successfully.","Configuration Saved");
		}
		else{
			jAlert(dwrResponse.responseMessage,"Updation failed");
		}
		SystemConfig.closeBox();
		Navbar.refreshView();
	},
	
	populateConfigTable : function (summaryTable)
	{
		if(summaryTable == null || summaryTable == undefined)
		{
			$("#config_table").html('<span>Config details not available. </span>');
			return;
		}
		
		SystemConfig.configKeyList = new Array();
		var flag=true;
		var colList=[];
		var rowList='';
		var tableRow = new Array();
		if (flag)
		{
			flag = false;
			for(var i=0; i< summaryTable.colNames.length; i++)
			{
				colList.push({ "sTitle": summaryTable.colNames[i]});	
			}
			colList.push({ "sTitle": ""});	
		}
		rowList = summaryTable.rows;
		var row;
		for(var i=0; i<rowList.length; i++)
		{
			row = rowList[i];
			var rowData = new Array();
			for(var j=0;j<row.length;j++)
			{
				rowData.push(row[j]);
			}
			SystemConfig.configKeyList.push(row[1]);
			rowData.push("<a href=\"javascript:SystemConfig.deleteKey('"+row[1]+"');\"><img  src=\"images/delete_icon.png\" style=\"height: 12pt; margin-right: 1pt; margin-top: 1pt;\"/></a>");
			tableRow.push(rowData);	
		}
		
		
		$('#config_table').dataTable({
			"bPaginate": false,
			"bLengthChange": true,
			"bFilter": false,
			"bSort": true,
			"bDestroy": true,
			"bInfo": false,
			"bAutoWidth": false,
			"aaData": tableRow,
	        "aoColumns": colList
	    });
	},
	deleteKey : function(key){
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm('Are you sure you want to delete key?','Confirm',function(val)
		{
			if (val)
			{
				
				RemoteManager.deleteSystemConfigurationKey(key,SystemConfig.handleDeleteKeyResponse);
			}
				
		});
		
	},
	handleDeleteKeyResponse : function(dwrResponse){
		if(dwrResponse.taskSuccess){
			jAlert("Key deleted successfully","Success");
			Navbar.refreshView();
		}else{
			jAlert(dwrResponse.responseMessage, "Failed");
		}
	},
	ready: function (flag) {
		if(flag){
			RemoteManager.getAllHadoopConfigsForHDFSType(SystemConfig.populateConfigTable);
		}
		else{
			RemoteManager.getAllHadoopConfigsForMapReduceType(SystemConfig.populateConfigTable);
		}
   	},
   	
	addNewKey : function()
	{
		Util.addLightbox("addDB","resources/addNewConfigKey.html",null, null);
	},
	
	closeBox : function()
	{
		Util.removeLightbox("addDB");
	},
	
	addNewKeyinConfig : function()
	{
		var id = 'Save';
		
		var type = $("#type").val();
		var newKey = $('#configKey').val();
		var keyval = $('#configValue').val();
		var desc = $('#configDesc').val();
		RemoteManager.addHadoopConfigValue(type, newKey, keyval, desc, SystemConfig.settingUpdated);
	},
	
	fillType : function(flag)
	{
		if(flag)
		{
			$("#type").html("<option value='HDFS'>HDFS</option>" +
							  "<option value='Datanode'>Datanode</option>" +
							  "<option value='Namenode'>Namenode</option>" +
							  "<option value='Checkpoint Node'>Checkpoint Node</option>");
		}
		else
		{
			$("#type").html("<option value='Map Reduce'>Map Reduce</option>" +
					  "<option value='Resource Manager'>Resource Manager</option>" +
					  "<option value='Node Manager'>Node Manager</option>");
		}
	}
	
};
	