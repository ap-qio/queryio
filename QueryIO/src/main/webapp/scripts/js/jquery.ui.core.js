(function(b,h){function j(a,c){var d=a.nodeName.toLowerCase();if("area"===d){var d=a.parentNode,e=d.name;if(!a.href||!e||"map"!==d.nodeName.toLowerCase())return!1;d=b("img[usemap=#"+e+"]")[0];return!!d&&k(d)}return(/input|select|textarea|button|object/.test(d)?!a.disabled:"a"==d?a.href||c:c)&&k(a)}function k(a){return!b(a).parents().andSelf().filter(function(){return"hidden"===b.curCSS(this,"visibility")||b.expr.filters.hidden(this)}).length}b.ui=b.ui||{};b.ui.version||(b.extend(b.ui,{version:"1.8.23",
keyCode:{ALT:18,BACKSPACE:8,CAPS_LOCK:20,COMMA:188,COMMAND:91,COMMAND_LEFT:91,COMMAND_RIGHT:93,CONTROL:17,DELETE:46,DOWN:40,END:35,ENTER:13,ESCAPE:27,HOME:36,INSERT:45,LEFT:37,MENU:93,NUMPAD_ADD:107,NUMPAD_DECIMAL:110,NUMPAD_DIVIDE:111,NUMPAD_ENTER:108,NUMPAD_MULTIPLY:106,NUMPAD_SUBTRACT:109,PAGE_DOWN:34,PAGE_UP:33,PERIOD:190,RIGHT:39,SHIFT:16,SPACE:32,TAB:9,UP:38,WINDOWS:91}}),b.fn.extend({propAttr:b.fn.prop||b.fn.attr,_focus:b.fn.focus,focus:function(a,c){return"number"===typeof a?this.each(function(){var d=
this;setTimeout(function(){b(d).focus();c&&c.call(d)},a)}):this._focus.apply(this,arguments)},scrollParent:function(){var a;a=b.browser.msie&&/(static|relative)/.test(this.css("position"))||/absolute/.test(this.css("position"))?this.parents().filter(function(){return/(relative|absolute|fixed)/.test(b.curCSS(this,"position",1))&&/(auto|scroll)/.test(b.curCSS(this,"overflow",1)+b.curCSS(this,"overflow-y",1)+b.curCSS(this,"overflow-x",1))}).eq(0):this.parents().filter(function(){return/(auto|scroll)/.test(b.curCSS(this,
"overflow",1)+b.curCSS(this,"overflow-y",1)+b.curCSS(this,"overflow-x",1))}).eq(0);return/fixed/.test(this.css("position"))||!a.length?b(document):a},zIndex:function(a){if(a!==h)return this.css("zIndex",a);if(this.length){a=b(this[0]);for(var c;a.length&&a[0]!==document;){c=a.css("position");if("absolute"===c||"relative"===c||"fixed"===c)if(c=parseInt(a.css("zIndex"),10),!isNaN(c)&&0!==c)return c;a=a.parent()}}return 0},disableSelection:function(){return this.bind((b.support.selectstart?"selectstart":
"mousedown")+".ui-disableSelection",function(a){a.preventDefault()})},enableSelection:function(){return this.unbind(".ui-disableSelection")}}),b("<a>").outerWidth(1).jquery||b.each(["Width","Height"],function(a,c){function d(a,c,d,f){b.each(e,function(){c-=parseFloat(b.curCSS(a,"padding"+this,!0))||0;d&&(c-=parseFloat(b.curCSS(a,"border"+this+"Width",!0))||0);f&&(c-=parseFloat(b.curCSS(a,"margin"+this,!0))||0)});return c}var e="Width"===c?["Left","Right"]:["Top","Bottom"],f=c.toLowerCase(),g={innerWidth:b.fn.innerWidth,
innerHeight:b.fn.innerHeight,outerWidth:b.fn.outerWidth,outerHeight:b.fn.outerHeight};b.fn["inner"+c]=function(a){return a===h?g["inner"+c].call(this):this.each(function(){b(this).css(f,d(this,a)+"px")})};b.fn["outer"+c]=function(a,e){return"number"!==typeof a?g["outer"+c].call(this,a):this.each(function(){b(this).css(f,d(this,a,!0,e)+"px")})}}),b.extend(b.expr[":"],{data:b.expr.createPseudo?b.expr.createPseudo(function(a){return function(c){return!!b.data(c,a)}}):function(a,c,d){return!!b.data(a,
d[3])},focusable:function(a){return j(a,!isNaN(b.attr(a,"tabindex")))},tabbable:function(a){var c=b.attr(a,"tabindex"),d=isNaN(c);return(d||0<=c)&&j(a,!d)}}),b(function(){var a=document.body,c=a.appendChild(c=document.createElement("div"));c.offsetHeight;b.extend(c.style,{minHeight:"100px",height:"auto",padding:0,borderWidth:0});b.support.minHeight=100===c.offsetHeight;b.support.selectstart="onselectstart"in c;a.removeChild(c).style.display="none"}),b.curCSS||(b.curCSS=b.css),b.extend(b.ui,{plugin:{add:function(a,
c,d){a=b.ui[a].prototype;for(var e in d)a.plugins[e]=a.plugins[e]||[],a.plugins[e].push([c,d[e]])},call:function(a,c,b){if((c=a.plugins[c])&&a.element[0].parentNode)for(var e=0;e<c.length;e++)a.options[c[e][0]]&&c[e][1].apply(a.element,b)}},contains:function(a,b){return document.compareDocumentPosition?a.compareDocumentPosition(b)&16:a!==b&&a.contains(b)},hasScroll:function(a,c){if("hidden"===b(a).css("overflow"))return!1;var d=c&&"left"===c?"scrollLeft":"scrollTop",e=!1;if(0<a[d])return!0;a[d]=1;
e=0<a[d];a[d]=0;return e},isOverAxis:function(a,b,d){return a>b&&a<b+d},isOver:function(a,c,d,e,f,g){return b.ui.isOverAxis(a,d,f)&&b.ui.isOverAxis(c,e,g)}}))})(jQuery);