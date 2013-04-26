<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 4/25/13
  Time: 1:00 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title></title>
</head>
<body>
<g:each in="${list}" var="elementAndPath">
    ${elementAndPath.toString()}
    <br/>
</g:each>
</body>
</html>