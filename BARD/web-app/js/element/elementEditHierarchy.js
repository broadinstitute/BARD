$(document).ready(function () {
//    Update the hidden field in the form with the path to be deleted
    $('button[button-role="deleteElementPath"]').click(function () {
        var elementPath = $(this).attr('id');
        $('#elementPathToDelete').val(elementPath);
    });

    $('#elementList').on('change', function(event) {
        var select2FullPath = $(this).select2("data").fullPath;
        $('#select2FullPath').val(select2FullPath);
        var select2ElementId = $(this).select2("data").id;
        $('#select2ElementId').val(select2ElementId);
        //Submit the form
        $('#addElementPathForm').submit();
    });
});
