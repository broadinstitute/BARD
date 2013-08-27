<g:if test="${target.getTargetClassifications()}">
    <div class="page-header">
        <h3>Classifications</h3>
    </div>

    <g:each in="${target.getTargetClassifications()}" var="classification">
        <dl class="dl-horizontal dl-horizontal-wide">
            <g:if test="${classification.id}">
                <dt>ID:</dt> <dd>${classification.id}</dd>
            </g:if>
            <g:if test="${classification.name}">
                <dt>Name:</dt> <dd>${classification.name}</dd>
            </g:if>
            <g:if test="${classification.description}">
                <dt>Description:</dt><dd><g:textBlock>${classification.description}</g:textBlock>&nbsp;</dd>
            </g:if>
            <g:if test="${classification.levelIdentifier}">
                <dt>Level Identifier:</dt><dd>${classification.levelIdentifier} &nbsp;</dd>
            </g:if>
            <g:if test="${classification.source}">
                <dt>Source:</dt><dd>${classification.source} &nbsp;</dd>
            </g:if>
        </dl>
    </g:each>
</g:if>

