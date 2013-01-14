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
//    $(document).bind("idStructureSearchBoxEvent", function(event, params) {
//        var marvinSketch = $('#MarvinSketch')[0];
//        alert(marvinSketch)
//            if(document.MarvinSketch != null) {
//                if(params.length ){
//                    document.MarvinSketch.setMol(params);
//                }
//
//            } else {
//                alert("Cannot load molecule:\n"+
//                    "no JavaScript to Java communication in your browser.\n");
//            }
//
//
//    });
    $('#modalDiv').modal({
        show:false
    });
    $("#modalDiv").draggable({
        handle: ".modal-header"
    });
    $('#modalDiv').css('width', 'auto') //Disable the default width=560px from bootstrap.css
    $('#modalDiv').on('show', function () {
//        var currentSearch = $('#searchString').val()
////        //populate the Id Box if the search box contains id searches
//        if (currentSearch.length) {
//            $(document).trigger("idStructureSearchBoxEvent", currentSearch);
//        }
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
