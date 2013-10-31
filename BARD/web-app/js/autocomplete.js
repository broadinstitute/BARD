$(document).ready(function () {

    //set up auto complete
    var autoOpts = {
        source:"/BARD/bardWebInterface/autoCompleteAssayNames",
        minLength:2,
        delay:1000
    };

    $("#searchString").autocomplete(autoOpts)
        .data( "autocomplete" )._renderItem = function( ul, item ) {
            return $( "<li></li>" )
                .data( "item.autocomplete", item )
                .append( "<a>"+ item.label + "</a>" )
                .appendTo( ul );
        };

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