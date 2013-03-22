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
    <p>
        <g:link action="edit" id="${instance.id}" class="btn">Edit</g:link>
    </p>

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
                        <%-- Added &nbsp; because there appears to be some problem with dd tags that causes them to shift up a row if a dd tag no content.  Need to investigate this further later. --%>
                        <dl class="dl-horizontal">
                            <dt>Assay</dt><dd><g:link controller="assayDefinition" action="show"
                                                  id="${instance.assay.id}">${instance.assay.name}</g:link>&nbsp;</dd>
                            <dt>Experiment Name</dt><dd>${instance.experimentName}&nbsp;</dd>
                            <dt>Description</dt><dd>${instance.description}&nbsp;</dd>
                            <dt>Status</dt><dd>${instance.experimentStatus}&nbsp;</dd>
                            <dt>Hold until</dt><dd>${instance.holdUntilDate}&nbsp;</dd>
                            <dt>Run Date from</dt><dd>${instance.runDateFrom}&nbsp;</dd>
                            <dt>Run Date to</dt><dd>${instance.runDateTo}&nbsp;</dd>
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
                                <form method="POST" enctype="multipart/form-data" action="${createLink(action: "uploadResults", controller:"results")}"
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

                        <r:require module="dynatree"/>
                        <div id="measure-tree"></div>
                        <r:script>
                            $("#measure-tree").dynatree({
                                children: ${ measuresAsJsonTree } })
                        </r:script>
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
                                  model="[contextOwner: instance, contexts: instance.groupContexts(), uneditable: true]"/>
                    </div>
                </div>
            </div>

        </div>    <!-- End accordion -->
    </div>

</g:if>

</body>
</html>