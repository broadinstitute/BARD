<%@ page import="bard.db.project.*" %>
<!DOCTYPE html>
<html>
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <title>Results uploaded</title>
</head>
<body>

<div class="row-fluid">
    <g:if test="${summary.hasErrors()}">
        <h4>Errors</h4>

        <ul>
            <g:each in="${summary.errors}" var="error">
                <li>
                    ${error.encodeAsHTML()}
                </li>
            </g:each>
        </ul>
    </g:if>
    <g:else>
        <h4>Success</h4>

        <p>${summary.linesParsed} lines successfully parsed.</p>
        <p>Uploaded ${summary.resultsCreated} values for ${summary.substanceCount} substances</p>
        <p>Created ${summary.experimentAnnotationsCreated} Experiment annotations</p>
        <p>
            Created the following number of results:
        <ul>
            <g:each in="${summary.resultsPerLabel.entrySet()}" var="entry">
                <li>${entry.key}: ${entry.value}</li>
            </g:each>
        </ul>
        </p>

        <p>Top 10 lines of submitted file</p>
        <table border="1">
            <tbody>
                <g:each in="${summary.topLines}" var="row">
                <tr>
                    <g:each in="${row}" var="cell">
                        <td>${cell}</td>
                    </g:each>
                </tr>
                </g:each>
            </tbody>
        </table>

    </g:else>

    <g:link action="show" controller="experiment" id="${experiment.id}">Return to Experiment's Details</g:link>
</div>

</body>
</html>