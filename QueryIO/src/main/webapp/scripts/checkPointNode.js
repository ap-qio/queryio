CHN = {
		selectedNode : [] ,
		hostArray:[],
		operation:'',
		totalCPN:0,
		ready : function(){
			RemoteManager.getAllHostDetails(CHN.fillHostArray);
			CHN.enableDisableButton();
		},
		fillHostArray : function(list){
			CHN.hostArray = list;
			RemoteManager.getAllCheckpointNodes(CHN.populateCheckPointNodeTable);
		},
		populateCheckPointNodeTable : function(list){
			var tabledata =[];
			if(list!=null){
				for(var i=0;i<list.length;i++){
					var checkPoint = list[i];
					if(!checkPoint.hasOwnProperty('status')){
						continue;
					}
					CHN.totalCPN++;
					var host = checkPoint.hostId;
					if(CHN.hostArray.length>0){
						
						for(var j=0;j<CHN.hostArray.length;j++){
							if(host==CHN.hostArray[j].id){
								host=CHN.hostArray[j].hostIP;
								break;
							}
						}
					}
					
					tabledata.push(['<input type="checkbox" onClick="javascript:CHN.nodeSelected(this)" id="'+checkPoint.id+'">',
					checkPoint.id,host,checkPoint.jmxPort,checkPoint.status+'<input type="hidden" id="status'+checkPoint.id+'" value="'+checkPoint.status+'">']);
				}
			}
			$('#check_point_node_table').dataTable( {
		        "bPaginate": false,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": true,
				"bInfo": false,
				"bAutoWidth": true,
				"bDestroy": true,
				"aaData": tabledata,
		        "aoColumns": [
		             { "sTitle": ""},
		            { "sTitle": "CheckPoint Node" },
		            { "sTitle": "Host" },
		            { "sTitle": "JMX Port" },
		            { "sTitle": "Status" }
		        ]
		    	} );
			
			
		},
		nodeSelected : function(element){
			var nodeId = element.id;
			if(element.checked){
				CHN.selectedNode.push(nodeId);
				var status = $('#status'+nodeId).val();
				if(status=='Started'){
					$('#stopchn').removeAttr("disabled");
					
				}else if(status=='Stopped'){
					$('#startchn').removeAttr("disabled");
				}else{
					$('#stopchn').removeAttr("disabled");
				}
			}else{
				for(var i=0;i<CHN.selectedNode.length;i++){
					if(CHN.selectedNode[i]==nodeId){
						CHN.selectedNode.splice(i,1);
					}
				}
			}
			CHN.enableDisableButton();
			
		},
		enableDisableButton : function(){
			if(CHN.selectedNode.length>0){
				if(CHN.selectedNode.length==1){
					$('#configchn').removeAttr("disabled");
//					$('#startchn').removeAttr("disabled");
//					$('#stopchn').removeAtt("disabled");
				}
				$('#deletechn').removeAttr("disabled");
				
			}else{
				$('#configchn').attr("disabled",true);
				$('#startchn').attr("disabled",true);
				$('#stopchn').attr("disabled",true);
				$('#deletechn').attr("disabled",true);
			}
		},
		newCheckPointReady : function(){
		
			RemoteManager.getAllHostDetails(CHN.fillHostName);
			RemoteManager.getNonStandByNodes(CHN.fillNameNode);
			$('#nodeId').val('CheckPoint'+(CHN.totalCPN+1));
			
		},
		fillHostName : function(list)
		{
			var optionData = '';
			if(list == null || list == undefined || list.length == 0)
				optionData = '<option value="">Select Host</option>';
			else
			{
				for(var i=0;i<list.length;i++)
				{
					optionData+='<option value="'+list[i].id+'">'+list[i].hostIP+'</option>';
				}				
			}
			$('#hostIP').html(optionData);
			CHN.fillUserDir();
		},
		fillNameNode : function(list){
				
				var data='';
				
				if(list == null || list == undefined || list.length == 0)
					data='<option value="">Select NameNode</option>';
				else
				{
					for(var i=0;i<list.length;i++){
						var node=list[i];
						data+='<option value="'+node.id+'">'+node.id+'</option>'
					}					
				}
				$('#nameNode').html(data);
				
			
		},
		
		addNewCheckPoint : function(){
			Util.addLightbox("addchpn", "resources/add_check_point.html", null, null);
		},
		closeBox : function(isRefresh){
			Util.removeLightbox("addchpn");
			if(isRefresh)
				Navbar.refreshView();
			
		},
		configChn : function(){
				config_nodeId = CHN.selectedNode[0];
				Util.importResource("service_ref","resources/nn_config.html");
		},
		startChn : function(){

			CHN.operation='Start';
			Util.addLightbox("addchpn", "resources/check_point_operation.html", null, null);
			
		},
		nodeStarted : function(dwrResponse){
			jAlert(dwrResponse.message);
		},
		stopChn : function(){
			CHN.operation='Stop';
			Util.addLightbox("addchpn", "resources/check_point_operation.html", null, null);
		},
		deleteChn : function()
		{
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm('Are you sure you want to delete selected item(s)?','Delete CheckPoint Node(s)',function(val)
			{
				if (val == true)
				{
					CHN.operation='Delete';
					Util.addLightbox("addchpn", "resources/check_point_operation.html", null, null);
				}
				else
				{
					return;
				}
			});
		},
		nodeStopped : function(dwrResponse){
			jAlert(dwrResponse.message);
		},
		saveNode : function(){
			var host = $('#hostIP').val();
			var nameNode = $('#nameNode').val();
			var nodeId = $('#nodeId').val();
//			var serverPort = $('#serverPort').val();
			var httpPort = $('#httpPort').val();
			
			var dirPath= $('#dirPath').val();
			var jmxPort=$('#jmxPort').val();
			
			if(host==""){
				jAlert("Host was not selected.Please select a host for checkpoint node.","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if(nameNode==""){
				jAlert("NameNode was not selected.Please select a NameNode .","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if(nodeId==""){
				jAlert("Please provide a Unique Identifier for checkpoint node.","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if(dirPath==""){
				jAlert("Please provide a directory path for installation of new checkpoint node.","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if(Util.isContainWhiteSpace(dirPath)){
				jAlert("Directory path contains space. Please remove space from Directory path.","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
				return;
			}
//			if(serverPort==""){
//				jAlert("Please provide  server port for new checkpoint node.","Incomplete detail.");
//				$("#popup_container").css("z-index","9999999");
//				return;
//			}
			
			if(httpPort==""){
				jAlert("Please provide  http port for new checkpoint node.","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if(jmxPort==""){
				jAlert("Please provide jmx port for new checkpoint node.","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			
			
			$('#popupnode').text(nodeId);
			$('#popupmessage').text('Installing checkpoint node at host '+$('#hostIP option:selected').text());
			$("#checkPointNode2").css("display","");
			$("#checkPointNode1").css("display","none");
			$('#otherInstruction_tr').remove();
			$('#instruction_tr').remove();
			
//			(int hostId, String nodeId, String namenodeId ,	String dirPath, String httpPort, String jmxPort) 
			RemoteManager.addCheckpointNode(parseInt(host),nodeId,nameNode,dirPath,httpPort,jmxPort,CHN.nodeSaved);
		},
		nodeSaved : function(dwrResponse){
			if(dwrResponse.taskSuccess){
				$('#popupmessage').text(dwrResponse.responseMessage);
				$('#popupstatus').text('Success');
				$("#imageprocessing").css("display","none");
				$("#imagesuccess").css("display","");
				if($('#isCheckPointNodeStart').is(":checked")){
					CHN.selectedNode=[];
					CHN.selectedNode.push(dwrResponse.id);
					Util.removeLightbox("addchpn");
					CHN.startChn();
					return;
				}
			}
			else{
				
				var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
				document.getElementById('log_div').innerHTML=log;
				document.getElementById('log_div').style.display="block";
				
				$('#popupmessage').text(dwrResponse.responseMessage);
				$('#popupstatus').text('Failed');
				$("#imageprocessing").css("display","none");
				$("#imagefail").css("display","");
			}
			$("#okpopup").removeAttr("disabled");
		},
		fillUserDir : function(){
			var hostName =$('#hostIP option:selected').text();
			RemoteManager.getUserHomeDirectoryPathForHost(hostName,CHN.fillUserHome);
		},
		fillUserHome : function(val){
			$('#dirPath').val(val+'/QueryIONodes/CheckPointNode');
		},
		nextStep : function(step){
			
			switch(step){
				case 1:
					
					
					$('#checkPointWizard1').css('display','');
					$('#checkPointWizard2').css('display','none');
					break;
				case 2:
					var host = $('#hostIP').val();
					var nameNode = $('#nameNode').val();
					var nodeId = $('#nodeId').val();
					if(host==""){
						jAlert("Host was not selected.Please select a host for check point node.","Incomplete detail.");
						$("#popup_container").css("z-index","9999999");
						return;
					}
					if(nameNode==""){
						jAlert("NameNode was not selected.Please select a NameNode .","Incomplete detail.");
						$("#popup_container").css("z-index","9999999");
						return;
					}
					if(nodeId==""){
						jAlert("Please provide a Unique Identifier for check point node.","Incomplete detail.");
						$("#popup_container").css("z-index","9999999");
						return;
					}
					$('#checkPointWizard1').css('display','none');
					$('#checkPointWizard2').css('display','');
					
					break;
		
			}
		},
		startNodeOperation : function(){
			
			var operation =CHN.operation;
			for(var i =0;i<CHN.selectedNode.length;i++){
				var id = CHN.selectedNode[i];
				dwr.util.setValue('popupcomponent','CheckPoint Node');
				dwr.util.cloneNode('poppattern',{ idSuffix:id });
				dwr.util.setValue('popuphost' + id,id);
				dwr.util.setValue('popupmessage' + id,operation+ ' operation performed on '+id);	
				dwr.util.setValue('popupstatus' + id,'Processing');
				dwr.util.byId('poppattern' + id).style.display = '';
			}
			
			if(CHN.operation=='Start'){
				for(var i =0;i<CHN.selectedNode.length;i++){
					//start node call.
					RemoteManager.startNode(CHN.selectedNode[i] , false ,CHN.nodeOperationPerformed);
				}
			}else if(CHN.operation=='Stop'){
				for(var i =0;i<CHN.selectedNode.length;i++){
					//start node call.
					RemoteManager.stopNode(CHN.selectedNode[i],CHN.nodeOperationPerformed);
				}
			}else if(CHN.operation=='Delete'){
				for(var i =0;i<CHN.selectedNode.length;i++){
					//start node call.
					RemoteManager.deleteNode(CHN.selectedNode[i],CHN.nodeOperationPerformed);
				}
			}
			
			
		},
		nodeOperationPerformed : function(dwrResponse){
			var id = dwrResponse.id;
			if(dwrResponse.taskSuccess)	
			{
				img_src='images/Success_img.png'
				status = 'Success'; 
				dwr.util.byId('imagesuccess' + id).style.display = '';
			}
			else
			{
				img_src='images/Fail_img.png'
				status = 'Fail';
				dwr.util.byId('imagefail' + id).style.display = '';
			}
			dwr.util.byId('imageprocessing' + id).style.display = 'none';
			dwr.util.setValue('popupmessage' + id,dwrResponse.responseMessage);
			dwr.util.setValue('popupstatus' + id,status);
			document.getElementById('okpopup').disabled = false;
			
		},
	
		addNewHost : function()
		{
			Util.removeLightbox("addchpn");
			Util.addLightbox("addchpn", "resources/new_host_box.html", null, null);
		},
		
		closeHostBox : function(){
			Util.removeLightbox("addchpn");
			CHN.addNewCheckPoint();
		},
		
		
};