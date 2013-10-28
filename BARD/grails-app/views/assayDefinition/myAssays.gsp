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
            <g:render template="/layouts/templates/tableSorterTip"/>
            <table>
                <caption><b>Total:</b> ${assays.size()}</caption>
                <thead>
                <tr>
                    <th data-sort="int">ADID</th><th data-sort="string-ins">Name</th><th data-sort="string">Status</th> <th data-sort="int">Date Created</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${assays}" var="assay">
                    <tr>
                        <td><g:link controller="assayDefinition" id="${assay.id}"
                                    action="show">${assay.id}</g:link></td>
                        <td style="line-height: 150%"><p>${assay.assayName?.trim()}</p></td>
                        <td>${assay.assayStatus.id}</td>
                        <td data-sort-value="${assay.dateCreated.time}"><g:formatDate date="${assay.dateCreated}" format="MM/dd/yyyy"/></td>
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