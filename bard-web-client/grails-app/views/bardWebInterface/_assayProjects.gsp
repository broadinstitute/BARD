<dl>
    <g:if test="${projects}">
        <g:each in="${projects}" var="project">
            <dt>Name</dt>
            <dd><g:link controller="bardWebInterface" action="showProject" id="${project.id}" params='[searchString:"${searchString}"]'>${project.name}</g:link></dd>
            <dt>Description</dt>
            <dd>${project.description}</dd>
            <dt>Created</dt>
            <dd>${project.created}</dd>
            <dt>Modified by</dt>
            <dd>${project.modified}</dd>
        </g:each>
    </g:if>
</dl>