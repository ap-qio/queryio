BQV = {
		currentSelectedTab:'',
		emailEnabled : false,
		
		tabClicked : function(tabId){
			BQV.currentSelectedTab=tabId;
			var executionId = tabId.substring(tabId.indexOf('_')+1,tabId.length);
			var queryId = '';
			$('#firstmsg').remove();
			queryId = DAT.idInfoMap[executionId];
			DAT.loadReportInTab(queryId, executionId, DAT.filePathMap[executionId]);
		},
		
		ready : function()
		{
			$('#refreshViewButton').css('display','none');
			DAT.filePathMap = new Object();
			var selectedNameNode = $('#queryIONameNodeId').val();
			if(!$('select#queryIONameNodeId option').length>0){
				$('#firstmsg').text("There is no Namespace configured currently. Please setup a cluster and import data to cluster to use Query and Analysis features.");
				$('#firstmsg').css('padding-left','35%');
				//jAlert("There is no NameNode present in cluster.Please install a NameNode before using BigQuery features.","Error");
				return;
			}
			RemoteManager.getAllBigQueriesInfo(selectedNameNode, BQV.fillAllQueryIds);
			
			BQV.populateViewTabs();
			RemoteManager.getNotificationSettings(BQV.setNotification);
			
		},
		
		setNotification : function(nbean)
		{
			BQV.emailEnabled = nbean.emailEnabled;
		},
		
		populateViewTabs : function()
		{
			
			DAT.$tabs = $("#queryTabs").tabs();
			
			DAT.$tabs.tabs('paging', { cycle: false, follow: true, selectOnAdd: true } );
			
			DAT.$tabs.tabs({
				tabTemplate: "<li><a style = 'font-size: 13px;' onclick=\"javascript:BQV.tabClicked('#{href}');\" href='#{href}'>#{label}</a> <span id='#{label}' class='ui-icon ui-icon-close' style='cursor: pointer;'></span></li>",
			    add: function( event, ui ) {
			    },
		 		remove: function(event, ui) {
		 				
			    	
		 		}
			});
			
			$('#queryTabs').on('click', 'span.ui-icon-close', function() {
				var index = $( "li", DAT.$tabs ).index( $( this ).parent() );
			    var removedId = $( this )[0].id;
			    
			    DAT.$tabs.tabs("remove",index);
			    if (removedId != undefined)
			    {
			    	
			    	var queryId = removedId.substr(removedId.indexOf('-') + 1);
			    	
			    	for (var i in DAT.idInfoMap)
					{
			    		if (DAT.idInfoMap[i] == queryId)
			    		{ 
			    			
			    			delete DAT.filePathMap[i];
						    delete DAT.idInfoMap[i];
						    
						    var userId = $('#loggedInUserId').text();
							Util.setCookie("last-visit-query"+userId,JSON.stringify(DAT.filePathMap), 15);
							Util.setCookie("last-visit-idInfoMap"+userId,JSON.stringify(DAT.idInfoMap), 15);
			    		}
					}
			    }
			});
		},
		fillAllQueryIds : function(object){
			var data = '<option value=""></option>';
			var flag=true;
			if(object!=null)
	   		{
				data="";
					for(var attr in object)
					{
						var query=object[attr]; 
			   			var queryId = query["id"];
			   			if(flag){
			   				Navbar.selectedQuery=queryId;
			   				data += '<option value="'+queryId+'" selected="selected">'+queryId+'</option>';
			   				flag=false;
			   				continue;
			   			}
			   			data += '<option value="'+queryId+'">'+queryId+'</option>';
					}
					
		  	}
			if(!flag){
				$('#printReport').removeAttr('disabled');
				$('#exportReport').removeAttr('disabled');
				$('#emailReport').removeAttr('disabled');
				$('#queryExecute').removeAttr('disabled');
			}
			$('#bigQueryIds').html(data);
			BQV.populateViewerView();
			
			
		},
		populateViewerView : function(){
			
			if(Navbar.isExecuteQuery){
				if(Navbar.selectedQueryId!=null&&Navbar.selectedQueryId!=''&&$('select#bigQueryIds option').length>0){
					$('#bigQueryIds').val(Navbar.selectedQueryId);
					BQV.executeSeletedQuery();
					Navbar.isExecuteQuery=false;
				}
			}
		},
		executeSeletedQuery : function(){
			
			
			$('#firstmsg').remove();
			var namenode = $('#queryIONameNodeId').val();
			var queryId = $('#bigQueryIds').val();

			DA.removeQueryFromCached(queryId);
//			var queryId = '';
			
//			for(var attr in DAT.idInfoMap){
//				if(executionId==attr){
//					queryId =DAT.idInfoMap[attr];
//					break;
//				}
//			}
//			for(var attr in DAT.idInfoMap){
//				if(DAT.idInfoMap[attr]==queryId&&attr!=executionId){
//					$('#litab_'+attr).remove();
//					$('#tab_'+attr).remove();
//					delete DAT.idInfoMap[attr];
//				}
//			}
			
			
//			for(var attr in DAT.idInfoMap){
//				if(DAT.idInfoMap[attr]==queryId){
//					var id ="#tab_"+attr
//					DAT.$tabs.tabs("select", id);
//					return;
//				}
//			}
			DAT.executeCommand(queryId);
		},
		exportReport : function()
		{
			var tabId = BQV.currentSelectedTab;
			if(tabId!=''||tabId!=null){
					var attr = tabId.substring(tabId.indexOf('_')+1,tabId.length);
					var queryId = DAT.idInfoMap[attr];
					if(queryId==undefined||queryId==null||queryId==''){
						queryId=$('#bigQueryIds').val();
					}
					var path = DAT.filePathMap[attr];
					DAT.exportReport(queryId);
			}
		},
		
		emailReport : function(filePath,queryId)
		{
			if(!BQV.emailEnabled)
			{
				jAlert("You have not configured email notifications. Please configure notifications and return to this wizard. To configure notifications, go to <b>Dashboard > Notifications</b> tab..","Error");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			var tabId = BQV.currentSelectedTab;
			if(tabId!=''||tabId!=null){
					var attr = tabId.substring(tabId.indexOf('_')+1,tabId.length);
					var queryId = DAT.idInfoMap[attr];
					if(queryId==undefined||queryId==null||queryId==''){
						queryId=$('#bigQueryIds').val();
					}
					var path = DAT.filePathMap[attr];
					DAT.emailReport(queryId);
			}
		},
		
		printReport : function(){
			var tabId = BQV.currentSelectedTab;
			if(tabId!=''||tabId!=null){
				var attr = tabId.substring(tabId.indexOf('_')+1,tabId.length);
				var queryId = DAT.idInfoMap[attr];
				if(queryId==undefined||queryId==null||queryId==''){
					queryId=$('#bigQueryIds').val();
				}
				var path = DAT.filePathMap[attr];
				var userName = Util.getLoggedInUserName();
				var nameNodeId = $('#queryIONameNodeId').val();
				nameNodeId = nameNodeId.replace(/ /g,"_").toLowerCase();
				var filepath = "Reports/" + userName + "/" + nameNodeId + "/" + path;
				var mywindow = window.open(filepath, 'spread sheet');
				
				mywindow.onload = function(){
					mywindow.print();
					mywindow.close();
				};
//				
				return false;
		}
		

        

//			window.print();
			
		},
		loadLastViewedQuery : function()
		{
			var userId = $('#loggedInUserId').text();
			var obj =  Util.getCookie("last-visit-query"+userId);
			if(obj == null ||obj == undefined){
				BQV.ready();	
				return;
			}
			BQV.populateViewTabs();
			$('#firstmsg').remove();
			var idInfObj = JSON.parse(Util.getCookie("last-visit-idInfoMap"+userId));
			DAT.idInfoMap = idInfObj;
			var cacheObj = JSON.parse(obj);
			DAT.filePathMap =cacheObj;
			var qId ='';
			var eId ='';
			for(var executionId in cacheObj){
				eId = executionId;
				qId = idInfObj[executionId];
				DAT.loadLastViewed(qId, executionId, cacheObj[executionId]);
			}
			if(qId != ""){
				if(Navbar.selectedQueryId == undefined)
					Navbar.selectedQueryId = qId;
				DAT.loadReportInTab(Navbar.selectedQueryId, eId, DAT.filePathMap[eId]);
				
			}
			RemoteManager.getAllBigQueriesInfo($('#queryIONameNodeId').val(), BQV.fillAllQueryIds);
			RemoteManager.getNotificationSettings(BQV.setNotification);
		}
		
		

};