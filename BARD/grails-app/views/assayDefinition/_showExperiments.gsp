<g:if test="${assayInstance.experiments}">
    <div id="showExperiments">

        <table class="table table-striped table-hover table-bordered">
            <thead>
            <tr>
                <th>EID</th><th>Experiment Name</th><th>External References</th> <th>Substances Tested</th> <th>PID</th><th>Project Name</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${assayInstance.experiments}" var="experiment">
                <tr>
                    <td><g:link controller="experiment" id="${experiment.id}"
                                action="show">${experiment.id}</g:link></td>
                    <td style="line-height: 150%"><p>${experiment.experimentName}</p></td>
                    <td>
                        <g:if test="${!experiment.externalReferences.isEmpty()}">

                            <g:each in="${experiment.externalReferences}" var="xRef">
                                <a href="${xRef.externalSystem.systemUrl}${xRef.extAssayRef}"
                                   target="_blank">${xRef.externalSystem.systemName} ${xRef.extAssayRef}</a>
                            </g:each>
                        </g:if>
                    </td>
                    <td></td>
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
        <br/>
        <g:if test="${assayInstance.allowsNewExperiments()}">
            <g:link controller="experiment" action="create" params="${[assayId: assayInstance.id]}"
                    class="btn"><i class="icon-plus"></i>Add New Experiment</g:link>
        </g:if>
    </div>
</g:if>