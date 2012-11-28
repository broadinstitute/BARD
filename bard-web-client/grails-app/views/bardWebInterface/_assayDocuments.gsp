<dl>
    <g:if test="${assayAdapter?.description}">
        <dt>Description</dt>
        <dd><g:textBlock>${assayAdapter?.description}</g:textBlock></dd>
    </g:if>
    <g:if test="${assayAdapter?.protocol}">
        <dt>Protocol</dt>
        <dd><g:textBlock>${assayAdapter?.protocol}</g:textBlock></dd>
    </g:if>
    <g:if test="${assayAdapter?.comments}">
        <dt>Comments</dt><br>
        <dd><g:textBlock>${assayAdapter?.comments}</g:textBlock></dd>
    </g:if>
</dl>