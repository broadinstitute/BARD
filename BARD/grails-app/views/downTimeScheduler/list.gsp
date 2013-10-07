<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,tableSorter"/>
    <meta name="layout" content="main"/>
    <title>History of Scheduled Down Time</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <script type="text/javascript">
            $(document).ready(function () {
                $("#downTime").tablesorter({
                    headers: {
                        0: { sorter: "digit"  },
                        1: { sorter: "shortDate" },
                        4: { sorter: "shortDate" }
                    },
                    widgets: ['zebra']
                });
            });
        </script>

        <g:if test="${downTimeSchedulerList}">
            <table id="downTime" class="tablesorter table table-striped table-hover table-bordered">
                <thead>

                <tr>
                    <th>ID</th><th>Down Time</th><th>Display Value</th> <th>Created By</th><th>Date Created</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${downTimeSchedulerList}" var="downTimeScheduler">
                    <tr>
                        <td><g:link controller="downTimeScheduler" action="show" id="${downTimeScheduler.id}">
                            ${downTimeScheduler.id}
                        </g:link>
                        </td>
                        <td>${downTimeScheduler.downTimeAsString}</td>
                        <td>${downTimeScheduler.displayValue}</td>
                        <td style="line-height: 150%"><p>${downTimeScheduler.createdBy}</p></td>
                        <td><g:formatDate date="${downTimeScheduler.dateCreated}" format="MM/dd/yyyy HH:mm:ss"/></td>
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