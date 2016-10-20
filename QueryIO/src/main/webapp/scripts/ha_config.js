HAConfig = {
	
		configKeyList:'',
	
	saveSettings: function ()
	{
		
		document.getElementById('ha.save').disabled = true;
		var keyList = HAConfig.configKeyList;
		var configvalueList= new Array();
		for(var i=0;i<HAConfig.configKeyList.length;i++)
		{
			configvalueList.push(document.getElementById(keyList[i]).value)
		}
		RemoteManager.updateHadoopConfigs(keyList,configvalueList,HAConfig.settingUpdated);
	},
	settingUpdated : function(dwrResponse)
	{
		if(dwrResponse.taskSuccess){
			
			jAlert("High Availability Configuration updated successfully.","Configuration Saved");
		}
		else{
			jAlert(dwrResponse.responseMessage,"Operation Failed");
		}
		Navbar.refreshView();
	},
	
	populateConfigTable : function (summaryTable)
	{
		$("#config_table").remove();
		if(summaryTable == null || summaryTable == undefined)
		{
			$("#config_table_div").html('<span style="text-align:center;">Config details not available. </span>');
			return;
		}
		else{
			$("#config_table_div").html('<table id="config_table"></table>');
			
		}
		
		HAConfig.configKeyList = new Array();
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
			HAConfig.configKeyList.push(row[0]);
			tableRow.push(rowData);	
		}
		
		//rowList = summaryTable.rows;

		$('#config_table').dataTable({
	        "bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": false,
			"bInfo": false,
			"bAutoWidth": false,
			"bDestroy": true,
			"bDestroy": true,
			"aaData": tableRow,
	        "aoColumns": colList
	    });
	},
	
	ready: function () {
		RemoteManager.getHAHadoopConfigs(HAConfig.populateConfigTable);
//		$('#config_table').dataTable( {
//	        "bPaginate": false,
//			"bLengthChange": false,
//			"bFilter": false,
//			"bSort": true,
//			"bInfo": false,
//			"bAutoWidth": true,
//			"aaSorting": [[ 1, "desc" ]],
//			"aaData": [
//	            [ 'dfs.blocksize', '<input type="text" id="dfs.blocksize" value="512">'],
//	            [ 'dfs.permissions.supergroup', '<input type="text" id="dfs.permissions.supergroup" value="admin">'],
//	            [ 'dfs.replication.count', '<input type="text" id="dfs.replication.count" value="2">'],
//	            [ 'dfs.replication', '<input type="text" id="dfs.replication" value="true">']
//	        ],
//	        "aoColumnDefs": [{ "bSortable": false, "aTargets": [ 1] } ],
//	        "aoColumns": [
//	            { "sTitle": "Property" },
//	            { "sTitle": "Value" },
//	        ]
//	    } ); 

   	}
};
	