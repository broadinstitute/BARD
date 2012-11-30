<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
<g:hiddenField name="totalAssays" id="totalAssays" value="${nhits}"/>
<div class="row-fluid">
<g:if test="${facets}">
    <g:render template="facets" model="['facets': facets, 'formName' : FacetFormType.AssayFacetForm]"/>
    <div class="span9">
</g:if>
<g:else>
    <div class="span12">
</g:else>
<g:if test="${nhits > 0}">
        <ul class="unstyled results">
        <g:each var="assayAdapter" in="${assayAdapters}">
            <li>
                <h3><g:link action="showAssay" id="${assayAdapter.assay.id}" params='[searchString:"${searchString}"]'>${assayAdapter.name} <small>(ADID: ${assayAdapter.assay.id})</small></g:link></h3>
                <g:saveToCartButton id="${assayAdapter.assay.id}"
                                    name="${JavaScriptUtility.cleanup(assayAdapter.name)}"
                                    type="${querycart.QueryItemType.AssayDefinition}"/>
                <g:if test="${assayAdapter.searchHighlight}">
                    <dl>
                        <dt>Search Match (highlighted in bold):</dt>
                        <dd>&hellip;${assayAdapter.searchHighlight}&hellip;</dd>
                    </dl>
                </g:if>
            </li>
        </g:each>
        </ul>
        <div class="pagination">
            <g:paginate total="${nhits?nhits:0}" params='[searchString:"${searchString}"]'/>
        </div>
</g:if>
<g:else>
        <div class="tab-message">No search results found</div>
</g:else>
    </div>
</div>