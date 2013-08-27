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
                                    id="${experiment.bardExptId}"
                                    params='[searchString: "${searchString}"]'>${experiment.capExptId}</g:link>
                        </g:if>
                        <g:else>
                            <g:link controller="bardWebInterface" action="showExperiment"
                                    id="${experiment.bardExptId}">${experiment.capExptId}</g:link>
                        </g:else>
                    </td>
                    <td style="line-height: 150%">
                        <g:if test="${searchString}">
                            <g:link controller="bardWebInterface" action="showExperiment"
                                    id="${experiment.bardExptId}"
                                    params='[searchString: "${searchString}"]'>${experiment.name}</g:link>
                        </g:if>
                        <g:else>
                            <g:link controller="bardWebInterface" action="showExperiment"
                                    id="${experiment.bardExptId}">${experiment.name}</g:link>
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
                                <g:link controller="bardWebInterface" action="showProject" id="${currProject.id}"
                                        params='[searchString: "${searchString}"]'>${currProject.id}</g:link>
                            </g:if>
                            <g:else>
                                <g:link controller="bardWebInterface" action="showProject"
                                        id="${currProject.id}">${currProject.id}</g:link>
                            </g:else>
                        </td>
                        <td rowspan="${experimentList.size()}">
                            <g:if test="${searchString}">
                                <g:link controller="bardWebInterface" action="showProject" id="${currProject.id}"
                                        params='[searchString: "${searchString}"]'>${currProject.name}</g:link>
                            </g:if>
                            <g:else>
                                <g:link controller="bardWebInterface" action="showProject"
                                        id="${currProject.id}">${currProject.name}</g:link>
                            </g:else>
                        </td>
                    </g:if>
                </tr>
            </g:each>
        </g:each>
        </tbody>
    </table>
</div>
