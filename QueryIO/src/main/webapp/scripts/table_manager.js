TM={
		
		ready : function(){
			TM.selectedNameNode = $("#queryIONameNodeId").val();
			if(TM.selectedNameNode==""||TM.selectedNameNode==null){
				$('#table_list_table_div').html('There is no Namespace configured currently. Please setup a cluster and import data to cluster to use Query and Analysis features.');
				$('#table_list_table_div').css('text-align','center');
				return;
			}
			TM.populateTableSummaryTable();
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
				
				if (CM.selectedChartArray.length < 1)
				{
					dwr.util.byId('tableClone').disabled=true;
   					dwr.util.byId('tableEdit').disabled=true;
   					dwr.util.byId('tableDelete').disabled=true;
				}
				else
				{
					if (CM.selectedChartArray.length == 1)
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
			Navbar.selectedTableId = chartId;
			Navbar.isFromsummaryView = true;
			var nameNodeId = TM.selectedNameNode;
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
			var nameNodeId = TM.selectedNameNode;
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
							Util.setCookie("last-visit-cchart"+userId,JSON.stringify(filePathObj), 15);
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
		fillExecuteTab : function(obj){
			
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

};