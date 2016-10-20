Node_Manager = {
	
	id: null,
	hostName: null,
	rManagerID: null,
	localizerPort: null,
	httpPort: null,
	jmxPort: null,
	dirPath: null,
	nodeSelect: 0,
	selectedNode: [],
	nmCache: null,	
	childID: null,
	currentOperation: null,	
	selectedIP: null,
	portNumberValues: [],
	currentPage: 1,
	timer : [],
	timerFlag : false,
	hostIP : '',
	
	
	ready: function () {
		Node_Manager.disableButton();
		RemoteManager.getNodeManagerAppsDetail(Node_Manager.fillTableAndChart);
		RemoteManager.getAllNodeManagersSummaryTable(false, Node_Manager.populateTable);
		
   	},

   	stopNodeManager: function(){
   		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm("This will stop selected NodeManager. Are you sure?","Stop NodeManager",function(confirm){
			if(confirm){
				Node_Manager.currentOperation = 'stopNode';
		   		Util.addLightbox("addNM","pages/popup.jsp");
				}
			else{
				return;
			}
			jQuery.alerts.okButton = 'Ok';
			jQuery.alerts.cancelButton  = 'Cancel';
		});
		$("#popup_container").css("z-index","99999999");
   		
   		
   		
   		
   	},
   	
   	
   	startNodeManager: function(){
   		Node_Manager.currentOperation = 'startNode';
   		Util.addLightbox("addNM","pages/popup.jsp");
   	},
   	
	deleteNodeManager: function(){
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm("This will delete selected NodeManager. Are you sure?","Delete NodeManager",function(confirm){
			if(confirm){
				Node_Manager.currentOperation = 'deleteNode';
		   		Util.addLightbox("addNM","pages/popup.jsp");
				}
			else{
				return;
			}
			jQuery.alerts.okButton = 'Ok';
			jQuery.alerts.cancelButton  = 'Cancel';
		});
		$("#popup_container").css("z-index","99999999");
		
		
   	},
   	
   	delete_NM: function(resp){
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
   	
   	
   	stop_NM: function(resp){
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
   	
   	start_NM: function(resp){
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
		document.forms[0].Stop.disabled=true;
		document.forms[0].Start.disabled=true;
		document.forms[0].StartMonitoring.disabled=true;
		document.forms[0].StopMonitoring.disabled=true;
	},
	
	enableButton: function(id){
		//if ()
		var detail = nmCache[parseInt(id.substring(4))];
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
			Node_Manager.selectedNode.push(id);
			
			Node_Manager.nodeSelect++;
			if(Node_Manager.nodeSelect != 1){
				Node_Manager.disableButton();
			}
			else{
				Node_Manager.enableButton(id);
			}
			if(($('#NodeManager_table tr').length - 1) == Node_Manager.nodeSelect)
				document.getElementById('selectAll').checked = true;
		}
		else{
			var index = Node_Manager.selectedNode.indexOf(id);
			Node_Manager.selectedNode.splice(index,1);
			Node_Manager.nodeSelect--;
			if(Node_Manager.nodeSelect != 1){
				Node_Manager.disableButton();
			}
			else{
				Node_Manager.enableButton(id);
			}
			document.getElementById('selectAll').checked = false;
		}
		var value = $('#'+id).val();
		if(value == "Not Responding")
		{
			document.forms[0].Start.disabled=false;
			document.forms[0].Stop.disabled=false;
			document.forms[0].StartMonitoring.disabled=false;
			document.forms[0].StopMonitoring.disabled=false;
		}
	},

	selectAllNodeSummary: function(){
		var flag = document.getElementById('selectAll').checked;
		for(var i=0;i<nmCache.length;i++){
			document.getElementById('node'+i).checked = flag;
			Node_Manager.selectedNode.splice(1,Node_Manager.selectedNode.length);
			
		}
		
		Node_Manager.disableButton();
		if(!flag){
			Node_Manager.nodeSelect = nmCache.length;
		}
		else{
			Node_Manager.nodeSelect = 0;
		}
		for(var i=0;i<nmCache.length;i++){
			Node_Manager.checkForButton('node'+i);
		}
	},
	
	
   	populateTable : function (sTable)
	{
   		$('#NodeManager_table').html('');
   		Node_Manager.timerFlag = false;
   		nmCache =new Array();
   		var columns = sTable.colNames;
   		var rows = sTable.rows;
		var dataColumn = [];
		dataColumn.push({"sTitle":'<input type="checkBox" id="selectAll" onClick="javascript:Node_Manager.selectAllNodeSummary();">'});
		for(var i=0;i<columns.length;i++)
		{
				dataColumn.push({"sTitle":columns[i]});
		}
		var dataRow = [];
		for(var i=0;i<rows.length;i++){
			var rowDetail = rows[i];
			nodeManagerIdArray.push(rowDetail[0]);
			nmCache[i]=rows[i];
			var rowData = [];
			rowData.push(['<input onClick="javascript:Node_Manager.checkForButton(this.id);" type="checkbox" id="node'+i+'" value="'+rowDetail[rowDetail.length-2]+'">']); 
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
					rowData.push(['<a href="javascript:Navbar.changeTab(\'Hadoop\',\'nm_detail\',\''+rowDetail[1]+'\',\''+rowDetail[j]+'\');">'+imgStatus+rowDetail[j]+'</a>']);
				}
				else if(j==1){
					rowData.push(['<a href="javascript:Navbar.changeTab(\'Hadoop\',\'nm_detail\',\''+rowDetail[j]+'\');">'+rowDetail[j]+'</a>']);
				}
				else{
					rowData.push(rowDetail[j]);
				}
//				rowData.push(rowDetail[j]);
			}
			dataRow.push(rowData);
			
			if(rowDetail[9] == "Launching") {
				Node_Manager.timerFlag = true;
			}
			Node_Manager.updateNMTable();
		}
	
	$('#NodeManager_table').dataTable( {
			        "bPaginate": false,
			        "bLengthChange": false,
					"bFilter": false,
					"bSort": true,
					"bDestroy": true,
					"bInfo": false,
					"bAutoWidth": false,
//					"aaSorting": [[ 1, "desc" ]],
					"aoColumnDefs": [{ 'bSortable': false, 'aTargets': [ 0 ] }],
					"aaData": dataRow,
			        "aoColumns": dataColumn
			    } );
	
   	//disabled the checkAll button when no data in the list
   	if(sTable.rows.length == 0 || sTable == null || sTable == undefined )
		$('#selectAll').attr('disabled', true);
	else
		$('#selectAll').removeAttr('disabled');
   		
	},
	
	addNodeManager: function()
	{
		Node_Manager.currentPage = 1;	 
		Util.addLightbox("addNM", "resources/new_NodeManager.html", null, null);
	},
	
	
	editNodeManager: function()
	{
		Util.importResource("service_ref","resources/nm_config.html");
	},
	
	
	closeBox : function()
	{
		Util.removeLightbox("addNM");
	},
	
	addBoxReady: function(){
		var currentNM = "";
		if($('#NodeManager_table tbody tr').length > 0 && $('#NodeManager_table tbody tr td').hasClass('dataTables_empty') )
			currentNM = 1;
		else
			currentNM = $('#NodeManager_table tbody tr').length + 1;
		$('#uniqueID').val("NodeManager" + currentNM);
		Node_Manager.fillHostNames();
		Node_Manager.fillReourceManagerIds();
	},
	
	fillReourceManagerIds: function()
	{
		RemoteManager.getAllResourceManagers(Node_Manager.populateResourceManager);
	},
	
	
	populateResourceManager: function(list)
	{
		var selectList = dwr.util.byId('resourceManagerID');
		dwr.util.removeAllOptions(selectList);
		Node_Manager.addOption(selectList, 0, 'Select Resource Manager');
		for (var i = 0; i < list.length; i++)
		{

			Node_Manager.addOption(selectList, list[i], list[i]);
		}
	},
	
	
	fillHostNames: function()
	{
		RemoteManager.getAllHostDetails(Node_Manager.populateAddHostForm);
	},

	populateAddHostForm: function(list)
	{
		var selectList = dwr.util.byId('hostForNode');
		dwr.util.removeAllOptions(selectList);
		Node_Manager.addOption(selectList, 0, 'Select Host');
		for (var i = 0; i < list.length; i++)
		{
			Node_Manager.addOption(selectList, list[i].id, list[i].hostIP);
		}
	},
	
	
	addOption: function(selectbox, value, text)
	{
		var optn = document.createElement("OPTION");
		optn.text = text;
		optn.value = value;
		selectbox.options.add(optn);
	},
	
	
	saveNM_DWRCall: function(){
		Node_Manager.selectedNode.splice(1,Node_Manager.selectedNode.length);
		var isLocal = false;
		if(Node_Manager.hostIP=="127.0.0.1")
			isLocal = true;
		RemoteManager.addNodeManager(Node_Manager.id, Node_Manager.hostName, Node_Manager.rManagerID,
				Node_Manager.localizerPort, Node_Manager.httpPort, Node_Manager.jmxPort, Node_Manager.dirPath, isLocal, Node_Manager.add_NM_Response);
	},
	
	saveNMClicked: function(){
		var resourceN = document.getElementById('add_NM');
		if(resourceN.dirPath.value == '' || resourceN.dirPath.value == null){
			jAlert("Directory Path not set","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
		}
//		else if(Util.isContainWhiteSpace(resourceN.dirPath.value)){
//			jAlert("Directory Path path contains space. Please remove space from directory path.","Incomplete Detail");
//			$("#popup_container").css("z-index","9999999");
//			return;	
//		}
		else{
			Node_Manager.startAfterSave = resourceN.startAfterSave.value;
			Node_Manager.id = resourceN.uniqueID.value;
			Node_Manager.hostName = resourceN.hostForNode.value;
			Node_Manager.rManagerID = resourceN.resourceManagerID.value;
			Node_Manager.localizerPort = resourceN.LocalizerPort.value;
			Node_Manager.httpPort = resourceN.httpPort.value;
			Node_Manager.jmxPort =  resourceN.jmxPort.value,
			Node_Manager.dirPath = resourceN.dirPath.value,
			Node_Manager.currentOperation = 'saveNode';
			$("#addNM").load("pages/popup.jsp");
		}
	},
	
	add_NM_Response: function(resp){
		var id = Node_Manager.id;
		dwr.util.byId('popup.image.processing'+id).style.display = 'none';
		dwr.util.setValue('popup.message'+id,resp.responseMessage);
		dwr.util.setValue('popup.status'+id,resp.taskSuccess?'Success':'Failed');
		if(resp.taskSuccess)
		{
			dwr.util.byId('popup.image.success'+id).style.display = '';
			if (Node_Manager.startAfterSave)
			{
				Node_Manager.startAfterSave = false;
				dwr.util.byId('pop.pattern'+id).style.display = 'none';
				Node_Manager.currentOperation = 'startNode';
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
	

	fillTableAndChart: function(list)
	{
		Node_Manager.fillSummaryChart(list);
		Node_Manager.fillStatusTable(list);
	},
	
	fillStatusTable: function(list)
	{
		document.getElementById("NM_container_launched").innerHTML = list[0];
		document.getElementById("NM_container_completed").innerHTML = list[1];
		document.getElementById("NM_container_failed").innerHTML = list[2];
		document.getElementById("NM_container_killed").innerHTML = list[3];
		document.getElementById("NM_container_running").innerHTML = list[4];
		document.getElementById("NM_container_initing").innerHTML = list[5];
	},
	
	
	fillSummaryChart : function(list)
	{
		
		
		if(document.getElementById('NM_Summary_chart')==undefined||document.getElementById('NM_Summary_chart')==null)return;
		if (list == null || list == undefined)
		{
			$('#NM_Summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
			return;
		}
		
		var seriesArray = [];
		var labels = [];
		var colors = [];
		
		if (list[0] == 0)
		{	
			$('#NM_Summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
		}
		else
		{
			//seriesArray.push(['ContainersLaunched', (list[0]/total)*100]);
			seriesArray.push(['ContainersCompleted', (list[1]/list[0])*100]);
			seriesArray.push(['ContainersFailed', (list[2]/list[0])*100]);
			seriesArray.push(['ContainersKilled', (list[3]/list[0])*100]);
			seriesArray.push(['ContainersRunning', (list[4]/list[0])*100]);
			seriesArray.push(['ContainersIniting', (list[5]/list[0])*100]);
			//labels.push('ContainersLaunched');
			//colors.push('#443266');
			labels.push('Completed');
			colors.push('#CC9752');
			labels.push('Failed');
			colors.push('#996699');
			labels.push('Killed');
			colors.push('#FFCC00');
			labels.push('Running');
			colors.push('#EAA228');
			labels.push('Initing');
			colors.push('#11A211');
			
			$.jqplot("NM_Summary_chart", [seriesArray], {
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
	
	
	findHostDetail: function(ip,child){
		Node_Manager.selectedIP = ip;
		if(typeof(child) != "undefined"){
			Node_Manager.childID = child;
			RemoteManager.getNodeManagerAppsDetailForId(child,Node_Manager.fillTableAndChart);
			var chartDiv = document.getElementById('NM_summary_table_div');
			chartDiv.innerHTML = '';
			var interval = Util.getCookie("TimeInterval");
			if(interval == null){
				interval = "onehour";
				Util.setCookie("TimeInterval",interval,1);
			}
			Node_Manager.loadResource(interval,true);
		}
		else{
			RemoteManager.getNodeManagerAppsDetailForIp(ip,Node_Manager.fillTableAndChart);
			RemoteManager.getAllNodeManagersSummaryTableForHost(ip,Node_Manager.populateHostDetailTable);
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
			if(Node_Manager.childID == rowDetail[0]||Node_Manager.childID == null)
			{
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
						rowData.push(['<a href="javascript:Navbar.changeTab(\'Hadoop\',\'nm_detail\',\''+rowDetail[1]+'\',\''+rowDetail[j]+'\');">'+imgStatus+rowDetail[j]+'</a>']);
					
					}
					else{
						rowData.push(rowDetail[j]);
					}
				}
				dataRow.push(rowData);
			}
		}
		

		$('#NodeManager_summary_table').dataTable( {
			        "bPaginate": false,
					"bLengthChange": false,
					"bFilter": false,
					"bSort": true,
					"bInfo": false,
					"bDestroy": true,
					"bAutoWidth": true,
					"aaSorting": [[ 1, "desc" ]],
					"aaData": dataRow,
			        "aoColumns": dataColumn
			    } );
		
		
	},
	
	
	loadResource: function(selectedInterval,firstTime)
	{
		$('#chartInterval').val(selectedInterval);
		if(resource=="chart.jsp"||firstTime){
			$('#NM_summary_table_div').load('pages/status/chart.jsp?nodeID='+Node_Manager.childID+'&nodeType=NM&interval='+selectedInterval);
		}
		else{
			$("#service_ref").load("pages/status/chartMagnified.jsp?chartData="+cName+"&nodeId="+Node_Manager.childID+"&nodeType=NM&interval="+selectedInterval);
		}
	},
	
	
	changeInterval: function (newInterval)
	{
		Node_Manager.loadResource(newInterval,false);
	},
	
	

	pageChange: function(pageNo){
		var valid = true;
		switch(pageNo){
			case 1:
				Node_Manager.currentPage = pageNo;
				$('#msg_td').html('NodeManager (NM) manages the user processes on configured machine.');
				$('#page1').css('display','');
				$('#page2').css('display','none');
				break;
			case 2:
				if(Node_Manager.currentPage<pageNo){
					valid = Node_Manager.checkValidity(1);
				}
				if(valid){
					Node_Manager.currentPage = pageNo;
					$('#msg_td').html('Please configure the required ports for starting NodeManager service on the host selected.');
					$('#page2').css('display','');
					$('#page1').css('display','none');
					$('#page3').css('display','none');
				}
				break;
			case 3:
				valid = Node_Manager.checkValidity(2);
				if(valid){
					Node_Manager.currentPage = pageNo;
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
		var resourceN = document.getElementById('add_NM');
		if(pageNo == 1)
		{
			if(resourceN.uniqueID.value == '' || resourceN.uniqueID.value == null){
				valid = false;
				jAlert("ID field can't be empty","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}else if(nodeManagerIdArray.indexOf(resourceN.uniqueID.value) != -1){
				valid = false;
				jAlert("Current NodeManager Id is already taken by another NodeManager. Please enter a new NodeManager Id.","Invalid Id");
				$('#id').focus();
				$("#popup_container").css("z-index","9999999");
			}else if(Util.isContainSpecialChar(resourceN.uniqueID.value)){
				valid = false;
				jAlert("NodeManager Id contains special character.Please remove special character from Id.","Invalid Id");
				$("#popup_container").css("z-index","9999999");
			}
			else if(resourceN.hostForNode.value == 0 || resourceN.hostForNode.value == null){
				valid = false;
				jAlert("Host Not Selected","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
			else if(resourceN.resourceManagerID.value == 0 || resourceN.resourceManagerID.value == null){
				valid = false;
				jAlert("Resource Manager Not selected","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}	
		}
		else{
			if(resourceN.LocalizerPort.value == '' || resourceN.LocalizerPort.value == null){
				valid = false;
				jAlert("Localizer port not set.","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
			else if(resourceN.httpPort.value == '' || resourceN.httpPort.value == null){
				valid = false;
				jAlert("WebApp port not set.","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
			else if(resourceN.jmxPort.value == '' || resourceN.jmxPort.value == null){
				valid = false;
				jAlert("JMX port not set.","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
			}
		}
		return valid;
	},
	
	drawNodeManagerTable : function()
	{
		var keyList = new Array();
		keyList.push("yarn.nodemanager.localizer.address");
		keyList.push("yarn.nodemanager.webapp.address");
		keyList.push("queryio.nodemanager.options");
		
		RemoteManager.getConfigurationServerPort(keyList,Node_Manager.drawNodeManagerConfigTable);
		
	},
	drawNodeManagerConfigTable : function(map){
		
		
		var localizer = map["yarn.nodemanager.localizer.address"]["value"];
		localizer = localizer.substring(localizer.indexOf(':')+1).trim();
		
		var webapp = map["yarn.nodemanager.webapp.address"]["value"];
		webapp = webapp.substring(webapp.indexOf(':')+1).trim();
		
		var jmx = map["queryio.nodemanager.options"]["value"];
		jmx = jmx.substring(jmx.indexOf('jmxremote.port=')+1);
		jmx = jmx.substring(jmx.indexOf('=')+1,jmx.indexOf('-Dcom.sun.management')).trim();
		
		
		$('#nodeManagerTable').dataTable({
			"bPaginate": false,
			"bLengthChange": true,
			"bFilter": false,
			"bSort": false,
			"bDestroy": true,
			"bInfo": false,
			"bAutoWidth": false,
	        "aaData": [
			[ "Localizer", '<input type="text" id="LocalizerPort" value="'+localizer+'">', 'Port for localizer IPC.'],
			[ "WebApp", '<input type="text" id="httpPort" value="'+webapp+'">', 'NM Webapp port.'],
			[ "JMX", '<input type="text" id="jmxPort" value="'+jmx+'">', 'JMX monitoring port.']
			],
			"aoColumns": [
			{ "sTitle": "Port Title" },
			{ "sTitle": "Port Number" },
			{ "sTitle": "Description" }
			]
	    });
	},
	fillUserDir : function(){
		var hostName =$('#hostForNode option:selected').text();
		Node_Manager.hostIP = hostName;
		RemoteManager.getUserHomeDirectoryPathForHost(hostName,Node_Manager.fillUserHome);
	},
	fillUserHome : function(val){
		$('#dirPath').val(val+'/QueryIONodes/NodeManager');
	},
	startNodeMonitoring: function(){
   		Node_Manager.currentOperation = 'startNodeMonitoring';
   		Util.addLightbox("addNM","pages/popup.jsp");
   	},
   	stopNodeMonitoring: function(){
   		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm("Are you sure you want to stop monitoring of this node?",'Monitoring is running',function(val)
		{
		
			if (val){
				Node_Manager.currentOperation = 'stopNodeMonitoring';
		   		Util.addLightbox("addNM","pages/popup.jsp");
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
   	handleStopMonitoringResponse: function(resp){
   		var id = 'stopNodeMonitoring';
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
   		var id = 'startNodeMonitoring';
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
	
	isPortNumberNumericFields : function()
	{
		
		Node_Manager.portNumberValues.splice(0 , Node_Manager.portNumberValues.length);
		Node_Manager.portNumberValues.push(dwr.util.byId('LocalizerPort').value);
		Node_Manager.portNumberValues.push(dwr.util.byId('httpPort').value);
		Node_Manager.portNumberValues.push(dwr.util.byId('jmxPort').value);
		var isNumeric = Util.isNumericPortNumbers(Node_Manager.portNumberValues);
		if(isNumeric)
		{
			Node_Manager.pageChange(3);
		}
		else
		{
			jAlert("Only integers are allowed in port number fields.","Incomplete Detail");
			$("#popup_container").css("z-index","9999999");
		}
		
	},
	
	updateNMTable : function()
	{
		if(Node_Manager.timerFlag) {
			var timerProcess = setTimeout(function() { RemoteManager.getAllNodeManagersSummaryTable(false, Node_Manager.populateTable);  } ,1000);
			Node_Manager.timer.push(timerProcess);
		} else 	{
			for(var i=0;i<Node_Manager.timer.length;i++){
				clearTimeout(Node_Manager.timer[i]);
			}
		}
	},
	
   	
};