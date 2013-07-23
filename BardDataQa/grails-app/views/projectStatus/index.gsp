<%--
  Created by IntelliJ IDEA.
  User: dlahr
  Date: 7/22/13
  Time: 4:19 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>QA Status of Projects in CAP</title>
</head>
<body>
<h1>QA Status of Projects in CAP</h1>
<table border="1" cellpadding="10" cellspacing="1">
    <tr>
        <th>Project ID</th>
        <th>QA Status of Project</th>
        <th>New QA Status</th>
        <th>Project Name</th>
        <th>Laboratory Name</th>
    </tr>

    <g:each in="${projectStatusList}" var="projectStatus">
        <tr>
            <td>${projectStatus.id}</td>
            <td>${projectStatus.qaStatus.name}</td>
            <td>
                <g:form name="changeStatusForm${projectStatus.id}" url="[controller: 'projectStatus', action: 'update']">
                    <g:hiddenField name="projectId" value="${projectStatus.id}"/>
                    <g:select name="qaStatusId" from="${qaStatusList}" optionKey="id" optionValue="name"
                              value="${projectStatus.qaStatus.id}" onchange="document.forms['changeStatusForm${projectStatus.id}'].submit()"/>
                </g:form>
            </td>
            <td>${projectStatus?.projectName}</td>
            <td>${projectStatus?.laboratoryName}</td>
        </tr>
    </g:each>
</table>
<br/>
<h2>Track a new project</h2>
<g:form name="newProjectForm" url="[controller: 'projectStatus', action: 'create']">
    Project ID:<g:textField name="projectId" size="5"/> <br/>
    QA status:<g:select name="qaStatusId" from="${qaStatusList}" optionKey="id" optionValue="name"/><br/>
    <g:submitButton name="addProjectSubmitButton" value="Add Project"/>
</g:form>


</body>
</html>