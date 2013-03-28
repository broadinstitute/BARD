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
</body>
</html>