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
                    ${error}
                </li>
            </g:each>
        </ul>
    </g:if>
    <g:else>
        <h4>Success</h4>

        <p>Uploaded ${summary.resultsCreated} values</p>
    </g:else>
</div>

</body>
</html>