//TODO : There is a lot of common code here. Need to refactor to make it DRY
//use GLOBAL params here
$(document).ready(function () {
    var spinnerImageLink = '<img src="/bardwebquery/static/images/loading_icon.gif"  height="16" width="16" />';
    var errorImageTwitterBootstrap = '<img src=""  class="icon-exclamation-sign" height="16" width="16" />';
    var autoOpts = {
        source:"/bardwebquery/bardWebInterface/autoCompleteAssayNames",
        minLength:2
    };
    $("#searchString").autocomplete(autoOpts);

    $('#aidForm').submit(function (event) {
        var searchType = findSearchType();

        switch (searchType.toUpperCase()) {
            case 'REGULAR':
                handleAssaySearch('searchAssays');
                handleCompoundSearch('searchCompounds');
                handleProjectSearch('searchProjects');
                break;
            case 'ID':
                //TODO: Right now we are treating Id searches like regular searches
                //i.e we send the ids to all 3 resources
                //We intend to change to a modal view, where a user picks
                //the type of id (like we do with structure searches) so that we
                //can only send the query to the resource of interest
                //we are also not intentionally paging here
                //subsequent iterations should support paging
                 handleAssaySearch('searchAssaysByIDs');
                handleCompoundSearch('searchCompoundsByIDs');
                handleProjectSearch('searchProjectsByIDs');
                break;
            case 'STRUCTURE':
                handleStructureSearch();
                break;


        }
        return false; //do not submit form the normal way, use Ajax instead

    });

    function findSearchType() {

        var searchString = $("#searchString").val();
        if (!$.trim(searchString).length) {  //if this is an empty string
            return "Empty";
        }
        var regex = /^\d+(?:, *\d+ *)*$/;
        if (searchString.match(regex)) {//this is an id match
            return "ID";
        }
        //we want to find out if this is a Structure search
        var searchStringSplit = searchString.split(":");
        var searchType = searchStringSplit[0];
        if (searchStringSplit.length == 2) { //is this a structure search
            searchType = searchType.toLowerCase();
            switch (searchType) { //must be one of these to qualify as a structure search
                case 'exact':
                case 'substructure':
                case 'superstructure':
                case 'similarity':
                    return 'STRUCTURE';
                    break;
            }
        }
        return "REGULAR"; //this a regular search
    }


    function handleAssaySearch(searchAssays) {
        var fullURL ='/bardwebquery/bardWebInterface/'+ searchAssays;
        $.ajax({
            url:fullURL,
            data:$("#aidForm").serialize(),
            cache:false,
            beforeSend:function () {
                $("#assaysTab").html("Assay Definitions " + spinnerImageLink);
            },
            success:function (data) {
                $("#assays").html(data);
                var assayTotal = 'Assay Definitions (' + $("#totalAssays").val() + ')' ;
                $("#assaysTab").html(assayTotal);
            } ,
            error:function (request, status, error) {
                $('#assaysTab').html("Assay Definitions " + errorImageTwitterBootstrap);
                $("#assays").html(error);
            },
            complete:function () {
            }
        });
    }

    function handleCompoundSearch(searchCompounds) {
        var fullURL ='/bardwebquery/bardWebInterface/'+ searchCompounds;
        $.ajax({
            url:fullURL,
            data:$("#aidForm").serialize(),
            cache:false,
            beforeSend:function () {
                $('#compoundsTab').html("Compounds " + spinnerImageLink);
            },
            success:function (data) {
                $("#compounds").html(data);
                var compoundTotal = 'Compounds (' + $("#totalCompounds").val() + ')';
                $("#compoundsTab").html(compoundTotal);
            },
            error:function (request, status, error) {
                $('#compoundsTab').html("Compounds " + errorImageTwitterBootstrap);
                $("#compounds").html(error);
            },
            complete:function () {
            }
        });
    }
    function handleProjectSearch(searchProjects) {
        var fullURL = '/bardwebquery/bardWebInterface/' + searchProjects;
        $.ajax({
            url:fullURL,
            data:$("#aidForm").serialize(),
            cache:false,
            beforeSend:function () {
                $('#projectsTab').html("Projects " + spinnerImageLink);
            },
            success:function (data) {
                //alert("Compounds: " + data)
                $("#projects").html(data);
                var projectsTotal = 'Projects (' + $("#totalProjects").val() + ')';
                $("#projectsTab").html(projectsTotal);
            },
            error:function (request, status, error) {
                $('#projectsTab').html("Projects " + errorImageTwitterBootstrap);
                $("#projects").html(error);
            },
            complete:function () {
            }
        });
    }

    /**
     * Handle structure searches { exact, Substructure, superstructure and similarity searches}
     */
    function handleStructureSearch() {
        var fullURL ='/bardwebquery/bardWebInterface/searchStructures';
        $.ajax({
            url:fullURL,
            data:$("#aidForm").serialize(),
            cache:false,
            beforeSend:function () {
                $('#compoundsTab').html("Compounds " + spinnerImageLink);
                $('#compounds').html('');
                $("#assaysTab").html('Assay Definitions (0)');
                $('#assays').html('');
                $('#assaysTabLi').removeClass('active');
                $("#projectsTab").html('Projects (0)');
                $('#projects').html('');
                $('#projectsTabLi').removeClass('active');
            },
            success:function (data) {
                $("#compounds").html(data);
                var compoundTotal = 'Compounds (' + $("#totalCompounds").val() + ')';
                $("#compoundsTab").html(compoundTotal);
                $("#compounds").tab('show');
            },
            error:function (request, status, error) {
                //TODO put in some code handling here. Dealing with time outs etc
            },
            complete:function () {
            }
        });
    }
    function resetTabsForStructureSearches(){
        $('#compoundsTab').html("Compounds " + spinnerImageLink);
        $('#compoundsTabLi').addClass('active') ;
        $('#compounds').html('');
        $("#assaysTab").html('Assay Definitions (0)');
        $('#assays').html('');
        $('#assaysTabLi').removeClass('active');
        $("#projectsTab").html('Projects (0)');
        $('#projects').html('');
        $('#projectsTabLi').removeClass('active');
    }
    function resetTabsForAssaySearches(){
        $('#compoundsTab').html("Assay Definitions " + spinnerImageLink);
        $('#assaysTabLi').addClass('active') ;
        $('#assays').html('');
        $("#compoundsTab").html('Compounds (0)');
        $('#compounds').html('');
        $('#compoundsTabLi').removeClass('active');
        $("#projectsTab").html('Projects (0)');
        $('#projects').html('');
        $('#projectsTabLi').removeClass('active');
    }
    function resetTabsForProjectSearches(){
        $('#compoundsTab').html("Projects " + spinnerImageLink);
        $('#projectsTabLi').addClass('active') ;
        $('#projects').html('');
        $("#compoundsTab").html('Compounds (0)');
        $('#compounds').html('');
        $('#compoundsTabLi').removeClass('active');
        $("#assaysTab").html('Projects (0)');
        $('#assays').html('');
        $('#assaysTabLi').removeClass('active');
    }
});
