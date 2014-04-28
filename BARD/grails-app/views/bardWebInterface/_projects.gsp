%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

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
                <g:saveToCartButton id="${projectAdapter.capProjectId}"
                                    name="${JavaScriptUtility.cleanup(projectAdapter.name)}"
                                    type="${querycart.QueryItemType.Project}"/>
                <g:if test="${projectAdapter.highlight}">
                    <dl class="dl-horizontal">
                        <dt>Search Match:</dt>
                        <dd>${projectAdapter.highlight}</dd>
                    </dl>
                </g:if>
                <dt><g:message code="project.projectStatus.label" default="Status"/>:</dt>
                <dd>
                    <span class="status">${projectAdapter?.projectStatus}</span>
                    <g:render template="/common/statusIcons" model="[status:projectAdapter?.projectStatus, entity: 'Project']"/>


                </dd>
                <g:if test="${projectAdapter?.getNumberOfExperiments()}">
                    <dl>
                        <dt>Number Of Experiments:</dt>
                        <dd>
                            <span class="badge badge-info">
                                <g:link controller="project" action="show" id="${projectAdapter.capProjectId}" fragment="experiments-info" style="color: white; text-decoration: underline">
                                    ${projectAdapter.getNumberOfExperiments()}
                                </g:link>
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
