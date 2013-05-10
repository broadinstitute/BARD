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
    <r:require modules="core,bootstrap,assayshow,projectstep,select2,accessontology,twitterBootstrapAffix"/>
    <meta name="layout" content="basic"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">
    <title>Show Project</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div class="well well-small">
            <div class="pull-left">
                <h4>View Project (PID: ${instance?.id})</h4>
            </div>
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
    <div class="container-fluid">
        <div class="row-fluid">
            <div class="span3 bs-docs-sidebar">
                <ul class="nav nav-list bs-docs-sidenav twitterBootstrapAffixNavBar">
                    <li><a href="#summary-header"><i class="icon-chevron-right"></i>Summary</a></li>
                    <li><a href="#contexts-header"><i class="icon-chevron-right"></i>Contexts</a></li>
                    <li><a href="#experiment-and-step-header"><i class="icon-chevron-right"></i>Experiments and steps
                    </a></li>
                    <li><a href="#documents-header"><i class="icon-chevron-right"></i>Documents</a></li>
                </ul>
            </div>

            <div class="span9">
                <section id="summary-header">
                    <div class="page-header">
                        <h3>Summary</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template="showSummary" model="['project': instance]"/>
                    </div>
                </section>
                <section id="contexts-header">
                    <div class="page-header">
                        <h3>Contexts</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template="../context/show"
                                    model="[contextOwner: instance, contexts: instance.groupContexts()]"/>
                    </div>
                </section>
                <section id="experiment-and-step-header">
                    <div class="page-header">
                        <h3>Experiments and steps</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template='/project/editstep'
                                  model="['instanceId': instance.id]"/>
                        <g:render template="showstep"
                                  model="['experiments': instance.projectExperiments, 'pegraph': pexperiment, 'instanceId': instance.id]"/>

                    </div>
                </section>
                <section id="documents-header">
                    <div class="page-header">
                        <h3>Documents</h3>
                    </div>

                    <div class="row-fluid">
                        <g:link action="create" controller="document" params="${[projectId: instance.id]}"
                                class="btn btn-primary">Add new document</g:link>
                        <g:render template="/document/list"
                                  model="['documents': instance.documents, documentTemplate: '/document/edit']"/>
                    </div>
                </section>
            </div>
        </div>
    </div>
</g:if>

</body>
</html>