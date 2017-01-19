QM={
		queryCache : new Object,
		totalQuery:1,
		isEditQuery :false,
		selectedNameNode : '',
		selectedQueryArray :new Array(),
		queryArray :new Array(),
		isNewQuery : false,
		
		ready : function(){
			QM.selectedNameNode = $("#queryIONameNodeId").val();
			if(QM.selectedNameNode==""||QM.selectedNameNode==null){
				$('#query_list_table_div').html('There is no Namespace configured currently. Please setup a cluster and import data to cluster to use Query and Analysis features.');
				$('#query_list_table_div').css('text-align','center');
				return;
			}
			DA.selectedNameNode=QM.selectedNameNode;
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
			
			QM.selectedQueryArray.splice(0, QM.selectedQueryArray.length);
			document.getElementById('selectAll').checked = false;
			QM.toggleButton("selectAll", false);
			var object = queryData["data"];
			var tableList = new Array();
			var qCache = new Object;
			if(object!=null)
	   		{
				    QM.queryArray.splice(0,QM.queryArray.length);
					for (var i=0; i< object.length;i++)
					{
						var queryData = object[i];
						var queryId = queryData[0]; // queryID
						var description = queryData[1]; // query description
						var database = queryData[2]; // query description
						var queryc = {};
						queryc["id"] = queryId;
						queryc["description"] = description;
						queryc["database"] = database;
						
						QM.totalQuery++;
			   			QM.queryArray.push(queryId);
						var check='<input type="checkbox" id="'+queryId+'" onclick="javascript:QM.clickBox(this.id);">';
						
						qCache[queryId] = queryc;
//						if(status=="SUCCESS"){
//							var path='Reports/Birt'+query["reportpath"];
//						}
						tableList.push([check,queryId,description,database]);
		   			}
		  	}
			queryData["data"] = tableList;
			QM.queryCache = qCache;
			return queryData;
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
			
			DA.selectedQueryId = QM.selectedQueryArray[0]
			
			Util.addLightbox("addclone", "resources/cloneQuery.html", null, null);
		},
		fillCloneQueryObject : function(object){
			
			DA.queryInfo =jQuery.extend(true, {},object);
		},
		deleteSelectedQuery : function(){
			DA.selectedQueryId = QM.selectedQueryArray[0];
			DA.deleteQuery();
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
		processDeleteQueryResponse : function(dwrResponse){
			
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
			DA.closeBox(true);
		},
		fillExecuteTab : function(obj){
			
		},

};