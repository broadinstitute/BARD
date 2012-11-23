<dl class="dl-horizontal">
    <g:if test="${projectAdapter?.project?.id}">
        <dt>ID:</dt>
        <dd>${projectAdapter.project.id}</dd>
    </g:if>
    <g:if test="${projectAdapter?.name}">
        <dt>Name:</dt>
        <dd>${projectAdapter.name}</dd>
    </g:if>
    <g:if test="${projectAdapter?.numberOfExperiments}">
        <dt>Number Of Experiments:</dt>
        <dd>${projectAdapter.numberOfExperiments}</dd>
    </g:if>
    <g:each var="otherProjectProperty" in="${projectAdapter?.project?.values}">
        <dt>${otherProjectProperty.id}:</dt>
        <dd>${otherProjectProperty.value}</dd>
    </g:each>
</dl>
