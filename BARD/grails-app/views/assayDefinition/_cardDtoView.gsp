<r:script>
	var idCounter = 0;
	var idItemsCounter = 0;
	$(function(){
		$("#addNewBtn").button({
			icons: {
				primary: "ui-icon-plus"
			}
		}).click(function(event){
     		$("#dialog_new_card").dialog("open");
   		});

   		$("#dialog_new_card").dialog({
     			height: 300,
     			width: 550,
     			title: "New Card",
				autoOpen: false,
				modal: true,
				buttons: {
					"Add new attribute-value pair": function() {
						$( this ).dialog( "close" );
					},
					Cancel: function() {
						$( this ).dialog( "close" );
					}
				}
     	});

   		$("#dialog_card").dialog({
     			height: 350,
     			width: 350,
     			title: "Edit Card",
				autoOpen: false,
				modal: true
     	});

     	$( "#dialog_confirm_delete_card" ).dialog({
			resizable: false,
			height:250,
			width: 450,
			modal: true,
			autoOpen: false,
			title: "Delete item?",
			buttons: {
				"Delete card": function() {
					$( this ).dialog( "close" );
				},
				Cancel: function() {
					$( this ).dialog( "close" );
				}
			}
		});
	});
</r:script>
<div class="cardView">

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

	<g:set var="cardIdCounter" value="${0}" />
	<g:set var="itemId" value="${0}" />
	<table>
		<tr><td>
        	<g:each in="${cardDtoList}" status="cardIndex" var="card">
        	<g:set var="cardIdCounter" value="${cardIdCounter + 1}" />
             	<g:if test="${(cardIndex % 3) == 0 && cardIndex != 0}">
                	</td></tr><tr><td>
           		</g:if>
            	<g:render template="cardDto" model="['card': card, 'cardId': cardIdCounter]" />
        	</g:each>
    	</td></tr>
	</table>
</div>
