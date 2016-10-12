DN_Summary = {
		dataNodeList : [],
		timer : [],
		timerFlag : false,
		
		fillStatusTable : function(list)
		{
			if(document.getElementById('dn_status_table_div')==undefined||document.getElementById('dn_status_table_div')==null)return;
	    	var div_data='<div id="dn_status_title" class="table_header_div" style="width: 98.8%;">Status summary</div>';
	    	
	    	if (list == null || list == undefined)
			{
	    		$("#dn_status_table_div").html('<span>DataNode Details not available. </span>');
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
	        $("#dn_status_table_div").html(div_data);
		},
		
		fillSummaryChart : function(list)
		{
			if(document.getElementById('dn_summary_chart')==undefined||document.getElementById('dn_summary_chart')==null)return;
			if (list == null || list == undefined)
			{
				$('#dn_summary_chart').append('<div style="margin: 38px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
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
				
				$('#dn_summary_chart').append('<div style="margin: 38px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
			}
			else
			{
				while(list[1].indexOf(",")>-1){
					list[1] = list[1].replace(",","");
				}
				while(list[0].indexOf(",")>-1){
					list[0] = list[0].replace(",","");
				}
				var memSize = ' GB ';
				if(list[0]>1024||list[1]>1024)
				{
					memSize = ' TB ';
					list[0] = list[0]/1024;
					list[1] = list[1]/1024;
				}
				var list1 = parseFloat(list[1]);
				var list0 = parseFloat(list[0]);
				var usedMem = list1;
				usedMem = usedMem * 100;
				usedMem = Math.round(usedMem);
				usedMem = usedMem / 100;
				
				seriesArray.push(['Used', usedMem]);
				
				var usedPer = ((list1/list0)*100);
				usedPer = usedPer * 100;
				usedPer = Math.round(usedPer);
				usedPer = usedPer / 100;
				
				labels.push('Used ' + usedMem + memSize );
				colors.push('#EAA228');
				
				var freeMem = (list0-list1);
				freeMem = freeMem * 100;
				freeMem = Math.round(freeMem);
				freeMem = freeMem / 100;
				
				seriesArray.push(['Free', freeMem]);

				var freePer = (((list0-list1)/list0)*100);
				freePer = freePer * 100;
				freePer = Math.round(freePer);
				freePer = freePer / 100;
				
				labels.push('Free ' + freeMem + memSize);
				colors.push('#579575');

				$.jqplot("dn_summary_chart", [seriesArray], {
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
		
		populateDataDetailSummaryTable :function(summaryTable)
		{
			DN_Summary.timerFlag = false;
			if(document.getElementById('dn_summary_table_div')==undefined||document.getElementById('dn_summary_table_div')==null)return;
			if (summaryTable == null || summaryTable == undefined)
			{
				$("#dn_summary_table_div").html('<span>DataNode Details not available. </span>');
				return;
			}
			DN_Summary.dataNodeList = summaryTable.rows;
			
			var flag=true;
//			var diskstatusArray = new Array();
			var diskStatusMap = new Object();
			
			
			var colList=summaryTable.colNames;
			var list = summaryTable.rows;
			
			
			var table_data = '<thead><tr>';
			table_data+='<th><input type="checkbox" value="node-1" onclick="javascript:DN_Summary.selectAllHostRow(this)" id="selectAll" ></th>';
			for(var i=0;i<colList.length-2;i++){
				if(colList[i]=='Host')continue;
				table_data+='<th>'+colList[i]+'</th>';
			}
			table_data+='</tr></thead>';
			var row='';
			var last=0;
			var flag = true;
			
			var statusValue=0;
			if(list.length==0){
				table_data+='<tr><td style="text-align:center;" colspan="'+colList.length+'">No Data available</td></tr>'
			}
			
			for(var i=0;i<list.length;i++){
				row=list[i];
				flag =true;
				statusValue=0;
				table_data +='<tr id = parent-'+(i+1);
				
				if(i!=0&&list[i-1][0]==row[0])
				{
					if(last==0){
						last = i; 
					}
					table_data+=' class = "child-of-parent-'+last+'">';
					table_data+='<td></td>';
					table_data+='<td></td>';
					flag=false;
				}
				else{
					last =0;
					table_data+='>';
					var dnid = row[row.length-1];
					dataNodeIdArray.push(dnid);
					table_data+='<td><input type="checkbox" value="node-1" onClick="javascript:DN_Summary.clickCheckBox(this.id)" id="node'+row[row.length-1]+'" ></td><td>';
					
					table_data+='<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'dn_detail\');" >';
					if(row[row.length-4]=='Started'){
						table_data+='<img id ="statusStart" alt="" src="images/status_start.png" class="statusImage" >'+row[0]+'<br> &nbsp;</a><a id="host'+row[1]+'" href="javascript:Navbar.changeTab(\''+row[1]+'\',\'host_summary\');">'+row[1] +'</a></td>';	
					}
					else if(row[row.length-4]=='Stopped'){
						table_data+='<img id ="statusStop" alt="" src="images/status_stop.png" class="statusImage" >'+row[0]+'<br> &nbsp;</a><a id="host'+row[1]+'" href="javascript:Navbar.changeTab(\''+row[1]+'\',\'host_summary\');">'+row[1] +'</a></td>';
					}
					else{
						table_data+='<img id ="noStatus" alt="" src="images/no_status.png" class="statusImage" >'+row[0]+'<br>&nbsp;</a><a id="host'+row[1]+'" href="javascript:Navbar.changeTab(\''+row[1]+'\',\'host_summary\');">'+row[1] +'</a></td>';
					}

				}
				
				for(var j=2;j<row.length-1;j++)
				{
				
					
					
					if(colList[j] == 'Disk')
					{
                        if(flag)
                        {
                        	table_data+='<td><img id ="nodeStatusHealthy'+row[row.length-1]+'" alt="" src="images/status_start.png" class="statusImage" style="display:none;" ><img id ="nodeStatusFailure'
                        		+ row[row.length-1]+'" alt="" src="images/status_stop.png" class="statusImage" style="display:none;"  ><img id ="nodeStatusNotDefined'
                        		+ row[row.length-1]+'" alt="" src="images/no_status.png" class="statusImage" style="display:none;" >'+row[j]+'</td>';
                        	
                        	 if(row[row.length-2]=='Healthy')
 	                         {
 	    						statusValue=1;
 	    				    }
 	    				 	else if(row[row.length-2] == "N/A")
 	    					{
 	    						statusValue=2;
 	    						
 	    					}
 	    					else if(row[row.length-2] == "")
 	    					{
 	    						statusValue=0;
 	    						
 	    					}
 	    					else{
 	    						statusValue=3;
 	    					}
                        }
                        else
                        {
							table_data+='<td  style="width : 12%; word-break: break-all;" >';
							
	                        if(row[row.length-2]=='Healthy')
	                        {
	    						table_data+='<img id ="nodeStatusHealthy" alt="" src="images/status_start.png" class="statusImage" >'+row[j]+'</a></td>';	
	    						statusValue=1;
	    					}
	    					else if(row[row.length-2] == "N/A")
	    					{
	    						table_data+='<img id ="nodeStatusNotDefined" alt="" src="images/no_status.png" class="statusImage" >'+row[j]+'</a></td>';
	    						statusValue=2;
	    						
	    					}
	    					else
	    					{
	    						table_data+='<img id ="nodeStatusFailure" alt="" src="images/status_stop.png" class="statusImage" >'+row[j]+'</a></td>';
	    						statusValue=3;
	    						
	    					}
                        }
                    }else if(colList[j]=='Rack Name'){
                    	table_data+='<td><a id="rack'+row[j]+'" href="javascript:Navbar.changeTab(\''+row[j]+'\',\'rack_summary\');">'+row[j]+'</a></td>';
                    }else if(colList[j]=='Monitoring'){
                    	table_data+='<td id="monitorStatus'+row[row.length-1]+'" >'+row[j]+'</td>';
                    }
					else if(colList[j] == 'Volume')
					{
                        table_data+='<td  style="width : 12%; word-break: break-all;" >'+row[j]+'</td>';
                    }
                    else if(colList[j] == 'Status')
                    {
                    	 table_data+='<td id="node.status'+row[row.length-1]+'" >'+row[j]+'</td>';
                    }
                    else if(colList[j] != 'Disk Health Status')
                    {
                        table_data+='<td>'+row[j]+'</td>';
                    }
				}
				if(statusValue<diskStatusMap[row[row.length-1]]){
					statusValue=diskStatusMap[row[row.length-1]];
				}
				diskStatusMap[row[row.length-1]]=statusValue;
				table_data +='</tr>';
				
				if(row[18] == "Launching") {
					DN_Summary.timerFlag = true;
				}
					
				
			}
			table_data+='';
			
			
			$('#dn_summary_table').html(table_data);
			$("#dn_summary_table").treeTable(
					{
						expandable: true,
						clickableNodeNames: false
					});
			$('.expander').css('margin-left','-13px');
			for ( var nodeId in diskStatusMap) {
				
				if(diskStatusMap[nodeId]==1){
					$('#nodeStatusHealthy'+nodeId).css('display','');
				}else if(diskStatusMap[nodeId]==2){
					$('#nodeStatusNotDefined'+nodeId).css('display','');
				}else{
					$('#nodeStatusFailure'+nodeId).css('display','');
				}
			}
			
		   	//disabled the checkAll button when no data in the list
		   	if( summaryTable.rows.length == 0 || summaryTable == null || summaryTable == undefined )
				$('#selectAll').attr('disabled' , true);
			else
				$('#selectAll').removeAttr('disabled');
		   	
		   	DN_Summary.updateDNTable();

		},
		
		
	ready: function () {
		DN_Summary.checkEnableDisable();
		RemoteManager.getAllDataNodesSummaryTable(false,DN_Summary.populateDataDetailSummaryTable);
		RemoteManager.getAllDataNodeStatusSummary(DN_Summary.fillStatusTable);
		RemoteManager.getAllDataNodeMemoryInfo(DN_Summary.fillSummaryChart);
		
	},
	
	checkEnableDisable: function(flag){
		if(selectedHdfsNode.length>0){
			dwr.util.byId('delete.service').disabled=false;
			$('#delete.service').removeClass("ui-state-disabled");
			if(selectedHdfsNode.length==1){
				dwr.util.byId('decommission.service').disabled=flag;
				document.getElementById('config.service').disabled = false;
			}
			else{
				dwr.util.byId('decommission.service').disabled=true;
				document.getElementById('config.service').disabled = true;
			}
		}
		else{
			dwr.util.byId('start.service').disabled=true;
			dwr.util.byId('stop.service').disabled=true;
			dwr.util.byId('start.monitoringservice').disabled=true;
			dwr.util.byId('stop.monitoringservice').disabled=true;
			dwr.util.byId('delete.service').disabled=true;
			dwr.util.byId('decommission.service').disabled=true;
			$('#start.service').addClass("ui-state-disabled");
			$('#stop.service').addClass("ui-state-disabled");
			$('#delete.service').addClass("ui-state-disabled");
			$('#decommission.service').addClass("ui-state-disabled");
			document.getElementById('config.service').disabled = true;
		}
	},
	
	clickCheckBox : function(chkbxid)
	{
		var serviceId=chkbxid.substring(4,chkbxid.length);
		dwr.util.byId('start.service').disabled=false;
		dwr.util.byId('stop.service').disabled=false;
		dwr.util.byId('start.monitoringservice').disabled=false;
		dwr.util.byId('stop.monitoringservice').disabled=false;
		var flag = true;
		if(dwr.util.byId(chkbxid).checked)
		{
			if(selectedHdfsNode.indexOf(serviceId)==-1)
			selectedHdfsNode.push(serviceId);
			
			dwr.util.byId('start.service').disabled=false;
			dwr.util.byId('stop.service').disabled=false;
			
			dwr.util.byId('start.monitoringservice').disabled=false;
			dwr.util.byId('stop.monitoringservice').disabled=false;
			
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
			}
			
			for(var i=0; i<DN_Summary.dataNodeList.length; i++)
			{
				if(DN_Summary.dataNodeList[i][18] == "")
					continue;
				if(DN_Summary.dataNodeList[i][0] == serviceId && DN_Summary.dataNodeList[i][18] != "Started")
				{
					flag = true;
					break;
				}
				if(DN_Summary.dataNodeList[i][0] != serviceId && DN_Summary.dataNodeList[i][18] == "Started")
				{
					flag = false;
				}
			}
		}
		else
		{
			var index = selectedHdfsNode.indexOf(serviceId);
			selectedHdfsNode.splice(index, 1);
			for(var i=0;i<selectedHdfsNode.length;i++)
			{
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
				}else{
					dwr.util.byId('start.service').disabled=true;
				}
			}
		}
		DN_Summary.enableDisableSelectAllButton();
		DN_Summary.checkEnableDisable(flag);
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
	 				DN_Summary.clickCheckBox(e.id);
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
	
	addHdfsNode :function()
	{
		RemoteManager.getAllHostDetails(DN_Summary.evaluateHostList);	
	},
	
	evaluateHostList : function(response)
	{
		 if(response.length == 0)
		 {
			 Util.addLightbox("adddn", "resources/new_host_box_dn.html", null, null);
		 }
		 else
		 {
			Util.addLightbox("adddn", "resources/add_dn.html", null, null);
		 }
	},
	closeHostBox : function(){
		Util.removeLightbox("adddn");
		RemoteManager.getAllHostDetails(DN_Summary.checkHost_forCloseHostBox);	
	},
	
	checkHost_forCloseHostBox: function(response)
	{
		if(response.length != 0)
		 {
			DN_Summary.addHdfsNode();
		 }
	},
	
	closeBox : function(isRefresh)
	{
		Util.removeLightbox("adddn");
		if(isRefresh)
		{
			RemoteManager.getTreeDetails(Dashboard.fillAllDataNode);
			Navbar.refreshView();
		}
	},
	
	addNewHost : function()
	{
		Navbar.isRefreshPage = true;
		Util.removeLightbox("adddn");
		Util.addLightbox("adddn", "resources/new_host_box_dn.html", null, null);
	},
	showConfiguration : function()
	{
		Navbar.isRefreshPage = true;
		config_nodeId = selectedHdfsNode[0];
		isCallFromNameNode = false;
		Util.importResource("service_ref","resources/nn_config.html");

	},
	addNewRack :function(){
		
		Util.addLightbox("addrac", "resources/new_rack_box.html", null, null);
		
	},
	saveNewRack : function(){
		
		var rac_name = $('#rackName').val();
		if(rac_name==''){
			jAlert("rack Name was not defined.Please defined a racName","Incomplete Detail");
			return;
		}
		RemoteManager.insertRack(rac_name,DN_Summary.fillAllRacks);
		
		DN_Summary.closeRacBox();
	},
	closeRacBox : function(){
		Util.removeLightbox("addrac");
	},
	fillAllRacks : function(){
		RemoteManager.getAllRacks(fillRack);
	},
	generateUniqueId : function(){
		var totaldn = $('#totaldn').text();
		$('#id').val('DataNode'+totaldn);
		$('#id').val($('#id').val().trim());
	},
	
	drawDataNodeTable : function()
	{
		var keyList = new Array();
		keyList.push("dfs.datanode.address");
		keyList.push("dfs.datanode.https.address");
		keyList.push("dfs.datanode.http.address");
		keyList.push("dfs.datanode.ipc.address");
		keyList.push("queryio.datanode.options");
		
		RemoteManager.getConfigurationServerPort(keyList,DN_Summary.drawDataNodeConfigurationTable);
		
		
	},
	drawDataNodeConfigurationTable : function(map){
		
		
		
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
		
		var colList = [
		   			{ "sTitle": "Port Title" },
					{ "sTitle": "Port Number" },
					{ "sTitle": "Description" }
					];
		
		var rowList =  [
		    			[ "Server Port", '<input type="text" id="serverPort" value="'+serverPort+'">', 'Port on which DataNode server will listen to.'],
		    			[ "HTTP Port", '<input type="text" id="httpPort" value="'+http+'">', 'The DataNode http port.'],
		    			[ "HTTPS Port", '<input type="text" id="httpsPort" value="'+https+'">', 'The DataNode secure http port.'],
		    			[ "IPC Port", '<input type="text" id="ipcPort" value="'+ipc+'">', 'Inter-process communocation port.'],
		    			[ "JMX Port", '<input type="text" id="jmxPort" value="'+jmx+'">', 'JMX monitoring port.']
		    			];
		$('#dataNodeTable').dataTable({
			"bPaginate": false,
			"bLengthChange": true,
			"bFilter": false,
			"bDestroy": true,
			"bSort": false,
			"bInfo": false,
			"bAutoWidth": false,
	        "aaData":rowList,
			"aoColumns":colList 
	    });
	},
	
	updateDNTable : function()
	{
		if(DN_Summary.timerFlag) {
			var timerProcess = setTimeout(function() { RemoteManager.getAllDataNodesSummaryTable(false,DN_Summary.populateDataDetailSummaryTable);  } ,1000);
			DN_Summary.timer.push(timerProcess);
		} else 	{
			for(var i=0;i<DN_Summary.timer.length;i++){
				clearTimeout(DN_Summary.timer[i]);
			}
		}
	},
};