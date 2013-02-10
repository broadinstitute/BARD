<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">
    <title>CAP - Assay Templates</title>
    <r:script>
</r:script>

</head>
<body>

<div class="row-fluid">
    <div class="span12">
        <div class="hero-unit-v1">
            <h4>Templates</h4>
        </div>
    </div>
</div>


<div class="row-fluid">
    <div class="span12">
        <ul>
            <g:each in="${templates}" var="template">
            <li><g:link action="show" id="${template.id}" controller="assayDefinition">${template.assayName}</g:link></li>
            </g:each>
        </ul>
    </div>
</div>


</body>
</html>