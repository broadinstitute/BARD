<%@ page import="org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils; bard.db.enums.Status; bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,assayshow,twitterBootstrapAffix,xeditable,richtexteditorForEdit,assaysummary,canEditWidget"/>
    <meta name="layout" content="basic"/>
    <title>ADID ${assayInstance?.id}: ${assayInstance?.assayName}</title>
</head>

<body>
<g:hiddenField name="assayId" id="assayId" value="${assayInstance?.id}"/>

<g:if test="${message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${message}</strong>
                    </p>
                </div>
            </div>
        </div>
    </div>
</g:if>

<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${flash.message}</strong>
                    </p>
                </div>
            </div>
        </div>
    </div>
</g:if>
<g:if test="${!assayInstance?.permittedToSeeEntity()}">
    <g:render template="/layouts/templates/handleDraftEntities" model="[entity: 'Assay Definition']"/>
</g:if>
<g:else>
    <g:render template="showAssayDefinition"
              model="[assayInstance: assayInstance, editable: editable, uneditable: uneditable, experimentsActiveVsTested:experimentsActiveVsTested]"/>
</g:else>

</body>
</html>