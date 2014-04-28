%{-- Copyright (c) 2014, The Broad Institute
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
 --}%

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
