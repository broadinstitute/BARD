<%--
  Created by IntelliJ IDEA.
  User: pmontgom
  Date: 8/30/13
  Time: 10:34 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <r:require modules="core,bootstrap"/>
    <meta name="layout" content="basic"/>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-plus.css')}" type="text/css">
    <title>Sandbox</title>
</head>
<body>

<h1>${scriptName}</h1>
<p>${script.description}</p>

<g:form action="run" class="form-horizontal">
    <input type="hidden" name="scriptName" value="${scriptName}">

<g:each in="${script.parameterNames}" var="parameterName" status="i">
    <div class="control-group">
        <label class="control-label" for="values[${i}]">
            ${parameterName}
        </label>
    <div class="controls">

        <textarea class="input-xxlarge" id="values[${i}]" name="values[${i}]" row="1"></textarea>
        <input type="hidden" name="keys[${i}]" value="${parameterName}">
    </div>
    </div>
</g:each>

<div class="control-group">
    <div class="controls">
        <g:submitButton class="btn btn-primary" name="Run"></g:submitButton>
    </div>
</div>
</g:form>

</body>
</html>