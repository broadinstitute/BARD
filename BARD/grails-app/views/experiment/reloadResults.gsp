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

<g:link controller="experiment" action="show" id="${experiment.id}">Return to experiment</g:link>

<div class="row-fluid">
<g:render template="../results/resultSummary" model="${[summary: results]}" />
</div>

</body>
</html>