<%@ page import="bard.db.enums.ContextType; bard.db.registration.DocumentKind; bard.db.model.AbstractContextOwner; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,twitterBootstrapAffix,dynatree,xeditable,experimentsummary,canEditWidget,richtexteditorForEdit, sectionCounter, card"/>
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
<g:if test="${editable == 'canedit'}">
    <p>
        <g:link action="edit" id="${instance.id}" class="btn">Edit</g:link>
    </p>
</g:if>


<div class="container-fluid">
<div class="row-fluid">
<div class="span3 bs-docs-sidebar heading-numbering">
    <ul class="nav nav-list bs-docs-sidenav twitterBootstrapAffixNavBar">
        <li><a href="#summary-header"><i class="icon-chevron-right"></i>Overview</a></li>
        <li><a href="#contexts-header"><i class="icon-chevron-right"></i>Contexts</a></li>
        <li><a href="#measures-header"><i class="icon-chevron-right"></i>Measures</a></li>
        <li><a href="#documents-header"><i class="icon-chevron-right"></i>Documents</a>
            <ul class="nav nav-list bs-docs-sidenav" style="padding-left: 0; margin: 0;">
                <li><a href="#documents-description-header"><i class="icon-chevron-right"></i>Descriptions</a>
                </li>
                <li><a href="#documents-protocol-header"><i class="icon-chevron-right"></i>Protocols</a></li>
                <li><a href="#documents-comment-header"><i class="icon-chevron-right"></i>Comments</a></li>
                <li><a href="#documents-publication-header"><i class="icon-chevron-right"></i>Publications</a>
                </li>
                <li><a href="#documents-urls-header"><i class="icon-chevron-right"></i>External URLS</a></li>
                <li><a href="#documents-other-header"><i class="icon-chevron-right"></i>Others</a></li>
            </ul>
        </li>
    </ul>
</div>
<g:hiddenField name="version" id="versionId" value="${instance.version}"/>
<div class="span9">
<section id="summary-header">
    <div class="page-header">
        <h3 class="sect">Overview</h3>
    </div>

    <div class="row-fluid">
        <dl class="dl-horizontal">
            <dt><g:message code="experiment.id.label" default="EID"/>:</dt>
            <dd>${instance?.id}</dd>
            <dt><g:message code="experiment.experimentStatus.label" default="Status"/>:</dt>
            <dd>
                <span
                        data-sourceCache="true"
                        data-toggle="manual"
                        class="status"
                        id="${instance?.experimentStatus?.id}"
                        data-type="select"
                        data-value="${instance?.experimentStatus?.id}"
                        data-source="/BARD/experiment/experimentStatus"
                        data-pk="${instance.id}"
                        data-url="/BARD/experiment/editExperimentStatus"
                        data-original-title="Select Experiment Status">${instance?.experimentStatus?.id}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Status"
                   data-id="${instance?.experimentStatus?.id}"></a>
            </dd>

            <dt><g:message code="experiment.experimentName.label" default="Name"/>:</dt>
            <dd>
                <span
                        data-toggle="manual"
                        class="experimentNameY"
                        id="nameId"

                        data-type="text"
                        data-value="${instance?.experimentName}"
                        data-pk="${instance.id}"
                        data-url="/BARD/experiment/editExperimentName"
                        data-placeholder="Required"
                        data-original-title="Edit Experiment Name">${instance?.experimentName}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Name"
                   data-id="nameId"></a>
            </dd>
            <dt><g:message code="experiment.description.label" default="Description"/>:</dt>
            <dd>
                <span
                        class="description"
                        data-toggle="manual"
                        id="descriptionId"
                        data-type="text"
                        data-value="${instance.description}"
                        data-pk="${instance.id}"
                        data-url="/BARD/experiment/editDescription"
                        data-placeholder="Required"
                        data-original-title="Edit Description By">${instance.description}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Description"
                   data-id="descriptionId"></a>
            </dd>

            <dt>Owner:</dt><dd>${experimentOwner}</dd>

            <dt><g:message code="experiment.runfromdate.label" default="Run Date from"/>:</dt>
            <dd>
                <span class="rfddate" id="rfd" data-type="combodate" data-pk="${instance.id}"
                      data-url="/BARD/experiment/editRunFromDate"
                      data-value="${instance.runDateFrom}"
                      data-original-title="Select run from date"
                      data-format="YYYY-MM-DD"
                      data-toggle="manual"
                      data-viewformat="MM/DD/YYYY"
                      data-template="D / MMM / YYYY">
                    <g:formatDate
                            format="MM/dd/yyyy"
                            date="${instance.runDateFrom}"/>
                </span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit run from date"
                   data-id="rfd"></a>
            </dd>
            <dt><g:message code="experiment.runtodate.label" default="Run Date to"/>:</dt>
            <dd>
                <span class="rdtdate" id="rdt" data-type="combodate" data-pk="${instance.id}"
                      data-url="/BARD/experiment/editRunToDate"
                      data-value="${instance.runDateTo}"
                      data-original-title="Select run to date"
                      data-toggle="manual"
                      data-format="YYYY-MM-DD"
                      data-viewformat="MM/DD/YYYY"
                      data-template="D / MMM / YYYY">
                    <g:formatDate
                            format="MM/dd/yyyy"
                            date="${instance.runDateTo}"/>
                </span><a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit run to date"
                          data-id="rdt"></a>
            </dd>
            <dt><g:message code="default.dateCreated.label"/>:</dt>
            <dd><g:formatDate date="${instance.dateCreated}" format="MM/dd/yyyy"/></dd>

            <dt><g:message code="default.lastUpdated.label"/>:</dt>
            <dd id="lastUpdatedId"><g:formatDate date="${instance.lastUpdated}"
                                                 format="MM/dd/yyyy"/></dd>

            <dt><g:message code="default.modifiedBy.label"/>:</dt>
            <dd id="modifiedById"><g:fieldValue bean="${instance}" field="modifiedBy"/></dd>
        </dl>

        <g:render template="experimentReferences" model="[experiment: instance, excludedLinks:['experiment.show']]"  />

        <g:link controller="results" action="configureTemplate"
                params="${[experimentId: instance.id]}"
                class="btn">Download a template</g:link>
        <g:if test="${editable == 'canedit'}">
            <a href="#uploadResultsModal" role="button" class="btn"
               data-toggle="modal">Upload results</a>
        </g:if>

        <g:if test="${editable == 'canedit'}">
            <g:link action="reloadResults" class="btn" id="${instance.id}">Reload Results from Pubchem</g:link>
        </g:if>

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

            <r:script>
                $("#uploadResultsButton").on("click", function () {
                    $("#uploadResultsForm").submit();
                })
            </r:script>
        </div>
</section>
<br/>
<section id="contexts-header">
    <h3 class="sect">Contexts</h3>

    <div class="row-fluid">
        <g:render template="../context/show"
                  model="[contextOwner: instance, contexts: instance.groupContexts(), uneditable: true]"/>
    </div>
    <br/>

    <div class="row-fluid">
        <g:if test="${!uneditable || true}">
            <g:if test="${editable == 'canedit' || true}">
                <div class="span12">
                    <g:link action="editContext" id="${instance?.id}"
                            params="[groupBySection: ContextType.UNCLASSIFIED.id.encodeAsURL()]"
                            class="btn"><i class="icon-pencil"></i> Edit Contexts</g:link>
                </div>
            </g:if>
        </g:if>
    </div>
</section>
<br/>
<section id="measures-header">

    <h3 class="sect">Measures</h3>

    <div class="row-fluid">
        <div id="measure-tree"></div>
        <r:script>
            $("#measure-tree").dynatree({children: ${measuresAsJsonTree} })
        </r:script>
    </div>
</section>


<g:render template="/document/documents"
          model="[documentKind: DocumentKind.ExperimentDocument, owningEntity: instance, canedit: editable, sectionNumber: '4.']"/>

<r:script>
    $("#uploadResultsButton").on("click", function () {
        $("#uploadResultsForm").submit();
    })
</r:script>
</div>
</section>
</div>
</div>
</div>
</g:if>

</body>
</html>