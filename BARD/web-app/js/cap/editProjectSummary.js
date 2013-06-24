$(document).ready(function () {

    //inline editing
    $.fn.editable.defaults.mode = 'inline';
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
    var modifiedBy = response.modifiedBy;
    $("#versionId").val(version);
    $("#lastUpdatedId").html(lastUpdated);
    $("#modifiedById").html(modifiedBy);

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


function validateRequiredField(fieldName, messageHolder) {
    if (!fieldName || 0 === fieldName || (/^\s*$/).test(fieldName)) {
        $("#" + messageHolder).html("Required and cannot be empty");
        return false;
    }
    return true;
}

function initSummaryFunction() {
    $("input#projectName").blur(function () {
        var projectName = $(this).val();
        validateRequiredField(projectName, "projectNameValidation");
    });
    $("input#projectName").click(function () {
        $("#projectNameValidation").html("");
    });
}

