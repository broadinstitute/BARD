<!-- page 'search' -->
<div data-role="page" id="search">
    <div>
        <g:render template="/layouts/templates/mobileLoginStrip"/>
    </div>

    <div data-role="content" style="text-align: center">
        <a href="${createLink(controller: 'BardWebInterface', action: 'index')}">
            <img src="${resource(dir: 'images', file: 'bard_logo_lg.jpg')}" align="middle" style="max-width: 80%;"
                 alt="BioAssay Research Database"/>
        </a>

        <div>
            <g:form name="searchForm" action="bardWebInterface/search" method="post" id="searchForm">
                <div data-role="fieldcontain">
                    <label for="searchString">Search BARD:</label>
                    <input type="text" id="searchString" name="searchString"
                           value="${flash.searchString ?: params?.searchString ?: ''}"/>
                </div>

                <div data-role="fieldcontain">
                    <g:link controller="bardWebInterface" action="jsDrawEditor" class="ui-link-inherit"
                            data-ajax='false'>
                        <img src="${resource(dir: 'images', file: 'structureEditIcon.png')}"
                             alt="Draw or paste a structure"
                             title="Draw or paste a structure"/> Draw or paste a structure
                    </g:link>
                </div>

                <button value="Search" name="search" id="searchButton" data-theme="b" type="submit"
                        class="ui-btn-hidden"
                        aria-disabled="false" data-inline="true" data-theme="b">Submit</button>
            </g:form>
        </div>

        <div>
            <g:link controller="bardWebInterface" action="turnoffMobileExperience" data-ajax="false">
                <g:message code="mobile.disable.experience" default="Switch to the regular website"/>
            </g:link>
        </div>
    </div><!-- /content -->

    <p>
        <a href="#home" data-role="button" data-inline="true" data-icon="back" data-transition="none">Back</a>
    </p>
</div><!-- /page -->


<g:render template="/mobile/bardWebInterface/searchResults"/>
