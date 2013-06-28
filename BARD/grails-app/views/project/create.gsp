<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="bard.db.enums.ProjectStatus; bard.db.project.*" %>
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

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span9">
            <div id="msg"></div>
            <dl class="dl-horizontal">
                <dt>* <g:message code="project.projectStatus.label" default="Status"/>:</dt>
                <dd>
                    <span class="projectForm"
                          id="statusId"  data-value="${projectCommand.status}" data-type="select" data-source="/BARD/project/projectStatus" data-name="status"
                          data-original-title="Select Project Status"></span>
                </dd>

                %{--<dt>* <g:message code="project.groupType.label" default="Fix i18n"/>:</dt>--}%
                %{--<dd>--}%
                    %{--<span class="projectForm"--}%
                          %{--id="groupTypeId"--}%
                          %{--data-value="${projectCommand.groupType}" data-type="select" data-source="/BARD/project/groupType" data-name="groupType"--}%
                          %{--data-original-title="Select Project Group Type"></span>--}%
                %{--</dd>--}%

                <dt>* <g:message code="project.name.label" default="Fix i18n"/>:</dt>
                <dd>
                    <span  class="projectForm" id="nameId"
                          data-type="text"
                          data-name="name" data-placeholder="Required"
                          data-original-title="Enter Project Name">${projectCommand.name}</span>
                </dd>
                <dt>* <g:message code="project.description.label" default="Fix i18n"/>:</dt>
                <dd>
                    <span class="projectForm"
                          id="descriptionId"
                          data-type="text" data-placeholder="Required" data-name="description"
                          data-original-title="Enter Description">${projectCommand.description}</span>
                </dd>
            </dl>

        </div>

        <div class="span9">
            <g:link controller="project" action="findById"  class="btn">Cancel</g:link>
            <button id="save-btn" class="btn btn-primary">Create New Project</button>

        </div>
    </div>
</div>

</body>
</html>