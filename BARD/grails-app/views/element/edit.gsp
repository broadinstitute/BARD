<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 4/22/13
  Time: 11:01 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Hello edit</title>
</head>
<body>
<h1>${maxPathLength}</h1>
    <g:form name="elementEditForm" url="[controller: 'element', action:'update']">
        <g:each in="${list}" var="elementAndPath">
            <g:textField name="textField" value="${elementAndPath.toString()}" size="${maxPathLength}" />
            <g:submitButton name="a buttons name" value="submit"/>
            <br/>
        </g:each>
    </g:form>
</body>
</html>