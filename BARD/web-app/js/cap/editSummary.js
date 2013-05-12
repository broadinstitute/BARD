$(document).ready(function () {
    $( "#editSummaryButton" )
        .button()
        .click(function() {
            $( "#dialog_edit_summary" ).dialog( "open" );
            initFunction();
        });
    $( "#dialog_edit_summary" ).dialog({
        autoOpen: false,
        height: 400,
        width: 500,
        modal: true,
        buttons: {
            "Update Summary": function() {
                var instanceId = $("#assayId").text();
                var assayStatus = $("#assayStatus option:selected").text()
                var assayType = $("#assayType option:selected").text()
                var assayName = $("#assayName").val();
                var designedBy = $("#designedBy").val();
                if (!validateRequiredField(assayName, "assayNameValidation")) return false;
                var inputdata = {'instanceId':instanceId, 'assayStatus':assayStatus, 'assayName':assayName, 'designedBy':designedBy, 'assayType':assayType};
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
    $("input#assayName").blur(function()
    {    var assayName = $(this).val();
        validateRequiredField(assayName, "assayNameValidation");
    });
    $("input#assayName").click(function()
    {
        $("#assayNameValidation").html("");
    });
}

function resetAfterCloseOrCancel() {
    var instanceId = $("#assayId").text();
    $("#editSummaryForm").clearForm();
    // Need to reload the original data
    var inputdata = {'instanceId':instanceId};
    $.ajax
        ({
            url:"../showEditSummary",
            data:inputdata,
            cache:false,
            success:function(responseText, statusText, xhr, jqForm){
                $("#dialog_edit_summary").html(responseText);
            }
        });
}


