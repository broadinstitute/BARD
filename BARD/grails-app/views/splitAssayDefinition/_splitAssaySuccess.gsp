<br/>
<br/>
<hr/>

<div class="alert alert-success">

    <div class="control-group">
        <label><h3>Successfully moved the following experiments from ${oldAssay.id} - ${oldAssay.assayName}</h3> to

        </label>
        <label>
            <h3><g:link controller="assayDefinition" action="show" target="assay"
                        id="${newAssay?.id}">${newAssay.id} - ${newAssay.assayName}</g:link></h3>
        </label>
    </div>

    <div class="control-group">
        <ol>Moved Experiments
            <g:each var="experiment" in="${newAssay.experiments}">
                <li>
                    <g:link controller="experiment" action="show" id="${experiment.id}" target="experiment">
                        ${experiment.id} - ${experiment.experimentName}</g:link>
                </li>
            </g:each>
        </ol>
    </div>

</div>


