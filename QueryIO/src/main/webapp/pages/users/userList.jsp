<script type="text/javascript" src="dwr/engine.js"></script>
<script type="text/javascript" src="dwr/util.js"></script>
<script type="text/javascript" src="scripts/user.js"></script>

<div id="admin_list">

	<table class="outtertab_table">
		<tr class="listheader_tr_new" width="100%">
			<td class="listheader_td_new">
				<div style="float: left"><span><a href="javascript:tabClicked('Status');"><img src="images/pathway-home.png"/></a></span></div>
				<a class="tab_banner" href="javascript:tabClicked('Admin');" style="text-decoration:underline;">
					Admin
				</a>
				<a class="tab_banner">
					Users
				</a>
			</td>
			<td width="50%" class="tab_banner_end" align=right>
			</td>
		</tr>
	</table>
	
	<table id="userTable" class="viewTable"> 
		<tr class="headerRow">
			<th width="2%"></th>
			<th width="18%">User Name</th>
			<th width="20%">First Name</th>
			<th width="20%">Last Name</th>
			<th width="20%">Password</th>
			<th width="20%">E-mail</th>
		</tr>
		<tbody id="users">
			<tr id="user_pattern" style="display:none;">
				<td><input id="user" name='user' type="radio" onclick="javascript:changeSelectedUser(this.id);"></td>
				<td>
<!-- 				<a href='#' id='user.' onclick="javascript:showUserClicked(this.id);">		 -->
					<span id="user.userName"></span>
<!-- 				</a>		 -->
				</td>
				<td><span id="user.firstName"></span></td>
				<td><span id="user.lastName"></span></td>
				<td><span id="user.password"></span></td>
				<td><span id="user.email"></span></td>
			</tr>
		</tbody>
	</table>
	
	<table class="buttonTable">
		<tr>
			<td><input type="button" class="buttonAdmin" id="add.user" value="Add" onclick="javascript:addUserClicked();" /></td>
			<td><input type="button" class="buttonAdmin" id="edit.user" value="Edit" onclick="javascript:editUserClicked();" style="margin: 0 0 0 10pt;"/></td>
			<td><input type="button" class="buttonAdmin" id="delete.user" value="Delete" onclick="javascript:deleteUserClicked();" style="margin: 0 0 0 10pt;"/></td>
		</tr>
	</table>
	<script>
		fillAllUsers();
	</script>

</div>