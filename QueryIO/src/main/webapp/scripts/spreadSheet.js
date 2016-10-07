SPS ={
		selectedNameNode : '',
		currentSpreadSheet : null,
		currentSheet : null,
		isFirstTime : true,
		isQueryIdDropDown : false,
		threadTime : 100,
		nullResponseTracker : 0,
		currentExecutingQueryId : '',
		queriesAlreadyInCookie : [],
		isExecuteQuery : false,
		
		setProgressMsg: function(response){
			var isAdhoc = response.adhoc;
			if(isAdhoc)
			{
				$('#stepMessage2').text('Launching MapReduce Job for processing data on cluster.');
				$('#stepMessage3').text('Job execution is in progress');
				$('#stepMessage4').text('Generating spreadsheet from data.');
				
			}
			else
			{
				$('#stepMessage2').text('Query execution is in progress.');
				$('#stepMessage3').text('Processing data from result set.');
				$('#stepMessage4').text('Generating spreadsheet from data.');
				
			}
			var d = '';
			var queryId = SPS.currentExecutingQueryId;
			SPS.currentSpreadSheet.openNewSheet(d, queryId, SPS.isExecuteQuery);	
			SPS.getSpreadSheetQueryStaus();
			
		},
		
		getSpreadSheetQueryStaus : function()
		{
			
			
			if(SPS.currentExecutingQueryId != '')
			{
				var userName = Util.getLoggedInUserName();
				RemoteManager.getSpreadSheetQueryStatus(SPS.currentExecutingQueryId, SPS.selectedNameNode,userName, SPS.handleSpreadSheetQueryStatus);
			}
			else
			{
				SPS.returnSpreadSheet();
			}
			
		},
		
		showSpreadSheetProgressView : function(){
			
			$('#shpreadSheetStatus').html('');
			Util.addLightbox('progressViewer','resources/spreadSheetProgressViewer.html',null,null);
		
		},
		
		handleSpreadSheetQueryStatus : function(response){
			
			
			var isSendCallBack = true;
			var id = 1;
			if(response != null){
				
				var currentStep = null
				if(response.hasOwnProperty("CURRENTSTEP"))
					currentStep = response.CURRENTSTEP ;
				if(currentStep != null && currentStep != undefined){
					SPS.nullResponseTracker = 0;	
				
					
					var status = response.STATUS;
					
					if(currentStep == 'STEP1')
					{
						id = 1;
						if(status == 'SUCCESS'){
							$('#step2').show();
						}
					}
					else if(currentStep == 'STEP2'){
						id = 2;
						if(status == 'SUCCESS'){
							$('#step3').show();
						}
					}
					else if(currentStep == 'STEP3'){
						id = 3;
						if(status == 'SUCCESS'){
							$('#step4').show();
						}
					}
					else if(currentStep == 'STEP4'){
						id = 4;
						isSendCallBack = false;
					}
					
					for(var i = id ; i >0; i--){
						$('#image_processing'+i).css('display','none');
						if(status == 'FAILED' && i==id)
						{
							$('#startImg'+i).css('display','none');
							$('#stopImg'+i).css('display','');
							$('#image_success'+i).css('display','none');
							$('#image_fail'+i).css('display','none');
							$('#error-div').html(response.ERROR);
							$('#error-step').css('display','');
							isSendCallBack = false
							status = 'SUCCESS';
						}
						else
						{
							$('#step'+i+1).css('display','');
							$('#step'+i).css('display','');
							$('#startImg'+i).css('display','');
							$('#stopImg'+i).css('display','none');
							$('#image_success'+i).css('display','');
						}
					}
				}else{
					SPS.nullResponseTracker++;
					if(SPS.nullResponseTracker == 10)
					{
						SPS.nullResponseTracker = 0;
						isSendCallBack = false
						$('#image_processing'+id).css('display','none');
						$('#image_fail'+id).css('display','');
						$('#error-div').html("Unable to get status of query.");
						$('#error-step').css('display','');
					}
				}
				
			}
			else
			{
				SPS.nullResponseTracker++;
				if(SPS.nullResponseTracker == 5)
				{
					SPS.nullResponseTracker = 0;
					isSendCallBack = false
					$('#image_processing'+id).css('display','none');
					$('#image_fail'+id).css('display','');
					$('#error-div').html("Unable to get status of query.");
					$('#error-step').css('display','');
				}
			}
				
			if(isSendCallBack)
			{
				setTimeout(
						function() 
						{
							SPS.getSpreadSheetQueryStaus();
						}, SPS.threadTime);
			}
			else
			{
				
				if(id==4 && status =="SUCCESS"){
					
					SPS.nullResponseTracker = 0;
				}
					
				
				
				var userName = Util.getLoggedInUserName();
				SPS.currentExecutingQueryId =$('#bigQueryIds').val();
				RemoteManager.deleteSpreadSheetQueryStatus(SPS.currentExecutingQueryId, SPS.selectedNameNode,userName, SPS.handleDeleteSpreadSheetQueryStatus)
			}
			
				
			
			
		},
		handleDeleteSpreadSheetQueryStatus : function(){
			
		},
		ready : function(){
			$("#mainWrapper").height($("#service_ref").height() - 10);
			SPS.selectedNameNode = $('#queryIONameNodeId').val();
			if(!$('select#queryIONameNodeId option').length>0){
				$('#firstmsg').text("There is no NameSpace defined currently.Please setup a cluster for using Analytics features.");
				$('#firstmsg').css('padding-left','35%');
				//jAlert("There is no NameNode present in cluster.Please install a NameNode before using BigQuery features.","Error");
				return;
			}
			RemoteManager.getAllBigQueriesInfo(SPS.selectedNameNode, SPS.fillAllQueryIds);
			

		},
		
		fillAllQueryIds : function(object){
			var data = '<option value=""></option>';
			var flag=true;
			if(object!=null)
	   		{
				data="";
					for(var attr in object)
					{
						var query=object[attr]; 
			   			var queryId = query["id"];
			   			if(flag){
			   				Navbar.selectedQuery = queryId;
			   				data += '<option value="'+queryId+'" selected="selected">'+queryId+'</option>';
			   				flag=false;
			   				continue;
			   			}
			   			data += '<option value="'+queryId+'">'+queryId+'</option>';
					}
		  	}
			if(!flag){
				$('#printReport').removeAttr('disabled');
				$('#exportReport').removeAttr('disabled');
				$('#emailReport').removeAttr('disabled');
				$('#queryExecute').removeAttr('disabled');
			}
			$('#bigQueryIds').html(data);
			
			$('#bigQueryIds').val(SPS.currentExecutingQueryId);
			if (SPS.isFirstTime)
			{
				SPS.isFirstTime = false;
//				if (Navbar.selectedQuery != undefined)
//					SPS.sendRequest();
//				else
//					SPS.processResponse();
					SPS.loadLastViewedQuery();
			}
			else
			{
				$('#bigQueryIds').val(Util.getCookie("last-visited-query"));
				
				SPS.changeQueryId(Util.getCookie("last-visited-query"));
			}
			
		},
		setQueryIdInCookieForLastVisit : function(queryId){
			

			if(SPS.queriesAlreadyInCookie.indexOf(queryId) != -1 || !SPS.isExecuteQuery)
				return;
			try{
				
			
			var userId = $('#loggedInUserId').text();
			var obj =   JSON.parse(Util.getCookie("last-visit-query"+userId));
			if(obj != null && obj != undefined){
				
				var idInfObj = JSON.parse(Util.getCookie("last-visit-idInfoMap"+userId));
				
				var randomnumber=Math.floor(Math.random()*1000)
				var filePath = queryId.split(' ').join('_');
				while(obj.hasOwnProperty(randomnumber)){
					randomnumber=Math.floor(Math.random()*1000);
				}
				obj[randomnumber] = '/'+filePath.toLowerCase()+".html";
				idInfObj[randomnumber] = queryId;
				
				Util.setCookie("last-visit-query"+userId,JSON.stringify(obj), 1);
				Util.setCookie("last-visit-idInfoMap"+userId,JSON.stringify(idInfObj), 1);
				
			}
			}catch(e){
				
			}
			
		},
		loadLastViewedQuery : function()
		{
			var userId = $('#loggedInUserId').text();
			var obj =  Util.getCookie("last-visit-query"+userId);
			var queryIds = new Array();
			SPS.queriesAlreadyInCookie =  new Array();
			if(obj != null && obj != undefined)
			{
				var idInfObj = JSON.parse(Util.getCookie("last-visit-idInfoMap"+userId));
				SPS.isQueryIdDropDown = true;
				var qId ='';
				for(var executionId in idInfObj)
				{
					qId = idInfObj[executionId];
					queryIds.push(qId);
					SPS.queriesAlreadyInCookie.push(qId);
					SPS.currentSpreadSheet = $.sheet.instance[0];
					Navbar.selectedQuery = qId;
		    		var d = '';
		    		SPS.currentExecutingQueryId = qId;
		    	}
			}
			if(queryIds.length>0)
			{
				
				SPS.currentExecutingQueryId = queryIds[0]; 
				SPS.sendRequest("",queryIds);
				
			}
			else
			{
				SPS.processResponse();
			}
	    },
		
		changeQueryId : function(queryId)
		{
			SPS.currentSpreadSheet = $.sheet.instance[0];
			SPS.isQueryIdDropDown = true;
			Navbar.selectedQuery = queryId;
			var userName = Util.getLoggedInUserName();
			var flag = false;
			var openSheetSpans = $('span.ui-corner-bottom a.jSTab');
			for(var i = 0; i<openSheetSpans.length;i++){
				if(queryId == (openSheetSpans[i]).innerHTML){
					$.sheet.instance[0].setActiveSheet(i);
					flag = true;
					break;
				}
			}
			
			if(flag)
			{
				SPS.loadQueryInSheet($.sheet.instance[0],false);
			}
			else
			{
				
				SPS.currentExecutingQueryId = queryId;
	    		if ((queryId == '') || (queryId == null)) {
	    			SPS.closeNewSpreadSheetView();
	    			return false;
	    		} else {
	    			
	    			var d = '';
	    			SPS.isExecuteQuery = false;
	    			SPS.currentExecutingQueryId = queryId;
	    			SPS.isQueryIdDropDown = true;
	    			SPS.newSpreadSheet(true);
	    			
	    		}
			}
		},
	
		loadQueryInSheet : function(sheet,isExecute)
		{
			SPS.isExecuteQuery = isExecute;
			SPS.isQueryIdDropDown = true;
			Navbar.selectedQuery = $('#bigQueryIds').val();
			if (Navbar.selectedQuery == undefined || Navbar.selectedQuery ==null){
				jAlert('No Query selected.Please select a query for execution.','Invalid query id');
				return;
			}
			SPS.currentSpreadSheet = sheet;
			SPS.newSpreadSheet(false);
		},
		
		sendRequest : function(request,queryId)
		{
			var requestURL = "spreadSheet.do?type=result&namenode=" + SPS.selectedNameNode + "&isContainsMultipleQueries=true&isRunQuery=false&queryId=" +JSON.stringify(queryId)+ "&username="+Util.getLoggedInUserName()+"";
        	jQuery.ajax({
    			url: requestURL,
    			type: "POST",
    			data : JSON.stringify(request),
    			async: false,
    			contentType: 'application/json; charset=utf-8',
    			success: function(resultData) {
    				SPS.processResponse(resultData);
    				
    			},
    			error : function(jqXHR, textStatus, errorThrown) {
    			},
    			
    			timeout: 120000,
    		});
		},
		
		loadSheet : function (obj, url, resultData, isJson)
		{
			var urls = {tables: url || $(obj).attr('href'), menuLeft: 'spreadsheet/menu.left.html', menuRight: 'spreadsheet/menu.right.html'}, results = {}, loadCount = 3, loaded = 0;

            for(var type in urls) {
                var tempUrl = urls[type] + (type == 'tables' ? ' #sheetParent table' : '');
                $('<div />')
                        .data('type', type)
                        .load(tempUrl, function() {
                    results[$(this).data('type')] = $(this).html();
                    loaded++;
                    if (loadCount == loaded) {
                    	
                    	var selectedTables = null;
                    	
                    	if (resultData != undefined)
                    	{
                    		if (isJson)
                        		selectedTables = $.sheet.dts.toTables.json(jQuery.parseJSON(resultData));
                        	else
                        		selectedTables = resultData;
                    	}
                    	else
                			selectedTables = results.tables;

         	        	
                        $('#sheet')
                            .html(selectedTables)
                            .sheet({
                                sheetFullScreen: function(e, jS) {
                                },
                                
                                sheetAdd: function(e, jS, i) {
                                	
                                	$.each(jS.obj.sheet(), function() {
                                     	
                                     	SPS.makeSortable(this);
                                     	
                                      });
                                },
                                
                                sheetOpen: function(e, jS, i) {
                                	
                                	 $.each(jS.obj.sheet(), function() {
                                     	
                                     	SPS.makeSortable(this);
                                     	
                                      });
                                },
                                
                                sheetAllOpened: function(e, jS) {
                                    
                                    $.each(jS.obj.sheets(), function() {
                                    	
                                    	SPS.makeSortable(this);
                                    	
                                     });
                                    
                                    SPS.currentSpreadSheet = jS;
                                    
                                    
                                    RemoteManager.getAllBigQueriesInfo(SPS.selectedNameNode, SPS.fillAllQueryIds);

                                },
//                                title: 'Query Spreadsheet',
                                title: null,
                                menuLeft: function(jS) {
                                    var menu = $(results.menuLeft.replace(/sheetInstance/g, "$.sheet.instance[" + jS.I + "]")),
                                        copy = menu.find('.jSCopy').mouseover(function() { $(this).one('mouseout', function() {return false;}); }),
                                        cut = menu.find('.jSCut').mouseover(function() { $(this).one('mouseout', function() {return false;} ); }),
                                        paste = menu.find('.jSPaste').click(function() {
                                            alert('Press Ctrl + V to paste over highlighted cells');
                                        });

                                    cut.mousedown(function() {
                                        jS.tdsToTsv(null, true);
                                    });

                                    return menu;
                                },
                                menuRight: function (jS){
                                    //we want to be able to edit the html for the menu to make them multi-instance
                                    var menu = results.menuRight.replace(/sheetInstance/g, "$.sheet.instance[" + jS.I + "]");
                                    menu = $(menu);

                                    //The following is just so you get an idea of how to style cells
                                    menu.find('.colorPickerCell').colorPicker().change(function(){
                                        jS.cellChangeStyle('background-color', $(this).val());
                                    });

                                    menu.find('.colorPickerFont').colorPicker().change(function(){
                                        jS.cellChangeStyle('color', $(this).val());
                                    });

                                    menu.find('.colorPickers').children().eq(1).css('background-image', "url('spreadsheet/images/palette.png')");
                                    menu.find('.colorPickers').children().eq(3).css('background-image', "url('spreadsheet/images/palette_bg.png')");

                                    return menu;
                                }
                            });
                    }
                });
            }
            
            
        },
        
        processResponse : function(resultData)
        {
            /* loadSheet({}, 'spreadsheet/examples/charts.html'); */
        	
        	
    
        	SPS.loadSheet({}, 'spreadsheet/examples/empty.html', resultData, true);
            /* loadSheet({}, 'spreadsheet/examples/dts.test.html'); */
        },
        
        fillQueryIds : function ()
        {
        	SPS.isQueryIdDropDown = false;
        	RemoteManager.getAllBigQueriesInfo(SPS.selectedNameNode, SPS.fillAllQueryIdsForNewSpreadSheet);
        },
        
        fillAllQueryIdsForNewSpreadSheet : function(object)
    	{
    		var data = '<option value="blank">blank sheet</option>';
    		var flag=true;
    		if(object!=null)
    			{
    			data="";
    				for(var attr in object)
    				{
    					var query=object[attr]; 
    		   			var queryId = query["id"];
    		   			data += '<option value="'+queryId+'">'+queryId+'</option>';
    				}
    	  	}
    		$('#bigQueryIdsForNewSpreadSheet').html(data);
    		
    	},
    	
    	closeNewSpreadSheetView : function()
    	{
//    		SPS.currentSpreadSheet = null;
    		Util.removeLightbox("new_spreadsheet");
    	},
    	
    	newSpreadSheet : function(notOpenNewTab)
    	{
    		
    		var queryId = '';
    		

    		if (SPS.isQueryIdDropDown){
    			queryId  = $('#bigQueryIds').val();
    		}
    		else
    			queryId = $('#bigQueryIdsForNewSpreadSheet').val();
    		if(notOpenNewTab)
    			SPS.isQueryIdDropDown = false;
    		
    		Util.removeLightbox("new_spreadsheet");	
    		
			SPS.currentExecutingQueryId = queryId;
    		
			if ((queryId == '') || (queryId == null)) {
    			SPS.closeNewSpreadSheetView();
    			return false;
    		} else {
    			
    			var d = '';
    			SPS.currentExecutingQueryId = queryId;
				if(SPS.isExecuteQuery){
						SPS.showSpreadSheetProgressView();
				}else{
					SPS.currentSpreadSheet.openNewSheet(d, queryId, SPS.isExecuteQuery);
				}
    			
    		}
    	},
    	
    	closeSaveSpreadSheetView : function()
    	{

    		$.sheet.instance[0].fnAddBindEvent();
    		SPS.currentSheet = null;
    		Util.removeLightbox("save_spreadsheet");
    	},
    	
    	saveSpreadSheet : function()
    	{
    		var sheetId =$('#sheetId').val();
    		$('#spsId').val(sheetId);
    		$('#processing').show();
    		$('#speadSheetIdDiv').hide();
    		
			if (sheetId) {

				// Save Workbook
				// Working, stored in JSON form.
				var ad = $.sheet.dts.fromTables.json(SPS.currentSpreadSheet);
				
				SPS.currentSpreadSheet.saveSheet(JSON.stringify(ad), sheetId);
				
				// Save sheet
//	    		var id = SPS.currentSpreadSheet.id.sheet + SPS.currentSpreadSheet.i;
//				var elem = $('<div>').append($("#" + id).clone());
//				
//				elem.find('.' + SPS.currentSpreadSheet.cl.barTopParent).remove();
//				elem.find('.' + SPS.currentSpreadSheet.cl.barLeft).remove();
//				
//				var d = elem.html();
//				elem.remove();
//				
//				SPS.currentSpreadSheet.saveSheet(d, sheetId);
				$.sheet.instance[0].fnAddBindEvent();
				return true;
			} else {
				SPS.closeSaveSpreadSheetView();
				return false;
			} 	
    	},
    	
    	fillSheedIds : function()
    	{
    		RemoteManager.getSpreadSheets(SPS.selectedNameNode,SPS.fillAllSheetId);
    	},
    	
    
    	fillAllSheetId  : function(object)
    	{
    		var data = '';
    		var flag=true;
    		if (object!=null)
    		{
				data="";
				for(var i in object)
				{
		   			data += '<option value="'+object[i]+'">'+object[i]+'</option>';
				}
    	  	}
    		$('#sheetIds').html(data);
    	},
    
    	closeOpenSpreadSheetView : function()
    	{
//    		SPS.currentSpreadSheet = null;
    		Util.removeLightbox("open_spreadsheet");
    	},
    	
    	openSpreadSheet : function()
    	{
    		var sheetId = $('#sheetIds').val();
    		
    		if ((sheetId == '') || (sheetId == null)) {
    			SPS.closeOpenSpreadSheetView();
    			return false;
    		} else {
    			var d = '';
    			SPS.currentSpreadSheet.openSavedSheet(d, sheetId);
    			return true;
    		}    		 	
    	},
    	printSpreadSheet : function()
    	{
//    		var o = SPS.currentSheet || SPS.currentSpreadSheet.obj.sheets();
//    		var v = jQuery.sheet.instance[SPS.currentSpreadSheet.I];

    		var id = SPS.currentSpreadSheet.id.sheet + SPS.currentSpreadSheet.i;
			
			var elem = $('<div>').append($("#" + id).clone());
			
			elem.find('td.' + SPS.currentSpreadSheet.cl.uiCellActive)
				.removeClass(SPS.currentSpreadSheet.cl.uiCellActive);
			elem.find('td.' + SPS.currentSpreadSheet.cl.uiCellHighlighted)
				.removeClass(SPS.currentSpreadSheet.cl.uiCellHighlighted);
			elem.find('.' + SPS.currentSpreadSheet.cl.barTopParent).remove();
			elem.find('.' + SPS.currentSpreadSheet.cl.barLeft).remove();
			elem.children('colgroup').children('col:first').remove();
			
			var d = elem.html();
			elem.remove();

    		Util.print(d);
    		
    	},
    	
    	sortTable : function(table, col, reverse) {
    	    var tb = table.tBodies[0], // use `<tbody>` to ignore `<thead>` and `<tfoot>` rows
    	        tr = Array.prototype.slice.call(tb.rows, 1), // put rows into array , from 1 since we have to ignore first row.
    	        i;
    	    reverse = -((+reverse) || -1);
    	    tr = tr.sort(function (a, b) { // sort rows
    	        return reverse // `-1 *` if want opposite order
    	            * (a.cells[col].textContent.trim() // using `.textContent.trim()` for test
    	                .localeCompare(b.cells[col].textContent.trim())
    	               );
    	    });
    	    for(i = 0; i < tr.length; ++i) tb.appendChild(tr[i]); // append each row in order
    	},

    	makeSortable : function(table) {
    	    var th = table.tBodies[0], i;
    	    th && (th = th.rows[0]) && (th = th.cells);
    	    if (th) i = th.length;
    	    else return; // if no `<thead>` then do nothing, using first <tbody> element.
    	    while (--i >= 0) (function (i) {
    	        var dir = 1;
    	        th[i].addEventListener('click', function () {SPS.sortTable(table, i, (dir = 1 - dir))});
    	    }(i));
    	}
};
