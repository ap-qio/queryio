Billing = {
		currentSchedulePage: 1,
		status: [],
		 userCache: [],
		ready  : function()
		{
			Billing.billingIntervalChanged();
		},
		
		fillBillingReportTable : function(summaryTable)
		{
			
			var colList=[];
			var tableRow=[];
			var rowList='';
			$('#admin_billing_table').remove();
			if (summaryTable == null || summaryTable == undefined)
			{
				$("#admin_billing_table_div").html('<span>Billing Details are not available. </span>');
				return;
			}
			else{
				$("#admin_billing_table_div").html('<table id="admin_billing_table" style="font-size: 9pt;"></table>');
			}

			for(var i=0; i< summaryTable.colNames.length; i++)
			{
					colList.push({ "sTitle": summaryTable.colNames[i]});	
			}
			
			rowList = summaryTable.rows;
			var row;
			for(var i=0; i<rowList.length; i++)
			{
				row = rowList[i];
				var rowData = new Array();
				for(var j=0;j<row.length;j++){
						rowData.push(row[j]);
				}
				tableRow.push(rowData);	
			}
			
			$('#admin_billing_table').dataTable( {
		        "bPaginate": false,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": false,
				"bInfo": false,
				"bAutoWidth": false,
				"bDestroy": true,
				"aaData": tableRow,
		        "aoColumns": colList
		    }); 
			
			
		},
		
		viewReport: function(){
			Util.addLightbox("generateBill", "resources/generate_billing_report.html", null, null);
		},
		
		intervalChanged : function(){
			var days = document.getElementById('reportInterval').value;
			var d = new Date();
			var currentDateinMillis = d.getTime();
			var daysinMillis =  86400000 * parseFloat(days);
			var startDateinMillis = Math.abs(currentDateinMillis - daysinMillis);
			var startDate = new Date(startDateinMillis);
			document.getElementById('report.stdate').value = (startDate.getMonth()+1)+"/"+startDate.getDate()+"/"+startDate.getFullYear()+" "+startDate.getHours()+":"+startDate.getMinutes()+":"+startDate.getSeconds();
			document.getElementById('report.enddate').value = (d.getMonth()+1)+"/"+d.getDate()+"/"+d.getFullYear()+" "+d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
		},
		generateReportReady:function()
		{
			
			var d =new Date();
			document.getElementById('title').value="Billing Report -"+((d.getMonth()+1)+"/"+d.getDate()+"/"+d.getFullYear());
			
			document.getElementById('pdf').checked=true;
			Billing.clickeRadioButton('rTime');
			Billing.intervalChanged();
		},
		setButtonDisable: function(){
			document.forms[1].stdate.value="";
			document.forms[1].enddate.value="";
			document.forms[1].title.value="";
			document.forms[1].stdate.disabled=true;
			document.forms[1].enddate.disabled=true;
			}, 

			closeBox : function(){
				Util.removeLightbox("generateBill");
			},
			clickeRadioButton : function(id){
				if(id=='rTime'){
					document.getElementById('rTime').checked =true;
					document.getElementById('aTime').checked = false
					$("#startDate").css('display','none');
					$("#endDate").css('display','none');
					$("#relativeTimeRow").css('display','');
					Billing.intervalChanged();
				}
				if(id=='aTime'){
					document.getElementById('reportInterval').selectedIndex = 0;
					Billing.intervalChanged();
					document.getElementById('aTime').checked =true;
					document.getElementById('rTime').checked =false
					$("#relativeTimeRow").css('display','none');
					$("#startDate").css('display','');
					$("#endDate").css('display','');
				}
		},

		view: function(){
			
			document.getElementById('msg_td_1').innerHTML="";
			var title=document.getElementById('title').value;
			var endTime = '';
			var startTime = ''
			if(document.getElementById('rTime').selected){
				 endTime = document.getElementById('reportInterval').value;
				 var d = (new Date()).getTime;
				 switch(endTime){
					 case 1: startTime = d -(24*(3600*1000));
					 			break;
					 case 7: startTime = d - (7*(24*(3600*1000)));
			 			break;
					 case 30: startTime = d - (30*(24*(3600*1000)));
			 			break;
					 case 90: startTime = d - (90*(24*(3600*1000)));
			 			break;
					 case 180: startTime = d - (180*(24*(3600*1000)));
			 			break;
					 case 360: startTime = d - (360*(24*(3600*1000)));
			 			break;
				 }
				 var curr = new Date(d)
				 endTime = curr.getMonth()+"/"+curr.getDate()+"/"+curr.getFullYear()+" "+curr.getHours()+":"+curr.getMinutes()+":"+curr.getSeconds();
				 curr = new Date(startTime);
				 startTime = curr.getMonth()+"/"+curr.getDate()+"/"+curr.getFullYear()+" "+curr.getHours()+":"+curr.getMinutes()+":"+curr.getSeconds();
			}
			else{
				endTime = document.getElementById('report.enddate').value;
				
				startTime =document.getElementById('report.stdate').value;
			}
			if(title!=""){
				if(document.getElementById('html').checked||document.getElementById('pdf').checked||document.getElementById('xls').checked){
					var format;
					if(document.getElementById('html').checked)
						format=0;
					else if(document.getElementById('pdf').checked)
						format=1;
					else if(document.getElementById('xls').checked)
						format=3;

					RemoteManager.viewBillingReport(format, title,startTime,endTime, Billing.viewReturn);
						
				}
				else{
					document.getElementById('msg_td_1').innerHTML="* No Format Selected <br>";
				}
			}
			else{
				document.getElementById('msg_td_1').innerHTML+="* Please insert Title for Report<br>";
			}
		},
		viewReturn: function(path){

			Billing.closeBox();
			if(path!=null){
					window.open(path,'BillingReport','width:500px;height:500px;');
			}
			else{
				jAlert("Billing report generation process failed due to some server error.","Generation Failed");
			}
		},
		emailReport: function()
		{
			Util.addLightbox("generateBill", "resources/email_billing_report.html", null, null);
		},
		emailReady: function()
		{
			var d =new Date();
			document.getElementById('title').value="Billing Report -"+((d.getMonth()+1)+"/"+d.getDate()+"/"+d.getFullYear());
			document.forms[1].email.disabled = false;
			$('#emailReportDiv_1').show();
			$('#emailReportDiv_2').hide();
			Billing.setButtonDisable();
			
		},
		email: function()
		{
			document.getElementById('msg_td_2').innerHTML="";
			var selectedUser=document.getElementById('selected').getElementsByTagName("option");
			var title=document.getElementById('title').value;
			var users=[];
			for(var i=0;i<selectedUser.length;i++){
				users.push(selectedUser[i].value);
			}
			if(users.length>0){	
				document.forms[1].email.disabled = true;
				var endTime = '';
				var startTime = ''
				if(document.getElementById('rTime').selected){
					 endTime = document.getElementById('reportInterval').value;
					 var d = (new Date()).getTime;
					 switch(endTime){
						 case 1: startTime = d -(24*(3600*1000));
						 			break;
						 case 7: startTime = d - (7*(24*(3600*1000)));
				 			break;
						 case 30: startTime = d - (30*(24*(3600*1000)));
				 			break;
						 case 90: startTime = d - (90*(24*(3600*1000)));
				 			break;
						 case 180: startTime = d - (180*(24*(3600*1000)));
				 			break;
						 case 360: startTime = d - (360*(24*(3600*1000)));
				 			break;
					 }
					 var curr = new Date(d)
					 endTime = curr.getMonth()+"/"+curr.getDate()+"/"+curr.getFullYear()+" "+curr.getHours()+":"+curr.getMinutes()+":"+curr.getSeconds();
					 curr = new Date(startTime);
					 startTime = curr.getMonth()+"/"+curr.getDate()+"/"+curr.getFullYear()+" "+curr.getHours()+":"+curr.getMinutes()+":"+curr.getSeconds();
				}
				else{
					endTime = document.forms[1].enddate.value;
					startTime = document.forms[1].stdate.value;
				}
				var format;
				if(document.getElementById('html').checked)
					format=0;
				else if(document.getElementById('pdf').checked)
					format=1;
				else if(document.getElementById('xls').checked)
					format=3;
				
				var defaultReport =true;
				
				//DWR call.
				RemoteManager.mailBillingReport(format,users,title,startTime,endTime,defaultReport, Billing.handleEmailReportResponse);
				
			}
			else{
				document.getElementById('msg_td_2').innerHTML+="* Users Not Selected<br>";
			}
		},
		handleEmailReportResponse : function(resp){
			Billing.closeBox();
			if(resp){
				jAlert("Bill report mailed successfully.","Mailed Successfully");
			}
			else{
				jAlert("Bill report generation process failed due to some server error.","Sending Failed");
			}
			
		},
		
		schedule: function(){

			Util.addLightbox("generateBill", "resources/scheduleBillingReport.html", null, null);
		},
		
		billingIntervalChanged : function()
		{
			
			var days = document.getElementById('billingInterval').value;
			var d = new Date();
			var startDate = null;
			var currentDateinMillis = d.getTime();
			if(days=='currentmonth')
			{
				startDate = new Date();
				startDate.setDate(1);
			}
			else
			{
				var daysinMillis =  86400000 * parseFloat(days);
				var startDateinMillis = Math.abs(currentDateinMillis - daysinMillis);
				startDate = new Date(startDateinMillis);
			}
			var startTime = startDate.getTime();
			
			//DWR call.
			RemoteManager.getBillingReportSummaryTable(startTime, currentDateinMillis, Billing.fillBillingReportTable);
		},
		
		nextScheduleStep: function(selectedDiv){
		var flag = true;
			if(Billing.currentSchedulePage<selectedDiv){
				flag =	Billing.validateSchedule();
			}
			if(flag){	
				switch(selectedDiv){
					case 1:{
							$('#reportdiv1').show();
							$('#reportdiv2').hide();
							$('#reportdiv3').hide();
							break;
						}
					case 2:{
							Billing.checkForScheduleID();
							break;
						}
					case 3:{
							RemoteManager.getUserDetails(Billing.addUser);
							$('#reportdiv1').hide();
							$('#reportdiv2').hide();
							$('#reportdiv3').show();
							$('input[value="Close"]').hide();
							break;
						}
					}
				Billing.currentSchedulePage = selectedDiv;
			}
			
	},

	validateSchedule: function(){
		var flag = true;
			switch(Billing.currentSchedulePage){
				case 1:{
					document.getElementById('msg_td_1').innerHTML="";
					var reportDate=document.forms[1].reportDate.value;
					if(reportDate==""){
						document.getElementById('msg_td_1').innerHTML+="* Date & Time Not Provided<br>";				
						flag = false;
					}	
					var exportFormatList = Reports.findExportType();
					if(exportFormatList.length==0){
						document.getElementById('msg_td_1').innerHTML+="* Format Not Selected<br>";
						flag = false;
					}
					break;
				}
				case 2:{
					document.getElementById('msg_td_2').innerHTML="";
					if(document.getElementById('alertRaisedNotificationMessage').value==""){
						document.getElementById('msg_td_2').innerHTML+="* Message Cannot be Empty";
						flag = false;
					}
					break;
				}
			}
		return flag;
	},	
	checkForScheduleID: function(){
		if(document.getElementById('schedID').value!="")
			RemoteManager.checkScheduleId(document.getElementById('schedID').value,Reports.checkResp);
		else{
			document.getElementById('msg_td_1').innerHTML+="* Schedule Id Required<br>";
		}
	},
		

	addUser: function(list){
	if(list!=null){
		if(Billing.userCache.length==0){
			for(var i=0;i<list.length;i++){
					user=list[i];
					Reports.userCache.push(user);
					$('#user').append('<option value="'+user.id+'">'+user.firstName+' '+user.lastName+'</option>');
				}
			}
		}
	},
	moveSelectedOptions: function(from,to){
		
		var source = document.getElementById(from).getElementsByTagName("option");
		for (var i=0; i< source.length; i++)
		{
			if (source[i].selected)
			{
				$('#'+to).append('<option value="'+source[i].value+'">'+Reports.findUser(source[i].value)+'</option>');
				
			}
		}
		 $("#"+from+" option:selected").remove();
	},
	moveAllOptions: function (from,to){
		
		var source = document.getElementById(from).getElementsByTagName("option");	
		for (var i=0; i< source.length; i++)
		{
			$('#'+to).append('<option value="'+source[i].value+'">'+Reports.findUser(source[i].value)+'</option>');
		}
		$('#'+from).children().remove();
	},
	nextPage: function(){
		var flag = Reports.validateEmailpage();
		if(flag){
			document.getElementById('msg_td_2').innerHTML="";
			$('#emailReportDiv_1').hide();
			$('#emailReportDiv_2').show();
			RemoteManager.getUserDetails(Reports.addUser);
		}
	},
	backPage: function(){
		$('#emailReportDiv_2').hide();
		$('#emailReportDiv_1').show();
	},

	addSchedule: function(){
		
		var selectedUser=document.getElementById('selected').getElementsByTagName("option");
		var users=[];
		for(var i=0;i<selectedUser.length;i++){
			users.push(selectedUser[i].value);
		}
		if(users.length>0){
			var reportsType = [];
			reportsType.push(10);
			var nodeID = -1;
			
			RemoteManager.scheduleJob(document.forms[1].interval.value,document.forms[1].reportDate.value,Billing.findExportType(),
			document.getElementById('notificationType').value, document.getElementById('alertRaisedNotificationMessage').value,
			users, reportsType, nodeID, document.getElementById('schedID').value,Billing.scheduleReturn);
		}
		else{
			document.getElementById('msg_td_3').innerHTML+="* Users Not Selected<br>";
		}	
	},
		
	scheduleReturn: function(flag){
		Billing.closeBox();
		if(flag){
			jAlert('Report scheduled successfully','Success');
		}
		else{
			jAlert('Some Error Occured', 'Error');
		}
	},
	findExportType: function()
	{
			var exportFormatList=[];
			if(document.forms[1].html.checked)
				exportFormatList.push(0);
			if(document.forms[1].pdf.checked)
				exportFormatList.push(1);
			if(document.forms[1].xls.checked)
				exportFormatList.push(3);
			return exportFormatList;
	}

	
};