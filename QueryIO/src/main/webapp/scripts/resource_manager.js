Resource_Manager = {
	nodeSelect: 0,
	selectedNode: [],
	rmCache: null,
	id: null,
	hostname: null,
	serverPort: null, 
	schedulerPort: null,
	httpPort: null,
	adminPort: null,
	jmxPort: null,
	rTrackerPort: null,
	jobHistoryServer: null,
	jobWebAppPort: null,
	dirPath: null,
	startAfterSave: null,
	childID: null,
	currentOperation:null,
	selectedIP: null,
	portNumberValues : [],
	currentPage: 1,
	timer : [],
	timerFlag : false,
	
	ready: function () {
		Resource_Manager.disableButton();
		RemoteManager.getResourceManagerAppsDetail(Resource_Manager.fillTableAndChart);
		RemoteManager.getAllResourceManagersSummaryTable(false, Resource_Manager.populateTable);

   	},
	
   	stopResourceManager: function(){
   		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm("This will stop selected ResourceManager. Are you sure?","Stop ResourceManager",function(confirm){
			if(confirm){
				Resource_Manager.currentOperation = 'stopNode';
		   		Util.addLightbox("addRM","pages/popup.jsp");
				}
			else{
				return;
			}
			jQuery.alerts.okButton = 'Ok';
			jQuery.alerts.cancelButton  = 'Cancel';
		});
		$("#popup_container").css("z-index","99999999");
   	},
   	
   	
   	startResourceManager: function(){
   		Resource_Manager.currentOperation = 'startNode';
   		Util.addLightbox("addRM","pages/popup.jsp");
   	},
   	
   	
   	deleteResourceManager: function(){
   		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm("This will delete selected ResourceManager. Are you sure?","Delete ResourceManager",function(confirm){
			if(confirm){
				Resource_Manager.currentOperation = 'deleteNode';
		   		Util.addLightbox("addRM","pages/popup.jsp");
				}
			else{
				return;
			}
			jQuery.alerts.okButton = 'Ok';
			jQuery.alerts.cancelButton  = 'Cancel';
		});
		$("#popup_container").css("z-index","99999999");
   		
   	
   	},
   	
   	delete_RM: function(resp){
   		var id = 'delete';
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
   	
   	
   	stop_RM: function(resp){
   		var id = 'stop';
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
 	handleStopMonitoringResponse: function(resp){

 		var id = 'stopMonitoring';
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
	handleStartMonitoringResponse: function(resp){

   		var id = 'startMonitoring';
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
   	
   	start_RM: function(resp){
   		var id = 'start';
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
   	
	disableButton: function(){
		document.forms[0].Edit.disabled=true;
		document.forms[0].Delete.disabled=true;
		document.forms[0].Start.disabled=true;
		document.forms[0].Stop.disabled=true;
		document.forms[0].StopMonitoring.disabled=true;
		document.forms[0].StartMonitoring.disabled=true;
	},
	
	
	enableButton: function(id){
		var detail = rmCache[parseInt(id.substring(4))];
		if(detail[detail.length-2]=='Stopped'){
			document.forms[0].Start.disabled=false;
			document.forms[0].Edit.disabled=false;
			document.forms[0].Delete.disabled=false;
			document.forms[0].StartMonitoring.disabled=true;
			document.forms[0].StopMonitoring.disabled=true;
		}
		else{
			document.forms[0].Stop.disabled=false;
			document.forms[0].Edit.disabled=false;
			if(detail[detail.length-1]=='Started'){
				document.forms[0].StartMonitoring.disabled=true;
				document.forms[0].StopMonitoring.disabled=false;
			}else{
				document.forms[0].StartMonitoring.disabled=false;
				document.forms[0].StopMonitoring.disabled=true;
			}
		}
		
	},

	checkForButton: function(id){
		
		if(document.getElementById(id).checked){
			Resource_Manager.selectedNode.push(id);
			
			Resource_Manager.nodeSelect++;
			if(Resource_Manager.nodeSelect != 1){
				Resource_Manager.disableButton();
			}
			else{
				Resource_Manager.enableButton(id);
			}
			if(($('#ResourceManager_table tr').length - 1) == Resource_Manager.nodeSelect)
				document.getElementById('selectAll').checked = true;
		}
		else{
			
			var index = Resource_Manager.selectedNode.indexOf(id);
			Resource_Manager.selectedNode.splice(index,1)
			Resource_Manager.nodeSelect--;
			if(Resource_Manager.nodeSelect != 1){
				Resource_Manager.disableButton();
			}
			else{
				Resource_Manager.enableButton(id);
			}
			document.getElementById('selectAll').checked = false;
		}
		var value = $('#'+id).val();
		console.log(value);
		if(value ==  "Not Responding"){
			document.forms[0].Stop.disabled=false;
			document.forms[0].Start.disabled=false;
			document.forms[0].StopMonitoring.disabled=false;
			document.forms[0].StartMonitoring.disabled=false;
			
			
		}
	},
	
	
	selectAllNodeSummary: function(){
		var flag = document.getElementById('selectAll').checked;
		for(var i=0;i<rmCache.length;i++){
			document.getElementById('node'+i).checked = flag;
			Resource_Manager.selectedNode.splice(1,Resource_Manager.selectedNode.length);
		}
		
		Resource_Manager.disableButton();
		if(flag){
			Resource_Manager.nodeSelect = rmCache.length;
		}
		else{
			Resource_Manager.nodeSelect = 0;
		}
		
	},
	selectAllResourceSummary : function(element){
		var val = element.checked;
		if(!val){
			Resource_Manager.nodeSelect = rmCache.length;
		}
		else{
			Resource_Manager.nodeSelect = 0;
		}
		for (var i=0;i<document.forms[0].elements.length;i++)
	 	{
	 		var e=document.forms[0].elements[i];
	 		if ((e.id != 'selectAll') && (e.type=='checkbox'))
	 		{
	 				e.checked=val;
	 				Resource_Manager.checkForButton(e.id)
	 		}
	 	}
	},
   	populateTable : function (sTable)
	{
   		Resource_Manager.timerFlag = false;
   		if (sTable == null || sTable == 'undefined')
		{
			$("#schedules_table_wrapper").html('<span>ResourceManager Details not available. </span>');
			return;
		}
   		rmCache =new Array();
   		var columns = sTable.colNames;
   		var rows = sTable.rows;
		var dataColumn = [];
		dataColumn.push({"sTitle":'<input type="checkBox" id="selectAll" onClick="javascript:Resource_Manager.selectAllResourceSummary(this);">'});
		for(var i=0;i<columns.length;i++)
		{			
				if(i==0){
					dataColumn.push({"sTitle":columns[i],"sClass": "dataTableNodeId"});
					continue;
				}
				dataColumn.push({"sTitle":columns[i]});
		}
		
		var dataRow = [];
		for(var i=0;i<rows.length;i++){
			var rowDetail = rows[i];
			resourceManagerIdArray.push(rowDetail[0]);
			rmCache[i]=rows[i];
			var rowData = [];
			rowData.push(['<input onClick="javascript:Resource_Manager.checkForButton(this.id);" type="checkbox" id="node'+i+'" value="'+rowDetail[rowDetail.length-2]+'">']); 
			for(var j=0;j<rowDetail.length;j++){
				if(j==0){
					var imgStatus;
					if(rowDetail[rowDetail.length-2]=='Stopped'){
						imgStatus = '<img id="noStatus" alt="" src="images/status_stop.png" class="statusImage">';
					}
					else if(rowDetail[rowDetail.length-2]=='Started'){
						imgStatus = '<img id="noStatus" alt="" src="images/status_start.png" class="statusImage">';
					}
					else{
						imgStatus = '<img id="noStatus" alt="" src="images/no_status.png" class="statusImage">';
					}
					rowData.push(['<a href="javascript:Navbar.changeTab(\'Hadoop\',\'rm_detail\',\''+rowDetail[1]+'\',\''+rowDetail[j]+'\');">'+imgStatus+rowDetail[j]+'</a>']);
				}
				else if(j==1){
					rowData.push(['<a href="javascript:Navbar.changeTab(\'Hadoop\',\'rm_detail\',\''+rowDetail[j]+'\');">'+rowDetail[j]+'</a>']);
				}
				else{
					rowData.push(rowDetail[j]);
				}
				
			}
			dataRow.push(rowData);
			
			if(rowDetail[16] == "Launching") {
				Resource_Manager.timerFlag = true;
			}
		}
   		
   	$('#ResourceManager_table').dataTable( {
			        "bPaginate": false,
//			        "sScrollX": "1200px",
//					"bScrollCollapse": true,
					"bLengthChange": false,
					"bDestroy": true,
					"bFilter": false,
					"bSort": true,
					"bInfo": false,
					"bAutoWidth": false,
//					"aaSorting": [[ 1, "desc" ]],
					"aoColumnDefs": [{ 'bSortable': false, 'aTargets': [ 0 ] }],
					"aaData": dataRow,
			        "aoColumns": dataColumn
			    } );
   	
   	
   		//disabled the checkAll button when no data in the list
   		if(rows == null || rows == undefined || rows.length == 0)
   			document.getElementById('selectAll').disabled = true;
   		else
   			document.getElementById('selectAll').disabled = false;
   		
   		Resource_Manager.updateRMTable();

	},
	
	
	addResourceManager: function()
	{
			Resource_Manager.currentPage = 1;
			Util.addLightbox("addRM", "resources/new_ResourceManager.html", null, null);
	},
	
	editResourceManager: function()
	{
		//Util.addLightbox("addRM", "resources/new_ResourceManager.html", null, null);
		Util.importResource("service_ref","resources/rm_config.html");
	},
	
	addBoxReady: function(){
		var currentRM = "";
		if($('#ResourceManager_table tbody tr').length > 0 && $('#ResourceManager_table tbody tr td').hasClass('dataTables_empty') )
			currentRM = 1;
		else
			currentRM = $('#ResourceManager_table tbody tr').length + 1;
		$('#uniqueID').val("ResourceManager" + currentRM);
		Resource_Manager.fillHostNames();
		
	},
	
	saveRM_DWRCall: function(){
		Resource_Manager.selectedNode.splice(1,Resource_Manager.selectedNode.length);
		RemoteManager.addResourceManager(Resource_Manager.id, Resource_Manager.hostname, Resource_Manager.serverPort, 
				Resource_Manager.schedulerPort, Resource_Manager.httpPort, Resource_Manager.adminPort, Resource_Manager.jmxPort, Resource_Manager.rTrackerPort, 
				Resource_Manager.jobHistoryServer, Resource_Manager.jobWebAppPort, Resource_Manager.dirPath, Resource_Manager.add_RM_Response);
	},
	
	saveRMClicked: function(){
		var resourceM = document.getElementById('add_RM');
		if(resourceM.dirPath.value == '' || resourceM.dirPath.value == null){
			jAlert("Directory Path not set","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
		}
//		else if(Util.isContainWhiteSpace(resourceM.dirPath.value)){
//			jAlert("Directory Path path contains space. Please remove space from directory path.","Incomplete Detail");
//			$("#popup_container").css("z-index","9999999");
//			return;	
//		}
		else{
			Resource_Manager.startAfterSave = resourceM.startAfterSave.value;
			Resource_Manager.id = resourceM.uniqueID.value;
			Resource_Manager.hostname = resourceM.hostForNode.value;
			Resource_Manager.serverPort = resourceM.serverPort.value, 
			Resource_Manager.schedulerPort = resourceM.SchedulerPort.value,
			Resource_Manager.httpPort = resourceM.httpPort.value;
			Resource_Manager.adminPort = resourceM.adminPort.value;
			Resource_Manager.jmxPort = resourceM.jmxPort.value;
			Resource_Manager.rTrackerPort = resourceM.ResourceTrackerPort.value;
			Resource_Manager.jobHistoryServer = resourceM.jobHistoryServer.value;
			Resource_Manager.jobWebAppPort = resourceM.jobWebAppPort.value;
			Resource_Manager.dirPath = resourceM.dirPath.value;
			Resource_Manager.currentOperation = 'saveNode';
			$("#addRM").load("pages/popup.jsp");
		}
	},
	
	add_RM_Response: function(resp)
	{
		var id = Resource_Manager.id;
		dwr.util.byId('popup.image.processing'+id).style.display = 'none';
		dwr.util.setValue('popup.message'+id,resp.responseMessage);
		dwr.util.setValue('popup.status'+id,resp.taskSuccess?'Success':'Failed');
		if (resp.taskSuccess)
		{
			dwr.util.byId('popup.image.success'+id).style.display = '';
			if (Resource_Manager.startAfterSave)
			{
				dwr.util.byId('pop.pattern'+id).style.display = 'none';
				Resource_Manager.startAfterSave = false;
				Resource_Manager.currentOperation = 'startNode';
				fillPopUp(false);
			}
			else
			{
				dwr.util.byId('ok.popup').disabled = false;
				Navbar.refreshView();
				Navbar.refreshNavBar();
			}
		}
		else
		{
			dwr.util.byId('popup.image.fail'+id ).style.display = '';
			dwr.util.byId('ok.popup').disabled = false;
			Navbar.refreshView();
			Navbar.refreshNavBar();
		}
	},
	
	
	fillHostNames: function()
	{
		RemoteManager.getAllHostDetails(Resource_Manager.populateAddHostForm);
	},
	
	doneTask: function(){
		Resource_Manager.closeBox();
		Navbar.changeTab('Hadoop','Hadoop', 'ResourceManager');
	},
	
	populateAddHostForm: function(list)
	{
		var selectList = dwr.util.byId('hostForNode');
		dwr.util.removeAllOptions(selectList);
		Resource_Manager.addOption(selectList, 0, 'Select Host');
		for (var i = 0; i < list.length; i++)
		{
			Resource_Manager.addOption(selectList, list[i].id, list[i].hostIP);
		}
	},
	
	
	addOption: function(selectbox, value, text)
	{
		var optn = document.createElement("OPTION");
		optn.text = text;
		optn.value = value;
		selectbox.options.add(optn);
	},
	
	closeBox : function()
	{
		Util.removeLightbox("addRM");
	},
	
	
	fillTableAndChart: function(list)
	{
		Resource_Manager.fillSummaryChart(list);
		Resource_Manager.fillStatusTable(list);
	},
	
	fillStatusTable: function(list)
	{
		var total = 0;
		for(var i=0;i<list.length;i++){
			total += parseInt(list[i]);
		}
		document.getElementById("RM_apps_submitted").innerHTML = total;
		document.getElementById("RM_apps_running").innerHTML = list[0];
		document.getElementById("RM_apps_pending").innerHTML = list[1];
		document.getElementById("RM_apps_completed").innerHTML = list[2];
		document.getElementById("RM_apps_killed").innerHTML = list[3];
	},
	
	
	fillSummaryChart : function(list)
	{
		var total = 0;
		for(var i=0;i<list.length;i++){
			total += parseInt(list[i]);
		}
				
		if(document.getElementById('RM_Summary_chart')==undefined||document.getElementById('RM_Summary_chart')==null)return;
				
		if (list == null || list == undefined)
		{
			$('#RM_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
			return;
		}
		
		var seriesArray = [];
		var labels = [];
		var colors = [];
		
		if (total == 0)
		{
			$('#RM_Summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
		}
		else
		{
			seriesArray.push(['Running', (list[0]/total)*100]);
			seriesArray.push(['Pending', (list[1]/total)*100]);
			seriesArray.push(['Completed', (list[2]/total)*100]);
			seriesArray.push(['Killed', (list[3]/total)*100]);
			labels.push('Running');
			colors.push('#CC9752');
			labels.push('Pending');
			colors.push('#996699');
			labels.push('Completed');
			colors.push('#FFCC00');
			labels.push('Killed');
			colors.push('#EAA228');
			
		
			$.jqplot("RM_Summary_chart", [seriesArray], {
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
	
	
	findHostDetail: function(ip, child){
		Resource_Manager.selectedIP = ip;
		if(typeof(child) != "undefined"){
			Resource_Manager.childID = child;
			RemoteManager.getResourceManagerAppsDetailForId(child,Resource_Manager.fillTableAndChart);
			var chartDiv = document.getElementById('RM_summary_table_div');
			chartDiv.innerHTML = '';
			var interval = Util.getCookie("TimeInterval");
			if(interval == null){
				interval = "onehour";
				Util.setCookie("TimeInterval",interval,1);
			}
			Resource_Manager.loadResource(interval,true);
		}
		else{
			RemoteManager.getResourceManagerAppsDetailForIp(ip,Resource_Manager.fillTableAndChart);
			RemoteManager.getAllResourceManagersSummaryTableForHost(ip,Resource_Manager.populateHostDetailTable);
		}
	},

	
	
	
	populateHostDetailTable : function (sTable)
	{
		var columns = sTable.colNames;
		var rows = sTable.rows;
		var dataColumn = [];
		for(var i=0;i<columns.length;i++)
		{
			dataColumn.push({"sTitle":columns[i]});
		}

		var dataRow = [];
		for(var i=0;i<rows.length;i++)
		{
			var rowDetail = rows[i];
			var rowData = []; 
			for(var j=0;j<rowDetail.length;j++)
			{
				

				if(j==0){
					var imgStatus;
					if(rowDetail[rowDetail.length-2]=='Stopped'){
						imgStatus = '<img id="noStatus" alt="" src="images/status_stop.png" class="statusImage">';
					}
					else if(rowDetail[rowDetail.length-2]=='Started'){
						imgStatus = '<img id="noStatus" alt="" src="images/status_start.png" class="statusImage">';
					}
					else{
						imgStatus = '<img id="noStatus" alt="" src="images/no_status.png" class="statusImage">';
					}
					rowData.push(['<a href="javascript:Navbar.changeTab(\'Hadoop\',\'rm_detail\',\''+rowDetail[1]+'\',\''+rowDetail[j]+'\');">'+imgStatus+rowDetail[j]+'</a>']);
				}
				else{
					rowData.push(rowDetail[j]);
				}
			}
			dataRow.push(rowData);
		}
		

		
		$('#ResourceManager_summary_table').dataTable( {
					"bPaginate": false,
		//	        "sScrollX": "1200px",
		//			"bScrollCollapse": true,
					"bLengthChange": false,
					"bDestroy": true,
					"bFilter": false,
					"bSort": true,
					"bInfo": false,
					"bAutoWidth": false,
		//			"aaSorting": [[ 1, "desc" ]],
					"aoColumnDefs": [{ 'bSortable': false, 'aTargets': [ 0 ] }],
					"aaData": dataRow,
			        "aoColumns": dataColumn
			    } );
		
		
	},
	loadResource: function(selectedInterval,firstTime)
	{
		$('#chartInterval').val(selectedInterval);
		if(resource=="chart.jsp"||firstTime){
			$('#RM_summary_table_div').load('pages/status/chart.jsp?nodeID='+Resource_Manager.childID+'&nodeType=RM&interval='+selectedInterval);
		}
		else{
			$("#service_ref").load("pages/status/chartMagnified.jsp?chartData="+cName+"&nodeId="+Resource_Manager.childID+"&nodeType=RM&interval="+selectedInterval);
		}
	},
	
	
	changeInterval: function (newInterval)
	{
		
		Resource_Manager.loadResource(newInterval,false);
	},
	
	
	pageChange: function(pageNo){
		var valid = true;
		switch(pageNo){
			case 1:
				$('#msg_td').html('A ResourceManager (RM) manages the global assignment of compute resources to applications. The ResourceManager is the ultimate authority that arbitrates resources among all the applications in the system.');
				Resource_Manager.currentPage = pageNo;
				$('#page1').css('display','');
				$('#page2').css('display','none');
				break;
			case 2:
				if(Resource_Manager.currentPage<pageNo){
					valid = Resource_Manager.checkValidity(1);
				}
				if(valid){
					Resource_Manager.currentPage = pageNo;
					$('#msg_td').html('Please configure the required ports for starting ResourceManager service on the host selected.');
					$('#page2').css('display','');
					$('#page1').css('display','none');
					$('#page3').css('display','none');
				}
				break;
			case 3:
				valid = Resource_Manager.checkValidity(2);
				if(valid){
					Resource_Manager.currentPage = pageNo;
					$('#msg_td').html('Please specify the directory path where installation may take place.');
					$('#page2').css('display','none');
					$('#page3').css('display','');
				}
				break;
		}
	},
	
	checkValidity : function(pageNo)
	{
		var valid = true;
		var resourceM = document.getElementById('add_RM');
		if(pageNo == 1)
		{
			if(resourceM.uniqueID.value == ''||resourceM.uniqueID.value == null){
				valid = false;
				jAlert("ID field can't be empty","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
			else if(resourceManagerIdArray.indexOf(resourceM.uniqueID.value) != -1){
				valid = false;
				jAlert("Current ResourceManager Id is already taken by another ResourceManager. Please enter a new ResourceManager Id.","Invalid Id");
				$("#popup_container").css("z-index","9999999");
			}
			else if(Util.isContainSpecialChar(resourceM.uniqueID.value)){
				valid = false;
				jAlert("ResourceManager Id contains special character.Please remove special character from Id.","Invalid Id");
				$("#popup_container").css("z-index","9999999");
			}
			else if(resourceM.hostForNode.value == 0 || resourceM.hostForNode.value == null){
				valid = false;
				jAlert("Host Not Selected","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
		}
		else{
			if(resourceM.serverPort.value == '' || resourceM.serverPort.value == null){
				valid = false;
				jAlert("Server port not set","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
			else if(resourceM.SchedulerPort.value == '' || resourceM.SchedulerPort.value == null){
				valid = false;
				jAlert("Scheduler port not set","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
			else if(resourceM.httpPort.value == '' || resourceM.httpPort.value == null){
				valid = false;
				jAlert("WebApp port not set","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
			else if(resourceM.adminPort.value == '' || resourceM.adminPort.value == null){
				valid = false;
				jAlert("Admin port not set","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
			else if(resourceM.jmxPort.value == '' || resourceM.jmxPort.value == null){
				valid = false;
				jAlert("JMX port not set","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
			else if(resourceM.ResourceTrackerPort.value == '' || resourceM.ResourceTrackerPort.value == null){
				valid = false;
				jAlert("Resource Tracker port not set","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
			else if(resourceM.jobHistoryServer.value == '' || resourceM.jobHistoryServer.value == null){
				valid = false;
				jAlert("Job History Server port not set","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
			else if(resourceM.jobWebAppPort.value == '' || resourceM.jobWebAppPort.value == null){
				valid = false;
				jAlert("job History WebApp port not set","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
		}
		return valid;
	},
	
	drawResourceTable : function()
	{
		var keyList = new Array();
		keyList.push("yarn.resourcemanager.address");
		keyList.push("yarn.resourcemanager.scheduler.address");
		keyList.push("yarn.resourcemanager.webapp.address");
		keyList.push("yarn.resourcemanager.admin.address");
		keyList.push("yarn.resourcemanager.resource-tracker.address");
		
		keyList.push("mapreduce.jobhistory.address");
		keyList.push("mapreduce.jobhistory.webapp.address");
		
		keyList.push("queryio.resourcemanager.options");
		
		
		
		
		RemoteManager.getConfigurationServerPort(keyList,Resource_Manager.drawResourceManagerConfigTable);
		
		
	},
	drawResourceManagerConfigTable : function(map){
		
		
		var serverPort = map["yarn.resourcemanager.address"]["value"];
		serverPort = serverPort.substring(serverPort.indexOf(':')+1).trim();
		
		var scheduler = map["yarn.resourcemanager.scheduler.address"]["value"];
		scheduler = scheduler.substring(scheduler.indexOf(':')+1).trim();
		
		var webapp = map["yarn.resourcemanager.webapp.address"]["value"];
		webapp = webapp.substring(webapp.indexOf(':')+1).trim();
		
		var adminPort = map["yarn.resourcemanager.admin.address"]["value"];
		adminPort = adminPort.substring(adminPort.indexOf(':')+1).trim();
		
		var tracker = map["yarn.resourcemanager.resource-tracker.address"]["value"];
		tracker = tracker.substring(tracker.indexOf(':')+1).trim();
		
		
		var hostoryPort = map["mapreduce.jobhistory.address"]["value"];
		hostoryPort = hostoryPort.substring(hostoryPort.indexOf(':')+1).trim();
		
		var webhostoryPort = map["mapreduce.jobhistory.webapp.address"]["value"];
		webhostoryPort = webhostoryPort.substring(webhostoryPort.indexOf(':')+1).trim();
		
		
		var jmx = map["queryio.resourcemanager.options"]["value"];
		jmx = jmx.substring(jmx.indexOf('jmxremote.port=')+1);
		jmx = jmx.substring(jmx.indexOf('=')+1,jmx.indexOf('-Dcom.sun.management')).trim();
		
		$('#resourceConfTable').dataTable({
			"bPaginate": false,
			"bLengthChange": true,
			"bFilter": false,
			"bSort": false,
			"bInfo": false,
			"bDestroy": true,
			"bAutoWidth": false,
	        "aaData": [
			[ "Server", '<input type="text" id="serverPort" value="'+serverPort+'">', 'The port for applications manager interface in the RM.'],
			[ "Scheduler", '<input type="text" id="SchedulerPort" value="'+scheduler+'">', 'The port for scheduler interface.'],
			[ "WebApp", '<input type="text" id="httpPort" value="'+webapp+'">', 'The port for RM web application.'],
			[ "Admin", '<input type="text" id="adminPort" value="'+adminPort+'">', 'The port for RM admin interface.'],
			[ "ResourceTracker", '<input type="text" id="ResourceTrackerPort" value="'+tracker+'">', 'The port for RM Resource Tracker.'],
			[ "JMX", '<input type="text" id="jmxPort" value="'+jmx+'">', 'JMX monitoring port']
			],
			"aoColumns": [
			{ "sTitle": "Port Title", "sWidth" : "20%" },
			{ "sTitle": "Port Number", "sWidth" : "30%" },
			{ "sTitle": "Description", "sWidth" : "50%" }
			]
	    });
		$('#resourceJobTable').dataTable({
			"bPaginate": false,
			"bLengthChange": true,
			"bDestroy": true,
			"bFilter": false,
			"bSort": false,
			"bInfo": false,
			"bAutoWidth": false,
	        "aaData": [
			[ "Server", '<input type="text" id="jobHistoryServer" value="'+hostoryPort+'">', 'MapReduce JobHistory Server IPC port'],
			[ "WebApp", '<input type="text" id="jobWebAppPort" value="'+webhostoryPort+'">', 'MapReduce JobHistory Server Web UI port']
			],
			"aoColumns": [
			{ "sTitle": "Port Title", "sWidth" : "20%"  },
			{ "sTitle": "Port Number", "sWidth" : "30%"  },
			{ "sTitle": "Description", "sWidth" : "50%"  }
			]
	    });
	
	},
	fillUserDir : function(){
		var hostName =$('#hostForNode option:selected').text();
		RemoteManager.getUserHomeDirectoryPathForHost(hostName,Resource_Manager.fillUserHome);
	},
	fillUserHome : function(val){
		$('#dirPath').val(val+'/QueryIONodes/ResourceManager');
	},
	startMonitoring : function(){
		Resource_Manager.currentOperation = 'startMonitoring';
   		Util.addLightbox("addRM","pages/popup.jsp");
	},
	stopMonitoring : function(){
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm("Are you sure you want to stop monitoring of this node?",'Monitoring is running',function(val)
		{
		
			if (val){
				Resource_Manager.currentOperation = 'stopMonitoring';
		   		Util.addLightbox("addRM","pages/popup.jsp");
			}
			else
			{
				return;
			}
			
			
		});
		jQuery.alerts.okButton = ' Ok';
		jQuery.alerts.cancelButton  = ' Cancel';
		$("#popup_container").css("z-index","99999999");
		
		
	},
	isPortNumberNumericFields : function()
	{
		
		Resource_Manager.portNumberValues.splice(0 , Resource_Manager.portNumberValues.length);
		Resource_Manager.portNumberValues.push(dwr.util.byId('serverPort').value);
		Resource_Manager.portNumberValues.push(dwr.util.byId('SchedulerPort').value);
		Resource_Manager.portNumberValues.push(dwr.util.byId('httpPort').value);
		Resource_Manager.portNumberValues.push(dwr.util.byId('adminPort').value);
		Resource_Manager.portNumberValues.push(dwr.util.byId('ResourceTrackerPort').value);
		Resource_Manager.portNumberValues.push(dwr.util.byId('jmxPort').value);
		Resource_Manager.portNumberValues.push(dwr.util.byId('jobHistoryServer').value);
		Resource_Manager.portNumberValues.push(dwr.util.byId('jobWebAppPort').value);
		var isNumeric = Util.isNumericPortNumbers(Resource_Manager.portNumberValues);
		if(isNumeric)
		{
			Resource_Manager.pageChange(3);
		}
		else
		{
			jAlert("Only integers are allowed in port number fields.","Incomplete Detail");
			$("#popup_container").css("z-index","9999999");
		}
		
	},
	
	updateRMTable : function()
	{
		if(Resource_Manager.timerFlag) {
			var timerProcess = setTimeout(function() { RemoteManager.getAllResourceManagersSummaryTable(false, Resource_Manager.populateTable);  } ,1000);
			Resource_Manager.timer.push(timerProcess);
		} else 	{
			for(var i=0;i<Resource_Manager.timer.length;i++){
				clearTimeout(Resource_Manager.timer[i]);
			}
		}
	},
	
	
};