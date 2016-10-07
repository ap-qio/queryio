Host_Summary={
		hostID :'',
		ready : function(){
			
			RemoteManager.getHostRack(Navbar.selectedHost,Navbar.showHostPath);
			this.hostID=parseInt(Navbar.selectedHost);
			RemoteManager.getAllDataNodesSummaryTableforHost(Navbar.selectedHost,false,Host_Summary.populateHostDetailSummaryTable);
			RemoteManager.getAllDataNodeStatusSummaryforHost(Navbar.selectedHost,Host_Summary.fillStatusTable);
			RemoteManager.getAllDataNodeMemoryInfoForHost(Navbar.selectedHost,Host_Summary.fillSummaryChart);
			RemoteManager.getAllDataNodesSummaryTable(false,Host_Summary.setDataSummaryValue);
			DN_Summary.checkEnableDisable();
		},
				
				setDataSummaryValue : function(summaryTable)
				{
					DN_Summary.dataNodeList = summaryTable.rows;
				},
				
				fillStatusTable : function(list)
				{
					if(document.getElementById('dn_status_table_div')==undefined||document.getElementById('dn_status_table_div')==null)return;
			    	var div_data='<div id="host_status_title" class="header" style="width: 99%">Status summary</div>';
			    	
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
					if(document.getElementById('host_summary_chart')==undefined||document.getElementById('host_summary_chart')==null)return;
					if (list == null || list == undefined)
					{
						$('#host_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
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
						
						$('#host_summary_chart').append('<div style="margin: 30px 0 0 15px; height:200px; background: url(images/noData.png) no-repeat"></div>');
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

						$.jqplot("host_summary_chart", [seriesArray], {
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
				populateHostDetailSummaryTable :function(summaryTable)
				{
					
				
					if(document.getElementById('host_summary_table_div')==undefined||document.getElementById('host_summary_table_div')==null)return;
					if (summaryTable == null || summaryTable == undefined)
					{
						$("#host_summary_table_div").html('<span>DataNode Details not available. </span>');
						return;
					}
					
					var flag=true;
//					var diskstatusArray = new Array();
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
							table_data+='<td><input type="checkbox" value="node-1" onClick="javascript:DN_Summary.clickCheckBox(this.id)" id="node'+row[row.length-1]+'" ></td><td>';
							
							table_data+='<a id="'+row[0] +'" href="javascript:Navbar.changeTab(\''+row[0] +'\',\'dn_detail\');" >';
							if(row[row.length-4]=='Started'){
								table_data+='<img id ="statusStart" alt="" src="images/status_start.png" class="statusImage" >'+row[0]+'<br></a> &nbsp;'+row[1] +'</td>';	
							}
							else if(row[row.length-4]=='Stopped'){
								table_data+='<img id ="statusStop" alt="" src="images/status_stop.png" class="statusImage" >'+row[0]+'<br></a> &nbsp;'+row[1] +'</td>';
							}
							else{
								table_data+='<img id ="noStatus" alt="" src="images/no_status.png" class="statusImage" >'+row[0]+'<br>&nbsp;</a>'+row[1] +'</td>';
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
					}

					$('#host_summary_table').html(table_data);
					$("#host_summary_table").treeTable(
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
				}
				
		
};