QM={
		queryCache : new Object,
		totalQuery:1,
		isEditQuery :false,
		selectedNameNode : '',
		selectedQueryArray :new Array(),
		queryArray :new Array(),
		isNewQuery : false,
		selectedDbName : '',
		lastSelectedDbName : '',
		isHive : false,
		queryInfo : {},
		isColumnSelected : false,
		isFromSelected : false,
		isGroupBySelected : false,
		isHavingSelected : false,
		queryFilterObj : {},
		queryFilterColMap : {},
		checkForAdded : false,
		selectedTableSchema : {},
		isTableSelectedByUser : true,
		selectedHistoryObj : {},
		isSetQueryRequest : false,
		colMap : {},
		selectedWhereArray : [],
		selectedHavingArray : [],
		searchColumn : [],
		searchFrom : null,
		query : '',
		columnsForCurrentFromSelection : [],
		colList : [],
		selectedQueryId : '',
		isDelete : false,
		isClone : false,
		isFirstTime : false,
		currentExecutionId : '',
		lastColumn : '',
		isHistoryFilled : false,
		tableMap : new Object(),
		
		ready : function(){
			QM.selectedNameNode = $("#queryIONameNodeId").val();
			if(QM.selectedNameNode==""||QM.selectedNameNode==null){
				$('#query_list_table_div').html('There is no Namespace configured currently. Please setup a cluster and import data to cluster to use Query and Analysis features.');
				$('#query_list_table_div').css('text-align','center');
				return;
			}
			QM.populateQuerySummaryTable();
			RemoteManager.getNotificationSettings(QM.setNotification);
		},
		
		populateQuerySummaryTable : function(){
			$('#query_list_table').dataTable( {
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
					RemoteManager.getAllQueriesInfo({
			            async:false,
			            callback:function(result){
			                 fnCallback(QM.fillQuerySummaryTable(result));	
			                 $(window).trigger('resize');
			                }});
					
					},
				"bAutoWidth": true,
		        "aoColumns": [
		            { "sTitle": '<input type="checkbox" value="selectAll" id="selectAll" onclick="javascript:QM.selectAllQueries(this.id)">'},
		            { "sTitle": "QueryID" },
		            { "sTitle": "Description" },
		            { "sTitle": "Query" }
		        ]
		    } );
			if($('#query_list_table tbody tr td').hasClass('dataTables_empty'))
				document.getElementById('selectAll').disabled = true;
			else
				document.getElementById('selectAll').disabled = false;
			
			$('#query_list_table_length').css('margin-top', 7 + 'px');
			$('#query_list_table_length').css('margin-bottom', 7 + 'px');
			$('#query_list_table_filter').css('margin-top', 7 + 'px');
			$('#query_list_table_filter').css('margin-bottom', 7 + 'px');
			
			
		},
		
		fillQuerySummaryTable : function(queryData){
			var retData = new Array();
			console.log("queryData: ");
			console.log(queryData);
			QM.selectedQueryArray.splice(0, QM.selectedQueryArray.length);
			document.getElementById('selectAll').checked = false;
			QM.toggleButton("selectAll", false);
			var object = JSON.parse(queryData["responseMessage"]);
			console.log("object: ");
			console.log(object);
			console.log(object.length);
			var tableList = new Array();
			var qCache = new Object;
			if(object!=null)
	   		{
				    QM.queryArray.splice(0,QM.queryArray.length);
					for (queryData in object)
					{
						var cur = object[queryData];
						console.log("cur: ", cur)
						var queryId = cur["id"]; // queryID
						var description = cur["description"]; // query description
						var properties = cur["properties"]; // query description
						var queryc = {};
						queryc["id"] = queryId;
						queryc["description"] = description;
						queryc["properties"] = properties;
						
						QM.totalQuery++;
			   			QM.queryArray.push(queryId);
						var check='<input type="checkbox" id="'+queryId+'" onclick="javascript:QM.clickBox(this.id);">';
						
						qCache[queryId] = queryc;
						tableList.push([check,queryId,description,properties]);
		   			}
		  	}
			retData["data"] = tableList;
			QM.queryCache = qCache;
			console.log("retData: ", retData)
			return retData;
		},
		
		clickBox : function(id)
		{
			var flag = document.getElementById(id).checked;
			if (flag == true)
			{
				QM.selectedQueryArray.push(id.toString());
			}
			else
			{
				var index = jQuery.inArray(id.toString(), QM.selectedQueryArray);
				if (index != -1)
				{
					QM.selectedQueryArray.splice(index, 1);
				}
			}
			if(($('#query_list_table tr').length - 1) == QM.selectedQueryArray.length)
			{
				document.getElementById("selectAll").checked = flag;
				QM.selectAllQuery("selectAll", flag);
			}
			else
				QM.toggleButton(id, flag, "selectAll");
		},
  		selectAllQuery : function(id)
   		{

  			var flag = document.getElementById(id).checked;
  			
  			QM.selectedQueryArray.splice(0, QM.selectedQueryArray.length);
  			for (var i=0; i<QM.queryArray.length; i++)
  			{
  				document.getElementById(QM.queryArray[i]).checked = flag;
  				if (flag)
  				{	
  					QM.selectedQueryArray.push(QM.queryArray[i]);
  				}
  			}
  			QM.toggleButton(id, flag);
   		},
   		toggleButton : function(id , value)
   		{

   			if (id == "selectAll")
   			{
   				if (QM.selectedQueryArray.length == 1)
   				{
   					dwr.util.byId('queryEdit').disabled=false;
   					dwr.util.byId('queryClone').disabled=false;
   				}
   				else
   				{
   					dwr.util.byId('queryClone').disabled=true;
   					dwr.util.byId('queryEdit').disabled=true;
   				}
   				dwr.util.byId('queryDelete').disabled=!value;
   				
   			}
   			else
   			{
				if(value == false)
					$('#selectAll').attr("checked",false);
				
				if (QM.selectedQueryArray.length < 1)
				{
					dwr.util.byId('queryClone').disabled=true;
   					dwr.util.byId('queryEdit').disabled=true;
   					dwr.util.byId('queryDelete').disabled=true;
				}
				else
				{
					if (QM.selectedQueryArray.length == 1)
					{
						dwr.util.byId('queryEdit').disabled=false;
	   					dwr.util.byId('queryClone').disabled=false;
					}
					else
					{
						dwr.util.byId('queryClone').disabled=true;
	   					dwr.util.byId('queryEdit').disabled=true;
					}
					dwr.util.byId('queryDelete').disabled=false;
				}
   				
   		
   			}
   		},
		addNewQuery : function(){
			QM.isEditQuery = false;
			QM.isNewQuery = true;
			Navbar.isAddNewQuery=true;
			Navbar.isFromsummaryView = true;
			Navbar.changeTab('Queries','queries','edit_queries');
		},
		editSelectedQuery : function(){
			Navbar.isEditQuery = true;
			var queryId = QM.selectedQueryArray[0]+'';
			Navbar.selectedQueryId = queryId;
			Navbar.isFromsummaryView = true;
			var nameNodeId = QM.selectedNameNode;
			Navbar.changeTab('Queries','queries','edit_queries');
		},
		backToSummary : function(){
			$('#refreshViewButton').attr('onclick','javascript:Navbar.refreshView()');
			Navbar.refreshView();
		},
		showQuery: function(queryId){
			Navbar.isEditQuery = true;
			Navbar.selectedQueryId = queryId;
			Navbar.isFromsummaryView = true;
			var nameNodeId = QM.selectedNameNode;
			Navbar.changeTab('QueryDesigner','analytics','QueryDesigner');
		},
		
		cloneSelectedQuery : function(){
			
			QM.selectedQueryId = QM.selectedQueryArray[0]
			
			Util.addLightbox("addclone", "resources/cloneQuery.html", null, null);
		},
		fillCloneQueryObject : function(object){
			
			QM.queryInfo =jQuery.extend(true, {},object);
		},
		deleteSelectedQuery : function(){
			QM.selectedQueryId = QM.selectedQueryArray[0];
			QM.deleteAQuery();
		},
		

		deleteAQuery : function() {

			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton = ' No';
			jConfirm('Are you sure you want to delete this Query ?',
					'Delete Query', function(val) {
						if (val == true) {
							// callBackFunc = deleteQuery;
							QM.isDelete = true;
							Navbar.queryManagerDirtyBit = false;
							Util.addLightbox('export',
									'resources/delete_query_box.html');
						} else {
							return;
						}
						jQuery.alerts.okButton = ' Ok ';
						jQuery.alerts.cancelButton = ' Cancel';
					});
			$("#popup_container").css("z-index", "99999999");

		},
		
		populateDeleteQueryBox : function(){
			
			var queryIdArray  = QM.selectedQueryArray;
			for (var i = 0; i <queryIdArray.length ; i++)
			{
				var id = queryIdArray[i];
				dwr.util.cloneNode('pattern',{ idSuffix:id });
				dwr.util.setValue('query' + id,id);
				dwr.util.setValue('message' + id,"Perform delete operation on query "+id);
				dwr.util.setValue('status' + id,'Deleting');
				dwr.util.byId('pattern' + id).style.display = '';
				
			}
			var nameNode = $('#queryIONameNodeId').val();
			
			for (var i = 0; i <queryIdArray.length ; i++)
			{
				var id = queryIdArray[i];
				RemoteManager.deleteQuery(id, QM.processDeleteQueryResponse);
				
			}
		},
		
		queryIdChanged : function() {
			$('#queryIdTitle').text($('#queryId').val());
			$("#design_link").text("Design: " + $("#queryId").val());
		},

		changeQueryIODbName : function(dbName) {

			Navbar.isAddNewQuery = true;
			if (QM.selectedDbName == '')
				QM.lastSelectedDbName = dbName;
			else
				QM.lastSelectedDbName = QM.selectedDbName;
			QM.selectedDbName = dbName;
			RemoteManager.getAllTagTableNames(QM.selectedNameNode,
					QM.selectedDbName, QM.populateNameNodeFromList);

			if (!QM.isHive)
				QM.isHive = true;
			else
				QM.isHive = false;

			if (QM.isHive) {
				$('#query_filter_table').css('visibility', 'visible');
			} else {
				$('#query_filter_table').css('visibility', 'hidden');
				$('#is_apply_query_filter').removeAttr('checked')
				$('#query_filter_sql').css('visibility', 'hidden');
				QM.applyQueryFilter();
			}
		},
		
		applyQueryFilter : function() {

			var isApplyFilter = $('#is_apply_query_filter').is(':checked')

			if (isApplyFilter) {

				QM.queryInfo["isFilterQuery"] = true;
				$('#query_filter_sql').css('visibility', 'visible');
				RemoteManager.getQueryFilterTableName(
						QM.queryInfo["selectedTable"][0], $('#queryIONameNodeId')
								.val(), function(tableName) {
							if (tableName == null || tableName == undefined) {
								jAlert("Table not found for input path filter");
								$('#is_apply_query_filter').removeAttr('checked',
										'checked');
								QM.applyQueryFilter();
								return;
							}

							var map = new Object();
							map[tableName] = false;
							// Fix for JTL files
							if (QM.stringEndsWith(tableName, "_CSV")) {
								var newTable = tableName.substr(0, tableName
										.lastIndexOf("_CSV"))
										+ "_JTL";
								map[newTable] = false;
							}
							QM.populateQueryFilterTableDiv(map, false);

						});

			} else {

				QM.queryInfo["isFilterQuery"] = false;

				$('#query_filter_sql').css('visibility', 'hidden');
				$('#query_filter_div').hide();
				$('#query_filter_where_div').hide();
				$('#query_filter_tables_div').hide();
				$('#query_filter_sql').val('');
			}
		},
		

		showQueryFilterDiv : function(isShow) {

			if (isShow) {
				$('#query_filter_div').show();
			} else {
				$('#query_filter_div').hide();
				$('#query_filter_where_div').hide();
				$('#query_filter_tables_div').hide();
			}

		},
		
		showQueryFilterTables : function(isShow) {

			if (isShow) {
				$('#query_filter_tables_div').show();
			} else {
				$('#query_filter_tables_div').hide();
			}
		},
		
		showFromFilters : function(element) {
			$('#searchFromFilters').fadeIn('slow');
			$(
					'#columnHeaderColFilters,#groupHeaderColFilters,#searchColFilters,#columnDetailColFilters,#groupByColFilters,#havingColFilters,#orderByColFilters,#whereFilters')
					.hide();

			QM.isFromSelected = true;

		},
		
		showQueryFilterWhere : function(isShow) {

			if (isShow) {
				$('#query_filter_where_div').show();
			} else {
				$('#query_filter_where_div').hide();
			}
		},
		
		showColFilters : function(element) {
			$('#searchColFilters').fadeIn('slow');
			$(
					'#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchFromFilters,#groupByColFilters,#havingColFilters,#orderByColFilters,#whereFilters')
					.hide();
			QM.isColumnSelected = true;

		},
		
		persistClicked : function(checked) {
			QM.queryInfo["persistResults"] = checked;
			document.getElementById('resultTableName').disabled = !checked;
		},
		
		showWhereCol : function(element) {
			$('#whereFilters').fadeIn('slow');
			$(
					'#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters,#havingColFilters,#groupByColFilters,#orderByColFilters')
					.hide();
		},
		
		showGroupByCol : function(element) {
			$('#groupByColFilters').fadeIn('slow');
			$(
					'#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters,#havingColFilters,#orderByColFilters,#whereFilters')
					.hide();
			QM.isGroupBySelected = true;

		},
		
		showHavingCol : function(element) {

			if ($("#grp_by_col").val() == "") {
				jAlert("HAVING clause cannot be set before GROUP BY clause.",
						"Improper Query");
				return;
			}

			$('#havingColFilters').fadeIn('slow');
			$(
					'#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters,#groupByColFilters,#orderByColFilters,#whereFilters')
					.hide();
			QM.isHavingSelected = true;

		},
		
		showOrderByCol : function(element) {
			$('#orderByColFilters').fadeIn('slow');
			$(
					'#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters, #havingColFilters,#groupByColFilters,#whereFilters')
					.hide();

			var theForm = document.getElementById("orderByForm");
			if (theForm == null || theForm == undefined || theForm.elements == null
					|| theForm.elements == undefined)
				return;
			for (var i = 0; i < theForm.elements.length; i++) {
				var e = theForm.elements[i];
				if (e.type == 'checkbox') {
					for ( var key in QM.queryInfo["selectedOrderBy"]) {
						if (e.value == key) {
							e.checked = true;
							var order = QM.queryInfo["selectedOrderBy"][key];
							$("#order" + e.value).val(order);
						}
					}
				}
			}
		},
		
		checkQueryEmpty : function() {
			var val = $("#query_textarea").val();
			if (val == "") {
				document.getElementById('executeQuery').disabled = true;
				document.getElementById('saveQuery').disabled = true;
			} else {
				document.getElementById('executeQuery').disabled = false;
				document.getElementById('saveQuery').disabled = false;
			}
		},
		
		saveQuery : function() {
			console.log(QM.queryInfo);
			QM.queryInfo["searchColumn"] = QM.searchColumn;
			QM.setResultTableName();
			if (!Navbar.isDataAvailabe) {
				var dName = QM.selectedDbName;
				if (dName == "Not Configured")
					jAlert(
							"Current Namespace is not associated with any database. Please configure a database for selected Namespace.",
							"Error");
				else
					jAlert(
							"There is no table available in selected "
									+ dName
									+ " database.Please upload data from Data Import/Export tab to use Analytics feature.",
							"Error");
				return;
			}
			if (Util.blockSpecialCharButNotSpace($('#queryId').val())) {
				jAlert(
						"Query Id contains special character. Please remove special character from Id.",
						"Invalid Id");
				$("#popup_container").css("z-index", "9999999");
				return;
			}
			var names = [];
			$('#bigQueryIds option').each(function() {
				names.push($(this).attr('value'));
			});
			for (var i = 0; i < names.length; i++) {
				if (names[i] == $("#queryId").val()) {
					if (QM.isNewQuery) {
						QM.isNewQuery = false;
						jAlert("Query with Query ID \"" + names[i]
								+ "\" already exists. Please rename the Query ID.",
								"Error : Query Id");
						return;
					} else if (names[i] != $("#bigQueryIds").val()) {
						jAlert("Query with Query ID \"" + names[i]
								+ "\" already exists. Please rename the Query ID.",
								"Error : Query Id");
						return;
					}
				}
			}
			if (!QM.isClone)
				QM.getQueryInfoObject();

			if (($("#queryId").val() == '')) {
				jAlert("Query ID  is not specified.", "Error : Query Id");
				return;
			}
			if (($("#query_textarea").val() == '')) {
				jAlert("Query Title is not specified.", "Error : Query Title");
				return;
			}

			QM.selectedQueryId = QM.queryInfo["queryId"];
			Navbar.selectedQueryId = QM.selectedQueryId;
			QM.query = QM.queryInfo["sqlQuery"];
			QM.isSave = true;
			Navbar.queryManagerDirtyBit = false;
			DAT.tempQuery = '';
			QM.queryInfo["namenode"] = $('#queryIONameNodeId').val();
			QM.queryInfo["dbName"] = $('#queryIODatabase').val();

			var list = new Array();

			var checkboxes = $('table#columnTable input[type=checkbox]');

			for (var t = 0; t < checkboxes.length; t++) {
				var e = checkboxes[t];
				if (e.checked && e.name != 'ALL') {
					list.push(e.name);
					QM.selectAggregateFunction(null, e.name);
				}
			}
			QM.queryInfo["selectedColumnList"] = list;

			QM.showQuerySavePopup();

		},
		

		showQuerySavePopup : function() {
			Util.addLightbox('export', 'pages/popup.jsp');
		},
		
		selectAggregateFunction : function(element, colName) {
			var value = $('#aggregate_' + colName).val();
			var func = "";
			if (value.indexOf("DISTINCT") != -1)
				func = value + " " + colName + ")";
			else
				func = value + "(" + colName + ")";

			for (var i = 0; i < QM.searchColumn.length; i++) {

				if (QM.searchColumn[i].indexOf(colName) != -1) {
					QM.searchColumn.splice(i, 1);
				}
			}
			if (value == "") {
				QM.searchColumn.push(colName);
			} else {
				QM.searchColumn.push(func);
			}

			$('#srch_col_fld').val(QM.searchColumn);
			if (QM.queryInfo["selectedColumn"][colName] == undefined
					|| QM.queryInfo["selectedColumn"][colName] == null) {
				QM.queryInfo["selectedColumn"][colName] = new Object();
			}
			QM.queryInfo["selectedColumn"][colName]["function"] = value;
			QM.setOrderByDropDown();

		},
		
		setOrderByDropDown : function() {
			var list = '';
			if (QM.searchColumn[0] == '*' && QM.searchColumn.length == 1) {
				list = this.colList;
			} else {
				list = QM.searchColumn;
			}
			var orderbyOptData = '<span> </span><span id="selectColClose" class="divcloser"><a href="javascript:QM.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span> <br><form name="orderByForm" id="orderByForm">';
			orderbyOptData += '<table>';
			for (var i = 0; i < list.length; i++) {
				var index = jQuery.inArray(list[i], QM.colList);
				if (index == -1)
					continue;

				var checked = '';
				var descseleted = '';
				var ascseleted = '';

				if (QM.queryInfo["selectedOrderBy"].hasOwnProperty(list[i])) {
					checked = 'checked="checked"';
					if (QM.queryInfo["selectedOrderBy"][list[i]] == 'DESC') {
						descseleted = 'selected="selected"';
					} else {
						ascseleted = 'selected="selected"';
					}
				}
				orderbyOptData += '<tr>'
						+ '<td width = "70%" style = "white-space: nowrap;">'
						+ '<input type="checkbox" name="orderBy'
						+ list[i]
						+ '" id="orderBy'
						+ list[i]
						+ '" value="'
						+ list[i]
						+ '" onclick="QM.setOrderBy(\''
						+ list[i]
						+ '\', this.checked);" '
						+ checked
						+ ' style=" margin-bottom: 10px;" > '
						+ list[i]
						+ '</td><td>'
						+ '<select id="order'
						+ list[i]
						+ '" onchange = "QM.setOrderBy(\''
						+ list[i]
						+ '\');" >'
						+ '<option value = "ASC" '
						+ ascseleted
						+ '>ASC</option>'
						+ '<option value = "DESC" '
						+ descseleted
						+ '>DESC</option>' + '</select>' + '</td></tr>';
			}
			orderbyOptData += '</table>';
			$('#orderByColFilters').html(orderbyOptData);
			QM.setOrderBy();

		},
		
		setOrderBy : function(value, isChecked) {
			var values = new Array();
			var valuesQuery = [];
			var theForm = document.getElementById("orderByForm");
			if (theForm == null || theForm == undefined || theForm.elements == null
					|| theForm.elements == undefined)
				return;
			for (var i = 0; i < theForm.elements.length; i++) {
				var e = theForm.elements[i];
				if (e.type == 'checkbox') {
					if (e.checked) {
						var valueOrder = $("#order" + e.value).val();

						if (!values.hasOwnProperty(e.value))
							values[e.value] = new Object();
						values[e.value] = valueOrder;

						valuesQuery.push(e.value + ' ' + valueOrder);
					}
				}
			}
			$('#order_by_col').val(valuesQuery);
			QM.queryInfo["selectedOrderBy"] = jQuery.extend(true, {}, values);
			QM.showCommand();

		},
		
		showCommand : function() {
			if (QM.searchFrom == null) {
				$('#query_textarea').val('');
				return;
			}
			var query = 'SELECT ';
			for (var i = 0; i < QM.searchColumn.length; i++) {
				if (i != 0) {
					query += ', ';
				}
				query += QM.searchColumn[i];
				if (QM.searchColumn[i].indexOf('(') != -1) {
					query += ' AS ';
					var aliasName = QM.searchColumn[i];
					aliasName = aliasName.replace(' ', '_');
					aliasName = aliasName.replace('(', '_');
					aliasName = aliasName.replace(')', '_');
					query += aliasName.substring(0, aliasName.lastIndexOf('_'))
							.toLowerCase()
							+ " ";
				}

			}
			query += ' ';
			query += ' FROM ' + QM.searchFrom;

			if ($('#where_col').val() != "") {

				query += ' WHERE ' + $('#where_col').val();
			}

			if ($('#grp_by_col').val() != "") {
				query += ' GROUP BY ';
				query += $('#grp_by_col').val() + ' ';
			}

			if ($('#having_col').val() != "") {
				query += ' HAVING ' + $('#having_col').val();
			}

			if ($('#order_by_col').val() != "") {
				query += ' ORDER BY ';
				query += $('#order_by_col').val() + ' ';

			}
			$('#query_textarea').val(query);
			QM.query = query;
		},
		
		closeSelectionDiv : function() {

			$(
					'#gantt_y_labelColFilters,#gantt_y_startColFilters,#gantt_y_endColFilters,#difference_y_positiveColFilters,#difference_y_negetiveColFilters,#stock_y_highColFilters,#stock_y_lowColFilters,#stock_y_openColFilters,#stock_y_closeColFilters,#line_y_seriesColFilters,#bubble_y_valueColFilters,#bubble_y_sizeColFilters,#searchColFilters,#columnDetailColFilters,#columnHeaderColFilters,#groupHeaderColFilters,#searchFromFilters,#groupByColFilters,#havingColFilters,#orderByColFilters,#whereFilters')
					.hide();
		},
		
		readyQEdit : function() {
			Navbar.isDataAvailabe = false;
			QM.checkForAdded = false;
			QM.resetQueryInfoJSON();

			QM.selectedNameNode = $("#queryIONameNodeId").val();

			if (Navbar.isEditQuery) {
				if (Navbar.selectedQueryId != null && Navbar.selectedQueryId != '')
					$("#queryIds").val(Navbar.selectedQueryId);
			}
			QM.selectedQueryId = $("#queryIds").val();

			if (Navbar.selectedQueryId == '' || Navbar.selectedQueryId == null
					|| Navbar.selectedQueryId == undefined)
				Navbar.selectedQueryId = QM.selectedQueryId;

			QM.setDBNameForNameNode(QM.selectedNameNode);
		},
		
		setDBNameForNameNode : function(nameNodeId) {
			if (nameNodeId != null && nameNodeId != '' && nameNodeId != undefined) {
				RemoteManager.getAllDBNameWithTypeForNameNodeMapping(nameNodeId,
						QM.fillDBName);
			}
		},
		
		fillDBName : function(dbNameList) {
			var data = "";
			if (dbNameList != null) {
				QM.selectedDbName = dbNameList["Metastore"];
				QM.isHive = false;
				QM.lastSelectedDbName = QM.selectedDbName;

				if (dbNameList["Metastore"] != null
						&& dbNameList["Metastore"] != "")
					data += '<option value="' + dbNameList["Metastore"] + '">'
							+ dbNameList["Metastore"] + '</option>';
				if (dbNameList["Hive"] != null && dbNameList["Hive"] != "")
					data += '<option value="' + dbNameList["Hive"] + '">'
							+ dbNameList["Hive"] + '</option>';
			}

			$('#queryIODatabase').html(data);
			QM.afterReadyQuery();
		},
		
		afterReadyQuery : function() {

			RemoteManager.getAllTagTableNames(QM.selectedNameNode,
					QM.selectedDbName, QM.populateNameNodeFromList);

			QM.isFirstTime = true;
			QM.slide();
			QM.SearchReady();

		},
		
		slide : function() {
			$("#expandQueryID").hide();
			$("#expandQueryDesign").hide();
			$("#expandQueryProperties").hide();
			$("#Show").hide();

			$("#Hide").click(function() {
				$("#Hide").hide();
				$("#Show").show();
				// $("#queryHistory").width("3%");
				// $("#data_analyzer_query_builder").width("96%");
				$("#queryHistory").width("30px");
				$("#queryHistory").css('min-width', '30px');
				// $("#data_analyzer_query_builder").css('padding-left','20px');
				$("#data_analyzer_query_builder").width("94%");
				$("#leftTable").css("display", "none");
			});
			$("#Show").click(function() {
				$("#Show").hide();
				$("#Hide").show();
				$("#queryHistory").width("210px");
				$("#queryHistory").css('min-width', '210px');
				// $("#data_analyzer_query_builder").css('margin-left','235px');
				$("#data_analyzer_query_builder").width("auto");
				$("#leftTable").css("display", "block");
			});

			$("#shrinkQueryID").click(function() {
				$("#shrinkQueryID").hide();
				$("#expandQueryID").show();
				$("#divQueryID").css("display", "none");

			});
			$("#expandQueryID").click(function() {
				$("#expandQueryID").hide();
				$("#shrinkQueryID").show();
				$("#divQueryID").css("display", "block");

			});

			$("#shrinkQueryDesign").click(function() {
				$("#shrinkQueryDesign").hide();
				$("#expandQueryDesign").show();
				$("#rightPart").css("display", "none");

			});
			$("#expandQueryDesign").click(function() {
				$("#expandQueryDesign").hide();
				$("#shrinkQueryDesign").show();
				$("#rightPart").css("display", "block");

			});

		},
		
		SearchReady : function() {
			filterState = false;
			$(
					'#column_detail_col,#columnDetailColFilters,#column_header_col,#columnHeaderColFilters,#group_footer_col,#groupFooterColFilters,#group_header_col,#groupHeaderColFilters, #srch_col_fld, #searchColFilters,#srch_from_fld, #searchFromFilters,#grp_by_col,#groupByColFilters,#havingColFilters,#order_by_col,#orderByColFilters,#where_col,#having_col')
					.hover(function() {
						filterState = true;
					}, function() {
						filterState = false;
					});
			$('html')
					.bind(
							'click',
							function() {
								if (!filterState) {
									$(
											'#columnDetailColFilters,#columnHeaderColFilters,#groupFooterColFilters,#groupHeaderColFilters,#searchColFilters,#searchFromFilters,#groupByColFilters,#havingColFilters,#orderByColFilters')
											.hide();

									if (QM.isColumnSelected) {
										QM.serachColChanged();
										QM.isColumnSelected = false;

									}
									if (QM.isFromSelected) {
										QM.searchFromChanged();
										QM.isFromSelected = false;
									}
								}
							});

		},
		
		serachColChanged : function() {
			QM.showCommand();
		},
		searchFromChanged : function() {
			QM.showCommand();
		},
		
		populateNameNodeFromList : function(map) {
			var list = [];
			if (map.ConnectionError == null) {
				QM.tableMap = map;
				for ( var tableName in map) {
					list.push(tableName);
				}
			} else {
				var dbname = QM.selectedDbName;

				jAlert("Could not connect to " + dbname
						+ " database. Please check if database is running.",
						"Error");
				if (QM.selectedDbName != QM.lastSelectedDbName)
					QM.selectedDbName = QM.lastSelectedDbName;
				$('#queryIODatabase').val(QM.selectedDbName);
				return;
			}

			if (list == null || list.length == 0) {

				var dName = QM.selectedDbName;
				if ((dName == "") || (dName == "Not Configured")) {
					jAlert(
							"Current Namespace is not associated with any database. Please configure a database for selected Namespace.",
							"Error");

				} else {
					jAlert("There is no table found in selected " + dName
							+ " database.", "Error");

					if (QM.selectedDbName != QM.lastSelectedDbName)
						QM.selectedDbName = QM.lastSelectedDbName;
					$('#queryIODatabase').val(QM.selectedDbName);
				}

				return;
			}
			Navbar.isDataAvailabe = true;
			var data = '<form name="tables" id="tables">';
			data += '<table><tr><td colspan="2"><span>Search On:</span><span id="selectColClose" class="divcloser"><a href="javascript:QM.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span></td></tr>';
			for (var i = 0; i < list.length; i++) {
				var tbl_name = list[i];
				if (i == 0) {
					data += '<tr><td><input checked checked="checked" type="checkbox" name="nnID[]" id="filterBy'
							+ tbl_name
							+ '" value="'
							+ tbl_name
							+ '" onclick="QM.setLocationSearch(\''
							+ tbl_name
							+ '\',this.checked);" > </td><td>'
							+ tbl_name
							+ '</td></tr>';
				} else {
					data += '<tr><td><input type="checkbox" name="nnID[]" id="filterBy'
							+ tbl_name
							+ '" value="'
							+ tbl_name
							+ '" onclick="QM.setLocationSearch(\''
							+ tbl_name
							+ '\',this.checked);" > </td><td>'
							+ tbl_name
							+ '</td></tr>';
				}
			}
			data += '</table></form>';
			$('#searchFromFilters').html(data);

			if ((Navbar.isEditQuery || QM.isFirstTime) && (!Navbar.isAddNewQuery)) {
				QM.selectedQueryId = $('#bigQueryIds').val();
				if (Navbar.selectedQueryId == "" || Navbar.selectedQueryId == null) {
					Navbar.selectedQueryId = $('#bigQueryIds').val();
				} else {
					$('#bigQueryIds').val(Navbar.selectedQueryId);
				}
				var userName = Util.getLoggedInUserName();
				if (QM.selectedNameNode != '' && Navbar.selectedQueryId != '') {
					RemoteManager.getBigQueryInfo(QM.selectedNameNode,
							Navbar.selectedQueryId, userName, QM.fillSavedQuery);
				}
				return;
			}
			Navbar.isAddNewQuery = false;
			QM.isTableSelectedByUser = false;
			$('#filterBy' + list[0]).attr('checked', 'checked');
		},
		
		fillSavedQuery : function(historyObj) {
			if (historyObj == null || historyObj == undefined)
				return;

			QM.isHistoryFilled = true;
			$("#error_msg").text("");
			QM.queryInfo = jQuery.extend(true, {}, historyObj);

			$("#queryIODatabase").val(historyObj["dbName"]);

			QM.setHiveFlag(QM.selectedNameNode, historyObj["dbName"]);

			QM.selectedDbName = $("#queryIODatabase").val();

			if (QM.lastSelectedDbName != QM.selectedDbName) {
				QM.lastSelectedDbName = QM.selectedDbName;
				RemoteManager.getAllTagTableNames(QM.selectedNameNode,
						QM.selectedDbName, QM.onlyPopulateTableNameDiv);

			}

			$("#queryId").val(historyObj["queryId"]);
			$("#queryIdTitle").text(historyObj["queryId"]);
			$("#design_link").text("Design: " + $("#queryId").val());
			$("#preview_span").text("Preview: " + $("#queryId").val());
			$("#queryDesc").val(historyObj["queryDesc"]);
			$("#query_textarea").val(historyObj["sqlQuery"]);

			$('#srch_col_fld').val(historyObj["searchColumn"]);

			var isFilterQuery = historyObj["isFilterQuery"];

			if (isFilterQuery) {

				QM.setQueryFilterDetail(historyObj["queryFilterDetail"],
						historyObj["selectedTable"][0]);

			}

			if (QM.queryInfo["setLimitResultRows"]) {
				$('#limitResultRows').attr('checked', 'checked');
				$('#limitResultRowsValue')
						.val(QM.queryInfo["limitResultRowsValue"]);
				$('#limitResultRowsValue').css('display', '');
			} else {
				$('#limitResultRows').removeAttr('checked');
				$('#limitResultRowsValue').css('display', 'none');
			}

			$('#searchFromFilters').find('input[type=checkbox]:checked')
					.removeAttr('checked');

			QM.isSetQueryRequest = true;

			var selectedTableObj = historyObj["selectedTable"];

			for (var i = 0; i < selectedTableObj.length; i++) {
				var value = selectedTableObj[i] + '';

				$('#filterBy' + selectedTableObj[i]).attr("checked", true);
			}

			QM.searchFrom = selectedTableObj;

			$('#srch_from_fld').val(selectedTableObj);

			RemoteManager.getAllAvailableTagsList(QM.selectedNameNode,
					QM.selectedDbName, selectedTableObj, QM.populateSearchColNames);

			RemoteManager.getResultTableName($('#srch_from_fld').val(),
					QM.selectedNameNode, QM.fetchResultTableName);

			QM.selectedHistoryObj = jQuery.extend(true, {}, historyObj);
			QM.isEditQuery = false;

		},
		
		fetchResultTableName : function(response) {
			if (response == null)
				return;

			if (response.adhoc == false) { // Comment these 4 lines to hide Result
											// Table
				$('#resultTableNameTD').hide(); // TODO Result Table span removed
				$('#resultTableName').attr('disabled', 'disabled');
			} else {
				$('#resultTableNameTD').show(); // TODO Result Table span removed
				$('#resultTableName').removeAttr('disabled');
			}
			$('#resultTableName').val(response.resultTableName);

			QM.queryInfo["resultTableName"] = response.resultTableName;
			QM.currentExecutionId = response.executionId;
			QM.setResultTableName();
			var value = response.tableName;
			var flag = false;
			if (response.adhoc == true) {

				if ($('#filterBy' + value).is(':checked')) {
					flag = true;
				} else {
					flag = false;
				}
				if (flag) {

					var theForm = document.getElementById("tables");
					if (theForm == null || theForm == undefined
							|| theForm.elements == null
							|| theForm.elements == undefined)
						return;
					for (var i = 0; i < theForm.elements.length; i++) {
						var e = theForm.elements[i];
						if (e.type == 'checkbox') {
							e.checked = false;
						}
					}
					$('#filterBy' + value).attr('checked', 'checked');
				}

			}
			if (!flag) {

				var theForm = document.getElementById("tables");
				if (theForm == null || theForm == undefined
						|| theForm.elements == null
						|| theForm.elements == undefined)
					return;

				for (var i = 0; i < theForm.elements.length; i++) {
					var e = theForm.elements[i];
					if (e.type == 'checkbox') {
						var val = e.value + '';
						if (QM.tableMap[val]) {
							e.checked = false;
							continue;
						}
					}
				}
			}
		},
		
		setResultTableName : function(element) {

			var value = $('#resultTableName').val();
			if (value == "") {
				jAlert("Please Enter valid Resulted table Name", "Error");
				return;
			}
			QM.queryInfo["resultTableName"] = value;

		},
		
		populateSearchColNames : function(tagListObject) {

			if (tagListObject == null || tagListObject == undefined) {
				jAlert("No column found for selected table.", "No column Found");
				return;
			}
			var map = tagListObject["columnMap"];
			var tableSchema = tagListObject["tableSchema"];
			QM.selectedTableSchema = tableSchema;
			if (map == null || map == undefined)
				return;
			var list = new Array();

			for ( var attr in map) {
				list.push(attr);
			}

			QM.colList = list;
			QM.colMap = map;
			var obj = new Object();
			var headerObj = new Object();

			if (QM.columnsForCurrentFromSelection.length > 0) {
				QM.columnsForCurrentFromSelection.splice(1,
						QM.columnsForCurrentFromSelection.length);
			}
			var data = '<span id="selectColClose" class="divcloser"><a href="javascript:QM.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span> <table id="columnTable"><tbody><tr><td nowrap="nowrap">Select Column</td><td nowrap="nowrap">Aggregate Function</td></tr>';
			var groupbyOptData = '<span> </span><span id="selectColClose" class="divcloser"><a href="javascript:QM.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span> <br><form name="groupByForm" id="groupByForm" style = "white-space: nowrap;">';
			var havingData = '<span id="selectColClose" class="divcloser"><a href="javascript:QM.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span> <table id="having_tbl"><tbody><tr><td nowrap="nowrap">Select Column</td><td nowrap="nowrap">Relational Operator</td><td nowrap="nowrap">Value</td><td nowrap="nowrap">Logical Operator</td></tr>';
			var colOpt = '';
			var where_data_table = '<span id="selectColClose" class="divcloser"><a href="javascript:QM.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span> <table id="where_tbl"><tbody><tr><td nowrap="nowrap">Select Column</td><td nowrap="nowrap">Relational Operator</td><td nowrap="nowrap">Value</td><td nowrap="nowrap">Logical Operator</td></tr>';

			data += '<tr><td><input checked type="checkbox" name="ALL" id="filterByALL" value="*" onclick="QM.selectColumnOperationWrapper(\'*\', this.checked);" checked="checked" >All</td><td nowrap="nowrap">'
					+ '</td></tr>';

			for (var i = 0; i < list.length; i++) {
				QM.columnsForCurrentFromSelection.push(list[i]);
				obj[list[i] + ''] = new Object();
				headerObj[list[i] + ''] = new Object();
				data += '<tr><td nowrap="nowrap"><input type="checkbox" name="'
						+ list[i] + '" id="filterBy' + list[i] + '" value="'
						+ list[i] + '" onclick="QM.selectColumnOperationWrapper(\''
						+ list[i] + '\', this.checked);" > ' + list[i] + '</td>';
				data += '<td nowrap="nowrap">'
						+ QM.getAggregateFunctionDropDown('aggregate_', list[i],
								'QM.selectAggregateFunction(this,\'' + list[i]
										+ '\')') + '</td></tr>';
				groupbyOptData += '<input type="checkbox" name="groupBy' + list[i]
						+ '" id="groupBy' + list[i] + '" value="' + list[i]
						+ '" onclick="QM.setGroupBy(\'' + list[i]
						+ '\', this.checked);" > ' + list[i] + '<br>';
				colOpt += '<option value="' + list[i] + '">' + list[i]
						+ '</option>';

				where_data_table += '<tr>';
				where_data_table += '<td nowrap="nowrap"><input type="checkbox" name="'
						+ list[i]
						+ '" id="whereBy'
						+ list[i]
						+ '" value="'
						+ list[i]
						+ '" onclick="QM.setWhereIn(\''
						+ list[i]
						+ '\', this.checked);" > ' + list[i] + '</td>';
				where_data_table += '<td>'
						+ QM.getRelationalOperatorDropDown(list[i], 'roperator_',
								'QM.selectRelationalFunction', QM.colMap[list[i]])
						+ '</td>';
				where_data_table += '<td><input type="text" id="whereval_'
						+ list[i]
						+ '" value="" onblur="javascript:QM.makeWhereCondition();"></td>';
				where_data_table += '<td>'
						+ QM.getLogicalOperatorDropDown(list[i], 'loperator_',
								'QM.selectLogicalFunction') + '</td>';

				havingData += '<tr>';
				havingData += '<td nowrap="nowrap"><input type="checkbox" name="'
						+ list[i] + '" id="having' + list[i] + '" value="'
						+ list[i] + '" onclick="QM.setHavingIn(\'' + list[i]
						+ '\', this.checked);" > ' + list[i] + '</td>';
				havingData += '<td>'
						+ QM.getRelationalOperatorDropDown(list[i],
								'roperatorHaving_',
								'QM.selectRelationalFunctionForHaving',
								QM.colMap[list[i]]) + '</td>';
				havingData += '<td><input type="text" id="havingval_'
						+ list[i]
						+ '" value="" onblur="javascript:QM.makeHavingCondition();"></td>';
				havingData += '<td>'
						+ QM.getLogicalOperatorDropDown(list[i],
								'loperatorHaving_', 'QM.makeHavingCondition')
						+ '</td>';

				if (i == list.length - 1)
					QM.lastColumn = list[i];

			}
			groupbyOptData += '</form>';
			QM.searchColumn.splice(0, QM.searchColumn.length);
			QM.searchColumn = [];
			QM.setColSearch('*', true);
			data += '</tbody></table>';
			where_data_table += '</tbody></table>';

			$('#searchColFilters').html(data);
			$('#whereFilters').html(where_data_table);

			$('#groupByColFilters').html(groupbyOptData);
			$('#grp_by_col').val('');
			$('#having_col').val('');

			$('#havingColFilters').html(havingData);
			$('#selectColForProp').html(colOpt);
			$('#selectColHeaderForProp').html(colOpt);

			if (QM.isHistoryFilled) {
				QM.isHistoryFilled = false;

			} else {
				QM.selectColumnOperationWrapper('*', true);

			}

			QM.populateComparisonKeyDropdown(list);
			QM.showCommand();
			QM.queryInfo["colDetail"] = new Object();

			QM.queryInfo["selectedColumn"] = new Object();
			QM.queryInfo["selectedWhere"] = new Object();
			QM.queryInfo["selectedOrderBy"] = new Array();
			QM.queryInfo["selectedGroupBy"] = new Array();
			QM.queryInfo["selectedHaving"] = new Object();

			QM.selectedWhereArray = [];
			$('#where_col').val("");

			if (QM.isSetQueryRequest) {
				QM.isSetQueryRequest = false;
				QM.setQueryBuilderValues();

			}
			QM.setQueryDirtyBitHandlerEvent();
		},
		
		setQueryBuilderValues : function() {
			QM.queryInfo = jQuery.extend(true, {}, QM.selectedHistoryObj);

			var selectedColumnObj = QM.queryInfo["selectedColumn"];

			for ( var colName in selectedColumnObj) {
				if (QM.colList.indexOf(colName) == -1) {
					delete QM.queryInfo["selectedColumn"][colName];
					continue;
				}
				$('#filterByALL').removeAttr("checked");
				$('#filterBy' + colName).attr("checked", true);

				$('#aggregate_' + colName).val(
						selectedColumnObj[colName]["function"]);
				QM.selectColumnOperationWrapper(colName, true);
				$('#aggregate_' + colName).removeAttr("disabled");

				QM.selectAggregateFunction(null, colName)
				// }
			}

			var selectedWhereObj = QM.queryInfo["selectedWhere"];
			QM.selectedWhereArray = [];
			for ( var colName in selectedWhereObj) {
				$('#whereBy' + colName).attr("checked", true);
				$('#roperator_' + colName).val(
						QM.queryInfo["selectedWhere"][colName]["roperator"]);
				$('#loperator_' + colName).val(
						QM.queryInfo["selectedWhere"][colName]["loperator"]);
				$('#whereval_' + colName).val(
						QM.queryInfo["selectedWhere"][colName]["value"]);

				$('#roperator_' + colName).removeAttr("disabled");
				$('#whereval_' + colName).removeAttr("disabled");
				$('#loperator_' + colName).removeAttr("disabled");
			}
			QM.makeWhereCondition();

			var selectedHavingObj = QM.queryInfo["selectedHaving"];
			QM.selectedHavingArray = [];
			for ( var colName in selectedHavingObj) {
				$('#having' + colName).attr("checked", true);
				$('#roperatorHaving_' + colName).val(
						QM.queryInfo["selectedHaving"][colName]["roperator"]);
				$('#loperatorHaving_' + colName).val(
						QM.queryInfo["selectedHaving"][colName]["loperator"]);
				$('#havingval_' + colName).val(
						QM.queryInfo["selectedHaving"][colName]["value"]);

				$('#roperator_' + colName).removeAttr("disabled");
				$('#havingval_' + colName).removeAttr("disabled");
				$('#loperatorHaving_' + colName).removeAttr("disabled");
			}
			QM.makeHavingCondition();

			var selectedGroupByObj = QM.queryInfo["selectedGroupBy"];
			QM.groupByForm = QM.queryInfo["selectedGroupBy"];
			$('#grp_by_col').val(QM.queryInfo["selectedGroupBy"]);
			for (var i = 0; i < selectedGroupByObj.length; i++) {
				$('#groupBy' + selectedGroupByObj[i]).attr("checked", true);
			}
			QM.groupByForm = QM.queryInfo["selectedGroupBy"];
			QM.setOrderByDropDown();
			var selectedOrderByObj = QM.queryInfo["selectedOrderBy"];
			var arrOrder = [];
			for (key in selectedOrderByObj)
				arrOrder.push(key + ' ' + selectedOrderByObj[key]);
			$('#order_by_col').val(arrOrder);

			for (var i = 0; i < selectedOrderByObj.length; i++) {
				$('#orderBy' + selectedOrderByObj[i]).attr("checked", true);
			}
			$("#query_textarea").val(QM.queryInfo["sqlQuery"]);

			document.getElementById("persist").checked = QM.queryInfo["persistResults"];
			QM.persistClicked(QM.queryInfo["persistResults"]);

			if (Navbar.isFromsummaryView && Navbar.isViewerView
					&& Navbar.isExecuteQuery) {
				DAT.executeSeletedQuery();
				Navbar.isExecuteQuery = false;
			}

		},
		
		populateComparisonKeyDropdown : function(list) {

			var data = '';
			if (list == null)
				return;

			for (var i = 0; i < list.length; i++) {
				data += '<option value="' + list[i] + '">' + list[i] + '</option>';
			}
			$('#comparison_col').html(data);
			$('#orderby_col').html(data);
			$('#groupby_col').append(data);
		},
		
		makeWhereCondition : function() {
			var frm = document.getElementById('where_tbl').getElementsByTagName(
					"input");
			var len = frm.length;
			// var colName ='';
			var cond = '';
			QM.selectedWhereArray = [];
			for (i = 0; i < len; i++) {
				if (frm[i].type == "checkbox") {

					if (frm[i].checked) {

						var colName = frm[i].value;

						QM.selectedWhereArray.push(colName);

						cond += colName;
						cond += $('#roperator_' + colName).val() + ' ';

						var colDataType = QM.colMap[colName];
						var operator = $('#roperator_' + colName).val();
						if ((colDataType.toUpperCase() == "STRING"
								|| colDataType.toUpperCase() == "BLOB" || colDataType
								.toUpperCase() == 'TIMESTAMP')
								&& operator.toUpperCase().indexOf("IN") == -1
								&& operator.toUpperCase().indexOf("IS NULL") == -1
								&& operator.toUpperCase().indexOf("IS NOT NULL") == -1) {
							cond += " '" + $('#whereval_' + colName).val() + "' ";
						} else {
							cond += ' ' + $('#whereval_' + colName).val() + ' ';
						}

						cond += $('#loperator_' + colName).val() + ' ';

						if (!QM.queryInfo["selectedWhere"].hasOwnProperty(colName)) {
							QM.queryInfo["selectedWhere"][colName] = new Object();
						}
						QM.queryInfo["selectedWhere"][colName]["roperator"] = $(
								'#roperator_' + colName).val();
						QM.queryInfo["selectedWhere"][colName]["loperator"] = $(
								'#loperator_' + colName).val();
						QM.queryInfo["selectedWhere"][colName]["value"] = $(
								'#whereval_' + colName).val();

					}
				}
			}
			$('#where_col').val(cond);
			QM.showCommand();

		},
		
		makeHavingCondition : function() {
			var frm = document.getElementById('having_tbl').getElementsByTagName(
					"input");
			var len = frm.length;
			// var colName ='';
			var cond = '';
			QM.selectedHavingArray = [];
			for (i = 0; i < len; i++) {
				if (frm[i].type === "checkbox") {

					if (frm[i].checked) {

						var colName = frm[i].value;
						QM.selectedHavingArray.push(colName);

						cond += colName;
						cond += $('#roperatorHaving_' + colName).val() + ' ';

						var colDataType = QM.colMap[colName];
						var operator = $('#roperatorHaving_' + colName).val();
						if ((colDataType.toUpperCase() == "STRING"
								|| colDataType.toUpperCase() == "BLOB" || colDataType
								.toUpperCase() == 'TIMESTAMP')
								&& operator.toUpperCase().indexOf("IN") == -1
								&& operator.toUpperCase().indexOf("IS NULL") == -1
								&& operator.toUpperCase().indexOf("IS NOT NULL") == -1) {
							cond += " '" + $('#havingval_' + colName).val() + "' ";
						} else {
							cond += ' ' + $('#havingval_' + colName).val() + ' ';
						}

						cond += $('#loperatorHaving_' + colName).val() + ' ';

						if (!QM.queryInfo["selectedHaving"].hasOwnProperty(colName)) {
							QM.queryInfo["selectedHaving"][colName] = new Object();
						}
						QM.queryInfo["selectedHaving"][colName]["roperator"] = $(
								'#roperatorHaving_' + colName).val();
						QM.queryInfo["selectedHaving"][colName]["loperator"] = $(
								'#loperatorHaving_' + colName).val();
						QM.queryInfo["selectedHaving"][colName]["value"] = $(
								'#havingval_' + colName).val();

					}
				}
			}
			$('#having_col').val(cond);
			QM.showCommand();

		},
		
		selectRelationalFunction : function(value, colName) {
			if (value.indexOf("IS NULL") != -1
					|| value.indexOf("IS NOT NULL") != -1) {
				$('#whereval_' + colName).val("");
				$('#whereval_' + colName).attr('disabled', 'disabled');
			} else
				$('#whereval_' + colName).removeAttr('disabled');
			QM.makeWhereCondition();
		},
		selectRelationalFunctionForHaving : function(value, colName) {
			if (value.indexOf("IS NULL") != -1
					|| value.indexOf("IS NOT NULL") != -1) {
				$('#havingval_' + colName).val("");
				$('#havingval_' + colName).attr('disabled', 'disabled');
			} else
				$('#havingval_' + colName).removeAttr('disabled');
			QM.makeHavingCondition();
		},
		selectLogicalFunction : function() {
			QM.makeWhereCondition();

		},
		enableWhereComponent : function(colName, isSelected) {
			if (isSelected) {
				$('#roperator_' + colName).removeAttr('disabled');
				$('#whereval_' + colName).removeAttr('disabled');
				if (colName != QM.lastColumn)
					$('#loperator_' + colName).removeAttr('disabled');
			} else {
				$('#roperator_' + colName).attr('disabled', 'disabled');
				$('#whereval_' + colName).attr('disabled', 'disabled');
				$('#loperator_' + colName).attr('disabled', 'disabled');
			}

		},
		enableHavingComponent : function(colName, isSelected) {
			if (isSelected) {
				$('#roperatorHaving_' + colName).removeAttr('disabled');
				$('#havingval_' + colName).removeAttr('disabled');
				if (colName != QM.lastColumn)
					$('#loperatorHaving_' + colName).removeAttr('disabled');
			} else {
				$('#roperatorHaving_' + colName).attr('disabled', 'disabled');
				$('#havingval_' + colName).attr('disabled', 'disabled');
				$('#loperatorHaving_' + colName).attr('disabled', 'disabled');
			}

		},
		
		getLogicalOperatorDropDown : function(colName, idPrefix, onChangeFunction) {
			var data = '<select id="'
					+ idPrefix
					+ colName
					+ '" disabled="disabled" style="width:100%;" onchange="javascript:'
					+ onChangeFunction + '(this,\'' + colName + '\');">';
			data += '<option value=""></option>';
			data += '<option value="AND">AND</option>';
			data += '<option value="OR">OR</option>';

			data += '</select>';
			return data;

		},

		getRelationalOperatorDropDown : function(colName, idPrefix,
				onChaneFunction, dataType) {

			var dbType = $('#queryIODatabase').val();
			var data = '<select id="'
					+ idPrefix
					+ colName
					+ '" disabled="disabled" style="width:100%;" onchange="javascript:'
					+ onChaneFunction + '(this.value,\'' + colName + '\');">';
			data += '<option value=""></option>';
			data += '<option value=" = ">=</option>';
			data += '<option value=" != ">!=</option>';
			if (dataType.toUpperCase() == 'INTEGER'
					|| dataType.toUpperCase() == 'LONG'
					|| dataType.toUpperCase() == 'DECIMAL'
					|| dataType.toUpperCase() == 'SHORT'
					|| dataType.toUpperCase() == 'DOUBLE') {
				data += '<option value=" > ">></option>';
				data += '<option value=" < "><</option>';
				data += '<option value=" <= "><=</option>';
				data += '<option value=" >= ">>=</option>';

			}
			if (!(dbType.toUpperCase().indexOf('HIVE') == 0)) {

				data += '<option value=" IN ">IN</option>';
				data += '<option value=" NOT IN ">NOT IN</option>';
			}

			data += '<option value=" IS NULL ">IS NULL</option>';
			data += '<option value=" IS NOT NULL ">IS NOT NULL</option>';
			data += '<option value=" LIKE ">LIKE</option>';
			data += '<option value=" NOT LIKE ">NOT LIKE</option>';
			data += '<option value=" BETWEEN ">BETWEEN</option>';
			data += '<option value=" NOT BETWEEN ">NOT BETWEEN</option>';

			data += '</select>';
			return data;

		},

		
		setWhereIn : function(colName, isChecked) {
			QM.enableWhereComponent(colName, isChecked);
			if (!isChecked) {
				$('#roperator_' + colName).val('');
				$('#whereval_' + colName).val('');
				$('#loperator_' + colName).val('');
				QM.setSelectedWhereInJSON(colName, false);
			}
			QM.makeWhereCondition();

		},
		
		setSelectedWhereInJSON : function(colName, isSelected) {
			if (isSelected) {
				if (!QM.queryInfo["selectedWhere"].hasOwnProperty(colName)) {
					QM.queryInfo["selectedWhere"][colName] = new Object();
				}
				QM.queryInfo["selectedWhere"][colName]["roperator"] = $(
						'#roperator_' + colName).val();
				QM.queryInfo["selectedWhere"][colName]["value"] = $(
						'#whereval_' + colName).val();
				QM.queryInfo["selectedWhere"][colName]["loperator"] = $(
						'#loperator_' + colName).val();
			} else {
				delete QM.queryInfo["selectedWhere"][colName];

			}

		},
		setSelectedHavingInJSON : function(colName, isSelected) {
			if (isSelected) {
				if (!QM.queryInfo["selectedHaving"].hasOwnProperty(colName)) {
					QM.queryInfo["selectedHaving"][colName] = new Object();
				}
				QM.queryInfo["selectedHaving"][colName]["roperator"] = $(
						'#roperatorHaving_' + colName).val();
				QM.queryInfo["selectedHaving"][colName]["value"] = $(
						'#havingvalHaving_' + colName).val();
				QM.queryInfo["selectedHaving"][colName]["loperator"] = $(
						'#loperatorHaving_' + colName).val();

			} else {
				delete QM.queryInfo["selectedHaving"][colName];

			}

		},

		setNotOperator : function() {
			QM.makeWhereCondition();
		},

		setHavingIn : function(colName, isChecked) {
			QM.enableHavingComponent(colName, isChecked);
			if (!isChecked) {
				$('#roperatorHaving_' + colName).val('');
				$('#havingval_' + colName).val('');
				$('#loperatorHaving_' + colName).val('');
				QM.setSelectedHavingInJSON(colName, false);
			}
			QM.makeHavingCondition();
		},
		
		setGroupBy : function(value, isChecked) {
			var values = [];
			var numberChecked = 0;
			var theForm = document.getElementById("groupByForm");
			if (theForm == null || theForm == undefined || theForm.elements == null
					|| theForm.elements == undefined)
				return;
			for (var i = 0; i < theForm.elements.length; i++) {
				var e = theForm.elements[i];
				if (e.type == 'checkbox') {
					if (e.checked) {

						values.push(e.value);
						numberChecked++;
					}
				}
			}
			QM.queryInfo["selectedGroupBy"] = values
			$('#grp_by_col').val(values);
			QM.showCommand();

			QM.setHaving(numberChecked);
		},

		setHaving : function(number) {
			if (number == 0) {
				$('#having_tbl').find('input[type=checkbox]:checked').removeAttr(
						'checked');
				$("#having_col").val("");
				QM.showCommand();
			}
		},

		
		getAggregateFunctionDropDown : function(idprefix, colName, onChageFunc) {

			if (QM.colList.indexOf(colName) == -1 && colName != '*') {
				return "";
			}

			var dataType = QM.colMap[colName];
			var data = '<select id="'
					+ idprefix
					+ colName
					+ '" disabled="disabled" style="width:100%;" onchange="javascript:'
					+ onChageFunc + ';">';
			data += '<option value=""></option>';
			data += '<option value="COUNT">COUNT</option>';
			data += '<option value="COUNT(DISTINCT">DISTINCT COUNT</option>';
			if (dataType.toUpperCase() == 'INTEGER'
					|| dataType.toUpperCase() == 'LONG'
					|| dataType.toUpperCase() == 'DECIMAL'
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
		
		selectColumnOperationWrapper : function(value, isChecked) {
			QM.setColSearch(value, isChecked);

		},
		
		setColSearch : function(value, isChecked) {
			isConfirm = true;
			QM.selectColumnOperation(value, isChecked);
		},
		
		selectColumnOperation : function(value, isChecked) {
			QM.enableSelectAggregateFunction(value, isChecked);

			if (!(QM.isSetQueryRequest && isChecked == true)) {
				QM.setSelectedColumn(value, isChecked);
			}

			if (value == "*") {
				if (isChecked) {
					QM.searchColumn = [];
					QM.searchColumn.push(value);
					$('#searchColFilters').find('input[type=checkbox]:checked')
							.removeAttr('checked');
					$('#searchColFilters').find('select').attr('disabled',
							'disabled');
					$('#searchColFilters').find('select').prop('selectedIndex', 0);
					$('#srch_col_fld').val("*");
					$('#filterByALL').attr('checked', 'checked');

				}
			} else {
				if (QM.searchColumn.indexOf("*") != -1) {
					if (value != "*") {
						$('#filterByALL').removeAttr('checked');
						QM.searchColumn.splice(QM.searchColumn.indexOf("*"),
								QM.searchColumn.length);
						QM.searchColumn.push(value);
					} else {
						jQuery.inArray(value, QM.searchColumn);
						QM.searchColumn.splice(index, 1);
					}
				} else {
					var aggVal = $("#aggregate_" + value).val();
					if (aggVal != "") {
						if (aggVal.indexOf("DISTINCT") != -1)
							value = aggVal + " " + value + ")";
						else
							value = aggVal + "(" + value + ")";
					}
					// }
					var index = jQuery.inArray(value, QM.searchColumn);
					if (isChecked) {
						if (index == -1) // Not in array
							QM.searchColumn.push(value);
					} else {
						if (index != -1) // Present in array
							QM.searchColumn.splice(index, 1);
					}
				}
			}

			// set Header/footer/order by col according to selected col.
			$('#srch_col_fld').val(QM.searchColumn);
			QM.setOrderByDropDown();

		},
		
		enableSelectAggregateFunction : function(colName, isSelected) {

			if (isSelected) {
				$('#aggregate_' + colName).removeAttr('disabled');
			} else {
				$('#aggregate_' + colName).attr('disabled', 'disabled');
			}
		},

		
		setQueryFilterDetail : function(queryFilterObj, tablename) {
			QM.queryFilterObj = queryFilterObj;
			$('#query_filter_table').css('visibility', 'visible');
			$('#is_apply_query_filter').attr('checked', 'checked');
			$('#query_filter_sql').css('visibility', 'visible');

			RemoteManager.getQueryFilterTableName(tablename,
					$('#queryIONameNodeId').val(), function(tableName) {
						if (tableName == null || tableName == undefined) {
							jAlert("Table not found for input path filter");
							$('#is_apply_query_filter').removeAttr('checked',
									'checked');
							QM.applyQueryFilter();
							return;
						}
						var map = new Object();
						map[tableName] = false;
						// Fix for JTL files
						if (QM.stringEndsWith(tableName, "_CSV")) {
							var newTable = tableName.substr(0, tableName
									.lastIndexOf("_CSV"))
									+ "_JTL";
							map[newTable] = false;
						}
						QM.populateQueryFilterTableDiv(map, false);

					});

			var values = QM.queryFilterObj["selectedTable"]
			$('#query_filter_tables').val(values);

			var metastoreTableName = document.getElementById("queryIODatabase").options[0].value;

			RemoteManager.getAllAvailableTagsList(QM.selectedNameNode,
					metastoreTableName, values, function(tagListObject) {
				QM.populateWhereClauseForQueryFilter(tagListObject, true);
					});

		},
		
		populateWhereClauseForQueryFilter : function(tagListObject,
				isSettingHistoryObj) {

			var map = tagListObject["columnMap"];
			var tableSchema = tagListObject["tableSchema"];
			QM.selectedTableSchema = tableSchema;
			if (map == null || map == undefined)
				return;
			var list = new Array();

			for ( var attr in map) {
				list.push(attr);
			}
			QM.queryFilterColMap = map;

			var where_data_table = '<span id="select_wher_query_filter_close" class="divcloser"><a href="javascript:QM.showQueryFilterWhere(false);">'
					+ '<img src="images/light-box-close.png" class="closerImage"></a></span> <table id="query_filter_where_tbl">'
					+ '<tbody><tr><td nowrap="nowrap">Select Column</td><td nowrap="nowrap">Relational Operator</td><td nowrap="nowrap">Value</td><td nowrap="nowrap">Logical Operator</td></tr>';

			for (var i = 0; i < list.length; i++) {
				where_data_table += '<tr>';
				where_data_table += '<td nowrap="nowrap"><input type="checkbox" name="'
						+ list[i]
						+ '" id="query_filter_whereBy'
						+ list[i]
						+ '" value="'
						+ list[i]
						+ '" onclick="QM.setWhereClauseForQueryFilter(\''
						+ list[i]
						+ '\', this.checked);" > ' + list[i] + '</td>';
				where_data_table += '<td>'
						+ QM.getRelationalOperatorDropDown(list[i],
								'query_filter_r_operator_',
								'QM.selectRelationalFuncForQueryFilterWhere',
								QM.queryFilterColMap[list[i]]) + '</td>';
				where_data_table += '<td><input type="text" id="query_filter_whereval_'
						+ list[i]
						+ '" value="" onblur="javascript:QM.makeQueryFilterWhereClause();"></td>';
				where_data_table += '<td>'
						+ QM.getLogicalOperatorDropDown(list[i],
								'query_filter_loperator_',
								'QM.selectLogicalFuncForQueryFilterWhere')
						+ '</td>';
			}

			where_data_table += '</tbody></table>';
			$('#query_filter_where_div').html(where_data_table);

			if (isSettingHistoryObj) {
				QM.setWhereQueryFilterValues();
			}

		},

		setWhereQueryFilterValues : function() {

			var whereObj = QM.queryFilterObj["selectedWhere"];

			for ( var colName in whereObj) {

				QM.enableQueryFilterWhereComponent(colName, true);
				$('#query_filter_whereBy' + colName).attr('checked', 'checked');
				$('#query_filter_r_operator_' + colName).val(
						whereObj[colName]["roperator"]);
				$('#query_filter_loperator_' + colName).val(
						whereObj[colName]["loperator"]);
				$('#query_filter_whereval_' + colName).val(
						whereObj[colName]["value"]);
			}

			QM.makeQueryFilterWhereClause();
		}		
		
		selectRelationalFuncForQueryFilterWhere : function(value, columnName) {
			QM.makeQueryFilterWhereClause();
			QM.showQueryFilterCommand();
		},

		selectLogicalFuncForQueryFilterWhere : function() {
			QM.makeQueryFilterWhereClause();
			QM.showQueryFilterCommand();
		},
		
		setWhereClauseForQueryFilter : function(colName, isChecked) {
			QM.enableQueryFilterWhereComponent(colName, isChecked);
			if (!isChecked) {
				$('#query_filter_r_operator_' + colName).val('');
				$('#query_filter_whereval_' + colName).val('');
				$('#query_filter_loperator_' + colName).val('');
			}
			QM.makeQueryFilterWhereClause();
		},
		enableQueryFilterWhereComponent : function(colName, isSelected) {
			if (isSelected) {
				$('#query_filter_r_operator_' + colName).removeAttr('disabled');
				$('#query_filter_whereval_' + colName).removeAttr('disabled');
				$('#query_filter_loperator_' + colName).removeAttr('disabled');

			} else {
				$('#query_filter_r_operator_' + colName).attr('disabled',
						'disabled');
				$('#query_filter_whereval_' + colName).attr('disabled', 'disabled');
				$('#query_filter_loperator_' + colName)
						.attr('disabled', 'disabled');
			}
		},

		makeQueryFilterWhereClause : function() {

			var frm = document.getElementById('query_filter_where_tbl')
					.getElementsByTagName("input");
			var len = frm.length;
			// 
			var cond = '';
			for (i = 0; i < len; i++) {
				if (frm[i].type == "checkbox") {

					if (frm[i].checked) {

						var colName = frm[i].value;

						cond += colName;
						cond += $('#query_filter_r_operator_' + colName).val()
								+ ' ';

						var colDataType = QM.queryFilterColMap[colName];
						var operator = $('#query_filter_r_operator_' + colName)
								.val();
						if ((colDataType.toUpperCase() == "STRING"
								|| colDataType.toUpperCase().indexOf("BYTE") != -1 || colDataType
								.toUpperCase() == 'TIMESTAMP')
								&& operator.toUpperCase().indexOf("IN") == -1
								&& operator.toUpperCase().indexOf("IS NULL") == -1
								&& operator.toUpperCase().indexOf("IS NOT NULL") == -1) {
							cond += " '"
									+ $('#query_filter_whereval_' + colName).val()
									+ "' ";
						} else {
							cond += ' '
									+ $('#query_filter_whereval_' + colName).val()
									+ ' ';
						}

						cond += $('#query_filter_loperator_' + colName).val() + ' ';

						if (!QM.queryInfo["queryFilterDetail"]
								.hasOwnProperty("selectedWhere")) {
							QM.queryInfo["queryFilterDetail"]["selectedWhere"] = new Object();
						}
						if (!QM.queryInfo["queryFilterDetail"]["selectedWhere"]
								.hasOwnProperty(colName)) {
							QM.queryInfo["queryFilterDetail"]["selectedWhere"][colName] = new Object();
						}
						QM.queryInfo["queryFilterDetail"]["selectedWhere"][colName]["roperator"] = $(
								'#query_filter_r_operator_' + colName).val();
						QM.queryInfo["queryFilterDetail"]["selectedWhere"][colName]["loperator"] = $(
								'#query_filter_loperator_' + colName).val();
						QM.queryInfo["queryFilterDetail"]["selectedWhere"][colName]["value"] = $(
								'#query_filter_whereval_' + colName).val();

					}
				}
			}
			$('#query_filter_where').val(cond);
			QM.showQueryFilterCommand();
		},

		showQueryFilterCommand : function() {

			var query = 'SELECT ';
			query += ' filepath ';
			query += ' FROM ' + $('#query_filter_tables').val();
			if ($('#query_filter_where').val() != "") {

				query += ' WHERE ' + $('#query_filter_where').val();
			}
			$('#query_filter_sql').val(query);
			QM.queryInfo["queryFilterDetail"]["filterQuery"] = query;

		},
		
		onlyPopulateTableNameDiv : function(map) {

			var list = [];
			QM.tableMap = map;

			for ( var tableName in map) {
				list.push(tableName);
			}

			if (list == null || list.length == 0) {
				jAlert(
						"There is no data available for selected namenode.Please upload data from Data Import/Export tab.",
						"Error");
				return;
			}
			Navbar.isDataAvailabe = true;
			var data = '<form name="tables" id="tables">';
			data += '<table><tr><td colspan="2"><span>Search On:</span><span id="selectColClose" class="divcloser"><a href="javascript:QM.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span></td></tr>';
			for (var i = 0; i < list.length; i++) {
				var tbl_name = list[i];
				if (i == 0) {
					data += '<tr><td><input checked checked="checked" type="checkbox" name="nnID[]" id="filterBy'
							+ tbl_name
							+ '" value="'
							+ tbl_name
							+ '" onclick="QM.setLocationSearch(\''
							+ tbl_name
							+ '\',this.checked);" > </td><td>'
							+ tbl_name
							+ '</td></tr>';
				} else {
					data += '<tr><td><input type="checkbox" name="nnID[]" id="filterBy'
							+ tbl_name
							+ '" value="'
							+ tbl_name
							+ '" onclick="QM.setLocationSearch(\''
							+ tbl_name
							+ '\',this.checked);" > </td><td>'
							+ tbl_name
							+ '</td></tr>';
				}
			}
			data += '</table></form>';
			$('#searchFromFilters').html(data);

			$('#searchFromFilters').find('input[type=checkbox]:checked')
					.removeAttr('checked');
			var obj = QM.queryInfo["selectedTable"];
			for (var i = 0; i < obj.length; i++) {
				$('#filterBy' + obj[i]).attr('checked', 'checked');
			}

		},
		
		setHiveFlag : function(nameNodeId, dbName) {
			RemoteManager.getAllDBNameWithTypeForNameNodeMapping(nameNodeId,
					function(list) {
						if (list["Metastore"] == dbName)
							QM.isHive = false;
						else if (list["Hive"] == dbName)
							QM.isHive = true;
						if (QM.isHive) {
							$('#query_filter_table').css('visibility', 'visible');
						} else {
							$('#query_filter_table').css('visibility', 'hidden');
							$('#is_apply_query_filter').removeAttr('checked')
							$('#query_filter_sql').css('visibility', 'hidden');
							QM.applyQueryFilter();
						}
					});
		},
		
		setLocationSearch : function(value, isChecked) {
			if (!QM.isTableSelectedByUser) {
				QM.selectTableOperation(value);
				QM.isTableSelectedByUser = true;
				return;
			}
			QM.checkForAdded = true;

			Navbar.queryManagerDirtyBit = true;
			QM.selectTableOperation(value);

		},
		
		selectTableOperation : function(value) {
			var flag = false;
			var values = [];
			if (value == undefined)
				return;

			$('#resultTableName').val(value);
			var nameNodeId = $('#queryIONameNodeId').val();

			RemoteManager.getResultTableName(value, nameNodeId,
					QM.fetchResultTableName);

			if (QM.tableMap[value]) {

				if ($('#filterBy' + value).is(':checked')) {
					flag = true;
				} else {
					flag = false;
				}
				if (flag) {
					// $('#srch_from_fld').width('70%');
					// $('#resultTableNameSpan').show();
					// $('#resultTableName').removeAttr('disabled');

					values.push(value);
					var theForm = document.getElementById("tables");
					if (theForm == null || theForm == undefined
							|| theForm.elements == null
							|| theForm.elements == undefined)
						return;
					for (var i = 0; i < theForm.elements.length; i++) {
						var e = theForm.elements[i];
						if (e.type == 'checkbox') {
							e.checked = false;
						}
					}

					$('#filterBy' + value).attr('checked', 'checked');
				}

			}
			if (!flag) {

				var theForm = document.getElementById("tables");
				if (theForm == null || theForm == undefined
						|| theForm.elements == null
						|| theForm.elements == undefined)
					return;

				for (var i = 0; i < theForm.elements.length; i++) {
					var e = theForm.elements[i];
					if (e.type == 'checkbox') {
						var val = (e.value).toUpperCase() + '';
						if (val.indexOf('ADHOC') == 0) {
							e.checked = false;
							continue;
						}
						if (e.checked) {

							values.push(e.value);
						}
					}
				}
			}
			QM.searchFrom = values;

			$('#srch_from_fld').val(values);
			QM.queryInfo["selectedTable"] = QM.searchFrom;
			RemoteManager.getAllAvailableTagsList(QM.selectedNameNode,
					QM.selectedDbName, values, QM.populateSearchColNames);
		},
		
		resetQueryInfoJSON : function() {

			QM.queryInfo = new Object();
			QM.queryInfo["queryId"] = "";
			QM.queryInfo["queryDesc"] = "";
			QM.queryInfo["sqlQuery"] = "";
			QM.queryInfo["colDetail"] = new Object();

			QM.queryInfo["selectedColumn"] = new Object();
			QM.queryInfo["selectedTable"] = new Array();
			QM.queryInfo["selectedWhere"] = new Object();
			QM.queryInfo["selectedOrderBy"] = new Array();
			QM.queryInfo["selectedGroupBy"] = new Array();
			QM.queryInfo["selectedHaving"] = new Object();

			QM.queryInfo["setLimitResultRows"] = true;
			QM.queryInfo["limitResultRowsValue"] = 300;
			QM.queryInfo["namenode"] = $('#queryIONameNodeId').val();
			QM.queryInfo["dbName"] = "";
			QM.queryInfo["persistResults"] = false;

			QM.queryInfo["isFilterQuery"] = false
			QM.queryInfo["queryFilterDetail"] = new Object();
			QM.queryInfo["queryFilterDetail"]["filterQuery"] = '';
			QM.queryInfo["queryFilterDetail"]["selectedWhere"] = new Object();
			QM.queryInfo["queryFilterDetail"]["selectedTable"] = new Array();

		},
		
		setQueryDirtyBitHandlerEvent : function() {
			$('input[type=checkbox]').click(function() {
				Navbar.queryManagerDirtyBit = true;
			});

			$('select').change(function() {
				if ((this.id != "queryIONameNodeId") && (this.id != "bigQueryIds"))
					Navbar.queryManagerDirtyBit = true;
			});

			$('input[type=text]').keypress(function() {
				Navbar.queryManagerDirtyBit = true;
			});

			$('textarea').keypress(function() {
				Navbar.queryManagerDirtyBit = true;
			});

			$('input[type=search]').keypress(function() {
				Navbar.queryManagerDirtyBit = true;
			});
			$('input[type=search]').change(function() {
				Navbar.queryManagerDirtyBit = true;
			});
			
		},
		
		querySaveResponse : function(response) {
			var id = QM.selectedQueryId;

			if (response.taskSuccess) {
				status = "Success";
				imgId = "popup.image.success";

			} else {
				status = "Failure";
				imgId = "popup.image.fail";
			}

			message = response.responseMessage;

			dwr.util.byId('popup.image.processing' + id).style.display = 'none';
			dwr.util.byId(imgId + id).style.display = '';

			dwr.util.setValue('popup.message' + id, message);
			dwr.util.setValue('popup.status' + id, status);
			dwr.util.byId('ok.popup').disabled = false;
		},
		
		processDeleteQueryResponse : function(dwrResponse){
			console.log("dwrResponse: ", dwrResponse);
			var id=dwrResponse.id;
			if(dwrResponse.taskSuccess){
				img_src='images/Success_img.png'
				status = 'Success'; 
				dwr.util.byId('imageSuccess' + id).style.display = '';
				var userId = $('#loggedInUserId').text();
				var obj =  Util.getCookie("last-visit-query"+userId);
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
							Util.setCookie("last-visit-cquery"+userId,JSON.stringify(filePathObj), 15);
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
			Navbar.selectedQueryId = '';
//			Navbar.refreshNavBar();
			
			
		},
		showQuery : function(){
			Navbar.selectedQueryId= QM.selectedQueryArray[0];
			Navbar.isEditQuery=true;
			
			Navbar.changeTab('Queries','queries','edit_queries');
			
		},
		closeDeleteQueryBox : function(){
			QM.closeBox(true);
		},
		fillExecuteTab : function(obj){
			
		},
		
		closeBox : function(isRefresh) {
			Util.removeLightbox("export");
			if ((QM.isSave) || (QM.isDelete)) {
				QM.isSave = false;
				QM.isDelete = false;
				if (isRefresh)
					Navbar.refreshView();
			}
		},
		
		checkID : function(count) {
			$('#bigQueryIds').find('option').each(function() {
				if ("New Query " + count == $(this).val()) {
					count = QM.checkID(count + 1);
				}
			});
			return (count);
		},
		
		populateQueryFilterTableDiv : function(map, isSettingHistoryObj) {
			var list = [];

			for ( var tableName in map) {
				list.push(tableName);
			}

			if (list == null || list.length == 0) {
				jAlert(
						"There is no data available for selected namenode.Please upload data from Data Import/Export tab.",
						"Error");
				return;
			}

			var data = '<form name="filter_tables" id="filter_tables">';
			data += '<table><tr><td colspan="2"><span>Search On:</span><span id="selectTableClose" class="divcloser"><a href="javascript:QM.showQueryFilterTables(false);"><img src="images/light-box-close.png" class="closerImage"></a></span></td></tr>';
			for (var i = 0; i < list.length; i++) {
				var tbl_name = list[i];
				if (i == 0) {
					data += '<tr><td><input checked checked="checked" type="radio" name="nnID[]" id="query_filter_by'
							+ tbl_name
							+ '" value="'
							+ tbl_name
							+ '" onclick="QM.setTableForQueryFilter(\''
							+ tbl_name
							+ '\',this.checked);" > </td><td>'
							+ tbl_name
							+ '</td></tr>';
				} else {
					data += '<tr><td><input type="radio" name="nnID[]" id="query_filter_by'
							+ tbl_name
							+ '" value="'
							+ tbl_name
							+ '" onclick="QM.setTableForQueryFilter(\''
							+ tbl_name
							+ '\',this.checked);" > </td><td>'
							+ tbl_name
							+ '</td></tr>';
				}
			}
			data += '</table></form>';
			$('#query_filter_tables_div').html(data);

			if (isSettingHistoryObj) {

				// if setting hobj from history
				var tables = QM.queryFilterObj["selectedTable"];
				for (var i = 0; i < tables.length; i++) {
					$('#query_filter_by' + tables[i]).attr('checked', 'checked');
				}

			} else {

				QM.setTableForQueryFilter();
			}

		},
		
		setTableForQueryFilter : function() {

			var flag = false;
			var values = [];

			if (!flag) {

				var theForm = document.getElementById("filter_tables");
				if (theForm == null || theForm == undefined
						|| theForm.elements == null
						|| theForm.elements == undefined)
					return;

				for (var i = 0; i < theForm.elements.length; i++) {
					var e = theForm.elements[i];
					if (e.type == 'radio') {
						if (e.checked) {
							values.push(e.value);
						}
					}
				}
			}

			$('#query_filter_tables').val(values);

			QM.queryInfo["queryFilterDetail"]["selectedTable"] = values;

			var metastoreTableName = document.getElementById("queryIODatabase").options[0].value;

			RemoteManager.getAllAvailableTagsList(QM.selectedNameNode,
					metastoreTableName, values, function(tagListObject) {
				QM.populateWhereClauseForQueryFilter(tagListObject, false);
					});
			QM.showQueryFilterCommand();

		},
		
		stringEndsWith : function(str, suffix) {
			return (str.indexOf(suffix, str.length - suffix.length) !== -1);
		},
		
		getQueryInfoObject : function() {
			var queryId = $('#queryId').val();
			var queryDesc = $('#queryDesc').val();
			var sqlQuery = $('#query_textarea').val();

			QM.queryInfo["queryId"] = queryId;
			QM.queryInfo["queryDesc"] = queryDesc;
			QM.queryInfo["sqlQuery"] = sqlQuery;
			QM.queryInfo["executionId"] = QM.currentExecutionId;
			QM.queryInfo["setLimitResultRows"] = QM.getLimitResultRowsState();
			QM.queryInfo["limitResultRowsValue"] = $('#limitResultRowsValue').val();

		},

		getLimitResultRowsState : function() {
			return $('#limitResultRows').is(':checked');
		},


};