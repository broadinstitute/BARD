<!DOCTYPE html>
<html>
<head>
    <r:require
            modules="core,bootstrap,assayshow,twitterBootstrapAffix,xeditable,richtexteditorForEdit,assaysummary,canEditWidget"/>
    <meta name="layout" content="basic"/>
    <title>Offline Validation</title>
</head>

<body>

<g:if test="${message}">
    <div class="row-fluid">
        <div class="span12">
            <p class="info">${message}</p>
        </div>
    </div>
</g:if>

<div class="row-fluid">
    <div class="span12">
        <p>Pick 1 or more classes and click validate.  The results will be asyncronously run and recorded to the database</p>
        <g:form action="validate">
            <div class="row-fluid">
                <g:select name="fullClassNames" from="${simpleNameFullNameMap.entrySet()}" optionKey="key" optionValue="value" multiple="true" class="span6" size="20"/>
            </div>

            <div class="row-fluid">
                <g:submitButton name="validate"/>
            </div>
        </g:form>
    </div>
</div>

</body>
</html>