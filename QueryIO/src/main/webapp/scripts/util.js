Util = {
		
	getLoggedInUserName : function(){
	
		var userName = $('#loggedInUserName').text();
		if(userName == undefined || userName == null || userName == ""){
			RemoteManager.getLoggedInUser(getUserName);
		}
		userName = $('#loggedInUserName').text();
		while(userName == undefined || userName == null || userName == ""){
			userName = $('#loggedInUserName').text();	
		}
		return userName;
	},

	importResource: function(divId, resourcePath, appender) {
		
		$("div#" + divId).load(resourcePath, function(resp, stat, req) {
			if(Navbar.isSetButtonWidth){
				Navbar.isSetButtonWidth = false;
				Navbar.setButtonWidth();
			}
		    if(req.status <= 0 || req.status >= 400) {
				window.location.reload();
				return; 
			}
			if (typeof(appender) != "undefined" && appender != null)
			{
				resp += appender;
				$("div#" + divId).append(resp);
				
			}
		});
		
	},
	
	isContainSpecialCharButNotUnderscore : function(val){
        var regex = new RegExp("^[a-zA-Z0-9_]+$");
        if (!regex.test(val)) {
           return true;
        }else{
            return false;
        }
    },

    isContainSpecialCharButNotUnderscoreAndSlash : function(val)
    {
    	var regex = new RegExp("^[_\/:\\\-.a-zA-Z0-9\\\\]+$");
    	if (!regex.test(val))
    		return true;
    	else
    		return false;    
    },
	
	htmlEncode: function (value) {
  		return $('<div/>').text(value).html();
	},
	
	htmlDecode: function (value) {
  		return $('<div/>').html(value).text();
	},

	addLightbox : function(box_name, boxResourcePath, top, appender)
	{
			Navbar.doAutoRefresh=false;
			Navbar.clearAllTimeout();
			this.addOverlay();
			this.disablePageFocus(true);
			
			if (typeof(top) == "undefined" || top == null)
			{
				 top = (window.pageYOffset + window.innerHeight/6) + "px";
			}
			var lightbox = "<div id='" + box_name + "_cont' style='position:absolute; top:0; left:0; width:100%; height:100%; z-index: 2000001;'><div id='" + box_name + "' style=\"position:relative; margin: 0 auto; top:" + top + "; \"></div></div>";
			
			$("body").append(lightbox);
			
//			$('#' + box_name + '_cont').draggable();
			
			var callFunc = "Util.importResource('" + box_name + "', '" + boxResourcePath + "'";
			if (typeof(appender) != "undefined" && appender != null)
			{
				callFunc += ", '" + appender + "'";
			}
			callFunc += ");";
			$(function() { setTimeout(callFunc, overtime); });
	},
	 removeLightbox : function(box_name)
	{
		this.removeOverlay();
		this.disablePageFocus(false);
		$("div#" + box_name + "_cont").remove();	
	},

	removeOverlay :function ()
	{
		$('#overlay').remove();
	},

	 addOverlay : function()
	{
		var overlay = "<div id='overlay' style='z-index: 2000; display: block;'></div>";
		$("body").append(overlay);
		$(function() { $('#overlay').fadeIn(overtime); });
	},

	disablePageFocus :function (enable)
	{
		var inputFields = document.getElementsByTagName('INPUT');
		var selectFields = document.getElementsByTagName('SELECT');
		var textAreas = document.getElementsByTagName('TEXTAREA');

		for (var i = 0; i < inputFields.length; i++) {
			if(inputFields[i].type == "checkbox")
				continue;
			inputFields[i].disabled = enable;
		}
		for (var i = 0; i < selectFields.length; i++) {
		     selectFields[i].disabled = enable;
		}
		for (var i = 0; i < textAreas.length; i++) {
		     textAreas[i].disabled = enable;
		}
	},
	validateEmail : function(email) { 
	    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	    return re.test(email);
	},
	maskNumericInput: function(event, intOnly) {
		// Backspace, tab, enter, end, home, left, right
		// We don't support the del key in Opera because del == . == 46.
		var controlKeys = [8, 9, 13];
		// IE doesn't support indexOf
		var isControlKey = controlKeys.join(",").match(new RegExp(event.which));
		// Some browsers just don't raise events for control keys. Easy.
		// e.g. Safari backspace.
		if (!event.which || // Control keys in most browsers. e.g. Firefox tab is 0
			(48 <= event.which && event.which <= 57) || (96 <= event.which && event.which <= 105) || // Always 0 through 9
			event.which == 37 || event.which == 38 || event.which == 39 || event.which == 40 || event.which == 46 || 
//			($(event.srcElement).attr("value")) || 
			isControlKey) // Opera assigns values for control keys.
		{ 
			
			return;
		}
		else
		{
			event.preventDefault();
		}	
		
    },
    
    setCookie: function (c_name,value,exdays){
	    var exdate=new Date();
	    exdate.setDate(exdate.getDate() + exdays);
	    var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
	    document.cookie=c_name + "=" + c_value;
    },
    getCookie : function (c_name){
	    var i,x,y,ARRcookies=document.cookie.split(";");
	    for (i=0;i<ARRcookies.length;i++)
	    {
	      x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
	      y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
	      x=x.replace(/^\s+|\s+$/g,"");
	      if (x==c_name)
	        {
	        return unescape(y);
	        }
	      }
    },
    removeSpecialChar : function(element, callBack) {
        var parsed = parseInt(element.value);
        if (parsed != element.value) {

            this.blink(element, '#f00', 500, 2);
            setTimeout(function() {
                element.value = '';
                if (callBack)
                    callBack(true);
            }, 550);

        } else {
            if (callBack)
                callBack(false);
        }
    },
    blink : function(target, fgcolor, interval, times) {
        // Load the current background color 
        var existingFgColor = $(target).css('color');
        for ( var i = 0; i != times; ++i) {
            // Set the new background color 
            setTimeout(function() {
                $(target).css('color', fgcolor);
            }, interval * i * 2);
            // Set it back to old color
            setTimeout(function() {
                $(target).css('color', existingFgColor);
            }, interval * (i * 2 + 1));

        }
    },

	blockSpecialChar : function(event) {
		var regex = new RegExp("^[a-zA-Z0-9]+$");
	    var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
    	if(event.charCode!=0){
		    if (!regex.test(key)) {
		       event.preventDefault();
		       return false;
		    }
	    }
    },
    
    blockSpecialCharButNotUnderScore : function(event, element) {
    	$('#sub_error_span').html('');
    	$('#sub_error_span').css('display','none');
    	var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
    	
    	if(element.value.length>30){
    		event.preventDefault();
    		$('#sub_error_span').text('Value should be less than 30 character.');
    		$('#sub_error_span').css('display','');
    		return false;
    	}
    	if(element.value.length == 0){
    		
    		var regex = new RegExp("^[a-zA-Z]+$");
        	if(event.charCode!=0){
    		    if (!regex.test(key)) {
    		    	$('#sub_error_span').text('First character of value should be alphabet.');
    	    		$('#sub_error_span').css('display','');
    		       event.preventDefault();
    		       return false;
    		    }
    	    }
    	}
    	
    	
    	var regex = new RegExp("^[a-zA-Z0-9_]+$");
    	if(event.charCode!=0){
		    if (!regex.test(key)) {
		       event.preventDefault();
		       return false;
		    }
	    }
    },
    
    blockSpecialCharButNotSpace : function(val) {
		var regex = new RegExp("^[a-zA-Z0-9\\s-_]+$");
//	    var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
	    if (regex.test(val))
	    	return false;
	    else
	    	return true;
    },
    
    isContainSpecialChar : function(val){
    	var regex = new RegExp("^[a-zA-Z0-9]+$");
	    if (!regex.test(val)) {
	       return true;
	    }else{
	    	return false;	
	    }
    },
   
	blockWhiteSpace : function(event) {
		var regex = new RegExp("\\s");
	    var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
	    if (regex.test(key)) {
	       event.preventDefault();
	       return false;
	    }
	},

	isNumericWithBlockOthers : function(event) 
	{
		var regex = new RegExp("^[0-9]+$");
		var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
		if (!regex.test(key))
		{
			event.preventDefault();
			return false;
		}
	},
	
    isContainWhiteSpace : function(val){
    	var regex = new RegExp("\\s");
	    if (regex.test(val)) {
	       return true;
	    }else{
	    	return false;	
	    }	    
   },
   
   
	isInDateFormat : function(val)
	{
		var regex = new RegExp("^[0-2][0-9][0-9][0-9]\-[0-1][0-9]\-[0-3][0-9]$");
		if (regex.test(val)) 
		{
			return true;
		}
		else
		{
			return false;	
		}   
	},
	
	isInHttpFormat : function(val)
	{
		var regex = new RegExp("^(http|https)[:][/][/]","i");
		if (regex.test(val)) 
		{
			return true;
		}
		else
		{
			return false;	
		}   
	},
	
	isInHdfsFormat : function(val)
	{
		var regex = new RegExp("^(hdfs)[:][/][/]","i");
		if (regex.test(val)) 
		{
			return true;
		}
		else
		{
			return false;	
		}   
	},
	    
	isNumeric : function(val)
	{
		var regex = new RegExp("^[0-9]+$");
		if(regex.test(val))
			return true;
		else
			return false;
	},
	
	isNumericPortNumbers : function(portNumbers)
	{
		var isNumeric = true;
		var intRegex = /^\d+$/;
		for (i=0;i<portNumbers.length;i++)
		{
			var str = portNumbers[i];
			if(!intRegex.test(str))
			{
				isNumeric = false;
				break;
			}
		}
		return isNumeric;	
	},
	getUniqueId : function(length)
	{
		length = parseInt(length);
	    var text = "";
	    var possible = "abcdefghijklmnopqrstuvwxyz";

	    for( var i=0; i < length; i++ )
	        text += possible.charAt(Math.floor(Math.random() * possible.length));

	    return text;
	},
	compareJSON : function(x, y){
		
		  var p;
		  for(p in y) {
		      if(typeof(x[p])=='undefined') {return false;}
		  }

		  for(p in y) {
		      if (y[p]) {
		          switch(typeof(y[p])) {
		              case 'object':
		                  if (!y[p].equals(x[p])) { return false; } break;
		              case 'function':
		                  if (typeof(x[p])=='undefined' ||
		                      (p != 'equals' && y[p].toString() != x[p].toString()))
		                      return false;
		                  break;
		              default:
		                  if (y[p] != x[p]) { return false; }
		          }
		      } else {
		          if (x[p])
		              return false;
		      }
		  }

		  for(p in x) {
		      if(typeof(y[p])=='undefined') {return false;}
		  }
		  return true;
	},
	print :function(data) 
    {
        var mywindow = window.open('', 'spread sheet');
        mywindow.document.write('<html><head><title></title>');
        mywindow.document.write('<link rel="stylesheet" type="text/css" href="spreadsheet/jquery.sheet.css" />');
        mywindow.document.write('<link rel="stylesheet" href="styles/jquery-ui-1.8.20.custom.css" type="text/css" />');
//        mywindow.document.write('<link rel="stylesheet" href="styles/jquery-ui-1.8.20.custom.css" type="text/css" media="print" />');
        mywindow.document.write('<link rel="stylesheet" href="styles/print.css" type="text/css" media="print" />');
      
        mywindow.document.write('</head><body >');
        mywindow.document.write(data);
        mywindow.document.write('</body></html>');

        mywindow.print();
        mywindow.close();

        return true;
    },
    
    getCurrentIdDropDown : function(list, message, value)
    {
    	var data = "";
    	
    	if(list == null || list == undefined || list.length == 0)
			data='<option value="' + value + '">' + message + '</option>';
		else
		{
			for(var i=0; i<list.length; i++)
			{
				var node = list[i];
				data += '<option value="'+node+'">'+node+'</option>';
			}			
		}
    	
    	return data;
    },
    deleteAllCookie : function(){
    	 var cookies = document.cookie.split(";");

		  for (var i = 0; i < cookies.length; i++) {
		    	var cookie = cookies[i];
		    	var eqPos = cookie.indexOf("=");
		    	var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
		    	document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
		   }
    },
    
    hex : ["%00", "%01", "%02", "%03", "%04", "%05",
		"%06", "%07", "%08", "%09", "%0a", "%0b", "%0c", "%0d", "%0e",
		"%0f", "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
		"%18", "%19", "%1a", "%1b", "%1c", "%1d", "%1e", "%1f", "%20",
		"%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29",
		"%2a", "%2b", "%2c", "%2d", "%2e", "%2f", "%30", "%31", "%32",
		"%33", "%34", "%35", "%36", "%37", "%38", "%39", "%3a", "%3b",
		"%3c", "%3d", "%3e", "%3f", "%40", "%41", "%42", "%43", "%44",
		"%45", "%46", "%47", "%48", "%49", "%4a", "%4b", "%4c", "%4d",
		"%4e", "%4f", "%50", "%51", "%52", "%53", "%54", "%55", "%56",
		"%57", "%58", "%59", "%5a", "%5b", "%5c", "%5d", "%5e", "%5f",
		"%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67", "%68",
		"%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f", "%70", "%71",
		"%72", "%73", "%74", "%75", "%76", "%77", "%78", "%79", "%7a",
		"%7b", "%7c", "%7d", "%7e", "%7f", "%80", "%81", "%82", "%83",
		"%84", "%85", "%86", "%87", "%88", "%89", "%8a", "%8b", "%8c",
		"%8d", "%8e", "%8f", "%90", "%91", "%92", "%93", "%94", "%95",
		"%96", "%97", "%98", "%99", "%9a", "%9b", "%9c", "%9d", "%9e",
		"%9f", "%a0", "%a1", "%a2", "%a3", "%a4", "%a5", "%a6", "%a7",
		"%a8", "%a9", "%aa", "%ab", "%ac", "%ad", "%ae", "%af", "%b0",
		"%b1", "%b2", "%b3", "%b4", "%b5", "%b6", "%b7", "%b8", "%b9",
		"%ba", "%bb", "%bc", "%bd", "%be", "%bf", "%c0", "%c1", "%c2",
		"%c3", "%c4", "%c5", "%c6", "%c7", "%c8", "%c9", "%ca", "%cb",
		"%cc", "%cd", "%ce", "%cf", "%d0", "%d1", "%d2", "%d3", "%d4",
		"%d5", "%d6", "%d7", "%d8", "%d9", "%da", "%db", "%dc", "%dd",
		"%de", "%df", "%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6",
		"%e7", "%e8", "%e9", "%ea", "%eb", "%ec", "%ed", "%ee", "%ef",
		"%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7", "%f8",
		"%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff"],
    
    encode : function(s)
    {
		var sbuf = "";
		var len = s.length;
		for(var i = 0; i < len; i++) 
		{
			var ch = s.charAt(i);
			if ('A' <= ch && ch <= 'Z')			// 'A'..'Z'
			{ 							
				sbuf = sbuf.concat(ch);
			}
			else if ('a' <= ch && ch <= 'z')	// 'a'..'z' 
			{ 
				sbuf = sbuf.concat(ch);
			}
			else if ('0' <= ch && ch <= '9')	// '0'..'9' 
			{ 
				sbuf = sbuf.concat(ch);
			}
			else if (ch == ' ')					// space 
			{ 
				sbuf = sbuf.concat('+');
			}
			else if (ch == '-'
					|| ch == '_' // unreserved
					|| ch == '.' || ch == '!' || ch == '~' || ch == '*'
					|| ch == '\'' || ch == '(' || ch == ')') 
			{
				sbuf = sbuf.concat(ch);
			} 
			else if (ch <= 0x007f)				// other ASCII 
			{ 
				sbuf = sbuf.concat(Util.hex[ch]);
			} 
			else if (ch <= 0x07FF) 
			{ // non-ASCII <= 0x7FF
				sbuf = sbuf.concat(Util.hex[0xc0 | (ch >> 6)]);
				sbuf = sbuf.concat(Util.hex[0x80 | (ch & 0x3F)]);
			}
			else
			{ // 0x7FF < ch <= 0xFFFF
				sbuf = sbuf.concat(Util.hex[0xe0 | (ch >> 12)]);
				sbuf = sbuf.concat(Util.hex[0x80 | ((ch >> 6) & 0x3F)]);
				sbuf = sbuf.concat(Util.hex[0x80 | (ch & 0x3F)]);
			}
		}
		return sbuf;
	}
};