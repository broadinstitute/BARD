$(document).ready(function () {

    //set up auto complete
    var autoOpts = {
        source:"/bardwebquery/bardWebInterface/autoCompleteAssayNames",
        minLength:2,
        html: true
    };
    $("#searchString").autocomplete(autoOpts);
    $("#searchString").bind("autocompleteselect", function (event, ui) {
        $("#searchButton").click();
    });
    // make sure to close the autocomplete box when the search button or ENTER are clicked
    $("#searchButton").click(function () {
        $("#searchString").autocomplete("close");
    });
    $('#searchButton').keypress(function(eventData) {
        if(eventData.which == 13) {
            $("#searchString").autocomplete("close");
        }
    });

});
