//report error messages
window.onerror = bardClientErrorHandler;
//    Handle javascript errors
function bardClientErrorHandler(message, url, line) {
    $.ajax({
        cache:false,
        type:"post",
        data:{error:message, url:url, line:line, browser:navigator.userAgent},
        url:"/bardwebclient/ErrorHandling/handleJsErrors",
        async:true
    });
    return true;
}
