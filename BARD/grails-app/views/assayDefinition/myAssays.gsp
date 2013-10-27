<%@ page import="bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="myBard"/>
    <meta name="layout" content="basic"/>
    <title>My Assay Definitions</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <g:if test="${assays}">
            <div id="overlay" class="overlay">
                Please wait while we sort the column...
            </div>
            <table id="myAssays" class="tablesorter table table-striped table-hover table-bordered myBard">
                <caption><b>Total:</b> ${assays.size()}</caption>
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
            <g:render template="/layouts/templates/tableSorterPaging"/>
        </g:if>
        <br/>
    </div>
</div>
</body>
</html>