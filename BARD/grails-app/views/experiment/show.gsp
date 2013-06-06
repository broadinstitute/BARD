<%@ page import="bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,twitterBootstrapAffix,dynatree,xeditable,experimentsummary"/>
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
                    <li><a href="#summary-header"><i class="icon-chevron-right"></i>1. Overview</a></li>
                    <li><a href="#contexts-header"><i class="icon-chevron-right"></i>2. Contexts</a></li>
                    <li><a href="#measures-header"><i class="icon-chevron-right"></i>3. Measures</a></li>
                </ul>
            </div>
            <g:hiddenField name="version" id="versionId" value="${instance.version}"/>
            <div class="span9">
                <section id="summary-header">
                    <div class="page-header">
                        <h3>1. Overview</h3>
                    </div>

                    <div class="row-fluid">
                        <dl class="dl-horizontal">
                            <dt>Assay Definition</dt><dd><g:link controller="assayDefinition" action="show"
                                                      id="${instance.assay.id}">${instance.assay.name}</g:link>&nbsp;</dd>
                            <dt><g:message code="experiment.id.label" default="EID"/>:</dt>
                            <dd>${instance?.id}</dd>


                            <dt><g:message code="experiment.experimentStatus.label" default="Status"/>:</dt>
                            <dd>
                                <a href="#"
                                   data-sourceCache="true"
                                   class="status"
                                   id="${instance?.experimentStatus?.id}"
                                   data-type="select"
                                   data-value="${instance?.experimentStatus?.id}"
                                   data-source="/BARD/experiment/experimentStatus"
                                   data-pk="${instance.id}"
                                   data-url="/BARD/experiment/editExperimentStatus"
                                   data-original-title="Select Experiment Status">${instance?.experimentStatus?.id}</a> <i
                                    class="icon-pencil"></i>
                            </dd>

                            <dt><g:message code="experiment.experimentName.label" default="Name"/>:</dt>
                            <dd>
                                <a href="#"
                                   class="experimentNameY"
                                   id="nameId"
                                   data-type="text"
                                   data-value="${instance?.experimentName}"
                                   data-pk="${instance.id}"
                                   data-url="/BARD/experiment/editExperimentName"
                                   data-placeholder="Required"
                                   data-original-title="Edit Experiment Name">${instance?.experimentName}</a> <i
                                    class="icon-pencil"></i>
                            </dd>
                            <dt><g:message code="experiment.description.label" default="Description"/>:</dt>
                            <dd>
                                <a href="#"
                                   class="description"
                                   id="descriptionId"
                                   data-type="text"
                                   data-value="${instance.description}"
                                   data-pk="${instance.id}"
                                   data-url="/BARD/experiment/editDescription"
                                   data-placeholder="Required"
                                   data-original-title="Edit Description By">${instance.description}</a> <i class="icon-pencil"></i>
                            </dd>
                            <dt><g:message code="experiment.holduntil.label" default="Hold until"/>:</dt>
                            <dd id="huddd">
                                <a href="#" class="huddate" id="hud" data-type="combodate" data-pk="${instance.id}"
                                   data-url="/BARD/experiment/editHoldUntilDate"
                                   data-value="${instance.holdUntilDate}"
                                   data-original-title="Select hold until date"
                                   data-format="YYYY-MM-DD"
                                   data-viewformat="MM/DD/YYYY"
                                   data-template="D / MMM / YYYY">
                                    <g:formatDate
                                            format="MM/dd/yyyy"
                                            date="${instance.holdUntilDate}"/>
                                </a> <i
                                    class="icon-pencil"></i>
                              </dd>

                            <dt><g:message code="experiment.runfromdate.label" default="Run Date from"/>:</dt>
                            <dd>
                                <a href="#" class="rfddate" id="rfd" data-type="combodate" data-pk="${instance.id}"
                                   data-url="/BARD/experiment/editRunFromDate"
                                   data-value="${instance.runDateFrom}"
                                   data-original-title="Select run from date"
                                   data-format="YYYY-MM-DD"
                                   data-viewformat="MM/DD/YYYY"
                                   data-template="D / MMM / YYYY">
                                    <g:formatDate
                                            format="MM/dd/yyyy"
                                            date="${instance.runDateFrom}"/>
                                </a> <i
                                    class="icon-pencil"></i>
                          </dd>
                            <dt><g:message code="experiment.runtodate.label" default="Run Date to"/>:</dt>
                            <dd>
                                <a href="#" class="rdtdate" id="rdt" data-type="combodate" data-pk="${instance.id}"
                                   data-url="/BARD/experiment/editRunToDate"
                                   data-value="${instance.runDateTo}"
                                   data-original-title="Select run to date"
                                   data-format="YYYY-MM-DD"
                                   data-viewformat="MM/DD/YYYY"
                                   data-template="D / MMM / YYYY">
                                    <g:formatDate
                                            format="MM/dd/yyyy"
                                            date="${instance.runDateTo}"/>
                                </a><i
                                    class="icon-pencil"></i>
                            </dd>
                            <dt><g:message code="default.dateCreated.label"/>:</dt>
                            <dd><g:formatDate date="${instance.dateCreated}" format="MM/dd/yyyy"/></dd>

                            <dt><g:message code="default.lastUpdated.label"/>:</dt>
                            <dd id="lastUpdatedId"><g:formatDate date="${instance.lastUpdated}"
                                                                 format="MM/dd/yyyy"/></dd>

                            <dt><g:message code="default.modifiedBy.label"/>:</dt>
                            <dd id="modifiedById"><g:fieldValue bean="${instance}" field="modifiedBy"/></dd>
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
                        <h3>2. Contexts</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template="../context/show"
                                  model="[contextOwner: instance, contexts: instance.groupContexts(), uneditable: true]"/>
                    </div>
                </section>
                <section id="measures-header">
                    <div class="page-header">
                        <h3>3. Measures</h3>
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