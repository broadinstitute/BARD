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
            <g:render template="/layouts/templates/tableSorterTip"/>
            <table>
                <caption><b>Total:</b> ${projects.size()}</caption>
                <thead>
                <tr>
                    <th data-sort="int">PID</th><th data-sort="string-ins">Name</th><th
                        data-sort="string">Status</th> <th data-sort="int">Date Created</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${projects}" var="project">
                    <tr>
                        <td><g:link controller="project" id="${project.id}"
                                    action="show">${project.id}</g:link></td>
                        <td style="line-height: 150%"><p>${project.name?.trim()}</p></td>
                        <td>${project.projectStatus.id}</td>
                        <td data-sort-value="${project.dateCreated.time}"><g:formatDate date="${project.dateCreated}"
                                                                                        format="MM/dd/yyyy"/></td>
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