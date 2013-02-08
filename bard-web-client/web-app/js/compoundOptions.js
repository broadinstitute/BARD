$(document).ready(function () {
    $(document).on("click", "a.analogs", function () {
        var searchString = $(this).attr('data-structure-search-params');
        var cutoff = $("#cutoff").val()
        searchString=searchString + " threshold:" + cutoff
        $('#searchString').attr('value', searchString);
        $('#searchForm').submit();
    });

});


