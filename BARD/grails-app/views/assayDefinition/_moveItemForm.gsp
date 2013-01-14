<%@ page import="bard.db.registration.*" %>

<div class="well well-small">
	Select the card where the item will be transferred to:
</div>
<form id="move_item_form" class="form-horizontal" >
	<div class="control-group">
    	<label class="control-label" for="edit_card_name">Card Name:</label>
		<div class="controls">
			<g:select name="cardId" from="${instance.contexts}" optionKey="id" optionValue="contextName" noSelection="['': '']"/>
		</div>
   </div>
   <input type="hidden" id="assayContextItemId" name="assayContextItemId" value="${itemId}"/>
   <input type="hidden" id="assayId" name="assayId" value="${assayId}"/>
</form>