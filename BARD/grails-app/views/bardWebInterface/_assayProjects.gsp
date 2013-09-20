<dl>
<g:if test="${projects}">
    <g:each in="${projects}" var="project">
        <dt>Name</dt>
        <dd>
        <g:if test="${searchString}">
            <g:link controller="project" action="show" id="${project.capProjectId}"
                    params='[searchString: "${searchString}"]'>${project.name}</g:link>
        </g:if>
        <g:else>
            <g:link controller="project" action="show" id="${project.capProjectId}">${project.name}</g:link>
        </g:else>
        </dd>
        <dt>Description</dt>
        <dd>${project.description}</dd>
        <dt>Created</dt>
        <dd>${project.created}</dd>
        <dt>Modified by</dt>
        <dd>${project.modified}</dd>
    </g:each>
</g:if>
</dl>