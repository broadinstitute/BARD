//use GLOBAL params here
$(document).ready(function () {
    var spinnerImageLink = '<img src="/bardwebquery/static/images/loading_icon.gif" alt="loading" title="loading" height="16" width="16" />';
    var errorImageTwitterBootstrap = '<img src=""  class="icon-exclamation-sign" alt="error" height="16" width="16" />';
    var autoOpts = {
        source:"/bardwebquery/bardWebInterface/autoCompleteAssayNames",
        minLength:2
    };
    $("#searchString").autocomplete(autoOpts);

    $('#aidForm').submit(function (event) {
        var searchType = findSearchType();

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
                handleStructureSearch();
                break;


        }
        return false; //do not submit form the normal way, use Ajax instead

    });
    /**
     *
     */
    function handleAllFreeTextSearches() {
        handleSearch('searchAssays', 'assaysTab', 'totalAssays', 'Assay Definitions ', 'assays');
        handleSearch('searchCompounds', 'compoundsTab', 'totalCompounds', 'Compounds ', 'compounds');
        handleSearch('searchProjects', 'projectsTab', 'totalProjects', 'Projects ', 'projects');
    }

    /**
     *
     */
    function handleAllIdSearches() {
        handleSearch('searchAssaysByIDs', 'assaysTab', 'totalAssays', 'Assay Definitions ', 'assays');
        handleSearch('searchCompoundsByIDs', 'compoundsTab', 'totalCompounds', 'Compounds ', 'compounds');
        handleSearch('searchProjectsByIDs', 'projectsTab', 'totalProjects', 'Projects ', 'projects');
    }

    /**
     * Find the search type
     * @return {String} - One of 'FREE_TEXT', 'STRUCTURE', 'EMPTY' or 'ID'
     */
    function findSearchType() {

        var searchString = $("#searchString").val();
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
        if (searchStringSplit.length == 2) { //has to be of the form Exact:CCC so there must be 2 things in the array
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

    /**
     *
     * @param controllerAction - The name of the controller action that would handle this request e.g 'searchAssays'
     * @param tabId - The ID of the tab where the results should be displayed  e.g 'assaysTab'
     * @param totalHitsForResourceId  - The ID of the hidden field that would hold the total number of hits
     * @param displayStringPrefix - The start of the string to display on the tab e.g 'Assay Definitions '
     * @param updateDiv - Where the results will be displayed
     */
    function handleSearch(controllerAction, tabId, totalHitsForResourceId, displayStringPrefix, updateDiv) {
        var fullURL = '/bardwebquery/bardWebInterface/' + controllerAction;
        var tabDivElement = '#' + tabId;
        var totalHitsElement = '#' + totalHitsForResourceId;
        var updateDivId = '#' + updateDiv;

        $.ajax({
            url:fullURL,
            data:$("#aidForm").serialize(),
            cache:false,
            beforeSend:function () {
                $(tabDivElement).html(displayStringPrefix + spinnerImageLink);
            },
            success:function (data) {
                $(updateDivId).html(data);
                var total = displayStringPrefix + '(' + $(totalHitsElement).val() + ')';
                $(tabDivElement).html(total);
            },
            error:function (request, status, error) {
                $(tabDivElement).html(displayStringPrefix + errorImageTwitterBootstrap);
                $(updateDivId).html(error);
            },
            complete:function () {
            }
        });
    }

    /**
     * Handle structure searches { exact, Substructure, superstructure and similarity searches}
     */
    function handleStructureSearch() {
        var fullURL = '/bardwebquery/bardWebInterface/searchStructures';
        $.ajax({
            url:fullURL,
            data:$("#aidForm").serialize(),
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

    function resetTabsForStructureSearches() {
        activateTabs('compoundsTab', 'compoundsTabLi', 'compounds', "Compounds ");
        deActivateTabs('assaysTab', 'assaysTabLi', 'assays', 'Assay Definitions (0)');
        deActivateTabs('projectsTab', 'projectsTabLi', 'projects', 'Projects (0)');
    }
});
