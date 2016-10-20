AutoCluster = 
{
	hostRow : '',
	evaluationHostRow : '',
	totalHosts : 1,
	currentView : 'evaluation_hosts_credentials',
	hostsDataArray : {},
	agentPort : null,
	currentStep : 1,
	addedHosts : [],
	servicesListForHosts : [],
	hostForResourceManager : null,
	hostForNameNode : null,
	dataNodePortsHTMLContent : null,
	nameNodePortsHTMLContent : null,
	resourceManagerPortsHTMLContent : null,
	nodeManagerPortsHTMLContent : null,
	totalConfigurationsRecieved : 0,
	totalConfigsToBeRcvd : 4,
	hostIdMappingMap : null,
	hostsBeingUsed : null,
	statusImagesContent : null,
	hostDetails : [],
	resourceManagerId : null,
	diskEvaluation : null,
	volumeEvaluation : null,
	isVolume : false,
	isDisk : false,
	isEvaluation : false,
	responseId : '',
	hostid : false,
	nameNodeConfig : null,
	dataNodeConfig : null,
	rmConfig : null,
	nmconfig : null,
	evaluationHostIdMap : null,
	installed_nn : [],
	installed_dn : [],
	installed_rm : [],
	installed_nm : [],
	successSetup : true,
	totalSingle : 0,
	totalNN : 0,
	totalDN : 0,
	totalRM : 0,
	totalNM : 0,
	responseNN : 0,
	responseDN : 0,
	responseRM : 0,
	responseNM : 0,
	isLocalClusterSetupEnabled : true,
	
	evaluationTotalConfigCount : 7,
	evaluationConfigRcvd : 0,

	ready : function()
	{
		AutoCluster.loadRowsMetaData();
		
		$('#refreshViewButton').css('display','none');
		$('#backButton').css('display','none');
		AutoCluster.installEvaluationCluster();
	},
	
	nextStep : function()
	{
		if(AutoCluster.currentView == 'hosts')
		{
			if(AutoCluster.validateFields() == false)
			{
				return;
			}
			$('#nextButton').css('display', '');
			$('#searchHostsDiv').css('display', 'none');
			$('#hostsCredentials').css('display', '');
			$('#hostsCredentials table').css('display', '');
			$('#hostsListWithCredentials').css('display', '');
			AutoCluster.currentView = 'hosts_credentials';
			AutoCluster.tocAgentActive();
			AutoCluster.loadHostTab();
			AutoCluster.loadSelectedHosts();
			$('#masterUsername').val('');
			$('#masterPassword').val('');
			$('#masterPort').val('22');
			$('#useMasterCredentials').attr('checked', true);
			$('#nextButton').html('Install').css('width', '100px');
			$('#backButton').css('width', '100px');
		}
		else if(AutoCluster.currentView == 'hosts_credentials')
		{
			if($("#hostsTable tbody tr").not('.hostAlreadyInstalled').length == 0)
			{
				//jAlert('Agent already installed. Click on Next to install services.');
				AutoCluster.currentView = 'hosts_services';
				$('#nextButton').html('Next').css('width', '64px');
				$('#backButton').css('width', '64px');
				AutoCluster.nextStep();
				return;
			}

			if(AutoCluster.validateFields() == false)
			{
				return;
			}
			$('#nextButton').css('display', '');
			AutoCluster.saveAllHosts();
			AutoCluster.currentView = 'hosts_services';
			$('#nextButton').html('Next').css('width', '64px');
			$('#backButton').css('width', '64px');
		}
		else if(AutoCluster.currentView == 'evaluation_hosts_credentials')
		{
			if(!AutoCluster.isLocalClusterSetupEnabled && AutoCluster.validateFields() == false)
			{
				return;
			}
			
			AutoCluster.searchHosts();	
		}
		else if(AutoCluster.currentView == 'hosts_services')
		{
			AutoCluster.tocNodeActive();
			if(AutoCluster.validateFields() == false)
			{
				return;
			}
			$('#nextButton').css('display', '');
			$('#nextButton').html('Install');
			$('#backButton').removeAttr('disabled');
			$('#hostsCredentials').css('display', 'none');
			$('#automate_cluster_setup_services').css('display', '');
			AutoCluster.currentView = 'services_install_nodes_selection';
			AutoCluster.setupServices();
		}
		else if(AutoCluster.currentView == 'services_install_nodes_selection')
		{
			if(AutoCluster.validateFields() == false)
			{
				return;
			}
			$('#installerDiv').css('display', '');
			$('#automate_cluster_install_services_status').css('display', '');
			$('#automate_cluster_setup_services').css('display', 'none');
			$('#nextButton').css('display', 'none');
			$('#backButton').css('display', 'none');
			AutoCluster.tocStatusActive();
			
			AutoCluster.currentView = 'services_install_nodes_status';
			AutoCluster.loadServicesListForHosts();
			AutoCluster.loadHostsListBeingInstalled();
			AutoCluster.showInstallationStatus();
			AutoCluster.installSetup();
			
			//Util.addLightbox("cluster_setup_status", "resources/automate_cluster_setup_status.html", null, null);
			
		}
	},
	
	closeBox : function()
	{
		Util.removeLightbox("cluster_setup_status");
	},
	
	backStep : function()
	{
		if(AutoCluster.currentView == 'install_mode')
		{
			//	Disable button instead of this.
		}
		else if(AutoCluster.currentView == 'hosts')
		{
			$('#setupMethodTable').css('display','');
			$('#serachHostTable').css('display', 'none');
			$('#searchedHostTable').css('display', 'none');
			$('#backButton').css('display','none');
			$('#nextButton').css('display','none');
			$('#toc').css('display','none');
			AutoCluster.currentView = 'evaluation_hosts_credentials';
		}
		else if(AutoCluster.currentView == 'hosts_credentials')
		{
			$('#nextButton').css('display', '');
			$('#nextButton').html('Next').css('width', '64px');
			$('#backButton').css('width', '64px');
			$('#searchHostsDiv').css('display', '');
			$('#hostsCredentials').css('display', 'none');
			AutoCluster.tocSlectHostActive();
			AutoCluster.currentView = 'hosts';
		}
		else if(AutoCluster.currentView == 'services_install_nodes_selection' )
		{
			$('#nextButton').css('display', '');
			$('#nextButton').html('Install').css('width', '100px');
			$('#backButton').css('width', '100px');
			$('#hostsCredentials').css('display', '');
			$('#automate_cluster_setup_services').css('display', 'none');
			AutoCluster.currentView = 'hosts_credentials';
			AutoCluster.tocAgentActive();
		}
		else if(AutoCluster.currentView == 'hosts_services' )
		{
			$('#nextButton').css('display', '');
			$('#nextButton').html('Next').css('width', '64px');
			$('#backButton').css('width', '64px');
			$('#searchHostsDiv').css('display', '');
			$('#hostsCredentials').css('display', 'none');
			AutoCluster.tocSlectHostActive();
			AutoCluster.currentView = 'hosts';
		}
		else if(AutoCluster.currentView == 'services_install_nodes_status')
		{
			$('#nextButton').css('display', '');
			$('#automate_cluster_install_services_status').css('display', 'none');
			$('#automate_cluster_setup_services').css('display', '');
			AutoCluster.currentView = 'services_install_nodes_selection';
			AutoCluster.tocNodeActive();
		}
		else if(AutoCluster.currentView == 'evaluation_hosts_credentials' || AutoCluster.currentView == 'clusterSetup')
		{
			$('#setupMethodTable').css('display','');
			$('#searchHostsDiv').css('display','');
			$('#hostsListWithCredentials').css('display', 'none');
			$('#backButton').css('display','none');
			$('#nextButton').css('display','none');
			$('#serachHostTable').css('display','none');
			$('#installerDiv').css('display','none');
			AutoCluster.currentView = 'evaluation_hosts_credentials';
		} 
			
	},
	
	validateFields : function()
	{
		if(AutoCluster.currentView == 'hosts')
		{
			if($('#searchedHostTable tbody input:checkbox:checked').length == 0)
			{
				jAlert('Select host(s) to proceed.', 'Insufficient Details');
				return false;
			}
		}
		else if(AutoCluster.currentView == 'hosts_credentials' )
		{
			var selector = $('#hostsListWithCredentials .required').filter(function(){
								if($(this).val() == '' && $(this).hasClass('password'))
								{
									return $('.authType', $(this).closest('tr')).val() == 'password';
								}
								else if($(this).val() == '' && $(this).hasClass('privateKey'))
								{
									return $('.authType', $(this).closest('tr')).val() == 'privateKey';
								}
								else
								{
									return $(this).val() == '';
								}
							});
			
			if(selector.length > 0)
			{
				var element = selector.eq(0);
				var missingField = null;
				if($(element).hasClass('hostName'))
				{
					missingField = "Hostname/IP";
				}
				else if($(element).hasClass('userName'))
				{
					missingField = "SSH User";
				}
				else if($(element).hasClass('password'))
				{
					missingField = "Password";
				}
				else if($(element).hasClass('port'))
				{
					missingField = "Port";
				}
				else if($(element).hasClass('privateKey'))
				{
					missingField = "Private Key";
				}
				else if($(element).hasClass('installationPath'))
				{
					missingField = "Installation Path";
				}
				else if($(element).hasClass('rackName'))
				{
					missingField = "Rack Name";
				}
				else if($(element).hasClass('agentPort'))
				{
					missingField = "QueryIO Agent Port";
				}
				$(element).focus();
				jAlert(missingField + ' is required');
				
				return false;
			}
		}
		else if(AutoCluster.currentView == 'evaluation_hosts_credentials')
		{
			var selector = $('#searchHostsDiv .required').filter(function(){
								if($(this).val() == '' && $(this).hasClass('password'))
								{
									return $('.authType', $(this).closest('tr')).val() == 'password';
								}
								else if($(this).val() == '' && $(this).hasClass('privateKey'))
								{
									return $('.authType', $(this).closest('tr')).val() == 'privateKey';
								}
								else
								{
									return $(this).val() == '';
								}
							});
			
			if(selector.length > 0)
			{
				var element = selector.eq(0);
				var missingField = null;
				if($(element).hasClass('hostName'))
				{
					missingField = "Host";
				}
				else if($(element).hasClass('userName'))
				{
					missingField = "SSH User";
				}
				else if($(element).hasClass('password'))
				{
					missingField = "Password";
				}
				else if($(element).hasClass('port'))
				{
					missingField = "Port";
				}
				else if($(element).hasClass('privateKey'))
				{
					missingField = "Private Key";
				}
				else if($(element).hasClass('installationPath'))
				{
					missingField = "Installation Path";
				}
				else if($(element).hasClass('rackName'))
				{
					missingField = "Rack Name";
				}
				else if($(element).hasClass('agentPort'))
				{
					missingField = "QueryIO Agent Port";
				}
				$(element).focus();
				jAlert(missingField + ' is required');
				
				return false;
			}
		}
		else if(AutoCluster.currentView == 'hosts_services')
		{
			if($('#hostsTable tbody tr').not('.hostAlreadyInstalled').length > 0)
			{
				jAlert('All the hosts in table must have agent installed. Please retry or remove such hosts.');
				return false;
			}
		}
		else if(AutoCluster.currentView == 'services_install_nodes_selection')
		{
			if($('#cluster_setup_services_table tbody input:checked').length == 0)
			{
				jAlert('Select node(s) to install.', 'Insufficient details');
				return false;
			}
			
			var selector = $('#cluster_setup_services_table input:text').filter(function(){
				return $(this).val() == '';
			});
			
			//	console.log('selector.length : ', selector.length);
			if(selector.length > 0)
			{
				var propName = selector.eq(0).closest('td').prev().html();
				jAlert(propName + ' is required.', 'Insufficient details');
				return false;
			}
		}
		return true;
	},
	
	searchHosts : function()
	{
		if(AutoCluster.currentView == 'evaluation_hosts_credentials')
		{
			if(AutoCluster.isLocalClusterSetupEnabled)
			{
//				RemoteManager.getLocalIP(AutoCluster.getLocalIP);
				RemoteManager.checkHosts("127.0.0.1", AutoCluster.evaluationClusterSetup);
			}
			else
				RemoteManager.checkHosts($('#hostsTable1 .hostName').val(), AutoCluster.evaluationClusterSetup);
		}
		else
		{
			if($('#searchHostsPattern').val() != "")
			{
				$('#searchbackButton').attr('disabled','disabled');
				$('#searchButton').attr('disabled','disabled');
				$('#backButton').css('display','none');
				$('#nextButton').css('display','none');
				$('#processingHost').css('display','');
				$('#searchedHostTableDiv').css('display','none');
				RemoteManager.checkHosts($('#searchHostsPattern').val(), AutoCluster.loadAvailableHostsList);
			}
			else
			{
				$('#warning').css('display','');
				$('#searchbackButton').css('display','');
				$('#backButton').css('display','none');
				$('#nextButton').css('display','none');
				$('#searchedHostTableDiv').css('display','none');
			}
		}
	},
	
//	getLocalIP : function(ip)
//	{
//		if(ip != null)
//		{
//			RemoteManager.checkHosts(ip, AutoCluster.evaluationClusterSetup);
//		}
//	},
	
	loadAvailableHostsList : function(list)
	{
		//
		$('#searchedHostTableDiv').css('display','');
		$('#searchButton').removeAttr('disabled');
		$('#processingHost').css('display','none');
		$('#warning').css('display','none');
		$('#searchbackButton').removeAttr('disabled');
		$('#searchbackButton').css('display','none');
		$('#nextButton').css('display','');
		$('#searchedHostTable').css('display', '');
		$('#backButton').css('display','');
		AutoCluster.hostDetails = list;
		var tableList = new Array();
		
		$('#searchedHostTable').empty();
		
		var foundEligibleHost = false;
		for(var i = 0; i < list.length; i++)
		{
			var rowData = new Array();
			
			rowData.push("<input type = 'checkbox' onclick = 'AutoCluster.selectAllButtonHandler();'" + (AutoCluster.isHostEligibleForAutoSetup(list[i]) ? "checked = 'checked'" : "disabled = 'disabled'") + ">");
			rowData.push("<span class = 'hostIP'>" + list[i].hostIP + "</span>");
			rowData.push(AutoCluster.hostAlreadyAddedWithNodes(list[i]));
			rowData.push(list[i].available ? "Yes" : "No");
			
			tableList.push(rowData);
			
			if(!foundEligibleHost && list[i].available && !list[i].alreadyAdded)
			{
				foundEligibleHost = true;
			}
		}
		
		
		//	console.log('tableList', tableList);
		
	   	$('#searchedHostTable').dataTable( {
   			"bPaginate": false,
			"bLengthChange": false,
			"sPaginationType": "full_numbers",
			"bFilter": false,
			"bDestroy": true,
			"bSort": true,
			"bInfo": false,
			"bDestroy": true,
			"bAutoWidth": false,
			"aaData": tableList,
	        
	        "aoColumns": [
	            { "sTitle": "<input type = 'checkbox' id = 'selectAllHosts' onclick = 'AutoCluster.selectAllHosts();'>" },
	            { "sTitle": "Host IP" },
	            { "sTitle": "Already Added" },
	            { "sTitle": "Available" }
	        ]
	    } );
		
	   	if(list.length > 0)
	   	{
	   		if(foundEligibleHost)
	   		{
	   			$('#selectAllHosts').prop('checked', true);
	   			$('#selectAllHosts').prop('disabled', false);
	   		}
	   		else
	   		{
	   			$('#selectAllHosts').prop('checked', false);
	   			$('#selectAllHosts').prop('disabled', true);
	   		}
	   	}
	   	$('#searchedHostTableDiv').css('display', '');

		
		//
		//	console.log('list :', list);
	},
	
	hostAlreadyAddedWithNodes : function(hostDetail)
	{
		var response = "";
		if(hostDetail.alreadyAdded)
		{
			response = "Yes";
			var nodes = [];
			if(hostDetail.dataNode)
			{
				nodes.push("DataNode");
			}
			if(hostDetail.nameNode)
			{
				nodes.push("NameNode");
			}
			if(hostDetail.resourceManager)
			{
				nodes.push("Resource Manager");
			}
			if(hostDetail.nodeManager)
			{
				nodes.push("Node Manager");
			}
			
			if(nodes.length > 0)
			{
				response += ' (' + nodes.join(', ') + ')';
			}
		}
		else
		{
			response = "No";
		}
		
		return response;
	},
	
	isHostEligibleForAutoSetup : function(hostDetail)
	{
		if(hostDetail.available)
		{
			if(hostDetail.alreadyAdded)
			{
				if(hostDetail.dataNode && hostDetail.nameNode && hostDetail.nodeManager && hostDetail.resourceManager)
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
	},
	
	selectAllHosts : function()
	{
		$('#searchedHostTable tbody input:checkbox').not(':disabled').prop('checked', $('#selectAllHosts').is(':checked'));
	},
	
	selectAllButtonHandler : function()
	{
		if($('#searchedHostTable tbody input:checkbox').not(':disabled').not(':checked').length > 0)
			$('#selectAllHosts').prop('checked', false);
		else
			$('#selectAllHosts').prop('checked', true);
	},
	
	loadView : function(view)
	{
		if(AutoCluster.currentView == view)
		{
			return;
		}
		AutoCluster.currentView = view;
        $("#tabs a.active").removeClass('active');
        $("#" + box_name).addClass("active");
	},
	
	loadRowsMetaData : function()
	{	
		AutoCluster.statusImagesContent = '<div style = "display: table-cell; vertical-align: top; width: 1%;"><span class="image_fail" style="display:none; float: left;"><img src="images/Fail_img.png" style="height: 12pt;"/></span>' + 
											'<span class="image_success" style="display:none; float: left;"><img  src="images/Success_img.png" style="height: 12pt;"/></span>' +
											'<span class="image_processing" style = "display: none; float: left;"><img  src="images/process.gif" style="height: 12pt;"/></span></div>' + 
											'<div class = "errorMessage" style = "margin-left: 10px; width : 90%; text-align: left;"></div>' +
											'<div style = "float: left; margin-left: 10px;"><a class = "retry" style = "display : none; cursor : pointer; text-decoration: underline;">Retry</a>' + 
											'<a href="javascript:Navbar.showServerLog();" style = "display : none; margin-left: 10px;" class = "viewLog">View Log</a></div>';
		
		AutoCluster.hostRow += "<td><input type = 'text' class = 'hostName required'></td>";
		AutoCluster.hostRow += "<td><input type = 'text' class = 'userName required'></td>";
		AutoCluster.hostRow += "<td><select class = 'authType' onchange = 'AutoCluster.passwordPrivateKeyHandler(this);'>";
		AutoCluster.hostRow += "<option value = 'password'>Password</option>";
		AutoCluster.hostRow += "<option value = 'privateKey'>Private Key</option>";
		AutoCluster.hostRow += "</select></td>";
		AutoCluster.hostRow += "<td><input type = 'password' class = 'password required'><input type = 'text' class = 'privateKey required' style = 'display : none;'></td>";
		AutoCluster.hostRow += "<td><input type = 'text' class = 'port required' onkeypress = 'javascript:Util.isNumericWithBlockOthers(event);' value = '22'></td>";
		AutoCluster.hostRow += "<td><input type = 'text' class = 'installationPath required' ></td>";
		AutoCluster.hostRow += "<td><input type = 'text' class = 'rackName required' value = '/default-rack'></td>";
		AutoCluster.hostRow += "<td><input type = 'text' class = 'agentPort required' onkeypress = 'javascript:Util.isNumericWithBlockOthers(event);' value = '6680'></td>";
		AutoCluster.hostRow += "<td style = 'width: 35px;'>";
			AutoCluster.hostRow += "<span onclick = 'javascript:AutoCluster.addHostRow();'><img alt = 'Add Host' src = 'images/plus_sign_brown.png' style = 'height: 13px;'></span>";
			AutoCluster.hostRow += "<span onclick = 'javascript:AutoCluster.removeHostRow(this);'><img alt = 'Remove Host' src = 'images/minus_sign_brown.png' style = 'height: 11px; width: 20px;'></span>";
		AutoCluster.hostRow += "</td>";
		AutoCluster.hostRow += "<td>"
		
		AutoCluster.hostRow += '<span class="image_fail" style="display:none;"><img src="images/Fail_img.png" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>' + 
								'<span class="image_success" style="display:none;"><img  src="images/Success_img.png" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>' +
								'<span class="image_processing" style = "display: none;"><img  src="images/process.gif" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>' + 
								'<div class = "errorMessage" style = "float: left; margin-left: 10px;"></div>' +
								'<div style = "float: right;"><a onclick = "javascript:AutoCluster.retryAddHost(this);" class = "retry" style = "display : none; cursor : pointer; text-decoration: underline;">Retry</a>' + 
								'<a href="javascript:Navbar.showServerLog();" style = "display : none; margin-left: 10px;" class = "viewLog">View Log</a></div>';
		AutoCluster.hostRow += "</td>";
		
		
		
		
		AutoCluster.evaluationHostRow += "<td><input type = 'text'  class = 'hostName required'></td>";
		AutoCluster.evaluationHostRow += "<td><input type = 'text'  class = 'userName required'></td>";
		AutoCluster.evaluationHostRow += "<td><select class = 'authType' onchange = 'AutoCluster.passwordPrivateKeyHandler(this);'>";
		AutoCluster.evaluationHostRow += "<option value = 'password'>Password</option>";
		AutoCluster.evaluationHostRow += "<option value = 'privateKey'>Private Key</option>";
		AutoCluster.evaluationHostRow += "</select></td>";
		AutoCluster.evaluationHostRow += "<td><input  type = 'password' class = 'password required'><input type = 'text' class = 'privateKey required' style = 'display : none;'></td>";
		AutoCluster.evaluationHostRow += "<td><input  type = 'text' class = 'port required' onkeypress = 'javascript:Util.isNumericWithBlockOthers(event);' value = '22'></td>";
		AutoCluster.evaluationHostRow += "<td><input type = 'text' class = 'installationPath required' ></td>";
		AutoCluster.evaluationHostRow += "<td><input type = 'text' class = 'rackName required' value = '/default-rack'></td>";
		AutoCluster.evaluationHostRow += "<td><input type = 'text' class = 'agentPort required' onkeypress = 'javascript:Util.isNumericWithBlockOthers(event);' value = '6680'></td>";
//		AutoCluster.evaluationHostRow += "<td>"
//		
//		AutoCluster.evaluationHostRow += 
//								'<div class = "errorMessage" style = "float: left; margin-left: 10px;"></div>';
//								
//		AutoCluster.evaluationHostRow += "</td>";
		
		
	},
	
	passwordPrivateKeyHandler : function(element)
	{
		//	console.log('onchange event called ');
		//	console.log('$(element).val()  : ', $(element).val());
		if($(element).val() == 'password')
		{
			$('.password', $(element).closest('tr')).css('display', '');
			$('.privateKey', $(element).closest('tr')).css('display', 'none');
		}
		else
		{
			$('.privateKey', $(element).closest('tr')).css('display', '');
			$('.password', $(element).closest('tr')).css('display', 'none');
		}
	},
	
	loadHostTab : function()
	{
		var tableContent;
		tableContent = "<table id = 'hostsTable' class = 'dataTable'>";
			tableContent += "<thead>";
				tableContent += "<tr style = 'height: 25px;'>";
					tableContent += "<th style = 'width: 150px;'>HostName / IP</th>";
					tableContent += "<th style = 'width: 150px;'>SSH User</th>";
					tableContent += "<th style = 'width: 100px;'>Authentication Method</th>";
					tableContent += "<th style = 'width: 150px;'>Password / Private Key</th>";
					tableContent += "<th style = 'width: 50px;'>SSH Port</th>";
					tableContent += "<th style = 'width: 120px;'>Installation Path</th>";
					tableContent += "<th style = 'width: 110px;'>Rack Name</th>";
					tableContent += "<th style = 'width: 100px;'>QueryIO Agent Port</th>";
					tableContent += "<th style = 'width: 35px;'></th>";
					tableContent += "<th>Status</th>";
				tableContent += "</tr>";
			tableContent += "</thead>";
			tableContent += "<tbody>";
				tableContent += "<tr id = 'host_1'>";
				tableContent += AutoCluster.hostRow;
				tableContent += "</tr>";
			tableContent += "</tbody>";

		$("#hostsListWithCredentials").html(tableContent);
		AutoCluster.totalHosts ++;
	},
	
	addHostRow : function()
	{
		var tableContent = "<tr id = 'host_" + AutoCluster.totalHosts + "'>";
		tableContent += AutoCluster.hostRow;
		tableContent += "</tr>";
		$("#hostsTable tbody").append(tableContent);
		AutoCluster.totalHosts ++;
//		//	console.log('element 2 :', $(element).val());
	},
	
	removeHostRow : function(element)
	{
		if($("#hostsTable tbody tr").length > 1)
		{
			var index = AutoCluster.addedHosts.indexOf($('.hostName', $(element).closest('tr')).val());
			if($('.image_processing', $(element).closest('tr')).is(':visible'))
			{
				jAlert('Agent is being added to this host. Please wait till processing is completed.');
				return;
				
			}
			if(index > -1)
			{
				AutoCluster.addedHosts.splice(index, 1);
			}
			$('hostName', $(element).closest('tr')).val();
			$(element).closest('tr').remove();
		}
	},
	
	retryAddHost : function(element)
	{
		$('#totalProcessing').text('1');
		$('#successProcessing').text('0');
		$('#failureProcessing').text('0');
		
		AutoCluster.saveHost($(element).closest('tr'));
	},
	
	retryAllFailedAddHost : function()
	{
		if($("#hostsTable tbody .retry:visible").not('.hostAlreadyInstalled').length == 0)
		{
			jAlert('Agent already installed.');
			return;
		}
		$(".retryAllFailed").css('display', 'none');
		
		$('#totalProcessing').text($("#hostsTable tbody .retry:visible").not('.hostAlreadyInstalled').length);
		$('#successProcessing').text('0');
		$('#failureProcessing').text('0');
		
		$("#hostsTable tbody .retry:visible").not('.hostAlreadyInstalled').each(function(){
			AutoCluster.saveHost($(this).closest('tr'));
		});
	},
	
	saveAllHosts : function()
	{
		$('#progressStatus').css('visibility', 'visible');
		$('#totalProcessing').text($("#hostsTable tbody tr").not('.hostAlreadyInstalled').length);
		$('#successProcessing').text('0');
		$('#failureProcessing').text('0');
		
		AutoCluster.hostsArray = [];
		
		$("#hostsTable tbody tr").not('.hostAlreadyInstalled').each(function(){
			AutoCluster.saveHost($(this));
		});
	},
	
	
	loadSelectedHosts : function()
	{
		//	add rows to fit hosts in
		var obj = $('#searchedHostTable tbody input:checkbox').filter(':checked');
		for(var i = 0; i < obj.length - 1; i++)
		{
			AutoCluster.addHostRow();
		}
		var hostIP;
		obj.each(function(index){
			hostIP = $('.hostIP', $(this).closest('tr')).html();
			var row = $('#hostsTable tbody tr').eq(index);
			$('.hostName', row).val(hostIP);
			if(AutoCluster.isAgentAlreadyInstalled(hostIP))
			{
				row.addClass('hostAlreadyInstalled');
				$('.errorMessage', row).html('Agent already installed.');
				if($.inArray(hostIP, AutoCluster.addedHosts) < 0)
					AutoCluster.addedHosts.push(hostIP);
			}
		});
	},
	
	isAgentAlreadyInstalled : function(hostIP)
	{
		var isAreadyInstalled = false;
		for(var i = 0; i < AutoCluster.hostDetails.length; i++)
		{
			if(AutoCluster.hostDetails[i].hostIP == hostIP)
			{
				isAreadyInstalled = AutoCluster.hostDetails[i].alreadyAdded;
				break;
			}
		}
		return isAreadyInstalled;
	},
	
	saveHost : function(host)
	{
		$('#nextButton').prop('disabled', true);
		$('#backButton').prop('disabled', true);
		
		var userName = $('.userName', host).val();
		
		var password = $('.password', host).val();
		
		var port = $('.port', host).val();
		
		var sshPrivateKeyFile = $('.privateKey', host).val();
		
		var rackName = $('.rackName', host).val();

		var agentPort = $('.agentPort', host).val();
		
		var installationPath = $('.installationPath', host).val();
			
		$('.image_processing', host).css('display', '');
		$('.image_success', host).css('display', 'none');
		$('.image_fail', host).css('display', 'none');
		$('.retry', host).css('display', 'none');
		$('.viewLog', host).css('display', 'none');
		$('.errorMessage', host).empty();
		
		var authType = $('.authType', host).val();
		
		if(authType == 'password')
		{
			sshPrivateKeyFile = null;
		}
		else
		{
			password = null;
		}
		//	console.log('sshPrivateKeyFile  :', sshPrivateKeyFile);
		//	console.log('password : ', password);
		RemoteManager.insertHostAutomatation($('.hostName', host).val(), userName,  password, sshPrivateKeyFile,  
				rackName, port, installationPath, agentPort, host[0].id, AutoCluster.hostSaved);
	},
	
	hostSaved : function(response)
	{
//		console.log('response', response);
		$('.image_processing', $('#' + response.id)).css('display', 'none');
		$('.errorMessage', $('#' + response.id)).html(response.responseMessage);
		if(response.taskSuccess || response.responseCode == 302)
		{
			if(response.responseCode == 302)
			{
				$('.errorMessage', $('#' + response.id)).html('Agent already installed.');
			}
			
			$('#' + response.id).addClass('hostAlreadyInstalled');
			AutoCluster.hostsArray.push(response.id);
			$('.image_success', $('#' + response.id)).css('display', '');
			if($("#hostsTable tbody .retry:visible").length == 0)
				$(".retryAllFailed").css('display', 'none');
			AutoCluster.addedHosts.push($('.hostName', $('#' + response.id)).val());
			var successCount = parseInt($('#successProcessing').text());
			successCount ++;
			$('#successProcessing').text(successCount);
		}
		else
		{
			$('.image_fail', $('#' + response.id)).css('display', '');
			$('.retry', $('#' + response.id)).css('display', '');
			$('.viewLog', $('#' + response.id)).css('display', '');
			$(".retryAllFailed").css('display', '');
			
			var failureCount = parseInt($('#failureProcessing').text());
			failureCount ++;
			$('#failureProcessing').text(failureCount);
			
			// Do not commit
//			if($.inArray($('.hostName', $('#' + response.id)).val(), AutoCluster.addedHosts) < 0)
//				AutoCluster.addedHosts.push($('.hostName', $('#' + response.id)).val());
			//
		}
		
		if($('#hostsTable .image_processing :visible').length == 0)
		{
			$('#nextButton').prop('disabled', false);
			$('#backButton').prop('disabled', false);
		}
		
		//	console.log('AutoCluster.hostsDataArray : ', AutoCluster.hostsArray);
	},
	
	setupServices : function()
	{
		AutoCluster.loadAllPredefinedConfigs();
//		AutoCluster.dataNodeGetConfigurationPorts();
	},
	
	loadAllPredefinedConfigs : function()
	{
		AutoCluster.dataNodeGetConfigurationPorts();
		AutoCluster.nameNodeGetConfigurationPorts();
		AutoCluster.resourceManagerGetConfigurationPorts();
		AutoCluster.nodeManagerGetConfigurationPorts();
	},
	
	configurationLoadingHandler : function()
	{
		AutoCluster.totalConfigurationsRecieved ++;
		if(AutoCluster.totalConfigurationsRecieved == AutoCluster.totalConfigsToBeRcvd)
		{
				AutoCluster.drawServicesTable();
		}
	},
	
	drawServicesTable : function()
	{
		//	console.log('addedHosts : ', AutoCluster.addedHosts);
		$('#cluster_setup_services_table tbody').empty();
		var tableContent = "";
		var isNameNodePresent = false;
		var isResourceManagerPresent = false;
		for(var i = 0; i < AutoCluster.addedHosts.length; i++)
		{
			tableContent = "<tr id = 'treeTableHost_" + i + "'>";
			tableContent += "<td class = 'selected_hosts'>" + AutoCluster.addedHosts[i] + "</td>";
			if(AutoCluster.isNodeAlreadyAdded(AutoCluster.addedHosts[i], 'nameNode') == false)
			{
				tableContent += "<td>" + "<input type = 'radio' class = 'nameNode' name = 'nameNode' onclick = 'javascript:AutoCluster.removeSelectionSpanHandler(\"nameNode\");'>" + "</td>";
			}
			else
			{
				tableContent += "<td class = 'nameNodeAlreadyInstalled'>Already installed</td>";
				AutoCluster.installed_nn.push(i);
				isNameNodePresent = true;
			}
			if(AutoCluster.isNodeAlreadyAdded(AutoCluster.addedHosts[i], 'dataNode') == false)
			{
				tableContent += "<td>" + "<input type = 'checkbox' class = 'dataNode' onclick = 'javascript:AutoCluster.selectAllServicesHandler(\"dataNode\");'>" + "</td>";
			}
			else
			{
				tableContent += "<td>Already installed</td>";
				AutoCluster.installed_dn.push(i);
			}
			if(AutoCluster.isNodeAlreadyAdded(AutoCluster.addedHosts[i], 'resourceManager') == false)
			{
				tableContent += "<td>" + "<input type = 'radio' class = 'resourceManager' name = 'resourceManager' onclick = 'javascript:AutoCluster.removeSelectionSpanHandler(\"resourceManager\");'>" + "</td>";
			}
			else
			{
				tableContent += "<td class = 'resourceManagerAlreadyInstalled'>Already installed</td>";
				isResourceManagerPresent = true;
				AutoCluster.installed_rm.push(i);
			}
			if(AutoCluster.isNodeAlreadyAdded(AutoCluster.addedHosts[i], 'nodeManager') == false)
			{
				tableContent += "<td>" + "<input type = 'checkbox' class = 'nodeManager' name = 'nodeManager' onclick = 'javascript:AutoCluster.selectAllServicesHandler(\"nodeManager\");AutoCluster.checkIfRMSelected();'>" + "</td>";
			}
			else
			{
				tableContent += "<td>Already installed</td>";
				AutoCluster.installed_nm.push(i);
			}
			tableContent += "</tr>";
			
//			$('#cluster_setup_services_table tbody').append(tableContent);
			
			tableContent += "<tr class = 'child-of-treeTableHost_" + i + "'>";
			tableContent += "<td></td>";
			tableContent += "<td class = 'nameNodeConfig'><table><tbody>";
			tableContent += AutoCluster.nameNodePortsHTMLContent;
			tableContent += AutoCluster.getDiskAndVolumeContentHTML('nameNode');
			tableContent += AutoCluster.getDatabaseContentHTML('nameNode');
			tableContent += "</tbody></table></td>";
			tableContent += "<td class = 'dataNodeConfig'><table><tbody>";
			tableContent += AutoCluster.dataNodePortsHTMLContent;
			tableContent += AutoCluster.getDiskAndVolumeContentHTML('dataNode');
			tableContent += "</tbody></table></td>";
			tableContent += "<td class = 'RMConfig'><table><tbody></td>";
			tableContent += AutoCluster.resourceManagerPortsHTMLContent;
			tableContent += AutoCluster.getVolumeContentHTML('RM');
			tableContent += "</tbody></table></td>";
			tableContent += "<td class = 'NMConfig'><table><tbody></td>";
			tableContent += AutoCluster.nodeManagerPortsHTMLContent;
			tableContent += AutoCluster.getVolumeContentHTML('NM');
			tableContent += "</tbody></table></td>";
			
			$('#cluster_setup_services_table tbody:first').append(tableContent);
			
			RemoteManager.getPhysicalDiskNamesAutomation(AutoCluster.addedHosts[i], "" + i, AutoCluster.fillDisks);
			RemoteManager.getUserHomeDirectoryPathForHostAutomation(AutoCluster.addedHosts[i], "" + i, AutoCluster.fillVolumePath);
			
			RemoteManager.getAllDbNamesAutomation(AutoCluster.addedHosts[i], "" + i, AutoCluster.fillDBNames);
//			RemoteManager.getAdhocDbNamesAutomation(AutoCluster.addedHosts[i], "" + i, AutoCluster.fillAdhocDb);
			
		}
		
		RemoteManager.getAllNodesCount(AutoCluster.fillNodesIds);
		
		if(isNameNodePresent)
		{
			$('#cluster_setup_services_table tbody:first input:radio.nameNode').prop('disabled', true);
		}
		if(isResourceManagerPresent)
		{
			$('#cluster_setup_services_table tbody:first input:radio.resourceManager').prop('disabled', true);
		}
		AutoCluster.disableAllConfigTables();
		
		$("#cluster_setup_services_table").treeTable({
			clickableNodeNames : true
		});
	},
	
	fillNodesIds : function(response)
	{
		//	console.log('response for nodes count', response);
		var count = parseInt(response.namenode);
		$('#cluster_setup_services_table input:text.nameNode_id').each(function(){
			$(this).val('NameNode' + (count + 1));	// Since NameNode can be single in cluster so repeat nameNode ID to all the host.
		});
		
		count = parseInt(response.datanode);
		$('#cluster_setup_services_table input:text.dataNode_id').each(function(index){
			$(this).val('DataNode' + (count + 1 + index));
		});
		
		count = parseInt(response.resourcemanager);
		$('#cluster_setup_services_table input:text.RM_id').each(function(){
			$(this).val('ResourceManager' + (count + 1));	// Since ResourceManager can be single in cluster so repeat ResourceManager ID to all the host.
		});
		
		count = parseInt(response.nodemanager);
		$('#cluster_setup_services_table input:text.NM_id').each(function(index){
			$(this).val('NodeManager' + (count + 1 + index));
		});
		
	},
	
	isNodeAlreadyAdded : function(host, nodeType)
	{
		//	console.log('checkEligibilityForNodeInstall host : ', host);
		//	console.log('nodeType : ', nodeType);
		//	console.log('AutoCluster.hostDetails isNodeAlreadyAdded : ', AutoCluster.hostDetails);

		var isEligible = false;
		for(var i = 0; i < AutoCluster.hostDetails.length; i++)
		{
			if(AutoCluster.hostDetails[i].hostIP == host)
			{
				//	console.log('AutoCluster.hostDetails[i].nodeType  :', AutoCluster.hostDetails[i][nodeType]);
				isEligible = AutoCluster.hostDetails[i][nodeType];
				break;
			}
		}
		//	console.log('isEligible : ', isEligible);
		return isEligible;
	},
	
	addNodeToHostDetails : function(host, nodeType)
	{
		//	console.log('host : ', host);
		//	console.log('nodeType : ', nodeType);
		//	console.log('AutoCluster.hostDetails : ', AutoCluster.hostDetails);
		for(var i = 0; i < AutoCluster.hostDetails.length; i++)
		{
			if(AutoCluster.hostDetails[i].hostIP == host)
			{
				//	console.log('AutoCluster.hostDetails[i][nodeType] before: ', AutoCluster.hostDetails[i][nodeType]);
				AutoCluster.hostDetails[i][nodeType] = true;
				//	console.log('AutoCluster.hostDetails[i][nodeType] after: ', AutoCluster.hostDetails[i][nodeType]);
				break;
			}
		}
	},
	
	getDiskAndVolumeContentHTML : function(type)
	{
		var tableContent = "<tr>";
		tableContent += "<td>Disk</td>";
		tableContent += "<td class = '" + type + "DiskRow'></td>";
		tableContent += "</tr>";
		tableContent += "<tr>";
		tableContent += "<td>Directory Path</td>";
		tableContent += "<td class = '" + type + "VolumePathRow'></td>";
		tableContent += "</tr>";
		
		return tableContent;
	},
	
	getDatabaseContentHTML : function(type)
	{
		var tableContent = "<tr>";
		tableContent += "<td>Metadata Database</td>";
		tableContent += "<td class = '" + type + "MetaStoreRow'></td>";
		tableContent += "</tr>";
		tableContent += "<tr>";
		tableContent += "<td>Hive Schema</td>";
		tableContent += "<td class = '" + type + "AdhocDbRow'></td>";
		tableContent += "</tr>";
		
		return tableContent;
	},
	
	getVolumeContentHTML : function(type)
	{
		var tableContent = "<tr>";
		tableContent += "<td>Directory Path</td>";
		tableContent += "<td class = '" + type + "VolumePathRow'></td>";
		tableContent += "</tr>";
		
		return tableContent;
	},
	
	fillDisks : function(response)
	{
		//	console.log('disks response : ', response);
		var options = "";
		if(response.disks != null)
		{
			for(var i = 0; i < response.disks.length; i++)
			{
				options += "<option value = '" + response.disks[i] + "'>" + response.disks[i] + "</option>";
			}
		}
		else
		{
			options = "<option value = 'unavailable'>No Disk</option>";
		}

		$(".child-of-treeTableHost_" + response.id + " .dataNodeDiskRow").html("<select class = 'dataNodeDisk' disabled = 'disabled'>" + options + "</select>");
		$(".child-of-treeTableHost_" + response.id + " .nameNodeDiskRow").html("<select class = 'nameNodeDisk' disabled = 'disabled'>" + options + "</select>");
		AutoCluster.hideAlreadyInstalledTables();
	},
	
	fillDBNames : function(response)
	{
		console.log('fillDBNames response : ', response);
		var options = "";
		if(response.MetaStore != null)
		{
			for(var i = 0; i < response.MetaStore.length; i++)
			{
				options += "<option value = '" + response.MetaStore[i] + "'>" + response.MetaStore[i] + "</option>";
			}
		}
		options += "<option value ='none'>None</option>";
		$(".child-of-treeTableHost_" + response.id + " .nameNodeMetaStoreRow").html("<select class = 'nameNodeMetaStore' disabled = 'disabled'>" + options + "</select>");

//		console.log('adhoc response : ', response);
		var options = "";
		if(response.adhocDb != null)
		{
			for(var i = 0; i < response.adhocDb.length; i++)
			{
				options += "<option value = '" + response.adhocDb[i] + "'>" + response.adhocDb[i] + "</option>";
			}
		}
		options += "<option value ='none'>None</option>";
		$(".child-of-treeTableHost_" + response.id + " .nameNodeAdhocDbRow").html("<select class = 'nameNodeAdhocDb' disabled = 'disabled'>" + options + "</select>");
		
	},
	
	fillVolumePath : function(response)
	{
		//	console.log('volume response', response);
		var classPrefix = ['dataNode', 'nameNode', 'RM', 'NM'];
		
		var volumePath = "";
		if(response.responseMessage != null)
		{
			volumePath = response.responseMessage;
		}
		for(var i = 0; i < classPrefix.length; i++)
		{
			var content = "<input type = 'text' class = '" + classPrefix[i] + "VolumePath' value = '" + volumePath + "'>";
			$(".child-of-treeTableHost_" + response.id + " ." + classPrefix[i] + "VolumePathRow").html(content);
		}
	},
	
	nameNodeGetConfigurationPorts : function()
	{
		var keyList = new Array();
		keyList.push("dfs.namenode.rpc-address");
		keyList.push("dfs.namenode.http-address");
		keyList.push("dfs.namenode.https-address");
		keyList.push("queryio.s3server.port");
		keyList.push("queryio.s3server.ssl.port");
		keyList.push("queryio.ftpserver.port");
		keyList.push("queryio.hdfsoverftp.port");
		keyList.push("queryio.ftpserver.ssl.port");
		keyList.push("queryio.namenode.options");
		
		RemoteManager.getConfigurationServerPort(keyList, AutoCluster.parseNameNodeConfigurationPorts);
	},
	
	dataNodeGetConfigurationPorts : function()
	{
		var keyList = new Array();
		keyList.push("dfs.datanode.address");
		keyList.push("dfs.datanode.https.address");
		keyList.push("dfs.datanode.http.address");
		keyList.push("dfs.datanode.ipc.address");
		keyList.push("queryio.datanode.options");
		
		RemoteManager.getConfigurationServerPort(keyList, AutoCluster.parseDataNodeConfigurationPorts);
	},
	
	resourceManagerGetConfigurationPorts : function()
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
		
		RemoteManager.getConfigurationServerPort(keyList, AutoCluster.parseResourceManagerConfigurationPorts);
	},
	
	nodeManagerGetConfigurationPorts : function()
	{
		var keyList = new Array();
		keyList.push("yarn.nodemanager.localizer.address");
		keyList.push("yarn.nodemanager.webapp.address");
		keyList.push("queryio.nodemanager.options");
		
		RemoteManager.getConfigurationServerPort(keyList, AutoCluster.parseNodeManagerConfigurationPorts);
	},
	
	evaluationConfigHandler : function()
	{
		AutoCluster.evaluationConfigRcvd ++;
		if(AutoCluster.evaluationTotalConfigCount == AutoCluster.evaluationConfigRcvd)
		{
			AutoCluster.installNameNodeEvaluation();
			AutoCluster.installResourceManagerEvaluation();
		}
	},
	
	parseNodeManagerConfigurationPorts : function(map)
	{
		var localizer = map["yarn.nodemanager.localizer.address"]["value"];
		localizer = localizer.substring(localizer.indexOf(':')+1).trim();
		
		var webapp = map["yarn.nodemanager.webapp.address"]["value"];
		webapp = webapp.substring(webapp.indexOf(':')+1).trim();
		
		var jmx = map["queryio.nodemanager.options"]["value"];
		jmx = jmx.substring(jmx.indexOf('jmxremote.port=')+1);
		jmx = jmx.substring(jmx.indexOf('=')+1,jmx.indexOf('-Dcom.sun.management')).trim();
		
		if(AutoCluster.isEvaluation)
		{
			AutoCluster.nmConfig = new Object();
			AutoCluster.nmConfig["localizer"] = localizer;
			AutoCluster.nmConfig["webapp"] = webapp;
			AutoCluster.nmConfig["jmx"] = jmx;
			AutoCluster.evaluationConfigHandler();
		}
		else
		{		
			AutoCluster.generateNodeManagerTableContent(localizer, webapp, jmx);
			AutoCluster.configurationLoadingHandler();
		}
	},
	
	parseResourceManagerConfigurationPorts : function(map)
	{
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
		
		
		if(AutoCluster.isEvaluation)
		{
			AutoCluster.rmConfig = new Object();
			AutoCluster.rmConfig["serverPort"] = serverPort;
			AutoCluster.rmConfig["scheduler"] = scheduler;
			AutoCluster.rmConfig["webapp"] = webapp;
			AutoCluster.rmConfig["adminPort"] = adminPort;
			AutoCluster.rmConfig["tracker"] = tracker;
			AutoCluster.rmConfig["hostoryPort"] = hostoryPort;
			AutoCluster.rmConfig["webhostoryPort"] = webhostoryPort;
			AutoCluster.rmConfig["jmx"] = jmx;
			AutoCluster.evaluationConfigHandler();
		}
		else
		{
			AutoCluster.generateResourceManagerTableContent(serverPort, scheduler, webapp, adminPort, tracker, hostoryPort, webhostoryPort, jmx);
			AutoCluster.configurationLoadingHandler();
		}
	},
	
	parseNameNodeConfigurationPorts : function(map)
	{
		var serverPort = map["dfs.namenode.rpc-address"]["value"];
		serverPort = serverPort.substring(serverPort.indexOf(':')+1).trim();
		
		var http = map["dfs.namenode.http-address"]["value"];
		http = http.substring(http.indexOf(':')+1).trim();
		
		var https = map["dfs.namenode.https-address"]["value"];
		https = https.substring(https.indexOf(':')+1).trim();

		var s3serverPort = map["queryio.s3server.port"]["value"];
		
		var s3SslserverPort = map["queryio.s3server.ssl.port"]["value"];
		
		var hdfsOverFtpPort = map["queryio.hdfsoverftp.port"]["value"];
		
		var ftpServerPort = map["queryio.ftpserver.port"]["value"];
		
		var ftpSslServerPort = map["queryio.ftpserver.ssl.port"]["value"];
		
		var jmx = map["queryio.namenode.options"]["value"];
		jmx = jmx.substring(jmx.indexOf('jmxremote.port=')+1);
		jmx = jmx.substring(jmx.indexOf('=')+1,jmx.indexOf('-Dcom.sun.management')).trim();
		
		if(AutoCluster.isEvaluation)
		{
			AutoCluster.nameNodeConfig = new Object();
			AutoCluster.nameNodeConfig["serverPort"] = serverPort;
			AutoCluster.nameNodeConfig["http"] = http;
			AutoCluster.nameNodeConfig["https"] = https;
			AutoCluster.nameNodeConfig["s3serverPort"] = s3serverPort;
			AutoCluster.nameNodeConfig["s3SslserverPort"] = s3SslserverPort;
			AutoCluster.nameNodeConfig["hdfsOverFtpPort"] = hdfsOverFtpPort;
			AutoCluster.nameNodeConfig["ftpServerPort"] = ftpServerPort;
			AutoCluster.nameNodeConfig["ftpSslServerPort"] = ftpSslServerPort;
			AutoCluster.nameNodeConfig["jmx"] = jmx;
			AutoCluster.evaluationConfigHandler();
		}
		else
		{
			AutoCluster.generateNameNodeTableContent(serverPort, https, http, s3serverPort, s3SslserverPort, hdfsOverFtpPort, ftpServerPort, ftpSslServerPort, jmx);
			AutoCluster.configurationLoadingHandler();
		}
	},
	
	parseDataNodeConfigurationPorts : function(map)
	{
		var serverPort = map["dfs.datanode.address"]["value"];
		serverPort = serverPort.substring(serverPort.indexOf(':')+1).trim();
		
		var https = map["dfs.datanode.https.address"]["value"];
		https = https.substring(https.indexOf(':')+1);
		
		var http = map["dfs.datanode.http.address"]["value"];
		http = http.substring(http.indexOf(':')+1);
		
		var ipc = map["dfs.datanode.ipc.address"]["value"];
		ipc = ipc.substring(ipc.indexOf(':')+1);
		
		var jmx = map["queryio.datanode.options"]["value"];
		jmx = jmx.substring(jmx.indexOf('jmxremote.port=')+1);
		jmx = jmx.substring(jmx.indexOf('=')+1,jmx.indexOf('-Dcom.sun.management')).trim();
		
		if(AutoCluster.isEvaluation)
		{
			AutoCluster.dataNodeConfig = new Object();
			AutoCluster.dataNodeConfig["serverPort"] = serverPort;
			AutoCluster.dataNodeConfig["http"] = http;
			AutoCluster.dataNodeConfig["https"] = https;
			AutoCluster.dataNodeConfig["ipc"] = ipc;
			AutoCluster.dataNodeConfig["jmx"] = jmx;
			AutoCluster.evaluationConfigHandler(); 
		}
		else
		{
			AutoCluster.generateDataNodeTableContent(serverPort, https, http, ipc, jmx);
			AutoCluster.configurationLoadingHandler();
		}
	},
	
	generateDataNodeTableContent : function(serverPort, https, http, ipc, jmx)
	{
		var tableContent = "<tr><td>DataNode ID</td>";
		tableContent += "<td><input type = 'text' class = 'dataNode_id'></td></tr>";
		
		tableContent += "<tr><td>Server Port</td>";
		tableContent += "<td><input type = 'text' class = 'dataNode_server' value = '" + serverPort + "'></td></tr>";
		
		tableContent += "<tr><td>HTTPS Port</td>";
		tableContent += "<td><input type = 'text' class = 'dataNode_https' value = '" + https + "'></td></tr>";
		
		tableContent += "<tr><td>HTTP Port</td>";
		tableContent += "<td><input type = 'text' class = 'dataNode_http' value = '" + http + "'></td></tr>";
		
		tableContent += "<tr><td>IPC Port</td>";
		tableContent += "<td><input type = 'text' class = 'dataNode_ipc' value = '" + ipc + "'></td></tr>";
		
		tableContent += "<tr><td>JMX Port</td>";
		tableContent += "<td><input type = 'text' class = 'dataNode_jmx' value = '" + jmx + "'></td></tr>";
		
		AutoCluster.dataNodePortsHTMLContent = tableContent;
		
	},
	
	generateNameNodeTableContent : function(serverPort, https, http, s3serverPort, s3SslserverPort, hdfsOverFtpPort, ftpServerPort, ftpSslServerPort, jmx)
	{
		var tableContent = "<tr><td>NameNode ID</td>";
		tableContent += "<td><input type = 'text' class = 'nameNode_id'></td></tr>";
		
		tableContent += "<tr><td>Server Port</td>";
		tableContent += "<td><input type = 'text' class = 'nameNode_server' value = '" + serverPort + "'></td></tr>";
		
		tableContent += "<tr><td>HTTPS Port</td>";
		tableContent += "<td><input type = 'text' class = 'nameNode_https' value = '" + https + "'></td></tr>";
		
		tableContent += "<tr><td>HTTP Port</td>";
		tableContent += "<td><input type = 'text' class = 'nameNode_http' value = '" + http + "'></td></tr>";
		
		tableContent += "<tr><td>S3 Server Port</td>";
		tableContent += "<td><input type = 'text' class = 'nameNode_s3Server' value = '" + s3serverPort + "'></td></tr>";
		
		tableContent += "<tr><td>S3 SSL Server Port</td>";
		tableContent += "<td><input type = 'text' class = 'nameNode_s3SSLServer' value = '" + s3SslserverPort + "'></td></tr>";
		
		tableContent += "<tr><td>HDFS Over FTP Port</td>";
		tableContent += "<td><input type = 'text' class = 'nameNode_hdfsOverFTP' value = '" + hdfsOverFtpPort + "'></td></tr>";
		
		tableContent += "<tr><td>FTP Server Port</td>";
		tableContent += "<td><input type = 'text' class = 'nameNode_ftpServer' value = '" + ftpServerPort + "'></td></tr>";
		
		tableContent += "<tr><td>FTP SSL Port</td>";
		tableContent += "<td><input type = 'text' class = 'nameNode_ftpSSLServer' value = '" + ftpSslServerPort + "'></td></tr>";
		
		tableContent += "<tr><td>JMX Port</td>";
		tableContent += "<td><input type = 'text' class = 'nameNode_jmx' value = '" + jmx + "'></td></tr>";
		
		AutoCluster.nameNodePortsHTMLContent = tableContent;
		
	},
	
	generateResourceManagerTableContent : function(serverPort, scheduler, webapp, adminPort, tracker, hostoryPort, webhostoryPort, jmx)
	{
		var tableContent = "<tr><td>Resource Manager ID</td>";
		tableContent += "<td><input type = 'text' class = 'RM_id'></td></tr>";
		
		tableContent += "<tr><td>Server Port</td>";
		tableContent += "<td><input type = 'text' class = 'RM_server' value = '" + serverPort + "'></td></tr>";

		tableContent += "<tr><td>Scheduler</td>";
		tableContent += "<td><input type = 'text' class = 'RM_scheduler' value = '" + scheduler + "'></td></tr>";
		
		tableContent += "<tr><td>WebApp</td>";
		tableContent += "<td><input type = 'text' class = 'RM_webapp' value = '" + webapp + "'></td></tr>";
		
		tableContent += "<tr><td>Admin</td>";
		tableContent += "<td><input type = 'text' class = 'RM_adminPort' value = '" + adminPort + "'></td></tr>";
		
		tableContent += "<tr><td>ResourceTracker</td>";
		tableContent += "<td><input type = 'text' class = 'RM_tracker' value = '" + tracker + "'></td></tr>";
		
		tableContent += "<tr><td>JMX</td>";
		tableContent += "<td><input type = 'text' class = 'RM_jmx' value = '" + jmx + "'></td></tr>";
		
		tableContent += "<tr><th colspan = '2'>Job History Ports</th></tr>";
		
		tableContent += "<tr><td>Server</td>";
		tableContent += "<td><input type = 'text' class = 'RM_historyPort' value = '" + hostoryPort + "'></td></tr>";
		
		tableContent += "<tr><td>WebApp</td>";
		tableContent += "<td><input type = 'text' class = 'RM_webhostoryPort' value = '" + webhostoryPort + "'></td></tr>";
		
		AutoCluster.resourceManagerPortsHTMLContent = tableContent;
	},
	
	generateNodeManagerTableContent : function(localizer, webapp, jmx)
	{		
		var tableContent = "<tr><td>Node Manager ID</td>";
		tableContent += "<td><input type = 'text' class = 'NM_id'></td></tr>";
		
		tableContent += "<tr><td>Localizer</td>";
		tableContent += "<td><input type = 'text' class = 'NM_localizer' value = '" + localizer + "'></td></tr>";
		
		tableContent += "<tr><td>WebApp</td>";
		tableContent += "<td><input type = 'text' class = 'NM_webApp' value = '" + webapp + "'></td></tr>";
		
		tableContent += "<tr><td>JMX</td>";
		tableContent += "<td><input type = 'text' class = 'NM_jmx' value = '" + jmx + "'></td></tr>";
		
		AutoCluster.nodeManagerPortsHTMLContent = tableContent;
	},
	
	removeSelectionSpanHandler : function(className)
	{
		AutoCluster.enableDisableConfigTable(className);
		$('#removeSelection_' + className).css('visibility', 'visible');
	},
	
	unselectRadio : function(className)
	{
		if(className == 'resourceManager')
		{
			AutoCluster.unselectCheckbox('nodeManager');
		}
		else if(className == 'nameNode')
		{
			AutoCluster.unselectCheckbox('dataNode');
		}
		$('#cluster_setup_services_table tbody input:radio[name=' + className + ']').prop('checked', false);
		$('#removeSelection_' + className).css('visibility', 'hidden');
		AutoCluster.enableDisableConfigTable(className);
	},
	
	unselectCheckbox : function(className)
	{
		$('#cluster_setup_services_table tbody input:checkbox.' + className).prop('checked', false);
		$('#selectAll_' + className).prop('checked', false);
	},
	
	selectAllServices : function(type)
	{
		var isConflict = false;
		isConflict = AutoCluster.checkDependency(type);
		
		if(isConflict == false)
		{
			$('#cluster_setup_services_table tbody input:checkbox.' + type).prop('checked', $('#selectAll_' + type).is(':checked'));
			AutoCluster.enableDisableConfigTable(type);
		}
	},
	
	selectAllServicesHandler : function(type)
	{
		var isConflict = false;
		isConflict = AutoCluster.checkDependency(type);

		if(isConflict == false)
		{
			AutoCluster.enableDisableConfigTable(type);
			if($('#cluster_setup_services_table tbody input.' + type).not(':checked').length > 0)
				$('#selectAll_' + type).prop('checked', false);
			else
				$('#selectAll_' + type).prop('checked', true);
		}
	},
	
	enableDisableConfigTable : function(type)
	{
		$('#cluster_setup_services_table tbody input.' + type).each(function(){
			var disableConfig =  ! $(this).attr('checked');
			var configRow = $(this).closest('tr').next();
			//	console.log('disableConfig  : ', disableConfig);
			//	console.log('configRow : ', configRow);
			//	console.log('type : ', type);
			if(type == 'dataNode')
			{
				$('.dataNodeConfig input', configRow).prop('disabled', disableConfig);
				$('.dataNodeDisk', configRow).prop('disabled', disableConfig);
			}
			else if(type == 'nameNode')
			{
				$('.nameNodeConfig input', configRow).prop('disabled', disableConfig);
				$('.nameNodeDisk', configRow).prop('disabled', disableConfig);
				$('.nameNodeMetaStore', configRow).prop('disabled', disableConfig);
				$('.nameNodeAdhocDb', configRow).prop('disabled', disableConfig);
			}
			else if(type == 'nodeManager')
			{
				$('.NMConfig input', configRow).prop('disabled', disableConfig);
			}
			else if(type == 'resourceManager')
			{
				$('.RMConfig input', configRow).prop('disabled', disableConfig);
			}
		});
	},
	
	disableAllConfigTables : function()
	{
		//	console.log('disable method length check : ', $('#cluster_setup_services_table .dataNodeConfig input').length);
		$('#cluster_setup_services_table .dataNodeConfig input').prop('disabled', true);
		$('#cluster_setup_services_table .dataNodeDisk').prop('disabled', true);
		$('#cluster_setup_services_table .nameNodeConfig input').prop('disabled', true);
		$('#cluster_setup_services_table .nameNodeDisk').prop('disabled', true);
		$('#cluster_setup_services_table .RMConfig input').prop('disabled', true);
		$('#cluster_setup_services_table .NMConfig input').prop('disabled', true);
	},
	
	checkDependency : function(type)
	{
		var isConflict = false;
		if(type == 'dataNode' && $('#cluster_setup_services_table tbody input:radio.nameNode:checked').length == 0)
		{
			if($('#cluster_setup_services_table tbody td.nameNodeAlreadyInstalled').length == 0)
			{
				jAlert('Installing DataNode requires NameNode. Please select NameNode first.');
				$('#cluster_setup_services_table tbody input.' + type).prop('checked', false);
				$('#selectAll_' + type).prop('checked', false);
				isConflict = true;
			}
		}
		else if(type == 'nodeManager' && $('#cluster_setup_services_table tbody input:radio.resourceManager:checked').length == 0)
		{
			if($('#cluster_setup_services_table tbody td.resourceManagerAlreadyInstalled').length == 0)
			{
				jAlert('Installing Node Manager requires Resource Manager. Please select Resource Manager first.');
				$('#cluster_setup_services_table tbody input.' + type).prop('checked', false);
				$('#selectAll_' + type).prop('checked', false);
				isConflict = true;
			}

		}
		return isConflict;
	},
	
	deselectDNIfRequired : function(type)
	{
		if(type == 'nameNode' && $('#cluster_setup_services_table tbody input:radio.nameNode:checked').length == 0)
		{
			$('#cluster_setup_services_table tbody input.dataNode').prop('checked', false);
			$('#selectAll_dataNode').prop('checked', false);
		}
	},
	
	deselectNMIfRequired : function(type)
	{
		if(type == 'resourceManager' && $('#cluster_setup_services_table tbody input:radio.resourceManager:checked').length == 0)
		{
			$('#cluster_setup_services_table tbody input.nodeManager').prop('checked', false);
			$('#selectAll_nodeManager').prop('checked', false);
		}
	},
	
	installSetup : function()
	{
		
		AutoCluster.getHostIdsForHosts();
//		AutoCluster.startServiceInstallation();
	},
	
	loadServicesListForHosts : function()
	{
		AutoCluster.hostForResourceManager = $('.selected_hosts a', $('#cluster_setup_services_table tbody input:radio[name=resourceManager]:checked').closest('tr')).html();
		AutoCluster.hostForNameNode = $('.selected_hosts a', $('#cluster_setup_services_table tbody input:radio[name=nameNode]:checked').closest('tr')).html();
		
		AutoCluster.servicesListForHosts = new Array();
		$('#cluster_setup_services_table tbody:first tr').each(function(){
			var row = $(this);
			if($('input:checkbox:checked', row).length > 0)
			{
				var obj = new Object();
				obj['host'] = $('.selected_hosts a', row).html();
				
				$('input:checkbox:checked', row).each(function(){
					obj[$(this).attr('class')] = true;
				});
				
				if(AutoCluster.servicesListForHosts.indexOf(obj) == -1)
					AutoCluster.servicesListForHosts.push(obj);
			}
		});
		//	console.log('AutoCluster.servicesListForHosts : ', AutoCluster.servicesListForHosts);
		//	console.log('AutoCluster.hostForResourceManager : ', AutoCluster.hostForResourceManager);
		//	console.log('AutoCluster.hostForNameNode', AutoCluster.hostForNameNode);		
	},
	
	loadHostsListBeingInstalled : function()
	{
		var hostsList = new Array();
		//	console.log('check again AutoCluster.servicesListForHosts :', AutoCluster.servicesListForHosts);
		for(var i = 0; i < AutoCluster.servicesListForHosts.length; i++)
		{
			if(hostsList.indexOf(AutoCluster.servicesListForHosts[i].host) == -1)
				hostsList.push(AutoCluster.servicesListForHosts[i].host);
		}
		
		if(AutoCluster.hostForNameNode != null && hostsList.indexOf(AutoCluster.hostForNameNode) == -1)
			hostsList.push(AutoCluster.hostForNameNode);
		
		if(AutoCluster.hostForResourceManager != null && hostsList.indexOf(AutoCluster.hostForResourceManager) == -1)
			hostsList.push(AutoCluster.hostForResourceManager);
		
		AutoCluster.hostsBeingUsed = hostsList;
		//	console.log('load AutoCluster.hostsBeingUsed : ', AutoCluster.hostsBeingUsed);
	},
	
	getHostIdsForHosts : function()
	{
		RemoteManager.getHostIds(AutoCluster.hostsBeingUsed, AutoCluster.loadHostsIds);
		AutoCluster.completeMultiMessage();
	},
	
	loadHostsIds : function(map)
	{
		//	console.log('host ids map : ', map);
		AutoCluster.hostIdMappingMap = map;
		AutoCluster.startServiceInstallation();
	},
	
	startServiceInstallation : function()
	{
		if($('#cluster_setup_services_table tbody td.nameNodeAlreadyInstalled').length == 0)
		{
			AutoCluster.installNameNode(); 		//	It will install DataNode if NameNode is successfully installed.
		}
		else
		{
			AutoCluster.startDataNodeInstallation();
		}
		
		if($('#cluster_setup_services_table tbody td.resourceManagerAlreadyInstalled').length == 0)
		{
			AutoCluster.installResourceManager(); 		//	It will install Node Manager if Resource Manager is successfully installed.
		}
		else
		{
			AutoCluster.startInstallationNodeManager();
		}
	},
	
	installNameNode : function()
	{
		if(AutoCluster.hostForNameNode == null)
		{
			return;
		}
		var propertiesContainer = $('.nameNodeConfig', $('#cluster_setup_services_table tbody td.selected_hosts a:contains(' + AutoCluster.hostForNameNode + ')').closest('tr').next());
		var nameNodeId = $('.nameNode_id', propertiesContainer).val();
		
		var statusRow = $('.hostName:contains(' + AutoCluster.hostForNameNode + ')', $('#cluster_install_status_table tbody')).next();
		var statusText = "<div id = '" + nameNodeId + "' class = 'statusDiv'>" + AutoCluster.statusImagesContent + "</div>";
		statusRow.append(statusText);
		
		var hostId = AutoCluster.hostIdMappingMap[AutoCluster.hostForNameNode];
		var disk = $('.nameNodeDisk', propertiesContainer).val();
		var dirPath = $('.nameNodeVolumePath', propertiesContainer).val();
		var serverPort = $('.nameNode_server', propertiesContainer).val();
		var httpsPort = $('.nameNode_https', propertiesContainer).val();
		var httpPort = $('.nameNode_http', propertiesContainer).val();
		var s3ServerPort = $('.nameNode_s3Server', propertiesContainer).val();
		var s3SSLServerPort = $('.nameNode_s3SSLServer', propertiesContainer).val();
		var hdfsOverFTPPort = $('.nameNode_hdfsOverFTP', propertiesContainer).val();
		var ftpPort = $('.nameNode_ftpServer', propertiesContainer).val();
		var ftpSecurePort = $('.nameNode_ftpSSLServer', propertiesContainer).val();
		var jmxPort = $('.nameNode_jmx', propertiesContainer).val();
		var MetaStore = $('.nameNodeMetaStore', propertiesContainer).val();
		var adhocDb = $('.nameNodeAdhocDb', propertiesContainer).val();
		if(MetaStore == "none")
			MetaStore = null;
		if(adhocDb == "none")
			adhocDb = null;
		$('#' + nameNodeId + ' .errorMessage').html("NameNode is being installed");
		$('#' + nameNodeId + ' .image_processing').css('display', '');
		
		RemoteManager.addNameNode(hostId, nameNodeId, disk, dirPath, serverPort, httpPort, httpsPort, jmxPort,
									s3ServerPort, s3SSLServerPort, hdfsOverFTPPort, ftpPort, ftpSecurePort, MetaStore, adhocDb, false, AutoCluster.nameNodeAdded);
		
	},
	
	nameNodeAdded : function(response)
	{
		//	store confirmation of installation
		
		//	console.log('name node add response : ', response);
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		if(response.taskSuccess)
		{
			AutoCluster.changeStatus('nameNode', true);
			
			AutoCluster.responseId = response.id;
			
			Navbar.automatedSetup = true;
			AutoCluster.fillNameNodeDropDown();
		}
		else
		{
			AutoCluster.changeStatus('nameNode', false);
			$('#' + response.id + ' .image_processing').css('display', 'none');
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.totalDN = 0;
			AutoCluster.successSetup = false;
		}
//		AutoCluster.responseNN++;
		AutoCluster.displayFinish();
	},
	
	changeStatus : function(nodeType, isSuccess, nodeId)
	{
		var dataToBeAppend = '<div class = "errorDiv"><span class="image_fail"><img src="images/Fail_img.png" style="height: 12pt; margin-right: 1pt; margin-top: 1pt; vertical-align: -webkit-baseline-middle;"/></span>' + 
		'<a href="javascript:Navbar.showServerLog();" style = "margin-left: 10px;" class = "viewLog">View Log</a></div>';

		if(nodeType == 'nameNode')
		{
			var cell = $('#cluster_setup_services_table input:radio[name=nameNode]:checked').closest('td');
			if(isSuccess)
			{
				cell.html('Installed');
				cell.addClass('nameNodeAlreadyInstalled');
				$('#removeSelection_nameNode').css('visibility', 'hidden');

				$('#cluster_setup_services_table tbody:first input:radio.nameNode').prop('disabled', true);
				
				$('#cluster_setup_services_table tbody:first input:radio.nameNode').closest('td').each(function(){
					$(this).html('-');
				});
				
				var hostIP = $('.selected_hosts a', cell.closest('tr')).html();
				if(AutoCluster.hostsBeingUsed.indexOf(hostIP) > -1)
				{
					AutoCluster.hostsBeingUsed.splice(AutoCluster.hostsBeingUsed.indexOf(hostIP), 1);
				}
				AutoCluster.addNodeToHostDetails(hostIP, nodeType);
			}
			else
			{
				if($('.errorDiv', cell).length == 0)
				{
					cell.append(dataToBeAppend);
				}
			}
		}
		else if(nodeType == 'dataNode')
		{
			var allRows = $('#cluster_setup_services_table input:checkbox.dataNode:checked').closest('td');
			var cell = null;
			allRows.each(function(){
				if($('.dataNode_id', $(this).closest('tr').next()).val() == nodeId)
				{
					if(cell == null)
						cell = $(this);
				}
			});
			//	console.log('cell : ', cell);
			if(isSuccess)
			{
				cell.html('Installed');
				$('#selectAll_dataNode').prop('checked', false);
				var hostIP = $('.selected_hosts a', cell.closest('tr')).html();
				
				if(AutoCluster.hostsBeingUsed.indexOf(hostIP) > -1)
				{
					AutoCluster.hostsBeingUsed.splice(AutoCluster.hostsBeingUsed.indexOf(hostIP), 1);
				}
				$('.errorDiv', cell).html('');
				AutoCluster.addNodeToHostDetails(hostIP, nodeType);
			}
			else
			{
				if($('.errorDiv', cell).length == 0)
				{
					cell.append(dataToBeAppend);
				}
			}
		}
		else if(nodeType == 'resourceManager')
		{
			var cell = $('#cluster_setup_services_table input:radio[name=resourceManager]:checked').closest('td');
			if(isSuccess)
			{
				cell.html('Installed');
				cell.addClass('resourceManagerAlreadyInstalled');
				$('#removeSelection_resourceManager').css('visibility', 'hidden');
				
				$('#cluster_setup_services_table tbody:first input:radio.resourceManager').prop('disabled', true);
				
				$('#cluster_setup_services_table tbody:first input:radio.resourceManager').closest('td').each(function(){
					$(this).html('-');
				});
				
				var hostIP = $('.selected_hosts a', cell.closest('tr')).html();
				if(AutoCluster.hostsBeingUsed.indexOf(hostIP) > -1)
				{
					AutoCluster.hostsBeingUsed.splice(AutoCluster.hostsBeingUsed.indexOf(hostIP), 1);
				}
				
				AutoCluster.addNodeToHostDetails(hostIP, nodeType);
			}
			else
			{
				if($('.errorDiv', cell).length == 0)
				{
					cell.append(dataToBeAppend);
				}
			}
		}
		else if(nodeType == 'nodeManager')
		{
			var allRows = $('#cluster_setup_services_table input:checkbox.nodeManager:checked').closest('td');
			var cell = null;
			allRows.each(function(){
				if($('.NM_id', $(this).closest('tr').next()).val() == nodeId)
				{
					if(cell == null)
						cell = $(this);
				}
			});
			//	console.log('cell : ', cell);
			if(isSuccess)
			{
				cell.html('Installed');
				$('#selectAll_nodeManager').prop('checked', false);
				var hostIP = $('.selected_hosts a', cell.closest('tr')).html();
				if(AutoCluster.hostsBeingUsed.indexOf(hostIP) > -1)
				{
					AutoCluster.hostsBeingUsed.splice(AutoCluster.hostsBeingUsed.indexOf(hostIP), 1);
				}
				$('.errorDiv', cell).html('');
				AutoCluster.addNodeToHostDetails(hostIP, nodeType);
			}
			else
			{
				if($('.errorDiv', cell).length == 0)
				{
					cell.append(dataToBeAppend);
				}
			}
		}
	},
	
	startNameNode : function(nodeId)
	{
//		console.log('startting namenode');
		RemoteManager.startNode(nodeId , false , AutoCluster.nameNodeStarted);
	},
	
	nameNodeStarted : function(response)
	{
		//	console.log('namenode start response : ', response);
		$('#' + response.id + ' .image_processing').css('display', 'none');
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		if(response.taskSuccess)
		{
			$('#' + response.id + ' .image_success').css('display', '');
			//	console.log('startting datanodes');
			$('#cluster_setup_services_table tbody input:checkbox.dataNode:checked').each(function(index){
				AutoCluster.installDataNode(index, $(this).closest('tr').next());
			});
		}
		else
		{
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.totalDN = 0;
			AutoCluster.successSetup = false;
		}
		AutoCluster.responseNN++;
		AutoCluster.displayFinish();
	},
	
	startDataNodeInstallation : function()
	{
		$('#cluster_setup_services_table tbody input:checkbox.dataNode:checked').each(function(index){
			AutoCluster.installDataNode(index, $(this).closest('tr').next());
		});
	},
	
	installDataNode : function(prefix, row)
	{
		
		var host = $('.selected_hosts a', row.prev()).html();
		var propertiesContainer = $('.dataNodeConfig', row);

		var dataNodeId = $('.dataNode_id', propertiesContainer).val();
		
		var statusRow = $('.hostName:contains(' + host + ')', $('#cluster_install_status_table tbody')).next();
		var statusText = "<div id = '" + dataNodeId + "' class = 'statusDiv'>" + AutoCluster.statusImagesContent + "</div>";
		statusRow.append(statusText);
		
		var hostId = AutoCluster.hostIdMappingMap[host];
		var disks = new Array();
		disks.push($('.dataNodeDisk', propertiesContainer).val());
		var dirPath = new Array();
		dirPath.push($('.dataNodeVolumePath', propertiesContainer).val());
		var serverPort = $('.dataNode_server', propertiesContainer).val();
		var httpsPort = $('.dataNode_https', propertiesContainer).val();
		var httpPort = $('.dataNode_http', propertiesContainer).val();
		var ipcPort = $('.dataNode_ipc', propertiesContainer).val();
		var jmxPort = $('.dataNode_jmx', propertiesContainer).val();
		
		$('#' + dataNodeId + ' .errorMessage').html("DataNode is being installed");
		$('#' + dataNodeId + ' .image_processing').css('display', '');
		
		RemoteManager.addDataNode(hostId, dataNodeId , serverPort, httpPort, httpsPort, ipcPort, jmxPort, disks, dirPath, false, AutoCluster.dataNodeAdded);
		
	},
	
	installResourceManager : function()
	{
		if(AutoCluster.hostForResourceManager == null)
		{
			return;
		}
		
		var propertiesContainer = $('.RMConfig', $('#cluster_setup_services_table tbody td.selected_hosts a:contains(' + AutoCluster.hostForResourceManager + ')').closest('tr').next());
		
		var resourceManagerId = $('.RM_id', propertiesContainer).val();
		AutoCluster.resourceManagerId = resourceManagerId;
		
		var statusRow = $('.hostName:contains(' + AutoCluster.hostForResourceManager + ')', $('#cluster_install_status_table tbody')).next();
		var statusText = "<div id = '" + resourceManagerId + "' class = 'statusDiv'>" + AutoCluster.statusImagesContent + "</div>";
		statusRow.append(statusText);
		
		var hostId = AutoCluster.hostIdMappingMap[AutoCluster.hostForResourceManager];
		
		var serverPort = $('.RM_server', propertiesContainer).val();
		var schedulerPort = $('.RM_scheduler', propertiesContainer).val();
		var webAppPort = $('.RM_webapp', propertiesContainer).val();
		var adminPort = $('.RM_adminPort', propertiesContainer).val();
		var trackerPort = $('.RM_tracker', propertiesContainer).val();
		var jmxPort = $('.RM_jmx', propertiesContainer).val();
		var historyPort = $('.RM_historyPort', propertiesContainer).val();
		var webHistoryPort = $('.RM_webhostoryPort', propertiesContainer).val();
		var dirPath = $('.RMVolumePath', propertiesContainer).val();
		
		$('#' + resourceManagerId + ' .errorMessage').html("Resource Manager is being installed");
		$('#' + resourceManagerId + ' .image_processing').css('display', '');
		
		RemoteManager.addResourceManager(resourceManagerId, hostId, serverPort, schedulerPort, webAppPort, adminPort, jmxPort, trackerPort, 
										historyPort, webHistoryPort, dirPath, AutoCluster.resourceManagerAdded);
	},
	
	resourceManagerAdded : function(response)
	{
		//	console.log('RM add response : ', response);
		
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		if(response.taskSuccess)
		{
			AutoCluster.changeStatus('resourceManager', true);
			AutoCluster.startResourceManager(response.id);
		}
		else
		{
			AutoCluster.changeStatus('resourceManager', false);
			$('#' + response.id + ' .image_processing').css('display', 'none');
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.totalNM = 0;
			AutoCluster.responseRM ++;
			AutoCluster.successSetup = false;
		}
		AutoCluster.displayFinish();
		
	},
	
	startResourceManager : function(nodeId)
	{
		//	console.log('starting RM');
		RemoteManager.startNode(nodeId , false , AutoCluster.resourceManagerStarted);
	},
	
	resourceManagerStarted : function(response)
	{
		// store confirmation
		//	console.log('RM start response : ', response);
		
		$('#' + response.id + ' .image_processing').css('display', 'none');
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		
		if(response.taskSuccess)
		{
			$('#' + response.id + ' .image_success').css('display', '');
			//	console.log('startting nodeManager');
			$('#cluster_setup_services_table tbody input:checkbox.nodeManager:checked').each(function(index){
				AutoCluster.installNodeManager(index, $(this).closest('tr').next());
			});
		}
		else
		{
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.totalNM = 0;
			AutoCluster.successSetup = false;
		}
		
		AutoCluster.responseRM ++;
		AutoCluster.displayFinish();
	},
	
	startInstallationNodeManager : function()
	{
		$('#cluster_setup_services_table tbody input:checkbox.nodeManager:checked').each(function(index){
			AutoCluster.installNodeManager(index, $(this).closest('tr').next());
		});
	},
	
	installNodeManager : function(prefix, row)
	{
		
		var propertiesContainer = $('.NMConfig', row);
		var nodeManagerId = $('.NM_id', propertiesContainer).val();
		
		
		var host = $('.selected_hosts a', row.prev()).html();

		var statusRow = $('.hostName:contains(' + host + ')', $('#cluster_install_status_table tbody')).next();
		var statusText = "<div id = '" + nodeManagerId + "' class = 'statusDiv'>" + AutoCluster.statusImagesContent + "</div>";
		statusRow.append(statusText);
		
		var hostId = AutoCluster.hostIdMappingMap[host];
		
		var localizerPort = $('.NM_localizer', propertiesContainer).val();
		var webAppPort = $('.NM_webApp', propertiesContainer).val();
		var jmxPort = $('.NM_jmx', propertiesContainer).val();
		var dirPath = $('.NMVolumePath', propertiesContainer).val();
		
		$('#' + nodeManagerId + ' .errorMessage').html("Node Manager is being installed.");
		$('#' + nodeManagerId + ' .image_processing').css('display', '');
		
		RemoteManager.addNodeManager(nodeManagerId, hostId, AutoCluster.resourceManagerId, localizerPort, webAppPort, jmxPort, dirPath, false, AutoCluster.nodeManagerAdded);
	},
	
	nodeManagerAdded : function(response)
	{
		//	console.log('NodeManager add response : ', response);
		
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		if(response.taskSuccess)
		{
			AutoCluster.changeStatus('nodeManager', true, response.id);
			AutoCluster.startNodeManager(response.id);
		}
		else
		{
			AutoCluster.changeStatus('nodeManager', false, response.id);
			$('#' + response.id + ' .image_processing').css('display', 'none');
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.successSetup = false;
			AutoCluster.responseNM++;
		}
		AutoCluster.displayFinish();
	},
	
	startNodeManager : function(nodeId)
	{
		//	console.log('starting NodeManager');
		RemoteManager.startNode(nodeId , false , AutoCluster.nodeManagerStarted);
	},
	
	nodeManagerStarted : function(response)
	{
		$('#' + response.id + ' .image_processing').css('display', 'none');
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		if(response.taskSuccess)
		{
			$('#' + response.id + ' .image_success').css('display', '');
		}
		else
		{
			$('#' + response.id + ' .image_fail').css('display', '');
		}
		AutoCluster.responseNM ++;
		AutoCluster.displayFinish();
	},
	
	dataNodeAdded : function(response)
	{
		//	console.log('dataNode add response : ', response);
		
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		if(response.taskSuccess)
		{
			AutoCluster.changeStatus('dataNode', true, response.id);
			AutoCluster.startDataNode(response.id);
		}
		else
		{
			AutoCluster.changeStatus('dataNode', false, response.id);
			$('#' + response.id + ' .image_processing').css('display', 'none');
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.successSetup = false;
			AutoCluster.responseDN++;
		}
		
		AutoCluster.displayFinish();
	},
	
	startDataNode : function(nodeId)
	{
		//	console.log('startting dataNode');
		RemoteManager.startNode(nodeId , false , AutoCluster.dataNodeStarted);
	},
	
	dataNodeStarted : function(response)
	{
		//	console.log('start datanode response : ', response);
		AutoCluster.responseDN++;
		$('#' + response.id + ' .image_processing').css('display', 'none');
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		if(response.taskSuccess)
		{
			$('#' + response.id + ' .image_success').css('display', '');
			AutoCluster.responseId = '';
		}
		else
		{
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.successSetup = false;
		}
		AutoCluster.displayFinish();
	},
	
	checkIfRMSelected : function()
	{
		var isColflict = false;
		if($('#cluster_setup_services_table tbody input:radio[name=resourceManager]:checked').length == 0)
		{
			if($('#cluster_setup_services_table tbody td.resourceManagerAlreadyInstalled').length == 0)
			{
				isColflict = true;
				$('#cluster_setup_services_table tbody input:checkbox.nodeManager').prop('checked', false);
				jAlert('Installing Node Manager requires Resource Manager. Please select Resource Manager first.');
			}
		}
		return isColflict;
	},
	
	showInstallationStatus : function()
	{
		AutoCluster.generateStatusTable();
	},
	
	generateStatusTable : function()
	{
		//	console.log('draw table AutoCluster.hostsBeingUsed : ', AutoCluster.hostsBeingUsed);
		var tableContent = "";
		for(var i = 0; i < AutoCluster.hostsBeingUsed.length; i++)
		{
			tableContent += "<tr>";
			tableContent += "<td class = 'hostName'>" + AutoCluster.hostsBeingUsed[i] + "</td>";
			tableContent += "<td class = 'status_host'></td>";
			tableContent += "</tr>";
		}
		$('#cluster_install_status_table tbody').html(tableContent);
	},
	
	generateEvaluationStatusTableEvaluation : function()
	{
		//	console.log('draw table AutoCluster.hostsBeingUsed : ', AutoCluster.hostsBeingUsed);
		$('#hostsCredentials').css('display', 'none');
		$('#nextButton').css('display', 'none');
		$('#installerDiv').css('display', '');
		var tableContent = "";
			tableContent += "<tr>";
			tableContent += "<td class = 'hostName'>" + $('#hostsTable1 tbody tr td input:text').val() + "</td>";
			tableContent += "<td class = 'status_host'></td>";
			tableContent += "</tr>";
		
			$('#cluster_install_status_table tbody').html(tableContent);
	},
	
	copyCredentials : function()
	{
		if($('#useMasterCredentials').is(':checked'))
		{
			$('#hostsListWithCredentials tbody .userName').val($('#masterUsername').val());
			//	console.log('auty type length : ', $('#hostsListWithCredentials tbody .authType').length);
			$('#hostsListWithCredentials tbody .authType').each(function(){
				console.log('value : ', $(this).val());
				if($(this).val() == 'password')
				{
					$('.password', $(this).closest('tr')).val($('#masterPassword').val());
				}
				if($(this).val() == 'privateKey')
				{
					$('.privateKey', $(this).closest('tr')).val($('#masterPrivateKey').val());
				}
			});
			
			$('#hostsListWithCredentials tbody .port').val($('#masterPort').val());
			
			$('#hostsListWithCredentials tbody .installationPath').val($('#masterInstallationPath').val());
			
			$('#hostsListWithCredentials tbody .rackName').val($('#masterRackName').val());
			
			$('#hostsListWithCredentials tbody .agentPort').val($('#masterAgentPort').val());
		}
	},
	
	setupMethod : function()
	{
		AutoCluster.successSetup = true;
		AutoCluster.totalNN = 0;
		AutoCluster.totalDN = 0;
		AutoCluster.totalRM = 0;
		AutoCluster.totalNM = 0;
		AutoCluster.responseNN = 0;
		AutoCluster.responseDN = 0;
		AutoCluster.responseRM = 0;
		AutoCluster.responseNM = 0;
		
//		$('#hostsCredentials').css('display', '');
//		$('#installerDiv').css('display', 'none');
		
		var selectedVal = "";
		var selected = $("input[type='radio'][name='group1']:checked");
		if (selected.length > 0)
		    selectedValue = selected.val();
		if(selectedValue == 'single')
		{
			
			AutoCluster.isEvaluation = true;
			$('#automate_cluster_install_services_status').css('display', '');
			$('#backButton').prop('disabled', true);
			$('#toc').css('display', 'none');
			AutoCluster.nextStep();
		} 
		else 
		{	
			AutoCluster.isEvaluation = false;
			$('#searchbackButton').css('display','');
			$('#setupMethodTable').css('display','none');
			$('#hostsCredentials').css('display','none');
			$('#nextButton').css('display','none');
			//$('#backButton').css('display','');
			$('#serachHostTable').css('display','');
			AutoCluster.currentView = 'hosts';
			
			$('#toc').css('display', '');
		}
	},
	
	installEvaluationCluster : function()
	{
			AutoCluster.loadEvaluationHostData();
			$('#searchHostsDiv').css('display', '');
			$('#hostsListWithCredentials').css('display', 'none');
			$('#hostsCredentials').css('display', 'none');
			$('#hostsCredentials table:first').css('display', 'none');
			AutoCluster.currentView = 'evaluation_hosts_credentials';
			$('#masterUsername').val('');
			$('#masterPassword').val('');
			$('#masterPort').val('22');
			$('#useMasterCredentials').attr('checked', true);
			//$('#nextButton').html('Install').css('width', '100px');
			//$('#nextButton').css('display','');
	},
	
//	fillLocalHost : function(ip)
//	{
//		$("#hostsTable1 .hostName").val(ip);
//	},
	
	disableEvaluationFields : function(checked)
	{
		AutoCluster.isLocalClusterSetupEnabled = checked;
		if(!checked)
		{
			$("#hostsTable1").find("input,select").removeAttr("disabled");
			$("#hostsTable1 .hostName").val("");
//			$("#hostsTable1 .hostName").css("color", "black");
		}
		else
		{
			$("#hostsTable1").find("input,select").attr("disabled", "disabled");
			$("#hostsTable1 .hostName").val("127.0.0.1");
//			RemoteManager.getLocalIP(AutoCluster.fillLocalHost);
//			$("#hostsTable1 .hostName").css("color", "white");
		}
	},
	
	loadEvaluationHostData : function()
	{
		var tableContent;
		tableContent = "<div style='float: left; margin-left: 20px;'><input type='checkbox' id = 'isLocalInstallation' checked onclick = 'AutoCluster.disableEvaluationFields(this.checked);'><label> Set up cluster on local machine</label></div><br><br>";
		tableContent += "<table id = 'hostsTable1' class = 'dataTable' style = 'margin-left: 20px;'>";
			tableContent += "<thead>";
				tableContent += "<tr style = 'height: 25px;'>";
					tableContent += "<th style = 'width: 150px;'>HostName / IP</th>";
					tableContent += "<th style = 'width: 150px;'>SSH User</th>";
					tableContent += "<th style = 'width: 100px;'>Authentication Method</th>";
					tableContent += "<th style = 'width: 150px;'>Password / Private Key</th>";
					tableContent += "<th style = 'width: 150px;'>SSH Port</th>";
					tableContent += "<th style = 'width: 120px;'>Installation Path</th>";
					tableContent += "<th style = 'width: 110px;'>Rack Name</th>";
					tableContent += "<th style = 'width: 100px;'>QueryIO Agent Port</th>";
//					tableContent += "<th style = 'width: 150px;'>Status</th>";
				tableContent += "</tr>";
			tableContent += "</thead>";
			tableContent += "<tbody>";
				tableContent += "<tr id = 'hostTable_1'>";
				tableContent += AutoCluster.evaluationHostRow;
				tableContent += "</tr>";
			tableContent += "</tbody>";

//		$("#singleHost").append('<td>'+tableContent+'</td>');
		var statusText = "<tr><td>"+tableContent+"</td></tr>";
		$('#singleHost').after(statusText);
		
		AutoCluster.disableEvaluationFields(true);
		AutoCluster.isLocalClusterSetupEnabled = true;
	},
	
	evaluationClusterSetup : function(list)
	{
		AutoCluster.hostDetails = list;
		var hostIP = $('#hostsTable1 tbody tr td input:text').val();
		if(AutoCluster.isAgentAlreadyInstalled(hostIP))
		{
//			$('.errorMessage').html('Agent already installed.');
			$('#hostsCredentials').css('display','none');
			jAlert('Agent already installed.');
		}
		else
		{
			//AutoCluster.saveHost(hostIP);
			$('#setupMethodTable').css('display','none');
			$('#backButton').css('display','');
			AutoCluster.currentView = 'clusterSetup';
			AutoCluster.generateEvaluationStatusTableEvaluation();
			AutoCluster.installHost();
			
		}
	},
	
	
	installHost : function()
	{
		var host = $('#hostsTable1').html();
		var hostIP = $('#hostsTable1 .hostName').val();
		var userName = $('#hostsTable1 .userName').val();
		var password = $('#hostsTable1 .password').val();
		var port = $('#hostsTable1 .port').val();
		var sshPrivateKeyFile = $('#hostsTable1 .privateKey').val();
		var authType = $('#hostsTable1 .authType').val();
		if(authType == 'password')
		{
			sshPrivateKeyFile = null;
		}
		else
		{
			password = null;
		}
		
		var rackName = $('#hostsTable1 .rackName').val();		
		var agentPort = $('#hostsTable1 .agentPort').val();
		var installationPath = $('#hostsTable1 .installationPath').val();
		
		var hostName =  hostIP.replace(/\./g, "_");
		var statusRow = $('.hostName:contains(' + hostIP + ')', $('#cluster_install_status_table tbody')).next();
		var statusText = "<div id = '" + hostName + "' style='width: 90%;' class = 'statusDiv'>" + AutoCluster.statusImagesContent + "</div>";
		statusRow.append(statusText);
		$('#' + hostName + ' .errorMessage').html("Host is being installed");
		$('#' + hostName + ' .image_processing').css('display', '');
		
		if(!AutoCluster.isLocalClusterSetupEnabled)
		RemoteManager.insertHostAutomatation(hostIP, userName, password,
				sshPrivateKeyFile, rackName, port, installationPath, agentPort, hostName,
				AutoCluster.hostAddedSingle);
		else
			RemoteManager.insertLocalHost(hostName, AutoCluster.hostAddedSingle);
	},
	
	hostAddedSingle : function(response)
	{
			response.id = response.id.replace(/\./g, "_");
			$('#' + response.id + ' .errorMessage').html(response.responseMessage);
			
			if(response.taskSuccess)
			{
				$('#' + response.id + ' .image_processing').css('display', 'none');
				$('#' + response.id + ' .image_success').css('display', '');
				$('#backButton').prop('disabled', false);
				$('#backButton').css('display', 'none');
				var hostsList = new Array();
				hostsList.push($('#hostsTable1 .hostName').val());
				RemoteManager.getHostIds( hostsList , AutoCluster.loadHostsIdsEvaluation);
				RemoteManager.getPhysicalDiskNamesAutomation($('#hostsTable1 .hostName').val(), "" + 1, AutoCluster.getDisk);
				RemoteManager.getUserHomeDirectoryPathForHostAutomation($('#hostsTable1 .hostName').val(), "" + 1, AutoCluster.getVolume);
				AutoCluster.setupServices();
				AutoCluster.totalNN = 1;
				AutoCluster.totalDN = 1;
				AutoCluster.totalNM = 1;
				AutoCluster.totalRM = 1;
			}
			else
			{
				$('#backButton').prop('disabled', false);
				$('#' + response.id + ' .image_processing').css('display', 'none');
				$('#' + response.id + ' .image_fail').css('display', '');
	//			$('#' + response.id + ' .retry').css('display', '');
				$('#' + response.id + ' .viewLog').css('display', '');
				AutoCluster.successSetup = false;
			}
			
	},
	
	installNameNodeEvaluation : function()
	{
		var volume = AutoCluster.volumeEvaluation +  '/QueryIONodes/NameNode';
		var nameNodeId = "NameNode1";
		var hostIP = $('#hostsTable1 .hostName').val();
		
		var statusRow = $('.hostName:contains(' + hostIP + ')', $('#cluster_install_status_table tbody')).next();
		var statusText = "<div id = '" + nameNodeId + "' style='width: 90%;' class = 'statusDiv'>" + AutoCluster.statusImagesContent + "</div>";
		statusRow.append(statusText);
		
		$('#' + nameNodeId + ' .errorMessage').html("NameNode is being installed");
		$('#' + nameNodeId + ' .image_processing').css('display', '');

		if(AutoCluster.diskEvaluation != null && AutoCluster.volumeEvaluation != null && AutoCluster.nameNodeConfig != null && AutoCluster.evaluationHostIdMap != null)
		{
			RemoteManager.addNameNode(AutoCluster.evaluationHostIdMap[hostIP], nameNodeId,
					AutoCluster.diskEvaluation, volume,
					AutoCluster.nameNodeConfig["serverPort"],
					AutoCluster.nameNodeConfig["http"],
					AutoCluster.nameNodeConfig["https"],
					AutoCluster.nameNodeConfig["jmx"],
					AutoCluster.nameNodeConfig["s3serverPort"],
					AutoCluster.nameNodeConfig["s3SslserverPort"],
					AutoCluster.nameNodeConfig["hdfsOverFtpPort"],
					AutoCluster.nameNodeConfig["ftpServerPort"],
					AutoCluster.nameNodeConfig["ftpSslServerPort"], "MetaStore", "Hive",
					false, AutoCluster.nameNodeAddedSingle);
		}
		
	},
	
	
	nameNodeAddedSingle : function(response)
	{
//			console.log("namenode added");
			$('#' + response.id + ' .errorMessage').html(response.responseMessage);
			if(response.taskSuccess)
			{
				RemoteManager.startNode(response.id, true, AutoCluster.nameNodeStartedEvaluation);
			}
			else
			{
				$('#' + response.id + ' .image_processing').css('display', 'none');
				$('#' + response.id + ' .image_fail').css('display', '');
	//			$('#' + response.id + ' .retry').css('display', '');
				$('#' + response.id + ' .viewLog').css('display', '');
				AutoCluster.successSetup = false;
				AutoCluster.totalDN = 0;
			}
			AutoCluster.displayFinish();
	},
	
	getDisksandVolume : function()
	{
		RemoteManager.getPhysicalDiskNamesAutomation($('#hostsTable1 .hostName').val(), "" + 1, AutoCluster.getDisk);
		RemoteManager.getUserHomeDirectoryPathForHostAutomation($('#hostsTable1 .hostName').val(), "" + 1, AutoCluster.getVolume);
	},
	
	getDisk : function(response)
	{
		if(response.disks == null)
		{
			jAlert('No Disk found.');
		}
		else
		{
			AutoCluster.isDisk = true;
			AutoCluster.diskEvaluation = response.disks[0];
			AutoCluster.evaluationConfigHandler();
		}
	},	
	
	getVolume : function(response)
	{
		if(response.responseMessage == null)
		{
			jAlert('User Home directory path not found.');
		}
		else
		{
			AutoCluster.isVolume = true;
			AutoCluster.volumeEvaluation = response.responseMessage;
			AutoCluster.evaluationConfigHandler();
		}
	},
	
	loadHostsIdsEvaluation : function(map)
	{
		AutoCluster.evaluationHostIdMap = map;
		AutoCluster.hostid = true;
		AutoCluster.evaluationConfigHandler();
	},
	
	
	nameNodeStartedEvaluation : function(response)
	{
		//	console.log('namenode start response : ', response);
		AutoCluster.responseNN++;
		$('#' + response.id + ' .image_processing').css('display', 'none');
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		if (response.taskSuccess)
		{
			$('#' + response.id + ' .image_success').css('display', '');
			
			Navbar.automatedSetup = true;
			AutoCluster.fillNameNodeDropDown();
		}
		else
		{
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.successSetup = false;
		}
		AutoCluster.displayFinish();
	},
	
	fillNameNodeDropDown: function()
	{
		Navbar.fillAllQueryIONameNode();
	},
	
	continueAutomatedSetup: function()
	{
		Navbar.automatedSetup = false;
		
		if (AutoCluster.isEvaluation)
		{
			AutoCluster.installDataNodeEvaluation();
		}
		else
		{
			AutoCluster.startNameNode(AutoCluster.responseId);
		}
	},
	
	installDataNodeEvaluation : function()
	{
		var volume = AutoCluster.volumeEvaluation +  '/QueryIONodes/DataNode';
		var dataNodeId = "DataNode1";
		var hostIP = $('#hostsTable1 .hostName').val();
		var hostId = AutoCluster.evaluationHostIdMap[hostIP];
		var diskList = new Array();
		diskList.push(AutoCluster.diskEvaluation);
		var volumeList = new Array();
		volumeList.push(volume);
		
		var isLocal = false;
		if(hostIP == '127.0.0.1')
			isLocal = true;
		var statusRow = $('.hostName:contains(' + hostIP + ')', $('#cluster_install_status_table tbody')).next();
		var statusText = "<div id = '" + dataNodeId + "' style='width: 90%;'  class = 'statusDiv'>" + AutoCluster.statusImagesContent + "</div>";
		statusRow.append(statusText);
		
		$('#' + dataNodeId + ' .errorMessage').html("DataNode is being installed");
		$('#' + dataNodeId + ' .image_processing').css('display', '');
		
		if(AutoCluster.diskEvaluation != null && AutoCluster.volumeEvaluation != null && AutoCluster.dataNodeConfig != null && AutoCluster.evaluationHostIdMap != null)
		{
			
			RemoteManager.addDataNode(hostId, dataNodeId,
					AutoCluster.dataNodeConfig["serverPort"],
					AutoCluster.dataNodeConfig["http"],
					AutoCluster.dataNodeConfig["https"],
					AutoCluster.dataNodeConfig["ipc"],
					AutoCluster.dataNodeConfig["jmx"], diskList, volumeList, isLocal,
					AutoCluster.dataNodeAddedEvaluation);
		}
		
	},
	
	dataNodeAddedEvaluation : function(response)
	{
		//	console.log('dataNode add response : ', response);
		
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		if(response.taskSuccess)
		{
			AutoCluster.startDataNode(response.id);
		}
		else
		{
			$('#' + response.id + ' .image_processing').css('display', 'none');
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.successSetup = false;
			AutoCluster.responseDN++;
		}
		AutoCluster.displayFinish();
	},
	
	
	installResourceManagerEvaluation : function()
	{
		var volume = AutoCluster.volumeEvaluation +  '/QueryIONodes/ResourceManager';
		var resourceManagerId = "ResourceManager1";
		var hostIP = $('#hostsTable1 .hostName').val();
		var hostId = AutoCluster.evaluationHostIdMap[hostIP];
		AutoCluster.resourceManagerId = resourceManagerId;
		
		var statusRow = $('.hostName:contains(' + hostIP + ')', $('#cluster_install_status_table tbody')).next();
		var statusText = "<div id = '" + resourceManagerId + "' style='width: 90%;' class = 'statusDiv'>" + AutoCluster.statusImagesContent + "</div>";
		statusRow.append(statusText);
		
		$('#' + resourceManagerId + ' .errorMessage').html("Resource Manager is being installed");
		$('#' + resourceManagerId + ' .image_processing').css('display', '');
		
		if(AutoCluster.volumeEvaluation != null && AutoCluster.rmConfig != null && AutoCluster.evaluationHostIdMap != null)
		{
			RemoteManager.addResourceManager(resourceManagerId, hostId,
					AutoCluster.rmConfig["serverPort"],
					AutoCluster.rmConfig["scheduler"],
					AutoCluster.rmConfig["webapp"],
					AutoCluster.rmConfig["adminPort"], AutoCluster.rmConfig["jmx"],
					AutoCluster.rmConfig["tracker"],
					AutoCluster.rmConfig["hostoryPort"],
					AutoCluster.rmConfig["webhostoryPort"], volume,
					AutoCluster.resourceManagerAddedEvaluation);
		}
	},
	
	resourceManagerAddedEvaluation : function(response)
	{
		//	console.log('RM add response : ', response);
		
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		if(response.taskSuccess)
		{
			RemoteManager.startNode(response.id , false , AutoCluster.resourceManagerStartedEvaluation);
		}
		else
		{
			$('#' + response.id + ' .image_processing').css('display', 'none');
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.successSetup = false;
			AutoCluster.totalNM = 0;
			AutoCluster.responseRM ++;
		}
		AutoCluster.displayFinish();
	},
	
	resourceManagerStartedEvaluation : function(response)
	{
		//	console.log('RM start response : ', response);
		
		$('#' + response.id + ' .image_processing').css('display', 'none');
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		AutoCluster.totalSingle++;
		if(response.taskSuccess)
		{
			$('#' + response.id + ' .image_success').css('display', '');
			//	console.log('startting nodeManager');
			AutoCluster.installNodeManagerEvaluation();
		}
		else
		{
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.successSetup = false;
		}
		AutoCluster.responseRM++;
		AutoCluster.displayFinish();
	},
	
	installNodeManagerEvaluation : function()
	{
		var volume = AutoCluster.volumeEvaluation +  '/QueryIONodes/NodeManager';
		var nodeManagerId = "NodeManager1";
		var hostIP = $('#hostsTable1 .hostName').val();
		var hostId = AutoCluster.evaluationHostIdMap[hostIP];
		
		var isLocal = false;
		if(hostIP == '127.0.0.1')
			isLocal = true;
		
		var statusRow = $('.hostName:contains(' + hostIP + ')', $('#cluster_install_status_table tbody')).next();
		var statusText = "<div id = '" + nodeManagerId + "' style='width: 90%;' class = 'statusDiv'>" + AutoCluster.statusImagesContent + "</div>";
		statusRow.append(statusText);
		
		$('#' + nodeManagerId + ' .errorMessage').html("Node Manager is being installed.");
		$('#' + nodeManagerId + ' .image_processing').css('display', '');
		
		if(AutoCluster.volumeEvaluation != null && AutoCluster.nmConfig != null && AutoCluster.evaluationHostIdMap != null)
		{
			RemoteManager.addNodeManager(nodeManagerId, hostId,
					AutoCluster.resourceManagerId,
					AutoCluster.nmConfig["localizer"],
					AutoCluster.nmConfig["webapp"],
					AutoCluster.nmConfig["jmx"], volume, isLocal,
					AutoCluster.nodeManagerAddedEvaluation);
		}
	},
	
	
	nodeManagerAddedEvaluation : function(response)
	{
		//	console.log('NodeManager add response : ', response);
		
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		if(response.taskSuccess)
		{
			RemoteManager.startNode(response.id , false , AutoCluster.nodeManagerStartedEvaluation);
		}
		else
		{
			$('#' + response.id + ' .image_processing').css('display', 'none');
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.successSetup = false;
			AutoCluster.responseNM ++;
		}
		AutoCluster.displayFinish();
	},
	
	nodeManagerStartedEvaluation : function(response)
	{
		AutoCluster.responseNM++;
		$('#' + response.id + ' .image_processing').css('display', 'none');
		$('#' + response.id + ' .errorMessage').html(response.responseMessage);
		AutoCluster.totalSingle++;
		if(response.taskSuccess)
		{
			$('#' + response.id + ' .image_success').css('display', '');
		}
		else
		{
			$('#' + response.id + ' .image_fail').css('display', '');
			AutoCluster.successSetup = false;
		}
		AutoCluster.displayFinish();
	},
	
	tocSlectHostActive : function()
	{
		$('#liSelect').addClass("active");
		$('#liAgent').removeClass('active');
		$('#liNode').removeClass('active');
		$('#liStatus').removeClass('active');
	},
	
	tocAgentActive : function()
	{
		$('#liSelect').removeClass("active");
		$('#liAgent').addClass('active');
		$('#liNode').removeClass('active');
		$('#liStatus').removeClass('active');
	},
	
	tocNodeActive : function()
	{
		$('#liSelect').removeClass("active");
		$('#liAgent').removeClass('active');
		$('#liNode').addClass('active');
		$('#liStatus').removeClass('active');
	},
	
	tocStatusActive : function()
	{
		$('#liSelect').removeClass("active");
		$('#liAgent').removeClass('active');
		$('#liNode').removeClass('active');
		$('#liStatus').addClass('active');
	},
	
	hideSingle : function()
	{
		var selectedValue="";
		var selected = $("input[type='radio'][name='group1']:checked");
		if (selected.length > 0)
		    selectedValue = selected.val();
		if(selectedValue == 'single')
		{
			$("#hostsTable1").find("input,select,checkbox").removeAttr("disabled");
		}
		else
		{
			$("#hostsTable1").find("input,select,checkbox").attr("disabled", "disabled");
		}
	},
	
	disableMasterCredentials : function()
	{
		if($('#useMasterCredentials').is(':checked'))
		{
			$("#hostsCredentials table:first").find("input[type='text'],input[type='[password'],select").removeAttr("disabled");
		}
		else
		{
			$("#hostsCredentials table:first").find("input[type='text'],input[type='[password'],select").attr("disabled", "disabled");
		}
	},
	
	hideAlreadyInstalledTables : function()
	{
		for(var i=0;i<AutoCluster.installed_nn.length;i++)
		{
			var id = AutoCluster.installed_nn[i];
			$('#cluster_setup_services_table tr.child-of-treeTableHost_'+id+ ' td.nameNodeConfig table' ).css('display' , 'none');
		}
		
		for(var i=0;i<AutoCluster.installed_dn.length;i++)
		{
			var id = AutoCluster.installed_dn[i];
			$('#cluster_setup_services_table tr.child-of-treeTableHost_'+id+ ' td.dataNodeConfig table' ).css('display' , 'none');
		}
		
		for(var i=0;i<AutoCluster.installed_rm.length;i++)
		{
			var id = AutoCluster.installed_rm[i];
			$('#cluster_setup_services_table tr.child-of-treeTableHost_'+id+ ' td.RMConfig table' ).css('display' , 'none');
		}
		
		for(var i=0;i<AutoCluster.installed_nm.length;i++)
		{
			var id = AutoCluster.installed_nm[i];
			$('#cluster_setup_services_table tr.child-of-treeTableHost_'+id+ ' td.NMConfig table' ).css('display' , 'none');
		}
	},
	
	completeMessage : function()
	{
		if(AutoCluster.totalSingle == AutoCluster.expectedTotalSingle )
		{
			if(! AutoCluster.successSetup)
				jAlert('Hadoop Cluster setup failed. please check configuration settings and try again.', 'Cluster Setup Complete');
			else
				jAlert('Hadoop Cluster setup completed successfully. You can now start importing data to cluster and analyze it.', 'Cluster Setup Complete');
		}
//		else
//		{
//			setTimeout(function(){
//				AutoCluster.completeMessage();
//	        },2000);
//		}
	},
	
	completeMultiMessage : function()
	{
		AutoCluster.totalNN = $('#cluster_setup_services_table tbody input.nameNode:checked').length;
		
		AutoCluster.totalDN = $('#cluster_setup_services_table tbody input.dataNode:checked').length;
		
		AutoCluster.totalRM = $('#cluster_setup_services_table tbody input.resourceManager:checked').length;
		
		AutoCluster.totalNM = $('#cluster_setup_services_table tbody input.nodeManager:checked').length;
		
		AutoCluster.displayFinish();
	},
	
	displayFinish : function()
	{		
		if(AutoCluster.responseNN == AutoCluster.totalNN && AutoCluster.responseRM == AutoCluster.totalRM)
		{
			if(AutoCluster.responseDN == AutoCluster.totalDN && AutoCluster.responseNM == AutoCluster.totalNM)
			{
				if(AutoCluster.successSetup == false)
					jAlert('Hadoop Cluster setup failed. please check configuration settings and try again.', 'Cluster Setup Incomplete');
				else
					jAlert('Hadoop Cluster setup completed successfully. You can now start importing data to cluster and analyze it.', 'Cluster Setup Complete');
			}
		}
	}	
};
