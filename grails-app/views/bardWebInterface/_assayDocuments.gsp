<dl>
    <g:if test="${assayInstance?.protocol}">
        <dt>Protocol</dt>
        <dd>${assayInstance.protocol}</dd>
    </g:if>
    <g:if test="${assayInstance?.description}">
        <dt>Description</dt>
        <dd>${assayInstance.description}</dd>
    </g:if>
    <g:if test="${assayInstance?.comments}">
        <dt>Comments</dt><br>
        <dd>${assayInstance.comments}</dd>
    </g:if>
</dl>