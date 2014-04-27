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
<h2>Click the AID link to view in PubChem</h2>

<table cellpadding="10">
    <g:each in="${aidList}" var="aid">
        <tr>
            <td>
                <a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid=${aid}">${aid}</a>
            </td>
            <g:if test="${extraController && aid}">
                <td>
                    <g:link controller="${extraController}" action="${extraAction}" params="[aid:aid]">${extraDescription}</g:link>
                </td>
            </g:if>
        </tr>
    </g:each>
</table>

</body>
</html>