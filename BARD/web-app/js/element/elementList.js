$(document).ready(function () {
    var bigSpinnerImage = '<div class="tab-message"><img src="' + bardAppContext + '/images/ajax-loader.gif" alt="loading" title="loading"/></div>';

    $.ajax({
        url: "/BARD/element/listAjax",
        type: 'GET',
        cache: false,
        beforeSend: function () {
            $('#elementList').html(bigSpinnerImage);
        }})
        .done(function (data) {
            $('#elementList').html('');
            var attributeSelect2 = new DescriptorSelect2('#elementList', 'Please select an element from the list', {results: []}, '/', 'bigdrop');
            attributeSelect2.initSelect2(data);
        })
        .fail(function (XMLHTTPRequest, textStatus, errorThrown) {
            $('#elementList').html('<p><i class="icon-exclamation-sign"/>Could not retrieve the element list from the server</p>');
        });
});

$('#btnUpdateElement').on('click', function () {
//    get the selected element
    var element = $("#elementList").select2("data");
//    send to the edit page
    window.location.href = bardAppContext + '/element/edit/' + element.id;
});