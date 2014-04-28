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

<%@ page import="bard.db.enums.Status; bard.core.interfaces.ExperimentValues;" %>
<table class="table">
    <thead>
    <tr>
        <th rowspan="2">EID</th>
        <th rowspan="2">Experiment Name</th>
        <th rowspan="2">Role</th>
        <th colspan="2"># Compounds</th>
        <th rowspan="2">ADID</th>
        <th rowspan="2">Assay Name</th>
    </tr>
    <tr>
        <th>Tested</th>
        <th>Active</th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${sortedAssays}" var="assay">
        <g:set var="experiments" value="${assayExperimentListMap[assay]}"/>
        <g:each in="${experiments}" var="experiment" status="i">
            <tr>
                <td>
                    <% String experimentStatus =
                            bard.db.experiment.Experiment.findById(experiment.capExptId)?.experimentStatus?.id
                    %>
                    <g:link controller="experiment" action="show" id="${experiment.capExptId}"
                            params='${searchString ? '[searchString: ${searchString}]' : ""}'>

                        <g:if test="${experimentStatus}">
                            ${experiment.capExptId} ${experimentStatus}
                            <g:render template="/common/statusIcons"
                                      model='[status: experimentStatus, entity: "Experiment"]'/>
                        </g:if>
                        <g:else>
                            ${experiment.capExptId}
                        </g:else>
                    </g:link>
                </td>
                <td>
                    <g:link controller="experiment" action="show" id="${experiment.capExptId}"
                            params='${searchString ? '[searchString: ${searchString}]' : ""}'>
                        ${experiment.name}</g:link>
                </td>
                <td>
                    <%
                        String role = experimentTypes?.get(experiment.bardExptId)
                        if (role == null) {
                            role = "Not Specified"
                        }
                    %>
                    ${role}
                </td>
                <td>${experiment.compounds}</td>
                <td>${experiment.activeCompounds ?: 0}</td>
                <g:if test="${i == 0}">
                    <td rowspan="${experiments.size()}">
                        <g:link controller="assayDefinition" action="show" id="${assay.capAssayId}"
                                params='${searchString ? '[searchString: ${searchString}]' : ""}'>
                            ${assay.capAssayId} ${assay.assayStatus}
                            <g:render template="/common/statusIcons"
                                      model='[status: assay.assayStatus, entity: "Assay"]'/>

                        </g:link>
                    </td>
                    <td rowspan="${experiments.size()}">
                        <g:link controller="assayDefinition" action="show" id="${assay.capAssayId}"
                                params='${searchString ? '[searchString: ${searchString}]' : ""}'>
                            ${assay.name}</g:link>
                    </td>
                </g:if>
            </tr>
        </g:each>
    </g:each>
    </tbody>
</table>
