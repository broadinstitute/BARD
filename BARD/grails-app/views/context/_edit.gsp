<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>


<%-- A template for both project and assay def --%>

<div id="cardView" class="cardView" class="row-fluid">

    <div class="row-fluid">
        <g:render template="/context/list" model="[contextOwner: contextOwner, contexts: contexts, subTemplate: 'edit', renderEmptyGroups: true]"/>
    </div>

    <r:script>
        registerAddNewCardButtons()
    </r:script>

    <div id="dialog_new_card">
        <form id="new_card_form" class="form-horizontal">
            <div class="control-group">
                <label class="control-label" for="edit_card_name">Name:</label>

                <div class="controls">
                    <input type="text" id="new_card_name" name="cardName" placeholder="Enter Card Name">
                </div>
            </div>
            <input type="hidden" id="new_card_section" name="cardSection" value=""/>
            <input type="hidden" name="instanceId" value="${contextOwner.id}"/>
        </form>
    </div>

    <div id="dialog_edit_card">
        <form id="edit_card_form" class="form-horizontal">
            <div class="control-group">
                <label class="control-label" for="edit_card_name">Name:</label>

                <div class="controls">
                    <input type="text" id="edit_card_name" name="edit_card_name" placeholder="Enter Card Name">
                </div>
            </div>
            <input type="hidden" id="contextId" name="contextId" value=""/>
            <input type="hidden" id="instanceId" name="instanceId" value="${contextOwner.id}"/>
        </form>
    </div>

    <div id="dialog_move_item"></div>

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

    <div id="dialog_add_item_wizard"></div>

    <div id="dialog_add_item_wizard_confirm_cancel">
        <p>
            <span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>
            Are you sure you want to cancel the Add Item Wizard?
        </p>
    </div>

</div>