<%@ page import="bard.core.rest.spring.assays.BardAnnotation; bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>BioAssay Research Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <r:require modules="core,bootstrap,cardDisplayCSS,jqueryMobile"/>
    <r:layoutResources/>
</head>

<body>
<div data-role="page" id="showProject">
    <div>
        <g:render template="/layouts/templates/mobileLoginStrip"/>
    </div>

    <div data-role="header" style="text-align: center; margin: 0 auto;">
        <p>Project: ${projectAdapter?.name}
            <g:if test="${projectAdapter.hasProbes()}">
                <span class="badge badge-info">Probe</span>
            </g:if>
            <small>(Project ID: ${projectAdapter?.capProjectId})</small>
        </p>
    </div>

    <div data-role="content" style="text-align: center; margin: 0 auto;">
        <div>
            <dl>
                <g:if test="${projectAdapter?.getNumberOfExperiments()}">
                    <dt style="float: left;">Number Of Experiments:</dt>
                    <dd style="text-align: left; padding-left: 10px; margin-left: 10px;">

                        <span class="badge badge-info">
                            <a href="#experiments-info"
                               style="color: white; text-decoration: underline">
                                ${projectAdapter.getNumberOfExperiments()}
                            </a>
                        </span></dd>
                </g:if>
            </dl>
        </div>

        <div>
            <ul class="thumbnails">
                <g:each var="probe" in="${projectAdapter?.probes}" status="i">
                    <li>
                        <div class="thumbnail">
                            <img alt="${compound.structureSMILES}"
                                 src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: probe?.smiles, width: 300, height: 200])}"/>

                            <div class="caption">
                                <h4>Probe ML#: ${probe.probeId}</h4>
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
        </div>

        <div>
            <g:if test="${BardAnnotation.areAnnotationsEmpty(projectAdapter.annotations)}">
                <section id="annotations-info" style="text-align: left;">
                    <div>
                        <h4>Annotations</h4>
                    </div>

                    <div id="cardView" class="cardView" class="row-fluid">
                        <g:render template="listContexts" model="[annotations: projectAdapter.annotations]"/>
                    </div>
                </section>
            </g:if>
            <g:if test="${projectAdapter.description}">
                <section id="description-info" style="text-align: left;">
                    <div>
                        <h4>Description</h4>
                    </div>

                    <div>
                        <g:textBlock>${projectAdapter?.description}</g:textBlock>
                    </div>
                </section>
            </g:if>
            <g:if test="${projectAdapter.targets}">
                <div style="text-align: left;">
                    <g:render template="/bardWebInterface/targets" model="['targets': projectAdapter.targets]"/>
                </div>
            </g:if>
            <g:if test="${projectAdapter.documents}">
                <g:render template="publications" model="['documents': projectAdapter.documents]"/>
            </g:if>

            <g:if test="${experiments}">
                <section id="experiments-info" style="text-align: left;">
                    <div class="page-header">
                        <h4>Experiments</h4>
                    </div>

                    <div>
                        <g:render template="experiments"
                                  model="['experiments': experiments, showAssaySummary: false, experimentTypes: projectAdapter.experimentTypes]"/>
                    </div>
                </section>
            </g:if>

            <section id="assays-info" style="text-align: left;">
                <div>
                    <h4>Assays</h4>
                </div>

                <div>
                    <g:each var="assay" in="${assays}" status="i">
                        <div>
                            <p>
                                <g:if test="${searchString}">
                                    <g:link controller="bardWebInterface" action="showAssay" id="${assay.id}"
                                            params='[searchString: "${searchString}"]'>${assay.title}
                                        <g:if test="${assay.assayStatus == 'Witnessed'}">
                                            <img src="${resource(dir: 'images', file: 'witnessed.png')}"
                                                 alt="Witnessed" title="Witnessed"/>
                                        </g:if>
                                        <g:if test="${assay.assayStatus == 'Measures Done' || assay.assayStatus == 'Annotations Done'}">
                                            <img src="${resource(dir: 'images', file: 'measures_annotations_done.png')}"
                                                 alt="Measures or Annotations Done"
                                                 title="Measures or Annotations Done"/>
                                        </g:if>
                                        <g:if test="${assay.assayStatus == 'Draft' || assay.assayStatus == 'Retired'}">
                                            <img src="${resource(dir: 'images', file: 'draft_retired.png')}"
                                                 alt="Draft or Retired" title="Draft or Retired"/>
                                        </g:if>
                                    </g:link>
                                </g:if>
                                <g:else>
                                    <g:link controller="bardWebInterface" action="showAssay"
                                            id="${assay.id}">${assay.title}
                                        <g:if test="${assay.assayStatus == 'Witnessed'}">
                                            <img src="${resource(dir: 'images', file: 'witnessed.png')}"
                                                 alt="Witnessed" title="Witnessed"/>
                                        </g:if>
                                        <g:if test="${assay.assayStatus == 'Measures Done' || assay.assayStatus == 'Annotations Done'}">
                                            <img src="${resource(dir: 'images', file: 'measures_annotations_done.png')}"
                                                 alt="Measures or Annotations Done"
                                                 title="Measures or Annotations Done"/>
                                        </g:if>
                                        <g:if test="${assay.assayStatus == 'Draft' || assay.assayStatus == 'Retired'}">
                                            <img src="${resource(dir: 'images', file: 'draft_retired.png')}"
                                                 alt="Draft or Retired" title="Draft or Retired"/>
                                        </g:if>
                                    </g:link>
                                </g:else>
                            </p>
                        </div>
                    </g:each>
                </div>
            </section>
        </div>
    </div>
</div>
<r:layoutResources/>
</body>
</html>