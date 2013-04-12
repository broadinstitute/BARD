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
    <h2>total count:  ${datasetAids.calculateTotalCount()}</h2>

    <h3>Not Summary AID:</h3>
    <g:each in="${datasetAids.notSummaryAidList}" var="notSummaryAid">
        ${notSummaryAid}<br/>
    </g:each>

    <h3>Summary AID:</h3>
    <g:each in="${datasetAids.isSummaryAidList}" var="summaryAid">
        ${summaryAid}<br/>
    </g:each>

    <h3>Unknown whether it is Summary AID or not:</h3>
    <g:each in="${datasetAids.unknownSummaryAidList}" var="unknownAid">
        ${unknownAid}<br/>
    </g:each>
</body>
</html>