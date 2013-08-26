<br/>
<br/>
<hr/>

<div class="alert alert-success">

    <div class="control-group">
        <label><h3>Successfully merged the following ${oldAssays.size()} assays</h3>
        </label>
    </div>

    <div class="control-group">
        <ol>
            <g:each var="assayToMerge" in="${oldAssays}" status="counter">
                 <li>
                    <g:link controller="assayDefinition" action="show" id="${assayToMerge.id}" target="_blank">
                        ${assayToMerge.id} - ${assayToMerge.assayName}</g:link>
                </li>
            </g:each>
        </ol>
    </div>

    <div class="control-group">
        <label><h3>Into Assay:</h3></label>
    </div>

    <div class="control-group">
        <label>
            <g:link controller="assayDefinition" action="show" id="${mergedAssay.id}"
                    target="_blank">
                ${mergedAssay.id} - ${mergedAssay.assayName}</g:link>
        </label>
    </div>
</div>


