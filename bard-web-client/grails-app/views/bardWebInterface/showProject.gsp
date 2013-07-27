<%@ page import="bard.core.rest.spring.assays.BardAnnotation; bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Project : ID ${projectAdapter?.capProjectId}</title>
    <r:require modules="showProjectAssay,compoundOptions,twitterBootstrapAffix,projectstep"/>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3 bs-docs-sidebar">
            <ul class="nav nav-list bs-docs-sidenav twitterBootstrapAffixNavBar" data-spy="affix">

                <g:if test="${projectAdapter.hasProbes()}">
                    <li><a href="#probe-info"><i class="icon-chevron-right"></i>Probes</a></li>
                </g:if>
                <g:if test="${!BardAnnotation.areAnnotationsEmpty(projectAdapter.annotations)}">
                    <li><a href="#annotations-info"><i
                            class="icon-chevron-right"></i>Annotations</a></li>
                </g:if>
                <g:if test="${projectAdapter.biology}">
                    <li><a href="#biology-info"><i
                            class="icon-chevron-right"></i>Biology (${projectAdapter.biology.size()})</a></li>
                </g:if>
                <g:if test="${projectAdapter.description}">
                    <li><a href="#description-info"><i class="icon-chevron-right"></i>Description</a></li>
                </g:if>
                <g:if test="${projectAdapter.documents}">
                    <li><a href="#publication-info"><i
                            class="icon-chevron-right"></i>Publications (${projectAdapter.documents.size()})</a></li>
                </g:if>
                <g:if test="${pegraph}">
                    <li><a href="#projectSteps-info"><i
                            class="icon-chevron-right"></i>Project Steps</a></li>
                </g:if>
                <g:if test="${experiments}">
                    <li><a href="#experiments-info"><i
                            class="icon-chevron-right"></i> Experiments (${projectAdapter?.getNumberOfExperiments()})
                    </a>
                    </li>
                </g:if>
            </ul>
        </div>

        <div class="span9">

            <h2>Project: ${projectAdapter?.name}
                <g:if test="${projectAdapter.hasProbes()}">
                    <span class="badge badge-info">Probe</span>
                </g:if>
                <small>(Project ID: ${projectAdapter?.capProjectId})</small></h2>
            <g:saveToCartButton id="${projectAdapter?.id}"
                                name="${JavaScriptUtility.cleanup(projectAdapter?.name)}"
                                type="${querycart.QueryItemType.Project}"/>
            <a class="btn btn-mini" href="${grailsApplication.config.bard.cap.project}${projectAdapter?.capProjectId}"
               title="Click To View Project In CAP" rel="tooltip">View in CAP</a>

            <g:if test="${projectAdapter.hasProbes()}">
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

            <g:if test="${!projectAdapter.annotations.contexts.isEmpty()}">
                <section id="annotations-info">
                    <h3>Annotations</h3>

                    <div id="cardView" class="cardView" class="row-fluid">
                        <g:render template="listContexts" model="[contexts: projectAdapter.annotations.contexts]"/>
                    </div>
                </section>
            </g:if>
            <g:if test="${projectAdapter.biology}">
                <section id="biology-info">
                    <h3>Biology</h3>

                    <g:render template="biology" model="['biology': projectAdapter.biology]"/>
                </section>
            </g:if>

            <g:if test="${projectAdapter.description}">
                <section id="description-info">
                    <h3>Description</h3>

                    <div>
                        <g:textBlock>${projectAdapter?.description}</g:textBlock>
                    </div>
                </section>
            </g:if>
            <g:if test="${projectAdapter.documents}">
                <g:render template="publications" model="['documents': projectAdapter.documents]"/>
            </g:if>
            <g:if test="${pegraph}">
                <section id="projectSteps-info">
                    <h3>Project Steps</h3>

                    <div>
                        <g:render template="/projects/showstep"
                                  model="['pegraph': pegraph, 'projectId': projectAdapter.capProjectId]"/>
                    </div>
                </section>
            </g:if>
            <g:if test="${experiments}">
                <section id="experiments-info">
                    <h3>Experiments</h3>

                    <g:displayExperimentsGroupedByAssay assays="${assays}" experiments="${experiments}" experimentTypes="${projectAdapter.experimentTypes}"/>
                </section>
            </g:if>

        </div>
    </div>
</div>
</body>
</html>