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

<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${flash.message}</strong>
                </div>
            </div>
        </div>
    </div>
</g:if>

<g:if test="${instance?.id}">
    <g:if test="${instance?.permittedToSeeEntity()}">
        <g:render template="showExperiment"
                  model="[instance: instance, editable: editable, uneditable: uneditable, contextIds: contextIds]"/>
    </g:if>
    <g:else>
        <g:render template="/layouts/templates/handleDraftEntities" model="[entity: 'Experiment']"/>
    </g:else>
</g:if>

</body>
</html>