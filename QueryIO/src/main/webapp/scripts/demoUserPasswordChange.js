DemoUser = {		
	changePassword : function() {
		var isValidated = DemoUser.validate();
		if(isValidated) {
			var currentPassword = $('#currentPassword').val();
			var newPassword = $('#newPassword').val();
			RemoteManager.updatePassword($('#loggedInUserId').text(), currentPassword, newPassword, DemoUser.passwordChangeCallback);
		}
	},
	
	validate : function() {
		var validated = true;
		$('.required').each(function(){
			if($(this).val() == '') {
				jAlert('All fields are required.');
				validated = false;
			}
		});
		if(validated) {			
			if($('#newPassword').val() != $('#retypePassword').val()) {
				validated = false;
				jAlert('New password must match with confirm password.');
			}
		}
		return validated;
	},
	
	passwordChangeCallback : function(response) {
		if(response.taskSuccess) {
			jAlert('Password changed successfully.', 'Info', function() {
				Navbar.promptPasswordChange = false;
				Navbar.changeTab('Data Migration','data', 'data_migration');
			});
		} else {
			jAlert(response.responseMessage);
		}
	}
};
