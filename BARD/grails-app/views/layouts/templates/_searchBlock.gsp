
    <g:form name="searchForm" controller="bardWebInterface" action="search" id="searchForm" class="search-form">
        <div class="row-fluid" style="margin-top: 15px;">
            <div class="search-field input-append">
                <g:link controller="bardWebInterface" action="jsDrawEditor">
                %{--<img src="${resource(dir: 'images', file: 'structureEditIcon.png')}"--}%
                    <img
                    %{--src="../images/bardHomepage/struct_icon.png" --}%
                     src="${resource(dir: 'images/bardHomepage', file: 'struct_icon.png')}"

                         alt="Draw or paste a structure"
                         title="Draw or paste a structure" style="width:74px;float: left;"/>
                </g:link>
                <img     src="${resource(dir: 'images/bardHomepage', file: 'ids_icon.png')}"
                        %{--src="../images/bardHomepage/ids_icon.png"--}%
                     alt="List of IDs for search"
                     title="List of IDs for search"  data-toggle="modal" href="#idModalDiv"
                     style="width:44px;float: left;"/>

                <div class="text-field"  style="width:67%;margin-right: 0px; float:left;">
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

                <div class="btn-field" style="width:84px;float: right;">
                    <button type="submit" name="search" class="btn btn-primary"
                            style="background: #0093d0;"
                            id="searchButton">SEARCH</button>
                </div>
            </div>
        </div>

    </g:form>

