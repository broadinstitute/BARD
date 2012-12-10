
var bigSpinnerImage = '<div class="tab-message"><img src="/bardwebclient/static/images/ajax-loader.gif" alt="loading" title="loading"/></div>';

$(document).ready(function () {
    var url = $('#sids').attr('href');
    loadSubstances(url);
});
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