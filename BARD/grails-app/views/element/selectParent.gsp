<html>
    <head>
        <r:require modules="core,bootstrap,dynatree,jqueryform,newTerm,contextItem"/>
        <meta name="layout" content="basic"/>
        <title>Propose New Term</title>
    </head>

    <body>
        <div class="row-fluid">
            <div id="selectParentForm">
                <g:render template="selectParentForm"/>
            </div>

            <div id="addParentTree">
                <g:render template="termTree"/>
            </div>

            <div id="addParentDictionaryTree">
                <g:render template="dictionaryTermTree"/>
            </div>
        </div>
    </body>
</html>
