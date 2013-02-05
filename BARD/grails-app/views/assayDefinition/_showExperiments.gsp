<div id="showExperiments">
    <p><strong>Used by the following experiments</strong></p>

    <ul>
        <g:each in="${assayInstance.experiments}" var="experiment">
            <li>
                <p>Experiment: ${experiment.experimentName}</p>
                <p><strong>External References</strong></p>
                <ul>
                <g:each in="${experiment.externalReferences}" var="xRef">
                    <li>
                        <a href="${xRef.externalSystem.systemUrl}${xRef.extAssayRef}" target="_blank">${xRef.externalSystem.systemName} ${xRef.extAssayRef}</a>
                    </li>
                </g:each>
                </ul>

                <p><strong>Referenced in the following projects:</strong></p>
                <ul>
                    <li>
                        <g:each in="${experiment.projectExperiments}" var="projExp">
                            <dl>
                                <dt>Project:</dt><dd> <g:link controller="project" id="${projExp.project.id}" action="show">${projExp.project.name}</g:link></dd>
                                <dt>Project Experiment:</dt><dd> <g:link controller="experiment" id="${projExp.experiment.id}" action="show">${projExp.experiment.experimentName} (${projExp.stage?.label})                        </g:link>
                            </dd>
                            </dl>
                        </g:each>
                    </li>
                </ul>
            </li>
        </g:each>
    </ul>
</div>
