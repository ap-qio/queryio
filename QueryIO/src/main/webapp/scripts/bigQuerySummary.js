BQS={
		queryCache : new Object,
		totalQuery:1,
		isExecuteQuery :false,
		isEditQuery :false,
		selectedNameNode : '',
		selectedQueryArray :new Array(),
		queryArray :new Array(),
		isNewQuery : false,
		emailEnabled : false,
		
		
		ready : function(){
			BQS.queryCache = new Object;
			BQS.totalQuery=1;
			BQS.isExecuteQuery =false;
			BQS.isEditQuery =false;
			BQS.selectedNameNode ='';
			BQS.selectedQueryArray =new Array();
			BQS.selectedNameNode = $("#queryIONameNodeId").val();
			if(BQS.selectedNameNode==""||BQS.selectedNameNode==null){
				$('#query_list_table_div').html('There is no Namespace configured currently. Please setup a cluster and import data to cluster to use Query and Analysis features.');
				$('#query_list_table_div').css('text-align','center');
				return;
			}
			DA.selectedNameNode=BQS.selectedNameNode;
			BQS.populateBigQuerySummaryTable();
			DA.selectedDbName=$("#queryIODatabase").val();
			RemoteManager.getNotificationSettings(BQS.setNotification);
		},
		
		populateBigQuerySummaryTable : function(){
			$('#query_list_table').dataTable( {
	   			"sScrollX": "100%",
	   			"bPaginate": true,
				"bLengthChange": true,
				"sPaginationType": "full_numbers",
				"aLengthMenu" : [50, 100, 200, 500 ],
				"bFilter": false,
				"bSort": true,
				"bInfo": true,
				"bDestroy": true,
				"serverSide": true,
				"searching": true,
				"aoColumnDefs": [{ "bSortable": false, "aTargets": [ 0 ] }],
				"fnServerData": function ( sSource, aoData, fnCallback, oSettings ) {
					RemoteManager.getAllBigQueriesInfoWithDataParam(BQS.selectedNameNode, JSON.stringify(aoData), {
			            async:false,
			            callback:function(result){
			                 fnCallback(BQS.fillQuerySummaryTable(result));	
			                 $(window).trigger('resize');
			                }});
					
					},
				"bAutoWidth": true,
		        "aoColumns": [
		            { "sTitle": '<input type="checkbox" value="selectAll" id="selectAll" onclick="javascript:BQS.selectAllQuery(this.id)">'},
		            { "sTitle": "QueryID" },
		            { "sTitle": "Description" },
		            { "sTitle": "Status" },
		            { "sTitle": "Database" }
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
		
		fillQuerySummaryTable : function(bigQueryData){
			
			BQS.selectedQueryArray.splice(0, BQS.selectedQueryArray.length);
			document.getElementById('selectAll').checked = false;
			BQS.toggleButton("selectAll", false);
			var object = bigQueryData["data"];
			var tableList = new Array();
			var qCache = new Object;
			if(object!=null)
	   		{
				    BQS.queryArray.splice(0,BQS.queryArray.length);
					for (var i=0; i< object.length;i++)
					{
						var queryData = object[i];
						var queryId = queryData[0]; // queryID
						var description = queryData[1]; // query description
						var datasource = queryData[2]; // query Namenode Id
						var dbName = queryData[3]; // database name
						var status = queryData[4]; //query status
						var queryc = {};
						queryc["id"] = queryId;
						queryc["description"] = description;
						queryc["namenode"] = datasource;
						queryc["dbName"] = dbName;
						queryc["status"] = status;
						queryc["reportpath"] = queryData[5];
						
						BQS.totalQuery++;
			   			BQS.queryArray.push(queryId);
						var check='<input type="checkbox" id="'+queryId+'" onclick="javascript:BQS.clickBox(this.id);">';
//						var description=query["description"];
//						var datasource=query["namenode"];
//						var dbName=query["dbName"];
//						var status = query["status"];
						
						qCache[queryId] = queryc;
//						if(status=="SUCCESS"){
//							var path='Reports/Birt'+query["reportpath"];
							queryId='<a href = "javascript:BQS.showQueryReport(\''+queryId+'\');">'+queryId+"</a>";
//						}
						if(status!=null&&status!='')
							status=status.substring(0,1)+status.substring(1,status.length).toLowerCase();
						tableList.push([check,queryId,description,status,dbName]);
		   			}
		  	}
			bigQueryData["data"] = tableList;
			BQS.queryCache = qCache;
			return bigQueryData;
		},
		
		clickBox : function(id)
		{
			var flag = document.getElementById(id).checked;
			if (flag == true)
			{
				BQS.selectedQueryArray.push(id.toString());
			}
			else
			{
				var index = jQuery.inArray(id.toString(), BQS.selectedQueryArray);
				if (index != -1)
				{
					BQS.selectedQueryArray.splice(index, 1);
				}
			}
			if(($('#query_list_table tr').length - 1) == BQS.selectedQueryArray.length)
			{
				document.getElementById("selectAll").checked = flag;
				BQS.selectAllQuery("selectAll", flag);
			}
			else
				BQS.toggleButton(id, flag, "selectAll");
		},
  		selectAllQuery : function(id)
   		{

  			var flag = document.getElementById(id).checked;
  			
  			BQS.selectedQueryArray.splice(0, BQS.selectedQueryArray.length);
  			for (var i=0; i<BQS.queryArray.length; i++)
  			{
  				document.getElementById(BQS.queryArray[i]).checked = flag;
  				if (flag)
  				{	
  					BQS.selectedQueryArray.push(BQS.queryArray[i]);
  				}
  			}
  			BQS.toggleButton(id, flag);
   		},
   		toggleButton : function(id , value)
   		{

   			if (id == "selectAll")
   			{
   				if (BQS.selectedQueryArray.length == 1)
   				{
   					dwr.util.byId('queryEdit').disabled=false;
   					dwr.util.byId('queryClone').disabled=false;
   					dwr.util.byId('queryExecute').disabled=false;
   					dwr.util.byId('queryExport').disabled=false;
   					dwr.util.byId('queryEmail').disabled=false;
   				}
   				else
   				{
   					dwr.util.byId('queryClone').disabled=true;
   					dwr.util.byId('queryEdit').disabled=true;
   					dwr.util.byId('queryExecute').disabled=true;
   					dwr.util.byId('queryExport').disabled=true;
   					dwr.util.byId('queryEmail').disabled=true;
   				}
   				dwr.util.byId('queryDelete').disabled=!value;
				dwr.util.byId('querySchedule').disabled=!value;

   				
   			}
   			else
   			{
				if(value == false)
					$('#selectAll').attr("checked",false);
				
				if (BQS.selectedQueryArray.length < 1)
				{
					dwr.util.byId('queryClone').disabled=true;
   					dwr.util.byId('queryEdit').disabled=true;
   					dwr.util.byId('queryExecute').disabled=true;
   					dwr.util.byId('queryExport').disabled=true;
   					dwr.util.byId('queryEmail').disabled=true;
   					dwr.util.byId('queryDelete').disabled=true;
   					dwr.util.byId('querySchedule').disabled=true;
				}
				else
				{
					if (BQS.selectedQueryArray.length == 1)
					{
						dwr.util.byId('queryEdit').disabled=false;
	   					dwr.util.byId('queryClone').disabled=false;
	   					dwr.util.byId('queryExecute').disabled=false;
	   					dwr.util.byId('queryExport').disabled=false;
	   					dwr.util.byId('queryEmail').disabled=false;
					}
					else
					{
						dwr.util.byId('queryClone').disabled=true;
	   					dwr.util.byId('queryEdit').disabled=true;
	   					dwr.util.byId('queryExecute').disabled=true;
	   					dwr.util.byId('queryExport').disabled=true;
	   					dwr.util.byId('queryEmail').disabled=true;						
					}
					dwr.util.byId('queryDelete').disabled=false;
					dwr.util.byId('querySchedule').disabled=false;
				}
   				
   		
   			}
   		},
		addNewQuery : function(){
			BQS.isEditQuery = false;
			BQS.isNewQuery = true;
			Navbar.isAddNewQuery=true;
			Navbar.isFromsummaryView = true;
			Navbar.changeTab('QueryDesigner','analytics','QueryDesigner');
		},
		editSelectedQuery : function(){
			Navbar.isEditQuery = true;
			var queryId = BQS.selectedQueryArray[0]+'';
			Navbar.selectedQueryId = queryId;
			Navbar.isFromsummaryView = true;
			var nameNodeId = BQS.selectedNameNode;
			Navbar.changeTab('QueryDesigner','analytics','QueryDesigner');
		},
		backToSummary : function(){
			$('#refreshViewButton').attr('onclick','javascript:Navbar.refreshView()');
			Navbar.refreshView();
		},
		fillSavedQuery : function(obj){
		},
		showQueryReport: function(queryId){
			Navbar.isEditQuery = true;
			Navbar.selectedQueryId = queryId;
			Navbar.isFromsummaryView = true;
			BQS.selectedQueryArray[0] =queryId 
			var nameNodeId = BQS.selectedNameNode;
			Navbar.changeTab('QueryDesigner','analytics','QueryDesigner');
			
		},
		
		cloneSelectedQuery : function(){
			
			DA.selectedQueryId = BQS.selectedQueryArray[0]
			
			Util.addLightbox("addclone", "resources/cloneQuery.html", null, null);
		},
		fillCloneQueryObject : function(object){
			
			DA.queryInfo =jQuery.extend(true, {},object);
		},
		deleteSelectedQuery : function(){
			DA.selectedQueryId = BQS.selectedQueryArray[0];
			DA.deleteQuery();
			
		},
		populateDeleteQueryBox : function(){
			
			var queryIdArray  = BQS.selectedQueryArray;
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
				RemoteManager.deleteBigQuery(nameNode, id, BQS.processDeleteQueryResponse);
				
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
							Util.setCookie("last-visit-query"+userId,JSON.stringify(filePathObj), 15);
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
		executeSelectedQuery : function(){
			Navbar.selectedQueryId= BQS.selectedQueryArray[0];
			Navbar.isExecuteQuery=true;
			Navbar.changeTab('QueryViewer','analytics','QueryViewer')
			
		},
		closeDeleteQueryBox : function(){
			DA.closeBox(true);
		},
		fillExecuteTab : function(obj){
			
		},
		exportSelectedReport: function(){
			
			for(var attr in BQS.queryCache)
			{
				var query=BQS.queryCache[attr]; 
	   			if(query["id"]==this.selectedQueryArray[0])
	   			{
	   					DA.selectedQueryId = BQS.selectedQueryArray[0];
	   					DAT.exportReport(query["id"]);
	   				break;
				}
   			}
   		},
   		emailSelectedReport : function(){
   			if(!BQS.emailEnabled)
			{
				jAlert("You have not configured email notifications. Please configure notifications and return to this wizard. To configure notifications, go to <b>Dashboard > Notifications</b> tab..","Error");
				$("#popup_container").css("z-index","99999999");
				return;
			}
   			for(var attr in BQS.queryCache)
			{
				var query=BQS.queryCache[attr]; 
	   			if(query["id"]==this.selectedQueryArray[0])
	   			{
	   				
	   					DA.selectedQueryId = BQS.selectedQueryArray[0];
	   					DAT.emailReport(query["id"]);
	   				break;
				}
   			}
   		},
   		redirectOnDBManagerView : function(){
   			Navbar.showSelectedDb = true;
   			Navbar.changeTab('DB_Config','analytics', 'db_Config');
   			
   		},
   		
   		scheduleSelectedReport: function(){
   			Util.addLightbox("export", "resources/scheduleQuery.html", null, null);
   		},
   		
   		setNotification : function(nbean)
		{
			BQS.emailEnabled = nbean.emailEnabled;
		},
};