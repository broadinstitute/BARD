var jsDrawEditor = null;

dojo.addOnLoad(function () {
    jsDrawEditor = new JSDraw2.Editor("jsDrawEditorDiv", {popup:false, rxn: false, biology: false});
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
        window.location.href = '/BARD/bardWebInterface/search';
        return false;
    }
    var structureSearchTypeSelected = $('input:radio[name=structureSearchType]:checked').attr('value');
    var constructedSearch = structureSearchTypeSelected + ":" + smiles;
    if (structureSearchTypeSelected == $('#similaritySearchTypeValue').attr('value')) {
        var cutoff = $("#cutoff").val();
        if (!isSimilarityThresholdValueValid(cutoff)) {
            //prevent the value keyed in.
            $('#cutoff').addClass('error')
            event.preventDefault();
            return false;
        }
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

//Allow only numeric values in the similarity threshold field
function isSimilarityThresholdValueValid(cutoff) {
    var intRegex = /^\d+(\.\d+)?$/;
    return (intRegex.test(cutoff.trim()) && cutoff <= 100 && cutoff >= 0)
}