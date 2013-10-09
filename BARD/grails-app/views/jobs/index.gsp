<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,tableSorter"/>
    <meta name="layout" content="basic"/>
    <title>My Experiments</title>
</head>

<body>

<div class="container-fluid">
    <div class="row-fluid">
        <table id="myJobs" class="table table-striped table-hover table-bordered">
            <caption><b>Total:</b> ${jobs.size()}</caption>
            <thead>
            <tr>
                <th>Job ID</th><th>Status</th>
            </tr>
            </thead>

            <tbody>
            <g:each in="${jobs}" var="job">
                <tr>
                    <td><a href="<%= job.link %>">${job.key}</a></td>
                    <td>${job.status}</td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>