<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="myBard"/>
    <meta name="layout" content="basic"/>
    <title>My Projects</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <g:if test="${projects}">
            <div id="overlay" class="overlay">
                Please wait while we sort the column...
            </div>
            <table id="myProjects" class="tablesorter table table-striped table-hover table-bordered myBard">
                <caption><b>Total:</b> ${projects.size()}</caption>
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
                        <td>${project.projectStatus.id}</td>
                        <td><g:formatDate date="${project.dateCreated}" format="MM/dd/yyyy"/></td>
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