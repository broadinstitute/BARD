<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 4/9/13
  Time: 10:15 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>List of Project UID for Dataset ID=${datasetId}</title>
</head>
<body>
    <h1>List of Project UID for Dataset ID=${datasetId}</h1>
    <h2>count:  ${projectUidList.size()}</h2>
    <g:each in="${projectUidList}" var="projectUid">
        ${projectUid}<br/>
    </g:each>
</body>
</html>