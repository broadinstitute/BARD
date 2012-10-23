<div id="cardView" class="cardView" class="row-fluid">

    <div class="span12">

        <div class="row-fluid">
            <div class="span12"><button id="addNewBtn" class="btn btn-primary">Add New Card</button></div>
        </div>

        <div class="row-fluid">
            <div id="cardHolder" class="span12">
                <g:render template="cards" model="[cardDtoMap: cardDtoMap, assayId: assayId]"/>
            </div>
        </div>

        <div id="dialog_edit_card">
            <form id="edit_card_form" class="form-horizontal" >
                <div class="control-group">
                    <label class="control-label" for="edit_card_name">Name:</label>

                    <div class="controls">
                        <input type="text" id="edit_card_name" name="edit_card_name" size="${bard.db.registration.AssayContext.CONTEXT_NAME_MAX_SIZE}" placeholder="Enter Card Name" >
                    </div>
                </div>
                <input type="hidden" id="assayContextId" name="assayContextId" value=""/>
                <input type="hidden" id="assayId" name="assayId" value="${assayId}"/>
            </form>
        </div>
		
		<div id="dialog_move_item">
			
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

    </div>
</div>
