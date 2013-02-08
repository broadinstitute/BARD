%{--<r:require modules="autocomplete, structureSearch"/>--}%
<r:require modules="autocomplete"/>
<noscript>
    <a href="http://www.enable-javascript.com/" target="javascript">
        <img src="${resource(dir: 'images', file: 'enable_js.png')}"
             alt="Please enable JavaScript to access the full functionality of this site."/>
    </a>
</noscript>
<g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm">
    <div class="row-fluid" style="margin-top: 15px;">
        <div class="input-append">
            <g:if test="${flash?.searchString}">
                <g:textField id="searchString" name="searchString" value="${flash.searchString}"
                             class="span10"/>
            </g:if>
            <g:elseif test="${params?.searchString}">
                <g:textField id="searchString" name="searchString" value="${params?.searchString}"
                             class="span10"/>
            </g:elseif>
            <g:else>
                <g:textField id="searchString" name="searchString" value="" class="span10"/>
            </g:else>

            <g:submitButton name="search" value="Search" class="btn btn-primary span2" id="searchButton"/>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span10">
            %{--<r:script> $(document).on('click', '#structureSearchLink', showJSDrawEditor)</r:script>--}%
            <div class="pull-right"><g:link controller="bardWebInterface" action="jsDrawEditor">
                <img src="${resource(dir: 'images', file: 'structureEditIcon.png')}" alt="Draw or paste a structure"
                     title="Draw or paste a structure"/> Draw or paste a structure</g:link> or <a
                    data-toggle="modal" href="#idModalDiv">list of IDs for search</a></div>
        </div>
    </div>
</g:form>

<g:render template="/layouts/templates/IdSearchBox"/>

