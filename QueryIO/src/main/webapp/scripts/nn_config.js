NN_Config = {
		
		nnConfigKeyList :[], 
		nnConfigMap : {},
		isChangeShown: true,
		isAnalyticsChangeShown: true,
		isSaveCalled : false,
		
		ready : function()
		{
			RemoteManager.getNodeConfig(config_nodeId, NN_Config.fillConfigTable);
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
		
			NN_Config.nnConfigKeyList = [];
			
			var tableRow = new Array();
			var row;
			for(var i=0; i<rowList.length; i++)
			{
				row = rowList[i];
				var rowData = new Array();
				NN_Config.nnConfigKeyList.push(row[0]);
				for(var j=0;j<row.length;j++)
				{
					if(j==1){
						rowData.push('<input type="text" size = "100%" id="' +row[0] + '" value="' + row[j] +' " onchange="javascript:NN_Config.saveChangedValue(this);">');
						continue;
					}
					rowData.push(row[j]);
				}
				rowData.push("<a href=\"javascript:NN_Config.deleteKey('"+row[0]+"');\"><img  src=\"images/delete_icon.png\" style=\"height: 12pt; margin-right: 1pt; margin-top: 1pt;\"/></a>");
				tableRow.push(rowData);	
			}
			
			$('#nn_config_table').dataTable({
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
					
					RemoteManager.deleteConfigurationKey(NN_Config.nodeId,key,NN_Config.handleDeleteKeyResponse);
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
			NN_Config.isSaveCalled = true;
			
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
			
			RemoteManager.updateNodeConfig(config_nodeId,keyList, nnConfigValuesList,NN_Config.nodeConfigUpdated);
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
		
		backToSummary : function()
		{
			$('#refreshViewButton').css("display","block");
			Navbar.refreshView();
		},
		
		saveChangedValue : function(element)
		{
		 this.nnConfigMap[element.id] = element.value;
		},
		
		configDbName : function()
		{
			Util.addLightbox("addDB","resources/configureDb.html",null, null);
		},
		
		fillDbList : function()
		{
			fillDBNamesForNameNode(config_nodeId);
		},
		
		showChooseAnalyticsDbName : function()
		{
			if (NN_Config.isAnalyticsChangeShown)
			{
				$('#analyticsDB').css("display","");
				$('#analyticsDbNameText').css("display","none");
				$('#chooseAnalyticsDbName').val('Keep Unchanged');
				NN_Config.isAnalyticsChangeShown = false;
			}
			else
			{
				$('#analyticsDB').css("display","none");
				$('#analyticsDbNameText').css("display","");
				$('#chooseAnalyticsDbName').val('Change');
				NN_Config.isAnalyticsChangeShown = true;
			}
		},
		
		showChooseDbName : function()
		{
			if (NN_Config.isChangeShown)
			{
				$('#NameNodeDB').css("display","");
				$('#dbNameText').css("display","none");
				$('#chooseDbName').val('Keep Unchanged');
				NN_Config.isChangeShown = false;
			}
			else
			{
				$('#NameNodeDB').css("display","none");
				$('#dbNameText').css("display","");
				$('#chooseDbName').val('Change');
				NN_Config.isChangeShown = true;
			}
		},
		
		saveConfigDbName : function()
		{
			if ((!NN_Config.isChangeShown) && (dwr.util.byId('NameNodeDB').value == 0))
			{
				jQuery.alerts.okButton = ' Yes ';
				jQuery.alerts.cancelButton  = ' No';
				
				if ((!NN_Config.isAnalyticsChangeShown) && (dwr.util.byId('analyticsDB').value == 0))
				{
					jConfirm('Databases not selected for namenode. You will not be able to perform searching of files on HDFS cluster using metadata or user defined tags and Big Data Analytics for this NameNode. ' +
							'Are you sure you want to continue?','Incomplete Detail',function(val)
					{
						if (val)
						{
							RemoteManager.updateDb(config_nodeId, "", "", NN_Config.configDbNameUpdated);
						}
						else
							return;
					});
					$("#popup_container").css("z-index","9999999");
				}
				else
				{
					jConfirm('Metadata Database not selected for namenode. You will not be able to perform searching of files on HDFS cluster using metadata or user defined tags for this NameNode. ' +
							'Are you sure you want to continue?','Incomplete Detail',function(val)
					{
						if (val)
						{
							var analyticsDbName = $('#analyticsDB').val();
							RemoteManager.updateDb(config_nodeId, "", analyticsDbName, NN_Config.configDbNameUpdated);
						}
						else
							return;
					});
					$("#popup_container").css("z-index","9999999");
				}
				jQuery.alerts.okButton = ' Ok ';
			}
			else if ((!NN_Config.isAnalyticsChangeShown) && (dwr.util.byId('analyticsDB').value == 0))
			{
				jQuery.alerts.okButton = ' Yes ';
				jQuery.alerts.cancelButton  = ' No ';
				jConfirm('All the data for HDFS metadata, Extended Metadata, user defined tags and Big Data Analytics on this NameNode will be stored in Metadata database. ' +
						'Are you sure you want to continue?','Incomplete Detail',function(val)
				{
					if (val)
					{
						var dbName = $('#NameNodeDB').val();
						RemoteManager.updateDb(config_nodeId, dbName, "", NN_Config.configDbNameUpdated);
					}
					else
						return;
				});
				$("#popup_container").css("z-index","9999999");
			}
			else
			{
				var dbName = $('#NameNodeDB').val();
				var analyticsDbName = $('#analyticsDB').val();
				RemoteManager.updateDb(config_nodeId, dbName, analyticsDbName, NN_Config.configDbNameUpdated);
			}
		},
		
		configDbNameUpdated : function(dwrResponse)
		{
			var message = dwrResponse.responseMessage;
			if (dwrResponse.taskSuccess)
			{
				jAlert("Query database configured successfully with the namespace. Restart NameNode to implement the changes done in configuration for database.","Successfully saved");
			}
			else
			{
				jAlert("Updation of configuration failed. " + message,"Updation failed");
			}
			
			NN_Config.closeBox();
			NN_Summary.showConfiguration();
		},
		
		closeBox : function()
		{
			Util.removeLightbox("addDB");
		},
		addNewKey : function(){
			Util.addLightbox("addDB","resources/addNewConfigKey.html",null, null);
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
			RemoteManager.updateNodeConfig(config_nodeId,keyList, nnConfigValuesList,NN_Config.nodeConfigUpdated);
		}
		
};