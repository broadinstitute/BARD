var ajaxErrors = {};

var notifyAjaxFailure = function(msg) {
    if(!($("#ajax-failure-dialog").dialog("isOpen") === true)) {
        // clear the list of errors when we show this for the first time
        ajaxErrors = {};

        $("#ajax-failure-dialog").dialog({
            width: 600,
            modal: true
        });
    }

    // add error, de-dupping
    if(ajaxErrors[msg]) {
        ajaxErrors[msg] += 1;
    } else {
        ajaxErrors[msg] = 0;
    }

    var listHtml = ["<ul>"];
    for(var umsg in ajaxErrors) {
        // there has got to be a better way.  This is to escape umsg.  Maybe insert directly into the DOM
        listHtml.push("<li>", $('<div />').text(umsg).html(), "</li>");
    }
    listHtml.push("</ul>");

    $("#ajax-failure-list").html(listHtml.join(""));
};

var ajaxFailureCallback = function(jqXHR, textStatus, errorThrown) {
    // override error handling

    var contentType = jqXHR.getResponseHeader("Content-Type");
    if(contentType)
        contentType = contentType.split(";")[0];

    /*
    if(typeof(error) === "function") {
     console.log("calling custom error method because error was", error);
        return error(jqXHR, textStatus, errorThrown);
    } else {
    */

        if(jqXHR.status == 403) {
            notifyAjaxFailure("Permission denied");
        } else if(contentType == "text/plain") {
            notifyAjaxFailure(jqXHR.responseText);
        } else {
            notifyAjaxFailure("Internal error");
        }
    //}
};

var handleAjaxError = function ( callback, useCallbackAndDefault ) {
    return function(request, status, error) {
        if(callback) {
            callback(request, status, error);
        }

        if(!callback || useCallbackAndDefault) {
            ajaxFailureCallback(request, status, error);
        }
    }
}

