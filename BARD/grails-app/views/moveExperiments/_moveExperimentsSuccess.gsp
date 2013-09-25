<br/>
<br/>
<hr/>

<div class="alert alert-success">

    <div class="control-group">
        <label><h3>Successfully moved the following ${movedExperiments?.size()} experiment(s) to Assay :  ${targetAssay?.id}</h3></label>
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


