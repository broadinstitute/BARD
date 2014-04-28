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

<%@ page import="bardqueryapi.JavaScriptUtility" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="basic"/>
    <title>CID ${compound.id}: ${compound?.name}</title>
    <r:require modules="promiscuity,substances,compoundOptions"/>
</head>

<body>
<div class="row-fluid">
    <div class="span12 page-header">
        <h1>${compound?.name}
            <g:if test="${compound.isDrug()}">
                <span class="badge badge-success">Drug</span>
            </g:if>
            <g:elseif test="${compound.isProbe()}">
                <span class="badge badge-info">Probe</span>
            </g:elseif></h1>
        <g:saveToCartButton id="${compound.id}"
                            name="${JavaScriptUtility.cleanup(compound.name)}"
                            type="${querycart.QueryItemType.Compound}"
                            smiles="${compound.smiles}"/>
        <span style="padding-left: 30px; padding-right: 10px; float: right;"><g:link controller="bardWebInterface" action="bigSunburst" id="${compound.id}"
        >Linked Hierarchy Visualization</g:link></span>
        <span style="padding-left: 30px; padding-right: 30px; float: right;"><g:link controller="bardWebInterface" action="showCompoundBioActivitySummary" id="${compound.id}"
                >Bio-activity Summary</g:link></span>

    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <dl class="dl-horizontal dl-horizontal-wide">
            <dt>Assays - Active vs Tested:</dt>
            <dd>
                <div class="activeVrsTested">
                    <div>
                        <span class="badge badge-info">
                            <g:link controller="molSpreadSheet" action="showExperimentDetails"
                                    style="color: white; text-decoration: underline"
                                    params="[cid: compound.pubChemCID, transpose: true]">${compound?.numberOfActiveAssays}</g:link>
                            /${compound?.numberOfAssays}</span>
                    </div>

                </div>
            </dd>
            <g:if test="${compound?.iupacName}">
                <dt>IUPAC Name:</dt>
                <dd>${compound?.iupacName}</dd>
            </g:if>
            <g:if test="${compound?.probeId}">
                <dt>Probe ID:</dt>
                <dd>${compound.probeId}</dd>
                <div id="probe"
                     href="${createLink(controller: 'bardWebInterface', action: 'probe', params: [probeId: compound.probeId])}">
                </div>
                <g:if test="${compound?.getProbeAnnotations() && compound?.getProbe()}">
                    <dt>Probe Report:</dt>
                    <dd><a href="${compound.getProbe().getUrl()}" target="_blank">Download from Molecular Libraries Bookshelf</a></dd>
                </g:if>
            </g:if>

            <g:if test="${compound?.getSynonyms()}">
                <dt>Synonyms:</dt>
                <dd>${compound?.getSynonyms()?.collect {it}?.join(', ')}</dd>
            </g:if>
            <dt>SMILES:</dt>
            <dd>${compound?.getSmiles()}</dd>
            <dt>PubChem CID:</dt>
            <dd><a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=${compound?.id}" target="_blank">
                <img src="${resource(dir: 'images', file: 'pubchem.png')}" alt="PubChem"/>
                ${compound?.id}</a></dd>
            <g:if test="${compound?.getRegistryNumbers()}">
                <dt>CAS Registry Numbers:</dt>
                <dd>${compound?.getRegistryNumbers()?.collect {it}?.join(', ')}</dd>
            </g:if>
            <g:if test="${compound?.getUniqueIngredientIdentifier()}">
                <dt>Unique Ingredient Identifier:</dt>
                <dd>${compound?.getUniqueIngredientIdentifier()}</dd>
            </g:if>
            <g:if test="${compound?.getOtherAnnotationValue('Wikipedia')}">
                <dt>Wikipedia Entry:</dt>
                <dd><a href="${compound?.getOtherAnnotationValue('Wikipedia').get(0)}"
                       target="_blank">${compound?.getOtherAnnotationValue('Wikipedia').get(0)}</a></dd>
            </g:if>
        </dl>
    </div>
</div>

<div class="row-fluid">
    <div class="span3">
        <dl class="dl-horizontal dl-horizontal-wide">
            <g:if test="${compound.mwt()}">
                <dt>Molecular Weight:</dt>
                <dd>${compound.mwt()}</dd>
            </g:if>
            <g:if test="${compound.exactMass()}">
                <dt>Exact Mass:</dt>
                <dd>${compound.exactMass()}</dd>
            </g:if>
            <g:if test="${compound.rotatable()}">
                <dt>Rotatable Bonds:</dt>
                <dd>${compound.rotatable()}</dd>
            </g:if>
            <g:if test="${compound.hbondAcceptor()}">
                <dt>HBond Acceptors:</dt>
                <dd>${compound.hbondAcceptor()}</dd>
            </g:if>
            <g:if test="${compound.hbondDonor()}">
                <dt>HBond Donors:</dt>
                <dd>${compound.hbondDonor()}</dd>
            </g:if>
            <g:if test="${compound.logP()}">
                <dt>LogP:</dt>
                <dd>${compound.logP()}</dd>
            </g:if>
            <g:if test="${compound.TPSA()}">
                <dt>Total Polar Surface Area:</dt>
                <dd>${compound.TPSA()}</dd>
            </g:if>
            <g:if test="${compound.definedStereo()}">
                <dt>Defined Stereo:</dt>
                <dd>${compound.definedStereo()}</dd>
            </g:if>
            <g:if test="${compound.stereocenters()}">
                <dt>Stereocenters:</dt>
                <dd>${compound.stereocenters()}</dd>
            </g:if>
        </dl>
    </div>

    <div class="span5">
        <ul class="thumbnails">
            <li>
                <g:compoundOptions
                        sid="${compound?.id}"
                        cid="${compound?.id}"
                        smiles="${compound?.smiles}"
                        name="${bardqueryapi.JavaScriptUtility.cleanup(compound?.name)}"
                        imageWidth="400"
                        imageHeight="300"/>
            </li>
        </ul>
    </div>

    <div class="span4">
        <h4>Scaffold Promiscuity Analysis</h4>

        <div class="promiscuity"
             href="${createLink(controller: 'bardWebInterface', action: 'promiscuity', params: [cid: compound.id])}"
             id="${compound.id}_prom"></div>
    </div>
</div>

<g:if test="${compound?.getMechanismOfAction()}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Mechanism of Action</h3>
            <ul class="unstyled">
                <g:each in="${compound?.getMechanismOfAction()?.collect {it}}" var="moa">
                    <li>${moa}</li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>

<g:if test="${compound?.getTherapeuticIndication()}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Therapeutic Indication</h3>
            <ul class="unstyled">
                <g:each in="${compound?.getTherapeuticIndication()?.collect {it}}"
                        var="indication">
                    <li>${indication}</li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>

<g:if test="${compound?.getOtherAnnotationValue('CLINICALTRIALS')}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Clinical Trials</h3>
            <ul class="horizontal-block-list">
                <g:each in="${compound?.getOtherAnnotationValue('CLINICALTRIALS')?.collect {it}}" var="trial">
                    <li><a href="http://clinicaltrials.gov/ct2/show/${trial}">${trial}</a></li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>

<g:if test="${compound?.getPrescriptionDrugLabel()}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Prescription Drug Label</h3>
            <ul class="unstyled">
                <g:each in="${compound?.getPrescriptionDrugLabel()?.collect {it}}" var="rxlabel">
                    <li>${rxlabel}</li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>

<g:if test="${compound?.getOtherAnnotationValue('TARGETS')}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Targets</h3>
            <ul class="unstyled">
                <g:each in="${compound?.getOtherAnnotationValue('TARGETS')?.collect {it}.sort()}" var="target">
                    <li>${target}</li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>
<div class="row-fluid" id="sids"
     href='${createLink(controller: "bardWebInterface", action: "findSubstanceIds", id: compound.id)}'>

</div>


<g:if test="${compound?.getOtherAnnotationValue('COLLECTION')}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Compound Collections</h3>
            <ul class="unstyled">
                <g:each in="${compound?.getOtherAnnotationValue('COLLECTION')?.collect {it}.sort()}" var="collection">
                    <li>${collection}</li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>

<g:if test="${compound?.getOtherAnnotationValue('CompoundSpectra')}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Compound Spectra</h3>
            <img src="${compound?.getOtherAnnotationValue('CompoundSpectra').get(0)}"
                 alt="Compound spectra for ${compound.id}">
        </div>
    </div>
</g:if>
<r:require modules="promiscuity,substances"/>
</body>
</html>
