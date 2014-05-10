/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
            $('#fromExperimentId').html('<option value=""></option>');
            $('#toExperimentId').html('<option value=""></option>');
            var allNodes = connectedNodes.concat(isolatedNodes);
            $.each(allNodes.sort(function (a, b) {
                //sort by ID
                return (a.id - b.id);
            }), function (i, node) {
                var nodeDisplayId = node.keyValues.type == 'single' ? node.keyValues.eid : node.keyValues.type == 'panel' ? node.keyValues.pnlExpId + ' (Panel-Experiment)' : null
                var selectOptionString = '<option value="' + node.id + '">' + nodeDisplayId + '</option>';
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
                var fromProjectExperimentId = $("#fromExperimentId").val();
                var toProjectExperimentId = $("#toExperimentId").val();
                var inputdata = {'fromProjectExperimentId': fromProjectExperimentId, 'toProjectExperimentId': toProjectExperimentId, 'projectId': projectId};
                $.ajax
                ({
                    url: "../linkProjectExperiment",
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

function deletePanelExperimentItem(panelExperimentId, projectId) {
    $("#dialog_confirm_delete_item").dialog("option", "buttons", [
        {
            text: "Delete",
            class: "btn btn-danger",
            click: function () {
                var data = {'panelExperimentId': panelExperimentId, 'projectId': projectId};
                $.ajax({
                    type: 'POST',
                    url: '../removePanelExperimentFromProject',
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
                var data = {'fromProjectExperimentId': fromId, 'toProjectExperimentId': toId, 'projectId': projectId};
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
