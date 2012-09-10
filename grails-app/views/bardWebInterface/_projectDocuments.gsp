<dl>
    %{--<g:if test="${projectAdapter?.project?.protocol}">--}%
        %{--<dt>Protocol</dt>--}%
        %{--<dd>${projectAdapter.project.protocol}</dd>--}%
    %{--</g:if>--}%
    <g:if test="${projectAdapter?.project?.description}">
        <dt>Description</dt>
        <dd>${projectAdapter.project.description}</dd>
    </g:if>
    %{--<g:if test="${projectAdapter?.project?.comments}">--}%
        %{--<dt>Comments</dt><br>--}%
        %{--<dd>${projectAdapter.project.comments}</dd>--}%
    %{--</g:if>--}%
</dl>