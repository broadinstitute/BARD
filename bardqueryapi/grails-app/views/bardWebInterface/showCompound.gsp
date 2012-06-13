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
  <title></title>
</head>
<body>
    <table border="1">
        <tr>
            <th>CID</th>
            <th>SIDs</th>
            <th>Probe ID</th>
            <th>SMILES</th>
        </tr>
        <tr>
            <td>${compoundJson.cid}</td>
            <td>${compoundJson.sids}</td>
            <td>${compoundJson.probeId}</td>
            <td>${compoundJson.smiles}</td>
        </tr>
    </table>
</body>
</html>