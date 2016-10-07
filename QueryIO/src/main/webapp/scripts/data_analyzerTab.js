DAT = {
	tempQuery:'',
	filePathMap:{},	
	$tabs : $("#queryTabs").tabs(),
	tabIndex : 0,
	idInfoMap : new Object,
	executionIdMap : new Array(),
	isAddNewTab : false,
	currentQueryId : '',
	isAdhoc : false,
	currentQueryInfo : {},
	currentExecutionId : '',
	oldExecutionId : '',
	
	replaceAll : function (txt, replace, with_this)
	{
		return txt.replace(new RegExp(replace, 'g'),with_this);
	},
	
	
	ready : function ()
	{
		$('#refreshViewButton').css('display','none');
		DAT.$tabs = $("#queryTabs").tabs();
		var selectedNameNode = $('#queryIONameNodeId').val();
		if(selectedNameNode==""||selectedNameNode==null){
			$('#designTab').html('There is no NameSpace defined currently.Please setup a cluster for using Analytics features.')
			$('#previewTab').html('There is no NameSpace defined currently.Please setup a cluster for using Analytics features.')
			$('#designTab').css('text-align','center');
			$('#previewTab').css('text-align','center');
			//jAlert("There is no NameNode present in cluster.Please install a NameNode before using BigQuery features.","Error");
			$('#saveQuery').attr("disabled","disabled");
			$('#executeQuery').attr("disabled","disabled");
			return;
		}
		RemoteManager.getAllBigQueriesInfo(selectedNameNode, DAT.fillAllQueryIds);
		DAT.filePathMap = new Object();
		
	},
	tabClicked : function(str){
	},
	
	populateDesignTab : function(){
		
		Navbar.queryManagerDirtyBit = false;
		
		DAT.$tabs.tabs('paging', { cycle: false, follow: true, selectOnAdd: true } );
		DAT.$tabs.tabs({
		    tabTemplate: "<li onclick=\"javascript:DAT.tabClicked('#{href}');\"><a style = 'font-size: 13px;' href='#{href}'>#{label}</a> <span id='#{label}' class='ui-icon ui-icon-close' style='cursor: pointer;'></span></li>",
		    add: function( event, ui ) {
		    },
	 		remove: function(event, ui) {
	 		}
		});
		
		var height = ($(window).height() - 150);
		if (height < 510)
		{
			$("#service_ref").height('510px');
		}
		else
		{
			$("#service_ref").height(height);
		}
		
		$("#designTab").load("resources/dataanalyzer.html");
		
		$('#queryTabs').on('click', 'span.ui-icon-close', function() {
			var index = $( "li", DAT.$tabs ).index( $( this ).parent() );
		    var removedId = $( this )[0].id;
		    if (removedId != undefined)
		    {
		    	var queryId = removedId.substr(removedId.indexOf('-') + 1);
		    	if(queryId!=null&&queryId!=undefined){
			    	for (var i in DAT.idInfoMap)
					{
			    		if (i == queryId)
			    		{ 
			    			DAT.$tabs.tabs( "remove",index);
			    			$('#tab_'+queryId).remove();
			    			$('#queryTabs ul > li').eq(index).remove();
			    			DAT.$tabs.tabs("select", 0);
			    			delete DAT.idInfoMap[i];
			    		}
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
		
		if(data==""){
			Navbar.isAddNewQuery = true;
		}
		$('#bigQueryIds').html(data);
		DAT.populateDesignTab();
		
	},
	loadLastViewed : function(queryId,executionId,filePath)
	{
		if(queryId == null || queryId == undefined )
			return;
		var id = "#tab_" + executionId;
		var index = $( 'li', DAT.$tabs ).length+1
		DAT.$tabs.tabs("add", id, "" + queryId);
		var content = '<div id="resultPart_' + executionId + '" style="">'
		+ '	<div id="executionStepsDiv' + executionId + '">'
		+ '	</div>'
		+ '</div>';
		$(id).html(content);
		
		$(id).css('overflow','auto');
		var reportId = 'reportPart_' + executionId;
		var queryId =$('#bigQueryIds').val();
		var content = '<div id="resultPart_' + executionId + '" >'
			+ '</div>'
			+ '<div id="' + reportId + '" style="max-height:600px;">'
			+ '</div>';
		
		var id = "#tab_" + executionId;
		
		DAT.$tabs.tabs("select", id);
		
		
		$(id).append(content);
				
		    document.getElementById('queryExecute').disabled = false;
			document.getElementById('emailReport').disabled = false;
			document.getElementById('exportReport').disabled = false;
			document.getElementById('printReport').disabled = false;
			$("#__bookmark_1").attr("border","1");
		
	},
	loadReportInTab : function(queryId,executionId,filePath){
		var userName = Util.getLoggedInUserName();
		var reportId = 'reportPart_' + executionId;
		if($('#' + reportId).html() != null && $('#' + reportId).html() != undefined && $('#' + reportId).html() !="")
			return;
		var nameNodeId = $('#queryIONameNodeId').val();
		nameNodeId = nameNodeId.replace(/ /g,"_").toLowerCase();
		var framepath = 'Reports/'+ userName+'/'+ nameNodeId + "/" + filePath;
		
		$('#' + reportId).html('<iframe src="'+framepath+'" style="overflow:auto;height:585px;width:99.5%" height="100%" width="100%" ></iframe>');

		
	},
	
	addUpdateTab : function (queryId, executionId)
	{
		DAT.tabIndex++;
		var id = "#tab_" + executionId;
		
		
		
		
		if (DAT.isAddNewTab)
		{
			var index = $( 'li', DAT.$tabs ).length+1
			DAT.$tabs.tabs("add", id, "" + queryId);
		}
		else
		{
			DAT.$tabs.tabs("select", id);
		}
		if(Navbar.isViewerView){
			$('#designTab').hide();
			$('#design_li').hide();
		}else{
			$('#designTab').show();
			$('#design_li').show();
		}
		
		var content = '<div id="resultPart_' + executionId + '" style="">'
		+ '	<div id="executionStepsDiv' + executionId + '">'
		+ '	</div>'
		+ '</div>';
		
		
		$(id).html(content);
		$('#executionStepsDiv' + executionId).load("resources/executionStep.html", function(resp, stat, req) {
			
		    if(req.status <= 0 || req.status >= 400)
		    	return;
		    
		    
		    dwr.util.cloneNode('allStepsDiv',{idSuffix: executionId});
		    dwr.util.byId('allStepsDiv'+executionId).style.display = '';
		    $("#allStepsDiv").remove();
		    
		    DAT.showSteps(executionId);
		    DAT.oldExecutionId = executionId; //Saved executionId, in case Query Execution/Launching MapReduce job fails.
		    $("#image_processing1").show();
		    
			document.getElementById('queryExecute').disabled = true;
			document.getElementById('emailReport').disabled = true;
			document.getElementById('exportReport').disabled = true;
			document.getElementById('printReport').disabled = true;

		});
		
		
		
	},
	executeQuery : function(executionId){

		document.getElementById('queryExecute').disabled = true;
		document.getElementById('emailReport').disabled = true;
		document.getElementById('exportReport').disabled = true;
		document.getElementById('printReport').disabled = true;
		
		var queryId = $('#bigQueryIds').val();
		for(var attr in DAT.idInfoMap){
			if(DAT.idInfoMap[attr]==queryId){
				var id ="#tab_"+attr
				DAT.currentExecutionId=attr;
//				DAT.$tabs.tabs("select", id);
				DAT.isAddNewTab = false;
				executionId = attr;
				delete DAT.executionIdMap[executionId];
			}
		}
		var namenode = $('#queryIONameNodeId').val();
		Util.removeLightbox('viewerLightBox');
		RemoteManager.isQueryAdhoc(namenode, queryId, executionId, DAT.isAdhocResponse);
		
	},
	
	isAdhocResponse : function(response)
	{
		DAT.isAdhoc = response.adhoc;
		var executionId = response.executionId;
		var namenode = response.namenode;
		var queryId = response.queryId;
		var userName = Util.getLoggedInUserName();
		
		DAT.idInfoMap[executionId] = queryId;
		DAT.addUpdateTab(queryId, executionId);
		RemoteManager.executeQuery(namenode, queryId,userName, false, DAT.queryExecuted);
	},
	
	executeCommand : function (queryId)
	{
		document.getElementById('queryExecute').disabled = true;
		document.getElementById('emailReport').disabled = true;
		document.getElementById('exportReport').disabled = true;
		document.getElementById('printReport').disabled = true;

		DAT.currentQueryId = queryId;
		DAT.isAddNewTab = true;
		RemoteManager.getQueryExecutionId(DAT.executeQuery);
		
	},
	
	queryExecuted : function (response)
	{
		var executionId = response.executionId;
		if(!DAT.isAddNewTab){
			executionId = DAT.currentExecutionId;
			response.executionId = DAT.currentExecutionId;
		}
		if (response.executionId != null)
		{
			$("#startImg1"+executionId).show();
			$("#stopImg1"+executionId).hide();
			$("#image_processing1"+executionId).hide();
			$("#image_success1"+executionId).show();
			
			if (DAT.isAdhoc)
				$("#step2"+executionId).show();
			else
				$("#step3"+executionId).show();
			
			isPresent = false;
			for (var i in DAT.executionIdMap)
			{
	    		if (i == executionId)
	    		{
	    			isPresent = true;
	    			break;
	    		}
			}
			if (!isPresent)
			{
				var timerId = setTimeout(function() { DAT.isReportFinished(executionId) }, 1000);
				DAT.executionIdMap[executionId] = timerId;
				// add timer
			}

//			DAT.idInfoMap[DAT.currentQueryId] = response.executionId;
//			DAT.addUpdateTab(null, response.executionId);
		}
		else
		{
			if(response.error != null)
				jAlert(response.error, 'Error');
			else
				jAlert("Query may have been deleted or not responding.", 'Error');
			$("#image_processing1" + DAT.oldExecutionId).hide();
			$("#image_fail1" + DAT.oldExecutionId).show();
		}
	},
	
	showSteps : function (executionId)
	{
		if (DAT.isAdhoc)
		{
			// show job execution steps.
		}
		else
		{
			// show normal execution steps.
			$("#step2"+executionId).css("display", "none");
			$("#stepMessage1"+executionId).text("Query Execution is in progress.");
			
		}
	},
	
	isReportFinished : function (executionId)
	{
		RemoteManager.isQueryComplete(executionId, DAT.reportGenerated);
	},
	
	reportGenerated : function (response)
	{
		
		var executionId = response.executionId;
		var filePath = response.filePath;
		var isError = response.error;
		var appStatus = response.appStatus;
		
		if (isError)
		{
			if (DAT.isAdhoc)
			{
				if(appStatus)
				{
					$("#image_processing2"+executionId).hide();
					$("#image_success2"+executionId).show();
					$("#startImg2"+executionId).show();
					$("#stopImg2"+executionId).hide();
					$("#step3"+executionId).show();
				}
				else
				{
					$("#image_processing2"+executionId).hide();
					$("#image_fail2"+executionId).show();
				}
			}
			// remove timer
			timerId = null;
			for (var i in DAT.executionIdMap)
			{
	    		if (i == executionId)
	    		{
	    			timerId = DAT.executionIdMap[executionId];
	    			clearTimeout(timerId);
	    		}
			}
			
			var content = '<div id="resultPart_' + executionId + '" style="">'
			+ '	<table style="width: 100%;">'
			+ '		<tr>'
			+ '			<td style="color: red;">'
			+ ' Error occurred: ' + filePath
			+ '			</td>'
			+ '		</tr>'
			+ '	</table>'
			+ '</div>';
			
			var id = "#tab_" + executionId;
			
			DAT.$tabs.tabs("select", id);
			
			$("#image_processing3"+executionId).hide();
			$("#image_fail3"+executionId).show();
			
			$(id).append(content);
//			jAlert(filePath, 'Error');
		}
		else if (filePath != null)
		{
			DAT.filePathMap[executionId]=filePath;
			
			if (DAT.isAdhoc)
			{
				if(appStatus)
				{
					$("#image_processing2"+executionId).hide();
					$("#image_success2"+executionId).show();
					$("#startImg2"+executionId).show();
					$("#stopImg2"+executionId).hide();
					$("#step3"+executionId).show();
				}
				else
				{
					$("#image_processing2"+executionId).hide();
					$("#image_fail2"+executionId).show();
					document.getElementById('queryExecute').disabled = false;
					document.getElementById('emailReport').disabled = false;
					document.getElementById('exportReport').disabled = false;
					document.getElementById('printReport').disabled = false;
				}
			}
			// remove timer
			timerId = null;
			for (var i in DAT.executionIdMap)
			{
	    		if (i == executionId)
	    		{
	    			timerId = DAT.executionIdMap[executionId];
	    			clearTimeout(timerId);
	    		}
			}
			var reportId = 'reportPart_' + executionId;
			var queryId =$('#bigQueryIds').val();
			var content = '<div id="resultPart_' + executionId + '" >'
				+ '</div>'
				+ '<div id="' + reportId + '" style="max-height:600px;">'
				+ '</div>';
			
			var id = "#tab_" + executionId;
			
			DAT.$tabs.tabs("select", id);
			
			$("#image_processing3"+executionId).hide();
			$("#image_success3"+executionId).show();
			$("#startImg3"+executionId).show();
			$("#stopImg3"+executionId).hide();
			$("#step4"+executionId).show();
			$(id).append(content);
			var userName = Util.getLoggedInUserName();
			var nameNodeId = $('#queryIONameNodeId').val();
			nameNodeId = nameNodeId.replace(/ /g,"_").toLowerCase();
			var framepath = "Reports/" + userName + '/' + nameNodeId + "/" + filePath;
			$('#' + reportId).html('<iframe src="'+framepath+'" style="overflow:auto;height:595px;width:99.5%" height="100%" width="100%" ></iframe>');
			
			    $("#image_processing4"+executionId).hide();
				$("#image_success4"+executionId).show();
				$("#startImg4"+executionId).show();
				$("#stopImg4"+executionId).hide();
				$("#allStepsDiv"+executionId).hide();
				document.getElementById('queryExecute').disabled = false;
				document.getElementById('emailReport').disabled = false;
				document.getElementById('exportReport').disabled = false;
				document.getElementById('printReport').disabled = false;
				$("#__bookmark_1").attr("border","1");
				var userId = $('#loggedInUserId').text();
				Util.setCookie("last-visit-query"+userId,JSON.stringify(DAT.filePathMap), 1);
				Util.setCookie("last-visit-idInfoMap"+userId,JSON.stringify(DAT.idInfoMap), 1);
		}
		else
		{
			timerId = setTimeout(function() { DAT.isReportFinished(executionId) }, 1000);
			DAT.executionIdMap[executionId] = timerId;
		}
	},
	
	exportReport : function(queryId)
	{
		
		DA.showExportLightBox();
		DA.exportQueryId=queryId;
	},
	
	emailReport : function(queryId)
	{
		DA.showEmailLightBox();
		DA.exportQueryId=queryId;
	},
	executeSeletedQuery : function(){
		
		$('#executeQuery').attr('disabled','disabled');
		$('#saveQuery').attr('disabled','disabled');
		DA.runCommand();
		return;
	},
	showPreview : function(){
		DA.showReportPreviewNew();
	}
};