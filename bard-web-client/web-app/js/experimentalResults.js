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

    //=== Handle Paging. We bind to all of the paging css classes on the anchor tag ===
    $(document).on("click", "a.step,a.nextLink,a.prevLink", function (event) {
        event.preventDefault();	// prevent the default action behaviour to happen
        var pagingurl = $(this).attr('href');
        pushStateHandler(pagingurl)
        populatePage(pagingurl);
    });


});
function handleFilteredQuery() {

}
//Handles pushing of state on history stack
function pushStateHandler(url) {
    history.pushState(url, null, url);
    return false;
}
function populatePage(url) {
    $.ajax({
        url:url,
        type:'GET',
        cache:false,
        //timeout: 10000,
        beforeSend:function () {
            //use the main form page
            $('#experimentalResults').html(bigSpinnerImage);
        },
        success:function (experimentalResultsData) {
            $('#experimentalResults').html(experimentalResultsData);
            $(".pop_smiles").popover();

        },
        error:function () {
            $('#experimentalResults').html('No data found');
        },
        complete:function () {

        }
    });
}

//Adding event handlers to the facets form submission
$(document).on("submit", "#ExperimentFacetForm", function (event) {
    //replace the action with a redirect to the same page
    var skip = $('#skip').attr('value');
    var top = $('#top').attr('value');
    var formUrl = '/bardwebclient/bardWebInterface/showExperiment/' + $('input#experimentId').attr('value') + '?offset=' + skip + '&max=' + top;
    $(this).attr('action', formUrl)
    return true; //submit tue form the normal way
});

$(document).on("click", "#ExperimentFacetForm_ResetButton", function () {
    resetAllFilters('ExperimentFacetForm');
});

function resetAllFilters(facetForm) {
    //Uncheck all filters for the current form
    $('#' + facetForm + ' input[type="checkbox"]').attr('checked', false);
    //Resubmit the form
    $('#' + facetForm).submit();
}
