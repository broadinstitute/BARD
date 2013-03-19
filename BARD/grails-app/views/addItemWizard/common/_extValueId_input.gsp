<g:if test="${supportsExternalOntologyLookup}">
    <p>An integrated search facility has been built to lookup ${attributeLabel} values, please enter an identifier or some text and select a value.
    Alternatively, you can search directly on the <a
            href="${attributeExternalUrl}">${attributeExternalUrl}</a> site and manually enter an external value id and description.
    </p>

    <div class="row-fluid">
        <div class="span12">
            <input type="hidden" id="extValueIdSearch" name="extValueIdSearch"/>
        </div>
    </div>

</g:if>
<g:else>
    <p>There is currently no integrated search for the ${attributeLabel} external ontology.</p>

    <p>Please search directly on the <a
            href="${attributeExternalUrl}">${attributeExternalUrl}</a> site and manually enter an external value id and a text description below.
    </p>
</g:else>
<br/>
<br/>
<br/>

<div class="row-fluid">
    <div class="span3">
        <label class="control-label">External Value Id:</label>
    </div>

    <div class="span8">
        <div class="controls">
            <g:textField id="extValueId" name="extValueId" value="${fixedValue?.extValueId}"/>
        </div>
    </div>
</div>

<div class="row-fluid">
    <div class="span3">
        <label class="control-label">Description:</label>
    </div>

    <div class="span8">
        <div class="controls">
            <g:textField id="valueLabel" name="valueLabel" value="${fixedValue?.valueLabel}"/>
        </div>
    </div>
</div>