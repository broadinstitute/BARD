
var bigSpinnerImage = '<div class="tab-message"><img src="/BARD/images/ajax-loader.gif" alt="loading" title="loading"/></div>';

$(document).ready(function () {
    var url = $('#sids').attr('href');

    if($('#probe').length > 0){
        var probeUrl = $('#probe').attr('href');
        loadProbe(probeUrl)
    }
    loadSubstances(url);
});
function loadProbe(url) {
    $.ajax({
        url:url,
        type:'GET',
        cache:false,
        beforeSend:function () {
            $('#sids').html(bigSpinnerImage);
        },
        success:function (probes) {
            $('#probe').html(probes);

        },
        error:function () {
            $('#probe').html('No Project found for Probe');
        },
        complete:function () {

        }
    });
}
function loadSubstances(url) {
    $.ajax({
        url:url,
        type:'GET',
        cache:false,
        //timeout: 10000,
        beforeSend:function () {
            $('#sids').html(bigSpinnerImage);
        },
        success:function (listOfSIDS) {
            $('#sids').html(listOfSIDS);

        },
        error:function () {
            $('#sids').html('No SIDS found for Compound');
        },
        complete:function () {

        }
    });
}