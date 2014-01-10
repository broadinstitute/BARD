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

    console.log("msg", msg);
    console.log("ajaxErrors", ajaxErrors);
    console.log("listHtml", listHtml)

    $("#ajax-failure-list").html(listHtml.join(""));
};

var ajaxFailureCallback = function(jqXHR, textStatus, errorThrown) {
    // override error handling
    console.log("error", jqXHR, textStatus, errorThrown);

    var contentType = jqXHR.getResponseHeader("Content-Type");
    if(contentType)
        contentType = contentType.split(";")[0];

    /*
    if(typeof(error) === "function") {
     console.log("calling custom error method because error was", error);
        return error(jqXHR, textStatus, errorThrown);
    } else {
    */
    console.log("contentType=",contentType);
    console.log("responseText=",jqXHR.responseText);

        if(jqXHR.status == 403) {
            notifyAjaxFailure("Permission denied");
        } else if(contentType == "text/plain") {
            notifyAjaxFailure(jqXHR.responseText);
        } else {
            notifyAjaxFailure("Internal error");
        }
    //}
};

var handleAjaxError = function ( callback ) {
    return function(request, status, error) {
        if(callback) {
            callback(request, status, error);
        }
        ajaxFailureCallback(request, status, error);
    }
}

/*
$.ajaxPrefilter(function(options, originalOptions, jqXHR) {
    console.log("options", options);
    console.log("originalOptions", originalOptions);

    var success = options.success;
    options.success = function(data, textStatus, jqXHR) {
        // override success handling
        console.log("success");
        if(typeof(success) === "function") return success(data, textStatus, jqXHR);
    };

    var error = options.error;
    options.error = ;
});
*/