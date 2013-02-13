var jsDrawEditor = null;

dojo.addOnLoad(function () {
    jsDrawEditor = new JSDraw2.Editor("jsDrawEditorDiv", {popup:false});
    adjustJSDrawEditorWindow()
    //Read cached structure
    jsDrawEditor.readCookie();
});

//On clicking the Submit button, update the hidden field with search-type + smiles and cache the structure in local storage.
$(document).on('click', '#searchButton', function () {
    var smiles = jsDrawEditor.getSmiles();
    var structureSearchTypeSelected = $('input:radio[name=structureSearchType]:checked').attr('value');
    var constructedSearch = structureSearchTypeSelected + ":" + smiles;
    if (structureSearchTypeSelected == $('#similaritySearchTypeValue').attr('value')) {
        var cutoff = $("#cutoff").val();
        constructedSearch += ' threshold:' + cutoff;
    }
    $('#searchString').attr('value', constructedSearch);
    jsDrawEditor.writeCookie();
});

$(document).ready(function () {
    $(window).resize(function () {
        adjustJSDrawEditorWindow();
    });
});

function adjustJSDrawEditorWindow() {
    var width = Math.max(($(window).width() - 30), 200);
    var height = Math.max(($(window).height() - 150), 200);
    if (width && height && jsDrawEditor) {
        jsDrawEditor.setSize(width, height)
    }
}
