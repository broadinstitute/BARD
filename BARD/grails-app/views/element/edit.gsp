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
  <title>Edit BARD Ontology</title>
</head>
<body>
<g:each in="${list}" var="elementAndPath">
    <g:form name="elementEditForm" url="[controller: 'element', action:'update']">
        <g:textField name="newPathString" value="${elementAndPath.toString()}" size="${maxPathLength}" />
        <g:submitButton name="submitButtonName" value="Do it!"/>
        <br/>

        <g:hiddenField name="elementId" value="${elementAndPath.element.id}"/>
        <g:each in="${elementAndPath.path}" status="index" var="elementHierarchy">
            <g:hiddenField name="elementHierarchyIdList[${index}]" value="${elementHierarchy.id}"/>
        </g:each>
    </g:form>
</g:each>
</body>
</html>