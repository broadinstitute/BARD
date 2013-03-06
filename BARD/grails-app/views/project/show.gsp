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
    <r:require modules="core,bootstrap,assayshow,projectstep,select2,accessontology"/>
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
            %{--The edit function is broken in project, temporary hide this for ESP demo--}%
            %{--<g:if test="${instance?.id}">--}%
                %{--<div class="pull-right">--}%
                    %{--<g:link action="edit" id="${instance?.id}" class="btn btn-small btn-info">Edit</g:link>--}%
                    %{--<g:link action="edit" id="${instance?.id}" class="btn btn-small btn-info">Clone</g:link>--}%
                %{--</div>--}%
            %{--</g:if>--}%
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
        <div id="accordion-foo" class="span12 accordion">
            <div class="accordion-group">
                <div class="accordion-heading">
                    <a href="#summary-header" id="summary-header" class="accordion-toggle" data-toggle="collapse"
                       data-target="#target-summary-info">
                        <i class="icon-chevron-down"></i>
                        Summary
                    </a>

                    <div id="target-summary-info" class="accordion-body in collapse">
                        <div class="accordion-inner">
                            <g:render template="showSummary" model="['project': instance]"/>
                        </div>
                    </div>
                </div>
            </div>


            <div class="accordion-group">
                <div class="accordion-heading">
                    <a href="#contexts-header" id="contexts-header" class="accordion-toggle" data-toggle="collapse"
                       data-target="#target-contexts-info">
                        <i class="icon-chevron-down"></i>
                        Contexts
                    </a>

                    <div id="target-contexts-info" class="accordion-body in collapse">
                        <div class="accordion-inner">
                            <g:render template="../context/show"
                                      model="[contextOwner: instance, contexts: instance.groupContexts()]"/>
                        </div>
                    </div>
                </div>
            </div>

            <div class="accordion-group">
                <div class="accordion-heading">
                    <a href="#experiment-and-step-header" id="experiment-and-step-header" class="accordion-toggle"
                       data-toggle="collapse"
                       data-target="#target-experiment-and-step-info">
                        <i class="icon-chevron-down"></i>
                        Experiments and steps
                    </a>

                    <div id="target-experiment-and-step-info" class="accordion-body in collapse">
                        <div class="accordion-inner">
                            <g:render template='/project/editstep' model="['instanceId': instance.id]"/>
                            <g:render template="showstep" model="['experiments': instance.projectExperiments, 'pegraph': pexperiment, 'instanceId': instance.id]"/>
                        </div>
                    </div>
                </div>
            </div>

            <div class="accordion-group">
                <div class="accordion-heading">
                    <a href="#documents-header" id="documents-header" class="accordion-toggle" data-toggle="collapse"
                       data-target="#target-documents-info">
                        <i class="icon-chevron-down"></i>
                        Documents
                    </a>

                    <div id="target-documents-info" class="accordion-body in collapse">
                        <div class="accordion-inner">
                            <g:render template="/document/list" model="['documents': instance.documents, documentTemplate: '/document/show']"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>    <!-- End accordion -->
    </div>

</g:if>

</body>
</html>