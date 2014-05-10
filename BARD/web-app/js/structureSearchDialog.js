/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//Implements the JSDraw popup/modal editor dialog
var dialog = null;

function showJSDrawEditor() {
    if (dialog == null) {
        // create the parent div to told the editor and the radio and submit buttons. params: (parent, element type, element text, CSS styles, attributes)
        var dialogDiv = scilligence.Utils.createElement(null, "div", null, {width:"auto"}, { id: 'dialogDiv', class:""});
        // create the editor dialog div (jsdraw editor placeholder)
        var jsDrawEditorDiv = scilligence.Utils.createElement(dialogDiv, "div");
        //control-group div
        var controlsGroupDiv = scilligence.Utils.createElement(dialogDiv, "div", null, {textAlign:"left"}, {class:'control-group'});
        // radio-buttons div
        var controlsDiv = scilligence.Utils.createElement(controlsGroupDiv, "div", null, {textAlign:"left"}, {class:'controls'});
        // create the radio buttons; use the same element name to create the radio-group.
        $('#searchTypes').attr('value').split(':').forEach(function (searchType) {
            var labelElm = scilligence.Utils.createElement(controlsDiv, "label", searchType, null, {class:'radio inline'});
            scilligence.Utils.createElement(labelElm, "radio", null, null, {name:"structureSearchType", value: searchType, checked:(searchType == 'Substructure')});

        });
        $('input[value="Substructure"]').attr('checked', 'checked');
        // submit-button div
        controlsDiv = scilligence.Utils.createElement(controlsGroupDiv, "div", null, {textAlign:"right"}, {class:'controls'});
        var submitButton = scilligence.Utils.createElement(controlsDiv, "Button", "Search", null, {class:'btn', id: 'structureSearchButton'});
        dojo.connect(submitButton, "onclick", onSearch);

        // finally, create the JSDraw editor dialog
        dialog = new JSDraw2.Dialog("Draw or Paste a Structure", dialogDiv);
        dialog.editor = new JSDraw2.Editor(jsDrawEditorDiv, {width:750, height:350, skin:"w8", sendquery:false, showfilemenu:false });
        adjustJSDrawEditorWindow();
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


$(window).resize(function () {
    adjustJSDrawEditorWindow();
});

function adjustJSDrawEditorWindow() {
    var width = $(window).width() / 2;
    var height = $(window).height() / 2;
    if (width && height && dialog && dialog.editor) {
        dialog.editor.setSize(width, height)
    }
}


//From marvinSketch.gsp
//It was originally created for user story https://www.pivotaltracker.com/story/show/38271119 - Sync smiles string in search box with contents of structure search dialog
//Since JSDraw cases the drawing into local storage, we don't really need that one anymore.

//var isJs2Java = isLiveConnect();
//var SELECTED = false
//msketch_mayscript = true;
//msketch_name = "MarvinSketch";
//msketch_begin("${request.contextPath}/marvin", window.innerWidth / 2, window.innerHeight / 2);
//
////We set the smiles to a dummy C
//msketch_param("mol", 'C');
//msketch_param('molLoaderFinishedEvent', 'javascript:insertCurrentSmiles();');
//msketch_end();
//
////If there is a smiles string in the search box insert it
//function insertCurrentSmiles() {
//    if (!SELECTED) {
//        var currentSearch = $('#searchString').val();
//        //extract smiles from search string
//        var smiles = extractSmilesFromSearchString(currentSearch)
//        var structureSearchType = extractStructureSearchTypeFromSearchString(currentSearch)
//        if (smiles.length) {
//            importMolToMSketch(null, smiles, document.MarvinSketch, "MarvinSketch")
//        } else {
//            importMolToMSketch(null, smiles, document.MarvinSketch, "MarvinSketch")
//        }
//        switch (structureSearchType) { //must be one of these to qualify as a structure search
//
//            case 'Exact': //this is an exact search
//                $("input[name='structureSearchType'][value='Exact']").attr("checked", "checked");
//                break;
//            case 'Substructure':
//                $("input[name='structureSearchType'][value='Substructure']").attr("checked", "checked");
//                break;
//            case 'Similarity':  //this is an project search with ids
//                $("input[name='structureSearchType'][value='Similarity']").attr("checked", "checked");
//                break;
//            case 'Superstructure':
//                $("input[name='structureSearchType'][value='Superstructure']").attr("checked", "checked");
//                break;
//
//        }
//        SELECTED = true //otherwise the applet keeps reloading itself
//    }
//
//}
//function importMolToMSketch(opts, smiles, targetMSketchDomObject, targetMSketchName) {
//
//    if (!smiles.length) {
//        targetMSketchDomObject.setMol(null, null);
//        return;
//    }
//    if ((targetMSketchDomObject != null) && isJs2Java) {
//        targetMSketchDomObject.setMol(smiles, opts);
//    } else if (!isJs2Java) {
//        mparams = "java.lang.String";
//        if (opts != null) {
//            mparams += ",java.lang.String";
//        }
//        setMethod(targetMSketchName + ".setMol", mparams);
//        addMethodParam(smiles);
//        if (opts != null) {
//            addMethodParam(opts);
//        }
//        runMethod();
//    } else {
//        alert("Cannot import molecule:\n" +
//            "no JavaScript to Java communication in your browser.\n");
//    }
//}
//function extractSmilesFromSearchString(searchString) {
//    if (searchString.length) {
//        var searchStringSplit = searchString.split(":");
//        if (searchStringSplit.length == 2 && $.trim(searchStringSplit[1]).length) { //has to be of the form Exact:CCC so there must be 2 things in the array
//            return searchStringSplit[1];
//        }
//    }
//    return ""
//
//}
///**
// * Find the structure search Type
// * @param searchString
// * @return {String}
// */
//function extractStructureSearchTypeFromSearchString(searchString) {
//    //we want to find out if this is a Structure search
//    var structureSearchType = "Substructure"
//    if (searchString.length) {
//        var searchStringSplit = searchString.split(":");
//        var searchType = searchStringSplit[0];
//        if (searchStringSplit.length == 2 && $.trim(searchStringSplit[1]).length) { //has to be of the form Exact:CCC so there must be 2 things in the array
//            searchType = searchType.toLowerCase();
//            var stringAfterColon = $.trim(searchStringSplit[1])
//            switch (searchType) { //must be one of these to qualify as a structure search
//                case 'exact':
//                    structureSearchType = "Exact";
//                    break;
//                case 'substructure':
//                    structureSearchType = "Substructure";
//                    break;
//                case 'superstructure':
//                    structureSearchType = "Superstructure";
//                    break;
//                case 'similarity':
//                    structureSearchType = "Similarity";
//                    break;
//
//            }
//        }
//    }
//    return structureSearchType
//}
