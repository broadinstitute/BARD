<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="myBard"/>
    <meta name="layout" content="main"/>
    <title>History of Scheduled Down Time</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <b>History of Scheduled Down Time</b>
        <g:if test="${downTimeSchedulerList}">

            <table id="downTime" class="table table-striped table-hover table-bordered">
                <thead>

                <tr>
                    <th data-sort="int">ID</th><th data-sort="int">Down Time</th><th
                        data-sort="string">Display Value</th> <th data-sort="string">Created By</th><th
                        data-sort="int">Date Created</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${downTimeSchedulerList}" var="downTimeScheduler">
                    <tr>
                        <td><g:link controller="downTimeScheduler" action="show" id="${downTimeScheduler.id}">
                            ${downTimeScheduler.id}
                        </g:link>
                        </td>
                        <td data-sort-value="${downTimeScheduler.downTimeAsLong}">${downTimeScheduler.downTimeAsString}</td>
                        <td>${downTimeScheduler.displayValue}</td>
                        <td style="line-height: 150%"><p>${downTimeScheduler.createdBy}</p></td>
                        <td data-sort-value="${downTimeScheduler.dateCreated?.time}"><g:formatDate
                                date="${downTimeScheduler.dateCreated}" format="MM/dd/yyyy HH:mm:ss"/></td>
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