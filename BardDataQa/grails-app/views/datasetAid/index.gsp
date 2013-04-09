<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 4/9/13
  Time: 10:42 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>List of Project UID for Dataset ID=${datasetId}</title>
</head>
<body>
    <h1>List of AID for Dataset ID=${datasetId} </h1>
    <h2>count:  ${aidList.size()}</h2>
    <g:each in="${aidList}" var="aid">
        ${aid}<br/>
    </g:each>
</body>
</html>