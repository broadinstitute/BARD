<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils; bard.db.enums.ProjectStatus; bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; bard.db.registration.DocumentKind; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="basic"/>
    <title>PID ${instance?.id}: ${instance?.name}</title>
    <r:require
            modules="core,bootstrap,select2,accessontology,twitterBootstrapAffix,xeditable,assayshow,richtexteditorForEdit,projectsummary,canEditWidget,projectstep,compoundOptions"/>
</head>

<body>
<g:if test="${instance?.id}">
    <g:if test="${!instance?.permittedToSeeEntity()}">
        <g:render template="/layouts/templates/handleDraftEntities" model="[entity: 'Project']"/>
    </g:if>
    <g:else>
        <g:render template="showProject"
                  model="[instance: instance, editable: editable, uneditable: uneditable, projectAdapter:projectAdapter,experiments:experiments,assays:assays]"/>
    </g:else>
</g:if>

</body>
</html>