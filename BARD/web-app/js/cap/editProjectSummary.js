$(document).ready(function () {
    $( "#editProjectSummaryButton" )
        .button()
        .click(function() {
            $( "#dialog_edit_project_summary" ).dialog( "open" );
            initFunction();
        });
    $( "#dialog_edit_project_summary" ).dialog({
        autoOpen: false,
        height: 400,
        width: 500,
        modal: true,
        buttons: {
            "Update Summary": function() {
                var instanceId = $("#projectId").text();
                var projectName = $("#projectName").val();
                var description = $("#description").val();
                if (!validateRequiredField(projectName, "projectNameNameValidation")) return false;
                var inputdata = {'instanceId':instanceId, 'projectName':projectName, 'description':description};
                $.ajax
                    ({
                        url:"../editSummary",
                        data:inputdata,
                        cache:false,
                        success:function(responseText, statusText, xhr, jqForm){
                                $("#summaryDetailSection").html(responseText);
                        }
                    });
                $( this ).dialog( "close" );
            },
            Cancel: function() {
                resetAfterCloseOrCancel();
                $( this ).dialog( "close" );
            }
        },
        close: function() {
            resetAfterCloseOrCancel();
        }
    });
});

function validateRequiredField(fieldName, messageHolder){
    if( !fieldName || 0 === fieldName || (/^\s*$/).test(fieldName)) {
        $("#"+messageHolder).html("Required and cannot be empty");
        return false;
    }
    return true;
};

function initFunction() {
    $("input#projectName").blur(function()
    {    var projectName = $(this).val();
        validateRequiredField(projectName, "projectNameValidation");
    });
    $("input#projectName").click(function()
    {
        $("#projectNameValidation").html("");
    });
}

function resetAfterCloseOrCancel() {
    var instanceId = $("#projectId").text();
    $("#editSummaryForm").clearForm();
    // Need to reload the original data
    var inputdata = {'instanceId':instanceId};
    $.ajax
        ({
            url:"../showEditSummary",
            data:inputdata,
            cache:false,
            success:function(responseText, statusText, xhr, jqForm){
                $("#dialog_edit_project_summary").html(responseText);
            }
        });
}


