<%@ page import="bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Project : ID ${projectAdapter?.project?.id}</title>
    <r:script>
        $(document).ready(function () {
            $("#accordion").accordion({ autoHeight:false });
            $('.projectTooltip').tooltip();
        })
    </r:script>

</head>

<body>
<div class="row-fluid">
    <div class="span12 page-header">
        <h1>Project: ${projectAdapter?.name} <small>(Project ID: ${projectAdapter?.project?.id})</small></h1>
        <g:saveToCartButton id="${projectAdapter?.project?.id}"
                            name="${JavaScriptUtility.cleanup(projectAdapter?.project?.name)}"
                            type="${querycart.QueryItemType.Project}"/>
    </div>
</div>

<div class="row-fluid">
    <div class="span4">
        <dl class="dl-horizontal dl-horizontal-wide">
            <g:if test="${projectAdapter?.project?.getValue('grant number')}">
                <dt>Grant Number:</dt>
                <dd>${projectAdapter.project.getValue('grant number').value}</dd>
            </g:if>
            <g:if test="${projectAdapter?.project?.getValue('laboratory name')}">
                <dt>Laboratory Name:</dt>
                <dd>${projectAdapter.project.getValue('laboratory name').value}</dd>
            </g:if>
        </dl>
    </div>

    <div class="span8">
        <ul class="thumbnails">
            <g:each var="probe" in="${projectAdapter?.probes}" status="i">
                <li class="span4">
                    <div class="thumbnail">
                        <g:link controller="bardWebInterface" action="showCompound" params="[cid: probe.cid]">
                            <img alt="${probe?.smiles}" title="${probe.probeId}"
                                 src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: probe.smiles, width: 300, height: 200])}"/>
                        </g:link>
                        <div class="caption">
                            <h3>Probe ML#: ${probe.probeId}</h3>
                            <ul>
                                    <li><a href="${probe.url}">Download probe report from Molecular Library BookShelf</a></li>
                                    <li><g:link controller="bardWebInterface" action="showCompound"
                                                params="[cid: probe.cid]">Show Compound Details in BARD</g:link></li>
                                    <li><a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=${probe.cid}" target="_blank">View CID ${probe.cid} in PubChem</a></li>
                                    <li><g:link controller="molSpreadSheet" action="showExperimentDetails"
                                                params="[cid: probe.cid, pid: projectAdapter.project.id]" data-placement="top"
                                                class="projectTooltip"
                                                rel="tooltip"
                                                data-original-title="Please note: Query Cart would be reset!">Show Experimental Details</g:link></li>
                            </ul>
                        </div>
                    </div>
                </li>
            </g:each>
        </ul>
    </div>
</div>


<r:script>
    $(document).ready(function () {
        $('.collapse').on('show', function () {
            var icon = $(this).siblings().find("i.icon-chevron-right");
            icon.removeClass('icon-chevron-right').addClass('icon-chevron-down');
        });
        $('.collapse').on('hide', function () {
            var icon = $(this).siblings().find("i.icon-chevron-down");
            icon.removeClass('icon-chevron-down').addClass('icon-chevron-right');
        });
    })
</r:script>
<div class="row-fluid">

    <div class="span12 accordion">

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#annotations-header" id="annotations-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#annotations-info"><i
                        class="icon-chevron-right"></i> Annotations (${projectAdapter.annotations.size()})</a>

                <div id="annotations-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <dl>
                            <g:each in="${projectAdapter?.annotations}" var="annotation">
                                <dt>${annotation.id}</dt>
                                <dd>${annotation.value}</dd>
                            </g:each>
                        </dl>
                    </div>
                </div>
            </div>
        </div>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#document-header" id="document-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#document-info"><i class="icon-chevron-down"></i> Description</a>

                <div id="document-info" class="accordion-body in collapse">
                    <div class="accordion-inner">
                        <g:textBlock>${projectAdapter.project.description}</g:textBlock>
                    </div>
                </div>
            </div>
        </div>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#experiments-header" id="experiments-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#experiments-info"><i
                        class="icon-chevron-right"></i> Experiments (${projectAdapter.numberOfExperiments})</a>

                <div id="experiments-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="experiments" model="['experiments': experiments, showAssaySummary: false]"/>
                    </div>
                </div>
            </div>
        </div>

        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#document-header" id="assays-header" class="accordion-toggle" data-toggle="collapse"
                   data-target="#assays-info"><i class="icon-chevron-right"></i> Assays (${assays?.size()})</a>

                <div id="assays-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:each var="assay" in="${assays}" status="i">
                            <div>
                                <p>
                                    <g:link controller="bardWebInterface" action="showAssay" id="${assay.id}"
                                            params='[searchString: "${searchString}"]'>${assay.name}</g:link>
                                </p>
                            </div>
                        </g:each>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>
</body>
</html>