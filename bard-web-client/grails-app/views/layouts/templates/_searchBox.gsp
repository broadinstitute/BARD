<r:require module="autocomplete"/>
<noscript>
    <a href="http://www.enable-javascript.com/" target="javascript">
        <img src="${resource(dir: 'images', file: 'enable_js.png')}"
             alt="Please enable JavaScript to access the full functionality of this site."/>
    </a>
</noscript>
<g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm">
    <div class="row-fluid" style="margin-top: 15px;">
        <div class="input-append">
            <g:textField id="searchString" name="searchString" value="${flash.searchString ?: params?.searchString}"
                         class="span10"/>
            <g:submitButton name="search" value="Search" class="btn btn-primary span2" id="searchButton"/>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span10">
            <div class="pull-right"><a data-toggle="modal" href="#modalDiv">
                <img src="${resource(dir: 'images', file: 'structureEditIcon.png')}" alt="Draw or paste a structure"
                     title="Draw or paste a structure"/> Draw or paste a structure</a> or <a
                    data-toggle="modal" href="#idModalDiv">list of IDs for search</a></div>
        </div>
    </div>
</g:form>

<g:render template="/layouts/templates/structureSearchBox"/>
<g:render template="/layouts/templates/IdSearchBox"/>

