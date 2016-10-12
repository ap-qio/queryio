TagManager = {
	parserType : '',
	counter : 1,
	file : [],
	tagData : [],
	ingestTags : [],
	postIngestTags : [],
	tagSelected : [],
	postTagSelected : [],
	editTagName : [],
	firstTimePostIngest : true,
	firstTimeIngest : true,
	isAdd : false,
	isEdit : false,
	isIngest : false,
	isEditClicked : false, 
	currentTable : '',
	
	isJarUpdated: false,
	
	libJarsArray : [],
	counter: 1,
	rowCounter: 1,

	
	ready : function()
	{
		$("#editButton").attr("disabled", true);
		$("#deleteButton").attr("disabled", true);
		$("#editPostButton").attr("disabled", true);
		$("#deletePostButton").attr("disabled", true);
		TagManager.populateIngest();
		TagManager.populatePostIngest();
	},
	
	resetFields : function()
	{
		TagManager.tagSelected.splice(0, TagManager.tagSelected.length);
		TagManager.postTagSelected.splice(0, TagManager.postTagSelected.length);
		$("#editButton").attr("disabled", true);
		$("#deleteButton").attr("disabled", true);
		$("#editPostButton").attr("disabled", true);
		$("#deletePostButton").attr("disabled", true);
		TagManager.isJarUpdated = false;
   		TagManager.counter = 1;
		TagManager.rowCounter = 1;
		TagManager.libJarsArray = [];
		TagManager.currentTable = "";
		$('input:checkbox').removeAttr('checked');
	},
	
	closeBox : function()
   	{
		TagManager.resetFields();
   		Util.removeLightbox("addtag");
   	},
   	
   	closePostTagBox : function()
   	{
   		TagManager.resetFields();
   		Util.removeLightbox("addPosttag");
   	},
   	
   	closeEditBox : function()
   	{
   		TagManager.resetFields();
   		Util.removeLightbox("edittag");
   	},
   
   	closePostIngestEditBox : function()
   	{
   		TagManager.resetFields();
   		Util.removeLightbox("editPostIngesttag");
   	},

   	addTag: function ()
	{
   		TagManager.currentTable = "tagsIngestTable";
   		TagManager.setStatus(true, false);
		Util.addLightbox("addtag", "resources/addIngest.html", null, null);
	},
	
	addPostTag: function ()
	{
		TagManager.currentTable = "tagsPostIngestTable";
		TagManager.setStatus(true, false);
		Util.addLightbox("addPosttag", "resources/addPostIngest.html", null, null);
	},
	
	accessTags : function(data)
	{
		TagManager.tagData = data;
		
		if(data == null || data == undefined)
		{
			$("#tagsIngestTable").html('<tr><td style="text-align:center;"><span>Table info not available. </span></td></tr>');
			return;
		}
		
		var colList = [];
		colList.push({ "sTitle":'<input type="checkbox" value="tag" id="selectAllTags" onclick="javascript:TagManager.selectAllTagsRow(this.id)" >' });
		colList.push({ "sTitle":'Tag Name'});
		colList.push({ "sTitle":'Tag Description'});
		colList.push({ "sTitle":'JAR'});
		colList.push({ "sTitle":'File Types'});
		colList.push({ "sTitle":'Class Name'});
		colList.push({ "sTitle":'NameNode'});
		colList.push({ "sTitle":'State'});
		
		var tableRow = [];
		for(var i=0; i<TagManager.tagData.length; i++)
		{
			var row = TagManager.tagData[i];

			var rowData = new Array();
			rowData.push('<input type="checkbox" value="tag-'+row.id+'" onClick="javascript:TagManager.clickBox(this.id)" id="tag-'+row.id+'" >');
			rowData.push(row.tagName);
			rowData.push(row.description);
			rowData.push(row.jarName);
			var filetype=row.fileTypes;
//			filetype=filetype.replace(/,/g,",&#8203;");
			//console.log("FileType : "+filetype);
			rowData.push(filetype);
			rowData.push(row.className);
			rowData.push(row.namenodeId);
			if (row.isActive)
				rowData.push("Active");
			else
				rowData.push("Inactive");
			
			TagManager.ingestTags.push("tag-"+row.id);
			tableRow.push(rowData);
		}
		
		if(TagManager.firstTimeIngest)
		{
			$('#tagsIngestTable').dataTable
			({		       
				"bPaginate": false,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": true,
				"bInfo": false,
				"bDestroy": true,
				"bAutoWidth": false,
				"aoColumns": colList,
				"aaData": tableRow
		    });
			TagManager.firstTimeIngest = false;
		}
		else
		{
			var oTable = $('#tagsIngestTable').dataTable();
			oTable.fnClearTable();
			for(i=0; i<tableRow.length; i++)
				oTable.fnAddData(tableRow[i]);
		}
		
		if(data == null || data == undefined || data.length == 0)
			document.getElementById('selectAllTags').disabled = true;
		else
			document.getElementById('selectAllTags').disabled = false;
		
		TagManager.tagSelected.splice(0, TagManager.tagSelected.length);
	},
	
	populateIngest : function()
	{
		RemoteManager.getAllOnIngestTagParserConfigs(TagManager.accessTags);
	},
	
	accessPostIngestTags : function(data)
	{
		TagManager.tagData = data;
		
		if(data == null || data == undefined)
		{
			$("#tagsPostIngestTable").html('<tr><td style="text-align:center;"><span>Table info not available. </span></td></tr>');
			return;
		}
		
		var colList = [];
		colList.push({ "sTitle":'<input type="checkbox" value="tag" id="selectAllPostTags" onclick="javascript:TagManager.selectAllPostTagsRow(this.id)" >' });
		colList.push({ "sTitle":'Tag Name'});
		colList.push({ "sTitle":'Tag Description'});
		colList.push({ "sTitle":'JAR'});
		colList.push({ "sTitle":'File Types'});
		colList.push({ "sTitle":'Class Name'});
		colList.push({ "sTitle":'NameNode'});
		colList.push({ "sTitle":'ResourceManager'});
		
		
		var tableRow = [];
		for(var i=0; i<TagManager.tagData.rows.length; i++)
		{
			var row = TagManager.tagData.rows[i];

			var rowData = new Array();
			rowData.push('<input type="checkbox" value="posttag-'+row[0]+'" onClick="javascript:TagManager.clickPostIngestBox(this.id)" id="posttag-'+row[0]+'" >');
			rowData.push(row[1]);
			rowData.push(row[2]);
			rowData.push(row[3]);
			rowData.push(row[4]);
			rowData.push(row[5]);
			rowData.push(row[6]);
			rowData.push(row[7]);
			rowData.push(row[8]);
			
			TagManager.postIngestTags.push("posttag-"+row[0]);
			tableRow.push(rowData);
		}
		
		if(TagManager.firstTimePostIngest)
		{
			$('#tagsPostIngestTable').dataTable
			({		       
				"bPaginate": false,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": true,
				"bInfo": false,
				"bDestroy": true,
				"bAutoWidth": false,
				"aoColumns": colList,
				"aaData": tableRow
			});
			TagManager.firstTimePostIngest = false;
		}
		else
		{
			var oTable = $('#tagsPostIngestTable').dataTable();
			oTable.fnClearTable();
			for(i=0; i<tableRow.length; i++)
				oTable.fnAddData(tableRow[i]);
		}
		
		if(data == null || data == undefined || data.rows.length == 0)
			document.getElementById('selectAllPostTags').disabled = true;
		else
			document.getElementById('selectAllPostTags').disabled = false;
		
		TagManager.postTagSelected.splice(0, TagManager.postTagSelected.length);
	},
	
	fillClusterNameNodeID : function(val, isEdit){
		TagManager.isEditClicked = isEdit;
		TagManager.isIngest = val;
		RemoteManager.getAllNameNodeForDBNameMapping(TagManager.populateClusterNameNodeIds);
	},
		
	populateClusterNameNodeIds : function(list)
	{
		var data = "";
		
		data = Util.getCurrentIdDropDown(list, "Select NameNode", "Select NameNode");
		
		$('#NameNodeId').html(data);
		
		if (TagManager.isIngest && TagManager.isEditClicked)
			TagManager.fillEditForm();
	},
	
	fillResourceManagerIds: function()
	{
		RemoteManager.getAllResourceManagers(TagManager.populateResourceManager);
	},
	
	populateResourceManager: function(list)
	{
		var data = "";
		
		data = Util.getCurrentIdDropDown(list, "Select ResourceManager", "Select ResourceManager");
		
		$('#ResourceManagerId').html(data);
		
		if(TagManager.isEditClicked)
			TagManager.fillPostIngestEditForm();
	},
	
	populatePostIngest : function()
	{
		RemoteManager.getAllPostIngestTagParserConfigs(TagManager.accessPostIngestTags);
	},
	
	fillEditForm : function()
	{
		var name = '';
		var desc = '';
		var jar = '';
		var fileTypes = '';
		var className = '';
		var isActive = '';
		var namenodeId = '';

		var tagName = '';
		var tagPos = '';
		var frm = document.getElementById('tagsIngestTable').getElementsByTagName("input");
		var len = frm.length;
		for (i=0; i<len; i++) 
		{
		    if (frm[i].type === "checkbox")
		    {
		    	if (frm[i].checked)
		    	{
		    		tagName = frm[i].id;
		    		TagManager.editTagName = tagName;
		    		tagPos = i-1;
		    	}
		    }
		}
		
		var rows = $("#tagsIngestTable tbody tr");
		name = $(rows[tagPos]).find("td:eq(1)").html();
		desc = $(rows[tagPos]).find("td:eq(2)").html();
		
		var libJars = $(rows[tagPos]).find("td:eq(3)").html();
		if ((libJars != null) && (libJars != ''))
		{
			TagManager.libJarsArray = libJars.split(",");
		}
		
		if ((TagManager.libJarsArray != null) && (TagManager.libJarsArray.length > 0))
		{
			jar = TagManager.libJarsArray[0];
			var isRemoveLib = false;
			for (var i=1; i<TagManager.libJarsArray.length; i++)
			{
				dwr.util.byId('chooseFileTagFile' + i).style.display = '';
				dwr.util.byId('fileTag' + i).style.display = 'none';
				dwr.util.byId('fileTagFT' + i).style.display = '';
				$('#fileTagFT' + i).val(TagManager.libJarsArray[i]);
				isRemoveLib = true;
				TagManager.addLibClicked();
			}
			if (isRemoveLib)
				TagManager.removeLibClicked(i);				
		}
		
		fileTypes = $(rows[tagPos]).find("td:eq(4)").html();
		className = $(rows[tagPos]).find("td:eq(5)").html();
		namenodeId = $(rows[tagPos]).find("td:eq(6)").html();
		isActive = $(rows[tagPos]).find("td:eq(7)").html();
		
		
		$("#tagName").val(name);
		$("#tagShowName").val(name);
		$("#tagDesc").val(desc);
		$("#fileTagOld").val(jar);
		$("#class_name").val(className);
		$("#file_type").val(fileTypes);
		
		if (isActive === "Active")
		{
			$("#labelActive").val(true);
			$("#checkActive").prop('checked', true);
		}
		else
		{
			$("#labelActive").val(false);
			$("#checkActive").prop('checked', false);
		}
		
		if (namenodeId != '')
			$("#NameNodeId").val(namenodeId);
	},
	
	overWriteExisting : function()
	{
		var rows = $("#" + TagManager.currentTable).dataTable().fnGetNodes();
		for(var i = 0; i < rows.length; i++)
		{
			var name = $(rows[i]).find("td:eq(1)").html();
			if(name == $("#tagName").val())
			{
				return true;
			}
		}
		return false;
	},
	
	setStatus : function(addStatus, editStatus)
	{
		TagManager.isAdd = addStatus;
		TagManager.isEdit = editStatus;
	},
	
	editTag : function()
	{
		if($("#tagName").val() == "")
		{
			jAlert("Tag Name required.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
		}
		else if($("#tagDesc").val() == "")
		{
			jAlert("Tag Description required.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
		}
		else if($("#file_type").val() == "")
		{
			jAlert("File type required.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
		}
		else if($("#class_name").val() == "")
		{
			jAlert("Class Name required.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
		}
		else if($("#NameNodeId").val() == "Select NameNode")
		{
			jAlert("Name Node required.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
		}
		else
		{
			var name = '';
			var desc = '';
			var jar = '';
			var fileTypes = '';
			var className = '';
			var nameNode = '';
			
			name = $("#tagName").val();
			desc = $("#tagDesc").val();
			
			var value = $("#chooseFile").val();
			if(value == "Change")
			{
				var temp = $("#fileTagOld").val();
				$("#fileTagText").val(temp);
				$("#fileTag").remove();
			}
			else
			{
				if($("#fileTag").val() == "")
				{
					jAlert("JAR required.","Incomplete Detail");
					$("#popup_container").css("z-index","99999999");
				}
				else
				{
					var file = $("#fileTag").val();
					file = file.substring(file.lastIndexOf("\\")+1, file.length);
					$("#fileTagText").val(file);
				}
			}
			
			jar = $("#fileTagText").val();
			className = $("#class_name").val();
			fileTypes = $("#file_type").val();
			nameNode = $("#NameNodeId").val();
			
			//RemoteManager.updateOnIngestTagParserConfig(parseInt(Id), name, desc, jar, fileTypes, className, TagManager.updated);
//			TagManager.fileUpload();
			TagManager.setStatus(false, true);
			TagManager.saveTag();
		}
	},
	
	fillPostIngestEditForm : function()
	{
		var name = '';
		var desc = '';
		var jar = '';
		var fileTypes = '';
		var className = '';
		
		var namenodeId = '';
		var rmId = '';
		
		var tagName = '';
		var tagPos = '';
		var frm = document.getElementById('tagsPostIngestTable').getElementsByTagName("input");
		var len = frm.length;
		for (i=0; i<len; i++) 
		{
		    if (frm[i].type == "checkbox") 
		    {    
		    	if(frm[i].checked)
		    	{
		    		tagName = frm[i].id;
		    		TagManager.editTagName = tagName;
		    		tagPos = i-1;
		    	}
		    }
		}
		var rows = $("#tagsPostIngestTable").dataTable().fnGetNodes();
		name = $(rows[tagPos]).find("td:eq(1)").html();
		desc = $(rows[tagPos]).find("td:eq(2)").html();
		
		var libJars = $(rows[tagPos]).find("td:eq(3)").html();
		if ((libJars != null) && (libJars != ''))
		{
			TagManager.libJarsArray = libJars.split(",");
		}
		
		if ((TagManager.libJarsArray != null) && (TagManager.libJarsArray.length > 0))
		{
			jar = TagManager.libJarsArray[0];
			var isRemoveLib = false;
			for (var i=1; i<TagManager.libJarsArray.length; i++)
			{
				dwr.util.byId('chooseFileTagFile' + i).style.display = '';
				dwr.util.byId('fileTag' + i).style.display = 'none';
				dwr.util.byId('fileTagFT' + i).style.display = '';
				$('#fileTagFT' + i).val(TagManager.libJarsArray[i]);
				isRemoveLib = true;
				TagManager.addLibClicked();
			}
			if (isRemoveLib)
				TagManager.removeLibClicked(i);				
		}
		
		fileTypes = $(rows[tagPos]).find("td:eq(4)").html();
		className = $(rows[tagPos]).find("td:eq(5)").html();
		namenodeId = $(rows[tagPos]).find("td:eq(6)").html();
		rmId = $(rows[tagPos]).find("td:eq(7)").html();
		
		$("#tagName").val(name);
		$("#tagShowName").val(name);
		$("#tagDesc").val(desc);
		$("#fileTagOld").val(jar);
		$("#class_name").val(className);
		$("#file_type").val(fileTypes);
		
		if (namenodeId != '')
			$("#NameNodeId").val(namenodeId);
		
		if (rmId != '')
			$("#ResourceManagerId").val(rmId);
			
	},
	
	editPostIngestTag : function()
	{
		if($("#tagName").val() == "")
		{
			jAlert("Tag Name required.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
		}
		else if($("#tagDesc").val() == "")
		{
			jAlert("Tag Description required.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
		}
		else if($("#file_type").val() == "")
		{
			jAlert("File type required.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
		}
		else if($("#class_name").val() == "")
		{
			jAlert("Class Name required.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
		}
		else if($("#NameNodeId").val() == "Select NameNode")
		{
			jAlert("Name Node required.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
		}
		else
		{
			var name = '';
			var desc = '';
			var jar = '';
			var fileTypes = '';
			var className = '';
			var nameNode = '';
			
			name = $("#tagName").val();
			desc = $("#tagDesc").val();
			
			var value = $("#chooseFile").val();
			if(value == "Change")
			{
				var temp = $("#fileTagOld").val();
				$("#fileTagText").val(temp);
				$("#fileTag").remove();
			}
			else
			{
				if($("#fileTag").val() == "")
				{
					jAlert("JAR required.","Incomplete Detail");
					$("#popup_container").css("z-index","99999999");
				}
				else
				{
					var file = $("#fileTag").val();
					file = file.substring(file.lastIndexOf("\\")+1, file.length);
					$("#fileTagText").val(file);
				}
			}
			
			jar = $("#fileTagText").val();
			className = $("#class_name").val();
			fileTypes = $("#file_type").val();
			nameNode = $("#NameNodeId").val();
			
			var Id = TagManager.editTagName.substring(8, TagManager.editTagName.length);
			
			TagManager.setStatus(false, true);
			TagManager.saveTag();
		}
	},
	
	
	
	showChooseFile : function()
	{
		var value = $("#chooseFile").val();
		if(value == "Change")
		{
			$("#chooseFile").val("Keep Unchanged");
			$("#fileTag").show();
			$("#fileTagOld").hide();
		}
		else
		{
			$("#chooseFile").val("Change");
			$("#fileTagOld").show();
			$("#fileTag").hide();
		}	
	},
	
	updated : function()
	{
		TagManager.closeEditBox();
		TagManager.closePostIngestEditBox();
		TagManager.ready();
	},
	populateDeleteTag : function(flag ,  dwrResponse)
	{
		var id='';
		var img_src='';
		var status='';
		if(flag){
			if(TagManager.parserType == "postIngest"){
				dwr.util.setValue('popup.component','Post Ingest-Tag Parser');
				var temp;
				var oTable = $('#tagsPostIngestTable').dataTable();
				$('#tagsPostIngestTable tbody tr').each(function()
				{
					var row = this.cells;
					var name = row[1];
					for(var j=0; j<TagManager.postTagSelected.length; j++)
					{
						if(row[0].firstChild.value == TagManager.postTagSelected[j])
						{
							oTable.fnDeleteRow(oTable.fnGetPosition(this));
							oTable.fnDraw();
							
							var index = jQuery.inArray(TagManager.postTagSelected[j], TagManager.postIngestTags);
							TagManager.postIngestTags.splice(index, 1);
							
							var Id = TagManager.postTagSelected[j].substring(8, TagManager.postTagSelected[j].length);
							dwr.util.cloneNode('pop.pattern',{ idSuffix:Id });
							dwr.util.setValue('popup.host' + Id,name);
							dwr.util.setValue('popup.message' + Id,"Delete selected item ...");
							dwr.util.setValue('popup.status' + Id,'Processing');
							dwr.util.byId('pop.pattern' + Id).style.display = '';
							
							RemoteManager.deleteTagParserConfig(Id, TagManager.deleted);
						}
					}
				});
			}else{
					
				dwr.util.setValue('popup.component','On Ingest-Tag Parser');
				var temp;
				var oTable = $('#tagsIngestTable').dataTable();
				$('#tagsIngestTable tbody tr').each(function()
				{
					var row = this.cells;
					var name = row[1];
					for(var j=0; j<TagManager.tagSelected.length; j++)
					{
						if(row[0].firstChild.value == TagManager.tagSelected[j])
						{
							oTable.fnDeleteRow(oTable.fnGetPosition(this));
							oTable.fnDraw();
							
							var index = jQuery.inArray(TagManager.tagSelected[j], TagManager.ingestTags);
							TagManager.ingestTags.splice(index, 1);
							
							var Id = TagManager.tagSelected[j].substring(4, TagManager.tagSelected[j].length);
							dwr.util.cloneNode('pop.pattern',{ idSuffix:Id });
							dwr.util.setValue('popup.host' + Id,name);
							dwr.util.setValue('popup.message' + Id,"Delete selected item ...");
							dwr.util.setValue('popup.status' + Id,'Processing');
							dwr.util.byId('pop.pattern' + Id).style.display = '';
							RemoteManager.deleteTagParserConfig(Id, TagManager.deleted);
						}
					}
			});
			}
		}
		else{
			
			id=dwrResponse.id;
			if(dwrResponse.taskSuccess){
				img_src='images/Success_img.png'
				status = 'Success'; 
				dwr.util.byId('popup.image.success' + id).style.display = '';
			}
			else{
				img_src='images/Fail_img.png'
				status = 'Fail';
				dwr.util.byId('popup.image.fail' + id).style.display = '';
				var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
				document.getElementById('log_div'+ id).innerHTML=log;
				document.getElementById('log_div'+ id).style.display="block";
			}
			dwr.util.byId('popup.image.processing' + id).style.display = 'none';
			dwr.util.setValue('popup.message' + id,dwrResponse.responseMessage);
			dwr.util.setValue('popup.status' + id,status);
			document.getElementById('ok.popup').disabled = false;
		}
	},
	deleteTag : function()
	{
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm('Are you sure you want to delete selected item(s)?','Delete On-Ingest Tag Parser(s)',function(val)
		{
			if (val == true)
			{
				TagManager.parserType = 'onIngest';
				Util.addLightbox('delete_pop_up','pages/popup.jsp',null,null);
			}
			else
			{
				return; 
			}
		});
	},
	
	deletePostIngestTag : function()
	{
		jQuery.alerts.okButton = ' Yes ';
		jQuery.alerts.cancelButton  = ' No';
		jConfirm('Are you sure you want to delete selected item(s)?','Delete Post-Ingest Tag Parser(s)',function(val)
		{
			if (val == true)
			{
				
				TagManager.parserType = 'postIngest';
				Util.addLightbox('delete_pop_up','pages/popup.jsp',null,null);
				
			}
			else
			{
				return; 
			}
		});
	},
	
	deleted : function(dwrResponse)
	{
		
		TagManager.populateDeleteTag(false,dwrResponse);
		
		
	},
	
	selectAllTagsRow: function(id)
	{
		var flag = document.getElementById(id).checked;
		
		TagManager.tagSelected.splice(0, TagManager.tagSelected.length);
		for (var i=0; i<TagManager.ingestTags.length; i++)
		{
			document.getElementById(TagManager.ingestTags[i]).checked = flag;
			if (flag)
				TagManager.tagSelected.push(TagManager.ingestTags[i]);
		}
		TagManager.toggleButton(id, flag);
	},
	
	selectAllPostTagsRow: function(id)
	{
		var flag = document.getElementById(id).checked;
		
		TagManager.postTagSelected.splice(0, TagManager.postTagSelected.length);
		for (var i=0; i<TagManager.postIngestTags.length; i++)
		{
			document.getElementById(TagManager.postIngestTags[i]).checked = flag;
			if (flag)
				TagManager.postTagSelected.push(TagManager.postIngestTags[i]);
		}
		TagManager.toggleButton(id, flag);
	},
	
	toggleButton: function(id, value, parent)
	{
		if (id == "selectAllTags")
		{
			if (TagManager.tagSelected.length == 1)
				$("#editButton").attr("disabled", false);
			else
				$("#editButton").attr("disabled", true);						
			$("#deleteButton").attr("disabled", !value);
		}
		else if (id == "selectAllPostTags")
		{
			if (TagManager.postTagSelected.length == 1)
				$("#editPostButton").attr("disabled", false);
			else
				$("#editPostButton").attr("disabled", true);						
			$("#deletePostButton").attr("disabled", !value);
		}
		else
		{
			if (parent == "selectAllTags")
			{
				if(value == false)
					$('#selectAllTags').attr("checked",false);
				
				if (TagManager.tagSelected.length < 1)
				{
					$("#editButton").attr("disabled", true);
					$("#deleteButton").attr("disabled", true);
				}
				else
				{
					if (TagManager.tagSelected.length == 1)
					{
						$("#editButton").attr("disabled", false);
					}
					else
					{
						$("#editButton").attr("disabled", true);						
					}
					$("#deleteButton").attr("disabled", false);
				}
			}
			else
			{
				if(value == false)
					$('#selectAllPostTags').attr("checked",false);
				
				if (TagManager.postTagSelected.length < 1)
				{
					$("#editPostButton").attr("disabled", true);
					$("#deletePostButton").attr("disabled", true);
				}
				else
				{
					if (TagManager.postTagSelected.length == 1)
					{
						$("#editPostButton").attr("disabled", false);
					}
					else
					{
						$("#editPostButton").attr("disabled", true);						
					}
					$("#deletePostButton").attr("disabled", false);
				}
			}
		}
	},
		
	clickBox : function(id)
	{
		var flag = document.getElementById(id).checked;
		if (flag == true)
		{
			TagManager.tagSelected.push(id.toString());
		}
		else
		{
			var index = jQuery.inArray(id.toString(), TagManager.tagSelected);
			if (index != -1)
			{
				TagManager.tagSelected.splice(index, 1);
			}
		}
		if(($('#tagsIngestTable tr').length - 1) == TagManager.tagSelected.length)
		{
			document.getElementById("selectAllTags").checked = flag;
			TagManager.selectAllTagsRow("selectAllTags", flag);
		}
		else
			TagManager.toggleButton(id, flag, "selectAllTags");
	},
	
	clickPostIngestBox : function(id)
	{
		var flag = document.getElementById(id).checked;
		if (flag == true)
		{
			TagManager.postTagSelected.push(id.toString());
		}
		else
		{
			var index = jQuery.inArray(id.toString(), TagManager.postTagSelected);
			if (index != -1)
			{
				TagManager.postTagSelected.splice(index, 1);
			}
		}
		if(($('#tagsPostIngestTable tr').length - 1) == TagManager.postTagSelected.length)
		{
			document.getElementById("selectAllPostTags").checked = flag;
			TagManager.selectAllPostTagsRow("selectAllPostTags", flag);
		}
		else
			TagManager.toggleButton(id, flag, "selectAllPostTags");
	},
	
	fileUpload : function(form)
	{
		var iframe = document.createElement("iframe");
        iframe.setAttribute("id", "upload_iframe");
        iframe.setAttribute("name", "upload_iframe");
        iframe.setAttribute("width", "0");
        iframe.setAttribute("height", "0");
        iframe.setAttribute("border", "0");
        iframe.setAttribute("style", "width: 0; height: 0; border: none;");
     
        var div_id = 'formDIV';
        // Add to document...
        form.parentNode.appendChild(iframe);
        window.frames['upload_iframe'].name = "upload_iframe";
     
        iframeId = document.getElementById("upload_iframe");
        $('#respProcessing').css('display','');
        $('#log_div').hide();
    	$('#respStatus').text('Processing');
    	$('#respFail').css('display','none');
    	$('#respSuccess').css('display','none');
        // Add event...
        var eventHandler = function () {
        	var content = new Object();
                if (iframeId.detachEvent) iframeId.detachEvent("onload", eventHandler);
                else iframeId.removeEventListener("load", eventHandler, false);
                
                // Message from server...
                if (iframeId.contentDocument) {
                    content = iframeId.contentDocument.body.textContent;
                } else if (iframeId.contentWindow) {
                    content = iframeId.contentWindow.document.body.textContent;
                } else if (iframeId.document) {
                    content = iframeId.document.body.textContent;
                }
                content=JSON.parse(content);

                
                $('#respProcessing').css('display','none');
                $('#respMessage').text(content.message);
                if(content.status=='fail'){
                	$('#log_div').html('<a href="javascript:TagManager.editSubmitedForm();">Edit Form</a>&nbsp;&nbsp;&nbsp;<a href="javascript:Navbar.showServerLog();">View Log</a>');
                	$('#log_div').show();
                	$('#respStatus').text('Fail');
                	$('#respFail').css('display','');
                }else{
                	$('#respStatus').text('Success');
                	$('#respSuccess').css('display','');
                }	
                
                $('#okpopup').removeAttr('disabled');
                
                //TagManager.ready();
//                Navbar.refreshView();
//                if(TagManager.isAdd)
//                {
////                	jAlert("Tag Parser added successfully.");                	
//                }
//                else if(TagManager.isEdit)
//                {
////                	jAlert("Tag Parser updated successfully.");                	                	
//                }
               
//                TagManager.closeBox();
//                TagManager.closePostTagBox();
//                TagManager.closeEditBox();
//                TagManager.closePostIngestEditBox();
//                TagManager.setStatus(false, false);
//              JB.fillResponsePopup(content);
            }
     
        if (iframeId.addEventListener) iframeId.addEventListener("load", eventHandler, true);
        if (iframeId.attachEvent) iframeId.attachEvent("onload", eventHandler);
     
        // Set properties of form...
        form.setAttribute("target", "upload_iframe");
        form.setAttribute("action", "TagParserFileUpload");
        form.setAttribute("method", "post");
        form.setAttribute("enctype", "multipart/form-data");
        form.setAttribute("encoding", "multipart/form-data");
        $('#parser').text($('#tagName').val());	
        $('#tagDiv').hide();
        $('#respMessage').text("Parser detail is uploading.");
		$('#processingDiv').show();  
		$('#close').hide();
        form.submit();
        
//        TagManager.closeBox();
//        TagManager.saveTag(2);
	},
	closePopUp : function(){
		TagManager.closeBox();
      TagManager.closePostTagBox();
      TagManager.closeEditBox();
      TagManager.closePostIngestEditBox();
      Navbar.refreshView();
	},
	editSubmitedForm : function(){
			$('#processingDiv').hide(); 
			$('#tagDiv').show();
			$('#close').show();
			
			
	},
	
	saveTag : function()
	{
		if($("#tagName").val() == "")
		{
			jAlert("You must specify the Tag Name.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			TagManager.setStatus(false, false);
		}else if(Util.isContainSpecialChar($("#tagName").val())){
			jAlert("Tag Name contains special character.Please remove special character from Tag Name.","Invalid Tag Name");
			$("#popup_container").css("z-index","9999999");
			TagManager.setStatus(false, false);
		}
		else if($("#tagDesc").val() == "")
		{
			jAlert("You must specify the Tag Description.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			TagManager.setStatus(false, false);
		}
		else if($("#fileTag").val() == "")
		{
			jAlert("You must specify the JAR file for the parser.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			TagManager.setStatus(false, false);
		}
		else if($("#saveFileTypes").val() == "Add" && TagManager.overWriteExisting())
		{
			jAlert('Tag Name already exists.','Conflicting Tag names');
			$("#popup_container").css("z-index","99999999");
			TagManager.setStatus(false, false);
		}
		else if($("#file_type").val() == "")
		{
			jAlert("You must specify the file types that you want to associate with this parser.","Insufficient Details");
			$("#popup_container").css("z-index","99999999");
			TagManager.setStatus(false, false);
		}
		else if($("#NameNodeId").val() == "Select NameNode")
		{
			jAlert("You must specify the Name Node.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
			TagManager.setStatus(false, false);
		}
		else if($("#ResourceManagerId").val() == "Select ResourceManager")
		{
			jAlert("You must specify the ResourceManager.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
			TagManager.setStatus(false, false);
		}
		else if($("#class_name").val() == "")
		{
			jAlert("Class Name required.","Incomplete Detail");
			$("#popup_container").css("z-index","99999999");
			TagManager.setStatus(false, false);
		}
		else
		{
			var flag = true;
			if($('#checkActive').is(':checked'))
			{
				var rows = $("#tagsIngestTable").dataTable().fnGetNodes();
				var fileType = $("#file_type").val();
				for(var i=0;i<rows.length;i++)
				{
					if(fileType == ($(rows[i]).find("td:eq(4)").html()) )
					{
						if(($(rows[i]).find("td:eq(7)").html()) == "Active" && (($(rows[i]).find("td:eq(1)").html()) != $('#tagShowName').val()))
						{
							flag = false;
							jAlert("Two parsers of same file type can not be active at same time.","Error");
							$("#popup_container").css("z-index","99999999");
							TagManager.setStatus(false, false);
						}
					}
				}
			}
			if(flag)
			{
				var jar;

				var isUploadJars = false;
				if (document.getElementById("fileTag") != null) {					
					jar = $("#fileTag").val();
					isUploadJars = true;
				}
				else {					
					jar = $("#fileTagText").val();
				}
				$(".fileTagClass").filter(':visible').each(function() {
					if($(this).val() != '') {
						isUploadJars = true;
					}
				});

				var extension = jar.substring(jar.length-4, jar.length);
				if(isUploadJars == true) 
				{					
					if(extension == ".jar" || extension == ".JAR")
					{
						var Id = document.getElementById('tagForm');
						TagManager.fileUpload(Id);
					}
					else
					{
						jAlert("Only JAR files are required to be uploaded.","Incorrect Detail");
						$("#popup_container").css("z-index","99999999");
					}
				}
				else
				{
					RemoteManager.updateOnIngestTagParserConfigExceptJarInfo($("#tagName").val(), $("#tagDesc").val(),
							$("#file_type").val(), $("#class_name").val(), $("#NameNodeId").val(), $('#checkActive').is(':checked'), TagManager.postIngestConfUpdated);
				}
			}
			
		}
	},
	
	postIngestConfUpdated : function(response)
	{
		console.log('response : ', response);
		$('#processingDiv').show();
		$('#tagDiv').hide();
		$('#close').hide();
		$('#respProcessing').css('display','none');
		$('#parser').text(response.id);
		if(response.taskSuccess) {
			$('#respMessage').text("Updated Successfully.");
			$('#respSuccess').css('display','');
			$('#respStatus').text('Success');
		} else {
            $('#respMessage').text("Updation Failed.");
        	$('#log_div').html('<a href="javascript:TagManager.editSubmitedForm();">Edit Form</a>&nbsp;&nbsp;&nbsp;<a href="javascript:Navbar.showServerLog();">View Log</a>');
        	$('#log_div').show();
        	$('#respStatus').text('Fail');
        	$('#respFail').css('display','');
		}
		$('#okpopup').prop('disabled', false);
		Navbar.refreshView();
	},
	
	updatedFileTag : function(index, id, value)
	{
		$('#fileTagFT' + index).val('');
		TagManager.isJarUpdated = true;
		var extension = value.substring(value.length-4, value.length);
		if(extension != ".jar" && extension != ".JAR")
		{
			jAlert("Only JAR files are required to be uploaded.","Incorrect Detail");
			$("#" + id).val("");
			$("#popup_container").css("z-index","99999999");
		}
	},
	
	showFileTagChooseFile : function(index)
	{
		if ($('#chooseFileTagFile'+ index).val() == 'Change')
		{
			dwr.util.byId('fileTag'+ index).style.display = '';
			dwr.util.byId('fileTagFT'+ index).style.display = 'none';
			$('#chooseFileTagFile'+ index).val('Keep Unchanged');
		}
		else
		{
			dwr.util.byId('fileTag'+ index).style.display = 'none';
			dwr.util.byId('fileTagFT'+ index).style.display = '';
			$('#chooseFileTagFile'+ index).val('Change');
		}
	},
	
	addLibClicked: function()
	{
    	if(TagManager.counter==10)
    	{
	        jAlert("Only 10 Files are allowed at once.","Limit Reached");
	        $("#popup_container").css("z-index","9999999");
	        return;
		}
		var temp = TagManager.rowCounter + 1;
		
		var tbl_data  = '<tr id="fileTagRow'+temp+'"><td style = "text-align: left; width: 10%;">Extra JAR(s)</td><td style = "text-align: left; width: 80%;">'+
		'<input type="text" id="fileTagText'+temp+'" name="fileTagText'+temp+'" class = "fileTagTextClass" value="" style="width: 60%; display: none;">'+
		'<input type="file" id="fileTag'+temp+'" name="fileTag'+temp+'" class = "fileTagClass" onchange="javascript:TagManager.updatedFileTag('+temp+', this.id, this.value);" style="width: 250px;">'+
		'<input type="text" id="fileTagFT'+temp+'" name="fileTagFT'+temp+'" class = "fileTagFTClass" readonly="readonly" value="" style="width: 75%; display: none;">'+
		'<a href="javascript:TagManager.removeLibClicked('+temp+');" style="color: white; float: right; padding-top: 7px;"><img alt="Remove File" src="images/minus_sign_brown.png" id="minusImage" style="height: 10px; width: 20px;"></a>'+
		'<a href="javascript:TagManager.addLibClicked();" style="float: right; padding-top: 7px;"><img alt="Add More File" src="images/plus_sign_brown.png" id="plusImage" style="height: 12px;"></a>'+
		'</td>'+
		'<td style = "text-align: left; width: 20%;">'+
			'<input type="button" class="buttonAdmin" id="chooseFileTagFile'+temp+'" value="Change" onclick="javascript:TagManager.showFileTagChooseFile('+temp+');" style="width: 100px; float: right; margin: 0px; display: none;"/>'+
		'</td></tr>';
		var $tr;
		var isAdded = true;
		var index = TagManager.counter;
		while (isAdded)
		{
			$tr = $('#fileTagRow'+index)
			if ($tr.length)
			{
				isAdded = false;
				$tr.after(tbl_data);
			}
			index ++;
		}
				
		TagManager.counter ++;
		TagManager.rowCounter ++;
	},
	
	removeLibClicked: function (id)
	{
		if(TagManager.counter==1)
		{
	          jAlert("Atleast 1 File should be there.","Invalid Action");
	          $("#popup_container").css("z-index","9999999");
	          return;
	    }   
		$("#fileTagRow"+id).remove();
		var lib = $('#fileTagFT' + id).val();
		if (lib != '')
			TagManager.isJarUpdated = true;
		TagManager.counter--;
	},
		
	activeToggle : function (val)
	{
		$("#labelActive").val(val);
	},
};