%{-- Extract this to a taglib to simplify the logic --}%
<%@ page import="org.apache.commons.lang3.BooleanUtils" %>
<dl>
    <dt><g:message code="assay.label" default="Assay Definition"/>:</dt>
    <dd>
        <ul>
            <li><g:link controller="assayDefinition" action="show"
                        id="${experiment?.assay?.id}">${experiment?.assay?.name}</g:link></li>
        </ul>
    </dd>
    <g:if test="">

    </g:if>

    <dt>Experiment Links:</dt>
    <dd>
        <ul>
            <g:if test="${excludedLinks?.contains('experiment.show') == false}" >
                <li>
                    <g:link controller="experiment" action="show" id="${experiment?.id}">View Experiment Details</g:link>
                </li>
            </g:if>
            <g:elseif test="${BooleanUtils.isFalse(excludedLinks?.contains('bardWebInterface.showExperiment')) && experiment?.ncgcWarehouseId}">
                <li>
                    <g:link controller="bardWebInterface" action="showExperiment" id="${experiment?.ncgcWarehouseId}">View Published Results</g:link>
                </li>
            </g:elseif>
            <g:else>
                Results have not yet been published for this experiment.
            </g:else>
            %{--TODO add link to preview results when we have that page--}%
        </ul>
    </dd>
    <dt>Projects:</dt>
    <dd>
        <ul>
            <g:each in="${experiment?.projectExperiments}" var="projectExperiment">
                <li><g:link controller="project" action="show"
                            id="${projectExperiment.project.id}">${projectExperiment.project.name}</g:link></li>
            </g:each>
        </ul>
    </dd>
    <dt>External references</dt>
    <dd>
        <ul>
            <g:each in="${experiment?.externalReferences}" var="xRef">
                <li>
                    <a href="${xRef.externalSystem.systemUrl}${xRef.extAssayRef}" target="_blank">${xRef.externalSystem.systemName} ${xRef.extAssayRef}</a>
                </li>
            </g:each>
        </ul>
    </dd>
</dl>