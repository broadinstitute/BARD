<%@ page import="bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Results uploaded</title>
</head>
<body>

<div class="row-fluid">
    <g:render template="resultSummary" model="${[summary: summary]}"/>
    <g:link action="show" controller="experiment" id="${experiment.id}">Return to Experiment's Details</g:link>
</div>

</body>
</html>