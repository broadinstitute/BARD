%{-- Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 --}%

<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="bard.db.enums.Status; bard.db.command.BardCommand; bard.db.people.Role; bard.db.enums.ProjectGroupType; bard.db.enums.Status; bard.db.project.*" %>
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
                    <g:textArea id="name" name="name" value="${projectCommand?.name}" required="" class="input-xxlarge"/>
                    <span class="help-inline"><g:fieldError field="name" bean="projectCommand"/></span>
                </div>
            </div>

            <div class="control-group ${hasErrors(bean:projectCommand, field: 'description', 'error')}">
                <label class="control-label" for="description">*
                <g:message code="project.description.label"/>:</label>

                <div class="controls">
                    <g:textArea id="description" name="description" value="${projectCommand?.description}" required="" class="input-xxlarge"/>
                    <span class="help-inline"><g:fieldError field="description" bean="projectCommand"/></span>
                </div>
            </div>

            <div class="control-group ${hasErrors(bean: projectCommand, field: 'projectStatus', 'error')}">
                <label class="control-label" for="projectStatus">* <g:message
                        code="project.projectStatus.label"/>:</label>

                <div class="controls">
                    <g:select name="projectStatus" id="projectStatus"
                              from="${Status.values()}"
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
                                  optionValue="displayName" optionKey="authority"/>
                    </g:if>
                    <g:else>
                        <p> You need to be part of a team to create Projects. Follow this <g:link controller="assayDefinition" action="teams">link</g:link> to the Teams Page</p>
                    </g:else>



                </div>
            </div>

            <div class="control-group">
                <div class="controls">
                    <g:link controller="project" action="myProjects"
                            class="btn">Cancel</g:link>
                    <input type="submit" class="btn btn-primary" value="Create New Project">
                </div>
            </div>

        </g:form>
    </div>
</div>
</body>
</html>
