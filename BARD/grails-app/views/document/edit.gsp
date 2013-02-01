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
        <g:form class="form-horizontal" action="update" >
            <input type="hidden" value="${document.id}" name="id">

            <g:render template="editProperties" model="${[document:document]}" />

            <div class="control-group">
                <div class="controls">
                    <g:link controller="assayDefinition" action="edit" id="${document.assay.id}" class="btn">Cancel</g:link>
                    <input type="submit" class="btn btn-primary" value="Update">
                </div>
            </div>

        </g:form>
    </div>
</div>
</body>
</html>