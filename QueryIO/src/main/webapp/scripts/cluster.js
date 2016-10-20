Cluster = {
		primaryRack:[],
		replicationRack:[],
		ready : function(){
//			var list = ["Rack1","Rack2"];
//			Cluster.fillAllCluster(list);
//			Cluster.fillDefaultRackTable(["192.168.0.1","192.168.0.2","192.168.0.3"]);
//			Cluster.fillReplicationRackTable(["192.168.0.4","192.168.0.5","192.168.0.6"]);
//			
			RemoteManager.getAllClusters(Cluster.fillAllCluster);
			

		},
		
		fillAllCluster : function(list){
			if(list==null){
				return
			}
			var select_data ="";
			for(var i=0;i<list.length;i++)
			{
				var cluster = list[i];
				select_data+='<option value='+cluster.id+'>'+cluster.name+'</option>';	
				
			}
			$("#cluster_select").html(select_data);	
			Cluster.fillRackTable();
				
		},
		
		fillRackTable : function(){
			var clusterId = $('#cluster_select').val();
			RemoteManager.getDefaultRackDetails(parseInt(clusterId),Cluster.fillDefaultRackTable);
			RemoteManager.getReplicationRackDetails(parseInt(clusterId),Cluster.fillReplicationRackTable);
		},
		
		fillDefaultRackTable : function(list){
			
			if(list==null){
				return
			}
			if(document.getElementById('default_Rack')==undefined||document.getElementById('default_Rack')==null)return;
			$('#default_Rack').remove();
			$('#default_Rack_div').html('<table id="default_Rack" style="font-size: 9pt; "></table>');
			
			var tableList=[];
			for(var i=0;i<list.length;i++){
			var obj = new Object();
			obj["0"]='<div><img alt="" src="images/Rack.png"><span><a href="javascript:Navbar.changeTab(\''+list[i]+'\',\'dn_detail\');">'+list[i]+'</a></span><button class="button" onclick="javascript:Cluster.movePrimaryDCtoSeconaryDC(\''+list[i]+'\');" style="float: right; margin-top: 10PX;">Move to Replication Rack</button></div>';
			obj["DT_RowId"]='p'+list[i];
				tableList.push(obj);
				
//				tableList.push(['<div><img alt="" src="images/Rack.png"><span>'+list[i]+'</span><button class="button" onclick="javascript:Cluster.movePrimaryDCtoSeconaryDC(\''+list[i]+'\');" style="float: right; margin-top: 10PX;">Move to Replication Rack</button></div>"DT_RowId":"'+list[i]+'"']);	
			}
			
			
			$('#default_Rack').dataTable( {
				        "bPaginate": false,
						"bLengthChange": false,
						"bFilter": false,
						"bDestroy": true,
						"bSort": true,
						"bInfo": false,
						"bAutoWidth": true,
						"aaData": tableList,
				        "aoColumns": [
				            {  "sTitle": "Primary Rack" }
				        ]
				    } );
			Cluster.primaryRack = list;
		},
		
		fillReplicationRackTable : function(list){
			if(list==null){
				return
			}
			if(document.getElementById('replication_Rack')==undefined||document.getElementById('replication_Rack')==null)return;
			$('#replication_Rack').remove();
			$('#replication_Rack_div').html('<table id="replication_Rack" style="font-size: 9pt; "></table>');
			var tableList=[];
			for(var i=0;i<list.length;i++){
				var obj = new Object();
				obj["0"]='<div><img alt="" src="images/Rack.png"><span><a href="javascript:Navbar.changeTab(\''+list[i]+'\',\'dn_detail\');">'+list[i]+'</a></span><button class="button" onclick="javascript:Cluster.moveSeconaryDCtoPrimaryDC(\''+list[i]+'\');" style="float: right; margin-top: 10PX;">Move to Primary Rack</button></div>';
				obj["DT_RowId"]='r'+list[i];
					tableList.push(obj);
			}
			$('#replication_Rack').dataTable( {
				        "bPaginate": false,
						"bLengthChange": false,
						"bFilter": false,
						"bSort": true,
						"bInfo": false,
						"bAutoWidth": true,
						"bDestroy": true,
						"aaData": tableList,
				        "aoColumns": [
				            {  "sTitle": "Secondary Rack" }
				        ]
				} );
			Cluster.replicationRack = list;
		},
		movePrimaryDCtoSeconaryDC : function(ip){
				var index = Cluster.primaryRack.indexOf(ip);
				if(index!=-1){
					Cluster.primaryRack.splice(index,1);
					Cluster.replicationRack.push(ip)
					var data= new Array();
					var obj = new Object();
					obj["0"]='<div><img alt="" src="images/Rack.png"><span><a href="javascript:Navbar.changeTab(\''+ip+'\',\'dn_detail\');">'+ip+'</a></span><button class="button" onclick="javascript:Cluster.moveSeconaryDCtoPrimaryDC(\''+ip+'\')" style="float: right; margin-top: 10PX;">Move to Primary Rack</button></div>';
					obj["DT_RowId"]='r'+ip;
					data.push(obj);
					Cluster.fillReplicationRackTable(Cluster.replicationRack);
					Cluster.deleteRowFromTable('p'+ip,'default_Rack');
				}
			
		},
		moveSeconaryDCtoPrimaryDC : function(ip){
				var index = Cluster.replicationRack.indexOf(ip);
				if(index!=-1){
					Cluster.replicationRack.splice(index,1);
					Cluster.primaryRack.push(ip)
					var data= new Array();
					var obj = new Object();
					obj["0"]='<div><img alt="" src="images/Rack.png"><span><a href="javascript:Navbar.changeTab(\''+ip+'\',\'dn_detail\');">'+ip+'</a></span><button class="button" onclick="javascript:Cluster.movePrimaryDCtoSeconaryDC(\''+ip+'\')" style="float: right; margin-top: 10PX;">Move to Replication Rack</button></div>';
					obj["DT_RowId"]='p'+ip;
					data.push(obj);
					Cluster.fillDefaultRackTable(Cluster.primaryRack);
					Cluster.deleteRowFromTable('r'+ip,'replication_Rack');
				}
		},
		deleteRowFromTable : function(rowId, tableId){
			var tableObj = document.getElementById(tableId);
			for(var i = 0;  i< tableObj.rows.length;i++){
				var row = tableObj.rows[i];
				if(row.id ==rowId){
					document.getElementById(tableId).deleteRow(i)
				}
			}
		},
		updateRack : function(){
			var clusterId = $('#cluster_select').val();
			RemoteManager.updateRackConfiguration(parseInt(clusterId), Cluster.primaryRack, Cluster.replicationRack,Cluster.RackUpdated);
		},
		RackUpdated : function(resp){
			if(resp){
				jAlert("Rack updated successfully.","Success");
			}else{
				jAlert("Rack updation failed.","Failed");
			}
			Navbar.refreshView();
		}
};