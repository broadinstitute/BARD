$(document).ready(function () {

    //set up auto complete
    var autoOpts = {
        source:"/bardwebclient/bardWebInterface/autoCompleteAssayNames",
        minLength:2,
        html: true,
        delay:1000
    };

    $("#searchString").autocomplete(autoOpts);
    $("#searchString").bind("autocompleteselect", function (event, ui) {
        $("#searchString").val(ui.item.value)
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
