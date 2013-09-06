<%@ page import="bard.db.enums.ContextType; bard.db.model.AbstractContextOwner; org.springframework.security.acls.domain.BasePermission; bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,assayshow,twitterBootstrapAffix,xeditable,richtexteditorForEdit,assaysummary,canEditWidget"/>
    <meta name="layout" content="basic"/>
    <title>My Projects</title>
</head>
<body>
<div class="row-fluid">
    <div class="span12">
        <div class="hero-unit-v1">
            <h4>My Projects</h4>
        </div>
    </div>
</div>
<p><b>Total projects:</b> ${projects.size()}</p>
<div id="showAssays">
    <g:if test="${projects}">
        <table class="table table-striped table-hover table-bordered">
            <thead>
            <tr>
                <th>PID</th><th>Project Name</th><th>Status</th> <th>Date Created</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${projects}" var="project">
                <tr>
                    <td><g:link controller="project" id="${project.id}"
                                action="show">${project.id}</g:link></td>
                    <td style="line-height: 150%"><p>${project.name}</p></td>
                    <td>${project.projectStatus}</td>
                    <td><g:formatDate date="${project.dateCreated}" format="MM/dd/yyyy"/></td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </g:if>
    <br/>
</div>
</body>
</html>