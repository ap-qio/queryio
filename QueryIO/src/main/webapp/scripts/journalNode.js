JN = {
		selectedNode : [] ,
		hostArray:[],
		operation:'',
		totalJN:0,
		ready : function(){
			RemoteManager.getAllHostDetails(JN.fillHostArray);
			JN.enableDisableButton();
		},
		fillHostArray : function(list){
			JN.hostArray = list;
			RemoteManager.getAllJournalNodes(JN.populateJournalNodeTable);
		},
		populateJournalNodeTable : function(list){
			var tabledata =[];
			if(list!=null){
				for(var i=0;i<list.length;i++){
					var Journal = list[i];
					if(!Journal.hasOwnProperty('status')){
						continue;
					}
					JN.totalJN++;
					var host = Journal.hostId;
					if(JN.hostArray.length>0){
						
						for(var j=0;j<JN.hostArray.length;j++){
							if(host==JN.hostArray[j].id){
								host=JN.hostArray[j].hostIP;
								break;
							}
						}
					}
					
					tabledata.push(['<input type="checkbox" onClick="javascript:JN.nodeSelected(this)" id="'+Journal.id+'">',
					Journal.id,host,Journal.jmxPort,Journal.status+'<input type="hidden" id="status'+Journal.id+'" value="'+Journal.status+'">']);
				}
			}
			$('#journal_node_table').dataTable( {
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
		            { "sTitle": "Journal Node" },
		            { "sTitle": "Host" },		            
		            { "sTitle": "JMX Port" },
		            { "sTitle": "Status" }
		        ]
		    } );
		},
		nodeSelected : function(element){
			var nodeId = element.id;
			if(element.checked){
				JN.selectedNode.push(nodeId);
				var status = $('#status'+nodeId).val();
				if(status=='Started'){
					$('#stopJN').removeAttr("disabled");
					
				}else if(status=='Stopped'){
					$('#startJN').removeAttr("disabled");
				}else{
					$('#stopJN').removeAttr("disabled");
				}
			}else{
				for(var i=0;i<JN.selectedNode.length;i++){
					if(JN.selectedNode[i]==nodeId){
						JN.selectedNode.splice(i,1);
					}
				}
			}
			JN.enableDisableButton();
			
		},
		enableDisableButton : function(){
			if(JN.selectedNode.length>0){
				if(JN.selectedNode.length==1){
					$('#configJN').removeAttr("disabled");
//					$('#startJN').removeAttr("disabled");
//					$('#stopJN').removeAtt("disabled");
				}
				$('#deleteJN').removeAttr("disabled");
				
			}else{
				$('#configJN').attr("disabled",true);
				$('#startJN').attr("disabled",true);
				$('#stopJN').attr("disabled",true);
				$('#deleteJN').attr("disabled",true);
			}
		},
		newJournalReady : function(){
		
			RemoteManager.getAllHostDetails(JN.fillHostName);
			RemoteManager.getNonStandByNodes(JN.fillNameNode);
			$('#nodeId').val('JournalNode'+(JN.totalJN+1));
			
		},
		fillHostName : function(list){
			if(list==null){
				return;
			}
			var optionData = '<option value="">Select Host</option>';
			for(var i=0;i<list.length;i++){
				optionData+='<option value="'+list[i].id+'">'+list[i].hostIP+'</option>';
			}
			$('#hostIP').html(optionData);
			
		},
		fillNameNode : function(list){
				
				var data='<option value="">Select NameNode</option>';
				for(var i=0;i<list.length;i++){
					var node=list[i];
					data+='<option value="'+node.id+'">'+node.id+'</option>'
				}
				$('#nameNode').html(data);
		},
		
		addNewJournal : function(){
			Util.addLightbox("addJN", "resources/add_journal.html", null, null);
		},
		closeBox : function(isRefresh){
			Util.removeLightbox("addJN");
			if(isRefresh)
				Navbar.refreshView();
			
		},
		configJN : function(){
				config_nodeId = JN.selectedNode[0];
				Util.importResource("service_ref","resources/nn_config.html");
		},
		startJN : function(){

			JN.operation='Start';
			Util.addLightbox("addJN", "resources/journal_operation.html", null, null);
			
		},
		nodeStarted : function(dwrResponse){
			jAlert(dwrResponse.message);
		},
		stopJN : function(){
			JN.operation='Stop';
			Util.addLightbox("addJN", "resources/journal_operation.html", null, null);
		},
		deleteJN : function()
		{
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm('Are you sure you want to delete selected item(s)?','Delete Journal Node(s)',function(val)
			{
				if (val == true)
				{
					JN.operation='Delete';
					Util.addLightbox("addJN", "resources/journal_operation.html", null, null);
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
			var serverPort = $('#serverPort').val();
			var httpPort = $('#httpPort').val();
			
			var dirPath= $('#dirPath').val();
			var jmxPort=$('#jmxPort').val();
			
			if(host==""){
				jAlert("Host was not selected.Please select a host for journal node.","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if(nameNode==""){
				jAlert("NameNode was not selected.Please select a NameNode .","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if(nodeId==""){
				jAlert("Please provide a Unique Identifier for journal node.","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if(dirPath==""){
				jAlert("Please provide a directory path for installation of new journal node.","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if(Util.isContainWhiteSpace(dirPath)){
				jAlert("Directory path contains space. Please remove space from Directory path.","Incomplete Detail");
				$("#popup_container").css("z-index","99999999");
				return;
			}
			if(serverPort==""){
				jAlert("Please provide  server port for new journal node.","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			
			if(httpPort==""){
				jAlert("Please provide  http port for new journal node.","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			if(jmxPort==""){
				jAlert("Please provide jmx port for new journal node.","Incomplete detail.");
				$("#popup_container").css("z-index","9999999");
				return;
			}
			
			
			$('#popupnode').text(nodeId);
			$('#popupmessage').text('Installing Journal Node at host '+$('#hostIP option:selected').text());
			$("#journalNode2").css("display","");
			$("#journalNode1").css("display","none");
			$('#otherInstruction_tr').remove();
			$('#instruction_tr').remove();
			
//			addJournalNode(int hostId, String nodeId, String dirPath, String serverPort, String httpPort, String jmxPort)
			RemoteManager.addJournalNode(parseInt(host),nodeId,dirPath,serverPort,httpPort,jmxPort,JN.nodeSaved);
		},
		nodeSaved : function(dwrResponse){
			if(dwrResponse.taskSuccess){
				$('#popupmessage').text(dwrResponse.responseMessage);
				$('#popupstatus').text('Success');
				$("#imageprocessing").css("display","none");
				$("#imagesuccess").css("display","");
				if($('#isJournalNodeStart').is(":checked")){
					JN.selectedNode=[];
					JN.selectedNode.push(dwrResponse.id);
					Util.removeLightbox("addJN");
					JN.startJN();
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
			RemoteManager.getUserHomeDirectoryPathForHost(hostName,JN.fillUserHome);
		},
		fillUserHome : function(val){
			$('#dirPath').val(val+'/QueryIONodes/JournalNode');
		},
		nextStep : function(step){
			
			switch(step){
				case 1:
					
					
					$('#journalWizard1').css('display','');
					$('#journalWizard2').css('display','none');
					break;
				case 2:
					var host = $('#hostIP').val();
					var nameNode = $('#nameNode').val();
					var nodeId = $('#nodeId').val();
					if(host==""){
						jAlert("Host was not selected.Please select a host for journal node.","Incomplete detail.");
						$("#popup_container").css("z-index","9999999");
						return;
					}
					if(nameNode==""){
						jAlert("NameNode was not selected.Please select a NameNode .","Incomplete detail.");
						$("#popup_container").css("z-index","9999999");
						return;
					}
					if(nodeId==""){
						jAlert("Please provide a Unique Identifier for journal node.","Incomplete detail.");
						$("#popup_container").css("z-index","9999999");
						return;
					}
					$('#journalWizard1').css('display','none');
					$('#journalWizard2').css('display','');
					
					break;
		
			}
		},
		startNodeOperation : function(){
			
			var operation =JN.operation;
			for(var i =0;i<JN.selectedNode.length;i++){
				var id = JN.selectedNode[i];
				dwr.util.setValue('popupcomponent','Journal Node');
				dwr.util.cloneNode('poppattern',{ idSuffix:id });
				dwr.util.setValue('popuphost' + id,id);
				dwr.util.setValue('popupmessage' + id,operation+ ' operation performed on '+id);	
				dwr.util.setValue('popupstatus' + id,'Processing');
				dwr.util.byId('poppattern' + id).style.display = '';
			}
			
			if(JN.operation=='Start'){
				for(var i =0;i<JN.selectedNode.length;i++){
					//start node call.
					RemoteManager.startNode(JN.selectedNode[i] , false ,JN.nodeOperationPerformed);
				}
			}else if(JN.operation=='Stop'){
				for(var i =0;i<JN.selectedNode.length;i++){
					//start node call.
					RemoteManager.stopNode(JN.selectedNode[i],JN.nodeOperationPerformed);
				}
			}else if(JN.operation=='Delete'){
				for(var i =0;i<JN.selectedNode.length;i++){
					//start node call.
					RemoteManager.deleteNode(JN.selectedNode[i],JN.nodeOperationPerformed);
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
			Util.removeLightbox("addJN");
			Navbar.selectedTabName = 'journal_node'
			Util.addLightbox("addJN", "resources/new_host_box.html", null, null);
		},
		
		closeHostBox : function(){
			Util.removeLightbox("addJN");
			JN.addNewJournal();
		},
		
		
};