<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 4/29/13
  Time: 4:31 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>AID's that need MAAS for Project UID = ${projectUid}</title>
</head>
<body>
<h1>AID's that need MAAS for Project UID = ${projectUid}</h1>
<h2>Click the link to view the AID in PubChem</h2>

<g:each in="${aidList}" var="aid">
    <a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid=${aid}">${aid}</a>
    <br/>
</g:each>

</body>
</html>