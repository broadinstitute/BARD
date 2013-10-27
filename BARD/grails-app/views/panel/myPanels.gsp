<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="myBard"/>
    <meta name="layout" content="basic"/>
    <title>My Panels</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <g:if test="${panels}">
            <div id="overlay">
                Please wait...
            </div>

            <table id="myPanels" class="tablesorter table table-striped table-hover table-bordered myBard">
                <caption><b>Total:</b> ${panels.size()}</caption>

                <thead>

                <tr>
                    <th>Panel ID</th><th>Name</th><th>Description</th> <th>Date Created</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${panels}" var="panel">
                    <tr>
                        <td><g:link controller="panel" id="${panel.id}"
                                    action="show">${panel.id}</g:link></td>
                        <td>
                            <g:link controller="panel" id="${panel.id}"
                                    action="show">${panel.name}</g:link>
                        </td>
                        <td style="line-height: 150%"><p>${panel.description}</p></td>
                        <td><g:formatDate date="${panel.dateCreated}" format="MM/dd/yyyy"/></td>
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