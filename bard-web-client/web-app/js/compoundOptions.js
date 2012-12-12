$(document).ready(function () {

    //display pop-over, need to figure why we need to do two clicks
    $(document).on("click", ".popover-link", function(){
        $('.popover-link').popover({
            content:function () {
                var popId = $(this).attr('data-detail-id');
                return $('#' + popId).html();
            },
            placement:'right'
        });
    });
    $(document).on("click", "a.analogs", function(){
        var searchString = $(this).attr('data-structure-search-params');
//        searchString = "Similarity:" + searchString
        $('#searchString').attr('value', searchString);
        $('#searchForm').submit();
    });

});


