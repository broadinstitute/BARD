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

<g:render template="/table"/>

</body>
</html>