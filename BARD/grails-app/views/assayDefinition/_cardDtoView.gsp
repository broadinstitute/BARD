
<div id="cardView" class="cardView">

	<div id="dialog_card">
		<h3>Edit card</h3>
	</div>

	<div id="dialog_new_card">
		<form>
			<fieldset>
				<table>
					<thead>
						<tr class="ui-widget-header">
							<th>Attribute</th>
							<th>Value</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><input type="text" name="attribute_1" id="attribute_1" class="text ui-widget-content ui-corner-all" /></td>
							<td><input type="text" name="attribute_2" id="attribute_2" class="text ui-widget-content ui-corner-all" /></td>
						</tr>
						<tr>
							<td><input type="text" name="value_1" id="value_1" value="" class="text ui-widget-content ui-corner-all" /></td>
							<td><input type="text" name="value_1" id="value_1" value="" class="text ui-widget-content ui-corner-all" /></td>
						</tr>
					</tbody>
				</table>
			</fieldset>
		</form>
	</div>

	<div id="dialog_confirm_delete_card">
		<p>
		<span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
		This item will be permanently deleted and cannot be recovered. Are you sure?
		</p>
	</div>

	<button id="addNewBtn">Add New Card</button>


    <div id="cardHolder">
        <g:render template="cards" model="[cardDtoList: cardDtoList]"/>
    </div>
</div>
