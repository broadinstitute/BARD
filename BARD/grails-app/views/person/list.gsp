<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap,twitterBootstrapAffix"/>
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
            <g:form action="save" class="form-inline">
                <input type="submit" value="Add user" class="btn btn-primary">
                <input type="text" placeholder="Username" name="userName" value="${params.userName}">
                <input type="text" placeholder="Full name" name="fullName" value="${params.fullName}">
                <input type="text" placeholder="Email address" name="emailAddress" value="${params.email}">
                Primary group: <g:select name="primaryGroup" from="${roles}" optionValue="displayName"  noSelection="${['':'']}"/>
            </g:form>

            <h3>Existing users</h3>
            <table>
                <tr><th>Full name</th><th>Username</th><th>Email</th></tr>
                <g:each in="${people}" var="person">
                    <tr>
                        <td><g:link action="edit" id="${person.id}">${person.fullName}</g:link></td>
                        <td><g:link action="edit" id="${person.id}">${person.userName}</g:link></td>
                        <td>${person.emailAddress}</td>
                    </tr>
                </g:each>
            </table>
        </div>
    </div>
</div>

</body>
</html>