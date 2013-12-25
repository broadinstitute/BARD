<br/>
<br/>
<hr/>

<div class="alert alert-success">

    <div class="control-group">
        <g:set var="experimentNum" value="${movedExperiments?.size()}"/>
        <label><h3>Successfully moved the following ${experimentNum} experiment${experimentNum && experimentNum > 1 ? "s" : ""} to ADID :  ${targetAssay?.id}</h3>
        </label>
    </div>

    <div class="control-group">
        <ol>
            <g:each var="movedExperiment" in="${movedExperiments}" status="counter">
                <li>
                    <g:link controller="experiment" action="show" id="${movedExperiment.id}" target="_blank">
                        ${movedExperiment.id} - ${movedExperiment.experimentName}</g:link>
                </li>
            </g:each>
        </ol>
    </div>

</div>


