<!DOCTYPE html>
<html>
<head>
    <title>Monitoring the BARD Data QA Load Process</title>
    <h1>Monitoring the BARD Data QA Load Process</h1>
</head>
<body>

    <h2>Available pages / actions:</h2>
    <table cellpadding="10" cellspacing="1">
        <tr>
            <td>Issue tracking</td>
            <td><a href="https://jira.broadinstitute.org/secure/IssueNavigator.jspa?sorter/field=updated&sorter/order=DESC">JIRA list of issues</a></td>
        </tr>
        <tr>
            <td>Datasets</td>
            <td><g:link controller="dataset" action="index">List datasets and calculate marginal product of work for them</g:link></td>
        </tr>
        <tr>
            <td>Result Map Issues</td>
            <td><g:link controller="resultMapIssue" action="index">List AID's with current known issues in the result_map table</g:link></td>
        </tr>
        <tr>
            <td>Missing AID's</td>
            <td><g:link controller="checkForMissingAids" action="index">Check for AID's that are in a dataset but missing from external_reference table (${missingAidCount})</g:link></td>
        </tr>
        <tr>
            <td>Projects, Assays and their Centers</td>
            <td><g:link controller="projectAssaysCenters" action="index">List assays associated with each project and the center for each</g:link></td>
        </tr>
    </table>
</body>
</html>