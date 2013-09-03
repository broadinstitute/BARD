<%--
  Created by IntelliJ IDEA.
  User: pmontgom
  Date: 8/30/13
  Time: 10:26 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title></title>
</head>
<body>

<ul>
<g:each in="${scriptNames}" var="scriptName">
    <li><g:link action="show" params="${[scriptName: scriptName]}">${scriptName}</g:link></li>
</g:each>
</ul>

</body>
</html>