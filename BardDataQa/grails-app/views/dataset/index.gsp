<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 4/2/13
  Time: 9:35 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Datasets</title>
</head>
<body>
<h1>Datasets</h1>
<table border="1" cellpadding="10" cellspacing="1">
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Description</th>
        <th>Marginal Product</th>
    </tr>
    <g:each in="${datasetList}" var="dataset">
        <tr>
            <td>${dataset.id}</td>
            <td>${dataset.name}</td>
            <td>${dataset.description}</td>
            <td><g:link controller="marginalProduct" action="show" params="[datasetId: dataset.id]">show marginal product</g:link> </td>
        </tr>
    </g:each>
</table>
</body>
</html>