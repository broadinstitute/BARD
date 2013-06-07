<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 5/17/13
  Time: 2:46 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Result map problems</title>
</head>
<body>
    <h1>Result map problems</h1>
    <g:each in="${problemAidMap.keySet()}" var="problem">
        <h4>${problem}</h4>
        <table cellpadding="10">
            <g:each in="${problemAidMap.get(problem)}" var="aid">
                <tr>
                    <td>
                        ${aid}
                    </td>
                    <td>
                        <a href="http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid=${aid}">view in PubChem</a>
                    </td>
                    <td>
                        <g:link controller="tidIssue" action="show" params="[aid:aid, problem:problem]">display problem TID's in result map</g:link>
                    </td>
                </tr>
            </g:each>
        </table>
    </g:each>
</body>
</html>