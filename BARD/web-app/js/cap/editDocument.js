/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/7/13
 * Time: 11:48 AM
 * To change this template use File | Settings | File Templates.
 */
$(document).ready(function () {
    // $.fn.editable.defaults.mode = 'inline';
    $('.dictionary').tooltip();
    //set up editing for documents
    $('.documents').editable({
        mode: 'inline',
        params: function (params) {
            var version = $(this).attr('data-version');
            var owningEntityId = $(this).attr('data-owningEntityId');
            params.version = version;
            params.owningEntityId = owningEntityId;
            params.documentName = $(this).attr('data-document-name');
            params.documentType = $(this).attr('data-documentType');
            params.documentKind = $(this).attr('data-documentKind');
            return params;
        },
        ajaxOptions: {
            complete: function (response, serverMessage) {
                updateEntityVersion(response, serverMessage);
            }
        }

    });
    //Handle textarea
    $('.textarea').editable({
        placement: 'top',
        showbuttons: 'bottom',
        rows:25,
        params: function (params) {
            var version = $(this).attr('data-version');
            var owningEntityId = $(this).attr('data-owningEntityId');
            params.version = version;
            params.owningEntityId = owningEntityId;
            params.documentName = $(this).attr('data-document-name');
            params.documentType = $(this).attr('data-documentType');
            params.documentKind = $(this).attr('data-documentKind');
            return params;
        },
        ajaxOptions: {
            complete: function (response, serverMessage) {
                updateEntityVersion(response, serverMessage);
            }
        }

    });
});
/**
 *
 * @param id
 * @param documentkind
 * @param owningEntityId
 * @param documentContent
 * @param documentName
 * @param documentType
 * @param version
 * @param msgId
 */
function editDocument(documentId, documentKind, owningEntityId, documentContent, documentName, documentType, version, msgId) {

    var payLoad = {
        'pk': documentId,
        'documentKind': documentKind,
        'name': documentKind,
        'owningEntityId': owningEntityId,
        'version': version,
        'value': documentContent,
        'documentName': documentName,
        'documentType': documentType
    };
    $.ajax({
        type: 'POST',
        url: bardAppContext + '/document/editDocument',
        data: payLoad,
        success: function (data, textStatus, response) {
            var version = response.getResponseHeader("version");
            var entityId = response.getResponseHeader("entityId");

            $('#textarea_' + documentId).attr('data-version', version);
            //also update the version on the document name
            $("#" + documentId + "_Name").attr('data-version', version);
            //document saved message
            //remove all previous errors and then add this
            $('.alert').html("");
            $('div').removeClass("alert alert-error alert-success");
            $('#' + msgId).addClass("alert alert-success");
            $('#' + msgId).html("Successfully edited");
        },
        error: function (response, textStatus, errorThrown) {
            //reset all div with class of alert
            $('.alert').html("");
            $('div').removeClass("alert alert-error alert-success");
            $('#' + msgId).addClass("alert alert-error");
            $('#' + msgId).html(response.responseText);
        }
    });
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
        location.reload(true)
    }
}


function validateRequiredField(fieldName, messageHolder) {
    if (!fieldName || 0 === fieldName || (/^\s*$/).test(fieldName)) {
        $("#" + messageHolder).html("Required and cannot be empty");
        return false;
    }
    return true;
}

function initDocumentFunction() {
    $("input#projectName").blur(function () {
        var projectName = $(this).val();
        validateRequiredField(projectName, "projectNameValidation");
    });
    $("input#projectName").click(function () {
        $("#projectNameValidation").html("");
    });
}

