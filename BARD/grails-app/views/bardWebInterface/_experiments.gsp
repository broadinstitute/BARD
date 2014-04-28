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

<%@ page import="bard.core.rest.spring.experiment.ExperimentAbstract; bard.core.interfaces.ExperimentValues;" %>

<div id="showExperiments">

    <table class="table table-striped table-bordered">
        <thead>
        <tr>
            <th rowspan="2">EID</th>
            <th rowspan="2">Experiment Name</th>
            <th rowspan="2">External References</th>
            <th colspan="2"># Compounds</th>
            <th rowspan="2">PID</th>
            <th rowspan="2">Project Name</th>
        </tr>
        <tr>
            <th>Tested</th>
            <th>Active</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${ExperimentAbstract.groupByProjectIds(experiments)}" var="entry">
            <g:set var="currProject" value="${projects.find { it.bardProjectId == entry.key}}"/>
            <g:set var="experimentList" value="${entry.value}"/>
            <g:each in="${experimentList}" var="experiment" status="i">
                <tr>
                    <td>
                        <g:if test="${searchString}">
                            <g:link controller="bardWebInterface" action="showExperiment"
                                    id="${experiment.capExptId}"
                                    params='[searchString: "${searchString}"]'>${experiment.capExptId}</g:link>
                        </g:if>
                        <g:else>
                            <g:link controller="bardWebInterface" action="showExperiment"
                                    id="${experiment.capExptId}">${experiment.capExptId}</g:link>
                        </g:else>
                    </td>
                    <td style="line-height: 150%">
                        <g:if test="${searchString}">
                            <g:link controller="bardWebInterface" action="showExperiment"
                                    id="${experiment.capExptId}"
                                    params='[searchString: "${searchString}"]'>${experiment.name}</g:link>
                        </g:if>
                        <g:else>
                            <g:link controller="bardWebInterface" action="showExperiment"
                                    id="${experiment.capExptId}">${experiment.name}</g:link>
                        </g:else>
                    </td>
                    <td>
                        <g:if test="${experiment.pubchemAid}">
                            <a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid=${experiment.pubchemAid}"
                               target="_blank">PubChem AID=${experiment.pubchemAid}</a>
                        </g:if>
                    </td>
                    <td>${experiment.compounds}</td>
                    <td>${experiment.activeCompounds}</td>
                    <g:if test="${i == 0}">
                        <td rowspan="${experimentList.size()}">
                            <g:if test="${searchString}">
                                <g:link controller="project" action="show" id="${currProject.capProjectId}"
                                        params='[searchString: "${searchString}"]'>${currProject.capProjectId}</g:link>
                            </g:if>
                            <g:else>
                                <g:link controller="project" action="show"
                                        id="${currProject.capProjectId}">${currProject.capProjectId}</g:link>
                            </g:else>
                        </td>
                        <td rowspan="${experimentList.size()}">
                            <g:if test="${searchString}">
                                <g:link controller="project" action="show" id="${currProject.capProjectId}"
                                        params='[searchString: "${searchString}"]'>${currProject.name}</g:link>
                            </g:if>
                            <g:else>
                                <g:link controller="project" action="show"
                                        id="${currProject.capProjectId}">${currProject.name}</g:link>
                            </g:else>
                        </td>
                    </g:if>
                </tr>
            </g:each>
        </g:each>
        </tbody>
    </table>
</div>
