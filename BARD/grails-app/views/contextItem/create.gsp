<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,contextItem,handlebars"/>
    <meta name="layout" content="basic"/>
    <title>Create Assay Document</title>
</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <h2>Add a New Item</h2>
    </div>
</div>

<div class="row-fluid">
    <div class="row-fluid">
        <div class="span10 offset1 alert alert-info">
            <p>Start typing to select an attribute from the BARD dictionary.  Based on the selection, additional values will be requested.</p>
        </div>
    </div>

    <g:render template="form" model="${[instance: instance, action: 'Save']}"/>

    <div class="row-fluid">
        <div class="span10 offset1">
            <h3>Current context:</h3>
                <g:render template="edit" model="${[context: instance.context]}"/>
        </div>
    </div>
</div>



</body>
</html>