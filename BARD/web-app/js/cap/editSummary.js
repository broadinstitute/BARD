$(document).ready(function () {
    $( "#editSummaryButton" )
        .button()
        .click(function() {
            $( "#dialog_edit_summary" ).dialog( "open" );
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
                var assayName = $("#assayName").val();
                var designedBy = $("#designedBy").val();
                if(!assayName || 0 === assayName || (/^\s*$/).test(assayName)){
                    alert("Assay Name field is required and cannot be empty");
                    return false;
                }
                var inputdata = {'instanceId':instanceId, 'assayStatus':assayStatus, 'assayName':assayName, 'designedBy':designedBy};
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
                $( this ).dialog( "close" );
            }
        },
        close: function() {
        }
    });
});


