$(document).ready(function () {

    $('.drc-popover-link').popover({
        content:function () {
            var popId = $(this).attr('data-detail-id');
            return $('#' + popId).html();
        },
        placement:'top'
    });
});
