var bigSpinnerImage = '<div class="tab-message"><img src="/bardwebclient/static/images/ajax-loader.gif" alt="loading" title="loading"/></div>';
//TODO: if javascript is not supported then do a full page load


$(document).ready(function () {
    $("[rel=tooltip]").tooltip();
    // hang on popstate event triggered by pressing back/forward button in browser
    window.onpopstate = function (event) {
        var returnLocation = history.location || document.location;
        event.preventDefault();	// prevent the default action behaviour to happen
        populatePage(returnLocation);
    };
    $(document).on("click", "a.desc_tip", function (event) {
        return false;
    });
    $(document).on("mouseover", "a.desc_tip", function (event) {
        $(this).tooltip();
    });
    $(document).on("submit", "#showExperimentForm", function (event) {
        event.preventDefault();	// prevent the default action behaviour to happen
        var max = $('#top').val()
        var offset = $('#skip').val()
        var url = $(this).attr('action') + "?" + $(this).serialize() + '&max=' + max + '&offset=' + offset;

        pushStateHandler(url)
        populatePage(url);
    });
});

//Handles pushing of state on history stack
function pushStateHandler(url) {
    history.pushState(url, null, url);
    return false;
}

//Adding event handlers to the showExperiment facets form submission
$(document).on("submit", "#ExperimentFacetForm", function (event) {
    //replace the action with a redirect to the same page
    var paginationUrl = $('#paginationUrl').attr('value')
    var formUrl
    if (paginationUrl) {
        formUrl = paginationUrl;
    } else {
        formUrl = '/bardwebclient/bardWebInterface/showExperiment/' + $('input#experimentId').attr('value');
    }
    $(this).attr('action', formUrl)
    return true; //submit tue form the normal way
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
        formUrl = '/bardwebclient/bardWebInterface/showCompoundBioActivitySummary/' + $('input#compoundId').attr('value');
    }
    $(this).attr('action', formUrl)
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
    pushStateHandler(url);
    $('#ExperimentFacetForm').submit(); //submit, and let the event handler handle the redirect.
});
