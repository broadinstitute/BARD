<%@ page import="bardqueryapi.JavaScriptUtility" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Compound : ${compound?.name} : PubChem CID ${compound.pubChemCID}</title>
    <r:require modules="promiscuity,activeVrsTested"/>
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
        <a href="#" class="addCompoundToCart btn btn-mini" data-cart-name="${JavaScriptUtility.cleanup(compound?.name)}"
           data-cart-id="${compound?.pubChemCID}" data-cart-smiles="${compound?.getStructureSMILES()}">
            Save for later analysis
        </a>
    </div>
</div>
<div class="row-fluid">
    <div class="span12">
        <dl class="dl-horizontal dl-horizontal-wide">
            <dt>Assays - Active vrs Tested:</dt>
            <dd>
                <div class="activeVrsTested" href="${createLink(controller: 'bardWebInterface', action: 'activeVrsTested', params: [cid: compound.pubChemCID])}" id="${compound.pubChemCID}_tested"></div>
            </dd>
            <g:if test="${compound?.compound?.getValue(bard.core.Compound.IUPACNameValue)}">
                <dt>IUPAC Name:</dt>
                <dd>${compound?.compound?.getValue(bard.core.Compound.IUPACNameValue)?.value}</dd>
            </g:if>
            <g:if test="${compound?.compound?.getValue(bard.core.Compound.ProbeIDValue)}">
                <dt>Probe ID:</dt>
                <dd>${compound?.compound?.getValue(bard.core.Compound.ProbeIDValue)?.value}</dd>
            </g:if>
            <g:if test="${compound?.compound?.getValue('Synonyms')}">
                <dt>Synonyms:</dt>
                <dd>${compound?.compound?.getValues('Synonyms')?.collect {it.value}?.join(', ')}</dd>
            </g:if>
            <dt>SMILES:</dt>
            <dd>${compound?.getStructureSMILES()}</dd>
            <dt>Formula:</dt>
            <dd>${compound?.formula()}</dd>
            <g:if test="${compound?.compound?.getValue(bard.core.Compound.CASValue)}">
                <dt>CAS Registry Numbers:</dt>
                <dd>${compound?.compound?.getValues(bard.core.CompoundValues.CASValue)?.collect {it.value}?.join(', ')}</dd>
            </g:if>
            <g:if test="${compound?.compound?.getValue(bard.core.Compound.UNIIValue)}">
                <dt>Unique Ingredient Identifier:</dt>
                <dd>${compound?.compound?.getValue(bard.core.CompoundValues.UNIIValue)?.value}</dd>
            </g:if>
            <g:if test="${compound?.compound?.getValue('Wikipedia')}">
                <dt>Wikipedia Entry:</dt>
                <dd><a href="${compound?.compound?.getValue('Wikipedia')?.value}" target="_blank">${compound?.compound?.getValue('Wikipedia')?.value}</a></dd>
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
            <dt>Is 'rule of 5' compliant:</dt>
            <dd>${compound.ruleOf5()?'Yes':'No'}</dd>
        </dl>
    </div>
    <div class="span5">
        <ul class="thumbnails">
            <li>
                <g:link class="thumbnail" controller="chemAxon" action="generateStructureImageFromSmiles" params="[smiles: compound?.structureSMILES, width: 600, height: 600]" target="_blank">
                    <img alt="${compound?.structureSMILES}" title="${compound.name}"
                         src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: compound?.structureSMILES, width: 400, height: 300])}"/>
                </g:link>
            </li>
        </ul>
    </div>
    <div class="span4">
        <h4>Scaffold Promiscuity Analysis</h4>
        <div class="promiscuity" href="${createLink(controller: 'bardWebInterface', action: 'promiscuity', params: [cid: compound.pubChemCID])}" id="${compound.pubChemCID}_prom"></div>
    </div>
</div>

<g:if test="${compound?.compound?.getValue('CompoundMOA')}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Mechanism of Action</h3>
            <ul class="unstyled">
                <g:each in="${compound?.compound?.getValues('CompoundMOA')?.collect {it.value}}" var="moa">
                    <li>${moa}</li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>

<g:if test="${compound?.compound?.getValue('CompoundIndication')}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Therapeutic Indication</h3>
            <ul class="unstyled">
                <g:each in="${compound?.compound?.getValues('CompoundIndication')?.collect {it.value}}" var="indication">
                    <li>${indication}</li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>

<g:if test="${compound?.compound?.getValue('CLINICALTRIALS')}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Clinical Trials</h3>
            <ul class="horizontal-block-list">
                <g:each in="${compound?.compound?.getValues('CLINICALTRIALS')?.collect {it.value}}" var="trial">
                    <li><a href="http://clinicaltrials.gov/ct2/show/${trial}">${trial}</a></li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>

<g:if test="${compound?.compound?.getValue('CompoundDrugLabelRx')}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Prescription Drug Label</h3>
            <ul class="unstyled">
                <g:each in="${compound?.compound?.getValues('CompoundDrugLabelRx')?.collect {it.value}}" var="rxlabel">
                    <li>${rxlabel}</li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>

<g:if test="${compound?.compound?.getValue('TARGETS')}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Targets</h3>
            <ul class="unstyled">
                <g:each in="${compound?.compound?.getValues('TARGETS')?.collect {it.value}.sort()}" var="target">
                    <li>${target}</li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>

<div class="row-fluid">
    <div class="span12">
        <h3>PubChem Substances</h3>
        <ul class="horizontal-block-list">
            <g:each in="${compound.pubChemSIDs.sort()}" var="pubChemSID">
                <li><a href="http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=${pubChemSID}">${pubChemSID}</a></li>
            </g:each>
        </ul>
    </div>
</div>

<g:if test="${compound?.compound?.getValue('COLLECTION')}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Compound Collections</h3>
            <ul class="unstyled">
                <g:each in="${compound?.compound?.getValues('COLLECTION')?.collect {it.value}.sort()}" var="collection">
                    <li>${collection}</li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>

<g:if test="${compound?.compound?.getValue('CompoundSpectra')}">
    <div class="row-fluid">
        <div class="span12">
            <h3>Compound Spectra</h3>
            <img src="${compound?.compound?.getValue('CompoundSpectra')?.value}" alt="Compound spectra for ${compound.pubChemCID}">
        </div>
    </div>
</g:if>
<r:require modules="promiscuity,activeVrsTested"/>
</body>
</html>