<div id="cardView" class="cardView">
	<div id="dialog_new_card">
		<form id="new_card_form">
				<table>
					<tbody>
						<tr>
							<td>Name:</td>
							<td><input type="text" id="card_name" name="card_name"size="30" class="text ui-widget-content ui-corner-all" /></td>
							<td><input type="hidden" id="assay_id" name="assay_id" value="${assayId}"/></td>
						</tr>						
					</tbody>
				</table>
		</form>
	</div>

	<div id="dialog_confirm_delete_item">
		<p>
		<span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
		This item will be permanently deleted and cannot be recovered. Are you sure?
		</p>
	</div>
	
	<div id="dialog_confirm_delete_card">
		<p>
		<span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
		This card will be permanently deleted and cannot be recovered. Are you sure?
		</p>
	</div>

	<button id="addNewBtn">Add New Card</button>

    <div id="cardHolder">
        <g:render template="cards" model="[cardDtoList: cardDtoList]"/>
    </div>
</div>
