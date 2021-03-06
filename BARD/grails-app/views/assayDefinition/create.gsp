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

<%@ page import="bard.db.command.BardCommand; bard.db.people.Role; bard.db.enums.AssayType; bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,descriptorSelect2Widget,createAssay"/>
    <meta name="layout" content="basic"/>
    <title>Create Assay Definition</title>

</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <h3>Create a New Assay Definition</h3>
    </div>

    <div class="row-fluid">
        <div class="span12">
            <g:form class="form-horizontal" action="save" controller="assayDefinition">

                <div class="control-group ${hasErrors(bean: assayCommand, field: 'assayName', 'error')}">
                    <label class="control-label" for="assayName">* <g:message code="assay.assayName.label"/>:</label>

                    <div class="controls">
                        <g:textArea id="assayName" name="assayName" value="${assayCommand?.assayName}" required=""
                                    class="span10"/>
                        <span class="help-inline"><g:fieldError field="assayName" bean="assay"/></span>
                    </div>
                </div>

                <g:hiddenField class="" id="assayFormatId" name="assayFormatId" value="${assayCommand?.assayFormatId}"/>

                <div class="control-group ${hasErrors(bean: assayCommand, field: 'assayFormatValueId', 'error')}">

                    <label class="control-label" for="assayFormatValueId">* <g:message
                            code="assay.assayFormat.label"/>:</label>

                    <div class="controls">
                        <g:hiddenField class="span10" id="assayFormatValueId" name="assayFormatValueId"
                                       value="${assayCommand?.assayFormatValueId}"/>
                        <p class="help-inline"><g:fieldError field="assayFormatValueId" bean="${assayCommand}"/></p>
                    </div>

                </div>


                <div class="control-group ${hasErrors(bean: assayCommand, field: 'ownerRole', 'error')}">
                    <label class="control-label" for="ownerRole">* <g:message code="entity.ownerRole.label"/>:</label>

                    <div class="controls">
                        <g:if test="${bard.db.command.BardCommand.userRoles()}">
                            <g:select name="ownerRole" id="ownerRole" required="required"
                                      from="${BardCommand.userRoles()}"
                                      value="${assayCommand?.ownerRole}"
                                      optionValue="displayName" optionKey="authority"/>
                        </g:if>
                        <g:else>
                            <p>You need to be part of a team to create Assays. Follow this <g:link
                                    controller="assayDefinition" action="teams">link</g:link> to the Teams Page</p>
                        </g:else>
                        <p class="help-inline"><g:fieldError field="ownerRole" bean="${assayCommand}"/></p>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <g:link controller="assayDefinition" action="myAssays"
                                class="btn">Cancel</g:link>
                        <input type="submit" class="btn btn-primary" value="Create New Assay Definition">
                    </div>
                </div>

            </g:form>
        </div>
    </div>
</div>
</body>
</html>
