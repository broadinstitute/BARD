<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils; bard.db.enums.Status; bard.db.dictionary.Element; bard.db.model.AbstractContextItem; bard.db.model.AbstractContext; bard.db.enums.Status; bard.db.enums.ContextType; bard.db.registration.DocumentKind; bard.db.model.AbstractContextOwner; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,twitterBootstrapAffix,xeditable,experimentsummary,canEditWidget,richtexteditorForEdit, sectionCounter, card,histogram"/>
    <meta name="layout" content="basic"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>EID ${instance?.id}: ${instance?.permittedToSeeEntity() ? instance?.experimentName : 'Experiment Pending'}</title>

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
    <g:if test="${instance?.permittedToSeeEntity()}">
        <g:render template="showExperiment"
                  model="[instance: instance, editable: editable, contextItemSubTemplate: contextItemSubTemplate, contextIds: contextIds, isAdmin: isAdmin]"/>
    </g:if>
    <g:else>
        <g:render template="/layouts/templates/handleDraftEntities" model="[entity: 'Experiment']"/>
    </g:else>
</g:if>

</body>
</html>