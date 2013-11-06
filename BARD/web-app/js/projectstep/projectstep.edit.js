$(document).ready(function () {
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
    initProjectEditFunction();
});

function initProjectEditFunction() {
    $('#ajaxProgress')
        .hide()  // hide it initially
        .ajaxStart(function () {
            $(this).show();
        })
        .ajaxStop(function () {
            $(this).hide();
        });


    $("#linkExperiment")
        .button()
        .click(function () {
            $("#linkExperimentForm").clearForm();
            $("#serviceResponse").html("");
            $("#dialog_link_experiment").dialog("open");
        });
    $("#dialog_link_experiment").dialog({
        autoOpen: false,
        height: 400,
        width: 500,
        modal: true,
        buttons: {
            "Link Experiment": function () {
                var projectId = $("#projectIdForStep").val();
                var fromExperimentId = $("#fromExperimentId").val();
                var toExperimentId = $("#toExperimentId").val();
                var inputdata = {'fromExperimentId': fromExperimentId, 'toExperimentId': toExperimentId, 'projectId': projectId};
                $.ajax
                ({
                    url: "../linkExperiment",
                    //dataType:'jsonP',
                    data: inputdata,
                    cache: false,
                    success: function (data) {
                        handleSuccess(data);
                        closeDialogCleanup("displayLinkExperimentErrorMessage");
                        $(this).dialog("close");
                    },
                    error: function (response, status, errorThrown) {
                        addErrorMessageToDialog("displayLinkExperimentErrorMessage", response.responseText);
                    }
                });


            },
            Cancel: function () {
                closeDialogCleanup("displayLinkExperimentErrorMessage");
                $(this).dialog("close");
            }
        },
        close: function () {
            closeDialogCleanup("displayLinkExperimentErrorMessage");
        }
    });
}
/**
 * Take the error message div id and use it to clean up the dialog interface before you close it.
 * @param errorMessageDivId
 */
function closeDialogCleanup(errorMessageDivId) {
    $("#" + errorMessageDivId).removeClass("alert alert-error");
    $("#" + errorMessageDivId).html("");
}
function addErrorMessageToDialog(errorMessageDivId, errorMessage) {
    $("#" + errorMessageDivId).addClass("alert alert-error");
    $("#" + errorMessageDivId).html(errorMessage);
}

function clearAvailableExperiment() {

}

function findExperimentInput(selected, allValues) {
    for (var i = 0; i < allValues.length; i++) {
        $('#' + allValues[i]).text("")
        if (selected === allValues[i]) {
            $('#' + selected).show();
        }
        else {
            $('#' + allValues[i]).hide();
        }
    }
}

function handleSuccess(data) {
    $("#serviceResponse").css("font-size", "16px")
    if (data.substring(0, 12) === "serviceError") {
        $("#serviceResponse").css("color", "red")
        $("#serviceResponse").text(data)
    }
    else {
        $("#showstep").html(data);
        layoutGraph();
        initProjectEditFunction();
    }
    $("#serviceResponse").show()
}

function deleteItem(experimentId, projectId) {
    $("#dialog_confirm_delete_item").dialog("option", "buttons", [
        {
            text: "Delete",
            class: "btn btn-danger",
            click: function () {
                var data = {'experimentId': experimentId, 'projectId': projectId};
                $.ajax({
                    type: 'POST',
                    url: '../removeExperimentFromProject',
                    data: data,
                    success: function (data) {
                        handleSuccess(data)
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

function deleteEdge(fromId, toId, projectId) {
    $("#dialog_confirm_delete_item").dialog("option", "buttons", [
        {
            text: "Delete",
            class: "btn btn-danger",
            click: function () {
                var data = {'fromExperimentId': fromId, 'toExperimentId': toId, 'projectId': projectId};
                $.ajax({
                    type: 'POST',
                    url: '../removeEdgeFromProject',
                    data: data,
                    success: function (data) {
                        handleSuccess(data)
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