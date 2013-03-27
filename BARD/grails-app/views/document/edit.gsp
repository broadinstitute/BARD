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
<g:render template="/common/message"/>
<g:render template="/common/errors" model="['errors': document?.errors?.globalErrors]"/>
<div class="row-fluid">
    <div class="span12">
        <g:form class="form-horizontal" action="update">
            <g:hiddenField name="assayId" value="${document?.assayId}"/>
            <g:hiddenField name="projectId" value="${document?.projectId}"/>

            <g:hiddenField name="documentId" value="${document?.documentId}"/>
            <g:hiddenField name="version" value="${document?.version}"/>

            <g:render template="editProperties" model="${[document: document]}"/>

            <div class="control-group">
                <div class="controls">
                    <g:link controller="${document?.ownerController}" action="show" id="${document?.ownerId}"
                            fragment="document-${document?.documentId}" class="btn">Cancel</g:link>
                    <g:actionSubmit value="Update" action="Update" class="btn btn-primary"/>
                </div>
            </div>

        </g:form>
    </div>
</div>
</body>
</html>