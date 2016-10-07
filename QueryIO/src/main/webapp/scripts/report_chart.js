RC={

		chartType : '',
		issetTimeOut:false,

		selectedChartYArray : {},
		selectedChartYObject : {},
		ySeriesHistory:{},
		
//		selectedLineChartYSeriesArray:[],
//		selectedLineChartYSeriesOject:{},
		
		chartOperation :'add',
		chartKey : '',
		chartPreferenceType : 'global',
		sortType : '',
		sortColumnSelected : '',
		
		ready : function(){
//			RC.selectedLineChartYSeriesArray=[];
//			RC.selectedLineChartYSeriesOject={};

			
			RC.selectedChartYArray= {
				lineseries : new Array(),
				bubblevalue : [],
				bubblesize : [],
				stockhigh : [],
				stocklow : [],
				stockopen : [],
				stockclose : [],
				differencepositive : [],
				differencenegetive : [],
				ganttlabel : [],
				ganttstart : [],
				ganttend: [],				
			};
			
			RC.selectedChartYObject= new Object();  
			
			RC.selectedChartYObject["lineseries"] = new Object();
			RC.selectedChartYObject["bubblevalue"] = new Object();
			RC.selectedChartYObject["bubblesize"] = new Object();
			RC.selectedChartYObject["stockhigh"] = new Object();
			RC.selectedChartYObject["stocklow"] = new Object();
			RC.selectedChartYObject["stockopen"] = new Object();			
			RC.selectedChartYObject["stockclose"] = new Object();
			RC.selectedChartYObject["differencepositive"] = new Object();
			RC.selectedChartYObject["differencenegetive"] = new Object();
			RC.selectedChartYObject["ganttlabel"] = new Object();
			RC.selectedChartYObject["ganttstart"] = new Object();
			RC.selectedChartYObject["ganttend"] = new Object();
			
			RC.ySeriesHistory= new Object();  
			
			RC.ySeriesHistory["lineseries"] = new Object();
			RC.ySeriesHistory["bubblevalue"] = new Object();
			RC.ySeriesHistory["bubblesize"] = new Object();
			RC.ySeriesHistory["stockhigh"] = new Object();
			RC.ySeriesHistory["stocklow"] = new Object();
			RC.ySeriesHistory["stockopen"] = new Object();			
			RC.ySeriesHistory["stockclose"] = new Object();
			RC.ySeriesHistory["differencepositive"] = new Object();
			RC.ySeriesHistory["differencenegetive"] = new Object();
			RC.ySeriesHistory["ganttlabel"] = new Object();
			RC.ySeriesHistory["ganttstart"] = new Object();
			RC.ySeriesHistory["ganttend"] = new Object();
			
			
			RC.chartType='';
			RC.sortType='None';
			RC.sortColumnSelected='';
			
			$('#chart_section').val(chartPosition);
			$('#chart_position').val(chartPosition);
			
//			$('#line_y_seriesColFilters').html(RC.getYSeriesHtmlData('line','series',true, true, true, true));
			
			RC.setXSeriesTable('line_x_series');
			RC.setXSeriesTable('y_series_grouping');
			
			 $('#y_series_grouping').append($('<option>', { 
			        value: "none",
			        text : "None" 
			    }));
			 $('#y_series_grouping option:contains("None")').prop('selected', true);
			RC.setXSeriesTable('bubble_x_series');
			RC.setXSeriesTable('bubble_y_series_grouping');
			$('#bubble_y_series_grouping').append($('<option>', { 
		        value: "none",
		        text : "None" 
		    }));
		 $('#bubble_y_series_grouping option:contains("None")').prop('selected', true);
			RC.setXSeriesTable('gantt_x_series');
			RC.setXSeriesTable('difference_x_series')
			RC.setXSeriesTable('difference_y_series_grouping');
			$('#difference_y_series_grouping').append($('<option>', { 
		        value: "none",
		        text : "None" 
		    }));
		 $('#difference_y_series_grouping option:contains("None")').prop('selected', true);
			RC.setXSeriesTable('stock_x_series');
			RC.setXSeriesTable('stock_y_series_grouping');
			$('#stock_y_series_grouping').append($('<option>', { 
		        value: "none",
		        text : "None" 
		    }));
		 $('#stock_y_series_grouping option:contains("None")').prop('selected', true);
			
			RC.setXSeriesTable('line_x_series_sort_column');
			RC.setXSeriesTable('bubble_x_series_sort_column');
			RC.setXSeriesTable('gantt_x_series_sort_column');
			RC.setXSeriesTable('difference_x_series_sort_column');
			RC.setXSeriesTable('stock_x_series_sort_column');
			
//			RC.setXSeriesTable('pie_x_series');
//			RC.setXSeriesTable('pie_y_series');
		},
		
		
		setXSeriesTable : function(id){
			var list='';
			
			if(DA.searchColumn[0]=='*'&&DA.searchColumn.length==1){
				list = DA.colList;
			} else {
				list = DA.searchColumn;
			}
			
			
			var data='';
			
			for(var i=0;i<list.length;i++){
				 data+='<option value="'+list[i]+'">'+list[i]+'</option>'
			}
			
			$('#'+id).html(data);		
		},
		
		getAggregateFunctionDropDown : function(idprefix, colName, onChageFunc, isInt, isText, isDate) {

			if (DA.colList.indexOf(colName) == -1) {
				return "";
			}
			var dataType = DA.colMap[colName];
			var data = '<select id="'
					+ idprefix
					+ colName
					+ '" disabled="disabled" style="width:100%;z-index:99999;"  onchange="javascript:'
					+ onChageFunc + ';">';
			if( (isText && dataType.toUpperCase()=='STRING') 
					|| (isDate && dataType.toUpperCase()=='TIMESTAMP' ) 
					|| (isDate && dataType.toUpperCase()=='BLOB' ) 
					||  (isInt && (dataType.toUpperCase() == 'INTEGER' || dataType.toUpperCase() == 'LONG' 
						|| dataType.toUpperCase() == 'DECIMAL' || dataType.toUpperCase() == 'SHORT' 
						|| dataType.toUpperCase() == 'DOUBLE' || dataType.toUpperCase() == 'FLOAT' )) ) 
			{
				data += '<option value=""></option>';
			}
			data += '<option value="Count">COUNT</option>';
			data += '<option value="DistinctCount">DISTINCT COUNT</option>';
			if (dataType.toUpperCase() == 'INTEGER' || dataType.toUpperCase() == 'LONG' || dataType.toUpperCase() == 'DECIMAL' || dataType.toUpperCase() == 'DOUBLE' || dataType.toUpperCase() == 'FLOAT') 
			{
				data += '<option value="Sum">SUM</option>';
				data += '<option value="Min">MIN</option>';
				data += '<option value="Max">MAX</option>';
				data += '<option value="Average">AVG</option>';
			}
			data += '</select>';
			return data;

		},
		
		getYSeriesHtmlData : function(prefix,suffix,isChecked, isInt, isText, isDate){
			   
			var list='';
	   		if(DA.searchColumn[0]=='*'&&DA.searchColumn.length==1){
	   			 list = DA.colList;	
	   		}else{
	   			list=DA.searchColumn;	
	   		}
			var groupHeader='<span class="divcloser"><a href="javascript:DA.closeSelectionDiv();"><img src="images/light-box-close.png" class="closerImage"></a></span><table id="'+prefix+suffix+'_chart_name"><tbody>';
	   		groupHeader+='<tr><td nowrap="nowrap">Select Column</td></tr>';//<td nowrap="nowrap">Aggregate Function</td></tr>';
	   		for(var i=0;i<list.length;i++)
	   		{
	   			var colName=list[i];
	   			
//	   			if((DA.colMap[colName] == "BIGINT" || DA.colMap[colName] == "DECIMAL" || DA.colMap[colName] == "INTEGER" || DA.colMap[colName] == "SMALLINT") && !isInt)
//	   				continue;
//	   			if(DA.colMap[colName].indexOf("VAR")==0 && !isText)
//	   				continue;
	   			
	   			if(DA.colMap[colName].toUpperCase() != "TIMESTAMP" && RC.chartType =="gantt" && suffix !='label')
	   				continue;
	   					
	   			
	   			
	   			groupHeader+='<tr><td nowrap="nowrap">'
	   			
   				if(isChecked){
   					groupHeader+='<input type="checkbox" name="'+prefix+'_y'+suffix+list[i]+'" id="'+prefix+'_y_'+suffix+list[i]+'" value="'+list[i]+'" onclick="RC.setChartY(\''+prefix+'\',\''+suffix+'\',\''+list[i]+'\', this.checked, true);" > ';
   				}else{
   					groupHeader+='<input type="radio" name="'+prefix+'_y'+suffix+'" id="'+prefix+'_y_'+suffix+list[i]+'" value="'+list[i]+'" onclick="RC.setChartY(\''+prefix+'\',\''+suffix+'\',\''+list[i]+'\', this.checked, '+isChecked+');" > '
   				}
   			
	   			groupHeader+=list[i]+'</td>';
	   			groupHeader+='<td nowrap="nowrap">'+RC.getAggregateFunctionDropDown(prefix+'_y_'+suffix+'_aggregate',list[i],'RC.selectY(\''+prefix+'\',\''+suffix+'\',this.value,\''+list[i]+'\')', isInt, isText, isDate)+'</td>';
	   			groupHeader+='</tr>'
	   				//				}
	   		}
	   		groupHeader+='</tbody></table>'
	   		
	   		return groupHeader
		},
		
		setChartY : function(prefix,suffix,colName,isChecked, notIsRadio){
	   			if(isChecked){
	   				if(!notIsRadio) {
//	   					$('#'+prefix+suffix+'_chart_name').find('select').each(function() {
//	   						console.log(this);
//	   						this.attr('disabled','disabled');	   						
//	   					});
	   					$('#'+prefix+suffix+'_chart_name').find('select').attr('disabled','disabled')
	   				}
	   	   			$('#'+prefix+'_y_'+suffix+'_aggregate'+colName).removeAttr('disabled');
	   	   		}else{
	   	   			$('#'+prefix+'_y_'+suffix+'_aggregate'+colName).attr('disabled','disabled');
	   	   			
	   	   		}
   			RC.selectY(prefix,suffix,colName,isChecked);
		},	

	   	
		selectY : function(prefix,suffix,value,colName){
			var frm = document.getElementById(prefix+suffix+'_chart_name').getElementsByTagName("input");
   			var len = frm.length;
   			var cond = '';
   			var obj=new Object();
   			//RC.selectedLineChartYSeriesArray=[];
   			var array = new Array();
   			for (i=0;i<len;i++) 
   			{
   			    if (frm[i].type == "checkbox"||frm[i].type == "radio") 
   			    { 
   			    	if(frm[i].checked){
   			    		var colName=frm[i].value;
   			    		var val = $('#'+prefix+'_y_'+suffix+'_aggregate'+colName).val();
   			    		if(val==""){
   			    			val="#";
   			    		}
   			    		obj[colName]=val;
   			    		if($('#'+prefix+'_y_'+suffix+'_aggregate'+colName).val()== undefined || $('#'+prefix+'_y_'+suffix+'_aggregate'+colName).val()=="")
   			    		{
   				   			array.push(colName);
   				   		}
   			    		else
   			    		{
   				   			var func = $('#'+prefix+'_y_'+suffix+'_aggregate'+colName).val()+"("+colName+")";
   				   			array.push(func);
   				   		}
   			    	}
   			    }
   			}
   			var name=prefix+suffix;
   			RC.selectedChartYObject[name]=obj;
   			RC.selectedChartYArray[name] = array;
   			$('#'+prefix+'_y_'+suffix).val(array);   			
	   	},

	   	closeBox : function(){
//	   		Util.removeLightbox("addchart");
	   		DA.showReportPreview();
//	   		RC.showChartInDiv();
//	   		RC.ready();
	   	},
	   	
	   	addBubbleChart : function(value){
	   		
	   		var chartType=$('#chart_type').val();
	   		RC.chartType = chartType;
	   		$('#error_msg').text("");
	   		var title = $('#line_chart_title').val();
	   		var xseries = $('#bubble_x_series').val();
	   		
	   		var xseriesSortColumn = $('#bubble_x_series_sort_column').val();
	   		
	   		var xlegend=$('#bubble_x_axis_legend').val();
	   		var yGrouping = $('#bubble_y_series_grouping').val();
	   		var ylegend=$('#bubble_y_axis_legend').val();
	   		var position = $('#chart_position').val();
	   		
	   		var chartWidth = $('#chart_width').val();
	   		var chartPos = $('#row_position').val();
	   		var chartColSpan = $('#col_span').val();
	   		var chartHeight = $('#chart_height').val();
	   		var chartDim = $('#chart_dimension').val();
	   		
	   		
	   		var cnt = jQuery("#chartTable").jqGrid('getGridParam', 'records');
	   		if(cnt == 0)
	   			value = true;
	   		if(chartType==""){
	   			$('#error_msg').text("Chart Type is not selected");
	   			return;
	   		}
	   		if(title==""){
	   			$('#error_msg').text("Title is not provided");
	   			return;
	   		}
	   		
	   		if(chartPos==""){
	   			$('#error_msg').text("Row Position is not provided");
	   			return;
	   		}
	   		
	   		if(chartColSpan==""){
	   			$('#error_msg').text("Column Span is not provided");
	   			return;
	   		}
	   		
	   		if(chartWidth==""){
	   			$('#error_msg').text("Chart Width is not provided");
	   			return;
	   		}
	   		
	   		if(chartDim==""){
	   			$('#error_msg').text("Chart Dimension is not provided");
	   			return;
	   		}

	   		if(chartHeight==""){
	   			$('#error_msg').text("Chart Height is not provided");
	   			return;
	   		}

	   		if(xseries==""){
	   			$('#error_msg').text("X Series is not provided");
	   			return;
	   		}

	   		if(RC.selectedChartYArray['bubblevalue'].length==0)
	   		{
	   			$('#error_msg').text("Y Series Value is not provided");
	   			return;
	   		}
	   		if(RC.selectedChartYArray['bubblesize'].length==0)
	   		{
	   			$('#error_msg').text("Y Series Size is not provided");
	   			return;
	   		}
	   		
	   		if(xlegend==""){
	   			$('#error_msg').text("X Axis legend is not provided");
	   			return;
	   		}
	   		if(ylegend==""){
	   			$('#error_msg').text("Y Axis legend is not provided");
	   			return;
	   		}
	   		
	   		var chartObject = new Object();
	   		chartObject["type"] = RC.chartType;
	   		chartObject["title"]=title;
	   		chartObject["position"]=position;
	   		chartObject["width"] = chartWidth;
	   		chartObject["height"] = chartHeight;
	   		chartObject["dimension"] = chartDim;
	   		chartObject["rowPosition"] = chartPos;
	   		chartObject["colSpan"] = chartColSpan;
	   		chartObject["xseries"]=xseries;
	   		chartObject["ygrouping"]=yGrouping;
	   		chartObject["xseriesSortColumn"] = xseriesSortColumn;
	   		chartObject["xseriesSortType"] = RC.sortType;
	   		chartObject["yseries"]={};
	   		chartObject["yseriesArray"]={};
	   		
	   		chartObject["yseriesValue"]=RC.selectedChartYObject['bubblevalue'];
	   		chartObject["yseriesValueArray"]=RC.selectedChartYArray['bubblevalue'];
	   		chartObject["yseriesSize"]=RC.selectedChartYObject['bubblesize'];
	   		chartObject["yseriesSizeArray"]=RC.selectedChartYArray['bubblesize'];
	   		chartObject["yseriesArray"]=["LEN"];
	   		
	   		chartObject["xlegend"]=xlegend;
	   		chartObject["ylegend"]=ylegend;
	   		chartObject["align"]="center";
	   		
	   		if(DA.queryInfo["chartDetail"]==undefined||DA.queryInfo["chartDetail"]==""||DA.queryInfo["chartDetail"]==null){
	   			DA.queryInfo["chartDetail"]=new Object();
	   		}else if(DA.queryInfo["chartDetail"]["chartPreferences"]==undefined||DA.queryInfo["chartDetail"]["chartPreferences"]==""||DA.queryInfo["chartDetail"]["chartPreferences"]==null){
	   			
	   		}else{
	   			var chartPreferences =null;
	   			if(value){
	   				var obj = DA.getInitialChartPRObject();
					chartPreferences = obj["chartPreferences"];
	   			}else{
	   				if(DA.queryInfo["chartDetail"][DA.selectedChartId] != null && DA.queryInfo["chartDetail"][DA.selectedChartId] !=undefined){
	   					chartPreferences  = DA.queryInfo["chartDetail"][DA.selectedChartId]["chartPreferences"];
	   				}else{
	   					 var obj = DA.getInitialChartPRObject();
	   					chartPreferences = obj["chartPreferences"];
	   				}
	   			}	
	   			chartObject["chartPreferences"] = jQuery.extend(true, {}, chartPreferences);
	   			
	   		}
	   		if(value)
	   		{
	   			var count=0;
	   			for(var key in DA.queryInfo["chartDetail"])
	   			{
		   			var temp = DA.queryInfo["chartDetail"][key]["title"];
		   			if(temp == $('#line_chart_title').val())
		   			{
		   				$('#error_msg').text("Title already exists");
		   				return;
		   			}
		   			count++;
	   			}
	   			var chartkey="chart"+count;
	   			while(DA.queryInfo["chartDetail"].hasOwnProperty(chartkey)){
	   				count++;
	   				chartkey="chart"+count;
	   			}
	   			
	   			DA.queryInfo["chartDetail"][chartkey]=chartObject;	   			
	   		}
	   		else
	   		{
	   			
	   			for(var key in DA.queryInfo["chartDetail"])
	   			{
	   				var chartkey = "chart"+count;
	   				if(key == DA.selectedChartId)
	   					DA.queryInfo["chartDetail"][key]=chartObject;
	   			}
	   			Navbar.queryManagerDirtyBit = true;
	   			
	   		}
	   		DA.currentSelectedChart = title;
	   		DA.createChartGrid(DA.queryInfo["chartDetail"]);
	   		DA.chartDesignerDirtyBit=false;
	   		RC.closeBox();
	   	},
	   	
	   	
	   	addStockChart : function(value){
	   		
	   		var chartType=$('#chart_type').val();
	   		RC.chartType = chartType;
	   		$('#error_msg').text("");
	   		var title = $('#line_chart_title').val();
	   		var position = $('#chart_position').val();	   		
	   		var chartWidth = $('#chart_width').val();
	   		var chartPos = $('#row_position').val();
	   		var chartColSpan = $('#col_span').val();
	   		var chartHeight = $('#chart_height').val();
	   		var chartDim = $('#chart_dimension').val();
	   		
	   		var xseries = $('#stock_x_series').val();
	   		var yGrouping = $('#stock_y_series_grouping').val();
	   		var xseriesSortColumn = $('#stock_x_series_sort_column').val();
	   		
	   		var xlegend=$('#stock_x_axis_legend').val();
	   		var ylegend=$('#stock_y_axis_legend').val();
	   		
	   		var cnt = jQuery("#chartTable").jqGrid('getGridParam', 'records');
	   		if(cnt == 0)
	   			value = true;
	   		if(chartType==""){
	   			$('#error_msg').text("Chart Type is not selected");
	   			return;
	   		}
	   		if(title==""){
	   			$('#error_msg').text("Title is not provided");
	   			return;
	   		}
	   		
	   		if(chartPos==""){
	   			$('#error_msg').text("Row Position is not provided");
	   			return;
	   		}
	   		
	   		if(chartColSpan==""){
	   			$('#error_msg').text("Column Span is not provided");
	   			return;
	   		}
	   		
	   		if(chartWidth==""){
	   			$('#error_msg').text("Chart Width is not provided");
	   			return;
	   		}
	   		
	   		if(chartDim==""){
	   			$('#error_msg').text("Chart Dimension is not provided");
	   			return;
	   		}

	   		if(chartHeight==""){
	   			$('#error_msg').text("Chart Height is not provided");
	   			return;
	   		}

	   		if(xseries==""){
	   			$('#error_msg').text("X Series is not provided");
	   			return;
	   		}

	   		if(RC.selectedChartYArray['stockhigh'].length==0)
	   		{
	   			$('#error_msg').text("Y Series High is not provided");
	   			return;
	   		}
	   		if(RC.selectedChartYArray['stocklow'].length==0)
	   		{
	   			$('#error_msg').text("Y Series Low is not provided");
	   			return;
	   		}
	   		
	   		if(RC.selectedChartYArray['stockopen'].length==0)
	   		{
	   			$('#error_msg').text("Y Series Open is not provided");
	   			return;
	   		}
	   		if(RC.selectedChartYArray['stockclose'].length==0)
	   		{
	   			$('#error_msg').text("Y Series Close is not provided");
	   			return;
	   		}
	   		
	   		
	   		if(xlegend==""){
	   			$('#error_msg').text("X Axis legend is not provided");
	   			return;
	   		}
	   		if(ylegend==""){
	   			$('#error_msg').text("Y Axis legend is not provided");
	   			return;
	   		}
	   		
	   		var chartObject = new Object();
	   		chartObject["type"] = RC.chartType;
	   		chartObject["title"]=title;
	   		chartObject["position"]=position;
	   		chartObject["width"] = chartWidth;
	   		chartObject["height"] = chartHeight;
	   		chartObject["dimension"] = chartDim;
	   		chartObject["rowPosition"] = chartPos;
	   		chartObject["colSpan"] = chartColSpan;
	   		chartObject["xseries"]=xseries;
	   		chartObject["ygrouping"]=yGrouping;
	   		chartObject["xseriesSortColumn"] = xseriesSortColumn;
	   		chartObject["xseriesSortType"] = RC.sortType;
	   		chartObject["yseries"]={};
	   		chartObject["yseriesArray"]={};
	   		
	   		chartObject["yseriesHigh"]=RC.selectedChartYObject['stockhigh'];
	   		chartObject["yseriesHighArray"]=RC.selectedChartYArray['stockhigh'];
	   		
	   		chartObject["yseriesLow"]=RC.selectedChartYObject['stocklow'];
	   		chartObject["yseriesLowArray"]=RC.selectedChartYArray['stocklow'];
	   		
	   		chartObject["yseriesOpen"]=RC.selectedChartYObject['stockopen'];
	   		chartObject["yseriesOpenArray"]=RC.selectedChartYArray['stockopen'];
	   		
	   		chartObject["yseriesClose"]=RC.selectedChartYObject['stockclose'];
	   		chartObject["yseriesCloseArray"]=RC.selectedChartYArray['stockclose'];
	   		
	   		chartObject["xlegend"]=xlegend;
	   		chartObject["ylegend"]=ylegend;
	   		chartObject["align"]="center";
	   		
	   		if(DA.queryInfo["chartDetail"]==undefined||DA.queryInfo["chartDetail"]==""||DA.queryInfo["chartDetail"]==null){
	   			DA.queryInfo["chartDetail"]=new Object();
	   		}else if(DA.queryInfo["chartDetail"]["chartPreferences"]==undefined||DA.queryInfo["chartDetail"]["chartPreferences"]==""||DA.queryInfo["chartDetail"]["chartPreferences"]==null){
	   			
	   		}else{
	   			var chartPreferences =null;
	   			if(value){
	   				//chartPreferences = DA.queryInfo["chartDetail"]["chartPreferences"];
	   				var obj = DA.getInitialChartPRObject();
					chartPreferences = obj["chartPreferences"];
	   			}else{
	   				if(DA.queryInfo["chartDetail"][DA.selectedChartId] != null && DA.queryInfo["chartDetail"][DA.selectedChartId] !=undefined){
	   					chartPreferences  = DA.queryInfo["chartDetail"][DA.selectedChartId]["chartPreferences"];
	   				}else{
	   					 var obj = DA.getInitialChartPRObject();
	   					chartPreferences = obj["chartPreferences"];
	   				}
	   			}	
	   			chartObject["chartPreferences"] = jQuery.extend(true, {}, chartPreferences);
	   			
	   		}
	   		if(value)
	   		{
	   			var count=0;
	   			for(var key in DA.queryInfo["chartDetail"])
	   			{
		   			var temp = DA.queryInfo["chartDetail"][key]["title"];
		   			if(temp == $('#line_chart_title').val())
		   			{
		   				$('#error_msg').text("Title already exists");
		   				return;
		   			}
		   			count++;
	   			}
	   			var chartkey="chart"+count;
	   			while(DA.queryInfo["chartDetail"].hasOwnProperty(chartkey)){
	   				count++;
	   				chartkey="chart"+count;
	   			}
	   			
	   			DA.queryInfo["chartDetail"][chartkey]=chartObject;	   			
	   		}
	   		else
	   		{
	   			
	   			for(var key in DA.queryInfo["chartDetail"])
	   			{
	   				var chartkey = "chart"+count;
	   				if(key == DA.selectedChartId)
	   					DA.queryInfo["chartDetail"][key]=chartObject;
	   			}
	   			Navbar.queryManagerDirtyBit = true;
	   			
	   		}
	   		DA.currentSelectedChart = title;
	   		DA.createChartGrid(DA.queryInfo["chartDetail"]);
	   		DA.chartDesignerDirtyBit=false;
	   		RC.closeBox();
	   	},
	   	
	   	
	   	addDifferenceChart : function(value){
	   		
	   		var chartType=$('#chart_type').val();
	   		RC.chartType = chartType;
	   		$('#error_msg').text("");
	   		var title = $('#line_chart_title').val();
	   		var chartWidth = $('#chart_width').val();
	   		var chartPos = $('#row_position').val();
	   		var chartColSpan = $('#col_span').val();
	   		var chartHeight = $('#chart_height').val();
	   		var chartDim = $('#chart_dimension').val();
	   		var position = $('#chart_position').val();

	   		var xseries = $('#difference_x_series').val();
	   		var yGrouping = $('#difference_y_series_grouping').val();	   		
	   		var xseriesSortColumn = $('#difference_x_series_sort_column').val();
	   		
	   		var xlegend=$('#difference_x_axis_legend').val();
	   		var ylegend=$('#difference_y_axis_legend').val();
	   		
	   		
	   		
	   		var cnt = jQuery("#chartTable").jqGrid('getGridParam', 'records');
	   		if(cnt == 0)
	   			value = true;
	   		if(chartType==""){
	   			$('#error_msg').text("Chart Type is not selected");
	   			return;
	   		}
	   		if(title==""){
	   			$('#error_msg').text("Title is not provided");
	   			return;
	   		}
	   		
	   		if(chartPos==""){
	   			$('#error_msg').text("Row Position is not provided");
	   			return;
	   		}
	   		
	   		if(chartColSpan==""){
	   			$('#error_msg').text("Column Span is not provided");
	   			return;
	   		}
	   		
	   		if(chartWidth==""){
	   			$('#error_msg').text("Chart Width is not provided");
	   			return;
	   		}
	   		
	   		if(chartDim==""){
	   			$('#error_msg').text("Chart Dimension is not provided");
	   			return;
	   		}

	   		if(chartHeight==""){
	   			$('#error_msg').text("Chart Height is not provided");
	   			return;
	   		}

	   		if(xseries==""){
	   			$('#error_msg').text("X Series is not provided");
	   			return;
	   		}

	   		if(RC.selectedChartYArray['differencepositive'].length==0)
	   		{
	   			$('#error_msg').text("Y Series Positive is not provided");
	   			return;
	   		}
	   		if(RC.selectedChartYArray['differencenegetive'].length==0)
	   		{
	   			$('#error_msg').text("Y Series Negative is not provided");
	   			return;
	   		}
	   		
	   		if(xlegend==""){
	   			$('#error_msg').text("X Axis legend is not provided");
	   			return;
	   		}
	   		if(ylegend==""){
	   			$('#error_msg').text("Y Axis legend is not provided");
	   			return;
	   		}
	   		
	   		var chartObject = new Object();
	   		chartObject["type"] = RC.chartType;
	   		chartObject["title"]=title;
	   		chartObject["position"]=position;
	   		chartObject["width"] = chartWidth;
	   		chartObject["height"] = chartHeight;
	   		chartObject["dimension"] = chartDim;
	   		chartObject["rowPosition"] = chartPos;
	   		chartObject["colSpan"] = chartColSpan;
	   		chartObject["xseries"]=xseries;
	   		chartObject["ygrouping"]=yGrouping;
	   		chartObject["xseriesSortColumn"] = xseriesSortColumn;
	   		chartObject["xseriesSortType"] = RC.sortType;
	   		chartObject["yseries"]={};
	   		chartObject["yseriesArray"]={};
	   		
	   		chartObject["yseriesPositive"]=RC.selectedChartYObject['differencepositive'];
	   		chartObject["yseriesPositiveArray"]=RC.selectedChartYArray['differencepositive'];
	   		
	   		chartObject["yseriesNegative"]=RC.selectedChartYObject['differencenegetive'];
	   		chartObject["yseriesNegativeArray"]=RC.selectedChartYArray['differencenegetive'];
	   		
	   		chartObject["xlegend"]=xlegend;
	   		chartObject["ylegend"]=ylegend;
	   		chartObject["align"]="center";
	   		
	   		if(DA.queryInfo["chartDetail"]==undefined||DA.queryInfo["chartDetail"]==""||DA.queryInfo["chartDetail"]==null){
	   			DA.queryInfo["chartDetail"]=new Object();
	   		}else if(DA.queryInfo["chartDetail"]["chartPreferences"]==undefined||DA.queryInfo["chartDetail"]["chartPreferences"]==""||DA.queryInfo["chartDetail"]["chartPreferences"]==null){
	   			
	   		}else{
	   			var chartPreferences =null;
	   			if(value){
	   				//chartPreferences = DA.queryInfo["chartDetail"]["chartPreferences"];
	   				var obj = DA.getInitialChartPRObject();
					chartPreferences = obj["chartPreferences"];
	   			}else{
	   				if(DA.queryInfo["chartDetail"][DA.selectedChartId] != null && DA.queryInfo["chartDetail"][DA.selectedChartId] !=undefined){
	   					chartPreferences  = DA.queryInfo["chartDetail"][DA.selectedChartId]["chartPreferences"];
	   				}else{
	   					 var obj = DA.getInitialChartPRObject();
	   					chartPreferences = obj["chartPreferences"];
	   				}
	   			}	
	   			chartObject["chartPreferences"] = jQuery.extend(true, {}, chartPreferences);
	   			
	   		}
	   		if(value)
	   		{
	   			var count=0;
	   			for(var key in DA.queryInfo["chartDetail"])
	   			{
		   			var temp = DA.queryInfo["chartDetail"][key]["title"];
		   			if(temp == $('#line_chart_title').val())
		   			{
		   				$('#error_msg').text("Title already exists");
		   				return;
		   			}
		   			count++;
	   			}
	   			var chartkey="chart"+count;
	   			while(DA.queryInfo["chartDetail"].hasOwnProperty(chartkey)){
	   				count++;
	   				chartkey="chart"+count;
	   			}
	   			
	   			DA.queryInfo["chartDetail"][chartkey]=chartObject;	   			
	   		}
	   		else
	   		{
	   			
	   			for(var key in DA.queryInfo["chartDetail"])
	   			{
	   				var chartkey = "chart"+count;
	   				if(key == DA.selectedChartId)
	   					DA.queryInfo["chartDetail"][key]=chartObject;
	   			}
	   			Navbar.queryManagerDirtyBit = true;
	   			
	   		}
	   		DA.currentSelectedChart = title;
	   		DA.createChartGrid(DA.queryInfo["chartDetail"]);
	   		DA.chartDesignerDirtyBit=false;
	   		RC.closeBox();
	   	},
	   	
	   	
	   	addGanttChart : function(value){
	   		
	   		var chartType=$('#chart_type').val();
	   		RC.chartType = chartType;
	   		$('#error_msg').text("");
	   		var title = $('#line_chart_title').val();
	   		var chartWidth = $('#chart_width').val();
	   		var chartPos = $('#row_position').val();
	   		var chartColSpan = $('#col_span').val();
	   		var chartHeight = $('#chart_height').val();
	   		var chartDim = $('#chart_dimension').val();
	   		var position = $('#chart_position').val();

	   		var xseries = $('#gantt_x_series').val();
	   		var yGrouping = $('#y_series_grouping').val();
	   		var xseriesSortColumn = $('#gantt_x_series_sort_column').val();
	   		
	   		var xlegend=$('#gantt_x_axis_legend').val();
	   		var ylegend=$('#gantt_y_axis_legend').val();
	   		
	   		
	   		
	   		var cnt = jQuery("#chartTable").jqGrid('getGridParam', 'records');
	   		if(cnt == 0)
	   			value = true;
	   		if(chartType==""){
	   			$('#error_msg').text("Chart Type is not selected");
	   			return;
	   		}
	   		if(title==""){
	   			$('#error_msg').text("Title is not provided");
	   			return;
	   		}
	   		
	   		if(chartPos==""){
	   			$('#error_msg').text("Row Position is not provided");
	   			return;
	   		}
	   		
	   		if(chartColSpan==""){
	   			$('#error_msg').text("Column Span is not provided");
	   			return;
	   		}
	   		
	   		if(chartWidth==""){
	   			$('#error_msg').text("Chart Width is not provided");
	   			return;
	   		}
	   		
	   		if(chartDim==""){
	   			$('#error_msg').text("Chart Dimension is not provided");
	   			return;
	   		}

	   		if(chartHeight==""){
	   			$('#error_msg').text("Chart Height is not provided");
	   			return;
	   		}

	   		if(xseries==""){
	   			$('#error_msg').text("X Series is not provided");
	   			return;
	   		}

	   		if(RC.selectedChartYArray['ganttlabel'].length==0)
	   		{
	   			$('#error_msg').text("Task Label is not provided");
	   			return;
	   		}
	   		if(RC.selectedChartYArray['ganttstart'].length==0)
	   		{
	   			$('#error_msg').text("Start Date is not provided");
	   			return;
	   		}
	   		if(RC.selectedChartYArray['ganttend'].length==0)
	   		{
	   			$('#error_msg').text("End Date is not provided");
	   			return;
	   		}
	   		if(xlegend==""){
	   			$('#error_msg').text("X Axis legend is not provided");
	   			return;
	   		}
	   		if(ylegend==""){
	   			$('#error_msg').text("Y Axis legend is not provided");
	   			return;
	   		}
	   		
	   		var chartObject = new Object();
	   		chartObject["type"] = RC.chartType;
	   		chartObject["title"]=title;
	   		chartObject["position"]=position;
	   		chartObject["width"] = chartWidth;
	   		chartObject["height"] = chartHeight;
	   		chartObject["dimension"] = chartDim;
	   		chartObject["rowPosition"] = chartPos;
	   		chartObject["colSpan"] = chartColSpan;
	   		chartObject["xseries"]=xseries;
	   		chartObject["ygrouping"]=yGrouping;
	   		chartObject["xseriesSortColumn"] = xseriesSortColumn;
	   		chartObject["xseriesSortType"] = RC.sortType;
	   		chartObject["yseries"]={};
	   		chartObject["yseriesArray"]={};
	   		
	   		chartObject["yseriesLabel"]=RC.selectedChartYObject['ganttlabel'];
	   		chartObject["yseriesLabelArray"]=RC.selectedChartYArray['ganttlabel'];
	   		
	   		chartObject["yseriesStart"]=RC.selectedChartYObject['ganttstart'];
	   		chartObject["yseriesStartArray"]=RC.selectedChartYArray['ganttstart'];
	   		
	   		chartObject["yseriesEnd"]=RC.selectedChartYObject['ganttend'];
	   		chartObject["yseriesEndArray"]=RC.selectedChartYArray['ganttend'];
	   		
	   		chartObject["xlegend"]=xlegend;
	   		chartObject["ylegend"]=ylegend;
	   		chartObject["align"]="center";
	   		
	   		if(DA.queryInfo["chartDetail"]==undefined||DA.queryInfo["chartDetail"]==""||DA.queryInfo["chartDetail"]==null){
	   			DA.queryInfo["chartDetail"]=new Object();
	   		}else if(DA.queryInfo["chartDetail"]["chartPreferences"]==undefined||DA.queryInfo["chartDetail"]["chartPreferences"]==""||DA.queryInfo["chartDetail"]["chartPreferences"]==null){
	   			
	   		}else{
	   			var chartPreferences =null;
	   			if(value){
	   				//chartPreferences = DA.queryInfo["chartDetail"]["chartPreferences"];
	   				var obj = DA.getInitialChartPRObject();
					chartPreferences = obj["chartPreferences"];
	   			}else{
	   				if(DA.queryInfo["chartDetail"][DA.selectedChartId] != null && DA.queryInfo["chartDetail"][DA.selectedChartId] !=undefined){
	   					chartPreferences  = DA.queryInfo["chartDetail"][DA.selectedChartId]["chartPreferences"];
	   				}else{
	   					 var obj = DA.getInitialChartPRObject();
	   					chartPreferences = obj["chartPreferences"];
	   				}
	   			}	
	   			chartObject["chartPreferences"] = jQuery.extend(true, {}, chartPreferences);
	   			
	   		}
	   		if(value)
	   		{
	   			var count=0;
	   			for(var key in DA.queryInfo["chartDetail"])
	   			{
		   			var temp = DA.queryInfo["chartDetail"][key]["title"];
		   			if(temp == $('#line_chart_title').val())
		   			{
		   				$('#error_msg').text("Title already exists");
		   				return;
		   			}
		   			count++;
	   			}
	   			var chartkey="chart"+count;
	   			while(DA.queryInfo["chartDetail"].hasOwnProperty(chartkey)){
	   				count++;
	   				chartkey="chart"+count;
	   			}
	   			
	   			DA.queryInfo["chartDetail"][chartkey]=chartObject;	   			
	   		}
	   		else
	   		{
	   			
	   			for(var key in DA.queryInfo["chartDetail"])
	   			{
	   				var chartkey = "chart"+count;
	   				if(key == DA.selectedChartId)
	   					DA.queryInfo["chartDetail"][key]=chartObject;
	   			}
	   			Navbar.queryManagerDirtyBit = true;
	   			
	   		}
	   		DA.currentSelectedChart = title;
	   		DA.createChartGrid(DA.queryInfo["chartDetail"]);
	   		DA.chartDesignerDirtyBit=false;
	   		RC.closeBox();
	   	},
	   	
	   	
	   	addLineChart : function(value)
	   	{
	   		
	   		var chartType=$('#chart_type').val();
	   		
	   		RC.chartType = chartType;
	   		$('#error_msg').text("");
	   		
	   		
	   		
	   		var title = $('#line_chart_title').val();
	   		var xseries = $('#line_x_series').val();
	   		var yGrouping = $('#y_series_grouping').val();	
	   		var xseriesSortColumn = $('#line_x_series_sort_column').val();
	   		var chartscale = $('#line_x_series_scale').val();
	   		
	   		var chartScaleMinVal = $('#y_scale_min_val').val();
	   		
//	   		var yseries = $('#line_y_series').val();
	   		var xlegend=$('#line_x_axis_legend').val();
	   		var ylegend=$('#line_y_axis_legend').val();
	   		var position = $('#chart_position').val();
	   		
	   		var chartWidth = $('#chart_width').val();
	   		var chartPos = $('#row_position').val();
	   		var chartColSpan = $('#col_span').val();
	   		var chartHeight = $('#chart_height').val();
	   		var chartDim = $('#chart_dimension').val();
	   		
//	   		RC.selectedLineChartYSeriesArray = $("#line_y_series").val();
	   		
//	   		RC.selectedLineChartYSeriesArray.push(yseries);
	   		
	   		var cnt = jQuery("#chartTable").jqGrid('getGridParam', 'records');
	   		if(cnt == 0)
	   			value = true;
	   		if(chartType==""){
	   			$('#error_msg').text("Chart Type is not selected");
	   			return;
	   		}
	   		if(title==""){
	   			$('#error_msg').text("Title is not provided");
	   			return;
	   		}
	   		
	   		if(chartPos==""){
	   			$('#error_msg').text("Row Position is not provided");
	   			return;
	   		}
	   		
	   		if(chartColSpan==""){
	   			$('#error_msg').text("Column Span is not provided");
	   			return;
	   		}
	   		
	   		if(chartWidth==""){
	   			$('#error_msg').text("Chart Width is not provided");
	   			return;
	   		}
	   		
	   		if(chartDim==""){
	   			$('#error_msg').text("Chart Dimension is not provided");
	   			return;
	   		}

	   		if(chartHeight==""){
	   			$('#error_msg').text("Chart Height is not provided");
	   			return;
	   		}

	   		if(xseries==""){
	   			$('#error_msg').text("X Series is not provided");
	   			return;
	   		}

	   		if(RC.selectedChartYArray['lineseries'].length==0)
	   		{
	   			$('#error_msg').text("Y Series is not provided");
	   			return;
	   		}
	   		if(xlegend==""){
	   			$('#error_msg').text("X Axis legend is not provided");
	   			return;
	   		}
	   		if(ylegend==""){
	   			$('#error_msg').text("Y Axis legend is not provided");
	   			return;
	   		}
	   		
	   		var chartObject = new Object();
	   		chartObject["type"] = RC.chartType;
	   		chartObject["title"]=title;
	   		chartObject["position"]=position;
	   		chartObject["width"] = chartWidth;
	   		chartObject["height"] = chartHeight;
	   		chartObject["dimension"] = chartDim;
	   		chartObject["rowPosition"] = chartPos;
	   		chartObject["colSpan"] = chartColSpan;
	   		chartObject["xseries"]=xseries;
	   		
	   		chartObject["chartscale"]=chartscale;
	   		
	   		
	   		chartObject["ygrouping"]=yGrouping;
	   		chartObject["xseriesSortColumn"] = xseriesSortColumn;
	   		chartObject["xseriesSortType"] = RC.sortType;
	   		chartObject["yseries"]=RC.selectedChartYObject['lineseries'];
	   		chartObject["yseriesArray"]=RC.selectedChartYArray['lineseries'];
	   		chartObject["xlegend"]=xlegend;
	   		chartObject["ylegend"]=ylegend;
	   		chartObject["align"]="center";
	   		chartObject["yScaleMinVal"]=chartScaleMinVal;
	   		
	   		
	   		if(DA.queryInfo["chartDetail"]==undefined||DA.queryInfo["chartDetail"]==""||DA.queryInfo["chartDetail"]==null){
	   			DA.queryInfo["chartDetail"]=new Object();
	   		}else if(DA.queryInfo["chartDetail"]["chartPreferences"]==undefined||DA.queryInfo["chartDetail"]["chartPreferences"]==""||DA.queryInfo["chartDetail"]["chartPreferences"]==null){
	   			JSON.stringify("CHARTPR "+JSON.stringify(CHARTPR.chartPR));
	   		}else{
	   			var chartPreferences =null;
	   			if(value){
	   				//chartPreferences = DA.queryInfo["chartDetail"]["chartPreferences"];
	   				var obj = DA.getInitialChartPRObject();
					chartPreferences = obj["chartPreferences"];
	   			}else{
	   				if(DA.queryInfo["chartDetail"][DA.selectedChartId] != null && DA.queryInfo["chartDetail"][DA.selectedChartId] !=undefined){
	   					chartPreferences  = DA.queryInfo["chartDetail"][DA.selectedChartId]["chartPreferences"];
	   				}else{
	   					 var obj = DA.getInitialChartPRObject();
	   					chartPreferences = obj["chartPreferences"];
	   				}
	   			}	
	   			chartObject["chartPreferences"] = jQuery.extend(true, {}, chartPreferences);
	   			
	   		}
	   		console.log("DA.globalChartPreferences "+JSON.stringify(DA.globalChartPreferences));
	   		if(DA.queryInfo["chartDetail"]["chartPreferences"] == undefined || DA.queryInfo["chartDetail"]["chartPreferences"] ==null)
	   			DA.queryInfo["chartDetail"]["chartPreferences"] = DA.globalChartPreferences;
	   		
	   		if(value)
	   		{
	   			var count=0;
	   			for(var key in DA.queryInfo["chartDetail"])
	   			{
		   			var temp = DA.queryInfo["chartDetail"][key]["title"];
		   			if(temp == $('#line_chart_title').val())
		   			{
		   				$('#error_msg').text("Title already exists");
		   				return;
		   			}
		   			count++;
	   			}
	   			var chartkey="chart"+count;
	   			while(DA.queryInfo["chartDetail"].hasOwnProperty(chartkey)){
	   				count++;
	   				chartkey="chart"+count;
	   			}
	   			
	   			DA.queryInfo["chartDetail"][chartkey]=chartObject;	   			
	   		}
	   		else
	   		{
	   			
	   			for(var key in DA.queryInfo["chartDetail"])
	   			{
	   				var chartkey = "chart"+count;
	   				if(key == DA.selectedChartId)
	   					DA.queryInfo["chartDetail"][key]=chartObject;
	   			}
	   			Navbar.queryManagerDirtyBit = true;
	   			
	   		}
	   		DA.currentSelectedChart = title;
	   		DA.createChartGrid(DA.queryInfo["chartDetail"]);
	   		DA.chartDesignerDirtyBit=false;
	   		RC.closeBox();
	   		
	   	},
	   	
	   	addPieChart : function(value){
	   		$('#error_msg').text("");
	   		var chartType=$('#chart_type').val();
	   		RC.chartType = chartType;
	   		var title = $('#line_chart_title').val();
	   		var xseries = $('#pie_x_series').val();
	   		var yseries = $('#pie_y_series').val();
	   		
	   		var position = $('#chart_position').val();
	   		
	   		var chartDim = $('#chart_dimension').val();
	   		var chartPos = $('#row_position').val();
	   		var chartColSpan = $('#col_span').val();
			
	   		var xlegend=$('#pie_x_axis_legend').val();
	   		var ylegend=$('#pie_y_axis_legend').val();
	   		
	   		var chartWidth = $('#chart_width').val();
	   		var chartHeight = $('#chart_height').val();
	   		
	   		var cnt = jQuery("#chartTable").jqGrid('getGridParam', 'records');
	   		if(cnt == 0)
	   			value = true;
	   		if(chartType==""){
	   			$('#error_msg').text("Chart Type is not selected");
	   			return;
	   		}
	   		
	   		if(title==""){
	   			$('#error_msg').text("Title is not provided");
	   			return;
	   		}
	   		
	   		if(chartWidth==""){
	   			$('#error_msg').text("Chart Width is not provided");
	   			return;
	   		}

	   		if(chartDim==""){
	   			$('#error_msg').text("Chart Dimension is not provided");
	   			return;
	   		}
	   		
	   		if(chartPos==""){
	   			$('#error_msg').text("Row Position is not provided");
	   			return;
	   		}
	   		
	   		if(chartColSpan==""){
	   			$('#error_msg').text("Column Span is not provided");
	   			return;
	   		}
	   		
	   		if(chartHeight==""){
	   			$('#error_msg').text("Chart Height is not provided");
	   			return;
	   		}
	   		
	   		if(xseries==""){
	   			$('#error_msg').text("X Series is not provided");
	   			return;
	   		}
	   		if(yseries==""){
	   			$('#error_msg').text("Y Series is not provided");
	   			return;
	   		}
	   		if(xlegend==""){
	   			$('#error_msg').text("X Axis legend is not provided");
	   			return;
	   		}
	   		if(ylegend==""){
	   			$('#error_msg').text("Y Axis legend is not provided");
	   			return;
	   		}
	   		var yseriesarray = new Array();
	   		yseriesarray.push(yseries);
	   		var chartObject = new Object();
	   		chartObject["type"] =chartType;
	   		chartObject["title"]=title;
	   		chartObject["position"]=position;
	   		chartObject["width"] = chartWidth;
	   		chartObject["dimension"] = chartDim;
	   		chartObject["rowPosition"] = chartPos;
	   		chartObject["colSpan"] = chartColSpan;
	   		
	   		chartObject["height"] = chartHeight;
	   		chartObject["xseries"]=xseries;
	   		chartObject["ygrouping"]=yGrouping;
	   		chartObject["yseries"]=RC.selectedLineChartYSeriesOject;
	   		chartObject["yseriesArray"]=RC.selectedLineChartYSeriesArray;
	   		chartObject["xlegend"]=xlegend;
	   		chartObject["ylegend"]=ylegend;
	   		chartObject["align"]="center";
	   		if(DA.queryInfo["chartDetail"]==undefined||DA.queryInfo["chartDetail"]==""||DA.queryInfo["chartDetail"]==null){
	   			DA.queryInfo["chartDetail"]=new Array();
	   		}else if(DA.queryInfo["chartDetail"]["chartPreferences"]==undefined||DA.queryInfo["chartDetail"]["chartPreferences"]==""||DA.queryInfo["chartDetail"]["chartPreferences"]==null){
	   			
	   		}else{
	   			//var chartPreferences  = DA.queryInfo["chartDetail"]["chartPreferences"];
	   			var chartPreferences = DA.getDefaultChartDetails(false)["chartPreferences"];
	   			chartObject["chartPreferences"] = jQuery.extend(true, {}, chartPreferences);
	   		}
//	   		var count=0;
//	   		for(key in DA.queryInfo["chartDetail"]) {
//	   		  count++;
//	   		}
//	   		var chartkey="chart"+count;
//	   		DA.queryInfo["chartDetail"][chartkey]=chartObject;
	   		if(value)
	   		{
	   			for(var key in DA.queryInfo["chartDetail"])
	   			{
		   			var temp = DA.queryInfo["chartDetail"][key]["title"];
		   			if(temp == $('#line_chart_title').val())
		   			{
		   				$('#error_msg').text("Title already exists");
		   				return;
		   			}
	   			}
	   			var count=0;
	   			for(key in DA.queryInfo["chartDetail"]) {
	   				count++;
	   			}
	   			var chartkey="chart"+count;
	   			DA.queryInfo["chartDetail"][chartkey]=chartObject;	   			
//	   			DA.resetChart();
	   		}
	   		else
	   		{
	   			
	   			for(var key in DA.queryInfo["chartDetail"])
	   			{
	   				var chartkey="chart"+count;
//	   				DA.queryInfo["chartDetail"][key][]
	   				if(key == DA.selectedChartId)
	   					DA.queryInfo["chartDetail"][key]=chartObject;
	   			}
	   		}
	   		DA.currentSelectedChart = title;
	   		DA.createChartGrid(DA.queryInfo["chartDetail"]);
	   		DA.chartDesignerDirtyBit=false;
	   		RC.closeBox();
	   	},
	   	
	   	sortOrderChanged : function(element, columnId)
	   	{
	   		RC.sortType = element.value;
	   		if (RC.sortType != "None") {
	   			$('#' + columnId).removeAttr("disabled");
	   		} else {
	   			$('#' + columnId).attr("disabled", "disabled");
	   		}
	   	},
	   	
	   	chartTypeChanged : function(element)
	   	{
	   		$('#bubble_x_axis_legend').val("X-Axis");
	   		$('#bubble_y_axis_legend').val("Y-Axis");

	   		$('#stock_x_axis_legend').val("X-Axis");
	   		$('#stock_y_axis_legend').val("Y-Axis");

	   		$('#difference_x_axis_legend').val("X-Axis");
	   		$('#difference_y_axis_legend').val("Y-Axis");

	   		$('#gantt_x_axis_legend').val("X-Axis");
	   		$('#gantt_y_axis_legend').val("Y-Axis");

	   		$('#pie_x_axis_legend').val("X-Axis");
	   		$('#pie_y_axis_legend').val("Y-Axis");

	   		$('#line_x_axis_legend').val("X-Axis");
	   		$('#line_y_axis_legend').val("Y-Axis");
	   		
	   		$('#chartPRButton').attr("disabled", "disabled");
	   		$("#error_msg").text("");
	   		RC.chartType = element.value;
	   		$('#line_chart_title').val(element.options[element.selectedIndex].text);
	   		RC.fillChartDimension(RC.chartType);
	   		if(RC.chartType != "scatter" && RC.chartType != "stock" && RC.chartType != "bubble" && RC.chartType != "difference" && RC.chartType !=  "gantt" && RC.chartType != "radar" && RC.chartType !=  "meter" ) 
			{
	   			$('#chart_dimension').prop('selectedIndex', 1);
			}
	   		
	   		if ((RC.chartType=="line") || (RC.chartType=="bar") || (RC.chartType=="pie") || (RC.chartType=="area") || (RC.chartType=="scatter") || (RC.chartType=="meter") ||  (RC.chartType=="tube") || (RC.chartType=="cone") || (RC.chartType=="pyramid") || (RC.chartType=="radar"))
	   		{
	   			if ((RC.chartType=="line") || (RC.chartType=="bar") || (RC.chartType=="area") || (RC.chartType=="tube") || (RC.chartType=="cone") || (RC.chartType=="pyramid"))
		   		{
	   				$('#y_scale_min_val_row').show();
		   		}else{
		   			$('#y_scale_min_val_row').hide()
		   		}
	   			
	   			$('#pie_chart_table').css("display","none");
	   			
	   			$('#line_chart_table').css("display","");
	   			if(RC.chartType=="meter" || RC.chartType=="radar" || RC.chartType=="pie") {
	   				$('#line_y_seriesColFilters').html(RC.getYSeriesHtmlData('line','series',true, true, false, false));
	   			}else{
	   				$('#line_y_seriesColFilters').html(RC.getYSeriesHtmlData('line','series',true, true, false, false));
	   			}
	   				   			
	   			$('#bubble_chart_table').css("display","none");
	   			$('#stock_chart_table').css("display","none");
	   			$('#difference_chart_table').css("display","none");
	   			$('#gantt_chart_table').css("display","none");
	   			
	   			$("#line_y_series").val("");
	   		} else if((RC.chartType=="stock")){
	   			$('#stock_chart_table').find('#chartPRButton').attr("disabled", "disabled");
	   			$('#pie_chart_table').css("display","none");
	   			$('#line_chart_table').css("display","none");
	   			$('#bubble_chart_table').css("display","none");
	   			
	   			$('#stock_chart_table').css("display","");
	   			$('#stock_y_highColFilters').html(RC.getYSeriesHtmlData('stock','high',true,true, false, false));
	   			$('#stock_y_lowColFilters').html(RC.getYSeriesHtmlData('stock','low',true, true, false, false));
	   			$('#stock_y_openColFilters').html(RC.getYSeriesHtmlData('stock','open',true, true, false, false));
	   			$('#stock_y_closeColFilters').html(RC.getYSeriesHtmlData('stock','close',true, true, false, false));
	   				   			
	   			$('#difference_chart_table').css("display","none");
	   			$('#gantt_chart_table').css("display","none");
	   			
	   			$("#stock_y_high").val("");
	   			$("#stock_y_low").val("");
	   			$("#stock_y_open").val("");
	   			$("#stock_y_close").val("");
	   		} else if((RC.chartType=="difference")){
	   			$('#difference_chart_table').find('#chartPRButton').attr("disabled", "disabled");
	   			$('#pie_chart_table').css("display","none");
	   			$('#line_chart_table').css("display","none");
	   			$('#bubble_chart_table').css("display","none");
	   			$('#stock_chart_table').css("display","none");
	   			
	   			$('#difference_chart_table').css("display","");
	   			$('#difference_y_positiveColFilters').html(RC.getYSeriesHtmlData('difference','positive',false, true, false, false));
	   			$('#difference_y_negetiveColFilters').html(RC.getYSeriesHtmlData('difference','negetive',false, true, false, false));

	   			$('#gantt_chart_table').css("display","none");
	   			$("#difference_y_positive").val("");
	   			$("#difference_y_negetive").val("");
	   			
	   		} else if ((RC.chartType=="gantt")){
	   			$('#gantt_chart_table').find('#chartPRButton').attr("disabled", "disabled");
	   			$('#pie_chart_table').css("display","none");
	   			$('#line_chart_table').css("display","none");
	   			$('#bubble_chart_table').css("display","none");
	   			$('#stock_chart_table').css("display","none");
	   			$('#difference_chart_table').css("display","none");
	   			
	   			$('#gantt_chart_table').css("display","");
	   			$('#gantt_y_labelColFilters').html(RC.getYSeriesHtmlData('gantt','label',false, true, true, true));
	   			$('#gantt_y_startColFilters').html(RC.getYSeriesHtmlData('gantt','start',false, false, false, true));
	   			$('#gantt_y_endColFilters').html(RC.getYSeriesHtmlData('gantt','end',false, false, false, true));
	   			$("#gantt_y_label").val("");
	   			$("#gantt_y_start").val("");
	   			$("#gantt_y_end").val("");
	   		} else if ((RC.chartType=="bubble")){
	   			$('#bubble_chart_table').find('#chartPRButton').attr("disabled", "disabled");
	   			$('#pie_chart_table').css("display","none");
	   			$('#line_chart_table').css("display","none");
	   			
	   			$('#bubble_chart_table').css("display","");
	   			$('#bubble_y_valueColFilters').html(RC.getYSeriesHtmlData('bubble','value',true, true, false, true));
	   			$('#bubble_y_sizeColFilters').html(RC.getYSeriesHtmlData('bubble','size',false, true, false, true));
  			
	   			$('#stock_chart_table').css("display","none");
	   			$('#difference_chart_table').css("display","none");
	   			$('#gantt_chart_table').css("display","none");
	   			$("#bubble_y_value").val("");
	   			$("#bubble_y_size").val("");
	   		} else{
	   			
	   			$('#pie_chart_table').css("display","none");
	   			$('#line_chart_table').css("display","none");
	   			$('#bubble_chart_table').css("display","none");
	   			$('#stock_chart_table').css("display","none");
	   			$('#difference_chart_table').css("display","none");
	   			$('#gantt_chart_table').css("display","none");
	   		}
	   	},

	   	deleteChart : function(chartKey){
	   		
	   		var object=DA.queryInfo["chartDetail"];
	   		for(var attr in object){
	   			if(attr==chartKey){
	   				delete DA.queryInfo["chartDetail"][attr];
	   			}
	   		}
	   	},
	   	
	   	setYSeriesHistoryObj : function(prefix,suffix){
	   		var flag = false;
	   		var name=prefix+suffix;
	   		var object=RC.ySeriesHistory[name];
	   		
	   		RC.selectedChartYObject[name]=jQuery.extend(true, {}, RC.ySeriesHistory[name]);
	   		for(var colName in object){
				$('#'+prefix+'_y_'+suffix+colName).attr("checked",true);
				$('#'+prefix+'_y_'+suffix+'_aggregate'+colName).val(object[colName]);
				$('#'+prefix+'_y_'+suffix+'_aggregate'+colName).removeAttr("disabled");
				
			}
			
	   		if(RC.issetTimeOut){
	   			RC.issetTimeOut=false;
	   			setTimeout('RC.setYSeriesHistoryObj()',1000);
	   		}
	   	},
	   	
	   	addCloneChart : function(){
	   		var title = $('#chartTitleClone').val();
	   		for(var key in DA.queryInfo["chartDetail"])
   			{
	   			var temp = DA.queryInfo["chartDetail"][key]["title"];
	   			if(temp == title)
	   			{
	   				jAlert("Title already exists","Error");
	   				$("#popup_container").css("z-index","9999999");
	   				return;
	   			}
   			}
	   		var chartObject =jQuery.extend(true, {}, DA.queryInfo["chartDetail"][DA.selectedChartId]); 
	   			chartObject["title"] = title;
   			var count=0;
   			for(key in DA.queryInfo["chartDetail"]) {
   				count++;
   			}
   			var chartkey="chart"+count;
   			while(DA.queryInfo["chartDetail"].hasOwnProperty(chartkey)){
   				count++;
   				chartkey="chart"+count;
   			}
   			DA.queryInfo["chartDetail"][chartkey]=chartObject;	   			
   			DA.closeCloneBox();
   			DA.currentSelectedChart = title;
   			DA.createChartGrid(DA.queryInfo["chartDetail"]);
	   	},
	   	
	   	fillChartDimension : function(type)
		{
	   		$('#chart_dimension').html('');
	   		$('#chart_dimension').append('<option value="0">2D</option>');
			if(type != "scatter" && type != "stock" && type != "bubble" && type != "difference" && type !=  "gantt" && type != "radar" && type !=  "meter" ) 
			{
				$('#chart_dimension').append('<option value="1" selected="selected">2D with Depth</option>');
				if(type !=  "pie")
					$('#chart_dimension').append('<option value="2">3D</option>');
			}
		},
		seriesChanged : function(){
			
			if ((RC.chartType=="line") || (RC.chartType=="bar") || (RC.chartType=="area") || (RC.chartType=="tube") || (RC.chartType=="cone") || (RC.chartType=="pyramid")){
				
				var val = $('#line_x_series_scale').val();
				if(val== 'Linear'){
					$('#y_scale_min_val_row').show();
				}else{
					$('#y_scale_min_val_row').hide();
				}
			}
		}
};