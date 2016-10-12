DDT = {
	selectedTag : [],
	tagArray : [],
	operation : '',
	totalDDT : 0,
	currentOperation : '',
	browse : false,
	jqGridColumnDetails : [],
	total : 0,
	allTagArray : [],

	ready : function() {
		// RemoteManager.getAllCustomTagsMetadataDetail(DDT.filltagArray);
		DDT.populateDataTagTable();
		DDT.enableDisableButton();
	},

	// filltagArray : function(list) {
	// console.log('in filltagArray' );
	// DDT.tagArray = list;
	// RemoteManager.getAllCustomTagsMetadataDetail(DDT.populateDataTagTable);
	// },

	populateDataTagTable : function() {

		$('#data_tag_table').dataTable(
				{
					"sScrollX" : "100%",
					"bPaginate" : true,
					"bLengthChange" : true,
					"aLengthMenu" : [ 10, 25, 50, 100 ],
					"sPaginationType" : "full_numbers",
					"bFilter" : false,
					"bDestroy" : true,
					"bSort" : true,
					"bInfo" : true,
					"bAutoWidth" : false,
					"serverSide" : true,
					"searching" : true,
					"aoColumnDefs" : [ {
						"bSortable" : false,
						"aTargets" : [ 0 ]
					} ],
					"fnServerData" : function(sSource, aoData, fnCallback,
							oSettings) {
						RemoteManager.getAllCustomTagsMetadataDetail(JSON
								.stringify(aoData), {
							async : false,
							callback : function(result) {
								fnCallback(DDT.fillTagDetails(result));
								$(window).trigger('resize');
							}
						});

					},
					"aoColumns" : [ {
						"sTitle" : ""
					},// '<input type="checkbox" id="selectAll"
					// onclick="javascript:DDT.selectAllMigration(this)">'},
					{
						"sTitle" : "Tag ID"
					}, {
						"sTitle" : "Description"
					}, {
						"sTitle" : "Database"
					}, {
						"sTitle" : "Tag Value"
					}, /*
						 * { "sTitle" : "Conditional Expression" },
						 */{
						"sTitle" : "Frequency"
					}, {
						"sTitle" : "Is Enabled"
					} ]
				});

		$('#data_tag_table_length').css('margin-top', 7 + 'px');
		$('#data_tag_table_length').css('margin-bottom', 7 + 'px');
		$('#data_tag_table_filter').css('margin-top', 7 + 'px');
		$('#data_tag_table_filter').css('margin-bottom', 7 + 'px');
	},

	fillTagDetails : function(tagDataResult) {
		var tableList = new Array();
		// AHQ.total = 0;
		DDT.allTagArray.splice(0, DDT.allTagArray.length);
		var tagQueryData = tagDataResult["data"];
		if ((tagQueryData != null) && (tagQueryData.length > 0)) {
			DDT.jqGridColumnDetails.splice(0, DDT.jqGridColumnDetails.length);
			DDT.total = tagQueryData.length;
			for (var i = 0; i < tagQueryData.length; i++) {
				var tagData = tagQueryData[i];

				var tagID = tagData[0] // TAGID;
				DDT.allTagArray.push(tagID);
				var tagJSON = tagData[1] // JSON

				if (tagJSON == null) {
					continue;
				}
				var json = JSON.parse(tagJSON);

				var condexp = '';
				var expr = json["Tags"][0]["Expressions"];
				var rel = json["Tags"][0]["Relations"];
				var tagValue = '';
				var tagsArr = json["Tags"];
				for (var tagSeq = 0; tagSeq < tagsArr.length; tagSeq++) {
					if (tagsArr[tagSeq].hasOwnProperty("TagValue")) {
						tagValue += tagsArr[tagSeq]["TagValue"];
						if (tagSeq != (tagsArr.length - 1))
							tagValue += ", "; // TAGVALUE
					}
				}

				var desc = tagData[2] // DESCRIPTION;
				var enabled = tagData[3] // ISACTIVE;
				var enb_input = '';

				if (enabled == 'TRUE' || enabled == 'true') {

					isactive = 'Enabled';
					enb_input = '<input type="checkbox" checked="checked" value="'
							+ tagID
							+ '" onclick="javascript:DDT.enableColumnChanged(\''
							+ tagID + '\');" id="enbColumn' + tagID + '">';
				} else {
					enb_input = '<input type="checkbox"   value="'
							+ tagID
							+ '" onclick="javascript:DDT.enableColumnChanged(\''
							+ tagID + '\');" id="enbColumn' + tagID + '">';
					isactive = 'Disabled';
				}

				var dbtype = tagData[4] // DATABASE;
				var filetype = tagData[5] // FILETYPE;
				var namenodeId = tagData[6] // NAMENODEID;
				var tableName = tagData[7] // TABLENAME;
				var scheduleInfo = tagData[8] // SCHEDULE_INFO;

//				console.log('scheduleInfo :: ', scheduleInfo);
				// var tagParserObj = eval("(" + Tag["dataTaggingTimeInfo"] +
				// ")");

				var tagParser = '';
				var Frequency = '';

				if (scheduleInfo["isPostIngest"]) {
					Frequency = 'Every '
							+ scheduleInfo["postIngestTimeDetail"]["Frequency"]
							+ ' '
							+ scheduleInfo["postIngestTimeDetail"]["timeUnit"];
					tagParser = Frequency + " as Post-Ingest Job";

				} else {
					tagParser = 'Each time during On-Ingest'

				}
				var jobNames = tagData[9] // JOB_NAMES;

				tableList.push([
						'<input type="checkbox" onClick="javascript:DDT.TagSelected(this)" id="'
								+ tagID + '">', tagID, desc, dbtype, tagValue,
						tagParser, enb_input ]);
			}
			tagDataResult["data"] = tableList;
		}
		return tagDataResult;

		// 		
		// 		
		// 		
		// /////////////////////////////
		// var tabledata = [];
		// if (tagDataResult != null) {
		// 
		// for (var i = 0; i < tagDataResult.length; i++) {
		// var Tag = tagDataResult[i];
		// 				
		// console.log('TAG :: ' , Tag);
		// if (Tag == null || Tag == undefined)
		// continue;
		// 
		// DDT.totalDDT++;
		// 
		// var tagid = Tag["id"];
		// var desc = Tag["desc"];
		// var enabled = Tag["isActive"];
		// var db_type = Tag["db_type"];
		// var tablename = Tag["tableName"];
		// var enb_input = '';
		// if (enabled == 'true') {
		// 
		// isactive = 'Enabled';
		// enb_input = '<input type="checkbox" checked="checked" value="'
		// + tagid
		// + '" onclick="javascript:DDT.enableColumnChanged(\''
		// + tagid + '\');" id="enbColumn' + tagid + '">';
		// } else {
		// enb_input = '<input type="checkbox" value="'
		// + tagid
		// + '" onclick="javascript:DDT.enableColumnChanged(\''
		// + tagid + '\');" id="enbColumn' + tagid + '">';
		// isactive = 'Disabled';
		// }
		// var JSONstring = Tag["json"];
		// if (JSONstring == null) {
		// continue;
		// }
		// var json = JSON.parse(JSONstring);
		// var condexp = '';
		// var expr = json["Tags"][0]["Expressions"];
		// var rel = json["Tags"][0]["Relations"];
		// var tagValue = '';
		// var tagsArr = json["Tags"];
		// for (var tagSeq = 0; tagSeq < tagsArr.length; tagSeq++) {
		// if (tagsArr[tagSeq].hasOwnProperty("TagValue")) {
		// tagValue += tagsArr[tagSeq]["TagValue"];
		// if (tagSeq != (tagsArr.length - 1))
		// tagValue += ", ";
		// }
		// }
		// 
		// // Apply data tag related json
		// // var str = '
		// //
		// {"postIngestTimeDetail":{"Frequency":"131","timeUnit":"Hours","StartingTime":"4/10/2013
		// //
		// 17:48:40"},"isPostIngest":true,"applyTag":true,"applyTagTimeDetail":{"isApplyNow":true,"scheduleTime":"0"}}'
		// var tagParserObj = eval("(" + Tag["dataTaggingTimeInfo"] + ")");
		// 
		// // var tagParserObj = ;
		// 
		// var tagParser = '';
		// var Frequency = '';
		// 
		// if (tagParserObj["isPostIngest"]) {
		// Frequency = 'Every '
		// + tagParserObj["postIngestTimeDetail"]["Frequency"]
		// + ' '
		// + tagParserObj["postIngestTimeDetail"]["timeUnit"];// +'
		// // started
		// // at
		// // '+tagParserObj["postIngestTimeDetail"]["StartingTime"];
		// tagParser = Frequency + " as Post-Ingest Job";
		// 
		// } else {
		// tagParser = 'Each time during On-Ingest'
		// 
		// }

		// 			
		// 			
		// }
		// }
	},
	enableColumnChanged : function(tagid) {
		var val = $('#enbColumn' + tagid).is(':checked');
		RemoteManager.updateCustomTagMetadatDataIsColumnValue(tagid, val,
				DDT.handleColumnChangeResp);
	},

	handleColumnChangeResp : function(response) {
		// console.log(response);
	},

	deleteTag : function() {
		jConfirm("Are you sure you want to delete selected tags(s)?",
				'Confirm Delete', function(val) {
					if (val == true) {
						DDT.currentOperation = 'delete';
						Util.addLightbox('add_confirm', 'pages/popup.jsp');
						RemoteManager.deleteCustomTagMetadatData(
								DDT.selectedTag, DDT.responseCallBack);
					} else {
						return;
					}
				});
	},

	deleteResponse : function(response) {
		if (response.taskSuccess == true) {
			jAlert("Tag deleted Successfully.", "Success");
			$("#popup_container").css("z-index", "99999999");
			Navbar.refreshView();
		} else {
			jAlert("Error while deleting tag..", "Error");
			$("#popup_container").css("z-index", "99999999");
		}
	},

	responseCallBack : function(response) {
		var operSuc;
		var operFail;
		var message;
		var status;
		var imgId;
		var id = DDT.currentOperation;

		if (id == 'add') {
			operSuc = "added";
			operFail = "add";
		} else if (id == 'delete') {
			operSuc = "deleted";
			operFail = "delete";
		} else if (id == 'edit') {
			operSuc = "saved";
			operFail = "save";
		}

		if (response.taskSuccess == true) {
			message = "Tag " + operSuc + " successfully."
			status = "Success";
			imgId = "popup.image.success";
		} else {
			message = "Failed to " + operFail + " tag.";
			status = "Failure";
			imgId = "popup.image.fail";
			// var log = '<a href="javascript:Navbar.showServerLog();">View
			// Log</a>';
			// document.getElementById('log_div'+ id).innerHTML=log;
			// document.getElementById('log_div'+ id).style.display="block";
		}

		dwr.util.setValue('popup.message' + id, message);
		dwr.util.setValue('popup.status' + id, status);
		dwr.util.byId('ok.popup').disabled = false;
		dwr.util.byId('popup.image.processing' + id).style.display = 'none';
		dwr.util.byId(imgId + id).style.display = '';
	},

	closeBox : function(isRefresh) {
		Util.removeLightbox("addJN");
		if (isRefresh)
			Navbar.refreshView();

	},

	selectAllMigration : function(element) {

		var val = element.checked;
		for (var i = 0; i < document.forms[0].elements.length; i++) {
			var e = document.forms[0].elements[i];
			if ((e.id != 'selectAll') && (e.type == 'checkbox')) {
				e.checked = val;
				// DDT.clickCheckBox(e.id);
			}
		}
		DDT.enableDisableButton();
	},

	TagSelected : function(element) {
		var TagId = element.id;
		if (element.checked) {
			DDT.selectedTag.push(TagId);
		} else {
			for (var i = 0; i < DDT.selectedTag.length; i++) {
				if (DDT.selectedTag[i] == TagId) {
					DDT.selectedTag.splice(i, 1);
				}
			}
		}
		DDT.enableDisableButton();
	},

	enableDisableButton : function() {
		if (DDT.selectedTag.length > 0) {
			if (DDT.selectedTag.length == 1) {
				$('#editTag').removeAttr("disabled");
				DDT.activeInactiveButton();
			} else {
				$('#editTag').attr("disabled", true);
				$('#activateTag').removeAttr("disabled");
				$('#deactivateTag').removeAttr("disabled");
			}
			$('#deleteTag').removeAttr("disabled");
		} else {
			$('#editTag').attr("disabled", true);
			$('#deleteTag').attr("disabled", true);
			$('#activateTag').attr("disabled", true);
			$('#deactivateTag').attr("disabled", true);
		}
		// if(DDT.tagArray.length == 0)
		// $('#selectAll').prop('disabled',true);
		// else
		// $('#selectAll').prop('disabled',false);
	},

	activeInactiveButton : function() {
		var id = DDT.selectedTag[0];
		var list = DDT.tagArray;
		for (var i = 0; i < list.length; i++) {
			var Tag = list[i];
			var tagid = Tag["id"];
			if (tagid == id) {
				var enabled = Tag["isActive"];
				if (enabled == 'true') {
					$('#activateTag').attr("disabled", true);
					$('#deactivateTag').removeAttr("disabled");
				} else {
					$('#activateTag').removeAttr("disabled");
					$('#deactivateTag').attr("disabled", true);
				}
				break;
			}
		}
	},

	newJournalReady : function() {

		RemoteManager.getAllHostDetails(DDT.fillHostName);
		RemoteManager.getNonStandByTags(DDT.fillNameTag);
		$('#TagId').val('DataTag' + (DDT.totalDDT + 1));

	},

	configDDT : function() {
		config_TagId = DDT.selectedTag[0];
		Util.importResource("service_ref", "resources/nn_config.html");
	},

	// startDDT : function(){
	//
	// DDT.operation='Start';
	// Util.addLightbox("addDDT", "resources/journal_operation.html", null,
	// null);
	//			
	// },
	// TagStarted : function(dwrResponse){
	// jAlert(dwrResponse.message);
	// },
	// stopDDT : function(){
	// DDT.operation='Stop';
	// Util.addLightbox("addDDT", "resources/journal_operation.html", null,
	// null);
	// },

	deleteDDT : function() {
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton = ' No';
		jConfirm(
				'Are you sure you want to delete selected item(s)?',
				'Delete Journal Tag(s)',
				function(val) {
					if (val == true) {
						DDT.operation = 'Delete';
						Util.addLightbox("addDDT",
								"resources/journal_operation.html", null, null);
					} else {
						return;
					}
				});
	},

	// TagStopped : function(dwrResponse){
	// jAlert(dwrResponse.message);
	// },
	//		
	// saveTag : function(){
	// var host = $('#hostIP').val();
	// var nameTag = $('#nameTag').val();
	// var TagId = $('#TagId').val();
	// var serverPort = $('#serverPort').val();
	// var httpPort = $('#httpPort').val();
	//			
	// var dirPath= $('#dirPath').val();
	// var jmxPort=$('#jmxPort').val();
	//			
	// if(host==""){
	// jAlert("Host was not selected.Please select a host for journal
	// Tag.","Incomplete detail.");
	// $("#popup_container").css("z-index","9999999");
	// return;
	// }
	// if(nameTag==""){
	// jAlert("NameTag was not selected.Please select a NameTag .","Incomplete
	// detail.");
	// $("#popup_container").css("z-index","9999999");
	// return;
	// }
	// if(TagId==""){
	// jAlert("Please provide a Unique Identifier for journal Tag.","Incomplete
	// detail.");
	// $("#popup_container").css("z-index","9999999");
	// return;
	// }
	// if(dirPath==""){
	// jAlert("Please provide a directory path for installation of new journal
	// Tag.","Incomplete detail.");
	// $("#popup_container").css("z-index","9999999");
	// return;
	// }
	// if(Util.isContainWhiteSpace(dirPath)){
	// jAlert("Directory path contains space. Please remove space from Directory
	// path.","Incomplete Detail");
	// $("#popup_container").css("z-index","99999999");
	// return;
	// }
	// if(serverPort==""){
	// jAlert("Please provide server port for new journal Tag.","Incomplete
	// detail.");
	// $("#popup_container").css("z-index","9999999");
	// return;
	// }
	//			
	// if(httpPort==""){
	// jAlert("Please provide http port for new journal Tag.","Incomplete
	// detail.");
	// $("#popup_container").css("z-index","9999999");
	// return;
	// }
	// if(jmxPort==""){
	// jAlert("Please provide jmx port for new journal Tag.","Incomplete
	// detail.");
	// $("#popup_container").css("z-index","9999999");
	// return;
	// }
	//			
	//			
	// $('#popupTag').text(TagId);
	// $('#popupmessage').text('Installing Journal Tag at host '+$('#hostIP
	// option:selected').text());
	// $("#DataTag2").css("display","");
	// $("#DataTag1").css("display","none");
	// $('#otherInstruction_tr').remove();
	// $('#instruction_tr').remove();
	//			
	// // addDataTag(int hostId, String TagId, String dirPath, String
	// serverPort, String httpPort, String jmxPort)
	// RemoteManager.addDataTag(parseInt(host),TagId,dirPath,serverPort,httpPort,jmxPort,DDT.TagSaved);
	// },

	TagSaved : function(dwrResponse) {
		if (dwrResponse.taskSuccess) {
			$('#popupmessage').text(dwrResponse.responseMessage);
			$('#popupstatus').text('Success');
			$("#imageprocessing").css("display", "none");
			$("#imagesuccess").css("display", "");
			if ($('#isDataTagStart').is(":checked")) {
				DDT.selectedTag = [];
				DDT.selectedTag.push(dwrResponse.id);
				Util.removeLightbox("addDDT");
				DDT.startDDT();
				return;
			}
		} else {

			var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
			document.getElementById('log_div').innerHTML = log;
			document.getElementById('log_div').style.display = "block";

			$('#popupmessage').text(dwrResponse.responseMessage);
			$('#popupstatus').text('Failed');
			$("#imageprocessing").css("display", "none");
			$("#imagefail").css("display", "");
		}
		$("#okpopup").removeAttr("disabled");
	},

	// startTagOperation : function(){
	//			
	// var operation =DDT.operation;
	// for(var i =0;i<DDT.selectedTag.length;i++){
	// var id = DDT.selectedTag[i];
	// dwr.util.setValue('popupcomponent','Journal Tag');
	// dwr.util.cloneTag('poppattern',{ idSuffix:id });
	// dwr.util.setValue('popuphost' + id,id);
	// dwr.util.setValue('popupmessage' + id,operation+ ' operation performed on
	// '+id);
	// dwr.util.setValue('popupstatus' + id,'Processing');
	// dwr.util.byId('poppattern' + id).style.display = '';
	// }
	//			
	// if(DDT.operation=='Start'){
	// for(var i =0;i<DDT.selectedTag.length;i++){
	// //start Tag call.
	// RemoteManager.startTag(DDT.selectedTag[i] , false
	// ,DDT.TagOperationPerformed);
	// }
	// }else if(DDT.operation=='Stop'){
	// for(var i =0;i<DDT.selectedTag.length;i++){
	// //start Tag call.
	// RemoteManager.stopTag(DDT.selectedTag[i],DDT.TagOperationPerformed);
	// }
	// }else if(DDT.operation=='Delete'){
	// for(var i =0;i<DDT.selectedTag.length;i++){
	// //start Tag call.
	// RemoteManager.deleteTag(DDT.selectedTag[i],DDT.TagOperationPerformed);
	// }
	// }
	// },
	// TagOperationPerformed : function(dwrResponse){
	// var id = dwrResponse.id;
	// if(dwrResponse.taskSuccess)
	// {
	// img_src='images/Success_img.png'
	// status = 'Success';
	// dwr.util.byId('imagesuccess' + id).style.display = '';
	// }
	// else
	// {
	// img_src='images/Fail_img.png'
	// status = 'Fail';
	// dwr.util.byId('imagefail' + id).style.display = '';
	// }
	// dwr.util.byId('imageprocessing' + id).style.display = 'none';
	// dwr.util.setValue('popupmessage' + id,dwrResponse.responseMessage);
	// dwr.util.setValue('popupstatus' + id,status);
	// document.getElementById('okpopup').disabled = false;
	//			
	// },

	activeTag : function(flag) {
		if (flag == 1) {
			// activate
		} else {
			// deactivate
		}
	},

	AddTag : function() {
		Util.addLightbox('mdt_Box', 'resources/add_MigrationDataTagger.html');
		// Util.addLightbox('mdt_Box','resources/add_NewMigrationDataTagger.html');
		$("#popup_container").css("z-index", "99999999");
	},

	editTag : function() {
		Util.addLightbox('mdt_Box', 'resources/edit_MigrationDataTagger.html');
		// Util.addLightbox('mdt_Box',
		// 'resources/edit_NewMigrationDataTagger.html');
		$("#popup_container").css("z-index", "99999999");
	}

};