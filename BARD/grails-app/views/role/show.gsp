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
        <div class="span3"></div>
        <div class="span9">
        <g:form class="form-inline" action="addUserToTeam" controller="role">
            <g:hiddenField class="" id="roleId" name="roleId" value="${roleInstance?.id}" />
            <g:textField name="email" value="" placeholder="Email address" required="required"/>
            <input type="submit" class="btn btn-primary" value="Add to team">
        </g:form>
        </div>
    </div>

    <div class="row-fluid">
        <g:render template="/layouts/templates/tableSorterTip"/>
        <table class="table table-striped table-hover table-bordered">
            <caption>Team Members</caption>
            <thead>
            <tr>
                <th data-sort="string-ins">Name</th>
                <th data-sort="string-ins">Email Address</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${teamMembers}" var="member">
                    <tr>
                        <td>${member.fullName}</td>
                        <td>${member.emailAddress}</td>
                    </tr>
            </g:each>
            </tbody>
        </table>
        <br/>
    </div>
    </div>
    </body>
</html>
