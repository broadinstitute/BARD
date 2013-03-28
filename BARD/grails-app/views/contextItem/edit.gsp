<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,contextItem"/>
    <meta name="layout" content="basic"/>
    <title>Edit ContextItem</title>

</head>

<body>
<div class="row-fluid">
    <div class="span12">
        <h4>${reviewNewItem? 'Review': 'Edit'} this item for the ${instance?.context?.contextName} Context</h4>
    </div>
</div>
<g:render template="form" model="${[instance: instance, action:'Update', reviewNewItem:reviewNewItem]}" />

<div class="row-fluid">
    <div class="span6 offset2">
        <h3>Current context:</h3>
        <g:render template="show" model="${[context:instance.context, highlightedItemId:instance.contextItemId]}" />
    </div>
</div>

</body>
</html>