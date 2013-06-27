<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="bard.db.registration.DocumentKind; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,select2,accessontology,twitterBootstrapAffix,xeditable,assayshow,richtexteditorForEdit,projectsummary,canEditWidget,projectstep"/>
    <meta name="layout" content="basic"/>
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
                    <li><a href="#summary-header"><i class="icon-chevron-right"></i>1. Overview</a></li>
                    <li><a href="#contexts-header"><i class="icon-chevron-right"></i>2. Contexts</a></li>
                    <li><a href="#experiment-and-step-header"><i class="icon-chevron-right"></i>3. Experiments and steps
                        <li><a href="#documents-header"><i class="icon-chevron-right"></i>4. Documents</a></li>
                        <li><a href="#documents-description-header"><i class="icon-chevron-right"></i>4.1 Descriptions
                        </a>
                        </li>
                        <li><a href="#documents-protocol-header"><i class="icon-chevron-right"></i>4.2 Protocols</a>
                        </li>
                        <li><a href="#documents-comment-header"><i class="icon-chevron-right"></i>4.3 Comments</a></li>
                        <li><a href="#documents-publication-header"><i class="icon-chevron-right"></i>4.4 Publications
                        </a>
                        </li>
                        <li><a href="#documents-urls-header"><i class="icon-chevron-right"></i>4.5 External URLS</a>
                        </li>
                        <li><a href="#documents-other-header"><i class="icon-chevron-right"></i>4.6 Others</a></li>
                    </a></li>
                </ul>
            </div>

            <div class="span9">
                <section id="summary-header">
                    <div class="page-header">
                        <h3>1. Overview</h3>

                    </div>

                    <div class="row-fluid">
                        <g:render template='editSummary' model="['project': instance, canedit: editable]"/>
                    </div>
                </section>
                <br/>
                <section id="contexts-header">
                    <h3>2. Contexts</h3>

                    <div class="row-fluid">
                        <g:render template="../context/show"
                                  model="[contextOwner: instance, contexts: instance.groupContexts(), canedit: editable]"/>
                    </div>
                </section>
                <br/>
                <section id="experiment-and-step-header">
                    <h3>3. Experiments and steps</h3>

                    <div class="row-fluid">
                        <g:render template='/project/editstep'
                                  model="['instanceId': instance.id, canedit: editable]"/>
                        <g:render template="showstep"
                                  model="['experiments': instance.projectExperiments,
                                          'pegraph': pexperiment, 'instanceId': instance.id, canedit: editable ]"/>

                    </div>
                </section>
                <br/>
                <g:render template="/document/documents"
                          model="[documentKind: DocumentKind.ProjectDocument, owningEntity: instance, canedit: editable]"/>
            </div>
        </div>
    </div>
</g:if>

</body>
</html>