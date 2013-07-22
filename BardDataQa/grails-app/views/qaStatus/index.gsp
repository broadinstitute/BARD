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
  <title></title>
</head>
<body>
<table border="1" cellpadding="10" cellspacing="1">
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