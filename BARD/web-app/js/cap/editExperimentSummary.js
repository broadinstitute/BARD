$(document).ready(function () {

    //inline editing
    $.fn.editable.defaults.mode = 'inline';


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
    //edit description by
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

    //edit hold until date
    $('.huddate').editable({
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
    //edit run from date
    $('.rfddate').editable({
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
    //edit run to date
    $('.rdtdate').editable({
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
    var modifiedBy = response.modifiedBy;

    $("#versionId").val(version);
    $("#modifiedById").html(modifiedBy);
    $("#lastUpdatedId").html(lastUpdated);
    return response.data;
}


function validateInput(value) {
    if ($.trim(value) == '') {
        return 'Required and cannot be empty';
    }
}





