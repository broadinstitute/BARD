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
    <r:require modules="core,bootstrap, assayshow"/>
    <meta name="layout" content="basic"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">
    <title>Assay Definition</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div class="well well-small">
            <div class="pull-left">
                <h4>View Project (PID: ${instance?.id})</h4>
            </div>
            <g:if test="${instance?.id}">
                <div class="pull-right">
                    <g:link action="edit" id="${instance?.id}" class="btn btn-small btn-info">Edit</g:link>
                    <g:link action="edit" id="${instance?.id}" class="btn btn-small btn-info">Clone</g:link>
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
        <div id="accordion" class="span12">
            <h3><a href="#">Summary</a></h3>
            <g:render template="../summary/show" model="['summary': instance]"/>

            <h3><a href="#">Documents</a></h3>
            <g:render template="../document/show" model="['documents': instance.documents]"/>

            <h3><a href="#">Contexts</a></h3>
            <g:render template="../context/show" model="['contexts': instance.contexts]"/>

            <h3><a href="#">Context Items</a></h3>
            <g:render template="../contextItem/show" model="['contexts': instance.contexts]"/>
        </div>
    </div>
</g:if>

</body>
</html>