<%@ page import="bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <r:external file="css/bootstrap-plus.css"/>
    <title>Show Experiment</title>
</head>

<body>

<g:if test="${status.summary == null}">
    <p>
       ${status.status}
    </p>
    <p>
        This page will refresh every 10 seconds until the job completes
    </p>
    <meta http-equiv="refresh" content="10">
</g:if>
<g:else>
    <g:link controller="experiment" action="show" id="${experimentId}">Return to experiment</g:link>

    <div class="row-fluid">
        <g:render template="../results/resultSummary" model="${[summary: status.summary]}" />
    </div>

</g:else>

</body>
</html>