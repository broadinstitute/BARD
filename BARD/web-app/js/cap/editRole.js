$(document).ready(function () {


    //Set up editing for button
    $('.documentPencil').click(function (e) {
        e.stopPropagation();
        e.preventDefault();
        var dataId = $(this).attr('data-id');
         $("#" + dataId).editable('toggle');
    });

       //edit name
    $('.role').editable({
        mode:'inline',
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
    var modifiedBy = response.modifiedBy;
    var lastUpdated = response.lastUpdate;
    var shortName = response.shortName;


    $("#versionId").val(version);
    $("#modifiedById").html(modifiedBy);
    $("#lastUpdatedId").html(lastUpdated);
    $("#shortNameId").html(shortName);
    return response.data;
}

function validateInput(value) {
    if ($.trim(value) == '') {
        return 'Required and cannot be empty';
    }
}


