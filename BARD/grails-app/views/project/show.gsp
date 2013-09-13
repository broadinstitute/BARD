<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; bard.db.registration.DocumentKind; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Project : ID ${instance.id}</title>
    <r:require
            modules="core,bootstrap,select2,accessontology,twitterBootstrapAffix,xeditable,assayshow,richtexteditorForEdit,projectsummary,canEditWidget,projectstep,compoundOptions"/>
</head>

<body>

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
            <div class="span3 bs-docs-sidebar heading-numbering">
                <ul class="nav nav-list bs-docs-sidenav twitterBootstrapAffixNavBar">
                    <li><a href="#summary-header"><i class="icon-chevron-right"></i>Overview</a></li>
                    <li><a href="#annotations-header"><i class="icon-chevron-right"></i>Annotations</a></li>
                    <li><a href="#experiment-and-step-header"><i class="icon-chevron-right"></i>Experiments and steps</a></li>
                    <li><a href="#documents-header"><i class="icon-chevron-right"></i>Documents</a>
                        <ul class="nav nav-list">
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

            <div class="span9">
                <h2>Project: ${instance.name}
                    <g:if test="${projectAdapter?.hasProbes()}">
                        <span class="badge badge-info">Probe</span>
                    </g:if>
                    <small>(Project ID: ${instance.id})</small>
                </h2>

                <g:if test="${projectAdapter != null}">
                    <g:saveToCartButton id="${instance.id}"
                                        name="${bardqueryapi.JavaScriptUtility.cleanup(projectAdapter?.name)}"
                                        type="${querycart.QueryItemType.Project}"/>
                </g:if>

                <section id="summary-header">
                    <div class="page-header">
                        <h3 class="sect">Overview</h3>
                    </div>

                    <div class="row-fluid">
                        <g:render template='editSummary' model="['project': instance, canedit: editable, projectOwner: projectOwner]"/>
                    </div>
                </section>

                <g:if test="${projectAdapter?.hasProbes()}">
                    <section id="probe-info">
                        <h3>Probes</h3>
                        <ul class="thumbnails">
                            <g:each var="probe" in="${projectAdapter?.probes}" status="i">
                                <li class="span4">
                                    <div class="thumbnail">
                                        <g:compoundOptions cid="${probe.cid}" sid="${probe.cid}" smiles="${probe?.smiles}"
                                                           imageHeight="200" imageWidth="300"/>
                                        <div class="caption">
                                            <h3>Probe ML#: ${probe.probeId}</h3>
                                            <ul>
                                                <li><a href="${probe.url}">Download probe report from Molecular Library BookShelf</a>
                                                </li>
                                                <li><g:link controller="bardWebInterface" action="showCompound"
                                                            params="[cid: probe.cid]">Show Compound Details in BARD</g:link></li>
                                                <li><a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=${probe.cid}"
                                                       target="_blank">View CID ${probe.cid} in PubChem</a></li>
                                                <li><g:link controller="molSpreadSheet" action="showExperimentDetails"
                                                            params="[cid: probe.cid, pid: projectAdapter.id, transpose: true]"
                                                            data-placement="top"
                                                            class="projectTooltip"
                                                            rel="tooltip"
                                                            data-original-title="">Show Experimental Details</g:link></li>
                                            </ul>
                                        </div>
                                    </div>
                                </li>
                            </g:each>
                        </ul>
                    </section>
                </g:if>

                <br/>

                <section id="annotations-header">
                    <h3 class="sect">Annotations</h3>

                    <div class="row-fluid">
                        <div id="cardHolderAssayComponents" class="span12">
                            <g:render template="/context/currentCard"
                                      model="[contextOwner: instance, currentCard: instance.groupUnclassified(), subTemplate: 'show', renderEmptyGroups: false]"/>
                        </div>
                    </div>

                    <br/>

                    <div class="row-fluid">
                        <g:if test="${!uneditable}">
                            <g:if test="${editable == 'canedit'}">
                                <div class="span12">
                                    <g:link action="editContext" id="${instance?.id}"  params="[groupBySection: ContextType.UNCLASSIFIED.id.encodeAsURL()]"
                                            class="btn"><i class="icon-pencil"></i>Edit Contexts</g:link>
                                </div>
                            </g:if>
                        </g:if>
                    </div>

                </section>

                <br/>

                <g:if test="${projectAdapter?.biology}">
                    <section id="biology-info">
                        <h3>Biology</h3>

                        <g:render template="../bardWebInterface/biology" model="['biology': projectAdapter.biology]"/>
                    </section>
                </g:if>

                <section id="experiment-and-step-header">
                    <h3 class="sect">Experiments and steps</h3>

                    <div class="row-fluid">
                        <g:render template='/project/editstep'
                                  model="['instanceId': instance.id, canedit: editable]"/>
                        <g:render template="showstep"
                                  model="['experiments': instance.projectExperiments,
                                          'pegraph': pexperiment, 'instanceId': instance.id, canedit: editable ]"/>
                    </div>
                </section>

                <br/>

                <g:if test="${experiments}">
                    <section id="experiments-info">
                        <h3>Experiments</h3>

                        <g:displayExperimentsGroupedByAssay assays="${assays}" experiments="${experiments}" experimentTypes="${projectAdapter.experimentTypes}"/>
                    </section>
                </g:if>

                <g:render template="/document/documents"
                          model="[documentKind: DocumentKind.ProjectDocument, owningEntity: instance, canedit: editable]"/>
            </div>
        </div>
    </div>
</g:if>

</body>
</html>