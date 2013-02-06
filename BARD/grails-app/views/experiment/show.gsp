<%@ page import="bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
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
    <div class="row-fluid">
        <div id="accordion-foo" class="span12 accordion">

            <div class="accordion-group">
                <div class="accordion-heading">
                    <a href="#contexts-header" class="accordion-toggle" data-toggle="collapse"
                       data-target="#target-contexts-info">
                        <i class="icon-chevron-down"></i>
                        Summary
                    </a>
                </div>

                <div class="accordion-body in collapse">
                    <div class="accordion-inner">
                        <table>
                            <tbody>
                            <tr><td>Description</td><td>${instance.description}</td></tr>
                            <tr><td>Experiment Name</td><td>${instance.experimentName}</td></tr>

                            <tr><td>Assay</td><td><g:link controller="assayDefinition" action="show"
                                                          id="${instance.assay.id}">${instance.assay.name}</g:link></td>
                            </tr>
                            <tr><td>Status</td><td>${instance.experimentStatus}</td></tr>
                            <tr><td>Hold until</td><td>${instance.holdUntilDate}</td></tr>
                            <tr><td>Ready for extraction</td><td>${instance.readyForExtraction}</td></tr>
                            <tr><td>Run Date from</td><td>${instance.runDateFrom}</td></tr>
                            <tr><td>Run Date to</td><td>${instance.runDateTo}</td></tr>

                            <tr><td>External references</td><td>
                                <ul>
                                    <g:each in="${instance.externalReferences}" var="xref">
                                        <li>
                                            ${xref.externalSystem.systemName}
                                            ${xref.extAssayRef}
                                        </li>
                                    </g:each>
                                </ul>

                            </td></tr>
                            </tbody>
                        </table>

                        <p>Referenced by projects:</p>
                        <ul>
                            <g:each in="${instance.projectExperiments}" var="projectExperiment">
                                <li><g:link controller="project" action="show"
                                            id="${projectExperiment.project.id}">${projectExperiment.project.name}</g:link></li>
                            </g:each>
                        </ul>

                        <g:link controller="results" action="configureTemplate" params="${[experimentId: instance.id]}"
                                class="btn">Download a template</g:link>
                        <a href="#uploadResultsModal" role="button" class="btn" data-toggle="modal">Upload results</a>
                        <%-- Dialog for uploading results --%>
                        <div id="uploadResultsModal" class="modal fade" tabindex="-1" role="dialog"
                             aria-labelledby="uploadResultsModalLabel" aria-hidden="true">
                            <div class="modal-header">
                                <h3 id="uploadResultsModalLabel">Upload Results</h3>
                            </div>

                            <div class="modal-body">
                                <form method="POST" enctype="multipart/form-data" action="${createLink(action: "uploadResults")}"
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
                            console.log("h")
                            $("#uploadResultsButton").on("click", function () {
                                console.log("b")
                                $("#uploadResultsForm").submit()
                            })
                        </r:script>

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
                </div>

                <div id="target-measures-info" class="accordion-body in collapse">
                    <div class="accordion-inner">
                        <ul>
                            <g:each in="${instance.experimentMeasures}" var="experimentMeasure">
                                <li>${experimentMeasure.measure.displayLabel}</li>
                            </g:each>
                        </ul>
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
                </div>

                <div id="target-contexts-info" class="accordion-body in collapse">
                    <div class="accordion-inner">
                        <g:render template="../context/show"
                                  model="[contextOwner: instance, contexts: instance.groupContexts()]"/>
                    </div>
                </div>
            </div>

        </div>    <!-- End accordion -->
    </div>

</g:if>

</body>
</html>