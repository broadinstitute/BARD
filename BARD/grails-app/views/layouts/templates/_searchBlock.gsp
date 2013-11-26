<r:require modules="autocomplete"/>
<g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm" class="search-form"
        style="border:1px solid rgba(244, 244, 244, 0.2); background:rgba(228, 228, 228, 0.2);">
%{-- .search-field is styled as "display: table" so make sure the children are styled with "display: table-row" and grandchildren with "display: table-cell" --}%
    <div class="search-box">
        <div class="search-box-row">
            <div class="search-box-search-subbuttons" style="width: 130px">
                <div style="display: inline-block; width: 74px" %{--class="search-box-search-subbuttons" %{--style="width:74px"--}%>
                    <g:link controller="bardWebInterface" action="jsDrawEditor">
                        <img id="struct_icon" src="${resource(dir: 'images/bardHomepage', file: 'struct_icon.png')}"
                             onmouseover="this.src = '${resource(dir: 'images/bardHomepage', file: 'struct_icon_bright.png')}'"
                             onmouseout="this.src = '${resource(dir: 'images/bardHomepage', file: 'struct_icon.png')}'"
                             alt="Draw or paste a structure"
                             title="Draw or paste a structure"/>
                    </g:link>
                </div>

                <div style="display: inline-block; width: 51px" %{--class="search-box-search-subbuttons" %{--style="width:44px"--}%>
                    <img id="ids_icon" src="${resource(dir: 'images/bardHomepage', file: 'ids_icon.png')}"
                         onmouseover="this.src = '${resource(dir: 'images/bardHomepage', file: 'ids_icon_bright.png')}'"
                         onmouseout="this.src = '${resource(dir: 'images/bardHomepage', file: 'ids_icon.png')}'"
                         alt="List of IDs for search"
                         title="List of IDs for search" data-toggle="modal" href="#idModalDiv"/>
                </div>
            </div>

            <div class="search-box-text-field-cell" style="width: 100%">
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

            <div class="search-box-button-cell">
                <button type="submit" name="search" class="search-button"
                        id="searchButton">SEARCH</button>
            </div>
        </div>
    </div>

</g:form>

