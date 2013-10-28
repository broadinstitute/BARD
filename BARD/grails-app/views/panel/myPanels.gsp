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
            <g:render template="/layouts/templates/tableSorterTip"/>
            <table>
                <caption><b>Total:</b> ${panels.size()}</caption>
                <thead>
                <tr>
                    <th data-sort="int">Panel ID</th><th data-sort="string-ins">Name</th><th
                        data-sort="string-ins">Description</th> <th data-sort="int">Date Created</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${panels}" var="panel">
                    <tr>
                        <td><g:link controller="panel" id="${panel.id}"
                                    action="show">${panel.id}</g:link></td>
                        <td>
                            <g:link controller="panel" id="${panel.id}"
                                    action="show">${panel.name?.trim()}</g:link>
                        </td>
                        <td style="line-height: 150%"><p>${panel.description?.trim()}</p></td>
                        <td data-sort-value="${panel.dateCreated?.time}"><g:formatDate date="${panel.dateCreated}" format="MM/dd/yyyy"/></td>
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