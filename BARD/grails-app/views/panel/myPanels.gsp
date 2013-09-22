<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,tableSorter"/>
    <meta name="layout" content="basic"/>
    <title>My Panels</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <script type="text/javascript">
            $(document).ready(function () {
                $("#myPanels").tablesorter({
                    headers: {
                        0:  { sorter: "digit"  },
                        3: { sorter: "shortDate"  }
                    },
                    widgets: ['zebra']
                });
            });
        </script>

        <g:if test="${panels}">
            <table id="myPanels" class="tablesorter table table-striped table-hover table-bordered">
                <caption><b>My Panels Total:</b> ${panels.size()}</caption>

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
</div>
</body>
</html>