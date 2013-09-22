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
        <script type="text/javascript">
            $(document).ready(function () {
                $("#myExperiments").tablesorter({
                    headers:
                    {
                        0:  { sorter: "digit"  },
                        3: { sorter: "shortDate"  }
                    },
                    widgets: ['zebra']
                });
            });
        </script>
        <g:if test="${experiments}">
            <table id="myExperiments" class="tablesorter table table-striped table-hover table-bordered">
                <caption><b>My Experiments; Total:</b> ${experiments.size()}</caption>
                <thead>
                <tr>
                    <th>Experiment ID</th><th>Name</th><th>Status</th> <th>Date Created</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${experiments}" var="experiment">
                    <tr>
                        <td><g:link controller="experiment" id="${experiment.id}"
                                    action="show">${experiment.id}</g:link></td>
                        <td style="line-height: 150%"><p>${experiment.experimentName}</p></td>
                        <td style="line-height: 150%"><p>${experiment.experimentStatus.id}</p></td>
                        <td><g:formatDate date="${experiment.dateCreated}" format="MM/dd/yyyy"/></td>
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