<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,contextItem"/>
    <meta name="layout" content="basic"/>
    <title>Edit ContextItem</title>

</head>

<body>
<g:render template="form" model="${[instance: instance, action:'Update']}" />

<div class="row-fluid">
    <div class="span6">
        <g:render template="show" model="${[context:instance.context]}" />
    </div>
</div>
</body>
</html>