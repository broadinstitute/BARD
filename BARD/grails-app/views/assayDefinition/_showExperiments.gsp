<%@ page import="bard.db.enums.ProjectStatus; bard.db.enums.ExperimentStatus; org.apache.commons.lang3.tuple.Pair" %>
<div id="showExperiments">
    <g:if test="${assayInstance.experiments}">
        <g:render template="/layouts/templates/tableSorterTip"/>
        <table class="table table-striped table-hover table-bordered">
            <thead>
            <tr>
                <th rowspan="2" data-sort="int">EID</th>
                <th rowspan="2">Experiment Name</th>
                <th rowspan="2" data-sort="string-ins">External References</th>
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
                <g:if test="${experiment.experimentStatus != ExperimentStatus.RETIRED}">
                    <tr>
                        <td>
                            <g:link controller="experiment" action="show"
                                    id="${experiment.id}">${experiment.id}</g:link>
                        </td>
                        <td>
                            <g:link controller="experiment" action="show"
                                    id="${experiment.id}">${experiment.experimentName}</g:link></td>
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

                            <g:each in="${experiment.projectExperiments}" var="projectExperiment">
                                <g:if test="${projectExperiment.project.projectStatus != ProjectStatus.RETIRED}">
                                    <td>

                                        <g:link controller="project" id="${projectExperiment.project.id}"
                                                action="show">${projectExperiment.project.id}</g:link>

                                    </td>
                                    <td style="line-height: 150%">
                                        ${projectExperiment.project.name}
                                    </td>
                                </g:if>
                            </g:each>

                        </g:if>
                        <g:else>
                            <td></td><td></td>
                        </g:else>
                    </tr>
                </g:if>
            </g:each>
            </tbody>
        </table>
    </g:if>
    <br/>
    <g:if test="${assayInstance.allowsNewExperiments()}">
        <sec:ifLoggedIn>
            <g:link controller="experiment" action="create" params="${[assayId: assayInstance.id]}"
                    class="btn"><i class="icon-plus"></i>Create Experiment</g:link>
        </sec:ifLoggedIn>
    </g:if>
</div>
