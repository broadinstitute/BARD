%{--<r:require modules="autocomplete, structureSearch"/>--}%
<script src="../js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.accentFolding.js"></script>
<script src="../js/jquery-ui-extensions/autocomplete/jquery.ui.autocomplete.html.js"></script>
<r:require modules="autocomplete"/>
<noscript>
    <a href="http://www.enable-javascript.com/" target="javascript">
        <img src="${resource(dir: 'images', file: 'enable_js.png')}"
             alt="Please enable JavaScript to access the full functionality of this site."/>
    </a>
</noscript>


<div class="search-panel">
    <div class="container-fluid">
        <div class="search-block">
        %{--<g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm" class="form-inline">--}%
            <g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm" class="search-form">
                <div class="row-fluid" style="margin-top: 15px;">
                    <div class="search-field input-append">
                        <div class="text-field">
                            <g:if test="${flash?.searchString}">
                                <g:textField id="searchString" name="searchString" value="${flash.searchString}"/>
                            </g:if>
                            <g:elseif test="${params?.searchString}">
                                <g:textField id="searchString" name="searchString" value="${params?.searchString}"/>
                            </g:elseif>
                            <g:else>
                                <g:textField id="searchString" name="searchString" value=""/>
                            </g:else>
                        </div>

                        <div class="btn-field">
                            <button type="submit" name="search" class="btn btn-primary"
                                    id="searchButton">Search</button>
                        </div>
                    </div>
                </div>

                <div class="row-fluid">
                    <div class="span10">
                        %{--<r:script> $(document).on('click', '#structureSearchLink', showJSDrawEditor)</r:script>--}%
                        <div class="pull-right"><g:link controller="bardWebInterface" action="jsDrawEditor">
                            <img src="${resource(dir: 'images', file: 'structureEditIcon.png')}"
                                 alt="Draw or paste a structure"
                                 title="Draw or paste a structure"/> Draw or paste a structure</g:link> or <a
                                data-toggle="modal" href="#idModalDiv">list of IDs for search</a></div>
                    </div>
                </div>
            </g:form>
        </div>
    </div>
</div>
<g:render template="/layouts/templates/IdSearchBox"/>