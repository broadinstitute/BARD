<dl>
    <g:if test="${assayAdapter?.assay.protocol}">
        <dt>Protocol</dt>
        <dd>${assayAdapter?.assay.protocol}</dd>
    </g:if>
    <g:if test="${assayAdapter?.assay.description}">
        <dt>Description</dt>
        <dd>${assayAdapter?.assay.description}</dd>
    </g:if>
    <g:if test="${assayAdapter?.assay.comments}">
        <dt>Comments</dt><br>
        <dd>${assayAdapter?.assay.comments}</dd>
    </g:if>
</dl>