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

<%@ page import="bard.db.people.Role; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Add Team</title>
</head>

<body>
<sec:ifAnyGranted roles="ROLE_BARD_ADMINISTRATOR">

    <div class="row-fluid">
        <div class="span3"></div>
        <div class="span9">
            <div class="well well-small">
                <div>
                    <h4>Create New Team</h4>
                    <h6>*  Means Field is Required</h6>
                </div>
            </div>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span3"></div>
        <div class="span9">
            <g:hasErrors bean="${roleInstance}">
                <ul class="errors" role="alert">
                    <g:eachError bean="${roleInstance}" var="error">
                        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                error="${error}"/></li>
                    </g:eachError>
                </ul>
            </g:hasErrors>
            <div>The  Sytem will Append the String 'ROLE_TEAM_' (if you do not specify it) to the team name you specify below.
            It will also remove all spaces and then convert it to uppercase.</div>

            <div>For example, Team : 'Broad X' will be represented in the database as ROLE_TEAM_BROADX<br/></div>

            <div>For display name use the full name of the institution. For example 'Broad Institute of Havard and MIT'</div>
            <br/>
            <br/>
            <g:form class="form-horizontal" action="save" controller="role">

                <div class="fieldcontain ${hasErrors(bean: roleInstance, field: 'authority', 'error')} required">
                    <label for="authority">
                        <g:message code="role.authority.label" default="Name"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <g:textField name="authority" maxlength="${bard.db.people.Role.AUTHORITY_NAME_SIZE}" required=""
                                 value="${roleInstance?.authority}"/>
                </div>

                <div class="fieldcontain ${hasErrors(bean: roleInstance, field: 'displayName', 'error')} ">
                    <label for="displayName">
                        <g:message code="role.displayName.label" default="Display Name"/>
                        <span class="required-indicator">*</span>
                    </label>
                    <g:textField name="displayName" maxlength="${bard.db.people.Role.DISPLAY_NAME_SIZE}"
                                 value="${roleInstance?.displayName}" required=""/>
                </div>
                 <br/>
                <div class="control-group">
                    <div class="controls">
                        <g:link controller="role" action="index"
                                class="btn">Cancel</g:link>
                        <input type="submit" class="btn btn-primary" value="Create New Team">
                    </div>
                </div>

            </g:form>
        </div>
    </div>

</sec:ifAnyGranted>
</body>
</html>
