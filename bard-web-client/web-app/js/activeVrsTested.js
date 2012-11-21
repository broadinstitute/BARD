
var waitingImage = '<img src="/bardwebclient/static/images/ajax-loader.gif" alt="loading" title="loading"/>';

var ActiveVrsTestedHandler = {
    setup : function() {
        $(".activeVrsTested:not(.processed)").each(function () {
            var activeVrsTestedDivId = $(this).attr('id');

            var url = $(this).attr('href');
            $.ajax({
                url:url,
                type:'GET',
                cache:false,
                //timeout: 10000,
                beforeSend: function() {
                    $('#' + activeVrsTestedDivId).html(waitingImage);
                },
                success:function (data) {
                    $('#' + activeVrsTestedDivId).html(data);
                 },
                error:function() {
                    $('#' + activeVrsTestedDivId).html('No data found');
                },
                complete:function() {
                    $(this).addClass("processed");
                }
            });
        });
    }
};

$(document).on('search.complete', '.activeVrsTested', ActiveVrsTestedHandler.setup);
$(document).on('search.complete', '#compounds', ActiveVrsTestedHandler.setup);

$(document).ready(function () {
    ActiveVrsTestedHandler.setup();
});
