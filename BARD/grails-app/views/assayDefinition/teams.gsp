<%@ page import="bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,tableSorter"/>
    <meta name="layout" content="basic"/>
    <title>BARD Teams</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <script type="text/javascript">
            $(document).ready(function () {
                $('#roles').tablesorter({
                    widgets: ['zebra']
                });
            });
        </script>
            <p>Please email the BARD team at bard-users@broadinstitute.org if you would like to be added to a Team</p> <br/>
            <table id="roles" class="tablesorter table table-striped table-hover table-bordered">
                <caption>BARD Teams</caption>
                <thead>
                <tr>
                    <th>Team Name</th><th>Date Created</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${roles}" var="role">
                    <g:if test="${role?.authority?.startsWith("ROLE_TEAM_")}">
                        <tr>
                            <td>${role.displayName}</td>
                            <td><g:formatDate date="${role.dateCreated}" format="MM/dd/yyyy"/></td>
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