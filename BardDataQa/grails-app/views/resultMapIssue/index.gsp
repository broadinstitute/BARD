<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 4/27/13
  Time: 7:30 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Summary of issues with result type mapping</title>
</head>
<body>
<h1>Summary of issues with result type mapping</h1>
<g:each in="${issueCountCommandList}" var="issueCountCommand">
    <g:link controller="${issueCountCommand.controller}" action="${issueCountCommand.action}">${issueCountCommand.description} (${issueCountCommand.count})</g:link>
    <br/>
    <br/>
</g:each>
</body>
</html>