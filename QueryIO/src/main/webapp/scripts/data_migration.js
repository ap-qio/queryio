DataMigrate = {
	attemp : 0,
	fileId : '',
	counter : 1,
	fileName : [],
	title : null,
	nnID : null,
	hdfsPath : null,
	submitted : [],
	maxConn : 20, 
	maxIdleConn : 10, 
	waitTime : 30000,
	jqGridColumnDetails : [],
	timer : [],
	responseJson : null,
	list : new Array(),
	tablenames : new Array(),
	dbdriverjar : '',
	alltables : new Array(),
	tableFlag : false,
	fileIsUnzipStatus : new Array(),
	fileSizeStatus : new Array(),
	count : 0,
	maxCount : 0,
	isFirstTimeStatusCalled : null,

	currentDataStore : '',

	ready : function() {
		
		for ( var i = 0; i < DataMigrate.timer.length; i++) {
			clearTimeout(DataMigrate.timer[i]);
		}
		
		if (isFirstTimeCluster)
			isFirstTimeCluster = false;
		
		RemoteManager.getAllMigrationDetails(DataMigrate.fillDataMigrateTableInfo);
			
		document.getElementById('datamigration.resume').disabled = true
		document.getElementById('datamigration.delete').disabled = true
		document.getElementById('datamigration.cancel').disabled = true
	},

	fillDataMigrateTableInfo : function(list) {
		
//		if(isFirstTimeCluster){
//			Navbar.refreshView();
//			isFirstTimeCluster = false;
//			return;
//		}
		
		if (document.getElementById('data_migration_table_container') == null
				|| document.getElementById('data_migration_table_container') == undefined) {
			return;
		}
		$('#data_migration_table').remove();
		$('#data_migration_table_container').html(
				'<table id="data_migration_table" ></table>');

		if (list == null || list.length == 0) {
			$('#data_migration_table')
			.dataTable(
					{
						"bPaginate" : true,
						"bLengthChange" : true,
						"sPaginationType" : "full_numbers",
						"aLengthMenu" : [50, 100, 200, 500 ],
						"iDisplayLength" : 50,
						"bFilter" : false,
						"bSort" : true,
						"bInfo" : false,
						"bAutoWidth" : false,
						"aaData" : [],
						"bDestroy" : true,
						"aoColumnDefs" : [ {
							"bSortable" : false,
							"aTargets" : [ 0 ]
						} ],
						"aoColumns" : [
								{
									"sTitle" : '<input type="checkbox" id="selectAll" onclick="javascript:DataMigrate.selectAllMigration(this)">'
								}, {
									"sTitle" : "NameNodeID"
								}, {
									"sTitle" : "Title"
								}, {
									"sTitle" : "DataSource"
								}, {
									"sTitle" : "Source Path"
								}, {
									"sTitle" : "Destination Path"
								}, {
									"sTitle" : "Start Time"
								}, {
									"sTitle" : "End Time"
								}, {
									"sTitle" : "Status"
								}, {
									"sTitle" : "Progress"
								}
						]
					});
			//quick fix of issue : list is coming null  first time. 
//			Navbar.refreshView();
			return;
		}
		var tableList = new Array();
		var isProgressStart = false;
		var prgress = new Array();

		if (list != null) {

			for ( var i = 0; i < list.length; i++) {
				totalMigration++;
				var migratationItem = list[i];
				var row = new Array();
				var check = '<input type="checkbox" id="'
						+ migratationItem[0]
						+ '" onclick="javascript:DataMigrate.clickCheckBox(this.id)">';
				row.push(check);
				var isImport = migratationItem[1];
				for ( var j = 2; j < migratationItem.length - 1; j++) {
					if (j == 4) {
						if (isImport) {
							row
									.push('<img style="vertical-align:middle" src="images/upload-icon.ico" height="16" width="16" > <span id="migrationType'
											+ migratationItem[0]
											+ '">'
											+ migratationItem[j] + '</span>');
						} else {
							row
									.push('<img style="vertical-align:middle" src="images/icon_download.gif" height="16" width="16" > <span id="migrationType'
											+ migratationItem[0]
											+ '">'
											+ migratationItem[j] + '</span>');
						}
						continue
					}
					if (j == 8) {
						row.push('<span id = "span-endtime-' + migratationItem[0]
								+ '">' + migratationItem[j] + '</span>');
						continue;
					}
					if (j == 9) {
						row.push('<span id = "span-' + migratationItem[0]
								+ '">' + migratationItem[j] + '</span>');
						continue;
					}
					if (j == migratationItem.length - 2) {
						if ((migratationItem[j] != "Failed")
								|| (migratationItem[j] != "Completed"))
							isProgressStart = true;
					}
					row.push(migratationItem[j]);
				}
				prgress.push(migratationItem[migratationItem.length - 1]);
				row
						.push('<div id="progressbar'
								+ migratationItem[0]
								+ '" style="height: 15px; background-color: #E6E9ED; "><span id="progresst'
								+ migratationItem[0]
								+ '" style="    position:absolute;     text-align: center; " ">'
								+ migratationItem[migratationItem.length - 1]
								+ '%</span></div>');
				tableList.push(row);
			}

		}
		
		$('#data_migration_table')
				.dataTable(
						{
							"bPaginate" : true,
							"bLengthChange" : true,
							"sPaginationType" : "full_numbers",
							"aLengthMenu" : [50, 100, 200, 500 ],
							"iDisplayLength" : 50,
							"bFilter" : false,
							"bSort" : true,
							"bInfo" : false,
							"bAutoWidth" : false,
							"aaData" : tableList,
							"bDestroy" : true,
							"aoColumnDefs" : [ {
								"bSortable" : false,
								"aTargets" : [ 0 ]
							} ],
							"aoColumns" : [
									{
										"sTitle" : '<input type="checkbox" id="selectAll" onclick="javascript:DataMigrate.selectAllMigration(this)">'
									}, {
										"sTitle" : "NameNodeID"
									}, {
										"sTitle" : "Title"
									}, {
										"sTitle" : "DataSource"
									}, {
										"sTitle" : "Source Path"
									}, {
										"sTitle" : "Destination Path"
									}, {
										"sTitle" : "Start Time"
									}, {
										"sTitle" : "End Time"
									}, {
										"sTitle" : "Status"
									}, {
										"sTitle" : "Progress"
									}

							]
						});

		for ( var i = 0; i < list.length; i++) {
			var migratationItem = list[i];

			$('#progressbar' + migratationItem[0]).progressbar({
				value : migratationItem[migratationItem.length - 1],
				showText : true
			});
		}
		$('.ui-widget-header').css("background-color", "#66CD00");

		// if (isProgressStart)
		
		DataMigrate.updateProgressBar();

		// disabled the checkAll button when no data in the list
		if (list == null || list == undefined || list.length == 0)
			document.getElementById('selectAll').disabled = true;
		else
			document.getElementById('selectAll').disabled = false;

		$('#data_migration_table_length').css('margin-top', 7 + 'px');
		

	},
	updateProgressBar : function() {
		var timerProcess = setTimeout(
				function() {
					RemoteManager
							.getAllMigrationDetails(DataMigrate.updateMigrationProgress)
				}, 1000);
		DataMigrate.timer.push(timerProcess);
	},

	updateMigrationProgress : function(list) {
		if (document.getElementById('data_migration_table_container') == null
				|| document.getElementById('data_migration_table_container') == undefined) {
			return;
		}
		if (list.length == 0) {

			var timerProcess = setTimeout(
					function() {
						RemoteManager
								.getAllMigrationDetails(DataMigrate.updateMigrationProgress)
					}, 1000);
			DataMigrate.timer.push(timerProcess);
		} else {
			for ( var i = 0; i < list.length; i++) {
				var migratationItem = list[i];
				
				$('#span-endtime-' + migratationItem[0]).text(migratationItem[8]);
				$('#span-' + migratationItem[0]).text(migratationItem[9]);
				$('#progressbar' + migratationItem[0]).progressbar({
					value : migratationItem[migratationItem.length - 1],
					showText : true
				});
				$("#progresst" + migratationItem[0]).text(
						migratationItem[migratationItem.length - 1] + "%");
			}
			$('.ui-widget-header').css("background-color", "#66CD00");

			var timerProcess = setTimeout(
					function() {
						RemoteManager
								.getAllMigrationDetails(DataMigrate.updateMigrationProgress);
					}, 1000);
			DataMigrate.timer.push(timerProcess);
		}

	},

	selectAllMigration : function(element) {

		var val = element.checked;
		for ( var i = 0; i < document.forms[0].elements.length; i++) {
			var e = document.forms[0].elements[i];
			if ((e.id != 'selectAll') && (e.type == 'checkbox')) {
				e.checked = val;
				DataMigrate.clickCheckBox(e.id);
			}
		}
	},

	checkSelectAllButtonEnableDisabled : function() {
		var isAllChecked = true;
		for ( var i = 0; i < document.forms[0].elements.length; i++) {
			var e = document.forms[0].elements[i];
			if ((e.id != 'selectAll') && (e.type == 'checkbox')) {
				if (e.checked == false) {
					isAllChecked = false;
					break;
				}
			}
		}
		if (isAllChecked)
			$("#selectAll").attr('checked', 'checked');
		else
			$("#selectAll").removeAttr('checked');
	},

	clickCheckBox : function(id) {

		var spanid = "span-" + id;
		var typeid = "migrationType" + id;
		if (document.getElementById(id).checked) {
			if (selectedMigrationProcess.indexOf(id) == -1)
				selectedMigrationProcess.push(id);
		} else {
			var index = selectedMigrationProcess.indexOf(id);
			selectedMigrationProcess.splice(index, 1);
		}

		if (selectedMigrationProcess.length > 0) {
			document.getElementById('datamigration.delete').disabled = false;
			
			if (selectedMigrationProcess.length == 1)
			{	
				
				var status = $('#span-' + selectedMigrationProcess[0]).text();
				var type = $('#migrationType' + selectedMigrationProcess[0]).text();
				if(status == 'Failed' || status == 'Stopped' || status == 'Completed') 
				{
					document.getElementById('datamigration.cancel').disabled = true;
					if(type == 'Amazon')
						document.getElementById('datamigration.resume').disabled = false;
					else
						document.getElementById('datamigration.resume').disabled = true;
				} 
				else 
				{
					document.getElementById('datamigration.resume').disabled = true;
					if(type == 'Local')
					{	
						document.getElementById('datamigration.cancel').disabled = true;
					}
					else
					{
						document.getElementById('datamigration.cancel').disabled = false;
					}
					if (status.indexOf("Migrated") != -1) 
					{
						var pattern = /[0-9]+/g;
						var Arr=status.match(pattern);				
						if(Arr[0] == Arr[1])				
							document.getElementById('datamigration.cancel').disabled = true;								
						else
							document.getElementById('datamigration.cancel').disabled = false;				
					}					
				}
			}
			else
			{
				document.getElementById('datamigration.resume').disabled = true;
				document.getElementById('datamigration.cancel').disabled = true;
			}	
		} 
		else 
		{
			document.getElementById('datamigration.resume').disabled = true;
			document.getElementById('datamigration.cancel').disabled = true;
			document.getElementById('datamigration.delete').disabled = true;
		}
		DataMigrate.checkSelectAllButtonEnableDisabled();
	},

	addDataMigrate : function(importType) {
		// 
		for ( var i = 0; i < DataMigrate.timer.length; i++) {
			clearTimeout(DataMigrate.timer[i]);
		}
		if (importType == "true") {
			Util.addLightbox("adddm", "resources/add_data_migration.html",
					null, null);
		} else {
			Util.addLightbox("adddm",
					"resources/add_data_migration_export.html", null, null);
		}

	},

	cancelDataMigrate : function() {
		console.log('cancel Data Migtare..!');
		RemoteManager.stopMigration(selectedMigrationProcess[0],DataMigrate.stopCallBack);
	},

	stopCallBack : function(status) {
		console.log('Call back..!',status);
		if(status == true)
		{
			jAlert("Data Migration process stopped successfully.",
			"Cancel Migration");
			Navbar.refreshView();
		}
		else
		{
			jAlert("Data Migration process could not be stopped.",
			"Cancel Migration");
			Navbar.refreshView();		
		}
	},
	
	closeBox : function(isRefresh) {
		DataMigrate.fileIsUnzipStatus = new Array();
		DataMigrate.fileSizeStatus = new Array();
		Util.removeLightbox("adddm");
		if (isRefresh) {
			Navbar.refreshView();
		}
	},

	cancelBox : function() {
		DataMigrate.fileIsUnzipStatus = new Array();
		DataMigrate.fileSizeStatus = new Array();
		Util.removeLightbox("adddm");
		var timerProcess = setTimeout(
				function() {
					RemoteManager
							.getAllMigrationDetails(DataMigrate.updateMigrationProgress)
				}, 1000);
		DataMigrate.timer.push(timerProcess);

	},

	migrateData : function(param) {
		var isImportType = false;
		if (param == "true") {
			isImportType = true;
		}
		var NodeId = $('#ClusterNameNodeId').val();
		var title = $('#title').val();
		var cloudStore = $('#cloudStore').val();
		var accessKey = $('#accessKey').val();
		var secureAccessKey = $('#secureAccessKey').val();
		var bucketName = $('#bucketName').val();
		var innerKey = $('#innerKey').val();
		var hdfsPath = $('#hdfsPath').val();
		var secure = document.getElementById('secure').checked;
		var compression = $('#compression').val();
		var encryption = $('#encryption').val();
		document.getElementById('msg_td_1').innerHTML = "";
		if (NodeId == "") {
			if (isImportType)
				document.getElementById('msg_td_1').innerHTML = "Destination NameNode was not selected. Please select a NameNode";
			else
				document.getElementById('msg_td_1').innerHTML = "Source NameNode was not selected. Please select a NameNode";
			return

		} else if (title == "") {
			document.getElementById('msg_td_1').innerHTML = "Title was not provided. Please provide a title to identify your data migration process.";
			return

			

		} else if (accessKey == "") {
			document.getElementById('msg_td_1').innerHTML = "Access key was not provided. Please provide access key for data migration.";
			return;
		} else if (secureAccessKey == "") {
			document.getElementById('msg_td_1').innerHTML = "Secret access key was not provided. Please provide secret access key for data migration.";
			return;
		} else if (bucketName == "") {
			document.getElementById('msg_td_1').innerHTML = "Bucket Name was not provided. Please provide bucket name for data migration.";
			return;
		}
		else if(Util.isContainSpecialCharButNotUnderscoreAndSlash(document.getElementById('hdfsPath').value))
		{
			jAlert("HDFS path cannot contain special characters.", "Incorrect Detail");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		
		var isExtract = false;
		if (isImportType) {
			isExtract = document.getElementById("isExtractAmazon").checked;
			document.getElementById('importAmazon').disabled = true;
		} else {
			$('#Export').attr("disabled", true);
		}

		// dwr call for save ddata migration detail
		RemoteManager.startS3Migration(NodeId, title, cloudStore, accessKey,
				secureAccessKey, bucketName, innerKey, hdfsPath, isImportType,
				secure, isExtract, compression, encryption,
				DataMigrate.dataMigrated);

	},

	dataMigrated : function(response) {
		jAlert(response.substring(2), 'Response');
		DataMigrate.closeBox(true);
		Navbar.isRefreshPage = true;
//		Navbar.changeTab('Data Migration', 'admin', 'data_migration');
	},

	showResumeDataMigrate : function() {

		var migrationType = $('#migrationType' + selectedMigrationProcess[0])
				.text();
		if ((migrationType.indexOf('ftp') != -1)
				|| (migrationType.indexOf('FTP') != -1)
				|| (migrationType.indexOf('Ftp') != -1)) {
			Util.addLightbox("adddm",
					"resources/resume_data_migration_ftp.html", null, null);
		} else if ((migrationType.indexOf('local') != -1)
				|| (migrationType.indexOf('LOCAL') != -1)
				|| (migrationType.indexOf('Local') != -1)) {
			jAlert("Resume data migration from Local is not possible.",
					"Information");
		} else {
			Util.addLightbox("adddm", "resources/resume_data_migration.html",
					null, null);
		}
	},
	resumeMigrateData : function(val) {
		var id = selectedMigrationProcess[0];
		var overwirte = document.getElementById('overwirte').checked;
		document.getElementById('msg_td_1').innerHTML = "";
		if (val == 1) // Amazon
		{
			var accessKey = $('#accessKey').val();
			var secureAccessKey = $('#secureAccessKey').val();
			if (accessKey == "") {
				document.getElementById('msg_td_1').innerHTML = "Access key was not provided.Please provide access key for data migration.";
				return;
			} else if (secureAccessKey == "") {
				document.getElementById('msg_td_1').innerHTML = "Secret access key was not provided. Please provide secret access key for data migration.";
				return;
			}

			RemoteManager.resumeS3Migration(id, accessKey, secureAccessKey,
					overwirte, DataMigrate.handleResumeMigrationResponse);
		} else if (val == 2) // FTP
		{
			var username = $('#username').val();
			var password = $('#password').val();
			if (username == "") {
				document.getElementById('msg_td_1').innerHTML = "Username was not provided.Please provide username for data migration.";
				return;
			}

			RemoteManager.resumeFTPMigration(id, username, password,
					overwirte, DataMigrate.handleResumeMigrationResponse);
		}
	},

	handleResumeMigrationResponse : function(resp) {
		if (resp) {
			jAlert("Migration resume successfully.", 'Success');
		} else {
			jAlert("Migration resume failed.", 'Failed');
		}
		DataMigrate.closeBox(true);
		Navbar.refreshView();
	},
	deleteMigration : function() {

		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton = ' No';
		jConfirm(
				'Are you sure you want to clear selected item(s) from this list?',
				'Delete', function(val) {
					if (val == true) {
						// for(var i=0;i<selectedMigrationProcess.length;i++)
						// {
						RemoteManager.deleteMigration(
								selectedMigrationProcess,
								DataMigrate.fileCleaned);
						// }

						// Navbar.isRefreshPage =true;
						// Navbar.changeTab('Data Migration','admin',
						// 'data_migration');
					} else
						return;
					jQuery.alerts.okButton = ' Ok ';
				});
	},

	fileCleaned : function(value) {
		DataMigrate.closeBox(true);
		if (value)
			jAlert("Data cleaned successfully", 'Delete');
		else
			jAlert("Data cleaning failed", 'Delete');
		// Navbar.refreshView();
	},

	dataStoreChanged : function() {

		var val = $('#cloudStore').val();

		if (val == 'FTP') {
			DataMigrate.clearFTPfields();
			$('#ftpStoreDiv').css('display', '');
			$('#localStoreDiv').css('display', 'none');
			$('#cloudStoreDiv').css('display', 'none');
			$('#HDFSDiv').css('display', 'none');
			$('#SSHDiv').css('display', 'none');
			$('#emailDiv').css('display', 'none');
			$('#httpDiv').css('display', 'none');
			$('#SFTPDiv').css('display', 'none');
			$('#DataBaseDiv').css('display', 'none');
			RemoteManager
					.getAllDataConnections(DataMigrate.fillFTPConnectionIds);
		} else if (val == 'Local') {
			$('#emailDiv').css('display', 'none');
			$('#ftpStoreDiv').css('display', 'none');
			$('#cloudStoreDiv').css('display', 'none');
			$('#localStoreDiv').css('display', '');
			$('#HDFSDiv').css('display', 'none');
			$('#httpDiv').css('display', 'none');
			$('#SSHDiv').css('display', 'none');
			$('#SFTPDiv').css('display', 'none');
			$('#DataBaseDiv').css('display', 'none');
		} else if (val == 'POP/IMAP') {
			DataMigrate.clearIMAPfields();
			$('#emailDiv').css('display', '');
			$('#ftpStoreDiv').css('display', 'none');
			$('#localStoreDiv').css('display', 'none');
			$('#cloudStoreDiv').css('display', 'none');
			$('#httpDiv').css('display', 'none');
			$('#HDFSDiv').css('display', 'none');
			$('#SSHDiv').css('display', 'none');
			$('#DataBaseDiv').css('display', 'none');
			$('#SFTPDiv').css('display', 'none');
			RemoteManager
					.getAllDataConnections(DataMigrate.fillIMAPConnectionIds);
		} else if (val == 'HTTP/HTTPS') {
			DataMigrate.clearHTTPfields();
			$('#httpDiv').css('display', '');
			$('#emailDiv').css('display', 'none');
			$('#ftpStoreDiv').css('display', 'none');
			$('#localStoreDiv').css('display', 'none');
			$('#cloudStoreDiv').css('display', 'none');
			$('#HDFSDiv').css('display', 'none');
			$('#SSHDiv').css('display', 'none');
			$('#DataBaseDiv').css('display', 'none');
			$('#SFTPDiv').css('display', 'none');
			RemoteManager
					.getAllDataConnections(DataMigrate.fillHTTPConnectionIds);
		} else if (val == 'HDFS') {
			DataMigrate.clearHDFSfields();
			$('#HDFSDiv').css('display', '');
			$('#httpDiv').css('display', 'none');
			$('#emailDiv').css('display', 'none');
			$('#ftpStoreDiv').css('display', 'none');
			$('#cloudStoreDiv').css('display', 'none');
			$('#localStoreDiv').css('display', 'none');
			$('#SSHDiv').css('display', 'none');
			$('#DataBaseDiv').css('display', 'none');
			$('#SFTPDiv').css('display', 'none');
			RemoteManager
					.getAllDataConnections(DataMigrate.fillHDFSConnectionIds);
		} else if (val == 'SSH') {
			DataMigrate.clearSSHfields();
			$('#HDFSDiv').css('display', 'none');
			$('#httpDiv').css('display', 'none');
			$('#emailDiv').css('display', 'none');
			$('#ftpStoreDiv').css('display', 'none');
			$('#cloudStoreDiv').css('display', 'none');
			$('#localStoreDiv').css('display', 'none');
			$('#SSHDiv').css('display', '');
			$('#DataBaseDiv').css('display', 'none');
			$('#SFTPDiv').css('display', 'none');
			RemoteManager
					.getAllDataConnections(DataMigrate.fillSSHConnectionIds);
		}

		else if (val == 'SFTP') {
			DataMigrate.clearSFTPfields();
			$('#HDFSDiv').css('display', 'none');
			$('#httpDiv').css('display', 'none');
			$('#emailDiv').css('display', 'none');
			$('#ftpStoreDiv').css('display', 'none');
			$('#cloudStoreDiv').css('display', 'none');
			$('#localStoreDiv').css('display', 'none');
			$('#SSHDiv').css('display', 'none');
			$('#DataBaseDiv').css('display', 'none');
			$('#SFTPDiv').css('display', '');
			RemoteManager
					.getAllDataConnections(DataMigrate.fillSFTPConnectionIds);
		} 
		else if(val == 'DB'){
			DataMigrate.clearDBfields();
			$('#HDFSDiv').css('display', 'none');
			$('#httpDiv').css('display', 'none');
			$('#emailDiv').css('display', 'none');
			$('#ftpStoreDiv').css('display', 'none');
			$('#cloudStoreDiv').css('display', 'none');
			$('#localStoreDiv').css('display', 'none');
			$('#SSHDiv').css('display', 'none');
			$('#SFTPDiv').css('display', 'none');
			$('#DataBaseDiv').css('display', '');
			RemoteManager
			.getAllDataConnections(DataMigrate.fillDBConnectionIds);
		}
		else {
			DataMigrate.clearS3fields();
			$("#bucketName").val("");
			$("#innerKey").val("");
			$('#httpDiv').css('display', 'none');
			$('#emailDiv').css('display', 'none');
			$('#localStoreDiv').css('display', 'none');
			$('#ftpStoreDiv').css('display', 'none');
			$('#HDFSDiv').css('display', 'none');
			$('#DataBaseDiv').css('display', 'none');
			$('#cloudStoreDiv').css('display', '');
			$('#SSHDiv').css('display', 'none');
			$('#SFTPDiv').css('display', 'none');
			RemoteManager
					.getAllDataConnections(DataMigrate.fillS3ConnectionIds);
		}

	},

	fillFTPConnectionIds : function(response) {
		var data = "";
		data += "<option value = 'manual'>--Manual--</option>";
		for ( var i = 0; i < response.length; i++) {
			var row = response[i];
			if (row.type == 0)
				data += "<option value = '" + row.id + "'>" + row.id
						+ "</option>";
		}
		$("#connectionIdFTP").html(data);
	},
	
	fillDBConnectionIds : function(response){
		var data = "";
		var firstrow="";
		var flag=0;
		
		for(var i=0;i<response.length;i++){
			var row = response[i];
			if(row.type == 9){
				if(flag==0)
				{
					flag = 1;
					firstrow=row.id;
				}
					data += "<option value = '" + row.id + "'>" + row.id
				+ "</option>";
			}
		}
		if (flag == 0)
		{	
			data += "<option value=\"0\">--Select--</option>";
			$('#DataBaseSubLink').css('display','none');
		}			
		$("#connectionIdDB").html(data);
		if(flag!=0)
			DataMigrate.dbConnectionChange(firstrow, firstrow);
	},
	
	showFromFilters : function(element , list){
		$('#searchFromFilters').fadeIn('slow');
		var closeImageStyle = ' style="float: left; height: 23px; width: 24px	;" '
		var data = "";
		data += '<table style="margin-top:-16px;"><tr><td colspan="2"><span id="selectColClose" class="divcloser"><a href="javascript:DataMigrate.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"' +closeImageStyle +'></a></span></td></tr>';
		for ( var i = 0; i < DataMigrate.list.length; i++) {
			var tbl_name = DataMigrate.list[i];
			if(!DataMigrate.tableFlag)
				DataMigrate.alltables.push(tbl_name);
			if (i == 0) {
				data += '<tr><td><input type="checkbox" name="nnID[]" id="filterBy'
						+ i
						+ '" value="'
						+ tbl_name
						+ '" onclick="DataMigrate.setLocationSearch(this.id);" > </td><td>' + tbl_name + '</td></tr>';
			} else {
				data += '<tr><td><input type="checkbox" name="nnID[]" id="filterBy'
						+ i
						+ '" value="'
						+ tbl_name
						+ '" onclick="DataMigrate.setLocationSearch(this.id);" > </td><td>' + tbl_name + '</td></tr>';
			}
		}
		data += '</table>';
		$('#searchFromFilters').html(data);
		if(DataMigrate.tablenames!=null && DataMigrate.tablenames.length!=0){
			var length = DataMigrate.tablenames.length;
			for(var i=0;i<length;i++){
				var index = DataMigrate.list.indexOf(DataMigrate.tablenames[i]);
				document.getElementById("filterBy"+index).checked = true;
			}
		}
	},

	closeSelectionDiv : function() {
		$("#searchFromFilters").hide();
	},
	
	fillSFTPConnectionIds : function(response) {
		var data = "";
		data += "<option value = 'manual'>--Manual--</option>";
		for ( var i = 0; i < response.length; i++) {
			var row = response[i];
			if (row.type == 6)
				data += "<option value = '" + row.id + "'>" + row.id
						+ "</option>";
		}
		$("#connectionIdSFTP").html(data);
	},

	fillS3ConnectionIds : function(response) {
		var data = "";
		data += "<option value = 'manual'>--Manual--</option>";
		for ( var i = 0; i < response.length; i++) {
			var row = response[i];
			if (row.type == 1)
				data += "<option value = '" + row.id + "'>" + row.id
						+ "</option>";
		}
		$("#connectionIdS3").html(data);
	},

	fillIMAPConnectionIds : function(response) {
		var data = "";
		var flag = 0;

		for ( var i = 0; i < response.length; i++) {
			var row = response[i];
			if (row.type == 3) {
				flag = 1;
				data += "<option value = '" + row.id + "'>" + row.id
						+ "</option>";
			}
		}
		if (flag == 0)
			data += "<option value=\"0\">--Select--</option>";
		$("#connectionIdEmail").html(data);
		DataMigrate.getAvailableFolders();
	},

	fillHTTPConnectionIds : function(response) {
		var data = "";
		data += "<option value = 'manual'>--Manual--</option>";
		for ( var i = 0; i < response.length; i++) {
			var row = response[i];
			if (row.type == 2)
				data += "<option value = '" + row.id + "'>" + row.id
						+ "</option>";
		}
		$("#connectionIdHttp").html(data);
	},

	fillHDFSConnectionIds : function(response) {
		var data = "";
		data += "<option value = 'manual'>--Manual--</option>";
		for ( var i = 0; i < response.length; i++) {
			var row = response[i];
			if (row.type == 4)
				data += "<option value = '" + row.id + "'>" + row.id
						+ "</option>";
		}
		$("#connectionIdHDFS").html(data);
	},

	fillSSHConnectionIds : function(response) {
		var data = "";
		data += "<option value = 'manual'>--Manual--</option>";
		for ( var i = 0; i < response.length; i++) {
			var row = response[i];
			if (row.type == 5)
				data += "<option value = '" + row.id + "'>" + row.id
						+ "</option>";
		}
		$("#connectionIdSSH").html(data);
	},

	ftpConnectionChange : function(id, value) {
		if (value == "manual")
		{
			DataMigrate.clearFTPfields();
			DataMigrate.showhide('ftpStoreSubDiv','ftpStoreSubLink');
		}
		else
		{
			RemoteManager.getFTPDataSource(value,
					DataMigrate.fillFTPConnectionDetails);
			DataMigrate.showhide('ftpStoreSubLink','ftpStoreSubDiv');
		}
	},

	sftpConnectionChange : function(id, value) {
		if (value == "manual")
		{
			DataMigrate.clearSFTPfields();
			DataMigrate.showhide('SFTPSubDiv','SFTPSubLink');
		}
		else
		{
			RemoteManager.getSFTPDataSource(value,
					DataMigrate.fillSFTPConnectionDetails);
			DataMigrate.showhide('SFTPSubLink','SFTPSubDiv');
		}
	},

	s3ConnectionChange : function(id, value) {
		if (value == "manual")
		{
			DataMigrate.clearS3fields();
			DataMigrate.showhide('cloudStoreSubDiv','cloudStoreSubLink');
		}		
		else
		{
			RemoteManager.getS3DataSource(value,DataMigrate.fillS3ConnectionDetails);
			DataMigrate.showhide('cloudStoreSubLink','cloudStoreSubDiv');
		}
	},

	httpConnectionChange : function(id, value) {
		if (value == "manual")
		{
			DataMigrate.clearHTTPfields();
			DataMigrate.showhide('httpSubDiv','httpSubLink');
		}
		else
		{
			RemoteManager.getHTTPDataSource(value,
					DataMigrate.fillHTTPConnectionDetails);
			DataMigrate.showhide('httpSubLink','httpSubDiv');
		}
	},

	hdfsConnectionChange : function(id, value) {
		if (value == "manual")
		{
			DataMigrate.clearHDFSfields();
			DataMigrate.showhide('HDFSSubDiv','HDFSSubLink');
		}
		else
		{
			RemoteManager.getHDFSDataSource(value,
					DataMigrate.fillHDFSConnectionDetails);
			DataMigrate.showhide('HDFSSubLink','HDFSSubDiv');
		}
	},

	sshConnectionChange : function(id, value) {
		if (value == "manual")
		{
			DataMigrate.clearSSHfields();
			DataMigrate.showhide('SSHSubDiv','SSHSubLink');	
		}
		else
		{
			RemoteManager.getSSHDataSource(value,
					DataMigrate.fillSSHConnectionDetails); // Make this
			DataMigrate.showhide('SSHSubLink','SSHSubDiv');
		}
															// function
	},

	dbConnectionChange : function(id, value){
		if (value == "select")
			DataMigrate.clearDBfields();
		else
			RemoteManager.getDataSource(value , DataMigrate.fillDBConnectionDetails);
	},
	fillDBConnectionDetails : function(response){
		
		$("#DBDriverClassMig").val(response.driver);
		$("#DBConnectionURLMig").val(response.connectionURL);
		$("#DataBaseUserMig").val(response.userName);
		$("#DataBasePassMig").val(response.password);
		DataMigrate.dbdriverjar = response.jarFileName;
		DataMigrate.maxConn = response.maxConnections; 
		DataMigrate.maxIdleConn = response.maxIdleConnections; 
		DataMigrate.waitTime = response.waitTimeMilliSeconds;
		DataMigrate.list = null;
		DataMigrate.alltables = null;
		DataMigrate.list = new Array();
		DataMigrate.alltables = new Array();
		var length = response.tableNames.length;
		for(var i=0;i<length;i++){
			var table_name = response.tableNames[i];
			DataMigrate.list[i] = table_name;
			DataMigrate.alltables.push(table_name);
		}
	},
	fillFTPConnectionDetails : function(response) {
		$("#hostname").val(response.host);
		$("#portnumber").val(response.port);
		$("#username").val(response.username);
		$("#password").val(response.password);
	},

	fillSFTPConnectionDetails : function(response) {
		$("#SFTPhost").val(response.host);
		$("#SFTPport").val(response.port);
		$("#SFTPuser").val(response.username);
		$("#SFTPpass").val(response.password);
	},

	fillS3ConnectionDetails : function(response) {
		$("#accessKey").val(response.accessKey);
		$("#secureAccessKey").val(response.secretAccessKey);
	},

	fillHTTPConnectionDetails : function(response) {
		$('#httpBaseURL').val(response.baseURL);
		$('#httpUser').val(response.userName);
		$('#httpPass').val(response.password);
	},

	fillHDFSConnectionDetails : function(response) {
		$("#HDFShost").val(response.host);
		$("#HDFSport").val(response.port);
		$("#HDFSuser").val(response.username);
		$("#HDFSgroup").val(response.group);
		
	},

	fillSSHConnectionDetails : function(response) {
		$("#SSHhost").val(response.host);
		$("#SSHport").val(response.port);
		$("#SSHuser").val(response.username);
		$("#SSHpass").val(response.password);
		$("#SSHkey").val(response.key);
		if($("#SSHpass").val()=="")
		{
			$("#authMethod").val("SSHk");
			$('#SSHpr').css('display','none');
			$('#SSHkr').css('display','');		
		}
		else
		{	
			$("#authMethod").val("SSHp");
			$('#SSHpr').css('display','');
			$('#SSHkr').css('display','none');
		}
	},

	clearFTPfields : function() {
		$("#hostname").val("");
		$("#portnumber").val("");
		$("#username").val("");
		$("#password").val("");
	},

	clearSFTPfields : function() {
		$("#SFTPhost").val("");
		$("#SFTPport").val("");
		$("#SFTPuser").val("");
		$("#SFTPpass").val("");
		$("#SFTPpath").val("/");
	},

	clearDBfields : function() {
		$("#DBDriverClass").val("");
		$("#DBConnectionURL").val("");
		$("#DataBaseUser").val("");
		$("#DataBasePass").val("");
		
	},
	
	clearS3fields : function() {
		$("#accessKey").val("");
		$("#secureAccessKey").val("");
	},

	clearHTTPfields : function() {
		$('#httpBaseURL').val("http://");
		$('#httpUser').val("");
		$('#httpPass').val("");
		$('#fileFolderHttp').val("");
		$('#parsePatternHttp').val("");
	},

	clearIMAPfields : function() {
		$("#startDateEmail").val("");
		$("#endDateEmail").val("");
	},

	clearHDFSfields : function() {
		$("#HDFShost").val("hdfs://");
		$("#HDFSport").val("");
		$("#HDFSuser").val("");
		$("#HDFSgroup").val("");		
		$('#HDFSroot').val("");
	},

	clearSSHfields : function() {
		$("#SSHhost").val("");
		$("#SSHport").val("");
		$("#SSHuser").val("");
		$("#SSHpass").val("");
		$("#SSHkey").val("");
		$('#SSHroot').val("");
	},

	addNewDataConnection : function() {
		DataMigrate.currentDataStore = $("#cloudStore").val();
		Util.addLightbox("conn", "resources/addConnectionDataMigration.html",
				null, null);
	},
	
	defineDataBaseSchema : function(){
		Util.addLightbox("schema", "resources/addSchemaDefinitionDataMigration.html",
				null, null);
	},
	checkSelectCase : function(id){
		
		$("#next").css('display','');
		if(id=='auto'){
			$("#SQLDiv").hide();
			$("#tbName").hide();
			$("#autoDetect").css('display','');
		}else if(id=='sql'){
			$("#autoDetect").css('display','none');
			$("#SQLDiv").show();
			$("#tbName").show();
		}
	},
	uploadSQLFile : function(){
		var radioFlag =  document.getElementById("auto").checked;
		if($("#deli").val() == ""){
			jAlert("You must specify the delimiter.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		
		if(radioFlag){
			
			if($("#fileTagSample").val() == ""){
				jAlert("You must specify the sample file to detect table structure.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			
			var form = document.getElementById("CommonFormUpload");
			var flag = false;
			var header = document.getElementById("header").checked;
			var flag = document.getElementById("auto").checked;
			var iframe = document.createElement("iframe");
	        iframe.setAttribute("id", "upload_iframe");
	        iframe.setAttribute("name", "upload_iframe");
	        iframe.setAttribute("width", "0");
	        iframe.setAttribute("height", "0");
	        iframe.setAttribute("border", "0");
	        iframe.setAttribute("style", "width: 0; height: 0; border: none;");
	        var div_id = 'formDIV';
	        // Add to document...
	        form.parentNode.appendChild(iframe);
	        window.frames['upload_iframe'].name = "upload_iframe";
//	        $("#addEditTable").hide();
	        form.setAttribute("target", "upload_iframe");
	        form.setAttribute("action", "SchemaDetection");
	        form.setAttribute("method", "post");
	        form.setAttribute("enctype", "multipart/form-data");
	        form.setAttribute("encoding", "multipart/form-data");
	        form.submit();
	        if(flag)
	        	var id = document.getElementById('fileTagSample').value;
	        else
	        	var id = document.getElementById('fileTag').value;
	    	id = id.substring(id.lastIndexOf("\\")+1 , id.length);
	        $("#parser").text(id);
	    	
	        iframeId = document.getElementById("upload_iframe");
	        var eventHandler = function (){
	        		var content = new Object();
	                if (iframeId.detachEvent)
	                	iframeId.detachEvent("onload", eventHandler);
	                else 
	                	iframeId.removeEventListener("load", eventHandler, false);
	                // Message from server...
	                if (iframeId.contentDocument) {
	                    content = iframeId.contentDocument.body.textContent;
	                } else if (iframeId.contentWindow) {
	                    content = iframeId.contentWindow.document.body.textContent;
	                } else if (iframeId.document) {
	                    content = iframeId.document.body.textContent;
	                }
	                content=JSON.parse(content);
	                $('#respProcessing').css('display','none');
	                $('#respMessage').text(content.message);
	                if(content[0].success == 'File uploaded successfully.'){
	                	DataMigrate.responseJson = content;
	                	var isHeader = document.getElementById('header').checked;
	        			var delimiter = document.getElementById('deli').value;
	        			var separator = document.getElementById('separator').value;
	        			
	        			DataMigrate.responseJson[0].header = isHeader;
	        			DataMigrate.responseJson[0].separator = separator;
	        			DataMigrate.responseJson[0].delimiter = delimiter;
	                	DataMigrate.toggleDiv(1 , 2 , header);
	                }else{
	                	$("#failed").css('display' , '');
	                }
	                flag = true;
	    	}
	        if(!flag){
//	        	$('#respProcessing').css('display','none');
//	        	$("#respMessage").text('Jar uploading failed.');
//	        	$('#respStatus').text('Failed');
//	        	$('#respFail').css('display','');
	        }
//	        $('#okpopup').removeAttr('disabled');
//	        var val = document.getElementById("connectionId").value;
	        if (iframeId.addEventListener) iframeId.addEventListener("load", eventHandler, true);
	        if (iframeId.attachEvent) iframeId.attachEvent("onload", eventHandler);
	        // Set properties of form...
	        form.setAttribute("target", "upload_iframe");
	        form.setAttribute("action", "SchemaDetection");
	        form.setAttribute("method", "post");
	        form.setAttribute("enctype", "multipart/form-data");
	        form.setAttribute("encoding", "multipart/form-data");
	        form.submit();
	   }else{

		   if($("#dbTableName").val() == ""){
				jAlert("You must specify the table name.","Insufficient Details");
				$("#popup_container").css("z-index","99999999");
				return;
			}
		   DataMigrate.createJqgrid();
		   DataMigrate.toggleDiv(1 , 2);
	   }
		
		
	},
	createJqgrid : function()
	{
		$("schemaTable").show();
		$('#columnListGrid').remove();
		$('#schemaTable').html('<table id="columnListGrid" class="dataTable"></table>');
		var columnNames = new Array();
		var columnTypes = new Array();
		var columnData = new Array();
		var header = '<thead>	<tr style="height: 25px;"><th style="text-align: center;">Column Name </th>	<th style="text-align: center;">Column Type</th>';
		header+='<th style="text-align: center;">Column Index</th>';
		header+='<th style="text-align: center;"></th></tr>	</thead>';
		$('#columnListGrid').append(header);DataMigrate.count = 0;
		DataMigrate.plusClicked();
	},
	
	plusClicked : function(){
		DataMigrate.maxCount = DataMigrate.maxCount + 1; 
		DataMigrate.count = DataMigrate.count+1;
		j = DataMigrate.count ; 
		var tbl_data  = '<tr id = "row_'+j+'">';
		var selected = '<select id="dataType_'+j+'" name="datatype" style="text-align:center;width: auto;" > <option id="Integer" value="INTEGER">INTEGER</option> <option id="VarChar_'+0+'" value="VARCHAR(128)">VARCHAR(128)</option> <option id="DATETIME"  value="TIMESTAMP">TIMESTAMP</option> <option id="boolean" value="BOOLEAN">BOOLEAN</option> <option id="decimal" value="DECIMAL">DECIMAL</option>	</select>';
		var name = '<input type="text" id="colName_'+j+'">';
		var index = '<input type="text" id="colindex_'+j+'">';
		var opt = '<a href="javascript:DataMigrate.plusClicked('+');"> <img alt="Add Volume" src="images/plus_sign_brown.png" id="plusImage_'+j+'" style="height: 15px;"></a><a href="javascript:DataMigrate.minusClicked('+j+');" style="color: white;"> <img alt="Remove Volume" src="images/minus_sign_brown.png" id="minusImage_'+j+'" style="height: 10px; width: 20px;"></a>';
		tbl_data = tbl_data + '<td>'+  name + '</td>';
		tbl_data = tbl_data + '<td>'+  selected + '</td>';
		tbl_data = tbl_data + '<td>'+  index + '</td>';
		tbl_data = tbl_data + '<td>'+  opt + '</td>' + '</tr>';

		$('#columnListGrid').append(tbl_data);
		
		$("#dataType_"+j).combify();
		
	},
	minusClicked : function(j){
		if(DataMigrate.count==1){
			jAlert("No more fields to remove","Invalid action");
	          $("#popup_container").css("z-index","999999999");
	          return ;
		}
		$("#row_"+j).remove();
		DataMigrate.count = DataMigrate.count-1;  
	},
	
	
	fillColumnsByJson : function(list)
	{
		if(list!=null)
		{
			var fields = jQuery.parseJSON(list);
			for (var i = 0; i < fields.length; i++)
			{
				var columnObject = fields[i];
				jQuery("#columnListGrid").jqGrid('addRowData',i+1,({
					
					colName : columnObject.colName,
					colType : columnObject.colType,
					colIndex : columnObject.colIndex,
				}));
			}
			
			$("#columnListGrid").jqGrid().setGridParam({sortorder: "asc", sortname: "colIndex"}).trigger("reloadGrid");
		}
	},
	
	toggleDiv : function(to , from , header ){
		var flag = document.getElementById('auto').checked; 
		if(from==2 && to==1){
			$("#dataConnectionCommon").css('display' , 'none');
			$("#autoDetect").css('display' , 'none');
			$("#tableForButtons").css('display' , 'none');
			$("#SQLDiv").css('display' , 'none');
			$("#button_tables").css('display' , '');
			$("#schemaTable").css('display' , '');
			if(flag){
				DataMigrate.createDataTables(header);
			}else{
				DataMigrate.createJqgrid();
			}
			
			
		}else if(from==1 && to==2){
			$("#button_tables").css('display' , 'none');
			$("#schemaTable").css('display' , 'none');
			$("#dataConnectionCommon").css('display' , '');
			if(flag){
				$("#autoDetect").css('display' , '');
			}else{
				$("#SQLDiv").css('display' , 'none');
			}
			$("#tableForButtons").css('display' , '');
		}
	},
	includeClicked : function(id)
	{
		var flag = document.getElementById(id).checked;
		id = id.toString();
		var index = id.indexOf('_');
		id = id.substring(index+1 , id.length);
		if(!flag)
		{
			$('#dataType_'+id).attr('disabled','disabled');
			$('#header_'+id).attr('disabled','disabled');
		}
		else
		{
			$('#dataType_'+id).removeAttr('disabled');
			$('#header_'+id).removeAttr('disabled');
		}
	},
	createDataTables : function(header){
		
		$("schemaTable").show();
		$('#sample_table').remove();
		$('#schemaTable').html('<table id="sample_table" class="dataTable"></table>');
		var json = DataMigrate.responseJson;
		var headerFields = json[0]['meta']['header'];
		var count=1;
		var columnNames = new Array();
		var columnTypes = new Array();
		var columnData = new Array();
		
		if(json[0]['meta'] != null || json[0]['meta'] != undefined){
			for(var key in json[0]['meta']['details'])
			{
				var value1 = json[0]['meta']['details'][key];
				columnTypes.push(value1['type']);
				count++;
			}
			
			
			var colList = [];
			var tableRow =[];
			count--;
			if(header)
			{
				for(var key in json[0]['meta']['details'])
				{
					var value = json[0]['meta']['header'][key];
					columnNames.push(value);
				}
			}
			else
			{
				for(var i=1;i<=count;i++)
					columnNames.push('Column'+i);
			}
			
			DataMigrate.selectedColumnNames = columnNames;

			for(var i=1; i< columnNames.length; i++)
			{
				var chk = '<input type="checkbox" name="include" id="include_'+i+'" checked="checked" onclick="javascript:DataMigrate.includeClicked(this.id);">';
				var col = '<input type="text" id = "header_'+i+'" value="'+columnNames[i]+'" style="width: 100px;"  onkeypress="javascript:Util.blockSpecialCharButNotUnderScore(window.event,this);"  ><span id="sub_error_span" style="display: none;color:red; "></span>';
				var title = '<div style="width: 135px;">'  +   col  + '</div>';
				colList.push({ "sTitle": title, "bSortable": false });
			}
			
			counter = count;
			var dataLength = json[0]['meta']['data'].length;
			for(var i=-1;i<dataLength;i++)
			{
				var rowData = [];
				/*if(i==-2)
				{
					for(var j=0;j<count;j++)
					{
						rowData.push('<input type="checkbox" name="include" id="include_'+j+'" checked="checked" onclick="javascript:AHQ.includeClicked(this.id);"> include ');
					}
					tableRow.push(rowData);
					continue;
				}
				else */if(i==-1)
				{
					for(var j=1;j<count;j++)
					{
						var selected = '<select id="dataType_'+j+'" name="datatype" style="text-align:center;width: auto;" > <option id="Integer" value="INTEGER">INTEGER</option> <option id="VarChar_'+j+'" value="VARCHAR(128)">VARCHAR(128)</option> <option id="DATETIME"  value="DATETIME">DATETIME</option> <option id="boolean" value="BOOLEAN">BOOLEAN</option> <option id="decimal" value="DECIMAL">DECIMAL</option>	</select>';
						rowData.push(selected);
					}
					tableRow.push(rowData);
					continue;
				}
				else
				{
					for(var k=1;k<count;k++)
					{
						rowData.push(json[0]['meta']['data'][i][k]);
					}
				}
				
				tableRow.push(rowData);
			}
			
			$('#sample_table').dataTable( {
		        "bPaginate": false,
				"bLengthChange": false,
				"bFilter": false,
				"bDestroy": true,
				"bInfo": false,
				"bAutoWidth": false,
				"aoColumns": colList,
				"aaData": tableRow
		    });

			for(var i=0;i<count;i++)
			{
				var type = columnTypes[i];
				var index = type.indexOf('VARCHAR');
				if(index!=-1)
				{
					var data1 = "<option value=" + type + ">" + type + "</option>";
					$('#dataType_' + i).append(data1);
				}
				$('#dataType_'+i).val(type);
			}
			
			$('#sample_table').ready(function(){
				for(var i=0;i<count;i++)
					$("#dataType_"+i).combify();
			});
			
		}
		else
		{
			jAlert("Your file can't be parsed.An Error occured.","Invalid File Details");
	        $("#popup_container").css("z-index","9999999");
	        return;
		}

		
	},
	fillUpResponseJSON : function(){
		var json = new Object();
		var details = new Array();
		var header = new Object();
		var kv = new Object();
//		kv["index"] = 0;kv["type"]= "VARCHAR(1280)";header["0"] = "FILEPATH";
//		details.push(kv);
		json["name"] = $("#dbTableName").val();
		json["success"] = "File uploaded successfully.";
		json["header"] = document.getElementById("header").checked;
		json["separator"] = $("#separator").val();
		json["delimiter"] = $("#deli").val();
		for(var i=1;i<DataMigrate.maxCount;i++){
			kv = new Object();
			if($("#colindex_"+i).val()!= null && $("#colindex_"+i).val() != undefined){
//				kv["index"] = $("#colindex_"+i).val();
				kv["index"] = (i - 1);
				kv["type"] = $("#dataType_"+i).val();
				details.push(kv);
				header[i - 1] = $("#colName_"+i).val();
			}
		}
		var meta = new Object();
		meta["details"] = details;
		meta["header"]=  header;json["meta"] = meta;
		var arr = new Array();arr.push(json);
		DataMigrate.responseJson = arr;
	},
	closeSchemaBox : function(flag){
		if(!flag){
			DataMigrate.responseJson = null;
			document.getElementById("exportDB").disabled = true;
		}else{
			if(document.getElementById("sql").checked == true){
				DataMigrate.fillUpResponseJSON();
			}
			document.getElementById("exportDB").disabled = false;
		}
		Util.removeLightbox("schema");
	},
	getAvailableFolders : function()
	{
		var connection = $("#connectionIdEmail").val();
		
		if(connection != "0")
		{
			$("#processingFolders").show();
			RemoteManager.getAllAvailableEmailFolders(connection, DataMigrate.setFolders);
		}
	},
	
	setFolders : function(response)
	{
		if(response.error != null)
		{
			$("#emailFolder").html("");
			$("#emailFolder").html("<option value='0'>--No folder--</option>");
			$("#processingFolders").hide();
			$("#msg_td_2").css('color', 'red');
			document.getElementById('msg_td_2').innerHTML = response.error;
			$("#startDateEmail").css("background", "white");
			$("#endDateEmail").css("background", "white");
			
			return;
		}
			
		var data = "";
		$("#emailFolder").html("");
		for(var i=0; i<response.folderList.length; i++)
			data += "<option id = '" + response.folderList[i] + "_folder' value = '" + response.folderList[i] + "'>" + response.folderList[i] + "</option>";
		
		$("#emailFolder").html(data);
		$("#processingFolders").hide();
		
		var protocol = response.protocol;
		if(protocol == "POP3")
		{
			document.getElementById('startDateEmail').disabled = true;
			document.getElementById('endDateEmail').disabled = true;
			$("#startDateEmail").css("background", "lightgrey");
			$("#endDateEmail").css("background", "lightgrey");
			$("#msg_td_2").css('color', 'blue');
			document.getElementById('msg_td_2').innerHTML = "POP3 does not support email fetch bounded by Start Date and End Date.";
		}
		else
		{
			document.getElementById('startDateEmail').disabled = false;
			document.getElementById('endDateEmail').disabled = false;
			$("#startDateEmail").css("background", "white");
			$("#endDateEmail").css("background", "white");
			document.getElementById('msg_td_2').innerHTML = "";
		}
	},
	
	//migrating db data into hdfs

	
	migrateFTPData : function(param) {

		var isImportType = false;
		if (param == "true") {
			isImportType = true;
		}

		var title = $('#title').val();
		var NodeId = $('#ClusterNameNodeId').val();

		var cloudStore = $('#cloudStore').val();
		// var hostname = $("#hostname option:selected").text(); ;
		var hostname = $("#hostname").val();
		var portnumber = $("#portnumber").val();
		var username = $('#username').val();
		var password = $('#password').val();
		var sourcePath = $('#sourcePath').val();
		var hdfsPath = $('#hdfsPath').val();
		var compression = $('#compression').val();
		var encryption = $('#encryption').val();

		document.getElementById('msg_td_1').innerHTML = "";
		if (title == "") {
			document.getElementById('msg_td_1').innerHTML = "Title was not provided. Please provide a title to identify your data migration process.";
			return;
		} else if (NodeId == "") {
			if (isImportType)
				document.getElementById('msg_td_1').innerHTML = "Destination NameNode was not selected. Please select a NameNode";
			else
				document.getElementById('msg_td_1').innerHTML = "Source NameNode was not selected. Please select a NameNode";
			return;
		} else if (hostname == "") {
			document.getElementById('msg_td_1').innerHTML = "FTP Host was not provided.Please provide FTP host for data migration.";
			return;
		} else if (portnumber == "") {
			document.getElementById('msg_td_1').innerHTML = "FTP Port number was not provided.Please provide FTP port number for data migration.";
			return;
		} else if (!Util.isNumeric(portnumber)) {
			document.getElementById('msg_td_1').innerHTML = "FTP Port must be a numeric value.";
			return;
		} else if (username == "") {
			document.getElementById('msg_td_1').innerHTML = "User name was not provided. Please provide user name for data migration.";
			return;
		}
		else if(Util.isContainSpecialCharButNotUnderscoreAndSlash(document.getElementById('hdfsPath').value))
		{
			jAlert("HDFS path cannot contain special characters.", "Incorrect Detail");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		// else if(password==""){
		// document.getElementById('msg_td_1').innerHTML="Password was not
		// provided. Please provide password for data migration.";
		// return;
		// }
		else if (sourcePath == "") {
			if (isImportType)
				document.getElementById('msg_td_1').innerHTML = "Source path was not provided. Please provide source path for data migration.";
			else
				document.getElementById('msg_td_1').innerHTML = "Destination path was not provided. Please provide destination path for data migration.";
			return;
		} else if (hdfsPath == "") {
			document.getElementById('msg_td_1').innerHTML = "HDFS path was not provided. Please provide hdfs path for data migration.";
			return;
		}
		hostname += ":" + portnumber;

		var isExtract = false;
		if (isImportType) {
			isExtract = document.getElementById("isExtractFTP").checked;
			document.getElementById('importFTP').disabled = true;
		} else {
			$('#Export').attr("disabled", true);
		}
		RemoteManager.startFTPMigration(NodeId, title, cloudStore, hostname,
				username, password, hdfsPath, sourcePath, isImportType,
				isExtract, compression, encryption, DataMigrate.dataMigrated);
	},

	// SFTP Import/Export
	migrateSFTPData : function(param) {

		var isImportType = false;
		if (param == "true") {
			isImportType = true;
		}

		var title = $('#title').val();
		var NodeId = $('#ClusterNameNodeId').val();
		var hdfsPath = $('#hdfsPath').val();
		var compression = $('#compression').val();
		var encryption = $('#encryption').val();

		var cloudStore = $('#cloudStore').val();
		var hostname = $("#SFTPhost").val();
		var portnumber = $("#SFTPport").val();
		var username = $('#SFTPuser').val();
		var password = $('#SFTPpass').val();
		var sourcePath = $('#SFTPpath').val();

		document.getElementById('msg_td_1').innerHTML = "";
		if (title == "") {
			document.getElementById('msg_td_1').innerHTML = "Title was not provided. Please provide a title to identify your data migration process.";
			return;
		} else if (NodeId == "") {
			if (isImportType)
				document.getElementById('msg_td_1').innerHTML = "Destination NameNode was not selected. Please select a NameNode";
			else
				document.getElementById('msg_td_1').innerHTML = "Source NameNode was not selected. Please select a NameNode";
			return;
		} else if (hostname == "") {
			document.getElementById('msg_td_1').innerHTML = "FTP Host was not provided.Please provide FTP host for data migration.";
			return;
		} else if (portnumber == "") {
			document.getElementById('msg_td_1').innerHTML = "FTP Port number was not provided.Please provide FTP port number for data migration.";
			return;
		} else if (!Util.isNumeric(portnumber)) {
			document.getElementById('msg_td_1').innerHTML = "FTP Port must be a numeric value.";
			return;
		} else if (username == "") {
			document.getElementById('msg_td_1').innerHTML = "User name was not provided. Please provide user name for data migration.";
			return;
		}
		// else if(password==""){
		// document.getElementById('msg_td_1').innerHTML="Password was not
		// provided. Please provide password for data migration.";
		// return;
		// }
		else if (sourcePath == "") {
			if (isImportType)
				document.getElementById('msg_td_1').innerHTML = "Source path was not provided. Please provide source path for data migration.";
			else
				document.getElementById('msg_td_1').innerHTML = "Destination path was not provided. Please provide destination path for data migration.";
			return;
		} else if (hdfsPath == "") {
			document.getElementById('msg_td_1').innerHTML = "HDFS path was not provided. Please provide hdfs path for data migration.";
			return;
		}
		else if(Util.isContainSpecialCharButNotUnderscoreAndSlash(document.getElementById('hdfsPath').value))
		{
			jAlert("HDFS path cannot contain special characters.", "Incorrect Detail");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		hostname += ":" + portnumber;

		var isExtract = false;
		if (isImportType) {
			isExtract = document.getElementById("isExtractSFTP").checked;
			document.getElementById('importSFTP').disabled = true;
		} else {
			$('#Export').attr("disabled", true);
		}
		
		RemoteManager.startSFTPMigration(NodeId, title, cloudStore, hostname, username, password, hdfsPath, sourcePath, isImportType, isExtract, compression, encryption, DataMigrate.dataMigrated);		
	},

	migrateDBData : function(param){
		var isImportType = false;
		if (param == "true") {
			isImportType = true;
		}
		
		var title = $('#title').val();
		var NodeId = $('#ClusterNameNodeId').val();
		var hdfsPath = $('#hdfsPath').val();
		var compression = $('#compression').val();
		var encryption = $('#encryption').val();

		var cloudStore = $('#cloudStore').val();
		var driverClass = $("#DBDriverClassMig").val();
		var connectionUrl = $("#DBConnectionURLMig").val();
		var username = $('#DataBaseUserMig').val();
		var password = $('#DataBasePassMig').val();
		document.getElementById('msg_td_1').innerHTML = "";
		if (NodeId == "") {
			if (isImportType)
				document.getElementById('msg_td_1').innerHTML = "Destination NameNode was not selected. Please select a NameNode";
			else
				document.getElementById('msg_td_1').innerHTML = "Source NameNode was not selected. Please select a NameNode";
			return;

		} else if (title == "") {
			document.getElementById('msg_td_1').innerHTML = "Title was not provided. Please provide a title to identify your data migration process.";
			return;
		} else if (hdfsPath == "") {
			document.getElementById('msg_td_1').innerHTML = "HDFS path was not provided. Please provide a HDFS path to migrate your data .";
			return;
		}
		else if(Util.isContainSpecialCharButNotUnderscoreAndSlash(document.getElementById('hdfsPath').value))
		{
			jAlert("HDFS path cannot contain special characters.", "Incorrect Detail");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		else if (driverClass == "") {
			document.getElementById('msg_td_1').innerHTML = "JDBC Driver class was not provided. Please provide a JDBC Driver class.";
			return;
		} else if (connectionUrl == "") {
			document.getElementById('msg_td_1').innerHTML = "Connection url was not provided. Please provide a connection url to identify your data migration process.";
			return;
		} else if (username == "") {
			document.getElementById('msg_td_1').innerHTML = "Username was not provided. Please provide username.";
			return;
		} else if (password == "") {
			document.getElementById('msg_td_1').innerHTML = "Password was not provided. Please provide password.";
			return;
		}else if (isImportType && (DataMigrate.list == null || DataMigrate.list.length==0)) {
			document.getElementById('msg_td_1').innerHTML = "No Table found to import in HDFS. Please add correct database credentials.";
			return;
		} 
		if(isImportType)
			var flag = document.getElementById('DataBaseImportAll').checked;	
		
		if(flag && isImportType)
			RemoteManager.startDataBaseMigration(NodeId, title, cloudStore, driverClass, connectionUrl, username, password, hdfsPath, DataMigrate.alltables,DataMigrate.dbdriverjar, compression, encryption, isImportType, DataMigrate.maxConn , DataMigrate.maxIdleConn, DataMigrate.waitTime, DataMigrate.dataMigrated);
		else if(isImportType)
			RemoteManager.startDataBaseMigration(NodeId, title, cloudStore, driverClass, connectionUrl, username, password, hdfsPath, DataMigrate.tablenames,DataMigrate.dbdriverjar, compression, encryption, isImportType,DataMigrate.maxConn , DataMigrate.maxIdleConn, DataMigrate.waitTime, DataMigrate.dataMigrated);
		else if(!isImportType){
			
			var resJson = JSON.stringify(DataMigrate.responseJson);
			RemoteManager.exportToDataBase(NodeId , title , cloudStore , driverClass , connectionUrl , username , password, hdfsPath, resJson,DataMigrate.dbdriverjar ,DataMigrate.maxConn , DataMigrate.maxIdleConn, DataMigrate.waitTime, DataMigrate.dataMigrated);
		}
		
		
	},
	
	// HDFS Migrate
	migrateHDFSData : function(param) {

		var isImportType = false;
		if (param == "true") {
			isImportType = true;
		}

		var title = $('#title').val();
		var NodeId = $('#ClusterNameNodeId').val();
		var cloudStore = $('#cloudStore').val();
		var hdfsPath = $('#hdfsPath').val();

		var host = $("#HDFShost").val();
		var port = $("#HDFSport").val();
		var root = $('#HDFSroot').val();
		var user = null;
		var group= null;
		
		if(!isImportType)
		{
			var user = $('#HDFSuser').val();
			var pass = $('#HDFSgroup').val();		
		}
		document.getElementById('msg_td_1').innerHTML = "";
		if (title == "") {
			document.getElementById('msg_td_1').innerHTML = "Title was not provided. Please provide a title to identify your data migration process.";
			return;

		} else if (NodeId == "") {
			if (isImportType)
				document.getElementById('msg_td_1').innerHTML = "Destination NameNode was not selected. Please select a NameNode";
			else
				document.getElementById('msg_td_1').innerHTML = "Source NameNode was not selected. Please select a NameNode";
			return;
		} else if (host == "") {
			document.getElementById('msg_td_1').innerHTML = "Host was not provided.Please provide HDFS host for data migration.";
			return;
		} else if (!Util.isInHdfsFormat(host)) {
			document.getElementById('msg_td_1').innerHTML = "Host should be in a format (hdfs://xxx).",
			"Insufficient Details.";
			return;
		} else if (port == "") {
			document.getElementById('msg_td_1').innerHTML = "Port number was not provided.Please provide HDFS port number for data migration.";
			return;
		} else if (!Util.isNumeric(port)) {
			document.getElementById('msg_td_1').innerHTML = "Port number must be a numeric value.";
			return;
		} else if(!isImportType)
		{
			if (user == "") {
				document.getElementById('msg_td_1').innerHTML = "User Name was not provided.Please provide User Name for data migration.";
				return;
			} else if (group == "") {
				document.getElementById('msg_td_1').innerHTML = "Group was not provided.Please provide Group for data migration.";
				return;
			} 
			
		} else if (root == "") {
			document.getElementById('msg_td_1').innerHTML = "Root path was not provided. Please provide Root path for data migration.";
			return;
		} else if (hdfsPath == "") {
			document.getElementById('msg_td_1').innerHTML = "HDFS path was not provided. Please provide hdfs path for data migration.";
			return;
		}
		else if(Util.isContainSpecialCharButNotUnderscoreAndSlash(document.getElementById('hdfsPath').value))
		{
			jAlert("HDFS path cannot contain special characters.", "Incorrect Detail");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		host += ":" + port;

		var isExtract = false;
		if (isImportType) {
			isExtract = document.getElementById("isExtractHDFS").checked;
			document.getElementById('importHDFS').disabled = true;
		} else {
			$('#Export').attr("disabled", true);
		}
		
		var compression = $('#compression').val();
		var encryption = $('#encryption').val();
		RemoteManager.startHDFSMigration(NodeId, title, cloudStore, host, root, user, group, hdfsPath, isImportType, isExtract, compression, encryption, DataMigrate.dataMigrated);		
	},

	// SSH Migrate
	migrateSSHData : function(param) {

		var isImportType = false;
		if (param == "true") {
			isImportType = true;
		}

		var IsPass = true;
		if($("#authMethod").val() == "SSHk") {
			IsPass = false;
		}
		
		var title = $('#title').val();
		var NodeId = $('#ClusterNameNodeId').val();
		var cloudStore = $('#cloudStore').val();
		var hdfsPath = $('#hdfsPath').val();

		var host = $("#SSHhost").val();
		var port = $("#SSHport").val();
		var user = $("#SSHuser").val();
		var pass = $("#SSHpass").val();
		var key = $("#SSHkey").val();
		var root = $('#SSHroot').val();

		document.getElementById('msg_td_1').innerHTML = "";
		if (title == "") {
			document.getElementById('msg_td_1').innerHTML = "Title was not provided. Please provide a title to identify your data migration process.";
			return;

		} else if (NodeId == "") {
			if (isImportType)
				document.getElementById('msg_td_1').innerHTML = "Destination NameNode was not selected. Please select a NameNode";
			else
				document.getElementById('msg_td_1').innerHTML = "Source NameNode was not selected. Please select a NameNode";
			return;
		} else if (host == "") {
			document.getElementById('msg_td_1').innerHTML = "Host was not provided.Please provide HDFS host for data migration.";
			return;
		} else if (port == "") {
			document.getElementById('msg_td_1').innerHTML = "Port number was not provided.Please provide HDFS port number for data migration.";
			return;
		} else if (!Util.isNumeric(port)) {
			document.getElementById('msg_td_1').innerHTML = "Port number must be a numeric value.";
			return;
		} else if (user == "") {
			document.getElementById('msg_td_1').innerHTML = "User Name was not provided. Please provide User Name for data migration.";
			return;
		} else if (IsPass) {
			if(pass == "") {
				document.getElementById('msg_td_1').innerHTML = "Password was not provided. Please provide Password for data migration.";
				return;
			}
		} else if (!IsPass) {
			if(key == "") {
				document.getElementById('msg_td_1').innerHTML = "SSh Key was not provided. Please provide SSH Key for data migration.";
				return;
			}
		} 
		if (root == "") {
			document.getElementById('msg_td_1').innerHTML = "SSH Source Path was not provided. Please provide SSH Source Path for data migration.";
			return;
		} else if (hdfsPath == "") {
			document.getElementById('msg_td_1').innerHTML = "HDFS path was not provided. Please provide hdfs path for data migration.";
			return;
		}
		else if(Util.isContainSpecialCharButNotUnderscoreAndSlash(document.getElementById('hdfsPath').value))
		{
			jAlert("HDFS path cannot contain special characters.", "Incorrect Detail");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
//		var pathArray = new Array();
//		root = root.replace(/\s/g, "");
//		pathArray = root.split(',');
//		function to convert root into tokens and push into pathArray 

		var isExtract = false;
		if (isImportType) 
		{
			isExtract = document.getElementById("isExtractSSH").checked;
			document.getElementById('importSSH').disabled = true;
		}
		else 
			$('#Export').attr("disabled", true);
		
		var compression = $('#compression').val();
		var encryption = $('#encryption').val();
		RemoteManager.startSSHMigration(NodeId, title, cloudStore, host, port, user, pass, root, !IsPass, hdfsPath, isImportType, isExtract, compression, encryption, DataMigrate.dataMigrated);
	},

	// HTTP Migrate
	migrateHTTPData : function(param) {

		var isImportType = false;
		if (param == "true") {
			isImportType = true;
		}

		var title = $('#title').val();
		var NodeId = $('#ClusterNameNodeId').val();
		var hdfsPath = $('#hdfsPath').val();
		var cloudStore = $('#cloudStore').val();

		var url = $('#httpBaseURL').val();
		var username = $('#httpUser').val();
		var password = $('#httpPass').val();
		var filefolder = $('#fileFolderHttp').val();
		// var parsepat = $('#parsePatternHttp').val();

		document.getElementById('msg_td_1').innerHTML = "";
		if (title == "") {
			document.getElementById('msg_td_1').innerHTML = "Title was not provided. Please provide a title to identify your data migration process.";
			return;

		} else if (NodeId == "") {
			if (isImportType)
				document.getElementById('msg_td_1').innerHTML = "Destination NameNode was not selected. Please select a NameNode";
			else
				document.getElementById('msg_td_1').innerHTML = "Source NameNode was not selected. Please select a NameNode";
			return;
		} else if (hdfsPath == "") {
			document.getElementById('msg_td_1').innerHTML = "HDFS path was not provided. Please provide hdfs path for data migration.";
			return;
		} 
		else if(Util.isContainSpecialCharButNotUnderscoreAndSlash(document.getElementById('hdfsPath').value))
		{
			jAlert("HDFS path cannot contain special characters.", "Incorrect Detail");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		else if (url == "") {
			document.getElementById('msg_td_1').innerHTML = "Base URL was not provided.Please provide Base URL for data migration.";
			return;
		} else if (!Util.isInHttpFormat(url)) {
					document.getElementById('msg_td_1').innerHTML = "Base URL should be in format (Http://xxx or Https://xxx).",
					"Insufficient Details.";
			return;
			// } else if (username == "") {
			// document.getElementById('msg_td_1').innerHTML = "User name was
			// not provided. Please provide user name for data migration.";
			// return;
			// } else if (password == "") {
			// document.getElementById('msg_td_1').innerHTML = "Password was not
			// provided. Please provide Password for data migration.";
			// return;
		} else if (filefolder == "") {
			document.getElementById('msg_td_1').innerHTML = "File was not provided. Please provide File for data migration.";
			return;
			// } else if (parsepat == "") {
			// document.getElementById('msg_td_1').innerHTML = "Parse Pattern
			// was not provided. Please provide Parse Pattern for data
			// migration.";
			// return;
		}

		var isExtract = false;
		if (isImportType) {
			document.getElementById('importHttp').disabled = true;
		} else {
			$('#Export').attr("disabled", true);
		}

		var compression = $('#compression').val();
		var encryption = $('#encryption').val();
		var encoding = $("#encodingHttp").val();
		
		RemoteManager.startHTTPMigration(NodeId, title, cloudStore, url, username, password, filefolder, encoding, hdfsPath, isImportType, compression, encryption, DataMigrate.dataMigrated);
				
	},

	// POP Migrate
	migratePOPData : function() {

		var title = $('#title').val();
		var NodeId = $('#ClusterNameNodeId').val();
		var hdfsPath = $('#hdfsPath').val();
		var cloudStore = $('#cloudStore').val();

		var connection = $("#connectionIdEmail").val();
		var startdate = $("#startDateEmail").val();
		var enddate = $("#endDateEmail").val();
		var folder = $("#emailFolder").val();

		document.getElementById('msg_td_1').innerHTML = "";
		if (title == "") {
			document.getElementById('msg_td_1').innerHTML = "Title was not provided. Please provide a title to identify your data migration process.";
			return;
		} else if (NodeId == "") {
			document.getElementById('msg_td_1').innerHTML = "Destination NameNode was not selected. Please select a NameNode";
			return;
		} else if (hdfsPath == "") {
			document.getElementById('msg_td_1').innerHTML = "HDFS path was not provided. Please provide hdfs path for data migration.";
			return;
		}
		else if(Util.isContainSpecialCharButNotUnderscoreAndSlash(document.getElementById('hdfsPath').value))
		{
			jAlert("HDFS path cannot contain special characters.", "Incorrect Detail");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		else if (connection == "0") {
			document.getElementById('msg_td_1').innerHTML = "No connection was provided. Please provide a connection for Email extraction. (To make a new connecton, click on 'Add' button)";
			return;
		} else if (folder == "0") {
			document.getElementById('msg_td_1').innerHTML = "No folder was provided. Please provide a folder for Email extraction.";
			return;
		} else if (startdate != "") {
			if (!Util.isInDateFormat(startdate)) {
				document.getElementById('msg_td_1').innerHTML = "Start Date is not in proper format (YYYY-MM-DD).";
				return;
			}
		}
		if (enddate != "") {
			if (!Util.isInDateFormat(enddate)) {
				document.getElementById('msg_td_1').innerHTML = "End Date is not in proper format (YYYY-MM-DD).";
				return;
			}
		}

		var compression = $('#compression').val();
		var encryption = $('#encryption').val();
		var suffix = $("#suffixEmail").val();
		var prefix = $("#prefixEmail").val();
		RemoteManager.startEmailMigration(NodeId, title, cloudStore,
				connection, folder, startdate, enddate, prefix, suffix,
				hdfsPath, compression, encryption, DataMigrate.dataMigrated);
	},

	fillFTPNameNodeHost : function() {
		RemoteManager.getAllHostDetails(DataMigrate.fillFTPNameNodeHostOption);
	},

	fillFTPNameNodeHostOption : function(list) {

		selectList = dwr.util.byId('hostname');
		dwr.util.removeAllOptions(selectList);
		DataMigrate.addOption(selectList, 0, 'Select Host');
		hostNames = 0;
		for ( var i = 0; i < list.length; i++) {
			DataMigrate.addOption(selectList, list[i].id, list[i].hostIP);
		}
		dwr.util.setValue('hostname', hostNames);

	},
	addOption : function(selectbox, value, text) {
		var optn = document.createElement("OPTION");
		optn.text = text;
		optn.value = value;
		selectbox.options.add(optn);
	},
	
	fillClusterNameNodeID : function() {

		RemoteManager
				.getNonStandByNodes(DataMigrate.populateClusterNameNodeIds);
	},

	
	populateClusterNameNodeIds : function(list) {

		var data = '';
		
		if(list == null || list == undefined || list.length == 0)
			data = '<option value="">Select NameNode</option>';
		else
		{
			for ( var i = 0; i < list.length; i++) 
			{
				var node = list[i];
				data += '<option value="' + node.id + '">' + node.id + '</option>';
			}			
		}
		$('#ClusterNameNodeId').html(data);

	},

	fillTagIDs : function ()
	{
		RemoteManager.getAllCustomTagMetadataIds(DataMigrate.populateTagIds);
	},
	
	populateTagIds : function(array)
	{
		var data = '' ;
		for(var i=0 ; i<array.length ; i++)
		{
			data += '<option value="'+ array[i] +'">'+ array[i] +'</option>';
		}
		$('#logicalTagsJSONId').html(data);
	},
	
	nextLocalData : function() {
		if (document.getElementById('title').value == ""
				|| document.getElementById('title').value == null) {
			jAlert("Title missing.", "Incomplete Detail");
			$("#popup_container").css("z-index", "9999999");
		} else if (document.getElementById('ClusterNameNodeId').value == ""
				|| document.getElementById('ClusterNameNodeId').value == null) {
			jAlert("NameNode not selected.", "Incomplete Detail");
			$("#popup_container").css("z-index", "9999999");
		} else if (document.getElementById('hdfsPath').value == ""
				|| document.getElementById('hdfsPath').value == null) {
			jAlert("HDFS path not set.", "Incomplete Detail");
			$("#popup_container").css("z-index", "9999999");
		} 
		else if(Util.isContainSpecialCharButNotUnderscoreAndSlash(document.getElementById('hdfsPath').value))
		{
			jAlert("HDFS path cannot contain special characters.", "Incorrect Detail");
			$("#popup_container").css("z-index", "9999999");
		}
		else {
			$('#localStoreDiv').css('display', 'none');
			$('#dataMigrateCommon').css('display', 'none');
			$('#FileDelectionDiv').css('display', '');
			$('#instruction').html(
					"Please add the files you need to import to QueryIO.");
			DataMigrate.fileName.push([ 'file-1' ]);
			DataMigrate.title = document.getElementById('title').value;
			DataMigrate.nnID = document.getElementById('ClusterNameNodeId').value;
			DataMigrate.hdfsPath = document.getElementById('hdfsPath').value;
		}
	},

	hideFileUploadWizard : function() {
		$('#localStoreDiv').css('display', '');
		$('#dataMigrateCommon').css('display', '');
		$('#FileDelectionDiv').css('display', 'none');
		$('#instruction')
				.html(
						"Import data to HDFS cluster from Amazon, FTP server or local file system.");
		$(".files").html("");
	},

	addVolumeClicked : function() {

		if (DataMigrate.counter == 10) {
			jAlert("Only 10 Files are allowed at once.", "Limit Reached");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		var temp = DataMigrate.counter + 1

		var tbl_data = '<tr id="form_row'
				+ temp
				+ '"><td><div id="form_div_'
				+ temp
				+ '" style="display: "><form id="upload_File'
				+ temp
				+ '">'
				+ '<input type="hidden" name="title" id="title_'
				+ temp
				+ '"><input type="hidden" name="ClusterNameNodeId" id="ClusterNameNodeId_'
				+ temp
				+ '"><input type="hidden" name="hdfsPath" id="hdfsPath_'
				+ temp
				+ '"><input type="hidden" name="extractArchives" id="extractArchives_'
				+ temp
				+ '"><input type="hidden" name="fileId" id="fileId_'
				+ temp
				+ '">'
				+ '<table  style="width: 100%; " border="1"><tr id = "fileMigraterow_'
				+ temp
				+ '">'
				+ '<td style="text-align:left; padding-bottom: 0px;"><input checked="checked" type="checkbox" id="isExtract'
				+ temp
				+ '" name="isExtract">&nbsp; Unzip file after upload.<br>'
				+ '<input  name="file-'
				+ temp
				+ '" type="file" id ="file-'
				+ temp
				+ '" onchange = "javascript:DataMigrate.enableUploadButton('
				+ temp
				+ ');" ></td>'
				+ '<td style="text-align:left; width: 30%;"><a style="color: white;" href="javascript:DataMigrate.removeVolumeClicked('
				+ temp
				+ ');">'
				+ '<img alt="Remove File" src="images/cross_sign_upload.png" id="minusImage" style="height: 20px; width: 20px; float: right;"></a><input type="button" onclick="javascript:DataMigrate.uploadFile(\''
				+ temp
				+ '\');" value = "Upload" disabled = "disabled" id = "uploadButton-'
				+ temp
				+ '"></td>'
				+ '</tr></table></form></div>'
				+ '<div id="upload_status_div_'
				+ temp
				+ '" style="display: none;"><span id="fileName_'
				+ temp
				+ '" style = "float: left; width: 30%;"></span>'
				+ '<div id="progressbar_div_'
				+ temp
				+ '" style="height: 15px; background-color: white; position: relative; display: inline-block; float: left; width: 40%;"></div>'
				+ '<span id="progressError_'
				+ temp
				+ '" style="float: right; margin-left: 5px; display: none;"><a href="javascript:Navbar.showServerLog();">View Log</a></span>'
				+ '<span id="progressStatus_' + temp
				+ '" style=" float: right; margin-left: 5px;">Uploading</span>'
				+ '<span id="progresst_' + temp
				+ '" style=" float: right; ">0%</span>' + '</div></td></tr>';
		$('#FileSelectTablebody').append(tbl_data);
		DataMigrate.fileName.push([ "file-" + temp ]);
		DataMigrate.counter = temp;
	},

	enableUploadButton : function(id) {
		$("#uploadButton-" + id).removeAttr("disabled");
	},

	removeVolumeClicked : function(id) {

		if (DataMigrate.counter == 1) {
			jAlert("Atleast 1 File should be there to remove.",
					"Invalid Action");
			$("#popup_container").css("z-index", "9999999");
			return;
		}
		$("#form_row" + id).remove();
		DataMigrate.fileName.splice(DataMigrate.fileName.indexOf('file-' + id),
				1);
		DataMigrate.counter--;
	},
	uploadFile : function(id) {
		$('#instruction').html("Upload Request Status");
		$('#submitBox').css('display', 'none');
		// DataMigrate.showPopup();

		$('#form_div_' + id).css("display", "none");
		$('#upload_status_div_' + id).css("display", "");

		$('#okBox').css('display', '');
		$('#okBox :input').attr('disabled', false);

		$('#title_' + id).val($('#title').val());
		$('#ClusterNameNodeId_' + id).val($('#ClusterNameNodeId').val());
		$('#hdfsPath_' + id).val($('#hdfsPath').val());
		if ($('#isExtract' + id).is(':checked')) {
			$('#extractArchives_' + id).val(true);
		} else {
			$('#extractArchives_' + id).val(false);
		}
		$('#fileId_' + id).val(id);

		var form = document.getElementById("upload_File" + id);

		var fileName = $("#file-" + id).val().substring(
				$("#file-" + id).val().lastIndexOf("\\") + 1);
		$('#fileName_' + id).text(fileName);

		// var contents = '<form id = "dummyUpload' + id + '"></form>';// +
		// $("#upload_File" + id).html() + '</form>';
		// document.getElementById('dummyForm').innerHTML = contents;

		// var form = document.getElementById("dummyUpload" + id);
		// $(':input[name]', form1).each(function() {
		// $('[name=' + $(this).attr('name') +']', form).val($(this).val())
		// });
		// }

		form.setAttribute("target", "responseFrame");
		form.setAttribute("action", "FileUpload");
		form.setAttribute("method", "POST");
		form.setAttribute("enctype", "multipart/form-data");
		form.setAttribute("encoding", "multipart/form-data");
		DataMigrate.isFirstTimeStatusCalled = true;
		DataMigrate.UploadStatus(id);
		form.submit();

	},

	UploadStatus : function(fileId) {
		RemoteManager.getDataUploadStatus(fileId,
				DataMigrate.setUploadStatusinDiv);
	},

	setUploadStatusinDiv : function(obj) {
		var fileId = obj["fileId"];

		var pervalue = obj["pervalue"];
		$('#progressbar_div_' + fileId).progressbar({
			value : pervalue,
			showText : true
		});

		$('#progresst_' + fileId).text(pervalue + "%");

		if (obj["status"] == "Success" || obj["status"] == "Failed") {
			$('#progressStatus_' + fileId).text(obj["status"]);
			if (obj["status"] == "Failed") {
				$('#progressError_' + fileId).css('display', '');
			}
		} else {
			var timerProcess = setTimeout(function() {
				DataMigrate.UploadStatus(fileId)
			}, 500);
			DataMigrate.timer.push(timerProcess);
		}
	},
	showPopup : function() {
		$('#FileSelectTable').css('display', 'none');
		$('#responsePopup').css('display', '');
		dwr.util.byId('popup.image.processing').style.display = '';
		dwr.util.setValue('popup.message', 'Processing Upload Request...');
		dwr.util.setValue('popup.status', 'Uploading');
	},

	fillResponsePopup : function(content) {

		// $('#okBox :input').removeAttr('disabled');

		var status;
		var imgId;

		if (content.indexOf("Failed") != -1) {
			status = "Failure";
			dwr.util.byId("popup.image.success").style.display = 'none';
			imgId = "popup.image.fail";
		} else {
			status = "Success";
			dwr.util.byId("popup.image.fail").style.display = 'none';
			imgId = "popup.image.success";
		}
		dwr.util.byId('popup.image.processing').style.display = 'none';
		dwr.util.byId(imgId).style.display = '';

		dwr.util.setValue('popup.message', content);
		dwr.util.setValue('popup.status', status);
	},

	showUploadProgress : function() {

		var fileId = '';
		var val = '';
		for ( var i = 0; i < DataMigrate.fileName.length; i++) {

			var file = DataMigrate.fileName[i] + '';
			fileId = file.substring(file.indexOf('-') + 1);

			val = $('#' + file).val() + '';
			val = val.split('\\');

			val = val[val.length - 1]
			var fileName = "Upload file /" + val;

			RemoteManager.getByTitle(fileName, parseInt(fileId),
					DataMigrate.updateProgress);
		}

		$('#progress_bar_' + fileId).html('');
		$('#progress_bar_' + fileId)
				.append(
						'<div id="progressbar_div'
								+ fileId
								+ '" style="height: 15px; background-color: white; "><span id="progresst'
								+ fileId
								+ '" style="    position:absolute;     text-align: center; " ">0%</span></div>');
	},
	updateProgress : function(migrationInfo) {

		if (migrationInfo != null) {
			var fileId = migrationInfo.id;
			var progress = migrationInfo.progress;

			$('#progress_bar_' + fileId).html();
			$('#progress_bar_' + fileId).html('');
			$('#progress_bar_' + fileId)
					.append(
							migrationInfo.title
									+ '<div id="progressbar_div'
									+ fileId
									+ '" style="height: 15px; background: white; "><span id="progresst'
									+ fileId
									+ '" style="    position:absolute;     text-align: center; " ">'
									+ progress + '%</span></div>');
			$('#progressbar_div' + fileId).progressbar({
				value : progress,
				showText : true
			});
			var fileName = migrationInfo.title;

			if (migrationInfo.progress == 100) {
				DataMigrate.fileName.splice(DataMigrate.fileName
						.indexOf('file-' + migrationInfo.id), 1);
			} else {
				var timerProcess = setTimeout('DataMigrate.updateProgress2(\''
						+ fileName + '\',\'' + fileId + '\')', 500);
				DataMigrate.timer.push(timerProcess);
				// DataMigrate.showUploadProgress();
			}
		}
	},
	updateProgress2 : function(fileName, fileId) {
		RemoteManager.getByTitle(fileName, parseInt(fileId),
				DataMigrate.updateProgress);
	},

	setLocationSearch : function(id){
		var flag = document.getElementById(id).checked;
		var table = document.getElementById(id).value;
		if(flag){
			DataMigrate.tablenames.push(table);
		}else{
			var index = DataMigrate.tablenames.indexOf(table);
			DataMigrate.tablenames.splice(index, 1);
		}
		var length = DataMigrate.tablenames.length;
		var data = "";
		for(var i=0;i<length;i++){
			data = data + DataMigrate.tablenames[i] + ",";
		}
		DataMigrate.tableFlag = true;
		data = data.substring(0 , data.length-1);
		$("#srch_from_fld").val(data);
	},
	saveDBConnection : function()
	{
		if($('#connectionId').val()==""){
			jAlert("You must specify the connection id.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}	   
		if($("#DBDriverClass").val() == ""){
			jAlert("You must specify the database driver class.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		if($("#DBConnectionURL").val() == ""){
			jAlert("You must specify the database connection url.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		if($("#DataBaseUser").val() == ""){
			jAlert("You must specify the username.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		if($("#DataBasePass").val() == ""){
			jAlert("You must specify the password.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		if($("#DataBaseDriverJar").val() == ""){
			jAlert("You must specify the jar file.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		var filename = $("#DataBaseDriverJar").val();
		filename = filename.substring(filename.length-4 , filename.length);
		if(filename!='.jar' && filename!='.JAR'){
			jAlert("You must specify the jar file.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		var form = document.getElementById("CommonForm");
		DataMigrate.fileUpload(form);
		
	},
	fileUpload : function(form )
	{
		var div = document.getElementById("DataBaseDivLB");
		form.appendChild(div);
		var iframe = document.createElement("iframe");
		var flag = false;
        iframe.setAttribute("id", "upload_iframe");
        iframe.setAttribute("name", "upload_iframe");
        iframe.setAttribute("width", "0");
        iframe.setAttribute("height", "0");
        iframe.setAttribute("border", "0");
        iframe.setAttribute("style", "width: 0; height: 0; border: none;");
        var div_id = 'formDIV';
        // Add to document...
        form.parentNode.appendChild(iframe);
        $("#connetionIdValueForm").val($("#connectionId").val());
        
        window.frames['upload_iframe'].name = "upload_iframe";
        $("#addEditTable").hide();
        var id = document.getElementById('DataBaseDriverJar').value;
    	id = id.substring(id.lastIndexOf("\\")+1 , id.length);
        $("#parser").text(id);
        $("#respMessage").text('Uploading Jar...');
        $("#headerspan").text('Status');
        $("#processingDiv").css('display', '');
        $('#respProcessing').css('display','');
        $('#log_div').hide();
    	$('#respStatus').text('Processing...');
    	$('#respFail').css('display','none');
    	$('#respSuccess').css('display','none');
    	
        iframeId = document.getElementById("upload_iframe");
        var eventHandler = function (){
        		var content = new Object();
                if (iframeId.detachEvent)
                	iframeId.detachEvent("onload", eventHandler);
                else 
                	iframeId.removeEventListener("load", eventHandler, false);
                // Message from server...
                if (iframeId.contentDocument) {
                    content = iframeId.contentDocument.body.textContent;
                } else if (iframeId.contentWindow) {
                    content = iframeId.contentWindow.document.body.textContent;
                } else if (iframeId.document) {
                    content = iframeId.document.body.textContent;
                }
                content=JSON.parse(content);
                $('#respProcessing').css('display','none');
                $('#respMessage').text(content.message);
                if(content.status == 'sucess'){
        
                	$("#respMessage").text('Jar uploaded successfully.');
                	$('#respStatus').text('Success');
                	$('#respSuccess').css('display','');
                	$('#respFail').css('display','none');
                	
                }else{
                	
                	$("#respMessage").text('Jar uploading failed.');
                	$('#respStatus').text('Failed');
                	$('#respFail').css('display','');
                	$('#respSuccess').css('display','none');
                }
                flag = true;
                DataMigrate.closeDBBox();
    	}
        if(!flag){
        	$('#respProcessing').css('display','none');
        	$("#respMessage").text('Jar uploading failed.');
        	$('#respStatus').text('Failed');
        	$('#respFail').css('display','');
        }
        $('#okpopup').removeAttr('disabled');
        var val = document.getElementById("connectionId").value;
        if (iframeId.addEventListener) iframeId.addEventListener("load", eventHandler, true);
        if (iframeId.attachEvent) iframeId.attachEvent("onload", eventHandler);
        // Set properties of form...
        form.setAttribute("target", "upload_iframe");
        form.setAttribute("action", "DataBaseJarFileUpload");
        form.setAttribute("method", "post");
        form.setAttribute("enctype", "multipart/form-data");
        form.setAttribute("encoding", "multipart/form-data");
        form.submit();
        form.removeChild(div);
	},
	closeDBBox : function(){
//		Util.removeLightbox("addDC");
		
		DataConnection.closeBox(false);
		RemoteManager
		.getAllDataConnections(DataMigrate.fillDBConnectionIds);
	},
	checkRequest : function()
	{
		var flag = document.getElementById('DataBaseImportAll').checked;
		if(flag)
		{
			$('#db_tables').css('display' , 'none');
		}
		else
		{
			$('#db_tables').css('display' , '');
		}
	},
	setUnzip : function(val, fileName) {
		DataMigrate.fileIsUnzipStatus[fileName] = val;
	},
	
	showhide : function(show,hide)
	{
		$('#' + show).css('display','');
		$('#' + hide).css('display','none');		
	},
	
	OpenTagData : function()
	{
//		DataMigrate.currentOperation = 'add';
		Util.addLightbox('mdt_Box','resources/add_MigrationDataTagger.html');
//		Util.addLightbox('mdt_Box','resources/add_NewMigrationDataTagger.html');
		$("#popup_container").css("z-index","99999999");
	}
};