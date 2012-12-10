<%@ page import="bardqueryapi.JavaScriptUtility" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Compound : ${compound?.name} : PubChem CID ${compound.pubChemCID}</title>
    <r:require modules="promiscuity"/>
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
            </g:elseif>
            <small>(PubChem CID: ${compound?.pubChemCID})</small></h1>
        <g:saveToCartButton id="${compound.pubChemCID}"
                            name="${JavaScriptUtility.cleanup(compound.name)}"
                            type="${querycart.QueryItemType.Compound}"
                            smiles="${compound.getStructureSMILES()}"/>
    </div>
</div>

<div class="row-fluid">
    <div class="span12">
        <dl class="dl-horizontal dl-horizontal-wide">
            <dt>Assays - Active vs Tested:</dt>
            <dd>
                <div class="activeVrsTested">
                    <div>
                        <span class="badge badge-info">${compound?.numberOfActiveAssays} / ${compound?.numberOfAssays}</span>
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
            </g:if>
             <g:if test="${compound?.getSynonyms()}">
                <dt>Synonyms:</dt>
                <dd>${compound?.getSynonyms()?.collect {it}?.join(', ')}</dd>
            </g:if>
            <dt>SMILES:</dt>
            <dd>${compound?.getStructureSMILES()}</dd>
            %{-- TODO <dt>Formula:</dt>--}%
            %{--<dd>${compound?.formula()}</dd>--}%
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
            <dt>Molecular Weight:</dt>
            <dd>${compound.mwt()}</dd>
            <dt>Exact Mass:</dt>
            <dd>${compound.exactMass()}</dd>
            <dt>Rotatable Bonds:</dt>
            <dd>${compound.rotatable()}</dd>
            <dt>HBond Acceptors:</dt>
            <dd>${compound.hbondAcceptor()}</dd>
            <dt>HBond Donors:</dt>
            <dd>${compound.hbondDonor()}</dd>
            <dt>LogP:</dt>
            <dd>${compound.logP()}</dd>
            <dt>Total Polar Surface Area:</dt>
            <dd>${compound.TPSA()}</dd>
            <dt>Defined Stereo:</dt>
            <dd>${compound.definedStereo()}</dd>
            <dt>Stereocenters:</dt>
            <dd>${compound.stereocenters()}</dd>
        </dl>
    </div>

    <div class="span5">
        <ul class="thumbnails">
            <li>
                <g:link class="thumbnail" controller="chemAxon" action="generateStructureImageFromSmiles"
                        params="[smiles: compound?.structureSMILES, width: 600, height: 600]" target="_blank">
                    <img alt="${compound?.structureSMILES}" title="${compound.name}"
                         src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: compound?.structureSMILES, width: 400, height: 300])}"/>
                </g:link>
            </li>
        </ul>
    </div>

    <div class="span4">
        <h4>Scaffold Promiscuity Analysis</h4>

        <div class="promiscuity"
             href="${createLink(controller: 'bardWebInterface', action: 'promiscuity', params: [cid: compound.pubChemCID])}"
             id="${compound.pubChemCID}_prom"></div>
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

%{--<div class="row-fluid">--}%
    %{--<div class="span12">--}%
        %{--<h3>PubChem Substances</h3>--}%
        %{--<ul class="horizontal-block-list">--}%
            %{--<g:each in="${compound.pubChemSIDs.sort()}" var="pubChemSID">--}%
                %{--<li><a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=${pubChemSID}">${pubChemSID}</a>--}%
                %{--</li>--}%
            %{--</g:each>--}%
        %{--</ul>--}%
    %{--</div>--}%
%{--</div>--}%

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
                 alt="Compound spectra for ${compound.pubChemCID}">
        </div>
    </div>
</g:if>
<r:require modules="promiscuity"/>
</body>
</html>
