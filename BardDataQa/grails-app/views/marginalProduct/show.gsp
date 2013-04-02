<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 4/2/13
  Time: 2:27 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Marginal Product for Dataset ${dataset.name}</title>
</head>
<body>
<h1>Marginal Product for Dataset ${dataset.name}</h1>
<h2>${dataset.description}</h2>

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