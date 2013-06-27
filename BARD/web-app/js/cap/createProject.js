$(document).ready(function () {

    //inline editing
    $.fn.editable.defaults.mode = 'inline';


    $('#nameId').editable('option', 'validate', function (v) {
        if (!v) return 'Required field!';
    });
    $('#descriptionId').editable('option', 'validate', function (v) {
        if (!v) return 'Required field!';
    });
    //edit status
    $('#statusId').editable('option', 'validate', function (v) {
        if (!v) return 'Required field!';
    });


    $('#save-btn').click(function () {
        $('.projectForm').editable('submit', {
            url: '/BARD/project/save',
            ajaxOptions: {
                dataType: 'json' //assuming json response
            },
            success: function (data, config) {
                window.location.href = data.url;
            },
            error: function (errors) {
                var msg = '';
                if (errors && errors.responseText) { //ajax error, errors = xhr object
                    msg = errors.responseText;
                } else { //validation error (client-side or server-side)
                    $.each(errors, function (k, v) {
                        msg += k + ": " + v + "<br>";
                    });
                }
                $('#msg').removeClass('alert-success').addClass('alert-error').html(msg).show();
            }
        });
    });
});



