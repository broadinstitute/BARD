<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,assayshow,assaysummary"/>
    <meta name="layout" content="basic"/>
    <title>My Panels</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div class="hero-unit-v1">
            <h4>My Panels</h4>
        </div>
    </div>
</div>

<p><b>Total Panels:</b> ${panels.size()}</p>

<div id="showPanels">
    <g:if test="${panels}">
        <table class="table table-striped table-hover table-bordered">
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
                    <td style="line-height: 150%"><p>${panel.name}</p></td>
                    <td style="line-height: 150%"><p>${panel.description}</p></td>
                    <td><g:formatDate date="${panel.dateCreated}" format="MM/dd/yyyy"/></td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </g:if>
    <br/>
</div>
</body>
</html>