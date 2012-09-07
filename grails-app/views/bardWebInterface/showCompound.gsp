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
    <meta name="layout" content="details"/>
    <title>BARD : Compound Details : PubChem CID ${compound.pubChemCID}</title>
</head>
<body>
<div class="row-fluid">
    <div class="span3">
        <a style="float: left;" href="${createLink(controller:'BardWebInterface',action:'index')}"><img src="${resource(dir: 'images', file: 'bardLogo.png')}" alt="BioAssay Research Database" /></a>
    </div>

    <div class="span7">
        <div><h1>Compound Details for PubChem CID ${compound.pubChemCID}</h1></div>
        <div class="QcartAppearance">
            <img class="addtocart" alt="${compound?.structureSMILES}" title="${compound.name}" src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compound?.structureSMILES, width: 200, height: 200])}"/>
            <a href="/bardwebquery/sarCart/add/1"  class="addtocart"
               onclick="jQuery.ajax({  type:'POST',
                   data:{'id': '${compound.name}','class': 'class bardqueryapi.CartCompound','smiles':'${JavaScriptUtility.cleanup(compound.name)}','cid':'${compound.pubChemCID}','version': '0', 'stt':trackStatus},
                   url:'/bardwebquery/sarCart/add',
                   success:function(data,textStatus){
                       jQuery(ajaxLocation).html(data);
                   }
               });
               return false;"
               action="add"
               controller="sarCart"><i class="icon-shopping-cart"></i><span class="addtocartfont">&nbsp;Add to Cart</span>
            </a>
        </div>

    </div>

    <div class="span2">
        <div class="well">
            <g:render template="queryCartIndicator"/>
            <div class="row-fluid">
                <h5><nobr><a class="trigger" href="#">View details/edit</a></nobr></h5>
            </div>
        </div>
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
                <td>IUPAC name</td>
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