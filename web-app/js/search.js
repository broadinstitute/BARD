//TODO : There is a lot of common code here. Need to refactor to make it DRY
$(document).ready(function () {
    var spinnerImageLink = '<img src="/bardwebquery/static/images/loading_icon.gif"  height="16" width="16" />';
    var errorImageTwitterBootstrap = '<img src=""  class="icon-exclamation-sign" height="16" width="16" />';
    var autoOpts = {
        source:"/bardwebquery/bardWebInterface/autoCompleteAssayNames",
        minLength:2
    };
    $("#searchString").autocomplete(autoOpts);

    $('#aidForm').submit(function (event) {
        var searchType = findSearchType()

        switch (searchType.toUpperCase()) {
            case 'REGULAR':
                handleAssaySearch();
                handleCompoundSearch();
                handleProjectSearch();
                break;
            case 'ID':
                handleAIDSearch();
                handleCIDSearch();
                handlePIDSearch();
                 break;
            case 'STRUCTURE':
                handleStructureSearch()
                break;


        }
        return false; //do not submit form the normal way, use Ajax instead

    });

    function findSearchType() {

        var searchString = $("#searchString").val()
        if (!$.trim(searchString).length) {  //if this is an empty string
            return "Empty"
        }
        var regex = /^\d+(?:, *\d+ *)*$/;
        if (searchString.match(regex)) {//this is an id match
            return "ID"
        }
        //we want to find out if this is a Structure search
        var searchStringSplit = searchString.split(":");
        var searchType = searchStringSplit[0]
        if (searchStringSplit.length == 2) { //is this a structure search
            searchType = searchType.toLowerCase()
            switch (searchType) { //must be one of these to qualify as a structure search
                case 'exact':
                case 'substructure':
                case 'superstructure':
                case 'similarity':
                    return 'STRUCTURE'
                    break;
            }
        }
        return "REGULAR" //this a regular search
    }


    function handleAssaySearch() {
        $.ajax({
            url:'/bardwebquery/bardWebInterface/searchAssays',
            data:$("#aidForm").serialize(),
            cache:false,
            beforeSend:function () {
                $("#assaysTab").html("Assays " + spinnerImageLink);
            },
            success:function (data) {
                $("#assays").html(data);
                var assayTotal = 'Assays (' + $("#totalAssays").val() + ')'
                $("#assaysTab").html(assayTotal);
            } ,
            error:function (request, status, error) {
                $('#assaysTab').html("Assays " + errorImageTwitterBootstrap);
                $("#assays").html(error);
            },
            complete:function () {
            }
        });
    }

    function handleCompoundSearch() {
        $.ajax({
            url:'/bardwebquery/bardWebInterface/searchCompounds',
            data:$("#aidForm").serialize(),
            cache:false,
            beforeSend:function () {
                $('#compoundsTab').html("Compounds " + spinnerImageLink);
            },
            success:function (data) {
                $("#compounds").html(data);
                var compoundTotal = 'Compounds (' + $("#totalCompounds").val() + ')'
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
    function handleCIDSearch() {
        $.ajax({
            url:'/bardwebquery/bardWebInterface/searchCompoundsByIDs',
            data:$("#aidForm").serialize(),
            cache:false,
            beforeSend:function () {
                $('#compoundsTab').html("Compounds " + spinnerImageLink);
            },
            success:function (data) {
                $("#compounds").html(data);
                var compoundTotal = 'Compounds (' + $("#totalCompounds").val() + ')'
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
    function handlePIDSearch() {
        $.ajax({
            url:'/bardwebquery/bardWebInterface/searchProjectsByIDs',
            data:$("#aidForm").serialize(),
            cache:false,
            beforeSend:function () {
                $('#projectsTab').html("Projects " + spinnerImageLink);
            },
            success:function (data) {
                //alert("Compounds: " + data)
                $("#projects").html(data);
                var projectsTotal = 'Projects (' + $("#totalProjects").val() + ')'
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
    function handleAIDSearch() {
        $.ajax({
            url:'/bardwebquery/bardWebInterface/searchAssaysByIDs',
            data:$("#aidForm").serialize(),
            cache:false,
            beforeSend:function () {
                $("#assaysTab").html("Assays " + spinnerImageLink);
            },
            success:function (data) {
                $("#assays").html(data);
                var assayTotal = 'Assays (' + $("#totalAssays").val() + ')'
                $("#assaysTab").html(assayTotal);
            } ,
            error:function (request, status, error) {
                $('#assaysTab').html("Assays " + errorImageTwitterBootstrap);
                $("#assays").html(error);
            },
            complete:function () {
            }
        });
    }
    function handleProjectSearch() {
        $.ajax({
            url:'/bardwebquery/bardWebInterface/searchProjects',
            data:$("#aidForm").serialize(),
            cache:false,
            beforeSend:function () {
                $('#projectsTab').html("Projects " + spinnerImageLink);
            },
            success:function (data) {
                //alert("Compounds: " + data)
                $("#projects").html(data);
                var projectsTotal = 'Projects (' + $("#totalProjects").val() + ')'
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

    function handleStructureSearch() {
        $.ajax({
            url:'/bardwebquery/bardWebInterface/searchStructures',
            data:$("#aidForm").serialize(),
            cache:false,
            beforeSend:function () {
                $('#compoundsTab').html("Compounds " + spinnerImageLink);
                $('#compoundsTabLi').addClass('active')
                $('#compounds').html('');
                $("#assaysTab").html('Assays (0)');
                $('#assays').html('');
                $('#assaysTabLi').removeClass('active');
                $("#projectsTab").html('Projects (0)');
                $('#projects').html('');
                $('#projectsTabLi').removeClass('active');
            },
            success:function (data) {
                $("#compounds").html(data);
                var compoundTotal = 'Compounds (' + $("#totalCompounds").val() + ')'
                $("#compoundsTab").html(compoundTotal);
            },
            error:function (request, status, error) {
                //TODO put in some code handling here. Dealing with time outs etc
            },
            complete:function () {
            }
        });
    }
});
