<div>
    <g:each var="experiment" in="${experiments}" status="i">
        <div>
            <h4>
                ${experiment.name} <small>(Experiment ID: ${experiment.id})</small>
            </h4>
            <div><g:textBlock>${experiment.description}</g:textBlock></div>
            <p>
                <g:link controller="bardWebInterface" action="showExperiment" id="${experiment.id}">View Results</g:link>
            </p>
        </div>
    </g:each>
</div>