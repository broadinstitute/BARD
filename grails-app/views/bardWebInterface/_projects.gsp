<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
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
                    <g:link action="showProject" id="${projectAdapter.project.id}"
                            target="_blank">Project ID: ${projectAdapter.project.id} - ${projectAdapter.project.name}</g:link>
                    <a href="#" class="addProjectToCart btn btn-mini" data-cart-name="${JavaScriptUtility.cleanup(projectAdapter.project.name)}" data-cart-id="${projectAdapter.project.id}">
                        Save for later analysis
                    </a>
                    <g:if test="${projectAdapter.searchHighlight}">
                        <ul>
                            <li>${projectAdapter.searchHighlight}</li>
                        </ul>
                    </g:if>
                </li>
            </g:each>
        </ul>
        <g:hiddenField name="totalProjects" id="totalProjects" value="${nhits}"/>
        <div class="pagination">
            <g:paginate total="${nhits ? nhits : 0}" params='[searchString: "${searchString}"]'/>
        </div>
    </div>
</g:if>
<g:else>
    <div class="tab-message">No search results found</div>
</g:else>
</div>