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
    <r:require modules="core,bootstrap,grailspagination"/>
    <meta name="layout" content="basic"/>
    <title>People</title>
</head>

<body>
<sec:ifAnyGranted roles="ROLE_BARD_ADMINISTRATOR">
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
            <div class="span12">
                <h3>Add User to Person Table</h3>
                <g:form action="save" controller="person" class="form-inline">
                    <input type="submit" value="Add user" class="btn btn-primary">
                    <input type="text" placeholder="Username" name="username" value="${personCommand.username}">
                    <input type="text" placeholder="Full name" name="displayName" value="${personCommand.displayName}">
                    <input type="email" placeholder="Email address" name="email" value="${personCommand.email}">

                    Roles:
                    <g:select id="roles" size="${Role.list().size()}"
                              name="roles" from="${Role.list()}"
                              multiple="multiple"
                              optionKey="id"
                              optionValue="displayName"
                              value="${personCommand?.roles}"/>

                </g:form>

                <h3>Existing users</h3>
                <table class="table">
                    <thead>
                    <tr>

                        <g:sortableColumn property="fullName" title="Full Name"/>

                        <g:sortableColumn property="userName"
                                          title="User Name"/>


                        <g:sortableColumn property="emailAddress"
                                          title="Email"/>


                        <th>Roles</th></tr>
                    </thead>
                    <tbody>
                    <g:each in="${people}" var="person">
                        <tr>
                            <td><g:link action="edit" id="${person.id}">${person.fullName}</g:link></td>
                            <td><g:link action="edit" id="${person.id}">${person.userName}</g:link></td>
                            <td>${person.emailAddress}</td>
                            <td>${person.rolesAsList}</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>

                <div class="pagination">
                    <g:paginate total="${peopleTotal}"/>
                </div>
            </div>
        </div>
    </div>
</sec:ifAnyGranted>
</body>

</html>
