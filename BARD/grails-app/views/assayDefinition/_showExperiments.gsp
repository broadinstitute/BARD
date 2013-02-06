<div id="showExperiments">
    <p><strong>Used by the following experiments</strong></p>

    <ul>
        <g:each in="${assayInstance.experiments}" var="experiment">
            <li>
                <p><g:link controller="experiment" id="${experiment.id}"
                           action="show">${experiment.experimentName}</g:link></p>

                <g:if test="${experiment.externalReferences.size() > 0}">
                    <p><strong>External References</strong></p>
                    <ul>
                        <g:each in="${experiment.externalReferences}" var="xRef">
                            <li>
                                <a href="${xRef.externalSystem.systemUrl}${xRef.extAssayRef}"
                                   target="_blank">${xRef.externalSystem.systemName} ${xRef.extAssayRef}</a>
                            </li>
                        </g:each>
                    </ul>
                </g:if>

                <g:if test="${experiment.projectExperiments.size() > 0}">
                    <p><strong>Referenced in the following projects:</strong></p>
                    <ul>
                        <g:each in="${experiment.projectExperiments}" var="projExp">
                            <li>
                                <g:link controller="project" id="${projExp.project.id}"
                                        action="show">${projExp.project.name}</g:link>
                            </li>
                        </g:each>
                    </ul>
                </g:if>
            </li>
        </g:each>
    </ul>

    <g:link controller="experiment" action="create" params="${[assayId: assayInstance.id]}"
            class="btn">Create a new experiment</g:link>
</div>
