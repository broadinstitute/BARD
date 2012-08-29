<dl class="dl-horizontal">
    <g:if test="${projectInstance?.id}">
        <dt>ID:</dt>
        <dd>${projectInstance.id}</dd>
    </g:if>
    <g:if test="${projectInstance?.name}">
        <dt>Name:</dt>
        <dd>${projectInstance.name}</dd>
    </g:if>
    <g:if test="${projectInstance?.category}">
        <dt>Category:</dt>
        <dd>${projectInstance.category}</dd>
    </g:if>
    <g:if test="${projectInstance?.role}">
        <dt>Role:</dt>
        <dd>${projectInstance.role}</dd>
    </g:if>
    <g:if test="${projectInstance?.type}">
        <dt>Type:</dt>
        <dd>${projectInstance.type}</dd>
    </g:if>
    <g:each var="otherProjectProperty" in="${projectInstance?.values}">
        <dt>${otherProjectProperty.id}:</dt>
        <dd>${otherProjectProperty.value}</dd>
    </g:each>
</dl>
