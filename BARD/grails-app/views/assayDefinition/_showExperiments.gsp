<%@ page import="org.apache.commons.lang3.tuple.Pair" %>
<div id="showExperiments">
    <g:if test="${assayInstance.experiments}">
        <table class="table table-striped table-hover table-bordered">
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
            <g:each in="${assayInstance.experiments}" var="experiment">
                <tr>
                    <td>
                        <g:if test="${experiment.ncgcWarehouseId}">
                            <g:link controller="bardWebInterface" action="showExperiment"
                                    id="${experiment.ncgcWarehouseId}">${experiment.id}</g:link>
                        </g:if>
                        <g:else>
                            ${experiment.id}
                        %{--TODO: Mark as not in warehouse--}%
                        </g:else>
                    </td>
                    <td style="line-height: 150%"><p>${experiment.experimentName}</p></td>
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

                        <g:each in="${experiment.projectExperiments}" var="projExp">
                            <td>

                                <g:link controller="project" id="${projExp.project.id}"
                                        action="show">${projExp.project.id}</g:link>

                            </td>
                            <td style="line-height: 150%">
                                ${projExp.project.name}
                            </td>
                        </g:each>

                    </g:if>
                    <g:else>
                        <td></td><td></td>
                    </g:else>
                </tr>
            </g:each>
            </tbody>
        </table>
    </g:if>
    <br/>
    <g:if test="${assayInstance.allowsNewExperiments()}">
        <g:if test="${editable == 'canedit'}">
            <g:link controller="experiment" action="create" params="${[assayId: assayInstance.id]}"
                    class="btn"><i class="icon-plus"></i>Create Experiment</g:link>
        </g:if>
    </g:if>
</div>
