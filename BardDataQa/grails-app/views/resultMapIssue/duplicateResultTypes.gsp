<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 3/30/13
  Time: 1:37 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Result Map Issues</title>
</head>
<body>
<h1>AID's with duplicate result-types in result map</h1>
<h2>(causing problems with result loading)  count:  ${rowList.size()}</h2>
<table border="1" cellpadding="10" cellspacing="1">
    <tr>
        <g:each in="${headers}" var="header">
            <th>${header}</th>
        </g:each>
    </tr>

    <g:each in="${rowList}" var="row">
        <tr>
            <g:each status="i" in="${row}" var="entry">
                <td>
                    <g:if test="${i == 0}">
                        <g:link controller="tidIssue" action="duplicateResultTypes" id="${entry}">${entry}</g:link>
                    </g:if>
                    <g:else>
                        ${entry}
                    </g:else>
                </td>
            </g:each>
        </tr>
    </g:each>
</table>
</body>
</html>