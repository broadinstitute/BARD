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
                            data-url="/BARD/role/editAuthority"
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
                            data-url="/BARD/role/editDisplayName"
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
    </body>
</html>
