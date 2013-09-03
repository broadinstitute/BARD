var spinnerImageLink = '<img src="/BARD/images/loading_icon.gif" alt="loading" title="loading" height="16" width="16"/>';
var bigSpinnerImage = '<div class="tab-message"><img src="/BARD/images/ajax-loader.gif" alt="loading" title="loading"/></div>';
var errorImageTwitterBootstrap = '<img src=""  class="icon-exclamation-sign" alt="error" height="16" width="16" />';

$(document).ready(function () {
    var url = $('#molecularSpreadSheet').attr('href');

    $.ajax({
        url:url,
        type:'GET',
        cache:false,
        //timeout: 10000,
        beforeSend:function () {
            $('#molecularSpreadSheet').html(bigSpinnerImage);
        },
        success:function (molSpreadSheetData) {
            $('#molecularSpreadSheet').html(molSpreadSheetData);
            addPopOver()
        },
        error:function () {
            $('#molecularSpreadSheet').html('No data found');
        },
        complete:function () {

        }
    });

});

function addPopOver() {
    $('.drc-popover-link').popover({
        content:function () {
            var popId = $(this).attr('data-detail-id');
            return $('#' + popId).html();
        },
        placement:'left'
    });
    $(".promiscuity").trigger('search.complete');
    $(".pop_smiles").popover();
}

