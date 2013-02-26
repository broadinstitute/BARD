var jsDrawEditor = null;

dojo.addOnLoad(function () {
    jsDrawEditor = new JSDraw2.Editor("jsDrawEditorDiv", {popup:false});
    adjustJSDrawEditorWindow()
    //Read cached structure
    jsDrawEditor.readCookie();
});

//On clicking the Submit button, update the hidden field with search-type + smiles and cache the structure in local storage.
$(document).on('click', '#searchButton', function (event) {
    var smiles = jsDrawEditor.getSmiles();
    if (!smiles) {
        //If the user didn't draw any structure, cancel the search
        event.preventDefault();
        window.location.href = '/bardwebclient/bardWebInterface/search';
        return false;
    }
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

//Disable the cutoff text-field unless the Similarity option is checked.
$(document).on('change', 'input:radio[name="structureSearchType"]', function () {
    if (($(this).attr('value') == "Similarity") && $(this).attr('checked')) {
        $('#cutoff').removeAttr("disabled");
    }
    else {
        $('#cutoff').attr("disabled", "disabled");
    }
});