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

//TODO: if javascript is not supported then do a full page load


$(document).ready(function () {
    $("[rel=tooltip]").tooltip();
    // hang on popstate event triggered by pressing back/forward button in browser
    window.onpopstate = function (event) {
        var returnLocation = history.location || document.location;
        event.preventDefault();	// prevent the default action behaviour to happen
//        populatePage(returnLocation);
    };
    $(document).on("click", "a.desc_tip", function (event) {
        return false;
    });
    $(document).on("mouseover", "a.desc_tip", function (event) {
        $(this).tooltip();
    });
//    $(document).on("submit", "#showExperimentForm", function (event) {
//        event.preventDefault();	// prevent the default action behaviour to happen
//        var max = $('#top').val()
//        var offset = $('#skip').val()
//        var url = $(this).attr('action') + "?" + $(this).serialize() + '&max=' + max + '&offset=' + offset;
//
//        pushStateHandler(url)
////        populatePage(url);
//    });
});

//Handles pushing of state on history stack
function pushStateHandler(url) {
    history.pushState(url, null, url);
    return false;
}

//Adding event handlers to the showExperiment facets form submission
$(document).on("submit", "#ExperimentFacetForm", function (event) {
    event.preventDefault();	// prevent the default action behaviour to happen
    //replace the action with a redirect to the same page
    var paginationUrl = $('#paginationUrl').attr('value')
    var formUrl
    if (paginationUrl) {
        formUrl = paginationUrl + '&';
    } else {
        formUrl = bardAppContext + '/bardWebInterface/showExperiment/' + $('input#experimentId').attr('value') + '?';
    }
    formUrl +=  $('#ExperimentFacetForm').serialize(); //add the filters
//    $(this).attr('action', formUrl).attr('method', 'get');
    window.location.href=formUrl //move to the new page.
    return false; //prevent form submission.
});
$(document).on("click", "#ExperimentFacetForm_ResetButton", function () {
    resetAllFilters('ExperimentFacetForm');
});

//Adding event handlers to the showCBAS facets form submission
$(document).on("submit", "#CompoundBioActivitySummaryForm", function (event) {
    //replace the action with a redirect to the same page
    var paginationUrl = $('#paginationUrl').attr('value')
    var formUrl
    if (paginationUrl) {
        formUrl = paginationUrl;
    } else {
        formUrl = bardAppContext + '/bardWebInterface/showCompoundBioActivitySummary/' + $('input#compoundId').attr('value');
    }
    $(this).attr('action', formUrl);
    return true; //submit tue form the normal way
});

$(document).on("click", "#CompoundBioActivitySummaryForm_ResetButton", function () {
    resetAllFilters('CompoundBioActivitySummaryForm');
});

function resetAllFilters(facetForm) {
    //Uncheck all filters for the current form
    $('#' + facetForm + ' input[type="checkbox"]').attr('checked', false);
    //Resubmit the form
    $('#' + facetForm).submit();
}

//=== Handle Paging. We bind to all of the paging css classes on the anchor tag ===
$('#showExperimentDiv').on("click", "a.step,a.nextLink,a.prevLink", function (event) {
    event.preventDefault();	// prevent the default action behaviour from happening
    var url = $(this).attr('href');
    $('#paginationUrl').attr('value', url); //save the pagination url for submission
//    pushStateHandler(url);
    $('#ExperimentFacetForm').submit(); //submit, and let the event handler handle the redirect.
});
