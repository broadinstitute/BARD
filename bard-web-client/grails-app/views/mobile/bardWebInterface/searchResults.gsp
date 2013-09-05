<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <title>BioAssay Research Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <r:require modules="jqueryMobile, search, bootstrap, core"/>
    <r:layoutResources/>
    <r:script>
        $(document).ready(function () {
            //Call the JavaScript method to populate the divs
            var searchString = "${params.searchString}"
            handleMainFormSubmit(searchString)

            //Generate the BACK button behavior: redirect back to the Search page and send the searchString param with it.
            $('#backToSearch').click(function() {
                var searchString = $('#searchString').attr('value');
                var urlToSearch = "${createLink(controller: 'bardWebInterface', action: 'search')}?searchString=" + searchString;
                window.location.href = urlToSearch;
        });

        });

        $(document).on('search.complete', '#assaysDiv, #compoundsDiv, #projectsDiv', function () {
            //Restore the JQuery Mobile navigation button style after overriding the button's html with the total-value.
            updateNavigationButtonStyle($('#assaysTab'));
            updateNavigationButtonStyle($('#compoundsTab'));
            updateNavigationButtonStyle($('#projectsTab'));
        });


        //Resize structure thumbnails with every window-resize even.
        $(window).resize(adjustStructureThumbnailSize);

        //      We need that to adjust the size of the structure image dynamically based on the screen size
        function adjustStructureThumbnailSize() {
            var w = Math.round(window.innerWidth / 3);
            var width = (w > 180) ? 180 : w;
            var height = Math.round(width * 5 / 6);
            $('.structureThumbnail').attr('width', width).attr('height', height);
        }

        function updateNavigationButtonStyle(buttonElement) {
            var existingHtml = $(buttonElement).text();
            var newHtml = '<span class="ui-btn-inner ui-btn-corner-all"><span class="ui-btn-text">' +
    existingHtml +
    '</span>' +
    '<span class="ui-icon ui-icon-arrow-r ui-icon-shadow"></span></span>'
            $(buttonElement).html(newHtml);
        }
    </r:script>
</head>

<body>
%{--A hidden form to store the search string for search.js handleSearchText() method--}%
<form name="searchForm" id="searchForm">
    <g:hiddenField name="searchString" value="${params?.searchString}" id="searchString"/>
</form>
<!-- page 'searchResults' -->
<div data-role="page" id="searchResults">
    <div>
        <g:render template="/layouts/templates/mobileLoginStrip"/>
    </div>

    <div data-role="header">
        <h1>Search Results</h1>
    </div><!-- /header -->

    <div data-role="content">
        <p><a href="#assaysDiv" data-role="button" data-icon="arrow-r" data-iconpos="right" data-transition="slide"
              id="assaysTab">Assay Definitions (0)</a></p>

        <p><a href="#compoundsDiv" data-role="button" data-icon="arrow-r" data-iconpos="right" data-transition="slide"
              id="compoundsTab">Compounds (0)</a></p>

        <p><a href="#projectsDiv" data-role="button" data-icon="arrow-r" data-iconpos="right" data-transition="slide"
              id="projectsTab">Projects (0)</a></p>

        <p><a id="backToSearch" href="" data-role="button" data-inline="true" data-icon="back"
              data-transition="none">Back</a>
            %{--<input type="button" id="backToSearch" data-role="button" data-inline="true" data-icon="back"--}%
            %{--data-transition="none">Back</input>--}%
    </div>
</div>

<!-- page 'assays' -->
<div data-role="page" id="assaysDiv">
    <div data-role="content" id="assays">
    </div>

    <p><a href="#searchResults" data-role="button" data-inline="true" data-icon="back" data-transition="none">Back</a>
    </p>
</div>

<!-- page 'compounds' -->
<div data-role="page" id="compoundsDiv">
    <div data-role="content" id="compounds">
    </div>

    <a href="#searchResults" data-role="button" data-inline="true" data-icon="back" data-transition="none">Back</a>
</div>

<!-- page 'projects' -->
<div data-role="page" id="projectsDiv">
    <div data-role="content" id="projects">
    </div>

    <p><a href="#searchResults" data-role="button" data-inline="true" data-icon="back" data-transition="none">Back</a>
    </p>
</div>

<r:layoutResources/>xmlns="http://www.w3.org/1999/html"
</body>
</html>