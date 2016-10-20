<!-- 
<div id="modal" style="border:3px solid black; background-color:#9999ff; padding:25px; font-size:150%; text-align:center; display:none;">
	This is a modal popup!<br><br>
	<input type="button" value="OK" onClick="Popup.hide('modal')">
</div>

<a href="#" onclick="Popup.showModal('modal');return false;">
	Show Modal Popup
</a>
<br>
<a href="#" onclick="Popup.showModal('modal',null,null,{'screenColor':'#99ff99','screenOpacity':.6});return false;">
	Show Modal Popup With A Custom Screen
</a>
-->

<div id="host_operation" class="lightbox">
    <table id="host_operation_table" class="outer" style="">
        <tbody>
        	<tr>
            	<td id="hdr_td" style="padding: 0pt; ">
            		<h4 style="font-weight: normal;">Status</h4>
            	</td>
        	</tr>
        	<tr>
            	<td id="msg_td">
                	<div class="instructional" style="text-align: center; width: 100%;">
                		<table align="center" width="100%" style="border-collapse: collapse; text-align: center;">
							<tr>
								<th><span id="popup.component">Host</span></th>
								<th><span id="popup.message">Message</span></th>
								<th><span id="popup.status">Status</span></th>
								<th><span id="popup.image">	</span></th>
							</tr>
							<tr id="pop.pattern" style="display:none;">
								<td><span id="popup.host"></span></td>
								<td><span id="popup.message"></span><br><div id="log_div" style="display: none;"></div></td>
								<td><span id="popup.status"></span></td>
								<td>
									<span id="popup.image.fail" style="display:none;"><img src="images/Fail_img.png" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>
									<span id="popup.image.success" style="display:none;"><img  src="images/Success_img.png" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>
									<span id="popup.image.processing"><img  src="images/process.gif" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/></span>
								</td>
								
							</tr>
							<tr>
							<td colspan="4">
								<input type="button" disabled="true"  class="buttonAdmin" id="ok.popup" value="Ok" onclick="javascript:closePopUpBox();">
								<!-- <input type="button" disabled="true" class="buttonAdmin" id="viewreason.popup" value="Reason For Failure" onclick="javascript:showReasonForFailure();" style="margin: 0 0 0 10pt;">-->
							</td>
							</tr>
						</table>
					</div>
            	</td>
        	</tr>
        	
			
		
    	</tbody>
	</table>
	<script type="text/javascript">
		fillPopUp(true);
		loadGIF();
		function loadGIF(){
			$('#popup.image.processing').html('');
			$('#popup.image.processing').html('<img  src="images/process.gif" style="height: 12pt; margin-right: 1pt; margin-top: 1pt;"/>');
			
			//Navbar.changeTab('MapReduce','MapReduce', 'NodeManager');
		}
	</script>
</div>