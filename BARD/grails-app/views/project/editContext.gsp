<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,select2,card,bootstrapplus"/>
    <meta name="layout" content="basic"/>
    <title>Edit ${instance?.getDomainClass()?.getNaturalName()} Context</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div class="well well-small">
            <div class="pull-left">
                <h4>Edit ${instance?.getDomainClass()?.getNaturalName()} (${instance?.id}) Context</h4>
            </div>
            <g:if test="${instance?.id}">
                <div class="pull-right">
                    <g:link action="show" id="${instance?.id}"
                            class="btn btn-small btn-primary">Finish Editing</g:link>
                </div>
            </g:if>
        </div>
    </div>
</div>

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
    <div class="row-fluid">
        <div class="row-fluid">
            <div class="accordion-inner">
                <g:render template="../context/edit"
                          model="[contextOwner: instance, contexts: instance?.groupContexts()]"/>
            </div>

        </div>

    </div>
</g:if>

</body>
</html>