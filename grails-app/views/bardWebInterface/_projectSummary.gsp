<dl class="dl-horizontal">
    <g:if test="${projectAdapter?.project?.id}">
        <dt>ID:</dt>
        <dd>${projectAdapter.project.id}</dd>
    </g:if>
    <g:if test="${projectAdapter?.name}">
        <dt>Name:</dt>
        <dd>${projectAdapter.name}</dd>
    </g:if>
    %{--<g:if test="${projectAdapter?.project?.category}">--}%
        %{--<dt>Category:</dt>--}%
        %{--<dd>${projectAdapter.project.category}</dd>--}%
    %{--</g:if>--}%
    %{--<g:if test="${projectAdapter?.project?.role}">--}%
        %{--<dt>Role:</dt>--}%
        %{--<dd>${projectAdapter.project.role}</dd>--}%
    %{--</g:if>--}%
    %{--<g:if test="${projectAdapter?.project?.type}">--}%
        %{--<dt>Type:</dt>--}%
        %{--<dd>${projectAdapter.project.type}</dd>--}%
    %{--</g:if>--}%
    <g:each var="otherProjectProperty" in="${projectAdapter?.project?.values}">
        <dt>${otherProjectProperty.id}:</dt>
        <dd>${otherProjectProperty.value}</dd>
    </g:each>
</dl>
