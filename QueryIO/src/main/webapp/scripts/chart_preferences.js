CHARTPR = {
		
		chartPR : new Object(),
		currentType : "",
		first : true,
		p : {},
		ready : function(){
			CHARTPR.fillChartPreference(false);
		},
		bindFunction : function(){
			$('div#chart_preference_lightbox select').change(function(){
				CHARTPR.finishStyling();
			});
			$("div#chart_preference_lightbox input[type=text]").bind("change paste keyup", function() {
				CHARTPR.finishStyling();
				});
			$('div#chart_preference_lightbox input[type=text]').keypress(function(){
				CHARTPR.finishStyling();
			});
			
			$('.applyJPicker').change(function(){
				CHARTPR.finishStyling();
			});
			$('div#chart_preference_lightbox input[type=text]').change(function(){
				CHARTPR.finishStyling();
			});
			$('div#chart_preference_lightbox input[type=checkbox]').click(function(){
				CHARTPR.finishStyling();
			});
			
		},
		
		
		
		LoadPRBox : function(box_name,element)
        {
			CHARTPR.currentType = box_name;
            $("#tabs a.active").removeClass('active');
            $("#" + box_name).addClass("active");
            $("#chart_prefernces_main_box").children('div').removeClass('showPreference');
            $("#chart_prefernces_" + box_name).addClass('showPreference').css('display', '');
            $('#chart_prefernces_main_box').children('div').not('.showPreference').css('display', 'none');
            this.jPickerBind();
            CHARTPR.showChartPreview();
            
        },
        saveStyle : function (){
        	
        	if(CHARTPR.validate())
        	{
	        	if(CM.queryInfo["chartDetail"]==undefined||CM.queryInfo["chartDetail"]==null){
					CM.queryInfo["chartDetail"] = new Object();
				}
				
	        	if(RC.chartOperation == 'edit'){
	        		CM.queryInfo["chartDetail"][RC.chartKey]["chartPreferences"] = CHARTPR.chartPR;
	        		CM.showChartSample('',CM.queryInfo["chartDetail"][RC.chartKey]);
	        	}else{
	        		CM.globalChartPreferences = jQuery.extend({}, CHARTPR.chartPR);
	        		CM.queryInfo["chartDetail"]["chartPreferences"] = CHARTPR.chartPR;
	        		//if(RC.chartPreferenceType == 'global')
	        		RemoteManager.saveChartPreferences(JSON.stringify(CHARTPR.chartPR), CM.handleSavePreferencesResponse);
	        	}
	        	Navbar.queryManagerDirtyBit = true;
	        	closePreferncesBox();
        	}
        },
		finishStyling : function()
		{
			var commonArray =[];
			var xLegendArray = [];
			var yLegendArray = [];
			var labelArray = [];
			var outlineArray = [];
			var insetsArray = [];
			var leaderLinesArray = [];
			var titleArray = [];
			var lineChartArray = [];
			PR = 
			{
				commonJson : {},
				yAxisJson : {},
				outLineJson : {},
				insetsJson : {},
				leaderLineJson : {},
				xAxisJson : {},
				titleJson : {},
				lineChartJson : {},
				labelJson : {},
				legendJson : {}
			};
			var outJson = new Object();
			var insetJson = new Object();
			var currentType =CHARTPR.currentType; 

			PR.commonJson["background"] = $('#Background').val();
			PR.commonJson["wallBackground"] = $('#WallBackground').val();
			PR.commonJson["floorBackground"] = $('#FloorBackground').val();
			PR.commonJson["clientBackground"] = $('#ClientBackground').val();
			PR.commonJson["plotBackground"] = $('#PlotBackground').val();
			PR.commonJson["emptyChartMessage"] = $('#EmptyChartMessage').val();
			for(var i=1;i<=10;i++)
			{
				commonArray.push($('#color'+i).val());
			}
			PR.commonJson["topColors"] = commonArray;
	
			PR.leaderLineJson["visible"] = $('#leaderLinesVisible').is(':checked');
			PR.leaderLineJson["style"] = $('#leaderLinesStyle').val();
			PR.leaderLineJson["width"] = $('#leaderLineWidth').val();
			PR.leaderLineJson["color"] = $('#leaderLineColor').val();
			PR.leaderLineJson["lineStyle"] = $('#leaderLinesLineStyle').val();
			PR.leaderLineJson["lineLength"] = $('#leaderLineLength').val();
		
			PR.xAxisJson["value"] = "Xaxis";
			PR.xAxisJson["visible"] = $('#xAxisShow').is(':checked');
			PR.xAxisJson["background"] = $('#xBackground').val();
			PR.xAxisJson["shadow"] = $('#xShadow').val();
			PR.xAxisJson["position"] = $('#legendxPosition').val();
			PR.xAxisJson["text-align"] = $('#legendxFontTextAlign').val();
			PR.xAxisJson["color"] = $('#legendxFontColor').val();
			PR.xAxisJson["font-style"] = $('#legendxFontStyle').val();
			PR.xAxisJson["font-size"] = $('#xFontSize').val()//+$('#legendXFontSizeType').val();
			PR.xAxisJson["font-family"] = $('#legendxFontFamily').val();
			PR.xAxisJson["titleFontSize"] = $('#xTitleFontSize').val();
			PR.xAxisJson["titleColor"] = $('#xTitleFontColor').val();
			
			PR.xAxisJson["gridline"] = new Object();
			PR.xAxisJson["gridline"]["visible"] = $('#xgridlineVisible').is(':checked');
			PR.xAxisJson["gridline"]["style"] = $('#xgridlineStyle').val();
			PR.xAxisJson["gridline"]["width"] = $('#xgridlineWidth').val();
			PR.xAxisJson["gridline"]["color"] = $('#xgridlineColor').val();
			PR.xAxisJson["gridline"]["gridstep"] = $('#xaxisgridstepnumber').val();
			
			PR.xAxisJson["outline"] = new Object();
			PR.xAxisJson["outline"]["visible"] = $('#xoutlineVisible').is(':checked');
			PR.xAxisJson["outline"]["style"] = $('#xoutlineStyle').val();
			PR.xAxisJson["outline"]["width"] = $('#xoutlineWidth').val();
			PR.xAxisJson["outline"]["color"] = $('#xoutlineColor').val();
			
			PR.xAxisJson["insets"] = new Object();
			PR.xAxisJson["insets"]["top"] = $('#xinsetsTop').val();
			PR.xAxisJson["insets"]["bottom"] = $('#xinsetBottom').val();
			PR.xAxisJson["insets"]["left"] = $('#xinsetLeft').val();
			PR.xAxisJson["insets"]["right"] = $('#xinsetRight').val();
			
			PR.yAxisJson["value"] = "Yaxis";
			PR.yAxisJson["visible"] = $('#yAxisShow').is(':checked');
			PR.yAxisJson["background"] = $('#yBackground').val();
			PR.yAxisJson["shadow"] = $('#yShadow').val();
			PR.yAxisJson["position"] = $('#legendyPosition').val();
			PR.yAxisJson["text-align"] = $('#legendyFontTextAlign').val();
			PR.yAxisJson["color"] = $('#legendyFontColor').val();
			PR.yAxisJson["font-style"] = $('#legendyFontStyle').val();
			PR.yAxisJson["font-size"] = $('#yFontSize').val()//+$('#legendXFontSizeType').val();
			PR.yAxisJson["font-family"] = $('#legendyFontFamily').val();
			PR.yAxisJson["titleColor"] = $('#yTitleFontColor').val();
			PR.yAxisJson["titleFontSize"] = $('#yTitleFontSize').val();
			
			PR.yAxisJson["gridline"] = new Object();
			PR.yAxisJson["gridline"]["visible"] = $('#ygridlineVisible').is(':checked');
			PR.yAxisJson["gridline"]["style"] = $('#ygridlineStyle').val();
			PR.yAxisJson["gridline"]["width"] = $('#ygridlineWidth').val();
			PR.yAxisJson["gridline"]["color"] = $('#ygridlineColor').val();
			PR.xAxisJson["gridline"]["gridstep"] = $('#yaxisgridstepnumber').val();
			
			PR.yAxisJson["outline"] = new Object();
			PR.yAxisJson["outline"]["visible"] = $('#youtlineVisible').is(':checked');
			PR.yAxisJson["outline"]["style"] = $('#youtlineStyle').val();
			PR.yAxisJson["outline"]["width"] = $('#youtlineWidth').val();
			PR.yAxisJson["outline"]["color"] = $('#youtlineColor').val();
			
			PR.yAxisJson["insets"] = new Object();
			PR.yAxisJson["insets"]["top"] = $('#yinsetsTop').val();
			PR.yAxisJson["insets"]["bottom"] = $('#yinsetBottom').val();
			PR.yAxisJson["insets"]["left"] = $('#yinsetLeft').val();
			PR.yAxisJson["insets"]["right"] = $('#yinsetRight').val();
			
			PR.titleJson["value"] = "Chart";
			PR.titleJson["anchor"] = $('#TitleAnchor').val();
			PR.titleJson["text-align"] = $('#TitleFontTextAlign').val();
			PR.titleJson["color"] = $('#TitleFontColor').val();
			PR.titleJson["font-style"] = $('#TitleFontStyle').val();;
			PR.titleJson["font-size"] = $('#TitleFontSize').val();//+$('#TitleFontSizeType').val();
			PR.titleJson["font-family"] = $('#TitleFontFamily').val();
			PR.titleJson["background"] = $('#TitleBackground').val();
			PR.titleJson["outline"] = new Object();
			PR.titleJson["outline"]["visible"] = $('#titleoutlineVisible').is(':checked');
			PR.titleJson["outline"]["style"] = $('#titleoutlineStyle').val();
			PR.titleJson["outline"]["width"] = $('#titleoutlineWidth').val();
			PR.titleJson["outline"]["color"] = $('#titleoutlineColor').val();
			PR.titleJson["insets"] = new Object();
			PR.titleJson["insets"]["top"] = $('#titleinsetsTop').val();
			PR.titleJson["insets"]["bottom"] = $('#titleinsetBottom').val();
			PR.titleJson["insets"]["left"] = $('#titleinsetLeft').val();
			PR.titleJson["insets"]["right"] = $('#titleinsetRight').val();
		
			PR.lineChartJson["showCurve"] = $('#showCurve').is(':checked');
			PR.lineChartJson["curveColor"] = $('#curveColor').val();
		
			PR.labelJson["visible"] = $('#labelsShow').is(':checked');
			PR.labelJson["position"] = $('#labelsPosition').val();
			PR.labelJson["text-align"] = $('#labelsFontTextAlign').val();
			PR.labelJson["background"] = $('#labelsBackground').val();
			PR.labelJson["shadow"] = $('#labelsShadow').val();
			PR.labelJson["font-color"] = $('#labelsFontColor').val();
			PR.labelJson["font-style"] = $('#labelsFontStyle').val();
			PR.labelJson["font-size"] = $('#labelFontSize').val()//+$('#labelFontSizeType').val();
			PR.labelJson["font-family"] = $('#labelsFontFamily').val();
			//PR.labelJson["percent-value"] = $('#labelsPercentValue').is(':checked');
			//PR.labelJson["actual-value"] = $('#labelsActualValue').is(':checked');
			PR.labelJson["values"] = $('#labelsValuePercentActual').val();
			PR.labelJson["prefix"] = $('#labelsPrefix').val();
			PR.labelJson["suffix"] = $('#labelsSuffix').val();
			PR.labelJson["separator"] = $('#labelsSeparator').val();
			PR.labelJson["outline"] = new Object();
			PR.labelJson["outline"]["visible"] = $('#labeloutlineVisible').is(':checked');
			PR.labelJson["outline"]["style"] = $('#labeloutlineStyle').val();
			PR.labelJson["outline"]["width"] = $('#labeloutlineWidth').val();
			PR.labelJson["outline"]["color"] = $('#labeloutlineColor').val();
			PR.labelJson["insets"] = new Object();
			PR.labelJson["insets"]["top"] = $('#labelinsetsTop').val();
			PR.labelJson["insets"]["bottom"] = $('#labelinsetBottom').val();
			PR.labelJson["insets"]["left"] = $('#labelinsetLeft').val();
			PR.labelJson["insets"]["right"] = $('#labelinsetRight').val();
			
		
			PR.outLineJson["visible"] = $('#outlineVisible').is(':checked');
			PR.outLineJson["style"] = $('#outlineStyle').val();
			PR.outLineJson["width"] = $('#outlineWidth').val();
			PR.outLineJson["color"] = $('#outlineColor').val();
		
			PR.insetsJson["top"] = $('#insetsTop').val();
			PR.insetsJson["bottom"] = $('#insetBottom').val();
			PR.insetsJson["left"] = $('#insetLeft').val();
			PR.insetsJson["right"] = $('#insetRight').val();
			
			
			PR.legendJson["visible"] = $('#legendShow').is(':checked');
			PR.legendJson["visibleTitle"] = $('#legendTitleShow').is(':checked');
			PR.legendJson["background"] = $('#legendBackground').val();
			PR.legendJson["title-font-family"] = $('#legendFontFamily').val();
			PR.legendJson["title-font-style"] = $('#legendFontStyle').val();
			PR.legendJson["title-font-color"] = $('#legendFontColor').val();
			PR.legendJson["title-font-size"] = $('#legendFontSize').val();
			PR.legendJson["position"] = $('#legendPosition').val();
			PR.legendJson["stretch"] = $('#legendStretch').val();
			PR.legendJson["anchor"] = $('#legendAnchor').val();
			PR.legendJson["outline"] = new Object();
			PR.legendJson["outline"]["visible"] = $('#legendoutlineVisible').is(':checked');
			PR.legendJson["outline"]["width"]  = $('#legendoutlineWidth').val();
			PR.legendJson["outline"]["style"] = $('#legendoutlineStyle').val();
			PR.legendJson["outline"]["color"] = $('#legendoutlineColor').val();
			PR.legendJson["insets"] = new Object();
			PR.legendJson["insets"]["top"] = $('#legendinsetsTop').val();
			PR.legendJson["insets"]["bottom"] = $('#legendinsetBottom').val();
			PR.legendJson["insets"]["left"] = $('#legendinsetLeft').val();
			PR.legendJson["insets"]["right"] = $('#legendinsetRight').val();
			
				
			CHARTPR.chartPR = PR;
			CHARTPR.showChartPreview();
		},

		showChartPreview : function(){
				$('#no_preview_available').hide();
				var chartType = 'pie';
				var prObject = CHARTPR.chartPR;
				var commonJson = prObject["commonJson"]
				var isXGridLineVisible = prObject["xAxisJson"]["gridline"]["visible"];
				var isYGridLineVisible = prObject["yAxisJson"]["gridline"]["visible"];
//				var isGridLineVisible = CHARTPR.chartPR[];
				if(commonJson==undefined||commonJson==null){
				 commonJson = {
				        "background": "bf0000", 
				        "wallBackground": "ffaa56", 
				        "floorBackground": "aaffd4", 
				        "clientBackground": "ffff56", 
				        "includeAxesBackground": "007f7f", 
				        "withinAxesBackground": "aad4ff", 
				        "plotBackground": "7f00ff", 
				        "emptyChartMessage": "Hello", 
				        "topColors": [
				              "579575", "4BB2C5", "EAA228", "C5B47F", "953579", "4B5DE4", "D8B83F", "990000", "003300", "004a6d"
				        ]
				    };
				}
				
				var seriesArray = [];
				var labels = [];
				var colors = [];
				
				var colorCode = commonJson["topColors"];
				for(var i=0;i<colorCode.length;i++){
					seriesArray.push([String.fromCharCode(65+i), + (6.6)*(i+1)]);
					labels.push(String.fromCharCode(65+i));
					colors.push('#'+colorCode[i]);
				}	
				
				var pos;
				var loc ;
				var locBar;
				if(CHARTPR.chartPR["labelJson"]["position"]=="inside") {
					pos = 0.6;
						locBar = 's';
						loc = 'e';
				} else {
					pos = 1.1;
						locBar = 'n';
						loc = 'w';
				}
				
				$('#preview_chart').html('');
				if((CHARTPR.currentType=='x_legend' || CHARTPR.currentType=='y_legend' ) && RC.chartPreferenceType =='global'){
					chartType = 'line';
				}else{
					chartType = RC.chartType;
				}
				if(chartType == 'line'){
					//code for kine chart.
					$.jqplot ('preview_chart', [[28,13,25,15,33]], {  
					      title: {show: false},
					      grid:{shadow: false, borderWidth:0.0, background: 'transparent', gridLineColor:prObject["xAxisJson"]["gridline"]["color"],gridLineWidth : prObject["xAxisJson"]["gridline"]["width"]},
					      series: [ {showMarker:false, pointLabels:{show : true, location : loc, ypadding:2, labelsFromSeries : true}}],
					      seriesColors:[colorCode[0]],
					      axes:{
					        xaxis:{
					          label: $('#line_x_axis_legend').val(),
					          tickOptions:{
					              showGridline: isXGridLineVisible,
					              showLabel: 'x',
					           }
					        },
					        yaxis:{
					          label: $('#line_y_axis_legend').val(),
					          tickOptions:{
					              showGridline: isYGridLineVisible,
					              showLabel: 'y',
					           },
					           
					        }
					      },
					      legend:{show:true, location: 'e'},
					});
					$('#preview_chart div.jqplot-table-legend-swatch-outline').css("color", "#" + colorCode[0]);
				
				} else if(chartType == 'scatter'){
					//code for kine chart.
					$.jqplot ('preview_chart', [[28,13,25,15,33]], {  
					      title: {show: false},
					      grid:{shadow: false, borderWidth:0.0, background: 'transparent', gridLineColor:prObject["xAxisJson"]["gridline"]["color"],gridLineWidth : prObject["xAxisJson"]["gridline"]["width"]},
					      series : [ {
								pointLabels : {
									show : true,
									location : loc,
									ypadding : 2,
									labelsFromSeries : true
								},
								showLine:false, 
						        markerOptions: { size: 7, style:"x" }
							} ],
					      seriesColors:[colorCode[0]],
					      axes:{
					        xaxis:{
					          label: $('#line_x_axis_legend').val(),
					          tickOptions:{
					              showGridline: isXGridLineVisible,
					              showLabel: 'x',
					           }
					        },
					        yaxis:{
					          label: $('#line_y_axis_legend').val(),
					          tickOptions:{
					              showGridline: isYGridLineVisible,
					              showLabel: 'y',
					           },
					           
					        }
					      },
					      legend:{show:true, location: 'e'},
					});
					$('#preview_chart div.jqplot-table-legend-swatch-outline').css("color", "#" + colorCode[0]);
				
				} else if(chartType =='bar' || chartType =='tube' ){
					//code for bar chart.
					var yseries = new Array();
					var yvalues =($('#line_y_series').val()).split(',');
					for(var i=0;i<yvalues.length;i++){
						yseries.push({label:yvalues[i]});
					}
					$.jqplot.config.enablePlugins = true;
			        var s1 = [2, 6, 7, 10];
			        var ticks = ['a', 'b', 'c', 'd'];
			         
			        $.jqplot('preview_chart', [s1], {
				        	grid : {
								shadow : false,
								borderWidth : 0.0,
								background : 'transparent',
								gridLineColor : prObject["xAxisJson"]["gridline"]["color"],
								gridLineWidth : prObject["xAxisJson"]["gridline"]["width"]
							},
			            seriesColors:[colorCode[0]],
			            seriesDefaults:{
			                renderer:$.jqplot.BarRenderer,
			                pointLabels : {
								show : true,
								location : locBar
							}
			            },
//			            series:yseries,
			            axes: {
			                xaxis: {
			                	label: $('#line_x_axis_legend').val(),
			                	renderer: $.jqplot.CategoryAxisRenderer,
			                    ticks: ticks,
			                    tickOptions:{
			                    	  showGridline: isXGridLineVisible,
						              showLabel: CHARTPR.chartPR.xAxisJson["visible"],
						           }
			                },
				            yaxis: {
				            	label: $('#line_y_axis_legend').val(),
				            	tickOptions:{
						              showGridline: isYGridLineVisible,
						              showLabel: CHARTPR.chartPR.yAxisJson["visible"],
						           }
			                }
			            },
			            legend:{show:true, location: 'e'},
			        });
			        $('#preview_chart div.jqplot-table-legend-swatch-outline').css("color", "#" + colorCode[0] );
				
				}  else if(chartType =='area') {
			        
			        $.jqplot('preview_chart' , [[11, 9, 5, 12,14]],{
			        	series : [ {
							showMarker : false,
							fill : true,
							pointLabels : {
								show : true,
								location : loc,
								ypadding : 2,
								labelsFromSeries : true
							}
							
						} ],
					       grid : {
									shadow : false,
									borderWidth : 0.0,
									background : 'transparent',
									gridLineColor : prObject["xAxisJson"]["gridline"]["color"],
									gridLineWidth : prObject["xAxisJson"]["gridline"]["width"]
								},
						   seriesColors : [ colorCode[0] ],
						   
						   axes: {
				                xaxis: {
				                	label: $('#line_x_axis_legend').val(),
				                	renderer: $.jqplot.CategoryAxisRenderer,
				                	ticks: ticks,
				                    tickOptions:{
				                    	  showGridline: isXGridLineVisible,
							              showLabel: CHARTPR.chartPR.xAxisJson["visible"],
							           }
				                },
					            yaxis: {
					            	label: $('#line_y_axis_legend').val(),
					            	tickOptions:{
							              showGridline: isYGridLineVisible,
							              showLabel: CHARTPR.chartPR.yAxisJson["visible"],
							           }
				                }
				            },
							legend : {
								show : true,
								location : 'e'
							},
					    });
			        $('#preview_chart div.jqplot-table-legend-swatch-outline').css("color", "#" + colorCode[0] );
				
				} else if(chartType =='cone' || chartType =='pyramid') {
					var ypad = 2;
					if(locBar == 's')
						ypad = 30;
			        $.jqplot('preview_chart' , [[0,11,0,0, 9,0,0, 5,0,0, 12,0]],{
			        	series : [ {
							showMarker : false,
							fill : true,
							pointLabels : {
								show : true,
								location : locBar,
								ypadding : ypad,
								labels:['', '11', '','', '9','','', '5','','','12','']
							}
							
						} ],
					       grid : {
									shadow : false,
									borderWidth : 0.0,
									background : 'transparent',
									gridLineColor : prObject["xAxisJson"]["gridline"]["color"],
									gridLineWidth : prObject["xAxisJson"]["gridline"]["width"]
								},
						   seriesColors : [ colorCode[0] ],
						   
						   axes: {
				                xaxis: {
				                	label: $('#line_x_axis_legend').val(),
				                	renderer: $.jqplot.CategoryAxisRenderer,
				                	
				                    tickOptions:{
				                    	  showGridline: isXGridLineVisible,
							              showLabel: CHARTPR.chartPR.xAxisJson["visible"],
							           }
				                },
					            yaxis: {
					            	label: $('#line_y_axis_legend').val(),
					            	ticks: [0,5,10,15], 
					            	tickOptions:{
							              showGridline: isYGridLineVisible,
							              showLabel: CHARTPR.chartPR.yAxisJson["visible"],
							           }
				                }
				            },
							legend : {
								show : true,
								location : 'e'
							},
					    });
			        $('#preview_chart div.jqplot-table-legend-swatch-outline').css("color", "#" + colorCode[0] );
				
				}  else if(chartType =='bubble') {
					var arr = [
					           [11, 123, 30, "Log"], [45, 92, 30, "csv"], 
					           [24, 104, 30, "ppt"], [40, 63, 30, "doc"], 
					           ];
			        $.jqplot('preview_chart' , [arr],{
			        	seriesDefaults:{
			                renderer: $.jqplot.BubbleRenderer,
			                rendererOptions: {
			                    bubbleAlpha: 0.6,
			                    highlightAlpha: 0.8,
			                    autoscaleBubbles : false,
			                    autoscalePointsFactor : -1.0
			                },
			                shadow: true,
			                shadowAlpha: 0.05
			            },
			            grid : {
							shadow : false,
							borderWidth : 0.0,
							background : 'transparent',
							gridLineColor : prObject["xAxisJson"]["gridline"]["color"],
							gridLineWidth : prObject["xAxisJson"]["gridline"]["width"]
						},
						//seriesColors: colors,
						seriesColors : [ colorCode[0] ],
			            axes: {
			                xaxis: {
			                	label: $('#line_x_axis_legend').val(),
			                	renderer: $.jqplot.CategoryAxisRenderer,
			                    ticks: ticks,
			                    tickOptions:{
			                    	  showGridline: isXGridLineVisible,
						              showLabel: CHARTPR.chartPR.xAxisJson["visible"],
						           }
			                },
				            yaxis: {
				            	label: $('#line_y_axis_legend').val(),
				            	tickOptions:{
						              showGridline: isYGridLineVisible,
						              showLabel: CHARTPR.chartPR.yAxisJson["visible"],
						           }
			                }
			            },
						legend : {
							show : true,
							location : 'e'
						},
				    });
			        $('#preview_chart div.jqplot-table-legend-swatch-outline').css("color", "#" + colorCode[0] );
				
				} else if(chartType =='meter') {
			        
					$.jqplot('preview_chart',[[1]],{
					       seriesDefaults: {
					           renderer: $.jqplot.MeterGaugeRenderer,
					           rendererOptions: {
					               background : 'transparent',
					               showTickLabels : CHARTPR.chartPR["labelJson"]["visible"],
					               ringColor : '#00000',
					               tickColor : '#0096bc',
					               ringWidth : 1.0,
					               labelPosition : 'bottom'
					           },
					           pointLabels: { show: false }
					       },
					       seriesColors: colors,
					       legend : {
								show : true,
								location : 'e'
							},
					   });
					
					$('#chart_preview_client_area .jqplot-series-canvas').css('background-color','#'+commonJson["clientBackground"]);
					$('#preview_chart div.jqplot-meterGauge-tick').css("z-index", "99" );
			        $('#preview_chart div.jqplot-table-legend-swatch-outline').css("color", "#" + colorCode[0] );
				
				
				}
				
				else if (chartType == "stock") {
					$('#preview_chart').html('');
					$('#preview_chart').html('<img src="images/stockchart.png"/ style="height: 250px; background: #'+commonJson["clientBackground"]+' ">');
					$('#legendTable').html('<tbody><tr class="jqplot-table-legend" style="background-color: transparent;"><td class="jqplot-table-legend jqplot-table-legend-swatch" style="text-align: center; padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;"><div class="jqplot-table-legend-swatch-outline" style="color: rgb(0, 150, 188);"><div class="jqplot-table-legend-swatch"></div></div></td><td class="jqplot-table-legend jqplot-table-legend-label" style="padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;">Series 1</td></tr></tbody>');
					$('#no_preview_available').show();
				}
				else if (chartType == "gantt") {
					$('#preview_chart').html('');
					$('#preview_chart').html('<img src="images/ganttchart.png"/ style="height: 250px; background: #'+commonJson["clientBackground"]+' ">');
					$('#legendTable').html('<tbody><tr class="jqplot-table-legend" style="background-color: transparent;"><td class="jqplot-table-legend jqplot-table-legend-swatch" style="text-align: center; padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;"><div class="jqplot-table-legend-swatch-outline" style="color: rgb(0, 150, 188);"><div class="jqplot-table-legend-swatch"></div></div></td><td class="jqplot-table-legend jqplot-table-legend-label" style="padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;">Series 1</td></tr></tbody>');
					$('#no_preview_available').show();
				}
				else if (chartType == "difference") {
					$('#preview_chart').html('');
					$('#preview_chart').html('<img src="images/differencechart.png"/ style="height: 250px; background: #'+commonJson["clientBackground"]+' ">');
					$('#legendTable').html('<tbody><tr class="jqplot-table-legend" style="background-color: transparent;"><td class="jqplot-table-legend jqplot-table-legend-swatch" style="text-align: center; padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;"><div class="jqplot-table-legend-swatch-outline" style="color: rgb(0, 150, 188);"><div class="jqplot-table-legend-swatch"></div></div></td><td class="jqplot-table-legend jqplot-table-legend-label" style="padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;">Series 1</td></tr></tbody>');
					$('#no_preview_available').show();
					
				}
				else if(chartType =='radar') {
					$('#preview_chart').html('');
			        $('#preview_chart').html('<img src="images/radar.png"/ style="height: 250px; background: #'+commonJson["clientBackground"]+' ">');
			        $('#legendTable').html('<tbody><tr class="jqplot-table-legend" style="background-color: transparent;"><td class="jqplot-table-legend jqplot-table-legend-swatch" style="text-align: center; padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;"><div class="jqplot-table-legend-swatch-outline" style="color: rgb(0, 150, 188);"><div class="jqplot-table-legend-swatch"></div></div></td><td class="jqplot-table-legend jqplot-table-legend-label" style="padding-top: 0px; background-color: transparent; border-top-left-radius: 0px; border-top-right-radius: 0px; border-bottom-right-radius: 0px; border-bottom-left-radius: 0px;">Series 1</td></tr></tbody>');
			        $('#no_preview_available').show();
				}	else {
					$.jqplot("preview_chart", [seriesArray], {
						title: {show: false},
						grid:{shadow: false, borderWidth:0.0, background: 'transparent'},
						seriesColors: colors,
						seriesDefaults:{renderer:$.jqplot.PieRenderer, rendererOptions:{sliceMargin:2, startAngle: 45, diameter:155, showDataLabels: true,  dataLabels: 'value', dataLabelPositionFactor: pos}},
						legend:{show:true, location: 'e', labels: labels}
					});
					
				}  
				
					
				$('#legend-table'+table_count).css('bottom','0px');
				$('#legend-table'+table_count).css('width','50%');
				$('#legend-table'+table_count).css('margin-right','50px');
				
				$('#chart_preview_plot_area').css('background-color','#'+commonJson["plotBackground"]);
				$('#chart_preview_client_area .jqplot-series-shadowCanvas').css('background-color','#'+commonJson["clientBackground"]);
				$('#chart_preview_client_area .jqplot-grid-canvas').css('z-index','19');
				$('#chart_preview_client_area .jqplot-series-canvas').css('z-index','19');
				
				
				$('#preview_chart_div').css('background-color','#'+commonJson["background"]);
				$('td.jqplot-table-legend').css('background-color','#'+commonJson["background"]);
				$('td.jqplot-table-legend').css('border-radius','0px');
				
				if(chartType!='radar' && chartType!='difference' && chartType!='stock' && chartType!='gantt') {
					$('#legendTable').html($('table.jqplot-table-legend').html());
					$('table.jqplot-table-legend').remove();
				}
				$('#chart_preveiw_title').css('font-size', CHARTPR.chartPR.titleJson["font-size"]+'px');
				$('#chart_preveiw_title').css("font-family",CHARTPR.chartPR["titleJson"]["font-family"]);
				$('#chart_preveiw_title').css("font-weight",CHARTPR.chartPR["titleJson"]["font-style"]);
				$('#chart_preveiw_title').css("text-align",CHARTPR.chartPR["titleJson"]["text-align"]);
				if(CHARTPR.chartPR.titleJson["background"]=="")
					$('#chart_preveiw_title').css('background', 'transparent');
				else
					$('#chart_preveiw_title').css('background', '#'+CHARTPR.chartPR.titleJson["background"]);
				
				if(CHARTPR.chartPR.titleJson["color"]=="")
					$('#chart_preveiw_title').css('color', 'transparent');
				else
					$('#chart_preveiw_title').css('color', '#'+CHARTPR.chartPR.titleJson["color"]);
				//set chart outLine.
				
				CHARTPR.setDivOutLine('chart_preveiw_title',CHARTPR.chartPR["titleJson"]["outline"]);
				CHARTPR.setDivInsets('chart_preveiw_title',CHARTPR.chartPR["titleJson"]["insets"]);
				
				if(CHARTPR.chartPR.titleJson["anchor"]=="top"){
					$('#chart_preveiw_title').css("position","");
					$('#chart_preveiw_title').css("top","");
					$('#parent_chart_legend_div').css("margin-top","0px");
				}
				else{
					$('#chart_preveiw_title').css("position","relative");
					$('#chart_preveiw_title').css("top","93%");
					$('#parent_chart_legend_div').css("margin-top","-17px");
				}
				
				CHARTPR.setDivOutLine('preview_chart_div',CHARTPR.chartPR["outLineJson"]);
								
				
				if(!CHARTPR.chartPR["labelJson"]["visible"]){
					$('#preview_chart div.jqplot-data-label, #preview_chart div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').remove();
				} 
				$('div.jqplot-data-label, div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("z-index","1000000");
				$('div.jqplot-data-label, div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick, #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("text-align",CHARTPR.chartPR["labelJson"]["text-align"]);
				if(CHARTPR.chartPR["labelJson"]["background"] == "")
					$('div.jqplot-data-label, div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick, #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("background",'transparent');
				else
					$('div.jqplot-data-label, div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("background",'#'+ CHARTPR.chartPR["labelJson"]["background"]);
				$('div.jqplot-data-label, div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("text-shadow",'2px 2px #'+CHARTPR.chartPR["labelJson"]["shadow"]);
				$('div.jqplot-data-label, div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("color",'#' + CHARTPR.chartPR["labelJson"]["font-color"]);
				$('div.jqplot-data-label, div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("font-size",CHARTPR.chartPR["labelJson"]["font-size"] + 'px');
				$('div.jqplot-data-label, div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("font-family",CHARTPR.chartPR["labelJson"]["font-family"]);
				$('div.jqplot-data-label, div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("font-weight",CHARTPR.chartPR["labelJson"]["font-style"]);
				var outLine = CHARTPR.chartPR["labelJson"]["outline"];
				if(outLine["visible"]){
					$('div.jqplot-data-label, div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("border-style",outLine["style"]);
					$('div.jqplot-data-label, div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("border-color",'#'+outLine["color"]);
					$('div.jqplot-data-label, div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("border-width",outLine["width"]+"px");
				}
				var insets = CHARTPR.chartPR["labelJson"]["insets"];
				$('#preview_chart div.jqplot-data-label, #preview_chart div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("margin-top",insets["top"]+"px");
				$('#preview_chart div.jqplot-data-label, #preview_chart div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("margin-right",insets["right"]+"px");
				$('#preview_chart div.jqplot-data-label, #preview_chart div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("margin-bottom",insets["bottom"]+"px");
				$('#preview_chart div.jqplot-data-label, #preview_chart div.jqplot-point-label , #preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').css("margin-left",insets["left"]+"px");
				
				
				if(CHARTPR.chartPR["labelJson"]["position"]=="outside" && RC.chartPreferenceType=="local")
				{
					$('#preview_chart div.jqplot-bubble-label ').each(function() {
						var temp = $(this).css("top");
						temp = temp.substring(0,temp.length - 2) - 40;
						$(this).css("top", temp + "px");
					});
					
					
				}
				
				if(CHARTPR.chartPR["labelJson"]["values"]=="percent")
				{
					var preTemp = CHARTPR.chartPR["labelJson"]["prefix"];
					var sepTemp = CHARTPR.chartPR["labelJson"]["separator"];
					var sufTemp = CHARTPR.chartPR["labelJson"]["suffix"];
					$('#preview_chart div.jqplot-data-label,#preview_chart div.jqplot-point-label ,#preview_chart div.jqplot-bubble-label , #preview_chart div.jqplot-meterGauge-tick').each(function() {
						var temp = $(this).html();
						temp = preTemp + temp + '.' + temp + '%' + sufTemp;
						$(this).html(temp);
				    })
				}
				var legendJson = CHARTPR.chartPR["legendJson"];
				if(legendJson != null && legendJson != undefined){
				
					if(legendJson["position"]=="left"){
						$('#parent_chart_legend_div').css("float","left");
						$('#chart_preview_plot_area').css("float","right");
					}else{
						$('#parent_chart_legend_div').css("float","right");
						$('#chart_preview_plot_area').css("float","left");
					}
					
					if(legendJson["anchor"]=="top"){
						$('#chart_legend_div').css("margin-top","10px");
					}else if(legendJson["anchor"]=="bottom"){
						$('#chart_legend_div').css("margin-top","220px");
					}else{
						$('#chart_legend_div').css("margin-top","100px");
					}
					
					if(legendJson["background"]=="")
					{
						$('#parent_chart_legend_div').css("background-color","transparent");
					} else {
						$('#parent_chart_legend_div').css("background-color",'#'+ legendJson["background"]);
					}
					
					$('#legendTable .jqplot-table-legend').css("background-color",'#'+ legendJson["background"]);
					
					$('#legend_title').text($(line_y_axis_legend).val());
					$('#legend_title').css("font-family",legendJson["title-font-family"]);
					$('#legend_title').css("font-size",legendJson["title-font-size"]+"px");
					$('#legend_title').css("color","#"+legendJson["title-font-color"]);
					$('#legend_title').css("font-weight",legendJson["title-font-style"]);
					
					if(legendJson["visibleTitle"]){
						$('#legend_title').css("display","");	
					}else{
						$('#legend_title').css("display","none");
					}
					
					if(legendJson["visible"]){
						$('#parent_chart_legend_div').css("display","");
					}else{
						$('#parent_chart_legend_div').css("display","none");
					}
					CHARTPR.setDivOutLine('parent_chart_legend_div',legendJson["outline"]);
//					CHARTPR.setDivInsets('chart_legend_div', legendJson["insets"]);
					
					var yAxisJson = CHARTPR.chartPR["yAxisJson"];
					var xAxisJson = CHARTPR.chartPR["xAxisJson"];
					//yaxis
					$('div.jqplot-yaxis-tick').css("background",'transparent');
					if(yAxisJson["visible"])
						$('div.jqplot-yaxis-tick').css("display","");
					else
						$('div.jqplot-yaxis-tick').css("display","none");
					$('div.jqplot-yaxis-tick').css("font-family",yAxisJson["font-family"]);
					$('div.jqplot-yaxis-tick').css("font-size",yAxisJson["font-size"]+"px");
					$('div.jqplot-yaxis-tick').css("color",'#'+yAxisJson["color"]);
					$('div.jqplot-yaxis-tick').css("font-weight",yAxisJson["font-style"]);
					$('div.jqplot-yaxis-tick').css("text-align",yAxisJson["text-align"]);
					if(yAxisJson["background"] == "")
						$('div.jqplot-yaxis-tick').css("background",'transparent');
					else
						$('div.jqplot-yaxis-tick').css("background",'#'+ yAxisJson["background"]);
					$('div.jqplot-yaxis-tick').css("text-shadow",'2px 2px #'+yAxisJson["shadow"]);
					if(yAxisJson["outline"]["visible"]){
						$('div.jqplot-yaxis-tick').css("border-style",yAxisJson["outline"]["style"]);
						if(yAxisJson["outline"]["color"] == "")
							$('div.jqplot-yaxis-tick').css("border-color",'transparent');
						else
							$('div.jqplot-yaxis-tick').css("border-color",'#'+yAxisJson["outline"]["color"]);
						$('div.jqplot-yaxis-tick').css("border-width",yAxisJson["outline"]["width"]+"px");
					}
					$('div.jqplot-yaxis-tick').css("margin-top",yAxisJson["insets"]["top"]+"px");
					$('div.jqplot-yaxis-tick').css("padding-right",yAxisJson["insets"]["right"]+"px");
					$('div.jqplot-yaxis-tick').css("margin-bottom",yAxisJson["insets"]["bottom"]+"px");
					$('div.jqplot-yaxis-tick').css("margin-left",yAxisJson["insets"]["left"]+"px");
					$('#preview_chart div.jqplot-yaxis-tick').css("z-index","9999999");
					$('#main_preview_chart div.jqplot-yaxis-tick').css("z-index","0");
					if(yAxisJson["position"] == "left")
						$('div.jqplot-yaxis-tick').css("margin-right","0px");
					else 
						$('#preview_chart div.jqplot-yaxis-tick').css("margin-right","-30px");
					
					//xaxis
					if(xAxisJson["visible"])
						$('div.jqplot-xaxis-tick').css("display","");
					else
						$('div.jqplot-xaxis-tick').css("display","none");
					$('div.jqplot-xaxis-tick').css("font-family",xAxisJson["font-family"]);
					$('div.jqplot-xaxis-tick').css("font-size",xAxisJson["font-size"]+"px");
					$('div.jqplot-xaxis-tick').css("color",'#'+xAxisJson["color"]);
					$('div.jqplot-xaxis-tick').css("font-weight",xAxisJson["font-style"]);
					$('div.jqplot-xaxis-tick').css("text-align",xAxisJson["text-align"]);
					if(xAxisJson["background"] == "")
						$('div.jqplot-xaxis-tick').css("background",'transparent');
					else
						$('div.jqplot-xaxis-tick').css("background",'#'+ xAxisJson["background"]);
					$('div.jqplot-xaxis-tick').css("text-shadow",'2px 2px #'+xAxisJson["shadow"]);
					if(xAxisJson["outline"]["visible"]){
						$('div.jqplot-xaxis-tick').css("border-style",xAxisJson["outline"]["style"]);
						if(xAxisJson["outline"]["color"] == "")
							$('div.jqplot-xaxis-tick').css("border-color",'transparent');
						else
							$('div.jqplot-xaxis-tick').css("border-color",'#'+xAxisJson["outline"]["color"]);
						$('div.jqplot-xaxis-tick').css("border-width",xAxisJson["outline"]["width"]+"px");
					}
					$('div.jqplot-xaxis-tick').css("padding-top",xAxisJson["insets"]["top"]+"px");
					$('div.jqplot-xaxis-tick').css("margin-right",xAxisJson["insets"]["right"]+"px");
					$('div.jqplot-xaxis-tick').css("margin-bottom",xAxisJson["insets"]["bottom"]+"px");
					$('div.jqplot-xaxis-tick').css("margin-left",xAxisJson["insets"]["left"]+"px");
					$('#preview_chart div.jqplot-xaxis-tick').css("z-index","9999999");
					$('#main_preview_chart div.jqplot-xaxis-tick').css("z-index","");
					if(xAxisJson["position"] == "below")
						$('div.jqplot-xaxis-tick').css("margin-top","0px");
					else
						$('div.jqplot-xaxis-tick').css("margin-top","-30px");
					
					
				}
			
		},
		jPickerBind : function(){
			$('div.jPicker').remove()
			$('span.jPicker').remove()
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
					 
					images:{clientPath:'images/'}
				},function(color, context)
		        {
			          	CHARTPR.finishStyling();
		        });
		},
		
		confirmRestore : function(){
			
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
			jConfirm("This will restore your chart preferences to default view. Are you sure?","Restore Default Chart Preferences",function(confirm){
				if(confirm){
					CHARTPR.fillChartPreference(true);
					}
				else{
					return;
				}
				jQuery.alerts.okButton = 'Ok';
				jQuery.alerts.cancelButton  = 'Cancel';
			});
			$("#popup_container").css("z-index","99999999");
		},
		
		fillChartPreference : function(restore)
		{
			
			if(RC.chartPreferenceType == "local" && RC.chartOperation == "edit")
			{
				if(RC.chartType != "pie")
				{
					$('#liLeader').css("display", "none");
					$('#tabs li').css("height","66px");
					$('#tabs li a').css("padding","33px 0 15px 31px");
				}
				else
				{
					$('#liX').css("display", "none");
					$('#liY').css("display", "none");
					$('#tabs li a').css("padding","44px 0 17px 32px");
					$('#tabs li').css("height","79px");
				}
			}
			
			var chartData = new Object(); 
			if(restore) {
				chartData = CM.getDefaultChartDetails(restore);
				chartData = chartData["chartPreferences"];
			}else {
				if(RC.chartOperation == 'edit'){
					chartData = CM.queryInfo["chartDetail"][RC.chartKey]["chartPreferences"];
					$('#chart_preveiw_title').html(''+CM.queryInfo["chartDetail"][RC.chartKey]["title"]+'');
				}else{
					chartData = CM.globalChartPreferences;
				}
			}
			
			CHARTPR.chartPR = jQuery.extend(true, {},chartData);
			
			
			$('#Background').val(chartData["commonJson"]["background"]);
			$('#WallBackground').val(chartData["commonJson"]["wallBackground"]);
			$('#FloorBackground').val(chartData["commonJson"]["floorBackground"]);
			$('#ClientBackground').val(chartData["commonJson"]["clientBackground"]);
			$('#PlotBackground').val(chartData["commonJson"]["plotBackground"]);
			$('#EmptyChartMessage').val(chartData["commonJson"]["emptyChartMessage"]);
			var temp = chartData["commonJson"]["topColors"];
			for(var i=0;i<=9;i++)
			{
				$('#color'+(i+1)).val(temp[i]);
			}
			
			if(chartData["leaderLineJson"]["visible"]) {
				$('#leaderLinesVisible').prop('checked', true);
				CHARTPR.leaderLineVisible();
			}
			else {
				$('#leaderLinesVisible').prop('checked', false);
				CHARTPR.leaderLineVisible();
			}
			$('#leaderLinesStyle').val(chartData["leaderLineJson"]["style"]);
			$('#leaderLineWidth').val(chartData["leaderLineJson"]["width"]);
			$('#leaderLineColor').val(chartData["leaderLineJson"]["color"]);
			$('#leaderLinesLineStyle').val(chartData["leaderLineJson"]["lineStyle"]);
			$('#leaderLineLength').val(chartData["leaderLineJson"]["lineLength"]);
		
			//chartData.xAxisJson["value"] = "Xaxis";
			if(chartData["xAxisJson"]["visible"] ) {
				$('#xAxisShow').prop('checked', true);
				CHARTPR.axisVisible('x');
			}
			else {
				$('#xAxisShow').prop('checked', false);
				CHARTPR.axisVisible('x');
			}
			$('#xBackground').val(chartData["xAxisJson"]["background"]);
			$('#xShadow').val(chartData["xAxisJson"]["shadow"]);
			$('#legendxPosition').val(chartData["xAxisJson"]["position"]);
			$('#legendxFontTextAlign').val(chartData["xAxisJson"]["text-align"]);
			$('#legendxFontColor').val(chartData["xAxisJson"]["color"]);
			$('#legendxsFontStyle').val(chartData["xAxisJson"]["font-style"]);
			$('#xFontSize').val(chartData["xAxisJson"]["font-size"]);
			console.log("chartData[\"xAxisJson\"][\"titleFontSize\"]: "  + chartData["xAxisJson"]["titleFontSize"]);
			$('#xTitleFontSize').val(chartData["xAxisJson"]["titleFontSize"]);
			$('#xTitleFontColor').val(chartData["xAxisJson"]["titleColor"]);
			
			//$('#xFontSize').val(chartData["xAxisJson"]["font-size"].substring(0,chartData["xAxisJson"]["font-size"].length - 2));
			//$('#legendXFontSizeType').val(chartData["xAxisJson"]["font-size"].substring(chartData["xAxisJson"]["font-size"].length - 2));
			$('#legendxFontFamily').val(chartData["xAxisJson"]["font-family"]);
			
			if(chartData["xAxisJson"]["outline"]["visible"] ) {
				$('#xoutlineVisible').prop('checked', true);
				CHARTPR.outlineVisible('x');
			}
			else {
				$('#xoutlineVisible').prop('checked', false);
				CHARTPR.outlineVisible('x');
			}
			if(chartData["xAxisJson"]["gridline"]["visible"]) {
				$('#xgridlineVisible').prop('checked', true);
				CHARTPR.gridlineVisible('x');
			}
			else {
				$('#xgridlineVisible').prop('checked', false);
				CHARTPR.gridlineVisible('x');
			}
			
			$('#xgridlineStyle').val(chartData["xAxisJson"]["gridline"]["style"] );
			$('#xgridlineWidth').val(chartData["xAxisJson"]["gridline"]["width"] );
			$('#xgridlineColor').val(chartData["xAxisJson"]["gridline"]["color"] );
			
			$('#xoutlineStyle').val(chartData["xAxisJson"]["outline"]["style"] );
			$('#xoutlineWidth').val(chartData["xAxisJson"]["outline"]["width"] );
			$('#xoutlineColor').val(chartData["xAxisJson"]["outline"]["color"] );
			
			$('#xinsetsTop').val(chartData["xAxisJson"]["insets"]["top"]);
			$('#xinsetBottom').val(chartData["xAxisJson"]["insets"]["bottom"]);
			$('#xinsetLeft').val(chartData["xAxisJson"]["insets"]["left"]);
			$('#xinsetRight').val(chartData["xAxisJson"]["insets"]["right"]);
			
//			
			if(chartData["yAxisJson"]["visible"] ) {
				$('#yAxisShow').prop('checked', true);
				CHARTPR.axisVisible('y');
			}
			else {
				$('#yAxisShow').prop('checked', false);
				CHARTPR.axisVisible('y');
			}
			$('#yBackground').val(chartData["yAxisJson"]["background"]);
			$('#yShadow').val(chartData["yAxisJson"]["shadow"]);
			$('#legendyPosition').val(chartData["yAxisJson"]["position"]);
			$('#legendyFontTeytAlign').val(chartData["yAxisJson"]["text-align"]);
			$('#legendyFontColor').val(chartData["yAxisJson"]["color"]);
			$('#legendyFontStyle').val(chartData["yAxisJson"]["font-style"]);
			$('#yFontSize').val(chartData["yAxisJson"]["font-size"]);
			$('#yTitleFontSize').val(chartData["yAxisJson"]["titleFontSize"]);
			$('#yTitleFontColor').val(chartData["yAxisJson"]["titleColor"]);
			//$('#yFontSize').val(chartData["yAxisJson"]["font-size"].substring(0,chartData["yAxisJson"]["font-size"].length - 2));
			//$('#legendyFontSizeType').val(chartData["yAxisJson"]["font-size"].substring(chartData["yAxisJson"]["font-size"].length - 2));
			$('#legendyFontFamily').val(chartData["yAxisJson"]["font-family"]);
			
			if(chartData["yAxisJson"]["outline"]["visible"] ) {
				$('#youtlineVisible').prop('checked', true);
				CHARTPR.outlineVisible('y');
			}
			else {
				$('#youtlineVisible').prop('checked', false);
				CHARTPR.outlineVisible('y');
			}
			if(chartData["yAxisJson"]["gridline"]["visible"] ) {
				$('#ygridlineVisible').prop('checked', true);
				CHARTPR.gridlineVisible('y');
			}
			else {
				$('#ygridlineVisible').prop('checked', false);
				CHARTPR.gridlineVisible('y');
			}
			$('#youtlineStyle').val(chartData["yAxisJson"]["outline"]["style"] );
			$('#youtlineWidth').val(chartData["yAxisJson"]["outline"]["width"] );
			$('#youtlineColor').val(chartData["yAxisJson"]["outline"]["color"] );
			
			$('#ygridlineStyle').val(chartData["yAxisJson"]["gridline"]["style"] );
			$('#ygridlineWidth').val(chartData["yAxisJson"]["gridline"]["width"] );
			$('#ygridlineColor').val(chartData["yAxisJson"]["gridline"]["color"] );
			
			$('#yinsetsTop').val(chartData["yAxisJson"]["insets"]["top"]);
			$('#yinsetBottom').val(chartData["yAxisJson"]["insets"]["bottom"]);
			$('#yinsetLeft').val(chartData["yAxisJson"]["insets"]["left"]);
			$('#yinsetRight').val(chartData["yAxisJson"]["insets"]["right"]);
			
			if(chartData["yAxisJson"]["visible"]) {
				$('#legendyVisible').prop('checked', true);
			}
			else {
				$('#legendyVisible').prop('checked', false);
			}
			
			
//		
			$('#TitleAnchor').val(chartData["titleJson"]["anchor"] );
			$('#TitleFontTextAlign').val( chartData["titleJson"]["text-align"] );
			$('#TitleFontColor').val( chartData["titleJson"]["color"] );
			$('#TitleFontStyle').val( chartData["titleJson"]["font-style"] );;
			$('#TitleFontSize').val(chartData["titleJson"]["font-size"]);
//			$('#TitleFontSizeType').val(chartData["titleJson"]["font-size"].substring(chartData["titleJson"]["font-size"].length - 2) );
			$('#TitleFontFamily').val( chartData["titleJson"]["font-family"] );
			$('#TitleBackground').val( chartData["titleJson"]["background"] );
			
			if(chartData["titleJson"]["outline"]["visible"]) {
				$('#titleoutlineVisible').prop('checked', true);
				CHARTPR.outlineVisible('title');
			}
			else {
				$('#titleoutlineVisible').prop('checked', false);
				CHARTPR.outlineVisible('title');
			}
			$('#titleoutlineStyle').val(chartData["titleJson"]["outline"]["style"] );
			$('#titleoutlineWidth').val(chartData["titleJson"]["outline"]["width"] );
			$('#titleoutlineColor').val(chartData["titleJson"]["outline"]["color"] );
			
			$('#titleinsetsTop').val(chartData["titleJson"]["insets"]["top"] );
			$('#titleinsetBottom').val(chartData["titleJson"]["insets"]["bottom"] );
			$('#titleinsetLeft').val(chartData["titleJson"]["insets"]["left"] );
			$('#titleinsetRight').val(chartData["titleJson"]["insets"]["right"] );
		
			if(chartData["lineChartJson"]["showCurve"])
				$('#showCurve').prop('checked', true);
			else
				$('#showCurve').prop('checked', false);
			$('#curveColor').val(chartData["lineChartJson"]["curveColor"] );
//		
			
			if(chartData["labelJson"]["visible"] ) {
				$('#labelsShow').prop('checked', true);
				CHARTPR.labelVisibility();
			}
			else {
				$('#labelsShow').prop('checked', false);
				CHARTPR.labelVisibility();
			}
			$('#labelsPosition').val(chartData["labelJson"]["position"] );
			$('#labelsFontTextAlign').val(chartData["labelJson"]["text-align"] );
			$('#labelsBackground').val(chartData["labelJson"]["background"] );
			$('#labelsShadow').val(chartData["labelJson"]["shadow"] );
			$('#labelsFontColor').val(chartData["labelJson"]["font-color"]);
			$('#labelsFontStyle').val(chartData["labelJson"]["font-style"]);
			$('#labelFontSize').val(chartData["labelJson"]["font-size"]);
			//$('#labelFontSize').val(chartData["labelJson"]["font-size"].substring(0,chartData["labelJson"]["font-size"].length - 2) );
			//$('#labelFontSizeType').val(chartData["labelJson"]["font-size"].substring(chartData["labelJson"]["font-size"].length - 2) );
			$('#labelsFontFamily').val(chartData["labelJson"]["font-family"] );
			$('#labelsValuePercentActual').val(chartData["labelJson"]["values"]);
			$('#labelsPrefix').val(chartData["labelJson"]["prefix"]);
			$('#labelsSuffix').val(chartData["labelJson"]["suffix"] );
			$('#labelsSeparator').val(chartData["labelJson"]["separator"] );
			if(chartData["labelJson"]["outline"]["visible"]) {
				$('#labeloutlineVisible').prop('checked', true);
				CHARTPR.outlineVisible('label');
			}
			else {
				$('#labeloutlineVisible').prop('checked', false);
				CHARTPR.outlineVisible('label');
			}
			$('#labeloutlineStyle').val(chartData["labelJson"]["outline"]["style"] );
			$('#labeloutlineWidth').val(chartData["labelJson"]["outline"]["width"] );
			$('#labeloutlineColor').val(chartData["labelJson"]["outline"]["color"] );
			
			$('#labelinsetsTop').val(chartData["labelJson"]["insets"]["top"] );
			$('#labelinsetBottom').val(chartData["labelJson"]["insets"]["bottom"] );
			$('#labelinsetLeft').val(chartData["labelJson"]["insets"]["left"] );
			$('#labelinsetRight').val(chartData["labelJson"]["insets"]["right"] );
			
			
//		
			if(chartData.outLineJson["visible"]) {
				$('#outlineVisible').prop('checked', true);
				CHARTPR.outlineVisible('');
			}
			else {
				$('#outlineVisible').prop('checked', false);
				CHARTPR.outlineVisible('');
			}
			$('#outlineStyle').val(chartData.outLineJson["style"] );
			$('#outlineWidth').val(chartData.outLineJson["width"] );
			$('#outlineColor').val(chartData.outLineJson["color"] );
//		
			$('#insetsTop').val(chartData.insetsJson["top"] );
			$('#insetBottom').val(chartData.insetsJson["bottom"] );
			$('#insetLeft').val(chartData.insetsJson["left"] );
			$('#insetRight').val(chartData.insetsJson["right"] );
//			
			if(chartData.legendJson["visible"]) {
				$('#legendShow').prop('checked', true);
				CHARTPR.legendVisibility();
			}
			else {
				$('#legendShow').prop('checked', false);
				CHARTPR.legendVisibility();
			}
			if(chartData.legendJson["visibleTitle"]) {
				$('#legendTitleShow').prop('checked', true);
				CHARTPR.legendTitleVisibility();
			}
			else {
				$('#legendTitleShow').prop('checked', false);
				CHARTPR.legendTitleVisibility();
			}
				
			$('#legendBackground').val(chartData.legendJson["background"] );
			$('#legendFontFamily').val(chartData.legendJson["title-font-family"] );
			$('#legendFontStyle').val(chartData.legendJson["title-font-style"] );
			$('#legendFontColor').val(chartData.legendJson["title-font-color"] );
			$('#legendFontSize').val(chartData.legendJson["title-font-size"] );
			$('#legendPosition').val(chartData.legendJson["position"] );
			$('#legendStretch').val(chartData.legendJson["stretch"] );
			$('#legendAnchor').val(chartData.legendJson["anchor"] );
			if(chartData.legendJson["outline"]["visible"]) {
				$('#legendoutlineVisible').prop('checked', true);
				CHARTPR.outlineVisible('legend');
			}
			else {
				$('#legendoutlineVisible').prop('checked', false);
				CHARTPR.outlineVisible('legend');
			}
			$('#legendoutlineWidth').val(chartData.legendJson["outline"]["width"] );
			$('#legendoutlineStyle').val(chartData.legendJson["outline"]["style"] );
			$('#legendoutlineColor').val(chartData.legendJson["outline"]["color"] );
			$('#legendinsetsTop').val(chartData.legendJson["insets"]["top"] );
			$('#legendinsetBottom').val(chartData.legendJson["insets"]["bottom"] );
			$('#legendinsetLeft').val(chartData.legendJson["insets"]["left"] );
			$('#legendinsetRight').val(chartData.legendJson["insets"]["right"] );
			
			
			CHARTPR.bindFunction();
			//CHARTPR.showChartPreview();
			if(restore)	
				CHARTPR.LoadPRBox(CHARTPR.currentType,$('#'+CHARTPR.currentType));
			else
				CHARTPR.LoadPRBox('common',$('#common'));
		},
		
		outlineVisible : function(prType) {
			if($('#'+prType+'outlineVisible').is(':checked'))
			{
				$('#'+prType+'outlineWidth').removeAttr("disabled");
				$('#'+prType+'outlineStyle').removeAttr("disabled");
				$('#'+prType+'outlineColor').removeAttr("disabled");
			}
			else
			{
				$('#'+prType+'outlineWidth').attr("disabled", "");
				$('#'+prType+'outlineStyle').attr("disabled", "");
				$('#'+prType+'outlineColor').attr("disabled", "");
			}
		},
		
		leaderLineVisible : function()
		{
			if($('#leaderLinesVisible').is(':checked'))
			{
				$('#leaderLineWidth').removeAttr("disabled");
				$('#leaderLinesLineStyle').removeAttr("disabled");
				$('#leaderLinesStyle').removeAttr("disabled");
				$('#leaderLineLength').removeAttr("disabled");
			}
			else
			{
				$('#leaderLineWidth').attr("disabled", "");
				$('#leaderLinesLineStyle').attr("disabled", "");
				$('#leaderLinesStyle').attr("disabled", "");
				$('#leaderLineLength').attr("disabled", "");
			}
		},
		
		labelVisibility : function()
		{
			if($('#labelsShow').is(':checked'))
			{
				$('#labelsPosition').removeAttr("disabled");
				$('#labelsFontTextAlign').removeAttr("disabled");
				$('#labelsFontFamily').removeAttr("disabled");
				$('#labelsFontStyle').removeAttr("disabled");
				$('#labelFontSize').removeAttr("disabled");
				$('#labelsValuePercentActual').removeAttr("disabled");
				$('#labelsSeparator').removeAttr("disabled");
				$('#labelsPrefix').removeAttr("disabled");
				$('#labelsSuffix').removeAttr("disabled");
				$('#labeloutlineVisible').removeAttr("disabled");
				$('#labeloutlineWidth').removeAttr("disabled");
				$('#labeloutlineStyle').removeAttr("disabled");
				$('#labelinsetsTop').removeAttr("disabled");
				$('#labelinsetBottom').removeAttr("disabled");
				$('#labelinsetLeft').removeAttr("disabled");
				$('#labelinsetRight').removeAttr("disabled");
			}
			else
			{
				$('#labelsPosition').attr("disabled", "");
				$('#labelsFontTextAlign').attr("disabled", "");
				$('#labelsFontFamily').attr("disabled", "");
				$('#labelsFontStyle').attr("disabled", "");
				$('#labelFontSize').attr("disabled", "");
				$('#labelsValuePercentActual').attr("disabled", "");
				$('#labelsSeparator').attr("disabled", "");
				$('#labelsPrefix').attr("disabled", "");
				$('#labelsSuffix').attr("disabled", "");
				$('#labeloutlineVisible').attr("disabled", "");
				$('#labeloutlineWidth').attr("disabled", "");
				$('#labeloutlineStyle').attr("disabled", "");
				$('#labelinsetsTop').attr("disabled", "");
				$('#labelinsetBottom').attr("disabled", "");
				$('#labelinsetLeft').attr("disabled", "");
				$('#labelinsetRight').attr("disabled", "");
			}
		},
		
		axisVisible : function(prType) {
			if($('#'+prType+'AxisShow').is(':checked'))
			{
				$('#'+prType+'Background').removeAttr("disabled");
				$('#'+prType+'Shadow').removeAttr("disabled");
				$('#legend'+prType+'Position').removeAttr("disabled");
				$('#legend'+prType+'FontTextAlign').removeAttr("disabled");
				$('#legend'+prType+'FontColor').removeAttr("disabled");
				$('#legend'+prType+'FontStyle').removeAttr("disabled");
				$('#'+prType+'FontSize').removeAttr("disabled");
				$('#legend'+prType+'FontFamily').removeAttr("disabled");
				$('#'+prType+'outlineVisible').removeAttr("disabled");
				$('#'+prType+'outlineWidth').removeAttr("disabled");
				$('#'+prType+'outlineStyle').removeAttr("disabled");
				$('#'+prType+'outlineColor').removeAttr("disabled");
				$('#'+prType+'insetsTop').removeAttr("disabled");
				$('#'+prType+'insetBottom').removeAttr("disabled");
				$('#'+prType+'insetLeft').removeAttr("disabled");
				$('#'+prType+'insetRight').removeAttr("disabled");
			}
			else
			{
				$('#'+prType+'Background').attr("disabled", "");
				$('#'+prType+'Shadow').attr("disabled", "");
				$('#legend'+prType+'Position').attr("disabled", "");
				$('#legend'+prType+'FontTextAlign').attr("disabled", "");
				$('#legend'+prType+'FontColor').attr("disabled", "");
				$('#legend'+prType+'FontStyle').attr("disabled", "");
				$('#'+prType+'FontSize').attr("disabled", "");
				$('#legend'+prType+'FontFamily').attr("disabled", "");
				$('#'+prType+'outlineVisible').attr("disabled", "");
				$('#'+prType+'outlineWidth').attr("disabled", "");
				$('#'+prType+'outlineStyle').attr("disabled", "");
				$('#'+prType+'outlineColor').attr("disabled", "");
				$('#'+prType+'insetsTop').attr("disabled", "");
				$('#'+prType+'insetBottom').attr("disabled", "");
				$('#'+prType+'insetLeft').attr("disabled", "");
				$('#'+prType+'insetRight').attr("disabled", "");
			}
		},
	
		
		
		legendVisibility : function()
		{
			if($('#legendShow').is(':checked'))
			{
				$('#legendBackground').removeAttr("disabled");
				$('#legendTitleShow').removeAttr("disabled");
				$('#legendFontFamily').removeAttr("disabled");
				$('#legendFontStyle').removeAttr("disabled");
				$('#legendFontSize').removeAttr("disabled");
				$('#legendPosition').removeAttr("disabled");
				$('#legendStretch').removeAttr("disabled");
				$('#legendAnchor').removeAttr("disabled");
				$('#legendoutlineVisible').removeAttr("disabled");
				$('#legendoutlineWidth').removeAttr("disabled");
				$('#legendoutlineStyle').removeAttr("disabled");
				$('#legendoutlineColor').removeAttr("disabled");
				$('#legendinsetsTop').removeAttr("disabled");
				$('#legendinsetBottom').removeAttr("disabled");
				$('#legendinsetLeft').removeAttr("disabled");
				$('#legendinsetRight').removeAttr("disabled");
			}
			else
			{
				$('#legendBackground').attr("disabled", "");
				$('#legendTitleShow').attr("disabled", "");
				$('#legendFontFamily').attr("disabled", "");
				$('#legendFontStyle').attr("disabled", "");
				$('#legendFontSize').attr("disabled", "");
				$('#legendPosition').attr("disabled", "");
				$('#legendStretch').attr("disabled", "");
				$('#legendAnchor').attr("disabled", "");
				$('#legendoutlineVisible').attr("disabled", "");
				$('#legendoutlineWidth').attr("disabled", "");
				$('#legendoutlineStyle').attr("disabled", "");
				$('#legendoutlineColor').attr("disabled", "");
				$('#legendinsetsTop').attr("disabled", "");
				$('#legendinsetBottom').attr("disabled", "");
				$('#legendinsetLeft').attr("disabled", "");
				$('#legendinsetRight').attr("disabled", "");
			}
		},
		
		legendTitleVisibility : function()
		{
			if($('#legendTitleShow').is(':checked'))
			{	
				$('#legendFontFamily').removeAttr("disabled");
				$('#legendFontStyle').removeAttr("disabled");
				$('#legendFontSize').removeAttr("disabled");
			}
			else
			{
				$('#legendFontFamily').attr("disabled", "");
				$('#legendFontStyle').attr("disabled", "");
				$('#legendFontSize').attr("disabled", "");
			}
		},
		
		
	setDivOutLine : function(divId, outLineJson){
			
			if(outLineJson["visible"]){
				if(outLineJson["color"]=="")
					$('#'+divId).css('border-color', 'transparent');
				else
					$('#'+divId).css('border-color','#'+outLineJson["color"]);
				$('#'+divId).css("border-style",outLineJson["style"]);
					$('#'+divId).css("border-width",outLineJson["width"]+"px");
			}else{
				$('#'+divId).css("border-style","none");
			}
		},
		setDivInsets : function(divId, insetsJson){
			
			
			$('#'+divId).css("margin-top",insetsJson["top"]+"px");
			$('#'+divId).css("margin-right",insetsJson["right"]+"px");
			$('#'+divId).css("margin-bottom",insetsJson["bottom"]+"px");
			$('#'+divId).css("margin-left",insetsJson["left"]+"px");
		},
		gridlineVisible : function(prType){
			if($('#'+prType+'gridlineVisible').is(':checked'))
			{
				$('#'+prType+'gridlineWidth').removeAttr("disabled");
				$('#'+prType+'gridlineStyle').removeAttr("disabled");
				$('#'+prType+'gridlineColor').removeAttr("disabled");
			}
			else
			{
				$('#'+prType+'gridlineWidth').attr("disabled", "");
				$('#'+prType+'gridlineStyle').attr("disabled", "");
				$('#'+prType+'gridlineColor').attr("disabled", "");
			}
		},
		
		validate : function()
		{
			var flag = true;
			var msg = '';
			var arr = ['','title','x','y','label','legend'];
//			$('#chart_prefernces_main_box input:text').each(function(){
//				if($(this).attr('id')!='EmptyChartMessage' && $(this).attr('id')!='labelsPrefix' && $(this).attr('id')!='labelsSuffix') {
//					if(!$(this).hasClass('applyJPicker')) {
//						if( $(this).val()=='')
//							flag = false;
//					}
//				}
//			});
			
			if($('#leaderLineLength').val()=='')
				msg = ' Leader line length, ';
			for(var i=0;i<arr.length;i++) {
				msg += CHARTPR.validateField(arr[i]);
				
			}
			
//			validateField('');
//			validateField('title');
//			validateField('x');
//			validateField('y');
//			validateField('label');
//			validateField('legend');
			
			if(msg != '')
			{
				msg += 'can not be empty.';
				jAlert(msg);
				$("#popup_container").css("z-index","99999999");
				return false;
			}
			else
			{
				return true;
			}
			
			
		},
		
		validateField : function(category)
		{
			var msg = '';
			var rep = category;
			if(category == 'x')
				rep = 'X-Axis';
			else if (category == 'y')
				rep = 'Y-Axis';
			else if (category == '')
				rep = 'Chart';
			
			
			if($('#'+category+'FontSize').val()=='' && category != '')
				msg += rep + ' font size, ';
			if($('#'+category+'insetsTop').val()=='') 
				msg +=  rep + ' top inset, ';
			if($('#'+category+'insetBottom').val()=='')
				msg += rep + ' bottom inset, ';
			if($('#'+category+'insetRight').val()=='')
				msg += rep + ' right inset, ';
			if($('#'+category+'insetLeft').val()=='')
				msg += rep +' left inset, ';
			
			return msg;
		}
}; 