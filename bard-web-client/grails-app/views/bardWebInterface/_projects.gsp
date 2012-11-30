<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
<g:hiddenField name="totalProjects" id="totalProjects" value="${nhits}"/>
<div class="row-fluid">
<g:if test="${facets}">
    <g:render template="facets" model="['facets': facets, 'formName': FacetFormType.ProjectFacetForm]"/>
    <div class="span9">
</g:if>
<g:else>
    <div class="span12">
</g:else>
<g:if test="${nhits > 0}">
    <ul class="unstyled results">
        <g:each var="projectAdapter" in="${projectAdapters}">
            <li>
                <h3><g:link action="showProject" id="${projectAdapter.project.id}"
                            params='[searchString: "${searchString}"]'>${projectAdapter.project.name} <small>(Project ID: ${projectAdapter.project.id})</small></g:link>
                </h3>
                <g:saveToCartButton id="${projectAdapter.project.id}"
                                    name="${JavaScriptUtility.cleanup(projectAdapter.project.name)}"
                                    type="${querycart.QueryItemType.Project}"/>
                <g:if test="${projectAdapter.searchHighlight}">
                    <dl>
                        <dt>Search Match (highlighted in bold):</dt>
                        <dd>&hellip;${projectAdapter.searchHighlight}&hellip;</dd>
                    </dl>
                </g:if>
                <g:if test="${projectAdapter?.getNumberOfExperiments()}">
                    <dl>
                        <dt>Number Of Experiments:</dt>
                        <dd><span class="badge badge-info">${projectAdapter.getNumberOfExperiments()}</span></dd>
                    </dl>
                </g:if>
            </li>
        </g:each>
    </ul>

    <div class="pagination">
        <g:paginate total="${nhits ? nhits : 0}" params='[searchString: "${searchString}"]'/>
    </div>
</g:if>
<g:else>
    <div class="tab-message">No search results found</div>
</g:else>
</div>
</div>