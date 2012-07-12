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
    <meta name="layout" content="main">
    <title><g:message code="compound.show.label" args="[compoundId]" default="Show Compound ${compoundId}"/></title>
</head>

<body>
<div class="nav" role="navigation">
    <ul>
        <li><g:link controller="bardWebInterface" action="index"><g:message
                code="default.home.label"/></g:link>
        </li>
     </ul>
</div>

<div>
    <table>
        <tr>
            <th>CID</th>
            <th>SIDs</th>
            <th>Probe ID</th>
            <th>SMILES</th>
        </tr>
        <g:if test="${compoundJson}">
            <tr>
                <td>${compoundJson?.cid}</td>
                <td>${compoundJson?.sids?.join(', ')}</td>
                <td>${compoundJson?.probeId}</td>
                <td>${compoundJson?.smiles}</td>
            </tr>
        </g:if>
    </table>
</div>
</body>
</html>