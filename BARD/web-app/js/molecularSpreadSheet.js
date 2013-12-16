var bigSpinnerImage = '<div class="tab-message">' + '<img src="' + bardAppContext + '/images/ajax-loader.gif"' + ' alt="loading" title="loading"/></div>';

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

