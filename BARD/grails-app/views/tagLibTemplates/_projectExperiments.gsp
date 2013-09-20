<%@ page import="bard.core.interfaces.ExperimentValues;" %>
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
                    <g:link controller="bardWebInterface" action="showExperiment" id="${experiment.bardExptId}"
                            params='${ searchString ? '[searchString: ${searchString}]' : ""}'>
                        ${experiment.capExptId}</g:link>
                </td>
                <td>
                    <g:link controller="bardWebInterface" action="showExperiment" id="${experiment.bardExptId}"
                            params='${ searchString ? '[searchString: ${searchString}]' : ""}'>
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
                <g:if test="${i==0}">
                    <td rowspan="${experiments.size()}">
                        <g:link controller="assayDefinition" action="show" id="${assay.capAssayId}"
                                params='${ searchString ? '[searchString: ${searchString}]' : ""}'>
                            ${assay.capAssayId}</g:link>
                    </td>
                    <td rowspan="${experiments.size()}">
                        <g:link controller="assayDefinition" action="show" id="${assay.capAssayId}"
                                params='${ searchString ? '[searchString: ${searchString}]' : ""}'>
                            ${assay.name}</g:link>
                        %{--<dl class="dl-horizontal muted">--}%
                            %{--<dt>Assay Format:</dt>--}%
                            %{--<dd>${assay.minimumAnnotation.assayFormat}</dd>--}%
                            %{--<dt>Assay Type:</dt>--}%
                            %{--<dd>${assay.minimumAnnotation.assayType}</dd>--}%
                            %{--<dt>Detection Method Type:</dt>--}%
                            %{--<dd>${assay.minimumAnnotation.detectionMethodType}</dd>--}%
                        %{--</dl>--}%
                    </td>
                </g:if>
            </tr>
        </g:each>
    </g:each>
    </tbody>
</table>
