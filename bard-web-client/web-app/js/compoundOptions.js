$(document).ready(function () {

//    $(document).on("click", "html", function () {
//        $('.popover-link').popover('hide');
//    });
    //display pop-over, need to figure why we need to do two clicks
//    $(document).on("click", ".popover-link", function () {
//        $('.popover-link').popover({
//            content:function () {
//                var popId = $(this).attr('data-detail-id');
//                return $('#' + popId).html();
//            },
//            trigger:'manual',
//            placement:'right'
//        }).click(function (evt) {
//                evt.stopPropagation();
//                $(this).popover('show');
//            });
//
//    });
    $(document).on("click", "a.analogs", function () {
        var searchString = $(this).attr('data-structure-search-params');
        $('#searchString').attr('value', searchString);
        $('#searchForm').submit();
    });

});


