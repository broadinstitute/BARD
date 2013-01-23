<%@ page import="bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Project : ID ${projectAdapter?.id}</title>
    <r:require modules="showProjectAssay,compoundOptions,twitterBootstrapAffix"/>
</head>

<body>
<div class="row-fluid">
    <div class="span12 page-header">

        <h1>Project: ${projectAdapter?.name}
            <g:if test="${projectAdapter.hasProbes()}">
                <span class="badge badge-info">Probe</span>
            </g:if>
            <small>(Project ID: ${projectAdapter?.id})</small></h1>
        <g:saveToCartButton id="${projectAdapter?.id}"
                            name="${JavaScriptUtility.cleanup(projectAdapter?.name)}"
                            type="${querycart.QueryItemType.Project}"/>
    </div>
</div>

<div class="row-fluid">
    <div class="span4">
        <dl class="dl-horizontal dl-horizontal-wide">
            <g:if test="${projectAdapter?.getNumberOfExperiments()}">
                <dt>Number Of Experiments:</dt>
                <dd><span class="badge badge-info">${projectAdapter.getNumberOfExperiments()}</span></dd>
            </g:if>
        </dl>
    </div>

    <div class="span8">
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
    </div>
</div>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3 bs-docs-sidebar">
            <ul class="nav nav-list bs-docs-sidenav twitterBootstrapAffixNavBar" data-spy="affix">
                <li><a href="#annotations-info"><i
                        class="icon-chevron-right"></i>Annotations</a></li>
                <li><a href="#annotations-info-misc"><i
                        class="icon-chevron-right"></i>Annotations - Miscellaneous</a></li>
                <li><a href="#description-info"><i class="icon-chevron-right"></i>Description</a></li>

                <g:if test="${projectAdapter.targets}">
                    <li><a href="#target-info"><i class="icon-chevron-right"></i>Targets (${projectAdapter.targets.size()})</a></li>
                </g:if>
                <g:if test="${projectAdapter.documents}">
                    <li><a href="#publication-info"><i class="icon-chevron-right"></i>Publications (${projectAdapter.documents.size()})</a></li>
                </g:if>
                <li><a href="#experiments-info"><i
                        class="icon-chevron-right"></i> Experiments (${projectAdapter?.getNumberOfExperiments()})</a>
                </li>
                <li><a href="#assays-info"><i class="icon-chevron-right"></i> Assays (${assays?.size()})</a></li>
            </ul>
        </div>

        <div class="span9">
            <section id="annotations-info">
                <div class="page-header">
                    <h3>Annotations</h3>
                </div>

                <div id="cardView" class="cardView" class="row-fluid">
                    <g:render template="listContexts" model="[annotations: projectAdapter.annotations]"/>
                </div>
            </section>
            <section id="annotations-info-misc">
                <div class="page-header">
                    <h3>Annotations - Miscellaneous</h3>
                </div>

                <div id="cardViewMisc" class="cardView" class="row-fluid">
                    <g:render template="listMiscellaneous" model="[annotations: projectAdapter.annotations]"/>
                </div>

            </section>

            <section id="description-info">
                <div class="page-header">
                    <h3>Description</h3>
                </div>

                <div>
                    <g:textBlock>${projectAdapter?.description}</g:textBlock>
                </div>
            </section>

            <g:if test="${projectAdapter.targets}">
                <g:render template="targets" model="['targets': projectAdapter.targets]"/>
            </g:if>
            <g:if test="${projectAdapter.documents}">
                <g:render template="publications" model="['documents': projectAdapter.documents]"/>
            </g:if>
            <section id="experiments-info">
                <div class="page-header">
                    <h3>Experiments</h3>
                </div>

                <div>
                    <g:render template="experiments" model="['experiments': experiments, showAssaySummary: false]"/>
                </div>
            </section>
            <section id="assays-info">
                <div class="page-header">
                    <h3>Assays </h3>
                </div>

                <div>
                    <g:each var="assay" in="${assays}" status="i">
                        <div>
                            <p>
                                <g:link controller="bardWebInterface" action="showAssay" id="${assay.id}"
                                        params='[searchString: "${searchString}"]'>${assay.name}</g:link>
                            </p>
                        </div>
                    </g:each>
                </div>
            </section>
        </div>
    </div>
</div>
</body>
</html>