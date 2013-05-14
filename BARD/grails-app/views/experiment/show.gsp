<%@ page import="bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,twitterBootstrapAffix,dynatree"/>
    <meta name="layout" content="basic"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>Show Experiment</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div class="well well-small">
            <div class="pull-left">
                <h4>View Experiment (${instance?.id})</h4>
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
    <p>
        <g:link action="edit" id="${instance.id}" class="btn">Edit</g:link>
    </p>

    <div class="container-fluid">
        <div class="row-fluid">
            <div class="span3 bs-docs-sidebar">
                <ul class="nav nav-list bs-docs-sidenav twitterBootstrapAffixNavBar">
                    <li><a href="#summary-header"><i class="icon-chevron-right"></i>Summary</a></li>
                    <li><a href="#contexts-header"><i class="icon-chevron-right"></i>Contexts</a></li>
                    <li><a href="#measures-header"><i class="icon-chevron-right"></i>Measures</a></li>
                </ul>
            </div>

            <div class="span9">
                <section id="summary-header">
                    <div class="page-header">
                        <h3>Summary</h3>
                    </div>

                    <div class="row-fluid">
                        <%-- Added &nbsp; because there appears to be some problem with dd tags that causes them to shift up a row if a dd tag no content.  Need to investigate this further later. --%>
                        <dl class="dl-horizontal">
                            <dt>Assay</dt><dd><g:link controller="assayDefinition" action="show"
                                                      id="${instance.assay.id}">${instance.assay.name}</g:link>&nbsp;</dd>
                            <dt>Experiment Name</dt><dd>${instance.experimentName}&nbsp;</dd>
                            <dt>Description</dt><dd>${instance.description}&nbsp;</dd>
                            <dt>Status</dt><dd>${instance.experimentStatus}&nbsp;</dd>
                            <dt>Hold until</dt><dd><g:formatDate format="MM/dd/yyyy"
                                                                 date="${instance.holdUntilDate}"/></dd>
                            <dt>Run Date from</dt><dd><g:formatDate format="MM/dd/yyyy"
                                                                    date="${instance.runDateFrom}"/></dd>
                            <dt>Run Date to</dt><dd><g:formatDate format="MM/dd/yyyy"
                                                                  date="${instance.runDateTo}"/></dd>
                        </dl>

                        <p>External references</p>
                        <ul>
                            <g:each in="${instance.externalReferences}" var="xRef">
                                <li>
                                    <a href="${xRef.externalSystem.systemUrl}${xRef.extAssayRef}"
                                       target="_blank">${xRef.externalSystem.systemName} ${xRef.extAssayRef}</a>
                                </li>
                            </g:each>
                        </ul>

                        <p>Referenced by projects:</p>
                        <ul>
                            <g:each in="${instance.projectExperiments}" var="projectExperiment">
                                <li><g:link controller="project" action="show"
                                            id="${projectExperiment.project.id}">${projectExperiment.project.name}</g:link></li>
                            </g:each>
                        </ul>

                        <g:link controller="results" action="configureTemplate"
                                params="${[experimentId: instance.id]}"
                                class="btn">Download a template</g:link>
                        <a href="#uploadResultsModal" role="button" class="btn"
                           data-toggle="modal">Upload results</a>
                        <%-- Dialog for uploading results --%>
                        <div id="uploadResultsModal" class="modal fade" tabindex="-1" role="dialog"
                             aria-labelledby="uploadResultsModalLabel" aria-hidden="true">
                            <div class="modal-header">
                                <h3 id="uploadResultsModalLabel">Upload Results</h3>
                            </div>

                            <div class="modal-body">
                                <form method="POST" enctype="multipart/form-data"
                                      action="${createLink(action: "uploadResults", controller: "results")}"
                                      id="uploadResultsForm">
                                    <p>Uploading results will replace any results already stored for this experiment.</p>

                                    <p>Select a file to upload</p>
                                    <input type="hidden" name="experimentId" value="${instance.id}">
                                    <input type="file" name="resultsFile">
                                </form>
                            </div>

                            <div class="modal-footer">
                                <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
                                <button class="btn btn-primary" id="uploadResultsButton">Upload</button>
                            </div>
                        </div>

                        <r:script>
                            $("#uploadResultsButton").on("click", function () {
                                $("#uploadResultsForm").submit();
                            })
                        </r:script>
                    </div>
                </section>
                <section id="contexts-header">
                    <div class="page-header">
                        <h3>Contexts</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template="../context/show"
                                  model="[contextOwner: instance, contexts: instance.groupContexts(), uneditable: true]"/>
                    </div>
                </section>
                <section id="measures-header">
                    <div class="page-header">
                        <h3>Measures</h3>
                    </div>

                    <div class="row-fluid">
                        <div id="measure-tree"></div>
                        <r:script>
                            $("#measure-tree").dynatree({
                                children: ${measuresAsJsonTree} })
                        </r:script>
                    </div>
                </section>
            </div>
        </div>
    </div>
</g:if>

</body>
</html>