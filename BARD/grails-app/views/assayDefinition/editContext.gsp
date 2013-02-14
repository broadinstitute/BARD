<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,assaycards,select2"/>
    <meta name="layout" content="basic"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'AddItemWizard.css')}"/>
    <title>Edit Assay Context</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div class="well well-small">
            <div class="pull-left">
                <h4>Edit Assay Context (ADID: ${assayInstance?.id})</h4>
            </div>
            <g:if test="${assayInstance?.id}">
                <div class="pull-right">
                    <g:link action="show" id="${assayInstance?.id}"
                            class="btn btn-small btn-primary">Finish Editing</g:link>
                    %{--<g:link action="show" id="${assayInstance?.id}" class="btn btn-small">Cancel</g:link>--}%
                </div>
            </g:if>
        </div>
    </div>
</div>

<div class="alert">
    <button type="button" class="close" data-dismiss="alert">×</button>
    <strong>Tips:</strong> Edits will be saved immediately. You can drag items within cards to other cards, or use menus on individual cards to move items.
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

<g:if test="${assayInstance?.id}">
    <div class="row-fluid">
        <div class="row-fluid">
            <div id="accordion-foo" class="span12 accordion">



                <div class="accordion-group">
                    <div class="accordion-heading">
                        <a href="#contexts-header" id="contexts-header" class="accordion-toggle" data-toggle="collapse"
                           data-target="#target-contexts-info">
                            <i class="icon-chevron-down"></i>
                            Contexts
                        </a>
                    </div>

                    <div id="target-contexts-info" class="accordion-body in collapse">
                            <div class="accordion-inner">
                                <g:render template="../context/edit"
                                          model="[contextOwner: assayInstance, contexts: assayInstance.groupContexts()]"/>
                            </div>
                    </div>
                </div>

            </div>    <!-- End accordion -->
        </div>

    </div>
</g:if>

</body>
</html>