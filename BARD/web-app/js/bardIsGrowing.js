var bigSpinnerImage = '<div class="tab-message"><img src="/BARD/images/ajax-loader.gif" alt="loading" title="loading"/></div>';

$(document).ready(function () {
    var url = $('#bardIsGrowing').attr('href');

    $.ajax({
        url:url,
        type:'GET',
        cache:false,
        beforeSend:function () {
            $('#bardIsGrowing').html(bigSpinnerImage);
        },
        success:function (data) {
            $('#bardIsGrowing').html(data);
        },
        error:function (data) {
            $('#bardIsGrowing').html(data);
        },
        complete:function () {

        }
    });

});
