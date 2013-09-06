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
            <g:if test="${projectAdapter.description}">
                <section id="description-info" style="text-align: left;">
                    <h4>Description</h4>

                    <g:textBlock>${projectAdapter?.description}</g:textBlock>
                </section>
            </g:if>
            <g:if test="${!BardAnnotation.areAnnotationsEmpty(projectAdapter.annotations)}">
                <section id="annotations-info" style="text-align: left;">
                    <h4>Annotations</h4>

                    <div id="cardView" class="cardView" class="row-fluid">
                        <g:render template="listContexts" model="[contexts: projectAdapter.annotations.contexts]"/>
                    </div>
                </section>
            </g:if>
            <g:if test="${projectAdapter?.biology}">
                <section id="biology-info" style="text-align: left;">
                    <h4>Biology</h4>
                    <g:render template="/bardWebInterface/biology" model="['biology': projectAdapter?.biology]"/>
                </section>
            </g:if>
            <g:if test="${experiments}">
                <section id="experiments-info" style="text-align: left;">
                    <h4>Experiments</h4>
                    <g:displayExperimentsGroupedByAssay assays="${assays}" experiments="${experiments}" experimentTypes="${projectAdapter.experimentTypes}"/>
                </section>
            </g:if>
            <g:if test="${projectAdapter.documents}">
                <g:render template="publications" model="['documents': projectAdapter.documents]"/>
            </g:if>

        </div>
    </div>
</div>
<r:layoutResources/>
</body>
</html>