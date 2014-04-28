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
<g:hiddenField name="totalExperiments" id="totalExperiments" value="${nhits}"/>
<div class="row-fluid">
    <g:if test="${facets}">
        <g:render template="facets" model="['facets': facets, 'formName': FacetFormType.ExperimentFacetForm]"/>
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
        <ul class="unstyled results">
            <g:each var="experimentAdapter" in="${experimentAdapters}">
                <li>
                    <h3>
                        <g:if test="${searchString}">
                            <g:link controller="experiment" action="show" id="${experimentAdapter.capExptId}"
                                    params='[searchString: "${searchString}"]'>${experimentAdapter.name} <small>(EID: ${experimentAdapter.capExptId})</small></g:link>
                        </g:if>
                        <g:else>
                            <g:link controller="experiment" action="show"
                                    id="${experimentAdapter.capExptId}">${experimentAdapter.name} <small>(EID: ${experimentAdapter.capExptId})</small></g:link>
                        </g:else>
                    </h3>

                    <div class="row-fluid">
                        <div class="span6">
                            <g:if test="${experimentAdapter.hasProbe}">
                                <span class="badge badge-info">Probe</span>
                            </g:if>
                            <dl class="dl-horizontal">
                                <dt><g:message code="exp.search.match" default="Search Match"/>:</dt>
                                <dd>${experimentAdapter.highlight}</dd>
                                <dt><g:message code="exp.status" default="Status"/>:</dt>
                                <dd>

                                    ${experimentAdapter.status}
                                    <g:render template="/common/statusIcons" model="[status:experimentAdapter.status, entity: 'Experiment']"/>

                                </dd>
                                <dt><g:message code="exp.samples.tested" default="Substances Tested"/>:</dt>
                                <dd>${experimentAdapter.substancesTested}</dd>
                                <dt><g:message code="exp.compounds.tested" default="Compounds Tested"/>:</dt>
                                <dd>${experimentAdapter.compoundsTested}</dd>
                                <dt><g:message code="exp.compounds.active" default="Compounds Active"/>:</dt>
                                <dd>${experimentAdapter.activeCompounds}</dd>
                                <dt><g:message code="exp.assay.definition" default="Assay Definition"/>:</dt>
                                <dd>
                                    <g:link controller="assayDefinition" action="show" id="${experimentAdapter.capAssayId}">${experimentAdapter.capAssayId}</g:link>
                                </dd>
                                <dt><g:message code="exp.appearsin.projects" default="Appears in Projects"/>:</dt>
                                <dd>
                                    <g:if test="${experimentAdapter.getProjectIdList().size() == 1}">
                                        <g:set var="projectId" value="${experimentAdapter.getProjectIdList().get(0).toString()}" />
                                        <g:link controller="project" action="show" id="${projectId}">${projectId}</g:link>
                                    </g:if>
                                    <g:else>
                                        <ul id="idnavlist">
                                            <g:each in="${experimentAdapter.getProjectIdList()}">
                                                <li><g:link controller="project" action="show" id="${it.toString()}">${it.toString()}</g:link></li>
                                            </g:each>
                                        </ul>
                                    </g:else>
                                </dd>
                            </dl>
                        </div>
                        <div class="span6">
                            <g:if test="${experimentAdapter.ncgcWarehouseId && experimentAdapter.experimentFiles}">
                                <div id="histogramExpt${experimentAdapter.capExptId}"></div>
                                <script>getSmallHistogram(${experimentAdapter.capExptId}, "histogramExpt" + ${experimentAdapter.capExptId});</script>
                                <g:link controller="bardWebInterface" action="showExperiment"
                                        id="${experimentAdapter.capExptId}"><h4>View Results for Experiment</h4></g:link>
                            </g:if>
                        </div>
                    </div>
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
