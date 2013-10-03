<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="bard.db.command.BardCommand; bard.db.people.Role; bard.db.enums.ProjectGroupType; bard.db.enums.ProjectStatus; bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,xeditable,createProject"/>
    <meta name="layout" content="basic"/>
    <title>Create New Project</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div class="well well-small">
            <div>
                <h4>Create New Project</h4>
                <h6>*  Means Field is Required</h6>
            </div>
        </div>
    </div>
</div>

<g:if test="${flash.message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${flash.message}</strong>
                </div>
            </div>
        </div>
    </div>
</g:if>
<div class="row-fluid">
    <div class="span12">
        <g:form class="form-horizontal" action="save" controller="project">

            <div class="control-group ${hasErrors(bean: projectCommand, field: 'name', 'error')}">
                <label class="control-label" for="name">*
                <g:message code="project.name.label"/>:</label>

                <div class="controls">
                    <g:textField id="name" name="name" value="${projectCommand?.name}"
                                 required=""/>
                    <span class="help-inline"><g:fieldError field="name" bean="projectCommand"/></span>
                </div>
            </div>

            <div class="control-group ${hasErrors(bean:projectCommand, field: 'description', 'error')}">
                <label class="control-label" for="description">*
                <g:message code="project.description.label"/>:</label>

                <div class="controls">
                    <g:textField id="description" name="description" value="${projectCommand?.description}"
                                 required=""/>
                    <span class="help-inline"><g:fieldError field="description" bean="projectCommand"/></span>
                </div>
            </div>

            <div class="control-group ${hasErrors(bean: projectCommand, field: 'projectStatus', 'error')}">
                <label class="control-label" for="projectStatus">* <g:message
                        code="project.projectStatus.label"/>:</label>

                <div class="controls">
                    <g:select name="projectStatus" id="projectStatus"
                              from="${ProjectStatus.values()}"
                              value="${projectCommand?.projectStatus}"
                              optionValue="id"/>

                </div>
            </div>
            <div class="control-group ${hasErrors(bean: projectCommand, field: 'projectGroupType', 'error')}">
                <label class="control-label" for="projectGroupType">* <g:message
                        code="project.groupType.label"/>:</label>

                <div class="controls">
                    <g:select name="projectGroupType" id="projectGroupType"
                              from="${ProjectGroupType.values()}"
                              value="${projectCommand?.projectGroupType}"
                              optionValue="id"/>

                </div>
            </div>
            <div class="control-group ${hasErrors(bean: projectCommand, field: 'ownerRole', 'error')}">
                <label class="control-label" for="ownerRole">* <g:message code="entity.ownerRole.label"/>:</label>

                <div class="controls">

                    <g:if test="${bard.db.command.BardCommand.userRoles()}">

                        <g:select name="ownerRole" id="ownerRole"  required="required"
                                  from="${BardCommand.userRoles()}"
                                  value="${projectCommand?.ownerRole}"
                                  optionValue="displayName" optionKey="id"/>
                    </g:if>
                    <g:else>
                        <p> You need to be part of a team to create Projects. Follow this <g:link controller="assayDefinition" action="teams">link</g:link> to the Teams Page</p>
                    </g:else>



                </div>
            </div>

            <div class="control-group">
                <div class="controls">
                    <g:link controller="project" action="groupProjects"
                            class="btn">Cancel</g:link>
                    <input type="submit" class="btn btn-primary" value="Create New Project">
                </div>
            </div>

        </g:form>
    </div>
</div>
</body>
</html>