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
            //Update the link-from/link-to select inputs with project's eids.
            var graphInJSON = $.parseJSON($('#stepGraph').html());
            var connectedNodes = graphInJSON.connectedNodes;
            var isolatedNodes = graphInJSON.isolatedNodes;
            var nodeIds = new Array();
            $.each(connectedNodes.concat(isolatedNodes), function (i, node) {
                if (node.keyValues.type == "single") {
                    nodeIds.push({"id": node.keyValues.eid, "type": "single"});
                } else if (node.keyValues.type == "panel") {
                    nodeIds.push({"id": node.keyValues.pnlExpId, "type": "panel"});
                }
            });
            $('#fromExperimentId').html('<option value=""></option>');
            $('#toExperimentId').html('<option value=""></option>');
            $.each(nodeIds.sort(function (a, b) {
                return (a.id - b.id);
            }), function (i, nodeId) {
                var selectOptionString
                if (nodeId.type == 'single') {
                    selectOptionString = '<option value="' + nodeId.id + '-' + nodeId.type + '">' + nodeId.id + '</option>';
                } else if (nodeId.type == 'panel') {
                    selectOptionString = '<option value="' + nodeId.id + '-' + nodeId.type + '">' + nodeId.id + ' (Panel-Experiment)</option>';
                }
                $('#fromExperimentId').append(selectOptionString);
                $('#toExperimentId').append(selectOptionString);
            });
        });
    $("#dialog_link_experiment").dialog({
        autoOpen: false,
        height: 400,
        width: 500,
        modal: true,
        buttons: {
            "Link Experiment": function () {
                var projectId = $("#projectIdForStep").val();
                var fromSplit = $("#fromExperimentId").val().split('-');
                var fromId = fromSplit[0];
                var fromType = fromSplit[1];
                var toSplit = $("#toExperimentId").val().split('-');
                var toId = toSplit[0];
                var toType = toSplit[1];
                var inputdata = {'fromId': fromId, 'fromType': fromType, 'toId': toId, 'toType': toType, 'projectId': projectId};
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
                    },
                    error: handleAjaxError()
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
                    },
                    error: handleAjaxError()
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