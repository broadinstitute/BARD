<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 7/22/13
  Time: 4:10 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>List of possible QA statuses</title>
</head>
<body>
<h1>List of possible QA statuses</h1>
<table border="1" cellpadding="10" cellspacing="1">
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>version in database</th>
    </tr>
    <g:each in="${qaStatusList}" var="qaStatus">
        <tr>
            <td>${qaStatus.id}</td>
            <td>${qaStatus.name}</td>
            <td>${qaStatus.version}</td>
        </tr>
    </g:each>
</table>
</body>
</html>