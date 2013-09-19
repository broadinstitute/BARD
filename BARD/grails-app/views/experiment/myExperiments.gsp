<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,assayshow,assaysummary"/>
    <meta name="layout" content="basic"/>
    <title>My Experiments</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <div class="hero-unit-v1">
            <h4>My Experiments</h4>
        </div>
    </div>
</div>

<p><b>Total Experiments:</b> ${experiments.size()}</p>

<div id="showExperiments">
    <g:if test="${experiments}">
        <table class="table table-striped table-hover table-bordered">
            <thead>
            <tr>
                <th>Experiment ID</th><th>Name</th><th>Description</th> <th>Date Created</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${experiments}" var="experiment">
                <tr>
                    <td><g:link controller="experiment" id="${experiment.id}"
                                action="show">${experiment.id}</g:link></td>
                    <td style="line-height: 150%"><p>${experiment.experimentName}</p></td>
                    <td style="line-height: 150%"><p>${experiment.description}</p></td>
                    <td><g:formatDate date="${experiment.dateCreated}" format="MM/dd/yyyy"/></td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </g:if>
    <br/>
</div>
</body>
</html>