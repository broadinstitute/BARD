/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

