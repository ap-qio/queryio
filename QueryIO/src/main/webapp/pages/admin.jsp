
<script language="javascript">
	
		var previousElement = "";
		var previousHrefElement ="";
		
		function subTabClicked(tdId)
		{
			changeCSSClass(tdId);

			if (tdId == "Hosts")
			{
				$("#currentSubTab").load("pages/hosts/hostList.jsp");
			}
			if (tdId == "Users")
			{
				$("#currentSubTab").load("pages/users/userList.jsp");
			}
		}

		function changeCSSClass(tdId)
		{

			var element = document.getElementById(tdId);
			if (tdId != null && tdId != "" && element != null)
			{
				var hrefElement = document.getElementById('a'+tdId);

				changeElementCSS (element,"outtertab_active_td", "none");	
				changeElementCSS (hrefElement,"tab_active_link", "none");
					
				if ( previousElement != "" && previousElement != tdId)
				{
					changeElementCSS (document.getElementById(previousElement),"outtertab_inactive_td", "none");	
					changeElementCSS (document.getElementById(previousHrefElement),"tab_inactive_link", "none");
				}
		
				previousElement = tdId;
				previousHrefElement = 'a'+tdId;
			}
		}

		function changeElementCSS( element, cssClass, textDecor )
		{
			if ( element != null )
			{
				element.className = cssClass;
				element.style.textDecoration = textDecor;
			}
		}
	</script>

<div>
	
	<table width="50%" height="24" style="border-collapse: collapse;">
		<tr>
			<td width="50%" id="Hosts" class="outtertab_inactive_td" style="border-top-left-radius: 8px; border-bottom-left-radius: 8px;" onmouseover="javascript:mouseHover(this, true);" onmouseout="javascript:mouseHover(this, false);">
				<a id="Hosts" href="javascript:subTabClicked('Hosts');" class="tab_inactive_link">
					Hosts
				</a>
		 	</td>
			<td class="tab_vseparator_td">
			</td>
			<td width="50%" id="Users" class="outtertab_inactive_td" style="border-top-right-radius: 8px; border-bottom-right-radius: 8px;" onmouseover="javascript:mouseHover(this, true);" onmouseout="javascript:mouseHover(this, false);">
		 		<a id="Users" href="javascript:subTabClicked('Users');" class="tab_inactive_link">
					Users
				</a>
		 	</td>
		</tr>
	</table>
	
	<table align="center" width="100%" style="border-collapse: collapse; margin-top: 10pt">
		<tr>
			<td>
				<div id="currentSubTab">
				</div>
			</td>
		</tr>
	</table>
	
	<script language="javascript">
	
		function mouseHover(id, isHover)
		{
			if(id.className!='outtertab_active_td')
			{
				if(isHover)	
					id.className='mouseIn';
				else
					id.className='outtertab_inactive_td';
			}
		}
	
		subTabClicked('Hosts');
	</script>
</div>