<%@ page import="bardqueryapi.JavaScriptUtility" %>
<g:hiddenField name="totalAssays" id="totalAssays" value="${nhits}"/>

<div data-role="header">
    <h1>Assays</h1>
</div><!-- /header -->

<div data-role="content">
    <g:if test="${nhits > 0}">
        <ul>
            <g:each var="assayAdapter" in="${assayAdapters}">
                <li>
                %{--<h3>--}%
                    <g:link action="showAssay" id="${assayAdapter.id}"
                            params='[searchString: "${searchString}"]'>${assayAdapter.name} <small>(ADID: ${assayAdapter.id})</small></g:link>
                %{--</h3>--}%
                </li>
            </g:each>
        </ul>

        <div id="paginateBar" class="pagination" data-ajax="false">
            <g:paginate total="${nhits ? nhits : 0}" params='[searchString: "${searchString}"]'/>
        </div>
    </g:if>
    <g:else>
        <div class="tab-message">No search results found</div>
    </g:else>
</div>