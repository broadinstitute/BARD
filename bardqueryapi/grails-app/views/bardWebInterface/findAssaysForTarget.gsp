<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="assays.target.label" args="[]" default="Find Assay's For Target"/></title>
</head>

<body>
<div class="nav" role="navigation">
    <ul>
        <li><g:link controller="bardWebInterface" action="index"><g:message
                code="default.home.label"/></g:link>
        </li>
        <li><g:link controller="bardWebInterface" action="targets">Find Assays For Target</g:link>
        </li>
    </ul>
</div>

<g:form name="targetsForm" controller="bardWebInterface" action="findAssaysForTarget">
    <div class="listHeader">
        <div class="listTitle">
            <h1>Find Assays For Target</h1><br>
        </div>

        <div class="clear"></div>
    </div>

    <div class="content">
        <label for="target">
            <g:message code="target.id.label" default="Target"/>
        </label>
        <g:textField name="target" value=""></g:textField>
        <g:submitButton name="findAssaysForTarget"
                        value="${message(code: 'default.button.search.label', default: 'Find!')}"/>
    </div>
</g:form>
</body>
</html>