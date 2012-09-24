
<r:require module="autocomplete"/>

<g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm">
    <div class="row-fluid" style="margin-top: 15px;">
        <div class="input-append">
            <g:textField id="searchString" name="searchString" value="${flash.searchString ?: params?.searchString}" class="span10"/>
            <g:submitButton name="search" value="Search" class="btn btn-primary span2" id="searchButton"/>
        </div>
    </div>
    <div class="row-fluid">
        <div class="span10">
            <div class="pull-right"><i class="icon-search"></i> <a data-toggle="modal" href="#modalDiv">Draw or paste a structure for a search</a></div>
        </div>
    </div>
</g:form>

<g:render template="/layouts/templates/structureSearchBox"/>
