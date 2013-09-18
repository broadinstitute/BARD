<%@ page import="bardqueryapi.FacetFormType; bardqueryapi.JavaScriptUtility" %>
<g:hiddenField name="totalProjects" id="totalProjects" value="${nhits}"/>
<div class="row-fluid">
<g:if test="${facets}">
    <g:render template="facets" model="['facets': facets, 'formName': FacetFormType.ProjectFacetForm]"/>
    <div class="span9">
    <g:if test="${params.max || params.offset}">
        <div class="pagination">
            <g:paginate total="${nhits ? nhits : 0}" params='[searchString: "${searchString}"]'/>
        </div>
    </g:if>
</g:if>
<g:else>
    <div class="span12">
</g:else>
<g:if test="${nhits > 0}">
    <div align="right">
        <g:selectAllItemsInPage mainDivName="projects"/>
    </div>

    <ul class="unstyled results">
        <g:each var="projectAdapter" in="${projectAdapters}">
            <li>
                <h3>
                    <g:if test="${searchString}">
                        <g:link controller="project" action="show" id="${projectAdapter.capProjectId}"
                                params='[searchString: "${searchString}"]'>${projectAdapter.name} <small>(Project ID: ${projectAdapter.capProjectId})</small></g:link>
                    </g:if>
                    <g:else>
                        <g:link controller="project" action="show"
                                id="${projectAdapter.capProjectId}">${projectAdapter.name} <small>(Project ID: ${projectAdapter.capProjectId})</small></g:link>
                    </g:else>

                </h3>
                <g:if test="${projectAdapter.hasProbes()}">
                    <span class="badge badge-info">Probe</span>
                </g:if>
                <g:saveToCartButton id="${projectAdapter.id}"
                                    name="${JavaScriptUtility.cleanup(projectAdapter.name)}"
                                    type="${querycart.QueryItemType.Project}"/>
                <g:if test="${projectAdapter.highlight}">
                    <dl class="dl-horizontal">
                        <dt>Search Match:</dt>
                        <dd>${projectAdapter.highlight}</dd>
                    </dl>
                </g:if>
                <g:if test="${projectAdapter?.getNumberOfExperiments()}">
                    <dl>
                        <dt>Number Of Experiments:</dt>
                        <dd>
                            <span class="badge badge-info">
                                <a href="/BARD/project/show/${projectAdapter.id}#experiments-info"
                                   style="color: white; text-decoration: underline">
                                    ${projectAdapter.getNumberOfExperiments()}
                                </a>
                            </span>
                        </dd>
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