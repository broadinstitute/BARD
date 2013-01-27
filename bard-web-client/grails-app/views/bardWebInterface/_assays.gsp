<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
<g:hiddenField name="totalAssays" id="totalAssays" value="${nhits}"/>
<div class="row-fluid">
<g:if test="${facets}">
    <g:render template="facets" model="['facets': facets, 'formName': FacetFormType.AssayFacetForm]"/>
    <div class="span9">
</g:if>
<g:else>
    <div class="span12">
</g:else>
<g:if test="${nhits > 0}">
    <ul class="unstyled results">
        <g:each var="assayAdapter" in="${assayAdapters}">
            <li>
                <h3>
                    <g:if test="${searchString}">
                        <g:link action="showAssay" id="${assayAdapter.id}"
                                params='[searchString: "${searchString}"]'>${assayAdapter.name} <small>(ADID: ${assayAdapter.id})</small></g:link>
                    </g:if>
                    <g:else>
                        <g:link action="showAssay"
                                id="${assayAdapter.id}">${assayAdapter.name} <small>(ADID: ${assayAdapter.id})</small></g:link>
                    </g:else>

                </h3>
                <g:saveToCartButton id="${assayAdapter.id}"
                                    name="${JavaScriptUtility.cleanup(assayAdapter.name)}"
                                    type="${querycart.QueryItemType.AssayDefinition}"/>
                <g:if test="${assayAdapter.highlight}">
                    <dl class="dl-horizontal">
                        <dt>Search Match:</dt>
                        <dd>${assayAdapter.highlight}</dd>
                    </dl>
                </g:if>
            </li>
        </g:each>
    </ul>
    <g:if test="${params.max || params.offset}">
        <div class="pagination">
            <g:paginate total="${nhits ? nhits : 0}" params='[searchString: "${searchString}"]'/>
        </div>
    </g:if>
</g:if>
<g:else>
    <div class="tab-message">No search results found</div>
</g:else>
</div>
</div>