<div>
    <g:each var="experiment" in="${experiments}" status="i">
        <div>
            <h3>
                <span class="badge badge-inverse">${(('A' as char)+i) as char}</span> ${experiment.name} <small>(Experiment ID: ${experiment.id})</small>
            </h3>
            <div><g:textBlock>${experiment.description}</g:textBlock></div>
            <p>TODO: Display Results Summary</p>
            <g:link controller="bardWebInterface" action="showResult" id="${experiment.id}">Results</g:link>
        </div>
    </g:each>
</div>