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
<sec:ifAnyGranted roles="ROLE_CURATOR">
    <h1>Edit BARD Ontology</h1>
    <table>
        <g:each in="${list}" var="elementAndPath">
            <tr>
                <g:form name="elementEditForm" url="[controller: 'element', action: 'update']">
                    <td>
                        <g:textField name="newPathString" value="${elementAndPath}" size="${maxPathLength}"/>
                    </td>

                    <g:hiddenField name="elementId" value="${elementAndPath.element.id}"/>
                    <g:each in="${elementAndPath.path}" status="index" var="elementHierarchy">
                        <g:hiddenField name="elementHierarchyIdList[${index}]" value="${elementHierarchy.id}"/>
                    </g:each>
                </g:form>
            </tr>
        </g:each>
    </table>
</sec:ifAnyGranted>
</body>
</html>