<%@ page import="bard.db.registration.*" %>
<%@ page import="bard.db.model.IDocumentType" %>
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
        <g:form>
            <input type="hidden" id="${document.id}">
            <g:render template="editProperties"/>
            <input type="submit" class="btn">
        </g:form>
    </div>
</div>
</body>
</html>