<dl>
    <g:if test="${assayAdapter?.assay?.description}">
        <dt>Description</dt>
        <dd><g:textBlock>${assayAdapter?.assay?.description}</g:textBlock></dd>
    </g:if>
    <g:if test="${assayAdapter?.assay?.protocol}">
        <dt>Protocol</dt>
        <dd><g:textBlock>${assayAdapter?.assay?.protocol}</g:textBlock></dd>
    </g:if>
    <g:if test="${assayAdapter?.assay?.comments}">
        <dt>Comments</dt><br>
        <dd><g:textBlock>${assayAdapter?.assay?.comments}</g:textBlock></dd>
    </g:if>
</dl>