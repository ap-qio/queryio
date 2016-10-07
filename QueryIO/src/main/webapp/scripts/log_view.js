LogView = {
		
		
		
		showLog : function(path)
		{
			logPath = path;
			Util.addLightbox("log", "resources/log_view.html", null, null);
			
		},
		
		ready : function()
		{
			 $.get(logPath, null, function(data)
                     {
				 		if(data==null){
				 			$('#logcontainer').val("File Not Found");
				 		}
				 		else{
				 			
				 			$('#logcontainer').val(data);
				 		}
                     }, "text");
		},
		closeBox : function()
		{
			Util.removeLightbox("log");
		}
		
		
};