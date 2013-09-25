<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,assayshow"/>
    <meta name="layout" content="basic"/>
    <title>Create Panel</title>

</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <g:form class="form-horizontal" action="save" controller="panel">
            <h3>Create a New Panel</h3>
            <div class="control-group ${hasErrors(bean: panelCommand, field: 'name', 'error')}">
                <label class="control-label" for="name">
                    <g:message code="panel.name.label"/>:</label>

                <div class="controls">
                    <g:textField id="name" name="name" value="${panelCommand?.name}" required=""/>
                    <span class="help-inline"><g:fieldError field="name" bean="panelCommand"/></span>
                </div>
            </div>
            <div class="control-group ${hasErrors(bean: panelCommand, field: 'description', 'error')}">
                <label class="control-label" for="description">
                    <g:message code="panel.description.label"/>:</label>

                <div class="controls">
                    <g:textField id="description" name="description" value="${panelCommand?.description}" required=""/>
                    <span class="help-inline"><g:fieldError field="description" bean="panelCommand"/></span>
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <g:link controller="panel" action="myPanels"
                            class="btn">Cancel</g:link>
                    <input type="submit" class="btn btn-primary" value="Create New Panel">
                </div>
            </div>

        </g:form>
    </div>
</div>
</body>
</html>