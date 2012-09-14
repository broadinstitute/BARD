<dl>
    <g:if test="${assayAdapter?.assay.projects}">
        <g:each in="${assayAdapter?.assay.projects}" var="project">
            <dt>Name</dt>
            <dd>${project.name}</dd>
            <dt>Description</dt>
            <dd>${project.description}</dd>
            <dt>Created</dt>
            <dd>${project.created}</dd>
            <dt>Modified by</dt>
            <dd>${project.modified}</dd>
        </g:each>
    </g:if>
</dl>