<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,assayshow,twitterBootstrapAffix,xeditable,assaysummary"/>
    <meta name="layout" content="basic"/>
    <title>Assay Definition</title>
</head>

<body>
<g:hiddenField name="assayId" id="assayId" value="${assayInstance?.id}"/>
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
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">

        </div>

        <div class="span9">
            <div class="pull-left">
                <h4>View Assay Definition (ADID: ${assayInstance?.id})</h4>
            </div>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span3">

        </div>

        <div class="span9">
            <div class="pull-left">
                <g:link action="cloneAssay" id="${assayInstance?.id}" class="btn"><g:img
                        uri="/images/clone-icon.png"/> Clone Assay Definition</g:link>
            </div>
        </div>
    </div>
</div>
<g:if test="${assayInstance?.id}">
    <div class="container-fluid">
        <div class="row-fluid">
            <div class="span3 bs-docs-sidebar">
                <ul class="nav nav-list bs-docs-sidenav twitterBootstrapAffixNavBar">
                    <li><a href="#summary-header"><i class="icon-chevron-right"></i>1. Overview</a></li>
                    <li><a href="#experiments-header"><i class="icon-chevron-right"></i>2. Experiments</a></li>
                    <li><a href="#contexts-header"><i class="icon-chevron-right"></i>3. Contexts</a></li>
                    <li><a href="#measures-header"><i class="icon-chevron-right"></i>4. Measures</a></li>
                    <li><a href="#documents-header"><i class="icon-chevron-right"></i>5. Documents</a></li>
                    <li><a href="#documents-description-header"><i class="icon-chevron-right"></i>5.1 Descriptions</a>
                    </li>
                    <li><a href="#documents-protocol-header"><i class="icon-chevron-right"></i>5.2 Protocols</a></li>
                    <li><a href="#documents-comment-header"><i class="icon-chevron-right"></i>5.3 Comments</a></li>
                    <li><a href="#documents-publication-header"><i class="icon-chevron-right"></i>5.4 Publications</a>
                    </li>
                    <li><a href="#documents-urls-header"><i class="icon-chevron-right"></i>5.5 External URLS</a></li>
                    <li><a href="#documents-other-header"><i class="icon-chevron-right"></i>5.6 Others</a></li>
                </ul>
            </div>

            <div class="span9">
                <section id="summary-header">
                    <h3>1. Overview</h3>

                    <div class="row-fluid">
                        <div id="msg" class="alert hide"></div>

                        <div id="showSummary">
                            <g:render template='editSummary' model="['assay': assayInstance]"/>
                        </div>
                    </div>
                </section>
                <section id="experiments-header">
                    <div class="page-header">
                        <h3>2. Experiments</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template="showExperiments" model="['assay': assayInstance]"/>
                    </div>
                </section>
                <section id="contexts-header">
                    <div class="page-header">
                        <h3>3. Contexts</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template="../context/show"
                                  model="[contextOwner: assayInstance, contexts: assayInstance.groupContexts()]"/>
                    </div>
                </section>
                <section id="measures-header">
                    <div class="page-header">
                        <h3>4. Measures</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template="measuresView"
                                  model="['measures': assayInstance.measures, 'measureTreeAsJson': measureTreeAsJson]"/>
                    </div>
                </section>
                <g:render template="assayDocuments" model="[assay: assayInstance]"/>
            </div>
        </div>
    </div>
</g:if>

</body>
</html>