	RM_Config = {
			nnConfigKeyList :[], 
			nnConfigMap : {},
			nodeId: null,
			isSaveCalled: false,
			
			ready : function()
			{
				
				var node = Resource_Manager.selectedNode[0];
				var index = parseInt(node.substring(4));
				var nodeDetail = rmCache[index];
				RM_Config.nodeId = nodeDetail[0];
				RemoteManager.getNodeConfig(RM_Config.nodeId, RM_Config.fillConfigTable);
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

				RM_Config.nnConfigKeyList = [];
				
				var tableRow = new Array();
				var row;
				for(var i=0; i<rowList.length; i++)
				{
					row = rowList[i];
					var rowData = new Array();
					RM_Config.nnConfigKeyList.push(row[0]);
					for(var j=0;j<row.length;j++)
					{
						if(j==1){
							rowData.push('<input type="text" size = "100%" id="' +row[0] + '" value="' + row[j] +' " onchange="javascript:RM_Config.saveChangedValue(this);">');
							continue;
						}
						rowData.push(row[j]);
					}
					rowData.push("<a href=\"javascript:RM_Config.deleteKey('"+row[0]+"');\"><img  src=\"images/delete_icon.png\" style=\"height: 12pt; margin-right: 1pt; margin-top: 1pt;\"/></a>");
					tableRow.push(rowData);	
				}
				
				$('#rm_config_table').dataTable({
			        "bPaginate": false,
					"bLengthChange": true,
					"bFilter": false,
					"bSort": true,
					"bDestroy": true,
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
						
						RemoteManager.deleteConfigurationKey(RM_Config.nodeId,key,RM_Config.handleDeleteKeyResponse);
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
				RM_Config.isSaveCalled = true;
				
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
				
				RemoteManager.updateNodeConfig(RM_Config.nodeId,keyList, nnConfigValuesList,RM_Config.nodeConfigUpdated);
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
			},
			addNewKey : function(){
				Util.addLightbox("saveSettingsTab","resources/addNewConfigKey.html",null, null);
			},
			closeBox : function()
			{
				Util.removeLightbox("saveSettingsTab");
			},
			
			addNewKeyinConfig : function(){
				var id = 'Save';
				
				dwr.util.cloneNode('pop.pattern',{ idSuffix: id});
				dwr.util.byId('pop.pattern'+id).style.display = '';
				dwr.util.byId('popup.image.processing'+id).style.display = '';
				dwr.util.setValue('popup.component','Update Settings');
				dwr.util.setValue('popup.host'+id,id);
				dwr.util.setValue('popup.message'+id,'Updating Settings...');
				dwr.util.setValue('popup.status'+id,'');
				
				$('#addNewKeyDiv').hide();
				$('#processingPopUp').show();
				
				var nnConfigValuesList = new Array();
				var keyList =new Array() ;
				for(var attr in this.nnConfigMap){
					keyList.push(attr);
					nnConfigValuesList.push(this.nnConfigMap[attr]);
				}
				var newKey = $('#configKey').val();
				var keyval = $('#configValue').val();
				keyList.push(newKey);
				nnConfigValuesList.push(keyval);
				RemoteManager.updateNodeConfig(RM_Config.nodeId,keyList, nnConfigValuesList,RM_Config.nodeConfigUpdated);
			}
	};