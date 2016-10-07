DBM = {
		
	errorMsg : null,
	currentError : '',
		
	ready: function()
	{
		Navbar.selectedDatabaseFromMigrationTable = null;
		RemoteManager.getMigrationStatus(DBM.populateMigrationStatusTable);
	},

	populateMigrationStatusTable : function (sTable)
	{
		$('#migration_table_container').html('<table id="migration_table" class="dataTable"></table>');
		
		var columns = sTable.colNames;
   		var rows = sTable.rows;
		var dataColumn = [];
		var columnValue;
		for(var i=0;i<columns.length;i++)
		{
			dataColumn.push({"sTitle":columns[i]});
		}
		
		var dataRow = new Array();
		var index = 0;
		DBM.errorMsg = new Array();
		for(var i=0;i<rows.length;i++){
			var rowDetail = rows[i];
			var rowData = new Array();
			for(var j=0;j<rowDetail.length;j++){
					if(j == rowDetail.length-1 && rowDetail[j] != "-")
					{
						DBM.errorMsg[index] = rowDetail[j];
						columnValue = '<a onclick = "DBM.addErrorBox(' + index + ');" style = "cursor: pointer; text-underline: none;">View Error</a>';
						index ++;
					}
					else if(j == 0 || j == 1)
					{
						//	Add link to Source and destination Database.
						columnValue = '<a onclick = "javascript:DBM.redirectOnDBManagerView(\'' + rowDetail[j] + '\');" style = "cursor: pointer;" >' + rowDetail[j] + '</a>';
					}
					else
						columnValue = rowDetail[j];
					rowData.push(columnValue);
			}
			dataRow.push(rowData);
		}
		$('#migration_table').dataTable( {
	        "bPaginate": false,
			"bLengthChange": true,
			"bFilter": false,
			"bSort": false,
			"bDestroy": true,
			"bInfo": false,
			"bAutoWidth": true,
			"aaData": dataRow,
	        "aoColumns": dataColumn
	    } );
		setTimeout('DBM.progressCaller();',2000);
	},
	progressCaller : function(){
		if(document.getElementById('migration_table_container')==undefined||document.getElementById('migration_table_container')=='undefined'){
			return;
		}
//		RemoteManager.getMigrationStatus(DBM.populateMigrationStatusTable);
	},
	
	addErrorBox : function(index)
	{
		DBM.currentError = DBM.errorMsg[index];
		Util.addLightbox("errorBox","resources/dbMigrationError.html");
	},
	
	showError : function()
	{
		$("#logcontainer").val(DBM.currentError);
	},
	
	redirectOnDBManagerView : function(databaseName)
	{
		Navbar.selectedDatabaseFromMigrationTable = databaseName;
		Navbar.showSelectedDb = true;
		Navbar.changeTab('DB_Config','data', 'db_Config');
	},
	
	closeBox : function()
	{
		Util.removeLightbox('errorBox');
	}
};