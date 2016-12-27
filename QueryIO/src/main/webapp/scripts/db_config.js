DB_Config = {
		
	aaData: null,	
	status: null,
	selectedDBId: null,
	connectionName: null,
	connectionURL: null,
	driverName: null,
	username: null,
	password: null,
	driverJar: null,
	newConnection: false,
	newConnectionName: null,
	newConnectionType: null,
	destinationName: null,
	lastsel2: null,
	response : null,
	isSubmitFromNN :false,
	migratedDBName :null,
	dbNameNotToUsed :null,
	
	selectedNameNode : '',
	
	isJarUpdated: false,
	isChangeShown: true,
	isPrimary: true,
	isMetaStore: true,
	
	isChangeShownMapping : true,
	isCreateDDL : true,
	
	ready: function()
	{
		$("#refreshViewButton").attr('disabled', 'disabled');
		DB_Config.adjustGridHeight();
		DB_Config.newConnection = false;
		
		if (Navbar.hiveViewNameNode != undefined)
		{
    		RemoteManager.getAnalyticsDBNameForNameNodeMapping(Navbar.hiveViewNameNode, DB_Config.setHighlightedDb);
		}
		else
		{
			RemoteManager.getAllConnectionsName(Navbar.isHiveViewSelected, DB_Config.fetchData);
		}
		DB_Config.slide();
		$('#migrationStatusBtn').show();
		jQuery(window).bind('resize', function() {
			DB_Config.resizeGrid();
		}).trigger('resize');
		
		if(Navbar.showSelectedDb){
			DB_Config.status = 'Update';
    		$('#SaveUpdate').html('Update');
    		if(Navbar.selectedDatabaseFromMigrationTable != null)
    		{
    			DB_Config.selectedDBId = Navbar.selectedDatabaseFromMigrationTable;
    			Navbar.selectedDatabaseFromMigrationTable = null;
    		}
    		else
    		{
    			DB_Config.selectedDBId = $('#queryIODatabase').text();
    		}
	    	DB_Config.setDBDetails();
		}
		document.getElementById('updateButton').disabled = true;
		$("#divDBDetails input").bind('keypress change', function() 
		{
			document.getElementById('updateButton').disabled = false;
		});
		DB_Config.resizeGrid();
	},

	resizeGrid : function() {
		$("#dbTable").setGridWidth(($("#table_container").width() - 5), true);
		$('.ui-jqgrid-bdiv:first').css('height','590px');
	},
	
	slide : function()
	{
		$("#ShowDC").hide();
		
		$("#HideDC").click(function()
		{
			$("#HideDC").hide();
			$("#ShowDC").show();
			$("#db_detail").width("2%");
			$("#rightDC").width("auto");
			$("#db_detail").css("min-width", "0px");
			$("#dbConfig").css("display","none");
			DB_Config.resizeGrid();
		});
		$("#ShowDC").click(function()
		{
			$("#ShowDC").hide();
			$("#HideDC").show();
			$("#db_detail").width("20%");
			$("#rightDC").width("auto");
			$("#db_detail").css("min-width", "348px");
			$("#dbConfig").css("display","block");
			DB_Config.resizeGrid();
		});
	},
	
	fillPopUpBox: function()
	{
		/*if(DB_Config.status == "Save")
		{
			var id = "Save";
			dwr.util.cloneNode('pop.pattern',{ idSuffix: id});
			dwr.util.byId('pop.pattern'+id).style.display = '';
			dwr.util.byId('popup.image.processing'+id).style.display = '';
			dwr.util.setValue('popup.component','DBConnection');
			dwr.util.setValue('popup.host'+id,DB_Config.connectionName);
			dwr.util.setValue('popup.message'+id,'Processing Request...');
			dwr.util.setValue('popup.status'+id,'');
			DB_Config.callForInsert();
		}
		else if(DB_Config.status == "Update")
		{
			var id = "Update";
			dwr.util.cloneNode('pop.pattern',{ idSuffix: id});
			dwr.util.byId('pop.pattern'+id).style.display = '';
			dwr.util.byId('popup.image.processing'+id).style.display = '';
			dwr.util.setValue('popup.component','DBConnection');
			dwr.util.setValue('popup.host'+id,DB_Config.connectionName);
			dwr.util.setValue('popup.message'+id,'Processing Request...');
			dwr.util.setValue('popup.status'+id,'');
			DB_Config.callForUpdate();
		}*/
		 if(DB_Config.status == "Migrate")
		{
			var id = "Migrate";
			dwr.util.cloneNode('pop.pattern',{ idSuffix: id});
			dwr.util.byId('pop.pattern'+id).style.display = '';
			dwr.util.byId('popup.image.processing'+id).style.display = '';
			dwr.util.setValue('popup.component','DBConnection');
			dwr.util.setValue('popup.host'+id,DB_Config.selectedDBId);
			dwr.util.setValue('popup.message'+id,'Processing Request...');
			dwr.util.setValue('popup.status'+id,'');
			DB_Config.callForMigrate();
		}
		else if(DB_Config.status == "Activate"){
			var id = "Activate";
			dwr.util.cloneNode('pop.pattern',{ idSuffix: id});
			dwr.util.byId('pop.pattern'+id).style.display = '';
			dwr.util.byId('popup.image.processing'+id).style.display = '';
			dwr.util.setValue('popup.component','DBConnection');
			dwr.util.setValue('popup.host'+id,DB_Config.selectedDBId);
			dwr.util.setValue('popup.message'+id,'Processing Request...');
			dwr.util.setValue('popup.status'+id,'');
			DB_Config.callforActivate();
		}
		else if(DB_Config.status == "Remove"){
			var id = "Remove";
			dwr.util.cloneNode('pop.pattern',{ idSuffix: id});
			dwr.util.byId('pop.pattern'+id).style.display = '';
			dwr.util.byId('popup.image.processing'+id).style.display = '';
			dwr.util.setValue('popup.component','DBConnection');
			dwr.util.setValue('popup.host'+id,DB_Config.selectedDBId);
			dwr.util.setValue('popup.message'+id,'Processing Request...');
			dwr.util.setValue('popup.status'+id,'');
			DB_Config.callForRemove();
		}
		else{
			var id = "exportDDL";
			dwr.util.cloneNode('pop.pattern',{ idSuffix: id});
			dwr.util.byId('pop.pattern'+id).style.display = '';
			dwr.util.byId('popup.image.processing'+id).style.display = '';
			dwr.util.setValue('popup.component','DBConnection');
			dwr.util.setValue('popup.host'+id,DB_Config.selectedDBId);
			dwr.util.setValue('popup.message'+id,'Processing Request...');
			dwr.util.setValue('popup.status'+id,'');
			DB_Config.callForExportDDL();
		}
	},
	
	moveToMigrationStatus : function()
	{
		Util.removeLightbox('addDBConnection');
		Navbar.changeTab('DB_Config', 'admin', 'DBConfigMigration');
	},
	
	
	closeBox: function(isRefresh)
	{
		DB_Config.aaData = null;	
		DB_Config.status = null;
		DB_Config.selectedDBId = null;
		DB_Config.connectionName = null;
		DB_Config.connectionURL = null;
		DB_Config.driverName = null;
		DB_Config.username = null;
		DB_Config.password = null;
		DB_Config.newConnection = false;
		DB_Config.isCreateDDL = true;
		DB_Config.destinationName = null;
		
		if(DB_Config.isSubmitFromNN){
			NN_Summary.closeDBBox();
			DB_Config.isSubmitFromNN=false;
		}else{
			Util.removeLightbox('addDBConnection');
		}
		
		if(isRefresh)
			Navbar.refreshView();
		else
			$('#selectedTable').attr('disabled','true');
	},
	
	setHighlightedDb : function(dbName)
	{
		DB_Config.hiveViewSelectedDb = dbName;
		Navbar.hiveViewNameNode = undefined;
		RemoteManager.getAllConnectionsName(Navbar.isHiveViewSelected, DB_Config.fetchData);
	},
	
    fetchData : function(data)
    {
    	DB_Config.aaData = new Array();
    	var selectedIndex = -1;
 		for (var i=0; i<data.length; i++)
 		{
			if (DB_Config.hiveViewSelectedDb == data[i][0])
				selectedIndex = i;
			DB_Config.aaData[i] = ({Name : data[i][0], Role : data[i][1], Type : data[i][2]});
 		}
 		DB_Config.populateLeftTable(DB_Config.aaData);
 		var rowId = jQuery("#dbTable").jqGrid('getDataIDs');
 		
 		if (!Navbar.showSelectedDb){
 			Navbar.showSelectedDb = false;
			
			if (selectedIndex == -1)
				jQuery("#dbTable").jqGrid('setSelection',rowId[0],true);
			else
				jQuery("#dbTable").jqGrid('setSelection',rowId[selectedIndex],true);
 		}
    },
     
 	populateLeftTable : function (aadata)
 	{
 		var topHeight = $("#service_ref").height() - $("#db_detail").height() - $('#dbHeader').height() -$('#buttonDiv').height() -13;
 		var actionContainerHeight = $('#actionContainer').height();
 		$('#connection_header').html('Databases ('+aadata.length+')');
 		$('#dbTable').remove();
 		$('#table_container').html('<table id="dbTable"></table>');
    		jQuery("#dbTable").jqGrid({
            datatype: "local", 
            colNames:['Name', 'Role', 'Type'],
            colModel:[  {name:'Name',index:'Name', width:(($("#table_container").width()-15)/3), sortable: false},
                        {name:'Role',index:'Role', width:(($("#table_container").width()-20)/3), sortable: false},
            			{name:'Type',index:'Type', width:(($("#table_container").width()-20)/3), sortable: false}
            	],
            height:($("#table_container").height() - 35),//topHeight-actionContainerHeight - 20,
            width: ($("#table_container").width() - 5),
            shrinkToFit: false,
            rowNum: aadata.length,
           	pager: "",
           	altRows: true,
            viewrecords: true,
            sortable: false,
 			pagination: false,
 	        caption: "",
 	        onSortCol: function (index, idxcol, sortorder) {
    		        if (this.p.lastsort >= 0 && this.p.lastsort !== idxcol
    		                && this.p.colModel[this.p.lastsort].sortable !== false) {
    		            $(this.grid.headers[this.p.lastsort].el)
    		                .find(">div.ui-jqgrid-sortable>span.s-ico").show();
    		            $(this.grid.headers[this.p.lastsort].el).removeClass('ui-state-highlight');
    		        }
    		        $(this.grid.headers[idxcol].el).addClass('ui-state-highlight');
    		    },
    		    
    		    onSelectRow: function(id) {
    		    	document.getElementById('updateButton').disabled = true;
    		    	var allRowsOnCurrentPage = $('#dbTable').jqGrid('getDataIDs');
    		    	for(var i=0; i<allRowsOnCurrentPage.length; i++)
    		    	{
    		    		if(allRowsOnCurrentPage[i] == id)
    		    			$('#' + allRowsOnCurrentPage[i]).css('background-color','#ECECEC');
    		    		else
    		    			$('#' + allRowsOnCurrentPage[i]).css('background-color','white');
    		    	}
    		    	
    		    	var rowData = jQuery("#dbTable").jqGrid('getRowData',id);							//data of the row

    		    	if (rowData.Name == DB_Config.newConnectionName)
    		    	{
    		    		DB_Config.status = 'Save';
    		    		$('#SaveUpdate').html('Save');
    		    		$('#detail_header').html('Database Configuration ('+rowData.Name+')');
    		    		DB_Config.selectedDBId ='';
    		    		DB_Config.setClear();
    		    		document.getElementById('db_connection').Activate.disabled = true;
    		    	}
    		    	else
    		    	{
    		    		DB_Config.status = 'Update';
    		    		$('#SaveUpdate').html('Update');
	    		    	DB_Config.selectedDBId = rowData.Name;
	    		    	DB_Config.setDBDetails();
    		    	}
    		    },
    		});
    		
 		jQuery("#dbTable").jqGrid('navGrid', '', {add:false, edit:false, del:false});
 		
 		var rowIndexToBeSelected = -1;
 		if(aadata == null || aadata == undefined || aadata.length == 0)
 		{
 			$("#refreshViewButton").removeAttr('disabled');
 		}
 		for(var i=0;i<aadata.length;i++)
 		{
 			if(DB_Config.selectedDBId != null && DB_Config.selectedDBId != '' && aadata[i].Name == DB_Config.selectedDBId)
 			{
 				rowIndexToBeSelected = i + 1;
 			}
 			jQuery("#dbTable").jqGrid('addRowData', i+1, aadata[i]);
 		}
 		
 		if(rowIndexToBeSelected > -1)
 		{
 			jQuery("#dbTable").jqGrid('setSelection', rowIndexToBeSelected);
 		}
 		else
 		{
 			$(jQuery("#dbTable")[0].grid.headers[0].el).addClass('ui-state-highlight');				// Highlight first column header on grid load.
 		}
 		DB_Config.resizeGrid();
	},
	
	updatedJar : function(id, value)
	{
		DB_Config.isJarUpdated = true;
		var extension = value.substring(value.length-4, value.length);
		if(extension != ".jar" && extension != ".JAR")
		{
			jAlert("Only JAR files are required to be uploaded.","Incorrect Detail");
			$("#" + id).val("");
			$("#popup_container").css("z-index","99999999");
		}
	},
	
	showChooseFile : function()
	{
		if (DB_Config.isChangeShown)
		{
			dwr.util.byId('driverJar').style.display = '';
			dwr.util.byId('jarFileText').style.display = 'none';
			$('#chooseFile').val('Keep Unchanged');
			DB_Config.isChangeShown = false;
		}
		else
		{
			dwr.util.byId('driverJar').style.display = 'none';
			dwr.util.byId('jarFileText').style.display = '';
			$('#chooseFile').val('Change');
			DB_Config.isChangeShown = true;
		}
	},
	
	setDetailHeader: function(connName)
	{
//		var imgStatus;
//		if(connName == DB_Config.selectedDBId){
//			imgStatus = '<img id="noStatus" alt="" src="images/status_start.png" class="statusImage">';
//			document.getElementById('db_connection').Activate.disabled = true;
//		}
//		else{
//			imgStatus = '<img id="noStatus" alt="" src="images/status_stop.png" class="statusImage">';
//			document.getElementById('db_connection').Activate.disabled = false;
//		}
		$('#detail_header').html('Database Configuration ('+DB_Config.selectedDBId+')');
	},
	
	
	adjustGridHeight : function()
	{
		var topHeight = $("#service_ref").height() - $("#db_detail").height() - $('#dbHeader').height() -$('#buttonDiv').height() -13;
		
		$("#table_container").height(topHeight);
		var actionContainerHeight = $('#actionContainer').height(); 
		$("#dbTable").setGridHeight((topHeight-actionContainerHeight), false);
    },
     
	
	setDBDetails: function()
	{
		RemoteManager.getDBDetail(DB_Config.selectedDBId,DB_Config.fillDBDetails);
	},
	
	fillNameSpace : function(nameNodeId)
	{
//		if(nameNodeId==null||nameNodeId=='')
//		{
//			$('#nameSpace_header').css('display','none');
//			DB_Config.selectedNameNode = '';
//		}
//		else
//		{
//			$('#nameSpace_header').text("Namespace: "+nameNodeId);
//			$('#nameSpace_header').css('display','');
//			DB_Config.selectedNameNode = nameNodeId;
//		}
	},
	
	fillDBDetails: function(dbinfo)
	{
//		RemoteManager.getNameNodeForDBNameMapping(DB_Config.selectedDBId,DB_Config.callFillNameSpace);
		if (dbinfo == null)
		{
			jAlert("DB Info not available.","Info");
			return;
		}
		$('#dbUrl').val(dbinfo.primaryConnectionURL);
		$('#isPrimary').val(dbinfo.isPrimary);
		DB_Config.isMetaStore = dbinfo.isPrimary;
		$('#type').val(dbinfo.connectionType);
		$('#dbUserName').val(dbinfo.primaryUsername);
		$('#dbPassword').val(dbinfo.primaryPassword);
		$('#dbDriver').val(dbinfo.primaryDriverName);
		
		$('#jarFileText').val(dbinfo.primaryJdbcJar);
		$('#maxConnections').val(dbinfo.maxConnection);
		$('#maxIdleConnections').val(dbinfo.maxIdleConnection);
		$('#waitTimeMilliSeconds').val(dbinfo.waitTimeinMillis);
		$('#waitTimeMilliSeconds').val(dbinfo.waitTimeinMillis);
		var nameNodeId = dbinfo.nameNodeId;
		if(nameNodeId==null||nameNodeId=='')
		{
			$('#nameSpace_header').css('display','none');
			DB_Config.selectedNameNode = '';
		}
		else
		{
			$('#nameSpace_header').text("Namespace: "+nameNodeId);
			$('#nameSpace_header').css('display','');
			DB_Config.selectedNameNode = nameNodeId;
		}
		
		
		dwr.util.byId('driverJar').style.display = 'none';
		dwr.util.byId('jarFileText').style.display = '';
		
//		if(dbinfo.migrated){
//			RemoteManager.getActiveDBConnectionName(dbinfo.customTagDB,DB_Config.setDetailHeader);
//		}
//		else{
			DB_Config.setDetailHeader(dbinfo.connectionName);
//		}
		
		$('#bigQueryTablesDiv').load('resources/bigQueryTables.html');
	},
	
	callFillNameSpace : function (dwrResponse)
	{
		if (dwrResponse.taskSuccess)
			DB_Config.fillNameSpace(dwrResponse.id);
		else
			jAlert(dwrResponse.responseMessage,"Error");
	},
	
	setClear: function()
	{
		var form = document.getElementById('db_connection');
		form.title.disabled = false;
		form.title.value= 'NewConnection';
		form.url.value= '';
		form.isPrimary.value = DB_Config.isPrimary;
		form.type.value = '';
		DB_Config.isMetaStore = true;
		if (DB_Config.isPrimary == 'false')
			DB_Config.isMetaStore = false;
		form.username.value= '';
		form.passwd.value= '';
		form.driver.value= '';
		form.driverJar.value = '';

		$('#jarFileText').val('');
		dwr.util.byId('driverJar').style.display = '';
		dwr.util.byId('jarFileText').style.display = 'none';
		dwr.util.byId('chooseFile').style.display = 'none';
	},
	
	AddNewDB : function()
	{
			DB_Config.newConnection = true;
			DB_Config.setClear();
			DB_Config.status = 'Save';
    		$('#SaveUpdate').html('Save');
    		DB_Config.selectedDBId ='';
			DB_Config.populateLeftTable(DB_Config.aaData);
			$('#selectedTable').attr('disabled','true');
	},
	
	InsertDBDetails: function()
	{
		var form = document.getElementById('db_connection');
		if(form.url.value==''||form.url.value==null){
			jAlert("URL for database connection missing.","Incomplete Details");
			
		}
		else if(form.username.value==''||form.username.value==null){
			jAlert("Username for database connection missing.","Incomplete Details");
			
		}
		else if(form.passwd.value==null){
			jAlert("URL for database connection missing.","Incomplete Details");
			
		}
		else if(form.driver.value==''||form.driver.value==null){
			jAlert("Driver for database connection missing.","Incomplete Details");
			
		}
		else if(form.driverJar.value==''||form.driverJar.value==null){
			jAlert("jar File required for database connection missing.","Incomplete Details");
			
		}
		else if(form.maxConnections.value=='' || form.maxConnections.value==null){
			jAlert("Maximum Connections required for database connection missing.","Incomplete Details");
			
		}
		else if(form.maxIdleConnections.value=='' || form.maxIdleConnections.value==null){
			jAlert("Maximum Idle Connections required for database connection missing.","Incomplete Details");
			
		}
		else if(form.waitTimeMilliSeconds.value=='' || form.waitTimeMilliSeconds.value==null){
			jAlert("Wait Time required for database connection missing.","Incomplete Details");
			
		}
		else
		{
			if(!DB_Config.isSubmitFromNN)
				form.connectionName.value = DB_Config.newConnectionName;
			
			form.type.value = DB_Config.newConnectionType;
			form.operation.value = DB_Config.status;
			
			DB_Config.saveForm = form;
			var iframe = document.createElement("iframe");
	        iframe.setAttribute("id", "upload_iframe");
	        iframe.setAttribute("name", "upload_iframe");
	        iframe.setAttribute("width", "0");
	        iframe.setAttribute("height", "0");
	        iframe.setAttribute("border", "0");
	        iframe.setAttribute("style", "width: 0; height: 0; border: none;");
	           		var div_id = 'db_connection';
		            var responsetd=document.getElementById(div_id); 
		            responsetd.parentNode.appendChild(iframe);
		            window.frames['upload_iframe'].name = "upload_iframe";
		            var iframeId = document.getElementById("upload_iframe");
		            var eventHandler = function () {
		                    if (iframeId.detachEvent) iframeId.detachEvent("onload", eventHandler);
		                    else iframeId.removeEventListener("load", eventHandler, false);
		                    if (iframeId.contentDocument) {
		                        content = iframeId.contentDocument.body.textContent;
		                    } else if (iframeId.contentWindow) {
		                        content = iframeId.contentWindow.document.body.textContent;
		                    } else if (iframeId.document) {
		                        content = iframeId.document.body.textContent;
		                    }
		                    DB_Config.response = content;
		                    if(DB_Config.isSubmitFromNN){
		                    	$('#formTr').css("display","none");
		                    	$('#responseDivTr').css("display","");
		                    	DB_Config.fillResponsePopup();
		                    }else{
		                    	 var status = "";
		                    	 if(content.indexOf("failed") == -1)
		                    		 status = "Success";
		                    	 else
		                    		 status = "Error";
		                    	 jAlert(content , status);
		                    	 DB_Config.closeBox(true);
//		                    	Util.addLightbox("addDBConnection","resources/save_newConnection.html");
		                    }
		             
		                }
		         
		            if (iframeId.addEventListener) iframeId.addEventListener("load", eventHandler, true);
		            if (iframeId.attachEvent) iframeId.attachEvent("onload", eventHandler);
			
			form.setAttribute("target", "upload_iframe");
	        form.setAttribute("action", "AddDBConnection.do");
	        form.setAttribute("method", "POST");
	        form.setAttribute("enctype", "multipart/form-data");
	        form.setAttribute("encoding", "multipart/form-data");
	        form.submit();
		}
       
	},
	
	
	SaveUpdateDB : function()
	{
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm("This will modify Database Configuration. Do you want to continue?","Modify Database Configuration",function(confirm){
			if(confirm)
			{
				if(DB_Config.status == "Save")
				{
					DB_Config.InsertDBDetails();
				}
				else
				{
					DB_Config.UpdateDBDetails();
				}
			}
			else
			{
				return;
			}
			jQuery.alerts.okButton = 'Ok';
			jQuery.alerts.cancelButton  = 'Cancel';
		});
	},
	
	UpdateDBDetails: function()
	{
		var form = document.getElementById('db_connection');
		if(form.url.value==''||form.url.value==null){
			jAlert("URL for database connection missing.","Incomplete Details");
			
		}
		else if(form.username.value==''||form.username.value==null){
			jAlert("Username for database connection missing.","Incomplete Details");
			
		}
		else if(form.passwd.value==null){
			jAlert("URL for database connection missing.","Incomplete Details");
			
		}
		else if(form.driver.value==''||form.driver.value==null){
			jAlert("Deiver for database connection missing.","Incomplete Details");
			
		}
		else if(form.maxConnections.value=='' || form.maxConnections.value==null){
			jAlert("Maximum Connections required for database connection missing.","Incomplete Details");
			
		}
		else if(form.maxIdleConnections.value=='' || form.maxIdleConnections.value==null){
			jAlert("Maximum Idle Connections required for database connection missing.","Incomplete Details");
			
		}
		else if(form.waitTimeMilliSeconds.value=='' || form.waitTimeMilliSeconds.value==null){
			jAlert("Wait Time required for database connection missing.","Incomplete Details");
			
		}
		else
		{
			var form = document.getElementById('db_connection');
			form.connectionName.value = DB_Config.selectedDBId;
			form.operation.value = DB_Config.status;
			DB_Config.saveForm = form;
			var iframe = document.createElement("iframe");
	        iframe.setAttribute("id", "upload_iframe");
	        iframe.setAttribute("name", "upload_iframe");
	        iframe.setAttribute("width", "0");
	        iframe.setAttribute("height", "0");
	        iframe.setAttribute("border", "0");
	        iframe.setAttribute("style", "width: 0; height: 0; border: none;");
	           		var div_id = 'db_connection';
		            var responsetd=document.getElementById(div_id); 
		            responsetd.parentNode.appendChild(iframe);
		            window.frames['upload_iframe'].name = "upload_iframe";
		            var iframeId = document.getElementById("upload_iframe");
		            var eventHandler = function () {
		                    if (iframeId.detachEvent) iframeId.detachEvent("onload", eventHandler);
		                    else iframeId.removeEventListener("load", eventHandler, false);
		                    if (iframeId.contentDocument) {
		                        content = iframeId.contentDocument.body.textContent;
		                    } else if (iframeId.contentWindow) {
		                        content = iframeId.contentWindow.document.body.textContent;
		                    } else if (iframeId.document) {
		                        content = iframeId.document.body.textContent;
		                    }
		                    DB_Config.response = content;
		                    var status = "";
	                    	 if(content.indexOf("failed") == -1)
	                    		 status = "Success";
	                    	 else
	                    		 status = "Error";
	                    	 jAlert(content , status);
	                    	 DB_Config.closeBox(true);
//		                    Util.addLightbox("addDBConnection","resources/save_newConnection.html");
		                }
		         
		            if (iframeId.addEventListener) iframeId.addEventListener("load", eventHandler, true);
		            if (iframeId.attachEvent) iframeId.attachEvent("onload", eventHandler);
			
			form.setAttribute("target", "upload_iframe");
	        form.setAttribute("action", "AddDBConnection.do");
	        form.setAttribute("method", "POST");
	        form.setAttribute("enctype", "multipart/form-data");
	        form.setAttribute("encoding", "multipart/form-data");
	        form.submit();
		}
	},
	
	/*
	callForUpdate: function()
	{
		RemoteManager.updateConnection(DB_Config.selectedDBId, DB_Config.connectionURL, DB_Config.username, DB_Config.password, DB_Config.driverName, DB_Config.driverJar,
				DB_Config.secondaryConnectionURL, DB_Config.secondaryUsername, DB_Config.secondaryPassword, DB_Config.secondaryDriverJar, DB_Config.secondaryDriverName,
				DB_Config.fillUpdateStatus);
	},
	
	fillUpdateStatus : function(resp)
	{
		var id = "Update";
		dwr.util.byId('popup.image.processing'+id).style.display = 'none';
		dwr.util.setValue('popup.message'+id,resp.responseMessage);
		dwr.util.setValue('popup.status'+id,resp.taskSuccess?'Success':'Failed');
		if(!resp.taskSuccess)
		{
			dwr.util.byId('popup.image.fail'+id ).style.display = '';
		}
		else
		{
			dwr.util.byId('popup.image.success'+id).style.display = '';
		}
		dwr.util.byId('ok.popup').disabled = false;
	},
	
	*/
	selectMigrateDestinationDB: function()
	{
		Util.addLightbox("addDBConnection","resources/migrate_db.html",null,null);
	},
	
	migrateDB: function()
	{
		RemoteManager.getNameNodeForDBNameMapping(DB_Config.selectedDBId, DB_Config.confirmUserAboutOpearion);
	},
	
	migrateDBImpl : function()
	{
		if($('#sourceName').val()==""){
			jAlert("Source database not entered for migration.Please enter a source database for migration process.","Error");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		if($('#destinationConnection').val()==""||$('#destinationConnection').val()==null){
			jAlert("Destination database not selected for migration.Please select a destination database for migration process.","Error");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		DB_Config.isCreateDDL = $("#createSchemaFlag").is(':checked');
		DB_Config.status = "Migrate";
		DB_Config.destinationName = document.getElementById('destinationConnection').value;
		
		Util.removeLightbox('addDBConnection');
		
		Util.addLightbox("addDBConnection","pages/popup.jsp");
	},
   
	fillSourceName: function()
	{
		var form = document.getElementById("CommonForm");
		form.sourceName.value=DB_Config.selectedDBId;
		if(DB_Config.selectedDBId==""||DB_Config.selectedDBId==null){
			jAlert("Source database is not selected for migration process.Please select a source database from list.","Error");
			$("#popup_container").css("z-index","99999999");
			return;
		}
//		RemoteManager.getNameNodeForDBNameMapping(DB_Config.selectedDBId, DB_Config.callFillNameSpace);
	},
	
	confirmUserAboutOpearion : function(dwrResponse){
		
		if (dwrResponse.taskSuccess)
		{
			namenode = dwrResponse.id;
			if(namenode!="" && namenode!=null)
			{
				jQuery.alerts.okButton = ' Yes ';
				jQuery.alerts.cancelButton  = ' No';
				jConfirm(" Selected database "+DB_Config.selectedDBId +" is configured as data source for "+namenode+". You would not be able to perform any operation on this cluster while migration is in progress. Do you want to proceed?.","Confirm",function(flag){
					if(flag)
					{
						DB_Config.migrateDBImpl();
					}
					else {
						Util.removeLightbox('addDBConnection');
					}
				});
				$("#popup_container").css("z-index","99999999");
				jQuery.alerts.okButton = ' Ok ';
				jQuery.alerts.cancelButton  = ' Cancel';
			}
			else
			{
				DB_Config.migrateDBImpl();
			}
		}
		else
			jAlert(dwrResponse.responseMessage,"Error");
	},
	
	callForMigrate: function()
	{
		RemoteManager.migrateDB(DB_Config.selectedDBId,DB_Config.destinationName, DB_Config.isCreateDDL , DB_Config.fillMigrateStatus);
	},
	
	fillMigrateStatus : function(resp)
	{
		var id = "Migrate";
		dwr.util.byId('popup.image.processing'+id).style.display = 'none';
		dwr.util.setValue('popup.message'+id,resp.responseMessage);
		dwr.util.setValue('popup.status'+id,resp.taskSuccess?'Success':'Failed');
		if(!resp.taskSuccess){
			dwr.util.byId('popup.image.fail'+id ).style.display = '';
		}
		else{
			dwr.util.byId('popup.image.success'+id).style.display = '';
		}
		dwr.util.byId('ok.popup').disabled = false;
		document.getElementById("ok.popup").onclick = function (){DB_Config.moveToMigrationStatus(); return false;};
		Navbar.refreshView();
		Navbar.refreshNavBar();
	},
	
	showDestinationDB: function()
	{
		RemoteManager.getAllConnectionsNameForOperation(DB_Config.fillmigratedDBNames);
	},
//	fillDBNameMapping : funcion(list){
//		DB_Config.dbNameNotToUsed=list;
//		RemoteManager.getAllConnectionsNameForOperation(DB_Config.fillmigratedDBNames);
//	},
	fillmigratedDBNames : function(list){
		DB_Config.migratedDBName = list;
//		RemoteManager.getAllConnectionsName(DB_Config.addDestination);
		RemoteManager.getAllConnectionsNameForNameNode(true, DB_Config.addDestination);
	},
	
	addDestination: function(list)
	{
		var selectBox = document.getElementById('destinationConnection');
//		for(var i=0;i<list.length;i++)
		
		
		for(var item in list)
		{
			if(list[item] == DB_Config.selectedDBId)
			{
				list.splice(list.indexOf(list[item]),1);
				continue;
			}
			for(var j=0;j<DB_Config.migratedDBName.length;j++){
				var index = DB_Config.migratedDBName[j].indexOf(list[item]);
				if(index!=-1&&DB_Config.migratedDBName[j][2]=='RUNNING'){
					list.splice(list.indexOf(list[item]),1);
					
				}
//				DB_Config.addOption(selectBox,item,item);
			}
		}
		for(var item in list)
		{
			DB_Config.addOption(selectBox,list[item],list[item]);
		}
	},
	
	addOption: function(selectbox, value, text)
	{
		var optn = document.createElement("OPTION");
		optn.text = text;
		optn.value = value;
		selectbox.options.add(optn);
	},
	
	
	RemoveDBConnection: function()
	{
		if(DB_Config.newConnection && DB_Config.selectedDBId == '')
		{
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm("New Connection added is not saved. Do you want to delete?", "Remove DB Connection", function(valueReturned){
				if(valueReturned)
				{
					DB_Config.newConnection = false;
					Navbar.refreshView();
				}
			});
			jQuery.alerts.okButton = ' Ok ';
			jQuery.alerts.cancelButton  = ' Cancel';
			return;
		}
		if (DB_Config.selectedNameNode!="" && DB_Config.selectedNameNode!=null){
			jAlert("Selected DB Connection is configured with the Namespace " + DB_Config.selectedNameNode + ". Remove the dependency to remove the connection.","Alert");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		else if(DB_Config.status == "Save"){
			jAlert("Selected DB Connection does not exist.","Alert");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		else{
			if(DB_Config.selectedDBId==""||DB_Config.selectedDBId==null){
				jAlert("No Database is selected.Please select a database from list to delete.","Alert");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm("Are you sure you want to remove database connection '"+DB_Config.selectedDBId+"'?","Remove DB Connection",function(flag){
				if(flag){
					DB_Config.status = "Remove";
					Util.addLightbox("addDBConnection","pages/popup.jsp");	
				}
			});
			jQuery.alerts.okButton = ' Ok ';
			jQuery.alerts.cancelButton  = ' Cancel';
			$("#popup_container").css("z-index","99999999");
		}
	},

	exportDDL: function()
	{
		if(DB_Config.status == "Save"){
			jAlert("Selected DB Connection does not exist.","Alert");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		else{
			if(DB_Config.selectedDBId=="" || DB_Config.selectedDBId==null){
				jAlert("No Database is selected. Please select a database from list to export Schema.","Alert");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';

			DB_Config.status = "exportDDL";
			Util.addLightbox("addDBConnection","pages/popup.jsp");	
			
			$("#popup_container").css("z-index","99999999");
		}
	},

	
	callForRemove: function()
	{
		RemoteManager.getAllConnectionsNameForOperation(DB_Config.validateDBNameBeforeRemove);
		
	},
	validateDBNameBeforeRemove : function(list){
		
		DB_Config.migratedDBName = list;
		var flag =true;
		for(var i=0;i<DB_Config.migratedDBName.length;i++){
			var index = DB_Config.migratedDBName[i].indexOf(DB_Config.selectedDBId);
			if(index!=-1&&DB_Config.migratedDBName[i][2]=='RUNNING'){
				flag = false;
				break;
			}
		}
		if(flag){
			RemoteManager.removeConnection(DB_Config.selectedDBId,DB_Config.fillRemoveStatus);
		}else{
			
			var id = "Remove";
			dwr.util.byId('popup.image.processing'+id).style.display = 'none';
			dwr.util.setValue('popup.message'+id,"Cannot remove Database "+DB_Config.selectedDBId+" because it is currently used in migration with other database.");
			dwr.util.setValue('popup.status'+id,'Failed');
			dwr.util.byId('popup.image.fail'+id ).style.display = '';
			dwr.util.byId('ok.popup').disabled = false;
			Navbar.refreshView();
			Navbar.refreshNavBar();
		}
		
		
	},
	
	callForExportDDL: function()
	{
		RemoteManager.getAllConnectionsNameForOperation(DB_Config.validateDBNameBeforeExportDDL);
		
	},
	validateDBNameBeforeExportDDL : function(list){
		
		DB_Config.migratedDBName = list;
		var flag =true;
		for(var i=0;i<DB_Config.migratedDBName.length;i++){
			var index = DB_Config.migratedDBName[i].indexOf(DB_Config.selectedDBId);
			if(index!=-1&&DB_Config.migratedDBName[i][2]=='RUNNING'){
				flag = false;
				break;
			}
		}
		if(flag){
			RemoteManager.exportDDL(DB_Config.selectedDBId,DB_Config.fillExportDDLStatus);
		}else{
			
			var id = "exportDDL";
			dwr.util.byId('popup.image.processing'+id).style.display = 'none';
			dwr.util.setValue('popup.message'+id,"Cannot Export Database "+DB_Config.selectedDBId+" because it is currently used in migration with other database.");
			dwr.util.setValue('popup.status'+id,'Failed');
			dwr.util.byId('popup.image.fail'+id ).style.display = '';
			dwr.util.byId('ok.popup').disabled = false;
		}
		
		
	},
	
	fillExportDDLStatus : function(resp)
	{
		var id = "exportDDL";
		dwr.util.byId('popup.image.processing'+id).style.display = 'none';
		if(resp.taskSuccess)
		{
			$('#log_div'+id).html('<a target="_blank" href="DDLDownloadServlet?fileName=' + resp.responseMessage + '">Download DDL Script</a>');
			document.getElementById('log_div'+ id).style.display="block";
			dwr.util.setValue('popup.message' + id,'Successfully Generated DDL Script.');
		}
		else
		{
			dwr.util.setValue('popup.message'+id, resp.responseMessage);
		}
		dwr.util.setValue('popup.status'+id,resp.taskSuccess?'Success':'Failed');
		if(!resp.taskSuccess){
			dwr.util.byId('popup.image.fail'+id ).style.display = '';
		}
		else{
			dwr.util.byId('popup.image.success'+id).style.display = '';
		}
		dwr.util.byId('ok.popup').disabled = false;
	},
	
	fillRemoveStatus : function(resp)
	{
		
		var id = "Remove";
		dwr.util.byId('popup.image.processing'+id).style.display = 'none';
		dwr.util.setValue('popup.message'+id,resp.responseMessage);
		dwr.util.setValue('popup.status'+id,resp.taskSuccess?'Success':'Failed');
		if(!resp.taskSuccess){
			dwr.util.byId('popup.image.fail'+id ).style.display = '';
		}
		else{
			dwr.util.byId('popup.image.success'+id).style.display = '';
		}
		dwr.util.byId('ok.popup').disabled = false;
		Navbar.refreshView();
		Navbar.refreshNavBar();
	},
	
	AddNewConnection: function(){
		if(!DB_Config.newConnection)
		{
			Util.addLightbox("addDBConnection","resources/add_Connection.html",null, null);
		}
		else
		{
			jAlert("New Connection Added is not saved. Please save the New Connection first.","NewConnection not saved.");
			$("#popup_container").css("z-index","99999999");
		}
		
	},
	
	selectTask : function()
	{
		if($("#actionDC").val() == "Add")
			DB_Config.AddNewConnection();
		else if($("#actionDC").val() == "Delete")
			DB_Config.RemoveDBConnection();
		else if($("#actionDC").val() == "Migrate")
			DB_Config.selectMigrateDestinationDB();
		else if($("#actionDC").val() == "Export Schema")
			DB_Config.exportDDL();
		else if($("#actionDC").val() == "Configure Namespace")
			DB_Config.linkDbToNamespace();
		
		$("#actionDC").prop('selectedIndex',0);
	},
	
	linkDbToNamespace : function()
	{
		Util.addLightbox("addDBConnection","resources/configureNamespace.html",null, null);
	},
	
	fillAllNameNode :function()
	{
		RemoteManager.getAllNameNodesForDBMapped(DB_Config.isMetaStore, DB_Config.fillNameNodeDropDown);
	},
	
	fillNameNodeDropDown : function(list)
	{
		var opt ='<option value="">Select NameNode</option>';
		if(list!=null && list!=undefined){
			
			for(var i=0;i<list.length;i++){
				opt+='<option value="'+list[i]+'">'+list[i]+'</option>';
			}
		}
		$('#nameNodeId').html(opt);
		if (DB_Config.selectedNameNode != '')
		{
			$('#chooseNameNode').css("display","");
			$('#nameNodeId').css("display","none");
			$('#nameNodeIdText').css("display","");
			$('#nameNodeIdText').val(DB_Config.selectedNameNode);
		}
		else
		{
			$('#chooseNameNode').css("display","none");
		}
	},
	
	showChooseNameNode : function()
	{
		if (DB_Config.isChangeShownMapping)
		{
			$('#nameNodeId').css("display","");
			$('#nameNodeIdText').css("display","none");
			$('#chooseNameNode').val('Keep Unchanged');
			DB_Config.isChangeShownMapping = false;
		}
		else
		{
			$('#nameNodeId').css("display","none");
			$('#nameNodeIdText').css("display","");
			$('#chooseNameNode').val('Change');
			DB_Config.isChangeShownMapping = true;
		}
	},
	
	saveConfigNamespace : function()
	{
		if (dwr.util.byId('nameNodeId').value == 0)
		{
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm('NameNode not selected for database. You will not be able to perform Big Data Analytics on this database. ' +
					'Are you sure you want to continue?','Incomplete Detail',function(val)
			{
				if (val)
				{
					RemoteManager.updateDbAssociated(DB_Config.selectedNameNode, "", DB_Config.isMetaStore, DB_Config.configNameNodeUpdated);
					DB_Config.selectedNameNode = "";
				}
				else
					return;
			});
			$("#popup_container").css("z-index","9999999");
			jQuery.alerts.okButton = ' Ok ';
			jQuery.alerts.cancelButton  = ' Cancel ';
		}
		else
		{
			DB_Config.selectedNameNode = $('#nameNodeId').val();
			RemoteManager.updateDbAssociated(DB_Config.selectedNameNode, DB_Config.selectedDBId, DB_Config.isMetaStore, DB_Config.configNameNodeUpdated);
		}
	},

	configNameNodeUpdated : function(dwrResponse)
	{
		var message = dwrResponse.responseMessage;
		if (dwrResponse.taskSuccess)
		{
			jAlert("Restart NameNode to implement the changes done in configuration for database.","Successfully saved");
		}
		else
		{
			jAlert("Updation of configuration failed. " + message,"Updation failed");
		}
		
		DB_Config.closeBox(true);
		DB_Config.fillNameSpace(DB_Config.selectedNameNode);
	},
	
	saveDBName: function(){
		
		if($("#connName").val() == ""){
			jAlert("You must specify the Database connection name.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		else if($("#dbType").val() == "0")
		{
			jAlert("You must specify the Database type.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		var form = document.getElementById('AddForm');
		var flag = true;
		
		for(var i=0;i<DB_Config.aaData.length;i++){
			if(DB_Config.aaData[i].Name  == form.connectionName.value)
			{
				flag = false;
				jAlert("DB Name Already exists.","Alert");
				$("#popup_container").css("z-index","99999999");
			}
		}
		if(flag){
			DB_Config.newConnectionName = form.connectionName.value;
			DB_Config.isPrimary = form.dbRole.value;
			DB_Config.newConnectionType = form.dbType.value;
			var role = "Metadata / Tagging";
			DB_Config.isMetaStore = true;
			if (DB_Config.isPrimary == 'false')
			{
				role = "Hive Schema";
				DB_Config.isMetaStore = false;
			}
			DB_Config.aaData[DB_Config.aaData.length] = ({Name : form.connectionName.value, Role : role, Type : form.dbType.value});
			$('#detail_header').html('Database Configuration ('+form.connectionName.value+')');
			Util.removeLightbox('addDBConnection');
			DB_Config.AddNewDB();
		}
		else{
			return;
		}
	},
	
	activateDB: function(){
		DB_Config.status = "Activate";
		Util.addLightbox("addDBConnection","pages/popup.jsp");
	},
	
	callforActivate: function(){
//		RemoteManager.activateDBConnection(DB_Config.selectedDBId,DB_Config.fillActivateStatus);
	},
	
//	fillActivateStatus: function(resp)
//	{
//		var id = "Activate";
//		dwr.util.byId('popup.image.processing'+id).style.display = 'none';
//		dwr.util.setValue('popup.message'+id,resp.responseMessage);
//		dwr.util.setValue('popup.status'+id,resp.taskSuccess?'Success':'Failed');
//		if(!resp.taskSuccess){
//			dwr.util.byId('popup.image.fail'+id ).style.display = '';
//		}
//		else{
//			dwr.util.byId('popup.image.success'+id).style.display = '';
//		}
//		dwr.util.byId('ok.popup').disabled = false;
//		Navbar.refreshView();
//		Navbar.refreshNavBar();
//	},
	
	
	fillResponsePopup : function ()
    {
		var content = DB_Config.response;
    	var status;
    	var imgId = "";
    	dwr.util.byId('pop.pattern').style.display = '';
    	if (content.indexOf("Failed") != -1)
    	{
    		status = "Failure";
			imgId = "popup.image.fail";
    	}
    	else
    	{
    		status = "Success";
			imgId = "popup.image.success";
    	}
    	dwr.util.byId('popup.image.processing').style.display = 'none';
		dwr.util.byId(imgId).style.display = '';
		dwr.util.setValue('popup.host1', DB_Config.selectedDBId);
		dwr.util.setValue('popup.message1', content);
		dwr.util.setValue('popup.status1', status);
		dwr.util.byId('ok.popup').disabled = false;
	},
	
	setDatabaseNames : function(list, id)
	{
		var data = "";
		
		if(list != null)
		{
			for(var i=0; i<list.length; i++)
				data += "<option value = '" + list[i] + "'>" + list[i] + "</option>";
		}
		else
			data += "<option value = '0'>--No database--</option>";
				
		$("#" + id).html(data);
	}
};