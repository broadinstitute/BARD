/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 2/7/13
 * Time: 11:48 AM
 * To change this template use File | Settings | File Templates.
 */


bkLib.onDomLoaded(function () {
    var textareas = $("textarea");
    for (var i = 0; i < textareas.length; i++) {
        var currentId = textareas[i].id
        var editableProperty = $("#" + currentId).attr('data-editable')


        new nicEditor({fullPanel: true,
            iconsPath: '/BARD/images/nicedit/nicEditorIcons.gif',
            onSave: function (content, id, instance) {
                var documentId = instance.e.id
                var documentContent = content;
                var version = $('#' + documentId).attr('data-version');
                var owningEntityId = $('#' + documentId).attr('data-owningEntityId');
                var documentName = $('#' + documentId).attr('data-document-name');
                var documentType = $('#' + documentId).attr('data-documentType');
                var documentKind = $('#' + documentId).attr('data-documentKind');
                //this is used to find the div to output success/error messages
                var msgId = $('#' + documentId).attr('data-server-response-id');
                var entityId = document.replace("textarea_", "");
                editDocument(entityId, documentKind, owningEntityId, documentContent, documentName, documentType, version, msgId)

            } }).panelInstance(currentId);
        if (editableProperty == 'cannotedit') {
            //disable document editing
            $('.nicEdit-main').attr('contenteditable','false');
            $('.nicEdit-panel').hide();
        }

    }
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
function editDocument(documentId, documentkind, owningEntityId, documentContent, documentName, documentType, version, msgId) {
    var payLoad = {
        'pk': documentId,
        'name': documentkind,
        'owningEntityId': owningEntityId,
        'version': version,
        'value': documentContent,
        'documentName': documentName,
        'documentType': documentType
    };
    $.ajax({
        type: 'POST',
        url: '/BARD/document/editDocument',
        data: payLoad,
        success: function (data, textStatus, response) {
            var version = response.getResponseHeader("version");
            var entityId = response.getResponseHeader("entityId");
            $('#' + documentId).attr('data-version', version);
            //also update the version on the document name
            $('#' + documentId + "_Name").attr('data-version', version);
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
