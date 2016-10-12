JB_Config = {
		nnConfigKeyList :[], 
		nnConfigMap : {},
		nodeId: null,
		isSaveCalled : false,
		
		ready : function()
		{
			var node = Node_Manager.selectedNode[0];
			var index = parseInt(node.substring(4));
			var nodeDetail = JB.jbCache[index];
			JB_Config.nodeId = nodeDetail[0];
			RemoteManager.getNodeConfig(JB_Config.nodeId, NM_Config.fillConfigTable);
			$('#refreshViewButton').css("display","none");
		},
		
		fillConfigTable : function(rowList)
		{
			$('#rhs_header').text("Node Configuration");
			
			var colList=[];
			colList.push({ "sTitle": "Key"});
			colList.push({ "sTitle": "Value"});
			colList.push({ "sTitle": "Description"});
			colList.push({ "sTitle": ""});

			JB_Config.nnConfigKeyList = [];
			
			var tableRow = new Array();
			var row;
			for(var i=0; i<rowList.length; i++)
			{
				row = rowList[i];
				var rowData = new Array();
				NM_Config.nnConfigKeyList.push(row[0]);
				for(var j=0;j<row.length;j++)
				{
					if(j==1){
						rowData.push('<input type="text" size = "100%" id="' +row[0] + '" value="' + row[j] +' " onchange="javascript:JB_Config.saveChangedValue(this);">');
						continue;
					}
					rowData.push(row[j]);
				}
				rowData.push("<a href=\"javascript:JB_Config.deleteKey('"+row[0]+"');\"><img  src=\"images/delete_icon.png\" style=\"height: 12pt; margin-right: 1pt; margin-top: 1pt;\"/></a>");
				tableRow.push(rowData);	
			}
			
			$('#jb_config_table').dataTable({
		        "bPaginate": false,
				"bLengthChange": true,
				"bFilter": false,
				"bDestroy": true,
				"bSort": true,
				"bInfo": false,
				"bAutoWidth": false,
				"aaData": tableRow,
		        "aoColumns": colList
		    });
		},
		deleteKey : function(key){
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm('Are you sure you want to delete key?','Confirm',function(val)
			{
				if (val)
				{
					
					RemoteManager.deleteConfigurationKey(JB_Config.nodeId,key,JB_Config.handleDeleteKeyResponse);
				}
					
			});
			
		},
		handleDeleteKeyResponse : function(dwrResponse){
			if(dwrResponse.taskSuccess){
				jAlert("Key deleted successfully","Success");
				this.ready();
			}else{
				jAlert(dwrResponse.responseMessage, "Failed");
			}
		},
		saveSettings : function()
		{
			document.getElementById('config.save').disabled = true;
			JB_Config.isSaveCalled = true;
			
			Util.addLightbox('saveSettingsTab','pages/popup.jsp');
		},
		
		callForUpdate : function()
		{
			var nnConfigValuesList = new Array();
			var keyList =new Array() ;
			for(var attr in this.nnConfigMap){
				keyList.push(attr);
				nnConfigValuesList.push(this.nnConfigMap[attr]);
			}

			RemoteManager.updateNodeConfig(JB_Config.nodeId,keyList, nnConfigValuesList,JB_Config.nodeConfigUpdated);
		},
		
		nodeConfigUpdated : function(dwrResponse)
		{
			id = 'Save';
			message = dwrResponse.responseMessage;
			if (dwrResponse.taskSuccess)
			{
				status = "Success";
				imgId = "popup.image.success";
			}
			else
			{
				status = "Failure";
				imgId = "popup.image.fail";
			}
			
			dwr.util.byId('popup.image.processing'+id).style.display = 'none';
			dwr.util.byId(imgId+id).style.display = '';
			
			dwr.util.setValue('popup.message'+id, message);
			dwr.util.setValue('popup.status'+id, status);
			dwr.util.byId('ok.popup').disabled = false;
			
			document.getElementById('config.save').disabled = false;
		},
		backToSummary : function(){
			$('#refreshViewButton').css("display","block");
			Navbar.refreshView();
		},
		saveChangedValue : function(element){
		 this.nnConfigMap[element.id] = element.value;
		 
		}
};