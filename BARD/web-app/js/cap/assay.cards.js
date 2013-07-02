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
        url: '../../context/createCard',
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
            $("#errorCardHolder").removeClass("alert alert-success alert-error");
            $("#errorCardHolder").addClass("alert alert-success");
            $("#errorCardHolder").html("Successfully created");
        },
        error: function (response, textStatus, errorThrown) {
            $("#errorCardHolder").removeClass("alert alert-success alert-error");
            $("#errorCardHolder").addClass("alert alert-error");
            $("#errorCardHolder").html(response.responseText);
        }
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

    $("#dialog_confirm_delete_card").dialog({
        height: 250,
        width: 450,
        title: "Delete card?",
        autoOpen: false,
        draggable: false,
        zIndex: 3999,
        modal: true
    });

    initDnd();
});

function initDnd() {
//    $("button", ".deleteCardButton").button({
//        icons: {
//            primary: "ui-icon-trash"
//        },
//        text: false
//    }).click(function (event) {
//            var cardId = $(this).attr('id');
//            var contextClass = $("#contextClass").value()
//            showDeleteCardConfirmation(cardId, contextClass);
//    });

    //setupDeleteItemButton()
};


function showDeleteCardConfirmation(cardId, contextClass,section) {
    $("#dialog_confirm_delete_card").dialog("option", "buttons", [
        {
            text: "Delete",
            class: "btn btn-danger",
            click: function () {
                var data = {'contextClass': contextClass, 'contextId': cardId, 'section':section };
                $.ajax({
                    type: 'POST',
                    url: '../../context/deleteEmptyCard',
                    data: data,
                    success: function (data) {
                        updateCardHolder(data)
                        $("#errorCardHolder").removeClass("alert alert-success alert-error");
                        $("#errorCardHolder").addClass("alert alert-success");
                        $("#errorCardHolder").html("Successfully deleted");
                    },
                    error: function (response, textStatus, errorThrown) {
                        $("#errorCardHolder").removeClass("alert alert-success alert-error");
                        $("#errorCardHolder").addClass("alert alert-error");
                        $("#errorCardHolder").html(response.responseText);
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
    }).each(function () {
            var jThis = $(this)
            var cardSection = jThis.attr("cardsection");
            jThis.click(function (event) {
                $("#new_card_section").val(cardSection)
                $("#dialog_new_card").dialog("open");
            });
        });

    //add click events to buttons
    $(".documentPencil").on("click", function(e){
        e.stopPropagation();
        e.preventDefault();
        var dataId = $(this).attr('data-id');
        $("#" + dataId).editable('toggle');
    });
}

function outputToConsole(message) {
    if (console) {
        console.log(message);
    }
}

