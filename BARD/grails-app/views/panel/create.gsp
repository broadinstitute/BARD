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
