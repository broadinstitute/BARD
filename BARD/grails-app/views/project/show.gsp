<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils; bard.db.enums.Status; bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; bard.db.registration.DocumentKind; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="basic"/>
    <title>PID ${instance?.id}: ${instance?.name}</title>
    <r:require
            modules="core,bootstrap,select2,accessontology,twitterBootstrapAffix,xeditable,assayshow,richtexteditorForEdit,projectsummary,canEditWidget,projectstep,compoundOptions"/>
</head>

<body>
<g:if test="${flash.error}">
    <div class="row-fluid">
        <div class="span12">
            <div class="alert alert-error">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>${flash.error}</strong>
            </div>
        </div>
    </div>
</g:if>
<g:if test="${flash.success}">
    <div class="row-fluid">
        <div class="span12">
            <div class="alert alert-success">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>${flash.success}</strong>
            </div>
        </div>
    </div>
</g:if>
<g:if test="${instance?.id}">
    <g:if test="${!instance?.permittedToSeeEntity()}">
        <g:render template="/layouts/templates/handleDraftEntities" model="[entity: 'Project']"/>
    </g:if>
    <g:else>
        <g:render template="showProject"
                  model="[instance: instance, editable: editable, contextItemSubTemplate:contextItemSubTemplate,
                          projectAdapter:projectAdapter,experiments:experiments,assays:assays]"/>
    </g:else>
</g:if>

</body>
</html>