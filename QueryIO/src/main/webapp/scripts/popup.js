var overtime = 100;

function addLightbox(box_name, boxResourcePath, top, appender)
{
		this.addOverlay();
		this.disablePageFocus(true);
		
		if (typeof(top) == "undefined" || top == null)
		{
			 top = (window.pageYOffset + window.innerHeight/6) + "px";
		}
		var lightbox = "<div id='" + box_name + "_cont' style='position:absolute; top:0; left:0; width:100%; height:100%; z-index: 2000001;'><div id='" + box_name + "' style=\"position:relative; margin: 0 auto; top:" + top + "; \"></div></div>";
				
		$("body").append(lightbox);
		
//		$('#' + box_name + '_cont').draggable();
		
		var callFunc = "importResource('" + box_name + "', '" + boxResourcePath + "'";
		if (typeof(appender) != "undefined" && appender != null)
		{
			callFunc += ", '" + appender + "'";
		}
		callFunc += ");";
		$(function() { setTimeout(callFunc, overtime); });
}

function addOverlay()
{
	var overlay = "<div id='overlay' style='z-index: 2000; display: block;'></div>";
	$("body").append(overlay);
	$(function() { $('#overlay').fadeIn(overtime); });
}

function disablePageFocus(enable)
{
	var inputFields = document.getElementsByTagName('INPUT');
	var selectFields = document.getElementsByTagName('SELECT');
	var textAreas = document.getElementsByTagName('TEXTAREA');

	for (var i = 0; i < inputFields.length; i++) {
		 inputFields[i].disabled = enable;
	}
	for (var i = 0; i < selectFields.length; i++) {
	     selectFields[i].disabled = enable;
	}
	for (var i = 0; i < textAreas.length; i++) {
	     textAreas[i].disabled = enable;
	}
}

function importResource(divId, resourcePath, appender)
{
		$("div#" + divId).load(resourcePath, function(resp, stat, req) {
		    if(req.status <= 0 || req.status >= 400) {
				window.location.reload();
				return; 
			}
			if (typeof(appender) != "undefined" && appender != null)
			{
				resp += appender;
				$("div#" + divId).append(appender);
			}
			
		});
	}
	
function removeLightbox (box_name)
{
	this.removeOverlay();
	this.disablePageFocus(false);
	$("div#" + box_name + "_cont").remove();	
}

function removeOverlay()
{
	$('#overlay').remove();
}

function populateList(list)
{
	
}