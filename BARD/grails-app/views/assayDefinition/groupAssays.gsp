<%@ page import="bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,tableSorter"/>
    <meta name="layout" content="basic"/>
    <title>My Assay Definitions</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <script type="text/javascript">
            $(document).ready(function () {
                $('#myAssays').tablesorter({
                    headers: {
                        0: { sorter: "digit"  },
                        3: { sorter: "shortDate"  }
                    },
                    widgets: ['zebra']
                });
            });
        </script>
        <g:if test="${assays}">
            <table id="myAssays" class="tablesorter table table-striped table-hover table-bordered">
                <caption><b>My Assay Definitions; Total:</b> ${assays.size()}</caption>
                <thead>
                <tr>
                    <th>ADID</th><th>Name</th><th>Status</th> <th>Date Created</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${assays}" var="assay">
                    <tr>
                        <td><g:link controller="assayDefinition" id="${assay.id}"
                                    action="show">${assay.id}</g:link></td>
                        <td style="line-height: 150%"><p>${assay.assayName}</p></td>
                        <td>${assay.assayStatus.id}</td>
                        <td><g:formatDate date="${assay.dateCreated}" format="MM/dd/yyyy"/></td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </g:if>
        <br/>
    </div>
</div>
</body>
</html>