Formatter = 
{
	currentColumnDataType : null,
	previousColName :'',
		
	closeBox : function()
	{
		Util.removeLightbox("formattingWizard");
	},
	
	loadFormatTypeList : function(flag)
	{
		
		if(flag){
			cssGenerator.formatColumn();
		}else{
			
			var previewCol = "";
			$("#formatTypeSubOptions").html("");
			$("#customFormatHolder").css('display', 'none');
			Formatter.previousColName = $("#availableColumnList").val();
			var colName = $("#availableColumnList").val();
			Formatter.currentColumnDataType = DA.colMap[colName];
			if(Formatter.currentColumnDataType==undefined||colName.indexOf('(')!=-1){
				var validColName = colName.substring(colName.indexOf('(')+1,colName.indexOf(')'));
				Formatter.currentColumnDataType = DA.colMap[validColName];
			}
			var date = new Date();
			var numberFormat = ["Unformatted", "General Number", "Currency", "Fixed", "Percentage", "Scientific", "Custom"];
			var stringFormat = ["Unformatted", "uppercase", "lowercase", "Custom"];
			var dateTimeFormat = [];
			var data = "<select style=\"width: 120px;\" id = 'formatTypeOptions' onchange = 'javascript:Formatter.loadFormatTypeSubOptions(false);'>";
			var options = [];
			if(Formatter.currentColumnDataType.toUpperCase() == 'BIGINT' || Formatter.currentColumnDataType.toUpperCase() == 'INTEGER' || Formatter.currentColumnDataType.toUpperCase().indexOf('INT') != -1 || Formatter.currentColumnDataType.toUpperCase() == 'DECIMAL'  || Formatter.currentColumnDataType.toUpperCase().indexOf('FLOAT') != -1 || Formatter.currentColumnDataType.toUpperCase().indexOf('DOUBLE') != -1)
			{
				options = numberFormat;
				previewCol = "123456";
				
			}
			else if(Formatter.currentColumnDataType.toUpperCase() == 'STRING' || Formatter.currentColumnDataType.toUpperCase() == 'BLOB')
			{
				options = stringFormat;
				previewCol = $("#availableColumnList").val();
			}
			if(Formatter.currentColumnDataType.toUpperCase() == 'TIMESTAMP')
			{
				data += Formatter.loadDateTimeSubOptions();
				previewCol = "03/12/2012";
			}
			else
			{
				for(var i = 0; i < options.length; i++)
				{
					data += "<option value='" + options[i] + "'>" + options[i] + "</option>";
				}
			}
			data += "</select>";
			
			
			$("#formatTypeList").html(data);
			$('#customFormat').val(previewCol);
			$('#formatPreview').val(previewCol);
			Formatter.loadFormatTypeSubOptions(true);
	  }
	},
	
	showSavedHistory : function(){
		
		var colName ='';
		colName = $("#availableColumnList").val();
		var datatype = DA.colMap[colName];
		var formatObj=null;
		if(DA.currentType=="groupHeader"){
			if(DA.queryInfo["groupHeader"][colName]!=undefined&&DA.queryInfo["groupHeader"]!=null){
				if(DA.queryInfo["groupHeader"][colName]["style"]!=undefined&&DA.queryInfo["groupHeader"][colName]!=null)
				{
					if(DA.queryInfo["groupHeader"][colName]["style"]["format"]!=undefined&&DA.queryInfo["groupHeader"][colName]["style"]["format"]!=null)
						formatObj =DA.queryInfo["groupHeader"][colName]["style"]["format"];
				}
			}
		}
		else if(DA.currentType=="groupFooter"){
			if(DA.queryInfo["groupFooter"][colName]!=undefined&&DA.queryInfo["groupFooter"]!=null){
				if(DA.queryInfo["groupFooter"][colName]["style"]!=undefined&&DA.queryInfo["groupFooter"][colName]!=null)
				{
					if(DA.queryInfo["groupFooter"][colName]["style"]["format"]!=undefined||DA.queryInfo["groupFooter"][colName]["style"]["format"]!=null){
						formatObj = DA.queryInfo["groupFooter"][colName]["style"]["format"];
					}
				}
			}
		}
		else if(DA.currentType=="columnHeader"){
			if(DA.queryInfo["colHeaderDetail"][colName]!=undefined&&DA.queryInfo["colHeaderDetail"][colName]!=null)
			{
				if(DA.queryInfo["colHeaderDetail"][colName]["format"]!=undefined&&DA.queryInfo["colHeaderDetail"][colName]["format"]!=null){
					formatObj=DA.queryInfo["colHeaderDetail"][colName]["format"];		
				}
			}	
			
		}
		else if(DA.currentType=="columnDetail"){
			if(DA.queryInfo["colDetail"][colName]!=undefined&&DA.queryInfo["colDetail"][colName]!=null)
			{
				if(DA.queryInfo["colDetail"][colName]["format"]!=undefined&&DA.queryInfo["colDetail"][colName]["format"]!=null){
					formatObj=DA.queryInfo["colDetail"][colName]["format"];
				}
			}
		}
		if(formatObj!=null){
			var sampleData = formatObj["sample"];
			var category=formatObj["category"];
			var pattern = formatObj["pattern"];
			$('#formatTypeOptions').val(category);
			$('#customFormat').val(sampleData);
			$('#formatPreview').val(sampleData);
			if(datatype.toUpperCase()=="BIGINT"||datatype.toUpperCase()=="INTEGER" || datatype.toUpperCase().indexOf('INT') != -1 || datatype.toUpperCase()=="DECIMAL" || datatype.toUpperCase().indexOf('DOUBLE') != -1 || datatype.toUpperCase().indexOf('FLOAT') != -1)
			{
				Formatter.loadNumberSubOptions(category);
				
				if(pattern!=undefined&&pattern!=null){
					 $('#decimalPlaceNumber').val(pattern["decimalPlace"]);
					 $('#roundModeNumber').val(pattern["roundingMode"]);
					 if(pattern["commaSeparator"]){
						 $('#commaSeparatorFlagNumber').attr("checked","checked");
					 }else{
						 $('#commaSeparatorFlagNumber').removeAttr("checked");
					 }
					if(pattern["useSymbolSpace"]){
						$('#useSymbolSpaceFlagNumber').attr("checked","checked");
						$('#useSymbolSpaceFlagNumber').removeAttr("disabled");
					}else{
						$('#useSymbolSpaceFlagNumber').removeAttr("checked");
					}
					$('#symbolNumber').val(	pattern["symbolNumber"]);
					if($('#symbolNumber').val()=="No Symbol"){
						$('#symbolPositionNumber').removeAttr("disabled");
					}
					$('#symbolPositionNumber').val(pattern["symbolPosition"]);
					$('#negativeNumber').val(pattern["negativeNumber"]);
				}
				
			}
			else if(datatype.toUpperCase() == 'STRING' || datatype.toUpperCase() == 'BLOB')
			{
				Formatter.loadStringSubOptions();
			}
			else if(datatype.toUpperCase() == 'TIMESTAMP')
			{
				Formatter.setDatePreview();
				if((category=="General Date"||category=="Long Date"||category=="Medium Date"||category=="Short Date"||category=="Long Time"||category=="Medium Time"||category=="Short Time")){
					
				}else{
					$('#formatTypeOptions').val("custom");
					$('#customFormat').val(formatObj["category"]);
					$('#formatPreview').val(formatObj["category"]);
				}
				
				
			}
		}
		
	},
	
	loadFormatTypeSubOptions : function(loadSavedQury)
	{
		var formatType = $("#formatTypeOptions").val();
		if(Formatter.currentColumnDataType.toUpperCase() == 'BIGINT' || Formatter.currentColumnDataType.toUpperCase() == 'INTEGER' || Formatter.currentColumnDataType.toUpperCase().indexOf('INT') != -1 || Formatter.currentColumnDataType.toUpperCase() == 'DECIMAL' || Formatter.currentColumnDataType.toUpperCase().indexOf('FLOAT') != -1 || Formatter.currentColumnDataType.toUpperCase().indexOf('DOUBLE') != -1)
		{
			$("#formatTypeSubOptions").load("resources/number_formatter.html", function() {
					Formatter.loadNumberSubOptions(formatType);
					$('#myNumber').val("123456");
					$('#formatPreview').val("123456");
					if(loadSavedQury){
						Formatter.showSavedHistory();
					}
					});
		}
		else if(Formatter.currentColumnDataType.toUpperCase() == 'STRING' || Formatter.currentColumnDataType.toUpperCase() == 'BLOB')
		{
			Formatter.loadStringSubOptions(formatType);
			if(loadSavedQury){
				Formatter.showSavedHistory();
			}
		}
		else if(Formatter.currentColumnDataType.toUpperCase() == 'TIMESTAMP')
		{
			Formatter.setDatePreview();
			if(loadSavedQury){
				Formatter.showSavedHistory();
			}
		}
		
	},
	
	setDatePreview : function()
	{
		var value = $("#formatTypeOptions option:selected").text();
		if(value == 'Custom')
		{
			$("#customFormatHolder").css('display', '');
			$("#customFormat").val("mmmm dd,yyyy");
			$("#formatPreview").val("mmmm dd,yyyy");
			
		}
		else
		{
			$("#customFormatHolder").css('display', 'none');
			$("#formatPreview").val(value);
		}
	},
	
	loadDateTimeSubOptions : function()
	{
		var dateFormat = ["General Date", "Long Date", "Medium Date", "Short Date", "Long Time", "Medium Time", "Short Time", "custom"];
		
		var data = "";
		var date = new Date();
		var time = Formatter.getTime12Hour(date);
		data += '<option value = "General Date">' + (new Date()).format('mmmm dd, yyyy hh:nn:ss a/p')+'</option>';
		data += '<option value = "Long Date">' + (new Date()).format('mmmm dd, yyyy') + '</option>';
		data +='<option value="Medium Date">'+(new Date()).format('mmm dd, yyyy') +'</option>'
		data += '<option value = "Short Date">' + (new Date()).format('mm/dd/yy') + '</option>';
		data += '<option value = "Long Time">' + (new Date()).format('hh:nn:ss a/p') + 'GMT+5.30</option>';
		data += '<option value = "Medium Time">' +  (new Date()).format('hh:nn:ss a/p')  + '</option>';
		time = date.getHours()+":" + date.getMinutes();
		data += '<option value = "Short Time">' + time + '</option>';
		data += '<option value = "custom">Custom</option>';
		
		return data;
		
	},
	
	getTime12Hour : function(date)
	{
		var time = "";
		var hours = date.getHours();
		if(hours > 12)
		{
			time += (hours - 12) + ":";
		}
		else
		{
			time += hours + ":";
		}
		time += date.getMinutes() + ":";
		time += date.getSeconds() + " ";
		if(hours > 12)
		{
			time += "PM";
		}
		else
		{
			time += "AM";
		}
		return time;
	},
	
	loadStringSubOptions : function()
	{
		$("#customFormatHolder").css('display', '');
		var previewString = $("#customFormat").val();
		if(previewString == '')
		{
			previewString = "My String";
		}
		
//		var previewString = 'My String';
		var value = $("#formatTypeOptions").val();
		if(value == 'Unformatted')
		{
			$("#formatPreview").val(previewString);
		}
		else if(value == 'uppercase')
		{
			$("#formatPreview").val(previewString.toUpperCase());
		}
		else if(value == 'lowercase')
		{
			$("#formatPreview").val(previewString.toLowerCase());
		}else{
			$("#formatPreview").val(previewString);
		}
		
//		if(value == 'Custom')
//		{
//			$("#customFormatHolder").css('display', '');
//		}
//		else
//		{
//			$("#customFormatHolder").css('display', 'none');
//		}
		
	},
	
	loadNumberSubOptions : function(formatType)
	{
		Formatter.loadNumberOptions();
		
		if(formatType == 'Unformatted' || formatType == 'General Number')
		{
			$("#subOptionRow").css('display', 'none');
		}
		else
		{
			$("#subOptionRow").css('display', '');
		}
		if(formatType == 'Currency')
		{
			Formatter.loadNumberCurrencyOptions();
		}
		else if(formatType == 'Fixed')
		{
			Formatter.loadNumberFixedOptions();
		}
		else if(formatType == 'Percentage')
		{
			Formatter.loadNumberPercentageOptions();
		}
		else if(formatType == 'Scientific')
		{
			Formatter.loadNumberScientificOptions();
		}
		
		if(formatType == 'Custom')
		{
			$("#customFormatHolder").css('display', '');
		}
		else
		{
			$("#customFormatHolder").css('display', 'none');
		}
		
		Formatter.previewNumber();
			
	},
	
	loadNumberOptions : function()
	{
		
		var decimalPlaces = [0,1,2,3,4,5,6,7,8,9,10];
		var data = "";
		for(var i = 0; i < decimalPlaces.length; i++)
		{
			if(decimalPlaces[i] == 2)
			{
				data += "<option value='" + decimalPlaces[i] + "' selected='selected'>" + decimalPlaces[i] + "</option>";
			}
			else
			{
				data += "<option value='" + decimalPlaces[i] + "'>" + decimalPlaces[i] + "</option>";
			}
		}
		
		$("#decimalPlaceNumber").html(data);
		
		var roundModes = ["Half Up", "Half Down", "Half Even", "Up", "Down", "Ceiling", "Floor", "Unnecessary"];
		var roundModesVal = ["Half_Up", "Half_Down", "Half_Even", "Up", "Down", "Ceiling", "Floor", "Unnecessary"];
		data = "";
		for(var i = 0; i < roundModes.length; i++)
		{
			data += "<option value='" + roundModesVal[i] + "'>" + roundModes[i] + "</option>"; 
		}
		$("#roundModeNumber").html(data);
		
//		var currencySymbol = ["No Symbol","´", "$", "Û", "£", "DKK"];
		var currencySymbol = ["No Symbol", "&#165;","&#36;", "&#8364;", "&#163;", "DKK"];
		
		data = "";
		for(var i = 0; i < currencySymbol.length; i++)
		{
			data += "<option value='" + currencySymbol[i] + "'>" + currencySymbol[i] + "</option>"; 
		}
		$("#symbolNumber").html(data);
		$("#customFormatHolder").css('display', '');
	},
	
	loadNumberCurrencyOptions : function()
	{
		var optionsToBeDisplayed = ["decimalHolder", "roundModeHolder", "commaSeparatorFlagHolder", "useSymbolSpaceFlagHolder", "symbolHolder", "symbolPositionHolder", "negativeNumberHolder"];
		Formatter.showOptions(optionsToBeDisplayed);
		
	},
	
	loadNumberFixedOptions : function()
	{
		var optionsToBeDisplayed = ["decimalHolder", "roundModeHolder", "commaSeparatorFlagHolder", "negativeNumberHolder"];
		Formatter.showOptions(optionsToBeDisplayed);
	},
	
	loadNumberPercentageOptions : function()
	{
		var optionsToBeDisplayed = ["decimalHolder", "roundModeHolder", "commaSeparatorFlagHolder", "symbolPositionHolder", "negativeNumberHolder"];
		Formatter.showOptions(optionsToBeDisplayed);
		$("#symbolPositionNumber").removeAttr("disabled");
	},
	
	loadNumberScientificOptions : function()
	{
		var optionsToBeDisplayed = ["decimalHolder", "roundModeHolder"];
		Formatter.showOptions(optionsToBeDisplayed);
	},
	
	showOptions : function(optionsToBeDisplayed)
	{
		Formatter.resetNumberSubOptions();
		var optionsToBeHide = ["decimalHolder", "roundModeHolder", "commaSeparatorFlagHolder", "useSymbolSpaceFlagHolder", "symbolHolder", "symbolPositionHolder", "negativeNumberHolder"];
		
		for(var i = 0; i < optionsToBeHide.length; i++)
		{
			$("#" + optionsToBeHide[i]).css('display', 'none');
		}
		
		for(var i = 0; i < optionsToBeDisplayed.length; i++)
		{
			$("#" + optionsToBeDisplayed[i]).css('display', '');
		}
	},
	
	resetNumberSubOptions : function()
	{
		$("#commaSeparatorFlagNumber").attr('checked', false);
		$("#useSymbolSpaceFlagNumber").attr('checked', false);
		$("#decimalPlaceNumber").val('2');
		$("#roundModeNumber").val("Half up");
		$("#symbolNumber").val("No Symbol");
		$("#symbolPositionNumber").val("before");
		$("#negativeNumber").val("useHyphen");
		$("#formatPreview").val("");
		
	},
	
	symbolHandlerNumber : function()
	{
		if($("#symbolNumber").val() == 'No Symbol')
		{
			$("#useSymbolSpaceFlagNumber").attr('disabled', 'disabled');
			$("#symbolPositionNumber").attr('disabled', 'disabled');
		}
		else
		{
			$("#useSymbolSpaceFlagNumber").removeAttr('disabled');
			$("#symbolPositionNumber").removeAttr('disabled');
		}
		
		Formatter.previewNumber();
	},
	
	
	previewNumber : function()
	{
		var isUnexpected = false;
		var number = "123.45";
		if($("#myNumber").val() == ''||$("#myNumber").val() == undefined)
		{
			number = "123.45";
		}
		else
		{
			number = $("#myNumber").val();
		}
		
//		number = "1237.45";
		var decimalPart = number.split(".");
		var decimal;
		if(decimalPart.length > 2)
		{
			isUnexpected = true;
		}
		if(decimalPart.length == 2 && decimalPart[1] == '')
		{
			isUnexpected = true;
		}
		
		if(decimalPart.length == 1)
		{
			number += number + ".";
			decimal = "";
		}
		else
		{
			decimal = decimalPart[1];
		}
		
		
		
		
		var decimalPlace = parseInt($("#decimalPlaceNumber").val());
		
		var count = 0;
		while(true)
		{
			if(decimalPlace > decimal.length + count)
			{
				number += "0";
				count ++;
			}
			else
			{
				break;
			}
		}
		
		count = 0;
		while(true)
		{
			if(decimalPlace < decimal.length - count)
			{
				number = number.substring(0, number.length - 1);
				count ++;
			}
			else
			{
				break;
			}
		}
	
		var comma = decimalPart[0];
		var tmpComma;
		var newComma;
		if(comma.length > 3 && $("#commaSeparatorFlagNumber").attr('checked'))
		{
			tmpComma = comma;
			newComma = tmpComma.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		}
		else
		{
			newComma = number;
		}
		
		if(decimalPlace == 0)
		{
			number = comma;
		}
		else
		{
			if(comma.length > 3)
			{
				comma = newComma;
			}
			else
			{
				comma = comma + "." + number.split(".")[1]
			}
			
			number = comma;
		}
		
		if($("#formatTypeOptions").val() == 'Percentage')
		{
			if($("#symbolPositionNumber").val() == 'before')
			{
				
				number = '%' + number;
			}
			else
			{
				number = number + '%';
			}
		}
		else if(!$("#symbolPositionNumber").attr('disabled'))
		{
			var space = "";
			if($("#useSymbolSpaceFlagNumber").attr('checked'))
			{
				space = " ";
			}
			
			if($("#symbolPositionNumber").val() == 'before')
			{
				
				number = $("#symbolNumber").val() + space + number;
			}
			else
			{
				number = number + space + $("#symbolNumber").val();
			}
		}
		if(isUnexpected == true)
		{
			$("#formatPreview").val('unexpected');
		}
		else
		{
			$("#formatPreview").val(number);
		}
	},
	
  	createFromatterWizard : function(columnName, type, columnDataType)
   	{
  		DA.currentType = type;
   		Formatter.currentColumnDataType = columnDataType;
   		Util.addLightbox("formatterWizard", "resources/css_generator_wizard.html", null, null);
   	},
   	
	loadColumnList : function()
	{
		$("#formatTypeSubOptions").html("");
   		var list='';
   		if(DA.currentType == 'groupHeader')
   		{
   			list = DA.selectedGroupHeaderArray;
   		}
   		else if(DA.currentType == 'groupFooter')
   		{
   			list = DA.selectedGroupFooterArray;
   		}
   		else if(DA.searchColumn[0]=='*'&&DA.searchColumn.length==1){
   			 list = DA.colList;	
   		}
   		else
   		{
   			list=DA.searchColumn;
   		}
   		
   		var data = "<select style=\"width: 120px;\" id = 'availableColumnList' onchange='javascript:Formatter.loadFormatTypeList(true);'>";
   		for(var i = 0; i < list.length; i++)
   		{
   			if(i == 0)
   				cssGenerator.currentColumn = list[i];
   			data += "<option value='" + list[i] +"'>" + list[i] + "</option>";
   		}
   		data += "</select>";
   		
   		cssGenerator.prevColumn = cssGenerator.currentColumn;
   		$("#columnList").html(data);
   		Formatter.previousColName = cssGenerator.currentColumn;
	}
};