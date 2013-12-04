<%@ page import="bard.db.bard.db.people.Role" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'role.label', default: 'Role')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-role" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                           default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label"
                                                              args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-role" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <ol class="property-list role">

        <g:if test="${roleInstance?.authority}">
            <li class="fieldcontain">
                <span id="authority-label" class="property-label"><g:message code="role.authority.label"
                                                                             default="Authority"/></span>

                <span class="property-value" aria-labelledby="authority-label"><g:fieldValue bean="${roleInstance}"
                                                                                             field="authority"/></span>

            </li>
        </g:if>

        <g:if test="${roleInstance?.dateCreated}">
            <li class="fieldcontain">
                <span id="dateCreated-label" class="property-label"><g:message code="role.dateCreated.label"
                                                                               default="Date Created"/></span>

                <span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate
                        date="${roleInstance?.dateCreated}"/></span>

            </li>
        </g:if>

        <g:if test="${roleInstance?.lastUpdated}">
            <li class="fieldcontain">
                <span id="lastUpdated-label" class="property-label"><g:message code="role.lastUpdated.label"
                                                                               default="Last Updated"/></span>

                <span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate
                        date="${roleInstance?.lastUpdated}"/></span>

            </li>
        </g:if>

        <g:if test="${roleInstance?.modifiedBy}">
            <li class="fieldcontain">
                <span id="modifiedBy-label" class="property-label"><g:message code="role.modifiedBy.label"
                                                                              default="Modified By"/></span>

                <span class="property-value" aria-labelledby="modifiedBy-label"><g:fieldValue bean="${roleInstance}"
                                                                                              field="modifiedBy"/></span>

            </li>
        </g:if>

        <g:if test="${roleInstance?.displayName}">
            <li class="fieldcontain">
                <span id="displayName-label" class="property-label"><g:message code="role.displayName.label"
                                                                               default="Display Name"/></span>

                <span class="property-value" aria-labelledby="displayName-label"><g:fieldValue bean="${roleInstance}"
                                                                                               field="displayName"/></span>

            </li>
        </g:if>

    </ol>
    <g:form>
        <fieldset class="buttons">
            <g:hiddenField name="id" value="${roleInstance?.id}"/>
            <g:link class="edit" action="edit" id="${roleInstance?.id}"><g:message code="default.button.edit.label"
                                                                                   default="Edit"/></g:link>
            <g:actionSubmit class="delete" action="delete"
                            value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
