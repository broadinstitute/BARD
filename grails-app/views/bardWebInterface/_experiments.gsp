<%@ page import="bard.core.ExperimentValues" %>
<div>
    <g:each var="experiment" in="${experiments}" status="i">
        <div>
            <h4>
                ${experiment.name} <small>(Experiment ID: ${experiment.id})</small>
            </h4>
            <p><span class="label label-info">${experiment.getRole()}</span>
                ${experiment.getValue(ExperimentValues.ExperimentCompoundCountValue).value} compound(s) /
                ${experiment.getValue(ExperimentValues.ExperimentSubstanceCountValue).value} substance(s) tested
            </p>
            <p>
                <g:link controller="bardWebInterface" action="showExperiment" id="${experiment.id}">View Results</g:link>
            </p>
            <g:if test="${showAssaySummary}">
                <div><g:render template="assaySummary" model="[assayAdapter:experiment.getAssay()]"/></div>
            </g:if>
            <div><g:textBlock>${experiment.description}</g:textBlock></div>
            <p>

            </p>
        </div>
    </g:each>
</div>