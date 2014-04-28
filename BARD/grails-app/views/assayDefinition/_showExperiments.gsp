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

<%@ page import="bard.db.enums.Status; org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils; bard.db.enums.Status; org.apache.commons.lang3.tuple.Pair" %>
<div id="showExperiments" xmlns="http://www.w3.org/1999/html">
    <g:if test="${assayInstance.experiments}">
        <g:render template="/layouts/templates/tableSorterTip"/>
        <div class="span3">

        </div>
        <div class="span9">
        <g:if test="${assayInstance.allowsNewExperiments()}">
            <sec:ifLoggedIn>
                <g:link controller="experiment" action="create" params="${[assayId: assayInstance.id]}"
                        class="btn"><i class="icon-plus"></i>Create Experiment</g:link>
            </sec:ifLoggedIn>
        </g:if>
        </div>
        <div class="span3">

        </div>

        <div class="span9">
            <table class="table table-striped table-hover table-bordered">
                <thead>
                <tr>
                    <th rowspan="2" data-sort="int">EID</th>
                    <th rowspan="2">Experiment Name</th>
                     <th rowspan="2" data-sort="string-ins">External References</th>
                    <th colspan="2"># Compounds</th>
                    <g:each var="i" in="${(0..< maxNumProjectsInExperiments)}">
                        <th rowspan="2">PID</th>
                        <th rowspan="2">Project Name</th>
                    </g:each>
                </tr>
                <tr>
                    <th>Tested</th>
                    <th>Active</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${assayInstance.experiments}" var="experiment">
                    <g:if test="${experiment.experimentStatus != Status.RETIRED}">
                        <g:if test="${!experiment?.permittedToSeeEntity()}">
                        %{--gray out the row if you are--}%
                            <tr class="grayout">
                                <td>
                                    ${experiment.id}
                                </td>
                                <td colspan="6"> Work In Progress</td>
                            </tr>
                        </g:if>
                        <g:else>
                            <tr>

                                <td>
                                    <g:link controller="experiment" action="show"
                                            id="${experiment.id}">${experiment.id} ${experiment.experimentStatus.id}
                                        <g:render template="/common/statusIcons" model="[status:experiment.experimentStatus.id, entity: 'Experiment']"/>

                                    </g:link>
                                </td>
                                <td>
                                    <g:link controller="experiment" action="show"
                                            id="${experiment.id}">${experiment.experimentName}
                                    </g:link></td>
                                <td>
                                    <g:if test="${!experiment.externalReferences.isEmpty()}">

                                        <g:each in="${experiment.externalReferences}" var="xRef">
                                            <a href="${xRef.externalSystem.systemUrl}${xRef.extAssayRef}"
                                               target="_blank">${xRef.externalSystem.systemName} ${xRef.extAssayRef}</a>
                                        </g:each>
                                    </g:if>
                                </td>
                                <td>
                                    <%
                                        Pair<Long, Long> activeVsTested = experimentsActiveVsTested[experiment.id]
                                        String active = activeVsTested?.left?.toString() ?: ""
                                        String tested = activeVsTested?.right?.toString() ?: ""
                                    %>
                                    ${tested}
                                </td>
                                <td>
                                    ${active}
                                </td>
                                <g:if test="${!experiment.projectExperiments.isEmpty()}">
                                    <g:set var="notRetiredCounter" value="${0}" />
                                    <g:each in="${experiment.projectExperiments}" var="projectExperiment">
                                        <g:if test="${projectExperiment.project.projectStatus != Status.RETIRED}">
                                            <g:set var="notRetiredCounter" value="${notRetiredCounter + 1}" />
                                            <td>
                                                <g:link controller="project" id="${projectExperiment.project.id}"
                                                        action="show">${projectExperiment.project.id} ${projectExperiment.project.projectStatus.id}
                                                    <g:render template="/common/statusIcons"
                                                              model="[status:projectExperiment.project.projectStatus.id, entity: 'Project']"/>
                                                </g:link>

                                            </td>
                                            <td style="line-height: 150%">
                                                ${projectExperiment.project.name}
                                            </td>
                                        </g:if>
                                    </g:each>
                                    <g:each var="i" in="${(0..< maxNumProjectsInExperiments - notRetiredCounter)}">
                                        <td></td><td></td>
                                    </g:each>
                                </g:if>
                                <g:else>
                                    <g:each var="i" in="${(0..< maxNumProjectsInExperiments)}">
                                        <td></td><td></td>
                                    </g:each>
                                </g:else>
                            </tr>
                        </g:else>
                    </g:if>
                </g:each>
                </tbody>
            </table>
        </div>
    </g:if>
    <br/>

</div>
