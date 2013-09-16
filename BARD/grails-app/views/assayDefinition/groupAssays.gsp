<%@ page import="bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,assayshow,twitterBootstrapAffix,xeditable,richtexteditorForEdit,assaysummary,canEditWidget"/>
    <meta name="layout" content="basic"/>
    <title>My Assay Definitions</title>
</head>
<body>
    <div class="row-fluid">
        <div class="span12">
            <div class="hero-unit-v1">
                <h4>My Assay Definitions</h4>
            </div>
        </div>
    </div>
    <p><b>Total Assay Definitions:</b> ${assays.size()}</p>
    <div id="showAssays">
        <g:if test="${assays}">
            <table class="table table-striped table-hover table-bordered">
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
                        <td>${assay.assayStatus}</td>
                        <td><g:formatDate date="${assay.dateCreated}" format="MM/dd/yyyy"/></td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </g:if>
        <br/>
    </div>
</body>
</html>