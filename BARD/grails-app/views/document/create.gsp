<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap, assayshow"/>
    <meta name="layout" content="basic"/>
    <title>Create Assay Document</title>

</head>
<body>
<g:render template="message"/>
<g:render template="errors" model="['errors': document?.errors?.allErrors]" />
<div class="row-fluid">
    <div class="span12">
        <g:form class="form-horizontal" action="save">
            <input type="hidden" name="assayId" value="${document.assayId}">

            <g:render template="editProperties" model="${[document: document]}"/>

            <div class="control-group">
                <div class="controls">
                    <g:link controller="assayDefinition" action="show" id="${document.assayId}" fragment="documents-header" class="btn">Cancel</g:link>
                    <input type="submit" class="btn btn-primary" value="Create">
                </div>
            </div>

        </g:form>
    </div>
</div>
</body>
</html>