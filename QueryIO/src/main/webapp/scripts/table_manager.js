TM={
		colMap : new Object(),
		queries : new Array(),
		selectedNameNode: '',
		colMap : new Object(),
		currentType: '',
		selectedNameNode: '',
		selectedTableArray: [],
		tableArray: [],
		totalTable: 0,
		tableCache: {},
		isEditTable: false,
		isNewTable: false,
		selectedTableId: '',
		queries: [],
		queryInfo: {},
		selectedGroupHeaderArray: [],
		selectedGroupFooterArray: [],
		searchColumn: [],
		selectedColumnHeaderArray: [],
		selectedColumnFooterArray: [],
		selectedColumnDetailArray: [],
		colList: [],
		
		ready : function(){
			TM.selectedNameNode = $("#queryIONameNodeId").val();
			if(TM.selectedNameNode==""||TM.selectedNameNode==null){
				$('#table_list_table_div').html('There is no Namespace configured currently. Please setup a cluster and import data to cluster to use Query and Analysis features.');
				$('#table_list_table_div').css('text-align','center');
				return;
			}
			TM.populateTableSummaryTable();
		},
		
		readyTable : function(){
			TM.populateQueries();
		},
		
		populateTableSummaryTable : function(){
			$('#table_list_table').dataTable( {
	   			"sScrollX": "100%",
	   			"bPaginate": true,
				"bLengthChange": true,
				"sPaginationType": "full_numbers",
//				"aLengthMenu" : [50, 100, 200, 500 ],
				"bFilter": false,
				"bSort": true,
				"bInfo": true,
				"bDestroy": true,
				"serverSide": true,
				"searching": true,
				"aoColumnDefs": [{ "bSortable": false, "aTargets": [ 0 ] }],
				"fnServerData": function ( sSource, aoData, fnCallback, oSettings ) {
					RemoteManager.getAllTablesInfo({
			            async:false,
			            callback:function(result){
			                 fnCallback(TM.fillTableSummaryTable(result));	
			                 $(window).trigger('resize');
			                }});
					
					},
				"bAutoWidth": true,
		        "aoColumns": [
		            { "sTitle": '<input type="checkbox" value="selectAll" id="selectAll" onclick="javascript:TM.selectAllTables(this.id)">'},
		            { "sTitle": "TableID" },
		            { "sTitle": "Description" },
		            { "sTitle": "Query" }
		        ]
		    } );
			if($('#table_list_table tbody tr td').hasClass('dataTables_empty'))
				document.getElementById('selectAll').disabled = true;
			else
				document.getElementById('selectAll').disabled = false;
			
			$('#table_list_table_length').css('margin-top', 7 + 'px');
			$('#table_list_table_length').css('margin-bottom', 7 + 'px');
			$('#table_list_table_filter').css('margin-top', 7 + 'px');
			$('#table_list_table_filter').css('margin-bottom', 7 + 'px');
			
			
		},
		
		fillTableSummaryTable : function(tableData){
			
			TM.selectedTableArray.splice(0, TM.selectedTableArray.length);
			document.getElementById('selectAll').checked = false;
			TM.toggleButton("selectAll", false);
			var object = tableData["data"];
			var tableList = new Array();
			var tCache = new Object;
			if(object!=null)
	   		{
				TM.tableArray.splice(0,TM.tableArray.length);
					for (var i=0; i< object.length;i++)
					{
						var tData = object[i];
						var tableId = tData[0]; // queryID
						var description = tData[1]; // query description
						var query = tData[2]; // query description
						var tablec = {};
						tablec["id"] = tableId;
						tablec["description"] = description;
						tablec["query"] = query;
						
						TM.totalTable++;
			   			TM.tableArray.push(tableId);
						var check='<input type="checkbox" id="'+tableId+'" onclick="javascript:TM.clickBox(this.id);">';
						
						tCache[tableId] = tablec;
//						if(status=="SUCCESS"){
//							var path='Reports/Birt'+query["reportpath"];
						tableId='<a href = "javascript:TM.showTable(\''+tableId+'\');">'+tableId+"</a>";
//						}
						var queryContent = '<a href = "javascript:TM.showQuery(\''+query+'\');">'+query+"</a>";
						tableList.push([check,tableId,description,queryContent]);
		   			}
		  	}
			tableData["data"] = tableList;
			TM.tableCache = tCache;
			return tableData;
		},
		
		clickBox : function(id)
		{
			var flag = document.getElementById(id).checked;
			if (flag == true)
			{
				TM.selectedTableArray.push(id.toString());
			}
			else
			{
				var index = jQuery.inArray(id.toString(), TM.selectedTableArray);
				if (index != -1)
				{
					TM.selectedTableArray.splice(index, 1);
				}
			}
			if(($('#table_list_table tr').length - 1) == TM.selectedTableArray.length)
			{
				document.getElementById("selectAll").checked = flag;
				TM.selectAllTable("selectAll", flag);
			}
			else
				TM.toggleButton(id, flag, "selectAll");
		},
  		selectAllTable : function(id)
   		{

  			var flag = document.getElementById(id).checked;
  			
  			TM.selectedTableArray.splice(0, TM.selectedTableArray.length);
  			for (var i=0; i<TM.tableArray.length; i++)
  			{
  				document.getElementById(TM.tableArray[i]).checked = flag;
  				if (flag)
  				{	
  					TM.selectedTableArray.push(TM.tableArray[i]);
  				}
  			}
  			TM.toggleButton(id, flag);
   		},
   		toggleButton : function(id , value)
   		{

   			if (id == "selectAll")
   			{
   				if (TM.selectedTableArray.length == 1)
   				{
   					dwr.util.byId('tableEdit').disabled=false;
   					dwr.util.byId('tableClone').disabled=false;
   				}
   				else
   				{
   					dwr.util.byId('tableClone').disabled=true;
   					dwr.util.byId('tableEdit').disabled=true;
   				}
   				dwr.util.byId('tableDelete').disabled=!value;
   				
   			}
   			else
   			{
				if(value == false)
					$('#selectAll').attr("checked",false);
				
				if (TM.selectedTableArray.length < 1)
				{
					dwr.util.byId('tableClone').disabled=true;
   					dwr.util.byId('tableEdit').disabled=true;
   					dwr.util.byId('tableDelete').disabled=true;
				}
				else
				{
					if (TM.selectedTableArray.length == 1)
					{
						dwr.util.byId('tableEdit').disabled=false;
	   					dwr.util.byId('tableClone').disabled=false;
					}
					else
					{
						dwr.util.byId('tableClone').disabled=true;
	   					dwr.util.byId('tableEdit').disabled=true;
					}
					dwr.util.byId('tableDelete').disabled=false;
				}
   				
   		
   			}
   		},
		addNewTable : function(){
			TM.isEditTable = false;
			TM.isNewTable = true;
			Navbar.isAddNewTable=true;
			Navbar.isFromsummaryView = true;
			Navbar.changeTab('Tables','tables','edit_tables');
		},
		editSelectedTable : function(){
			Navbar.isEditTable = true;
			var tableId = TM.selectedTableArray[0]+'';
			Navbar.selectedTableId = tableId;
			Navbar.isFromsummaryView = true;
			Navbar.changeTab('Tables','tables','edit_tables');
		},
		backToSummary : function(){
			$('#refreshViewButton').attr('onclick','javascript:Navbar.refreshView()');
			Navbar.refreshView();
		},
		showQuery: function(queryId){
			Navbar.isEditQuery = true;
			Navbar.selectedQueryId = queryId;
			Navbar.isFromsummaryView = true;
			Navbar.changeTab('QueryDesigner','analytics','QueryDesigner');
		},
		
		cloneSelectedTable : function(){
			
			TM.selectedTableId = TM.selectedTableArray[0]
			
			Util.addLightbox("addclone", "resources/cloneTable.html", null, null);
		},
		fillCloneTableObject : function(object){
			
			TM.tableInfo =jQuery.extend(true, {},object);
		},
		deleteSelectedTable : function(){
			TM.selectedTableId = TM.selectedTableArray[0];
			TM.deleteTable();
		},
		
		deleteTable : function() {
			
		},
		
		populateDeleteTableBox : function(){
			
			var tableIdArray  = TM.selectedTableArray;
			for (var i = 0; i <tableIdArray.length ; i++)
			{
				var id = tableIdArray[i];
				dwr.util.cloneNode('pattern',{ idSuffix:id });
				dwr.util.setValue('table' + id,id);
				dwr.util.setValue('message' + id,"Perform delete operation on table "+id);
				dwr.util.setValue('status' + id,'Deleting');
				dwr.util.byId('pattern' + id).style.display = '';
				
			}
			var nameNode = $('#queryIONameNodeId').val();
			
			for (var i = 0; i <tableIdArray.length ; i++)
			{
				var id = tableIdArray[i];
				RemoteManager.deleteTable(id, TM.processDeleteTableResponse);
				
			}
		},
		processDeleteTableResponse : function(dwrResponse){
			
			var id=dwrResponse.id;
			if(dwrResponse.taskSuccess){
				img_src='images/Success_img.png'
				status = 'Success'; 
				dwr.util.byId('imageSuccess' + id).style.display = '';
				var userId = $('#loggedInUserId').text();
				var obj =  Util.getCookie("last-visit-chart"+userId);
				var idInfoObj = null;
				if(obj != null && obj != undefined){
					var filePathObj = JSON.parse(obj);
					idInfoObj = JSON.parse(Util.getCookie("last-visit-idInfoMap"+userId));
					for (var i in idInfoObj)
					{
			    		if (idInfoObj[i] == id)
			    		{ 
			    			
			    			delete filePathObj[i];
						    delete idInfoObj[i];
						    
						    var userId = $('#loggedInUserId').text();
							Util.setCookie("last-visit-idInfoMap"+userId,JSON.stringify(idInfoObj), 15);
			    		}
					}
				}
				
				
			}
			else{
				img_src='images/Fail_img.png'
				status = 'Fail';
				dwr.util.byId('imageFail' + id).style.display = '';
				var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
				document.getElementById('log_div'+ id).innerHTML=log;
				document.getElementById('log_div'+ id).style.display="block";
			}
			dwr.util.byId('imageProcessing' + id).style.display = 'none';
			dwr.util.setValue('message' + id,dwrResponse.responseMessage);
			dwr.util.setValue('status' + id,status);
			document.getElementById('okbtn').disabled = false;
			Navbar.selectedTableId = '';
//			Navbar.refreshNavBar();
			
			
		},
		showTable : function(){
			Navbar.selectedTableId= TM.selectedTableArray[0];
			Navbar.isEditTable=true;
			
			Navbar.changeTab('Tables','tables','edit_tables');
			
		},
		closeDeleteTableBox : function(){
			TM.closeBox(true);
		},
		
		closeBox : function(value) {
			
		},
		
		fillExecuteTab : function(obj){
			
		},
		
		populateQueries : function() {
			RemoteManager.getAllQueriesInfo(function (response) {
				var object = JSON.parse(response["responseMessage"]);
				if(object!=null)
		   		{
					TM.queries = object;
					for (queryData in TM.queries)
					{
						var cur = object[queryData];
						console.log("cur: ", cur)
						var queryId = cur["id"]; // queryID
						$('#selectQueryId').append('<option value="' + queryId + '">' + queryId + '</option>');
					}
		   		}
			});
		},
		
		queryIdChanged : function() {
			var queryId = $('#selectQueryId').val();
			console.log(queryId);
			for (queryData in TM.queries)
			{
				var cur = TM.queries[queryData];
				var thisQueryId = cur["id"];
				console.log("thisQueryId: " + thisQueryId);
				if(queryId == thisQueryId){
					var thisColMap = cur["selectedCols"];
					TM.colMap = JSON.parse(thisColMap);
					TM.populateColumns();
				}
			}
			
		},
		
		setGroupHeader : function(colName, isChecked) {
			for (var i = 0; i < TM.selectedGroupHeaderArray.length; i++) {
				if (TM.selectedGroupHeaderArray[i] == colName) {
					TM.selectedGroupHeaderArray.splice(i, 1);
				}
			}
			if (isChecked) {
				TM.selectedGroupHeaderArray.push(colName);
				$(document.getElementById('group_header_prefix' + colName))
						.removeAttr('disabled');
				$(document.getElementById('header_' + colName)).removeAttr(
						'disabled');
				$(document.getElementById('group_header_suffix' + colName))
						.removeAttr('disabled');
				$(document.getElementById('group_header_style' + colName))
						.removeAttr('disabled');
			} else {

				$(document.getElementById('group_header_prefix' + colName)).attr(
						'disabled', 'disabled');
				$(document.getElementById('header_' + colName)).attr('disabled',
						'disabled');
				;
				$(document.getElementById('group_header_suffix' + colName)).attr(
						'disabled', 'disabled');
				$(document.getElementById('group_header_style' + colName)).attr(
						'disabled', 'disabled');
				$(document.getElementById('group_header_style_val' + colName))
						.attr('disabled', 'disabled');

				if (TM.queryInfo["groupHeader"] != undefined
						&& TM.queryInfo["groupHeader"] != null
						&& TM.queryInfo["groupHeader"] != "") {
					delete TM.queryInfo["groupHeader"][colName];
				}
			}

			if (TM.selectedGroupHeaderArray.length == 0) {
				$("#nextViewCSSGenerator").attr('disabled', 'disabled');
			} else {
				$("#nextViewCSSGenerator").removeAttr('disabled');
			}

			TM.setGroupHeaderInJSON();

		},

		setGroupFooter : function(colName, isChecked) {
			for (var i = 0; i < TM.selectedGroupFooterArray.length; i++) {
				if (TM.selectedGroupFooterArray[i] == colName) {
					TM.selectedGroupFooterArray.splice(i, 1);
				}
			}
			if (isChecked) {
				TM.selectedGroupFooterArray.push(colName);

				$(document.getElementById('group_footer_prefix' + colName))
						.removeAttr('disabled');
				$(document.getElementById('footer_' + colName)).removeAttr(
						'disabled');
				$(document.getElementById('group_footer_suffix' + colName))
						.removeAttr('disabled');
				$(document.getElementById('group_footer_style' + colName))
						.removeAttr('disabled');
				$(document.getElementById('group_footer_style_val' + colName))
						.removeAttr('disabled');
			} else {
				$(document.getElementById('group_footer_prefix' + colName)).attr(
						'disabled', 'disabled');
				$(document.getElementById('footer_' + colName)).attr('disabled',
						'disabled');
				$(document.getElementById('group_footer_suffix' + colName)).attr(
						'disabled', 'disabled');
				$(document.getElementById('group_footer_style' + colName)).attr(
						'disabled', 'disabled');
				$(document.getElementById('group_footer_style_val' + colName))
						.attr('disabled', 'disabled');
				if (TM.queryInfo["groupFooter"] != undefined
						&& TM.queryInfo["groupFooter"] != null
						&& TM.queryInfo["groupFooter"] != "") {
					delete TM.queryInfo["groupFooter"][colName];
				}
			}

			if (TM.selectedGroupFooterArray.length == 0) {
				$("#nextViewCSSGenerator").attr('disabled', 'disabled');
			} else {
				$("#nextViewCSSGenerator").removeAttr('disabled');
			}

			TM.setGroupFooterInJSON();

		},

		setGroupHeaderInJSON : function() {
			var prefix = '';
			var suffix = '';
			var func = '';
			if (TM.queryInfo["groupHeader"] == undefined
					|| TM.queryInfo["groupHeader"] == null
					|| TM.queryInfo["groupHeader"] == "") {
				TM.queryInfo["groupHeader"] = new Object();
			}
			for (var i = 0; i < TM.selectedGroupHeaderArray.length; i++) {
				var colName = TM.selectedGroupHeaderArray[i];
				if (TM.queryInfo["groupHeader"][colName] == undefined
						|| TM.queryInfo["groupHeader"][colName] == null) {
					TM.queryInfo["groupHeader"][colName] = new Object();
				}

				var val = $(
						document.getElementById('group_header_prefix' + colName))
						.val();
				val = $('input[id="group_header_prefix' + colName + '"]').val();
				TM.queryInfo["groupHeader"][colName]["prefix"] = $(
						document.getElementById('group_header_prefix' + colName))
						.val();
				var header_func = $(document.getElementById('header_' + colName))
						.val();
				if (header_func == undefined || header_func == null) {
					header_func = ""
				}
				TM.queryInfo["groupHeader"][colName]["function"] = header_func;
				TM.queryInfo["groupHeader"][colName]["suffix"] = $(
						document.getElementById('group_header_suffix' + colName))
						.val();
				if (TM.queryInfo["groupHeader"][colName]["style"] == undefined
						|| TM.queryInfo["groupHeader"][colName] == null) {
					TM.queryInfo["groupHeader"][colName]["style"] = new Object();
					TM.queryInfo["groupHeader"][colName]["style"]["border"] = "1px solid #DDD";
				}
				var styleKey = $(
						document.getElementById('group_header_style' + colName))
						.val();
				TM.queryInfo["groupHeader"][colName]["style"][styleKey] = $(
						document.getElementById('group_header_style_val' + colName))
						.val();
			}
			$('#group_header_col').val(JSON.stringify(TM.queryInfo["groupHeader"]));

		},
		setGroupFooterInJSON : function() {
			var prefix = '';
			var suffix = '';
			var func = '';
			if (TM.queryInfo["groupFooter"] == undefined
					|| TM.queryInfo["groupFooter"] == null
					|| TM.queryInfo["groupFooter"] == "") {
				TM.queryInfo["groupFooter"] = new Object();
			}
			for (var i = 0; i < TM.selectedGroupFooterArray.length; i++) {
				var colName = TM.selectedGroupFooterArray[i];
				if (TM.queryInfo["groupFooter"][colName] == undefined
						|| TM.queryInfo["groupFooter"][colName] == null) {
					TM.queryInfo["groupFooter"][colName] = new Object();
				}
				var footer_func = $(document.getElementById('footer_' + colName))
						.val();
				if (footer_func == undefined || footer_func == null) {
					footer_func = "";
				}
				TM.queryInfo["groupFooter"][colName]["function"] = footer_func;
				TM.queryInfo["groupFooter"][colName]["prefix"] = $(
						document.getElementById('group_footer_prefix' + colName))
						.val();
				TM.queryInfo["groupFooter"][colName]["suffix"] = $(
						document.getElementById('group_footer_suffix' + colName))
						.val();
				if (TM.queryInfo["groupFooter"][colName]["style"] == undefined
						|| TM.queryInfo["groupFooter"][colName]["style"] == null) {
					TM.queryInfo["groupFooter"][colName]["style"] = new Object();
					TM.queryInfo["groupFooter"][colName]["style"]["border"] = "1px solid #DDD";
				}
				var styleKey = $(
						document.getElementById('group_footer_style' + colName))
						.val();
				TM.queryInfo["groupFooter"][colName]["style"][styleKey] = $(
						document.getElementById('group_footer_style_val' + colName))
						.val();
			}
			$('#group_footer_col').val(JSON.stringify(TM.queryInfo["groupFooter"]));

		},
		
		populateColumns : function() {
			console.log(TM.colMap);
			// Do something with this colmap
			for(col in TM.colMap){
				TM.searchColumn.push(col);
				TM.colList.push(col);
				TM.setGroupFooter(col, true);
				TM.setGroupHeader(col, true);
				TM.setColumnHeaderProperty(col, true);
				TM.setColumnDetailProperty(col, true);
			}
			
		},
		
	   	getAggregateFunctionDropDownForGroupFooter : function(colName) {
			if (TM.colList.indexOf(colName) == -1) {
				return "";
			}

			var dataType = TM.colMap[colName];
			var data = '<select id="footer_'
					+ colName
					+ '"  style="width:100%;" onchange="javascript:TM.setGroupFooterInJSON();" disabled="disabled">';
			data += '<option value=""></option>';
			data += '<option value="COUNT">COUNT</option>';
			data += '<option value="DistinctCount">DISTINCT COUNT</option>';
			if (dataType.toUpperCase() == 'INTEGER'
					|| dataType.toUpperCase() == 'LONG'
					|| dataType.toUpperCase() == 'SHORT'
					|| dataType.toUpperCase() == 'DOUBLE') {
				data += '<option value="SUM">SUM</option>';
				data += '<option value="MIN">MIN</option>';
				data += '<option value="MAX">MAX</option>';
				data += '<option value="AVG">AVG</option>';
			}
			data += '</select>';
			return data;

		},
		
		getAggregateFunctionDropDownForGroupHeader : function(colName) {
			if (TM.colList.indexOf(colName) == -1) {
				return "";
			}

			var dataType = TM.colMap[colName];
			var data = '<select id="header_'
					+ colName
					+ '"  style="width:100%;" onchange="javascript:TM.setGroupHeaderInJSON();" disabled="disabled">';
			data += '<option value=""></option>';
			data += '<option value="COUNT">COUNT</option>';
			if (dataType.toUpperCase() == 'INTEGER'
					|| dataType.toUpperCase() == 'DECIMAL') {
				data += '<option value="SUM">SUM</option>';
				data += '<option value="MIN">MIN</option>';
				data += '<option value="MAX">MAX</option>';
				data += '<option value="AVG">AVG</option>';
			}
			data += '</select>';
			return data;

		},
		
		createCSSGeneratorWizard : function(type) {
			TM.currentType = type;
			if (type == "columnHeader")
				TM.tempData = jQuery.extend(true, {},
						TM.queryInfo["colHeaderDetail"]);
			else if (type == "columnDetail")
				TM.tempData = jQuery.extend(true, {}, TM.queryInfo["colDetail"]);
			else
				TM.tempData = jQuery.extend(true, {}, TM.queryInfo[type]);

			Util.addLightbox("cssGeneratorWizard",
					"resources/css_generator_wizard.html", null, null);

		},
		
		setColumnHeaderProperty : function(colName, isChecked) {
			for (var i = 0; i < TM.selectedColumnHeaderArray.length; i++) {
				if (TM.selectedColumnHeaderArray[i] == colName) {
					TM.selectedColumnHeaderArray.splice(i, 1);
				}
			}
			if (isChecked) {
				TM.selectedColumnHeaderArray.push(colName);
				$('#columnHeaderProperty_' + colName).removeAttr('disabled');
				$('#column_header_title' + colName).removeAttr('disabled');
			} else {
				$('#columnHeaderProperty_' + colName).attr('disabled', 'disabled');
				$('#column_header_title' + colName).attr('disabled', 'disabled');
				delete TM.queryInfo["colHeaderDetail"][colName];
			}
			TM.setColumnHeaderPropInJSON();

		},
		
		setColumnHeaderPropInJSON : function() {

			var styleKey = '';
			var styleValue = '';

			if (TM.queryInfo["colHeaderDetail"] == undefined
					|| TM.queryInfo["colHeaderDetail"] == null) {
				TM.queryInfo["colHeaderDetail"] = TM.getDefaultHeaderColumnJSON();
			}
			for (var i = 0; i < TM.selectedColumnHeaderArray.length; i++) {
				var colName = TM.selectedColumnHeaderArray[i];

				if (TM.queryInfo["colHeaderDetail"][colName] == undefined
						|| TM.queryInfo["colHeaderDetail"][colName] == null) {
					TM.queryInfo["colHeaderDetail"][colName] = TM
							.getDefaultHeaderColumnJSONForCol(colName);
					TM.queryInfo["colHeaderDetail"][colName]["border"] = "1px solid #DDD";
				}

				// var styleKey =$('#columnHeaderProperty_'+colName).val();
				// var styleVal = $('#column_header_prop'+colName).val();
				var colTitle = $('#column_header_title' + colName).val();

				if (colTitle == "") {
					delete TM.queryInfo["colHeaderDetail"][colName]["title"];
				} else {
					TM.queryInfo["colHeaderDetail"][colName]["title"] = colTitle;

				}

			}
			$('#column_header_col').val(
					JSON.stringify(TM.queryInfo["colHeaderDetail"]));

		},
		
		setColumnDetailProperty : function(colName, isChecked) {
			for (var i = 0; i < TM.selectedColumnDetailArray.length; i++) {
				if (TM.selectedColumnDetailArray[i] == colName) {
					TM.selectedColumnDetailArray.splice(i, 1);
				}
			}
			if (isChecked) {
				TM.selectedColumnDetailArray.push(colName);
				$('#columnDetailProperty_' + colName).removeAttr('disabled');
				$('#column_detail_prop' + colName).removeAttr('disabled');
			} else {
				$('#columnDetailProperty_' + colName).attr('disabled', 'disabled');
				$('#column_detail_prop' + colName).attr('disabled', 'disabled');
				delete TM.queryInfo["colDetail"][colName];
			}
			TM.setColumnDetailPropInJSON();

		},
		getColumnDetailCSSStyleDropDown : function(colName) {
			if (TM.colList.indexOf(colName) == -1) {
				return "";
			}

			var dataType = TM.colMap[colName];
			var data = '<select id="columnDetailProperty_'
					+ colName
					+ '"  style="width:100%;" onchange="javascript:TM.setColumnDetailPropInJSON();" disabled="disabled">';
			data += '<option value=""></option>';
			data += '<option value="background-color">background-color</option>';
			data += '<option value="font">font</option>';
			data += '<option value="width">width</option>';

			data += '</select>';
			return data;
		},
		getHeaderCSSStyleDropDown : function(colName) {
			if (TM.colList.indexOf(colName) == -1) {
				return "";
			}

			var dataType = TM.colMap[colName];
			var data = '<select id="columnHeaderProperty_'
					+ colName
					+ '"  style="width:100%;" onchange="javascript:TM.setColumnHeaderPropInJSON();" disabled="disabled">';
			data += '<option value=""></option>';
			data += '<option value="background-color">background-color</option>';
			data += '<option value="font">font</option>';
			data += '<option value="width">width</option>';

			data += '</select>';
			return data;
		},
		
		setColumnDetailPropInJSON : function() {
			var styleKey = '';
			var styleValue = '';

			if (TM.queryInfo["colDetail"] == undefined
					|| TM.queryInfo["colDetail"] == null) {
				TM.queryInfo["colDetail"] = new Object();
			}
			for (var i = 0; i < TM.selectedColumnDetailArray.length; i++) {
				var colName = TM.selectedColumnDetailArray[i];

				if (TM.queryInfo["colDetail"][colName] == undefined
						|| TM.queryInfo["colDetail"][colName] == null) {
					TM.queryInfo["colDetail"][colName] = new Object();
					TM.queryInfo["colDetail"][colName]["border"] = "1px solid #DDD";

				}

				var styleKey = $('#columnDetailProperty_' + colName).val();
				var styleVal = $('#column_detail_prop' + colName).val();
				TM.queryInfo["colDetail"][colName][styleKey] = styleVal;

			}

			$('#column_detail_col').val(JSON.stringify(TM.queryInfo["colDetail"]));

		},
		getCSSStyleDropDown : function(colName, idPrefix, onChangeFunc) {
			if (TM.colList.indexOf(colName) == -1) {
				return "";
			}

			var dataType = TM.colMap[colName];
			var data = '<select id="' + idPrefix + colName
					+ '"  style="width:100%;" onchange="javascript:' + onChangeFunc
					+ ';" disabled="disabled">';
			data += '<option value=""></option>';
			data += '<option value="background-color">background-color</option>';
			data += '<option value="font">font</option>';
			data += '<option value="width">width</option>';

			data += '</select>';
			return data;
		},
		
		getDefaultHeaderColumnJSON : function() {

			var header = new Object();
			this.colMap;
			for ( var attr in TM.colMap) {
				var colObject = new Object();
				colObject["title"] = attr;
				colObject["color"] = "333333";
				colObject["text-align"] = "center";
				colObject["font-size"] = "10px";
				colObject["font-style"] = "normal";
				colObject["background-color"] = "69abdb";
				colObject["font-family"] = "Arial";
				colObject["font-weight"] = "bold";
				colObject["format"] = {};
				if (TM.colMap[attr].toUpperCase() == 'INTEGER'
						|| TM.colMap[attr].toUpperCase() == 'LONG') {
					colObject["width"] = "70px";
				} else if (TM.colMap[attr].toUpperCase() == 'DOUBLE') {
					colObject["width"] = "120px";
				} else if (TM.colMap[attr].toUpperCase() == 'STRING') {
					colObject["width"] = "100px";
				} else if (TM.colMap[attr].toUpperCase() == 'TIMESTAMP') {
					colObject["width"] = "125px";
				} else if (TM.colMap[attr].toUpperCase() == 'SHORT') {
					colObject["width"] = "40px";
				} else {
					colObject["width"] = "100px";
				}
				if (attr.toUpperCase() == 'FILEPATH') {
					colObject["width"] = "250px";
				}
				header[attr] = colObject;
			}
			return header;
		},
		getDefaultHeaderColumnJSONForCol : function(colName) {
			var colObject = new Object();
			colObject["title"] = colName;
			colObject["color"] = "#2E6E9E";
			colObject["text-align"] = "center";
			colObject["font-size"] = "12px";
			colObject["font-style"] = "normal";
			colObject["background-color"] = "#90c7f1";
			colObject["font-family"] = "Arial";
			colObject["font-weight"] = "bold";
			colObject["format"] = {};

			if (TM.colMap[colName].toUpperCase() == 'INTEGER') {
				colObject["width"] = "70px";
			} else if (TM.colMap[colName].toUpperCase() == 'DOUBLE') {
				colObject["width"] = "120px";
			} else if (TM.colMap[colName].toUpperCase() == 'STRING') {
				colObject["width"] = "100px";
			} else if (TM.colMap[colName].toUpperCase() == 'TIMESTAMP') {
				colObject["width"] = "125px";
			} else if (TM.colMap[colName].toUpperCase() == 'SHORT') {
				colObject["width"] = "40px";
			} else {
				colObject["width"] = "100px";
			}
			if (colName.toUpperCase() == 'FILEPATH') {
				colObject["width"] = "250px";
			}
			return colObject;
		},

};