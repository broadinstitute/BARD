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
    <title>Show Compound ${compound.pubChemCID}</title>
</head>

<body>
<div class="row">
    <div class="span6"><h2>View compound showing results</h2></div> <br/>

    <div class="span6"><a href="/bardwebquery"><img src="${resource(dir: 'images', file: 'bardLogo.png')}"
                                                    alt="BARD - Home Page"/></a></div>
</div>
<br/>

<div class="row">
    <div class="span4 offset1"><h1>View Compound</h1></div>
</div>

<div class="row">
    <div class="span4 offset1"><h2>CID  ${compound.pubChemCID}</h2></div> <br/>

    <div class="span2"><img
            src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compound?.structureSMILES, width: 150, height: 120])}"/>
    </div> <br/>

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