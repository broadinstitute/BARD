<html>
    <head>
        <r:require modules="core,bootstrap,dynatree,jqueryform,newTerm"/>
        <meta name="layout" content="basic"/>
        <title>Propose new term</title>
    </head>

    <body>
        <div class="row-fluid">
            <div id="addTermForm">
                <g:render template="addForm"/>
            </div>

            <div id="addTermTree">
                <g:render template="termTree"/>
            </div>
        </div>
    </body>
</html>
