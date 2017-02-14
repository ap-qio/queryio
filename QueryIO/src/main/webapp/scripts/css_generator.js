cssGenerator = {
	
	userDefinedCSS : null,	
	fontSizeOptionsArray : ["xx-small", "x-small", "small", "medium", "large", "x-large", "xx-large", "smaller", "larger", "custom"],
	currentColumn : null,
	prevColumn : null,
	
	tabLevel : 0,
	
	ready : function()
	{
		var flag = true;
		if(TM.currentType == 'groupHeader' || TM.currentType == 'groupFooter')
		{
			cssGenerator.loadGroupPrefixSuffixWizard();
		}
		else
		{
//			cssGenerator.initializeCSSGenerator();
			cssGenerator.loadCSSStyleGeneratorPage();
			$("#backViewCSSGenerator").css('display', 'none');
//			$("#nextViewCSSGenerator").css('display', 'none');
		}
		
		
		if(TM.currentType == 'groupHeader')
		{
			$("#headerspan").text('Group Header');
			
		}
		else if(TM.currentType == 'groupFooter')
		{
			$("#headerspan").text('Group Footer');
		}
		else if(TM.currentType == 'columnDetail')
		{
			$("#headerspan").text('Column Detail');
			
		}
		else if(TM.currentType == 'columnHeader')
		{
			$("#headerspan").text('Column Header');
			$("#nextViewCSSGenerator").css("display","none");
		}
		else if(TM.currentType == 'queryHeader')
		{
			$("#headerspan").text('Report Header');
			$("#nextViewCSSGenerator").css("display","none");
		}
		else if(TM.currentType == 'queryFooter')
		{
			$("#headerspan").text('Report Footer');
			$("#nextViewCSSGenerator").css("display","none");
			
			
			
		}
		
		
	},
	
	enablePrefixSuffixTextBox : function(type, colName)
	{
		$('#' + type + '_prefix'+colName).removeAttr('disabled');
		$('#' + type + '_suffix'+colName).removeAttr('disabled');
	},
	
	loadDefaultSelectedColumns : function()
	{
		var prefixName;
		var list=new Array();
		if(TM.currentType == 'groupHeader')
		{
			prefixName = "group_header";
			for(var attr in TM.queryInfo["groupHeader"]){
				list.push(attr);
			}
			
			 TM.selectedGroupHeaderArray=list;
		}
		else if(TM.currentType == 'groupFooter')
		{
			prefixName = "group_footer";
			
			for(var attr in TM.queryInfo["groupFooter"]){
				list.push(attr);
			}
			 TM.selectedGroupFooterArray=list;
		}
		
		
		
		
		for(var i = 0; i < list.length; i++)
		{
			var columnName = TM.currentType + list[i];
			$("#" + columnName).attr('checked',true);
			if(TM.currentType == 'groupHeader')
			{
				cssGenerator.enablePrefixSuffixTextBox("group_header", list[i]);
			}
			else if(TM.currentType == 'groupFooter')
			{
				cssGenerator.enablePrefixSuffixTextBox("group_footer", list[i]);
			}
			
			columnName = "#" + prefixName + "_prefix" + list[i];
			$(columnName).val(TM.queryInfo[TM.currentType][list[i]]["prefix"]);
			columnName = "#" + prefixName + "_suffix" + list[i];
			$(columnName).val(TM.queryInfo[TM.currentType][list[i]]["suffix"]);
			if(TM.currentType=='groupFooter'){
				$("#footer_" + list[i]).val(TM.queryInfo[TM.currentType][list[i]]["function"]);
				$("#footer_" + list[i]).removeAttr("disabled");
			}
			
		}
		
		if(TM.currentType == 'groupHeader')
		{
			
			if(TM.selectedGroupHeaderArray.length == 0)
			{
				$("#nextViewCSSGenerator").attr('disabled', 'disabled');
			}
			else
			{
				$("#nextViewCSSGenerator").removeAttr('disabled');
			}
		}
		else if(TM.currentType == 'groupFooter')
		{
			if(TM.selectedGroupFooterArray.length == 0)
			{
				$("#nextViewCSSGenerator").attr('disabled', 'disabled');
			}
			else
			{
				$("#nextViewCSSGenerator").removeAttr('disabled');
			}
		}
		
	},
	
	loadCSSStyleGeneratorPage : function()
	{
		if(TM.currentType == 'groupHeader' || TM.currentType == 'groupFooter'){
			cssGenerator.generateJSONOfCSS();
		}
		
		if(TM.currentType == 'columnHeader' || TM.currentType == 'columnDetail')
			cssGenerator.generateJSONOfCSS();
		
		if(cssGenerator.tabLevel == 0)
		{
			$("#dataLoadArea").load('resources/css_style_attributes.html', function(){
				cssGenerator.initializeCSSGenerator();
				if(TM.currentType == 'queryHeader' || TM.currentType == 'queryFooter'){
					$('#title_row').css("display","");
					$('#column_list_row').css("display","none");
				}
				if(TM.currentType == 'columnHeader'){
					$('#title_row').css("display","");
					$('#width_row').css("display","");
				} 
				
			});
			
			
			$("#backViewCSSGenerator").css('display', '');
			$("#nextViewCSSGenerator").css('display', '');
			$("#doneButton").css('display', 'none');
			
			cssGenerator.tabLevel = 1;
			
		}
		else if(cssGenerator.tabLevel == 1)
		{
			cssGenerator.generateJSONOfCSS();
			$("#backViewCSSGenerator").css('display', '');
			$("#nextViewCSSGenerator").css('display', 'none');
			$("#doneButton").css('display', '');
			
			
			
			$("#dataLoadArea").load('resources/formatter_wizard.html', function(){
				Formatter.loadColumnList();
				Formatter.loadFormatTypeList(false);
				if(TM.currentType == 'queryHeader' || TM.currentType == 'queryFooter'){
					$('#title_row').css("display","");
					$('#column_list_row').css("display","none");
				}
				
			});
			cssGenerator.tabLevel = 2;
			
		}

		cssGenerator.currentColumn=$('#availableColumnList').val();	
	},
	
	initializeCSSGenerator : function()
	{
		if(TM.currentType == 'columnDetail')
		{
			$("#checkBookStyleTD").css('display', '');
		}
		cssGenerator.loadColumnList();
		cssGenerator.getFontSizeOptions();
		cssGenerator.fontFamily();
		cssGenerator.fillAtStartup();
		cssGenerator.bindJPicker();
		
	},
	
	getFontSizeOptions : function()
	{
		var data = "<select style=\"width: 100%;\" id = 'fontSizeOption' onchange='javascript:cssGenerator.enableCustomFontSize();' >";
		
		var fontSizeOptionsArray = cssGenerator.fontSizeOptionsArray;
	
		for(var i = 0; i < fontSizeOptionsArray.length; i++)
		{
			data += "<option value='" + fontSizeOptionsArray[i] + "'>" + fontSizeOptionsArray[i] + "</option>";  
		}
  
   		data += '</select>';
   		$("#fontSizeSection").html(data);
   		
	},

	fontFamily : function()
	{
		var fontFamilyArray = ["Arial", "Times New Roman", "Verdana", "Courier"];
		var data = "<select style=\"width: 100%;\" id = 'fontFamilyOption'>";
	
		for(var i = 0; i < fontFamilyArray.length; i++)
		{
			data += "<option value='" + fontFamilyArray[i] + "'>" + fontFamilyArray[i] + "</option>";  
		}
		data += '</select>';
		$("#fontFamily").html(data);
	},
	
	closeBox : function(){
//		var el = document.getElementsByTagName("iframe")[0];
//		el.parentNode.removeChild(el);
		$('.jPicker').css('display', 'none');
		Util.removeLightbox("cssGeneratorWizard");
	},
	
	revertBack : function()
	{
		var type = TM.currentType;
		if(type == "columnHeader")
			TM.queryInfo["colHeaderDetail"] = jQuery.extend(true, {}, TM.tempData);
		else if(type == "columnDetail")
			TM.queryInfo["colDetail"] = jQuery.extend(true, {}, TM.tempData);
		else
			TM.queryInfo[type] = jQuery.extend(true, {}, TM.tempData);
		
	},
	
	finishStyling : function()
	{
		if(TM.currentType == 'groupHeader'||TM.currentType == 'groupFooter'||TM.currentType == 'columnHeader'||TM.currentType == 'columnDetail'){
			
			if(cssGenerator.tabLevel==1){
				cssGenerator.generateJSONOfCSS();
			}else if(cssGenerator.tabLevel==2){
				cssGenerator.formatColumn();
			}
		}else{
			cssGenerator.generateJSONOfCSS();
		}
			cssGenerator.closeBox();
		
	},
	
	generateJSONOfCSS : function()
	{

		if(cssGenerator.currentColumn != null)
		{
			if(TM.currentType == 'columnHeader')
			{
				cssGenerator.columnHeaderCSSHandler(cssGenerator.currentColumn);
			}
			else if(TM.currentType == 'groupHeader')
			{
				cssGenerator.groupHeaderCSSHandler(cssGenerator.currentColumn);
			}
			else if(TM.currentType == 'columnDetail')
			{
				cssGenerator.columnDetailCSSHandler(cssGenerator.currentColumn);
			}
			else if(TM.currentType == 'groupFooter')
			{
				cssGenerator.groupFooterCSSHandler(cssGenerator.currentColumn);		
			}
			else if(TM.currentType == 'queryHeader')
			{
				//Set Query/Report Header
				cssGenerator.setReportHeaderCSS();		
			}
			else if(TM.currentType == 'queryFooter')
			{
				//Set Query/Report Footer
				cssGenerator.setReportFooterCSS();		
			}
		}
			
		
	},
	setReportFooterCSS : function()
	{
		
		TM.queryInfo["queryFooter"] = new Object();
		TM.queryInfo["queryFooter"]["footer"] = new Object();
		TM.queryInfo["queryFooter"]["footer"] =cssGenerator.addCSSValues();
		TM.queryInfo["queryFooter"]["footer"]["title"]=$('#header_title').val();
		
		
	},
	setReportHeaderCSS : function()
	{
		
		TM.queryInfo["queryHeader"] = new Object();
		TM.queryInfo["queryHeader"]["header"] = new Object();
		TM.queryInfo["queryHeader"]["header"]=cssGenerator.addCSSValues();
		TM.queryInfo["queryHeader"]["header"]["title"]=$('#header_title').val();
		
		
	},
	clearFields : function()
	{
		var tmp = $("#availableColumnList").val();
		$("input[type='text']").val("");
		$("#fontSize").val("10");
		$('#hostDiv select').prop('selectedIndex',0);
		$("#availableColumnList").val(tmp);
		cssGenerator.unbindJPicker();
		
	},
	
	fillAvailableProperties : function()
	{
		cssGenerator.prevColumn=cssGenerator.currentColumn;
		cssGenerator.generateJSONOfCSS();
		
		cssGenerator.currentColumn = $("#availableColumnList").val();
		cssGenerator.clearFields();
		cssGenerator.fillAtStartup();
		
		cssGenerator.currentColumn = $("#availableColumnList").val();
		cssGenerator.prevColumn = cssGenerator.currentColumn;
		cssGenerator.bindJPicker();
	},
	
	fillAtStartup : function()
	{
		if(TM.currentType == 'columnHeader')
		{
			
			cssGenerator.fillProperties(TM.queryInfo["colHeaderDetail"][cssGenerator.currentColumn]);
			var flag = true;
			if(TM.queryInfo["colHeaderDetail"][cssGenerator.currentColumn]!=undefined&&TM.queryInfo["colHeaderDetail"][cssGenerator.currentColumn]!=null){
				if(TM.queryInfo["colHeaderDetail"][cssGenerator.currentColumn].hasOwnProperty("title")){
					$('#header_title').val(TM.queryInfo["colHeaderDetail"][cssGenerator.currentColumn]["title"]);
					flag=false;
				}
			}
			if(flag)
			$('#header_title').val(cssGenerator.currentColumn);
		}
		if(TM.currentType == 'queryHeader')
		{
			
			if(TM.queryInfo["queryHeader"].hasOwnProperty("header")){
				cssGenerator.fillProperties(TM.queryInfo["queryHeader"]["header"]);
				if(TM.queryInfo["queryHeader"]["header"].hasOwnProperty("title")){
					$('#header_title').val(TM.queryInfo["queryHeader"]["header"]["title"]);
				}
			}

		}
		if(TM.currentType == 'queryFooter')
		{
			
			if(TM.queryInfo["queryFooter"].hasOwnProperty("footer")){
				cssGenerator.fillProperties(TM.queryInfo["queryFooter"]["footer"]);
				if(TM.queryInfo["queryFooter"]["footer"].hasOwnProperty("title")){
					
					$('#header_title').val(TM.queryInfo["queryFooter"]["footer"]["title"]);
				}
			}	
			
		}
		else if(TM.currentType == 'groupHeader')
		{
			if(TM.queryInfo["groupHeader"][cssGenerator.currentColumn] != undefined)
			{
				cssGenerator.fillProperties(TM.queryInfo["groupHeader"][cssGenerator.currentColumn]["style"]);
			}else{
				$('#header_title').val(cssGenerator.currentColumn);
			}
				
		}
		else if(TM.currentType == 'columnDetail')
		{
			cssGenerator.fillProperties(TM.queryInfo["colDetail"][cssGenerator.currentColumn]);
		}
		else if(TM.currentType == 'groupFooter')
		{
			if(TM.queryInfo["groupFooter"][cssGenerator.currentColumn] != undefined )
			{
				cssGenerator.fillProperties(TM.queryInfo["groupFooter"][cssGenerator.currentColumn]["style"]);	
			}
		}
	},
	
	fillProperties : function(obj)
	{
		if(obj == undefined || obj["background-color"] == undefined)
		{
			cssGenerator.fillDefaultProperties();
		}
		else
		{
			if(obj["font-size"] != undefined)
			{
				if($.inArray(obj["font-size"], cssGenerator.fontSizeOptionsArray) == -1)
				{
					$("#fontSize").val(obj["font-size"].substring(0,obj["font-size"].length - 2));
					$("#fontSizeUnit").val(obj["font-size"].substring(obj["font-size"].length - 2));
					$("#fontSizeOption").val('custom');
					cssGenerator.enableCustomFontSize();
				}
				else
				{
					$("#fontSizeOption").val(obj["font-size"]);
				}
			}
			if(obj["width"] != undefined){
				var widthVal=obj["width"]+'';
				$("#column_detail_width").val(widthVal.substring(0,widthVal.length - 2));
				$("#column_detail_width_unit").val(widthVal.substring(widthVal.length - 2));
			}
			if(obj["font-family"] != undefined)
				$("#fontFamilyOption").val(obj["font-family"]);
			if(obj["color"] != undefined)
				$("#fontColor").val(obj["color"]);
			if(obj["background-color"] != undefined)
				$("#backgroundColor").val(obj["background-color"]);
			if(obj["text-align"] != undefined)
				$("#fontTextAlign").val(obj["text-align"]);
			if(obj["font-weight"] != undefined)
				$("#fontWeight").val(obj["font-weight"]);
			if(obj["font-style"] != undefined)
				$("#fontstyle").val(obj["font-style"]);
		}
	},
	
	fillDefaultProperties : function()
	{
		var cssObj = new Object();
		if(TM.currentType == 'columnDetail')
		{
			cssObj["width"] = "50px";
			cssObj["background-color"] = "";
			cssObj["color"] = "#000000";
			cssObj["font-family"] = "Arial";
			cssObj["font-size"] = "10px";
			cssObj["font-style"] = "normal";
			cssObj["font-weight"] = "normal";
			cssGenerator.fillProperties(cssObj);

		}
		else if(TM.currentType == 'columnHeader')
		{
		
			cssObj["width"] = "50px";
			cssObj["background-color"] = "#cccccc";
			cssObj["color"] = "#000000";
			cssObj["font-family"] = "Arial";
			cssObj["font-size"] = "12px";
			cssObj["font-style"] = "normal";
			cssObj["font-weight"] = "normal";
			cssObj["text-align"] = "center";
			cssGenerator.fillProperties(cssObj);
			
			
		}
		else if(TM.currentType == 'queryHeader')
		{
			cssObj["width"] = "100%";
			cssObj["background-color"] = "69abdb";
			cssObj["color"] = "#7c7c7c";
			cssObj["font-family"] = "Arial";
			cssObj["font-size"] = "16px";
			cssObj["font-style"] = "normal";
			cssObj["font-weight"] = "bold";
			cssObj["text-align"] = "center";
			cssGenerator.fillProperties(cssObj);
		}
		else if(TM.currentType == 'queryFooter')
		{
			cssObj["width"] = "100%";
			cssObj["background-color"] = "69abdb";
			cssObj["color"] = "#7c7c7c";
			cssObj["font-family"] = "Arial";
			cssObj["font-size"] = "16px";
			cssObj["font-style"] = "normal";
			cssObj["font-weight"] = "normal";
			cssObj["text-align"] = "center";
			cssGenerator.fillProperties(cssObj);
		}
		
	},
	
	fillDefaultPropertiesColumnDetail : function()
	{
		
	},
	
	groupHeaderCSSHandler : function(colName)
	{
		if(TM.queryInfo["groupHeader"]==undefined||TM.queryInfo["groupHeader"]==null||TM.queryInfo["groupHeader"]=="")
		{
   			TM.queryInfo["groupHeader"]=new Object();
   		}
		if(TM.queryInfo["groupHeader"][colName]==undefined||TM.queryInfo["groupHeader"][colName]==null || TM.queryInfo["groupHeader"][colName]=="")
		{
			TM.queryInfo["groupHeader"][colName]=new Object();
			TM.queryInfo["groupHeader"][colName]["prefix"] = "";
   			TM.queryInfo["groupHeader"][colName]["function"] = "";
   			TM.queryInfo["groupHeader"][colName]["suffix"]= "";
   			TM.queryInfo["groupHeader"][colName]["style"]=new Object();
		}
		if(TM.queryInfo["groupHeader"][colName]["style"]==undefined||TM.queryInfo["groupHeader"][colName]==null)
		{
			TM.queryInfo["groupHeader"][colName]["style"]=new Object();
		}
		
		var format =cssGenerator.getDefaultFormatObject(colName);
		if(TM.queryInfo["groupHeader"][colName]["style"]!=undefined&&TM.queryInfo["groupHeader"][colName]["style"]!=null){
			if(TM.queryInfo["groupHeader"][colName]["style"]["format"]!=undefined&&TM.queryInfo["groupHeader"][colName]["style"]["format"]!=null){
				format=TM.queryInfo["groupHeader"][colName]["style"]["format"];
			}
		}
		
		TM.queryInfo["groupHeader"][colName]["style"] = cssGenerator.addCSSValues();
		TM.queryInfo["groupHeader"][colName]["style"]["format"]=format;
		
	},
	
	groupFooterCSSHandler : function(colName)
	{
		if(TM.queryInfo["groupFooter"]==undefined||TM.queryInfo["groupFooter"]==null||TM.queryInfo["groupFooter"]=="")
		{
   			TM.queryInfo["groupFooter"]=new Object();
   		}
		if(TM.queryInfo["groupFooter"][colName]==undefined||TM.queryInfo["groupFooter"][colName]==null || TM.queryInfo["groupFooter"][colName]== "")
		{
			TM.queryInfo["groupFooter"][colName]=new Object();
			TM.queryInfo["groupFooter"][colName]["function"]="";
   			TM.queryInfo["groupFooter"][colName]["prefix"]="";
   			TM.queryInfo["groupFooter"][colName]["suffix"]="";
   			TM.queryInfo["groupFooter"][colName]["style"]=new Object();
		}
		if(TM.queryInfo["groupFooter"][colName]["style"]==undefined||TM.queryInfo["groupFooter"][colName]["style"]==null)
		{
			TM.queryInfo["groupFooter"][colName]["style"]=new Object();
		}
		var format =cssGenerator.getDefaultFormatObject(colName);
		if(TM.queryInfo["groupFooter"][colName]["style"]!=undefined&&TM.queryInfo["groupFooter"][colName]["style"]!=null){
			if(TM.queryInfo["groupFooter"][colName]["style"]["format"]!=undefined&&TM.queryInfo["groupFooter"][colName]["style"]["format"]!=null){
				format=TM.queryInfo["groupFooter"][colName]["style"]["format"];
			}
		}
		TM.queryInfo["groupFooter"][colName]["style"]=cssGenerator.addCSSValues();
		TM.queryInfo["groupFooter"][colName]["style"]["format"]=format;
	},
	getDefaultFormatObject: function(colName){
		var obj = new Object();
		obj["category"]="Unformatted";
		obj["pattern"]=new Object();
		obj["pattern"]["decimalPlace"]="-1";
		obj["pattern"]["roundingMode"]="-";
		obj["pattern"]["commaSeparator"]=false;
		obj["pattern"]["useSymbolSpace"]=false;
		obj["pattern"]["symbolPosition"]="-"
		obj["pattern"]["symbolNumber"]="-"
		obj["pattern"]["negativeNumber"]="-"
		obj["sample"]=colName;
		
		var datatype = TM.colMap[colName];
		if(datatype.toUpperCase()=="STRING" || datatype.toUpperCase() == 'BLOB'){
			datatype="string";
		}else if(datatype.toUpperCase()=="LONG"||datatype.toUpperCase()=="INTEGER"){
			datatype="number"
		}else if(datatype.toUpperCase()=="TIMESTAMP"){
			datatype="datetime";
		}
		obj["type"]=datatype;
		return obj;
	},
	columnHeaderCSSHandler : function(colName)
	{
		cssGenerator.userDefinedCSS = "";
  		if(TM.queryInfo["colHeaderDetail"] == undefined || TM.queryInfo["colHeaderDetail"] == null)
   		{
   			TM.queryInfo["colHeaderDetail"]=new Object();
   		}
  		if(TM.queryInfo["colHeaderDetail"][colName]==undefined||TM.queryInfo["colHeaderDetail"][colName]==null || TM.queryInfo["colHeaderDetail"][colName] == "")
  		{
			TM.queryInfo["colHeaderDetail"][colName]=new Object();
		}
  		var title="";
  		title=$('#header_title').val();
//  		if(TM.queryInfo["colHeaderDetail"][colName].hasOwnProperty("title")){
//  			title=TM.queryInfo["colHeaderDetail"][colName]["title"];
//  		}
  		TM.queryInfo["colHeaderDetail"][colName]=cssGenerator.addCSSValues();
//  		if(title!=""){
  			TM.queryInfo["colHeaderDetail"][colName]["title"]=title;
//  		}
  		$('#column_header_col').val(JSON.stringify(TM.queryInfo["colHeaderDetail"]));
	},
	
	columnDetailCSSHandler : function(colName)
	{
		if(TM.queryInfo["colDetail"] == undefined || TM.queryInfo["colDetail"] == null)
   		{
   			TM.queryInfo["colDetail"]=new Object();
   		}
		if(TM.queryInfo["colDetail"][colName]==undefined||TM.queryInfo["colDetail"][colName]==null || TM.queryInfo["colDetail"][colName]=="")
		{
			TM.queryInfo["colDetail"][colName]=new Object();
		}
		TM.queryInfo["colDetail"]["checkBookFlag"] = $("#checkBookStyleFlag").is(':checked');
		var format =cssGenerator.getDefaultFormatObject(colName);
		if(TM.queryInfo["colDetail"][colName]!=undefined&&TM.queryInfo["colDetail"][colName]!=null){
			if(TM.queryInfo["colDetail"][colName]["format"]!=undefined&&TM.queryInfo["colDetail"][colName]["format"]!=null){
				format=TM.queryInfo["colDetail"][colName]["format"];
			}
		}
		TM.queryInfo["colDetail"][colName]=cssGenerator.addCSSValues();
		$('#column_detail_col').val(JSON.stringify(TM.queryInfo["colDetail"]));
		TM.queryInfo["colDetail"][colName]["format"]=format;
		TM.queryInfo["colDetail"][colName]["width"]=$('#column_detail_width').val() + $('#column_detail_width_unit').val();
		
	},
	
	addCSSValues : function()
	{
		var obj = new Object();
		var value = $("#fontSizeOption").val();
		var fontSize = "";
		if(value == 'custom')
		{
			fontSize = $("#fontSize").val() + $("#fontSizeUnit").val();
		}
		else
		{
			fontSize = $("#fontSizeOption").val();
		}
		obj["font-size"] = fontSize;
		obj["width"] = $("#colWidth").val() + $("#colwidthUnit").val();
		if(TM.currentType == 'queryHeader' || TM.currentType == 'queryFooter'){
			obj["width"] = "100%";
			
		}
		obj["font-family"] = $("#fontFamilyOption").val();
		obj["color"] = $("#fontColor").val();
		obj["background-color"] = $("#backgroundColor").val();
		obj["text-align"] = $("#fontTextAlign").val();
		obj["font-weight"] = $("#fontWeight").val();
		obj["font-style"] = $("#fontstyle").val();
		obj["format"]=new Object();
		return obj;
	},
	
	enableCustomFontSize : function()
	{
		var value = $("#fontSizeOption").val();
		if(value == 'xx-small')
		{
			$("#fontSize").val(6);
		}
		else if(value == 'x-small')
		{
			$("#fontSize").val(8);
		}
		else if(value == 'small')
		{
			$("#fontSize").val(10);
		}
		else if(value == 'medium')
		{
			$("#fontSize").val(12);
		}
		else if(value == 'large')
		{
			$("#fontSize").val(14);
		}
		else if(value == 'x-large')
		{
			$("#fontSize").val(18);
		}
		else if(value == 'xx-large')
		{
			$("#fontSize").val(24);
		}
		else if(value == 'smaller')
		{
			$("#fontSize").val(10);
		}
		else if(value == 'larger')
		{
			$("#fontSize").val(14);
		}
		
		if(value == 'custom')
		{
			$("#fontSize").removeAttr("disabled");
			$("#fontSizeUnit").removeAttr("disabled");
		}
		else
		{
			$("#fontSize").attr("disabled", "disabled");
			$("#fontSizeUnit").attr("disabled", "disabled");
		}
	},
	
	loadColumnList : function()
	{
   		var list='';
   		if(TM.currentType == 'groupHeader')
   		{
   			list = TM.selectedGroupHeaderArray;
   		}
   		else if(TM.currentType == 'groupFooter')
   		{
   			list = TM.selectedGroupFooterArray;
   		}
   		else
   		{
   		}
   		
   		var data = "<select style=\"width: 100%;\" id = 'availableColumnList' onchange='javascript:cssGenerator.fillAvailableProperties();'>";
   		for(var i = 0; i < list.length; i++)
   		{
   			if(i == 0)
   				cssGenerator.currentColumn = list[i];
   			data += "<option value='" + list[i] +"'>" + list[i] + "</option>";
   		}
   		data += "</select>";
   		
   		cssGenerator.prevColumn = cssGenerator.currentColumn;
   		$("#columnList").html(data);
	},
	
	bindJPicker : function()
	{
		$('#fontColor').jPicker(
				{  window: // used to define the position of the popup window only useful in binded mode
				  {
				    title: null, // any title for the jPicker window itself - displays "Drag Markers To Pick A Color" if left null
				    effects:
				    {
				      type: 'slide', // effect used to show/hide an expandable picker. Acceptable values "slide", "show", "fade"
				      speed:
				      {
				        show: 'fast', // duration of "show" effect. Acceptable values are "fast", "slow", or time in ms
				        hide: 'fast' // duration of "hide" effect. Acceptable value are "fast", "slow", or time in ms
				      }
				    },
				    position:
				    {
				      x: 'center', // acceptable values "left", "center", "right", "screenCenter", or relative px value
				      y: 'center', // acceptable values "top", "bottom", "center", or relative px value
				    },
				    expandable: false, // default to large static picker - set to true to make an expandable picker (small icon with popup) - set
				                       // automatically when binded to input element
				    liveUpdate: true, // set false if you want the user to click "OK" before the binded input box updates values (always "true"
				                      // for expandable picker)
				    alphaSupport: false, // set to true to enable alpha picking
				    alphaPrecision: 0, // set decimal precision for alpha percentage display - hex codes do not map directly to percentage
				                       // integers - range 0-2
				    updateInputColor: true // set to false to prevent binded input colors from changing
				  },
					
				images:{clientPath:'images/'}}); 

		$('#backgroundColor').jPicker(
				{  window: // used to define the position of the popup window only useful in binded mode
				  {
				    title: null, // any title for the jPicker window itself - displays "Drag Markers To Pick A Color" if left null
				    effects:
				    {
				      type: 'slide', // effect used to show/hide an expandable picker. Acceptable values "slide", "show", "fade"
				      speed:
				      {
				        show: 'fast', // duration of "show" effect. Acceptable values are "fast", "slow", or time in ms
				        hide: 'fast' // duration of "hide" effect. Acceptable value are "fast", "slow", or time in ms
				      }
				    },
				    position:
				    {
				      x: 'center', // acceptable values "left", "center", "right", "screenCenter", or relative px value
				      y: 'center', // acceptable values "top", "bottom", "center", or relative px value
				    },
				    expandable: false, // default to large static picker - set to true to make an expandable picker (small icon with popup) - set
				                       // automatically when binded to input element
				    liveUpdate: true, // set false if you want the user to click "OK" before the binded input box updates values (always "true"
				                      // for expandable picker)
				    alphaSupport: false, // set to true to enable alpha picking
				    alphaPrecision: 0, // set decimal precision for alpha percentage display - hex codes do not map directly to percentage
				                       // integers - range 0-2
				    updateInputColor: true // set to false to prevent binded input colors from changing
				  },
					
				images:{clientPath:'images/'}}); 

	},
	
	unbindJPicker : function()
	{
		$('.jPicker').remove();
		$('#fontColor').unbind();
		$('#backgroundColor').unbind();
		$('#checkBookOddColor').unbind();
		$('#checkBookEvenColor').unbind();
	},
	
 	loadGroupPrefixSuffixWizard : function()
 	{
 		var data;
   		if(TM.currentType == 'groupHeader')
   			data = cssGenerator.groupHeader();
   		else if(TM.currentType == 'groupFooter')
   			data = cssGenerator.groupFooter();
   		$('#dataLoadArea').html(data);
   		cssGenerator.loadDefaultSelectedColumns();
   		
   	},
   	
   	backButtonClicked : function()
   	{
   		if(cssGenerator.tabLevel == 1)
   		{
   			$("#backViewCSSGenerator").css('display', 'none');
   			$("#nextViewCSSGenerator").css('display', '');
   			cssGenerator.loadGroupPrefixSuffixWizard();
   			cssGenerator.tabLevel = 0;
   		}
   		else if(cssGenerator.tabLevel == 2)
   		{
   			cssGenerator.tabLevel = 0;
   			cssGenerator.loadCSSStyleGeneratorPage();
   		}
   	},
   	
   	groupFooter : function()
   	{
   		var list='';

   			list=TM.searchColumn;	
   		
   	   		var groupFooter='<div id="groupFooterTable"><table><tbody>';
   	   		groupFooter+='<tr><td nowrap="nowrap">Select Column</td><td nowrap="nowrap">Prefix</td><td nowrap="nowrap">Aggregate Function</td><td nowrap="nowrap">Suffix</td></tr>';
   			for(var i=0;i<list.length;i++){
   				
   				groupFooter+='<tr><td style= "text-align: left; white-space: nowrap;" nowrap="nowrap"><input type="checkbox" name="groupFooter'+list[i]+'" id="groupFooter'+list[i]+'" value="'+list[i]+'" onclick="TM.setGroupFooter(\''+list[i]+'\', this.checked);" > '+list[i]+'</td>';
   				groupFooter+='<td style= "text-align: left;" nowrap="nowrap"><input type="text" disabled="disabled" id="group_footer_prefix'+list[i]+'" name="group_footer_prefix" placeholder="prefix" onblur="javascript:TM.setGroupFooterInJSON();"> </td>';
   				groupFooter+='<td style= "text-align: left;" nowrap="nowrap">'+TM.getAggregateFunctionDropDownForGroupFooter(list[i])+'</td>';
   				
   				groupFooter+='<td style= "text-align: left;" nowrap="nowrap"><input type="text" disabled="disabled" id="group_footer_suffix'+list[i]+'" name="group_footer_suffix" placeholder="suffix" onblur="javascript:TM.setGroupFooterInJSON();"> </td>';
   				groupFooter+='</tr>';
   			}
   			groupFooter+='</tbody></table></div>'
			return groupFooter;
   	   	
   	}, 
   	
   	groupHeader : function()
   	{

   			list=TM.searchColumn;	
   		
   		var groupHeader='<div id="groupHeaderTable"><table><tbody>';
  		 groupHeader+='<tr><td nowrap="nowrap" style="text-align: left;">Group By</td><td nowrap="nowrap">Prefix</td><td nowrap="nowrap">Suffix</td></tr>';
		for(var i=0;i<list.length;i++){
			
			groupHeader+='<tr><td style= "text-align: left; white-space: nowrap;" nowrap="nowrap"><input type="checkbox" name="groupHeader'+list[i]+'" id="groupHeader'+list[i]+'" value="'+list[i]+'" onclick="TM.setGroupHeader(\''+list[i]+'\', this.checked);" > '+list[i]+'</td>';
			groupHeader+='<td style= "text-align: left;" nowrap="nowrap" ><input type="text" id="group_header_prefix'+list[i]+'" name="group_footer_prefix" placeholder="prefix" onblur="javascript:TM.setGroupHeaderInJSON();" disabled="disabled"> </td>';
			groupHeader+='<td nowrap="nowrap" style="display: none; text-align: left;">'+TM.getAggregateFunctionDropDownForGroupHeader(list[i])+'</td>';
			groupHeader+='<td style= "text-align: left;" nowrap="nowrap"><input type="text" id="group_header_suffix'+list[i]+'" name="group_footer_suffix" placeholder="suffix" onblur="javascript:TM.setGroupHeaderInJSON();" disabled="disabled"> </td>';
			
			groupHeader+='</tr>';
		}
		groupHeader+='</tbody></table></div>';
		
		return groupHeader;
   	
   	},
   	
   	showCheckBookColorSelector : function()
   	{
//   		$("#checkbookStyleColorsRow").css('display', '');
   	},
   	formatColumn : function()
	{
		
		var colName ='';
		
		colName= Formatter.previousColName;
		
		var category = $('#formatTypeOptions').val();
		var datatype = TM.colMap[colName];
		if(datatype.toUpperCase()=="STRING" || datatype.toUpperCase() == 'BLOB'){
			datatype="string";
		}else if(datatype.toUpperCase()=="LONG" || datatype.toUpperCase()=="INTEGER" || datatype.toUpperCase()=="FLOAT" || datatype.toUpperCase()=="DOUBLE"){
			datatype="number"
		}else if(datatype.toUpperCase()=="TIMESTAMP"){
			datatype="datetime";
			if(category=="custom"){
				if($("#formatPreview").val()==""){
					category="Short Date";
					$("#formatPreview").val((new Date()).format('mm/dd/yy'));
					
				}else{
					category=$("#formatPreview").val();
				}
			}
		}
		
		var pattern = cssGenerator.getPatternByCategory(category);
		
		var formatObj = new Object();
		
		formatObj["category"]=category;
		formatObj["pattern"]=pattern;
		formatObj["type"]=datatype;
		
		var sampleData=colName;
		if($('#formatPreview').val()!=""){
			sampleData=$('#formatPreview').val();
		}
		formatObj["sample"]=sampleData;
		
		
		if(TM.currentType=="groupHeader"){
			
			if(TM.queryInfo["groupHeader"][colName]["style"]==undefined||TM.queryInfo["groupHeader"][colName]==null)
			{
				TM.queryInfo["groupHeader"][colName]["style"]=new Object();
			}
			TM.queryInfo["groupHeader"][colName]["style"]["format"]=formatObj;
		}
		else if(TM.currentType=="groupFooter"){
			
			if(TM.queryInfo["groupFooter"][colName]["style"]==undefined||TM.queryInfo["groupFooter"][colName]==null)
			{
				TM.queryInfo["groupFooter"][colName]["style"]=new Object();
			}
			
			TM.queryInfo["groupFooter"][colName]["style"]["format"]=formatObj;
		}
		else if(TM.currentType=="columnHeader"){
			if(TM.queryInfo["colHeaderDetail"][colName]==undefined||TM.queryInfo["colHeaderDetail"][colName]==null)
			{
				TM.queryInfo["colHeaderDetail"][colName]=new Object();
			}	
			TM.queryInfo["colHeaderDetail"][colName]["format"]=formatObj;
		}
		else if(TM.currentType=="columnDetail"){
			if(TM.queryInfo["colDetail"][colName]==undefined||TM.queryInfo["colDetail"][colName]==null)
			{
				TM.queryInfo["colDetail"][colName]=new Object();
			}
			
			TM.queryInfo["colDetail"][colName]["format"]=formatObj;
		}
		
		Formatter.previousColName = $('#availableColumnList').val();
		Formatter.loadFormatTypeList(false);
	},
	
	getPatternByCategory : function(category)
	{
		var pattern = new Object();
		
		var decimalPlace ="-1"
		var roundMode =  "-";
		var commaSeparator= false;
		var useSymbolSpace = false;
		var symbolNumber ="-";
		var symbolPosition = '-';
		var negativeNumber ='-';
		
		if(category=="Currency"){
			
			decimalPlace = $('#decimalPlaceNumber').val();
			 roundMode =  $('#roundModeNumber').val();
			commaSeparator= $('#commaSeparatorFlagNumber').is(':checked');
			useSymbolSpace = $('#useSymbolSpaceFlagNumber').is(':checked');
			symbolNumber =$('#symbolNumber').val();
			symbolPosition = $('#symbolPositionNumber').val();
			negativeNumber = $('#negativeNumber').val();
			
			
			
		}
		else if(category=="Percentage"){
			
			decimalPlace = $('#decimalPlaceNumber').val();
			roundMode =  $('#roundModeNumber').val();
			commaSeparator= $('#commaSeparatorFlagNumber').is(':checked');
			symbolNumber ="%";
			symbolPosition = $('#symbolPositionNumber').val();
			negativeNumber = $('#negativeNumber').val();
			
		
		}
		else if(category=="Fixed"){
			
			decimalPlace = $('#decimalPlaceNumber').val();
			roundMode =  $('#roundModeNumber').val();
			commaSeparator= $('#commaSeparatorFlagNumber').is(':checked');
			useSymbolSpace = false;
			negativeNumber = $('#negativeNumber').val();
			
		}else if(category=="Scientific"){
			decimalPlace = $('#decimalPlaceNumber').val();
			roundMode =  $('#roundModeNumber').val();
		}
		pattern["decimalPlace"]=decimalPlace;
		pattern["roundingMode"]=roundMode;
		pattern["commaSeparator"]=commaSeparator;
		pattern["useSymbolSpace"]=useSymbolSpace;
		pattern["symbolPosition"]=symbolPosition;
		pattern["symbolNumber"]=symbolNumber;
		pattern["negativeNumber"]=negativeNumber;
		
		return pattern;
	}
};