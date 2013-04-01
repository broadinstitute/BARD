<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 3/30/13
  Time: 12:45 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Data QA Issues</title>
</head>
<body>
<h1>Issues identified during data QA process</h1>

<table border="1" cellpadding="10" cellspacing="1">
    <tr>
    <g:each in="${headers}" var="header">
        <th>${header}</th>
    </g:each>
    </tr>

    <g:each in="${rowList}" var="row">
        <tr>
        <g:each in="${row}" var="entry">
            <td>${entry}</td>
        </g:each>
        </tr>
    </g:each>
</table>
</body>
</html>