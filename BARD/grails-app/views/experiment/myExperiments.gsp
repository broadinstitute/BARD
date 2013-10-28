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
            <g:render template="/layouts/templates/tableSorterTip"/>


            <div class="span1">

            </div>

            <div class="span10">
                <g:render template="/layouts/templates/tableSorterTip"/>
                <table>
                    <caption><b>Total:</b> ${experiments.size()}</caption>
                    <thead>
                    <tr>
                        <th data-sort="int">Experiment ID</th><th data-sort="string-ins">Name</th><th data-sort="string">Status</th>
                        <th data-sort="int">Belongs to ADID</th>
                        <th data-sort="int">Date Created</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${experiments}" var="experiment">
                        <tr>
                            <td><g:link controller="experiment" id="${experiment.id}"
                                        action="show">${experiment.id}</g:link></td>
                            <td style="line-height: 150%"><p>${experiment.experimentName?.trim()}</p></td>
                            <td style="line-height: 150%"><p>${experiment.experimentStatus.id}</p></td>
                            <td style="line-height: 150%"><p>
                                <g:link controller="assayDefinition" id="${experiment.assay.id}"
                                        action="show">${experiment.assay.id}</g:link>
                            </p></td>
                            <td data-sort-value="${experiment.dateCreated?.time}"><g:formatDate date="${experiment.dateCreated}" format="MM/dd/yyyy"/></td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </g:if>
        <br/>
    </div>
</div>
</body>
</html>