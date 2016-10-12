DataBrowser = {
	selectedFileId : '',
	SelectedFileArray : [],
	selectedFileTypes : [],
	selectedNameNode : '',
	overallSelectedType : '',
	ready : function() {
		DataBrowser.selectedNameNode = $('#queryIONameNodeId').val();

		var height = ($(window).height() - 160);
		if (height < 510)
		{
			$("#service_ref").height('510px');
		}
		else
		{
			$("#service_ref").height(height);
		}
		
		$("#jqgrid_container").height(
				$("#service_ref").height()
						- ($("#data_browser_path_bar").height() + 65));

		DataBrowser.populateTable();
		DataBrowser.setConfigPermissionState(false);
		
		jQuery(window).bind('resize', function() 
		{
			DataBrowser.resizeGrid();
		}).trigger('resize');
	},
	
	resizeGrid : function() 
	{
		$("#data_browser_table").setGridWidth(($("#jqgrid_container").width() - 3), true);
	},
	
	setSelectedFileId : function(id) {
		$('#data_browser_table >tbody >tr').removeClass('rowSelector');
		this.selectedFileId = id;
		$('#' + id).addClass('ui-state-highlight');
		// $('#'+id).attr('style', '{background-color:"red"}');

//		DataBrowser.setConfigPermissionState(true);
	},
	setConfigPermissionState : function(isEnable) {

		if (isEnable) {
			// $('#configPerm').attr('disabled');
			$('#configPer').removeAttr("disabled");
			$('#configTag').removeAttr("disabled");
		} else {
			$('#configPer').attr("disabled", "disabled");
			$('#configTag').attr("disabled", "disabled");
			
		}
	},
	createCheckBox : function(cellvalue, options, rowObject) {
		return '<input type="checkbox"'
				+ 'onclick="javascript:DataBrowser.clickBox(this.id);" id="kasd" style="margin-top: 5px;""/>';

	},
	populateTable : function() {
		if (document.getElementById('data_browser_table') == undefined
				|| document.getElementById('data_browser_table') == null)
			return;
		DataBrowser.selectedNameNode = $('#queryIONameNodeId').val();
		var path = "/";
		var wdth = ($("#jqgrid_container").width() - 45) / 10;
		jQuery("#data_browser_table")
				.jqGrid(
						{
							url : 'databrowser.do?q=1&dirPath=' + path
									+ '&nodeId=' + DataBrowser.selectedNameNode,
							datatype : "json",
							colNames : [
									'<input type="checkbox" id="selectAll" onclick="javascript:DataBrowser.selectAll(this.id);" style=" float: left;"> ',
									'Name', 'Kind', 'Size', 'Replicas', 'Last Read',
									'Last Write', 'Permission', 'Owner',
									'Group','Compression','Encryption' ],
							colModel : [ {
								name : 'CheckBox',
								index : 'CheckBox',
								width : 35,
								align : 'left',
								sortable : false,
							}, {
								name : 'Name',
								index : 'Name',
								sortable : true,
								width : wdth,
								resizable : true
							}, {
								name : 'Kind',
								index : 'Kind',
								sortable : false,
								hidden : true
							}, {
								name : 'Size',
								index : 'Size',
								sortable : false,
								width : wdth,
								resizable : true
							}, {
								name : 'Replicas',
								index : 'Replicas',
								sortable : true,
								width : wdth,
								resizable : true
							}, {
								name : 'Last Read',
								index : 'Last_Read',
								sortable : false,
								width : wdth,
								resizable : true
							}, {
								name : 'Last Write',
								index : 'Last_Write',
								sortable : false,
								width : wdth,
								resizable : true
							}, {
								name : 'Permission',
								index : 'Permission',
								sortable : true,
								width : wdth,
								resizable : true
							}, {
								name : 'Owner',
								index : 'Owner',
								sortable : true,
								width : wdth,
								resizable : true
							}, {
								name : 'Group',
								index : 'Group',
								sortable : true,
								width : wdth,
								resizable : true
							},
							{
								name : 'Compression',
								index : 'Compression',
								sortable : true,
								width : wdth,
								resizable : true
							},
							{
								name : 'Encryption',
								index : 'Encryption',
								sortable : true,
								width : wdth,
								resizable : true
							}
							],
							altRows : false,
							onSelectRow : function(id) {
								DataBrowser.setSelectedFileId(id);
							},
							rowNum : 50,
							rowList : [ 50, 100, 200, 500, 1000 ],
							pager : "#pager",
							height : ($("#jqgrid_container").height() - 45),
							width : ($("#jqgrid_container").width() - 3),
							shrinkToFit : true,
							viewrecords : true,
							sortable : true,
							multiselect : false,
							caption : "",
							onSortCol : function(index, idxcol, sortorder) {
								// data_analyzer_table");
								if (this.p.lastsort >= 0
										&& this.p.lastsort !== idxcol
										&& this.p.colModel[this.p.lastsort].sortable !== false) {
									$(this.grid.headers[this.p.lastsort].el)
											.find(
													">div.ui-jqgrid-sortable>span.s-ico")
											.show();
									$(this.grid.headers[this.p.lastsort].el)
											.removeClass('ui-state-highlight');
								}
								$(this.grid.headers[idxcol].el).addClass(
										'ui-state-highlight');
							},
						}).navGrid('#pager', {
					add : false,
					edit : false,
					del : false,
					search : false,
					refresh : false
				});
		$('#jqgh_CheckBox').removeClass("ui-jqgrid-sortable");

		$('#alertmod').remove();
		$('#search_data_browser_table').remove();
		DataBrowser.setFirstRowSelected();
		DataBrowser.fillNameNodes();
		DataBrowser.setConfigPermissionState(false);
		DataBrowser.SelectedFileArray = [];
		DataBrowser.SelectedFileTypes = [];
		$('#deleteFile').attr('disabled', 'disabled');
		DataBrowser.showTotalFileCount(DataBrowser.selectedNameNode,path);
	},

	decodeURI : function(str) {
		return decodeURIComponent((str + '').replace(/\+/g, '%20'));
	},
	
	filePathForTags : function(path)
	{
		path = path + "  ";
		dirPath = path;
		var linkStr = "";
		if (path != "/  ") {
			var split = path.split("/");
			var temp = "";
			if(split.length > 2)
				temp = "/";
			for ( var i = 1; i < split.length-1; i++) 
			{
				if (i != 1)
					temp += "/";
				temp += split[i].substring(2, split[i].length-2);
			}
			temp += "/" + split[split.length - 1].substring(2, split[split.length - 1].length-2);
			dirPath = temp + "/";
		} 
		else
			dirPath = "/";
//			dirPath = path;
		return dirPath + DataBrowser.SelectedFileArray[0];
	},
	
	listFiles : function(path) {

		$('#data_browser_table').remove();
		$('#pager').remove();
		var wdth = ($("#jqgrid_container").width() - 40) /10;
		$('#jqgrid_container')
				.html(
						'<table id="data_browser_table"></table><div id="pager"></div>');
		dirPath = path;
		var linkStr = '<b>Contents of directory:</b> &nbsp;&nbsp;&nbsp;<a style="text-decoration:none" href="javascript:DataBrowser.listFiles(\''
				+ '/' + '\')">/</a>';
		if (path != "/") {
			var split = path.split("/");
			var temp = "/";
			for ( var i = 1; i < (split.length - 1); i++) {
				if (i != 1)
					temp += "/";
				temp += split[i];
				linkStr += '  <a style="text-decoration:none" href="javascript:DataBrowser.listFiles(\''
						+ temp + '\')">' + DataBrowser.decodeURI(split[i]) + '</a>  /';
			}
			linkStr += "  " + DataBrowser.decodeURI(split[split.length - 1]);
			dirPath = path + "/";
		} else {
			dirPath = path;
		}
		
		$("#data_browser_path_bar_text").html(linkStr);
		
		DataBrowser.selectedNameNode = $('#queryIONameNodeId').val();
		
		jQuery("#data_browser_table")
				.jqGrid(
						{
							url : 'databrowser.do?q=1&dirPath=' + dirPath
									+ '&nodeId=' + DataBrowser.selectedNameNode,
							datatype : "json",
							colNames : [
									'<input type="checkbox" id="selectAll" onclick="javascript:DataBrowser.selectAll(this.id);" style=" float: left; "> ',
									'Name', 'Kind', 'Size', 'Replicas', 'Last Read',
									'Last Write', 'Permission', 'Owner',
									'Group','Compression','Encryption' ],
							colModel : [ {
								name : 'CheckBox',
								index : 'CheckBox',
								width : 35,
								align : 'left'
							}, {
								name : 'Name',
								index : 'Name',
								sortable : true,
								width : wdth,
								resizable : true
							}, {
								name : 'Kind',
								index : 'Kind',
								sortable : false,
								hidden : true
							}, {
								name : 'Size',
								index : 'Size',
								sortable : false,
								width : wdth,
								resizable : true
							}, {
								name : 'Replicas',
								index : 'Replicas',
								sortable : true,
								width : wdth,
								resizable : true
							}, {
								name : 'Last Read',
								index : 'Last_Read',
								sortable : false,
								width : wdth,
								resizable : true
							}, {
								name : 'Last Write',
								index : 'Last_Write',
								sortable : false,
								width : wdth,
								resizable : true
							}, {
								name : 'Permission',
								index : 'Permission',
								sortable : true,
								width : wdth,
								resizable : true
							}, {
								name : 'Owner',
								index : 'Owner',
								sortable : true,
								width : wdth,
								resizable : true
							}, {
								name : 'Group',
								index : 'Group',
								sortable : true,
								width : wdth,
								resizable : true
							},
							{
								name : 'Compression',
								index : 'Compression',
								sortable : true,
								width : wdth,
								resizable : true
							},
							{
								name : 'Encryption',
								index : 'Encryption',
								sortable : true,
								width : wdth,
								resizable : true
							} ],
							altRows : false,
							onSelectRow : function(id) {
								DataBrowser.setSelectedFileId(id);
							},
							rowNum : 50,
							rowList : [ 50, 100, 200, 500, 1000 ],
							pager : "#pager",
							height : ($("#jqgrid_container").height() - 20),
							width : ($("#jqgrid_container").width() - 3),
							shrinkToFit : false,
							viewrecords : true,
							sortable : true,
							multiselect : false,
							caption : "",
							onSortCol : function(index, idxcol, sortorder) {
								// data_analyzer_table");
								if (this.p.lastsort >= 0
										&& this.p.lastsort !== idxcol
										&& this.p.colModel[this.p.lastsort].sortable !== false) {
									$(this.grid.headers[this.p.lastsort].el)
											.find(
													">div.ui-jqgrid-sortable>span.s-ico")
											.show();
									$(this.grid.headers[this.p.lastsort].el)
											.removeClass('ui-state-highlight');
								}
								$(this.grid.headers[idxcol].el).addClass(
										'ui-state-highlight');
							},

						}).navGrid('#pager', {
					add : false,
					edit : false,
					del : false,
					search : false,
					refresh : false
				});

		$('#jqgh_CheckBox').removeClass("ui-jqgrid-sortable");
		DataBrowser.setFirstRowSelected();
		DataBrowser.fillNameNodes();
		DataBrowser.setConfigPermissionState(false);
		DataBrowser.SelectedFileArray = [];
		DataBrowser.SelectedFileTypes = [];
		$('#deleteFile').attr('disabled', 'disabled');
		DataBrowser.showTotalFileCount(DataBrowser.selectedNameNode,dirPath);
	},

	refreshDataTable : function() {
		$('#data_browser_table').remove();
		$('#pager').remove();
		$('#jqgrid_container')
				.html(
						'<table id="data_browser_table"></table><div id="pager"></div>');
		var wdth = ($("#jqgrid_container").width() - 40) / 10;
		DataBrowser.selectedNameNode = $('#queryIONameNodeId').val();
		jQuery("#data_browser_table")
				.jqGrid(
						{
							url : 'databrowser.do?q=1&dirPath=' + dirPath
									+ '&nodeId=' + DataBrowser.selectedNameNode,
							datatype : "json",
							colNames : [
									'<input type="checkbox" id="selectAll" onclick="javascript:DataBrowser.selectAll(this.id);" style=" float: left; "> ',
									'Name', 'Kind', 'Size', 'Replicas', 'Last Read',
									'Last Write', 'Permission', 'Owner',
									'Group','Compression','Encryption' ],
							colModel : [ {
								name : 'CheckBox',
								index : 'CheckBox',
								width : 35,
								align : 'left'
							}, {
								name : 'Name',
								index : 'Name',
								sortable : true,
								width : wdth,
								resizable : true
							}, {
								name : 'Kind',
								index : 'Kind',
								sortable : false,
								hidden : true
							}, {
								name : 'Size',
								index : 'Size',
								sortable : false,
								width : wdth,
								resizable : true
							}, {
								name : 'Replicas',
								index : 'Replicas',
								sortable : true,
								width : wdth,
								resizable : true
							}, {
								name : 'Last Read',
								index : 'Last_Read',
								sortable : false,
								width : wdth,
								resizable : true
							}, {
								name : 'Last Write',
								index : 'Last_Write',
								sortable : false,
								width : wdth,
								resizable : true
							}, {
								name : 'Permission',
								index : 'Permission',
								sortable : true,
								width : wdth,
								resizable : true
							}, {
								name : 'Owner',
								index : 'Owner',
								sortable : true,
								width : wdth,
								resizable : true
							}, {
								name : 'Group',
								index : 'Group',
								sortable : true,
								width : wdth,
								resizable : true
							},{
								name : 'Compression',
								index : 'Compression',
								sortable : true,
								width : wdth,
								resizable : true
							},
							{
								name : 'Encryption',
								index : 'Encryption',
								sortable : true,
								width : wdth,
								resizable : true
							} ],
							altRows : false,
							onSelectRow : function(id) {
								DataBrowser.setSelectedFileId(id);
							},
							rowNum : 50,
							rowList : [ 50, 100, 200, 500, 1000 ],
							pager : "#pager",
							height : ($("#jqgrid_container").height() - 20),
							width : ($("#jqgrid_container").width() - 3),
							shrinkToFit : false,
							viewrecords : true,
							sortable : true,
							multiselect : false,
							caption : "",
							onSortCol : function(index, idxcol, sortorder) {
								if (this.p.lastsort >= 0
										&& this.p.lastsort !== idxcol
										&& this.p.colModel[this.p.lastsort].sortable !== false) {
									$(this.grid.headers[this.p.lastsort].el)
											.find(
													">div.ui-jqgrid-sortable>span.s-ico")
											.show();
									$(this.grid.headers[this.p.lastsort].el)
											.removeClass('ui-state-highlight');
								}
								$(this.grid.headers[idxcol].el).addClass(
										'ui-state-highlight');
							},

						}).navGrid('#pager', {
					add : false,
					edit : false,
					del : false,
					search : false,
					refresh : false
				});
		// $('.ui-pg-input').css('height','20px');
		// $('.ui-widget-content ui-corner-all').css('font-size','9pt');
		// $('.ui-jqgrid-bdiv').css('height','100%');
		// $('.ui-jqgrid-bdiv').css('min-height','510px');
		// $('#load_data_browser_table').css('display','none');
		// $('#alertmod').remove();
		// $('#search_data_browser_table').remove();
		// $('#pager').css('height','50px');
		$('#jqgh_CheckBox').removeClass("ui-jqgrid-sortable");
		DataBrowser.setFirstRowSelected();
		DataBrowser.fillNameNodes();
		DataBrowser.setConfigPermissionState(false);
		DataBrowser.SelectedFileArray = [];
		DataBrowser.SelectedFileTypes = [];
		$('#deleteFile').attr('disabled', 'disabled');
		DataBrowser.showTotalFileCount(DataBrowser.selectedNameNode,dirPath);
	},

	populatePermissionTable : function() {

		$('#permissionTable')
				.dataTable(
						{
							"bPaginate" : false,
							"bLengthChange" : true,
							"bFilter" : false,
							"bSort" : false,
							"bInfo" : false,
							"bAutoWidth" : false,
							"aaData" : [
									[
											'<span style="padding: 10%;">	<img alt="" src="images/owner.png" style="height: 15px;  width: 15px;"></span><select id="fileOwner" style="width: 100px;"></select>',
											'<select id="ownerPermission" style="width: 135px;"></select>' ],
									[
											'<span style="padding: 8%;"><img alt="" src="images/groups.png" style="height: 15px;   width: 25px;"></span><select id="fileGroup" style="width: 100px;"></select>',
											'<select id="groupPermission" style="width: 135px;"></select>' ],
									[
											'<span style="padding: 8%;">	<img alt="" src="images/everyone.png" style="height: 15px;  width: 25px; "></span><select id="fileGroup" style="width: 100px;"><option>EveryOne</option></select>',
											'<select id="publicPermission" style="width: 135px;"></select>' ] ],
							"aoColumns" : [ {
								"sTitle" : "Name"
							}, {
								"sTitle" : "Privilege"
							} ]
						});

		RemoteManager.getAllUserNames(DataBrowser.fillAllUsers);
		RemoteManager.getAllGroupNames(DataBrowser.fillAllGroups);
		DataBrowser.showPermissionForFile();
	},

	fillAllUsers : function(list) {
		var data = '';
		if (list != undefined || list != null) {
			for ( var i = 0; i < list.length; i++) {
				data += '<option value="' + list[i] + '">' + list[i]
						+ '</option>';
			}
		}
		$('#fileOwner').html(data);
		var id = DataBrowser.selectedFileId;
		var owner = $('#data_browser_table').jqGrid('getCell', id, 'Owner');
		$('#fileOwner').val(owner);
	},

	fillAllGroups : function(list) {
		var data = '';
		if (list != undefined || list != null) {
			for ( var i = 0; i < list.length; i++) {
				data += '<option value="' + list[i] + '">' + list[i]
						+ '</option>';
			}
		}
		$('#fileGroup').html(data);
		var id = DataBrowser.selectedFileId;
		var group = $('#data_browser_table').jqGrid('getCell', id, 'Group');
		$('#fileGroup').val(group);

	},

	showPermissionForFile : function() {
		var id = DataBrowser.selectedFileId;
		var group = $('#data_browser_table').jqGrid('getCell', id, 'Group');
		var permission = $('#data_browser_table').jqGrid('getCell', id,
				'Permission');
		var fileElement = $('#data_browser_table').jqGrid('getCell', id, 'Name');
		var fileElementType = $('#data_browser_table').jqGrid('getCell', id, 'Kind');
		
		$('#selectedRowId').text(id);
		$('#fileElement').html(fileElement);
		$('#fileElementType').html(fileElementType);
		var name = $('#name').text();
		var kind = $('#kind').text();
		
		$('#filespan').text(name);
		$('#kindspan').text(kind);

		var permission_opt;
		if (kind == 'Directory') {
			$('#addRecPer').removeAttr("disabled");
			permission_opt = '<option value="7">read-write-execute</option>'
				+ '<option value="6">read-write</option>'
				+ '<option value="5">read-execute</option>'
				+ '<option value="4">read only</option>'
				+ '<option value="3">write-execute</option>'
				+ '<option value="2">write only</option>'
				+ '<option value="1">execute only</option>';
		}
		else {
			permission_opt = '<option value="6">read-write</option>'
				+ '<option value="4">read only</option>'
				+ '<option value="2">write only</option>';
		}
		
		
		$('#ownerPermission').html(permission_opt);
		permission_opt += '<option value="0">No Permission</option>';
		$('#groupPermission').html(permission_opt);
		$('#publicPermission').html(permission_opt);

		$('#permissionTable tr:first-child td').css('padding', '3px 0px 3px 10px');
		$('#permissionTable tr td').css('text-align', 'left');


		var permissionArray = [];
		for ( var i = 0; i < permission.length; i += 3) {
			permissionArray.push(permission.substring(i, i + 3));
		}
		this.setPermissionValue(permissionArray[0], 'ownerPermission');
		this.setPermissionValue(permissionArray[1], 'groupPermission');
		this.setPermissionValue(permissionArray[2], 'publicPermission');

	},

	setPermissionValue : function(permissionValue, id) {
		var val = 0;
		if (permissionValue.indexOf('r') != -1) {
			val += 4;
		} else {
			val += 0
		}
		if (permissionValue.indexOf('w') != -1) {
			val += 2;
		} else {
			val += 0
		}
		if (permissionValue.indexOf('x') != -1) {
			val += 1;
		} else {
			val += 0
		}
		$('#' + id).val(val);
	},
	
	editPermission : function() {
		$("#permissionDiv :input").attr("disabled", false);
	},

	cancelPermissionChanges : function() {
		DataBrowser.showPermissionForFile($('#selectedRowId').text());
	},

	saveNewPermission : function() {

		var filepath = dirPath + $('#filespan').text();
		var owner = $('#fileOwner').val();
		var group = $('#fileGroup').val();
		var permission = '' + 0 + $('#ownerPermission').val()
				+ $('#groupPermission').val() + $('#publicPermission').val();
		var isRecursive = document.getElementById('addRecPer').checked;

		var namenodeId = $('#db_namenode').val();
		
		RemoteManager.setOwnerAndPermissions(namenodeId, filepath,
				owner, group, parseInt(permission, 10), isRecursive,
				DataBrowser.handleSavePermission);
		
		DataBrowser.closePermissionBox();
	},
	
	handleSavePermission : function(dwrResponse) {

		jAlert(dwrResponse.responseMessage, "Configure Permissions");
		Navbar.refreshView();
	},
	showPermissionBox : function() {
		Util.addLightbox("permission", "resources/permission_box.html", null,
				null);
	},
	closePermissionBox : function() {
		Util.removeLightbox("permission");
	},
	setFirstRowSelected : function() {
		// $('#configPer').attr("disabled", "disabled");
		// return;
		//		
		// $("#data_browser_table").setSelection('1', true);
		// DataBrowser.setSelectedFileId(1);
		// var rowCount = $('#data_browser_table >tbody >tr').length;
		//		
		// if($('#1')==undefined){
		// $('#configPer').attr("disabled", "disabled");
		// }else{
		// $('#configPer').removeAttr("disabled");
		// }
		// $('#1').css('background-color', '#135');

	},
	// showUploaderBox : function(){
	// },
	fillNameNodes : function() {
		RemoteManager.getNonStandByNodes(DataBrowser.populateNameNodeIds);
	},
	populateNameNodeIds : function(list) {
		var data = '';
		for ( var i = 0; i < list.length; i++) {

			var node = list[i];
			if (i == 0) {
				if (isFirstTime) {
					DataBrowser.selectedNameNode = $('#queryIONameNodeId').val();
				}
			}
			data += '<option value="' + node.id + '">' + node.id + '</option>';
		}
		$('#db_namenode').html(data);
		DataBrowser.setNameNodeVal();
	},

	setNameNodeVal : function() {
		DataBrowser.selectedNameNode = $('#queryIONameNodeId').val();
		if (DataBrowser.selectedNameNode == null || DataBrowser.selectedNameNode == undefined) {
			DataBrowser.selectedNameNode = $("#db_namenode option:first").val();
			// :"+this.selectedNameNode);

		}
		if (isFirstTime) {
			isFirstTime = false;
			DataBrowser.showDataOfNameNode();
		}

		$("#db_namenode").val(DataBrowser.selectedNameNode);

	},
	showDataOfNameNode : function() {
		
		DataBrowser.refreshDataTable();

	},

	downloadFile : function(filePath, nodeID) {

		// var formData = new FormData();
		// formData.append( 'namenode', nodeID );
		// formData.append( 'filepath', filePath );
		// var xmlhttp=new XMLHttpRequest();
		// xmlhttp.open("GET","FileDownload",true);
		// //xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		// xmlhttp.send("namenode="+nodeID+"&filepath="+filePath);
		// $.ajax({
		// type: "GET",
		// url: "FileDownload?namenode="+nodeID+"&filePath="+filePath,
		// contentType: "text/plain",
		// // data: formData,
		// processData: false,
		// async:true,
		// });
		$.download('/"FileDownload', 'namenode=' + nodeID + '&filePath='
				+ filePath);

	},
	clickCheckBox : function(id, value) 
	{
		var fileName = $('#' + id).val();
		var idRow = id.toString().substring(7, id.toString().length);
		var fileElementType = $('#data_browser_table').jqGrid('getCell', idRow, 'Kind');
		
		var index = fileElementType.indexOf('kind');
		
		var kind;
		var $html = $('<div>',{html:fileElementType}).hide().appendTo('body');
		$html.find('span').each(function(i,e){
			kind = $(e).text();
		});
		$html.remove();
		
		if (value)
		{
			DataBrowser.SelectedFileArray.push(fileName);
			DataBrowser.SelectedFileTypes.push(kind);
			var rowId = id.substring(id.indexOf('_') + 1);
			DataBrowser.setSelectedFileId(rowId);
		} 
		else 
		{
			var index = DataBrowser.SelectedFileArray.indexOf(fileName);
			DataBrowser.SelectedFileArray.splice(index, 1);
			var indexFile = DataBrowser.SelectedFileTypes.indexOf(kind);
			DataBrowser.SelectedFileTypes.splice(indexFile, 1);
			$('#selectAll').removeAttr("checked");
		}
		var rows= $("#data_browser_table").jqGrid('getRowData').length;
		var selectedFiles = DataBrowser.SelectedFileArray.length;
		if (rows==selectedFiles)
			$("#selectAll").attr('checked', 'checked');
		else
			$('#selectAll').removeAttr("checked");
		
		DataBrowser.toggleButton(id, value);
	},
	
	toggleButton : function(id, value)
	{
		if (id == "selectAll")
		{
			$("#configPer").attr("disabled", true);
			$("#deleteFile").attr("disabled", !value);
			$("#configTag").attr("disabled", true);
			
		}
		else
		{
			if(value == false)
				$('#selectAll').attr("checked",false);
			
			if (DataBrowser.SelectedFileArray.length < 1)
			{
				$("#configPer").attr("disabled", true);
				$("#deleteFile").attr("disabled", true);
				$("#configTag").attr("disabled", true);
			}
			else
			{
				if (DataBrowser.SelectedFileArray.length == 1)
				{
					$("#configPer").attr("disabled", false);
					$("#configTag").attr("disabled", false);
				}
				else
				{
					$("#configPer").attr("disabled", true);				
					$("#configTag").attr("disabled", true);
				}
				$("#deleteFile").attr("disabled", false);
			}
		}
	},
	
	selectAll : function()
	{
		var val = $('#selectAll').attr("checked");
		DataBrowser.SelectedFileArray.splice(0, DataBrowser.SelectedFileArray.length);
		var buttonArray = $('#data_browser_table :input[type="checkbox"]');
		for ( var j = 0; j < buttonArray.length; j++) {
			var id = buttonArray[j].id;
			buttonArray[j].checked = val;
			DataBrowser.clickCheckBox(id, val);
		}
	},

	deleteFiles : function() 
	{
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';

		var cntFile = 0;
		var cntDir = 0;
		for(var i=0; i<DataBrowser.SelectedFileTypes.length; i++)
		{
			var type = DataBrowser.SelectedFileTypes[i];
			if(type == "File")
				cntFile++;
			else if(type == "Directory")
				cntDir++;
		}
		
		if(cntFile == 0)
			DataBrowser.overallSelectedType = "folder(s) and its contents";
		else if(cntDir == 0)
			DataBrowser.overallSelectedType = "file(s)";
		else
			DataBrowser.overallSelectedType = "file(s) and folder(s)";
		
		jConfirm('Are you sure you want to delete selected ' + DataBrowser.overallSelectedType + '?','Delete',function(val){
			 if (val== true){
				 
//				 var filepath = dirPath + DataBrowser.SelectedFileArray[0];
				 var isRecursive = false;
				 var namenodeId = $('#db_namenode').val();
				 
				 Util.addLightbox("deleteFiles", "pages/popup.jsp", null, null);
			 }
			 else
				 return ;
			 jQuery.alerts.okButton = ' Ok ';
			 jQuery.alerts.cancelButton  = ' Cancel';
		});
	},
	showDeleteResponse : function(dwrResponse) {
		var id = "delete";
		var imgId = "";
		var status = "";
		if(dwrResponse.taskSuccess)
		{
			imgId = "popup.image.success";
			status = "Success";
		}
		else
		{
			imgId = "popup.image.fail";
			status = "Failed";
		}
		dwr.util.setValue('popup.message' + id, dwrResponse.responseMessage);
		dwr.util.setValue('popup.status' + id, status);
		dwr.util.byId('popup.image.processing' + id).style.display = "none";
		dwr.util.byId(imgId + id).style.display = "";
		dwr.util.byId('ok.popup').disabled = false;
//		jAlert(dwrResponse.responseMessage, "Delete Files");
		Navbar.refreshView();
	},
	setSelectedRowId : function(ele) {
		// var rowId = element.closest('tr')
		//		
	},
	showTotalFileCount : function(nodeId,path){
		
		RemoteManager.getItemCount(nodeId,path,DataBrowser.setTotalFileValue);
	},
	setTotalFileValue : function(value){
		var data = '<table>';
		data+='<tr><td style=" padding-left: 10px;  ">Total Files & Directories: </td><td><span id="totalFileCount">'+value+'</span></td></tr>'
		data+='</table>';
		$('#pager_left').html(data);
	},
	showTagBox : function(){
		Util.addLightbox("tagBox", "resources/config_tag.html", null,
				null);
	}
	
};