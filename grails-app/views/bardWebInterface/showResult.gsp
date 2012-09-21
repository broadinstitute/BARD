<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 9/21/12
  Time: 10:55 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="logoSearchCartAndFooter"/>
    <title>BARD : Experiment Result : EID</title>
</head>

<body>
<table class="table table-condensed">

<td style="min-width: 180px;">
    <img alt="${compoundAdapter.structureSMILES}" title="${compoundAdapter.name}"
         src="${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: compoundAdapter.structureSMILES, width: 180, height: 150])}"/>
</td>
</table>
</body>
</html>