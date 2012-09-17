<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 6/12/12
  Time: 3:30 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="bardqueryapi.JavaScriptUtility" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Compound Details : PubChem CID ${compound.pubChemCID}</title>
</head>
<body>
<div class="row-fluid">
    <div class="span12 page-header">
        <h1>Compound: ${compound?.compound?.preferredName} <small>(PubChem CID: ${compound?.pubChemCID})</small></h1>
        <a href="#" class="addCompoundToCart btn btn-mini" data-cart-name="${JavaScriptUtility.cleanup(compound?.compound?.preferredName)}"
           data-cart-id="${compound?.pubChemCID}" data-cart-smiles="${compound?.getStructureSMILES()}">
            Save for later analysis
        </a>
    </div>
</div>

<div class="row-fluid">
    <div class="span6 offset3">
        <img class="addtocart" alt="${compound?.structureSMILES}" title="${compound.name}"
             src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compound?.structureSMILES, width: 300, height: 200])}"/>
    </div>
</div>

<div class="row-fluid">
    <div class="span9">
        <table class="table">
            <tr>
                <th>Compound Property</th>
                <th>Value</th>
            </tr>

            <tr>
                <td>Name</td>
                <td>${compound.name}</td>
            </tr>

            <tr>
                <td>Description</td>
                <td>${compound?.compound?.description}</td>
            </tr>

            <tr>
                <td>Synonyms</td>
                <td></td>
            </tr>

            <tr>
                <td>Molecular formula</td>
                <td>${compound.formula()}</td>
            </tr>

            <tr>
                <td>IUPAC name</td>
                <td>${compound?.compound?.getValue(bard.core.Compound.IUPACNameValue)?.value as java.lang.String}</td>
            </tr>

            <tr>
                <td>Canonical SIMLES</td>
                <td>${compound.structureSMILES}</td>
            </tr>

            <tr>
                <td>MW</td>
                <td>${compound.mwt()}</td>
            </tr>

            <tr>
                <td>Exact mass</td>
                <td>${compound.exactMass()}</td>
            </tr>

            <tr>
                <td>logP</td>
                <td>${compound.logP()}</td>
            </tr>

            <tr>
                <td>TPSA</td>
                <td>${compound.TPSA()}</td>
            </tr>

            <tr>
                <td>Probe ID</td>
                <% String probeId = compound?.compound?.getValue(bard.core.Compound.ProbeIDValue)?.value as java.lang.String %>
                <td>${probeId == 'null' ? '' : probeId}</td>
            </tr>

        </table>
    </div>

    <div class="span3">
        <table class="table">
            <tr>
                <th>SIDs</th>
                <th>Source</th>
            </tr>
            <g:each in="${compound?.pubChemSIDs}" var="sid">
                <tr>
                    <td>${sid}</td>
                    <td></td>
                </tr>
            </g:each>
        </table>
    </div>
</div>
</body>
</html>