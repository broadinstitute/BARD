<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap, assaycards,projectstep"/>
    <meta name="layout" content="basic"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">
    <title>Edit Project</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div class="well well-small">
            <div class="pull-left">
                <h4>Edit Project (PID: ${instance?.id})</h4>
            </div>
            <g:if test="${instance?.id}">
                <div class="pull-right">
                    <g:link action="show" id="${instance?.id}" class="btn btn-small btn-primary">Finish Editing</g:link>
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

<g:if test="${instance?.id}">
    <div class="row-fluid">
        <div id="accordion" class="span12">
            <h3><a href="#">Summary</a></h3>
            <g:render template="../summary/show" model="['summary': instance]"/>

            <h3><a href="#">Documents</a></h3>
            <g:render template="../document/show" model="['documents': instance.documents]"/>

            <h3><a href="#">Contexts</a></h3>
            <g:render template="../context/edit" model="['contexts': instance.groupContexts(), 'instanceId': instance.id]"/>

            <h3><a href="#">Experiments and steps</a></h3>
            <g:render template="showstep" model="['experiments': instance.projectExperiments, 'pegraph': pexperiment]"/>
        </div>
    </div>
</g:if>

</body>
</html>