<!-- page 'searchResults' -->
<div data-role="page" id="searchResults">
    <div>
        <g:render template="/layouts/templates/mobileLoginStrip"/>
    </div>

    <div data-role="header">
        <h1>Search Results</h1>
    </div><!-- /header -->

    <div data-role="content">
        <p><a href="#assays" data-role="button" data-icon="arrow-r" data-iconpos="right" data-transition="slide"
              id="assaysTab">Assay Definitions (0)</a></p>

        <p><a href="#compounds" data-role="button" data-icon="arrow-r" data-iconpos="right" data-transition="slide"
              id="compoundsTab">Compounds (0)</a></p>

        <p><a href="#projects" data-role="button" data-icon="arrow-r" data-iconpos="right" data-transition="slide"
              id="projectsTab">Projects (0)</a></p>

        <p><a href="#search" data-role="button" data-inline="true" data-icon="back" data-transition="none">Back</a></p>
    </div>
</div>

<!-- page 'assays' -->
<div data-role="page" id="assays">
    <div data-role="content">
        <h1>Assays page</h1>
        %{--<g:render template="assays"/>--}%
        <p><a href="#searchResults" data-role="button" data-inline="true" data-icon="back"
              data-transition="none">Back</a></p>
    </div>
</div>

<!-- page 'compounds' -->
<div data-role="page" id="compounds">
    <div data-role="content">
        <h1>Compounds page</h1>
        %{--<g:render template="compounds"/>--}%
        <p><a href="#searchResults" data-role="button" data-inline="true" data-icon="back"
              data-transition="none">Back</a></p>
    </div>
</div>

<!-- page 'projects' -->
<div data-role="page" id="projects">
    <div data-role="content">
        <h1>Projects page</h1>
        %{--<g:render template="projects"/>--}%
        <p><a href="#searchResults" data-role="button" data-inline="true" data-icon="back"
              data-transition="none">Back</a></p>
    </div>
</div>
