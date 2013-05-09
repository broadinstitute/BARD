<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap, assayshow,twitterBootstrapAffix"/>
    <meta name="layout" content="basic"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'measures-dynatree.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'card.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">
    <title>Assay Definition</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div class="well well-small">
            <div class="pull-left">
                <h4>View Assay Definition (ADID: ${assayInstance?.id})</h4>
            </div>
            <g:if test="${assayInstance?.id}">
                <div class="pull-right">
                    <g:link action="edit" id="${assayInstance?.id}" class="btn btn-small btn-info">Clone</g:link>
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

<g:if test="${assayInstance?.id}">
    <div class="container-fluid">
        <div class="row-fluid">
            <div class="span3 bs-docs-sidebar">
                <ul class="nav nav-list bs-docs-sidenav twitterBootstrapAffixNavBar">
                    <li><a href="#summary-header"><i class="icon-chevron-right"></i>Summary</a></li>
                    <li><a href="#experiments-header"><i class="icon-chevron-right"></i>Experiments</a></li>
                    <li><a href="#contexts-header"><i class="icon-chevron-right"></i>Contexts</a></li>
                    <li><a href="#measures-header"><i class="icon-chevron-right"></i>Measures</a></li>
                    <li><a href="#documents-header"><i class="icon-chevron-right"></i>Documents</a></li>
                </ul>
            </div>

            <div class="span9">
                <section id="summary-header">
                    <div class="page-header">
                        <h3>Summary</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template="showSummary" model="['assay': assayInstance]"/>
                    </div>
                </section>
                <section id="experiments-header">
                    <div class="page-header">
                        <h3>Experiments</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template="showExperiments" model="['assay': assayInstance]"/>
                    </div>
                </section>
                <section id="contexts-header">
                    <div class="page-header">
                        <h3>Contexts</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template="../context/show"
                                  model="[contextOwner: assayInstance, contexts: assayInstance.groupContexts()]"/>
                    </div>
                </section>
                <section id="measures-header">
                    <div class="page-header">
                        <h3>Measures</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template="measuresView"
                                  model="['measures': assayInstance.measures, 'measureTreeAsJson': measureTreeAsJson]"/>
                    </div>
                </section>
                <section id="documents-header">
                    <div class="page-header">
                        <h3>Documents</h3>
                    </div>

                    <div class="row-fluid">
                        <g:link action="create" controller="document" params="${[assayId: assayInstance.id]}"
                                class="btn">Add new document</g:link>
                        <g:render template="../document/list"
                                  model="[documents: assayInstance.documents, documentTemplate: '/document/edit']"/>
                    </div>
                </section>
            </div>
        </div>
    </div>
</g:if>

</body>
</html>