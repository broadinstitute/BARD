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

        <p><a href="#search" data-role="button" data-inline="true" data-icon="back" data-transition="none">Back</a></p>
    </div>
</div>

<!-- page 'assays' -->
<div data-role="page" id="assaysDiv">
    <div data-role="content" id="assays">
    </div>
    <p><a href="#searchResults" data-role="button" data-inline="true" data-icon="back" data-transition="none">Back</a></p>
</div>

<!-- page 'compounds' -->
<div data-role="page" id="compoundsDiv">
    <div data-role="content" id="compounds">
    </div>
    <p><a href="#searchResults" data-role="button" data-inline="true" data-icon="back" data-transition="none">Back</a></p>
</div>

<!-- page 'projects' -->
<div data-role="page" id="projectsDiv">
    <div data-role="content" id="projects">
    </div>
    <p><a href="#searchResults" data-role="button" data-inline="true" data-icon="back" data-transition="none">Back</a></p>
</div>
