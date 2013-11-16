<%@ page import="bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="myBard"/>
    <meta name="layout" content="basic"/>
    <title>BARD Teams</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <p>Please email the BARD team at <g:render template="../layouts/templates/bardusers"/> if you would like to be added to a Team</p> <br/>

        <g:render template="/layouts/templates/tableSorterTip"/>
        <table class="table table-striped table-hover table-bordered">
            <caption>BARD Teams</caption>
            <thead>
            <tr>
                <th data-sort="string-ins">Team Name</th><th data-sort="int">Date Created</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${roles}" var="role">
                <g:if test="${role?.authority?.startsWith("ROLE_TEAM_")}">
                    <tr>
                        <td>${role.displayName}</td>
                        <td data-sort-value="${role.dateCreated?.time}"><g:formatDate date="${role.dateCreated}"
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