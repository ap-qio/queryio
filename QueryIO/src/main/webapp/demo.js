

(function(a){a.alerts={verticalOffset:-75,horizontalOffset:0,repositionOnResize:!0,overlayOpacity:0.01,overlayColor:"#FFF",draggable:!0,okButton:"&nbsp;OK&nbsp;",cancelButton:"&nbsp;Cancel&nbsp;",dialogClass:null,alert:function(b,c,d){null==c&&(c="Alert");a.alerts._show(c,b,null,"alert",function(a){d&&d(a)})},confirm:function(b,c,d){null==c&&(c="Confirm");a.alerts._show(c,b,null,"confirm",function(a){d&&d(a)})},prompt:function(b,c,d,f){null==d&&(d="Prompt");a.alerts._show(d,b,c,"prompt",function(a){f&&
f(a)})},_show:function(b,c,d,f,e){a.alerts._hide();a.alerts._overlay("show");a("BODY").append('<div id="popup_container"><h1 id="popup_title"></h1><div id="popup_content"><div id="popup_message"></div></div></div>');a.alerts.dialogClass&&a("#popup_container").addClass(a.alerts.dialogClass);a("#popup_container").addClass("lightbox");var g=a.browser.msie&&6>=parseInt(a.browser.version)?"absolute":"fixed";a("#popup_container").css({position:g,zIndex:99999,padding:0,margin:0});a("#popup_title").text(b);
a("#popup_content").addClass(f);a("#popup_message").text(c);a("#popup_message").html(a("#popup_message").text().replace(/\n/g,"<br />"));a("#popup_container").css({minWidth:a("#popup_container").outerWidth(),maxWidth:a("#popup_container").outerWidth()});a.alerts._reposition();a.alerts._maintainPosition(!0);switch(f){case "alert":a("#popup_message").after('<div id="popup_panel"><input type="button" value="'+a.alerts.okButton+'" id="popup_ok" /></div>');a("#popup_ok").click(function(){a.alerts._hide();
e(!0)});a("#popup_ok").focus().keypress(function(b){(13==b.keyCode||27==b.keyCode)&&a("#popup_ok").trigger("click")});break;case "confirm":a("#popup_message").after('<div id="popup_panel"><input type="button" value="'+a.alerts.okButton+'" id="popup_ok" /> <input type="button" value="'+a.alerts.cancelButton+'" id="popup_cancel" /></div>');a("#popup_ok").click(function(){a.alerts._hide();e&&e(!0)});a("#popup_cancel").click(function(){a.alerts._hide();e&&e(!1)});a("#popup_ok").focus();a("#popup_ok, #popup_cancel").keypress(function(b){13==
b.keyCode&&a("#popup_ok").trigger("click");27==b.keyCode&&a("#popup_cancel").trigger("click")});break;case "prompt":a("#popup_message").append('<br /><input type="text" size="30" id="popup_prompt" />').after('<div id="popup_panel"><input type="button" value="'+a.alerts.okButton+'" id="popup_ok" /> <input type="button" value="'+a.alerts.cancelButton+'" id="popup_cancel" /></div>'),a("#popup_prompt").width(a("#popup_message").width()),a("#popup_ok").click(function(){var b=a("#popup_prompt").val();a.alerts._hide();
e&&e(b)}),a("#popup_cancel").click(function(){a.alerts._hide();e&&e(null)}),a("#popup_prompt, #popup_ok, #popup_cancel").keypress(function(b){13==b.keyCode&&a("#popup_ok").trigger("click");27==b.keyCode&&a("#popup_cancel").trigger("click")}),d&&a("#popup_prompt").val(d),a("#popup_prompt").focus().select()}if(a.alerts.draggable)try{a("#popup_container").draggable({handle:a("#popup_title")}),a("#popup_title").css({cursor:"move"})}catch(h){}},_hide:function(){a("#popup_container").remove();a.alerts._overlay("hide");
a.alerts._maintainPosition(!1)},_overlay:function(b){switch(b){case "show":a.alerts._overlay("hide");a("BODY").append('<div id="popup_overlay"></div>');a("#popup_overlay").css({position:"absolute",zIndex:99998,top:"0px",left:"0px",width:"100%",height:a(document).height(),background:a.alerts.overlayColor,opacity:a.alerts.overlayOpacity});break;case "hide":a("#popup_overlay").remove()}},_reposition:function(){var b=a(window).height()/2-a("#popup_container").outerHeight()/2+a.alerts.verticalOffset,c=
a(window).width()/2-a("#popup_container").outerWidth()/2+a.alerts.horizontalOffset;0>b&&(b=0);0>c&&(c=0);a.browser.msie&&6>=parseInt(a.browser.version)&&(b+=a(window).scrollTop());a("#popup_container").css({top:b+"px",left:c+"px"});a("#popup_overlay").height(a(document).height())},_maintainPosition:function(b){if(a.alerts.repositionOnResize)switch(b){case !0:a(window).bind("resize",a.alerts._reposition);break;case !1:a(window).unbind("resize",a.alerts._reposition)}}};jAlert=function(b,c,d){a.alerts.alert(b,
c,d)};jConfirm=function(b,c,d){""==c&&(c="Confirm");a.alerts.confirm(b,c,d)};jPrompt=function(b,c,d,f){a.alerts.prompt(b,c,d,f)}})(jQuery);



(function(a){function g(a){for(var d=[];a=m(a);)d[d.length]=a[0];return d}function f(b){return a(b).siblings("tr."+c.childPrefix+b[0].id)}function n(a){a=parseInt(a[0].style.paddingLeft,10);return isNaN(a)?h:a}function k(b,d){var e=a(b.children("td")[c.treeColumn]);e[0].style.paddingLeft=n(e)+d+"px";f(b).each(function(){k(a(this),d)})}function l(b){if(!b.hasClass("initialized")){b.addClass("initialized");var d=f(b);!b.hasClass("parent")&&0<d.length&&b.addClass("parent");if(b.hasClass("parent")){var e=
	a(b.children("td")[c.treeColumn]),g=n(e)+c.indent;d.each(function(){a(this).children("td")[c.treeColumn].style.paddingLeft=g+"px"});if(c.expandable){d='<a href="#" title="'+c.stringExpand+'" style="margin-left: -'+c.indent+"px; padding-left: "+c.indent+'px" class="expander"></a>';c.clickableNodeNames?e.wrapInner(d):e.html(d+e.html());a(e[0].firstChild).click(function(){b.toggleBranch();return!1}).mousedown(function(){return!1});a(e[0].firstChild).keydown(function(a){if(13==a.keyCode)return b.toggleBranch(),
	!1});c.clickableNodeNames&&(e[0].style.cursor="pointer",a(e).click(function(a){"expander"!=a.target.className&&b.toggleBranch()}));if(e=c.persist)try{e="1"==j.get(b.attr("id"))}catch(h){e=!1}e&&b.addClass("expanded");!b.hasClass("expanded")&&!b.hasClass("collapsed")&&b.addClass(c.initialState);b.hasClass("expanded")&&b.expand()}}}}function p(b,d){b.insertAfter(d);f(b).reverse().each(function(){p(a(this),b[0])})}function m(b){for(var d=b[0].className.split(" "),e=0;e<d.length;e++)if(d[e].match(c.childPrefix))return a(b).siblings("#"+
	d[e].substring(c.childPrefix.length));return null}function q(a){if(a.hasClass("expanded"))try{j.set(a.attr("id"),"1")}catch(d){}else try{j.remove(a.attr("id"))}catch(c){}}var c,h,j;a.fn.treeTable=function(b){c=a.extend({},a.fn.treeTable.defaults,b);c.persist&&(j=new Persist.Store(c.persistStoreName));return this.each(function(){a(this).addClass("treeTable").find("tbody tr").each(function(){if(!a(this).hasClass("initialized")){var b=-1==a(this)[0].className.search(c.childPrefix);b&&isNaN(h)&&(h=parseInt(a(a(this).children("td")[c.treeColumn]).css("padding-left"),
	10));!b&&(c.expandable&&"collapsed"==c.initialState)&&a(this).addClass("ui-helper-hidden");(!c.expandable||b)&&l(a(this))}})})};a.fn.treeTable.defaults={childPrefix:"child-of-",clickableNodeNames:!1,expandable:!0,indent:19,initialState:"collapsed",onNodeShow:null,onNodeHide:null,treeColumn:0,persist:!1,persistStoreName:"treeTable",stringExpand:"Expand",stringCollapse:"Collapse"};a.fn.expandAll=function(){a(this).find("tr").each(function(){a(this).expand()})};a.fn.collapseAll=function(){a(this).find("tr").each(function(){a(this).collapse()})};
	a.fn.collapse=function(){return this.each(function(){a(this).removeClass("expanded").addClass("collapsed");c.persist&&q(a(this));f(a(this)).each(function(){a(this).hasClass("collapsed")||a(this).collapse();a(this).addClass("ui-helper-hidden");a.isFunction(c.onNodeHide)&&c.onNodeHide.call(this)})})};a.fn.expand=function(){return this.each(function(){a(this).removeClass("collapsed").addClass("expanded");c.persist&&q(a(this));f(a(this)).each(function(){l(a(this));a(this).is(".expanded.parent")&&a(this).expand();
	a(this).removeClass("ui-helper-hidden");a.isFunction(c.onNodeShow)&&c.onNodeShow.call(this)})})};a.fn.reveal=function(){a(g(a(this)).reverse()).each(function(){l(a(this));a(this).expand().show()});return this};a.fn.appendBranchTo=function(b){var d=a(this),e=m(d),f=a.map(g(a(b)),function(a){return a.id});if(-1==a.inArray(d[0].id,f)&&(!e||b.id!=e[0].id)&&b.id!=d[0].id)k(d,-1*g(d).length*c.indent),e&&d.removeClass(c.childPrefix+e[0].id),d.addClass(c.childPrefix+b.id),p(d,b),k(d,g(d).length*c.indent);
	return this};a.fn.reverse=function(){return this.pushStack(this.get().reverse(),arguments)};a.fn.toggleBranch=function(){a(this).hasClass("collapsed")?a(this).expand():a(this).collapse();return this}})(jQuery);




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



Navbar = {
	currentBackupTabSelected :'',
	isSetButtonWidth:true,
	selectedRack:'',
	selectedHost:'',
	doAutoRefresh:false,
	selectedTabId : '',
	isRefreshPage : false,
	activeMenu:'',
	timeOutArray :[],
	selectedTabName :'',
	selectedChildTab :'',
	selectedGrandChildTab : '',
	currentSelectedView : 1,
	isquickStartOpen:true,
	showSelectedDb : false,
	dataTablePreferredRowCount : new Object(),
	queryManagerDirtyBit : false,
	disableAutoRefresh : false,
	interval : '',
	autoRefreshTimeout : 20000,
	selectedQueryId : '',
	isFromsummaryView:false,
	isViewerView :false,
	isAddNewQuery :false,
	isExecuteQuery:false,
	isEditQuery :false,
	isDataAvailabe:true,
	selectedDatabaseFromMigrationTable : null,
	currentJobTabSelected : '',
	currentScheduleTabSelected : '',
	currentManageTabSelected : '',
	currentHiveTabSelected : '',
	isHiveViewSelected : false,
	hiveViewNameNode : undefined,
	hiveViewSelectedTable : undefined,
	automatedSetup : false,
	promptPasswordChange : false,

	changeTab: function (selectedId, tabName, childtab, grandchildtab) {

		if (!((tabName == 'rm_detail'||tabName == 'nm_detail') && (this.selectedTabId==selectedId)))
		{
			if(this.selectedTabName ==tabName && this.selectedTabId==selectedId&&!this.isRefreshPage &&this.selectedChildTab==childtab&&this.selectedGrandChildTab==grandchildtab){
				return;
			}
		}

		if(Navbar.queryManagerDirtyBit == true)
		{
			jQuery.alerts.okButton = ' Yes ';
			jQuery.alerts.cancelButton  = ' No';
                jConfirm("Some of the fields of Query \"" + DA.selectedQueryId + "\" are modified. Do you want to navigate?",'Query',function(val){
                    if (val== true)
                    {
                    	Navbar.queryManagerDirtyBit = false;
                    	Navbar.changetabImpl(selectedId, tabName, childtab, grandchildtab);


                    }
                });
                jQuery.alerts.okButton = ' Ok ';
    			jQuery.alerts.cancelButton  = ' Cancel';
//            $("#popup_container").css("z-index","99999999");
		}
		else
		{
			Navbar.changetabImpl(selectedId, tabName, childtab, grandchildtab);

		}

	},

	changetabImpl : function(selectedId, tabName, childtab, grandchildtab)
	{

		if($('#loggedInUser').html() == ''){
			 RemoteManager.getLoggedInUser(getUserName);
		}

		if (!((tabName == 'rm_detail'||tabName == 'nm_detail') && (this.selectedTabId==selectedId)))
		{
			if(this.selectedTabName ==tabName && this.selectedTabId==selectedId&&!this.isRefreshPage &&this.selectedChildTab==childtab&&this.selectedGrandChildTab==grandchildtab){
				return;
			}
		}

		if (this.selectedChildTab == 'QuerySpreadSheet')
		{
//			console.log("Remove all textarea, if any.");
			$.sheet.killAll();
		}

		Navbar.doAutoRefresh=false;
		this.isRefreshPage = false;
		this.selectedTabId = selectedId;
		this.selectedTabName = tabName;
		this.selectedChildTab = childtab;
		this.selectedGrandChildTab =grandchildtab;
		$('#refreshViewButton').show();

		$('#queryIONameNodeId').removeAttr('disabled');
		$('#refreshViewButton').attr('onclick','javascript:Navbar.refreshView()');

		document.getElementById("queryIONameNodeIdSpan").style.display = 'none';
		document.getElementById("migrationStatusBtn").style.display = 'none';
		Navbar.isHiveViewSelected = false;

		var source = '';
		var header = 'Dashboard';
		var pathHeader = 'HDFS >> Dashboard';
		if (tabName == 'dashboard')
		{
			if (typeof(childtab) == "undefined")
			{
				header = 'Dashboard';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span> Dashboard</span>'
					+ '<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span>HDFS Overview</span>';
				source = 'resources/dashboard.html';
				Navbar.doAutoRefresh=true;
			}
			else if(childtab == 'MapReduce')
			{
				header = 'Dashboard';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');"> Dashboard</a></span>'
							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span>MapReduce Overview</span>';
				source = 'resources/dashboard-MapReduce.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == "system_monitor")
			{
				header = 'System Monitor';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');"> Dashboard</a></span>'
							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span>System Monitor</span>';
				source = 'resources/status.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == "all_alerts")
			{
				header = 'Alerts';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');"> Dashboard</a></span>'
							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span>System Alerts</span>';
				source = 'resources/alerts_list.html';
			}
			else if (childtab == 'set_alerts')
			{
				header = 'Rules for Alerts';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');"> Dashboard</a></span>'
							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/alerts_configure.html';
			}
			else if(childtab == 'notifications')
			{
				header = 'Notifications';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');"> Dashboard</a></span>'
							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/admin_notifications.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == 'notifications_rules_for_alerts')
			{
				header = 'Notifications';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');"> Dashboard</a></span>'
							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Configure Alerts\',\'dashboard\', \'set_alerts\');"> Rules for Alerts</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/admin_notifications.html';
			}
			selectedId = 'Dashboard';

		}
		else if (tabName == 'Hadoop')
		{
			if(childtab == "HDFS")
			{
				header = 'HDFS Overview';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+ '<img src="images/forward.png" style="height:20px"><span>HDFS</span><img src="images/forward.png" style="height:20px">'
							+ '<img src="images/forward.png" style="height:20px"><span>HDFS Overview</span>';
				source = 'resources/dashboard.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == "MapReduce")
			{
				header = 'MapReduce Overview';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+ '<img src="images/forward.png" style="height:20px"><span>MapReduce</span><img src="images/forward.png" style="height:20px">'
							+ '<img src="images/forward.png" style="height:20px"><span>MapReduce Overview</span>';
				source = 'resources/dashboard-MapReduce.html';
				Navbar.doAutoRefresh=true;
			}
			else if (typeof(childtab) == "undefined")
			{
				header = 'System Monitor';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+ '<img src="images/forward.png" style="height:20px"><span> Hadoop</span><img src="images/forward.png" style="height:20px">'
							+ '<img src="images/forward.png" style="height:20px"><span>' + header + '</span>';
				source = 'resources/status.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == "system_config_HDFS")
			{
				header = "Configure HDFS";
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/system_config.html';
			}
			else if(childtab == 'JournalNode')
			{
				header = 'Journal Node';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/journal_node.html';
			}
			else if(childtab == 'CheckPointNode')
			{
				header = 'CheckPoint Node';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/check_point_node.html';
			}
			else if(childtab == 'system_config_MR')
			{
				header = "Configure "+grandchildtab;
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'MapReduce\');"> MapReduce</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/system_config.html';
			}
			else if(childtab=='ResourceManager')
			{
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'MapReduce\');"> MapReduce</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span>ResourceManager</span>';
				source = 'resources/resource_manager.html';
				Navbar.doAutoRefresh=true;
			}
			else if(childtab=='NodeManager')
			{
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'MapReduce\');"> MapReduce</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span>NodeManager</span>';
				source = 'resources/node_manager.html';
				Navbar.doAutoRefresh=true;
			}
			else if(childtab=='JobBrowser')
			{
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'MapReduce\');"> MapReduce</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span>JobManager</span>';
				source = 'resources/job_browser.html';
				Navbar.doAutoRefresh=true;
			}
			selectedId = 'Hadoop';
		}

		else if(tabName == "data")
		{
			selectedId = 'Data';
			if (typeof(childtab) == "undefined")
			{
				header = 'Data Browser';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> Data Browser</span>';
				source = 'resources/databrowser.html';
				document.getElementById("queryIONameNodeIdSpan").style.display = '';
			}
			else if (childtab == 'manage_hive')
			{
				header = 'Manage Hive ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/manage_hive.html';
				Navbar.doAutoRefresh=true;
			}else if (childtab == 'define_data_tags')
			{
				header = 'Data Tagging';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/data_tagging.html';
				Navbar.doAutoRefresh=true;
			}
//			else if (childtab == 'manage_datasources')
//			{
//				header = 'Manage Data Sources ';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
//					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/manage_datasources.html';
//				Navbar.doAutoRefresh=true;
//			}
			else if (childtab == 'tag_manager')
			{
				header = 'Manage Data Tagging ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'DB_Config\',\'data\', \'db_Config\');"> Manage Datasources</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/tagManager.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == 'data_migration')
			{
				header = 'Data Import/Export ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> Data</span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/data_migration.html';
				Navbar.doAutoRefresh=true;
			}
//			else if(childtab == 'DBConfigMigration')
//			{
//				header = 'Databases Migration Statistics';
//				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'DB_Config\',\'data\',\'manage_datasources\');"> Manage Datasources</a></span><img src="images/forward.png" style="height:20px">'
//							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
//				source = 'resources/database_migration.html';
//			}
			else if(childtab == 'db_Config')
			{
				header = 'Manage Databases';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> Manage Datasources</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/database_config.html';
			}
			else if(childtab == 'data_connections')
			{
				header = 'Manage Datasource Connections';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'DB_Config\',\'data\', \'db_Config\');"> Manage Datasources</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';

				source = 'resources/dataConnections.html';
			}
			else if(childtab == 'AdHocDataDefinition')
			{
				header = 'Manage Hive';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/adHocQuery.html';
			}
			else if(childtab == 'AdHocContent')
			{
				header = 'Manage Content Processor';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Data Migration\',\'data\', \'data_migration\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'DB_Config\',\'data\', \'db_Config\');"> Manage Datasources</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/adhocJobs.html';
			}
		}
		else if (tabName == 'analytics')
		{
			selectedId = 'Analytics';
			Navbar.isViewerView=false;

			if (typeof(childtab) == "undefined")
			{
				header = 'Query Manager';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> ' + header + '</span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryDesigner\',\'analytics\',\'QueryDesigner\');"> Query Designer</span></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryViewer\',\'analytics\',\'QueryViewer\');"> Query Viewer</span></a>'
							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QuerySpreadSheet\',\'analytics\',\'QuerySpreadSheet\');"> SpreadSheet Viewer</span></a>';
				document.getElementById("queryIONameNodeIdSpan").style.display = '';
				source = 'resources/bigQuerySummary.html';
			}
			else if(childtab == 'QueryDesigner')
			{
				header = 'Query Designer';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Analytics\',\'analytics\');"> Query Manager</span></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryViewer\',\'analytics\',\'QueryViewer\');"> Query Viewer</span></a>'
					+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QuerySpreadSheet\',\'analytics\',\'QuerySpreadSheet\');"> SpreadSheet Viewer</span></a>';
				source = 'resources/data_analyzerTab.html';
				document.getElementById("queryIONameNodeIdSpan").style.display = '';
				$('#queryIONameNodeId').attr('disabled','disabled');
				$('#refreshViewButton').hide();

			}
			else if(childtab == 'QueryViewer')
			{
				header = 'Query Viewer';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Analytics\',\'analytics\');"> Query Manager</span></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryDesigner\',\'analytics\',\'QueryDesigner\');"> Query Designer</span></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>'
					+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QuerySpreadSheet\',\'analytics\',\'QuerySpreadSheet\');"> SpreadSheet Viewer</span></a>';
				source = 'resources/bigQueryViewer.html';
				$('#refreshViewButton').hide();
				document.getElementById("queryIONameNodeIdSpan").style.display = '';
			}
			else if(childtab == 'QuerySpreadSheet') {

				if(document.getElementById('bigQueryIds') != undefined){
					var queryId = document.getElementById('bigQueryIds').value;

					Util.setCookie("last-visited-query",queryId,1);
				}

                header = 'SpreadSheet Viewer';
                pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
                	+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Analytics\',\'analytics\');"> Query Manager</span></a><img src="images/forward.png" style="height:20px">'
                	+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryDesigner\',\'analytics\',\'QueryDesigner\');"> Query Designer</span></a><img src="images/forward.png" style="height:20px">'
                    +'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryViewer\',\'analytics\',\'QueryViewer\');"> Query Viewer</span></a>'
                    +'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
                source = 'spreadsheet/spreadSheetView.html';
                $('#refreshViewButton').hide();
                document.getElementById("queryIONameNodeIdSpan").style.display = '';
			}
			else if(childtab == 'QuerySpreadSheetSlick') {
				header = 'Slick SpreadSheet Viewer';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Analytics\',\'analytics\');"> Query Manager</span></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryDesigner\',\'analytics\',\'QueryDesigner\');"> Query Designer</span></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'QueryViewer\',\'analytics\',\'QueryViewer\');"> Query Viewer</span></a>'
					+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/spreadSheet_slick.html';
				$('#refreshViewButton').hide();
				document.getElementById("queryIONameNodeIdSpan").style.display = '';
			}
		}

		else if (tabName == 'nn_summary')
		{
				header = 'Rack Summary' +selectedId;
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> NameNode Summary</span>';
				source = 'resources/nn_summary.html';
				Navbar.doAutoRefresh=true;
				selectedId = 'Hadoop';

		}
		else if (tabName == 'rack_summary')
		{
			header = 'Rack Summary'+selectedId;
			pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'TreeDataNode\',\'dn_summary\');" class="tab_banner" >DataNode Summary</a></span><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span> '+selectedId+'</span>';
			Navbar.doAutoRefresh=true;
			selectedId = 'Hadoop';
			this.selectedRack=selectedId;
			source = 'resources/rack_summary.html';

		}
		else if(tabName=='nn_host')
		{
			header = 'Host Summary'+selectedId;
			pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'nn_summary\');" class="tab_banner">NameNode Summary</a></span><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span> '+selectedId+'</span>';
			this.selectedHost=selectedId;
			source = 'resources/nn_host_summary.html';
			selectedId = 'Hadoop';
			Navbar.doAutoRefresh=true;
		}
		else if (tabName == 'host_summary')
		{
			header = 'Host Summary'+selectedId;
			pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span> '+selectedId+'</span>';
			this.selectedHost=selectedId;
			selectedId = 'Hadoop';
			source = 'resources/host_summary.html';
			Navbar.doAutoRefresh=true;
		}
		else if (tabName == 'nn_detail')
		{
				header = 'NameNode Details : '+selectedId;
				selectedNameNode = selectedId;
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'TreeNameNode\',\'nn_summary\');" class="tab_banner" >NameNode Summary</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\''+childtab+'\',\'nn_host\');" class="tab_banner" >'+childtab+'</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span id="NameNode'+selectedId+'"> '+selectedId+'</span>';

				source = 'resources/nn_detail.html';
				selectedNodeTypeForDetailView = "NameNode";
				selectedId = 'Hadoop';
				Navbar.doAutoRefresh=true;

		}
		else if (tabName == 'dn_summary')
		{
				header = 'DataNode Summary';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> DataNode Summary</span>';
				Navbar.doAutoRefresh=true;
				source = 'resources/dn_summary.html';
		}
		else if (tabName == 'dn_detail')
		{
			if (typeof(grandchildtab) == "undefined")
			{
				header = 'DataNode Details : '+selectedId;
				selectedDataNode = selectedId;

				pathHeader+=$('#pathbar_div').html();

				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'TreeDataNode\',\'dn_summary\');" class="tab_banner" >DataNode Summary</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+selectedId+'</span>';
				source = 'resources/dn_detail_host.html';
				selectedNodeTypeForDetailView = "DataNode";
				selectedId = 'Hadoop';
				Navbar.doAutoRefresh=true;

			}
			else
			{
				header = 'DataNode Volume Details';
				selectedDataNode = selectedId;
				pathHeader+=$('#pathbar_div').html();
				source = 'resources/dn_detail_volume.html';
				Navbar.doAutoRefresh=true;
			}
		}
		else if(tabName=='rm_detail')
		{
				header = 'ResourceManager Details : '+selectedId;
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'MapReduce\');"> MapReduce</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\', \'ResourceManager\');"> ResourceManager</a></span><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px">';
				Navbar.doAutoRefresh=true;
				if(typeof(grandchildtab) != "undefined")
				{
					pathHeader += '<span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'rm_detail\',\''+childtab+'\');" class="tab_banner">'+childtab+'</a></span><img src="images/forward.png" style="height:20px">'
								 +'<img src="images/forward.png" style="height:20px"><span>'+grandchildtab+'</span>';
					selectedNodeTypeForDetailView = "RM";
				}
				else{
					pathHeader+='<span>'+childtab+'</span>';
				}
				source = 'resources/rm_detail.html';
				selectedId = 'Hadoop';
			source = 'resources/rm_detail.html';

		}
		else if(tabName=='nm_detail')
		{
			header = 'NodeManager Details : '+selectedId;
			pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'MapReduce\');"> MapReduce</a></span><img src="images/forward.png" style="height:20px">'
						+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\', \'NodeManager\');"> NodeManager</a></span><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px">';
			Navbar.doAutoRefresh=true;
			if(typeof(grandchildtab) != "undefined")
			{
				pathHeader+='<span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'nm_detail\',\''+childtab+'\');">'+childtab+'</a></span><img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px">'
				+'<span>'+grandchildtab+'</span>';
				selectedNodeTypeForDetailView = "NM";
			}
			else
			{
				pathHeader+='<span>'+childtab+'</span>';
			}
			source = 'resources/nm_detail.html';

			selectedId = 'Hadoop';
		}


		else if (tabName == 'admin')
		{
			if(typeof(childtab) == "undefined")
			{
				header = 'Cluster Setup ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> Admin</span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/automate_cluster_setup.html';
				Navbar.doAutoRefresh=false;
			}
			else if(childtab == 'hosts')
			{
				header = 'Manage Hosts';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/host_config.html';
				Navbar.doAutoRefresh=true;
			}
			else if (childtab == 'queryio_services')
			{
				header = 'QueryIO Services ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span>' +header+'</span>';
				source = 'resources/queryio_services.html';
				Navbar.doAutoRefresh=true;
			}
			else if(childtab == 'BackupAndRecovery')
			{
				header = 'Disaster Recovery';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/backup_recovery.html';
			}
			else if (childtab == 'all_reports')
			{
				header = 'System Reports';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span>' + header + '</span>';
				source = 'resources/reports_all_reports.html';
			}
			else if (childtab == 'users')
			{
				header = 'Users & Groups';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				if(Navbar.promptPasswordChange) {
					source = 'resources/demoUserPasswordChange.html';
				} else {
					source = 'resources/admin_users.html';
				}
			}
			else if(childtab == 'report_schedules')
			{
//				header = childtab == 'all_reports' ? 'Reports':'Schedules';
//				var temp = (childtab == 'all_reports' ? 'All Reports':'Report Schedules');
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><img src="images/forward.png" style="height:20px"><span>System Schedules</span>';
				source = 'resources/reports_' + childtab + '.html';
			}
			else if (childtab == 'manage_datasources')
			{
				header = 'Manage Data Sources ';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
					+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/manage_datasources.html';
				Navbar.doAutoRefresh=true;
			}
			else if(childtab == 'DBConfigMigration')
			{
				header = 'Databases Migration Statistics';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Data</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'DB_Config\',\'admin\',\'manage_datasources\');"> Manage Datasources</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';
				source = 'resources/database_migration.html';
			}
			else if(childtab == 'reportnotifications')
			{
				header = 'Notifications';
				pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Admin\',\'admin\');"> Admin</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'All Reports\',\'admin\', \'all_reports\');"> System Reports</a></span><img src="images/forward.png" style="height:20px">'
							+'<img src="images/forward.png" style="height:20px"><span> '+header+'</span>';

				source = 'resources/admin_notifications.html';
			}
			selectedId = 'Users';
		}
		else if (tabName == 'help') {

				header = 'Help';
	//			pathHeader = 'HDFS >> Admin >> System Config';
				//pathHeader = '<a href="javascript:Navbar.changeTab(\'dashboard\')"><img src="images/home-icon.png"></a> <i><b><a href="#" style="text-decoration: underline; color: #222;" onclick="javascript:Navbar.changeTab(\'admin\')">Admin</a> <a style="color: #222;">Help</a></b></i>';
				return;

		} else {
		}
		if (source != '') {
			$("#rhs_header").html(header);
			$("#pathbar_div").html(pathHeader);
			//$("#surround_box_div #surround_box span#header").html(pathHeader);
			var appender = '<script>Navbar.setButtonWidth();</script>';
			Navbar.isSetButtonWidth=true;
			Util.importResource("service_ref", source);
			Navbar.objectClicked(selectedId);
		}
		if ((tabName == 'nn_detail') || (tabName == 'dn_detail') || (tabName == 'nm_detail') || (tabName == 'rm_detail'))
		{
			var checkChart = false;
			if((tabName == 'nm_detail') || (tabName == 'rm_detail')){
				if(typeof(childtab) != "undefined"){
					checkChart = true;
				}
			}
			else{
				checkChart = true;
			}
			if(checkChart){
				document.getElementById("chartInterval").getElementsByTagName("option")[0].selected = true;
				document.getElementById("intervalHeader").style.display = '';
			}
		}
		else
		{
			document.getElementById("intervalHeader").style.display = 'none';

		}
		$('#refreshViewButton').css("display","block");

//		Navbar.doAutoRefreshView();

	},


	doAutoRefreshView : function(){
//		for(var i=0;i<Navbar.timeOutArray.length;i++){
//			clearTimeout(Navbar.timeOutArray[i]);
//		}
//
//		if(Navbar.doAutoRefresh && !Navbar.disableAutoRefresh){
//			Navbar.interval = setTimeout('Navbar.refreshView()',Navbar.autoRefreshTimeout);
//			Navbar.timeOutArray.push(Navbar.interval);
//		}
	},

	clearAllTimeout : function(){
		for(var i=0;i<Navbar.timeOutArray.length;i++){
			clearTimeout(Navbar.timeOutArray[i]);
		}
		Navbar.timeOutArray=[];
	},

	objectClicked: function (tdId)
	{
		this.activeMenu=tdId;
		$("#menu_ul li").removeClass("menuactive");
		var id = tdId+'_li';
		$('#'+id).addClass("menuactive");
	},

	mouseHover: function (id, isHover)
	{
		if (id.style.backgroundColor != "#535353")	// Equivalent to #ECECEC
		{
			if (isHover)
				id.style.backgroundColor = "#535353";
			else
				id.style.backgroundColor = "rgb(255, 255, 255)";	// White color
		}
	},

	changeInterval: function(interval)
	{
		Util.setCookie("TimeInterval",interval,1);
		if (selectedNodeTypeForDetailView == 'NameNode')
			NN_Detail.changeInterval(interval);
		else if(selectedNodeTypeForDetailView == 'RM')
			Resource_Manager.changeInterval(interval);
		else if (selectedNodeTypeForDetailView=='NM')
			Node_Manager.changeInterval(interval);
		else
			DN_Host.changeInterval(interval);
	},

	ready: function()
	{
		var totalNodeCount = $('#totaldn').text();
		Navbar.refreshNavBar();
	},

	fillNameNodeDropDown : function(list)
	{
		var opt ='';
		if(list!=null && list!=undefined){

			for(var i=0;i<list.length;i++){
				var node = list[i];
				opt+='<option value="'+node.id+'">'+node.id+'</option>';
			}
		}
		$('#queryIONameNodeId').html(opt);

		if (Navbar.automatedSetup)
		{
			AutoCluster.continueAutomatedSetup();
		}

		if(Navbar.promptPasswordChange) {
			Navbar.changeTab('Users','admin','users');
		} else if (isFirstTimeCluster)
		{
			Navbar.changeTab('Data Migration','data', 'data_migration');
		}
	},

	changeQueryIONameNodeId : function(namenodeId)
	{
		if (this.selectedTabId == "Data Browser")
		{
			$('#db_namenode').val(namenodeId);
			DataBrowser.showDataOfNameNode();
		}
		else if (this.selectedTabId == "DataAnalyzer")
		{
			BQS.ready();
		}
		else if (this.selectedTabId == "BigQueryTables")
		{
			BQT.ready();
		}
		else if (this.selectedTabId == "QueryDesigner"||this.selectedTabId == "QueryViewer")
		{
			DAT.ready();
		}
		else if (this.selectedTabId == "QuerySpreadSheet")
		{
			SPS.ready();
		}
	},

	fillAllQueryIONameNode :function()
	{
		//get all host ip and id for populate hostForNode selection box.
		RemoteManager.getNonStandByNodes(Navbar.fillNameNodeDropDown);
	},


	setAutoRefreshTimeout : function()
	{
		var value = $("#refreshTimeout").val();
		Navbar.autoRefreshTimeout = parseInt(value)*1000;
		Navbar.interval = setTimeout('Navbar.refreshView()',Navbar.autoRefreshTimeout);
	},

	toggleRefresh : function(value)
	{
		if(value)
		{
			$("#pauseRefresh").show();
			$("#resumeRefresh").hide();
			$("#autoRefreshSpan").show();
			Navbar.disableAutoRefresh = false;
			Navbar.interval = setTimeout('Navbar.refreshView()',Navbar.autoRefreshTimeout);
		}
		else
		{
			$("#pauseRefresh").hide();
			$("#resumeRefresh").show();
			$("#autoRefreshSpan").hide();
			Navbar.disableAutoRefresh = true;
			clearTimeout(Navbar.interval);
		}
	},

	refreshNavBar : function()
	{

		$('#nntree').remove();
		$('#dntree').remove();
		$('#Reports').remove();
		$('#Alerts').remove();
		$('#Admin').remove();
		$('#menu_container').html("");
		$('#menu_container').load('pages/menu.jsp');
		Navbar.fillAllQueryIONameNode();
	},
	setActiveTab : function(){
		this.objectClicked(this.activeMenu);


	},
	loadNameNodeNavBar : function(){
		RemoteManager.getNameNodes(Navbar.fillAllNameNode);
	},

	fillAllNameNode : function(list)
	{
		var node;
		var content='';
		for (var i = 0; i < list.length; i = i+1)
		{
			node = list[i];
			content+='<li><a id="nn'+node.id+'" href="javascript:Navbar.changeTab(\''+node.id+'\',\'nn_detail\');"  >'+node.id+'</a></li>';
		}
		content+='';
		$("ul#nntree").html(content);
	},



	refreshView : function()
	{
		if (Navbar.selectedTabId=='Status')
		{
			var interval = document.getElementById('timeInterval').value;
			StatusManager.getStatus(interval,Status.fillStatus);
			return;
		}else if (Navbar.selectedTabId=='Data')
		{
			DataBrowser.refreshDataTable();
			return;
		}
		else if (Navbar.selectedTabName=='chartMagnified')
		{

			var interval = document.getElementById("chartInterval").value;
			Navbar.changeInterval(interval);
			return;
		}else if($("#rhs_header").text()=='Dashboard'){
			Navbar.isRefreshPage = true;
//			/this.selectedTabId = ''
			Navbar.changeTab(Navbar.selectedTabId, Navbar.selectedTabName, Navbar.selectedChildTab, Navbar.selectedGrandChildTab);
			return;
		}else if(Navbar.selectedTabName=="rack_summary"){
			Navbar.isRefreshPage = true;
			Navbar.changeTab(Navbar.selectedRack, Navbar.selectedTabName);
			return;
		}
		Navbar.isRefreshPage = true;
		Navbar.changeTab(Navbar.selectedTabId, Navbar.selectedTabName, Navbar.selectedChildTab, Navbar.selectedGrandChildTab);
	},
	helpClicked : function(){
		window.open("docs/common/index.html");
		return;
	},
//	setDataNodePathbar : function(array){
//		var pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
//			+'<img src="images/forward.png" style="height:20px"><span> HDFS Node</span><img src="images/forward.png" style="height:20px">'
//			+'<img src="images/forward.png" style="height:20px"><a href="javascript:Navbar.changeTab(\'TreeDataNode\',\'dn_summary\');" class="tab_banner" ><span>DataNode Summary</span></a><img src="images/forward.png" style="height:20px">'
//			+'<img src="images/forward.png" style="height:20px"><span> '+array[0]+'</span><img src="images/forward.png" style="height:20px">'
//			+'<img src="images/forward.png" style="height:20px"><span> '+array[1]+'</span><img src="images/forward.png" style="height:20px">'
//			+'<img src="images/forward.png" style="height:20px"><span> '+array[2]+'</span>';
//		$("#pathbar_div").html(pathHeader);
//	},
//	setHostPathbar : function(array){
////		var pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
////			+'<img src="images/forward.png" style="height:20px"><a href="javascript:Navbar.changeTab(\'TreeDataNode\',\'dn_summary\');" class="tab_banner" ><span>DataNode Summary</span></a><img src="images/forward.png" style="height:20px">'
////			+'<img src="images/forward.png" style="height:20px"><span><a id="rack'+array[0]+'" href="javascript:Navbar.changeTab(\''+array[0]+'\',\'rack_summary\');"  > '+array[0]+'</a></span><img src="images/forward.png" style="height:20px">'
////			+'<img src="images/forward.png" style="height:20px"><span> '+array[1]+'</span><img src="images/forward.png" style="height:20px">'
////
////		$("#pathbar_div").html(pathHeader);
//	},
	setDataNodePathbar : function(array){
		var pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'TreeDataNode\',\'dn_summary\');" class="tab_banner" >DataNode Summary</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a id="rack'+array[0]+'" href="javascript:Navbar.changeTab(\''+array[0]+'\',\'rack_summary\');"  > '+array[0]+'</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a id="rack'+array[1]+'" href="javascript:Navbar.changeTab(\''+array[1]+'\',\'host_summary\');"  > '+array[1]+'</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span id="DataNode'+array[2]+'"> '+array[2]+'</span>'

		$("#pathbar_div").html(pathHeader);
	},
	showHostPath : function(array){
		var pathHeader = '<a href="javascript:Navbar.changeTab(\'Dashboard\',\'dashboard\');" class="tab_banner" ><img src="images/home-icon.png"></a><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\');"> Hadoop</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'Hadoop\',\'Hadoop\',\'HDFS\');"> HDFS</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a href="javascript:Navbar.changeTab(\'TreeDataNode\',\'dn_summary\');" class="tab_banner" >DataNode Summary</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span><a id="rack'+array[0]+'" href="javascript:Navbar.changeTab(\''+array[0]+'\',\'rack_summary\');" class="tab_banner" > '+array[0]+'</a></span><img src="images/forward.png" style="height:20px">'
			+'<img src="images/forward.png" style="height:20px"><span id="host'+array[1]+'"> '+array[1]+'</span>'

		$("#pathbar_div").html(pathHeader);
	},
	gettingStarted: function()
	{
//		window.open('resources/getting_Started.html','location=no','width=850,height=580,left=200,top=120');
//		var w = 850;
//		var h = 580;
//		var left = (screen.width/2)-(w/2);
//		var top = (screen.height/2)-(h/2);
//		window.open('resources/getting_Started.html','location=no','width=' + w + ', height=' + h + ', top=' + top + ', left=' + left + "'");
//		Util.addLightbox("GS","resources/getting_Started.html",null,null);
		window.open("docs/hadoop-big-data-tutorial/index.html", 'gettingStarted', 'toolbar=no,location=no,titlebar=no,status=no,menubar=no,titlebar=no,fullscreen=no,width=1500,height=750,top=100,left=100');
		return;
	},

	changeView: function(view)
	{
		Navbar.currentSelectedView = view;

		switch(view){
		case 1:
			$('#page1').css('display','');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page5').css('display','none');
			$('#page6').css('display','none');
			$('#page7').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');

			$('#nextButton').removeAttr('disabled');
			$('#prevButton').attr('disabled','disabled');
			break;
		case 2:
			$('#page2').css('display','');
			$('#page1').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page5').css('display','none');
			$('#page6').css('display','none');
			$('#page7').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 3:
			$('#page3').css('display','');
			$('#page2').css('display','none');
			$('#page1').css('display','none');
			$('#page4').css('display','none');
			$('#page5').css('display','none');
			$('#page6').css('display','none');
			$('#page7').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 4:
			$('#page4').css('display','');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page1').css('display','none');
			$('#page5').css('display','none');
			$('#page6').css('display','none');
			$('#page7').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 5:
			$('#page5').css('display','');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page6').css('display','none');
			$('#page7').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 6:
			$('#page6').css('display','');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page5').css('display','none');
			$('#page7').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 7:
			$('#page7').css('display','');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page6').css('display','none');
			$('#page5').css('display','none');
			$('#page8').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 8:
			$('#page8').css('display','');
			$('#page7').css('display','none');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page6').css('display','none');
			$('#page5').css('display','none');
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 9:
			$('#page9').css('display','');
			$('#page11').css('display','none');
			$('#page10').css('display','none');
			$('#page8').css('display','none');
			$('#page7').css('display','none');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page6').css('display','none');
			$('#page5').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 10:
			$('#page9').css('display','none');
			$('#page11').css('display','none');
			$('#page10').css('display','');
			$('#page8').css('display','none');
			$('#page7').css('display','none');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page6').css('display','none');
			$('#page5').css('display','none');
			$('#nextButton').removeAttr('disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		case 11:
			$('#page9').css('display','none');
			$('#page11').css('display','');
			$('#page10').css('display','none');
			$('#page8').css('display','none');
			$('#page7').css('display','none');
			$('#page2').css('display','none');
			$('#page3').css('display','none');
			$('#page4').css('display','none');
			$('#page1').css('display','none');
			$('#page6').css('display','none');
			$('#page5').css('display','none');
			$('#nextButton').attr('disabled','disabled');
			$('#prevButton').removeAttr('disabled');
			break;
		}
	},

	nextView : function()
	{
		if(Navbar.currentSelectedView < 11)
			Navbar.changeView(Navbar.currentSelectedView + 1);
	},

	previousView : function()
	{
		if(Navbar.currentSelectedView > 1)
			Navbar.changeView(Navbar.currentSelectedView - 1);
	},

	closeBox: function(){
		window.close();
	},

	getPreferredRowCountForDataTable : function(tableName)
	{
		var selector = "[name=" + tableName + "_length]";
	 	$($(selector)[0]).change(function()
	   			{
	   				Navbar.dataTablePreferredRowCount[tableName] = $($(selector)[0]).val();
	   			});

	 	if(Navbar.dataTablePreferredRowCount[tableName] == undefined || Navbar.dataTablePreferredRowCount[tableName] == null || Navbar.dataTablePreferredRowCount[tableName] == "" )
		{
			Navbar.dataTablePreferredRowCount[tableName] = $($(selector)[0]).val();
		}
		$($(selector)[0]).val(Navbar.dataTablePreferredRowCount[tableName]);
	},
	showServerLog : function(){
		window.open("GetServerLog?file-type=log");
	},

	setButtonWidth: function()
	{
		var allWidth = new Array();
		var buttonArray = $("#service_ref .button");
		for(var j=0;j<buttonArray.length;j++)
		{
			var e=buttonArray[j];
			if(e.id=='createFSimage')continue;
 			allWidth.push(e.offsetWidth);

		}

		if (allWidth.length>0)
		{

			var largest = Math.max.apply(Math, allWidth);
			largest = largest + 4;
			largest = largest + 'px';

			for(var i=0;i<buttonArray.length;i++)
			{
				buttonArray[i].style.width=largest;
			}
		}

//		console.log("Navbar.setButtonWidth");

		$('#createFSimage').css('width', '90px');
		$('#createFSimage').css('background-color', '#5DA423');
		$('#createFSimage').css('border', '1px solid #396516');
	},

	readmeClicked : function(){
		window.open("README.html");
		return;
	},
};


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
//popup.js
var overtime=100;
function addLightbox(b,d,c,e){this.addOverlay();this.disablePageFocus(!0);if("undefined"==typeof c||null==c)c=window.pageYOffset+window.innerHeight/6+"px";c="<div id='"+b+"_cont' style='position:absolute; top:0; left:0; width:100%; height:100%; z-index: 2000001;'><div id='"+b+"' style=\"position:relative; margin: 0 auto; top:"+c+'; "></div></div>';$("body").append(c);var a="importResource('"+b+"', '"+d+"'";"undefined"!=typeof e&&null!=e&&(a+=", '"+e+"'");a+=");";$(function(){setTimeout(a,overtime)})}
function addOverlay(){$("body").append("<div id='overlay' style='z-index: 2000; display: block;'></div>");$(function(){$("#overlay").fadeIn(overtime)})}function disablePageFocus(b){for(var d=document.getElementsByTagName("INPUT"),c=document.getElementsByTagName("SELECT"),e=document.getElementsByTagName("TEXTAREA"),a=0;a<d.length;a++)d[a].disabled=b;for(a=0;a<c.length;a++)c[a].disabled=b;for(a=0;a<e.length;a++)e[a].disabled=b}
function importResource(b,d,c){$("div#"+b).load(d,function(e,a,d){0>=d.status||400<=d.status?window.location.reload():"undefined"!=typeof c&&null!=c&&$("div#"+b).append(c)})}function removeLightbox(b){this.removeOverlay();this.disablePageFocus(!1);$("div#"+b+"_cont").remove()}function removeOverlay(){$("#overlay").remove()}function populateList(b){};