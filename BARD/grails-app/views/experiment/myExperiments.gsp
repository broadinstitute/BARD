<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="myBard"/>
    <meta name="layout" content="basic"/>
    <title>My Experiments</title>

</head>

<body>

<div class="container-fluid">
    <div class="row-fluid">
        <g:if test="${experiments}">
            <div id="overlay" class="overlay">
                Please wait while we sort the column...
            </div>
            <table id="myExperiments" class="tablesorter table table-striped table-hover table-bordered">
                <caption><b>Total:</b> ${experiments.size()}</caption>
                <thead>
                <tr>
                    <th>Experiment ID</th><th>Name</th><th>Status</th><th>Belongs to ADID</th> <th>Date Created</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${experiments}" var="experiment">
                    <tr>
                        <td><g:link controller="experiment" id="${experiment.id}"
                                    action="show">${experiment.id}</g:link></td>
                        <td style="line-height: 150%"><p>${experiment.experimentName}</p></td>
                        <td style="line-height: 150%"><p>${experiment.experimentStatus.id}</p></td>
                        <td style="line-height: 150%"><p>
                            <g:link controller="assayDefinition" id="${experiment.assay.id}"
                                    action="show">${experiment.assay.id}</g:link>
                        </p></td>
                        <td><g:formatDate date="${experiment.dateCreated}" format="MM/dd/yyyy"/></td>
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