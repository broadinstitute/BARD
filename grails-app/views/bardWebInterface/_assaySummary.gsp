<dl class="dl-horizontal">
    <g:if test="${assayInstance?.id}">
        <dt>ID:</dt>
        <dd>${assayInstance.id}</dd>
    </g:if>
    <g:if test="${assayInstance?.name}">
        <dt>Name:</dt>
        <dd>${assayInstance.name}</dd>
    </g:if>
    <g:if test="${assayInstance?.category}">
        <dt>Category:</dt>
        <dd>${assayInstance.category}</dd>
    </g:if>
    <g:if test="${assayInstance?.role}">
        <dt>Role:</dt>
        <dd>${assayInstance.role}</dd>
    </g:if>
    <g:if test="${assayInstance?.type}">
        <dt>Type:</dt>
        <dd>${assayInstance.type}</dd>
    </g:if>
    <g:each var="otherAssayProperty" in="${assayInstance?.values}">
        <dt>${otherAssayProperty.id}:</dt>
        <dd>${otherAssayProperty.value}</dd>
    </g:each>
</dl>