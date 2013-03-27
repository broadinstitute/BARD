<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,contextItem"/>
    <meta name="layout" content="basic"/>
    <title>Create Assay Document</title>

</head>

<body>
%{--<g:render template="message"/>--}%
%{--<g:render template="errors" model="['errors': instance?.errors?.allErrors]"/>--}%
<div class="row-fluid">
    <div class="span12">
        <g:form class="form-horizontal" action="save">
            <g:hiddenField name="contextId" value="${instance?.contextId}"/>
            <g:hiddenField name="contextClass" value="${instance?.contextClass}"/>

            <g:render template="editProperties" />

            <div class="control-group">
                <div class="controls">
                    <input type="submit" class="btn btn-primary" value="Create">
                </div>
            </div>

        </g:form>
    </div>
</div>
</body>
</html>