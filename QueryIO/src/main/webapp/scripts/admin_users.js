Users = {

	aaData : null,
	userCache : [],
	operation : '',

	ready : function() {
		selectedRole = "Admin";
		RemoteManager.getAllGroupNames(Users.populateGroupTable);
		// RemoteManager.getAllUserForGroup(Users.populateUsersGroupTable)
		dwr.util.byId('user.edit').disabled = true;
		dwr.util.byId('user.delete').disabled = true;
		dwr.util.byId('user.deleteFromGroup').disabled = true;
		Users.slide();

		jQuery(window).bind('resize', function() {
			Users.resizeGrid();
		}).trigger('resize');
	},

	selectTask : function() {
		if ($("#actionDC").val() == "Add")
			Users.AddNewGroup();
		else if ($("#actionDC").val() == "Delete")
			Users.RemoveGroup();
		$("#actionDC").prop('selectedIndex', 0);
	},

	AddNewGroup : function() {
		Users.operation = 'addGroup';
		Util.addLightbox("adduser", "resources/addNewGroup.html", null, null);
	},

	RemoveGroup : function() {
		jQuery.alerts.okButton = ' Yes';
		jQuery.alerts.cancelButton = ' No';
		jConfirm('Are you sure you want to delete selected Group?',
				'Delete Group', function(val) {
					if (val == true) {
						RemoteManager.deleteGroup(selectedGroupId,
								Users.userDeleted);
					} else
						return;
					jQuery.alerts.okButton = ' Ok';
					jQuery.alerts.cancelButton = ' Cancel';

				});
	},

	resetSettings : function() {
		selectedUsers.splice(0, selectedUsers.length);
		$('input:checkbox').removeAttr('checked');
		document.getElementById("user.edit").disabled = true;
		document.getElementById("user.delete").disabled = true;
	},

	populateUsersTable : function(userList) {
		dwr.util.byId('user.edit').disabled = true;
		dwr.util.byId('user.delete').disabled = true;
		dwr.util.byId('user.deleteFromGroup').disabled = true;
		if (userList == null || userList == undefined) {
			$("#users_table").html('<span>User details not available. </span>');
			return;
		}
		var flag = true;
		var colList = [];
		var rowList = '';
		var tableRow = new Array();

		totalUser = userList.length;

		Users.userCache = [];
		for (var i = 0; i < userList.length; i++) {
			var row = userList[i];
			var rowData = new Array();
			rowData
					.push('<input type="checkbox" onclick="javascript:Users.clickCheckBox(this.id)" id="user-'
							+ row.id + '" >');
			rowData.push(row.userName);
			rowData.push(row.firstName);
			rowData.push(row.lastName);
			rowData.push(row.email);
			rowData.push(row.role);
			tableRow.push(rowData);
			Users.userCache.push(row);
		}

		$('#users_table')
				.dataTable(
						{
							"bPaginate" : false,
							"bLengthChange" : false,
							"bFilter" : false,
							"bDestroy" : true,
							"bSort" : false,
							"bInfo" : false,
							"bAutoWidth" : false,
							"aaSorting" : [ [ 1, "desc" ] ],
							"aaData" : tableRow,
							"aoColumnDefs" : [ {
								"bSortable" : false,
								"aTargets" : [ 0 ]
							} ],
							"aoColumns" : [
									{
										"sTitle" : '<input type="checkbox" id="selectAll" onclick="javascript:Users.selectAllUserRow()" >'
									}, {
										"sTitle" : "UserName"
									}, {
										"sTitle" : "FirstName"
									}, {
										"sTitle" : "LastName"
									}, {
										"sTitle" : "Email ID"
									}, {
										"sTitle" : "Role"
									}, ],
						});

		if (userList == null || userList == undefined || userList.length == 0) {
			document.getElementById('selectAll').disabled = true;
		}

	},

	clickCheckBox : function(chkbxid) {
		if (dwr.util.byId(chkbxid).checked) {
			selectedUsers.push(chkbxid);
		} else {
			var index = selectedUsers.indexOf(chkbxid);
			selectedUsers.splice(index, 1);
		}
		if (($('#users_table tr').length - 1) == selectedUsers.length) {
			document.getElementById("selectAll").checked = dwr.util
					.byId(chkbxid).checked;
			Users.selectAllUserRow();
		} else {
			if ((($('#users_table tr').length - 1) == selectedUsers.length + 1 && !dwr.util
					.byId(chkbxid).checked))
				document.getElementById("selectAll").checked = dwr.util
						.byId(chkbxid).checked;

			if (selectedUsers.length > 0) {
				if (selectedUsers.length == 1) {
					dwr.util.byId('user.edit').disabled = false;
				} else {
					dwr.util.byId('user.edit').disabled = true;
				}
				dwr.util.byId('user.delete').disabled = false;
				dwr.util.byId('user.deleteFromGroup').disabled = false;
			} else {
				dwr.util.byId('user.edit').disabled = true;
				dwr.util.byId('user.delete').disabled = true;
				dwr.util.byId('user.deleteFromGroup').disabled = true;
			}
		}
	},

	selectAllUserRow : function() {
		var val = document.getElementById('selectAll').checked;
		if (!val) {
			selectedUsers = [];
		}
		selectedUsers.splice(0, selectedUsers.length)
		for (var i = 0; i < Users.userCache.length; i++) {
			var user = Users.userCache[i];
			document.getElementById('user-' + user.id).checked = val;
			if (val) {
				selectedUsers.push('user-' + user.id);
			}
		}

		if (selectedUsers.length > 0) {
			if (selectedUsers.length == 1) {
				dwr.util.byId('user.edit').disabled = false;
			} else {
				dwr.util.byId('user.edit').disabled = true;
			}
			dwr.util.byId('user.delete').disabled = false;
			dwr.util.byId('user.deleteFromGroup').disabled = false;
		} else {
			dwr.util.byId('user.edit').disabled = true;
			dwr.util.byId('user.delete').disabled = true;
			dwr.util.byId('user.deleteFromGroup').disabled = true;
		}
	},

	roleChanged : function(role) {
		selectedRole = role;
		$("#userGroup option[value='queryio']").remove();
		if (role == "Admin") {
			$("#userGroup").append('<option value="queryio">queryio</option>');
		} else
			$("#defaultGroup option[value='queryio']").remove();
	},

	addUser : function() {
		Users.operation = 'add';
		Util.addLightbox("adduser", "resources/add_user.html", null, null);
	},

	editUser : function() {
		Users.operation = 'edit';
		if (selectedUsers.length == 1) {
			selectedUserId = selectedUsers[0].substring(selectedUsers[0]
					.indexOf('-') + 1);
			Util.addLightbox("adduser", "resources/edit_user.html", null, null);
		} else {
			jAlert("Select any 1 User.", "Incomplete Detail");
			Navbar.refreshView();
		}
	},

	isLoggedInUserInSelectedList : function() {
		isSelected = false;
		var loggedInUsrId = (document.getElementById("loggedInUserId").innerHTML)
				.toString();
		for (var i = 0; i < selectedUsers.length; i++) {
			var splitGroupAndID = selectedUsers[i].split("-");
			currentSelected = splitGroupAndID[splitGroupAndID.length - 1];
			if (currentSelected == loggedInUsrId) {
				isSelected = true;
				break;
			}
		}

		return isSelected;
	},

	deleteUser : function() {
		Users.operation = 'delete';
		jQuery.alerts.okButton = ' Ok ';
		if (Users.isLoggedInUserInSelectedList()) {
			jAlert(
					'You were attempting to delete yourself. Please deselect yourself before deleting user.',
					'Invalid Action');
			return;
		}

		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton = ' No';
		jConfirm('Are you sure you want to delete selected User(s)?',
				'Delete User(s)', function(val) {
					if (val == true) {
						var loggingUserId = document
								.getElementById("loggedInUserId").innerHTML;
						var selectUsersList = [];
						for (var i = 0; i < selectedUsers.length; i++) {
							selectUsersList
									.push(selectedUsers[i]
											.substring(selectedUsers[i]
													.indexOf('-') + 1));
						}
						RemoteManager.deleteUsersFromList(selectUsersList,
								Users.userDeleted);
					} else
						return;
					jQuery.alerts.okButton = ' Ok';
					jQuery.alerts.cancelButton = ' Cancel';

				});
	},

	deleteUserFromGroup : function() {
		if (selectedUsers
				.indexOf(document.getElementById("loggedInUserId").innerHTML) != -1) {
			jAlert(
					'You were attempting to delete yourself.Please deselect yourself before deleting user.',
					'Invalid Action');
			return;
		}

		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton = ' No';
		jConfirm(
				'Are you sure you want to delete selected item(s)?',
				'Delete User(s)',
				function(val) {
					if (val == true) {
						var loggingUserId = document
								.getElementById("loggedInUserId").innerHTML;
						for (var i = 0; i < totalUser.length; i++) {

							if (totalUser[i][0] == loggingUserId) {
								if (totalUser[i][6] != 'Admin') {
									jAlert(
											'You do not have rights to delete any user.',
											'Insufficient Access');
									return;
								}
							}
						}
						for (var i = 0; i < selectedUsers.length; i++) {
							selectedUserId = selectedUsers[i]
									.substring(selectedUsers[i].indexOf('-') + 1);
							var groupName = selectedUsers[i].substring(0,
									selectedUsers[i].indexOf('-'));
							// Not present in PermissionManager class
							RemoteManager.removeUserFromGroup(selectedUserId,
									groupName, Users.userDeleted);
						}
					} else
						return;
					jQuery.alerts.okButton = ' Ok ';
					jQuery.alerts.cancelButton = ' Cancel';
				});

	},

	userDeleted : function(dwrResponse) {
		jAlert(dwrResponse.responseMessage, 'Response');
		// Navbar.isRefreshPage =true;
		// Navbar.changeTab('Users','admin','users');
		Users.resetSettings();
		Navbar.refreshView();
	},

	saveUserClicked : function() {
		$('#savebtn').prop('disabled', true);

		var userName = dwr.util.byId('user.name').value;
		var fName = dwr.util.byId('firstName').value;
		var lName = dwr.util.byId('lastName').value;
		var password = dwr.util.byId('password').value;
		var retypepassword = dwr.util.byId('retypepassword').value;
		var email = dwr.util.byId('email').value;
		var defaultGroup = $('#defaultGroup').val();
		var selectedgroupName = new Array();
		var groupOptions = document.getElementById('userGroup');

		for (var i = 0; i < groupOptions.length; i++) {
			if (groupOptions[i].selected) {
				selectedgroupName.push(groupOptions[i].value);
			}
		}

		if (userName == '') {
			this
					.setErrorMsg('User name was not entered. Please enter a valid user name.');
			return;
		}
		if (fName == '') {
			this
					.setErrorMsg('First name was not entered. Please enter first name.');
			return;
		}
		if (lName == '') {
			this
					.setErrorMsg('Last name was not entered. Please enter last name of user.');
			return;
		}
		if (password == '') {
			this
					.setErrorMsg('Password was not entered. Please enter a valid password.');
			return;
		}
		if (retypepassword == '') {
			this
					.setErrorMsg('Password confirmation was not entered. Please enter a valid password.');
			return;
		}
		if (email == '') {
			this
					.setErrorMsg('Email was not entered. Please enter a valid email id.');
			return;
		}
		if (defaultGroup == '') {
			this
					.setErrorMsg('Default Group was not select for the user. Please select a default group');
			return;
		}
		if (!Util.validateEmail(email)) {
			this
					.setErrorMsg('Email address id not valid . Please enter a valid email id.');
			return;
		}
		if (selectedgroupName.length == 0) {
			this
					.setErrorMsg('Group Name was not selected. Please select a group for user.');
			return;
		}
		if (password != retypepassword) {
			this
					.setErrorMsg('Passwords do not match. Please retype your password.');
			$("#retypepassword").val("");
			return;
		}
		if (Users.checkUserWithUserID(userName)) {
			RemoteManager.insertUser(selectedRole, userName, fName, lName,
					password, email, selectedgroupName, defaultGroup,
					Users.userSaved);
		} else {
			this
					.setErrorMsg('Username already taken. Please select some different username');
			return;
		}

	},

	userSaved : function(dwrResponse) {
		Users.closeBox();
		jAlert(dwrResponse.responseMessage, 'Response');
		$('#savebtn').prop('disabled', false);
		RemoteManager
				.getUserForGroup(selectedGroupId, Users.populateUsersTable);
	},

	groupSaved : function(dwrResponse) {
		Users.closeBox();
		jAlert(dwrResponse.responseMessage, 'Response');
		Navbar.refreshView();
	},

	navigationClickHandlerUser : function() {
		Navbar.refreshView();
	},

	loadSelectedUser : function() {
		RemoteManager.getAllGroupNames(Users.fillGroup);

	},

	fillUsersPage : function(user) {
		selectedUserId = user.id;
		dwr.util.byId('user.name').value = user.userName;
		dwr.util.byId('firstName').value = user.firstName;
		dwr.util.byId('lastName').value = user.lastName;
		dwr.util.byId('userRole').value = user.role;
		Users.roleChanged(user.role);
		dwr.util.byId('userRole').disabled = true;
		dwr.util.byId('email').value = user.email;

		var list = user.groups;
		var groupOptions = document.getElementById('userGroup');
		for (var i = 0; i < list.length; i++) {
			var item = list[i];
			for (var j = 0; j < groupOptions.length; j++) {
				if (groupOptions[j].value == item) {
					groupOptions[j].selected = true;
					$('#defaultGroup').append(
							'<option value="' + groupOptions[j].value + '">'
									+ groupOptions[j].value + '</option>');
				}
			}
		}
		RemoteManager.getDefaultGroupForUser(user.userName,
				Users.fillDefaultGroup)
	},
	fillDefaultGroup : function(groupName) {
		$('#defaultGroup').val(groupName);
	},

	savePassword : function(id) {
		document.getElementById(id).disabled = true;
		var password = dwr.util.byId('password').value;

		var retypePassword = dwr.util.byId('retypepassword').value;

		var oldPassword = dwr.util.byId('oldPassword').value;

		if (password == '') {
			this
					.setEditErrorMsg('Password was not entered. Please enter a valid password.');
			document.getElementById('save.password').disabled = false;
			return;
		}
		if (retypePassword == '') {
			this
					.setEditErrorMsg('Password confirmation was not entered. Please enter a valid password.');
			document.getElementById('save.password').disabled = false;
			return;
		}
		if (oldPassword == '') {
			this
					.setEditErrorMsg('Password confirmation was not entered. Please enter a valid password.');
			document.getElementById('save.password').disabled = false;
			return;
		}

		if (password != retypePassword) {
			this
					.setEditErrorMsg('Passwords do not match. Please retype your password.');
			document.getElementById('save.password').disabled = false;
			$("#retypepassword").val("");
			return;
		}

		RemoteManager.updatePassword(selectedUserId, oldPassword, password,
				Users.passwordUpdated);
	},

	passwordUpdated : function(dwrResponse) {
		dwr.util.byId('password').value = '';
		dwr.util.byId('retypepassword').value = '';
		dwr.util.byId('oldPassword').value = '';

		if (dwrResponse.responseMessage == 'Password Updated Successfully.') {
			// $("#oldPasswordRow").css('display', 'none');
			// $("#passwordRow").css('display', 'none');
			// $("#retypePasswordRow").css('display', 'none');
			// $("#savePasswordRow").css('display', 'none');
			$('#userForm').css('display', '');
			$("#changePasswordForm").css('display', 'none');

			Users.setEditErrorMsg('');
		}
		document.getElementById('save.password').disabled = false;
		jAlert(dwrResponse.responseMessage, 'Response');
		$("#popup_container").css("z-index", "99999999");
	},

	updateUserClicked : function() {
		$('#saveeditbtn').prop('disabled', true);
		var userName = dwr.util.byId('user.name').value;
		var fName = dwr.util.byId('firstName').value;
		var lName = dwr.util.byId('lastName').value;
		var email = dwr.util.byId('email').value;
		var defaultGroup = $('#defaultGroup').val();
		var selectedgroupName = new Array();
		var groupOptions = document.getElementById('userGroup');

		for (var i = 0; i < groupOptions.length; i++) {
			if (groupOptions[i].selected) {
				selectedgroupName.push(groupOptions[i].value);
			}
		}

		if (userName == '') {
			this
					.setEditErrorMsg('User name was not entered. Please enter a valid user name.');
			return;
		}
		if (fName == '') {
			this
					.setEditErrorMsg('First name was not entered. Please enter first name.');
			return;
		}
		if (lName == '') {
			this
					.setEditErrorMsg('Last name was not entered. Please enter last name of user.');
			return;
		}
		if (email == '') {
			this
					.setEditErrorMsg('Email was not entered. Please enter a valid email id.');
			return;
		}
		if (defaultGroup == '') {
			this
					.setErrorMsg('Default Group was not select for the user. Please select a default group');
			return;
		}
		if (!Util.validateEmail(email)) {
			this
					.setEditErrorMsg('Email address id not valid . Please enter a valid email id.');
			return;
		}
		if (selectedgroupName.length == 0) {
			this
					.setEditErrorMsg('Group Name was not selected. Please select a group for user.');
			return;
		}
		RemoteManager.updateUser(selectedUserId, userName, fName, lName, email,
				selectedgroupName, defaultGroup, Users.userUpdated);

	},

	checkUserWithUserID : function(newUserName) {
		for (var i = 0; i < Users.userCache.length; i++) {
			var user = Users.userCache[i];
			if (user.userName == newUserName) {
				return false;
			}
		}
		return true;
	},

	userUpdated : function(dwrResponse) {

		Users.closeBox();
		jAlert(dwrResponse.responseMessage, 'Response');
		$('#saveeditbtn').prop('disabled', false);
		Navbar.refreshView();
	},

	populateGroupTable : function(data) {
		Users.aaData = new Array();
		for (var i = 0; i < data.length; i++) {
			Users.aaData[i] = ({
				Name : data[i]
			});
		}
		Users.populateLeftTable(Users.aaData);
		var rowId = jQuery("#dbTable").jqGrid('getDataIDs');
		jQuery("#dbTable").jqGrid('setSelection', rowId[0], true);
	},

	resizeGrid : function() {
		$("#dbTable").setGridWidth(($("#table_container").width() - 5), true);
		$('.ui-jqgrid-bdiv').css('height', '590px');
	},

	populateLeftTable : function(aadata) {
		var topHeight = $("#service_ref").height() - $("#db_detail").height()
				- $('#dbHeader').height() - $('#buttonDiv').height() - 13;
		var actionContainerHeight = $('#actionContainer').height();
		$('#connection_header').html('Groups (' + aadata.length + ')');
		$('#dbTable').remove();
		$('#table_container').html('<table id="dbTable"></table>');
		jQuery("#dbTable")
				.jqGrid(
						{
							datatype : "local",
							colNames : [ 'Name' ],
							colModel : [ {
								name : 'Name',
								index : 'Name',
								width : ($("#table_container").width() - 15),
								sorttype : "text"
							}, ],
							height : ($("#table_container").height() - 35),// topHeight-actionContainerHeight
							// -
							// 20,
							width : ($("#table_container").width() - 5),
							shrinkToFit : false,
							rowNum : aadata.length,
							pager : "",
							altRows : true,
							viewrecords : true,
							sortable : true,
							pagination : false,
							caption : "",
							onSortCol : function(index, idxcol, sortorder) {
								if (this.p.lastsort >= 0
										&& this.p.lastsort !== idxcol
										&& this.p.colModel[this.p.lastsort].sortable !== false) {
									$(this.grid.headers[this.p.lastsort].el)
											.find(
													">div.ui-jqgrid-sortable>span.s-ico")
											.show();
									$(this.grid.headers[this.p.lastsort].el)
											.removeClass('ui-state-highlight');
								}
								$(this.grid.headers[idxcol].el).addClass(
										'ui-state-highlight');
							},

							onSelectRow : function(id) {
								var allRowsOnCurrentPage = $('#dbTable')
										.jqGrid('getDataIDs');
								for (var i = 0; i < allRowsOnCurrentPage.length; i++) {
									if (allRowsOnCurrentPage[i] == id)
										$('#' + allRowsOnCurrentPage[i]).css(
												'background-color', '#ECECEC');
									else
										$('#' + allRowsOnCurrentPage[i]).css(
												'background-color', 'white');
								}

								var rowData = jQuery("#dbTable").jqGrid(
										'getRowData', id); // data of the row
								selectedGroupId = rowData.Name;
								$('#detail_header').html(
										'Users Info (' + selectedGroupId + ')');
								RemoteManager.getUserForGroup(rowData.Name,
										Users.populateUsersTable);
								Users.resetSettings();
							},
						});

		jQuery("#dbTable").jqGrid('navGrid', '', {
			add : false,
			edit : false,
			del : false
		});

		var rowIndexToBeSelected = -1;
		if (aadata == null || aadata == undefined || aadata.length == 0) {
			$("#refreshViewButton").removeAttr('disabled');
		}
		for (var i = 0; i < aadata.length; i++) {
			if (Users.selectedGroupId != null && Users.selectedGroupId != ''
					&& aadata[i].Name == Users.selectedGroupId) {
				rowIndexToBeSelected = i + 1;
			}
			jQuery("#dbTable").jqGrid('addRowData', i + 1, aadata[i]);
		}

		if (rowIndexToBeSelected > -1) {
			jQuery("#dbTable").jqGrid('setSelection', rowIndexToBeSelected);
		} else {
			$(jQuery("#dbTable")[0].grid.headers[0].el).addClass(
					'ui-state-highlight'); // Highlight first column header on
			// grid load.
		}
		Users.resizeGrid();
	},

	slide : function() {
		$("#ShowDC").hide();

		$("#HideDC").click(function() {
			$("#HideDC").hide();
			$("#ShowDC").show();
			$("#db_detail").width("2%");
			$("#db_detail").css('min-width', '0px');
			$("#rightDC").width("auto");
			$("#dbConfig").css("display", "none");
			Users.resizeGrid();
		});

		$("#ShowDC").click(function() {
			$("#ShowDC").hide();
			$("#HideDC").show();
			$("#db_detail").width("340px");
			$("#db_detail").css('min-width', '340px');
			$("#rightDC").width("auto");
			$("#dbConfig").css("display", "block");
			Users.resizeGrid();
		});

	},

	// populateUsersGroupTable : function(map){
	// usersCache = [];
	// var table_data = '<thead><tr>';
	//   		
	// table_data+='<th style = "width: 10%;">Groups</th>';
	// table_data+='<th style = "width: 20%;">User Name</th>';
	// table_data+='<th style = "width: 20%;">First Name</th>';
	// table_data+='<th style = "width: 20%;">Last Name</th>';
	// table_data+='<th style = "width: 20%;">Email Id</th>';
	// table_data+='<th style = "width: 10%;">Role</th>';
	//   		
	// table_data+='</tr></thead>';
	//   		
	// for(var group in map){
	//   			
	// var userList = map[group];
	// if(userList.length==0)
	// continue;
	// table_data+='<tr id="'+group+'">';
	// table_data+='<td><span><img alt="" src="images/groups.png" style="height:
	// 15px; width: 30px;"></span> '+group+'</td>';
	// table_data+='<td colspan="6"></td>'
	// table_data+='</tr>';
	// for(var i = 0; i<userList.length;i++){
	// var user = userList[i];
	// table_data+='<tr id="'+user.id+'" class="child-of-'+group+'">';
	// table_data+='<td><input type="checkbox"
	// onclick="javascript:Users.clickCheckBox(this.id)"
	// id="'+group+'-'+user.id+'"></td>';
	// table_data+='<td><span><img alt="" src="images/owner.png" style="height:
	// 15px; width: 13px;">&nbsp;&nbsp;</span>'+user.userName+'</td>';
	// table_data+='<td>'+user.firstName+'</td>';
	// table_data+='<td>'+user.lastName+'</td>';
	// table_data+='<td>'+user.email+'</td>';
	// table_data+='<td>'+user.role+'</td>';
	// table_data+='</tr>';
	// Users.userCache.push(user);
	// }
	// }
	//
	//   		
	// $('#users_table').html(table_data);
	// $("#users_table").treeTable(
	// {
	// expandable: true,
	// clickableNodeNames: true,
	// });
	//		
	//   		
	// },

	closeBox : function() {
		Users.resetSettings();
		Util.removeLightbox("adduser");
	},

	setErrorMsg : function(errMsg) {
		$("div#add_user tr#error_msg_row").html(
				'<td style="color: red;"><div class="instructional">' + errMsg
						+ '</div></td>');
	},

	setEditErrorMsg : function(errMsg) {
		$("div#edit_user tr#error_msg_row").html(
				'<td style="color: red;"><div class="instructional">' + errMsg
						+ '</div></td>');
	},

	addUserReady : function() {
		RemoteManager.getAllGroupNames(Users.fillGroup);
	},

	fillGroup : function(list) {

		if (list == null || list == undefined) {
			RemoteManager.getAllUserNames(Permissions.populateUserTable);
			return;
		}
		var data = '';
		for (var i = 0; i < list.length; i++) {
			if (list[i] == selectedGroupId) {
				data += '<option selected value="' + list[i] + '">' + list[i]
						+ '</option>';
				continue;
			}
			data += '<option value="' + list[i] + '">' + list[i] + '</option>';
		}

		$('#userGroup').html(data);

		if (Users.operation == 'edit') {
			RemoteManager.getUserDetail(selectedUserId, Users.fillUsersPage);
		} else {
			Users.groupChanged();
		}

	},

	addNewGroup : function() {
		$('#userDiv').css("display", "none");
		$('#addGroup').css("display", "");
		$('#headerspan').text('');
		$('#headerspan').text('Add Group');
	},

	closeGroupBox : function() {
		Util.removeLightbox("adduser");
		Util.addLightbox("adduser", "resources/add_user.html", null, null);

	},

	saveNewGroup : function() {
		var groupName = $('#group_name').val();
		groupName = groupName.toLowerCase();
		var flag = Users.checkExistingGroup(groupName);
		if (groupName == '' || groupName == undefined || flag) {
			jAlert("Please provide a unique group name.", "Incomplete Detail");
			$("#popup_container").css("z-index", "99999999");
			return;
		}
		RemoteManager.addGroup(groupName, Users.groupSaved);
	},

	checkExistingGroup : function(groupName) {
		for (var i = 0; i < Users.aaData.length; i++) {
			if (Users.aaData[i].Name == groupName)
				return true;
		}
		return false;
	},

	togglePasswordContainer : function() {
		// if($("#oldPasswordRow").is(":visible"))
		// {
		// $("#oldPasswordRow").css('display', 'none');
		// $("#passwordRow").css('display', 'none');
		// $("#retypePasswordRow").css('display', 'none');
		// $("#savePasswordRow").css('display', 'none');
		// }
		// else
		// {
		// $("#oldPasswordRow").css('display', '');
		// $("#passwordRow").css('display', '');
		// $("#retypePasswordRow").css('display', '');
		// $("#savePasswordRow").css('display', '');
		// }
		if ($("#userForm").is(":visible")) {
			$('#userForm').css('display', 'none');
			$("#changePasswordForm").css('display', '');
			document.getElementById('save.password').disabled = false;
		} else if ($("#changePasswordForm").is(":visible")) {
			$('#userForm').css('display', '');
			$("#changePasswordForm").css('display', 'none');
		}
	},
	groupChanged : function() {
		var groupOptions = document.getElementById('userGroup');
		var data = "";
		$('#defaultGroup').html(data);

		for (var i = 0; i < groupOptions.length; i++) {
			if (groupOptions[i].selected) {
				data += '<option value="' + groupOptions[i].value + '">'
						+ groupOptions[i].value + '</option>';
			}
		}
		$('#defaultGroup').html(data);
	}
};