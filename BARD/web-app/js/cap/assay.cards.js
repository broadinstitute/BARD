$(document).ready(function () {
    //bind show event to accordion
    $('.collapse').on('show', function () {
        var icon = $(this).siblings().find("i.icon-chevron-right");
        icon.removeClass('icon-chevron-right').addClass('icon-chevron-down');
    });
    //bind hide event to accordion
    $('.collapse').on('hide', function () {
        var icon = $(this).siblings().find("i.icon-chevron-down");
        icon.removeClass('icon-chevron-down').addClass('icon-chevron-right');
    });

    $("#dialog:ui-dialog").dialog("destroy");

    $("#dialog_edit_card").dialog({
        height: 180,
        width: 500,
        autoOpen: false,
        modal: true,
        draggable: false,
        zIndex: 3999
    });
    $("#dialog_edit_card").dialog("option", "buttons", [
        {
            text: "Save",
            class: "btn btn-primary",
            click: function () {
                $("#edit_card_form").submit();
            }
        },
        {
            text: "Cancel",
            class: "btn",
            click: function () {
                $(this).dialog("close");
                $("#edit_card_form").clearForm();
                $("#assayContextId").val('');
            }
        }
    ]);
    $("#edit_card_form").ajaxForm({
        url: '../updateCardName',
        type: 'POST',
        beforeSubmit: function (formData, jqForm, options) {
            var form = jqForm[0];
            var nameValue = form.edit_card_name.value;
            if (!nameValue || 0 === nameValue || (/^\s*$/).test(nameValue)) {
                alert("Name field is required and cannot be empty");
                return false;
            }
            else {
                $("#dialog_edit_card").dialog("close");
            }
        },
        success: function (responseText, statusText, xhr, jqForm) {
            $("#edit_card_form").clearForm();
            $("#assayContextId").val('');
            updateCardHolder(responseText)
        }
    });

    $("#dialog_new_card").dialog({
        height: 180,
        width: 500,
        autoOpen: false,
        modal: true,
        draggable: false,
        zIndex: 3999,
        title: "Create new card"
    });
    $("#dialog_new_card").dialog("option", "buttons", [
        {
            text: "Save",
            class: "btn btn-primary",
            click: function () {
                $("#new_card_form").submit();
            }
        },
        {
            text: "Cancel",
            class: "btn",
            click: function () {
                $(this).dialog("close");
                $("#dialog_new_card").clearForm();
            }
        }
    ]);
    $("#new_card_form").ajaxForm({
        url: '../createCard',
        type: 'POST',
        beforeSubmit: function (formData, jqForm, options) {
            var form = jqForm[0];
            var nameValue = form.cardName.value;
            if (!nameValue || 0 === nameValue || (/^\s*$/).test(nameValue)) {
                alert("Name field is required and cannot be empty");
                return false;
            }
            else {
                $("#dialog_new_card").dialog("close");
            }
        },
        success: function (responseText, statusText, xhr, jqForm) {
            $("#new_card_form").clearForm();
            $("#cardSection").val('');
            updateCardHolder(responseText)
        }
    });

    $("#dialog_move_item").dialog({
        height: 300,
        width: 500,
        autoOpen: false,
        modal: true,
        draggable: false,
        zIndex: 3999,
        title: "Move card item"
    });
    $("#dialog_edit_card").dialog("option", "buttons", [
        {
            text: "Save",
            class: "btn btn-primary",
            click: function () {
                $("#edit_card_form").submit();
            }
        },
        {
            text: "Cancel",
            class: "btn",
            click: function () {
                $(this).dialog("close");
                $("#edit_card_form").clearForm();
                $("#assayContextId").val('');
            }
        }
    ]);

    $("#dialog_add_item_wizard_confirm_cancel").dialog({
        height: 200,
        width: 500,
        autoOpen: false,
        modal: true,
        draggable: false,
        zIndex: 4000,
        title: "Confirm"
    });
    
    $("#dialog_edit_card_item").dialog({
        height: 300,
        width: 500,
        autoOpen: false,
        modal: true,
        draggable: true,
        zIndex: 4000,
        title: "Edit Item"
    });

    $("#dialog_add_item_wizard_confirm_cancel").dialog("option", "buttons", [
        {
            text: "OK",
            class: "btn btn-primary",
            click: function () {
                $("#dialog_add_item_wizard").dialog("close");
                $(this).dialog("close");
            }
        },
        {
            text: "Cancel",
            class: "btn",
            click: function () {
                $(this).dialog("close");
            }
        }
    ]);

    $("#dialog_add_item_wizard").dialog({
        height: 600,
        width: 750,
        autoOpen: false,
        modal: true,
        draggable: true,
        zIndex: 3999,
        title: "Add Item Wizard"
    });
    

    $("#dialog_confirm_delete_card").dialog({
        height: 250,
        width: 450,
        title: "Delete card?",
        autoOpen: false,
        draggable: false,
        zIndex: 3999,
        modal: true
    });

    $("#dialog_confirm_delete_item").dialog({
        resizable: false,
        height: 250,
        width: 450,
        modal: true,
        autoOpen: false,
        draggable: false,
        zIndex: 3999,
        title: "Delete item?"
    });

    initDnd();
});


function initDnd() {
    $("caption.assay_context").dblclick(function () {
        var assayContextId = $(this).attr('id');
        var name = $(this).find('div.cardTitle').text();
        editCardName(assayContextId, name)

    });

    $("button", ".deleteCardButton").button({
        icons: {
            primary: "ui-icon-trash"
        },
        text: false
    }).click(function (event) {
            var cardId = $(this).attr('id');

            $("#dialog_confirm_delete_card").dialog("option", "buttons", [
                {
                    text: "Delete",
                    class: "btn btn-danger",
                    click: function () {
                        var data = {'assay_context_id': cardId };
                        $.ajax({
                            type: 'POST',
                            url: '../deleteEmptyCard',
                            data: data,
                            success: function (data) {
                                updateCardHolder(data)
                            }
                        });
                        $(this).dialog("close");
                    }
                },
                {
                    text: "Cancel",
                    class: "btn",
                    click: function () {
                        $(this).dialog("close");
                    }
                }
            ]);

            $("#dialog_confirm_delete_card").dialog("open");

        });

    $(".deleteItemButton button").button({
        icons: {
            primary: "ui-icon-trash"
        },
        text: false
    }).click(function (event) {
            var itemId = $(this).attr('id');
            var assayContextId = $(this).parents("div.card").attr('id');
            $("#dialog_confirm_delete_item").dialog("option", "buttons", [
                {
                    text: "Delete",
                    class: "btn btn-danger",
                    click: function () {
                        var data = {'assay_context_item_id': itemId };
                        $.ajax({
                            type: 'POST',
                            url: '../deleteItemFromCard',
                            data: data,
                            success: function (data) {
                                $("div#" + assayContextId).replaceWith(data);
                                initDnd();
                            }
                        });
                        $(this).dialog("close");
                    }
                },
                {
                    text: "Cancel",
                    class: "btn",
                    click: function () {
                        $(this).dialog("close");
                    }
                }
            ]);

            $("#dialog_confirm_delete_item").dialog("open");

        });


    $(document).ready(function () {

        $(".attributeLabel").draggable({
            cursor: 'move',
            scroll: true,
            revert: true,
            appendTo: 'body',
            helper: function (event) {
                return $('<div class="cardView"><table style="width:33%; z-index:3999;" class="gridtable"></table></div>').find('table').append($(event.target).closest('tr').clone());
            }
        });
    });

    $(document).ready(function () {
        $("tr.context_item_row").droppable({
            hoverClass: "drophover",
            accept: ".attributeLabel",
            drop: function (event, ui) {
                var src_assay_context_item_id = ui.draggable.closest('tr').attr('id');
                var target_assay_context_item_id = $(this).attr('id');
                var data = {'src_assay_context_item_id': src_assay_context_item_id,
                    'target_assay_context_item_id': target_assay_context_item_id};
                $.ajax({
                    type: 'POST',
                    url: '../addItemToCardAfterItem',
                    data: data,
                    success: function (data) {
                        updateCardHolder(data)
                    }
                });
                ui.helper.remove();
            }
        });
    });
    $(document).ready(function () {
        $("div.card").droppable({
            hoverClass: "drophoverCard",
            accept: ".attributeLabel",
            drop: function (event, ui) {
                var src_assay_context_item_id = ui.draggable.closest('tr').attr('id');
                var target_assay_context_id = $(this).find('caption').attr('id');
                var data = {'src_assay_context_item_id': src_assay_context_item_id,
                    'target_assay_context_id': target_assay_context_id};
//                alert(data.src_assay_context_item_id + ' ' + data.target_assay_context_id );
                $.ajax({
                    type: 'POST',
                    url: '../addItemToCard',
                    data: data,
                    success: function (data) {
                        updateCardHolder(data)
                    }
                });
                ui.helper.remove();
            }
        });
    });

};

function deleteCardItem(itemId, assayContextId) {
    $("#dialog_confirm_delete_item").dialog("option", "buttons", [
        {
            text: "Delete",
            class: "btn btn-danger",
            click: function () {
                var data = {'assay_context_item_id': itemId };
                $.ajax({
                    type: 'POST',
                    url: '../deleteItemFromCard',
                    data: data,
                    success: function (data) {
                        updateCardHolder(data)
                    }
                });
                $(this).dialog("close");
            }
        },
        {
            text: "Cancel",
            class: "btn",
            click: function () {
                $(this).dialog("close");
            }
        }
    ]);

    $("#dialog_confirm_delete_item").dialog("open");
}

function setupMoveCardItemForm() {

    $("#move_item_form").ajaxForm({
        url: '../moveCardItem',
        type: 'POST',
        beforeSubmit: function (formData, jqForm, options) {
            var form = jqForm[0];
            var cardId = form.cardId.value;
            if (!cardId || 0 === cardId || (/^\s*$/).test(cardId)) {
                alert("Card Name field is required and cannot be empty");
                return false;
            }
            else {
                $("#dialog_move_item").dialog("close");
            }
        },
        success: function (responseText, statusText, xhr, jqForm) {
            updateCardHolder(responseText)
        }
    });

    $("#dialog_move_item").dialog("option", "buttons", [
        {
            text: "Move",
            class: "btn btn-primary",
            click: function () {
                $("#move_item_form").submit();
            }
        },
        {
            text: "Cancel",
            class: "btn",
            click: function () {
                $(this).dialog("close");
            }
        }
    ]);
}

function moveCardItem(assayId, itemId) {

    var data = {'assayId': assayId, 'itemId': itemId };
    $.ajax({
        type: 'POST',
        url: '../showMoveItemForm',
        data: data,
        success: function (data) {
//        	alert(data);
            $("#dialog_move_item").html(data);
            setupMoveCardItemForm();
            $("#dialog_move_item").dialog("open");
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert("Error: " + textStatus);
        }
    });
}

function deleteCard(cardId) {
    $("#dialog_confirm_delete_card").dialog("option", "buttons", [
        {
            text: "Delete",
            class: "btn btn-danger",
            click: function () {
                var data = {'assay_context_id': cardId };
                $.ajax({
                    type: 'POST',
                    url: '../deleteEmptyCard',
                    data: data,
                    success: function (data) {
                        updateCardHolder(data)
                    }
                });
                $(this).dialog("close");
            }
        },
        {
            text: "Cancel",
            class: "btn",
            click: function () {
                $(this).dialog("close");
            }
        }
    ]);

    $("#dialog_confirm_delete_card").dialog("open");
}

function editCardName(cardId, cardName) {
    $("#edit_card_name").val(cardName);
    $("#contextId").val(cardId);
    $("#dialog_edit_card").dialog({title: "Edit Card Name"});
    $("#dialog_edit_card").dialog("open");
}

function editCardItem(itemId, assayContextId) {
	var data = {'assayContextId': assayContextId, 'assayContextItemId': itemId};
	$.ajax({
        type: 'POST',
        url: '../launchEditItemInCard',
        data: data,
        success: function (data) {
//        	alert(data);
        	$("#dialog_edit_card_item").dialog("option", "buttons", [
			{
				    text: "Save",
				    class: "btn btn-primary",
				    click: function () {
				        
				        $.ajax({
				            type: 'POST',
				            url: '../editItemInCard',
				            data: data,
				            success: function (data) {
				                updateCardHolder(data)
				            }
				        });
				        $(this).dialog("close");
				    }
				},
				{
				    text: "Cancel",
				    class: "btn",
				    click: function () {
				        $(this).dialog("close");
				    }
				}
			]);        	                                                 			        
            $("#dialog_edit_card_item").html(data);
            $("#dialog_edit_card_item").dialog("open");
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert("Error: " + textStatus + " -> " + errorThrown);
        }
    });
	                                                      

}

function removeItemFromList(listIndex) {
	alert('Remove icon has been clicked! List Index: ' + listIndex);
	var data = {'listIndex': listIndex };
    $.ajax({
        type: 'POST',
        url: '../../addItemWizard/pages/removeItemFromList',
        data: data,
        success: function (data) {
        	$("div#itemsInListTable").html(data);
        },
	    error: function (jqXHR, textStatus, errorThrown) {
	        alert("Error: " + textStatus + " -> " + errorThrown);
	    }
    });
}

function launchAddItemWizard(assayId, assayContextId, cardSection) {
	
    var data = {'assayId': assayId, 'assayContextId': assayContextId, 'cardSection': cardSection};
    $.ajax({
        type: 'POST',
        url: '../../addItemWizard/addItemWizard',
        data: data,
        success: function (data) {
//        	alert(data);            
            $("#dialog_add_item_wizard").html(data);
            $("#dialog_add_item_wizard").dialog("open");
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert("Error: " + textStatus + " -> " + errorThrown);
        }
    });
}

function updateCardHolder(response) {
    $("div#cardHolder").html(response);
    initDnd();
    registerAddNewCardButtons();
}

function registerAddNewCardButtons() {
    $(".add-card-button").button({
        icons: {
            primary: "ui-icon-plus"
        }
    }).each( function() {
            var jThis = $(this)
            var cardSection = jThis.attr("cardsection");
            jThis.click(function(event){
                $("#new_card_section").val(cardSection)
                $("#dialog_new_card").dialog("open");
            });
    });
}