<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 5/6/13
  Time: 10:57 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${title}</title>
</head>
<body>
<h1>${title}</h1>
<h2>Click the link to view the AID in PubChem</h2>

    <g:each in="${aidList}" var="aid">
        <a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid=${aid}">${aid}</a>
        <br/>
    </g:each>

</body>
</html>