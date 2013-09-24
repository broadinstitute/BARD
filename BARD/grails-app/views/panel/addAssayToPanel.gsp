<%@ page import="bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Add Assay To Panel</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <g:form class="form-horizontal" action="addAssays" controller="panel">

                <g:hiddenField name="assayIds" value="${associatePanelCommand?.assayIds}"/>
                <div class="control-group">
                    <label>
                        <h3>Associate Assay ADID: ${associatePanelCommand?.assays?.get(0)?.id} - ${associatePanelCommand?.assays?.get(0)?.assayName}</h3>
                    </label>
                </div>
                <div class="control-group ${hasErrors(bean: associatePanelCommand, field: 'id', 'error')}">
                    <label class="control-label" for="id">
                        Panel ID:</label>

                    <div class="controls">
                        <g:textField id="panelId" name="id" value="${associatePanelCommand?.id}" required=""/>
                        <span class="help-inline"><g:fieldError field="id" bean="associatePanelCommand"/></span>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <g:link controller="panel" action="myPanels"
                                class="btn">Cancel</g:link>
                        <input type="submit" class="btn btn-primary" value="Add To Panel">
                    </div>
                </div>

            </g:form>
        </div>
    </div>
</div>

</body>
</html>