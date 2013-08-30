var waitingImage = '<img src="/BARD/static/images/ajax-loader.gif" alt="loading" title="loading"/>';

var PromiscuityHandler = {
    setup:function () {
        $(".promiscuity:not(.processed)").each(function () {
            var promiscuityDivId = $(this).attr('id');

            var url = $(this).attr('href');
            $.ajax({
                url:url,
                type:'GET',
                cache:false,
                //timeout: 10000,
                beforeSend:function () {
                    $('#' + promiscuityDivId).html(waitingImage);
                },
                success:function (promData) {
                    $('#' + promiscuityDivId).html(promData);
                    $('#' + promiscuityDivId + ' .popover-link').popover({
                        content:function () {
                            var scaffoldId = $(this).attr('data-detail-id');
                            return $('#' + scaffoldId).html();
                        },
                        placement:'bottom',
                        html:true,
                        trigger:'manual',
                        animation:false
                    }).click(function (ee) {
                            $('.popover-link').not(this).popover('hide');
                            $(this).popover('toggle');
                            //needed to block firing of the document's click event which would hide in return all popoevers (used when the user clicks away)
                            ee.stopPropagation();
                        });
                },
                error:function () {
                    $('#' + promiscuityDivId).html('No data found');
                },
                complete:function () {
                    $(this).addClass("processed");
                }
            });
        });
    }
};

$(document).on('search.complete', '#compounds', PromiscuityHandler.setup);

$(document).ready(function () {
    $(document).on("click", "html:not(.popover-link)", function (ee) {
        $('.popover-link').popover('hide');
    });
    PromiscuityHandler.setup();
});
