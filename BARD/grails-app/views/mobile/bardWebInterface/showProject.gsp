%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

<%@ page import="bard.core.rest.spring.assays.BardAnnotation; bardqueryapi.JavaScriptUtility; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>BioAssay Research Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <r:require modules="core,bootstrap,card,jqueryMobile"/>
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
