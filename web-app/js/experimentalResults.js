var bigSpinnerImage = '<div class="tab-message"><img src="/bardwebquery/static/images/ajax-loader.gif" alt="loading" title="loading"/></div>';

$(document).ready(function () {
    var url = $('#experimentalResults').attr('href');
    populatePage(url);

    //=== Handle Paging. We bind to all of the paging css classes on the anchor tag ===
    $("a.step,a.nextLink,a.prevLink").live('click', function (event) {
        event.preventDefault();	// prevent the default action behaviour to happen
        var pagingurl = $(this).attr('href');
        populatePage(pagingurl);
    });

});
function populatePage(url) {
    $.ajax({
        url:url,
        type:'GET',
        cache:false,
        //timeout: 10000,
        beforeSend:function () {
            $('#experimentalResults').html(bigSpinnerImage);
        },
        success:function (experimentalResultsData) {
            $('#experimentalResults').html(experimentalResultsData);

        },
        error:function () {
            $('#experimentalResults').html('No data found');
        },
        complete:function () {

        }
    });
}



