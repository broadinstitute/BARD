<%@ page import="bard.db.people.Role" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="basic"/>
    <title>Team : ${roleInstance?.displayName}</title>
    <r:require
            modules="core,bootstrap,xeditable,editRole"/>
</head>

<body>
<r:script disposition='head'>
    function submitTeamRoleForm(teamRole){
        var teamRoleForm = document.getElementById("modifyTeamRoles");
        var checkboxes = teamRoleForm.elements["checkboxes"];
        if(checkboxes != null){
            var checkedBoxes = 0;
            for (var i = 0; i < checkboxes.length; i++){
                if(checkboxes[i].checked)
                    checkedBoxes++;
            }
            if(checkedBoxes > 0){
                teamRoleForm.elements["teamRole"].value = teamRole;
                teamRoleForm.submit();
            }
            else{
                alert('No member has been selected. Please select one or more team members to be able to set role')
            }
        }
        else{
            alert('Please add a team member to be able to set role')
        }
    }
</r:script>
<div class="container-fluid">
    <g:hiddenField name="version" id="versionId" value="${roleInstance?.version}"/>
    <div class="row-fluid">
        <div class="span3"></div>
        <div class="span9">
            <dl class="dl-horizontal">
                <dt><g:message code="role.authority.label" default="Name"/>:</dt>
                <dd>
                    <span
                            class="role"
                            data-toggle="manual"
                            id="authorityId"
                            data-inputclass="input-xxlarge"
                            data-type="textarea"
                            data-value="${roleInstance?.authority}"
                            data-pk="${roleInstance?.id}"
                            data-url="${request.contextPath}/role/editAuthority"
                            data-original-title="Edit Name">${roleInstance?.authority}</span>
                    <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit name"
                       data-id="authorityId"></a>
                </dd>

                <dt><g:message code="role.displayName.label" default="Display Name"/>:</dt>
                <dd>
                    <span
                            class="role"
                            data-toggle="manual"
                            id="displayNameId"
                            data-inputclass="input-xxlarge"
                            data-type="textarea"
                            data-value="${roleInstance?.displayName}"
                            data-pk="${roleInstance?.id}"
                            data-url="${request.contextPath}/role/editDisplayName"
                            data-original-title="Edit Display Name">${roleInstance?.displayName}</span>
                    <a href="#" class="icon-pencil documentPencil ${editable}" title="Click to edit display name"
                       data-id="displayNameId"></a>
                </dd>
                <dt><g:message code="default.dateCreated.label"/>:</dt>
                <dd><g:formatDate date="${roleInstance?.dateCreated}" format="MM/dd/yyyy"/></dd>
                <dt><g:message code="default.lastUpdated.label"/>:</dt>
                <dd id="lastUpdatedId"><g:formatDate date="${roleInstance?.lastUpdated}" format="MM/dd/yyyy"/></dd>
                <dt><g:message code="default.modifiedBy.label"/>:</dt>
                <dd id="modifiedById"><g:fieldValue bean="${roleInstance}" field="modifiedBy"/></dd>
            </dl>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span12">
            <g:if test="${flash.success}">
                <div class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button><strong>${flash.success}</strong></div>
            </g:if>
            <g:if test="${flash.error}">
                <div class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button><strong>${flash.error}</strong></div>
            </g:if>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span12">
        %{--<g:render template="/layouts/templates/tableSorterTip"/>--}%
        <table class="table table-striped table-hover table-bordered">
            <caption><strong>Team Members</strong></caption>

            <thead>
            <tr>
            <th colspan="4">
            <div class="row-fluid">
                <div class="span3">
                    <g:if test="${isTeamManager}">
                    <div class="btn-group">
                        <button class="btn">Actions</button>
                        <button class="btn dropdown-toggle" data-toggle="dropdown">
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu">
                            <li class="dropdown-submenu"><a tabindex="-1" href="#">Add to role</a>
                                <ul class="dropdown-menu">
                                    <li><a onclick="submitTeamRoleForm('Member');">Member</a>
                                    <li><a onclick="submitTeamRoleForm('Manager');">Manager</a>
                                    %{--<li><g:link action="modifyTeamRoles" id="Member" params="[roleId: roleInstance?.id]">Member</g:link></li>--}%
                                    %{--<li><g:link action="modifyTeamRoles" id="Manager" params="[roleId: roleInstance?.id]">Manager</g:link></li>--}%
                                    <li></li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                    </g:if>
                </div>
                <div class="span9">
                    <g:form id="addUserToTeam" name="addUserToTeam" action="addUserToTeam" controller="role">
                        <div class="input-append">
                            <g:hiddenField class="" id="roleId" name="roleId" value="${roleInstance?.id}" />
                            <g:textField name="email" value="" placeholder="Email address" required="required"/>
                            <input type="submit" class="btn btn-primary" value="Add to team">
                        </div>
                    </g:form>
                </div>
            </div>
            </th>
            </tr>

            <tr>
                <g:if test="${isTeamManager}"><th></th></g:if>
                <th data-sort="string-ins">Name</th>
                <th data-sort="string-ins">Email Address</th>
                <th data-sort="string-ins">Role</th>
            </tr>
            </thead>
            <tbody>
                <g:form id="modifyTeamRoles" name="modifyTeamRoles" action="modifyTeamRoles" controller="role">
                    <g:hiddenField id="roleId" name="roleId" value="${roleInstance?.id}" />
                    <g:hiddenField id="teamRole" name="teamRole" value="" />
                    <g:each in="${teamMembers}" var="member">
                            <tr>
                                <g:if test="${isTeamManager}"><td><g:checkBox id="checkboxes" name="checkboxes" value="${member.id}" checked="" /></td></g:if>
                                <td>${member.person.fullName}</td>
                                <td>${member.person.emailAddress}</td>
                                <g:if test="${member.teamRole.equals("Member")}">
                                    <td><span class="label">${member.teamRole}</span></td>
                                </g:if>
                                <g:elseif test="${member.teamRole.equals("Manager")}">
                                    <td><span class="label label-success">${member.teamRole}</span></td>
                                </g:elseif>
                                <g:else>
                                    <td><span class="label label-info">${member.teamRole}</span></td>
                                </g:else>
                            </tr>
                    </g:each>
                </g:form>
            </tbody>
        </table>
        <br/>
        </div>
    </div>
</div>
</body>
</html>
