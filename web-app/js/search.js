//use GLOBAL params here
var spinnerImageLink = '<img src="/bardwebquery/static/images/loading_icon.gif" alt="loading" title="loading" height="16" width="16"/>';
var errorImageTwitterBootstrap = '<img src=""  class="icon-exclamation-sign" alt="error" height="16" width="16" />';

$(document).ready(function () {

    //set up auto complete
    var autoOpts = {
        source:"/bardwebquery/bardWebInterface/autoCompleteAssayNames",
        minLength:2
    };
    $("#searchString").autocomplete(autoOpts);
    $("#searchString").bind("autocompleteselect", function (event, ui) {
        $("#searchButton").click();
    });
    // make sure to close the autocomplete box when the search button is clicked
    $("#searchButton").click(function () {
        $("#searchString").autocomplete("close");
    });


    //set up form submission
    $('#searchForm').submit(function (event) {
        var searchString = $("#searchString").val();
        handleMainFormSubmit(searchString);
        return false; //do not submit form the normal way, use Ajax instead

    });


    //set up filter form submissions
    //We are using live because we want to do late binding
    //these forms would not exist when the document first loads
    $('#AssayFacetForm').live('submit', function (event) {
        var searchString = $("#searchString").val();
        handleFilteredQuery(searchString, 'AssayFacetForm', 'AssayFacetForm', 'assaysTab', 'totalAssays', 'assays', 'Assay Definitions ');
        return false; //do not submit form the normal way, use Ajax instead

    });
    $('#ProjectFacetForm').live('submit', function (event) {
        var searchString = $("#searchString").val();
        handleFilteredQuery(searchString, 'ProjectFacetForm', 'ProjectFacetForm', 'projectsTab', 'totalProjects', 'projects', 'Projects ');
        return false; //do not submit form the normal way, use Ajax instead
    });
    $('#CompoundFacetForm').live('submit', function (event) {
        var searchString = $("#searchString").val();
        handleFilteredQuery(searchString, 'CompoundFacetForm', 'CompoundFacetForm', 'compoundsTab', "totalCompounds", 'compounds', 'Compounds ')
        return false; //do not submit form the normal way, use Ajax instead

    });
});

///**
// * @param controllerAction -
// * @param currentFormId - The id of the currently selected form
// * @param currentTabId - The id of the currently selected tab
// * @param numberOfHitsDivId - The id of the div where we would display the total number of hits
// * @param updateId - The id of the div where we would display the results of the ajax call
// * @param tabDisplayPrefix - The prefix for the string that we would display on the currently selected tab (for example 'Assay Definitions')
// */
//function handleFilterFormSubmit(controllerAction, currentFormId, currentTabId, numberOfHitsDivId, updateId, tabDisplayPrefix) {
//
//    var fullURL = '/bardwebquery/bardWebInterface/' + controllerAction;
//    var currentTabDivId = '#' + currentTabId;
//    var totalHitsElement = '#' + numberOfHitsDivId;
//    var updateDivId = '#' + updateId;
//    var formId = '#' + currentFormId;
//
//    $.ajax({
//        url:fullURL,
//        type:'POST',
//        data:$(formId).serialize(),
//        cache:false,
//        beforeSend:function () {
//            $(currentTabDivId).html(tabDisplayPrefix + spinnerImageLink);
//        },
//        success:function (data) {
//            $(updateDivId).html(data);
//            var total = tabDisplayPrefix + ' (' + $(totalHitsElement).val() + ')';
//            $(currentTabDivId).html(total);
//        },
//        error:function (request, status, error) {
//            $(currentTabDivId).html(tabDisplayPrefix + errorImageTwitterBootstrap);
//            $(updateDivId).html(error);
//        },
//        complete:function () {
//        }
//    });
//}
/**
 * Handle structure searches { exact, Substructure, superstructure and similarity searches}
 */
function handleStructureSearch(currentFormId) {
    var searchForm = "#" + currentFormId;

    var fullURL = '/bardwebquery/bardWebInterface/searchStructures';
    $.ajax({
        url:fullURL,
        type:'POST',
        data:$(searchForm).serialize(),
        cache:false,
        beforeSend:function () {
            resetTabsForStructureSearches();
        },
        success:function (data) {
            $("#compounds").html(data);
            var compoundTotal = 'Compounds (' + $("#totalCompounds").val() + ')';
            $("#compoundsTab").html(compoundTotal);
            $("#compounds").tab('show');
        },
        error:function (request, status, error) {
            $("#compoundsTab").html('Compounds ' + errorImageTwitterBootstrap);
            $("#compounds").html(error);
        },
        complete:function () {
        }
    });
}
/**
 *
 * @param controllerAction - The name of the controller action that would handle this request e.g 'searchAssays'
 * @param tabId - The ID of the tab where the results should be displayed  e.g 'assaysTab'
 * @param totalHitsForResourceId  - The ID of the hidden field that would hold the total number of hits
 * @param prefixOfTextToAppearOnTab - The start of the string to display on the tab e.g 'Assay Definitions '
 * @param updateDiv - Where the results will be displayed
 */
function handleSearch(controllerAction, currentFormId, tabId, totalHitsForResourceId, prefixOfTextToAppearOnTab, updateDiv) {
    var fullURL = '/bardwebquery/bardWebInterface/' + controllerAction;
    var tabDivElement = '#' + tabId;
    var totalHitsElement = '#' + totalHitsForResourceId;
    var updateDivId = '#' + updateDiv;
    var searchForm = "#" + currentFormId;
    $.ajax({
        url:fullURL,
        type:'POST',
        data:$(searchForm).serialize(),
        cache:false,
        beforeSend:function () {
            $(tabDivElement).html(prefixOfTextToAppearOnTab + spinnerImageLink);
        },
        success:function (data) {
            $(updateDivId).html(data);
            var total = prefixOfTextToAppearOnTab + '(' + $(totalHitsElement).val() + ')';
            $(tabDivElement).html(total);
        },
        error:function (request, status, error) {
            $(tabDivElement).html(prefixOfTextToAppearOnTab + errorImageTwitterBootstrap);
            $(updateDivId).html(error);
        },
        complete:function () {
        }
    });
}
/**
 * TODO: There ought to be a better way of doing this. Please refactor
 * Handles form submission
 * @param searchString
 */
function handleFilteredQuery(searchString, facetFormType, currentFormId, currentTabId, numberOfHitsDivId, updateId, tabDisplayPrefix) {

    var controllerAction = findTheAppropriateControllerActionForRequest(searchString, facetFormType)
    if (controllerAction == 'structureSearch') {
        handleStructureSearch(currentFormId)
    }
    else if (controllerAction != 'EMPTY') {
        handleSearch(controllerAction, currentFormId, currentTabId, numberOfHitsDivId, tabDisplayPrefix, updateId);
    }

}
function findTheAppropriateControllerActionForRequest(searchString, facetFormType) {

    var searchType = findSearchType(searchString);

    //find the right controller to send request to
    switch (searchType.toUpperCase()) {
        case 'FREE_TEXT':
            if (facetFormType == 'AssayFacetForm') {
                return "searchAssays"
            }
            if (facetFormType == 'ProjectFacetForm') {
                return "searchProjects"
            }
            if (facetFormType == 'CompoundFacetForm') {
                return "searchCompounds"
            }
            break;
        case 'ID':
            if (facetFormType == 'AssayFacetForm') {
                return 'searchAssaysByIDs'
            }
            if (facetFormType == 'ProjectFacetForm') {
                return 'searchProjectsByIDs'
            }
            if (facetFormType == 'CompoundFacetForm') {
                return 'searchCompoundsByIDs'
            }
            break;
        case 'STRUCTURE':
            if (facetFormType == 'CompoundFacetForm') {
                return  'structureSearch'
            }
            break;
    }
    return "EMPTY"

}

/**
 * Handles form submission
 * @param searchString
 */
function handleMainFormSubmit(searchString) {

    var searchType = findSearchType(searchString);

    switch (searchType.toUpperCase()) {
        case 'FREE_TEXT':
            handleAllFreeTextSearches();
            break;
        case 'ID':
            //TODO: Right now we are treating Id searches like regular searches
            //i.e we send the ids to all 3 resources
            //We intend to change to a modal view, where a user picks
            //the type of id (like we do with structure searches) so that we
            //can only send the query to the resource of interest
            handleAllIdSearches();
            break;
        case 'STRUCTURE':
            handleStructureSearch('searchForm');
            break;
    }
}
/**
 * Handle all free text searches
 */
function handleAllFreeTextSearches() {
    handleSearch('searchAssays', 'searchForm', 'assaysTab', 'totalAssays', 'Assay Definitions ', 'assays');
    handleSearch('searchCompounds', 'searchForm', 'compoundsTab', 'totalCompounds', 'Compounds ', 'compounds');
    handleSearch('searchProjects', 'searchForm', 'projectsTab', 'totalProjects', 'Projects ', 'projects');
}

/**
 * Handle all ID searches
 */
function handleAllIdSearches() {
    handleSearch('searchAssaysByIDs', 'searchForm', 'assaysTab', 'totalAssays', 'Assay Definitions ', 'assays');
    handleSearch('searchCompoundsByIDs', 'searchForm', 'compoundsTab', 'totalCompounds', 'Compounds ', 'compounds');
    handleSearch('searchProjectsByIDs', 'searchForm', 'projectsTab', 'totalProjects', 'Projects ', 'projects');
}
/**
 * Make Tabs inactive
 * @param tabId - The ID of the tab
 * @param tabListId - The ID of the tab list li element
 * @param resourceId - The ID of the resource (assays, compounds or projects)
 * @param tabDisplayPrefix - The prefix to display on tab
 */
function deActivateTabs(tabId, tabListId, resourceId, tabDisplayPrefix) {
    var tabIdElement = '#' + tabId;
    var resourceIdElement = '#' + resourceId;
    var tabListIdElement = '#' + tabListId;

    $(tabIdElement).html(tabDisplayPrefix);
    $(resourceIdElement).html('');
    $(tabListIdElement).removeClass('active');
}
/**
 * Make Tabs active
 * @param tabId - The ID of the tab
 * @param tabListId - The ID of the tab list li element
 * @param resourceId - The ID of the resource (assays, compounds or projects)
 * @param tabDisplayPrefix - The prefix to display on tab
 */
function activateTabs(tabId, tabListId, resourceId, tabDisplayPrefix) {
    var tabIdElement = '#' + tabId;
    var tabListIdElement = '#' + tabListId;
    var resourceIdElement = '#' + resourceId;

    $(tabIdElement).html(tabDisplayPrefix + spinnerImageLink);
    $(resourceIdElement).html('');
    $(tabListIdElement).addClass('active');
}
/**
 * Reset the tabs for structure searches
 * Make the counts zero for each tab, before you start the search
 */
function resetTabsForStructureSearches() {
    activateTabs('compoundsTab', 'compoundsTabLi', 'compounds', "Compounds ");
    deActivateTabs('assaysTab', 'assaysTabLi', 'assays', 'Assay Definitions (0)');
    deActivateTabs('projectsTab', 'projectsTabLi', 'projects', 'Projects (0)');
}
/**
 * Find the search type
 * @return {String} - One of 'FREE_TEXT', 'STRUCTURE', 'EMPTY' or 'ID'
 */
function findSearchType(searchString) {

    if (!$.trim(searchString).length) {  //if this is an empty string, do nothing
        return "EMPTY";
    }
    var numberMatchingRegex = /^\d+(?:, *\d+ *)*$/; //-- Potential performance issue, because we look at every thing
    if (searchString.match(numberMatchingRegex)) {//this is an id match
        return "ID";
    }
    //we want to find out if this is a Structure search
    var searchStringSplit = searchString.split(":");
    var searchType = searchStringSplit[0];
    if (searchStringSplit.length == 2 && $.trim(searchStringSplit[1]).length) { //has to be of the form Exact:CCC so there must be 2 things in the array
        searchType = searchType.toLowerCase();
        switch (searchType) { //must be one of these to qualify as a structure search
            case 'exact':
            case 'substructure':
            case 'superstructure':
            case 'similarity':
                return 'STRUCTURE';
        }
    }
    return "FREE_TEXT"; //this a Free Text Search
}
