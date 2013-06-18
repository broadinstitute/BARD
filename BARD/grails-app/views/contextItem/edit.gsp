<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,contextItem,handlebars"/>
    <meta name="layout" content="basic"/>
    <title>Edit ContextItem</title>

</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <h2>${reviewNewItem ? 'Review' : 'Edit'} Item</h2>
    </div>
</div>
<br/>

<div class="row-fluid">
    <div class="span12">
        <g:render template="form" model="${[instance: instance, action: 'Update', reviewNewItem: reviewNewItem]}"/>
        <div class="row-fluid">
            <div class="span6 offset2">
                <h3>Current context:</h3>
                <g:render template="show"
                          model="${[context: instance.context, highlightedItemId: instance.contextItemId]}"/>
            </div>
        </div>
    </div>
</div>
</body>
</html>