<%@ page import="bard.db.registration.DocumentKind" %>
<g:if test="${documentKind==DocumentKind.AssayDocument}">
    <g:link action="create" controller="document"
            params="${[assayId: "${owningEntityId}", documentType: "${documentType}"]}"
            class="btn"><i class="icon-plus"></i> ${label}</g:link>
</g:if>
<g:else>
    <g:link action="create" controller="document"
            params="${[projectId: "${owningEntityId}", documentType: "${documentType}"]}"
            class="btn"><i class="icon-plus"></i> ${label}</g:link>
</g:else>
<br/><br/>
