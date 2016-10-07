BQT = {
		selectedTable : '',
		selectedTableList : [],
		popupId : '',
		tableData : [],
		tagNames : [],
		firstTime : true,
		
		ready : function()
		{
			var height = ($('#db_detail').height() - 150);
			
			$('#bigQueryTablesDiv').height($('#rightPanelDCST').height() - 29);
//			$('#loadPageBQT').height($('#rightPanelDCST').height() - 50);
			$('#bigQueryContainer').height($('#rightPanelDCST').height() - 75);
			
			$('#bigQuery_table_div').height($('#bigQueryContainer').height());
		
			jQuery(window).bind('resize', function() 
			{
				$("#bigQueryTable_list").setGridWidth(($("#bigQueryContainer").width() - 2), true);
				$("#bigQueryTable_list").setGridHeight(($("#bigQueryContainer").height() - 18), true);
			}).trigger('resize');
			
			document.getElementById('selectQueryOption').disabled = true;
//			if (DB_Config.selectedNameNode != '')
				RemoteManager.getAllTagTableListForDB(DB_Config.selectedDBId, BQT.populateTableList);
		},
		
		populateTableList : function(list)
		{
			BQT.allTableNames = list;
			if(list == null || list == undefined || list.length == 0)
			{
				$("#tableCount").text(0);
				$("#bigQueryTable_list").html('<tr><td style="text-align:center;"><span>Table info not available. </span></td></tr>');
				document.getElementById('selectQueryOption').disabled = true;
				$("#refreshViewButton").removeAttr('disabled');
				return;
			}
			
//			var colList = [];
//			colList.push({ "sTitle":'<center><input type="checkbox" value="tag" id="selectAllTags" onclick="javascript:BQT.selectAllTagsRow(this.id)"></center>', "sWidth":"5%" });
//			colList.push({ "sTitle":'<center>Table Name</center>'});
//			
//			var tableRow = [];
//			for(var i=0; i<list.length; i++)
//			{
//				var row = list[i];
//
//				var rowData = new Array();
//				rowData.push('<input type="checkbox" value="' + row + '" onClick="javascript:BQT.clickBox(this.id)" id="' + row + '" >');
//				rowData.push(row);
//
//				BQT.tagNames.push(row);
//				tableRow.push(rowData);
//			}
			
			$("#tableCount").text(list.length);
			
			$('#bigQueryTable_list').remove();
			$('#bigQueryContainer').html('<table id="bigQueryTable_list"></table>');
			jQuery("#bigQueryTable_list").jqGrid({
				datatype : "local",
				colNames : ['<input type=checkbox onclick="BQT.selectAllBigTables();" id=selectAllSchedulesGrid style="margin-top: 5px; margin-left: 8px;"/>','ID' ],
				colModel : [ 
				{
					sortable : false,
					name : 'Check',
					index : 'Check',
					width : (($("#bigQueryContainer").width() / 4) - 5),
					formatter: BQT.createCheckBox,
					align : 'center'
				},
				{
					sortable : true,
					name : 'ID',
					index : 'ID',
					width : (((3*$("#bigQueryContainer").width()) / 4) - 7),
					sorttype : "text"
				}],
				height : ($("#bigQueryContainer").height() - 25),
				width : ($("#bigQueryContainer").width() - 2),
				shrinkToFit : false,
				rowNum : list.length,
				pager : "",
				altRows : true,
				viewrecords : true,
				sortable : true,
				pagination : false,
				caption : "",
				
				onSelectRow : function(id)
				{
					$("#refreshViewButton").removeAttr('disabled');
					var allRowsOnCurrentPage = $('#bigQueryTable_list').jqGrid('getDataIDs');
    		    	for(var i=0; i<allRowsOnCurrentPage.length; i++)
    		    	{
    		    		if(allRowsOnCurrentPage[i] == id)
    		    			$('#' + allRowsOnCurrentPage[i]).css('background-color','#ECECEC');
    		    		else
    		    			$('#' + allRowsOnCurrentPage[i]).css('background-color','white');
    		    	}
					var rowData = jQuery("#bigQueryTable_list").jqGrid('getRowData', id);
					
					
					BQT.selectedTable = rowData.ID;
					
					if(BQT.firstTime)
					{
						BQT.firstTime = false;
						BQT.viewSchema();
					}
					else
						BQT.showSchema();
                	
				},
				 onSortCol: function (index, idxcol, sortorder) {
	    		        if (this.p.lastsort >= 0 && this.p.lastsort !== idxcol
	    		                && this.p.colModel[this.p.lastsort].sortable !== false) {
	    		            $(this.grid.headers[this.p.lastsort].el)
	    		                .find(">div.ui-jqgrid-sortable>span.s-ico").show();
	    		            $(this.grid.headers[this.p.lastsort].el).removeClass('ui-state-highlight');
	    		        }
	    		        $(this.grid.headers[idxcol].el).addClass('ui-state-highlight');
	    		    },
				
				loadComplete : function()
				{
					$("#jqgh_Check").removeClass("ui-jqgrid-sortable");
				}
			});
			jQuery("#bigQueryTable_list").jqGrid('navGrid', '', {
				add : false,
				edit : false,
				del : false
			});
			
			var aaData = new Array();
			var selectedIndex = -1;
			
			if (list != null) {
				for ( var i = 0; i < list.length; i++)
				{
					if (Navbar.hiveViewSelectedTable == list[i])
						selectedIndex = i;
					aaData[i] = ({							
						All : "<input type = 'checkbox'>",
						ID : list[i]
					});
					jQuery("#bigQueryTable_list").jqGrid('addRowData', "tableEntry"+(i + 1),
							aaData[i]);
					
				}
			}
			
			if (list == null || list == undefined || list.length == 0)
			{
				document.getElementById('selectAllSchedulesGrid').disabled = true;
			}
			
			if (aaData.length == 0)
			{
				$("#refreshViewButton").removeAttr('disabled');
			}
			
			var rowId = jQuery("#bigQueryTable_list").jqGrid('getDataIDs');
	 		
//			console.log("hiveViewSelectedTable: " , Navbar.hiveViewSelectedTable);
//			console.log("selectedIndex: " , selectedIndex);
			
			if (selectedIndex == -1)
				jQuery("#bigQueryTable_list").jqGrid('setSelection',rowId[0],true);
			else
			{
				Navbar.hiveViewSelectedTable = undefined;
				jQuery("#bigQueryTable_list").jqGrid('setSelection',rowId[selectedIndex],true);
			}
			
			$(jQuery("#bigQueryTable_list")[0].grid.headers[0].el).addClass('ui-state-highlight');
			
//			$('#bigQueryTable_list').dataTable({		       
//				"bPaginate": false,
//				"bLengthChange": true,
//				"bFilter": false,
//				"bSort": true,
//				"bInfo": false,
//				"bAutoWidth": false,
//				"aaData": tableRow,
//				"aoColumns": colList
//		    });
			
//			if(list == null || list == undefined || list.length == 0)
//				document.getElementById('selectAllTags').disabled = true;
//			else
//				document.getElementById('selectAllTags').disabled = false;
			
			BQT.selectedTableList.splice(0, BQT.selectedTableList.length);
		},
		
		createCheckBox : function(cellvalue, options, rowObject) {
			return '<input type="checkbox"' + 
		      'onclick="javascript:BQT.clickBox(this.id);" id="' + rowObject.ID + '" style="margin-top: 5px;""/>';

		  },
		  
		selectAllBigTables : function()
		{
			if(document.getElementById("selectAllSchedulesGrid").checked)
				$("#bigQueryTable_list input[type = checkbox]").attr('checked', 'checked');
			else
				$("#bigQueryTable_list input[type = checkbox]").removeAttr('checked');
			
			var flag = document.getElementById("selectAllSchedulesGrid").checked;
			
			BQT.selectedTableList.splice(0, BQT.selectedTableList.length);
			for (var i=0; i<BQT.allTableNames.length; i++)
			{
				if (flag)
					BQT.selectedTableList.push(BQT.allTableNames[i]);
			}
			BQT.toggleButton();
			
		},
		
		selectTask : function()
		{
			if(BQT.selectedTable == null || BQT.selectedTable == undefined || BQT.selectedTable == '')
			{
				$("#selectQueryOption").prop('selectedIndex',0);
				jAlert('No Table selected. Select a table to perform operation.');
				return;
			}
			var option = $("#selectQueryOption").val();
			if(option == "Delete")
			{
				BQT.deleteTable();
				$("#selectQueryOption").prop('selectedIndex',0);
			}
			else if(option == "Clear")
			{
				BQT.clearTable();
				$("#selectQueryOption").prop('selectedIndex',0);
			}
		},
		
		deleteTable : function()
		{
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No ';
			jConfirm('Are you sure you want to delete all the selected tables ?','Delete Tables',function(val){
				if (val == true)
				{
					BQT.popupId = "_delete";
					Util.addLightbox("bqt_Box","pages/popup.jsp");
				}
				else
				{
					return;
				}
				jQuery.alerts.okButton = ' Ok ';
				jQuery.alerts.cancelButton  = ' Cancel ';
			});
		},
		
		clearTable : function()
		{
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No ';
			jConfirm('Are you sure you want to clear this table ?','Clear Table',function(val){
				if (val == true)
				{
					BQT.popupId = "_clear";
					Util.addLightbox("bqt_Box","pages/popup.jsp");
				}
				else
				{
					return;
				}
				jQuery.alerts.okButton = ' Ok ';
				jQuery.alerts.cancelButton  = ' Cancel ';
			});
		},
		
		viewSchema : function()
		{
//			Util.addLightbox("bqt_Box","resources/bigQueryTableSchema.html");
			$("#loadPageBQT").load("resources/bigQueryTableSchema.html");
		},
		
		popupResponse : function(dwrResponse)
		{
			var id = BQT.popupId;
			var status;
			var imgId;
			
			if (dwrResponse.taskSuccess)
			{
				status = 'Success';
				imgId = "popup.image.success" + id;
			}
			else
			{
				status = 'Failure';
				imgId = "popup.image.fail" + id;
				var log = '<a href="javascript:Navbar.showServerLog();">View Log</a>';
				document.getElementById('log_div'+ id).innerHTML=log;
				document.getElementById('log_div'+ id).style.display="block";

			}
			
			dwr.util.byId('popup.image.processing' + id).style.display = 'none';
			dwr.util.byId(imgId).style.display = '';
			dwr.util.setValue('popup.message' + id, dwrResponse.responseMessage);
			dwr.util.setValue('popup.status' + id, status);
			document.getElementById('ok.popup').disabled = false;
			
//			Navbar.refreshView();
			$("#bigQueryTable_list").setGridWidth(($("#bigQueryContainer").width() - 2), true);
			$("#bigQueryTable_list").setGridHeight(($("#bigQueryContainer").height() - 18), true);
		},
		
		closeBox : function()
		{
			BQT.popupId = '';
			BQT.selectedTable = '';
			BQT.selectedTableList = [];
			Util.removeLightbox('bqt_Box');
		},
		
		showSchema : function()
		{
			$("#selectedTable").val(BQT.selectedTable);
			RemoteManager.viewSchemaBigQueryTable(DB_Config.selectedDBId, BQT.selectedTable, BQT.fillTable);
		},
		
		fillTable : function(summaryTable)
		{
			var oTable = $('#schemaTable').dataTable();
			oTable.fnClearTable();
			
			if(summaryTable == null || summaryTable == undefined)
			{
				$("#schemaTable").html('<tr><td style="text-align:center;"><span>Schema details not available. </span></td></tr>');
				return;
			}
			var tableRow = summaryTable.rows;
			oTable.fnAddData(tableRow);
		},
		
		plotSchema : function(summaryTable)
		{
			$("#selectedTable").val(BQT.selectedTable);
			if(summaryTable == null || summaryTable == undefined)
			{
				$("#schemaTable").html('<tr><td style="text-align:center;"><span>Schema details not available. </span></td></tr>');
//				$('#bigQueryTablesDiv').height($('#loadPageBQT').height() + 45);
//				$('#bigQuery_table_div').height($('#loadPageBQT').height() + 20);
				return;
			}
			var colList=[];
			for (var i=0; i<summaryTable.colNames.length; i++)
			{
				colList.push({ "sTitle": summaryTable.colNames[i]});
			}
			
			var tableRow = summaryTable.rows;
			
			$('#schemaTable').dataTable({
				"bPaginate": false,
				"bLengthChange": false,
				"bFilter": false,
				"bSort": false,
				"bDestroy": true,
				"bInfo": false,
				"bAutoWidth": false,
				"sScrollY": "235px",
				"sScrollX": "100%",
			    "bScrollCollapse": true, //make it false if div size is to be made constant
				"aaData": tableRow,
				"aoColumns": colList
		    });
			
//			$('#bigQueryTablesDiv').height($('#loadPageBQT').height() + (45+33));
//			$('#bigQuery_table_div').height($('#loadPageBQT').height() + (20+40));
//			if (($('#bigQueryContainer').height() + 35) > $('#bigQuery_table_div').height())
//				$('#bigQueryContainer').height($('#bigQuery_table_div').height() - 40);
//			if($('#loadPageBQT').height() > $('#bigQueryTablesDiv').height())
//			{
//				$('#rightPanelDCST').height($('#loadPageBQT').height()+58);
//				$('#bigQueryTablesDiv').height($('#loadPageBQT').height()+30);
//			}
//			else
//			{
//				$('#rightPanelDCST').height($('#bigQueryContainer').height()+100);
//				$('#bigQueryTablesDiv').height($('#bigQueryContainer').height()+72);
//			}
		},
		
		toggleButton: function()
		{
			if(BQT.selectedTableList.length == 0)
				document.getElementById('selectQueryOption').disabled = true;
			else
				document.getElementById('selectQueryOption').disabled = false;
		},
		
		clickBox : function(id)
		{
			var i = -1;
			for(i = 0; i < BQT.allTableNames.length; i++)
			{
				if(!document.getElementById(BQT.allTableNames[i]).checked)
				{
					$("#selectAllSchedulesGrid").removeAttr('checked');
					break;
				}
			}
			if(i == BQT.allTableNames.length)
			{
				$("#selectAllSchedulesGrid").attr('checked', 'checked');
			}
			var flag = document.getElementById(id).checked;
			if (flag == true)
			{
				BQT.selectedTableList.push(id.toString());
				BQT.selectedTable = id.toString();
			}
			else
			{
				var index = jQuery.inArray(id.toString(), BQT.selectedTableList);
				if (index != -1)
				{
					BQT.selectedTableList.splice(index, 1);
				}
			}
			BQT.toggleButton();
		}
};