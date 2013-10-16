<html>
<head>
    <r:require modules="core,bootstrap,dynatree,jqueryform,newTerm,contextItem"/>
    <meta name="layout" content="basic"/>
    <title>Propose New Term</title>
</head>

<body>
<div class="row-fluid">
    <div id="selectParentForm" class="span6">
        <g:render template="selectParentForm"/>
    </div>

    <div class="span6">
        <div id="addParentTree">
            <g:render template="termTree"/>
        </div>

        <div id="addParentDictionaryTree">
            <g:render template="dictionaryTermTree"/>
        </div>
    </div>
</div>
</body>
</html>
