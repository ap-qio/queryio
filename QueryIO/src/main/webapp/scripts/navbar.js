Navbar = {
	currentBackupTabSelected :'',
	isSetButtonWidth:true,
	selectedRack:'',
	selectedHost:'',
	doAutoRefresh:false,
	selectedTabId : '',
	isRefreshPage : false,
	activeMenu:'',
	timeOutArray :[],
	selectedTabName :'',
	selectedChildTab :'',
	selectedGrandChildTab : '',
	currentSelectedView : 1,
	isquickStartOpen:true,
	showSelectedDb : false,
	dataTablePreferredRowCount : new Object(),
	queryManagerDirtyBit : false,
	disableAutoRefresh : false,
	interval : '',
	autoRefreshTimeout : 20000,
	selectedQueryId : '',
	isFromsummaryView:false,
	isViewerView :false,
	isAddNewQuery :false,
	isExecuteQuery:false,
	isEditQuery :false,
	isDataAvailabe:true,
	selectedDatabaseFromMigrationTable : null,
	currentJobTabSelected : '',
	currentScheduleTabSelected : '',
	currentManageTabSelected : '',
	currentHiveTabSelected : '',
	currentViewTabSelected : '',
	isHiveViewSelected : false,
	hiveViewNameNode : undefined,
	hiveViewSelectedTable : undefined,
	automatedSetup : false,
	promptPasswordChange : false,
	imgArray : new Array(),
	
	changeTab: function (selectedId, tabName, childtab, grandchildtab) {
		
		console.log("selectedId: " + selectedId);
		console.log("tabName: " + tabName);
		console.log("childtab: " + childtab);
		console.log("grandchildtab: " + grandchildtab);
		console.log("this.selectedTabId: " + this.selectedTabId);
		
		if (!((tabName == 'rm_detail'||tabName == 'nm_detail') && (this.selectedTabId==selectedId)))
		{
			if(this.selectedTabName ==tabName && this.selectedTabId==selectedId&&!this.isRefreshPage &&this.selectedChildTab==childtab&&this.selectedGrandChildTab==grandchildtab){
				return;
			}
		}
		
		if(Navbar.queryManagerDirtyBit == true)
		{
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
                jConfirm("Some of the fields of Query \"" + DA.selectedQueryId + "\" are modified. Do you want to navigate?",'Query',function(val){
                    if (val== true)
                    {
                    	Navbar.queryManagerDirtyBit = false;
                    	Navbar.changetabImpl(selectedId, tabName, childtab, grandchildtab);
            			

                    }
                });
                jQuery.alerts.okButton = ' Ok ';
    			jQuery.alerts.cancelButton  = ' Cancel';
//            $("#popup_container").css("z-index","99999999");
		}
		else
		{
			Navbar.changetabImpl(selectedId, tabName, childtab, grandchildtab);
			
		}

	},
	
	changetabImpl : function(selectedId, tabName, childtab, grandchildtab)
	{
		console.log("selectedId: " + selectedId);
		console.log("tabName: " + tabName);
		console.log("childtab: " + childtab);
		console.log("grandchildtab: " + grandchildtab);
		
		if($('#loggedInUser').html() == ''){
			 RemoteManager.getLoggedInUser(getUserName);
		}
		
		if (!((tabName == 'rm_detail'||tabName == 'nm_detail') && (this.selectedTabId==selectedId)))
		{
			if(this.selectedTabName ==tabName && this.selectedTabId==selectedId&&!this.isRefreshPage &&this.selectedChildTab==childtab&&this.selectedGrandChildTab==grandchildtab){
				return;
			}
		}
		
		if (this.selectedChildTab == 'QuerySpreadSheet')
		{
//			console.log("Remove all textarea, if any.");
			$.sheet.killAll();
		}
		
		Navbar.doAutoRefresh=false;
		this.isRefreshPage = false;
		this.selectedTabId = selectedId;
		this.selectedTabName = tabName;
		this.selectedChildTab = childtab;
		this.selectedGrandChildTab =grandchildtab;
		$('#refreshViewButton').show();
		
		$('#queryIONameNodeId').removeAttr('disabled');
		$('#refreshViewButton').attr('onclick','javascript:Navbar.refreshView()');
		
		document.getElementById("queryIONameNodeIdSpan").style.display = 'none';
		document.getElementById("migrationStatusBtn").style.display = 'none';
		Navbar.isHiveViewSelected = false;
		
		var source = '';
		var header = 'Dashboard';
		var pathHeader = 'HDFS >> Dashboard';
		
		
		if(tabName == "data")
		{
			selectedId = 'Data';
			if (typeof(childtab) == "undefined")
			{
				header = 'Data Browser';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data </a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> Data Browser</span>';
				source = 'resources/databrowser.html';
				document.getElementById("queryIONameNodeIdSpan").style.display = '';
			}
			else if (childtab == 'define_schema') 
			{
				header = 'Define Schema ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/adHocQuery.html';
				Navbar.doAutoRefresh=true;
			}else if (childtab == 'define_data_tags') 
			{
				header = 'Data Tags';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/data_tagging.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == 'data_migration') 
			{
				header = 'Data Import/Export ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> Data </span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/data_migration.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == 'data_overview') 
			{
				header = 'Overview ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> Data </span>';
				source = 'resources/data_overview.html';
				Navbar.doAutoRefresh=true;
			}
		}
		else if (tabName == 'analytics')
		{
			selectedId = 'Analytics';
			Navbar.isViewerView=false;
			
			if (typeof(childtab) == "undefined")
			{
				header = 'Manage Query';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> ' + header + '</span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryDesigner\',\'analytics\',\'QueryDesigner\');">Design Report</span></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryViewer\',\'analytics\',\'QueryViewer\');">View Report</span></a>'
							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QuerySpreadSheet\',\'analytics\',\'QuerySpreadSheet\');">SpreadSheet</span></a>';
				document.getElementById("queryIONameNodeIdSpan").style.display = '';
				source = 'resources/bigQuerySummary.html';
			}
			else if(childtab == 'QueryDesigner') 
			{
				header = 'Design Report';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Analytics\',\'analytics\');">Manage Query</span></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryViewer\',\'analytics\',\'QueryViewer\');">View Report</span></a>'
					+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QuerySpreadSheet\',\'analytics\',\'QuerySpreadSheet\');"> SpreadSheet </span></a>';
				source = 'resources/data_analyzerTab.html';
				document.getElementById("queryIONameNodeIdSpan").style.display = '';
				$('#queryIONameNodeId').attr('disabled','disabled');
				$('#refreshViewButton').hide();
				
			}
			else if(childtab == 'QueryViewer') 
			{
				header = 'View Report';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Analytics\',\'analytics\');">Manage Query</span></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryDesigner\',\'analytics\',\'QueryDesigner\');">Design Report</span></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>'
					+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QuerySpreadSheet\',\'analytics\',\'QuerySpreadSheet\');">SpreadSheet</span></a>';
				source = 'resources/bigQueryViewer.html';
				$('#refreshViewButton').hide();
				document.getElementById("queryIONameNodeIdSpan").style.display = '';
			}
			
			else if(childtab == 'analytics_overview') 
			{
				header = 'Overview ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Analytics\',\'analytics\');">Analyse</span></a>';
				source = 'resources/analytics_overview.html';
				$('#refreshViewButton').hide();
				document.getElementById("queryIONameNodeIdSpan").style.display = '';
			}
			
			else if(childtab == 'QuerySpreadSheet') {
				
				if(document.getElementById('bigQueryIds') != undefined){
					var queryId = document.getElementById('bigQueryIds').value;
					
					Util.setCookie("last-visited-query",queryId,1);
				}
				
                header = ' View SpreadSheet';
                pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
                	+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Analytics\',\'analytics\');">Manage Query</span></a><img src="images/forward.png" style="height:20px">'
                	+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryDesigner\',\'analytics\',\'QueryDesigner\');">Design Report</span></a><img src="images/forward.png" style="height:20px">'
                    +'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryViewer\',\'analytics\',\'QueryViewer\');">View Report</span></a>'
                    +'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
                source = 'spreadsheet/spreadSheetView.html';
                $('#refreshViewButton').hide();
                document.getElementById("queryIONameNodeIdSpan").style.display = '';
			}
		}
		else if (tabName == 'jobs') 
		{
			if(childtab=='JobBrowser')
			{
				pathHeader = '<a href="javascript:Navbar.changeTab(\'jobs\',\'jobs\', \'JobBrowser\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> Manage Jobs </span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'jobs\',\'jobs\',\'JobHistory\');"> History </a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'jobs\',\'jobs\',\'JobStatus\');"> Status </a></span>';
				source = 'resources/job_browser.html';
				Navbar.doAutoRefresh=true;
			}
			else if(childtab=='JobHistory')
			{
				pathHeader = '<a href="javascript:Navbar.changeTab(\'jobs\',\'jobs\', \'JobBrowser\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'jobs\',\'jobs\',\'JobBrowser\');"> Manage Jobs </a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span>History</span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'jobs\',\'jobs\',\'JobStatus\');"> Status </a></span>';
				source = 'resources/job_history.html';
				Navbar.doAutoRefresh=true;
			}
			else if(childtab=='jobs_overview')
			{
				pathHeader = '<a href="javascript:Navbar.changeTab(\'jobs\',\'jobs\', \'JobBrowser\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'jobs\',\'jobs\',\'JobBrowser\');"> Jobs </a></span>';
				source = 'resources/jobs_overview.html';
				Navbar.doAutoRefresh=true;
			}
			else if(childtab=='JobStatus')
			{
				pathHeader = '<a href="javascript:Navbar.changeTab(\'jobs\',\'jobs\', \'JobBrowser\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'jobs\',\'jobs\',\'JobBrowser\' );"> Manage Jobs </a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'jobs\',\'jobs\',\'JobHistory\');">  History </a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> Status </span>';
				source = 'resources/schedule_status.html';
				Navbar.doAutoRefresh=true;
			}
			selectedId = 'Jobs';
		}
		else if (tabName == 'setup') 
		{
			if(childtab == 'quicksetup')
			{
				header = 'Cluster Setup ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Setup\',\'setup\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> Quick Setup </span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/automate_cluster_setup.html';
				Navbar.doAutoRefresh=false;
			}
			else if(childtab == 'hosts') 
			{
				header = 'Manage Hosts';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Setup\',\'setup\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> Quick Setup </span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/host_config.html';
				Navbar.doAutoRefresh=true;
			}
			selectedId = 'Setup';
		}
		
		else if (tabName == 'Hadoop') 
		{
			if (typeof(childtab) == "undefined")
			{
				header = 'HDFS Overview';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> HDFS </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/dashboard.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == 'nn_summary')
			{
					header = 'Rack Summary';
					pathHeader = '<a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span> HDFS </span><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span>NameNode Summary</span>';
					source = 'resources/nn_summary.html';
					Navbar.doAutoRefresh=true;
					selectedId = 'Hadoop';
					
			}
			else if (childtab == 'dn_summary')
			{
					header = 'DataNode Summary';
					pathHeader = '<a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span> HDFS </span><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
					source = 'resources/dn_summary.html';
					Navbar.doAutoRefresh=true;
			}
			else if(childtab == 'JournalNode') 
			{
				header = 'Journal Node';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> HDFS </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/journal_node.html';
				Navbar.doAutoRefresh=true;
			}
			else if(childtab == 'CheckPointNode') 
			{
				header = 'CheckPoint Node';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> HDFS </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/check_point_node.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == "system_config_HDFS") 
			{
				header = "Configure HDFS";
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> HDFS </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/system_config.html';
				Navbar.doAutoRefresh=true;
			}
			selectedId = 'Hadoop';
		}
		
		
		else if (tabName == 'MapReduce')
		{
			if (typeof(childtab) == "undefined")
			{
				header = 'MapReduce  Overview';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'MapReduce\',\'MapReduce\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> MapReduce </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/dashboard-MapReduce.html';
				Navbar.doAutoRefresh=true;
			}
			else if(childtab=='ResourceManager')
			{
				header = 'ResourceManager';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'MapReduce\',\'MapReduce\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> MapReduce </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/resource_manager.html';
				Navbar.doAutoRefresh=true;
			}
			else if(childtab=='NodeManager')
			{
				header = 'NodeManager';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'MapReduce\',\'MapReduce\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> MapReduce </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/node_manager.html';	
				Navbar.doAutoRefresh=true;
			}

			else if(childtab == 'system_config_MR')
			{
				header = "Configure "+grandchildtab;
				pathHeader = '<a href="javascript:Navbar.changeTab(\'MapReduce\',\'MapReduce\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> MapReduce </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/system_config.html';
				Navbar.doAutoRefresh=true;
			}
			selectedId = 'MapReduce';
			
		}
		
		else if(tabName == 'Monitor') {
			if (childtab == 'system_monitor')
			{
				header = 'System Monitor';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Monitor\',\'Monitor\',\'system_monitor\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> Monitor </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/status.html';
				Navbar.doAutoRefresh=true;				
			}
			else if (childtab == 'HDFS')
			{
				header = 'HDFS Monitor';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Monitor\',\'Monitor\',\'system_monitor\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> Monitor </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/dashboard.html';
				Navbar.doAutoRefresh=true;
			}
			if (childtab == 'MapReduce')
			{
				header = 'MapReduce  Monitor';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Monitor\',\'Monitor\',\'system_monitor\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> Monitor </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/dashboard-MapReduce.html';
				Navbar.doAutoRefresh=true;
			}
			selectedId = 'Monitor';
			
		}
		
		else if (tabName == "Admin") {
			
			if (childtab == 'queryio_services') 
			{
				header = 'QueryIO Services ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Admin\',\'Admin\',\'queryio_services\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> Others </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/queryio_services.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == 'manage_datasources') 
			{
				header = 'Manage Data Sources ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Admin\',\'Admin\',\'queryio_services\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> Others </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/manage_datasources.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == 'users') 
			{
				header = 'Users & Groups';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Admin\',\'Admin\',\'queryio_services\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> Others </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				if(Navbar.promptPasswordChange) {
					source = 'resources/demoUserPasswordChange.html';
				} else {
					source = 'resources/admin_users.html';
				}
			}
			else if (childtab == "all_alerts")
			{
				header = 'Alerts';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Admin\',\'Admin\',\'queryio_services\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> Others </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/alerts_list.html';
			}
			else if (childtab == 'set_alerts') 
			{
				header = 'Rules for Alerts';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Admin\',\'Admin\',\'queryio_services\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> Others </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/alerts_configure.html';
			}
			else if(childtab == 'notifications') 
			{
				header = 'Notifications';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Admin\',\'Admin\',\'queryio_services\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> Others </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/admin_notifications.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == 'all_reports') 
			{
				header = 'System Reports';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Admin\',\'Admin\',\'queryio_services\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> Others </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/reports_all_reports.html';
			}
			else if(childtab == 'report_schedules')
			{
				header = 'Schedules';
//				header = childtab == 'all_reports' ? 'Reports':'Schedules';
//				var temp = (childtab == 'all_reports' ? 'All Reports':'Report Schedules');
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Admin\',\'Admin\',\'queryio_services\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> Others </span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/reports_' + childtab + '.html';
			} 			
			selectedId = 'Admin';
		}
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		else if (tabName == 'dashboard')
//		{
//			if (typeof(childtab) == "undefined")
//			{
//				header = 'Dashboard';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span> Dashboard</span>'
//					+ '<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span>HDFS Overview</span>';
//				source = 'resources/dashboard.html';
//				Navbar.doAutoRefresh=true;
//			}
//			else if(childtab == 'MapReduce')
//			{
//				header = 'Dashboard';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');"> Dashboard</a></span>'
//							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span>MapReduce Overview</span>';
//				source = 'resources/dashboard-MapReduce.html';
//				Navbar.doAutoRefresh=true;
//			}
//			else if (childtab == "system_monitor")
//			{
//				header = 'System Monitor';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');"> Dashboard</a></span>'
//							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span>System Monitor</span>';
//				source = 'resources/status.html';
//				Navbar.doAutoRefresh=true;				
//			}
//			else if (childtab == "all_alerts")
//			{
//				header = 'Alerts';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');"> Dashboard</a></span>'
//							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span>System Alerts</span>';
//				source = 'resources/alerts_list.html';
//			}
//			else if (childtab == 'set_alerts') 
//			{
//				header = 'Rules for Alerts';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');"> Dashboard</a></span>'
//							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/alerts_configure.html';
//			}
//			else if(childtab == 'notifications') 
//			{
//				header = 'Notifications';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');"> Dashboard</a></span>'
//							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/admin_notifications.html';
//				Navbar.doAutoRefresh=true;
//			}
//			else if (childtab == 'notifications_rules_for_alerts')
//			{
//				header = 'Notifications';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');"> Dashboard</a></span>'
//							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Configure Alerts\',\'dashboard\', \'set_alerts\');"> Rules for Alerts</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/admin_notifications.html';
//			}
//			selectedId = 'Dashboard';
//			
//		}
//		else if (tabName == 'Hadoop') 
//		{
//			if(childtab == "HDFS")
//			{
//				header = 'HDFS Overview';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+ '<img src="images/forward.png" style="height:20px"><span>HDFS</span><img src="images/forward.png" style="height:20px">'
//							+ '<img src="images/forward.png" style="height:20px"><span>HDFS Overview</span>';
//				source = 'resources/dashboard.html';
//				Navbar.doAutoRefresh=true;
//			}
//			else if (childtab == "MapReduce")
//			{
//				header = 'MapReduce Overview';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+ '<img src="images/forward.png" style="height:20px"><span>MapReduce</span><img src="images/forward.png" style="height:20px">'
//							+ '<img src="images/forward.png" style="height:20px"><span>MapReduce Overview</span>';
//				source = 'resources/dashboard-MapReduce.html';
//				Navbar.doAutoRefresh=true;
//			}
//			else if (typeof(childtab) == "undefined")
//			{
//				header = 'System Monitor';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+ '<img src="images/forward.png" style="height:20px"><span> Hadoop</span><img src="images/forward.png" style="height:20px">'
//							+ '<img src="images/forward.png" style="height:20px"><span>' + header + '</span>';
//				source = 'resources/status.html';
//				Navbar.doAutoRefresh=true;				
//			}
//			else if (childtab == "system_config_HDFS") 
//			{
//				header = "Configure HDFS";
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/system_config.html';
//			}
//			else if(childtab == 'JournalNode') 
//			{
//				header = 'Journal Node';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/journal_node.html';
//			}
//			else if(childtab == 'CheckPointNode') 
//			{
//				header = 'CheckPoint Node';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/check_point_node.html';
//			}
//			else if(childtab == 'system_config_MR')
//			{
//				header = "Configure "+grandchildtab;
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'MapReduce\');"> MapReduce</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/system_config.html';
//			}
//			else if(childtab=='ResourceManager')
//			{
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'MapReduce\');"> MapReduce</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span>ResourceManager</span>';
//				source = 'resources/resource_manager.html';
//				Navbar.doAutoRefresh=true;
//			}
//			else if(childtab=='NodeManager')
//			{
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'MapReduce\');"> MapReduce</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span>NodeManager</span>';
//				source = 'resources/node_manager.html';	
//				Navbar.doAutoRefresh=true;
//			}
//			selectedId = 'Hadoop';
//		}
//		
//		else if (tabName == 'analytics')
//		{
//			selectedId = 'Analytics';
//			Navbar.isViewerView=false;
//			
//			if (typeof(childtab) == "undefined")
//			{
//				header = 'Manage Query';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> ' + header + '</span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryDesigner\',\'analytics\',\'QueryDesigner\');">Design Report</span></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryViewer\',\'analytics\',\'QueryViewer\');">View Report</span></a>'
//							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QuerySpreadSheet\',\'analytics\',\'QuerySpreadSheet\');">SpreadSheet</span></a>';
//				document.getElementById("queryIONameNodeIdSpan").style.display = '';
//				source = 'resources/bigQuerySummary.html';
//			}
//			else if(childtab == 'QueryDesigner') 
//			{
//				header = 'Design Report';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Analytics\',\'analytics\');">Manage Query</span></a><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryViewer\',\'analytics\',\'QueryViewer\');">View Report</span></a>'
//					+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QuerySpreadSheet\',\'analytics\',\'QuerySpreadSheet\');"> SpreadSheet </span></a>';
//				source = 'resources/data_analyzerTab.html';
//				document.getElementById("queryIONameNodeIdSpan").style.display = '';
//				$('#queryIONameNodeId').attr('disabled','disabled');
//				$('#refreshViewButton').hide();
//				
//			}
//			else if(childtab == 'QueryViewer') 
//			{
//				header = 'View Query';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Analytics\',\'analytics\');">Manage Query</span></a><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryDesigner\',\'analytics\',\'QueryDesigner\');">Design Query</span></a><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>'
//					+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QuerySpreadSheet\',\'analytics\',\'QuerySpreadSheet\');">SpreadSheet</span></a>';
//				source = 'resources/bigQueryViewer.html';
//				$('#refreshViewButton').hide();
//				document.getElementById("queryIONameNodeIdSpan").style.display = '';
//			}
//			else if(childtab == 'QuerySpreadSheet') {
//				
//				if(document.getElementById('bigQueryIds') != undefined){
//					var queryId = document.getElementById('bigQueryIds').value;
//					
//					Util.setCookie("last-visited-query",queryId,1);
//				}
//				
//                header = ' View SpreadSheet';
//                pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//                	+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Analytics\',\'analytics\');">Manage Query</span></a><img src="images/forward.png" style="height:20px">'
//                	+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryDesigner\',\'analytics\',\'QueryDesigner\');">Design Report</span></a><img src="images/forward.png" style="height:20px">'
//                    +'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryViewer\',\'analytics\',\'QueryViewer\');">View Report</span></a>'
//                    +'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//                source = 'spreadsheet/spreadSheetView.html';
//                $('#refreshViewButton').hide();
//                document.getElementById("queryIONameNodeIdSpan").style.display = '';
//			}
//			else if(childtab == 'QuerySpreadSheetSlick') {
//				header = 'Slick SpreadSheet Viewer';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Analytics\',\'analytics\');"> Query Manager</span></a><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryDesigner\',\'analytics\',\'QueryDesigner\');"> Query Designer</span></a><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryViewer\',\'analytics\',\'QueryViewer\');"> Query Viewer</span></a>'
//					+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/spreadSheet_slick.html';
//				$('#refreshViewButton').hide();
//				document.getElementById("queryIONameNodeIdSpan").style.display = '';
//			}
//		}
//
//		else if (tabName == 'nn_summary')
//		{
//				header = 'Rack Summary' +selectedId;
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'		
//							+'<img src="images/forward.png" style="height:20px"><span> NameNode Summary</span>';
//				source = 'resources/nn_summary.html';
//				Navbar.doAutoRefresh=true;
//				selectedId = 'Hadoop';
//				
//		}
//		else if (tabName == 'rack_summary') 
//		{
//			header = 'Rack Summary'+selectedId;
//			pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'	
//						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'TreeDataNode\',\'dn_summary\');" class="tab_banner" >DataNode Summary</a></span><img src="images/forward.png" style="height:20px">'
//						+'<img src="images/forward.png" style="height:20px"><span> '+selectedId+'</span>';
//			Navbar.doAutoRefresh=true;
//			selectedId = 'Hadoop';
//			this.selectedRack=selectedId;
//			source = 'resources/rack_summary.html';
//			
//		}
//		else if(tabName=='nn_host')
//		{
//			header = 'Host Summary'+selectedId;
//			pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'	
//						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'nn_summary\');" class="tab_banner">NameNode Summary</a></span><img src="images/forward.png" style="height:20px">'
//						+'<img src="images/forward.png" style="height:20px"><span> '+selectedId+'</span>';
//			this.selectedHost=selectedId;
//			source = 'resources/nn_host_summary.html';
//			selectedId = 'Hadoop';
//			Navbar.doAutoRefresh=true;
//		}
//		else if (tabName == 'host_summary')
//		{
//			header = 'Host Summary'+selectedId;
//			pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'	
//						+'<img src="images/forward.png" style="height:20px"><span> '+selectedId+'</span>';
//			this.selectedHost=selectedId;
//			selectedId = 'Hadoop';
//			source = 'resources/host_summary.html';
//			Navbar.doAutoRefresh=true;
//		}
//		else if (tabName == 'nn_detail') 
//		{
//				header = 'NameNode Details : '+selectedId;
//				selectedNameNode = selectedId;
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'	
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'TreeNameNode\',\'nn_summary\');" class="tab_banner" >NameNode Summary</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\''+childtab+'\',\'nn_host\');" class="tab_banner" >'+childtab+'</a></span><img src="images/forward.png" style="height:20px">'	
//							+'<img src="images/forward.png" style="height:20px"><span id="NameNode'+selectedId+'"> '+selectedId+'</span>';
//				
//				source = 'resources/nn_detail.html';
//				selectedNodeTypeForDetailView = "NameNode";
//				selectedId = 'Hadoop';
//				Navbar.doAutoRefresh=true;
//			
//		}
//		else if (tabName == 'dn_summary')
//		{
//				header = 'DataNode Summary';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'	
//							+'<img src="images/forward.png" style="height:20px"><span> DataNode Summary</span>';
//				Navbar.doAutoRefresh=true;
//				source = 'resources/dn_summary.html';
//		}
//		else if (tabName == 'dn_detail') 
//		{
//			if (typeof(grandchildtab) == "undefined")
//			{
//				header = 'DataNode Details : '+selectedId;
//				selectedDataNode = selectedId;
//				
//				pathHeader+=$('#pathbar_div').html();
//				
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'	
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'TreeDataNode\',\'dn_summary\');" class="tab_banner" >DataNode Summary</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> '+selectedId+'</span>';
//				source = 'resources/dn_detail_host.html';
//				selectedNodeTypeForDetailView = "DataNode";
//				selectedId = 'Hadoop';
//				Navbar.doAutoRefresh=true;
//				
//			}
//			else 
//			{
//				header = 'DataNode Volume Details';
//				selectedDataNode = selectedId;
//				pathHeader+=$('#pathbar_div').html();
//				source = 'resources/dn_detail_volume.html';
//				Navbar.doAutoRefresh=true;
//			}
//		}
//		else if(tabName=='rm_detail')
//		{
//				header = 'ResourceManager Details : '+selectedId;
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'MapReduce\');"> MapReduce</a></span><img src="images/forward.png" style="height:20px">'		
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\', \'ResourceManager\');"> ResourceManager</a></span><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px">';
//				Navbar.doAutoRefresh=true;
//				if(typeof(grandchildtab) != "undefined")
//				{
//					pathHeader += '<span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'rm_detail\',\''+childtab+'\');" class="tab_banner">'+childtab+'</a></span><img src="images/forward.png" style="height:20px">'
//								 +'<img src="images/forward.png" style="height:20px"><span>'+grandchildtab+'</span>';
//					selectedNodeTypeForDetailView = "RM";
//				}
//				else{
//					pathHeader+='<span>'+childtab+'</span>';
//				}
//				source = 'resources/rm_detail.html';
//				selectedId = 'Hadoop';
//			source = 'resources/rm_detail.html';
//			
//		}
//		else if(tabName=='nm_detail')
//		{
//			header = 'NodeManager Details : '+selectedId;
//			pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
//						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'MapReduce\');"> MapReduce</a></span><img src="images/forward.png" style="height:20px">'
//						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\', \'NodeManager\');"> NodeManager</a></span><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px">';
//			Navbar.doAutoRefresh=true;
//			if(typeof(grandchildtab) != "undefined")
//			{
//				pathHeader+='<span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'nm_detail\',\''+childtab+'\');">'+childtab+'</a></span><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px">'
//				+'<span>'+grandchildtab+'</span>';
//				selectedNodeTypeForDetailView = "NM";
//			}
//			else
//			{
//				pathHeader+='<span>'+childtab+'</span>';
//			}
//			source = 'resources/nm_detail.html';
//			
//			selectedId = 'Hadoop';
//		}
//		
//		
//		else if (tabName == 'admin') 
//		{
//			if(typeof(childtab) == "undefined")
//			{
//				header = 'Cluster Setup ';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> Admin</span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
//				source = 'resources/automate_cluster_setup.html';
//				Navbar.doAutoRefresh=false;
//			}
//			else if(childtab == 'hosts') 
//			{
//				header = 'Manage Hosts';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/host_config.html';
//				Navbar.doAutoRefresh=true;
//			}
//			else if (childtab == 'queryio_services') 
//			{
//				header = 'QueryIO Services ';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
//				source = 'resources/queryio_services.html';
//				Navbar.doAutoRefresh=true;
//			}
//			else if(childtab == 'BackupAndRecovery') 
//			{
//				header = 'Disaster Recovery';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/backup_recovery.html';
//			}
//			else if (childtab == 'all_reports') 
//			{
//				header = 'System Reports';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span>' + header + '</span>';
//				source = 'resources/reports_all_reports.html';
//			}
//			else if (childtab == 'users') 
//			{
//				header = 'Users & Groups';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				if(Navbar.promptPasswordChange) {
//					source = 'resources/demoUserPasswordChange.html';
//				} else {
//					source = 'resources/admin_users.html';
//				}
//			}
//			else if(childtab == 'report_schedules')
//			{
////				header = childtab == 'all_reports' ? 'Reports':'Schedules';
////				var temp = (childtab == 'all_reports' ? 'All Reports':'Report Schedules');
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span>System Schedules</span>';
//				source = 'resources/reports_' + childtab + '.html';
//			} 			
//			else if (childtab == 'manage_datasources') 
//			{
//				header = 'Manage Data Sources ';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/manage_datasources.html';
//				Navbar.doAutoRefresh=true;
//			}
//			else if(childtab == 'DBConfigMigration') 
//			{
//				header = 'Databases Migration Statistics';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'DB_Config\',\'admin\',\'manage_datasources\');"> Manage Datasources</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/database_migration.html';
//			}
//			else if(childtab == 'reportnotifications')
//			{
//				header = 'Notifications';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'All Reports\',\'admin\', \'all_reports\');"> System Reports</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				
//				source = 'resources/admin_notifications.html';
//			}
//			selectedId = 'Users';
//		}
		else if (tabName == 'help') {
			
				header = 'Help';
	//			pathHeader = 'HDFS >> Admin >> System Config';
				//pathHeader = '<a href="javascript:Navbar.changeTab(\'dashboard\')"><img src="" id="homeImage"></a> <i><b><a href="#" style="text-decoration: underline; color: #222;" onclick="javascript:Navbar.changeTab(\'admin\')">Admin</a> <a style="color: #222;">Help</a></b></i>';
				return;
			
		} else {
		}
		if (source != '') {
			$("#rhs_header").html(header);
			$("#pathbar_div").html(pathHeader);
			//$("#surround_box_div #surround_box span#header").html(pathHeader);
			var appender = '<script>Navbar.setButtonWidth();</script>';
			Navbar.isSetButtonWidth=true;
			Util.importResource("service_ref", source);
			Navbar.objectClicked(selectedId);
			document.getElementById("homeImage").src = Navbar.imgArray[0].src;
		}
		if ((tabName == 'nn_detail') || (tabName == 'dn_detail') || (tabName == 'nm_detail') || (tabName == 'rm_detail'))
		{
			var checkChart = false;
			if((tabName == 'nm_detail') || (tabName == 'rm_detail')){
				if(typeof(childtab) != "undefined"){
					checkChart = true;
				}
			}
			else{
				checkChart = true;
			}
			if(checkChart){
				document.getElementById("chartInterval").getElementsByTagName("option")[0].selected = true;
				document.getElementById("intervalHeader").style.display = '';
			}
		}
		else
		{
			document.getElementById("intervalHeader").style.display = 'none';
			
		}
		$('#refreshViewButton').css("display","block");
		
//		Navbar.doAutoRefreshView();
		
	},
	
	
	doAutoRefreshView : function(){
//		for(var i=0;i<Navbar.timeOutArray.length;i++){
//			clearTimeout(Navbar.timeOutArray[i]);
//		}
//	
//		if(Navbar.doAutoRefresh && !Navbar.disableAutoRefresh){
//			Navbar.interval = setTimeout('Navbar.refreshView()',Navbar.autoRefreshTimeout);
//			Navbar.timeOutArray.push(Navbar.interval);
//		}
	},
	
	clearAllTimeout : function(){
		for(var i=0;i<Navbar.timeOutArray.length;i++){
			clearTimeout(Navbar.timeOutArray[i]);
		}
		Navbar.timeOutArray=[];
	},
	
	objectClicked: function (tdId)
	{
		this.activeMenu=tdId;
		$("#menu_ul li").removeClass("menuactive");
		var id = tdId+'_li';
		$('#'+id).addClass("menuactive");
	},
	
	mouseHover: function (id, isHover)
	{
		if (id.style.backgroundColor != "#535353")	// Equivalent to #ECECEC
		{
			if (isHover)
				id.style.backgroundColor = "#535353";
			else
				id.style.backgroundColor = "rgb(255, 255, 255)";	// White color
		}
	},
	
	changeInterval: function(interval)
	{
		Util.setCookie("TimeInterval",interval,1);
		if (selectedNodeTypeForDetailView == 'NameNode')
			NN_Detail.changeInterval(interval);
		else if(selectedNodeTypeForDetailView == 'RM')
			Resource_Manager.changeInterval(interval);
		else if (selectedNodeTypeForDetailView=='NM')
			Node_Manager.changeInterval(interval);
		else
			DN_Host.changeInterval(interval);
	},
	
	ready: function()
	{
		var totalNodeCount = $('#totaldn').text();
		Navbar.refreshNavBar();
		Navbar.imgArray = new Array();
		Navbar.imgArray[0] = new Image();
		Navbar.imgArray[0].src = 'images/home-icon.png';
		Navbar.imgArray[1] = new Image();
		Navbar.imgArray[1].src = 'images/forward.png';
		
	},
	
	fillNameNodeDropDown : function(list)
	{
		var opt ='';
		if(list!=null && list!=undefined){
			
			for(var i=0;i<list.length;i++){
				var node = list[i];
				opt+='<option value="'+node.id+'">'+node.id+'</option>';
			}
		}
		$('#queryIONameNodeId').html(opt);
		
		if (Navbar.automatedSetup)
		{
			AutoCluster.continueAutomatedSetup();
		}
		
		if(Navbar.promptPasswordChange) {
			Navbar.changeTab('Users','admin','users');
		} else if (isFirstTimeCluster)
		{
			Navbar.changeTab('Data Migration','data', 'data_migration');
		}
	}, 
	
	changeQueryIONameNodeId : function(namenodeId)
	{
		if (this.selectedTabId == "Data Browser")
		{
			$('#db_namenode').val(namenodeId);
			DataBrowser.showDataOfNameNode();
		}
		else if (this.selectedTabId == "DataAnalyzer")
		{
			BQS.ready();
		}
		else if (this.selectedTabId == "BigQueryTables")
		{
			BQT.ready();
		}
		else if (this.selectedTabId == "QueryDesigner"||this.selectedTabId == "QueryViewer")
		{
			DAT.ready();
		}
		else if (this.selectedTabId == "QuerySpreadSheet")
		{
			SPS.ready();
		}
	},
	
	fillAllQueryIONameNode :function()
	{
		//get all host ip and id for populate hostForNode selection box.
		RemoteManager.getNonStandByNodes(Navbar.fillNameNodeDropDown);
	},

	
	setAutoRefreshTimeout : function()
	{
		var value = $("#refreshTimeout").val();
		Navbar.autoRefreshTimeout = parseInt(value)*1000;
		Navbar.interval = setTimeout('Navbar.refreshView()',Navbar.autoRefreshTimeout);
	},
	
	toggleRefresh : function(value)
	{
		if(value)
		{
			$("#pauseRefresh").show();
			$("#resumeRefresh").hide();
			$("#autoRefreshSpan").show();
			Navbar.disableAutoRefresh = false;
			Navbar.interval = setTimeout('Navbar.refreshView()',Navbar.autoRefreshTimeout);
		}
		else
		{
			$("#pauseRefresh").hide();
			$("#resumeRefresh").show();
			$("#autoRefreshSpan").hide();
			Navbar.disableAutoRefresh = true;
			clearTimeout(Navbar.interval);
		}
	},
	
	refreshNavBar : function()
	{
		
		$('#nntree').remove();
		$('#dntree').remove();
		$('#Reports').remove();
		$('#Alerts').remove();
		$('#Admin').remove();
		$('#menu_container').html("");
		$('#menu_container').load('pages/menu.jsp');
		Navbar.fillAllQueryIONameNode();
	},
	setActiveTab : function(){
		this.objectClicked(this.activeMenu);
		
		
	},
	loadNameNodeNavBar : function(){
		RemoteManager.getNameNodes(Navbar.fillAllNameNode);
	},

	fillAllNameNode : function(list)
	{
		var node;
		var content='';
		for (var i = 0; i < list.length; i = i+1)
		{
			node = list[i];
			content+='<li><a id="nn'+node.id+'" href="javascript:Navbar.changeTab(\''+node.id+'\',\'nn_detail\');"  >'+node.id+'</a></li>';
		}
		content+='';
		$("ul#nntree").html(content);
	},


	
	refreshView : function()
	{
		if (Navbar.selectedTabId=='Status')
		{
			var interval = document.getElementById('timeInterval').value;
			StatusManager.getStatus(interval,Status.fillStatus);
			return;
		}else if (Navbar.selectedTabId=='Data')
		{
			DataBrowser.refreshDataTable();
			return;
		}
		else if (Navbar.selectedTabName=='chartMagnified')
		{
			
			var interval = document.getElementById("chartInterval").value;
			Navbar.changeInterval(interval);
			return;
		}else if($("#rhs_header").text()=='Dashboard'){
			Navbar.isRefreshPage = true;
//			/this.selectedTabId = ''
			Navbar.changeTab(Navbar.selectedTabId, Navbar.selectedTabName, Navbar.selectedChildTab, Navbar.selectedGrandChildTab);
			return;
		}else if(Navbar.selectedTabName=="rack_summary"){
			Navbar.isRefreshPage = true;
			Navbar.changeTab(Navbar.selectedRack, Navbar.selectedTabName);
			return;
		}
		Navbar.isRefreshPage = true;
		Navbar.changeTab(Navbar.selectedTabId, Navbar.selectedTabName, Navbar.selectedChildTab, Navbar.selectedGrandChildTab);
	},
	helpClicked : function(){
		window.open("docs/common/index.html");
		return;
	},
//	setDataNodePathbar : function(array){
//		var pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
//			+'<img src="images/forward.png" style="height:20px"><span> HDFS Node</span><img src="images/forward.png" style="height:20px">'
//			+'<img src="images/forward.png" style="height:20px"><a href="javascript:Navbar.changeTab(\'TreeDataNode\',\'dn_summary\');" class="tab_banner" ><span>DataNode Summary</span></a><img src="images/forward.png" style="height:20px">'
//			+'<img src="images/forward.png" style="height:20px"><span> '+array[0]+'</span><img src="images/forward.png" style="height:20px">'
//			+'<img src="images/forward.png" style="height:20px"><span> '+array[1]+'</span><img src="images/forward.png" style="height:20px">'
//			+'<img src="images/forward.png" style="height:20px"><span> '+array[2]+'</span>';
//		$("#pathbar_div").html(pathHeader);	
//	},
//	setHostPathbar : function(array){
////		var pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
////			+'<img src="images/forward.png" style="height:20px"><a href="javascript:Navbar.changeTab(\'TreeDataNode\',\'dn_summary\');" class="tab_banner" ><span>DataNode Summary</span></a><img src="images/forward.png" style="height:20px">'
////			+'<img src="images/forward.png" style="height:20px"><span><a id="rack'+array[0]+'" href="javascript:Navbar.changeTab(\''+array[0]+'\',\'rack_summary\');"  > '+array[0]+'</a></span><img src="images/forward.png" style="height:20px">'
////			+'<img src="images/forward.png" style="height:20px"><span> '+array[1]+'</span><img src="images/forward.png" style="height:20px">'
////			
////		$("#pathbar_div").html(pathHeader);	
//	},
	setDataNodePathbar : function(array){
		var pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'TreeDataNode\',\'dn_summary\');" class="tab_banner" >DataNode Summary</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a id="rack'+array[0]+'" href="javascript:Navbar.changeTab(\''+array[0]+'\',\'rack_summary\');"  > '+array[0]+'</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a id="rack'+array[1]+'" href="javascript:Navbar.changeTab(\''+array[1]+'\',\'host_summary\');"  > '+array[1]+'</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span id="DataNode'+array[2]+'"> '+array[2]+'</span>'
			
		$("#pathbar_div").html(pathHeader);
	},
	showHostPath : function(array){
		var pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="" id="homeImage"></a><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'TreeDataNode\',\'dn_summary\');" class="tab_banner" >DataNode Summary</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a id="rack'+array[0]+'" href="javascript:Navbar.changeTab(\''+array[0]+'\',\'rack_summary\');" class="tab_banner" > '+array[0]+'</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span id="host'+array[1]+'"> '+array[1]+'</span>'
			
		$("#pathbar_div").html(pathHeader);
	},
	gettingStarted: function()
	{
//		window.open('resources/getting_Started.html','location=no','width=850,height=580,left=200,top=120');
//		var w = 850;
//		var h = 580;
//		var left = (screen.width/2)-(w/2);
//		var top = (screen.height/2)-(h/2);
//		window.open('resources/getting_Started.html','location=no','width=' + w + ', height=' + h + ', top=' + top + ', left=' + left + "'");
//		Util.addLightbox("GS","resources/getting_Started.html",null,null);
		window.open("docs/hadoop-big-data-tutorial/index.html", 'gettingStarted', 'toolbar=no,location=no,titlebar=no,status=no,menubar=no,titlebar=no,fullscreen=no,width=1500,height=750,top=100,left=100');
		return;
	},
	
	changeView: function(view)
	{
		Navbar.currentSelectedView = view;
		
		switch(view){
		case 1:
			$('#page1').css('display','');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page5').css('display','none');
			$('#page6').css('display','none');
			$('#page7').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').attr('disabled','disabled');
			break;
		case 2:
			$('#page2').css('display','');
			$('#page1').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page5').css('display','none');
			$('#page6').css('display','none');
			$('#page7').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 3:
			$('#page3').css('display','');
			$('#page2').css('display','none');
			$('#page1').css('display','none');
			$('#page4').css('display','none');
			$('#page5').css('display','none');
			$('#page6').css('display','none');
			$('#page7').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 4:
			$('#page4').css('display','');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page1').css('display','none');
			$('#page5').css('display','none');
			$('#page6').css('display','none');
			$('#page7').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 5:
			$('#page5').css('display','');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page6').css('display','none');
			$('#page7').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 6:
			$('#page6').css('display','');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page5').css('display','none');
			$('#page7').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 7:
			$('#page7').css('display','');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page6').css('display','none');
			$('#page5').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 8:
			$('#page8').css('display','');
			$('#page7').css('display','none');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page6').css('display','none');
			$('#page5').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 9:
			$('#page9').css('display','');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#page8').css('display','none');
			$('#page7').css('display','none');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page6').css('display','none');
			$('#page5').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 10:
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','');
			$('#page8').css('display','none');
			$('#page7').css('display','none');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page6').css('display','none');
			$('#page5').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 11:
			$('#page9').css('display','none');
			$('#page11').css('display','');
			$('#page10').css('display','none');
			$('#page8').css('display','none');
			$('#page7').css('display','none');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page6').css('display','none');
			$('#page5').css('display','none');
			$('#nextButton').attr('disabled','disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		}
	},
	
	nextView : function()
	{
		if(Navbar.currentSelectedView < 11)
			Navbar.changeView(Navbar.currentSelectedView + 1);
	},
	
	previousView : function()
	{
		if(Navbar.currentSelectedView > 1)
			Navbar.changeView(Navbar.currentSelectedView - 1);
	},
	
	closeBox: function(){
		window.close();
	},
	
	getPreferredRowCountForDataTable : function(tableName)
	{
		var selector = "[name=" + tableName + "_length]";
	 	$($(selector)[0]).change(function()
	   			{
	   				Navbar.dataTablePreferredRowCount[tableName] = $($(selector)[0]).val();
	   			});
	 	
	 	if(Navbar.dataTablePreferredRowCount[tableName] == undefined || Navbar.dataTablePreferredRowCount[tableName] == null || Navbar.dataTablePreferredRowCount[tableName] == "" )
		{
			Navbar.dataTablePreferredRowCount[tableName] = $($(selector)[0]).val();
		}
		$($(selector)[0]).val(Navbar.dataTablePreferredRowCount[tableName]);
	},
	showServerLog : function(){
		window.open("GetServerLog?file-type=log");
	},
	
	setButtonWidth: function()
	{
		var allWidth = new Array(); 
		var buttonArray = $("#service_ref .button");
		for(var j=0;j<buttonArray.length;j++)
		{
			var e=buttonArray[j];
			if(e.id=='createFSimage')continue;
 			allWidth.push(e.offsetWidth);
 			
		}
		
		if (allWidth.length>0)
		{
			
			var largest = Math.max.apply(Math, allWidth);
			largest = largest + 4;
			largest = largest + 'px';
			
			for(var i=0;i<buttonArray.length;i++)
			{
				buttonArray[i].style.width=largest;
			}
		}
		
//		console.log("Navbar.setButtonWidth");
		
		$('#createFSimage').css('width', '90px');
		$('#createFSimage').css('background-color', '#5DA423');
		$('#createFSimage').css('border', '1px solid #396516');
	},
	
	readmeClicked : function(){
		window.open("README.html");
		return;
	},
};