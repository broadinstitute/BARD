<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,grailspagination"/>
    <meta name="layout" content="basic"/>
    <title>People</title>
</head>

<body>

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
            <h3>Add new user</h3>
            <g:form action="save" controller="person" class="form-inline">
                <input type="submit" value="Add user" class="btn btn-primary">
                <input type="text" placeholder="Username" name="username" value="${personCommand.username}">
                <input type="text" placeholder="Full name" name="displayName" value="${personCommand.displayName}">
                <input type="email" placeholder="Email address" name="email" value="${personCommand.email}">
                Primary group:
                <g:select required="" id="primaryGroup" name="primaryGroup.id" from="${bard.db.people.Role.list()}"
                          optionKey="id"
                          optionValue="displayName" noSelection="${['': '']}"
                          value="${personCommand?.primaryGroup?.id}"/>
                Roles:
                <g:select id="roles" size="${bard.db.people.Role.list().size()}"
                          name="roles" from="${bard.db.people.Role.list()}"
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

                    <g:sortableColumn property="newObjectRole"
                                      title="Primary Group"/>

                    %{--<th>Full name</th><th>Username</th><th>Email</th><th>Primary Group</th>--}%
                    <th>Roles</th></tr>
                </thead>
                <tbody>
                <g:each in="${people}" var="person">
                    <tr>
                        <td><g:link action="edit" id="${person.id}">${person.fullName}</g:link></td>
                        <td><g:link action="edit" id="${person.id}">${person.userName}</g:link></td>
                        <td>${person.emailAddress}</td>
                        <td>${person?.newObjectRole?.displayName}</td>
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

</body>
</html>