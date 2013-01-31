<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap, assayshow"/>
    <meta name="layout" content="basic"/>
    <title>Edit Assay Document</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <g:form class="form-horizontal">
            <input type="hidden" name="assayId" value="${assayId}">
            <g:render template="editProperties"/>
            <input type="submit" class="btn" value="Create">
        </g:form>
    </div>
</div>
</body>
</html>