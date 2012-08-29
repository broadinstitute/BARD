<dl>
    <g:if test="${projectInstance?.protocol}">
        <dt>Protocol</dt>
        <dd>${projectInstance.protocol}</dd>
    </g:if>
    <g:if test="${projectInstance?.description}">
        <dt>Description</dt>
        <dd>${projectInstance.description}</dd>
    </g:if>
    <g:if test="${projectInstance?.comments}">
        <dt>Comments</dt><br>
        <dd>${projectInstance.comments}</dd>
    </g:if>
</dl>