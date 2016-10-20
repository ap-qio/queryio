NN_Summary = {
		HAEnable : false,
		callBackFunc :null,
		currentPage: 1,
		portNumberValues : new Array(),
		nameNode_present: false,
		timer: [],
		timerFlag : false,
		
		
		fillStatusTable : function(list)
		{
			if(document.getElementById('nn_status_table_div')=='undefined'||document.getElementById('nn_status_table_div')==null)return;
	    	var div_data='<div class=" table_header_div" id="status_summary_title" style="width: 98.8%;">Status summary</div>';
	    	
	    	if (list == null || list == 'undefined')
			{
	    		$("#nn_status_table_div").html('<span>NameNode Details not available. </span>');
				return;
			}
	    	
	    	var tmp;
    		for(var i=0;i<list.length;i++)
    		{
    			tmp=list[i];
    			if(i%2==0)
    			{
    				div_data+='<div class="row even">';
    			}
    			else
    			{
    				div_data+='<div class="row odd">';
    			}
    			div_data+=tmp.name+": ";
    			div_data+=tmp.value;
    			div_data+='</div>';
    		}
    		$("#nn_status_table_div").html(div_data);
		},
		
		fillSummaryChart : function(list)
		{
			if(document.getElementById('nn_summary_chart')=='undefined'||document.getElementById('nn_summary_chart')==null)return;
			if (list == null || list == 'undefined')
			{
				$('#nn_summary_chart').append('<div style="margin: 38px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
				return;
			}
			
			var seriesArray = [];
			var labels = [];
			var colors = [];
			
			if ((list[0] == 0) && (list[1] == 0))
			{
				seriesArray.push(['No Data', 100]);
				labels.push('No Data');
				colors.push('#6C6C6C');
				
				$('#nn_summary_chart').append('<div style="margin: 38px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
			}
			else
			{
				seriesArray.push(['File Reads', list[0]]);
				labels.push('File Reads '+ list[0]);
				colors.push('#EAA228');
				seriesArray.push(['File Writes', list[1]]);
				labels.push('File Writes '+list[1]);
				colors.push('#579575');
				$.jqplot("nn_summary_chart", [seriesArray], {
					title: {show: false},
					grid:{shadow: false, borderWidth:0.0, background: '#fff'},
					seriesColors: colors,
					seriesDefaults:{renderer:$.jqplot.PieRenderer, rendererOptions:{sliceMargin:2, startAngle: 45, diameter:185, showDataLabels: true}},
					legend:{show:true, location: 'e', labels: labels}
				});
				$('#legend-table'+table_count).css('bottom','0px');
				$('#legend-table'+table_count).css('width','50%');
				$('#legend-table'+table_count).css('margin-right','50px');
			}
		},
		
		populateNodeDetailSummaryTable : function(summaryTable)
		{
//			NN_Summary.fillSeconadryNameNode(summaryTable);
			NN_Summary.timerFlag = false;
			if(document.getElementById('nn_summary_table_div')=='undefined'||document.getElementById('nn_summary_table_div')==null)return;
			var flag=true;
			var colList=[];
			var tableRow=[];
			var rowList='';
			if (summaryTable == null || summaryTable == 'undefined')
			{
				$("#nn_summary_table_div").html('<span>NameNode Details not available. </span>');
				return;
			}

			if(flag)
			{
				flag = false;
				colList.push({ "sTitle":'<input type="checkbox" value="node-1" id="selectAll" onclick="javascript:NN_Summary.selectAllHostRow(this)" >' });
				for(var i=0; i< summaryTable.colNames.length-1; i++)
				{
					if(summaryTable.colNames[i]=='Host')continue;
					colList.push({ "sTitle": summaryTable.colNames[i]});	
				}
				
			}
			rowList = summaryTable.rows;
			
			var row;
			for(var i=0; i<rowList.length; i++)
			{
				row = rowList[i];
				var rowData = new Array();
				var nnid = row[row.length-1];
				nameNodeIdArray.push(nnid);
				rowData.push('<input type="checkbox" value="node-1" onClick="javascript:NN_Summary.clickCheckBox(this.id)" id="node'+nnid+'" >');
				if(row[row.length-3]=='Started'){
						rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\',\''+row[1]+'\');" ><img id ="statusStart" alt="" src="images/status_start.png" class="statusImage" >&nbsp;'+row[0]+'</a><br><a href="javascript:Navbar.changeTab(\'' + row[1] + '\',\'nn_host\');">' + row[1] + '</a>');	
					}
					else if(row[row.length-3]=='Stopped'){
						rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\',\''+row[1]+'\');" ><img id ="statusStop" alt="" src="images/status_stop.png" class="statusImage" >&nbsp;'+row[0]+'<br></a><a href="javascript:Navbar.changeTab(\'' + row[1] + '\',\'nn_host\');">' + row[1] + '</a>');
					}
					else{
						rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\',\''+row[1]+'\');" ><img id ="noStatus" alt="" src="images/no_status.png" class="statusImage" >&nbsp;'+row[0]+'<br></a><a href="javascript:Navbar.changeTab(\'' + row[1] + '\',\'nn_host\');">' + row[1] + '</a>');
					}
			
				for(var j=2;j<row.length-1;j++){
					
					if(j==row.length-3){
					rowData.push('<div id="node.status'+nnid+'">'+row[j]+'</div>');
					}else if(j==row.length-2){
						rowData.push('<div id="monitorStatus'+nnid+'">'+row[j]+'</div>');
					} else if(j == 2) {
						rowData.push('<div id="nodeType'+nnid+'">'+row[j]+'</div>');
					}
					else{
						rowData.push(row[j]);
					}
						
				}
				tableRow.push(rowData);
				
				if(row[15] == "Launching") {
					NN_Summary.timerFlag = true;
				}
			}
			
			$('#nn_summary_table').dataTable( {
		        "bPaginate": false,
				"bLengthChange": false,
				"bFilter": false,
				"bDestroy": true,
				"bSort": false,
				"bInfo": false,
				"bAutoWidth": false,
				"aaData": tableRow,
		        "aoColumns": colList
		    });
			//disabled the checkAll button when no data in the list
		   	if(rowList == null || rowList == undefined || rowList.length == 0)
				document.getElementById('selectAll').disabled = true;
			else
				$('#selectAll').removeAttr('disabled');
		   	
		   	NN_Summary.updateNNTable();
			
		},

		fillLogTable : function(rowList)
		{
			if(rowList==null)
				return;
			
			if(document.getElementById('nn_log_table')=='undefined'||document.getElementById('nn_log_table')==null)return;
			var colList = new Array();
			colList.push({ "sTitle": "NameNode"});
			colList.push({ "sTitle": "Start time"});
			colList.push({ "sTitle": "Activity"});
			colList.push({ "sTitle": "Status"});
			colList.push({ "sTitle": "Log"});
			
			var row;
			var tableRow = new Array();
			for(var i=0; i<rowList.length; i++)
			{
				row = rowList[i];
				var rowData = new Array();
				
				for(var j=1;j<row.length-1;j++){
					rowData.push(row[j]);
				}
				if(row[j]==null){
					rowData.push('N/A');
				}
				else{
					rowData.push('<a href="javascript:NN_Summary.showNodeLog(\''+row[j]+'\')">View Log</a>');
				}
				tableRow.push(rowData);	
			}
			$('#nn_log_table').dataTable( {
		        "bPaginate": false,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": false,
				"bDestroy": true,
				"bInfo": false,
				"bAutoWidth": false,
				"aaData": tableRow,
		        "aoColumns": colList
		    }); 
		
		
		},
		fillSeconadryNameNode : function(summaryTable){
			return;
			
			if(document.getElementById('nn_summary_table_div')=='undefined'||document.getElementById('nn_summary_table_div')==null)return;
			var flag=true;
			var colList=[];
			var tableRow=[];
			var rowList='';
			if (summaryTable == null || summaryTable == 'undefined')
			{
				$("#nn_summary_table_div").html('<span>NameNode Details not available. </span>');
				return;
			}

			if(flag)
			{
				flag = false;
				colList.push({ "sTitle":'<input type="checkbox" value="node-1" id="selectAll" onclick="javascript:NN_Summary.selectAllHostRow(this)" >' });
				for(var i=0; i< summaryTable.colNames.length-1; i++)
				{
					colList.push({ "sTitle": summaryTable.colNames[i]});	
				}
			}
			rowList = summaryTable.rows;
			var row;
			for(var i=0; i<rowList.length; i++)
			{
				row = rowList[i];
				var rowData = new Array();
				rowData.push('<input type="checkbox" value="node-1" onClick="javascript:NN_Summary.clickCheckBox(this.id)" id="node'+row[row.length-1]+'" >');
			
				if(row[row.length-2]=='Started'){
						rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\');" ><img id ="statusStart" alt="" src="images/status_start.png" class="statusImage" >&nbsp;</a>'+row[1]+'');	
					}
					else if(row[row.length-2]=='Stopped'){
						rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\');" ><img id ="statusStop" alt="" src="images/status_stop.png" class="statusImage" >&nbsp;</a>'+row[1]+'');
					}
					else{
						rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\');" ><img id ="noStatus" alt="" src="images/no_status.png" class="statusImage" >&nbsp;</a>'+row[1]+'');
					}
					
				
				for(var j=1;j<row.length-1;j++){
					
					if(j==row.length-2)
					rowData.push('<div id="node.status'+row[row.length-1]+'">'+row[j]+'</div>');
					else{
						rowData.push(row[j]);
					}
						
				}
				tableRow.push(rowData);	
			}
			
			$('#nn_sec_table').dataTable( {
		        "bPaginate": false,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": false,
				"bInfo": false,
				"bDestroy": true,
				"bAutoWidth": false,
				"aaData": tableRow,
		        "aoColumns": colList
		    }); 
			
		},
		
		ready: function () {
			NN_Summary.checkEnableDisable();
			
			
			RemoteManager.getAllNameNodesSummaryTable(false,NN_Summary.populateNodeDetailSummaryTable);
//			
//			
			RemoteManager.getAllNameNodeStatusSummary(NN_Summary.fillStatusTable);
			RemoteManager.getAllNameNodeReadWrites(NN_Summary.fillSummaryChart);
			RemoteManager.getHadoopServiceLogs(NN_Summary.fillLogTable);
//			RemoteManager.getAllNameNodesSummaryTable(false,NN_Summary.fillSeconadryNameNode);
//			RemoteManager.isHAEnable(NN_Summary.setHAState);
		},
		
	
	checkEnableDisable: function(){
		if(selectedHdfsNode.length>0){
			dwr.util.byId('delete.service').disabled=false;
			if(selectedHdfsNode.length==1){
				document.getElementById('config.service').disabled = false;
			}
			else{
				document.getElementById('config.service').disabled = true;
			}
			
			if(dwr.util.byId('start.service').disabled){
				dwr.util.byId('filecheck.service').disabled=false;
				dwr.util.byId('balancer.service').disabled=false;
			}
		}
		else{
			
		dwr.util.byId('start.monitoringservice').disabled=true;
		dwr.util.byId('stop.monitoringservice').disabled=true;
		dwr.util.byId('start.service').disabled=true;
		dwr.util.byId('stop.service').disabled=true;
		dwr.util.byId('delete.service').disabled=true;
		dwr.util.byId('filecheck.service').disabled=true;
		dwr.util.byId('balancer.service').disabled=true;
		document.getElementById('config.service').disabled = true;
		}
	},
	
	clickCheckBox : function(chkbxid)
	{
		if(document.getElementById('start.service')=='undefined'||document.getElementById('start.service')==null)return;
		var serviceId=chkbxid.substring(4,chkbxid.length);
		dwr.util.byId('start.service').disabled=false
		dwr.util.byId('stop.service').disabled=false;
		dwr.util.byId('start.monitoringservice').disabled=false;
		dwr.util.byId('stop.monitoringservice').disabled=false;
		if(dwr.util.byId(chkbxid).checked)
		{
			if(selectedHdfsNode.indexOf(serviceId)==-1)
			selectedHdfsNode.push(serviceId);
			
			if(dwr.util.getValue('node.status'+serviceId)=='Started')
			{
				dwr.util.byId('start.service').disabled=true;
				if(dwr.util.getValue('monitorStatus'+serviceId)=='Started')
					dwr.util.byId('start.monitoringservice').disabled=true;
				else
					dwr.util.byId('stop.monitoringservice').disabled=true;
			}
			else if(dwr.util.getValue('node.status'+serviceId)=='Stopped')
			{
				dwr.util.byId('stop.service').disabled=true;
				dwr.util.byId('start.monitoringservice').disabled=true;
				dwr.util.byId('stop.monitoringservice').disabled=true;
			}else{
				dwr.util.byId('start.monitoringservice').disabled=false;
				dwr.util.byId('start.service').disabled=false;
				dwr.util.byId('stop.service').disabled=false;
				dwr.util.byId('stop.monitoringservice').disabled=false;
			}
			RemoteManager.isHANode(serviceId,NN_Summary.setHAState);
			
		}
		else
		{
			var index = selectedHdfsNode.indexOf(serviceId);
			selectedHdfsNode.splice(index, 1);
			for(var i=0;i<selectedHdfsNode.length;i++)
			{
				dwr.util.byId('start.monitoringservice').disabled=false;
				dwr.util.byId('start.service').disabled=false;
				dwr.util.byId('stop.service').disabled=false;
				dwr.util.byId('stop.monitoringservice').disabled=false;
				
				if(dwr.util.getValue('node.status'+selectedHdfsNode[i])=='Started')
				{
					dwr.util.byId('start.service').disabled=true;
					if(dwr.util.getValue('monitorStatus'+serviceId)=='Started')
						dwr.util.byId('start.monitoringservice').disabled=true;
					else
						dwr.util.byId('stop.monitoringservice').disabled=true;
				}
				else if(dwr.util.getValue('node.status'+selectedHdfsNode[i])=='Stopped')
				{
					dwr.util.byId('stop.service').disabled=true;
					dwr.util.byId('start.monitoringservice').disabled=true;
					dwr.util.byId('stop.monitoringservice').disabled=true;
				}
				
			}
		}
		
		NN_Summary.enableDisableSelectAllButton();
		NN_Summary.checkEnableDisable();
	},
	
	selectAllHostRow :function (element)
	{
		var val = element.checked;
		for (var i=0;i<document.forms[0].elements.length;i++)
	 	{
	 		var e=document.forms[0].elements[i];
	 		if ((e.id != 'selectAll') && (e.type=='checkbox'))
	 		{
	 				e.checked=val;
	 				NN_Summary.clickCheckBox(e.id);
	 		}
	 	}
	},
	
	enableDisableSelectAllButton :function ()
	{
		var i = 0;
		for (i=0; i < document.forms[0].elements.length; i++)
	 	{
	 		var e=document.forms[0].elements[i];
	 		if ((e.id != 'selectAll') && (e.type=='checkbox'))
	 		{
	 			if(!e.checked)
	 				break;
	 		}
	 	}
		if(i > 0 && document.forms[0].elements.length == i)
			$("#selectAll").attr('checked', 'checked');
		else
			$("#selectAll").removeAttr('checked');
	},
	
	addNameNodeDB : function(){
		
		Util.removeLightbox("addnn");
		Util.addLightbox("addDB","resources/add_new_db_connection.html",null, null);
		
	},
	closeDBBox: function(){
		Util.removeLightbox("addDB");	
		NN_Summary.addHdfsNode();
	},
	InsertDB : function(){
		
		if($('#connectionName').val()==""){
			jAlert("Connection name is empty.Please provide a unique connection name","Error");
			$("#popup_container").css("z-index","999999999");
			return;
		}
		else if($("#type").val() == "0")
		{
			jAlert("You must specify the Database type.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			return;
		}
		DB_Config.isSubmitFromNN=true;
		DB_Config.selectedDBId =$('#connectionName').val();
		DB_Config.newConnectionType = $("#type").val();
		DB_Config.status = "Save";
		DB_Config.InsertDBDetails();
	},
	
	addHdfsNode :function()
	{
//		Navbar.isRefreshPage=true;
		RemoteManager.getAllHostDetails(NN_Summary.evaluateHostList);	
	},
	
	evaluateHostList : function(response)
	{
		NN_Summary.currentPage = 1;
		if(response.length == 0)
		{
			Util.addLightbox("addnn", "resources/new_host_box.html", null, null);
		}
		else
		{
			Util.addLightbox("addnn", "resources/add_nn.html", null, null);
		}
	},
	closeHostBox : function(){
		Util.removeLightbox("addnn");
		RemoteManager.getAllHostDetails(NN_Summary.checkHost_forCloseHostBox);
	},
	
	checkHost_forCloseHostBox: function(response)
	{
		if (response.length != 0)
		 {
			NN_Summary.addHdfsNode();
		 }
	},	
	
	closeBox : function(isRefresh)
	{
		Util.removeLightbox("addnn");
		if (isRefresh)
		{
			Navbar.refreshNavBar();
			Navbar.refreshView();
		}
	},
	
	addNewHost : function()
	{
		Util.removeLightbox("addnn");
		Util.addLightbox("addnn", "resources/new_host_box.html", null, null);
	},
	
	showConfiguration : function()
	{
		config_nodeId = selectedHdfsNode[0];
		isCallFromNameNode = true;
		Util.importResource("service_ref","resources/nn_config.html");
	},
	
	showNodeLog : function(path)
	{
		LogView.showLog(path);
	},
	addSecondaryNameNode :function()
	{
		Util.addLightbox("addsecnn", "resources/add_sec_name_node.html", null, null);
	},
	populateStandByNameNodeDetailSummaryTable : function(summaryTable)
	{
		if(document.getElementById('nn_summary_table_div')=='undefined'||document.getElementById('nn_summary_table_div')==null)return;
		var flag=true;
		var colList=[];
		var tableRow=[];
		var rowList='';
		if (summaryTable == null || summaryTable == 'undefined')
		{
			$("#nn_standby_summary_table_div").html('<span>NameNode Details not available. </span>');
			return;
		}

		if(flag)
		{
			flag = false;
			colList.push({ "sTitle":'<input type="checkbox" value="node-1" id="selectAll" onclick="javascript:NN_Summary.selectAllStandByNameNode(this)" >' });
			for(var i=0; i< summaryTable.colNames.length-1; i++)
			{
				colList.push({ "sTitle": summaryTable.colNames[i]});	
			}
		}
		rowList = summaryTable.rows;
		var row;
		for(var i=0; i<rowList.length; i++)
		{
			row = rowList[i];
			var rowData = new Array();
			rowData.push('<input type="checkbox" value="node-1" onClick="javascript:NN_Summary.clickStandByCheckBox(this.id)" id="node'+row[row.length-1]+'" >');
			
			if(row[row.length-2]=='Started'){
					rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\');" ><img id ="statusStart" alt="" src="images/status_start.png" class="statusImage" >&nbsp;'+row[0]+'</a>');	
				}
				else if(row[row.length-2]=='Stopped'){
					rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\');" ><img id ="statusStop" alt="" src="images/status_stop.png" class="statusImage" >&nbsp;'+row[0]+'</a>');
				}
				else{
					rowData.push('<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'nn_detail\');" ><img id ="noStatus" alt="" src="images/no_status.png" class="statusImage" >&nbsp;'+row[0]+'</a>');
				}
		
			
			for(var j=1;j<row.length-1;j++){
				
				if(j==row.length-2)
				rowData.push('<div id="node.status'+row[row.length-1]+'">'+row[j]+'</div>');
				else{
					rowData.push(row[j]);
				}
					
			}
			
			tableRow.push(rowData);	
		}
		
		$('#nn_standby_summary_table').dataTable( {
	        "bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": false,
			"bInfo": false,
			"bDestroy": true,
			"bAutoWidth": false,
			"aaData": tableRow,
	        "aoColumns": colList
	    }); 
	},
	
	initiateFailOver : function(){
		
		Util.addLightbox("addnn", "resources/enableHA.html", null, null);
//		setTimeout(RemoteManager.initiateFailOver(NN_Summary.handleHAEnableResp),10000);
		
	},
	FailOverInitiatedStatus : function(resp){
		jAlert(resp.substring(resp.indexOf('_')+1));
		Navbar.refreshView();		
	},
	enableHA : function(flag,HAValue){
		var nodeId = selectedHdfsNode[0];
		if(flag){
			
			
			if(HAValue=='HAEnable'){
				this.HAEnable = true;
				this.callBackFunc = 'RemoteManager.setHaEnabled(true,NN_Summary.handleHAEnableResp)';
			}else if(HAValue=='HADisable'){
				this.HAEnable =false;
				this.callBackFunc = 'RemoteManager.setHaEnabled(false,NN_Summary.handleHAEnableResp)';
			}else if(HAValue=='IntiateFaiilOver'){
				this.callBackFunc = 'RemoteManager.initiateFailOver';
			}else{
				return;
			}
			Util.addLightbox("addnn", "resources/enableHA.html", null, null);
			
		}
		else
		{
			if(this.callBackFunc=='RemoteManager.initiateFailOver'){
				
				RemoteManager.initiateFailOver(nodeId,NN_Summary.handleHAEnableResp);	
			}else{
				RemoteManager.setHaEnabled(this.HAEnable,NN_Summary.handleHAEnableResp);
			}
			
		}
		
	},

	handleHAEnableResp : function(dwrResponse){
		var status = '';
		if(dwrResponse.taskSuccess){
			img_src='images/Success_img.png'
			status = 'Success'; 
			dwr.util.byId('popupimagesuccess').style.display = '';
		}
		else{
			img_src='images/Fail_img.png'
			status = 'Fail';
			dwr.util.byId('popupimagefail' ).style.display = '';
			var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
			document.getElementById('log_div').innerHTML=log;
			document.getElementById('log_div').style.display="block";
		}
		dwr.util.byId('popupimageprocessing').style.display = 'none';
		dwr.util.setValue('popupmessage',dwrResponse.responseMessage);
		dwr.util.setValue('popupstatus',status);
		
		document.getElementById('ok.popup').disabled = false;
	
		Navbar.refreshView();
		
	},

	setHAState : function(state){
		if(state){
			$('#initiateFailover').removeAttr('disabled');
			$('#enableHA').css('display','none');
			$('#disableHA').css('display','');
		}else{
			$('#initiateFailover').attr('disabled',true);
			$('#enableHA').css('display','');
			$('#disableHA').css('display','none');
		}
		
	},
	generateUniqueId : function(){
		var totalnn = $('#totalnn').text();
		$('#id').val('NameNode'+(totalnn));
	},
	getPhysicalDiskName : function(element){
		var id = element.id;
		var hostName =$('#'+id+' option:selected').text();  
		if(hostName=='Select Host'){
			return;
		}
		RemoteManager.getPhysicalDiskNames(hostName, NN_Summary.fillvolumeInfoTable);
		RemoteManager.getVolumeDiskMap(hostName,NN_Summary.fillDiskMapCache);
		RemoteManager.getUserHomeDirectoryPathForHost(hostName,NN_Summary.fillUserHome);
	},
	fillvolumeInfoTable : function(list)
	{
		if(list==null){
			jAlert("No disk info found at host.","No Disk found");
			$("#popup_container").css("z-index","9999999");
//			return;	
		}
		//sorting the list
		list.sort();
		//for(var t=0;t<list.length;t++)          //just for debugging
		
		var data = '<option value="select disk">Select Disk</option>';
		for(var i=0;i<list.length;i++){
			data+= '<option value="'+list[i]+'">'+list[i]+'</option>';
		}
		$('#disk').html(data);
	},
	 fillDiskMapCache : function(map) {
		diskMap = map;
		if(isUserWaiting){
			jAlert("Disk volume mapping received from server. Please add datanode now.")
			$("#popup_container").css("z-index","999999999");
		}
		isUserWaiting = false;
	},
	

	nextPage: function(pageNo , flag){
		var valid = true;
		switch(pageNo){
			case 1:
				NN_Summary.currentPage = pageNo;
				$('#nndetail').html('NameNode keeps the directory tree of all files in the file system in form of metadata, and tracks where across the cluster, the file data is kept.');
				$('#page1').css('display','');
				$('#page2').css('display','none');
				$('#nnType').css('display','');
				break;
			case 2:
				if(NN_Summary.currentPage<pageNo){
					valid = NN_Summary.checkValidity(1);
				}
				if(nameNodeIdArray.indexOf($('#id').val())!=-1){
					jAlert("Current NameNode Id is already taken by another NameNode. Please enter a new NameNode Id.","Invalid Id");
					$('#id').focus();
					$("#popup_container").css("z-index","9999999");
					return;
				}
				if(Util.isContainSpecialChar($('#id').val())){
					jAlert("NameNode Id contains special character.Please remove special character from Id.","Invalid Id");
					$("#popup_container").css("z-index","9999999");
					return;
				}
				if (dwr.util.byId('NameNodeDB').value == 0 && flag == 0)
				{
					if(valid)
					{
						jQuery.alerts.okButton = ' Yes ';
						jQuery.alerts.cancelButton  = ' No ';
						if (dwr.util.byId('useDifferentDb').checked)
						{
							if (dwr.util.byId('analyticsDB').value == 0 )
							{
								jConfirm('Databases not selected for namenode. You will not be able to perform searching of files on HDFS cluster using metadata or user defined tags and Big Data Analytics for this NameNode. ' +
										'Are you sure you want to continue?','Incomplete Detail',function(val)
								{
									if (val)
									{
										NN_Summary.currentPage = pageNo;
										$('#nndetail').html('Please configure the required ports for starting NameNode service on the host selected.');
										$('#nnType').css('display','none');
										$('#page2').css('display','');
										$('#page1').css('display','none');
										$('#page3').css('display','none');
									}
									else
										return;
								});
								$("#popup_container").css("z-index","9999999");
							}
							else
							{
								jQuery.alerts.okButton = ' Yes ';
								jQuery.alerts.cancelButton  = ' No ';
								jConfirm('Metadata Database not selected for namenode. You will not be able to perform searching of files on HDFS cluster using metadata or user defined tags for this NameNode. ' +
										'Are you sure you want to continue?','Incomplete Detail',function(val)
								{
									if (val)
									{
										NN_Summary.currentPage = pageNo;
										$('#nndetail').html('Please configure the required ports for starting NameNode service on the host selected.');
										$('#nnType').css('display','none');
										$('#page2').css('display','');
										$('#page1').css('display','none');
										$('#page3').css('display','none');
									}
									else
										return;
								});
								$("#popup_container").css("z-index","9999999");
							}
						}
						else
						{
							jQuery.alerts.okButton = ' Yes ';
							jQuery.alerts.cancelButton  = ' No ';
							jConfirm('Metadata Database not selected for namenode. You will not be able to perform searching of files on HDFS cluster using metadata or user defined tags for this NameNode. ' +
									'Are you sure you want to continue?','Incomplete Detail',function(val)
							{
								if (val)
								{
									NN_Summary.currentPage = pageNo;
									$('#nndetail').html('Please configure the required ports for starting NameNode service on the host selected.');
									$('#nnType').css('display','none');
									$('#page2').css('display','');
									$('#page1').css('display','none');
									$('#page3').css('display','none');
								}
								else
									return;
							});
							$("#popup_container").css("z-index","9999999");
						}
					}
				}
				else if (dwr.util.byId('analyticsDB').value == 0 && flag == 0)
				{
					if(valid)
					{
						jQuery.alerts.okButton = ' Yes ';
						jQuery.alerts.cancelButton  = ' No ';
						jConfirm('All the data for HDFS metadata, Extended Metadata, user defined tags and Big Data Analytics on this NameNode will be stored in Metadata database. ' +
								'Are you sure you want to continue?','Incomplete Detail',function(val)
						{
							if (val)
							{
								NN_Summary.currentPage = pageNo;
								$('#nndetail').html('Please configure the required ports for starting NameNode service on the host selected.');
								$('#nnType').css('display','none');
								$('#page2').css('display','');
								$('#page1').css('display','none');
								$('#page3').css('display','none');
							}
							else
								return;
						});
						$("#popup_container").css("z-index","9999999");
					}
				}
				else if(valid)
				{
					NN_Summary.currentPage = pageNo;
					$('#nndetail').html('Please configure the required ports for starting NameNode service on the host selected.');
					$('#nnType').css('display','none');
					$('#page2').css('display','');
					$('#page1').css('display','none');
					$('#page3').css('display','none');
				}
				break;
			case 3:
				valid = NN_Summary.checkValidity(2);
				if(valid){
					NN_Summary.currentPage = pageNo;
					$('#nndetail').html('Please select the disk and specify the directory path where installation may take place.');
					$('#page2').css('display','none');
					$("#startNamenodeAfterInstall").show();
					$("#startnodechkbox").attr('checked', 'checked');
					$('#page3').css('display','');
				}
				break;
		}
	},

	fillUserHome : function(val){
		$('#dirPath').val(val+'/QueryIONodes/NameNode');
	},
	
	isPortNumberNumericFields : function()
	{
		
		NN_Summary.portNumberValues.splice(0 , NN_Summary.portNumberValues.length);
		NN_Summary.portNumberValues.push(dwr.util.byId('serverPort').value);
		NN_Summary.portNumberValues.push(dwr.util.byId('httpPort').value);
		NN_Summary.portNumberValues.push(dwr.util.byId('httpsPort').value);
		NN_Summary.portNumberValues.push(dwr.util.byId('jmxPort').value);
		NN_Summary.portNumberValues.push(dwr.util.byId('os3ServerPort').value);
		NN_Summary.portNumberValues.push(dwr.util.byId('secureOs3ServerPort').value);
		NN_Summary.portNumberValues.push(dwr.util.byId('hdfsOverFtpServerPort').value);
		NN_Summary.portNumberValues.push(dwr.util.byId('ftpServerPort').value);
		NN_Summary.portNumberValues.push(dwr.util.byId('secureFtpServerPort').value);
		var isNumeric = Util.isNumericPortNumbers(NN_Summary.portNumberValues);
		if(isNumeric)
		{
			NN_Summary.nextPage(3);
		}
		else
		{
			jAlert("Only integers are allowed in port number fields.","Incomplete Detail");
			$("#popup_container").css("z-index","9999999");
		}
		
	},
	
	checkValidity : function(pageNo)
	{
		var valid = true;
		jQuery.alerts.okButton = ' OK ';
		if(pageNo == 1)
		{
			if (dwr.util.byId('hostForNode').value == 0)
			{
				valid = false;
				jAlert("No Host Selected","Incomplete Detail");
				$("#popup_container").css("z-index","9999999");
			}
			else if (dwr.util.byId('id').value == '')
			{
				valid = false;
				jAlert("Node Unique Identifier was not specified. Please provide a valid Unique Identifier to configure DataNode.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
		
			}
		}
		else{
			if (dwr.util.byId('serverPort').value == '')
			{
				valid = false;
				jAlert("Server port not set.","Incomplete Detail");
				$("#popup_container").css("z-index","9999999");
			}	
			else if (dwr.util.byId('httpPort').value == '')
			{
				valid = false;
				jAlert("Http port not set.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
			}
			else if (dwr.util.byId('httpsPort').value == '')
			{
				valid = false;
				jAlert("Https port not set.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
			}
			else if (dwr.util.byId('jmxPort').value == '')
			{
				valid = false;
				jAlert("JMX port not set.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
			}
			else if (dwr.util.byId('os3ServerPort').value == '')
			{
				valid = false;
				jAlert("OS3 Server port not set.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
			}
			else if (dwr.util.byId('hdfsOverFtpServerPort').value == '')
			{
				valid = false;
				jAlert("HDFS Over FTP Server port not set.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
			}
			else if (dwr.util.byId('ftpServerPort').value == '')
			{
				valid = false;
				jAlert("FTP server port not set.","Incomplete detail");
				$("#popup_container").css("z-index","9999999");
			}
		}
		return valid;
	},
	
	toggleDifferentDb : function(val)
	{
		dwr.util.byId('analyticsDB').disabled = !(val);
	},
	
	updateNNTable : function()
	{
		if(NN_Summary.timerFlag) {
			var timerProcess = setTimeout(function() { RemoteManager.getAllNameNodesSummaryTable(false,NN_Summary.populateNodeDetailSummaryTable);  } ,1000);
			NN_Summary.timer.push(timerProcess);
		} else 	{
			for(var i=0;i<NN_Summary.timer.length;i++){
				clearTimeout(NN_Summary.timer[i]);
			}
		}
	},
	
};