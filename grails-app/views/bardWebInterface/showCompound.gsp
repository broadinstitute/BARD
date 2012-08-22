<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 6/12/12
  Time: 3:30 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <r:require modules="bootstrap"/>
    <title>Compound Details ${compound.pubChemCID}</title>
</head>

<body>
<div class="row">
    <div class="span6"><a href="/bardwebquery"><img src="${resource(dir: 'images', file: 'bardLogo.png')}"
                                                    alt="BARD - Home Page"/></a></div>

    <div class="span6"><h1>Compound Details</h1></div> <br/>

</div>
<br/>

<div class="span6"><h2>CID ${compound.pubChemCID}</h2></div> <br/>
<br/>
<br/>

<div class="row">
    <div class="span3"><img
            src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compound?.structureSMILES, width: 150, height: 120])}"/>
    </div> <br/>

    <div class="span6 table">
        <table class="span5">
            <tr>
                <th>Compound Property</th>
                <th>Value</th>
            </tr>

            <tr>
                <td>Name</td>
                <td>${compound.compound.name}</td>
            </tr>

            <tr>
                <td>Description</td>
                <td>${compound.compound.description}</td>
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
                <td>IUAP name</td>
                <td>${compound.compound.getValue(bard.core.Compound.IUPACNameValue)?.value as java.lang.String}</td>
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
                <% String probeId = compound.compound.getValue(bard.core.Compound.ProbeIDValue)?.value as java.lang.String %>
                <td>${probeId == 'null' ? '' : probeId}</td>
            </tr>

        </table>
    </div>

    <div class="span6">
        <table>
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
<r:layoutResources/>
<r:require modules="bootstrap"/>
</body>
</html>