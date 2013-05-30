$(document).ready(function () {

    //inline editing
    $.fn.editable.defaults.mode = 'inline';

    //set up editing for documents
    $('.documents').editable({
        params: function (params) {
            var version = $(this).attr('data-version');
            var owningEntityId = $(this).attr('data-owningEntityId');
            params.version = version;
            params.owningEntityId = owningEntityId;
            params.documentName = $(this).attr('data-document-name');
            params.documentType = $(this).attr('data-documentType');
            return params;
        },
        ajaxOptions: {
            complete: function (response, serverMessage) {
                updateEntityVersion(response, serverMessage);
            }
        }

    });

    //Set up editing for button
    $('.documentPencil').click(function (e) {
        e.stopPropagation();
        e.preventDefault();
        var dataId = $(this).attr('data-id');
        $("#" + dataId).editable('toggle');
    });

    //edit status
    $('.status').editable({
        params: function (params) {
            params.version = $('#versionId').val();
            return params;
        },
        validate: function (value) {
            return validateInput(value);
        },
        success: function (response, newValue) {
            updateSummary(response, newValue);
        }
    });

    //edit name
    $('#nameId').editable({
        inputclass: 'input-large',
        params: function (params) {
            params.version = $('#versionId').val();
            return params;
        },
        validate: function (value) {
            return validateInput(value);
        },
        success: function (response, newValue) {
            updateSummary(response, newValue);
        }
    });

    //edit designed by
    $('.description').editable({
        inputclass: 'input-large',
        params: function (params) {
            params.version = $('#versionId').val();
            return params;
        },
        validate: function (value) {
            return validateInput(value);
        },
        success: function (response, newValue) {
            updateSummary(response, newValue);
        }
    });
});
function updateSummary(response, newValue) {

    var version = response.version;
    var lastUpdated = response.lastUpdate;

    $("#versionId").val(version);
    $("#lastUpdatedId").html(lastUpdated);
    return response.data;
}
function updateEntityVersion(response, serverresponse) {
    if (serverresponse == "success") { //only update the version on success
        //update the version of the assay
        var version = response.getResponseHeader("version");
        var entityId = response.getResponseHeader("entityId");

        //we use the entity id as the name of the class
        var elements = document.getElementsByClassName("" + entityId);
        for (var i = elements.length - 1; i >= 0; --i) {
            var element = elements[i];
            element.setAttribute("data-version", version);
        }
    }
}

function validateInput(value) {
    if ($.trim(value) == '') {
        return 'Required and cannot be empty';
    }
}

//$(document).ready(function () {
//    $( "#editProjectSummaryButton" )
//        .button()
//        .click(function() {
//            $( "#dialog_edit_project_summary" ).dialog( "open" );
//            initFunction();
//        });
//    $( "#dialog_edit_project_summary" ).dialog({
//        autoOpen: false,
//        height: 400,
//        width: 500,
//        modal: true,
//        buttons: {
//            "Update Summary": function() {
//                var instanceId = $("#projectId").text();
//                var projectName = $("#projectName").val();
//                var description = $("#description").val();
//                var projectStatus = $("#projectStatus").val();
//                if (!validateRequiredField(projectName, "projectNameNameValidation")) return false;
//                var inputdata = {'instanceId':instanceId, 'projectName':projectName, 'description':description, 'projectStatus':projectStatus};
//                $.ajax
//                    ({
//                        type:"POST",
//                        url:"../editSummary",
//                        data:inputdata,
//                        cache:false,
//                        success:function(responseText, statusText, xhr, jqForm){
//                                $("#summaryDetailSection").html(responseText);
//                        }
//                    });
//                $( this ).dialog( "close" );
//            },
//            Cancel: function() {
//                resetAfterCloseOrCancel();
//                $( this ).dialog( "close" );
//            }
//        },
//        close: function() {
//            resetAfterCloseOrCancel();
//        }
//    });
//});

function validateRequiredField(fieldName, messageHolder) {
    if (!fieldName || 0 === fieldName || (/^\s*$/).test(fieldName)) {
        $("#" + messageHolder).html("Required and cannot be empty");
        return false;
    }
    return true;
};

function initFunction() {
    $("input#projectName").blur(function () {
        var projectName = $(this).val();
        validateRequiredField(projectName, "projectNameValidation");
    });
    $("input#projectName").click(function () {
        $("#projectNameValidation").html("");
    });
}

//function resetAfterCloseOrCancel() {
//    var instanceId = $("#projectId").text();
//    $("#editSummaryForm").clearForm();
//    // Need to reload the original data
//    var inputdata = {'instanceId':instanceId};
//    $.ajax
//        ({
//            url:"../showEditSummary",
//            data:inputdata,
//            cache:false,
//            success:function(responseText, statusText, xhr, jqForm){
//                $("#dialog_edit_project_summary").html(responseText);
//            }
//        });
//}


