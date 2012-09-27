<div id="cardView" class="cardView" class="row-fluid">

    <div class="span12">

        <div class="row-fluid">
            <div class="span12"><button id="addNewBtn" class="btn btn-primary">Add New Card</button></div>
        </div>

        <div class="row-fluid">
            <div id="cardHolder" class="span12">
                <g:render template="cards" model="[cardDtoMap: cardDtoMap]"/>
            </div>
        </div>

        <div id="dialog_new_card">
            <form id="new_card_form" class="form-horizontal">
                <div class="control-group">
                    <label class="control-label" for="card_name">Name:</label>

                    <div class="controls">
                        <input type="text" id="card_name" name="card_name" size="30" placeholder="Enter Card Name">
                    </div>
                </div>

                <div>
                    <input type="hidden" id="assay_id" name="assay_id" value="${assayId}"/>
                </div>
            </form>
        </div>


        <div id="dialog_confirm_delete_item">
            <p>
                <span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
                This item will be permanently deleted and cannot be recovered. Are you sure?
            </p>
        </div>

    </div>
</div>
