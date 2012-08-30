<dl class="dl-horizontal">
    <g:if test="${assayAdapter?.assay.id}">
        <dt>ID:</dt>
        <dd>${assayAdapter.assay.id}</dd>
    </g:if>
    <g:if test="${assayAdapter?.assay.name}">
        <dt>Name:</dt>
        <dd>${assayAdapter.assay.name}</dd>
    </g:if>
    <g:if test="${assayAdapter?.assay.category}">
        <dt>Category:</dt>
        <dd>${assayAdapter.assay.category}</dd>
    </g:if>
    <g:if test="${assayAdapter?.assay.role}">
        <dt>Role:</dt>
        <dd>${assayAdapter?.assay.role}</dd>
    </g:if>
    <g:if test="${assayAdapter?.assay.type}">
        <dt>Type:</dt>
        <dd>${assayAdapter?.assay.type}</dd>
    </g:if>
    <g:each var="otherAssayProperty" in="${assayAdapter?.assay.values}">
        <dt>${otherAssayProperty.id}:</dt>
        <dd>${otherAssayProperty.value}</dd>
    </g:each>
</dl>