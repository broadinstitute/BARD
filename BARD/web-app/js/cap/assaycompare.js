function handleSuccess() {
    //remove any old error messages
    $('#errorMsgId').empty();
    $('#errorMsgId').removeClass('alert-error');
}
function handleError() {
    $('#errorMsgId').addClass('alert-error');
    $('#compare').empty();
}

