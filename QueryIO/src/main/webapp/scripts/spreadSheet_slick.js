SSS = {
		
		dataView : null,
		grid : null,
		data : [],
		columns : [],
		total : -1,
		selectedNameNode : '',
		selectedQueryId : '',
		
		ready : function()
		{
			$('#myGridContainer').width($('#service_ref').width() - 2);
			$('#myGrid').height($('#service_ref').height() - 2);

//			SSS.sendRequest();
			
			SSS.selectedNameNode = "NameNode";
	      	SSS.selectedQueryId = "New Query 3";
			
			RemoteManager.getSpreadSheetSlickResultsMetadata(SSS.selectedQueryId, SSS.selectedNameNode, SSS.fillData);
		},
		
		fillData : function(result)
		{
//			console.log("result: " , result);
			
			if (result == null)
			{
				// TODO add handling for empty grid.
				return;
			}
			
			var columnList = result.columnList;
			SSS.total = result.total;
			
			if (columnList == null)
			{
				// TODO add handling for empty grid.
				return;
			}
			
			if (columnList.length <= 0)
			{
				// TODO add handling for empty grid.
				return;
			}
			
			SSS.columns = [{
				id: "selector",
				name: "#",
				field: "num",
				width: 60,
				cannotTriggerInsert: true,
				resizable: false,
				selectable: false,
				behavior: "select",
				cssClass: "cell-selection",
				sortable: true
		    }];
			
			for (var i = 0; i < columnList.length; i++) {
				SSS.columns.push({
					id: i,
					name: columnList[i],
		            field: i,
		            width: 120,
		            sortable: true
				});
			}

//			for (var i = 0; i < 10000; i++) {
//				SSS.data[i] = {
//					id: "id_" + i,
//					num: i + 1,
//					0: "0 Task " + i,
//					1: "1 Task " + i,
//					2: "2 Task " + i,
//					3: "3 Task " + i,
//					4: "4 Task " + i
//   	        	};
//			}
			
			SSS.createGrid();
		},
		
//		sendRequest : function(request)
//		{
////        	console.log("SSS.selectedNameNode: " , SSS.selectedNameNode);
////        	console.log("Navbar.selectedQuery: " , Navbar.selectedQuery);
//        	
//			var nameNodeId = "NameNode";
//			var queryId = "New Query 2";
//			
//        	var requestURL = "spreadSheet.do?type=slick&namenode=" + nameNodeId + "&queryId=" + queryId + "";
//        	
//        	jQuery.ajax({
//    			url: requestURL,
//    			type: "POST",
//    			data : JSON.stringify(request),
//    			async: false,
//    			contentType: 'application/json; charset=utf-8',
//    			success: function(resultData) {
//    				SSS.fillData(resultData);
//    			},
//    			error : function(jqXHR, textStatus, errorThrown) {
//    				console.log("Error occured while collecting data- " + "Status: " + textStatus + " errorThrown: " + errorThrown, 'Error');
//    			},
//    			
//    			timeout: 120000,
//    		});
//		},
		
		createGrid : function()
		{
			var options = {
				editable: false,
				enableAddRow: false,
				enableCellNavigation: true,
				asyncEditorLoading: false,
				autoEdit: false,
				enableColumnReorder: false,
				forceFitColumns: false,
				topPanelHeight: 25,
				rowHeight: 50
			};
			
//			SSS.dataView = new Slick.Data.DataView({ inlineFilters: true });
			
			var loader = new Slick.Data.RemoteModel();
			SSS.grid = new Slick.Grid("#myGrid", loader.data, SSS.columns, options);

			SSS.grid.setSelectionModel(new Slick.RowSelectionModel());
			SSS.grid.registerPlugin(new Slick.AutoTooltips());

		    // set keyboard focus on the grid
			SSS.grid.getCanvasNode().focus();

			var loadingIndicator = null;
			
			SSS.grid.onViewportChanged.subscribe(function (e, args) {
			      var vp = SSS.grid.getViewport();
			      loader.ensureData(vp.top, vp.bottom);
			    });

			SSS.grid.onSort.subscribe(function (e, args) {
		      loader.setSort(args.sortCol.field, args.sortAsc ? 1 : -1);
		      var vp = SSS.grid.getViewport();
		      loader.ensureData(vp.top, vp.bottom);
		    });

		    loader.onDataLoading.subscribe(function () {
		      if (!loadingIndicator) {
		        loadingIndicator = $("<span class='loading-indicator'><label>Loading Data...</label></span>").appendTo(document.body);
		        var $g = $("#myGrid");

		        loadingIndicator
		            .css("position", "absolute")
		            .css("top", $g.position().top + $g.height() / 2 - loadingIndicator.height() / 2)
		            .css("left", $g.position().left + $g.width() / 2 - loadingIndicator.width() / 2);
		      }

		      loadingIndicator.show();
		    });

		    loader.onDataLoaded.subscribe(function (e, args) {
		      for (var i = args.from; i <= args.to; i++) {
		    	  SSS.grid.invalidateRow(i);
		      }

		      SSS.grid.updateRowCount();
		      SSS.grid.render();

		      loadingIndicator.fadeOut();
		    });

		    // load the first page
		    SSS.grid.onViewportChanged.notify();
			
			
			// for client side sorting.
//			var sortcol = String.fromCharCode("A".charCodeAt(0) + (0 / 26) | 0) + String.fromCharCode("A".charCodeAt(0) + (0 % 26));
//			var sortdir = 1;
//			// wire up model events to drive the grid
//			SSS.dataView.onRowCountChanged.subscribe(function (e, args) {
//				SSS.grid.updateRowCount();
//			    SSS.grid.render();
//			});
//
//			SSS.dataView.onRowsChanged.subscribe(function (e, args) {
//				SSS.grid.invalidateRows(args.rows);
//				SSS.grid.render();
//			});
//
//			SSS.grid.onSort.subscribe(function (e, args) {
//			    sortdir = args.sortAsc ? 1 : -1;
//			    sortcol = args.sortCol.field;
//
//			    if ($.browser.msie && $.browser.version <= 8) {
//			      // using temporary Object.prototype.toString override
//			      // more limited and does lexicographic sort only by default, but can be much faster
//
////			      var percentCompleteValueFn = function () {
////			        var val = this["percentComplete"];
////			        if (val < 10) {
////			          return "00" + val;
////			        } else if (val < 100) {
////			          return "0" + val;
////			        } else {
////			          return val;
////			        }
////			      };
////
////			      // use numeric sort of % and lexicographic for everything else
////			      dataView.fastSort((sortcol == "percentComplete") ? percentCompleteValueFn : sortcol, args.sortAsc);
//			    	
//			    	SSS.dataView.fastSort(sortcol, args.sortAsc);
//			    	
//			    } else {
//			      // using native sort with comparer
//			      // preferred method but can be very slow in IE with huge datasets
//			    	SSS.dataView.sort(comparer, args.sortAsc);
//			    }
//			  });
//			
//			function comparer(a, b)
//			{
//				var x = a[sortcol], y = b[sortcol];
//				return (x == y ? 0 : (x > y ? 1 : -1));
//			}
//			
//			// initialize the model after all the events have been hooked up
//			SSS.dataView.beginUpdate();
//			SSS.dataView.setItems(SSS.data);
//			SSS.dataView.endUpdate();
//			
//			// if you don't want the items that are not visible (due to being filtered out
//			// or being on a different page) to stay selected, pass 'false' to the second arg
//			SSS.dataView.syncGridSelection(SSS.grid, true);
//
//			$("#gridContainer").resizable();
			
			
			
//		    var copyManager = new Slick.CellCopyManager();
//		    grid.registerPlugin(copyManager);
//
//		    copyManager.onPasteCells.subscribe(function (e, args) {
//		      if (args.from.length !== 1 || args.to.length !== 1) {
//		        throw "This implementation only supports single range copy and paste operations";
//		      }
//
//		      var from = args.from[0];
//		      var to = args.to[0];
//		      var val;
//		      for (var i = 0; i <= from.toRow - from.fromRow; i++) {
//		        for (var j = 0; j <= from.toCell - from.fromCell; j++) {
//		          if (i <= to.toRow - to.fromRow && j <= to.toCell - to.fromCell) {
//		            val = data[from.fromRow + i][columns[from.fromCell + j].field];
//		            data[to.fromRow + i][columns[to.fromCell + j].field] = val;
//		            grid.invalidateRow(to.fromRow + i);
//		          }
//		        }
//		      }
//		      grid.render();
//		    });
//
//		    grid.onAddNewRow.subscribe(function (e, args) {
//		      var item = args.item;
//		      var column = args.column;
//		      grid.invalidateRow(data.length);
//		      data.push(item);
//		      grid.updateRowCount();
//		      grid.render();
//		    });
		},
		
		/***
		   * A proof-of-concept cell editor with Excel-like range selection and insertion.
		   */
		FormulaEditor : function(args)
		{
			var _self = this;
		    var _editor = new Slick.Editors.Text(args);
		    var _selector;
		    
		    $.extend(this, _editor);
		    
		    function init() {
		    	// register a plugin to select a range and append it to the textbox
		    	// since events are fired in reverse order (most recently added are executed first),
		    	// this will override other plugins like moverows or selection model and will
		    	// not require the grid to not be in the edit mode
		    	_selector = new Slick.CellRangeSelector();
		    	_selector.onCellRangeSelected.subscribe(_self.handleCellRangeSelected);
		    	args.grid.registerPlugin(_selector);
		    }
		    
		    this.destroy = function () {
			    _selector.onCellRangeSelected.unsubscribe(_self.handleCellRangeSelected);
			    grid.unregisterPlugin(_selector);
			    _editor.destroy();
		    };
		
		    this.handleCellRangeSelected = function (e, args) {
		    	_editor.setValue(
		    			_editor.getValue() +
		    			grid.getColumns()[args.range.fromCell].name +
		    			args.range.fromRow +
		    			":" +
		    			grid.getColumns()[args.range.toCell].name +
		    			args.range.toRow
		    	);
		    };
		
		    init();
		},	
};