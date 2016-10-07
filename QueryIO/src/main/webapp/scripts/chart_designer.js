ChartDesigner = 
{

	
	
	ready : function()
	{
		
		$("#chartStylesTable").load('resources/chart_designer_' + currentDesignerType + '.html', function(){
			//ChartDesigner.bindJPicker();
			//ChartDesigner.loadFontFamily();
			//ChartDesigner.initJson();
		});
		
//		ChartDesigner.loadCommonProperties();
//		if(RC.chartType == 'line')
//		{
//			ChartDesigner.loadLineChartStyle();
//		}
//		else if(RC.chartType == 'bar')
//		{
//			ChartDesigner.loadBarChartStyle();
//		}
//		else if(RC.chartType == 'pie')
//		{
//			ChartDesigner.loadPieChartStyle();
//		}
	},
	
	loadCommonProperties : function()
	{
		
	},
	
	loadLineChartStyle : function()
	{
		$("#chartStylesTable").load('resources/lineChartStyle.html', function(){
			ChartDesigner.loadValuesLineChart();
		});
	},
	
	loadBarChartStyle : function()
	{
		
	},
	
	loadPieChartStyle : function()
	{
		
	},
	
	loadValuesLineChart : function()
	{
		
	},
	
	closeBox : function()
	{
		Util.removeLightbox("chartDesigner");
	},
	
	loadFontFamily : function()
	{
		var fontFamilies = ["Arial", "Times New Roman", "Verdana", "Courier"];
		var data = "";
		
		for(var i = 0; i < fontFamilies.length; i++)
		{
			data += "<option value='" + fontFamilies[i] + "'>" + fontFamilies[i] + "</option>";  
		}
		
		$(".fontFamilyContainer").html(data)
	},
	
	bindJPicker : function()
	{
		$(".applyJPicker").jPicker(
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
	
	
	
	
	
};