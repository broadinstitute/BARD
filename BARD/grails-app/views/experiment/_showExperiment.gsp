<%@ page import="bard.db.enums.Status; org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils; bard.db.dictionary.Element; bard.db.model.AbstractContextItem; bard.db.model.AbstractContext; bard.db.enums.Status; bard.db.enums.ContextType; bard.db.registration.DocumentKind; bard.db.model.AbstractContextOwner; bard.db.project.*" %>
<g:hiddenField name="experimentId" id="experimentId" value="${instance?.id}"/>
<div class="row-fluid">
    <div class="span12">
        <div class="well well-small">
            <div class="pull-left">
                <h4>View Experiment (${instance?.id})</h4>
            </div>
        </div>
    </div>
</div>

<div class="container-fluid">
<div class="row-fluid">
<div class="span3 bs-docs-sidebar heading-numbering">
    <ul class="nav nav-list bs-docs-sidenav twitterBootstrapAffixNavBar">
        <li><a href="#summary-header"><i class="icon-chevron-right"></i>Overview</a></li>
        <li><a href="#contexts-header"><i class="icon-chevron-right"></i>Annotations</a></li>
        <li><a href="#referenced-contexts-header"><i class="icon-chevron-right"></i>Experimental Variables</a></li>
        <li><a href="#results-header"><i class="icon-chevron-right"></i>Results</a>
            <ul class="nav nav-list bs-docs-sidenav" style="padding-left: 0; margin: 0;">
                <li><a href="#result-type-header"><i class="icon-chevron-right"></i>Result Types</a></li>
                <g:if test="${instance.ncgcWarehouseId && instance.experimentFiles}">
                    <li><a href="#results-summary-histogram"><i class="icon-chevron-right"></i>Result Summary- Histogram
                    </a></li>
                </g:if>
                <li><a href="#results-summary-details"><i class="icon-chevron-right"></i>Result Summary - Details
                </a></li>
            </ul>
        </li>

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
<g:render template="../layouts/templates/askAQuestion" model="['entity': 'Experiment']"/>

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
                        data-source="${request.contextPath}/experiment/experimentStatus"
                        data-pk="${instance?.id}"
                        data-url="${request.contextPath}/experiment/editExperimentStatus"
                        data-original-title="Select Experiment Status">${instance?.experimentStatus?.id}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Status"
                   data-id="${instance?.experimentStatus?.id}"></a>

                <g:render template="/common/statusIcons" model="[status:instance?.experimentStatus.id, entity: 'Experiment']"/>

            </dd>
            <g:render template="/common/statusMessage" model="[status:instance?.experimentStatus,
                    displayName:instance.approvedBy?.displayName,
                    approvedDate:instance.approvedDate]"/>


            <dt><g:message code="experiment.experimentName.label" default="Name"/>:</dt>
            <dd>
                <span
                        data-toggle="manual"
                        class="experimentNameY"
                        id="nameId"
                        data-inputclass="input-xxlarge"
                        data-type="textarea"
                        data-value="${instance?.experimentName}"
                        data-pk="${instance?.id}"
                        data-url="${request.contextPath}/experiment/editExperimentName"
                        data-placeholder="Required"
                        data-original-title="Edit Experiment Name">${instance?.experimentName}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Name"
                   data-id="nameId"></a>
            </dd>
            <g:if test="${editable == 'canedit'}">
                <dt><g:message code="experiment.description.label" default="Description"/>:</dt>
                <dd>

                    <span
                            class="description"
                            data-toggle="manual"
                            id="descriptionId"
                            data-inputclass="input-xxlarge"
                            data-type="textarea"
                            data-value="${instance.description}"
                            data-pk="${instance?.id}"
                            data-url="${request.contextPath}/experiment/editDescription"
                            data-placeholder="Required"
                            data-original-title="Edit Description By">${instance.description}</span>
                    <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit Description"
                       data-id="descriptionId"></a>

                </dd>
            </g:if>
            <dt><g:message code="experiment.ownerRole.label" default="Owner"/>:</dt>
            <dd>
                <span
                        class="status"
                        data-toggle="manual"
                        data-sourceCache="false"
                        id="ownerRoleId"
                        data-type="select"
                        data-value="${instance?.owner}"
                        data-source="${request.contextPath}/assayDefinition/roles"
                        data-pk="${instance?.id}"
                        data-url="${request.contextPath}/experiment/editOwnerRole"
                        data-placeholder="Required"
                        data-original-title="Select Owner Role">${instance?.owner}</span>
                <a href="#" class="icon-pencil documentPencil ${editable}" data-id="ownerRoleId"
                   title="Click to edit owner role"></a>
            </dd>
            <g:if test="${editable == 'canedit'}">
                <dt><g:message code="experiment.runfromdate.label" default="Run Date from"/>:</dt>
                <dd>
                    <span class="rfddate" id="rfd" data-type="combodate" data-pk="${instance?.id}"
                          data-url="${request.contextPath}/experiment/editRunFromDate"
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
                    <span class="rdtdate" id="rdt" data-type="combodate" data-pk="${instance?.id}"
                          data-url="${request.contextPath}/experiment/editRunToDate"
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
            </g:if>
            <dt><g:message code="default.dateCreated.label"/>:</dt>
            <dd><g:formatDate date="${instance.dateCreated}" format="MM/dd/yyyy"/></dd>

            <dt><g:message code="default.lastUpdated.label"/>:</dt>
            <dd id="lastUpdatedId"><g:formatDate date="${instance.lastUpdated}"
                                                 format="MM/dd/yyyy"/></dd>

            <dt><g:message code="default.modifiedBy.label"/>:</dt>
            <dd id="modifiedById"><g:renderModifiedByEnsureNoEmail entity="${instance}"/></dd>
        </dl>

        <g:render template="experimentReferences"
                  model="[experiment: instance, excludedLinks: ['experiment.show'], editable: editable]"/>
        <sec:ifLoggedIn>
            <g:link controller="results" action="configureTemplate"
                    params="${[experimentId: instance?.id]}"
                    class="btn">Download a template</g:link>
        </sec:ifLoggedIn>
        <g:if test="${editable == 'canedit'}">
            <a href="#uploadResultsModal" role="button" class="btn"
               data-toggle="modal">Upload results</a>
            <g:link action="reloadResults" class="btn" id="${instance?.id}">Reload Results from Pubchem</g:link>
            <g:link controller="externalReference" action="create"
                    params="[ownerClass: instance.class.simpleName, ownerId: instance.id]"
                    class="btn">Add an External Reference</g:link>
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
                    <input type="hidden" name="experimentId" value="${instance?.id}">
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
    <h3 class="sect">Annotations <g:link target="dictionary" controller="element" action="showTopLevelHierarchyHelp"><i
            class="icon-question-sign"></i></g:link></h3>

    <div class="row-fluid">
        <g:render template="/context/currentCard"
                  model="[contextOwner: instance, currentCard: instance.groupUnclassified(),
                          subTemplate: contextItemSubTemplate]"/>
    </div>

</section>

<section id="referenced-contexts-header">
    <h3 class="sect">Experimental Variables <g:link target="dictionary" controller="element"
                                                    action="showTopLevelHierarchyHelp"><i
                class="icon-question-sign"></i></g:link></h3>

    <div class="row-fluid">
        <%
            //This relates to assay's experimental-variables that are displayed in the experiment show page.
            //If the attribute/value pair is provided by the experiment annotation, at the experiment level, we don't want to display its (duplicated) counterpart at experimental varibales section as well.
            //We are detecting the duplicates by comparing context-item attributes between the two contexts (experiment annotation and assay's experimental-variables contexts).
            //If two contexts have the same set of attribute elements (ignoring attribute values) we will remove that context from the assay's experimental variables contexts set.
            //See: https://www.pivotaltracker.com/story/show/60411318
            //
            //Get all assay's experimental variables contexts.
            AbstractContextOwner.ContextGroup assayExperimentalVariablesContextGroup = instance.assay.groupExperimentalVariables()
            //Experiment's list of annotations attributes.
            List<Element> experimentAnnotationsAttributeList = instance.contexts*.contextItems.attributeElement.flatten()

            // Remove all the experimental-variables context cards that have ALL their context-items already populated in experiment's annotations.
            assayExperimentalVariablesContextGroup.value.removeAll { AbstractContext context ->
                experimentAnnotationsAttributeList.containsAll(context.contextItems*.attributeElement)
            }

//            //Find and kepp only the contexts that have at least one context-item attribute not being defined in the experiment annotations.
//            List<AbstractContext> currentCard = assayExperimentalVariablesContextGroup.value.findAll { AbstractContext context ->
//                context.contextItems.find { AbstractContextItem contextItem ->
//                    return !experimentAnnotationsAttributeList.contains(contextItem.attributeElement) } }
//            assayExperimentalVariablesContextGroup.value = currentCard
        %>
        <g:render template="/context/currentCard"
                  model="[contextOwner: instance.assay,
                          currentCard: assayExperimentalVariablesContextGroup,
                          subTemplate: 'show',
                          showCheckBoxes: false,
                          existingContextIds: contextIds,
                          displayNonFixedContextsOnly: true,
                          renderEmptyGroups: false,
                          experimentId: instance?.id
                  ]"/>
    </div>
    <br/>
</section>
<section id="results-header">
    <div class="page-header">
        <h3 class="sect">Results</h3>
        <section id="result-type-header">
            <h4 class="subsect">Result Types</h4>
            <g:if test="${instance.experimentMeasures}">
                <div class="row-fluid">
                    <span><i
                            class='icon-star'></i> by any node in the tree below, means that the result type is a priority element
                    <g:render template="priorityElementDictionary"/>
                    </span> <br/><br/>

                    <div class="tree" id="result-type-table">

                    </div>
                </div>
            </g:if>
            <br/>
            <g:if test="${editable == 'canedit'}">
                <g:if test="${instance.experimentMeasures}">
                </g:if>
                <g:else>
                    <p><b>No result types specified</b></p>
                </g:else>

                <g:link action="addResultTypes" params="[experimentId: instance?.id]" class="btn"><i
                        class="icon-plus"></i> Add result type</g:link>
                <g:link action="addDoseResultTypes" params="[experimentId: instance?.id]"
                        class="btn"><i class="icon-plus"></i> Add dose response result type</g:link>

            </g:if>
            <br/>
            <br/>
        </section>
        <g:if test="${instance.ncgcWarehouseId && instance.experimentFiles}">
            <section id="results-summary-histogram">
                <h4 class="subsect">Result Summary - Histogram</h4>

                <div class="row-fluid">
                    <div id="histogramHere" class="span12"></div>
                </div>

            </section>
        </g:if>
        <section id="results-summary-details">
            <h4 class="subsect">Result Summary - Details</h4>

            <div class="row-fluid">
                <g:if test="${isAdmin && instance.experimentFiles}">
                    <g:link controller="bardWebInterface" action="showExperiment"
                            id="${instance?.id}"><h4>View all results for this experiment</h4></g:link>
                    <g:link controller="bardWebInterface" action="previewResults"
                            id="${instance?.id}"><h4>Preview results</h4></g:link>
                </g:if>
                <g:elseif test="${instance.ncgcWarehouseId && instance.experimentFiles}">
                    <g:link controller="bardWebInterface" action="showExperiment"
                            id="${instance?.id}"><h4>View all results for this experiment</h4></g:link>
                </g:elseif>
                <g:elseif test="${!instance.experimentFiles}">
                    <h4>No results uploaded for this experiment</h4>
                </g:elseif>
                <g:else>
                    <g:if test="${instance.experimentStatus == Status.APPROVED || instance.experimentStatus == Status.PROVISIONAL}">
                        <h4>Results for this experiment aren't available for querying because this experiment is waiting to be loaded to the warehouse.</h4>
                    </g:if>
                    <g:elseif test="${instance.experimentStatus == Status.RETIRED}">
                        <h4>Results for this experiment aren't available for querying because this experiment has a Retired status</h4>
                    </g:elseif>
                    <g:else>
                        <h4>Results for this experiment aren't available for querying because this experiment has a Draft status and needs to be approved</h4>
                    </g:else>
                    <g:if test="${editable == 'canedit'}">
                        <g:link controller="bardWebInterface" action="previewResults"
                                id="${instance?.id}"><h4>Preview results</h4></g:link>
                    </g:if>
                </g:else>

            </div>

        </section>
    </div>
</section>
<br/>

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
