<%@ page import="bard.db.command.BardCommand; bard.db.people.Role; bard.db.registration.*" %>
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

            <div style="color: #b94a48;">
                <g:hasErrors>
                    <g:renderErrors bean="${panelCommand}"/>
                </g:hasErrors>
            </div>

            <div class="control-group ${hasErrors(bean: panelCommand, field: 'name', 'error')}">
                <label class="control-label" for="name">
                    <g:message code="panel.name.label"/>:</label>

                <div class="controls">
                    <g:textArea id="name" name="name" value="${panelCommand?.name}" required="" class="input-xxlarge"/>
                    <span class="help-inline"><g:fieldError field="name" bean="panelCommand"/></span>
                </div>
            </div>

            <div class="control-group ${hasErrors(bean: panelCommand, field: 'description', 'error')}">
                <label class="control-label" for="description">
                    <g:message code="panel.description.label"/>:</label>

                <div class="controls">
                    <g:textArea id="description" name="description" value="${panelCommand?.description}" required=""
                                class="input-xxlarge"/>
                    <span class="help-inline"><g:fieldError field="description" bean="panelCommand"/></span>
                </div>
            </div>

            <div class="control-group ${hasErrors(bean: panelCommand, field: 'ownerRole', 'error')}">
                <label class="control-label" for="ownerRole">* <g:message code="entity.ownerRole.label"/>:</label>

                <div class="controls">

                    <g:if test="${bard.db.command.BardCommand.userRoles()}">

                        <g:select name="ownerRole" id="ownerRole" required="required"
                                  from="${BardCommand.userRoles()}"
                                  value="${panelCommand?.ownerRole}"
                                  optionValue="displayName" optionKey="authority"/>
                    </g:if>
                    <g:else>
                        <p>You need to be part of a team to create Panels. Follow this <g:link
                                controller="assayDefinition" action="teams">link</g:link> to the Teams Page</p>
                    </g:else>

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