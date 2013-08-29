<dl class="dl-horizontal">
    <g:if test="${projectAdapter?.id}">
        <dt>ID:</dt>
        <dd>${projectAdapter.capProjectId}</dd>
    </g:if>
    <g:if test="${projectAdapter?.name}">
        <dt>Name:</dt>
        <dd>${projectAdapter.name}</dd>
    </g:if>
    <g:if test="${projectAdapter?.numberOfExperiments}">
        <dt>Number Of Experiments:</dt>
        <dd>${projectAdapter.numberOfExperiments}</dd>
    </g:if>
    %{-- TODO: Lets figure out what we need<g:each var="otherProjectProperty" in="${projectAdapter?.project?.values}">--}%
        %{--<dt>${otherProjectProperty.id}:</dt>--}%
        %{--<dd>${otherProjectProperty.value}</dd>--}%
    %{--</g:each>--}%
</dl>
