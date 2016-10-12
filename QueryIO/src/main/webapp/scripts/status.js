Status = {
		CELL_EMPTY:0,
		CELL_SUSPENDED : 1,
		CELL_OK :2,
		CELL_WARNING :3,
		CELL_ERROR :4,
		CELL_FAILURE:5,
	    ready: function ()
	    {
	    	var timeInterval = 'onehour';
	    	var selectedInterval=Util.getCookie("TimeInterval");
			if(selectedInterval!=undefined){
				timeInterval = selectedInterval;
				$('#timeInterval').val(selectedInterval);
			}
			StatusManager.getStatus(timeInterval,Status.fillStatus);
	    },
	    
	    getStatusColor : function (state){
	    	state = parseInt(state);
	    	var cellColorClass='';
	    	switch (state)
			{
				case Status.CELL_EMPTY: //Constant to define EMPTY cell
				{
					cellColorClass = "empty_dashbrdcell_td";
					break;
				}
				case Status.CELL_OK: //Constant to define OK cell
				{
					cellColorClass = "ok_dashbrdcell_td";
					break;
				}
				case Status.CELL_WARNING:	//Constant to define WARNING cell
				{
					cellColorClass = "warning_dashbrdcell_td";
					break;
				}
				case Status.CELL_ERROR: //Constant to define ERROR cell
				{
					cellColorClass = "error_dashbrdcell_td";
					break;
				}
				case Status.CELL_FAILURE:	//Constant to define POLLING cell
				{
					cellColorClass = "polling_dashbrdcell_td";
					break;
				}
				case Status.CELL_SUSPENDED: //Constant to define EMPTY cell
				{
					cellColorClass = "suspended_dashbrdcell_legend";
					break;
				}
				default:
				{
					cellColorClass = "empty_dashbrdcell_td";
					break;
				}
			}
	    	return cellColorClass;
	    },
	    
	    fillStatus: function (list)
	    {
	    	
	    	if(document.getElementById('status_tree_table')==undefined||document.getElementById('status_tree_table')==null)return;
	    	if(list==null||list==undefined||list.length==0){
	    		$('#status_tree_table').html('<tr><td style="text-align: center;"><span>Status details not available.</span></td></tr>');
	    		return;
	    	}
	    	
	    	var flag=true;
			var table_data = '';
			var host ='';
			for(var i=0;i<list.length;i++){
				
				 host =list[i];
				table_data +='<tr id = parent-'+(i+1)+'>';
				table_data +='<td width="10%" style="background-color: #EDE8EA;">'+host.name+'</td>';
				if(host.dashboardCells!=undefined||host.dashboardCells!=null){
					var status = host.dashboardCells;
					for(var m = status.length-1;m>=0;m--){
						var className = Status.getStatusColor(status[m]['state']);
						table_data += '<td width = "3%" class="'+className+'" style="border: 1px solid white; cursor: default;">&nbsp;';
						table_data+='</td>'
					}	
				}
				
				table_data +='</tr>';
				if(host.childs!=undefined||host.childs!=null){
					var childs = host.childs;
					for(var j = 0 ;j<childs.length;j++){
						var child  = childs[j];
						var hlink = '';
						var name = '';
						if(child.nodeType=='namenode'){
							name = child.name;
							hlink = 'javascript:Navbar.changeTab(\''+name+'\',\'nn_detail\',\''+host.name+'\');';							
						}else if(child.nodeType =='datanode'){
							name = child.name;
							hlink = 'javascript:Navbar.changeTab(\''+name+'\',\'dn_detail\');';
						}else if(child.nodeType=='resourcemanager'){
							name = child.name;
							hlink = 'javascript:Navbar.changeTab(\'Hadoop\',\'rm_detail\',\''+host.name+'\',\''+name+'\');';							
						}else if(child.nodeType =='nodemanager'){
							name = child.name;
							hlink = 'javascript:Navbar.changeTab(\'Hadoop\',\'nm_detail\',\''+host.name+'\',\''+name+'\');';
						}
						
					table_data +='<tr id = "parent'+child.name+'-'+(i+1)+'" class="child-of-parent-'+(i+1)+'">';
					table_data+='<td  style="background-color: #EDE8EA; cursor: hand;"  width = "10%" onclick="'+hlink+'">'+name+'</td>';
					if(child.dashboardCells!=undefined||child.dashboardCells!=null){
						var statuscell = child.dashboardCells;
						for(var k = statuscell.length-1 ; k>=0;k--){
							var className = Status.getStatusColor(statuscell[k]['state']);
							table_data += '<td width = "3%" class="'+className+'" style="border: 1px solid white;" onclick="'+hlink+'"> &nbsp;';
							table_data+='</td>'
						}
						table_data+='</tr>';
					}
					
					table_data +='</tr>'
				}
				}
			}
			table_data+='<tr><td width = "10%" style="border: 1px solid white;"></td>';
			if(host.dashboardCells!=undefined||host.dashboardCells!=null){
				var status = host.dashboardCells;
				for(var m = status.length-1;m>=0;m--){
					table_data += '<td width = "3%"  style="border: 1px solid white; word-wrap: break-word;  padding: 0;">';
					table_data+=Status.getTimeString(status[m]['startTime']);
					table_data+='</td>'
				}	
			}	

			table_data+="</tr>";
			$('#status_tree_table').html(table_data);
			$("#status_tree_table").treeTable(
					{
						expandable: true,
						initialState :"expanded",
						clickableNodeNames :true
						
					});
			$('.expander').css('margin-left','-13px');
	    },
	    getTimeString : function(timeStamp){
	    	
	    	var time = '';
	    	var d = new Date(timeStamp);
	    	var interval ='';
	    	if(document.getElementById('timeInterval')==null){
	    		interval = 'onehour';
	    	}else{
	    		interval = document.getElementById('timeInterval').value;	
	    	}
//	    	interval = document.getElementById('timeInterval').value;
	    	if(interval=='onemonth'||interval=='quarter'||interval=='halfyear'){
	    		var date  =d.getDate().toString();
	    		if(date.length==1){
	    			date="0"+date;
	    		}
	    		time+=date;
	    		time+="-";
	    		var month =(d.getMonth()+1).toString();
	    		if(month.length==1){
	    			month="0"+month;
	    		}
	    		time+=month;
		    	time+=" ";
	    	}
	    	else if(interval=='oneyear'){
	    		var date  =d.getDate().toString();
	    		if(date.length==1){
	    			date="0"+date;
	    		}
	    		time+=date;
	    		time+="-";
	    		var month =(d.getMonth()+1).toString();
	    		if(month.length==1){
	    			month="0"+month;
	    		}
	    		time+=month;
		    	time+="-"
	    		time+=((d.getFullYear()).toString()).substring(2);
		    	time+=" ";
	    	}
	    	else if(interval=='oneday'||interval=='onehour'){
	    		var hrs  =d.getHours().toString();
	    		if(hrs.length==1){
	    			hrs="0"+hrs;
	    		}
	    		time+=hrs;
	    		time+=":";
		    	var min = d.getMinutes().toString();
		    	if(min.length==1){
		    		min ="0"+min;
		    	}
	    		time+=min;
//		    	time+=": ";
//		    	time+=d.getSeconds();
	    	}
	    	else if(interval=='oneweek'){
	    		var date  =d.getDate().toString();
	    		if(date.length==1){
	    			date="0"+date;
	    		}
	    		time+=date;
	    		time+="-";
	    		var month =(d.getMonth()+1).toString();
	    		
	    		if(month.length==1){
	    			month="0"+month;
	    		}
	    		time+=month;
		    	time+=" ";
		    	var hrs =d.getHours().toString();
	    		if(hrs.length==1){
	    			hrs="0"+hrs;
	    		}
	    		time+=hrs;
		    	time+=":";
		    	var min = d.getMinutes().toString();
		    	if(min.length==1){
		    		min ="0"+min;
		    	}
	    		time+=min;
	    	}
	    	return time;
	    },
	    
	    changeInterval : function(){
	    	if(document.getElementById('timeInterval')==null){
	    		return;
	    	}
	    	
	    	var interval = document.getElementById('timeInterval').value;
//	    	interval = $('#timeInterval').val;
			Util.setCookie("TimeInterval",interval,1);
	    	StatusManager.getStatus(interval,Status.fillStatus);
	    }
};