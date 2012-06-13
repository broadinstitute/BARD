<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 6/8/12
  Time: 3:46 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
</head>

<body>
<g:form name="aidForm" controller="bardWebInterface" action="findCompoundsForAssay">
    <label for="assay">
        <g:message code="assay.id.label" default="AID"/>
    </label>
    <g:textField name="assay" value=""></g:textField>
    <g:submitButton name="findCompoundsForAssay"
                    value="${message(code: 'default.button.search.label', default: 'Search')}"/>
</g:form>
</body>
</html>