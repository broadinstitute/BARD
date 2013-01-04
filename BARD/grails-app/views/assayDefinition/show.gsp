<%@ page import="bard.db.registration.*" %>
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
                <h4>View Assay Definition (ADID: ${assayInstance?.id})</h4>
            </div>
            <g:if test="${assayInstance?.id}">
                <div class="pull-right">
                    <g:link action="edit" id="${assayInstance?.id}" class="btn btn-small btn-info">Edit</g:link>
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
                            <g:render template="../summary/show" model="['summary': assayInstance]"/>
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
                            <g:render template="../context/show" model="['contexts': assayInstance.groupContexts()]"/>
                        </div>
                    </div>
                </div>
            </div>

            <div class="accordion-group">
                <div class="accordion-heading">
                    <a href="#measures-header" id="measures-header" class="accordion-toggle" data-toggle="collapse"
                       data-target="#target-measures-info">
                        <i class="icon-chevron-down"></i>
                        Measures
                    </a>

                    <div id="target-measures-info" class="accordion-body in collapse">
                        <div class="accordion-inner">
                            <g:render template="measuresView" model="['assayInstance': assayInstance]"/>
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
                            <g:render template="../document/show" model="['documents': assayInstance.documents]"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>    <!-- End accordion -->
    </div>
</g:if>

</body>
</html>