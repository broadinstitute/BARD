//Implements the JSDraw popup/modal editor dialog
var dialog = null;

function showJSDrawEditor() {
    if (dialog == null) {
        // create the parent div to told the editor and the radio and submit buttons. params: (parent, element type, element text, CSS styles, attributes)
        var dialogDiv = scilligence.Utils.createElement(null, "div", null, {width:"auto"}, { class:""});
        // create the editor dialog div (jsdraw editor placeholder)
        var jsDrawEditorDiv = scilligence.Utils.createElement(dialogDiv, "div");
        //control-group div
        var controlsGroupDiv = scilligence.Utils.createElement(dialogDiv, "div", null, {textAlign:"left"}, {class:'control-group'});
        // radio-buttons div
        var controlsDiv = scilligence.Utils.createElement(controlsGroupDiv, "div", null, {textAlign:"left"}, {class:'controls'});
        // create the radio buttons; use the same element name to create the radio-group.
        $('#searchTypes').attr('value').split(':').forEach(function (searchType) {
            var labelElm = scilligence.Utils.createElement(controlsDiv, "label", searchType, null, {class:'radio inline'});
            scilligence.Utils.createElement(labelElm, "radio", null, null, {name:"structureSearchType", checked: (searchType=='Substructure')});

        });
        $('input[value="Substructure"]').attr('checked', 'checked');
        // submit-button div
        controlsDiv = scilligence.Utils.createElement(controlsGroupDiv, "div", null, {textAlign:"right"}, {class:'controls'});
        var submitButton = scilligence.Utils.createElement(controlsDiv, "Button", "Search", null, {class:'btn'});
        dojo.connect(submitButton, "onclick", onSearch);

        // finally, create the JSDraw editor dialog
        dialog = new JSDraw2.Dialog("Draw or Paste a Structure", dialogDiv);
        dialog.editor = new JSDraw2.Editor(jsDrawEditorDiv, {width:750, height:350, skin:"w8", sendquery:false, showfilemenu:false });
    }

    dialog.show();

//    var molfile = dojo.byId("testdata").value;
//    dialog.editor.setMolfile(molfile);
    dialog.editor.readCookie();
}

function onSearch() {
    //Writes the current structure into the client storage
    dialog.editor.writeCookie();

    var smiles = dialog.editor.getSmiles();
    var structureSearchTypeSelected = $('input:radio[name=structureSearchType]:checked').parent().text();
    var constructedSearch = structureSearchTypeSelected + ":" + smiles;
    $('#searchString').attr('value', constructedSearch);
    $('#searchForm').submit();
    dialog.hide();
}


var adjustMarvinSketchWindow = function () {
    var width = window.innerWidth / 2;
    var height = window.innerHeight / 2;
    if (width && document.MarvinSketch.width) {
        document.MarvinSketch.width = width
    }
    if (height && document.MarvinSketch.height) {
        document.MarvinSketch.height = height
    }
}


$(window.parent).resize(adjustMarvinSketchWindow);

$(document).ready(function () {

    $('#modalDiv').modal({
        show:false
    });
    $("#modalDiv").draggable({
        handle:".modal-header"
    });
    // $('#modalDiv').css('width', 'auto') //Disable the default width=560px from bootstrap.css
    $('#modalDiv').on('show', function () {

    });
    $('#structureSearchButton').click(function () {
        var structureSearchTypeSelected = $('input:radio[name=structureSearchType]:checked').val();
        var marvinSketch = $('#MarvinSketch')[0];
        var smiles = marvinSketch.getMol('smiles');

        //construct the query into a form that we want
        var constructedSearch = structureSearchTypeSelected + ":" + smiles;
        $('#searchString').attr('value', constructedSearch);
        $('#searchForm').submit();

    });
    // TODO Write method to get search type and smiles from searchString and push it into marvin
//    $('#modalDiv').on('show', function() {
//        var searchString = $('#searchString').val();
//        var regex = '(\w+):[\s]?(\S+)';
//        var searchType = regex.
//        var allSearchOptions = [];
//        $("[name=structureSearchType]").each(function() {
//            allSearchOptions.push($(this).val());
//        });
//
//    });
});
