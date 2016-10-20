Permissions = {
		ready : function(){
			RemoteManager.getAllGroupNames(Permissions.fillGroup);
		},
		fillGroup : function(list){
			
			if(list==null||list==undefined){
				RemoteManager.getAllUserNames(Permissions.populateUserTable);
				return;
			}
			var data='';
			for(var i = 0;i<list.length;i++){
				data+='<option value="'+list[i]+'">'+list[i]+'</option>';
			}
			$('#groupName').html(data);
			RemoteManager.getUserForGroup(list[0],Permissions.populateUserTable);
		},
		popUpAddGroup : function(){
			Util.addLightbox("permdiv", "resources/add_group.html", null, null);
		},
		popUpDeleteGroup : function(){
			
		},
		groupAddResponse : function(resp){
			if(resp){
				jAlert("Group added Successfully","Success");
			}else{
				jAlert("Group addition failed","Failed");
			}
			Permissions.closeBox();
			Navbar.refreshView();
		},
		saveNewGroup : function(){
			var groupName = $('#groupName').val();
			RemoteManager.addGroup(groupName,Permissions.groupAddResponse);
			
		},
		closeBox : function(){
			Util.removeLightbox("permdiv");
		},
		processingReady : function(){
			
		},
		populateUserTable : function(list){
			var user
			$('#userTableDiv').html('<table id="userTable"  style="font-size: 9pt;"></table>');
			if(list==null||list==undefined){
				return;
		  	}
			var table_data=[];
			var user
			for(var i=0;i<list.length;i++){
				user = list[i];
				table_data.push([user.firstName,user.lastName,user.email,user.mobileno]);
				
			}
			
		   	$('#userTable').dataTable( {
		   			"sScrollX": "100%",
		   			"bPaginate": false,
					"bLengthChange": true,
					"bDestroy": true,
					"sPaginationType": "full_numbers",
					"bFilter": false,
					"bSort": true,
					"bInfo": false,
					"bAutoWidth": true,
					"aaData": table_data,
			        
			        "aoColumns": [
			            { "sTitle": "FirstName" },
			            { "sTitle": "LastName" },
			            { "sTitle": "Email" },
			            { "sTitle": "mobile" }
			        ]
			    } );
		}
		
		
}