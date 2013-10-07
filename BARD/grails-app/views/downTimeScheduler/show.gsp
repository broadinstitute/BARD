<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,tableSorter"/>
    <meta name="layout" content="main"/>
    <title>View Down Time</title>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <g:if test="${downTimeScheduler}">
            <dl class="dl-horizontal">

                <dt>ID:</dt>
                <dd>
                    ${downTimeScheduler.id}
                </dd>
                <dt>Down Time - Date and Time:</dt>
                <dd>
                    ${downTimeScheduler.downTimeAsString}
                </dd>

                <dt>Display Value:</dt>
                <dd>
                    ${downTimeScheduler.displayValue}
                </dd>
                <dt>Created By:</dt>
                <dd>
                    ${downTimeScheduler.createdBy}
                </dd>

                <dt>Date Created:</dt>
                <dd>
                    <g:formatDate date="${downTimeScheduler.dateCreated}" format="MM/dd/yyyy HH:mm:ss"/>
                </dd>
            </dl>
        </g:if>
        <g:else>
            No Information Found
        </g:else>
        <br/>
    </div>
</div>
</body>
</html>