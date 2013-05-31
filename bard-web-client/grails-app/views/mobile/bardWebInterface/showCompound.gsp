<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>BioAssay Research Database</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <r:require modules="promiscuity,substances,jqueryMobile,core"/>
    <r:layoutResources/>
</head>

<body>
<div data-role="page" id="showCompound">
<div>
    <g:render template="/layouts/templates/mobileLoginStrip"/>
</div>

<div data-role="content" style="text-align: center; margin: 0 auto;">

<h3>${compound?.name}
    <g:if test="${compound.isDrug()}">
        <span class="badge badge-success">Drug</span>
    </g:if>
    <g:elseif test="${compound.isProbe()}">
        <span class="badge badge-info">Probe</span>
    </g:elseif>
    <small>(PubChem CID: ${compound?.pubChemCID})</small>
</h3>

<g:link controller="bardWebInterface" action="showCompoundBioActivitySummary" id="${compound.pubChemCID}"
        style="float: right;">Bio-activity Summary</g:link>
<div style="text-align: center;">
<table>
<tr>
    <td class="rightAligned"><dfn>Assays - Active vs Tested:</dfn></td>

    <td class="leftAligned">
        <span class="badge badge-info">
            <g:link controller="molSpreadSheet" action="showExperimentDetails"
                    style="text-decoration: underline"
                    params="[cid: compound.pubChemCID, transpose: true]">${compound?.numberOfActiveAssays}</g:link>
            /${compound?.numberOfAssays}</span>
    </td>
</tr>

<g:if test="${compound?.iupacName}">
    <tr>
        <td class="rightAligned"><dfn>IUPAC Name:</dfn></td>
        <td class="leftAligned">${compound?.iupacName}</td>
    </tr>
</g:if>

<g:if test="${compound?.probeId}">
    <tr>
        <td class="rightAligned"><dfn>Probe ID:</dfn></td>
        <td class="leftAligned">${compound.probeId}</td>
    </tr>
    <tr>
        <td colspan="2" class="noMarker">
            <div id="probe"
                 href="${createLink(controller: 'bardWebInterface', action: 'probe', params: [probeId: compound.probeId])}">
            </div>
        </td>
    </tr>
    <g:if test="${compound?.getProbeAnnotations()}">
        <tr>
            <td colspan="2" class="leftAligned noMarker">
                <div class="caption">
                    <ul class="noMarker">
                        <g:if test="${compound?.getProbe()}">
                            <li><a href="${compound.getProbe().getUrl()}">Download probe report from Molecular Library BookShelf</a>
                            </li>
                        </g:if>
                        <g:if test="${compound?.getProbeCid()}">
                            <li><a href="${compound.getProbeCid().getUrl()}"
                                   target="_blank">View Probe by CID (${compound.pubChemCID}) in PubChem</a></li>

                        </g:if>
                        <g:if test="${compound?.getProbeSid()}">
                            <li><a href="${compound.getProbeSid().getUrl()}"
                                   target="_blank">View Probe by SID (${compound.getProbeSid().getDisplay()}) in PubChem</a>
                            </li>

                        </g:if>
                    </ul>
                </div>
            </td>
        </tr>
    </g:if>
</g:if>

<g:if test="${compound?.getSynonyms()}">
    <tr>
        <td class="rightAligned"><dfn>Synonyms:</dfn></td>
        <td class="leftAligned">${compound?.getSynonyms()?.collect {it}?.join(', ')}</td>
    </tr>
</g:if>

<tr>
    <td class="rightAligned"><dfn>SMILES:</dfn></td>
    <td class="leftAligned">${compound?.getStructureSMILES()}</td>
</tr>

<g:if test="${compound?.getRegistryNumbers()}">
    <tr>
        <td class="rightAligned"><dfn>CAS Registry Numbers:</dfn></td>
        <td class="leftAligned">${compound?.getRegistryNumbers()?.collect {it}?.join(', ')}</td>
    </tr>
</g:if>

<g:if test="${compound?.getUniqueIngredientIdentifier()}">
    <tr>
        <td class="rightAligned"><dfn>Unique Ingredient Identifier:</dfn></td>
        <td class="leftAligned">${compound?.getUniqueIngredientIdentifier()}</td>
    </tr>
</g:if>

<g:if test="${compound?.getOtherAnnotationValue('Wikipedia')}">
    <tr>
        <td class="rightAligned"><dfn>Wikipedia Entry:</dfn></td>
        <td class="leftAligned"><a href="${compound?.getOtherAnnotationValue('Wikipedia').get(0)}"
                                   target="_blank">${compound?.getOtherAnnotationValue('Wikipedia').get(0)}</a></td>
    </tr>
</g:if>

<g:if test="${compound.mwt()}">
    <tr>
        <td class="rightAligned"><dfn>Molecular Weight:</dfn></td>
        <td class="leftAligned">${compound.mwt()}</td>
    </tr>
</g:if>
<g:if test="${compound.exactMass()}">
    <tr>
        <td class="rightAligned"><dfn>Exact Mass:</dfn></td>
        <td class="leftAligned">${compound.exactMass()}</td>
    </tr>
</g:if>
<g:if test="${compound.rotatable()}">
    <tr>
        <td class="rightAligned"><dfn>Rotatable Bonds:</dfn></td>
        <td class="leftAligned">${compound.rotatable()}</td>
    </tr>
</g:if>
<g:if test="${compound.hbondAcceptor()}">
    <tr>
        <td class="rightAligned"><dfn>HBond Acceptors:</dfn></td>
        <td class="leftAligned">${compound.hbondAcceptor()}</td>
    </tr>
</g:if>
<g:if test="${compound.hbondDonor()}">
    <tr>
        <td class="rightAligned"><dfn>HBond Donors:</dfn></td>
        <td class="leftAligned">${compound.hbondDonor()}</td>
    </tr>
</g:if>
<g:if test="${compound.logP()}">
    <tr>
        <td class="rightAligned"><dfn>LogP:</dfn></td>
        <td class="leftAligned">${compound.logP()}</td>
    </tr>
</g:if>
<g:if test="${compound.TPSA()}">
    <tr>
        <td class="rightAligned"><dfn>Total Polar Surface Area:</dfn></td>
        <td class="leftAligned">${compound.TPSA()}</td>
    </tr>
</g:if>
<g:if test="${compound.definedStereo()}">
    <tr>
        <td class="rightAligned"><dfn>Defined Stereo:</dfn></td>
        <td class="leftAligned">${compound.definedStereo()}</td>
    </tr>
</g:if>
<g:if test="${compound.stereocenters()}">
    <tr>
        <td class="rightAligned"><dfn>Stereocenters:</dfn></td>
        <td class="leftAligned">${compound.stereocenters()}</td>
    </tr>
</g:if>


%{--<ul class="thumbnails">--}%
%{--<li>--}%
%{--<g:compoundOptions--}%
%{--sid="${compound?.pubChemCID}"--}%
%{--cid="${compound?.pubChemCID}"--}%
%{--smiles="${compound?.structureSMILES}"--}%
%{--name="${bardqueryapi.JavaScriptUtility.cleanup(compound?.name)}"--}%
%{--numActive="${compound?.numberOfActiveAssays}"--}%
%{--numAssays="${compound?.numberOfAssays}"--}%
%{--imageWidth="400"--}%
%{--imageHeight="300"/>--}%
%{--</li>--}%
%{--</ul>--}%

<g:if test="${compound?.structureSMILES}">
    <img alt="${compound.structureSMILES}"
         src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromSmiles', params: [smiles: compound.structureSMILES, width: 400, height: 300])}"/>
</g:if>
<g:else>
    <img alt="SID: ${compound?.pubChemCID}" title="SID: ${compound?.pubChemCID}"
         src="${createLink(controller: 'chemAxon', action: 'generateStructureImageFromCID', params: [cid: compound?.pubChemCID, width: 400, height: 300])}"/>
</g:else>


<tr>
    <td class="rightAligned"><dfn>Scaffold Promiscuity Analysis:</dfn></td>

    <td class="leftAligned">
        <div class="promiscuity noMarker"
             href="${createLink(controller: 'bardWebInterface', action: 'promiscuity', params: [cid: compound.pubChemCID])}"
             id="${compound.pubChemCID}_prom">

        </div>
    </td>
</tr>

<g:if test="${compound?.getMechanismOfAction()}">
    <tr>
        <td class="rightAligned"><dfn>Mechanism of Action:</dfn></td>

        <td class="leftAligned">
            <ul class="noMarker">
                <g:each in="${compound?.getMechanismOfAction()?.collect {it}}" var="moa">
                    <li>${moa}</li>
                </g:each>
            </ul>
        </td>
    </tr>
</g:if>

<g:if test="${compound?.getTherapeuticIndication()}">
    <tr>
        <td class="rightAligned"><dfn>Therapeutic Indication:</dfn></td>

        <td class="leftAligned">
            <ul class="noMarker">
                <g:each in="${compound?.getTherapeuticIndication()?.collect {it}}"
                        var="indication">
                    <li>${indication}</li>
                </g:each>
            </ul>
        </td>
    </tr>
</g:if>

<g:if test="${compound?.getOtherAnnotationValue('CLINICALTRIALS')}">
    <tr>
        <td class="rightAligned"><dfn>Clinical Trials:</dfn></td>

        <td class="leftAligned">
            <ul class="noMarker">
                <g:each in="${compound?.getOtherAnnotationValue('CLINICALTRIALS')?.collect {it}}" var="trial">
                    <li><a href="http://clinicaltrials.gov/ct2/show/${trial}">${trial}</a></li>
                </g:each>
            </ul>
        </td>
    </tr>
</g:if>

<g:if test="${compound?.getPrescriptionDrugLabel()}">
    <tr>
        <td class="rightAligned"><dfn>Prescription Drug Label:</dfn></td>

        <td>
            <ul class="noMarker">
                <g:each in="${compound?.getPrescriptionDrugLabel()?.collect {it}}" var="rxlabel">
                    <li>${rxlabel}</li>
                </g:each>
            </ul>
        </td>
    </tr>
</g:if>

<g:if test="${compound?.getOtherAnnotationValue('TARGETS')}">
    <tr>
        <td class="rightAligned"><dfn>Targets:</dfn></td>

        <td class="leftAligned">
            <ul class="noMarker">
                <g:each in="${compound?.getOtherAnnotationValue('TARGETS')?.collect {it}.sort()}" var="target">
                    <li>${target}</li>
                </g:each>
            </ul>
        </td>
    </tr>
</g:if>

<tr>
    <td colspan="2" class="leftAligned noMarker">
        <div id="sids"
             href='${createLink(controller: "bardWebInterface", action: "findSubstanceIds", id: compound.id)}'>
        </div>
    </td>
</tr>

<g:if test="${compound?.getOtherAnnotationValue('COLLECTION')}">
    <tr>
        <td class="rightAligned"><dfn>Compound Collections:</dfn></td>
        <td class="leftAigned">
            <ul class="noMarker">
                <g:each in="${compound?.getOtherAnnotationValue('COLLECTION')?.collect {it}.sort()}" var="collection">
                    <li>${collection}</li>
                </g:each>
            </ul>
        </td>
    </tr>
</g:if>

<g:if test="${compound?.getOtherAnnotationValue('CompoundSpectra')}">
    <tr>
        <td class="rightAligned"><dfn>Compound Spectra:</dfn></td>

        <td class="leftAligned">
            <img src="${compound?.getOtherAnnotationValue('CompoundSpectra').get(0)}"
                 alt="Compound spectra for ${compound.pubChemCID}">
        </td>
    </tr>
</g:if>

</table>
</div>
</div>
</div>
<r:layoutResources/>
</body>
</html>
