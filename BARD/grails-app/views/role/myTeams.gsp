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

<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="myBard"/>
    <meta name="layout" content="basic"/>
    <title>My Teams</title>
</head>

<body>

<g:if test="${message}">
    <div class="row-fluid">
        <div class="span12">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="margin-top: 20px; padding: 0 .7em;">
                    <p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>
                        <strong>${message}</strong>
                    </p>
                </div>
            </div>
        </div>
    </div>
</g:if>
<div class="container-fluid">
    <div class="row-fluid">
        <g:render template="/layouts/templates/tableSorterTip"/>
        <table class="table table-striped table-hover table-bordered">
            <caption>My Teams</caption>
            <thead>
            <tr>
                <th data-sort="int">ID</th>
                <th data-sort="string-ins">Authority</th>
                <th data-sort="string-ins">Display Name</th>
                <th data-sort="string-ins">Role</th>
                <th data-sort="string-ins">Modified by</th>
                <th data-sort="int">Date Created</th>
                <th data-sort="int">Last Updated</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${teams}" var="teamRole">
                <g:if test="${teamRole.role?.authority?.startsWith("ROLE_TEAM_")}">
                    <tr>
                        <td data-sort-value="${teamRole.role.id}"><g:link controller="role" action="show" id="${teamRole.role.id}">${teamRole.role.id}</g:link> </td>
                        <td>${teamRole.role.authority}</td>
                        <td>${teamRole.role.displayName}</td>

                        <g:if test="${teamRole.teamRole.id.equals("Member")}">
                            <td><span class="label">${teamRole.teamRole.id}</span></td>
                        </g:if>
                        <g:elseif test="${teamRole.teamRole.id.equals("Manager")}">
                            <td><span class="label label-success">${teamRole.teamRole.id}</span></td>
                        </g:elseif>
                        <g:else>
                            <td><span class="label label-info">${teamRole.teamRole.id}</span></td>
                        </g:else>

                        <td><g:renderModifiedByEnsureNoEmail entity="${teamRole}" /></td>
                        <td data-sort-value="${teamRole.role.dateCreated?.time}"><g:formatDate date="${teamRole.role.dateCreated}"
                                                                                      format="MM/dd/yyyy"/></td>
                        <td data-sort-value="${teamRole.role.lastUpdated?.time}"><g:formatDate date="${teamRole.role.lastUpdated}"
                                                                                      format="MM/dd/yyyy"/></td>
                    </tr>
                </g:if>
            </g:each>
            </tbody>
        </table>
        <br/>
    </div>
</div>
</body>
</html>
