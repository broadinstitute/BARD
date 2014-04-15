var bigSpinnerImage = '<div class="tab-message">' + '<img src="' + bardAppContext + '/images/ajax-loader.gif"' + ' alt="loading" title="loading"/></div>';

$(document).on('change', 'select[id*="pane"]', function (event) {
    var panelId = $('select[id*="pane"]').val();
    $.ajax({
        url: "/BARD/panelExperiment/findExperimentsForPanelAjax",
        data: {"id": panelId},
        type: 'GET',
        cache: false,
        beforeSend: function () {
            $('#bigSpinnerImage').html(bigSpinnerImage);
        }})
        .done(function (experiments) {
            $('#bigSpinnerImage').html('');
            var htmlString = "";
            $.each(experiments, function (index, experiment) {
                htmlString += '<option value="' + experiment.id + '">' + experiment.id + ' - ' + experiment.experimentName + '</option>'
            });
            $('#experimentIds').html(htmlString);
        })
        .fail(function (XMLHTTPRequest, textStatus, errorThrown) {
            $('#bigSpinnerImage').html('<p><i class="icon-exclamation-sign"/>Could not retrieve the experiment list from the server</p>');
        });

});